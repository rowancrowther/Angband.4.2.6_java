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
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryAssembler;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryParseRecord;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

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
 *       {@link StatElemType};</li>
 *   <li>the name {@code <TAG>} ({@code nameTag}) drives the concrete {@link ElementEnum};</li>
 * </ul>
 * plus the registry/enum look-ups (renderer, combine, template) and the suite-wide
 * skip-and-continue policy (a record whose present field fails to resolve is dropped with an
 * error while the rest survive).
 *
 * <p>The assembler reaches into {@code UIRegistry.getUIEntryRenderer}/{@code getUIEntryBase},
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

        // UIEntryBaseReader runs the real UIEntryBaseAssembler as part of parsing, and that
        // assembler's own job is to also populate UIRegistry's shared UIEntry list with one
        // TEMPLATE_ONLY placeholder per base (UIEntryBaseAssembler.java:136) - a side effect this
        // class does not want, since every test below assumes UIEntryAssembler.assemble() starts
        // from an empty registry (UIEntryAssembler.java:108). Reset it so that seeding the bases
        // does not leak placeholder entries into every test's result count.
        UIRegistry.setUIEntries(List.of());
    }

    private static void setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    /**
     * Build a record varying only the fields under test; the rest default to empty.
     */
    private static UIEntryParseRecord rec(String name, String parameter, String nameTag,
                                          String renderer, String combine, String template) {
        return new UIEntryParseRecord(name, template, "", "", "", List.of(), parameter, renderer,
                combine, "", List.of(), List.of(), "", nameTag, 1);
    }

    // ---- the parameter/nameTag split ---------------------------------------------------------

    @Test
    void statOrElementResolvesFromTheParameterKind() {
        List<String> errors = new ArrayList<>();
        // A created entry with neither combine: nor template: is dropped by the create path's
        // combiner_index==0 guard (UIEntryAssembler.java:583-586, mirroring hatch_embryo -
        // [C] ui-entry.c:1773-1779), so every record here needs a real combiner to survive.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("s", "stat", "", "", "ADD", ""),
                rec("e", "element", "", "", "ADD", ""),
                rec("n", "", "", "", "ADD", "")), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        // The parameter:stat record expands into one tagged entry per stat (STR..CON) and the
        // parameter:element record expands into one tagged entry per real element (ACID..ARROW,
        // skipping the Java-only ELEM_NONE/ELEM_MAX placeholders), so the three input records
        // yield 5 + 25 + 1 = 31 entries: the five stats first, then the 25 elements, then none.
        assertEquals(31, out.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(StatElemType.STAT, out.get(i).getStatOrElement());
            // Regression (260922): the UIEntry constructor has no stat-index slot, so each entry
            // must have its index stamped on afterward - without that, every entry here would
            // silently read back 0 (STR's index) regardless of which stat it actually is.
            assertEquals(i, out.get(i).getStatParameter());
        }
        assertEquals("s<STR>", out.get(0).getName());
        assertEquals("s<CON>", out.get(4).getName());
        for (int i = 5; i < 30; i++) {
            assertEquals(StatElemType.ELEMENT, out.get(i).getStatOrElement());
        }
        assertEquals("e<ACID>", out.get(5).getName());
        assertEquals(ElementEnum.ELEM_ACID, out.get(5).getParameter());
        assertEquals("e<ARROW>", out.get(29).getName());
        assertEquals(ElementEnum.ELEM_ARROW, out.get(29).getParameter());
        assertEquals(StatElemType.NONE, out.get(30).getStatOrElement());
    }

    @Test
    void nameTagResolvesToTheConcreteElementParameter() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("resist_ui_compact_0<DARK>", "", "DARK", "", "ADD", "")), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntry u = out.get(0);
        // The tag drives the element parameter; the (specialization) record has no parameter: line,
        // so its kind is NONE, and the name keeps the full tagged form.
        assertEquals(ElementEnum.ELEM_DARK, u.getParameter());
        assertEquals(StatElemType.NONE, u.getStatOrElement());
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
                rec("good", "element", "", "", "ADD", "")), errors);

        // "good" is a parameter:element record, so it survives as its full 25-element expansion,
        // not a single entry - the bad record is what gets dropped.
        assertEquals(25, out.size());
        assertEquals("good<ACID>", out.get(0).getName());
        assertTrue(out.stream().allMatch(e -> e.getName().startsWith("good<")));
        assertFalse(errors.isEmpty());
    }

    // ---- override-path priority-scheme dispatch (regression, 260922 finding #3) --------------
    //
    // parseEachEntry's override branch must resolve an "index"/"negative_index" priority: line
    // against the EXISTING (already-parameterized) entry's own element/stat index - C reads
    // embryo->entry->param_index, and embryo->entry IS the existing entry in the exists branch
    // ([C] ui-entry.c:1924, 2180-2182). A record that overrides an already-registered entry never
    // carries its own parameter: line, so dispatching on the incoming record's own type (rather
    // than the existing entry's) silently leaves the index at 0 instead of resolving it.

    @Test
    void overridePriorityIndexReadsTheExistingEntrysElementIndex() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("e2", "element", "", "", "ADD", ""),
                new UIEntryParseRecord("e2<ELEC>", "", "", "", "", List.of(), "", "", "",
                        "index", List.of(), List.of(), "", "", 1)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        UIEntry elec = out.stream().filter(e -> e.getName().equals("e2<ELEC>"))
                .findFirst().orElseThrow();
        // ELEC is C's element_names[] index 1 (ACID=0, ELEC=1); get_priority_from_index(1) == 1.
        assertEquals(1, elec.getDefaultPriority());
    }

    @Test
    void overridePriorityNegativeIndexReadsTheExistingEntrysElementIndex() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("e3", "element", "", "", "ADD", ""),
                new UIEntryParseRecord("e3<FIRE>", "", "", "", "", List.of(), "", "", "",
                        "negative_index", List.of(), List.of(), "", "", 1)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        UIEntry fire = out.stream().filter(e -> e.getName().equals("e3<FIRE>"))
                .findFirst().orElseThrow();
        // FIRE is C's element_names[] index 2; get_priority_from_negative_index(2) == -2.
        assertEquals(-2, fire.getDefaultPriority());
    }

    @Test
    void overridePriorityIndexWithACategoryAttachesToTheCategoryNotTheEntry() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("e4", "element", "", "", "ADD", ""),
                new UIEntryParseRecord("e4<FIRE>", "", "", "", "", List.of("mycat"), "", "", "",
                        "index", List.of(), List.of(), "", "", 1)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        UIEntry fire = out.stream().filter(e -> e.getName().equals("e4<FIRE>"))
                .findFirst().orElseThrow();
        // The priority: line attaches to the category just declared before it, not the entry's own
        // default priority, matching parse_entry_priority's last_category_index branch
        // ([C] ui-entry.c:2190-2202).
        assertEquals(0, fire.getDefaultPriority());
        UIEntryCategory myCat = fire.getCategories().stream()
                .filter(c -> c.getName().equals("mycat")).findFirst().orElseThrow();
        assertTrue(myCat.isPrioritySet());
        assertEquals(2, myCat.getPriority());
    }

    @Test
    void overridePriorityIndexReadsTheExistingEntrysStatIndex() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("s2", "stat", "", "", "ADD", ""),
                new UIEntryParseRecord("s2<DEX>", "", "", "", "", List.of(), "", "", "",
                        "index", List.of(), List.of(), "", "", 1)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        UIEntry dex = out.stream().filter(e -> e.getName().equals("s2<DEX>"))
                .findFirst().orElseThrow();
        // Stats (STR,INT,WIS,DEX,CON) carry no NONE-style placeholder to offset for, so DEX's
        // index is its plain position, 3; get_priority_from_index(3) == 3.
        assertEquals(3, dex.getDefaultPriority());
    }
}
