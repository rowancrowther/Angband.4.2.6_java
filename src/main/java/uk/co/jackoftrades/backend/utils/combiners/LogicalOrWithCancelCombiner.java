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
 * The LOGICAL_OR_WITH_CANCEL combiner: a logical OR in which any negative
 * contribution overrides every positive one. This is the
 * {@code "LOGICAL_OR_WITH_CANCEL"} row of the C original's UI-entry combiner
 * table ({@code ui-entry-combiner.c}:
 * {@code logical_or_with_cancel_combine_init},
 * {@code logical_or_with_cancel_combine_accum},
 * {@code logical_or_with_cancel_combine_finish},
 * {@code logical_or_with_cancel_vec}). It suits properties where one source can
 * negate another - a vulnerability cancelling a resist, say - rather than the two
 * simply adding up.
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and the
 * two channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) are combined independently
 * but in lock-step.
 *
 * <h2>The accumulator is a bit-set, not a number</h2>
 *
 * <p>This is the first combiner whose accumulator is not the value it will
 * eventually report. During the fold each channel holds a two-bit set: bit 0
 * ({@code 1}) records "at least one positive contribution was seen", bit 1
 * ({@code 2}) records "at least one negative contribution was seen". A zero
 * contribution sets neither and so changes nothing. The accumulator is therefore
 * one of {@code 0}, {@code 1}, {@code 2} or {@code 3} - or a reserved sentinel -
 * right up until {@link #finish()}.
 *
 * <p>Keeping the two sightings separate is what makes cancellation expressible:
 * the combiner cannot know whether a positive is going to be cancelled until every
 * contribution has been seen, so it records both facts and decides at the end.
 * {@link #finish()} collapses the bit-set to the reported value - {@code -1} if any
 * negative was seen, otherwise the {@code 0} or {@code 1} already held.
 *
 * <p>Two {@code int} values are reserved sentinels rather than real values, and are
 * handled specially rather than being encoded:
 * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} (no contribution - skipped) and
 * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} (unknown to the player). Both pass
 * through the collapse untouched, which matters because
 * {@code UI_ENTRY_UNKNOWN_VALUE} is {@link Integer#MAX_VALUE} - it has bit 1 set,
 * so bit-testing it would silently report an unlearned property as cancelled.
 *
 * @author Rowan Crowther
 */
public class LogicalOrWithCancelCombiner implements Combiner, Cloneable {
    private UIEntryCombinerState state;

    /**
     * Begins a fresh streaming fold, seeding both channels with the first
     * contribution encoded as a bit-set: {@code 1} for a positive value, {@code 2}
     * for a negative one, {@code 0} for zero. A sentinel seed is stored as-is
     * rather than encoded, so an absent or unknown first contribution stays
     * distinguishable from a genuine zero.
     *
     * <p>The negative-accumulator fields are explicitly zeroed, mirroring the C's
     * {@code st->work = 0}. In the C, {@code work} is a {@code void *} scratch
     * pointer that only {@code RESIST_0} allocates (as a two-{@code int} array of
     * most-negative accumulators, which is what {@code negAccum}/{@code negAccumAux}
     * model here); this row merely nulls it. Note that despite the name, this
     * combiner's own "negative" bookkeeping lives in bit 1 of {@code accum}, not in
     * those fields.
     *
     * @param v the value channel of the first contribution
     * @param a the auxiliary channel of the first contribution
     */
    @Override
    public void init(int v, int a) {
        state = new UIEntryCombinerState();

        state.setNegAccum(0);
        state.setNegAccumAux(0);

        if (v == UI_ENTRY_UNKNOWN_VALUE || v == UI_ENTRY_VALUE_NOT_PRESENT) {
            state.setAccum(v);
        } else {
            int seed = (v > 0) ? 1 : ((v < 0) ? 2 : 0);
            state.setAccum(seed);
        }

        if (a == UI_ENTRY_UNKNOWN_VALUE || a == UI_ENTRY_VALUE_NOT_PRESENT) {
            state.setAccumAux(a);
        } else {
            int seed = (a > 0) ? 1 : ((a < 0) ? 2 : 0);
            state.setAccumAux(seed);
        }
    }

    /**
     * Folds a single contribution {@code x} into a running bit-set accumulator,
     * honouring the reserved sentinels; the C original mutates {@code *accum} in
     * place, whereas this returns the new accumulator (Java has no
     * pointer-to-int).
     *
     * <p>The rules, in order: a NOT_PRESENT contribution changes nothing; an
     * UNKNOWN contribution promotes an absent accumulator to UNKNOWN but otherwise
     * leaves it alone, and in either case stops there; a real contribution onto an
     * absent-or-unknown accumulator replaces it with its own encoding; otherwise
     * the corresponding bit is OR-ed in.
     *
     * <p>A zero contribution deliberately falls off the end of that chain and
     * changes nothing - it is neither a positive nor a negative sighting. The C
     * expresses this as an {@code if}/{@code else if} with no trailing
     * {@code else}, and the distinction matters: treating zero as negative would
     * set bit 1 and cancel the whole entry to {@code -1} at
     * {@link #finish()}.
     *
     * @param x     the incoming contribution (possibly a sentinel)
     * @param accum the current bit-set accumulator (possibly a sentinel)
     * @return the updated accumulator: {@code 0}, {@code 1}, {@code 2}, {@code 3},
     * or a sentinel
     */
    private int logicalOrWithCancelCombineAccumHelp(int x, int accum) {
        int result = accum;

        if (x == UI_ENTRY_VALUE_NOT_PRESENT)
            return result;

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (accum == UI_ENTRY_VALUE_NOT_PRESENT) {
                result = UI_ENTRY_UNKNOWN_VALUE;
            }
            return result;
        }

        if (accum == UI_ENTRY_UNKNOWN_VALUE || accum == UI_ENTRY_VALUE_NOT_PRESENT) {
            result = (x > 0) ? 1 : ((x < 0) ? 2 : 0);
        } else if (x > 0)
            result = accum | 1;
        else if (x < 0)
            result = accum | 2;

        return result;
    }

    /**
     * Folds one further contribution into the running state, updating the bit-set
     * on both channels via {@link #logicalOrWithCancelCombineAccumHelp}. Sentinel
     * inputs are handled by that helper rather than being encoded.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        int newAccum = logicalOrWithCancelCombineAccumHelp(v, state.getAccum());
        int newAccumAux = logicalOrWithCancelCombineAccumHelp(a, state.getAccumAux());
        state.setAccum(newAccum);
        state.setAccumAux(newAccumAux);
    }

    /**
     * Completes the streaming fold by collapsing each channel's bit-set into the
     * value actually reported. Unlike every combiner ported so far, this is real
     * work rather than a formality - the C original gives this row a genuine
     * {@code logical_or_with_cancel_combine_finish} where the others use
     * {@code dummy_combine_finish}.
     *
     * <p>The rule is that any negative cancels everything: if bit 1 is set the
     * channel becomes {@code -1}, whatever else was seen. Otherwise the
     * accumulator is already {@code 0} or {@code 1} and is reported unchanged. The
     * C asserts that invariant in the {@code else} branch; the assertion compiles
     * away under {@code NDEBUG} and carries no behaviour, so it has no counterpart
     * here.
     *
     * <p>Sentinel channels skip the collapse entirely. That guard is essential
     * rather than tidy: {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} is
     * {@link Integer#MAX_VALUE}, whose bit 1 is set, so bit-testing it would turn
     * every unknown property into a cancelled one.
     *
     * <p>The result is a fresh {@link UIEntryCombinerState} and the accumulator is
     * left in its un-collapsed bit-set form, so calling this twice yields the same
     * answer both times. Collapsing in place - as the C does, since it owns the
     * caller's struct - would make a second call re-test bit 1 of a value that is
     * now {@code -1}.
     *
     * @return a snapshot of the collapsed value and auxiliary channels, each
     * {@code -1}, {@code 0}, {@code 1}, or a sentinel
     */
    @Override
    public UIEntryCombinerState finish() {
        UIEntryCombinerState result = new UIEntryCombinerState();

        int newAccum = state.getAccum();
        int newAccumAux = state.getAccumAux();

        if (state.getAccum() != UI_ENTRY_UNKNOWN_VALUE && state.getAccum() != UI_ENTRY_VALUE_NOT_PRESENT) {
            if ((state.getAccum() & 2) != 0)
                newAccum = -1;
        }

        if (state.getAccumAux() != UI_ENTRY_UNKNOWN_VALUE && state.getAccumAux() != UI_ENTRY_VALUE_NOT_PRESENT) {
            if ((state.getAccumAux() & 2) != 0)
                newAccumAux = -1;
        }

        result.setAccum(newAccum);
        result.setAccumAux(newAccumAux);

        return result;
    }

    /**
     * One-shot array form of the fold, the counterpart to C's
     * {@code logical_or_with_cancel_vec}: folds whole arrays of values and
     * auxiliaries in a single call instead of streaming them through
     * {@code init}/{@code accum}.
     *
     * <p>Because this row has a real finishing step, {@code vec} has to do both
     * halves of the job - fold every element into the bit-set, then collapse it -
     * where the earlier combiners' one-shot form only ever folds. The C inlines
     * that collapse rather than calling {@code finish}, which is why the same
     * bit-test appears four times over in the original.
     *
     * <p>Like {@link LogicalOrCombiner} and unlike the arithmetic rows, this does
     * not seed from element 0: both channels start at
     * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} and every element - including the
     * first - is folded in through the same helper, which is why the loops run from
     * {@code 0} rather than {@code 1}. An {@code n == 0} run therefore yields
     * NOT_PRESENT on both channels without needing a special case.
     *
     * <p>Note that {@code n}, not the list size, bounds the fold - matching the C's
     * {@code for (i = 0; i < n; ++i)} over a bare {@code const int *} - so any tail
     * beyond {@code n} is ignored. The under-length guard has no counterpart in the
     * C, whose pointers carry no length to check; it is a port-level defence, and
     * returns {@code null} to stay consistent with the rest of the combiner family.
     *
     * <p>Unlike the streaming path this does not touch or depend on the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the collapsed value and auxiliary channels, or
     * {@code null} if either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        if (n > values.size() || n > auxs.size())
            return null;

        int accumPointer = UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 0; index < n; index++) {
            int output = logicalOrWithCancelCombineAccumHelp(values.get(index), accumPointer);
            accumPointer = output;
        }
        if (accumPointer != UI_ENTRY_UNKNOWN_VALUE && accumPointer != UI_ENTRY_VALUE_NOT_PRESENT) {
            if ((accumPointer & 2) != 0)
                accumPointer = -1;
        }

        int accumAuxPointer = UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 0; index < n; index++) {
            int output = logicalOrWithCancelCombineAccumHelp(auxs.get(index), accumAuxPointer);
            accumAuxPointer = output;
        }
        if (accumAuxPointer != UI_ENTRY_UNKNOWN_VALUE && accumAuxPointer != UI_ENTRY_VALUE_NOT_PRESENT) {
            if ((accumAuxPointer & 2) != 0)
                accumAuxPointer = -1;
        }

        UIEntryCombinerState result = new UIEntryCombinerState();
        result.setAccum(accumPointer);
        result.setAccumAux(accumAuxPointer);

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
        if (state == null)
            return new LogicalOrWithCancelCombiner();

        UIEntryCombinerState thisState = state;
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(thisState.getAccum());
        newState.setAccumAux(thisState.getAccumAux());
        newState.setNegAccum(thisState.getNegAccum());
        newState.setNegAccumAux(thisState.getNegAccumAux());

        LogicalOrWithCancelCombiner result = new LogicalOrWithCancelCombiner();
        result.state = newState;

        return result;
    }
}
