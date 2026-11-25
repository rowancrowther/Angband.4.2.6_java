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
 * One entry in an allocation table: a plain data holder with no behaviour of its
 * own. Each entry records the thing's index, its base dungeon level, and three
 * probability weights, one per allocation pass. The weights are filled in at
 * different times: pass 1 ({@code prob1}) comes from the allocation information
 * in the game data, pass 2 ({@code prob2}) from the allocation restriction (the
 * caller's filter), and pass 3 ({@code prob3}) from the allocation calculation at
 * the level being generated.
 *
 * <p>This is the Java port of {@code struct alloc_entry} in the C original's
 * {@code alloc.h}. All fields are plain {@code int}, as in C, and default to
 * zero, matching the zero-filled tables C allocates ({@code mem_zalloc}).
 *
 * <p>The header calls it an object/monster entry, but in the 4.2.6 C source only two
 * tables hold it: the monster race table ({@code alloc_race_table} in
 * {@code mon-make.c}, built by {@code init_race_allocs()}) and the ego-item table
 * ({@code alloc_ego_table} in {@code obj-make.c}, built by
 * {@code alloc_init_egos()}). Plain object kinds use a different structure, the
 * cumulative {@code obj_alloc} arrays. Both tables are sorted by level, and C
 * starts every entry with all three probabilities equal; each later pass sets its
 * probability either to the previous pass's value or to zero, so
 * {@code prob2} and {@code prob3} never exceed {@code prob1}.
 *
 * <p>Class AllocEntry coded before 261125, commented in full on 261125.
 *
 * @author Rowan Crowther
 */
public class AllocEntry {
    /**
     * The actual index of the thing this entry allocates, into the record list
     * the table was built from: the monster races for the race table, the ego
     * items for the ego table (C field {@code index}). It is the entry's identity;
     * the position of the entry in the table is not, because the table is
     * ordered by level.
     *
     * <p>Field index coded before 261125, commented in full on 261125.
     */
    private int index;
    /**
     * Base dungeon level of the thing: the depth at which it naturally appears
     * (C field {@code level}). For a monster race it is the race's level, and
     * {@code get_mon_num()} stops scanning at the first entry deeper than the
     * level being generated. For an ego item C stores the minimum allocation
     * depth here and marks it "Unused": nothing reads it back.
     *
     * <p>Field level coded before 261125, commented in full on 261125.
     */
    private int level;
    /**
     * Allocation probability for pass 1, determined from the allocation
     * information in the game data (C field {@code prob1}). It is the base weight
     * and is never changed after the table is built: for a monster race it is
     * {@code (100 / rarity) * (1 + level / 10)} with integer division, for an ego
     * item it is the {@code alloc_prob}.
     *
     * <p>Field prob1 coded before 261125, commented in full on 261125.
     */
    private int prob1;
    /**
     * Allocation probability for pass 2, determined from the allocation
     * restriction, i.e. the caller's filter (C field {@code prob2}). In C it is
     * always either {@code prob1} (the filter accepted the thing, or there was no
     * filter) or 0 (the filter rejected it); {@code get_mon_num_prep()} resets it
     * for the whole race table on every call.
     *
     * <p>Field prob2 coded before 261125, commented in full on 261125.
     */
    private int prob2;
    /**
     * Allocation probability for pass 3, determined from the allocation
     * calculation at the level being generated (C field {@code prob3}). In C it is
     * always either {@code prob2} or 0: {@code get_mon_num()} zeroes it for
     * entries the universal rules exclude (town monsters in the dungeon, seasonal
     * monsters outside 24-26 December, a unique already at its maximum count, a
     * force-depth monster deeper than the current level) and recomputes it on every
     * call. Entries deeper than the generated level are not visited, so they keep
     * whatever an earlier call left in this field.
     *
     * <p>Field prob3 coded before 261125, commented in full on 261125.
     */
    private int prob3;

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getIndex coded before 261125, commented in full on 260929.
     *
     * @return the index of the allocated thing
     */
    public int getIndex() {
        return index;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setIndex coded before 261125, commented in full on 260929.
     *
     * @param index the index of the allocated thing
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getLevel coded before 261125, commented in full on 260929.
     *
     * @return the base dungeon level of the allocated thing
     */
    public int getLevel() {
        return level;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setLevel coded before 261125, commented in full on 260929.
     *
     * @param level the base dungeon level of the allocated thing
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getProb1 coded before 261125, commented in full on 260929.
     *
     * @return the pass 1 probability (from allocation information)
     */
    public int getProb1() {
        return prob1;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setProb1 coded before 261125, commented in full on 260929.
     *
     * @param prob1 the pass 1 probability (from allocation information)
     */
    public void setProb1(int prob1) {
        this.prob1 = prob1;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getProb2 coded before 261125, commented in full on 260929.
     *
     * @return the pass 2 probability (from allocation restriction)
     */
    public int getProb2() {
        return prob2;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setProb2 coded before 261125, commented in full on 260929.
     *
     * @param prob2 the pass 2 probability (from allocation restriction)
     */
    public void setProb2(int prob2) {
        this.prob2 = prob2;
    }

    /**
     * Plain getter; no C equivalent, as C reads the struct field directly.
     *
     * <p>Method getProb3 coded before 261125, commented in full on 260929.
     *
     * @return the pass 3 probability (from allocation calculation)
     */
    public int getProb3() {
        return prob3;
    }

    /**
     * Plain setter; no C equivalent, as C writes the struct field directly.
     *
     * <p>Method setProb3 coded before 261125, commented in full on 260929.
     *
     * @param prob3 the pass 3 probability (from allocation calculation)
     */
    public void setProb3(int prob3) {
        this.prob3 = prob3;
    }
}
