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
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the migrated UIEntryBase pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryBaseReader} →
 * {@link uk.co.jackoftrades.backend.parser.uientrybase.UIEntryBaseAssembler} → resolved
 * {@link UIEntryBase} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>These drive real fixtures through the whole chain and exercise both error channels:
 * <ul>
 *   <li><b>hard</b> grammar/lex errors, caught by {@code ParseErrors} - fail-closed: empty list +
 *       collected errors (missing {@code record-count} header, a record missing a mandatory field);</li>
 *   <li><b>soft</b> interpretation/validation errors, appended while partial results survive
 *       (unknown {@code renderer}, {@code record-count} mismatch).</li>
 * </ul>
 * The renderer registry the assembler resolves against is seeded via reflection (see
 * {@link UIEntryBaseAssemblerTest} for the same rationale).
 *
 * @author Rowan Crowther
 */
class UIEntryBaseReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ui_entry_base.txt";

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seedRenderers() throws Exception {
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        Field field = GameConstants.class.getDeclaredField("uiEntryRenderers");
        field.setAccessible(true);
        field.set(null, renderers);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    /**
     * A full, well-formed record with a renderer known to the real registry.
     */
    private static String record(String name, String renderer, String combine) {
        return "name:" + name + "\nrenderer:" + renderer + "\ncombine:" + combine
                + "\ncategory:CHAR_SCREEN1\nflags:TIMED_AS_AUX\ndesc:hello\n";
    }

    // ---- happy path -------------------------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileResolvesAllTemplates() throws IOException {
        ParseResult<UIEntryBase> result = new UIEntryBaseReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(3, result.items().size());

        UIEntryBase first = result.items().get(0);
        assertEquals("good_flag_ui_compact_0", first.getName());
        assertTrue(first.toString().contains("combine=LOGICAL_OR"), first::toString);
        assertTrue(first.toString().contains("char_screen1_flag_renderer"), first::toString);

        UIEntryBase last = result.items().get(2);
        assertEquals("numeric_as_sign_ui_0", last.getName());
        assertTrue(last.toString().contains("combine=ADD"), last::toString);
    }

    // ---- soft errors (partial results survive) ----------------------------------------------

    @Test
    void recordCountMismatchIsLoggedButValidRecordSurvives() throws IOException {
        // Header over-declares (5) vs one actual record; the record itself is valid and comes through.
        String path = tempFile("bad-count.txt",
                "record-count:5\n" + record("x", "char_screen1_flag_renderer", "LOGICAL_OR"));

        ParseResult<UIEntryBase> result = new UIEntryBaseReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void unknownRendererIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-renderer.txt",
                "record-count:1\n" + record("x", "no_such_renderer", "LOGICAL_OR"));

        ParseResult<UIEntryBase> result = new UIEntryBaseReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("no_such_renderer")),
                result.errors()::toString);
    }

    // ---- hard errors (fail-closed: empty list) ----------------------------------------------

    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        // No record-count directive -> the file rule can't match -> grammar error via ParseErrors.
        String path = tempFile("no-header.txt", record("x", "char_screen1_flag_renderer", "LOGICAL_OR"));

        ParseResult<UIEntryBase> result = new UIEntryBaseReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordMissingMandatoryDescFailsClosed() throws IOException {
        // desc: is mandatory; a record that stops after flags cannot complete the fixed sequence.
        String path = tempFile("no-desc.txt",
                "record-count:1\nname:x\nrenderer:char_screen1_flag_renderer\ncombine:LOGICAL_OR\n"
                        + "category:CHAR_SCREEN1\nflags:TIMED_AS_AUX\n");

        ParseResult<UIEntryBase> result = new UIEntryBaseReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- several distinct failures at once ---------------------------------------------------

    @Test
    void multipleDistinctFailuresAreLoggedAndGoodRecordsSurvive() throws IOException {
        // Four records: valid, unknown-renderer, invalid-combine, valid. record-count matches the raw
        // count (4), so the only errors are the two semantic ones - both valid records survive in order.
        String path = tempFile("mixed.txt", "record-count:4\n"
                + record("good_first", "char_screen1_flag_renderer", "LOGICAL_OR")
                + record("bad_renderer", "no_such_renderer", "LOGICAL_OR")
                + record("bad_combine", "char_screen1_flag_renderer", "BOGUS")
                + record("good_last", "char_screen1_numeric_as_sign_renderer", "ADD"));

        ParseResult<UIEntryBase> result = new UIEntryBaseReader().parseWithResults(path);

        List<UIEntryBase> items = result.items();
        assertEquals(2, items.size(), items::toString);
        assertEquals("good_first", items.get(0).getName());
        assertEquals("good_last", items.get(1).getName());

        assertTrue(result.errors().stream().anyMatch(e -> e.contains("no_such_renderer")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("Illegal combine name")),
                result.errors()::toString);
    }
}
