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

package uk.co.jackoftradesltd.middle.gameinput;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link DefaultGameInput}, the port of the absent-hook paths of {@code game-input.c}.
 *
 * <p>The expected values come from the C wrappers with every {@code *_hook} pointer {@code NULL},
 * not from the Java: {@code get_string}, {@code get_check}, {@code get_com}, {@code get_rep_dir},
 * {@code get_aim_dir}, {@code get_item} and {@code get_curse} return {@code false};
 * {@code get_quantity} returns {@code 0}; {@code get_spell_from_book} and {@code get_spell} return
 * {@code -1}; {@code get_effect_from_list} returns {@code allow_random ? -2 : -1};
 * {@code panel_contains} and {@code map_is_visible} return {@code true}; and the {@code void}
 * {@code get_panel} and {@code view_ability_menu} do nothing. The {@code 0}, {@code -1} and
 * {@code false} returns all become {@link Optional#empty()} in the port.
 *
 * <p>{@code GameInputHolderTest} already reaches the default through the holder for a few of
 * these; this class tests the default directly and covers every hook, including the arguments that
 * C's wrappers ignore.
 *
 * <p>Class DefaultGameInputTest coded on 261014, commented in full on 261014.
 */
class DefaultGameInputTest {

    private final DefaultGameInput input = new DefaultGameInput();

    @Test
    @DisplayName("get_rep_dir and get_aim_dir return false, so no direction, whether or not 5 is allowed")
    void directionsAbort() {
        assertEquals(Optional.<DirectionEnum>empty(), input.getRepDir(true));
        assertEquals(Optional.<DirectionEnum>empty(), input.getRepDir(false));
        assertEquals(Optional.<DirectionEnum>empty(), input.getAimDir());
    }

    @Test
    @DisplayName("get_item returns false for any prompt, filter and mode, leaving no choice")
    void itemAborts() {
        assertEquals(Optional.empty(), input.getItem("Drop which item? ", "You have nothing to drop.",
                CommandCode.CMD_NULL, null, null));
        assertEquals(Optional.empty(), input.getItem(null, null, null, item -> true, null));
    }

    @Test
    @DisplayName("get_quantity returns 0 (cancelled) at the minimum, ordinary and large maxima")
    void quantityAborts() {
        assertEquals(Optional.<Integer>empty(), input.getQuantity("Quantity: ", 1));
        assertEquals(Optional.<Integer>empty(), input.getQuantity("Quantity: ", 10));
        assertEquals(Optional.<Integer>empty(), input.getQuantity(null, Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("get_string returns false: empty, not the default text and not an empty string")
    void stringAborts() {
        assertEquals(Optional.<String>empty(), input.getString("Name: ", "default"));
        assertEquals(Optional.<String>empty(), input.getString("Name: ", ""));
        assertEquals(Optional.<String>empty(), input.getString("Name: ", null));
    }

    @Test
    @DisplayName("get_spell_from_book and get_spell return -1, so no spell and no book")
    void spellsAbort() {
        assertEquals(Optional.empty(), input.getSpellFromBook(null, "cast", null, "No spells.", null));
        assertEquals(Optional.empty(), input.getSpell(null, "study", null, CommandCode.CMD_NULL,
                "No books.", null, "No spells.", null));
    }

    @Test
    @DisplayName("get_effect_from_list returns -2 (random) when allowed, else -1 (aborted), never an index")
    void effectFromListFallBack() {
        // allow_random set: -2.
        assertInstanceOf(EffectChoice.Random.class, input.getEffectFromList(null, List.of(), -1, true));
        assertInstanceOf(EffectChoice.Random.class, input.getEffectFromList("Which? ", null, 0, true));
        assertInstanceOf(EffectChoice.Random.class, input.getEffectFromList("Which? ", List.of(), 5, true));

        // allow_random clear: -1.
        assertInstanceOf(EffectChoice.Aborted.class, input.getEffectFromList(null, List.of(), -1, false));
        assertInstanceOf(EffectChoice.Aborted.class, input.getEffectFromList("Which? ", null, 0, false));
        assertInstanceOf(EffectChoice.Aborted.class, input.getEffectFromList("Which? ", List.of(), 5, false));
    }

    @Test
    @DisplayName("get_check returns false for any prompt")
    void checkDeclines() {
        assertFalse(input.getCheck("Are you sure? "));
        assertFalse(input.getCheck(""));
        assertFalse(input.getCheck(null));
    }

    @Test
    @DisplayName("get_com returns false, so no keypress is offered")
    void comAborts() {
        assertEquals(Optional.<Character>empty(), input.getCom("Command: "));
        assertEquals(Optional.<Character>empty(), input.getCom(null));
    }

    @Test
    @DisplayName("get_curse returns false, so no curse index")
    void curseAborts() {
        assertEquals(Optional.<Integer>empty(), input.getCurse(null, ""));
        assertEquals(Optional.<Integer>empty(), input.getCurse(null, "3d4"));
    }

    @Test
    @DisplayName("get_panel does nothing, so no bounds are reported")
    void panelReportsNothing() {
        assertEquals(Optional.<GameInput.Rectangle>empty(), input.getPanel());
    }

    @Test
    @DisplayName("panel_contains returns true for every grid, corners and far outside included")
    void panelContainsEverything() {
        assertTrue(input.panelContains(Loc.zero));
        assertTrue(input.panelContains(Loc.row(4).col(3)));
        assertTrue(input.panelContains(Loc.row(65).col(197)));
        assertTrue(input.panelContains(Loc.row(10000).col(10000)));
    }

    @Test
    @DisplayName("map_is_visible returns true")
    void mapIsVisible() {
        assertTrue(input.mapIsVisible());
    }

    @Test
    @DisplayName("view_ability_menu does nothing: no throw, and the list is left as it was")
    void viewAbilityMenuIsNoOp() {
        List<PlayerProperty> abilities = new ArrayList<>();
        assertDoesNotThrow(() -> input.viewAbilityMenu(abilities));
        assertTrue(abilities.isEmpty());

        List<PlayerProperty> withNull = new ArrayList<>(Arrays.asList(null, null));
        assertDoesNotThrow(() -> input.viewAbilityMenu(withNull));
        assertEquals(2, withNull.size());
    }

    @Test
    @DisplayName("confirm_debug is not overridden: it is the interface's generic fall-back")
    void confirmDebugIsInherited() throws NoSuchMethodException {
        Method method = GameInput.class.getMethod("confirmDebug");

        assertTrue(method.isDefault());
        for (Method declared : DefaultGameInput.class.getDeclaredMethods()) {
            assertNotEquals("confirmDebug", declared.getName());
        }
    }

    @Test
    @DisplayName("the default holds no state: separate instances answer alike and repeat calls agree")
    void stateless() {
        DefaultGameInput other = new DefaultGameInput();

        assertEquals(input.getCheck("x"), other.getCheck("x"));
        assertEquals(input.panelContains(Loc.zero), other.panelContains(Loc.zero));
        assertEquals(input.getQuantity("q", 3), input.getQuantity("q", 3));
        assertInstanceOf(EffectChoice.Random.class, other.getEffectFromList(null, List.of(), -1, true));
    }
}
