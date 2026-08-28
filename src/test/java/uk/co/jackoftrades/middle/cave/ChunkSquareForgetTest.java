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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@code Chunk.squareForget}, the port of C's {@code square_forget}
 * ({@code cave-square.c}).
 *
 * <p>C's whole function is two lines — bail out unless the chunk is the live level, then write
 * {@code FEAT_NONE} into the player's remembered copy — so the tests are built around the three
 * things those two lines can get wrong: writing to the wrong chunk, writing the wrong feature, and
 * writing at the wrong grid.
 *
 * <p><b>Which chunk is written.</b> The real level and the player's memory are the same Java type,
 * so a port that dropped the {@code player.getCave()} hop would compile and would look right at
 * every call site: the grid would appear to be forgotten, and the level's real terrain would have
 * been destroyed instead. Every test here therefore asserts on both chunks, never just the one it
 * expects to change.
 *
 * <p><b>The method is private</b> and its only caller, {@code Chunk.updateView}, reaches it through
 * a blindness condition that needs a calculated {@code PlayerState} behind the player. Driving the
 * tests through that caller would make them tests of the condition rather than of the two lines
 * under it, so the method is invoked reflectively — see {@link #forget}.
 *
 * <p><b>The terrain registry is seeded here rather than borrowed.</b> The method resolves
 * {@code FEAT_NONE} through {@link TerrainRegistry}, which is global static state; a class that
 * relied on some earlier reader having loaded {@code terrain.txt} would pass in a full suite run and
 * fail run on its own. The registry is saved, replaced with three features built in this file, and
 * put back afterwards.
 *
 * <p>The chunk is deliberately not square, and grids are placed off the diagonal, so a transposed
 * {@code squares[x][y]} would fail rather than quietly agree with itself.
 *
 * <p>Class ChunkSquareForgetTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkSquareForgetTest {

    /**
     * The chunk's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 9;

    /**
     * The chunk's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 6;

    /**
     * The grid the tests forget. Off the diagonal, and inside the level.
     */
    private static final Loc GRID = Loc.row(1).col(7);

    /**
     * A second grid, left alone by every test, standing in for the rest of the level.
     */
    private static final Loc OTHER = Loc.row(4).col(2);

    /**
     * The "unknown grid" feature, C's {@code FEAT_NONE} — what forgetting writes.
     */
    private static Feature none;

    /**
     * An impassable wall, standing in for terrain the player has remembered.
     */
    private static Feature granite;

    /**
     * A floor, used where a test needs the two chunks to differ from each other.
     */
    private static Feature floor;

    /**
     * The terrain registry's contents before this class replaced them, restored afterwards.
     */
    private static List<Feature> savedFeatures;

    /**
     * The live level — the chunk the method is called on.
     */
    private Chunk level;

    /**
     * The player's remembered copy of the level, which is what forgetting writes to.
     */
    private Chunk memory;

    /**
     * The player owning the remembered copy.
     */
    private Player player;

    /**
     * Replace the terrain registry with three features of this file's own making.
     *
     * <p>{@code FEAT_NONE} is the only one the method looks up; the other two exist so a grid can
     * hold something recognisable before and after.
     */
    @BeforeAll
    static void seedTerrain() {
        savedFeatures = currentFeatures();

        none = feature(TerrainFlags.FEAT_NONE, "unknown grid");
        granite = feature(TerrainFlags.FEAT_GRANITE, "granite wall");
        floor = feature(TerrainFlags.FEAT_FLOOR, "open floor", TerrainFeatureFlags.TF_PASSABLE);

        TerrainRegistry.setFeatures(List.of(none, granite, floor));
    }

    /**
     * Put back whatever the registry held before, so a later class finds the real data if it was
     * there and an unloaded registry if it was not.
     */
    @AfterAll
    static void restoreTerrain() {
        TerrainRegistry.setFeatures(savedFeatures);
    }

    /**
     * Reads the terrain registry without insisting it has been loaded.
     *
     * @return a copy of the registry's features, or {@code null} if nothing has been loaded
     */
    private static List<Feature> currentFeatures() {
        try {
            return new ArrayList<>(TerrainRegistry.getFeatures());
        } catch (NullPointerException notLoaded) {
            return null;
        }
    }

    /**
     * Builds a feature carrying only the code, name and flags a test needs. Everything the data
     * files supply beyond that — messages, glyph, digging difficulty — is left null or zero,
     * because nothing on this path reads it.
     *
     * @param code  the terrain code
     * @param name  the feature's name, so a failure message names it
     * @param setOn the terrain flags to switch on
     * @return the feature
     */
    private static Feature feature(TerrainFlags code, String name, TerrainFeatureFlags... setOn) {
        Flag<TerrainFeatureFlags> flags = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : setOn) flags.on(flag);

        return new Feature(code, name, "", null, 0, 0, flags, null,
                null, null, null, null, null, null, null, null);
    }

    /**
     * A live level and a matching remembered copy, both knowing which chunk is current, with the
     * whole of both laid with granite.
     *
     * <p>Laying the memory with real terrain rather than leaving it blank is what makes forgetting
     * observable: a grid that started as {@code FEAT_NONE} would pass every assertion here without
     * the method having run at all.
     */
    @BeforeEach
    void newLevel() {
        player = new Player();

        level = new Chunk("level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
        memory = new Chunk("memory", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);

        level.setCurrentLevel(level);
        memory.setCurrentLevel(level);
        player.setCave(memory);
        GameState.setCave(level);

        lay(level, granite);
        lay(memory, granite);
    }

    /**
     * Leave no live level behind for the next class, since it is global static state.
     */
    @AfterEach
    void clearCave() {
        GameState.setCave(null);
    }

    /**
     * Puts one feature on every grid of a chunk.
     *
     * @param chunk   the chunk to lay
     * @param feature the feature to lay it with
     */
    private void lay(Chunk chunk, Feature feature) {
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                chunk.getSquare(Loc.row(y).col(x)).setFeature(feature);
    }

    /**
     * Calls the private method under test.
     *
     * @param chunk the chunk to call it on
     * @param grid  the grid to forget
     */
    private void forget(Chunk chunk, Loc grid) {
        try {
            Method method = Chunk.class.getDeclaredMethod("squareForget", Loc.class);
            method.setAccessible(true);
            method.invoke(chunk, grid);
        } catch (InvocationTargetException e) {
            throw new AssertionError("squareForget threw " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("could not reach Chunk.squareForget", e);
        }
    }

    /**
     * The feature a chunk holds at a grid.
     *
     * @param chunk the chunk to read
     * @param grid  the grid to read
     * @return the feature there
     */
    private Feature featureAt(Chunk chunk, Loc grid) {
        return chunk.getSquare(grid).getFeature();
    }

    /**
     * What forgetting writes, and where it writes it.
     */
    @Nested
    @DisplayName("the write")
    class TheWrite {

        /**
         * The remembered terrain becomes {@code FEAT_NONE} — not merely something else, and not the
         * feature the real level holds. Asserting on identity with the registry's own entry is what
         * separates "forgotten" from "overwritten with whatever came to hand".
         */
        @Test
        @DisplayName("the remembered grid becomes FEAT_NONE")
        void memoryIsCleared() {
            forget(level, GRID);

            assertSame(none, featureAt(memory, GRID));
        }

        /**
         * The real level keeps its terrain. C forgets a grid precisely so that the player's memory
         * and the level can disagree, so a port that wrote to both would defeat the purpose of the
         * call.
         */
        @Test
        @DisplayName("the real level is untouched")
        void levelIsUntouched() {
            forget(level, GRID);

            assertSame(granite, featureAt(level, GRID));
        }

        /**
         * Only the named grid is forgotten. C writes one square; a loop, or a write that ignored
         * part of the location, would take the rest of the level's memory with it.
         */
        @Test
        @DisplayName("no other grid is forgotten")
        void neighboursAreUntouched() {
            forget(level, GRID);

            assertSame(granite, featureAt(memory, OTHER));

            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++) {
                    Loc grid = Loc.row(y).col(x);
                    if (grid.equals(GRID)) continue;
                    assertSame(granite, featureAt(memory, grid));
                }
        }

        /**
         * The write lands on the grid asked for, and not on its transpose. On a level this shape
         * {@code (row 1, col 4)} and {@code (row 4, col 1)} are both valid grids, so an
         * {@code x}/{@code y} swap would be silent everywhere except a test that names both.
         */
        @Test
        @DisplayName("the write is not transposed")
        void writeIsNotTransposed() {
            Loc grid = Loc.row(1).col(4);
            Loc transpose = Loc.row(4).col(1);

            forget(level, grid);

            assertSame(none, featureAt(memory, grid));
            assertSame(granite, featureAt(memory, transpose));
        }

        /**
         * Forgetting the same grid twice leaves it forgotten. C's write is unconditional — there is
         * no "already unknown" test in front of it — so the second call is the first call again.
         */
        @Test
        @DisplayName("forgetting twice is the same as forgetting once")
        void forgettingIsIdempotent() {
            forget(level, GRID);
            forget(level, GRID);

            assertSame(none, featureAt(memory, GRID));
            assertSame(granite, featureAt(level, GRID));
        }

        /**
         * What the grid remembered before makes no difference. C writes {@code FEAT_NONE} whatever
         * was there, so a floor is forgotten exactly as a wall is — the caller's interest in
         * impassable terrain lives in {@code updateView}, not here.
         */
        @Test
        @DisplayName("passable terrain is forgotten too")
        void anyTerrainIsForgotten() {
            memory.getSquare(GRID).setFeature(floor);

            forget(level, GRID);

            assertSame(none, featureAt(memory, GRID));
        }
    }

    /**
     * C's {@code if (c != cave) return;} — the guard that decides whether anything happens at all.
     */
    @Nested
    @DisplayName("the live-level guard")
    class LiveLevelGuard {

        /**
         * Called on the player's remembered copy, nothing happens. That chunk is not the live level,
         * and C's guard exists exactly so that a chunk which is itself a memory cannot go on to
         * write into one.
         */
        @Test
        @DisplayName("called on the remembered copy, nothing is forgotten")
        void memoryChunkDoesNothing() {
            forget(memory, GRID);

            assertSame(granite, featureAt(memory, GRID));
            assertSame(granite, featureAt(level, GRID));
        }

        /**
         * Called on a level that is not the current one — a chunk being generated, or one the player
         * has left — nothing happens, even though that chunk is a real level rather than a memory.
         * The guard tests identity with the live level, not the kind of chunk it was given.
         */
        @Test
        @DisplayName("called on a level that is not current, nothing is forgotten")
        void otherLevelDoesNothing() {
            Chunk other = new Chunk("elsewhere", 0, 0, 0, 0, 0, false,
                    HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
            other.setCurrentLevel(other);
            lay(other, granite);

            forget(other, GRID);

            assertSame(granite, featureAt(memory, GRID));
            assertSame(granite, featureAt(other, GRID));
        }

        /**
         * With no live level at all, nothing happens and nothing throws. C compares against a
         * {@code cave} global that is {@code NULL} between levels, and the comparison is as happy
         * with {@code NULL} as with anything else.
         */
        @Test
        @DisplayName("with no live level, nothing is forgotten")
        void noLiveLevelDoesNothing() {
            GameState.setCave(null);

            assertDoesNotThrow(() -> forget(level, GRID));

            assertSame(granite, featureAt(memory, GRID));
        }
    }
}
