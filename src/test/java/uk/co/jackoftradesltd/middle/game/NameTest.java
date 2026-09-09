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

package uk.co.jackoftradesltd.middle.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Name}, the per-section building block C assembles into {@code name_sections}
 * in {@code finish_parse_names} ({@code init.c}).
 *
 * @author Rowan Crowther
 */
class NameTest {
    @Test
    @DisplayName("getSection returns the raw section number, unvalidated")
    void getSectionReturnsRawValue() {
        Name name = new Name(1, List.of("bran", "carc"));

        assertEquals(1, name.getSection());
    }

    @Test
    @DisplayName("getSection accepts section zero, matching C's parse_names_section")
    void getSectionAcceptsZero() {
        // C's parse_names_section (init.c) accepts section zero and files those words in the
        // name_sections[0] slot that randname_make never reads; Name itself carries no range
        // check, deferring rejection to RandnameType.fromIndex further down the pipeline.
        Name name = new Name(0, List.of("word"));

        assertEquals(0, name.getSection());
    }

    @Test
    @DisplayName("getSection accepts an out-of-range section, unvalidated")
    void getSectionAcceptsOutOfRange() {
        Name name = new Name(99, List.of("word"));

        assertEquals(99, name.getSection());
    }

    @Test
    @DisplayName("getWord preserves file order")
    void getWordPreservesFileOrder() {
        // C's finish_parse_names walks a linked list that parse_names_word built by prepending
        // each word, so C's own array comes out in reverse file order. Name does no such
        // reordering: whatever order the caller hands in is the order returned.
        List<String> words = List.of("bran", "carc", "dorn");
        Name name = new Name(1, words);

        assertEquals(List.of("bran", "carc", "dorn"), name.getWord());
    }

    @Test
    @DisplayName("getWord is empty for a section with no words")
    void getWordEmptyForNoWords() {
        Name name = new Name(1, Collections.emptyList());

        assertTrue(name.getWord().isEmpty());
    }

    @Test
    @DisplayName("getWord is a live, unmodifiable view of the constructor argument")
    void getWordIsLiveUnmodifiableView() {
        // The constructor keeps word by reference rather than copying it, matching C's own array
        // holding pointers into words it does not itself own; a later change to the backing list
        // is visible through getWord.
        List<String> words = new ArrayList<>(List.of("bran"));
        Name name = new Name(1, words);

        words.add("carc");

        assertEquals(List.of("bran", "carc"), name.getWord());
        assertThrows(UnsupportedOperationException.class, () -> name.getWord().add("dorn"));
    }
}
