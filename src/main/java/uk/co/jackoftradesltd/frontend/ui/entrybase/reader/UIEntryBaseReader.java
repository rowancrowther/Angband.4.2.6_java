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

package uk.co.jackoftradesltd.frontend.ui.entrybase.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase.UIEntryBaseGrammar;
import uk.co.jackoftradesltd.frontend.ui.entrybase.antlr4.uientrybase.UIEntryBaseLexer;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseAssembler;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseParseRecord;

import java.io.IOException;
import java.util.List;

/**
 * Loads {@code ui_entry_base.txt} into {@link UIEntry} objects by driving the
 * matching ANTLR-generated lexer/parser. Each base record becomes a plain {@link UIEntry}
 * flagged {@code ENTRY_FLAG_TEMPLATE_ONLY}, as in C, where bases and ordinary entries share one
 * entry list. The thin hand-written bridge between the generated grammar code and the game,
 * implementing the shared {@link Reader} contract (Java port of the equivalent C data-file
 * parser). The reader does not register what it returns; {@code UIDataLoader.loadUIEntryBases()}
 * does that.
 *
 * @author Rowan Crowther
 */
public class UIEntryBaseReader implements Reader<UIEntry> {
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
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<UIEntry> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Runs the full lexer &rarr; parser &rarr; extract &rarr; assemble pipeline for
     * {@code ui_entry_base.txt} through {@link GrammarDriver#run} and returns the
     * assembled entries. This is the method {@link #parse(String)} delegates to.
     * Every error is fatal and none is caught here: a syntax error is thrown as a
     * {@code ParseCancellationException} by {@link #extract}, and a record the
     * assembler refuses as a {@code DataFileParseException}.
     *
     * <p>Method parseWithResults coded before 261104, commented in full on 261104
     * for the removal of the soft-error path.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} carrying the assembled {@code TEMPLATE_ONLY}
     * {@link UIEntry} objects, one per base record
     * @throws IOException if the file cannot be found or its {@code CharStream}
     *                     cannot be created
     */
    public ParseResult<UIEntry> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                UIEntryBaseLexer::new,
                UIEntryBaseGrammar::new,
                UIEntryBaseReader::extract,
                new UIEntryBaseAssembler(), logger);
    }

    /**
     * Bridges the generated parser's typed output back to the plain
     * {@link UIEntryBaseParseRecord} list the {@link GrammarDriver} expects.
     * Runs the top-level {@code file} rule and throws, through
     * {@link ParseErrors#throwIfAny()}, if the parse collected any syntax
     * errors, so the records are only returned from a clean parse.
     *
     * <p>Method extract coded before 261104, commented in full on 261104 for the
     * removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the ANTLR parser positioned at the start of the file
     * @param errorCatcher accumulator of syntax errors gathered during the parse
     * @return the list of raw records parsed from the file
     */
    private static List<UIEntryBaseParseRecord> extract(
            @NotNull UIEntryBaseGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        UIEntryBaseGrammar.FileContext output = parser.file();
        List<UIEntryBaseParseRecord> results = output.uiEntryBases;
        errorCatcher.throwIfAny();

        return results;
    }
}