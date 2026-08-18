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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerBody#equippedItemBySlotName}, the port of C's
 * {@code equipped_item_by_slot_name} ({@code obj-gear.c:127}).
 *
 * <p>Slot names come from {@code body.txt} — "weapon", "shooting", "light", "body" — and are how
 * the rest of the game asks for one piece of equipment without knowing the body's layout. The
 * fixture uses the real humanoid names for that reason, though nothing here depends on the file.
 *
 * <p><b>Names are not unique.</b> A humanoid body has two {@code RING} slots that differ only by
 * name, so the two-rings case is the one that says what the method is for: the type cannot answer
 * the question and the index would require the caller to know the layout. C stops its loop at the
 * first match and so does this; a group below pins that the first of two same-named slots wins.
 *
 * <p><b>Three different questions share one answer.</b> An empty slot, an unknown name and a null
 * name all give null. That collapse is deliberate — C asserts on an unknown name instead
 * ({@code obj-gear.c:62}) and would crash on a null one — so each of the three has its own case
 * here, since a future rewrite might reasonably want to tell them apart and would have to change
 * the callers to do it.
 *
 * <p>{@link EquipSlot} has no setter for its item, so the fixture writes the field directly, as
 * {@code PlayerBodyEquippedTest} does. Slots are filled by the wield code, which is not yet ported.
 *
 * <p>Class PlayerBodySlotNameTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class PlayerBodySlotNameTest {

    /**
     * A body with the humanoid slot names this method is asked for in practice.
     */
    private PlayerBody body;

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
        return new PlayerBody("Humanoid", new ArrayList<>(List.of(slots)));
    }

    @BeforeEach
    void setUp() {
        body = bodyOf(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_BOW, "shooting"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "right hand"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "left hand"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_LIGHT, "light"));
    }

    /**
     * The ordinary path: a named slot with something in it.
     */
    @Nested
    class FindingAWornItem {

        @Test
        void theItemInTheNamedSlotIsReturned() throws Exception {
            ItemObject sword = new ItemObject();
            wear(body.getSlot(0), sword);

            assertSame(sword, body.equippedItemBySlotName("weapon"));
        }

        @Test
        void eachNameFindsItsOwnSlot() throws Exception {
            ItemObject sword = new ItemObject();
            ItemObject bow = new ItemObject();
            ItemObject torch = new ItemObject();
            wear(body.getSlot(0), sword);
            wear(body.getSlot(1), bow);
            wear(body.getSlot(4), torch);

            assertSame(sword, body.equippedItemBySlotName("weapon"));
            assertSame(bow, body.equippedItemBySlotName("shooting"));
            assertSame(torch, body.equippedItemBySlotName("light"));
        }

        /**
         * The item is handed back, not a copy of it — C returns the pointer out of the slot and the
         * callers act on the object itself.
         */
        @Test
        void theItemIsReturnedByIdentity() throws Exception {
            ItemObject sword = new ItemObject();
            wear(body.getSlot(0), sword);

            assertSame(sword, body.equippedItemBySlotName("weapon"));
            assertSame(body.getSlot(0).getItem(), body.equippedItemBySlotName("weapon"));
        }

        @Test
        void aLaterNamedSlotIsFoundAsReadilyAsAnEarlyOne() throws Exception {
            ItemObject torch = new ItemObject();
            wear(body.getSlot(4), torch);

            assertSame(torch, body.equippedItemBySlotName("light"));
        }
    }

    /**
     * Two slots of one type, told apart only by their names.
     */
    @Nested
    class SlotsSharingAType {

        @Test
        void theTwoRingSlotsAreDistinguishedByName() throws Exception {
            ItemObject ringRight = new ItemObject();
            ItemObject ringLeft = new ItemObject();
            wear(body.getSlot(2), ringRight);
            wear(body.getSlot(3), ringLeft);

            assertSame(ringRight, body.equippedItemBySlotName("right hand"));
            assertSame(ringLeft, body.equippedItemBySlotName("left hand"));
        }

        @Test
        void oneFilledRingSlotDoesNotAnswerForTheOther() throws Exception {
            wear(body.getSlot(2), new ItemObject());

            assertNull(body.equippedItemBySlotName("left hand"));
        }

        /**
         * Two slots sharing a name is not something {@code body.txt} does, but the tie-break is
         * C's — its loop breaks at the first match — and it should not depend on which happens to
         * be filled.
         */
        @Test
        void theFirstOfTwoSameNamedSlotsWins() throws Exception {
            PlayerBody odd = bodyOf(new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "hand"),
                    new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "hand"));
            ItemObject second = new ItemObject();
            wear(odd.getSlot(1), second);

            assertNull(odd.equippedItemBySlotName("hand"),
                    "the first matching slot answers, empty or not");
        }
    }

    /**
     * The three questions that share the null answer.
     */
    @Nested
    class TheNullAnswers {

        @Test
        void anEmptySlotAnswersNull() {
            assertNull(body.equippedItemBySlotName("weapon"));
        }

        @Test
        void anUnknownNameAnswersNullRatherThanThrowing() {
            assertNull(body.equippedItemBySlotName("tail"),
                    "C asserts here; the port answers null");
        }

        @Test
        void aNullNameAnswersNullRatherThanThrowing() {
            assertDoesNotThrow(() -> body.equippedItemBySlotName(null));
            assertNull(body.equippedItemBySlotName(null));
        }

        /**
         * A body whose slots are all full still answers null for a name it does not have, so the
         * unknown-name answer is about the name and not about the equipment.
         */
        @Test
        void anUnknownNameAnswersNullEvenOnAFullyEquippedBody() throws Exception {
            for (int index = 0; index < body.getCount(); index++) {
                wear(body.getSlot(index), new ItemObject());
            }

            assertNull(body.equippedItemBySlotName("tail"));
        }

        @Test
        void anEmptyStringIsJustAnUnknownName() {
            assertNull(body.equippedItemBySlotName(""));
        }
    }

    /**
     * Matching is exact, C using {@code streq}.
     */
    @Nested
    class MatchingIsExact {

        @Test
        void theMatchIsCaseSensitive() throws Exception {
            wear(body.getSlot(0), new ItemObject());

            assertNull(body.equippedItemBySlotName("Weapon"));
            assertNull(body.equippedItemBySlotName("WEAPON"));
        }

        @Test
        void aPrefixOfASlotNameDoesNotMatch() throws Exception {
            wear(body.getSlot(1), new ItemObject());

            assertNull(body.equippedItemBySlotName("shoot"));
        }

        @Test
        void surroundingWhitespaceIsNotTrimmed() throws Exception {
            wear(body.getSlot(0), new ItemObject());

            assertNull(body.equippedItemBySlotName(" weapon"));
            assertNull(body.equippedItemBySlotName("weapon "));
        }

        @Test
        void aMultiWordSlotNameMatchesInFull() throws Exception {
            ItemObject ring = new ItemObject();
            wear(body.getSlot(2), ring);

            assertSame(ring, body.equippedItemBySlotName("right hand"));
            assertNull(body.equippedItemBySlotName("right"));
        }
    }
}
