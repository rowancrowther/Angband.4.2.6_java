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
import uk.co.jackoftrades.backend.parser.world.WorldParseRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link WorldReader} (grammar-suite reader track R4).
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/world.txt}; a clean load
 * is itself the assertion that the file's declared {@code record-count} matched the records found.
 * The error-path tests build tiny fixtures that inject a single defect each.
 *
 * <p>Note on coverage: the reader's per-field {@code parseInt} guards are effectively unreachable —
 * the lexer's {@code INTEGER} token already guarantees the depth/line/count fields are numeric, so a
 * non-numeric value is a <em>grammar</em> error, not a reader coercion error. The genuinely
 * reader-level failure is the {@code record-count} declared-vs-actual mismatch, which the grammar
 * cannot catch; that is exercised on its own below.
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
        ParseResult<WorldParseRecord> result = new WorldReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());

        List<WorldParseRecord> levels = result.items();
        assertEquals(128, levels.size());

        // Town: level 0, nothing above it (the "None" sentinel is mapped to null by the reader).
        WorldParseRecord town = levels.get(0);
        assertEquals(0, town.getLevelNumber());
        assertEquals("Town", town.getLevelName());
        assertNull(town.getUp());
        assertEquals("Angband 1", town.getDown());

        // The deepest level: nothing below it.
        WorldParseRecord deepest = levels.get(127);
        assertEquals(127, deepest.getLevelNumber());
        assertEquals("Angband 127", deepest.getLevelName());
        assertEquals("Angband 126", deepest.getUp());
        assertNull(deepest.getDown());
    }

    @Test
    void recordCountMismatchIsReportedWithNoItems() throws IOException {
        // One real record, but the header over-declares — the reader's declared-vs-actual cross-check
        // (the one failure the grammar cannot detect) fires.
        String path = tempFile("bad-count.txt", "record-count:5\nlevel:0:Town:None:Angband 1\n");

        ParseResult<WorldParseRecord> result = new WorldReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void missingRecordCountHeaderIsReportedWithNoItems() throws IOException {
        // No record-count directive at all: a grammar syntax error, surfaced through ParseErrors.
        String path = tempFile("no-header.txt", "level:0:Town:None:Angband 1\n");

        ParseResult<WorldParseRecord> result = new WorldReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }
}
