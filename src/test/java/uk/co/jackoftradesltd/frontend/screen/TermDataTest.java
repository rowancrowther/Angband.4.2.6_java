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
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermXtraWin;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
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
     * A fresh 80x24 {@link Screen}, matching {@link SwingUI}'s own grid size - the fixture
     * {@link TermData}'s constructor now requires, since {@link TermData#termDataLink} builds
     * a {@code TermScreenHook} from it.
     */
    private static Screen newScreen() {
        return new Screen(new CellGrid(24, 80), new ArrayList<>());
    }

    /**
     * {@link TermData#getTerm} returns exactly the {@link Term} it was linked to, the same
     * identity C keeps by embedding {@code term t;} in {@code term_data} rather than pointing
     * to a copy.
     */
    @Test
    void getTermReturnsTheLinkedTerm() {
        Term term = new Term();
        TermData termData = new TermData(newScreen());

        termData.termDataLink(term);

        assertSame(term, termData.getTerm());
    }

    /**
     * {@code termDataLink(null)} builds its own {@link Term} - {@link TermData#getTerm} must
     * hand back that same instance, not {@code null}.
     */
    @Test
    void getTermReturnsTheSelfBuiltTermWhenLinkedWithNull() {
        TermData termData = new TermData(newScreen());

        termData.termDataLink(null);

        assertNotNull(termData.getTerm());
    }

    /**
     * Reads one of {@link Term}'s private fields, the way {@code term_data_link}
     * ({@code [C] src/main-win.c}) leaves them - {@code soft_cursor}, {@code complex_input}
     * and {@code higher_pict} have no public getters, so this is the only way to check what
     * {@link TermData#termDataLink} wrote without adding test-only API to {@link Term} itself.
     *
     * @param term the term to read
     * @param name the field's name
     * @return the field's value
     * @throws Exception if the field cannot be reached
     */
    private static Object termField(Term term, String name) throws Exception {
        Field field = Term.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(term);
    }

    /**
     * {@link TermData#termDataLink} enables soft-cursor, complex-input and higher-pict on the
     * linked {@link Term}, the port of {@code t->soft_cursor}, {@code t->complex_input} and
     * {@code t->higher_pict} all being set {@code true} in {@code term_data_link}
     * ({@code [C] src/main-win.c}).
     */
    @Test
    void termDataLinkEnablesSoftCursorComplexInputAndHigherPict() throws Exception {
        Term term = new Term();
        TermData termData = new TermData(newScreen());

        termData.termDataLink(term);

        assertTrue((Boolean) termField(term, "softCursor"), "soft_cursor");
        assertTrue((Boolean) termField(term, "complexInput"), "complex_input");
        assertTrue((Boolean) termField(term, "higherPict"), "higher_pict");
    }

    /**
     * {@link TermData#termDataLink} leaves the double-height hook {@code null} - the port of
     * {@code t->dblh_hook = NULL;} in {@code term_data_link} ({@code [C] src/main-win.c}). The
     * Windows front end never installs one at link time; C only wires one up later, elsewhere
     * in that file, conditionally on the "overdraw" graphics setting. This is the boundary a
     * prior version of the port got wrong by wiring the shared hook in here instead.
     */
    @Test
    void termDataLinkLeavesTheDoubleHeightHookNull() throws Exception {
        Term term = new Term();
        TermData termData = new TermData(newScreen());

        termData.termDataLink(term);

        assertNull(termField(term, "dblhHook"), "dblh_hook");
    }

    /**
     * The cursor, big-cursor, wipe, text, pict and view-map hooks all get the same
     * {@link TermXtraWin} instance - the Java collapsing of the C original's distinct per-role
     * functions ({@code Term_curs_win}, {@code Term_bigcurs_win}, {@code Term_wipe_win},
     * {@code Term_text_win}, {@code Term_pict_win}, {@code term_view_map_hook},
     * {@code [C] src/main-win.c}) into one event-handler type.
     */
    @Test
    void termDataLinkSharesOneHookInstanceAcrossTheDrawingRoles() throws Exception {
        Term term = new Term();
        TermData termData = new TermData(newScreen());

        termData.termDataLink(term);

        Object cursHook = termField(term, "cursHook");
        assertNotNull(cursHook, "curs_hook");
        assertTrue(cursHook instanceof TermXtraWin);

        assertSame(cursHook, termField(term, "bigcursHook"), "bigcurs_hook");
        assertSame(cursHook, termField(term, "wipeHook"), "wipe_hook");
        assertSame(cursHook, termField(term, "textHook"), "text_hook");
        assertSame(cursHook, termField(term, "pictHook"), "pict_hook");
        assertSame(cursHook, termField(term, "viewMapHook"), "view_map_hook");
    }

    /**
     * The "extra" hook gets its own {@link TermXtraWin} instance, distinct from the one shared
     * by the drawing hooks - matching C's {@code Term_xtra_win} being a function separate from
     * {@code Term_curs_win} and the rest ({@code [C] src/main-win.c}), even though Java's port
     * of those maps every role onto the same class.
     */
    @Test
    void termDataLinkGivesTheExtraHookItsOwnInstance() throws Exception {
        Term term = new Term();
        TermData termData = new TermData(newScreen());

        termData.termDataLink(term);

        Object xtraHook = termField(term, "xtraHook");
        assertNotNull(xtraHook, "xtra_hook");
        assertTrue(xtraHook instanceof TermXtraWin);
        assertNotSame(xtraHook, termField(term, "cursHook"),
                "xtra_hook and curs_hook are separate objects");
    }

    /**
     * {@link TermData#termDataLink} attaches this {@link TermData} to the {@link Term} it links,
     * the port of {@code t->data = td;} in {@code term_data_link} ({@code [C] src/main-win.c}).
     */
    @Test
    void termDataLinkAttachesThisTermDataToTheTerm() {
        Term term = new Term();
        TermData termData = new TermData(newScreen());

        termData.termDataLink(term);

        assertSame(termData, term.getTermData());
    }

    /**
     * The self-built {@link Term} from {@code termDataLink(null)} gets exactly the same wiring
     * as one that was passed in - {@code term_data_link} ({@code [C] src/main-win.c}) always
     * links the {@code term} embedded in its own {@code term_data}, so C has no equivalent of a
     * "freshly built" branch to diverge from the given-a-term one.
     */
    @Test
    void termDataLinkWiresTheSelfBuiltTermTheSameWay() throws Exception {
        TermData termData = new TermData(newScreen());

        termData.termDataLink(null);
        Term term = termData.getTerm();

        assertTrue((Boolean) termField(term, "softCursor"), "soft_cursor");
        assertNull(termField(term, "dblhHook"), "dblh_hook");
        assertSame(termData, term.getTermData());
    }

    /**
     * {@link TermData#setWindow}/{@link TermData#getWindow} round-trip exactly the window
     * given - the port of writing then reading C's {@code td->w}.
     */
    @Test
    void getWindowReturnsWhatWasSet() {
        TermData termData = new TermData(newScreen());
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

        TermData termData = new TermData(newScreen());
        termData.setWindow(window);

        assertTrue(window.isDisplayable(), "the fixture should start with a realised window");

        SwingUtilities.invokeAndWait(termData::dispose);

        assertFalse(window.isDisplayable(), "dispose must dispose the window");
    }
}
