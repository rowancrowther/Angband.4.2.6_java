package uk.co.jackoftrades.background.strings.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    private List<ColourType> allValues;

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
    void getColourName() {
        assertAll(
                () -> assertEquals("Dark", dark.getColourName()),
                () -> assertEquals("White", white.getColourName()),
                () -> assertEquals("Slate", slate.getColourName()),
                () -> assertEquals("Orange", orange.getColourName()),
                () -> assertEquals("Red", red.getColourName()),
                () -> assertEquals("Green", green.getColourName()),
                () -> assertEquals("Blue", blue.getColourName()),
                () -> assertEquals("Umber", umber.getColourName()),
                () -> assertEquals("Light Dark", lightDark.getColourName()),
                () -> assertEquals("Light Slate", lightSlate.getColourName()),
                () -> assertEquals("Light Purple", lightPurple.getColourName()),
                () -> assertEquals("Yellow", yellow.getColourName()),
                () -> assertEquals("Light Red", lightRed.getColourName()),
                () -> assertEquals("Light Green", lightGreen.getColourName()),
                () -> assertEquals("Light Blue", lightBlue.getColourName()),
                () -> assertEquals("Light Umber", lightUmber.getColourName()),
                () -> assertEquals("Purple", purple.getColourName()),
                () -> assertEquals("Violet", violet.getColourName()),
                () -> assertEquals("Teal", teal.getColourName()),
                () -> assertEquals("Mud", mud.getColourName()),
                () -> assertEquals("Light Yellow", lightYellow.getColourName()),
                () -> assertEquals("Magenta-Pink", magentaPink.getColourName()),
                () -> assertEquals("Light Teal", lightTeal.getColourName()),
                () -> assertEquals("Light Violet", lightViolet.getColourName()),
                () -> assertEquals("Light Pink", lightPink.getColourName()),
                () -> assertEquals("Mustard", mustard.getColourName()),
                () -> assertEquals("Blue Slate", blueSlate.getColourName()),
                () -> assertEquals("Deep Light Blue", deepLightBlue.getColourName())
        );
    }

    @Test
    void colourAttribute() {
        AttributeColour colour = AttributeColour.COLOUR_WHITE;

        // check that the second value is always white for everything other than COLOUR_TYPE_DARK
        for (ColourType colourType : ColourType.values()) {
            if (colourType == ColourType.COLOUR_TYPE_DARK)
                assertEquals(AttributeColour.COLOUR_DARK, colourType.colourAttribute(1));
            else
                assertEquals(colour, colourType.colourAttribute(1));
        }

        // Now do some at random, total coverage would be too hard
        assertAll(
                () -> assertEquals(AttributeColour.COLOUR_DEEP_L_BLUE, ColourType.COLOUR_TYPE_BLUE_SLATE.colourAttribute(6)),
                () -> assertEquals(AttributeColour.COLOUR_MAGENTA, ColourType.COLOUR_TYPE_LIGHT_PINK.colourAttribute(5)),
                () -> assertEquals(AttributeColour.COLOUR_TEAL, ColourType.COLOUR_TYPE_LIGHT_TEAL.colourAttribute(5)),
                () -> assertEquals(AttributeColour.COLOUR_MUSTARD, ColourType.COLOUR_TYPE_MUD.colourAttribute(4)),
                () -> assertEquals(AttributeColour.COLOUR_SLATE, ColourType.COLOUR_TYPE_PURPLE.colourAttribute(3)),
                () -> assertEquals(AttributeColour.COLOUR_YELLOW, ColourType.COLOUR_TYPE_LIGHT_GREEN.colourAttribute(7))
        );
    }

    @Test
    void getColourType() {
        // Only do a sample of these
        assertAll(
                () -> assertEquals(AttributeColour.COLOUR_WHITE, ColourType.getColourType('w')),
                () -> assertEquals(AttributeColour.COLOUR_L_PURPLE, ColourType.getColourType('P')),
                () -> assertEquals(AttributeColour.COLOUR_BLUE_SLATE, ColourType.getColourType('z')),
                () -> assertEquals(AttributeColour.COLOUR_DEEP_L_BLUE, ColourType.getColourType('Z')),
                () -> assertEquals(AttributeColour.COLOUR_L_RED, ColourType.getColourType('R')),
                () -> assertEquals(AttributeColour.COLOUR_WHITE, ColourType.getColourType('R', 1)),
                () -> assertEquals(AttributeColour.COLOUR_L_RED, ColourType.getColourType('R', 0)),
                () -> assertEquals(AttributeColour.COLOUR_L_GREEN, ColourType.getColourType('g', 4)),
                () -> assertEquals(AttributeColour.COLOUR_UMBER, ColourType.getColourType('u', 8))
        );
    }

    @Test
    void values() {
        for (ColourType colourType : ColourType.values()) {
            if (!allValues.contains(colourType))
                fail("ColourType " + colourType.name() + " not found in manually created array");
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
}