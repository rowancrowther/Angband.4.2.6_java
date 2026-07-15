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
import uk.co.jackoftrades.backend.parser.grammars.playerclass.PlayerClassGrammar;
import uk.co.jackoftrades.backend.parser.grammars.playerclass.PlayerClassLexer;
import uk.co.jackoftrades.backend.parser.playerclass.PlayerClassAssembler;
import uk.co.jackoftrades.backend.parser.playerclass.PlayerClassParseRecord;
import uk.co.jackoftrades.middle.player.PlayerClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link PlayerClass} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class PlayerClassReader implements Reader<PlayerClass> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<PlayerClass> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult} — the assembled
     * {@link PlayerClass}es together with any soft errors collected during parsing and assembly.
     * Prefer this over {@link #parse} when the caller needs to see the errors (e.g. to reject a file
     * that loaded with problems, as {@code GameConstants} does).
     *
     * @param filename the class data file to read
     * @return the parse result: the classes plus any error messages
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<PlayerClass> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerClassLexer::new,
                PlayerClassGrammar::new,
                PlayerClassReader::extract,
                new PlayerClassAssembler(), logger);
    }

    private static List<PlayerClassParseRecord> extract(
            @NotNull PlayerClassGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        PlayerClassGrammar.FileContext output = parser.file();
        List<PlayerClassParseRecord> results = output.playerClasses;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }
}
