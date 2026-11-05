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
 * with {@code items().getFirst()}, while the {@code parse*WithResults} methods hand back the
 * {@link ParseResult} itself, which {@code MonsterDataLoader} unwraps the same way before
 * installing the tables.
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
 * <strong>Errors.</strong> C quits on a missing file and on any handler error, and so does this
 * reader: a missing file is an {@code IOException}, a hard parse failure is thrown from the
 * extractors as a {@code ParseCancellationException}, and a cycle the assembler refuses is thrown
 * as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. None is caught here, so
 * no result is returned for a file that fails. That includes a file with {@code flicker:} blocks
 * and no {@code cycle:} blocks, where C's {@code visuals_cycler_new(0)} returns NULL and
 * {@code visuals_file_parser_finish} fails the load.
 * <p>
 * <strong>Where it differs from C.</strong> The grammar's differences (a colour read as its first
 * letter, an unlisted letter read as white, block order, a field after the name, indented lines)
 * are written up in {@code docs/grammar_issues.md}. Of the caps C enforces, 32 steps per cycle and
 * 512 {@code cycle:} lines are enforced here too; 8 groups, 64 cycles per group and three flicker
 * steps are not, as recorded in {@code docs/Assembler_Issues.md}.
 *
 * <p>Class commented in full on 261019, updated on 261105 for the removal of the soft-error path
 * and the cycle caps the assembler now enforces.
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
     * Parse visuals.txt and return just its {@link VisualsCycler}, unwrapped with
     * {@code items().getFirst()} from the {@link ParseResult} that {@link #parseCyclerWithResults}
     * gives.
     *
     * <p>A hard parse failure or a refused cycle throws out of {@link #parseCyclerWithResults}
     * before the unwrap is reached, as C quits.
     *
     * <p>Function parseCycler coded before 260915, commented in full on 261019, updated on 261105
     * for the removal of the soft-error path.
     *
     * @param filename the visuals data file to read
     * @return the assembled named-cycle table
     * @throws IOException if the file cannot be read
     */
    public @NotNull VisualsCycler parseCycler(@NotNull String filename) throws IOException {
        return parseCyclerWithResults(filename).items().getFirst();
    }

    /**
     * Parse visuals.txt and return just its {@link FlickerTable}, unwrapped with
     * {@code items().getFirst()} from the {@link ParseResult} that {@link #parseFlickerWithResults}
     * gives.
     *
     * <p>A hard parse failure throws out of {@link #parseFlickerWithResults} before the unwrap is
     * reached, as C quits.
     *
     * <p>Function parseFlicker coded before 260915, commented in full on 261019, updated on 261105
     * for the removal of the soft-error path.
     *
     * @param filename the visuals data file to read
     * @return the assembled base-attribute flicker table
     * @throws IOException if the file cannot be read
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
     * @return the parsed {@code cycle:} records
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
     */
    private static List<VisualsCycleParseRecord> extractCycles(
            @NotNull VisualsGrammar parser,
            @NotNull ParseErrors errorCatcher) throws IOException {
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
     * @return the parsed {@code flicker:} records
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
     */
    private static List<VisualsFlickerParseRecord> extractFlickers(
            @NotNull VisualsGrammar parser,
            @NotNull ParseErrors errorCatcher) throws IOException {
        VisualsGrammar.FileContext output = parser.file();
        List<VisualsFlickerParseRecord> result = output.flickers;
        errorCatcher.throwIfAny();

        return result;
    }

    /**
     * Parse visuals.txt into its {@link VisualsCycler}, the sole element of the returned result's
     * item list. A hard parse failure throws a {@code ParseCancellationException} instead, and no
     * result is returned.
     *
     * <p>This is the port of {@code visuals_file_parser_run} for the cycler half, with
     * {@link VisualsCycleAssembler} doing the work of {@code visuals_parse_context_convert} in
     * {@code visuals_file_parser_finish}: bucketing the cycles group-then-name, with a later cycle of
     * the same group and name replacing the earlier one. The assembler throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException} for more than 512
     * {@code cycle:} lines, a cycle of more than 32 steps, or a file with no {@code cycle:} block,
     * where C fails the load; it does not enforce C's caps of 8 groups and 64 cycles per group (see
     * {@code docs/Assembler_Issues.md}). A missing file is an {@code IOException}, where C quits.
     *
     * <p>Function parseCyclerWithResults coded before 260915, commented in full on 261019, updated
     * on 261105 for the removal of the soft-error path.
     *
     * @param filename the visuals data file to read
     * @return the parse result wrapping the cycler
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
     * Parse visuals.txt into its {@link FlickerTable}, the sole element of the returned result's
     * item list. A hard parse failure throws a {@code ParseCancellationException} instead, and no
     * result is returned.
     *
     * <p>This is the port of {@code visuals_file_parser_run} for the flicker half, with
     * {@link VisualsFlickerAssembler} doing what {@code visuals_parse_flicker_color} does by writing
     * into C's flicker table. C keeps exactly three steps per base colour and gives every colour a
     * cycle; the assembler keeps as many steps as the file lists and only for the colours it names
     * (see {@code docs/Assembler_Issues.md}). {@link FlickerTable} has no read path yet, so a test can
     * only check that it loaded. A missing file is an {@code IOException}, where C quits.
     *
     * <p>Function parseFlickerWithResults coded before 260915, commented in full on 261019, updated
     * on 261105 for the removal of the soft-error path.
     *
     * @param filename the visuals data file to read
     * @return the parse result wrapping the flicker table
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
