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

package uk.co.jackoftrades.backend.utils.combiners;

import uk.co.jackoftrades.backend.utils.Combiner;
import uk.co.jackoftrades.backend.utils.UIEntryCombinerState;

import java.util.List;

/**
 * The LARGEST combiner: folds several contributions down to the largest of
 * them. This is the {@code "LARGEST"} row of the C original's UI-entry combiner
 * table ({@code ui-entry-combiner.c}: {@code simple_combine_init},
 * {@code largest_combine_accum}, {@code dummy_combine_finish},
 * {@code largest_vec}).
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and the
 * two channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) take their respective
 * maximum independently but in lock-step.
 *
 * <p>Two {@code int} values are reserved sentinels rather than real numbers, and
 * are handled specially rather than being compared arithmetically:
 * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} (no contribution - skipped) and
 * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} (unknown to the player - which infects
 * an otherwise-absent accumulator).
 *
 * @author Rowan Crowther
 */
public class LargestCombiner implements Cloneable, Combiner {
    /**
     * Returns a shallow clone of this combiner, as declared by {@link Combiner}.
     *
     * @return a shallow copy of this instance
     * @throws CloneNotSupportedException if cloning is not supported
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private UIEntryCombinerState state;

    /**
     * Begins a fresh streaming fold, seeding both accumulator channels with the
     * first contribution.
     *
     * @param v the value channel of the first contribution
     * @param a the auxiliary channel of the first contribution
     */
    @Override
    public void init(int v, int a) {
        state = new UIEntryCombinerState();
        state.setAccum(v);
        state.setAccumAux(a);
    }

    /**
     * Folds one further contribution into the running state, keeping the larger
     * of the incoming and stored value on each channel via
     * {@link #largestCombineAccumHelp}. Sentinel inputs are handled by that helper
     * rather than being compared.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        int largestAccum = largestCombineAccumHelp(v, state.getAccum());
        state.setAccum(largestAccum);
        int largestAccumAux = largestCombineAccumHelp(a, state.getAccumAux());
        state.setAccumAux(largestAccumAux);
    }

    /**
     * Completes the streaming fold. LARGEST needs no finishing step (the C
     * original uses {@code dummy_combine_finish}), so this hands back the state
     * holding the maximum value and auxiliary channels.
     *
     * @return the combined state after all contributions
     */
    @Override
    public UIEntryCombinerState finish() {
        return state;
    }

    /**
     * One-shot array form of the fold, the counterpart to C's
     * {@code largest_vec}: reduces whole arrays of values and auxiliaries to
     * their maxima in a single call instead of streaming them through
     * {@code init}/{@code accum}. The first element of each array seeds its
     * channel (or {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} when {@code n == 0}),
     * and the remainder are folded in with the same sentinel-aware helper as
     * {@link #accum}.
     *
     * <p>Unlike the streaming path this does not touch or depend on the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the maximum value and auxiliary channels, or
     * {@code null} if either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        if (values.size() < n || auxs.size() < n)
            return null;

        int storage = (n > 0) ? values.get(0) : UI_ENTRY_VALUE_NOT_PRESENT;
        int accum;

        for (int index = 1; index < n; index++) {
            accum = largestCombineAccumHelp(values.get(index), storage);
            storage = accum;
        }
        accum = storage;

        int accumAux;
        storage = (n > 0) ? auxs.get(0) : UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 1; index < n; index++) {
            accumAux = largestCombineAccumHelp(auxs.get(index), storage);
            storage = accumAux;
        }
        accumAux = storage;

        UIEntryCombinerState result = new UIEntryCombinerState();
        result.setAccum(accum);
        result.setAccumAux(accumAux);

        return result;
    }

    /**
     * Keeps the larger of a single contribution {@code x} and a running
     * accumulator, honouring the reserved sentinels; the C original mutates
     * {@code *accum} in place, whereas this returns the new accumulator (Java has
     * no pointer-to-int).
     *
     * <p>The rules, in order: a NOT_PRESENT contribution changes nothing; an
     * UNKNOWN contribution turns an absent accumulator UNKNOWN but otherwise
     * leaves it alone; otherwise the contribution wins if the accumulator is
     * absent or unknown, or if the contribution is strictly greater.
     *
     * @param x     the incoming contribution (possibly a sentinel)
     * @param accum the current accumulator (possibly a sentinel)
     * @return the updated accumulator (the larger of the two)
     */
    public int largestCombineAccumHelp(int x, int accum) {
        int result = accum;

        if (x == UI_ENTRY_VALUE_NOT_PRESENT)
            return result;

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (accum == UI_ENTRY_VALUE_NOT_PRESENT)
                result = UI_ENTRY_UNKNOWN_VALUE;
            return result;
        }
        if (accum == UI_ENTRY_UNKNOWN_VALUE || accum == UI_ENTRY_VALUE_NOT_PRESENT
                || accum < x) {
            return x;
        }

        return result;
    }
}
