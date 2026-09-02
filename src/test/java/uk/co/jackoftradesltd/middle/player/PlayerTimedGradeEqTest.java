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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

/**
 * Tests {@link PlayerTimed#timedGradeEq}, the port of C's {@code player_timed_grade_eq}
 * ({@code player-timed.c:734}).
 *
 * <p>A timed effect is one counter, but the status the player is told about is a <em>band</em> of
 * that counter: stunning runs "Stun" then "Heavy Stun" then "Knocked Out" as the number climbs.
 * This method answers which band the counter is in, by name, and callers branch on the answer —
 * {@code GameWorld.decreaseTimeouts} asks whether a wound is a "Mortal Wound" before deciding it
 * does not bleed down, and the digestion code asks whether nourishment is "Full" or "Faint".
 *
 * <p><b>The band is the first grade whose {@code max} the value does not exceed, and only that
 * grade is compared.</b> That is the whole difficulty of the method and the reason for most of the
 * cases below. Every grade above the active one also has a {@code max} covering the value, so a
 * search that keeps looking after a mismatch answers {@code true} for every stronger band as well
 * — a lightly stunned player would report as "Knocked Out". C expresses the rule as a
 * {@code while} that walks to the band followed by a single {@code streq} outside the loop, and
 * these tests pin the same behaviour from the outside.
 *
 * <p><b>Fixtures are built by hand rather than parsed.</b> The grade maxima used here are the
 * shipped stun bands from {@code player_timed.txt} (50 / 150 / 10000) because they make the
 * boundaries concrete, but nothing depends on that file: the rule under test should hold for any
 * ascending set of grades.
 *
 * <p>{@link #setTimedValue} reaches the private map reflectively rather than going through
 * {@code setTimed}. That was originally because {@code setTimed} wrote nothing; now that it does, the
 * reason is a better one — it coerces the value it is given to the effect's bounds, so a test that
 * wants a counter of exactly 50 to sit on a band boundary would be asking the method under test's
 * neighbour to agree with it first.
 *
 * <p>{@link PlayerRegistry} is global static state shared with the reader suites, so the loaded
 * effects are saved and put back around every test.
 *
 * <p>Class PlayerTimedGradeEqTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerTimedGradeEqTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * Whatever the registry held before this test, put back afterwards.
     */
    private Object savedEffects;

    /**
     * @return the registry's private list of loaded timed effects, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField() throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        f.setAccessible(true);
        return f;
    }

    /**
     * Builds a grade band.
     *
     * @param position the grade's place in the ascending order
     * @param max      the highest counter value this band covers
     * @param status   the band's name, or {@code null} for a band with no name
     * @return the grade
     */
    private static TimedGrade grade(int position, int max, String status) {
        return new TimedGrade(position, ColourEnum.COLOUR_WHITE, max, status, "up", "down");
    }

    /**
     * Loads the registry with a single effect carrying the given grades.
     *
     * @param effect the effect the definition is for
     * @param grades its bands, in ascending order of {@code max}
     */
    private static void loadEffect(TimedEffect effect, TimedGrade... grades) throws Exception {
        PlayerTimedEffect definition = new PlayerTimedEffect(effect, "test effect", null, null,
                null, null, List.of(), List.of(grades), null, null, false, 0, null, false,
                null, null, null);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition);
        registryField().set(null, all);
    }

    /**
     * Loads the shipped stun bands: Stun to 50, Heavy Stun to 150, Knocked Out to 10000.
     */
    private static void loadStunGrades() throws Exception {
        loadEffect(TimedEffect.TMD_STUN,
                grade(1, 50, "Stun"),
                grade(2, 150, "Heavy Stun"),
                grade(3, 10000, "Knocked Out"));
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        player = new Player();
    }

    @AfterEach
    void tearDown() throws Exception {
        registryField().set(null, savedEffects);
    }

    /**
     * Writes a timed effect's counter directly, bypassing the stubbed {@code setTimed}.
     *
     * @param effect the effect to set
     * @param value  the counter value to give it
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void setTimedValue(TimedEffect effect, int value) throws Exception {
        Field f = Player.class.getDeclaredField("timed");
        f.setAccessible(true);
        ((Map<TimedEffect, Integer>) f.get(player)).put(effect, value);
    }

    /**
     * Which band a given counter value falls in.
     */
    @Nested
    class BandSelection {

        @Test
        void aValueInsideTheFirstBandMatchesThatBandsName() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 10);

            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        @Test
        void aValueInsideTheSecondBandMatchesThatBandsName() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 60);

            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Heavy Stun"));
        }

        @Test
        void aValueInsideTheThirdBandMatchesThatBandsName() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 200);

            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Knocked Out"));
        }

        @Test
        void aValueExactlyOnABoundaryStaysInTheLowerBand() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 50);

            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Heavy Stun"));
        }

        @Test
        void oneAboveABoundaryMovesToTheNextBand() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 51);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Heavy Stun"));
        }
    }

    /**
     * Only the active band is compared — the rule the method exists to get right.
     */
    @Nested
    class OnlyTheActiveBandIsCompared {

        @Test
        void aLowValueDoesNotMatchAStrongerBandsName() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 10);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Knocked Out"),
                    "grades above the active one also cover the value; they must not be compared");
            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Heavy Stun"));
        }

        @Test
        void aHighValueDoesNotMatchAWeakerBandsName() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 200);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Heavy Stun"));
        }

        @Test
        void twoBandsSharingANameStillOnlyMatchTheActiveOne() throws Exception {
            loadEffect(TimedEffect.TMD_STUN,
                    grade(1, 50, "Same"),
                    grade(2, 150, "Different"));
            setTimedValue(TimedEffect.TMD_STUN, 100);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Same"));
            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Different"));
        }
    }

    /**
     * The guards, each of which answers {@code false} without consulting a band.
     */
    @Nested
    class Guards {

        /**
         * A dormant effect answers no, whatever is asked of it.
         *
         * <p>C opens with {@code if (p->timed[idx])}. The port needs the same check for a reason C
         * does not have: its grade list has no entry for the dormant state, so a zero reaching the
         * search would be tested against the first real band and report as "Stun".
         */
        @Test
        void anEffectAtZeroIsNeverAtAnyGrade() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 0);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Knocked Out"));
        }

        @Test
        void aFreshPlayerHasEveryEffectDormant() throws Exception {
            loadStunGrades();

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        @Test
        void anEffectWithNoLoadedDefinitionAnswersFalse() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_POISONED, 10);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_POISONED, "Poisoned"));
        }

        @Test
        void aNameThatMatchesNoGradeAnswersFalse() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, 10);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Bewildered"));
        }

        @Test
        void anUnnamedBandAnswersFalseRatherThanThrowing() throws Exception {
            loadEffect(TimedEffect.TMD_STUN, grade(1, 50, null));
            setTimedValue(TimedEffect.TMD_STUN, 10);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        @Test
        void anEffectWithNoGradesAtAllAnswersFalse() throws Exception {
            loadEffect(TimedEffect.TMD_STUN);
            setTimedValue(TimedEffect.TMD_STUN, 10);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        /**
         * A value above every band's maximum answers false rather than walking off the end.
         *
         * <p>A deliberate difference from C, which would follow a null {@code next} pointer here.
         * The shipped data puts 10000 on the topmost band, so the case is unreachable in practice.
         */
        @Test
        void aValueAboveEveryBandAnswersFalse() throws Exception {
            loadEffect(TimedEffect.TMD_STUN, grade(1, 50, "Stun"));
            setTimedValue(TimedEffect.TMD_STUN, 500);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        /**
         * A negative counter is treated as being in the first band, as in C.
         *
         * <p>C's opening test is a truthiness check, which a negative passes, and its walk then
         * stops immediately because the value does not exceed the first maximum.
         */
        @Test
        void aNegativeValueLandsInTheFirstBand() throws Exception {
            loadStunGrades();
            setTimedValue(TimedEffect.TMD_STUN, -5);

            assertTrue(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        @Test
        void theSentinelEffectIsNeverAtAGrade() throws Exception {
            loadStunGrades();

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_NONE, "Stun"));
        }

        @Test
        void anEmptyRegistryListAnswersFalseRatherThanThrowing() throws Exception {
            registryField().set(null, new ArrayList<PlayerTimedEffect>());
            setTimedValue(TimedEffect.TMD_STUN, 10);

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }

        @Test
        void aMissingEffectFromTheMapAnswersFalse() throws Exception {
            loadStunGrades();
            Field f = Player.class.getDeclaredField("timed");
            f.setAccessible(true);
            f.set(player, new HashMap<TimedEffect, Integer>());

            assertFalse(PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun"));
        }
    }
}
