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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ParseUtils#isTextNumber(String)} against {@code lookup_sval} in {@code obj-util.c}.
 * C reads the sval with {@code strtoul} in base 10. If any digits were read, the text is a number
 * only when the rest is spaces and tabs and the value is below {@code INT_MAX}; otherwise it is
 * refused with no name lookup. With no digits it is a name. {@code strtoul} wraps a minus past
 * {@code INT_MAX}, so only {@code -0} survives a minus. Expected values come from that rule, not
 * from the Java: the answer is the number, or -1 where C does not read a number.
 */
class ParseUtilsTextNumberTest {

    private static final int NOT_A_NUMBER = -1;

    /** The figure is returned as written, whatever surrounds it. */
    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', value = {
            "0|0", "1|1", "99|99", "007|7", "2147483646|2147483646",
            "+99|99", "+0|0", "-0|0",
            "\" 99\"|99", "\"99 \"|99", "\"99\t\"|99", "\"\t99\"|99", "\" -0 \"|0", "\"  +7\t \"|7"})
    void aWholeNumberBelowIntMaxIsReadAsItself(String text, int expected) {
        assertEquals(expected, ParseUtils.isTextNumber(text));
    }

    /** A minus passes only on zero, because strtoul wraps it past INT_MAX. */
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-5", "-9", "-2147483646", "--5", "- 5"})
    void aNegativeNumberIsNotANumber(String text) {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTextNumber(text));
    }

    /** INT_MAX itself is refused, and so is anything larger, including past a long. */
    @ParameterizedTest
    @ValueSource(strings = {"2147483647", "2147483648", "99999999999", "9999999999999999999999",
            "+2147483647"})
    void aNumberAtOrAboveIntMaxIsNotANumber(String text) {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTextNumber(text));
    }

    /** Anything but a space or a tab after the digits, or a second sign, ends the number. */
    @ParameterizedTest
    @ValueSource(strings = {"12abc", "0x10", "99 x", "99x", "9 9", "1.5", "99\n", "+-5", "-+5", "++5"})
    void textAfterTheDigitsOrASecondSignIsNotANumber(String text) {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTextNumber(text));
    }

    /** No digits at all: a name, a bare sign, or nothing. */
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "+", "-", "Redo", "Phial", "-x", "abc12"})
    void textWithNoLeadingDigitsIsNotANumber(String text) {
        assertEquals(NOT_A_NUMBER, ParseUtils.isTextNumber(text));
    }
}
