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

package uk.co.jackoftradesltd.frontend.screen.enums;

import uk.co.jackoftradesltd.channel.utils.Combiner;
import uk.co.jackoftradesltd.channel.utils.combiners.*;

/**
 * The name-indexed table of value-combining algorithms a UI entry can bind to, one
 * constant per way several contributing values (a resistance, a stat bonus, a
 * sustain flag, ...) fold down into the single figure actually displayed for it.
 * Mirrors the C original's alphabetically-sorted {@code combiners[]} table
 * ({@code ui-entry-combiner.c}) together with the two functions that index it,
 * {@code ui_entry_combiner_lookup(name)} and {@code ui_entry_combiner_get_funcs(ind)}
 * ({@code ui-entry-combiner.h}): each named constant here (other than {@link #NONE})
 * holds the {@link Combiner} prototype the C row's four function pointers become,
 * and {@link #init(int, int)} is the Java equivalent of looking that row up and
 * starting a fold on it.
 *
 * <p>{@link #NONE} is the Java counterpart of a C {@code combiner_index} of zero -
 * no name matched, or none was bound at all - meaning there is nothing to fold and a
 * single unadorned value is shown instead. The C original never calls its combining
 * functions in that case; a call site that expects one already bound instead
 * {@code assert(0)}s ({@code ui-entry.c:697}). The port carries the same
 * precondition by leaving {@link #NONE}'s prototype {@code null}, so calling
 * {@link #init(int, int)} on it fails fast with a {@code NullPointerException}
 * rather than silently folding nothing.
 *
 * <p>Class CombinerName coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public enum CombinerName {
    /**
     * No combining algorithm bound. Mirrors a C {@code combiner_index} of zero: the
     * UI entry shows its single contributing value unadorned rather than folding
     * several together. Carries no {@link Combiner} prototype, so
     * {@link #init(int, int)} must never be called on it.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    NONE(null),
    /**
     * Sums the contributing values. The {@code "ADD"} row of C's {@code
     * combiners[]} table ({@code simple_combine_init}, {@code add_combine_accum},
     * {@code dummy_combine_finish}, {@code add_vec} - {@code ui-entry-combiner.c});
     * full semantics live on {@link AddCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    ADD(new AddCombiner()),
    /**
     * Bitwise-ORs the contributing values together. The {@code "BITWISE_OR"} row of
     * C's {@code combiners[]} table ({@code simple_combine_init}, {@code
     * bitwise_or_combine_accum}, {@code dummy_combine_finish}, {@code
     * bitwise_or_vec} - {@code ui-entry-combiner.c}); full semantics live on
     * {@link BitwiseOrCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    BITWISE_OR(new BitwiseOrCombiner()),
    /**
     * Takes the first contributing value and ignores the rest. The {@code "FIRST"}
     * row of C's {@code combiners[]} table ({@code simple_combine_init}, {@code
     * dummy_combine_accum}, {@code dummy_combine_finish}, {@code first_vec} -
     * {@code ui-entry-combiner.c}); full semantics live on {@link FirstCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    FIRST(new FirstCombiner()),
    /**
     * Takes the largest contributing value. The {@code "LARGEST"} row of C's
     * {@code combiners[]} table ({@code simple_combine_init}, {@code
     * largest_combine_accum}, {@code dummy_combine_finish}, {@code largest_vec} -
     * {@code ui-entry-combiner.c}); full semantics live on {@link LargestCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    LARGEST(new LargestCombiner()),
    /**
     * Takes the last contributing value seen. The {@code "LAST"} row of C's
     * {@code combiners[]} table ({@code simple_combine_init}, {@code
     * last_combine_accum}, {@code dummy_combine_finish}, {@code last_vec} -
     * {@code ui-entry-combiner.c}); full semantics live on {@link LastCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    LAST(new LastCombiner()),
    /**
     * Logical-ORs the contributing values (true if any contributor is true). The
     * {@code "LOGICAL_OR"} row of C's {@code combiners[]} table ({@code
     * logical_combine_init}, {@code logical_or_combine_accum}, {@code
     * dummy_combine_finish}, {@code logical_or_vec} - {@code ui-entry-combiner.c});
     * full semantics live on {@link LogicalOrCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    LOGICAL_OR(new LogicalOrCombiner()),
    /**
     * Logical-ORs the contributing values, but a value and its opposite cancel
     * each other out rather than one simply winning. The {@code
     * "LOGICAL_OR_WITH_CANCEL"} row of C's {@code combiners[]} table ({@code
     * logical_or_with_cancel_combine_init}, {@code
     * logical_or_with_cancel_combine_accum}, {@code
     * logical_or_with_cancel_combine_finish}, {@code logical_or_with_cancel_vec} -
     * {@code ui-entry-combiner.c}); full semantics live on
     * {@link LogicalOrWithCancelCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    LOGICAL_OR_WITH_CANCEL(new LogicalOrWithCancelCombiner()),
    /**
     * Combines elemental resistance values, treating a resistance-and-vulnerability
     * pair without an immunity ({@link Combiner#UI_ENTRY_RESIST0_RES_VUL}) as a
     * special case rather than an ordinary value. The {@code "RESIST_0"} row of
     * C's {@code combiners[]} table ({@code resist_0_combine_init}, {@code
     * resist_0_combine_accum}, {@code resist_0_combine_finish}, {@code
     * resist_0_vec} - {@code ui-entry-combiner.c}); full semantics live on
     * {@link Resist0Combiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    RESIST_0(new Resist0Combiner()),
    /**
     * Takes the smallest contributing value. The {@code "SMALLEST"} row of C's
     * {@code combiners[]} table ({@code simple_combine_init}, {@code
     * smallest_combine_accum}, {@code dummy_combine_finish}, {@code smallest_vec} -
     * {@code ui-entry-combiner.c}); full semantics live on {@link SmallestCombiner}.
     *
     * <p>coded before 260916, commented in full on 260916.
     */
    SMALLEST(new SmallestCombiner());

    /**
     * The prototype {@link Combiner} for this name, cloned by
     * {@link #init(int, int)} to start each independent fold. Corresponds to the
     * function-pointer bundle a row of C's {@code combiners[]} table carries
     * ({@code struct ui_entry_combiner_funcs}, {@code ui-entry-combiner.c});
     * {@code null} for {@link #NONE}, which has no row of its own.
     *
     * <p>Field combiner coded before 260916, commented in full on 260916.
     */
    private final Combiner combiner;

    /**
     * Associates this constant with its prototype combining algorithm.
     *
     * @param combiner the prototype {@link #init(int, int)} clones for each fold,
     *                 or {@code null} for {@link #NONE}
     *
     *                 <p>Function CombinerName coded before 260916, commented in
     *                 full on 260916.
     */
    private CombinerName(Combiner combiner) {
        this.combiner = combiner;
    }

    /**
     * Starts a fresh, independent streaming fold under this algorithm, seeded with
     * the first contributing value and auxiliary value. Clones {@link #combiner}
     * and calls {@link Combiner#init(int, int) init} on the clone, mirroring
     * {@code ui_entry_combiner_get_funcs(index, &funcs)} followed by {@code
     * funcs.init_func(v, a, &cst)} on a fresh caller-owned state struct in the C
     * original ({@code ui-entry.c}) - cloning the prototype is how the port gets
     * that same fresh, isolated state, since {@link Combiner} implementations
     * (unlike C's stateless functions) hold their fold state internally rather
     * than in a caller-supplied struct.
     *
     * @param v the value channel of the first contribution
     * @param a the auxiliary channel of the first contribution
     * @return a fresh, independent combiner instance with the fold under way
     * @throws NullPointerException if this constant is {@link #NONE}, which has no
     *                              prototype to clone
     *
     *                              <p>Function init coded before 260916, commented
     *                              in full on 260916.
     */
    public Combiner init(int v, int a) {
        Combiner result = this.combiner.clone();
        result.init(v, a);
        return result;
    }
}
