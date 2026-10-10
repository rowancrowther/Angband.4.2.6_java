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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@link PlayerClass#getStatsAdj}, {@link PlayerClass#getSkill} and
 * {@link PlayerClass#getXSkill} answer 0 for a stat or skill the class does not name, and the
 * scalar accessors {@link PlayerClass#getMaxHitDie()} and {@link PlayerClass#getExpFactor()}.
 *
 * <p>C allocates a class with {@code mem_zalloc}, so {@code c_adj}, {@code c_skills} and
 * {@code x_skills} are arrays of zeros that the {@code class.txt} lines then overwrite. A line left
 * out leaves a 0 that the calculations add without complaint. The port stores the same data in maps
 * that simply lack the key, so the accessors must supply the 0 themselves; an unboxing of a missing
 * key would throw where C reads a zero.
 *
 * <p>Class PlayerClassUnsetSlotsTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class PlayerClassUnsetSlotsTest {

    /**
     * A class with the given stat and skill maps.
     *
     * @param stats       the stat adjustments
     * @param classSkills the flat skills
     * @param extraSkills the per-level skills
     * @return the class
     */
    private static PlayerClass playerClass(Map<Stats, Integer> stats,
                                           Map<PlayerSkill, Integer> classSkills,
                                           Map<PlayerSkill, Integer> extraSkills) {
        return new PlayerClass("Tester", List.of(), stats, classSkills, extraSkills, 7, 3,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5,
                List.of(), ClassMagic.NONE);
    }

    /**
     * A class that names one stat and two skills: those read back, and everything else reads 0.
     * A negative adjustment is kept, as the Warrior's `-2` for intelligence is in the file.
     */
    @Test
    @DisplayName("named slots read back and unnamed slots read zero")
    void namedAndUnnamed() {
        PlayerClass c = playerClass(
                Map.of(Stats.STAT_INT, -2),
                Map.of(PlayerSkill.SKILL_DEVICE, 18),
                Map.of(PlayerSkill.SKILL_DEVICE, 7));

        assertAll(
                () -> assertEquals(-2, c.getStatsAdj(Stats.STAT_INT)),
                () -> assertEquals(0, c.getStatsAdj(Stats.STAT_STR)),
                () -> assertEquals(18, c.getSkill(PlayerSkill.SKILL_DEVICE)),
                () -> assertEquals(0, c.getSkill(PlayerSkill.SKILL_SAVE)),
                () -> assertEquals(7, c.getXSkill(PlayerSkill.SKILL_DEVICE)),
                () -> assertEquals(0, c.getXSkill(PlayerSkill.SKILL_SAVE)));
    }

    /**
     * A class with empty maps is C's all-zero struct: every read is 0 and none throws.
     */
    @Test
    @DisplayName("empty maps read zero everywhere")
    void emptyMaps() {
        PlayerClass c = playerClass(Map.of(), Map.of(), Map.of());

        assertAll(
                () -> assertDoesNotThrow(() -> c.getStatsAdj(Stats.STAT_CON)),
                () -> assertEquals(0, c.getStatsAdj(Stats.STAT_CON)),
                () -> assertEquals(0, c.getSkill(PlayerSkill.SKILL_DIGGING)),
                () -> assertEquals(0, c.getXSkill(PlayerSkill.SKILL_DIGGING)));
    }

    /**
     * C adds the class's `c_mhp` to the race's hit die and its `c_exp` to the race's experience
     * factor, so the accessors hand back the stored figures unchanged (7 and 3 here, distinct so a
     * swap shows).
     */
    @Test
    @DisplayName("the hit die and experience factor read their own fields")
    void hitDieAndExperienceFactor() {
        PlayerClass c = playerClass(Map.of(), Map.of(), Map.of());

        assertAll(
                () -> assertEquals(7, c.getMaxHitDie()),
                () -> assertEquals(3, c.getExpFactor()));
    }
}
