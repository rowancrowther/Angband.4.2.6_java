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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
     * The number of {@code name:} records in the real file.
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
        String path = tempFile("good.txt", GOOD_RECORD + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

        RoomTemplate good = result.items().getFirst();
        assertEquals("Good room", good.getName());
        assertEquals("%%%%.%%%%", good.getMapText());
    }

    /**
     * The assembler's refusal is a {@link DataFileParseException} that comes straight out of the
     * reader, naming the {@code name:} line of the bad record and the file. Every fixture puts the
     * bad record first, so that line is 1.
     */
    private void assertStopsTheLoad(String fileName, String content, String message) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new RoomProfileReader().parseWithResults(tempFile(fileName, content)));
        assertTrue(thrown.getMessage().contains(message), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 1"), thrown::getMessage);
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

        assertStopsTheLoad("too-tall.txt", tooTall + "\n" + GOOD_RECORD + "\n",
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

        assertStopsTheLoad("too-wide.txt", tooWide + "\n",
                "illegal column count value (outside room template size) 34");

        ParseResult<RoomTemplate> result = new RoomProfileReader()
                .parseWithResults(tempFile("zero-wide.txt", zeroWide + "\n"));
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
        String path = tempFile("ragged.txt", ragged + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

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
        String path = tempFile("short-map.txt", shortMap + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

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

        assertStopsTheLoad("bad-tval-name.txt", badName + "\n",
                "illegal tValue value: no such tval");
        assertStopsTheLoad("bad-tval-number.txt", badNumber + "\n",
                "illegal tValue value: 9999");
    }

    /**
     * An unknown flag name stops the load at the first bad name, as {@code parse_room_flags} stops
     * at the first name {@code grab_flag} rejects.
     */
    @Test
    void anUnknownFlagStopsTheLoadAtTheFirstBadName() {
        String badFlags = GOOD_RECORD.replace("tval:0", "tval:0\nflags:NOT_A_FLAG|ALSO_NOT_A_FLAG");

        assertStopsTheLoad("bad-flags.txt", badFlags + "\n",
                "illegal room flag value: NOT_A_FLAG");
    }

    /**
     * {@code NONE} is index 0 of C's {@code room_flags[]} table, and {@code lookup_flag} starts its
     * search at {@code FLAG_START}, past it, so {@code grab_flag} refuses it in
     * {@code parse_room_flags}. The load stops, whether {@code NONE} stands alone or follows a good
     * flag.
     */
    @Test
    void theSentinelNoneFlagStopsTheLoad() {
        String alone = GOOD_RECORD.replace("tval:0", "tval:0\nflags:NONE");
        String afterAGoodFlag = GOOD_RECORD.replace("tval:0", "tval:0\nflags:FEW_ENTRANCES|NONE");

        assertStopsTheLoad("none-flag.txt", alone + "\n",
                "illegal sentinel room flag value: NONE");
        assertStopsTheLoad("good-then-none-flag.txt", afterAGoodFlag + "\n",
                "illegal sentinel room flag value: NONE");
    }

    /**
     * C: every directive after {@code name:} is optional, because {@code parse_room_name} makes a
     * zeroed template and the other eight handlers fill in what they are given. A record with no
     * {@code doors:} loads with 0 doors, and the record after it is untouched.
     *
     * <p>Function aMissingDirectiveLoadsAsInC coded on 261119, commented in full on 261119. It
     * replaces aMissingMandatoryDirectiveFailsClosed.
     */
    @Test
    void aMissingDirectiveLoadsAsInC() throws IOException {
        String noDoors = GOOD_RECORD.replace("doors:1\n", "");
        String path = tempFile("no-doors.txt", noDoors + "\n" + GOOD_RECORD + "\n");

        List<RoomTemplate> items = new RoomProfileReader().parseWithResults(path).items();

        assertEquals(2, items.size());
        // The list runs in reverse file order, so the record without doors: is last.
        assertEquals(1, items.get(0).getDoors());
        assertEquals(0, items.get(1).getDoors());
        assertEquals("%%%%.%%%%", items.get(1).getMapText());
    }

    /**
     * C: a record with no {@code D:} lines has a null {@code text} and loads. Here the template
     * loads with an empty map list and an empty map text.
     *
     * <p>Function aRecordWithNoMapLoadsAsInC coded on 261119, commented in full on 261119. It
     * replaces aRecordWithNoMapFailsClosed.
     */
    @Test
    void aRecordWithNoMapLoadsAsInC() throws IOException {
        String noMap = String.join("\n",
                "name:No map", "type:1", "rating:1", "rows:3", "columns:3", "doors:1", "tval:0");
        String path = tempFile("no-map.txt", noMap + "\n");

        RoomTemplate template = new RoomProfileReader().parseWithResults(path).items().getFirst();

        assertEquals("No map", template.getName());
        assertEquals(List.of(), template.getMap());
        assertEquals("", template.getMapText());
    }

    /**
     * C: a record that is only a {@code name:} line is a zeroed template. Every number is 0, the
     * tval is {@code TV_NONE} and there are no flags and no map. In particular an absent
     * {@code rating:} is 0, not 255.
     *
     * <p>Function aNameOnlyRecordIsAZeroedTemplateAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void aNameOnlyRecordIsAZeroedTemplateAsInC() throws IOException {
        RoomTemplate template = new RoomProfileReader()
                .parseWithResults(tempFile("bare.txt", "name:Bare\n")).items().getFirst();

        assertEquals("Bare", template.getName());
        assertEquals(0, template.getType());
        assertEquals(0, template.getRating());
        assertEquals(0, template.getHeight());
        assertEquals(0, template.getWidth());
        assertEquals(0, template.getDoors());
        assertEquals(TValue.TV_NONE, template.getTval());
        assertFalse(template.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(List.of(), template.getMap());
    }

    /**
     * C: {@code rating:} left out leaves the zeroed template's 0, while the other fields of the
     * record are read as written.
     *
     * <p>Function anAbsentRatingIsZeroAsInC coded on 261119, commented in full on 261119.
     */
    @Test
    void anAbsentRatingIsZeroAsInC() throws IOException {
        RoomTemplate template = new RoomProfileReader()
                .parseWithResults(tempFile("no-rating.txt", "name:R\ntype:2\n")).items().getFirst();

        assertEquals(2, template.getType());
        assertEquals(0, template.getRating());
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
        String path = tempFile("hashes.txt", "# a real comment\n" + hashMap + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

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

    // ---- Directive order and count, and where Java still differs from C ----
    //
    // C registers nine independent one-line directives in init_parse_room, so after a name:
    // they may come in any order, repeat, or be left out, and parser.c skips leading
    // whitespace, ignores anything after a numeric value, and reads a uint with strtoul in base
    // zero. RoomProfileGrammar now takes the eight directives after name: in any order, each
    // optional and repeatable (Stage 7a), and the file may hold no records. The flags: separator
    // follows C's strtok(s, " |") (Stage 7d, tests below). The cases below that still end in a
    // whole-file failure (no items, one hard error) are the hexadecimal number and the field after
    // a value, which are lexer work in later stages.

    /**
     * Runs {@code content} through the reader and asserts the whole file was refused: hard errors
     * and no templates at all.
     */
    private void assertWholeFileRefused(String fileName, String content) throws IOException {
        assertThrows(ParseCancellationException.class, () -> new RoomProfileReader().parseWithResults(tempFile(fileName, content)), "expected a hard error");

    }

    /**
     * C: {@code parse_room_type} and {@code parse_room_rating} are independent handlers, so
     * {@code rating:} before {@code type:} is fine, and so is {@code rows:} ahead of both, with
     * the {@code D:} line before the rest.
     *
     * <p>Function directivesInAnotherOrderLoadAsInC coded on 261119, commented in full on 261119.
     * It replaces directivesInAnotherOrderAreRefusedHereWhereCAcceptsThem.
     */
    @Test
    void directivesInAnotherOrderLoadAsInC() throws IOException {
        String shuffled = String.join("\n",
                "name:Shuffled", "D:abc", "rows:1", "doors:4", "rating:5", "type:2", "columns:3", "tval:rod");

        RoomTemplate template = new RoomProfileReader()
                .parseWithResults(tempFile("shuffled.txt", shuffled + "\n")).items().getFirst();

        assertEquals(2, template.getType());
        assertEquals(5, template.getRating());
        assertEquals(1, template.getHeight());
        assertEquals(3, template.getWidth());
        assertEquals(4, template.getDoors());
        assertEquals(TValue.TV_ROD, template.getTval());
        assertEquals(List.of("abc"), template.getMap());
    }

    /**
     * C: a directive before the first {@code name:} has no template to fill in, which is
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}. The grammar needs {@code name:} first, so the file
     * is refused as well.
     *
     * <p>Function aDirectiveBeforeAnyNameIsRefusedAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void aDirectiveBeforeAnyNameIsRefusedAsInC() throws IOException {
        assertWholeFileRefused("no-header.txt", "type:2\n" + GOOD_RECORD + "\n");
    }

    /**
     * C: {@code parse_room_flags} ORs into {@code t->flags}, so a second {@code flags:} line adds to
     * the first. Two lines with different flags leave both set.
     *
     * <p>Function aRepeatedFlagsLineAccumulatesAsInC coded on 261119, commented in full on 261119.
     * It replaces aRepeatedFlagsLineIsRefusedHereWhereCAccumulatesIt.
     */
    @Test
    void aRepeatedFlagsLineAccumulatesAsInC() throws IOException {
        String twice = GOOD_RECORD.replace("tval:0", "tval:0\nflags:FEW_ENTRANCES\nflags:MAX");

        RoomTemplate template = new RoomProfileReader()
                .parseWithResults(tempFile("two-flags.txt", twice + "\n")).items().getFirst();

        assertTrue(template.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertTrue(template.getFlags().has(RoomFlags.MAX));
    }

    /**
     * C: {@code parse_room_d} appends each {@code D:} line to the one buffer whatever comes between
     * them, and a repeated {@code type:} is a plain assignment, so the last one stays.
     *
     * <p>Function dLinesSplitByOtherDirectivesStillAppendAsInC coded on 261119, commented in full
     * on 261119.
     */
    @Test
    void dLinesSplitByOtherDirectivesStillAppendAsInC() throws IOException {
        String split = "name:Split\nD:aa\ntype:3\nD:bb\ntype:7\n";

        RoomTemplate template = new RoomProfileReader()
                .parseWithResults(tempFile("split.txt", split)).items().getFirst();

        assertEquals(List.of("aa", "bb"), template.getMap());
        assertEquals("aabb", template.getMapText());
        assertEquals(7, template.getType());
    }

    /**
     * C: {@code parse_room_height}, {@code parse_room_type} and {@code parse_room_tval} test each
     * line as it is read, so a bad first value of a repeated directive stops the load even when a
     * good one follows. {@code RoomProfileGrammar} keeps only the last text of each directive, so
     * the assembler never sees the bad one and all three load here. Parked: the fix is one
     * {@code Directive} per line, as {@code VaultGrammar} does (see {@code docs/Parked.md}).
     * The control, a bad value last, stops the load as in C.
     *
     * <p>Function aBadFirstValueOfARepeatedDirectiveIsLostHereWhereCRefusesIt coded on 261119,
     * commented in full on 261119.
     */
    @Test
    void aBadFirstValueOfARepeatedDirectiveIsLostHereWhereCRefusesIt() throws IOException {
        RoomTemplate tooTall = new RoomProfileReader()
                .parseWithResults(tempFile("tall-then-ok.txt", "name:R\nrows:99\nrows:5\n")).items().getFirst();
        assertEquals(5, tooTall.getHeight());

        RoomTemplate negative = new RoomProfileReader()
                .parseWithResults(tempFile("neg-then-ok.txt", "name:R\ntype:-1\ntype:1\n")).items().getFirst();
        assertEquals(1, negative.getType());

        RoomTemplate unknown = new RoomProfileReader()
                .parseWithResults(tempFile("bogus-then-rod.txt", "name:R\ntval:bogus\ntval:rod\n")).items().getFirst();
        assertEquals(TValue.TV_ROD, unknown.getTval());

        assertStopsTheLoad("ok-then-tall.txt", "name:R\nrows:5\nrows:99\n", "illegal row count value");
    }

    // ---- flags: separators (Stage 7d) ----
    //
    // C: parse_room_flags in generate.c splits the value with strtok(s, " |"), so any run of
    // blanks and bars separates two names, and a run before the first name or after the last is
    // skipped. flags is a required str in init_parse_room, so an empty value is
    // PARSE_ERROR_MISSING_FIELD, while a value of blanks and bars alone reaches the handler, where
    // strtok finds no name and it loads with no flags.

    /**
     * Separators between two names, each one a run of blanks and bars that {@code strtok(s, " |")}
     * collapses into a single break.
     */
    private static final List<String> SEPARATORS = List.of(" ", "    ", "|", " |", "| ", " | ", "  |  ",
            "||", " | | ", "| |", " |  | ");

    /**
     * Runs of blanks and bars with no name in them, which {@code strtok} skips at either end of a
     * list.
     */
    private static final List<String> DELIMITER_RUNS = List.of(" ", "  ", "|", " | ", "||", " | | ", "|  |");

    /**
     * Loads a {@link #GOOD_RECORD} carrying {@code flagsLine} (no line end of its own) after its
     * {@code tval:} line, followed by a second, flagless record. Checks that both records loaded and
     * that the flagged record kept all three {@code D:} lines, so the {@code flags:} line ended
     * cleanly, then returns the flagged record. C prepends each record, so it is the last in the list.
     */
    private RoomTemplate withFlagsMidFile(String flagsLine) throws IOException {
        String body = GOOD_RECORD.replace("tval:0", "tval:0\n" + flagsLine) + "\n"
                + GOOD_RECORD.replace("Good room", "Second room") + "\n";

        List<RoomTemplate> items = new RoomProfileReader().parseWithResults(tempFile("flags-mid.txt", body)).items();

        assertEquals(2, items.size(), flagsLine);
        assertEquals("Second room", items.getFirst().getName(), flagsLine);
        assertFalse(items.getFirst().getFlags().has(RoomFlags.FEW_ENTRANCES), flagsLine);
        assertEquals("%%%%.%%%%", items.getLast().getMapText(), flagsLine);
        return items.getLast();
    }

    /**
     * Loads a {@link #GOOD_RECORD} followed by {@code flagsLine} as the last line of the file, with
     * no final newline, checks that the one record loaded, and returns it.
     */
    private RoomTemplate withFlagsAsLastLine(String flagsLine) throws IOException {
        List<RoomTemplate> items = new RoomProfileReader()
                .parseWithResults(tempFile("flags-last.txt", GOOD_RECORD + "\n" + flagsLine)).items();

        assertEquals(1, items.size(), flagsLine);
        return items.getFirst();
    }

    /**
     * C: {@code strtok(s, " |")} splits on any run of blanks and bars, so every separator in
     * {@link #SEPARATORS} gives two names. {@code MAX} is a name C's {@code lookup_flag} finds, so
     * both flags are set. {@code WIBBLE} is not, so a second name that is split off reaches
     * {@code grab_flag} and stops the load as {@code PARSE_ERROR_INVALID_FLAG}, which an unsplit
     * line could not do.
     *
     * <p>Function anyRunOfBlanksAndBarsSeparatesFlagsAsInC coded on 261120, commented in full on
     * 261120. It replaces flagsSeparatedByASpaceAloneAreRefusedHereWhereCSplitsOnIt.
     */
    @Test
    void anyRunOfBlanksAndBarsSeparatesFlagsAsInC() throws IOException {
        for (String sep : SEPARATORS) {
            RoomTemplate template = withFlagsMidFile("flags:FEW_ENTRANCES" + sep + "MAX");
            assertTrue(template.getFlags().has(RoomFlags.FEW_ENTRANCES), () -> "sep=[" + sep + "]");
            assertTrue(template.getFlags().has(RoomFlags.MAX), () -> "sep=[" + sep + "]");

            String split = GOOD_RECORD.replace("tval:0", "tval:0\nflags:FEW_ENTRANCES" + sep + "WIBBLE");
            assertStopsTheLoad("flags-split.txt", split + "\n", "illegal room flag value: WIBBLE");
        }
    }

    /**
     * C: {@code strtok} skips delimiters before the first name and finds nothing after the last,
     * so a run of blanks and bars at either end of the list adds nothing and loses nothing.
     *
     * <p>Function aLeadingOrTrailingDelimiterOnFlagsLoadsAsInC coded on 261120, commented in full
     * on 261120.
     */
    @Test
    void aLeadingOrTrailingDelimiterOnFlagsLoadsAsInC() throws IOException {
        for (String run : DELIMITER_RUNS) {
            for (String line : List.of("flags:" + run + "FEW_ENTRANCES", "flags:FEW_ENTRANCES" + run)) {
                RoomTemplate template = withFlagsMidFile(line);
                assertTrue(template.getFlags().has(RoomFlags.FEW_ENTRANCES), () -> "[" + line + "]");
                assertFalse(template.getFlags().has(RoomFlags.MAX), () -> "[" + line + "]");
            }
        }
    }

    /**
     * C: a value of blanks and bars alone is present, so the required {@code str} reaches
     * {@code parse_room_flags}, where {@code strtok} finds no name and the handler returns
     * {@code PARSE_ERROR_NONE}. The record loads with no flags.
     *
     * <p>Function aDelimiterOnlyFlagsLineLoadsWithNoFlagsAsInC coded on 261120, commented in full
     * on 261120.
     */
    @Test
    void aDelimiterOnlyFlagsLineLoadsWithNoFlagsAsInC() throws IOException {
        for (String run : DELIMITER_RUNS) {
            RoomTemplate template = withFlagsMidFile("flags:" + run);
            assertFalse(template.getFlags().has(RoomFlags.FEW_ENTRANCES), () -> "run=[" + run + "]");
            assertFalse(template.getFlags().has(RoomFlags.MAX), () -> "run=[" + run + "]");
        }
    }

    /**
     * C: {@code flags} is a required {@code str}, so {@code parser_parse} finds nothing after the
     * colon of a bare {@code flags:} and stops the load with {@code PARSE_ERROR_MISSING_FIELD}.
     * The shared {@code NL} fragment takes any blanks before the newline, so the grammar cannot
     * tell {@code flags:} from {@code flags: }, and both load with no flags. Parked in
     * {@code docs/Parked.md} (a bare {@code X:} and a blank-only {@code X: }), direction undecided.
     *
     * <p>Function aBareFlagsLineLoadsWithNoFlagsHereWhereCRefusesIt coded on 261120, commented in
     * full on 261120.
     */
    @Test
    void aBareFlagsLineLoadsWithNoFlagsHereWhereCRefusesIt() throws IOException {
        assertFalse(withFlagsMidFile("flags:").getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertFalse(withFlagsAsLastLine("flags:").getFlags().has(RoomFlags.FEW_ENTRANCES));
    }

    /**
     * C: {@code file_getl} hands an unterminated last line to the parser as it stands, so a
     * {@code flags:} line with no final newline gives the same answer as the same line with one.
     * {@code FLAG_EOF} pops the flag mode on a newline only, and end of file ends the parse
     * cleanly without it.
     *
     * <p>Function aFlagsLineWithNoFinalNewlineLoadsAsInC coded on 261120, commented in full on
     * 261120.
     */
    @Test
    void aFlagsLineWithNoFinalNewlineLoadsAsInC() throws IOException {
        List<String> one = List.of("flags:FEW_ENTRANCES", "flags: FEW_ENTRANCES", "flags:| FEW_ENTRANCES",
                "flags:FEW_ENTRANCES |", "flags:FEW_ENTRANCES ||");
        List<String> two = List.of("flags:FEW_ENTRANCES MAX", "flags:FEW_ENTRANCES|MAX",
                "flags:FEW_ENTRANCES | | MAX");
        List<String> none = List.of("flags: ", "flags:|", "flags:||", "flags: | ", "flags:| |");

        for (String line : one) {
            RoomTemplate template = withFlagsAsLastLine(line);
            assertTrue(template.getFlags().has(RoomFlags.FEW_ENTRANCES), () -> "[" + line + "]");
            assertFalse(template.getFlags().has(RoomFlags.MAX), () -> "[" + line + "]");
        }
        for (String line : two) {
            RoomTemplate template = withFlagsAsLastLine(line);
            assertTrue(template.getFlags().has(RoomFlags.FEW_ENTRANCES), () -> "[" + line + "]");
            assertTrue(template.getFlags().has(RoomFlags.MAX), () -> "[" + line + "]");
        }
        for (String line : none) {
            RoomTemplate template = withFlagsAsLastLine(line);
            assertFalse(template.getFlags().has(RoomFlags.FEW_ENTRANCES), () -> "[" + line + "]");
            assertFalse(template.getFlags().has(RoomFlags.MAX), () -> "[" + line + "]");
        }
    }

    /**
     * C: {@code finish_parse_room} hands {@code room_templates} whatever the file produced, so a
     * file with no {@code name:} loads an empty list. The grammar takes any number of records,
     * none included.
     *
     * <p>Function aFileWithNoTemplatesLoadsAnEmptyListAsInC coded on 261119, commented in full on
     * 261119. It replaces aFileWithNoTemplatesIsRefusedHereWhereCLoadsAnEmptyList.
     */
    @Test
    void aFileWithNoTemplatesLoadsAnEmptyListAsInC() throws IOException {
        assertEquals(List.of(),
                new RoomProfileReader().parseWithResults(tempFile("empty.txt", "")).items());
    }

    /**
     * C: {@code parser_parse} skips leading whitespace on a line before looking for the directive.
     * The {@code INDENT} rule does the same, so an indented directive loads.
     *
     * <p>Function anIndentedDirectiveLoadsAsInC coded on 261109, commented in full on 261109. It
     * replaces anIndentedDirectiveIsRefusedHereWhereCSkipsTheIndent.
     */
    @Test
    void anIndentedDirectiveLoadsAsInC() throws IOException {
        String indented = GOOD_RECORD.replace("rating:1", "  rating:1");
        int expected = new RoomProfileReader().parseWithResults(tempFile("plain.txt", GOOD_RECORD + "\n")).items().size();

        assertEquals(expected,
                new RoomProfileReader().parseWithResults(tempFile("indented.txt", indented + "\n")).items().size());
    }

    /**
     * C: {@code rows} is a {@code uint}, read with {@code strtoul} in base zero, so {@code 0x3} is
     * 3. The lexer's {@code INTEGER} is decimal only.
     */
    @Test
    void aHexadecimalRowCountIsRefusedHereWhereCReadsBaseZero() throws IOException {
        String hex = GOOD_RECORD.replace("rows:3", "rows:0x3");

        assertWholeFileRefused("hex.txt", hex + "\n");
    }

    /**
     * C: an {@code int} or {@code uint} field is tokenized on {@code :}, so {@code type:1:7} reads 1
     * and drops the rest. Here the extra field is a syntax error.
     */
    @Test
    void aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt() throws IOException {
        String extra = GOOD_RECORD.replace("type:1", "type:1:7");

        assertWholeFileRefused("extra-field.txt", extra + "\n");
    }

    /**
     * C: {@code parser_parse} skips a {@code #} comment with or without a final newline. The lexer's
     * {@code COMMENT} rule ends at a line end or at end of file, so a comment on the last line with
     * no newline loads.
     *
     * <p>Function aFinalCommentWithoutANewlineLoadsAsInC coded on 261109, commented in full on 261109.
     * It replaces aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt.
     */
    @Test
    void aFinalCommentWithoutANewlineLoadsAsInC() throws IOException {
        int expected = new RoomProfileReader().parseWithResults(tempFile("plain.txt", GOOD_RECORD + "\n")).items().size();

        assertEquals(expected,
                new RoomProfileReader().parseWithResults(tempFile("tail-comment.txt", GOOD_RECORD + "\n# the end")).items().size());
    }

    /**
     * Loads {@code GOOD_RECORD} with its {@code tval:} line replaced by {@code tvalLine} and returns
     * the one template's tval.
     */
    private TValue tvalOf(String fileName, String tvalLine) throws IOException {
        String record = GOOD_RECORD.replace("tval:0", tvalLine);
        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(tempFile(fileName, record + "\n"));

        assertEquals(1, result.items().size());
        return result.items().getFirst().getTval();
    }

    /**
     * C: {@code tval} is a {@code sym}, tokenized with {@code strtok(sp, ":")}, so the value is cut
     * at the first colon that follows text and whatever follows the colon is ignored:
     * {@code tval:rod:junk} reads {@code rod}, as do {@code tval:rod:} and {@code tval:rod:junk:more};
     * {@code tval:0:junk} reads 0, and a name with a blank in it ({@code magic book}) is cut the same way.
     *
     * <p>Function textAfterATvalNameIsDroppedAsInC coded on 261123, commented in full on 261123. It
     * replaces textAfterATvalNameIsTreatedAsPartOfTheNameHereWhereCDropsIt.
     */
    @Test
    void textAfterATvalNameIsDroppedAsInC() throws IOException {
        assertEquals(TValue.TV_ROD, tvalOf("tval-extra.txt", "tval:rod:junk"));
        assertEquals(TValue.TV_ROD, tvalOf("tval-trailing-colon.txt", "tval:rod:"));
        assertEquals(TValue.TV_ROD, tvalOf("tval-two-extras.txt", "tval:rod:junk:more"));
        assertEquals(TValue.TV_NONE, tvalOf("tval-number-extra.txt", "tval:0:junk"));
        assertEquals(TValue.TV_MAGIC_BOOK, tvalOf("tval-book-extra.txt", "tval:magic book:x"));
        assertEquals(TValue.TV_PRAYER_BOOK, tvalOf("tval-book-colon.txt", "tval:prayer book:"));
    }

    /**
     * C: {@code strtok(sp, ":")} skips leading colons before the first piece, so {@code tval::rod}
     * and {@code tval:::rod:x} read {@code rod}.
     *
     * <p>Function leadingColonsBeforeATvalNameAreSkippedAsInC coded on 261123, commented in full on
     * 261123.
     */
    @Test
    void leadingColonsBeforeATvalNameAreSkippedAsInC() throws IOException {
        assertEquals(TValue.TV_ROD, tvalOf("tval-one-lead.txt", "tval::rod"));
        assertEquals(TValue.TV_ROD, tvalOf("tval-three-lead.txt", "tval:::rod:x"));
        assertEquals(TValue.TV_NONE, tvalOf("tval-lead-number.txt", "tval::0"));
    }

    /**
     * C: with nothing but colons after {@code tval:}, {@code strtok} finds no piece and the missing
     * required {@code sym} is a parse error, so {@code tval:}, {@code tval::} and {@code tval:::} each
     * stop the load, here as a grammar error.
     *
     * <p>Function aTvalOfOnlyColonsIsRefusedAsInC coded on 261123, commented in full on 261123.
     */
    @Test
    void aTvalOfOnlyColonsIsRefusedAsInC() {
        for (String tvalLine : List.of("tval:", "tval::", "tval:::")) {
            String record = GOOD_RECORD.replace("tval:0", tvalLine);

            assertThrows(ParseCancellationException.class,
                    () -> new RoomProfileReader().parseWithResults(tempFile("tval-only-colons.txt", record + "\n")),
                    tvalLine);
        }
    }

    /**
     * The text after a colon on a {@code tval:} line is ignored to the end of the line only: the
     * next directive on the following line is still read, with LF and with CRLF line ends.
     *
     * <p>Function aTvalCutAtAColonStillReadsTheNextDirective coded on 261123, commented in full on
     * 261123.
     */
    @Test
    void aTvalCutAtAColonStillReadsTheNextDirective() throws IOException {
        assertEquals(TValue.TV_WAND, tvalOf("tval-then-flags.txt", "tval:wand:junk\nflags:FEW_ENTRANCES"));
        assertEquals(TValue.TV_WAND, tvalOf("tval-then-flags-crlf.txt", "tval:wand:junk\r\nflags:FEW_ENTRANCES"));
        assertEquals(TValue.TV_WAND, tvalOf("tval-lead-then-flags.txt", "tval::wand\nflags:FEW_ENTRANCES"));
    }

    /**
     * C: {@code tval_find_idx} accepts a number followed only by spaces ({@code contains_only_spaces}),
     * so {@code tval:0 } is tval 0. The port resolves it the same way.
     */
    @Test
    void aTvalNumberWithATrailingSpaceResolvesAsInC() throws IOException {
        String trailing = GOOD_RECORD.replace("tval:0", "tval:0 ");
        String path = tempFile("tval-space.txt", trailing + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

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

        assertStopsTheLoad("negative-type.txt", negativeType + "\n",
                "illegal negative type value: -1");
        assertStopsTheLoad("negative-doors.txt", negativeDoors + "\n",
                "illegal negative doors value: -1");
    }

    /**
     * C: {@code typ}, {@code rat}, {@code hgt}, {@code wid} and {@code dor} are {@code uint8_t}, so
     * {@code type:257} is stored as 1. The assembler reduces the value modulo 256 the same way.
     */
    @Test
    void aTypeThatDoesNotFitAByteIsTruncatedAsInC() throws IOException {
        String big = GOOD_RECORD.replace("type:1", "type:257");
        String path = tempFile("big-type.txt", big + "\n");

        ParseResult<RoomTemplate> result = new RoomProfileReader().parseWithResults(path);

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

        assertWholeFileRefused("empty-d.txt", empty + "\n");
    }

    /**
     * C reads the last line of a file whether or not it ends in a newline; so does the lexer, so a
     * final {@code D:} line with no terminator still loads.
     */
    @Test
    void aFinalLineWithoutANewlineStillLoads() throws IOException {
        ParseResult<RoomTemplate> result = new RoomProfileReader()
                .parseWithResults(tempFile("no-eol.txt", GOOD_RECORD));

        assertEquals("%%%%.%%%%", result.items().getFirst().getMapText());
    }

    /**
     * {@code parse_room_name} points each new record's {@code next} at the previous head, so the
     * list runs backwards; two records in a fixture come out second then first.
     */
    @Test
    void theResultRunsInReverseFileOrderAsInC() throws IOException {
        String second = GOOD_RECORD.replace("name:Good room", "name:Second room");
        String path = tempFile("two.txt", GOOD_RECORD + "\n" + second + "\n");

        List<RoomTemplate> items = new RoomProfileReader().parseWithResults(path).items();

        assertEquals(List.of("Second room", "Good room"), items.stream().map(RoomTemplate::getName).toList());
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new RoomProfileReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }
}
