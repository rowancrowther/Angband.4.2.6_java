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

package uk.co.jackoftradesltd.frontend.screen.hooks;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;

/**
 * The Java counterpart to a C {@code struct term}'s hook table
 * ({@code [C] src/ui-term.h}), narrowed to the two low-level driver primitives that table
 * actually exposes: {@code text_hook} (this interface's {@link #putStr}) and
 * {@code wipe_hook} ({@link #erase}). C's own table also carries {@code init_hook},
 * {@code nuke_hook}, {@code xtra_hook}, {@code curs_hook}, {@code bigcurs_hook},
 * {@code pict_hook}, {@code view_map_hook} and {@code dblh_hook}, none of which are part of
 * this interface's boundary - only the two primitives text-painting logic actually calls
 * through are here. Higher-level C functions built from those two primitives, such as
 * {@code c_prt} ({@code [C] src/ui-output.c}), are not hooks in C either - they are ported
 * as ordinary composition (see
 * {@link uk.co.jackoftradesltd.frontend.screen.Term#cPrt}), not as a third method on this
 * interface.
 *
 * <p>{@link TermScreenHook} is the only implementation, and
 * {@link uk.co.jackoftradesltd.frontend.screen.Term} is the only caller, reaching this
 * interface through its own {@code outputHook} field.
 *
 * <p>Interface TermTextHook coded on 260910, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public interface TermTextHook {
    /**
     * Draw up to {@code n} characters of {@code string} at ({@code x}, {@code y}), the
     * counterpart to C's {@code text_hook} function pointer, invoked via
     * {@code Term_queue_chars} ({@code [C] src/ui-term.c}). A negative {@code n} means "no
     * limit" - implementations draw the string as given, relying on their own painting
     * surface's right-edge clipping - while {@code n >= 0} truncates to exactly the first
     * {@code n} characters.
     *
     * <p>Method putStr coded on 260910, commented in full on 260916.
     *
     * @param x      the column to draw at
     * @param y      the row to draw at
     * @param n      the maximum number of characters to draw; negative means "as many as
     *               fit"
     * @param colour the colour to draw the string in
     * @param string the string to draw
     */
    void putStr(int x, int y, int n, ColourEnum colour, String string);

    /**
     * Blank {@code n} columns of a row starting at {@code (col, row)}, the counterpart to
     * C's {@code wipe_hook} function pointer, invoked via {@code Term_erase}
     * ({@code [C] src/ui-term.c}). Callers are free to pass an {@code n} larger than any
     * real row width - as C's own callers do - and rely on the implementation to clip it to
     * the row's remaining columns rather than needing the exact count.
     *
     * <p>Method erase coded on 260910, commented in full on 260916.
     *
     * @param col the column the blanked run starts at
     * @param row the row to blank
     * @param n   how many columns to blank
     */
    void erase(int col, int row, int n);
}
