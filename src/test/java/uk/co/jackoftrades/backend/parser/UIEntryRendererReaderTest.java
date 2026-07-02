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
import uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererParseRecord;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link UIEntryRendererReader} (grammar-suite reader track R4).
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/ui_entry_renderer.txt}; a
 * clean load is itself the assertion that all 5 renderers resolved (every code/sign). It also spot-
 * checks the two optional fields both ways: the four renderers with no {@code ndigit}/{@code sign}
 * default to {@code 1} / {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT}, while the last sets them
 * explicitly. The error-path tests build minimal fixtures injecting a single defect each; the reader
 * is fail-closed, so any error yields an empty item list plus the collected errors.
 *
 * @author ClaudeCode
 */
class UIEntryRendererReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ui_entry_renderer.txt";

    /**
     * A minimal, valid renderer record: the five mandatory fields, a real code, no ndigit/sign.
     */
    private static final String ONE_RECORD =
            "name:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\ncolors:w\nlabelcolors:s\nsymbols:?\n";

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllRenderers() throws IOException {
        ParseResult<UIEntryRendererParseRecord> result = new UIEntryRendererReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(5, result.items().size());

        // First renderer: code resolves (FLAG), colours/symbols verbatim, optional ndigit/sign default.
        UIEntryRendererParseRecord first = result.items().get(0);
        assertEquals("char_screen1_resist_renderer", first.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX, first.getCode());
        assertEquals("wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW", first.getColours());
        assertEquals("swBrgwBrwBwBr", first.getLabelColours());
        assertEquals("?..+-*!^.=.%%%~!=%~+=~", first.getSymbols());
        assertEquals(1, first.getnDigits());                              // ndigit absent -> default 1
        assertEquals(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT, first.getSign()); // sign absent -> default

        // Last renderer: the only one that sets ndigit and sign explicitly; symbols has a space.
        UIEntryRendererParseRecord last = result.items().get(4);
        assertEquals("char_screen1_stat_mod_renderer", last.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, last.getCode());
        assertEquals("? .s*=", last.getSymbols());
        assertEquals(1, last.getnDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, last.getSign());       // sign:NO_SIGN resolved
    }

    @Test
    void recordCountMismatchIsReportedWithNoItems() throws IOException {
        // Header over-declares: 5 vs the single record present.
        String path = tempFile("bad-count.txt", "record-count:5\n" + ONE_RECORD);

        ParseResult<UIEntryRendererParseRecord> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void unknownCodeIsReportedWithNoItems() throws IOException {
        // NOTACODE has no UI_ENTRY_RENDERER_ constant.
        String path = tempFile("bad-code.txt",
                "record-count:1\nname:x\ncode:NOTACODE\ncolors:w\nlabelcolors:s\nsymbols:?\n");

        ParseResult<UIEntryRendererParseRecord> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("NOTACODE")), result.errors()::toString);
    }

    @Test
    void unknownSignIsReportedAsASignErrorWithNoItems() throws IOException {
        // BOGUS lexes as a valid FLAG but is not a UIEntryEnum sign; must be reported as a sign error
        // (not a code error) - the sign resolution has its own labelled catch.
        String path = tempFile("bad-sign.txt",
                "record-count:1\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n"
                        + "colors:w\nlabelcolors:s\nsymbols:?\nsign:BOGUS\n");

        ParseResult<UIEntryRendererParseRecord> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("sign") && e.contains("BOGUS")),
                result.errors()::toString);
    }

    @Test
    void missingRecordCountHeaderIsReportedWithNoItems() throws IOException {
        // No record-count directive: a grammar syntax error surfaced through ParseErrors.
        String path = tempFile("no-header.txt", ONE_RECORD);

        ParseResult<UIEntryRendererParseRecord> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }
}
