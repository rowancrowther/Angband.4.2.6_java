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
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftrades.backend.parser.grammars.uientryrenderer.UIEntryRendererGrammar;
import uk.co.jackoftrades.backend.parser.grammars.uientryrenderer.UIEntryRendererLexer;
import uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererAssembler;
import uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link UIEntryRenderer} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin handwritten bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class UIEntryRendererReader implements Reader<UIEntryRenderer> {
    /**
     * Logger used to report file-loading failures.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<UIEntryRenderer> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Run the parser and generate both the ArrayList and the error list, returning
     * a ParseResult which contains both
     *
     * @param filename The filename of the file we are parsing
     * @return A ParseResult containing a List of {@link UIEntryRenderer}
     * and a list of errors thrown during the parse
     * @throws IOException when the file cannot be read/is not existent
     */
    @NotNull
    @CheckReturnValue
    public ParseResult<UIEntryRenderer> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                UIEntryRendererLexer::new,
                UIEntryRendererGrammar::new,
                UIEntryRendererReader::extract,
                new UIEntryRendererAssembler(), logger);
    }

    private static List<UIEntryRendererParseRecord> extract(
            @NotNull UIEntryRendererGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        List<UIEntryRendererParseRecord> records = new ArrayList<>();
        UIEntryRendererGrammar.FileContext output = parser.file();
        List<List<String>> results = output.renderers;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.record;
        GrammarDriver.checkRecordCount(declaredRecordCount,
                results.size(), errors);

        for (List<String> result : results) {
            int line = Integer.parseInt(result.getLast());

            records.add(getUiEntryRendererParseRecord(result));
        }

        return records;
    }

    /**
     * Small function to turn a list of strings into a {@link UIEntryRendererParseRecord}
     *
     * @param record the list of strings that have been read in from
     *               lib/gamedata/ui_entry_renderer.txt
     * @return A UIEntryRendererParseRecord
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @NotNull
    private static UIEntryRendererParseRecord getUiEntryRendererParseRecord(@NotNull List<String> record) {
        String line = record.getLast();
        String name = record.get(0);
        String code = record.get(1);
        String colours = record.get(2);
        String labelColours = record.get(3);
        String symbols = record.get(4);
        String nDigits = record.get(5);
        String sign = record.get(6);

        return new UIEntryRendererParseRecord(line, name, code, colours,
                labelColours, symbols, nDigits, sign);
    }
}