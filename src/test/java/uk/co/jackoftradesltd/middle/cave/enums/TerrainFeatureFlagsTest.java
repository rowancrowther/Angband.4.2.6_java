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

package uk.co.jackoftradesltd.middle.cave.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link TerrainFeatureFlags}, the port of {@code list-terrain-flags.h} and the
 * {@code TF_*} enum in {@code cave.h}.
 *
 * <p>Expected values come from the C header: the {@code TF()} lines in order, from {@code NONE}
 * (0) to {@code FIERY} (31), then {@code TF_MAX} (32). The descriptions are the header's second
 * field, written out here rather than read from the enum.
 *
 * <p>Class TerrainFeatureFlagsTest coded on 260930, commented in full on 260930.
 */
class TerrainFeatureFlagsTest {

    /**
     * The header's flag names and descriptions, in header order.
     */
    private static final String[][] HEADER = {
            {"TF_NONE", ""},
            {"TF_LOS", "Allows line of sight"},
            {"TF_PROJECT", "Allows projections to pass through"},
            {"TF_PASSABLE", "Can be passed through by all creatures"},
            {"TF_INTERESTING", "Is noticed on looking around"},
            {"TF_PERMANENT", "Is permanent"},
            {"TF_EASY", "Is easily passed through"},
            {"TF_TRAP", "Can hold a trap"},
            {"TF_NO_SCENT", "Cannot store scent"},
            {"TF_NO_FLOW", "No flow through"},
            {"TF_OBJECT", "Can hold objects"},
            {"TF_TORCH", "Becomes bright when torch-lit"},
            {"TF_HIDDEN", "Can be found by searching"},
            {"TF_GOLD", "Contains treasure"},
            {"TF_CLOSABLE", "Can be closed"},
            {"TF_FLOOR", "Is a clear floor"},
            {"TF_WALL", "Is a solid wall"},
            {"TF_ROCK", "Is rocky"},
            {"TF_GRANITE", "Is a granite rock wall"},
            {"TF_DOOR_ANY", "Is any door"},
            {"TF_DOOR_CLOSED", "Is a closed door"},
            {"TF_SHOP", "Is a shop"},
            {"TF_DOOR_JAMMED", "Is a jammed door"},
            {"TF_DOOR_LOCKED", "Is a locked door"},
            {"TF_MAGMA", "Is a magma seam"},
            {"TF_QUARTZ", "Is a quartz seam"},
            {"TF_STAIR", "Is a stair"},
            {"TF_UPSTAIR", "Is an up staircase"},
            {"TF_DOWNSTAIR", "Is a down staircase"},
            {"TF_SMOOTH", "Should have smooth boundaries"},
            {"TF_BRIGHT", "Is internally lit"},
            {"TF_FIERY", "Is fire-based"},
    };

    @Test
    void ordinalsMatchCEnumValues() {
        assertEquals(0, TerrainFeatureFlags.TF_NONE.ordinal());
        assertEquals(1, TerrainFeatureFlags.TF_LOS.ordinal());
        assertEquals(17, TerrainFeatureFlags.TF_ROCK.ordinal());
        assertEquals(31, TerrainFeatureFlags.TF_FIERY.ordinal());
        assertEquals(32, TerrainFeatureFlags.TF_MAX.ordinal());
    }

    @Test
    void maxIsTheSizeOfTheFlagSet() {
        assertEquals(HEADER.length, TerrainFeatureFlags.TF_MAX.ordinal());
        assertEquals(HEADER.length + 1, TerrainFeatureFlags.values().length);
    }

    @Test
    void everyFlagHasTheHeadersNameAndDescriptionAtTheHeadersIndex() {
        for (int i = 0; i < HEADER.length; i++) {
            TerrainFeatureFlags flag = TerrainFeatureFlags.values()[i];
            assertEquals(HEADER[i][0], flag.name(), "name at index " + i);
            assertEquals(HEADER[i][1], flag.getDescription(), "description of " + HEADER[i][0]);
        }
    }

    @Test
    void sentinelsHaveEmptyDescriptions() {
        assertEquals("", TerrainFeatureFlags.TF_NONE.getDescription());
        assertEquals("", TerrainFeatureFlags.TF_MAX.getDescription());
    }

    @Test
    void namesResolveByValueOf() {
        assertEquals(TerrainFeatureFlags.TF_DOOR_JAMMED, TerrainFeatureFlags.valueOf("TF_DOOR_JAMMED"));
        assertThrows(IllegalArgumentException.class, () -> TerrainFeatureFlags.valueOf("TF_DOOR_JAM"));
    }
}
