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
package uk.co.jackoftradesltd.middle.strings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link MessageTag#getTag} against C's {@code msg_tag_lookup}, which compares with
 * {@code strncmp} over the length of each name in the order {@code name}, {@code kind}, {@code s},
 * {@code is}. The caller passes the letters between the braces, so there is never a brace in the
 * argument.
 */
@DisplayName("MessageTag.getTag")
class MessageTagTest {

    @Test
    @DisplayName("the four tags match exactly")
    void exactTags() {
        assertAll(
                () -> assertEquals(MessageTag.MSG_TAG_NAME, MessageTag.getTag("name")),
                () -> assertEquals(MessageTag.MSG_TAG_KIND, MessageTag.getTag("kind")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB, MessageTag.getTag("s")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB_IS, MessageTag.getTag("is")));
    }

    @Test
    @DisplayName("a longer tag matches on its opening letters")
    void prefixesMatch() {
        assertAll(
                () -> assertEquals(MessageTag.MSG_TAG_NAME, MessageTag.getTag("names")),
                () -> assertEquals(MessageTag.MSG_TAG_KIND, MessageTag.getTag("kindly")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB, MessageTag.getTag("sx")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB, MessageTag.getTag("size")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB_IS, MessageTag.getTag("isn")));
    }

    @Test
    @DisplayName("a tag that stops short of its name matches nothing")
    void shortTagsMatchNothing() {
        assertAll(
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("nam")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("kin")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("i")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("")));
    }

    @Test
    @DisplayName("an unrelated tag matches nothing")
    void unknownTag() {
        assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("zzz"));
    }

    @Test
    @DisplayName("matching is case sensitive, as strncmp is")
    void caseSensitive() {
        assertAll(
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("Name")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("IS")));
    }

    /**
     * C tests the names in a fixed order, and a tag that begins with another name's letters
     * after its own first letter is decided by the first letter alone: {@code sname} begins with
     * {@code s}, so it is a verb ending, not a name.
     */
    @Test
    @DisplayName("only the opening letters decide the tag")
    void openingLettersDecide() {
        assertAll(
                () -> assertEquals(MessageTag.MSG_TAG_VERB, MessageTag.getTag("sname")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB, MessageTag.getTag("skind")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB, MessageTag.getTag("sis")),
                () -> assertEquals(MessageTag.MSG_TAG_NAME, MessageTag.getTag("namekind")),
                () -> assertEquals(MessageTag.MSG_TAG_KIND, MessageTag.getTag("kinds")),
                () -> assertEquals(MessageTag.MSG_TAG_VERB_IS, MessageTag.getTag("isis")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("n")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("k")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("ki")),
                () -> assertEquals(MessageTag.MSG_TAG_NONE, MessageTag.getTag("nname")));
    }

    /**
     * The sizes are the Java port's own, the length of each tag with its closing brace:
     * {@code name}, {@code kind}, {@code s}, {@code is}, and 1 for the unrecognised tag.
     */
    @Test
    @DisplayName("each tag's size is its braced length")
    void sizes() {
        assertAll(
                () -> assertEquals("name}".length(), MessageTag.MSG_TAG_NAME.getSize()),
                () -> assertEquals("kind}".length(), MessageTag.MSG_TAG_KIND.getSize()),
                () -> assertEquals("s}".length(), MessageTag.MSG_TAG_VERB.getSize()),
                () -> assertEquals("is}".length(), MessageTag.MSG_TAG_VERB_IS.getSize()),
                () -> assertEquals(1, MessageTag.MSG_TAG_NONE.getSize()));
    }

    /**
     * {@code s} is tested before {@code is}, but {@code is...} does not begin with {@code s}, so the
     * order only matters for tags beginning with {@code s}.
     */
    @Test
    @DisplayName("s is tested before is, and is... is not an s tag")
    void orderDoesNotStealIs() {
        assertEquals(MessageTag.MSG_TAG_VERB_IS, MessageTag.getTag("is"));
    }
}
