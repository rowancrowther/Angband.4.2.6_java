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

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <p><code>Assembler</code> interface</p>
 * <p>
 * The final step of a parser boundary: it takes the raw parse-records a
 * {@link Reader} has pulled out of one {@code lib/gamedata} file and combines them
 * into the finished result type the rest of the game uses. Implementations decide
 * how the records map onto {@code T} (for example, resolving names to registry
 * entries, or indexing a list of records by id); this interface only fixes the
 * shape of that step so {@link GrammarDriver#run} can drive every list-shaped
 * reader the same way.
 * <p>
 * The C counterpart is the {@code finish} hook of {@code struct file_parser} in
 * {@code datafile.h}, which {@code datafile.c}, function {@code run_parser()}, calls
 * once the whole file has parsed without error. In C the hook takes the
 * {@code struct parser} (whose private data holds the records) and returns an
 * {@code errr}; any non-zero value makes {@code run_parser()} print
 * "Parser finish error in &lt;file&gt;" and quit the game. The Java shape is the same
 * step with the records handed over directly and the result returned as a value,
 * since the C hook writes into a global such as {@code k_info} instead.
 * <p>
 * Since the soft errors were turned fatal there is no error list to carry: an
 * assembly check that fails throws instead, which is the Java stand-in for C's
 * non-zero {@code finish} return and quit. Implementations name {@code filename}
 * in the message of that exception.
 * <p>
 * Interface Assembler coded on 260911 / commented in full on 261104.
 *
 * @param <R> parse-record type produced by the parser
 * @param <T> assembled result type consumed by the rest of the game
 * @author Rowan Crowther
 */
public interface Assembler<R, T> {
    /**
     * Combines the parse-records read from one file into the assembled result.
     * <p>
     * This is the Java form of the {@code finish} hook that {@code datafile.c},
     * function {@code run_parser()}, calls after a clean parse. {@link GrammarDriver#run}
     * calls it once per file, after the extractor has returned, and wraps the
     * result in a {@link ParseResult}. C's {@code finish} returns a non-zero
     * {@code errr} to stop the game; an implementation throws instead and does not
     * return a partial result.
     * <p>
     * Method assemble coded on 260911 / commented in full on 261104.
     *
     * @param records  list of parse-records, one per parsed entry, in file order
     * @param filename the file the records were read from, for implementations that name it in
     *                 the message of a failure that stops the load
     * @return the assembled result of type {@code T}
     */
    T assemble(@NotNull List<R> records, String filename) ;
}
