package uk.co.jackoftrades.background.colour.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ColourType {
    COLOUR_TYPE_DARK('d', "Dark",
            new AttributeColour[]{AttributeColour.COLOUR_DARK, AttributeColour.COLOUR_DARK, AttributeColour.COLOUR_DARK,
                    AttributeColour.COLOUR_DARK, AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_DARK,
                    AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_DARK}),
    COLOUR_TYPE_WHITE('w', "White",
            new AttributeColour[]{AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_WHITE}),
    COLOUR_TYPE_SLATE('s', "Slate",
            new AttributeColour[]{AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_SLATE}),
    COLOUR_TYPE_ORANGE('o', "Orange",
            new AttributeColour[]{AttributeColour.COLOUR_ORANGE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_ORANGE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_ORANGE}),
    COLOUR_TYPE_RED('r', "Red",
            new AttributeColour[]{AttributeColour.COLOUR_RED, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_RED,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_RED}),
    COLOUR_TYPE_GREEN('g', "Green",
            new AttributeColour[]{AttributeColour.COLOUR_GREEN, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_GREEN,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_GREEN}),
    COLOUR_TYPE_BLUE('b', "Blue",
            new AttributeColour[]{AttributeColour.COLOUR_BLUE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_BLUE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_BLUE}),
    COLOUR_TYPE_UMBER('u', "Umber",
            new AttributeColour[]{AttributeColour.COLOUR_UMBER, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_UMBER,
                    AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_UMBER}),
    COLOUR_TYPE_LIGHT_DARK('D', "Light Dark",
            new AttributeColour[]{AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_DARK}),
    COLOUR_TYPE_LIGHT_SLATE('W', "Light Slate",
            new AttributeColour[]{AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_SLATE}),
    COLOUR_TYPE_LIGHT_PURPLE('P', "Light Purple",
            new AttributeColour[]{AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_PURPLE}),
    COLOUR_TYPE_YELLOW('y', "Yellow",
            new AttributeColour[]{AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_YELLOW, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW}),
    COLOUR_TYPE_LIGHT_RED('R', "Light Red",
            new AttributeColour[]{AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_RED,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_RED,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_RED}),
    COLOUR_TYPE_LIGHT_GREEN('G', "Light Green",
            new AttributeColour[]{AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_GREEN,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_GREEN,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_GREEN}),
    COLOUR_TYPE_LIGHT_BLUE('B', "Light Blue",
            new AttributeColour[]{AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_BLUE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_BLUE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_BLUE}),
    COLOUR_TYPE_LIGHT_UMBER('U', "Light Umber",
            new AttributeColour[]{AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_UMBER,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_UMBER,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_UMBER}),
    COLOUR_TYPE_PURPLE('p', "Purple",
            new AttributeColour[]{AttributeColour.COLOUR_PURPLE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE}),
    COLOUR_TYPE_VIOLET('v', "Violet",
            new AttributeColour[]{AttributeColour.COLOUR_VIOLET, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE}),
    COLOUR_TYPE_TEAL('t', "Teal",
            new AttributeColour[]{AttributeColour.COLOUR_TEAL, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_BLUE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_L_BLUE}),
    COLOUR_TYPE_MUD('m', "Mud",
            new AttributeColour[]{AttributeColour.COLOUR_MUD, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_GREEN,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_UMBER}),
    COLOUR_TYPE_LIGHT_YELLOW('Y', "Light Yellow",
            new AttributeColour[]{AttributeColour.COLOUR_L_YELLOW, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_YELLOW}),
    COLOUR_TYPE_MAGENTA_PINK('i', "Magenta-Pink",
            new AttributeColour[]{AttributeColour.COLOUR_MAGENTA, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_RED,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_RED,
                    AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_L_PURPLE}),
    COLOUR_TYPE_LIGHT_TEAL('T', "Light Teal",
            new AttributeColour[]{AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_BLUE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_TEAL,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_BLUE}),
    COLOUR_TYPE_LIGHT_VIOLET('V', "Light Violet",
            new AttributeColour[]{AttributeColour.COLOUR_L_VIOLET, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_VIOLET,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_BLUE}),
    COLOUR_TYPE_LIGHT_PINK('I', "Light Pink",
            new AttributeColour[]{AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_RED,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_MAGENTA,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_PURPLE}),
    COLOUR_TYPE_MUSTARD('M', "Mustard",
            new AttributeColour[]{AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW}),
    COLOUR_TYPE_BLUE_SLATE('z', "Blue Slate",
            new AttributeColour[]{AttributeColour.COLOUR_BLUE_SLATE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_WHITE}),
    COLOUR_TYPE_DEEP_LIGHT_BLUE('Z', "Deep Light Blue",
            new AttributeColour[]{AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_BLUE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_BLUE_SLATE,
                    AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_L_BLUE});

    private final char colourCharacter;
    private final String colourName;
    private final AttributeColour[] colourTranslate;
    private static final Logger logger = LogManager.getLogger();

    ColourType(char c, String name, AttributeColour[] table) {
        colourCharacter = c;
        colourName = name;
        colourTranslate = table;
    }

    public char getColourCharacter() {
        return colourCharacter;
    }

    public String getColourName() {
        return colourName;
    }

    public AttributeColour colourAttribute(int index) {
        if (index < 0 || index >= colourTranslate.length) {
            String message = "Colour index out of bounds, should be between 0 and " + colourTranslate.length + ", was " + index;
            logger.error(message);
            throw new IndexOutOfBoundsException(message);
        }

        return colourTranslate[index];
    }

    public static AttributeColour getColourType(char ch, int index) {
        for (ColourType c : ColourType.values()) {
            if (c.getColourCharacter() == ch) {
                if (index < 0 || index > 8)
                    index = 0;

                return c.colourAttribute(index);
            }
        }

        return AttributeColour.COLOUR_WHITE;
    }

    public static AttributeColour getColourType(char ch) {
        return getColourType(ch, 0);
    }
}