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
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.util.ArrayList;

/**
 * One terminal's content buffer: the cursor state plus the grids of coloured
 * characters that make up the display. This is the Java port of the C original's
 * {@code term_win} struct ({@code src/z-term.h}). Where the C code keeps separate
 * attribute and character arrays, this port stores each cell as a single
 * {@link AngbandDisplayCharacter} (glyph + colour), with a main layer and a
 * terrain layer, each kept in both a working copy and a displayed/"visible" copy
 * so changes can be diffed before being flushed to screen.
 *
 * @author Rowan Crowther
 */
public class TermWin {
    /**
     * Cursor "used"/off-screen flag (true when the cursor is parked/disabled).
     */
    private boolean cu;
    /**
     * Cursor visible flag.
     */
    private boolean cv;
    /**
     * Cursor column.
     */
    private int cx;
    /**
     * Cursor row.
     */
    private int cy;
    /**
     * Secondary cursor column (e.g. for a multi-cell/"big" cursor).
     */
    private int cnx;
    /**
     * Secondary cursor row (e.g. for a multi-cell/"big" cursor).
     */
    private int cny;

    /**
     * Working copy of the main (character) layer.
     */
    private ArrayList<AngbandDisplayCharacter> a;
    /**
     * Displayed/"visible" copy of the main layer, diffed against {@link #a}.
     */
    private ArrayList<AngbandDisplayCharacter> va;
    /**
     * Working copy of the terrain layer.
     */
    private ArrayList<AngbandDisplayCharacter> ta;
    /**
     * Displayed/"visible" copy of the terrain layer, diffed against {@link #ta}.
     */
    private ArrayList<AngbandDisplayCharacter> vta;

    /**
     * Allocate the four content layers and fill them with blank white cells. The
     * grids are sized from {@code height}; one entry is added per row.
     *
     * @param width  the terminal width (currently unused in allocation)
     * @param height the terminal height, controlling how many cells are created
     */
    public void init(int width, int height) {
        a = new ArrayList<>();
        va = new ArrayList<>();
        ta = new ArrayList<>();
        vta = new ArrayList<>();

        for (int index = 0; index < height; index++) {
            a.add(new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE));
            va.add(new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE));
            ta.add(new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE));
            vta.add(new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE));
        }
    }

    /**
     * Set the cursor's column. The port of writing C's {@code scr->cx} directly
     * ({@code [C] src/z-term.h}); there is no dedicated C setter, since the original
     * assigns the struct field in place wherever it moves the cursor - see
     * {@code Term_gotoxy} ({@code [C] src/ui-term.c}) for the caller this exists for.
     *
     * <p>Function setCx coded on 260909, commented in full on 260909.
     *
     * @param x the new cursor column
     */
    public void setCx(int x) {
        this.cx = x;
    }

    /**
     * Set the cursor's row. The port of writing C's {@code scr->cy} directly
     * ({@code [C] src/z-term.h}); as with {@link #setCx}, the original has no dedicated
     * setter function.
     *
     * <p>Function setCy coded on 260909, commented in full on 260909.
     *
     * @param y the new cursor row
     */
    public void setCy(int y) {
        this.cy = y;
    }

    /**
     * Set the cursor's "unused"/off-screen flag. The port of writing C's {@code scr->cu}
     * directly ({@code [C] src/z-term.h}), where it is an {@code int} used as a boolean
     * (0/1); as with {@link #setCx}, the original has no dedicated setter function.
     *
     * <p>Function setCu coded on 260909, commented in full on 260909.
     *
     * @param cu {@code true} to mark the cursor unused, {@code false} to mark it live
     */
    public void setCu(boolean cu) {
        this.cu = cu;
    }
}
