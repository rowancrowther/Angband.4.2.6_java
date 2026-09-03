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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link PlayerUpkeep} members around the pack and quiver: the notice-flag request queue
 * ({@link PlayerUpkeep#orNoticeFlag}), the two live views ({@link PlayerUpkeep#getQuiver},
 * {@link PlayerUpkeep#getInventory}), and the two wholesale replacements
 * ({@link PlayerUpkeep#setInventory}, {@link PlayerUpkeep#setQuiverObjects}).
 *
 * <p><b>Three contracts, each easy to break without noticing.</b> The notice flag is a request
 * queue, so setting one twice has to be the same as setting it once — a caller deep in the
 * knowledge code asks for the ignore pass to be re-run and must not care whether someone else asked
 * first. The two list getters are live views into the upkeep rather than snapshots of it, which is
 * what lets the label code walk them by index and see the pack as it currently stands; a defensive
 * copy in either would be correct-looking and wrong, so both are asserted by identity. The two
 * setters are the one place the port does something C never does — replace the array reference
 * outright rather than write through a slot — so what they're tested against is the shape of that
 * difference rather than a C-derived value.
 *
 * <p>Class PlayerUpkeepGearViewsTest coded on 260816, commented in full on 260816, gear-replacement
 * nested class added 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpkeepGearViewsTest {

    private PlayerUpkeep upkeep;

    @BeforeEach
    void setUp() {
        upkeep = new PlayerUpkeep();
    }

    /**
     * The request queue. C's {@code p->upkeep->notice |= PN_…} answers nothing; the port answers
     * whether the request was new, and these tests fix what that answer means.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("orNoticeFlag")
    class NoticeFlags {

        @Test
        @DisplayName("setting a flag for the first time reports it as new")
        void firstSetIsNew() {
            assertTrue(upkeep.orNoticeFlag(PlayerNotice.PN_IGNORE));
        }

        /**
         * The idempotence that makes the flag a request rather than a counter. A second caller asking
         * for the same pass changes nothing, and is told so.
         */
        @Test
        @DisplayName("setting a flag again reports it as already set")
        void secondSetIsNotNew() {
            upkeep.orNoticeFlag(PlayerNotice.PN_IGNORE);

            assertFalse(upkeep.orNoticeFlag(PlayerNotice.PN_IGNORE));
        }

        /**
         * Or, not assignment. C's operator is bitwise or for a reason: several requests can be
         * outstanding at once, and setting one must not clear another.
         */
        @Test
        @DisplayName("setting one flag leaves another untouched")
        void flagsDoNotDisplaceEachOther() {
            upkeep.orNoticeFlag(PlayerNotice.PN_IGNORE);
            upkeep.orNoticeFlag(PlayerNotice.PN_COMBINE);

            assertFalse(upkeep.orNoticeFlag(PlayerNotice.PN_IGNORE));
            assertFalse(upkeep.orNoticeFlag(PlayerNotice.PN_COMBINE));
        }

        /**
         * A fresh upkeep has no requests outstanding — the state a newborn player is in.
         */
        @Test
        @DisplayName("a fresh upkeep has no notices set")
        void freshUpkeepHasNoNotices() {
            assertTrue(upkeep.orNoticeFlag(PlayerNotice.PN_COMBINE));
        }
    }

    /**
     * The two gear views.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the quiver and the pack")
    class GearViews {

        /**
         * The quiver is a fixed set of slots, all of them empty, rather than a list with nothing in
         * it. C indexes {@code p->upkeep->quiver[i]} over {@code 0 .. z_info->quiver_size} and reads
         * a {@code NULL} entry as an empty slot, and {@code quiverAbsorbNum} counts those nulls to
         * decide whether a stack has anywhere to go. A variable-length list has no empty slots to
         * count, so the free-space figure comes out as zero and nothing is ever admitted.
         */
        @Test
        @DisplayName("the quiver starts as quiver-size empty slots")
        void quiverStartsAsEmptySlots() {
            assertEquals(GameConstants.getCarryCapQuiverSize(), upkeep.getQuiver().length);
            for (ItemObject slot : upkeep.getQuiver()) {
                assertNull(slot);
            }
        }

        /**
         * The pack is the same shape as the quiver: pack-size slots, every one empty. C's
         * {@code p->upkeep->inven} is an array of that length with {@code NULL} for an empty slot,
         * and the rebuild in {@code calcInventory} writes into it by index.
         */
        @Test
        @DisplayName("the pack starts as pack-size empty slots")
        void inventoryStartsEmpty() {
            assertEquals(GameConstants.getCarryCapPackSize() + 1, upkeep.getInventory().length);
            for (ItemObject slot : upkeep.getInventory()) {
                assertNull(slot);
            }
        }

        /**
         * Live, not copied. If the getter snapshotted, the second read would not see the write.
         *
         * <p>Slot 3 rather than slot 0, because a position in this array is a quiver slot number and
         * not a place in a queue: writing to 3 must leave 0 empty rather than shuffling anything
         * along. That is what lets an inscription like {@code @f3} ask for a particular slot.
         */
        @Test
        @DisplayName("the quiver is a live view, not a snapshot")
        void quiverIsLive() {
            ItemObject arrows = new ItemObject();
            upkeep.getQuiver()[3] = arrows;

            assertSame(arrows, upkeep.getQuiver()[3]);
            assertNull(upkeep.getQuiver()[0]);
        }

        /**
         * Live, not copied, and indexed rather than appended - as with the quiver, a position here is
         * the letter the player selects the object by, so writing to slot 2 must leave 0 and 1 empty.
         */
        @Test
        @DisplayName("the pack is a live view, not a snapshot")
        void inventoryIsLive() {
            ItemObject potion = new ItemObject();
            upkeep.getInventory()[2] = potion;

            assertSame(potion, upkeep.getInventory()[2]);
            assertNull(upkeep.getInventory()[0]);
        }

        /**
         * Successive reads hand back the one array, which is the strongest form of the same claim and
         * the one a defensive copy would fail outright.
         */
        @Test
        @DisplayName("successive reads return the same array object")
        void readsAreStable() {
            assertSame(upkeep.getQuiver(), upkeep.getQuiver());
            assertSame(upkeep.getInventory(), upkeep.getInventory());
        }

        /**
         * Order is preserved, because order is meaning: an object's position in these arrays is the
         * key the player presses to select it, so two objects written to successive slots come back
         * in that order and not in the order they were created.
         */
        @Test
        @DisplayName("slot order is the order things were put in")
        void orderIsPreserved() {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            upkeep.getInventory()[0] = first;
            upkeep.getInventory()[1] = second;

            assertSame(first, upkeep.getInventory()[0]);
            assertSame(second, upkeep.getInventory()[1]);
        }

        /**
         * The two views are separate arrays. Ammunition goes in one and everything else in the other,
         * and an object written to the pack must not appear in the quiver.
         */
        @Test
        @DisplayName("the quiver and the pack are distinct")
        void viewsAreDistinct() {
            upkeep.getInventory()[0] = new ItemObject();

            for (ItemObject slot : upkeep.getQuiver()) {
                assertNull(slot);
            }
        }
    }

    /**
     * {@link PlayerUpkeep#setInventory} and {@link PlayerUpkeep#setQuiverObjects}: the wholesale
     * swap the C original never performs. C allocates {@code p->upkeep->inven} and
     * {@code p->upkeep->quiver} exactly once and only ever writes through a slot afterwards, so
     * these two setters have no C statement to derive expected values from — what's being tested is
     * that the swap is exactly that, a reference replacement, with none of the slot-preserving
     * behaviour a real C write would have.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("replacing the pack and the quiver outright")
    class GearReplacement {

        /**
         * The new array becomes what {@link PlayerUpkeep#getInventory} returns — a plain reference
         * write, checked by identity because a copy would pass an equals-by-content check just as
         * well and hide the difference this method exists to make.
         */
        @Test
        @DisplayName("setInventory installs the given array as the pack")
        void setInventoryInstallsTheArray() {
            ItemObject[] fresh = new ItemObject[]{new ItemObject(), new ItemObject()};

            upkeep.setInventory(fresh);

            assertSame(fresh, upkeep.getInventory());
        }

        /**
         * Same claim, for the quiver.
         */
        @Test
        @DisplayName("setQuiverObjects installs the given array as the quiver")
        void setQuiverObjectsInstallsTheArray() {
            ItemObject[] fresh = new ItemObject[]{new ItemObject()};

            upkeep.setQuiverObjects(fresh);

            assertSame(fresh, upkeep.getQuiver());
        }

        /**
         * The caveat the Javadoc calls out: a reference taken before the swap is not updated by it.
         * C has nothing to test here — the pointer it hands out never goes stale, because C never
         * replaces it — so this fixes the one way the port's convenience differs from the original.
         */
        @Test
        @DisplayName("a reference held before the swap does not see it")
        void oldReferenceGoesStale() {
            ItemObject[] original = upkeep.getInventory();
            ItemObject[] replacement = new ItemObject[]{new ItemObject()};

            upkeep.setInventory(replacement);

            assertSame(replacement, upkeep.getInventory());
            assertNotSame(replacement, original);
            for (ItemObject slot : original) {
                assertNull(slot);
            }
        }

        /**
         * The setters do not police the array they are given — no length check against pack size or
         * quiver size, and no rejection of {@code null}. Nothing in C constrains them either, since C
         * never performs this operation at all; this fixes that the port's version is equally
         * unguarded, not that it ought to be.
         */
        @Test
        @DisplayName("setInventory accepts a null array")
        void setInventoryAcceptsNull() {
            upkeep.setInventory(null);

            assertNull(upkeep.getInventory());
        }

        @Test
        @DisplayName("setQuiverObjects accepts an array of a different length")
        void setQuiverObjectsAcceptsAnyLength() {
            ItemObject[] oversized = new ItemObject[GameConstants.getCarryCapQuiverSize() + 5];

            upkeep.setQuiverObjects(oversized);

            assertEquals(oversized.length, upkeep.getQuiver().length);
        }
    }
}
