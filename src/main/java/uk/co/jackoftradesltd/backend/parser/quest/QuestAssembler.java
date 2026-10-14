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

package uk.co.jackoftradesltd.backend.parser.quest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link QuestParseRecord}s from {@code quest.txt} into domain {@link Quest}s.
 *
 * <p>This is the "assemble" half of the reader pipeline: it does the interpretation the grammar
 * deliberately skipped - parsing the integer fields and resolving each {@code race:} name to a real
 * {@link MonsterRace} against the loaded registry. It mirrors the behaviour of C's
 * {@code parse_quest_*} functions ({@code player-quest.c}), which likewise fill an in-memory quest
 * from the same four directives.
 *
 * <p><b>C counterpart:</b> {@code player-quest.c} reads {@code quest.txt} with one hook per directive
 * ({@code parse_quest_name}, {@code parse_quest_level}, {@code parse_quest_race} and
 * {@code parse_quest_number}) and then {@code finish_parse_quest} numbers the quests by their final
 * array position. The port folds all of that into the single {@link #assemble} method, because the
 * grammar has already grouped the four directives into one {@link QuestParseRecord}.
 *
 * <p><b>Dependency:</b> race resolution needs {@code monster.txt} already loaded, which is why
 * {@code loadQuests()} runs late in {@link GameConstants}' init order (after {@code loadMonsters()}).
 *
 * <p><b>Deliberate divergences from C</b> (decided 261013), all in how the two integer fields are read
 * and none reachable from the shipped {@code quest.txt}:
 * <ul>
 *   <li>C reads integers with {@code strtoul} in base 0, so a leading {@code 0} means octal and
 *       {@code 0x} means hexadecimal ({@code 010} is 8, {@code 08} is 0). The port always reads
 *       decimal. Base 0 parsing is skipped on purpose.</li>
 *   <li>A {@code level} above {@code Long.MAX_VALUE} is dropped as a soft error. C reads it into an
 *       {@code unsigned long}, so it wraps silently up to {@code ULONG_MAX} and saturates at
 *       {@code ULONG_MAX} beyond that, giving a stored level of 255. Quest levels that large are not
 *       realistic. Because the drop happens before the race is looked up, a record with such a level
 *       and an unknown {@code race} is dropped here, where C wraps the level and then aborts the load
 *       on the race.</li>
 *   <li>A {@code number} above {@code Integer.MAX_VALUE} is dropped as a soft error. C wraps it into a
 *       negative {@code int}. Anything above 1,000,000,000 is not realistic, so losing the record is
 *       acceptable.</li>
 * </ul>
 *
 * <p>Class QuestAssembler coded before 261013, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class QuestAssembler implements Assembler<QuestParseRecord, List<Quest>> {
    /**
     * Log4j logger for this class. {@link #assemble} writes to it at {@code fatal} level just before it
     * throws, so the reason for an aborted {@code quest.txt} load reaches the log as well as the stack
     * trace.
     *
     * <p>Field logger coded before 261013, commented in full on 261014.
     */
    private static final Logger logger = LogManager.getLogger(QuestAssembler.class);

    /**
     * Assemble every parsed record into a {@link Quest}, reading the {@code level} and {@code number}
     * text the way C's {@code parse_quest_level} and {@code parse_quest_number} do, and resolving the
     * {@code race} name the way {@code parse_quest_race} does.
     *
     * <p>The lexer's {@code INTEGER} token is an optional {@code -} followed by digits, so the only text
     * that reaches here is a signed run of digits. Each record is checked in the order C meets the
     * directives (level, then race, then number), and a failure ends in one of two ways:
     * <ul>
     *   <li><b>Dropped.</b> A {@code level} too large for a {@code long}, or a {@code number} too large
     *       for an {@code int}, raises {@link NumberFormatException}. The message is appended to
     *       {@code errors} and the record is skipped, honouring the suite's partial-results contract.</li>
     *   <li><b>Aborts the load.</b> A negative {@code level} or {@code number} throws
     *       {@link IllegalArgumentException}, and a {@code race} that matches no monster throws
     *       {@link RuntimeException}. C does the same: its unsigned parse rejects a leading {@code -}
     *       with {@code PARSE_ERROR_NOT_NUMBER}, and {@code parse_quest_race} returns
     *       {@code PARSE_ERROR_INVALID_MONSTER}, and either error stops the whole {@code quest.txt}
     *       load. Neither error is caught by {@code GrammarDriver.run} or {@code loadQuests}, so the
     *       exception reaches the caller of {@code GameConstants} init. Any text starting with
     *       {@code -} counts as negative, including {@code -0}, as in C's {@code *tok == '-'} test.</li>
     * </ul>
     * A dropped record ends its checks there: an overflowing {@code level} is dropped before the
     * {@code race} is looked up, and a record that fails on {@code race} never reaches {@code number}.
     * The race name is looked up with {@link MonsterRegistry#lookupMonsterRace}, exact match first and then
     * the first substring match, faithful to C {@code lookup_monster}.
     *
     * <p><b>Level.</b> C's {@code quest.level} is a {@code uint8_t}, and C's value passes through
     * {@code strtoul} and an {@code unsigned int} first, so the stored level is the parsed value modulo
     * 256. Casting to {@code int} can leave a negative value when bit 31 is set, and Java's {@code %}
     * keeps the sign of the dividend, so 256 is added back when the remainder is negative:
     * {@code 2147483649} gives 1 and {@code 4294967295} gives 255, as in C.
     *
     * <p><b>Number.</b> C's {@code quest.max_num} is a plain {@code int}, so the value is stored as
     * parsed, with no truncation: {@code 300} stays 300.
     *
     * <p>The {@code index} is advanced only just before a surviving quest is built, so it stays
     * contiguous over the assembled list even when earlier records were dropped (C stamps
     * {@code quest.index} = final array position). Each quest's kill tally starts empty, so
     * {@code currentNumber} is seeded to 0 (C {@code cur_num}); only {@code maxNumber} comes from the
     * file.
     *
     * <p>The remaining differences from C (decimal-only parsing, and the upper limits on the two
     * integers) are listed in the class block.
     *
     * <p>Function assemble coded before 260915, level and number handling reworked on 261013,
     * commented in full on 261014.
     *
     * @param records the raw quest records in file order
     * @param errors  the soft-error sink, appended to in place for every dropped record
     * @return the assembled quests, in file order, minus any that were dropped
     * @throws IllegalArgumentException if a record's {@code level} or {@code number} is negative
     * @throws RuntimeException         if a record's {@code race} matches no monster
     */
    @Override
    public List<Quest> assemble(@NotNull List<QuestParseRecord> records, @NotNull List<String> errors) {
        List<Quest> quests = new ArrayList<>();
        int index = -1;

        for (QuestParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            int level;
            try {
                if (record.level().startsWith("-")) {
                    String message = "Invalid negative level number: " + record.level();
                    logger.fatal(message);
                    throw new IllegalArgumentException(message);
                }
                long input = Long.parseLong(record.level());
                
                int levelInt = (int) input;
                
                level = levelInt % 256;
                if (level < 0) level += 256;
            } catch (NumberFormatException e) {
                errors.add("Quest at line " + line + " has " +
                        "an invalid level integer: " + record.level());
                continue;
            }
            MonsterRace race = MonsterRegistry.lookupMonsterRace(record.race());
            if (race == null) {
                String message = "Quest at line: " + line + " has " +
                        "an unknown monster race: " + record.race();
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            int currentNumber = 0;
            int maxNumber;
            try {
                if (record.number().startsWith("-")) {
                    String message = "Invalid negative number: " + record.number();
                    logger.fatal(message);
                    throw new IllegalArgumentException(message);
                }
                maxNumber = Integer.parseInt(record.number());
            } catch (NumberFormatException e) {
                errors.add("Quest at line: " + line + " has " +
                        "an invalid number integer: " + record.number());
                continue;
            }

            index++;
            quests.add(new Quest(index, name, level, race,
                    currentNumber, maxNumber));
        }

        return quests;
    }
}
