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
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.objects.Brand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
 * <p>The reader's own contract is pinned against values worked out from the upstream
 * {@code brand.txt} and {@code obj-init.c}: every field of all ten records, C's last-first list
 * order, directive order and repeats, free-text values and the missing-file {@link IOException}. A
 * final group pins the places the grammar and assembler differ from C, each named for the C
 * behaviour it departs from, so that a grammar or assembler fix shows up here as a test to flip.
 *
 * <p>Class BrandReaderTest coded before 261017, extended and commented in full on 261017.
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
        // C's brands[] runs in reverse file order, so the last record, POIS_2, is first
        assertEquals("POIS_2", result.items().get(0).getCode());
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

    // ---- Reader contract, worked out from the upstream brand.txt and obj-init.c ----

    /**
     * What {@link Brand#toString()} prints for a record, so a test can compare every field in one
     * assertion ({@link Brand} has no getters for the verb, flags or numbers).
     */
    private static String printed(String code, String name, String verb, String resist, String vuln,
                                  int mult, int oMult, int power) {
        return "Brand{code='" + code + "', name='" + name + "', verb='" + verb
                + "', resistFlag=RF_" + resist + ", vulnerableFlag=RF_" + vuln
                + ", multiplier=" + mult + ", oMultiplier=" + oMult + ", power=" + power + "}";
    }

    private static List<String> printedAll(List<Brand> brands) {
        List<String> out = new ArrayList<>();
        for (Brand b : brands) {
            out.add(b.toString());
        }
        return out;
    }

    @Test
    void realFileHoldsEveryFieldOfEveryRecordInCsLastFirstOrder() throws IOException {
        // Worked out from lib/gamedata/brand.txt in the upstream tree. parse_brand_code pushes each
        // record on the head of a chain and finish_parse_brand fills brands[1..] from that head, so
        // the file's last record is slot 1 and its first (ACID_3) is last. Only FIRE and COLD carry
        // a vuln-flag; the rest are zero-filled, which is RF_NONE.
        List<String> expected = List.of(
                printed("POIS_2", "poison", "sicken", "IM_POIS", "NONE", 2, 15, 111),
                printed("COLD_2", "cold", "chill", "IM_COLD", "HURT_COLD", 2, 15, 109),
                printed("FIRE_2", "fire", "singe", "IM_FIRE", "HURT_FIRE", 2, 15, 107),
                printed("ELEC_2", "lightning", "zap", "IM_ELEC", "NONE", 2, 15, 108),
                printed("ACID_2", "acid", "corrode", "IM_ACID", "NONE", 2, 15, 130),
                printed("POIS_3", "poison", "poison", "IM_POIS", "NONE", 3, 25, 122),
                printed("COLD_3", "cold", "freeze", "IM_COLD", "HURT_COLD", 3, 25, 119),
                printed("FIRE_3", "fire", "burn", "IM_FIRE", "HURT_FIRE", 3, 25, 113),
                printed("ELEC_3", "lightning", "shock", "IM_ELEC", "NONE", 3, 25, 116),
                printed("ACID_3", "acid", "dissolve", "IM_ACID", "NONE", 3, 25, 161));

        ParseResult<Brand> result = new BrandReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(expected, printedAll(result.items()));
    }

    @Test
    void realFileMatchesTheUpstreamFileApartFromTheRecordCountHeader() throws IOException {
        // The shipped copy adds one record-count: line and a blank line after it; the upstream file
        // has neither. Every other line is the same, so the expected values above are C's.
        Path upstream = Path.of(System.getProperty("user.home"), "Desktop/Angband-4.2.6/lib/gamedata/brand.txt");
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(upstream));

        List<String> ours = new ArrayList<>(Files.readAllLines(Path.of(REAL_FILE)));
        ours.removeIf(l -> l.startsWith("record-count:"));
        ours.remove(ours.indexOf("code:ACID_3") - 1);

        assertEquals(Files.readAllLines(upstream), ours);
    }

    @Test
    void parseReturnsTheSameBrandsAsParseWithResults() throws IOException {
        BrandReader reader = new BrandReader();

        assertEquals(printedAll(reader.parseWithResults(REAL_FILE).items()),
                printedAll(reader.parse(REAL_FILE)));
    }

    @Test
    void parseDropsTheSoftErrorsAndSimplyLosesTheBadRecord() throws IOException {
        Path file = tempDir.resolve("parse-bad.txt");
        Files.writeString(file, withHeader(2, validBrand("FIRE_3")
                + brand("ELEC_3", "lightning", "shock", "3", "25", "116", "NOTAFLAG", "")));

        List<Brand> brands = new BrandReader().parse(file.toString());

        assertEquals(1, brands.size());
        assertEquals("FIRE_3", brands.get(0).getCode());
    }

    @Test
    void missingFileIsAnIOExceptionWhereCQuits() {
        // parse_file_quit_not_found calls quit() on a file that cannot be opened; here it surfaces
        // as an exception for the caller to treat as fatal.
        assertThrows(IOException.class,
                () -> new BrandReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void listIsInReverseFileOrder() throws IOException {
        ParseResult<Brand> result = load("order.txt", withHeader(3,
                validBrand("FIRE_3") + validBrand("COLD_3") + validBrand("ACID_3")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("ACID_3", "COLD_3", "FIRE_3"),
                result.items().stream().map(Brand::getCode).toList());
    }

    @Test
    void skippedRecordLeavesTheOthersInReverseFileOrder() throws IOException {
        String body = validBrand("FIRE_3")
                + brand("COLD_3", "cold", "freeze", "3", "25", "119", "NOTAFLAG", "")
                + validBrand("ACID_3");
        ParseResult<Brand> result = load("skip-order.txt", withHeader(3, body));

        assertEquals(List.of("ACID_3", "FIRE_3"),
                result.items().stream().map(Brand::getCode).toList());
    }

    @Test
    void twoRecordsWithTheSameCodeBothLoadAsInC() throws IOException {
        // C never checks for a repeated code: parse_brand_code allocates a fresh record each time.
        ParseResult<Brand> result = load("dup.txt", withHeader(2,
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE", "")
                        + brand("FIRE_3", "fire", "singe", "2", "15", "107", "IM_FIRE", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertTrue(result.items().get(0).toString().contains("verb='singe'"));
        assertTrue(result.items().get(1).toString().contains("verb='burn'"));
    }

    @Test
    void freeTextValueKeepsColonsAndTrailingSpaces() throws IOException {
        // "str" fields consume the rest of the line untokenized, so a colon is ordinary text and the
        // trailing spaces stay in the value.
        ParseResult<Brand> result = load("free-text.txt", withHeader(1,
                brand("FIRE_3", "fire: hot  ", "burn it", "3", "25", "113", "IM_FIRE", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).toString().contains("name='fire: hot  '"),
                () -> result.items().get(0).toString());
        assertTrue(result.items().get(0).toString().contains("verb='burn it'"),
                () -> result.items().get(0).toString());
    }

    @Test
    void commentAndBlankLinesInsideARecordAreIgnored() throws IOException {
        String body = "code:FIRE_3\n# a note\nname:fire\n\nverb:burn\nmultiplier:3\n"
                + "o-multiplier:25\npower:113\n# another\nresist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("comments.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(printed("FIRE_3", "fire", "burn", "IM_FIRE", "NONE", 3, 25, 113),
                result.items().get(0).toString());
    }

    @Test
    void softErrorNamesTheLineOfTheRecordsCodeDirective() throws IOException {
        // Header on line 1, the good record (with a vuln-flag) on lines 2-9, the bad one starts on line 10.
        ParseResult<Brand> result = load("lines.txt", withHeader(2, validBrand("FIRE_3")
                + brand("ELEC_3", "lightning", "shock", "3", "25", "116", "NOTAFLAG", "")));

        assertTrue(result.errors().stream().anyMatch(e -> e.contains("line: 10")),
                result.errors()::toString);
    }

    @Test
    void emptyVulnFlagIsRfNoneAndAPresentOneIsNot() throws IOException {
        ParseResult<Brand> result = load("vuln.txt", withHeader(2,
                brand("ACID_3", "acid", "dissolve", "3", "25", "161", "IM_ACID", "")
                        + validBrand("FIRE_3")));

        assertEquals("RF_NONE", flagOf(result.items().get(1), "vulnerableFlag"));
        assertEquals("RF_HURT_FIRE", flagOf(result.items().get(0), "vulnerableFlag"));
        assertEquals("RF_IM_FIRE", flagOf(result.items().get(0), "resistFlag"));
    }

    private static String flagOf(Brand brand, String field) {
        String text = brand.toString();
        int from = text.indexOf(field + "=") + field.length() + 1;
        return text.substring(from, text.indexOf(',', from));
    }

    // ---- Where the grammar differs from C: each is a test to flip when the grammar is fixed ----

    @Test
    void directivesOutOfOrderAreAHardErrorHereWhereCTakesAnyOrder() throws IOException {
        // C registers each directive on its own and runs them in file order; verb: before name: is fine.
        String body = "code:FIRE_3\nverb:burn\nname:fire\nmultiplier:3\no-multiplier:25\n"
                + "power:113\nresist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("order-swap.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingPowerIsAHardErrorHereWhereCLeavesItZero() throws IOException {
        String body = "code:FIRE_3\nname:fire\nverb:burn\nmultiplier:3\no-multiplier:25\n"
                + "resist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("no-power.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingResistFlagIsAHardErrorHereWhereCLeavesFlagZero() throws IOException {
        String body = "code:FIRE_3\nname:fire\nverb:burn\nmultiplier:3\no-multiplier:25\npower:113\n";
        ParseResult<Brand> result = load("no-resist.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void oneBadRecordSyntaxCostsTheWholeFileHereWhereCStopsAtThatRecord() throws IOException {
        // The first record is complete and valid, yet nothing is returned because the second is
        // missing its power: line.
        String bad = "code:COLD_3\nname:cold\nverb:freeze\nmultiplier:3\no-multiplier:25\n"
                + "resist-flag:IM_COLD\n";
        ParseResult<Brand> result = load("one-bad.txt", withHeader(2, validBrand("FIRE_3") + bad));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void repeatedNameIsAHardErrorHereWhereCOverwrites() throws IOException {
        // parse_brand_name frees the old name and stores the new one, so C keeps the last.
        String body = "code:FIRE_3\nname:fire\nname:flame\nverb:burn\nmultiplier:3\n"
                + "o-multiplier:25\npower:113\nresist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("two-names.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void headerOnlyFileIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        // C: an empty chain, brand_max ends at 1. Here the file rule needs at least one record.
        ParseResult<Brand> result = load("header-only.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void negativeMultiplierLoadsHereWhereCParserGetUintRejectsIt() throws IOException {
        // parser_parse refuses a uint token that starts with '-' (PARSE_ERROR_NOT_NUMBER).
        ParseResult<Brand> result = load("neg-mult.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "-2", "25", "113", "IM_FIRE", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).toString().contains("multiplier=-2"),
                () -> result.items().get(0).toString());
    }

    @Test
    void leadingZeroNumberIsDecimalHereWhereCReadsOctal() throws IOException {
        // strtoul(tok, &z, 0): "010" is 8 in C.
        ParseResult<Brand> result = load("octal.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "010", "25", "113", "IM_FIRE", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).toString().contains("multiplier=10,"),
                () -> result.items().get(0).toString());
    }

    @Test
    void hexNumberIsAHardErrorHereWhereCReadsItAsSixteen() throws IOException {
        // strtoul(tok, &z, 0): "0x10" is 16 in C.
        ParseResult<Brand> result = load("hex.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "0x10", "25", "113", "IM_FIRE", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void spaceAfterAFlagIsIgnoredHereWhereCLooksUpTheTextWithTheSpace() throws IOException {
        // C's sym token is "IM_FIRE " (strtok splits on ':' only), which lookup_flag cannot match,
        // so parse_brand_resist_flag returns PARSE_ERROR_INVALID_FLAG. Here the lexer's end-of-line
        // rule swallows the spaces and the flag resolves.
        ParseResult<Brand> result = load("flag-space.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE ", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void lowerCaseFlagIsAHardLexerErrorWhereCReportsAnInvalidFlag() throws IOException {
        // Both reject it; this pins that here it is a syntax error that costs the whole file, not
        // the soft "illegal resist flag" that skips one record.
        ParseResult<Brand> result = load("lower.txt", withHeader(2, validBrand("FIRE_3")
                + brand("COLD_3", "cold", "freeze", "3", "25", "119", "im_cold", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Where the assembler differs from C ----

    @Test
    void resistFlagNoneLoadsHereWhereCLookupFlagSkipsIt() throws IOException {
        // lookup_flag starts at FLAG_START (1), so the NONE entry at index 0 of mon_race_flags is
        // never matched and parse_brand_resist_flag returns PARSE_ERROR_INVALID_FLAG.
        ParseResult<Brand> result = load("resist-none.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "NONE", "")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("RF_NONE", flagOf(result.items().get(0), "resistFlag"));
    }

    @Test
    void vulnFlagNoneLoadsHereWhereCLookupFlagSkipsIt() throws IOException {
        ParseResult<Brand> result = load("vuln-none.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE", "NONE")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("RF_NONE", flagOf(result.items().get(0), "vulnerableFlag"));
    }

    @Test
    void resistFlagMaxIsRejectedAsInC() throws IOException {
        // The RF_MAX terminator is not in mon_race_flags[] as a name, and has no enum constant here.
        ParseResult<Brand> result = load("resist-max.txt", withHeader(1,
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "MAX", "")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("illegal resist flag")),
                result.errors()::toString);
    }

    @Test
    void moreThanTwoHundredFiftyFourBrandsAllLoadHereWhereCFailsTheParse() throws IOException {
        // finish_parse_brand stops counting at 254 and returns PARSE_ERROR_TOO_MANY_ENTRIES.
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            body.append(brand("B_" + i, "fire", "burn", "3", "25", "113", "IM_FIRE", ""));
        }
        ParseResult<Brand> result = load("many.txt", withHeader(255, body.toString()));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(255, result.items().size());
    }
}
