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

package uk.co.jackoftrades.backend.parser.realm;

/**
 * The flat, all-strings capture of one realm record from {@code realm.txt}, produced by
 * {@link uk.co.jackoftrades.backend.parser.grammars.realm.RealmGrammar} before any validation. It is
 * the hand-off from the ANTLR parse to
 * {@link uk.co.jackoftrades.backend.parser.realm.RealmAssembler}, which turns the raw text into a
 * typed {@link uk.co.jackoftrades.middle.magic.MagicRealm} (resolving the stat name and book-noun).
 * Keeping the values as strings here lets a malformed entry be reported against its {@link #line}
 * rather than throwing inside the parser.
 *
 * @param name      the {@code name:} value — the realm's display name (e.g. {@code "arcane"})
 * @param stat      the {@code stat:} value — the primary casting stat's name (e.g. {@code "INT"})
 * @param verb      the {@code verb:} value — the casting verb (e.g. {@code "cast"})
 * @param spellNoun the {@code spell-noun:} value — what a casting is called (e.g. {@code "spell"})
 * @param bookNoun  the {@code book-noun:} value — the spellbook's item-type name (e.g. {@code "magic book"})
 * @param line      the 1-based source line of this record's {@code name:} directive, used in error messages
 * @author Rowan Crowther
 */
public record RealmParseRecord(String name,
                               String stat,
                               String verb,
                               String spellNoun,
                               String bookNoun,
                               int line) {
}
