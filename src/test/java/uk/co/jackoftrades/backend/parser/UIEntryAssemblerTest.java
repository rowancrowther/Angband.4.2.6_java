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
import uk.co.jackoftrades.backend.parser.uientry.UIEntryAssembler;
import uk.co.jackoftrades.backend.parser.uientry.UIEntryParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link UIEntryParseRecord}s directly (no grammar/reader), exercising the
 * assembler in isolation. They pin the two resolutions that motivated the {@code parameter}/
 * {@code nameTag} split:
 * <ul>
 *   <li>the {@code parameter:} directive ({@code stat}/{@code element}/absent) drives
 *       {@link UIEntry.StatElemType};</li>
 *   <li>the name {@code <TAG>} ({@code nameTag}) drives the concrete {@link ElementEnum};</li>
 * </ul>
 * plus the registry/enum look-ups (renderer, combine, template) and the suite-wide
 * skip-and-continue policy (a record whose present field fails to resolve is dropped with an
 * error while the rest survive).
 *
 * <p>The assembler reaches into {@code GameConstants.getUIEntryRenderer}/{@code getUIEntryBase},
 * which need their registries populated. There is no public setter (the game loads them in
 * {@code GameConstants.init()}), so {@link #seedRegistries()} loads the real renderer and base
 * files and injects them into the private static fields via reflection - keeping the test
 * self-contained and independent of full-game init order.
 *
 * @author Rowan Crowther
 */
class UIEntryAssemblerTest {

    /**
     * A renderer name that exists in the real ui_entry_renderer.txt.
     */
    private static final String KNOWN_RENDERER = "char_screen1_flag_renderer";

    @BeforeAll
    static void seedRegistries() throws Exception {
        // Renderers first: the base reader's assembler resolves renderers, so it needs them seeded.
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        setStatic("uiEntryRenderers", renderers);
        List<UIEntryBase> bases = new UIEntryBaseReader()
                .parseWithResults("lib/gamedata/ui_entry_base.txt").items();
        setStatic("uiEntryBases", bases);
    }

    private static void setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    /**
     * Build a record varying only the fields under test; the rest default to empty.
     */
    private static UIEntryParseRecord rec(String name, String parameter, String nameTag,
                                          String renderer, String combine, String template) {
        return new UIEntryParseRecord(name, template, "", "", "", parameter, renderer,
                combine, "", List.of(), List.of(), "", nameTag, 1);
    }

    // ---- the parameter/nameTag split ---------------------------------------------------------

    @Test
    void statOrElementResolvesFromTheParameterKind() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("s", "stat", "", "", "", ""),
                rec("e", "element", "", "", "", ""),
                rec("n", "", "", "", "", "")), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(3, out.size());
        assertEquals(UIEntry.StatElemType.STAT, out.get(0).getStatOrElement());
        assertEquals(UIEntry.StatElemType.ELEMENT, out.get(1).getStatOrElement());
        assertEquals(UIEntry.StatElemType.NONE, out.get(2).getStatOrElement());
    }

    @Test
    void nameTagResolvesToTheConcreteElementParameter() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("resist_ui_compact_0<DARK>", "", "DARK", "", "", "")), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntry u = out.get(0);
        // The tag drives the element parameter; the (specialization) record has no parameter: line,
        // so its kind is NONE, and the name keeps the full tagged form.
        assertEquals(ElementEnum.ELEM_DARK, u.getParameter());
        assertEquals(UIEntry.StatElemType.NONE, u.getStatOrElement());
        assertEquals("resist_ui_compact_0<DARK>", u.getName());
    }

    // ---- registry / enum resolution ----------------------------------------------------------

    @Test
    void resolvesAKnownRendererFromTheRegistry() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("x", "", "", KNOWN_RENDERER, "ADD", "")), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        assertTrue(out.get(0).toString().contains(KNOWN_RENDERER), out.get(0)::toString);
    }

    @Test
    void unknownRendererIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("x", "", "", "no_such_renderer", "", "")), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("no_such_renderer")), errors::toString);
    }

    @Test
    void invalidCombineIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("x", "", "", "", "BOGUS", "")), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("illegal combiner enum value")),
                errors::toString);
    }

    @Test
    void unknownTemplateIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("x", "", "", "", "", "no_such_base")), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("illegal template name")), errors::toString);
    }

    @Test
    void illegalParameterKindIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("x", "bogus", "", "", "", "")), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("illegal parameter kind: bogus")),
                errors::toString);
    }

    // ---- skip-and-continue -------------------------------------------------------------------

    @Test
    void partialResultsSurviveABadRecord() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("bad", "bogus", "", "", "", ""),
                rec("good", "element", "", "", "", "")), errors);

        assertEquals(1, out.size());
        assertEquals("good", out.get(0).getName());
        assertFalse(errors.isEmpty());
    }
}
