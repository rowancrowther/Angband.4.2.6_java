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

package uk.co.jackoftradesltd.channel.parser;

import org.antlr.v4.runtime.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
 * {@link ParseResult} wrapping, and the logging of an IO failure. It catches
 * nothing else: a grammar or assembler error passes straight through to the
 * loader, so a bad file stops the load, as C's {@code run_parser()} quits.
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
 *
 * <p>Class GrammarDriver coded on 260911, commented in full on 260915, updated on 261104 for the
 * removal of the soft-error path.
 */
public final class GrammarDriver {
    private static final Logger logger = LogManager.getLogger(GrammarDriver.class);
    
    /**
     * Private, no-op constructor. {@code GrammarDriver} is a static-only
     * utility class - every reader drives its file through {@link #run}
     * rather than holding an instance, so construction is blocked entirely.
     *
     * <p>Constructor GrammarDriver coded on 260911, commented in full on 260915.
     */
    private GrammarDriver() {
        // No instances
    }

    /**
     * Drive one data file through the full parse-and-assemble pipeline and
     * return the assembled items.
     * <p>
     * The fixed skeleton: open the file, build the lexer/parser from the two
     * constructor references, install the shared {@link ParseErrors} listener,
     * let the {@code extractor} produce the parse-records, hand those to the
     * {@code assembler}, and wrap the assembled {@code List<T>} in a
     * {@link ParseResult}.
     * <p>
     * <strong>Every error is fatal</strong>, as in C, where {@code run_parser()}
     * ({@code datafile.c}) quits on the first one. There are three ways out
     * other than a clean return:
     * <ul>
     *   <li>a grammar or lexer error is collected by {@link ParseErrors} and
     *       thrown as a
     *       {@code org.antlr.v4.runtime.misc.ParseCancellationException} by the
     *       extractor's call to {@link ParseErrors#throwIfAny()};</li>
     *   <li>a record the assembler refuses is thrown as a
     *       {@link DataFileParseException};</li>
     *   <li>an {@link IOException} is logged under the caller's logger and
     *       rethrown.</li>
     * </ul>
     * The first two are unchecked and are not caught here, so they reach the
     * loader unlogged and unwrapped. No {@link ParseResult} is built for a file
     * that fails, so a caller never sees a partial list.
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
     * @return a {@link ParseResult} of every item assembled from the file
     * @throws IOException if the file cannot be read
     *
     * <p>Method run coded on 260911, commented in full on 260915, updated on
     * 261104 for the removal of the soft-error path.
     */
    public static <L extends Lexer, P extends Parser, R, T> ParseResult<T> run(
            @NotNull String filename,
            @NotNull Function<CharStream, L> newLexer,
            @NotNull Function<CommonTokenStream, P> newParser,
            @NotNull Extractor<P, R> extractor,
            @NotNull Assembler<R, List<T>> assembler,
            @NotNull Logger logger) throws IOException {

        ParseErrors errorCatcher = null;

        try {
            CharStream input = CharStreams.fromFileName(filename);
            L lexer = newLexer.apply(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            P parser = newParser.apply(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);

            List<R> records = extractor.extract(parser, errorCatcher);
            List<T> items = assembler.assemble(records, filename);
            return new ParseResult<>(items);
        } catch (IOException e) {
            logger.error("Error reading file {}", filename, e);
            throw e;
        }
    }

    /**
     * The one step {@link #run} cannot perform itself: turn a parsed file into
     * a list of parse-records. An implementation runs the parser's entry rule
     * ({@code parser.file()}), fires {@link ParseErrors#throwIfAny()}, and maps each raw
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
     *       {@code FileContext} names its record-list field differently, so
     *       only grammar-specific code can reach it.</li>
     * </ul>
     * There is no error list to append to: a grammar or lexer problem surfaces
     * through the {@code errorCatcher}, and anything later is the assembler's
     * to throw.
     *
     * @param <P> the concrete generated parser type (e.g. {@code ProjectionGrammar})
     * @param <R> the per-grammar parse-record (DTO) type
     * @author Rowan Crowther
     *
     * <p>Interface Extractor coded on 260911, commented in full on 260915,
     * updated on 261104 for the removal of the soft-error path and the
     * {@code record-count:} header.
     */
    @FunctionalInterface
    public interface Extractor<P extends Parser, R> {
        /**
         * Runs the parser's entry rule, fail-closes on any hard grammar/lexer
         * error via {@code errorCatcher.throwIfAny()}, then maps each parsed
         * row into a grammar-specific record of type {@code R}. Nothing is
         * collected for later: a hard error throws here, and the rows are only
         * mapped once the parse is clean.
         *
         * @param parser       the constructed grammar parser, not yet run
         * @param errorCatcher the installed {@link ParseErrors} listener; call
         *                     {@link ParseErrors#throwIfAny()} after parsing
         *                     and before mapping rows, so hard errors fail
         *                     closed
         * @return the parsed records, one per row, in file order
         * @throws IOException if the underlying read fails
         *
         *                     <p>Method extract coded on 260911, commented in full on 260915,
         *                     updated on 261104 for the removal of the soft-error path.
         */
        @NotNull
        List<R> extract(@NotNull P parser,
                        @NotNull ParseErrors errorCatcher) throws IOException;
    }
}
