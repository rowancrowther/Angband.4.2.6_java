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
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code Chunk.addLight}, the port of C's {@code add_light} ({@code cave-view.c}).
 *
 * <p>The method sweeps the bounding square of side {@code 2 * radius + 1} around a light source and
 * adds a distance-dependent contribution to every grid that survives four filters — in bounds,
 * within the approximate radius, reachable by line of sight, and, for a wall, showing the player a
 * face this source lights. The expected light levels here are worked out from the C arithmetic
 * ({@code inten - dist} for a light, {@code inten + dist} for an unlight) using C's integer
 * {@code distance} approximation, not by reading the port back.
 *
 * <p>That approximation is what makes the corners of the swept square interesting: with a radius of
 * two the grid two rows and two columns away scores three, not two, so the swept square is rounded
 * off to a rough disc and its four corners are left dark. A port that compared the raw offsets
 * instead would light them.
 *
 * <p>Light accumulates rather than replaces, so several tests give a grid a starting level and
 * assert the sum. That is how the method is actually used: {@code calcLighting} lays down a base
 * from glowing terrain first, then adds each source on top.
 *
 * <p>Every grid of the level is given a terrain feature during setup, carrying both {@code TF_LOS}
 * and {@code TF_PROJECT} for open ground — line of sight is traced through
 * {@link Chunk#squareIsProjectable(Loc)} while the wall test uses {@code featAllowsLOS}, and a grid
 * that was open for one and not the other would make the fixture lie.
 *
 * <p>The method under test is private, and its only caller is not yet finished, so the tests reach
 * it by reflection. That is also how the player is placed: {@link Player} exposes
 * {@link Player#getGrid()} but nothing to set it.
 *
 * <p>Class ChunkAddLightTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkAddLightTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 11;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 9;

    /**
     * The light source in most cases. Off the diagonal, and far enough from every edge that a
     * radius of three stays inside the level.
     */
    private static final Loc SOURCE = Loc.row(4).col(5);

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player whose view of a lit wall face is being judged.
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
     * A level whose every grid is open ground and unlit, with the player standing on the light
     * source. Tests that care about walls or about a starting light level add them.
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

        placePlayer(SOURCE);
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
     * Makes a grid a wall: opaque, and blocking the line of sight traced through it.
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
     * caller, which is not yet finished.
     *
     * @param source the grid the light is emitted from
     * @param radius the reach of the source in grids
     * @param inten  the intensity at the source
     */
    private void addLight(Loc source, int radius, int inten) {
        try {
            Method method = Chunk.class.getDeclaredMethod("addLight",
                    Player.class, Loc.class, int.class, int.class);
            method.setAccessible(true);
            method.invoke(level, player, source, radius, inten);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("addLight threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("addLight is no longer reachable", e);
        }
    }

    /**
     * The light level a grid ended up holding.
     *
     * @param grid the grid to read
     * @return its light level
     */
    private int light(Loc grid) {
        return level.getSquare(grid).getLight();
    }

    /**
     * The shape of the contribution on an empty level, where no filter but distance applies.
     */
    @Nested
    @DisplayName("on open ground")
    class OpenGround {

        /**
         * A light of intensity three and radius two contributes {@code 3 - dist}. The source itself
         * takes the full three, the eight grids around it two — the diagonal neighbours included,
         * because C's approximation puts them at distance one — and the ring beyond one.
         */
        @Test
        @DisplayName("the contribution falls off with distance")
        void contributionFallsOffWithDistance() {
            addLight(SOURCE, 2, 3);

            assertEquals(3, light(SOURCE));
            assertEquals(2, light(Loc.row(4).col(6)));
            assertEquals(2, light(Loc.row(3).col(5)));
            assertEquals(2, light(Loc.row(3).col(4)));
            assertEquals(1, light(Loc.row(4).col(7)));
            assertEquals(1, light(Loc.row(2).col(5)));
            assertEquals(1, light(Loc.row(3).col(7)));
        }

        /**
         * The corners of the swept square sit two rows and two columns out, which C's approximation
         * scores as three, not two. They are past the radius and stay dark.
         */
        @Test
        @DisplayName("the corners of the swept square are past the radius")
        void cornersOfTheSweptSquareAreDark() {
            addLight(SOURCE, 2, 3);

            assertEquals(0, light(Loc.row(2).col(3)));
            assertEquals(0, light(Loc.row(2).col(7)));
            assertEquals(0, light(Loc.row(6).col(3)));
            assertEquals(0, light(Loc.row(6).col(7)));
        }

        /**
         * Nothing outside the swept square is visited at all.
         */
        @Test
        @DisplayName("grids outside the swept square are untouched")
        void gridsOutsideTheSweptSquareAreUntouched() {
            addLight(SOURCE, 2, 3);

            assertEquals(0, light(Loc.row(4).col(8)));
            assertEquals(0, light(Loc.row(1).col(5)));
        }

        /**
         * The contribution is added to what the grid already holds, not written over it — the base
         * level {@code calcLighting} lays down from glowing terrain has to survive.
         */
        @Test
        @DisplayName("the contribution is added to the level already there")
        void contributionIsAdded() {
            level.getSquare(SOURCE).setLight(5);
            level.getSquare(Loc.row(4).col(6)).setLight(1);

            addLight(SOURCE, 2, 3);

            assertEquals(8, light(SOURCE));
            assertEquals(3, light(Loc.row(4).col(6)));
        }

        /**
         * A negative intensity is an unlight source, and the sign of the distance term flips with
         * it: {@code inten + dist}, darkest at the source and weakening outwards. Applied over lit
         * ground it can leave a grid darker than it started or, at the rim, no darker at all.
         */
        @Test
        @DisplayName("a negative intensity darkens, weakening with distance")
        void negativeIntensityDarkensWeakeningOutwards() {
            addLight(SOURCE, 2, -3);

            assertEquals(-3, light(SOURCE));
            assertEquals(-2, light(Loc.row(4).col(6)));
            assertEquals(-1, light(Loc.row(4).col(7)));
        }

        /**
         * A radius below zero — what a player carrying no light at all produces — makes both loops
         * empty, and the level is left exactly as it was.
         */
        @Test
        @DisplayName("a negative radius changes nothing")
        void negativeRadiusIsANoOp() {
            level.getSquare(SOURCE).setLight(7);

            addLight(SOURCE, -1, 0);

            assertEquals(7, light(SOURCE));
        }

        /**
         * Half the swept square falls outside a level when the source sits in the corner. Those
         * grids are skipped, and the ones inside are lit as usual.
         */
        @Test
        @DisplayName("a source in the corner sweeps off the level without complaint")
        void sourceInCornerIsClippedToTheLevel() {
            Loc corner = Loc.row(0).col(0);
            placePlayer(corner);

            addLight(corner, 2, 3);

            assertEquals(3, light(corner));
            assertEquals(2, light(Loc.row(0).col(1)));
            assertEquals(1, light(Loc.row(2).col(0)));
        }
    }

    /**
     * The two filters that stop light where the geometry says it should stop.
     */
    @Nested
    @DisplayName("with a wall in the way")
    class Walls {

        /**
         * Light does not propagate through a wall. The wall itself is adjacent to the source, so
         * the line to it is unbroken and it takes its share; everything behind it is left dark,
         * including grids well inside the radius.
         */
        @Test
        @DisplayName("light stops at a wall and does not reach past it")
        void lightDoesNotPassThroughAWall() {
            placePlayer(Loc.row(4).col(4));
            wall(Loc.row(4).col(6));

            addLight(SOURCE, 3, 4);

            assertEquals(3, light(Loc.row(4).col(6)));
            assertEquals(0, light(Loc.row(4).col(7)));
            assertEquals(0, light(Loc.row(4).col(8)));
        }

        /**
         * A wall is only worth lighting when the face this source lights is the face the player is
         * looking at. With the player on the far side of the wall from the source, the two look at
         * opposite faces and the wall is skipped — while the open ground on the source's side is
         * lit as normal, showing the filter is the wall test and not the line of sight.
         */
        @Test
        @DisplayName("a wall lit on a face the player cannot see is skipped")
        void wallLitOnAnUnseenFaceIsSkipped() {
            placePlayer(Loc.row(4).col(8));
            wall(Loc.row(4).col(6));

            addLight(SOURCE, 3, 4);

            assertEquals(0, light(Loc.row(4).col(6)));
            assertEquals(3, light(Loc.row(4).col(4)));
        }
    }
}
