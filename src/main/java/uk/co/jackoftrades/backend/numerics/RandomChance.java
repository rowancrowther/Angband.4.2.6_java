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

/**
 * A class representing a random chance of success, such as 12 in 13
 */
public class RandomChance {
    /**
     * The {@code X} in an "X in Y" chance (number of favourable outcomes).
     *
     * @author ClaudeCode
     */
    private final int numerator;
    /**
     * The {@code Y} in an "X in Y" chance (total number of outcomes).
     *
     * @author ClaudeCode
     */
    private final int denominator;

    /**
     * Constructor
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
     * Scale a random chance and return an appropriate integer
     *
     * @param scale the scale by which the ratio is multiplied
     * @return a value equivalent to the percentage chance of this random chance times scale being rolled
     */
    public int scale(int scale) {
        return scale * numerator / denominator;
    }

    /**
     * roll a check on this random chance and return true if it succeeds and false if it fails
     *
     * @return true if a check on this random chance succeeds and false if it fails
     */
    public boolean check() {
        return RandomValueUtils.randInt0(denominator) >= denominator - numerator;
    }


}