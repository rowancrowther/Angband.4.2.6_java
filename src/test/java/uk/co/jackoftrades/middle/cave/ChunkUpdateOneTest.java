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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.WorldData;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.updateOne}, the port of C's {@code update_one} ({@code cave-view.c:834}).
 *
 * <p>The expected values come from the C body rather than from the port. C does four things to a
 * grid, in this order: it clears {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} if the player
 * is blind, or checks a seen grid for a trap if they are not; it acts on an unseen-to-seen
 * crossing, counting the grid towards the level feeling if it carries {@code SQUARE_FEEL}; it acts
 * on a seen-to-unseen crossing; and it clears {@code SQUARE_WASSEEN} whatever happened.
 *
 * <p>Three details of that body are what these tests are mostly about. The two crossing tests are
 * consecutive {@code if}s and not an if/else, which matters because the blind branch above can
 * clear {@code SQUARE_SEEN} between the two, so a blind grid that was seen takes the seen-to-unseen
 * path in the same call that made it unseen. The feeling count is announced on {@code ==
 * feeling_need} and not {@code >=}, so a count that arrives at the threshold from above never
 * announces. And {@code SQUARE_WASSEEN} comes off on every path, including the one where nothing
 * else happened at all.
 *
 * <p>The observable effects are the square's own flags, the level's feeling count, and what
 * {@code square_light_spot} raises — a {@code PR_ITEMLIST} redraw flag and an {@code EVENT_MAP}
 * event — which is what tells the two crossings apart from the two non-crossings. C's
 * {@code square_note_spot}, {@code square_reveal_trap} and {@code display_feeling} are stubs in the
 * port and so are not observable here; {@code PR_FEELING} is raised alongside the last of those and
 * is.
 *
 * <p>The level is not square and no grid used is its own transpose, so a chunk indexing
 * {@code squares[y][x]} would fail here rather than quietly agree with itself.
 *
 * <p>Class ChunkUpdateOneTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkUpdateOneTest {

    /**
     * The chunk's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 9;

    /**
     * The chunk's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 6;

    /**
     * The grid the tests work on. Off the diagonal, and inside the level.
     */
    private static final Loc GRID = Loc.row(1).col(7);

    /**
     * The number of feeling squares the level has to be shown before the feeling is announced —
     * {@code z_info->feeling_need}, seeded here rather than read from {@code constants.txt}.
     */
    private static final int FEELING_NEED = 10;

    /**
     * Whatever {@code GameConstants.data} held before this class seeded it.
     */
    private GameConstantsData savedConstants;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the level belongs to.
     */
    private Player player;

    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;

    /**
     * The bus the engine held before.
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
     * Seeds the constants the method under test reads through — {@code world:feeling-need}, which
     * decides when the feeling is announced, and the carry-cap block, which {@code PlayerUpkeep}
     * needs before a {@link Player} can be constructed at all.
     *
     * <p>Seeded per test rather than per class, so that it holds whichever order the class-level
     * fixtures run in, and put back afterwards so a later class sees the table it expects.
     */
    private void seedConstants() {
        GameConstantsData seed = new GameConstantsData(
                null, null, null, null,
                new WorldData(128, 0, 0, 0, 0, 0, 0, FEELING_NEED, 0, 0),
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null);
        savedConstants = setStatic("data", seed);
    }

    /**
     * Sets one of {@link GameConstants}'s private static fields.
     *
     * @param name  the field's name
     * @param value the value to write
     * @return what the field held before
     */
    private GameConstantsData setStatic(String name, GameConstantsData value) {
        try {
            Field field = GameConstants.class.getDeclaredField(name);
            field.setAccessible(true);
            GameConstantsData previous = (GameConstantsData) field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach GameConstants." + name, e);
        }
    }

    /**
     * Wires a level, a player and a capturing bus. The level starts with no feeling squares
     * counted and every grid's info flags empty.
     */
    @BeforeEach
    void setUp() {
        seedConstants();
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        realBus = GameEngine.getEventsBusHandler();

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
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
        setStatic("data", savedConstants);
    }

    /**
     * Calls the method under test, which is private and has no public caller that does not also
     * sweep the whole level.
     *
     * @param grid the grid to settle
     */
    private void updateOne(Loc grid) {
        try {
            Method method = Chunk.class.getDeclaredMethod("updateOne", Loc.class, Player.class);
            method.setAccessible(true);
            method.invoke(level, grid, player);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("updateOne threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach updateOne", e);
        }
    }

    /**
     * Sets the state a grid is in before the call, in the terms C uses: whether the recalculated
     * view has it as seen, and whether the record of the previous view had it as seen.
     *
     * @param grid the grid to set up
     * @param seen whether {@code SQUARE_SEEN} is on
     * @param was  whether {@code SQUARE_WASSEEN} is on
     */
    private void seenAndWasSeen(Loc grid, boolean seen, boolean was) {
        Square square = level.getSquare(grid);
        if (seen) square.sqInfoOn(SquareEnum.SQUARE_SEEN);
        else square.sqInfoOff(SquareEnum.SQUARE_SEEN);
        if (was) square.sqInfoOn(SquareEnum.SQUARE_WASSEEN);
        else square.sqInfoOff(SquareEnum.SQUARE_WASSEEN);
    }

    /**
     * Blinds the player, or restores their sight.
     *
     * @param turns the number of turns of {@code TMD_BLIND} left, zero for a sighted player
     */
    @SuppressWarnings("unchecked")
    private void blind(int turns) {
        try {
            Field field = Player.class.getDeclaredField("timed");
            field.setAccessible(true);
            ((Map<TimedEffect, Integer>) field.get(player)).put(TimedEffect.TMD_BLIND, turns);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach Player.timed", e);
        }
    }

    /**
     * Sets the interface's partial-update flag, which suppresses the feeling announcement.
     *
     * @param value what to set {@code only_partial} to
     */
    private void onlyPartial(boolean value) {
        try {
            Field field = player.getPlayerUpkeep().getClass().getDeclaredField("onlyPartial");
            field.setAccessible(true);
            field.set(player.getPlayerUpkeep(), value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach PlayerUpkeep.onlyPartial", e);
        }
    }

    /**
     * Reads or writes the level's running count of feeling squares seen, which the chunk keeps
     * private and only the constructor sets.
     *
     * @return the accessible field
     */
    private Field feelingSquaresField() {
        try {
            Field field = Chunk.class.getDeclaredField("feelingSquares");
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach Chunk.feelingSquares", e);
        }
    }

    /**
     * @return the level's running count of feeling squares seen
     */
    private int feelingSquares() {
        try {
            return feelingSquaresField().getInt(level);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("cannot read Chunk.feelingSquares", e);
        }
    }

    /**
     * @param value the count to put the level at
     */
    private void feelingSquares(int value) {
        try {
            feelingSquaresField().setInt(level, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("cannot write Chunk.feelingSquares", e);
        }
    }

    /**
     * Reads one of a square's info flags. {@code SQUARE_CLOSE_PLAYER} has no accessor of its own on
     * {@link Square}, so the flag set is read directly.
     *
     * @param grid the grid to ask about
     * @param flag the flag to test for
     * @return whether the flag is on
     */
    @SuppressWarnings("unchecked")
    private boolean hasInfo(Loc grid, SquareEnum flag) {
        try {
            Field field = Square.class.getDeclaredField("info");
            field.setAccessible(true);
            return ((Flag<SquareEnum>) field.get(level.getSquare(grid))).has(flag);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach Square.info", e);
        }
    }

    /**
     * @return whether a map redraw was signalled for any grid during this test
     */
    private boolean redrewMap() {
        return bus.types.contains(GameEventType.EVENT_MAP);
    }

    /**
     * @return whether the level-feeling redraw flag is raised on the player
     */
    private boolean feelingRedrawRaised() {
        return player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_FEELING);
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
     * What blindness does, which C applies before anything else and which overrides what the light
     * and sight calculation decided.
     */
    @Nested
    @DisplayName("blindness")
    class Blindness {

        /**
         * C clears both {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} for a blind player.
         */
        @Test
        @DisplayName("clears seen and close-player")
        void clearsSeenAndClose() {
            Square square = level.getSquare(GRID);
            square.sqInfoOn(SquareEnum.SQUARE_SEEN);
            square.sqInfoOn(SquareEnum.SQUARE_CLOSE_PLAYER);
            blind(5);

            updateOne(GRID);

            assertFalse(level.squareIsSeen(GRID), "a blind player sees nothing");
            assertFalse(hasInfo(GRID, SquareEnum.SQUARE_CLOSE_PLAYER),
                    "close-player comes off with seen");
        }

        /**
         * The blind branch and the crossing tests are consecutive statements, not an if/else, so a
         * grid the view had as seen is cleared and then takes the seen-to-unseen path in the same
         * call — which redraws it.
         */
        @Test
        @DisplayName("a grid it clears then takes the seen-to-unseen path")
        void clearedGridRedraws() {
            seenAndWasSeen(GRID, true, true);
            blind(1);

            updateOne(GRID);

            assertTrue(redrewMap(), "the grid has just gone out of sight and needs redrawing");
        }

        /**
         * A blind player's newly seen grid cannot cross to seen, because the flag has already been
         * taken off by the time the crossing is tested. Nothing is counted and nothing is drawn.
         */
        @Test
        @DisplayName("no unseen-to-seen crossing while blind")
        void noCrossingWhileBlind() {
            seenAndWasSeen(GRID, true, false);
            level.getSquare(GRID).sqInfoOn(SquareEnum.SQUARE_FEEL);
            blind(1);

            updateOne(GRID);

            assertEquals(0, feelingSquares(), "a blind player learns no feeling");
            assertTrue(level.getSquare(GRID).isFeel(), "the marker is left for a later pass");
            assertFalse(redrewMap(), "neither crossing applies");
        }
    }

    /**
     * The two crossings, and the two non-crossings that look like them.
     */
    @Nested
    @DisplayName("crossings")
    class Crossings {

        /**
         * Unseen to seen: C notes the spot and lights it.
         */
        @Test
        @DisplayName("unseen to seen redraws the grid")
        void unseenToSeen() {
            seenAndWasSeen(GRID, true, false);

            updateOne(GRID);

            assertTrue(redrewMap(), "a newly seen grid is drawn");
        }

        /**
         * Seen to unseen: C lights the spot and nothing else, since there is nothing new to learn
         * about a grid that has gone out of sight.
         */
        @Test
        @DisplayName("seen to unseen redraws the grid")
        void seenToUnseen() {
            seenAndWasSeen(GRID, false, true);

            updateOne(GRID);

            assertTrue(redrewMap(), "a grid just lost from sight is drawn");
        }

        /**
         * Seen before and seen now is no crossing at all.
         */
        @Test
        @DisplayName("seen throughout redraws nothing")
        void seenThroughout() {
            seenAndWasSeen(GRID, true, true);

            updateOne(GRID);

            assertFalse(redrewMap(), "nothing about the grid has changed");
        }

        /**
         * Unseen before and unseen now is no crossing either.
         */
        @Test
        @DisplayName("unseen throughout redraws nothing")
        void unseenThroughout() {
            seenAndWasSeen(GRID, false, false);

            updateOne(GRID);

            assertFalse(redrewMap(), "nothing about the grid has changed");
        }
    }

    /**
     * The level-feeling count, which C collects on the unseen-to-seen crossing only.
     */
    @Nested
    @DisplayName("feeling squares")
    class Feeling {

        /**
         * Prepares a grid that carries the feeling marker and is crossing into view.
         */
        @BeforeEach
        void markedAndCrossing() {
            seenAndWasSeen(GRID, true, false);
            level.getSquare(GRID).sqInfoOn(SquareEnum.SQUARE_FEEL);
        }

        /**
         * C counts the grid and then clears the marker, so the same grid cannot be counted twice.
         */
        @Test
        @DisplayName("counts the grid once and clears the marker")
        void countsAndClears() {
            updateOne(GRID);

            assertEquals(1, feelingSquares(), "the grid counts once");
            assertFalse(level.getSquare(GRID).isFeel(), "the marker is cleared behind it");

            seenAndWasSeen(GRID, true, false);
            updateOne(GRID);

            assertEquals(1, feelingSquares(), "the same grid cannot count again");
        }

        /**
         * A marked grid that was already seen is not crossing, so it is not counted and keeps its
         * marker.
         */
        @Test
        @DisplayName("a marked grid already in view is not counted")
        void notCountedWithoutCrossing() {
            seenAndWasSeen(GRID, true, true);

            updateOne(GRID);

            assertEquals(0, feelingSquares(), "no crossing, no count");
            assertTrue(level.getSquare(GRID).isFeel(), "the marker survives for a later crossing");
        }

        /**
         * An unmarked grid crossing into view counts for nothing.
         */
        @Test
        @DisplayName("an unmarked grid is not counted")
        void unmarkedNotCounted() {
            level.getSquare(GRID).sqInfoOff(SquareEnum.SQUARE_FEEL);

            updateOne(GRID);

            assertEquals(0, feelingSquares(), "only marked grids count");
            assertFalse(feelingRedrawRaised(), "and only marked grids can announce");
        }

        /**
         * The pass that takes the count to {@code feeling_need} announces the feeling.
         */
        @Test
        @DisplayName("announces on the pass that reaches the threshold")
        void announcesAtThreshold() {
            feelingSquares(FEELING_NEED - 1);

            updateOne(GRID);

            assertEquals(FEELING_NEED, feelingSquares(), "the threshold is reached");
            assertTrue(feelingRedrawRaised(), "and the feeling is announced");
        }

        /**
         * A pass short of the threshold does not announce.
         */
        @Test
        @DisplayName("does not announce below the threshold")
        void silentBelowThreshold() {
            feelingSquares(FEELING_NEED - 2);

            updateOne(GRID);

            assertEquals(FEELING_NEED - 1, feelingSquares(), "still one short");
            assertFalse(feelingRedrawRaised(), "so nothing is announced");
        }

        /**
         * C tests {@code ==} and not {@code >=}, so a count that is already past the threshold
         * never announces, however many more marked grids are seen.
         */
        @Test
        @DisplayName("does not announce again once past the threshold")
        void silentAboveThreshold() {
            feelingSquares(FEELING_NEED);

            updateOne(GRID);

            assertEquals(FEELING_NEED + 1, feelingSquares(), "the count keeps rising");
            assertFalse(feelingRedrawRaised(), "but the announcement is equality-tested");
        }

        /**
         * The announcement is suppressed while the interface is rebuilding a character's state
         * rather than playing a turn — but the grid is still counted and its marker still cleared.
         */
        @Test
        @DisplayName("only-partial suppresses the announcement, not the count")
        void onlyPartialSuppresses() {
            feelingSquares(FEELING_NEED - 1);
            onlyPartial(true);

            updateOne(GRID);

            assertEquals(FEELING_NEED, feelingSquares(), "the count is unaffected");
            assertFalse(level.getSquare(GRID).isFeel(), "and so is the clearing of the marker");
            assertFalse(feelingRedrawRaised(), "but the feeling is not announced");
        }
    }

    /**
     * The record of the previous view, which C clears on every path.
     */
    @Nested
    @DisplayName("wasseen")
    class WasSeen {

        /**
         * Every combination of the two flags, sighted and blind, ends with
         * {@code SQUARE_WASSEEN} off — including the one where the method does nothing else.
         */
        @Test
        @DisplayName("is cleared whatever else happened")
        void alwaysCleared() {
            List<String> left = new ArrayList<>();
            Map<String, Loc> cases = new HashMap<>();
            cases.put("seen, was seen", Loc.row(0).col(0));
            cases.put("seen, was unseen", Loc.row(2).col(5));
            cases.put("unseen, was seen", Loc.row(4).col(1));
            cases.put("unseen, was unseen", Loc.row(HEIGHT - 1).col(WIDTH - 1));

            for (Map.Entry<String, Loc> entry : cases.entrySet()) {
                boolean seen = entry.getKey().startsWith("seen");
                boolean was = entry.getKey().endsWith("was seen");
                for (int turns : new int[]{0, 3}) {
                    seenAndWasSeen(entry.getValue(), seen, was);
                    blind(turns);
                    updateOne(entry.getValue());
                    if (level.getSquare(entry.getValue()).wasSeen()) {
                        left.add(entry.getKey() + (turns == 0 ? ", sighted" : ", blind"));
                    }
                }
            }

            assertTrue(left.isEmpty(), "wasseen left standing for: " + left);
        }
    }
}
