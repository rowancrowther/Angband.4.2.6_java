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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftrades.backend.parser.grammars.uientryrenderer.UIEntryRendererGrammar;
import uk.co.jackoftrades.backend.parser.grammars.uientryrenderer.UIEntryRendererLexer;
import uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link UIEntryRenderer} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author ClaudeCode
 */
public class UIEntryRendererReader implements Reader<UIEntryRendererParseRecord> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author ClaudeCode
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
    public List<UIEntryRendererParseRecord> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    public ParseResult<UIEntryRendererParseRecord> parseWithResults(@NotNull String filename) throws IOException {
        List<UIEntryRendererParseRecord> items = new ArrayList<>();
        List<List<String>> records;
        int recCount = 0;

        ParseErrors errorCatcher = null;

        try {
            CharStream input = CharStreams.fromFileName(filename);
            UIEntryRendererLexer lexer = new UIEntryRendererLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UIEntryRendererGrammar parser = new UIEntryRendererGrammar(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);
            UIEntryRendererGrammar.FileContext output = parser.file();

            records = output.renderers;

            errorCatcher.throwIfAny();

            try {
                recCount = Integer.parseInt(output.record);
            } catch (NumberFormatException e) {
                errorCatcher.add("Invalid number format for declared count");
                recCount = -1;
            }

            if (recCount != -1 && recCount != records.size()) {
                errorCatcher.add("record-count header declares " + recCount +
                        " but file contains " + records.size());
            }

            int line = -1;

            for (List<String> record : records) {
                try {
                    line = Integer.parseInt(record.getLast());
                    String name = record.get(0);
                    UIEntryRendererEnum code = UIEntryRendererEnum.valueOf("UI_ENTRY_RENDERER_" + record.get(1));
                    String colours = record.get(2);
                    String labelColours = record.get(3);
                    String symbols = record.get(4);
                    int nDigits = 1;
                    if (!record.get(5).isEmpty()) {
                        nDigits = Integer.parseInt(record.get(5));
                        if (nDigits == -1) {
                            nDigits = 1;
                        }
                    }
                    UIEntryEnum sign = UIEntryEnum.UI_ENTRY_SIGN_DEFAULT;
                    if (!record.get(6).isEmpty()) {
                        try {
                            sign = UIEntryEnum.valueOf("UI_ENTRY_" + record.get(6));
                        } catch (IllegalArgumentException e) {
                            errorCatcher.add("Line: " + record.getLast() + " Invalid" +
                                    " sign found: " + record.get(6));
                        }
                    }

                    UIEntryRendererParseRecord parseRecord = new UIEntryRendererParseRecord(line,
                            name, code, colours,
                            labelColours, symbols, nDigits, sign);

                    items.add(parseRecord);
                } catch (NumberFormatException e) {
                    errorCatcher.add("Line: " + record.getLast() + " Invalid number format found: "
                            + record.get(0));
                } catch (IllegalArgumentException e) {
                    errorCatcher.add("Line: " + record.getLast() + " Invalid code found: " +
                            record.get(1));
                }
            }

            errorCatcher.throwIfAny();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            return new ParseResult<>(List.of(), errorCatcher != null ? errorCatcher.getErrors() : List.of());
        }

        return new ParseResult<>(items, errorCatcher.getErrors());
    }
}