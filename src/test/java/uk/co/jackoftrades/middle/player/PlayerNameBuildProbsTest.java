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
import uk.co.jackoftrades.middle.game.Name;
import uk.co.jackoftrades.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftrades.middle.player.enums.RandnameType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code PlayerName.buildProbs} — the port of C's {@code build_prob}
 * ({@code src/randname.c:44}), which turns a list of words into the Markov counts that
 * {@code randname_make} later draws letters from.
 *
 * <p><b>Every expectation here is a hand-simulation of the C, not of the port.</b> C seeds
 * {@code c_prev = c_cur = S_WORD} and then walks <em>every</em> character of the word, including
 * the first, before recording one end-of-word transition. So a word of {@code n} letters produces
 * exactly {@code n + 1} outcome bumps and the same number of {@code TOTAL} bumps, and the very
 * first of those lands in {@code probs[S_WORD][S_WORD]}. Each test below lists the cells C would
 * touch and asserts that the port touched those and nothing else.
 *
 * <p><b>Why "nothing else" matters.</b> The failure this method is most exposed to is a walk that
 * starts one character late: it still produces a plausible-looking table, but the start-of-word
 * row is empty and the second letter is filed under a letter pair that only occurs mid-word.
 * {@code randname_make} divides by {@code probs[S_WORD][S_WORD][TOTAL]}, so an empty start row is
 * fatal, and no assertion about a single cell would catch it. {@link #assertOnly} therefore
 * checks the whole 27×27×28 table against the expected cells.
 *
 * <p><b>The one deliberate divergence</b> is the empty word, which the port skips and C would
 * count as a word that began and ended at once. {@code MiscRegistry} is fed only parsed name
 * tokens, so the case is unreachable in the game; it is pinned here so the difference is a
 * recorded decision rather than a surprise.
 *
 * <p>Class PlayerNameBuildProbsTest coded on 260831, commented in full on 260831.
 */
@DisplayName("PlayerName.buildProbs — C build_prob")
public class PlayerNameBuildProbsTest {
    /**
     * C's {@code S_WORD}: the start-of-word marker, and the pair a word is seeded with.
     */
    private static final int S_WORD = 26;

    /**
     * C's {@code E_WORD}: the same value again, read on the outcome axis as "the word ended".
     */
    private static final int E_WORD = S_WORD;

    /**
     * C's {@code TOTAL}: the running sum of every outcome recorded for a letter pair.
     */
    private static final int TOTAL = 27;

    /**
     * The key {@link #assertOnly} addresses one cell of the table by.
     */
    private static String cell(int prev, int cur, int next) {
        return prev + "," + cur + "," + next;
    }

    /**
     * Every non-zero cell of {@code probs}, keyed as {@link #cell} keys them.
     */
    private static Map<String, Integer> toMap(int[][][] probs) {
        Map<String, Integer> found = new LinkedHashMap<>();

        for (int prev = 0; prev < probs.length; prev++) {
            for (int cur = 0; cur < probs[prev].length; cur++) {
                for (int next = 0; next < probs[prev][cur].length; next++) {
                    if (probs[prev][cur][next] != 0) {
                        found.put(cell(prev, cur, next), probs[prev][cur][next]);
                    }
                }
            }
        }

        return found;
    }

    /**
     * The sum of every count in the table, {@code TOTAL} cells included.
     */
    private static int sum(int[][][] probs) {
        int total = 0;

        for (Map.Entry<String, Integer> entry : toMap(probs).entrySet()) {
            total += entry.getValue();
        }

        return total;
    }

    /**
     * Asserts that {@code probs} holds exactly the expected counts and no others — a cell C would
     * not have touched must still be zero.
     */
    private static void assertOnly(int[][][] probs, Map<String, Integer> expected) {
        assertEquals(new LinkedHashMap<>(expected), toMap(probs));
    }

    /**
     * Loads {@code words} into the registry as the Tolkien section and returns the table
     * {@code buildProbs} makes of them.
     *
     * <p>The section number is the enum's ordinal plus one, because C numbers the sections
     * from one ({@code RANDNAME_TOLKIEN = 1}, {@code randname.h}) and {@code names.txt}
     * carries that same number on its {@code section:} lines.
     */
    private int[][][] build(String... words) throws Exception {
        List<String> list = new ArrayList<>(Arrays.asList(words));
        MiscRegistry.setNames(List.of(new Name(RandnameType.RANDNAME_TOLKIEN.ordinal() + 1, list)));

        Method method = PlayerName.class.getDeclaredMethod("buildProbs", RandnameType.class);
        method.setAccessible(true);
        try {
            return (int[][][]) method.invoke(new PlayerName(), RandnameType.RANDNAME_TOLKIEN);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    @Nested
    @DisplayName("A word of one letter")
    class OneLetterWord {
        /**
         * C never special-cases a short word. For {@code "a"} it records the first letter out of
         * the start pair, then ends immediately from {@code (S_WORD, a)} — four bumps in all.
         */
        @Test
        @DisplayName("records a first letter and an immediate end")
        void singleLetter() throws Exception {
            assertOnly(build("a"), Map.of(
                    cell(S_WORD, S_WORD, 0), 1,
                    cell(S_WORD, S_WORD, TOTAL), 1,
                    cell(S_WORD, 0, E_WORD), 1,
                    cell(S_WORD, 0, TOTAL), 1));
        }
    }

    @Nested
    @DisplayName("A word of two letters")
    class TwoLetterWord {
        /**
         * {@code "ab"} is the shortest word with a real letter-to-letter transition. C walks
         * {@code (26,26)→a}, {@code (26,a)→b}, then ends from {@code (a,b)}: six bumps.
         */
        @Test
        @DisplayName("files the second letter under the start-of-word context")
        void twoLetters() throws Exception {
            Map<String, Integer> expected = new LinkedHashMap<>();
            expected.put(cell(S_WORD, S_WORD, 0), 1);
            expected.put(cell(S_WORD, S_WORD, TOTAL), 1);
            expected.put(cell(S_WORD, 0, 1), 1);
            expected.put(cell(S_WORD, 0, TOTAL), 1);
            expected.put(cell(0, 1, E_WORD), 1);
            expected.put(cell(0, 1, TOTAL), 1);

            assertOnly(build("ab"), expected);
        }

        /**
         * A doubled letter puts {@code cur} and {@code next} on the same index and then makes the
         * end-of-word pair {@code (a,a)}. Nothing in C treats that differently, and the counts
         * must not collapse into one another.
         */
        @Test
        @DisplayName("handles a repeated letter")
        void doubledLetter() throws Exception {
            Map<String, Integer> expected = new LinkedHashMap<>();
            expected.put(cell(S_WORD, S_WORD, 0), 1);
            expected.put(cell(S_WORD, S_WORD, TOTAL), 1);
            expected.put(cell(S_WORD, 0, 0), 1);
            expected.put(cell(S_WORD, 0, TOTAL), 1);
            expected.put(cell(0, 0, E_WORD), 1);
            expected.put(cell(0, 0, TOTAL), 1);

            assertOnly(build("aa"), expected);
        }
    }

    @Nested
    @DisplayName("A longer word")
    class LongerWord {
        /**
         * {@code "elrond"} (e=4, l=11, r=17, o=14, n=13, d=3) exercises the sliding pair for a
         * full six letters. C's walk is {@code (26,26)→e}, {@code (26,e)→l}, {@code (e,l)→r},
         * {@code (l,r)→o}, {@code (r,o)→n}, {@code (o,n)→d}, then end from {@code (n,d)}.
         */
        @Test
        @DisplayName("slides the letter pair one place per character")
        void sixLetters() throws Exception {
            Map<String, Integer> expected = new LinkedHashMap<>();
            expected.put(cell(S_WORD, S_WORD, 4), 1);
            expected.put(cell(S_WORD, S_WORD, TOTAL), 1);
            expected.put(cell(S_WORD, 4, 11), 1);
            expected.put(cell(S_WORD, 4, TOTAL), 1);
            expected.put(cell(4, 11, 17), 1);
            expected.put(cell(4, 11, TOTAL), 1);
            expected.put(cell(11, 17, 14), 1);
            expected.put(cell(11, 17, TOTAL), 1);
            expected.put(cell(17, 14, 13), 1);
            expected.put(cell(17, 14, TOTAL), 1);
            expected.put(cell(14, 13, 3), 1);
            expected.put(cell(14, 13, TOTAL), 1);
            expected.put(cell(13, 3, E_WORD), 1);
            expected.put(cell(13, 3, TOTAL), 1);

            assertOnly(build("elrond"), expected);
        }
    }

    @Nested
    @DisplayName("A list of words")
    class WordList {
        /**
         * C reseeds the pair at the top of each word, so two words never chain into one another:
         * the counts are the sum of what each word would have produced alone.
         */
        @Test
        @DisplayName("resets the pair between words and sums the counts")
        void twoWordsDoNotChain() throws Exception {
            Map<String, Integer> expected = new LinkedHashMap<>();
            expected.put(cell(S_WORD, S_WORD, 0), 1);
            expected.put(cell(S_WORD, S_WORD, 1), 1);
            expected.put(cell(S_WORD, S_WORD, TOTAL), 2);
            expected.put(cell(S_WORD, 0, 1), 1);
            expected.put(cell(S_WORD, 0, TOTAL), 1);
            expected.put(cell(0, 1, E_WORD), 1);
            expected.put(cell(0, 1, TOTAL), 1);
            expected.put(cell(S_WORD, 1, 0), 1);
            expected.put(cell(S_WORD, 1, TOTAL), 1);
            expected.put(cell(1, 0, E_WORD), 1);
            expected.put(cell(1, 0, TOTAL), 1);

            assertOnly(build("ab", "ba"), expected);
        }

        /**
         * The same word twice is the same cells at twice the count — C accumulates rather than
         * recording presence.
         */
        @Test
        @DisplayName("counts a repeated word twice")
        void repeatedWord() throws Exception {
            Map<String, Integer> expected = new LinkedHashMap<>();
            expected.put(cell(S_WORD, S_WORD, 0), 2);
            expected.put(cell(S_WORD, S_WORD, TOTAL), 2);
            expected.put(cell(S_WORD, 0, 1), 2);
            expected.put(cell(S_WORD, 0, TOTAL), 2);
            expected.put(cell(0, 1, E_WORD), 2);
            expected.put(cell(0, 1, TOTAL), 2);

            assertOnly(build("ab", "ab"), expected);
        }

        /**
         * With no words at all C's {@code for} never runs and the table stays as
         * {@code memset} left it.
         */
        @Test
        @DisplayName("leaves the table empty when there are no words")
        void emptyList() throws Exception {
            assertOnly(build(), Map.of());
        }
    }

    @Nested
    @DisplayName("Character handling")
    class CharacterHandling {
        /**
         * C lower-cases each character as it reads it, through
         * {@code A2I(tolower(...))}; the port lower-cases the whole word first. For the purely
         * alphabetic words the name file holds, the two are the same, so a capitalised word must
         * produce exactly the lower-case word's table.
         */
        @Test
        @DisplayName("lower-cases the word, as C's tolower does")
        void mixedCase() throws Exception {
            assertOnly(build("AbC"), toMap(build("abc")));
        }
    }

    @Nested
    @DisplayName("The invariants randname_make relies on")
    class GeneratorInvariants {
        /**
         * {@code randname_make} draws {@code randint0(lprobs[c_prev][c_cur][TOTAL])} and then
         * walks the row subtracting each outcome until the roll runs out. That only terminates
         * inside the row if {@code TOTAL} is exactly the sum of the outcomes {@code 0..E_WORD}
         * for that pair, so the invariant is checked for every pair in the table.
         */
        @Test
        @DisplayName("TOTAL is the sum of the outcomes for every letter pair")
        void totalIsTheRowSum() throws Exception {
            int[][][] probs = build("elrond", "aragorn", "frodo", "gandalf", "a", "ab");

            for (int prev = 0; prev <= S_WORD; prev++) {
                for (int cur = 0; cur <= S_WORD; cur++) {
                    int sum = 0;
                    for (int next = 0; next <= E_WORD; next++) {
                        sum += probs[prev][cur][next];
                    }
                    assertEquals(sum, probs[prev][cur][TOTAL],
                            "row sum for pair (" + prev + ", " + cur + ")");
                }
            }
        }

        /**
         * Generation starts at {@code (S_WORD, S_WORD)}, so that row must hold one first letter
         * per word. If the walk skipped the first character the row would be empty and the
         * generator's first {@code randint0} would be handed a zero bound.
         */
        @Test
        @DisplayName("the start-of-word row holds one entry per word")
        void startRowIsPopulated() throws Exception {
            int[][][] probs = build("elrond", "aragorn", "frodo", "a");

            assertEquals(4, probs[S_WORD][S_WORD][TOTAL], "start-of-word TOTAL");
            assertEquals(2, probs[S_WORD][S_WORD][0], "words starting with 'a'");
            assertEquals(1, probs[S_WORD][S_WORD][4], "words starting with 'e'");
            assertEquals(1, probs[S_WORD][S_WORD][5], "words starting with 'f'");
            assertEquals(0, probs[S_WORD][S_WORD][E_WORD], "no word ends before it starts");
        }

        /**
         * A word of {@code n} letters is {@code n + 1} outcome bumps and {@code n + 1}
         * {@code TOTAL} bumps in C — one per character plus the end marker — so the whole table
         * sums to twice that. This catches a walk that runs one step short or one step long
         * without depending on which cells it touched.
         */
        @Test
        @DisplayName("a word of n letters contributes 2 * (n + 1) counts")
        void countsPerWord() throws Exception {
            assertEquals(2 * (6 + 1), sum(build("elrond")));
            assertEquals(2 * (1 + 1), sum(build("a")));
            assertEquals(2 * (6 + 1) + 2 * (1 + 1), sum(build("elrond", "a")));
        }
    }

    @Nested
    @DisplayName("The table itself")
    class TableShape {
        /**
         * C's {@code name_probs} is {@code [S_WORD+1][S_WORD+1][TOTAL+1]}, so the table must be
         * 27 × 27 × 28: both marker indices addressable, and one slot past {@code TOTAL}.
         */
        @Test
        @DisplayName("is 27 x 27 x 28, as C's name_probs is")
        void dimensions() throws Exception {
            int[][][] probs = build("ab");

            assertEquals(S_WORD + 1, probs.length);
            assertEquals(S_WORD + 1, probs[0].length);
            assertEquals(TOTAL + 1, probs[0][0].length);
        }

        /**
         * C rebuilds into a {@code static} it has just {@code memset} to zero, so a build never
         * inherits the previous one's counts. The port returns a new table each call; two builds
         * from the same list must therefore be equal rather than doubled.
         */
        @Test
        @DisplayName("starts from zero on every call")
        void freshEachCall() throws Exception {
            assertOnly(build("ab"), toMap(build("ab")));
        }
    }

    @Nested
    @DisplayName("The empty word")
    class EmptyWord {
        /**
         * The recorded divergence. C would record {@code probs[26][26][E_WORD]} and its
         * {@code TOTAL} for a zero-length word — a word that ended where it began — because its
         * {@code while} simply never runs. The port skips such a word entirely, which is only
         * reachable if something puts an empty token in the registry. Pinned so the difference
         * stays a decision.
         */
        @Test
        @DisplayName("is skipped, where C would count an immediate end")
        void emptyWordSkipped() throws Exception {
            assertOnly(build(""), Map.of());
            assertOnly(build("", "ab"), toMap(build("ab")));
        }
    }
}
