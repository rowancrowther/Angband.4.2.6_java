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
import uk.co.jackoftrades.backend.parser.names.NamesAssembler;
import uk.co.jackoftrades.backend.parser.names.NamesParseRecord;
import uk.co.jackoftrades.middle.game.Name;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link NamesReader}: file text -&gt; {@code NamesLexer}/
 * {@code NamesGrammar} -&gt; {@link NamesParseRecord} -&gt; {@link NamesAssembler} -&gt; {@link Name}.
 *
 * <p>{@code names.txt} is the random-name source: a {@code record-count} header followed by two
 * {@code section:} blocks (1 = Tolkien-ish names, 2 = scroll-title syllables), each block a run of
 * {@code word:} lines. A record here is therefore <em>one whole section plus its word list</em>, not
 * a single word - so the happy-path assertions check both the section count and the per-section word
 * totals. Names carry no cross-references, so like {@code HintReaderTest} this suite needs no registry
 * bootstrap and drives the real file and small {@link TempDir} fixtures directly.
 *
 * @author Rowan Crowther
 */
class NamesReaderTest {

    private static final String REAL_FILE = "lib/gamedata/names.txt";

    /**
     * names.txt's {@code record-count:} header, and the number of {@code section:} blocks in it.
     */
    private static final int EXPECTED_SECTIONS = 2;

    /**
     * Word totals per section, counted from the real file.
     */
    private static final int TOLKIEN_WORDS = 601;
    private static final int SCROLL_WORDS = 587;

    @TempDir
    Path tempDir;

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n\n" + body;
    }

    private ParseResult<Name> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new NamesReader().parseWithResults(file.toString());
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsBothSectionsWithNoErrors() throws IOException {
        ParseResult<Name> result = new NamesReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(EXPECTED_SECTIONS, result.items().size());
    }

    /**
     * Sections are stored in file order and their numeric labels resolve to the {@code int}s 1 and 2;
     * each carries the full run of {@code word:} lines that followed it.
     */
    @Test
    void sectionsResolveToIntsWithCorrectWordCounts() throws IOException {
        List<Name> names = new NamesReader().parseWithResults(REAL_FILE).items();

        Name tolkien = names.get(0);
        Name scroll = names.get(1);

        assertEquals(1, tolkien.getSection());
        assertEquals(2, scroll.getSection());
        assertEquals(TOLKIEN_WORDS, tolkien.getWord().size());
        assertEquals(SCROLL_WORDS, scroll.getWord().size());
    }

    /**
     * The word list is a plain ordered run - first and last entries of each section survive verbatim,
     * confirming nothing is dropped, sorted, or de-duplicated on the way through.
     */
    @Test
    void firstAndLastWordsOfEachSectionSurviveInOrder() throws IOException {
        List<Name> names = new NamesReader().parseWithResults(REAL_FILE).items();

        List<String> tolkien = names.get(0).getWord();
        List<String> scroll = names.get(1).getWord();

        assertEquals("adanedhel", tolkien.get(0));
        assertEquals("yavanna", tolkien.get(tolkien.size() - 1));
        assertEquals("abracadabra", scroll.get(0));
        assertEquals("vultus", scroll.get(scroll.size() - 1));
    }

    /**
     * The domain object hands back a read-only view of its words.
     */
    @Test
    void wordListIsUnmodifiable() throws IOException {
        Name first = new NamesReader().parseWithResults(REAL_FILE).items().get(0);

        assertThrows(UnsupportedOperationException.class, () -> first.getWord().add("intruder"));
    }

    // ---- Happy path: small fixtures --------------------------------------

    /**
     * A minimal two-section fixture: each section keeps exactly its own words, in order, and the
     * boundary between sections falls where the next {@code section:} line begins.
     */
    @Test
    void multipleSectionsPartitionTheirWordsOnSectionBoundaries() throws IOException {
        ParseResult<Name> result = load("two.txt", withHeader(2,
                "section:1\nword:alpha\nword:beta\n\nsection:2\nword:gamma\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(List.of("alpha", "beta"), result.items().get(0).getWord());
        assertEquals(List.of("gamma"), result.items().get(1).getWord());
    }

    /**
     * A {@code word:} value is free text grabbed to end of line (the lexer's {@code STRING_MODE}), so
     * capitals, apostrophes, hyphens and internal spaces must all pass through untouched - the code
     * derives its letter-frequency tables from these exact strings.
     */
    @Test
    void wordValuePreservesCaseAndPunctuationVerbatim() throws IOException {
        String odd = "Gil-galad's Host";
        ParseResult<Name> result = load("odd.txt", withHeader(1, "section:1\nword:" + odd + "\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(odd, result.items().get(0).getWord().get(0));
    }

    /**
     * A negative section label is a valid {@code INTEGER} and round-trips as a negative {@code int}.
     */
    @Test
    void negativeSectionLabelRoundTrips() throws IOException {
        ParseResult<Name> result = load("neg.txt", withHeader(1, "section:-3\nword:x\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-3, result.items().get(0).getSection());
    }

    // ---- Soft error ------------------------------------------------------

    @Test
    void recordCountMismatchIsASoftError() throws IOException {
        // Header claims five sections, body holds one; the section still loads (partial-results contract).
        ParseResult<Name> result = load("miscount.txt",
                withHeader(5, "section:1\nword:only\n"));

        assertEquals(1, result.items().size());
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
        ParseResult<Name> result = load("noheader.txt", "section:1\nword:orphan\n");

        assertTrue(result.items().isEmpty(), "a hard grammar error must fail closed");
        assertTrue(result.hasErrors());
    }

    /**
     * The {@code section} rule requires an {@code INTEGER}, so a non-numeric section label is a hard
     * lexer/parse error - it never reaches the assembler as a {@link NumberFormatException}.
     */
    @Test
    void nonNumericSectionLabelIsAHardErrorYieldingEmptyResult() throws IOException {
        ParseResult<Name> result = load("badsection.txt",
                withHeader(1, "section:tolkien\nword:x\n"));

        assertTrue(result.items().isEmpty(), "a hard grammar error must fail closed");
        assertTrue(result.hasErrors());
    }

    // ---- Assembler unit: the defensive parseInt branch -------------------

    /**
     * Exercised directly because the grammar can't feed a non-numeric section through
     * (see {@link #nonNumericSectionLabelIsAHardErrorYieldingEmptyResult}): a {@link NamesParseRecord}
     * with a bad section string is dropped with a soft error rather than aborting the batch, so a
     * following well-formed record still assembles.
     */
    @Test
    void assemblerDropsRecordWithUnparseableSectionButKeepsTheRest() {
        List<NamesParseRecord> records = List.of(
                new NamesParseRecord("NaN", List.of("skipme")),
                new NamesParseRecord("2", List.of("keepme")));
        List<String> errors = new ArrayList<>();

        List<Name> names = new NamesAssembler().assemble(records, errors);

        assertEquals(1, names.size());
        assertEquals(2, names.get(0).getSection());
        assertEquals(List.of("keepme"), names.get(0).getWord());
        assertTrue(errors.stream().anyMatch(e -> e.contains("invalid integer")),
                () -> errors.toString());
    }
}
