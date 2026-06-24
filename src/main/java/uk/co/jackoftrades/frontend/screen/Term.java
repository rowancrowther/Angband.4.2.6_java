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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.frontend.events.Event;
import uk.co.jackoftrades.frontend.screen.enums.Sidebar;
import uk.co.jackoftrades.frontend.screen.hooks.TermEventHook;

import java.util.ArrayList;

/**
 * The abstract terminal: a platform-independent model of a text display, ported
 * from the C original's {@code term} struct ({@code src/z-term.h}). It holds the
 * display contents (as {@link TermWin} buffers), the region of the screen that
 * has changed since the last refresh, an input key queue, behaviour flags, and a
 * set of {@link TermEventHook} callbacks the front end installs to actually draw
 * text/pictures/cursors. The core game talks only to this abstraction; concrete
 * front ends (here {@link Screen}) supply the hooks.
 *
 * @author ClaudeCode
 */
public class Term {
    /**
     * Opaque user data attached by the front end.
     *
     * @author ClaudeCode
     */
    private Object user;

    /**
     * The front-end window state associated with this terminal.
     *
     * @author ClaudeCode
     */
    private TermData data;

    /**
     * Flag: whether {@link #user} is meaningful.
     *
     * @author ClaudeCode
     */
    private boolean userFlag;
    /**
     * Flag: whether {@link #data} is meaningful.
     *
     * @author ClaudeCode
     */
    private boolean dataFlag;

    /**
     * Whether this terminal is the currently active one.
     *
     * @author ClaudeCode
     */
    private boolean activeFlag;
    /**
     * Whether this terminal is mapped (shown).
     *
     * @author ClaudeCode
     */
    private boolean mappedFlag;
    /**
     * Whether the next refresh should fully erase before redrawing.
     *
     * @author ClaudeCode
     */
    private boolean totalErase;
    /**
     * Whether the terminal has a fixed size and cannot be resized.
     *
     * @author ClaudeCode
     */
    private boolean fixedShape;
    /**
     * Whether the bottom-right corner cell is unusable ("icky").
     *
     * @author ClaudeCode
     */
    private boolean ickyCorner;
    /**
     * Whether the cursor is drawn in software rather than by the front end.
     *
     * @author ClaudeCode
     */
    private boolean softCursor;
    /**
     * Whether every cell must be drawn via the picture hook.
     *
     * @author ClaudeCode
     */
    private boolean alwaysPict;
    /**
     * Whether the higher (graphics) picture hook is preferred when available.
     *
     * @author ClaudeCode
     */
    private boolean higherPict;
    /**
     * Whether every cell must be drawn via the text hook.
     *
     * @author ClaudeCode
     */
    private boolean alwaysText;
    /**
     * Whether the terminal should never emit "bored"/idle events.
     *
     * @author ClaudeCode
     */
    private boolean neverBored;
    /**
     * Whether row flushing ("frosh") should be suppressed.
     *
     * @author ClaudeCode
     */
    private boolean neverFrosh;

    /**
     * Where the status sidebar is positioned for this terminal.
     *
     * @author ClaudeCode
     */
    private Sidebar sidebarMode;

    /**
     * Whether the terminal accepts complex (multi-key/mouse) input.
     *
     * @author ClaudeCode
     */
    private boolean complexInput;

    /**
     * The pending input event (key) queue.
     *
     * @author ClaudeCode
     */
    private ArrayList<Event> keyQueue;

    /**
     * Index of the next event to read from {@link #keyQueue}.
     *
     * @author ClaudeCode
     */
    private int keyHead;
    /**
     * Index of the next free slot to write into {@link #keyQueue}.
     *
     * @author ClaudeCode
     */
    private int keyTail;
    /**
     * Insertion point for "extra"/priority events pushed to the front.
     *
     * @author ClaudeCode
     */
    private int keyXtra;
    /**
     * Capacity of the key queue.
     *
     * @author ClaudeCode
     */
    private int keySize;

    /**
     * Terminal width in columns.
     *
     * @author ClaudeCode
     */
    private int wid;
    /**
     * Terminal height in rows.
     *
     * @author ClaudeCode
     */
    private int hgt;

    /**
     * Topmost row of the region changed since the last refresh.
     *
     * @author ClaudeCode
     */
    private int y1;
    /**
     * Bottommost row of the region changed since the last refresh.
     *
     * @author ClaudeCode
     */
    private int y2;

    /**
     * Per-row leftmost changed column (parallel to rows).
     *
     * @author ClaudeCode
     */
    private ArrayList<Integer> x1;
    /**
     * Per-row rightmost changed column (parallel to rows).
     *
     * @author ClaudeCode
     */
    private ArrayList<Integer> x2;

    /**
     * Horizontal display offset of the terminal contents.
     *
     * @author ClaudeCode
     */
    private int offsetX;
    /**
     * Vertical display offset of the terminal contents.
     *
     * @author ClaudeCode
     */
    private int offsetY;

    /**
     * The contents as last displayed (used to diff against {@link #scr}).
     *
     * @author ClaudeCode
     */
    private TermWin old;
    /**
     * The current working contents to be drawn next refresh.
     *
     * @author ClaudeCode
     */
    private TermWin scr;

    /**
     * Scratch buffer used while building or restoring screens.
     *
     * @author ClaudeCode
     */
    private TermWin tmp;
    /**
     * Saved buffer used by the screen save/load stack.
     *
     * @author ClaudeCode
     */
    private TermWin mem;

    /**
     * Depth of the saved-screen stack.
     *
     * @author ClaudeCode
     */
    private int saved;

    /**
     * Hook invoked when the terminal is initialised.
     *
     * @author ClaudeCode
     */
    private Object initHook;
    /**
     * Hook invoked when the terminal is destroyed.
     *
     * @author ClaudeCode
     */
    private Object nukeHook;

    /**
     * Hook for "extra" platform actions (noise, flush, clear, delay, …).
     *
     * @author ClaudeCode
     */
    private TermEventHook xtraHook;
    /**
     * Hook for drawing the cursor.
     *
     * @author ClaudeCode
     */
    private TermEventHook cursHook;
    /**
     * Hook for drawing the large (tile-sized) cursor.
     *
     * @author ClaudeCode
     */
    private TermEventHook bigcursHook;
    /**
     * Hook for wiping (clearing) a run of cells.
     *
     * @author ClaudeCode
     */
    private TermEventHook wipeHook;
    /**
     * Hook for drawing a run of text.
     *
     * @author ClaudeCode
     */
    private TermEventHook textHook;
    /**
     * Hook for drawing a run of picture/tile cells.
     *
     * @author ClaudeCode
     */
    private TermEventHook pictHook;
    /**
     * Hook for drawing the reduced-scale map view.
     *
     * @author ClaudeCode
     */
    private TermEventHook viewMapHook;
    /**
     * Hook for double-height/decorated drawing.
     *
     * @author ClaudeCode
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
     * @author ClaudeCode
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
     * @author ClaudeCode
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
     * @author ClaudeCode
     */
    public void setSoftCursor(boolean softCursor) {
        this.softCursor = softCursor;
    }

    /**
     * Enable or disable preference for the higher (graphics) picture hook.
     *
     * @param higherPict true to prefer graphics tiles
     * @author ClaudeCode
     */
    public void setHigherPict(boolean higherPict) {
        this.higherPict = higherPict;
    }

    /**
     * Enable or disable complex (multi-key/mouse) input.
     *
     * @param complexInput true to accept complex input
     * @author ClaudeCode
     */
    public void setComplexInput(boolean complexInput) {
        this.complexInput = complexInput;
    }

    /**
     * Install the "extra" platform-action hook.
     *
     * @param xtraHook the hook to install
     * @author ClaudeCode
     */
    public void setXtraHook(TermEventHook xtraHook) {
        this.xtraHook = xtraHook;
    }

    /**
     * Install the cursor-drawing hook.
     *
     * @param cursHook the hook to install
     * @author ClaudeCode
     */
    public void setCursHook(TermEventHook cursHook) {
        this.cursHook = cursHook;
    }

    /**
     * Install the large-cursor drawing hook.
     *
     * @param bigcursHook the hook to install
     * @author ClaudeCode
     */
    public void setBigcursHook(TermEventHook bigcursHook) {
        this.bigcursHook = bigcursHook;
    }

    /**
     * Install the cell-wipe hook.
     *
     * @param wipeHook the hook to install
     * @author ClaudeCode
     */
    public void setWipeHook(TermEventHook wipeHook) {
        this.wipeHook = wipeHook;
    }

    /**
     * Install the text-drawing hook.
     *
     * @param textHook the hook to install
     * @author ClaudeCode
     */
    public void setTextHook(TermEventHook textHook) {
        this.textHook = textHook;
    }

    /**
     * Install the picture/tile-drawing hook.
     *
     * @param pictHook the hook to install
     * @author ClaudeCode
     */
    public void setPictHook(TermEventHook pictHook) {
        this.pictHook = pictHook;
    }

    /**
     * Install the double-height/decorated drawing hook.
     *
     * @param dblhHook the hook to install
     * @author ClaudeCode
     */
    public void setDblhHook(TermEventHook dblhHook) {
        this.dblhHook = dblhHook;
    }

    /**
     * Install the map-view drawing hook.
     *
     * @param viewMapHook the hook to install
     * @author ClaudeCode
     */
    public void setViewMapHook(TermEventHook viewMapHook) {
        this.viewMapHook = viewMapHook;
    }

    /**
     * Attach the front-end window data to this terminal.
     *
     * @param data the window data
     * @author ClaudeCode
     */
    public void setData(TermData data) {
        this.data = data;
    }

    /**
     * @return the front-end window data attached to this terminal
     * @author ClaudeCode
     */
    public TermData getTermData() {
        return data;
    }
}