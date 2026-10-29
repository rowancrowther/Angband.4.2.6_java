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
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.slay.SlayAssembler;
import uk.co.jackoftradesltd.backend.parser.slay.SlayParseRecord;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Slay;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SlayAssembler}.
 *
 * <p>These construct {@link SlayParseRecord}s directly - no grammar, no reader - so values the
 * lexer would never produce (a lower-case flag, an {@code RF_} prefix, a leading space) can still
 * reach the assembler. End-to-end cases live in {@code SlayReaderTest}.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>the handlers {@code parse_slay_code}, {@code parse_slay_name}, {@code parse_slay_race_flag},
 *       {@code parse_slay_base}, {@code parse_slay_multiplier}, {@code parse_slay_o_multiplier},
 *       {@code parse_slay_power}, {@code parse_slay_melee_verb} and {@code parse_slay_range_verb} in
 *       {@code obj-init.c}. Each allocates the slay with {@code mem_zalloc}, so an absent number is 0
 *       and an absent string is {@code NULL}; a second target on one slay is
 *       {@code PARSE_ERROR_INVALID_SLAY}, and nothing requires a first;</li>
 *   <li>the race flag from {@code lookup_flag} in {@code datafile.c}, which compares with
 *       {@code streq} (exact case) from {@code FLAG_START} (1), so {@code NONE} at index 0 is never
 *       matched, and there is no {@code MAX} row;</li>
 *   <li>the base from {@code lookup_monster_base} in {@code mon-util.c}, also {@code streq}. C
 *       refuses every base because {@code slays} is parsed before {@code monster bases} in
 *       {@code init.c}; Java resolves a matching one, an accepted deviation of 2026-10-29;</li>
 *   <li>the numbers from the {@code uint} branch of {@code parser.c}: {@code strtoul} in base 0, so
 *       a leading {@code 0} is octal, and a first character of {@code -} is
 *       {@code PARSE_ERROR_NOT_NUMBER} whatever the value, so {@code -0} is refused too;</li>
 *   <li>the order from {@code parse_slay_code} pushing each slay onto the head of its chain and
 *       {@code finish_parse_slay} copying from the head, so the last record comes first;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class SlayAssemblerTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class SlayAssemblerTest {

    private static final String FILE = "slay.txt";

    /** A base name the seeded monster bases hold. */
    private static final String BASE = "ancient dragon";

    private static Object savedBases;

    @BeforeAll
    static void seed() throws Exception {
        var field = RegistrySeeding.resolve("monsterBases");
        field.setAccessible(true);
        savedBases = field.get(null);
        MonsterRegistry.setMonsterBases(List.of(new MonsterBase(BASE), new MonsterBase("zephyr hound")));
    }

    @AfterAll
    static void restore() throws Exception {
        var field = RegistrySeeding.resolve("monsterBases");
        field.setAccessible(true);
        field.set(null, savedBases);
    }

    private static SlayParseRecord record(String code, String raceFlag, String base,
                                          String mult, String oMult, String power) {
        return new SlayParseRecord(code, "evil creatures", raceFlag, base, mult, oMult, power,
                "smite", "pierces", 7);
    }

    /** The upstream EVIL_2 block of {@code slay.txt}. */
    private static SlayParseRecord evil2() {
        return record("EVIL_2", "EVIL", "", "2", "18", "200");
    }

    private static List<Slay> assemble(List<SlayParseRecord> records, List<String> errors) {
        return new SlayAssembler().assemble(records, errors, FILE);
    }

    private static Slay one(SlayParseRecord record) {
        List<Slay> slays = assemble(List.of(record), new ArrayList<>());
        assertEquals(1, slays.size());
        return slays.getFirst();
    }

    private static DataFileParseException refused(SlayParseRecord record) {
        List<String> errors = new ArrayList<>();
        DataFileParseException e =
                assertThrows(DataFileParseException.class, () -> assemble(List.of(record), errors));
        assertTrue(errors.isEmpty(), "a load-ending error is thrown, never added to the error list");
        return e;
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void theUpstreamEvil2RecordAssemblesFieldForField() {
        // lib/gamedata/slay.txt's EVIL_2 block, as C's handlers store it.
        Slay expected = new Slay("EVIL_2", "evil creatures", null, "smite", "pierces",
                MonsterRaceFlag.RF_EVIL, 2, 18, 200);

        Slay actual = one(evil2());

        assertEquals(expected, actual);
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void theRecordsComeBackInReverseFileOrder() {
        // slays[1] in C is the last record in the file.
        List<Slay> slays = assemble(List.of(
                record("EVIL_2", "EVIL", "", "2", "18", "200"),
                record("ORC_3", "ORC", "", "3", "25", "101"),
                record("UNDEAD_5", "UNDEAD", "", "5", "35", "130")), new ArrayList<>());

        assertEquals(List.of("UNDEAD_5", "ORC_3", "EVIL_2"), slays.stream().map(Slay::getCode).toList());
    }

    @Test
    void noRecordsGiveNoSlays() {
        assertTrue(assemble(List.of(), new ArrayList<>()).isEmpty());
    }

    @Test
    void theErrorListIsLeftAloneOnSuccess() {
        List<String> errors = new ArrayList<>(List.of("kept"));
        assemble(List.of(evil2()), errors);
        assertEquals(List.of("kept"), errors);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BANE", "EVIL", "EVIL_X", "NOSUCHFLAG_2", "evil_2"})
    void aCodeIsStoredAsWrittenAndNeverTakenApart(String code) {
        assertEquals(code, one(record(code, "EVIL", "", "2", "18", "200")).getCode());
    }

    @Test
    void duplicateCodesAreBothKept() {
        // C does not check codes for uniqueness: both go onto the chain.
        List<Slay> slays = assemble(List.of(
                record("EVIL_2", "EVIL", "", "2", "18", "200"),
                record("EVIL_2", "ORC", "", "3", "25", "101")), new ArrayList<>());

        assertEquals(2, slays.size());
        assertEquals(3, slays.get(0).getMultiplier());
        assertEquals(2, slays.get(1).getMultiplier());
    }

    // ---- Absent directives -------------------------------------------------

    @Test
    void anAbsentNameAndBothVerbsAreNullAsInCsZallocedRecord() {
        Slay slay = one(new SlayParseRecord("EVIL_2", "", "EVIL", "", "2", "18", "200", "", "", 7));

        assertNull(slay.getName());
        String text = slay.toString();
        assertTrue(text.contains("name='null'"), text);
        assertTrue(text.contains("meleeVerb='null'"), text);
        assertTrue(text.contains("rangedVerb='null'"), text);
    }

    @Test
    void anAbsentMultiplierOMultiplierAndPowerAreEachZero() {
        Slay slay = one(record("EVIL_2", "EVIL", "", "", "", ""));

        assertEquals(0, slay.getMultiplier());
        assertEquals(0, slay.getPower());
        assertTrue(slay.toString().contains("oMultiplier=0"), slay::toString);
    }

    @Test
    void oneAbsentNumberDoesNotZeroTheOthers() {
        Slay slay = one(record("EVIL_2", "EVIL", "", "2", "18", ""));

        assertEquals(2, slay.getMultiplier());
        assertEquals(0, slay.getPower());
        assertTrue(slay.toString().contains("oMultiplier=18"), slay::toString);
    }

    // ---- Race flag ----------------------------------------------------------

    @Test
    void everyRaceFlagTheUpstreamFileNamesResolves() {
        for (String flag : List.of("EVIL", "ANIMAL", "ORC", "TROLL", "GIANT", "DEMON", "DRAGON", "UNDEAD")) {
            Slay slay = one(record("X_2", flag, "", "2", "18", "100"));
            assertTrue(slay.toString().contains("raceFlag=RF_" + flag), slay::toString);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOTAFLAG", "evil", "Evil", "RF_EVIL", "MAX", "RF_MAX", " EVIL"})
    void anyNameLookupFlagWouldMissIsRefused(String flag) {
        // streq: exact case, no RF_ prefix; there is no MAX row in mon_race_flags.
        DataFileParseException e = refused(record("EVIL_2", flag, "", "2", "18", "200"));
        assertTrue(e.getMessage().contains("race flag"), e::getMessage);
        assertTrue(e.getMessage().contains(flag), e::getMessage);
    }

    @Test
    void raceFlagNoneIsRefusedAsLookupFlagSkipsIndexZero() {
        // FLAG_START is 1, so NONE at index 0 of mon_race_flags is never matched.
        DataFileParseException e = refused(record("EVIL_2", "NONE", "", "2", "18", "200"));
        assertTrue(e.getMessage().contains("NONE"), e::getMessage);
    }

    // ---- Base ---------------------------------------------------------------

    @Test
    void aBaseThatMatchesALoadedMonsterBaseResolvesHereWhereCRefusesEveryBase() {
        // C: slays are parsed before monster bases, so lookup_monster_base finds nothing and
        // parse_slay_base returns PARSE_ERROR_INVALID_MONSTER_BASE for any name. Accepted deviation.
        Slay slay = one(record("DRAGON_5", "", BASE, "5", "35", "110"));

        String text = slay.toString();
        assertFalse(text.contains("base=null"), text);
        assertTrue(text.contains("codeName='" + BASE + "'"), text);
        assertTrue(text.contains("raceFlag=null"), text);
    }

    @ParameterizedTest
    @ValueSource(strings = {"no such base", "Ancient Dragon", "ANCIENT DRAGON", "ancient dragon ", "dragon"})
    void aBaseNameNoLoadedBaseMatchesExactlyIsRefused(String base) {
        // lookup_monster_base compares with streq.
        DataFileParseException e = refused(record("DRAGON_5", "", base, "5", "35", "110"));
        assertTrue(e.getMessage().contains("monster base"), e::getMessage);
    }

    // ---- Race flag and base together, and neither ----------------------------

    @Test
    void aRaceFlagAndABaseTogetherAreRefused() {
        // PARSE_ERROR_INVALID_SLAY: "Flag or base, not both".
        DataFileParseException e = refused(record("EVIL_2", "EVIL", BASE, "2", "18", "200"));
        assertTrue(e.getMessage().contains("illegal state"), e::getMessage);
    }

    @Test
    void aRaceFlagAndAnUnknownBaseAreRefused() {
        refused(record("EVIL_2", "EVIL", "no such base", "2", "18", "200"));
    }

    @Test
    void neitherARaceFlagNorABaseLoadsWithBothNull() {
        // No handler and no check in finish_parse_slay requires a target.
        Slay slay = one(record("EVIL_2", "", "", "2", "18", "200"));

        String text = slay.toString();
        assertTrue(text.contains("base=null"), text);
        assertTrue(text.contains("raceFlag=null"), text);
        assertEquals(2, slay.getMultiplier());
    }

    // ---- The three numbers -----------------------------------------------------

    @Test
    void theUpstreamFigureSetsAreReadAsWritten() {
        Slay a = one(record("DRAGON_5", "DRAGON", "", "5", "35", "110"));
        assertEquals(5, a.getMultiplier());
        assertEquals(110, a.getPower());
        assertTrue(a.toString().contains("oMultiplier=35"), a::toString);
    }

    @Test
    void aLeadingZeroIsOctalAsStrtoulBaseZeroReadsIt() {
        Slay slay = one(record("EVIL_2", "EVIL", "", "010", "020", "0100"));

        assertEquals(8, slay.getMultiplier());
        assertEquals(64, slay.getPower());
        assertTrue(slay.toString().contains("oMultiplier=16"), slay::toString);
    }

    @Test
    void zeroAndAllZerosAreZero() {
        Slay slay = one(record("EVIL_2", "EVIL", "", "0", "00", "000"));

        assertEquals(0, slay.getMultiplier());
        assertEquals(0, slay.getPower());
    }

    @Test
    void theLargestIntIsKept() {
        assertEquals(Integer.MAX_VALUE, one(record("EVIL_2", "EVIL", "", "2147483647", "18", "200")).getMultiplier());
    }

    @Test
    void aFigureAboveIntMaxIsClampedWhereCWrapsIt() {
        // Accepted deviation (ParseUtils, "An integer too big for its C type is clamped, where C
        // wraps it"): strtoul gives 4294967296 and the 32-bit field keeps 0; here it is clamped.
        Slay slay = one(record("EVIL_2", "EVIL", "", "99999999999", "4294967296", "2147483648"));

        assertEquals(Integer.MAX_VALUE, slay.getMultiplier());
        assertEquals(Integer.MAX_VALUE, slay.getPower());
        assertTrue(slay.toString().contains("oMultiplier=" + Integer.MAX_VALUE), slay::toString);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-2", "-0", "-00", "-2147483648", "-99999999999"})
    void aLeadingMinusOnTheMultiplierIsRefusedAsParserGetUintRefusesIt(String text) {
        DataFileParseException e = refused(record("EVIL_2", "EVIL", "", text, "18", "200"));
        assertTrue(e.getMessage().contains("multiplier"), e::getMessage);
        assertTrue(e.getMessage().contains(text), e::getMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0", "-35"})
    void aLeadingMinusOnTheOMultiplierIsRefused(String text) {
        DataFileParseException e = refused(record("EVIL_2", "EVIL", "", "2", text, "200"));
        assertTrue(e.getMessage().contains("o-multiplier"), e::getMessage);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0", "-200"})
    void aLeadingMinusOnThePowerIsRefused(String text) {
        DataFileParseException e = refused(record("EVIL_2", "EVIL", "", "2", "18", text));
        assertTrue(e.getMessage().contains("power"), e::getMessage);
    }

    // ---- Stop the load ----------------------------------------------------------

    @Test
    void theMessageNamesTheRecordLineAndFile() {
        DataFileParseException e = refused(record("EVIL_2", "NOTAFLAG", "", "2", "18", "200"));

        assertTrue(e.getMessage().contains("line: 7"), e::getMessage);
        assertTrue(e.getMessage().contains("file: " + FILE), e::getMessage);
    }

    @Test
    void aBadRecordAfterGoodOnesEndsTheWholeLoad() {
        // C's first parser error ends the file; the earlier good records are not handed back.
        List<String> errors = new ArrayList<>();
        List<SlayParseRecord> records = List.of(
                evil2(),
                record("BAD_2", "NOTAFLAG", "", "2", "18", "200"),
                record("ORC_3", "ORC", "", "3", "25", "101"));

        assertThrows(DataFileParseException.class, () -> assemble(records, errors));
        assertTrue(errors.isEmpty());
    }
}
