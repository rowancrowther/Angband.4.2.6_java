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
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils.Radix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The {@link Radix} overload of {@link ParseUtils#parseIntString} against the base 0 reading of
 * {@code strtol(tok, &z, 0)} in {@code parser_parse} in {@code parser.c}, split at the prefix: the
 * caller picks HEX for {@code 0x}, OCTAL for a leading {@code 0} and DECIMAL otherwise, and the
 * overload strips the prefix and reads the digits. Expected values are what {@code strtol} returns
 * for the whole text: a consumed prefix is itself the digit {@code 0}, text with no digit at all
 * stops the load.
 */
class ParseUtilsRadixTest {

    private static int read(String text, Radix radix) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", radix);
    }

    @ParameterizedTest
    @CsvSource({"0x10,16", "0X1f,31", "0xff,255", "+0x10,16", "-0x1F,-31", "0x1g,1", "0x0,0"})
    void hexReadsTheDigitsAfterTheZeroX(String text, int expected) {
        assertEquals(expected, read(text, Radix.HEX));
    }

    /** strtol reads the "0" and stops at the "x" when no hex digit follows. */
    @ParameterizedTest
    @CsvSource({"0x,0", "0xg,0", "0X,0", "+0x,0", "-0xg,0"})
    void hexWithNoDigitsAfterThePrefixIsZero(String text, int expected) {
        assertEquals(expected, read(text, Radix.HEX));
    }

    @ParameterizedTest
    @CsvSource({"010,8", "0,0", "00,0", "+010,8", "-017,-15", "08,0", "019,1", "0178,15", "0777,511"})
    void octalReadsTheDigitsAfterTheLeadingZero(String text, int expected) {
        assertEquals(expected, read(text, Radix.OCTAL));
    }

    @ParameterizedTest
    @CsvSource({"5,5", "12abc,12", "+5,5", "-5,-5", "99,99", "0,0"})
    void decimalReadsTheLeadingDigits(String text, int expected) {
        assertEquals(expected, read(text, Radix.DECIMAL));
    }

    @ParameterizedTest
    @CsvSource({"99999999999,2147483647", "-99999999999,-2147483648"})
    void decimalClampsWhereCWraps(String text, int expected) {
        assertEquals(expected, read(text, Radix.DECIMAL));
    }

    @Test
    void emptyTextGivesZeroInEveryBase() {
        assertEquals(0, read("", Radix.HEX));
        assertEquals(0, read("", Radix.OCTAL));
        assertEquals(0, read("", Radix.DECIMAL));
    }

    /** strtol finds no digit after a lone sign, so PARSE_ERROR_NOT_NUMBER stops the load. */
    @ParameterizedTest
    @ValueSource(strings = {"+", "-"})
    void aLoneSignStopsTheLoadInEveryBase(String text) {
        assertThrows(DataFileParseException.class, () -> read(text, Radix.HEX));
        assertThrows(DataFileParseException.class, () -> read(text, Radix.OCTAL));
        assertThrows(DataFileParseException.class, () -> read(text, Radix.DECIMAL));
    }

    /** strtol finds no digit in any of these, so PARSE_ERROR_NOT_NUMBER stops the load. */
    @ParameterizedTest
    @ValueSource(strings = {"abc", "x10", "+-5", "--5", "+x", " 5"})
    void decimalWithNoDigitStopsTheLoad(String text) {
        assertThrows(DataFileParseException.class, () -> read(text, Radix.DECIMAL));
    }

    /**
     * The contract of the overload, not a C reading: the caller picks HEX or OCTAL from the prefix, so
     * text without it is refused. A literal base 16 or base 8 strtol would read "10" as 16 or 8, and
     * would refuse "g" and "8".
     */
    @ParameterizedTest
    @ValueSource(strings = {"10", "1f", "g", "x10", "+10"})
    void hexWithoutTheZeroXIsRefused(String text) {
        assertThrows(DataFileParseException.class, () -> read(text, Radix.HEX));
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "8", "7", "+10", "x"})
    void octalWithoutTheLeadingZeroIsRefused(String text) {
        assertThrows(DataFileParseException.class, () -> read(text, Radix.OCTAL));
    }
}
