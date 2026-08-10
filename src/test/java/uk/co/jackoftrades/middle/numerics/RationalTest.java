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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Rational}, the exact fraction type the game uses where
 * floating-point drift would accumulate over many turns.
 *
 * <p>The class keeps every value in lowest terms, so almost all of its behaviour is
 * observable through the numerator/denominator pair after construction or arithmetic. The
 * tests therefore assert on that canonical pair rather than on a decimal approximation:
 * {@code 6/4} and {@code 3/2} are the same value, but only one of them is a legal internal
 * state, and a simplification that silently stopped working would be invisible to a
 * value-only check while still letting the numerator overflow after enough additions.
 *
 * <p>Sign handling gets its own attention because the private GCD helper is the plain
 * Euclidean recursion, which returns a <em>negative</em> divisor when handed negative
 * inputs. That is not a bug in itself - dividing through by a negative GCD is what moves
 * the sign out of the denominator - but it means every combination of signs takes a
 * different path to the same canonical form, and each is pinned here.
 *
 * @author Rowan Crowther
 */
class RationalTest {

    /**
     * Asserts that a rational is in exactly the given canonical form.
     *
     * @param expectedNumerator   the numerator the rational should be reduced to
     * @param expectedDenominator the denominator the rational should be reduced to
     * @param actual              the rational under test
     */
    private static void assertFraction(int expectedNumerator, int expectedDenominator,
                                       Rational actual) {
        assertEquals(expectedNumerator, actual.getNumerator(),
                () -> "numerator of " + actual);
        assertEquals(expectedDenominator, actual.getDenominator(),
                () -> "denominator of " + actual);
    }

    @Test
    void toStringShowsBothHalvesOfTheReducedFraction() {
        assertEquals("3 / 2", new Rational(6, 4).toString());
        assertEquals("0 / 1", Rational.zero.toString());
    }

    /**
     * Construction, simplification and the canonical placement of the sign.
     */
    @Nested
    class Construction {

        @Test
        void singleArgumentConstructorGivesADenominatorOfOne() {
            assertFraction(7, 1, new Rational(7));
        }

        @Test
        void constructionReducesToLowestTerms() {
            assertFraction(3, 2, new Rational(6, 4));
            assertFraction(1, 3, new Rational(5, 15));
            assertFraction(7, 1, new Rational(70, 10));
        }

        @Test
        void alreadyReducedFractionsAreLeftAlone() {
            assertFraction(3, 7, new Rational(3, 7));
        }

        @Test
        void zeroNumeratorNormalisesToZeroOverOne() {
            // gcd(0, d) is d, so any 0/d collapses to the single canonical zero rather than
            // leaving a stray denominator behind for later arithmetic to multiply up.
            assertFraction(0, 1, new Rational(0, 5));
            assertFraction(0, 1, new Rational(0, -5));
        }

        @Test
        void suppressingSimplificationKeepsTheRawPair() {
            assertFraction(6, 4, new Rational(6, 4, false));
        }

        @Test
        void signIsCarriedByTheNumeratorWhicheverArgumentItArrivedOn() {
            assertFraction(-1, 2, new Rational(-4, 8));
            assertFraction(-1, 2, new Rational(4, -8));
        }

        @Test
        void twoNegativesCancelToAPositiveFraction() {
            assertFraction(1, 2, new Rational(-4, -8));
        }

        @Test
        void aZeroDenominatorIsRejected() {
            IllegalArgumentException thrown =
                    assertThrows(IllegalArgumentException.class, () -> new Rational(1, 0));
            assertTrue(thrown.getMessage().contains("Divide by zero"), thrown.getMessage());
        }

        @Test
        void aZeroDenominatorIsRejectedEvenWithSimplificationOff() {
            // The guard sits ahead of the simplify() call, so it must fire on both paths -
            // an unsimplified x/0 would otherwise sit in a field waiting to divide by zero.
            assertThrows(IllegalArgumentException.class, () -> new Rational(1, 0, false));
        }

        @Test
        void theSharedConstantsHoldTheirDocumentedValues() {
            assertFraction(0, 1, Rational.zero);
            assertFraction(1, 1, Rational.one);
        }
    }

    /**
     * The four arithmetic operations, each of which returns a fresh simplified rational.
     */
    @Nested
    class Arithmetic {

        @Test
        void additionOverACommonDenominator() {
            assertFraction(5, 6, new Rational(1, 2).add(new Rational(1, 3)));
        }

        @Test
        void additionSimplifiesItsResult() {
            // 1/6 + 1/3 is 3/6 before reduction; the result must not be left in that form.
            assertFraction(1, 2, new Rational(1, 6).add(new Rational(1, 3)));
        }

        @Test
        void additionOfOppositesGivesCanonicalZero() {
            assertFraction(0, 1, new Rational(1, 2).add(new Rational(-1, 2)));
        }

        @Test
        void additionLeavesBothOperandsUntouched() {
            Rational left = new Rational(1, 2);
            Rational right = new Rational(1, 3);

            left.add(right);

            assertFraction(1, 2, left);
            assertFraction(1, 3, right);
        }

        @Test
        void subtraction() {
            assertFraction(1, 6, new Rational(1, 2).sub(new Rational(1, 3)));
            assertFraction(-1, 6, new Rational(1, 3).sub(new Rational(1, 2)));
        }

        @Test
        void multiplication() {
            assertFraction(1, 6, new Rational(1, 2).multi(new Rational(1, 3)));
            assertFraction(1, 2, new Rational(2, 3).multi(new Rational(3, 4)));
        }

        @Test
        void multiplicationByZeroCollapsesToZero() {
            assertFraction(0, 1, new Rational(3, 4).multi(Rational.zero));
        }

        @Test
        void multiplicationByANegativeFlipsTheSign() {
            assertFraction(-3, 4, new Rational(3, 4).multi(new Rational(-1)));
        }

        @Test
        void division() {
            assertFraction(3, 2, new Rational(1, 2).div(new Rational(1, 3)));
            assertFraction(1, 2, new Rational(1, 4).div(new Rational(1, 2)));
        }

        @Test
        void divisionByANegativePutsTheSignOnTheNumerator() {
            // div() reciprocates its argument, so a negative divisor arrives at the
            // constructor as a negative *denominator* - the case the sign flip exists for.
            assertFraction(-1, 2, new Rational(1, 4).div(new Rational(-1, 2)));
        }

        @Test
        void dividingByZeroIsRejected() {
            // Reciprocating zero produces a zero denominator, which the constructor rejects.
            assertThrows(IllegalArgumentException.class,
                    () -> new Rational(1, 2).div(Rational.zero));
        }
    }

    /**
     * Splitting a rational into its whole and fractional parts.
     */
    @Nested
    class WholeAndFractionalParts {

        @Test
        void integerPartTruncatesTowardsZero() {
            assertEquals(3, new Rational(7, 2).getIntegerPart());
            assertEquals(0, new Rational(1, 2).getIntegerPart());
            assertEquals(-3, new Rational(-7, 2).getIntegerPart());
        }

        @Test
        void integerPartOfAWholeNumberIsItself() {
            assertEquals(4, new Rational(8, 2).getIntegerPart());
        }

        @Test
        void scalingBeforeTakingTheIntegerPart() {
            // 1/3 is 0 whole, but ten thirds is 3 - the scale is applied before truncation,
            // which is the whole point of the overload.
            assertEquals(0, new Rational(1, 3).getIntegerPart());
            assertEquals(3, new Rational(1, 3).getIntegerPart(10));
        }

        @Test
        void scalingByZeroGivesZero() {
            assertEquals(0, new Rational(7, 2).getIntegerPart(0));
        }

        @Test
        void remainderIsWhatIsLeftAfterTheIntegerPart() {
            assertFraction(1, 2, new Rational(7, 2).getRemainder());
            assertFraction(1, 3, new Rational(1, 3).getRemainder());
        }

        @Test
        void remainderOfAWholeNumberIsZero() {
            assertFraction(0, 1, new Rational(8, 2).getRemainder());
        }

        @Test
        void integerPartAndRemainderReassembleTheOriginal() {
            Rational value = new Rational(17, 5);

            Rational rebuilt = new Rational(value.getIntegerPart()).add(value.getRemainder());

            assertFraction(17, 5, rebuilt);
        }

        @Test
        void toUintScalesThenTruncates() {
            assertEquals(15, new Rational(3, 2).toUint(10));
            assertEquals(1, new Rational(1, 3).toUint(5));
            assertEquals(0, new Rational(1, 3).toUint(0));
        }
    }

    /**
     * Value equality, which is what lets equal fractions be compared regardless of the form
     * they were written in.
     */
    @Nested
    class Equality {

        @Test
        void fractionsWrittenDifferentlyAreStillEqual() {
            assertEquals(new Rational(1, 2), new Rational(2, 4));
            assertEquals(new Rational(1, 2), new Rational(-3, -6));
        }

        @Test
        void unsimplifiedFractionsCompareByValue() {
            // equals() re-simplifies both sides, so the simplify:false escape hatch does not
            // create a value that compares unequal to its own reduced form.
            assertEquals(new Rational(1, 2), new Rational(6, 12, false));
        }

        @Test
        void differentValuesAreNotEqual() {
            assertNotEquals(new Rational(1, 2), new Rational(1, 3));
            assertNotEquals(new Rational(1, 2), new Rational(-1, 2));
        }

        @Test
        void equalityIsReflexive() {
            Rational value = new Rational(3, 7);

            assertEquals(value, value);
        }

        @Test
        void nullAndForeignTypesAreNotEqual() {
            Rational value = new Rational(1, 2);

            assertNotEquals(null, value);
            assertFalse(value.equals("1 / 2"));
        }
    }
}
