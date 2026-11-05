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
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindAssembler;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.flavour.FlavourGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.flavour.FlavourLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code flavor.txt} into a list of {@link FlavourKind}s, wiring the
 * {@code FlavourLexer}/{@code FlavourGrammar} to the {@link FlavourKindAssembler}
 * through the shared {@link GrammarDriver}. {@link #parseWithResults} returns the
 * items wrapped in a {@link ParseResult}; {@link #parse} returns just the items.
 *
 * <p>Stands in for three C functions in {@code init.c}. {@code init_parse_flavor} registers
 * {@code kind}, {@code flavor} and {@code fixed}; here that directive table is the grammar.
 * {@code run_parse_flavor} is {@code parse_file_quit_not_found}, which quits when the file is
 * missing; here that is an {@link IOException}, and the quit is left to
 * {@code MiscDataLoader.loadFlavours}. {@code finish_parse_flavor} assigns the parsed list to the
 * global {@code flavors}; here the loader hands {@link #parseWithResults}'s items to
 * {@code MiscRegistry}.
 * The per-line work of {@code parse_flavor_kind} and {@code parse_flavor_flavor} (the tval, the
 * colour, the sval) belongs to the assemblers, not to this class.
 *
 * <p>Where the port differs from C, none of it visible on the shipped file:
 * <ul>
 *   <li>C keeps one flat list with each flavour carrying its tval and glyph. Here a
 *       {@code kind:} line and the lines under it make one {@link FlavourKind}, so two
 *       {@code kind:} lines for one tval are two blocks. {@link FlavourKind} stores a block last
 *       file line first, as C's head-insertion leaves its list.</li>
 *   <li>The grammar is stricter than {@code parser_parse}: a {@code kind:} with no flavours, a
 *       flavour before any {@code kind:}, a colon in a description, a trailing colon after the
 *       colour, an indented line and text after the glyph are hard errors where C accepts them,
 *       and an empty file is one too. They are written up in {@code docs/grammar_issues.md}.</li>
 * </ul>
 *
 * <p>Every error is fatal, as C quits on the first: a syntax error is thrown from
 * {@link #extract} as a {@code ParseCancellationException}, and an unknown tval, a glyph that is
 * not one character or a bad index is thrown by the assemblers as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here, and
 * nothing is returned. An unknown colour is not an error; it becomes white, as in C. The
 * assembler differences are in {@code docs/Assembler_Issues.md}.
 *
 * <p>Class FlavourReader coded before 260915, commented in full on 261018, updated on 261105 for
 * the removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class FlavourReader implements Reader<FlavourKind> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which reports a file that cannot be read through
     * it before rethrowing the {@link IOException}. The reader logs nothing itself.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled flavours, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. This is the {@link Reader} entry
     * point; {@code MiscDataLoader.loadFlavours} calls {@link #parseWithResults} instead.
     *
     * <p>The blocks come back in file order, each holding its flavours last file line first. Any
     * error throws exactly as it does in {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every flavour kind in the file, in file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<FlavourKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Drives the parse and pulls the raw kind blocks off the tree: runs the
     * {@code file} rule and surfaces any syntax errors.
     *
     * <p>{@link ParseErrors#throwIfAny()} runs straight after the parse, so a syntax error throws
     * a {@code ParseCancellationException} before any block is used. The list returned holds the
     * {@code kind:} blocks, not the flavours inside them.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the grammar to run
     * @param errorCatcher collects syntax errors raised during the parse
     * @return the raw {@link FlavourKindParseRecord}s for the assembler
     */
    private static List<FlavourKindParseRecord> extract(
            @NotNull FlavourGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        FlavourGrammar.FileContext output = parser.file();
        List<FlavourKindParseRecord> records = output.sections;
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Parses the file and returns the assembled flavour kinds wrapped in a {@link ParseResult}.
     *
     * <p>This is the port of {@code run_parse_flavor}: a file that cannot be read is an
     * {@link IOException} where C quits. A syntax error throws a
     * {@code ParseCancellationException} and a record the assemblers refuse throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, so no partial result is
     * returned. The {@link FlavourKindAssembler} needs the object-kind table, artifact-synthesised
     * kinds included, to resolve a {@code fixed:} sval, so the caller loads this after artifacts.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the file to read
     * @return every flavour kind in the file, in file order
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<FlavourKind> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                FlavourLexer::new,
                FlavourGrammar::new,
                FlavourReader::extract,
                new FlavourKindAssembler(), logger);
    }
}
