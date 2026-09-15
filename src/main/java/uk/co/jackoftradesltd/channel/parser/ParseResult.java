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

import java.util.List;

/**
 * A generic holder for the outcome of parsing one {@code lib/gamedata} file: the items
 * successfully assembled from it, plus any soft errors gathered along the way.
 * <p>
 * There is no single C original for this record to be checked against. The C parser
 * (see {@code parser.h}, {@code struct parser_state}) reports one {@code enum parser_error}
 * at a time and fails fast; this Java port instead lets a reader keep parsing past a bad line
 * and collect every error into {@link #errors()} as a list of strings, so a caller such as
 * {@link GrammarDriver} can gate on {@link #hasErrors()} after the whole file has been read
 * rather than aborting on the first bad line.
 *
 * @param items  the items successfully parsed from the file, of type T (the template class)
 * @param errors the soft errors gathered while parsing the file, as human-readable strings
 * @param <T>    the object type which this parser is holding
 * @author Rowan Crowther
 *
 * <p>Record ParseResult coded on 260911, commented in full on 260915.
 */
public record ParseResult<T>(List<T> items, List<String> errors) {
    /**
     * Reports whether the parse that produced this result gathered any soft errors.
     *
     * @return {@code true} if {@link #errors()} is non-empty, {@code false} otherwise
     *
     * <p>Function hasErrors coded on 260911, commented in full on 260915.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
