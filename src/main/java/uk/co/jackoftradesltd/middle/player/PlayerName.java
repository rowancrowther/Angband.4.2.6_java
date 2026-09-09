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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.utils.StringUtils;

public class PlayerName {
    private static final Logger logger = LogManager.getLogger(PlayerName.class);

    /**
     * Finds the Roman numeral suffix on the end of a name — the port of C's
     * {@code find_roman_suffix_start} ({@code player-birth.c}). C uses it for dynastic
     * successions: on a quickstart birth the suffix is read, incremented and written back, so
     * Fred III begets Fred IV.
     *
     * <p>The rule is C's, and it is deliberately loose. Everything after the last space is the
     * candidate, and it qualifies only if every one of its characters is one of
     * {@code I V X L C D M}. Case matters — C compares against the upper-case letters alone, so
     * a lower-cased "iii" is not a suffix. Nothing checks that the letters spell a well-formed
     * numeral; C's own comment on the companion {@code roman_to_int} admits it parses nonsense.
     *
     * <p>Two absent results are not the same thing, and the distinction is C's. {@code null} is
     * C's {@code NULL}: there is no space at all, or what followed the last one was not
     * numerals. An empty string is a success — a name ending in a space has an empty suffix, and
     * C returns a live pointer to the terminator there rather than {@code NULL}, its loop never
     * running. Both leave C's caller with nothing to increment, but only the second gives it a
     * buffer position to write to.
     *
     * <p>C returns a pointer into the player's own name buffer and the caller increments the
     * numeral through it, editing the name in place. A copy comes back here instead, which is
     * the boundary an immutable {@code String} imposes: a caller wanting Fred IV has to rebuild
     * the whole name rather than write over its tail.
     *
     * <p>Outstanding: nothing calls this yet. C's {@code roman_to_int} and {@code int_to_roman},
     * which turn the suffix found here into the next one, are not ported.
     *
     * <p>Method findRomanSuffixStart coded on 260831, commented in full on 260831.
     *
     * @param name the full player name to inspect
     * @return the numerals following the last space, empty if the name ends in a space, or
     * {@code null} if there is no space or the trailing word is not all numerals
     */
    public static String findRomanSuffixStart(String name) {
        if (name.contains(" ")) {
            String suffix = "";

            for (int index = name.length() - 1; index >= 0; index--) {
                if (name.charAt(index) == ' ') {
                    suffix = name.substring(index + 1);
                    break;
                }
            }

            for (int i = 0; i < suffix.length(); i++) {
                char ch = suffix.charAt(i);

                if (ch != 'I' && ch != 'V' && ch != 'X'
                        && ch != 'L' && ch != 'C' && ch != 'D'
                        && ch != 'M') {
                    return null;
                }
            }

            return suffix;
        }

        return null;
    }

    /**
     * Reduces a player name to a form safe to use as a filename — the port of C's
     * {@code player_safe_name} ({@code player.c}). Everything that is not an ASCII letter or
     * digit becomes an underscore, any Roman numeral suffix is dropped, the result is cut to
     * the length asked for, and a name that survives none of that becomes {@code PLAYER}.
     *
     * <p>{@code stripSuffix} is never read, here or in C. C declares the parameter and its
     * callers pass both values — {@code ui-options.c:61} true, {@code ui-death.c:168} false —
     * but the body strips unconditionally, so the flag has no effect in 4.2.6. It is kept
     * rather than dropped so the signature still matches the callers waiting to be ported.
     *
     * <p>The suffix is found by {@link #findRomanSuffixStart}, and the length is measured back
     * from it: C's {@code suffix - name - 1} is a pointer difference, with the {@code -1}
     * discarding the space in front of the numerals. A name that ends in a space has an empty
     * suffix rather than none, which trims the trailing space off — the two absent results that
     * method distinguishes are load-bearing here.
     *
     * <p>The alphanumeric test is C's {@code isalpha}/{@code isdigit}, which {@code main.c:483}
     * runs under the user's locale. That locale is required to be UTF-8, but the tests are
     * still single-byte, and no byte above {@code 0x7F} is alphabetic in a UTF-8 locale — so C
     * sanitises every byte of a multi-byte character. {@link StringUtils#isAlpha} and
     * {@link StringUtils#isDigit} are the same ASCII ranges, applied to a {@code char}. The
     * remaining difference is one of units, not of rules: an accented letter costs C two
     * underscores and two places of the limit where it costs one of each here. Java strings are
     * characters and C strings are bytes, and nothing short of encoding the name would close
     * that.
     *
     * <p>{@code MIN(limit, safelen)} is reproduced exactly, including the fact that C allows
     * {@code limit} to reach {@code safelen} and then writes its terminator one place past the
     * buffer. The characters both versions produce are the same; only C pays for the terminator.
     *
     * <p>The fallback is C's {@code my_strcpy}, which copies {@code min(strlen(src),
     * safelen - 1)} characters, reserving the last place for the terminator, and writes nothing
     * at all when {@code safelen} is zero. Both are reproduced: the {@code -1} belongs to the
     * buffer and not to the word, so a buffer of seven or more yields {@code PLAYER} whole
     * while a buffer of three yields {@code PL}, and the floor at zero stands in for C's
     * {@code bufsize == 0} early return. C leaves its caller's buffer untouched in that case,
     * where an empty string is returned here — the same emptiness the caller would read back.
     *
     * <p>C fills a caller's buffer and returns nothing; the name is returned instead, so
     * {@code safeLen} bounds the result rather than describing storage that already exists.
     *
     * <p>Method playerSafeName coded on 260831, commented in full on 260901.
     *
     * @param safeLen     the size of the buffer C would have been given; a sanitised name is cut to
     *                    {@code safeLen} characters, while the {@code PLAYER} fallback fits itself
     *                    into {@code safeLen - 1}, exactly as C's two paths do
     * @param name        the player's full name, which may be {@code null}
     * @param stripSuffix ignored, as it is in C; see above
     * @return the sanitised name, or {@code PLAYER} cut to fit if nothing of the name survived
     */
    public String playerSafeName(int safeLen, String name, boolean stripSuffix) {
        String suffix = "";
        int limit = 0;

        if (name != null) {
            suffix = findRomanSuffixStart(name);

            if (suffix != null)
                limit = name.length() - suffix.length() - 1;
            else
                limit = name.length();
        }

        limit = Math.min(limit, safeLen);

        StringBuilder safeName = new StringBuilder();

        for (int i = 0; i < limit; i++) {
            char c = name.charAt(i);

            if (!StringUtils.isAlpha(c) && !StringUtils.isDigit(c)) {
                c = '_';
            }

            safeName.append(c);
        }

        String safe = safeName.toString();

        if (safe.isEmpty()) {
            safeLen = Math.min(safeLen - 1, "PLAYER".length());
            safeLen = Math.max(0, safeLen);

            safe = "PLAYER".substring(0, safeLen);
        }

        return safe;
    }
}
