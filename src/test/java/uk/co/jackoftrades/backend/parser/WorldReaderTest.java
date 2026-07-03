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
import uk.co.jackoftrades.middle.cave.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link WorldReader} (grammar-suite reader track R4).
 *
 * <p>These tests run the full reader-through-assembler pipeline, so they assert on the assembled
 * {@link World} domain records — not the intermediate {@code WorldParseRecord} DTOs. In particular
 * the {@code "None"} sentinel that the grammar extracts verbatim for the up/down fields is resolved
 * to {@code null} by {@link uk.co.jackoftrades.backend.parser.world.WorldAssembler}, so the town has
 * a {@code null} previous level and the deepest level a {@code null} next level.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/world.txt}; a clean load
 * is itself the assertion that the file's declared {@code record-count} matched the records found.
 * The error-path tests build tiny fixtures that inject a single defect each.
 *
 * <p>Note on coverage: the per-field numeric coercion is effectively unreachable — the lexer's
 * {@code INTEGER} token already guarantees the depth/line/count fields are numeric, so a non-numeric
 * value is a <em>grammar</em> error, not an assembly coercion error. The genuinely reader-level
 * failure is the {@code record-count} declared-vs-actual mismatch, which the grammar cannot catch;
 * that is a <em>soft</em> error, so the valid records still load alongside it (partial-results
 * contract). Both paths are exercised below.
 *
 * @author ClaudeCode
 */
class WorldReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/world.txt";

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

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllLevels() throws IOException {
        ParseResult<World> result = new WorldReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());

        List<World> levels = result.items();
        assertEquals(128, levels.size());

        // Town: level 0, nothing above it (the "None" sentinel is mapped to null by the assembler).
        World town = levels.get(0);
        assertEquals(0, town.levelNumber());
        assertEquals("Town", town.levelName());
        assertNull(town.prevLevel());
        assertEquals("Angband 1", town.nextLevel());

        // The deepest level: nothing below it.
        World deepest = levels.get(127);
        assertEquals(127, deepest.levelNumber());
        assertEquals("Angband 127", deepest.levelName());
        assertEquals("Angband 126", deepest.prevLevel());
        assertNull(deepest.nextLevel());
    }

    @Test
    void recordCountMismatchFailsClosed() throws IOException {
        // world.txt is the whole cave map, so a wrong record-count (missing/extra levels) is a HARD
        // error, not a soft one: the whole file refuses to load rather than serve a partial map.
        String path = tempFile("bad-count.txt", "record-count:5\nlevel:0:Town:None:Angband 1\n");

        ParseResult<World> result = new WorldReader().parseWithResults(path);

        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void badLevelNumberFailsClosed() throws IOException {
        // A level number that overflows int range (the only way past the lexer's INTEGER token) is a
        // corrupt cave-map index, so it is HARD: nothing loads, the bad number is reported.
        String path = tempFile("bad-number.txt", "record-count:1\nlevel:99999999999:Deep:None:None\n");

        ParseResult<World> result = new WorldReader().parseWithResults(path);

        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("Invalid level number") && e.contains("99999999999")),
                result.errors()::toString);
    }

    @Test
    void missingRecordCountHeaderIsReportedWithNoItems() throws IOException {
        // No record-count directive at all: a grammar syntax error, surfaced through ParseErrors as a
        // hard (fail-closed) failure, so nothing loads.
        String path = tempFile("no-header.txt", "level:0:Town:None:Angband 1\n");

        ParseResult<World> result = new WorldReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void twoDifferentErrorsAreBothReportedAndFailClosed() throws IOException {
        // Two *different* hard errors at once: the header over-declares (5 vs the 2 records present),
        // AND the second level's depth overflows int range. Both land on the hard error channel, so
        // the whole file fails closed - no items - and both errors are reported (they are collected
        // before the single throwIfAny() aborts the parse).
        String path = tempFile("two-errors.txt", String.join("\n",
                "record-count:5",
                "level:0:Town:None:Angband 1",
                "level:99999999999:Deep:Angband 1:None",
                ""));

        ParseResult<World> result = new WorldReader().parseWithResults(path);

        // Fail closed: nothing loads.
        assertTrue(result.items().isEmpty(), result.items()::toString);

        // Both distinct errors are reported.
        List<String> errors = result.errors();
        assertTrue(errors.stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 2")),
                errors::toString);
        assertTrue(errors.stream().anyMatch(e -> e.contains("Invalid level number") && e.contains("99999999999")),
                errors::toString);
    }
}
