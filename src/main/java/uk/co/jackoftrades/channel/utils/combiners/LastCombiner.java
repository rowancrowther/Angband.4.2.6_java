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

package uk.co.jackoftrades.channel.utils.combiners;

import uk.co.jackoftrades.channel.utils.Combiner;
import uk.co.jackoftrades.channel.utils.UIEntryCombinerState;

import java.util.List;

/**
 * The LAST combiner: keeps the most recent contribution and discards every
 * earlier one. This is the {@code "LAST"} row of the C original's UI-entry
 * combiner table ({@code ui-entry-combiner.c}: {@code simple_combine_init},
 * {@code last_combine_accum}, {@code dummy_combine_finish}, {@code last_vec}).
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and both
 * channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) are simply overwritten by
 * each new contributor. This is the mirror image of {@link FirstCombiner}, and
 * like it needs no sentinel-handling helper: nothing is ever folded, so whatever
 * arrives last - real value or reserved sentinel - is passed through verbatim.
 *
 * @author Rowan Crowther
 */
public class LastCombiner implements Combiner, Cloneable {
    private UIEntryCombinerState state;

    /**
     * Seeds both channels with the first contribution. If no further contribution
     * follows, this seed is also the final result of the streaming fold.
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
     * Overwrites both channels with this contribution, discarding whatever was
     * held before. This mirrors the C original's {@code last_combine_accum}.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        state.setAccum(v);
        state.setAccumAux(a);
    }

    /**
     * Completes the streaming fold. LAST needs no finishing step (the C original
     * uses {@code dummy_combine_finish}), so the accumulated channels are already
     * the answer and nothing is computed here.
     *
     * <p>The result is a fresh {@link UIEntryCombinerState} rather than this
     * combiner's own, so a caller cannot reach through the returned object and
     * disturb a fold that may still be running. Repeated calls are therefore
     * independent snapshots, and writing to one is invisible to both the combiner
     * and any other snapshot.
     *
     * @return a snapshot of the last contribution
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
     * One-shot array form, the counterpart to C's {@code last_vec}: returns
     * element {@code n - 1} of each channel, or
     * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} on both when {@code n == 0}.
     * Everything before the last element is never inspected.
     *
     * <p>Note that {@code n}, not the list size, chooses the element - matching C's
     * {@code vals[n - 1]} - so any tail beyond {@code n} is ignored rather than
     * read. The under-length guard has no counterpart in the C, whose bare
     * {@code const int *} carries no length to check; it is a port-level defence,
     * and returns {@code null} to stay consistent with the rest of the combiner
     * family rather than because the C says anything on the matter.
     *
     * <p>Unlike the streaming path this does not touch or depend on the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions (only the last is used)
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the last value and auxiliary, or {@code null} if
     * either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        if (n > values.size() || n > auxs.size())
            return null;

        UIEntryCombinerState result = new UIEntryCombinerState();

        if (n > 0) {
            result.setAccum(values.get(n - 1));
            result.setAccumAux(auxs.get(n - 1));
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
            return new LastCombiner();
        }

        UIEntryCombinerState thisState = state;
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(thisState.getAccum());
        newState.setAccumAux(thisState.getAccumAux());
        newState.setNegAccum(thisState.getNegAccum());
        newState.setNegAccumAux(thisState.getNegAccumAux());

        LastCombiner result = new LastCombiner();
        result.state = newState;

        return result;
    }
}
