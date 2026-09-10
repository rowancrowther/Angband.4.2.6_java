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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.frontend.events.Event;
import uk.co.jackoftradesltd.frontend.screen.enums.Sidebar;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermEventHook;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermScreenHook;
import uk.co.jackoftradesltd.frontend.screen.hooks.TermTextHook;

import java.util.ArrayList;

/**
 * The abstract terminal: a platform-independent model of a text display, ported
 * from the C original's {@code term} struct ({@code [C] src/ui-term.h}). It holds the
 * display contents (as {@link TermWin} buffers), the region of the screen that
 * has changed since the last refresh, an input key queue, behaviour flags, and a
 * set of {@link TermEventHook} callbacks the front end installs to actually draw
 * text/pictures/cursors. The core game talks only to this abstraction; concrete
 * front ends (here {@link uk.co.jackoftradesltd.frontend.screen.hooks.TermXtraWin TermXtraWin}
 * and its siblings) supply the hooks.
 *
 * <p>Deliberately holds no reference to a platform window. C's {@code term.data} is a
 * {@code void*} the owning front end fills in with its own struct - for the Windows front end,
 * {@code term_data} ({@code [C] src/win/win-term.h}, {@code struct _term_data}), which pairs
 * the {@code term} with an {@code HWND}. {@link #data}, typed {@link TermData}, is the port of
 * that pointer; a {@link Window} belongs on {@link TermData}, not here.
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

    private TermTextHook outputHook;

    private TermData owner;

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
    public void termInit(int width, int height, int keys, TermData owner, Screen screen) {
        user = null;
        this.owner = owner;

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

        for (int index = 0; index < height; index++) {
            x1.add(0);
            x2.add(width - 1);
        }

        y1 = 0;
        y2 = height - 1;

        totalErase = true;
        saved = 0;

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
        outputHook = new TermScreenHook(screen);
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

    /**
     * Move the cursor to a given cell, the Java port of the C original's
     * {@code Term_gotoxy} ({@code [C] src/ui-term.c}). An out-of-range {@code x} or
     * {@code y} leaves the cursor exactly where it was and returns {@code -1}; C's own
     * comment on {@code Term_gotoxy} - "illegal requests do not move the cursor" - is
     * exactly this behaviour, not a Java addition.
     *
     * <p>Bounds are checked against {@link #wid}/{@link #hgt}, this terminal's own stored
     * dimensions from {@link #termInit}, matching C's {@code Term->wid}/{@code Term->hgt} -
     * not the front end's live window size, which can differ from what this terminal was
     * initialised to.
     *
     * <p>On success, writes the new column and row into {@link #scr} via
     * {@link TermWin#setCx}/{@link TermWin#setCy}, then clears the cursor's "unused" flag
     * with {@link TermWin#setCu}, matching {@code Term->scr->cx}/{@code cy}/{@code cu} in
     * that order.
     *
     * <p>Function gotoXY coded on 260909, commented in full on 260909.
     *
     * @param x the target column
     * @param y the target row
     * @return {@code 0} on success, {@code -1} if the coordinate is outside the terminal
     */
    public int gotoXY(int x, int y) {
        if (x < 0 || y < 0 || x >= this.wid || y >= this.hgt) return -1;

        scr.setCx(x);
        scr.setCy(y);
        scr.setCu(false);

        return 0;
    }

    /**
     * Move the cursor to a cell and write a coloured string there, the Java port of the
     * C original's {@code c_put_str} ({@code [C] src/ui-output.c}). Delegates the move to
     * {@link #gotoXY} and the write to {@link #outputHook}, matching {@code c_put_str}'s
     * own delegation to {@code Term_putstr}, which does the same two steps internally
     * ({@code Term_gotoxy} then {@code Term_addstr}).
     *
     * <p>An out-of-range {@code row}/{@code col} leaves the screen untouched and returns
     * {@code -1}, mirroring {@code Term_putstr} returning early - without reaching
     * {@code Term_addstr} - when {@code Term_gotoxy} fails. C's {@code c_put_str} is
     * {@code void} and discards this outcome; the Java port keeps it because
     * {@link #gotoXY} already produces it.
     *
     * <p>The string is always written with C's {@code n = -1} ("no explicit length"),
     * matching the fixed {@code -1} that {@code c_put_str} passes to {@code Term_putstr}.
     *
     * <p>Function cPutStr coded on 260910, commented in full on 260910.
     *
     * @param colour the colour to draw the string in
     * @param str    the string to write
     * @param row    the row to write it on
     * @param col    the column to start at
     * @return {@code 0} on success, {@code -1} if the coordinate is outside the terminal
     */
    public int cPutStr(ColourEnum colour, String str, int row, int col) {
        int result = gotoXY(col, row);
        if (result == -1) return -1;

        outputHook.putStr(col, row, -1, colour, str);
        return 0;
    }

    /**
     * As {@link #cPutStr}, but always in {@link ColourEnum#COLOUR_WHITE}, the Java port
     * of the C original's {@code put_str} ({@code [C] src/ui-output.c}).
     *
     * <p>Function putStr coded on 260910, commented in full on 260910.
     *
     * @param str the string to write
     * @param row the row to write it on
     * @param col the column to start at
     * @return {@code 0} on success, {@code -1} if the coordinate is outside the terminal
     */
    public int putStr(String str, int row, int col) {
        return cPutStr(ColourEnum.COLOUR_WHITE, str, row, col);
    }

    /**
     * Move the cursor to a cell and write a coloured string there, clearing the rest of
     * that row first, the Java port of the C original's {@code prt} ({@code [C] src/ui-output.c}).
     * Delegates entirely to {@link #cPrt} in {@link ColourEnum#COLOUR_WHITE}, matching
     * {@code prt}'s own delegation to {@code c_prt}.
     *
     * <p>Function prt coded on 260910, commented in full on 260910.
     *
     * @param str the string to write
     * @param row the row to write it on
     * @param col the column to start at
     */
    public void prt(String str, int row, int col) {
        cPrt(ColourEnum.COLOUR_WHITE, str, row, col);
    }

    /**
     * Write a coloured string at a cell, first clearing the rest of that row, the Java
     * port of the C original's {@code c_prt} ({@code [C] src/ui-output.c}). C does this in
     * two steps - {@code Term_erase(col, row, 255)} (which itself repositions the cursor
     * via {@code Term_gotoxy}) then {@code Term_addstr(-1, attr, str)} - and the Java port
     * keeps that as one call into {@link #outputHook}'s {@code cPrt}, since the
     * erase-then-draw boundary sits inside the hook rather than being composed here from
     * {@link #gotoXY} and {@link #addstr} the way {@link #cPutStr} composes {@link #gotoXY}
     * with the text-hook write.
     *
     * <p>Unlike {@link #cPutStr}, this has no out-of-range coordinate to report: C's
     * {@code c_prt} is {@code void}, and nothing along this path returns a failure code
     * to surface.
     *
     * <p>Function cPrt coded on 260910, commented in full on 260910.
     *
     * @param colour the colour to draw the string in
     * @param str    the string to write
     * @param row    the row to write it on
     * @param col    the column to start at
     */
    public void cPrt(ColourEnum colour, String str, int row, int col) {
        outputHook.cPrt(colour, str, row, col);
    }

    /**
     * Move to a cell and add a string there with an explicit maximum length, the Java
     * port of the C original's {@code Term_putstr} ({@code [C] src/ui-term.c}). Delegates
     * the move to {@link #gotoXY} and the write to {@link #addstr}, matching C's own two
     * internal steps ({@code Term_gotoxy} then {@code Term_addstr}); C additionally guards
     * on a null {@code Term} pointer before either step, which has no equivalent here
     * since this method can only run on an already-constructed {@code Term}.
     *
     * <p>Propagates whichever step returns non-zero first, exactly as given.
     * {@link #gotoXY} only ever returns {@code 0} or {@code -1}, so checking it against
     * {@code -1} and returning {@code -1} are equivalent; {@link #addstr}'s result is
     * returned unchanged - {@code -1} for an already-unusable cursor, or the positive
     * count of characters actually written when the write was truncated at the right
     * edge - matching C's {@code if ((res = ...) != 0) return (res);} pattern used for
     * both steps.
     *
     * <p>Function putstr coded on 260910, commented in full on 260910.
     *
     * @param x      the column to start at
     * @param y      the row to write on
     * @param n      the maximum number of characters to write; negative means "as many
     *               as fit"
     * @param colour the colour to draw the string in
     * @param str    the string to write
     * @return {@code 0} on success, {@code -1} if the coordinate is out of range or the
     * cursor is already unusable, or a positive count of characters actually written if
     * the string was truncated at the terminal's right edge
     */
    public int putstr(int x, int y, int n, ColourEnum colour, String str) {
        int output = gotoXY(x, y);
        if (output == -1) return -1;

        output = addstr(n, colour, str);
        if (output != 0) return output;

        return 0;
    }

    /**
     * At the cursor's current position, write up to {@code length} characters of a
     * string and advance the cursor, the Java port of the C original's
     * {@code Term_addstr} ({@code [C] src/ui-term.c}). A negative {@code length} means
     * "as many as fit", ported as {@code wid + 1} - deliberately one past the terminal
     * width, since the usable length is still capped below by both the string's own
     * length and, if needed, the distance remaining to the right edge.
     *
     * <p>Width is read from {@link #wid}, this terminal's own stored width from
     * {@link #termInit} - matching C's {@code Term->wid} - not any front end's live
     * window size, for the same reason given on {@link #gotoXY}.
     *
     * <p>A cursor already marked unusable ({@link TermWin#getCu}) writes nothing and
     * returns {@code -1} immediately, before anything else is computed - matching C's
     * {@code if (Term->scr->cu) return (-1);} guard. Otherwise, if the requested run
     * would reach or pass {@link #wid}, it is clipped to the columns actually remaining
     * ({@code wid - cx}), that clipped count becomes both the number of characters
     * written and the returned value, and the cursor is marked unusable via
     * {@link TermWin#setCu} for next time - matching C's
     * {@code if (cx + n >= w) res = n = w - cx;} followed by {@code if (res) cu = 1;}.
     *
     * <p>Function addstr coded on 260910, commented in full on 260910.
     *
     * @param length the maximum number of characters to write; negative means "as many
     *               as fit"
     * @param colour the colour to draw the string in
     * @param str    the string to write
     * @return {@code 0} on success, {@code -1} if the cursor was already unusable, or a
     * positive count of characters actually written if the string was truncated at the
     * terminal's right edge
     */
    public int addstr(int length, ColourEnum colour, String str) {
        // Is the curser useable
        if (scr.getCu()) return -1;

        // Get the maximal length
        int k = (length < 0) ? wid + 1 : length;

        // Get the useable string length
        int n = Math.min(k, str.length());

        int res = 0;
        // Check we don't overrun the screen
        if (scr.getCx() + n >= wid) {
            n = wid - scr.getCx();
            res = n;
        }

        outputHook.putStr(scr.getCx(), scr.getCy(), n, colour, str);

        scr.setCx(scr.getCx() + n);

        if (res != 0) scr.setCu(true);

        return res;
    }
}