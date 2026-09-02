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

package uk.co.jackoftradesltd.channel.utils.combiners;

import uk.co.jackoftradesltd.channel.utils.Combiner;
import uk.co.jackoftradesltd.channel.utils.UIEntryCombinerState;

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
     * Returns an independent copy of this combiner, as declared by
     * {@link Combiner}. The copy gets its own {@link UIEntryCombinerState} holding
     * the same four channels, so folding through one combiner cannot disturb the
     * other; a shallow field copy would share the one state object and defeat the
     * point of cloning at all.
     *
     * <p>Cloning exists only because the port keeps the fold state inside the
     * combiner. The C original passes a caller-owned
     * {@code struct ui_entry_combiner_state} into every function, leaving the
     * combiners themselves stateless and freely shareable, so it needs no
     * equivalent.
     *
     * <p>A combiner that has not been {@link #init(int, int) init}-ed yet has no
     * state to copy, so the clone is a fresh instance - which is the case
     * {@code CombinerName} actually exercises, since it clones an un-initialised
     * prototype per fold.
     *
     * @return an independent copy of this combiner
     */
    @Override
    public final Combiner clone() {
        if (state == null) {
            return new LargestCombiner();
        }

        UIEntryCombinerState thisState = state;
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(thisState.getAccum());
        newState.setAccumAux(thisState.getAccumAux());
        newState.setNegAccum(thisState.getNegAccum());
        newState.setNegAccumAux(thisState.getNegAccumAux());

        LargestCombiner newCombiner = new LargestCombiner();
        newCombiner.state = newState;

        return newCombiner;
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
     * original uses {@code dummy_combine_finish}), so the accumulated channels are
     * already the answer and nothing is computed here.
     *
     * <p>The result is a fresh {@link UIEntryCombinerState} rather than this
     * combiner's own, so a caller cannot reach through the returned object and
     * disturb a fold that may still be running. Repeated calls are therefore
     * independent snapshots, and writing to one is invisible to both the combiner
     * and any other snapshot.
     *
     * @return a snapshot of the maximum value and auxiliary channels
     */
    @Override
    public UIEntryCombinerState finish() {
        UIEntryCombinerState finalState = new UIEntryCombinerState();

        finalState.setAccum(state.getAccum());
        finalState.setAccumAux(state.getAccumAux());
        finalState.setNegAccum(state.getNegAccum());
        finalState.setNegAccumAux(state.getNegAccumAux());

        return finalState;
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
