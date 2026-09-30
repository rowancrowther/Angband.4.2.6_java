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
 * Unit tests for {@link TerrainFlags}, the port of the {@code FEAT_*} indexes in
 * {@code list-terrain.h} and {@code cave.h}.
 *
 * <p>Expected values come from the C header: the {@code FEAT()} lines in order, from
 * {@code NONE} (0) to {@code PASS_RUBBLE} (24), then {@code FEAT_MAX} (25). The names are
 * written out here rather than read from the enum.
 *
 * <p>Class TerrainFlagsTest coded on 260930, commented in full on 260930.
 */
class TerrainFlagsTest {

    /**
     * The header's feature names, in header order; the index is the C value.
     */
    private static final String[] HEADER = {
            "FEAT_NONE", "FEAT_FLOOR", "FEAT_CLOSED", "FEAT_OPEN", "FEAT_BROKEN",
            "FEAT_LESS", "FEAT_MORE", "FEAT_STORE_GENERAL", "FEAT_STORE_ARMOR",
            "FEAT_STORE_WEAPON", "FEAT_STORE_BOOK", "FEAT_STORE_ALCHEMY",
            "FEAT_STORE_MAGIC", "FEAT_STORE_BLACK", "FEAT_HOME", "FEAT_SECRET",
            "FEAT_RUBBLE", "FEAT_MAGMA", "FEAT_QUARTZ", "FEAT_MAGMA_K", "FEAT_QUARTZ_K",
            "FEAT_GRANITE", "FEAT_PERM", "FEAT_LAVA", "FEAT_PASS_RUBBLE",
    };

    /**
     * The first, an interior and the last real terrain sit at their C values, and
     * {@code FEAT_MAX} follows.
     */
    @Test
    void ordinalsMatchCEnumValues() {
        assertEquals(0, TerrainFlags.FEAT_NONE.ordinal());
        assertEquals(1, TerrainFlags.FEAT_FLOOR.ordinal());
        assertEquals(15, TerrainFlags.FEAT_SECRET.ordinal());
        assertEquals(24, TerrainFlags.FEAT_PASS_RUBBLE.ordinal());
        assertEquals(25, TerrainFlags.FEAT_MAX.ordinal());
    }

    /**
     * Every header line maps to the constant of the same name at the same index.
     */
    @Test
    void everyHeaderLineMatchesInOrder() {
        for (int i = 0; i < HEADER.length; i++) {
            assertEquals(HEADER[i], TerrainFlags.values()[i].name(), "index " + i);
            assertEquals(i, TerrainFlags.valueOf(HEADER[i]).ordinal(), HEADER[i]);
        }
    }

    /**
     * The enum holds the header's 25 terrains plus the {@code FEAT_MAX} sentinel, and
     * nothing else.
     */
    @Test
    void sizeIsHeaderPlusSentinel() {
        assertEquals(HEADER.length + 1, TerrainFlags.values().length);
        assertEquals(TerrainFlags.FEAT_MAX.ordinal(), HEADER.length);
    }

    /**
     * {@code TerrainFeatureAssembler} looks a terrain up as {@code "FEAT_" + code} from
     * {@code terrain.txt}; an unknown code must fail rather than resolve.
     */
    @Test
    void unknownNameIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> TerrainFlags.valueOf("FEAT_LOS"));
        assertThrows(IllegalArgumentException.class, () -> TerrainFlags.valueOf("NONE"));
    }
}
