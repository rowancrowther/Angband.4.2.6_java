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
import uk.co.jackoftradesltd.middle.numerics.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the three-argument {@link MonsterBlow} constructor and the {@code known} flag.
 *
 * <p>C's {@code parse_monster_blow} ({@code mon-init.c}) fills a race's blow from the {@code blow:}
 * line and leaves {@code times_seen} at the zero that {@code mem_zalloc} gave it. Whether the player
 * knows a blow is not on C's {@code monster_blow} at all: it is the parallel {@code lore->blow_known}
 * array, which {@code mem_zalloc} also starts at false and which {@code lore_update}
 * ({@code mon-lore.c}) only ever sets to true.
 *
 * <p>Class MonsterBlowConstructionTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class MonsterBlowConstructionTest {

    @Test
    @DisplayName("the constructor stores the dice it is given, by reference")
    void storesDiceByReference() {
        Random dice = new Random(0, 0, 2, 6, false);

        MonsterBlow blow = new MonsterBlow(null, null, dice);

        assertSame(dice, blow.getDice());
        assertEquals(2, blow.getDice().getDice());
        assertEquals(6, blow.getDice().getSides());
    }

    @Test
    @DisplayName("a blow built with the constructor starts unseen and unknown")
    void startsUnseenAndUnknown() {
        MonsterBlow blow = new MonsterBlow(null, null, new Random(0, 0, 1, 4, false));

        assertEquals(0, blow.getTimesSeen());
        assertFalse(blow.isKnown());
    }

    @Test
    @DisplayName("a blow built with null parts keeps them null")
    void nullPartsStayNull() {
        MonsterBlow blow = new MonsterBlow(null, null, null);

        assertNull(blow.getMethod());
        assertNull(blow.getEffect());
        assertNull(blow.getDice());
    }

    @Test
    @DisplayName("a blank blow starts unknown")
    void blankBlowStartsUnknown() {
        assertFalse(new MonsterBlow().isKnown());
    }

    @Test
    @DisplayName("setKnown turns the flag on and off and does not touch the sighting count")
    void setKnownIsPlainAssignment() {
        MonsterBlow blow = new MonsterBlow();

        blow.setKnown(true);
        assertTrue(blow.isKnown());
        assertEquals(0, blow.getTimesSeen());

        blow.setKnown(false);
        assertFalse(blow.isKnown());
    }

    @Test
    @DisplayName("counting a sighting does not make the blow known")
    void sightingDoesNotSetKnown() {
        MonsterBlow blow = new MonsterBlow();

        blow.incrementBlowTimesSeen();

        assertEquals(1, blow.getTimesSeen());
        assertFalse(blow.isKnown());
    }
}
