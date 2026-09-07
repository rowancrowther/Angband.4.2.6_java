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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code PlayerBirth.value(char)} and {@code PlayerBirth.romanToInt(String)}, the port of
 * C's single-letter lookup embedded in {@code roman_token_vals[*][0]} and of
 * {@code roman_to_int} itself ({@code player-birth.c:1439-1492}). Both methods are private and
 * static, so every test reaches them through reflection.
 *
 * <p>The expected values throughout come from hand-tracing C's {@code roman_to_int}, not from the
 * port — in particular the six-pair restriction below, which a naive "smaller value before a
 * larger one subtracts" implementation would get wrong.
 *
 * <p><b>The subtraction is a fixed set of six pairs, not a general rule.</b> C's
 * {@code roman_token_chr2} only lets {@code I}, {@code X} and {@code C} lead a subtractive pair,
 * and only with one of two specific followers each ({@code IV IX}, {@code XL XC}, {@code CD CM}).
 * {@code M}, {@code D}, {@code L} and {@code V} have no {@code chr2} entry at all and so can never
 * lead a pair, however much larger the next letter is. {@code "VX"} is therefore {@code 15} in C
 * (V added alone, then X added alone), not {@code 5} (which a general magnitude rule would give by
 * treating V as subtracted from X) — this is the exact case an earlier draft of the port got wrong
 * by comparing raw magnitudes instead of checking against this fixed pair set.
 *
 * <p>An earlier draft of {@code romanToInt} also never added a character's value unless it had a
 * following character to look ahead at, which meant the <em>last</em> character of any string, and
 * any {@code I}/{@code X}/{@code C} not followed by its specific partner, contributed nothing.
 * {@code romanToIntOfSingleLetterMatchesValue} and {@code romanToIntOfRepeatedNonPairLetters} pin
 * exactly the inputs that draft got wrong.
 *
 * @author Rowan Crowther
 */
class PlayerBirthRomanToIntTest {

    private static int value(char roman) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("value", char.class);
        method.setAccessible(true);
        return (int) method.invoke(null, roman);
    }

    private static int romanToInt(String roman) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("romanToInt", String.class);
        method.setAccessible(true);
        return (int) method.invoke(null, roman);
    }

    @Nested
    @DisplayName("value(char)")
    class ValueTests {

        @Test
        @DisplayName("each of the seven canonical letters answers its Roman value")
        void canonicalLettersAnswerTheirValue() throws Exception {
            assertEquals(1, value('I'));
            assertEquals(5, value('V'));
            assertEquals(10, value('X'));
            assertEquals(50, value('L'));
            assertEquals(100, value('C'));
            assertEquals(500, value('D'));
            assertEquals(1000, value('M'));
        }

        @Test
        @DisplayName("a letter outside I V X L C D M answers -1")
        void unrecognisedLetterAnswersMinusOne() throws Exception {
            assertEquals(-1, value('A'));
            assertEquals(-1, value('Z'));
        }

        @Test
        @DisplayName("a lowercase letter answers -1, matching C's uppercase-only table")
        void lowercaseLetterAnswersMinusOne() throws Exception {
            assertEquals(-1, value('i'));
            assertEquals(-1, value('x'));
        }
    }

    @Nested
    @DisplayName("romanToInt(String) - malformed input")
    class MalformedInputTests {

        @Test
        @DisplayName("an empty string answers -1, matching C's strlen(roman) == 0 check")
        void emptyStringAnswersMinusOne() throws Exception {
            assertEquals(-1, romanToInt(""));
        }

        @Test
        @DisplayName("a letter outside I V X L C D M anywhere in the string answers -1")
        void invalidLetterAnswersMinusOne() throws Exception {
            assertEquals(-1, romanToInt("IA"));
            assertEquals(-1, romanToInt("AI"));
        }

        @Test
        @DisplayName("a lowercase numeral answers -1, since neither version case-folds")
        void lowercaseNumeralAnswersMinusOne() throws Exception {
            assertEquals(-1, romanToInt("iv"));
        }
    }

    @Nested
    @DisplayName("romanToInt(String) - the last-character and no-pair boundary")
    class BoundaryTests {

        @Test
        @DisplayName("a single letter answers its own value - the bug where the last "
                + "character was never added")
        void romanToIntOfSingleLetterMatchesValue() throws Exception {
            assertEquals(1, romanToInt("I"));
            assertEquals(5, romanToInt("V"));
            assertEquals(100, romanToInt("C"));
        }

        @Test
        @DisplayName("repeated letters with no subtractive pair sum plainly, "
                + "including the trailing one")
        void romanToIntOfRepeatedNonPairLetters() throws Exception {
            assertEquals(2, romanToInt("II"));
            assertEquals(3, romanToInt("III"));
            assertEquals(20, romanToInt("XX"));
        }

        @Test
        @DisplayName("a leading letter followed by a non-partner letter adds both plainly")
        void nonPartnerFollowerAddsBothPlainly() throws Exception {
            assertEquals(6, romanToInt("VI"));
            assertEquals(11, romanToInt("XI"));
        }
    }

    @Nested
    @DisplayName("romanToInt(String) - the six canonical subtractive pairs")
    class SubtractivePairTests {

        @Test
        @DisplayName("each of IV IX XL XC CD CM subtracts")
        void canonicalPairsSubtract() throws Exception {
            assertEquals(4, romanToInt("IV"));
            assertEquals(9, romanToInt("IX"));
            assertEquals(40, romanToInt("XL"));
            assertEquals(90, romanToInt("XC"));
            assertEquals(400, romanToInt("CD"));
            assertEquals(900, romanToInt("CM"));
        }

        @Test
        @DisplayName("a full numeral combines pairs and plain letters in C's order")
        void fullNumeralCombinesPairsAndPlainLetters() throws Exception {
            assertEquals(1994, romanToInt("MCMXCIV"));
            assertEquals(1900, romanToInt("MCM"));
        }
    }

    @Nested
    @DisplayName("romanToInt(String) - letters C never lets lead a pair")
    class NonPairLeadingLetterTests {

        @Test
        @DisplayName("V before a larger letter never subtracts - V has no chr2 entry in C")
        void vNeverLeadsAPair() throws Exception {
            assertEquals(15, romanToInt("VX"));
        }

        @Test
        @DisplayName("L before a larger letter never subtracts - L has no chr2 entry in C")
        void lNeverLeadsAPair() throws Exception {
            assertEquals(150, romanToInt("LC"));
        }

        @Test
        @DisplayName("I before a letter outside its two specific partners adds plainly, not "
                + "the general-magnitude difference")
        void iOnlySubtractsAgainstItsOwnTwoPartners() throws Exception {
            assertEquals(101, romanToInt("IC"));
        }
    }
}
