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
import uk.co.jackoftradesltd.backend.parser.brand.BrandAssembler;
import uk.co.jackoftradesltd.backend.parser.brand.BrandParseRecord;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Brand;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BrandAssembler}.
 *
 * <p>These construct {@link BrandParseRecord}s directly - no grammar, no reader - so values the
 * lexer would never produce (lower-case flags, an {@code RF_} prefix) can still reach the
 * assembler. End-to-end cases live in {@code BrandReaderTest}.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>flag resolution from {@code parse_brand_resist_flag} and {@code parse_brand_vuln_flag} in
 *       {@code obj-init.c}, which call {@code lookup_flag} in {@code datafile.c} against
 *       {@code mon_race_flags[]}. The search starts at {@code FLAG_START} (1), so {@code NONE} at
 *       index 0 is never matched; there is no {@code MAX} row; the compare is {@code streq}, so
 *       case-sensitive; and a miss is {@code PARSE_ERROR_INVALID_FLAG};</li>
 *   <li>a missing {@code vuln-flag} from {@code parse_brand_code}'s {@code mem_zalloc}, which
 *       leaves {@code vuln_flag} at 0, {@code NONE};</li>
 *   <li>the numbers from the {@code uint} branch in {@code parser.c}: {@code strtoul} in base 0,
 *       so a leading {@code 0} is octal, and {@code *tok == '-'} is
 *       {@code PARSE_ERROR_NOT_NUMBER}, whatever the value, so {@code -0} is refused too;</li>
 *   <li>the order from {@code parse_brand_code} pushing each brand onto the head of its list and
 *       {@code finish_parse_brand} copying from the head, so the last record comes first;</li>
 *   <li>the directives from {@code init_parse_brand}, which registers each on its own: after
 *       {@code code:} they come in any order, may be left out (the zeroed field stays, and
 *       {@code name} and {@code verb} stay {@code NULL}) and may repeat. A repeat overwrites the
 *       field, but each line is checked as it is read, so a bad number or flag ends the load even
 *       when a good one follows it;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class BrandAssemblerTest coded on 261023, commented in full on 261023, updated on 261113 once
 * {@link BrandParseRecord} carried every number and flag as the list of its lines.
 *
 * @author Rowan Crowther
 */
class BrandAssemblerTest {

    private static final String FILE = "brand.txt";

    /**
     * One directive's lines as the grammar hands them over: no entry for an empty string (the
     * directive was left out), otherwise the one line.
     */
    private static List<String> lines(String text) {
        return text.isEmpty() ? List.of() : List.of(text);
    }

    private static BrandParseRecord record(String code, String mult, String oMult, String power,
                                           String resist, String vuln) {
        return new BrandParseRecord(code, "fire", lines(mult), lines(oMult), lines(power), "burn",
                lines(resist), lines(vuln), 7);
    }

    /**
     * A FIRE_3 record whose five repeatable directives each carry the lines given, in file order.
     */
    private static BrandParseRecord repeated(List<String> mult, List<String> oMult, List<String> power,
                                             List<String> resist, List<String> vuln) {
        return new BrandParseRecord("FIRE_3", "fire", mult, oMult, power, "burn", resist, vuln, 7);
    }

    /**
     * A FIRE_3 record with good values everywhere except {@code directive}, which carries
     * {@code values} in file order.
     */
    private static BrandParseRecord repeating(String directive, List<String> values) {
        return switch (directive) {
            case "multiplier" -> repeated(values, List.of("25"), List.of("113"), List.of("IM_FIRE"), List.of());
            case "o-multiplier" -> repeated(List.of("3"), values, List.of("113"), List.of("IM_FIRE"), List.of());
            case "power" -> repeated(List.of("3"), List.of("25"), values, List.of("IM_FIRE"), List.of());
            case "resist-flag" -> repeated(List.of("3"), List.of("25"), List.of("113"), values, List.of());
            case "vuln-flag" -> repeated(List.of("3"), List.of("25"), List.of("113"), List.of("IM_FIRE"), values);
            default -> throw new IllegalArgumentException(directive);
        };
    }

    private static BrandParseRecord fire3(String resist, String vuln) {
        return record("FIRE_3", "3", "25", "113", resist, vuln);
    }

    private static List<Brand> assemble(List<BrandParseRecord> records) {
        return new BrandAssembler().assemble(records, FILE);
    }

    private static Brand one(BrandParseRecord record) {
        List<Brand> brands = assemble(List.of(record));
        assertEquals(1, brands.size());
        return brands.getFirst();
    }

    private static void refused(BrandParseRecord record) {
        assertThrows(DataFileParseException.class, () -> assemble(List.of(record)));
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void theUpstreamFire3RecordAssemblesFieldForField() {
        // lib/gamedata/brand.txt's FIRE_3 block, as C's handlers store it.
        Brand brand = assemble(List.of(fire3("IM_FIRE", "HURT_FIRE"))).getFirst();

        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113), brand);
    }

    @Test
    void aMissingVulnFlagIsTheZeroC_sZallocLeaves() {
        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_NONE, 3, 25, 113), one(fire3("IM_FIRE", "")));
    }

    @Test
    void theRecordsComeBackInReverseFileOrder() {
        // brands[1] in C is the last record in the file.
        List<Brand> brands = assemble(List.of(
                record("ACID_3", "3", "25", "161", "IM_ACID", ""),
                record("ELEC_3", "3", "25", "116", "IM_ELEC", ""),
                record("FIRE_3", "3", "25", "113", "IM_FIRE", "HURT_FIRE")));

        assertEquals(List.of("FIRE_3", "ELEC_3", "ACID_3"), brands.stream().map(Brand::getCode).toList());
    }

    @Test
    void noRecordsGiveNoBrands() {
        assertTrue(assemble(List.of()).isEmpty());
    }

    // ---- Flags -------------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {"NONE", "MAX", "IM_FOO", "im_fire", "RF_IM_FIRE", "Im_Fire"})
    void aResistFlagLookupFlagCannotFindEndsTheLoad(String flag) {
        refused(fire3(flag, ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"NONE", "MAX", "HURT_FOO", "hurt_fire", "RF_HURT_FIRE"})
    void aVulnFlagLookupFlagCannotFindEndsTheLoad(String flag) {
        refused(fire3("IM_FIRE", flag));
    }

    @Test
    void theFlagsAreIndependent() {
        // No cross-check in C: a resist flag and an unrelated vuln flag sit together.
        Brand brand = one(fire3("IM_ACID", "HURT_COLD"));
        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_ACID,
                MonsterRaceFlag.RF_HURT_COLD, 3, 25, 113), brand);
    }

    // ---- Numbers -----------------------------------------------------------

    @ParameterizedTest
    @CsvSource({
            "3,     3",
            "0,     0",
            "010,   8",   // strtoul base 0: leading 0 is octal
            "08,    0",   // octal stops at the 8
            "4294967296, 2147483647"   // accepted deviation: clamped where C wraps
    })
    void aMultiplierIsReadAsStrtoulInBaseZeroReadsIt(String text, int expected) {
        assertEquals(expected, one(record("FIRE_3", text, "25", "113", "IM_FIRE", "")).getMultiplier());
    }

    @ParameterizedTest
    @CsvSource({"113, 113", "0144, 100", "0, 0"})
    void aPowerIsReadAsStrtoulInBaseZeroReadsIt(String text, int expected) {
        assertEquals(expected, one(record("FIRE_3", "3", "25", text, "IM_FIRE", "")).getPower());
    }

    @Test
    void anOMultiplierIsStoredAsC_Reads() {
        // No getter for oMultiplier, so compare through equals.
        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_NONE, 3, 13, 113), one(record("FIRE_3", "3", "015", "113", "IM_FIRE", "")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-3", "-0", "-00", "-99999999999"})
    void aLeadingMinusOnTheMultiplierEndsTheLoad(String text) {
        refused(record("FIRE_3", text, "25", "113", "IM_FIRE", ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0"})
    void aLeadingMinusOnTheOMultiplierEndsTheLoad(String text) {
        refused(record("FIRE_3", "3", text, "113", "IM_FIRE", ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-113", "-0"})
    void aLeadingMinusOnThePowerEndsTheLoad(String text) {
        refused(record("FIRE_3", "3", "25", text, "IM_FIRE", ""));
    }

    // ---- Absent and repeated directives --------------------------------------

    @Test
    void aRecordOfACodeAloneIsAllZeroesAsMemZallocLeavesIt() {
        // The grammar hands over null for a left-out name or verb and empty lists for the rest.
        BrandParseRecord codeOnly = new BrandParseRecord("FIRE_3", null, List.of(), List.of(), List.of(),
                null, List.of(), List.of(), 7);

        assertEquals(new Brand("FIRE_3", null, null, MonsterRaceFlag.RF_NONE, MonsterRaceFlag.RF_NONE, 0, 0, 0),
                one(codeOnly));
    }

    @Test
    void aLeftOutNumberIsZeroAndALeftOutFlagIsNone() {
        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_NONE, MonsterRaceFlag.RF_NONE, 0, 25, 0),
                one(record("FIRE_3", "", "25", "", "", "")));
    }

    @Test
    void aRepeatedDirectiveKeepsTheLastLine() {
        // C overwrites the field on each line, so the last good one is what is stored.
        BrandParseRecord twice = repeated(List.of("3", "5"), List.of("25", "30"), List.of("113", "120"),
                List.of("IM_FIRE", "IM_COLD"), List.of("HURT_FIRE", "HURT_COLD"));

        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_COLD,
                MonsterRaceFlag.RF_HURT_COLD, 5, 30, 120), one(twice));
    }

    @ParameterizedTest
    @CsvSource({
            "multiplier,    -2,        3",
            "multiplier,    -0,        3",
            "o-multiplier,  -2,        25",
            "power,         -1,        113",
            "resist-flag,   NOTAFLAG,  IM_FIRE",
            "resist-flag,   NONE,      IM_FIRE",
            "vuln-flag,     NOTAFLAG,  HURT_FIRE",
            "vuln-flag,     NONE,      HURT_FIRE"
    })
    void aBadLineEndsTheLoadWhetherAGoodOneComesBeforeOrAfterIt(String directive, String bad, String good) {
        // C refuses the bad line when it reads it, so a good line either side cannot save the load.
        refused(repeating(directive, List.of(bad, good)));
        refused(repeating(directive, List.of(good, bad)));
        refused(repeating(directive, List.of(good, bad, good)));
    }

    @Test
    void theErrorNamesTheOneBadFlagNotTheWholeList() {
        DataFileParseException resist = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(repeating("resist-flag", List.of("IM_FIRE", "NOTAFLAG", "IM_COLD")))));
        DataFileParseException vuln = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(repeating("vuln-flag", List.of("NONE", "HURT_FIRE")))));

        assertTrue(resist.getMessage().contains("invalid resist flag value: NOTAFLAG"), resist::getMessage);
        assertFalse(resist.getMessage().contains("["), resist::getMessage);
        assertTrue(vuln.getMessage().contains("invalid vulnerable flag value: NONE"), vuln::getMessage);
        assertFalse(vuln.getMessage().contains("["), vuln::getMessage);
    }

    // ---- Stop-the-load -----------------------------------------------------

    @Test
    void oneBadRecordEndsTheWholeLoad() {
        // C stops at the first error; no partial list comes back.
        List<BrandParseRecord> records = List.of(
                record("ACID_3", "3", "25", "161", "IM_ACID", ""),
                record("BAD_3", "3", "25", "113", "NONE", ""),
                record("FIRE_3", "3", "25", "113", "IM_FIRE", ""));
        assertThrows(DataFileParseException.class, () -> assemble(records));
    }
}
