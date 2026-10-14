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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link ObjectFlagID} against {@code enum object_flag_id} in {@code obj-properties.h}
 * and the {@code streq} chain in {@code parse_object_property_id_type} ({@code obj-init.c}).
 *
 * <p>Expected ordinals count down the C enum (OFID_NONE = 0, OFID_NORMAL, OFID_TIMED,
 * OFID_WIELD = 3); the C enum has no OFID_MAX. Expected tokens are the three strings the C
 * chain compares against ({@code "on effect"}, {@code "timed"}, {@code "on wield"}).
 */
class ObjectFlagIDTest {

    @Test
    void ordinalsMatchCDeclarationOrder() {
        assertEquals(0, ObjectFlagID.OFID_NONE.ordinal());
        assertEquals(1, ObjectFlagID.OFID_NORMAL.ordinal());
        assertEquals(2, ObjectFlagID.OFID_TIMED.ordinal());
        assertEquals(3, ObjectFlagID.OFID_WIELD.ordinal());
        assertEquals(4, ObjectFlagID.values().length);
    }

    @Test
    void everyCToken_resolvesToItsConstant() {
        assertSame(ObjectFlagID.OFID_NORMAL, ObjectFlagID.getFlagID("on effect"));
        assertSame(ObjectFlagID.OFID_TIMED, ObjectFlagID.getFlagID("timed"));
        assertSame(ObjectFlagID.OFID_WIELD, ObjectFlagID.getFlagID("on wield"));
    }

    @Test
    void getIdMethod_returnsTheCToken() {
        assertEquals("", ObjectFlagID.OFID_NONE.getIdMethod());
        assertEquals("on effect", ObjectFlagID.OFID_NORMAL.getIdMethod());
        assertEquals("timed", ObjectFlagID.OFID_TIMED.getIdMethod());
        assertEquals("on wield", ObjectFlagID.OFID_WIELD.getIdMethod());
    }

    @Test
    void getIdMethod_roundTripsEveryConstant() {
        for (ObjectFlagID id : ObjectFlagID.values()) {
            assertSame(id, ObjectFlagID.getFlagID(id.getIdMethod()));
        }
    }

    /** A property with no {@code id-type:} line keeps C's zero default, OFID_NONE. */
    @Test
    void emptyToken_resolvesToNone() {
        assertSame(ObjectFlagID.OFID_NONE, ObjectFlagID.getFlagID(""));
    }

    /** C's chain is {@code streq}: exact, case-sensitive, no trimming. These all hit the error branch. */
    @Test
    void unrecognizedTokens_returnNull() {
        assertNull(ObjectFlagID.getFlagID("On effect"));
        assertNull(ObjectFlagID.getFlagID("ON EFFECT"));
        assertNull(ObjectFlagID.getFlagID("on  effect"));
        assertNull(ObjectFlagID.getFlagID(" timed"));
        assertNull(ObjectFlagID.getFlagID("timed "));
        assertNull(ObjectFlagID.getFlagID("wield"));
        assertNull(ObjectFlagID.getFlagID("on_wield"));
        assertNull(ObjectFlagID.getFlagID("normal"));
        assertNull(ObjectFlagID.getFlagID("OFID_WIELD"));
        assertNull(ObjectFlagID.getFlagID("none"));
    }

    @Test
    void nullToken_returnsNullWithoutThrowing() {
        assertNull(ObjectFlagID.getFlagID(null));
    }

    /**
     * Every distinct {@code id-type:} value in the real data file is one the C chain accepts, and
     * each of the three C tokens is used (the file has 15 on effect, 6 timed, 17 on wield).
     */
    @Test
    void everyIdTypeInObjectPropertyTxt_resolves() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("lib", "gamedata", "object_property.txt"));
        Set<String> tokens = new TreeSet<>();
        for (String line : lines) {
            if (line.startsWith("id-type:")) {
                String token = line.substring("id-type:".length());
                assertNotNull(ObjectFlagID.getFlagID(token), "unresolved id-type: " + token);
                tokens.add(token);
            }
        }
        assertEquals(Set.of("on effect", "timed", "on wield"), tokens);
    }
}
