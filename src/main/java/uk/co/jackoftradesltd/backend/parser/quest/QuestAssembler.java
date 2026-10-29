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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
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
 * <p><b>Every fault stops the load.</b> The assembler has no soft errors: a negative {@code level} or
 * {@code number}, a figure with no digits, and a {@code race} that matches no monster each log at
 * {@code fatal} and throw {@link DataFileParseException}, as C's {@code PARSE_ERROR_NOT_NUMBER} and
 * {@code PARSE_ERROR_INVALID_MONSTER} end the whole {@code quest.txt} load. No record is ever dropped
 * and the {@code errors} list passed to {@link #assemble} is never written to.
 *
 * <p><b>Integers.</b> {@code level} and {@code number} are {@code uint} fields in C, read by
 * {@code parser_parse} ({@code parser.c}) with {@code strtoul} in base 0 into an {@code unsigned int}.
 * They go through {@link ParseUtils#parseIntString(String, String, int, String, String)}, which reads base 0
 * as well, so {@code 010} is 8 and {@code 08} is 0 here as there; {@link ParseUtils#minusFound(String)}
 * is asked first about the raw text because C refuses any token starting with {@code -}, {@code -0}
 * included. The lexer's {@code INTEGER} token is an optional {@code -} and digits, so a {@code 0x}
 * prefix or a {@code +} cannot arrive.
 *
 * <p><b>Accepted deviation</b> (decided 261027; "An integer too big for its C type is clamped, where C
 * wraps it" under ParseUtils in {@code docs/Accepted_Deviations.md}): C keeps the low 32 bits of a figure
 * above {@code UINT_MAX} and wraps one above {@code INT_MAX} into a negative {@code int}. The port clamps
 * to {@link Integer#MAX_VALUE}, so {@code level:2147483649} stores 255 where C stores 1,
 * {@code level:4294967296} stores 255 where C stores 0, and {@code number:2147483648} stores
 * 2147483647 where C stores {@code INT_MIN}. The shipped {@code quest.txt} holds levels 99 and 100 and
 * a number of 1.
 *
 * <p>Class QuestAssembler coded before 261013, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class QuestAssembler implements Assembler<QuestParseRecord, List<Quest>> {
    /**
     * Log4j logger for this class. {@link #assemble} writes to it at {@code fatal} level just before each
     * of its three throws (negative {@code level}, unknown {@code race}, negative {@code number}), so the
     * reason for an aborted {@code quest.txt} load reaches the log as well as the stack trace. The
     * figure-reading faults inside {@link ParseUtils} are logged by {@code ParseUtils}' own logger.
     *
     * <p>Field logger coded before 261013, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(QuestAssembler.class);

    /**
     * Assemble every parsed record into a {@link Quest}, reading the {@code level} and {@code number}
     * text the way C's {@code parse_quest_level} and {@code parse_quest_number} do, and resolving the
     * {@code race} name the way {@code parse_quest_race} does.
     *
     * <p>Each record is checked in the order C meets the directives (level, then race, then number),
     * records in file order, and the first fault stops the whole load. Every fault logs at {@code fatal}
     * and throws {@link DataFileParseException}, which nothing between here and the caller of
     * {@code GameConstants} init catches, as C's parse errors abort the {@code quest.txt} load:
     * <ul>
     *   <li><b>Negative {@code level} or {@code number}.</b> C's {@code uint} test is
     *       {@code z == tok || *tok == '-'}, so any text starting with {@code -} is refused, {@code -0}
     *       included. {@link ParseUtils#minusFound(String)} makes the same first-character test on the
     *       raw text, before the figure is read.</li>
     *   <li><b>A figure with no digits.</b> {@link ParseUtils#parseIntString(String, String, int, String,
     *       String)} throws for it, as {@code z == tok} gives {@code PARSE_ERROR_NOT_NUMBER}. The lexer
     *       only ever passes digits, so this cannot be reached from a data file.</li>
     *   <li><b>Unknown {@code race}.</b> {@link MonsterRegistry#lookupMonsterRace} returns {@code null}
     *       where C's {@code lookup_monster} does, and {@code parse_quest_race} returns
     *       {@code PARSE_ERROR_INVALID_MONSTER}. The lookup is an exact case-insensitive match first and
     *       then the first race whose name contains the text.</li>
     * </ul>
     * A record that fails on {@code level} never has its {@code race} looked up, and one that fails on
     * {@code race} never reaches {@code number}. Because the load stops, no record is dropped and
     * {@code errors} is never written to.
     *
     * <p><b>Level.</b> C's {@code quest.level} is a {@code uint8_t}, so the stored level is the
     * base 0 value modulo 256: {@code 255} stays 255, {@code 256} gives 0, {@code 257} gives 1,
     * {@code 356} gives 100, {@code 010} gives 8 and {@code 08} gives 0. The figure is already clamped to
     * {@code 0..Integer.MAX_VALUE} by then (a negative one was refused above), so the
     * {@code if (level < 0) level += 256} line is unreachable. Above {@code Integer.MAX_VALUE} the
     * clamp makes the stored level 255, where C stores the low byte of the wrapped value (see the
     * accepted deviation in the class block).
     *
     * <p><b>Number.</b> C's {@code quest.max_num} is a plain {@code int}, so the value is stored as
     * read, with no truncation: {@code 300} stays 300 and {@code 2147483647} stays 2147483647. Above
     * that the clamp stores 2147483647, where C wraps into a negative {@code int}.
     *
     * <p>The {@code index} is the record's position in the returned list, 0 for the first, as
     * {@code finish_parse_quest} stamps {@code quest.index} from the final array position (C builds
     * the list newest-first and fills the array from the back, so the order is the file's). Each quest's
     * kill tally starts empty, so {@code currentNumber} is seeded to 0 (C {@code cur_num}, from
     * {@code mem_zalloc}); only {@code maxNumber} comes from the file. {@code QuestGrammar} makes all four
     * directives mandatory and in one order, so no field is ever absent here as it can be in C.
     *
     * <p>Function assemble coded before 260915, level and number handling reworked on 261013,
     * soft errors made fatal before 261029, commented in full on 261029.
     *
     * @param records  the raw quest records in file order
     * @param errors   the soft-error sink required by {@link Assembler}; never written to here
     * @param filename the file being read, for the messages
     * @return the assembled quests, in file order, each with the index of its position
     * @throws DataFileParseException if a record's {@code level} or {@code number} is negative or has
     *                                no digits, or its {@code race} matches no monster
     */
    @Override
    public List<Quest> assemble(@NotNull List<QuestParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Quest> quests = new ArrayList<>();
        int index = -1;

        for (QuestParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            int level;
            if (ParseUtils.minusFound(record.level())) {
                String message = "Quest starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative level value: " + record.level();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            level = ParseUtils.parseIntString("Quest", record.level(), line, filename, "level");
            level = level % 256;
            if (level < 0) level += 256;
            MonsterRace race = MonsterRegistry.lookupMonsterRace(record.race());
            if (race == null) {
                String message = "Quest starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal monster race value: " + record.race();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            int currentNumber = 0;
            int maxNumber;
            if (ParseUtils.minusFound(record.number())) {
                String message = "Quest starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative number value: " + record.number();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            maxNumber = ParseUtils.parseIntString("Quest", record.number(), line, filename, "number");


            index++;
            quests.add(new Quest(index, name, level, race,
                    currentNumber, maxNumber));
        }

        return quests;
    }
}
