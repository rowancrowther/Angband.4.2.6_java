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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBody#copy}, which stands in for the copying half of C's {@code player_embody}
 * ({@code player-birth.c:369}).
 *
 * <p>C builds a player's body from the race's template with a {@code memcpy} of the struct followed
 * by a freshly allocated, zeroed slot array whose entries take their type and name from the
 * template. This method is both halves, which is what lets {@code PlayerBirth.embody} be one assignment.
 *
 * <p><b>Nothing may be shared with the source.</b> The source is normally a race's template, held
 * once in the registry and shared by every player of that race, so a copy that shared its list or
 * its slots would let one character wear another's equipment and would let a player write into the
 * game's own data. Most of the cases below are about that, because sharing is invisible until two
 * players exist and is exactly what a shallow copy would give.
 *
 * <p><b>The copy is unworn.</b> A slot is rebuilt from its type and name alone, so an item held in
 * the source is not carried across — "copy the plan", not "copy the body as it stands". C gets the
 * same from allocating the slot array with {@code mem_zalloc} rather than copying the old one.
 *
 * <p>{@link EquipSlot} has no setter for its item, so the fixture writes the field directly, as
 * {@code PlayerBodyEquippedTest} does. That is a statement about the class rather than about this
 * test: slots are filled by the wield code, which is not yet ported.
 *
 * <p>Class PlayerBodyCopyTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class PlayerBodyCopyTest {

    /**
     * The body being copied, a three-slot humanoid stand-in.
     */
    private PlayerBody source;

    /**
     * Fills a slot, standing in for the wield code that would normally do it.
     *
     * @param slot the slot to fill
     * @param item the item to put in it
     * @throws Exception if the field cannot be reached
     */
    private static void wear(EquipSlot slot, ItemObject item) throws Exception {
        Field f = EquipSlot.class.getDeclaredField("item");
        f.setAccessible(true);
        f.set(slot, item);
    }

    /**
     * @param slots the slots the body should have, in order
     * @return a body named "Humanoid" with those slots
     */
    private static PlayerBody bodyOf(EquipSlot... slots) {
        List<EquipSlot> list = new ArrayList<>(List.of(slots));
        return new PlayerBody("Humanoid", list);
    }

    @BeforeEach
    void setUp() {
        source = bodyOf(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_BOW, "shooting"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "on right hand"));
    }

    /**
     * The layout carried across: name, count, and each slot's type and name.
     */
    @Nested
    class TheLayoutIsCarriedAcross {

        @Test
        void theNameIsCopied() {
            assertEquals("Humanoid", source.copy().getName());
        }

        @Test
        void theSlotCountIsCopied() {
            assertEquals(3, source.copy().getCount());
        }

        @Test
        void eachSlotKeepsItsTypeAndName() {
            PlayerBody copy = source.copy();

            assertEquals(EquipmentSlotsEnum.EQUIP_WEAPON, copy.getSlot(0).getType());
            assertEquals("weapon", copy.getSlot(0).getName());
            assertEquals(EquipmentSlotsEnum.EQUIP_BOW, copy.getSlot(1).getType());
            assertEquals("shooting", copy.getSlot(1).getName());
            assertEquals(EquipmentSlotsEnum.EQUIP_RING, copy.getSlot(2).getType());
            assertEquals("on right hand", copy.getSlot(2).getName());
        }

        /**
         * Order is identity for a slot — C addresses them as {@code slots[i]} and the port's
         * {@code equippedItemSlot} hands the index back as the item's label — so the copy must not
         * reorder them.
         */
        @Test
        void theSlotOrderIsPreserved() {
            PlayerBody copy = source.copy();

            assertEquals("weapon", copy.getSlot(0).getName());
            assertEquals("shooting", copy.getSlot(1).getName());
            assertEquals("on right hand", copy.getSlot(2).getName());
        }

        @Test
        void aBodyLookedUpByNameOnTheCopyFindsTheSameSlot() {
            assertEquals(3, source.copy().getSlots().size());
            assertNull(source.copy().equippedItemBySlotName("weapon"));
        }

        @Test
        void aSingleSlotBodyCopiesAsWell() {
            PlayerBody single = bodyOf(new EquipSlot(EquipmentSlotsEnum.EQUIP_LIGHT, "light"));

            assertEquals(1, single.copy().getCount());
            assertEquals("light", single.copy().getSlot(0).getName());
        }
    }

    /**
     * Nothing is shared with the source — the point of the method.
     */
    @Nested
    class NothingIsSharedWithTheSource {

        @Test
        void theCopyIsADifferentBody() {
            assertNotSame(source, source.copy());
        }

        @Test
        void everySlotIsANewObject() {
            PlayerBody copy = source.copy();

            for (int index = 0; index < source.getCount(); index++) {
                assertNotSame(source.getSlot(index), copy.getSlot(index),
                        "slot " + index + " must not be shared with the template");
            }
        }

        @Test
        void theSlotListIsNotShared() {
            PlayerBody copy = source.copy();

            assertNotSame(source.getSlots(), copy.getSlots());
            assertEquals(source.getCount(), copy.getCount());
        }

        /**
         * Wearing something on the copy leaves the template untouched, which is what stops one
         * character's equipment appearing on another's body.
         */
        @Test
        void wearingOnTheCopyDoesNotAffectTheSource() throws Exception {
            PlayerBody copy = source.copy();
            ItemObject sword = new ItemObject();

            wear(copy.getSlot(0), sword);

            assertTrue(copy.itemIsEquipped(sword));
            assertFalse(source.itemIsEquipped(sword),
                    "the race's template must not gain the player's equipment");
            assertNull(source.getSlot(0).getItem());
        }

        /**
         * And the other way about, since the source is the object that outlives the copy.
         */
        @Test
        void wearingOnTheSourceDoesNotAffectAnEarlierCopy() throws Exception {
            PlayerBody copy = source.copy();
            ItemObject bow = new ItemObject();

            wear(source.getSlot(1), bow);

            assertFalse(copy.itemIsEquipped(bow));
            assertNull(copy.getSlot(1).getItem());
        }

        /**
         * Two copies of one template are independent of each other, which is the two-players case
         * the sharing would show up in.
         */
        @Test
        void twoCopiesOfOneTemplateAreIndependent() throws Exception {
            PlayerBody first = source.copy();
            PlayerBody second = source.copy();
            ItemObject ring = new ItemObject();

            wear(first.getSlot(2), ring);

            assertTrue(first.itemIsEquipped(ring));
            assertFalse(second.itemIsEquipped(ring));
            assertNotSame(first.getSlot(2), second.getSlot(2));
        }

        /**
         * Copying a copy is the same operation again, with no link back to the original.
         */
        @Test
        void aCopyOfACopyIsStillIndependent() throws Exception {
            PlayerBody copy = source.copy().copy();
            ItemObject sword = new ItemObject();

            wear(copy.getSlot(0), sword);

            assertFalse(source.itemIsEquipped(sword));
            assertEquals(3, copy.getCount());
        }

        /**
         * The name is a {@link String} and so may be shared safely — immutability is what makes the
         * absence of a defensive copy correct here.
         */
        @Test
        void theNameMayBeSharedBecauseStringsAreImmutable() {
            assertSame(source.getName(), source.copy().getName());
        }
    }

    /**
     * The copy is unworn, whatever the source is carrying.
     */
    @Nested
    class TheCopyIsUnworn {

        @Test
        void everySlotOfACopyStartsEmpty() {
            PlayerBody copy = source.copy();

            for (int index = 0; index < copy.getCount(); index++) {
                assertNull(copy.getSlot(index).getItem(), "slot " + index + " must start empty");
            }
        }

        @Test
        void anItemInTheSourceIsNotCarriedOver() throws Exception {
            ItemObject sword = new ItemObject();
            wear(source.getSlot(0), sword);

            PlayerBody copy = source.copy();

            assertTrue(source.itemIsEquipped(sword));
            assertFalse(copy.itemIsEquipped(sword),
                    "copy takes the plan, not the equipment");
            assertNull(copy.getSlot(0).getItem());
        }

        /**
         * A fully kitted-out source still copies to an empty body, so the method cannot be used to
         * duplicate a character's gear.
         */
        @Test
        void aFullyEquippedSourceCopiesToAnEmptyBody() throws Exception {
            for (int index = 0; index < source.getCount(); index++) {
                wear(source.getSlot(index), new ItemObject());
            }

            PlayerBody copy = source.copy();

            for (int index = 0; index < copy.getCount(); index++) {
                assertNull(copy.getSlot(index).getItem());
            }
            assertEquals(3, copy.getCount());
        }

        @Test
        void anEquippedSourceStillCopiesItsLayout() throws Exception {
            wear(source.getSlot(1), new ItemObject());

            PlayerBody copy = source.copy();

            assertEquals(EquipmentSlotsEnum.EQUIP_BOW, copy.getSlot(1).getType());
            assertEquals("shooting", copy.getSlot(1).getName());
        }
    }
}
