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

package uk.co.jackoftradesltd.backend.io.savefiles;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SavefileDetails}, the port of C's {@code struct
 * savefile_details} ({@code src/ui-game.h}).
 * <p>
 * Expected values are derived from the C original: {@code got_savefile()} in
 * {@code src/ui-game.c} zero-allocates the enclosing {@code
 * savefile_getter_impl} with {@code mem_zalloc} before populating it, so
 * {@code fnam} and {@code desc} start as {@code NULL} and {@code foff} starts
 * as {@code 0}; the Java port uses empty strings in place of {@code NULL}. The
 * struct itself has no behaviour beyond holding whatever {@code fnam}, {@code
 * desc}, and {@code foff} are set to, so these tests cover the default state
 * and a plain round trip through each setter/getter pair.
 * <p>
 * Class SavefileDetailsTest coded on 260915, commented in full on 260915.
 *
 * @author Rowan Crowther
 */
class SavefileDetailsTest {

    @Test
    void defaultConstructorMatchesTheZeroAllocatedCStruct() {
        SavefileDetails details = new SavefileDetails();

        assertEquals("", details.getFileName(), "fnam starts NULL in C; empty string here");
        assertEquals("", details.getDescription(), "desc starts NULL in C; empty string here");
        assertEquals(0, details.getOffset(), "foff starts 0 via mem_zalloc");
    }

    @Test
    void fileNameRoundTripsThroughSetterAndGetter() {
        SavefileDetails details = new SavefileDetails();

        details.setFileName("1.PlayerOne");

        assertEquals("1.PlayerOne", details.getFileName());
    }

    @Test
    void descriptionRoundTripsThroughSetterAndGetter() {
        SavefileDetails details = new SavefileDetails();

        details.setDescription("PlayerOne, the Longbeard Dwarf Warrior");

        assertEquals("PlayerOne, the Longbeard Dwarf Warrior", details.getDescription());
    }

    @Test
    void offsetRoundTripsThroughSetterAndGetter() {
        SavefileDetails details = new SavefileDetails();

        details.setOffset(2);

        assertEquals(2, details.getOffset());
    }

    @Test
    void offsetMatchesTheLengthOfTheSetgidUserPrefixWhenSet() {
        SavefileDetails details = new SavefileDetails();
        String fileName = "1.PlayerOne";

        details.setFileName(fileName);
        details.setOffset(2);

        assertEquals("PlayerOne", fileName.substring(details.getOffset()),
                "foff is meant to skip past the \"<uid>.\" prefix set up under SETGID");
    }
}
