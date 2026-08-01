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
 * The FIRST combiner: keeps the first contribution and discards every later one.
 * This is the {@code "FIRST"} row of the C original's UI-entry combiner table
 * ({@code ui-entry-combiner.c}: {@code simple_combine_init},
 * {@code dummy_combine_accum}, {@code dummy_combine_finish}, {@code first_vec}).
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and both
 * channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) simply take their first
 * contributor. Because nothing is ever folded, this combiner needs no
 * sentinel-handling helper: whatever the first contribution is - real value or
 * reserved sentinel - is passed through verbatim.
 *
 * @author Rowan Crowther
 */
public class FirstCombiner implements Combiner, Cloneable {
    private UIEntryCombinerState state;

    /**
     * Seeds both channels with the first contribution. Since {@link #accum} is a
     * no-op, this seed is also the final result of a streaming fold.
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
     * Ignores this contribution: FIRST keeps only what {@link #init} seeded. This
     * mirrors the C original's {@code dummy_combine_accum}.
     *
     * @param v the value channel of this contribution (ignored)
     * @param a the auxiliary channel of this contribution (ignored)
     */
    @Override
    public void accum(int v, int a) {
        // Do nothing
    }

    /**
     * Completes the streaming fold. FIRST needs no finishing step (the C original
     * uses {@code dummy_combine_finish}), so the seeded channels are already the
     * answer and nothing is computed here.
     *
     * <p>The result is a fresh {@link UIEntryCombinerState} rather than this
     * combiner's own, so a caller cannot reach through the returned object and
     * disturb a fold that may still be running. Repeated calls are therefore
     * independent snapshots, and writing to one is invisible to both the combiner
     * and any other snapshot.
     *
     * @return a snapshot of the first contribution
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
     * One-shot array form, the counterpart to C's {@code first_vec}: returns
     * element 0 of each channel, or {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} on
     * both when {@code n == 0}. The tail of each array is never inspected.
     *
     * <p>Unlike the streaming path this does not touch or depend on the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions (only the first is used)
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the first value and auxiliary, or {@code null} if
     * either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        if (values.size() < n || auxs.size() < n) {
            return null;
        }

        UIEntryCombinerState result = new UIEntryCombinerState();

        if (n > 0) {
            result.setAccum(values.get(0));
            result.setAccumAux(auxs.get(0));
        } else {
            result.setAccum(UI_ENTRY_VALUE_NOT_PRESENT);
            result.setAccumAux(UI_ENTRY_VALUE_NOT_PRESENT);
        }

        return result;
    }

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
            return new FirstCombiner();
        }

        UIEntryCombinerState thisState = state;
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(thisState.getAccum());
        newState.setAccumAux(thisState.getAccumAux());
        newState.setNegAccum(thisState.getNegAccum());
        newState.setNegAccumAux(thisState.getNegAccumAux());

        FirstCombiner result = new FirstCombiner();
        result.state = newState;

        return result;
    }
}
