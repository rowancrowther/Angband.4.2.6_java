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
 * Tests {@code PlayerBirth.intToRoman(int)}, the port of C's {@code int_to_roman}
 * ({@code player-birth.c:1379-1424}). The method is private and static, so every test reaches it
 * through reflection.
 *
 * <p>The expected values throughout come from hand-tracing C's {@code int_to_roman}, not from the
 * port. In particular, every exact-symbol-value case below ({@code 1}, {@code 4}, {@code 5},
 * {@code 1000}, and so on) pins the boundary an earlier draft of the port got wrong: that draft
 * used a strict {@code value > romanSymbolValues[index]} test rather than C's inclusive
 * {@code n >= roman_symbol_values[i]} (visible in C as the negation of its
 * {@code while (n < roman_symbol_values[i])} loop condition). With the strict test, no exact
 * match was ever consumed, so the remainder could never reach {@code 0}, and every positive input
 * eventually exhausted the symbol table and answered the empty string instead of a numeral.
 *
 * @author Rowan Crowther
 */
class PlayerBirthIntToRomanTest {

    private static String intToRoman(int value) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("intToRoman", int.class);
        method.setAccessible(true);
        return (String) method.invoke(null, value);
    }

    @Nested
    @DisplayName("intToRoman(int) - no zero or negative numerals")
    class NonPositiveTests {

        @Test
        @DisplayName("zero answers the empty string, matching C's n < 1 guard")
        void zeroAnswersEmptyString() throws Exception {
            assertEquals("", intToRoman(0));
        }

        @Test
        @DisplayName("a negative value answers the empty string, matching C's n < 1 guard")
        void negativeAnswersEmptyString() throws Exception {
            assertEquals("", intToRoman(-1));
            assertEquals("", intToRoman(-1000));
        }
    }

    @Nested
    @DisplayName("intToRoman(int) - exact-symbol-value boundary")
    class ExactSymbolValueTests {

        @Test
        @DisplayName("each plain symbol's own value answers that single symbol - the bug where "
                + "an exact match was never consumed")
        void plainSymbolExactValueAnswersItself() throws Exception {
            assertEquals("I", intToRoman(1));
            assertEquals("V", intToRoman(5));
            assertEquals("X", intToRoman(10));
            assertEquals("L", intToRoman(50));
            assertEquals("C", intToRoman(100));
            assertEquals("D", intToRoman(500));
            assertEquals("M", intToRoman(1000));
        }

        @Test
        @DisplayName("each subtractive pair's own value answers that pair - the same "
                + "exact-match boundary, one symbol-table row down")
        void subtractivePairExactValueAnswersItself() throws Exception {
            assertEquals("IV", intToRoman(4));
            assertEquals("IX", intToRoman(9));
            assertEquals("XL", intToRoman(40));
            assertEquals("XC", intToRoman(90));
            assertEquals("CD", intToRoman(400));
            assertEquals("CM", intToRoman(900));
        }
    }

    @Nested
    @DisplayName("intToRoman(int) - composite values")
    class CompositeValueTests {

        @Test
        @DisplayName("repeated plain symbols accumulate, re-trying the same symbol after a match")
        void repeatedPlainSymbolsAccumulate() throws Exception {
            assertEquals("II", intToRoman(2));
            assertEquals("III", intToRoman(3));
        }

        @Test
        @DisplayName("a value spanning several symbol rows combines plain and subtractive forms")
        void mixedValueCombinesPlainAndSubtractiveForms() throws Exception {
            assertEquals("LVIII", intToRoman(58));
            assertEquals("MCM", intToRoman(1900));
            assertEquals("MCMXCIV", intToRoman(1994));
        }

        @Test
        @DisplayName("the largest traditionally-representable value repeats M three times, "
                + "exercising the index reset after every match")
        void largestTraditionalValueRepeatsLeadingSymbol() throws Exception {
            assertEquals("MMMCMXCIX", intToRoman(3999));
        }
    }
}
