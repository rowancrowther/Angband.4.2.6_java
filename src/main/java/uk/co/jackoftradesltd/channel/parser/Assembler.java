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
 * Functional interface for the final step of a parser boundary: it takes the raw
 * parse-records a {@link Reader} has accumulated from a {@code lib/gamedata} file,
 * plus any error messages collected along the way, and combines them into the
 * finished result type the rest of the game uses. Implementations decide how the
 * records map onto {@code T} (for example, indexing a list of records into an
 * array keyed by id); this interface only fixes the shape of that step so parser
 * boundaries can be driven generically.
 *
 * @param <R> parse-record type produced by the parser
 * @param <T> assembled result type consumed by the rest of the game
 * @author Rowan Crowther
 *
 * <p>coded on 2026-09-11 / commented in full on 2026-09-15
 */
public interface Assembler<R, T> {
    /**
     * Combines the parse-records read from a file into the assembled result.
     *
     * @param records list of parse-records, one per parsed entry, in file order
     * @param errors  error messages accumulated during parsing; implementations
     *                are not required to consume these, but should not discard
     *                any that describe a problem with the given {@code records}
     * @return the assembled result of type {@code T}
     *
     * <p>coded on 2026-09-11 / commented in full on 2026-09-15
     */
    T assemble(@NotNull List<R> records, @NotNull List<String> errors);
}
