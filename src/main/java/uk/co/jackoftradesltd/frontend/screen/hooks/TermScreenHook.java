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
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

/**
 * The live {@link TermTextHook} implementation, painting directly onto a {@link Screen}'s
 * root {@link uk.co.jackoftradesltd.frontend.screen.grid.Region}. This is the Java
 * counterpart to a concrete C {@code struct term} hook table
 * ({@code [C] src/ui-term.h}) - specifically the {@code text_hook} and {@code wipe_hook}
 * function pointers, the only two low-level driver primitives the real hook table exposes.
 * {@link uk.co.jackoftradesltd.frontend.screen.Term} never paints the screen itself; every
 * write reaches it by calling through its own {@code outputHook} field, which
 * {@link uk.co.jackoftradesltd.frontend.screen.Term#termInit} constructs as an instance of
 * this class.
 *
 * <p>Class TermScreenHook coded on 260910, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class TermScreenHook implements TermTextHook {
    /**
     * The screen this hook paints and blanks through, via its
     * {@link Screen#root()} region. Supplied once at construction and never reassigned.
     *
     * <p>Field screen coded on 260910, commented in full on 260916.
     */
    private Screen screen;

    /**
     * Wraps a {@link Screen} as the destination for every {@link #putStr} and
     * {@link #erase} call this hook receives. The screen is not copied - this hook trusts
     * whoever constructs it (in practice, {@link uk.co.jackoftradesltd.frontend.screen.Term#termInit})
     * to hand over the one {@link Screen} this {@link uk.co.jackoftradesltd.frontend.screen.Term}
     * instance owns.
     *
     * <p>Constructor TermScreenHook coded on 260910, commented in full on 260916.
     *
     * @param screen the screen this hook paints and blanks
     */
    public TermScreenHook(Screen screen) {
        this.screen = screen;
    }

    /**
     * Draw up to {@code n} characters of {@code string} at ({@code x}, {@code y}) on the
     * root {@link Screen} region, the boundary this hook implements for
     * {@link uk.co.jackoftradesltd.frontend.screen.Term#addstr}, the Java port's
     * counterpart to C's {@code Term_queue_chars} call inside {@code Term_addstr}
     * ({@code [C] src/ui-term.c}). A negative {@code n} means "no limit" - the string is
     * drawn as given, relying on
     * {@link uk.co.jackoftradesltd.frontend.screen.grid.Region#put}'s own right-edge
     * clipping - while {@code n >= 0} truncates to exactly the first {@code n} characters
     * first, so an explicit {@code n} of {@code 0} draws nothing rather than the whole
     * string.
     *
     * <p>Function putStr coded on 260910, commented in full on 260910.
     *
     * @param x      the column to draw at
     * @param y      the row to draw at
     * @param n      the maximum number of characters to draw; negative means "as many as
     *               fit"
     * @param colour the colour to draw the string in
     * @param string the string to draw
     */
    @Override
    public void putStr(int x, int y, int n, ColourEnum colour, String string) {
        if (n >= 0)
            string = string.substring(0, n);
        screen.root().put(y, x, string, colour);
    }

    /**
     * Blank {@code n} columns of a row, the boundary this hook implements for
     * {@link uk.co.jackoftradesltd.frontend.screen.Term#clearFrom}, standing in for C's
     * {@code Term_erase} call inside {@code clear_from} ({@code [C] src/ui-input.c}). The
     * {@code 255} that {@code clearFrom} passes as {@code n} is, as in C, deliberately larger
     * than any real row width -
     * {@link uk.co.jackoftradesltd.frontend.screen.grid.Region#erase} clips it to the row's
     * remaining columns rather than needing the exact count.
     *
     * <p>This method's own parameter order is {@code (col, row, n)}, matching the rest of
     * {@link TermTextHook}, but {@link uk.co.jackoftradesltd.frontend.screen.grid.Region#erase}
     * takes {@code (row, col, n)} - the call below swaps them rather than passing straight
     * through.
     *
     * <p>Function erase coded on 260910, commented in full on 260910.
     *
     * @param col the column the blanked run starts at
     * @param row the row to blank
     * @param n   how many columns to blank
     */
    @Override
    public void erase(int col, int row, int n) {
        screen.root().erase(row, col, n);
    }
}
