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
import uk.co.jackoftrades.backend.parser.grammars.realm.RealmGrammar;
import uk.co.jackoftrades.backend.parser.grammars.realm.RealmLexer;
import uk.co.jackoftrades.backend.parser.realm.RealmAssembler;
import uk.co.jackoftrades.backend.parser.realm.RealmParseRecord;
import uk.co.jackoftrades.middle.magic.MagicRealm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link MagicRealm} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class RealmReader implements Reader<MagicRealm> {
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
    public @NotNull List<MagicRealm> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult} — the assembled
     * {@link MagicRealm}s together with any soft errors collected during parsing and assembly.
     *
     * @param filename the realm data file to read
     * @return the parse result: the realms plus any error messages
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<MagicRealm> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                RealmLexer::new,
                RealmGrammar::new,
                RealmReader::extract,
                new RealmAssembler(), logger);
    }

    /**
     * Bridges the generated parser to the assembler input: runs the {@code file} rule, throws on any
     * hard syntax error via {@code errorCatcher}, then softly checks the declared {@code record-count}
     * header against the number of records actually parsed.
     *
     * @param parser       the generated parser positioned at the start of the file
     * @param errorCatcher collects syntax errors raised during the parse (thrown if any occurred)
     * @param errors       sink for soft, non-fatal messages such as a record-count mismatch
     * @return the raw parse records to hand to the assembler
     * @author Rowan Crowther
     */
    private static @NotNull List<RealmParseRecord> extract(
            @NotNull RealmGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        RealmGrammar.FileContext output = parser.file();
        List<RealmParseRecord> result = output.realms;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);
    }
}
