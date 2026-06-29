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

import uk.co.jackoftrades.middle.game.globals.GameConstantsData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link GameConstantsReader} (grammar-suite reader-track R4).
 *
 * <p>Unlike the list readers, this one produces a single {@link GameConstantsData} carrier, so a
 * clean load <em>is</em> the assertion that every required constant was present exactly once.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/constants.txt}. The
 * error-path tests start from that same complete file and inject a single defect each, so the only
 * error reported is the one under test — no "missing constant" cascade drowning out the signal.
 * The completeness check is exercised on its own by removing a line ({@link #missingConstantIsReported}).
 *
 * <p>Two deliberate properties of these fixtures are worth noting:
 * <ul>
 *   <li>The injected scalar defects target {@code world:max-depth}, which is already set by the real
 *       file near the top. A duplicate or a non-integer on a later line therefore produces exactly
 *       one error and no missing-constant cascade (the slot is already filled).</li>
 *   <li>The critical-level categories are not completeness-checked (0-or-more is legal), so a
 *       malformed level line yields just its own arity error.</li>
 * </ul>
 *
 * @author ClaudeCode
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

    @Test
    void twoDistinctErrorsAreBothReportedWithTheirLines() throws IOException {
        // Append a duplicate (world:max-depth is already set) and an unknown category; neither
        // removes a required constant, so collect-and-report yields exactly these two, each tagged
        // with its own line.
        String base = realText();
        int duplicateLine = lineCount(base) + 1;
        int unknownLine = lineCount(base) + 2;

        String path = tempFile("two-errors.txt", base + "world:max-depth:1\n" + "nonsense:foo:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());

        List<String> errors = result.getErrors();
        assertEquals(2, errors.size(), errors::toString);

        assertTrue(errors.stream().anyMatch(
                        e -> e.contains("Line: " + duplicateLine) && e.contains("duplicate world:max-depth")),
                errors::toString);
        assertTrue(errors.stream().anyMatch(
                        e -> e.contains("Line: " + unknownLine) && e.contains("unknown category")),
                errors::toString);
    }

    @Test
    void missingConstantIsReported() throws IOException {
        // Drop world:max-depth from an otherwise complete file; build()'s completeness sweep flags it.
        String path = tempFile("missing.txt", realTextWithout("world:max-depth:"));

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());

        List<String> errors = result.getErrors();
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("Missing required constant world:max-depth"), errors::toString);
    }

    @Test
    void duplicateScalarConstantIsReportedWithItsLine() throws IOException {
        String base = realText();
        int duplicateLine = lineCount(base) + 1;

        String path = tempFile("duplicate.txt", base + "world:max-depth:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());

        List<String> errors = result.getErrors();
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("Line: " + duplicateLine), errors::toString);
        assertTrue(errors.get(0).contains("duplicate world:max-depth"), errors::toString);
    }

    @Test
    void nonIntegerValueIsReportedWithItsLine() throws IOException {
        // A second world:max-depth carrying a non-numeric value: coercion fails on that line and the
        // null-guard returns before the set, so the already-filled slot is untouched — one error, no
        // missing cascade.
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
