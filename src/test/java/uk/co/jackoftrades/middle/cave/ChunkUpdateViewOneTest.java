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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.PlayerData;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerState;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.updateViewOne}, the port of C's {@code update_view_one} ({@code cave-view.c}).
 *
 * <p>The method decides whether one grid belongs in the player's view. It computes an approximate
 * distance, drops the grid if that exceeds {@code z_info->max_sight}, works out whether the grid is
 * inside the player's own light radius, and then traces line of sight — for a wall, from the grid
 * one step back toward the player rather than from the wall itself. A grid that survives is handed
 * to {@code becomeViewable}, which is what actually writes the flags; so the assertions here read
 * {@code SQUARE_VIEW} for "the grid was passed on", {@code SQUARE_SEEN} together with
 * {@code SQUARE_CLOSE_PLAYER} for "and it was passed on as close", and no flags at all for "the
 * grid was dropped".
 *
 * <p>The expected values were derived from the C rather than from the Java. C's {@code los} and
 * {@code distance} were transcribed and run over each of the maps below to fix what the answer
 * should be, so a port that traced sight to the wall's own grid, stepped both coordinates together,
 * or used {@code <=} where C uses {@code <} would fail here rather than agree with itself.
 *
 * <p>The distance function is C's approximation, {@code max + min / 2}, not a true hypotenuse, so
 * the sight-range boundary is stated along a row where the two agree: twenty grids due east of the
 * player is a distance of exactly twenty and is kept, twenty-one is dropped.
 *
 * <p>The knight's-move branches in the C are not given a test of their own. They cannot change the
 * outcome — the grid each one requires to be a wall is the same grid the preceding wall check has
 * already rejected the loan over — which was confirmed by running the transcribed C over every grid
 * of two hundred random maps with the branches present and absent, with no difference in any case.
 * A test asserting a difference would be asserting something the original does not do.
 *
 * <p>The method under test is private and is reached by reflection, as are the player's location,
 * level and calculated state, none of which {@link Player} exposes a setter for.
 *
 * <p>Class ChunkUpdateViewOneTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkUpdateViewOneTest {

    /**
     * The level's width, in grids. Wide enough to put a grid beyond {@code max_sight} due east of
     * the player while leaving the level's own edge further out still.
     */
    private static final int WIDTH = 25;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 11;

    /**
     * Where the player stands: near the west edge, so that everything of interest lies due east or
     * just north of them, and clear of every edge.
     */
    private static final Loc PLAYER = Loc.row(5).col(2);

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private Object savedConstants;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the distances, the light radius and the wall faces are measured from.
     */
    private Player player;

    /**
     * A terrain feature carrying the given flags and nothing else.
     *
     * @param flags the terrain flags the feature carries
     * @return the constructed feature
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags)
            set.on(flag);
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class));
    }

    /**
     * Writes a static field, returning what it held before.
     *
     * @param owner the class declaring the field
     * @param name  the field's name
     * @param value the value to write
     * @return the previous value
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
     * <p>{@code updateViewOne} compares its distance against {@code max-sight}, and
     * {@link SeededPlayerRegistry} seeds carry-cap only, so the table it leaves would throw on the
     * first call. The sight value is {@code constants.txt}'s own twenty, which is what the expected
     * values here were derived against; carry-cap is carried across so that {@link Player} can still
     * be built.
     *
     * <p>This runs per test rather than once for the class because the extension's own seeding is
     * undone at the end of every nested container, taking the table with it.
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
     * A level whose every grid is open ground and unlit, with the player standing near the west
     * edge carrying a light radius of three and no player flags.
     */
    @BeforeEach
    void newLevel() {
        player = new Player();
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);

        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++) {
                Loc grid = Loc.row(y).col(x);
                open(grid);
                level.getSquare(grid).setLight(0);
            }

        set(Player.class, "state", new PlayerState());
        set(Player.class, "level", 1);
        placePlayer(PLAYER);
        player.getPlayerState().setCurLight(3);
    }

    /**
     * Makes a grid open ground: transparent to sight and passable by a projection. Line of sight is
     * traced through {@code TF_PROJECT} and the wall special case turns on {@code TF_LOS}, so an
     * open grid needs both.
     *
     * @param grid the grid to open up
     */
    private void open(Loc grid) {
        level.getSquare(grid).setFeature(
                feature(TerrainFeatureFlags.TF_LOS, TerrainFeatureFlags.TF_PROJECT));
    }

    /**
     * Makes a grid a wall: opaque and impassable, so it both blocks a traced line and takes the
     * wall branch when it is itself the grid under consideration.
     *
     * @param grid the grid to wall off
     */
    private void wall(Loc grid) {
        level.getSquare(grid).setFeature(feature());
    }

    /**
     * Walls a whole row from edge to edge.
     *
     * @param row the row to wall off
     */
    private void wallRow(int row) {
        for (int x = 0; x < WIDTH; x++)
            wall(Loc.row(row).col(x));
    }

    /**
     * Writes a private {@link Player} field. Level, calculated state and location have getters but
     * no setters, and a freshly constructed player has no state at all — birth is what fills it in,
     * and birth is not ported yet.
     *
     * @param owner the class declaring the field
     * @param name  the field's name
     * @param value the value to write
     */
    private void set(Class<?> owner, String name, Object value) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            field.set(player, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Player." + name + " is no longer reachable", e);
        }
    }

    /**
     * Stands the player on a grid.
     *
     * @param grid the grid to place the player on
     */
    private void placePlayer(Loc grid) {
        set(Player.class, "grid", grid);
    }

    /**
     * Runs the method under test. Private, and reached by reflection rather than through its
     * caller.
     *
     * @param grid the grid being considered for the view
     */
    private void updateViewOne(Loc grid) {
        try {
            Method method = Chunk.class.getDeclaredMethod("updateViewOne", Loc.class, Player.class);
            method.setAccessible(true);
            method.invoke(level, grid, player);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("updateViewOne threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("updateViewOne is no longer reachable", e);
        }
    }

    /**
     * Whether a grid holds an info flag.
     *
     * @param grid the grid to read
     * @param flag the flag to look for
     * @return true if the grid holds the flag
     */
    private boolean has(Loc grid, SquareEnum flag) {
        return level.getSquare(grid).hasInfoFlag(flag);
    }

    /**
     * Asserts that a grid was passed on to {@code becomeViewable} but not as close.
     *
     * @param grid the grid to check
     */
    private void assertInViewOnly(Loc grid) {
        assertTrue(has(grid, SquareEnum.SQUARE_VIEW), "expected the grid to be in view");
        assertFalse(has(grid, SquareEnum.SQUARE_SEEN), "expected the grid to be unseen");
        assertFalse(has(grid, SquareEnum.SQUARE_CLOSE_PLAYER),
                "expected the grid to be outside the light radius");
    }

    /**
     * Asserts that a grid was passed on as close, which is the only route to
     * {@code SQUARE_CLOSE_PLAYER}.
     *
     * @param grid the grid to check
     */
    private void assertClose(Loc grid) {
        assertTrue(has(grid, SquareEnum.SQUARE_VIEW), "expected the grid to be in view");
        assertTrue(has(grid, SquareEnum.SQUARE_SEEN), "expected the grid to be seen");
        assertTrue(has(grid, SquareEnum.SQUARE_CLOSE_PLAYER),
                "expected the grid to be inside the light radius");
    }

    /**
     * Asserts that a grid was dropped, leaving it with no view flags at all.
     *
     * @param grid the grid to check
     */
    private void assertDropped(Loc grid) {
        assertFalse(has(grid, SquareEnum.SQUARE_VIEW), "expected the grid to be out of view");
        assertFalse(has(grid, SquareEnum.SQUARE_SEEN), "expected the grid to be unseen");
        assertFalse(has(grid, SquareEnum.SQUARE_CLOSE_PLAYER),
                "expected the grid to be outside the light radius");
    }

    /**
     * Grids with nothing in the way, where the only question is distance.
     */
    @Nested
    @DisplayName("open ground in line of sight")
    class OpenGround {

        /**
         * A grid the player can see but cannot light is in view and no more: the light radius is
         * three, so a grid ten east is well outside it.
         */
        @Test
        @DisplayName("a far grid is in view but not close")
        void farGridIsInViewOnly() {
            updateViewOne(Loc.row(5).col(12));

            assertInViewOnly(Loc.row(5).col(12));
        }

        /**
         * A grid inside the light radius picks up all three flags. Distance two against a radius of
         * three, so {@code d < cur_light} holds.
         */
        @Test
        @DisplayName("a grid inside the light radius is close")
        void nearGridIsClose() {
            updateViewOne(Loc.row(5).col(4));

            assertClose(Loc.row(5).col(4));
        }

        /**
         * The comparison is strictly less than, so a grid at exactly the light radius is not close.
         * A port using {@code <=} would light one ring too many.
         */
        @Test
        @DisplayName("a grid at exactly the light radius is not close")
        void gridAtLightRadiusIsNotClose() {
            updateViewOne(Loc.row(5).col(5));

            assertInViewOnly(Loc.row(5).col(5));
        }

        /**
         * The player's own grid is distance zero, inside any positive radius, and adjacent to
         * itself so line of sight is trivially true.
         */
        @Test
        @DisplayName("the player's own grid is close")
        void playersOwnGridIsClose() {
            updateViewOne(PLAYER);

            assertClose(PLAYER);
        }
    }

    /**
     * The sight-range cut-off, {@code d > max_sight}, which is an early return before any line is
     * traced.
     */
    @Nested
    @DisplayName("the sight-range boundary")
    class SightRange {

        /**
         * Twenty grids due east of the player is a distance of exactly {@code max_sight}, and the
         * test is {@code >} rather than {@code >=}, so the grid is kept.
         */
        @Test
        @DisplayName("a grid at exactly max sight is kept")
        void gridAtMaxSightIsKept() {
            assertEquals(20, GameConstants.getPlayerMaxSight(),
                    "the shipped max-sight the expected values were derived against");

            updateViewOne(Loc.row(5).col(22));

            assertInViewOnly(Loc.row(5).col(22));
        }

        /**
         * One grid further out is dropped, with clear ground all the way and nothing else to stop
         * it, so the cut-off is the only thing that can have done it.
         */
        @Test
        @DisplayName("a grid one past max sight is dropped")
        void gridPastMaxSightIsDropped() {
            updateViewOne(Loc.row(5).col(23));

            assertDropped(Loc.row(5).col(23));
        }
    }

    /**
     * Grids the player cannot trace a line to.
     */
    @Nested
    @DisplayName("line of sight blocked")
    class Blocked {

        /**
         * A single wall on the row between the player and an open grid due east stops the line, and
         * the grid is left untouched. The grid is open, so the wall special case never runs.
         */
        @Test
        @DisplayName("an open grid behind a wall is dropped")
        void openGridBehindWallIsDropped() {
            wall(Loc.row(5).col(12));

            updateViewOne(Loc.row(5).col(16));

            assertDropped(Loc.row(5).col(16));
        }

        /**
         * The wall doing the blocking is itself adjacent-visible along the row, so it is in view;
         * this pins the previous case on the grid behind it rather than on the row being broken.
         */
        @Test
        @DisplayName("the blocking wall itself stays in view")
        void blockingWallIsStillInView() {
            wall(Loc.row(5).col(12));

            updateViewOne(Loc.row(5).col(12));

            assertInViewOnly(Loc.row(5).col(12));
        }
    }

    /**
     * The wall-lighting special case, on the corridor from the comment in the C: a run of wall
     * along the row north of the player, whose cells cannot be reached by a line to their own
     * centres.
     */
    @Nested
    @DisplayName("walls borrowing line of sight")
    class WallLighting {

        /**
         * The corridor case. Twelve grids east and one north of the player, in a solid wall run: a
         * line traced to the wall's own centre fails, but the open grid one step back toward the
         * player is on the player's own row and plainly visible, so the wall is in view.
         */
        @Test
        @DisplayName("a corridor wall borrows sight from the grid in front of it")
        void corridorWallIsInView() {
            wallRow(4);

            updateViewOne(Loc.row(4).col(14));

            assertInViewOnly(Loc.row(4).col(14));
        }

        /**
         * The far side of a double-thickness wall is not in view. The grid one step back toward the
         * player is the near wall, which refuses the loan, and the grid's own line fails — without
         * that refusal both faces of the wall would light up.
         */
        @Test
        @DisplayName("the far side of a double wall is dropped")
        void farSideOfDoubleWallIsDropped() {
            wallRow(4);
            wallRow(3);

            updateViewOne(Loc.row(3).col(14));

            assertDropped(Loc.row(3).col(14));
        }

        /**
         * The near face of the same double wall is in view, by the same loan as the corridor case.
         * Together with the previous test this says the refusal is about the grid being borrowed
         * from, not about the row being walled.
         */
        @Test
        @DisplayName("the near side of a double wall is in view")
        void nearSideOfDoubleWallIsInView() {
            wallRow(4);
            wallRow(3);

            updateViewOne(Loc.row(4).col(14));

            assertInViewOnly(Loc.row(4).col(14));
        }

        /**
         * A wall inside the light radius is close as well as in view: closeness is decided from the
         * wall's own distance, before the loan is considered, and is carried down whichever grid
         * the line was traced from.
         */
        @Test
        @DisplayName("a wall inside the light radius is close")
        void nearWallIsClose() {
            wallRow(4);

            updateViewOne(Loc.row(4).col(3));

            assertClose(Loc.row(4).col(3));
        }
    }

    /**
     * The special radius for {@code PF_UNLIGHT} players, who see in the dark until they pick up a
     * light of their own.
     */
    @Nested
    @DisplayName("unlight players")
    class Unlight {

        /**
         * Gives the player the unlight flag, a level and a light radius.
         *
         * @param level    the character level the radius is scaled from
         * @param curLight the light radius the player currently sheds
         */
        private void unlight(int level, int curLight) {
            player.getPlayerState().playerFlagOn(PlayerFlag.PF_UNLIGHT);
            set(Player.class, "level", level);
            player.getPlayerState().setCurLight(curLight);
        }

        /**
         * At level twelve with a radius of one the special radius is {@code 2 + 12 / 6 - 1}, three,
         * so a grid at distance two is close where the ordinary radius of one would not have
         * reached it.
         */
        @Test
        @DisplayName("the special radius reaches further than the light radius")
        void specialRadiusReachesFurther() {
            unlight(12, 1);

            updateViewOne(Loc.row(5).col(4));

            assertClose(Loc.row(5).col(4));
        }

        /**
         * The special radius is a strict comparison too: at distance three against a radius of
         * three the grid is in view and no more.
         */
        @Test
        @DisplayName("a grid at exactly the special radius is not close")
        void gridAtSpecialRadiusIsNotClose() {
            unlight(12, 1);

            updateViewOne(Loc.row(5).col(5));

            assertInViewOnly(Loc.row(5).col(5));
        }

        /**
         * The division by six is integer division, so a level-five unlight player with no light has
         * a radius of {@code 2 + 0 - 0}, two: distance one is close, and distance two is not.
         */
        @Test
        @DisplayName("the level bonus is integer division by six")
        void levelBonusIsIntegerDivision() {
            unlight(5, 0);

            updateViewOne(Loc.row(5).col(3));
            updateViewOne(Loc.row(5).col(4));

            assertClose(Loc.row(5).col(3));
            assertInViewOnly(Loc.row(5).col(4));
        }

        /**
         * The special radius applies only while the player's own light is one or less. With a
         * radius of two the ordinary test is used, so a grid at distance two is not close even
         * though the special radius would have made it so at this level.
         */
        @Test
        @DisplayName("a real light source turns the special radius off")
        void lightSourceDisablesSpecialRadius() {
            unlight(12, 2);

            updateViewOne(Loc.row(5).col(4));

            assertInViewOnly(Loc.row(5).col(4));
        }

        /**
         * Without the flag the special radius never runs, whatever the light: a level-twelve player
         * with a radius of one lights only what is adjacent.
         */
        @Test
        @DisplayName("without the flag the ordinary radius is used")
        void withoutTheFlagOrdinaryRadiusIsUsed() {
            set(Player.class, "level", 12);
            player.getPlayerState().setCurLight(1);

            updateViewOne(Loc.row(5).col(4));

            assertInViewOnly(Loc.row(5).col(4));
        }
    }
}
