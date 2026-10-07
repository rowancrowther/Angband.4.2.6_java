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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.channel.utils.Flag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Part F round-two checks on {@link ItemObject}: the inscription scan, the flag readers and the
 * aliasing behaviour of {@code objectFlags}. Expected values are worked from C's
 * {@code check_for_inscrip}, {@code of_has} and the {@code of_wipe} then {@code of_copy} pair in
 * {@code object_flags}, not from the Java.
 *
 * @author Rowan Crowther
 */
class ItemObjectPartFRound2Test {

    /**
     * Overlapping matches are each counted, because C's scan resumes at {@code s++}: "!!!" holds
     * "!!" at offsets 0 and 1.
     */
    @Test
    @DisplayName("checkForInscription counts overlapping matches")
    void overlappingMatchesCounted() {
        ItemObject item = new ItemObject();
        item.setNote("!!!");

        assertEquals(2, item.checkForInscription("!!"));
        assertEquals(3, item.checkForInscription("!"));
    }

    /**
     * A single tag inside a longer note is found once, and a missing tag or a missing note counts
     * as zero.
     */
    @Test
    @DisplayName("checkForInscription: single, absent and no note")
    void singleAbsentAndNoNote() {
        ItemObject item = new ItemObject();
        assertEquals(0, item.checkForInscription("!d"));

        item.setNote("@m1 !d");
        assertEquals(1, item.checkForInscription("!d"));
        assertEquals(0, item.checkForInscription("!k"));
        assertEquals(0, item.checkForInscription("D"), "strstr is case sensitive");
    }

    /**
     * A flag set on the item is reported by {@code hasFlag}; a flag that is not set is not.
     */
    @Test
    @DisplayName("hasFlag reads the item's own flags")
    void hasFlagReadsOwnFlags() {
        ItemObject item = new ItemObject();
        item.getObjectFlags().on(ObjectFlag.OF_FEATHER);

        assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER));
        assertFalse(item.hasFlag(ObjectFlag.OF_BURNS_OUT));
    }

    /**
     * {@code objectFlags} wipes the caller's set before copying, so what was in it is discarded.
     */
    @Test
    @DisplayName("objectFlags replaces, not merges, the caller's set")
    void objectFlagsReplaces() {
        ItemObject item = new ItemObject();
        item.getObjectFlags().on(ObjectFlag.OF_FEATHER);

        Flag<ObjectFlag> out = new Flag<>(ObjectFlag.class, ObjectFlag.OF_BURNS_OUT);
        item.objectFlags(out);

        assertTrue(out.has(ObjectFlag.OF_FEATHER));
        assertFalse(out.has(ObjectFlag.OF_BURNS_OUT));
        assertEquals(1, out.count());
    }

    /**
     * Passing the item's own live set empties it, as C's {@code of_wipe} then {@code of_copy} on
     * the same array does.
     */
    @Test
    @DisplayName("objectFlags given the live set empties it, as C does")
    void objectFlagsAliasedEmpties() {
        ItemObject item = new ItemObject();
        item.getObjectFlags().on(ObjectFlag.OF_FEATHER);

        item.objectFlags(item.getObjectFlags());

        assertTrue(item.getObjectFlags().isEmpty());
    }
}
