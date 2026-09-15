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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link UIEntryCombinerState}, the port of the C original's
 * {@code struct ui_entry_combiner_state} ({@code ui-entry-combiner.h}).
 *
 * <p>There is no C control flow to walk here - the struct itself has no behaviour, only fields -
 * so these cover the two things that are still port-specific: that a fresh instance starts every
 * channel at zero (the struct has no C original either, since C never {@code calloc}s it or reads
 * {@code work[0]}/{@code work[1]} before {@code resist_0_combine_init} has set them, but a
 * freshly-constructed Java object is always zero-initialised) and that each accessor pair is a
 * faithful round trip, independent of the other three.
 *
 * @author Rowan Crowther
 */
class UIEntryCombinerStateTest {

    @Test
    void freshInstanceStartsAllChannelsAtZero() {
        UIEntryCombinerState state = new UIEntryCombinerState();

        assertEquals(0, state.getAccum());
        assertEquals(0, state.getAccumAux());
        assertEquals(0, state.getNegAccum());
        assertEquals(0, state.getNegAccumAux());
    }

    @Test
    void accumRoundTrips() {
        UIEntryCombinerState state = new UIEntryCombinerState();

        state.setAccum(42);

        assertEquals(42, state.getAccum());
        assertEquals(0, state.getAccumAux());
        assertEquals(0, state.getNegAccum());
        assertEquals(0, state.getNegAccumAux());
    }

    @Test
    void accumAuxRoundTrips() {
        UIEntryCombinerState state = new UIEntryCombinerState();

        state.setAccumAux(-7);

        assertEquals(-7, state.getAccumAux());
        assertEquals(0, state.getAccum());
        assertEquals(0, state.getNegAccum());
        assertEquals(0, state.getNegAccumAux());
    }

    @Test
    void negAccumRoundTrips() {
        UIEntryCombinerState state = new UIEntryCombinerState();

        state.setNegAccum(-1);

        assertEquals(-1, state.getNegAccum());
        assertEquals(0, state.getAccum());
        assertEquals(0, state.getAccumAux());
        assertEquals(0, state.getNegAccumAux());
    }

    @Test
    void negAccumAuxRoundTrips() {
        UIEntryCombinerState state = new UIEntryCombinerState();

        state.setNegAccumAux(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, state.getNegAccumAux());
        assertEquals(0, state.getAccum());
        assertEquals(0, state.getAccumAux());
        assertEquals(0, state.getNegAccum());
    }

    @Test
    void allFourChannelsAreIndependent() {
        UIEntryCombinerState state = new UIEntryCombinerState();

        state.setAccum(1);
        state.setAccumAux(2);
        state.setNegAccum(3);
        state.setNegAccumAux(4);

        assertEquals(1, state.getAccum());
        assertEquals(2, state.getAccumAux());
        assertEquals(3, state.getNegAccum());
        assertEquals(4, state.getNegAccumAux());
    }
}
