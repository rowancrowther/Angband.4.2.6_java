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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.testsupport.Bodies;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the construction and read side of {@link EquipSlot}: the constructor, {@code getType},
 * {@code getName} and {@code getItem}, against C's {@code struct equip_slot} ({@code player.h}).
 *
 * <p>C has no constructor. A slot is {@code mem_zalloc} memory, so {@code obj} starts
 * {@code NULL}, and {@code type} and {@code name} are then assigned by {@code parse_body_slot}
 * ({@code init.c}) and by {@code player_embody} ({@code player-birth.c}). The expected values here
 * come from that: a fresh slot is empty whatever its type, and the type and the name are exactly
 * the two values assigned. The slot names used are the ones in C's {@code lib/gamedata/body.txt}
 * ({@code weapon}, {@code left hand}, {@code body}); C's display wording ("Wielding", "On %s")
 * lives in {@code slot_table} and is not the name.
 *
 * <p>Writing the item is covered by {@link EquipSlotSetItemTest}.
 *
 * <p>Class EquipSlotConstructionTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class EquipSlotConstructionTest {

    /**
     * Every slot kind starts empty. C zero-fills the slot, so {@code obj} is {@code NULL} for
     * every {@code type}; the sentinels {@code EQUIP_NONE} and {@code EQUIP_MAX} are included
     * because the constructor does no validation of its own.
     *
     * @param kind each constant of {@link EquipmentSlotsEnum}
     */
    @ParameterizedTest
    @EnumSource(EquipmentSlotsEnum.class)
    void aNewSlotIsEmptyForEveryKind(EquipmentSlotsEnum kind) {
        EquipSlot slot = new EquipSlot(kind, "any");

        assertNull(slot.getItem());
        assertSame(kind, slot.getType());
    }

    /**
     * The type and name given are the ones read back, as for C's two assignments in
     * {@code parse_body_slot}.
     */
    @Test
    void typeAndNameAreWhatWasGiven() {
        EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "body");

        assertSame(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, slot.getType());
        assertEquals("body", slot.getName());
    }

    /**
     * Two slots may share a type and differ only by name, as the humanoid body's two
     * {@code RING} slots do ({@code body.txt}): the name is the only thing that tells them apart.
     */
    @Test
    void twoSlotsOfOneTypeKeepTheirOwnNames() {
        EquipSlot right = new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "right hand");
        EquipSlot left = new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "left hand");

        assertSame(right.getType(), left.getType());
        assertEquals("right hand", right.getName());
        assertEquals("left hand", left.getName());
    }

    /**
     * Wearing and removing an item changes only the item. C's wield and take-off code writes
     * {@code obj} and leaves {@code type} and {@code name} alone.
     */
    @Test
    void settingTheItemLeavesTypeAndNameAlone() {
        EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon");

        slot.setItem(new ItemObject());
        slot.setItem(null);

        assertSame(EquipmentSlotsEnum.EQUIP_WEAPON, slot.getType());
        assertEquals("weapon", slot.getName());
    }

    /**
     * {@code PlayerBody#copy}, the port's {@code player_embody}, gives each copied slot the same
     * type and name and no item, even when the original slot is occupied.
     */
    @Test
    void aBodyCopyGivesEmptySlotsWithTheSameTypeAndName() {
        EquipSlot original = new EquipSlot(EquipmentSlotsEnum.EQUIP_LIGHT, "light");
        original.setItem(new ItemObject());
        PlayerBody body = Bodies.of("Humanoid", new java.util.ArrayList<>(java.util.List.of(original)));

        EquipSlot copied = body.copy().getSlot(0);

        assertSame(EquipmentSlotsEnum.EQUIP_LIGHT, copied.getType());
        assertEquals("light", copied.getName());
        assertNull(copied.getItem());
    }
}
