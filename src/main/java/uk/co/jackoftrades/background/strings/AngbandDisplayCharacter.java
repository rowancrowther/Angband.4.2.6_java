package uk.co.jackoftrades.background.strings;

import uk.co.jackoftrades.background.strings.enums.AttributeColour;

public class AngbandDisplayCharacter {
    private char character;
    private AttributeColour attributeColour;

    public AngbandDisplayCharacter(char character, AttributeColour attributeColour) {
        this.character = character;
        this.attributeColour = attributeColour;
    }

    public char getCharacter() {
        return character;
    }

    public AttributeColour getAttributeColour() {
        return attributeColour;
    }
}
