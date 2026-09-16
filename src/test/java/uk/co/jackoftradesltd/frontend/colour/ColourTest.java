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

package uk.co.jackoftradesltd.frontend.colour;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.awt.Color;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link Colour}, the port of the C original's
 * {@code angband_color_table} ({@code src/z-color.c}).
 *
 * <p>The expected RGB triples below are transcribed straight from the hex
 * literals in {@code angband_color_table}, not read back out of
 * {@link Colour}, so a transcription slip in the Java table would fail these
 * rather than agree with itself.
 *
 * @author Rowan Crowther
 */
class ColourTest {

    @BeforeAll
    static void setUp() {
        Colour.init();
    }

    /**
     * Every row of {@code angband_color_table}, index for index, as
     * {@code {0x00, r, g, b}} in the C source.
     */
    static Stream<Arguments> colourRgbTriples() {
        return Stream.of(
                Arguments.of(ColourEnum.COLOUR_DARK, 0x00, 0x00, 0x00),
                Arguments.of(ColourEnum.COLOUR_WHITE, 0xff, 0xff, 0xff),
                Arguments.of(ColourEnum.COLOUR_SLATE, 0x80, 0x80, 0x80),
                Arguments.of(ColourEnum.COLOUR_ORANGE, 0xff, 0x80, 0x00),
                Arguments.of(ColourEnum.COLOUR_RED, 0xc0, 0x00, 0x00),
                Arguments.of(ColourEnum.COLOUR_GREEN, 0x00, 0x80, 0x40),
                Arguments.of(ColourEnum.COLOUR_BLUE, 0x00, 0x40, 0xff),
                Arguments.of(ColourEnum.COLOUR_UMBER, 0x80, 0x40, 0x00),
                Arguments.of(ColourEnum.COLOUR_LIGHT_DARK, 0x60, 0x60, 0x60),
                Arguments.of(ColourEnum.COLOUR_LIGHT_SLATE, 0xc0, 0xc0, 0xc0),
                Arguments.of(ColourEnum.COLOUR_LIGHT_PURPLE, 0xff, 0x00, 0xff),
                Arguments.of(ColourEnum.COLOUR_YELLOW, 0xff, 0xff, 0x00),
                Arguments.of(ColourEnum.COLOUR_LIGHT_RED, 0xff, 0x40, 0x40),
                Arguments.of(ColourEnum.COLOUR_LIGHT_GREEN, 0x00, 0xff, 0x00),
                Arguments.of(ColourEnum.COLOUR_LIGHT_BLUE, 0x00, 0xff, 0xff),
                Arguments.of(ColourEnum.COLOUR_LIGHT_UMBER, 0xc0, 0x80, 0x40),
                Arguments.of(ColourEnum.COLOUR_PURPLE, 0x90, 0x00, 0x90),
                Arguments.of(ColourEnum.COLOUR_VIOLET, 0x90, 0x20, 0xff),
                Arguments.of(ColourEnum.COLOUR_TEAL, 0x00, 0xa0, 0xa0),
                Arguments.of(ColourEnum.COLOUR_MUD, 0x6c, 0x6c, 0x30),
                Arguments.of(ColourEnum.COLOUR_LIGHT_YELLOW, 0xff, 0xff, 0x90),
                Arguments.of(ColourEnum.COLOUR_MAGENTA_PINK, 0xff, 0x00, 0xa0),
                Arguments.of(ColourEnum.COLOUR_LIGHT_TEAL, 0x20, 0xff, 0xdc),
                Arguments.of(ColourEnum.COLOUR_LIGHT_VIOLET, 0xb8, 0xa8, 0xff),
                Arguments.of(ColourEnum.COLOUR_LIGHT_PINK, 0xff, 0x80, 0x80),
                Arguments.of(ColourEnum.COLOUR_MUSTARD, 0xb4, 0xb4, 0x00),
                Arguments.of(ColourEnum.COLOUR_BLUE_SLATE, 0xa0, 0xc0, 0xd0),
                Arguments.of(ColourEnum.COLOUR_DEEP_LIGHT_BLUE, 0x00, 0xb0, 0xff),
                Arguments.of(ColourEnum.COLOUR_SHADE, 0x28, 0x28, 0x28));
    }

    @ParameterizedTest
    @MethodSource("colourRgbTriples")
    void everyBasicColourMatchesItsCRow(ColourEnum colour, int r, int g, int b) {
        assertEquals(new Color(r, g, b), Colour.getColour(colour));
    }

    @Test
    void theFirstRowIsBlack() {
        // Row 0 in angband_color_table, COLOUR_DARK - the boundary at the start
        // of the table.
        assertEquals(new Color(0, 0, 0), Colour.getColour(ColourEnum.COLOUR_DARK));
    }

    @Test
    void theLastNamedRowIsShade() {
        // Row 28, the last of the 29 rows angband_color_table actually names;
        // the boundary at the end of Colour#basicColours.
        assertEquals(new Color(0x28, 0x28, 0x28), Colour.getColour(ColourEnum.COLOUR_SHADE));
    }

    @Test
    void reinitialisingLeavesEveryColourUnchanged() {
        // init() has no C equivalent to diverge from - C's array is populated
        // once, at compile time - so the only risk on the Java side is a
        // repeated call somehow perturbing state it has already set.
        Colour.init();
        Colour.init();

        assertEquals(new Color(0xc0, 0x00, 0x00), Colour.getColour(ColourEnum.COLOUR_RED));
        assertEquals(new Color(0x28, 0x28, 0x28), Colour.getColour(ColourEnum.COLOUR_SHADE));
    }
}
