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
import uk.co.jackoftrades.backend.parser.grammars.visuals.VisualsGrammar;
import uk.co.jackoftrades.backend.parser.grammars.visuals.VisualsLexer;
import uk.co.jackoftrades.backend.parser.visuals.VisualsCycleAssembler;
import uk.co.jackoftrades.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftrades.backend.parser.visuals.VisualsFlickerAssembler;
import uk.co.jackoftrades.backend.parser.visuals.VisualsFlickerParseRecord;
import uk.co.jackoftrades.frontend.colour.FlickerTable;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;

import java.io.IOException;
import java.util.List;

/**
 * Loads visuals.txt into its two colour-cycling tables by driving the ANTLR-generated
 * {@code VisualsLexer}/{@code VisualsGrammar}. The one file feeds two independent aggregates - a
 * {@link VisualsCycler} (named cycles, keyed group-then-name) and a {@link FlickerTable} (cycles
 * keyed by base attribute) - so this reader exposes a parse method per table rather than
 * implementing the list-shaped {@link Reader} the per-entry readers use. Java port of the C visuals
 * loading ({@code ui-visuals.c}).
 * <p>
 * Each table is produced by its own single-aggregate assembler wrapped as a one-element list to fit
 * {@link GrammarDriver}'s list-shaped contract; the {@code parse*} convenience methods unwrap that
 * with {@code items().getFirst()}, while the {@code parse*WithResults} methods hand back the full
 * {@link ParseResult} (aggregate plus any soft errors) that {@code GameConstants} inspects before
 * installing the tables.
 * <p>
 * <strong>Two parses.</strong> The grammar's {@code file} rule yields both halves at once, but the
 * cycler and flicker paths currently each run their own full parse and keep only their half. That is
 * correct but does duplicate the lexing/parsing work; folding them into one parse is a possible later
 * refactor.
 *
 * @author Rowan Crowther
 */
public class VisualsReader {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse visuals.txt and return just its {@link VisualsCycler}, discarding any soft errors. Use
     * {@link #parseCyclerWithResults} instead when the errors matter.
     *
     * @param filename the visuals data file to read
     * @return the assembled named-cycle table
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public @NotNull VisualsCycler parseCycler(@NotNull String filename) throws IOException {
        return parseCyclerWithResults(filename).items().getFirst();
    }

    /**
     * Parse visuals.txt and return just its {@link FlickerTable}, discarding any soft errors. Use
     * {@link #parseFlickerWithResults} instead when the errors matter.
     *
     * @param filename the visuals data file to read
     * @return the assembled base-attribute flicker table
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public FlickerTable parseFlicker(@NotNull String filename) throws IOException {
        return parseFlickerWithResults(filename).items().getFirst();
    }

    /**
     * Parse visuals.txt into its {@link VisualsCycler}, keeping the soft errors alongside. The
     * aggregate is the sole element of the returned result's item list; a hard parse failure yields an
     * empty list carrying the error messages instead.
     *
     * @param filename the visuals data file to read
     * @return the parse result wrapping the cycler and any soft errors
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<VisualsCycler> parseCyclerWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                VisualsLexer::new,
                VisualsGrammar::new,
                VisualsReader::extractCycles,
                new VisualsCycleAssembler(), logger);
    }

    /**
     * Parse visuals.txt into its {@link FlickerTable}, keeping the soft errors alongside. The
     * aggregate is the sole element of the returned result's item list; a hard parse failure yields an
     * empty list carrying the error messages instead.
     *
     * @param filename the visuals data file to read
     * @return the parse result wrapping the flicker table and any soft errors
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<FlickerTable> parseFlickerWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                VisualsLexer::new,
                VisualsGrammar::new,
                VisualsReader::extractFlickers,
                new VisualsFlickerAssembler(), logger);
    }

    /**
     * {@link GrammarDriver.Extractor} for the cycler half: run the {@code file} rule, fail closed on
     * any hard parse error, and return only its {@code cycle:} records (the flicker half is ignored on
     * this pass).
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the shared hard-error catcher; {@link ParseErrors#throwIfAny()} fires after
     *                     the parse to fail closed before the records are used
     * @param errors       the soft-error sink (unused here; the cycler's soft errors arise in the
     *                     assembler, not this extractor)
     * @return the parsed {@code cycle:} records
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
     * @author Rowan Crowther
     */
    private static List<VisualsCycleParseRecord> extractCycles(
            @NotNull VisualsGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) throws IOException {
        VisualsGrammar.FileContext output = parser.file();
        List<VisualsCycleParseRecord> result = output.cycles;
        errorCatcher.throwIfAny();

        return result;
    }

    /**
     * {@link GrammarDriver.Extractor} for the flicker half: run the {@code file} rule, fail closed on
     * any hard parse error, and return only its {@code flicker:} records (the cycle half is ignored on
     * this pass).
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the shared hard-error catcher; {@link ParseErrors#throwIfAny()} fires after
     *                     the parse to fail closed before the records are used
     * @param errors       the soft-error sink (unused; the flicker assembler has no soft-error path)
     * @return the parsed {@code flicker:} records
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
     * @author Rowan Crowther
     */
    private static List<VisualsFlickerParseRecord> extractFlickers(
            @NotNull VisualsGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) throws IOException {
        VisualsGrammar.FileContext output = parser.file();
        List<VisualsFlickerParseRecord> result = output.flickers;
        errorCatcher.throwIfAny();

        return result;
    }
}
