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
 * The LOGICAL_OR combiner: reduces several contributions to a single boolean -
 * true if any contributor is true. This is the {@code "LOGICAL_OR"} row of the C
 * original's UI-entry combiner table ({@code ui-entry-combiner.c}:
 * {@code logical_combine_init}, {@code logical_or_combine_accum},
 * {@code dummy_combine_finish}, {@code logical_or_vec}). It suits properties that
 * are present-or-absent rather than quantities.
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and the
 * two channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) are OR-ed independently but
 * in lock-step.
 *
 * <p>This is the first combiner whose accumulator is not simply a running total of
 * its inputs: every real contribution is flattened to {@code 0} or {@code 1} on
 * the way in, so the accumulator only ever holds those two values or a sentinel.
 * That flattening is what makes {@code C}'s integer {@code ||} translate to a
 * comparison-plus-ternary here - Java's {@code ||} takes and returns
 * {@code boolean}, whereas C's takes any nonzero {@code int} as true and yields
 * {@code int} 1 or 0.
 *
 * <p>Two {@code int} values are reserved sentinels rather than real values, and are
 * handled specially rather than being flattened:
 * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} (no contribution - skipped) and
 * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} (unknown to the player). The UNKNOWN
 * case matters more here than in the arithmetic combiners: because the sentinel's
 * numeric value is {@link Integer#MAX_VALUE}, letting it reach the flattening step
 * would read as true and claim a property the player has not learned.
 *
 * @author Rowan Crowther
 */
public class LogicalOrCombiner implements Cloneable, Combiner {
    private UIEntryCombinerState state;

    /**
     * Begins a fresh streaming fold, seeding both channels with the first
     * contribution flattened to {@code 0} or {@code 1}. A sentinel seed is stored
     * as-is rather than flattened, so an absent or unknown first contribution stays
     * distinguishable from a false one.
     *
     * <p>The negative-accumulator fields are explicitly zeroed, mirroring the C's
     * {@code st->work = 0} in {@code logical_combine_init}. In the C, {@code work}
     * is a {@code void *} scratch pointer that only {@code RESIST_0} allocates (as
     * a two-{@code int} array of most-negative accumulators, which is what
     * {@code negAccum}/{@code negAccumAux} model here); LOGICAL_OR merely nulls it.
     *
     * @param v the value channel of the first contribution
     * @param a the auxiliary channel of the first contribution
     */
    @Override
    public void init(int v, int a) {
        state = new UIEntryCombinerState();

        state.setNegAccum(0);
        state.setNegAccumAux(0);

        if (v == UI_ENTRY_UNKNOWN_VALUE || v == UI_ENTRY_VALUE_NOT_PRESENT)
            state.setAccum(v);
        else {
            int seed = (v != 0) ? 1 : 0;
            state.setAccum(seed);
        }

        if (a == UI_ENTRY_UNKNOWN_VALUE || a == UI_ENTRY_VALUE_NOT_PRESENT)
            state.setAccumAux(a);
        else {
            int seed = (a != 0) ? 1 : 0;
            state.setAccumAux(seed);
        }
    }

    /**
     * Folds one further contribution into the running state, OR-ing both channels
     * via {@link #logicalOrCombineAccumHelp}. Sentinel inputs are handled by that
     * helper rather than being flattened.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        int newA = logicalOrCombineAccumHelp(v, state.getAccum());
        int newAAux = logicalOrCombineAccumHelp(a, state.getAccumAux());
        state.setAccum(newA);
        state.setAccumAux(newAAux);
    }

    /**
     * Completes the streaming fold. LOGICAL_OR needs no finishing step (the C
     * original uses {@code dummy_combine_finish}), so the accumulated channels are
     * already the answer and nothing is computed here - unlike
     * {@link LogicalOrWithCancelCombiner}, whose accumulator is an intermediate
     * encoding that {@code finish} has to collapse.
     *
     * <p>The result is a fresh {@link UIEntryCombinerState} rather than this
     * combiner's own, so a caller cannot reach through the returned object and
     * disturb a fold that may still be running. Repeated calls are therefore
     * independent snapshots, and writing to one is invisible to both the combiner
     * and any other snapshot.
     *
     * @return a snapshot of the OR-ed value and auxiliary channels
     */
    @Override
    public UIEntryCombinerState finish() {
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setNegAccum(state.getNegAccum());
        newState.setNegAccumAux(state.getNegAccumAux());
        newState.setAccum(state.getAccum());
        newState.setAccumAux(state.getAccumAux());
        return newState;
    }

    /**
     * One-shot array form of the fold, the counterpart to C's
     * {@code logical_or_vec}: OR-s whole arrays of values and auxiliaries in a
     * single call instead of streaming them through {@code init}/{@code accum}.
     *
     * <p>Unlike the other combiners, this does not seed from element 0. Both
     * channels start at {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} and every
     * element - including the first - is folded in through the same helper, which
     * is why the loops run from {@code 0} rather than {@code 1}. That falls out of
     * the helper's own contract: a NOT_PRESENT accumulator is replaced outright by
     * the first real contribution, so an explicit seed would be redundant. It also
     * means an {@code n == 0} run yields NOT_PRESENT on both channels without
     * needing the special case the other rows carry.
     *
     * <p>Note that {@code n}, not the list size, bounds the fold - matching the C's
     * {@code for (i = 0; i < n; ++i)} over a bare {@code const int *} - so any tail
     * beyond {@code n} is ignored rather than OR-ed in. The under-length guard has
     * no counterpart in the C, whose pointers carry no length to check; it is a
     * port-level defence, and returns {@code null} to stay consistent with the rest
     * of the combiner family.
     *
     * <p>Unlike the streaming path this does not touch or depend on the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the OR-ed value and auxiliary channels, or
     * {@code null} if either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        if (n > values.size() || n > auxs.size())
            return null;

        int accumPointer = UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 0; index < n; index++) {
            int result = logicalOrCombineAccumHelp(values.get(index), accumPointer);
            accumPointer = result;
        }

        int accumAuxPointer = UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 0; index < n; index++) {
            int result = logicalOrCombineAccumHelp(auxs.get(index), accumAuxPointer);
            accumAuxPointer = result;
        }

        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(accumPointer);
        newState.setAccumAux(accumAuxPointer);

        return newState;
    }

    /**
     * OR-s a single contribution {@code x} onto a running accumulator, honouring
     * the reserved sentinels; the C original mutates {@code *accum} in place,
     * whereas this returns the new accumulator (Java has no pointer-to-int).
     *
     * <p>The rules, in order: a NOT_PRESENT contribution changes nothing; an
     * UNKNOWN contribution promotes an absent accumulator to UNKNOWN but otherwise
     * leaves it alone, and in either case stops there; a real contribution onto an
     * absent-or-unknown accumulator replaces it with its own truth value; otherwise
     * the two truth values are OR-ed.
     *
     * <p>The early return on UNKNOWN is load-bearing rather than an optimisation.
     * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} is {@link Integer#MAX_VALUE}, so if
     * the sentinel reached the final OR it would test as nonzero and turn a known-
     * false accumulator true - reporting a property the player has never learned.
     *
     * @param x     the incoming contribution (possibly a sentinel)
     * @param accum the current accumulator (possibly a sentinel)
     * @return the updated accumulator: {@code 0}, {@code 1}, or a sentinel
     */
    private int logicalOrCombineAccumHelp(int x, int accum) {
        int output = accum;

        if (x == UI_ENTRY_VALUE_NOT_PRESENT)
            return output;

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (accum == UI_ENTRY_VALUE_NOT_PRESENT) {
                output = UI_ENTRY_UNKNOWN_VALUE;
            }
            return output;
        }

        if (accum == UI_ENTRY_UNKNOWN_VALUE || accum == UI_ENTRY_VALUE_NOT_PRESENT)
            output = (x != 0) ? 1 : 0;
        else
            output = (output != 0 || x != 0) ? 1 : 0;

        return output;
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
        if (state == null)
            return new LogicalOrCombiner();

        UIEntryCombinerState thisState = state;
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(thisState.getAccum());
        newState.setAccumAux(thisState.getAccumAux());
        newState.setNegAccum(thisState.getNegAccum());
        newState.setNegAccumAux(thisState.getNegAccumAux());

        LogicalOrCombiner result = new LogicalOrCombiner();
        result.state = newState;

        return result;
    }
}
