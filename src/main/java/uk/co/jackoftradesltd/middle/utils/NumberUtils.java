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

package uk.co.jackoftradesltd.middle.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.middle.numerics.Rational;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Static integer and statistics helpers, the Java form of the arithmetic utilities in {@code z-util.c}: the four
 * saturating "guard" functions ({@code add_guardi}, {@code sub_guardi}, {@code add_guardi16},
 * {@code sub_guardi16}), {@code mean} and {@code variance}. It also holds {@link #cmp}, which stands in for the
 * {@code CMP} macro in {@code h-basic.h}.
 *
 * <p>The guards reproduce C's saturating arithmetic: rather than letting an addition or subtraction overflow, the
 * result is clamped to the {@code int} range or to the {@code int16_t} range. They test before they add, so Java's
 * wrapping never comes into it.
 *
 * <p>{@link #mean} and {@link #variance} return an exact {@link Rational} where C returns an {@code int} and fills in
 * a {@code struct my_rational} for the fractional part. The integer and the fraction are folded into one value, and
 * C's {@code frac == NULL} mode, which rounds to the nearest integer, has no counterpart. Both clamp {@code size} to
 * the length of the list, where C would read past the end of its array. {@code NumberUtilsCParityTest} pins both, and
 * the guards, against figures printed by the compiled C, including values near {@code INT_MIN} and {@code INT_MAX}.
 *
 * <p><b>Accepted deviation (2026-10-14, recorded in {@code docs/Accepted_Deviations.md}):</b> for a variance between
 * {@code INT_MAX} and {@code INT_MAX + 1}, C returns {@code INT_MAX} with the fractional part kept, and
 * {@link #variance} returns {@code INT_MAX} exactly. Every other value agrees with C.
 *
 * <p>Class NumberUtils coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public abstract class NumberUtils {
    /**
     * Shared logger. Nothing in this class logs through it; it is here for diagnostics in this static-only holder.
     *
     * <p>Field logger coded before 261014, commented in full on 261014.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Private constructor preventing instantiation of this static-only utility class. C has no counterpart, as
     * {@code z-util.c} is a file of free functions.
     *
     * <p>Function NumberUtils() coded before 261014, commented in full on 261014.
     */
    private NumberUtils() {
    }

    /**
     * Returns {@code a + b}, clamped to the {@code int} range if the sum would overflow. This is C's
     * {@code add_guardi} ({@code z-util.c}). When {@code a} is negative the sum can only underflow, and it does so when
     * {@code b} is negative and {@code a} is below {@code Integer.MIN_VALUE - b}; when {@code a} is zero or positive the
     * sum can only overflow, and it does so when {@code b} is positive and {@code a} is above
     * {@code Integer.MAX_VALUE - b}. The comparisons are arranged so that none of them can itself overflow.
     *
     * <p>For example {@code addGuardI(Integer.MAX_VALUE, 1)} is {@code Integer.MAX_VALUE},
     * {@code addGuardI(Integer.MIN_VALUE, -1)} is {@code Integer.MIN_VALUE}, and {@code addGuardI(-5, 3)} is -2.
     *
     * <p>Function addGuardI coded on 260830, commented in full on 261014.
     *
     * @param a one of the integers to be added
     * @param b one of the integers to be added
     * @return {@code a + b} if it lies in {@code Integer.MIN_VALUE} to {@code Integer.MAX_VALUE}, otherwise whichever
     *         limit it passed
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int addGuardI(int a, int b) {
        if (a < 0)
            return b >= 0 || (b > Integer.MIN_VALUE && a >= Integer.MIN_VALUE - b)
                 ? a + b
                 : Integer.MIN_VALUE;
        return b <= 0 || a <= Integer.MAX_VALUE - b
             ? a + b
             : Integer.MAX_VALUE;
    }

    /**
     * Returns {@code a - b}, clamped to the {@code int} range if the difference would overflow. This is C's
     * {@code sub_guardi} ({@code z-util.c}). When {@code a} is negative the difference can only underflow, and it does
     * so when {@code b} is positive and {@code a} is below {@code Integer.MIN_VALUE + b}; when {@code a} is zero or
     * positive it can only overflow, and it does so when {@code b} is negative and {@code a} is above
     * {@code Integer.MAX_VALUE + b}.
     *
     * <p>For example {@code subGuardI(0, Integer.MIN_VALUE)} is {@code Integer.MAX_VALUE}, because the true answer is
     * one past the top, and {@code subGuardI(-1, Integer.MAX_VALUE)} is {@code Integer.MIN_VALUE}, which is exact.
     *
     * <p>Function subGuardI coded on 260830, commented in full on 261014.
     *
     * @param a the number to subtract from
     * @param b the number to subtract
     * @return {@code a - b} if it lies in {@code Integer.MIN_VALUE} to {@code Integer.MAX_VALUE}, otherwise whichever
     *         limit it passed
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int subGuardI(int a, int b) {
        if (a < 0) {
            return b <= 0 || a >= Integer.MIN_VALUE + b
                    ? a - b
                    : Integer.MIN_VALUE;
        }

        return b >= 0 || a <= Integer.MAX_VALUE + b
                ? a - b
                : Integer.MAX_VALUE;
    }

    /**
     * Returns {@code a + b}, clamped to the range -32768 to 32767 (C's {@code int16_t}). This is C's
     * {@code add_guardi16} ({@code z-util.c}), which curse application uses on the armour class, the to-hit and
     * to-damage bonuses and the modifiers of an object. The method is the same shape as {@link #addGuardI} with the
     * 16-bit limits written as literals.
     *
     * <p>C's parameters are {@code int16_t}, so a caller cannot hand it a value outside the range. These parameters are
     * {@code int} and are not narrowed, so an argument outside -32768 to 32767 is not clamped on the way in:
     * {@code addGuardI16(40000, 0)} is 40000. Callers must pass values that are already 16-bit. C also tests
     * {@code b > -32768} before the underflow comparison; here that test is left out, because with {@code a} negative
     * and {@code b} equal to -32768 the comparison {@code a >= 0} is already false, so the result is the same.
     *
     * <p>Function addGuardI16 coded before 261014, commented in full on 261014.
     *
     * @param a one of the integers to add, in the 16-bit range
     * @param b one of the integers to add, in the 16-bit range
     * @return {@code a + b} if it lies in -32768 to 32767, -32768 if it is below that, or 32767 if it is above
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int addGuardI16(int a, int b) {
        int maxValue = 32767;
        int minValue = -32768;

        if (a < 0) {
            return b >= 0 || a >= minValue - b
                    ? a + b
                    : minValue;
        }

        return b <= 0 || a <= maxValue - b
                ? a + b
                : maxValue;
    }

    /**
     * Returns {@code a - b}, clamped to the range -32768 to 32767 (C's {@code int16_t}). This is C's
     * {@code sub_guardi16} ({@code z-util.c}) and is the same shape as {@link #subGuardI} with the 16-bit limits written
     * as literals. As with {@link #addGuardI16}, the arguments are {@code int} and are not narrowed to 16 bits, so
     * callers must pass values that are already in range.
     *
     * <p>For example {@code subGuardI16(0, -32768)} is 32767, because the true answer is 32768, and
     * {@code subGuardI16(-1, 32767)} is -32768, which is exact.
     *
     * <p>Function subGuardI16 coded before 261014, commented in full on 261014.
     *
     * @param a the number to subtract from, in the 16-bit range
     * @param b the number to subtract, in the 16-bit range
     * @return {@code a - b} if it lies in -32768 to 32767, -32768 if it is below that, or 32767 if it is above
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int subGuardI16(int a, int b) {
        int maxValue = 32767;
        int minValue = -32768;

        if (a < 0) {
            return b <= 0 || a >= minValue + b
                    ? a - b
                    : minValue;
        }

        return b >= 0 || a <= maxValue + b
                ? a - b
                : maxValue;
    }

    /**
     * Returns the exact arithmetic mean of the first {@code size} entries of {@code numbers} as a {@link Rational}.
     * This is C's {@code mean} ({@code z-util.c}), whose only caller is {@code obj-randart.c}; nothing in the port
     * calls this method yet.
     *
     * <p>C returns an {@code int} and, when given a {@code frac} pointer, the fractional part as a
     * {@code struct my_rational}. With {@code frac} set the integer is the largest value not above the true mean, so
     * the mean of {-1, -2} is -2 with a fraction of 1/2. With {@code frac} null the integer is the mean rounded to the
     * nearest. Here the integer and the fraction are one value, so {-1, -2} gives -3/2, and the round-to-nearest mode
     * has no counterpart. The sum is built with {@link Rational#add}, which is exact for any list of {@code int}s that
     * can be held in memory, then divided by the count with {@link Rational#div}.
     *
     * <p>A {@code size} of zero or less gives {@link Rational#zero}, as C returns 0 with a fraction of 0/1. An empty
     * list also gives zero, where C would read past the end of the array. A {@code size} larger than the list is cut
     * back to the length of the list, and the mean is taken over that many entries, where C would read past the end of
     * its array.
     *
     * <p>Function mean coded before 261014, commented in full on 261014.
     *
     * @param numbers the values to average
     * @param size    how many entries from the front of {@code numbers} to average
     * @return the mean of the first {@code size} entries, or {@link Rational#zero} if {@code size} is not positive or
     *         the list is empty
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static @NotNull Rational mean(@NotNull ArrayList<Integer> numbers, int size) {
        if (size <= 0 || numbers.isEmpty()) {
            return Rational.zero;
        }

        int newSize = Math.min(size, numbers.size());

        Rational sum = numbers.subList(0, newSize)
                .stream()
                .map(Rational::new)
                .reduce(Rational.zero, Rational::add);

        return sum.div(new Rational(newSize));
    }

    /**
     * Returns the exact variance of the first {@code size} entries of {@code numbers} as a {@link Rational}. This is
     * C's {@code variance} ({@code z-util.c}), whose only caller is {@code obj-randart.c}; nothing in the port calls
     * this method yet.
     *
     * <p>With {@code n} entries, {@code sum} their total and {@code squares} the total of their squares, the value is
     * {@code (n * squares - sum * sum) / (n * norm * (ofMean ? n : 1))}, where {@code norm} is {@code n - 1} if
     * {@code unbiased} is true and {@code n} if not. That is C's {@code (sum(x^2) - sum(x)^2 / n) / norm}, divided by a
     * further {@code n} when {@code ofMean} is true, with C's multi-limb remainders folded into one exact fraction.
     * The sums are {@link BigInteger}s, so no step overflows, and the result is reduced by the greatest common divisor.
     *
     * <p>A {@code size} of one or less gives {@link Rational#zero}, as C returns 0 with a fraction of 0/1. A list of
     * one entry or an empty list also gives zero, and a {@code size} larger than the list is cut back to the length of
     * the list, where C would read past the end of its array.
     *
     * <p>C's {@code frac == NULL} mode, which rounds to the nearest integer, has no counterpart. When the variance is
     * above {@code Integer.MAX_VALUE}, the result is {@code Integer.MAX_VALUE} exactly, as C returns it with a fraction
     * of 0/1. C cuts off a little later: it keeps the fractional part for a value between {@code INT_MAX} and
     * {@code INT_MAX + 1} and saturates from {@code INT_MAX + 1}. This is an accepted deviation (2026-10-14, recorded in
     * {@code docs/Accepted_Deviations.md}); no input found in testing reaches the difference.
     *
     * <p><b>Not yet decided:</b> the final conversion to {@code long} throws an {@link ArithmeticException} when the
     * reduced numerator or denominator does not fit in 63 bits, where C approximates the fraction and carries on. It
     * needs a long list and large values together, and depends on the data: 5000 entries drawn by
     * {@code java.util.Random(1)} from +/-3,000,000, with {@code ofMean} true, throws, and 3000 entries from the same
     * spread does not. Where the exact fraction fits in a {@code long} but not in 32 bits, it differs from C's
     * approximation by a few parts in 10^10, which is the {@link Rational} accepted deviation.
     *
     * <p>Function variance coded before 261014, commented in full on 261014.
     *
     * @param numbers  the values to measure
     * @param size     how many entries from the front of {@code numbers} to use
     * @param unbiased true to divide by {@code n - 1}, false to divide by {@code n}
     * @param ofMean   true to divide by a further {@code n}, giving the variance of the estimate of the mean
     * @return the variance of the first {@code size} entries, {@link Rational#zero} if there are fewer than two, or
     *         {@code Integer.MAX_VALUE} if the variance is above it
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static @NotNull Rational variance(@NotNull ArrayList<Integer> numbers, int size, 
                                             boolean unbiased, boolean ofMean)
    {
        // Deal with division by zero errors
        if (size <= 1 || numbers.size() == 1 || numbers.isEmpty()) return Rational.zero;

        int n = Math.min(size, numbers.size());

        BigInteger total = BigInteger.ZERO;
        BigInteger sqTotal = BigInteger.ZERO;
        for (int index = 0; index < n; index++) {
            BigInteger number = BigInteger.valueOf(numbers.get(index));
            total = total.add(number);
            sqTotal = sqTotal.add(number.multiply(number));
        }
        
        BigInteger bigN = BigInteger.valueOf(n);
        BigInteger numerator = bigN.multiply(sqTotal).subtract(total.multiply(total));
        BigInteger denominator = bigN.multiply(BigInteger.valueOf(unbiased ? n - 1 : n))
                .multiply(BigInteger.valueOf(ofMean ? n : 1));
        
        if (numerator.compareTo(denominator.multiply(BigInteger.valueOf(Integer.MAX_VALUE))) > 0) {
            return new Rational(Integer.MAX_VALUE);
        }
        
        BigInteger gcd = numerator.gcd(denominator);
        return new Rational(numerator.divide(gcd).longValueExact(), denominator.divide(gcd).longValueExact());
    }

    /**
     * Three-way comparison of two integers, standing in for the {@code CMP} macro in {@code h-basic.h}, which gives -1
     * if {@code a < b}, 1 if {@code b < a} and 0 otherwise. The only caller is {@code ItemObject.compareObjectTrait},
     * whose results {@code is_object_good} in {@code obj-ignore.c} sums with weights of 4, 2 and 1, so it needs exactly
     * -1, 0 or 1.
     *
     * <p>{@link Integer#compare} documents only the sign of its result, but the OpenJDK implementation returns exactly
     * -1, 0 or 1, so the two agree in practice. A JDK that returned another negative or positive value would break the
     * weighting.
     *
     * <p>Function cmp coded before 261014, commented in full on 261014.
     *
     * @param int1 the first integer
     * @param int2 the second integer
     * @return -1, 0 or 1 as {@code int1} is less than, equal to or greater than {@code int2}
     */
    public static int cmp(int int1, int int2) {
        return Integer.compare(int1, int2);
    }
}