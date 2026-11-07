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
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.ParseUtils.Radix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Edge cases of the four {@link ParseUtils#parseIntString} forms that the other ParseUtils tests
 * leave open, written on 2026-11-07 when the Javadoc of the forms was extended.
 *
 * <p>Expected values for the base 0 and fixed-base forms are what {@code strtol} returns in
 * {@code parser_parse()} ({@code parser.c}) and {@code grab_int_range()} ({@code datafile.c}):
 * whitespace is skipped in front of the sign only, a sign followed by a space reads no digit, and
 * a consumed {@code 0} or {@code 0x} prefix is itself the digit. The {@link Radix} form is the
 * documented exception: it does not skip leading whitespace, so the rows for it record the
 * Java behaviour (a throw) where {@code strtol} would read the number. Binary is not a base C
 * reads, so its rows pin the documented Java behaviour.
 */
class ParseUtilsParseIntStringEdgesTest {

    private static int base0(String text) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token");
    }

    private static int withDefault(String text, int def) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", def);
    }

    private static int radix(String text, Radix radix) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", radix);
    }

    @ParameterizedTest
    @CsvSource({"'\t-5',-5", "' +5',5", "'\n\u000B\f\r 7',7", "-0,0", "+0,0", "-0x,0", "-08,0", "' 0x1F',31"})
    void baseZeroSkipsWhitespaceBeforeTheSignAndReadsAConsumedPrefixAsZero(String text, int expected) {
        assertEquals(expected, base0(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"- 5", "+ 5", "-\t5", "- ", "+\n"})
    void baseZeroRefusesWhitespaceAfterTheSign(String text) {
        assertThrows(DataFileParseException.class, () -> base0(text));
    }

    @Test
    void emptyTextIsTheOnlyTextThatGivesTheDefault() {
        assertEquals(42, withDefault("", 42));
        assertEquals(5, withDefault("5", 42));
        assertEquals(8, withDefault("010", 42));
        assertEquals(0, withDefault("08", 42));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", " \t\r\n", "-", "+", "abc"})
    void defaultFormPassesWhitespaceOnlyAndSignOnlyTextOnToBeRefused(String text) {
        assertThrows(DataFileParseException.class, () -> withDefault(text, 42));
    }

    @ParameterizedTest
    @ValueSource(strings = {" 5", "\t-5", "- 5", "+ 5", " ", "\n0x10"})
    void radixDecimalDoesNotSkipWhitespaceWhereStrtolWould(String text) {
        assertThrows(DataFileParseException.class, () -> radix(text, Radix.DECIMAL));
    }

    @ParameterizedTest
    @ValueSource(strings = {" 0x10", "\t0x10"})
    void radixHexDoesNotSkipWhitespace(String text) {
        assertThrows(DataFileParseException.class, () -> radix(text, Radix.HEX));
    }

    @ParameterizedTest
    @ValueSource(strings = {" 010", "\t010"})
    void radixOctalDoesNotSkipWhitespace(String text) {
        assertThrows(DataFileParseException.class, () -> radix(text, Radix.OCTAL));
    }

    @ParameterizedTest
    @CsvSource({"101,5", "-101,-5", "+11,3", "12,1", "10abc,2"})
    void radixBinaryReadsLeadingBinaryDigitsAfterTheDecimalCheck(String text, int expected) {
        assertEquals(expected, radix(text, Radix.BINARY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "9", "-2"})
    void radixBinaryGivesZeroForDecimalDigitsThatAreNotBinary(String text) {
        assertEquals(0, radix(text, Radix.BINARY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "-", "+", "x1"})
    void radixBinaryRefusesWhatTheDecimalCheckRefuses(String text) {
        assertThrows(DataFileParseException.class, () -> radix(text, Radix.BINARY));
    }

    @ParameterizedTest
    @CsvSource({"'\t-010',-10", "' 0x10',0", "'\n08',8", "-0,0"})
    void fixedBaseTenSkipsWhitespaceAndStopsAtThePrefixLetter(String text, int expected) {
        assertEquals(expected, ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", false));
    }

    @ParameterizedTest
    @CsvSource({"'\t-010',-8", "'\n07',7", "-0,0", "08,0"})
    void fixedBaseEightSkipsWhitespaceAndStopsAtANonOctalDigit(String text, int expected) {
        assertEquals(expected, ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"8", "9", "- 7", "-"})
    void fixedBaseEightRefusesTextWithNoOctalDigit(String text) {
        assertThrows(DataFileParseException.class,
                () -> ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", true));
    }
}
