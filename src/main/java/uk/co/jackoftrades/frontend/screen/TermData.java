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

import javafx.scene.text.Font;
import uk.co.jackoftrades.frontend.screen.hooks.TermEventHook;
import uk.co.jackoftrades.frontend.screen.hooks.TermXtraWin;

/**
 * Per-window front-end state for one terminal, pairing a logical {@link Term}
 * with the on-screen {@link Screen} that draws it and all the platform geometry
 * (position, size, fonts, tile sizes) needed to lay it out. This is the Java
 * port of the C original's {@code term_data} struct ({@code src/main-win.c}),
 * which the Windows front end used to track each game window.
 *
 * @author ClaudeCode
 */
public class TermData {
    /**
     * The on-screen surface this terminal is drawn onto.
     *
     * @author ClaudeCode
     */
    private Screen screen;

    /**
     * The logical terminal this window backs.
     *
     * @author ClaudeCode
     */
    private Term t;

    /**
     * The window's name/title.
     *
     * @author ClaudeCode
     */
    private final String s;

    /**
     * Size of this terminal's key-input queue.
     *
     * @author ClaudeCode
     */
    private int keys;

    /**
     * Number of text rows in this terminal.
     *
     * @author ClaudeCode
     */
    private int rows;
    /**
     * Number of text columns in this terminal.
     *
     * @author ClaudeCode
     */
    private int cols;

    /**
     * Window X position on screen.
     *
     * @author ClaudeCode
     */
    private int posX;
    /**
     * Window Y position on screen.
     *
     * @author ClaudeCode
     */
    private int posY;
    /**
     * Window width in pixels.
     *
     * @author ClaudeCode
     */
    private int sizeWidth;
    /**
     * Window height in pixels.
     *
     * @author ClaudeCode
     */
    private int sizeHeight;
    /**
     * Left border offset (outer width 1) used when sizing the client area.
     *
     * @author ClaudeCode
     */
    private int sizeOW1;
    /**
     * Top border offset (outer height 1) used when sizing the client area.
     *
     * @author ClaudeCode
     */
    private int sizeOH1;
    /**
     * Right border offset (outer width 2) used when sizing the client area.
     *
     * @author ClaudeCode
     */
    private int sizeOW2;
    /**
     * Bottom border offset (outer height 2) used when sizing the client area.
     *
     * @author ClaudeCode
     */
    private int sizeOH2;

    /**
     * Re-entrancy guard set while a resize is being processed.
     *
     * @author ClaudeCode
     */
    private boolean sizeHack;
    /**
     * Re-entrancy guard set while an "extra" terminal action is being processed.
     *
     * @author ClaudeCode
     */
    private boolean xtraHack;

    /**
     * Whether this window is currently visible.
     *
     * @author ClaudeCode
     */
    private boolean visible;
    /**
     * Whether this window is maximized.
     *
     * @author ClaudeCode
     */
    private boolean maximized;
    /**
     * Whether the "bizarre" display workaround is enabled for this window.
     *
     * @author ClaudeCode
     */
    private boolean bizarre;

    /**
     * The font used to render this window's text.
     *
     * @author ClaudeCode
     */
    private Font font;
    /**
     * Character cell width in pixels for the current font.
     *
     * @author ClaudeCode
     */
    private int fontWidth;
    /**
     * Character cell height in pixels for the current font.
     *
     * @author ClaudeCode
     */
    private int fontHeight;

    /**
     * Tile width in pixels when graphics tiles are in use.
     *
     * @author ClaudeCode
     */
    private int tileWidth;
    /**
     * Tile height in pixels when graphics tiles are in use.
     *
     * @author ClaudeCode
     */
    private int tileHeight;

    /**
     * Tile width in pixels when drawing the reduced-scale map view.
     *
     * @author ClaudeCode
     */
    private int mapTileWidth;
    /**
     * Tile height in pixels when drawing the reduced-scale map view.
     *
     * @author ClaudeCode
     */
    private int mapTileHeight;

    /**
     * Whether the reduced-scale map view is currently active in this window.
     *
     * @author ClaudeCode
     */
    private boolean mapActive;

    /**
     * Create an empty terminal-window descriptor with a blank title.
     *
     * @author ClaudeCode
     */
    public TermData() {
        s = "";
    }

    /**
     * @return whether the reduced-scale map view is active
     * @author ClaudeCode
     */
    public boolean isMapActive() {
        return mapActive;
    }

    /**
     * @return the graphics tile width in pixels
     * @author ClaudeCode
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * @return the graphics tile height in pixels
     * @author ClaudeCode
     */
    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * @return the bottom border offset
     * @author ClaudeCode
     */
    public int getSizeOH2() {
        return sizeOH2;
    }

    /**
     * @return the right border offset
     * @author ClaudeCode
     */
    public int getSizeOW2() {
        return sizeOW2;
    }

    /**
     * @return the top border offset
     * @author ClaudeCode
     */
    public int getSizeOH1() {
        return sizeOH1;
    }

    /**
     * @return the left border offset
     * @author ClaudeCode
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
     * @author ClaudeCode
     */
    public void termDataLink(Term term) {
        if (term == null) {
            t = new Term();
        } else {
            t = term;
        }

        t.termInit(cols, rows, keys);

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
     * @return the logical {@link Term} bound to this window
     * @author ClaudeCode
     */
    public Term getTerm() {

        return t;
    }
}
