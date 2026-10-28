/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.monsters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * One set of "pain" messages, the Java copy of one {@code type:} block of {@code pain.txt} and of
 * {@code struct monster_pain} in {@code monster.h}. Each {@code monster_base.txt} record names one
 * set through its {@code pain:} line, and the monster message code in {@code mon-msg.c} reads it
 * when a monster is hurt without dying.
 *
 * <p>The struct has three members. {@code messages[7]} is {@link #messages}, {@code pain_idx} is
 * {@link #painIndex}, and {@code next} has no counterpart. C only uses {@code next} while
 * {@code pain.txt} is being read: {@code parse_pain_type} chains each new record on at the head,
 * and {@code finish_parse_pain} in {@code mon-init.c} then copies the records into the
 * direct-access array {@code pain_messages}, indexed by {@code pain_idx}, and links them again.
 * Here {@code MonsterRegistry} holds the records in a list and finds one by comparing
 * {@link #getPainIndex()}.
 *
 * <p>Accepted deviation (decided 261009, "for the time being"): C's {@code pain_messages} has a
 * blank record in slot 0 and {@code z_info->mp_max} is one more than the record count. This class
 * is only ever built for a real {@code type:} block, so the blank record does not exist; see
 * {@code MonsterRegistry} for the counter and the lookup.
 *
 * <p>A set always holds seven slots. C reads {@code message:} lines into {@code messages[]} in order
 * and refuses only an eighth, so a {@code type:} block with fewer than seven lines is accepted and
 * its unfilled slots stay {@code NULL} (decided 261028). The constructor copies that: a short list
 * is padded with {@code null} up to seven, and {@link #getMessage} gives {@code null} for a slot
 * that was never filled.
 *
 * <p>Class MonsterPain coded before 261010, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class MonsterPain {
    /**
     * Logger used to report a pain definition that has more than seven messages, the one case the
     * constructor refuses.
     *
     * <p>Field logger coded before 261010, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The serial number from the {@code type:} line, C's {@code pain_idx}. It is the number a
     * {@code pain:} line in {@code monster_base.txt} uses to pick this set, and
     * {@code MonsterRegistry.lookupMonsterPain} matches on it. It is stored as given: C reads it
     * with {@code parser_getuint} and neither C nor this class checks that it is unique or in order.
     *
     * <p>Field painIndex coded before 261010, commented in full on 261028.
     */
    private final int painIndex;
    /**
     * The seven messages, C's {@code messages[7]}, in the order the {@code message:} lines appear.
     * Element 0 is the mildest reaction, shown while the monster has more than 95% of the health it
     * had before the blow, and element 6 is the worst, shown at 10% or less. {@code mon-msg.c},
     * function {@code get_message_text}, maps {@code MON_MSG_95} to element 0 and {@code MON_MSG_0}
     * to element 6, and {@code get_pain_msg_code} picks the code from the health left after the
     * blow as a percentage of the health before it. The text is kept as written in the file,
     * including the {@code [singular|plural]} markers, which C resolves when the message is built.
     * The list always has seven elements: a slot the {@code type:} block did not fill is
     * {@code null}, as C leaves it {@code NULL}. It is a private {@link ArrayList} copy of the list
     * the constructor was given; it is not unmodifiable, but nothing outside the class can reach it.
     *
     * <p>Field messages coded before 261010, commented in full on 261028.
     */
    private final List<String> messages;

    /**
     * Builds one pain set from a {@code type:} number and its seven message lines.
     *
     * <p>The list is copied, so later changes to the caller's list do not reach this record. A
     * list of more than seven is logged and refused with an {@link IllegalStateException}, as C's
     * {@code parse_pain_message} refuses an eighth message with {@code PARSE_ERROR_TOO_MANY_ENTRIES}.
     * A list of fewer than seven, including an empty one, is accepted as C accepts it: the copy is
     * padded with {@code null} up to seven, the slots C leaves {@code NULL}, which
     * {@code get_message_text} would then read (decided 261028). The header of {@code pain.txt}
     * says every entry must have seven, so this only matters for a file that is already wrong.
     * A {@code null} element inside the list is kept as it is, not refused.
     *
     * <p>Constructor MonsterPain coded before 261010, commented in full on 261010, short lists
     * accepted and padded on 261028.
     *
     * @param painIndex the serial number from the {@code type:} line, kept as {@code pain_idx}
     * @param messages  the {@code message:} strings in file order, mildest first, seven or fewer
     * @throws IllegalStateException if {@code messages} holds more than seven entries
     */
    public MonsterPain(int painIndex, @NotNull List<String> messages) {
        // Check to ensure that no more than 7 messages have come in
        if (messages.size() > 7) {
            String message = "Illegal number of messages found on Monster Pain type " + painIndex;
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        this.messages = new ArrayList<>(messages);
        int currentLength = messages.size();
        for (int index = currentLength; index < 7; index++) {
            this.messages.add(null);
        }

        this.painIndex = painIndex;
    }

    /**
     * Gives one of the seven messages, the Java form of reading {@code messages[level]} on a
     * {@code struct monster_pain}. Level 0 is the mildest reaction (more than 95% of the monster's
     * health left) and level 6 the worst (10% or less), which is the order
     * {@code get_message_text} in {@code mon-msg.c} uses. C does no range check on the index; here
     * a level outside 0 to 6 is refused. A slot the {@code type:} block did not fill gives
     * {@code null}, where C would hand {@code get_message_text} a {@code NULL} pointer.
     *
     * <p>Function getMessage coded before 261010, commented in full on 261028.
     *
     * @param level the position of the message, 0 (mildest) to 6 (worst)
     * @return the message at that position, with its {@code [singular|plural]} markers unresolved,
     * or {@code null} if that slot was never filled
     * @throws IllegalArgumentException if {@code level} is below 0 or above 6
     */
    @Nullable
    public String getMessage(int level) {
        if (level < 0 || level > 6)
            throw new IllegalArgumentException("level must be between 0 and 6");

        return messages.get(level);
    }

    /**
     * Builds a debug string of the form {@code MonsterPain{painIndex=1, messages='a', 'b', }},
     * with each message quoted and a trailing separator after the last. An unfilled slot prints as
     * {@code 'null'}, so the string always lists seven messages. It has no C counterpart and nothing
     * parses it.
     *
     * <p>Function toString coded before 261010, commented in full on 261028.
     *
     * @return a debug string listing this pain record's index and its messages
     */
    @Override
    @Contract(pure = true)
    public String toString() {
        StringBuilder result = new StringBuilder("MonsterPain{" +
                "painIndex=" + painIndex +
                ", messages=");

        for (String message : messages) {
            result.append("'").append(message).append("', ");
        }

        result.append("}");

        return result.toString();
    }

    /**
     * Gives the serial number of this set, C's {@code pain_idx}. {@code MonsterRegistry} matches a
     * {@code pain:} line from {@code monster_base.txt} against it, where C uses the same number as a
     * direct index into {@code pain_messages}.
     *
     * <p>Function getPainIndex coded before 261010, commented in full on 261028.
     *
     * @return the {@code type:} number this set was built with
     */
    public int getPainIndex() {
        return painIndex;
    }
}
