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
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceCategory;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link MonsterRaceCategory}, the port of C's {@code enum monster_flag_type} in
 * {@code monster.h}.
 *
 * <p>Expected values come from the C, not from the Java: the declaration order in
 * {@code monster.h} (which fixes each constant's integer value, with {@code RFT_NONE = 0} and
 * {@code RFT_MAX} last), and the per-category tally of the {@code RF()} rows in
 * {@code list-mon-race-flags.h}, counted from the C file. The tally matters because
 * {@code create_mon_flag_mask()} in {@code mon-util.c} builds the lore masks by selecting
 * every flag whose category matches, so a flag filed under the wrong category silently changes
 * what the lore screens print. The spot checks pin one flag per category, including the two
 * neighbouring vulnerability categories, which are the easiest pair to swap.
 *
 * <p>Class MonsterRaceCategoryTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class MonsterRaceCategoryTest {

    /**
     * The category order from {@code monster.h}: the ordinal is the C integer value.
     *
     * <p>Function ordinalsFollowCDeclarationOrder coded on 261008, commented in full on 261008.
     */
    @Test
    @DisplayName("Ordinals follow the C declaration order, RFT_NONE = 0 and RFT_MAX last")
    void ordinalsFollowCDeclarationOrder() {
        MonsterRaceCategory[] expected = {
                MonsterRaceCategory.RFT_NONE, MonsterRaceCategory.RFT_OBV,
                MonsterRaceCategory.RFT_DISP, MonsterRaceCategory.RFT_GEN,
                MonsterRaceCategory.RFT_NOTE, MonsterRaceCategory.RFT_BEHAV,
                MonsterRaceCategory.RFT_DROP, MonsterRaceCategory.RFT_DET,
                MonsterRaceCategory.RFT_ALTER, MonsterRaceCategory.RFT_RACE_N,
                MonsterRaceCategory.RFT_RACE_A, MonsterRaceCategory.RFT_VULN,
                MonsterRaceCategory.RFT_VULN_I, MonsterRaceCategory.RFT_RES,
                MonsterRaceCategory.RFT_PROT, MonsterRaceCategory.RFT_MAX};

        assertEquals(16, MonsterRaceCategory.values().length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(i, expected[i].ordinal(), "ordinal of " + expected[i]);
        }
        assertEquals(0, MonsterRaceCategory.RFT_NONE.ordinal());
        assertEquals(15, MonsterRaceCategory.RFT_MAX.ordinal());
    }

    /**
     * The per-category tally of the {@code RF()} rows in {@code list-mon-race-flags.h}.
     *
     * <p>Function flagCountsPerCategoryMatchC coded on 261008, commented in full
     * on 261008.
     */
    @Test
    @DisplayName("Flag counts per category match the RF() rows in list-mon-race-flags.h")
    void flagCountsPerCategoryMatchC() {
        Map<MonsterRaceCategory, Integer> expected = new EnumMap<>(MonsterRaceCategory.class);
        expected.put(MonsterRaceCategory.RFT_NONE, 1);
        expected.put(MonsterRaceCategory.RFT_OBV, 6);
        expected.put(MonsterRaceCategory.RFT_DISP, 5);
        expected.put(MonsterRaceCategory.RFT_GEN, 4);
        expected.put(MonsterRaceCategory.RFT_NOTE, 3);
        expected.put(MonsterRaceCategory.RFT_BEHAV, 10);
        expected.put(MonsterRaceCategory.RFT_DROP, 11);
        expected.put(MonsterRaceCategory.RFT_DET, 4);
        expected.put(MonsterRaceCategory.RFT_ALTER, 11);
        expected.put(MonsterRaceCategory.RFT_RACE_N, 5);
        expected.put(MonsterRaceCategory.RFT_RACE_A, 5);
        expected.put(MonsterRaceCategory.RFT_VULN, 2);
        expected.put(MonsterRaceCategory.RFT_VULN_I, 2);
        expected.put(MonsterRaceCategory.RFT_RES, 10);
        expected.put(MonsterRaceCategory.RFT_PROT, 6);

        int total = 0;
        for (Map.Entry<MonsterRaceCategory, Integer> e : expected.entrySet()) {
            long actual = Arrays.stream(MonsterRaceFlag.values())
                    .filter(f -> f.getCategory() == e.getKey()).count();
            assertEquals(e.getValue().longValue(), actual, "flags in " + e.getKey());
            total += e.getValue();
        }
        // 85 RF() rows in C, and no flag sits under RFT_MAX
        assertEquals(85, total);
        assertEquals(85, MonsterRaceFlag.values().length);
        assertEquals(0, Arrays.stream(MonsterRaceFlag.values())
                .filter(f -> f.getCategory() == MonsterRaceCategory.RFT_MAX).count());
    }

    /**
     * One flag per category, taken from the {@code RF()} rows in {@code list-mon-race-flags.h}.
     *
     * <p>Function spotChecksPinOneFlagPerCategory coded on 261008, commented in full on 261008.
     */
    @Test
    @DisplayName("One representative flag per category, including the paired vulnerability sets")
    void spotChecksPinOneFlagPerCategory() {
        assertEquals(MonsterRaceCategory.RFT_NONE, MonsterRaceFlag.RF_NONE.getCategory());
        assertEquals(MonsterRaceCategory.RFT_OBV, MonsterRaceFlag.RF_UNIQUE.getCategory());
        assertEquals(MonsterRaceCategory.RFT_DISP, MonsterRaceFlag.RF_ATTR_MULTI.getCategory());
        assertEquals(MonsterRaceCategory.RFT_GEN, MonsterRaceFlag.RF_SEASONAL.getCategory());
        assertEquals(MonsterRaceCategory.RFT_NOTE, MonsterRaceFlag.RF_MULTIPLY.getCategory());
        assertEquals(MonsterRaceCategory.RFT_BEHAV, MonsterRaceFlag.RF_SMART.getCategory());
        assertEquals(MonsterRaceCategory.RFT_DROP, MonsterRaceFlag.RF_DROP_GREAT.getCategory());
        assertEquals(MonsterRaceCategory.RFT_DET, MonsterRaceFlag.RF_COLD_BLOOD.getCategory());
        assertEquals(MonsterRaceCategory.RFT_ALTER, MonsterRaceFlag.RF_PASS_WEB.getCategory());
        assertEquals(MonsterRaceCategory.RFT_RACE_N, MonsterRaceFlag.RF_DRAGON.getCategory());
        assertEquals(MonsterRaceCategory.RFT_RACE_A, MonsterRaceFlag.RF_UNDEAD.getCategory());
        assertEquals(MonsterRaceCategory.RFT_VULN, MonsterRaceFlag.RF_HURT_LIGHT.getCategory());
        assertEquals(MonsterRaceCategory.RFT_VULN_I, MonsterRaceFlag.RF_HURT_FIRE.getCategory());
        assertEquals(MonsterRaceCategory.RFT_VULN_I, MonsterRaceFlag.RF_HURT_COLD.getCategory());
        assertEquals(MonsterRaceCategory.RFT_RES, MonsterRaceFlag.RF_IM_DISEN.getCategory());
        assertEquals(MonsterRaceCategory.RFT_PROT, MonsterRaceFlag.RF_NO_SLOW.getCategory());
    }
}
