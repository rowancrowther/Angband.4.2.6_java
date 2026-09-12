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

package uk.co.jackoftradesltd.frontend.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetConfig;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link UIPlayer#haveValidCharSheetConfig()} against C's
 * {@code have_valid_char_sheet_config} ({@code [C] src/ui-player.c:146-156}).
 *
 * <p>C's check has exactly two outcomes to prove: no config at all fails outright, and an
 * existing config fails the moment its cached {@code res_cols} stops matching
 * {@code res_nlabel + 1 + player->body.count}. Both fields and the method itself are private, so
 * every test reaches them by reflection rather than through a port-provided setter that does not
 * exist.
 *
 * <p>The interesting case is the one C's own comment calls out: a body-count change after the
 * layout was cached, standing in for a race switch mid-game. The humanoid body from
 * {@link SeededPlayerRegistry#humanoidBody()} gives a known count of twelve slots for the
 * ordinary-path tests; the staleness test swaps that for a differently-sized body once a config
 * has already been cached against the original.
 *
 * <p>Class UIPlayerTest coded on 260911, commented in full on 260911.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class UIPlayerTest {

    /**
     * The object under test.
     */
    private UIPlayer uiPlayer;

    /**
     * The current player, twelve equipment slots per {@link SeededPlayerRegistry#humanoidBody()}.
     */
    private Player player;

    /**
     * A fresh player and a fresh {@link UIPlayer}, with the player installed as the current game's
     * player so {@code GameState.getPlayer().getPlayerBody().getCount()} resolves the way C's
     * {@code player->body.count} does.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @BeforeEach
    void setUp() throws Exception {
        uiPlayer = new UIPlayer();
        player = CalcBonusesFixture.plainCharacter().player();
        GameState.setPlayer(player);
    }

    /**
     * Clears the player back out of the shared game state so this test cannot leak one into a
     * class that runs after it.
     */
    @AfterEach
    void tearDown() {
        GameState.setPlayer(null);
    }

    /**
     * Writes {@link UIPlayer}'s private {@code cachedConfig} field.
     *
     * @param config the config to install, or {@code null}
     * @throws Exception if the field cannot be reached
     */
    private void setCachedConfig(CharSheetConfig config) throws Exception {
        Field field = UIPlayer.class.getDeclaredField("cachedConfig");
        field.setAccessible(true);
        field.set(uiPlayer, config);
    }

    /**
     * Builds a {@link CharSheetConfig} with only the two fields the check reads.
     *
     * @param resNLabel the cached label width
     * @param resCols   the cached resistance-panel column count
     * @return the config
     * @throws Exception if a field cannot be reached
     */
    private CharSheetConfig configWith(int resNLabel, int resCols) throws Exception {
        CharSheetConfig config = new CharSheetConfig();
        Field nLabelField = CharSheetConfig.class.getDeclaredField("resNLabel");
        nLabelField.setAccessible(true);
        nLabelField.set(config, resNLabel);
        Field colsField = CharSheetConfig.class.getDeclaredField("resCols");
        colsField.setAccessible(true);
        colsField.set(config, resCols);
        return config;
    }

    /**
     * Replaces the current player's body with one of a chosen slot count, the stand-in for a race
     * switch changing {@code player->body.count} out from under a cached layout.
     *
     * @param slotCount the number of slots the replacement body should have
     * @throws Exception if the field cannot be reached
     */
    private void setPlayerBodySlotCount(int slotCount) throws Exception {
        List<EquipSlot> slots = new ArrayList<>();
        for (int i = 0; i < slotCount; i++) {
            slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "slot" + i));
        }
        PlayerBody body = new PlayerBody("Test", slots);
        Field field = Player.class.getDeclaredField("body");
        field.setAccessible(true);
        field.set(player, body);
    }

    /**
     * Invokes the private method under test.
     *
     * @return its result
     * @throws Exception if the method cannot be reached or throws
     */
    private boolean invoke() throws Exception {
        Method method = UIPlayer.class.getDeclaredMethod("haveValidCharSheetConfig");
        method.setAccessible(true);
        return (boolean) method.invoke(uiPlayer);
    }

    /**
     * No layout has ever been cached — C's {@code !cached_config} branch.
     */
    @Nested
    @DisplayName("with no cached config")
    class NoCachedConfig {

        /**
         * A freshly constructed {@link UIPlayer} starts with a {@code null} cache and reports it
         * as invalid, without looking at the player at all.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("reports invalid")
        void reportsInvalid() throws Exception {
            assertFalse(invoke());
        }
    }

    /**
     * A cached layout whose {@code res_cols} still agrees with the current player's body.
     */
    @Nested
    @DisplayName("with a cache matching the current body")
    class MatchingCache {

        /**
         * The ordinary path: {@code res_cols} equal to {@code res_nlabel + 1 + body.count} for the
         * twelve-slot humanoid body reports valid, exactly as C's equality test would.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reports valid")
        void reportsValid() throws Exception {
            setCachedConfig(configWith(6, 6 + 1 + 12));

            assertTrue(invoke());
        }

        /**
         * {@code res_nlabel} of zero is not special-cased in C's formula, so a config built with a
         * zero label width and the matching column count is just as valid as any other.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reports valid when res_nlabel is zero")
        void reportsValidWithZeroLabelWidth() throws Exception {
            setCachedConfig(configWith(0, 0 + 1 + 12));

            assertTrue(invoke());
        }
    }

    /**
     * A cached layout whose {@code res_cols} no longer agrees with the current player's body.
     */
    @Nested
    @DisplayName("with a cache that disagrees with the current body")
    class MismatchedCache {

        /**
         * One column too many fails the equality test, the same as it would in C.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reports invalid when res_cols is one too high")
        void reportsInvalidWhenTooHigh() throws Exception {
            setCachedConfig(configWith(6, 6 + 1 + 12 + 1));

            assertFalse(invoke());
        }

        /**
         * One column too few fails the same way.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reports invalid when res_cols is one too low")
        void reportsInvalidWhenTooLow() throws Exception {
            setCachedConfig(configWith(6, 6 + 1 + 12 - 1));

            assertFalse(invoke());
        }

        /**
         * The case C's own comment names: the layout was cached for the player's body as it stood,
         * then the body changed shape — standing in for a race switch mid-game, which can bring a
         * different equipment slot count. The cache is now stale and must fail even though nothing
         * about the config itself was touched.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("reports invalid once the player's body slot count changes underneath it")
        void reportsInvalidAfterBodyChanges() throws Exception {
            setCachedConfig(configWith(6, 6 + 1 + 12));
            assertTrue(invoke());

            setPlayerBodySlotCount(3);

            assertFalse(invoke());
        }
    }
}
