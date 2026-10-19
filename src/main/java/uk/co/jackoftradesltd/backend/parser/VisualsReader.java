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
import uk.co.jackoftradesltd.backend.parser.grammars.visuals.VisualsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.visuals.VisualsLexer;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.frontend.colour.FlickerTable;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;

import java.io.IOException;
import java.util.List;

/**
 * Loads {@code lib/gamedata/visuals.txt} into its two colour-cycling tables by driving the
 * ANTLR-generated {@code VisualsLexer}/{@code VisualsGrammar}. The one file feeds two independent
 * aggregates - a {@link VisualsCycler} (named cycles, keyed group-then-name) and a
 * {@link FlickerTable} (cycles keyed by base attribute) - so this reader exposes a parse method per
 * table rather than implementing the list-shaped {@link Reader} the per-entry readers use.
 * {@code MonsterDataLoader.loadVisualTables()} drives it.
 * <p>
 * Each table is produced by its own single-aggregate assembler wrapped as a one-element list to fit
 * {@link GrammarDriver}'s list-shaped contract; the {@code parse*} convenience methods unwrap that
 * with {@code items().getFirst()}, while the {@code parse*WithResults} methods hand back the full
 * {@link ParseResult} (aggregate plus any soft errors) that {@code MonsterDataLoader} inspects
 * before installing the tables.
 * <p>
 * <strong>What it stands in for.</strong> C's {@code visuals_file_parser} in {@code ui-visuals.c}:
 * {@code visuals_file_parser_init}, which registers the four directives ({@code flicker},
 * {@code flicker-color}, {@code cycle}, {@code cycle-color}), is the grammar, and
 * {@code visuals_file_parser_run} is {@link GrammarDriver#run}. The four {@code visuals_parse_*}
 * handlers are split between the grammar (which line may follow which) and the two assemblers (what
 * each value means). {@code visuals_file_parser_finish}, which turns the parse context into the
 * cycler, is {@link VisualsCycleAssembler}; the flicker half has no finish step, because C writes
 * each {@code flicker-color:} straight into the module's flicker table as it reads it. C runs the
 * parser from {@code ui_visuals_module_init}, which {@code init.c} orders ahead of monsters and objects.
 * <p>
 * <strong>Two parses.</strong> The grammar's {@code file} rule yields both halves at once, but the
 * cycler and flicker paths currently each run their own full parse and keep only their half. That is
 * correct but does duplicate the lexing/parsing work; folding them into one parse is a possible later
 * refactor. C parses the file once.
 * <p>
 * <strong>Where it differs from C.</strong> C quits on a missing file and on any handler error. Here
 * a missing file is an {@code IOException}, and a hard parse failure is an empty result carrying the
 * errors, so the {@code parse*} convenience methods then throw {@code NoSuchElementException} from
 * their unwrap. A file with {@code flicker:} blocks and no {@code cycle:} blocks loads here with an
 * empty cycler and no errors, where C's {@code visuals_cycler_new(0)} returns NULL and
 * {@code visuals_file_parser_finish} fails the load. The grammar's differences (a colour read as its
 * first letter, an unlisted letter read as white, block order, a field after the name, indented
 * lines) are written up in {@code docs/grammar_issues.md}, and the caps C enforces (8 groups, 64
 * cycles per group, 32 steps per cycle, 512 {@code cycle:} lines, three flicker steps) in
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class VisualsReader {
    /**
     * Logger handed to {@link GrammarDriver#run}, so an I/O failure opening {@code visuals.txt} is
     * reported under this reader's name. C has no counterpart: {@code visuals_file_parser_run} calls
     * {@code parse_file_quit_not_found}, which quits on a missing file, where this reader logs and
     * throws an {@code IOException}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse visuals.txt and return just its {@link VisualsCycler}, discarding any soft errors. Use
     * {@link #parseCyclerWithResults} instead when the errors matter.
     *
     * <p>The unwrap is {@code items().getFirst()}, so a hard parse failure, which leaves the item list
     * empty, surfaces as a {@code NoSuchElementException} with no hint why. C quits in that case.
     *
     * <p>Function parseCycler coded before 260915, commented in full on 261019.
     *
     * @param filename the visuals data file to read
     * @return the assembled named-cycle table
     * @throws IOException                   if the file cannot be read
     * @throws java.util.NoSuchElementException if the file has a hard parse error
     */
    public @NotNull VisualsCycler parseCycler(@NotNull String filename) throws IOException {
        return parseCyclerWithResults(filename).items().getFirst();
    }

    /**
     * Parse visuals.txt and return just its {@link FlickerTable}, discarding any soft errors. Use
     * {@link #parseFlickerWithResults} instead when the errors matter.
     *
     * <p>The unwrap is {@code items().getFirst()}, so a hard parse failure, which leaves the item list
     * empty, surfaces as a {@code NoSuchElementException} with no hint why. C quits in that case.
     *
     * <p>Function parseFlicker coded before 260915, commented in full on 261019.
     *
     * @param filename the visuals data file to read
     * @return the assembled base-attribute flicker table
     * @throws IOException                   if the file cannot be read
     * @throws java.util.NoSuchElementException if the file has a hard parse error
     */
    public FlickerTable parseFlicker(@NotNull String filename) throws IOException {
        return parseFlickerWithResults(filename).items().getFirst();
    }

    /**
     * {@link GrammarDriver.Extractor} for the cycler half: run the {@code file} rule, fail closed on
     * any hard parse error, and return only its {@code cycle:} records (the flicker half is ignored on
     * this pass).
     *
     * <p>The {@code file} rule is the whole parse here: it stands in for C's reading of every line
     * through the {@code visuals_parse_cycle} and {@code visuals_parse_cycle_color} handlers, and the
     * grammar decides which line may follow which. {@code errorCatcher} turns any lexer or parser
     * error into a failure, where C quits on a handler error.
     *
     * <p>Function extractCycles coded before 260915, commented in full on 261019.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the shared hard-error catcher; {@link ParseErrors#throwIfAny()} fires after
     *                     the parse to fail closed before the records are used
     * @param errors       the soft-error sink (unused here; the cycler's soft errors arise in the
     *                     assembler, not this extractor)
     * @return the parsed {@code cycle:} records
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
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
     * <p>The {@code file} rule is the whole parse here: it stands in for C's reading of every line
     * through the {@code visuals_parse_flicker} and {@code visuals_parse_flicker_color} handlers, and
     * the grammar decides which line may follow which. {@code errorCatcher} turns any lexer or parser
     * error into a failure, where C quits on a handler error.
     *
     * <p>Function extractFlickers coded before 260915, commented in full on 261019.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the shared hard-error catcher; {@link ParseErrors#throwIfAny()} fires after
     *                     the parse to fail closed before the records are used
     * @param errors       the soft-error sink (unused here; the flicker assembler's soft errors
     *                     arise in the assembler, not this extractor)
     * @return the parsed {@code flicker:} records
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
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

    /**
     * Parse visuals.txt into its {@link VisualsCycler}, keeping the soft errors alongside. The
     * aggregate is the sole element of the returned result's item list; a hard parse failure yields an
     * empty list carrying the error messages instead.
     *
     * <p>This is the port of {@code visuals_file_parser_run} for the cycler half, with
     * {@link VisualsCycleAssembler} doing the work of {@code visuals_parse_context_convert} in
     * {@code visuals_file_parser_finish}: bucketing the cycles group-then-name, with a later cycle of
     * the same group and name replacing the earlier one. The assembler does not enforce C's caps of
     * 8 groups, 64 cycles per group and 32 steps per cycle, and a file with no {@code cycle:} block
     * comes back as an empty cycler with no errors where C fails the load (see
     * {@code docs/Assembler_Issues.md}). A missing file is an {@code IOException}, where C quits.
     *
     * <p>Function parseCyclerWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the visuals data file to read
     * @return the parse result wrapping the cycler and any soft errors
     * @throws IOException if the file cannot be read
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
     * <p>This is the port of {@code visuals_file_parser_run} for the flicker half, with
     * {@link VisualsFlickerAssembler} doing what {@code visuals_parse_flicker_color} does by writing
     * into C's flicker table. C keeps exactly three steps per base colour and gives every colour a
     * cycle; the assembler keeps as many steps as the file lists and only for the colours it names
     * (see {@code docs/Assembler_Issues.md}). {@link FlickerTable} has no read path yet, so a test can
     * only check that it loaded. A missing file is an {@code IOException}, where C quits.
     *
     * <p>Function parseFlickerWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the visuals data file to read
     * @return the parse result wrapping the flicker table and any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<FlickerTable> parseFlickerWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                VisualsLexer::new,
                VisualsGrammar::new,
                VisualsReader::extractFlickers,
                new VisualsFlickerAssembler(), logger);
    }
}
