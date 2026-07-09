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

import uk.co.jackoftrades.middle.Activation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ActivationReader} (grammar-suite reader-track R4).
 *
 * <p>The happy-path tests run against the real {@code lib/gamedata/activation.txt}; the
 * error-path tests write small malformed files to a temp directory. They exercise the two
 * outcomes of the collect-and-report design end to end:
 * <ul>
 *   <li>a clean file yields all items and no errors;</li>
 *   <li>a parse error (checkpoint 1) or a data error (checkpoint 2) yields <em>no</em> items
 *       and a populated error list carrying the source line.</li>
 * </ul>
 *
 * <p>Note: {@link Activation} currently exposes only {@code getName()}, so field-level
 * assertions are limited to the record name; richer assertions await getters.
 *
 * <p>Record counts are no longer asserted against a hard-coded sentinel: under C12 the
 * data file declares its own {@code record-count}, and the reader treats a declared-vs-actual
 * mismatch as a hard error. A clean load therefore <em>is</em> the count assertion.
 *
 * @author Rowan Crowther
 */
class ActivationReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/activation.txt";

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
    void parseWithResultLoadsAllRecordsCleanly() throws IOException {
        ParseResult<Activation> result = new ActivationReader().parseWithResult(REAL_FILE);

        // No errors implies the file's declared record-count matched the records found.
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertFalse(result.items().isEmpty());
        assertEquals("CURE_POISON", result.items().get(0).getName());
    }

    @Test
    void parseShimReturnsTheItems() throws IOException {
        List<Activation> activations = new ActivationReader().parse(REAL_FILE);

        assertFalse(activations.isEmpty());
        assertEquals("CURE_POISON", activations.get(0).getName());
    }

    @Test
    void dataErrorYieldsNoItemsAndAReportedError() throws IOException {
        // Parses fine, but NOTAREALEFFECT is not a known effect — a data error. The
        // record-count header is line 1, so the record's name token sits on line 2.
        String path = tempFile("bad-data.txt",
                "record-count:1\nname:TEST_BAD\naim:0\nlevel:5\npower:1\neffect:NOTAREALEFFECT\ndesc:bad activation\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertEquals(1, result.errors().size());
        String error = result.errors().get(0);
        assertTrue(error.contains("NOTAREALEFFECT"), error);
        assertTrue(error.contains("line 2"), error);
    }

    @Test
    void parseErrorYieldsNoItemsAndAReportedError() throws IOException {
        // Header is present, but a record must begin with "name:"; this one starts
        // with "power:" — a syntax error (checkpoint 1).
        String path = tempFile("bad-syntax.txt", "record-count:1\npower:5\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void recordCountMismatchIsAHardError() throws IOException {
        // The header declares 5 records but the file holds only 1: a C12 hard error.
        // The records parse and assemble cleanly, so this exercises the reader's
        // declared-vs-actual cross-check, not a grammar or data error.
        String path = tempFile("bad-count.txt",
                "record-count:5\nname:TEST_OK\naim:0\nlevel:5\npower:1\neffect:CURE:POISONED\ndesc:fine\n");

        ParseResult<Activation> result = new ActivationReader().parseWithResult(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        String error = result.errors().get(0);
        assertTrue(error.contains("declares 5"), error);
        assertTrue(error.contains("contains 1"), error);
    }
}
