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
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermEventHook;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermXtraWin;

import java.util.Objects;

/**
 * Per-window front-end state for one terminal, pairing a logical {@link Term} with the
 * {@link Window} that draws it and all the platform geometry (position, size, fonts, tile
 * sizes) needed to lay it out. This is the Java port of the C original's {@code term_data}
 * struct ({@code [C] src/win/win-term.h}, {@code struct _term_data}), which the Windows front
 * end used to track each game window as a {@code term t} and an {@code HWND w} side by side -
 * exactly what {@link #t} and {@link #window} are here. Neither {@link Term} nor {@link Window}
 * refers to the other; this class is the one place they meet.
 *
 * <p>Not every C field has a Java counterpart. {@code dwStyle}/{@code dwExStyle} (raw Win32
 * window-style bits) have nothing to port to, since {@link Window} extends {@code JFrame}
 * rather than wrapping an {@code HWND} directly; {@code font_want}/{@code font_file}/
 * {@code font_id} (the font resource itself, as opposed to its pixel cell size) likewise have no
 * counterpart, since this front end measures its font once in {@code SwingUI.init} rather than
 * storing a font handle per window.
 *
 * <p>Class TermData coded before 260909, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class TermData {

    /**
     * This window's title-bar text, set once at construction and never mutated afterwards -
     * the Java counterpart of the C original's {@code const char *s} ({@code [C]
     * src/win/win-term.h}), which {@code init_windows} ({@code [C] src/main-win.c}) passes
     * straight into {@code CreateWindowEx} as the window's title. Unlike the C field, nothing
     * here re-derives or changes it later; {@link Window#displayString} is the separate path
     * that updates the title bar text actually shown on screen.
     *
     * <p>Field screenTitle coded before 260916, commented in full on 260916.
     */
    private final String screenTitle;
    /**
     * The logical terminal this window backs - the Java counterpart of the C original's
     * embedded {@code term t} ({@code [C] src/win/win-term.h}, {@code struct _term_data}).
     * C reaches it by direct struct access, {@code td->t}; Java holds it by reference and
     * binds it in {@link #termDataLink}.
     *
     * <p>Field t coded before 260909, commented in full on 260916.
     */
    private Term t;
    /**
     * The platform window this terminal draws into - the Java counterpart of the C original's
     * {@code HWND w} ({@code [C] src/win/win-term.h}), or a curses front end's
     * {@code WINDOW *win} ({@code [C] src/main-gcu.c}). Paired with {@link #t} here rather than
     * held by it, or holding it in turn.
     *
     * <p>Field window coded before 260909, commented in full on 260916.
     */
    private Window window;
    /**
     * The drawable grid this terminal's text output hook writes into once
     * {@link #termDataLink} binds a {@link Term} - the destination
     * {@link uk.co.jackoftradesltd.frontend.screen.hooks.TermScreenHook TermScreenHook}
     * ultimately reaches through {@code screen.root()}. Has no counterpart field in the C
     * original's {@code term_data}: C's Windows front end draws straight onto the {@code HWND}
     * via GDI calls made from the hook functions themselves, with no separate grid object to
     * hand the terminal.
     *
     * <p>Field screen coded before 260909, commented in full on 260916.
     */
    private Screen screen;
    /**
     * Capacity of this terminal's key-input queue, in keypresses - the Java counterpart of the
     * C original's {@code uint keys} ({@code [C] src/win/win-term.h}), which
     * {@code term_data_link} ({@code [C] src/main-win.c}) passes straight into
     * {@code term_init} as the queue size; here {@link #termDataLink} passes it the same way
     * into {@link Term#termInit}.
     *
     * <p>Field keys coded before 260916, commented in full on 260916.
     */
    private int keys;

    /**
     * Number of text rows in this terminal - the Java counterpart of the C original's
     * {@code uint16_t rows} ({@code [C] src/win/win-term.h}), passed into {@link Term#termInit}
     * by {@link #termDataLink} the same way {@code term_data_link} passes {@code td->rows} into
     * {@code term_init}.
     *
     * <p>Field rows coded before 260916, commented in full on 260916.
     */
    private int rows;
    /**
     * Number of text columns in this terminal - the Java counterpart of the C original's
     * {@code uint16_t cols} ({@code [C] src/win/win-term.h}), passed into {@link Term#termInit}
     * by {@link #termDataLink} the same way {@code term_data_link} passes {@code td->cols} into
     * {@code term_init}.
     *
     * <p>Field cols coded before 260916, commented in full on 260916.
     */
    private int cols;

    /**
     * Window X position on screen, in pixels - the Java counterpart of the C original's
     * {@code uint pos_x} ({@code [C] src/win/win-term.h}), which {@code init_windows}
     * ({@code [C] src/main-win.c}) reads into the {@code WINDOWPLACEMENT} it hands
     * {@code SetWindowPlacement}.
     *
     * <p>Field posX coded before 260916, commented in full on 260916.
     */
    private int posX;
    /**
     * Window Y position on screen, in pixels - the Java counterpart of the C original's
     * {@code uint pos_y} ({@code [C] src/win/win-term.h}), read the same way as {@link #posX}.
     *
     * <p>Field posY coded before 260916, commented in full on 260916.
     */
    private int posY;
    /**
     * Window width in pixels - the Java counterpart of the C original's {@code uint size_wid}
     * ({@code [C] src/win/win-term.h}), used the same way {@link #posX} is.
     *
     * <p>Field sizeWidth coded before 260916, commented in full on 260916.
     */
    private int sizeWidth;
    /**
     * Window height in pixels - the Java counterpart of the C original's {@code uint size_hgt}
     * ({@code [C] src/win/win-term.h}), used the same way {@link #posY} is.
     *
     * <p>Field sizeHeight coded before 260916, commented in full on 260916.
     */
    private int sizeHeight;
    /**
     * Left border offset (outer width 1) used when sizing the client area - the Java
     * counterpart of the C original's {@code uint size_ow1} ({@code [C] src/win/win-term.h}).
     * Added to a column's pixel offset when {@code init_windows} ({@code [C] src/main-win.c})
     * derives a client rectangle from tile coordinates, and added together with
     * {@link #sizeOW2} when deriving total window width from {@link #cols} and the tile width.
     *
     * <p>Field sizeOW1 coded before 260916, commented in full on 260916.
     */
    private int sizeOW1;
    /**
     * Top border offset (outer height 1) used when sizing the client area - the Java
     * counterpart of the C original's {@code uint size_oh1} ({@code [C] src/win/win-term.h}),
     * used the same way as {@link #sizeOW1} but for row offsets and paired with
     * {@link #sizeOH2}.
     *
     * <p>Field sizeOH1 coded before 260916, commented in full on 260916.
     */
    private int sizeOH1;
    /**
     * Right border offset (outer width 2) used when sizing the client area - the Java
     * counterpart of the C original's {@code uint size_ow2} ({@code [C] src/win/win-term.h}),
     * added to {@link #sizeOW1} when deriving total window width from {@link #cols} and the
     * tile width.
     *
     * <p>Field sizeOW2 coded before 260916, commented in full on 260916.
     */
    private int sizeOW2;
    /**
     * Bottom border offset (outer height 2) used when sizing the client area - the Java
     * counterpart of the C original's {@code uint size_oh2} ({@code [C] src/win/win-term.h}),
     * added to {@link #sizeOH1} when deriving total window height from {@link #rows} and the
     * tile height.
     *
     * <p>Field sizeOH2 coded before 260916, commented in full on 260916.
     */
    private int sizeOH2;

    /**
     * Re-entrancy guard set while a resize is being processed - the Java counterpart of the
     * C original's {@code bool size_hack} ({@code [C] src/win/win-term.h}). {@code WM_SIZE}
     * handling ({@code [C] src/main-win.c}) checks this at entry and returns immediately if it
     * is set, so the {@code ShowWindow} calls it makes on sub-windows while resizing cannot
     * recursively re-enter the same handler; {@code init_windows} sets it the same way around
     * its own initial {@code ShowWindow} call.
     *
     * <p>Field sizeHack coded before 260916, commented in full on 260916.
     */
    private boolean sizeHack;
    /**
     * Re-entrancy guard for an "extra" terminal action - the Java counterpart of the C
     * original's {@code bool xtra_hack} ({@code [C] src/win/win-term.h}). Declared on the
     * struct in the C original but never read or written anywhere in it at this version, so it
     * is dead there; this field mirrors that and is currently unused in the port as well.
     *
     * <p>Field xtraHack coded before 260916, commented in full on 260916.
     */
    private boolean xtraHack;

    /**
     * Whether this window is currently visible - the Java counterpart of the C original's
     * {@code bool visible} ({@code [C] src/win/win-term.h}), which the sub-window show/hide
     * loops in {@code init_windows} and the {@code WM_SIZE} handler ({@code [C]
     * src/main-win.c}) read to decide which windows to {@code ShowWindow}.
     *
     * <p>Field visible coded before 260916, commented in full on 260916.
     */
    private boolean visible;
    /**
     * Whether this window is maximized - the Java counterpart of the C original's
     * {@code bool maximized} ({@code [C] src/win/win-term.h}).
     *
     * <p>Field maximized coded before 260916, commented in full on 260916.
     */
    private boolean maximized;
    /**
     * Whether the "bizarre" display workaround is enabled for this window - the Java
     * counterpart of the C original's {@code bool bizarre} ({@code [C] src/win/win-term.h}), a
     * user-toggleable, ini-file-backed setting. When set (or when the tile size does not match
     * the font size), the C original's glyph-drawing code takes a slower path that erases the
     * whole cell rectangle before drawing, rather than relying on the font's own background
     * fill ({@code [C] src/main-win.c}).
     *
     * <p>Field bizarre coded before 260916, commented in full on 260916.
     */
    private boolean bizarre;

    /**
     * Character cell width in pixels for the current font - the Java counterpart of the C
     * original's {@code uint font_wid} ({@code [C] src/win/win-term.h}). C tracks the font
     * resource itself alongside this, in {@code font_want}/{@code font_file}/{@code font_id};
     * this front end has no counterpart to those fields, per the class-level note.
     *
     * <p>Field fontWidth coded before 260916, commented in full on 260916.
     */
    private int fontWidth;
    /**
     * Character cell height in pixels for the current font - the Java counterpart of the C
     * original's {@code uint font_hgt} ({@code [C] src/win/win-term.h}), tracked the same way
     * as {@link #fontWidth}.
     *
     * <p>Field fontHeight coded before 260916, commented in full on 260916.
     */
    private int fontHeight;

    /**
     * Tile width in pixels when graphics tiles are in use - the Java counterpart of the C
     * original's {@code uint tile_wid} ({@code [C] src/win/win-term.h}).
     *
     * <p>Field tileWidth coded before 260916, commented in full on 260916.
     */
    private int tileWidth;
    /**
     * Tile height in pixels when graphics tiles are in use - the Java counterpart of the C
     * original's {@code uint tile_hgt} ({@code [C] src/win/win-term.h}).
     *
     * <p>Field tileHeight coded before 260916, commented in full on 260916.
     */
    private int tileHeight;

    /**
     * Tile width in pixels when drawing the reduced-scale map view - the Java counterpart of
     * the C original's {@code uint map_tile_wid} ({@code [C] src/win/win-term.h}), derived from
     * {@link #tileWidth} scaled down to fit the whole level in the window and used in place of
     * it, for drawing, only while {@link #mapActive} is set.
     *
     * <p>Field mapTileWidth coded before 260916, commented in full on 260916.
     */
    private int mapTileWidth;
    /**
     * Tile height in pixels when drawing the reduced-scale map view - the Java counterpart of
     * the C original's {@code uint map_tile_hgt} ({@code [C] src/win/win-term.h}), related to
     * {@link #tileHeight} the same way {@link #mapTileWidth} is related to {@link #tileWidth}.
     *
     * <p>Field mapTileHeight coded before 260916, commented in full on 260916.
     */
    private int mapTileHeight;

    /**
     * Whether the reduced-scale map view is currently active in this window - the Java
     * counterpart of the C original's {@code bool map_active} ({@code [C] src/win/win-term.h}),
     * which gates whether drawing code reads {@link #mapTileWidth}/{@link #mapTileHeight} in
     * place of {@link #tileWidth}/{@link #tileHeight}.
     *
     * <p>Field mapActive coded before 260916, commented in full on 260916.
     */
    private boolean mapActive;

    /**
     * Create an empty terminal-window descriptor with a blank title, holding the given
     * {@link Screen} for {@link #termDataLink} to build a screen hook from later. No direct C
     * counterpart - the C original's {@code term_data} entries are constructed in bulk as a
     * fixed {@code data[MAX_TERM_DATA]} array ({@code [C] src/main-win.c}), with fields filled
     * in afterwards, rather than built one at a time through a constructor taking their
     * eventual values.
     *
     * <p>Constructor TermData coded before 260916, commented in full on 260916.
     *
     * @param screen the drawable grid this terminal's text output ultimately writes into
     */
    public TermData(Screen screen) {
        screenTitle = "";
        this.screen = screen;
    }

    /**
     * Whether the reduced-scale map view is active. No dedicated C function backs this - the
     * original reads {@code td->map_active} ({@code [C] src/win/win-term.h}) directly, since
     * {@code term_data} is a plain struct with no accessor of its own.
     *
     * <p>Function isMapActive coded before 260916, commented in full on 260916.
     *
     * @return whether the reduced-scale map view is active
     */
    public boolean isMapActive() {
        return mapActive;
    }

    /**
     * The graphics tile width in pixels. The port of reading C's {@code td->tile_wid}
     * ({@code [C] src/win/win-term.h}) directly; there is no dedicated getter function in the
     * original.
     *
     * <p>Function getTileWidth coded before 260916, commented in full on 260916.
     *
     * @return the graphics tile width in pixels
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * The graphics tile height in pixels. The port of reading C's {@code td->tile_hgt}
     * ({@code [C] src/win/win-term.h}) directly; there is no dedicated getter function in the
     * original.
     *
     * <p>Function getTileHeight coded before 260916, commented in full on 260916.
     *
     * @return the graphics tile height in pixels
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * The bottom border offset used when sizing the client area. The port of reading C's
     * {@code td->size_oh2} ({@code [C] src/win/win-term.h}) directly; there is no dedicated
     * getter function in the original.
     *
     * <p>Function getSizeOH2 coded before 260916, commented in full on 260916.
     *
     * @return the bottom border offset
     */
    public int getSizeOH2() {
        return sizeOH2;
    }

    /**
     * The right border offset used when sizing the client area. The port of reading C's
     * {@code td->size_ow2} ({@code [C] src/win/win-term.h}) directly; there is no dedicated
     * getter function in the original.
     *
     * <p>Function getSizeOW2 coded before 260916, commented in full on 260916.
     *
     * @return the right border offset
     */
    public int getSizeOW2() {
        return sizeOW2;
    }

    /**
     * The top border offset used when sizing the client area. The port of reading C's
     * {@code td->size_oh1} ({@code [C] src/win/win-term.h}) directly; there is no dedicated
     * getter function in the original.
     *
     * <p>Function getSizeOH1 coded before 260916, commented in full on 260916.
     *
     * @return the top border offset
     */
    public int getSizeOH1() {
        return sizeOH1;
    }

    /**
     * The left border offset used when sizing the client area. The port of reading C's
     * {@code td->size_ow1} ({@code [C] src/win/win-term.h}) directly; there is no dedicated
     * getter function in the original.
     *
     * <p>Function getSizeOW1 coded before 260916, commented in full on 260916.
     *
     * @return the left border offset
     */
    public int getSizeOW1() {
        return sizeOW1;
    }

    /**
     * Bind a logical {@link Term} to this window and wire up its hooks. Uses {@code term} if
     * one is given, or creates a fresh {@link Term} otherwise, initialises it to this window's
     * column/row/key sizes, enables soft-cursor / complex-input / higher-pict modes, and
     * installs the {@link TermXtraWin} event handlers for the drawing hooks. This is the Java
     * port of the C original's {@code term_data_link} ({@code [C] src/main-win.c}), which always
     * links the {@code term} embedded in its own {@code term_data}; the {@code term} parameter
     * here exists only because Java has no equivalent embedded field to take the address of.
     *
     * <p>The double-height hook is left {@code null}, matching {@code t->dblh_hook = NULL} in
     * the C original - this front end installs no double-height hook at link time; C only wires
     * one up later, conditionally on the "overdraw" graphics setting, elsewhere in
     * {@code src/main-win.c}. Every other hook (cursor, big cursor, wipe, text, pict, view-map)
     * is bound to a shared {@link TermXtraWin} instance, and the "extra" hook to a second,
     * separate instance of the same class - the Java collapsing of the C original's distinct
     * per-role functions ({@code Term_curs_win}, {@code Term_xtra_win}, and so on) into one
     * event-handler type.
     *
     * <p>Function termDataLink coded on 260916, commented in full on 260916.
     *
     * @param term the terminal to link, or {@code null} to create a new one
     */
    public void termDataLink(Term term) {
        t = Objects.requireNonNullElseGet(term, Term::new);

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
        t.setDblhHook(null);
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
