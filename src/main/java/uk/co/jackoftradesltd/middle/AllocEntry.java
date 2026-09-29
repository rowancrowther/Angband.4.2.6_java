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

package uk.co.jackoftradesltd.middle;

/**
 * One entry in the object/monster allocation table: a plain data holder with no
 * behaviour of its own. Each entry records the thing's index, its base dungeon
 * level, and three probability weights, one per allocation pass. The weights are
 * filled in at different times: pass 1 ({@code prob1}) comes from the allocation
 * information in the game data, pass 2 ({@code prob2}) from the allocation
 * restriction (the caller's filter), and pass 3 ({@code prob3}) from the
 * allocation calculation at the level being generated.
 *
 * <p>This is the Java port of {@code struct alloc_entry} in the C original's
 * {@code alloc.h}. All fields are plain {@code int}, as in C, and default to
 * zero, matching the zero-filled tables C allocates.
 *
 * <p>Class AllocEntry commented in full on 260929.
 *
 * @author Rowan Crowther
 */
public class AllocEntry {
    /**
     * The actual index of the thing this entry allocates, into its own kind
     * table (C field {@code index}).
     *
     * <p>Field Index commented in full on 260929.
     */
    private int Index;
    /**
     * Base dungeon level of the thing: the depth at which it naturally appears
     * (C field {@code level}).
     *
     * <p>Field level commented in full on 260929.
     */
    private int level;
    /**
     * Allocation probability for pass 1, determined from the allocation
     * information in the game data (C field {@code prob1}).
     *
     * <p>Field prob1 commented in full on 260929.
     */
    private int prob1;
    /**
     * Allocation probability for pass 2, determined from the allocation
     * restriction, i.e. the caller's filter (C field {@code prob2}).
     *
     * <p>Field prob2 commented in full on 260929.
     */
    private int prob2;
    /**
     * Allocation probability for pass 3, determined from the allocation
     * calculation at the level being generated (C field {@code prob3}).
     *
     * <p>Field prob3 commented in full on 260929.
     */
    private int prob3;

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getIndex commented in full on 260929.
     *
     * @return the index of the allocated thing
     */
    public int getIndex() {
        return Index;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setIndex commented in full on 260929.
     *
     * @param index the index of the allocated thing
     */
    public void setIndex(int index) {
        Index = index;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getLevel commented in full on 260929.
     *
     * @return the base dungeon level of the allocated thing
     */
    public int getLevel() {
        return level;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setLevel commented in full on 260929.
     *
     * @param level the base dungeon level of the allocated thing
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getProb1 commented in full on 260929.
     *
     * @return the pass 1 probability (from allocation information)
     */
    public int getProb1() {
        return prob1;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setProb1 commented in full on 260929.
     *
     * @param prob1 the pass 1 probability (from allocation information)
     */
    public void setProb1(int prob1) {
        this.prob1 = prob1;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getProb2 commented in full on 260929.
     *
     * @return the pass 2 probability (from allocation restriction)
     */
    public int getProb2() {
        return prob2;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setProb2 commented in full on 260929.
     *
     * @param prob2 the pass 2 probability (from allocation restriction)
     */
    public void setProb2(int prob2) {
        this.prob2 = prob2;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getProb3 commented in full on 260929.
     *
     * @return the pass 3 probability (from allocation calculation)
     */
    public int getProb3() {
        return prob3;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setProb3 commented in full on 260929.
     *
     * @param prob3 the pass 3 probability (from allocation calculation)
     */
    public void setProb3(int prob3) {
        this.prob3 = prob3;
    }
}
