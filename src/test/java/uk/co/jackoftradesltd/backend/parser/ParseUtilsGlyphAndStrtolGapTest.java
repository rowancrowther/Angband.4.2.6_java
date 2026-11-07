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
import uk.co.jackoftradesltd.channel.parser.ParseUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Two groups of {@link ParseUtils} cases that the other {@code ParseUtils*Test} classes leave
 * open: the glyph reader {@link ParseUtils#parseADCGlyph(String)}, which had no test, and six
 * {@code strtol} inputs the 61-row table in {@code ParseUtilsStrtolTest} does not hold.
 *
 * <p>The {@code strtol} rows were read from a small C program compiled with {@code gcc} on
 * 2026-11-07 and run over each text with {@code strtol(text, &end, 0)} and
 * {@code strtol(text, &end, 10)}, the calls behind {@code parser_parse()} in {@code parser.c} and
 * {@code grab_int_range()} in {@code datafile.c}. The expected values are what it printed, not what
 * the Java gives: {@code 00x10} is octal {@code 00} then a stop at the {@code x}, so 0; {@code 0x-5}
 * reads the {@code 0} and stops at the {@code x}, so 0; {@code 0x1G} reads {@code 0x1}, so 1;
 * {@code -007} is octal 7 negated; {@code 0x7fffffff} is the largest {@code int} and base 10 stops
 * at the {@code x}.
 *
 * <p>The glyph rows follow C's {@code PARSE_T_CHAR} reading in {@code parser_parse()}, which takes
 * one character of the token as a {@code wchar_t} ({@code text_mbstowcs} with a count of one) and
 * fails the line with {@code PARSE_ERROR_FIELD_TOO_LONG} if more than one follows. Java splits
 * that value in two, so a code point at or below {@code U+FFFF} must come back in
 * {@code glyphChar} and one above it in {@code wideGlyphChar}, and text of two or more code points
 * gives {@code null}. Empty and {@code null} text have no C case (C refuses a missing field first)
 * and give {@code null} here too.
 *
 * <p>Class ParseUtilsGlyphAndStrtolGapTest coded on 261107, commented in full on 261107 (the
 * more-than-one-code-point row corrected the same day from "first code point taken" to "refused").
 */
class ParseUtilsGlyphAndStrtolGapTest {

    private static int base0(String text) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token");
    }

    private static int base10(String text) {
        return ParseUtils.parseIntString("Test", text, 1, "test.txt", "token", false);
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" reads as {1} in base 0 and {2} in base 10")
    @CsvSource({
        "00x10,      0,          0",
        "0x-5,       0,          0",
        "0x1G,       1,          0",
        "-007,      -7,         -7",
        "0x7fffffff, 2147483647, 0",
        "-0x80000000, -2147483648, 0"
    })
    void strtolRowsNotInTheMainTable(String text, int base0Expected, int base10Expected) {
        assertEquals(base0Expected, base0(text));
        assertEquals(base10Expected, base10(text));
    }

    @ParameterizedTest(name = "[{index}] \"{0}\" is a basic-plane glyph")
    @ValueSource(strings = {"#", "e", "é", "·", "中", "￿"})
    void aBasicPlaneGlyphComesBackInTheCharHalf(String glyph) {
        ParseUtils.GlyphCharacter result = ParseUtils.parseADCGlyph(glyph);

        assertEquals(glyph.charAt(0), result.glyphChar());
        assertEquals(0, result.wideGlyphChar());
    }

    @ParameterizedTest(name = "[{index}] code point {0} is a supplementary glyph")
    @ValueSource(ints = {0x10000, 0x1F600, 0x10FFFF})
    void aSupplementaryGlyphComesBackInTheWideHalf(int codePoint) {
        ParseUtils.GlyphCharacter result = ParseUtils.parseADCGlyph(new String(Character.toChars(codePoint)));

        assertEquals('\0', result.glyphChar());
        assertEquals(codePoint, result.wideGlyphChar());
    }

    /**
     * C's {@code parser_parse()} skips one code point with {@code utf8_fskip} and fails the line
     * with {@code PARSE_ERROR_FIELD_TOO_LONG} when anything but a {@code :} follows. The glyph
     * token is given here without a colon, so every text of two or more code points is refused.
     * {@code "e\u0301"} is two code points (a combining mark starts a new one in
     * {@code utf8_fskip}, which only passes over {@code 10xxxxxx} continuation bytes, and
     * U+0301 is encoded {@code CC 81}), as is a supplementary glyph followed by a letter.
     */
    @ParameterizedTest(name = "[{index}] more than one code point is refused")
    @ValueSource(strings = {"ab", "e\u0301", "\uD83D\uDE00x", "x\uD83D\uDE00", "\uD83D\uDE00\uD83D\uDE00", "  "})
    void moreThanOneCodePointIsRefused(String glyph) {
        assertNull(ParseUtils.parseADCGlyph(glyph));
    }

    @Test
    void aSpaceIsAGlyphLikeAnyOther() {
        ParseUtils.GlyphCharacter result = ParseUtils.parseADCGlyph(" ");

        assertEquals(' ', result.glyphChar());
        assertEquals(0, result.wideGlyphChar());
    }

    @Test
    void nullAndEmptyTextGiveNull() {
        assertNull(ParseUtils.parseADCGlyph(null));
        assertNull(ParseUtils.parseADCGlyph(""));
    }
}
