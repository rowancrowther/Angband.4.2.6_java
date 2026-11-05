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
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a value the assembler cannot
 * resolve is thrown as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}.
 * Neither is caught here, and no features are returned. A file that cannot be opened is an
 * {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places, each a hard error where C loads the file: a
 * record that is not a {@code code:}, {@code name:} and {@code graphics:} line in that order, a
 * repeated {@code graphics:} line, flags separated by a space alone, an empty {@code flags:} line,
 * a file with no records, hexadecimal or trailing text after a number, a second field after
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
 * <p>Class TerrainReader coded before 260915, commented in full on 261019, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header.
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
    private static final Logger logger = LogManager.getLogger(TerrainReader.class);

    /**
     * {@link Reader} entry point: reads {@code filename} and returns the assembled features in file
     * order, unwrapped from the {@link ParseResult} that {@link #parseWithResults} gives. Any error
     * throws exactly as it does there, as C quits.
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the terrain data file to read
     * @return every feature in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    @NotNull
    @Override
    public List<Feature> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the code, flags, numbers and
     * colour stay as text for {@link TerrainFeatureAssembler}.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a record without its {@code name:} or {@code graphics:} line, a line the lexer
     * does not know) fails closed with a {@code ParseCancellationException} before any row is
     * used. The driver catches nothing but an {@link IOException}, so that exception reaches the
     * loader. The rule needs at least one record, where C accepts a file with none. The rows are
     * copied into a new list so the assembler cannot alter the parser's own.
     *
     * <p>Function extract coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parse records, one per {@code code:} block, in file order
     */
    private static List<TerrainFeatureParseRecord> extract(
            @NotNull TerrainFeatureGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        TerrainFeatureGrammar.FileContext output = parser.file();
        List<TerrainFeatureParseRecord> results = output.features;
        errorCatcher.throwIfAny();

        return new ArrayList<>(results);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link Feature}s wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_feat} followed by the
     * list-building half of {@code finish_parse_feat}, and the entry point
     * {@code TerrainDataLoader.loadTerrainFeatures()} uses. {@link GrammarDriver#run} does the lex,
     * parse, extract and assemble.
     *
     * <p>As C quits on a code, mimic, flag, resist flag or number it cannot resolve, or on a
     * repeated {@code code:}, the assembler throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException} there. An unknown colour
     * never gets that far: the lexer rejects it, and it is thrown as a
     * {@code ParseCancellationException}. No result is returned for a file that fails. A file that
     * cannot be opened is an {@link IOException}, where C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the terrain data file to read
     * @return every feature in the file, in file order
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
