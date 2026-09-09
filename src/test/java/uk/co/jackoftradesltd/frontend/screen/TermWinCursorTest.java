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

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TermWin#setCx}, {@link TermWin#setCy} and {@link TermWin#setCu}: blind field writes,
 * the port of C directly assigning {@code scr->cx}/{@code cy}/{@code cu}
 * ({@code [C] src/z-term.h}). Unlike {@link Term#gotoXY}, which is the caller that validates
 * a coordinate before reaching these, {@code term_win} itself performs no bounds check - so
 * these accept any {@code int}, in or out of any terminal's range.
 *
 * <p>Read back by reflection, since {@link TermWin} has no getters of its own -
 * {@link TermGotoXYTest} uses the same helper shape.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermWinCursorTest {

    private TermWin scr;

    private static Field field(String name) throws Exception {
        Field field = TermWin.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @BeforeEach
    void buildATermWin() {
        scr = new TermWin();
        scr.init(80, 24);
    }

    /**
     * {@link TermWin#setCx} stores exactly the column given.
     */
    @Test
    void setCxStoresTheColumn() throws Exception {
        scr.setCx(42);

        assertEquals(42, field("cx").getInt(scr));
    }

    /**
     * {@link TermWin#setCy} stores exactly the row given.
     */
    @Test
    void setCyStoresTheRow() throws Exception {
        scr.setCy(17);

        assertEquals(17, field("cy").getInt(scr));
    }

    /**
     * {@link TermWin#setCu} stores the flag given, both ways.
     */
    @Test
    void setCuStoresTheFlag() throws Exception {
        scr.setCu(true);
        assertTrue(field("cu").getBoolean(scr));

        scr.setCu(false);
        assertFalse(field("cu").getBoolean(scr));
    }

    /**
     * No bounds check: a negative or otherwise out-of-terminal value is stored as-is. The
     * guard lives in {@link Term#gotoXY}, not here - matching C, where {@code scr->cx = x;}
     * is an unconditional assignment.
     */
    @Test
    void setCxAcceptsAnOutOfRangeValueWithoutValidation() throws Exception {
        scr.setCx(-999);

        assertEquals(-999, field("cx").getInt(scr));
    }

    /**
     * Setting one coordinate does not disturb the other.
     */
    @Test
    void settingOneCoordinateLeavesTheOtherAlone() throws Exception {
        scr.setCx(5);
        scr.setCy(9);

        scr.setCx(6);

        assertEquals(6, field("cx").getInt(scr));
        assertEquals(9, field("cy").getInt(scr), "setCx must not touch cy");
    }
}
