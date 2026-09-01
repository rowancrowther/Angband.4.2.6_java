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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@code PlayerName.findRomanSuffixStart} — the port of C's
 * {@code find_roman_suffix_start} ({@code src/player-birth.c:1335}), which locates the Roman
 * numeral on the end of a player name so a quickstart birth can increment it.
 *
 * <p><b>Every expectation here is read off the C, not off the port.</b> The C is four lines of
 * pointer work and each line has a case that distinguishes it:
 *
 * <ul>
 *   <li>{@code strrchr(buf, ' ')} — the <em>last</em> space, not the first, so a name of three
 *       or more words is cut at the right place.</li>
 *   <li>{@code start++} — the suffix begins after the space, so a returned suffix never carries
 *       one. A port that forgets this rejects every name, because a space is not a numeral.</li>
 *   <li>{@code while (*p)} — every character of the suffix is checked, and only the terminator
 *       ends the walk. A port that stops one short accepts {@code "Fred IIQ"}.</li>
 *   <li>{@code start = NULL} on the first bad character — failure is a null pointer, and it is
 *       distinct from the empty suffix a name ending in a space produces.</li>
 * </ul>
 *
 * <p><b>Why the empty suffix is pinned as a success.</b> When the name ends in a space, C's
 * incremented pointer lands on the terminator, the {@code while} body never runs, and a
 * non-{@code NULL} pointer to an empty string comes back. C's caller ({@code player-birth.c:1063})
 * branches on {@code if (buf)}, so that empty result is treated as a suffix to increment —
 * {@code roman_to_int("")} then returns -1 and the player is told the suffix could not be dealt
 * with. Collapsing the empty string into the failure value would silently change that path, so
 * {@link EmptySuffix} keeps the two apart.
 *
 * <p><b>Case.</b> C compares against upper-case letters only, so a lower-cased numeral is not a
 * suffix. That is tested rather than assumed, because it is the kind of thing a port
 * "helpfully" relaxes.
 *
 * <p>Class PlayerNameFindRomanSuffixStartTest coded on 260831, commented in full on 260831.
 */
@DisplayName("PlayerName.findRomanSuffixStart — C find_roman_suffix_start")
public class PlayerNameFindRomanSuffixStartTest {
    /**
     * Calls the private method under test with {@code name}.
     *
     * <p>The method is private in C too — it is exported from {@code player-birth.c} but has one
     * caller — so reflection is the honest way in rather than a reason to widen the port.
     */
    private String find(String name) throws Exception {
        Method method = PlayerName.class.getDeclaredMethod("findRomanSuffixStart", String.class);
        method.setAccessible(true);
        try {
            return (String) method.invoke(new PlayerName(), name);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    @Nested
    @DisplayName("A name with a valid numeral suffix")
    class ValidSuffix {
        /**
         * The ordinary case: C finds the space, steps past it, walks {@code "III"} without
         * meeting a non-numeral, and returns the position of the first {@code I}.
         */
        @Test
        @DisplayName("returns the numerals after the last space")
        void ordinary() throws Exception {
            assertEquals("III", find("Fred III"));
        }

        /**
         * All seven symbols C accepts, in one suffix. It is not a well-formed numeral, and C
         * does not care — the check is per-character membership of {@code "IVXLCDM"}, nothing
         * more. C's own comment on {@code roman_to_int} concedes it parses nonsense.
         */
        @Test
        @DisplayName("accepts every symbol in the set, well-formed or not")
        void everySymbol() throws Exception {
            assertEquals("MDCLXVI", find("Fred MDCLXVI"));
        }

        /**
         * Three words. {@code strrchr} finds the <em>last</em> space, so the suffix is
         * {@code "X"} and the surname is not part of it. A port that cut at the first space
         * would see {@code "de Vere X"} and reject it on the {@code d}.
         */
        @Test
        @DisplayName("cuts at the last space, not the first")
        void lastSpaceWins() throws Exception {
            assertEquals("X", find("Fred de Vere X"));
        }

        /**
         * Two spaces in a row. C only ever looks at the last one, so the empty word between
         * them is invisible and the suffix is {@code "II"}.
         */
        @Test
        @DisplayName("ignores a run of spaces before the suffix")
        void repeatedSpaces() throws Exception {
            assertEquals("II", find("Fred  II"));
        }

        /**
         * A leading space and nothing else. C's {@code strrchr} finds it at index 0, steps to
         * index 1, and the whole rest of the string is the suffix.
         */
        @Test
        @DisplayName("treats a leading space as the cut when it is the only one")
        void leadingSpace() throws Exception {
            assertEquals("III", find(" III"));
        }

        /**
         * A single-character suffix, which is where an off-by-one in the walk hides best: check
         * one character too few and the sole character goes unexamined.
         */
        @Test
        @DisplayName("checks a one-character suffix")
        void singleCharacter() throws Exception {
            assertEquals("I", find("Fred I"));
        }
    }

    @Nested
    @DisplayName("A name whose trailing word is not all numerals")
    class InvalidSuffix {
        /**
         * The bad character is last. C's walk only stops at the terminator, so the {@code Q} is
         * reached and the result is {@code NULL}. A walk that stopped one character short would
         * return {@code "IIQ"} here, and this is the test that says so.
         */
        @Test
        @DisplayName("rejects a bad character in the last position")
        void badCharacterLast() throws Exception {
            assertNull(find("Fred IIQ"));
        }

        /**
         * The bad character is first, so C fails on the very first pass of the loop.
         */
        @Test
        @DisplayName("rejects a bad character in the first position")
        void badCharacterFirst() throws Exception {
            assertNull(find("Fred QII"));
        }

        /**
         * An ordinary two-word name. Nothing about the surname is numeral-like, so there is no
         * suffix, which is the common case at birth.
         */
        @Test
        @DisplayName("rejects a plain surname")
        void plainSurname() throws Exception {
            assertNull(find("Fred Bloggs"));
        }

        /**
         * Lower case. C compares against {@code 'I'}, {@code 'V'}, {@code 'X'} and the rest as
         * upper-case literals, so {@code "iii"} is three bad characters.
         */
        @Test
        @DisplayName("rejects lower-case numerals")
        void lowerCase() throws Exception {
            assertNull(find("Fred iii"));
        }

        /**
         * A digit, not a letter. The Arabic form is not what C is looking for.
         */
        @Test
        @DisplayName("rejects an Arabic numeral")
        void arabicNumeral() throws Exception {
            assertNull(find("Fred 3"));
        }
    }

    @Nested
    @DisplayName("A name with no space")
    class NoSpace {
        /**
         * {@code strrchr} returns {@code NULL} and C returns it unchanged, without looking at
         * the name at all. The name being pure numerals makes no difference — the space is what
         * marks a suffix.
         */
        @Test
        @DisplayName("returns null for a single word")
        void singleWord() throws Exception {
            assertNull(find("Fred"));
        }

        /**
         * A one-word name made entirely of numeral letters. Still no space, so still no suffix;
         * this is the case a port keyed on the characters rather than the space would get wrong.
         */
        @Test
        @DisplayName("returns null for a bare numeral with no space")
        void bareNumeral() throws Exception {
            assertNull(find("III"));
        }

        /**
         * The empty name. {@code strrchr("", ' ')} is {@code NULL}.
         */
        @Test
        @DisplayName("returns null for an empty name")
        void emptyName() throws Exception {
            assertNull(find(""));
        }
    }

    @Nested
    @DisplayName("A name ending in a space")
    class EmptySuffix {
        /**
         * C finds the trailing space, steps onto the terminator, and the {@code while} body
         * never runs — so a non-{@code NULL} pointer to an empty string comes back. The
         * assertion is deliberately in two halves: the result must be empty <em>and</em> must
         * not be null, because C's caller distinguishes them and this is the only input that
         * produces the empty one.
         */
        @Test
        @DisplayName("returns an empty suffix, not null")
        void trailingSpace() throws Exception {
            String result = find("Fred ");
            assertNotNull(result);
            assertEquals("", result);
        }

        /**
         * A name that is nothing but a space. Same reasoning: the last space is at index 0, and
         * what follows it is empty.
         */
        @Test
        @DisplayName("returns an empty suffix for a name that is only a space")
        void onlySpace() throws Exception {
            String result = find(" ");
            assertNotNull(result);
            assertEquals("", result);
        }

        /**
         * A valid suffix followed by a space. C cuts at the <em>last</em> space, so the
         * {@code "IV"} is behind the cut and the suffix is empty, not {@code "IV"}.
         */
        @Test
        @DisplayName("returns an empty suffix when a numeral is followed by a space")
        void numeralThenSpace() throws Exception {
            String result = find("Fred IV ");
            assertNotNull(result);
            assertEquals("", result);
        }
    }
}
