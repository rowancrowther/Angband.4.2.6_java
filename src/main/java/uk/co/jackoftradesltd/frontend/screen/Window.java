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

import uk.co.jackoftradesltd.frontend.SwingUI;
import uk.co.jackoftradesltd.frontend.screen.grid.Frame;

import javax.swing.*;
import java.awt.*;

/// One game window: a frame wrapped around a single character grid. This is the Java counterpart
/// of the native window handle a C front end's `term_data` holds beside its `term` -
/// `HWND w` for the Windows front end (`[C] src/win/win-term.h`), or
/// `WINDOW *win` for curses (`[C] src/main-gcu.c`). [TermData] is where a
/// `Window` and a [Term] meet, each in its own field; this class is not a stand-in
/// for [Term] and holds no reference back to one.
///
/// The frame holds exactly one `JPanelArea`, captured by [#add] as it goes in so the
/// rest of the front end can reach the grid without walking the component tree. C's
/// `term_data` array is a fixed eight (`ANGBAND_TERM_MAX`) - a main window plus
/// subwindows - so more of these are expected; nothing here assumes it is the only one.
///
/// Everything on this class is Swing, so every method belongs on the event dispatch thread.
/// {@code clear()} in particular is currently reached from the game thread through
/// `SplashScreen`, which is a bug in the caller rather than here.
///
/// @author Rowan Crowther
public class Window extends JFrame {
    /**
     * The character grid this window displays, captured by {@link #add}. Null until a
     * {@code JPanelArea} has been added, which {@code SwingUI.init} does during start-up.
     */
    private SwingUI.JPanelArea area;

    /**
     * Build an empty window with a placeholder title. Nothing is sized, laid out or shown here -
     * {@code SwingUI.init} does all of that once it has measured the font.
     */
    public Window() {
        super("Welcome");
    }

    /**
     * Put a string in the window's title bar.
     *
     * <p>Chrome, not game display - the grid is where the game is drawn. Named for what it will
     * eventually be rather than what it does: this is the hook C's {@code Term_xtra(TERM_XTRA_TITLE)}
     * fills in.
     *
     * @param string the title to show
     */
    public void displayString(String string) {
        super.setTitle(string);
    }

    /**
     * Add a component, and remember it if it is the character grid.
     *
     * <p>Overridden purely to capture the grid on its way in, so {@link #getArea()} has something
     * to hand back without searching the component tree. Everything else is left to
     * {@link java.awt.Container#add(Component)}, including the layout invalidation it performs.
     *
     * <p>Only the last grid added is kept: a second one silently replaces the reference, though the
     * component itself is still added. That is fine while a window holds exactly one grid, which is
     * the arrangement {@code SwingUI.init} builds.
     *
     * @param comp the component to add; kept as this window's grid if it is a {@code JPanelArea}
     * @return the component argument, as {@code Container.add} contracts
     * @throws NullPointerException if {@code comp} is {@code null}
     */
    @Override
    public Component add(Component comp) {
        if (comp instanceof SwingUI.JPanelArea jPanel) {
            area = jPanel;
        }
        return super.add(comp);
    }

    /**
     * The character grid this window displays.
     *
     * @return the grid, or {@code null} if none has been added yet
     */
    public SwingUI.JPanelArea getArea() {
        return area;
    }

    /**
     * Push a frame onto this window's grid and repaint it. {@link Frame} is new architecture with
     * no C original ({@code Frame.java}); this method plays the role C's {@code Term_fresh()}
     * flush plays at the end of {@code splashscreen_note()} ({@code [C] src/ui-display.c}) -
     * painting onto a {@code Screen}'s grid is invisible until the active window is told to show
     * it again, which is what this does.
     *
     * <p>{@code frame.grid().getCells()} hands back rows shared with the {@code CellGrid} the
     * frame was built from, not copies of them - safe only because a {@link Frame}'s grid is never
     * written to again once published ({@code CellGrid.getCells()}'s own Javadoc names this method
     * as the one caller that discipline covers). Deferred onto the event dispatch thread with
     * {@link SwingUtilities#invokeLater}, since every caller today runs on a channel-processing
     * thread, not the EDT - the hop {@link #clear()}'s caller skips.
     *
     * <p>Function show coded before 260916, commented in full on 260916.
     *
     * @param frame the screen snapshot to paint; its grid replaces the window's current one
     */
    public void show(Frame frame) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                area.setChars(frame.grid().getCells());
                area.repaint();
            }
        });
    }

    /**
     * This window's character grid width, in columns - a pass-through to
     * {@link SwingUI.JPanelArea#getDisplayWidth()}, which is itself the port of half of
     * {@code Term_get_size} ({@code [C] src/ui-term.c}).
     *
     * <p>Not the same source as a terminal's own {@link Term#gotoXY} bounds check, which
     * reads the {@link Term} fields set by {@link Term#termInit} rather than asking the
     * window. Currently has no caller of its own.
     *
     * <p>Function getCharacterWidth coded on 260909, commented in full on 260909.
     *
     * @return the number of character columns in this window's grid
     */
    public int getCharacterWidth() {
        return area.getDisplayWidth();
    }

    /**
     * This window's character grid height, in rows - the counterpart
     * {@link #getCharacterWidth()} describes, delegating to
     * {@link SwingUI.JPanelArea#getDisplayHeight()}.
     *
     * <p>Function getCharacterHeight coded on 260909, commented in full on 260909.
     *
     * @return the number of character rows in this window's grid
     */
    public int getCharacterHeight() {
        return area.getDisplayHeight();
    }


}
