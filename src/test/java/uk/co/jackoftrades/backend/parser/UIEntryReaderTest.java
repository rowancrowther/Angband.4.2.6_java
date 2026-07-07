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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the UIEntry pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryReader} → {@link uk.co.jackoftrades.backend.parser.uientry.UIEntryAssembler} →
 * resolved {@link UIEntry} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/ui_entry.txt}; a clean
 * load of all 47 records is itself the assertion that every renderer, template, combiner and flag
 * resolved. It also spot-checks the {@code parameter:}/{@code nameTag} split that this pipeline hinges
 * on (a generic entry gets its stat/element <em>kind</em>; a specialization keeps its full tagged name
 * and gets a concrete element). The remaining tests inject one defect each to exercise both error
 * channels: hard grammar errors (missing {@code record-count}) fail closed; soft errors (unknown
 * renderer/template, bad {@code parameter:} kind, record-count mismatch) are reported while partial
 * results survive.
 *
 * <p>The assembler resolves against the renderer and base registries, so {@link #seedRegistries()}
 * loads the real renderer and base files and injects them into {@code GameConstants}' private static
 * fields via reflection, independent of full-game init order.
 *
 * @author Rowan Crowther
 */
class UIEntryReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ui_entry.txt";

    @TempDir
    Path tempDir;

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

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    private static UIEntry byName(List<UIEntry> items, String name) {
        return items.stream().filter(u -> name.equals(u.getName())).findFirst()
                .orElseThrow(() -> new AssertionError("no UIEntry named " + name));
    }

    // ---- happy path -------------------------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileResolvesAllEntries() throws IOException {
        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        // 47 records in the file, but the single stat_mod_ui_compact_0 + parameter:stat entry is
        // expanded into one tagged entry per stat (STR..CON), so 47 - 1 + 5 = 51 resolved entries.
        assertEquals(51, result.items().size());

        // Generic entry: parameter:element sets the ELEMENT kind, no tag so no concrete element.
        UIEntry generic = byName(result.items(), "resist_ui_compact_0");
        assertEquals(UIEntry.StatElemType.ELEMENT, generic.getStatOrElement());
        assertEquals(ElementEnum.ELEM_NONE, generic.getParameter());

        // Specialization: no parameter: line (kind NONE), tag drives the concrete element, and the
        // name keeps the full tagged form so bindui look-ups by "name<TAG>" match.
        UIEntry dark = byName(result.items(), "resist_ui_compact_0<DARK>");
        assertEquals(UIEntry.StatElemType.NONE, dark.getStatOrElement());
        assertEquals(ElementEnum.ELEM_DARK, dark.getParameter());

        // The stat entry is expanded: the tagless stat_mod_ui_compact_0 no longer exists on its own;
        // it becomes five tagged entries, each still carrying the STAT kind.
        UIEntry statStr = byName(result.items(), "stat_mod_ui_compact_0<STR>");
        assertEquals(UIEntry.StatElemType.STAT, statStr.getStatOrElement());
    }

    @Test
    void parameterAndNameTagSplitSurviveTheWholePipeline() throws IOException {
        // A generic (parameter:element, no tag) and a specialization (tag, no parameter:) together.
        String path = tempFile("split.txt",
                "record-count:2\nname:generic_thing\nparameter:element\nname:tagged_thing<ACID>\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());

        UIEntry generic = byName(result.items(), "generic_thing");
        assertEquals(UIEntry.StatElemType.ELEMENT, generic.getStatOrElement());
        assertEquals(ElementEnum.ELEM_NONE, generic.getParameter());

        UIEntry tagged = byName(result.items(), "tagged_thing<ACID>");
        assertEquals(UIEntry.StatElemType.NONE, tagged.getStatOrElement());
        assertEquals(ElementEnum.ELEM_ACID, tagged.getParameter());
    }

    // ---- soft errors (partial results survive) ----------------------------------------------

    @Test
    void recordCountMismatchIsLoggedButValidRecordSurvives() throws IOException {
        String path = tempFile("bad-count.txt", "record-count:5\nname:foo\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void unknownRendererIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-renderer.txt", "record-count:1\nname:foo\nrenderer:no_such_renderer\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("no_such_renderer")),
                result.errors()::toString);
    }

    @Test
    void unknownTemplateIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-template.txt", "record-count:1\nname:foo\ntemplate:no_such_base\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal template name")),
                result.errors()::toString);
    }

    @Test
    void illegalParameterKindIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-param.txt", "record-count:1\nname:foo\nparameter:bogus\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal parameter kind: bogus")),
                result.errors()::toString);
    }

    // ---- hard errors (fail-closed: empty list) ----------------------------------------------

    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        // No record-count directive -> the file rule can't match -> grammar error via ParseErrors.
        String path = tempFile("no-header.txt", "name:foo\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
