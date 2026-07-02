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
import uk.co.jackoftrades.middle.game.Projection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ProjectionReader} (grammar-suite reader track R4).
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/projection.txt}; a clean
 * load is itself the assertion that all 56 records resolved (every code/type/msgt/colour). The
 * error-path tests build minimal fixtures injecting a single defect each. The reader is fail-closed:
 * any error yields an empty item list plus the collected errors.
 *
 * @author ClaudeCode
 */
class ProjectionReaderTest {

    private static final String REAL_FILE = "lib/gamedata/projection.txt";

    /**
     * A minimal, valid projection block (mandatory fields only).
     */
    private static final String ONE_RECORD =
            "code:ACID\ntype:element\ndesc:acid\nblind-desc:acid\nobvious:1\ncolor:Slate\n";

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllProjections() throws IOException {
        ParseResult<Projection> result = new ProjectionReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(56, result.items().size());
        // Spot-check the first record (ACID) via the one getter Projection exposes.
        assertEquals("acid", result.items().get(0).getLashDescription());
    }

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        // Header over-declares: 5 vs the single record present. The mismatch is a
        // soft error - the one valid record still loads (partial-results contract).
        String path = tempFile("bad-count.txt", "record-count:5\n" + ONE_RECORD);
        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);
        assertTrue(result.hasErrors());
        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") &&
                        e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void unknownCodeIsReportedWithNoItems() throws IOException {
        // NOTACODE is not a ProjectionEnum value.
        String path = tempFile("bad-code.txt",
                "record-count:1\ncode:NOTACODE\ntype:element\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate\n");

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("NOTACODE")), result.errors()::toString);
    }

    @Test
    void missingRecordCountHeaderIsReportedWithNoItems() throws IOException {
        // No record-count directive: a grammar syntax error surfaced through ParseErrors.
        String path = tempFile("no-header.txt", ONE_RECORD);

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }
}
