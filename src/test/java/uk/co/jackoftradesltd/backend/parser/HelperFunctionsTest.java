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
 * Unit tests for {@link HelperFunctions#parseIntString}.
 *
 * <p>The expected values are worked out from C, not from the Java. {@code parse_act_aim},
 * {@code parse_act_level} and {@code parse_act_power} in {@code obj-init.c} take their numbers from
 * {@code parser_getint} and {@code parser_getuint}, which in {@code parser.c} call {@code strtol} or
 * {@code strtoul} with base 0: a leading {@code 0} means octal, anything else decimal, reading
 * stops at the first character that is not a digit of the chosen base, and the only refusal is a
 * token with no digits ({@code PARSE_ERROR_NOT_NUMBER}).
 *
 * <p>Where C wraps a value that is too big for 32 bits, the Java clamps instead; that is the accepted
 * deviation recorded in {@code docs/Accepted_Deviations.md}, so the out-of-range rows expect
 * {@link Integer#MAX_VALUE} and {@link Integer#MIN_VALUE}, not C's wrapped value.
 *
 * <p>The lexer's {@code INTEGER} token only produces an optional {@code -} and ASCII digits, so
 * the octal digit-limit rows ({@code 08}, {@code 019}) are reachable from a data file while the
 * {@code 12abc} and {@code abc} rows have to be injected by hand.
 */
class HelperFunctionsTest {

    private static int parse(String text) {
        return HelperFunctions.parseIntString(text, 7, "activation.txt", "level");
    }

    @Test
    void emptyTextIsAnAbsentDirectiveAndGivesZero() {
        assertEquals(0, parse(""));
    }

    @ParameterizedTest(name = "decimal {0} -> {1}")
    @CsvSource({
            "0, 0",
            "1, 1",
            "5, 5",
            "10, 10",
            "60, 60",
            "-3, -3",
            "-60, -60",
            "2147483647, 2147483647",
            "-2147483648, -2147483648"
    })
    void decimalValuesAreReadAsWritten(String text, int expected) {
        assertEquals(expected, parse(text));
    }

    @ParameterizedTest(name = "octal {0} -> {1}")
    @CsvSource({
            "00, 0",
            "-0, 0",
            "01, 1",
            "07, 7",
            "010, 8",
            "017, 15",
            "-010, -8",
            "0000000000000000000012, 10"
    })
    void aLeadingZeroMeansOctal(String text, int expected) {
        assertEquals(expected, parse(text));
    }

    @ParameterizedTest(name = "octal digit limit {0} -> {1}")
    @CsvSource({
            "08, 0",
            "09, 0",
            "-08, 0",
            "019, 1",
            "0178, 15",
            "0709, 56"
    })
    void octalReadingStopsAtAnEightOrNine(String text, int expected) {
        assertEquals(expected, parse(text));
    }

    @ParameterizedTest(name = "decimal trailing text {0} -> {1}")
    @CsvSource({
            "12abc, 12",
            "9z, 9",
            "-4x, -4"
    })
    void decimalReadingStopsAtTheFirstNonDigit(String text, int expected) {
        assertEquals(expected, parse(text));
    }

    @ParameterizedTest(name = "clamp {0} -> {1}")
    @CsvSource({
            "2147483648, 2147483647",
            "3000000000, 2147483647",
            "4294967296, 2147483647",
            "99999999999999999999999, 2147483647",
            "-2147483649, -2147483648",
            "-3000000000, -2147483648",
            "-99999999999999999999999, -2147483648",
            "020000000000, 2147483647",
            "037777777777, 2147483647",
            "-020000000001, -2147483648"
    })
    void aValueTooBigForAnIntClampsWhereCWraps(String text, int expected) {
        assertEquals(expected, parse(text));
    }

    @Test
    void anOctalValueInsideTheRangeIsNotClamped() {
        // 03000000000 octal = 3 * 8^9 = 402653184, below Integer.MAX_VALUE.
        assertEquals(402653184, parse("03000000000"));
        // 017777777777 octal = 2^31 - 1 exactly.
        assertEquals(Integer.MAX_VALUE, parse("017777777777"));
        // -020000000000 octal = -2^31 exactly.
        assertEquals(Integer.MIN_VALUE, parse("-020000000000"));
    }

    @ParameterizedTest(name = "no digits {0}")
    @CsvSource({
            "abc",
            "-abc",
            "x1"
    })
    void textWithNoLeadingDigitsStopsTheLoad(String text) {
        assertThrows(DataFileParseException.class, () -> parse(text));
    }

    @Test
    void theMessageNamesTheLineFileAndDirective() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parse("abc"));
        String message = e.getMessage();
        assertEquals(true, message.contains("line: 7"), message);
        assertEquals(true, message.contains("activation.txt"), message);
        assertEquals(true, message.contains("level"), message);
    }
}
