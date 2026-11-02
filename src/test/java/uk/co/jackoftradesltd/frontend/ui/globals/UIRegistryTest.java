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

import org.junit.jupiter.api.*;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;
import uk.co.jackoftradesltd.frontend.ui.entry.reader.UIEntryReader;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link UIRegistry}'s name lookups, the port of C's {@code ui_entry_lookup}/
 * {@code ui_entry_search} (in {@code ui-entry.c}) over the one shared entry list and of
 * {@code ui_entry_renderer_lookup} (in {@code ui-entry-renderers.c}) over the renderers.
 *
 * <p>Class UIRegistryTest coded on 260924, commented in full on 261102.
 *
 * @author Rowan Crowther
 */
class UIRegistryTest {

    private static UIEntryRenderer renderer(String name) {
        return new UIEntryRenderer(name, UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE, "", "", "", 0, null);
    }

    private static UIEntry entry(String name) {
        return entry(name, new Flag<>(ChannelEntryFlag.class));
    }

    private static UIEntry entry(String name, Flag<ChannelEntryFlag> flags) {
        return new UIEntry(name, null, ElementEnum.ELEM_NONE, StatElemType.NONE, null, CombinerName.NONE,
                List.of(), 0, "", flags, "desc", "", "", "");
    }

    private static UIEntry templateOnlyEntry(String name) {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
        return entry(name, flags);
    }

    @BeforeEach
    void seedRegistry() {
        UIRegistry.setUIEntryRenderers(List.of(renderer("known_renderer")));
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
        @DisplayName("returns null and appends an error naming an unknown renderer")
        void reportsAnUnknownName() {
            List<String> errors = new ArrayList<>();
            assertNull(UIRegistry.getUIEntryRenderer("no_such_renderer", errors));
            assertEquals(1, errors.size());
            assertTrue(errors.get(0).contains("no_such_renderer"));
        }

        @Test
        @DisplayName("returns the first of several renderers that match the name")
        void firstMatchWins() {
            UIEntryRenderer first = renderer("dup");
            UIEntryRenderer second = renderer("dup");
            UIRegistry.setUIEntryRenderers(List.of(renderer("other"), first, second));
            assertSame(first, UIRegistry.getUIEntryRenderer("dup", new ArrayList<>()));
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

        @Test
        @DisplayName("finds a TEMPLATE_ONLY base record and a plain record through the same lookup")
        void oneLookupFindsBothKinds() {
            UIEntry base = templateOnlyEntry("a_base");
            UIEntry plain = entry("a_plain");
            UIRegistry.setUIEntries(List.of(base, plain));

            assertSame(base, UIRegistry.getUIEntry("a_base"));
            assertTrue(UIRegistry.getUIEntry("a_base").getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
            assertSame(plain, UIRegistry.getUIEntry("a_plain"));
            assertFalse(UIRegistry.getUIEntry("a_plain").getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        }
    }

    /**
     * Tests the lookups' edge cases: the unloaded-registry states and the exact-match rule, against
     * C's {@code ui_entry_search} and {@code ui_entry_renderer_lookup}, which compare with
     * {@code strcmp} and {@code streq} (case-sensitive, whole string).
     *
     * <p>Class LookupEdges coded on 260929, commented in full on 261102.
     */
    @Nested
    @DisplayName("lookup edge cases")
    class LookupEdges {

        @Test
        @DisplayName("names are matched case-sensitively, as strcmp and streq do")
        void matchIsCaseSensitive() {
            assertNull(UIRegistry.getUIEntry("KNOWN_ENTRY"));
            assertNull(UIRegistry.getUIEntryRenderer("KNOWN_RENDERER", new ArrayList<>()));
        }

        @Test
        @DisplayName("names are matched whole, not by prefix")
        void matchIsWholeString() {
            assertNull(UIRegistry.getUIEntry("known"));
            assertNull(UIRegistry.getUIEntry("known_entry2"));
            assertNull(UIRegistry.getUIEntry(""));
            assertNull(UIRegistry.getUIEntryRenderer("known", new ArrayList<>()));
        }

        @Test
        @DisplayName("a parameterised name with its <TAG> suffix is one whole name")
        void taggedNamesAreWholeNames() {
            UIRegistry.setUIEntries(List.of(entry("resist_ui_compact_0"), entry("resist_ui_compact_0<ACID>")));
            assertEquals("resist_ui_compact_0<ACID>", UIRegistry.getUIEntry("resist_ui_compact_0<ACID>").getName());
            assertNull(UIRegistry.getUIEntry("resist_ui_compact_0<ELEC>"));
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
        @DisplayName("an entry list that is not in name order is still searched in full")
        void unsortedListIsStillFound() {
            // C keeps entries[] sorted and binary-searches it; the Java list is in load order.
            UIRegistry.setUIEntries(List.of(entry("m"), entry("z"), entry("a"), entry("q")));
            assertEquals("a", UIRegistry.getUIEntry("a").getName());
            assertEquals("z", UIRegistry.getUIEntry("z").getName());
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
        @DisplayName("an unloaded renderer registry reports through errors and returns null")
        void unloadedRenderersReportThroughErrors() {
            UIRegistry.setUIEntryRenderers(null);
            List<String> errors = new ArrayList<>();
            assertNull(UIRegistry.getUIEntryRenderer("x", errors));
            assertEquals(1, errors.size());
        }

        @Test
        @DisplayName("the list getters throw NullPointerException when unloaded, as documented")
        void listGettersThrowWhenUnloaded() {
            UIRegistry.setUIEntries(null);
            UIRegistry.setUIEntryRenderers(null);
            assertThrows(NullPointerException.class, UIRegistry::getUIEntries);
            assertThrows(NullPointerException.class, UIRegistry::getUIEntryRenderers);
        }

        @Test
        @DisplayName("the list getters return read-only views")
        void listGettersAreReadOnly() {
            assertThrows(UnsupportedOperationException.class, () -> UIRegistry.getUIEntries().add(entry("x")));
            assertThrows(UnsupportedOperationException.class,
                    () -> UIRegistry.getUIEntryRenderers().add(renderer("x")));
            assertEquals(1, UIRegistry.getUIEntries().size());
            assertEquals(1, UIRegistry.getUIEntryRenderers().size());
        }
    }

    /**
     * Tests the lookups against the real shipped data files, with the expected names and flags taken
     * from {@code ui_entry_base.txt} and {@code ui_entry.txt} in the C tree: a {@code template:}
     * names a base record, and C's {@code run_parse_ui_entry} flags every record of the base file
     * {@code ENTRY_FLAG_TEMPLATE_ONLY} while {@code parse_entry_template} masks it off the copy.
     *
     * <p>Class RealData coded on 261102, commented in full on 261102.
     */
    @Nested
    @DisplayName("shipped data")
    class RealData {
        private List<UIEntry> savedEntries;
        private List<UIEntryRenderer> savedRenderers;

        @BeforeEach
        void loadShippedFiles() throws Exception {
            savedEntries = safely(UIRegistry::getUIEntries);
            savedRenderers = safely(UIRegistry::getUIEntryRenderers);

            UIRegistry.setUIEntryRenderers(
                    new UIEntryRendererReader().parseWithResults("lib/gamedata/ui_entry_renderer.txt").items());
            // Register the base entries first; UIEntryReader then seeds from them, as the game's
            // load order does.
            UIRegistry.setUIEntries(
                    new UIEntryBaseReader().parseWithResults("lib/gamedata/ui_entry_base.txt").items());
            UIRegistry.setUIEntries(new UIEntryReader().parseWithResults("lib/gamedata/ui_entry.txt").items());
        }

        @AfterEach
        void restore() {
            UIRegistry.setUIEntryRenderers(savedRenderers);
            UIRegistry.setUIEntries(savedEntries);
        }

        private <T> List<T> safely(java.util.function.Supplier<List<T>> getter) {
            try {
                return getter.get();
            } catch (NullPointerException unloaded) {
                return null;
            }
        }

        @Test
        @DisplayName("a renderer named in ui_entry_renderer.txt is found")
        void realRendererIsFound() {
            List<String> errors = new ArrayList<>();
            UIEntryRenderer renderer = UIRegistry.getUIEntryRenderer("char_screen1_flag_renderer", errors);
            assertEquals("char_screen1_flag_renderer", renderer.getName());
            assertTrue(errors.isEmpty());
        }

        @Test
        @DisplayName("a ui_entry.txt entry is found and is not TEMPLATE_ONLY")
        void realEntryIsFoundAndDisplayable() {
            UIEntry entry = UIRegistry.getUIEntry("pfear_ui_compact_0");
            assertNotNull(entry);
            assertFalse(entry.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        }

        @Test
        @DisplayName("a ui_entry_base.txt record survives the entry load and stays TEMPLATE_ONLY")
        void realBaseIsFoundThroughTheSameLookup() {
            UIEntry base = UIRegistry.getUIEntry("good_flag_ui_compact_0");
            assertNotNull(base);
            assertTrue(base.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        }

        @Test
        @DisplayName("an explicitly tagged ui_entry.txt name is found whole")
        void realTaggedEntryIsFound() {
            assertNotNull(UIRegistry.getUIEntry("resist_ui_compact_0<ACID>"));
            assertNull(UIRegistry.getUIEntry("resist_ui_compact_0<NO_SUCH_ELEMENT>"));
        }
    }

    /**
     * Tests the size constants against the {@code #define} values in C: {@code MAX_ENTRY_LABEL} and
     * {@code MAX_SHORTENED} in {@code ui-entry.c}, {@code MAX_PALETTE} in {@code ui-entry-renderers.c}.
     *
     * <p>Class Constants coded on 260929, commented in full on 261102.
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
     * values of C's {@code stat_names}/{@code stat_names_reduced} in {@code ui-display.c}, not
     * against whatever the Java literals currently read.
     *
     * <p>Class StatNames coded on 260925, commented in full on 261102.
     */
    @Nested
    @DisplayName("statNames / statReducedNames")
    class StatNames {

        /**
         * {@code stat_names[STAT_MAX]} in {@code ui-display.c}.
         */
        private static final String[] C_STAT_NAMES =
                {"STR: ", "INT: ", "WIS: ", "DEX: ", "CON: "};

        /**
         * {@code stat_names_reduced[STAT_MAX]} in {@code ui-display.c}.
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
