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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryBase}, checked against the C original's
 * {@code parse_entry_desc} ({@code src/ui-entry.c}).
 *
 * <p>Covers the constructor's {@code desc} validation (accepted but never
 * stored, matching {@code parse_entry_desc}'s explicit no-op) and
 * {@link UIEntryBase#getFlags()}'s passthrough of an already-resolved
 * {@link Flag}. The {@code flags:} tokenizing/validation this class used to
 * do itself (splitting on {@code |}, matching each piece against
 * {@code ChannelEntryFlag}) moved to {@code UIEntryBaseAssembler}, which
 * builds the {@link Flag} before the constructor ever sees it - this class
 * only has to prove the constructor stores that {@link Flag} unchanged and
 * does not re-derive or re-validate it.
 *
 * @author Rowan Crowther
 */
class UIEntryBaseTest {

    private static UIEntryBase base(Flag<ChannelEntryFlag> flags, String desc) {
        return new UIEntryBase("test_base", null, CombinerName.LOGICAL_OR,
                List.of("CHAR_SCREEN1"), flags, desc);
    }

    private static Flag<ChannelEntryFlag> flagsOf(ChannelEntryFlag... on) {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        for (ChannelEntryFlag flag : on) flags.on(flag);
        return flags;
    }

    // ---- constructor: desc is validated but never stored ---------------------------------------

    @Test
    void constructorRejectsANullDescription() {
        assertThrows(IllegalArgumentException.class, () ->
                base(flagsOf(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX), null));
    }

    @Test
    void constructorAcceptsANonNullDescriptionAndDoesNotExposeIt() {
        UIEntryBase b = base(flagsOf(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX), "some description");

        assertEquals("test_base", b.getName());
        // No getDesc() exists: C's parse_entry_desc (ui-entry.c) discards the desc: value too,
        // so there is nothing further to assert beyond the constructor accepting a legitimate one.
        assertFalse(b.toString().contains("some description"));
    }

    // ---- getFlags(): the constructor stores what it is given, unchanged --------------------------

    @Test
    void aSingleFlagPassedInIsReturnedByGetFlags() {
        UIEntryBase b = base(flagsOf(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX), "d");

        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void multipleFlagsPassedInAreAllReturnedByGetFlags() {
        UIEntryBase b = base(flagsOf(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX,
                ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY), "d");

        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertTrue(b.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
    }

    @Test
    void anEmptyFlagSetPassedInIsReturnedEmpty() {
        UIEntryBase b = base(new Flag<>(ChannelEntryFlag.class), "d");

        assertTrue(b.getFlags().isEmpty());
    }

    // ---- name/renderer/combine/categories pass through unchanged ---------------------------------

    @Test
    void gettersReturnTheConstructorArguments() {
        UIEntryRenderer renderer = null;
        UIEntryBase b = new UIEntryBase("t", renderer, CombinerName.ADD,
                List.of("CHAR_SCREEN1", "abilities"),
                flagsOf(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX), "d");

        assertEquals("t", b.getName());
        assertEquals(renderer, b.getRenderer());
        assertEquals(CombinerName.ADD, b.getCombine());
        assertEquals(List.of("CHAR_SCREEN1", "abilities"), b.getCategories());
    }
}
