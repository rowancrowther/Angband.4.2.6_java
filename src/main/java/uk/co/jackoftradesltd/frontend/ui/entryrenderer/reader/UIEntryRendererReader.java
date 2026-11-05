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

package uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.antlr4.uientryrenderer.UIEntryRendererGrammar;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.antlr4.uientryrenderer.UIEntryRendererLexer;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererAssembler;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererParseRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * First stage of the {@code ui_entry_renderer.txt} pipeline: drives the ANTLR-generated
 * {@link UIEntryRendererLexer} and {@link UIEntryRendererGrammar}, and hands the raw text of each
 * record to the
 * {@link UIEntryRendererAssembler} as a {@link UIEntryRendererParseRecord}. It implements the
 * shared {@link Reader} contract and is the thin handwritten boundary between the generated
 * grammar code and the game.
 *
 * <p>The C original is {@code ui-entry-renderers.c}, where {@code init_parse_ui_entry_renderer}
 * registers one callback per directive ({@code parse_renderer_name}, {@code parse_renderer_code},
 * {@code parse_renderer_colors}, {@code parse_renderer_labelcolors}, {@code parse_renderer_symbols},
 * {@code parse_renderer_ndigit}, {@code parse_renderer_sign}) and
 * {@code finish_parse_ui_entry_renderer} applies the backend defaults afterwards. Here the split
 * is different: this class and the grammar do the parsing, holding text only, while the assembler
 * does the {@code code} and {@code sign} lookups, the {@code ndigit} range check and the defaulting.
 *
 * <p>Every error is fatal, as in C, where {@code run_parser} quits on the first bad line. A grammar
 * or lexer error makes {@code errorCatcher.throwIfAny()} in {@link #extract} throw a
 * {@code ParseCancellationException} before any record is built. A record the assembler refuses
 * throws a {@code DataFileParseException}. Either way nothing is returned, so no partial list of
 * renderers ever reaches the caller.
 *
 * <p>The directives C accepts that this reader cannot carry ({@code combine}, {@code units},
 * {@code combined-renderer}), the fixed directive order, the mandatory {@code code} and the missing
 * merge of a repeated {@code name} are recorded under "Things not to drop" in
 * {@code docs/precis/260929.md}; none of them is reached by the shipped file.
 *
 * <p>Class UIEntryRendererReader coded before 260929, commented in full on 260929, updated on
 * 261105 for the removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class UIEntryRendererReader implements Reader<UIEntryRenderer> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that lexer, grammar and assembly errors for
     * this file are reported under this class's name.
     *
     * <p>Field logger coded before 260929, commented in full on 260929.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Runs the grammar's {@code file} rule and turns each parsed record into a
     * {@link UIEntryRendererParseRecord}. The order is: parse, then
     * {@code errorCatcher.throwIfAny()}, which throws on any lexer or grammar error, then convert
     * every record. Rows are only converted once the parse is clean.
     *
     * <p>Function extract coded before 260929, commented in full on 260929, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated grammar positioned at the start of the file
     * @param errorCatcher collects lexer and grammar errors; a non-empty catcher aborts the read
     * @return one parse record per record in the file, in file order
     * @throws org.antlr.v4.runtime.misc.ParseCancellationException if the parse raised any lexer or
     *                                                              grammar error
     */
    private static List<UIEntryRendererParseRecord> extract(
            @NotNull UIEntryRendererGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        List<UIEntryRendererParseRecord> records = new ArrayList<>();
        UIEntryRendererGrammar.FileContext output = parser.file();
        List<List<String>> results = output.renderers;
        errorCatcher.throwIfAny();

        for (List<String> result : results) {
            records.add(getUiEntryRendererParseRecord(result));
        }

        return records;
    }

    /**
     * Unpacks the grammar's positional string list into a {@link UIEntryRendererParseRecord}. The
     * grammar's {@code uiEntry} rule fills seven slots in a fixed order - name, code, colours,
     * label colours, symbols, {@code ndigit}, sign - with the empty string for any optional
     * directive that was absent, then appends the record's line number as the last element. This
     * method reads slots 0 to 6 by index and takes the line from {@code getLast()}; nothing is
     * validated or defaulted here, so an absent directive stays {@code ""} for the assembler to
     * default.
     *
     * <p>Function getUiEntryRendererParseRecord coded before 260929, commented in full on 260929.
     *
     * @param record the seven directive strings followed by the line number, as built by the
     *               grammar from {@code lib/gamedata/ui_entry_renderer.txt}
     * @return the equivalent {@link UIEntryRendererParseRecord}
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

    /**
     * Reads {@code filename} and returns the resolved renderers. It is exactly
     * {@code parseWithResults(filename).items()}, and any grammar, lexer or assembler error
     * throws out of it, as from {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260929, commented in full on 260929, updated on 261105 for the
     * removal of the soft-error path.
     *
     * @param filename the path of the data file, normally {@code lib/gamedata/ui_entry_renderer.txt}
     * @return the renderers read from the file, in file order, never null
     * @throws IOException when the file cannot be read or does not exist
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<UIEntryRenderer> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Reads {@code filename} through {@link GrammarDriver#run}, wiring in the generated lexer and
     * grammar, {@link #extract} as the record extractor and a new {@link UIEntryRendererAssembler}
     * as the second stage, and returns the renderers wrapped in a {@link ParseResult}.
     *
     * <p>Every error is fatal. A grammar or lexer error throws a
     * {@code ParseCancellationException} from {@link #extract}. An assembler rejection, such as an
     * unknown {@code code}, an unknown {@code sign} or an {@code ndigit} below 1, throws a
     * {@code DataFileParseException}. A result is only returned when every record loaded.
     *
     * <p>Function parseWithResults coded before 260929, commented in full on 260929, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the path of the file being parsed
     * @return a {@link ParseResult} holding every {@link UIEntryRenderer} in the file, in file order
     * @throws IOException when the file cannot be read or does not exist
     * @throws org.antlr.v4.runtime.misc.ParseCancellationException if the parse raised any lexer or
     *                                                              grammar error
     * @throws uk.co.jackoftradesltd.channel.parser.DataFileParseException if the assembler refuses
     *                                                                      a record
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
}