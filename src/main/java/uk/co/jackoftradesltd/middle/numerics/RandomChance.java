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

/**
 * An "X in Y" chance of success, such as 8 in 125 (6.4%), the Java form of C's
 * {@code random_chance} struct ({@code z-rand.h}) with its two functions {@code random_chance_check}
 * and {@code random_chance_scaled} ({@code z-rand.c}).
 *
 * <p>C passes the struct by value and lets the caller fill both members in place; this class is
 * immutable and the constructor takes both. Neither C nor this class checks that the pair makes
 * sense: a numerator at or above the denominator is simply a chance that always succeeds, and a
 * numerator of zero or below one that never does (see {@link #check}). A denominator of zero is
 * accepted on construction and fails only when {@link #scale} divides by it; a negative
 * denominator fails in {@link #check}.
 *
 * <p>Nothing else in {@code src/main} constructs a {@code RandomChance} at present.
 *
 * <p>Class RandomChance coded before 261014, commented in full on 261014.
 */
public class RandomChance {
    /**
     * The {@code X} in an "X in Y" chance (number of favourable outcomes), C's
     * {@code random_chance.numerator} ({@code int32_t}). Not range-checked.
     *
     * <p>Field numerator coded before 261014, commented in full on 261014.
     */
    private final int numerator;
    /**
     * The {@code Y} in an "X in Y" chance (total number of outcomes), C's
     * {@code random_chance.denominator} ({@code int32_t}). Not range-checked on construction.
     *
     * <p>Field denominator coded before 261014, commented in full on 261014.
     */
    private final int denominator;

    /**
     * Builds the chance {@code numerator} in {@code denominator}. C has no constructor for this;
     * a caller declares a {@code random_chance} and assigns the two members, and this stores the
     * same two values unchanged. Nothing is validated here, so a bad pair is only noticed by
     * {@link #check} or {@link #scale}.
     *
     * <p>Constructor RandomChance coded before 261014, commented in full on 261014.
     *
     * @param numerator   the number X of (a X in Y chance)
     * @param denominator the number Y of (a X in Y chance)
     */
    public RandomChance(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

//    /**
//     * Getter for the numerator
//     * @return the numerator
//     */
//    public int getNumerator() {
//        return numerator;
//    }
//
//    /**
//     * Getter for the denominator
//     * @return the denominator
//     */
//    public int getDenominator() {
//        return denominator;
//    }

    /**
     * Scales this chance to the denominator given as {@code scale} and returns the matching
     * numerator, the port of {@code random_chance_scaled} ({@code z-rand.c}). A chance of 7 in 13
     * (53.8%) at scale 100 gives 53, and at scale 1000 gives 538.
     *
     * <p>The sum is {@code scale * numerator / denominator} in {@code int} arithmetic, as in C: the
     * product is formed first, so it can overflow and wrap for large operands, and the division
     * truncates towards zero, so the result is rounded down for positive operands. A
     * {@code denominator} of zero throws {@link ArithmeticException}, where C divides by zero.
     * Nothing is rolled; the result is the same every time.
     *
     * <p>Function scale coded before 261014, commented in full on 261014.
     *
     * @param scale the scale by which the ratio is multiplied
     * @return {@code scale * numerator / denominator}, the numerator of this chance over {@code scale}
     * @throws ArithmeticException if the denominator is zero
     */
    public int scale(int scale) {
        return scale * numerator / denominator;
    }

    /**
     * Rolls once on this chance and reports whether it succeeded, the port of
     * {@code random_chance_check} ({@code z-rand.c}).
     *
     * <p>The test is {@code randInt0(denominator) >= denominator - numerator}, so it is the high
     * rolls that pass, as C's comment says. {@code randInt0(denominator)} is uniform on
     * {@code 0..denominator-1}, and exactly {@code numerator} of those values reach the threshold (for
     * {@code 0 <= numerator <= denominator}),
     * giving the stated {@code numerator / denominator} chance. At the edges, for a denominator
     * above 1: a numerator of at least the denominator always succeeds, and a numerator of zero
     * or below never does. A denominator of 0 or 1 makes the roll a fixed 0, so 0 in 0, 1 in 1
     * and 5 in 0 succeed while 0 in 1 and -1 in 0 do not. A negative denominator throws
     * {@link IllegalArgumentException} from {@link RandomValueUtils#randInt0}, where C asserts.
     *
     * <p>Function check coded before 261014, commented in full on 261014.
     *
     * @return {@code true} if this roll succeeds and {@code false} if it fails
     * @throws IllegalArgumentException if the denominator is negative or above {@code 0x10000000}
     */
    public boolean check() {
        return RandomValueUtils.randInt0(denominator) >= denominator - numerator;
    }


}