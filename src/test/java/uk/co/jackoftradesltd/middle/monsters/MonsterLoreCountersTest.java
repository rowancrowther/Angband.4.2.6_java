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

/**
 * Tests {@link MonsterLore#setPSkills(int)} and {@link MonsterLore#setThefts(int)}.
 *
 * <p>C declares {@code lore->pkills} and {@code lore->thefts} as {@code uint16_t} (mon-lore.h:35-36)
 * and, everywhere the C source writes them, does so as a direct assignment — no accumulation, no
 * clamping. The port stores both as plain {@code int} fields, matching every other counter already
 * on this class, so the interesting check is that each setter writes only its own field.
 *
 * @author Rowan Crowther
 */
class MonsterLoreCountersTest {

    /**
     * Builds a lore record with every counter at zero.
     *
     * @return the lore under test
     */
    private static MonsterLore zeroedLore() {
        return new MonsterLore(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, null, null, null, null, null, null);
    }

    /**
     * Reads one of the lore's private fields.
     *
     * @param lore the lore to inspect
     * @param name the field's name
     * @return the field's current value
     * @throws Exception if the field cannot be reached
     */
    private static int readIntField(MonsterLore lore, String name) throws Exception {
        Field field = MonsterLore.class.getDeclaredField(name);
        field.setAccessible(true);
        return (int) field.get(lore);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 65535})
    @DisplayName("setPSkills stores the value unchanged into pkills")
    void setPSkillsStoresValueUnchanged(int value) throws Exception {
        MonsterLore lore = zeroedLore();

        lore.setPSkills(value);

        assertEquals(value, readIntField(lore, "pkills"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 65535})
    @DisplayName("setThefts stores the value unchanged into thefts")
    void setTheftsStoresValueUnchanged(int value) throws Exception {
        MonsterLore lore = zeroedLore();

        lore.setThefts(value);

        assertEquals(value, readIntField(lore, "thefts"));
    }

    @Test
    @DisplayName("setPSkills and setThefts do not affect one another or the other counters")
    void countersAreIndependent() throws Exception {
        MonsterLore lore = zeroedLore();

        lore.setPSkills(5);
        lore.setThefts(3);

        assertEquals(5, readIntField(lore, "pkills"));
        assertEquals(3, readIntField(lore, "thefts"));
        assertEquals(0, readIntField(lore, "sightings"), "unrelated counters must be untouched");
        assertEquals(0, readIntField(lore, "tkills"), "unrelated counters must be untouched");

        lore.setPSkills(0);

        assertEquals(0, readIntField(lore, "pkills"));
        assertEquals(3, readIntField(lore, "thefts"), "clearing pkills must not clear thefts");
    }
}
