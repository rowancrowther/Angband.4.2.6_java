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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code PlayerName.playerSafeName} — the port of C's {@code player_safe_name}
 * ({@code src/player.c:392}), which reduces a player name to something that can be used as a
 * savefile name.
 *
 * <p><b>Every expectation here is read off the C, not off the port.</b> The C is short but it
 * has four distinct pieces of arithmetic, and each of them has a case that pins it:
 *
 * <ul>
 *   <li>{@code limit = suffix - name - 1} — a pointer difference, with the {@code -1} throwing
 *       away the space in front of the numerals. Off by one here leaves a trailing space or
 *       eats the last letter of the name.</li>
 *   <li>{@code limit = MIN(limit, safelen)} — the cut applies to the <em>name</em> path, and it
 *       is a plain minimum with no adjustment for the terminator. C then writes that terminator
 *       one place past the buffer when the two are equal; the characters it produces are still
 *       what is asserted below.</li>
 *   <li>{@code isalpha}/{@code isdigit} — anything else becomes an underscore. This is the
 *       whole point of the function: {@code main.c:397} names it as what stops a {@code -u}
 *       argument escaping the central save directory.</li>
 *   <li>{@code my_strcpy(safe, "PLAYER", safelen)} — the fallback copies
 *       {@code min(strlen(src), safelen - 1)} characters ({@code z-util.c:489}) and writes
 *       nothing when {@code safelen} is zero ({@code z-util.c:486}). The {@code -1} belongs to
 *       the buffer, not to the word, so it stops applying once the buffer can hold
 *       {@code PLAYER} whole. {@link Fallback} walks that boundary a place at a time, because
 *       it is where every plausible misreading of {@code my_strcpy} lands.</li>
 * </ul>
 *
 * <p><b>Why {@code stripSuffix} is tested by being ignored.</b> C takes the flag and never
 * reads it — the body strips unconditionally — so the two calls in {@link SuffixFlagIsIgnored}
 * must agree. C's callers pass both values ({@code ui-options.c:61} true,
 * {@code ui-death.c:168} false) and get the same answer, and a port that "fixed" the omission
 * by honouring the flag would quietly rename every character with a numeral suffix.
 *
 * <p><b>Where the port cannot match C exactly.</b> C walks bytes and Java walks characters. In
 * the UTF-8 locale {@code main.c:483} insists on, no byte above {@code 0x7F} is alphabetic, so
 * C turns each byte of an accented letter into its own underscore and charges each byte against
 * the limit. {@link NonAscii} asserts the character-based result and records the byte-based one
 * C would give, so the difference is pinned rather than discovered later.
 *
 * <p>Class PlayerNamePlayerSafeNameTest coded on 260901, commented in full on 260901.
 */
@DisplayName("PlayerName.playerSafeName — C player_safe_name")
public class PlayerNamePlayerSafeNameTest {
    /**
     * The buffer size C's callers use is a comfortable one; 32 stands in for it throughout.
     */
    private static final int ROOMY = 32;

    private String safe(int safeLen, String name) {
        return new PlayerName().playerSafeName(safeLen, name, false);
    }

    @Nested
    @DisplayName("Stripping the Roman numeral suffix")
    class SuffixStripping {
        /**
         * The ordinary succession name. C finds the suffix at index 5 and computes
         * {@code 5 - 0 - 1 = 4}, so the space goes with the numerals and {@code "Fred"} is left.
         */
        @Test
        @DisplayName("drops the numerals and the space before them")
        void ordinary() {
            assertEquals("Fred", safe(ROOMY, "Fred III"));
        }

        /**
         * A name ending in a space. C's {@code find_roman_suffix_start} returns a pointer to the
         * terminator rather than {@code NULL} — its walk never runs — so the arithmetic still
         * applies and the trailing space is trimmed. A port that collapsed that empty suffix
         * into failure would keep the space and produce {@code "Fred_"}.
         */
        @Test
        @DisplayName("trims a trailing space, because the empty suffix is a success")
        void trailingSpace() {
            assertEquals("Fred", safe(ROOMY, "Fred "));
        }

        /**
         * A surname is not a numeral, so the suffix search fails, {@code limit} becomes the
         * whole length, and the space is sanitised like any other symbol.
         */
        @Test
        @DisplayName("keeps a second word that is not numerals, with the space underscored")
        void twoWordName() {
            assertEquals("Fred_Smith", safe(ROOMY, "Fred Smith"));
        }

        /**
         * Nothing but a suffix. {@code limit} works out as zero, the copy loop never runs, and
         * the empty result falls through to {@code PLAYER} — the emptiness test is on what was
         * built, not on whether a name was supplied.
         */
        @Test
        @DisplayName("falls back when the name is only a suffix")
        void nothingButSuffix() {
            assertEquals("PLAYER", safe(ROOMY, " III"));
        }
    }

    @Nested
    @DisplayName("Sanitising characters")
    class Sanitising {
        /**
         * Letters of either case and digits are all {@code isalpha}/{@code isdigit}, so they survive.
         */
        @Test
        @DisplayName("passes ASCII letters and digits through unchanged")
        void alphanumericSurvives() {
            assertEquals("Fred2FRED", safe(ROOMY, "Fred2FRED"));
        }

        /**
         * The case {@code main.c:397} calls out by name: a {@code -u} argument that tries to
         * climb out of the save directory. Every separator and dot becomes an underscore.
         */
        @Test
        @DisplayName("underscores the characters a path would need")
        void pathCharacters() {
            assertEquals("___etc_passwd", safe(ROOMY, "../etc/passwd"));
        }

        /**
         * A name made entirely of symbols is <em>not</em> empty once sanitised — it is a row of
         * underscores — so the fallback does not fire. The test is on {@code safe[0]}, and an
         * underscore is not a NUL.
         */
        @Test
        @DisplayName("does not fall back when the name sanitises to underscores")
        void allSymbols() {
            assertEquals("___", safe(ROOMY, "!!!"));
        }
    }

    @Nested
    @DisplayName("Cutting the name to length")
    class Truncation {
        /**
         * {@code MIN(limit, safelen)} with the name the longer of the two.
         */
        @Test
        @DisplayName("cuts a long name at safeLen")
        void cutsLongName() {
            assertEquals("Fred", safe(4, "Frederick"));
        }

        /**
         * The cut is applied after the suffix has already shortened {@code limit}, so it is the
         * smaller of the two that wins.
         */
        @Test
        @DisplayName("cuts what is left after the suffix, not the original name")
        void cutsAfterStripping() {
            assertEquals("Frede", safe(5, "Fredericka III"));
        }

        /**
         * Name and buffer the same length. C allows {@code limit == safelen} and then writes its
         * terminator at {@code safe[safelen]}, one place past the buffer — a real overrun, and
         * not something to reproduce. What C <em>produces</em> is all four characters, which is
         * what is asserted.
         */
        @Test
        @DisplayName("keeps the whole name when it exactly fills the buffer")
        void exactFit() {
            assertEquals("Fred", safe(4, "Fred"));
        }

        /**
         * A zero-length buffer cuts everything, so the empty result reaches the fallback — which
         * is itself empty at that size. Both of C's zero paths meet here.
         */
        @Test
        @DisplayName("returns nothing at all for a zero-length buffer")
        void zeroBuffer() {
            assertEquals("", safe(0, "Fred"));
        }
    }

    @Nested
    @DisplayName("The PLAYER fallback")
    class Fallback {
        /**
         * C's {@code if (name)} guard leaves {@code limit} at zero, so nothing is copied.
         */
        @Test
        @DisplayName("fires for a null name")
        void nullName() {
            assertEquals("PLAYER", safe(ROOMY, null));
        }

        /**
         * An empty name reaches the same place by a different route: {@code strlen} is zero.
         */
        @Test
        @DisplayName("fires for an empty name")
        void emptyName() {
            assertEquals("PLAYER", safe(ROOMY, ""));
        }

        /**
         * {@code my_strcpy} into a one-byte buffer copies {@code min(6, 0) = 0} characters and
         * writes only the terminator. Nothing of the word survives.
         */
        @Test
        @DisplayName("yields nothing at safeLen 1")
        void oneByteBuffer() {
            assertEquals("", safe(1, null));
        }

        /**
         * {@code min(6, 2) = 2}. The truncation is by the buffer, one place reserved.
         */
        @Test
        @DisplayName("truncates to safeLen - 1 while the buffer is the smaller")
        void truncatesToBuffer() {
            assertEquals("PL", safe(3, null));
        }

        /**
         * The last size at which the reservation still bites: {@code min(6, 5) = 5}. A port that
         * applied the {@code -1} to the word instead of the buffer agrees here and nowhere else
         * below.
         */
        @Test
        @DisplayName("is still one short at safeLen 6")
        void oneShortOfWhole() {
            assertEquals("PLAYE", safe(6, null));
        }

        /**
         * The boundary. {@code strlen("PLAYER")} is 6 and {@code safelen - 1} is 6, so the
         * minimum is 6 and the word arrives whole. From here the {@code -1} never applies again.
         */
        @Test
        @DisplayName("delivers the whole word at safeLen 7")
        void wholeWordAtTheBoundary() {
            assertEquals("PLAYER", safe(7, null));
        }

        /**
         * Past the boundary, and the case every real caller takes. C's {@code len >= bufsize}
         * test is false, so no truncation happens at all — the result does not grow with the
         * buffer, and it is not {@code safelen - 1} characters long.
         */
        @Test
        @DisplayName("does not pad or shrink once the buffer is roomy")
        void roomyBuffer() {
            assertEquals("PLAYER", safe(ROOMY, null));
        }
    }

    @Nested
    @DisplayName("The stripSuffix flag")
    class SuffixFlagIsIgnored {
        /**
         * C declares {@code strip_suffix} and never reads it, so both of its callers' values
         * give the same answer — including for a name where the flag would visibly matter if it
         * were honoured.
         */
        @Test
        @DisplayName("makes no difference either way")
        void bothValuesAgree() {
            PlayerName playerName = new PlayerName();

            assertEquals(playerName.playerSafeName(ROOMY, "Fred III", false),
                    playerName.playerSafeName(ROOMY, "Fred III", true));
            assertEquals("Fred", playerName.playerSafeName(ROOMY, "Fred III", true));
        }
    }

    @Nested
    @DisplayName("Non-ASCII input")
    class NonAscii {
        /**
         * {@code isalpha} is a single-byte test and no byte above {@code 0x7F} is alphabetic in
         * a UTF-8 locale, so C underscores an accented letter — twice, once per byte, giving
         * {@code "Fr__d__ric"} and spending four places of the limit. Java sees one character
         * and spends one. The rule is C's; only the unit differs, and that difference is the
         * boundary between a byte string and a {@code String}.
         */
        @Test
        @DisplayName("underscores an accented letter, once per character rather than per byte")
        void accentedLetter() {
            assertEquals("Fr_d_ric", safe(ROOMY, "Frédéric"));
        }

        /**
         * The same for digits: {@code isdigit} is ASCII-only, so an Arabic-Indic digit is not a
         * digit to C and must not be one here either. A port reaching for
         * {@code Character.isDigit} passes it through.
         */
        @Test
        @DisplayName("underscores a non-ASCII digit")
        void nonAsciiDigit() {
            assertEquals("Fred_", safe(ROOMY, "Fred٣"));
        }
    }
}
