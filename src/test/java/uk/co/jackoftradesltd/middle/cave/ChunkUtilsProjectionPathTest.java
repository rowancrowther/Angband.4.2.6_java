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
import uk.co.jackoftradesltd.middle.combat.enums.ProjectEnum;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ChunkUtils#projectionPath}, the port of C's {@code project_path}
 * ({@code project.c}).
 *
 * <p>Every expected path was worked out by hand-executing the C, not by reading the Java. The
 * fixed-point walk visits a specific set of grids, and a Bresenham-style line would agree with it
 * on most open levels and disagree exactly where these cases look. Paths are compared as text,
 * {@code "(x,y) (x,y) ..."}, so a failure shows the whole path.
 *
 * <p>The three things most likely to diverge are covered on purpose: the slant tie
 * ({@code frac >= half}, where {@code frac} lands exactly on {@code half}), the sign handling when
 * the path runs towards lower coordinates, and the range limit, which C computes as
 * {@code n + (k >> 1)} for the vertical and horizontal branches and {@code n + (n >> 1)} for the
 * diagonal.
 *
 * <p>Test class ChunkUtilsProjectionPathTest coded on 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkUtilsProjectionPathTest {

    /**
     * The level's size in both directions. Larger than any path below, so only the range limit and
     * the stopping rules end a path, never the edge of the level.
     */
    private static final int SIZE = 45;

    /**
     * A range far beyond every case that is not about the range limit.
     */
    private static final int FAR = 100;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * A terrain feature carrying the given flags and nothing else.
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags)
            set.on(flag);
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class), 0);
    }

    /**
     * Shorthand for a grid, in column/row order.
     */
    private static Loc at(int x, int y) {
        return Loc.row(y).col(x);
    }

    /**
     * A level whose every grid is projectable.
     */
    @BeforeEach
    void newLevel() {
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                SIZE, SIZE, 0, 4, 3, 0, 0, 0, new Player());

        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++)
                level.getSquare(at(x, y)).setFeature(feature(TerrainFeatureFlags.TF_PROJECT));
    }

    /**
     * Makes a grid unprojectable.
     */
    private void wall(int x, int y) {
        level.getSquare(at(x, y)).setFeature(feature());
    }

    /**
     * Writes the private {@code decoy} field, standing in for the decoy-placing code that is not
     * ported yet.
     */
    private void placeDecoy(Loc grid) throws ReflectiveOperationException {
        Field field = Chunk.class.getDeclaredField("decoy");
        field.setAccessible(true);
        field.set(level, grid);
    }

    /**
     * Traces a path and renders it as text.
     */
    private String path(int x1, int y1, int x2, int y2, int range, ProjectEnum... flags) {
        Flag<ProjectEnum> set = new Flag<>(ProjectEnum.class);
        for (ProjectEnum flag : flags)
            set.on(flag);
        List<Loc> grids = ChunkUtils.projectionPath(level, range, at(x1, y1), at(x2, y2), set);
        StringBuilder text = new StringBuilder();
        for (Loc grid : grids) {
            if (text.length() > 0) text.append(' ');
            text.append('(').append(grid.getX()).append(',').append(grid.getY()).append(')');
        }
        return text.toString();
    }

    /**
     * The cases settled before any grid is looked at.
     */
    @Nested
    @DisplayName("degenerate cases")
    class Degenerate {

        /**
         * C returns 0 if and only if the grids are equal, so the list is empty.
         */
        @Test
        @DisplayName("equal grids give an empty path")
        void equalGrids() {
            assertEquals("", path(5, 5, 5, 5, FAR));
        }

        /**
         * The start is never saved, so an adjacent target gives exactly one grid.
         */
        @Test
        @DisplayName("adjacent grid gives just that grid")
        void adjacent() {
            assertEquals("(1,0)", path(0, 0, 1, 0, FAR));
        }
    }

    /**
     * Straight and diagonal lines, where the slope is zero or exact.
     */
    @Nested
    @DisplayName("exact lines")
    class ExactLines {

        /**
         * Horizontal branch with {@code m = 0}: only x advances.
         */
        @Test
        @DisplayName("due east")
        void east() {
            assertEquals("(1,0) (2,0) (3,0)", path(0, 0, 3, 0, FAR));
        }

        /**
         * Vertical branch running towards lower y, so {@code sy = -1}.
         */
        @Test
        @DisplayName("due north")
        void north() {
            assertEquals("(5,4) (5,3) (5,2)", path(5, 5, 5, 2, FAR));
        }

        /**
         * Diagonal branch, both signs positive.
         */
        @Test
        @DisplayName("diagonal south-east")
        void southEast() {
            assertEquals("(3,3) (4,4) (5,5)", path(2, 2, 5, 5, FAR));
        }

        /**
         * Diagonal branch, both signs negative.
         */
        @Test
        @DisplayName("diagonal north-west")
        void northWest() {
            assertEquals("(4,4) (3,3)", path(5, 5, 3, 3, FAR));
        }
    }

    /**
     * Slanted lines, where {@code frac >= half} decides when the shorter axis steps.
     */
    @Nested
    @DisplayName("slanted lines")
    class Slants {

        /**
         * From (0,0) to (1,5): {@code half = 5}, {@code frac = 1}, {@code m = 2}. The fraction runs
         * 3, then exactly 5 at the third grid. {@code 5 >= 5} steps x, which is the tie the
         * boundary turns on. A port with {@code half = ax + ay} (6) steps one grid later.
         */
        @Test
        @DisplayName("vertical slant steps x on the tie")
        void verticalTie() {
            assertEquals("(0,1) (0,2) (1,3) (1,4) (1,5)", path(0, 0, 1, 5, FAR));
        }

        /**
         * The mirror of the vertical case: from (0,0) to (5,1).
         */
        @Test
        @DisplayName("horizontal slant steps y on the tie")
        void horizontalTie() {
            assertEquals("(1,0) (2,0) (3,1) (4,1) (5,1)", path(0, 0, 5, 1, FAR));
        }

        /**
         * Both signs negative: from (5,5) to (0,4).
         */
        @Test
        @DisplayName("horizontal slant towards lower coordinates")
        void horizontalNegative() {
            assertEquals("(4,5) (3,5) (2,4) (1,4) (0,4)", path(5, 5, 0, 4, FAR));
        }
    }

    /**
     * The range limit: {@code n + (k >> 1) >= range} for vertical and horizontal paths, and
     * {@code n + (n >> 1) >= range} for the diagonal.
     */
    @Nested
    @DisplayName("range limit")
    class RangeLimit {

        /**
         * A straight line has {@code k = 0}, so it runs for exactly {@code range} grids.
         */
        @Test
        @DisplayName("straight line runs for range grids")
        void straight() {
            assertEquals("(1,20) (2,20) (3,20) (4,20) (5,20) (6,20) (7,20) (8,20) (9,20) (10,20)",
                    path(0, 20, 40, 20, 10));
        }

        /**
         * A diagonal stops when {@code n + (n >> 1) >= 10}: n = 6 gives 9, n = 7 gives 10.
         */
        @Test
        @DisplayName("diagonal stops at n + n/2")
        void diagonal() {
            assertEquals("(1,1) (2,2) (3,3) (4,4) (5,5) (6,6) (7,7)", path(0, 0, 30, 30, 10));
        }

        /**
         * A shallow vertical slant, from (0,0) to (9,10): {@code half = 90}, {@code frac} starts at
         * 81 and {@code m = 162}. The fraction overflows at every grid, so {@code k} rises by one
         * each time and the path stops at n = 7 where {@code 7 + (6 >> 1) = 10}. A port that left
         * {@code k} out would run to n = 10.
         */
        @Test
        @DisplayName("slanted path stops early as the shorter axis steps")
        void slantedStopsEarly() {
            assertEquals("(0,1) (1,2) (2,3) (3,4) (4,5) (5,6) (6,7)", path(0, 0, 9, 10, 10));
        }
    }

    /**
     * The stopping rules that follow the range check.
     */
    @Nested
    @DisplayName("stopping rules")
    class Stopping {

        /**
         * Without {@code PROJECT_THRU} the path ends at the finish grid.
         */
        @Test
        @DisplayName("stops at the finish grid")
        void stopsAtFinish() {
            assertEquals("(1,0) (2,0)", path(0, 0, 2, 0, FAR));
        }

        /**
         * With {@code PROJECT_THRU} the path carries on past the finish grid, to the range limit.
         */
        @Test
        @DisplayName("PROJECT_THRU runs on to the range limit")
        void thru() {
            assertEquals("(1,0) (2,0) (3,0) (4,0) (5,0) (6,0)",
                    path(0, 0, 2, 0, 6, ProjectEnum.PROJECT_THRU));
        }

        /**
         * A wall grid is saved, then the path stops.
         */
        @Test
        @DisplayName("a wall ends the path after being saved")
        void wallEndsPath() {
            wall(3, 0);
            assertEquals("(1,0) (2,0) (3,0)", path(0, 0, 5, 0, FAR));
        }

        /**
         * The first grid is saved before any test, so a wall right beside the start still appears.
         */
        @Test
        @DisplayName("a wall beside the start is still saved")
        void wallAtFirstGrid() {
            wall(1, 0);
            assertEquals("(1,0)", path(0, 0, 5, 0, FAR));
        }

        /**
         * {@code PROJECT_ROCK} skips the wall test, so the path goes through.
         */
        @Test
        @DisplayName("PROJECT_ROCK passes through walls")
        void rock() {
            wall(3, 0);
            assertEquals("(1,0) (2,0) (3,0) (4,0) (5,0)",
                    path(0, 0, 5, 0, FAR, ProjectEnum.PROJECT_ROCK));
        }

        /**
         * With {@code PROJECT_STOP} a monster ends the path, itself included.
         */
        @Test
        @DisplayName("PROJECT_STOP ends at a monster")
        void stopAtMonster() {
            level.getSquare(at(3, 0)).setMon(1);
            assertEquals("(1,0) (2,0) (3,0)", path(0, 0, 5, 0, FAR, ProjectEnum.PROJECT_STOP));
        }

        /**
         * Without {@code PROJECT_STOP} the same monster does not stop the path.
         */
        @Test
        @DisplayName("a monster is ignored without PROJECT_STOP")
        void monsterIgnored() {
            level.getSquare(at(3, 0)).setMon(1);
            assertEquals("(1,0) (2,0) (3,0) (4,0) (5,0)", path(0, 0, 5, 0, FAR));
        }

        /**
         * With {@code PROJECT_STOP} the decoy ends the path, itself included.
         */
        @Test
        @DisplayName("PROJECT_STOP ends at the decoy")
        void stopAtDecoy() throws ReflectiveOperationException {
            placeDecoy(at(2, 0));
            assertEquals("(1,0) (2,0)", path(0, 0, 5, 0, FAR, ProjectEnum.PROJECT_STOP));
        }

        /**
         * A decoy is only tested under {@code PROJECT_STOP}.
         */
        @Test
        @DisplayName("the decoy is ignored without PROJECT_STOP")
        void decoyIgnored() throws ReflectiveOperationException {
            placeDecoy(at(2, 0));
            assertTrue(path(0, 0, 5, 0, FAR).endsWith("(5,0)"));
        }
    }
}
