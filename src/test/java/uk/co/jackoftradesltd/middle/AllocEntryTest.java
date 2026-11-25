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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link AllocEntry}, the port of {@code struct alloc_entry} in the C
 * original's {@code alloc.h}. The C struct is five plain {@code int} fields
 * that C reads and writes directly and allocates zero-filled, so the contract
 * is: defaults are zero, each field round-trips, and no field aliases another.
 *
 * <p>Class AllocEntryTest coded before 261125, extended on 261125 with the race-table and
 * ego-table usage scenarios from {@code mon-make.c} and {@code obj-make.c}.
 */
class AllocEntryTest {

    /**
     * C tables are zero-filled ({@code mem_zalloc}); a fresh entry must be all zero.
     */
    @Test
    void freshEntryIsZeroFilled() {
        AllocEntry e = new AllocEntry();
        assertEquals(0, e.getIndex());
        assertEquals(0, e.getLevel());
        assertEquals(0, e.getProb1());
        assertEquals(0, e.getProb2());
        assertEquals(0, e.getProb3());
    }

    /**
     * Setting one field must leave the other four untouched (index/level/prob1-3 are separate).
     */
    @Test
    void fieldsDoNotAlias() {
        AllocEntry e = new AllocEntry();
        e.setIndex(11);
        e.setLevel(22);
        e.setProb1(33);
        e.setProb2(44);
        e.setProb3(55);
        assertEquals(11, e.getIndex());
        assertEquals(22, e.getLevel());
        assertEquals(33, e.getProb1());
        assertEquals(44, e.getProb2());
        assertEquals(55, e.getProb3());
    }

    /**
     * Overwriting one field leaves the rest as they were.
     */
    @Test
    void overwriteOneFieldOnly() {
        AllocEntry e = new AllocEntry();
        e.setProb1(100);
        e.setProb2(50);
        e.setProb3(25);
        e.setProb2(7);
        assertEquals(100, e.getProb1());
        assertEquals(7, e.getProb2());
        assertEquals(25, e.getProb3());
    }

    /**
     * Boundary values of a C {@code int}: zero, negatives (never used by C but not
     * clamped by it either) and the 32-bit extremes must round-trip unchanged on
     * every field.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 255, -1, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void everyFieldRoundTripsIntValues(int v) {
        AllocEntry e = new AllocEntry();
        e.setIndex(v);
        e.setLevel(v);
        e.setProb1(v);
        e.setProb2(v);
        e.setProb3(v);
        assertEquals(v, e.getIndex());
        assertEquals(v, e.getLevel());
        assertEquals(v, e.getProb1());
        assertEquals(v, e.getProb2());
        assertEquals(v, e.getProb3());
    }

    /**
     * Two entries are independent records (C table slots are separate structs): setting
     * every field on one must leave a second fresh entry all zero.
     */
    @Test
    void separateEntriesDoNotShareState() {
        AllocEntry a = new AllocEntry();
        AllocEntry b = new AllocEntry();
        a.setIndex(5);
        a.setLevel(6);
        a.setProb1(7);
        a.setProb2(8);
        a.setProb3(9);
        assertEquals(0, b.getIndex());
        assertEquals(0, b.getLevel());
        assertEquals(0, b.getProb1());
        assertEquals(0, b.getProb2());
        assertEquals(0, b.getProb3());
    }

    /**
     * An entry loaded the way {@code init_race_allocs()} in {@code mon-make.c} loads a
     * monster race: index, level and one probability copied into all three passes. The
     * race used is rarity 2, level 25, so p = (100 / 2) * (1 + 25 / 10) = 50 * 3 = 150.
     */
    @Test
    void raceEntryLoadedWithAllThreePassesEqual() {
        AllocEntry e = new AllocEntry();
        e.setIndex(42);
        e.setLevel(25);
        e.setProb1(150);
        e.setProb2(150);
        e.setProb3(150);
        assertEquals(42, e.getIndex());
        assertEquals(25, e.getLevel());
        assertEquals(150, e.getProb1());
        assertEquals(e.getProb1(), e.getProb2());
        assertEquals(e.getProb2(), e.getProb3());
    }

    /**
     * A restriction pass as {@code get_mon_num_prep()} runs it: a rejected entry gets
     * prob2 = 0 and keeps prob1; the next call with no filter restores prob2 = prob1.
     * Neither step may disturb prob1, level or index.
     */
    @Test
    void restrictionZeroesProb2ThenRestoresFromProb1() {
        AllocEntry e = new AllocEntry();
        e.setIndex(3);
        e.setLevel(10);
        e.setProb1(60);
        e.setProb2(60);
        e.setProb3(60);

        e.setProb2(0);
        assertEquals(60, e.getProb1());
        assertEquals(0, e.getProb2());
        assertEquals(60, e.getProb3());

        e.setProb2(e.getProb1());
        assertEquals(60, e.getProb2());
        assertEquals(3, e.getIndex());
        assertEquals(10, e.getLevel());
    }

    /**
     * An ego-item entry as {@code alloc_init_egos()} in {@code obj-make.c} loads it: the
     * level holds the minimum allocation depth (0 is a legal depth) and the three
     * probabilities all hold the item's allocation probability.
     */
    @Test
    void egoEntryAtDepthZero() {
        AllocEntry e = new AllocEntry();
        e.setIndex(0);
        e.setLevel(0);
        e.setProb1(20);
        e.setProb2(20);
        e.setProb3(20);
        assertEquals(0, e.getIndex());
        assertEquals(0, e.getLevel());
        assertEquals(20, e.getProb1());
        assertEquals(20, e.getProb2());
        assertEquals(20, e.getProb3());
    }
}
