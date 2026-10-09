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

package uk.co.jackoftradesltd.middle.gameinput;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests for {@link EffectChoice}, the Java replacement for the {@code int} that C's
 * {@code get_effect_from_list} ({@code game-input.c}) returns: a list index when {@code >= 0},
 * {@code -2} for the random option and {@code -1} for a cancelled or invalid selection.
 *
 * <p>The expected values come from the C contract, not from the Java: the sentinel mapping is
 * written out as a small decoder below and each case is checked against what C would have
 * returned. The fall-back with no UI hook is {@code allow_random ? -2 : -1} in C.
 *
 * <p>Class EffectChoiceTest coded on 261009, commented in full on 261009.
 */
class EffectChoiceTest {

    /**
     * Decodes a case back into the {@code int} C would have returned, so the tests can assert
     * against the documented C values.
     */
    private static int asCInt(EffectChoice choice) {
        return switch (choice) {
            case EffectChoice.Index i -> i.value();
            case EffectChoice.Random r -> -2;
            case EffectChoice.Aborted a -> -1;
        };
    }

    @Test
    @DisplayName("the three cases decode to C's index, -2 and -1")
    void decodesToCSentinels() {
        assertEquals(0, asCInt(new EffectChoice.Index(0)));
        assertEquals(5, asCInt(new EffectChoice.Index(5)));
        assertEquals(-2, asCInt(new EffectChoice.Random()));
        assertEquals(-1, asCInt(new EffectChoice.Aborted()));
    }

    @Test
    @DisplayName("an Index keeps its value, including the first position")
    void indexKeepsValue() {
        assertEquals(0, new EffectChoice.Index(0).value());
        assertEquals(7, new EffectChoice.Index(7).value());
    }

    @Test
    @DisplayName("equal cases compare equal, different cases do not")
    void equality() {
        assertEquals(new EffectChoice.Index(2), new EffectChoice.Index(2));
        assertNotEquals(new EffectChoice.Index(2), new EffectChoice.Index(3));
        assertEquals(new EffectChoice.Random(), new EffectChoice.Random());
        assertEquals(new EffectChoice.Aborted(), new EffectChoice.Aborted());
        assertNotEquals(new EffectChoice.Random(), new EffectChoice.Aborted());
        assertNotEquals(new EffectChoice.Index(0), new EffectChoice.Aborted());
    }

    @Test
    @DisplayName("an Index is not confused with a sentinel, unlike C's int")
    void indexIsNotASentinel() {
        // In C, -1 and -2 are indistinguishable from a bad index; here they are different types.
        assertNotEquals(new EffectChoice.Random(), new EffectChoice.Index(-2));
        assertNotEquals(new EffectChoice.Aborted(), new EffectChoice.Index(-1));
    }

    @Test
    @DisplayName("exactly three cases are permitted: Index, Random and Aborted")
    void exactlyThreeCases() {
        Set<Class<?>> permitted = Arrays.stream(EffectChoice.class.getPermittedSubclasses())
                .collect(Collectors.toSet());
        assertEquals(Set.of(EffectChoice.Index.class, EffectChoice.Random.class,
                EffectChoice.Aborted.class), permitted);
    }

    @Test
    @DisplayName("with no UI hook, random allowed gives Random (C returns -2)")
    void defaultFallbackAllowRandom() {
        EffectChoice choice = new DefaultGameInput().getEffectFromList(null, null, -1, true);
        assertEquals(-2, asCInt(choice));
    }

    @Test
    @DisplayName("with no UI hook, random not allowed gives Aborted (C returns -1)")
    void defaultFallbackNoRandom() {
        EffectChoice choice = new DefaultGameInput().getEffectFromList(null, null, -1, false);
        assertEquals(-1, asCInt(choice));
    }
}
