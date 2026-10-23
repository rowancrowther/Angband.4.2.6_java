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
 * Unit tests for all three forms of {@link ParseUtils#parseIntString}: the base 0 reading, the
 * fixed-base reading and the base 0 reading with a default for empty text, and for
 * {@link ParseUtils#minusFound}, the leading-{@code -} test that {@code parser_parse} in
 * {@code parser.c} applies to a {@code uint} token.
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
 * <p>The fixed-base form is the reading {@code grab_int_range} in {@code datafile.c} gives the two
 * numbers of an {@code alloc:} range: {@code strtol} in base 10. Its rows were worked out with
 * {@code strtol(text, &end, 10)}, and the base 8 rows with {@code strtol(text, &end, 8)}.
 *
 * <p>The lexer's {@code INTEGER} token only produces an optional {@code -} and ASCII digits, so
 * the octal digit-limit rows ({@code 08}, {@code 019}) are reachable from a data file while the
 * {@code 12abc} and {@code abc} rows have to be injected by hand.
 */
class ParseUtilsTest {

    private static int parse(String text) {
        return ParseUtils.parseIntString("", text, 7, "activation.txt", "level");
    }

    private static int parseDecimal(String text) {
        return ParseUtils.parseIntString("Artifact", text, 7, "artifact.txt", "Min", false);
    }

    private static int parseOctal(String text) {
        return ParseUtils.parseIntString("Artifact", text, 7, "artifact.txt", "Min", true);
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

    // ---- the fixed-base form --------------------------------------------

    @Test
    void theFixedBaseFormGivesZeroForEmptyTextAndALoneMinus() {
        assertEquals(0, parseDecimal(""));
        // C's strtol finds no digits in "-" and refuses it as PARSE_ERROR_NOT_NUMBER; the lexer never
        // produces it, so the Java answers 0 (see the class block of ParseUtils).
        assertEquals(0, parseDecimal("-"));
    }

    @ParameterizedTest(name = "base 10 {0} -> {1}")
    @CsvSource({
            "0, 0",
            "00, 0",
            "-0, 0",
            "7, 7",
            "08, 8",
            "09, 9",
            "010, 10",
            "017, 17",
            "019, 19",
            "0178, 178",
            "-08, -8",
            "-010, -10",
            "255, 255",
            "12abc, 12",
            "-4x, -4"
    })
    void aLeadingZeroIsStillDecimalInTheFixedBaseForm(String text, int expected) {
        assertEquals(expected, parseDecimal(text));
    }

    @ParameterizedTest(name = "base 10 clamp {0} -> {1}")
    @CsvSource({
            "2147483647, 2147483647",
            "2147483648, 2147483647",
            "99999999999999999999999, 2147483647",
            "-2147483648, -2147483648",
            "-2147483649, -2147483648"
    })
    void theFixedBaseFormClampsAValueTooBigForAnInt(String text, int expected) {
        assertEquals(expected, parseDecimal(text));
    }

    @ParameterizedTest(name = "base 8 {0} -> {1}")
    @CsvSource({
            "10, 8",
            "17, 15",
            "19, 1",
            "-17, -15",
            "0, 0"
    })
    void theOctalSwitchReadsInBase8WhateverTheTextStartsWith(String text, int expected) {
        assertEquals(expected, parseOctal(text));
    }

    @Test
    void aDigitOutsideTheBaseWithNothingBeforeItStopsTheLoad() {
        // strtol("8", &end, 8) consumes nothing, which parser.c would refuse as NOT_NUMBER.
        assertThrows(DataFileParseException.class, () -> parseOctal("8"));
        assertThrows(DataFileParseException.class, () -> parseDecimal("abc"));
    }

    @Test
    void theMessageOpensWithTheRecordKindItWasGiven() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parseDecimal("abc"));
        assertEquals(true, e.getMessage().startsWith("Artifact beginning at line: 7 of file: artifact.txt"),
                e.getMessage());
        assertEquals(true, e.getMessage().contains("Min"), e.getMessage());
    }

    // ---- the form with a default for empty text ----------------------------

    private static int parseWithDefault(String text, int def) {
        return ParseUtils.parseIntString("Dungeon profile", text, 7, "dungeon_profile.txt", "alloc", def);
    }

    @ParameterizedTest(name = "empty text with default {0}")
    @CsvSource({"0", "7", "-1", "2147483647"})
    void emptyTextGivesTheDefault(int def) {
        assertEquals(def, parseWithDefault("", def));
    }

    // Non-empty text is parser_getint's strtol(tok, &z, 0), as the five-argument form reads it; a
    // default of 99 shows it is never used for these.
    @ParameterizedTest(name = "with default, base 0 {0} -> {1}")
    @CsvSource({
            "0, 0",
            "15, 15",
            "-1, -1",
            "200, 200",
            "010, 8",
            "-010, -8",
            "08, 0",
            "019, 1",
            "-0, 0",
            "12abc, 12"
    })
    void nonEmptyTextIsReadInBase0AndIgnoresTheDefault(String text, int expected) {
        assertEquals(expected, parseWithDefault(text, 99));
    }

    @Test
    void aLoneMinusGivesZeroNotTheDefault() {
        // C's strtol finds no digits in "-" and refuses it as PARSE_ERROR_NOT_NUMBER; the lexer never
        // produces it. The text is not empty, so the default is not used and the five-argument form's
        // 0 comes back.
        assertEquals(0, parseWithDefault("-", 99));
    }

    @ParameterizedTest(name = "with default, clamp {0} -> {1}")
    @CsvSource({
            // C's strtol gives 99999999999 in a 64-bit long and the int field keeps the low 32 bits,
            // 1215752191; the accepted deviation clamps instead.
            "99999999999, 2147483647",
            "-99999999999, -2147483648",
            "3000000000, 2147483647"
    })
    void theDefaultFormClampsWhereCWraps(String text, int expected) {
        assertEquals(expected, parseWithDefault(text, 99));
    }

    @ParameterizedTest(name = "with default, no digits {0}")
    @CsvSource({"abc", "-abc", "x1"})
    void theDefaultFormStillStopsTheLoadOnTextWithNoDigits(String text) {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parseWithDefault(text, 99));
        assertEquals(true, e.getMessage().startsWith("Dungeon profile beginning at line: 7 of file: dungeon_profile.txt"),
                e.getMessage());
        assertEquals(true, e.getMessage().contains("alloc"), e.getMessage());
    }

    // minusFound mirrors the "*tok == '-'" half of the PARSE_T_UINT test in parser.c's
    // parser_parse(): only the first byte of the raw token is looked at, never the value.
    @ParameterizedTest(name = "minusFound \"{0}\" -> {1}")
    @CsvSource({
            "-5, true",
            "-0, true",
            "-, true",
            "--5, true",
            "-abc, true",
            "5, false",
            "0, false",
            "010, false",
            "5-, false",
            "+5, false",
            "'−5', false"
    })
    void minusFoundLooksOnlyAtTheFirstCharacter(String text, boolean expected) {
        assertEquals(expected, ParseUtils.minusFound(text));
    }

    @Test
    void minusFoundIsFalseForEmptyText() {
        // An absent optional field: C's *tok would be '\0', never '-'.
        assertEquals(false, ParseUtils.minusFound(""));
    }

    @Test
    void minusFoundDoesNotLookPastALeadingSpace() {
        // strtoul skips the space, but parser.c's *tok == '-' sees ' ' and lets " -5" through.
        assertEquals(false, ParseUtils.minusFound(" -5"));
        assertEquals(false, ParseUtils.minusFound("\t-5"));
    }
}
