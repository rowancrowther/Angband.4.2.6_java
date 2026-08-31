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
 * Tests {@code PlayerName.playerRandomName} — the port of C's {@code player_random_name}
 * ({@code src/player.c}), the name a character is born with.
 *
 * <p>The C is three lines and every one of them is an expectation here: the section it draws
 * from, the two length bounds it passes, and the {@code my_strcap} it applies to what comes
 * back. All three are read off {@code player.c:378-385} rather than off the port — the method
 * takes no arguments, so a wrong constant would be invisible without pinning it from the C.
 *
 * <p><b>Forcing a known answer.</b> Generation is random, and the port's {@code randDiv} does
 * not reproduce C's rejection sampling, so no seed makes the two agree draw for draw. The
 * technique is the one {@link PlayerNameRandnameMakeTest} documents at length: a word list whose
 * adjacent letter pairs are all distinct gives every pair exactly one successor, so the walk
 * must replay that word and the roll cannot change the answer. {@code "elrond"} is six letters
 * and holds vowels, which puts it inside C's four-to-eight window; the capitalised
 * {@code "Elrond"} is then the only string C could return.
 *
 * <p><b>The bounds.</b> Testing that four and eight are the numbers passed means finding lists
 * that a wrong constant would fail on. A list of only three-letter walks is unsatisfiable at a
 * minimum of four, so it can only hang — hence the timeout rather than an assertion. What is
 * asserted instead is the pair of edges: a six-letter word must be reachable (it lies inside the
 * window), and over a varied list no returned name may fall outside four to eight letters. A
 * {@code min} of 5 or a {@code max} of 6 would break the first; a wider window would break the
 * second.
 *
 * <p>Class PlayerNamePlayerRandomNameTest coded on 260831, commented in full on 260831.
 */
@DisplayName("PlayerName.playerRandomName — C player_random_name")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
public class PlayerNamePlayerRandomNameTest {
    /**
     * Enough draws that a branch taken only rarely is still taken many times over.
     */
    private static final int MANY = 2000;

    private PlayerName playerName;

    /**
     * A list with enough variety that the walk has real choices at most pairs, while staying
     * short enough to reason about. Every word holds a vowel and is four to eight letters, so
     * the list is satisfiable at the bounds C passes.
     */
    private static List<String> tolkienish() {
        return new ArrayList<>(Arrays.asList(
                "elrond", "aragorn", "frodo", "gandalf", "legolas", "boromir", "celeborn",
                "faramir", "eowyn", "denethor", "isildur", "beren"));
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
    @DisplayName("The capitalisation")
    class Capitalisation {
        /**
         * C's {@code my_strcap} upper-cases the first character and nothing else
         * ({@code z-util.c:531}). {@code randname_make} produces lower-case letters only, so the
         * whole of the difference between the two is the leading letter.
         */
        @Test
        @DisplayName("capitalises the first letter and leaves the rest alone")
        void firstLetterOnly() {
            load("elrond");

            for (int i = 0; i < 50; i++) {
                assertEquals("Elrond", playerName.playerRandomName());
            }
        }

        /**
         * The same claim over a list with real choices in it, where the word cannot be predicted:
         * whatever comes back, its first character is an upper-case letter and every other one is
         * lower case. A port that skipped {@code my_strcap} would fail on the first character; a
         * port that upper-cased the whole string would fail on the rest.
         */
        @Test
        @DisplayName("returns exactly one upper-case letter, at the front")
        void restStaysLowerCase() {
            load(tolkienish());

            for (int i = 0; i < MANY; i++) {
                String name = playerName.playerRandomName();

                assertTrue(Character.isUpperCase(name.charAt(0)),
                        "first letter not capitalised: '" + name + "'");
                for (int j = 1; j < name.length(); j++) {
                    assertTrue(name.charAt(j) >= 'a' && name.charAt(j) <= 'z',
                            "not a lower-case letter after the first: '" + name + "'");
                }
            }
        }
    }

    @Nested
    @DisplayName("The length bounds C passes")
    class LengthBounds {
        /**
         * C passes {@code min} of 4 and {@code max} of 8, and {@code randname_make} treats both
         * as inclusive, so no name may fall outside that window.
         */
        @Test
        @DisplayName("keeps every name between four and eight letters inclusive")
        void withinFourToEight() {
            load(tolkienish());

            for (int i = 0; i < MANY; i++) {
                String name = playerName.playerRandomName();

                assertTrue(name.length() >= 4, "shorter than min: '" + name + "'");
                assertTrue(name.length() <= 8, "longer than max: '" + name + "'");
            }
        }

        /**
         * The window is not just an upper bound. This list offers exactly two walks, of four and
         * of eight letters, and both must come back. The two words share no letter, so the only
         * pair with a choice in it is the start-of-word pair and neither walk can cross into the
         * other; a {@code min} above 4 would rule out {@code "obey"} and a {@code max} below 8
         * would rule out {@code "midflank"}, leaving this list producing one word or hanging.
         */
        @Test
        @DisplayName("reaches both edges of the window")
        void bothEdgesReachable() {
            load("obey", "midflank");
            Set<String> seen = new HashSet<>();

            for (int i = 0; i < MANY; i++) {
                seen.add(playerName.playerRandomName());
            }

            assertEquals(Set.of("Obey", "Midflank"), seen);
        }
    }

    @Nested
    @DisplayName("The section it draws from")
    class Section {
        /**
         * C passes {@code RANDNAME_TOLKIEN}. Loading a decoy into the scroll section with
         * disjoint letters means a name built from the wrong section could not spell the right
         * answer, and the deterministic Tolkien walk pins which one was used.
         *
         * <p>The section number is the enum's ordinal plus one, because C numbers the sections
         * from one ({@code RANDNAME_TOLKIEN = 1}, {@code randname.h}) and {@code names.txt}
         * carries that same number on its {@code section:} lines.
         */
        @Test
        @DisplayName("draws from the Tolkien section, not another")
        void tolkienSectionOnly() {
            MiscRegistry.setNames(List.of(
                    new Name(RandnameType.RANDNAME_TOLKIEN.ordinal() + 1,
                            new ArrayList<>(List.of("elrond"))),
                    new Name(RandnameType.RANDNAME_SCROLL.ordinal() + 1,
                            new ArrayList<>(List.of("mub")))));

            for (int i = 0; i < 50; i++) {
                assertEquals("Elrond", playerName.playerRandomName());
            }
        }
    }

    @Nested
    @DisplayName("The name itself")
    class TheName {
        /**
         * Each call runs a fresh walk, so a varied list must yield more than one name. A method
         * that generated once and cached, or that returned a fixed member of the list, would
         * come back with a single answer every time.
         */
        @Test
        @DisplayName("generates a different name across calls")
        void variesBetweenCalls() {
            load(tolkienish());
            Set<String> seen = new HashSet<>();

            for (int i = 0; i < MANY; i++) {
                seen.add(playerName.playerRandomName());
            }

            assertTrue(seen.size() > 1, "every call returned the same name: " + seen);
        }

        /**
         * The vowel condition survives the capitalisation. C tests it on the lower-case letters
         * inside {@code randname_make}, and {@code my_strcap} runs afterwards, so an upper-case
         * vowel at the front is still a vowel as far as the guarantee goes.
         */
        @Test
        @DisplayName("always contains a vowel")
        void alwaysContainsAVowel() {
            load(tolkienish());

            for (int i = 0; i < MANY; i++) {
                String name = playerName.playerRandomName().toLowerCase();

                assertTrue(name.chars().anyMatch(ch -> "aeiou".indexOf(ch) >= 0),
                        "no vowel in '" + name + "'");
            }
        }
    }
}
