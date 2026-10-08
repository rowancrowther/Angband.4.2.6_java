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
import uk.co.jackoftradesltd.channel.utils.Flag;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ObjectDescription} against the anonymous enum under "Modes for object_desc()" in
 * {@code obj-desc.h}: the names, their order, and the bit each one holds in C's {@code uint32_t} mode.
 *
 * <p>Three departures from C are pinned here: {@code ODESC_BASE} is 0x00 in C but a real constant
 * here, {@code ODESC_FULL} has no constant, and the ordinal of every constant after {@code ODESC_BASE}
 * is one more than its bit position.
 *
 * <p>Class ObjectDescriptionTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class ObjectDescriptionTest {

    /** The names in {@code obj-desc.h}, in the order the header lists them, minus {@code ODESC_FULL}. */
    private static final List<String> HEADER_NAMES = List.of(
            "ODESC_BASE", "ODESC_COMBAT", "ODESC_EXTRA", "ODESC_STORE", "ODESC_PLURAL", "ODESC_SINGULAR",
            "ODESC_SPOIL", "ODESC_PREFIX", "ODESC_CAPITAL", "ODESC_TERSE", "ODESC_NOEGO", "ODESC_ALTNUM");

    /** The values in {@code obj-desc.h}, in the same order. */
    private static final int[] HEADER_VALUES = {
            0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x100, 0x200, 0x400};

    @Test
    void constantsAreTheHeaderNamesInHeaderOrder() {
        assertEquals(HEADER_NAMES, Arrays.stream(ObjectDescription.values()).map(Enum::name).toList());
    }

    @Test
    void noConstantForOdescFull() {
        // C: ODESC_FULL = ODESC_COMBAT | ODESC_EXTRA is a convenience alias, written out by callers.
        assertThrows(IllegalArgumentException.class, () -> ObjectDescription.valueOf("ODESC_FULL"));
    }

    @Test
    void ordinalAfterBaseIsOnePastTheBitPosition() {
        for (ObjectDescription mode : ObjectDescription.values()) {
            if (mode == ObjectDescription.ODESC_BASE) continue;
            assertEquals(HEADER_VALUES[mode.ordinal()], 1 << (mode.ordinal() - 1), mode.name());
        }
        assertEquals(0x01, 1 << (ObjectDescription.ODESC_COMBAT.ordinal() - 1));
        assertEquals(0x80, 1 << (ObjectDescription.ODESC_CAPITAL.ordinal() - 1));
        assertEquals(0x400, 1 << (ObjectDescription.ODESC_ALTNUM.ordinal() - 1));
    }

    @Test
    void baseIsTheOnlyConstantWithNoBit() {
        assertEquals(0x00, HEADER_VALUES[ObjectDescription.ODESC_BASE.ordinal()]);
        for (ObjectDescription mode : ObjectDescription.values()) {
            if (mode != ObjectDescription.ODESC_BASE) {
                assertNotEquals(0, HEADER_VALUES[mode.ordinal()], mode.name());
            }
        }
    }

    @Test
    void elevenBitsOccupyTheLowElevenBitsOfTheCMode() {
        assertEquals(12, ObjectDescription.values().length);
        assertEquals(0x7FF, Arrays.stream(HEADER_VALUES).reduce(0, (a, b) -> a | b));
    }

    @Test
    void odescFullIsCombatPlusExtra() {
        // C: ODESC_PREFIX | ODESC_FULL == 0x43; the port writes the three out.
        Flag<ObjectDescription> mode = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_PREFIX,
                ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_EXTRA);
        assertEquals(3, mode.count());
        assertEquals(0x43, HEADER_VALUES[ObjectDescription.ODESC_PREFIX.ordinal()]
                | HEADER_VALUES[ObjectDescription.ODESC_COMBAT.ordinal()]
                | HEADER_VALUES[ObjectDescription.ODESC_EXTRA.ordinal()]);
    }

    @Test
    void emptySetIsNotASetHoldingOnlyBase() {
        // C: both are 0x00, but a Flag over the enum can tell them apart, so object_desc must not test BASE.
        Flag<ObjectDescription> empty = new Flag<>(ObjectDescription.class);
        Flag<ObjectDescription> baseOnly = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_BASE);
        assertTrue(empty.isEmpty());
        assertFalse(baseOnly.isEmpty());
        assertFalse(empty.isEqual(baseOnly));
    }

    @Test
    void prefixWithBaseDoesNotSetTheCombatOrExtraBits() {
        // C: ODESC_PREFIX | ODESC_BASE (mon-blows.c) must not trigger "mode & ODESC_COMBAT" or "mode & ODESC_EXTRA".
        Flag<ObjectDescription> mode = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_PREFIX,
                ObjectDescription.ODESC_BASE);
        assertTrue(mode.has(ObjectDescription.ODESC_PREFIX));
        assertFalse(mode.has(ObjectDescription.ODESC_COMBAT));
        assertFalse(mode.has(ObjectDescription.ODESC_EXTRA));
    }

    @Test
    void valueOfRejectsOtherSpellings() {
        assertEquals(ObjectDescription.ODESC_NOEGO, ObjectDescription.valueOf("ODESC_NOEGO"));
        assertThrows(IllegalArgumentException.class, () -> ObjectDescription.valueOf("odesc_noego"));
    }
}
