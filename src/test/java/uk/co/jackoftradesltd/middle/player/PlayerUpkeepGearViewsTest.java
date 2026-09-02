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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the three {@link PlayerUpkeep} members the object-knowledge work reaches for:
 * {@link PlayerUpkeep#orNoticeFlag}, {@link PlayerUpkeep#getQuiver} and
 * {@link PlayerUpkeep#getInventory}.
 *
 * <p><b>Two contracts, both easy to break without noticing.</b> The notice flag is a request queue,
 * so setting one twice has to be the same as setting it once — a caller deep in the knowledge code
 * asks for the ignore pass to be re-run and must not care whether someone else asked first. The two
 * list getters are live views into the upkeep rather than snapshots of it, which is what lets the
 * label code walk them by index and see the pack as it currently stands. A defensive copy in either
 * would be correct-looking and wrong, so both are asserted by identity.
 *
 * <p>Class PlayerUpkeepGearViewsTest coded on 260816, commented in full on 260816.
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
}
