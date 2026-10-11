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

package uk.co.jackoftradesltd.middle.monsters.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link MonsterSpell} as the stand-in for C's {@code RSF_*} list
 * ({@code list-mon-spells.h}).
 *
 * <p>Expected values come from the C, not from the Java: {@code EXPECTED} is the 93 {@code RSF()}
 * rows of {@code list-mon-spells.h}, copied from the C file as {@code (name, RST_ types)} in file
 * order, with the types joined by {@code |} and an empty string for C's {@code 0}. In C the row
 * position is the {@code RSF_} index, so the ordinal check pins every constant to where C puts
 * it. {@code RSF_NONE} and {@code RSF_MAX} are rows of the C file and constants here, so an extra
 * constant anywhere in the enum shifts every later ordinal and fails the row count and the
 * name check at the first row it displaces.
 *
 * <p>This test exists because the enum once carried 19 short-name breath constants
 * ({@code RSF_ACID} to {@code RSF_MANA}) that C does not have, which put every ordinal after
 * them 19 too high.
 *
 * <p>Class MonsterSpellTableTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class MonsterSpellTableTest {

    /**
     * The {@code RSF()} rows of {@code list-mon-spells.h}, in file order.
     *
     * <p>Field EXPECTED coded on 261011, commented in full on 261011.
     */
    private static final String[][] EXPECTED = {
            {"RSF_NONE", ""},
            {"RSF_SHRIEK", "RST_ANNOY|RST_INNATE"},
            {"RSF_WHIP", "RST_BOLT|RST_INNATE"},
            {"RSF_SPIT", "RST_BOLT|RST_INNATE"},
            {"RSF_SHOT", "RST_BOLT|RST_INNATE|RST_ARCHERY"},
            {"RSF_ARROW", "RST_BOLT|RST_INNATE|RST_ARCHERY"},
            {"RSF_BOLT", "RST_BOLT|RST_INNATE|RST_ARCHERY"},
            {"RSF_BR_ACID", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_ELEC", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_FIRE", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_COLD", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_POIS", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_NETH", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_LIGHT", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_DARK", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_SOUN", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_CHAO", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_DISE", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_NEXU", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_TIME", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_INER", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_GRAV", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_SHAR", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_PLAS", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_WALL", "RST_BREATH|RST_INNATE"},
            {"RSF_BR_MANA", "RST_BREATH|RST_INNATE"},
            {"RSF_BOULDER", "RST_BOLT|RST_INNATE"},
            {"RSF_WEAVE", "RST_ANNOY|RST_INNATE"},
            {"RSF_BA_ACID", "RST_BALL"},
            {"RSF_BA_ELEC", "RST_BALL"},
            {"RSF_BA_FIRE", "RST_BALL"},
            {"RSF_BA_COLD", "RST_BALL"},
            {"RSF_BA_POIS", "RST_BALL"},
            {"RSF_BA_SHAR", "RST_BALL"},
            {"RSF_BA_NETH", "RST_BALL"},
            {"RSF_BA_WATE", "RST_BALL"},
            {"RSF_BA_MANA", "RST_BALL"},
            {"RSF_BA_HOLY", "RST_BALL"},
            {"RSF_BA_DARK", "RST_BALL"},
            {"RSF_BA_LIGHT", "RST_BALL"},
            {"RSF_STORM", "RST_BALL"},
            {"RSF_DRAIN_MANA", "RST_ANNOY"},
            {"RSF_MIND_BLAST", "RST_DIRECT|RST_ANNOY"},
            {"RSF_BRAIN_SMASH", "RST_DIRECT|RST_ANNOY"},
            {"RSF_WOUND", "RST_DIRECT"},
            {"RSF_BO_ACID", "RST_BOLT"},
            {"RSF_BO_ELEC", "RST_BOLT"},
            {"RSF_BO_FIRE", "RST_BOLT"},
            {"RSF_BO_COLD", "RST_BOLT"},
            {"RSF_BO_POIS", "RST_BOLT"},
            {"RSF_BO_NETH", "RST_BOLT"},
            {"RSF_BO_WATE", "RST_BOLT"},
            {"RSF_BO_MANA", "RST_BOLT"},
            {"RSF_BO_PLAS", "RST_BOLT"},
            {"RSF_BO_ICE", "RST_BOLT"},
            {"RSF_MISSILE", "RST_BOLT"},
            {"RSF_BE_ELEC", "RST_BALL"},
            {"RSF_BE_NETH", "RST_BALL"},
            {"RSF_SCARE", "RST_ANNOY"},
            {"RSF_BLIND", "RST_ANNOY"},
            {"RSF_CONF", "RST_ANNOY"},
            {"RSF_SLOW", "RST_ANNOY|RST_HASTE"},
            {"RSF_HOLD", "RST_ANNOY|RST_HASTE"},
            {"RSF_HASTE", "RST_HASTE"},
            {"RSF_HEAL", "RST_HEAL"},
            {"RSF_HEAL_KIN", "RST_HEAL_OTHER"},
            {"RSF_BLINK", "RST_TACTIC|RST_ESCAPE"},
            {"RSF_TPORT", "RST_ESCAPE"},
            {"RSF_TELE_TO", "RST_ANNOY"},
            {"RSF_TELE_SELF_TO", "RST_ANNOY"},
            {"RSF_TELE_AWAY", "RST_ESCAPE"},
            {"RSF_TELE_LEVEL", "RST_ESCAPE"},
            {"RSF_DARKNESS", "RST_ANNOY"},
            {"RSF_TRAPS", "RST_ANNOY"},
            {"RSF_FORGET", "RST_ANNOY"},
            {"RSF_SHAPECHANGE", "RST_TACTIC"},
            {"RSF_S_KIN", "RST_SUMMON"},
            {"RSF_S_HI_DEMON", "RST_SUMMON"},
            {"RSF_S_MONSTER", "RST_SUMMON"},
            {"RSF_S_MONSTERS", "RST_SUMMON"},
            {"RSF_S_ANIMAL", "RST_SUMMON"},
            {"RSF_S_SPIDER", "RST_SUMMON"},
            {"RSF_S_HOUND", "RST_SUMMON"},
            {"RSF_S_HYDRA", "RST_SUMMON"},
            {"RSF_S_AINU", "RST_SUMMON"},
            {"RSF_S_DEMON", "RST_SUMMON"},
            {"RSF_S_UNDEAD", "RST_SUMMON"},
            {"RSF_S_DRAGON", "RST_SUMMON"},
            {"RSF_S_HI_UNDEAD", "RST_SUMMON"},
            {"RSF_S_HI_DRAGON", "RST_SUMMON"},
            {"RSF_S_WRAITH", "RST_SUMMON"},
            {"RSF_S_UNIQUE", "RST_SUMMON"},
            {"RSF_MAX", ""},
    };

    /**
     * Every C row, by position: the constant at that ordinal has the same name and the same set
     * of spell types.
     *
     * <p>Function everyRowMatchesTheCTable coded on 261011, commented in full on 261011.
     */
    @Test
    @DisplayName("Every constant matches its row of list-mon-spells.h by index and types")
    void everyRowMatchesTheCTable() {
        MonsterSpell[] actual = MonsterSpell.values();

        assertEquals(93, EXPECTED.length);
        assertEquals(EXPECTED.length, actual.length);
        for (int i = 0; i < EXPECTED.length; i++) {
            String name = EXPECTED[i][0];
            assertEquals(name, actual[i].name(), "name at index " + i);
            assertEquals(i, MonsterSpell.valueOf(name).ordinal(), "index of " + name);
            Set<MonsterSpellTypeEnum> types = EnumSet.noneOf(MonsterSpellTypeEnum.class);
            types.addAll(actual[i].getTypes().toList());
            assertEquals(typesOf(EXPECTED[i][1]), types, "types of " + name);
        }
    }

    /**
     * RSF_NONE is index 0 and RSF_MAX is the last row, both with no types, as in C.
     *
     * <p>Function sentinelRowsBracketTheTable coded on 261011, commented in full on 261011.
     */
    @Test
    @DisplayName("RSF_NONE is index 0 and RSF_MAX is last, both with no types")
    void sentinelRowsBracketTheTable() {
        MonsterSpell[] all = MonsterSpell.values();

        assertEquals(MonsterSpell.RSF_NONE, all[0]);
        assertEquals(MonsterSpell.RSF_MAX, all[all.length - 1]);
        assertEquals(92, MonsterSpell.RSF_MAX.ordinal());
        assertTrue(MonsterSpell.RSF_NONE.getTypes().isEmpty());
        assertTrue(MonsterSpell.RSF_MAX.getTypes().isEmpty());
    }

    /**
     * C's {@code RST_DAMAGE} is {@code RST_BOLT | RST_BALL | RST_BREATH | RST_DIRECT}; the Java
     * type's {@code isDamage()} must be true for exactly those four.
     *
     * <p>Function damageTypesAreBoltBallBreathDirect coded on 261011, commented in full on
     * 261011.
     */
    @Test
    @DisplayName("isDamage is true for exactly the types in C's RST_DAMAGE")
    void damageTypesAreBoltBallBreathDirect() {
        Set<MonsterSpellTypeEnum> damage = EnumSet.of(MonsterSpellTypeEnum.RST_BOLT,
                MonsterSpellTypeEnum.RST_BALL, MonsterSpellTypeEnum.RST_BREATH,
                MonsterSpellTypeEnum.RST_DIRECT);

        for (MonsterSpellTypeEnum t : MonsterSpellTypeEnum.values()) {
            assertEquals(damage.contains(t), t.isDamage(), "isDamage of " + t);
        }
    }

    /**
     * Turn a {@code |}-joined list of {@code RST_} names into a set.
     *
     * <p>Function typesOf coded on 261011, commented in full on 261011.
     *
     * @param joined the types, e.g. {@code "RST_BOLT|RST_INNATE"}, or {@code ""} for none
     * @return the matching set, empty for {@code ""}
     */
    private static Set<MonsterSpellTypeEnum> typesOf(String joined) {
        Set<MonsterSpellTypeEnum> set = EnumSet.noneOf(MonsterSpellTypeEnum.class);

        if (!joined.isEmpty()) {
            for (String name : joined.split("\\|")) {
                set.add(MonsterSpellTypeEnum.valueOf(name));
            }
        }
        return set;
    }
}
