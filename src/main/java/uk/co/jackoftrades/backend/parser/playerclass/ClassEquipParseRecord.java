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

/**
 * The raw, unvalidated capture of a single {@code equip:} line from {@code class.txt} — one
 * piece of a class's starting equipment. Every field is held as the verbatim file text (a
 * "lossless DTO"); interpretation into a {@link uk.co.jackoftrades.middle.player.StartItem}
 * — resolving the tval, parsing the quantities and expanding the exclusion options — is deferred
 * to {@link ClassEquipAssembler}, so a malformed value here is reported as a soft error rather
 * than aborting the parse.
 *
 * <p>Format: {@code equip:tval:sval:min:max:eopts}.
 *
 * @param tValue the item's base type name, e.g. {@code "sword"} (resolved to a {@code TValue})
 * @param sValue the item's subtype, held by name e.g. {@code "Dagger"} (resolved later by lookup)
 * @param min    the minimum quantity granted at birth, as raw text
 * @param max    the maximum quantity granted at birth, as raw text
 * @param eopts  the birth-option exclusion clause ({@code "none"}, or a space/{@code |}-separated list)
 * @param line   the 1-based source line, retained so soft errors can point back at the file
 * @author Rowan Crowther
 */
public record ClassEquipParseRecord(String tValue,
                                    String sValue,
                                    String min,
                                    String max,
                                    String eopts,
                                    int line) {
}
