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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Streamer;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.StreamerAssembler;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.StreamerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StreamerAssembler}.
 *
 * <p>These construct {@link Streamer} records directly - no grammar, no reader - so number texts
 * the shipped file never holds can still reach the assembler.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>field order from {@code parse_profile_streamer} in {@code generate.c}, which reads
 *       {@code den}, {@code rng}, {@code mag}, {@code mc}, {@code qua}, {@code qc} into
 *       {@code c->str} and stores them with no range check;</li>
 *   <li>number reading from {@code parser_getint}, whose field was filled by {@code strtol(tok,
 *       &z, 0)} in {@code parser.c}: base 0, so a leading {@code 0} is octal, digits after the
 *       last valid one are ignored ({@code 08} is 0), and only text with no digit at all is
 *       {@code PARSE_ERROR_NOT_NUMBER};</li>
 *   <li>the empty-text value from {@code mem_zalloc} in {@code parse_profile_name}, which leaves
 *       every field of {@code str} at 0;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>One accepted deviation is pinned, not hidden: a figure too big for an {@code int} is clamped
 * here, where C keeps the low 32 bits ({@code ParseUtils}' class block).
 *
 * <p>Class StreamerAssemblerTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class StreamerAssemblerTest {

    private static final String FILE = "dungeon_profile.txt";

    private static Streamer streamer(String den, String rng, String mag, String mc, String qua, String qc) {
        return new Streamer(den, rng, mag, mc, qua, qc, 90);
    }

    private static StreamerProfile assemble(List<Streamer> records) {
        return new StreamerAssembler().assemble(records, new ArrayList<>(), FILE);
    }

    private static StreamerProfile one(Streamer record) {
        return assemble(List.of(record));
    }

    private static void assertProfile(StreamerProfile s, int den, int rng, int mag, int mc, int qua, int qc) {
        assertAll(
                () -> assertEquals(den, s.getDen(), "den"),
                () -> assertEquals(rng, s.getRng(), "rng"),
                () -> assertEquals(mag, s.getMam(), "mag"),
                () -> assertEquals(mc, s.getMc(), "mc"),
                () -> assertEquals(qua, s.getQua(), "qua"),
                () -> assertEquals(qc, s.getQc(), "qc"));
    }

    /** The six getters in file order, so a parameterised test can name a field by its index. */
    private static final List<ToIntFunction<StreamerProfile>> FIELDS = List.of(
            StreamerProfile::getDen, StreamerProfile::getRng, StreamerProfile::getMam,
            StreamerProfile::getMc, StreamerProfile::getQua, StreamerProfile::getQc);

    /** A record holding {@code text} in field {@code field} and 1 in the other five. */
    private static Streamer withField(int field, String text) {
        String[] f = {"1", "1", "1", "1", "1", "1"};
        f[field] = text;
        return streamer(f[0], f[1], f[2], f[3], f[4], f[5]);
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void shippedClassicLineAssemblesEveryField() {
        // dungeon_profile.txt: streamer:5:2:3:90:2:40 (classic, modified, moria, lair)
        assertProfile(one(streamer("5", "2", "3", "90", "2", "40")), 5, 2, 3, 90, 2, 40);
    }

    @Test
    void shippedTownLineAssemblesEveryField() {
        // dungeon_profile.txt: streamer:1:1:0:0:0:0 - den and rng drive the town's lava streamers
        assertProfile(one(streamer("1", "1", "0", "0", "0", "0")), 1, 1, 0, 0, 0, 0);
    }

    @Test
    void eachFieldLandsInItsOwnSlot() {
        // C: den, rng, mag, mc, qua, qc in that order; six distinct values catch any swap
        assertProfile(one(streamer("11", "12", "13", "14", "15", "16")), 11, 12, 13, 14, 15, 16);
    }

    @Test
    void theAllZeroRecordForAProfileWithNoStreamerLineIsCsZeroFill() {
        // DungeonProfileAssembler hands over six "0"s; C's mem_zalloc leaves str all zero
        assertProfile(one(streamer("0", "0", "0", "0", "0", "0")), 0, 0, 0, 0, 0, 0);
    }

    @Test
    void nothingIsAddedToTheErrorList() {
        List<String> errors = new ArrayList<>();
        new StreamerAssembler().assemble(List.of(streamer("5", "2", "3", "90", "2", "40")), errors, FILE);
        assertTrue(errors.isEmpty(), errors::toString);
    }

    // ---- Number reading: strtol in base 0 ----------------------------------

    @ParameterizedTest(name = "field {0}: \"{1}\" -> {2}")
    @CsvSource({
            // decimal
            "0, 90, 90",
            "3, 90, 90",
            // octal: a leading 0 switches base, and reading stops at the first non-octal digit
            "0, 010, 8",
            "1, 017, 15",
            "2, 08, 0",
            "3, 019, 1",
            "4, 00, 0",
            "5, 0, 0",
            // negative: C stores it as given, no range check
            "0, -3, -3",
            "5, -010, -8",
            "2, -0, 0",
            // trailing text after the digits is ignored, as strtol stops there
            "4, 12abc, 12",
    })
    void eachFieldIsReadAsStrtolBaseZeroReadsIt(int field, String text, int expected) {
        StreamerProfile s = one(withField(field, text));
        assertEquals(expected, FIELDS.get(field).applyAsInt(s));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void emptyTextReadsAsZeroAsMemZallocLeavesIt(int field) {
        // C has no empty int field; the nearest value is the zero-fill from parse_profile_name
        assertEquals(0, FIELDS.get(field).applyAsInt(one(withField(field, ""))));
    }

    // ---- Failures that stop the load ----------------------------------------

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void aFieldWithNoDigitStopsTheLoadAsInC(int field) {
        // C: z == tok after strtol gives PARSE_ERROR_NOT_NUMBER, and the load quits
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> one(withField(field, "x")));
        assertTrue(e.getMessage().contains(FILE), e.getMessage());
        assertTrue(e.getMessage().contains("90"), e.getMessage());
    }

    @Test
    void aNullRecordListStopsTheLoad() {
        assertThrows(DataFileParseException.class, () -> assemble(null));
    }

    @Test
    void anEmptyRecordListStopsTheLoad() {
        // C embeds one str per profile; there is no "no streamer" record to assemble
        assertThrows(DataFileParseException.class, () -> assemble(List.of()));
    }

    @Test
    void twoRecordsStopTheLoad() {
        // The grammar keeps only the last streamer: line, as C's overwrite does, so two never arrive
        Streamer s = streamer("5", "2", "3", "90", "2", "40");
        assertThrows(DataFileParseException.class, () -> assemble(List.of(s, s)));
    }

    // ---- Where the Java and C differ, pinned so a change is noticed --------

    /**
     * C stores {@code strtol}'s 64-bit result in a 32-bit {@code int} and keeps the low 32 bits:
     * {@code 99999999999} becomes 1215752191 and {@code -99999999999} becomes -1215752191. Here
     * the value is clamped instead (accepted deviation, {@code ParseUtils}), and the load does
     * not stop in either.
     */
    @ParameterizedTest(name = "field {0}: \"{1}\" -> {2}")
    @CsvSource({
            "0, 99999999999, 2147483647",
            "3, 99999999999, 2147483647",
            "5, -99999999999, -2147483648",
    })
    void aFigureTooBigForAnIntIsClampedWhereCWraps(int field, String text, int expected) {
        assertEquals(expected, FIELDS.get(field).applyAsInt(one(withField(field, text))));
    }
}
