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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the migrated UIEntryRenderer pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryRendererReader} → {@link uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererAssembler}
 * → resolved {@link UIEntryRenderer} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>These drive real fixtures through the whole chain, so they exercise both of the reader's error
 * channels:
 * <ul>
 *   <li><b>hard</b> grammar/lex errors, caught by {@code ParseErrors} - fail-closed: empty list +
 *       collected errors (missing {@code record-count} header, a record missing a mandatory field);</li>
 *   <li><b>soft</b> interpretation/validation errors, appended to the returned list while partial
 *       results survive (unknown {@code code}, invalid {@code sign}, {@code record-count} mismatch).</li>
 * </ul>
 * The final test injects several distinct failures at once to confirm every one is logged
 * independently and the valid records still come through.
 *
 * @author ClaudeCode
 */
class UIEntryRendererReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ui_entry_renderer.txt";

    /**
     * A code whose value/label/symbol/digit/sign defaults are all distinct.
     */
    private static final UIEntryRendererEnum FLAG =
            UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX;

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- happy path -------------------------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileResolvesAllRenderers() throws IOException {
        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(5, result.items().size());

        // First renderer omits ndigit/sign -> both default from the resolved code.
        UIEntryRendererEnum firstCode = UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX;
        UIEntryRenderer first = result.items().get(0);
        assertEquals("char_screen1_resist_renderer", first.getName());
        assertEquals(firstCode, first.getCode());
        assertEquals("wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW", first.getColours());
        assertEquals(firstCode.getDefaultDigits(), first.getnDigit());
        assertEquals(firstCode.getEntry(), first.getSign());

        // Last renderer is the only one that sets ndigit and sign explicitly.
        UIEntryRenderer last = result.items().get(4);
        assertEquals("char_screen1_stat_mod_renderer", last.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, last.getCode());
        assertEquals(1, last.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, last.getSign());
    }

    @Test
    void omittedColourFieldsDefaultThroughTheWholePipeline() throws IOException {
        // Only the two mandatory fields; the loosened grammar accepts this, and every optional
        // field must fall back to the resolved code's backend default (decision (b), end-to-end).
        String path = tempFile("defaults.txt",
                "record-count:1\nname:only_mandatory\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        UIEntryRenderer u = result.items().get(0);
        assertEquals(FLAG, u.getCode());
        assertEquals(FLAG.getDefaultColours(), u.getColours());
        assertEquals(FLAG.getDefaultLabelColours(), u.getLabelColours());
        assertEquals(FLAG.getDefaultSymbols(), u.getSymbols());
        assertEquals(FLAG.getDefaultDigits(), u.getnDigit());
        assertEquals(FLAG.getEntry(), u.getSign());
    }

    // ---- soft errors (partial results survive) ----------------------------------------------

    @Test
    void unknownCodeIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-code.txt",
                "record-count:1\nname:x\ncode:NOTACODE\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("illegal code enum value") && e.contains("NOTACODE")),
                result.errors()::toString);
    }

    @Test
    void invalidSignIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-sign.txt",
                "record-count:1\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\nsign:BOGUS\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("illegal sign enum value") && e.contains("BOGUS")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsLoggedButValidRecordSurvives() throws IOException {
        // Header over-declares (5) vs one actual record; the record itself is valid and comes through.
        String path = tempFile("bad-count.txt",
                "record-count:5\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- hard errors (fail-closed: empty list) ----------------------------------------------

    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        // No record-count directive -> the file rule can't match -> grammar error via ParseErrors.
        String path = tempFile("no-header.txt",
                "name:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordMissingMandatoryCodeFailsClosed() throws IOException {
        // 'code' is mandatory; a record with only 'name' is a grammar error, not a soft one.
        String path = tempFile("no-code.txt",
                "record-count:1\nname:x\ncolors:w\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- several distinct failures at once ---------------------------------------------------

    @Test
    void multipleDistinctFailuresAreEachLoggedAndGoodRecordsSurvive() throws IOException {
        // Four records: valid, unknown-code, invalid-sign, valid. record-count matches the raw
        // count (4), so the only errors are the two semantic ones - and both valid records survive.
        String path = tempFile("mixed.txt", String.join("\n",
                "record-count:4",
                "name:good_first",
                "code:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX",
                "name:bad_code",
                "code:NOTACODE",
                "name:bad_sign",
                "code:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX",
                "sign:BOGUS",
                "name:good_last",
                "code:NUMERIC_RENDERER_WITH_BOOL_AUX",
                ""));

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        // Two valid records survive, in order.
        List<UIEntryRenderer> items = result.items();
        assertEquals(2, items.size(), items::toString);
        assertEquals("good_first", items.get(0).getName());
        assertEquals("good_last", items.get(1).getName());

        // Exactly the two semantic errors are logged - no spurious count mismatch, one per failure.
        List<String> errors = result.errors();
        assertEquals(2, errors.size(), errors::toString);
        assertTrue(errors.stream()
                        .anyMatch(e -> e.contains("illegal code enum value") && e.contains("NOTACODE")),
                errors::toString);
        assertTrue(errors.stream()
                        .anyMatch(e -> e.contains("illegal sign enum value") && e.contains("BOGUS")),
                errors::toString);
    }
}
