package uk.co.jackoftrades.background.strings;

import uk.co.jackoftrades.background.colour.enums.AttributeColour;

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
