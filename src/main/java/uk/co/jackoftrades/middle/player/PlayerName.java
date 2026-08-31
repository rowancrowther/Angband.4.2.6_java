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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.player.enums.RandnameType;
import uk.co.jackoftrades.middle.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayerName {
    private static final Logger logger = LogManager.getLogger(PlayerName.class);

    private final int S_WORD = 26;
    private final int E_WORD = S_WORD;
    private final int TOTAL = 27;

    /**
     * Rolls a random name for the player at birth — the port of C's {@code player_random_name}
     * ({@code player.c}). The word is drawn from the Tolkien section of the name file, between
     * four and eight letters, and comes back with its first letter capitalised.
     *
     * <p>The three constants are C's, not choices made here: {@code RANDNAME_TOLKIEN} is the
     * setting-appropriate section, and {@code 4} and {@code 8} are the length bounds C passes.
     * {@link #randnameMake} returns a word of lower-case letters, so the capitalisation is a
     * separate step — C's {@code my_strcap}, ported as {@link StringUtils#strCap}, which
     * leaves an empty string alone exactly as C's {@code buf[0]} guard does. That case cannot
     * arise from a four-letter minimum, but the two versions agree on it regardless.
     *
     * <p>C fills a caller's buffer and returns the character count; the name itself is returned
     * instead, and its length is what C would have returned. Both callers in C
     * ({@code ui-input.c}) want the name and use the count only as its length.
     *
     * <p>Method playerRandomName coded on 260831, commented in full on 260831.
     *
     * @return a fresh random name of four to eight letters, capitalised
     */
    public String playerRandomName() {
        return StringUtils.strCap(randnameMake(RandnameType.RANDNAME_TOLKIEN, 4, 8));
    }

    /**
     * Generates one random word from the Markov counts {@link #buildProbs} gathers — the port of
     * C's {@code randname_make} ({@code randname.c}), which is W. Sheldon Simms' name generator.
     * A letter is chosen from the two letters that preceded it, so the output reads like the word
     * list it learned from without repeating any word in it.
     *
     * <p>The walk starts with both halves of the pair set to {@link #S_WORD}, the start-of-word
     * marker, which is why {@code buildProbs} files first letters under {@code [S_WORD][S_WORD]}.
     * Each step draws {@code randInt0} of that pair's {@link #TOTAL} and then subtracts its way
     * along the row until the roll runs out; the index it stops on is the outcome. Because
     * {@code TOTAL} is exactly the row sum, the roll can never walk off the end of the row.
     *
     * <p>An outcome of {@link #E_WORD} is only a candidate ending. It is accepted when the word
     * has reached {@code min} letters and holds a vowel; otherwise it costs one try and the same
     * position is drawn again. Ten spent tries, or a word that has run past {@code max}, abandons
     * the attempt and starts a fresh word — which is why every per-attempt variable, the length
     * count included, is reset at the top of the outer loop and not before it.
     *
     * <p>{@code max} bounds the loop rather than the word, and the two are not the same thing.
     * The length is tested before a letter is appended, so the walk can lay down one letter past
     * {@code max} — but that letter is never in a returned word, because an ending is only ever
     * accepted on an iteration the {@code lNum <= max} guard has already let through. A returned
     * word is therefore between {@code min} and {@code max} letters inclusive. C still has to
     * hold the overrun letter somewhere, which is what its {@code assert(buflen > max)} reserves
     * room for; a {@link StringBuilder} has no such limit, so the assert has no counterpart here.
     *
     * <p>C's remaining asserts on the letter indices become thrown exceptions. Their range is
     * inclusive of {@code S_WORD} on all three axes, since the marker is a legitimate value for
     * the pair — the seed is nothing else — and for an outcome, where it reads as the end of the
     * word.
     *
     * <p>C returns the length and fills a caller's buffer; the word itself is returned instead,
     * and its length is what C would have returned. The table is rebuilt on every call rather
     * than cached in a {@code static} against the last name type, as {@code buildProbs} records.
     *
     * <p>Neither version terminates if the word list cannot satisfy the conditions asked of it —
     * a list with no vowel in it, or none of {@code min} letters, restarts for ever. C guards
     * that with an assert on the name type only, and the sections in the name file are all
     * ordinary English-alphabet words.
     *
     * <p>Method randnameMake coded on 260831, commented in full on 260831.
     *
     * @param nameType the section of the name file to draw the word's style from
     * @param min the fewest letters an ending will be accepted at
     * @param max the most letters a returned word may have; a longer attempt is abandoned
     * @return the generated word, in lower case, of between {@code min} and {@code max} letters
     * @throws RuntimeException if a letter index falls outside the table, which the counts
     *                          should make impossible
     */
    public String randnameMake(RandnameType nameType, int min, int max) {
        boolean foundWord = false;
        int lNum;
        StringBuilder buffer = new StringBuilder();

        int[][][] lProbs = buildProbs(nameType);

        while (!foundWord) {
            buffer = new StringBuilder();
            int chPrev = S_WORD;
            int chCur = S_WORD;
            int tries = 0;
            lNum = 0;
            boolean containsVowel = false;

            // If we go for more than 10 tries, or run out of space, try again
            while (tries < 10 && lNum <= max && !foundWord) {
                int r;
                int chNext = 0;

                if (chPrev < 0 || chPrev > S_WORD || chCur < 0 || chCur > S_WORD) {
                    String message = "Error, character outside accepted range in " +
                            "either current or previous positions";
                    logger.error(message);
                    throw new RuntimeException(message);
                }

                r = RandomValueUtils.randInt0(lProbs[chPrev][chCur][TOTAL]);

                while (r >= lProbs[chPrev][chCur][chNext]) {
                    r -= lProbs[chPrev][chCur][chNext];
                    chNext++;
                }

                if (chNext > E_WORD || chNext < 0) {
                    String message = "Error: character in next position outside accepted range";
                    logger.error(message);
                    throw new RuntimeException(message);
                }

                if (chNext == E_WORD) {
                    // check whether this word has reached the simple conditions 
                    // Or try again for this position
                    if (lNum >= min && containsVowel)
                        foundWord = true;
                    else
                        tries++;
                } else {
                    // add the letter to the word and move on
                    char current = StringUtils.I2C(chNext);

                    if (StringUtils.isVowel(current))
                        containsVowel = true;

                    buffer.append(current);
                    lNum++;

                    if (chNext > S_WORD || chNext < 0) {
                        String message = "Error: character in next position outside accepted range";
                        logger.error(message);
                        throw new RuntimeException(message);
                    }
                    chPrev = chCur;
                    chCur = chNext;
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Builds the Markov probability table a random name is later generated from — the port of
     * C's {@code build_prob} ({@code randname.c}). Given the word list for a name type, it
     * counts, for every pair of letters seen, which letter followed that pair and how often.
     *
     * <p>The table is indexed {@code probs[prev][cur][next]}: the number of times {@code next}
     * followed the pair {@code prev, cur}. Two of those indices are not letters.
     * {@link #S_WORD} (26) is the marker a word begins with, so {@code probs[S_WORD][S_WORD]}
     * holds the first letters of words and {@code probs[S_WORD][c]} their second letters;
     * {@link #E_WORD}, the same value read on the third axis, counts the times a word ended
     * after that pair. {@link #TOTAL} (27) is not an outcome at all but the running sum of
     * every outcome for that pair, so the generator can draw one weighted letter with a single
     * random roll and subtract its way along the row.
     *
     * <p>Each word is walked with the pair seeded to {@code (S_WORD, S_WORD)} and the walk
     * starts at the first character, so that first letter is recorded as a transition out of
     * the start-of-word context rather than being passed over. The generator starts from that
     * same pair, and would have nothing to divide by if it were left empty. A one-letter word
     * is worth counting for the same reason: it contributes a first letter and an immediate
     * end.
     *
     * <p>C leans on ASCII through {@code A2I} and lower-cases one character at a time. Both are
     * kept here, as {@link StringUtils#C2I} and a lower-casing of the whole word, which comes to
     * the same thing for the purely alphabetic words the name file holds. C's terminating
     * {@code NULL} entry has no counterpart — the list ends where it ends.
     *
     * <p>Empty words are skipped, where C would count one as a word that ended the moment it
     * began. Only parsed name tokens reach the registry, so no such word can arrive, and the
     * guard keeps the first {@code charAt} safe.
     *
     * <p>C caches one table in a {@code static} and rebuilds it only when the name type
     * changes; a freshly zeroed table is returned on every call instead, which is the state
     * C's {@code memset} puts its static back into before each rebuild.
     *
     * <p>Method buildProbs coded on 260831, commented in full on 260831.
     *
     * @param nameType the section of the name file to learn from
     * @return a newly counted table, indexed {@code [prev][cur][next]}
     */
    private int[][][] buildProbs(RandnameType nameType) {
        int[][][] probs = new int[S_WORD + 1][E_WORD + 1][TOTAL + 1];
        int curChar;
        int nextChar;
        int prevChar;

        List<String> words = MiscRegistry.getNameSection(nameType);

        for (String word : words) {
            if (word.isEmpty()) continue;

            word = word.toLowerCase();
            curChar = S_WORD;
            prevChar = curChar;

            // consume the word
            for (int index = 0; index < word.length(); index++) {
                nextChar = StringUtils.C2I(word.charAt(index));

                probs[prevChar][curChar][nextChar]++;
                probs[prevChar][curChar][TOTAL]++;

                // next character
                prevChar = curChar;
                curChar = nextChar;
            }

            probs[prevChar][curChar][E_WORD]++;
            probs[prevChar][curChar][TOTAL]++;
        }

        return probs;
    }
}
