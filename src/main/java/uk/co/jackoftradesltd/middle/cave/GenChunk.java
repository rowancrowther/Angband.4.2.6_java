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

/**
 * Java port of the chunk-list bookkeeping counter from C's {@code gen-chunk.c}.
 *
 * <p>C's chunk list ({@code chunk_list}) holds pointers to saved chunks of the world - used, for
 * example, to save and restore the town between visits - and {@link #chunkListMax} is its
 * companion count of how many entries are actually in use, as distinct from the list's allocated
 * capacity.
 *
 * <p>Outstanding: only the counter is ported so far; the list itself and the functions that
 * operate on both ({@code chunk_list_add}, {@code chunk_list_remove}, {@code chunk_find_name},
 * {@code chunk_find}, {@code gen-chunk.c:69-138}) are not yet ported.
 *
 * @author Rowan Crowther
 */
public class GenChunk {
    /**
     * Current number of chunks held in the chunk list, as distinct from the list's allocated
     * capacity. Ported from C's {@code chunk_list_max} ({@code gen-chunk.c:38}), which is declared
     * {@code uint16_t}; this class widens it to {@code int}, the same {@code uint16_t}-to-{@code
     * int} convention already used for {@link Chunk#getMonMax}'s field in this package.
     */
    private static int chunkListMax;

    /**
     * Returns the current number of chunks held in the chunk list.
     *
     * <p>Function getChunkListMax coded on 260908, commented in full on 260908.
     *
     * @return the current chunk-list count
     */
    public static int getChunkListMax() {
        return chunkListMax;
    }

    /**
     * Sets the current number of chunks held in the chunk list.
     *
     * <p>Function setChunkListMax coded on 260908, commented in full on 260908.
     *
     * @param chunkListMax the new chunk-list count
     */
    public static void setChunkListMax(int chunkListMax) {
        GenChunk.chunkListMax = chunkListMax;
    }
}
