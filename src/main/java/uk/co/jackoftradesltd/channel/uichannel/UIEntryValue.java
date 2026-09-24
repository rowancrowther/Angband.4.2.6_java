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

package uk.co.jackoftradesltd.channel.uichannel;

/**
 * A UI entry's computed display value, the Java form of the {@code (val, auxval)} out-parameter pair
 * C's {@code compute_ui_entry_values_for_object} and {@code compute_ui_entry_values_for_player}
 * ({@code [C] ui-entry.c:672, 875}) each write into, bundled as one return value instead of two
 * pointers. {@link #knownRune} carries what a separate call to C's {@code is_ui_entry_for_known_rune}
 * ({@code [C] ui-entry.c:554}) would report; it is not set by either compute method and is
 * {@code false} on every value they currently return, so a caller wanting known-rune status still
 * calls {@code UIEntryValueRegistry.isKnownRune} directly rather than reading it off this record.
 *
 * <p>Record UIEntryValue coded before 260924, commented in full on 260924.
 *
 * @param val       the combined value across the entry's non-auxiliary bound properties, or one of
 *                  {@code Combiner}'s sentinel values ({@code UI_ENTRY_VALUE_NOT_PRESENT},
 *                  {@code UI_ENTRY_UNKNOWN_VALUE})
 * @param auxVal    the combined value across the entry's auxiliary bound properties, under the same
 *                  sentinel rules as {@link #val}
 * @param knownRune whether the entry counts as a known rune; currently always {@code false} when
 *                  produced by {@code UIEntryValueRegistry}'s compute methods
 * @author Rowan Crowther
 */
public record UIEntryValue(int val,
                           int auxVal,
                           boolean knownRune) {
}
