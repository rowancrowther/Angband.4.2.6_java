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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link ObjPropertyType} against {@code enum obj_property_type} in {@code obj-properties.h}
 * and the {@code streq} chain in {@code parse_object_property_type} ({@code obj-init.c}).
 *
 * <p>Expected ordinals count down the C enum (OBJ_PROPERTY_NONE = 0, then each name in
 * declaration order, OBJ_PROPERTY_MAX = 8). Expected tokens are the seven strings the C chain
 * compares against, which are also the seven distinct {@code type:} values in
 * {@code object_property.txt}. C has no branch for any other word, so every other input,
 * including the placeholder tokens of NONE and MAX, must come back {@code null}.
 *
 * <p>The {@code lookup} tests have no C counterpart to derive from: they pin the Java-only name
 * match against the constant names in {@code enum obj_property_type}.
 *
 * <p>Class ObjPropertyTypeTest coded on 261009, {@code lookup} tests added on 261115,
 * commented in full on 261115.
 */
class ObjPropertyTypeTest {

    @Test
    void ordinalsMatchCDeclarationOrder() {
        assertEquals(0, ObjPropertyType.OBJ_PROPERTY_NONE.ordinal());
        assertEquals(1, ObjPropertyType.OBJ_PROPERTY_STAT.ordinal());
        assertEquals(2, ObjPropertyType.OBJ_PROPERTY_MOD.ordinal());
        assertEquals(3, ObjPropertyType.OBJ_PROPERTY_FLAG.ordinal());
        assertEquals(4, ObjPropertyType.OBJ_PROPERTY_IGNORE.ordinal());
        assertEquals(5, ObjPropertyType.OBJ_PROPERTY_RESIST.ordinal());
        assertEquals(6, ObjPropertyType.OBJ_PROPERTY_VULN.ordinal());
        assertEquals(7, ObjPropertyType.OBJ_PROPERTY_IMM.ordinal());
        assertEquals(8, ObjPropertyType.OBJ_PROPERTY_MAX.ordinal());
        assertEquals(9, ObjPropertyType.values().length);
    }

    @Test
    void everyCToken_resolvesToItsConstant() {
        assertSame(ObjPropertyType.OBJ_PROPERTY_STAT, ObjPropertyType.fromValue("stat"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_MOD, ObjPropertyType.fromValue("mod"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjPropertyType.fromValue("flag"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_IGNORE, ObjPropertyType.fromValue("ignore"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_RESIST, ObjPropertyType.fromValue("resistance"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_VULN, ObjPropertyType.fromValue("vulnerability"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_IMM, ObjPropertyType.fromValue("immunity"));
    }

    /**
     * C's chain has no branch for the empty string, so a {@code type:} of nothing is
     * {@code PARSE_ERROR_INVALID_PROPERTY} and {@code prop->type} stays at zero. It must not
     * resolve to OBJ_PROPERTY_NONE.
     */
    @Test
    void emptyToken_isRejectedNotNone() {
        assertNull(ObjPropertyType.fromValue(""));
    }

    /** "max" is not one of C's seven words; it must not resolve to the count sentinel. */
    @Test
    void maxToken_isRejectedNotMax() {
        assertNull(ObjPropertyType.fromValue("max"));
    }

    /** No real category may resolve to a placeholder, and no placeholder to anything. */
    @Test
    void placeholderTokens_neverResolve() {
        for (ObjPropertyType type : ObjPropertyType.values()) {
            ObjPropertyType resolved = switch (type) {
                case OBJ_PROPERTY_NONE -> ObjPropertyType.fromValue("");
                case OBJ_PROPERTY_MAX -> ObjPropertyType.fromValue("max");
                default -> null;
            };
            assertNull(resolved, type + " must not be reachable from a data file");
        }
    }

    /** C's chain is {@code streq}: exact, case-sensitive, no trimming, no abbreviations. */
    @Test
    void unrecognizedTokens_returnNull() {
        assertNull(ObjPropertyType.fromValue("Stat"));
        assertNull(ObjPropertyType.fromValue("STAT"));
        assertNull(ObjPropertyType.fromValue(" stat"));
        assertNull(ObjPropertyType.fromValue("stat "));
        assertNull(ObjPropertyType.fromValue("resist"));
        assertNull(ObjPropertyType.fromValue("vuln"));
        assertNull(ObjPropertyType.fromValue("imm"));
        assertNull(ObjPropertyType.fromValue("resistances"));
        assertNull(ObjPropertyType.fromValue("OBJ_PROPERTY_STAT"));
        assertNull(ObjPropertyType.fromValue("none"));
        assertNull(ObjPropertyType.fromValue(" "));
    }

    /** A {@code null} token (no line parsed at all) must be rejected, not throw. */
    @Test
    void nullToken_returnsNull() {
        assertNull(ObjPropertyType.fromValue(null));
    }

    /**
     * {@code lookup} is Java-only (C has no name-to-enum path besides the {@code type:} chain), so
     * its expected values are the constant names in {@code enum obj_property_type}
     * ({@code obj-properties.h}), given in full.
     */
    @Test
    void lookup_fullCNames_resolveToTheirConstants() {
        assertSame(ObjPropertyType.OBJ_PROPERTY_NONE, ObjPropertyType.lookup("OBJ_PROPERTY_NONE"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_STAT, ObjPropertyType.lookup("OBJ_PROPERTY_STAT"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_MOD, ObjPropertyType.lookup("OBJ_PROPERTY_MOD"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjPropertyType.lookup("OBJ_PROPERTY_FLAG"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_IGNORE, ObjPropertyType.lookup("OBJ_PROPERTY_IGNORE"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_RESIST, ObjPropertyType.lookup("OBJ_PROPERTY_RESIST"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_VULN, ObjPropertyType.lookup("OBJ_PROPERTY_VULN"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_IMM, ObjPropertyType.lookup("OBJ_PROPERTY_IMM"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_MAX, ObjPropertyType.lookup("OBJ_PROPERTY_MAX"));
    }

    /** The {@code OBJ_PROPERTY_} prefix is optional, so each short name finds the same constant. */
    @Test
    void lookup_namesWithoutPrefix_resolveToTheirConstants() {
        assertSame(ObjPropertyType.OBJ_PROPERTY_NONE, ObjPropertyType.lookup("NONE"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_STAT, ObjPropertyType.lookup("STAT"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_MOD, ObjPropertyType.lookup("MOD"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjPropertyType.lookup("FLAG"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_IGNORE, ObjPropertyType.lookup("IGNORE"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_RESIST, ObjPropertyType.lookup("RESIST"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_VULN, ObjPropertyType.lookup("VULN"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_IMM, ObjPropertyType.lookup("IMM"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_MAX, ObjPropertyType.lookup("MAX"));
    }

    /**
     * Unlike {@code fromValue}, which refuses the placeholders because no C {@code streq} branch
     * names them, {@code lookup} is a name match and finds NONE and MAX.
     */
    @Test
    void lookup_resolvesPlaceholders_whereFromValueRefuses() {
        assertSame(ObjPropertyType.OBJ_PROPERTY_NONE, ObjPropertyType.lookup("NONE"));
        assertSame(ObjPropertyType.OBJ_PROPERTY_MAX, ObjPropertyType.lookup("MAX"));
        assertNull(ObjPropertyType.fromValue(""));
        assertNull(ObjPropertyType.fromValue("max"));
    }

    /**
     * The data-file tokens are not names: C's {@code type:} words ({@code "stat"}, {@code "resistance"}
     * and so on) must not resolve through {@code lookup}, and names are case-sensitive.
     */
    @Test
    void lookup_tokensAndWrongCase_returnNull() {
        assertNull(ObjPropertyType.lookup("stat"));
        assertNull(ObjPropertyType.lookup("resistance"));
        assertNull(ObjPropertyType.lookup("vulnerability"));
        assertNull(ObjPropertyType.lookup("immunity"));
        assertNull(ObjPropertyType.lookup("Stat"));
        assertNull(ObjPropertyType.lookup("obj_property_stat"));
        assertNull(ObjPropertyType.lookup("OBJ_PROPERTY_stat"));
    }

    /** Anything that is not a constant's name, including near misses, misses quietly. */
    @Test
    void lookup_unknownNames_returnNull() {
        assertNull(ObjPropertyType.lookup(""));
        assertNull(ObjPropertyType.lookup(" "));
        assertNull(ObjPropertyType.lookup("OBJ_PROPERTY_"));
        assertNull(ObjPropertyType.lookup("OBJ_PROPERTY_RESISTANCE"));
        assertNull(ObjPropertyType.lookup("OBJ_PROPERTY_OBJ_PROPERTY_STAT"));
        assertNull(ObjPropertyType.lookup("STAT "));
        assertNull(ObjPropertyType.lookup(" STAT"));
        assertNull(ObjPropertyType.lookup("OBJ_PROPERTY_COUNT"));
    }

    /** A {@code null} name returns {@code null}, matching {@code fromValue(null)}, rather than throwing. */
    @Test
    void lookup_nullName_returnsNull() {
        assertNull(ObjPropertyType.lookup(null));
    }
}
