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
 * {@code term_win} struct ({@code [C] src/ui-term.h}). Where C stores each cell as
 * a separate attribute ({@code int}) and character ({@code wchar_t}) entry, this
 * port merges the pair into a single {@link AngbandDisplayCharacter} (glyph +
 * colour) per cell, and keeps a main layer ({@link #a}/{@link #va}) and a terrain
 * layer ({@link #ta}/{@link #vta}).
 *
 * <p>In C, a {@code term_win}'s {@code a}/{@code c} arrays are not a separate copy
 * of {@code va}/{@code vc}: {@code a[y]} is a row-view pointer into that same flat
 * buffer ({@code s->a[y] = s->va + w * y}, {@code term_win_init}, {@code [C]
 * src/ui-term.c}), so a single {@code term_win} holds exactly one copy of each
 * layer's content. Java has no pointer aliasing, so this port keeps {@link #a}
 * and {@link #va} (and {@link #ta} and {@link #vta}) as independent arrays that
 * {@link #init} fills identically - there is no working/displayed pair to diff
 * within a single {@link TermWin}. The working-versus-displayed diff lives one
 * level up instead, between two whole {@link TermWin} instances - see
 * {@code Term_fresh} ({@code [C] src/ui-term.c}), which compares {@code
 * Term->old} against {@code Term->scr} cell by cell and cursor field by cursor
 * field to decide what needs redrawing.
 *
 * @author Rowan Crowther
 */
public class TermWin {
    /**
     * Cursor "unused"/off-screen flag - an {@code int} used as a boolean (0/1) in
     * C, {@code true} once the cursor has been pushed past the row's last column
     * and disabled. The port of C's {@code term_win.cu} ({@code [C]
     * src/ui-term.h}).
     */
    private boolean cu;
    /**
     * Cursor visible flag. The port of C's {@code term_win.cv} ({@code [C]
     * src/ui-term.h}).
     */
    private boolean cv;
    /**
     * Cursor column. The port of C's {@code term_win.cx} ({@code [C]
     * src/ui-term.h}).
     */
    private int cx;
    /**
     * Cursor row. The port of C's {@code term_win.cy} ({@code [C]
     * src/ui-term.h}).
     */
    private int cy;
    /**
     * Cursor width, in cells - how many columns wide the cursor block spans (1
     * for an ordinary cursor, the tile width for a multi-cell "big" cursor). The
     * port of C's {@code term_win.cnx} ({@code [C] src/ui-term.h}); {@code
     * Term_fresh} ({@code [C] src/ui-term.c}) uses it to find the rightmost
     * column ({@code cx + cnx - 1}) still needing redrawing after the cursor
     * moves.
     */
    private int cnx;
    /**
     * Cursor height, in cells - how many rows tall the cursor block spans (1 for
     * an ordinary cursor, the tile height for a multi-cell "big" cursor). The
     * port of C's {@code term_win.cny} ({@code [C] src/ui-term.h}); used the same
     * way as {@link #cnx}, but for the row extent.
     */
    private int cny;

    /**
     * The main (character) layer's cell grid, indexed {@code [row][col]}. The
     * port of C's {@code term_win.a}/{@code .c} pair ({@code [C]
     * src/ui-term.h}); see the class Javadoc for how the row-pointer aliasing in
     * C differs from this port's storage.
     */
    private AngbandDisplayCharacter[][] a;
    /**
     * The main layer's backing storage, indexed {@code [row][col]}. The port of
     * C's {@code term_win.va}/{@code .vc} pair ({@code [C] src/ui-term.h}) - the
     * flat buffer {@code a}/{@code c} point into row-by-row in the original; see
     * the class Javadoc.
     */
    private AngbandDisplayCharacter[][] va;
    /**
     * The terrain layer's cell grid, indexed {@code [row][col]}. The port of C's
     * {@code term_win.ta}/{@code .tc} pair ({@code [C] src/ui-term.h}); see the
     * class Javadoc for how the row-pointer aliasing in C differs from this
     * port's storage.
     */
    private AngbandDisplayCharacter[][] ta;
    /**
     * The terrain layer's backing storage, indexed {@code [row][col]}. The port
     * of C's {@code term_win.vta}/{@code .vtc} pair ({@code [C] src/ui-term.h}) -
     * the flat buffer {@code ta}/{@code tc} point into row-by-row in the
     * original; see the class Javadoc.
     */
    private AngbandDisplayCharacter[][] vta;

    /**
     * Allocate the main and terrain layers - {@link #a}/{@link #va} and
     * {@link #ta}/{@link #vta} - as {@code height} rows of {@code width} columns
     * each, and fill every cell with a blank, uncoloured placeholder: {@code
     * '\0'} at {@link ColourEnum#COLOUR_DARK}. The port of C's {@code
     * term_win_init} ({@code [C] src/ui-term.c}), which {@code mem_zalloc}s
     * {@code va}/{@code vc}/{@code vta}/{@code vtc} as flat {@code height *
     * width} buffers and points {@code a}/{@code ta}'s row entries into them -
     * zeroed memory, which is attr {@code 0} ({@code COLOUR_DARK}) and character
     * {@code 0} ({@code '\0'}), the same blank cell this method builds
     * explicitly, since Java arrays don't come pre-filled with meaningful domain
     * objects the way C's zalloc'd {@code int}/{@code wchar_t} arrays do.
     *
     * <p>Function init coded on 260916, commented in full on 260916.
     *
     * @param width  the terminal width, in columns; the grids' second dimension
     * @param height the terminal height, in rows; the grids' first dimension
     */
    public void init(int width, int height) {
        a = new AngbandDisplayCharacter[height][width];
        va = new AngbandDisplayCharacter[height][width];
        ta = new AngbandDisplayCharacter[height][width];
        vta = new AngbandDisplayCharacter[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                a[row][col] = new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK);
                va[row][col] = new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK);
                ta[row][col] = new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK);
                vta[row][col] = new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK);
            }
        }
    }

    /**
     * Get the cursor's "unused"/off-screen flag. The port of reading C's {@code scr->cu}
     * directly ({@code [C] src/ui-term.h}); as with {@link #setCu}, the original has no
     * dedicated getter function.
     *
     * <p>Function getCu coded on 260910, commented in full on 260916.
     *
     * @return {@code true} if the cursor is marked unused, {@code false} if it is live
     */
    public boolean getCu() {
        return cu;
    }

    /**
     * Set the cursor's "unused"/off-screen flag. The port of writing C's {@code scr->cu}
     * directly ({@code [C] src/ui-term.h}), where it is an {@code int} used as a boolean
     * (0/1); as with {@link #setCx}, the original has no dedicated setter function.
     *
     * <p>Function setCu coded on 260909, commented in full on 260916.
     *
     * @param cu {@code true} to mark the cursor unused, {@code false} to mark it live
     */
    public void setCu(boolean cu) {
        this.cu = cu;
    }

    /**
     * Get the cursor's column. The port of reading C's {@code scr->cx} directly
     * ({@code [C] src/ui-term.h}); as with {@link #setCx}, the original has no dedicated
     * getter function.
     *
     * <p>Function getCx coded on 260910, commented in full on 260916.
     *
     * @return the current cursor column
     */
    public int getCx() {
        return cx;
    }

    /**
     * Set the cursor's column. The port of writing C's {@code scr->cx} directly
     * ({@code [C] src/ui-term.h}); there is no dedicated C setter, since the original
     * assigns the struct field in place wherever it moves the cursor - see
     * {@code Term_gotoxy} ({@code [C] src/ui-term.c}) for the caller this exists for.
     *
     * <p>Function setCx coded on 260909, commented in full on 260916.
     *
     * @param x the new cursor column
     */
    public void setCx(int x) {
        this.cx = x;
    }

    /**
     * Get the cursor's row. The port of reading C's {@code scr->cy} directly
     * ({@code [C] src/ui-term.h}); as with {@link #setCy}, the original has no dedicated
     * getter function.
     *
     * <p>Function getCy coded on 260910, commented in full on 260916.
     *
     * @return the current cursor row
     */
    public int getCy() {
        return cy;
    }

    /**
     * Set the cursor's row. The port of writing C's {@code scr->cy} directly
     * ({@code [C] src/ui-term.h}); as with {@link #setCx}, the original has no dedicated
     * setter function.
     *
     * <p>Function setCy coded on 260909, commented in full on 260916.
     *
     * @param y the new cursor row
     */
    public void setCy(int y) {
        this.cy = y;
    }
}
