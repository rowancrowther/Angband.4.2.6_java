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

package uk.co.jackoftradesltd.middle.player.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerRedraw}'s group membership — the port of C's {@code PR_BASIC},
 * {@code PR_EXTRA} and {@code PR_SUBWINDOW} masks ({@code player-calcs.h:81-96}).
 *
 * <p>C composes each group by OR-ing named flags into one constant, so membership is stated once and
 * in one place. The port has no bit values to OR, so each constant carries three booleans instead,
 * and the single statement became twenty-six three-column rows. That is a transcription, and
 * transcriptions of tables are where flags land in the wrong column — a redraw flag in the wrong
 * group repaints the wrong region, which looks like a rendering bug rather than a data one.
 *
 * <p>So the three groups are checked against C's lists by name. The disjointness test is the one
 * that catches the likely slip: C's three masks share no flag, so a constant answering {@code true}
 * twice means a row was filled in from the wrong line.
 *
 * <p>Class PlayerRedrawTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class PlayerRedrawTest {

    /**
     * Collects the flags answering {@code true} to one membership question.
     *
     * @param test the membership question
     * @return the flags in that group
     */
    private static Set<PlayerRedraw> group(java.util.function.Predicate<PlayerRedraw> test) {
        Set<PlayerRedraw> found = EnumSet.noneOf(PlayerRedraw.class);
        for (PlayerRedraw flag : PlayerRedraw.values()) {
            if (test.test(flag)) found.add(flag);
        }
        return found;
    }

    /**
     * C's {@code PR_BASIC} — the twelve character-sheet fields down the side bar
     * ({@code player-calcs.h:81-84}).
     */
    @Test
    @DisplayName("the basic group is C's twelve side-bar fields")
    void basicGroup() {
        Set<PlayerRedraw> expected = EnumSet.of(
                PlayerRedraw.PR_MISC, PlayerRedraw.PR_TITLE, PlayerRedraw.PR_STATS,
                PlayerRedraw.PR_LEV, PlayerRedraw.PR_EXP, PlayerRedraw.PR_GOLD,
                PlayerRedraw.PR_ARMOR, PlayerRedraw.PR_HP, PlayerRedraw.PR_MANA,
                PlayerRedraw.PR_DEPTH, PlayerRedraw.PR_HEALTH, PlayerRedraw.PR_SPEED);

        assertEquals(expected, group(PlayerRedraw::isBasic));
    }

    /**
     * C's {@code PR_EXTRA} — status, state and study ({@code player-calcs.h:89-90}).
     */
    @Test
    @DisplayName("the extra group is status, state and study")
    void extraGroup() {
        Set<PlayerRedraw> expected = EnumSet.of(
                PlayerRedraw.PR_STATUS, PlayerRedraw.PR_STATE, PlayerRedraw.PR_STUDY);

        assertEquals(expected, group(PlayerRedraw::isExtra));
    }

    /**
     * C's {@code PR_SUBWINDOW} — the four detachable recall and list panes
     * ({@code player-calcs.h:95-96}).
     */
    @Test
    @DisplayName("the subwindow group is the four detachable panes")
    void subwindowGroup() {
        Set<PlayerRedraw> expected = EnumSet.of(
                PlayerRedraw.PR_MONSTER, PlayerRedraw.PR_OBJECT,
                PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST);

        assertEquals(expected, group(PlayerRedraw::isSubwindow));
    }

    /**
     * No flag belongs to two groups, because C's three masks share no constant. A flag that answered
     * twice would be a row transcribed from the wrong line, and this is the cheapest way to see it.
     */
    @Test
    @DisplayName("no flag belongs to more than one group")
    void groupsAreDisjoint() {
        assertAll(() -> {
            for (PlayerRedraw flag : PlayerRedraw.values()) {
                int memberships = (flag.isBasic() ? 1 : 0)
                        + (flag.isExtra() ? 1 : 0)
                        + (flag.isSubwindow() ? 1 : 0);
                assertTrue(memberships <= 1, flag + " belongs to " + memberships + " groups");
            }
        });
    }

    /**
     * The groups do not cover everything, and that is C's arrangement too — the map, the two item
     * lists, the message line, the trap-detect edge, the level feeling and the light radius are in
     * none of the three masks. Pinned so that "every flag should be in a group" is not mistaken for
     * a missing entry later.
     */
    @Test
    @DisplayName("some flags belong to no group at all")
    void someFlagsAreUngrouped() {
        assertAll(
                () -> assertFalse(PlayerRedraw.PR_MAP.isBasic()),
                () -> assertFalse(PlayerRedraw.PR_MAP.isExtra()),
                () -> assertFalse(PlayerRedraw.PR_MAP.isSubwindow()),
                () -> assertFalse(PlayerRedraw.PR_MESSAGE.isBasic()),
                () -> assertFalse(PlayerRedraw.PR_LIGHT.isSubwindow()));
    }
}
