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

package uk.co.jackoftradesltd.middle.player.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link SustainStat} - the port of C's {@code sustain_flag()} ({@code obj-properties.c}).
 *
 * <p><b>Every expectation comes from the C source, not from the port.</b> C is
 * {@code if (stat < 0 || stat >= STAT_MAX) return -1; return stat + 1;}, and {@code list-stats.h}
 * and {@code list-object-flags.h} pin the layout: the five stats are 0 to 4 and the five sustains
 * are the first five object flags, 1 to 5, behind {@code OF_NONE}.
 *
 * <p>The one place the port differs is the out-of-range answer: C returns {@code -1}, the port
 * returns {@code null}. That is recorded below as a decision, not asserted as C behaviour.
 *
 * @author Rowan Crowther
 */
@DisplayName("SustainStat")
class SustainStatTest {

    @Test
    @DisplayName("each real stat maps to its own sustain, as stat + 1")
    void realStatsMapToSustains() {
        assertSame(SustainStat.SUS_STAT_STR, SustainStat.getSustainFromStat(Stats.STAT_STR));
        assertSame(SustainStat.SUS_STAT_INT, SustainStat.getSustainFromStat(Stats.STAT_INT));
        assertSame(SustainStat.SUS_STAT_WIS, SustainStat.getSustainFromStat(Stats.STAT_WIS));
        assertSame(SustainStat.SUS_STAT_DEX, SustainStat.getSustainFromStat(Stats.STAT_DEX));
        assertSame(SustainStat.SUS_STAT_CON, SustainStat.getSustainFromStat(Stats.STAT_CON));
    }

    @Test
    @DisplayName("the sustain's value is the stat index plus one, for every real stat")
    void valueIsIndexPlusOne() {
        for (int stat = 0; stat < 5; stat++) {
            assertEquals(stat + 1, SustainStat.getSustainFromInt(stat).getSustainValue(),
                    "stat index " + stat);
        }
    }

    @Test
    @DisplayName("getSustainFromStat and getSustainFromInt agree for every real stat")
    void stat_and_int_routes_agree() {
        for (Stats stat : new Stats[]{Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS,
                Stats.STAT_DEX, Stats.STAT_CON}) {
            assertSame(SustainStat.getSustainFromInt(stat.getValue()),
                    SustainStat.getSustainFromStat(stat));
        }
    }

    @Test
    @DisplayName("the lowest bound: index 0 is STR's sustain, not the NONE sentinel")
    void lowestRealIndex() {
        assertSame(SustainStat.SUS_STAT_STR, SustainStat.getSustainFromInt(0));
    }

    @Test
    @DisplayName("the highest bound: index 4 is CON's sustain, the last real one")
    void highestRealIndex() {
        assertSame(SustainStat.SUS_STAT_CON, SustainStat.getSustainFromInt(4));
    }

    @Test
    @DisplayName("C rejects stat < 0; the port answers null where C answers -1")
    void belowRangeAnswersNone() {
        assertNull(SustainStat.getSustainFromInt(-1));
        assertNull(SustainStat.getSustainFromInt(-100));
        assertNull(SustainStat.getSustainFromStat(Stats.STAT_NONE));
    }

    @Test
    @DisplayName("C rejects stat >= STAT_MAX; the port answers null where C answers -1")
    void atOrAboveMaxAnswersNone() {
        assertNull(SustainStat.getSustainFromInt(5));
        assertNull(SustainStat.getSustainFromInt(6));
        assertNull(SustainStat.getSustainFromInt(Integer.MAX_VALUE));
        assertNull(SustainStat.getSustainFromStat(Stats.STAT_MAX));
    }

    @Test
    @DisplayName("a stat index is never answered with the MAX sentinel")
    void neverAnswersMax() {
        for (int i = -2; i <= 10; i++) {
            assertEquals(false, SustainStat.getSustainFromInt(i) == SustainStat.SUS_STAT_MAX,
                    "index " + i);
        }
    }

    @Test
    @DisplayName("a null stat throws (C has no equivalent)")
    void nullStatThrows() {
        assertThrows(NullPointerException.class, () -> SustainStat.getSustainFromStat(null));
    }

    @Test
    @DisplayName("the constants carry the object flag indices list-object-flags.h gives them")
    void constantValues() {
        assertEquals(0, SustainStat.SUS_STAT_NONE.getSustainValue());
        assertEquals(1, SustainStat.SUS_STAT_STR.getSustainValue());
        assertEquals(2, SustainStat.SUS_STAT_INT.getSustainValue());
        assertEquals(3, SustainStat.SUS_STAT_WIS.getSustainValue());
        assertEquals(4, SustainStat.SUS_STAT_DEX.getSustainValue());
        assertEquals(5, SustainStat.SUS_STAT_CON.getSustainValue());
        assertEquals(6, SustainStat.SUS_STAT_MAX.getSustainValue());
    }

    @Test
    @DisplayName("each sustain's value is the position of its OF_SUST_* flag in ObjectFlag")
    void valuesLineUpWithObjectFlags() {
        assertEquals(ObjectFlag.OF_NONE.ordinal(), SustainStat.SUS_STAT_NONE.getSustainValue());
        assertEquals(ObjectFlag.OF_SUST_STR.ordinal(), SustainStat.SUS_STAT_STR.getSustainValue());
        assertEquals(ObjectFlag.OF_SUST_INT.ordinal(), SustainStat.SUS_STAT_INT.getSustainValue());
        assertEquals(ObjectFlag.OF_SUST_WIS.ordinal(), SustainStat.SUS_STAT_WIS.getSustainValue());
        assertEquals(ObjectFlag.OF_SUST_DEX.ordinal(), SustainStat.SUS_STAT_DEX.getSustainValue());
        assertEquals(ObjectFlag.OF_SUST_CON.ordinal(), SustainStat.SUS_STAT_CON.getSustainValue());
    }
}
