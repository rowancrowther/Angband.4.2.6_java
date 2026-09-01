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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerUtils#playerIsImmune(Player, ElementEnum)}, the port of C's
 * {@code player_is_immune}
 * ({@code player-util.c:1566}).
 *
 * <p>The C function is a single comparison, {@code p->state.el_info[element].res_level == 3}, and
 * that equality — not a {@code >=} — is the whole of its behaviour. So the values worth walking are
 * the ones either side of 3 and, because C compares for equality, a level above 3 as well: an
 * implementation written as "3 or better" would pass every other case here and only part company on
 * that one.
 *
 * <p>The other thing the C pins down is <em>which</em> state is read. {@code player_is_immune} takes
 * {@code p->state}, the calculated state, and not {@code p->known_state}, so a player who is immune
 * without knowing it still answers {@code true}. The port's own caller in the timed-effect code uses
 * the knowledge check separately, alongside this one, which is only correct if this one ignores
 * knowledge.
 *
 * <p>The method is called directly rather than through the timed effect that uses it; the timed
 * path has its own conditions and would not isolate this comparison. The calculated state it reads
 * is still installed by reflection, since {@code new Player()} leaves it null.
 *
 * <p>Class PlayerIsImmuneTest coded on 260829, commented in full on 260829, reworked on 260901 for
 * the move of the method to {@link PlayerUtils}.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerIsImmuneTest {

    /**
     * The player under test, fresh for each test.
     */
    private Player player;

    /**
     * The calculated state the method reads, held here so tests can set resistance levels on it.
     */
    private PlayerState state;

    /**
     * A new player with an empty calculated state — {@code new Player()} leaves {@code state} null,
     * since C's state is only filled by {@code calc_bonuses}, so one is installed directly. A fresh
     * {@link PlayerState} has an entry at level 0 for every real element.
     *
     * @throws Exception if the field cannot be reached
     */
    @BeforeEach
    void newPlayer() throws Exception {
        player = new Player();
        state = new PlayerState();
        Field field = Player.class.getDeclaredField("state");
        field.setAccessible(true);
        field.set(player, state);
    }

    /**
     * Calls the method under test.
     *
     * @param element the element to ask about
     * @return what the method returned
     */
    private boolean isImmune(ElementEnum element) {
        return PlayerUtils.playerIsImmune(player, element);
    }

    /**
     * Level 3 is immunity, and is the only level the C accepts.
     */
    @Test
    @DisplayName("a resistance level of exactly 3 is immunity")
    void levelThreeIsImmune() throws Exception {
        state.setResLevel(ElementEnum.ELEM_FIRE, 3);
        assertTrue(isImmune(ElementEnum.ELEM_FIRE));
    }

    /**
     * The levels below immunity: vulnerable, neutral and ordinary resistance all answer false.
     */
    @Test
    @DisplayName("vulnerable, neutral and resistant are all not immune")
    void levelsBelowThreeAreNotImmune() throws Exception {
        for (int level : new int[]{-1, 0, 1, 2}) {
            state.setResLevel(ElementEnum.ELEM_COLD, level);
            assertFalse(isImmune(ElementEnum.ELEM_COLD), "level " + level + " should not be immune");
        }
    }

    /**
     * The boundary that separates C's equality from a "3 or better" test. Nothing in the game raises
     * a player's level past 3, but the comparison the C makes is an exact one, so a level of 4 is
     * not immunity.
     */
    @Test
    @DisplayName("a level above 3 is not immunity, since C tests equality")
    void levelAboveThreeIsNotImmune() throws Exception {
        state.setResLevel(ElementEnum.ELEM_ACID, 4);
        assertFalse(isImmune(ElementEnum.ELEM_ACID));
    }

    /**
     * A fresh state is all zeroes, so no element is immune before anything has been calculated.
     */
    @Test
    @DisplayName("a wiped state is immune to nothing")
    void wipedStateIsImmuneToNothing() throws Exception {
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            assertFalse(isImmune(element), element + " should not be immune");
        }
    }

    /**
     * Each element is asked about independently — C indexes the array by element, so immunity to one
     * says nothing about its neighbours.
     */
    @Test
    @DisplayName("immunity is per element")
    void immunityIsPerElement() throws Exception {
        state.setResLevel(ElementEnum.ELEM_POIS, 3);
        assertTrue(isImmune(ElementEnum.ELEM_POIS));
        assertFalse(isImmune(ElementEnum.ELEM_FIRE));
        assertFalse(isImmune(ElementEnum.ELEM_DARK));
    }

    /**
     * The reading comes from the calculated state, not the known state — C's {@code p->state}. The
     * known state is left untouched here and must not change the answer.
     */
    @Test
    @DisplayName("reads the calculated state, not the known state")
    void readsCalculatedStateNotKnownState() throws Exception {
        PlayerState known = new PlayerState();
        Field field = Player.class.getDeclaredField("knownState");
        field.setAccessible(true);
        field.set(player, known);

        state.setResLevel(ElementEnum.ELEM_ELEC, 3);
        assertEquals(0, known.getResLevel(ElementEnum.ELEM_ELEC));
        assertTrue(isImmune(ElementEnum.ELEM_ELEC));
    }
}
