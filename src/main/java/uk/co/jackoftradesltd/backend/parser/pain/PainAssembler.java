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

package uk.co.jackoftradesltd.backend.parser.pain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link PainParseRecord}s the grammar produces into domain {@link MonsterPain}
 * objects. The grammar is a pure text collector, so all interpretation happens here, matching the
 * ObjectBase pipeline.
 *
 * <p>Each record's message list arrives as {@code [typeNum, msg1..msgN]} - the {@code type:} serial
 * number is carried as element 0 for this assembler to peel back off and parse into the pain index.
 * The grammar accepts any number of {@code message:} lines after a {@code type:} line, so the count
 * is checked here.
 *
 * <p>It stands in for the two line handlers that read {@code pain.txt} in {@code mon-init.c}:
 * {@code parse_pain_type}, which reads the serial with {@code parser_getuint}, and
 * {@code parse_pain_message}, which fills {@code messages[7]} in order. {@code finish_parse_pain}
 * (the direct-access array and {@code z_info->mp_max}) is not done here; see {@code PainReader}.
 *
 * <p>Every problem ends the load. C's parser stops at the first error and the game quits, and here
 * each one is logged and thrown as a {@link DataFileParseException}, so a bad record is never
 * skipped. The ones C reports are a serial that is not a number (a leading {@code -}, which C
 * refuses for a {@code uint}, even on {@code -0}) and an eighth {@code message:} line
 * ({@code PARSE_ERROR_TOO_MANY_ENTRIES}). The serial is read as {@code strtoul} in base 0 reads it,
 * so {@code 010} is 8, {@code 08} and {@code 0089} are 0, as the digits stop at the first 8 or 9.
 * Fewer than seven messages is not an error in C, which leaves the missing slots {@code NULL}, and
 * is not one here: the list is padded with {@code null} to seven, and a {@code type:} with no
 * messages at all gives seven. The serial is not checked for being unique or in order, as in C.
 *
 * <p>Accepted deviation (decided 261027): a serial outside the range of an {@code int} is clamped
 * by {@link ParseUtils#parseIntString}, where C keeps the low 32 bits of the {@code strtoul} result
 * (so {@code 4294967296} is 0 in C). The shipped {@code pain.txt} runs from 1 to 12.
 *
 * <p>Accepted deviation (decided 261009, "for the time being"): the {@link MonsterPain} list is
 * base 0 and holds only real records, where C's {@code pain_messages} has a blank record in slot
 * 0. Nothing in this class builds that blank record.
 *
 * <p>Class PainAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class PainAssembler implements Assembler<PainParseRecord, List<MonsterPain>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown, so a bad
     * {@code type:} serial or message count is logged with its line and file as well as thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(PainAssembler.class);

    /**
     * Assemble the parsed pain records into {@link MonsterPain} objects, in file order, peeling the
     * serial number off the front of each message list. For each record, in the order C reads the
     * lines: a serial starting with {@code -} is refused ({@link ParseUtils#minusFound}, C's
     * {@code *tok == '-'} test in {@code parser_parse}), then the serial is read as an {@code int}
     * ({@code parse_pain_type}'s {@code parser_getuint}), then a list of more than seven messages is
     * refused ({@code parse_pain_message}'s {@code PARSE_ERROR_TOO_MANY_ENTRIES}), and a list of
     * fewer than seven is padded with {@code null} up to seven, the slots C leaves {@code NULL}.
     * Message text is passed on as written.
     *
     * <p>C reports the eighth message at the line of that {@code message:} directive; the message
     * here names the line of the {@code type:} directive that opened the record.
     *
     * <p>Every failure is fatal and the rest of the file is not read. {@code errors} is not written
     * to: it is part of the {@link Assembler} contract and nothing here is a soft error any more.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw pain records from the grammar, each {@code [typeNum, msg1..msgN]}
     * @param errors   the soft-error sink, left untouched
     * @param filename the file being read, for the messages
     * @return one {@link MonsterPain} per record, in the order given, each holding seven messages
     * @throws DataFileParseException if a serial starts with {@code -} or has no leading digits, or
     *                                a record has more than seven messages
     */
    @Override
    public List<MonsterPain> assemble(@NotNull List<PainParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MonsterPain> monsterPains = new ArrayList<>();

        for (PainParseRecord record : records) {
            int line = record.line();
            List<String> values = new ArrayList<>(record.messages());

            String rawType = values.removeFirst();
            if (ParseUtils.minusFound(rawType)) {
                String msg = "Pain record starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative found on the type value: " + rawType;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int type = ParseUtils.parseIntString("Pain record", rawType, line, filename, "type");
            if (values.size() > 7) {
                String msg = "Pain record starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal number of messages. Should be 7, is: " + values.size();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            } else if (values.size() < 7) {
                int currentSize = values.size();
                for (int index = currentSize; index < 7; index++) {
                    values.add(null);
                }
            }
            monsterPains.add(new MonsterPain(type, values));
        }

        return monsterPains;
    }
}
