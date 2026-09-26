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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.messages.data.PlayerCharSheetView;
import uk.co.jackoftradesltd.channel.messages.data.PlayerStatusView;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.TermData;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.frontend.ui.output.Region;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetConfig;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        private PlayerCharSheetView savedCharSheetView;

        /**
         * Saves the char-sheet cache so this test's body count cannot leak into another test,
         * then clears the UI-entry registry.
         */
        @BeforeEach
        void saveGlobalState() {
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
            UIRegistry.setUIEntries(List.of());
        }

        /**
         * Restores the char-sheet cache saved by {@link #saveGlobalState()} and clears the
         * UI-entry registry back to empty.
         */
        @AfterEach
        void restoreGlobalState() {
            UIRegistry.setUIEntries(List.of());
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
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
            PlayerEventStatusUpdate.updatePlayerCharSheetBodyCount(12);
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
            PlayerEventStatusUpdate.updatePlayerCharSheetBodyCount(0);
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
            PlayerEventStatusUpdate.updatePlayerCharSheetBodyCount(0);
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
            PlayerEventStatusUpdate.updatePlayerCharSheetBodyCount(0);
            UIRegistry.setUIEntries(List.of(entry("no", category("resistances", 1))));

            invoke();

            assertEquals(0, cachedConfig().getnResistsByRegion(0));
        }
    }

    /**
     * Tests {@link UIPlayer#cnvStat(int)} against C's {@code cnv_stat}
     * ({@code [C] src/ui-display.c:117-132}).
     *
     * <p>Expected values are derived from the C source directly: the plain {@code "    %2d"} form
     * at or below 18 ({@code ui-display.c:129-130}), the {@code "18/***"} literal at a bonus of
     * 220 or more ({@code ui-display.c:123-124}), the inclusive {@code bonus >= 100} threshold for
     * the three-digit {@code "18/%03d"} form ({@code ui-display.c:125-126}), and the padded
     * {@code " 18/%02d"} form below that ({@code ui-display.c:127-128}). The method is private, so
     * every test reaches it by reflection.
     *
     * <p>Class CnvStat coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("cnvStat")
    class CnvStat {

        /**
         * Invokes the private method under test.
         *
         * @param stat the raw stat value to format
         * @return the formatted string
         * @throws Exception if the method cannot be reached or throws
         */
        private String invoke(int stat) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("cnvStat", int.class);
            method.setAccessible(true);
            return (String) method.invoke(uiPlayer, stat);
        }

        /**
         * A stat at 18 or below takes C's plain {@code "    %2d"} branch, unrelated to the bonus
         * formatting below it.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("formats a stat of 18 or below as a plain right-justified number")
        void formatsPlainNumberAtOrBelowEighteen() throws Exception {
            assertEquals("     3", invoke(3));
            assertEquals("    18", invoke(18));
        }

        /**
         * A bonus below 100 pads to two digits with a leading space before the slash.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("pads a bonus below 100 to two digits")
        void padsBonusBelowOneHundred() throws Exception {
            assertEquals(" 18/01", invoke(19));
            assertEquals(" 18/99", invoke(117));
        }

        /**
         * The inclusive threshold C tests with {@code bonus >= 100}: a bonus of exactly 100 takes
         * the three-digit branch, not the padded one.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("switches to three digits at a bonus of exactly 100")
        void switchesToThreeDigitsAtOneHundred() throws Exception {
            assertEquals("18/100", invoke(118));
        }

        /**
         * A bonus above 100 continues in the three-digit form up to the {@code "18/***"} cutoff.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("formats a bonus above 100 with three digits")
        void formatsBonusAboveOneHundred() throws Exception {
            assertEquals("18/101", invoke(119));
            assertEquals("18/219", invoke(237));
        }

        /**
         * The inclusive {@code bonus >= 220} cutoff collapses to the literal {@code "18/***"}, and
         * stays there for anything higher.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("collapses to the literal cap at a bonus of 220 or more")
        void collapsesToCapAtTwoHundredTwenty() throws Exception {
            assertEquals("18/***", invoke(238));
            assertEquals("18/***", invoke(300));
        }
    }

    /**
     * Tests {@link UIPlayer#displayPlayerStatInfo()} against C's {@code display_player_stat_info}
     * ({@code [C] src/ui-player.c:450-510}).
     *
     * <p>Expected values are derived from the C source directly: the reduced/normal name choice
     * and the {@code 18 + 100} natural-maximum marker both gated on
     * {@code stat_cur[i] < stat_max[i]} ({@code ui-player.c:472-482}), the {@code cnv_stat}
     * formatting of the natural and modified maximum columns ({@code ui-player.c:485-486,
     * 500-502}), the {@code "%+3d"} race/class/equipment bonus columns
     * ({@code ui-player.c:488-498}), and the drained-only {@code stat_use} column gated on the
     * same comparison a second time ({@code ui-player.c:504-508}). The method is private, so
     * every test reaches it by reflection, and cell contents are read back from the
     * {@link CellGrid} a hand-built {@link Term}/{@link TermData} pair writes into, the same
     * fixture shape {@code TermPutStrTest} uses.
     *
     * <p>The five fixture stats are chosen to each exercise a different combination: index 0
     * (STR) is undrained with an ordinary maximum; index 1 (INT) is drained with the natural
     * maximum exactly at {@code 18 + 100} and a modified maximum past it; index 2 (WIS) is
     * drained but at or below 18 throughout; index 3 (DEX) is undrained with a modified maximum
     * past the {@code "18/***"} cutoff; index 4 (CON) is undrained at the lowest ordinary values.
     *
     * <p>Class DisplayPlayerStatInfo coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("displayPlayerStatInfo")
    class DisplayPlayerStatInfo {

        private static final int WIDTH = 100;
        private static final int HEIGHT = 24;
        private static final int ROW = 2;
        private static final int COL = 42;

        private CellGrid grid;
        private PlayerStatusView savedStatusView;
        private PlayerCharSheetView savedCharSheetView;

        /**
         * Wires a fresh {@link CellGrid}-backed {@link Term} into {@code uiPlayer} via a
         * hand-built {@link TermData} (its private {@code t} field is set directly by
         * reflection, bypassing {@link TermData#termDataLink}, which would otherwise build its
         * own zero-sized {@link Term} from {@code TermData}'s unset column/row fields), then
         * installs a five-stat fixture split across the current {@link PlayerStatusView} (the
         * current/maximum stat arrays) and {@link PlayerCharSheetView} (the four stat-bonus
         * columns plus the drained-use column).
         *
         * @throws Exception if the field cannot be reached
         */
        @BeforeEach
        void arrange() throws Exception {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();

            grid = new CellGrid(HEIGHT, WIDTH);
            Term term = new Term();
            term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));

            TermData termData = new TermData(new Screen(grid, new ArrayList<>()));
            Field tField = TermData.class.getDeclaredField("t");
            tField.setAccessible(true);
            tField.set(termData, term);

            uiPlayer.setTermData(termData);

            PlayerEventStatusUpdate.updatePlayerStatusView(new PlayerStatusView(null, null, null,
                    null, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    new int[]{18, 17, 5, 25, 1},
                    new int[]{18, 118, 10, 25, 1},
                    new String[]{"STR", "INT", "WIS", "DEX", "CON"}, 0, 0, false,
                    false, false, false,
                    false, false, false,
                    false, false, false,
                    0, 0, null, null, null,
                    null, null, null,
                    0, 0, 0, 0));
            PlayerEventStatusUpdate.updatePlayerCharSheetView(new PlayerCharSheetView(0, true,
                    new int[]{2, -2, 0, 10, 1},
                    new int[]{-1, 5, 0, -10, -1},
                    new int[]{0, -3, 0, 99, 2},
                    new int[]{19, 137, 10, 258, 1},
                    new int[]{0, 20, 5, 0, 0}, 0, 0, new long[]{}, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, false));
        }

        /**
         * Restores the caches saved by {@link #arrange()}.
         */
        @AfterEach
        void restore() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
        }

        private AngbandDisplayCharacter cell(int row, int col) {
            return grid.get(row, col);
        }

        /**
         * Reads back {@code length} consecutive characters from the grid as a string.
         *
         * @param row    the row to read
         * @param col    the starting column
         * @param length how many characters to read
         * @return the characters at {@code (row, col)} through {@code (row, col + length - 1)}
         */
        private String text(int row, int col, int length) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(cell(row, col + i).getCharacter());
            }
            return builder.toString();
        }

        /**
         * Invokes the private method under test.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        private void invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("displayPlayerStatInfo");
            method.setAccessible(true);
            method.invoke(uiPlayer);
        }

        /**
         * The five column headers, written once above the stat rows regardless of any stat's
         * value.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("writes the column headers above the stat rows")
        void writesColumnHeaders() throws Exception {
            invoke();

            assertEquals("  Self", text(ROW - 1, COL + 5, 6));
            assertEquals(" RB", text(ROW - 1, COL + 12, 3));
            assertEquals(" CB", text(ROW - 1, COL + 16, 3));
            assertEquals(" EB", text(ROW - 1, COL + 20, 3));
            assertEquals("  Best", text(ROW - 1, COL + 24, 6));
        }

        /**
         * An undrained stat ({@code stat_cur == stat_max}) uses the uppercase name and, since its
         * maximum is not {@code 18 + 100}, leaves the marker column holding the name's own
         * trailing colon rather than a {@code "!"}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("uses the uppercase name and no marker when a stat is undrained")
        void usesUppercaseNameWithNoMarkerWhenUndrained() throws Exception {
            invoke();

            assertEquals(UIRegistry.statNames[0], text(ROW, COL, 5));
            assertEquals(':', cell(ROW, COL + 3).getCharacter());
        }

        /**
         * A drained stat ({@code stat_cur < stat_max}) uses the lowercase name; when its maximum
         * is exactly {@code 18 + 100}, the marker write follows the name write and overwrites the
         * name's own trailing colon (both land at {@code col + 3}) with {@code "!"}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("uses the lowercase name and shows the marker when drained at the natural maximum")
        void usesLowercaseNameWithMarkerWhenDrainedAtNaturalMaximum() throws Exception {
            invoke();

            assertEquals(UIRegistry.statReducedNames[1].substring(0, 3) + "! ", text(ROW + 1, COL, 5));
        }

        /**
         * A drained stat whose maximum is not {@code 18 + 100} still takes the lowercase name,
         * but leaves the marker column alone.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("uses the lowercase name and no marker when drained below the natural maximum")
        void usesLowercaseNameWithNoMarkerWhenDrainedBelowNaturalMaximum() throws Exception {
            invoke();

            assertEquals(UIRegistry.statReducedNames[2], text(ROW + 2, COL, 5));
            assertEquals(':', cell(ROW + 2, COL + 3).getCharacter());
        }

        /**
         * The natural-maximum and modified-maximum columns both go through {@link
         * UIPlayer#cnvStat(int)}: an ordinary at-or-below-18 value, the exact {@code 18 + 100}
         * boundary, and a value past the {@code "18/***"} cutoff.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("formats the natural and modified maximum columns via cnvStat")
        void formatsMaximumColumnsViaCnvStat() throws Exception {
            invoke();

            assertEquals("    18", text(ROW, COL + 5, 6));
            assertEquals(" 18/01", text(ROW, COL + 24, 6));

            assertEquals("18/100", text(ROW + 1, COL + 5, 6));
            assertEquals("18/119", text(ROW + 1, COL + 24, 6));

            assertEquals(" 18/07", text(ROW + 3, COL + 5, 6));
            assertEquals("18/***", text(ROW + 3, COL + 24, 6));
        }

        /**
         * The race, class and equipment bonus columns are each formatted as {@code "%+3d"} -
         * positive, negative and zero all fit within three characters, but a two-digit magnitude
         * fills the width with no leading space.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("formats the race, class and equipment bonus columns as signed three-character numbers")
        void formatsBonusColumnsSigned() throws Exception {
            invoke();

            assertEquals(" +2", text(ROW, COL + 12, 3));
            assertEquals(" -1", text(ROW, COL + 16, 3));
            assertEquals(" +0", text(ROW, COL + 20, 3));

            assertEquals("+10", text(ROW + 3, COL + 12, 3));
            assertEquals("-10", text(ROW + 3, COL + 16, 3));
            assertEquals("+99", text(ROW + 3, COL + 20, 3));
        }

        /**
         * The {@code stat_use} column is gated on the same drained comparison as the name
         * column: a drained stat gets its used value via {@link UIPlayer#cnvStat(int)}, but an
         * undrained one leaves the column untouched.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("shows the used-value column only for a drained stat")
        void showsUsedValueColumnOnlyWhenDrained() throws Exception {
            invoke();

            assertEquals(" 18/02", text(ROW + 1, COL + 31, 6));
            assertEquals("     5", text(ROW + 2, COL + 31, 6));
            assertNull(cell(ROW, COL + 31));
            assertNull(cell(ROW + 3, COL + 31));
            assertNull(cell(ROW + 4, COL + 31));
        }
    }

    /**
     * Tests {@link UIPlayer.PlayerDisplayMode} against the raw {@code int mode} states C's
     * {@code display_player} switches on ({@code [C] src/ui-player.c:892, 907-920}) and the
     * {@code do_cmd_change_name} toggle that flips between them ({@code [C] src/ui-player.c:1215,
     * 1224}).
     *
     * <p>C never names its two modes, so there is no C-derived value to compare a mapped
     * {@code int} against; what the port owes C is that there are exactly two states, standing in
     * for C's {@code #define INFO_SCREENS 2}. {@link UIPlayer.Panel}'s {@code panelLine} method
     * and the {@link UIPlayer.PanelLine} rows it builds are covered separately, by
     * {@code PanelLineMethod} below. {@link UIPlayer.panelRegions} is
     * likewise not covered: it too is a non-static private nested class nothing outside
     * {@link UIPlayer} can construct without reflection, and its three methods are direct field
     * pass-throughs (a {@link java.util.function.Supplier#get()} delegation for {@code getPanel}) with
     * no C-derived computation of their own to check against {@code ui-player.c} — the value worth
     * fixing was the {@code panelFunc} field's shape, already covered by the stage-1 review rather
     * than a test.
     */
    @Nested
    @DisplayName("PlayerDisplayMode")
    class PlayerDisplayModeEnum {

        /**
         * Exactly two modes exist, matching C's {@code INFO_SCREENS} count for the two states
         * {@code do_cmd_change_name} toggles between.
         */
        @Test
        @DisplayName("has exactly two modes")
        void hasExactlyTwoModes() {
            assertEquals(2, UIPlayer.PlayerDisplayMode.values().length);
        }

        /**
         * {@code DISPLAY_FULL} is C's truthy mode, drawing the top-left panel plus the sustain and
         * flag panels; {@code DISPLAY_EXTRA} is C's falsy mode, drawing the five summary panels
         * and the player's history via {@code display_player_xtra_info}
         * ({@code [C] ui-player.c:907-920}). Neither branch is ported yet, so this only fixes the
         * two names and their order.
         */
        @Test
        @DisplayName("declares DISPLAY_FULL before DISPLAY_EXTRA")
        void declaresFullBeforeExtra() {
            assertEquals("DISPLAY_FULL", UIPlayer.PlayerDisplayMode.values()[0].name());
            assertEquals("DISPLAY_EXTRA", UIPlayer.PlayerDisplayMode.values()[1].name());
        }
    }

    /**
     * Tests the private {@code panelAllocate(int)} against C's {@code panel_allocate}
     * ({@code [C] src/ui-player.c:71-79}).
     *
     * <p>C's version {@code mem_zalloc}s a {@code struct panel}, then sets {@code len} to zero,
     * {@code max} to its {@code n} argument, and {@code lines} to a freshly {@code mem_zalloc}'d,
     * {@code max}-element array. The Java port's {@code lines} is instead an empty, growable list
     * rather than a pre-sized array, so these tests check {@code len}/{@code max} against C's exact
     * values and {@code lines} only for non-null emptiness, not element count. Both the returned
     * {@code Panel} and its fields are private, so every test reaches them by reflection.
     *
     * <p>Class PanelAllocate coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("panelAllocate")
    class PanelAllocate {

        /**
         * Invokes the private method under test.
         *
         * @param size the requested panel capacity
         * @return the resulting {@code Panel}, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object invoke(int size) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("panelAllocate", int.class);
            method.setAccessible(true);
            return method.invoke(uiPlayer, size);
        }

        /**
         * Reads an {@code int} field off a returned {@code Panel} by reflection.
         *
         * @param panel     the {@code Panel} instance to read from
         * @param fieldName {@code "len"} or {@code "max"}
         * @return the field's value
         * @throws Exception if the field cannot be reached
         */
        private int readInt(Object panel, String fieldName) throws Exception {
            Field field = panel.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (int) field.get(panel);
        }

        /**
         * Reads the {@code lines} field off a returned {@code Panel} by reflection.
         *
         * @param panel the {@code Panel} instance to read from
         * @return the {@code lines} list
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * C sets {@code len} to zero and {@code max} to its {@code n} argument unconditionally
         * ({@code [C] src/ui-player.c:74-75}).
         *
         * @throws Exception if the method or its fields cannot be reached
         */
        @Test
        @DisplayName("sets max from size and leaves len at zero")
        void setsMaxAndZeroesLen() throws Exception {
            Object panel = invoke(6);
            assertEquals(0, readInt(panel, "len"));
            assertEquals(6, readInt(panel, "max"));
        }

        /**
         * C's {@code lines} pointer is never {@code NULL} after {@code panel_allocate} returns
         * ({@code [C] src/ui-player.c:76}); the Java port's list is likewise never {@code null},
         * though it starts at size zero rather than C's {@code max} pre-sized, zeroed slots.
         *
         * @throws Exception if the method or its field cannot be reached
         */
        @Test
        @DisplayName("initialises lines to a non-null, empty list")
        void initialisesEmptyLines() throws Exception {
            Object panel = invoke(6);
            List<?> lines = readLines(panel);
            assertNotNull(lines);
            assertTrue(lines.isEmpty());
        }

        /**
         * Every one of C's five {@code get_panel_*} constructors passes a different {@code n}
         * ({@code [C] src/ui-player.c:697, 710, 731, 781, 829}); {@code max} must track each one
         * exactly.
         *
         * @throws Exception if the method or its field cannot be reached
         */
        @Test
        @DisplayName("matches every panel size the C constructors allocate")
        void matchesEveryConstructorSize() throws Exception {
            assertEquals(6, readInt(invoke(6), "max"));
            assertEquals(9, readInt(invoke(9), "max"));
            assertEquals(8, readInt(invoke(8), "max"));
            assertEquals(7, readInt(invoke(7), "max"));
        }

        /**
         * C's {@code mem_zalloc} returns a fresh block on every call, so two calls with the same
         * {@code n} never alias; the Java port must not silently cache or share a {@code Panel}
         * either.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns a fresh Panel on every call rather than a shared instance")
        void returnsDistinctInstances() throws Exception {
            Object first = invoke(6);
            Object second = invoke(6);
            assertNotSame(first, second);
        }

        /**
         * {@code n = 0} is not special-cased in C — {@code mem_zalloc(0)} still returns a valid,
         * zero-length allocation, and {@code len}/{@code max} are set the same way regardless.
         *
         * @throws Exception if the method or its fields cannot be reached
         */
        @Test
        @DisplayName("allocates a zero-capacity panel when size is zero")
        void allocatesZeroCapacity() throws Exception {
            Object panel = invoke(0);
            assertEquals(0, readInt(panel, "len"));
            assertEquals(0, readInt(panel, "max"));
        }
    }

    /**
     * Tests the private {@code Panel.panelLine(ColourEnum, String, String, Object...)} against
     * C's {@code panel_line} ({@code [C] src/ui-player.c}, function {@code panel_line}).
     *
     * <p>Expected values are derived from the C source directly: the {@code assert(p->len !=
     * p->max)} full-panel guard, the direct pass-through of {@code attr}/{@code label} onto the
     * new row, and {@code vstrnfmt}'s silent truncation of the formatted value into the fixed
     * {@code char value[20]} buffer. {@code vstrnfmt} ({@code z-form.c}, function
     * {@code vstrnfmt}) stops appending content once it has written 19 of that buffer's 20 bytes,
     * reserving the last for the null terminator a Java {@link String} does not need, so the
     * content width C ever displays is 19 characters, not 20. {@code Panel} and {@code PanelLine}
     * are both private non-static nested classes, so every test reaches them, and the fields of
     * the rows they produce, by reflection.
     *
     * <p>Class PanelLineMethod coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("Panel.panelLine")
    class PanelLineMethod {

        /**
         * Builds a {@code Panel} of the given capacity via the already-tested
         * {@code panelAllocate}.
         *
         * @param size the panel's capacity
         * @return the {@code Panel} instance, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object allocate(int size) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("panelAllocate", int.class);
            method.setAccessible(true);
            return method.invoke(uiPlayer, size);
        }

        /**
         * Invokes the private method under test. Reflection does not pack trailing positional
         * arguments into a varargs array the way ordinary calls do, so {@code formatArgs} is
         * passed as a single already-built array occupying the method's fourth formal parameter.
         *
         * @param panel      the {@code Panel} to append to
         * @param attribute  the row's display colour
         * @param label      the row's fixed label text
         * @param format     the {@link String#format} pattern for the row's value text
         * @param formatArgs the arguments {@code format} is applied to
         * @throws Exception if the method cannot be reached; any exception it throws is unwrapped
         *                   from the reflective {@link InvocationTargetException} and rethrown
         *                   directly
         */
        private void invoke(Object panel, ColourEnum attribute, String label, String format,
                            Object... formatArgs) throws Exception {
            Method method = panel.getClass().getDeclaredMethod("panelLine", ColourEnum.class,
                    String.class, String.class, Object[].class);
            method.setAccessible(true);
            try {
                method.invoke(panel, new Object[]{attribute, label, format, formatArgs});
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw e;
            }
        }

        /**
         * Reads {@code Panel}'s private {@code len} field.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its current {@code len}
         * @throws Exception if the field cannot be reached
         */
        private int readLen(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("len");
            field.setAccessible(true);
            return (int) field.get(panel);
        }

        /**
         * Reads {@code Panel}'s private {@code lines} field.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its current rows, each a private {@code PanelLine} typed as {@link Object}
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * Reads a {@code PanelLine} row's colour via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code attribute}
         * @throws Exception if the method cannot be reached
         */
        private ColourEnum readAttribute(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getAttribute");
            method.setAccessible(true);
            return (ColourEnum) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's label via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code label}
         * @throws Exception if the method cannot be reached
         */
        private String readLabel(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getLabel");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's value via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code value}
         * @throws Exception if the method cannot be reached
         */
        private String readValue(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * The ordinary path: {@code attribute} and {@code label} are stored unchanged, the
         * formatted value is stored whole when it fits, and {@code len} advances by one —
         * mirroring C's {@code pl->attr = attr; pl->label = label;} plus a {@code vstrnfmt} call
         * short enough not to truncate.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("appends a row with attribute, label and formatted value, incrementing len")
        void appendsRowWithAttributeLabelAndFormattedValue() throws Exception {
            Object panel = allocate(3);

            invoke(panel, ColourEnum.COLOUR_LIGHT_BLUE, "HP", "%d/%d", 10, 20);

            assertEquals(1, readLen(panel));
            List<?> lines = readLines(panel);
            assertEquals(1, lines.size());
            Object line = lines.get(0);
            assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, readAttribute(line));
            assertEquals("HP", readLabel(line));
            assertEquals("10/20", readValue(line));
        }

        /**
         * Several calls append in order rather than overwriting a single slot, each advancing
         * {@code len} by one — the Java form of C's {@code p->lines[p->len++]} claiming a fresh
         * slot on every call.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("appends successive rows in call order")
        void appendsSuccessiveRowsInOrder() throws Exception {
            Object panel = allocate(3);

            invoke(panel, ColourEnum.COLOUR_LIGHT_BLUE, "Name", "%s", "Bob");
            invoke(panel, ColourEnum.COLOUR_LIGHT_GREEN, "Race", "%s", "Half-Troll");

            assertEquals(2, readLen(panel));
            List<?> lines = readLines(panel);
            assertEquals("Name", readLabel(lines.get(0)));
            assertEquals("Bob", readValue(lines.get(0)));
            assertEquals("Race", readLabel(lines.get(1)));
            assertEquals("Half-Troll", readValue(lines.get(1)));
        }

        /**
         * A value that formats to exactly 19 characters is stored whole — C's {@code vstrnfmt}
         * only breaks its copy loop once {@code n} reaches {@code max - 1} (19, for the 20-byte
         * buffer), so a 19-character result fills the loop's full budget without ever hitting
         * that break.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("stores a value of exactly nineteen characters unchanged")
        void storesNineteenCharacterValueUnchanged() throws Exception {
            Object panel = allocate(1);
            String nineteenChars = "1234567890123456789";

            invoke(panel, ColourEnum.COLOUR_WHITE, "L", "%s", nineteenChars);

            assertEquals(nineteenChars, readValue(readLines(panel).get(0)));
        }

        /**
         * A value longer than 19 characters is silently cut down to the first 19 — the Java form
         * of C's {@code vstrnfmt} breaking its copy loop as soon as {@code n} reaches
         * {@code max - 1}, leaving the 20th of the buffer's bytes for the null terminator that a
         * Java {@link String} has no need of.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("truncates a value longer than nineteen characters to the first nineteen")
        void truncatesValueLongerThanNineteenCharacters() throws Exception {
            Object panel = allocate(1);

            invoke(panel, ColourEnum.COLOUR_WHITE, "L", "%s", "12345678901234567890123");

            List<?> lines = readLines(panel);
            assertEquals("1234567890123456789", readValue(lines.get(0)));
        }

        /**
         * A full panel ({@code len == max}) refuses the write — the Java form of C's
         * {@code assert(p->len != p->max)}, enforced unconditionally here rather than only in a
         * debug build.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("throws once the panel is full")
        void throwsOncePanelIsFull() throws Exception {
            Object panel = allocate(1);
            invoke(panel, ColourEnum.COLOUR_WHITE, "First", "%s", "fits");

            assertThrows(RuntimeException.class,
                    () -> invoke(panel, ColourEnum.COLOUR_WHITE, "Second", "%s", "overflow"));
        }

        /**
         * A zero-capacity panel is already full before any row is added, matching C's assertion
         * against {@code p->max} regardless of its value.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("throws immediately on a zero-capacity panel")
        void throwsImmediatelyOnZeroCapacityPanel() throws Exception {
            Object panel = allocate(0);

            assertThrows(RuntimeException.class,
                    () -> invoke(panel, ColourEnum.COLOUR_WHITE, "L", "%s", "x"));
        }
    }

    /**
     * Tests the private {@code Panel.space()} against C's {@code panel_space}
     * ({@code [C] src/ui-player.c}, function {@code panel_space}).
     *
     * <p>Expected values are derived from the C source directly: the {@code assert(p->len !=
     * p->max)} full-panel guard C only enforces in a debug build but this port enforces
     * unconditionally, and {@code p->len++} advancing past a slot {@code panel_allocate} has
     * already zeroed to a blank row. {@code Panel} and {@code PanelLine} are both private
     * non-static nested classes, so every test reaches them, and the fields of the rows they
     * produce, by reflection. C never reads a skipped row's {@code attr} or {@code value}
     * ({@code display_panel} continues past it before either is touched), so these tests pin down
     * only the C-derived properties — {@code len}/{@code lines} staying in step, an empty label,
     * and the capacity guard — not this port's particular placeholder colour, which C leaves
     * unspecified.
     *
     * <p>Class SpaceMethod coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("Panel.space")
    class SpaceMethod {

        /**
         * Builds a {@code Panel} of the given capacity via the already-tested
         * {@code panelAllocate}.
         *
         * @param size the panel's capacity
         * @return the {@code Panel} instance, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object allocate(int size) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("panelAllocate", int.class);
            method.setAccessible(true);
            return method.invoke(uiPlayer, size);
        }

        /**
         * Invokes the private {@code panelLine}, used here only to fill rows around a spacer so
         * ordering can be checked.
         *
         * @param panel      the {@code Panel} to append to
         * @param attribute  the row's display colour
         * @param label      the row's fixed label text
         * @param format     the {@link String#format} pattern for the row's value text
         * @param formatArgs the arguments {@code format} is applied to
         * @throws Exception if the method cannot be reached; any exception it throws is unwrapped
         *                   from the reflective {@link InvocationTargetException} and rethrown
         *                   directly
         */
        private void invokePanelLine(Object panel, ColourEnum attribute, String label,
                                     String format, Object... formatArgs) throws Exception {
            Method method = panel.getClass().getDeclaredMethod("panelLine", ColourEnum.class,
                    String.class, String.class, Object[].class);
            method.setAccessible(true);
            try {
                method.invoke(panel, new Object[]{attribute, label, format, formatArgs});
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw e;
            }
        }

        /**
         * Invokes the private method under test.
         *
         * @param panel the {@code Panel} to append the spacer row to
         * @throws Exception if the method cannot be reached; any exception it throws is unwrapped
         *                   from the reflective {@link InvocationTargetException} and rethrown
         *                   directly
         */
        private void invokeSpace(Object panel) throws Exception {
            Method method = panel.getClass().getDeclaredMethod("space");
            method.setAccessible(true);
            try {
                method.invoke(panel);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                throw e;
            }
        }

        /**
         * Reads {@code Panel}'s private {@code len} field.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its current {@code len}
         * @throws Exception if the field cannot be reached
         */
        private int readLen(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("len");
            field.setAccessible(true);
            return (int) field.get(panel);
        }

        /**
         * Reads {@code Panel}'s private {@code lines} field.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its current rows, each a private {@code PanelLine} typed as {@link Object}
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * Reads a {@code PanelLine} row's label via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code label}
         * @throws Exception if the method cannot be reached
         */
        private String readLabel(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getLabel");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's value via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code value}
         * @throws Exception if the method cannot be reached
         */
        private String readValue(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * The ordinary path: one blank row is appended and {@code len} advances by one, mirroring
         * C's {@code p->len++} advancing past an already-zeroed slot. The row's label and value
         * are empty, matching the {@code NULL} label and zeroed {@code value} buffer of that
         * slot — the property a future renderer would need to tell a spacer row apart from a real
         * one.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("appends a blank row and advances len by one")
        void appendsBlankRowAndAdvancesLen() throws Exception {
            Object panel = allocate(2);

            invokeSpace(panel);

            assertEquals(1, readLen(panel));
            List<?> lines = readLines(panel);
            assertEquals(1, lines.size());
            Object line = lines.get(0);
            assertEquals("", readLabel(line));
            assertEquals("", readValue(line));
        }

        /**
         * A row appended after a spacer keeps its own position rather than overwriting the
         * spacer's slot — the Java form of C's {@code panel_line} claiming
         * {@code p->lines[p->len++]}, which after a {@code panel_space} call is the very next
         * array slot, not the one the spacer already advanced past.
         *
         * @throws Exception if a method cannot be reached or throws
         */
        @Test
        @DisplayName("keeps later rows in their own slot after a spacer")
        void keepsLaterRowsInOwnSlotAfterSpacer() throws Exception {
            Object panel = allocate(3);

            invokePanelLine(panel, ColourEnum.COLOUR_LIGHT_GREEN, "Adv Exp", "%s", "1234");
            invokeSpace(panel);
            invokePanelLine(panel, ColourEnum.COLOUR_LIGHT_GREEN, "Gold", "%d", 500);

            assertEquals(3, readLen(panel));
            List<?> lines = readLines(panel);
            assertEquals(3, lines.size());
            assertEquals("Adv Exp", readLabel(lines.get(0)));
            assertEquals("", readLabel(lines.get(1)));
            assertEquals("Gold", readLabel(lines.get(2)));
            assertEquals("500", readValue(lines.get(2)));
        }

        /**
         * A full panel ({@code len == max}) refuses the spacer — the Java form of C's
         * {@code assert(p->len != p->max)}, enforced unconditionally here rather than only in a
         * debug build, the same bound {@code panelLine} enforces.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("throws once the panel is full")
        void throwsOncePanelIsFull() throws Exception {
            Object panel = allocate(1);
            invokeSpace(panel);

            assertThrows(RuntimeException.class, () -> invokeSpace(panel));
        }

        /**
         * A zero-capacity panel is already full before any row is added, matching C's assertion
         * against {@code p->max} regardless of its value.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("throws immediately on a zero-capacity panel")
        void throwsImmediatelyOnZeroCapacityPanel() throws Exception {
            Object panel = allocate(0);

            assertThrows(RuntimeException.class, () -> invokeSpace(panel));
        }
    }

    /**
     * Tests the {@link UIPlayer#UIPlayer()} constructor against C's file-scope {@code panels[]}
     * table ({@code [C] src/ui-player.c:848-859}).
     *
     * <p>Expected values are read straight off C's brace-initialised rows: each entry's
     * {@code region} (col, row, width, page_rows), its {@code align_left} flag, and which of the
     * five {@code get_panel_*} builders it pairs with, in table order. {@link UIPlayer#panels} is
     * private, so every test reaches it by reflection; each entry's builder is confirmed
     * indirectly, by invoking the {@link java.util.function.Supplier} it holds and checking the
     * {@code Panel} (or
     * {@code null}, for the one still-stub builder) that only that one builder would produce.
     *
     * <p>Class Constructor coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("constructor")
    class Constructor {

        private PlayerStatusView savedStatusView;
        private PlayerCharSheetView savedCharSheetView;
        private int savedMaxLevel;

        /**
         * Saves the current global player-view state and {@code PY_MAX_LEVEL}, then installs a
         * minimal, valid fixture — a level of 10 against a fifty-entry {@code expToLevel} table —
         * so invoking each builder in {@link #panels()} (in particular
         * {@link UIPlayer#getPanelMidLeft()}, which reaches {@link UIPlayer#showAdvanceExperience()},
         * and {@link UIPlayer#getPanelCombat()}, which divides by {@code bthPlusAdj}) has real
         * player state to read rather than whatever a previously run test class happened to leave
         * behind in these process-wide caches.
         */
        @BeforeEach
        void installFixture() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
            savedMaxLevel = ChannelRegistry.getPYMaxLevel();

            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(new PlayerStatusView(
                    "Test", "Test", "Test", "Test", 10, 50, 0L, 0L, 0L,
                    0, 0, 0, 0, 0, 0,
                    new int[0], new int[0], new String[0],
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, 0, null, null, null, null, null, null,
                    0, 0, 0, 0));
            PlayerEventStatusUpdate.updatePlayerCharSheetView(new PlayerCharSheetView(0, true,
                    new int[0], new int[0], new int[0], new int[0], new int[0], 0, 0,
                    new long[50], 100, 0, 0, 0,
                    5, 3, 2, 40, 30, 3, 2, 6, 150, 20,
                    0, 0, 0, 0, 0, 0, 0, 0, false));
        }

        /**
         * Restores the state saved by {@link #installFixture()}.
         */
        @AfterEach
        void restoreFixture() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
            ChannelRegistry.setPYMaxLevel(savedMaxLevel);
        }

        /**
         * Reads {@link UIPlayer}'s private {@code panels} field, freshly built by
         * {@link UIPlayer#UIPlayer()} in {@link #setUp()}.
         *
         * @return the five panel/region pairings, each typed as {@link Object} since
         * {@code PanelRegions} is private
         * @throws Exception if the field cannot be reached
         */
        private Object[] panels() throws Exception {
            Field field = UIPlayer.class.getDeclaredField("panels");
            field.setAccessible(true);
            return (Object[]) field.get(uiPlayer);
        }

        /**
         * Reads a {@code PanelRegions} entry's screen region via its public getter.
         *
         * @param panelRegions the entry, typed as {@link Object} since the class is private
         * @return its {@link Region}
         * @throws Exception if the method cannot be reached
         */
        private Region bounds(Object panelRegions) throws Exception {
            Method method = panelRegions.getClass().getDeclaredMethod("getBounds");
            method.setAccessible(true);
            return (Region) method.invoke(panelRegions);
        }

        /**
         * Reads a {@code PanelRegions} entry's alignment flag via its public getter.
         *
         * @param panelRegions the entry, typed as {@link Object} since the class is private
         * @return {@code true} if this entry aligns flush left
         * @throws Exception if the method cannot be reached
         */
        private boolean alignLeft(Object panelRegions) throws Exception {
            Method method = panelRegions.getClass().getDeclaredMethod("isAlignLeft");
            method.setAccessible(true);
            return (boolean) method.invoke(panelRegions);
        }

        /**
         * Invokes a {@code PanelRegions} entry's builder via its public {@code getPanel()}.
         *
         * @param panelRegions the entry, typed as {@link Object} since the class is private
         * @return the {@code Panel} its builder produces, or {@code null} for a still-stub builder
         * @throws Exception if the method cannot be reached
         */
        private Object panel(Object panelRegions) throws Exception {
            Method method = panelRegions.getClass().getDeclaredMethod("getPanel");
            method.setAccessible(true);
            return method.invoke(panelRegions);
        }

        /**
         * Reads an {@code int} field off a {@code Panel} by reflection.
         *
         * @param panel     the {@code Panel} instance to read from
         * @param fieldName {@code "len"} or {@code "max"}
         * @return the field's value
         * @throws Exception if the field cannot be reached
         */
        private int readInt(Object panel, String fieldName) throws Exception {
            Field field = panel.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (int) field.get(panel);
        }

        /**
         * C's {@code panels[]} table has exactly five rows.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("builds exactly five panel/region pairings")
        void buildsFivePairings() throws Exception {
            assertEquals(5, panels().length);
        }

        /**
         * Each region's col/row/width/page_rows matches C's brace-initialised values, in table
         * order: top-left, misc, mid-left, combat, skills.
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("matches C's five regions in table order")
        void matchesRegionsInTableOrder() throws Exception {
            Object[] panels = panels();
            int[][] expected = {
                    {1, 1, 40, 7},
                    {21, 1, 18, 3},
                    {1, 9, 24, 9},
                    {29, 9, 19, 9},
                    {52, 9, 20, 8}
            };

            for (int i = 0; i < 5; i++) {
                Region region = bounds(panels[i]);
                assertEquals(expected[i][0], region.getCol(), "index " + i + " col");
                assertEquals(expected[i][1], region.getRow(), "index " + i + " row");
                assertEquals(expected[i][2], region.getWidth(), "index " + i + " width");
                assertEquals(expected[i][3], region.getPageRows(), "index " + i + " pageRows");
            }
        }

        /**
         * Only the top-left entry aligns flush left in C's table; the other four are flush
         * right.
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("aligns only the top-left panel flush left")
        void alignsOnlyTopLeftFlushLeft() throws Exception {
            Object[] panels = panels();

            assertTrue(alignLeft(panels[0]));
            for (int i = 1; i < 5; i++) {
                assertFalse(alignLeft(panels[i]), "index " + i);
            }
        }

        /**
         * Each region is paired with its own builder, in C's table order: {@code getPanelTopLeft}
         * (which allocates a six-row {@code Panel}) at index 0, {@code getPanelMisc} (which
         * allocates a seven-row {@code Panel}) at index 1, {@code getPanelMidLeft} (which
         * allocates a nine-row {@code Panel}) at index 2, {@code getPanelCombat} (which also
         * allocates a nine-row {@code Panel}) at index 3, and {@code getPanelSkills} (which
         * allocates an eight-row {@code Panel}) at index 4.
         *
         * @throws Exception if a field or method cannot be reached
         */
        @Test
        @DisplayName("pairs each region with its own C-matching builder")
        void pairsEachRegionWithItsOwnBuilder() throws Exception {
            Object[] panels = panels();

            Object topLeft = panel(panels[0]);
            assertNotNull(topLeft);
            assertEquals(6, readInt(topLeft, "max"));

            Object misc = panel(panels[1]);
            assertNotNull(misc);
            assertEquals(7, readInt(misc, "max"));

            Object midLeft = panel(panels[2]);
            assertNotNull(midLeft);
            assertEquals(9, readInt(midLeft, "max"));

            Object combat = panel(panels[3]);
            assertNotNull(combat);
            assertEquals(9, readInt(combat, "max"));

            Object skills = panel(panels[4]);
            assertNotNull(skills);
            assertEquals(8, readInt(skills, "max"));
        }
    }

    /**
     * Tests {@link UIPlayer#weightRemaining()} against C's {@code weight_remaining}
     * ({@code [C] src/player-calcs.c:1765-1775}).
     *
     * <p>Expected values are computed directly from C's formula, {@code 60 * weightLimit -
     * totalWeight - 1}: an ordinary case with room to spare, the exact boundary where the result
     * is zero, and an over-burdened case where the result goes negative (C's caller in
     * {@code show_inven}, {@code [C] src/ui-object.c:479-491}, treats a negative result as
     * "overweight" rather than "remaining"). {@code weightLimit} here is C's raw, unscaled
     * {@code adj_str_wgt} table entry, not {@code weight_limit()}'s ×100 figure — see
     * {@link UIPlayer#weightRemaining()}'s own Javadoc. The method is private, so every test
     * reaches it by reflection.
     *
     * <p>Class WeightRemaining coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("weightRemaining")
    class WeightRemaining {

        private PlayerCharSheetView savedCharSheetView;

        /**
         * Saves the char-sheet cache so this test's fixture cannot leak into another test.
         */
        @BeforeEach
        void saveCharSheetView() {
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
        }

        /**
         * Restores the char-sheet cache saved by {@link #saveCharSheetView()}.
         */
        @AfterEach
        void restoreCharSheetView() {
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
        }

        /**
         * Installs a {@link PlayerCharSheetView} carrying only the two fields the method reads.
         *
         * @param weightLimit the raw, unscaled {@code adj_str_wgt} table entry
         * @param totalWeight the total weight carried, in tenth-pounds
         */
        private void installView(int weightLimit, int totalWeight) {
            PlayerEventStatusUpdate.updatePlayerCharSheetView(new PlayerCharSheetView(0, true,
                    new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0},
                    new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, totalWeight, weightLimit,
                    new long[]{}, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, false));
        }

        /**
         * Invokes the private method under test.
         *
         * @return its result
         * @throws Exception if the method cannot be reached or throws
         */
        private int invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("weightRemaining");
            method.setAccessible(true);
            return (int) method.invoke(uiPlayer);
        }

        /**
         * The ordinary path: plenty of room left, {@code 60 * 15 - 200 - 1 == 699}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("computes 60 times the weight limit minus total weight minus one")
        void computesTheOrdinaryFormula() throws Exception {
            installView(15, 200);

            assertEquals(699, invoke());
        }

        /**
         * The exact boundary: {@code 60 * 10 - 599 - 1 == 0}, carrying no weight to spare but not
         * yet over.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns exactly zero at the carrying boundary")
        void returnsZeroAtBoundary() throws Exception {
            installView(10, 599);

            assertEquals(0, invoke());
        }

        /**
         * Carrying more than the limit allows drives the result negative — C's caller reads a
         * negative result as "overweight" rather than "remaining".
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("goes negative once total weight exceeds the limit")
        void goesNegativeWhenOverburdened() throws Exception {
            installView(5, 1000);

            assertEquals(-701, invoke());
        }
    }

    /**
     * Tests the private {@code maxColour(int, int)} against C's {@code max_color}
     * ({@code [C] src/ui-player.c:682-685}).
     *
     * <p>C's formula is a single ternary, {@code val < max ? COLOUR_YELLOW : COLOUR_L_GREEN}, so
     * there are exactly three cases worth proving: below the maximum, at it, and above it. The
     * method is overloaded with a {@code (long, long)} sibling, covered separately by
     * {@link MaxColourLong}, that {@link UIPlayer#getPanelMidLeft()} uses for
     * {@code experience()}/{@code maxExperience()} — {@code long} fields on
     * {@link PlayerStatusView}; this class covers only the {@code (int, int)} overload, the one
     * C's {@code get_panel_midleft} uses for {@code player->lev}/{@code max_lev}.
     *
     * <p>Class MaxColour coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("maxColour(int, int)")
    class MaxColour {

        /**
         * Invokes the private method under test.
         *
         * @param val the value being displayed
         * @param max the value's recorded maximum
         * @return the chosen colour
         * @throws Exception if the method cannot be reached or throws
         */
        private ColourEnum invoke(int val, int max) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("maxColour", int.class, int.class);
            method.setAccessible(true);
            return (ColourEnum) method.invoke(uiPlayer, val, max);
        }

        /**
         * A value below its maximum takes C's {@code COLOUR_YELLOW} branch.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns yellow when val is below max")
        void returnsYellowWhenBelowMax() throws Exception {
            assertEquals(ColourEnum.COLOUR_YELLOW, invoke(5, 10));
        }

        /**
         * The boundary: {@code val == max} is not less than {@code max}, so it falls into C's
         * {@code COLOUR_L_GREEN} branch rather than the yellow one.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns light green when val equals max")
        void returnsLightGreenWhenEqualToMax() throws Exception {
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(10, 10));
        }

        /**
         * A value above its maximum also takes C's {@code COLOUR_L_GREEN} branch.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns light green when val is above max")
        void returnsLightGreenWhenAboveMax() throws Exception {
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(15, 10));
        }

        /**
         * Negative values are not special-cased in C's ternary; the same {@code val < max} test
         * applies unchanged.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("compares negative values the same way")
        void comparesNegativeValues() throws Exception {
            assertEquals(ColourEnum.COLOUR_YELLOW, invoke(-5, 0));
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(0, -5));
        }
    }

    /**
     * Tests the private {@code maxColour(long, long)} against C's {@code max_color}
     * ({@code [C] src/ui-player.c:682-685}).
     *
     * <p>C's {@code max_color} only ever takes {@code int} — {@code player->exp} and
     * {@code player->max_exp} are both {@code int32_t} — so this overload has no direct C
     * counterpart of its own; it exists purely because
     * {@link PlayerStatusView#experience()}/{@link PlayerStatusView#maxExperience()} are held as
     * {@code long} on the Java side. The formula is identical to the {@code (int, int)} overload
     * covered by {@link MaxColour}, so this class repeats its three boundary cases and adds one
     * proving the wider type: a pair of values outside {@code int} range, which the {@code (int,
     * int)} overload could not even accept.
     *
     * <p>Class MaxColourLong coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("maxColour(long, long)")
    class MaxColourLong {

        /**
         * Invokes the private method under test.
         *
         * @param val the value being displayed
         * @param max the value's recorded maximum
         * @return the chosen colour
         * @throws Exception if the method cannot be reached or throws
         */
        private ColourEnum invoke(long val, long max) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("maxColour", long.class, long.class);
            method.setAccessible(true);
            return (ColourEnum) method.invoke(uiPlayer, val, max);
        }

        /**
         * A value below its maximum takes C's {@code COLOUR_YELLOW} branch.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns yellow when val is below max")
        void returnsYellowWhenBelowMax() throws Exception {
            assertEquals(ColourEnum.COLOUR_YELLOW, invoke(5L, 10L));
        }

        /**
         * The boundary: {@code val == max} is not less than {@code max}, so it falls into C's
         * {@code COLOUR_L_GREEN} branch rather than the yellow one.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns light green when val equals max")
        void returnsLightGreenWhenEqualToMax() throws Exception {
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(10L, 10L));
        }

        /**
         * A value above its maximum also takes C's {@code COLOUR_L_GREEN} branch.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns light green when val is above max")
        void returnsLightGreenWhenAboveMax() throws Exception {
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(15L, 10L));
        }

        /**
         * Negative values are not special-cased in C's ternary; the same {@code val < max} test
         * applies unchanged.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("compares negative values the same way")
        void comparesNegativeValues() throws Exception {
            assertEquals(ColourEnum.COLOUR_YELLOW, invoke(-5L, 0L));
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(0L, -5L));
        }

        /**
         * Values outside {@code int} range are the one case this overload can reach that the
         * {@code (int, int)} overload cannot; the comparison still behaves identically to C's
         * narrower {@code int} version because the formula never depends on the operand width.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("compares values outside int range the same way")
        void comparesOutOfIntRangeValues() throws Exception {
            long beyondIntMax = (long) Integer.MAX_VALUE + 1_000_000L;
            assertEquals(ColourEnum.COLOUR_YELLOW, invoke(beyondIntMax, beyondIntMax + 1));
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, invoke(beyondIntMax + 1, beyondIntMax));
        }
    }

    /**
     * Tests the private {@code getPanelTopLeft()} against C's {@code get_panel_topleft}
     * ({@code [C] src/ui-player.c:698-709}).
     *
     * <p>C's version allocates a six-row panel and fills it, in order, with Name, Race, Class,
     * Title, HP and SP, each row in {@code COLOUR_L_BLUE}. This installs a
     * {@link PlayerStatusView} fixture carrying known values for all six and checks that the
     * returned {@code Panel}'s rows match in order, colour, label and formatted value.
     * {@code Panel} and {@code PanelLine} are both private non-static nested classes, so every
     * test reaches them, and the fields of the rows they produce, by reflection.
     *
     * <p>Class GetPanelTopLeft coded on 260925, commented in full on 260925.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getPanelTopLeft")
    class GetPanelTopLeft {

        private PlayerStatusView savedStatusView;

        /**
         * Saves the status-view cache so this test's fixture cannot leak into another test, then
         * installs one carrying known values for the six fields this method reads.
         */
        @BeforeEach
        void installFixture() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            PlayerEventStatusUpdate.updatePlayerStatusView(new PlayerStatusView(
                    "Legolas", "Rogue", "Elf", "Ranger", 0, 0, 0L, 0L, 0L,
                    42, 50, 8, 12, 0, 0,
                    new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0},
                    new String[]{"STR", "INT", "WIS", "DEX", "CON"},
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, 0, null, null, null, null, null, null,
                    0, 0, 0, 0));
        }

        /**
         * Restores the status view saved by {@link #installFixture()}.
         */
        @AfterEach
        void restoreFixture() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
        }

        /**
         * Invokes the private method under test.
         *
         * @return the built {@code Panel}, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("getPanelTopLeft");
            method.setAccessible(true);
            return method.invoke(uiPlayer);
        }

        /**
         * Reads the {@code lines} field off a returned {@code Panel} by reflection.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its rows, each a private {@code PanelLine} typed as {@link Object}
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * Reads a {@code PanelLine} row's colour via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code attribute}
         * @throws Exception if the method cannot be reached
         */
        private ColourEnum readAttribute(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getAttribute");
            method.setAccessible(true);
            return (ColourEnum) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's label via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code label}
         * @throws Exception if the method cannot be reached
         */
        private String readLabel(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getLabel");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's value via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code value}
         * @throws Exception if the method cannot be reached
         */
        private String readValue(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Six rows, in C's order, each carrying {@code COLOUR_L_BLUE} and the matching field from
         * the installed {@link PlayerStatusView}.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("builds Name/Race/Class/Title/HP/SP in order, all light blue")
        void buildsSixRowsInOrder() throws Exception {
            Object panel = invoke();
            List<?> lines = readLines(panel);

            assertEquals(6, lines.size());

            String[] expectedLabels = {"Name", "Race", "Class", "Title", "HP", "SP"};
            String[] expectedValues = {"Legolas", "Elf", "Ranger", "Rogue", "42/50", "8/12"};

            for (int i = 0; i < 6; i++) {
                Object line = lines.get(i);
                assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, readAttribute(line), "index " + i + " colour");
                assertEquals(expectedLabels[i], readLabel(line), "index " + i + " label");
                assertEquals(expectedValues[i], readValue(line), "index " + i + " value");
            }
        }

        /**
         * The panel is allocated with room for exactly six rows, matching C's
         * {@code panel_allocate(6)} call.
         *
         * @throws Exception if the method or its field cannot be reached
         */
        @Test
        @DisplayName("allocates a six-row panel")
        void allocatesSixRowCapacity() throws Exception {
            Object panel = invoke();
            Field field = panel.getClass().getDeclaredField("max");
            field.setAccessible(true);
            assertEquals(6, (int) field.get(panel));
        }
    }

    /**
     * Tests the private {@code getPanelMisc()} against C's {@code get_panel_misc}
     * ({@code [C] src/ui-player.c}, function {@code get_panel_misc}).
     *
     * <p>C's version allocates a seven-row panel and fills it, in order, with Age, Height,
     * Weight, a blank "Turns used:" label, Game, Standard and Resting, each row in
     * {@code COLOUR_L_BLUE}. This installs a {@link PlayerCharSheetView}/{@link PlayerStatusView}
     * fixture carrying known values for all six numeric fields and checks that the returned
     * {@code Panel}'s rows match in order, colour, label and formatted value — including the
     * trailing literal inch mark on Height ({@code "%d'%d\""}) that this method's stage-1 fix
     * restored. {@code Panel} and {@code PanelLine} are both private non-static nested classes, so
     * every test reaches them, and the fields of the rows they produce, by reflection.
     *
     * <p>Class GetPanelMisc coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getPanelMisc")
    class GetPanelMisc {

        private PlayerStatusView savedStatusView;
        private PlayerCharSheetView savedCharSheetView;

        /**
         * Saves both view caches so this test's fixture cannot leak into another test, then
         * installs ones carrying known values for the six fields this method reads.
         */
        @BeforeEach
        void installFixture() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();

            PlayerEventStatusUpdate.updatePlayerStatusView(new PlayerStatusView(
                    "Test", "Test", "Test", "Test", 0, 0, 0L, 0L, 0L,
                    0, 0, 0, 0, 0, 0,
                    new int[0], new int[0], new String[0],
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, 0, null, null, null, null, null, null,
                    0, 12345, 2550, 7));
            PlayerEventStatusUpdate.updatePlayerCharSheetView(new PlayerCharSheetView(0, true,
                    new int[0], new int[0], new int[0], new int[0], new int[0], 0, 0,
                    new long[0], 0, 71, 203, 42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, false));
        }

        /**
         * Restores the views saved by {@link #installFixture()}.
         */
        @AfterEach
        void restoreFixture() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
        }

        /**
         * Invokes the private method under test.
         *
         * @return the built {@code Panel}, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("getPanelMisc");
            method.setAccessible(true);
            return method.invoke(uiPlayer);
        }

        /**
         * Reads the {@code lines} field off a returned {@code Panel} by reflection.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its rows, each a private {@code PanelLine} typed as {@link Object}
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * Reads a {@code PanelLine} row's colour via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code attribute}
         * @throws Exception if the method cannot be reached
         */
        private ColourEnum readAttribute(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getAttribute");
            method.setAccessible(true);
            return (ColourEnum) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's label via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code label}
         * @throws Exception if the method cannot be reached
         */
        private String readLabel(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getLabel");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's value via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code value}
         * @throws Exception if the method cannot be reached
         */
        private String readValue(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Seven rows, in C's order, each carrying {@code COLOUR_L_BLUE} and the matching value
         * derived from the installed fixture — Height split into feet/inches with its trailing
         * inch mark, Weight split into stone/pounds, Standard divided by 100, and the label-only
         * "Turns used:" spacer carrying an empty value.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("builds Age/Height/Weight/Turns-used/Game/Standard/Resting in order, all light blue")
        void buildsSevenRowsInOrder() throws Exception {
            Object panel = invoke();
            List<?> lines = readLines(panel);

            assertEquals(7, lines.size());

            String[] expectedLabels = {"Age", "Height", "Weight", "Turns used:", "Game", "Standard",
                    "Resting"};
            String[] expectedValues = {"42", "5'11\"", "14st 7lb", "", "12345", "25", "7"};

            for (int i = 0; i < 7; i++) {
                Object line = lines.get(i);
                assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, readAttribute(line), "index " + i + " colour");
                assertEquals(expectedLabels[i], readLabel(line), "index " + i + " label");
                assertEquals(expectedValues[i], readValue(line), "index " + i + " value");
            }
        }

        /**
         * The panel is allocated with room for exactly seven rows, matching C's
         * {@code panel_allocate(7)} call.
         *
         * @throws Exception if the method or its field cannot be reached
         */
        @Test
        @DisplayName("allocates a seven-row panel")
        void allocatesSevenRowCapacity() throws Exception {
            Object panel = invoke();
            Field field = panel.getClass().getDeclaredField("max");
            field.setAccessible(true);
            assertEquals(7, (int) field.get(panel));
        }
    }

    /**
     * Tests {@link UIPlayer#showAdvanceExperience()} against C's {@code show_adv_exp}
     * ({@code [C] src/ui-player.c:640-652}).
     *
     * <p>Expected values are derived from the C source directly: the
     * {@code player_exp[player->lev - 1]} lookup against a one-based level — the off-by-one this
     * method's fix was about, since {@code expToLevel()} is zero-based the same way
     * {@code player_exp[]} is — the {@code * player->expfact / 100L} scaling with C's truncating
     * {@code int32_t} division, and the {@code "********"} literal once {@code player->lev} reaches
     * {@code PY_MAX_LEVEL}. The method is private, so every test reaches it by reflection.
     *
     * <p>Class ShowAdvanceExperience coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("showAdvanceExperience")
    class ShowAdvanceExperience {

        private PlayerStatusView savedStatusView;
        private PlayerCharSheetView savedCharSheetView;
        private int savedMaxLevel;

        /**
         * Saves the status-view, char-sheet-view and {@code PY_MAX_LEVEL} state so this test's
         * fixtures cannot leak into another test.
         */
        @BeforeEach
        void saveGlobalState() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
            savedMaxLevel = ChannelRegistry.getPYMaxLevel();
        }

        /**
         * Restores the state saved by {@link #saveGlobalState()}.
         */
        @AfterEach
        void restoreGlobalState() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
            ChannelRegistry.setPYMaxLevel(savedMaxLevel);
        }

        /**
         * Builds a {@link PlayerStatusView} carrying only the {@code level} this method reads;
         * every other field is irrelevant to {@code showAdvanceExperience}.
         *
         * @param level the player's current level
         * @return the view
         */
        private PlayerStatusView statusViewWithLevel(int level) {
            return new PlayerStatusView(
                    "Test", "Test", "Test", "Test", level, 0, 0L, 0L, 0L,
                    0, 0, 0, 0, 0, 0,
                    new int[0], new int[0], new String[0],
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, 0, null, null, null, null, null, null,
                    0, 0, 0, 0);
        }

        /**
         * Builds a {@link PlayerCharSheetView} carrying only the two fields this method reads.
         *
         * @param expToLevel the zero-based experience-to-level table
         * @param expFactor  the scaling percentage
         * @return the view
         */
        private PlayerCharSheetView charSheetViewWith(long[] expToLevel, int expFactor) {
            return new PlayerCharSheetView(0, true, new int[0], new int[0], new int[0],
                    new int[0], new int[0], 0, 0, expToLevel, expFactor, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, false);
        }

        /**
         * Invokes the private method under test.
         *
         * @return its result
         * @throws Exception if the method cannot be reached or throws
         */
        private String invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("showAdvanceExperience");
            method.setAccessible(true);
            return (String) method.invoke(uiPlayer);
        }

        /**
         * The ordinary path: an exact-division scaling below the cap.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the scaled experience-to-next-level figure below the cap")
        void returnsScaledFigureBelowCap() throws Exception {
            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithLevel(10));
            long[] expToLevel = new long[10];
            expToLevel[9] = 650L;
            PlayerEventStatusUpdate.updatePlayerCharSheetView(charSheetViewWith(expToLevel, 150));

            assertEquals("975", invoke());
        }

        /**
         * The boundary this method's stage-1 fix was about: at level 1, C reads
         * {@code player_exp[0]}, not {@code player_exp[1]}. A regression back to indexing
         * {@code expToLevel()} at {@code level()} rather than {@code level() - 1} would read 25
         * here instead of 10.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("indexes expToLevel at level - 1, not level")
        void indexesAtLevelMinusOne() throws Exception {
            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithLevel(1));
            PlayerEventStatusUpdate.updatePlayerCharSheetView(
                    charSheetViewWith(new long[]{10L, 25L}, 100));

            assertEquals("10", invoke());
        }

        /**
         * C's {@code int32_t} scaling truncates rather than rounds: {@code 7 * 133 / 100} is
         * {@code 9.31}, which C's integer division floors to {@code 9}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("truncates the scaling division rather than rounding")
        void truncatesScalingDivision() throws Exception {
            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithLevel(5));
            long[] expToLevel = new long[5];
            expToLevel[4] = 7L;
            PlayerEventStatusUpdate.updatePlayerCharSheetView(charSheetViewWith(expToLevel, 133));

            assertEquals("9", invoke());
        }

        /**
         * Still computes the figure one level below the cap, the boundary just short of C's
         * {@code player->lev < PY_MAX_LEVEL} failing.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("still computes the figure one level below the cap")
        void computesFigureOneLevelBelowCap() throws Exception {
            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithLevel(49));
            long[] expToLevel = new long[50];
            expToLevel[48] = 5_000_000L;
            PlayerEventStatusUpdate.updatePlayerCharSheetView(charSheetViewWith(expToLevel, 100));

            assertEquals("5000000", invoke());
        }

        /**
         * At the cap, C's {@code player->lev < PY_MAX_LEVEL} test fails and the literal placeholder
         * is returned without ever touching {@code player_exp}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the literal placeholder once level reaches the cap")
        void returnsPlaceholderAtCap() throws Exception {
            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithLevel(50));
            PlayerEventStatusUpdate.updatePlayerCharSheetView(charSheetViewWith(new long[50], 100));

            assertEquals("********", invoke());
        }
    }

    /**
     * Tests {@link UIPlayer#getPanelMidLeft()} against C's {@code get_panel_midleft}
     * ({@code [C] src/ui-player.c:715-734}).
     *
     * <p>Expected values are derived from the C source directly: nine rows in C's exact call
     * order (Level, Cur Exp, Max Exp, Adv Exp, a blank spacer, Gold, Burden, Overweight, Max
     * Depth), the {@code attr} colour C computes once from {@code weight_remaining(player)} and
     * reuses for both Burden and Overweight, and the {@code "%.1f lb"}/{@code "%d.%d lb"} value
     * formats C's own {@code panel_line} calls use. The level/experience fixture is deliberately
     * chosen with each maximum strictly above its current value, so the Level and Cur Exp rows
     * exercise {@link UIPlayer#maxColour(int, int)}/{@link UIPlayer#maxColour(long, long)}'s
     * yellow branch rather than coincidentally landing on green — those methods' own full branch
     * coverage lives in {@link MaxColour}/{@link MaxColourLong}. The weight fixture is the same
     * over-limit pair {@link WeightRemaining#goesNegativeWhenOverburdened()} already proves
     * produces {@code -701}, so Burden/Overweight exercise the red, over-burdened branch. The
     * method is private, so every test reaches it by reflection.
     *
     * <p>Class GetPanelMidLeft coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getPanelMidLeft")
    class GetPanelMidLeft {

        private PlayerStatusView savedStatusView;
        private PlayerCharSheetView savedCharSheetView;
        private int savedMaxLevel;

        /**
         * Saves the status-view, char-sheet-view and {@code PY_MAX_LEVEL} state so this test's
         * fixtures cannot leak into another test, then installs known values for every field the
         * method reads (directly, or via {@link UIPlayer#weightRemaining()},
         * {@link UIPlayer#showAdvanceExperience()} and {@link UIPlayer#showDepth()}).
         */
        @BeforeEach
        void installFixture() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
            savedMaxLevel = ChannelRegistry.getPYMaxLevel();

            ChannelRegistry.setPYMaxLevel(50);
            PlayerEventStatusUpdate.updatePlayerStatusView(new PlayerStatusView(
                    "Test", "Test", "Test", "Test", 10, 20, 500L, 1000L, 250L,
                    0, 0, 0, 0, 0, 0,
                    new int[0], new int[0], new String[0],
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, 12, null, null, null, null, null, null,
                    0, 0, 0, 0));
            long[] expToLevel = new long[10];
            expToLevel[9] = 650L;
            PlayerEventStatusUpdate.updatePlayerCharSheetView(new PlayerCharSheetView(0, true,
                    new int[0], new int[0], new int[0], new int[0], new int[0],
                    1000, 5, expToLevel, 150, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, false));
        }

        /**
         * Restores the state saved by {@link #installFixture()}.
         */
        @AfterEach
        void restoreFixture() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
            ChannelRegistry.setPYMaxLevel(savedMaxLevel);
        }

        /**
         * Invokes the private method under test.
         *
         * @return the built {@code Panel}, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("getPanelMidLeft");
            method.setAccessible(true);
            return method.invoke(uiPlayer);
        }

        /**
         * Reads the {@code lines} field off a returned {@code Panel} by reflection.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its rows, each a private {@code PanelLine} typed as {@link Object}
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * Reads a {@code PanelLine} row's colour via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code attribute}
         * @throws Exception if the method cannot be reached
         */
        private ColourEnum readAttribute(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getAttribute");
            method.setAccessible(true);
            return (ColourEnum) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's label via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code label}
         * @throws Exception if the method cannot be reached
         */
        private String readLabel(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getLabel");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's value via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code value}
         * @throws Exception if the method cannot be reached
         */
        private String readValue(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Nine rows in C's exact order, each carrying the label, colour and formatted value C's
         * own {@code panel_line}/{@code panel_space} calls would produce from the installed
         * fixture: Level 10 below a max of 20 (yellow), Cur Exp 500 below a max of 1000 (yellow),
         * Max Exp 1000 (green), Adv Exp {@code (650 * 150) / 100 == 975} (green), a blank spacer,
         * Gold 250 (green), Burden {@code 1000 / 10.0 == "100.0 lb"} (red, since weightRemaining()
         * is negative), Overweight {@code -(-701) / 10 == 70}, {@code 701 % 10 == 1} formatted as
         * {@code "70.1 lb"} (red), and Max Depth {@code "600' (L12)"} (green).
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("builds all nine rows in C's order with matching colours and values")
        void buildsNineRowsInOrder() throws Exception {
            Object panel = invoke();
            List<?> lines = readLines(panel);

            assertEquals(9, lines.size());

            ColourEnum green = ColourEnum.COLOUR_LIGHT_GREEN;
            ColourEnum yellow = ColourEnum.COLOUR_YELLOW;
            ColourEnum red = ColourEnum.COLOUR_LIGHT_RED;
            ColourEnum white = ColourEnum.COLOUR_WHITE;

            ColourEnum[] expectedColours = {yellow, yellow, green, green, white, green, red, red, green};
            String[] expectedLabels =
                    {"Level", "Cur Exp", "Max Exp", "Adv Exp", "", "Gold", "Burden", "Overweight", "Max Depth"};
            String[] expectedValues =
                    {"10", "500", "1000", "975", "", "250", "100.0 lb", "70.1 lb", "600' (L12)"};

            for (int i = 0; i < 9; i++) {
                Object line = lines.get(i);
                assertEquals(expectedColours[i], readAttribute(line), "index " + i + " colour");
                assertEquals(expectedLabels[i], readLabel(line), "index " + i + " label");
                assertEquals(expectedValues[i], readValue(line), "index " + i + " value");
            }
        }

        /**
         * The panel is allocated with room for exactly nine rows, matching C's
         * {@code panel_allocate(9)} call.
         *
         * @throws Exception if the method or its field cannot be reached
         */
        @Test
        @DisplayName("allocates a nine-row panel")
        void allocatesNineRowCapacity() throws Exception {
            Object panel = invoke();
            Field field = panel.getClass().getDeclaredField("max");
            field.setAccessible(true);
            assertEquals(9, (int) field.get(panel));
        }
    }

    /**
     * Tests {@link UIPlayer#getPanelCombat()} against C's {@code get_panel_combat}
     * ({@code [C] src/ui-player.c}, function {@code get_panel_combat}).
     *
     * <p>C's version allocates a nine-row panel and fills it, in order, with Armour, a blank
     * spacer, Melee, To-hit, Blows, another blank spacer, Shoot to-dam, To-hit and Shots, each row
     * (bar the spacers) in {@code COLOUR_L_BLUE}. This installs a {@link PlayerCharSheetView}/
     * {@link PlayerStatusView} fixture carrying known, distinct values for every field the method
     * reads and checks that the returned {@code Panel}'s rows match in order, colour, label and
     * formatted value — deliberately choosing values ({@code meleeSkill=55, bthPlusAdj=3}) whose
     * {@code bth} does not divide evenly by ten, so a stray truncation or a dropped {@code * 10}
     * (stage 1's original ranged-{@code bth} finding) would show up as a wrong "To-hit" figure,
     * and choosing {@code numBlows}/{@code numShots} values whose formatted rows would read
     * identically whether the separator is "/" or "." except for that one character, so a
     * regression on stage 1's other original finding (the "Blows" row's format string) would also
     * be caught. {@code Panel} and {@code PanelLine} are both private non-static nested classes, so
     * every test reaches them, and the fields of the rows they produce, by reflection.
     *
     * <p>Class GetPanelCombat coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getPanelCombat")
    class GetPanelCombat {

        private PlayerStatusView savedStatusView;
        private PlayerCharSheetView savedCharSheetView;

        /**
         * Saves both view caches so this test's fixture cannot leak into another test, then
         * installs ones carrying known values for the fields this method reads: {@code armourClass}
         * on the status view, and {@code toA}/{@code toD}/{@code toH}/{@code meleeSkill}/
         * {@code shootSkill}/{@code bthPlusAdj}/{@code meleeDice}/{@code meleeSides}/
         * {@code numBlows}/{@code numShots} on the char-sheet view.
         */
        @BeforeEach
        void installFixture() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
            savedCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();

            PlayerEventStatusUpdate.updatePlayerStatusView(new PlayerStatusView(
                    "Test", "Test", "Test", "Test", 0, 0, 0L, 0L, 0L,
                    0, 0, 0, 0, 45, 0,
                    new int[0], new int[0], new String[0],
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, 0, null, null, null, null, null, null,
                    0, 0, 0, 0));
            PlayerEventStatusUpdate.updatePlayerCharSheetView(new PlayerCharSheetView(0, true,
                    new int[0], new int[0], new int[0], new int[0], new int[0], 0, 0,
                    new long[0], 0, 0, 0, 0,
                    12, 7, -2, 55, 27, 3, 3, 8, 234, 47,
                    0, 0, 0, 0, 0, 0, 0, 0, false));
        }

        /**
         * Restores the views saved by {@link #installFixture()}.
         */
        @AfterEach
        void restoreFixture() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
            PlayerEventStatusUpdate.updatePlayerCharSheetView(savedCharSheetView);
        }

        /**
         * Invokes the private method under test.
         *
         * @return the built {@code Panel}, typed as {@link Object} since the class is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("getPanelCombat");
            method.setAccessible(true);
            return method.invoke(uiPlayer);
        }

        /**
         * Reads the {@code lines} field off a returned {@code Panel} by reflection.
         *
         * @param panel the {@code Panel} instance to read from
         * @return its rows, each a private {@code PanelLine} typed as {@link Object}
         * @throws Exception if the field cannot be reached
         */
        private List<?> readLines(Object panel) throws Exception {
            Field field = panel.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            return (List<?>) field.get(panel);
        }

        /**
         * Reads a {@code PanelLine} row's colour via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code attribute}
         * @throws Exception if the method cannot be reached
         */
        private ColourEnum readAttribute(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getAttribute");
            method.setAccessible(true);
            return (ColourEnum) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's label via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code label}
         * @throws Exception if the method cannot be reached
         */
        private String readLabel(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getLabel");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Reads a {@code PanelLine} row's value via its public getter.
         *
         * @param line the row, typed as {@link Object} since the class is private
         * @return its {@code value}
         * @throws Exception if the method cannot be reached
         */
        private String readValue(Object line) throws Exception {
            Method method = line.getClass().getDeclaredMethod("getValue");
            method.setAccessible(true);
            return (String) method.invoke(line);
        }

        /**
         * Nine rows in C's exact order, each carrying the label, colour and formatted value C's
         * own {@code panel_line}/{@code panel_space} calls would produce from the installed
         * fixture: Armour {@code "[45,+12]"}, a blank spacer, Melee {@code "3d8,+7"}, To-hit
         * {@code (55 * 10 / 3) / 10 == 18} paired with {@code toH == -2} as {@code "18,-2"}, Blows
         * {@code 234 / 100 == 2} and {@code 234 / 10 % 10 == 3} as {@code "2.3/turn"}, another blank
         * spacer, Shoot to-dam {@code "+0"} (C's fixed zero default), To-hit
         * {@code (27 * 10 / 3) / 10 == 9} paired with the same {@code toH == -2} reused as
         * {@code "9,-2"}, and Shots {@code 47 / 10 == 4} and {@code 47 % 10 == 7} as
         * {@code "4.7/turn"}.
         *
         * @throws Exception if the method cannot be reached or throws
         */
        @Test
        @DisplayName("builds all nine rows in C's order with matching colours and values")
        void buildsNineRowsInOrder() throws Exception {
            Object panel = invoke();
            List<?> lines = readLines(panel);

            assertEquals(9, lines.size());

            ColourEnum blue = ColourEnum.COLOUR_LIGHT_BLUE;
            ColourEnum white = ColourEnum.COLOUR_WHITE;

            ColourEnum[] expectedColours = {blue, white, blue, blue, blue, white, blue, blue, blue};
            String[] expectedLabels = {"Armour", "", "Melee", "To-hit", "Blows", "",
                    "Shoot to-dam", "To-hit", "Shots"};
            String[] expectedValues = {"[45,+12]", "", "3d8,+7", "18,-2", "2.3/turn", "",
                    "+0", "9,-2", "4.7/turn"};

            for (int i = 0; i < 9; i++) {
                Object line = lines.get(i);
                assertEquals(expectedColours[i], readAttribute(line), "index " + i + " colour");
                assertEquals(expectedLabels[i], readLabel(line), "index " + i + " label");
                assertEquals(expectedValues[i], readValue(line), "index " + i + " value");
            }
        }

        /**
         * The panel is allocated with room for exactly nine rows, matching C's
         * {@code panel_allocate(9)} call.
         *
         * @throws Exception if the method or its field cannot be reached
         */
        @Test
        @DisplayName("allocates a nine-row panel")
        void allocatesNineRowCapacity() throws Exception {
            Object panel = invoke();
            Field field = panel.getClass().getDeclaredField("max");
            field.setAccessible(true);
            assertEquals(9, (int) field.get(panel));
        }
    }

    /**
     * Tests {@link UIPlayer#showDepth()} against C's {@code show_depth}
     * ({@code [C] src/ui-player.c:656-665}).
     *
     * <p>C has exactly two branches: {@code player->max_depth == 0} returns the literal
     * {@code "Town"} without touching {@code strnfmt} at all, and anything else formats
     * {@code "%d' (L%d)"} from {@code max_depth * 50} and {@code max_depth} directly, with no
     * separate rounding or clamping to trip over. The method is private, so every test reaches it
     * by reflection.
     *
     * <p>Class ShowDepth coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("showDepth")
    class ShowDepth {

        private PlayerStatusView savedStatusView;

        /**
         * Saves the status-view cache so this test's fixture cannot leak into another test.
         */
        @BeforeEach
        void saveStatusView() {
            savedStatusView = PlayerEventStatusUpdate.getPlayerStatusView();
        }

        /**
         * Restores the status view saved by {@link #saveStatusView()}.
         */
        @AfterEach
        void restoreStatusView() {
            PlayerEventStatusUpdate.updatePlayerStatusView(savedStatusView);
        }

        /**
         * Builds a {@link PlayerStatusView} carrying only the {@code maxDepth} this method reads;
         * every other field is irrelevant to {@code showDepth}.
         *
         * @param maxDepth the player's recorded maximum dungeon level
         * @return the view
         */
        private PlayerStatusView statusViewWithMaxDepth(int maxDepth) {
            return new PlayerStatusView(
                    "Test", "Test", "Test", "Test", 0, 0, 0L, 0L, 0L,
                    0, 0, 0, 0, 0, 0,
                    new int[0], new int[0], new String[0],
                    0, 0, false, false, false, false, false, false, false, false, false, false,
                    0, maxDepth, null, null, null, null, null, null,
                    0, 0, 0, 0);
        }

        /**
         * Invokes the private method under test.
         *
         * @return its result
         * @throws Exception if the method cannot be reached or throws
         */
        private String invoke() throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("showDepth");
            method.setAccessible(true);
            return (String) method.invoke(uiPlayer);
        }

        /**
         * The boundary C tests explicitly: a recorded maximum depth of zero returns the literal
         * placeholder without ever reaching {@code strnfmt}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("returns the literal \"Town\" at depth zero")
        void returnsTownAtDepthZero() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithMaxDepth(0));

            assertEquals("Town", invoke());
        }

        /**
         * The ordinary path: depth one level below the surface, {@code 1 * 50 == 50}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("formats the shallowest non-Town depth as 50' (L1)")
        void formatsShallowestDepth() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithMaxDepth(1));

            assertEquals("50' (L1)", invoke());
        }

        /**
         * An ordinary mid-game depth, {@code 12 * 50 == 600}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("formats an ordinary depth as feet and dungeon level")
        void formatsOrdinaryDepth() throws Exception {
            PlayerEventStatusUpdate.updatePlayerStatusView(statusViewWithMaxDepth(12));

            assertEquals("600' (L12)", invoke());
        }
    }

    /**
     * Tests the private {@code likert(int, int)} against C's {@code likert(int x, int y,
     * uint8_t *attr)} ({@code [C] src/ui-player.c:278-348}).
     *
     * <p>Expected values are derived from the C source directly: the {@code y <= 0} divisor
     * guard, the {@code x < 0} early return, and the eleven {@code switch ((x / y))} buckets —
     * including the two bands ("Good" and "Very Good") C colours yellow rather than the green
     * and blue this port's colours originally, and mistakenly, used. The method and the
     * {@code StringAndColour} record it returns are both private, so every test reaches them by
     * reflection.
     *
     * <p>Class Likert coded on 260926, commented in full on 260926.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("likert")
    class Likert {

        /**
         * Invokes the private method under test.
         *
         * @param skill the raw value to rate
         * @param level the divisor applied before bucketing
         * @return the resulting {@code StringAndColour}, typed as {@link Object} since the
         * record is private
         * @throws Exception if the method cannot be reached or throws
         */
        private Object invoke(int skill, int level) throws Exception {
            Method method = UIPlayer.class.getDeclaredMethod("likert", int.class, int.class);
            method.setAccessible(true);
            return method.invoke(uiPlayer, skill, level);
        }

        /**
         * Reads a {@code StringAndColour} record's {@code string} component.
         *
         * @param result the record instance, typed as {@link Object} since the class is private
         * @return its rating string
         * @throws Exception if the accessor cannot be reached
         */
        private String readString(Object result) throws Exception {
            Method method = result.getClass().getDeclaredMethod("string");
            method.setAccessible(true);
            return (String) method.invoke(result);
        }

        /**
         * Reads a {@code StringAndColour} record's {@code attr} component.
         *
         * @param result the record instance, typed as {@link Object} since the class is private
         * @return its display colour
         * @throws Exception if the accessor cannot be reached
         */
        private ColourEnum readAttr(Object result) throws Exception {
            Method method = result.getClass().getDeclaredMethod("attr");
            method.setAccessible(true);
            return (ColourEnum) method.invoke(result);
        }

        /**
         * Asserts the rating and colour {@code likert(skill, 1)} produces.
         *
         * @param skill          the value to rate
         * @param expectedString the expected rating string
         * @param expectedColour the expected display colour
         * @throws Exception if the method cannot be reached
         */
        private void assertRating(int skill, String expectedString, ColourEnum expectedColour)
                throws Exception {
            Object result = invoke(skill, 1);
            assertEquals(expectedString, readString(result), "skill " + skill);
            assertEquals(expectedColour, readAttr(result), "skill " + skill);
        }

        /**
         * A negative {@code skill} always rates "Very Bad" in red, regardless of {@code level} —
         * C's {@code x < 0} check runs before the divisor is ever applied
         * ({@code [C] ui-player.c:284-287}).
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("rates a negative skill as Very Bad regardless of level")
        void ratesNegativeSkillAsVeryBad() throws Exception {
            Object result = invoke(-1, 5);
            assertEquals("Very Bad", readString(result));
            assertEquals(ColourEnum.COLOUR_RED, readAttr(result));
        }

        /**
         * C's "paranoia" guard treats a non-positive {@code level} as {@code 1}
         * ({@code [C] ui-player.c:281}), so a {@code level} of zero or negative produces the same
         * rating as {@code level == 1} for the same {@code skill}.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("treats a non-positive level as one")
        void treatsNonPositiveLevelAsOne() throws Exception {
            Object viaOne = invoke(5, 1);
            Object viaZero = invoke(5, 0);
            Object viaNegative = invoke(5, -3);

            assertEquals(readString(viaOne), readString(viaZero));
            assertEquals(readAttr(viaOne), readAttr(viaZero));
            assertEquals(readString(viaOne), readString(viaNegative));
            assertEquals(readAttr(viaOne), readAttr(viaNegative));
        }

        /**
         * Every named bucket and its colour, walked at {@code level == 1} so {@code skill} is the
         * value bucketed directly, matching {@code ui-player.c}'s {@code switch} clause for
         * clause — including the two bands, "Good" and "Very Good", that are yellow rather than
         * green or blue.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("buckets every rating band with its C colour")
        void bucketsEveryRatingBand() throws Exception {
            assertRating(0, "Bad", ColourEnum.COLOUR_RED);
            assertRating(1, "Bad", ColourEnum.COLOUR_RED);
            assertRating(2, "Poor", ColourEnum.COLOUR_RED);
            assertRating(3, "Fair", ColourEnum.COLOUR_YELLOW);
            assertRating(4, "Fair", ColourEnum.COLOUR_YELLOW);
            assertRating(5, "Good", ColourEnum.COLOUR_YELLOW);
            assertRating(6, "Very Good", ColourEnum.COLOUR_YELLOW);
            assertRating(7, "Excellent", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(8, "Excellent", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(9, "Superb", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(13, "Superb", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(14, "Heroic", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(17, "Heroic", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(18, "Legendary", ColourEnum.COLOUR_LIGHT_GREEN);
            assertRating(100, "Legendary", ColourEnum.COLOUR_LIGHT_GREEN);
        }

        /**
         * {@code level} divides {@code skill} with C's truncating integer division before
         * bucketing, not rounding — {@code 17 / 2 == 8}, landing in the "Excellent" band rather
         * than "Superb", which a rounded {@code 8.5} would reach.
         *
         * @throws Exception if the method cannot be reached
         */
        @Test
        @DisplayName("divides skill by level with integer truncation before bucketing")
        void dividesWithIntegerTruncation() throws Exception {
            Object result = invoke(17, 2);
            assertEquals("Excellent", readString(result));
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, readAttr(result));
        }
    }
}
