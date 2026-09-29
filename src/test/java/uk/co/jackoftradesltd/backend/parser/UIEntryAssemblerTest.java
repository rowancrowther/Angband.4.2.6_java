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
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
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

    /**
     * Build a record with every field under test spelled out.
     */
    private static UIEntryParseRecord full(String name, String template, String label, String label5,
                                           List<String> before, String parameter, String combine,
                                           String priority, List<String> after, List<String> flags) {
        return new UIEntryParseRecord(name, template, label, label5, "", before, parameter, "", combine,
                priority, after, flags, "", "", 1);
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
    // (ui-entry.c). A record that overrides an already-registered entry never
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
    void statOrElementResolvesFromTheParameterKind() {
        List<String> errors = new ArrayList<>();
        // A created entry with neither combine: nor template: is dropped by the create path's
        // combiner_index==0 guard (UIEntryAssembler.java:583-586, mirroring hatch_embryo -
        // ui-entry.c), so every record here needs a real combiner to survive.
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

    // ---- create path (260929 port pass) -----------------------------------------------------
    //
    // Expected values come from ui-entry.c: parse_entry_flags ORs a record's flags into the
    // embryo's entry; hatch_embryo rejects a new entry with no combiner and parameterises names
    // over element_names[] (ACID=0 .. ARROW=24) / stat_names[] (STR..CON), taking the label from
    // the parameter name when the record has none and the default priority from the scheme.

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
        // (ui-entry.c).
        assertEquals(0, fire.getDefaultPriority());
        UIEntryCategory myCat = fire.getCategories().stream()
                .filter(c -> c.getName().equals("mycat")).findFirst().orElseThrow();
        assertTrue(myCat.isPrioritySet());
        assertEquals(2, myCat.getPriority());
    }

    @Test
    void createPathKeepsTheRecordsOwnFlagsOnEveryElementEntry() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                full("f1", "", "", "", List.of(), "element", "ADD", "", List.of(),
                        List.of("TIMED_AS_AUX"))), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(25, out.size());
        // parse_entry_flags sets the flag on the embryo, and hatch_embryo copies flags to each
        // parameterised entry, so all 25 carry it (this is resist_ui_compact_0's shape).
        assertTrue(out.stream().allMatch(e -> e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX)));
    }

    @Test
    void createPathWithNoFlagsLeavesTheFlagSetEmpty() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                full("f2", "", "", "", List.of(), "", "ADD", "", List.of(), List.of())), errors);

        assertEquals(1, out.size());
        assertTrue(out.get(0).getEntryFlag().isEmpty());
    }

    @Test
    void newEntryWithNoCombinerIsRejectedAndNotAdded() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                full("nc", "", "", "", List.of(), "", "", "", List.of(), List.of())), errors);

        // hatch_embryo: combiner_index == 0 frees the entry and returns 1 - nothing is inserted.
        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("no combiner")), errors::toString);
    }

    @Test
    void elementLabelDefaultsToTheElementNameUnlessTheRecordGivesOne() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> unlabelled = new UIEntryAssembler().assemble(List.of(
                full("l1", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of())), errors);
        List<UIEntry> labelled = new UIEntryAssembler().assemble(List.of(
                full("l2", "", "Res", "", List.of(), "element", "ADD", "", List.of(), List.of())), errors);

        assertEquals("ACID", unlabelled.get(0).getLabel());
        assertEquals("ARROW", unlabelled.get(24).getLabel());
        assertEquals("Res", labelled.get(0).getLabel());
        assertEquals("Res", labelled.get(24).getLabel());
    }

    @Test
    void statLabelDefaultsToTheStatName() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("l3", "", "", "", List.of(), "stat", "ADD", "", List.of(), List.of())),
                new ArrayList<>());

        assertEquals("STR", out.get(0).getLabel());
        assertEquals("CON", out.get(4).getLabel());
    }

    @Test
    void shortenedLabelIsCopiedToEveryParameterisedEntry() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("sh", "", "Poison", "Pois", List.of(), "element", "ADD", "", List.of(), List.of())),
                new ArrayList<>());

        // copy_shortened_labels in hatch_embryo copies label5 to each entry.
        assertTrue(out.stream().allMatch(e -> "Pois".equals(e.getLabel5())));
    }

    @Test
    void indexSchemeGivesEachElementEntryItsIndexAsDefaultPriority() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p1", "", "", "", List.of(), "element", "ADD", "index", List.of(), List.of())),
                new ArrayList<>());

        // get_priority_from_index(i) == i over element_names[]: ACID 0, CHAOS 11, ARROW 24.
        assertEquals(0, out.get(0).getDefaultPriority());
        assertEquals(11, out.stream().filter(e -> e.getName().equals("p1<CHAOS>"))
                .findFirst().orElseThrow().getDefaultPriority());
        assertEquals(24, out.get(24).getDefaultPriority());
    }

    @Test
    void negativeIndexSchemeGivesEachStatEntryTheNegatedIndex() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p2", "", "", "", List.of(), "stat", "ADD", "negative_index", List.of(), List.of())),
                new ArrayList<>());

        assertEquals(0, out.get(0).getDefaultPriority());
        assertEquals(-4, out.get(4).getDefaultPriority());
    }

    @Test
    void numericPriorityIsTheDefaultPriorityOfAnEntryWithNoCategories() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p3", "", "", "", List.of(), "", "ADD", "7", List.of(), List.of())),
                new ArrayList<>());

        assertEquals(7, out.get(0).getDefaultPriority());
    }

    @Test
    void categoryBeforeAPriorityLineTakesTheParameterisedPriority() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                full("p4", "", "", "", List.of("c1"), "element", "ADD", "negative_index", List.of(),
                        List.of())), new ArrayList<>());

        UIEntry fire = out.stream().filter(e -> e.getName().equals("p4<FIRE>")).findFirst().orElseThrow();
        UIEntryCategory c1 = fire.getCategories().get(0);
        // parse_entry_priority attaches the scheme to last_category_index; parameterize_category_list
        // then evaluates it for FIRE (index 2) and sets priority_set.
        assertEquals("c1", c1.getName());
        assertEquals(-2, c1.getPriority());
        assertTrue(c1.isPrioritySet());
    }

    @Test
    void categoryAfterAPriorityLineIsLeftForTheFinishingPass() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p5", "", "", "", List.of(), "", "ADD", "3", List.of("c2"), List.of())),
                new ArrayList<>());

        // priority: with no category yet sets the default; the later category is priority_set false.
        assertEquals(3, out.get(0).getDefaultPriority());
        assertFalse(out.get(0).getCategories().get(0).isPrioritySet());
    }

    @Test
    void templateSuppliesCombinerFlagsAndCategoriesAndDropsTemplateOnly() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("t1", "good_flag_ui_compact_0", "", "", List.of(), "", "", "", List.of(), List.of())),
                errors);

        // parse_entry_template copies renderer, combiner, flags (minus TEMPLATE_ONLY) and categories,
        // so a record with no combine: of its own still survives hatch_embryo's combiner check.
        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntry e = out.get(0);
        assertEquals(CombinerName.LOGICAL_OR, e.getCombineType());
        assertTrue(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertFalse(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
        List<String> cats = e.getCategories().stream().map(UIEntryCategory::getName).toList();
        assertTrue(cats.containsAll(List.of("CHAR_SCREEN1", "EQUIPCMP_SCREEN", "abilities")), cats::toString);
    }

    // ---- override path ----------------------------------------------------------------------

    @Test
    void overrideOrsFlagsIntoTheExistingEntryAndKeepsThemWhenNoneAreGiven() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("o1", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of()),
                        full("o1<ACID>", "", "", "", List.of(), "", "", "", List.of(), List.of("TIMED_AS_AUX")),
                        full("o1<ELEC>", "", "", "", List.of(), "", "", "", List.of(), List.of())),
                new ArrayList<>());

        UIEntry acid = out.stream().filter(e -> e.getName().equals("o1<ACID>")).findFirst().orElseThrow();
        UIEntry elec = out.stream().filter(e -> e.getName().equals("o1<ELEC>")).findFirst().orElseThrow();
        assertTrue(acid.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertTrue(elec.getEntryFlag().isEmpty());
    }

    @Test
    void overrideReplacesTheLabelAndCombinerWhenGiven() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("o2", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of()),
                        full("o2<FIRE>", "", "Fire", "", List.of(), "", "LOGICAL_OR", "", List.of(), List.of())),
                new ArrayList<>());

        UIEntry fire = out.stream().filter(e -> e.getName().equals("o2<FIRE>")).findFirst().orElseThrow();
        UIEntry cold = out.stream().filter(e -> e.getName().equals("o2<COLD>")).findFirst().orElseThrow();
        assertEquals("Fire", fire.getLabel());
        assertEquals(CombinerName.LOGICAL_OR, fire.getCombineType());
        // The neighbouring entry is untouched.
        assertEquals("COLD", cold.getLabel());
        assertEquals(CombinerName.ADD, cold.getCombineType());
    }

    @Test
    void overrideAddsANewCategoryOnceAndNotAgainWhenAlreadyPresent() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("o3", "", "", "", List.of("a"), "element", "ADD", "", List.of(), List.of()),
                        full("o3<ACID>", "", "", "", List.of("a", "b"), "", "", "", List.of(), List.of())),
                new ArrayList<>());

        UIEntry acid = out.stream().filter(e -> e.getName().equals("o3<ACID>")).findFirst().orElseThrow();
        List<String> cats = acid.getCategories().stream().map(UIEntryCategory::getName).toList();
        // search_embryo_categories finds "a" already present, so only "b" is inserted.
        assertEquals(2, cats.size(), cats::toString);
        assertTrue(cats.containsAll(List.of("a", "b")));
    }

    @Test
    void parameterOnAnAlreadyExistingNameIsRejected() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                full("o4", "", "", "", List.of(), "", "ADD", "", List.of(), List.of()),
                full("o4", "", "", "", List.of(), "element", "", "", List.of(), List.of())), errors);

        // parse_entry_parameter returns PARSE_ERROR_INVALID_OPTION when embryo->exists.
        assertEquals(1, out.size());
        assertTrue(errors.stream().anyMatch(e -> e.contains("already existing entry name")), errors::toString);
    }
}
