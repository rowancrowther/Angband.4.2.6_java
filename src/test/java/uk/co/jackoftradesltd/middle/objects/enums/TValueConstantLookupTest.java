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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the two {@link TValue} lookups the other tval tests leave out: {@link TValue#lookup(String)},
 * which has no C original, and the one place {@link TValue#fromName(String)} differs from
 * {@code tval_find_idx} in {@code obj-tval.c} that no earlier test pins.
 *
 * <p>The {@code lookup} expectations follow its contract (constant name, optional {@code TV_} prefix,
 * exact case), since C has nothing to derive them from. The {@code strtoul} figures were produced by
 * compiling {@code strtoul(s, &pe, 10)} on this 64-bit machine, where {@code unsigned long} is 64 bits:
 * a minus sign negates modulo 2<sup>64</sup>, so {@code "-18446744073709551615"} reads as 1.
 *
 * <p>Class TValueConstantLookupTest coded on 261115, commented in full on 261115.
 *
 * @author Rowan Crowther
 */
class TValueConstantLookupTest {

    /**
     * Every constant is found by its own name, with the prefix and without it.
     *
     * <p>Function everyConstantIsFound coded on 261115, commented in full on 261115.
     */
    @Test
    @DisplayName("every constant is found with or without its TV_ prefix")
    void everyConstantIsFound() {
        for (TValue tval : TValue.values()) {
            assertSame(tval, TValue.lookup(tval.name()), tval.name());
            assertSame(tval, TValue.lookup(tval.name().substring("TV_".length())), tval.name());
        }
    }

    /**
     * The match is on the identifier, not the display name: the two rows where they differ
     * ({@code TV_DIGGING}/{@code "digger"}, {@code TV_DRAG_ARMOR}/{@code "dragon armor"}) answer to the
     * identifier only.
     *
     * <p>Function identifierNotDisplayName coded on 261115, commented in full on 261115.
     */
    @Test
    @DisplayName("the identifier matches, the display name does not")
    void identifierNotDisplayName() {
        assertSame(TValue.TV_DIGGING, TValue.lookup("DIGGING"));
        assertNull(TValue.lookup("DIGGER"));
        assertNull(TValue.lookup("digger"));
        assertSame(TValue.TV_DRAG_ARMOR, TValue.lookup("DRAG_ARMOR"));
        assertNull(TValue.lookup("DRAGON_ARMOR"));
        assertNull(TValue.lookup("dragon armor"));
    }

    /**
     * Nothing is forgiven: case, padding, spaces for underscores, the British spelling and numbers all
     * miss, where {@code fromName} would resolve several of them.
     *
     * <p>Function nothingIsForgiven coded on 261115, commented in full on 261115.
     *
     * @param text a spelling that is not an exact constant name
     */
    @ParameterizedTest
    @ValueSource(strings = {"sword", "Sword", "tv_sword", " SWORD", "SWORD ", "SOFT ARMOR", "soft_armor",
            "SOFT_ARMOUR", "DRAG_ARMOUR", "5", "", "TV_", "TV_TV_SWORD", "NOSUCHTHING"})
    @DisplayName("any other spelling gives null")
    void nothingIsForgiven(String text) {
        assertNull(TValue.lookup(text), "lookup(\"" + text + "\")");
    }

    /**
     * {@code lookup} and {@code fromName} are different questions: a number resolves through
     * {@code fromName} only, and the prefixed identifier through {@code lookup} only.
     *
     * <p>Function differsFromFromName coded on 261115, commented in full on 261115.
     */
    @Test
    @DisplayName("lookup and fromName answer different forms")
    void differsFromFromName() {
        assertSame(TValue.TV_BOW, TValue.fromName("5"));
        assertNull(TValue.lookup("5"));
        assertNull(TValue.fromName("TV_SWORD"));
        assertSame(TValue.TV_SWORD, TValue.lookup("TV_SWORD"));
    }

    /**
     * A {@code null} name is a caller error, not a miss.
     *
     * <p>Function nullIsRefused coded on 261115, commented in full on 261115.
     */
    @Test
    @DisplayName("null throws")
    void nullIsRefused() {
        assertThrows(NullPointerException.class, () -> TValue.lookup(null));
    }

    /**
     * {@code strtoul} negates a minus-signed number modulo 2<sup>64</sup>, so text whose magnitude is
     * between 2<sup>64</sup>&minus;35 and 2<sup>64</sup>&minus;1 comes back as a small positive number and
     * passes C's {@code r < TV_MAX} test: {@code "-18446744073709551615"} is tval 1 and
     * {@code "-18446744073709551581"} is tval 35. The magnitude is outside {@code int}, so Java refuses
     * it. C's neighbours are checked as well: {@code "-18446744073709551580"} reads as 36 and fails, and
     * {@code "-18446744073709551616"} overflows to the largest value and fails. No data file writes a
     * number like this.
     *
     * <p>Function hugeNegativeWrapDiffersFromC coded on 261115, commented in full on 261115.
     */
    @Test
    @DisplayName("a huge negative that wraps into range in C is refused here (differs from C)")
    void hugeNegativeWrapDiffersFromC() {
        assertNull(TValue.fromName("-18446744073709551615"));
        assertEquals(-1, TValue.findIndex("-18446744073709551615"));
        assertNull(TValue.fromName("-18446744073709551581"));
        assertEquals(-1, TValue.findIndex("-18446744073709551581"));

        assertNull(TValue.fromName("-18446744073709551580"));
        assertNull(TValue.fromName("-18446744073709551616"));
    }
}
