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
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        @Test
        @DisplayName("both start empty rather than null")
        void bothStartEmpty() {
            assertTrue(upkeep.getQuiver().isEmpty());
            assertTrue(upkeep.getInventory().isEmpty());
        }

        /**
         * Live, not copied. If the getter snapshotted, the second read would not see the write.
         */
        @Test
        @DisplayName("the quiver is a live view, not a snapshot")
        void quiverIsLive() {
            ItemObject arrows = new ItemObject();
            upkeep.getQuiver().add(arrows);

            assertEquals(1, upkeep.getQuiver().size());
            assertSame(arrows, upkeep.getQuiver().get(0));
        }

        @Test
        @DisplayName("the pack is a live view, not a snapshot")
        void inventoryIsLive() {
            ItemObject potion = new ItemObject();
            upkeep.getInventory().add(potion);

            assertEquals(1, upkeep.getInventory().size());
            assertSame(potion, upkeep.getInventory().get(0));
        }

        /**
         * Successive reads hand back the one list, which is the strongest form of the same claim and
         * the one a defensive copy would fail outright.
         */
        @Test
        @DisplayName("successive reads return the same list object")
        void readsAreStable() {
            assertSame(upkeep.getQuiver(), upkeep.getQuiver());
            assertSame(upkeep.getInventory(), upkeep.getInventory());
        }

        /**
         * Order is preserved, because order is meaning: an object's position in these lists is the
         * key the player presses to select it.
         */
        @Test
        @DisplayName("slot order is the order things were put in")
        void orderIsPreserved() {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            upkeep.getInventory().add(first);
            upkeep.getInventory().add(second);

            assertSame(first, upkeep.getInventory().get(0));
            assertSame(second, upkeep.getInventory().get(1));
        }

        /**
         * The two views are separate lists. Ammunition goes in one and everything else in the other,
         * and an object added to the pack must not appear in the quiver.
         */
        @Test
        @DisplayName("the quiver and the pack are distinct")
        void viewsAreDistinct() {
            upkeep.getInventory().add(new ItemObject());

            assertTrue(upkeep.getQuiver().isEmpty());
        }
    }
}
