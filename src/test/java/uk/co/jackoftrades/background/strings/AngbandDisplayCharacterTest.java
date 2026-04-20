package uk.co.jackoftrades.background.strings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.background.strings.enums.AttributeColour;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AngbandDisplayCharacterTest {
    AngbandDisplayCharacter whiteZ;
    AngbandDisplayCharacter darkA;
    AngbandDisplayCharacter lightRedQ;

    @BeforeEach
    void setUp() {
        whiteZ = new AngbandDisplayCharacter('Z', AttributeColour.COLOUR_WHITE);
        darkA = new AngbandDisplayCharacter('A', AttributeColour.COLOUR_DARK);
        lightRedQ = new AngbandDisplayCharacter('Q', AttributeColour.COLOUR_L_RED);
    }

    @Test
    void getCharacter() {
        assertAll(
                () -> assertEquals('Z', whiteZ.getCharacter()),
                () -> assertEquals('A', darkA.getCharacter()),
                () -> assertEquals('Q', lightRedQ.getCharacter())
        );
    }

    @Test
    void getAttributeColour() {
        assertAll(
                () -> assertEquals(AttributeColour.COLOUR_WHITE, whiteZ.getAttributeColour()),
                () -> assertEquals(AttributeColour.COLOUR_DARK, darkA.getAttributeColour()),
                () -> assertEquals(AttributeColour.COLOUR_L_RED, lightRedQ.getAttributeColour())
        );
    }
}