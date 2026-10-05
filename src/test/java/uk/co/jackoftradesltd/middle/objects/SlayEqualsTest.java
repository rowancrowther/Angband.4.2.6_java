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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Slay#copy()}, {@link Slay#equals} and {@link Slay#hashCode()}, and how
 * {@code equals} relates to {@link Slay#sameMonsterSlain}, the port of C's
 * {@code same_monsters_slain} ({@code obj-slays.c}).
 *
 * <p>C has no counterpart for {@code equals}: its slays are indices into a global table, so
 * "the same slay" is the same index. The port's value equality is its own contract, and what is
 * pinned here is that a copy is interchangeable with its original inside a {@code Set<Slay>}, as
 * {@code ItemObject.addSlay} and {@code removeSlay} need. {@code same_monsters_slain} is C's rule
 * for grouping slays into one rune: equal race flag and equal base, with name, multiplier and
 * power ignored, so the expected values for that half come from C's function body.
 */
@DisplayName("Slay equality and copy")
class SlayEqualsTest {

    /**
     * An evil-monster slay with every field set to a known value.
     */
    private static Slay evilSlay() {
        return new Slay("EVIL_2", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100);
    }

    @Test
    @DisplayName("a copy is a distinct instance, equal to its original, with the same hash")
    void copyIsEqualButNotSame() {
        Slay original = evilSlay();
        Slay copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original, copy);
        assertEquals(copy, original, "equality is symmetric");
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    @DisplayName("a slay equals itself")
    void reflexive() {
        Slay slay = evilSlay();

        assertEquals(slay, slay);
    }

    @Test
    @DisplayName("null and other types are never equal")
    void nullAndForeignTypes() {
        Slay slay = evilSlay();

        assertNotEquals(null, slay);
        assertNotEquals("EVIL_2", slay);
    }

    @Test
    @DisplayName("a set holding the original finds the copy and removes the original by it")
    void setTreatsCopyAsSameEntry() {
        Slay original = evilSlay();
        Set<Slay> slays = new HashSet<>();
        slays.add(original);

        assertTrue(slays.contains(original.copy()));

        slays.add(original.copy());
        assertEquals(1, slays.size(), "adding a copy does not add a second entry");

        slays.remove(original.copy());
        assertTrue(slays.isEmpty(), "removing by a copy removes the original");
    }

    @Test
    @DisplayName("slays differing in any one field are not equal")
    void anySingleFieldDifferenceBreaksEquality() {
        Slay base = evilSlay();

        assertNotEquals(base, new Slay("EVIL_3", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100), "code, and with it the parsed level");
        assertNotEquals(base, new Slay("ANIMAL_2", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100), "code, and with it the parsed type");
        assertNotEquals(base, new Slay("EVIL_2", "other name", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100), "name");
        assertNotEquals(base, new Slay("EVIL_2", "evil creatures", null, "hit", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100), "melee verb");
        assertNotEquals(base, new Slay("EVIL_2", "evil creatures", null, "smite", "shoot",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100), "ranged verb");
        assertNotEquals(base, new Slay("EVIL_2", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_ANIMAL, 2, 16, 100), "race flag");
        assertNotEquals(base, new Slay("EVIL_2", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 3, 16, 100), "multiplier");
        assertNotEquals(base, new Slay("EVIL_2", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 24, 100), "O-combat multiplier");
        assertNotEquals(base, new Slay("EVIL_2", "evil creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 99), "power");
    }

    @Test
    @DisplayName("a different base breaks equality; the same base instance keeps it")
    void baseParticipates() {
        MonsterBase dragons = new MonsterBase("dragon");
        MonsterBase hounds = new MonsterBase("zephyr hound");
        Slay withDragons = new Slay("EVIL_2", "evil creatures", dragons, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100);

        assertEquals(withDragons, withDragons.copy(), "the copy shares the base reference");
        assertNotEquals(withDragons, new Slay("EVIL_2", "evil creatures", hounds, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 2, 16, 100));
        assertNotEquals(withDragons, evilSlay(), "a base against no base");
    }

    @Test
    @DisplayName("sameMonsterSlain ignores name, multiplier and power, as C's same_monsters_slain does")
    void sameMonsterSlainIsLooserThanEquals() {
        Slay weak = evilSlay();
        Slay strong = new Slay("EVIL_3", "wicked creatures", null, "smite", "pierce",
                MonsterRaceFlag.RF_EVIL, 3, 24, 250);

        assertTrue(weak.sameMonsterSlain(strong), "same race flag and both bases null");
        assertTrue(strong.sameMonsterSlain(weak));
        assertFalse(weak.equals(strong), "yet the two are different slays");
    }

    @Test
    @DisplayName("sameMonsterSlain is false for a different race flag")
    void sameMonsterSlainNeedsSameRaceFlag() {
        Slay evil = evilSlay();
        Slay animal = new Slay("ANIMAL_2", "animals", null, "smite", "pierce",
                MonsterRaceFlag.RF_ANIMAL, 2, 16, 100);

        assertFalse(evil.sameMonsterSlain(animal));
    }

    @Test
    @DisplayName("sameMonsterSlain is false for a base against no base, in either order")
    void sameMonsterSlainBaseAgainstNone() {
        Slay withBase = new Slay("EVIL_2", "evil creatures", new MonsterBase("dragon"), "smite",
                "pierce", MonsterRaceFlag.RF_EVIL, 2, 16, 100);
        Slay noBase = evilSlay();

        assertFalse(withBase.sameMonsterSlain(noBase));
        assertFalse(noBase.sameMonsterSlain(withBase));
    }
}
