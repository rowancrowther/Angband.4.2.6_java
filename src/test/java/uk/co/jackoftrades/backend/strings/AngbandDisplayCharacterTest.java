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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
 */

package uk.co.jackoftrades.backend.strings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

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