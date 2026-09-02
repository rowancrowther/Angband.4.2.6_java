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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link StatTables}, the hand-transcribed {@code adj_*} arrays from
 * {@code src/player-calcs.c}.
 *
 * <p>These are 684 numbers copied out of a C file, and a mistyped one is the quietest bug the port
 * can carry: nothing throws, nothing looks wrong, a character simply gets slightly the wrong
 * regeneration rate or carrying capacity for the rest of the game. Worse, every table here is
 * currently unread — the calculations that will consume them are not ported — so no other test
 * touches them and a transcription slip would sit undetected until the day something starts using
 * it and the numbers are long since trusted.
 *
 * <p>Two kinds of check, because they catch different mistakes:
 *
 * <ul>
 *   <li><b>Length.</b> Every table is indexed by a compressed stat index and must have exactly
 *       {@code STAT_RANGE} entries. A short table is an out-of-bounds waiting to happen at high
 *       stats; a long one means a stray value was inserted and every entry past it is shifted.</li>
 *   <li><b>Spot values.</b> Four probes per table — first, middle, three-quarters, last —
 *       transcribed from the C source. A shift or a typo anywhere in a table moves at least one of
 *       them.</li>
 * </ul>
 *
 * <p>The expected values below were read from {@code player-calcs.c}, not from
 * {@code StatTables.java}. That is the whole point: a check derived from the Java would agree with
 * any typo the Java contains. If one of these ever needs changing, re-read the C file.
 *
 * <p>Class StatTablesTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class StatTablesTest {

    /**
     * The number of compressed stat rungs every table must cover, C's {@code STAT_RANGE}.
     */
    private static final int STAT_RANGE = 38;

    /**
     * The indices probed in each table: first, middle, three-quarters, last.
     */
    private static final int[] PROBES = {0, 15, 24, 37};

    /**
     * Every table, by its C name, with the four probe values C gives at {@link #PROBES}.
     *
     * @return the C-side expectations
     */
    private static Map<String, int[]> expectedProbes() {
        Map<String, int[]> expected = new LinkedHashMap<>();
        expected.put("adj_con_fix", new int[]{0, 2, 3, 9});
        expected.put("adj_int_dev", new int[]{0, 3, 6, 13});
        expected.put("adj_wis_sav", new int[]{0, 3, 6, 19});
        expected.put("adj_dex_dis", new int[]{0, 3, 9, 19});
        expected.put("adj_int_dis", new int[]{0, 3, 9, 19});
        expected.put("adj_dex_ta", new int[]{-4, 2, 5, 15});
        expected.put("adj_str_td", new int[]{-2, 2, 5, 20});
        expected.put("adj_dex_th", new int[]{-3, 3, 5, 15});
        expected.put("adj_str_th", new int[]{-3, 1, 4, 15});
        expected.put("adj_str_wgt", new int[]{5, 20, 30, 30});
        expected.put("adj_str_hold", new int[]{4, 30, 70, 100});
        expected.put("adj_str_dig", new int[]{0, 9, 45, 100});
        expected.put("adj_str_blow", new int[]{3, 20, 110, 240});
        expected.put("adj_dex_blow", new int[]{0, 2, 6, 11});
        expected.put("adj_dex_safe", new int[]{0, 10, 45, 100});
        expected.put("adj_con_mhp", new int[]{-250, 150, 450, 1250});
        expected.put("adj_mag_study", new int[]{0, 110, 190, 250});
        expected.put("adj_mag_mana", new int[]{0, 150, 350, 800});
        return expected;
    }

    /**
     * Every table, by its C name, paired with the Java array porting it.
     *
     * @return the port-side tables
     */
    private static Map<String, int[]> tables() {
        Map<String, int[]> tables = new LinkedHashMap<>();
        tables.put("adj_con_fix", StatTables.adjConFix);
        tables.put("adj_int_dev", StatTables.adjIntDev);
        tables.put("adj_wis_sav", StatTables.adjWisSav);
        tables.put("adj_dex_dis", StatTables.adjDexDis);
        tables.put("adj_int_dis", StatTables.adjIntDis);
        tables.put("adj_dex_ta", StatTables.adjDexTa);
        tables.put("adj_str_td", StatTables.adjStrTd);
        tables.put("adj_dex_th", StatTables.adjDexTh);
        tables.put("adj_str_th", StatTables.adjStrTh);
        tables.put("adj_str_wgt", StatTables.adjStrWgt);
        tables.put("adj_str_hold", StatTables.adjStrHold);
        tables.put("adj_str_dig", StatTables.adjStrDig);
        tables.put("adj_str_blow", StatTables.adjStrBlow);
        tables.put("adj_dex_blow", StatTables.adjDexBlow);
        tables.put("adj_dex_safe", StatTables.adjDexSafe);
        tables.put("adj_con_mhp", StatTables.adjConMhp);
        tables.put("adj_mag_study", StatTables.adjMagStudy);
        tables.put("adj_mag_mana", StatTables.adjMagMana);
        return tables;
    }

    /**
     * Shape: every table covers exactly the compressed stat range.
     */
    @Nested
    class Shape {

        @Test
        void everyTableHasOneEntryPerCompressedStatRung() {
            tables().forEach((name, table) ->
                    assertEquals(STAT_RANGE, table.length, name + " length"));
        }

        @Test
        void everyTableCIsDeclaredHasBeenPorted() {
            assertEquals(expectedProbes().keySet(), tables().keySet());
        }
    }

    /**
     * Values, probed against the C source.
     */
    @Nested
    class Values {

        @Test
        void everyTableMatchesCAtTheProbedIndices() {
            Map<String, int[]> tables = tables();
            expectedProbes().forEach((name, expected) -> {
                int[] table = tables.get(name);
                for (int i = 0; i < PROBES.length; i++) {
                    assertEquals(expected[i], table[PROBES[i]],
                            name + "[" + PROBES[i] + "]");
                }
            });
        }

        /**
         * The tables rise with the stat, which no probe set can check on its own.
         *
         * <p>Every one of these is an adjustment that gets better as the stat gets better, so a
         * value that dips below its predecessor is a transcription slip however plausible it looks.
         * This catches a mistyped digit between two probes, which the probes themselves cannot.
         */
        @Test
        void noTableEverDecreasesAsTheStatRises() {
            tables().forEach((name, table) -> {
                for (int i = 1; i < table.length; i++) {
                    assertTrue(table[i] >= table[i - 1],
                            name + " dips at index " + i + ": " + table[i - 1] + " then " + table[i]);
                }
            });
        }

        /**
         * The one table read by ported code, checked at the value that code depends on.
         *
         * <p>{@code GameWorld.decreaseTimeouts} looks this up by CON index and adds one, so a
         * character with the lowest CON recovers at one point per turn rather than none.
         */
        @Test
        void theRegenerationTableStartsAtZeroSoTheWorstConStillRecovers() {
            assertEquals(0, StatTables.adjConFix[0]);
        }
    }
}
