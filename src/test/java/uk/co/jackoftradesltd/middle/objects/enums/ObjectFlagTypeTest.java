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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Pins {@link ObjectFlagType} against {@code enum object_flag_type} in {@code obj-properties.h}
 * and the {@code streq} chain in {@code parse_object_property_subtype} ({@code obj-init.c}).
 *
 * <p>Expected ordinals count down the C enum (OFT_NONE = 0, then each name in declaration
 * order, OFT_MAX = 10). Expected tokens are the nine strings the C chain compares against,
 * which are also the nine distinct {@code subtype:} values in {@code object_property.txt}.
 */
class ObjectFlagTypeTest {

    @Test
    void ordinalsMatchCDeclarationOrder() {
        assertEquals(0, ObjectFlagType.OFT_NONE.ordinal());
        assertEquals(1, ObjectFlagType.OFT_SUST.ordinal());
        assertEquals(2, ObjectFlagType.OFT_PROT.ordinal());
        assertEquals(3, ObjectFlagType.OFT_MISC.ordinal());
        assertEquals(4, ObjectFlagType.OFT_LIGHT.ordinal());
        assertEquals(5, ObjectFlagType.OFT_MELEE.ordinal());
        assertEquals(6, ObjectFlagType.OFT_BAD.ordinal());
        assertEquals(7, ObjectFlagType.OFT_DIG.ordinal());
        assertEquals(8, ObjectFlagType.OFT_THROW.ordinal());
        assertEquals(9, ObjectFlagType.OFT_CURSE_ONLY.ordinal());
        assertEquals(10, ObjectFlagType.OFT_MAX.ordinal());
        assertEquals(11, ObjectFlagType.values().length);
    }

    @Test
    void everyCToken_resolvesToItsConstant() {
        assertSame(ObjectFlagType.OFT_SUST, ObjectFlagType.getFlagTypeFromSubtype("sustain"));
        assertSame(ObjectFlagType.OFT_PROT, ObjectFlagType.getFlagTypeFromSubtype("protection"));
        assertSame(ObjectFlagType.OFT_MISC, ObjectFlagType.getFlagTypeFromSubtype("misc ability"));
        assertSame(ObjectFlagType.OFT_LIGHT, ObjectFlagType.getFlagTypeFromSubtype("light"));
        assertSame(ObjectFlagType.OFT_MELEE, ObjectFlagType.getFlagTypeFromSubtype("melee"));
        assertSame(ObjectFlagType.OFT_BAD, ObjectFlagType.getFlagTypeFromSubtype("bad"));
        assertSame(ObjectFlagType.OFT_DIG, ObjectFlagType.getFlagTypeFromSubtype("dig"));
        assertSame(ObjectFlagType.OFT_THROW, ObjectFlagType.getFlagTypeFromSubtype("throw"));
        assertSame(ObjectFlagType.OFT_CURSE_ONLY, ObjectFlagType.getFlagTypeFromSubtype("curse-only"));
    }

    @Test
    void getSubtypeText_roundTripsEveryRealGrouping() {
        for (ObjectFlagType type : ObjectFlagType.values()) {
            if (type == ObjectFlagType.OFT_NONE || type == ObjectFlagType.OFT_MAX) {
                assertEquals("", type.getSubtypeText());
            } else {
                assertSame(type, ObjectFlagType.getFlagTypeFromSubtype(type.getSubtypeText()));
            }
        }
    }

    /** A property with no {@code subtype:} line keeps C's zero default, OFT_NONE, never OFT_MAX. */
    @Test
    void emptyToken_resolvesToNoneNotMax() {
        assertSame(ObjectFlagType.OFT_NONE, ObjectFlagType.getFlagTypeFromSubtype(""));
    }

    /** C's chain is {@code streq}: exact, case-sensitive, no trimming. These all hit the error branch. */
    @Test
    void unrecognizedTokens_returnNull() {
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("Sustain"));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("SUSTAIN"));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype(" light"));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("light "));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("misc"));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("curse_only"));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("OFT_SUST"));
        assertNull(ObjectFlagType.getFlagTypeFromSubtype("none"));
    }
}
