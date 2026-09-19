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

package uk.co.jackoftradesltd.frontend.entries;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryBase}, checked against the C original's
 * {@code parse_entry_flags} and {@code parse_entry_desc} ({@code src/ui-entry.c}).
 *
 * <p>Covers the constructor's {@code desc} validation (accepted but never
 * stored, matching {@code parse_entry_desc}'s explicit no-op) and
 * {@link UIEntryBase#getFlags()}'s flag resolution (matching
 * {@code parse_entry_flags}'s tokenize-and-validate behaviour) - in
 * particular the pipe-separated multi-flag case, which is the boundary a
 * prior version of {@code parseFlags} got wrong by splitting on the regex
 * {@code "|"} (matches every position, so a single flag like
 * {@code "TIMED_AS_AUX"} was torn into one token per character) instead of
 * the literal pipe character.
 *
 * @author Rowan Crowther
 */
class UIEntryBaseTest {

    private static UIEntryBase base(String flags, String desc) {
        return new UIEntryBase("test_base", null, CombinerName.LOGICAL_OR,
                List.of("CHAR_SCREEN1"), flags, desc);
    }

    // ---- constructor: desc is validated but never stored ---------------------------------------

    @Test
    void constructorRejectsANullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                base("TIMED_AS_AUX", null));
    }

    @Test
    void constructorAcceptsANonNullDescriptionAndDoesNotExposeIt() {
        UIEntryBase b = base("TIMED_AS_AUX", "some description");

        assertEquals("test_base", b.getName());
        // No getDesc() exists: C's parse_entry_desc (ui-entry.c) discards the desc: value too,
        // so there is nothing further to assert beyond the constructor accepting a legitimate one.
        assertFalse(b.toString().contains("some description"));
    }

    // ---- getFlags(): single flag -----------------------------------------------------------------

    @Test
    void aSingleKnownFlagIsSet() {
        UIEntryBase b = base("TIMED_AS_AUX", "d");

        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    // ---- getFlags(): pipe-separated multiple flags (the split("|") regression) -------------------

    @Test
    void pipeSeparatedFlagsSetBothBits() {
        UIEntryBase b = base("TIMED_AS_AUX|TEMPLATE_ONLY", "d");

        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void pipeSeparatedFlagsWithSurroundingSpacesAreTrimmed() {
        UIEntryBase b = base(" TIMED_AS_AUX | TEMPLATE_ONLY ", "d");

        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    // ---- getFlags(): rejection mirrors C's PARSE_ERROR_INVALID_FLAG -------------------------------

    @Test
    void anUnrecognisedFlagTokenIsRejected() {
        // Matches parse_entry_flags (ui-entry.c) falling through entry_flags[] and returning
        // PARSE_ERROR_INVALID_FLAG for a token that names no known flag.
        assertThrows(IllegalArgumentException.class, () -> base("NOT_A_REAL_FLAG", "d"));
    }

    @Test
    void oneBadTokenAmongGoodOnesStillRejectsTheWhole() {
        // C's parse_entry_flags (ui-entry.c) checks each strtok'd piece against entry_flags[] in
        // order and bails out on the first miss, even if an earlier piece matched - it never
        // partially applies a flags: value. This mirrors that: TIMED_AS_AUX matches first, but the
        // second token still fails the whole call.
        assertThrows(IllegalArgumentException.class, () -> base("TIMED_AS_AUX|NOT_A_REAL_FLAG", "d"));
    }

    // ---- name/renderer/combine/categories pass through unchanged ---------------------------------

    @Test
    void gettersReturnTheConstructorArguments() {
        UIEntryRenderer renderer = null;
        UIEntryBase b = new UIEntryBase("t", renderer, CombinerName.ADD,
                List.of("CHAR_SCREEN1", "abilities"), "TIMED_AS_AUX", "d");

        assertEquals("t", b.getName());
        assertEquals(renderer, b.getRenderer());
        assertEquals(CombinerName.ADD, b.getCombine());
        assertEquals(List.of("CHAR_SCREEN1", "abilities"), b.getCategories());
    }
}
