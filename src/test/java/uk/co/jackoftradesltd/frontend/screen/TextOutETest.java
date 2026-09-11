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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TextOut#textOutE}, checked against C's {@code text_out_e} ({@code [C] src/z-textblock.c}).
 * Every expected value below is derived from reading that C function and {@code next_section}
 * directly, not from the Java port, so a test passing only proves the port matches the original -
 * not that the port is internally consistent.
 *
 * <p>Unlike {@link TextOut#textOutC}, which always makes exactly one {@code text_out_hook} call,
 * {@code text_out_e} calls it once per section {@code next_section} finds, so
 * {@link RecordingTextOutHook} records every call in order rather than just the last one.
 *
 * @author Rowan Crowther
 */
class TextOutETest {

    private final RecordingTextOutHook hook = new RecordingTextOutHook();
    private final TextOut textOut = new TextOut(null, hook);

    /**
     * Plain text with no {@code {}} tags at all is one untagged section - C's
     * {@code next_section} default fallthrough - written once in {@code COLOUR_WHITE}.
     */
    @Test
    void plainTextWithNoTagsIsOneWhiteCall() {
        textOut.textOutE("Hello, %s!", "world");

        assertEquals(List.of(new Call(ColourEnum.COLOUR_WHITE, "Hello, world!")), hook.calls);
    }

    /**
     * A well-formed {@code {tag}...{/}} at the very start of the buffer is one section, in
     * the tag's colour - regression coverage for the fix at {@code TextOut.java:95-96}: before
     * it, {@code start} began as {@code ""} instead of {@code buf}, so {@code textOutE} never
     * reached the loop body and made no hook calls at all.
     */
    @Test
    void aTagAtTheStartIsWrittenInThatColour() {
        textOut.textOutE("{red}ouch{/}");

        assertEquals(List.of(new Call(ColourEnum.COLOUR_RED, "ouch")), hook.calls);
    }

    /**
     * Plain text, a tagged section, then more plain text: C's loop produces three separate
     * {@code text_out_hook} calls in order - the untagged prefix and suffix in white, the
     * tagged middle in its own colour.
     */
    @Test
    void textAroundATagIsSplitIntoThreeCalls() {
        textOut.textOutE("abc {red}def{/} ghi");

        assertEquals(List.of(
                new Call(ColourEnum.COLOUR_WHITE, "abc "),
                new Call(ColourEnum.COLOUR_RED, "def"),
                new Call(ColourEnum.COLOUR_WHITE, " ghi")), hook.calls);
    }

    /**
     * Two adjacent tagged sections with nothing between them produce two hook calls, each in
     * its own colour - C's loop re-enters {@code next_section} from {@code end} until it runs
     * out of buffer, with no untagged call forced in between.
     */
    @Test
    void twoAdjacentTagsProduceTwoCalls() {
        textOut.textOutE("{red}A{/}{blue}B{/}");

        assertEquals(List.of(
                new Call(ColourEnum.COLOUR_RED, "A"),
                new Call(ColourEnum.COLOUR_BLUE, "B")), hook.calls);
    }

    /**
     * A tag name that doesn't name a real colour: C's {@code color_text_to_attr} returns
     * {@code -1}, and {@code text_out_e}'s {@code if (a == -1) a = COLOUR_WHITE;} falls back
     * to white rather than dropping the section.
     */
    @Test
    void anUnrecognisedTagNameFallsBackToWhite() {
        textOut.textOutE("{zzz}text{/}");

        assertEquals(List.of(new Call(ColourEnum.COLOUR_WHITE, "text")), hook.calls);
    }

    /**
     * A {@code {tag}} with no matching {@code {/}} anywhere in the rest of the buffer: C's
     * {@code close == NULL} branch in {@code next_section} treats the entire remainder - brace
     * and all - as one untagged lump, written in white.
     */
    @Test
    void anUnclosedTagIsOneWholeUntaggedCall() {
        textOut.textOutE("{red}text with no closing tag");

        assertEquals(List.of(new Call(ColourEnum.COLOUR_WHITE, "{red}text with no closing tag")), hook.calls);
    }

    /**
     * A formatted string well past the 1024-byte buffer is truncated to 1023 characters by
     * {@code vstrnfmt} before {@code next_section} ever sees it, the same bound
     * {@link TextOut#textOutC} is held to - and, with no braces in the input, still comes out
     * as a single white call.
     */
    @Test
    void aStringOverTheBufferLimitIsTruncatedBeforeSplitting() {
        String input = "A".repeat(2000);

        textOut.textOutE("%s", input);

        assertEquals(1, hook.calls.size());
        assertEquals(ColourEnum.COLOUR_WHITE, hook.calls.get(0).colour());
        assertEquals(1023, hook.calls.get(0).string().length());
        assertEquals(input.substring(0, 1023), hook.calls.get(0).string());
    }

    private record Call(ColourEnum colour, String string) {
    }

    private static class RecordingTextOutHook implements TextOutHook {
        private final List<Call> calls = new ArrayList<>();

        @Override
        public void output(ColourEnum attribute, String string) {
            calls.add(new Call(attribute, string));
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
