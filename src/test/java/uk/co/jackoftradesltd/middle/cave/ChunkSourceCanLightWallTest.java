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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.sourceCanLightWall}, the port of C's {@code source_can_light_wall}
 * ({@code cave-view.c}).
 *
 * <p>The method decides one narrow thing: whether the face of a wall that a light source
 * illuminates is the same face the player is looking at. Everything it works from is a bearing —
 * one step from the wall towards the light, one step from the wall towards the player — so the
 * fixture is built entirely out of where the three parties stand relative to each other, and
 * distances are chosen to be irrelevant. Two of the tests put the light source several grids away
 * behind a solid wall precisely to pin that down: the method is documented as answering
 * "regardless of line-of-sight details", and a port that quietly started tracing a line would pass
 * every adjacent-source test and fail those.
 *
 * <p>The interesting failures all live in the last branch. When the two bearings share exactly one
 * component the answer depends on a third grid, beside the wall, that the player's view of the lit
 * face has to pass through — and there are two ways to get that grid wrong that no simple case
 * catches. It can be built from the wrong component pairing, giving the grid on the other axis, and
 * it can be built transposed. Both are tested by walling the grid the code should consult and,
 * separately, the grid a mistake would consult, and asserting the two give opposite answers.
 *
 * <p>Every grid of the level is given a terrain feature during setup. A freshly built
 * {@link Chunk} fills itself with squares carrying a null feature, and
 * {@link Square#featAllowsLOS} reads that feature directly, so an unfurnished level would fail
 * these tests with a null pointer rather than a wrong answer.
 *
 * <p>The method under test is private, and its only caller is not yet finished, so the tests reach
 * it by reflection. That is also how the player is placed: {@link Player} exposes
 * {@link Player#getGrid()} but nothing to set it, and the tests need the player somewhere other
 * than the origin.
 *
 * <p>Class ChunkSourceCanLightWallTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkSourceCanLightWallTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 11;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 9;

    /**
     * The wall under test in most cases. Off the diagonal, and clear of every edge so that all
     * eight of its neighbours are inside the level.
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
     * A terrain feature carrying the given flags and nothing else. Only {@code TF_LOS} is read
     * here, through {@link Square#featAllowsLOS}, so the rest of the feature is left empty rather
     * than built from the registry.
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
     * A level whose every grid is open ground, with the wall under test the only obstruction, and a
     * player attached. Tests add further walls where a blocked view is the point.
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
     * Makes a grid transparent, so that a view passing through it is not blocked.
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
     * Stands the player on a grid. {@link Player} has no setter for its location, and the tests
     * need the player somewhere other than the origin, so the field is written directly.
     *
     * @param grid the grid to place the player on; need not be inside the level
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
     * Asks the method under test whether the wall appears lit. Private, and reached by reflection
     * rather than through its caller, which is not yet finished.
     *
     * @param source   the grid the light is emitted from
     * @param wallGrid the grid of the wall being lit
     * @return the method's answer
     */
    private boolean canLight(Loc source, Loc wallGrid) {
        try {
            Method method = Chunk.class.getDeclaredMethod("sourceCanLightWall",
                    Player.class, Loc.class, Loc.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(level, player, source, wallGrid);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("sourceCanLightWall threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("sourceCanLightWall is no longer reachable", e);
        }
    }

    /**
     * The two cases where one party stands in the wall itself, and the bearing to it is
     * {@code DIR_NONE}. Both are answered before any face is worked out.
     */
    @Nested
    @DisplayName("standing in the wall")
    class Coincident {

        /**
         * A light source inside the wall lights every face, so whichever face the player is looking
         * at is a lit one. Nothing about the player's position can change that, so the player is
         * put where the general case would answer false.
         */
        @Test
        @DisplayName("a light source in the wall lights it whatever the player's bearing")
        void sourceInWallAlwaysLights() {
            placePlayer(Loc.row(6).col(7));

            assertTrue(canLight(WALL, WALL));
        }

        /**
         * A player inside the wall sees every face, so whichever face the light source lights is a
         * visible one. The source is put diagonally opposite, where the general case would answer
         * false.
         */
        @Test
        @DisplayName("a player in the wall sees it lit whatever the source's bearing")
        void playerInWallAlwaysSees() {
            placePlayer(WALL);

            assertTrue(canLight(Loc.row(2).col(7), WALL));
        }
    }

    /**
     * The cases decided by comparing the two bearings alone, with no third grid consulted.
     */
    @Nested
    @DisplayName("comparing the two bearings")
    class Bearings {

        /**
         * Light and player on the same side of the wall are looking at the same face. The grids
         * around the wall are left walled off to confirm nothing else is consulted once both
         * components match.
         */
        @Test
        @DisplayName("the same bearing lights the wall")
        void sameBearingLights() {
            placePlayer(Loc.row(4).col(8));
            wall(Loc.row(4).col(6));
            wall(Loc.row(3).col(6));
            wall(Loc.row(5).col(6));

            assertTrue(canLight(Loc.row(4).col(7), WALL));
        }

        /**
         * Distance is not part of the question, and neither is a clear line to the source. A source
         * far away behind a solid wall still lights the face pointing towards it; the caller is
         * what decides whether the light reaches.
         */
        @Test
        @DisplayName("range and obstruction between wall and source are not consulted")
        void rangeAndObstructionAreNotConsulted() {
            placePlayer(Loc.row(4).col(7));
            wall(Loc.row(4).col(8));
            wall(Loc.row(4).col(9));

            assertTrue(canLight(Loc.row(4).col(10), WALL));
        }

        /**
         * Light and player on opposite sides of the wall are looking at opposite faces. The shared
         * component here names the wall itself as the grid to look through, which is opaque, so the
         * answer is false — the wall blocks the view of its own far face.
         */
        @Test
        @DisplayName("opposite sides of the wall do not light it")
        void oppositeSidesDoNotLight() {
            placePlayer(Loc.row(4).col(7));

            assertFalse(canLight(Loc.row(4).col(3), WALL));
        }

        /**
         * Bearings sharing neither component are looking at faces that cannot be the same one, and
         * the answer is false without any grid being consulted. Every grid around the wall is left
         * open so that the false cannot be coming from an obstruction.
         */
        @Test
        @DisplayName("bearings sharing no component do not light the wall")
        void perpendicularBearingsDoNotLight() {
            placePlayer(Loc.row(6).col(3));

            assertFalse(canLight(Loc.row(2).col(7), WALL));
        }
    }

    /**
     * The last branch, where the bearings share exactly one component and a third grid decides.
     */
    @Nested
    @DisplayName("the grid beside the wall")
    class BlockedFace {

        /**
         * The scenario drawn in the C comment. The light-emitting monster to the north-west and the
         * player to the south-west share the westward component, so the face at stake is the
         * western one, seen through the grid immediately west of the wall. With that grid open, the
         * wall is lit.
         */
        @Test
        @DisplayName("a shared column lights the wall when the grid beside it is open")
        void sharedColumnOpenLights() {
            placePlayer(Loc.row(5).col(4));

            assertTrue(canLight(Loc.row(3).col(4), WALL));
        }

        /**
         * The same scenario with the grid immediately west of the wall walled off. This is the case
         * the branch exists for: both parties have a clear line to the wall, but the lit face is
         * hidden behind its neighbour.
         */
        @Test
        @DisplayName("a shared column does not light the wall when the grid beside it is blocked")
        void sharedColumnBlockedDoesNotLight() {
            placePlayer(Loc.row(5).col(4));
            wall(Loc.row(4).col(4));

            assertFalse(canLight(Loc.row(3).col(4), WALL));
        }

        /**
         * The mirror image on the other axis: a source to the north-east and a player to the
         * north-west share the northward component, so the grid consulted is the one immediately
         * north of the wall, and an open one lets the light through.
         */
        @Test
        @DisplayName("a shared row lights the wall when the grid beside it is open")
        void sharedRowOpenLights() {
            placePlayer(Loc.row(3).col(4));

            assertTrue(canLight(Loc.row(3).col(6), WALL));
        }

        /**
         * The same, blocked. Walling the grid north of the wall hides the northern face.
         */
        @Test
        @DisplayName("a shared row does not light the wall when the grid beside it is blocked")
        void sharedRowBlockedDoesNotLight() {
            placePlayer(Loc.row(3).col(4));
            wall(Loc.row(3).col(5));

            assertFalse(canLight(Loc.row(3).col(6), WALL));
        }

        /**
         * The two branches build their grid from opposite pairings of the two coordinates, which is
         * an easy swap to make and a hard one to see. Here the bearings share a column, so the grid
         * that matters is west of the wall; walling the grid north of it — the one the other
         * pairing would name — must change nothing.
         */
        @Test
        @DisplayName("the shared column branch does not consult the grid on the other axis")
        void sharedColumnIgnoresTheOtherAxis() {
            placePlayer(Loc.row(5).col(4));
            wall(Loc.row(3).col(5));

            assertTrue(canLight(Loc.row(3).col(4), WALL));
        }

        /**
         * And the reverse: the bearings share a row, so the grid that matters is north of the wall,
         * and walling the grid west of it must change nothing.
         */
        @Test
        @DisplayName("the shared row branch does not consult the grid on the other axis")
        void sharedRowIgnoresTheOtherAxis() {
            placePlayer(Loc.row(3).col(4));
            wall(Loc.row(4).col(4));

            assertTrue(canLight(Loc.row(3).col(6), WALL));
        }

        /**
         * The grid consulted is built from one coordinate of the wall and one of the bearing, so a
         * transposed construction would still land on a real grid of this level and would still
         * usually be open. The wall is moved off the diagonal to somewhere its blocking neighbour
         * and that neighbour's transpose are different grids, and the transpose is walled: a
         * transposed build would report the face blocked, and the correct one reports it lit.
         */
        @Test
        @DisplayName("the grid beside the wall is not transposed")
        void theGridBesideIsNotTransposed() {
            Loc offDiagonal = Loc.row(2).col(5);
            wall(offDiagonal);
            placePlayer(Loc.row(3).col(4));
            wall(Loc.row(4).col(2));

            assertTrue(canLight(Loc.row(1).col(4), offDiagonal));
        }
    }

    /**
     * The divergence from C, which asserts that the grid it consults is inside the level.
     */
    @Nested
    @DisplayName("off the edge of the level")
    class OutOfBounds {

        /**
         * With the wall on the level's western edge and both parties further west still, the grid
         * to consult falls outside the level. C asserts here; the Java answers false, leaving the
         * wall unlit. Reaching this needs both parties off the level, so it guards a defensive path
         * rather than a position play can produce — but it is the path a mistake in the caller
         * would arrive on, and it should not be an exception.
         */
        @Test
        @DisplayName("a grid off the level leaves the wall unlit rather than throwing")
        void offLevelGridDoesNotThrow() {
            Loc edge = Loc.row(4).col(0);
            wall(edge);
            placePlayer(Loc.row(5).col(-1));

            assertFalse(canLight(Loc.row(3).col(-1), edge));
        }
    }
}
