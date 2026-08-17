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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

/**
 * An exact fraction (numerator / denominator) kept in lowest terms, used where
 * the game needs precise fractional arithmetic without floating-point drift —
 * for example accumulating fractional regeneration or growth rates that must
 * stay exact over many turns. Fractions are simplified on construction so that
 * equal values share a single canonical representation and never balloon into
 * huge numerator/denominator pairs.
 *
 * @author Rowan Crowther
 */
public class Rational {
    /**
     * The denominator; kept positive and coprime with the numerator after {@link #simplify()}.
     */
    private int denominator;
    /**
     * The numerator; carries the sign of the fraction after {@link #simplify()}.
     */
    private int numerator;

    /**
     * Public static Rational with the value of 0, used for a quick creation/test
     */
    public static Rational zero = new Rational(0, 1);

    /**
     * Public static Rational with the value of 1, used for a quick creation/test
     */
    public static Rational one = new Rational(1, 1);

    /**
     * Getter for the numerator
     *
     * @return the numerator
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getNumerator() {
        return numerator;
    }

    /**
     * Getter for the denominator
     *
     * @return the denominator
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getDenominator() {
        return denominator;
    }

    /**
     * Single value constructor, default denominator of 1 used
     *
     * @param numerator the value of the numerator for this Rational
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Rational(int numerator) {
        this.numerator = numerator;
        denominator = 1;
    }

    /**
     * Returns the Rational formed by multiplying this by the incoming rational
     *
     * @param rational the rational to multiply this by
     * @return a simplified version of the rational of this * rational
     */
    @Contract(pure = true)
    @CheckReturnValue
    public Rational multi(@NotNull Rational rational) {
        if (this.denominator == 0 || rational.denominator == 0)
            return new Rational(0, 1);

        Rational result = new Rational(this.numerator * rational.numerator, this.denominator * rational.denominator);
        result.simplify();
        return result;
    }

    /**
     * Returns the result of dividing this rational by another one
     *
     * @param other the other rational to divide this one by
     * @return a new rational equal to this / other
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational div(@NotNull Rational other) {
        Rational flipped = new Rational(other.getDenominator(), other.getNumerator());
        Rational result = this.multi(flipped);
        result.simplify();
        return result;
    }

    /**
     * Calculates the result of adding this to another rational
     *
     * @param rational the rational we are adding to this
     * @return a new rational equal to this + rational
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational add(@NotNull Rational rational) {
        int otherNumerator = rational.getNumerator();
        int otherDenominator = rational.getDenominator();

        Rational result = new Rational(this.numerator * otherDenominator + otherNumerator * this.denominator, otherDenominator * this.denominator);
        result.simplify();
        return result;
    }

    /**
     * Two value constructor. Uses the three value constructor to create the Rational
     *
     * @param numerator   the numerator of the new Rational
     * @param denominator the denominator of the new Rational
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Rational(int numerator, int denominator) {
        this(numerator, denominator, true);
    }

    /**
     * Three argument constructor
     *
     * @param numerator   the numerator of the Rational
     * @param denominator the denominator of the Rational
     * @param simplify    whether this Rational should be simplified before being stored
     * @throws IllegalArgumentException if the denominator is 0 an illegal argument exception is thrown
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Rational(int numerator, int denominator, boolean simplify) throws IllegalArgumentException {
        if (denominator == 0) throw new IllegalArgumentException("Divide by zero not allowed.");

        this.numerator = numerator;
        this.denominator = denominator;

        if (simplify) this.simplify();
    }

    /**
     * Compares this rational with another by <em>value</em> rather than by representation, so
     * {@code 1/2} equals {@code 2/4}. Both sides are simplified into throwaway copies before
     * their numerators and denominators are compared, which is what lets two rationals that were
     * never simplified in place still compare equal.
     *
     * <p>The zero-denominator branches are defensive and currently unreachable: the three-argument
     * constructor rejects a zero denominator outright, the one-argument constructor fixes the
     * denominator at 1, there are no setters, and {@link #simplify()} divides by a GCD that is
     * itself never zero. They are kept because they are cheap and because they pair with the same
     * guard in {@link #multi} and {@link #hashCode()} — but a zero-denominator {@code Rational}
     * cannot be built, so nothing exercises them.
     *
     * @param obj the object to compare against
     * @return {@code true} if {@code obj} is a {@link Rational} of the same value
     */
    @Contract(value = "null -> false")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Rational other) {

            if (denominator == 0 && other.getDenominator() == 0) return true;
            if (other.getDenominator() == 0 || denominator == 0) return false;

            Rational thisRational = new Rational(this.numerator, this.denominator, true);
            Rational objRational = new Rational(other.getNumerator(), (other.getDenominator()), true);

            return thisRational.numerator == objRational.numerator && thisRational.denominator == objRational.denominator;
        }

        return false;
    }

    /**
     * Calculates the Greatest Common Denominator of two numbers using the Euclidean algorithm
     * @param a a number to calculate the GCD of
     * @param b another number to calculate the GCV of
     * @return the greatest common denominator of a and b
     */
    @Contract(pure = true)
    @CheckReturnValue
    private long getGCD(long a, long b) {
        return (b == 0)
                ? a : getGCD(b, a % b);
    }

    /**
     * Simplifies this into a form whereby the values of the denominator and numerator are the smallest possible.
     * This is used to ensure we don't end up with representations of 1/2 where each of the numerator and denominator
     * have over 100 digits!
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    private void simplify() {
        // do the calculation
        int whole = numerator / denominator;
        numerator = numerator - whole * denominator;
        long gcd = getGCD(numerator, denominator);

        numerator = (int) ((numerator + whole * denominator) / gcd);
        denominator = (int) (denominator / gcd);

        // Flip any negative sign to the numerator
        if (denominator < 0 && numerator > 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    /**
     * Get the integer part of this i.e. 7, 2 would return 1
     * @return The integer part of this Rational
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getIntegerPart()
    {
        return getIntegerPart(1);
    }

    /**
     * Return the integer part of this Rational scaled up by an integer
     * @param scale The integer to scale this rational by
     * @return THe integer part of this rational, once it has been scaled
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getIntegerPart(int scale) {
        Rational scaled = new Rational(numerator * scale, denominator, true);
        return scaled.getNumerator() / scaled.getDenominator();
    }

    /**
     * Subtract one rational from this rational
     * @param other The rational to subtract
     * @return A new Rational consisting of this - other
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational sub(@NotNull Rational other) {
        return this.add(other.multi(new Rational(-1)));
    }

    /**
     * Get the remainder of this Rational
     * @return The remainder of the rational
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational getRemainder() {
        return this.sub(new Rational(getIntegerPart()));
    }

    /**
     * Overrides the toString method to show something useful
     * @return a string representation of this Rational
     */
    @Override
    @CheckReturnValue
    @Contract(pure = true)
    public String toString() {
        return numerator + " / " + denominator;
    }

    /**
     * Returns the integer value of the rational multiplied by scale (a) where (a*c+b) = as an integer
     *
     * @param scale The integer to scale the rational by
     * @return the integer value of the rational times the scale
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int toUint(int scale) {
        return multi(new Rational(scale)).getIntegerPart();
    }

    /**
     * Hashes this rational by <em>value</em>, so that any two rationals {@link #equals} calls equal
     * hash alike — {@code 1/2} and {@code 2/4} included.
     *
     * <p>The hash is taken from a throwaway simplified copy rather than from the fields directly,
     * which is the whole point of the method: a rational built with simplification suppressed
     * ({@code new Rational(2, 4, false)}) keeps its raw pair, so hashing the fields would give it a
     * different hash from the {@code 1/2} it compares equal to. Building the copy the same way
     * {@link #equals} does keeps the two consistent by construction, including any future change to
     * {@link #simplify()}, its sign handling, or {@link #getGCD}.
     *
     * <p>The zero-denominator guard mirrors the unreachable one in {@link #equals} and exists for
     * the same reason; see that method for why no such rational can be built.
     *
     * @return a hash consistent with {@link #equals}
     */
    @Override
    public int hashCode() {
        if (denominator == 0) return 0;

        Rational simplified = new Rational(numerator, denominator, true);
        return Objects.hash(simplified.numerator, simplified.denominator);
    }
}