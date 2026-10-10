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

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link MonsterRaceFlag} as the stand-in for C's {@code struct monster_flag} and
 * {@code monster_flag_table[]} ({@code monster.h}, {@code mon-util.c}).
 *
 * <p>Expected values come from the C, not from the Java: {@code EXPECTED} is the 85 {@code RF()}
 * rows of {@code list-mon-race-flags.h}, copied from the C file as {@code (name, RFT_ category,
 * desc)} in file order. In C the row position is the {@code RF_} index, so the ordinal check is
 * what proves the {@code index} member survived the fold into the enum constant, and the
 * category and description checks prove the {@code type} and {@code desc} members did. The
 * descriptions are quoted exactly as the C has them: none of them contains a US/UK spelling
 * difference.
 *
 * <p>Class MonsterRaceFlagTableTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class MonsterRaceFlagTableTest {

    /**
     * The {@code RF()} rows of {@code list-mon-race-flags.h}, in file order.
     *
     * <p>Field EXPECTED coded on 261010, commented in full on 261010.
     */
    private static final String[][] EXPECTED = {
            {"RF_NONE", "RFT_NONE", ""},
            {"RF_UNIQUE", "RFT_OBV", ""},
            {"RF_QUESTOR", "RFT_OBV", ""},
            {"RF_MALE", "RFT_OBV", ""},
            {"RF_FEMALE", "RFT_OBV", ""},
            {"RF_GROUP_AI", "RFT_OBV", ""},
            {"RF_NAME_COMMA", "RFT_OBV", ""},
            {"RF_CHAR_CLEAR", "RFT_DISP", ""},
            {"RF_ATTR_RAND", "RFT_DISP", ""},
            {"RF_ATTR_CLEAR", "RFT_DISP", ""},
            {"RF_ATTR_MULTI", "RFT_DISP", ""},
            {"RF_ATTR_FLICKER", "RFT_DISP", ""},
            {"RF_FORCE_DEPTH", "RFT_GEN", ""},
            {"RF_FORCE_SLEEP", "RFT_GEN", ""},
            {"RF_FORCE_EXTRA", "RFT_GEN", ""},
            {"RF_SEASONAL", "RFT_GEN", ""},
            {"RF_UNAWARE", "RFT_NOTE", ""},
            {"RF_MULTIPLY", "RFT_NOTE", ""},
            {"RF_REGENERATE", "RFT_NOTE", ""},
            {"RF_FRIGHTENED", "RFT_BEHAV", ""},
            {"RF_NEVER_BLOW", "RFT_BEHAV", ""},
            {"RF_NEVER_MOVE", "RFT_BEHAV", ""},
            {"RF_RAND_25", "RFT_BEHAV", ""},
            {"RF_RAND_50", "RFT_BEHAV", ""},
            {"RF_MIMIC_INV", "RFT_BEHAV", ""},
            {"RF_STUPID", "RFT_BEHAV", ""},
            {"RF_SMART", "RFT_BEHAV", ""},
            {"RF_SPIRIT", "RFT_BEHAV", ""},
            {"RF_POWERFUL", "RFT_BEHAV", ""},
            {"RF_ONLY_GOLD", "RFT_DROP", ""},
            {"RF_ONLY_ITEM", "RFT_DROP", ""},
            {"RF_DROP_40", "RFT_DROP", ""},
            {"RF_DROP_60", "RFT_DROP", ""},
            {"RF_DROP_1", "RFT_DROP", ""},
            {"RF_DROP_2", "RFT_DROP", ""},
            {"RF_DROP_3", "RFT_DROP", ""},
            {"RF_DROP_4", "RFT_DROP", ""},
            {"RF_DROP_GOOD", "RFT_DROP", ""},
            {"RF_DROP_GREAT", "RFT_DROP", ""},
            {"RF_DROP_20", "RFT_DROP", ""},
            {"RF_INVISIBLE", "RFT_DET", "invisible"},
            {"RF_COLD_BLOOD", "RFT_DET", "cold blooded"},
            {"RF_EMPTY_MIND", "RFT_DET", "not detected by telepathy"},
            {"RF_WEIRD_MIND", "RFT_DET", "rarely detected by telepathy"},
            {"RF_OPEN_DOOR", "RFT_ALTER", "open doors"},
            {"RF_BASH_DOOR", "RFT_ALTER", "bash down doors"},
            {"RF_PASS_WALL", "RFT_ALTER", "pass through walls"},
            {"RF_KILL_WALL", "RFT_ALTER", "bore through walls"},
            {"RF_SMASH_WALL", "RFT_ALTER", "smash walls"},
            {"RF_MOVE_BODY", "RFT_ALTER", "push past weaker monsters"},
            {"RF_KILL_BODY", "RFT_ALTER", "destroy weaker monsters"},
            {"RF_TAKE_ITEM", "RFT_ALTER", "pick up objects"},
            {"RF_KILL_ITEM", "RFT_ALTER", "destroy objects"},
            {"RF_CLEAR_WEB", "RFT_ALTER", "clear webs"},
            {"RF_PASS_WEB", "RFT_ALTER", "pass through webs"},
            {"RF_ORC", "RFT_RACE_N", "orc"},
            {"RF_TROLL", "RFT_RACE_N", "troll"},
            {"RF_GIANT", "RFT_RACE_N", "giant"},
            {"RF_DRAGON", "RFT_RACE_N", "dragon"},
            {"RF_DEMON", "RFT_RACE_N", "demon"},
            {"RF_ANIMAL", "RFT_RACE_A", "natural"},
            {"RF_EVIL", "RFT_RACE_A", "evil"},
            {"RF_UNDEAD", "RFT_RACE_A", "undead"},
            {"RF_NONLIVING", "RFT_RACE_A", "nonliving"},
            {"RF_METAL", "RFT_RACE_A", "metal"},
            {"RF_HURT_LIGHT", "RFT_VULN", "bright light"},
            {"RF_HURT_ROCK", "RFT_VULN", "rock remover"},
            {"RF_HURT_FIRE", "RFT_VULN_I", "fire"},
            {"RF_HURT_COLD", "RFT_VULN_I", "cold"},
            {"RF_IM_ACID", "RFT_RES", "acid"},
            {"RF_IM_ELEC", "RFT_RES", "lightning"},
            {"RF_IM_FIRE", "RFT_RES", "fire"},
            {"RF_IM_COLD", "RFT_RES", "cold"},
            {"RF_IM_POIS", "RFT_RES", "poison"},
            {"RF_IM_NETHER", "RFT_RES", "nether"},
            {"RF_IM_WATER", "RFT_RES", "water"},
            {"RF_IM_PLASMA", "RFT_RES", "plasma"},
            {"RF_IM_NEXUS", "RFT_RES", "nexus"},
            {"RF_IM_DISEN", "RFT_RES", "disenchantment"},
            {"RF_NO_FEAR", "RFT_PROT", "frightened"},
            {"RF_NO_STUN", "RFT_PROT", "stunned"},
            {"RF_NO_CONF", "RFT_PROT", "confused"},
            {"RF_NO_SLEEP", "RFT_PROT", "slept"},
            {"RF_NO_HOLD", "RFT_PROT", "held"},
            {"RF_NO_SLOW", "RFT_PROT", "slowed"},
    };

    /**
     * Every C row, by position: the constant at that ordinal has the same name, category and
     * description.
     *
     * <p>Function everyRowMatchesTheCTable coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("Every constant matches its row of list-mon-race-flags.h by index, type and desc")
    void everyRowMatchesTheCTable() {
        MonsterRaceFlag[] actual = MonsterRaceFlag.values();

        assertEquals(85, EXPECTED.length);
        assertEquals(EXPECTED.length, actual.length);
        for (int i = 0; i < EXPECTED.length; i++) {
            String name = EXPECTED[i][0];
            assertEquals(name, actual[i].name(), "name at index " + i);
            assertEquals(i, MonsterRaceFlag.valueOf(name).ordinal(), "index of " + name);
            assertEquals(MonsterRaceCategory.valueOf(EXPECTED[i][1]), actual[i].getCategory(),
                    "type of " + name);
            assertEquals(EXPECTED[i][2], actual[i].getDescription(), "desc of " + name);
        }
    }

    /**
     * RF_NONE is index 0 in C ({@code RF_NONE = 0}), the placeholder row under {@code RFT_NONE}
     * with an empty description.
     *
     * <p>Function placeholderRowIsIndexZero coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("RF_NONE is index 0 with RFT_NONE and an empty description")
    void placeholderRowIsIndexZero() {
        assertEquals(0, MonsterRaceFlag.RF_NONE.ordinal());
        assertEquals(MonsterRaceCategory.RFT_NONE, MonsterRaceFlag.RF_NONE.getCategory());
        assertEquals("", MonsterRaceFlag.RF_NONE.getDescription());
    }

    /**
     * In C only the detection, alteration, race, vulnerability, resistance and protection
     * categories carry lore wording; every flag in the other categories has {@code ""}.
     *
     * <p>Function descriptionIsEmptyExactlyForTheSilentCategories coded on 261010, commented in
     * full on 261010.
     */
    @Test
    @DisplayName("Descriptions are empty exactly for the categories C gives no lore wording")
    void descriptionIsEmptyExactlyForTheSilentCategories() {
        Set<MonsterRaceCategory> silent = EnumSet.of(MonsterRaceCategory.RFT_NONE,
                MonsterRaceCategory.RFT_OBV, MonsterRaceCategory.RFT_DISP,
                MonsterRaceCategory.RFT_GEN, MonsterRaceCategory.RFT_NOTE,
                MonsterRaceCategory.RFT_BEHAV, MonsterRaceCategory.RFT_DROP);

        for (MonsterRaceFlag f : MonsterRaceFlag.values()) {
            if (silent.contains(f.getCategory())) {
                assertEquals("", f.getDescription(), "desc of " + f);
            } else {
                assertTrue(!f.getDescription().isEmpty(), "desc of " + f + " should have wording");
            }
        }
    }

    /**
     * The last C row, {@code RF_NO_SLOW}, is the last constant, and nothing follows it (the
     * {@code RF_MAX} sentinel row has no Java counterpart).
     *
     * <p>Function lastRowIsNoSlowWithNoSentinel coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("The last constant is RF_NO_SLOW; there is no RF_MAX sentinel constant")
    void lastRowIsNoSlowWithNoSentinel() {
        MonsterRaceFlag[] all = MonsterRaceFlag.values();

        assertEquals(MonsterRaceFlag.RF_NO_SLOW, all[all.length - 1]);
        assertEquals(84, MonsterRaceFlag.RF_NO_SLOW.ordinal());
        for (MonsterRaceFlag f : all) {
            assertTrue(!f.name().equals("RF_MAX"));
        }
    }
}
