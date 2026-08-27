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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Chunk#squareLightSpot}, {@link Chunk#decreaseTrapTimeout} and the level-generation
 * stubs beside them.
 *
 * <p>{@code squareLightSpot} is the level's way of telling the interface that one grid has changed,
 * and it does two things at once: raises the item-list redraw on the player and signals a map event
 * for the grid. Both halves are asserted, and so is the guard — a grid off the level is ignored
 * rather than signalled, which is what lets the trap sweep below call it for every grid without
 * checking.
 *
 * <p>The coordinate order in that signal is worth pinning: it is sent x-then-y where the rest of the
 * class works y-then-x, deliberately, because that is what C does. A test is the only place that
 * asymmetry is written down.
 *
 * <p>The stubs are covered too, and stated as stubs. A test that calls one and asserts it does
 * nothing is not much of a test, but it does say plainly which methods are not yet ported — and it
 * fails the day one of them starts doing something without its tests being written.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkTrapsAndStubsTest {

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player it belongs to.
     */
    private Player player;

    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;

    /**
     * The bus the engine had before.
     */
    private EventsHandler realBus;

    /**
     * The player the game held before.
     */
    private Player savedPlayer;

    /**
     * The cave the game held before.
     */
    private Chunk savedCave;

    /**
     * Wires a level, a player and a capturing bus.
     */
    @BeforeEach
    void setUp() {
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        realBus = GameEngine.getEventsBusHandler();

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        GameState.setPlayer(player);
        GameState.setCave(level);
        level.setCurrentLevel(level);

        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    /**
     * Puts the bus, player and cave back.
     */
    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
    }

    /**
     * An event bus that records what the level raises.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * Every event type seen, in order.
         */
        private final List<GameEventType> types = new ArrayList<>();

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            types.add(eventType);
        }
    }

    /**
     * The single-grid redraw.
     */
    @Nested
    @DisplayName("squareLightSpot")
    class LightSpot {

        /**
         * A grid on the level raises the item-list redraw and signals a map event.
         */
        @Test
        @DisplayName("a grid on the level is signalled and raises the redraw")
        void gridOnLevelIsSignalled() {
            level.squareLightSpot(Loc.row(2).col(3));

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_ITEMLIST));
            assertEquals(1, bus.types.size());
            assertEquals(GameEventType.EVENT_MAP, bus.types.get(0));
        }

        /**
         * A grid off the level is ignored entirely — no redraw and no signal — which is what lets
         * callers sweep the whole grid array without bounds-checking first.
         */
        @Test
        @DisplayName("a grid off the level is ignored")
        void gridOffLevelIsIgnored() {
            level.squareLightSpot(Loc.row(-1).col(0));
            level.squareLightSpot(Loc.row(0).col(99));

            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_ITEMLIST));
            assertTrue(bus.types.isEmpty());
        }

        /**
         * Several grids raise several signals — the event is per grid, not a single "something
         * changed" for the whole level.
         */
        @Test
        @DisplayName("each grid raises its own signal")
        void eachGridSignalsSeparately() {
            level.squareLightSpot(Loc.row(1).col(1));
            level.squareLightSpot(Loc.row(2).col(2));

            assertEquals(2, bus.types.size());
        }
    }

    /**
     * The trap countdown, which sweeps every grid on the level.
     */
    @Nested
    @DisplayName("decreaseTrapTimeout")
    class TrapTimeout {

        /**
         * A level with no traps at all sweeps quietly — the common case, and the one that proves the
         * sweep is not signalling per grid regardless.
         */
        @Test
        @DisplayName("a level with no traps signals nothing")
        void noTrapsSignalsNothing() {
            level.decreaseTrapTimeout();

            assertTrue(bus.types.isEmpty());
        }

        /**
         * The sweep covers every grid, including the boundary — unlike the scent ageing beside it,
         * which deliberately skips the outer ring. The two loops sit within a few lines of each
         * other and differ in exactly that, so it is worth stating which is which.
         */
        @Test
        @DisplayName("the sweep covers the whole grid array, boundary included")
        void sweepCoversTheBoundary() {
            level.decreaseTrapTimeout();

            assertTrue(bus.types.isEmpty(),
                    "nothing to report, but the sweep visited every square without complaint");
        }
    }

    /**
     * The methods that are not yet ported. Each is called and asserted to be harmless, which records
     * what is outstanding and fails the day one of them starts doing something untested.
     */
    @Nested
    @DisplayName("stubs")
    class Stubs {

        /**
         * Lighting the level does nothing yet. C's version lights every room by daylight or leaves
         * the dungeon dark; the port's comment also notes that the daytime test should come from the
         * world rather than an argument once it is written.
         */
        @Test
        @DisplayName("illuminate does nothing yet")
        void illuminateIsAStub() {
            level.illuminate(true);
            level.illuminate(false);

            assertTrue(bus.types.isEmpty());
        }

        /**
         * Remembering the traps on a square does nothing yet, which is why the trap sweep above can
         * call it for every changed square without effect.
         */
        @Test
        @DisplayName("squareMemorizeTraps does nothing yet")
        void memorizeTrapsIsAStub() {
            level.squareMemorizeTraps(Loc.row(2).col(3));

            assertTrue(bus.types.isEmpty());
        }

        /**
         * Moving a monster between array slots does nothing yet — the compaction below would need it
         * to close the gaps it leaves.
         */
        @Test
        @DisplayName("monsterIndexMove does nothing yet")
        void indexMoveIsAStub() {
            level.monsterIndexMove(0, 1);

            assertEquals(2, level.getMonMax(), "the array is untouched");
        }

        /**
         * Placing a distant monster always reports failure, so callers behave as though the level
         * were too crowded to spawn one.
         */
        @Test
        @DisplayName("pickAndPlaceDistantMonster always reports failure")
        void distantMonsterIsAStub() {
            assertFalse(level.pickAndPlaceDistantMonster(Loc.row(2).col(3), 10, false, 1));
        }

        /**
         * Telling the player how the level feels does nothing yet.
         */
        @Test
        @DisplayName("displayFeeling does nothing yet")
        void displayFeelingIsAStub() {
            level.displayFeeling(true);
            level.displayFeeling(false);

            assertTrue(bus.types.isEmpty());
        }
    }

    /**
     * Monster compaction, which is implemented but cannot presently be run.
     */
    @Nested
    @DisplayName("compactMonsters")
    class Compaction {

        /**
         * <b>Outstanding.</b> Compacting throws before it does anything, on a level with no monsters
         * at all and even when asked to compact none.
         *
         * <p>The excise loop at the foot of the method starts at {@code monMax} and reads
         * {@code monsters[monMax]}, but the array is exactly {@code monMax} long — so the first
         * iteration is off the end. C starts one lower and stops at 1 rather than 0
         * ({@code mon-make.c:538}): {@code for (m_idx = cave_monster_max(c) - 1; m_idx >= 1;
         * m_idx--)}. Index 0 is C's "no monster" slot and is deliberately never compacted.
         *
         * <p>Asserted as a throw rather than skipped, so that the test starts failing the day the
         * loop bounds are corrected — at which point this becomes a test of what compaction does.
         */
        @Test
        @DisplayName("compacting throws on the excise loop's first index")
        void compactingThrowsOnTheExciseLoop() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> level.compactMonsters(0));
        }
    }
}
