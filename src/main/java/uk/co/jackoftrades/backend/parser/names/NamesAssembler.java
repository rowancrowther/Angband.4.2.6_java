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

package uk.co.jackoftrades.backend.parser.names;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.game.Name;

import java.util.ArrayList;
import java.util.List;

public class NamesAssembler implements Assembler<NamesParseRecord, List<Name>> {
    /**
     *
     * @param records List of R_ParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of R objects
     */
    @Override
    public List<Name> assemble(@NotNull List<NamesParseRecord> records, @NotNull List<String> errors) {
        List<Name> names = new ArrayList<>();

        for (NamesParseRecord record : records) {
            int section = 0;
            try {
                section = Integer.parseInt(record.section());
            } catch (NumberFormatException e) {
                errors.add("Section " + record.section() + " is an invalid integer");
                continue;
            }
            List<String> words = new ArrayList<>();
            for (String word : record.word()) {
                words.add(word);
            }

            names.add(new Name(section, words));
        }

        return names;
    }
}
