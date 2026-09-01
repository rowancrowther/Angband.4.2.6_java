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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#adjustSkillScale(int, int, int, int)}, the port of C's
 * {@code adjust_skill_scale} ({@code player-calcs.c:1781-1792}) — the way every temporary skill
 * bonus and penalty in {@code calcBonuses} is applied.
 *
 * <p><b>The negative branch is not the positive one with a sign flipped</b>, and that is the whole
 * reason this needs a test. C rounds the subtraction <em>up</em>, with a deliberate
 * {@code + denominator - 1}, and says why in a comment: without it a penalty comes out slightly
 * gentler than the equivalent multiplication, and the two idioms are used interchangeably across the
 * original. Anyone tidying the two branches into one expression would drop that, and the result
 * would be a handful of skill points in the player's favour under stunning, hunger and fear — never
 * enough to look like a bug.
 *
 * <p>The other property worth pinning is that the adjustment is taken from the <em>magnitude</em> of
 * the value. A negative skill scaled by a positive fraction still moves upward, because C takes
 * {@code ABS(*v)} before applying the fraction; a naive implementation would push it further
 * negative.
 *
 * <p><b>Fixture note.</b> {@link Player}'s constructor reads the player registry for a body and a
 * race, so a bare {@code new Player()} throws unless something has loaded them.
 * {@link SeededPlayerRegistry} supplies both when they are absent, which keeps this class runnable
 * on its own rather than only as part of the whole suite.
 *
 * <p>Class PlayerAdjustSkillScaleTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerAdjustSkillScaleTest {

    /**
     * The player the method is called on; it reads no instance state.
     */
    private static Player player;

    @BeforeAll
    static void buildPlayer() {
        player = new Player();
    }

    /**
     * Bonuses — the branch with a non-negative numerator.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("bonuses")
    class Bonuses {

        /**
         * A twentieth of the value, added — the shape of the blessing bonus
         * ({@code adjust_skill_scale(&skills[SKILL_DEVICE], 1, 20, 0)}). Proportional, so it is
         * worth more to a character who is already skilled.
         */
        @Test
        @DisplayName("a fraction of the value is added")
        void addsAFraction() {
            assertAll(
                    () -> assertEquals(105, PlayerCalcs.adjustSkillScale(100, 1, 20, 0)),
                    () -> assertEquals(210, PlayerCalcs.adjustSkillScale(200, 1, 20, 0)));
        }

        /**
         * The bonus truncates on the way in — no rounding up on the positive side, which is where
         * the two branches genuinely differ.
         */
        @Test
        @DisplayName("a bonus truncates rather than rounding up")
        void bonusTruncates() {
            assertEquals(10, PlayerCalcs.adjustSkillScale(10, 1, 20, 0));
        }

        /**
         * Zero skill gains nothing unless a floor is supplied, because the fraction is taken of the
         * value itself. The {@code minValue} argument exists precisely so a caller can say
         * otherwise.
         */
        @Test
        @DisplayName("the minimum lifts the base the fraction is taken of")
        void minimumLiftsTheBase() {
            assertAll(
                    () -> assertEquals(0, PlayerCalcs.adjustSkillScale(0, 1, 20, 0)),
                    () -> assertEquals(5, PlayerCalcs.adjustSkillScale(0, 1, 20, 100)));
        }
    }

    /**
     * Penalties — the branch with a negative numerator, and its rounding rule.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("penalties")
    class Penalties {

        /**
         * A tenth of the value, taken away — the shape of the heavy-stun and hunger penalties.
         */
        @Test
        @DisplayName("a fraction of the value is subtracted")
        void subtractsAFraction() {
            assertEquals(90, PlayerCalcs.adjustSkillScale(100, -1, 10, 0));
        }

        /**
         * The rounding rule, stated as C states it: the penalty must match what
         * {@code value * (den + num) / den} gives. A tenth of 95 is 9.5; rounding up takes ten and
         * leaves 85, which is what {@code 95 * 9 / 10} gives. Truncating would take nine and leave
         * 86 — a point of skill given away, every recalculation, for as long as the status lasts.
         */
        @Test
        @DisplayName("a penalty rounds up, matching the equivalent multiplication")
        void penaltyRoundsUp() {
            int value = 95;
            int viaScale = PlayerCalcs.adjustSkillScale(value, -1, 10, 0);
            int viaMultiplication = value * (10 - 1) / 10;

            assertAll(
                    () -> assertEquals(85, viaScale),
                    () -> assertEquals(viaMultiplication, viaScale,
                            "the two idioms C uses interchangeably must agree"));
        }

        /**
         * The agreement holds across a range, not just at one convenient number — every value from
         * 1 to 200 under the three fractions {@code calcBonuses} actually uses.
         */
        @Test
        @DisplayName("penalty and multiplication agree across the range")
        void agreesAcrossTheRange() {
            assertAll(() -> {
                int[] denominators = {4, 5, 10, 20};
                for (int den : denominators) {
                    for (int value = 1; value <= 200; value++) {
                        assertEquals(value * (den - 1) / den,
                                PlayerCalcs.adjustSkillScale(value, -1, den, 0),
                                "value " + value + " over " + den);
                    }
                }
            });
        }

        /**
         * The three-tenths penalty from the worst hunger band, checked separately because its
         * numerator is not one and a rounding rule that only worked for {@code -1} would pass
         * everything above.
         */
        @Test
        @DisplayName("a numerator other than one obeys the same rule")
        void largerNumerator() {
            assertEquals(70, PlayerCalcs.adjustSkillScale(100, -3, 10, 0));
        }
    }

    /**
     * The magnitude rule — what happens when the skill is already negative.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("negative skills")
    class NegativeSkills {

        /**
         * C takes {@code ABS(*v)} before applying the fraction, so a bonus to a negative skill moves
         * it toward zero rather than further away. An implementation that skipped the absolute value
         * would drive a bad skill worse every time something tried to improve it.
         */
        @Test
        @DisplayName("a bonus to a negative skill moves it upward")
        void bonusMovesNegativeUpward() {
            int adjusted = PlayerCalcs.adjustSkillScale(-100, 1, 20, 0);

            assertAll(
                    () -> assertEquals(-95, adjusted),
                    () -> assertTrue(adjusted > -100));
        }

        /**
         * And a penalty to a negative skill takes it further down by the same magnitude, so the two
         * directions stay symmetric about the sign of the numerator rather than the sign of the
         * value.
         */
        @Test
        @DisplayName("a penalty to a negative skill moves it downward")
        void penaltyMovesNegativeDownward() {
            assertEquals(-110, PlayerCalcs.adjustSkillScale(-100, -1, 10, 0));
        }
    }
}
