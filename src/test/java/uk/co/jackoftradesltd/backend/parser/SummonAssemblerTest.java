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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.summon.SummonAssembler;
import uk.co.jackoftradesltd.backend.parser.summon.SummonParseRecord;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SummonAssembler}.
 *
 * <p>These construct {@link SummonParseRecord}s directly - no grammar, no reader - so values the
 * lexer would never produce (a lower-case flag, an {@code RF_} prefix, a number with a sign or a
 * leading {@code 0}) can still reach the assembler. End-to-end cases live in
 * {@code SummonReaderTest}.
 *
 * <p>The expected values are worked out from {@code mon-summon.c}, not from the Java:
 * <ul>
 *   <li>{@code parse_summon_message_type} calls {@code message_lookup_by_name}, which matches the
 *       C names case-insensitively, accepts a message number, has no name {@code NONE} (its list
 *       starts at {@code GENERIC}), and gives -1, a parser error, for anything else;</li>
 *   <li>{@code parse_summon_unique} reads an {@code int} with {@code parser_getint}
 *       ({@code strtol}, base 0) and sets {@code unique_allowed} when it is non-zero, so
 *       {@code 2}, {@code -1} and the octal {@code 010} are true and {@code 0}, {@code 00} and
 *       {@code -0} are false. A figure too big for 32 bits is wrapped in C and clamped here, the
 *       accepted deviation recorded in {@code docs/Accepted_Deviations.md};</li>
 *   <li>{@code parse_summon_base} uses {@code lookup_monster_base}, which compares with
 *       {@code streq} (exact case), and returns {@code PARSE_ERROR_INVALID_MONSTER_BASE} for a
 *       miss. Each base is pushed on the head of a list, so C holds them in reverse; only
 *       membership is ever tested, so the Java file order is not observable;</li>
 *   <li>{@code parse_summon_race_flag} uses {@code lookup_flag} on the names without their
 *       {@code RF_} prefix, compared with {@code streq} from {@code FLAG_START} (1), so
 *       {@code NONE} at index 0 is never matched and there is no {@code MAX} row;</li>
 *   <li>{@code finish_parse_summon} gives each summon the index {@code summon_name_to_idx} finds for
 *       its fallback name: exact match, the first summon of that name wins, and -1, with no error,
 *       for a name that matches nothing or for none at all;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class SummonAssemblerTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class SummonAssemblerTest {

    private static final String FILE = "summon.txt";

    /** A base name the seeded monster bases hold. */
    private static final String BASE = "ancient dragon";
    private static final String OTHER_BASE = "zephyr hound";

    private static Object savedBases;

    @BeforeAll
    static void seed() throws Exception {
        var field = RegistrySeeding.resolve("monsterBases");
        field.setAccessible(true);
        savedBases = field.get(null);
        MonsterRegistry.setMonsterBases(List.of(new MonsterBase(BASE), new MonsterBase(OTHER_BASE)));
    }

    @AfterAll
    static void restore() throws Exception {
        var field = RegistrySeeding.resolve("monsterBases");
        field.setAccessible(true);
        field.set(null, savedBases);
    }

    private static SummonParseRecord record(String name, String msgt, String uniques,
                                            List<String> bases, String raceFlag, String fallback) {
        return new SummonParseRecord(name, msgt, uniques, bases, raceFlag, fallback, "a monster", 7);
    }

    /** A record with no base, race flag or fallback. */
    private static SummonParseRecord plain(String name) {
        return record(name, "SUM_MONSTER", "0", List.of(), "", "");
    }

    private static List<Summon> assemble(List<SummonParseRecord> records, List<String> errors) {
        return new SummonAssembler().assemble(records, errors, FILE);
    }

    private static Summon one(SummonParseRecord record) {
        List<Summon> summons = assemble(List.of(record), new ArrayList<>());
        assertEquals(1, summons.size());
        return summons.getFirst();
    }

    private static DataFileParseException refused(SummonParseRecord... records) {
        List<String> errors = new ArrayList<>();
        DataFileParseException e =
                assertThrows(DataFileParseException.class, () -> assemble(List.of(records), errors));
        assertTrue(errors.isEmpty(), "a load-ending error is thrown, never added to the error list");
        return e;
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void theUpstreamWraithRecordAssemblesFieldForField() {
        // lib/gamedata/summon.txt: WRAITH is SUM_WRAITH, uniques 1, base wraith, race-flag UNIQUE,
        // fallback HI_UNDEAD. Here the base is one the seeded registry holds.
        List<Summon> summons = assemble(List.of(
                plain("HI_UNDEAD"),
                record("WRAITH", "SUM_WRAITH", "1", List.of(BASE), "UNIQUE", "HI_UNDEAD")), new ArrayList<>());

        Summon wraith = summons.get(1);
        assertEquals("WRAITH", wraith.getName());
        assertEquals(MessageType.MSG_SUM_WRAITH, wraith.getMessageType());
        assertTrue(wraith.isUniquesAllowed());
        assertEquals(List.of(MonsterRegistry.lookupMonsterBase(BASE)), wraith.getBases());
        assertEquals(MonsterRaceFlag.RF_UNIQUE, wraith.getRaceFlag());
        assertSame(summons.get(0), wraith.getFallback());
        assertEquals("HI_UNDEAD", wraith.getFallbackName());
        assertEquals("a monster", wraith.getDescription());
    }

    @Test
    void theRecordsComeBackInFileOrder() {
        // finish_parse_summon fills summons[] from the highest index down while walking the head-first
        // chain, so the array is in file order.
        List<Summon> summons = assemble(List.of(plain("ANY"), plain("KIN"), plain("MONSTER")), new ArrayList<>());

        assertEquals(List.of("ANY", "KIN", "MONSTER"), summons.stream().map(Summon::getName).toList());
    }

    @Test
    void noRecordsGiveNoSummons() {
        assertTrue(assemble(List.of(), new ArrayList<>()).isEmpty());
    }

    @Test
    void theErrorListIsLeftAloneOnSuccess() {
        List<String> errors = new ArrayList<>(List.of("kept"));
        assemble(List.of(plain("ANY")), errors);
        assertEquals(List.of("kept"), errors);
    }

    // ---- msgt: -------------------------------------------------------------

    @Test
    void aMessageTypeNameIsMatchedWithoutRegardToCase() {
        // message_lookup_by_name uses my_stricmp.
        assertEquals(MessageType.MSG_SUM_MONSTER,
                one(record("A", "sum_monster", "0", List.of(), "", "")).getMessageType());
    }

    @Test
    void aMessageNumberIsAcceptedAsInC() {
        // strtoul of the whole token, and 0 is MSG_GENERIC.
        assertEquals(MessageType.MSG_GENERIC, one(record("A", "0", "0", List.of(), "", "")).getMessageType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOTREAL", "NONE", "MSG_SUM_MONSTER", "-1", "153", "SUM_MONSTER x"})
    void anUnresolvedMessageTypeStopsTheLoad(String msgt) {
        DataFileParseException e = refused(record("A", msgt, "0", List.of(), "", ""));
        String text = e.getMessage();
        assertTrue(text.contains("Summon starting at line: 7 of file: " + FILE), text);
        assertTrue(text.contains("message type value: " + msgt), text);
    }

    // ---- uniques: ----------------------------------------------------------

    @ParameterizedTest
    @CsvSource({
            "0, false",
            "00, false",
            "-0, false",
            "1, true",
            "2, true",
            "-1, true",
            "010, true",
            "99, true",
            "2147483647, true"})
    void uniquesIsTrueForAnyNonZeroNumberAsInC(String text, boolean expected) {
        assertEquals(expected, one(record("A", "SUM_MONSTER", text, List.of(), "", "")).isUniquesAllowed());
    }

    @Test
    void aFigureTooBigForAnIntIsClampedHereWhereCWrapsIt() {
        // strtol gives 4294967296 and the int field keeps the low 32 bits, 0, so C reads false. The
        // clamp makes it 2147483647, true. Accepted deviation, "An integer too big for its C type is
        // clamped, where C wraps it".
        assertTrue(one(record("A", "SUM_MONSTER", "4294967296", List.of(), "", "")).isUniquesAllowed());
    }

    @Test
    void uniquesWithNoDigitsStopsTheLoad() {
        refused(record("A", "SUM_MONSTER", "abc", List.of(), "", ""));
    }

    // ---- base: -------------------------------------------------------------

    @Test
    void noBaseGivesAnEmptyList() {
        assertTrue(one(plain("A")).getBases().isEmpty());
    }

    @Test
    void severalBasesAreAllKept() {
        Summon summon = one(record("A", "SUM_MONSTER", "0", List.of(BASE, OTHER_BASE), "", ""));

        // C holds these in reverse, and only tests membership, so only membership is checked.
        assertEquals(2, summon.getBases().size());
        assertTrue(summon.getBases().contains(MonsterRegistry.lookupMonsterBase(BASE)));
        assertTrue(summon.getBases().contains(MonsterRegistry.lookupMonsterBase(OTHER_BASE)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"no such base", "Ancient Dragon", "ancient", "ancient dragon "})
    void anUnknownBaseStopsTheLoad(String base) {
        // lookup_monster_base is streq: exact case, no prefix match, no trimming.
        DataFileParseException e = refused(record("A", "SUM_MONSTER", "0", List.of(base), "", ""));
        String text = e.getMessage();
        assertTrue(text.contains("Summon starting at line: 7 of file: " + FILE), text);
        assertTrue(text.contains("monster base value: " + base), text);
    }

    @Test
    void aBadBaseAfterAGoodOneStopsTheLoad() {
        refused(record("A", "SUM_MONSTER", "0", List.of(BASE, "no such base"), "", ""));
    }

    // ---- race-flag: --------------------------------------------------------

    @Test
    void anAbsentRaceFlagIsRfNone() {
        // The summon is allocated zeroed, so race_flag stays 0.
        assertEquals(MonsterRaceFlag.RF_NONE, one(plain("A")).getRaceFlag());
    }

    @ParameterizedTest
    @CsvSource({"UNDEAD, RF_UNDEAD", "DEMON, RF_DEMON", "ANIMAL, RF_ANIMAL", "UNIQUE, RF_UNIQUE"})
    void aRaceFlagNameResolvesWithoutItsPrefix(String name, MonsterRaceFlag expected) {
        assertEquals(expected, one(record("A", "SUM_MONSTER", "0", List.of(), name, "")).getRaceFlag());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOTAFLAG", "NONE", "MAX", "undead", "RF_UNDEAD", " UNDEAD"})
    void anUnresolvedRaceFlagStopsTheLoad(String flag) {
        // lookup_flag is streq from FLAG_START (1): NONE (index 0) is never matched, there is no MAX
        // row, the table holds the names without RF_, and the case must be exact.
        DataFileParseException e = refused(record("A", "SUM_MONSTER", "0", List.of(), flag, ""));
        String text = e.getMessage();
        assertTrue(text.contains("Summon starting at line: 7 of file: " + FILE), text);
        assertTrue(text.contains("race flag value: " + flag), text);
    }

    // ---- fallback: ---------------------------------------------------------

    @Test
    void anAbsentFallbackIsNullWithAnEmptyName() {
        Summon summon = one(plain("A"));

        assertNull(summon.getFallback());
        assertEquals("", summon.getFallbackName());
    }

    @Test
    void aFallbackNamingAnEarlierSummonResolves() {
        List<Summon> summons = assemble(List.of(
                plain("HI_UNDEAD"),
                record("WRAITH", "SUM_MONSTER", "0", List.of(), "", "HI_UNDEAD")), new ArrayList<>());

        assertSame(summons.get(0), summons.get(1).getFallback());
    }

    @Test
    void aFallbackNamingALaterSummonResolves() {
        // finish_parse_summon links after every summon exists, so a forward reference works.
        List<Summon> summons = assemble(List.of(
                record("WRAITH", "SUM_MONSTER", "0", List.of(), "", "HI_UNDEAD"),
                plain("HI_UNDEAD")), new ArrayList<>());

        assertSame(summons.get(1), summons.get(0).getFallback());
    }

    @Test
    void aSummonMayFallBackOnItself() {
        Summon summon = one(record("LOOP", "SUM_MONSTER", "0", List.of(), "", "LOOP"));

        assertSame(summon, summon.getFallback());
    }

    @Test
    void duplicateNamesAreBothKeptAndAFallbackFindsTheFirst() {
        // summon_name_to_idx returns the first match in the array.
        List<Summon> summons = assemble(List.of(
                plain("TWIN"),
                plain("TWIN"),
                record("A", "SUM_MONSTER", "0", List.of(), "", "TWIN")), new ArrayList<>());

        assertEquals(3, summons.size());
        assertSame(summons.get(0), summons.get(2).getFallback());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOSUCH", "hi_undead", "HI_UNDEAD ", "HI"})
    void anUnresolvedFallbackIsSilentAndTheRecordIsKept(String fallback) {
        // summon_name_to_idx is streq and gives -1 for a miss; finish_parse_summon reports nothing.
        List<String> errors = new ArrayList<>();
        List<Summon> summons = assemble(List.of(
                plain("HI_UNDEAD"),
                record("A", "SUM_MONSTER", "0", List.of(), "", fallback)), errors);

        assertEquals(2, summons.size());
        assertNull(summons.get(1).getFallback());
        assertEquals(fallback, summons.get(1).getFallbackName());
        assertTrue(errors.isEmpty());
    }

    // ---- Stopping the load -------------------------------------------------

    @Test
    void theFirstFaultInARecordIsTheOneReported() {
        // C handles the lines in file order: msgt, uniques, base, race-flag.
        DataFileParseException msgtFirst = refused(
                record("A", "NOTREAL", "0", List.of("no such base"), "NOTAFLAG", ""));
        assertTrue(msgtFirst.getMessage().contains("message type"), msgtFirst.getMessage());

        DataFileParseException baseFirst = refused(
                record("A", "SUM_MONSTER", "0", List.of("no such base"), "NOTAFLAG", ""));
        assertTrue(baseFirst.getMessage().contains("monster base"), baseFirst.getMessage());
    }

    @Test
    void aFaultInALaterRecordStopsTheWholeLoad() {
        // The clean record before it is not handed back: C's first parser error ends the file.
        refused(plain("GOOD"), record("BAD", "NOTREAL", "0", List.of(), "", ""));
    }

    @Test
    void theLineInTheMessageIsTheRecordsOwn() {
        SummonParseRecord bad = new SummonParseRecord("A", "NOTREAL", "0", List.of(), "", "", "d", 42);

        DataFileParseException e = refused(bad);
        assertTrue(e.getMessage().contains("Summon starting at line: 42 of file: " + FILE), e.getMessage());
    }
}
