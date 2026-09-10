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
import uk.co.jackoftradesltd.frontend.SwingUI;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link TermData#getTerm}, {@link TermData#setWindow}, {@link TermData#getWindow} and
 * {@link TermData#dispose} - all straight field access in C ({@code td->t}, {@code td->w},
 * {@code [C] src/win/win-term.h}) except {@link TermData#dispose}, which has no C counterpart
 * of its own: it extracts the one per-window step - {@code DestroyWindow(data[i].w)} - from
 * the shutdown loop in C's {@code hook_quit} ({@code [C] src/main-win.c}).
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermDataTest {

    @BeforeEach
    void requireADisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: Window is a JFrame");
    }

    /**
     * {@link TermData#getTerm} returns exactly the {@link Term} it was linked to, the same
     * identity C keeps by embedding {@code term t;} in {@code term_data} rather than pointing
     * to a copy.
     */
    @Test
    void getTermReturnsTheLinkedTerm() {
        Term term = new Term();
        TermData termData = new TermData();

        termData.termDataLink(term);

        assertSame(term, termData.getTerm());
    }

    /**
     * {@code termDataLink(null)} builds its own {@link Term} - {@link TermData#getTerm} must
     * hand back that same instance, not {@code null}.
     */
    @Test
    void getTermReturnsTheSelfBuiltTermWhenLinkedWithNull() {
        TermData termData = new TermData();

        termData.termDataLink(null);

        assertNotNull(termData.getTerm());
    }

    /**
     * {@link TermData#setWindow}/{@link TermData#getWindow} round-trip exactly the window
     * given - the port of writing then reading C's {@code td->w}.
     */
    @Test
    void getWindowReturnsWhatWasSet() {
        TermData termData = new TermData();
        Window window = new Window() {
        };

        termData.setWindow(window);

        assertSame(window, termData.getWindow());
    }

    /**
     * {@link TermData#dispose} reaches the attached window's real {@code dispose()} - the
     * window stops being displayable, the same assertion {@code SwingUITest} makes for
     * {@link SwingUI#closeDown()}, the method {@link TermData#dispose} exists for.
     */
    @Test
    void disposeDisposesTheAttachedWindow() throws Exception {
        SwingUI swingUi = new SwingUI(null, null, null);
        Window window = swingUi.getActiveWindow();
        SwingUtilities.invokeAndWait(window::pack);

        TermData termData = new TermData();
        termData.setWindow(window);

        assertTrue(window.isDisplayable(), "the fixture should start with a realised window");

        SwingUtilities.invokeAndWait(termData::dispose);

        assertFalse(window.isDisplayable(), "dispose must dispose the window");
    }
}
