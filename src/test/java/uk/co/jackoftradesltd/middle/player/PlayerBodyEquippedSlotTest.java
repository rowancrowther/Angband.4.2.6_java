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
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.testsupport.Bodies;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerBody#equippedItemSlot}, the port of C's {@code equipped_item_slot}
 * ({@code player-calcs.c}), and the null-item behaviour of {@link PlayerBody#itemIsEquipped}, the
 * port of {@code object_is_equipped} ({@code obj-gear.c}).
 *
 * <p>Expected values come from the C functions. {@code equipped_item_slot} returns
 * {@code body.count} at once for a NULL item, and otherwise the index of the first slot holding that
 * pointer, or {@code body.count} when the loop runs off the end. {@code object_is_equipped} is
 * {@code object_slot(body, obj) < body.count}, and {@code object_slot} has no NULL early return, so
 * NULL matches the first empty slot: a body with a gap says a NULL item is equipped, and a full body
 * says it is not. The two functions therefore disagree about NULL, and one test below pins that.
 *
 * <p>{@code PlayerBodyEquippedTest} already covers the ordinary {@code itemIsEquipped} cases and
 * {@code PlayerBodyCopyTest} the copy; this class covers what they leave out.
 *
 * <p>Class PlayerBodyEquippedSlotTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class PlayerBodyEquippedSlotTest {

    private static EquipSlot slotOf(EquipmentSlotsEnum type, String name, ItemObject worn) {
        EquipSlot slot = new EquipSlot(type, name);
        slot.setItem(worn);
        return slot;
    }

    private static PlayerBody bodyOf(EquipSlot... slots) {
        return Bodies.of("test", new ArrayList<>(List.of(slots)));
    }

    /** Weapon, ring, ring, body — the ring pair being the C humanoid body's repeated type. */
    private static PlayerBody kitted(ItemObject weapon, ItemObject ringA, ItemObject ringB,
                                     ItemObject armour) {
        return bodyOf(
                slotOf(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon", weapon),
                slotOf(EquipmentSlotsEnum.EQUIP_RING, "right hand", ringA),
                slotOf(EquipmentSlotsEnum.EQUIP_RING, "left hand", ringB),
                slotOf(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "body", armour));
    }

    @Test
    @DisplayName("a worn item's index is the position of its slot")
    void wornItemReturnsItsSlotIndex() {
        ItemObject weapon = new ItemObject();
        ItemObject ringA = new ItemObject();
        ItemObject ringB = new ItemObject();
        ItemObject armour = new ItemObject();
        PlayerBody body = kitted(weapon, ringA, ringB, armour);

        assertEquals(0, body.equippedItemSlot(weapon));
        assertEquals(1, body.equippedItemSlot(ringA));
        assertEquals(2, body.equippedItemSlot(ringB));
        assertEquals(3, body.equippedItemSlot(armour));
    }

    @Test
    @DisplayName("an item worn past empty slots is still found at its own index")
    void emptySlotsStillCountTowardTheIndex() {
        ItemObject armour = new ItemObject();
        PlayerBody body = kitted(null, null, null, armour);

        assertEquals(3, body.equippedItemSlot(armour));
    }

    @Test
    @DisplayName("an item that is not worn returns the slot count")
    void unwornItemReturnsCount() {
        PlayerBody body = kitted(new ItemObject(), null, null, new ItemObject());

        assertEquals(4, body.equippedItemSlot(new ItemObject()));
        assertEquals(body.getCount(), body.equippedItemSlot(new ItemObject()));
    }

    @Test
    @DisplayName("an identical twin of a worn item is not found")
    void identicalTwinReturnsCount() {
        ItemObject worn = new ItemObject();
        PlayerBody body = kitted(worn, null, null, null);

        assertEquals(0, body.equippedItemSlot(worn));
        assertEquals(4, body.equippedItemSlot(new ItemObject()));
    }

    @Test
    @DisplayName("a null item returns the slot count, even on a body with a gap")
    void nullItemReturnsCountDespiteEmptySlots() {
        PlayerBody withGap = kitted(new ItemObject(), null, null, new ItemObject());
        PlayerBody full = kitted(new ItemObject(), new ItemObject(), new ItemObject(), new ItemObject());

        assertEquals(4, withGap.equippedItemSlot(null));
        assertEquals(4, full.equippedItemSlot(null));
    }

    @Test
    @DisplayName("an item held in two slots is found at the first")
    void firstMatchWins() {
        ItemObject shared = new ItemObject();
        PlayerBody body = kitted(null, shared, shared, null);

        assertEquals(1, body.equippedItemSlot(shared));
    }

    @Test
    @DisplayName("the last slot's index is count minus one, not count")
    void lastSlotIsDistinctFromNotWorn() {
        ItemObject last = new ItemObject();
        PlayerBody body = kitted(null, null, null, last);

        assertEquals(body.getCount() - 1, body.equippedItemSlot(last));
    }

    @Test
    @DisplayName("a single-slot body gives 0 for its item and 1 for anything else")
    void singleSlotBody() {
        ItemObject worn = new ItemObject();
        PlayerBody body = bodyOf(slotOf(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon", worn));

        assertEquals(0, body.equippedItemSlot(worn));
        assertEquals(1, body.equippedItemSlot(new ItemObject()));
        assertEquals(1, body.equippedItemSlot(null));
    }

    @Test
    @DisplayName("a null item counts as equipped while any slot is empty")
    void nullIsEquippedWhenThereIsAGap() {
        PlayerBody body = kitted(new ItemObject(), null, new ItemObject(), new ItemObject());

        assertTrue(body.itemIsEquipped(null));
    }

    @Test
    @DisplayName("a null item is not equipped when every slot is full")
    void nullIsNotEquippedWhenFull() {
        PlayerBody body = kitted(new ItemObject(), new ItemObject(), new ItemObject(), new ItemObject());

        assertFalse(body.itemIsEquipped(null));
    }

    /**
     * The C asymmetry: {@code equipped_item_slot(NULL)} returns the count at once, whereas
     * {@code object_is_equipped(NULL)} reaches {@code object_slot} and finds the empty slot. A caller
     * that tested the pair for a null item would get "equipped" from one and "no slot" from the other.
     */
    @Test
    @DisplayName("itemIsEquipped and equippedItemSlot disagree about null on a body with a gap")
    void nullDisagreementIsPreserved() {
        PlayerBody body = kitted(new ItemObject(), null, null, null);

        assertTrue(body.itemIsEquipped(null));
        assertEquals(body.getCount(), body.equippedItemSlot(null));
    }

    @Test
    @DisplayName("a worn item is equipped and its slot index is below the count")
    void equippedAgreesWithSlotIndex() {
        ItemObject ring = new ItemObject();
        PlayerBody body = kitted(null, ring, null, null);

        assertTrue(body.itemIsEquipped(ring));
        assertTrue(body.equippedItemSlot(ring) < body.getCount());
    }

    @Test
    @DisplayName("the 'not worn' index is not a valid argument to getSlot")
    void notWornIndexIsOutOfRangeForGetSlot() {
        PlayerBody body = kitted(null, null, null, null);

        assertThrows(IndexOutOfBoundsException.class, () -> body.getSlot(body.equippedItemSlot(new ItemObject())));
    }
}
