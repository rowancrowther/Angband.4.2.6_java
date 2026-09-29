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

import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.antlr4.uientryrenderer.UIEntryRendererGrammar;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.antlr4.uientryrenderer.UIEntryRendererLexer;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererAssembler;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * First stage of the {@code ui_entry_renderer.txt} pipeline: drives the ANTLR-generated
 * {@link UIEntryRendererLexer} and {@link UIEntryRendererGrammar}, checks the declared
 * {@code record-count}, and hands the raw text of each record to the
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
 * <p>Errors travel two ways. A grammar or lexer error is fail-closed: {@code errorCatcher.throwIfAny()}
 * in {@link #extract} aborts the read and the caller gets an empty list plus the collected
 * messages. A {@code record-count} mismatch is soft: it is appended to the error list and the
 * records still flow through to the assembler.
 *
 * <p>The directives C accepts that this reader cannot carry ({@code combine}, {@code units},
 * {@code combined-renderer}), the fixed directive order, the mandatory {@code code} and the missing
 * merge of a repeated {@code name} are recorded under "Things not to drop" in
 * {@code docs/precis/260929.md}; none of them is reached by the shipped file.
 *
 * <p>Class UIEntryRendererReader coded before 260929, commented in full on 260929.
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
     * {@link UIEntryRendererParseRecord}. The order is: parse, {@code errorCatcher.throwIfAny()}
     * (fail-closed on any lexer or grammar error), compare the declared {@code record-count}
     * against the number of records with {@link GrammarDriver#checkRecordCount} (soft: appends to
     * {@code errors} and carries on), then convert every record.
     *
     * <p>The count compared is the number of {@code name:} blocks parsed, not the number of distinct
     * renderers, so a file that repeats a {@code name} counts each block.
     *
     * <p>Function extract coded before 260929, commented in full on 260929.
     *
     * @param parser       the generated grammar positioned at the start of the file
     * @param errorCatcher collects lexer and grammar errors; a non-empty catcher aborts the read
     * @param errors       receives the soft {@code record-count} mismatch message
     * @return one parse record per record in the file, in file order
     */
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
     * Reads {@code filename} and returns only the resolved renderers, discarding the error list.
     * It is exactly {@code parseWithResults(filename).items()}, so a fail-closed grammar error
     * gives an empty list here and the reason is visible only through the logger. Callers that
     * need the messages use {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260929, commented in full on 260929.
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
     * as the second stage, and returns both the renderers and every error message.
     *
     * <p>A hard grammar or lexer error gives an empty item list. A {@code record-count} mismatch
     * or an assembler rejection (unknown {@code code}, unknown {@code sign}, {@code ndigit} below
     * 1) is soft: it adds a message and the valid records still come through in file order.
     *
     * <p>Function parseWithResults coded before 260929, commented in full on 260929.
     *
     * @param filename the path of the file being parsed
     * @return a {@link ParseResult} holding the {@link UIEntryRenderer} list and the errors
     * raised during the parse and assembly
     * @throws IOException when the file cannot be read or does not exist
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