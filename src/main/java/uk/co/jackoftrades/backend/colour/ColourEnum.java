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

package uk.co.jackoftrades.backend.colour;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public enum ColourEnum {
    COLOUR_TYPE_DARK('d', "Dark",
            new char[]{'d', 'd', 'd', 'd', 'D', 'd', 'D', 'D', 'd'},
            new Color(0, 0, 0)),
    COLOUR_TYPE_WHITE('w', "White",
            new char[]{'w', 'w', 'w', 'w', 'y', 'W', 'B', 'y', 'w'},
            new Color(255, 255, 255)),
    COLOUR_TYPE_SLATE('s', "Slate",
            new char[]{'s', 'w', 's', 's', 'W', 'D', 'W', 'W', 's'},
            new Color(128, 128, 128)),
    COLOUR_TYPE_ORANGE('o', "Orange",
            new char[]{'o', 'w', 'o', 'W', 'y', 's', 'y', 'y', 'o'},
            new Color(255, 128, 0)),
    COLOUR_TYPE_RED('r', "Red",
            new char[]{'r', 'w', 'r', 's', 'R', 's', 'R', 'R', 'r'},
            new Color(192, 0, 0)),
    COLOUR_TYPE_GREEN('g', "Green",
            new char[]{'g', 'w', 'g', 's', 'G', 's', 'G', 'G', 'g'},
            new Color(0, 128, 64)),
    COLOUR_TYPE_BLUE('b', "Blue",
            new char[]{'b', 'w', 'b', 's', 'B', 's', 'B', 'B', 'b'},
            new Color(0, 64, 255)),
    COLOUR_TYPE_UMBER('u', "Umber",
            new char[]{'u', 'w', 'u', 'D', 'U', 'D', 'U', 'U', 'u'},
            new Color(128, 64, 0)),
    COLOUR_TYPE_LIGHT_DARK('D', "Light Dark",
            new char[]{'D', 'w', 'D', 'D', 's', 'D', 's', 's', 'D'},
            new Color(96, 96, 96)),
    COLOUR_TYPE_LIGHT_SLATE('W', "Light Slate",
            new char[]{'W', 'w', 'W', 'W', 'w', 's', 'w', 'w', 's'},
            new Color(192, 192, 192)),
    COLOUR_TYPE_LIGHT_PURPLE('P', "Light Purple",
            new char[]{'P', 'w', 'P', 's', 'y', 's', 'y', 'y', 'P'},
            new Color(255, 0, 255)),
    COLOUR_TYPE_YELLOW('y', "Yellow",
            new char[]{'y', 'w', 'y', 'W', 'Y', 'W', 'w', 'w', 'y'},
            new Color(255, 255, 0)),
    COLOUR_TYPE_LIGHT_RED('R', "Light Red",
            new char[]{'R', 'w', 'R', 'W', 'y', 'r', 'y', 'y', 'R'},
            new Color(255, 64, 64)),
    COLOUR_TYPE_LIGHT_GREEN('G', "Light Green",
            new char[]{'G', 'w', 'G', 'W', 'y', 'g', 'y', 'y', 'G'},
            new Color(0, 255, 0)),
    COLOUR_TYPE_LIGHT_BLUE('B', "Light Blue",
            new char[]{'B', 'w', 'B', 'W', 'y', 'b', 'y', 'y', 'B'},
            new Color(0, 255, 255)),
    COLOUR_TYPE_LIGHT_UMBER('U', "Light Umber",
            new char[]{'U', 'w', 'U', 'W', 'y', 'u', 'y', 'y', 'U'},
            new Color(192, 128, 64)),
    COLOUR_TYPE_PURPLE('p', "Purple",
            new char[]{'p', 'w', 'P', 's', 'P', 's', 'P', 'P', 'P'},
            new Color(144, 0, 144)),
    COLOUR_TYPE_VIOLET('v', "Violet",
            new char[]{'v', 'w', 'P', 's', 'P', 's', 'P', 'P', 'P'},
            new Color(144, 32, 255)),
    COLOUR_TYPE_TEAL('t', "Teal",
            new char[]{'t', 'w', 'b', 's', 'T', 's', 'T', 'T', 'B'},
            new Color(0, 160, 160)),
    COLOUR_TYPE_MUD('m', "Mud",
            new char[]{'m', 'w', 'g', 's', 'M', 's', 'M', 'M', 'u'},
            new Color(108, 108, 48)),
    COLOUR_TYPE_LIGHT_YELLOW('Y', "Light Yellow",
            new char[]{'Y', 'w', 'y', 'w', 'w', 'y', 'w', 'w', 'Y'},
            new Color(255, 255, 144)),
    COLOUR_TYPE_MAGENTA_PINK('i', "Magenta-Pink",
            new char[]{'i', 'w', 'R', 's', 'I', 'r', 'I', 'I', 'P'},
            new Color(255, 0, 160)),
    COLOUR_TYPE_LIGHT_TEAL('T', "Light Teal",
            new char[]{'T', 'w', 'B', 'W', 'y', 't', 'y', 'y', 'B'},
            new Color(32, 255, 220)),
    COLOUR_TYPE_LIGHT_VIOLET('V', "Light Violet",
            new char[]{'V', 'w', 'P', 'W', 'y', 'v', 'y', 'y', 'P'},
            new Color(184, 168, 255)),
    COLOUR_TYPE_LIGHT_PINK('I', "Light Pink",
            new char[]{'I', 'w', 'R', 'W', 'y', 'i', 'y', 'y', 'P'},
            new Color(255, 128, 128)),
    COLOUR_TYPE_MUSTARD('M', "Mustard",
            new char[]{'M', 'w', 'y', 's', 'y', 's', 'y', 'y', 'y'},
            new Color(180, 180, 0)),
    COLOUR_TYPE_BLUE_SLATE('z', "Blue Slate",
            new char[]{'z', 'w', 'W', 's', 'Z', 's', 'Z', 'Z', 'W'},
            new Color(160, 192, 208)),
    COLOUR_TYPE_DEEP_LIGHT_BLUE('Z', "Deep Light Blue",
            new char[]{'Z', 'w', 'B', 'W', 'B', 'z', 'B', 'B', 'B'},
            new Color(0, 176, 255)),
    COLOUR_TYPE_SHADE(' ', "Shade",
            new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            new Color(40, 40, 40));

    /**
     * Single-character code identifying this colour in data files/preferences.
     *
     * @author Rowan Crowther
     */
    private final char colourCharacter;
    /**
     * Human-readable name of this colour.
     *
     * @author Rowan Crowther
     */
    private final String colourName;
    /**
     * Per-context translation table: indexed by {@link ColourTranslation}.
     *
     * @author Rowan Crowther
     */
    private final char[] colourTranslate;
    /**
     * Concrete {@link java.awt.Color} colour used when drawing this colour.
     *
     * @author Rowan Crowther
     */
    private final Color colour;

    /**
     * Build a colour-table entry.
     *
     * @param c      the single-character colour code
     * @param name   the colour's display name
     * @param table  the 9-entry per-context translation table
     * @param colour the JavaFX colour used to draw it
     * @author Rowan Crowther
     */
    ColourEnum(char c, String name, char[] table, Color colour) {
        colourCharacter = c;
        colourName = name;
        colourTranslate = table;
        this.colour = colour;
    }

    @CheckReturnValue
    @NotNull
    @Contract(pure = true)
    public Color getColour() {
        return colour;
    }

    @NotNull
    @Contract(pure = true)
    @CheckReturnValue
    public static String attributeToString(@NotNull ColourEnum colour) {
        return colour.colourName;
    }

    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static ColourEnum fromCode(@NotNull String code) {
        if (code.length() == 1) {
            return fromCode(code.charAt(0));
        }

        for (ColourEnum colour : ColourEnum.values()) {
            if (colour.colourName.equalsIgnoreCase(code)) {
                return colour;
            }
        }
        return null;
    }

    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static ColourEnum fromCode(char c) {
        for (ColourEnum colour : ColourEnum.values()) {
            if (colour.colourCharacter == c) {
                return colour;
            }
        }

        return null;
    }

    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public ColourEnum forTranslation(ColourTranslation index) {
        return ColourEnum.fromCode(colourTranslate[index.getValue()]);
    }

    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static ColourEnum translateColour(@NotNull ColourEnum startColour, @NotNull ColourTranslation translation,
                                             int numberOfTimes) {
        ColourEnum currentColour = startColour;

        for (int i = 0; i < numberOfTimes; i++) {
            if (currentColour != null) {
                currentColour = currentColour.forTranslation(translation);
            }
        }

        return currentColour;
    }
}
