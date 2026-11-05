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
 * assembled from it, in the order the assembler returned them.
 * <p>
 * There is no single C original for this record to be checked against. C's
 * {@code run_parser()} ({@code datafile.c}) quits on the first error, so a file either loads
 * whole or stops the game. This record follows the same rule: every error in a file is
 * thrown before a {@code ParseResult} is built ({@link ParseErrors#throwIfAny()} for a grammar
 * or lexer error, {@link DataFileParseException} for a record the assembler refuses), so an
 * instance only ever holds a complete, clean file. There is no error list and no partial
 * result.
 * <p>
 * Its one component could be returned as a bare {@code List<T>}; the record is kept so each
 * reader's {@code parseWithResults} signature and each loader's {@code result.items()} stay as
 * they are. Whether to fold it away is a separate decision.
 *
 * @param items  the items assembled from the file, of type T (the template class)
 * @param <T>    the object type which this parser is holding
 * @author Rowan Crowther
 *
 * <p>Record ParseResult coded on 260911, commented in full on 260915, errors component removed
 * and description rewritten on 261104 for the removal of the soft-error path.
 */
public record ParseResult<T>(List<T> items) {
}
