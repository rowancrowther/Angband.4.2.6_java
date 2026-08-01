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
 * The ADD combiner: folds several contributing values into their running sum.
 * This is the {@code "ADD"} row of the C original's UI-entry combiner table
 * ({@code ui-entry-combiner.c}: {@code simple_combine_init},
 * {@code add_combine_accum}, {@code dummy_combine_finish}, {@code add_vec}).
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and the
 * two channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) are summed independently
 * but in lock-step. What an auxiliary carries depends on the property being
 * displayed (for example a temporary effect alongside a permanent one, or a
 * sustain flag alongside a stat modifier); ADD itself treats both channels the
 * same way.
 *
 * <p>Two {@code int} values are reserved sentinels rather than real numbers, and
 * both are handled specially rather than being added arithmetically:
 * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} (no contribution - skipped) and
 * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} (unknown to the player - which infects
 * an otherwise-absent accumulator). The {@code INT_MAX - 2} clamp in
 * {@link #addCombineAccumHelp} exists so a genuine sum can never grow up into
 * that sentinel band.
 *
 * @author Rowan Crowther
 */
public class AddCombiner implements Combiner, Cloneable {
    private UIEntryCombinerState state;

    /**
     * Begins a fresh streaming fold, seeding both accumulator channels with the
     * first contribution. The negative-accumulator fields are left at their
     * default zero; ADD never reads them (they belong to {@code RESIST_0}, which
     * uses them for the {@code work} array the C original allocates alongside the
     * state).
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
     * Folds one further contribution into the running state, updating both
     * channels via {@link #addCombineAccumHelp}. Sentinel inputs are handled by
     * that helper rather than being summed.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        int accum = state.getAccum();
        int accumAux = state.getAccumAux();
        int newAccum = addCombineAccumHelp(v, accum);
        int newAccumAux = addCombineAccumHelp(a, accumAux);
        state.setAccum(newAccum);
        state.setAccumAux(newAccumAux);
    }

    /**
     * Completes the streaming fold. ADD needs no finishing step (the C original
     * uses {@code dummy_combine_finish}), so this simply hands back the state
     * holding the summed value and auxiliary channels.
     *
     * @return the combined state after all contributions
     */
    @Override
    public UIEntryCombinerState finish() {
        return state;
    }

    /**
     * One-shot array form of the fold, the counterpart to C's {@code add_vec}:
     * combines whole arrays of values and auxiliaries in a single call instead
     * of streaming them through {@code init}/{@code accum}. The first element of
     * each array seeds its channel (or {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT}
     * when {@code n == 0}), and the remainder are folded in with the same
     * sentinel-aware helper as {@link #accum}.
     *
     * <p>Note that {@code n}, not the list size, bounds the fold - matching the C's
     * {@code for (i = 1; i < n; ++i)} over a bare {@code const int *} - so any tail
     * beyond {@code n} is ignored rather than summed. The under-length guard has no
     * counterpart in the C, whose pointers carry no length to check; it is a
     * port-level defence, and returns {@code null} to stay consistent with the rest
     * of the combiner family.
     *
     * <p>Unlike the streaming path this does not touch or depend on the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the combined value and auxiliary channels, or
     * {@code null} if either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        int index;
        int accumPointer;
        int auxPointer;
        int result;
        UIEntryCombinerState returnValue = new UIEntryCombinerState();

        if (values.size() < n || auxs.size() < n) {
            return null;
        }

        if (n > 0)
            accumPointer = values.get(0);
        else
            accumPointer = UI_ENTRY_VALUE_NOT_PRESENT;

        for (index = 1; index < n; index++) {
            result = addCombineAccumHelp(values.get(index), accumPointer);
            accumPointer = result;

        }

        if (n > 0) auxPointer = auxs.get(0);
        else auxPointer = UI_ENTRY_VALUE_NOT_PRESENT;

        for (index = 1; index < n; index++) {
            result = addCombineAccumHelp(auxs.get(index), auxPointer);
            auxPointer = result;
        }

        returnValue.setAccum(accumPointer);
        returnValue.setAccumAux(auxPointer);

        return returnValue;
    }

    /**
     * Adds a single contribution {@code x} onto a running accumulator, honouring
     * the reserved sentinels; the C original mutates {@code *accum} in place,
     * whereas this returns the new accumulator (Java has no pointer-to-int).
     *
     * <p>The rules, in order: a NOT_PRESENT contribution changes nothing; an
     * UNKNOWN contribution turns an absent accumulator UNKNOWN but otherwise
     * leaves it alone; a real contribution onto an absent-or-unknown accumulator
     * simply becomes the accumulator; otherwise the two are summed, clamped away
     * from the sentinel band ({@code INT_MAX - 2}) at the top and {@code INT_MIN}
     * at the bottom so a genuine total can never masquerade as a sentinel.
     *
     * @param x          the incoming contribution (possibly a sentinel)
     * @param accumValue the current accumulator (possibly a sentinel)
     * @return the updated accumulator
     */
    private int addCombineAccumHelp(int x, int accumValue) {
        int result = accumValue;

        if (x == UI_ENTRY_VALUE_NOT_PRESENT) {
            return accumValue;
        }

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (result == UI_ENTRY_VALUE_NOT_PRESENT) {
                result = UI_ENTRY_UNKNOWN_VALUE;
            }
            return result;
        }

        if (accumValue == UI_ENTRY_UNKNOWN_VALUE || accumValue == UI_ENTRY_VALUE_NOT_PRESENT) {
            return x;
        }

        /*
         * Just in case, guard against overflow or underflow.  Also guard
         * adding up to the special values which are equal to INT_MAX and
         * INT_MAX - 1.
         */
        if (x > 0) {
            if (result <= Integer.MAX_VALUE - 2 - x) {
                result += x;
            } else {
                result = Integer.MAX_VALUE - 2;
            }
        } else if (x < 0) {
            if (result >= Integer.MIN_VALUE - x) {
                result += x;
            } else {
                result = Integer.MIN_VALUE;
            }
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
            return new AddCombiner();
        }

        UIEntryCombinerState thisState = state;
        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(thisState.getAccum());
        newState.setAccumAux(thisState.getAccumAux());
        newState.setNegAccum(thisState.getNegAccum());
        newState.setNegAccumAux(thisState.getNegAccumAux());

        AddCombiner result = new AddCombiner();
        result.state = newState;

        return result;
    }
}