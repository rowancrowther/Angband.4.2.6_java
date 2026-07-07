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

package uk.co.jackoftrades.backend.parser.pain;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.monsters.MonsterPain;


import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link PainParseRecord}s the grammar produces into domain {@link MonsterPain}
 * objects. The grammar is a pure text collector, so all interpretation happens here, matching the
 * ObjectBase pipeline.
 *
 * <p>Each record's message list arrives as {@code [typeNum, msg1..msg7]} - the {@code type:} serial
 * number is carried as element 0 for this assembler to peel back off and parse into the pain index.
 * Problems are handled softly: a non-numeric or out-of-range serial, or a message count other than
 * seven, appends to {@code errors} and skips just that record, so one malformed block never sinks
 * the rest of the file.
 *
 * @author Rowan Crowther
 */
public class PainAssembler implements Assembler<PainParseRecord, List<MonsterPain>> {
    /**
     * Assemble the parsed pain records into {@link MonsterPain} objects, peeling the serial number
     * off the front of each message list and skipping any record that fails to resolve.
     *
     * @param records the raw pain records from the grammar
     * @param errors  the soft-error sink, mutated in place with one message per skipped record
     * @return the successfully assembled {@link MonsterPain} sets
     * @author Rowan Crowther
     */
    @Override
    public List<MonsterPain> assemble(@NotNull List<PainParseRecord> records, @NotNull List<String> errors) {
        List<MonsterPain> monsterPains = new ArrayList<>();

        for (PainParseRecord record : records) {
            int line = record.line();
            List<String> values = new ArrayList<>(record.messages());

            String rawType = values.removeFirst();
            int type = 0;
            try {
                type = Integer.parseInt(rawType);
            } catch (NumberFormatException e) {
                errors.add("Block at line: " + line + " has an invalid " +
                        "type value: " + rawType);
                continue;
            }
            if (values.size() != 7) {
                errors.add("Block at line: " + line + " has an invalid " +
                        "number of messages. Should be 7, is: " + values.size());
                continue;
            }

            monsterPains.add(new MonsterPain(type, values));
        }

        return monsterPains;
    }
}
