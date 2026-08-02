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

package uk.co.jackoftrades.backend.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.colour.ColourEnum;

/**
 * Class to hold a single character of a particular colour
 */
public class AngbandDisplayCharacter {
    private static final Logger logger = LogManager.getLogger();
    /**
     * The glyph to display.
     *
     * @author Rowan Crowther
     */
    private final char character;
    /**
     * The colour the glyph is drawn in, resolved to a concrete attribute colour.
     *
     * @author Rowan Crowther
     */
    private final ColourEnum attributeColour;

    /**
     * Constructor (the only way to set field values
     *
     * @param character  The character of this DisplayCharacter
     * @param colourType The ColourEnum in which this colour needs to be displayed in a screen
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public AngbandDisplayCharacter(char character, @NotNull ColourEnum colourType) {
        this.character = character;
        this.attributeColour = colourType;
    }

    /**
     * Constructor
     *
     * @param character The character of this DisplayCharacter
     * @param colour    the character representation of this ColourEnum
     */
    public AngbandDisplayCharacter(char character, char colour) {
        this.character = character;
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * Constructor
     *
     * @param character The character glyph for this DisplayCharacter
     * @param colour    A string containing either a single character
     *                  representation of the ColourEnum, or a name
     *                  of the ColourEnum
     */
    public AngbandDisplayCharacter(char character, @NotNull String colour) {
        this.character = character;
        this.attributeColour = ColourEnum.fromCode(colour);
    }

    /**
     * Getter for the character
     * @return the character of this instance
     */
    @CheckReturnValue
    @Contract(pure = true)
    public char getCharacter() {
        return character;
    }

    /**
     * Getter for the colour
     * @return the colour of this character as an ColourEnum
     */
    @CheckReturnValue
    @Contract(pure = true)
    public ColourEnum getAttributeColour() {
        return attributeColour;
    }

    /**
     * Checks to see if an incoming value is the same class type and value as this one
     *
     * @param o the reference object with which to compare.
     * @return true if this is the same class type and has the same values as this
     */
    @CheckReturnValue
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AngbandDisplayCharacter that = (AngbandDisplayCharacter) o;
        return getCharacter() == that.getCharacter() && getAttributeColour() == that.getAttributeColour();
    }

    /**
     * Overrides the hash code method of this class
     * <p>
     * This is done to ensure we can use .equals() functionality and for use in hashed collections.
     *
     * @return a hash for this instance of this class
     */
    @CheckReturnValue
    @Contract(pure = true)
    @Override
    public int hashCode() {
        int result = getCharacter();
        result = 31 * result + getAttributeColour().hashCode();
        return result;
    }
}