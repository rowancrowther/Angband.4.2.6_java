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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Pins {@link RuneGroup} against C's {@code rune_group_text[]} ({@code ui-knowledge.c}) and the
 * {@code enum rune_variety} ({@code obj-knowledge.h}) that indexes it.
 *
 * <p>Expected values are read off the C tables: the seven strings in the order the array lists
 * them, and the enum's values {@code RUNE_VAR_COMBAT} to {@code RUNE_VAR_FLAG} as 0 to 6. C's
 * trailing {@code NULL} terminator is deliberately not a constant here.
 *
 * <p>Test RuneGroupTest written on 261009.
 *
 * @author Rowan Crowther
 */
class RuneGroupTest {

    /** The strings of {@code rune_group_text[]}, in array order, without the {@code NULL} terminator. */
    private static final List<String> C_GROUP_TEXT =
            List.of("Combat", "Modifiers", "Resists", "Brands", "Slays", "Curses", "Other");

    @Test
    void headingsAreRuneGroupTextInOrder() {
        assertEquals(C_GROUP_TEXT, Arrays.stream(RuneGroup.values()).map(RuneGroup::getName).toList());
    }

    @Test
    void thereAreSevenGroupsAndNoTerminatorEntry() {
        // C's N_ELEMENTS(rune_group_text) is 8 because of the NULL; the port keeps only the 7 headings
        assertEquals(7, RuneGroup.values().length);
    }

    @Test
    void ordinalsAreTheRuneVarietyEnumValues() {
        // enum rune_variety: COMBAT 0, MOD 1, RESIST 2, BRAND 3, SLAY 4, CURSE 5, FLAG 6
        assertEquals(0, RuneGroup.COMBAT.ordinal());
        assertEquals(1, RuneGroup.MODIFIERS.ordinal());
        assertEquals(2, RuneGroup.RESIST.ordinal());
        assertEquals(3, RuneGroup.BRAND.ordinal());
        assertEquals(4, RuneGroup.SLAY.ordinal());
        assertEquals(5, RuneGroup.CURSE.ordinal());
        assertEquals(6, RuneGroup.OTHER.ordinal());
    }

    @Test
    void flagVarietyIsLabelledOtherNotFlags() {
        // RUNE_VAR_FLAG indexes the last heading, which reads "Other"
        assertEquals("Other", RuneGroup.OTHER.getName());
        assertEquals(C_GROUP_TEXT.get(6), RuneGroup.values()[6].getName());
    }

    @Test
    void headingsAreDistinctSoNoTwoGroupsShareAPanelEntry() {
        Set<String> names = new HashSet<>();
        for (RuneGroup g : RuneGroup.values()) {
            names.add(g.getName());
        }
        assertEquals(7, names.size());
    }

    @Test
    void getNameIsTheDisplayTextNotTheConstantIdentifier() {
        assertEquals("RESIST", RuneGroup.RESIST.name());
        assertEquals("Resists", RuneGroup.RESIST.getName());
        assertNotEquals(RuneGroup.MODIFIERS.name(), RuneGroup.MODIFIERS.getName());
    }
}
