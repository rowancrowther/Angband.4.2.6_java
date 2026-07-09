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

package uk.co.jackoftrades.frontend.colour.enums;

import org.jetbrains.annotations.Contract;

/**
 * Colour translation enum used to support environments with a limited colour depth, as well as translating colours to
 * alternates for, for example, menu highlighting
 */
public enum ColourTranslation {
    /**
     * Full colour - keep the colour the same
     */
    ATTR_FULL(0),

    /**
     * Just black and white
     */
    ATTR_MONO(1),

    /**
     * VGA standard 16 colours
     */
    ATTR_VGA(2),

    /**
     * Colours to use if the character is "blind"
     */
    ATTR_BLIND(3),

    /**
     * Colour to use for torch light areas
     */
    ATTR_LIGHT(4),

    /**
     * Colours to use for dark areas
     */
    ATTR_DARK(5),

    /**
     * Colours for highlighted items
     */
    ATTR_HIGH(6),

    /**
     * Metallic colours
     */
    ATTR_METAL(7),

    /**
     * Miscellaneous colours
     */
    ATTR_MISC(8),

    /**
     * The maximum number of colour translations. If the index to ColourType.colourTable[] is greater or equal to this,
     * or less than 0, an index out of bounds error will be thrown.
     */
    ATTR_MAX(9);

    /**
     * The integer index this translation occupies in a {@code ColourType}
     * translation table.
     *
     * @author Rowan Crowther
     */
    private final int value;

    /**
     * Bind the constant to its table index.
     *
     * @param value the translation-table index
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    ColourTranslation(int value) {
        this.value = value;
    }

    /**
     * @return the translation-table index for this constant
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    public int getValue() {
        return value;
    }
}
