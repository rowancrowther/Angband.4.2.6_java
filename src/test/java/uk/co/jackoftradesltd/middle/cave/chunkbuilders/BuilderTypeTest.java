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

package uk.co.jackoftradesltd.middle.cave.chunkbuilders;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks {@link BuilderType} against C's {@code cave_builders[]}, which {@code generate.c} builds
 * from {@code list-dun-profiles.h}. The expected names and order are read from that header, not
 * from the enum.
 */
class BuilderTypeTest {
    /**
     * The {@code DUN(name, ...)} rows of {@code list-dun-profiles.h}, in file order.
     */
    private static final List<String> C_NAMES = List.of(
            "town", "modified", "moria", "lair", "gauntlet",
            "hard centre", "labyrinth", "cavern", "classic");

    @Test
    void sameNumberOfEntriesAsC() {
        assertEquals(C_NAMES.size(), BuilderType.values().length);
    }

    @Test
    void namesAndOrderMatchC() {
        List<String> actual = Arrays.stream(BuilderType.values()).map(BuilderType::getName).toList();
        assertEquals(C_NAMES, actual);
    }

    @Test
    void namesAreUnique() {
        assertEquals(C_NAMES.size(),
                new HashSet<>(Arrays.stream(BuilderType.values()).map(BuilderType::getName).toList()).size());
    }

    @Test
    void multiWordNameKeepsItsSpace() {
        assertEquals("hard centre", BuilderType.HARD_CENTRE.getName());
    }

    @Test
    void lookupByNameFindsEveryEntryLikeParseProfileName() {
        for (String name : C_NAMES) {
            boolean found = Arrays.stream(BuilderType.values()).anyMatch(b -> b.getName().equals(name));
            assertTrue(found, name);
        }
    }

    @Test
    void lookupIsExactLikeStreq() {
        for (String bad : List.of("Town", "hard_centre", "hard centre ", "", "vault")) {
            boolean found = Arrays.stream(BuilderType.values()).anyMatch(b -> b.getName().equals(bad));
            assertTrue(!found, bad);
        }
    }
}
