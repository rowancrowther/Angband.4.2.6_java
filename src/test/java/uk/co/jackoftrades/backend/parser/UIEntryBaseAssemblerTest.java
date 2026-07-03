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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseAssembler;
import uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryBaseAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link UIEntryBaseParseRecord}s directly (no grammar/reader), so they exercise
 * the assembler in isolation: {@code renderer} resolution against the {@link GameConstants} renderer
 * registry, {@code combine} enum resolution, and skip-and-continue (a record whose renderer or
 * combiner fails to resolve is dropped with an error, the rest survive).
 *
 * <p>The assembler reaches into {@code GameConstants.getUIEntryRenderer}, which needs the renderer
 * registry populated. There is no public setter (the game loads it in {@code GameConstants.init()}),
 * so {@link #seedRenderers()} loads the real renderer file and injects it into the private static
 * field via reflection — keeping the test self-contained and independent of full-game init order.
 *
 * @author ClaudeCode
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
        Field field = GameConstants.class.getDeclaredField("uiEntryRenderers");
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
        // UIEntryBase exposes only getName(); the remaining fields are asserted through toString().
        String s = u.toString();
        assertTrue(s.contains("combine=LOGICAL_OR"), s);
        assertTrue(s.contains("categories=[CHAR_SCREEN1, abilities]"), s);
        assertTrue(s.contains("flags='TIMED_AS_AUX'"), s);
        assertTrue(s.contains("desc='some desc'"), s);
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
                        rec("good", KNOWN_RENDERER, "LOGICAL_OR", "F", "d", List.of("C"))),
                errors);

        assertEquals(1, out.size());
        assertEquals("good", out.get(0).getName());
        assertFalse(errors.isEmpty());
    }
}
