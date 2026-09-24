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

/**
 * The running fold state passed between the four methods of a {@link Combiner}: the Java shape of
 * the C original's {@code struct ui_entry_combiner_state} ({@code ui-entry-combiner.h}), which holds
 * a value channel and an auxiliary channel accumulator ({@code accum}, {@code accum_aux}) plus a
 * {@code void *work} scratch pointer.
 *
 * <p>The C's {@code work} pointer is {@code mem_alloc}-ed to a two-{@code int} array by
 * {@code resist_0_combine_init} and freed by {@code resist_0_combine_finish} - it is the only row
 * that uses it, every other row's {@code init_func} sets it to {@code 0} and never touches it again
 * (see {@code ui-entry-combiner.c}: {@code simple_combine_init}, {@code logical_combine_init},
 * {@code logical_or_with_cancel_combine_init}). Rather than model a generic scratch pointer, the port
 * gives the two ints it ever holds named fields, {@link #negAccum} and {@link #negAccumAux}, so
 * {@code Resist0Combiner} is the only combiner that reads or writes them and there is nothing to
 * allocate or free.
 *
 * <p>coded on 2026-08-30 / commented in full on 2026-09-15
 *
 * @author Rowan Crowther
 */
public class UIEntryCombinerState {
    /**
     * The value channel's most-negative accumulator, used only by the {@code RESIST_0} row to track
     * the deepest vulnerability seen while {@link #accum} tracks the strongest resistance. Mirrors
     * {@code work[0]} in the C's {@code resist_0_combine_*} functions; every other row leaves this at
     * its default of {@code 0}, unread.
     */
    private int negAccum;

    /**
     * The auxiliary channel's counterpart to {@link #negAccum}. Mirrors {@code work[1]} in the C's
     * {@code resist_0_combine_*} functions.
     */
    private int negAccumAux;

    /**
     * The value channel's running fold result. Mirrors the C's {@code accum} field, which
     * {@code finish_func} leaves holding the combined value.
     */
    private int accum;

    /**
     * The auxiliary channel's running fold result. Mirrors the C's {@code accum_aux} field, which
     * {@code finish_func} leaves holding the combined auxiliary value.
     */
    private int accumAux;

    public UIEntryCombinerState() {
        this.negAccum = 0;
        this.negAccumAux = 0;
        this.accum = 0;
        this.accumAux = 0;
    }

    public UIEntryCombinerState(int negAccum, int negAccumAux, int accum, int accumAux) {
        this.negAccum = negAccum;
        this.negAccumAux = negAccumAux;
        this.accum = accum;
        this.accumAux = accumAux;
    }

    /**
     * Returns the value channel's most-negative accumulator.
     *
     * @return the tracked most-negative value, meaningful only mid-fold in {@code Resist0Combiner}
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public int getNegAccum() {
        return negAccum;
    }

    /**
     * Sets the value channel's most-negative accumulator.
     *
     * @param negAccum the new most-negative value
     *
     *                 <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public void setNegAccum(int negAccum) {
        this.negAccum = negAccum;
    }

    /**
     * Returns the auxiliary channel's most-negative accumulator.
     *
     * @return the tracked most-negative auxiliary value, meaningful only mid-fold in
     * {@code Resist0Combiner}
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public int getNegAccumAux() {
        return negAccumAux;
    }

    /**
     * Sets the auxiliary channel's most-negative accumulator.
     *
     * @param negAccumAux the new most-negative auxiliary value
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public void setNegAccumAux(int negAccumAux) {
        this.negAccumAux = negAccumAux;
    }

    /**
     * Returns the value channel's running fold result.
     *
     * @return the combined value, or a sentinel from {@link Combiner}
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public int getAccum() {
        return accum;
    }

    /**
     * Sets the value channel's running fold result.
     *
     * @param accum the new combined value
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public void setAccum(int accum) {
        this.accum = accum;
    }

    /**
     * Returns the auxiliary channel's running fold result.
     *
     * @return the combined auxiliary value, or a sentinel from {@link Combiner}
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public int getAccumAux() {
        return accumAux;
    }

    /**
     * Sets the auxiliary channel's running fold result.
     *
     * @param accumAux the new combined auxiliary value
     *
     * <p>coded on 2026-08-30 / commented in full on 2026-09-15
     */
    public void setAccumAux(int accumAux) {
        this.accumAux = accumAux;
    }
}
