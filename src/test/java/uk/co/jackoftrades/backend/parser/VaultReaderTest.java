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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.cave.enums.RoomFlags;
import uk.co.jackoftrades.middle.cave.profiles.vault.Vault;
import uk.co.jackoftrades.middle.cave.roombuilders.RoomType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link VaultReader}: file text -> {@code VaultLexer}/
 * {@code VaultGrammar} -> {@code VaultParseRecord} -> {@code VaultAssembler} -> {@link Vault}.
 *
 * <p>Expected values are taken from {@code lib/gamedata/vault.txt} itself and from the C parser it
 * ports, {@code init_parse_vault} ({@code [C] src/generate.c:595}).
 *
 * @author Rowan Crowther
 */
class VaultReaderTest {

    private static final String REAL_FILE = "lib/gamedata/vault.txt";

    /**
     * vault.txt's {@code record-count:} header, and the number of {@code name:} records in it.
     */
    private static final int EXPECTED_VAULTS = 162;

    /**
     * How many records carry a {@code flags:} line; all 35 carry {@code FEW_ENTRANCES}.
     */
    private static final int EXPECTED_FLAGGED = 35;

    /**
     * How many records declare {@code max-depth:0}, meaning "no maximum"; the other 33 declare a
     * real cap.
     */
    private static final int EXPECTED_UNCAPPED = 129;

    /**
     * A minimal well-formed record, used as the clean neighbour in the error fixtures so each of
     * those can assert that one bad record costs only itself.
     */
    private static final String GOOD_RECORD = String.join("\n",
            "name:Good vault",
            "type:Lesser vault",
            "rating:5",
            "rows:3",
            "columns:3",
            "min-depth:0",
            "max-depth:0",
            "D:###",
            "D:#.#",
            "D:###");

    @TempDir
    Path tempDir;

    /**
     * @param vaults the assembled vaults
     * @param name   the vault name to look for
     * @return the vault with that name
     * @throws java.util.NoSuchElementException if no vault has that name
     */
    private static Vault byName(List<Vault> vaults, String name) {
        return vaults.stream().filter(v -> v.getName().equals(name)).findFirst().orElseThrow();
    }

    /**
     * {@code max-depth:} is rewritten to the world's maximum depth when the file says 0, so the
     * assembler needs the game constants loaded.
     */
    @BeforeAll
    static void bootstrap() {
        GameConstants.init();
    }

    /**
     * {@link GameConstants#init()} populates the shared object-kind registries in place; reset them
     * to the empty baseline so this heavy load does not leak into order-sensitive suites (matching
     * {@code PitReaderTest}'s and {@code MonsterReaderTest}'s cleanup).
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
    }

    /**
     * @return the names of the vaults that survived assembly, in file order
     */
    private static List<String> names(ParseResult<Vault> result) {
        return result.items().stream().map(Vault::getName).toList();
    }

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    @Test
    void parsesEveryVaultWithoutError() throws Exception {
        ParseResult<Vault> result = new VaultReader().parseWithResults(REAL_FILE);

        assertTrue(result.errors().isEmpty(), () -> "unexpected errors: " + result.errors());
        assertEquals(EXPECTED_VAULTS, result.items().size());
    }

    @Test
    void readsTheFirstVaultsFields() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        Vault round = vaults.getFirst();
        assertEquals("Round", round.getName());
        assertEquals(RoomType.LESSER_VAULT, round.getType());
        assertEquals(5, round.getRating());
        assertEquals(12, round.getHeight());
        assertEquals(20, round.getWidth());
        assertEquals(0, round.getMinLevel());
        assertTrue(round.getFlags().isEmpty());
    }

    /**
     * C's {@code parse_vault_max_depth} turns a declared 0 into {@code z_info->max_depth}
     * ({@code [C] src/generate.c:561}), so no assembled vault may keep a 0 maximum.
     *
     * <p>129 of the 162 records declare 0 and so come out at the world maximum; the other 33
     * declare a real cap and must keep it untouched.
     */
    @Test
    void rewritesAZeroMaxDepthToTheWorldMaximum() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertNotEquals(0, vault.getMaxLevel(), vault.getName() + " kept a 0 maximum");
        }

        long atWorldMaximum = vaults.stream()
                .filter(v -> v.getMaxLevel() == GameConstants.getWorldMaxDepth())
                .count();
        assertEquals(EXPECTED_UNCAPPED, atWorldMaximum);

        Vault hatchery = byName(vaults, "Baby dragon hatchery -ES-");
        assertEquals(16, hatchery.getMaxLevel(), "a declared maximum must survive the rewrite");
        assertEquals(8, hatchery.getMinLevel());
    }

    /**
     * The layout is stored twice: as one line per row, and as the rows concatenated into the flat
     * string C keeps in {@code vault.text} and indexes as {@code text[y * wid + x]}. Both must agree
     * with the declared {@code rows:}/{@code columns:} - the port of the length check in
     * {@code parse_vault_d} ({@code [C] src/generate.c:588}).
     */
    @Test
    void everyLayoutMatchesItsDeclaredDimensions() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertEquals(vault.getHeight(), vault.getMap().size(), vault.getName() + " row count");
            for (String row : vault.getMap()) {
                assertEquals(vault.getWidth(), row.length(), vault.getName() + " row width");
            }
            assertEquals(vault.getHeight() * vault.getWidth(), vault.getMapLines().length(),
                    vault.getName() + " flat layout length");
        }
    }

    /**
     * No vault may be larger than the cap its room type carries from {@code list-rooms.h}, the
     * check C makes in {@code parse_vault_rows}/{@code parse_vault_columns}
     * ({@code [C] src/generate.c:521, 540}).
     */
    @Test
    void noVaultExceedsItsRoomTypeSizeCap() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertTrue(vault.getHeight() <= vault.getType().getMaxHeight(),
                    vault.getName() + " is taller than " + vault.getType().getName() + " allows");
            assertTrue(vault.getWidth() <= vault.getType().getMaxWidth(),
                    vault.getName() + " is wider than " + vault.getType().getName() + " allows");
        }
    }

    @Test
    void readsTheFlagsLines() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        long flagged = vaults.stream().filter(v -> !v.getFlags().isEmpty()).count();
        assertEquals(EXPECTED_FLAGGED, flagged);

        Vault cross = byName(vaults, "Cross");
        assertTrue(cross.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(1, cross.getFlags().count());
    }

    /**
     * Every vault type named in the file must resolve to a {@link RoomType}; C treats a name it
     * cannot match as {@code PARSE_ERROR_NO_ROOM_FOUND}.
     */
    @Test
    void resolvesEveryVaultType() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertNotNull(vault.getType(), vault.getName());
        }
    }

    // ---- Fixtures: the error paths ---------------------------------------

    /**
     * The fixture the error tests build on loads cleanly by itself — so a failure below is the
     * injected defect, not the fixture.
     */
    @Test
    void theFixtureRecordLoadsCleanly() throws IOException {
        String path = tempFile("good.txt", "record-count:1\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Vault good = result.items().getFirst();
        assertEquals("Good vault", good.getName());
        assertEquals(RoomType.LESSER_VAULT, good.getType());
        assertEquals("####.#" + "###", good.getMapLines());
        assertEquals(GameConstants.getWorldMaxDepth(), good.getMaxLevel());
    }

    /**
     * A {@code type:} matching none of the seven room-builder names — C's
     * {@code PARSE_ERROR_NO_ROOM_FOUND} ({@code [C] src/generate.c:517}). Soft here: the record is
     * dropped and its neighbour still loads, where C would fail the whole file.
     */
    @Test
    void anUnknownVaultTypeDropsOnlyThatRecord() throws IOException {
        String badType = GOOD_RECORD.replace("name:Good vault", "name:Bad type")
                .replace("type:Lesser vault", "type:Enormous vault");
        String path = tempFile("bad-type.txt",
                "record-count:2\n" + badType + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("an invalid vault type: Enormous vault")),
                result.errors()::toString);
    }

    /**
     * The type name is matched case-insensitively, so the data file's own capitalisation is not
     * load-bearing — worth pinning, since {@code toUpperCase()} is the only reason
     * {@code "lesser vault"} works.
     */
    @Test
    void theVaultTypeNameIsMatchedCaseInsensitively() throws IOException {
        String lower = GOOD_RECORD.replace("type:Lesser vault", "type:lesser vault");
        String path = tempFile("lower-type.txt", "record-count:1\n" + lower + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(RoomType.LESSER_VAULT, result.items().getFirst().getType());
    }

    /**
     * {@code rows:} outside {@code 1..}{@link RoomType#getMaxHeight()} — C's
     * {@code PARSE_ERROR_VAULT_TOO_BIG} in {@code parse_vault_rows}
     * ({@code [C] src/generate.c:521}). The bound is per type, so a lesser vault is capped at 22;
     * the check is {@code <= 0 || > max}, so a zero is rejected at the other end.
     */
    @Test
    void rowsOutsideTheTypesBoundsAreRejectedAtBothEnds() throws IOException {
        String tooTall = GOOD_RECORD.replace("name:Good vault", "name:Too tall")
                .replace("rows:3", "rows:23");
        String noRows = GOOD_RECORD.replace("name:Good vault", "name:No rows")
                .replace("rows:3", "rows:0");
        String path = tempFile("bad-rows.txt",
                "record-count:3\n" + tooTall + "\n" + noRows + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("an invalid row value")).count(),
                result.errors()::toString);
    }

    /**
     * The same bound on the other axis ({@code parse_vault_columns},
     * {@code [C] src/generate.c:540}).
     */
    @Test
    void columnsOutsideTheTypesBoundsAreRejectedAtBothEnds() throws IOException {
        String tooWide = GOOD_RECORD.replace("name:Good vault", "name:Too wide")
                .replace("columns:3", "columns:23");
        String noColumns = GOOD_RECORD.replace("name:Good vault", "name:No columns")
                .replace("columns:3", "columns:0");
        String path = tempFile("bad-columns.txt",
                "record-count:3\n" + tooWide + "\n" + noColumns + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("an invalid column value")).count(),
                result.errors()::toString);
    }

    /**
     * The bound is read off the record's own {@code type:}, not a global — 23 rows is too many for
     * a lesser vault but well inside an interesting room's 40. This is what makes the type having
     * to resolve first load-bearing rather than incidental.
     */
    @Test
    void theSizeCapComesFromTheRecordsOwnType() throws IOException {
        String interesting = GOOD_RECORD.replace("name:Good vault", "name:Tall interesting room")
                .replace("type:Lesser vault", "type:Interesting room")
                .replace("rows:3", "rows:23")
                .replace("D:###\nD:#.#\nD:###", "D:###\n".repeat(22) + "D:###");
        String path = tempFile("interesting.txt", "record-count:1\n" + interesting + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Vault tall = result.items().getFirst();
        assertEquals(RoomType.INTERESTING, tall.getType());
        assertEquals(23, tall.getHeight());
    }

    /**
     * A {@code D:} line whose length disagrees with {@code columns:} — C checks this line by line
     * as it parses ({@code strlen(desc) != v->wid}, {@code [C] src/generate.c:588}). Here it is one
     * report for the record however many rows are wrong, since the assembler only flags the map.
     */
    @Test
    void aRaggedMapDropsTheRecord() throws IOException {
        String ragged = GOOD_RECORD.replace("name:Good vault", "name:Ragged")
                .replace("D:#.#", "D:#..#");
        String path = tempFile("ragged.txt", "record-count:2\n" + ragged + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("a bad map")),
                result.errors()::toString);
    }

    /**
     * The row-count half of the same check: the map is rectangular, but there are fewer {@code D:}
     * lines than {@code rows:} promises. C never checks this at all — a truncated record there
     * leaves the tail of {@code vault.text} unwritten.
     */
    @Test
    void tooFewMapRowsDropsTheRecord() throws IOException {
        String shortMap = GOOD_RECORD.replace("name:Good vault", "name:Short")
                .replace("D:#.#\nD:###", "D:#.#");
        String path = tempFile("short-map.txt",
                "record-count:2\n" + shortMap + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("a bad map")),
                result.errors()::toString);
    }

    /**
     * An unknown flag name. Every bad flag in the record is reported before the record is skipped,
     * so one run covers all of its problems rather than one per re-run.
     */
    @Test
    void unknownFlagsDropTheRecordAndReportEveryBadFlag() throws IOException {
        String badFlags = GOOD_RECORD.replace("name:Good vault", "name:Bad flags")
                .replace("max-depth:0", "max-depth:0\nflags:NOT_A_FLAG|ALSO_NOT_A_FLAG");
        String path = tempFile("bad-flags.txt",
                "record-count:2\n" + badFlags + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("an invalid flag")).count(),
                result.errors()::toString);
    }

    /**
     * Several {@code flags:} lines OR into one set rather than the last one winning — the file's
     * own header says so, and C appends ({@code [C] src/generate.c:565}). {@link RoomFlags} carries
     * only the one real flag, so accumulation shows up two ways: repeating it is idempotent (a
     * {@code Flag} is a set), and a bad name on the <em>second</em> line is still reported, which is
     * what proves the line was read at all rather than overwritten.
     */
    @Test
    void everyFlagsLineIsRead() throws IOException {
        String repeated = GOOD_RECORD.replace("max-depth:0",
                "max-depth:0\nflags:FEW_ENTRANCES\nflags:FEW_ENTRANCES");
        String secondLineBad = GOOD_RECORD.replace("name:Good vault", "name:Second line bad")
                .replace("max-depth:0", "max-depth:0\nflags:FEW_ENTRANCES\nflags:NOT_A_FLAG");

        ParseResult<Vault> both = new VaultReader().parseWithResults(
                tempFile("repeated-flags.txt", "record-count:1\n" + repeated + "\n"));
        assertFalse(both.hasErrors(), () -> both.errors().toString());
        assertEquals(1, both.items().getFirst().getFlags().count());
        assertTrue(both.items().getFirst().getFlags().has(RoomFlags.FEW_ENTRANCES));

        ParseResult<Vault> bad = new VaultReader().parseWithResults(
                tempFile("second-flag-line-bad.txt",
                        "record-count:2\n" + secondLineBad + "\n" + GOOD_RECORD + "\n"));
        assertEquals(List.of("Good vault"), names(bad));
        assertTrue(bad.errors().stream().anyMatch(e -> e.contains("an invalid flag: NOT_A_FLAG")),
                bad.errors()::toString);
    }

    /**
     * The integer directives are lexed as {@code INTEGER}, so the only way to reach the
     * assembler's {@code NumberFormatException} arms is a value too large for an {@code int}.
     */
    @Test
    void anOverflowingIntegerDropsTheRecord() throws IOException {
        String huge = GOOD_RECORD.replace("name:Good vault", "name:Huge rating")
                .replace("rating:5", "rating:99999999999");
        String path = tempFile("overflow.txt", "record-count:2\n" + huge + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Good vault"), names(result));
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("an invalid rating integer: 99999999999")),
                result.errors()::toString);
    }

    /**
     * A wrong {@code record-count:} is soft: reported, but the records that parsed still load. C's
     * own parser never validates this header at all, so this check is stricter than the original.
     */
    @Test
    void recordCountMismatchIsReportedButVaultsStillLoad() throws IOException {
        String path = tempFile("bad-count.txt", "record-count:7\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(1, result.items().size(), "the count check must not cost the file its records");
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 7") && e.contains("contains 1")),
                result.errors()::toString);
    }

    /**
     * Every directive except {@code flags:} is mandatory and the grammar fixes their order, so a
     * record missing one is a hard error and the whole file fails closed — no partial results.
     */
    @Test
    void aMissingMandatoryDirectiveFailsClosed() throws IOException {
        String noRating = GOOD_RECORD.replace("rating:5\n", "");
        String path = tempFile("no-rating.txt",
                "record-count:2\n" + noRating + "\n" + GOOD_RECORD + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * Order is fixed too, not merely conventional: the same seven directives in a different
     * sequence is a grammar error, where C's parser would accept them in any order.
     */
    @Test
    void directivesOutOfOrderFailClosed() throws IOException {
        String swapped = GOOD_RECORD.replace("rating:5\nrows:3", "rows:3\nrating:5");
        String path = tempFile("out-of-order.txt", "record-count:1\n" + swapped + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * Inside a {@code D:} line a leading {@code #} is granite, not a comment — the imported
     * {@code CommentsAndEol} rules apply only in the default lexer mode, never inside
     * {@code REST_OF_LINE}. Trailing spaces survive for the same reason: they are what pads a short
     * row out to the declared width.
     */
    @Test
    void aMapRowKeepsItsHashesAndItsTrailingPadding() throws IOException {
        String padded = GOOD_RECORD.replace("D:#.#", "D:#. ");
        String path = tempFile("padded.txt", "# a real comment\nrecord-count:1\n" + padded + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Vault vault = result.items().getFirst();
        assertEquals(List.of("###", "#. ", "###"), vault.getMap());
        assertEquals("####. ###", vault.getMapLines());
    }

    /**
     * {@link VaultReader#parse} is {@link VaultReader#parseWithResults} with the messages dropped:
     * same items, no way to see what went wrong.
     */
    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        VaultReader reader = new VaultReader();

        List<Vault> viaParse = reader.parse(REAL_FILE);
        List<Vault> viaResults = reader.parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.size(), viaParse.size());
        assertEquals(viaResults.stream().map(Vault::getName).toList(),
                viaParse.stream().map(Vault::getName).toList());
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new VaultReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }
}
