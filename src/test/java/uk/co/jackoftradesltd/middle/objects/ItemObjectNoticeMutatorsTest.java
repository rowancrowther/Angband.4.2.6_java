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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#setNoticeOn} and {@link ItemObject#setNoticeOff}, the ports of C's
 * {@code obj->notice |= flag} and {@code obj->notice &= ~flag}.
 *
 * <p>The expected values come from the bit-operator semantics of C: OR-ing a bit in is idempotent
 * and leaves every other bit alone, and AND-ing its complement is the same for clearing. The
 * boolean each method returns has no C counterpart; it is pinned here as "did the set change".
 *
 * <p>Test class ItemObjectNoticeMutatorsTest written on 261003.
 */
class ItemObjectNoticeMutatorsTest {

    private ItemObject item;

    @BeforeEach
    void setUp() {
        item = new ItemObject();
    }

    @Test
    @DisplayName("setNoticeOn raises the flag and reports that the set changed")
    void onRaisesFlag() {
        assertTrue(item.setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED));
        assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED));
    }

    @Test
    @DisplayName("setNoticeOn on a flag already up changes nothing and reports false, as |= is idempotent")
    void onTwiceIsIdempotent() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN);
        assertFalse(item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN));
        assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_WORN));
    }

    @Test
    @DisplayName("setNoticeOn leaves the other three flags alone")
    void onLeavesOthers() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IGNORE);
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_IMAGINED));
    }

    @Test
    @DisplayName("setNoticeOn works for every flag in the enum")
    void onEveryFlag() {
        for (ObjectNotice n : ObjectNotice.values()) {
            assertTrue(item.setNoticeOn(n), n.name());
            assertTrue(item.getNotice().has(n), n.name());
        }
    }

    @Test
    @DisplayName("setNoticeOff clears a raised flag and reports true")
    void offClearsFlag() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IGNORE);
        assertTrue(item.setNoticeOff(ObjectNotice.OBJ_NOTICE_IGNORE));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_IGNORE));
    }

    @Test
    @DisplayName("setNoticeOff on a flag that was never up reports false and changes nothing, as &= ~ is idempotent")
    void offOnDownFlag() {
        assertFalse(item.setNoticeOff(ObjectNotice.OBJ_NOTICE_IGNORE));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_IGNORE));
    }

    @Test
    @DisplayName("setNoticeOff leaves the other flags up")
    void offLeavesOthers() {
        for (ObjectNotice n : ObjectNotice.values()) item.setNoticeOn(n);
        item.setNoticeOff(ObjectNotice.OBJ_NOTICE_IGNORE);
        assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_WORN));
        assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED));
        assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_IMAGINED));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_IGNORE));
    }

    @Test
    @DisplayName("on then off then on again round-trips, the ignore/unignore menu path in ui-object.c")
    void ignoreUnignoreRoundTrip() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IGNORE);
        item.setNoticeOff(ObjectNotice.OBJ_NOTICE_IGNORE);
        assertTrue(item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IGNORE));
        assertTrue(item.getNotice().has(ObjectNotice.OBJ_NOTICE_IGNORE));
    }

    @Test
    @DisplayName("setNoticeOn and orNotice edit the same set")
    void sharesSetWithOrNotice() {
        item.orNotice(ObjectNotice.OBJ_NOTICE_WORN);
        assertFalse(item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN));
        assertTrue(item.setNoticeOff(ObjectNotice.OBJ_NOTICE_WORN));
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_WORN));
    }

    @Test
    @DisplayName("editing the copy getNotice returns marks nothing on the item")
    void getNoticeCopyIsDetached() {
        item.getNotice().on(ObjectNotice.OBJ_NOTICE_WORN);
        assertFalse(item.getNotice().has(ObjectNotice.OBJ_NOTICE_WORN));
    }
}
