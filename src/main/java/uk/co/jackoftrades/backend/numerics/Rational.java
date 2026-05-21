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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.numerics;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class Rational {
    private int denominator;
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
    @Contract(value = "_, _, 0 -> fail", mutates = "this")
    public Rational(int numerator, int denominator, boolean simplify) throws IllegalArgumentException {
        if (denominator == 0) throw new IllegalArgumentException("Divide by zero not allowed.");

        this.numerator = numerator;
        this.denominator = denominator;

        if (simplify) this.simplify();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an <dfn>{@index "equivalence relation"}</dfn>
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     * </ul>
     *
     * <p>
     * An equivalence relation partitions the elements it operates on
     * into <i>equivalence classes</i>; all the members of an
     * equivalence class are equal to each other. Members of an
     * equivalence class are substitutable for each other, at least
     * for some purposes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @implSpec The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * In other words, under the reference equality equivalence
     * relation, each equivalence class only has a single element.
     * @apiNote It is generally necessary to override the {@link #hashCode() hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     * <p>The two-argument {@link Objects#equals(Object,
     * Object) Objects.equals} method implements an equivalence relation
     * on two possibly-null object references.
     * @see #hashCode()
     * @see HashMap
     */
    @Contract(value = "null -> false", mutates = "this")
    @CheckReturnValue
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Rational)) return false;

        Rational other = (Rational) obj;

        if (denominator == 0 && other.denominator == 0) return true;
        if (other.denominator == 0 || denominator == 0) return false;

        this.simplify();
        other.simplify();

        return this.numerator == other.numerator && this.denominator == other.denominator;
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
}