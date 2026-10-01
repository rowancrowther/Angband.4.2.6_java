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
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link ProjectionReader} (grammar-suite reader track R4).
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/projection.txt}; a clean
 * load is itself the assertion that all 56 records resolved (every code/type/msgt/colour). The
 * error-path tests inject a single defect each, either into a minimal fixture or into a copy of
 * the shipped file. The assembler is fail-closed on the element block (a wrong element count
 * yields an empty item list plus the collected errors) but soft on other faults, where the bad
 * record is skipped and the rest still loads; see {@link ProjectionAssemblerTest} for the cases
 * driven from hand-built records.
 *
 * @author Rowan Crowther
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
    void recordCountMismatchIsReportedAlongsideTheElementCountFailure() throws IOException {
        // Header over-declares: 5 vs the single record present. The mismatch is reported as a soft
        // error, but a one-record file cannot satisfy the element rule either: C's
        // finish_parse_projection() quits unless exactly 25 records are of type element, so the
        // assembler discards the lone record and says why.
        String path = tempFile("bad-count.txt", "record-count:5\n" + ONE_RECORD);
        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);
        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") &&
                        e.contains("contains 1")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("expected 25 got 1")),
                result.errors()::toString);
    }

    @Test
    void headerMismatchOnTheRealFileIsReportedButAllProjectionsStillLoad() throws IOException {
        // The shipped file with its header changed to over-declare by one: the mismatch is a soft
        // error and the 56 valid records, which satisfy the element rule, all still load.
        String path = tempFile("real-bad-count.txt",
                Files.readString(Path.of(REAL_FILE)).replace("record-count:56", "record-count:57"));

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertEquals(56, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 57") &&
                        e.contains("contains 56")),
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

    @Test
    void unknownCodeAfterTheElementBlockIsReportedAndTheRestLoads() throws IOException {
        // The shipped file plus one extra block whose code is not a ProjectionEnum value, with the
        // header raised to match. The element block is intact, so the file passes the element rule;
        // the unknown code is a soft error and that one block is skipped. It does NOT fail closed.
        String extra = "\ncode:NOTACODE\ntype:monster\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate\n";
        String path = tempFile("real-plus-bad-code.txt",
                Files.readString(Path.of(REAL_FILE)).replace("record-count:56", "record-count:57") + extra);

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertEquals(56, result.items().size(), result.items()::toString);
        List<String> errors = result.errors();
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.getFirst().contains("NOTACODE"), errors::toString);
    }

    @Test
    void unknownTypeIsNotAnError() throws IOException {
        // C keeps type as a string and only ever tests it against "element", so an unrecognised
        // type is not a fault: the block loads with no type. Here ELEC (position 2) has an unknown
        // type, so it no longer counts as an element, the file has 24, and the element rule rejects
        // it - with the element count as the only complaint.
        String path = tempFile("unknown-type.txt", String.join("\n",
                "record-count:2",
                "code:ACID\ntype:element\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Slate",
                "code:ELEC\ntype:NOTATYPE\ndesc:x\nblind-desc:x\nobvious:1\ncolor:Blue",
                ""));

        ParseResult<Projection> result = new ProjectionReader().parseWithResults(path);

        assertTrue(result.items().isEmpty(), result.items()::toString);
        List<String> errors = result.errors();
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.getFirst().contains("expected 25 got 1"), errors::toString);
    }
}
