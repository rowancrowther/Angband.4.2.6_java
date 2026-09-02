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

import uk.co.jackoftradesltd.frontend.events.Event;
import uk.co.jackoftradesltd.frontend.screen.enums.Sidebar;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermEventHook;

import java.util.ArrayList;

/**
 * The abstract terminal: a platform-independent model of a text display, ported
 * from the C original's {@code term} struct ({@code src/z-term.h}). It holds the
 * display contents (as {@link TermWin} buffers), the region of the screen that
 * has changed since the last refresh, an input key queue, behaviour flags, and a
 * set of {@link TermEventHook} callbacks the front end installs to actually draw
 * text/pictures/cursors. The core game talks only to this abstraction; concrete
 * front ends (here {@link uk.co.jackoftradesltd.frontend.screen.hooks.TermXtraWin TermXtraWin}
 * and its siblings) supply the hooks.
 *
 * @author Rowan Crowther
 */
public class Term {
    /**
     * Opaque user data attached by the front end.
     */
    private Object user;

    /**
     * The front-end window state associated with this terminal.
     */
    private TermData data;

    /**
     * Flag: whether {@link #user} is meaningful.
     */
    private boolean userFlag;
    /**
     * Flag: whether {@link #data} is meaningful.
     */
    private boolean dataFlag;

    /**
     * Whether this terminal is the currently active one.
     */
    private boolean activeFlag;
    /**
     * Whether this terminal is mapped (shown).
     */
    private boolean mappedFlag;
    /**
     * Whether the next refresh should fully erase before redrawing.
     */
    private boolean totalErase;
    /**
     * Whether the terminal has a fixed size and cannot be resized.
     */
    private boolean fixedShape;
    /**
     * Whether the bottom-right corner cell is unusable ("icky").
     */
    private boolean ickyCorner;
    /**
     * Whether the cursor is drawn in software rather than by the front end.
     */
    private boolean softCursor;
    /**
     * Whether every cell must be drawn via the picture hook.
     */
    private boolean alwaysPict;
    /**
     * Whether the higher (graphics) picture hook is preferred when available.
     */
    private boolean higherPict;
    /**
     * Whether every cell must be drawn via the text hook.
     */
    private boolean alwaysText;
    /**
     * Whether the terminal should never emit "bored"/idle events.
     */
    private boolean neverBored;
    /**
     * Whether row flushing ("frosh") should be suppressed.
     */
    private boolean neverFrosh;

    /**
     * Where the status sidebar is positioned for this terminal.
     */
    private Sidebar sidebarMode;

    /**
     * Whether the terminal accepts complex (multi-key/mouse) input.
     */
    private boolean complexInput;

    /**
     * The pending input event (key) queue.
     */
    private ArrayList<Event> keyQueue;

    /**
     * Index of the next event to read from {@link #keyQueue}.
     */
    private int keyHead;
    /**
     * Index of the next free slot to write into {@link #keyQueue}.
     */
    private int keyTail;
    /**
     * Insertion point for "extra"/priority events pushed to the front.
     */
    private int keyXtra;
    /**
     * Capacity of the key queue.
     */
    private int keySize;

    /**
     * Terminal width in columns.
     */
    private int wid;
    /**
     * Terminal height in rows.
     */
    private int hgt;

    /**
     * Topmost row of the region changed since the last refresh.
     */
    private int y1;
    /**
     * Bottommost row of the region changed since the last refresh.
     */
    private int y2;

    /**
     * Per-row leftmost changed column (parallel to rows).
     */
    private ArrayList<Integer> x1;
    /**
     * Per-row rightmost changed column (parallel to rows).
     */
    private ArrayList<Integer> x2;

    /**
     * Horizontal display offset of the terminal contents.
     */
    private int offsetX;
    /**
     * Vertical display offset of the terminal contents.
     */
    private int offsetY;

    /**
     * The contents as last displayed (used to diff against {@link #scr}).
     */
    private TermWin old;
    /**
     * The current working contents to be drawn next refresh.
     */
    private TermWin scr;

    /**
     * Scratch buffer used while building or restoring screens.
     */
    private TermWin tmp;
    /**
     * Saved buffer used by the screen save/load stack.
     */
    private TermWin mem;

    /**
     * Depth of the saved-screen stack.
     */
    private int saved;

    /**
     * Hook invoked when the terminal is initialised.
     */
    private Object initHook;
    /**
     * Hook invoked when the terminal is destroyed.
     */
    private Object nukeHook;

    /**
     * Hook for "extra" platform actions (noise, flush, clear, delay, …).
     */
    private TermEventHook xtraHook;
    /**
     * Hook for drawing the cursor.
     */
    private TermEventHook cursHook;
    /**
     * Hook for drawing the large (tile-sized) cursor.
     */
    private TermEventHook bigcursHook;
    /**
     * Hook for wiping (clearing) a run of cells.
     */
    private TermEventHook wipeHook;
    /**
     * Hook for drawing a run of text.
     */
    private TermEventHook textHook;
    /**
     * Hook for drawing a run of picture/tile cells.
     */
    private TermEventHook pictHook;
    /**
     * Hook for drawing the reduced-scale map view.
     */
    private TermEventHook viewMapHook;
    /**
     * Hook for double-height/decorated drawing.
     */
    private TermEventHook dblhHook;

    /**
     * Initialise this terminal to the given size and key-queue capacity: reset
     * all behaviour flags, allocate the {@link #old}/{@link #scr} content buffers
     * and the per-row change bounds, and clear all hooks. This is the Java port
     * of the C original's {@code term_init}.
     *
     * @param width  terminal width in columns
     * @param height terminal height in rows
     * @param keys   key-queue capacity
     */
    public void termInit(int width, int height, int keys) {
        user = null;
        data = null;

        userFlag = false;
        dataFlag = false;
        activeFlag = false;
        mappedFlag = false;
        totalErase = false;
        fixedShape = false;
        ickyCorner = false;
        softCursor = false;
        alwaysPict = false;
        higherPict = false;
        alwaysText = false;
        neverBored = false;
        neverFrosh = false;
        sidebarMode = Sidebar.SIDEBAR_LEFT;
        complexInput = false;
        keyQueue = new ArrayList<>();
        keyHead = 0;
        keyTail = 0;
        keyXtra = 0;
        keySize = keys;
        wid = width;
        hgt = height;
        x1 = new ArrayList<>();
        x2 = new ArrayList<>();

        old = new TermWin();
        old.init(width, height);

        scr = new TermWin();
        scr.init(width, height);

        if (x1.isEmpty()) {
            initArrays(height);
        } else {
            for (int index = 0; index < height; index++) {
                x1.set(index, 0);
                x2.set(index, width - 1);
            }

            y1 = 0;
            y2 = height - 1;

            totalErase = true;
            saved = 0;
        }

        initHook = null;
        nukeHook = null;
        textHook = null;
        pictHook = null;
        viewMapHook = null;
        dblhHook = null;
        xtraHook = null;
        cursHook = null;
        bigcursHook = null;
        wipeHook = null;
    }

    /**
     * Seed the per-row change-bound arrays ({@link #x1}/{@link #x2}) with one
     * zero entry per row.
     *
     * @param height number of rows to initialise
     */
    private void initArrays(int height) {
        for (int y = 0; y < height; y++) {
            x1.add(0);
            x2.add(0);
        }
    }

    /**
     * Enable or disable software cursor drawing.
     *
     * @param softCursor true to draw the cursor in software
     */
    public void setSoftCursor(boolean softCursor) {
        this.softCursor = softCursor;
    }

    /**
     * Enable or disable preference for the higher (graphics) picture hook.
     *
     * @param higherPict true to prefer graphics tiles
     */
    public void setHigherPict(boolean higherPict) {
        this.higherPict = higherPict;
    }

    /**
     * Enable or disable complex (multi-key/mouse) input.
     *
     * @param complexInput true to accept complex input
     */
    public void setComplexInput(boolean complexInput) {
        this.complexInput = complexInput;
    }

    /**
     * Install the "extra" platform-action hook.
     *
     * @param xtraHook the hook to install
     */
    public void setXtraHook(TermEventHook xtraHook) {
        this.xtraHook = xtraHook;
    }

    /**
     * Install the cursor-drawing hook.
     *
     * @param cursHook the hook to install
     */
    public void setCursHook(TermEventHook cursHook) {
        this.cursHook = cursHook;
    }

    /**
     * Install the large-cursor drawing hook.
     *
     * @param bigcursHook the hook to install
     */
    public void setBigcursHook(TermEventHook bigcursHook) {
        this.bigcursHook = bigcursHook;
    }

    /**
     * Install the cell-wipe hook.
     *
     * @param wipeHook the hook to install
     */
    public void setWipeHook(TermEventHook wipeHook) {
        this.wipeHook = wipeHook;
    }

    /**
     * Install the text-drawing hook.
     *
     * @param textHook the hook to install
     */
    public void setTextHook(TermEventHook textHook) {
        this.textHook = textHook;
    }

    /**
     * Install the picture/tile-drawing hook.
     *
     * @param pictHook the hook to install
     */
    public void setPictHook(TermEventHook pictHook) {
        this.pictHook = pictHook;
    }

    /**
     * Install the double-height/decorated drawing hook.
     *
     * @param dblhHook the hook to install
     */
    public void setDblhHook(TermEventHook dblhHook) {
        this.dblhHook = dblhHook;
    }

    /**
     * Install the map-view drawing hook.
     *
     * @param viewMapHook the hook to install
     */
    public void setViewMapHook(TermEventHook viewMapHook) {
        this.viewMapHook = viewMapHook;
    }

    /**
     * Attach the front-end window data to this terminal.
     *
     * @param data the window data
     */
    public void setData(TermData data) {
        this.data = data;
    }

    /**
     * @return the front-end window data attached to this terminal
     */
    public TermData getTermData() {
        return data;
    }
}