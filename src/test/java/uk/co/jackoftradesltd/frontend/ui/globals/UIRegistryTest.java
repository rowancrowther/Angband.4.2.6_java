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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
}
