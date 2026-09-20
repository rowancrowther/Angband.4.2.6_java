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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseAssembler;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseParseRecord;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryBaseAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link UIEntryBaseParseRecord}s directly (no grammar/reader), so they exercise
 * the assembler in isolation: {@code renderer} resolution against the {@link UIRegistry} renderer
 * registry, {@code combine} enum resolution, and skip-and-continue (a record whose renderer or
 * combiner fails to resolve is dropped with an error, the rest survive).
 *
 * <p>The assembler reaches into {@code UIRegistry.getUIEntryRenderer}, which needs the renderer
 * registry populated. There is no public setter (the game loads it in {@code GameConstants.init()}),
 * so {@link #seedRenderers()} loads the real renderer file and injects it into the private static
 * field via reflection — keeping the test self-contained and independent of full-game init order.
 *
 * @author Rowan Crowther
 */
class UIEntryBaseAssemblerTest {

    /**
     * A renderer name that exists in the real ui_entry_renderer.txt.
     */
    private static final String KNOWN_RENDERER = "char_screen1_flag_renderer";

    @BeforeAll
    static void seedRenderers() throws Exception {
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        Field field = UIRegistry.class.getDeclaredField("uiEntryRenderers");
        field.setAccessible(true);
        field.set(null, renderers);
    }

    private static UIEntryBaseParseRecord rec(String name, String renderer, String combine,
                                              String flags, String desc, List<String> categories) {
        return new UIEntryBaseParseRecord(name, renderer, combine, flags, desc, categories, 1);
    }

    @Test
    void resolvesRendererAndCombineAndCarriesRawFieldsThrough() {
        List<String> errors = new ArrayList<>();
        List<UIEntryBase> out = new UIEntryBaseAssembler().assemble(
                List.of(rec("t", KNOWN_RENDERER, "LOGICAL_OR", "TIMED_AS_AUX", "some desc",
                        List.of("CHAR_SCREEN1", "abilities"))),
                errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntryBase u = out.get(0);
        assertEquals("t", u.getName());
        assertTrue(u.getFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        // UIEntryBase does not expose desc (C's parse_entry_desc never stores it either);
        // the remaining fields are asserted through toString().
        String s = u.toString();
        assertTrue(s.contains("combine=LOGICAL_OR"), s);
        assertTrue(s.contains("categories=[CHAR_SCREEN1, abilities]"), s);
        assertTrue(s.contains(KNOWN_RENDERER), s);
    }

    @Test
    void unknownRendererIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntryBase> out = new UIEntryBaseAssembler().assemble(
                List.of(rec("t", "no_such_renderer", "LOGICAL_OR", "F", "d", List.of("C"))), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("no_such_renderer")), errors::toString);
    }

    @Test
    void invalidCombineIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntryBase> out = new UIEntryBaseAssembler().assemble(
                List.of(rec("t", KNOWN_RENDERER, "BOGUS", "F", "d", List.of("C"))), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("Illegal combine name")), errors::toString);
    }

    @Test
    void partialResultsSurviveABadRecord() {
        List<String> errors = new ArrayList<>();
        List<UIEntryBase> out = new UIEntryBaseAssembler().assemble(
                List.of(rec("bad", "no_such_renderer", "LOGICAL_OR", "F", "d", List.of("C")),
                        rec("good", KNOWN_RENDERER, "LOGICAL_OR", "TIMED_AS_AUX", "d", List.of("C"))),
                errors);

        assertEquals(1, out.size());
        assertEquals("good", out.get(0).getName());
        assertFalse(errors.isEmpty());
    }

    // ---- the shared UIEntry registry parseEachEntry builds alongside the returned UIEntryBase ------
    // ---- list, mirroring hatch_embryo's no-parameterisation branch (ui-entry.c:1826-1859) and -------
    // ---- run_parse_ui_entry's post-base-file TEMPLATE_ONLY pass (ui-entry.c:2273-2278) --------------

    @Test
    void createPathInsertsATemplateOnlyEntryWithUnsetCategoryPriorities() {
        List<String> errors = new ArrayList<>();
        new UIEntryBaseAssembler().assemble(
                List.of(rec("good_flag_ui_compact_0", KNOWN_RENDERER, "LOGICAL_OR", "TIMED_AS_AUX",
                        "desc", List.of("CHAR_SCREEN1", "abilities"))),
                errors);

        UIEntry entry = UIRegistry.getUIEntry("good_flag_ui_compact_0");
        assertNotNull(entry, "the base record should have produced a shared UIEntry");
        assertTrue(entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY),
                "every base-file entry is template-only, matching C's post-base-file OR pass");
        assertTrue(entry.entryFlagHas(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));

        List<UIEntryCategory> categories = entry.getCategories();
        assertEquals(2, categories.size());
        for (UIEntryCategory category : categories) {
            // No priority: directive in ui_entry_base.txt, so every category is inserted
            // with priority_set=false, matching insert_embryo_category's call from
            // parse_entry_category (ui-entry.c:2114-2130) with a fresh embryo's
            // psource_index=0 and default_priority=0.
            assertFalse(category.isPrioritySet(), category.getName() + " should be unset");
            assertEquals(0, category.getPriority());
        }
    }

    @Test
    void aRepeatedNameMergesNewCategoriesIntoTheExistingEntryRatherThanDuplicatingIt() {
        List<String> errors = new ArrayList<>();
        List<UIEntryBase> out = new UIEntryBaseAssembler().assemble(
                List.of(rec("shared_name", KNOWN_RENDERER, "LOGICAL_OR", "TIMED_AS_AUX", "d1",
                                List.of("CHAR_SCREEN1", "abilities")),
                        rec("shared_name", KNOWN_RENDERER, "ADD", "TIMED_AS_AUX", "d2",
                                List.of("abilities", "EQUIPCMP_SCREEN"))),
                errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(2, out.size(), "both raw UIEntryBase records still come back from assemble()");

        // The override path (hatch_embryo's embryo->exists branch, ui-entry.c:1758-1759) means
        // only one shared UIEntry should exist for the repeated name, not two.
        long matches = UIRegistry.getUIEntries().stream()
                .filter(e -> e.getName().equals("shared_name")).count();
        assertEquals(1, matches, "a repeated name should edit the existing entry, not duplicate it");

        UIEntry entry = UIRegistry.getUIEntry("shared_name");
        // Renderer/combiner/flags are overwritten outright by the second record, mirroring
        // parse_entry_renderer/parse_entry_combine's unconditional assignment regardless of
        // embryo->exists (ui-entry.c:2015-2042).
        assertEquals(uk.co.jackoftradesltd.channel.utils.combiners.CombinerName.ADD, entry.getCombineType());

        // Categories not already present are added; ones already there are not duplicated -
        // matching insert_embryo_category's exists-branch (ui-entry.c:1493-1513).
        List<String> categoryNames = entry.getCategories().stream().map(UIEntryCategory::getName).toList();
        assertEquals(3, categoryNames.size(), categoryNames::toString);
        assertTrue(categoryNames.containsAll(List.of("CHAR_SCREEN1", "abilities", "EQUIPCMP_SCREEN")));
    }
}
