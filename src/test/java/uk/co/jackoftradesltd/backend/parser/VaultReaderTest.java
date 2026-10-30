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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link VaultReader}: file text -> {@code VaultLexer}/
 * {@code VaultGrammar} -> {@code VaultParseRecord} -> {@code VaultAssembler} -> {@link Vault}.
 *
 * <p>Expected values are taken from {@code lib/gamedata/vault.txt} itself and from the C parser it
 * ports, {@code init_parse_vault} in {@code generate.c}.
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
     * A minimal well-formed record, the base the fixtures below edit one directive of, and the
     * clean neighbour in the error fixtures, which a bad record now takes down with it as in C.
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
    static void bootstrap() throws IOException {
        // Mirrors UILoop's EVENT_ENTER_INIT handler: the real init chain now blocks partway
        // through GameConstants.init() for this ack, so the front end's UIEntry load has to
        // actually happen before it is sent, or the assemblers below find an empty UIRegistry.
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    /**
     * {@code GameConstants.init(CoreChannel)} populates the shared object-kind registries in place; reset them
     * to the empty baseline so this heavy load does not leak into order-sensitive suites (matching
     * {@code PitReaderTest}'s and {@code MonsterReaderTest}'s cleanup).
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
    }

    /**
     * @return the names of the vaults that survived assembly, in the list's order (C's reverse file order)
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

        // C prepends each record (parse_vault_name), so the file's first vault is the tail.
        Vault round = vaults.getLast();
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
     * ({@code generate.c}), so no assembled vault may keep a 0 maximum.
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
     * {@code parse_vault_d} ({@code generate.c}).
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
     * ({@code generate.c}).
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

        // vault.txt has two vaults called "Cross"; the flagged one is the Lesser vault.
        Vault cross = vaults.stream()
                .filter(v -> v.getName().equals("Cross") && v.getType() == RoomType.LESSER_VAULT)
                .findFirst().orElseThrow();
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
     * A {@code type:} matching none of the room-builder names in {@code list-rooms.h} — C's
     * {@code PARSE_ERROR_NO_ROOM_FOUND} ({@code generate.c}), which ends the whole load.
     */
    @Test
    void anUnknownVaultTypeEndsTheLoad() throws IOException {
        String badType = GOOD_RECORD.replace("name:Good vault", "name:Bad type")
                .replace("type:Lesser vault", "type:Enormous vault");
        String path = tempFile("bad-type.txt",
                "record-count:2\n" + badType + "\n" + GOOD_RECORD + "\n");

        assertThrows(DataFileParseException.class, () -> new VaultReader().parseWithResults(path));
    }

    /**
     * C matches the type with {@code streq}, which is case-sensitive, so {@code "lesser vault"} is
     * no room builder at all.
     */
    @Test
    void aLowerCaseVaultTypeEndsTheLoad() throws IOException {
        String lower = GOOD_RECORD.replace("type:Lesser vault", "type:lesser vault");
        String path = tempFile("lower-type.txt", "record-count:1\n" + lower + "\n");

        assertThrows(DataFileParseException.class, () -> new VaultReader().parseWithResults(path));
    }

    /**
     * {@code rows:} over {@link RoomType#getMaxHeight()} — C's {@code PARSE_ERROR_VAULT_TOO_BIG} in
     * {@code parse_vault_rows} ({@code generate.c}). The bound is per type, so a lesser vault is
     * capped at 22.
     */
    @Test
    void rowsOverTheTypesCapEndTheLoad() throws IOException {
        String tooTall = GOOD_RECORD.replace("name:Good vault", "name:Too tall")
                .replace("rows:3", "rows:23");
        String path = tempFile("bad-rows.txt", "record-count:1\n" + tooTall + "\n");

        assertThrows(DataFileParseException.class, () -> new VaultReader().parseWithResults(path));
    }

    /**
     * The same bound on the other axis ({@code parse_vault_columns}, {@code generate.c}).
     */
    @Test
    void columnsOverTheTypesCapEndTheLoad() throws IOException {
        String tooWide = GOOD_RECORD.replace("name:Good vault", "name:Too wide")
                .replace("columns:3", "columns:23");
        String path = tempFile("bad-columns.txt", "record-count:1\n" + tooWide + "\n");

        assertThrows(DataFileParseException.class, () -> new VaultReader().parseWithResults(path));
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
     * as it parses ({@code strlen(desc) != v->wid}, {@code generate.c}) and returns
     * {@code PARSE_ERROR_VAULT_DESC_WRONG_LENGTH}, which ends the load.
     */
    @Test
    void aRaggedMapEndsTheLoad() throws IOException {
        String ragged = GOOD_RECORD.replace("name:Good vault", "name:Ragged")
                .replace("D:#.#", "D:#..#");
        String path = tempFile("ragged.txt", "record-count:2\n" + ragged + "\n" + GOOD_RECORD + "\n");

        assertThrows(DataFileParseException.class, () -> new VaultReader().parseWithResults(path));
    }

    /**
     * Fewer {@code D:} lines than {@code rows:} promises. C never compares the two, so the record
     * loads with its declared height and a short map.
     */
    @Test
    void tooFewMapRowsLoadAsInC() throws IOException {
        String shortMap = GOOD_RECORD.replace("name:Good vault", "name:Short")
                .replace("D:#.#\nD:###", "D:#.#");
        String path = tempFile("short-map.txt", "record-count:1\n" + shortMap + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Vault vault = result.items().getFirst();
        assertEquals(3, vault.getHeight());
        assertEquals(List.of("###", "#.#"), vault.getMap());
    }

    /**
     * An unknown flag name — C's {@code PARSE_ERROR_INVALID_FLAG} from {@code parse_vault_flags},
     * which ends the load at the first bad name.
     */
    @Test
    void anUnknownFlagEndsTheLoad() throws IOException {
        String badFlags = GOOD_RECORD.replace("name:Good vault", "name:Bad flags")
                .replace("max-depth:0", "max-depth:0\nflags:NOT_A_FLAG|ALSO_NOT_A_FLAG");
        String path = tempFile("bad-flags.txt",
                "record-count:2\n" + badFlags + "\n" + GOOD_RECORD + "\n");

        assertThrows(DataFileParseException.class, () -> new VaultReader().parseWithResults(path));
    }

    /**
     * Several {@code flags:} lines OR into one set rather than the last one winning — the file's
     * own header says so, and C appends ({@code generate.c}). {@link RoomFlags} carries
     * only the one real flag, so accumulation shows up two ways: repeating it is idempotent (a
     * {@code Flag} is a set), and a bad name on the <em>second</em> line still ends the load, which
     * is what proves the line was read at all rather than overwritten.
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

        String badPath = tempFile("second-flag-line-bad.txt",
                "record-count:2\n" + secondLineBad + "\n" + GOOD_RECORD + "\n");
        assertThrows(DataFileParseException.class,
                () -> new VaultReader().parseWithResults(badPath));
    }

    /**
     * A rating too large for an {@code int}. C narrows {@code strtol}'s result into an {@code int},
     * which wraps; the port clamps it to {@link Integer#MAX_VALUE} instead, an accepted deviation.
     */
    @Test
    void anOverflowingRatingIsClamped() throws IOException {
        String huge = GOOD_RECORD.replace("name:Good vault", "name:Huge rating")
                .replace("rating:5", "rating:99999999999");
        String path = tempFile("overflow.txt", "record-count:1\n" + huge + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(Integer.MAX_VALUE, result.items().getFirst().getRating());
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

    // ---- Reader contract and the differences from C -----------------------

    /**
     * Parses a one-record file made from {@link #GOOD_RECORD} with {@code from} replaced by
     * {@code to}, with the {@code record-count:} header C's file does not carry.
     */
    private ParseResult<Vault> parseVariant(String fileName, String from, String to)
            throws IOException {
        String record = GOOD_RECORD.replace(from, to);
        return new VaultReader().parseWithResults(
                tempFile(fileName, "record-count:1\n" + record + "\n"));
    }

    /**
     * C's {@code parse_vault_name} points each new record's {@code next} at the previous head, so
     * {@code vaults} runs from the last record in {@code vault.txt} to the first. The list handed
     * back is in that order: "Golden Flower " is the final {@code name:} in the file.
     */
    @Test
    void theListRunsFromTheLastVaultInTheFileToTheFirst() throws IOException {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        assertEquals("Golden Flower ", vaults.getFirst().getName());
        assertEquals("Round", vaults.getLast().getName());
    }

    /**
     * Two records in one file come out second-first, whatever their names.
     */
    @Test
    void twoRecordsComeBackSecondFirst() throws IOException {
        String first = GOOD_RECORD.replace("name:Good vault", "name:First");
        String second = GOOD_RECORD.replace("name:Good vault", "name:Second");
        String path = tempFile("two.txt", "record-count:2\n" + first + "\n" + second + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertEquals(List.of("Second", "First"), names(result));
    }

    /**
     * A {@code str} directive takes the whole rest of the line, so the trailing space on
     * "Golden Flower " in the shipped file is part of the name, as it is in C.
     */
    @Test
    void aNameKeepsItsTrailingSpaceAndAnyColon() throws IOException {
        ParseResult<Vault> result = parseVariant("name.txt", "name:Good vault", "name:Odd: name ");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("Odd: name ", result.items().getFirst().getName());
    }

    /**
     * The count of each room-builder type in {@code vault.txt}, taken by counting the {@code type:}
     * lines of the upstream file: 14 + 15 + 27 lesser, medium and greater vaults, 12 + 15 + 13 of
     * their {@code (new)} variants, and 66 interesting rooms (162 in all).
     */
    @Test
    void theShippedFileHasTheExpectedNumberOfEachType() throws IOException {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        java.util.Map<RoomType, Long> counts = new java.util.EnumMap<>(RoomType.class);
        for (Vault vault : vaults) {
            counts.merge(vault.getType(), 1L, Long::sum);
        }
        assertEquals(14L, counts.get(RoomType.LESSER_VAULT));
        assertEquals(15L, counts.get(RoomType.MEDIUM_VAULT));
        assertEquals(27L, counts.get(RoomType.GREATER_VAULT));
        assertEquals(12L, counts.get(RoomType.NEW_LESSER_VAULT));
        assertEquals(15L, counts.get(RoomType.NEW_MEDIUM_VAULT));
        assertEquals(13L, counts.get(RoomType.NEW_GREATER_VAULT));
        assertEquals(66L, counts.get(RoomType.INTERESTING));
    }

    /**
     * The size caps at their exact edge, from {@code list-rooms.h}: a lesser vault may be 22 by 22
     * and no more, and C's test is {@code > max}, so the cap itself is accepted.
     */
    @Test
    void aVaultAtExactlyItsSizeCapIsAccepted() throws IOException {
        String row = "D:" + "#".repeat(22) + "\n";
        String record = String.join("\n", "name:Full size", "type:Lesser vault", "rating:1",
                "rows:22", "columns:22", "min-depth:0", "max-depth:0") + "\n" + row.repeat(22);
        String path = tempFile("full.txt", "record-count:1\n" + record);

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(22 * 22, result.items().getFirst().getMapLines().length());
    }

    /**
     * C reads {@code min-depth:} and {@code max-depth:} with {@code parser_getuint}, which refuses
     * a leading minus with {@code PARSE_ERROR_NOT_NUMBER}. The lexer's {@code INTEGER} is signed,
     * so the minus reaches the assembler, which ends the load as C does.
     */
    @Test
    void negativeDepthsEndTheLoadAsInC() {
        assertThrows(DataFileParseException.class,
                () -> parseVariant("neg-min.txt", "min-depth:0", "min-depth:-5"));
        assertThrows(DataFileParseException.class,
                () -> parseVariant("neg-max.txt", "max-depth:0", "max-depth:-5"));
    }

    /**
     * {@code rating:} is the one signed field: C reads it with {@code parser_getint}, so a negative
     * rating is legal there and here.
     */
    @Test
    void aNegativeRatingIsAccepted() throws IOException {
        ParseResult<Vault> result = parseVariant("neg-rating.txt", "rating:5", "rating:-3");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-3, result.items().getFirst().getRating());
    }

    /**
     * C's only test on {@code rows:} is {@code > max_height}, so {@code rows:0} loads there, and
     * the three {@code D:} lines are not counted against it. The same holds here.
     */
    @Test
    void zeroRowsLoadAsInC() throws IOException {
        ParseResult<Vault> result = parseVariant("zero-rows.txt", "rows:3", "rows:0");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(0, result.items().getFirst().getHeight());
    }

    /**
     * C accepts the directives in any order and any may be missing; here a {@code type:} that
     * arrives after {@code rows:} is a grammar error and the file yields nothing.
     */
    @Test
    void typeAfterRowsIsRefusedHereWhereCTakesItInAnyOrder() throws IOException {
        String reordered = GOOD_RECORD.replace("type:Lesser vault\nrating:5\nrows:3",
                "rating:5\nrows:3\ntype:Lesser vault");
        String path = tempFile("type-late.txt", "record-count:1\n" + reordered + "\n");

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    /**
     * A header and no records. C accepts an empty vault file and leaves {@code vaults} null; the
     * grammar wants at least one record, so the file is refused.
     */
    @Test
    void aFileWithNoVaultsIsRefusedHereWhereCLoadsAnEmptyList() throws IOException {
        ParseResult<Vault> result = new VaultReader().parseWithResults(
                tempFile("empty.txt", "record-count:0\n"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    /**
     * C's {@code parser_parse} skips leading whitespace on a line before it looks at it. The shared
     * lexer rules do not, so an indented directive is a lexer error and the file yields nothing.
     */
    @Test
    void anIndentedDirectiveIsRefusedHereWhereCSkipsTheIndent() throws IOException {
        ParseResult<Vault> result = parseVariant("indent.txt", "rating:5", "  rating:5");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    /**
     * C reads {@code uint} fields with {@code strtoul} in base zero, so {@code rows:0x3} is 3 there.
     * The lexer is decimal-only, so it is refused here.
     */
    @Test
    void aHexadecimalRowCountIsRefusedHereWhereCReadsBaseZero() throws IOException {
        ParseResult<Vault> result = parseVariant("hex.txt", "rows:3", "rows:0x3");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    /**
     * C splits the flag list on spaces and bars alike ({@code strtok(s, " |")}), so spaces around
     * the bar are separators. The lexer's {@code OR} rule takes an optional space either side.
     */
    @Test
    void spacesAroundTheBarSeparateFlags() throws IOException {
        ParseResult<Vault> result = parseVariant("flag-spaces.txt", "max-depth:0",
                "max-depth:0\nflags:FEW_ENTRANCES | FEW_ENTRANCES");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().getFirst().getFlags().has(RoomFlags.FEW_ENTRANCES));
    }

    /**
     * The upstream file ends without a final newline after its last {@code D:} line; the lexer's
     * {@code REST_OF_LINE} mode ends at end of file instead.
     */
    @Test
    void aFinalMapRowWithoutANewlineStillLoads() throws IOException {
        String path = tempFile("no-newline.txt", "record-count:1\n" + GOOD_RECORD);

        ParseResult<Vault> result = new VaultReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("###", result.items().getFirst().getMap().getLast());
    }

    /**
     * A hard grammar error fails closed: {@link VaultReader#parse} hands back an empty list, the
     * same as for a file with no vaults, while {@link VaultReader#parseWithResults} says why.
     */
    @Test
    void parseCannotTellAHardErrorFromAnEmptyFile() throws IOException {
        String path = tempFile("garbage.txt", "record-count:1\nthis is not a vault\n");
        VaultReader reader = new VaultReader();

        assertTrue(reader.parse(path).isEmpty());
        assertTrue(reader.parseWithResults(path).hasErrors());
    }
}
