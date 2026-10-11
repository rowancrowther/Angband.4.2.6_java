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
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the blank {@link MonsterBlow} and {@link MonsterBlow#incrementBlowTimesSeen()}.
 *
 * <p>C bumps a blow's sighting count with {@code if (times_seen < UCHAR_MAX) times_seen++}
 * ({@code mon-attack.c}), so the count climbs from 0 and stops at 255. The "obvious, damaging or
 * seen more than 10 times" condition around that line is combat code and is not part of the blow.
 * Because per-blow known-ness is derived ({@code times_seen > 0 || all_known}, the rule
 * {@code lore_update} applies in {@code mon-lore.c}), the exact edges 0, 1 and 255 are the values
 * that matter.
 *
 * @author Rowan Crowther
 */
class MonsterBlowTimesSeenTest {

    @Test
    @DisplayName("a blank blow has no method, effect or dice and has never been seen")
    void blankBlowIsEmpty() {
        MonsterBlow blow = new MonsterBlow();

        assertNull(blow.getMethod());
        assertNull(blow.getEffect());
        assertNull(blow.getDice());
        assertEquals(0, blow.getTimesSeen());
    }

    @ParameterizedTest(name = "{0} increments give a count of {1}")
    @CsvSource({"0, 0", "1, 1", "2, 2", "10, 10", "11, 11", "254, 254", "255, 255", "256, 255", "1000, 255"})
    @DisplayName("the count rises by one per call and stops at 255")
    void countStopsAtUnsignedCharMax(int calls, int expected) {
        MonsterBlow blow = new MonsterBlow();

        for (int i = 0; i < calls; i++)
            blow.incrementBlowTimesSeen();

        assertEquals(expected, blow.getTimesSeen());
    }

    @Test
    @DisplayName("incrementing one blow leaves another untouched")
    void blowsCountIndependently() {
        MonsterBlow first = new MonsterBlow();
        MonsterBlow second = new MonsterBlow();

        first.incrementBlowTimesSeen();
        first.incrementBlowTimesSeen();

        assertEquals(2, first.getTimesSeen());
        assertEquals(0, second.getTimesSeen());
    }
}
