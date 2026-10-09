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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link StartItem}, the port of C's {@code struct start_item} ({@code player.h}).
 *
 * <p>The type is a plain data holder, so the cases pin what the constructor stores and the
 * getters return. The values are taken from the {@code equip:} lines of the Warrior in
 * {@code lib/gamedata/class.txt}, which {@code parse_class_equip()} ({@code init.c}) turns into
 * {@code start_item} records: {@code food:Ration of Food:1:3:none},
 * {@code potion:Berserk Strength:1:1:none} and {@code scroll:Word of Recall:1:1:birth_no_recall}.
 *
 * <p>Class StartItemTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class StartItemTest {

    /**
     * {@code equip:food:Ration of Food:1:3:none} — a quantity range with no exclusions.
     */
    @Test
    @DisplayName("Ration of Food line stores tval, sval and the 1-3 range")
    void rationOfFoodFields() {
        StartItem item = new StartItem(TValue.TV_FOOD, "Ration of Food", 1, 3, List.of());

        assertEquals(TValue.TV_FOOD, item.gettValue());
        assertEquals("Ration of Food", item.getsValue());
        assertEquals(1, item.getMin());
        assertEquals(3, item.getMax());
    }

    /**
     * {@code none} in the file leaves {@code si->eopts} unset in C; the port represents that as an
     * empty list, which the {@code PlayerBirth} loop treats as "never excluded".
     */
    @Test
    @DisplayName("an unconstrained item exposes an empty exclusion list")
    void unconstrainedItemHasEmptyExclusions() {
        StartItem item = new StartItem(TValue.TV_POTION, "Berserk Strength", 1, 1, List.of());

        assertTrue(item.geteOpts().isEmpty());
    }

    /**
     * {@code equip:scroll:Word of Recall:1:1:birth_no_recall} — one plain (positive) exclusion,
     * C's {@code eopts[0] > 0} case.
     */
    @Test
    @DisplayName("Word of Recall line keeps its single non-negated birth_no_recall exclusion")
    void wordOfRecallExclusion() {
        StartOptionExclusion exclusion = new StartOptionExclusion(PlayerOptionEnum.OP_birth_no_recall, false);
        StartItem item = new StartItem(TValue.TV_SCROLL, "Word of Recall", 1, 1, List.of(exclusion));

        assertEquals(1, item.geteOpts().size());
        assertSame(exclusion, item.geteOpts().getFirst());
        assertEquals(PlayerOptionEnum.OP_birth_no_recall, item.geteOpts().getFirst().option());
        assertFalse(item.geteOpts().getFirst().negated());
    }

    /**
     * A {@code NOT-} entry is C's negative index; both it and a plain entry must survive in the
     * order given, since C's array is walked in order.
     */
    @Test
    @DisplayName("mixed plain and NOT- exclusions keep their order and sign")
    void mixedExclusionsKeepOrderAndSign() {
        StartOptionExclusion plain = new StartOptionExclusion(PlayerOptionEnum.OP_birth_no_recall, false);
        StartOptionExclusion negated = new StartOptionExclusion(PlayerOptionEnum.OP_birth_start_kit, true);
        StartItem item = new StartItem(TValue.TV_SCROLL, "Word of Recall", 1, 1, List.of(plain, negated));

        assertEquals(List.of(plain, negated), item.geteOpts());
        assertFalse(item.geteOpts().get(0).negated());
        assertTrue(item.geteOpts().get(1).negated());
    }

    /**
     * The constructor does not copy the list, so the getter hands back the same instance.
     */
    @Test
    @DisplayName("geteOpts returns the list the constructor was given")
    void exclusionListIsNotCopied() {
        List<StartOptionExclusion> list = List.of(
                new StartOptionExclusion(PlayerOptionEnum.OP_birth_force_descend, false));
        StartItem item = new StartItem(TValue.TV_SCROLL, "Deep Descent", 1, 1, list);

        assertSame(list, item.geteOpts());
    }

    /**
     * C accepts {@code min == max} (every {@code :1:1:} line above) and {@code 99}, the largest
     * value {@code parse_class_equip()} allows; the holder must store both unchanged.
     */
    @Test
    @DisplayName("equal bounds and the C maximum of 99 are stored unchanged")
    void boundaryQuantities() {
        StartItem single = new StartItem(TValue.TV_SWORD, "Dagger", 1, 1, List.of());
        StartItem top = new StartItem(TValue.TV_FOOD, "Ration of Food", 99, 99, List.of());

        assertEquals(1, single.getMin());
        assertEquals(1, single.getMax());
        assertEquals(99, top.getMin());
        assertEquals(99, top.getMax());
    }

    /**
     * Nothing in the constructor validates; a null list is stored as null. The birth loop
     * guards for this, so the holder must not substitute an empty list behind its back.
     */
    @Test
    @DisplayName("a null exclusion list is stored as null")
    void nullExclusionsStoredAsNull() {
        StartItem item = new StartItem(TValue.TV_LIGHT, "Wooden Torch", 1, 3, null);

        assertNull(item.geteOpts());
    }
}
