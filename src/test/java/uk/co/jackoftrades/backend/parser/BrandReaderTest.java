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
import uk.co.jackoftrades.middle.objects.Brand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link BrandReader}: file text -> {@code BrandLexer}/
 * {@code BrandGrammar} -> {@code BrandParseRecord} -> {@code BrandAssembler} -> {@link Brand}. These
 * pin the assembler half - resist-flag resolved unconditionally, the optional vuln-flag resolved
 * only when present (else the {@code RF_NONE} sentinel), the numeric-field parsing, and the
 * skip-and-continue soft-error channel that {@code GrammarDriver} threads through
 * {@link ParseResult}, plus the fail-closed hard-error channel.
 *
 * <p>Unlike the Slay suite, the brand assembler touches no {@code GameConstants} state (it resolves
 * only {@code MonsterRaceFlag} and builds {@link Brand}), so no reflection seeding is needed.
 *
 * @author Rowan Crowther
 */
class BrandReaderTest {

    private static final String REAL_FILE = "lib/gamedata/brand.txt";

    @TempDir
    Path tempDir;

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A complete brand record in the file's fixed directive order. Pass {@code ""} for
     * {@code vulnFlag} to omit the optional {@code vuln-flag:} line; {@code mult}/{@code oMult}/
     * {@code power} are strings so a test can feed an out-of-range value.
     */
    private static String brand(String code, String name, String verb, String mult,
                                String oMult, String power, String resistFlag, String vulnFlag) {
        String s = "code:" + code + "\n"
                + "name:" + name + "\n"
                + "verb:" + verb + "\n"
                + "multiplier:" + mult + "\n"
                + "o-multiplier:" + oMult + "\n"
                + "power:" + power + "\n"
                + "resist-flag:" + resistFlag + "\n";
        if (!vulnFlag.isEmpty()) {
            s += "vuln-flag:" + vulnFlag + "\n";
        }
        return s;
    }

    /**
     * The common valid case (fire brand, with a vuln flag).
     */
    private static String validBrand(String code) {
        return brand(code, "fire", "burn", "3", "25", "113", "IM_FIRE", "HURT_FIRE");
    }

    private ParseResult<Brand> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new BrandReader().parseWithResults(file.toString());
    }

    // ---- Happy path ------------------------------------------------------

    @Test
    void realFileLoadsAllTenBrandsWithNoErrors() throws IOException {
        ParseResult<Brand> result = new BrandReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(10, result.items().size());
        assertEquals("ACID_3", result.items().get(0).getCode());
    }

    @Test
    void syntheticBrandWithVulnFlagAssembles() throws IOException {
        ParseResult<Brand> result = load("ok-vuln.txt", withHeader(1, validBrand("FIRE_3")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).toString().contains("vulnerableFlag=RF_HURT_FIRE"),
                () -> result.items().get(0).toString());
    }

    @Test
    void syntheticBrandWithoutVulnFlagUsesTheNoneSentinel() throws IOException {
        // The optional vuln-flag is omitted, so the assembler must leave RF_NONE, not skip the record.
        ParseResult<Brand> result =
                load("ok-novuln.txt", withHeader(1, brand("ACID_3", "acid", "dissolve", "3", "25", "161", "IM_ACID", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).toString().contains("vulnerableFlag=RF_NONE"),
                () -> result.items().get(0).toString());
    }

    // ---- Assembler soft-error branches (skip-and-continue) ---------------

    @Test
    void illegalResistFlagIsReportedAndSkipped() throws IOException {
        ParseResult<Brand> result = load("bad-resist.txt",
                withHeader(1, brand("FIRE_3", "fire", "burn", "3", "25", "113", "NOTAFLAG", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal resist flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
    }

    @Test
    void illegalVulnFlagIsReportedAndSkipped() throws IOException {
        ParseResult<Brand> result = load("bad-vuln.txt",
                withHeader(1, brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE", "NOTAVULN")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal vulnerable flag") && e.contains("NOTAVULN")),
                result.errors()::toString);
    }

    @Test
    void overflowingMultiplierIsReportedAndSkipped() throws IOException {
        // multiplier: is an INTEGER token, so the only route into the NumberFormatException branch is
        // a value too large for int.
        ParseResult<Brand> result = load("bad-mult.txt",
                withHeader(1, brand("FIRE_3", "fire", "burn", "99999999999999", "25", "113", "IM_FIRE", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid multiplier")),
                result.errors()::toString);
    }

    @Test
    void overflowingOMultiplierIsReportedAndSkipped() throws IOException {
        ParseResult<Brand> result = load("bad-omult.txt",
                withHeader(1, brand("FIRE_3", "fire", "burn", "3", "99999999999999", "113", "IM_FIRE", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid o-multiplier")),
                result.errors()::toString);
    }

    @Test
    void overflowingPowerIsReportedAndSkipped() throws IOException {
        ParseResult<Brand> result = load("bad-power.txt",
                withHeader(1, brand("FIRE_3", "fire", "burn", "3", "25", "99999999999999", "IM_FIRE", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid power")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        ParseResult<Brand> result = load("bad-count.txt", withHeader(5, validBrand("FIRE_3")));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- Hard (fail-closed) parse errors ---------------------------------

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        // The file rule requires the record-count header; without it the parse fails and the hard
        // channel yields an empty result carrying the error.
        ParseResult<Brand> result = load("no-header.txt", validBrand("FIRE_3"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingCodeHeaderIsAHardError() throws IOException {
        // A record must open with code:; starting with name: is a syntax error.
        String noCode = "name:fire\nverb:burn\nmultiplier:3\no-multiplier:25\n"
                + "power:113\nresist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("no-code.txt", withHeader(1, noCode));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Two soft errors at once -----------------------------------------

    @Test
    void twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads() throws IOException {
        // Three records, declared count 3 (so no count error): one clean, one with a bad resist flag,
        // one with a bad vuln flag. The two bad ones are skipped independently, both errors are
        // recorded, and the clean record still loads - proving the errors accumulate rather than the
        // first one aborting the file.
        String body = validBrand("FIRE_3")
                + brand("ELEC_3", "lightning", "shock", "3", "25", "116", "NOTAFLAG", "")
                + brand("COLD_3", "cold", "freeze", "3", "25", "119", "IM_COLD", "NOTAVULN");
        ParseResult<Brand> result = load("two-errors.txt", withHeader(3, body));

        assertEquals(1, result.items().size());
        assertEquals("FIRE_3", result.items().get(0).getCode());

        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal resist flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("illegal vulnerable flag") && e.contains("NOTAVULN")),
                result.errors()::toString);
    }
}
