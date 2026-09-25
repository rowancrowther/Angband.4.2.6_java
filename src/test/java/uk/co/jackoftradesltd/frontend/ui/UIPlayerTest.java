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
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.messages.data.PlayerStatusView;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    /**
     * Tests {@link UIPlayer#getUIEntryLabel(UIEntry, int, boolean, String)} against C's
     * {@code get_ui_entry_label} ({@code [C] src/ui-entry.c:339-387}).
     *
     * <p>Expected values are derived from the C source directly: the no-op guard at
     * {@code ui-entry.c:354-356}, the empty-buffer case at {@code ui-entry.c:357-360}, the
     * shortened/full label cutoff at {@code ui-entry.c:361-367} (against
     * {@code UIRegistry.MAX_SHORTENED == 10}), the {@code length - 1 - n} padding counts at
     * {@code ui-entry.c:372,379}, and the {@code length - 1} truncation width at
     * {@code ui-entry.c:384}. The method is private, so every test reaches it by reflection.
     *
     * <p>Class GetUIEntryLabel coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getUIEntryLabel")
    class GetUIEntryLabel {

        /**
         * Builds a minimal {@link UIEntry} carrying only the default-width label the test needs;
         * every other constructor argument is irrelevant to label formatting.
         *
         * @param label the default-width label text
         * @return the entry
         */
        private UIEntry entryWithLabel(String label) {
            return new UIEntry("test", null, null, null, null, null, List.of(), 0, null, null,
                    "desc", label, null, null);
        }

        /**
         * Invokes the private method under test.
         *
         * @param entry   the entry to format a label for
         * @param length  the desired content width, including C's reserved terminator slot
         * @param padLeft which side to pad on when the source text is shorter than requested
         * @param label   the fallback value for a zero-or-less length
         * @return the formatted label
         * @throws Exception if the method cannot be reached or throws
         */
        private String invoke(UIEntry entry, int length, boolean padLeft, String label) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("getUIEntryLabel", UIEntry.class,
                    int.class, boolean.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(uiPlayer, entry, length, padLeft, label);
        }

        /**
         * A {@code length} of zero is C's no-op guard ({@code length <= 0}) — the fallback
         * {@code label} comes back unchanged and the entry is never consulted.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the fallback label unchanged when length is zero")
        void returnsFallbackWhenLengthZero() throws Exception {
            assertEquals("unchanged", invoke(entryWithLabel("Fire"), 0, true, "unchanged"));
        }

        /**
         * A negative {@code length} hits the same C guard as zero.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the fallback label unchanged when length is negative")
        void returnsFallbackWhenLengthNegative() throws Exception {
            assertEquals("unchanged", invoke(entryWithLabel("Fire"), -3, true, "unchanged"));
        }

        /**
         * A {@code length} of one stands in for C's single null-only buffer.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the empty string when length is one")
        void returnsEmptyWhenLengthOne() throws Exception {
            assertEquals("", invoke(entryWithLabel("Fire"), 1, true, "unchanged"));
        }

        /**
         * {@code length <= MAX_SHORTENED + 1} selects the shortened-label slot at index
         * {@code length - 2}, then pads it on the left with {@code length - 1 - n} spaces.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("uses the shortened label and left-pads when length is within MAX_SHORTENED + 1")
        void usesShortenedLabelWithLeftPadding() throws Exception {
            UIEntry entry = entryWithLabel("should not be used");
            entry.setShortenedLabel(2, "Ac");

            assertEquals(" Ac", invoke(entry, 4, true, "unused"));
        }

        /**
         * Same shortened-label selection, right-padded instead: {@code length - 1 - n} trailing
         * spaces rather than leading ones.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("right-pads when padLeft is false")
        void usesShortenedLabelWithRightPadding() throws Exception {
            UIEntry entry = entryWithLabel("should not be used");
            entry.setShortenedLabel(2, "Ac");

            assertEquals("Ac ", invoke(entry, 4, false, "unused"));
        }

        /**
         * At the boundary {@code length == MAX_SHORTENED + 1 == 11}, the shortened-label slot at
         * index 9 is still used, not the default-width label.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("still uses the shortened label at the MAX_SHORTENED + 1 boundary")
        void usesShortenedLabelAtBoundary() throws Exception {
            UIEntry entry = entryWithLabel("should not be used");
            entry.setShortenedLabel(9, "Confuse");

            assertEquals("   Confuse", invoke(entry, 11, true, "unused"));
        }

        /**
         * One past the boundary, {@code length == MAX_SHORTENED + 2 == 12}, switches to the
         * default-width label.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("uses the default-width label just past the MAX_SHORTENED + 1 boundary")
        void usesDefaultLabelPastBoundary() throws Exception {
            UIEntry entry = entryWithLabel("Nether");

            assertEquals("     Nether", invoke(entry, 12, true, "unused"));
        }

        /**
         * The source text is longer than the requested content width ({@code numChars >=
         * length - 1}), so it is truncated to exactly {@code length - 1} characters.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("truncates to length minus one when the source text is too long")
        void truncatesWhenSourceTooLong() throws Exception {
            UIEntry entry = entryWithLabel("should not be used");
            entry.setShortenedLabel(2, "Poison");

            assertEquals("Poi", invoke(entry, 4, true, "unused"));
        }

        /**
         * The boundary between padding and truncation: source text exactly {@code length - 1}
         * characters long takes the truncation branch in C ({@code n < length - 1} is false) but
         * comes back whole, since truncating to its own length changes nothing.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the source text unchanged when it exactly fills length minus one")
        void returnsWholeTextAtExactWidth() throws Exception {
            UIEntry entry = entryWithLabel("should not be used");
            entry.setShortenedLabel(3, "Acid");

            assertEquals("Acid", invoke(entry, 5, true, "unused"));
        }
    }

    /**
     * Tests {@link UIPlayer#configureCharSheet()} against C's {@code configure_char_sheet}
     * ({@code [C] src/ui-player.c:186-266}).
     *
     * <p>Expected values are derived from the C source directly: the {@code STAT_MAX} clamp on
     * stat-modifier entries ({@code ui-player.c:211-213}), the per-region column/row/width
     * formulas and the 22/20 row-fit clamp ({@code ui-player.c:230-244}), and the requirement
     * that {@code resists_by_region[i]} retain every matching entry for region {@code i}, not
     * just the last one ({@code ui-player.c:246-253}). The method and {@link UIPlayer#cachedConfig}
     * are both private, so every test reaches them by reflection. {@link UIRegistry}'s entries are
     * reset to empty before and after each test, the same convention {@code UIEntryCodeTest} uses,
     * rather than saved and restored — {@link UIRegistry#getUIEntries()} throws if the registry's
     * backing list is ever read before something has called {@link UIRegistry#setUIEntries}, so
     * capturing "whatever was there before" is not safe this early in a suite run.
     * {@link PlayerEventStatusUpdate}'s cached body count has no such hazard and is saved and
     * restored properly, since it is process-wide state this method also reads.
     *
     * <p>Class ConfigureCharSheet coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("configureCharSheet")
    class ConfigureCharSheet {

        private PlayerStatusView savedStatusView;

        /**
         * Saves the player-status cache so this test's body count cannot leak into another test,
         * then clears the UI-entry registry.
         */
        @BeforeEach
        void saveGlobalState() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            UIRegistry.setUIEntries(List.of());
        }

        /**
         * Restores the player-status cache saved by {@link #saveGlobalState()} and clears the
         * UI-entry registry back to empty.
         */
        @AfterEach
        void restoreGlobalState() {
            UIRegistry.setUIEntries(List.of());
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
        }

        /**
         * Builds a UI entry carrying {@code categories}, with its shortened label at index 4 (the
         * slot {@code getUIEntryLabel} reads for the resistance panel's fixed {@code resNlabel} of
         * 6) set to {@code shortLabel}.
         *
         * @param shortLabel the four-index shortened-label text
         * @param categories the categories this entry belongs to
         * @return the entry
         */
        private UIEntry entry(String shortLabel, UIEntryCategory... categories) {
            return new UIEntry("entry", null, null, null, null, null, List.of(categories), 0, null,
                    new Flag<>(ChannelEntryFlag.class), "test", "unused", shortLabel, "unused2");
        }

        /**
         * @return the {@code "CHAR_SCREEN1"} category every {@code configureCharSheet} predicate
         * requires alongside a region- or stat-modifier-specific one
         */
        private UIEntryCategory charScreen1() {
            return new UIEntryCategory("CHAR_SCREEN1", 0, false);
        }

        /**
         * @param name     the category name
         * @param priority its explicit sort priority
         * @return the category
         */
        private UIEntryCategory category(String name, int priority) {
            return new UIEntryCategory(name, priority, true);
        }

        /**
         * Invokes the private method under test.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        private void invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("configureCharSheet");
            method.setAccessible(true);
            method.invoke(uiPlayer);
        }

        /**
         * Reads {@link UIPlayer}'s private {@code cachedConfig} field after {@link #invoke()}.
         *
         * @return the built config
         * @throws Exception if the field cannot be reached
         */
        private CharSheetConfig cachedConfig() throws Exception {
            Field field = UIPlayer.class.getDeclaredField("cachedConfig");
            field.setAccessible(true);
            return (CharSheetConfig) field.get(uiPlayer);
        }

        /**
         * Reads {@link CharSheetConfig}'s private {@code nStatModEntries} field, which has no
         * public getter.
         *
         * @param config the config to read
         * @return how many stat-modifier entries it holds
         * @throws Exception if the field cannot be reached
         */
        private int nStatModEntries(CharSheetConfig config) throws Exception {
            Field field = CharSheetConfig.class.getDeclaredField("nStatModEntries");
            field.setAccessible(true);
            return (int) field.get(config);
        }

        /**
         * The ordinary path: a handful of entries spread across the stat-modifier list and three
         * of the four resistance-panel regions, with a known player body count. Every region's
         * column/row/width/pageRows formula and entry count is checked against C's formulas
         * directly, and — the case this method's fixes were about — every individual entry within
         * the three-entry "resistances" region is confirmed present with its own label, not just
         * the last one written.
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("lays out regions and retains every entry within a region")
        void laysOutRegionsAndRetainsEveryEntry() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusBodyCount(12);
            UIRegistry.setUIEntries(List.of(
                    entry("s1", charScreen1(), category("stat_modifiers", 1)),
                    entry("s2", charScreen1(), category("stat_modifiers", 2)),
                    entry("Ac", charScreen1(), category("resistances", 3)),
                    entry("El", charScreen1(), category("resistances", 2)),
                    entry("Fi", charScreen1(), category("resistances", 1)),
                    entry("a1", charScreen1(), category("abilities", 1)),
                    entry("m1", charScreen1(), category("modifiers", 1))
            ));

            invoke();
            CharSheetConfig config = cachedConfig();

            assertEquals(2, nStatModEntries(config));

            assertEquals(6, config.getResNLabel());
            assertEquals(6 + 1 + 12, config.getResCols());

            for (int region = 0; region < 4; region++) {
                assertEquals(region * (config.getResCols() + 1), config.getResRegion(region).getCol());
                assertEquals(2 + 5, config.getResRegion(region).getRow());
                assertEquals(config.getResCols(), config.getResRegion(region).getWidth());
            }

            assertEquals(3, config.getnResistsByRegion(0));
            assertEquals(1, config.getnResistsByRegion(1));
            assertEquals(0, config.getnResistsByRegion(2));
            assertEquals(1, config.getnResistsByRegion(3));

            assertEquals(3, config.getResRows());
            for (int region = 0; region < 4; region++) {
                assertEquals(5, config.getResRegion(region).getPageRows());
            }

            assertEquals("   Ac:", config.getResistsByRegion(0, 0).getLabel());
            assertEquals("   El:", config.getResistsByRegion(0, 1).getLabel());
            assertEquals("   Fi:", config.getResistsByRegion(0, 2).getLabel());
            assertEquals("   a1:", config.getResistsByRegion(1, 0).getLabel());
            assertEquals("   m1:", config.getResistsByRegion(3, 0).getLabel());
        }

        /**
         * More than {@code STAT_MAX} matching stat-modifier entries are clamped to
         * {@code STAT_MAX}, mirroring C's {@code if (n > STAT_MAX) n = STAT_MAX;}
         * ({@code [C] ui-player.c:211-213}).
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("clamps stat-modifier entries to STAT_MAX")
        void clampsStatModEntriesToStatMax() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusBodyCount(0);
            List<UIEntry> entries = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                entries.add(entry("s" + i, charScreen1(), category("stat_modifiers", i)));
            }
            UIRegistry.setUIEntries(entries);

            invoke();

            assertEquals(5, nStatModEntries(cachedConfig()));
        }

        /**
         * A region whose matching entry count would overflow row 22 once the two rows of chrome
         * below it are added is shortened to {@code 20 - row} instead of kept at its full count —
         * the same 22/20 bounds C applies ({@code [C] ui-player.c:238-244}). With
         * {@code row == 2 + STAT_MAX == 7}, twenty matching entries clamp to
         * {@code 20 - 7 == 13}.
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("clamps a region's entry count to fit the display")
        void clampsRegionEntryCountToFitDisplay() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusBodyCount(0);
            List<UIEntry> entries = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                entries.add(entry("r" + i, charScreen1(), category("resistances", i)));
            }
            UIRegistry.setUIEntries(entries);

            invoke();
            CharSheetConfig config = cachedConfig();

            assertEquals(13, config.getnResistsByRegion(0));
            assertEquals(13, config.getResRows());
            assertEquals(15, config.getResRegion(0).getPageRows());
        }

        /**
         * An entry carrying only the region's own category, not {@code "CHAR_SCREEN1"}, is not a
         * match — C's predicate requires both categories ({@code check_for_two_categories},
         * {@code [C] ui-player.c:176-183}).
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("excludes entries missing the CHAR_SCREEN1 category")
        void excludesEntriesMissingCharScreen1() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusBodyCount(0);
            UIRegistry.setUIEntries(List.of(entry("no", category("resistances", 1))));

            invoke();

            assertEquals(0, cachedConfig().getnResistsByRegion(0));
        }
    }
}
