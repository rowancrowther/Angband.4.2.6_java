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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests {@link Brand#copy()}. C has no counterpart to cross-reference — {@code obj->brands} is a
 * {@code bool} array indexed into the fixed global {@code brands[]} table, so C never duplicates a
 * {@code struct brand}, it only flips a bit — so these tests check the Java-only contract the
 * method exists to satisfy: a distinct instance, equal in every field, for
 * {@code ObjectUtils.copyBrands} to place in a destination set without sharing the source's
 * reference.
 *
 * @author Rowan Crowther
 */
class BrandCopyTest {

    @Test
    void copyIsEqualButNotSame() {
        Brand original = new Brand("FIRE_1", "fire brand", "burns",
                MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 2, 9);

        Brand copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    void copyPreservesEveryField() {
        Brand original = new Brand("COLD_2", "cold brand", "freezes",
                MonsterRaceFlag.RF_IM_COLD, MonsterRaceFlag.RF_HURT_COLD, 5, 4, 17);

        Brand copy = original.copy();

        assertEquals(original.getCode(), copy.getCode());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getMultiplier(), copy.getMultiplier());
        assertEquals(original.getPower(), copy.getPower());
        assertEquals(original.toString(), copy.toString());
    }

    @Test
    void copyToleratesNullFlags() {
        Brand original = new Brand("POIS_1", "poison brand", "poisons",
                null, null, 3, 2, 9);

        Brand copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original, copy);
    }

    @Test
    void copyHasSameHashCode() {
        Brand original = new Brand("FIRE_3", "fire brand", "burns",
                MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 2, 9);

        Brand copy = original.copy();

        assertEquals(original.hashCode(), copy.hashCode());
    }
}
