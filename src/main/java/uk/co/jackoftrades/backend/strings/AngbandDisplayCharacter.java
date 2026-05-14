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

package uk.co.jackoftrades.backend.strings;

import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

/**
 * Class to hold a single character of a particular colour
 */
public class AngbandDisplayCharacter {
    private char character;
    private AttributeColour attributeColour;

    /**
     * Constructor (the only way to set field values
     *
     * @param character       The character of this DisplayCharacter
     * @param attributeColour The AttributeColour in which this colour needs to be displayed in a screen
     */
    public AngbandDisplayCharacter(char character, AttributeColour attributeColour) {
        this.character = character;
        this.attributeColour = attributeColour;
    }

    /**
     * Getter for the character
     * @return the character of this instance
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Getter for the colour
     * @return the colour of this character as an AttributeColour
     */
    public AttributeColour getAttributeColour() {
        return attributeColour;
    }
}
