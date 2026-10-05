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
 * Tests {@link ItemObject#getNoticeHas}, the port of C's {@code obj->notice & OBJ_NOTICE_x} read.
 *
 * <p>The expected values come from the bit-AND semantics of C: {@code object_prep} leaves
 * {@code notice} at 0 so every test is false on a fresh item; a single-bit mask is true only when
 * that bit is set, whatever the other three bits hold; and the four {@code OBJ_NOTICE_*} flags of
 * {@code object.h} (0x01, 0x02, 0x04, 0x08) are independent bits.
 *
 * <p>Test class ItemObjectNoticeHasTest written on 261003.
 */
class ItemObjectNoticeHasTest {

    private ItemObject item;

    @BeforeEach
    void setUp() {
        item = new ItemObject();
    }

    @Test
    @DisplayName("A fresh item has every notice flag down, as C's notice starts at 0")
    void freshItemHasNone() {
        for (ObjectNotice n : ObjectNotice.values()) {
            assertFalse(item.getNoticeHas(n), n.name());
        }
    }

    @Test
    @DisplayName("A flag raised with setNoticeOn tests true")
    void raisedFlagTestsTrue() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED);
        assertTrue(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED));
    }

    @Test
    @DisplayName("A flag raised with orNotice tests true")
    void orNoticeTestsTrue() {
        item.orNotice(ObjectNotice.OBJ_NOTICE_WORN);
        assertTrue(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_WORN));
    }

    @Test
    @DisplayName("Raising one flag leaves the other three testing false")
    void onlyTheRaisedFlagIsTrue() {
        for (ObjectNotice raised : ObjectNotice.values()) {
            ItemObject fresh = new ItemObject();
            fresh.setNoticeOn(raised);
            for (ObjectNotice probe : ObjectNotice.values()) {
                assertTrue(fresh.getNoticeHas(probe) == (probe == raised),
                        "raised " + raised + ", probed " + probe);
            }
        }
    }

    @Test
    @DisplayName("All four flags can be up together and each tests true")
    void allFlagsTogether() {
        for (ObjectNotice n : ObjectNotice.values()) {
            item.setNoticeOn(n);
        }
        for (ObjectNotice n : ObjectNotice.values()) {
            assertTrue(item.getNoticeHas(n), n.name());
        }
    }

    @Test
    @DisplayName("Lowering a flag with setNoticeOff makes it test false, as obj->notice &= ~flag")
    void loweredFlagTestsFalse() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IGNORE);
        item.setNoticeOff(ObjectNotice.OBJ_NOTICE_IGNORE);
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_IGNORE));
    }

    @Test
    @DisplayName("Lowering one flag leaves another that was up still testing true")
    void loweringOneKeepsAnother() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IGNORE);
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IMAGINED);
        item.setNoticeOff(ObjectNotice.OBJ_NOTICE_IGNORE);
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_IGNORE));
        assertTrue(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_IMAGINED));
    }

    @Test
    @DisplayName("The test reads this item's own set, not its known half's")
    void readsOwnSetNotKnown() {
        ItemObject known = new ItemObject();
        item.setKnown(known);
        known.setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED);
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED));
        assertTrue(item.getKnown().getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED));
    }

    @Test
    @DisplayName("A copy's flags are independent: raising one on the copy does not show on the original")
    void copyIsIndependent() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN);
        ItemObject copy = item.copy(false);
        assertTrue(copy.getNoticeHas(ObjectNotice.OBJ_NOTICE_WORN));
        copy.setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED);
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_ASSESSED));
    }

    @Test
    @DisplayName("wipe blanks the notice flags, so every test is false afterwards")
    void wipeClearsFlags() {
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN);
        item.setNoticeOn(ObjectNotice.OBJ_NOTICE_IMAGINED);
        item.wipe();
        for (ObjectNotice n : ObjectNotice.values()) {
            assertFalse(item.getNoticeHas(n), n.name());
        }
    }

    @Test
    @DisplayName("Editing the copy getNotice hands out does not change what getNoticeHas reports")
    void getNoticeCopyDoesNotLeak() {
        item.getNotice().on(ObjectNotice.OBJ_NOTICE_WORN);
        assertFalse(item.getNoticeHas(ObjectNotice.OBJ_NOTICE_WORN));
    }
}
