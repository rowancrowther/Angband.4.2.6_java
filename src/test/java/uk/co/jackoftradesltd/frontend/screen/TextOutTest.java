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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.frontend.screen.hooks.TextOutHook;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TextOut#textOut} and {@link TextOut#textOutC}, checked against C's {@code text_out}
 * and {@code text_out_c} ({@code [C] src/z-textblock.c}): both format into a fixed
 * {@code char buf[1024]} via {@code vstrnfmt(buf, sizeof(buf), fmt, vp)}, which leaves output
 * shorter than the buffer untouched and truncates only text that would overrun it, to 1023
 * characters ({@code max - 1} per {@code [C] src/z-form.c}'s own doc comment for
 * {@code vstrnfmt}), before making a single {@code text_out_hook} call - {@code text_out}
 * always in {@code COLOUR_WHITE}, {@code text_out_c} in whichever colour it is given.
 *
 * @author Rowan Crowther
 */
class TextOutTest {

    private final RecordingTextOutHook hook = new RecordingTextOutHook();
    private final TextOut textOut = new TextOut(null, hook);

    /**
     * An ordinary formatted string, well under the 1024-character buffer, is written
     * unchanged and in exactly one hook call - matching C leaving a short
     * {@code vstrnfmt} result untouched.
     */
    @Test
    void anOrdinaryStringIsWrittenUnchanged() {
        textOut.textOutC(ColourEnum.COLOUR_RED, "Hello, %s!", "world");

        assertEquals(1, hook.callCount);
        assertEquals(ColourEnum.COLOUR_RED, hook.colour);
        assertEquals("Hello, world!", hook.string);
    }

    /**
     * {@code text_out} is {@code text_out_c} fixed to {@code COLOUR_WHITE} - the same
     * formatted write, with no colour argument.
     */
    @Test
    void textOutWritesInWhite() {
        textOut.textOut("Hello, %s!", "world");

        assertEquals(ColourEnum.COLOUR_WHITE, hook.colour);
        assertEquals("Hello, world!", hook.string);
    }

    /**
     * A plain string with no format specifiers and no arguments is written as-is -
     * {@code text_out}/{@code text_out_c} are routinely called with a bare message and no
     * varargs at all.
     */
    @Test
    void aPlainStringWithNoArgumentsIsWrittenAsIs() {
        textOut.textOutC(ColourEnum.COLOUR_BLUE, "You feel a bit better.");

        assertEquals("You feel a bit better.", hook.string);
    }

    /**
     * A formatted string of exactly 1023 characters - {@code max - 1} for the 1024-byte
     * buffer - is the longest string that still passes through whole.
     */
    @Test
    void aStringOfMaxMinusOneCharactersIsUnchanged() {
        String longest = "A".repeat(1023);

        textOut.textOutC(ColourEnum.COLOUR_WHITE, "%s", longest);

        assertEquals(1023, hook.string.length());
        assertEquals(longest, hook.string);
    }

    /**
     * A formatted string of exactly 1024 characters is one over the limit and loses its
     * last character - C's {@code vstrnfmt} keeps {@code min(max - 1, strlen(str))} bytes.
     */
    @Test
    void aStringOfMaxCharactersLosesItsLastCharacter() {
        String input = "A".repeat(1024);

        textOut.textOutC(ColourEnum.COLOUR_WHITE, "%s", input);

        assertEquals(1023, hook.string.length());
        assertEquals(input.substring(0, 1023), hook.string);
    }

    /**
     * A formatted string well past the limit is truncated to the same 1023 characters,
     * not merely clipped by however much it overran.
     */
    @Test
    void aStringWellOverTheLimitIsTruncatedToMaxMinusOne() {
        String input = "B".repeat(5000);

        textOut.textOutC(ColourEnum.COLOUR_WHITE, "%s", input);

        assertEquals(1023, hook.string.length());
        assertEquals(input.substring(0, 1023), hook.string);
    }

    private static class RecordingTextOutHook implements TextOutHook {
        private ColourEnum colour;
        private String string;
        private int callCount = 0;

        @Override
        public void output(ColourEnum attribute, String string) {
            this.colour = attribute;
            this.string = string;
            callCount++;
        }

        @Override
        public void out(ColourEnum colour, String toFormat, Object... objects) {
            throw new AssertionError("TextOut never calls TextOutHook.out(ColourEnum, String, Object...)");
        }

        @Override
        public void out(String toFormat, Object... objects) {
            throw new AssertionError("TextOut never calls TextOutHook.out(String, Object...)");
        }
    }
}
