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

package uk.co.jackoftrades.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.names.NamesGrammar;
import uk.co.jackoftrades.backend.parser.grammars.names.NamesLexer;
import uk.co.jackoftrades.backend.parser.names.NamesAssembler;
import uk.co.jackoftrades.backend.parser.names.NamesParseRecord;
import uk.co.jackoftrades.middle.game.Name;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NamesReader implements Reader<Name> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<Name> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    public ParseResult<Name> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                NamesLexer::new,
                NamesGrammar::new,
                NamesReader::extract,
                new NamesAssembler(), logger);
    }

    private static List<NamesParseRecord> extract(
            @NotNull NamesGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        NamesGrammar.FileContext output = parser.file();
        List<NamesParseRecord> records = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }
}
