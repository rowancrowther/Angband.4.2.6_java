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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link CombatRunes} to {@code enum combat_runes} in {@code obj-knowledge.h} and the
 * {@code c_rune[]} table in {@code obj-knowledge.c}. Expected values are written from the C source:
 * the enum values 0 to 3, and the three strings of {@code c_rune[]} in Oxford spelling
 * ("armour" where C says "armor").
 *
 * <p>Class CombatRunesTest coded on 261009, commented in full on 261009.
 */
class CombatRunesTest {

    /**
     * C declares {@code COMBAT_RUNE_TO_A = 0}, then {@code TO_H}, {@code TO_D}, {@code MAX} in
     * that order, so the ordinals are 0, 1, 2 and 3. The order matters because {@code init_rune}
     * fills the first slots of the rune list by looping {@code i} from 0 to
     * {@code COMBAT_RUNE_MAX}.
     *
     * <p>Function ordinalsMatchTheCEnum coded on 261009, commented in full on 261009.
     */
    @Test
    void ordinalsMatchTheCEnum() {
        assertEquals(0, CombatRunes.COMBAT_RUNE_TO_A.ordinal());
        assertEquals(1, CombatRunes.COMBAT_RUNE_TO_H.ordinal());
        assertEquals(2, CombatRunes.COMBAT_RUNE_TO_D.ordinal());
        assertEquals(3, CombatRunes.COMBAT_RUNE_MAX.ordinal());
    }

    /**
     * The constant count is the three runes plus the sentinel, and the sentinel's ordinal is the
     * number of real runes, as {@code COMBAT_RUNE_MAX} is in C.
     *
     * <p>Function sentinelOrdinalIsTheRuneCount coded on 261009, commented in full on 261009.
     */
    @Test
    void sentinelOrdinalIsTheRuneCount() {
        assertEquals(4, CombatRunes.values().length);
        assertEquals(CombatRunes.values().length - 1, CombatRunes.COMBAT_RUNE_MAX.ordinal());
    }

    /**
     * The names are the C identifiers, spelled exactly, in declaration order.
     *
     * <p>Function namesAreTheCIdentifiers coded on 261009, commented in full on 261009.
     */
    @Test
    void namesAreTheCIdentifiers() {
        assertEquals(
                List.of("COMBAT_RUNE_TO_A", "COMBAT_RUNE_TO_H", "COMBAT_RUNE_TO_D", "COMBAT_RUNE_MAX"),
                Arrays.stream(CombatRunes.values()).map(Enum::name).toList());
    }

    /**
     * {@code c_rune[0..2]} are "enchantment to armor", "enchantment to hit" and "enchantment to
     * damage"; the Java text differs only by the Oxford "armour".
     *
     * <p>Function descriptionsPortCRuneTable coded on 261009, commented in full on 261009.
     */
    @Test
    void descriptionsPortCRuneTable() {
        assertEquals("enchantment to armour", CombatRunes.COMBAT_RUNE_TO_A.getDescription());
        assertEquals("enchantment to hit", CombatRunes.COMBAT_RUNE_TO_H.getDescription());
        assertEquals("enchantment to damage", CombatRunes.COMBAT_RUNE_TO_D.getDescription());
    }

    /**
     * {@code c_rune[]} has three entries, so the sentinel has no text: its description is the
     * empty string rather than null or a placeholder.
     *
     * <p>Function sentinelHasEmptyDescription coded on 261009, commented in full on 261009.
     */
    @Test
    void sentinelHasEmptyDescription() {
        assertEquals("", CombatRunes.COMBAT_RUNE_MAX.getDescription());
    }

    /**
     * Every real rune has a distinct, non-blank description, so the knowledge menu can never show
     * two combat runes under the same name. The sentinel is excluded, as C's
     * {@code i < COMBAT_RUNE_MAX} bound excludes it.
     *
     * <p>Function realRunesHaveDistinctNonBlankDescriptions coded on 261009, commented in full on
     * 261009.
     */
    @Test
    void realRunesHaveDistinctNonBlankDescriptions() {
        Set<String> seen = new HashSet<>();
        for (CombatRunes rune : CombatRunes.values()) {
            if (rune == CombatRunes.COMBAT_RUNE_MAX) continue;
            assertFalse(rune.getDescription().isBlank(), rune.name());
            assertFalse(!seen.add(rune.getDescription()), rune.name() + " repeats a description");
        }
        assertEquals(3, seen.size());
    }

    /**
     * Lookup by name is exact: the C identifier resolves, a near miss does not.
     *
     * <p>Function valueOfIsExact coded on 261009, commented in full on 261009.
     */
    @Test
    void valueOfIsExact() {
        assertEquals(CombatRunes.COMBAT_RUNE_TO_H, CombatRunes.valueOf("COMBAT_RUNE_TO_H"));
        assertThrows(IllegalArgumentException.class, () -> CombatRunes.valueOf("combat_rune_to_h"));
        assertThrows(IllegalArgumentException.class, () -> CombatRunes.valueOf("COMBAT_RUNE_TO_X"));
    }
}
