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

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link MonsterRace#setCurNum(int)}, {@link MonsterRace#setMaxNum(int)} and
 * {@link MonsterRace#getLore()}.
 *
 * <p>C only ever writes {@code race->cur_num} and {@code race->max_num} directly — reset to zero on
 * level generation, {@code --}/{@code ++} on death/generation (mon-make.c:257-1107), reset to
 * {@code 0}/{@code 1}/{@code 100} at birth (player-birth.c:424-427) — with no clamping either side,
 * even though C declares {@code max_num} as an unsigned byte (monster.h:361-362). {@code get_lore}
 * (mon-lore.c:1735-1738) instead indexes a global {@code l_list} array by {@code race->ridx}; this
 * port stores the record directly on the race as a field, so the interesting check is that
 * {@code getLore} returns the exact reference given to {@code setLore}, not a copy.
 *
 * @author Rowan Crowther
 */
class MonsterRacePopulationAndLoreTest {

    /**
     * Reads one of the race's private fields.
     *
     * @param race the race to inspect
     * @param name the field's name
     * @return the field's current value
     * @throws Exception if the field cannot be reached
     */
    private static int readIntField(MonsterRace race, String name) throws Exception {
        Field field = MonsterRace.class.getDeclaredField(name);
        field.setAccessible(true);
        return (int) field.get(race);
    }

    @Test
    @DisplayName("a fresh instance starts with both counts at zero")
    void freshInstanceStartsAtZero() throws Exception {
        MonsterRace race = new MonsterRace();

        assertEquals(0, readIntField(race, "curNum"));
        assertEquals(0, readIntField(race, "maxNum"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 100})
    @DisplayName("setCurNum stores the value unchanged, matching C's direct assignment")
    void setCurNumStoresValueUnchanged(int value) throws Exception {
        MonsterRace race = new MonsterRace();

        race.setCurNum(value);

        assertEquals(value, readIntField(race, "curNum"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 255})
    @DisplayName("setMaxNum stores the value unchanged, matching C's direct assignment")
    void setMaxNumStoresValueUnchanged(int value) throws Exception {
        MonsterRace race = new MonsterRace();

        race.setMaxNum(value);

        assertEquals(value, readIntField(race, "maxNum"));
    }

    @Test
    @DisplayName("setCurNum and setMaxNum do not affect one another")
    void curNumAndMaxNumAreIndependent() throws Exception {
        MonsterRace race = new MonsterRace();

        race.setMaxNum(1);
        race.setCurNum(1);
        race.setCurNum(0);

        assertEquals(0, readIntField(race, "curNum"));
        assertEquals(1, readIntField(race, "maxNum"), "clearing curNum must not clear maxNum");
    }

    @Test
    @DisplayName("a fresh race carries no lore")
    void freshRaceHasNoLore() {
        assertNull(new MonsterRace().getLore());
    }

    @Test
    @DisplayName("getLore returns the exact reference passed to setLore, not a copy")
    void getLoreReturnsSameReference() {
        MonsterRace race = new MonsterRace();
        MonsterLore lore = new MonsterLore(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, null, null, null, null, null, null);

        race.setLore(lore);

        assertSame(lore, race.getLore());
    }
}
