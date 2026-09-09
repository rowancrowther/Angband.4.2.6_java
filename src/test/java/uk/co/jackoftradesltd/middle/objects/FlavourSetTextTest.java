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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Flavour#setText}, the Java equivalent of the direct C struct-field write
 * {@code f->text = ...} in {@code flavor_assign_random} ({@code obj-util.c:106}), where a random
 * scroll flavour is retitled from the {@code scroll_adj} table after its sval is chosen.
 *
 * @author Rowan Crowther
 */
class FlavourSetTextTest {

    @Test
    void overwritesExistingText() {
        Flavour flavour = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);

        flavour.setText("Foul-Smelling");

        assertEquals("Foul-Smelling", flavour.getText());
    }

    @Test
    void acceptsNull() {
        Flavour flavour = new Flavour("Azure", ColourEnum.COLOUR_BLUE, 3);

        flavour.setText(null);

        assertNull(flavour.getText());
    }

    @Test
    void leavesOtherFieldsUntouched() {
        Flavour flavour = new Flavour("Kellek", "Ring of Power", ColourEnum.COLOUR_RED, 7);

        flavour.setText("Woven");

        assertEquals("Woven", flavour.getText());
        assertEquals("Ring of Power", flavour.getsValStr());
        assertEquals(ColourEnum.COLOUR_RED, flavour.getColour());
        assertEquals(7, flavour.getIndex());
        assertEquals(true, flavour.isFixed());
    }
}
