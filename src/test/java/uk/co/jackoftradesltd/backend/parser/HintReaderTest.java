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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.Hint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link HintReader}: file text -&gt; {@code HintLexer}/
 * {@code HintGrammar} -&gt; {@link uk.co.jackoftradesltd.backend.parser.hint.HintParseRecord} -&gt;
 * {@code HintAssembler} -&gt; {@link Hint}.
 *
 * <p>Hints carry no cross-references (each is a bare line of text), so unlike the monster-dependent
 * readers this suite needs no registry bootstrap - it parses the real file and small {@link TempDir}
 * fixtures directly.
 *
 * <p>Test class HintReaderTest coded before 261105, updated on 261105 for the removal of the
 * soft-error path.
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


    private ParseResult<Hint> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new HintReader().parseWithResults(file.toString());
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllHintsWithNoErrors() throws IOException {
        ParseResult<Hint> result = new HintReader().parseWithResults(REAL_FILE);

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
        ParseResult<Hint> result = load("punct.txt", "H:" + text + "\n");

        assertEquals(1, result.items().size());
        assertEquals(text, result.items().get(0).getHint());
    }

    // ---- Hard error (fail-closed) ----------------------------------------

    // ---- The shipped file against the upstream one -----------------------

    /**
     * Every {@code H:} line of the shipped file comes back, in file order, as the text after the
     * {@code H:}. The expected side is a plain line split of the file, not the reader's output. C
     * would hand the list over reversed, but nothing can tell: {@code random_hint} in
     * {@code ui-store.c} treats every position alike.
     */
    @Test
    void everyShippedHintComesBackInFileOrder() throws IOException {
        List<String> expected = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("H:")) {
                expected.add(line.substring(2));
            }
        }
        List<Hint> hints = new HintReader().parse(REAL_FILE);

        assertEquals(EXPECTED_HINTS, expected.size());
        assertEquals(expected.size(), hints.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), hints.get(i).getHint(), "hint " + i);
        }
    }

    /**
     * Spot values read off the upstream {@code lib/gamedata/hints.txt}: the first, a middle one and
     * the last, and the five whose upstream line ends in a space. The shipped file has that space
     * removed, so these load without it; the upstream file would load with it (see
     * {@link #trailingSpacesAreKeptAsCDoes}).
     */
    @Test
    void spotValuesAgreeWithTheUpstreamFile() throws IOException {
        List<Hint> hints = new HintReader().parse(REAL_FILE);

        assertEquals("Cure Critical Wounds potions will cure blindness and confusion; carry lots.",
                hints.get(0).getHint());
        assertEquals("Potions of Heroism or Berserk Strength will protect you from being scared.",
                hints.get(2).getHint());
        assertEquals("Never underestimate the value of constitution. Hitpoints keep you alive.",
                hints.get(10).getHint());
        assertEquals("You can never teleport into a vault, even if you're already standing in it.",
                hints.get(22).getHint());
        assertEquals("+10 speed means you move twice as fast as normal.",
                hints.get(24).getHint());
        assertEquals("If you have more than 40 items in your quiver, it will take up more slots.",
                hints.get(49).getHint());
        assertEquals("You always get the first move when entering a level.",
                hints.get(74).getHint());
        assertEquals("When shapechanged inspect items with I. Resume your shape with m or p.",
                hints.get(98).getHint());
    }

    // ---- The text of a hint ----------------------------------------------

    /**
     * C's {@code parser_parse} gives a {@code str} field the rest of the line untouched
     * ({@code strtok(sp, "")}), so a trailing space belongs to the hint. Five upstream lines end in one.
     */
    @Test
    void trailingSpacesAreKeptAsCDoes() throws IOException {
        ParseResult<Hint> result = load("trailing.txt", "H:Keep alive. \n");

        assertEquals("Keep alive. ", result.items().get(0).getHint());
    }

    /** Spaces straight after {@code H:} are part of the text too, and a line of only spaces is a hint. */
    @Test
    void leadingAndAllSpaceTextIsKept() throws IOException {
        ParseResult<Hint> result = load("spaces.txt", "H: leading\nH:   \n");

        assertEquals(" leading", result.items().get(0).getHint());
        assertEquals("   ", result.items().get(1).getHint());
    }

    /** Only the first {@code :} splits the directive off, so a hint may begin with or contain one. */
    @Test
    void colonsInTheTextSurvive() throws IOException {
        ParseResult<Hint> result = load("colon.txt", "H::a:b\n");

        assertEquals(":a:b", result.items().get(0).getHint());
    }

    @Test
    void nonAsciiTextSurvives() throws IOException {
        ParseResult<Hint> result = load("utf8.txt", "H:caf\u00e9 \u2014 ok\n");

        assertEquals("caf\u00e9 \u2014 ok", result.items().get(0).getHint());
    }

    /** A hint on the last line needs no newline after it. */
    @Test
    void finalHintNeedsNoTrailingNewline() throws IOException {
        ParseResult<Hint> result = load("nofinalnl.txt", "H:last");

        assertEquals("last", result.items().get(0).getHint());
    }

    /** A Windows line ending does not leave a carriage return on the hint. */
    @Test
    void carriageReturnIsNotPartOfTheText() throws IOException {
        ParseResult<Hint> result = load("crlf.txt", "H:abc\r\n");

        assertEquals("abc", result.items().get(0).getHint());
    }

    /** Comment and blank lines between and after hints are skipped, as C skips them. */
    @Test
    void commentsAndBlankLinesAreSkipped() throws IOException {
        ParseResult<Hint> result = load("comments.txt",
                "# a comment\nH:one\n\n# another\nH:two\n# last\n");

        assertEquals(List.of("one", "two"),
                result.items().stream().map(Hint::getHint).toList());
    }

    @Test
    void hintsComeBackInFileOrderWhereCsListIsReversed() throws IOException {
        ParseResult<Hint> result = load("order.txt", "H:a\nH:b\nH:c\n");

        assertEquals(List.of("a", "b", "c"),
                result.items().stream().map(Hint::getHint).toList());
    }

    // ---- Counts ----------------------------------------------------------

    /** C accepts a file with no {@code H:} line (and leaves {@code hints} NULL); here it is an empty list. */
    @Test
    void aFileWithNoHintsLoadsEmptyWhenTheHeaderSaysZero() throws IOException {
        ParseResult<Hint> result = load("zero.txt", "# nothing here\n");

        assertTrue(result.items().isEmpty());
    }

    // ---- Hard errors (fail-closed) ---------------------------------------

    /** C needs text after {@code H:} (MISSING_FIELD otherwise); the lexer cannot produce a STRING for it. */
    @Test
    void anHLineWithNoTextIsAHardError() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("emptyh.txt", "H:\n"));

    }

    /** C only has the {@code H:} directive, and its directive names are case-sensitive. */
    @Test
    void anUnknownDirectiveIsAHardError() throws IOException {
        for (String bad : List.of("X:abc\n", "h:abc\n")) {
            assertThrows(ParseCancellationException.class, () -> load("bad.txt", bad), bad);

        }
    }

    /**
     * C skips leading whitespace on a line before it reads the directive, so an indented
     * {@code H:}, an indented comment, a tab-only line and a comment on the last line with no
     * newline are all fine to it. The lexer now takes all four.
     *
     * <p>Function leadingWhitespaceBeforeADirectiveLoadsAsInC coded on 261109, commented in full on
     * 261109. It replaces leadingWhitespaceBeforeADirectiveIsAHardErrorWhereCSkipsIt.
     */
    @Test
    void leadingWhitespaceBeforeADirectiveLoadsAsInC() throws IOException {
        for (String ok : List.of("  H:indented\n", "  # indented comment\nH:a\n", "\t\nH:a\n",
                "H:a\n# no newline")) {
            assertEquals(1, load("ws.txt", ok).items().size(), ok);
        }
    }

    // ---- File access -----------------------------------------------------

    /** C's {@code parse_file_quit_not_found} quits for a missing file; here it is an {@link IOException}. */
    @Test
    void aMissingFileIsAnIOException() {
        String missing = tempDir.resolve("nope.txt").toString();

        assertThrows(IOException.class, () -> new HintReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new HintReader().parse(missing));
    }

    @Test
    void parseReturnsTheSameHintsAsParseWithResults() throws IOException {
        List<Hint> plain = new HintReader().parse(REAL_FILE);
        List<Hint> full = new HintReader().parseWithResults(REAL_FILE).items();

        assertEquals(full.size(), plain.size());
        for (int i = 0; i < full.size(); i++) {
            assertEquals(full.get(i).getHint(), plain.get(i).getHint());
        }
    }
}
