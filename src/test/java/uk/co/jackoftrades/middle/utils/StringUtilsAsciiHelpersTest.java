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

package uk.co.jackoftrades.middle.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the four ASCII helpers ported from C's {@code h-basic.h} and {@code <ctype.h>}:
 * {@code C2I} and {@code I2C} for the macros {@code A2I} ({@code src/h-basic.h:189}) and
 * {@code I2A} ({@code src/h-basic.h:190}), and {@code isAlpha} and {@code isDigit} for the C
 * library's {@code isalpha} and {@code isdigit} as they behave in the {@code "C"} locale the
 * game runs in.
 *
 * <p>Expected values come from the C definitions rather than from the Java: the macros are
 * plain addition and subtraction of {@code 'a'} with no range check, so the out-of-contract
 * cases below assert the arithmetic C would perform, not an error. The classification cases
 * assert the ASCII-only answer C's {@code "C"} locale gives, which is what separates these
 * methods from {@link Character#isLetter} and {@link Character#isDigit}.
 *
 * @author Rowan Crowther
 */
class StringUtilsAsciiHelpersTest {

    /**
     * C's {@code A2I} — letter to zero-based index.
     */
    @Nested
    class C2I {

        @Test
        @DisplayName("the alphabet maps onto 0 to 25")
        void theAlphabetMapsOntoZeroToTwentyFive() {
            assertEquals(0, StringUtils.C2I('a'));
            assertEquals(1, StringUtils.C2I('b'));
            assertEquals(25, StringUtils.C2I('z'));
        }

        @Test
        @DisplayName("characters outside the contract subtract just as C's macro does")
        void charactersOutsideTheContractStillSubtract() {
            // 'A' is 65, 'a' is 97, so C's ((X) - 'a') gives -32; likewise '0' is 48, giving -49.
            assertEquals(-32, StringUtils.C2I('A'));
            assertEquals(-49, StringUtils.C2I('0'));
            // '{' is the character immediately after 'z'.
            assertEquals(26, StringUtils.C2I('{'));
        }
    }

    /**
     * C's {@code I2A} — zero-based index to letter.
     */
    @Nested
    class I2C {

        @Test
        @DisplayName("0 to 25 maps onto the alphabet")
        void zeroToTwentyFiveMapsOntoTheAlphabet() {
            assertEquals('a', StringUtils.I2C(0));
            assertEquals('b', StringUtils.I2C(1));
            assertEquals('z', StringUtils.I2C(25));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 7, 13, 25})
        @DisplayName("I2C undoes C2I across the contract's range")
        void i2cUndoesC2IAcrossTheRange(int index) {
            assertEquals(index, StringUtils.C2I(StringUtils.I2C(index)));
        }

        @Test
        @DisplayName("an index past the alphabet adds on regardless, as C's macro does")
        void anIndexPastTheAlphabetAddsOnRegardless() {
            assertEquals('{', StringUtils.I2C(26));
            assertEquals('A', StringUtils.I2C(-32));
        }
    }

    /**
     * C's {@code isalpha} in the {@code "C"} locale.
     */
    @Nested
    class IsAlpha {

        @ParameterizedTest
        @ValueSource(chars = {'a', 'b', 'm', 'y', 'z', 'A', 'B', 'M', 'Y', 'Z'})
        @DisplayName("ASCII letters of both cases are letters")
        void asciiLettersOfBothCasesAreLetters(char letter) {
            assertTrue(StringUtils.isAlpha(letter));
        }

        @ParameterizedTest
        @ValueSource(chars = {'0', '9', ' ', '\n', '_', '@', '[', '`', '{', '\0'})
        @DisplayName("digits, punctuation and control characters are not letters")
        void nonLettersAreRejected(char notALetter) {
            assertFalse(StringUtils.isAlpha(notALetter));
        }

        @Test
        @DisplayName("the four characters bracketing the two ranges are excluded")
        void theBracketingCharactersAreExcluded() {
            // '@' and '[' sit either side of 'A'-'Z'; '`' and '{' either side of 'a'-'z'.
            assertFalse(StringUtils.isAlpha('@'));
            assertTrue(StringUtils.isAlpha('A'));
            assertTrue(StringUtils.isAlpha('Z'));
            assertFalse(StringUtils.isAlpha('['));
            assertFalse(StringUtils.isAlpha('`'));
            assertTrue(StringUtils.isAlpha('a'));
            assertTrue(StringUtils.isAlpha('z'));
            assertFalse(StringUtils.isAlpha('{'));
        }

        @Test
        @DisplayName("non-ASCII letters are not letters, unlike Character.isLetter")
        void nonAsciiLettersAreRejected() {
            // Written numerically so the assertion does not depend on the source encoding:
            // 0xE9 is e-acute and 0x03B1 is Greek alpha. C's isalpha in the "C" locale says no
            // to both; Character.isLetter would say yes, which is why it is not used.
            assertFalse(StringUtils.isAlpha((char) 0xE9));
            assertFalse(StringUtils.isAlpha((char) 0x03B1));
        }
    }

    /**
     * C's {@code isdigit} in the {@code "C"} locale.
     */
    @Nested
    class IsDigit {

        @ParameterizedTest
        @ValueSource(chars = {'0', '1', '4', '8', '9'})
        @DisplayName("the ASCII ten are digits")
        void theAsciiTenAreDigits(char digit) {
            assertTrue(StringUtils.isDigit(digit));
        }

        @ParameterizedTest
        @ValueSource(chars = {'a', 'A', 'f', 'F', ' ', '-', '.', '\0'})
        @DisplayName("letters, signs and separators are not digits")
        void nonDigitsAreRejected(char notADigit) {
            assertFalse(StringUtils.isDigit(notADigit));
        }

        @Test
        @DisplayName("the two characters bracketing the range are excluded")
        void theBracketingCharactersAreExcluded() {
            // '/' and ':' sit either side of '0'-'9'.
            assertFalse(StringUtils.isDigit('/'));
            assertTrue(StringUtils.isDigit('0'));
            assertTrue(StringUtils.isDigit('9'));
            assertFalse(StringUtils.isDigit(':'));
        }

        @Test
        @DisplayName("non-ASCII digits are not digits, unlike Character.isDigit")
        void nonAsciiDigitsAreRejected() {
            // 0x0660 is Arabic-Indic digit zero and 0x0966 Devanagari digit zero. C's isdigit
            // in the "C" locale says no to both; Character.isDigit would say yes.
            assertFalse(StringUtils.isDigit((char) 0x0660));
            assertFalse(StringUtils.isDigit((char) 0x0966));
        }
    }
}
