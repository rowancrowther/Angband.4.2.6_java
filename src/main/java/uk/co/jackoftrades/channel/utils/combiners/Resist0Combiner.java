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
 * The RESIST_0 combiner: the rule Angband uses to fold several elemental
 * resistances into the single figure shown on the character sheet. This is the
 * {@code "RESIST_0"} row of the C original's UI-entry combiner table
 * ({@code ui-entry-combiner.c}: {@code resist_0_combine_init},
 * {@code resist_0_combine_accum}, {@code resist_0_combine_finish},
 * {@code resist_0_vec}).
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value - and the
 * two channels ({@link UIEntryCombinerState#getAccum() accum} and
 * {@link UIEntryCombinerState#getAccumAux() accumAux}) are combined independently
 * but in lock-step.
 *
 * <h2>What the numbers mean</h2>
 *
 * <p>A contribution is a signed resistance level, decoded by the renderer's
 * {@code convert_vanilla_res_level}: {@code 0} is no resistance, {@code 1} or
 * {@code 2} is resistance, {@code 3} or above is immunity, and anything
 * {@code -1} or below is vulnerability. Three {@code int} values are reserved
 * sentinels rather than levels - {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} (no
 * contribution), {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} (not yet learned by the
 * player), and {@link Combiner#UI_ENTRY_RESIST0_RES_VUL} (resistance and
 * vulnerability together, with no immunity). All three live at the top of the
 * {@code int} range, at {@link Integer#MAX_VALUE} and the two below it.
 *
 * <h2>Two accumulators, not one</h2>
 *
 * <p>This row cannot fold into a single running figure, because the outcome
 * depends on the most positive and the most negative contribution at once and
 * neither can be discarded while the other is still arriving. So the fold keeps
 * both: {@code accum} tracks the strongest resistance seen and
 * {@link UIEntryCombinerState#getNegAccum() negAccum} the deepest vulnerability,
 * with {@code accumAux}/{@code negAccumAux} doing the same for the auxiliary
 * channel. In the C these negative accumulators are not struct fields at all but
 * a two-{@code int} array hung off the state's {@code work} scratch pointer,
 * {@code mem_alloc}-ed by {@code init} and freed by {@code finish}; RESIST_0 is
 * the only row that uses it. The port gives them named fields instead, so there
 * is nothing to allocate or free.
 *
 * <p>Because the answer is not known until every contribution has been seen,
 * {@link #finish()} does real work here rather than being the formality it is for
 * the arithmetic rows. It applies the rule the C states in one line: <em>a
 * vulnerability cancels a resist but not an immunity</em>. So a vulnerability
 * alongside a bare resistance collapses to {@code RES_VUL}, alongside nothing at
 * all to {@code -1}, and alongside an immunity ({@code 3} or more) to nothing -
 * the immunity stands. That threshold is why the accumulator has to keep the
 * resistance's magnitude through the fold instead of reducing it to a flag.
 *
 * @author Rowan Crowther
 */
public class Resist0Combiner implements Combiner, Cloneable {

    /**
     * A most-positive/most-negative accumulator pair, moved through the fold as
     * one unit.
     *
     * <p>It exists because C's helper takes two out-pointers,
     * {@code resist_0_combine_accum_help(int x, int *pos, int *neg)}, and a Java
     * method has only one return value. Bundling them is the same move
     * {@link UIEntryCombinerState} makes at the interface boundary, one level
     * down and kept private: this pair never leaves the class.
     *
     * @param pos the strongest resistance seen so far, or a sentinel
     * @param neg the deepest vulnerability seen so far, or a sentinel
     */
    private record TwoValues(int pos, int neg) {
    }

    /**
     * The running fold state; {@code null} until {@link #init(int, int)} seeds it.
     */
    private UIEntryCombinerState state;

    /**
     * Begins a fresh streaming fold, splitting each seed across the two
     * accumulators of its channel: a positive level seeds the positive
     * accumulator and leaves the negative one at zero, a negative level does the
     * reverse, and {@code RES_VUL} - which already means "both" - seeds
     * {@code 1} and {@code -1} together.
     *
     * <p>A sentinel seed is stored verbatim in <em>both</em> accumulators rather
     * than split, which is what keeps an absent or unlearned contribution
     * distinguishable from a genuine zero for the rest of the fold. Note that
     * zero itself falls into the final branch and seeds {@code accum} to
     * {@code 0} and {@code negAccum} to {@code 0} - no resistance and no
     * vulnerability, which is exactly right.
     *
     * <p>The two explicit zeroings at the top mirror the C's {@code mem_alloc} of
     * the {@code work} array: they establish the negative accumulators before the
     * branches below overwrite them. Every branch assigns both fields, so they
     * are belt-and-braces rather than load-bearing.
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
            state.setNegAccum(v);
        } else if (v == UI_ENTRY_RESIST0_RES_VUL) {
            state.setAccum(1);
            state.setNegAccum(-1);
        } else if (v > 0) {
            state.setAccum(v);
            state.setNegAccum(0);
        } else {
            state.setAccum(0);
            state.setNegAccum(v);
        }

        if (a == UI_ENTRY_UNKNOWN_VALUE || a == UI_ENTRY_VALUE_NOT_PRESENT) {
            state.setAccumAux(a);
            state.setNegAccumAux(a);
        } else if (a == UI_ENTRY_RESIST0_RES_VUL) {
            state.setAccumAux(1);
            state.setNegAccumAux(-1);
        } else if (a > 0) {
            state.setAccumAux(a);
            state.setNegAccumAux(0);
        } else {
            state.setAccumAux(0);
            state.setNegAccumAux(a);
        }
    }

    /**
     * Folds one further contribution into the running state, running each
     * channel's accumulator pair through {@link #resist0CombineAccumHelp}.
     *
     * <p>The two channels are wholly independent - the C makes this vivid by
     * passing {@code work} and {@code work + 1} into the same helper - so a
     * vulnerability on the value channel can never cancel a resistance on the
     * auxiliary one.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     */
    @Override
    public void accum(int v, int a) {
        TwoValues accumVals = new TwoValues(state.getAccum(), state.getNegAccum());
        TwoValues accum = resist0CombineAccumHelp(v, accumVals);
        state.setAccum(accum.pos());
        state.setNegAccum(accum.neg());

        TwoValues auxVals = new TwoValues(state.getAccumAux(), state.getNegAccumAux());
        TwoValues accumAux = resist0CombineAccumHelp(a, auxVals);
        state.setAccumAux(accumAux.pos());
        state.setNegAccumAux(accumAux.neg());
    }

    /**
     * Folds a single contribution {@code x} into one channel's accumulator pair,
     * honouring the reserved sentinels. The C original mutates {@code *pos} and
     * {@code *neg} in place; this returns the updated pair instead, since Java
     * has no pointer-to-int.
     *
     * <p>The rules, in the order the C tests them:
     *
     * <ul>
     *   <li>a NOT_PRESENT contribution changes nothing;
     *   <li>an UNKNOWN contribution promotes an <em>absent</em> pair to UNKNOWN
     *       and otherwise leaves it alone - so one unlearned source among several
     *       known ones does not obscure what the player has already learned;
     *   <li>a RES_VUL contribution asserts both halves at once, pushing
     *       {@code pos} up to at least {@code 1} and {@code neg} down to at most
     *       {@code -1};
     *   <li>a positive level maxes into {@code pos}, a zero or negative one mins
     *       into {@code neg}.
     * </ul>
     *
     * <p>In the last two cases a pair that is still absent or unknown is
     * <em>replaced</em> rather than compared against - and note that the
     * replacement also resets the opposite accumulator to {@code 0}. That reset
     * is what stops a sentinel from surviving in the half the contribution did
     * not touch, where it would later read as an enormous positive magnitude.
     *
     * <p>Zero takes the {@code else} branch and so mins into {@code neg}, leaving
     * it at {@code 0} if no vulnerability has been seen. This differs from
     * {@link LogicalOrWithCancelCombiner}, where zero is deliberately neither
     * sighting; here {@code neg == 0} and "no vulnerability" are the same state,
     * so there is nothing to distinguish.
     *
     * @param x       the incoming contribution (possibly a sentinel)
     * @param stateIn the channel's current accumulator pair
     * @return the updated pair
     */
    private TwoValues resist0CombineAccumHelp(int x, TwoValues stateIn) {
        int pos = stateIn.pos();
        int neg = stateIn.neg();

        if (x == UI_ENTRY_VALUE_NOT_PRESENT)
            return new TwoValues(pos, neg);

        if (x == UI_ENTRY_UNKNOWN_VALUE) {
            if (pos == UI_ENTRY_VALUE_NOT_PRESENT) {
                pos = UI_ENTRY_UNKNOWN_VALUE;
                neg = UI_ENTRY_UNKNOWN_VALUE;
            }
            return new TwoValues(pos, neg);
        }

        if (x == UI_ENTRY_RESIST0_RES_VUL) {
            if (pos == UI_ENTRY_UNKNOWN_VALUE || pos == UI_ENTRY_VALUE_NOT_PRESENT) {
                pos = 1;
                neg = -1;
            } else {
                if (pos < 1)
                    pos = 1;

                if (neg > -1)
                    neg = -1;
            }
            return new TwoValues(pos, neg);
        }

        if (x > 0) {
            if (pos == UI_ENTRY_UNKNOWN_VALUE || pos == UI_ENTRY_VALUE_NOT_PRESENT) {
                pos = x;
                neg = 0;
            } else if (pos < x) {
                pos = x;
            }
        } else {
            if (neg == UI_ENTRY_UNKNOWN_VALUE || neg == UI_ENTRY_VALUE_NOT_PRESENT) {
                neg = x;
                pos = 0;
            } else if (neg > x)
                neg = x;
        }

        return new TwoValues(pos, neg);
    }

    /**
     * Completes the streaming fold by resolving each channel's two accumulators
     * into the single level actually reported. Unlike the arithmetic rows, this
     * is real work rather than a formality - the C gives RESIST_0 a genuine
     * {@code resist_0_combine_finish} where most rows use
     * {@code dummy_combine_finish}.
     *
     * <p>The rule is the one the C states in a comment: a vulnerability cancels a
     * resist but not an immunity. So if a real vulnerability was seen and the
     * resistance is below the immunity threshold of {@code 3}, the channel
     * collapses - to {@code -1} where there was no resistance at all, and to
     * {@link Combiner#UI_ENTRY_RESIST0_RES_VUL} where resistance and
     * vulnerability were both present, which the renderer draws as its own
     * distinct state. An immunity is above the threshold and survives untouched.
     *
     * <p>The sentinel guards in both conditions are inert: every sentinel sits at
     * the top of the {@code int} range, so {@code negAccum < 0} and
     * {@code accum < 3} have already excluded them. They are kept because that
     * redundancy holds only by virtue of where the constants happen to be
     * defined, and because dropping them would break the line-by-line
     * correspondence with the C. Contrast
     * {@link LogicalOrWithCancelCombiner#finish()}, where the same-looking guard
     * is load-bearing: it bit-tests, and {@code UNKNOWN} is
     * {@link Integer#MAX_VALUE}, whose bit 1 is set.
     *
     * <p>The collapse runs into locals and the result is a fresh
     * {@link UIEntryCombinerState}, so calling this twice gives the same answer
     * both times. The C can afford to collapse in place - it owns the caller's
     * struct and is about to free {@code work} - but doing so here would leave
     * {@code accum} holding {@code RES_VUL}, which a second pass would then read
     * back as a huge positive.
     *
     * <p>The reported negative accumulators are zeroed rather than carried out.
     * They are fold-internal bookkeeping with no meaning to a caller, and the C's
     * counterpart is freed outright at this point.
     *
     * @return a snapshot of the resolved value and auxiliary channels
     */
    @Override
    public UIEntryCombinerState finish() {
        int pos = state.getAccum();
        int posAux = state.getAccumAux();

        if (state.getNegAccum() < 0 && state.getNegAccum() != UI_ENTRY_UNKNOWN_VALUE
                && state.getNegAccum() != UI_ENTRY_VALUE_NOT_PRESENT) {
            if (state.getAccum() < 3 && state.getAccum() != UI_ENTRY_UNKNOWN_VALUE
                    && state.getAccum() != UI_ENTRY_VALUE_NOT_PRESENT) {
                if (state.getAccum() == 0)
                    pos = -1;
                else
                    pos = UI_ENTRY_RESIST0_RES_VUL;
            }
        }

        if (state.getNegAccumAux() < 0 && state.getNegAccumAux() != UI_ENTRY_UNKNOWN_VALUE
                && state.getNegAccumAux() != UI_ENTRY_VALUE_NOT_PRESENT) {
            if (state.getAccumAux() < 3 && state.getAccumAux() != UI_ENTRY_UNKNOWN_VALUE
                    && state.getAccumAux() != UI_ENTRY_VALUE_NOT_PRESENT) {
                if (state.getAccumAux() == 0)
                    posAux = -1;
                else
                    posAux = UI_ENTRY_RESIST0_RES_VUL;
            }
        }

        UIEntryCombinerState result = new UIEntryCombinerState();
        result.setAccum(pos);
        result.setAccumAux(posAux);
        result.setNegAccum(0);
        result.setNegAccumAux(0);

        return result;
    }

    /**
     * One-shot array form of the fold, the counterpart to C's
     * {@code resist_0_vec}: folds whole lists of values and auxiliaries in a
     * single call instead of streaming them through {@code init}/{@code accum}.
     *
     * <p>Because this row has a real finishing step, {@code vec} does both halves
     * of the job - fold every element into an accumulator pair, then resolve it -
     * where the arithmetic rows' one-shot form only ever folds. The C inlines
     * that resolution rather than calling {@code finish}, which is why the same
     * comparison chain appears twice here and twice again above.
     *
     * <p>The C signature is
     * {@code resist_0_vec(int n, const int *vals, const int *auxs, int *accum, int *accum_aux)},
     * but only the first two pointers are arrays: {@code accum} and
     * {@code accum_aux} are single out-parameters, dereferenced and never
     * indexed, and every call site passes {@code &vc, &ac}
     * ({@code ui-entry-renderers.c:635} and five others). They are C's way of
     * returning two values from a {@code void} function, so in the port they
     * disappear into the returned {@link UIEntryCombinerState} rather than
     * becoming parameters.
     *
     * <p>Both channels start at {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} and
     * every element - including the first - is folded through the same helper,
     * which is why the loops run from {@code 0} rather than seeding from element
     * zero. An {@code n == 0} run therefore yields NOT_PRESENT on both channels
     * with no special case.
     *
     * <p>Note that {@code n}, not the list size, bounds the fold - matching the
     * C's {@code for (i = 0; i < n; ++i)} over a bare {@code const int *} - so
     * any tail beyond {@code n} is ignored. The under-length guard has no
     * counterpart in the C, whose pointers carry no length to check; it is a
     * port-level defence, and returns {@code null} to stay consistent with the
     * rest of the combiner family.
     *
     * <p>The most-negative accumulators are function-local here, where the
     * streaming path keeps them in {@code state}. The C does the same - they are
     * one reused {@code int neg} on its stack - and they are not reported, since
     * {@code resist_0_vec} writes only {@code *accum} and {@code *accum_aux}.
     *
     * <p>Unlike the streaming path this neither reads nor disturbs the instance
     * {@code state}; it returns a fresh result.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return a state holding the resolved value and auxiliary channels, or
     * {@code null} if either input list is shorter than {@code n}
     */
    @Override
    public UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs) {
        int neg;
        int pos;
        int negAux;
        int posAux;

        if (n > values.size() || n > auxs.size())
            return null;

        // Value channel
        pos = UI_ENTRY_VALUE_NOT_PRESENT;
        neg = UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 0; index < n; index++) {
            TwoValues result = resist0CombineAccumHelp(values.get(index), new TwoValues(pos, neg));
            pos = result.pos();
            neg = result.neg();
        }

        if (neg < 0 && neg != UI_ENTRY_UNKNOWN_VALUE && neg != UI_ENTRY_VALUE_NOT_PRESENT) {
            if (pos < 3 && pos != UI_ENTRY_UNKNOWN_VALUE && pos != UI_ENTRY_VALUE_NOT_PRESENT) {
                if (pos == 0)
                    pos = -1;
                else
                    pos = UI_ENTRY_RESIST0_RES_VUL;
            }
        }

        // Auxiliary channel
        posAux = UI_ENTRY_VALUE_NOT_PRESENT;
        negAux = UI_ENTRY_VALUE_NOT_PRESENT;
        for (int index = 0; index < n; index++) {
            TwoValues result = resist0CombineAccumHelp(auxs.get(index), new TwoValues(posAux, negAux));
            posAux = result.pos();
            negAux = result.neg();
        }

        if (negAux < 0 && negAux != UI_ENTRY_UNKNOWN_VALUE &&
                negAux != UI_ENTRY_VALUE_NOT_PRESENT) {
            if (posAux < 3 && posAux != UI_ENTRY_UNKNOWN_VALUE && posAux != UI_ENTRY_VALUE_NOT_PRESENT) {
                if (posAux == 0)
                    posAux = -1;
                else
                    posAux = UI_ENTRY_RESIST0_RES_VUL;
            }
        }

        UIEntryCombinerState result = new UIEntryCombinerState();
        result.setAccum(pos);
        result.setAccumAux(posAux);
        result.setNegAccum(0);
        result.setNegAccumAux(0);

        return result;
    }

    /**
     * Returns an independent copy of this combiner, as declared by
     * {@link Combiner}. The copy gets its own {@link UIEntryCombinerState}
     * holding the same four channels, so folding through one combiner cannot
     * disturb the other; a shallow field copy would share the one state object
     * and defeat the point of cloning at all.
     *
     * <p>Cloning exists only because the port keeps the fold state inside the
     * combiner. The C original passes a caller-owned
     * {@code struct ui_entry_combiner_state} into every function, leaving the
     * combiners themselves stateless and freely shareable, so it needs no
     * equivalent. It is also why this row has nothing to deep-copy: the C's
     * matching state owns a heap-allocated {@code work} array that a copy would
     * have to duplicate, whereas here the negative accumulators are plain
     * {@code int} fields.
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
            return new Resist0Combiner();

        UIEntryCombinerState newState = new UIEntryCombinerState();
        newState.setNegAccumAux(state.getNegAccumAux());
        newState.setNegAccum(state.getNegAccum());
        newState.setAccum(state.getAccum());
        newState.setAccumAux(state.getAccumAux());

        Resist0Combiner newCombiner = new Resist0Combiner();
        newCombiner.state = newState;
        return newCombiner;
    }
}
