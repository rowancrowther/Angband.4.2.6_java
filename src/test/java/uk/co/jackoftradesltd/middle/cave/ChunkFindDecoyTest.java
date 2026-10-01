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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Chunk#caveFindDecoy()}, the port of C's {@code cave_find_decoy()} ({@code cave.c}),
 * which is {@code return c->decoy;}.
 *
 * <p>C's {@code cave_chunk_new()}/{@code chunk_new()} zero-fills the chunk, so a fresh chunk has
 * {@code decoy = loc(0, 0)}, the "no decoy" value. Nothing in {@code middle/} sets the field after
 * construction yet, so the stored-location case writes it by reflection.
 *
 * <p>Test class ChunkFindDecoyTest coded on 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkFindDecoyTest {

    /**
     * The chunk under test.
     */
    private Chunk chunk;

    /**
     * Builds a small chunk with no decoy placed.
     */
    @BeforeEach
    void newChunk() {
        chunk = new Chunk("test level", 0, 0, 0, 0, 0, false,
                5, 9, 0, 4, 6, 0, -1, 0, new Player());
    }

    /**
     * Writes the private {@code decoy} field, standing in for the decoy-placing code that is not
     * ported yet.
     */
    private void placeDecoy(Loc grid) throws ReflectiveOperationException {
        Field field = Chunk.class.getDeclaredField("decoy");
        field.setAccessible(true);
        field.set(chunk, grid);
    }

    /**
     * A fresh chunk answers C's {@code loc(0, 0)}, which callers read as "no decoy".
     */
    @Test
    @DisplayName("a fresh chunk has no decoy: loc(0, 0)")
    void freshChunkHasNoDecoy() {
        Loc found = chunk.caveFindDecoy();
        assertTrue(found.isZero());
        assertEquals(0, found.getX());
        assertEquals(0, found.getY());
    }

    /**
     * A stored location comes back unchanged, with x and y not swapped.
     */
    @Test
    @DisplayName("a placed decoy comes back with x and y intact")
    void placedDecoyIsReturned() throws ReflectiveOperationException {
        Loc grid = Loc.row(3).col(7);
        placeDecoy(grid);

        Loc found = chunk.caveFindDecoy();
        assertEquals(7, found.getX());
        assertEquals(3, found.getY());
        assertEquals(grid, found);
    }

    /**
     * C returns the struct by value. {@link Loc} is immutable, so the stored reference is an
     * equivalent answer; this pins that down so a mutable {@code Loc} would fail here.
     */
    @Test
    @DisplayName("repeat calls agree and do not disturb the stored grid")
    void repeatCallsAgree() throws ReflectiveOperationException {
        placeDecoy(Loc.row(2).col(4));

        Loc first = chunk.caveFindDecoy();
        Loc second = chunk.caveFindDecoy();
        assertSame(first, second);
        assertEquals(Loc.row(2).col(4), second);
    }

    /**
     * A decoy on a grid with only one zero coordinate is still a decoy: C's test is a comparison
     * with {@code loc(0, 0)}, not an either-coordinate check.
     */
    @Test
    @DisplayName("a decoy on x=0 or y=0 alone is not read as 'none'")
    void oneZeroCoordinateIsStillADecoy() throws ReflectiveOperationException {
        placeDecoy(Loc.row(0).col(5));
        assertTrue(!chunk.caveFindDecoy().isZero());

        placeDecoy(Loc.row(5).col(0));
        assertTrue(!chunk.caveFindDecoy().isZero());
    }
}
