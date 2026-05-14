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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
 */

package uk.co.jackoftrades.backend.strings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextBlockTest {
    TextBlock block;

    @BeforeEach
    void setUp() {
        block = new TextBlock();
    }

    @Test
    void append() {
        char firstChar = '1';
        AttributeColour att = AttributeColour.COLOUR_DEEP_L_BLUE;

        block.append(firstChar, att);

        String expected = String.valueOf(firstChar);
        assertEquals(expected, block.toString());
    }

    @Test
    void testAppend() {
        AngbandDisplayCharacter adc = new AngbandDisplayCharacter('Q', AttributeColour.COLOUR_L_RED);
        block.append(adc);
        String expected = "Q";
        assertEquals(expected, block.toString());
    }

    @Test
    void testAppend1() {
        TextBlock toAppend = new TextBlock();
        toAppend.append('1', AttributeColour.COLOUR_BLUE);
        toAppend.append('2', AttributeColour.COLOUR_BLUE);
        toAppend.append('3', AttributeColour.COLOUR_BLUE);

        block.append(toAppend);
        String expected = "123";

        assertEquals(expected, block.toString());
    }

    @Test
    void testAppend2() {
        String toFormat = "This %s %s string.";
        String expected = "This is a string.";
        block.append(AttributeColour.COLOUR_WHITE, toFormat, "is", "a");

        assertEquals(expected, block.toString());
    }

    @Test
    void testAppend3() {
        String toFormat = "This %s %s string.";
        String expected = "This is a string.";
        block.append(toFormat, "is", "a");

        assertEquals(expected, block.toString());
    }

    @Test
    void length() {
        for (char c = 'a'; c <= 'z'; c++) {
            block.append(new AngbandDisplayCharacter(c, AttributeColour.COLOUR_WHITE));
        }

        int expected = 26;
        assertEquals(expected, block.length());
    }

    @Test
    void calculateLines() {
        String toAppend = "This is a test of splitting the text into individual lines.";
        int wrapAt = 15;

        block.append(toAppend);

        List<TextBlock> lines = block.calculateLines(wrapAt);

        for (TextBlock b : lines) {
            System.out.println(b.toString());
        }
    }

    @Test
    void testAppend4() {
        String toAppend = "HowILoveThee";
        ArrayList<AttributeColour> colours = new ArrayList<>();

        for (int index = 0; index < toAppend.length(); index++) {
            colours.add(AttributeColour.COLOUR_MUSTARD);
        }

        block.append(toAppend, colours);

        assertEquals(toAppend, block.toString());
    }

    @Test
    void toFile() {
    }

    @Test
    void testToString() {
        for (char c = 'a'; c <= 'z'; c++) {
            block.append(new AngbandDisplayCharacter(c, AttributeColour.COLOUR_WHITE));
        }

        String expected = "abcdefghijklmnopqrstuvwxyz";
        assertEquals(expected, block.toString());
    }
}