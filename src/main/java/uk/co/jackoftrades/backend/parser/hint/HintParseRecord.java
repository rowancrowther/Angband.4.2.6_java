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

package uk.co.jackoftrades.backend.parser.hint;

/**
 * The raw form of a single {@code hints.txt} record - one {@code H:} line - produced by
 * {@code HintGrammar} and consumed by {@link HintAssembler}.
 *
 * <p>There is nothing to interpret: a hint is already just text, so the assembler copies it straight
 * into a {@link uk.co.jackoftrades.middle.game.Hint}. The {@code line} is carried only to keep the
 * DTO shape consistent with the other readers (and to locate a record should a future check need to
 * report against it).
 *
 * @param hint the advice text as written after {@code H:}
 * @param line the source line this record was parsed from
 * @author Rowan Crowther
 */
public record HintParseRecord(String hint,
                              int line) {
}
