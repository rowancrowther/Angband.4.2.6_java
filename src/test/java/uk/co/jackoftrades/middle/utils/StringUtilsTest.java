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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link StringUtils}, the port of the C source's string helpers
 * ({@code src/z-util.c} and {@code src/z-form.c}).
 *
 * <p>Most of these methods exist only because C has no {@link String} type: the original
 * works over {@code char *} buffers and needs explicit length-bounded copies, concatenation
 * and comparison. The Java ports keep the original names and shapes so ported call sites
 * read the same, which means several of them carry over C conventions that are surprising
 * in isolation - most notably the {@code max}-suffixed formatters, which return
 * {@code max - 1} characters because in C the last byte of the buffer holds the
 * terminating NUL. That off-by-one is deliberate and is pinned here so a future
 * "tidy-up" cannot quietly change the length every ported call site depends on.
 *
 * <p>{@link StringUtils#strICmp} and {@link StringUtils#strNICmp} are documented as
 * returning -1/0/1 but delegate to {@link String#compareToIgnoreCase}, which returns the
 * character difference. The tests therefore assert on the <em>sign</em>, which is the part
 * every caller actually uses and the part the C {@code strcmp} contract guarantees.
 *
 * @author Rowan Crowther
 */
class StringUtilsTest {

    /**
     * Formatting and concatenation.
     */
    @Nested
    class Formatting {

        @Test
        void strnfcatAppendsTheFormattedTailToTheHead() {
            assertEquals("head: 3 of orc", StringUtils.strnfcat("head: ", "%d of %s", 3, "orc"));
        }

        @Test
        void strnfcatWithNoPlaceholdersIsAPlainConcatenation() {
            assertEquals("ab", StringUtils.strnfcat("a", "b"));
        }

        @Test
        void formatAndVformatAreStraightDelegationsToStringFormat() {
            assertEquals("2 rings", StringUtils.format("%d %s", 2, "rings"));
            assertEquals("2 rings", StringUtils.vformat("%d %s", 2, "rings"));
        }

        @Test
        void theBoundedFormattersKeepMaxMinusOneCharacters() {
            // The C original reserves the final byte of the buffer for the NUL terminator,
            // so "max" characters of buffer holds max - 1 characters of text.
            assertEquals("hell", StringUtils.vstrnfmt(5, "hello world"));
            assertEquals("hell", StringUtils.strnfmt("hello world", 5));
        }

        @Test
        void theBoundedFormattersSubstituteBeforeTheyTruncate() {
            assertEquals("orcs", StringUtils.vstrnfmt(5, "%s and gnomes", "orcs"));
            assertEquals("orcs", StringUtils.strnfmt("%s and gnomes", 5, "orcs"));
        }
    }

    /**
     * Pluralisation and agreement.
     */
    @Nested
    class Agreement {

        @Test
        void oneItemTakesNoPluralSuffix() {
            assertEquals("", StringUtils.plural(1));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 2, 17, -1})
        void anythingOtherThanOneTakesAnS(int number) {
            // Zero and negatives pluralise too - "0 potions", not "0 potion".
            assertEquals("s", StringUtils.plural(number));
        }

        @Test
        void verbAgreementPicksTheSingularOnlyForOne() {
            assertEquals("is", StringUtils.verbAgreement(1, "is", "are"));
            assertEquals("are", StringUtils.verbAgreement(0, "is", "are"));
            assertEquals("are", StringUtils.verbAgreement(2, "is", "are"));
        }
    }

    /**
     * Copying, clipping and concatenating with a length bound.
     */
    @Nested
    class BoundedCopies {

        @Test
        void clipToTakesTheFirstNCharacters() {
            assertEquals("hel", StringUtils.clipTo("hello", 3));
            assertEquals("", StringUtils.clipTo("hello", 0));
            assertEquals("hello", StringUtils.clipTo("hello", 5));
        }

        @Test
        void strCpyTakesTheFirstNCharacters() {
            assertEquals("hel", StringUtils.strCpy("hello", 3));
        }

        @Test
        void strCpyStopsAtTheEndOfAShortSource() {
            // Unlike clipTo, strCpy clamps rather than running off the end - it is the
            // bounded copy, so an over-long request is satisfied by whatever is there.
            assertEquals("hello", StringUtils.strCpy("hello", 99));
        }

        @Test
        void strCpyOfANegativeLengthIsEmpty() {
            assertEquals("", StringUtils.strCpy("hello", -1));
        }

        @Test
        void strCatJoinsThenTruncates() {
            assertEquals("abc", StringUtils.strCat("ab", "cd", 3));
        }

        @Test
        void strCatReturnsEverythingWhenTheBoundIsGenerous() {
            assertEquals("abcd", StringUtils.strCat("ab", "cd", 99));
        }

        @Test
        void strCatOfANegativeSizeIsEmpty() {
            assertEquals("", StringUtils.strCat("ab", "cd", -1));
        }

        @Test
        void strCatWithEmptyOperands() {
            assertEquals("cd", StringUtils.strCat("", "cd", 99));
            assertEquals("ab", StringUtils.strCat("ab", "", 99));
        }
    }

    /**
     * Comparison and search.
     */
    @Nested
    class Comparison {

        @Test
        void streqIsPlainCaseSensitiveEquality() {
            assertTrue(StringUtils.streq("orc", "orc"));
            assertFalse(StringUtils.streq("orc", "Orc"));
            assertTrue(StringUtils.streq("", ""));
        }

        @Test
        void strICmpIgnoresCaseAndOrdersAlphabetically() {
            assertEquals(0, StringUtils.strICmp("Orc", "orc"));
            assertTrue(StringUtils.strICmp("apple", "Banana") < 0);
            assertTrue(StringUtils.strICmp("Banana", "apple") > 0);
        }

        @Test
        void strICmpOrdersAPrefixBeforeTheLongerString() {
            assertTrue(StringUtils.strICmp("orc", "orcish") < 0);
        }

        @Test
        void strNICmpComparesOnlyTheFirstNCharacters() {
            // The strings differ from the third character on, but the first two match.
            assertEquals(0, StringUtils.strNICmp("apples", "APRICOT", 2));
            assertTrue(StringUtils.strNICmp("apples", "apricot", 3) < 0);
        }

        @Test
        void strNICmpNeverReadsPastTheShorterString() {
            // n exceeds both lengths; the comparison is clamped rather than throwing.
            assertEquals(0, StringUtils.strNICmp("or", "OR", 99));
            assertEquals(0, StringUtils.strNICmp("or", "orcish", 99));
        }

        @Test
        void strIStrReturnsTheTailFromTheFirstCaseInsensitiveMatch() {
            assertEquals("World", StringUtils.strIStr("Hello World", "world"));
            assertEquals("Hello World", StringUtils.strIStr("Hello World", "hello"));
        }

        @Test
        void strIStrFallsBackToTheNeedleWhenThereIsNoMatch() {
            // A miss returns the search term itself rather than null or an empty string.
            assertEquals("dwarf", StringUtils.strIStr("Hello World", "dwarf"));
        }

        @Test
        void textSchrFindsACharacterAnywhereInTheString() {
            assertTrue(StringUtils.textSchr("hello", 'h'));
            assertTrue(StringUtils.textSchr("hello", 'o'));
            assertFalse(StringUtils.textSchr("hello", 'z'));
            assertFalse(StringUtils.textSchr("", 'a'));
        }

        @Test
        void stringLengthCountsCharacters() {
            assertEquals(5, StringUtils.stringLength("hello"));
            assertEquals(0, StringUtils.stringLength(""));
        }
    }

    /**
     * Prefix and suffix tests.
     */
    @Nested
    class Affixes {

        @Test
        void suffixMatchesTheEndOfTheString() {
            assertTrue(StringUtils.suffix("longsword", "sword"));
            assertFalse(StringUtils.suffix("longsword", "long"));
        }

        @Test
        void suffixIsCaseSensitiveButSuffixIIsNot() {
            assertFalse(StringUtils.suffix("longsword", "SWORD"));
            assertTrue(StringUtils.suffixI("longsword", "SWORD"));
            assertTrue(StringUtils.suffixI("LONGSWORD", "sword"));
        }

        @Test
        void aWholeStringIsASuffixOfItself() {
            assertTrue(StringUtils.suffix("sword", "sword"));
        }

        @Test
        void anEmptySuffixAlwaysMatches() {
            assertTrue(StringUtils.suffix("sword", ""));
            assertTrue(StringUtils.suffixI("sword", ""));
        }

        @Test
        void anEmptyStringHasNoSuffixAtAll() {
            // The empty-string guard fires before the empty-suffix one, so even "" is not a
            // suffix of "" here - a deliberate asymmetry worth pinning.
            assertFalse(StringUtils.suffix("", ""));
        }

        @Test
        void aSuffixLongerThanTheStringCannotMatch() {
            assertFalse(StringUtils.suffix("axe", "battleaxe"));
        }

        @Test
        void prefixMatchesTheStartOfTheString() {
            assertTrue(StringUtils.prefix("longsword", "long"));
            assertFalse(StringUtils.prefix("longsword", "sword"));
        }

        @Test
        void prefixIsCaseSensitiveButPrefixIIsNot() {
            assertFalse(StringUtils.prefix("longsword", "LONG"));
            assertTrue(StringUtils.prefixI("longsword", "LONG"));
            assertTrue(StringUtils.prefixI("LONGSWORD", "long"));
        }

        @Test
        void anEmptyPrefixAlwaysMatchesAndAWholeStringIsItsOwnPrefix() {
            assertTrue(StringUtils.prefix("sword", ""));
            assertTrue(StringUtils.prefix("", ""));
            assertTrue(StringUtils.prefix("sword", "sword"));
        }
    }

    /**
     * Character classification and hexadecimal conversion.
     */
    @Nested
    class CharactersAndNumbers {

        @Test
        void isPrintReturnsPrintableAsciiUnchanged() {
            assertEquals('A', StringUtils.isPrint('A'));
            assertEquals(' ', StringUtils.isPrint(' '));
            assertEquals('~', StringUtils.isPrint('~'));
        }

        @Test
        void isPrintReturnsNulForControlAndNonAsciiCharacters() {
            assertEquals('\0', StringUtils.isPrint('\n'));
            assertEquals('\0', StringUtils.isPrint('\t'));
            // Written numerically rather than as a literal so the assertion does not depend
            // on the compiler's source encoding: 0xE9 is e-acute, outside printable ASCII.
            assertEquals('\0', StringUtils.isPrint((char) 0xE9));
        }

        @ParameterizedTest
        @ValueSource(chars = {'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'})
        void vowelsAreRecognisedInEitherCase(char vowel) {
            assertTrue(StringUtils.isVowel(vowel));
        }

        @ParameterizedTest
        @ValueSource(chars = {'b', 'z', 'Y', '1', ' '})
        void nonVowelsAreRejected(char notAVowel) {
            assertFalse(StringUtils.isVowel(notAVowel));
        }

        @Test
        void hexDigitsConvertInEitherCase() {
            assertEquals(0, StringUtils.hexCharToInt('0'));
            assertEquals(9, StringUtils.hexCharToInt('9'));
            assertEquals(10, StringUtils.hexCharToInt('A'));
            assertEquals(15, StringUtils.hexCharToInt('F'));
            assertEquals(10, StringUtils.hexCharToInt('a'));
            assertEquals(15, StringUtils.hexCharToInt('f'));
        }

        @Test
        void nonHexCharactersConvertToMinusOne() {
            assertEquals(-1, StringUtils.hexCharToInt('g'));
            assertEquals(-1, StringUtils.hexCharToInt('G'));
            assertEquals(-1, StringUtils.hexCharToInt(' '));
        }

        @Test
        void hexStringsConvertInEitherCase() {
            assertEquals(255, StringUtils.hexStrToInt("ff"));
            assertEquals(255, StringUtils.hexStrToInt("FF"));
            assertEquals(0, StringUtils.hexStrToInt("0"));
            assertEquals(4095, StringUtils.hexStrToInt("fff"));
        }

        @Test
        void aMalformedHexStringConvertsToMinusOne() {
            assertEquals(-1, StringUtils.hexStrToInt("zz"));
            assertEquals(-1, StringUtils.hexStrToInt(""));
            assertEquals(-1, StringUtils.hexStrToInt("0x10"));
        }

        @Test
        void containsOnlySpacesTreatsAllWhitespaceAlike() {
            assertTrue(StringUtils.containsOnlySpaces(""));
            assertTrue(StringUtils.containsOnlySpaces("   "));
            assertTrue(StringUtils.containsOnlySpaces(" \t\n "));
            assertFalse(StringUtils.containsOnlySpaces(" a "));
        }
    }

    /**
     * Capitalisation and escape-aware character removal.
     */
    @Nested
    class Rewriting {

        @Test
        void strCapUppercasesTheFirstCharacterOnly() {
            assertEquals("Hello world", StringUtils.strCap("hello world"));
            assertEquals("Hello", StringUtils.strCap("Hello"));
        }

        @Test
        void strCapLeavesNonLettersAndTheEmptyStringAlone() {
            assertEquals("", StringUtils.strCap(""));
            assertEquals("1st", StringUtils.strCap("1st"));
        }

        @Test
        void strSkipRemovesRunsOfTheTargetCharacter() {
            assertEquals("abc", StringUtils.strSkip("axxbxc", 'x', 'q'));
        }

        @Test
        void strSkipSpareTheOccurrenceThatFollowsTheEscapeCharacter() {
            // The 'x' after 'q' is escaped and survives; the unescaped one later does not.
            assertEquals("aqxbc", StringUtils.strSkip("aqxbxc", 'x', 'q'));
        }

        @Test
        void strSkipLeavesAStringWithNoOccurrencesUntouched() {
            assertEquals("abc", StringUtils.strSkip("abc", 'x', 'q'));
        }
    }
}
