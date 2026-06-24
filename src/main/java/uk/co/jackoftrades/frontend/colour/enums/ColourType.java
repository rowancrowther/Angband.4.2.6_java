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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.colour.enums;

import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * The master colour table, ported from the C original's {@code color_table}
 * (see {@code src/ui-prefs.c}). Each constant is one named display colour and
 * carries:
 * <ul>
 *   <li>a single-character code used in data files and preferences;</li>
 *   <li>a human-readable name;</li>
 *   <li>a 9-entry {@link AttributeColour} translation table, indexed by
 *       {@link ColourTranslation}, giving how this colour should appear under
 *       different rendering contexts (full colour, mono, light, dark, …) so the
 *       same logical colour can be remapped for accessibility or terminal
 *       capability;</li>
 *   <li>the concrete JavaFX {@link Color} used when actually drawing.</li>
 * </ul>
 * The individual colour constants are self-describing by name, so they are
 * documented collectively here rather than individually.
 *
 * @author ClaudeCode
 */
public enum ColourType {
    COLOUR_TYPE_DARK('d', "Dark",
            new AttributeColour[]{AttributeColour.COLOUR_DARK, AttributeColour.COLOUR_DARK, AttributeColour.COLOUR_DARK,
                    AttributeColour.COLOUR_DARK, AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_DARK,
                    AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_DARK},
            Color.rgb(0, 0, 0)),
    COLOUR_TYPE_WHITE('w', "White",
            new AttributeColour[]{AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_WHITE},
            Color.rgb(255, 255, 255)),
    COLOUR_TYPE_SLATE('s', "Slate",
            new AttributeColour[]{AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_SLATE},
            Color.rgb(128, 128, 128)),
    COLOUR_TYPE_ORANGE('o', "Orange",
            new AttributeColour[]{AttributeColour.COLOUR_ORANGE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_ORANGE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_ORANGE},
            Color.rgb(255, 128, 0)),
    COLOUR_TYPE_RED('r', "Red",
            new AttributeColour[]{AttributeColour.COLOUR_RED, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_RED,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_RED},
            Color.rgb(192, 0, 0)),
    COLOUR_TYPE_GREEN('g', "Green",
            new AttributeColour[]{AttributeColour.COLOUR_GREEN, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_GREEN,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_GREEN},
            Color.rgb(0, 128, 64)),
    COLOUR_TYPE_BLUE('b', "Blue",
            new AttributeColour[]{AttributeColour.COLOUR_BLUE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_BLUE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_BLUE},
            Color.rgb(0, 64, 255)),
    COLOUR_TYPE_UMBER('u', "Umber",
            new AttributeColour[]{AttributeColour.COLOUR_UMBER, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_UMBER,
                    AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_UMBER},
            Color.rgb(128, 64, 0)),
    COLOUR_TYPE_LIGHT_DARK('D', "Light Dark",
            new AttributeColour[]{AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_L_DARK, AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_DARK,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_DARK},
            Color.rgb(96, 96, 96)),
    COLOUR_TYPE_LIGHT_SLATE('W', "Light Slate",
            new AttributeColour[]{AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_SLATE},
            Color.rgb(192, 192, 192)),
    COLOUR_TYPE_LIGHT_PURPLE('P', "Light Purple",
            new AttributeColour[]{AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_PURPLE},
            Color.rgb(255, 0, 255)),
    COLOUR_TYPE_YELLOW('y', "Yellow",
            new AttributeColour[]{AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_YELLOW, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW},
            Color.rgb(255, 255, 0)),
    COLOUR_TYPE_LIGHT_RED('R', "Light Red",
            new AttributeColour[]{AttributeColour.COLOUR_L_RED, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_RED,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_RED,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_RED},
            Color.rgb(255, 64, 64)),
    COLOUR_TYPE_LIGHT_GREEN('G', "Light Green",
            new AttributeColour[]{AttributeColour.COLOUR_L_GREEN, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_GREEN,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_GREEN,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_GREEN},
            Color.rgb(0, 255, 0)),
    COLOUR_TYPE_LIGHT_BLUE('B', "Light Blue",
            new AttributeColour[]{AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_BLUE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_BLUE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_BLUE},
            Color.rgb(0, 255, 255)),
    COLOUR_TYPE_LIGHT_UMBER('U', "Light Umber",
            new AttributeColour[]{AttributeColour.COLOUR_L_UMBER, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_UMBER,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_UMBER,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_UMBER},
            Color.rgb(192, 128, 64)),
    COLOUR_TYPE_PURPLE('p', "Purple",
            new AttributeColour[]{AttributeColour.COLOUR_PURPLE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE},
            Color.rgb(144, 0, 144)),
    COLOUR_TYPE_VIOLET('v', "Violet",
            new AttributeColour[]{AttributeColour.COLOUR_VIOLET, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE, AttributeColour.COLOUR_L_PURPLE},
            Color.rgb(144, 32, 255)),
    COLOUR_TYPE_TEAL('t', "Teal",
            new AttributeColour[]{AttributeColour.COLOUR_TEAL, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_BLUE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_L_BLUE},
            Color.rgb(0, 160, 160)),
    COLOUR_TYPE_MUD('m', "Mud",
            new AttributeColour[]{AttributeColour.COLOUR_MUD, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_GREEN,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_UMBER},
            Color.rgb(108, 108, 48)),
    COLOUR_TYPE_LIGHT_YELLOW('Y', "Light Yellow",
            new AttributeColour[]{AttributeColour.COLOUR_L_YELLOW, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_YELLOW},
            Color.rgb(255, 255, 144)),
    COLOUR_TYPE_MAGENTA_PINK('i', "Magenta-Pink",
            new AttributeColour[]{AttributeColour.COLOUR_MAGENTA, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_RED,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_RED,
                    AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_L_PURPLE},
            Color.rgb(255, 0, 160)),
    COLOUR_TYPE_LIGHT_TEAL('T', "Light Teal",
            new AttributeColour[]{AttributeColour.COLOUR_L_TEAL, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_BLUE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_TEAL,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_BLUE},
            Color.rgb(32, 255, 220)),
    COLOUR_TYPE_LIGHT_VIOLET('V', "Light Violet",
            new AttributeColour[]{AttributeColour.COLOUR_L_VIOLET, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_PURPLE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_VIOLET,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_BLUE},
            Color.rgb(184, 168, 255)),
    COLOUR_TYPE_LIGHT_PINK('I', "Light Pink",
            new AttributeColour[]{AttributeColour.COLOUR_L_PINK, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_RED,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_MAGENTA,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_L_PURPLE},
            Color.rgb(255, 128, 128)),
    COLOUR_TYPE_MUSTARD('M', "Mustard",
            new AttributeColour[]{AttributeColour.COLOUR_MUSTARD, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_YELLOW,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW, AttributeColour.COLOUR_YELLOW},
            Color.rgb(180, 180, 0)),
    COLOUR_TYPE_BLUE_SLATE('z', "Blue Slate",
            new AttributeColour[]{AttributeColour.COLOUR_BLUE_SLATE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_WHITE,
                    AttributeColour.COLOUR_SLATE, AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_SLATE,
                    AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_WHITE},
            Color.rgb(160, 192, 208)),
    COLOUR_TYPE_DEEP_LIGHT_BLUE('Z', "Deep Light Blue",
            new AttributeColour[]{AttributeColour.COLOUR_DEEP_L_BLUE, AttributeColour.COLOUR_WHITE, AttributeColour.COLOUR_L_BLUE,
                    AttributeColour.COLOUR_L_WHITE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_BLUE_SLATE,
                    AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_L_BLUE, AttributeColour.COLOUR_L_BLUE},
            Color.rgb(0, 176, 255)),
    COLOUR_TYPE_SHADE(' ', "Shade",
            new AttributeColour[]{AttributeColour.COLOUR_SHADE, AttributeColour.COLOUR_SHADE, AttributeColour.COLOUR_SHADE,
                    AttributeColour.COLOUR_SHADE, AttributeColour.COLOUR_SHADE, AttributeColour.COLOUR_SHADE,
                    AttributeColour.COLOUR_SHADE, AttributeColour.COLOUR_SHADE, AttributeColour.COLOUR_SHADE},
            Color.rgb(40, 40, 40));

    /**
     * Single-character code identifying this colour in data files/preferences.
     *
     * @author ClaudeCode
     */
    private final char colourCharacter;
    /**
     * Human-readable name of this colour.
     *
     * @author ClaudeCode
     */
    private final String colourName;
    /**
     * Per-context translation table: indexed by {@link ColourTranslation}, gives
     * the {@link AttributeColour} this colour maps to under each rendering mode.
     *
     * @author ClaudeCode
     */
    private final AttributeColour[] colourTranslate;
    /**
     * Concrete JavaFX colour used when drawing this colour.
     *
     * @author ClaudeCode
     */
    private final Color colour;

    /**
     * Logger used to report out-of-range colour-translation lookups.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Build a colour-table entry.
     *
     * @param c      the single-character colour code
     * @param name   the colour's display name
     * @param table  the 9-entry per-context translation table
     * @param colour the JavaFX colour used to draw it
     * @author ClaudeCode
     */
    ColourType(char c, String name, AttributeColour[] table, Color colour) {
        colourCharacter = c;
        colourName = name;
        colourTranslate = table;
        this.colour = colour;
    }

    /**
     * @return the JavaFX {@link Color} used to draw this colour
     * @author ClaudeCode
     */
    public Color getColour() {
        return colour;
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

    /**
     * Get the colour type based on the name of the colour. Note, the name of the colour can come in in any case, so we
     * need to match it in the upper (or lower) case to confirm a match, so Umber matched umber and vice versa.
     *
     * @param colourName The colour name we are looking for
     * @return The Colour Type which has colourName for a name, or COLOUR_TYPE_DARK if there is no matching colour type
     */
    public static ColourType getColourType(String colourName) {
        String upperColourName = colourName.toUpperCase();
        for (ColourType colourType : ColourType.values()) {
            if (colourName.equals(colourType.getColourName().toUpperCase())) {
                return colourType;
            }
        }

        return COLOUR_TYPE_DARK;
    }

    /**
     * Look up this colour's {@link AttributeColour} for a given rendering
     * context. {@link ColourTranslation#ATTR_MAX} is the out-of-range sentinel
     * and is rejected as an invalid index.
     *
     * @param index the rendering-context translation to apply
     * @return the translated attribute colour
     * @throws InvalidTokenFoundDuringParse if {@code index} is {@code ATTR_MAX}
     * @author ClaudeCode
     */
    public AttributeColour colourAttribute(ColourTranslation index) {
        if (index == ColourTranslation.ATTR_MAX) {
            String message = "Colour index out of bounds, should be between 0 and " +
                    ColourTranslation.ATTR_MAX.getValue() + ", was " + index.getValue();
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
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

    /**
     * Find the colour type whose base (index 0) translation equals the given
     * attribute colour.
     *
     * @param colour the attribute colour to match
     * @return the matching colour type, or {@link #COLOUR_TYPE_DARK} if none matches
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public static ColourType findColourType(AttributeColour colour) {
        for (ColourType colourType : ColourType.values())
            if (colourType.colourTranslate[0] == colour)
                return colourType;

        return COLOUR_TYPE_DARK;
    }

    /**
     * Find the colour type with the given name, case-insensitively.
     *
     * @param colourName the colour name to match
     * @return the matching colour type, or {@link #COLOUR_TYPE_DARK} if none matches
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public static ColourType findColourType(@NotNull String colourName) {
        String lcColourName = colourName.toLowerCase();

        for (ColourType colourType : ColourType.values()) {
            if (colourType.colourName.toLowerCase().equals(lcColourName))
                return colourType;
        }

        return COLOUR_TYPE_DARK;
    }

    /**
     * Find the colour type with the given single-character code.
     *
     * @param colChar the colour code to match
     * @return the matching colour type, or {@link #COLOUR_TYPE_DARK} if none matches
     * @author ClaudeCode
     */
    public static ColourType findColourType(@NotNull char colChar) {
        for (ColourType colourType : ColourType.values()) {
            if (colourType.getColourCharacter() == colChar)
                return colourType;
        }

        return COLOUR_TYPE_DARK;
    }
}