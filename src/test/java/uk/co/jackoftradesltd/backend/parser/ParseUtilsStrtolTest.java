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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The base 0 and fixed base 10 readings of {@link ParseUtils#parseIntString} against a table of
 * 61 inputs read by C's {@code strtol(text, &end, 0)} and {@code strtol(text, &end, 10)}, the calls
 * behind {@code parser_parse} in {@code parser.c} and {@code grab_int_range} in {@code datafile.c}.
 *
 * <p>The expected values are not taken from the Java: a small C program was compiled with {@code gcc}
 * on 2026-10-28 and run over the inputs on the 64-bit {@code long} of this machine, and its output
 * is what the rows below record. Four groups come out of it. Inputs that C reads inside the range
 * of an {@code int} must give the same value here. Inputs past that range are the accepted
 * deviation in {@code docs/Accepted_Deviations.md} ("An integer too big for its C type is clamped,
 * where C wraps it"), so the row records the clamp and the comment records what C keeps. Inputs
 * with no digit are refused by C ({@code z == tok}), and so here. A lone {@code -} or {@code +} is
 * its own group so that it stays visible: C refuses it, and since 261028 so does every form of
 * {@code parseIntString}.
 *
 * <p>Class ParseUtilsStrtolTest coded on 261028, commented in full on 261028.
 */
class ParseUtilsStrtolTest {

    private static int read(int base, String text) {
        if (base == 0)
            return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token");
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", false);
    }

    private static Stream<Arguments> inRange() {
        return Stream.of(
            Arguments.of(0, "0", 0),
            Arguments.of(0, "-0", 0),
            Arguments.of(0, "+0", 0),
            Arguments.of(0, "5", 5),
            Arguments.of(0, "+5", 5),
            Arguments.of(0, "-5", -5),
            Arguments.of(0, "007", 7),
            Arguments.of(0, "010", 8),
            Arguments.of(0, "08", 0),
            Arguments.of(0, "09", 0),
            Arguments.of(0, "019", 1),
            Arguments.of(0, "017", 15),
            Arguments.of(0, "0x", 0),
            Arguments.of(0, "0X", 0),
            Arguments.of(0, "0xg", 0),
            Arguments.of(0, "0x10", 16),
            Arguments.of(0, "0X1f", 31),
            Arguments.of(0, "+0x10", 16),
            Arguments.of(0, "-0x10", -16),
            Arguments.of(0, "0x0x5", 0),
            Arguments.of(0, "12abc", 12),
            Arguments.of(0, "  5", 5),
            Arguments.of(0, "\t5", 5),
            Arguments.of(0, "\n5", 5),
            Arguments.of(0, "\u000B5", 5),
            Arguments.of(0, "\f5", 5),
            Arguments.of(0, "\r5", 5),
            Arguments.of(0, " \t\n\u000B\f\r7", 7),
            Arguments.of(0, "0 5", 0),
            Arguments.of(0, "  -0x10", -16),
            Arguments.of(0, "  010", 8),
            Arguments.of(0, "5 ", 5),
            Arguments.of(0, "2147483647", 2147483647),
            Arguments.of(0, "-2147483648", -2147483648),
            Arguments.of(0, "1e5", 1),
            Arguments.of(0, "5.5", 5),
            Arguments.of(0, "0b1", 0),
            Arguments.of(0, "00", 0),
            Arguments.of(0, "000", 0),
            Arguments.of(0, "0x00", 0),
            Arguments.of(0, "-08", 0),
            Arguments.of(0, "+08", 0),
            Arguments.of(10, "0", 0),
            Arguments.of(10, "-0", 0),
            Arguments.of(10, "+0", 0),
            Arguments.of(10, "5", 5),
            Arguments.of(10, "+5", 5),
            Arguments.of(10, "-5", -5),
            Arguments.of(10, "007", 7),
            Arguments.of(10, "010", 10),
            Arguments.of(10, "08", 8),
            Arguments.of(10, "09", 9),
            Arguments.of(10, "019", 19),
            Arguments.of(10, "017", 17),
            Arguments.of(10, "0x", 0),
            Arguments.of(10, "0X", 0),
            Arguments.of(10, "0xg", 0),
            Arguments.of(10, "0x10", 0),
            Arguments.of(10, "0X1f", 0),
            Arguments.of(10, "+0x10", 0),
            Arguments.of(10, "-0x10", 0),
            Arguments.of(10, "0x0x5", 0),
            Arguments.of(10, "0xFFFFFFFF", 0),
            Arguments.of(10, "12abc", 12),
            Arguments.of(10, "  5", 5),
            Arguments.of(10, "\t5", 5),
            Arguments.of(10, "\n5", 5),
            Arguments.of(10, "\u000B5", 5),
            Arguments.of(10, "\f5", 5),
            Arguments.of(10, "\r5", 5),
            Arguments.of(10, " \t\n\u000B\f\r7", 7),
            Arguments.of(10, "0 5", 0),
            Arguments.of(10, "  -0x10", 0),
            Arguments.of(10, "  010", 10),
            Arguments.of(10, "5 ", 5),
            Arguments.of(10, "2147483647", 2147483647),
            Arguments.of(10, "-2147483648", -2147483648),
            Arguments.of(10, "0x7fffffffffffffff", 0),
            Arguments.of(10, "1e5", 1),
            Arguments.of(10, "5.5", 5),
            Arguments.of(10, "0b1", 0),
            Arguments.of(10, "00", 0),
            Arguments.of(10, "000", 0),
            Arguments.of(10, "0x00", 0),
            Arguments.of(10, "-08", -8),
            Arguments.of(10, "+08", 8)
        );
    }

    private static Stream<Arguments> pastTheRangeOfAnInt() {
        return Stream.of(
            Arguments.of(0, "0xFFFFFFFF", 2147483647),  // C keeps -1
            Arguments.of(0, "2147483648", 2147483647),  // C keeps -2147483648
            Arguments.of(0, "-2147483649", -2147483648),  // C keeps 2147483647
            Arguments.of(0, "3000000000", 2147483647),  // C keeps -1294967296
            Arguments.of(0, "4294967296", 2147483647),  // C keeps 0
            Arguments.of(0, "99999999999999999999999", 2147483647),  // C keeps -1
            Arguments.of(0, "-99999999999999999999999", -2147483648),  // C keeps 0
            Arguments.of(0, "0777777777777", 2147483647),  // C keeps -1
            Arguments.of(0, "0x7fffffffffffffff", 2147483647),  // C keeps -1
            Arguments.of(10, "2147483648", 2147483647),  // C keeps -2147483648
            Arguments.of(10, "-2147483649", -2147483648),  // C keeps 2147483647
            Arguments.of(10, "3000000000", 2147483647),  // C keeps -1294967296
            Arguments.of(10, "4294967296", 2147483647),  // C keeps 0
            Arguments.of(10, "99999999999999999999999", 2147483647),  // C keeps -1
            Arguments.of(10, "-99999999999999999999999", -2147483648),  // C keeps 0
            Arguments.of(10, "0777777777777", 2147483647)  // C keeps 388697201
        );
    }

    private static Stream<Arguments> noDigit() {
        return Stream.of(
            Arguments.of(0, "abc"),
            Arguments.of(0, "x10"),
            Arguments.of(0, "+-5"),
            Arguments.of(0, "--5"),
            Arguments.of(0, "   "),
            Arguments.of(0, "\t\n"),
            Arguments.of(0, "- 5"),
            Arguments.of(0, "+ 5"),
            Arguments.of(10, "abc"),
            Arguments.of(10, "x10"),
            Arguments.of(10, "+-5"),
            Arguments.of(10, "--5"),
            Arguments.of(10, "   "),
            Arguments.of(10, "\t\n"),
            Arguments.of(10, "- 5"),
            Arguments.of(10, "+ 5")
        );
    }

    private static Stream<Arguments> loneSign() {
        return Stream.of(
            Arguments.of(0, "-"),
            Arguments.of(0, "+"),
            Arguments.of(10, "-"),
            Arguments.of(10, "+")
        );
    }

    @ParameterizedTest(name = "[{index}] base {0} reads \"{1}\" as {2}")
    @MethodSource("inRange")
    void readsInsideTheRangeOfAnIntAsStrtolDoes(int base, String text, int expected) {
        assertEquals(expected, read(base, text));
    }

    @ParameterizedTest(name = "[{index}] base {0} clamps \"{1}\" to {2}")
    @MethodSource("pastTheRangeOfAnInt")
    void clampsWhereCWrapsToThirtyTwoBits(int base, String text, int expected) {
        assertEquals(expected, read(base, text));
    }

    @ParameterizedTest(name = "[{index}] base {0} refuses \"{1}\"")
    @MethodSource("noDigit")
    void refusesWhereStrtolReadsNoDigit(int base, String text) {
        assertThrows(DataFileParseException.class, () -> read(base, text));
    }

    @ParameterizedTest(name = "[{index}] base {0} refuses the lone sign \"{1}\"")
    @MethodSource("loneSign")
    void aLoneSignIsRefusedAsCRefusesIt(int base, String text) {
        assertThrows(DataFileParseException.class, () -> read(base, text));
    }
}
