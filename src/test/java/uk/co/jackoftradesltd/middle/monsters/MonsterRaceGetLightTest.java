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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link MonsterRace#getLight()}.
 *
 * <p>Expected values come from the C original, where {@code race->light} is a plain signed
 * {@code int} (monster.h:342) written by {@code parse_monster_light} (mon-init.c:1196) with no
 * clamping, and read back unchanged. The interesting values are the four bands the lore code
 * distinguishes (mon-lore.c:1317-1324): {@code > 1}, {@code == 1}, {@code == -1} and {@code < -1},
 * plus zero, which mon-make.c:333 and mon-util.c:604 use as the "no lighting effect" test.
 */
class MonsterRaceGetLightTest {

    /**
     * Builds a race carrying nothing but the given light value.
     *
     * @param light the light intensity to store
     * @return the race under test
     */
    private static MonsterRace raceWithLight(int light) {
        return new MonsterRace("test", "", "", null, 0, 0, 0, 0, 0, 0, light, 0, 0, 0, 0,
                null, null, List.of(), 0, 0, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), 0, null);
    }

    @ParameterizedTest
    @ValueSource(ints = {-3, -2, -1, 0, 1, 2, 4})
    @DisplayName("the stored light value is returned unchanged and unclamped")
    void returnsStoredValue(int light) {
        assertEquals(light, raceWithLight(light).getLight());
    }

    @Test
    @DisplayName("a race with no light line reads as zero, as C's zeroed allocation does")
    void defaultsToZero() {
        assertEquals(0, new MonsterRace().getLight());
    }

    @Test
    @DisplayName("negative light survives as negative, so darkening races are distinguishable")
    void negativeLightIsPreserved() {
        assertTrue(raceWithLight(-1).getLight() < 0);
        assertTrue(raceWithLight(-2).getLight() < -1);
    }

    @Test
    @DisplayName("the four lore bands are separable from the returned value")
    void loreBandsAreSeparable() {
        assertTrue(raceWithLight(2).getLight() > 1);
        assertEquals(1, raceWithLight(1).getLight());
        assertEquals(-1, raceWithLight(-1).getLight());
        assertTrue(raceWithLight(-2).getLight() < -1);
    }

    @Test
    @DisplayName("only zero counts as no lighting effect")
    void zeroIsTheOnlyInertValue() {
        assertEquals(0, raceWithLight(0).getLight());
        assertTrue(raceWithLight(1).getLight() != 0);
        assertTrue(raceWithLight(-1).getLight() != 0);
    }
}
