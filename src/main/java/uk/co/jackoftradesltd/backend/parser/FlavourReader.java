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
 * through the shared {@link GrammarDriver}. Callers wanting the collected soft
 * errors as well as the items use {@link #parseWithResults}; {@link #parse}
 * returns just the items.
 *
 * <p>Stands in for three C functions in {@code init.c}. {@code init_parse_flavor} registers
 * {@code kind}, {@code flavor} and {@code fixed}; here that directive table is the grammar.
 * {@code run_parse_flavor} is {@code parse_file_quit_not_found}, which quits when the file is
 * missing; here that is an {@link IOException}, and the quit is left to
 * {@code MiscDataLoader.loadFlavours}. {@code finish_parse_flavor} assigns the parsed list to the
 * global {@code flavors}; here the loader hands {@link #parse}'s items to {@code MiscRegistry}.
 * The per-line work of {@code parse_flavor_kind} and {@code parse_flavor_flavor} (the tval, the
 * colour, the sval) belongs to the assemblers, not to this class.
 *
 * <p>Where the port differs from C, none of it visible on the shipped file:
 * <ul>
 *   <li>The file starts with a {@code record-count:} header, which upstream's {@code flavor.txt}
 *       does not have. It counts the {@code kind:} blocks (eight), not the 302 flavours.</li>
 *   <li>C keeps one flat list with each flavour carrying its tval and glyph. Here a
 *       {@code kind:} line and the lines under it make one {@link FlavourKind}, so two
 *       {@code kind:} lines for one tval are two blocks. {@link FlavourKind} stores a block last
 *       file line first, as C's head-insertion leaves its list.</li>
 *   <li>The grammar is stricter than {@code parser_parse}: a {@code kind:} with no flavours, a
 *       flavour before any {@code kind:}, a colon in a description, a trailing colon after the
 *       colour, an indented line and text after the glyph are hard errors where C accepts them,
 *       and an empty file is one too. They are written up in {@code docs/grammar_issues.md}.</li>
 *   <li>A hard error gives an empty result with the syntax errors, where C quits; the soft errors
 *       (an unknown tval or colour, a bad index, an unresolvable {@code fixed:} sval) drop a record or a
 *       block. Those are in {@code docs/Assembler_Issues.md}.</li>
 * </ul>
 *
 * <p>Class FlavourReader coded before 260915, commented in full on 261018.
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
     * Parses {@code filename} and returns just the assembled flavours, discarding the
     * soft-error channel (use {@link #parseWithResults} to inspect errors). This is the
     * {@link Reader} entry point {@code MiscDataLoader.loadFlavours} does not use; it calls
     * {@link #parseWithResults} so that it can report the errors.
     *
     * <p>The blocks come back in file order, each holding its flavours last file line first.
     * A hard syntax error gives an empty list, so a caller of this method cannot tell it from an
     * empty file.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the data file to read
     * @return the flavours read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<FlavourKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Drives the parse and pulls the raw kind blocks off the tree: runs the
     * {@code file} rule, surfaces any syntax errors, then soft-checks the
     * declared record-count against the number of kind blocks parsed.
     *
     * <p>{@link ParseErrors#throwIfAny()} runs before the count check, so a syntax error ends the
     * parse with no items and no record-count message. The count is of {@code kind:} blocks, the
     * list {@code file} returns, not of the flavours inside them. C has no header to check.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the grammar to run
     * @param errorCatcher collects syntax errors raised during the parse
     * @param errors       the soft-error sink for the record-count check
     * @return the raw {@link FlavourKindParseRecord}s for the assembler
     */
    private static List<FlavourKindParseRecord> extract(
            @NotNull FlavourGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        FlavourGrammar.FileContext output = parser.file();
        List<FlavourKindParseRecord> records = output.sections;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parses the file and returns both the assembled flavour kinds and any soft
     * errors collected along the way (unknown tval/colour, unresolvable sval,
     * record-count mismatch).
     *
     * <p>This is the port of {@code run_parse_flavor}: a file that cannot be read is an
     * {@link IOException} where C quits. The {@link FlavourKindAssembler} needs the object-kind
     * table, artifact-synthesised kinds included, to resolve a {@code fixed:} sval, so the caller
     * loads this after artifacts.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the file to read
     * @return the parse result — items plus collected errors
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
