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
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectKind#setTval}, the port of C's direct {@code kind->tval} write.
 *
 * <p>C has no setter. {@code parse_object_type} in {@code obj-init.c} and {@code write_book_kind}
 * in {@code init.c} assign {@code kind->tval} and then, as separate statements, point
 * {@code kind->base} at {@code kb_info[tval]} and take the next sval from that base. The expected
 * values here come from that: the assignment is the whole of what the setter owes, it stores any
 * value without a check ({@code tval_find_idx}'s rejection of an unknown name happens before the
 * assignment, in the parser), and nothing else on the kind moves with it.
 *
 * <p>Class ObjectKindSetTvalTest coded on 261025, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
class ObjectKindSetTvalTest {

    /**
     * C's {@code tval} is a {@code uint8_t} holding any of the {@code TV_*} numbers, so every one of
     * them, {@code TV_NONE} included, must go in and come back unchanged.
     */
    @Test
    @DisplayName("every TValue goes in and comes back out")
    void everyTvalueRoundTrips() {
        for (TValue tValue : TValue.values()) {
            ObjectKind kind = new ObjectKind();
            kind.setTval(tValue);
            assertSame(tValue, kind.gettValue(), () -> "round trip of " + tValue);
        }
    }

    /**
     * A second write replaces the first, as a second {@code kind->tval = x} would.
     */
    @Test
    @DisplayName("a second write replaces the first")
    void secondWriteReplacesTheFirst() {
        ObjectKind kind = new ObjectKind();
        kind.setTval(TValue.TV_RING);
        kind.setTval(TValue.TV_POTION);
        assertSame(TValue.TV_POTION, kind.gettValue());
    }

    /**
     * Writing the value the kind already has leaves it as it was.
     */
    @Test
    @DisplayName("writing the same value twice is harmless")
    void sameValueTwice() {
        ObjectKind kind = new ObjectKind();
        kind.setTval(TValue.TV_AMULET);
        kind.setTval(TValue.TV_AMULET);
        assertSame(TValue.TV_AMULET, kind.gettValue());
    }

    /**
     * The empty shell has no item type, and the setter is what gives it one.
     */
    @Test
    @DisplayName("the empty shell starts with no tval")
    void emptyShellStartsWithNone() {
        assertNull(new ObjectKind().gettValue());
    }

    /**
     * The setter is only the assignment: C's {@code parse_object_type} also sets {@code kind->base}
     * and {@code kind->sval} on the lines after it, and this method must not do so on its own. A kind
     * with an sval already in place keeps it, and a kind with no base still has none.
     */
    @Test
    @DisplayName("only the tval moves - base and sval are left alone")
    void leavesBaseAndSvalAlone() {
        ObjectKind kind = new ObjectKind();
        kind.setsVal(7);

        kind.setTval(TValue.TV_WAND);

        assertNull(kind.getBase(), "C points kind->base at kb_info[tval] separately");
        assertEquals(7, kind.getsVal(), "C takes the next sval from the base separately");
    }

    /**
     * C's value is a bare number and cannot be absent, but the port stores what it is given and says
     * so in its Javadoc; this pins that there is no hidden guard that would turn {@code null} into a
     * default or throw.
     */
    @Test
    @DisplayName("null is stored as given, with no guard")
    void nullIsStoredAsGiven() {
        ObjectKind kind = new ObjectKind();
        kind.setTval(TValue.TV_RING);
        assertDoesNotThrow(() -> kind.setTval(null));
        assertNull(kind.gettValue());
    }
}
