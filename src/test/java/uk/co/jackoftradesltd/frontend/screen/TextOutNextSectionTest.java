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

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link TextOut#nextSection}, checked against C's {@code next_section}
 * ({@code [C] src/z-textblock.c}). Every expected value below is derived from reading that C
 * function directly, not from the Java port, so a test passing only proves the port matches the
 * original - not that the port is internally consistent.
 *
 * <p>{@code nextSection} returns a private {@code TextOut.SectionDetails} record, so its four
 * components ({@code found}/{@code text}/{@code tag}/{@code next}) are read back through
 * reflection rather than named directly.
 *
 * @author Rowan Crowther
 */
class TextOutNextSectionTest {

    private final TextOut textOut = new TextOut(null, null);

    /**
     * An empty {@code source} substring returns {@code found = false} - C's
     * {@code if (*text[0] == '\0') return false;} - and nothing else is inspected.
     */
    @Test
    void anEmptySourceIsNotFound() {
        assertFalse(found(""));
    }

    /**
     * Plain text with no {@code {} anywhere falls through to C's final default: the whole
     * string is the section, with no tag and an empty (terminal) {@code next}.
     */
    @Test
    void plainTextWithNoTagsIsReturnedWhole() {
        Section s = section("hello world");

        assertTrue(s.found);
        assertEquals("hello world", s.text);
        assertEquals("", s.tag);
        assertEquals("", s.next);
    }

    /**
     * A {@code {} with nothing after it in the string is C's {@code *s == '\0'} case - the
     * scan cursor never finds a character to skip or a closing brace, so the whole string is
     * handed back untagged.
     */
    @Test
    void aTrailingOpenBraceWithNothingAfterItIsReturnedWhole() {
        Section s = section("abc{");

        assertTrue(s.found);
        assertEquals("abc{", s.text);
        assertEquals("", s.tag);
        assertEquals("", s.next);
    }

    /**
     * The same {@code *s == '\0'} case, but reached after skipping some alphabetic
     * characters first - the tag's letters run out before a closing {@code }} or a
     * non-letter is found.
     */
    @Test
    void anOpenBraceWhoseTagRunsOffTheEndIsReturnedWhole() {
        Section s = section("{red");

        assertTrue(s.found);
        assertEquals("{red", s.text);
        assertEquals("", s.tag);
        assertEquals("", s.next);
    }

    /**
     * A well-formed {@code {tag}...{/}} at the very start of the fragment: C's
     * {@code next == *text} branch clips the text to end at the closing tag, fills
     * {@code tag} with the name between the braces, and points {@code end} just past
     * the {@code {/}}.
     */
    @Test
    void aTagAtTheStartClipsToTheClosingTag() {
        Section s = section("{red}Hello{/} world");

        assertTrue(s.found);
        assertEquals("Hello", s.text);
        assertEquals("red", s.tag);
        assertEquals(" world", s.next);
    }

    /**
     * A tag name may contain spaces as well as letters - C's scan loop skips
     * {@code isalpha} or {@code isspace} characters alike.
     */
    @Test
    void aMultiWordTagNameIsCapturedInFull() {
        Section s = section("{light blue}text{/}tail");

        assertTrue(s.found);
        assertEquals("text", s.text);
        assertEquals("light blue", s.tag);
        assertEquals("tail", s.next);
    }

    /**
     * A {@code {tag}} found after some plain text: C's {@code else} branch (tag not at the
     * start) returns only the text before the brace, with no tag, and {@code end} pointing
     * at the {@code {} itself so the next call re-parses the tag from there.
     */
    @Test
    void plainTextBeforeATagReturnsOnlyThatChunk() {
        Section s = section("Hi {red}there{/} you");

        assertTrue(s.found);
        assertEquals("Hi ", s.text);
        assertEquals("", s.tag);
        assertEquals("{red}there{/} you", s.next);
    }

    /**
     * A {@code {tag}} with no matching {@code {/}} anywhere in the rest of the string: C's
     * {@code close == NULL} branch treats the entire remaining text - brace and all - as one
     * untagged lump.
     */
    @Test
    void aTagWithNoClosingTagIsOneWholeChunk() {
        Section s = section("{red}Hello world");

        assertTrue(s.found);
        assertEquals("{red}Hello world", s.text);
        assertEquals("", s.tag);
        assertEquals("", s.next);
    }

    /**
     * A {@code {} followed by something other than a letter or space before any closing
     * {@code }} is an invalid tag - C skips past it and keeps scanning for another
     * {@code {}. With none left, this falls through to the same default as plain text.
     */
    @Test
    void anInvalidTagBodyIsSkippedAndTreatedAsPlainText() {
        Section s = section("{1red} rest");

        assertTrue(s.found);
        assertEquals("{1red} rest", s.text);
        assertEquals("", s.tag);
        assertEquals("", s.next);
    }

    /**
     * An invalid tag followed later by a valid one: C's invalid-tag branch resumes scanning
     * from just past the discarded {@code {} and finds the next one, then applies the
     * ordinary tag-not-at-the-start rule to it - the invalid tag's braces are just more
     * plain text.
     */
    @Test
    void anInvalidTagIsSkippedAndScanningContinuesToTheNextBrace() {
        Section s = section("{1x}{ok}there{/}rest");

        assertTrue(s.found);
        assertEquals("{1x}", s.text);
        assertEquals("", s.tag);
        assertEquals("{ok}there{/}rest", s.next);
    }

    private boolean found(String source) {
        return invoke(source).found;
    }

    private Section section(String source) {
        return invoke(source);
    }

    private Section invoke(String source) {
        try {
            Method nextSection = TextOut.class.getDeclaredMethod("nextSection", String.class, int.class, String.class);
            nextSection.setAccessible(true);
            Object details = nextSection.invoke(textOut, source, 0, "");

            return new Section(
                    (boolean) component(details, "found"),
                    (String) component(details, "text"),
                    (String) component(details, "tag"),
                    (String) component(details, "next"));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private Object component(Object record, String name) throws ReflectiveOperationException {
        Method accessor = record.getClass().getDeclaredMethod(name);
        accessor.setAccessible(true);
        return accessor.invoke(record);
    }

    private record Section(boolean found, String text, String tag, String next) {
    }
}
