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

package uk.co.jackoftradesltd.frontend.globals;

/**
 * Static holder for the front end's globally-shared text-output state, the Java port of the C
 * original's module-scope globals in {@code z-textblock.c} ({@code [C] src/z-textblock.h:57-61}).
 * Currently wraps {@code text_out_indent} only; C's neighbouring {@code text_out_wrap} and
 * {@code text_out_pad} globals (and the {@code text_out_hook}/{@code text_out_file} pointers)
 * have no counterpart here yet.
 *
 * <p>Class UIGlobals coded before 260911, commented in full on 260911.
 *
 * @author Rowan Crowther
 */
public class UIGlobals {
    /**
     * The current indentation for wrapped text, the Java port of the C original's
     * {@code text_out_indent} ({@code [C] src/z-textblock.c:382}). Read at the start of each
     * output line by whichever hook writes wrapped text to a file, to pad that line's indent; C
     * default-initialises it to {@code 0}, which this mirrors with an explicit static
     * initializer.
     *
     * <p>Field textOutIndent coded before 260911, commented in full on 260911.
     */
    private static int textOutIndent;

    static {
        textOutIndent = 0;
    }

    /**
     * Returns the current text-output indentation, the Java port of the C original's direct
     * reads of {@code text_out_indent} ({@code [C] src/z-textblock.c:446, 464}).
     *
     * <p>Function getTextOutIndent coded before 260911, commented in full on 260911.
     *
     * @return the number of columns to indent wrapped text by
     */
    public static int getTextOutIndent() {
        return textOutIndent;
    }

    /**
     * Sets the text-output indentation, the Java port of the C original's direct assignments to
     * {@code text_out_indent} ({@code [C] src/z-textblock.c}) made by call sites such as
     * {@code ui-birth.c}, {@code ui-store.c} and {@code ui-target.c} around blocks of
     * {@code text_out} calls, each later resetting it back to {@code 0}.
     *
     * <p>Function setTextOutIndent coded before 260911, commented in full on 260911.
     *
     * @param textOutIndent the number of columns to indent wrapped text by
     */
    public static void setTextOutIndent(int textOutIndent) {
        UIGlobals.textOutIndent = textOutIndent;
    }
}
