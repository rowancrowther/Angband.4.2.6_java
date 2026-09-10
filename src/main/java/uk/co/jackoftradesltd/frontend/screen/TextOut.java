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
import uk.co.jackoftradesltd.frontend.screen.hooks.TextOutHook;
import uk.co.jackoftradesltd.channel.utils.StringUtils;

/**
 * Formats and colours text before handing it to a {@link TextOutHook} sink, the Java port of
 * the C original's {@code text_out} and {@code text_out_c} ({@code [C] src/z-textblock.c}). C
 * dispatches through a single raw function pointer, {@code text_out_hook}, that the front end
 * points at either a screen writer or {@code text_out_to_file}; this wraps that pointer as an
 * injected {@link TextOutHook} instead, so the destination is fixed per {@code TextOut} instance
 * rather than swapped globally. {@code text_out_to_file}'s own line-wrapping is the concern of
 * whichever {@link TextOutHook} writes to a file, not of this class.
 *
 * @author Rowan Crowther
 */
public class TextOut {
    /**
     * The terminal this instance is attached to. Not yet read by {@link #textOut} or
     * {@link #textOutC} - both write only through {@link #textOutHook}.
     */
    private Term term;

    /**
     * The sink formatted, coloured text is written to, the Java port of C's
     * {@code text_out_hook} function pointer ({@code [C] src/z-textblock.c}).
     */
    private TextOutHook textOutHook;

    /**
     * @param term        the terminal this output is attached to
     * @param textOutHook the sink to write formatted, coloured text to
     */
    public TextOut(Term term, TextOutHook textOutHook) {
        this.term = term;
        this.textOutHook = textOutHook;
    }

    /**
     * Format {@code fmt} with {@code args} and write it in {@link ColourEnum#COLOUR_WHITE}, the
     * Java port of the C original's {@code text_out} ({@code [C] src/z-textblock.c}). Delegates
     * entirely to {@link #textOutC}, matching {@code text_out}'s own call to
     * {@code text_out_hook(COLOUR_WHITE, buf)}.
     *
     * <p>Function textOut coded on 260910, commented in full on 260910.
     *
     * @param fmt  the format string
     * @param args the objects to format {@code fmt} with
     */
    public void textOut(String fmt, Object... args) {
        textOutC(ColourEnum.COLOUR_WHITE, fmt, args);
    }

    /**
     * Format {@code fmt} with {@code args} and write it in {@code colour} to
     * {@link #textOutHook}, the Java port of the C original's {@code text_out_c}
     * ({@code [C] src/z-textblock.c}). C formats into a fixed {@code char buf[1024]} via
     * {@code vstrnfmt(buf, sizeof(buf), fmt, vp)}, which leaves output shorter than the buffer
     * untouched and truncates only text that would overrun it, to 1023 characters; this calls
     * {@link StringUtils#vstrnfmt(int, String, Object...)} with the same 1024 bound to match.
     *
     * <p>Function textOutC coded on 260910, commented in full on 260910.
     *
     * @param colour the colour to write {@code fmt} in
     * @param fmt    the format string
     * @param args   the objects to format {@code fmt} with
     */
    public void textOutC(ColourEnum colour, String fmt, Object... args) {
        String buf = StringUtils.vstrnfmt(1024, fmt, args);
        textOutHook.output(colour, buf);
    }
}
