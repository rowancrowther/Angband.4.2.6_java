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

import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermEventHook;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermXtraWin;

/**
 * Per-window front-end state for one terminal, pairing a logical {@link Term} with the
 * {@link Window} that draws it and all the platform geometry (position, size, fonts, tile
 * sizes) needed to lay it out. This is the Java port of the C original's {@code term_data}
 * struct ({@code [C] src/win/win-term.h}, {@code struct _term_data}), which the Windows front
 * end used to track each game window as a {@code term t} and an {@code HWND w} side by side -
 * exactly what {@link #t} and {@link #window} are here. Neither {@link Term} nor {@link Window}
 * refers to the other; this class is the one place they meet.
 *
 * @author Rowan Crowther
 */
public class TermData {

    /**
     * The logical terminal this window backs.
     */
    private Term t;

    /**
     * The platform window this terminal draws into - the Java counterpart of the C original's
     * {@code HWND w} ({@code [C] src/win/win-term.h}), or a curses front end's
     * {@code WINDOW *win} ({@code [C] src/main-gcu.c}). Paired with {@link #t} here rather than
     * held by it, or holding it in turn.
     */
    private Window window;

    private Screen screen;

    /**
     * The window's name/title.
     */
    private final String s;

    /**
     * Size of this terminal's key-input queue.
     */
    private int keys;

    /**
     * Number of text rows in this terminal.
     */
    private int rows;
    /**
     * Number of text columns in this terminal.
     */
    private int cols;

    /**
     * Window X position on screen.
     */
    private int posX;
    /**
     * Window Y position on screen.
     */
    private int posY;
    /**
     * Window width in pixels.
     */
    private int sizeWidth;
    /**
     * Window height in pixels.
     */
    private int sizeHeight;
    /**
     * Left border offset (outer width 1) used when sizing the client area.
     */
    private int sizeOW1;
    /**
     * Top border offset (outer height 1) used when sizing the client area.
     */
    private int sizeOH1;
    /**
     * Right border offset (outer width 2) used when sizing the client area.
     */
    private int sizeOW2;
    /**
     * Bottom border offset (outer height 2) used when sizing the client area.
     */
    private int sizeOH2;

    /**
     * Re-entrancy guard set while a resize is being processed.
     */
    private boolean sizeHack;
    /**
     * Re-entrancy guard set while an "extra" terminal action is being processed.
     */
    private boolean xtraHack;

    /**
     * Whether this window is currently visible.
     */
    private boolean visible;
    /**
     * Whether this window is maximized.
     */
    private boolean maximized;
    /**
     * Whether the "bizarre" display workaround is enabled for this window.
     */
    private boolean bizarre;

    /**
     * Character cell width in pixels for the current font.
     */
    private int fontWidth;
    /**
     * Character cell height in pixels for the current font.
     */
    private int fontHeight;

    /**
     * Tile width in pixels when graphics tiles are in use.
     */
    private int tileWidth;
    /**
     * Tile height in pixels when graphics tiles are in use.
     */
    private int tileHeight;

    /**
     * Tile width in pixels when drawing the reduced-scale map view.
     */
    private int mapTileWidth;
    /**
     * Tile height in pixels when drawing the reduced-scale map view.
     */
    private int mapTileHeight;

    /**
     * Whether the reduced-scale map view is currently active in this window.
     */
    private boolean mapActive;

    /**
     * Create an empty terminal-window descriptor with a blank title.
     */
    public TermData(Screen screen) {
        s = "";
        this.screen = screen;
    }

    /**
     * @return whether the reduced-scale map view is active
     */
    public boolean isMapActive() {
        return mapActive;
    }

    /**
     * @return the graphics tile width in pixels
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * @return the graphics tile height in pixels
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * @return the bottom border offset
     */
    public int getSizeOH2() {
        return sizeOH2;
    }

    /**
     * @return the right border offset
     */
    public int getSizeOW2() {
        return sizeOW2;
    }

    /**
     * @return the top border offset
     */
    public int getSizeOH1() {
        return sizeOH1;
    }

    /**
     * @return the left border offset
     */
    public int getSizeOW1() {
        return sizeOW1;
    }

    /**
     * Bind a logical {@link Term} to this window and wire up its hooks. Creates a
     * fresh {@link Term} if {@code term} is {@code null}, initialises it to this
     * window's column/row/key sizes, enables soft-cursor / complex-input /
     * higher-pict modes, and installs the {@link TermXtraWin} event handlers for
     * the various drawing hooks. This is the Java port of the C original's
     * {@code term_data_link}.
     *
     * @param term the terminal to link, or {@code null} to create a new one
     */
    public void termDataLink(Term term) {
        if (term == null) {
            t = new Term();
        } else {
            t = term;
        }

        t.termInit(cols, rows, keys, this, screen);

        t.setSoftCursor(true);
        t.setComplexInput(true);
        t.setHigherPict(true);

        TermEventHook hook = new TermXtraWin();
        TermXtraWin win = new TermXtraWin();

        t.setXtraHook(win);
        t.setCursHook(hook);
        t.setBigcursHook(hook);
        t.setWipeHook(hook);
        t.setTextHook(hook);
        t.setPictHook(hook);
        t.setDblhHook(hook);
        t.setViewMapHook(hook);

        t.setData(this);
    }

    /**
     * The logical {@link Term} bound to this window. No dedicated C function backs this -
     * the original reaches the same value with the direct struct access {@code td->t},
     * since {@code term_data.t} ({@code [C] src/win/win-term.h}) is an embedded
     * {@code term}, not a pointer.
     *
     * <p>Function getTerm coded on 260909, commented in full on 260909.
     *
     * @return the logical {@link Term} bound to this window
     */
    public Term getTerm() {
        return t;
    }

    /**
     * The platform {@link Window} this terminal draws into. The port of reading C's
     * {@code td->w} ({@code [C] src/win/win-term.h}) directly; there is no dedicated
     * getter function in the original.
     *
     * <p>Function getWindow coded on 260909, commented in full on 260909.
     *
     * @return the window this terminal draws into
     */
    public Window getWindow() {
        return window;
    }

    /**
     * Attach the platform {@link Window} this terminal draws into. The port of assigning
     * C's {@code td->w} ({@code [C] src/win/win-term.h}) directly; there is no dedicated
     * setter function in the original, since C code writes the struct field in place.
     *
     * <p>Function setWindow coded on 260909, commented in full on 260909.
     *
     * @param window the window to attach
     */
    public void setWindow(Window window) {
        this.window = window;
    }

    /**
     * Release this terminal's platform window, by handing off to
     * {@link java.awt.Window#dispose()} - {@link Window} inherits it through
     * {@code JFrame}, and never overrides it. The Java-side extraction of one
     * per-window step from C's shutdown loop - {@code DestroyWindow(data[i].w)}
     * ({@code [C] src/main-win.c}) - into a method {@link SwingUI#closeDown()} can
     * call once per {@link TermData}, rather than walking a separate array of raw
     * windows of its own.
     *
     * <p>Deliberately narrower than that C loop's full per-window teardown, matching
     * the scope {@link SwingUI#closeDown()} already documents for itself: nothing
     * here nils {@link #window} afterwards, frees fonts, or calls {@code term_nuke}
     * on {@link #t} - the way C's loop does all three - because this object and
     * everything it holds go away with the process shortly after.
     *
     * <p>Function dispose coded on 260909, commented in full on 260909.
     */
    public void dispose() {
        window.dispose();
    }
}
