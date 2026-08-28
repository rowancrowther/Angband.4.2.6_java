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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.glowCanLightWall}, the port of C's {@code glow_can_light_wall}
 * ({@code cave-view.c}).
 *
 * <p>The method asks whether a permanently glowing wall grid will look lit to the player, and it
 * answers by consulting at most three grids: the single step from the wall towards the player, and
 * the two grids flanking that step. Every expected value below is worked out from the C, by walking
 * the same three grids and, where the C hands off to {@code source_can_light_wall}, following that
 * function through to the grid whose transparency it ends on.
 *
 * <p>The fixture is therefore built around bearings rather than distances. The player is stood far
 * enough away that only the bearing to them matters, and the grids the method should consult are
 * furnished one at a time so that each branch is reached on its own: a glowing grid that a mistaken
 * pairing would consult is left dark, so a wrong index reads as {@code false} rather than passing by
 * luck.
 *
 * <p>Two of the branches differ only in their bounds testing. For a diagonal bearing the flanking
 * grids are orthogonally adjacent to the wall and cannot leave the level, and C tests no bounds; for
 * a straight bearing they sit on a diagonal from the wall and C bounds-tests them. The edge cases
 * put the wall against a border so that one flanker falls outside the level, and assert both that
 * nothing is thrown and that the remaining flanker still decides the answer.
 *
 * <p>Every grid of the level is given a terrain feature during setup. A freshly built {@link Chunk}
 * fills itself with squares carrying a null feature, and {@link Square#featAllowsLOS} reads that
 * feature directly, so an unfurnished level would fail these tests with a null pointer rather than a
 * wrong answer.
 *
 * <p>The method under test is private, and its only caller is not yet finished, so the tests reach
 * it by reflection. That is also how the player is placed: {@link Player} exposes
 * {@link Player#getGrid()} but nothing to set it, and the tests need the player somewhere other than
 * the origin.
 *
 * <p>Class ChunkGlowCanLightWallTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkGlowCanLightWallTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 11;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 9;

    /**
     * The glowing wall under test in most cases. Off the diagonal, and clear of every edge so that
     * all eight of its neighbours are inside the level.
     */
    private static final Loc WALL = Loc.row(4).col(5);

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player whose view of the lit face is being judged.
     */
    private Player player;

    /**
     * A terrain feature carrying the given flags and nothing else. Only {@code TF_LOS} is read here,
     * through {@link Square#featAllowsLOS}, so the rest of the feature is left empty rather than
     * built from the registry.
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
     * A level whose every grid is open ground and dark, with the wall under test the only
     * obstruction. Tests light and wall off individual grids as each branch requires.
     */
    @BeforeEach
    void newLevel() {
        player = new Player();
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);

        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                open(Loc.row(y).col(x));

        wall(WALL);
    }

    /**
     * Makes a grid transparent, so that it can act as a glowing source and so that a view passing
     * through it is not blocked.
     *
     * @param grid the grid to open up
     */
    private void open(Loc grid) {
        level.getSquare(grid).setFeature(feature(TerrainFeatureFlags.TF_LOS));
    }

    /**
     * Makes a grid opaque, so that a view passing through it is blocked.
     *
     * @param grid the grid to wall off
     */
    private void wall(Loc grid) {
        level.getSquare(grid).setFeature(feature());
    }

    /**
     * Marks a grid as permanently glowing, the flag the method looks for on the grids around the
     * wall.
     *
     * @param grid the grid to light
     */
    private void glow(Loc grid) {
        level.getSquare(grid).sqInfoOn(SquareEnum.SQUARE_GLOW);
    }

    /**
     * Stands the player on a grid. {@link Player} has no setter for its location, and the tests need
     * the player somewhere other than the origin, so the field is written directly.
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
     * Asks the method under test whether the glowing wall appears lit. Private, and reached by
     * reflection rather than through its caller, which is not yet finished.
     *
     * @param wallGrid the glowing wall being tested
     * @return the method's answer
     */
    private boolean glowCanLight(Loc wallGrid) {
        try {
            Method method = Chunk.class.getDeclaredMethod("glowCanLightWall", Player.class, Loc.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(level, player, wallGrid);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("glowCanLightWall threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("glowCanLightWall is no longer reachable", e);
        }
    }

    /**
     * The first branch: the player is standing in the wall grid itself, so the bearing to them is
     * {@code DIR_NONE} and the step lands back on the wall.
     */
    @Nested
    @DisplayName("the player in the wall")
    class Coincident {

        /**
         * A player inside the wall sees every face, so the wall looks lit whatever surrounds it. The
         * level is left entirely dark to confirm no glowing grid is needed for this answer.
         */
        @Test
        @DisplayName("a player in the wall sees the lit face with nothing glowing")
        void playerInWallAlwaysSees() {
            placePlayer(WALL);

            assertTrue(glowCanLight(WALL));
        }
    }

    /**
     * The second branch: the grid one step towards the player is itself open and glowing, so it
     * lights the very face the player is looking at.
     */
    @Nested
    @DisplayName("the grid towards the player")
    class TowardsPlayer {

        /**
         * The straight case. With the player due east, the step is the grid immediately east of the
         * wall; open and glowing, it answers the question on its own.
         */
        @Test
        @DisplayName("an open glowing grid towards the player lights the wall")
        void openGlowingStepLights() {
            placePlayer(Loc.row(4).col(8));
            glow(Loc.row(4).col(6));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * The diagonal case, reached through a different arm of the branch below it, so it is worth
         * pinning separately.
         */
        @Test
        @DisplayName("an open glowing grid on a diagonal bearing lights the wall")
        void openGlowingDiagonalStepLights() {
            placePlayer(Loc.row(2).col(7));
            glow(Loc.row(3).col(6));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * Glow alone is not enough: a wall grid does not light its neighbour's face however brightly
         * it is marked, because the flag on a wall is what the caller is trying to resolve in the
         * first place. Here the step is walled off and nothing else glows.
         */
        @Test
        @DisplayName("a glowing but opaque grid towards the player does not light the wall")
        void glowingWallStepDoesNotLight() {
            placePlayer(Loc.row(4).col(8));
            wall(Loc.row(4).col(6));
            glow(Loc.row(4).col(6));

            assertFalse(glowCanLight(WALL));
        }
    }

    /**
     * The third branch on a diagonal bearing: the two flanking grids are the ones sharing one
     * coordinate with the wall and one with the step, and C bounds-tests neither.
     */
    @Nested
    @DisplayName("flanking a diagonal bearing")
    class DiagonalFlankers {

        /**
         * With the player to the north-east the step is the grid north-east of the wall. The first
         * flanker C tries takes its column from the step and its row from the wall — the grid due
         * east. Glowing it alone, and leaving the other flanker dark, fails a port that pairs the
         * coordinates the other way round.
         */
        @Test
        @DisplayName("the flanker sharing the wall's row lights the wall")
        void rowFlankerLights() {
            placePlayer(Loc.row(2).col(7));
            glow(Loc.row(4).col(6));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * The second flanker takes its row from the step and its column from the wall — the grid due
         * north. Glowing it alone is the mirror of the case above, and the pair together pin the
         * indexing down.
         */
        @Test
        @DisplayName("the flanker sharing the wall's column lights the wall")
        void columnFlankerLights() {
            placePlayer(Loc.row(2).col(7));
            glow(Loc.row(3).col(5));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * A glowing grid two steps off is never consulted, however clear the line to it. Only the
         * step and its two flankers can make a glowing wall look lit.
         */
        @Test
        @DisplayName("a glowing grid beyond the flankers does not light the wall")
        void distantGlowDoesNotLight() {
            placePlayer(Loc.row(2).col(7));
            glow(Loc.row(2).col(7));
            glow(Loc.row(4).col(7));

            assertFalse(glowCanLight(WALL));
        }

        /**
         * Nothing adjacent glowing is the ordinary negative answer.
         */
        @Test
        @DisplayName("no adjacent glow leaves the wall unlit")
        void nothingGlowingIsFalse() {
            placePlayer(Loc.row(2).col(7));

            assertFalse(glowCanLight(WALL));
        }
    }

    /**
     * The third branch on a straight bearing: the flanking grids sit on a diagonal from the wall and
     * are bounds-tested, and each has to pass {@code source_can_light_wall} as a light source of its
     * own before it counts.
     */
    @Nested
    @DisplayName("flanking a straight bearing")
    class StraightFlankers {

        /**
         * The player due east, the step east of the wall open but dark, and the grid north-east of
         * the wall glowing. Followed through {@code source_can_light_wall}, the view of that lit
         * face passes through the step, which is open, so the wall reads as lit.
         */
        @Test
        @DisplayName("the flanker above a horizontal bearing lights the wall")
        void flankerAboveLights() {
            placePlayer(Loc.row(4).col(8));
            glow(Loc.row(3).col(6));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * The flanker on the other side of the same bearing, checked separately because C tries them
         * in turn and a port could drop either.
         */
        @Test
        @DisplayName("the flanker below a horizontal bearing lights the wall")
        void flankerBelowLights() {
            placePlayer(Loc.row(4).col(8));
            glow(Loc.row(5).col(6));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * The flanking grids are not taken on their glow alone. Walling the step blocks the player's
         * view of the face the flanker lights, which is what {@code source_can_light_wall} ends on,
         * so the answer turns false even with the flanker open and glowing.
         */
        @Test
        @DisplayName("a blocked view of the lit face leaves the wall unlit")
        void blockedFaceIsNotLit() {
            placePlayer(Loc.row(4).col(8));
            wall(Loc.row(4).col(6));
            glow(Loc.row(3).col(6));

            assertFalse(glowCanLight(WALL));
        }

        /**
         * The vertical bearing runs through the other arm of the branch, where the flankers differ in
         * column rather than row. The player due south, and the grid south-west of the wall glowing.
         */
        @Test
        @DisplayName("a flanker beside a vertical bearing lights the wall")
        void verticalFlankerLights() {
            placePlayer(Loc.row(7).col(5));
            glow(Loc.row(5).col(4));

            assertTrue(glowCanLight(WALL));
        }

        /**
         * Neither flanker glowing, on a straight bearing, is the negative answer for this arm.
         */
        @Test
        @DisplayName("no glowing flanker leaves the wall unlit")
        void noFlankerGlowingIsFalse() {
            placePlayer(Loc.row(4).col(8));

            assertFalse(glowCanLight(WALL));
        }
    }

    /**
     * The straight bearing with the wall against a border, where one flanker falls outside the level
     * and C's bounds test is what keeps the lookup legal.
     */
    @Nested
    @DisplayName("flankers off the edge")
    class Edges {

        /**
         * A wall on the top row with the player due east: the first flanker C tries is the row above,
         * which is outside the level. The second, inside, is glowing, so the answer is true — and
         * reaching it at all means the out-of-bounds grid was refused rather than read.
         */
        @Test
        @DisplayName("an out-of-bounds flanker is skipped and the other still decides")
        void outOfBoundsFlankerSkipped() {
            Loc edgeWall = Loc.row(0).col(5);
            wall(edgeWall);
            placePlayer(Loc.row(0).col(8));
            glow(Loc.row(1).col(6));

            assertTrue(glowCanLight(edgeWall));
        }

        /**
         * The same corner of the level with nothing glowing: the out-of-bounds flanker must read as
         * no light rather than as an exception or a stray true.
         */
        @Test
        @DisplayName("an out-of-bounds flanker cannot light the wall")
        void outOfBoundsFlankerIsNotLight() {
            Loc edgeWall = Loc.row(0).col(5);
            wall(edgeWall);
            placePlayer(Loc.row(0).col(8));

            assertFalse(glowCanLight(edgeWall));
        }

        /**
         * The vertical arm of the same branch, against the left-hand border: the flanker a column to
         * the west is outside the level, and the one to the east is glowing.
         */
        @Test
        @DisplayName("an out-of-bounds flanker on a vertical bearing is skipped")
        void outOfBoundsVerticalFlankerSkipped() {
            Loc edgeWall = Loc.row(4).col(0);
            wall(edgeWall);
            placePlayer(Loc.row(7).col(0));
            glow(Loc.row(5).col(1));

            assertTrue(glowCanLight(edgeWall));
        }
    }
}
