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

package uk.co.jackoftradesltd.backend.parser.roomprofile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link RoomProfileAssembler}, driven with hand-built {@link RoomProfileParseRecord}s so
 * each branch of the port of {@code parse_room_type}/{@code parse_room_rating}/
 * {@code parse_room_height}/{@code parse_room_width}/{@code parse_room_doors}/
 * {@code parse_room_tval}/{@code parse_room_flags}/{@code parse_room_d} ({@code generate.c}) is
 * reached on its own, without a lexer or grammar in front of it.
 *
 * <p>The expected values come from the C source, not from the Java. {@code struct room_template}
 * declares {@code typ}, {@code rat}, {@code hgt}, {@code wid}, {@code dor} and {@code tval} as
 * {@code uint8_t}, so a value is stored modulo 256; {@code type:}, {@code rows:}, {@code columns:}
 * and {@code doors:} are read with {@code parser_getuint}, which refuses a token starting with
 * {@code -}; {@code rating:} is read with {@code parser_getint}, which does not; the size cap in
 * {@code parse_room_height} and {@code parse_room_width} is tested on the stored, wrapped value
 * against the {@code "room template"} builder's 11 by 33; {@code parse_room_d} does no length or
 * count check at all; and every fault is fatal.
 *
 * <p>Class RoomProfileAssemblerTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class RoomProfileAssemblerTest {

    private static final String FILE = "room_template.txt";

    /** The {@code name:} line every record built here claims to start on. */
    private static final int NAME_LINE = 7;

    private static final List<String> SQUARE = List.of("%%%", "%.%", "%%%");

    /**
     * Builds a record with a square 3 by 3 layout and no flags, so a test only names the field it
     * is about.
     */
    private static RoomProfileParseRecord record(String type, String rating, String rows, String columns,
                                                 String doors, String tval, List<String> flags,
                                                 List<String> map) {
        return new RoomProfileParseRecord("Test room", type, rating, rows, columns, doors, tval, flags,
                map, NAME_LINE, NAME_LINE + 8);
    }

    private static RoomProfileParseRecord record(String type, String rating, String rows, String columns,
                                                 String doors) {
        return record(type, rating, rows, columns, doors, "0", List.of(), SQUARE);
    }

    private static RoomProfileParseRecord withTval(String tval) {
        return record("1", "1", "3", "3", "1", tval, List.of(), SQUARE);
    }

    private static RoomProfileParseRecord withFlags(String... flags) {
        return record("1", "1", "3", "3", "1", "0", List.of(flags), SQUARE);
    }

    private static RoomProfileParseRecord withMap(String rows, String columns, String... map) {
        return record("1", "1", rows, columns, "1", "0", List.of(), List.of(map));
    }

    private static RoomTemplate assembleOne(RoomProfileParseRecord record) {
        List<String> errors = new ArrayList<>();
        List<RoomTemplate> result = new RoomProfileAssembler().assemble(List.of(record), errors, FILE);
        assertTrue(errors.isEmpty(), "the errors list is not used: " + errors);
        assertEquals(1, result.size());
        return result.getFirst();
    }

    /**
     * Asserts the assembler throws, and that the message names the record's line, the file and the
     * fault.
     */
    private static void assertStopsTheLoad(RoomProfileParseRecord record, String message) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new RoomProfileAssembler().assemble(List.of(record), new ArrayList<>(), FILE));
        assertTrue(thrown.getMessage().contains(message), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: " + NAME_LINE), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("file: " + FILE), thrown::getMessage);
    }

    // ---- The ordinary path ---------------------------------------------------

    /**
     * Every field of an ordinary record reaches the template: the name, the numbers, a named tval,
     * a flag and the layout.
     */
    @Test
    void anOrdinaryRecordCarriesEveryFieldThrough() {
        RoomTemplate template = assembleOne(record("1", "2", "3", "3", "4", "rod",
                List.of("FEW_ENTRANCES"), SQUARE));

        assertEquals("Test room", template.getName());
        assertEquals(1, template.getType());
        assertEquals(2, template.getRating());
        assertEquals(3, template.getHeight());
        assertEquals(3, template.getWidth());
        assertEquals(4, template.getDoors());
        assertEquals(TValue.TV_ROD, template.getTval());
        assertTrue(template.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(SQUARE, template.getMap());
        assertEquals("%%%%.%%%%", template.getMapText());
    }

    /**
     * No records is no templates, and the errors list stays empty.
     */
    @Test
    void noRecordsGiveAnEmptyList() {
        List<String> errors = new ArrayList<>();

        assertTrue(new RoomProfileAssembler().assemble(List.of(), errors, FILE).isEmpty());
        assertTrue(errors.isEmpty());
    }

    /**
     * {@code parse_room_name} points each new template at the previous head, so
     * {@code room_templates} runs from the last template in the file to the first.
     */
    @Test
    void theResultRunsInReverseFileOrder() {
        List<RoomProfileParseRecord> records = new ArrayList<>();
        for (String name : List.of("first", "second", "third")) {
            records.add(new RoomProfileParseRecord(name, "1", "1", "3", "3", "1", "0", List.of(), SQUARE, 1, 2));
        }

        List<RoomTemplate> result = new RoomProfileAssembler().assemble(records, new ArrayList<>(), FILE);

        assertEquals(List.of("third", "second", "first"), result.stream().map(RoomTemplate::getName).toList());
    }

    /**
     * Nothing is skipped: the first fault throws, so the records behind it are never reached, and
     * the errors list is left alone.
     */
    @Test
    void aBadRecordStopsTheLoadAndNothingIsReturned() {
        List<RoomProfileParseRecord> records = List.of(
                record("1", "1", "3", "3", "1"),
                record("1", "1", "12", "3", "1"),
                record("1", "1", "3", "3", "1"));
        List<String> errors = new ArrayList<>();

        assertThrows(DataFileParseException.class,
                () -> new RoomProfileAssembler().assemble(records, errors, FILE));
        assertTrue(errors.isEmpty());
    }

    // ---- uint8_t storage -----------------------------------------------------

    /**
     * {@code typ} is a {@code uint8_t}: 255 fits, 256 is 0, 257 is 1, 511 is 255 and 512 is 0.
     */
    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "255,255", "256,0", "257,1", "511,255", "512,0"})
    void typeIsStoredModulo256(String written, int stored) {
        assertEquals(stored, assembleOne(record(written, "1", "3", "3", "1")).getType());
    }

    /**
     * {@code rat} is a {@code uint8_t} filled from {@code parser_getint}, so a negative wraps up
     * from 256: -1 is 255, -256 is 0 and -257 is 255.
     */
    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "255,255", "256,0", "257,1", "-1,255", "-2,254", "-256,0", "-257,255", "-0,0"})
    void ratingIsStoredModulo256AndMayStartNegative(String written, int stored) {
        assertEquals(stored, assembleOne(record("1", written, "3", "3", "1")).getRating());
    }

    /**
     * {@code dor} is a {@code uint8_t}: 300 is stored as 44.
     */
    @ParameterizedTest
    @CsvSource({"0,0", "6,6", "255,255", "256,0", "300,44"})
    void doorsAreStoredModulo256(String written, int stored) {
        assertEquals(stored, assembleOne(record("1", "1", "3", "3", written)).getDoors());
    }

    // ---- The size caps -------------------------------------------------------

    /**
     * {@code hgt} is a {@code uint8_t} and the cap is tested on the stored value, so 256 is 0 and
     * passes, 257 is 1 and passes, 267 is 11 and passes, and 268 is 12 and does not. Zero is allowed.
     */
    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "11,11", "256,0", "257,1", "267,11"})
    void rowsAtOrUnderTheCapAfterTheWrapLoad(String written, int stored) {
        assertEquals(stored, assembleOne(withMap(written, "3", "%%%")).getHeight());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "255", "268"})
    void rowsOverTheCapAfterTheWrapStopTheLoad(String written) {
        assertStopsTheLoad(withMap(written, "3", "%%%"),
                "illegal row count value (outside room template size) " + written);
    }

    /**
     * {@code wid} follows the same rule against 33: 289 is 33 and passes, 290 is 34 and does not.
     */
    @ParameterizedTest
    @CsvSource({"0,0", "1,1", "33,33", "256,0", "289,33"})
    void columnsAtOrUnderTheCapAfterTheWrapLoad(String written, int stored) {
        assertEquals(stored, assembleOne(withMap("1", written, "%%%")).getWidth());
    }

    @ParameterizedTest
    @ValueSource(strings = {"34", "255", "290"})
    void columnsOverTheCapAfterTheWrapStopTheLoad(String written) {
        assertStopsTheLoad(withMap("1", written, "%%%"),
                "illegal column count value (outside room template size) " + written);
    }

    // ---- Negative numbers ----------------------------------------------------

    /**
     * {@code parser_getuint} refuses a token starting with {@code -}, {@code -0} included, with
     * {@code PARSE_ERROR_NOT_NUMBER}.
     */
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0", "-256"})
    void aMinusSignOnAnUnsignedFieldStopsTheLoad(String written) {
        assertStopsTheLoad(record(written, "1", "3", "3", "1"), "illegal negative type value: " + written);
        assertStopsTheLoad(record("1", "1", written, "3", "1"), "illegal negative row value: " + written);
        assertStopsTheLoad(record("1", "1", "3", written, "1"), "illegal negative column value: " + written);
        assertStopsTheLoad(record("1", "1", "3", "3", written), "illegal negative doors value: " + written);
    }

    // ---- tval ----------------------------------------------------------------

    /**
     * {@code tval_find_idx} returns 0 for "0" and for "none", and {@code parse_room_tval} only
     * refuses a negative result, so both are accepted and give no tval. 480 of the 500 shipped
     * records are {@code tval:0}.
     */
    @ParameterizedTest
    @ValueSource(strings = {"0", "-0", "none", "NONE", "0 "})
    void tvalZeroAndNoneAreAccepted(String written) {
        assertEquals(TValue.TV_NONE, assembleOne(withTval(written)).getTval());
    }

    /**
     * A number from 1 to 35 is taken as a tval in base 10, so "08" is 8, not an octal error.
     * 36 is {@code TV_MAX} and is refused.
     */
    @Test
    void aNumericTvalIsReadInBaseTenAndBoundedByTheTvalCount() {
        assertEquals(TValue.values()[8], assembleOne(withTval("08")).getTval());
        assertEquals(TValue.values()[10], assembleOne(withTval("010")).getTval());
        assertEquals(TValue.TV_GOLD, assembleOne(withTval("35")).getTval());
        assertStopsTheLoad(withTval("36"), "illegal tValue value: 36");
    }

    /**
     * A name matches without regard to case ({@code my_stricmp}), and {@code de_armour} lets
     * "armour" stand for "armor".
     */
    @Test
    void aTvalNameMatchesAnyCaseAndEitherSpellingOfArmour() {
        assertEquals(TValue.TV_ROD, assembleOne(withTval("rod")).getTval());
        assertEquals(TValue.TV_ROD, assembleOne(withTval("ROD")).getTval());
        assertEquals(TValue.TV_MAGIC_BOOK, assembleOne(withTval("magic book")).getTval());
        assertEquals(TValue.TV_SOFT_ARMOR, assembleOne(withTval("soft armor")).getTval());
        assertEquals(TValue.TV_SOFT_ARMOR, assembleOne(withTval("soft armour")).getTval());
    }

    /**
     * An unknown name, a negative number, and a name with text after it all give -1 from
     * {@code tval_find_idx}, which is {@code PARSE_ERROR_UNRECOGNISED_TVAL}.
     */
    @ParameterizedTest
    @ValueSource(strings = {"bogus", "-5", "rod:junk", "5abc", "99999"})
    void aTvalThatResolvesToNothingStopsTheLoad(String written) {
        assertStopsTheLoad(withTval(written), "illegal tValue value: " + written);
    }

    // ---- flags ---------------------------------------------------------------

    /**
     * C's {@code room_flags} table is {@code NONE}, {@code FEW_ENTRANCES} and {@code MAX}, so
     * {@code grab_flag} accepts all three; a record with no flags has none set.
     */
    @Test
    void everyNameInCsRoomFlagTableLoads() {
        assertFalse(assembleOne(withFlags()).getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertTrue(assembleOne(withFlags("FEW_ENTRANCES")).getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertDoesNotThrow(() -> assembleOne(withFlags("NONE")));
        assertDoesNotThrow(() -> assembleOne(withFlags("MAX")));
        assertTrue(assembleOne(withFlags("NONE", "FEW_ENTRANCES")).getFlags().has(RoomFlags.FEW_ENTRANCES));
    }

    /**
     * {@code lookup_flag} compares with {@code streq}, so the match is case-sensitive.
     */
    @Test
    void aFlagNameInTheWrongCaseStopsTheLoad() {
        assertStopsTheLoad(withFlags("few_entrances"), "illegal room flag value: few_entrances");
    }

    /**
     * {@code parse_room_flags} stops at the first name {@code grab_flag} rejects, so the message
     * names that one and not the ones after it.
     */
    @Test
    void theFirstBadFlagIsTheOneReported() {
        assertStopsTheLoad(withFlags("FEW_ENTRANCES", "BOGUS", "WORSE"), "illegal room flag value: BOGUS");
    }

    // ---- The layout ----------------------------------------------------------

    /**
     * {@code parse_room_d} appends every {@code D:} line with nothing between them and checks
     * nothing, so a line of the wrong length loads.
     */
    @Test
    void aRaggedLayoutLoadsAndIsJoinedAsWritten() {
        RoomTemplate template = assembleOne(withMap("3", "3", "%%%%", "%.%", "%%"));

        assertEquals(List.of("%%%%", "%.%", "%%"), template.getMap());
        assertEquals("%%%%%.%%%", template.getMapText());
    }

    /**
     * Nor does C count the lines: fewer than {@code rows:}, more than {@code rows:}, and a
     * {@code rows:} of 0 with a line all load, keeping the declared height.
     */
    @Test
    void aLineCountThatDisagreesWithRowsLoads() {
        RoomTemplate fewer = assembleOne(withMap("3", "3", "%%%", "%%%"));
        assertEquals(3, fewer.getHeight());
        assertEquals(2, fewer.getMap().size());

        RoomTemplate more = assembleOne(withMap("1", "3", "%%%", "%.%", "%%%"));
        assertEquals(1, more.getHeight());
        assertEquals(3, more.getMap().size());

        RoomTemplate none = assembleOne(withMap("0", "3", "%%%"));
        assertEquals(0, none.getHeight());
        assertEquals("%%%", none.getMapText());
    }

    /**
     * The wrapped height is what is kept, and the lines are still all there: 257 rows is stored as
     * 1, whatever number of lines follows.
     */
    @Test
    void aWrappedRowCountKeepsEveryLine() {
        RoomTemplate template = assembleOne(withMap("257", "3", "%%%", "%.%", "%%%"));

        assertEquals(1, template.getHeight());
        assertEquals(SQUARE, template.getMap());
    }
}
