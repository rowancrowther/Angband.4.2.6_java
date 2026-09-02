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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the redraw-flag half of {@link PlayerUpkeep} — the port of C's
 * {@code p->upkeep->redraw} and the take/clear pair {@code redraw_stuff} uses on it
 * ({@code player-calcs.c:2678-2711}).
 *
 * <p>C's repaint pass has a shape these three methods exist to support, and each step of it is a
 * separate thing that can be got wrong:
 *
 * <ol>
 *   <li>{@code uint32_t redraw = p->upkeep->redraw;} takes a copy;</li>
 *   <li>{@code redraw &= PR_SUBWINDOW;} narrows that copy when the map is not on screen, leaving
 *       the pending set untouched so the flags it skipped are still waiting next time;</li>
 *   <li>the pass acts on the copy;</li>
 *   <li>{@code p->upkeep->redraw &= ~redraw;} clears only what was handled.</li>
 * </ol>
 *
 * <p>Steps 2 and 4 are why {@link PlayerUpkeep#getRedrawFlags} hands back a mutable snapshot
 * rather than a view, and why {@link PlayerUpkeep#clearRedrawFlags} is a difference rather than a
 * wipe. Both are easy to "simplify" into something that passes a contents-only test and loses a
 * repaint: a view would make step 2 corrupt the live set, and a wipe would silently drop any flag
 * raised while the pass was running. The tests below pin each of those separately.
 *
 * <p>Nothing calls these yet — {@code redrawStuff} is still a stub — so these are the only
 * exercise the contract gets.
 *
 * <p>Class PlayerUpkeepRedrawFlagsTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpkeepRedrawFlagsTest {

    /**
     * A fresh upkeep for every test; its redraw set starts empty.
     */
    private PlayerUpkeep upkeep;

    /**
     * Builds a flag set in one expression.
     *
     * @param members the redraw flags to switch on
     * @return a set holding exactly those flags
     */
    private static Flag<PlayerRedraw> flagsOf(PlayerRedraw... members) {
        Flag<PlayerRedraw> result = new Flag<>(PlayerRedraw.class);
        result.set(members);
        return result;
    }

    @BeforeEach
    void setUp() {
        upkeep = new PlayerUpkeep();
    }

    /**
     * Raising and reading the pending set.
     */
    @Nested
    class Snapshot {

        @Test
        void aFreshUpkeepHasNothingPending() {
            assertTrue(upkeep.getRedrawFlags().isEmpty());
        }

        @Test
        void raisingAFlagShowsUpInTheSnapshot() {
            assertTrue(upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP));

            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
        }

        @Test
        void raisingAFlagTwiceReportsNoChangeTheSecondTime() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);

            assertFalse(upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP));
        }

        @Test
        void eachCallHandsBackADistinctSnapshot() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);

            assertNotSame(upkeep.getRedrawFlags(), upkeep.getRedrawFlags());
        }

        /**
         * The narrowing step, and the reason a view would not do.
         *
         * <p>C's {@code redraw &= PR_SUBWINDOW} writes to its local copy. If the snapshot were the
         * live set, that line would clear the pending flags outright and the map would never be
         * repainted once it came back on screen.
         */
        @Test
        void narrowingTheSnapshotLeavesThePendingSetAlone() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_STATUS);

            Flag<PlayerRedraw> taken = upkeep.getRedrawFlags();
            taken.mask(PlayerRedraw.PR_STATUS);

            assertEquals(1, taken.count());
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }

        @Test
        void aSnapshotDoesNotTrackLaterChanges() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);
            Flag<PlayerRedraw> taken = upkeep.getRedrawFlags();

            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_STATUS);

            assertFalse(taken.has(PlayerRedraw.PR_STATUS));
        }
    }

    /**
     * Clearing what has been handled.
     */
    @Nested
    class DifferencingClear {

        @Test
        void clearingWhatWasHandledLeavesTheRestPending() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_STATUS);

            assertTrue(upkeep.clearRedrawFlags(flagsOf(PlayerRedraw.PR_MAP)));

            assertFalse(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }

        /**
         * A flag raised while the repaint was running must survive the clear.
         *
         * <p>This is the case a wipe would lose: the snapshot was taken before {@code PR_STATUS}
         * went up, so clearing by the snapshot must not take it down. C gets this from
         * {@code &= ~redraw} rather than {@code = 0}.
         */
        @Test
        void aFlagRaisedDuringThePassSurvivesTheClear() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);
            Flag<PlayerRedraw> handled = upkeep.getRedrawFlags();

            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_STATUS);
            upkeep.clearRedrawFlags(handled);

            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_STATUS));
            assertFalse(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
        }

        /**
         * A flag narrowed out of the snapshot must survive too.
         *
         * <p>The other half of the same guarantee: the pass declined to service {@code PR_MAP},
         * so it must still be pending afterwards.
         */
        @Test
        void aFlagNarrowedOutOfTheSnapshotStaysPending() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_STATUS);

            Flag<PlayerRedraw> handled = upkeep.getRedrawFlags();
            handled.mask(PlayerRedraw.PR_STATUS);
            upkeep.clearRedrawFlags(handled);

            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
            assertFalse(upkeep.getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }

        @Test
        void clearingFlagsThatWereNotPendingReportsNoChange() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);

            assertFalse(upkeep.clearRedrawFlags(flagsOf(PlayerRedraw.PR_STATUS)));
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
        }

        @Test
        void clearingAnEmptySetIsANoOp() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);

            assertFalse(upkeep.clearRedrawFlags(new Flag<>(PlayerRedraw.class)));
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_MAP));
        }

        @Test
        void clearingLeavesTheArgumentAlone() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);
            Flag<PlayerRedraw> handled = flagsOf(PlayerRedraw.PR_MAP);

            upkeep.clearRedrawFlags(handled);

            assertTrue(handled.has(PlayerRedraw.PR_MAP));
        }

        @Test
        void theSingleFlagClearStillWorksAlongsideIt() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MAP);

            assertTrue(upkeep.setRedrawFlagsOff(PlayerRedraw.PR_MAP));
            assertTrue(upkeep.getRedrawFlags().isEmpty());
            assertFalse(upkeep.setRedrawFlagsOff(PlayerRedraw.PR_MAP));
        }
    }
}
