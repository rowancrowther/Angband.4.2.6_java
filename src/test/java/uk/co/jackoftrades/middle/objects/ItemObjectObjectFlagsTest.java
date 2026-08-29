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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#objectFlags(Flag)} — the port of C's {@code object_flags}
 * ({@code src/obj-util.c:351}), which is three clauses long:
 *
 * <pre>
 *     of_wipe(flags);
 *     if (!obj) return;
 *     of_copy(flags, obj-&gt;flags);
 * </pre>
 *
 * <p>The expected values below come from those clauses, not from the Java. The one that carries
 * weight is the wipe: {@code of_wipe} is a {@code memset} to zero, so a set the caller has already
 * filled comes back holding this item's flags <b>only</b> — the caller's contents are discarded,
 * never merged. C's callers rely on that, reusing one buffer across a run of items, and a port that
 * unioned instead would leak the previous item's flags into the next.
 *
 * <p>The null clause has no counterpart to test: an instance method cannot be reached without an
 * item. What is testable is the other half of C's out-parameter contract — that the caller's set is
 * filled in place and does not afterwards alias the item's own set, so that writing to one leaves
 * the other alone.
 *
 * @author Rowan Crowther
 */
class ItemObjectObjectFlagsTest {

    /**
     * Builds an item carrying the given flags, everything else empty or zero.
     *
     * @param flags the flag set the item should hold
     * @return the constructed item
     */
    private static ItemObject itemWithFlags(Flag<ObjectFlag> flags) {
        return new ItemObject(new ObjectKind(), null, null, null, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                flags, Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    /**
     * A flag set holding exactly the flags given.
     *
     * @param on the flags to switch on
     * @return the set
     */
    private static Flag<ObjectFlag> flagsOf(ObjectFlag... on) {
        Flag<ObjectFlag> set = new Flag<>(ObjectFlag.class);
        for (ObjectFlag flag : on) set.set(flag);
        return set;
    }

    @Test
    @DisplayName("of_copy: an empty set comes back holding exactly the item's flags")
    void copiesTheItemsFlagsIntoAnEmptySet() {
        ItemObject item = itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS, ObjectFlag.OF_FREE_ACT));

        Flag<ObjectFlag> out = new Flag<>(ObjectFlag.class);
        item.objectFlags(out);

        assertTrue(out.has(ObjectFlag.OF_SEE_INVIS), "OF_SEE_INVIS should have been copied");
        assertTrue(out.has(ObjectFlag.OF_FREE_ACT), "OF_FREE_ACT should have been copied");
        assertEquals(2, out.count(), "only the item's two flags should be set");
    }

    @Test
    @DisplayName("of_wipe: the caller's existing flags are discarded, not merged")
    void wipesTheCallersSetBeforeCopying() {
        ItemObject item = itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS));

        Flag<ObjectFlag> out = flagsOf(ObjectFlag.OF_HOLD_LIFE, ObjectFlag.OF_PROT_FEAR);
        item.objectFlags(out);

        assertTrue(out.has(ObjectFlag.OF_SEE_INVIS), "the item's flag should be present");
        assertFalse(out.has(ObjectFlag.OF_HOLD_LIFE), "OF_HOLD_LIFE was the caller's, and of_wipe clears it");
        assertFalse(out.has(ObjectFlag.OF_PROT_FEAR), "OF_PROT_FEAR was the caller's, and of_wipe clears it");
        assertEquals(1, out.count(), "the result is the item's flags alone");
    }

    @Test
    @DisplayName("an item with no flags leaves the caller's set empty")
    void anItemWithNoFlagsEmptiesTheSet() {
        ItemObject item = itemWithFlags(new Flag<>(ObjectFlag.class));

        Flag<ObjectFlag> out = flagsOf(ObjectFlag.OF_FEATHER);
        item.objectFlags(out);

        assertEquals(0, out.count(), "wipe with nothing to copy back in leaves an empty set");
    }

    @Test
    @DisplayName("reuse across items: the second call does not carry the first item's flags")
    void reusingOneSetAcrossTwoItemsDoesNotAccumulate() {
        ItemObject first = itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS));
        ItemObject second = itemWithFlags(flagsOf(ObjectFlag.OF_FREE_ACT));

        Flag<ObjectFlag> out = new Flag<>(ObjectFlag.class);
        first.objectFlags(out);
        second.objectFlags(out);

        assertTrue(out.has(ObjectFlag.OF_FREE_ACT), "the second item's flag should be present");
        assertFalse(out.has(ObjectFlag.OF_SEE_INVIS), "the first item's flag should have been wiped");
        assertEquals(1, out.count(), "only the second item's flags survive");
    }

    @Test
    @DisplayName("the filled set is a copy: writing to it does not reach the item")
    void theResultDoesNotAliasTheItemsOwnSet() {
        Flag<ObjectFlag> itemFlags = flagsOf(ObjectFlag.OF_SEE_INVIS);
        ItemObject item = itemWithFlags(itemFlags);

        Flag<ObjectFlag> out = new Flag<>(ObjectFlag.class);
        item.objectFlags(out);
        assertNotSame(itemFlags, out, "the caller's set is filled, not replaced by the item's");

        out.set(ObjectFlag.OF_FEATHER);
        assertFalse(item.hasFlag(ObjectFlag.OF_FEATHER), "writing to the copy must not reach the item");

        item.setFlag(ObjectFlag.OF_HOLD_LIFE);
        assertFalse(out.has(ObjectFlag.OF_HOLD_LIFE), "writing to the item must not reach the copy");
    }

    @Test
    @DisplayName("agrees with getFlags, the same read in return-value form")
    void agreesWithGetFlags() {
        ItemObject item = itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS, ObjectFlag.OF_FEATHER));

        Flag<ObjectFlag> out = flagsOf(ObjectFlag.OF_PROT_FEAR);
        item.objectFlags(out);

        assertTrue(out.isEqual(item.getFlags()), "the out-parameter and return-value forms must agree");
    }
}
