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
import org.junit.jupiter.api.function.Executable;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests the small {@link Chunk} accessors ported from fields of C's {@code struct chunk}
 * ({@code cave.h}): {@code width}, {@code height}, {@code mon_cnt}, {@code mon_current} and
 * {@code feeling}.
 *
 * <p>The one with a real behavioural boundary is {@code monsterCount}. C's
 * {@code cave_monster_count()} ({@code cave.c}) returns the live count, which rises when a monster
 * is placed and falls when its slot is wiped in {@code delete_monster_idx()} ({@code mon-make.c}).
 * It is not the capacity, which is {@code cave_monster_max()}. An earlier port returned the array
 * length, so {@code process_world}'s two compaction tests compared a constant to the limit.
 *
 * <p>Test class ChunkMemberAccessTest coded on 260929, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkMemberAccessTest {

    /**
     * The chunk's width, in grids. Deliberately different from {@link #HEIGHT}, so a swapped pair of
     * getters fails.
     */
    private static final int WIDTH = 9;

    /**
     * The chunk's height, in grids.
     */
    private static final int HEIGHT = 5;

    /**
     * The monster array capacity, larger than any count the tests reach.
     */
    private static final int MON_MAX = 6;

    /**
     * The chunk under test, built with {@code monCurrent} at C's "nobody acting" value.
     */
    private Chunk chunk;

    /**
     * A blank monster to occupy a slot; only its presence matters to the count.
     */
    private static Monster monster() {
        return new Monster(null, null, null, 0, 0, null, 0, 0, 0, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    /**
     * Builds a chunk with a monster array of {@link #MON_MAX} empty slots.
     */
    @BeforeEach
    void newChunk() {
        chunk = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, MON_MAX, 0, -1, 0, new Player());
    }

    /**
     * Width and height are C's {@code c->width} and {@code c->height}.
     */
    @Nested
    @DisplayName("width and height")
    class Dimensions {

        /**
         * Each getter returns its own constructor argument, not the other's.
         */
        @Test
        @DisplayName("each getter returns its own dimension")
        void dimensions() {
            assertEquals(WIDTH, chunk.getWidth());
            assertEquals(HEIGHT, chunk.getHeight());
        }
    }

    /**
     * {@code monsterCount} is C's {@code cave_monster_count()}, the live count.
     */
    @Nested
    @DisplayName("monsterCount")
    class MonsterCount {

        /**
         * A fresh chunk has no monsters, whatever its capacity.
         */
        @Test
        @DisplayName("an empty chunk counts none, not its capacity")
        void emptyChunk() {
            assertEquals(0, chunk.monsterCount());
            assertEquals(MON_MAX, chunk.getMonMax());
        }

        /**
         * Each occupied slot adds one, and the capacity is unchanged.
         */
        @Test
        @DisplayName("each occupied slot adds one")
        void occupiedSlotsCount() {
            chunk.getMonsters()[1] = monster();
            assertEquals(1, chunk.monsterCount());

            chunk.getMonsters()[3] = monster();
            assertEquals(2, chunk.monsterCount(), "gaps between occupied slots are not counted");
            assertEquals(MON_MAX, chunk.getMonMax());
        }

        /**
         * Wiping a slot, as {@code delete_monster_idx()} does, takes the count back down.
         */
        @Test
        @DisplayName("clearing a slot lowers the count")
        void clearedSlotFalls() {
            chunk.getMonsters()[1] = monster();
            chunk.getMonsters()[2] = monster();

            chunk.getMonsters()[1] = null;

            assertEquals(1, chunk.monsterCount());
        }
    }

    /**
     * {@code mon_current} is {@code -1} when no monster is acting.
     */
    @Nested
    @DisplayName("getMonCurrent")
    class MonCurrent {

        /**
         * The constructor's value comes back untouched, {@code -1} included, since callers test
         * {@code > 0}.
         */
        @Test
        @DisplayName("returns the nobody-acting value -1 as constructed")
        void nobodyActing() {
            assertEquals(-1, chunk.getMonCurrent());
        }

        /**
         * A positive index is returned as given.
         */
        @Test
        @DisplayName("returns an acting monster's index")
        void someoneActing() {
            Chunk acting = new Chunk("test level", 0, 0, 0, 0, 0, false,
                    HEIGHT, WIDTH, 0, 4, MON_MAX, 0, 2, 0, new Player());
            assertEquals(2, acting.getMonCurrent());
        }
    }

    /**
     * {@code setFeeling} records the packed value.
     */
    @Nested
    @DisplayName("setFeeling")
    class Feeling {

        /**
         * There is no getter for the feeling yet, so the value cannot be read back; this only pins
         * that recording it needs no game state and does not throw, since C's assignment is inert.
         */
        @Test
        @DisplayName("recording a packed feeling does not throw")
        void doesNotThrow() {
            assertDoesNotThrow(() -> chunk.setFeeling(43));
        }
    }

    /**
     * What a freshly built chunk holds, and what {@code caveMonster} answers at the edges of its
     * array.
     */
    @Nested
    @DisplayName("construction and monster lookup")
    class ConstructionAndLookup {

        /**
         * C's {@code cave_new()} ({@code cave.c}) zero-fills every square, so a fresh grid is dark
         * and unoccupied. An occupant of zero is what makes the grid free and a light of zero is what
         * makes it unlit; anything else would read as a monster standing on every grid.
         */
        @Test
        @DisplayName("a new chunk's squares are zeroed, as cave_new() leaves them")
        void newChunkSquaresAreZeroed() {
            List<Executable> checks = new ArrayList<>();
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    Square s = chunk.getSquare(Loc.row(y).col(x));
                    String at = "(" + x + "," + y + ")";
                    checks.add(() -> assertEquals(0, s.getLight(), at + " light"));
                    checks.add(() -> assertEquals(0, s.getMonsterIndex(), at + " occupant"));
                }
            }
            assertAll(checks);
        }

        /**
         * C's {@code cave_monster()} returns {@code NULL} for an index of zero or below. An index
         * past the array has no C answer, because C reads out of bounds, so {@code null} is the safe
         * reading. An in-range empty slot is {@code null} because the port has no zeroed struct.
         */
        @Test
        @DisplayName("caveMonster answers null for index 0, negative and past the end")
        void caveMonsterBoundaries() {
            assertAll(
                    () -> assertNull(chunk.caveMonster(0), "the reserved dummy slot"),
                    () -> assertNull(chunk.caveMonster(-1), "C returns NULL for idx <= 0"),
                    () -> assertNull(chunk.caveMonster(chunk.getMonsters().length), "one past the end"),
                    () -> assertNull(chunk.caveMonster(1), "an empty slot"));
        }
    }
}
