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

package uk.co.jackoftradesltd.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.frontend.screen.TermData;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link SwingUI#terms}, {@link SwingUI#activeTermData} and {@link SwingUI#getActiveTermData()}
 * as the constructor leaves them: one {@link TermData} built, held in both {@code terms} and
 * {@code activeTermData} at once - the state before anything plays the part of C's
 * {@code Term_activate} ({@code [C] src/ui-term.c}).
 *
 * <p>{@code terms} has no public accessor, so it is read here by reflection, the same pattern
 * {@link uk.co.jackoftradesltd.frontend.screen.TermGotoXYTest TermGotoXYTest} uses for
 * {@code Term}'s private fields.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SwingUIActiveTermDataTest {

    @SuppressWarnings("unchecked")
    private static List<TermData> terms(SwingUI swingUi) throws Exception {
        Field field = SwingUI.class.getDeclaredField("terms");
        field.setAccessible(true);
        return (List<TermData>) field.get(swingUi);
    }

    @BeforeEach
    void requireADisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing SwingUI builds a JFrame");
    }

    /**
     * {@link SwingUI#getActiveTermData()} hands back a real, non-{@code null} terminal.
     */
    @Test
    void getActiveTermDataIsNotNull() {
        SwingUI swingUi = new SwingUI(null, null, null);

        assertNotNull(swingUi.getActiveTermData());
    }

    /**
     * {@link SwingUI#getActiveTermData()} and {@link SwingUI#getActiveWindow()} name the same
     * terminal from two directions - {@code getActiveWindow} is {@code getActiveTermData()}
     * followed by {@link TermData#getWindow()}.
     */
    @Test
    void getActiveTermDataOwnsTheActiveWindow() {
        SwingUI swingUi = new SwingUI(null, null, null);

        assertSame(swingUi.getActiveWindow(), swingUi.getActiveTermData().getWindow());
    }

    /**
     * {@code terms} holds exactly the one {@link TermData} the constructor built, and it is
     * the very object {@code activeTermData} names - not a second, equal-looking instance.
     */
    @Test
    void termsHoldsExactlyTheActiveTermData() throws Exception {
        SwingUI swingUi = new SwingUI(null, null, null);

        List<TermData> terms = terms(swingUi);

        assertEquals(1, terms.size());
        assertSame(swingUi.getActiveTermData(), terms.get(0));
    }
}
