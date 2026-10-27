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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.names.NamesAssembler;
import uk.co.jackoftradesltd.backend.parser.names.NamesParseRecord;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.Name;

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

    // ---- The shipped file against the upstream one -----------------------

    /**
     * The upstream {@code lib/gamedata/names.txt} puts 601 words under {@code section:1} (file lines
     * 63-663) and 587 under {@code section:2} (lines 667-1253); the shipped copy adds only the
     * {@code record-count:2} header and strips trailing spaces from comments. The expected side is a
     * plain line split of the file, not the reader's output.
     */
    @Test
    void everyShippedWordComesBackUnderItsSectionInFileOrder() throws IOException {
        List<List<String>> expected = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("section:")) {
                expected.add(new ArrayList<>());
            } else if (line.startsWith("word:")) {
                expected.get(expected.size() - 1).add(line.substring(5));
            }
        }
        List<Name> names = new NamesReader().parse(REAL_FILE);

        assertEquals(EXPECTED_SECTIONS, expected.size());
        assertEquals(EXPECTED_SECTIONS, names.size());
        for (int s = 0; s < expected.size(); s++) {
            assertEquals(expected.get(s), names.get(s).getWord(), "section " + (s + 1));
        }
        assertEquals(TOLKIEN_WORDS + SCROLL_WORDS,
                names.stream().mapToInt(n -> n.getWord().size()).sum());
    }

    /** Spot values read off the upstream file: the first, a middle and the last word of each section. */
    @Test
    void spotValuesAgreeWithTheUpstreamFile() throws IOException {
        List<Name> names = new NamesReader().parse(REAL_FILE);

        assertEquals("adanedhel", names.get(0).getWord().get(0));
        assertEquals("yavanna", names.get(0).getWord().get(600));
        assertEquals("abracadabra", names.get(1).getWord().get(0));
        assertEquals("vultus", names.get(1).getWord().get(586));
    }

    // ---- Sections --------------------------------------------------------

    /**
     * C keeps one word list per section number, so a repeated {@code section:1} adds to the first. The
     * reader cannot join them: it returns one {@link Name} per block and leaves the joining to
     * {@code MiscRegistry.setNames}.
     */
    @Test
    void aRepeatedSectionGivesTwoRecordsWhereCJoinsThemIntoOneList() throws IOException {
        ParseResult<Name> result = load("repeat.txt", withHeader(3,
                "section:1\nword:a\nsection:2\nword:b\nsection:1\nword:c\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(3, result.items().size());
        assertEquals(1, result.items().get(0).getSection());
        assertEquals(2, result.items().get(1).getSection());
        assertEquals(1, result.items().get(2).getSection());
        assertEquals(List.of("c"), result.items().get(2).getWord());
    }

    /** A section with no {@code word:} lines is a valid, empty record; the next block starts a new one. */
    @Test
    void aSectionWithNoWordsIsAnEmptyRecord() throws IOException {
        ParseResult<Name> result = load("empty-section.txt", withHeader(2,
                "section:1\nsection:2\nword:a\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertTrue(result.items().get(0).getWord().isEmpty());
        assertEquals(List.of("a"), result.items().get(1).getWord());
    }

    /**
     * {@code parse_names_section} in {@code init.c} refuses a section at or above
     * {@code RANDNAME_NUM_TYPES} (3) with {@code PARSE_ERROR_OUT_OF_BOUNDS}, which stops the load.
     * The assembler throws with the {@code section:} line, which is line 3 after the header and
     * blank line.
     */
    @Test
    void aSectionOfThreeStopsTheLoad() {
        assertStopsTheLoad("range.txt", "section:3\nword:c\n", "illegal section value: 3");
    }

    /**
     * Accepted deviation: C accepts section 0 and files its words in a slot nothing reads. The
     * port refuses it at the assembler, so the error carries the line.
     */
    @Test
    void aSectionOfZeroStopsTheLoadWhereCAcceptsIt() {
        assertStopsTheLoad("zero-section.txt", "section:0\nword:a\n", "illegal section value of zero: 0");
    }

    /**
     * C's {@code strtoul} reads base 0, so {@code 02} is 2 and {@code 010} is octal 8, out of
     * bounds.
     */
    @Test
    void aZeroPaddedSectionIsReadInBaseZero() throws IOException {
        ParseResult<Name> result = load("padded.txt", withHeader(1, "section:02\nword:a\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().get(0).getSection());
        assertStopsTheLoad("octal.txt", "section:010\nword:b\n", "illegal section value: 010");
    }

    /**
     * The assembler's refusal is a {@link DataFileParseException} that comes straight out of the
     * reader, naming the {@code section:} line (3, after the header and blank line) and the file.
     */
    private void assertStopsTheLoad(String fileName, String body, String message) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> load(fileName, withHeader(1, body)));
        assertTrue(thrown.getMessage().contains(message), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 3"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(fileName), thrown::getMessage);
    }

    // ---- The text of a word ----------------------------------------------

    /**
     * C's {@code parser_parse} gives a {@code str} field the rest of the line untouched, so spaces
     * before and after the word belong to it.
     */
    @Test
    void leadingAndTrailingSpacesAreKeptAsCDoes() throws IOException {
        ParseResult<Name> result = load("spaces.txt", withHeader(1, "section:1\nword: pad \n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(" pad ", result.items().get(0).getWord().get(0));
    }

    /** Only the first {@code :} splits the directive off, so a word may contain a colon or a hash. */
    @Test
    void colonsAndHashesInTheTextSurvive() throws IOException {
        ParseResult<Name> result = load("colon.txt", withHeader(1, "section:1\nword::a:b#c\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(":a:b#c", result.items().get(0).getWord().get(0));
    }

    /** The same word twice is two entries: the letter counts in {@code build_prob} depend on it. */
    @Test
    void duplicateWordsAreBothKept() throws IOException {
        ParseResult<Name> result = load("dup.txt", withHeader(1, "section:1\nword:ab\nword:ab\n"));

        assertEquals(List.of("ab", "ab"), result.items().get(0).getWord());
    }

    @Test
    void wordsComeBackInFileOrderWhereCsListIsReversed() throws IOException {
        ParseResult<Name> result = load("order.txt", withHeader(1, "section:1\nword:a\nword:b\nword:c\n"));

        assertEquals(List.of("a", "b", "c"), result.items().get(0).getWord());
    }

    /** A final word needs no newline after it, and a Windows line ending leaves no carriage return. */
    @Test
    void finalWordNeedsNoNewlineAndCarriageReturnsAreDropped() throws IOException {
        ParseResult<Name> noNewline = load("nonl.txt", withHeader(1, "section:1\nword:last"));
        ParseResult<Name> crlf = load("crlf.txt", "record-count:1\r\nsection:1\r\nword:abc\r\n");

        assertEquals("last", noNewline.items().get(0).getWord().get(0));
        assertFalse(crlf.hasErrors(), () -> crlf.errors().toString());
        assertEquals("abc", crlf.items().get(0).getWord().get(0));
    }

    /** Comment and blank lines between and after the lines are skipped, as C skips them. */
    @Test
    void commentsAndBlankLinesAreSkipped() throws IOException {
        ParseResult<Name> result = load("comments.txt", withHeader(1,
                "# top\nsection:1\n\n# between\nword:one\n\nword:two\n# last\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("one", "two"), result.items().get(0).getWord());
    }

    // ---- Counts ----------------------------------------------------------

    /** C accepts a file with no sections at all; the header then says zero and the list is empty. */
    @Test
    void aFileWithNoSectionsLoadsEmptyWhenTheHeaderSaysZero() throws IOException {
        ParseResult<Name> result = load("zero.txt", "record-count:0\n# nothing here\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().isEmpty());
    }

    /** The header counts {@code section:} blocks, not words: 2 blocks with 3 words is a count of 2. */
    @Test
    void theHeaderCountsSectionsNotWords() throws IOException {
        assertFalse(load("two.txt", withHeader(2, "section:1\nword:a\nword:b\nsection:2\nword:c\n"))
                .hasErrors());
        assertTrue(load("three.txt", withHeader(3, "section:1\nword:a\nword:b\nsection:2\nword:c\n"))
                .hasErrors());
    }

    @Test
    void aHeaderDeclaringTooFewIsASoftErrorToo() throws IOException {
        ParseResult<Name> result = load("toofew.txt", withHeader(1,
                "section:1\nword:a\nsection:2\nword:b\n"));

        assertEquals(2, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("record-count header declares")),
                () -> result.errors().toString());
    }

    // ---- Hard errors (fail-closed) ---------------------------------------

    /**
     * Where C and the grammar differ, written up in {@code docs/grammar_issues.md}: C keeps a running
     * section that starts at 0, so a {@code word:} before any {@code section:} lands in section 0.
     * The {@code record} rule starts with {@code section}, so the grammar rejects it.
     */
    @Test
    void aWordBeforeAnySectionIsAHardErrorWhereCFilesItUnderSectionZero() throws IOException {
        ParseResult<Name> result = load("orphan.txt", withHeader(1, "word:orphan\nsection:1\nword:a\n"));

        assertTrue(result.items().isEmpty(), "a hard grammar error must fail closed");
        assertTrue(result.hasErrors());
    }

    /**
     * C's {@code section} is a {@code uint}, so {@code parser_parse} rejects a minus sign with
     * {@code PARSE_ERROR_NOT_NUMBER}. The lexer lets {@code -1} through and the assembler refuses it.
     */
    @Test
    void aNegativeSectionStopsTheLoadAsInC() {
        assertStopsTheLoad("neg.txt", "section:-1\nword:a\n", "illegal negative section value: -1");
        assertStopsTheLoad("negzero.txt", "section:-0\nword:a\n", "illegal negative section value: -0");
    }

    /** C needs text after {@code word:} (MISSING_FIELD otherwise); the lexer cannot produce a STRING for it. */
    @Test
    void aWordLineWithNoTextIsAHardError() throws IOException {
        ParseResult<Name> result = load("emptyword.txt", withHeader(1, "section:1\nword:\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aSectionLineWithNoNumberIsAHardError() throws IOException {
        ParseResult<Name> result = load("nonum.txt", withHeader(1, "section:\nword:a\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /** C only has {@code section:} and {@code word:}, and its directive names are case-sensitive. */
    @Test
    void anUnknownDirectiveIsAHardError() throws IOException {
        for (String bad : List.of("name:abc\n", "Word:abc\n", "SECTION:1\n")) {
            ParseResult<Name> result = load("bad.txt", withHeader(1, "section:1\n" + bad));

            assertTrue(result.items().isEmpty(), bad);
            assertTrue(result.hasErrors(), bad);
        }
    }

    @Test
    void anEmptyFileIsAHardErrorForWantOfTheHeader() throws IOException {
        ParseResult<Name> result = load("empty.txt", "");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void theHeaderMustComeFirstAndOnlyOnce() throws IOException {
        for (String bad : List.of("section:1\nrecord-count:1\n",
                "record-count:1\nrecord-count:1\nsection:1\n")) {
            ParseResult<Name> result = load("header.txt", bad);

            assertTrue(result.items().isEmpty(), bad);
            assertTrue(result.hasErrors(), bad);
        }
    }

    /**
     * Where C and the grammar differ, written up in {@code docs/grammar_issues.md}: C skips leading
     * whitespace on a line before it reads the directive, so an indented line, an indented comment
     * and a tab-only line are all fine to it. The lexer takes none of them, and a comment on the
     * last line needs its newline.
     */
    @Test
    void leadingWhitespaceBeforeADirectiveIsAHardErrorWhereCSkipsIt() throws IOException {
        for (String bad : List.of("  word:indented\n", "  # indented comment\n", "\t\n",
                "# no newline")) {
            ParseResult<Name> result = load("ws.txt", withHeader(1, "section:1\n" + bad));

            assertTrue(result.items().isEmpty(), bad);
            assertTrue(result.hasErrors(), bad);
        }
    }

    // ---- File access -----------------------------------------------------

    /** C's {@code parse_file_quit_not_found} quits for a missing file; here it is an {@link IOException}. */
    @Test
    void aMissingFileIsAnIOException() {
        String missing = tempDir.resolve("nope.txt").toString();

        assertThrows(IOException.class, () -> new NamesReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new NamesReader().parse(missing));
    }

    @Test
    void parseReturnsTheSameNamesAsParseWithResults() throws IOException {
        List<Name> plain = new NamesReader().parse(REAL_FILE);
        List<Name> full = new NamesReader().parseWithResults(REAL_FILE).items();

        assertEquals(full.size(), plain.size());
        for (int i = 0; i < full.size(); i++) {
            assertEquals(full.get(i).getSection(), plain.get(i).getSection());
            assertEquals(full.get(i).getWord(), plain.get(i).getWord());
        }
    }
}
