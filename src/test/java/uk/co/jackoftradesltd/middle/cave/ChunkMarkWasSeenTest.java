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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.markWasSeen}, the port of C's {@code mark_wasseen}
 * ({@code cave-view.c}).
 *
 * <p>The method does two things that pull in opposite directions, and the tests are built around
 * keeping them apart. It <em>sets</em> {@code SQUARE_WASSEEN}, but conditionally — only where the
 * player can see — and it <em>clears</em> {@code SQUARE_VIEW}, {@code SQUARE_SEEN} and
 * {@code SQUARE_CLOSE_PLAYER}, unconditionally, everywhere. A sweep that cleared the three only
 * where it set the one would pass any test that looked at seen grids alone, so the unseen grid is
 * asserted every time the seen grid is.
 *
 * <p>{@code SQUARE_SEEN} is the flag that makes this delicate: it is both the input to the
 * condition and one of the outputs of the wipe. Read it after the sweep and it is gone whatever
 * happened, so the only evidence of what the condition decided is {@code SQUARE_WASSEEN} — which
 * is why every test here asserts on that flag rather than on the one it was derived from.
 *
 * <p>The sweep is invoked directly, by reflection, rather than through its caller
 * {@link Chunk#updateView(Player)}. It used to be driven through the caller, and that stopped
 * working when {@code updateOne} was ported: {@code updateView} now ends by clearing
 * {@code SQUARE_WASSEEN} at every grid, exactly as C's {@code update_one} does, so the flag is a
 * transient private to one pass of {@code updateView} and is gone by the time the caller returns.
 * There is nothing left to read from outside, and the two halves of the sweep can only be told
 * apart from inside. The one thing the pairing still shows from outside is that the flag does not
 * survive, and {@link ThroughUpdateView} asserts precisely that and nothing more.
 *
 * <p>The chunk is deliberately not square, and grids are placed off the diagonal, so a transposed
 * {@code squares[x][y]} would fail rather than quietly agree with itself.
 *
 * <p>Class ChunkMarkWasSeenTest coded on 260827, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkMarkWasSeenTest {

    /**
     * The chunk's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 9;

    /**
     * The chunk's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 6;

    /**
     * A grid the tests mark as seen before the sweep. Off the diagonal, and inside the level.
     */
    private static final Loc SEEN = Loc.row(1).col(7);

    /**
     * A grid the tests leave unseen. Off the diagonal, and the transpose of no other grid used.
     */
    private static final Loc UNSEEN = Loc.row(4).col(2);

    /**
     * Where the player stands. Off the diagonal, clear of all four corners, and neither
     * {@link #SEEN} nor {@link #UNSEEN}.
     *
     * <p>The corner matters to {@link ThroughUpdateView}: {@code updateView} puts
     * {@code SQUARE_VIEW}, and — the player carrying light — {@code SQUARE_SEEN} and
     * {@code SQUARE_CLOSE_PLAYER} back on the player's own grid after the sweep has wiped them. A
     * player left at {@code Loc.zero}, which is what {@code Player()} gives, would stand on the
     * top-left corner and make a corner case assert against a grid the code is entitled to have
     * re-lit.
     */
    private static final Loc PLAYER = Loc.row(3).col(5);

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private Object savedConstants;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the sweep is run for.
     */
    private Player player;

    /**
     * Sets a private static field by reflection, returning what it held before.
     *
     * @param owner the class declaring the field
     * @param name  the field's name
     * @param value the value to put in it
     * @return the value the field held beforehand
     */
    private static Object setStatic(Class<?> owner, String name, Object value) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            Object previous = field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(owner.getSimpleName() + "." + name
                    + " is no longer settable by reflection", e);
        }
    }

    /**
     * Seeds {@code GameConstants.data} with {@code player:max-sight} and the carry-cap figures.
     *
     * <p>{@code updateView} runs {@code updateViewOne}, which compares each grid's distance from
     * the player against {@code max-sight}, and {@link SeededPlayerRegistry} seeds carry-cap only,
     * so the table it leaves would throw on the first grid. The sight value is
     * {@code constants.txt}'s own twenty, and carry-cap is carried across so that {@link Player}
     * can still be built.
     *
     * <p>This runs per test rather than once for the class because the extension's own seeding is
     * undone at the end of every nested container, taking the table with it; a class-level seed
     * would survive only as far as the first nested class.
     */
    @BeforeEach
    void seedConstants() {
        GameConstantsData seed = new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, new PlayerData(20, 20, 0, 0),
                null, null, null, null, null, null, null, null);
        savedConstants = setStatic(GameConstants.class, "data", seed);
    }

    /**
     * Puts back whatever {@code GameConstants.data} held beforehand, so a test running later in the
     * same JVM sees the state it expects.
     */
    @AfterEach
    void restoreConstants() {
        setStatic(GameConstants.class, "data", savedConstants);
    }

    /**
     * A small level with a player attached, every grid starting with empty info flags.
     *
     * <p>The player is a {@link LitPlayer}, not a bare {@link Player}, for the reason given on that
     * class.
     */
    @BeforeEach
    void newLevel() {
        player = new LitPlayer();
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
        placePlayer(PLAYER);
    }

    /**
     * Runs the sweep under test on {@link #level}.
     *
     * <p>{@code markWasSeen} is private, and its only caller consumes the flag it sets before
     * returning, so reflection is the only way to observe what the sweep itself does. A rename
     * fails the test loudly rather than silently testing nothing.
     */
    private void markWasSeen() {
        try {
            Method method = Chunk.class.getDeclaredMethod("markWasSeen");
            method.setAccessible(true);
            method.invoke(level);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Chunk.markWasSeen is no longer callable by reflection", e);
        }
    }

    /**
     * Moves the player to a grid. {@code Player} has no setter for its grid — placing a character
     * is the dungeon's job, and that is not ported — so the field is set directly.
     *
     * @param grid the grid to stand the player on
     */
    private void placePlayer(Loc grid) {
        try {
            Field field = Player.class.getDeclaredField("grid");
            field.setAccessible(true);
            field.set(player, grid);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Player.grid is no longer reachable", e);
        }
    }

    /**
     * Turns a flag on at one grid of the level under test.
     *
     * @param grid the grid to set the flag at
     * @param flag the flag to turn on
     */
    private void setFlag(Loc grid, SquareEnum flag) {
        level.getSquare(grid).sqInfoOn(flag);
    }

    /**
     * Tests a flag at one grid of the level under test.
     *
     * @param grid the grid to test
     * @param flag the flag to look for
     * @return true if the flag is set at that grid
     */
    private boolean hasFlag(Loc grid, SquareEnum flag) {
        return level.getSquare(grid).hasInfoFlag(flag);
    }

    /**
     * A player that reports a light radius without having a calculated {@link
     * uk.co.jackoftradesltd.middle.player.PlayerState} behind it.
     *
     * <p><b>Temporary, and to be deleted.</b> {@code Player()} leaves {@code state} null — birth is
     * what fills it in, and birth is not ported yet — while {@code Chunk.updateView} runs
     * {@code calcLighting}, whose last act is to read the player's own light radius through
     * {@code Player.getStateLight()}, and {@code updateViewOne}, which asks the player for
     * {@code PF_UNLIGHT} through {@code Player.hasPlayerFlag}. Either dereferences the null state
     * and the {@link ThroughUpdateView} test dies on it before reaching an assertion. C has no
     * equivalent case: its {@code p->state} is a struct, zeroed from allocation, so
     * {@code cur_light} and the flag table are readable from the moment the player exists.
     *
     * <p>Overriding the two accessors is preferred to building a whole {@code PlayerState}, because
     * it says exactly which values the fixture is standing in for and nothing else. Nothing in
     * {@code updateView} asks the player for anything but these and its grid.
     *
     * <p>The value is 1 — a radius of {@code abs(1) - 1 == 0}, so the lighting sweep touches the
     * player's own grid and no other, which is the least this class's assertions can be disturbed
     * by. It is not a claim about what a born character's light radius should be.
     *
     * <p>Remove this class, and go back to {@code new Player()}, once birth gives the player a real
     * state. Recorded in {@code docs/Chapter_3_Roadmap.md} under stage G.
     *
     * <p>No flag is held: a zeroed {@code p->state} in C has an empty flag table, so
     * {@code PF_UNLIGHT} is off, and with it off {@code updateViewOne} never reaches the special
     * unlit radius that would read {@code getPlayerState().getCurLight()} on the same null state.
     *
     * <p>Class LitPlayer coded on 260827, commented in full on 260828.
     */
    private static final class LitPlayer extends Player {

        /**
         * Returns a light radius without consulting the player's calculated state.
         *
         * @return 1, giving a lighting radius of zero
         */
        @Override
        public int getStateLight() {
            return 1;
        }

        /**
         * Reports every player flag as unset, without consulting the player's calculated state.
         *
         * @param flag the flag asked about
         * @return false, whatever the flag
         */
        @Override
        public boolean hasPlayerFlag(PlayerFlag flag) {
            return false;
        }
    }

    /**
     * The conditional half: {@code SQUARE_WASSEEN} follows what the player could see.
     */
    @Nested
    @DisplayName("the snapshot")
    class Snapshot {

        /**
         * A seen grid is recorded, which is the whole purpose of the snapshot.
         */
        @Test
        @DisplayName("a seen grid gains SQUARE_WASSEEN")
        void seenGridIsRecorded() {
            setFlag(SEEN, SquareEnum.SQUARE_SEEN);

            markWasSeen();

            assertTrue(hasFlag(SEEN, SquareEnum.SQUARE_WASSEEN));
        }

        /**
         * An unseen grid is not recorded. Without this the sweep could set the flag everywhere and
         * the test above would not notice.
         */
        @Test
        @DisplayName("an unseen grid does not gain SQUARE_WASSEEN")
        void unseenGridIsNotRecorded() {
            setFlag(SEEN, SquareEnum.SQUARE_SEEN);

            markWasSeen();

            assertFalse(hasFlag(UNSEEN, SquareEnum.SQUARE_WASSEEN));
        }

        /**
         * {@code SQUARE_VIEW} is not the condition. A grid in view but not seen — which is what a
         * dark grid within line of sight is — must not be recorded.
         */
        @Test
        @DisplayName("a grid in view but not seen does not gain SQUARE_WASSEEN")
        void viewIsNotTheCondition() {
            setFlag(UNSEEN, SquareEnum.SQUARE_VIEW);

            markWasSeen();

            assertFalse(hasFlag(UNSEEN, SquareEnum.SQUARE_WASSEEN));
        }

        /**
         * The sweep covers the whole level, corners included, not just the middle. The four corners
         * are where an off-by-one in either loop bound would show, and where a level walked only to
         * {@code width - 1} by {@code height - 1} would leave a grid untouched.
         */
        @Test
        @DisplayName("the corners of the level are swept")
        void cornersAreSwept() {
            Loc[] corners = {
                    Loc.row(0).col(0),
                    Loc.row(0).col(WIDTH - 1),
                    Loc.row(HEIGHT - 1).col(0),
                    Loc.row(HEIGHT - 1).col(WIDTH - 1)
            };

            for (Loc corner : corners)
                setFlag(corner, SquareEnum.SQUARE_SEEN);

            markWasSeen();

            for (Loc corner : corners) {
                assertTrue(hasFlag(corner, SquareEnum.SQUARE_WASSEEN));
                assertFalse(hasFlag(corner, SquareEnum.SQUARE_SEEN));
            }
        }

        /**
         * The snapshot lands on the grid that was seen, and not on its transpose. On a level this
         * shape {@code (row 1, col 4)} and {@code (row 4, col 1)} are both valid grids, so an
         * {@code x}/{@code y} swap would be silent everywhere except a test that names both.
         */
        @Test
        @DisplayName("the snapshot lands on the seen grid, not its transpose")
        void snapshotIsNotTransposed() {
            Loc grid = Loc.row(1).col(4);
            Loc transpose = Loc.row(4).col(1);
            setFlag(grid, SquareEnum.SQUARE_SEEN);

            markWasSeen();

            assertTrue(hasFlag(grid, SquareEnum.SQUARE_WASSEEN));
            assertFalse(hasFlag(transpose, SquareEnum.SQUARE_WASSEEN));
        }
    }

    /**
     * The unconditional half: the three live visibility flags are cleared everywhere.
     */
    @Nested
    @DisplayName("the wipe")
    class Wipe {

        /**
         * All three flags go from a seen grid — including {@code SQUARE_SEEN}, which the condition
         * has just read.
         */
        @Test
        @DisplayName("view, seen and close-player are cleared from a seen grid")
        void seenGridIsWiped() {
            setFlag(SEEN, SquareEnum.SQUARE_SEEN);
            setFlag(SEEN, SquareEnum.SQUARE_VIEW);
            setFlag(SEEN, SquareEnum.SQUARE_CLOSE_PLAYER);

            markWasSeen();

            assertFalse(hasFlag(SEEN, SquareEnum.SQUARE_SEEN));
            assertFalse(hasFlag(SEEN, SquareEnum.SQUARE_VIEW));
            assertFalse(hasFlag(SEEN, SquareEnum.SQUARE_CLOSE_PLAYER));
        }

        /**
         * The wipe is not conditional on the grid having been seen. A grid holding
         * {@code SQUARE_VIEW} and {@code SQUARE_CLOSE_PLAYER} but not {@code SQUARE_SEEN} is
         * cleared just the same — this is the case a wipe tucked inside the {@code if} would fail.
         */
        @Test
        @DisplayName("view and close-player are cleared from an unseen grid too")
        void unseenGridIsWipedToo() {
            setFlag(UNSEEN, SquareEnum.SQUARE_VIEW);
            setFlag(UNSEEN, SquareEnum.SQUARE_CLOSE_PLAYER);

            markWasSeen();

            assertFalse(hasFlag(UNSEEN, SquareEnum.SQUARE_VIEW));
            assertFalse(hasFlag(UNSEEN, SquareEnum.SQUARE_CLOSE_PLAYER));
        }

        /**
         * Only the three named flags go. The sweep clears individual flags rather than emptying the
         * info field, so the level's own memory — what the player has mapped, what glows, what is
         * inside a room — has to survive every step the player takes.
         */
        @Test
        @DisplayName("flags outside the three are left alone")
        void otherFlagsSurvive() {
            setFlag(SEEN, SquareEnum.SQUARE_SEEN);
            setFlag(SEEN, SquareEnum.SQUARE_MARK);
            setFlag(SEEN, SquareEnum.SQUARE_GLOW);
            setFlag(UNSEEN, SquareEnum.SQUARE_ROOM);

            markWasSeen();

            assertTrue(hasFlag(SEEN, SquareEnum.SQUARE_MARK));
            assertTrue(hasFlag(SEEN, SquareEnum.SQUARE_GLOW));
            assertTrue(hasFlag(UNSEEN, SquareEnum.SQUARE_ROOM));
        }

        /**
         * A level nobody can see is swept without complaint, and nothing is recorded. The sweep has
         * no early exit and no notion of an interesting grid, so the empty case is the loop running
         * its full course and setting nothing.
         */
        @Test
        @DisplayName("a level with nothing seen records nothing")
        void nothingSeenRecordsNothing() {
            markWasSeen();

            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    assertFalse(hasFlag(Loc.row(y).col(x), SquareEnum.SQUARE_WASSEEN));
        }
    }

    /**
     * What the sweep looks like from outside its caller, which is: nothing at all.
     */
    @Nested
    @DisplayName("through updateView")
    class ThroughUpdateView {

        /**
         * {@code SQUARE_WASSEEN} does not outlive one pass of {@code updateView}. The sweep sets
         * the flag at the top of the pass and {@code updateOne} clears it at the bottom, so a grid
         * that was seen going in carries no trace of it coming out. That pairing is what makes the
         * flag mean "seen before this update" rather than "seen at some point", and a sweep left
         * unpaired — either half dropped — would show here as a flag that survived.
         */
        @Test
        @DisplayName("no grid keeps SQUARE_WASSEEN after the pass")
        void wasSeenDoesNotSurviveThePass() {
            setFlag(SEEN, SquareEnum.SQUARE_SEEN);

            level.updateView(player);

            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    assertFalse(hasFlag(Loc.row(y).col(x), SquareEnum.SQUARE_WASSEEN));
        }
    }
}
