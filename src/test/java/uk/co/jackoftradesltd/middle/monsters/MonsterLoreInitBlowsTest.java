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

import java.util.IdentityHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MonsterLore#initblows(int)}, which gives a blank lore one blank {@link MonsterBlow}
 * per blow its race has, the Java stand-in for C's zeroed {@code lore->blows} array.
 *
 * @author Rowan Crowther
 */
class MonsterLoreInitBlowsTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 4, 9})
    @DisplayName("fills the list with one blank blow per slot")
    void fillsBlankSlots(int count) {
        MonsterLore lore = new MonsterLore();

        lore.initblows(count);

        assertEquals(count, lore.getBlows().size());
        for (MonsterBlow blow : lore.getBlows()) {
            assertNotNull(blow);
            assertEquals(0, blow.getTimesSeen());
        }
    }

    @Test
    @DisplayName("every slot is a separate object, so counting one blow leaves the others at zero")
    void slotsAreDistinct() {
        MonsterLore lore = new MonsterLore();
        lore.initblows(3);

        Set<MonsterBlow> distinct = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        distinct.addAll(lore.getBlows());
        assertEquals(3, distinct.size());

        lore.getBlows().get(1).incrementBlowTimesSeen();

        assertEquals(0, lore.getBlows().get(0).getTimesSeen());
        assertEquals(1, lore.getBlows().get(1).getTimesSeen());
        assertEquals(0, lore.getBlows().get(2).getTimesSeen());
    }

    @Test
    @DisplayName("a second call with the same count leaves the existing slots alone")
    void repeatCallIsIdempotent() {
        MonsterLore lore = new MonsterLore();
        lore.initblows(2);
        MonsterBlow first = lore.getBlows().get(0);
        first.incrementBlowTimesSeen();

        lore.initblows(2);

        assertEquals(2, lore.getBlows().size());
        assertSame(first, lore.getBlows().get(0));
        assertEquals(1, first.getTimesSeen());
    }

    @Test
    @DisplayName("a second call with a different count changes nothing")
    void mismatchedRepeatCallChangesNothing() {
        MonsterLore lore = new MonsterLore();
        lore.initblows(2);

        lore.initblows(5);

        assertEquals(2, lore.getBlows().size());
    }

    @Test
    @DisplayName("a negative count adds nothing")
    void negativeCountAddsNothing() {
        MonsterLore lore = new MonsterLore();

        lore.initblows(-1);

        assertTrue(lore.getBlows().isEmpty());
    }

    @Test
    @DisplayName("a record built with a null blow list gets a list of its own")
    void nullListIsReplaced() {
        MonsterLore lore = new MonsterLore(false, false, null, 0, 0, 0, 0, 0, false, null, null, null, null,
                0, false, null, null, 0, 0, false, null, false, 0, 0, 0);

        lore.initblows(2);

        assertEquals(2, lore.getBlows().size());
    }
}
