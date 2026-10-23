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
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Tunnel;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.TunnelAssembler;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.TunnelProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TunnelAssembler}.
 *
 * <p>These construct {@link Tunnel} records directly - no grammar, no reader - so number texts
 * the shipped file never holds can still reach the assembler.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>field order from {@code parse_profile_tunnel} in {@code generate.c}, which reads
 *       {@code rnd}, {@code chg}, {@code con}, {@code pen}, {@code jct} into {@code c->tun} and
 *       stores them with no range check;</li>
 *   <li>number reading from {@code parser_getint}, whose field was filled by {@code strtol(tok,
 *       &z, 0)} in {@code parser.c}: base 0, so a leading {@code 0} is octal, digits after the
 *       last valid one are ignored ({@code 08} is 0), and only text with no digit at all is
 *       {@code PARSE_ERROR_NOT_NUMBER};</li>
 *   <li>the empty-text value from {@code mem_zalloc} in {@code parse_profile_name}, which leaves
 *       every field of {@code tun} at 0;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>One accepted deviation is pinned, not hidden: a figure too big for an {@code int} is clamped
 * here, where C keeps the low 32 bits ({@code ParseUtils}' class block).
 *
 * <p>Class TunnelAssemblerTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class TunnelAssemblerTest {

    private static final String FILE = "dungeon_profile.txt";

    private static Tunnel tunnel(String rnd, String chg, String con, String pen, String jct) {
        return new Tunnel(rnd, chg, con, pen, jct, 89);
    }

    private static TunnelProfile assemble(List<Tunnel> records) {
        return new TunnelAssembler().assemble(records, new ArrayList<>(), FILE);
    }

    private static TunnelProfile one(Tunnel record) {
        return assemble(List.of(record));
    }

    private static void assertProfile(TunnelProfile t, int rnd, int chg, int con, int pen, int jct) {
        assertAll(
                () -> assertEquals(rnd, t.getRnd(), "rnd"),
                () -> assertEquals(chg, t.getChg(), "chg"),
                () -> assertEquals(con, t.getCon(), "con"),
                () -> assertEquals(pen, t.getPen(), "pen"),
                () -> assertEquals(jct, t.getJct(), "jct"));
    }

    /** The five getters in file order, so a parameterised test can name a field by its index. */
    private static final List<ToIntFunction<TunnelProfile>> FIELDS = List.of(
            TunnelProfile::getRnd, TunnelProfile::getChg, TunnelProfile::getCon,
            TunnelProfile::getPen, TunnelProfile::getJct);

    /** A record holding {@code text} in field {@code field} and 1 in the other four. */
    private static Tunnel withField(int field, String text) {
        String[] f = {"1", "1", "1", "1", "1"};
        f[field] = text;
        return tunnel(f[0], f[1], f[2], f[3], f[4]);
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void shippedClassicLineAssemblesEveryField() {
        // dungeon_profile.txt: tunnel:10:30:15:25:50 (classic, modified, lair)
        assertProfile(one(tunnel("10", "30", "15", "25", "50")), 10, 30, 15, 25, 50);
    }

    @Test
    void shippedMoriaLineAssemblesEveryField() {
        // dungeon_profile.txt: tunnel:10:30:15:25:30 - moria's jct is the one shipped difference
        assertProfile(one(tunnel("10", "30", "15", "25", "30")), 10, 30, 15, 25, 30);
    }

    @Test
    void eachFieldLandsInItsOwnSlot() {
        // C: rnd, chg, con, pen, jct in that order; five distinct values catch any swap
        assertProfile(one(tunnel("11", "12", "13", "14", "15")), 11, 12, 13, 14, 15);
    }

    @Test
    void nothingIsAddedToTheErrorList() {
        List<String> errors = new ArrayList<>();
        new TunnelAssembler().assemble(List.of(tunnel("10", "30", "15", "25", "50")), errors, FILE);
        assertTrue(errors.isEmpty(), errors::toString);
    }

    // ---- Number reading: strtol in base 0 ----------------------------------

    @ParameterizedTest(name = "field {0}: \"{1}\" -> {2}")
    @CsvSource({
            // decimal
            "0, 50, 50",
            "4, 100, 100",
            // C stores a "percentage" past 100 as given; nothing clamps it at parse time
            "2, 250, 250",
            // octal: a leading 0 switches base, and reading stops at the first non-octal digit
            "0, 010, 8",
            "1, 017, 15",
            "2, 08, 0",
            "3, 019, 1",
            "4, 00, 0",
            "0, 0, 0",
            // negative: C stores it as given, no range check
            "1, -3, -3",
            "4, -010, -8",
            "3, -0, 0",
            // trailing text after the digits is ignored, as strtol stops there
            "2, 12abc, 12",
    })
    void eachFieldIsReadAsStrtolBaseZeroReadsIt(int field, String text, int expected) {
        TunnelProfile t = one(withField(field, text));
        assertEquals(expected, FIELDS.get(field).applyAsInt(t));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void emptyTextReadsAsZeroAsMemZallocLeavesIt(int field) {
        // C has no empty int field; the nearest value is the zero-fill from parse_profile_name
        assertEquals(0, FIELDS.get(field).applyAsInt(one(withField(field, ""))));
    }

    // ---- Failures that stop the load ----------------------------------------

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void aFieldWithNoDigitStopsTheLoadAsInC(int field) {
        // C: z == tok after strtol gives PARSE_ERROR_NOT_NUMBER, and the load quits
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> one(withField(field, "x")));
        assertTrue(e.getMessage().contains(FILE), e.getMessage());
        assertTrue(e.getMessage().contains("89"), e.getMessage());
    }

    @Test
    void aLoneMinusReadsAsZero() {
        // C refuses a lone "-" (no digit read); ParseUtils gives 0 - unreachable, since the
        // lexer's INTEGER token needs a digit (ParseUtils' class block)
        assertEquals(0, one(withField(0, "-")).getRnd());
    }

    @Test
    void aNullRecordListStopsTheLoad() {
        assertThrows(DataFileParseException.class, () -> assemble(null));
    }

    @Test
    void anEmptyRecordListStopsTheLoad() {
        // C embeds one tun per profile; there is no "no tunnel" record to assemble
        assertThrows(DataFileParseException.class, () -> assemble(List.of()));
    }

    @Test
    void twoRecordsStopTheLoad() {
        // The grammar keeps only the last tunnel: line, as C's overwrite does, so two never arrive
        Tunnel t = tunnel("10", "30", "15", "25", "50");
        assertThrows(DataFileParseException.class, () -> assemble(List.of(t, t)));
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
            "2, 99999999999, 2147483647",
            "4, -99999999999, -2147483648",
    })
    void aFigureTooBigForAnIntIsClampedWhereCWraps(int field, String text, int expected) {
        assertEquals(expected, FIELDS.get(field).applyAsInt(one(withField(field, text))));
    }
}
