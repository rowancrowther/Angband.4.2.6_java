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

package uk.co.jackoftradesltd.channel.colour;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ColourEnum} and {@link ColourTranslation}, the port of the C
 * source's colour tables ({@code src/ui-term.c}, {@code list-attr-codes.h}).
 *
 * <p>Each colour carries a one-character code - the letter that appears in the game data
 * files - and a nine-entry translation row saying what it becomes under each display mode:
 * on a monochrome terminal, when the player is blind, when the grid is lit or in shadow,
 * and so on. Those rows are a transcription of a table in the C source, and a transcription
 * has exactly one interesting failure mode: a typo. A code that does not name any colour
 * makes {@link ColourEnum#forTranslation} return null, and the null then propagates
 * silently through {@link ColourEnum#translateColour} to whatever tries to draw with it.
 * The structural tests below therefore walk every colour against every translation, which
 * is the only way a single mistyped character in 261 gets caught.
 *
 * <p>{@link ColourEnum#fromCode(String)} is doubly overloaded by length: a one-character
 * string is a colour code, anything longer is a display name. Both routes are pinned,
 * including the case where they could disagree.
 *
 * @author Rowan Crowther
 */
class ColourEnumTest {

    @Test
    void attributeToStringGivesTheHumanReadableName() {
        assertEquals("Light Blue", ColourEnum.attributeToString(ColourEnum.COLOUR_LIGHT_BLUE));
        assertEquals("Dark", ColourEnum.attributeToString(ColourEnum.COLOUR_DARK));
    }

    /**
     * Lookup by data-file code and by display name.
     */
    @Nested
    class Lookup {

        @Test
        void aSingleCharacterResolvesToTheColourWithThatCode() {
            assertSame(ColourEnum.COLOUR_DARK, ColourEnum.fromCode('d'));
            assertSame(ColourEnum.COLOUR_WHITE, ColourEnum.fromCode('w'));
            assertSame(ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.fromCode('B'));
        }

        @Test
        void codesAreCaseSensitiveBecauseCaseIsWhatDistinguishesLightFromDark() {
            // 'b' and 'B' are two different colours in the data files, not one written two
            // ways - a case-insensitive lookup here would merge every light/dark pair.
            assertSame(ColourEnum.COLOUR_BLUE, ColourEnum.fromCode('b'));
            assertSame(ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.fromCode('B'));
        }

        @Test
        void aSpaceResolvesToDarkNotShade() {
            // C's color_char_to_attr() answers COLOUR_DARK for ' ' before it searches the
            // table, so no data-file character can name COLOUR_SHADE.
            assertSame(ColourEnum.COLOUR_DARK, ColourEnum.fromCode(' '));
        }

        @Test
        void aNulCharacterResolvesToDark() {
            // The other half of the same C early return; entry 28 (Shade) has a '\0' index
            // character in C, but the early return means it is never reached.
            assertSame(ColourEnum.COLOUR_DARK, ColourEnum.fromCode('\0'));
        }

        @Test
        void aOneCharacterSpaceStringTakesTheSameRouteAsTheCharacter() {
            assertSame(ColourEnum.COLOUR_DARK, ColourEnum.fromCode(" "));
        }

        @Test
        void anUnusedCharacterResolvesToNull() {
            assertNull(ColourEnum.fromCode('q'));
            assertNull(ColourEnum.fromCode('\n'));
        }

        @Test
        void aOneCharacterStringIsTreatedAsACode() {
            assertSame(ColourEnum.COLOUR_DARK, ColourEnum.fromCode("d"));
            assertNull(ColourEnum.fromCode("q"));
        }

        @Test
        void aLongerStringIsTreatedAsADisplayName() {
            assertSame(ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.fromCode("Light Blue"));
            assertSame(ColourEnum.COLOUR_MAGENTA_PINK, ColourEnum.fromCode("Magenta-Pink"));
        }

        @Test
        void displayNamesAreMatchedIgnoringCase() {
            assertSame(ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.fromCode("light blue"));
            assertSame(ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.fromCode("LIGHT BLUE"));
        }

        @Test
        void anUnknownNameResolvesToNull() {
            assertNull(ColourEnum.fromCode("Chartreuse"));
        }

        @Test
        void theEmptyStringTakesTheNameRouteAndFindsNothing() {
            // Length 0 is not length 1, so it falls through to the name search rather than
            // reading a character that is not there.
            assertNull(ColourEnum.fromCode(""));
        }

        @ParameterizedTest
        @EnumSource(ColourEnum.class)
        void everyColourIsFoundByItsOwnDisplayName(ColourEnum colour) {
            assertSame(colour, ColourEnum.fromCode(ColourEnum.attributeToString(colour)));
        }
    }

    /**
     * Structural invariants of the transcribed translation tables.
     */
    @Nested
    class TranslationTables {

        @Test
        void everyColourHasADistinctDisplayName() {
            Set<String> names = new HashSet<>();
            for (ColourEnum colour : ColourEnum.values()) {
                names.add(ColourEnum.attributeToString(colour).toLowerCase());
            }

            assertEquals(ColourEnum.values().length, names.size());
        }

        @ParameterizedTest
        @EnumSource(ColourEnum.class)
        void everyEntryInEveryTranslationRowNamesARealColour(ColourEnum colour) {
            // The one failure a transcribed table actually suffers from: a mistyped code,
            // which resolves to null and then propagates to whatever tries to draw with it.
            for (ColourTranslation translation : ColourTranslation.values()) {
                assertNotNull(colour.forTranslation(translation),
                        () -> ColourEnum.attributeToString(colour) + " under " + translation);
            }
        }

        @ParameterizedTest
        @EnumSource(ColourEnum.class)
        void theFullColourTranslationIsTheIdentity(ColourEnum colour) {
            // ATTR_FULL is "draw it as it is": C's get_color() returns the colour without
            // reading the table, and forTranslation does the same.
            assertSame(colour, colour.forTranslation(ColourTranslation.ATTR_FULL));
        }

        @ParameterizedTest
        @EnumSource(ColourEnum.class)
        void everyColourExceptShadeIsFoundByItsOwnCode(ColourEnum colour) throws Exception {
            // fromCode returns the first match, so if two colours shared a code the later one
            // could never round-trip back to itself here - this is the uniqueness check on the
            // codes. Shade is excluded because C's early return for ' ' and '\0' means no code
            // can name it.
            if (colour == ColourEnum.COLOUR_SHADE) return;

            Field code = ColourEnum.class.getDeclaredField("colourCharacter");
            code.setAccessible(true);
            assertSame(colour, ColourEnum.fromCode(code.getChar(colour)));
        }

        @ParameterizedTest
        @EnumSource(ColourEnum.class)
        void theFirstColumnOfEveryRowIsTheColoursOwnCode(ColourEnum colour) throws Exception {
            // Column 0 is never read, but it is what the C table's "full" column holds, so a
            // row that disagrees with its own colour is a transcription slip.
            Field code = ColourEnum.class.getDeclaredField("colourCharacter");
            Field row = ColourEnum.class.getDeclaredField("colourTranslate");
            code.setAccessible(true);
            row.setAccessible(true);
            assertEquals(code.getChar(colour), ((char[]) row.get(colour))[0]);
        }

        @Test
        void theTranslationIndicesAreTheNineTableColumnsInOrder() {
            // These index the char[] rows directly, so a reordering here would silently
            // read the wrong column of every colour.
            assertEquals(0, ColourTranslation.ATTR_FULL.getValue());
            assertEquals(1, ColourTranslation.ATTR_MONO.getValue());
            assertEquals(2, ColourTranslation.ATTR_VGA.getValue());
            assertEquals(3, ColourTranslation.ATTR_BLIND.getValue());
            assertEquals(4, ColourTranslation.ATTR_LIGHT.getValue());
            assertEquals(5, ColourTranslation.ATTR_DARK.getValue());
            assertEquals(6, ColourTranslation.ATTR_HIGH.getValue());
            assertEquals(7, ColourTranslation.ATTR_METAL.getValue());
            assertEquals(8, ColourTranslation.ATTR_MISC.getValue());
        }
    }

    /**
     * Applying a translation, once or repeatedly.
     */
    @Nested
    class Translating {

        @Test
        void aMonochromeDisplayCollapsesColoursOntoWhite() {
            assertSame(ColourEnum.COLOUR_WHITE,
                    ColourEnum.COLOUR_RED.forTranslation(ColourTranslation.ATTR_MONO));
            assertSame(ColourEnum.COLOUR_WHITE,
                    ColourEnum.COLOUR_GREEN.forTranslation(ColourTranslation.ATTR_MONO));
        }

        @Test
        void lightingBrightensAndShadowingDarkens() {
            assertSame(ColourEnum.COLOUR_LIGHT_RED,
                    ColourEnum.COLOUR_RED.forTranslation(ColourTranslation.ATTR_LIGHT));
            assertSame(ColourEnum.COLOUR_SLATE,
                    ColourEnum.COLOUR_RED.forTranslation(ColourTranslation.ATTR_DARK));
        }

        @Test
        void shadeIsUnchangedOnlyByTheFullTranslation() {
            // C's get_color() returns the colour as it is for ATTR_FULL without reading the
            // table. Shade has no row in C's color_table (the entries stop at index 27), so
            // every other translation reads zeros, which is COLOUR_DARK.
            assertSame(ColourEnum.COLOUR_SHADE,
                    ColourEnum.COLOUR_SHADE.forTranslation(ColourTranslation.ATTR_FULL));
            for (ColourTranslation translation : ColourTranslation.values()) {
                if (translation == ColourTranslation.ATTR_FULL) continue;
                assertSame(ColourEnum.COLOUR_DARK,
                        ColourEnum.COLOUR_SHADE.forTranslation(translation),
                        () -> "Shade under " + translation);
            }
        }

        @Test
        void translateColourLeavesShadeAloneUnderTheFullTranslationHoweverManyTimes() {
            assertSame(ColourEnum.COLOUR_SHADE, ColourEnum.translateColour(
                    ColourEnum.COLOUR_SHADE, ColourTranslation.ATTR_FULL, 0));
            assertSame(ColourEnum.COLOUR_SHADE, ColourEnum.translateColour(
                    ColourEnum.COLOUR_SHADE, ColourTranslation.ATTR_FULL, 3));
        }

        @Test
        void translateColourSendsShadeToDarkUnderAnyOtherTranslation() {
            assertSame(ColourEnum.COLOUR_DARK, ColourEnum.translateColour(
                    ColourEnum.COLOUR_SHADE, ColourTranslation.ATTR_DARK, 1));
        }

        @Test
        void translateColourAppliesTheStepTheGivenNumberOfTimes() {
            assertSame(ColourEnum.COLOUR_YELLOW, ColourEnum.translateColour(
                    ColourEnum.COLOUR_WHITE, ColourTranslation.ATTR_LIGHT, 1));
            assertSame(ColourEnum.COLOUR_LIGHT_YELLOW, ColourEnum.translateColour(
                    ColourEnum.COLOUR_WHITE, ColourTranslation.ATTR_LIGHT, 2));
        }

        @Test
        void translatingZeroTimesReturnsTheStartingColour() {
            assertSame(ColourEnum.COLOUR_RED, ColourEnum.translateColour(
                    ColourEnum.COLOUR_RED, ColourTranslation.ATTR_LIGHT, 0));
        }

        @Test
        void translatingANegativeNumberOfTimesIsAlsoANoOp() {
            assertSame(ColourEnum.COLOUR_RED, ColourEnum.translateColour(
                    ColourEnum.COLOUR_RED, ColourTranslation.ATTR_MONO, -3));
        }

        @Test
        void repeatedTranslationSettlesOnAFixedPoint() {
            // Monochrome is idempotent after the first step - white maps to itself - so
            // applying it many times must not wander or fall off into null.
            ColourEnum settled = ColourEnum.translateColour(
                    ColourEnum.COLOUR_RED, ColourTranslation.ATTR_MONO, 10);

            assertSame(ColourEnum.COLOUR_WHITE, settled);
        }

        @ParameterizedTest
        @EnumSource(ColourEnum.class)
        void repeatedTranslationNeverFallsOffIntoNull(ColourEnum colour) {
            for (ColourTranslation translation : ColourTranslation.values()) {
                assertNotNull(ColourEnum.translateColour(colour, translation, 5),
                        () -> ColourEnum.attributeToString(colour) + " under " + translation);
            }
        }
    }
}
