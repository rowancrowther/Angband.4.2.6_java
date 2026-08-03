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

public enum ColourEnum {
    COLOUR_DARK('d', "Dark",
            new char[]{'d', 'd', 'd', 'd', 'D', 'd', 'D', 'D', 'd'}),
    COLOUR_WHITE('w', "White",
            new char[]{'w', 'w', 'w', 'w', 'y', 'W', 'B', 'y', 'w'}),
    COLOUR_SLATE('s', "Slate",
            new char[]{'s', 'w', 's', 's', 'W', 'D', 'W', 'W', 's'}),
    COLOUR_ORANGE('o', "Orange",
            new char[]{'o', 'w', 'o', 'W', 'y', 's', 'y', 'y', 'o'}),
    COLOUR_RED('r', "Red",
            new char[]{'r', 'w', 'r', 's', 'R', 's', 'R', 'R', 'r'}),
    COLOUR_GREEN('g', "Green",
            new char[]{'g', 'w', 'g', 's', 'G', 's', 'G', 'G', 'g'}),
    COLOUR_BLUE('b', "Blue",
            new char[]{'b', 'w', 'b', 's', 'B', 's', 'B', 'B', 'b'}),
    COLOUR_UMBER('u', "Umber",
            new char[]{'u', 'w', 'u', 'D', 'U', 'D', 'U', 'U', 'u'}),
    COLOUR_LIGHT_DARK('D', "Light Dark",
            new char[]{'D', 'w', 'D', 'D', 's', 'D', 's', 's', 'D'}),
    COLOUR_LIGHT_SLATE('W', "Light Slate",
            new char[]{'W', 'w', 'W', 'W', 'w', 's', 'w', 'w', 's'}),
    COLOUR_LIGHT_PURPLE('P', "Light Purple",
            new char[]{'P', 'w', 'P', 's', 'y', 's', 'y', 'y', 'P'}),
    COLOUR_YELLOW('y', "Yellow",
            new char[]{'y', 'w', 'y', 'W', 'Y', 'W', 'w', 'w', 'y'}),
    COLOUR_LIGHT_RED('R', "Light Red",
            new char[]{'R', 'w', 'R', 'W', 'y', 'r', 'y', 'y', 'R'}),
    COLOUR_LIGHT_GREEN('G', "Light Green",
            new char[]{'G', 'w', 'G', 'W', 'y', 'g', 'y', 'y', 'G'}),
    COLOUR_LIGHT_BLUE('B', "Light Blue",
            new char[]{'B', 'w', 'B', 'W', 'y', 'b', 'y', 'y', 'B'}),
    COLOUR_LIGHT_UMBER('U', "Light Umber",
            new char[]{'U', 'w', 'U', 'W', 'y', 'u', 'y', 'y', 'U'}),
    COLOUR_PURPLE('p', "Purple",
            new char[]{'p', 'w', 'P', 's', 'P', 's', 'P', 'P', 'P'}),
    COLOUR_VIOLET('v', "Violet",
            new char[]{'v', 'w', 'P', 's', 'P', 's', 'P', 'P', 'P'}),
    COLOUR_TEAL('t', "Teal",
            new char[]{'t', 'w', 'b', 's', 'T', 's', 'T', 'T', 'B'}),
    COLOUR_MUD('m', "Mud",
            new char[]{'m', 'w', 'g', 's', 'M', 's', 'M', 'M', 'u'}),
    COLOUR_LIGHT_YELLOW('Y', "Light Yellow",
            new char[]{'Y', 'w', 'y', 'w', 'w', 'y', 'w', 'w', 'Y'}),
    COLOUR_MAGENTA_PINK('i', "Magenta-Pink",
            new char[]{'i', 'w', 'R', 's', 'I', 'r', 'I', 'I', 'P'}),
    COLOUR_LIGHT_TEAL('T', "Light Teal",
            new char[]{'T', 'w', 'B', 'W', 'y', 't', 'y', 'y', 'B'}),
    COLOUR_LIGHT_VIOLET('V', "Light Violet",
            new char[]{'V', 'w', 'P', 'W', 'y', 'v', 'y', 'y', 'P'}),
    COLOUR_LIGHT_PINK('I', "Light Pink",
            new char[]{'I', 'w', 'R', 'W', 'y', 'i', 'y', 'y', 'P'}),
    COLOUR_MUSTARD('M', "Mustard",
            new char[]{'M', 'w', 'y', 's', 'y', 's', 'y', 'y', 'y'}),
    COLOUR_BLUE_SLATE('z', "Blue Slate",
            new char[]{'z', 'w', 'W', 's', 'Z', 's', 'Z', 'Z', 'W'}),
    COLOUR_DEEP_LIGHT_BLUE('Z', "Deep Light Blue",
            new char[]{'Z', 'w', 'B', 'W', 'B', 'z', 'B', 'B', 'B'}),
    COLOUR_SHADE(' ', "Shade",
            new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '});

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
     * Build a colour-table entry.
     *
     * @param c      the single-character colour code
     * @param name   the colour's display name
     * @param table  the 9-entry per-context translation table
     * @author Rowan Crowther
     */
    ColourEnum(char c, String name, char[] table) {
        colourCharacter = c;
        colourName = name;
        colourTranslate = table;
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
        return COLOUR_WHITE;
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
