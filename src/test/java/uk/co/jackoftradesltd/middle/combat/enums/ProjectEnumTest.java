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

package uk.co.jackoftradesltd.middle.combat.enums;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ProjectEnum}, the port of the anonymous {@code PROJECT_*} enum in
 * {@code project.h}. Expected names, order and bit values are copied from that header, not
 * read off the Java enum.
 *
 * @author Rowan Crowther
 */
class ProjectEnumTest {

    /**
     * Name and C bit value, in the declaration order of {@code project.h}.
     */
    private static final Object[][] C_TABLE = {
            {"PROJECT_NONE", 0x0000},
            {"PROJECT_JUMP", 0x0001},
            {"PROJECT_BEAM", 0x0002},
            {"PROJECT_THRU", 0x0004},
            {"PROJECT_STOP", 0x0008},
            {"PROJECT_GRID", 0x0010},
            {"PROJECT_ITEM", 0x0020},
            {"PROJECT_KILL", 0x0040},
            {"PROJECT_HIDE", 0x0080},
            {"PROJECT_AWARE", 0x0100},
            {"PROJECT_SAFE", 0x0200},
            {"PROJECT_ARC", 0x0400},
            {"PROJECT_PLAY", 0x0800},
            {"PROJECT_INFO", 0x1000},
            {"PROJECT_SHORT", 0x2000},
            {"PROJECT_SELF", 0x4000},
            {"PROJECT_ROCK", 0x8000},
    };

    /**
     * The bit C would give a constant, derived from its position: NONE is 0, then 1, 2, 4, ...
     */
    private static int bitOf(ProjectEnum e) {
        return e.ordinal() == 0 ? 0 : 1 << (e.ordinal() - 1);
    }

    @Test
    void sameNumberOfConstantsAsC() {
        assertEquals(C_TABLE.length, ProjectEnum.values().length);
    }

    @Test
    void namesAndOrderMatchC() {
        ProjectEnum[] values = ProjectEnum.values();
        for (int i = 0; i < C_TABLE.length; i++)
            assertEquals(C_TABLE[i][0], values[i].name(), "position " + i);
    }

    @Test
    void positionImpliesCBitValue() {
        for (Object[] row : C_TABLE)
            assertEquals(row[1], bitOf(ProjectEnum.valueOf((String) row[0])), (String) row[0]);
    }

    @Test
    void noneIsFirstAndRockIsLast() {
        assertEquals(0, ProjectEnum.PROJECT_NONE.ordinal());
        assertEquals(ProjectEnum.PROJECT_ROCK, ProjectEnum.values()[ProjectEnum.values().length - 1]);
    }

    @Test
    void emptyFlagSetHasNoMembers() {
        Flag<ProjectEnum> none = new Flag<>(ProjectEnum.class);
        assertTrue(none.isEmpty());
        assertFalse(none.has(ProjectEnum.PROJECT_NONE));
    }

    /**
     * Pins the deliberate divergence: C's PROJECT_NONE is an empty mask, here it is a member.
     */
    @Test
    void flagBuiltFromNoneIsNotEmpty() {
        Flag<ProjectEnum> none = new Flag<>(ProjectEnum.class, ProjectEnum.PROJECT_NONE);
        assertFalse(none.isEmpty());
        assertEquals(1, none.count());
        assertTrue(none.has(ProjectEnum.PROJECT_NONE));
    }

    @Test
    void combinedFlagsAreIndependentMembers() {
        Flag<ProjectEnum> f = new Flag<>(ProjectEnum.class, ProjectEnum.PROJECT_BEAM, ProjectEnum.PROJECT_ROCK);
        assertTrue(f.has(ProjectEnum.PROJECT_BEAM));
        assertTrue(f.has(ProjectEnum.PROJECT_ROCK));
        assertFalse(f.has(ProjectEnum.PROJECT_THRU));
        assertEquals(2, f.count());
    }
}
