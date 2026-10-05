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

package uk.co.jackoftradesltd.middle.numerics;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;

import java.lang.reflect.Field;
import java.util.Collections;

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
 * given to, that the setters store what C's direct field assignment would, and that
 * {@link Random#randCalc} composes base, dice and level bonus in the way the aspects require.
 *
 * <p>The expected figures are worked out by hand from {@code z-rand.c}, functions {@code randcalc()},
 * {@code randcalc_valid()}, {@code randcalc_varies()}, {@code damcalc()} and {@code m_bonus_calc()},
 * and from the negation block of {@code parse_random()} in {@code parser.c}, not read back from the
 * Java. The setters' only departure from C is that a negative dice or sides count is stored as 0,
 * which C never produces; the tests pin that, and pin that 0 itself, a negative base and a zero
 * {@code m_bonus} are kept.
 *
 * <p>Class RandomAccessorsTest coded before 260815, commented in full on 261005.
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
     * Whatever the world list held before this class ran, put back afterwards.
     */
    private static Object savedWorlds;

    /**
     * Seeds the two things evaluation reads: the world's maximum depth for the average aspect, and a
     * world list of that length for the randomised aspect's level bonus. Without them those aspects
     * fail loudly, which suits an unloaded game but not a unit test.
     *
     * @throws Exception if either field cannot be reached
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

        Field worlds = WorldRegistry.class.getDeclaredField("worlds");
        worlds.setAccessible(true);
        savedWorlds = worlds.get(null);
        worlds.set(null, Collections.nCopies(MAX_DEPTH, null));
    }

    /**
     * Puts both globals back, so a class running after this one sees what it expected.
     *
     * @throws Exception if either field cannot be reached
     */
    @AfterAll
    static void restoreConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);

        Field worlds = WorldRegistry.class.getDeclaredField("worlds");
        worlds.setAccessible(true);
        worlds.set(null, savedWorlds);
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
         * Zero is a legitimate dice count, sides count and {@code m_bonus} in C ({@code { 0, 0, 0, 0 }}
         * is the usual starting point, and a plain constant has no dice), so the setters keep it.
         */
        @Test
        @DisplayName("zero dice, sides and bonus are kept")
        void zeroIsKept() {
            Random value = new Random(0, 5, 5, 5, false);

            value.setDice(0);
            value.setSides(0);
            value.setMBonus(0);

            assertEquals(0, value.getDice());
            assertEquals(0, value.getSides());
            assertEquals(0, value.getMBonus());
        }

        /**
         * C never produces a negative dice or sides count, so the port refuses one by storing 0. This
         * is the one place the setters depart from C's bare field assignment.
         */
        @Test
        @DisplayName("a negative dice or sides count is stored as zero")
        void negativeCountsBecomeZero() {
            Random value = new Random(0, 0, 5, 5, false);

            value.setDice(-3);
            value.setSides(-1);

            assertEquals(0, value.getDice());
            assertEquals(0, value.getSides());
        }

        /**
         * The base is stored as given, negative included, because a negated value carries a negative
         * base ({@code parse_random()} in {@code parser.c} produces {@code -5} for {@code "-1d4"}) and
         * C assigns {@code v.base} directly.
         */
        @Test
        @DisplayName("the base is stored as given, even when negative")
        void negativeBaseIsKept() {
            Random value = new Random(4, 0, 1, 1, false);

            value.setBase(-2);

            assertEquals(-2, value.getBase());
        }

        /**
         * The level bonus is a ceiling that C assigns directly, so whatever is given is what is
         * stored, and the evaluation sees it: {@code m_bonus_calc()} maximises to {@code max}.
         */
        @Test
        @DisplayName("the bonus is stored as given and changes the maximum")
        void bonusIsStoredAsGiven() {
            Random value = new Random(0, 0, 1, 4, false);

            value.setMBonus(6);

            assertEquals(6, value.getMBonus());
            assertEquals(4 + 6, value.randCalc(0, DamageAspect.MAXIMIZE));
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
         * C tests the bonus field for truthiness ({@code if (value.m_bonus)} in {@code effects-info.c}
         * and {@code player-spell.c}), so 0 is "no bonus" and anything else, 1 included, is a bonus:
         * {@code m_bonus(1, level)} still rolls 0 or 1.
         */
        @Test
        @DisplayName("a bonus of zero is no bonus and a bonus of one is a bonus")
        void zeroIsNoBonus() {
            Random none = new Random(2, 0, 1, 4, false);
            Random one = new Random(2, 1, 1, 4, false);
            Random scaled = new Random(2, 3, 1, 4, false);

            assertFalse(none.hasBonus());
            assertTrue(one.hasBonus());
            assertTrue(scaled.hasBonus());
        }

        /**
         * C's {@code { 0, 0, 0, 0 }} has no term of any kind, which is the value the description code
         * must print as nothing. A plain constant has only a base. Negating {@code 1d4} gives it a
         * base too, since the shift is {@code -(1 * (4 + 1))}.
         */
        @Test
        @DisplayName("an all-zero value has no term, and a constant has only a base")
        void emptyAndConstant() {
            Random empty = Random.Zero();
            Random constant = new Random(5, 0, 0, 0, false);

            assertFalse(empty.hasBase());
            assertFalse(empty.hasDice());
            assertFalse(empty.hasSides());
            assertFalse(empty.hasBonus());

            assertTrue(constant.hasBase());
            assertFalse(constant.hasDice());
            assertFalse(constant.hasSides());
            assertFalse(constant.hasBonus());

            assertTrue(new Random(0, 0, 1, 4, true).hasBase(),
                    "negating 1d4 moves the base to -5");
        }
    }

    /**
     * The two constructors that carry expression text for a {@code $} variable, which have no
     * counterpart in C's {@code random_value} (C resolves variable dice in {@code z-dice.c}).
     */
    @Nested
    @DisplayName("string-term constructors")
    class StringTerms {

        /**
         * The text is kept aside and the resolved base stays 0, with the other terms stored as given
         * and no negation applied.
         */
        @Test
        @DisplayName("a string base leaves the resolved base at zero")
        void stringBase() {
            Random value = new Random("$B", 2, 3, 4);

            assertEquals(0, value.getBase());
            assertFalse(value.hasBase());
            assertEquals(2, value.getMBonus());
            assertEquals(3, value.getDice());
            assertEquals(4, value.getSides());
        }

        /**
         * Likewise the resolved sides stay 0 and the other terms are stored as given.
         */
        @Test
        @DisplayName("a string sides count leaves the resolved sides at zero")
        void stringSides() {
            Random value = new Random(2, 3, 1, "$S");

            assertEquals(2, value.getBase());
            assertEquals(3, value.getMBonus());
            assertEquals(1, value.getDice());
            assertEquals(0, value.getSides());
            assertFalse(value.hasSides());
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

        /**
         * For a negated value the minimum is the end further from zero, and extremifying must return
         * it. {@code "-1d4"} has base -5, so it runs from -4 to -1, and {@code abs(-4) > abs(-1)}
         * selects -4.
         */
        @Test
        @DisplayName("extremifying a negated value picks the minimum")
        void extremifyNegated() {
            Random value = new Random(0, 0, 1, 4, true);

            assertEquals(-4, value.randCalc(0, DamageAspect.MINIMIZE));
            assertEquals(-1, value.randCalc(0, DamageAspect.MAXIMIZE));
            assertEquals(-4, value.randCalc(0, DamageAspect.EXTREMIFY));
        }

        /**
         * When the two ends are equally far from zero C's {@code abs(min) > abs(max)} is false, so
         * the maximum wins. {@code -3 + 1d5} runs from -2 to 2, and the answer is 2, not -2.
         */
        @Test
        @DisplayName("extremifying a tie returns the maximum")
        void extremifyTie() {
            Random value = new Random(-3, 0, 1, 5, false);

            assertEquals(-2, value.randCalc(0, DamageAspect.MINIMIZE));
            assertEquals(2, value.randCalc(0, DamageAspect.MAXIMIZE));
            assertEquals(2, value.randCalc(0, DamageAspect.EXTREMIFY));
        }

        /**
         * Averaging scales the bonus by {@code level / MAX_RAND_DEPTH} in integer arithmetic:
         * {@code 3 + 2*(6+1)/2 + 4*64/128} is {@code 3 + 7 + 2}.
         */
        @Test
        @DisplayName("averaging at depth 64 gives half the bonus")
        void averageAtDepth() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(12, value.randCalc(64, DamageAspect.AVERAGE));
        }

        /**
         * The integer division truncates, so one level short of the bottom the bonus is
         * {@code 4*127/128 = 3}, not 4: {@code 3 + 7 + 3}.
         */
        @Test
        @DisplayName("averaging truncates the bonus at depth 127")
        void averageTruncates() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(13, value.randCalc(127, DamageAspect.AVERAGE));
        }

        /**
         * The average of the dice is also integer arithmetic: {@code 1 * (4 + 1) / 2} is 2, not 2.5.
         */
        @Test
        @DisplayName("averaging a d4 truncates to 2")
        void averageDiceTruncates() {
            assertEquals(2, new Random(0, 0, 1, 4, false).randCalc(0, DamageAspect.AVERAGE));
        }

        /**
         * The level only ever scales the bonus: minimising and maximising ignore it, so the answer is
         * the same at the surface and at the bottom.
         */
        @Test
        @DisplayName("level does not move the minimum or maximum")
        void levelIgnoredAtTheEnds() {
            Random value = new Random(3, 4, 2, 6, false);

            assertEquals(5, value.randCalc(100, DamageAspect.MINIMIZE));
            assertEquals(19, value.randCalc(100, DamageAspect.MAXIMIZE));
        }

        /**
         * A random roll must land between the minimum and the maximum. {@code 2 + 1d4 M3} runs from
         * 3 to 9, so five hundred rolls at depth 64 must all fall inside.
         */
        @Test
        @DisplayName("randomised rolls stay between the minimum and the maximum")
        void randomiseStaysInRange() {
            RandomValueUtils.stateInit(261005L);
            Random value = new Random(2, 3, 1, 4, false);

            for (int i = 0; i < 500; i++) {
                int roll = value.randCalc(64, DamageAspect.RANDOMIZE);
                assertTrue(roll >= 3 && roll <= 9, "roll " + roll + " is outside 3..9");
            }
        }

        /**
         * A constant such as {@code "5"} has no dice and no bonus, so every aspect gives 5.
         */
        @Test
        @DisplayName("a constant evaluates to itself under every aspect")
        void constantIsConstant() {
            Random value = new Random(5, 0, 0, 0, false);

            for (DamageAspect aspect : DamageAspect.values()) {
                assertEquals(5, value.randCalc(50, aspect), "aspect " + aspect);
            }
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

        /**
         * A constant, and C's all-zero literal, have one answer and so do not vary, nor does a
         * negated constant.
         */
        @Test
        @DisplayName("a constant does not vary")
        void constantDoesNotVary() {
            assertFalse(new Random(5, 0, 0, 0, false).varies());
            assertFalse(Random.Zero().varies());
            assertFalse(new Random(3, 0, 0, 0, true).varies());
        }

        /**
         * With dice of {@code 0d0} the only variation is the bonus, which contributes 0 at the
         * minimum and {@code m_bonus} at the maximum, so {@code m_bonus} of 3 varies from 0 to 3.
         */
        @Test
        @DisplayName("a bonus alone makes a value vary")
        void bonusAloneVaries() {
            assertTrue(new Random(0, 3, 0, 0, false).varies());
        }

        /**
         * {@code 1d4 M3} runs from {@code 1 + 0 = 1} to {@code 4 + 3 = 7} at level 0. Both ends are
         * valid, and one step beyond either is not.
         */
        @Test
        @DisplayName("the range of a dice-and-bonus value is its worked-out ends")
        void workedOutRange() {
            Random value = new Random(0, 3, 1, 4, false);

            assertFalse(value.isValid(0));
            assertTrue(value.isValid(1));
            assertTrue(value.isValid(7));
            assertFalse(value.isValid(8));
        }

        /**
         * A negated value's range is the positive range reflected about zero. {@code 2 + 1d4 M3}
         * runs from 3 to 9, so negated it runs from -9 to -3, and positive numbers are all outside.
         */
        @Test
        @DisplayName("a negated value's range is the reflection of the positive range")
        void negatedRange() {
            Random value = new Random(2, 3, 1, 4, true);

            assertFalse(value.isValid(-10));
            assertTrue(value.isValid(-9));
            assertTrue(value.isValid(-3));
            assertFalse(value.isValid(-2));
            assertFalse(value.isValid(3));
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
         * A bare minus has nothing to negate, so it is refused rather than read as zero.
         */
        @Test
        @DisplayName("a lone minus parses to nothing")
        void loneMinusParsesToNull() {
            assertNull(Random.parseStr("-"));
        }

        /**
         * A negated string containing a {@code $} variable is refused, since C has no such form.
         */
        @Test
        @DisplayName("a negated variable string parses to nothing")
        void negatedVariableParsesToNull() {
            assertNull(Random.parseStr("-$B"));
        }

        /**
         * The full form {@code base+XdYMZ}: {@code parse_random()} in {@code parser.c} fills
         * {@code values[]} as base 2, dice 1, sides 4, {@code m_bonus} 3.
         */
        @Test
        @DisplayName("base, dice, sides and bonus all parse")
        void parsesFullForm() {
            Random parsed = Random.parseStr("2+1d4M3");

            assertEquals(2, parsed.getBase());
            assertEquals(1, parsed.getDice());
            assertEquals(4, parsed.getSides());
            assertEquals(3, parsed.getMBonus());
        }

        /**
         * A bare number is a constant: {@code parse_random()} stores it in {@code values[0]} and
         * stops, leaving dice, sides and bonus at 0.
         */
        @Test
        @DisplayName("a bare number is a constant base")
        void parsesConstant() {
            Random parsed = Random.parseStr("5");

            assertEquals(5, parsed.getBase());
            assertEquals(0, parsed.getDice());
            assertEquals(0, parsed.getSides());
            assertEquals(0, parsed.getMBonus());
        }

        /**
         * A {@code d} with no preceding number implies one die ({@code values[1] = 1} in C).
         */
        @Test
        @DisplayName("a die with no count is one die")
        void parsesImpliedSingleDie() {
            Random parsed = Random.parseStr("d4");

            assertEquals(0, parsed.getBase());
            assertEquals(1, parsed.getDice());
            assertEquals(4, parsed.getSides());
        }

        /**
         * A negated value carries the shifted base {@code -base - m_bonus - dice * (sides + 1)}:
         * {@code "-1d4"} is {@code -5} and {@code "-5"} is just {@code -5}.
         */
        @Test
        @DisplayName("negation shifts the parsed base as parse_random does")
        void negatedBaseIsShifted() {
            Random dice = Random.parseStr("-1d4");
            Random constant = Random.parseStr("-5");

            assertEquals(-5, dice.getBase());
            assertEquals(-5, constant.getBase());
            assertEquals(0, constant.getDice());
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

        /**
         * Copying a negated value keeps its already-shifted base and does not shift it again, which
         * is C's struct copy: the base was settled when the value was parsed.
         */
        @Test
        @DisplayName("a copy of a negated value keeps the shifted base")
        void copyOfNegatedKeepsBase() {
            Random original = Random.parseStr("-1d4");
            Random duplicate = original.copy();

            assertEquals(-5, duplicate.getBase());
            assertEquals(-4, duplicate.randCalc(0, DamageAspect.MINIMIZE));
            assertEquals(-1, duplicate.randCalc(0, DamageAspect.MAXIMIZE));
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

        /**
         * The constructor's negate flag applies the shift straight away: {@code 2 + 1d4 M3} negated
         * has base {@code -2 - 3 - 1*(4+1) = -10}, which runs from -9 to -3, the exact reflection of
         * the positive 3 to 9.
         */
        @Test
        @DisplayName("the constructor negates as it builds")
        void constructorNegates() {
            Random value = new Random(2, 3, 1, 4, true);

            assertEquals(-10, value.getBase());
            assertEquals(-9, value.randCalc(0, DamageAspect.MINIMIZE));
            assertEquals(-3, value.randCalc(0, DamageAspect.MAXIMIZE));
        }

        /**
         * Without the flag the constructor leaves the base alone.
         */
        @Test
        @DisplayName("the constructor does not negate without the flag")
        void constructorLeavesBaseAlone() {
            Random value = new Random(2, 3, 1, 4, false);

            assertEquals(2, value.getBase());
        }

        /**
         * A value already negated by the constructor cannot be negated a second time through the
         * setter, so the base stays at its once-shifted figure.
         */
        @Test
        @DisplayName("a value negated by the constructor cannot be negated again")
        void constructorNegationIsFinal() {
            Random value = new Random(2, 3, 1, 4, true);

            value.setToNegate(true);
            value.negate();

            assertEquals(-10, value.getBase());
        }
    }
}
