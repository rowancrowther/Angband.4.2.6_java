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
import uk.co.jackoftradesltd.backend.parser.brand.BrandAssembler;
import uk.co.jackoftradesltd.backend.parser.brand.BrandParseRecord;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Brand;

import java.util.ArrayList;
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
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class BrandAssemblerTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class BrandAssemblerTest {

    private static final String FILE = "brand.txt";

    private static BrandParseRecord record(String code, String mult, String oMult, String power,
                                           String resist, String vuln) {
        return new BrandParseRecord(code, "fire", mult, oMult, power, "burn", resist, vuln, 7);
    }

    private static BrandParseRecord fire3(String resist, String vuln) {
        return record("FIRE_3", "3", "25", "113", resist, vuln);
    }

    private static List<Brand> assemble(List<BrandParseRecord> records, List<String> errors) {
        return new BrandAssembler().assemble(records, errors, FILE);
    }

    private static Brand one(BrandParseRecord record) {
        List<Brand> brands = assemble(List.of(record), new ArrayList<>());
        assertEquals(1, brands.size());
        return brands.getFirst();
    }

    private static void refused(BrandParseRecord record) {
        List<String> errors = new ArrayList<>();
        assertThrows(DataFileParseException.class, () -> assemble(List.of(record), errors));
        assertTrue(errors.isEmpty(), "a load-ending error is thrown, never added to the error list");
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void theUpstreamFire3RecordAssemblesFieldForField() {
        // lib/gamedata/brand.txt's FIRE_3 block, as C's handlers store it.
        List<String> errors = new ArrayList<>();
        Brand brand = assemble(List.of(fire3("IM_FIRE", "HURT_FIRE")), errors).getFirst();

        assertEquals(new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113), brand);
        assertTrue(errors.isEmpty());
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
                record("FIRE_3", "3", "25", "113", "IM_FIRE", "HURT_FIRE")), new ArrayList<>());

        assertEquals(List.of("FIRE_3", "ELEC_3", "ACID_3"), brands.stream().map(Brand::getCode).toList());
    }

    @Test
    void noRecordsGiveNoBrands() {
        assertTrue(assemble(List.of(), new ArrayList<>()).isEmpty());
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

    // ---- Stop-the-load -----------------------------------------------------

    @Test
    void oneBadRecordEndsTheWholeLoad() {
        // C stops at the first error; no partial list comes back.
        List<BrandParseRecord> records = List.of(
                record("ACID_3", "3", "25", "161", "IM_ACID", ""),
                record("BAD_3", "3", "25", "113", "NONE", ""),
                record("FIRE_3", "3", "25", "113", "IM_FIRE", ""));
        assertThrows(DataFileParseException.class, () -> assemble(records, new ArrayList<>()));
    }
}
