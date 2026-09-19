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

package uk.co.jackoftradesltd.frontend.globals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link UIGlobals}' {@code textOutIndent} holder against the C original's {@code int
 * text_out_indent = 0;} ({@code [C] src/z-textblock.c:382}), a plain, unbounded global that call
 * sites read and write directly with no validation.
 *
 * <p>Every test resets the holder back to {@code 0} afterwards, matching the C convention (seen
 * in {@code ui-birth.c}, {@code ui-store.c}, {@code ui-target.c}, and elsewhere) of always
 * restoring {@code text_out_indent} to {@code 0} once the indented block of output is done.
 *
 * @author Rowan Crowther
 */
class UIGlobalsTest {

    /**
     * Puts the holder back to the C original's default of {@code 0}, so a test running after this
     * one sees what it expected.
     */
    @AfterEach
    void resetIndent() {
        UIGlobals.setTextOutIndent(0);
    }

    /**
     * With nothing set, the holder reads back {@code 0}, matching C's {@code int text_out_indent =
     * 0;} default-initialisation.
     */
    @Test
    @DisplayName("defaults to 0, matching C's default-initialised global")
    void defaultsToZero() {
        assertEquals(0, UIGlobals.getTextOutIndent());
    }

    /**
     * A positive value round-trips through the setter and getter, matching a C call site such as
     * {@code ui-birth.c}'s {@code text_out_indent = RACE_AUX_COL;}.
     */
    @Test
    @DisplayName("a set value is read back unchanged")
    void setThenGetRoundTrips() {
        UIGlobals.setTextOutIndent(5);

        assertEquals(5, UIGlobals.getTextOutIndent());
    }

    /**
     * Setting and reading is repeatable, matching the C global being written and read many times
     * over a program's life rather than being fixed once.
     */
    @Test
    @DisplayName("later sets overwrite earlier ones")
    void repeatedSetsOverwrite() {
        UIGlobals.setTextOutIndent(3);
        assertEquals(3, UIGlobals.getTextOutIndent());

        UIGlobals.setTextOutIndent(11);
        assertEquals(11, UIGlobals.getTextOutIndent());
    }

    /**
     * Setting back to {@code 0} after a non-zero value reproduces the reset call sites make once
     * their indented block of {@code text_out} calls is finished, for example {@code ui-birth.c}'s
     * {@code text_out_indent = 0;} after {@code RACE_AUX_COL}.
     */
    @Test
    @DisplayName("can be reset to 0 after a non-zero value")
    void resetsToZero() {
        UIGlobals.setTextOutIndent(7);
        assertEquals(7, UIGlobals.getTextOutIndent());

        UIGlobals.setTextOutIndent(0);
        assertEquals(0, UIGlobals.getTextOutIndent());
    }

    /**
     * C places no bounds on {@code text_out_indent} - it is a plain {@code int} - so a negative
     * value round-trips just like any other; the setter neither clamps nor rejects it.
     */
    @Test
    @DisplayName("a negative value round-trips, since C places no bounds on the global either")
    void negativeValueRoundTrips() {
        UIGlobals.setTextOutIndent(-1);

        assertEquals(-1, UIGlobals.getTextOutIndent());
    }
}
