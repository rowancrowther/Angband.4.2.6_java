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
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;

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
 * per soft-error branch in {@link uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureAssembler},
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

    // =====================================================================
    // Helpers for the contract and difference tests below
    // =====================================================================

    private ParseResult<Feature> load(String name, String content) throws IOException {
        return new TerrainReader().parseWithResults(tempFile(name, content));
    }

    private static String withHeader(int count, String records) {
        return "record-count:" + count + "\n" + records;
    }

    /** A hard grammar or lexer error: nothing loads and the messages come back. */
    private static void assertHardError(ParseResult<Feature> r) {
        assertTrue(r.hasErrors(), "expected the messages of a hard error");
        assertTrue(r.items().isEmpty(), r.items()::toString);
    }

    // =====================================================================
    // The real file, worked out from the upstream terrain.txt and list-terrain.h
    // =====================================================================

    /**
     * Code and name of every {@code code:} record in the upstream file, in file order, which is the
     * order of {@code list-terrain.h}: the slot {@code f_info} files each one under.
     */
    private static final String[][] SHIPPED = {
            {"NONE", "unknown grid"}, {"FLOOR", "open floor"}, {"CLOSED", "closed door"},
            {"OPEN", "open door"}, {"BROKEN", "broken door"}, {"LESS", "up staircase"},
            {"MORE", "down staircase"}, {"STORE_GENERAL", "General Store"}, {"STORE_ARMOR", "Armoury"},
            {"STORE_WEAPON", "Weapon Smiths"}, {"STORE_BOOK", "Bookseller"},
            {"STORE_ALCHEMY", "Alchemy Shop"}, {"STORE_MAGIC", "Magic Shop"},
            {"STORE_BLACK", "Black Market"}, {"HOME", "Home"}, {"SECRET", "secret door"},
            {"RUBBLE", "pile of rubble"}, {"MAGMA", "magma vein"}, {"QUARTZ", "quartz vein"},
            {"MAGMA_K", "magma vein with treasure"}, {"QUARTZ_K", "quartz vein with treasure"},
            {"GRANITE", "granite wall"}, {"PERM", "permanent wall"}, {"LAVA", "lava"},
            {"PASS_RUBBLE", "pile of passable rubble"}
    };

    @Test
    void theRealFileLoadsEveryRecordInFileOrder() throws IOException {
        List<Feature> items = new TerrainReader().parseWithResults(REAL_FILE).items();

        assertEquals(SHIPPED.length, items.size());
        for (int i = 0; i < SHIPPED.length; i++) {
            assertEquals(TerrainFlags.valueOf("FEAT_" + SHIPPED[i][0]), items.get(i).getCodeFlags(),
                    "record " + i);
            assertEquals(SHIPPED[i][1], items.get(i).getName(), "record " + i);
        }
    }

    /** The upstream file has eight records with {@code TF_SHOP}, STORE_GENERAL to HOME. */
    @Test
    void theRealFileHasEightShopsAndOneMimic() throws IOException {
        List<Feature> items = new TerrainReader().parseWithResults(REAL_FILE).items();

        assertEquals(8, items.stream().filter(Feature::isShop).count());
        assertEquals(List.of(TerrainFlags.FEAT_SECRET),
                items.stream().filter(Feature::isMimicing).map(Feature::getCodeFlags).toList());
    }

    /**
     * The shipped file differs from upstream in the {@code record-count:25} header with the blank
     * line after it, one trailing space on a comment, and the trailing space on the lava
     * {@code walk-msg:} and {@code run-msg:} lines; every other line is identical. The trailing
     * spaces are part of the C message, as {@code str} takes the rest of the line.
     */
    @Test
    void theShippedFileDiffersFromUpstreamInTheHeaderAndThreeTrailingSpaces() throws IOException {
        List<String> lines = Files.readAllLines(Path.of(REAL_FILE));

        assertEquals(1, lines.stream().filter(l -> l.startsWith("record-count:")).count());
        assertTrue(lines.contains("record-count:25"));
        assertTrue(lines.contains("walk-msg:The lava will scald you!  Really step in?"));
        assertTrue(lines.contains("run-msg:Lava blocks your path.  Step into it?"));
    }

    // =====================================================================
    // The reader's contract
    // =====================================================================

    @Test
    void parseReturnsTheSameFeaturesAsParseWithResults() throws Exception {
        List<Feature> items = new TerrainReader().parse(REAL_FILE);

        // Feature.equals compares Flag by identity, so compare what each record is called instead.
        assertEquals(new TerrainReader().parseWithResults(REAL_FILE).items().stream().map(Feature::getName).toList(),
                items.stream().map(Feature::getName).toList());
    }

    /** C's {@code parse_file_quit_not_found} quits; the port throws so the loader can stop. */
    @Test
    void aMissingFileIsAnIOException() {
        assertThrows(IOException.class,
                () -> new TerrainReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void aHardGrammarErrorReturnsNoFeaturesAndTheMessages() throws IOException {
        assertHardError(load("hard.txt", withHeader(2, VALID_RECORD + "nonsense:1\n")));
    }

    // =====================================================================
    // What the grammar does the way C does
    // =====================================================================

    /** C: {@code parse_feat_priority}, {@code parse_feat_digging} and {@code parse_feat_mimic} assign, so the last wins. */
    @Test
    void aRepeatedPriorityDiggingOrMimicKeepsTheLastLine() throws IOException {
        ParseResult<Feature> r = load("repeat-assign.txt", withHeader(1, VALID_RECORD
                + "priority:1\npriority:7\ndigging:2\ndigging:4\nmimic:NONE\nmimic:GRANITE\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        String text = r.items().get(0).toString();
        assertTrue(text.contains("priority=7"), text);
        assertTrue(text.contains("dig=4"), text);
        assertTrue(text.contains("mimic=FEAT_GRANITE"), text);
    }

    /** C: {@code parse_feat_desc} calls {@code string_append}, so lines join with no separator added. */
    @Test
    void descLinesJoinInFileOrderKeepingTheLeadingBlank() throws IOException {
        ParseResult<Feature> r = load("desc.txt", withHeader(1, VALID_RECORD
                + "desc:An open floor or bare ground.  Can be walked across by anything, and can\n"
                + "desc: hold traps or items.\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertTrue(r.items().get(0).toString().contains(
                "description='An open floor or bare ground.  Can be walked across by anything, and can"
                        + " hold traps or items.'"), r.items().get(0)::toString);
    }

    /** C: {@code name} is a {@code str}, which takes the rest of the line, colons included. */
    @Test
    void aColonInTheNameIsKept() throws IOException {
        ParseResult<Feature> r = load("colon-name.txt", withHeader(1,
                "code:FLOOR\nname:a: b:c\ngraphics:.:w\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals("a: b:c", r.items().get(0).getName());
    }

    /** C: any order of the optional lines is accepted after the three header lines. */
    @Test
    void optionalLinesMayComeInAnyOrder() throws IOException {
        ParseResult<Feature> r = load("order.txt", withHeader(1, VALID_RECORD
                + "resist-flag:RAND_50\ndesc:d\nlook-prefix:a\nflags:LOS | PROJECT\npriority:3\n"
                + "flags:PASSABLE\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertTrue(r.items().get(0).isPassable());
        assertTrue(r.items().get(0).isLos());
    }

    // =====================================================================
    // Where the grammar is stricter than C (docs/grammar_issues.md)
    // =====================================================================

    /** C: {@code parse_feat_code} is the only line a record needs. */
    @Test
    void aRecordWithOnlyACodeIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("code-only.txt", withHeader(1, "code:FLOOR\n")));
    }

    @Test
    void aRecordWithNoGraphicsIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("no-graphics.txt", withHeader(1, "code:FLOOR\nname:open floor\n")));
    }

    @Test
    void aRecordWithNoNameIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("no-name.txt", withHeader(1, "code:FLOOR\ngraphics:.:w\n")));
    }

    /** C: after {@code code:} the handlers run in whatever order the lines come. */
    @Test
    void graphicsBeforeTheNameIsAHardErrorHereWhereCAcceptsAnyOrder() throws IOException {
        assertHardError(load("graphics-first.txt", withHeader(1,
                "code:FLOOR\ngraphics:.:w\nname:open floor\n")));
    }

    /** C: {@code parse_feat_graphics} assigns, so a second line replaces the first. */
    @Test
    void aRepeatedGraphicsLineIsAHardErrorHereWhereCKeepsTheLast() throws IOException {
        assertHardError(load("two-graphics.txt", withHeader(1, VALID_RECORD + "graphics:#:r\n")));
    }

    /** C: {@code string_append} on a {@code walk-msg:} line that already has text, so the lines join. */
    @Test
    void repeatedMessageLinesReplaceEachOtherHereWhereCAppendsThem() throws IOException {
        String[][] directives = {
                {"walk-msg", "walkMsg"}, {"run-msg", "runMsg"}, {"hurt-msg", "hurtMsg"},
                {"die-msg", "dieMsg"}, {"confused-msg", "confusedMsg"}
        };
        for (String[] d : directives) {
            ParseResult<Feature> r = load(d[0] + ".txt", withHeader(1, VALID_RECORD
                    + d[0] + ":first\n" + d[0] + ":second\n"));

            assertFalse(r.hasErrors(), r.errors()::toString);
            String text = r.items().get(0).toString();
            assertTrue(text.contains(d[1] + "='second'"), d[0] + ": " + text);
        }
    }

    /** C: the same append, then {@code finish_parse_feat} adds one trailing space. */
    @Test
    void repeatedLookLinesReplaceEachOtherHereWhereCAppendsThem() throws IOException {
        ParseResult<Feature> r = load("look.txt", withHeader(1, VALID_RECORD
                + "look-prefix:first\nlook-prefix:second\n"
                + "look-in-preposition:in\nlook-in-preposition:to\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        String text = r.items().get(0).toString();
        assertTrue(text.contains("lookPrefix='second '"), text);
        assertTrue(text.contains("lookInPreposition='to '"), text);
    }

    /** C: {@code strtok(flags, " |")}, so a space alone separates two flags. */
    @Test
    void flagsSeparatedByASpaceAloneAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        assertHardError(load("space-flags.txt", withHeader(1, VALID_RECORD + "flags:LOS PASSABLE\n")));
    }

    /** C: {@code flags} is {@code ?str}, and an absent value is {@code PARSE_ERROR_NONE}. */
    @Test
    void anEmptyFlagsLineIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError(load("empty-flags.txt", withHeader(1, VALID_RECORD + "flags:\n")));
    }

    /** C: a file with a header and no records runs {@code finish_parse_feat} over a zeroed table. */
    @Test
    void aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        assertHardError(load("empty.txt", "record-count:0\n"));
    }

    /** C: {@code strtoul(tok, &z, 0)}, so {@code 0x3} is 3. */
    @Test
    void aHexadecimalPriorityIsAHardErrorHereWhereCReadsBaseZero() throws IOException {
        assertHardError(load("hex-priority.txt", withHeader(1, VALID_RECORD + "priority:0x3\n")));
    }

    /** C: {@code strtoul} stops at the {@code x} and {@code z != tok}, so {@code 3x} is 3. */
    @Test
    void textAfterThePriorityNumberIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(load("junk-priority.txt", withHeader(1, VALID_RECORD + "priority:3x\n")));
    }

    /** C: an {@code int} is tokenised on a colon and nothing checks what follows the last field. */
    @Test
    void aSecondFieldAfterTheDiggingNumberIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(load("junk-digging.txt", withHeader(1, VALID_RECORD + "digging:2:junk\n")));
    }

    /** C: {@code resist-flag} is a {@code sym}, tokenised on a colon, so the text after it is ignored. */
    @Test
    void aSecondFieldAfterTheResistFlagIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        assertHardError(load("junk-resist.txt", withHeader(1, VALID_RECORD + "resist-flag:RAND_50:x\n")));
    }

    /** C: {@code color_text_to_attr} answers white for a name it does not know. */
    @Test
    void anUnknownColourNameIsAHardErrorHereWhereCDrawsItWhite() throws IOException {
        assertHardError(load("bad-colour.txt", withHeader(1, "code:FLOOR\nname:x\ngraphics:.:nonsense\n")));
    }

    /** C: {@code parser_parse} skips leading whitespace before it reads the directive. */
    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        assertHardError(load("indented.txt", withHeader(1, VALID_RECORD + "  priority:3\n")));
    }

    // =====================================================================
    // Where the list differs from f_info
    // =====================================================================

    /**
     * C: {@code f_info} is allocated with {@code FEAT_MAX} slots and each record fills the slot of its
     * {@code code:}, so MORE (6) would come after FLOOR (1) whatever the file order. The list holds the
     * records in file order and nothing else.
     */
    @Test
    void recordsComeBackInFileOrderHereWhereCFilesEachUnderItsCode() throws IOException {
        ParseResult<Feature> r = load("order.txt", withHeader(2,
                "code:MORE\nname:down staircase\ngraphics:>:w\n"
                        + "code:FLOOR\nname:open floor\ngraphics:.:w\n"));

        assertFalse(r.hasErrors(), r.errors()::toString);
        assertEquals(List.of(TerrainFlags.FEAT_MORE, TerrainFlags.FEAT_FLOOR),
                r.items().stream().map(Feature::getCodeFlags).toList());
    }
}
