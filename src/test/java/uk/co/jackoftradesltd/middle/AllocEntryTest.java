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
}
