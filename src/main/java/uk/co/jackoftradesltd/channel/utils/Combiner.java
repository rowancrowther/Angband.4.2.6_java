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

package uk.co.jackoftradesltd.channel.utils;

import java.util.List;

/**
 * The contract every "combining algorithm" implements: the rule Angband uses to fold several
 * equipment slots' contributions to one displayed property (a resistance, a stat bonus, a sustain
 * flag) down to the single figure shown on the character sheet. This is the Java shape of the C
 * original's {@code struct ui_entry_combiner_funcs} ({@code ui-entry-combiner.h}), whose four
 * function pointers - {@code init_func}, {@code accum_func}, {@code finish_func}, {@code vec_func} -
 * become the four methods below; the table of named algorithms it indexes ({@code combiners[]} in
 * {@code ui-entry-combiner.c}: {@code ADD}, {@code BITWISE_OR}, {@code FIRST}, {@code LARGEST},
 * {@code LAST}, {@code LOGICAL_OR}, {@code LOGICAL_OR_WITH_CANCEL}, {@code RESIST_0},
 * {@code SMALLEST}) becomes the implementations under the {@code combiners} subpackage.
 *
 * <p>Every contribution is a pair - a "value" and an "auxiliary" value, whose meaning varies by
 * algorithm (a permanent effect paired with a time-dependent one, or a stat modifier paired with its
 * sustain flag) - and there are two ways to combine a run of them, matching the C doc comment on
 * {@code struct ui_entry_combiner_state}:
 *
 * <ul>
 *   <li>streaming: {@link #init(int, int) init} the fold with the first pair, {@link #accum(int, int)
 *       accum} each further pair into it, then {@link #finish() finish} to read off the result;
 *   <li>one-shot: hand the whole run to {@link #vec(int, List, List) vec} at once.
 * </ul>
 *
 * <p>A value or auxiliary value may also be one of three reserved sentinels rather than a genuine
 * level, declared as the constants below: {@link #UI_ENTRY_UNKNOWN_VALUE} (not yet learned by the
 * player), {@link #UI_ENTRY_VALUE_NOT_PRESENT} (no contribution at all, for instance from empty
 * equipment), and {@link #UI_ENTRY_RESIST0_RES_VUL} (meaningful only to the {@code RESIST_0} row -
 * a resistance and a vulnerability to the same element together, without an immunity).
 *
 * <p>The C keeps its combiners stateless: every function takes the caller-owned {@code struct
 * ui_entry_combiner_state} as an explicit argument, so one set of functions can be shared freely
 * across any number of concurrent folds. The port instead lets an implementing instance hold its own
 * fold state directly (see, for instance, {@code Resist0Combiner}'s {@code state} field), which is
 * why this interface extends {@link Cloneable} and declares {@link #clone()}: taking an independent
 * copy of an un-initialised prototype is how a caller gets a fresh, isolated fold without sharing
 * mutable state the way the C's stateless functions can.
 *
 * @author Rowan Crowther
 *
 * <p>coded on 2026-08-30 / commented in full on 2026-09-15
 */
public interface Combiner extends Cloneable {
    /**
     * Sentinel meaning the true value is not yet known to the player. Mirrors the C's
     * {@code UI_ENTRY_UNKNOWN_VALUE}, defined as {@code (INT_MAX)} in {@code ui-entry-combiner.h}.
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    int UI_ENTRY_UNKNOWN_VALUE = Integer.MAX_VALUE;

    /**
     * Sentinel meaning there is no contribution to combine at all, for instance because the
     * corresponding equipment slot is empty. Mirrors the C's {@code UI_ENTRY_VALUE_NOT_PRESENT},
     * defined as {@code (INT_MAX - 1)} in {@code ui-entry-combiner.h}.
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    int UI_ENTRY_VALUE_NOT_PRESENT = Integer.MAX_VALUE - 1;

    /**
     * Sentinel used only by the {@code RESIST_0} row, meaning a source provides both a resistance
     * and a vulnerability to the same element without providing an immunity to it. Mirrors the C's
     * {@code UI_ENTRY_RESIST0_RES_VUL}, defined as {@code (INT_MAX - 2)} in
     * {@code ui-entry-combiner.h}.
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    int UI_ENTRY_RESIST0_RES_VUL = Integer.MAX_VALUE - 2;

    /**
     * Begins a fresh streaming fold, seeding it with the first contribution. Mirrors the C's
     * {@code init_func(int v, int a, struct ui_entry_combiner_state *st)}; where the C writes the
     * seed into a caller-owned state struct, an implementation here holds it directly as instance
     * state, to be extended by {@link #accum(int, int) accum} and read off by {@link #finish()
     * finish}.
     *
     * @param v the value channel of the first contribution
     * @param a the auxiliary channel of the first contribution
     *
     *          <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    void init(int v, int a);

    /**
     * Folds one further contribution into the running state started by {@link #init(int, int)
     * init}. Mirrors the C's {@code accum_func(int v, int a, struct ui_entry_combiner_state *st)}.
     *
     * @param v the value channel of this contribution
     * @param a the auxiliary channel of this contribution
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    void accum(int v, int a);

    /**
     * Completes the streaming fold and returns the combined result. Mirrors the C's
     * {@code finish_func(struct ui_entry_combiner_state *st)}, which leaves the answer in the state
     * struct's {@code accum}/{@code accum_aux} fields in place; here it is returned as a fresh
     * {@link UIEntryCombinerState} instead. For most rows this is a formality that reports the
     * running totals unchanged, but a row like {@code RESIST_0} does real work here to resolve
     * two independent running accumulators into the one figure actually reported.
     *
     * @return the combined value and auxiliary channels
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    UIEntryCombinerState finish();

    /**
     * Combines a whole run of contributions in one call, without going through
     * {@link #init(int, int) init}/{@link #accum(int, int) accum}/{@link #finish() finish}. Mirrors
     * the C's {@code vec_func(int n, const int *vals, const int *auxs, int *accum, int *accum_aux)};
     * the two C out-parameters become the fields of the returned {@link UIEntryCombinerState}.
     *
     * @param n      the number of contributions to combine
     * @param values the value channel of each contribution (at least {@code n} long)
     * @param auxs   the auxiliary channel of each contribution (at least {@code n} long)
     * @return the combined value and auxiliary channels
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    UIEntryCombinerState vec(int n, List<Integer> values, List<Integer> auxs);

    /**
     * Returns an independent copy of this combiner. The C original has no counterpart, since its
     * combiner functions are stateless and share one caller-owned {@link UIEntryCombinerState}
     * across any number of folds; this port keeps the fold state inside the combiner instance
     * instead, so cloning a prototype - typically before it has been {@link #init(int, int)
     * init}-ed - is how a caller gets a fresh, independent fold.
     *
     * @return an independent copy of this combiner
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    Combiner clone();
}
