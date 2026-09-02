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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link EquipmentSlotsEnum}, the port of C's equipment-slot table
 * ({@code list-equip-slots.h}, read into an enum at {@code obj-gear.h:28-34} and a parallel
 * {@code slot_table} at {@code obj-gear.c:34-46}).
 *
 * <p><b>Order is behaviour here, not presentation.</b> C's constants are positions in a macro list
 * and its {@code slot_table} rows are matched to them by index, so a constant inserted or moved in
 * the port silently disagrees with the C the rest of the port is being written against.
 * {@link EquipmentSlotsEnum#EQUIP_MAX} in particular has to stay last: it is C's hand-written
 * sentinel and code that iterates the enum skips it by name, so a constant added after it would be
 * quietly excluded from every such loop.
 *
 * <p>The other thing worth pinning is which slots are armour. Two calculations turn on it —
 * {@code calcBonuses} withholds to-hit and to-damage from the weapon and launcher slots
 * ({@code player-calcs.c:1997-1998}), and mana encumbrance weighs everything except those two plus
 * rings, amulet and light ({@code player-calcs.c:1511-1519}). Both are expressed as exclusions of
 * named types, so the set of names has to be right.
 *
 * <p>Class EquipmentSlotsEnumTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class EquipmentSlotsEnumTest {

    /**
     * The full list, in C's order, sentinel last. Written out rather than counted so that a
     * reordering names the constant that moved.
     */
    @Test
    @DisplayName("the constants match list-equip-slots.h, in order, with the sentinel last")
    void constantsMatchC() {
        EquipmentSlotsEnum[] expected = {
                EquipmentSlotsEnum.EQUIP_NONE,
                EquipmentSlotsEnum.EQUIP_WEAPON,
                EquipmentSlotsEnum.EQUIP_BOW,
                EquipmentSlotsEnum.EQUIP_RING,
                EquipmentSlotsEnum.EQUIP_AMULET,
                EquipmentSlotsEnum.EQUIP_LIGHT,
                EquipmentSlotsEnum.EQUIP_BODY_ARMOR,
                EquipmentSlotsEnum.EQUIP_CLOAK,
                EquipmentSlotsEnum.EQUIP_SHIELD,
                EquipmentSlotsEnum.EQUIP_HAT,
                EquipmentSlotsEnum.EQUIP_GLOVES,
                EquipmentSlotsEnum.EQUIP_BOOTS,
                EquipmentSlotsEnum.EQUIP_MAX};
        EquipmentSlotsEnum[] actual = EquipmentSlotsEnum.values();

        assertAll(
                () -> assertEquals(expected.length, actual.length),
                () -> {
                    for (int i = 0; i < expected.length; i++) {
                        assertSame(expected[i], actual[i], "position " + i);
                    }
                },
                () -> assertSame(EquipmentSlotsEnum.EQUIP_MAX, actual[actual.length - 1],
                        "the sentinel must stay last"));
    }

    /**
     * The five slots the mana calculation ignores are the five C names, and every remaining slot
     * bar the two sentinels is armour it weighs. Expressed the way the calculation expresses it —
     * as an exclusion — so that a slot added later falls on the armour side by default, as it would
     * in C.
     */
    @Test
    @DisplayName("the non-armour slots are exactly the five the mana calculation excludes")
    void nonArmourSlots() {
        Set<EquipmentSlotsEnum> excluded = EnumSet.of(
                EquipmentSlotsEnum.EQUIP_WEAPON,
                EquipmentSlotsEnum.EQUIP_BOW,
                EquipmentSlotsEnum.EQUIP_RING,
                EquipmentSlotsEnum.EQUIP_AMULET,
                EquipmentSlotsEnum.EQUIP_LIGHT);

        Set<EquipmentSlotsEnum> armour = EnumSet.allOf(EquipmentSlotsEnum.class);
        armour.removeAll(excluded);
        armour.remove(EquipmentSlotsEnum.EQUIP_NONE);
        armour.remove(EquipmentSlotsEnum.EQUIP_MAX);

        assertAll(
                () -> assertEquals(6, armour.size(), "six armour slots on a standard body"),
                () -> assertTrue(armour.contains(EquipmentSlotsEnum.EQUIP_BODY_ARMOR)),
                () -> assertTrue(armour.contains(EquipmentSlotsEnum.EQUIP_BOOTS)),
                () -> assertFalse(armour.contains(EquipmentSlotsEnum.EQUIP_WEAPON)));
    }

    /**
     * The two slots {@code calcBonuses} withholds to-hit and to-damage from are distinct constants
     * and neither is the sentinel — the test exists because an earlier version of the port also
     * excluded {@code EQUIP_NONE} and {@code EQUIP_MAX}, which C does not.
     */
    @Test
    @DisplayName("weapon and bow are the only two slots the combat bonuses skip")
    void combatExcludedSlots() {
        assertAll(
                () -> assertFalse(EquipmentSlotsEnum.EQUIP_WEAPON == EquipmentSlotsEnum.EQUIP_BOW),
                () -> assertFalse(EquipmentSlotsEnum.EQUIP_WEAPON == EquipmentSlotsEnum.EQUIP_MAX),
                () -> assertFalse(EquipmentSlotsEnum.EQUIP_BOW == EquipmentSlotsEnum.EQUIP_NONE));
    }
}
