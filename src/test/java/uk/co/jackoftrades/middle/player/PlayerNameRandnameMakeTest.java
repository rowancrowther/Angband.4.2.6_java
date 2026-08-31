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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftrades.middle.game.Name;
import uk.co.jackoftrades.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.player.enums.RandnameType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerName.randnameMake} — the port of C's {@code randname_make}
 * ({@code src/randname.c}), W. Sheldon Simms' Markov name generator.
 *
 * <p><b>Every expectation is hand-simulated from the C, not read off the port.</b> The method is
 * random, so the tests do not seed the generator and assert a particular stream — the port's
 * {@code randDiv} deliberately does not reproduce C's rejection-sampling partitions, so no seed
 * makes the two agree draw for draw. Instead each test supplies a word list whose probability
 * table admits only <em>one</em> outcome that C would accept. The roll then cannot change the
 * answer, and the assertion is on C's semantics rather than on a lucky seed.
 *
 * <p><b>Building a list that forces one answer.</b> {@code build_prob} keys on the two preceding
 * letters, so a word whose adjacent letter pairs are all distinct gives every pair exactly one
 * successor, and generation replays that word letter for letter. Where a test needs a genuine
 * choice — a rejected ending, an abandoned attempt — the list weights the two branches so the
 * unwanted one is common, and the assertion is that only C's acceptable word ever comes back.
 *
 * <p><b>What is deliberately not tested.</b> A list that cannot satisfy the conditions asked of
 * it — no vowel anywhere, or no word of {@code min} letters — restarts for ever, in the port and
 * in C alike. That non-termination is faithful, and a test of it could only hang.
 *
 * <p><b>The bounds these tests pin.</b> Two are worth stating because both were wrong at some
 * point in the port. A returned word is between {@code min} and {@code max} letters
 * <em>inclusive</em>: the walk may lay down one letter past {@code max}, but that word can never
 * be accepted, because acceptance only happens on an iteration the {@code lnum <= max} guard let
 * through. And every per-attempt variable, the length count included, must be reset when an
 * attempt is abandoned — C re-zeroes {@code lnum} at {@code randname.c:109}, inside the retry
 * loop, next to the buffer reset. {@link RestartingAnAttempt} exists for that one.
 *
 * <p>Class PlayerNameRandnameMakeTest coded on 260831, commented in full on 260831.
 */
@DisplayName("PlayerName.randnameMake — C randname_make")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
public class PlayerNameRandnameMakeTest {
    /**
     * The five vowels C's {@code is_a_vowel} accepts.
     */
    private static final String VOWELS = "aeiou";

    /**
     * Enough draws that a branch taken only rarely is still taken many times over.
     */
    private static final int MANY = 2000;

    private PlayerName playerName;

    /**
     * A list with enough variety that the walk has real choices at most pairs, while staying
     * short enough to reason about. Every word holds a vowel and is at least four letters.
     */
    private static List<String> tolkienish() {
        return new ArrayList<>(Arrays.asList(
                "elrond", "aragorn", "frodo", "gandalf", "legolas", "boromir", "galadriel",
                "celeborn", "thranduil", "faramir", "eowyn", "denethor", "isildur", "beren"));
    }

    /**
     * Loads {@code words} into the registry as the Tolkien section.
     */
    private static void load(String... words) {
        load(new ArrayList<>(Arrays.asList(words)));
    }

    /**
     * Loads {@code words} into the registry as the Tolkien section.
     *
     * <p>The section number is the enum's ordinal plus one, because C numbers the sections
     * from one ({@code RANDNAME_TOLKIEN = 1}, {@code randname.h}) and {@code names.txt}
     * carries that same number on its {@code section:} lines.
     */
    private static void load(List<String> words) {
        MiscRegistry.setNames(List.of(
                new Name(RandnameType.RANDNAME_TOLKIEN.ordinal() + 1, new ArrayList<>(words))));
    }

    @BeforeEach
    void setUp() {
        playerName = new PlayerName();
        // Seeded only so a failure reproduces; no expectation below depends on the stream.
        RandomValueUtils.stateInit(20260831L);
    }

    @Nested
    @DisplayName("A word list with only one possible walk")
    class SingleWalk {
        /**
         * {@code "elrond"} has no repeated adjacent pair, so every pair in the table has exactly
         * one successor: {@code (26,26)→e}, {@code (26,e)→l}, {@code (e,l)→r}, {@code (l,r)→o},
         * {@code (r,o)→n}, {@code (o,n)→d}, and then the end from {@code (n,d)}. C has no choice
         * to make at any step and must reproduce the word it learned.
         */
        @Test
        @DisplayName("replays the word it learned, letter for letter")
        void deterministicWalk() {
            load("elrond");

            for (int i = 0; i < 50; i++) {
                assertEquals("elrond", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
            }
        }

        /**
         * The shortest word C will generate. {@code "a"} records one first letter and an
         * immediate end, so the walk is one letter long; it clears {@code min} of 1 and holds a
         * vowel, which is the pair of conditions {@code randname.c:135} tests.
         */
        @Test
        @DisplayName("generates a one-letter word when the list holds one")
        void oneLetterWord() {
            load("a");

            assertEquals("a", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
        }

        /**
         * A doubled letter makes {@code (a,a)} the end-of-word pair without making the walk
         * ambiguous — {@code (26,26)→a}, {@code (26,a)→a}, {@code (a,a)→end}. It is worth its own
         * case because it is the one shape where {@code c_cur} and {@code c_next} coincide.
         */
        @Test
        @DisplayName("handles a repeated letter in the only walk")
        void doubledLetter() {
            load("aa");

            assertEquals("aa", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
        }
    }

    @Nested
    @DisplayName("Rejecting an ending")
    class RejectingAnEnding {
        /**
         * C accepts an ending only if the word holds a vowel. This list gives both words the same
         * first letter, so the walk always begins {@code b} and then chooses: {@code (26,b)→r}
         * leads to {@code "br"}, whose only continuation is the end, and which C must refuse for
         * ever; {@code (26,b)→a} leads to {@code "ba"}, which C accepts. Only {@code "ba"} can
         * come back, however the rolls fall.
         */
        @Test
        @DisplayName("never returns a word without a vowel")
        void vowelIsRequired() {
            load("br", "ba");

            for (int i = 0; i < 200; i++) {
                assertEquals("ba", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
            }
        }

        /**
         * C refuses an ending shorter than {@code min} and spends a try on it rather than
         * returning it. From {@code (b,a)} this list offers an end or a further {@code b}; with
         * {@code min} of 3 the end is refused every time, so the only word C can produce is
         * {@code "bab"}, whose {@code (a,b)} pair ends unconditionally.
         */
        @Test
        @DisplayName("never returns a word shorter than min")
        void minimumLengthIsRequired() {
            load("ba", "bab");

            for (int i = 0; i < 200; i++) {
                assertEquals("bab", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 3, 8));
            }
        }

        /**
         * The same list with {@code min} of 1 makes both endings legal, so C can return either.
         * The point of the case is the converse of the two above: the refusals are driven by the
         * conditions, not by something in the walk that rules a short word out on its own.
         */
        @Test
        @DisplayName("returns either word when both endings are legal")
        void bothEndingsLegal() {
            load("ba", "bab");
            Set<String> seen = new HashSet<>();

            for (int i = 0; i < MANY; i++) {
                seen.add(playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
            }

            assertEquals(Set.of("ba", "bab"), seen);
        }
    }

    @Nested
    @DisplayName("Restarting an attempt")
    class RestartingAnAttempt {
        /**
         * The regression test for the abandoned attempt. Nine copies of {@code "ba"} against one
         * {@code "bab"} weight {@code (b,a)} nine to one towards an end that {@code min} of 3
         * refuses, so ten tries run out often and the attempt is abandoned. C starts the next
         * attempt from {@code lnum = 0} ({@code randname.c:109}); a port that reset the buffer
         * but carried the count over would satisfy {@code lnum >= min} with letters that are no
         * longer in the buffer and hand back {@code "ba"} — two letters where three were asked
         * for.
         */
        @Test
        @DisplayName("counts the length of the current attempt, not of the abandoned one")
        void lengthIsPerAttempt() {
            load("ba", "ba", "ba", "ba", "ba", "ba", "ba", "ba", "ba", "bab");

            for (int i = 0; i < MANY; i++) {
                assertEquals("bab", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 3, 8));
            }
        }

        /**
         * The same fault seen through {@code max} instead of {@code min}, and the more brutal of
         * the two. Three copies of {@code "bab"} against one {@code "ba"} mean the walk usually
         * reaches four letters and trips the {@code lnum <= max} guard with {@code max} of 2, so
         * attempts are abandoned over length rather than over tries. A carried-over count would
         * leave every later attempt already past {@code max}, the inner loop would never run
         * again, and the outer loop would spin for ever — so this case hangs rather than fails if
         * the reset is missing, which is what the class timeout is for.
         */
        @Test
        @DisplayName("recovers from an attempt abandoned for running past max")
        void recoversFromOverlongAttempt() {
            load("ba", "bab", "bab", "bab");

            for (int i = 0; i < MANY; i++) {
                assertEquals("ba", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 2, 2));
            }
        }
    }

    @Nested
    @DisplayName("The length bounds")
    class LengthBounds {
        /**
         * C tests {@code lnum <= max} before appending, so the walk can lay down one letter past
         * {@code max} — but that word is never accepted, because the acceptance at
         * {@code randname.c:135} only runs on an iteration the guard let through. A returned word
         * is therefore never longer than {@code max}, and never shorter than {@code min}.
         */
        @Test
        @DisplayName("keeps every returned word within min and max inclusive")
        void withinBoundsInclusive() {
            load(tolkienish());

            for (int i = 0; i < MANY; i++) {
                String word = playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 4, 8);

                assertTrue(word.length() >= 4, "shorter than min: '" + word + "'");
                assertTrue(word.length() <= 8, "longer than max: '" + word + "'");
            }
        }

        /**
         * With {@code min} and {@code max} equal there is exactly one acceptable length, so every
         * attempt that does not hit it must be abandoned. It is the tightest the two conditions
         * can be squeezed without becoming unsatisfiable.
         */
        @Test
        @DisplayName("returns exactly that length when min equals max")
        void minEqualsMax() {
            load(tolkienish());

            for (int i = 0; i < 500; i++) {
                assertEquals(5, playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 5, 5).length());
            }
        }

        /**
         * A {@code max} exactly the length of the only walk available is the boundary the
         * off-by-one lives on: {@code "elrond"} is six letters, and the guard must still admit
         * the iteration on which its end is drawn. One less than this and the list becomes
         * unsatisfiable.
         */
        @Test
        @DisplayName("admits a word of exactly max letters")
        void wordOfExactlyMaxLetters() {
            load("elrond");

            assertEquals("elrond", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 6));
        }
    }

    @Nested
    @DisplayName("The generated word")
    class GeneratedWord {
        /**
         * The start of the walk is the pair {@code (S_WORD, S_WORD)}, which is a legal value for
         * both halves — C asserts {@code c_prev <= S_WORD}, inclusive. A guard that excluded the
         * marker would throw on the first iteration of every call, so any completed call is the
         * evidence; a plain list generating cleanly is asserted here rather than left implied.
         */
        @Test
        @DisplayName("starts from the start-of-word pair without complaint")
        void startsFromTheMarkerPair() {
            load(tolkienish());

            for (int i = 0; i < 200; i++) {
                assertTrue(!playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 4, 8).isEmpty());
            }
        }

        /**
         * {@code I2A} maps an index onto a lower-case letter, and {@code build_prob} learns from
         * lower-cased words, so nothing outside {@code a}–{@code z} can reach the output even
         * from a capitalised list.
         */
        @Test
        @DisplayName("is lower-case letters only, even from a capitalised list")
        void lowerCaseLettersOnly() {
            load("Elrond", "Aragorn", "Frodo", "Galadriel", "Thranduil", "Denethor");

            for (int i = 0; i < MANY; i++) {
                String word = playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 4, 8);

                for (char ch : word.toCharArray()) {
                    assertTrue(ch >= 'a' && ch <= 'z', "not a lower-case letter: '" + word + "'");
                }
            }
        }

        /**
         * The vowel condition again, over a list where it is a real constraint rather than a
         * forced one: C rejects any ending reached without a vowel having been laid down, so no
         * returned word can be all consonants.
         */
        @Test
        @DisplayName("always contains one of C's five vowels")
        void alwaysContainsAVowel() {
            load(tolkienish());

            for (int i = 0; i < MANY; i++) {
                String word = playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 4, 8);

                assertTrue(word.chars().anyMatch(ch -> VOWELS.indexOf(ch) >= 0),
                        "no vowel in '" + word + "'");
            }
        }

        /**
         * A Markov walk is not a lookup: with a list this size the generator must reach words
         * that were never in it. If it only ever returned members of the list, the table would be
         * being read as whole words rather than as letter transitions.
         */
        @Test
        @DisplayName("reaches words that were not in the list")
        void generatesNewWords() {
            List<String> words = tolkienish();
            load(words);
            Set<String> source = new HashSet<>(words);
            boolean sawSomethingNew = false;

            for (int i = 0; i < MANY && !sawSomethingNew; i++) {
                sawSomethingNew = !source.contains(
                        playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 4, 8));
            }

            assertTrue(sawSomethingNew, "every generated word was already in the list");
        }
    }

    @Nested
    @DisplayName("The name type")
    class NameType {
        /**
         * C caches one table and rebuilds it when the type changes; the port rebuilds every call.
         * Either way the word must come from the section asked for, so two sections holding
         * disjoint letters must never produce each other's letters.
         *
         * <p>The section number is the enum's ordinal plus one, because C numbers the sections
         * from one ({@code RANDNAME_TOLKIEN = 1}, {@code randname.h}) and {@code names.txt}
         * carries that same number on its {@code section:} lines.
         */
        @Test
        @DisplayName("draws only from the section it was given")
        void drawsFromTheGivenSection() {
            MiscRegistry.setNames(List.of(
                    new Name(RandnameType.RANDNAME_TOLKIEN.ordinal() + 1, new ArrayList<>(List.of("elrond"))),
                    new Name(RandnameType.RANDNAME_SCROLL.ordinal() + 1, new ArrayList<>(List.of("mub")))));

            for (int i = 0; i < 50; i++) {
                assertEquals("elrond", playerName.randnameMake(RandnameType.RANDNAME_TOLKIEN, 1, 8));
                assertEquals("mub", playerName.randnameMake(RandnameType.RANDNAME_SCROLL, 1, 8));
            }
        }
    }
}
