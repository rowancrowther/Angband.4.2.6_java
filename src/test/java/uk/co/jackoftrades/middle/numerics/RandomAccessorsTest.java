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

package uk.co.jackoftrades.middle.numerics;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.enums.DamageAspect;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.WorldData;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Random}'s accessors, guarded setters and evaluation — the port of C's
 * {@code random_value} and {@code randcalc} ({@code z-rand.h}, {@code z-rand.c}).
 *
 * <p>The dice arithmetic itself belongs to {@code RandomValueUtils} and is tested there. What is
 * tested here is the layer above it: that the four terms come back from the accessors they were
 * given to, that the setters silently substitute 1 for a value that would make the dice meaningless,
 * and that {@link Random#randCalc} composes base, dice and level bonus in the way the aspects
 * require.
 *
 * <p>The setters' flooring is the part worth pinning. A die with zero sides is not a die, so the
 * class refuses one rather than reporting an error — which means a data file with a nonsense figure
 * produces a playable value instead of an exception, and a test is the only place that behaviour is
 * written down.
 *
 * @author Rowan Crowther
 */
class RandomAccessorsTest {

    /**
     * The world's maximum depth, which the average aspect divides the level bonus by. Any non-zero
     * figure will do; this is C's.
     */
    private static final int MAX_DEPTH = 128;

    /**
     * Whatever was in the constants holder before this class ran, put back afterwards.
     */
    private static Object savedConstants;

    /**
     * Seeds the one constant {@link DamageAspect#AVERAGE} needs. Without it the average aspect
     * divides by a field of an unloaded record and throws, which is a fair description of the game
     * before its data files are read but no use to a unit test.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @BeforeAll
    static void seedConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        savedConstants = data.get(null);
        data.set(null, new GameConstantsData(
                null, null, null, null,
                new WorldData(MAX_DEPTH, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                null, null, null, null, null, null, null, null, null, null, null, null));
    }

    /**
     * Puts the constants holder back, so a class running after this one sees what it expected.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @AfterAll
    static void restoreConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);
    }

    /**
     * The four terms of {@code base + m_bonus + dice 'd' sides}, each read back from its own
     * accessor. The constructor takes them in a different order than the formula reads, which is
     * exactly the sort of thing that transposes silently.
     */
    @Test
    @DisplayName("each term comes back from its own accessor")
    void termsRoundTrip() {
        Random value = new Random(3, 7, 2, 5, false);

        assertEquals(3, value.getBase());
        assertEquals(7, value.getMBonus());
        assertEquals(2, value.getDice());
        assertEquals(5, value.getSides());
    }

    /**
     * The guarded setters.
     */
    @Nested
    @DisplayName("setters")
    class Setters {

        /**
         * Ordinary values pass through untouched.
         */
        @Test
        @DisplayName("a sensible value is stored as given")
        void sensibleValuesStored() {
            Random value = new Random(0, 1, 1, 1, false);

            value.setBase(4);
            value.setDice(3);
            value.setSides(6);
            value.setMBonus(9);

            assertEquals(4, value.getBase());
            assertEquals(3, value.getDice());
            assertEquals(6, value.getSides());
            assertEquals(9, value.getMBonus());
        }

        /**
         * Zero and negative dice, sides and bonus are floored at 1 rather than rejected: a die with
         * no sides cannot be rolled, and the class would rather be rollable than faithful to a bad
         * data file.
         */
        @Test
        @DisplayName("dice, sides and bonus are floored at one")
        void nonsenseValuesAreFloored() {
            Random value = new Random(0, 5, 5, 5, false);

            value.setDice(0);
            value.setSides(-3);
            value.setMBonus(0);

            assertEquals(1, value.getDice());
            assertEquals(1, value.getSides());
            assertEquals(1, value.getMBonus());
        }

        /**
         * The base is the exception: it is floored at zero, not one, because a base of zero is
         * meaningful — most dice have none — while a negative one is expressed through negation
         * rather than stored directly.
         */
        @Test
        @DisplayName("the base is floored at zero, not one")
        void baseFlooredAtZero() {
            Random value = new Random(4, 1, 1, 1, false);

            value.setBase(-2);

            assertEquals(0, value.getBase());
        }
    }

    /**
     * The presence tests, which the description code uses to decide whether a term is worth
     * printing.
     */
    @Nested
    @DisplayName("presence tests")
    class Presence {

        /**
         * Each answers on its own term, so a value with dice but no base reports exactly that.
         */
        @Test
        @DisplayName("each reports on its own term")
        void reportPerTerm() {
            Random diceOnly = new Random(0, 1, 2, 6, false);

            assertFalse(diceOnly.hasBase());
            assertTrue(diceOnly.hasDice());
            assertTrue(diceOnly.hasSides());
        }

        /**
         * A bonus of exactly 1 is "no bonus" — the multiplier's neutral value — which is why the
         * test is against 1 and not against zero as the other three are.
         */
        @Test
        @DisplayName("a bonus of one counts as no bonus")
        void bonusOfOneIsNoBonus() {
            Random neutral = new Random(2, 1, 1, 4, false);
            Random scaled = new Random(2, 3, 1, 4, false);

            assertFalse(neutral.hasBonus());
            assertTrue(scaled.hasBonus());
        }
    }

    /**
     * Evaluation through {@link Random#randCalc}, which composes the base with the dice term and the
     * level-scaled bonus.
     */
    @Nested
    @DisplayName("randCalc")
    class RandCalc {

        /**
         * Minimising takes the floor of each term: one per die, and no level bonus at all. For
         * {@code 3 + 2d6} that is {@code 3 + 2 + 0}.
         */
        @Test
        @DisplayName("minimising takes one per die and no bonus")
        void minimise() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(5, value.randCalc(0, DamageAspect.MINIMIZE));
        }

        /**
         * Maximising takes the ceiling of each: every die on its highest face, and the whole bonus
         * regardless of level. For {@code 3 + 2d6} with a bonus of 4 that is {@code 3 + 12 + 4}.
         */
        @Test
        @DisplayName("maximising takes every die high and the whole bonus")
        void maximise() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(19, value.randCalc(0, DamageAspect.MAXIMIZE));
        }

        /**
         * Averaging takes the mean face — {@code n * (sides + 1) / 2} — and scales the bonus by
         * depth, so at the surface the bonus contributes nothing.
         */
        @Test
        @DisplayName("averaging takes the mean face and scales the bonus by depth")
        void average() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(3 + 7, value.randCalc(0, DamageAspect.AVERAGE));
        }

        /**
         * Extremifying picks whichever of the two ends is further from zero. For a value that cannot
         * be negative that is always the maximum, which is what makes it distinguishable from
         * minimising.
         */
        @Test
        @DisplayName("extremifying picks the end further from zero")
        void extremify() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(value.randCalc(0, DamageAspect.MAXIMIZE),
                    value.randCalc(0, DamageAspect.EXTREMIFY));
        }
    }

    /**
     * Range membership and variability, both of which are defined in terms of the two extremes.
     */
    @Nested
    @DisplayName("range")
    class Range {

        /**
         * A value is valid when it falls between the surface minimum and maximum inclusive, so both
         * ends count as inside.
         */
        @Test
        @DisplayName("both ends of the range are inside it")
        void endsAreInside() {
            Random value = new Random(0, 1, 1, 4, false);

            assertTrue(value.isValid(value.randCalc(0, DamageAspect.MINIMIZE)));
            assertTrue(value.isValid(value.randCalc(0, DamageAspect.MAXIMIZE)));
        }

        /**
         * And a value outside either end is not.
         */
        @Test
        @DisplayName("a value beyond either end is outside it")
        void beyondEndsIsOutside() {
            Random value = new Random(0, 1, 1, 4, false);

            assertFalse(value.isValid(value.randCalc(0, DamageAspect.MINIMIZE) - 1));
            assertFalse(value.isValid(value.randCalc(0, DamageAspect.MAXIMIZE) + 1));
        }

        /**
         * A one-sided die with no level bonus produces one answer, so it does not vary; a six-sided
         * one does. The description code uses this to decide whether to print a range or a number.
         *
         * <p>The bonus has to be zero for the first of these, and that is the point worth
         * recording: the level bonus contributes nothing at the minimum and its whole value at the
         * maximum, so <em>any</em> non-zero bonus makes a value vary however fixed its dice are.
         */
        @Test
        @DisplayName("a die with one face and no bonus does not vary")
        void singleFaceDoesNotVary() {
            assertFalse(new Random(2, 0, 1, 1, false).varies());
            assertTrue(new Random(2, 0, 1, 6, false).varies());
            assertTrue(new Random(2, 1, 1, 1, false).varies(),
                    "a level bonus varies even when the dice cannot");
        }
    }

    /**
     * Parsing and copying, the two ways a random value arrives other than through a constructor.
     */
    @Nested
    @DisplayName("parseStr and copy")
    class ParseAndCopy {

        /**
         * The plain {@code NdM} form.
         */
        @Test
        @DisplayName("a dice string parses into its terms")
        void parsesDice() {
            Random parsed = Random.parseStr("2d6");

            assertEquals(2, parsed.getDice());
            assertEquals(6, parsed.getSides());
        }

        /**
         * A leading minus is stripped and applied through negation rather than stored as a negative
         * base, because the grammar cannot express one. The result is that the whole rolled range
         * moves below zero.
         */
        @Test
        @DisplayName("a negated value ends up wholly negative")
        void parsesNegated() {
            Random parsed = Random.parseStr("-1d4");

            assertTrue(parsed.randCalc(0, DamageAspect.MAXIMIZE) < 0,
                    "even the largest roll of a negated value is below zero");
        }

        /**
         * An empty string is not a value, and the caller is told so with {@code null} rather than an
         * exception.
         */
        @Test
        @DisplayName("an empty string parses to nothing")
        void emptyParsesToNull() {
            assertNull(Random.parseStr(""));
        }

        /**
         * A copy carries the four resolved terms and shares nothing, so the two can be re-diced
         * independently.
         */
        @Test
        @DisplayName("a copy carries the terms and shares no state")
        void copyIsIndependent() {
            Random original = new Random(3, 4, 2, 6, false);
            Random duplicate = original.copy();

            assertNotSame(original, duplicate);
            assertEquals(original.getBase(), duplicate.getBase());
            assertEquals(original.getMBonus(), duplicate.getMBonus());
            assertEquals(original.getDice(), duplicate.getDice());
            assertEquals(original.getSides(), duplicate.getSides());

            duplicate.setSides(8);
            assertEquals(6, original.getSides(), "the original keeps its own sides");
        }
    }

    /**
     * Negation, which is how the port expresses a range that lies below zero.
     */
    @Nested
    @DisplayName("negation")
    class Negation {

        /**
         * Marking a value to negate and then negating it moves the whole range below zero.
         */
        @Test
        @DisplayName("negating moves the whole range below zero")
        void negateFlipsTheRange() {
            Random value = new Random(2, 1, 1, 4, false);
            value.setToNegate(true);
            value.negate();

            assertTrue(value.randCalc(0, DamageAspect.MAXIMIZE) < 0);
        }

        /**
         * Negation happens once. A second request after the value has already been flipped is
         * refused, so a value cannot be turned back positive by asking twice.
         */
        @Test
        @DisplayName("a value already negated cannot be marked again")
        void negationHappensOnce() {
            Random value = new Random(2, 1, 1, 4, false);
            value.setToNegate(true);
            value.negate();
            int afterFirst = value.randCalc(0, DamageAspect.MAXIMIZE);

            value.setToNegate(true);
            value.negate();

            assertEquals(afterFirst, value.randCalc(0, DamageAspect.MAXIMIZE));
        }
    }
}
