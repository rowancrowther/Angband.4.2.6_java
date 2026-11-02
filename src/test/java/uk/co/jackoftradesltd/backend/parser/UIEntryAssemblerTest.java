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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryAssembler;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryParseRecord;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link UIEntryParseRecord}s directly (no grammar/reader), exercising the
 * assembler in isolation. They pin the two resolutions that motivated the {@code parameter:} line
 * and the name's {@code <TAG>} being kept apart:
 * <ul>
 *   <li>the {@code parameter:} directive ({@code stat}/{@code element}/absent) drives
 *       {@link StatElemType};</li>
 *   <li>the name's {@code <TAG>} drives the concrete {@link ElementEnum};</li>
 * </ul>
 * plus the registry/enum look-ups (renderer, combine, template) and the fatal-error policy (a
 * record whose present field fails to resolve throws a {@link DataFileParseException} naming the
 * record's line and file, so no partial result is returned).
 *
 * <p>The assembler reaches into {@code UIRegistry.getUIEntryRenderer}, which needs its registry
 * populated. There is no public setter (the game loads it in {@code GameConstants.init()}), so
 * {@link #seedRegistries()} loads the real renderer and base files and injects them into the
 * private static fields via reflection - keeping the test self-contained and independent of
 * full-game init order. A {@code template:} is looked up among the entries assembled so far, so
 * the template tests register the base placeholders (or an earlier record) for themselves.
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
        // does not leak placeholder entries into every test's result count. The placeholders are kept
        // aside in templatePlaceholders: parse_entry_template looks a template up among all the
        // entries parsed so far (UIEntryAssembler.java:317), so the template tests register them
        // again, for their own duration, through withTemplatesRegistered.
        templatePlaceholders = new ArrayList<>(UIRegistry.getUIEntries());
        UIRegistry.setUIEntries(List.of());
    }

    /**
     * The TEMPLATE_ONLY placeholder {@code UIEntryBaseAssembler} makes for each base in
     * {@code ui_entry_base.txt}, captured by {@link #seedRegistries()} before it empties the registry.
     */
    private static List<UIEntry> templatePlaceholders;

    private static void setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    /**
     * Build a record varying only the fields under test; the rest default to empty.
     */
    private static UIEntryParseRecord rec(String name, String parameter,
                                          String renderer, String combine, String template) {
        return new UIEntryParseRecord(name, template, "", "", "", List.of(), parameter, renderer,
                combine, "", List.of(), List.of(), "", 1);
    }

    // ---- the parameter/name-tag split ---------------------------------------------------------

    /**
     * Build a record with every field under test spelled out.
     */
    private static UIEntryParseRecord full(String name, String template, String label, String label5,
                                           List<String> before, String parameter, String combine,
                                           String priority, List<String> after, List<String> flags) {
        return new UIEntryParseRecord(name, template, label, label5, "", before, parameter, "", combine,
                priority, after, flags, "", 1);
    }

    /**
     * The entry called {@code name}; {@code assemble} returns its results sorted by name.
     */
    private static UIEntry byName(List<UIEntry> entries, String name) {
        return entries.stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow();
    }

    @Test
    void aTaggedNameWithNoParameterLineHasNoElementParameter() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("resist_ui_compact_0<DARK>", "", "", "ADD", "")), errors, "test.txt");

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntry u = out.get(0);
        // hatch_embryo leaves param_index at -1 when the record has no parameter: line, so the
        // <DARK> is only part of the name; nothing in C reads it back as an element.
        assertEquals(ElementEnum.ELEM_NONE, u.getParameter());
        assertEquals(StatElemType.NONE, u.getStatOrElement());
        assertEquals("resist_ui_compact_0<DARK>", u.getName());
    }

    @Test
    void resolvesAKnownRendererFromTheRegistry() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("x", "", KNOWN_RENDERER, "ADD", "")), errors, "test.txt");

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        assertTrue(out.get(0).toString().contains(KNOWN_RENDERER), out.get(0)::toString);
    }

    /**
     * Assemble the records, expecting a fatal {@link DataFileParseException}, and return its message.
     */
    private static String fatalMessage(UIEntryParseRecord... records) {
        return assertThrows(DataFileParseException.class,
                () -> new UIEntryAssembler().assemble(List.of(records), new ArrayList<>(), "test.txt"))
                .getMessage();
    }

    @Test
    void unknownRendererIsFatal() {
        String message = fatalMessage(rec("x", "", "no_such_renderer", "", ""));

        assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal renderer value: "
                + "no_such_renderer", message);
    }

    @Test
    void invalidCombineIsFatal() {
        String message = fatalMessage(rec("x", "", "", "BOGUS", ""));

        assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal combiner name value: "
                + "BOGUS", message);
    }

    @Test
    void unknownTemplateIsFatal() {
        String message = fatalMessage(rec("x", "", "", "", "no_such_base"));

        assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal template value: "
                + "no_such_base", message);
    }

    @Test
    void illegalParameterKindIsFatal() {
        String message = fatalMessage(rec("x", "bogus", "", "", ""));

        assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal parameter value: bogus",
                message);
    }

    // ---- fatal errors stop the run -----------------------------------------------------------

    @Test
    void aBadRecordStopsTheRunSoNoPartialResultIsReturned() {
        // The bad record comes first, so the good one is never reached; nothing is returned.
        String message = fatalMessage(
                rec("bad", "bogus", "", "", ""),
                rec("good", "element", "", "ADD", ""));

        assertTrue(message.contains("bogus"), message);
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
                rec("e2", "element", "", "ADD", ""),
                new UIEntryParseRecord("e2<ELEC>", "", "", "", "", List.of(), "", "", "",
                        "index", List.of(), List.of(), "", 1)), errors, "test.txt");

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
                rec("e3", "element", "", "ADD", ""),
                new UIEntryParseRecord("e3<FIRE>", "", "", "", "", List.of(), "", "", "",
                        "negative_index", List.of(), List.of(), "", 1)), errors, "test.txt");

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
                rec("s", "stat", "", "ADD", ""),
                rec("e", "element", "", "ADD", ""),
                rec("n", "", "", "ADD", "")), errors, "test.txt");

        assertTrue(errors.isEmpty(), errors::toString);
        // The parameter:stat record expands into one tagged entry per stat (STR..CON) and the
        // parameter:element record expands into one tagged entry per real element (ACID..ARROW,
        // skipping the Java-only ELEM_NONE/ELEM_MAX placeholders), so the three input records
        // yield 5 + 25 + 1 = 31 entries. The result is sorted by name, as C's entries[] is, so the
        // entries are looked up by name rather than by position.
        assertEquals(31, out.size());
        List<String> statNames = List.of("STR", "INT", "WIS", "DEX", "CON");
        for (int i = 0; i < 5; i++) {
            UIEntry stat = byName(out, "s<" + statNames.get(i) + ">");
            assertEquals(StatElemType.STAT, stat.getStatOrElement());
            // Regression (260922): the UIEntry constructor has no stat-index slot, so each entry
            // must have its index stamped on afterward - without that, every entry here would
            // silently read back 0 (STR's index) regardless of which stat it actually is.
            assertEquals(i, stat.getStatParameter());
        }
        assertEquals(25, out.stream().filter(e -> e.getName().startsWith("e<")).count());
        assertTrue(out.stream().filter(e -> e.getName().startsWith("e<"))
                .allMatch(e -> e.getStatOrElement() == StatElemType.ELEMENT));
        assertEquals(ElementEnum.ELEM_ACID, byName(out, "e<ACID>").getParameter());
        assertEquals(ElementEnum.ELEM_ARROW, byName(out, "e<ARROW>").getParameter());
        assertEquals(StatElemType.NONE, byName(out, "n").getStatOrElement());
    }

    @Test
    void overridePriorityIndexReadsTheExistingEntrysStatIndex() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                rec("s2", "stat", "", "ADD", ""),
                new UIEntryParseRecord("s2<DEX>", "", "", "", "", List.of(), "", "", "",
                        "index", List.of(), List.of(), "", 1)), errors, "test.txt");

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
                rec("e4", "element", "", "ADD", ""),
                new UIEntryParseRecord("e4<FIRE>", "", "", "", "", List.of("mycat"), "", "", "",
                        "index", List.of(), List.of(), "", 1)), errors, "test.txt");

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
                        List.of("TIMED_AS_AUX"))), errors, "test.txt");

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
                full("f2", "", "", "", List.of(), "", "ADD", "", List.of(), List.of())), errors, "test.txt");

        assertEquals(1, out.size());
        assertTrue(out.get(0).getEntryFlag().isEmpty());
    }

    @Test
    void newEntryWithNoCombinerIsFatal() {
        // hatch_embryo: combiner_index == 0 frees the entry and returns 1 - nothing is inserted, and
        // the Java port treats that as fatal.
        String message = fatalMessage(full("nc", "", "", "", List.of(), "", "", "", List.of(), List.of()));

        assertEquals("UI entry starting at line: 1 of file: test.txt has no combiner", message);
    }

    @Test
    void elementLabelDefaultsToTheElementNameUnlessTheRecordGivesOne() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> unlabelled = new UIEntryAssembler().assemble(List.of(
                full("l1", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of())), errors, "test.txt");
        List<UIEntry> labelled = new UIEntryAssembler().assemble(List.of(
                full("l2", "", "Res", "", List.of(), "element", "ADD", "", List.of(), List.of())), errors, "test.txt");

        assertEquals("ACID", byName(unlabelled, "l1<ACID>").getLabel());
        assertEquals("ARROW", byName(unlabelled, "l1<ARROW>").getLabel());
        assertEquals("Res", byName(labelled, "l2<ACID>").getLabel());
        assertEquals("Res", byName(labelled, "l2<ARROW>").getLabel());
    }

    @Test
    void statLabelDefaultsToTheStatName() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("l3", "", "", "", List.of(), "stat", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        assertEquals("STR", byName(out, "l3<STR>").getLabel());
        assertEquals("CON", byName(out, "l3<CON>").getLabel());
    }

    @Test
    void shortenedLabelIsCopiedToEveryParameterisedEntry() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("sh", "", "Poison", "Pois", List.of(), "element", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        // copy_shortened_labels in hatch_embryo copies label5 to each entry.
        assertTrue(out.stream().allMatch(e -> "Pois".equals(e.getLabel5())));
    }

    @Test
    void indexSchemeGivesEachElementEntryItsIndexAsDefaultPriority() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p1", "", "", "", List.of(), "element", "ADD", "index", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        // get_priority_from_index(i) == i over element_names[]: ACID 0, CHAOS 11, ARROW 24.
        assertEquals(0, byName(out, "p1<ACID>").getDefaultPriority());
        assertEquals(11, byName(out, "p1<CHAOS>").getDefaultPriority());
        assertEquals(24, byName(out, "p1<ARROW>").getDefaultPriority());
    }

    @Test
    void negativeIndexSchemeGivesEachStatEntryTheNegatedIndex() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p2", "", "", "", List.of(), "stat", "ADD", "negative_index", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        assertEquals(0, byName(out, "p2<STR>").getDefaultPriority());
        assertEquals(-4, byName(out, "p2<CON>").getDefaultPriority());
    }

    @Test
    void numericPriorityIsTheDefaultPriorityOfAnEntryWithNoCategories() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p3", "", "", "", List.of(), "", "ADD", "7", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        assertEquals(7, out.get(0).getDefaultPriority());
    }

    @Test
    void categoryBeforeAPriorityLineTakesTheParameterisedPriority() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                full("p4", "", "", "", List.of("c1"), "element", "ADD", "negative_index", List.of(),
                        List.of())), new ArrayList<>(), "test.txt");

        UIEntry fire = out.stream().filter(e -> e.getName().equals("p4<FIRE>")).findFirst().orElseThrow();
        UIEntryCategory c1 = fire.getCategories().get(0);
        // parse_entry_priority attaches the scheme to last_category_index; parameterize_category_list
        // then evaluates it for FIRE (index 2) and sets priority_set.
        assertEquals("c1", c1.getName());
        assertEquals(-2, c1.getPriority());
        assertTrue(c1.isPrioritySet());
    }

    // ---- item 10: a category before the priority line leaves the default alone ---------------------
    //
    // parse_entry_priority sets default_priority only when last_category_index == -1; otherwise the
    // number goes to that category and default_priority keeps its value from parse_entry_name (0).

    @Test
    void numericPriorityAfterACategoryLeavesTheDefaultPriorityZero() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("c1", "", "", "", List.of("first"), "", "ADD", "5", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntry created = byName(out, "c1");
        assertEquals(0, created.getDefaultPriority());
        UIEntryCategory first = created.getCategories().get(0);
        assertEquals(5, first.getPriority());
        assertTrue(first.isPrioritySet());
    }

    @Test
    void aCategoryAfterThePriorityLineDoesNotInheritADefaultThePriorityNeverSet() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("c2", "", "", "", List.of("first"), "", "ADD", "5", List.of("second"), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntry created = byName(out, "c2");
        // The 5 went to "first"; "second" is priority_set false and waits on the entry's default, 0.
        assertEquals(0, created.getDefaultPriority());
        UIEntryCategory second = created.getCategories().stream()
                .filter(c -> c.getName().equals("second")).findFirst().orElseThrow();
        assertFalse(second.isPrioritySet());
    }

    @Test
    void numericPriorityAfterACategoryLeavesEveryParameterisedDefaultZero() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("c3", "", "", "", List.of("first"), "element", "ADD", "5", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        assertEquals(25, out.size());
        assertTrue(out.stream().allMatch(e -> e.getDefaultPriority() == 0));
        assertTrue(out.stream().allMatch(e -> e.getCategories().get(0).getPriority() == 5));
    }

    @Test
    void overrideNumericPriorityAfterACategoryGoesToThatCategoryNotTheDefault() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("c4", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of()),
                        full("c4<ACID>", "", "", "", List.of("mycat"), "", "", "5", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntry acid = byName(out, "c4<ACID>");
        // The override path still reads the record's raw number: only the create path's default changed.
        assertEquals(0, acid.getDefaultPriority());
        UIEntryCategory mycat = acid.getCategories().stream()
                .filter(c -> c.getName().equals("mycat")).findFirst().orElseThrow();
        assertEquals(5, mycat.getPriority());
        assertTrue(mycat.isPrioritySet());
    }

    @Test
    void categoryAfterAPriorityLineIsLeftForTheFinishingPass() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("p5", "", "", "", List.of(), "", "ADD", "3", List.of("c2"), List.of())),
                new ArrayList<>(), "test.txt");

        // priority: with no category yet sets the default; the later category is priority_set false.
        assertEquals(3, out.get(0).getDefaultPriority());
        assertFalse(out.get(0).getCategories().get(0).isPrioritySet());
    }

    /**
     * Run the body with the base placeholders in the entry registry, as they are in the game, then
     * empty it again for the next test.
     */
    private static void withTemplatesRegistered(Runnable body) {
        UIRegistry.setUIEntries(new ArrayList<>(templatePlaceholders));
        try {
            body.run();
        } finally {
            UIRegistry.setUIEntries(List.of());
        }
    }

    @Test
    void templateSuppliesCombinerFlagsAndCategoriesAndDropsTemplateOnly() {
        withTemplatesRegistered(() -> {
            List<String> errors = new ArrayList<>();
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("t1", "good_flag_ui_compact_0", "", "", List.of(), "", "", "", List.of(), List.of())),
                    errors, "test.txt");

            // parse_entry_template copies renderer, combiner, flags (minus TEMPLATE_ONLY) and categories,
            // so a record with no combine: of its own still survives hatch_embryo's combiner check.
            assertTrue(errors.isEmpty(), errors::toString);
            UIEntry e = byName(out, "t1");
            assertEquals(CombinerName.LOGICAL_OR, e.getCombineType());
            assertTrue(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
            assertFalse(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY));
            List<String> cats = e.getCategories().stream().map(UIEntryCategory::getName).toList();
            assertTrue(cats.containsAll(List.of("CHAR_SCREEN1", "EQUIPCMP_SCREEN", "abilities")), cats::toString);
        });
    }

    @Test
    void anEarlierRecordCanBeUsedAsATemplate() {
        List<String> errors = new ArrayList<>();
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("tpl_src", "", "", "", List.of(), "", "LOGICAL_OR", "5", List.of("srccat"),
                                List.of("TIMED_AS_AUX")),
                        full("tpl_user", "tpl_src", "", "", List.of(), "", "", "", List.of(), List.of())),
                errors, "test.txt");

        // ui_entry_lookup searches every entry parsed so far, not just ui_entry_base.txt, so the
        // second record inherits combiner, flags, categories and default priority from the first.
        assertTrue(errors.isEmpty(), errors::toString);
        UIEntry e = byName(out, "tpl_user");
        assertEquals(CombinerName.LOGICAL_OR, e.getCombineType());
        assertTrue(e.getEntryFlag().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX));
        assertEquals(5, e.getDefaultPriority());
        List<String> cats = e.getCategories().stream().map(UIEntryCategory::getName).toList();
        assertTrue(cats.contains("srccat"), cats::toString);
    }

    @Test
    void aRecordDefinedLaterInTheFileIsNotYetATemplate() {
        String message = fatalMessage(
                full("tpl_early", "tpl_late", "", "", List.of(), "", "ADD", "", List.of(), List.of()),
                full("tpl_late", "", "", "", List.of(), "", "ADD", "", List.of(), List.of()));

        // The lookup sees only the entries before the template: line, so a later record is not found.
        assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal template value: "
                + "tpl_late", message);
    }

    // ---- create path: template default priority ------------------------------------------------

    private static final String PRIORITY_TEMPLATE = "good_flag_ui_compact_0";

    /**
     * No shipped base sets a default priority, so give the template's placeholder one for the
     * duration of a test and put it back afterwards.
     */
    private static void withTemplateDefaultPriority(int priority, Runnable body) {
        UIEntry template = byName(templatePlaceholders, PRIORITY_TEMPLATE);
        int old = template.getDefaultPriority();
        template.setDefaultPriority(priority);
        try {
            withTemplatesRegistered(body);
        } finally {
            template.setDefaultPriority(old);
        }
    }

    @Test
    void templateDefaultPriorityIsKeptWhenTheRecordHasNoPriorityLine() throws Exception {
        withTemplateDefaultPriority(7, () -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("tp1", PRIORITY_TEMPLATE, "", "", List.of(), "", "", "", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            // parse_entry_template copies tentry->default_priority and nothing overwrites it.
            assertEquals(7, byName(out, "tp1").getDefaultPriority());
        });
    }

    @Test
    void recordPriorityOverridesTheTemplateDefaultWhenNoCategoryCameFirst() throws Exception {
        withTemplateDefaultPriority(7, () -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("tp2", PRIORITY_TEMPLATE, "", "", List.of(), "", "", "3", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            // last_category_index == -1, so parse_entry_priority sets default_priority.
            assertEquals(3, byName(out, "tp2").getDefaultPriority());
        });
    }

    @Test
    void aNumericZeroPriorityStillOverridesTheTemplateDefault() throws Exception {
        withTemplateDefaultPriority(7, () -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("tp3", PRIORITY_TEMPLATE, "", "", List.of(), "", "", "0", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            assertEquals(0, byName(out, "tp3").getDefaultPriority());
        });
    }

    @Test
    void recordPriorityAfterACategoryLeavesTheTemplateDefaultAlone() throws Exception {
        withTemplateDefaultPriority(7, () -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("tp4", PRIORITY_TEMPLATE, "", "", List.of("zz"), "", "", "3", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            // The priority went to category zz (priority_set true); default_priority stays the template's.
            assertEquals(7, byName(out, "tp4").getDefaultPriority());
            UIEntryCategory zz = byName(out, "tp4").getCategories().stream()
                    .filter(c -> c.getName().equals("zz")).findFirst().orElseThrow();
            assertEquals(3, zz.getPriority());
            assertTrue(zz.isPrioritySet());
        });
    }

    @Test
    void indexSchemeOnAnUnparameterizedTemplateEntrySetsDefaultToZero() throws Exception {
        withTemplateDefaultPriority(7, () -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("tp5", PRIORITY_TEMPLATE, "", "", List.of(), "", "", "index", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            // psource_index is set but never applied without a parameter, and priority is 0.
            assertEquals(0, byName(out, "tp5").getDefaultPriority());
        });
    }

    @Test
    void parameterizedTemplateEntryKeepsTheTemplateDefaultOrTakesTheScheme() throws Exception {
        withTemplateDefaultPriority(7, () -> {
            List<UIEntry> plain = new UIEntryAssembler().assemble(List.of(
                            full("tp6", PRIORITY_TEMPLATE, "", "", List.of(), "element", "", "", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            // No priority line: psource_index is 0, so every element entry keeps the template's default.
            List<UIEntry> plainOnes = plain.stream().filter(e -> e.getName().startsWith("tp6<")).toList();
            assertFalse(plainOnes.isEmpty());
            for (UIEntry e : plainOnes) {
                assertEquals(7, e.getDefaultPriority(), e.getName());
            }
            List<UIEntry> indexed = new UIEntryAssembler().assemble(List.of(
                            full("tp7", PRIORITY_TEMPLATE, "", "", List.of(), "element", "", "index", List.of(), List.of())),
                    new ArrayList<>(), "test.txt");
            // hatch_embryo replaces the default with scheme(i), i being the element's index.
            UIEntry fire = indexed.stream().filter(e -> e.getName().equals("tp7<FIRE>")).findFirst().orElseThrow();
            assertEquals(2, fire.getDefaultPriority());
        });
    }

    // ---- override path ----------------------------------------------------------------------

    @Test
    void overrideOrsFlagsIntoTheExistingEntryAndKeepsThemWhenNoneAreGiven() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("o1", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of()),
                        full("o1<ACID>", "", "", "", List.of(), "", "", "", List.of(), List.of("TIMED_AS_AUX")),
                        full("o1<ELEC>", "", "", "", List.of(), "", "", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

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
                new ArrayList<>(), "test.txt");

        UIEntry fire = out.stream().filter(e -> e.getName().equals("o2<FIRE>")).findFirst().orElseThrow();
        UIEntry cold = out.stream().filter(e -> e.getName().equals("o2<COLD>")).findFirst().orElseThrow();
        assertEquals("Fire", fire.getLabel());
        assertEquals(CombinerName.LOGICAL_OR, fire.getCombineType());
        // The neighbouring entry is untouched.
        assertEquals("COLD", cold.getLabel());
        assertEquals(CombinerName.ADD, cold.getCombineType());
    }

    @Test
    void overrideWithNoLabelLinesLeavesTheExistingLabelAndShortenedLabelsAlone() {
        // parse_entry_label and parse_entry_shortened_label only run for a line that is present.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        new UIEntryParseRecord("o4", "", "Res", "Resis", "Rs", List.of(), "element", "", "ADD",
                                "", List.of(), List.of(), "", 1),
                        new UIEntryParseRecord("o4<ACID>", "", "", "", "", List.of(), "", "", "",
                                "", List.of(), List.of(), "", 2)),
                new ArrayList<>(), "test.txt");

        UIEntry acid = out.stream().filter(e -> e.getName().equals("o4<ACID>")).findFirst().orElseThrow();
        assertEquals("Res", acid.getLabel());
        assertEquals("Resis", acid.getLabel5());
        assertEquals("Rs", acid.getLabel2());
    }

    @Test
    void overrideWithOneShortenedLabelWritesOnlyThatSlot() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        new UIEntryParseRecord("o5", "", "Res", "Resis", "Rs", List.of(), "element", "", "ADD",
                                "", List.of(), List.of(), "", 1),
                        new UIEntryParseRecord("o5<ACID>", "", "", "", "Xx", List.of(), "", "", "",
                                "", List.of(), List.of(), "", 2)),
                new ArrayList<>(), "test.txt");

        UIEntry acid = out.stream().filter(e -> e.getName().equals("o5<ACID>")).findFirst().orElseThrow();
        assertEquals("Xx", acid.getLabel2());
        assertEquals("Resis", acid.getLabel5());
        assertEquals("Res", acid.getLabel());
    }

    @Test
    void overrideAddsANewCategoryOnceAndNotAgainWhenAlreadyPresent() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("o3", "", "", "", List.of("a"), "element", "ADD", "", List.of(), List.of()),
                        full("o3<ACID>", "", "", "", List.of("a", "b"), "", "", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntry acid = out.stream().filter(e -> e.getName().equals("o3<ACID>")).findFirst().orElseThrow();
        List<String> cats = acid.getCategories().stream().map(UIEntryCategory::getName).toList();
        // search_embryo_categories finds "a" already present, so only "b" is inserted.
        assertEquals(2, cats.size(), cats::toString);
        assertTrue(cats.containsAll(List.of("a", "b")));
    }

    // ---- a record that repeats the previous record's name (accepted deviation, 261102) --------------
    //
    // parse_entry_name returns at once when the new name equals the open embryo's name, so the second
    // record keeps editing the first embryo. UIEntryAssembler resolves each record on its own. The
    // first three tests pin Java's behaviour where it differs from C; the fourth pins the case where
    // the two agree. See "UI entry" in docs/Accepted_Deviations.md.

    @Test
    void aParameterLineOnARepeatedNameEndsTheLoadHereWhereCAcceptsItOnANewEmbryo() {
        // C: the first record's embryo is new (exists is false), so parse_entry_parameter accepts the
        // parameter: line. Java: the first record's entry is already in the list, so the name is
        // reported as pre-existing.
        String message = fatalMessage(
                full("o4", "", "", "", List.of(), "", "ADD", "", List.of(), List.of()),
                full("o4", "", "", "", List.of(), "element", "", "", List.of(), List.of()));

        assertEquals("UI entry starting at line: 1 of file: test.txt has an pre-existing stat/parameter "
                + "name: o4", message);
    }

    @Test
    void aRepeatedNameAfterAParameterisedRecordMakesASeparateBareEntryHereWhereCEditsTheSameEmbryo() {
        // C: the second record edits the first embryo, so the bare name is never entered on its own.
        // Java: only the name<X> entries exist after the first record, so the second record creates
        // the bare-name entry.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("o6", "", "", "", List.of(), "stat", "ADD", "", List.of(), List.of()),
                        full("o6", "", "", "", List.of(), "", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        assertEquals(6, out.size(), () -> out.stream().map(UIEntry::getName).toList().toString());
        assertEquals("o6", byName(out, "o6").getName());
        assertEquals(StatElemType.NONE, byName(out, "o6").getStatOrElement());
        assertEquals(StatElemType.STAT, byName(out, "o6<STR>").getStatOrElement());
    }

    @Test
    void aSecondParameterLineOnARepeatedNameEndsTheLoadHereWhereCOverwritesTheFirst() {
        // C: parse_entry_parameter overwrites param_index on the same embryo. Java: the second fan-out
        // reaches the override path for o7<ACID>, which refuses an entry that still has a parameter.
        String message = fatalMessage(
                full("o7", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of()),
                full("o7", "", "", "", List.of(), "element", "ADD", "", List.of(), List.of()));

        assertEquals("UI Entry starting at line: 1 of file: test.txt is a duplicate of a non null "
                + "parameter", message);
    }

    @Test
    void aParameterLineOnARepeatedNameOfAnEntryFromAnEarlierFileIsFatalAsInC() {
        // parse_entry_parameter returns PARSE_ERROR_INVALID_OPTION when embryo->exists, which is the
        // case when the name was entered by an earlier file (ui_entry_base.txt); Java agrees.
        List<UIEntry> earlier = new UIEntryAssembler().assemble(List.of(
                        full("o8", "", "", "", List.of(), "", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "base.txt");
        UIRegistry.setUIEntries(new ArrayList<>(earlier));
        try {
            String message = fatalMessage(
                    full("o8", "", "", "", List.of(), "element", "", "", List.of(), List.of()));

            assertEquals("UI entry starting at line: 1 of file: test.txt has an pre-existing "
                    + "stat/parameter name: o8", message);
        } finally {
            UIRegistry.setUIEntries(List.of());
        }
    }

    // ---- accepted deviations: the parse record keeps no line order (261102) ---------------------

    @Test
    void aRecordsOwnRendererBeatsItsTemplateWhateverTheLineOrderHereWhereCTakesTheLastLine() {
        // parse_entry_template overwrites renderer_index, so in C a template: line written after a
        // renderer: line wins. The parse record has lost the order, so Java applies the template
        // first and the record's own renderer always wins. Shipped data writes template: first.
        withTemplatesRegistered(() -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            new UIEntryParseRecord("ro1", PRIORITY_TEMPLATE, "", "", "", List.of(), "",
                                    "char_screen1_numeric_as_sign_renderer", "", "", List.of(), List.of(), "", 1)),
                    new ArrayList<>(), "test.txt");

            assertEquals("char_screen1_numeric_as_sign_renderer", byName(out, "ro1").getRenderer().getName());
        });
    }

    @Test
    void aPriorityOnACategoryTheTemplateAlreadySuppliedLandsOnItAsInC() {
        // In C the category: line finds the template's category and the following priority: line
        // sets its priority and priority_set. The create path copies the priority onto the category
        // it already holds, so it lands. No shipped record that has a template also has a category: line.
        withTemplatesRegistered(() -> {
            List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                            full("dc1", PRIORITY_TEMPLATE, "", "", List.of("abilities"), "", "", "5",
                                    List.of(), List.of())),
                    new ArrayList<>(), "test.txt");

            UIEntryCategory abilities = byName(out, "dc1").getCategories().stream()
                    .filter(c -> c.getName().equals("abilities")).findFirst().orElseThrow();
            assertTrue(abilities.isPrioritySet());
            assertEquals(5, abilities.getPriority());
        });
    }

    // ---- a category with no priority: line stays unset (buildCategories priorityFound) ------------

    // parse_entry_category adds a category with priority_set false; only parse_entry_priority sets it
    // true. So a record with categories and no priority: line has none of them set.

    @Test
    void noPriorityLineLeavesEveryBeforeCategoryUnset() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("np1", "", "", "", List.of("a", "b"), "", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntry created = byName(out, "np1");
        assertEquals(0, created.getDefaultPriority());
        for (UIEntryCategory category : created.getCategories()) {
            assertFalse(category.isPrioritySet(), category.getName());
        }
    }

    @Test
    void noPriorityLineLeavesTheLastBeforeCategoryUnsetOnATemplatedRecordWithANonZeroDefault() {
        // The template's default priority is 5. In C the category stays unset and the finishing pass
        // fills it with 5; marked set at 0 it would stay 0.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("np2_tpl", "", "", "", List.of(), "", "ADD", "5", List.of(), List.of()),
                        full("np2", "np2_tpl", "", "", List.of("x"), "", "", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntry created = byName(out, "np2");
        assertEquals(5, created.getDefaultPriority());
        UIEntryCategory x = created.getCategories().stream()
                .filter(c -> c.getName().equals("x")).findFirst().orElseThrow();
        assertFalse(x.isPrioritySet());
    }

    @Test
    void anExplicitZeroPriorityStillSetsTheLastBeforeCategory() {
        // "0" is a priority line, unlike the empty string that means there was none.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("np3", "", "", "", List.of("a", "b"), "", "ADD", "0", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        List<UIEntryCategory> cats = byName(out, "np3").getCategories();
        assertFalse(cats.get(0).isPrioritySet(), "a");
        assertTrue(cats.get(1).isPrioritySet(), "b");
        assertEquals(0, cats.get(1).getPriority());
    }

    @Test
    void noPriorityLineLeavesTheBeforeCategoriesOfEachElementEntryUnset() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("np4", "", "", "", List.of("x"), "element", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        for (String tag : List.of("ACID", "CHAOS", "ARROW")) {
            UIEntryCategory x = byName(out, "np4<" + tag + ">").getCategories().get(0);
            assertFalse(x.isPrioritySet(), tag);
        }
    }

    @Test
    void noPriorityLineLeavesTheBeforeCategoriesOfEachStatEntryUnset() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("np5", "", "", "", List.of("x"), "stat", "ADD", "", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        for (String tag : List.of("STR", "CON")) {
            UIEntryCategory x = byName(out, "np5<" + tag + ">").getCategories().get(0);
            assertFalse(x.isPrioritySet(), tag);
        }
    }

    @Test
    void anIndexPriorityLineStillSetsTheLastBeforeCategoryOfEachElementEntry() {
        // get_priority_from_index(i) == i; the priority: line attaches to the category before it.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                        full("np6", "", "", "", List.of("x"), "element", "ADD", "index", List.of(), List.of())),
                new ArrayList<>(), "test.txt");

        UIEntryCategory fire = byName(out, "np6<FIRE>").getCategories().get(0);
        assertTrue(fire.isPrioritySet());
        assertEquals(2, fire.getPriority());
        assertEquals(0, byName(out, "np6<FIRE>").getDefaultPriority());
    }

    // ---- priority text: parse_entry_priority's strtol and end check ----------------------------

    /**
     * Build a record with an unparameterized name and only the label and priority lines set.
     */
    private static UIEntryParseRecord withPriority(String name, String priority) {
        return new UIEntryParseRecord(name, "", "", "", "", List.of(), "", "", "ADD",
                priority, List.of(), List.of(), "", 1);
    }

    /**
     * The default priority of the single entry a one-record file with this {@code priority:} makes.
     */
    private static int defaultPriorityOf(String priority) {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(withPriority("pt", priority)),
                new ArrayList<>(), "test.txt");
        return byName(out, "pt").getDefaultPriority();
    }

    @Test
    void aSignedDecimalPriorityIsReadAsStrtolReadsIt() {
        // strtol(name, &end, 10): "-5" is -5, "+5" is 5, "007" is 7 (base 10, not octal).
        assertEquals(-5, defaultPriorityOf("-5"));
        assertEquals(5, defaultPriorityOf("+5"));
        assertEquals(7, defaultPriorityOf("007"));
        assertEquals(0, defaultPriorityOf("0"));
        assertEquals(0, defaultPriorityOf("-0"));
    }

    @Test
    void textAfterThePriorityDigitsIsFatalAsInC() {
        // parse_entry_priority returns PARSE_ERROR_NOT_NUMBER when *end is not the terminator, so
        // the load ends. strtol would have read 5, 0, 1 and 5 from these.
        for (String text : List.of("5x", "0x10", "1.5", "5 6")) {
            assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal priority value: "
                    + text, fatalMessage(withPriority("pt", text)), text);
        }
    }

    @Test
    void aPriorityWithNoDigitsIsFatalAsInC() {
        // strtol reads nothing from these, so *end is the first character and the record is refused.
        for (String text : List.of("abc", "-", "+", "+-5", "--5", "x10")) {
            assertEquals("UI entry starting at line: 1 of file: test.txt has an illegal priority value: "
                    + text, fatalMessage(withPriority("pt", text)), text);
        }
    }

    @Test
    void aPriorityAboveIntMaxClampsAsInC() {
        // priority = (v <= INT_MAX) ? ... : INT_MAX
        assertEquals(Integer.MAX_VALUE, defaultPriorityOf("2147483647"));
        assertEquals(Integer.MAX_VALUE, defaultPriorityOf("2147483648"));
        assertEquals(Integer.MAX_VALUE, defaultPriorityOf("99999999999"));
    }

    @Test
    void aPriorityOfIntMinOrBelowLoadsOneAboveIntMinHereWhereCClampsToIntMin() {
        // Accepted deviation 261102: C gives INT_MIN for all three; ParseUtils clamps the digits
        // before the sign is applied.
        assertEquals(-Integer.MAX_VALUE, defaultPriorityOf("-2147483648"));
        assertEquals(-Integer.MAX_VALUE, defaultPriorityOf("-99999999999"));
        // The last value C and Java agree on.
        assertEquals(-Integer.MAX_VALUE, defaultPriorityOf("-2147483647"));
    }

    // ---- label, label2 and label5: parse_entry_label / parse_entry_shortened_label -------------

    /**
     * Build a record with a parameter kind and the three label lines set.
     */
    private static UIEntryParseRecord withLabels(String name, String parameter, String label, String label5,
                                                 String label2) {
        return new UIEntryParseRecord(name, "", label, label5, label2, List.of(), parameter, "", "ADD",
                "", List.of(), List.of(), "", 1);
    }

    /**
     * Whether a shortened-label slot is unset, which C records as {@code nshortened == 0}: left for
     * the finishing pass to fill.
     */
    private static boolean unset(UIEntry entry, int slot) {
        String text = entry.getShortenedLabel(slot);
        return text == null || text.isEmpty();
    }

    @Test
    void aLabelOfEightyCharactersIsKeptWholeAndEightyOneIsCutOnEveryPath() {
        // MAX_ENTRY_LABEL is 80: text_mbstowcs(..., MAX_ENTRY_LABEL) drops the rest with no error.
        String eighty = "L".repeat(80);
        String eightyOne = "L".repeat(81);
        String longest = "L".repeat(100);

        List<UIEntry> plain = new UIEntryAssembler().assemble(List.of(
                withLabels("w80", "", eighty, "", ""), withLabels("w81", "", eightyOne, "", ""),
                withLabels("w100", "", longest, "", "")), new ArrayList<>(), "test.txt");
        List<UIEntry> element = new UIEntryAssembler().assemble(List.of(
                withLabels("e81", "element", eightyOne, "", "")), new ArrayList<>(), "test.txt");
        List<UIEntry> stat = new UIEntryAssembler().assemble(List.of(
                withLabels("s81", "stat", eightyOne, "", "")), new ArrayList<>(), "test.txt");

        assertEquals(eighty, byName(plain, "w80").getLabel());
        assertEquals(eighty, byName(plain, "w81").getLabel());
        assertEquals(eighty, byName(plain, "w100").getLabel());
        assertEquals(eighty, byName(element, "e81<ACID>").getLabel());
        assertEquals(eighty, byName(element, "e81<ARROW>").getLabel());
        assertEquals(eighty, byName(stat, "s81<STR>").getLabel());
        assertEquals(eighty, byName(stat, "s81<CON>").getLabel());
    }

    @Test
    void label5AndLabel2AreCutToFiveAndTwoCharactersOnEveryPath() {
        // text_mbstowcs(shortened_labels[n - 1], name, n) keeps at most n characters.
        List<UIEntry> plain = new UIEntryAssembler().assemble(List.of(
                withLabels("c1", "", "Tunneling", "ABCDEFGH", "XYZ")), new ArrayList<>(), "test.txt");
        List<UIEntry> element = new UIEntryAssembler().assemble(List.of(
                withLabels("c2", "element", "Tunneling", "ABCDEFGH", "XYZ")), new ArrayList<>(), "test.txt");
        List<UIEntry> stat = new UIEntryAssembler().assemble(List.of(
                withLabels("c3", "stat", "Tunneling", "ABCDEFGH", "XYZ")), new ArrayList<>(), "test.txt");

        for (UIEntry entry : List.of(byName(plain, "c1"), byName(element, "c2<ACID>"),
                byName(element, "c2<ARROW>"), byName(stat, "c3<STR>"), byName(stat, "c3<CON>"))) {
            assertEquals("ABCDE", entry.getLabel5(), entry.getName());
            assertEquals("XY", entry.getLabel2(), entry.getName());
        }
    }

    @Test
    void anExplicitLabel5IsKeptAndTheShorterSlotsArePrefixesOfIt() {
        // Slot 4 is label5 itself ("Tunn." is not a prefix of "Tunneling"); fill_out_shortened takes
        // slots 3 and 2 from the next longer slot that is set, which is label5.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                withLabels("sl1", "", "Tunneling", "Tunn.", "")), new ArrayList<>(), "test.txt");

        UIEntry entry = byName(out, "sl1");
        assertEquals("Tunn.", entry.getShortenedLabel(4));
        assertEquals("Tunn", entry.getShortenedLabel(3));
        assertEquals("Tun", entry.getShortenedLabel(2));
        assertEquals("Tunneling", entry.getLabel());
    }

    @Test
    void anExplicitLabel2IsKeptAndSlotZeroIsItsFirstCharacter() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                withLabels("sl2", "", "Tunneling", "", "Tx")), new ArrayList<>(), "test.txt");

        UIEntry entry = byName(out, "sl2");
        assertEquals("Tx", entry.getShortenedLabel(1));
        assertEquals("T", entry.getShortenedLabel(0));
    }

    @Test
    void slotsAboveFiveArePrefixesOfTheFullLabel() {
        // No label6 to label10 line is read, so these come from the full label, as in C.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                withLabels("sl3", "", "Tunneling", "Tunn.", "Tx")), new ArrayList<>(), "test.txt");

        UIEntry entry = byName(out, "sl3");
        assertEquals("Tunnel", entry.getShortenedLabel(5));
        assertEquals("Tunneli", entry.getShortenedLabel(6));
        assertEquals("Tunnelin", entry.getShortenedLabel(7));
        assertEquals("Tunneling", entry.getShortenedLabel(8));
    }

    @Test
    void anAbsentLabel5AndLabel2LeaveTheirSlotsUnsetForTheFinishingPass() {
        // C stores nothing for a label line that is absent; fill_out_shortened fills the slot later.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                withLabels("sl4", "", "Tunneling", "", "")), new ArrayList<>(), "test.txt");

        UIEntry entry = byName(out, "sl4");
        for (int slot = 0; slot <= 4; slot++) {
            assertTrue(unset(entry, slot), "slot " + slot);
        }
    }

    @Test
    void aRecordWithNoLabelLinesLeavesEverySlotUnsetOnAnElementEntry() {
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                withLabels("sl5", "element", "", "", "")), new ArrayList<>(), "test.txt");

        UIEntry entry = byName(out, "sl5<ACID>");
        for (int slot = 0; slot <= 8; slot++) {
            assertTrue(unset(entry, slot), "slot " + slot);
        }
    }

    @Test
    void aLabelShorterThanTheShortenedLabelsDoesNotOverrun() {
        // A 3 character label with a 5 character label5: nothing may index past either string.
        List<UIEntry> out = new UIEntryAssembler().assemble(List.of(
                withLabels("sl6", "", "Res", "Resis", "Rs")), new ArrayList<>(), "test.txt");

        UIEntry entry = byName(out, "sl6");
        assertEquals("Res", entry.getLabel());
        assertEquals("Resis", entry.getLabel5());
        assertEquals("Rs", entry.getLabel2());
        assertEquals("Res", entry.getShortenedLabel(5));
    }
}
