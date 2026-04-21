package uk.co.jackoftrades.background.colour.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

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

    /**
     * Gets the colour character for this ColourType
     *
     * @return a character representing this colour
     */
    @Contract(pure = true)
    public char getColourCharacter() {
        return colourCharacter;
    }

    /**
     * Gets the colour name for this ColourType
     *
     * @return the colour name for this colour
     */
    @Contract(pure = true)
    public String getColourName() {
        return colourName;
    }

    public AttributeColour colourAttribute(ColourTranslation index) {
        if (index == ColourTranslation.ATTR_MAX) {
            String message = "Colour index out of bounds, should be between 0 and " +
                    ColourTranslation.ATTR_MAX.getValue() + ", was " + index.getValue();
            logger.error(message);
            throw new IndexOutOfBoundsException(message);
        }

        return colourTranslate[index.getValue()];
    }

    /**
     * Get the AttributeColour for a given colour character and translation
     *
     * @param ch          The character colour
     * @param translation The type of area we are looking at this object in, i.e. Light, Dark, Metal, etc.
     * @return The attribute colour that this object should be displayed in
     */
    public static AttributeColour getAttributeColour(char ch, ColourTranslation translation) {
        for (ColourType c : ColourType.values()) {
            if (c.getColourCharacter() == ch) {
                if (translation == ColourTranslation.ATTR_MAX)
                    translation = ColourTranslation.ATTR_FULL;

                return c.colourAttribute(translation);
            }
        }

        return AttributeColour.COLOUR_WHITE;
    }

    /**
     * Given an AttributeColour, return the string name stored in this enum.
     * <br><br>
     * This is used here as we want AttributeColour to handle the actual colours only, and this enum class to handle
     * everything else.
     * <br><br>
     * This function loops through the colours in the values return and finds the one where the ATTR_FULL translation is
     * the same as the incoming colour, and then return the name of that
     *
     * @param colour The attribute colour
     * @return The name of that colour
     */
    public static String attributeToString(AttributeColour colour) {
        for (ColourType colourType : ColourType.values()) {
            if (colourType.colourTranslate[ColourTranslation.ATTR_FULL.getValue()] == colour)
                return colourType.getColourName();
        }

        return "Icky";
    }

    /**
     * Returns the ATTR_FULL colour translation from a colour defined by its character
     *
     * @param ch The character to use
     * @return the result of getAttributeColour where the colour translation is set to ATTR_FULL
     */
    public static AttributeColour getAttributeColour(char ch) {
        return getAttributeColour(ch, ColourTranslation.ATTR_FULL);
    }

    /**
     * Move through a number of translations on a given AttributeColour to get a new colour
     *
     * @param startColour       The initial colour we are looking at (as an Attribute Colour)
     * @param translation       The translation we are going to loop through
     * @param numOfTranslations The number of times we are looping through the translation
     * @return A ColourType (NOT AttributeColour) consisting of the value of the ColourType with the resultant
     * AttributeColour.ColourTable[startColour] n times
     * TODO: Rewrite this. It currently makes no sense, and the code, while simple, needs a bit of explaining
     */
    public static ColourType getColourType(AttributeColour startColour,
                                           ColourTranslation translation,
                                           int numOfTranslations) {
        if (translation == ColourTranslation.ATTR_DARK || translation == ColourTranslation.ATTR_MAX)
            return findColourType(startColour);

        while (numOfTranslations > 0) {
            startColour = findColourType(startColour).colourAttribute(translation);
            numOfTranslations--;
        }

        return findColourType(startColour);
    }

    @Contract(pure = true)
    private static ColourType findColourType(AttributeColour colour) {
        for (ColourType colourType : ColourType.values())
            if (colourType.colourTranslate[0] == colour)
                return colourType;

        return COLOUR_TYPE_DARK;
    }
}