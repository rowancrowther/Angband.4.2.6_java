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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

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
 * name, and the {@code D:} lines are concatenated, unchecked, into the single {@code mapText}
 * buffer C builds with {@code string_appends}.
 *
 * <p>The happy path runs the real shipped file — 500 records, every one of them clean. The error
 * paths build one-defect fixtures: each is fatal, as it is in C, so the assembler throws a
 * {@code DataFileParseException} out of the reader and no template is returned. The field-by-field
 * branches of the assembler itself are pinned in {@code RoomProfileAssemblerTest}.
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

        // C prepends each record (parse_room_name), so the list runs in reverse file order.
        assertEquals("Rod chamber", result.items().getFirst().getName());
        assertEquals("Tiny hidden room", result.items().getLast().getName());
    }

    /**
     * The first record in the file (the tail of C's reverse-ordered list), pinned field by field — the plainest shape in the file: numeric
     * {@code tval:0}, no {@code flags:} line, a square map.
     */
    @Test
    void theFirstTemplateResolvesEveryField() throws IOException {
        RoomTemplate tiny = new RoomProfileReader().parseWithResults(REAL_FILE).items().getLast();

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
     * The last record in the file (the head of C's reverse-ordered list) exercises the two legs the first one does not: a named (rather than numeric)
     * {@code tval:} and a {@code flags:} line.
     */
    @Test
    void aNamedTvalAndAFlagsLineBothResolve() throws IOException {
        RoomTemplate rod = new RoomProfileReader().parseWithResults(REAL_FILE).items().getFirst();

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
     * Every record in the file is well formed, although the assembler checks none of this itself, so
     * this is the assertion that {@link RoomType#TEMPLATE}'s bounds still agree with the shipped
     * data: no map is ragged, none is the wrong height, and none exceeds 11x33.
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
     * The assembler's refusal is a {@link DataFileParseException} that comes straight out of the
     * reader, naming the {@code name:} line of the bad record and the file. Every fixture puts the
     * bad record first, after the {@code record-count:} header, so that line is 2.
     */
    private void assertStopsTheLoad(String fileName, String content, String message) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new RoomProfileReader().parseWithResults(tempFile(fileName, content)));
        assertTrue(thrown.getMessage().contains(message), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 2"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(fileName), thrown::getMessage);
    }

    /**
     * {@code rows:} beyond {@link RoomType#TEMPLATE}'s max height — C's
     * {@code PARSE_ERROR_VAULT_TOO_BIG} in {@code parse_room_height}, which is fatal at start-up.
     * The load stops, and the good neighbour behind it is never reached.
     */
    @Test
    void rowsBeyondTheTemplateMaximumStopTheLoad() {
        String tooTall = GOOD_RECORD.replace("name:Good room", "name:Too tall")
                .replace("rows:3", "rows:12");

        assertStopsTheLoad("too-tall.txt", "record-count:2\n" + tooTall + "\n" + GOOD_RECORD + "\n",
                "illegal row count value (outside room template size) 12");
    }

    /**
     * The same bound on the other axis ({@code parse_room_width}). C refuses only a value above the
     * cap, so {@code columns:0} loads with a width of 0.
     */
    @Test
    void columnsAboveTheTemplateMaximumStopTheLoadAndZeroLoads() throws IOException {
        String tooWide = GOOD_RECORD.replace("name:Good room", "name:Too wide")
                .replace("columns:3", "columns:34");
        String zeroWide = GOOD_RECORD.replace("name:Good room", "name:No width")
                .replace("columns:3", "columns:0");

        assertStopsTheLoad("too-wide.txt", "record-count:1\n" + tooWide + "\n",
                "illegal column count value (outside room template size) 34");

        ParseResult<RoomTemplate> result = new RoomProfileReader()
                .parseWithResults(tempFile("zero-wide.txt", "record-count:1\n" + zeroWide + "\n"));
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(0, result.items().getFirst().getWidth());
    }

    /**
     * A {@code D:} line whose length disagrees with {@code columns:}. C's {@code parse_room_d}
     * makes no length check, so the layout loads, and {@code mapText} is the lines joined as
     * written.
     */
    @Test
    void aRaggedMapRowLoadsAsInC() throws IOException {
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
        String path = tempFile("ragged.txt", "record-count:1\n" + ragged + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        RoomTemplate template = result.items().getFirst();
        assertEquals(List.of("%%%%", "%.%", "%%"), template.getMap());
        assertEquals("%%%%%.%%%", template.getMapText());
    }

    /**
     * Fewer {@code D:} lines than {@code rows:} promises. C does not count them, so the record loads
     * with the height it declared and the lines it has.
     */
    @Test
    void tooFewMapRowsLoadAsInC() throws IOException {
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
        String path = tempFile("short-map.txt", "record-count:1\n" + shortMap + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        RoomTemplate template = result.items().getFirst();
        assertEquals(3, template.getHeight());
        assertEquals(2, template.getMap().size());
    }

    /**
     * A {@code tval:} that is neither a number in range nor a known name stops the load, as C's
     * {@code PARSE_ERROR_UNRECOGNISED_TVAL} does. The numeric leg is tried first, so an
     * out-of-range number is not retried as a name.
     */
    @Test
    void anUnknownTvalStopsTheLoad() {
        String badName = GOOD_RECORD.replace("tval:0", "tval:no such tval");
        String badNumber = GOOD_RECORD.replace("tval:0", "tval:9999");

        assertStopsTheLoad("bad-tval-name.txt", "record-count:1\n" + badName + "\n",
                "illegal tValue value: no such tval");
        assertStopsTheLoad("bad-tval-number.txt", "record-count:1\n" + badNumber + "\n",
                "illegal tValue value: 9999");
    }

    /**
     * An unknown flag name stops the load at the first bad name, as {@code parse_room_flags} stops
     * at the first name {@code grab_flag} rejects.
     */
    @Test
    void anUnknownFlagStopsTheLoadAtTheFirstBadName() {
        String badFlags = GOOD_RECORD.replace("tval:0", "tval:0\nflags:NOT_A_FLAG|ALSO_NOT_A_FLAG");

        assertStopsTheLoad("bad-flags.txt", "record-count:1\n" + badFlags + "\n",
                "illegal room flag value: NOT_A_FLAG");
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

    // ---- Where the grammar is stricter than C's parser --------------------
    //
    // C registers nine independent one-line directives in init_parse_room, so after a name:
    // they may come in any order, repeat, or be left out, and parser.c skips leading
    // whitespace, ignores anything after a numeric value, and reads a uint with strtoul in base
    // zero. RoomProfileGrammar fixes the order and count, so each case below is a whole-file
    // failure here (no items, one hard error) where C loads the record.

    /**
     * Runs {@code content} through the reader and asserts the whole file was refused: hard errors
     * and no templates at all.
     */
    private void assertWholeFileRefused(String fileName, String content) throws IOException {
        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(tempFile(fileName, content));

        assertTrue(result.hasErrors(), "expected a hard error");
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * C: {@code parse_room_type} and {@code parse_room_rating} are independent handlers, so
     * {@code rating:} before {@code type:} is fine. Here the directive order is fixed.
     */
    @Test
    void directivesInAnotherOrderAreRefusedHereWhereCAcceptsThem() throws IOException {
        String swapped = GOOD_RECORD.replace("type:1\nrating:1", "rating:1\ntype:1");

        assertWholeFileRefused("swapped.txt", "record-count:1\n" + swapped + "\n");
    }

    /**
     * C: {@code parse_room_flags} ORs into {@code t->flags}, so a second {@code flags:} line adds to
     * the first. The grammar takes at most one.
     */
    @Test
    void aRepeatedFlagsLineIsRefusedHereWhereCAccumulatesIt() throws IOException {
        String twice = GOOD_RECORD.replace("tval:0", "tval:0\nflags:FEW_ENTRANCES\nflags:FEW_ENTRANCES");

        assertWholeFileRefused("two-flags.txt", "record-count:1\n" + twice + "\n");
    }

    /**
     * C: {@code parse_room_flags} splits on {@code strtok(s, " |")}, so a space alone separates
     * flags. {@code FLAG_OR} in the lexer needs the {@code |}. The {@code |} form, with or without
     * spaces round it, is the control.
     */
    @Test
    void flagsSeparatedByASpaceAloneAreRefusedHereWhereCSplitsOnIt() throws IOException {
        String piped = GOOD_RECORD.replace("tval:0", "tval:0\nflags:FEW_ENTRANCES | FEW_ENTRANCES");
        String spaced = GOOD_RECORD.replace("tval:0", "tval:0\nflags:FEW_ENTRANCES FEW_ENTRANCES");

        ParseResult<RoomTemplate> control = new RoomProfileReader()
                .parseWithResults(tempFile("piped.txt", "record-count:1\n" + piped + "\n"));
        assertFalse(control.hasErrors(), () -> control.errors().toString());
        assertTrue(control.items().getFirst().getFlags().has(RoomFlags.FEW_ENTRANCES));

        assertWholeFileRefused("spaced.txt", "record-count:1\n" + spaced + "\n");
    }

    /**
     * C: {@code finish_parse_room} hands {@code room_templates} whatever the file produced, so a
     * file with no {@code name:} loads an empty list. The grammar wants one or more records.
     */
    @Test
    void aFileWithNoTemplatesIsRefusedHereWhereCLoadsAnEmptyList() throws IOException {
        assertWholeFileRefused("empty.txt", "record-count:0\n");
    }

    /**
     * C: {@code parser_parse} skips leading whitespace on a line before looking for the directive.
     * An indented directive is a lexer error here.
     */
    @Test
    void anIndentedDirectiveIsRefusedHereWhereCSkipsTheIndent() throws IOException {
        String indented = GOOD_RECORD.replace("rating:1", "  rating:1");

        assertWholeFileRefused("indented.txt", "record-count:1\n" + indented + "\n");
    }

    /**
     * C: {@code rows} is a {@code uint}, read with {@code strtoul} in base zero, so {@code 0x3} is
     * 3. The lexer's {@code INTEGER} is decimal only.
     */
    @Test
    void aHexadecimalRowCountIsRefusedHereWhereCReadsBaseZero() throws IOException {
        String hex = GOOD_RECORD.replace("rows:3", "rows:0x3");

        assertWholeFileRefused("hex.txt", "record-count:1\n" + hex + "\n");
    }

    /**
     * C: an {@code int} or {@code uint} field is tokenized on {@code :}, so {@code type:1:7} reads 1
     * and drops the rest. Here the extra field is a syntax error.
     */
    @Test
    void aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt() throws IOException {
        String extra = GOOD_RECORD.replace("type:1", "type:1:7");

        assertWholeFileRefused("extra-field.txt", "record-count:1\n" + extra + "\n");
    }

    /**
     * C: {@code parser_parse} skips a {@code #} comment with or without a final newline. The lexer's
     * {@code COMMENT} rule ends on {@code \n}, so a comment on the last line with no newline is a
     * lexer error.
     */
    @Test
    void aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt() throws IOException {
        assertWholeFileRefused("tail-comment.txt", "record-count:1\n" + GOOD_RECORD + "\n# the end");
    }

    /**
     * C: {@code tval} is a {@code sym}, tokenized on {@code :}, so {@code tval:rod:junk} reads
     * {@code rod}. The lexer hands the whole rest of the line to the assembler, which cannot resolve
     * {@code rod:junk}, so the load stops.
     */
    @Test
    void textAfterATvalNameIsTreatedAsPartOfTheNameHereWhereCDropsIt() {
        String extra = GOOD_RECORD.replace("tval:0", "tval:rod:junk");

        assertStopsTheLoad("tval-extra.txt", "record-count:1\n" + extra + "\n",
                "illegal tValue value: rod:junk");
    }

    /**
     * C: {@code tval_find_idx} accepts a number followed only by spaces ({@code contains_only_spaces}),
     * so {@code tval:0 } is tval 0. The port resolves it the same way.
     */
    @Test
    void aTvalNumberWithATrailingSpaceResolvesAsInC() throws IOException {
        String trailing = GOOD_RECORD.replace("tval:0", "tval:0 ");
        String path = tempFile("tval-space.txt", "record-count:1\n" + trailing + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(TValue.TV_NONE, result.items().getFirst().getTval());
    }

    /**
     * C: {@code type} and {@code doors} are {@code uint}, and {@code parser_parse} refuses a token
     * that starts with {@code -} ({@code PARSE_ERROR_NOT_NUMBER}). {@code INTEGER} takes a minus
     * sign, so the assembler is the one that refuses it, and the load stops.
     */
    @Test
    void aNegativeTypeOrDoorCountStopsTheLoadAsInC() {
        String negativeType = GOOD_RECORD.replace("type:1", "type:-1");
        String negativeDoors = GOOD_RECORD.replace("doors:1", "doors:-1");

        assertStopsTheLoad("negative-type.txt", "record-count:1\n" + negativeType + "\n",
                "illegal negative type value: -1");
        assertStopsTheLoad("negative-doors.txt", "record-count:1\n" + negativeDoors + "\n",
                "illegal negative doors value: -1");
    }

    /**
     * C: {@code typ}, {@code rat}, {@code hgt}, {@code wid} and {@code dor} are {@code uint8_t}, so
     * {@code type:257} is stored as 1. The assembler reduces the value modulo 256 the same way.
     */
    @Test
    void aTypeThatDoesNotFitAByteIsTruncatedAsInC() throws IOException {
        String big = GOOD_RECORD.replace("type:1", "type:257");
        String path = tempFile("big-type.txt", "record-count:1\n" + big + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().getFirst().getType());
    }

    // ---- Where the grammar matches C ----------------------------------------

    /**
     * C: {@code parse_room_d} uses {@code parser_getstr}, which for a {@code str} field is
     * {@code strtok(sp, "")}; an empty one is a null token and so
     * {@code PARSE_ERROR_MISSING_FIELD}. An empty {@code D:} line is refused here as well.
     */
    @Test
    void anEmptyMapLineFailsClosedAsInC() throws IOException {
        String empty = GOOD_RECORD.replace("D:%.%", "D:");

        assertWholeFileRefused("empty-d.txt", "record-count:1\n" + empty + "\n");
    }

    /**
     * C reads the last line of a file whether or not it ends in a newline; so does the lexer, so a
     * final {@code D:} line with no terminator still loads.
     */
    @Test
    void aFinalLineWithoutANewlineStillLoads() throws IOException {
        ParseResult<RoomTemplate> result = new RoomProfileReader()
                .parseWithResults(tempFile("no-eol.txt", "record-count:1\n" + GOOD_RECORD));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("%%%%.%%%%", result.items().getFirst().getMapText());
    }

    /**
     * {@code parse_room_name} points each new record's {@code next} at the previous head, so the
     * list runs backwards; two records in a fixture come out second then first.
     */
    @Test
    void theResultRunsInReverseFileOrderAsInC() throws IOException {
        String second = GOOD_RECORD.replace("name:Good room", "name:Second room");
        String path = tempFile("two.txt", "record-count:2\n" + GOOD_RECORD + "\n" + second + "\n");

        List<RoomTemplate> items = new RoomProfileReader().parseWithResults(path).items();

        assertEquals(List.of("Second room", "Good room"), items.stream().map(RoomTemplate::getName).toList());
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new RoomProfileReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }
}
