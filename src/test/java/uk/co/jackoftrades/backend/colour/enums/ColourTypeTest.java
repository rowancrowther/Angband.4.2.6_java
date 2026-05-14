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

package uk.co.jackoftrades.backend.colour.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;
import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ColourTypeTest {
    private final ColourType dark = ColourType.COLOUR_TYPE_DARK;
    private final ColourType white = ColourType.COLOUR_TYPE_WHITE;
    private final ColourType slate = ColourType.COLOUR_TYPE_SLATE;
    private final ColourType orange = ColourType.COLOUR_TYPE_ORANGE;
    private final ColourType red = ColourType.COLOUR_TYPE_RED;
    private final ColourType green = ColourType.COLOUR_TYPE_GREEN;
    private final ColourType blue = ColourType.COLOUR_TYPE_BLUE;
    private final ColourType umber = ColourType.COLOUR_TYPE_UMBER;
    private final ColourType lightDark = ColourType.COLOUR_TYPE_LIGHT_DARK;
    private final ColourType lightSlate = ColourType.COLOUR_TYPE_LIGHT_SLATE;
    private final ColourType lightPurple = ColourType.COLOUR_TYPE_LIGHT_PURPLE;
    private final ColourType yellow = ColourType.COLOUR_TYPE_YELLOW;
    private final ColourType lightRed = ColourType.COLOUR_TYPE_LIGHT_RED;
    private final ColourType lightGreen = ColourType.COLOUR_TYPE_LIGHT_GREEN;
    private final ColourType lightBlue = ColourType.COLOUR_TYPE_LIGHT_BLUE;
    private final ColourType lightUmber = ColourType.COLOUR_TYPE_LIGHT_UMBER;
    private final ColourType purple = ColourType.COLOUR_TYPE_PURPLE;
    private final ColourType violet = ColourType.COLOUR_TYPE_VIOLET;
    private final ColourType teal = ColourType.COLOUR_TYPE_TEAL;
    private final ColourType mud = ColourType.COLOUR_TYPE_MUD;
    private final ColourType lightYellow = ColourType.COLOUR_TYPE_LIGHT_YELLOW;
    private final ColourType magentaPink = ColourType.COLOUR_TYPE_MAGENTA_PINK;
    private final ColourType lightTeal = ColourType.COLOUR_TYPE_LIGHT_TEAL;
    private final ColourType lightViolet = ColourType.COLOUR_TYPE_LIGHT_VIOLET;
    private final ColourType lightPink = ColourType.COLOUR_TYPE_LIGHT_PINK;
    private final ColourType mustard = ColourType.COLOUR_TYPE_MUSTARD;
    private final ColourType blueSlate = ColourType.COLOUR_TYPE_BLUE_SLATE;
    private final ColourType deepLightBlue = ColourType.COLOUR_TYPE_DEEP_LIGHT_BLUE;

    private ArrayList<ColourType> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();

        allValues.add(dark);
        allValues.add(white);
        allValues.add(slate);
        allValues.add(orange);
        allValues.add(red);
        allValues.add(green);
        allValues.add(blue);
        allValues.add(umber);
        allValues.add(lightDark);
        allValues.add(lightSlate);
        allValues.add(lightPurple);
        allValues.add(yellow);
        allValues.add(lightRed);
        allValues.add(lightGreen);
        allValues.add(lightBlue);
        allValues.add(lightUmber);
        allValues.add(purple);
        allValues.add(violet);
        allValues.add(teal);
        allValues.add(mud);
        allValues.add(lightYellow);
        allValues.add(magentaPink);
        allValues.add(lightTeal);
        allValues.add(lightViolet);
        allValues.add(lightPink);
        allValues.add(mustard);
        allValues.add(blueSlate);
        allValues.add(deepLightBlue);
    }

    @Test
    void getColourCharacter() {
        assertAll(
                () -> assertEquals('d', dark.getColourCharacter()),
                () -> assertEquals('w', white.getColourCharacter()),
                () -> assertEquals('s', slate.getColourCharacter()),
                () -> assertEquals('o', orange.getColourCharacter()),
                () -> assertEquals('r', red.getColourCharacter()),
                () -> assertEquals('g', green.getColourCharacter()),
                () -> assertEquals('b', blue.getColourCharacter()),
                () -> assertEquals('u', umber.getColourCharacter()),
                () -> assertEquals('D', lightDark.getColourCharacter()),
                () -> assertEquals('W', lightSlate.getColourCharacter()),
                () -> assertEquals('P', lightPurple.getColourCharacter()),
                () -> assertEquals('y', yellow.getColourCharacter()),
                () -> assertEquals('R', lightRed.getColourCharacter()),
                () -> assertEquals('G', lightGreen.getColourCharacter()),
                () -> assertEquals('B', lightBlue.getColourCharacter()),
                () -> assertEquals('U', lightUmber.getColourCharacter()),
                () -> assertEquals('p', purple.getColourCharacter()),
                () -> assertEquals('v', violet.getColourCharacter()),
                () -> assertEquals('t', teal.getColourCharacter()),
                () -> assertEquals('m', mud.getColourCharacter()),
                () -> assertEquals('Y', lightYellow.getColourCharacter()),
                () -> assertEquals('i', magentaPink.getColourCharacter()),
                () -> assertEquals('T', lightTeal.getColourCharacter()),
                () -> assertEquals('V', lightViolet.getColourCharacter()),
                () -> assertEquals('I', lightPink.getColourCharacter()),
                () -> assertEquals('M', mustard.getColourCharacter()),
                () -> assertEquals('z', blueSlate.getColourCharacter()),
                () -> assertEquals('Z', deepLightBlue.getColourCharacter())
        );
    }

    @Test
    void colourAttribute() {
        assertAll(
                () -> assertEquals(AttributeColour.COLOUR_L_DARK, dark.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_BLUE, white.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_WHITE, slate.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, orange.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_RED, red.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_GREEN, green.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_BLUE, blue.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_UMBER, umber.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_SLATE, lightDark.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, lightSlate.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightPurple.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, yellow.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightRed.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightGreen.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightBlue.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightUmber.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_PURPLE, purple.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_PURPLE, violet.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_TEAL, teal.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_MUSTARD, mud.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, lightYellow.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_PINK, magentaPink.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightTeal.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightViolet.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, lightPink.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, mustard.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_DEEP_L_BLUE, blueSlate.colourAttribute(ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_BLUE, deepLightBlue.colourAttribute(ColourTranslation.ATTR_HIGH))
        );
    }

    @Test
    void getAttributeColour() {
        assertAll(
                () -> assertEquals(AttributeColour.COLOUR_DARK, ColourType.getAttributeColour('d')),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, ColourType.getAttributeColour('w', ColourTranslation.ATTR_MONO)),
                () -> assertEquals(AttributeColour.COLOUR_SLATE, ColourType.getAttributeColour('s', ColourTranslation.ATTR_BLIND)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, ColourType.getAttributeColour('o', ColourTranslation.ATTR_LIGHT)),
                () -> assertEquals(AttributeColour.COLOUR_L_RED, ColourType.getAttributeColour('r', ColourTranslation.ATTR_LIGHT)),
                () -> assertEquals(AttributeColour.COLOUR_SLATE, ColourType.getAttributeColour('g', ColourTranslation.ATTR_DARK)),
                () -> assertEquals(AttributeColour.COLOUR_L_BLUE, ColourType.getAttributeColour('b', ColourTranslation.ATTR_HIGH)),
                () -> assertEquals(AttributeColour.COLOUR_L_UMBER, ColourType.getAttributeColour('u', ColourTranslation.ATTR_METAL)),
                () -> assertEquals(AttributeColour.COLOUR_L_DARK, ColourType.getAttributeColour('D', ColourTranslation.ATTR_MISC)),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, ColourType.getAttributeColour('X')),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, ColourType.getAttributeColour('X', ColourTranslation.ATTR_METAL)),
                () -> assertEquals(AttributeColour.COLOUR_UMBER, ColourType.getAttributeColour('u', ColourTranslation.ATTR_MAX))
        );
    }

    @Test
    void values() {
        for (ColourType colourType : ColourType.values()) {
            if (!allValues.contains(colourType))
                fail("Colour type " + colourType.getColourName() + " not found in manually created list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(dark, ColourType.valueOf("COLOUR_TYPE_DARK")),
                () -> assertEquals(white, ColourType.valueOf("COLOUR_TYPE_WHITE")),
                () -> assertEquals(slate, ColourType.valueOf("COLOUR_TYPE_SLATE")),
                () -> assertEquals(orange, ColourType.valueOf("COLOUR_TYPE_ORANGE")),
                () -> assertEquals(red, ColourType.valueOf("COLOUR_TYPE_RED")),
                () -> assertEquals(green, ColourType.valueOf("COLOUR_TYPE_GREEN")),
                () -> assertEquals(blue, ColourType.valueOf("COLOUR_TYPE_BLUE")),
                () -> assertEquals(umber, ColourType.valueOf("COLOUR_TYPE_UMBER")),
                () -> assertEquals(lightDark, ColourType.valueOf("COLOUR_TYPE_LIGHT_DARK")),
                () -> assertEquals(lightSlate, ColourType.valueOf("COLOUR_TYPE_LIGHT_SLATE")),
                () -> assertEquals(lightPurple, ColourType.valueOf("COLOUR_TYPE_LIGHT_PURPLE")),
                () -> assertEquals(yellow, ColourType.valueOf("COLOUR_TYPE_YELLOW")),
                () -> assertEquals(lightRed, ColourType.valueOf("COLOUR_TYPE_LIGHT_RED")),
                () -> assertEquals(lightGreen, ColourType.valueOf("COLOUR_TYPE_LIGHT_GREEN")),
                () -> assertEquals(lightBlue, ColourType.valueOf("COLOUR_TYPE_LIGHT_BLUE")),
                () -> assertEquals(lightUmber, ColourType.valueOf("COLOUR_TYPE_LIGHT_UMBER")),
                () -> assertEquals(purple, ColourType.valueOf("COLOUR_TYPE_PURPLE")),
                () -> assertEquals(violet, ColourType.valueOf("COLOUR_TYPE_VIOLET")),
                () -> assertEquals(teal, ColourType.valueOf("COLOUR_TYPE_TEAL")),
                () -> assertEquals(mud, ColourType.valueOf("COLOUR_TYPE_MUD")),
                () -> assertEquals(lightYellow, ColourType.valueOf("COLOUR_TYPE_LIGHT_YELLOW")),
                () -> assertEquals(magentaPink, ColourType.valueOf("COLOUR_TYPE_MAGENTA_PINK")),
                () -> assertEquals(lightTeal, ColourType.valueOf("COLOUR_TYPE_LIGHT_TEAL")),
                () -> assertEquals(lightViolet, ColourType.valueOf("COLOUR_TYPE_LIGHT_VIOLET")),
                () -> assertEquals(lightPink, ColourType.valueOf("COLOUR_TYPE_LIGHT_PINK")),
                () -> assertEquals(mustard, ColourType.valueOf("COLOUR_TYPE_MUSTARD")),
                () -> assertEquals(blueSlate, ColourType.valueOf("COLOUR_TYPE_BLUE_SLATE")),
                () -> assertEquals(deepLightBlue, ColourType.valueOf("COLOUR_TYPE_DEEP_LIGHT_BLUE"))
        );
    }

    @Test
    void attributeToString() {
        AttributeColour dark = AttributeColour.COLOUR_DARK;
        AttributeColour mud = AttributeColour.COLOUR_MUD;
        AttributeColour deepLightBlue = AttributeColour.COLOUR_DEEP_L_BLUE;

        assertAll(
                () -> assertEquals("Dark", ColourType.attributeToString(dark)),
                () -> assertEquals("Mud", ColourType.attributeToString(mud)),
                () -> assertEquals("Deep Light Blue", ColourType.attributeToString(deepLightBlue))
        );
    }

    @Test
    void getColourType() {
        ColourTranslation translation = ColourTranslation.ATTR_METAL;
        AttributeColour colour = AttributeColour.COLOUR_GREEN;
        int numTranslations = 3;

        ColourType result = ColourType.getColourType(colour, translation, numTranslations);
        ColourType expected = ColourType.COLOUR_TYPE_WHITE;

        assertEquals(expected, result);
    }
}