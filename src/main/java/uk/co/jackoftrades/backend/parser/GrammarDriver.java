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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The single shared skeleton every list-shaped data-file reader delegates to.
 * <p>
 * Each {@code lib/gamedata} reader used to hand-copy the same ~9-step ritual:
 * open a {@link CharStream}, wire a lexer to a {@link CommonTokenStream} to a
 * parser, install the shared {@link ParseErrors} listener, pull the parsed
 * records out, hand them to an {@link Assembler}, and wrap the result in a
 * {@link ParseResult}. Only four things ever varied between readers, so those
 * become parameters and the ritual lives here exactly once:
 * <pre>
 *   X.txt -&gt; XLexer/XGrammar -&gt; ParseErrors -&gt; extract records
 *         -&gt; Assembler -&gt; ParseResult
 * </pre>
 * <p>
 * <strong>What the driver owns</strong> (the invariant part): the ANTLR
 * plumbing, the error-listener install, the assemble call, the
 * {@link ParseResult} wrapping, and the IO / cancellation handling.
 * <strong>What the caller supplies</strong> (the four knobs): the lexer
 * constructor, the parser constructor, an {@link Extractor} (the one
 * grammar-specific step), and the grammar's {@link Assembler}.
 * <p>
 * <strong>Scope:</strong> this drives <em>list-shaped</em> readers only - those
 * whose assembler yields a {@code List<T>} (Projection, UIEntryRenderer, World,
 * ObjectBase, and the rest). It deliberately does <em>not</em> fit
 * {@code GameConstants}, whose assembler produces a single aggregate
 * {@code GameConstantsData} rather than a list; that reader keeps its own
 * result type. The {@code Assembler<R, List<T>>} bound on {@link #run} enforces
 * this at compile time.
 *
 * @author Rowan Crowther
 */
public final class GrammarDriver {
    /**
     * The one step {@link #run} cannot perform itself: turn a parsed file into
     * a list of parse-records. An implementation runs the parser's entry rule
     * ({@code parser.file()}), fires {@link ParseErrors#throwIfAny()}, validates
     * the record-count header (see {@link #checkRecordCount}), and maps each raw
     * row to a grammar-specific DTO of type {@code R}.
     * <p>
     * It receives the live {@link ParseErrors} handle - not just the parser -
     * for two reasons the driver cannot work around:
     * <ul>
     *   <li><strong>Ordering:</strong> {@code throwIfAny()} must fire
     *       <em>after</em> the parse but <em>before</em> the row-mapping, so the
     *       fail-closed check has to live inside the extractor, not in
     *       {@link #run}.</li>
     *   <li><strong>No common supertype:</strong> each grammar's
     *       {@code FileContext} names its record-list and count fields
     *       differently (e.g. {@code .projections}/{@code .records} vs
     *       {@code .renderers}/{@code .record}), so only grammar-specific code
     *       can reach them.</li>
     * </ul>
     * Soft (recoverable) problems are appended to {@code errors}; hard
     * grammar/lexer problems surface through the {@code errorCatcher}.
     *
     * @param <P> the concrete generated parser type (e.g. {@code ProjectionGrammar})
     * @param <R> the per-grammar parse-record (DTO) type
     * @author Rowan Crowther
     */
    @FunctionalInterface
    public interface Extractor<P extends Parser, R> {
        @NotNull
        List<R> extract(@NotNull P parser,
                        @NotNull ParseErrors errorCatcher,
                        @NotNull List<String> errors);
    }

    /**
     * Static only - no instances
     *
     * @author Rowan Crowther
     */
    private GrammarDriver() {
        // No instances
    }

    /**
     * Drive one data file through the full parse-and-assemble pipeline and
     * return its results plus any soft errors gathered along the way.
     * <p>
     * The fixed skeleton: open the file, build the lexer/parser from the two
     * constructor references, install the shared {@link ParseErrors} listener,
     * let the {@code extractor} produce the parse-records, hand those to the
     * {@code assembler}, and wrap the assembled {@code List<T>} in a
     * {@link ParseResult}.
     * <p>
     * <strong>Error handling has two channels.</strong> Hard grammar/lexer
     * errors are collected by {@link ParseErrors} and rethrown as a
     * {@link ParseCancellationException} by the extractor's
     * {@link ParseErrors#throwIfAny()}; that is caught here and returned as an
     * <em>empty</em> {@link ParseResult} carrying the collected error messages.
     * Soft errors (bad record-count, unresolvable fields) accumulate in the
     * {@code errors} list and ride out alongside whatever records did assemble,
     * preserving the partial-results contract. An {@link IOException} is logged
     * and rethrown rather than swallowed.
     * <p>
     * The {@code Assembler<R, List<T>>} bound is what restricts this to
     * list-shaped readers and keeps the assembler's {@code List<T>} result in
     * step with the returned {@code ParseResult<T>}.
     *
     * @param filename  the data file to parse
     * @param newLexer  the generated lexer's constructor, e.g. {@code XLexer::new}
     * @param newParser the generated parser's constructor, e.g. {@code XGrammar::new}
     * @param extractor the grammar-specific record {@link Extractor}
     * @param assembler the grammar's {@link Assembler}, producing a {@code List<T>}
     * @param logger    the calling reader's logger, so an IO failure is logged
     *                  under that reader's name
     * @param <L>       the generated lexer type
     * @param <P>       the generated parser type
     * @param <R>       the per-grammar parse-record (DTO) type
     * @param <T>       the assembled element type held by the {@link ParseResult}
     * @return a {@link ParseResult} of the assembled items and any soft errors;
     * empty items (with errors) if the parse was cancelled
     * @throws IOException if the file cannot be read
     */
    public static <L extends Lexer, P extends Parser, R, T> ParseResult<T> run(
            @NotNull String filename,
            @NotNull Function<CharStream, L> newLexer,
            @NotNull Function<CommonTokenStream, P> newParser,
            @NotNull Extractor<P, R> extractor,
            @NotNull Assembler<R, List<T>> assembler,
            @NotNull Logger logger) throws IOException {

        ParseErrors errorCatcher = null;
        List<String> errors = new ArrayList<>();

        try {
            CharStream input = CharStreams.fromFileName(filename);
            L lexer = newLexer.apply(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            P parser = newParser.apply(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);

            List<R> records = extractor.extract(parser, errorCatcher, errors);
            List<T> items = assembler.assemble(records, errors);
            return new ParseResult<>(items, errors);
        } catch (IOException e) {
            logger.error("Error reading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            return new ParseResult<>(List.of(), errorCatcher != null ? errorCatcher.getErrors() : List.of());
        }
    }

    /**
     * Validate the file's declared {@code record-count} header against the
     * number of records actually parsed, recording a soft error on any
     * discrepancy. A mismatch (or a non-numeric header) is never fatal - the
     * records that did parse still load, per the partial-results contract - so
     * this reports into {@code errors} rather than throwing.
     * <p>
     * Its purpose is the side effect on {@code errors}; there is no return
     * value to consume (the parsed count is used only for the comparison here).
     *
     * @param declared the header's declared-count text
     * @param actual   the number of records actually parsed
     * @param errors   the soft-error sink, mutated in place
     * @author Rowan Crowther
     */
    public static void checkRecordCount(@NotNull String declared,
                                        int actual,
                                        @NotNull List<String> errors) {
        int count = -1;
        try {
            count = Integer.parseInt(declared);
        } catch (NumberFormatException e) {
            errors.add("Invalid number format on declared record count: " + declared);
        }

        if (count != -1 && count != actual) {
            errors.add("record-count header declares " + count + " records, but file contains " + actual);
        }
    }
}
