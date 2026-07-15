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

package uk.co.jackoftrades.backend.parser.playerclass;

import java.util.List;

/**
 * The raw capture of one spellbook definition from {@code class.txt} — the {@code book:} line plus
 * its optional {@code book-graphics:}/{@code book-properties:} lines and the {@code spell:} blocks
 * that belong to it. {@link ClassSpellBookAssembler} turns this into a
 * {@link uk.co.jackoftrades.middle.magic.MagicBook} and, mirroring C's {@code write_book_kind},
 * synthesises the backing {@link uk.co.jackoftrades.middle.objects.ObjectKind}.
 *
 * <p><b>Empty strings, never null:</b> the {@code book-graphics:} and {@code book-properties:} lines
 * are optional (a book re-referenced by a later class omits them), so the grammar seeds
 * {@link #glyph}, {@link #colour}, {@link #cost}, {@link #commonness}, {@link #min} and {@link #max}
 * to {@code ""} when the line is absent. The assembler relies on that "empty string means absent"
 * contract via its {@code isEmpty()} guards — a {@code null} here would NPE it.
 *
 * <p>Format: {@code book:oBase:locFrom:name:noOfSpells:realm}, then optionally
 * {@code book-graphics:glyph:colour} and {@code book-properties:cost:commonness:min to max}.
 *
 * @param oBase      the book's object-base name, e.g. {@code "magic book"} (resolved to a {@code TValue})
 * @param locFrom    where the book is obtained: {@code "town"} or {@code "dungeon"}
 * @param name       the book's display name, e.g. {@code "[First Spells]"}
 * @param noOfSpells the declared spell count, as raw text
 * @param realm      the magic realm name, e.g. {@code "arcane"} (resolved against the realm registry)
 * @param glyph      the display glyph from {@code book-graphics:}, or {@code ""} if absent
 * @param colour     the display colour from {@code book-graphics:}, or {@code ""} if absent
 * @param cost       the base cost from {@code book-properties:}, or {@code ""} if absent
 * @param commonness the drop commonness from {@code book-properties:}, or {@code ""} if absent
 * @param min        the minimum depth from {@code book-properties:}, or {@code ""} if absent
 * @param max        the maximum depth from {@code book-properties:}, or {@code ""} if absent
 * @param spells     the parsed spell blocks contained in this book, in file order
 * @param line       the 1-based source line, retained so soft errors can point back at the file
 * @author Rowan Crowther
 */
public record ClassSpellBookParseRecord(String oBase,
                                        String locFrom,
                                        String name,
                                        String noOfSpells,
                                        String realm,
                                        String glyph,
                                        String colour,
                                        String cost,
                                        String commonness,
                                        String min,
                                        String max,
                                        List<ClassSpellParseRecord> spells,
                                        int line) {
}
