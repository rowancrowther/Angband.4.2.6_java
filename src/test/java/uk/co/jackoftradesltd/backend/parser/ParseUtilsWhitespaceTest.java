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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the whitespace side of {@link ParseUtils}: {@code onlyCSpace}, the {@code cTrim}
 * family, the {@code trimOnlySpaceAndTab} family, and the leading-whitespace skip of the
 * fixed-base {@code parseIntString}.
 *
 * <p>The whitespace set is C's {@code isspace} in the C locale: space, {@code \t}, {@code \n},
 * vertical tab ({@code \u000B}), {@code \f} and {@code \r}. {@code strtol} skips exactly that set
 * before the sign, so the number rows below are the results of {@code strtol(text, &end, 10)}
 * compiled and run from C on 2026-10-28 (a leading run of whitespace is skipped; text of nothing
 * but whitespace, or whitespace between the sign and the digits, reads no digit and is refused).
 * The trimmer rows come from the definition of the set, as C has no function that returns a trimmed
 * copy.
 *
 * @author Rowan Crowther
 */
class ParseUtilsWhitespaceTest {

    private static int base10(String text) {
        return ParseUtils.parseIntString("Artifact", text, 7, "artifact.txt", "Min", false);
    }

    private static int base0(String text) {
        return ParseUtils.parseIntString("Artifact", text, 7, "artifact.txt", "level");
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" is only C whitespace")
    @ValueSource(strings = {"", " ", "\t", "\n", "\u000B", "\f", "\r", " \t\n\u000B\f\r", "\r\n", "          "})
    void emptyAndRunsOfCWhitespaceAreOnlyCSpace(String text) {
        assertTrue(ParseUtils.onlyCSpace(text));
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" holds something else")
    @ValueSource(strings = {"a", "0", "-", " a", "a ", "\na\n", "  ", " ", "\u0000"})
    void anyOtherCharacterAnywhereIsNotOnlyCSpace(String text) {
        assertFalse(ParseUtils.onlyCSpace(text));
    }

    @Test
    void onlyCSpaceOfNullThrows() {
        assertThrows(NullPointerException.class, () -> ParseUtils.onlyCSpace(null));
    }

    @ParameterizedTest(name = "[{index}] cTrim of {0} is {1}")
    @CsvSource(delimiter = '|', quoteCharacter = '`', value = {
            "`x`|`x`",
            "`  x  `|`x`",
            "`\t x \n`|`x`",
            "`\u000B\f\r x \r\f\u000B`|`x`",
            "`a b`|`a b`",
            "`  a b  `|`a b`",
            "`  \n  `|``",
            "``|``"
    })
    void cTrimRemovesCWhitespaceFromBothEnds(String text, String expected) {
        assertEquals(expected, ParseUtils.cTrim(text));
    }

    @Test
    void cTrimFrontLeavesTheRearAndCTrimRearLeavesTheFront() {
        assertEquals("x \t", ParseUtils.cTrimFront(" \t\n x \t"));
        assertEquals(" \t\n x", ParseUtils.cTrimRear(" \t\n x \t"));
    }

    @Test
    void cTrimOfOnlyWhitespaceIsEmptyFromEitherEnd() {
        assertEquals("", ParseUtils.cTrimFront(" \t\n"));
        assertEquals("", ParseUtils.cTrimRear(" \t\n"));
    }

    @Test
    void cTrimsOfNullAreNull() {
        assertNull(ParseUtils.cTrim(null));
        assertNull(ParseUtils.cTrimFront(null));
        assertNull(ParseUtils.cTrimRear(null));
    }

    @ParameterizedTest(name = "[{index}] a character beyond ASCII {0} is not trimmed")
    @ValueSource(strings = {" x ", " x ", "　x　"})
    void cTrimLeavesNonAsciiSpacesAlone(String text) {
        assertEquals(text, ParseUtils.cTrim(text));
    }

    @ParameterizedTest(name = "[{index}] trimOnlySpaceAndTab of {0} is {1}")
    @CsvSource(delimiter = '|', quoteCharacter = '`', value = {
            "`x`|`x`",
            "`  x  `|`x`",
            "`\t x \t`|`x`",
            "`a b`|`a b`",
            "` \t `|``",
            "``|``"
    })
    void trimOnlySpaceAndTabRemovesSpacesAndTabsFromBothEnds(String text, String expected) {
        assertEquals(expected, ParseUtils.trimOnlySpaceAndTab(text));
    }

    @Test
    void trimOnlySpaceAndTabStopsAtOtherWhitespace() {
        // A newline is not in the space-and-tab set, so it stays and shields what is behind it.
        assertEquals("\n x", ParseUtils.trimOnlySpaceAndTab(" \n x"));
        assertEquals("x \n", ParseUtils.trimOnlySpaceAndTab("x \n "));
    }

    @Test
    void trimFrontAndRearOnlySpaceAndTabTakeOneEndEach() {
        assertEquals("x \t", ParseUtils.trimFrontOnlySpaceAndTab(" \t x \t"));
        assertEquals(" \t x", ParseUtils.trimRearOnlySpaceAndTab(" \t x \t"));
        assertEquals("", ParseUtils.trimFrontOnlySpaceAndTab(" \t"));
        assertEquals("", ParseUtils.trimRearOnlySpaceAndTab(" \t"));
    }

    @Test
    void theSpaceAndTabTrimsOfNullAreNull() {
        assertNull(ParseUtils.trimOnlySpaceAndTab(null));
        assertNull(ParseUtils.trimFrontOnlySpaceAndTab(null));
        assertNull(ParseUtils.trimRearOnlySpaceAndTab(null));
    }

    @ParameterizedTest(name = "[{index}] fixed base 10 reads \"{0}\" as {1}")
    @CsvSource(delimiter = '|', quoteCharacter = '`', value = {
            "`\t 5`|5",
            "` \n7`|7",
            "`\u000B\f\r9`|9",
            "`  -5`|-5",
            "`  +5`|5",
            "`\t 010`|10",
            "`5 `|5"
    })
    void fixedBaseSkipsLeadingCWhitespaceAsStrtolDoes(String text, int expected) {
        assertEquals(expected, base10(text));
    }

    @ParameterizedTest(name = "[{index}] fixed base 10 refuses \"{0}\"")
    @ValueSource(strings = {"   ", "\t\n", "- 5", "abc", "+-5", "--5", "x10"})
    void fixedBaseRefusesWhitespaceOnlyAndNoDigits(String text) {
        assertThrows(DataFileParseException.class, () -> base10(text));
    }

    @Test
    void fixedBaseNamesAnEmptyValueForWhitespaceOnlyText() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> base10("  \t"));
        assertTrue(e.getMessage().contains("empty Min value"), e.getMessage());
    }

    @ParameterizedTest(name = "[{index}] base 0 reads \"{0}\" as {1}")
    @CsvSource(delimiter = '|', quoteCharacter = '`', value = {
            "`\t 5`|5",
            "` \n7`|7",
            "`\u000B\f\r 010`|8",
            "`  -0x10`|-16"
    })
    void baseZeroSkipsLeadingCWhitespaceAsStrtolDoes(String text, int expected) {
        assertEquals(expected, base0(text));
    }

    @ParameterizedTest(name = "[{index}] base 0 refuses \"{0}\"")
    @ValueSource(strings = {"   ", "\t\n", "- 5"})
    void baseZeroRefusesWhitespaceOnlyAndWhitespaceAfterTheSign(String text) {
        assertThrows(DataFileParseException.class, () -> base0(text));
    }
}
