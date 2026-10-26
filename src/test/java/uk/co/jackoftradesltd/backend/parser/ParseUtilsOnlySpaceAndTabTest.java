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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ParseUtils#onlySpaceAndTab}, the port of {@code contains_only_spaces} in
 * {@code z-util.c}.
 *
 * <p>C's function is {@code strspn(s, " \t")} followed by a test that the next character is the
 * terminator, so it is true for the empty string and for any run of spaces and tabs, and false as soon
 * as any other character appears, whether first, last or between. The rows below are worked out from
 * that definition, not from the Java: in particular every other whitespace character C's
 * {@code isspace} would accept ({@code \n}, {@code \r}, {@code \u000B}, {@code \f}) is not in the
 * {@code strspn} set, so it makes the answer false.
 *
 * @author Rowan Crowther
 */
class ParseUtilsOnlySpaceAndTabTest {

    @ParameterizedTest(name = "[{index}] \"{0}\" holds only spaces and tabs")
    @ValueSource(strings = {"", " ", "\t", "  ", "\t\t", " \t", "\t ", " \t \t ", "          "})
    void emptyAndRunsOfSpacesAndTabsAreTrue(String text) {
        assertTrue(ParseUtils.onlySpaceAndTab(text));
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" holds something else")
    @ValueSource(strings = {"a", "0", ":", "-", " a", "a ", " a ", "\ta", "a\t", " \t x \t ", "x  ", "  x"})
    void anyOtherCharacterAnywhereIsFalse(String text) {
        assertFalse(ParseUtils.onlySpaceAndTab(text));
    }

    @ParameterizedTest(name = "[{index}] other whitespace {0} is not in strspn's set")
    @ValueSource(strings = {"\n", "\r", "\u000B", "\f", " \n", "\n ", "\t\r", " \u000B ", "\r\n"})
    void whitespaceOtherThanSpaceAndTabIsFalse(String text) {
        assertFalse(ParseUtils.onlySpaceAndTab(text));
    }

    @ParameterizedTest(name = "[{index}] non-ASCII space {0} is not a space")
    @ValueSource(strings = {" ", " ", "　", "  ", "​"})
    void spaceCharactersBeyondAsciiAreFalse(String text) {
        // strspn compares bytes against ' ' and '\t' only; a no-break or ideographic space is not one.
        assertFalse(ParseUtils.onlySpaceAndTab(text));
    }

    @Test
    void aNullArgumentThrowsAsTheCallersGuardAgainstItFirst() {
        assertThrows(NullPointerException.class, () -> ParseUtils.onlySpaceAndTab(null));
    }
}
