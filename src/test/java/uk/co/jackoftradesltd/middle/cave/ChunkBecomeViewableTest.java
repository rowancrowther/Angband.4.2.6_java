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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.becomeViewable}, the port of C's {@code become_viewable}
 * ({@code cave-view.c}).
 *
 * <p>The method sets {@code SQUARE_VIEW} on a grid the caller has already established a line to,
 * and then decides separately whether the grid is also {@code SQUARE_SEEN}. Two independent routes
 * lead to seen: the {@code close} argument, meaning the grid is inside the player's own light
 * radius, which also brings {@code SQUARE_CLOSE_PLAYER} with it; and the grid being lit, which in
 * C is {@code square_islit} — {@code square_light() > 0}, the accumulated light level — and not the
 * {@code SQUARE_GLOW} flag. The distinction is the point of several tests below: a grid with light
 * and no glow must be seen, and a grid with glow and no light must not.
 *
 * <p>Walls take a longer path. Light never reaches the face the player looks at from the wall's own
 * grid, so C tests the grid one step back toward the player instead, stepping each coordinate
 * independently and leaving one alone when it already matches the player's. The expected values
 * here are read off that C arithmetic, so a port that tested the wall's own grid, or that stepped
 * both coordinates together, would fail rather than agree with itself.
 *
 * <p>The early return on a grid already in view matters because the visibility sweep can reach a
 * grid more than once; a second call must not upgrade a grid to seen that the first call withheld.
 *
 * <p>Line of sight is not exercised — the caller, {@code updateViewOne}, has already decided that.
 * Every grid is given a terrain feature during setup so the wall tests turn on
 * {@code featAllowsLOS} alone.
 *
 * <p>The method under test is private and is reached by reflection, as is the player's location,
 * which {@link Player} exposes a getter for but no setter.
 *
 * <p>Class ChunkBecomeViewableTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkBecomeViewableTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 11;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 9;

    /**
     * Where the player stands. Off the diagonal, and clear of every edge.
     */
    private static final Loc PLAYER = Loc.row(4).col(5);

    /**
     * The grid brought into view in most cases. Two columns east of the player and on the same row,
     * so the grid one step back toward the player is unambiguous.
     */
    private static final Loc TARGET = Loc.row(4).col(7);

    /**
     * The grid between {@link #TARGET} and the player — the one C consults when the target is a
     * wall.
     */
    private static final Loc APPROACH = Loc.row(4).col(6);

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the light and the wall faces are measured from.
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
     * A level whose every grid is open ground, unlit and holding no info flags, with the player
     * placed clear of the edges.
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

        placePlayer(PLAYER);
    }

    /**
     * Makes a grid open ground: transparent to sight and passable by a projection.
     *
     * @param grid the grid to open up
     */
    private void open(Loc grid) {
        level.getSquare(grid).setFeature(
                feature(TerrainFeatureFlags.TF_LOS, TerrainFeatureFlags.TF_PROJECT));
    }

    /**
     * Makes a grid a wall: opaque, so {@code featAllowsLOS} is false for it.
     *
     * @param grid the grid to wall off
     */
    private void wall(Loc grid) {
        level.getSquare(grid).setFeature(feature());
    }

    /**
     * Stands the player on a grid. {@link Player} has no setter for its location, so the field is
     * written directly.
     *
     * @param grid the grid to place the player on
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
     * Runs the method under test. Private, and reached by reflection rather than through its
     * caller.
     *
     * @param grid  the grid being brought into view
     * @param close whether the grid lies inside the player's light radius
     */
    private void becomeViewable(Loc grid, boolean close) {
        try {
            Method method = Chunk.class.getDeclaredMethod("becomeViewable",
                    Loc.class, Player.class, boolean.class);
            method.setAccessible(true);
            method.invoke(level, grid, player, close);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("becomeViewable threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("becomeViewable is no longer reachable", e);
        }
    }

    /**
     * Sets a grid's accumulated light level.
     *
     * @param grid  the grid to light
     * @param level the light level to give it
     */
    private void light(Loc grid, int level) {
        this.level.getSquare(grid).setLight(level);
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
     * The three flags on a grid that is neither close nor lit.
     */
    @Nested
    @DisplayName("in view but unlit")
    class UnlitAndFar {

        /**
         * View is set unconditionally, whatever the light. This is the whole of what the method
         * promises for a distant, dark grid.
         */
        @Test
        @DisplayName("view is set")
        void viewIsSet() {
            becomeViewable(TARGET, false);

            assertTrue(has(TARGET, SquareEnum.SQUARE_VIEW));
        }

        /**
         * Neither route to seen applies, so the grid stays unseen and out of the light radius.
         */
        @Test
        @DisplayName("seen and close-player are left alone")
        void seenAndClosePlayerAreNotSet() {
            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
            assertFalse(has(TARGET, SquareEnum.SQUARE_CLOSE_PLAYER));
        }

        /**
         * The method touches the grid it is given and no other. The grid between it and the player
         * is read in the wall case but never written in any case.
         */
        @Test
        @DisplayName("no other grid is touched")
        void neighbourIsUntouched() {
            becomeViewable(TARGET, false);

            assertFalse(has(APPROACH, SquareEnum.SQUARE_VIEW));
            assertFalse(has(PLAYER, SquareEnum.SQUARE_VIEW));
        }
    }

    /**
     * The first route to seen: the grid is inside the player's own light radius.
     */
    @Nested
    @DisplayName("close to the player")
    class Close {

        /**
         * All three flags go on together. {@code SQUARE_CLOSE_PLAYER} is set by this route and by
         * no other.
         */
        @Test
        @DisplayName("view, seen and close-player are all set")
        void allThreeAreSet() {
            becomeViewable(TARGET, true);

            assertTrue(has(TARGET, SquareEnum.SQUARE_VIEW));
            assertTrue(has(TARGET, SquareEnum.SQUARE_SEEN));
            assertTrue(has(TARGET, SquareEnum.SQUARE_CLOSE_PLAYER));
        }

        /**
         * Closeness does not depend on the grid's own light: an unlit grid inside the player's
         * radius is still seen, because the player's light is what reaches it.
         */
        @Test
        @DisplayName("an unlit grid is seen anyway")
        void unlitGridIsStillSeen() {
            becomeViewable(TARGET, true);

            assertTrue(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * A wall close to the player is seen without the lit branch being consulted, so the state
         * of the grid in between does not matter.
         */
        @Test
        @DisplayName("a wall is seen without checking its approach")
        void closeWallIsSeen() {
            wall(TARGET);

            becomeViewable(TARGET, true);

            assertTrue(has(TARGET, SquareEnum.SQUARE_SEEN));
            assertTrue(has(TARGET, SquareEnum.SQUARE_CLOSE_PLAYER));
        }
    }

    /**
     * The second route to seen: the grid is lit. C tests the accumulated light level, not the glow
     * flag.
     */
    @Nested
    @DisplayName("lit but not close")
    class Lit {

        /**
         * An open grid with light on it is seen, without {@code SQUARE_CLOSE_PLAYER} — that flag
         * belongs to the closeness route alone.
         */
        @Test
        @DisplayName("a lit floor is seen but not close-player")
        void litFloorIsSeen() {
            light(TARGET, 1);

            becomeViewable(TARGET, false);

            assertTrue(has(TARGET, SquareEnum.SQUARE_VIEW));
            assertTrue(has(TARGET, SquareEnum.SQUARE_SEEN));
            assertFalse(has(TARGET, SquareEnum.SQUARE_CLOSE_PLAYER));
        }

        /**
         * The light test is {@code > 0}, so a level of zero is dark. This is the boundary either
         * side of which C's {@code square_islit} flips.
         */
        @Test
        @DisplayName("a light level of zero is dark")
        void zeroLightIsDark() {
            light(TARGET, 0);

            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * A negative level — what a darkness source leaves behind — is dark too, and would pass a
         * test written as {@code != 0}.
         */
        @Test
        @DisplayName("a negative light level is dark")
        void negativeLightIsDark() {
            light(TARGET, -2);

            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * Light, not glow, is what C reads. A grid carrying {@code SQUARE_GLOW} whose light has
         * been driven to zero is dark — the case that separates {@code square_islit} from
         * {@code square_isglow}.
         */
        @Test
        @DisplayName("glow without light is dark")
        void glowWithoutLightIsDark() {
            level.getSquare(TARGET).sqInfoOn(SquareEnum.SQUARE_GLOW);
            light(TARGET, 0);

            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * The other half of that pair: light without glow, which is every grid inside a torch's
         * reach in an unlit corridor, is seen.
         */
        @Test
        @DisplayName("light without glow is seen")
        void lightWithoutGlowIsSeen() {
            light(TARGET, 2);

            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_GLOW));
            assertTrue(has(TARGET, SquareEnum.SQUARE_SEEN));
        }
    }

    /**
     * The wall branch, where the light that matters is on the grid one step back toward the player.
     */
    @Nested
    @DisplayName("a lit wall")
    class LitWall {

        /**
         * The wall is lit and so is its approach, so the face the player sees is lit and the wall is
         * seen.
         */
        @Test
        @DisplayName("is seen when the grid toward the player is lit")
        void litApproachIsSeen() {
            wall(TARGET);
            light(TARGET, 1);
            light(APPROACH, 1);

            becomeViewable(TARGET, false);

            assertTrue(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * The wall's own light is not enough. With a dark approach the visible face is unlit, and
         * the wall stays unseen — a port that tested the wall's own grid twice would pass it.
         */
        @Test
        @DisplayName("is unseen when the grid toward the player is dark")
        void darkApproachIsUnseen() {
            wall(TARGET);
            light(TARGET, 1);
            light(APPROACH, 0);

            becomeViewable(TARGET, false);

            assertTrue(has(TARGET, SquareEnum.SQUARE_VIEW));
            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * The approach is only consulted when the wall itself is lit — the outer test guards the
         * inner one. A dark wall with a lit approach is not seen.
         */
        @Test
        @DisplayName("is unseen when the wall itself is dark")
        void darkWallIsUnseen() {
            wall(TARGET);
            light(TARGET, 0);
            light(APPROACH, 3);

            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * Each coordinate steps toward the player on its own. For a wall one row north and two
         * columns east of the player, the grid consulted is the diagonal neighbour to its
         * south-west, not either orthogonal one.
         */
        @Test
        @DisplayName("steps both coordinates toward the player")
        void bothCoordinatesStep() {
            Loc corner = Loc.row(3).col(7);
            Loc diagonal = Loc.row(4).col(6);
            wall(corner);
            light(corner, 1);
            light(diagonal, 1);

            becomeViewable(corner, false);

            assertTrue(has(corner, SquareEnum.SQUARE_SEEN));
        }

        /**
         * The orthogonal neighbours of that same wall are not the grid C consults. Lighting them
         * instead of the diagonal leaves the wall unseen.
         */
        @Test
        @DisplayName("does not consult the orthogonal neighbours")
        void orthogonalNeighboursAreNotConsulted() {
            Loc corner = Loc.row(3).col(7);
            wall(corner);
            light(corner, 1);
            light(Loc.row(3).col(6), 1);
            light(Loc.row(4).col(7), 1);

            becomeViewable(corner, false);

            assertFalse(has(corner, SquareEnum.SQUARE_SEEN));
        }

        /**
         * A coordinate the player already shares is left alone rather than stepped. For a wall due
         * north of the player the column does not move, so the grid consulted is directly below the
         * wall.
         */
        @Test
        @DisplayName("leaves a shared coordinate alone")
        void sharedCoordinateDoesNotStep() {
            Loc north = Loc.row(2).col(5);
            wall(north);
            light(north, 1);
            light(Loc.row(3).col(5), 1);

            becomeViewable(north, false);

            assertTrue(has(north, SquareEnum.SQUARE_SEEN));
        }

        /**
         * A wall on the player's own grid steps nowhere: both coordinates match, so the grid
         * consulted is the wall itself, and its own light decides.
         */
        @Test
        @DisplayName("on the player's grid consults itself")
        void wallOnPlayerGridConsultsItself() {
            wall(PLAYER);
            light(PLAYER, 1);

            becomeViewable(PLAYER, false);

            assertTrue(has(PLAYER, SquareEnum.SQUARE_SEEN));
        }
    }

    /**
     * The early return, which makes a second call on a grid already in view do nothing.
     */
    @Nested
    @DisplayName("a grid already in view")
    class AlreadyInView {

        /**
         * A grid that picked up view without seen on the first call is not upgraded by a later call
         * that passes {@code close}. The guard returns before either flag is written.
         */
        @Test
        @DisplayName("is not upgraded to seen by a later close call")
        void notUpgradedToSeen() {
            becomeViewable(TARGET, false);

            becomeViewable(TARGET, true);

            assertTrue(has(TARGET, SquareEnum.SQUARE_VIEW));
            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
            assertFalse(has(TARGET, SquareEnum.SQUARE_CLOSE_PLAYER));
        }

        /**
         * Nor by light arriving between the two calls — the guard is read before the lit branch.
         */
        @Test
        @DisplayName("is not upgraded to seen by light arriving later")
        void notUpgradedByLight() {
            becomeViewable(TARGET, false);
            light(TARGET, 3);

            becomeViewable(TARGET, false);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
        }

        /**
         * The guard reads {@code SQUARE_VIEW} alone. A grid pre-marked with view but nothing else
         * is left exactly as it was, whatever the call would otherwise have done.
         */
        @Test
        @DisplayName("set by hand is left untouched")
        void preMarkedGridIsUntouched() {
            level.getSquare(TARGET).sqInfoOn(SquareEnum.SQUARE_VIEW);
            light(TARGET, 3);

            becomeViewable(TARGET, true);

            assertFalse(has(TARGET, SquareEnum.SQUARE_SEEN));
            assertFalse(has(TARGET, SquareEnum.SQUARE_CLOSE_PLAYER));
        }
    }
}
