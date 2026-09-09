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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.SwingUI;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link TermData#getTerm}, {@link TermData#clear}, {@link TermData#setWindow},
 * {@link TermData#getWindow} and {@link TermData#dispose} - all straight field access in C
 * ({@code td->t}, {@code td->w}, {@code [C] src/win/win-term.h}) except {@link TermData#clear}
 * and {@link TermData#dispose}, neither of which has a C counterpart of its own:
 * {@code clear} hands off to {@link Window#clear()}'s port of {@code Term_clear}
 * ({@code [C] src/ui-term.c}), and {@code dispose} extracts the one per-window step -
 * {@code DestroyWindow(data[i].w)} - from the shutdown loop in C's {@code hook_quit}
 * ({@code [C] src/main-win.c}).
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermDataTest {

    private static final int ROWS = 24;
    private static final int COLS = 80;

    private static final AngbandDisplayCharacter MARKER =
            new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED);

    private static final AngbandDisplayCharacter BLANK =
            new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);

    @BeforeEach
    void requireADisplay() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: clear() reaches a Window, which is a JFrame");
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
     * {@link TermData#clear} reaches the attached window's real grid: a screen marked full of
     * one character comes back entirely blank, matching {@link Window#clear()}'s own contract.
     *
     * <p>{@link Window#clear()} installs a brand new array via {@code setChars} rather than
     * writing into the one already there, so the panel's grid is re-read by reflection after
     * the call rather than trusting the array handed in beforehand.
     */
    @Test
    void clearBlanksTheAttachedWindowsGrid() throws Exception {
        SwingUI swingUi = new SwingUI(null, null, null);
        Window window = swingUi.getActiveWindow();
        window.add(swingUi.new JPanelArea());

        AngbandDisplayCharacter[][] markedGrid = new AngbandDisplayCharacter[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                markedGrid[row][col] = MARKER;
            }
        }
        window.getArea().setChars(markedGrid);

        TermData termData = new TermData();
        termData.setWindow(window);

        termData.clear();

        Field displayField = SwingUI.JPanelArea.class.getDeclaredField("display");
        displayField.setAccessible(true);
        AngbandDisplayCharacter[][] clearedGrid =
                (AngbandDisplayCharacter[][]) displayField.get(window.getArea());

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                assertEquals(BLANK, clearedGrid[row][col],
                        "row " + row + ", column " + col + " should be blanked");
            }
        }
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
