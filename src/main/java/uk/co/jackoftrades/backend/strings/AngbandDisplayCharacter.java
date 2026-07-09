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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;
import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

/**
 * Class to hold a single character of a particular colour
 */
public class AngbandDisplayCharacter {
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
    private final AttributeColour attributeColour;

    /**
     * Constructor (the only way to set field values
     *
     * @param character  The character of this DisplayCharacter
     * @param colourType The ColourType in which this colour needs to be displayed in a screen
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public AngbandDisplayCharacter(char character, @NotNull ColourType colourType) {
        this.character = character;
        this.attributeColour = colourType.colourAttribute(ColourTranslation.ATTR_FULL);
    }

    /**
     * Constructor (the only way to set field values
     *
     * @param character       The character of this DisplayCharacter
     * @param attributeColour The AttributeColour in which this colour needs to be displayed in a screen
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public AngbandDisplayCharacter(char character, @NotNull AttributeColour attributeColour) {
        this.character = character;
        this.attributeColour = attributeColour;
    }

    /**
     * Constructor
     *
     * @param character The character of this DisplayCharacter
     * @param colour    the character representation of this AttributeColour
     */
    public AngbandDisplayCharacter(char character, char colour) {
        this.character = character;
        this.attributeColour = ColourType.getAttributeColour(colour);
    }

    /**
     * Constructor
     *
     * @param character The character glyph for this DisplayCharacter
     * @param colour    A string containing either a single character
     *                  representation of the ColourType, or a name
     *                  of the ColourType
     */
    public AngbandDisplayCharacter(char character, String colour) {
        ColourType colourType;

        if (colour.length() == 1)
            colourType = ColourType.findColourType(colour.charAt(0));
        else
            colourType = ColourType.findColourType(colour);

        this.character = character;
        this.attributeColour = colourType.colourAttribute(ColourTranslation.ATTR_FULL);
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
     * @return the colour of this character as an AttributeColour
     */
    @CheckReturnValue
    @Contract(pure = true)
    public AttributeColour getAttributeColour() {
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