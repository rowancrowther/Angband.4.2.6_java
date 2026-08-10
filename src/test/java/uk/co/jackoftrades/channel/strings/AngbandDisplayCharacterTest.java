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

package uk.co.jackoftrades.channel.strings;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.colour.ColourEnum;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AngbandDisplayCharacter}, the glyph-plus-colour pair the terminal
 * draws - the port of the C source's {@code attr}/{@code char} couple.
 *
 * <p>The type is immutable and carries no logic beyond its three constructors, but those
 * constructors are where the game's data files meet the code: a monster or terrain entry
 * gives its colour as a single letter, or occasionally as a full name, and the string
 * overload has to route to the right one. The colour lookup it delegates to returns null
 * for anything it does not recognise, and this constructor does not intervene - so a
 * mistyped colour in a data file produces a glyph with a null colour rather than a parse
 * error. That is pinned below as current behaviour, since it is the kind of thing a caller
 * has to know to guard against.
 *
 * @author Rowan Crowther
 */
class AngbandDisplayCharacterTest {

    /**
     * The three ways a glyph can be built.
     */
    @Nested
    class Construction {

        @Test
        void theColourEnumConstructorStoresBothHalves() {
            AngbandDisplayCharacter glyph =
                    new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE);

            assertEquals('@', glyph.getCharacter());
            assertSame(ColourEnum.COLOUR_WHITE, glyph.getAttributeColour());
        }

        @Test
        void theCharConstructorResolvesTheColourCode() {
            AngbandDisplayCharacter glyph = new AngbandDisplayCharacter('k', 'r');

            assertEquals('k', glyph.getCharacter());
            assertSame(ColourEnum.COLOUR_RED, glyph.getAttributeColour());
        }

        @Test
        void theStringConstructorTakesASingleCharacterAsACode() {
            AngbandDisplayCharacter glyph = new AngbandDisplayCharacter('k', "r");

            assertSame(ColourEnum.COLOUR_RED, glyph.getAttributeColour());
        }

        @Test
        void theStringConstructorTakesALongerStringAsADisplayName() {
            AngbandDisplayCharacter glyph = new AngbandDisplayCharacter('k', "Light Blue");

            assertSame(ColourEnum.COLOUR_LIGHT_BLUE, glyph.getAttributeColour());
        }

        @Test
        void anUnrecognisedColourLeavesTheGlyphWithNoColourAtAll() {
            // No exception: the null propagates to whatever tries to draw it, so callers
            // parsing data files have to check rather than assume.
            assertNull(new AngbandDisplayCharacter('k', 'q').getAttributeColour());
            assertNull(new AngbandDisplayCharacter('k', "Chartreuse").getAttributeColour());
        }

        @Test
        void theGlyphItselfIsStoredVerbatimIncludingUnusualCharacters() {
            assertEquals(' ', new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_SHADE)
                    .getCharacter());
            assertEquals('\0', new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK)
                    .getCharacter());
        }
    }

    /**
     * Value semantics - both halves take part in equality.
     */
    @Nested
    class Equality {

        @Test
        void glyphsWithTheSameCharacterAndColourAreEqual() {
            assertEquals(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE),
                    new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE));
        }

        @Test
        void theSameCharacterInADifferentColourIsADifferentGlyph() {
            assertNotEquals(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE),
                    new AngbandDisplayCharacter('@', ColourEnum.COLOUR_RED));
        }

        @Test
        void aDifferentCharacterInTheSameColourIsADifferentGlyph() {
            assertNotEquals(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE),
                    new AngbandDisplayCharacter('k', ColourEnum.COLOUR_WHITE));
        }

        @Test
        void theTwoConstructorRoutesProduceEqualGlyphs() {
            // A glyph built from a data-file colour code must compare equal to the same
            // glyph built from the enum directly, or lookups keyed on it would miss.
            assertEquals(new AngbandDisplayCharacter('k', ColourEnum.COLOUR_RED),
                    new AngbandDisplayCharacter('k', 'r'));
            assertEquals(new AngbandDisplayCharacter('k', ColourEnum.COLOUR_RED),
                    new AngbandDisplayCharacter('k', "Red"));
        }

        @Test
        void nullAndForeignTypesAreNotEqual() {
            AngbandDisplayCharacter glyph =
                    new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE);

            assertNotEquals(null, glyph);
            assertFalse(glyph.equals("@"));
        }

        @Test
        void equalGlyphsHashAlike() {
            assertEquals(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE).hashCode(),
                    new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE).hashCode());
        }

        @Test
        void glyphsWorkAsSetMembers() {
            Set<AngbandDisplayCharacter> seen = new HashSet<>();
            seen.add(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE));

            assertTrue(seen.contains(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE)));
            assertFalse(seen.contains(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_RED)));
        }

        @Test
        void everyColourOfOneGlyphIsADistinctValue() {
            // The character sheet and monster list distinguish creatures by colour alone, so
            // colour has to be a full participant in identity, not a decoration.
            Set<AngbandDisplayCharacter> glyphs = new HashSet<>();
            for (ColourEnum colour : ColourEnum.values()) {
                glyphs.add(new AngbandDisplayCharacter('k', colour));
            }

            assertEquals(ColourEnum.values().length, glyphs.size());
        }
    }
}
