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

import uk.co.jackoftrades.backend.parser.grammars.terrainfeature.TerrainFeatureGrammar;
import uk.co.jackoftrades.backend.parser.grammars.terrainfeature.TerrainFeatureLexer;
import uk.co.jackoftrades.backend.parser.terrainfeature.TerrainFeatureAssembler;
import uk.co.jackoftrades.backend.parser.terrainfeature.TerrainFeatureParseRecord;
import uk.co.jackoftrades.middle.cave.Feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/terrain.txt} into {@link Feature} objects, the Java port of the C
 * {@code feat_parser} in {@code src/init.c}. This is the thin, hand-written reader that sits at
 * the head of the terrain pipeline:
 *
 * <pre>
 *   terrain.txt
 *     &rarr; TerrainFeatureLexer / TerrainFeatureGrammar  (ANTLR-generated)
 *     &rarr; TerrainFeatureParseRecord                    (raw-String DTO, one per record)
 *     &rarr; TerrainFeatureAssembler                      (resolves codes/flags, builds Feature)
 *     &rarr; ParseResult&lt;Feature&gt;
 * </pre>
 *
 * <p>The invariant lex&rarr;parse&rarr;assemble skeleton lives in the shared {@link GrammarDriver};
 * this class supplies only the three things that genuinely vary per grammar - the lexer/parser
 * constructors, the {@link #extract} step that pulls the parse records off the {@code file} rule,
 * and the {@link TerrainFeatureAssembler}. Implementing {@link Reader} keeps
 * {@link uk.co.jackoftrades.middle.game.globals.GameConstants} agnostic of the grammar behind it.
 *
 * @author Rowan Crowther
 */
public class TerrainReader implements Reader<Feature> {
    /**
     * Logger handed to {@link GrammarDriver} so I/O failures opening the data file are reported
     * (and rethrown) with the offending filename.
     *
     * @author Rowan Crowther
     */
    Logger logger = LogManager.getLogger();

    /**
     * {@link Reader} entry point: parse the file and discard the error channel, returning only the
     * successfully assembled features. Callers that need to inspect soft (assembly) errors should
     * use {@link #parseWithResults} instead.
     *
     * @param filename the terrain data file to load
     * @return the features that assembled cleanly (bad records are skipped, not thrown)
     * @throws IOException if the file cannot be opened/read
     * @author Rowan Crowther
     */
    @NotNull
    @Override
    public List<Feature> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Full-fidelity load: drives the grammar through {@link GrammarDriver} and returns both the
     * assembled {@link Feature} list and the collected soft-error messages. A hard grammar/lexer
     * error fails the whole file closed (empty items, errors carried); a soft error skips just the
     * offending record. This two-channel result is what {@code GameConstants.loadTerrainFeatures}
     * checks via {@link ParseResult#hasErrors()} before accepting the data.
     *
     * @param filename the terrain data file to load
     * @return the assembled features paired with any soft-error messages
     * @throws IOException if the file cannot be opened/read
     * @author Rowan Crowther
     */
    public ParseResult<Feature> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                TerrainFeatureLexer::new,
                TerrainFeatureGrammar::new,
                TerrainReader::extract,
                new TerrainFeatureAssembler(),
                logger);
    }

    /**
     * The per-grammar residue the {@link GrammarDriver} cannot generalise: run the top-level
     * {@code file} rule and hand back the raw parse records. Ordering is load-bearing -
     * {@link ParseErrors#throwIfAny()} must fire <em>after</em> {@code file()} (so lexer/parser
     * syntax errors abort before any record is trusted) but the driver owns the surrounding
     * cancellation catch, which is why it passes {@code errorCatcher} in rather than calling
     * {@code file()} itself. The record-count check is soft: a mismatch is appended to
     * {@code errors} but the valid records still load.
     *
     * @param parser       the grammar positioned at the start of the token stream
     * @param errorCatcher hard-error channel; {@link ParseErrors#throwIfAny()} aborts the file
     * @param errors       soft-error channel; the record-count mismatch message is added here
     * @return the parse records for the assembler to resolve into {@link Feature} objects
     * @author Rowan Crowther
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

}
