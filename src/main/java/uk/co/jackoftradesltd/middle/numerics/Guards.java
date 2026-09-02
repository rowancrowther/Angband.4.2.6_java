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
 * Integer arithmetic that saturates instead of wrapping — the port of C's {@code add_guardi} and
 * {@code sub_guardi} ({@code z-util.c:937}, {@code z-util.c:949}).
 *
 * <p>Object power sums figures that no data file could produce on its own but that a long chain of
 * curses and bonuses can push past the end of the range. C's answer is not to let the result wrap
 * round to a large negative — which would make a monstrous object look worthless — but to stop it at
 * the end of the range and leave it there. These two methods are that rule.
 *
 * <p><b>Widen, clamp, narrow.</b> C tests for the overflow before doing the arithmetic, because in C
 * signed overflow is undefined and must not be allowed to happen at all. Java has no such
 * prohibition and a wider type to hand, so the port promotes both operands to {@code long}, does the
 * sum where it cannot overflow, and clamps the result back into {@code int} range. Different route,
 * same answer for every input.
 *
 * <p><b>Only the 32-bit pair is ported.</b> C also has {@code add_guardi16} for its {@code int16_t}
 * fields, which saturates at ±32767; the port's equivalent fields are all {@code int}, so nothing
 * overflows at that width and the narrower guard has no work to do. The saturation <em>bound</em>
 * therefore differs from C's for those fields, at values no data file produces.
 *
 * <p>Class Guards commented in full on 260827.
 *
 * @author Rowan Crowther
 */
public class Guards {
    /**
     * Adds two integers, stopping at the end of the range rather than wrapping round it - the port
     * of C's {@code add_guardi}.
     *
     * <p>Both operands are widened to {@code long} first, so the addition itself cannot overflow and
     * the clamp sees the true sum.
     *
     * <p>Function addGuardI commented in full on 260827.
     *
     * @param a the first addend
     * @param b the second addend
     * @return {@code a + b}, or the nearest end of the {@code int} range if the true sum lies
     * outside it
     */
    public static int addGuardI(int a, int b) {
        long al = a;
        long bl = b;

        long result = Math.clamp(al + bl, Integer.MIN_VALUE, Integer.MAX_VALUE);

        return (int) result;
    }

    /**
     * Subtracts one integer from another, stopping at the end of the range rather than wrapping
     * round it - the port of C's {@code sub_guardi}.
     *
     * <p>Same widen-clamp-narrow shape as {@link #addGuardI}. Used by the curse pricing to take the
     * difference between two power figures, where either may itself already be saturated.
     *
     * <p>Function subGuardI commented in full on 260827.
     *
     * @param a the value subtracted from
     * @param b the value to subtract
     * @return {@code a - b}, or the nearest end of the {@code int} range if the true difference lies
     * outside it
     */
    public static int subGuardI(int a, int b) {
        long al = a;
        long bl = b;

        long result = Math.clamp(al - bl, Integer.MIN_VALUE, Integer.MAX_VALUE);

        return (int) result;
    }
}
