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
import uk.co.jackoftrades.middle.cave.Feature;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link TerrainReader} (grammar-suite reader track R4),
 * driving {@code TerrainFeatureLexer}/{@code TerrainFeatureGrammar} through the
 * shared {@link GrammarDriver} into {@link Feature} objects.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/terrain.txt}
 * (25 records); a clean load is itself the assertion that every {@code code:}/{@code mimic:}/
 * {@code flags:}/{@code resist-flag:} resolved and every {@code graphics:} tokenised - including
 * the {@code :}-glyph rubble terrains that the {@code AngbandDisplayCharacter} glyph/separator
 * split now handles. The remaining tests each inject a single defect into a minimal fixture, one
 * per soft-error branch in {@link uk.co.jackoftrades.backend.parser.terrainfeature.TerrainFeatureAssembler},
 * plus the hard "missing record-count" grammar error and a soft record-count mismatch. The final
 * test proves two <em>different</em> defects are each reported independently while a valid record
 * still loads (the partial-results contract).
 *
 * <p>Every soft defect makes the assembler skip that one record ({@code continue}) and append a
 * message; a hard grammar error fails the whole file closed (empty items, errors carried).
 *
 * @author Rowan Crowther
 */
class TerrainReaderTest {

    private static final String REAL_FILE = "lib/gamedata/terrain.txt";

    /**
     * A minimal, valid terrain block: the three mandatory header directives only
     * ({@code code}/{@code name}/{@code graphics}), resolving to {@code FEAT_FLOOR}.
     */
    private static final String VALID_RECORD =
            "code:FLOOR\nname:open floor\ngraphics:.:w\n";

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Happy path -------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAll25Features() throws IOException {
        ParseResult<Feature> result = new TerrainReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(25, result.items().size(), () -> "loaded: " + result.items().size());

        // Spot-check the first record and a flag-bearing one via the getters Feature exposes.
        Feature none = result.items().get(0);
        assertEquals(TerrainFlags.FEAT_NONE, none.getCodeFlags());
        assertEquals("unknown grid", none.getName());
        assertTrue(result.items().get(1).isFloor(), "second record (open floor) should carry TF_FLOOR");
    }

    /**
     * Regression guard for the {@code AngbandDisplayCharacter} glyph/separator fix: a terrain whose
     * display glyph is itself a colon ({@code graphics:::W}, as RUBBLE/PASS_RUBBLE ship) must
     * tokenise - glyph {@code :}, separator {@code :}, colour {@code W} - and load cleanly. Before
     * the fix this produced a {@code token recognition error at ':'} on every such line.
     */
    @Test
    void colonGlyphTerrainTokenisesAndLoads() throws IOException {
        String path = tempFile("colon-glyph.txt",
                "record-count:1\ncode:RUBBLE\nname:pile of rubble\ngraphics:::W\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(TerrainFlags.FEAT_RUBBLE, result.items().get(0).getCodeFlags());
    }

    // ---- Hard grammar error (fail closed) --------------------------------

    @Test
    void missingRecordCountHeaderIsReportedWithNoItems() throws IOException {
        // The file rule is `recordCount (terrain)+ EOF`; with no record-count directive the parse
        // fails hard through ParseErrors and the whole file is dropped (empty items, errors carried).
        String path = tempFile("no-header.txt", VALID_RECORD);

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    // ---- Soft record-count mismatch --------------------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        // Header over-declares (5 vs the single record). The mismatch is soft: the valid record
        // still loads alongside the reported discrepancy.
        String path = tempFile("bad-count.txt", "record-count:5\n" + VALID_RECORD);

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- One test per assembler soft-error branch ------------------------

    @Test
    void unknownTerrainCodeIsReportedAndRecordSkipped() throws IOException {
        // NOTACODE has no matching FEAT_ value in TerrainFlags.
        String path = tempFile("bad-code.txt",
                "record-count:1\ncode:NOTACODE\nname:x\ngraphics:.:w\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal terrain feature code") && e.contains("NOTACODE")),
                result.errors()::toString);
    }

    @Test
    void unknownMimicCodeIsReportedAndRecordSkipped() throws IOException {
        // Valid code, but the mimic references a non-existent FEAT_ value.
        String path = tempFile("bad-mimic.txt",
                "record-count:1\ncode:FLOOR\nname:open floor\ngraphics:.:w\nmimic:NOTAMIMIC\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal mimic feature code") && e.contains("NOTAMIMIC")),
                result.errors()::toString);
    }

    @Test
    void unknownTerrainFlagIsReportedAndRecordSkipped() throws IOException {
        // NOTAFLAG has no matching TF_ value in TerrainFeatureFlags.
        String path = tempFile("bad-flag.txt",
                "record-count:1\ncode:FLOOR\nname:open floor\ngraphics:.:w\nflags:NOTAFLAG\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("Illegal terrain") && e.contains("NOTAFLAG")),
                result.errors()::toString);
    }

    @Test
    void unknownResistFlagIsReportedAndRecordSkipped() throws IOException {
        // NOTARESIST has no matching RF_ value in MonsterRaceFlag.
        String path = tempFile("bad-resist.txt",
                "record-count:1\ncode:FLOOR\nname:open floor\ngraphics:.:w\nresist-flag:NOTARESIST\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("Illegal resist flag") && e.contains("NOTARESIST")),
                result.errors()::toString);
    }

    @Test
    void overflowingPriorityIsReportedAndRecordSkipped() throws IOException {
        // The INTEGER token guarantees digits, so the only way to reach the assembler's priority
        // NumberFormatException branch is a value too large for int.
        String path = tempFile("bad-priority.txt",
                "record-count:1\ncode:FLOOR\nname:open floor\ngraphics:.:w\npriority:99999999999999999999\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal number format on priority")),
                result.errors()::toString);
    }

    @Test
    void overflowingDiggingIsReportedAndRecordSkipped() throws IOException {
        // Same as priority: digging only fails on an int-overflowing value.
        String path = tempFile("bad-digging.txt",
                "record-count:1\ncode:FLOOR\nname:open floor\ngraphics:.:w\ndigging:99999999999999999999\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal number format on digging")),
                result.errors()::toString);
    }

    // ---- Digit-bearing FLAG token (FLAG_BODY digit tail) -----------------

    /**
     * Pins the shared {@code FLAG_BODY} rule ({@code ('A'..'Z') ('A'..'Z' | '_' | '0'..'9')*} in
     * {@code imports/Flags.g4}): a flag value whose tail carries digits must lex as a single
     * {@code FLAG} token and never split or collide with {@code INTEGER}. The digit tail is the
     * non-obvious case - no <em>terrain</em> flag ships with a digit, so this exercises a latent
     * lexer capability deliberately.
     *
     * <p>{@code resist-flag:RAND_50} resolves to the real {@code RF_RAND_50} monster-race flag, so a
     * clean, error-free single-record load is the proof: had the digit tail failed to lex we would
     * get a hard parse error, and had {@code RAND_50} failed to resolve we would get the assembler's
     * soft "Illegal resist flag" message with zero items - neither occurs here.
     */
    @Test
    void digitBearingFlagTokenisesAndResolves() throws IOException {
        String path = tempFile("digit-flag.txt",
                "record-count:1\ncode:FLOOR\nname:open floor\ngraphics:.:w\nresist-flag:RAND_50\n");

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size(), result.items()::toString);
        assertEquals(TerrainFlags.FEAT_FLOOR, result.items().get(0).getCodeFlags());
    }

    // ---- Two different defects at once -----------------------------------

    @Test
    void twoDifferentErrorsAreEachLoggedAndGoodRecordSurvives() throws IOException {
        // Three records, record-count matching (so no count error): one valid, one with an unknown
        // terrain code, one with an unknown terrain flag. The two *different* semantic errors are
        // each reported and both bad records skipped, while the valid record still loads.
        String path = tempFile("mixed.txt", String.join("\n",
                "record-count:3",
                "code:FLOOR\nname:open floor\ngraphics:.:w",
                "code:NOTACODE\nname:x\ngraphics:.:w",
                "code:CLOSED\nname:closed door\ngraphics:+:w\nflags:NOTAFLAG",
                ""));

        ParseResult<Feature> result = new TerrainReader().parseWithResults(path);

        // Only the valid FLOOR record survives.
        assertEquals(1, result.items().size(), result.items()::toString);
        assertEquals(TerrainFlags.FEAT_FLOOR, result.items().get(0).getCodeFlags());

        // Both distinct errors are present.
        List<String> errors = result.errors();
        assertTrue(errors.stream().anyMatch(
                        e -> e.contains("illegal terrain feature code") && e.contains("NOTACODE")),
                errors::toString);
        assertTrue(errors.stream().anyMatch(
                        e -> e.contains("Illegal terrain") && e.contains("NOTAFLAG")),
                errors::toString);
    }
}
