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

package uk.co.jackoftradesltd.middle.cave.profiles.room;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.RoomProfileReader;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link RoomTemplate}, the port of C's {@code struct room_template}
 * ({@code generate.h}).
 *
 * <p>The class is a pure data holder, so the tests are in two halves: the constructor and getters
 * hand back exactly what they were given, and the records loaded from the real
 * {@code room_template.txt} satisfy the invariants C's {@code build_room_template()} and
 * {@code random_room_template()} rely on. The expected figures in the second half (500 records,
 * type 1 throughout, the rating and door histograms) were counted from the C original's
 * {@code lib/gamedata/room_template.txt} with {@code grep}, not read back from the Java.
 *
 * <p>Class RoomTemplateTest written on 260930.
 *
 * @author Rowan Crowther
 */
class RoomTemplateTest {

    private static final String REAL_FILE = "lib/gamedata/room_template.txt";

    private static List<RoomTemplate> load() throws IOException {
        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(REAL_FILE);
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        return result.items();
    }

    /**
     * Every constructor argument comes back from its own getter; none are swapped, and the
     * references are kept rather than copied.
     */
    @Test
    void constructorArgumentsComeBackFromTheirOwnGetters() {
        Flag<RoomFlags> flags = new Flag<>(RoomFlags.class, RoomFlags.FEW_ENTRANCES);
        List<String> map = List.of("%%%", "%.%", "%%%");

        RoomTemplate t = new RoomTemplate("Test room", "%%%%.%%%%", map, flags,
                1, 2, 3, 4, 5, TValue.TV_ROD);

        assertEquals("Test room", t.getName());
        assertEquals("%%%%.%%%%", t.getMapText());
        assertSame(map, t.getMap());
        assertSame(flags, t.getFlags());
        assertEquals(1, t.getType());
        assertEquals(2, t.getRating());
        assertEquals(3, t.getHeight());
        assertEquals(4, t.getWidth());
        assertEquals(5, t.getDoors());
        assertEquals(TValue.TV_ROD, t.getTval());
    }

    /**
     * C zero-initialises a record ({@code mem_zalloc}), so an absent {@code tval:} is 0, which is
     * {@link TValue#TV_NONE}, and an absent {@code flags:} is an empty set. The class itself must
     * carry both without complaint.
     */
    @Test
    void anAbsentTvalAndAnEmptyFlagSetAreCarriedAsGiven() {
        RoomTemplate t = new RoomTemplate("Plain", "%", List.of("%"),
                new Flag<>(RoomFlags.class), 1, 1, 1, 1, 1, TValue.TV_NONE);

        assertEquals(TValue.TV_NONE, t.getTval());
        assertTrue(t.getFlags().isEmpty());
    }

    /**
     * {@code build_room_template()} walks {@code hgt * wid} characters of {@code text}, so every
     * real record's flat text must be exactly that long and must equal its rows joined with no
     * separator.
     */
    @Test
    void everyRealTemplateTextIsHeightTimesWidthLong() throws IOException {
        for (RoomTemplate t : load()) {
            assertEquals(t.getHeight() * t.getWidth(), t.getMapText().length(), t.getName());
            assertEquals(t.getHeight(), t.getMap().size(), t.getName());
            assertEquals(String.join("", t.getMap()), t.getMapText(), t.getName());
        }
    }

    /**
     * {@code random_room_template()} matches on {@code typ}; all 500 records in the C data carry
     * {@code type:1} (counted with {@code grep | sort | uniq -c}).
     */
    @Test
    void everyRealTemplateIsTypeOne() throws IOException {
        for (RoomTemplate t : load()) {
            assertEquals(1, t.getType(), t.getName());
        }
    }

    /**
     * The rating histogram of the C data file: 417 rated 1, 63 rated 2, 20 rated 3. This is what
     * {@code random_room_template()}'s uniform pick is over, per rating.
     */
    @Test
    void theRatingHistogramMatchesTheCDataFile() throws IOException {
        Map<Integer, Integer> counts = new TreeMap<>();
        for (RoomTemplate t : load()) {
            counts.merge(t.getRating(), 1, Integer::sum);
        }

        assertEquals(Map.of(1, 417, 2, 63, 3, 20), counts);
    }

    /**
     * The door-count histogram of the C data file. {@code build_room_template()} calls
     * {@code randint1(doors)}, so a {@code doors} of 0 would be invalid; the data's minimum is 1.
     */
    @Test
    void theDoorHistogramMatchesTheCDataFileAndNeverHitsZero() throws IOException {
        Map<Integer, Integer> counts = new TreeMap<>();
        for (RoomTemplate t : load()) {
            counts.merge(t.getDoors(), 1, Integer::sum);
        }

        assertEquals(Map.of(1, 255, 2, 113, 3, 46, 4, 75, 5, 7, 6, 4), counts);
    }
}
