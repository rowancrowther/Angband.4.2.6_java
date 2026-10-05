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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the construction, accessors, {@link Slay#toString()} and {@link Slay#sameMonsterSlain} of
 * {@link Slay}, the port of C's {@code struct slay} ({@code object.h}) and of
 * {@code same_monsters_slain} ({@code obj-slays.c}). Copy and value equality are covered by
 * {@link SlayEqualsTest}.
 *
 * <p>Field values are taken from the shipped {@code lib/gamedata/slay.txt}, which is what C's
 * {@code parse_slay_*} functions would store for each record. The truth table for
 * {@code same_monsters_slain} comes from the body of the C function: race flags must be equal;
 * then both bases absent is a match, exactly one absent is not, and two present bases match when
 * their names are equal.
 *
 * <p>Class SlayTest coded on 261005, commented in full on 261005.
 */
@DisplayName("Slay construction, accessors and same-monsters test")
class SlayTest {

    /**
     * The {@code EVIL_2} record of {@code slay.txt}: race flag EVIL, multiplier 2, o-multiplier 18,
     * power 200, verbs smite and pierces.
     */
    private static Slay evil2() {
        return new Slay("EVIL_2", "evil creatures", null, "smite", "pierces",
                MonsterRaceFlag.RF_EVIL, 2, 18, 200);
    }

    @Test
    @DisplayName("the accessors return the slay.txt values for EVIL_2")
    void accessorsReturnStoredValues() {
        Slay evil = evil2();

        assertEquals("EVIL_2", evil.getCode());
        assertEquals("evil creatures", evil.getName());
        assertEquals(2, evil.getMultiplier());
        assertEquals(200, evil.getPower());
    }

    @Test
    @DisplayName("the accessors return the slay.txt values for DEMON_5, a kill rather than a slay")
    void accessorsForAKill() {
        Slay demon = new Slay("DEMON_5", "demons", null, "fiercely smite", "deeply pierces",
                MonsterRaceFlag.RF_DEMON, 5, 35, 120);

        assertEquals("DEMON_5", demon.getCode());
        assertEquals("demons", demon.getName());
        assertEquals(5, demon.getMultiplier());
        assertEquals(120, demon.getPower());
    }

    @Test
    @DisplayName("the constructor accepts every code in slay.txt")
    void constructorSplitsEveryShippedCode() {
        String[][] codes = {
                {"EVIL_2", "EVIL"}, {"ANIMAL_2", "ANIMAL"}, {"ORC_3", "ORC"}, {"TROLL_3", "TROLL"},
                {"GIANT_3", "GIANT"}, {"DEMON_3", "DEMON"}, {"DRAGON_3", "DRAGON"},
                {"UNDEAD_3", "UNDEAD"}, {"DEMON_5", "DEMON"}, {"DRAGON_5", "DRAGON"},
                {"UNDEAD_5", "UNDEAD"}};

        for (String[] entry : codes) {
            MonsterRaceFlag flag = MonsterRaceFlag.valueOf("RF_" + entry[1]);
            Slay slay = new Slay(entry[0], "n", null, "v", "v", flag, 2, 2, 100);
            assertEquals(entry[0], slay.getCode(), entry[0]);
        }
    }

    @Test
    @DisplayName("a code with no underscore, an unknown type or a non-numeric level is rejected")
    void constructorRejectsMalformedCodes() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                new Slay("EVIL", "n", null, "v", "v", MonsterRaceFlag.RF_EVIL, 2, 2, 100));
        assertThrows(IllegalArgumentException.class, () ->
                new Slay("NOSUCHFLAG_2", "n", null, "v", "v", MonsterRaceFlag.RF_EVIL, 2, 2, 100));
        assertThrows(NumberFormatException.class, () ->
                new Slay("EVIL_X", "n", null, "v", "v", MonsterRaceFlag.RF_EVIL, 2, 2, 100));
    }

    @Test
    @DisplayName("toString lists the nine stored fields and names the base")
    void toStringListsFields() {
        assertEquals("Slay{code='EVIL_2', name='evil creatures', base=null, meleeVerb='smite', "
                + "rangedVerb='pierces', raceFlag=RF_EVIL, multiplier=2, oMultiplier=18, "
                + "power=200}", evil2().toString());
    }

    @Test
    @DisplayName("same race flag and no base on either side: same monsters (C: both bases absent)")
    void sameFlagNoBases() {
        Slay a = evil2();
        Slay b = new Slay("EVIL_5", "wicked things", null, "slay", "shoot",
                MonsterRaceFlag.RF_EVIL, 5, 50, 300);

        assertTrue(a.sameMonsterSlain(b));
        assertTrue(b.sameMonsterSlain(a));
        assertTrue(a.sameMonsterSlain(a), "a slay kills the same monsters as itself");
    }

    @Test
    @DisplayName("different race flags: not the same monsters, whatever the other fields say")
    void differentFlags() {
        Slay evil = evil2();
        Slay animal = new Slay("ANIMAL_2", "evil creatures", null, "smite", "pierces",
                MonsterRaceFlag.RF_ANIMAL, 2, 18, 200);

        assertFalse(evil.sameMonsterSlain(animal));
        assertFalse(animal.sameMonsterSlain(evil));
    }

    @Test
    @DisplayName("same race flag, same base instance: same monsters (C: streq on equal names)")
    void sameFlagSameBase() {
        MonsterBase dragons = new MonsterBase("dragon");
        Slay a = new Slay("EVIL_2", "evil creatures", dragons, "smite", "pierces",
                MonsterRaceFlag.RF_EVIL, 2, 18, 200);
        Slay b = new Slay("EVIL_3", "other name", dragons, "hit", "hit",
                MonsterRaceFlag.RF_EVIL, 3, 30, 10);

        assertTrue(a.sameMonsterSlain(b));
    }

    @Test
    @DisplayName("same race flag, different bases: not the same monsters (C: streq on unequal names)")
    void sameFlagDifferentBases() {
        Slay a = new Slay("EVIL_2", "evil creatures", new MonsterBase("dragon"), "smite", "pierces",
                MonsterRaceFlag.RF_EVIL, 2, 18, 200);
        Slay b = new Slay("EVIL_2", "evil creatures", new MonsterBase("zephyr hound"), "smite",
                "pierces", MonsterRaceFlag.RF_EVIL, 2, 18, 200);

        assertFalse(a.sameMonsterSlain(b));
    }

    @Test
    @DisplayName("same race flag, a base on one side only: not the same monsters, in either order")
    void sameFlagOneBase() {
        Slay withBase = new Slay("EVIL_2", "evil creatures", new MonsterBase("dragon"), "smite",
                "pierces", MonsterRaceFlag.RF_EVIL, 2, 18, 200);

        assertFalse(withBase.sameMonsterSlain(evil2()));
        assertFalse(evil2().sameMonsterSlain(withBase));
    }

    @Test
    @DisplayName("two base-only slays (no race flag) on the same base are the same monsters")
    void baseOnlySlays() {
        MonsterBase dragons = new MonsterBase("dragon");
        Slay a = new Slay("EVIL_2", "dragons", dragons, "smite", "pierces", null, 2, 18, 200);
        Slay b = new Slay("EVIL_3", "wyrms", dragons, "smite", "pierces", null, 3, 30, 200);
        Slay c = new Slay("EVIL_2", "hounds", new MonsterBase("zephyr hound"), "smite", "pierces",
                null, 2, 18, 200);

        assertTrue(a.sameMonsterSlain(b), "null race flags are equal, bases are the same");
        assertFalse(a.sameMonsterSlain(c), "null race flags are equal, bases differ");
    }
}
