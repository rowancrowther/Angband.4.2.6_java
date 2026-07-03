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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.uientrybase.UIEntryBaseGrammar;
import uk.co.jackoftrades.backend.parser.grammars.uientrybase.UIEntryBaseLexer;
import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseAssembler;
import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;

import java.io.IOException;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link UIEntryBase} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class UIEntryBaseReader implements Reader<UIEntryBase> {
    /**
     * Logger used to report file-loading failures.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     * @throws IOException if there is an error loading the file stream
     * @author Rowan Crowther
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<UIEntryBase> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Runs the full lexer &rarr; parser &rarr; extract &rarr; assemble pipeline for
     * {@code ui_entry_base.txt} and returns the outcome together with any
     * accumulated errors. This is the method {@link #parse(String)} delegates to;
     * callers that need the error list (rather than just the items) use this
     * variant directly.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} carrying the assembled {@link UIEntryBase}
     * templates and any non-fatal errors collected along the way
     * @throws IOException if the file cannot be found or its {@code CharStream}
     *                     cannot be created
     * @author Rowan Crowther
     */
    public ParseResult<UIEntryBase> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                UIEntryBaseLexer::new,
                UIEntryBaseGrammar::new,
                UIEntryBaseReader::extract,
                new UIEntryBaseAssembler(), logger);
    }

    /**
     * Bridges the generated parser's typed output back to the plain
     * {@link UIEntryBaseParseRecord} list the {@link GrammarDriver} expects.
     * Runs the top-level {@code file} rule, rethrows immediately if the parse
     * collected any syntax errors, then cross-checks the record-count header
     * declared in the file against the number of records actually parsed,
     * appending a message to {@code errors} on a mismatch.
     *
     * @param parser       the ANTLR parser positioned at the start of the file
     * @param errorCatcher accumulator of syntax errors gathered during the parse
     * @param errors       collector for non-syntax (e.g. record-count) problems
     * @return the list of raw records parsed from the file
     * @author Rowan Crowther
     */
    private static List<UIEntryBaseParseRecord> extract(
            @NotNull UIEntryBaseGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        UIEntryBaseGrammar.FileContext output = parser.file();
        List<UIEntryBaseParseRecord> results = output.uiEntryBases;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount,
                results.size(), errors);

        return results;
    }
}