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
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link EquipSlot#setItem}, the port's encapsulation of C's plain field write to
 * {@code equip_slot.obj} ({@code player.h:154}) — there is no C function of its own to compare
 * against, only the many call sites that assign the field directly (e.g.
 * {@code player->body.slots[slot].obj = wielded;}, {@code obj-gear.c:991}).
 *
 * <p>Because the assignment carries no logic, the cases here are about what a plain field write
 * implies rather than any arithmetic: the stored reference is the one given (not a copy), a slot
 * can be emptied with {@code null} the same way C sets the pointer to {@code NULL}, a second call
 * replaces rather than merges with the first, and one slot's item is independent of another's.
 *
 * <p>Class EquipSlotSetItemTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
class EquipSlotSetItemTest {

    /**
     * The item given is the item returned — C aliases the same pointer rather than copying the
     * object it points to.
     */
    @Test
    void storesTheGivenInstance() {
        EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "wielding");
        ItemObject sword = new ItemObject();

        slot.setItem(sword);

        assertSame(sword, slot.getItem());
    }

    /**
     * {@code null} empties the slot, matching C's {@code obj = NULL} at the many call sites that
     * remove an item (e.g. {@code obj-gear.c:1068}).
     */
    @Test
    void nullEmptiesTheSlot() {
        EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "wielding");
        slot.setItem(new ItemObject());

        slot.setItem(null);

        assertNull(slot.getItem());
    }

    /**
     * A second call replaces the first outright; the write carries no bookkeeping of the outgoing
     * item; the caller decides what happens to it, in the port as in C.
     */
    @Test
    void aSecondCallReplacesTheFirst() {
        EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "wielding");
        ItemObject sword = new ItemObject();
        ItemObject dagger = new ItemObject();

        slot.setItem(sword);
        slot.setItem(dagger);

        assertSame(dagger, slot.getItem());
    }

    /**
     * Two slots are independent, as two elements of C's {@code slots} array would be — writing
     * one leaves the other exactly as it was.
     */
    @Test
    void slotsAreIndependentOfOneAnother() {
        EquipSlot weapon = new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "wielding");
        EquipSlot bow = new EquipSlot(EquipmentSlotsEnum.EQUIP_BOW, "shooting");
        ItemObject sword = new ItemObject();

        weapon.setItem(sword);

        assertNull(bow.getItem());
        assertSame(sword, weapon.getItem());
    }
}
