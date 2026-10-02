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
}
