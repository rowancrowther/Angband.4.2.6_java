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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the accessors of {@link Trap} against {@code struct trap} in {@code trap.h} and the timeout countdown in
 * {@code game-world.c}.
 *
 * <p>The countdown in C is {@code if (trap->timeout) { trap->timeout--; ... }}. {@link Trap#decrementTimeout()} is
 * the bare {@code trap->timeout--}, so the zero guard belongs to the caller; the tests pin the walk from a positive
 * timeout down to zero and the flag lookups, which are per trap and never fall back to the kind's flags.
 * {@link Trap} has no constructor or setters for its state, so the fixtures fill its private fields by reflection.
 *
 * <p>Class TrapTest coded on 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
class TrapTest {

    private static TrapKind kind(TrapEnum... kindFlags) {
        Flag<TrapEnum> set = new Flag<>(TrapEnum.class);
        for (TrapEnum flag : kindFlags) {
            set.on(flag);
        }
        return new TrapKind("test", "", "a test trap", "", "", "", "", 7, null, 0, 0, 0, null,
                set, null, new ArrayList<>(), new ArrayList<>());
    }

    private static void set(Trap trap, String field, Object value) {
        try {
            Field f = Trap.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(trap, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Trap trap(TrapKind kind, int power, int timeout, TrapEnum... flags) {
        Trap trap = new Trap();
        Flag<TrapEnum> set = new Flag<>(TrapEnum.class);
        for (TrapEnum flag : flags) {
            set.on(flag);
        }
        set(trap, "kind", kind);
        set(trap, "power", power);
        set(trap, "timeout", timeout);
        set(trap, "flags", set);
        return trap;
    }

    @Test
    @DisplayName("getKind, getPower and getTimeout return the stored fields")
    void accessorsReturnFields() {
        TrapKind kind = kind();
        Trap trap = trap(kind, 42, 5);
        assertAll(
                () -> assertSame(kind, trap.getKind()),
                () -> assertEquals(42, trap.getPower()),
                () -> assertEquals(5, trap.getTimeout()));
    }

    @Test
    @DisplayName("hasTrap answers for the flags on this trap only (trf_has(trap->flags, ...))")
    void hasTrapReadsInstanceFlags() {
        Trap trap = trap(kind(), 0, 0, TrapEnum.TRF_VISIBLE, TrapEnum.TRF_LOCK);
        assertAll(
                () -> assertTrue(trap.hasTrap(TrapEnum.TRF_VISIBLE)),
                () -> assertTrue(trap.hasTrap(TrapEnum.TRF_LOCK)),
                () -> assertFalse(trap.hasTrap(TrapEnum.TRF_TRAP)),
                () -> assertFalse(trap.hasTrap(TrapEnum.TRF_WEB)));
    }

    @Test
    @DisplayName("a flag set only on the kind is not found by the trap's own hasTrap")
    void kindFlagsAreNotInstanceFlags() {
        Trap trap = trap(kind(TrapEnum.TRF_FLOOR), 0, 0);
        assertFalse(trap.hasTrap(TrapEnum.TRF_FLOOR));
    }

    @Test
    @DisplayName("decrementTimeout from 1 reaches 0, the transition C flags as 'changed'")
    void decrementFromOneReachesZero() {
        Trap trap = trap(kind(), 0, 1);
        trap.decrementTimeout();
        assertEquals(0, trap.getTimeout());
    }

    @Test
    @DisplayName("decrementTimeout walks 3, 2, 1, 0 one turn at a time")
    void decrementWalksDown() {
        Trap trap = trap(kind(), 0, 3);
        int[] seen = new int[3];
        for (int i = 0; i < 3; i++) {
            trap.decrementTimeout();
            seen[i] = trap.getTimeout();
        }
        assertEquals(2, seen[0]);
        assertEquals(1, seen[1]);
        assertEquals(0, seen[2]);
    }

    @Test
    @DisplayName("decrementTimeout leaves power and flags alone")
    void decrementTouchesOnlyTimeout() {
        Trap trap = trap(kind(), 9, 2, TrapEnum.TRF_VISIBLE);
        trap.decrementTimeout();
        assertAll(
                () -> assertEquals(9, trap.getPower()),
                () -> assertTrue(trap.hasTrap(TrapEnum.TRF_VISIBLE)));
    }
}
