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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectUtils;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Player}'s equipment slot lookups — the port of C's {@code slot_by_type},
 * {@code slot_by_name} and the index helpers around them ({@code obj-gear.c:71}).
 *
 * <p>{@link ObjectUtils#slotByType} is the one that earns a test of every path. C's loop variable outlives
 * its loop, so a pass that finds nothing leaves the counter one past the end and the closing ternary
 * yields the fallback; Java's cannot, and the port stands a separate counter in for it. That counter
 * has to produce three different answers — the index it stopped at, the count when it ran off the
 * end, and the fallback when the body holds no slot of that type at all — and every one of them is
 * reachable only through a differently shaped body. So each is built and asserted here.
 *
 * <p>Two ring slots is the case the fallback exists for: with both rings worn, asking for an empty
 * ring slot must still answer a ring slot, so a caller wanting to swap has somewhere to put the new
 * one.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerSlotLookupTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The slot ordering of {@code body.txt}'s Humanoid, which the tests index against.
     *
     * @return the body
     */
    private static PlayerBody humanoidBody() {
        return new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_BOW, "shooting"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "right hand"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "left hand"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_AMULET, "neck"))));
    }

    /**
     * A player with the standard humanoid body: weapon, shooting, two rings, then the rest.
     *
     * @throws Exception if the body cannot be installed
     */
    @BeforeEach
    void newPlayer() throws Exception {
        player = new Player();
        setBody(humanoidBody());
    }

    /**
     * Installs a body on the player under test.
     *
     * @param body the body to install
     * @throws Exception if the field cannot be reached
     */
    private void setBody(PlayerBody body) throws Exception {
        Field field = Player.class.getDeclaredField("body");
        field.setAccessible(true);
        field.set(player, body);
    }

    /**
     * Fills a slot, standing in for the wield code.
     *
     * @param index the slot's index
     * @param item  the item to put in it
     * @throws Exception if the field cannot be reached
     */
    private void wear(int index, ItemObject item) throws Exception {
        Field field = EquipSlot.class.getDeclaredField("item");
        field.setAccessible(true);
        field.set(player.getPlayerBody().getSlots().get(index), item);
    }

    /**
     * The three answers {@code slotByType} can give.
     */
    @Nested
    @DisplayName("slotByType")
    class ByType {

        /**
         * With the slot empty, asking for an empty one answers that slot — the ordinary case, and
         * the one that stops at index 1 rather than 0, so an off-by-one in the counter shows.
         */
        @Test
        @DisplayName("an empty slot of the type is found at its own index")
        void emptySlotFound() {
            assertEquals(1, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_BOW, false));
            assertEquals(0, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_WEAPON, false));
        }

        /**
         * Asking for a full slot when it is full answers the same index, which is the other way
         * through the same loop.
         *
         * @throws Exception if a slot cannot be filled
         */
        @Test
        @DisplayName("a full slot of the type is found when one is asked for")
        void fullSlotFound() throws Exception {
            wear(1, new ItemObject());

            assertEquals(1, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_BOW, true));
        }

        /**
         * The first empty slot wins, not the last: with the right hand full and the left empty,
         * asking for an empty ring slot answers the left.
         *
         * @throws Exception if a slot cannot be filled
         */
        @Test
        @DisplayName("the first slot in the state asked for wins")
        void firstMatchingSlotWins() throws Exception {
            wear(2, new ItemObject());

            assertEquals(3, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_RING, false));
            assertEquals(2, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_RING, true));
        }

        /**
         * The fallback: with both rings worn, asking for an empty ring slot answers the first ring
         * slot rather than nothing, so a caller swapping a ring has somewhere to put it. This is the
         * path C reaches by running off the end of its loop.
         *
         * @throws Exception if a slot cannot be filled
         */
        @Test
        @DisplayName("with every slot of the type full, the fallback is the first of them")
        void fallbackIsTheFirstOfTheType() throws Exception {
            wear(2, new ItemObject());
            wear(3, new ItemObject());

            assertEquals(2, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_RING, false));
        }

        /**
         * A body with no slot of the type at all answers the slot count — one past the last index,
         * which is this code's "not found" throughout the gear.
         *
         * @throws Exception if the body cannot be installed
         */
        @Test
        @DisplayName("a body without that slot type answers one past the end")
        void missingTypeAnswersPastTheEnd() throws Exception {
            setBody(new PlayerBody("Torso", new ArrayList<>(List.of(
                    new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")))));

            assertEquals(1, ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_RING, false),
                    "one past the only slot");
        }

        /**
         * The degenerate case — a body with no slots at all — cannot arise: {@link PlayerBody}
         * refuses an empty slot list at construction. Worth pinning here rather than in the body's
         * own tests, because it is what makes {@code slotByType}'s counter arithmetic safe: the loop
         * always runs at least once, so the counter is never read at its starting value.
         */
        @Test
        @DisplayName("a body with no slots cannot be built in the first place")
        void emptyBodyIsRefused() {
            assertThrows(IllegalArgumentException.class,
                    () -> new PlayerBody("Nothing", new ArrayList<>()));
        }
    }

    /**
     * The name lookup, which every caller in the power and gear code uses with a literal.
     */
    @Nested
    @DisplayName("slotByName")
    class ByName {

        /**
         * A name from {@code body.txt} answers that slot's index.
         */
        @Test
        @DisplayName("a known name answers its index")
        void knownNameAnswersIndex() {
            assertEquals(0, ObjectUtils.slotByName(player, "weapon"));
            assertEquals(1, ObjectUtils.slotByName(player, "shooting"));
            assertEquals(4, ObjectUtils.slotByName(player, "neck"));
        }

        /**
         * Two slots of one type have different names, which is how the player tells them apart and
         * how the lookup distinguishes them.
         */
        @Test
        @DisplayName("the two ring slots are distinguished by name")
        void ringSlotsAreDistinct() {
            assertEquals(2, ObjectUtils.slotByName(player, "right hand"));
            assertEquals(3, ObjectUtils.slotByName(player, "left hand"));
        }

        /**
         * An unknown name throws rather than answering one past the end as C does. Every caller
         * passes a literal, so a miss is a coding error and not a runtime condition — and answering
         * a number would let it be read as a real slot.
         */
        @Test
        @DisplayName("an unknown name is refused rather than answered")
        void unknownNameThrows() {
            assertThrows(IllegalArgumentException.class, () -> ObjectUtils.slotByName(player, "tail"));
        }
    }

    /**
     * The two index helpers, which convert between a slot and its position.
     */
    @Nested
    @DisplayName("index helpers")
    class Indexes {

        /**
         * The number-to-slot lookup answers the body's own slot, by identity.
         */
        @Test
        @DisplayName("a number answers that slot")
        void numberAnswersSlot() {
            EquipSlot expected = player.getPlayerBody().getSlots().get(2);

            assertSame(expected, ObjectUtils.slotByNumber(player, 2));
        }

        /**
         * And the reverse. Compared by identity, not equality: two ring slots are different slots
         * even though they hold the same kind of thing.
         */
        @Test
        @DisplayName("a slot answers its own number, by identity")
        void slotAnswersNumber() {
            EquipSlot rightHand = player.getPlayerBody().getSlots().get(2);
            EquipSlot leftHand = player.getPlayerBody().getSlots().get(3);

            assertEquals(2, ObjectUtils.numberFromSlot(player, rightHand));
            assertEquals(3, ObjectUtils.numberFromSlot(player, leftHand));
        }

        /**
         * A slot this body does not hold answers the slot count — the same "not found" convention as
         * {@code slotByType}, rather than a negative or an exception.
         */
        @Test
        @DisplayName("a slot from another body answers one past the end")
        void foreignSlotAnswersPastTheEnd() {
            EquipSlot foreign = new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "right hand");

            assertEquals(5, ObjectUtils.numberFromSlot(player, foreign),
                    "an equal-looking slot from elsewhere is still not this body's");
        }

        /**
         * The two helpers are inverses for every slot the body holds, which is the property the
         * gear code relies on when it converts an object's slot to a label and back.
         */
        @Test
        @DisplayName("the two helpers are inverses")
        void helpersAreInverses() {
            for (int index = 0; index < player.getPlayerBody().getSlots().size(); index++) {
                assertEquals(index, ObjectUtils.numberFromSlot(player, ObjectUtils.slotByNumber(player, index)));
            }
        }
    }
}
