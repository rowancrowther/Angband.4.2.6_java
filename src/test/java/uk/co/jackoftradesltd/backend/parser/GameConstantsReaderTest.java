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

import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link GameConstantsReader} (grammar-suite reader-track R4).
 *
 * <p>Unlike the list readers, this one produces a single {@link GameConstantsData} carrier. As in C,
 * the carrier is not completeness- or duplicate-checked. {@code init_parse_constants()} in
 * {@code init.c} {@code mem_zalloc}s the struct, so a constant no line sets reads {@code 0}, and each
 * {@code parse_constants_*()} handler simply assigns its field, so a later line for the same label
 * overwrites the earlier one with no error. {@link #missingConstantIsLeftAtZero} and
 * {@link #duplicateScalarConstantOverwritesEarlierValue} pin both.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/constants.txt}. The
 * error-path tests start from that same complete file and inject defects on appended lines, so the
 * only errors reported are the ones under test.
 *
 * <p>The injected scalar defects target {@code world:max-depth}, which the real file already sets
 * near the top. A non-integer on a later line is refused before the setter is called, so the earlier
 * value stands and the defect is the only error.
 *
 * <p>Class GameConstantsReaderTest coded before 261002, updated on 261002 once missing constants
 * defaulted to 0 and duplicates overwrote, as in C.
 *
 * @author Rowan Crowther
 */
class GameConstantsReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/constants.txt";

    @TempDir
    Path tempDir;

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    /**
     * Reads the real data file, guaranteeing a trailing newline so an appended line lands on its
     * own line (and so {@link #lineCount} gives the index the appended line will occupy minus one).
     */
    private String realText() throws IOException {
        String base = Files.readString(Path.of(REAL_FILE));
        return base.endsWith("\n") ? base : base + "\n";
    }

    /**
     * The real data file with every line whose start matches {@code linePrefix} removed.
     */
    private String realTextWithout(String linePrefix) throws IOException {
        return Files.readAllLines(Path.of(REAL_FILE)).stream()
                .filter(line -> !line.startsWith(linePrefix))
                .collect(Collectors.joining("\n", "", "\n"));
    }

    /**
     * Number of newline-terminated lines in {@code text}; a line appended after it sits at
     * {@code lineCount(text) + 1}.
     */
    private int lineCount(String text) {
        return (int) text.chars().filter(c -> c == '\n').count();
    }

    @Test
    void cleanLoadReportsNoErrorsAndPopulatesTheCarrier() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());

        GameConstantsData data = result.getData();
        assertNotNull(data);

        // A couple of scalar spot-checks straight from constants.txt.
        assertEquals(1024, data.levelMax().monsters());
        assertEquals(128, data.world().maxDepth());

        // The four critical-level lists keep file order and the counts shipped in the file.
        assertEquals(5, data.meleeCriticalLevel().size());
        assertEquals(3, data.rangedCriticalLevel().size());
        assertEquals(5, data.oMeleeCriticalLevel().size());
        assertEquals(3, data.oRangedCriticalLevel().size());
    }

    /**
     * Two independent defects on appended lines, a non-integer value and an unknown category, are
     * both collected and each is tagged with its own line. Any error at all means no carrier, as C's
     * {@code run_parser()} fails the whole file.
     *
     * <p>Function twoDistinctErrorsAreBothReportedWithTheirLines coded before 261002, updated on
     * 261002 to use a non-integer in place of the duplicate, which is no longer an error.
     */
    @Test
    void twoDistinctErrorsAreBothReportedWithTheirLines() throws IOException {
        String base = realText();
        int badIntLine = lineCount(base) + 1;
        int unknownLine = lineCount(base) + 2;

        String path = tempFile("two-errors.txt", base + "world:max-depth:notanumber\n" + "nonsense:foo:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());

        List<String> errors = result.getErrors();
        assertEquals(2, errors.size(), errors::toString);

        assertTrue(errors.stream().anyMatch(
                        e -> e.contains("Line: " + badIntLine) && e.contains("not an integer")),
                errors::toString);
        assertTrue(errors.stream().anyMatch(
                        e -> e.contains("Line: " + unknownLine) && e.contains("unknown category")),
                errors::toString);
    }

    /**
     * Removing {@code world:max-depth} is not an error. The load is clean and the constant reads
     * {@code 0}, as it would in C's zeroed {@code struct angband_constants}. The rest of the file is
     * unaffected.
     *
     * <p>Function missingConstantIsLeftAtZero coded on 261002, commented in full on 261002.
     */
    @Test
    void missingConstantIsLeftAtZero() throws IOException {
        String path = tempFile("missing.txt", realTextWithout("world:max-depth:"));

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());

        GameConstantsData data = result.getData();
        assertNotNull(data);
        assertEquals(0, data.world().maxDepth());
        assertEquals(1024, data.levelMax().monsters());
    }

    /**
     * A second {@code world:max-depth} line is not an error. The later value wins, because C's
     * {@code parse_constants_world()} simply assigns {@code z->max_depth} again.
     *
     * <p>Function duplicateScalarConstantOverwritesEarlierValue coded on 261002, commented in full on
     * 261002.
     */
    @Test
    void duplicateScalarConstantOverwritesEarlierValue() throws IOException {
        String path = tempFile("duplicate.txt", realText() + "world:max-depth:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());

        GameConstantsData data = result.getData();
        assertNotNull(data);
        assertEquals(1, data.world().maxDepth());
    }

    @Test
    void nonIntegerValueIsReportedWithItsLine() throws IOException {
        // A second world:max-depth carrying a non-numeric value: coercion fails on that line and the
        // null-guard returns before the set, so the earlier value is untouched — exactly one error.
        String base = realText();
        int badLine = lineCount(base) + 1;

        String path = tempFile("bad-int.txt", base + "world:max-depth:notanumber\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());

        List<String> errors = result.getErrors();
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("Line: " + badLine), errors::toString);
        assertTrue(errors.get(0).contains("not an integer"), errors::toString);
    }

    @Test
    void wrongArityCriticalLevelLineIsReportedWithItsLine() throws IOException {
        // melee-critical-level needs four fields (cutoff:mult:add:msg); supply three.
        String base = realText();
        int badLine = lineCount(base) + 1;

        String path = tempFile("bad-arity.txt", base + "melee-critical-level:400:2:5\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());

        List<String> errors = result.getErrors();
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("Line: " + badLine), errors::toString);
        assertTrue(errors.get(0).contains("melee-critical-level"), errors::toString);
    }

    /**
     * An empty file is clean and every constant reads {@code 0}, with empty critical-level lists. C
     * {@code mem_zalloc}s {@code struct angband_constants} in {@code init_parse_constants()} and has no
     * header or completeness check, so an empty {@code constants.txt} loads.
     *
     * <p>Function anEmptyFileLoadsAllZeroes coded on 261019, commented in full on 261019.
     */
    @Test
    void anEmptyFileLoadsAllZeroes() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(tempFile("empty.txt", ""));

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        GameConstantsData data = result.getData();
        assertNotNull(data);
        assertEquals(0, data.levelMax().monsters());
        assertEquals(0, data.world().maxDepth());
        assertTrue(data.meleeCriticalLevel().isEmpty());
        assertTrue(data.rangedCriticalLevel().isEmpty());
        assertTrue(data.oMeleeCriticalLevel().isEmpty());
        assertTrue(data.oRangedCriticalLevel().isEmpty());
    }

    /**
     * A file of only comments and blank lines is the same as an empty one. C's {@code parse_file()}
     * skips both.
     *
     * <p>Function aCommentOnlyFileLoadsAllZeroes coded on 261019, commented in full on 261019.
     */
    @Test
    void aCommentOnlyFileLoadsAllZeroes() throws IOException {
        String path = tempFile("comments.txt", "# File: constants.txt\n\n# nothing here\n\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().levelMax().monsters());
    }

    /**
     * A file that does not exist throws {@link IOException} from both entry points. C's
     * {@code parse_file_quit_not_found()} quits.
     *
     * <p>Function aMissingFileThrowsFromBothEntryPoints coded on 261019, commented in full on 261019.
     */
    @Test
    void aMissingFileThrowsFromBothEntryPoints() {
        String path = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new GameConstantsReader().parseWithResults(path));
        assertThrows(IOException.class, () -> new GameConstantsReader().parse(path));
    }

    /**
     * {@code parse} is the same call as {@code parseWithResults}: same data, same errors.
     *
     * <p>Function parseGivesTheSameResultAsParseWithResults coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void parseGivesTheSameResultAsParseWithResults() throws IOException {
        GameConstantsParseResult viaParse = new GameConstantsReader().parse(REAL_FILE);
        GameConstantsParseResult viaResults = new GameConstantsReader().parseWithResults(REAL_FILE);

        assertFalse(viaParse.hasErrors(), () -> viaParse.getErrors().toString());
        assertEquals(viaResults.getData(), viaParse.getData());
        assertEquals(viaResults.getErrors(), viaParse.getErrors());

        String bad = tempFile("bad.txt", "Level-Max:monsters:1\n");
        GameConstantsParseResult badParse = new GameConstantsReader().parse(bad);
        GameConstantsParseResult badResults = new GameConstantsReader().parseWithResults(bad);
        assertNull(badParse.getData());
        assertEquals(badResults.getErrors(), badParse.getErrors());
    }

    /**
     * A grammar error is hard: no data, the lexer/parser message with the file and line, and nothing
     * from the assembler. An upper-case category cannot be a {@code GC_NAME}, so it is a syntax error
     * here, where C would report an undefined directive. Either way the file is refused.
     *
     * <p>Function aGrammarErrorGivesNoDataAndTheLexerMessage coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void aGrammarErrorGivesNoDataAndTheLexerMessage() throws IOException {
        String path = tempFile("hard.txt", "level-max:monsters:1024\nLEVEL-MAX:monsters:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertTrue(result.getErrors().stream().allMatch(e -> e.contains("line 2")), result.getErrors()::toString);
    }

    /**
     * Soft and hard errors do not mix: a hard error stops before the assembler runs, so a bad
     * category on an earlier line is not reported alongside it.
     *
     * <p>Function aHardErrorStopsTheAssemblerBeforeItReportsSoftOnes coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aHardErrorStopsTheAssemblerBeforeItReportsSoftOnes() throws IOException {
        String path = tempFile("mixed.txt", "nonsense:foo:1\nLEVEL-MAX:monsters:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertNull(result.getData());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().stream().noneMatch(e -> e.contains("unknown category")),
                result.getErrors()::toString);
    }

    /**
     * Melee cutoffs that do not strictly increase refuse the file, as C's
     * {@code finish_parse_constants()} does with {@code PARSE_ERROR_NON_SEQUENTIAL_RECORDS}. C compares
     * every cutoff but the last against the one before: 10, 5, 20 fails on the 5.
     *
     * <p>Function nonIncreasingMeleeCutoffsRefuseTheFile coded on 261019, commented in full on 261019.
     */
    @Test
    void nonIncreasingMeleeCutoffsRefuseTheFile() throws IOException {
        String path = tempFile("melee-order.txt",
                "melee-critical-level:10:2:5:HIT_GOOD\n"
                        + "melee-critical-level:5:3:10:HIT_GREAT\n"
                        + "melee-critical-level:20:4:15:HIT_SUPERB\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertEquals(1, result.getErrors().size(), result.getErrors()::toString);
        assertTrue(result.getErrors().get(0).toLowerCase().contains("melee"), result.getErrors()::toString);
    }

    /**
     * The same for ranged cutoffs, which C checks second, after the melee list.
     *
     * <p>Function nonIncreasingRangedCutoffsRefuseTheFile coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void nonIncreasingRangedCutoffsRefuseTheFile() throws IOException {
        String path = tempFile("ranged-order.txt",
                "ranged-critical-level:10:2:5:HIT_GOOD\n"
                        + "ranged-critical-level:10:3:10:HIT_GREAT\n"
                        + "ranged-critical-level:20:4:15:HIT_SUPERB\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertEquals(1, result.getErrors().size(), result.getErrors()::toString);
        assertTrue(result.getErrors().get(0).toLowerCase().contains("ranged"), result.getErrors()::toString);
    }

    /**
     * The last cutoff is never compared, and a list of one or two rows always passes. C's
     * {@code check_critical_levels()} skips the final row, so 10, 20, 1 loads, and so does 10, 5.
     *
     * <p>Function theLastCutoffIsNotComparedAndShortListsPass coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void theLastCutoffIsNotComparedAndShortListsPass() throws IOException {
        String path = tempFile("last-cutoff.txt",
                "melee-critical-level:10:2:5:HIT_GOOD\n"
                        + "melee-critical-level:20:3:10:HIT_GREAT\n"
                        + "melee-critical-level:1:4:15:HIT_SUPERB\n"
                        + "ranged-critical-level:10:2:5:HIT_GOOD\n"
                        + "ranged-critical-level:5:3:10:HIT_GREAT\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        assertEquals(3, result.getData().meleeCriticalLevel().size());
        assertEquals(2, result.getData().rangedCriticalLevel().size());
    }

    /**
     * An unknown message name refuses the file. C's {@code message_lookup_by_name()} returns -1 and the
     * handler returns {@code PARSE_ERROR_INVALID_MESSAGE}.
     *
     * <p>Function anUnknownMessageNameRefusesTheFile coded on 261019, commented in full on 261019.
     */
    @Test
    void anUnknownMessageNameRefusesTheFile() throws IOException {
        String path = tempFile("bad-msg.txt", "melee-critical-level:10:2:5:NOT_A_MESSAGE\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertTrue(result.getErrors().get(0).contains("Line: 1"), result.getErrors()::toString);
    }

    /**
     * A field after the value is hard here. C's line parser reads only the fields the directive
     * registers and ignores the rest, so {@code world:max-depth:5:6} loads with 5. Pins the
     * divergence recorded in {@code docs/grammar_issues.md}.
     *
     * <p>Function aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt coded on 261019, commented in full
     * on 261019.
     */
    @Test
    void aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("extra.txt", "world:max-depth:5:6\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A hexadecimal value is hard here. C reads an {@code int} with {@code strtol(tok, &z, 0)}, so
     * {@code 0x10} is 16.
     *
     * <p>Function aHexadecimalValueIsRefusedHereWhereCReadsBaseZero coded on 261019, commented in full
     * on 261019.
     */
    @Test
    void aHexadecimalValueIsRefusedHereWhereCReadsBaseZero() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("hex.txt", "world:max-depth:0x10\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A leading zero is read as decimal here. C's base-zero {@code strtol} reads {@code 010} as
     * octal 8.
     *
     * <p>Function aLeadingZeroValueIsReadAsDecimalWhereCReadsItAsOctal coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aLeadingZeroValueIsReadAsDecimalWhereCReadsItAsOctal() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("octal.txt", "world:max-depth:010\n"));

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        assertEquals(10, result.getData().world().maxDepth());
    }

    /**
     * An indented line is hard here. C's {@code parse_file()} skips leading whitespace before the
     * directive, so {@code "  world:max-depth:7"} loads.
     *
     * <p>Function anIndentedLineIsRefusedHereWhereCSkipsTheIndent coded on 261019, commented in full
     * on 261019.
     */
    @Test
    void anIndentedLineIsRefusedHereWhereCSkipsTheIndent() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("indent.txt", "  world:max-depth:7\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A lower-case message name is hard here. C's {@code message_lookup_by_name()} compares with
     * {@code my_stricmp()}, so {@code hit_good} resolves.
     *
     * <p>Function aLowerCaseMessageNameIsRefusedHereWhereCIsCaseBlind coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aLowerCaseMessageNameIsRefusedHereWhereCIsCaseBlind() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("lower-msg.txt", "melee-critical-level:10:2:5:hit_good\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A negative value on a {@code level-max}, {@code world} and the other plain constants loads here.
     * C's handlers for those categories return {@code PARSE_ERROR_INVALID_VALUE} when {@code value < 0}.
     * {@code melee-critical:chance-offset:-60} is the one negative the shipped file needs, and C allows
     * it because the two critical handlers have no such check. Pins the divergence recorded in
     * {@code docs/Assembler_Issues.md}.
     *
     * <p>Function aNegativeConstantLoadsHereWhereCRefusesIt coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void aNegativeConstantLoadsHereWhereCRefusesIt() throws IOException {
        GameConstantsParseResult negative = new GameConstantsReader()
                .parseWithResults(tempFile("negative.txt", "level-max:monsters:-1\nworld:max-depth:-1\n"));

        assertFalse(negative.hasErrors(), () -> negative.getErrors().toString());
        assertEquals(-1, negative.getData().levelMax().monsters());
        assertEquals(-1, negative.getData().world().maxDepth());

        GameConstantsParseResult critical = new GameConstantsReader()
                .parseWithResults(tempFile("negative-crit.txt", "melee-critical:chance-offset:-60\n"));

        assertFalse(critical.hasErrors(), () -> critical.getErrors().toString());
    }

    /**
     * A value too big for an {@code int} is a soft error here, worded "is not an integer". C reads it
     * with {@code strtol} and truncates it without complaint.
     *
     * <p>Function anOverflowingValueIsRefusedHereWhereCTruncatesIt coded on 261019, commented in full
     * on 261019.
     */
    @Test
    void anOverflowingValueIsRefusedHereWhereCTruncatesIt() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("overflow.txt", "world:max-depth:99999999999\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertTrue(result.getErrors().get(0).contains("Line: 1"), result.getErrors()::toString);
    }

    /**
     * A comment on the last line with no newline after it is hard here: the lexer's {@code COMMENT}
     * rule needs the {@code \n}. C skips it.
     *
     * <p>Function aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("tail-comment.txt", "world:max-depth:7\n# the end"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }
}
