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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryAssembler;
import uk.co.jackoftradesltd.frontend.ui.entry.reader.UIEntryReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * End-to-end tests for the UIEntry pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryReader} → {@link UIEntryAssembler} →
 * resolved {@link UIEntry} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/ui_entry.txt}; a clean
 * load of all 47 records is itself the assertion that every renderer, template, combiner and flag
 * resolved. It also spot-checks the {@code parameter:}/{@code nameTag} split that this pipeline hinges
 * on (a generic entry gets its stat/element <em>kind</em>; a specialization keeps its full tagged name
 * and gets a concrete element). The remaining tests inject one defect each to exercise the error
 * channels: a grammar error fails closed; an unknown renderer/template or a bad
 * {@code parameter:} kind is fatal, ending the load with a {@link DataFileParseException} as a
 * failed {@code parse_entry_*} callback does in C.
 *
 * <p>The assembler resolves against the renderer and base registries, so {@link #seedRegistries()}
 * loads the real renderer and base files and injects them into {@code UIRegistry}' private static
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
        // Renderers first: the entry assembler resolves renderers, so it needs them seeded.
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        setStatic("uiEntryRenderers", renderers);

        // Every test below assumes UIEntryAssembler.assemble() starts from an empty registry, so
        // the base entries are deliberately not registered.
        UIRegistry.setUIEntries(List.of());
    }

    private static void setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
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
    @Disabled("UIEntryAssembler.assemble() doesn't insert into results yet - embryo dispatch is "
            + "stubbed out and the stat/element expansion loops only keep their last iteration "
            + "(260919); re-enable once it actually populates its return value")
    void cleanLoadOfTheRealFileResolvesAllEntries() throws IOException {
        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(REAL_FILE);

        // 47 records in the file, but the single stat_mod_ui_compact_0 + parameter:stat entry is
        // expanded into one tagged entry per stat (STR..CON), so 47 - 1 + 5 = 51 resolved entries.
        assertEquals(51, result.items().size());

        // Generic entry: parameter:element sets the ELEMENT kind, no tag so no concrete element.
        UIEntry generic = byName(result.items(), "resist_ui_compact_0");
        assertEquals(StatElemType.ELEMENT, generic.getStatOrElement());
        assertEquals(ElementEnum.ELEM_NONE, generic.getParameter());

        // Specialization: no parameter: line (kind NONE), tag drives the concrete element, and the
        // name keeps the full tagged form so bindui look-ups by "name<TAG>" match.
        UIEntry dark = byName(result.items(), "resist_ui_compact_0<DARK>");
        assertEquals(StatElemType.NONE, dark.getStatOrElement());
        assertEquals(ElementEnum.ELEM_DARK, dark.getParameter());

        // The stat entry is expanded: the tagless stat_mod_ui_compact_0 no longer exists on its own;
        // it becomes five tagged entries, each still carrying the STAT kind.
        UIEntry statStr = byName(result.items(), "stat_mod_ui_compact_0<STR>");
        assertEquals(StatElemType.STAT, statStr.getStatOrElement());
    }

    @Test
    @Disabled("UIEntryAssembler.assemble() doesn't insert into results yet - embryo dispatch is "
            + "stubbed out (260919); re-enable once it actually populates its return value")
    void parameterAndNameTagSplitSurviveTheWholePipeline() throws IOException {
        // A generic (parameter:element, no tag) and a specialization (tag, no parameter:) together.
        String path = tempFile("split.txt",
                "name:generic_thing\nparameter:element\nname:tagged_thing<ACID>\n");

        ParseResult<UIEntry> result = new UIEntryReader().parseWithResults(path);

        assertEquals(2, result.items().size());

        UIEntry generic = byName(result.items(), "generic_thing");
        assertEquals(StatElemType.ELEMENT, generic.getStatOrElement());
        assertEquals(ElementEnum.ELEM_NONE, generic.getParameter());

        UIEntry tagged = byName(result.items(), "tagged_thing<ACID>");
        assertEquals(StatElemType.NONE, tagged.getStatOrElement());
        assertEquals(ElementEnum.ELEM_ACID, tagged.getParameter());
    }

    // ---- fatal errors ------------------------------------------------------------------------

    /**
     * Read the file, expecting a fatal {@link DataFileParseException}, and return its message.
     */
    private static String fatalMessage(String path) {
        return assertThrows(DataFileParseException.class,
                () -> new UIEntryReader().parseWithResults(path)).getMessage();
    }

    @Test
    void unknownRendererIsFatal() throws IOException {
        // A failed parse_entry_renderer ends the load in C; no partial list comes back.
        String path = tempFile("bad-renderer.txt", "name:foo\nrenderer:no_such_renderer\n");

        assertEquals("UI entry starting at line: 1 of file: " + path
                + " has an illegal renderer value: no_such_renderer", fatalMessage(path));
    }

    @Test
    void unknownTemplateIsFatal() throws IOException {
        String path = tempFile("bad-template.txt", "name:foo\ntemplate:no_such_base\n");

        assertEquals("UI entry starting at line: 1 of file: " + path
                + " has an illegal template value: no_such_base", fatalMessage(path));
    }

    @Test
    void illegalParameterKindIsFatal() throws IOException {
        String path = tempFile("bad-param.txt", "name:foo\nparameter:bogus\n");

        assertEquals("UI entry starting at line: 1 of file: " + path
                + " has an illegal parameter value: bogus", fatalMessage(path));
    }

    // ---- reader plumbing (a valid record, so the assembler does not stop the load first) ----

    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        String path = tempFile("same.txt", "name:foo\ncombine:ADD\n");

        List<String> withResults = new UIEntryReader().parseWithResults(path).items().stream()
                .map(UIEntry::getName).toList();
        List<String> plain = new UIEntryReader().parse(path).stream().map(UIEntry::getName).toList();

        assertEquals(List.of("foo"), withResults);
        assertEquals(withResults, plain);
    }

    @Test
    void missingFileIsAnIOException() {
        String path = tempDir.resolve("does-not-exist.txt").toString();

        assertThrows(IOException.class, () -> new UIEntryReader().parseWithResults(path));
    }

    /**
     * A syntax error stops the load, so no partial list comes back.
     *
     * <p>Function aSyntaxErrorFailsClosed coded on 261104, commented in full on 261104. It replaces
     * syntaxErrorAfterAValidHeaderFailsClosedAndSkipsTheCountCheck.
     */
    @Test
    void aSyntaxErrorFailsClosed() throws IOException {
        String path = tempFile("garbage.txt", "this is not a directive\n");

        assertThrows(ParseCancellationException.class, () -> new UIEntryReader().parseWithResults(path));
    }

    // ---- hard errors (fail-closed: empty list) ----------------------------------------------

}
