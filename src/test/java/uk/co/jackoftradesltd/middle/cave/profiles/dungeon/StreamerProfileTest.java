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
 * Tests for {@link StreamerProfile}, the port of {@code struct streamer_profile} in the C
 * original's {@code generate.h}. The type is a plain data holder, so the contract is that each
 * constructor argument comes back from its own getter, in the order of the {@code streamer:} line
 * ({@code den : rng : mag : mc : qua : qc}), with nothing clamped.
 *
 * <p>Expected values are taken from {@code lib/gamedata/dungeon_profile.txt}, not from the Java
 * code.
 *
 * <p>Class StreamerProfileTest coded on 260930, commented in full on 260930.
 */
class StreamerProfileTest {

    /**
     * The {@code classic}, {@code modified}, {@code moria} and {@code lair} records all read
     * {@code streamer:5:2:3:90:2:40}. Every value is distinct, so a getter wired to the wrong
     * field, or a constructor that swaps two arguments, fails. C's {@code mag} is {@code getMam}.
     */
    @Test
    void classicRecordRoundTripsEveryField() {
        StreamerProfile s = new StreamerProfile(5, 2, 3, 90, 2, 40);

        assertEquals(5, s.getDen());
        assertEquals(2, s.getRng());
        assertEquals(3, s.getMam());
        assertEquals(90, s.getMc());
        assertEquals(2, s.getQua());
        assertEquals(40, s.getQc());
    }

    /**
     * The {@code town} record, {@code streamer:1:1:0:0:0:0}: no streamers of either kind, and
     * the treasure chances of zero are kept as zero rather than replaced by a default.
     */
    @Test
    void townRecordKeepsZeroCounts() {
        StreamerProfile s = new StreamerProfile(1, 1, 0, 0, 0, 0);

        assertEquals(1, s.getDen());
        assertEquals(1, s.getRng());
        assertEquals(0, s.getMam());
        assertEquals(0, s.getMc());
        assertEquals(0, s.getQua());
        assertEquals(0, s.getQc());
    }

    /**
     * C's {@code parse_profile_streamer} stores {@code parser_getint} results unvalidated, so
     * negative and extreme values must come back untouched.
     */
    @ParameterizedTest
    @CsvSource({
            "-1, -1, -1, -1, -1, -1",
            "2147483647, 2147483647, 2147483647, 2147483647, 2147483647, 2147483647",
            "0, 0, 0, 0, 0, 0"
    })
    void valuesAreStoredRaw(int den, int rng, int mam, int mc, int qua, int qc) {
        StreamerProfile s = new StreamerProfile(den, rng, mam, mc, qua, qc);

        assertEquals(den, s.getDen());
        assertEquals(rng, s.getRng());
        assertEquals(mam, s.getMam());
        assertEquals(mc, s.getMc());
        assertEquals(qua, s.getQua());
        assertEquals(qc, s.getQc());
    }
}
