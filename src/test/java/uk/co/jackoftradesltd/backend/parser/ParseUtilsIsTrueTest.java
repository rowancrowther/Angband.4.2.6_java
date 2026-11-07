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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ParseUtils#isTrue(String)} against {@code parse_act_aim} in {@code obj-init.c}: the figure
 * is read in {@code parser_parse()} ({@code parser.c}) with {@code strtoul(tok, &z, 0)}, and the
 * callback stores {@code val ? true : false}. Expected values come from running C's
 * {@code strtoul} base 0 on each input, not from the Java: a result with {@code z == tok} is
 * {@code PARSE_ERROR_NOT_NUMBER} and so {@code Optional.empty()}; any other result is true when
 * non-zero and false when zero.
 */
class ParseUtilsIsTrueTest {

    private static final Optional<Boolean> TRUE = Optional.of(true);
    private static final Optional<Boolean> FALSE = Optional.of(false);
    private static final Optional<Boolean> NOT_A_NUMBER = Optional.empty();

    @ParameterizedTest
    @ValueSource(strings = {"5", "1", "5abc", "-5", "+5", " -5", "2147483647", "0x1F", "0X10", "0xA",
            "-0x1", "0x1g", "0xFFFF"})
    void aNonZeroDecimalOrHexFigureIsTrue(String text) {
        assertEquals(TRUE, ParseUtils.isTrue(text));
    }

    /** A leading 0 is octal in strtoul base 0, so these are non-zero. */
    @ParameterizedTest
    @ValueSource(strings = {"01", "07", "012", "0005", "0777", "-01", "+07", "007x"})
    void aLeadingZeroFigureIsReadAsOctalAndIsTrueWhenNonZero(String text) {
        assertEquals(TRUE, ParseUtils.isTrue(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "00", "+0", "-0", "0x0", "0X0", "0x00"})
    void aFigureThatReadsAsZeroIsFalse(String text) {
        assertEquals(FALSE, ParseUtils.isTrue(text));
    }

    /**
     * strtoul reads the leading 0 and stops at the first non-digit of the base, so z != tok: the
     * value is zero and no error is raised. "08" and "09" stop because 8 and 9 are not octal digits.
     */
    @ParameterizedTest
    @ValueSource(strings = {"08", "09", "0abc", "00x1", "0 ", "0\t", "-0abc"})
    void aLeadingZeroThenANonOctalCharacterIsFalseNotAnError(String text) {
        assertEquals(FALSE, ParseUtils.isTrue(text));
    }

    /** strtoul("0x") stops at the "x" with the 0 read, so it is false and not an error. */
    @ParameterizedTest
    @ValueSource(strings = {"0x", "0X", "0xg", "0x ", "-0x", "+0xZ", "+0x0g", "\t-0x"})
    void aHexPrefixWithNoDigitsAfterItIsFalseNotAnError(String text) {
        assertEquals(FALSE, ParseUtils.isTrue(text));
    }

    /** strtoul reads no characters at all, so z == tok and parser_parse() raises NOT_NUMBER. */
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "-", "+", "- 5", "--5", " -", "- "})
    void aFigureWithNoLeadingDigitsIsNotANumber(String text) {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTrue(text));
    }

    /** C has no NULL case; the Java reports it as not a number. */
    @Test
    void nullIsNotANumber() {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTrue(null));
    }

    /** isspace skips space, tab, newline, vertical tab, form feed and carriage return. */
    @Test
    void everyCWhitespaceCharacterIsSkippedBeforeTheFigure() {
        assertEquals(TRUE, ParseUtils.isTrue("  7"));
        assertEquals(TRUE, ParseUtils.isTrue("\u000b\f 7"));
        assertEquals(TRUE, ParseUtils.isTrue("\n\r7"));
        assertEquals(FALSE, ParseUtils.isTrue("\t0"));
        assertEquals(NOT_A_NUMBER, ParseUtils.isTrue("\u000b\f\r\n\t "));
    }

    /**
     * Accepted deviation (261027): C stores the 64-bit strtoul result in a 32-bit uval, so
     * 4294967296 is 0 (false) and 2147483648 is non-zero (true) in C. The Java refuses anything
     * outside the int range. The test pins the Java behaviour so a change to it is noticed.
     */
    @ParameterizedTest
    @ValueSource(strings = {"2147483648", "4294967296", "0x80000000", "020000000000"})
    void aFigureOutsideTheIntRangeIsRefusedWhereCWraps(String text) {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTrue(text));
    }
}
