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
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code GenChunk}, the port of the {@code chunk_list_max} counter from C's
 * {@code gen-chunk.c} ({@code gen-chunk.c:38}).
 *
 * <p>{@code chunkListMax} is a static field, so each test resets it directly through reflection
 * before running, rather than relying on JVM-wide state left over from another test.
 *
 * <p>Class GenChunkTest coded on 260908, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
class GenChunkTest {

    @BeforeEach
    void resetChunkListMax() throws Exception {
        Field field = GenChunk.class.getDeclaredField("chunkListMax");
        field.setAccessible(true);
        field.setInt(null, 0);
    }

    /**
     * C initializes {@code chunk_list_max} to {@code 0} at file scope ({@code gen-chunk.c:38});
     * Java's default {@code int} field value matches without needing an explicit initializer.
     */
    @Test
    void initialValueMatchesCInitializer() {
        assertEquals(0, GenChunk.getChunkListMax());
    }

    @Test
    void setThenGetRoundTripsTheValue() {
        GenChunk.setChunkListMax(5);

        assertEquals(5, GenChunk.getChunkListMax());
    }

    @Test
    void aLaterSetOverwritesAnEarlierOne() {
        GenChunk.setChunkListMax(10);
        GenChunk.setChunkListMax(20);

        assertEquals(20, GenChunk.getChunkListMax());
    }

    /**
     * C declares {@code chunk_list_max} as {@code uint16_t}, whose maximum representable value is
     * {@code 65535}; the port widens it to {@code int} ({@code GenChunk.java}), so this pins that
     * the C-side upper bound still round-trips faithfully rather than wrapping.
     */
    @Test
    void roundTripsTheTopOfCsUint16Range() {
        GenChunk.setChunkListMax(65535);

        assertEquals(65535, GenChunk.getChunkListMax());
    }
}
