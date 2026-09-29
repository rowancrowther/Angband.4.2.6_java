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

package uk.co.jackoftradesltd.frontend.ui.globals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link UIRegistry}'s name-based lookups, the port of C's {@code ui_entry_lookup}/
 * {@code ui_entry_search} ({@code [C] ui-entry.c:1165-1208}) as applied across the registry's three
 * loaded lists.
 *
 * <p>Class UIRegistryTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
class UIRegistryTest {

    private static UIEntryRenderer renderer(String name) {
        return new UIEntryRenderer(name, UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE, "", "", "", 0, null);
    }

    private static UIEntryBase base(String name) {
        return new UIEntryBase(name, renderer("r"), CombinerName.NONE, List.of(),
                new Flag<>(ChannelEntryFlag.class), "desc");
    }

    private static UIEntry entry(String name) {
        return new UIEntry(name, null, ElementEnum.ELEM_NONE, StatElemType.NONE, null, CombinerName.NONE,
                List.of(), 0, "", new Flag<>(ChannelEntryFlag.class), "desc", "", "", "");
    }

    @BeforeEach
    void seedRegistry() {
        UIRegistry.setUIEntryRenderers(List.of(renderer("known_renderer")));
        UIRegistry.setUIEntryBases(List.of(base("known_base")));
        UIRegistry.setUIEntries(List.of(entry("known_entry")));
    }

    @Nested
    @DisplayName("getUIEntryRenderer")
    class GetUIEntryRenderer {

        @Test
        @DisplayName("finds a loaded renderer by name")
        void findsALoadedRenderer() {
            List<String> errors = new ArrayList<>();
            UIEntryRenderer result = UIRegistry.getUIEntryRenderer("known_renderer", errors);
            assertEquals("known_renderer", result.getName());
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("returns null and appends an error for an unknown name")
        void reportsAnUnknownName() {
            List<String> errors = new ArrayList<>();
            assertNull(UIRegistry.getUIEntryRenderer("no_such_renderer", errors));
            assertEquals(1, errors.size());
        }
    }

    @Nested
    @DisplayName("getUIEntryBase")
    class GetUIEntryBase {

        @Test
        @DisplayName("finds a loaded base by name")
        void findsALoadedBase() {
            assertEquals("known_base", UIRegistry.getUIEntryBase("known_base").getName());
        }

        @Test
        @DisplayName("returns null for an unknown name")
        void returnsNullForAnUnknownName() {
            assertNull(UIRegistry.getUIEntryBase("no_such_base"));
        }
    }

    @Nested
    @DisplayName("getUIEntry")
    class GetUIEntry {

        @Test
        @DisplayName("finds a loaded entry by name")
        void findsALoadedEntry() {
            assertEquals("known_entry", UIRegistry.getUIEntry("known_entry").getName());
        }

        @Test
        @DisplayName("returns null for an unknown name")
        void returnsNullForAnUnknownName() {
            assertNull(UIRegistry.getUIEntry("no_such_entry"));
        }
    }

    /**
     * Tests the lookups' edge cases: the unloaded-registry states and the exact-match rule, against
     * C's {@code ui_entry_search}, which compares with {@code strcmp} (case-sensitive, whole string).
     *
     * <p>Class LookupEdges coded on 260929, commented in full on 260929.
     */
    @Nested
    @DisplayName("lookup edge cases")
    class LookupEdges {

        @Test
        @DisplayName("names are matched case-sensitively, as strcmp does")
        void matchIsCaseSensitive() {
            assertNull(UIRegistry.getUIEntry("KNOWN_ENTRY"));
            assertNull(UIRegistry.getUIEntryBase("Known_Base"));
            assertNull(UIRegistry.getUIEntryRenderer("KNOWN_RENDERER", new ArrayList<>()));
        }

        @Test
        @DisplayName("names are matched whole, not by prefix")
        void matchIsWholeString() {
            assertNull(UIRegistry.getUIEntry("known"));
            assertNull(UIRegistry.getUIEntry("known_entry2"));
            assertNull(UIRegistry.getUIEntry(""));
        }

        @Test
        @DisplayName("picks the right record among several")
        void picksTheRightRecord() {
            UIEntry a = entry("a");
            UIEntry b = entry("b");
            UIRegistry.setUIEntries(List.of(a, b));
            assertSame(a, UIRegistry.getUIEntry("a"));
            assertSame(b, UIRegistry.getUIEntry("b"));
        }

        @Test
        @DisplayName("an empty registry finds nothing rather than throwing")
        void emptyRegistryReturnsNull() {
            UIRegistry.setUIEntries(List.of());
            assertNull(UIRegistry.getUIEntry("known_entry"));
        }

        @Test
        @DisplayName("an unloaded entry registry throws")
        void unloadedEntriesThrow() {
            UIRegistry.setUIEntries(null);
            assertThrows(IllegalStateException.class, () -> UIRegistry.getUIEntry("x"));
        }

        @Test
        @DisplayName("an unloaded base registry throws")
        void unloadedBasesThrow() {
            UIRegistry.setUIEntryBases(null);
            assertThrows(IllegalStateException.class, () -> UIRegistry.getUIEntryBase("x"));
        }

        @Test
        @DisplayName("an unloaded renderer registry reports through errors and returns null")
        void unloadedRenderersReportThroughErrors() {
            UIRegistry.setUIEntryRenderers(null);
            List<String> errors = new ArrayList<>();
            assertNull(UIRegistry.getUIEntryRenderer("x", errors));
            assertEquals(1, errors.size());
        }
    }

    /**
     * Tests the size constants against the {@code #define} values in C: {@code MAX_ENTRY_LABEL} and
     * {@code MAX_SHORTENED} in {@code ui-entry.c}, {@code MAX_PALETTE} in {@code ui-entry-renderers.c}.
     *
     * <p>Class Constants coded on 260929, commented in full on 260929.
     */
    @Nested
    @DisplayName("size constants")
    class Constants {

        @Test
        @DisplayName("MAX_ENTRY_LABEL is 80")
        void maxEntryLabel() {
            assertEquals(80, UIRegistry.MAX_ENTRY_LABEL);
        }

        @Test
        @DisplayName("MAX_SHORTENED is 10")
        void maxShortened() {
            assertEquals(10, UIRegistry.MAX_SHORTENED);
        }

        @Test
        @DisplayName("MAX_PALETTE is 64")
        void maxPalette() {
            assertEquals(64, UIRegistry.MAX_PALETTE);
        }
    }

    /**
     * Tests {@link UIRegistry#statNames} and {@link UIRegistry#statReducedNames} against the literal
     * values of C's {@code stat_names}/{@code stat_names_reduced} ({@code [C] ui-display.c:99-110}),
     * not against whatever the Java literals currently read.
     *
     * <p>Class StatNames coded on 260925, commented in full on 260925.
     */
    @Nested
    @DisplayName("statNames / statReducedNames")
    class StatNames {

        /**
         * {@code stat_names[STAT_MAX]}, {@code [C] ui-display.c:99-102}.
         */
        private static final String[] C_STAT_NAMES =
                {"STR: ", "INT: ", "WIS: ", "DEX: ", "CON: "};

        /**
         * {@code stat_names_reduced[STAT_MAX]}, {@code [C] ui-display.c:104-110}.
         */
        private static final String[] C_STAT_NAMES_REDUCED =
                {"Str: ", "Int: ", "Wis: ", "Dex: ", "Con: "};

        @Test
        @DisplayName("matches C's stat_names, including the trailing space")
        void statNamesMatchesC() {
            assertArrayEquals(C_STAT_NAMES, UIRegistry.statNames);
        }

        @Test
        @DisplayName("matches C's stat_names_reduced, including the trailing space")
        void statReducedNamesMatchesC() {
            assertArrayEquals(C_STAT_NAMES_REDUCED, UIRegistry.statReducedNames);
        }

        @Test
        @DisplayName("is sized to STAT_MAX, as the C arrays are")
        void bothArraysAreSizedToStatMax() {
            assertEquals(ChannelRegistry.STAT_MAX, UIRegistry.statNames.length);
            assertEquals(ChannelRegistry.STAT_MAX, UIRegistry.statReducedNames.length);
        }

        @Test
        @DisplayName("every label is 5 characters, so col+3 lands on the colon C's natural-max indicator overwrites")
        void everyLabelIsFiveCharactersWide() {
            for (String name : UIRegistry.statNames) {
                assertEquals(5, name.length());
                assertEquals(':', name.charAt(3));
            }
            for (String name : UIRegistry.statReducedNames) {
                assertEquals(5, name.length());
                assertEquals(':', name.charAt(3));
            }
        }
    }
}
