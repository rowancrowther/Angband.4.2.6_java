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
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The five-argument {@link ParseUtils#parseIntString} against {@code strtol(tok, &z, 0)}, which
 * {@code parser_parse} in {@code parser.c} uses for every {@code int} and {@code uint} field.
 * Expected values are what C's {@code strtol} base 0 returns: an optional sign after any leading
 * whitespace, then {@code 0x} or {@code 0X} for hex, a leading {@code 0} for octal, otherwise
 * decimal, stopping at the first character that is not a digit of the base.
 */
class ParseUtilsBase0Test {

    private static int read(String text) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token");
    }

    @ParameterizedTest
    @CsvSource({"0x10,16", "0X1f,31", "0xff,255", "+0x10,16", "-0x1F,-31", "0x1g,1", "0x0,0"})
    void aHexPrefixIsReadAsHex(String text, int expected) {
        assertEquals(expected, read(text));
    }

    /** strtol reads the "0" and stops at the "x" when no hex digit follows. */
    @ParameterizedTest
    @CsvSource({"0x,0", "0xg,0"})
    void aHexPrefixWithNoDigitsIsZero(String text, int expected) {
        assertEquals(expected, read(text));
    }

    @ParameterizedTest
    @CsvSource({"5,5", "-5,-5", "+5,5", "010,8", "-010,-8", "08,0", "019,1", "0178,15", "12abc,12", "0,0"})
    void decimalAndOctalAreReadAsBeforeAndStopAtTheFirstNonDigit(String text, int expected) {
        assertEquals(expected, read(text));
    }

    @Test
    void leadingWhitespaceIsSkippedAsIsspaceSkipsIt() {
        assertEquals(5, read(" 5"));
        assertEquals(-5, read("\t-5"));
        assertEquals(16, read("  0x10"));
        assertEquals(8, read(" 010"));
    }

    @Test
    void noDigitsStopsTheLoad() {
        assertThrows(DataFileParseException.class, () -> read("abc"));
        assertThrows(DataFileParseException.class, () -> read("+-5"));
        assertThrows(DataFileParseException.class, () -> read("--5"));
    }
}
