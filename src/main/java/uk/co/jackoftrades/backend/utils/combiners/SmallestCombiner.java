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
 * The SMALLEST combiner: reports the least of the values contributed, so that a
 * property held at several strengths shows the weakest. This is the
 * {@code "SMALLEST"} row of the C original's UI-entry combiner table
 * ({@code ui-entry-combiner.c}: {@code simple_combine_init},
 * {@code smallest_combine_accum}, {@code dummy_combine_finish},
 * {@code smallest_vec}), and the last of the nine rows to be ported.
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and the
 * two channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) are combined independently
 * but in lock-step. Unlike {@link Resist0Combiner} and
 * {@link LogicalOrWithCancelCombiner}, the accumulator is the reported value all
 * the way through: there is no encoding to undo, which is why C gives this row
 * {@code dummy_combine_finish} and {@link #finish()} is a snapshot rather than a
 * resolution.
 *
 * <h2>Why this is not {@link Math#min}</h2>
 *
 * <p>Two {@code int} values are reserved sentinels rather than magnitudes:
 * {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} (no contribution) and
 * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} (not yet learned by the player). Both
 * sit at the top of the {@code int} range - {@link Integer#MAX_VALUE} and the one
 * below it - which is convenient for a minimum, since any real value beats them
 * numerically and so displaces them without needing to be asked.
 *
 * <p>Where a plain comparison breaks down is the two sentinels against
 * <em>each other</em>. Numerically NOT_PRESENT is the smaller, but the rule this
 * row wants is the reverse: an absent accumulator meeting an unknown contribution
 * becomes <em>unknown</em>, because the property has now been shown to be present
 * and merely unlearned. {@link Math#min} would report it as still absent, and
 * would equally let a NOT_PRESENT contribution overwrite an accumulator that had
 * already learned something. Those two cases are why
 * {@link #smallestCombineAccumHelp(int, int)} tests the sentinels explicitly
 * before it compares anything, and they are the whole reason this row is a
 * function rather than a one-line fold.
 *
 * @author Rowan Crowther
 */
public class SmallestCombiner implements Combiner, Cloneable {

    /**
     * The running fold state; {@code null} until {@link #init(int, int)} seeds it.
     */
    private UIEntryCombinerState state;

    /**
     * Begins a fresh streaming fold, seeding both channels with the first
     * contribution exactly as given.
     *
     * <p>This row shares C's {@code simple_combine_init} with the arithmetic rows:
     * there is no encoding step, so a seed - sentinel or not - is simply stored,
     * and becomes the value every later contribution is compared against. Note
     * that the seed bypasses {@link #smallestCombineAccumHelp(int, int)}
     * altogether, so a NOT_PRESENT seed genuinely starts the fold absent rather
     * than being skipped.
     *
     * <p>The two negative accumulators are zeroed to mirror the C's
     * {@code st->work = 0}. That pointer is RESIST_0's scratch space and this row
     * never reads it; the port models it as two plain fields, so there is nothing
     * to allocate here and nothing to free later.
     *
     * @param v the value channel of the first contribution
     * @param a the auxiliary channel of the first contribution
     */
    @Override
    public void init(int v, int a) {
        state = new UIEntryCombinerState();
        state.setAccum(v);
        state.setAccumAux(a);
        state.setNegAccum(0);
        state.setNegAccumAux(0);
    }

    /**
     * Folds one further contribution into the running state, taking each channel
     * down to the smaller of what it held and what arrived.
     *
     * <p>The two channels are wholly independent - the C makes this vivid by
     * passing {@code &st->accum} and {@code &st->accum_aux} into the same helper -
     * so the value channel can fall while the auxiliary one holds.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        int smallestAccum = smallestCombineAccumHelp(v, state.getAccum());
        int smallestAccumAux = smallestCombineAccumHelp(a, state.getAccumAux());
        state.setAccum(smallestAccum);
        state.setAccumAux(smallestAccumAux);
    }

    /**
     * Folds a single contribution {@code x} into a running minimum, honouring the
     * reserved sentinels. The C original mutates {@code *accum} in place; this
     * returns the new accumulator instead, since Java has no pointer-to-int.
     *
     * <p>The rules, in the order the C tests them:
     *
     * <ul>
     *   <li>a NOT_PRESENT contribution changes nothing - a source that has no
     *       opinion must not drag the minimum anywhere;
     *   <li>an UNKNOWN contribution promotes an <em>absent</em> accumulator to
     *       UNKNOWN and otherwise leaves it alone, so one unlearned source among
     *       several known ones does not obscure what the player has already worked
     *       out;
     *   <li>otherwise the smaller wins, with a sentinel accumulator always losing
     *       to a real value.
     * </ul>
     *
     * <p>Only the first two rules are doing work that a numeric comparison could
     * not. In the third, the two sentinel tests are inert: both constants sit at
     * the top of the {@code int} range, so {@code accum > x} has already decided
     * the question for any real {@code x} - and {@code x} is known to be real by
     * then, the branches above having returned. They are kept because that holds
     * only by virtue of where the constants happen to be defined, and because it
     * preserves the line-by-line correspondence with the C.
     *
     * @param x     the incoming contribution (possibly a sentinel)
     * @param accum the current running minimum (possibly a sentinel)
     * @return the updated minimum
     */
    private int smallestCombineAccumHelp(int x, int accum) {
        int newAccum = accum;

        if (x == UI_ENTRY_VALUE_NOT_PRESENT)
            return newAccum;

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (accum == UI_ENTRY_VALUE_NOT_PRESENT)
                newAccum = UI_ENTRY_UNKNOWN_VALUE;
            return newAccum;
        }

        if (accum == UI_ENTRY_UNKNOWN_VALUE ||
                accum == UI_ENTRY_VALUE_NOT_PRESENT ||
                accum > x)
            newAccum = x;

        return newAccum;
    }

    /**
     * Completes the streaming fold. There is nothing to resolve - the accumulator
     * has been the reported value the whole way - so this is a formality, matching
     * the {@code dummy_combine_finish} the C hands this row.
     *
     * <p>It is not quite a no-op in the port, though: it returns a fresh
     * {@link UIEntryCombinerState} rather than the live one, so a caller holding
     * the result cannot write back into the combiner and a second call is
     * unaffected by what happened to the first. The C needs no such care, since it
     * hands results back through the caller's own struct.
     *
     * <p>The negative accumulators are reported as zero rather than copied. This
     * row never touches them - they are RESIST_0's scratch - and reporting a
     * constant keeps the whole family's snapshots uniform.
     *
     * @return a snapshot of the value and auxiliary channels
     */
    @Override
    public UIEntryCombinerState finish() {
        UIEntryCombinerState finalState = new UIEntryCombinerState();

        finalState.setAccum(state.getAccum());
        finalState.setAccumAux(state.getAccumAux());
        finalState.setNegAccum(0);
        finalState.setNegAccumAux(0);

        return finalState;
    }

    /**
     * One-shot array form of the fold, the counterpart to C's
     * {@code smallest_vec}: folds whole lists of values and auxiliaries in a
     * single call instead of streaming them through {@code init}/{@code accum}.
     *
     * <p>Because this row's {@code finish} is a formality, {@code vec} only folds
     * - there is no inlined resolution of the kind {@link Resist0Combiner#vec} and
     * {@link LogicalOrWithCancelCombiner#vec} have to carry.
     *
     * <p>Each channel is seeded from element {@code 0} <em>verbatim</em> and the
     * loops run from {@code 1}, exactly as {@code init} seeds the streaming path
     * and {@code accum} continues it. That is the arithmetic rows' shape, not the
     * logical ones', which start from NOT_PRESENT and fold every element. The
     * consequence is that the two paths agree element for element, and that an
     * {@code n == 0} run needs the explicit NOT_PRESENT fallback in the seeding
     * expression rather than getting it for free.
     *
     * <p>The C signature is
     * {@code smallest_vec(int n, const int *vals, const int *auxs, int *accum, int *accum_aux)},
     * but only the first two pointers are arrays; {@code accum} and
     * {@code accum_aux} are single out-parameters and become the returned
     * {@link UIEntryCombinerState} here rather than parameters.
     *
     * <p>Note that {@code n}, not the list size, bounds the fold - matching the
     * C's {@code for (i = 1; i < n; ++i)} over a bare {@code const int *} - so any
     * tail beyond {@code n} is ignored. The under-length guard has no counterpart
     * in the C, whose pointers carry no length to check; it is a port-level
     * defence, and returns {@code null} to stay consistent with the rest of the
     * combiner family.
     *
     * <p>Unlike the streaming path this neither reads nor disturbs the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the smallest value and auxiliary contributions, or
     * {@code null} if either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        int accum;
        int accumAux;

        if (n > values.size() || n > auxs.size())
            return null;

        accum = (n > 0) ? values.get(0) : UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 1; index < n; index++) {
            int temp = smallestCombineAccumHelp(values.get(index), accum);
            accum = temp;
        }

        accumAux = (n > 0) ? auxs.get(0) : UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 1; index < n; index++) {
            int temp = smallestCombineAccumHelp(auxs.get(index), accumAux);
            accumAux = temp;
        }

        UIEntryCombinerState finalState = new UIEntryCombinerState();
        finalState.setAccum(accum);
        finalState.setAccumAux(accumAux);
        finalState.setNegAccum(0);
        finalState.setNegAccumAux(0);

        return finalState;
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
            return new SmallestCombiner();

        SmallestCombiner smallestCombiner = new SmallestCombiner();

        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setAccum(state.getAccum());
        newState.setAccumAux(state.getAccumAux());
        newState.setNegAccum(state.getNegAccum());
        newState.setNegAccumAux(state.getNegAccumAux());

        smallestCombiner.state = newState;

        return smallestCombiner;
    }
}
