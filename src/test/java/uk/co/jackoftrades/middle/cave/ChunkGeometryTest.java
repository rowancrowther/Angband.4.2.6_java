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
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Chunk}'s geometry, square lookup and the two heat maps — the port of the bounds and
 * accessor macros around C's {@code struct chunk} ({@code cave.h}).
 *
 * <p>The two bounds tests are the pair worth having. {@code inBounds} asks whether a grid is on the
 * level at all; {@code inBoundsFully} asks whether it is strictly inside, with a grid to every side.
 * The second is what the level builders use, because a room dug against the edge would have no wall
 * beyond it — so the difference between them is exactly the outermost ring of grids, and a test that
 * only checked the middle would pass either way round.
 *
 * <p>{@code getSquare} answers {@code null} rather than throwing for a grid off the level, and the
 * square predicates are each guarded by {@code inBounds} for the same reason: the monster and
 * lighting code asks about neighbours it may not have, and would rather be told "no" than stopped.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkGeometryTest {

    /**
     * The chunk's width, in grids.
     */
    private static final int WIDTH = 8;

    /**
     * The chunk's height, in grids.
     */
    private static final int HEIGHT = 6;

    /**
     * The chunk under test.
     */
    private Chunk chunk;

    /**
     * A small chunk with a player attached, since several accessors reach the player through it.
     */
    @BeforeEach
    void newChunk() {
        chunk = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, new Player());
    }

    /**
     * The two bounds tests, which differ by the outermost ring of grids.
     */
    @Nested
    @DisplayName("bounds")
    class Bounds {

        /**
         * A grid in the middle is inside on both tests.
         */
        @Test
        @DisplayName("a middle grid is inside on both tests")
        void middleIsInsideBoth() {
            Loc middle = Loc.row(3).col(4);

            assertTrue(chunk.inBounds(middle));
            assertTrue(chunk.inBoundsFully(middle));
        }

        /**
         * The edge ring is the difference: on the level, but not fully inside it. This is the case
         * the two tests exist to tell apart.
         */
        @Test
        @DisplayName("the edge ring is on the level but not fully inside it")
        void edgeIsInsideButNotFully() {
            Loc topLeft = Loc.row(0).col(0);
            Loc bottomRight = Loc.row(HEIGHT - 1).col(WIDTH - 1);

            assertTrue(chunk.inBounds(topLeft));
            assertFalse(chunk.inBoundsFully(topLeft));

            assertTrue(chunk.inBounds(bottomRight));
            assertFalse(chunk.inBoundsFully(bottomRight));
        }

        /**
         * Beyond the edge is outside on both, in either direction and on either axis.
         */
        @Test
        @DisplayName("beyond the edge is outside on both tests")
        void beyondEdgeIsOutside() {
            assertFalse(chunk.inBounds(Loc.row(-1).col(4)));
            assertFalse(chunk.inBounds(Loc.row(3).col(-1)));
            assertFalse(chunk.inBounds(Loc.row(HEIGHT).col(4)));
            assertFalse(chunk.inBounds(Loc.row(3).col(WIDTH)));

            assertFalse(chunk.inBoundsFully(Loc.row(-1).col(4)));
            assertFalse(chunk.inBoundsFully(Loc.row(HEIGHT).col(4)));
        }
    }

    /**
     * The square lookup and the predicates that go through it.
     */
    @Nested
    @DisplayName("square lookup")
    class Squares {

        /**
         * Every grid on the level has a square, allocated by the constructor rather than on demand.
         */
        @Test
        @DisplayName("every grid on the level has a square")
        void everyGridHasASquare() {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    assertNotSame(null, chunk.getSquare(Loc.row(y).col(x)),
                            "grid " + y + "," + x);
                }
            }
        }

        /**
         * A grid off the level answers nothing rather than throwing — the neighbour-walking code
         * asks about grids it may not have.
         */
        @Test
        @DisplayName("a grid off the level answers nothing")
        void offLevelAnswersNull() {
            assertNull(chunk.getSquare(Loc.row(-1).col(0)));
            assertNull(chunk.getSquare(Loc.row(HEIGHT).col(0)));
            assertNull(chunk.getSquare(Loc.row(0).col(WIDTH)));
        }

        /**
         * Successive lookups answer the same square, so a caller can write to one and read it back
         * through another lookup.
         */
        @Test
        @DisplayName("the same grid always answers the same square")
        void lookupIsStable() {
            Loc grid = Loc.row(2).col(3);

            assertSame(chunk.getSquare(grid), chunk.getSquare(grid));
        }

        /**
         * The square predicates are guarded, so a grid off the level answers false rather than
         * throwing on the null square the lookup would give them.
         */
        @Test
        @DisplayName("the square predicates answer false off the level")
        void predicatesAreGuarded() {
            Loc offLevel = Loc.row(-1).col(-1);

            assertFalse(chunk.squareIsSeen(offLevel));
            assertFalse(chunk.squareIsNoFlow(offLevel));
            assertFalse(chunk.squareIsNoScent(offLevel));
        }

        /**
         * On the level they read the square. A fresh level has been seen by nobody.
         */
        @Test
        @DisplayName("on the level they read the square")
        void predicatesReadTheSquare() {
            assertFalse(chunk.squareIsSeen(Loc.row(2).col(3)));
        }
    }

    /**
     * The dimensions and counts the level builders read.
     */
    @Nested
    @DisplayName("dimensions and counts")
    class Dimensions {

        /**
         * Height and width come back as given, and are not crossed — the constructor takes them in
         * that order and the grid array is indexed the other way round, which is where confusion
         * would live.
         */
        @Test
        @DisplayName("height and width are not crossed")
        void dimensionsAreNotCrossed() {
            assertEquals(HEIGHT, chunk.getHeight());
            assertEquals(WIDTH, chunk.getWidth());
        }

        /**
         * The monster array is sized from the constructor's figure, and the count reports its
         * length.
         */
        @Test
        @DisplayName("the monster array is sized as asked")
        void monsterArraySized() {
            assertEquals(3, chunk.getMonMax());
            assertEquals(3, chunk.getMonsters().length);
            assertEquals(3, chunk.monsterCount());
        }

        /**
         * The object list starts empty and is handed out read-only, so nothing outside the chunk can
         * add to it.
         */
        @Test
        @DisplayName("the object list starts empty and is read-only")
        void objectListIsReadOnly() {
            assertTrue(chunk.getObjects().isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> chunk.getObjects().add(null));
        }
    }

    /**
     * The two heat maps, which monsters hear and smell the player through.
     */
    @Nested
    @DisplayName("noise and scent")
    class HeatMaps {

        /**
         * Both exist from the start and are separate maps — a monster hearing the player is not the
         * same as one smelling them.
         */
        @Test
        @DisplayName("noise and scent are separate maps")
        void mapsAreSeparate() {
            assertNotSame(chunk.getNoise(), chunk.getScent());

            chunk.getNoise().setValue(2, 3, 5);

            assertEquals(5, chunk.getNoise().getValue(2, 3));
            assertEquals(0, chunk.getScent().getValue(2, 3), "the scent map was not touched");
        }

        /**
         * Resetting the noise replaces the map rather than clearing it in place, so a caller holding
         * the old one keeps the old readings — worth knowing before caching it.
         */
        @Test
        @DisplayName("resetting the noise replaces the map")
        void resetReplacesTheMap() {
            Heatmap before = chunk.getNoise();
            before.setValue(2, 3, 5);

            chunk.resetNoise();

            assertNotSame(before, chunk.getNoise());
            assertEquals(0, chunk.getNoise().getValue(2, 3));
            assertEquals(5, before.getValue(2, 3), "the replaced map still holds its readings");
        }

        /**
         * Ageing the scent adds a turn to every trail that exists, and leaves grids with no trail
         * alone — an empty grid does not acquire a one-turn-old scent.
         */
        @Test
        @DisplayName("ageing the scent staleness only affects existing trails")
        void ageingOnlyAffectsTrails() {
            chunk.getScent().setValue(2, 3, 1);

            chunk.updateScent();

            assertEquals(2, chunk.getScent().getValue(2, 3));
            assertEquals(0, chunk.getScent().getValue(2, 4), "a grid with no trail stays empty");
        }

        /**
         * The outermost ring is skipped, as C's loop does — the boundary is not part of the playable
         * level and no scent is tracked across it.
         */
        @Test
        @DisplayName("the boundary ring is left out of the ageing")
        void boundaryIsSkipped() {
            chunk.getScent().setValue(0, 0, 1);
            chunk.getScent().setValue(HEIGHT - 1, WIDTH - 1, 1);

            chunk.updateScent();

            assertEquals(1, chunk.getScent().getValue(0, 0), "the boundary was not aged");
            assertEquals(1, chunk.getScent().getValue(HEIGHT - 1, WIDTH - 1));
        }
    }
}
