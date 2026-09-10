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

package uk.co.jackoftradesltd.frontend.screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TermWin#getCu}, {@link TermWin#getCx} and {@link TermWin#getCy}: the port of
 * reading C's {@code scr->cu}/{@code cx}/{@code cy} directly ({@code [C] src/ui-term.h}).
 * {@link TermWinCursorTest} covers the matching setters by reflection, from before these
 * getters existed; these tests read the same state back through the public API instead.
 *
 * <p>The default-state test is derived from C, not the Java implementation: {@code Term->scr}
 * is allocated with {@code mem_zalloc(sizeof(term_win))} and {@code term_win_init} never
 * touches {@code cu}/{@code cx}/{@code cy}/{@code cnx}/{@code cny} ({@code [C] src/ui-term.c}
 * lines 2919-2928 and 353-372), so a freshly initialised window is zeroed - cursor at
 * {@code (0, 0)}, usable.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermWinGettersTest {

    private TermWin scr;

    @BeforeEach
    void buildATermWin() {
        scr = new TermWin();
        scr.init(80, 24);
    }

    /**
     * A freshly initialised window has its cursor at {@code (0, 0)} and usable - C never
     * writes these fields itself, relying on {@code mem_zalloc} zeroing them.
     */
    @Test
    void aFreshWindowHasTheCursorAtOriginAndUsable() {
        assertEquals(0, scr.getCx());
        assertEquals(0, scr.getCy());
        assertFalse(scr.getCu());
    }

    /**
     * {@link TermWin#getCx} reads back exactly what {@link TermWin#setCx} stored.
     */
    @Test
    void getCxReturnsWhatWasSet() {
        scr.setCx(42);

        assertEquals(42, scr.getCx());
    }

    /**
     * {@link TermWin#getCy} reads back exactly what {@link TermWin#setCy} stored.
     */
    @Test
    void getCyReturnsWhatWasSet() {
        scr.setCy(17);

        assertEquals(17, scr.getCy());
    }

    /**
     * {@link TermWin#getCu} reads back exactly what {@link TermWin#setCu} stored, both ways.
     */
    @Test
    void getCuReturnsWhatWasSet() {
        scr.setCu(true);
        assertTrue(scr.getCu());

        scr.setCu(false);
        assertFalse(scr.getCu());
    }
}
