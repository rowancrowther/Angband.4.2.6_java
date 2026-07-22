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
import uk.co.jackoftrades.middle.game.Hint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link HintReader}: file text -&gt; {@code HintLexer}/
 * {@code HintGrammar} -&gt; {@link uk.co.jackoftrades.backend.parser.hint.HintParseRecord} -&gt;
 * {@code HintAssembler} -&gt; {@link Hint}.
 *
 * <p>Hints carry no cross-references (each is a bare line of text), so unlike the monster-dependent
 * readers this suite needs no registry bootstrap - it parses the real file and small {@link TempDir}
 * fixtures directly.
 *
 * @author Rowan Crowther
 */
class HintReaderTest {

    private static final String REAL_FILE = "lib/gamedata/hints.txt";

    /**
     * hints.txt's {@code record-count:} header, and the number of {@code H:} records in it.
     */
    private static final int EXPECTED_HINTS = 99;

    @TempDir
    Path tempDir;

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n\n" + body;
    }

    private ParseResult<Hint> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new HintReader().parseWithResults(file.toString());
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllHintsWithNoErrors() throws IOException {
        ParseResult<Hint> result = new HintReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(EXPECTED_HINTS, result.items().size());
    }

    @Test
    void firstAndLastHintTextResolveVerbatim() throws IOException {
        List<Hint> hints = new HintReader().parseWithResults(REAL_FILE).items();

        assertEquals("Cure Critical Wounds potions will cure blindness and confusion; carry lots.",
                hints.get(0).getHint());
        assertEquals("When shapechanged inspect items with I. Resume your shape with m or p.",
                hints.get(hints.size() - 1).getHint());
    }

    /**
     * The hint text is a free-text {@code STRING} to end of line, so punctuation that means something
     * to other grammars - quotes, braces, colons, {@code @} - must survive verbatim (many real hints
     * quote inscriptions like {@code "!s"} or {@code "@v0"}).
     */
    @Test
    void hintTextPreservesPunctuationVerbatim() throws IOException {
        String text = "Inscribe an item with \"!s\", use '{', or try \"@v0\" - all pass through.";
        ParseResult<Hint> result = load("punct.txt", withHeader(1, "H:" + text + "\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(text, result.items().get(0).getHint());
    }

    // ---- Soft error ------------------------------------------------------

    @Test
    void recordCountMismatchIsASoftError() throws IOException {
        // Header claims five, body holds two; both still load (partial-results contract).
        ParseResult<Hint> result = load("miscount.txt",
                withHeader(5, "H:First hint.\nH:Second hint.\n"));

        assertEquals(2, result.items().size());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("record-count header declares")),
                () -> result.errors().toString());
    }

    // ---- Hard error (fail-closed) ----------------------------------------

    /**
     * The {@code file} rule requires the {@code record-count} header, so a file without it is a hard
     * grammar error: {@code throwIfAny()} fires and the driver returns an empty result carrying the
     * collected errors rather than a partial load.
     */
    @Test
    void missingRecordCountHeaderIsAHardErrorYieldingEmptyResult() throws IOException {
        ParseResult<Hint> result = load("noheader.txt", "H:A lonely headerless hint.\n");

        assertTrue(result.items().isEmpty(), "a hard grammar error must fail closed");
        assertTrue(result.hasErrors());
    }
}
