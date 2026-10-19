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

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftradesltd.backend.parser.grammars.terrainfeature.TerrainFeatureGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.terrainfeature.TerrainFeatureLexer;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureAssembler;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.Feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code terrain.txt} into {@link Feature}s by driving {@code TerrainFeatureLexer} and
 * {@code TerrainFeatureGrammar}, the hand-written bridge between the generated parser and the game.
 * It stands in for {@code feat_parser} in the C original's {@code init.c}: {@code init_parse_feat}
 * (the sixteen directives from {@code code} to {@code resist-flag}, which the grammar's
 * {@code terrain} rule takes), {@code run_parse_feat} (a single call to
 * {@code parse_file_quit_not_found}) and the list-building half of {@code finish_parse_feat}.
 *
 * <pre>
 *   terrain.txt
 *     &rarr; TerrainFeatureLexer / TerrainFeatureGrammar  (ANTLR-generated)
 *     &rarr; TerrainFeatureParseRecord                    (raw-String DTO, one per record)
 *     &rarr; TerrainFeatureAssembler                      (resolves codes/flags, builds Feature)
 *     &rarr; ParseResult&lt;Feature&gt;
 * </pre>
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_feat_*} handlers and the look-string
 * half of {@code finish_parse_feat} are {@link TerrainFeatureAssembler}; the other half, the shop
 * numbers and {@code z_info->store_max}, is {@code TerrainDataLoader.loadTerrainFeatures()}, the
 * caller. {@code cleanup_feat} has no counterpart, as the garbage collector frees the features.
 *
 * <p>The features come back in file order. C allocates {@code f_info} with exactly {@code FEAT_MAX}
 * slots in {@code init_parse_feat} and files each record under its {@code code:}, so the table is in
 * {@code list-terrain.h} order whatever the file order and a code the file leaves out stays a zeroed
 * slot. The two agree for the shipped file, which has all 25 records in that order; for a shorter or
 * shuffled file the Java list has only the records it was given, and the missing code has no entry.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no features and the messages, and a {@code record-count:} mismatch
 * is a soft error that leaves the features that did parse loaded. The {@code record-count:} header
 * is Java-only, the upstream file has none, and it counts {@code code:} lines. A file that cannot be
 * opened is an {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places, each a hard error where C loads the file: a
 * record that is not a {@code code:}, {@code name:} and {@code graphics:} line in that order, a
 * repeated {@code graphics:} line, flags separated by a space alone, an empty {@code flags:} line,
 * a header with no records, hexadecimal or trailing text after a number, a second field after
 * {@code resist-flag:}, an unknown colour name and an indented line. It also differs in one way that
 * still loads: a repeated {@code walk-msg:}, {@code run-msg:}, {@code hurt-msg:}, {@code die-msg:},
 * {@code confused-msg:}, {@code look-prefix:} or {@code look-in-preposition:} line replaces the
 * earlier one, where C's {@code string_append} joins them. They are written up in
 * {@code docs/grammar_issues.md}.
 *
 * <p>The invariant lex, parse and assemble skeleton lives in {@link GrammarDriver}: this class supplies only
 * the lexer and parser constructors, the {@link #extract} step that pulls the parse records off the
 * {@code file} rule, and the assembler. Implementing {@link Reader} keeps
 * {@link uk.co.jackoftradesltd.middle.game.globals.GameConstants} agnostic of the grammar behind it.
 *
 * <p>Class TerrainReader coded before 260915, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class TerrainReader implements Reader<Feature> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name, with the offending filename, before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    Logger logger = LogManager.getLogger();

    /**
     * {@link Reader} entry point: reads {@code filename} and returns the assembled features in file
     * order, discarding the error messages. The convenience form of {@link #parseWithResults}, which
     * is the one to call when the errors matter: with this one a hard grammar error and a file in
     * which every record was dropped both come back as an empty list. A record the assembler cannot
     * resolve is skipped, where C would quit.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the terrain data file to read
     * @return the features read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @NotNull
    @Override
    public List<Feature> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of {@code code:} blocks
     * parsed. No value is interpreted here: the code, flags, numbers and colour stay as text for
     * {@link TerrainFeatureAssembler}.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a missing {@code record-count:} header, a record without its {@code name:} or
     * {@code graphics:} line, a line the lexer does not know) fails closed before the count check
     * runs against whatever did parse. The driver owns the surrounding cancellation catch, which is
     * why it passes {@code errorCatcher} in rather than this method calling {@code file()} itself.
     * The rule needs at least one record, where C accepts a file with none. The rows are copied into
     * a new list so the assembler cannot alter the parser's own.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here
     * @return the parse records, one per {@code code:} block, in file order
     */
    private static List<TerrainFeatureParseRecord> extract(
            @NotNull TerrainFeatureGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        TerrainFeatureGrammar.FileContext output = parser.file();
        List<TerrainFeatureParseRecord> results = output.features;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link Feature}s and every error message collected on the way. This is the Java form of C's
     * {@code run_parse_feat} followed by the list-building half of {@code finish_parse_feat}, and the
     * entry point {@code TerrainDataLoader.loadTerrainFeatures()} uses so that it can inspect
     * {@link ParseResult#hasErrors()} before registering the result. {@link GrammarDriver#run} does
     * the lex, parse, extract and assemble.
     *
     * <p>Where C quits on a code, mimic, flag, resist flag or number it cannot resolve, the assembler
     * records a soft error and drops the whole record. An unknown colour never gets that far: the
     * lexer rejects it as a hard error. A repeated {@code code:} is the exception in the other
     * direction: the assembler throws, as C quits on it. A file that cannot be opened is an
     * {@link IOException}, where C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the terrain data file to read
     * @return the parse result: the features in file order plus any error messages
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<Feature> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                TerrainFeatureLexer::new,
                TerrainFeatureGrammar::new,
                TerrainReader::extract,
                new TerrainFeatureAssembler(),
                logger);
    }

}
