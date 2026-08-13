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
import uk.co.jackoftrades.middle.cave.enums.RoomFlags;
import uk.co.jackoftrades.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftrades.middle.cave.roombuilders.RoomType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link RoomProfileReader}: {@code room_template.txt} text ->
 * {@code RoomProfileLexer}/{@code RoomProfileGrammar} -> {@code RoomProfileParseRecord} ->
 * {@code RoomProfileAssembler} -> {@link RoomTemplate}.
 *
 * <p>Assertions are on the assembled {@link RoomTemplate}s, because the assembler is where the
 * validation C spreads across {@code parse_room_height}/{@code parse_room_width}/
 * {@code parse_room_tval}/{@code parse_room_flags}/{@code parse_room_d} lands: the size bounds are
 * checked against {@link RoomType#TEMPLATE}, {@code tval:} is resolved either numerically or by
 * name, and the {@code D:} lines are both checked against {@code rows:}/{@code columns:} and
 * concatenated into the single {@code mapText} buffer C builds with {@code string_appends}.
 *
 * <p>The happy path runs the real shipped file — 500 records, every one of them clean. The error
 * paths build one-defect fixtures: each is a <em>soft</em> error, so the bad record is dropped and
 * the rest of the file still loads (the partial-results contract), where C would fail the parse.
 *
 * @author Rowan Crowther
 */
class RoomProfileReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/room_template.txt";

    /**
     * The file's {@code record-count:} header, and the number of {@code name:} records in it.
     */
    private static final int EXPECTED_TEMPLATES = 500;

    /**
     * A minimal well-formed record body, used as the clean neighbour in the error fixtures so each
     * of those can assert that one bad record costs only itself.
     */
    private static final String GOOD_RECORD = String.join("\n",
            "name:Good room",
            "type:1",
            "rating:1",
            "rows:3",
            "columns:3",
            "doors:1",
            "tval:0",
            "D:%%%",
            "D:%.%",
            "D:%%%");

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

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllTemplates() throws IOException {
        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(EXPECTED_TEMPLATES, result.items().size());

        // File order is preserved.
        assertEquals("Tiny hidden room", result.items().getFirst().getName());
        assertEquals("Rod chamber", result.items().getLast().getName());
    }

    /**
     * The first record, pinned field by field — the plainest shape in the file: numeric
     * {@code tval:0}, no {@code flags:} line, a square map.
     */
    @Test
    void theFirstTemplateResolvesEveryField() throws IOException {
        RoomTemplate tiny = new RoomProfileReader().parseWithResults(REAL_FILE).items().getFirst();

        assertEquals("Tiny hidden room", tiny.getName());
        assertEquals(1, tiny.getType());
        assertEquals(1, tiny.getRating());
        assertEquals(7, tiny.getHeight());
        assertEquals(7, tiny.getWidth());
        assertEquals(4, tiny.getDoors());
        // tval:0 goes down the numeric path: TValue.values()[0].
        assertEquals(TValue.TV_NONE, tiny.getTval());
        assertFalse(tiny.getFlags().has(RoomFlags.FEW_ENTRANCES), "no flags: line on this record");

        assertEquals(7, tiny.getMap().size());
        assertEquals("%%#%#%%", tiny.getMap().getFirst());
        assertEquals("%%#%#%%", tiny.getMap().getLast());
        // mapText is the rows concatenated with nothing between them, matching C's parse_room_d;
        // the row boundaries are re-derived from rows/columns rather than stored in the text.
        assertEquals(49, tiny.getMapText().length());
        assertEquals(String.join("", tiny.getMap()), tiny.getMapText());
    }

    /**
     * The last record exercises the two legs the first one does not: a named (rather than numeric)
     * {@code tval:} and a {@code flags:} line.
     */
    @Test
    void aNamedTvalAndAFlagsLineBothResolve() throws IOException {
        RoomTemplate rod = new RoomProfileReader().parseWithResults(REAL_FILE).items().getLast();

        assertEquals("Rod chamber", rod.getName());
        assertEquals(3, rod.getRating());
        // "rod" is not a number, so the assembler falls back to name resolution.
        assertEquals(TValue.TV_ROD, rod.getTval());
        assertTrue(rod.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(11, rod.getMap().size());
        assertEquals(32, rod.getWidth());
    }

    /**
     * A multi-word tval name ({@code "magic book"}) resolves as well as a single-word one — the name
     * is matched whole rather than tokenised.
     */
    @Test
    void aMultiWordTvalNameResolves() throws IOException {
        List<RoomTemplate> templates = new RoomProfileReader().parseWithResults(REAL_FILE).items();

        assertTrue(templates.stream().anyMatch(t -> t.getTval() == TValue.TV_MAGIC_BOOK),
                "room_template.txt has a 'tval:magic book' record");
        assertTrue(templates.stream().anyMatch(t -> t.getTval() == TValue.TV_PRAYER_BOOK),
                "room_template.txt has a 'tval:prayer book' record");
    }

    /**
     * Every record in the file satisfies the invariants the assembler enforces, so this is the
     * assertion that {@link RoomType#TEMPLATE}'s bounds still agree with the shipped data: no map is
     * ragged, none is the wrong height, and none exceeds 11x33.
     */
    @Test
    void everyTemplateInTheRealFileIsWithinBoundsAndRectangular() throws IOException {
        List<RoomTemplate> templates = new RoomProfileReader().parseWithResults(REAL_FILE).items();

        for (RoomTemplate template : templates) {
            assertTrue(template.getHeight() >= 1 && template.getHeight() <= RoomType.TEMPLATE.getMaxHeight(),
                    () -> template.getName() + " has height " + template.getHeight());
            assertTrue(template.getWidth() >= 1 && template.getWidth() <= RoomType.TEMPLATE.getMaxWidth(),
                    () -> template.getName() + " has width " + template.getWidth());
            assertEquals(template.getHeight(), template.getMap().size(),
                    () -> template.getName() + " has the wrong number of map rows");
            for (String row : template.getMap()) {
                assertEquals(template.getWidth(), row.length(),
                        () -> template.getName() + " has a ragged map row: '" + row + "'");
            }
        }
    }

    // ---- Fixtures: the error paths ---------------------------------------

    /**
     * The fixture used by the error tests loads cleanly on its own — so a failure below is the
     * injected defect, not the fixture.
     */
    @Test
    void theFixtureRecordLoadsCleanly() throws IOException {
        String path = tempFile("good.txt", "record-count:1\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        RoomTemplate good = result.items().getFirst();
        assertEquals("Good room", good.getName());
        assertEquals("%%%%.%%%%", good.getMapText());
    }

    /**
     * {@code rows:} beyond {@link RoomType#TEMPLATE}'s max height — C's
     * {@code PARSE_ERROR_VAULT_TOO_BIG} in {@code parse_room_height}. Soft here: the oversized
     * record is dropped, its neighbour still loads.
     */
    @Test
    void rowsBeyondTheTemplateMaximumDropsOnlyThatRecord() throws IOException {
        String tooTall = GOOD_RECORD.replace("name:Good room", "name:Too tall")
                .replace("rows:3", "rows:12");
        String path = tempFile("too-tall.txt", "record-count:2\n" + tooTall + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(List.of("Good room"), result.items().stream().map(RoomTemplate::getName).toList());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("row count outside room template size")),
                result.errors()::toString);
    }

    /**
     * The same bound on the other axis ({@code parse_room_width}), and the same for a zero: the
     * check is {@code < 1 || > max}, so an empty room is rejected too.
     */
    @Test
    void columnsOutsideTheTemplateBoundsAreRejectedAtBothEnds() throws IOException {
        String tooWide = GOOD_RECORD.replace("name:Good room", "name:Too wide")
                .replace("columns:3", "columns:34");
        String zeroWide = GOOD_RECORD.replace("name:Good room", "name:No width")
                .replace("columns:3", "columns:0");
        String path = tempFile("bad-columns.txt",
                "record-count:3\n" + tooWide + "\n" + zeroWide + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(List.of("Good room"), result.items().stream().map(RoomTemplate::getName).toList());
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("column count outside room template size")).count(),
                result.errors()::toString);
    }

    /**
     * A {@code D:} line whose length disagrees with {@code columns:}. Every bad row is reported
     * before the record is skipped, rather than bailing on the first one.
     */
    @Test
    void aRaggedMapRowDropsTheRecordAndReportsEveryBadRow() throws IOException {
        String ragged = String.join("\n",
                "name:Ragged",
                "type:1",
                "rating:1",
                "rows:3",
                "columns:3",
                "doors:1",
                "tval:0",
                "D:%%%%",
                "D:%.%",
                "D:%%");
        String path = tempFile("ragged.txt", "record-count:2\n" + ragged + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(List.of("Good room"), result.items().stream().map(RoomTemplate::getName).toList());
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("map line of the wrong length")).count(),
                result.errors()::toString);
    }

    /**
     * The row-count check: the map is rectangular, but there are fewer {@code D:} lines than
     * {@code rows:} promises.
     */
    @Test
    void tooFewMapRowsDropsTheRecord() throws IOException {
        String shortMap = String.join("\n",
                "name:Short",
                "type:1",
                "rating:1",
                "rows:3",
                "columns:3",
                "doors:1",
                "tval:0",
                "D:%%%",
                "D:%%%");
        String path = tempFile("short-map.txt", "record-count:2\n" + shortMap + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(List.of("Good room"), result.items().stream().map(RoomTemplate::getName).toList());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("wrong number of rows in the map")),
                result.errors()::toString);
    }

    /**
     * A {@code tval:} that is neither a number in range nor a known name. Note the numeric leg is
     * tried first, so an out-of-range number falls out of {@code TValue.fromName(int)} as null
     * rather than being retried as a name.
     */
    @Test
    void anUnknownTvalDropsTheRecord() throws IOException {
        String badName = GOOD_RECORD.replace("name:Good room", "name:Bad tval name")
                .replace("tval:0", "tval:no such tval");
        String badNumber = GOOD_RECORD.replace("name:Good room", "name:Bad tval number")
                .replace("tval:0", "tval:9999");
        String path = tempFile("bad-tval.txt",
                "record-count:3\n" + badName + "\n" + badNumber + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(List.of("Good room"), result.items().stream().map(RoomTemplate::getName).toList());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid tValue: no such tval")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid tValue: 9999")),
                result.errors()::toString);
    }

    /**
     * An unknown flag name. As with the ragged map, every bad flag in the record is collected before
     * the record is skipped, so one report covers all of its problems.
     */
    @Test
    void anUnknownFlagDropsTheRecordAndReportsEveryBadFlag() throws IOException {
        String badFlags = GOOD_RECORD.replace("tval:0", "tval:0\nflags:NOT_A_FLAG|ALSO_NOT_A_FLAG")
                .replace("name:Good room", "name:Bad flags");
        String path = tempFile("bad-flags.txt", "record-count:2\n" + badFlags + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(List.of("Good room"), result.items().stream().map(RoomTemplate::getName).toList());
        assertEquals(2, result.errors().stream()
                        .filter(e -> e.contains("unknown room flag")).count(),
                result.errors()::toString);
    }

    /**
     * A wrong {@code record-count:} is soft: reported, but the records that parsed still load. C's
     * own parser never validates this header at all, so this check is stricter than the original.
     */
    @Test
    void recordCountMismatchIsReportedButTemplatesStillLoad() throws IOException {
        String path = tempFile("bad-count.txt", "record-count:7\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertEquals(1, result.items().size(), "the count check must not cost the file its records");
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 7") && e.contains("contains 1")),
                result.errors()::toString);
    }

    /**
     * The directives other than {@code flags:} are mandatory and ordered in the grammar, so a record
     * missing one is a hard error and the whole file fails closed.
     */
    @Test
    void aMissingMandatoryDirectiveFailsClosed() throws IOException {
        String noDoors = GOOD_RECORD.replace("doors:1\n", "");
        String path = tempFile("no-doors.txt", "record-count:2\n" + noDoors + "\n" + GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * A record with no {@code D:} lines at all is likewise a grammar error — {@code roomMap} needs
     * at least one.
     */
    @Test
    void aRecordWithNoMapFailsClosed() throws IOException {
        String noMap = String.join("\n",
                "name:No map", "type:1", "rating:1", "rows:3", "columns:3", "doors:1", "tval:0");
        String path = tempFile("no-map.txt", "record-count:1\n" + noMap + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * Inside a {@code D:} line a leading {@code #} is granite, not a comment — the imported
     * {@code CommentsAndEol} rules apply only in the default lexer mode, never inside
     * {@code REST_OF_LINE}.
     */
    @Test
    void aHashInAMapRowIsGraniteNotAComment() throws IOException {
        String hashMap = String.join("\n",
                "name:Hashes",
                "type:1",
                "rating:1",
                "rows:3",
                "columns:3",
                "doors:1",
                "tval:0",
                "D:###",
                "D:#.#",
                "D:###");
        String path = tempFile("hashes.txt", "# a real comment\nrecord-count:1\n" + hashMap + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        RoomTemplate hashes = result.items().getFirst();
        assertEquals(List.of("###", "#.#", "###"), hashes.getMap());
        assertEquals("####.####", hashes.getMapText());
    }

    /**
     * {@link Reader#parse} is {@link RoomProfileReader#parseWithResults} with the messages dropped:
     * same items, no way to see what went wrong.
     */
    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        RoomProfileReader reader = new RoomProfileReader();

        List<RoomTemplate> viaParse = reader.parse(REAL_FILE);
        List<RoomTemplate> viaResults = reader.parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.size(), viaParse.size());
        assertEquals(viaResults.stream().map(RoomTemplate::getName).toList(),
                viaParse.stream().map(RoomTemplate::getName).toList());
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new RoomProfileReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }
}
