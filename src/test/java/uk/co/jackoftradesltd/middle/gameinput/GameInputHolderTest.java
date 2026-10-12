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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.cave.Loc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GameInputHolder}, the Java home of C's file-scope {@code get_*_hook} pointers in
 * {@code game-input.c}.
 *
 * <p>The expected values come from C, not from the Java. In C every hook starts {@code NULL}, and
 * each wrapper then falls back: {@code get_check} returns {@code false}, {@code get_string},
 * {@code get_quantity}, {@code get_item} and the like report an abort, {@code get_effect_from_list}
 * returns {@code allow_random ? -2 : -1}, and {@code panel_contains} and {@code map_is_visible}
 * return {@code true}. A hook installed by {@code textui_input_init()} replaces the fall-back, and
 * the C tests ({@code tests/monster/desc.c}) override a single hook, {@code panel_contains_hook},
 * leaving the rest unset. The holder's job is to reproduce that: unset at start, replaceable,
 * restorable, and never {@code null}.
 *
 * <p>Class GameInputHolderTest coded on 261012, commented in full on 261012.
 */
class GameInputHolderTest {

    /**
     * A fake that overrides only the panel hook, as the C tests do with {@code panel_contains_hook};
     * every other request still takes the absent-hook fall-back.
     */
    private static final class NeverOnPanelInput extends DefaultGameInput {
        @Override
        public boolean panelContains(Loc location) {
            return false;
        }
    }

    @BeforeEach
    void startUnset() {
        GameInputHolder.resetInstance();
    }

    @AfterEach
    void restoreDefault() {
        GameInputHolder.resetInstance();
    }

    @Test
    @DisplayName("with nothing installed the instance is a DefaultGameInput, never null")
    void startsAsDefault() {
        GameInput input = GameInputHolder.getInstance();

        assertNotNull(input);
        assertInstanceOf(DefaultGameInput.class, input);
    }

    @Test
    @DisplayName("unset hooks take C's absent-hook fall-backs: decline, abort, or true for the panel pair")
    void unsetHooksFallBackAsInC() {
        GameInput input = GameInputHolder.getInstance();

        // get_check: false; the value getters: false / 0 / -1, which the port reports as empty.
        assertFalse(input.getCheck("Sure? "));
        assertEquals(Optional.empty(), input.getCom("Command: "));
        assertEquals(Optional.empty(), input.getRepDir(true));
        assertEquals(Optional.empty(), input.getAimDir());
        assertEquals(Optional.empty(), input.getQuantity("Quantity: ", 10));
        assertEquals(Optional.empty(), input.getString("Name: ", "default"));
        assertEquals(Optional.empty(), input.getCurse(null, ""));
        assertEquals(Optional.empty(), input.getPanel());

        // panel_contains and map_is_visible are the two fall-backs that return true.
        assertTrue(input.panelContains(Loc.zero));
        assertTrue(input.mapIsVisible());
    }

    @Test
    @DisplayName("get_effect_from_list falls back to random (-2) when allowed, otherwise aborted (-1)")
    void effectFromListFallBack() {
        GameInput input = GameInputHolder.getInstance();

        assertInstanceOf(EffectChoice.Random.class, input.getEffectFromList(null, List.of(), -1, true));
        assertInstanceOf(EffectChoice.Aborted.class, input.getEffectFromList(null, List.of(), -1, false));
    }

    @Test
    @DisplayName("setInstance makes getInstance return that exact object")
    void setInstanceInstalls() {
        GameInput fake = new NeverOnPanelInput();

        GameInputHolder.setInstance(fake);

        assertSame(fake, GameInputHolder.getInstance());
    }

    @Test
    @DisplayName("an installed fake replaces the fall-back for its hook only, as in the C tests")
    void installedFakeOverridesOneHook() {
        GameInputHolder.setInstance(new NeverOnPanelInput());

        GameInput input = GameInputHolder.getInstance();
        assertFalse(input.panelContains(Loc.row(4).col(3)));
        // The hooks the fake did not touch keep their fall-backs.
        assertTrue(input.mapIsVisible());
        assertFalse(input.getCheck("Sure? "));
    }

    @Test
    @DisplayName("installing a second implementation replaces the first")
    void secondInstallReplacesFirst() {
        GameInput first = new NeverOnPanelInput();
        GameInput second = new DefaultGameInput();

        GameInputHolder.setInstance(first);
        GameInputHolder.setInstance(second);

        assertSame(second, GameInputHolder.getInstance());
    }

    @Test
    @DisplayName("setInstance(null) throws IllegalArgumentException and leaves the installed instance alone")
    void nullIsRejectedAndInstanceKept() {
        GameInput fake = new NeverOnPanelInput();
        GameInputHolder.setInstance(fake);

        assertThrows(IllegalArgumentException.class, () -> GameInputHolder.setInstance(null));

        assertSame(fake, GameInputHolder.getInstance());
    }

    @Test
    @DisplayName("resetInstance discards the fake and restores a DefaultGameInput")
    void resetRestoresDefault() {
        GameInput fake = new NeverOnPanelInput();
        GameInputHolder.setInstance(fake);

        GameInputHolder.resetInstance();

        GameInput restored = GameInputHolder.getInstance();
        assertNotSame(fake, restored);
        assertInstanceOf(DefaultGameInput.class, restored);
        assertTrue(restored.panelContains(Loc.row(4).col(3)));
    }
}
