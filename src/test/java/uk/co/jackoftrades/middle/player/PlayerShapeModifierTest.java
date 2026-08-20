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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerShape#getModifier(Stats)}, the port of reading C's
 * {@code shape->modifiers[i]} for a stat index ({@code player-calcs.c:1821-1823}).
 *
 * <p><b>C is subscripting one array with two different kinds of index.</b> A shape's modifiers are
 * an {@code OBJ_MOD_*} array, and {@code calc_shapechange} reads the first five entries as though
 * they were the five stats — which works only because {@code list-object-modifiers.h} happens to
 * open with STR, INT, WIS, DEX and CON in the order {@code list-stats.h} declares them. Nothing
 * enforces that; it is a property the two data files maintain by convention.
 *
 * <p>The port declines the coincidence and resolves the name instead. That is worth a test because
 * the failure it prevents is silent and specific: were the two lists ever to fall out of step, C
 * would quietly read a shape's constitution bonus as its stealth bonus, and the port must not. The
 * tests below pin each stat to its own modifier by giving all five different values — a fixture with
 * one value would pass however the names were wired.
 *
 * <p>Class PlayerShapeModifierTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class PlayerShapeModifierTest {

    /**
     * A shape carrying the given modifiers and nothing else.
     *
     * @param modifiers the shape's {@code values:} modifiers
     * @return the shape
     */
    private static PlayerShape shape(Map<ObjectModifier, Integer> modifiers) {
        return new PlayerShape("werewolf", 0, 0, 0, Map.<PlayerSkill, Integer>of(),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), modifiers,
                Map.<ElementEnum, ElementInfo>of(), List.of(), 0, List.of());
    }

    /**
     * Each stat reads its own modifier. Five distinct values, so a crossed pair shows up as a
     * mismatch rather than passing.
     */
    @Test
    @DisplayName("each stat resolves to the modifier of the same name")
    void eachStatResolvesToItsOwnModifier() {
        PlayerShape s = shape(Map.of(
                ObjectModifier.OM_STR, 1,
                ObjectModifier.OM_INT, 2,
                ObjectModifier.OM_WIS, 3,
                ObjectModifier.OM_DEX, 4,
                ObjectModifier.OM_CON, 5));

        assertAll(
                () -> assertEquals(1, s.getModifier(Stats.STAT_STR)),
                () -> assertEquals(2, s.getModifier(Stats.STAT_INT)),
                () -> assertEquals(3, s.getModifier(Stats.STAT_WIS)),
                () -> assertEquals(4, s.getModifier(Stats.STAT_DEX)),
                () -> assertEquals(5, s.getModifier(Stats.STAT_CON)));
    }

    /**
     * A stat the shape says nothing about is zero, not a neighbour's value — which is what a
     * mis-resolved name would most likely produce.
     */
    @Test
    @DisplayName("a stat the shape does not modify reads as zero")
    void unmodifiedStatIsZero() {
        PlayerShape s = shape(Map.of(ObjectModifier.OM_STR, 4));

        assertAll(
                () -> assertEquals(4, s.getModifier(Stats.STAT_STR)),
                () -> assertEquals(0, s.getModifier(Stats.STAT_CON)));
    }

    /**
     * A shape's stat modifier and its non-stat modifiers come from the same map, and reading one
     * must not reach the other. {@code calcShapechange} adds stealth and search through a different
     * route entirely, so a stat accessor that fell through to them would double-count.
     */
    @Test
    @DisplayName("stat lookups do not reach the non-stat modifiers")
    void statLookupsDoNotReachOtherModifiers() {
        PlayerShape s = shape(Map.of(
                ObjectModifier.OM_STEALTH, 9,
                ObjectModifier.OM_STR, 1));

        assertAll(
                () -> assertEquals(1, s.getModifier(Stats.STAT_STR)),
                () -> assertEquals(9, s.getObjectValueModifiers()
                        .getOrDefault(ObjectModifier.OM_STEALTH, 0)),
                () -> assertTrue(s.getModifier(Stats.STAT_DEX) == 0));
    }

    /**
     * Negative modifiers pass through — a shape that trades intelligence for strength is ordinary,
     * and nothing clamps on the way out.
     */
    @Test
    @DisplayName("negative modifiers are returned as they stand")
    void negativeModifiers() {
        PlayerShape s = shape(Map.of(ObjectModifier.OM_INT, -4));

        assertEquals(-4, s.getModifier(Stats.STAT_INT));
    }
}
