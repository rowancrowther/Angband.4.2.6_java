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

package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

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
     *
     * @author Rowan Crowther
     */
    private boolean cu;
    /**
     * Cursor visible flag.
     *
     * @author Rowan Crowther
     */
    private boolean cv;
    /**
     * Cursor column.
     *
     * @author Rowan Crowther
     */
    private int cx;
    /**
     * Cursor row.
     *
     * @author Rowan Crowther
     */
    private int cy;
    /**
     * Secondary cursor column (e.g. for a multi-cell/"big" cursor).
     *
     * @author Rowan Crowther
     */
    private int cnx;
    /**
     * Secondary cursor row (e.g. for a multi-cell/"big" cursor).
     *
     * @author Rowan Crowther
     */
    private int cny;

    /**
     * Working copy of the main (character) layer.
     *
     * @author Rowan Crowther
     */
    private ArrayList<AngbandDisplayCharacter> a;
    /**
     * Displayed/"visible" copy of the main layer, diffed against {@link #a}.
     *
     * @author Rowan Crowther
     */
    private ArrayList<AngbandDisplayCharacter> va;
    /**
     * Working copy of the terrain layer.
     *
     * @author Rowan Crowther
     */
    private ArrayList<AngbandDisplayCharacter> ta;
    /**
     * Displayed/"visible" copy of the terrain layer, diffed against {@link #ta}.
     *
     * @author Rowan Crowther
     */
    private ArrayList<AngbandDisplayCharacter> vta;

    /**
     * Allocate the four content layers and fill them with blank white cells. The
     * grids are sized from {@code height}; one entry is added per row.
     *
     * @param width  the terminal width (currently unused in allocation)
     * @param height the terminal height, controlling how many cells are created
     * @author Rowan Crowther
     */
    public void init(int width, int height) {
        a = new ArrayList<>();
        va = new ArrayList<>();
        ta = new ArrayList<>();
        vta = new ArrayList<>();

        for (int index = 0; index < height; index++) {
            a.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
            va.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
            ta.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
            vta.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
        }
    }
}
