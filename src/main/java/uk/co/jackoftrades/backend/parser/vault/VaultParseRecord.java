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

package uk.co.jackoftrades.backend.parser.vault;

import java.util.List;

/**
 * One raw vault record straight out of
 * {@link uk.co.jackoftrades.backend.parser.grammars.vault.VaultGrammar} — everything the grammar
 * could pull out of a {@code vault.txt} entry, still in its as-written text form. Numeric fields
 * ({@code rating}, {@code rows}, {@code cols}, {@code minLevel}, {@code maxLevel}) are kept as
 * {@link String} rather than parsed here, because parsing and validating them is
 * {@link VaultAssembler}'s job, not the grammar's; this record is the boundary between "the file
 * parsed" and "the file makes sense".
 *
 * <p>{@code type} is likewise left as text — the data writes room-builder names in prose
 * ({@code "Lesser vault"}, {@code "Interesting room"}, {@code "Greater vault (new)"}), and
 * resolving one to a {@code RoomType} is also the assembler's job.
 *
 * <p>The layout arrives twice over, because the two forms serve different callers. {@code map} is
 * one entry per {@code D:} line, which is what the assembler needs to check each row's length
 * against {@code cols}. {@code mapLines} is those same rows concatenated into the flat
 * {@code cols * rows} string C keeps in {@code vault.text} and indexes as
 * {@code text[y * cols + x]} ([C] src/generate.h:268). Both keep the trailing spaces that pad a
 * short row out to the declared width; trimming either would change the vault's shape.
 *
 * <p>{@code flags} is never {@code null} even when the record has no {@code flags:} directive —
 * the grammar makes it optional, and an absent line simply leaves this an empty list.
 *
 * @param name     the vault's name, from {@code name:}
 * @param type     the room-builder name, as written after {@code type:}
 * @param mapLines the layout as one flat string, the {@code D:} lines concatenated in file order
 * @param map      the layout as one string per {@code D:} line, in file order
 * @param rating   the vault's rating, as written after {@code rating:}
 * @param rows     the row count, as written after {@code rows:}
 * @param cols     the column count, as written after {@code columns:}
 * @param minLevel the minimum depth, as written after {@code min-depth:}; {@code "0"} means none
 * @param maxLevel the maximum depth, as written after {@code max-depth:}; {@code "0"} means none,
 *                 and the assembler rewrites it to the world maximum
 * @param flags    the flag names from any {@code flags:} directives, in file order; empty if there
 *                 were none
 * @param line     the source line of this record's {@code name:} directive, for error messages
 * @author Rowan Crowther
 */
public record VaultParseRecord(String name,
                               String type,
                               String mapLines,
                               List<String> map,
                               String rating,
                               String rows,
                               String cols,
                               String minLevel,
                               String maxLevel,
                               List<String> flags,
                               int line) {
}
