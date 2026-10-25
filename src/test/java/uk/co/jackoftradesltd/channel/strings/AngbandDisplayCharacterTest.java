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

package uk.co.jackoftradesltd.channel.strings;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AngbandDisplayCharacter}, the glyph-plus-colour pair the terminal
 * draws - the port of the C source's {@code attr}/{@code char} couple.
 *
 * <p>The type is immutable and carries no logic beyond its five constructors, but those
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

            assertEquals('@', glyph.getNarrowCharacter());
            assertSame(ColourEnum.COLOUR_WHITE, glyph.getAttributeColour());
        }

        @Test
        void theCharConstructorResolvesTheColourCode() {
            AngbandDisplayCharacter glyph = new AngbandDisplayCharacter('k', 'r');

            assertEquals('k', glyph.getNarrowCharacter());
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
                    .getNarrowCharacter());
            assertEquals('\0', new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK)
                    .getNarrowCharacter());
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
        void anUnrecognisedColourHashesAndComparesConsistently() {
            // equals() treats a null attributeColour as an ordinary value via ==, and
            // hashCode() must agree rather than throwing, or such a glyph could never be
            // used as a set/map key despite comparing equal to its twin.
            AngbandDisplayCharacter glyph = new AngbandDisplayCharacter('k', 'q');
            AngbandDisplayCharacter sameGlyph = new AngbandDisplayCharacter('k', 'q');

            assertEquals(glyph, sameGlyph);
            assertEquals(glyph.hashCode(), sameGlyph.hashCode());
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

    /**
     * Glyphs given as an {@code int} code point. C holds a glyph in one {@code wchar_t}
     * ({@code d_char} in {@code object.h}), so the expected values here are what a single number
     * would give: the same number is the same glyph however it was written, and different numbers
     * are different glyphs. Java has to split the number across a {@code char} and an {@code int},
     * which is where the two could part company.
     */
    @Nested
    class CodePoints {

        private static final ColourEnum RED = ColourEnum.COLOUR_RED;

        @Test
        void aCodePointThatFitsInACharIsStoredNarrowWithNoWidePart() {
            for (int cp : new int[]{0, 'a', 0x2591, 0xD800, 0xFFFF}) {
                AngbandDisplayCharacter glyph = new AngbandDisplayCharacter(cp, RED);

                assertEquals((char) cp, glyph.getNarrowCharacter(), "narrow for " + cp);
                assertEquals(0, glyph.getWideCharacter(), "wide for " + cp);
                assertEquals(cp, glyph.getCharacter().codePointAt(0), "whole glyph for " + cp);
            }
        }

        @Test
        void aSupplementaryCodePointIsStoredWideAndTheNarrowPartIsNul() {
            for (int cp : new int[]{0x10000, 0x1F600, 0x10FFFF}) {
                AngbandDisplayCharacter glyph = new AngbandDisplayCharacter(cp, RED);

                assertEquals('\0', glyph.getNarrowCharacter(), "narrow for " + cp);
                assertEquals(cp, glyph.getWideCharacter(), "wide for " + cp);
                assertEquals(cp, glyph.getCharacter().codePointAt(0), "whole glyph for " + cp);
                assertEquals(1, glyph.getCharacter().codePointCount(0, glyph.getCharacter().length()),
                        "one code point for " + cp);
            }
        }

        @Test
        void theLastCharSizedValueAndTheFirstSupplementaryOneSitOnOppositeSides() {
            assertEquals(0, new AngbandDisplayCharacter(0xFFFF, RED).getWideCharacter());
            assertEquals(0x10000, new AngbandDisplayCharacter(0x10000, RED).getWideCharacter());
        }

        @Test
        void aValueOutsideTheCodePointRangeIsRefused() {
            for (int cp : new int[]{-1, Integer.MIN_VALUE, 0x110000, Integer.MAX_VALUE}) {
                assertThrows(IllegalArgumentException.class,
                        () -> new AngbandDisplayCharacter(cp, RED), "enum colour, " + cp);
                assertThrows(IllegalArgumentException.class,
                        () -> new AngbandDisplayCharacter(cp, "Red"), "string colour, " + cp);
            }
        }

        @Test
        void theSameNumberIsTheSameGlyphWhicheverConstructorBuiltIt() {
            // C: 'a' and 97 are one wchar_t value, so the cells must be one value too.
            AngbandDisplayCharacter fromChar = new AngbandDisplayCharacter('a', RED);
            AngbandDisplayCharacter fromInt = new AngbandDisplayCharacter(97, RED);

            assertEquals(fromChar, fromInt);
            assertEquals(fromChar.hashCode(), fromInt.hashCode());
            assertEquals(new AngbandDisplayCharacter('a', "r"), new AngbandDisplayCharacter(97, "r"));
        }

        @Test
        void aNulGlyphIsTheSameWhicheverWayItIsWritten() {
            AngbandDisplayCharacter fromChar = new AngbandDisplayCharacter('\0', RED);
            AngbandDisplayCharacter fromInt = new AngbandDisplayCharacter(0, RED);

            assertEquals(fromChar, fromInt);
            assertEquals("\0", fromInt.getCharacter());
        }

        @Test
        void twoDifferentWideGlyphsAreDifferentValues() {
            AngbandDisplayCharacter a = new AngbandDisplayCharacter(0x1F600, RED);
            AngbandDisplayCharacter b = new AngbandDisplayCharacter(0x1F601, RED);

            assertNotEquals(a, b);
            assertNotEquals(a.hashCode(), b.hashCode());
        }

        @Test
        void aWideGlyphIsNotEqualToAnyNarrowOneInTheSameColour() {
            assertNotEquals(new AngbandDisplayCharacter(0x1F600, RED),
                    new AngbandDisplayCharacter('\0', RED));
        }

        @Test
        void twoDifferentWideGlyphsStillHashApartWhenTheColourIsUnrecognised() {
            AngbandDisplayCharacter a = new AngbandDisplayCharacter(0x1F600, "Chartreuse");
            AngbandDisplayCharacter b = new AngbandDisplayCharacter(0x1F601, "Chartreuse");

            assertNull(a.getAttributeColour());
            assertNotEquals(a, b);
            assertNotEquals(a.hashCode(), b.hashCode());
        }

        @Test
        void aWideGlyphBuiltFromAColourNameEqualsOneBuiltFromTheEnum() {
            AngbandDisplayCharacter fromEnum = new AngbandDisplayCharacter(0x1F600, RED);
            AngbandDisplayCharacter fromName = new AngbandDisplayCharacter(0x1F600, "Red");

            assertEquals(fromEnum, fromName);
            assertEquals(fromEnum.hashCode(), fromName.hashCode());
        }

        @Test
        void wideGlyphsWorkAsSetMembers() {
            Set<AngbandDisplayCharacter> seen = new HashSet<>();
            seen.add(new AngbandDisplayCharacter(0x1F600, RED));

            assertTrue(seen.contains(new AngbandDisplayCharacter(0x1F600, "r")));
            assertFalse(seen.contains(new AngbandDisplayCharacter(0x1F601, RED)));
        }

        @Test
        void theCharConstructorsReadBackWholeAsText() {
            assertEquals("@", new AngbandDisplayCharacter('@', RED).getCharacter());
            assertEquals(" ", AngbandDisplayCharacter.blank.getCharacter());
        }
    }
}
