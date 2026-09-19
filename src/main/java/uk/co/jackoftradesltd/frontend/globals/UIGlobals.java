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
 * Static holder for the front end's globally-shared UI state, the Java port of the C original's
 * module-scope {@code text_out_indent} global in {@code z-textblock.c}
 * ({@code [C] src/z-textblock.h:57-61}). C's neighbouring {@code text_out_wrap} and
 * {@code text_out_pad} globals (and the {@code text_out_hook}/{@code text_out_file} pointers)
 * have no counterpart here yet.
 *
 * <p>This class previously also wrapped C's file-scoped, five-entry {@code stat_names}
 * abbreviation array ({@code [C] src/ui-entry.c:1603-1622}) via a {@code getStatName(int)}
 * accessor. That was removed on 260919 as a duplicate: the same fixed {@code STR}/{@code INT}/
 * {@code WIS}/{@code DEX}/{@code CON} order already lives as data in
 * {@link uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate}'s
 * {@code statString} snapshot field, seeded from the same C order, and is exercised there and in
 * {@link uk.co.jackoftradesltd.middle.enums.Stats#getStatString()} - this class had no test or
 * caller of its own that the data class's coverage didn't already subsume.
 *
 * <p>Class UIGlobals coded before 260911, commented in full on 260919.
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
