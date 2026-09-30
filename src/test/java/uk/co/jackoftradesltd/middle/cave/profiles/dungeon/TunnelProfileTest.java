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

package uk.co.jackoftradesltd.middle.cave.profiles.dungeon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link TunnelProfile}, the port of {@code struct tunnel_profile} in the C original's
 * {@code generate.h}. The type is a plain data holder, so the contract is that each constructor
 * argument comes back from its own getter, in the order of the {@code tunnel:} line
 * ({@code rnd : chg : con : pen : jct}), with nothing clamped.
 *
 * <p>Expected values are taken from {@code lib/gamedata/dungeon_profile.txt} and from
 * {@code parse_profile_tunnel} in {@code generate.c}, not from the Java code.
 *
 * <p>Class TunnelProfileTest coded on 260930, commented in full on 260930.
 */
class TunnelProfileTest {

    /**
     * The {@code classic} record reads {@code tunnel:10:30:15:25:50}. Every value is distinct, so a
     * getter wired to the wrong field, or a constructor that swaps two arguments, fails.
     */
    @Test
    void classicRecordRoundTripsEveryField() {
        TunnelProfile t = new TunnelProfile(10, 30, 15, 25, 50);

        assertEquals(10, t.getRnd());
        assertEquals(30, t.getChg());
        assertEquals(15, t.getCon());
        assertEquals(25, t.getPen());
        assertEquals(50, t.getJct());
    }

    /**
     * The constructor takes its arguments in {@code tunnel:} line order, which is the order
     * {@code parse_profile_tunnel} reads them, not the order of the struct's comments or of the
     * getters. A distinct power of ten per slot makes any transposition visible.
     */
    @Test
    void argumentOrderMatchesTunnelLine() {
        TunnelProfile t = new TunnelProfile(1, 10, 100, 1000, 10000);

        assertEquals(1, t.getRnd());
        assertEquals(10, t.getChg());
        assertEquals(100, t.getCon());
        assertEquals(1000, t.getPen());
        assertEquals(10000, t.getJct());
    }

    /**
     * C's {@code parse_profile_tunnel} stores {@code parser_getint} results unvalidated, so
     * zero, negative and extreme values, including ones above 100, must come back untouched.
     */
    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0, 0",
            "-1, -1, -1, -1, -1",
            "100, 100, 100, 100, 100",
            "101, 250, 1000, 500, 999",
            "2147483647, 2147483647, 2147483647, 2147483647, 2147483647"
    })
    void valuesAreStoredRaw(int rnd, int chg, int con, int pen, int jct) {
        TunnelProfile t = new TunnelProfile(rnd, chg, con, pen, jct);

        assertEquals(rnd, t.getRnd());
        assertEquals(chg, t.getChg());
        assertEquals(con, t.getCon());
        assertEquals(pen, t.getPen());
        assertEquals(jct, t.getJct());
    }
}
