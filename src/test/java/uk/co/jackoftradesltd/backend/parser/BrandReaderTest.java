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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
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
 * only when present (else the {@code RF_NONE} sentinel), the numeric-field parsing, the
 * load-ending {@link DataFileParseException} the assembler throws where a C handler returns a
 * parser error, and the fail-closed hard-error channel {@code GrammarDriver} threads through
 * {@link ParseResult}.
 *
 * <p>Unlike the Slay suite, the brand assembler touches no {@code GameConstants} state (it resolves
 * only {@code MonsterRaceFlag} and builds {@link Brand}), so no reflection seeding is needed.
 *
 * <p>The reader's own contract is pinned against values worked out from the upstream
 * {@code brand.txt} and {@code obj-init.c}: every field of all ten records, C's last-first list
 * order, directive order, absence and repeats, free-text values and the missing-file
 * {@link IOException}. A final group pins the places the grammar and assembler differ from C, each
 * named for the C behaviour it departs from, so that a grammar or assembler fix shows up here as a
 * test to flip.
 *
 * <p>Class BrandReaderTest coded before 261017, extended and commented in full on 261017, updated
 * on 261023 once the assembler's errors ended the load and it read octal and refused {@code NONE}
 * and a leading {@code -} as C does, and updated on 261113 once the grammar took the directives in
 * any order, left out or repeated, and an empty file, so the six pins that said otherwise now
 * agree with C.
 *
 * @author Rowan Crowther
 */
class BrandReaderTest {

    private static final String REAL_FILE = "lib/gamedata/brand.txt";

    @TempDir
    Path tempDir;

    // ---- fixture helpers -------------------------------------------------


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

        assertEquals(10, result.items().size());
        // C's brands[] runs in reverse file order, so the last record, POIS_2, is first
        assertEquals("POIS_2", result.items().get(0).getCode());
    }

    @Test
    void syntheticBrandWithVulnFlagAssembles() throws IOException {
        ParseResult<Brand> result = load("ok-vuln.txt", validBrand("FIRE_3"));

        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).toString().contains("vulnerableFlag=RF_HURT_FIRE"),
                () -> result.items().get(0).toString());
    }

    @Test
    void syntheticBrandWithoutVulnFlagUsesTheNoneSentinel() throws IOException {
        // The optional vuln-flag is omitted, so the assembler must leave RF_NONE, not skip the record.
        ParseResult<Brand> result =
                load("ok-novuln.txt", brand("ACID_3", "acid", "dissolve", "3", "25", "161", "IM_ACID", ""));

        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).toString().contains("vulnerableFlag=RF_NONE"),
                () -> result.items().get(0).toString());
    }

    // ---- Assembler errors (end the load, as C's first parser error does) ---

    @Test
    void illegalResistFlagEndsTheLoad() {
        // parse_brand_resist_flag: lookup_flag misses, PARSE_ERROR_INVALID_FLAG.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-resist.txt",
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "NOTAFLAG", "")));

        assertTrue(e.getMessage().contains("invalid resist flag value: NOTAFLAG"), e::getMessage);
    }

    @Test
    void illegalVulnFlagEndsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-vuln.txt",
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE", "NOTAVULN")));

        assertTrue(e.getMessage().contains("invalid vulnerable flag value: NOTAVULN"), e::getMessage);
    }

    @Test
    void overflowingMultiplierIsClampedHereWhereCWraps() throws IOException {
        // Accepted deviation (ParseUtils): C keeps the low 32 bits, here the value clamps.
        ParseResult<Brand> result = load("big-mult.txt",
                brand("FIRE_3", "fire", "burn", "99999999999999", "25", "113", "IM_FIRE", ""));

        assertEquals(Integer.MAX_VALUE, result.items().get(0).getMultiplier());
    }

    @Test
    void overflowingOMultiplierIsClampedHereWhereCWraps() throws IOException {
        ParseResult<Brand> result = load("big-omult.txt",
                brand("FIRE_3", "fire", "burn", "3", "99999999999999", "113", "IM_FIRE", ""));

        assertTrue(result.items().get(0).toString().contains("oMultiplier=" + Integer.MAX_VALUE),
                () -> result.items().get(0).toString());
    }

    @Test
    void overflowingPowerIsClampedHereWhereCWraps() throws IOException {
        ParseResult<Brand> result = load("big-power.txt",
                brand("FIRE_3", "fire", "burn", "3", "25", "99999999999999", "IM_FIRE", ""));

        assertEquals(Integer.MAX_VALUE, result.items().get(0).getPower());
    }

    // ---- Hard (fail-closed) parse errors ---------------------------------

    @Test
    void missingCodeHeaderIsAHardError() throws IOException {
        // A record must open with code:; starting with name: is a syntax error.
        String noCode = "name:fire\nverb:burn\nmultiplier:3\no-multiplier:25\n"
                + "power:113\nresist-flag:IM_FIRE\n";
        assertThrows(ParseCancellationException.class, () -> load("no-code.txt", noCode));

    }

    // ---- Two bad records at once -----------------------------------------

    @Test
    void theFirstOfTwoBadRecordsEndsTheLoad() {
        // C stops at its first parser error, so only the bad resist flag on ELEC_3 is reported and
        // the clean FIRE_3 before it is not returned.
        String body = validBrand("FIRE_3")
                + brand("ELEC_3", "lightning", "shock", "3", "25", "116", "NOTAFLAG", "")
                + brand("COLD_3", "cold", "freeze", "3", "25", "119", "IM_COLD", "NOTAVULN");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("two-errors.txt", body));

        assertTrue(e.getMessage().contains("NOTAFLAG"), e::getMessage);
        assertFalse(e.getMessage().contains("NOTAVULN"), e::getMessage);
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

        assertEquals(expected, printedAll(result.items()));
    }

    /**
     * With the {@code record-count:} header gone, the shipped file is the upstream file line for
     * line, so the expected values above are C's.
     *
     * <p>Function realFileMatchesTheUpstreamFile coded on 261104, commented in full on 261104. It
     * replaces realFileMatchesTheUpstreamFileApartFromTheRecordCountHeader.
     */
    @Test
    void realFileMatchesTheUpstreamFile() throws IOException {
        Path upstream = Path.of(System.getProperty("user.home"), "Desktop/Angband-4.2.6/lib/gamedata/brand.txt");
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(upstream));

        assertEquals(Files.readAllLines(upstream), Files.readAllLines(Path.of(REAL_FILE)));
    }

    @Test
    void parseReturnsTheSameBrandsAsParseWithResults() throws IOException {
        BrandReader reader = new BrandReader();

        assertEquals(printedAll(reader.parseWithResults(REAL_FILE).items()),
                printedAll(reader.parse(REAL_FILE)));
    }

    @Test
    void parseAlsoEndsTheLoadOnABadRecord() throws IOException {
        Path file = tempDir.resolve("parse-bad.txt");
        Files.writeString(file, validBrand("FIRE_3")
                + brand("ELEC_3", "lightning", "shock", "3", "25", "116", "NOTAFLAG", ""));

        assertThrows(DataFileParseException.class, () -> new BrandReader().parse(file.toString()));
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
        ParseResult<Brand> result = load("order.txt", validBrand("FIRE_3") + validBrand("COLD_3") + validBrand("ACID_3"));

        assertEquals(List.of("ACID_3", "COLD_3", "FIRE_3"),
                result.items().stream().map(Brand::getCode).toList());
    }

    @Test
    void aBadMiddleRecordReturnsNoBrandsAtAll() {
        // No partial list: C's load stops at COLD_3, and so does this one.
        String body = validBrand("FIRE_3")
                + brand("COLD_3", "cold", "freeze", "3", "25", "119", "NOTAFLAG", "")
                + validBrand("ACID_3");

        assertThrows(DataFileParseException.class, () -> load("skip-order.txt", body));
    }

    @Test
    void twoRecordsWithTheSameCodeBothLoadAsInC() throws IOException {
        // C never checks for a repeated code: parse_brand_code allocates a fresh record each time.
        ParseResult<Brand> result = load("dup.txt", brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE", "")
                        + brand("FIRE_3", "fire", "singe", "2", "15", "107", "IM_FIRE", ""));

        assertEquals(2, result.items().size());
        assertTrue(result.items().get(0).toString().contains("verb='singe'"));
        assertTrue(result.items().get(1).toString().contains("verb='burn'"));
    }

    @Test
    void freeTextValueKeepsColonsAndTrailingSpaces() throws IOException {
        // "str" fields consume the rest of the line untokenized, so a colon is ordinary text and the
        // trailing spaces stay in the value.
        ParseResult<Brand> result = load("free-text.txt", brand("FIRE_3", "fire: hot  ", "burn it", "3", "25", "113", "IM_FIRE", ""));

        assertTrue(result.items().get(0).toString().contains("name='fire: hot  '"),
                () -> result.items().get(0).toString());
        assertTrue(result.items().get(0).toString().contains("verb='burn it'"),
                () -> result.items().get(0).toString());
    }

    @Test
    void commentAndBlankLinesInsideARecordAreIgnored() throws IOException {
        String body = "code:FIRE_3\n# a note\nname:fire\n\nverb:burn\nmultiplier:3\n"
                + "o-multiplier:25\npower:113\n# another\nresist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("comments.txt", body);

        assertEquals(printed("FIRE_3", "fire", "burn", "IM_FIRE", "NONE", 3, 25, 113),
                result.items().get(0).toString());
    }

    @Test
    void theErrorNamesTheLineOfTheRecordsCodeDirective() {
        // Header on line 1, the good record (with a vuln-flag) on lines 2-9, the bad one starts on line 10.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("lines.txt", validBrand("FIRE_3")
                        + brand("ELEC_3", "lightning", "shock", "3", "25", "116", "NOTAFLAG", "")));

        assertTrue(e.getMessage().contains("line: 9"), e::getMessage);
    }

    @Test
    void emptyVulnFlagIsRfNoneAndAPresentOneIsNot() throws IOException {
        ParseResult<Brand> result = load("vuln.txt", brand("ACID_3", "acid", "dissolve", "3", "25", "161", "IM_ACID", "")
                        + validBrand("FIRE_3"));

        assertEquals("RF_NONE", flagOf(result.items().get(1), "vulnerableFlag"));
        assertEquals("RF_HURT_FIRE", flagOf(result.items().get(0), "vulnerableFlag"));
        assertEquals("RF_IM_FIRE", flagOf(result.items().get(0), "resistFlag"));
    }

    private static String flagOf(Brand brand, String field) {
        String text = brand.toString();
        int from = text.indexOf(field + "=") + field.length() + 1;
        return text.substring(from, text.indexOf(',', from));
    }

    // ---- Directive order, absence and repeats: C runs each directive on its own line ----
    //
    // init_parse_brand registers the eight directives separately, so after code: they come in any
    // order, any of them may be left out (the zero-filled field stays), and a repeat overwrites.
    // A number or flag is checked on every line it appears on, so an earlier bad one ends the load
    // even when a later one is good.

    @Test
    void directivesInAnyOrderLoadAsInC() throws IOException {
        // verb: before name:, resist-flag: ahead of the numbers, vuln-flag: ahead of resist-flag:.
        String body = "code:FIRE_3\nverb:burn\nvuln-flag:HURT_FIRE\nresist-flag:IM_FIRE\n"
                + "power:113\nname:fire\no-multiplier:25\nmultiplier:3\n";
        ParseResult<Brand> result = load("order-swap.txt", body);

        assertEquals(List.of(printed("FIRE_3", "fire", "burn", "IM_FIRE", "HURT_FIRE", 3, 25, 113)),
                printedAll(result.items()));
    }

    @Test
    void aLeftOutPowerIsZeroAsInC() throws IOException {
        String body = "code:FIRE_3\nname:fire\nverb:burn\nmultiplier:3\no-multiplier:25\n"
                + "resist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("no-power.txt", body);

        assertEquals(List.of(printed("FIRE_3", "fire", "burn", "IM_FIRE", "NONE", 3, 25, 0)),
                printedAll(result.items()));
    }

    @Test
    void aLeftOutResistFlagIsRfNoneAsInC() throws IOException {
        String body = "code:FIRE_3\nname:fire\nverb:burn\nmultiplier:3\no-multiplier:25\npower:113\n";
        ParseResult<Brand> result = load("no-resist.txt", body);

        assertEquals(List.of(printed("FIRE_3", "fire", "burn", "NONE", "NONE", 3, 25, 113)),
                printedAll(result.items()));
    }

    @Test
    void leftOutMultipliersAreZeroAsInC() throws IOException {
        String body = "code:FIRE_3\nname:fire\nverb:burn\npower:113\nresist-flag:IM_FIRE\n";
        ParseResult<Brand> result = load("no-mults.txt", body);

        assertEquals(List.of(printed("FIRE_3", "fire", "burn", "IM_FIRE", "NONE", 0, 0, 113)),
                printedAll(result.items()));
    }

    @Test
    void aCodeAloneLoadsWithEveryOtherFieldZeroedAsInC() throws IOException {
        // parse_brand_code mem_zallocs the record: name and verb stay NULL, the numbers 0, the flags NONE.
        ParseResult<Brand> result = load("code-only.txt", "code:FIRE_3\n");

        assertEquals(1, result.items().size());
        assertEquals(new Brand("FIRE_3", null, null, MonsterRaceFlag.RF_NONE, MonsterRaceFlag.RF_NONE, 0, 0, 0),
                result.items().get(0));
    }

    @Test
    void aCodeAloneBetweenTwoFullRecordsDoesNotSwallowEitherNeighbour() throws IOException {
        ParseResult<Brand> result = load("code-between.txt", validBrand("ACID_3") + "code:ELEC_3\n" + validBrand("FIRE_3"));

        assertEquals(List.of("FIRE_3", "ELEC_3", "ACID_3"), result.items().stream().map(Brand::getCode).toList());
        assertNull(result.items().get(1).getName());
        assertEquals(113, result.items().get(0).getPower());
    }

    @Test
    void oneRecordMissingADirectiveDoesNotCostTheFile() throws IOException {
        // The second record has no power: line. C zero-fills it and carries on with every record.
        String missingPower = "code:COLD_3\nname:cold\nverb:freeze\nmultiplier:3\no-multiplier:25\n"
                + "resist-flag:IM_COLD\n";
        ParseResult<Brand> result = load("one-short.txt", validBrand("FIRE_3") + missingPower);

        assertEquals(List.of(printed("COLD_3", "cold", "freeze", "IM_COLD", "NONE", 3, 25, 0),
                printed("FIRE_3", "fire", "burn", "IM_FIRE", "HURT_FIRE", 3, 25, 113)),
                printedAll(result.items()));
    }

    @Test
    void aRepeatedDirectiveKeepsTheLastValueAsCOverwrites() throws IOException {
        // parse_brand_name and parse_brand_verb free the old string and store the new one; the other
        // six assign over the old value. Every value here is good, so the last one is what is kept.
        String body = "code:FIRE_3\nname:fire\nname:flame\nverb:burn\nverb:scorch\n"
                + "multiplier:3\nmultiplier:5\no-multiplier:25\no-multiplier:30\n"
                + "power:113\npower:120\nresist-flag:IM_FIRE\nresist-flag:IM_COLD\n"
                + "vuln-flag:HURT_FIRE\nvuln-flag:HURT_COLD\n";
        ParseResult<Brand> result = load("repeats.txt", body);

        assertEquals(List.of(printed("FIRE_3", "flame", "scorch", "IM_COLD", "HURT_COLD", 5, 30, 120)),
                printedAll(result.items()));
    }

    /**
     * A good record with {@code line} added to it, ahead of the record's own directives
     * ({@code first}) or after them. The record's own {@code multiplier:3}, {@code o-multiplier:25},
     * {@code power:113}, {@code resist-flag:IM_FIRE} and {@code vuln-flag:HURT_FIRE} stay good, so
     * the added line is the only bad one.
     */
    private static String withAnExtraLine(String line, boolean first) {
        String record = validBrand("FIRE_3");
        if (first) {
            return record.replaceFirst("code:FIRE_3\n", "code:FIRE_3\n" + line + "\n");
        }
        return record + line + "\n";
    }

    @ParameterizedTest
    @CsvSource({
            "multiplier,    -2",
            "multiplier,    -0",
            "o-multiplier,  -2",
            "power,         -1",
            "resist-flag,   NOTAFLAG",
            "resist-flag,   NONE",
            "vuln-flag,     NOTAFLAG",
            "vuln-flag,     NONE"
    })
    void aBadRepeatedLineEndsTheLoadWhereverItSits(String directive, String bad) {
        // C checks the line when it reads it: parser_parse refuses a '-' uint, lookup_flag misses.
        String line = directive + ":" + bad;

        assertThrows(DataFileParseException.class, () -> load("bad-first.txt", withAnExtraLine(line, true)));
        assertThrows(DataFileParseException.class, () -> load("bad-last.txt", withAnExtraLine(line, false)));
    }

    @Test
    void theErrorNamesTheOneBadRepeatedFlagNotTheList() {
        DataFileParseException resist = assertThrows(DataFileParseException.class,
                () -> load("bad-resist-repeat.txt", withAnExtraLine("resist-flag:NOTAFLAG", true)));
        DataFileParseException vuln = assertThrows(DataFileParseException.class,
                () -> load("bad-vuln-repeat.txt", withAnExtraLine("vuln-flag:NOTAVULN", false)));

        assertTrue(resist.getMessage().contains("invalid resist flag value: NOTAFLAG"), resist::getMessage);
        assertFalse(resist.getMessage().contains("["), resist::getMessage);
        assertTrue(vuln.getMessage().contains("invalid vulnerable flag value: NOTAVULN"), vuln::getMessage);
        assertFalse(vuln.getMessage().contains("["), vuln::getMessage);
    }

    @Test
    void aFileWithNoRecordsLoadsNoBrandsAsInC() throws IOException {
        // C: an empty chain, brand_max ends at 1. Neither an empty file nor one of only comments and
        // blank lines is an error.
        assertTrue(load("empty.txt", "").items().isEmpty());
        assertTrue(load("comments-only.txt", "# nothing here\n\n# or here\n").items().isEmpty());
    }

    @Test
    void aDirectiveBeforeAnyCodeIsStillAHardError() {
        // C: PARSE_ERROR_MISSING_RECORD_HEADER. The grammar refuses it as a syntax error instead.
        assertThrows(ParseCancellationException.class, () -> load("no-header.txt", "power:113\n" + validBrand("FIRE_3")));
    }

    @Test
    void negativeMultiplierEndsTheLoadAsCParserGetUintDoes() {
        // parser_parse refuses a uint token that starts with '-' (PARSE_ERROR_NOT_NUMBER).
        assertThrows(DataFileParseException.class, () -> load("neg-mult.txt", brand("FIRE_3", "fire", "burn", "-2", "25", "113", "IM_FIRE", "")));
    }

    @Test
    void minusZeroEndsTheLoadAsInC() {
        // The refusal is on the leading '-', not the value, so -0 is refused too.
        assertThrows(DataFileParseException.class, () -> load("neg-zero.txt", brand("FIRE_3", "fire", "burn", "3", "25", "-0", "IM_FIRE", "")));
    }

    @Test
    void leadingZeroNumberIsOctalAsInC() throws IOException {
        // strtoul(tok, &z, 0): "010" is 8 in C.
        ParseResult<Brand> result = load("octal.txt", brand("FIRE_3", "fire", "burn", "010", "25", "113", "IM_FIRE", ""));

        assertEquals(8, result.items().get(0).getMultiplier());
    }

    @Test
    void hexNumberIsSixteenAsStrtoulBaseZeroReadsIt() throws IOException {
        // strtoul(tok, &z, 0): "0x10" is 16 in C. Each of the three uint directives reads it.
        ParseResult<Brand> result = load("hex.txt", brand("FIRE_3", "fire", "burn", "0x10", "0x20", "0x71", "IM_FIRE", ""));

        Brand brand = result.items().get(0);
        assertEquals(16, brand.getMultiplier());
        assertTrue(brand.toString().contains("oMultiplier=32"), brand::toString);
        assertEquals(113, brand.getPower());
    }

    @Test
    void anUpperCasePrefixAndUpperCaseDigitsAreHexToo() throws IOException {
        // strtoul accepts "0X" as well as "0x", and hex digits in either case: "0X1f" is 31.
        ParseResult<Brand> result = load("hex-upper.txt", brand("FIRE_3", "fire", "burn", "0X1f", "0x1F", "113", "IM_FIRE", ""));

        Brand brand = result.items().get(0);
        assertEquals(31, brand.getMultiplier());
        assertTrue(brand.toString().contains("oMultiplier=31"), brand::toString);
    }

    @Test
    void aPlusSignBeforeAHexNumberIsAccepted() throws IOException {
        // strtoul takes an optional '+' ahead of the prefix: "+0x10" is 16. Only '-' is refused.
        ParseResult<Brand> result = load("plus-hex.txt", brand("FIRE_3", "fire", "burn", "+0x10", "25", "113", "IM_FIRE", ""));

        assertEquals(16, result.items().get(0).getMultiplier());
    }

    @Test
    void aMinusSignBeforeAHexNumberEndsTheLoad() {
        // The refusal is on the leading '-', whatever follows it, so "-0x10" goes the way "-2" does.
        assertThrows(DataFileParseException.class, () -> load("neg-hex.txt", brand("FIRE_3", "fire", "burn", "-0x10", "25", "113", "IM_FIRE", "")));
    }

    @Test
    void spaceAfterAFlagIsIgnoredHereWhereCLooksUpTheTextWithTheSpace() throws IOException {
        // C's sym token is "IM_FIRE " (strtok splits on ':' only), which lookup_flag cannot match,
        // so parse_brand_resist_flag returns PARSE_ERROR_INVALID_FLAG. Here the lexer's end-of-line
        // rule swallows the spaces and the flag resolves.
        ParseResult<Brand> result = load("flag-space.txt", brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE ", ""));

        assertEquals(1, result.items().size());
    }

    @Test
    void lowerCaseFlagIsAHardLexerErrorWhereCReportsAnInvalidFlag() throws IOException {
        // Both reject it; this pins that here it is a syntax error that costs the whole file, not
        // the soft "illegal resist flag" that skips one record.
        assertThrows(ParseCancellationException.class, () -> load("lower.txt", validBrand("FIRE_3")
                + brand("COLD_3", "cold", "freeze", "3", "25", "119", "im_cold", "")));

    }

    // ---- Flag edge cases the assembler now handles as C does ----

    @Test
    void resistFlagNoneEndsTheLoadAsCLookupFlagSkipsIt() {
        // lookup_flag starts at FLAG_START (1), so the NONE entry at index 0 of mon_race_flags is
        // never matched and parse_brand_resist_flag returns PARSE_ERROR_INVALID_FLAG.
        assertThrows(DataFileParseException.class, () -> load("resist-none.txt", brand("FIRE_3", "fire", "burn", "3", "25", "113", "NONE", "")));
    }

    @Test
    void vulnFlagNoneEndsTheLoadAsCLookupFlagSkipsIt() {
        assertThrows(DataFileParseException.class, () -> load("vuln-none.txt", brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE", "NONE")));
    }

    @Test
    void resistFlagMaxIsRejectedAsInC() {
        // The RF_MAX terminator is not in mon_race_flags[] as a name, and has no enum constant here.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("resist-max.txt",
                brand("FIRE_3", "fire", "burn", "3", "25", "113", "MAX", "")));

        assertTrue(e.getMessage().contains("invalid resist flag value: MAX"), e::getMessage);
    }

    // ---- The 254-brand limit ----

    @Test
    void exactlyTwoHundredFiftyFourBrandsLoadAsInC() throws IOException {
        // finish_parse_brand counts up to 254 without error; the 255th entry is the one it refuses.
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < 254; i++) {
            body.append(brand("B_" + i, "fire", "burn", "3", "25", "113", "IM_FIRE", ""));
        }
        ParseResult<Brand> result = load("max.txt", body.toString());

        assertEquals(254, result.items().size());
    }

    @Test
    void theTwoHundredFiftyFifthBrandStopsTheLoadAsInC() {
        // finish_parse_brand breaks when brand_max is already 254 and returns
        // PARSE_ERROR_TOO_MANY_ENTRIES.
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            body.append(brand("B_" + i, "fire", "burn", "3", "25", "113", "IM_FIRE", ""));
        }

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("many.txt", body.toString()));

        assertTrue(e.getMessage().contains("overflow"), e::getMessage);
    }

    // ---- Stage 6 item 1: the shared CommentsAndEol rules, proved on one lexer -------------------
    //
    // BrandLexer imports CommentsAndEol and keeps every directive in the default mode, so it shows
    // the shared rules on their own. The moded half of the same work is in ItemObjectReaderTest.

    /**
     * C drops every leading blank before it reads the directive, and its reader turns a tab into
     * spaces, so a tab is indentation.
     *
     * <p>Function aLeadingTabBeforeADirectiveLoadsAsInC coded on 261109, commented in full on 261109.
     */
    @Test
    void aLeadingTabBeforeADirectiveLoadsAsInC() throws IOException {
        ParseResult<Brand> result = load("lead-tab.txt",
                "\tcode:FIRE_3\n\tname:fire\n\tverb:burn\n\tmultiplier:3\n\to-multiplier:25\n"
                        + "\tpower:113\n\tresist-flag:IM_FIRE\n");

        assertEquals(1, result.items().size());
        assertEquals("FIRE_3", result.items().get(0).getCode());
        assertEquals("fire", result.items().get(0).getName());
    }

    /**
     * C skips leading whitespace on a line and only then asks whether it starts with {@code #}, so
     * an indented comment is a comment. Before the line-start rule the lexer had nothing for it.
     *
     * <p>Function aLeadingSpaceCommentIsSkippedAsInC coded on 261109, commented in full on 261109.
     */
    @Test
    void aLeadingSpaceCommentIsSkippedAsInC() throws IOException {
        ParseResult<Brand> result = load("lead-comment.txt",
                "  # an indented comment\n" + validBrand("FIRE_3") + "    # and another, between records\n");

        assertEquals(1, result.items().size());
    }

    /**
     * C's reader returns an unterminated last line if it holds any character, and a {@code #} line
     * is a comment whether or not a newline follows it. {@code COMMENT} used to need its
     * {@code '\n'}, so the last line of the file was a lexer error.
     *
     * <p>Function aLastLineCommentWithNoNewlineLoadsAsInC coded on 261109, commented in full on 261109.
     */
    @Test
    void aLastLineCommentWithNoNewlineLoadsAsInC() throws IOException {
        ParseResult<Brand> result = load("last-comment.txt", validBrand("FIRE_3") + "# the last line, no newline");

        assertEquals(1, result.items().size());
    }

    /**
     * A tab before the newline is trailing whitespace to C, which has already turned it into
     * spaces. {@code multiplier:3<tab>} ends in a number, which is a position where a blank is
     * invisible. A line holding nothing but a tab is a blank line.
     *
     * <p>Function aTabBeforeTheNewlineLoadsAsInC coded on 261109, commented in full on 261109.
     */
    @Test
    void aTabBeforeTheNewlineLoadsAsInC() throws IOException {
        ParseResult<Brand> result = load("tab-eol.txt",
                "code:FIRE_3\nname:fire\nverb:burn\nmultiplier:3\t\n\t\no-multiplier:25 \t \npower:113\t\n"
                        + "resist-flag:IM_FIRE\n");

        assertEquals(1, result.items().size());
        assertEquals(3, result.items().get(0).getMultiplier());
    }

    /**
     * C ends a line at a bare {@code '\r'} as well as at {@code '\n'} (its reader skips the
     * {@code '\r'}, and if the next character is not {@code '\n'} it ends the line there), so
     * {@code power:113\rresist-flag:IM_FIRE} is two directives. The lexer used to see a
     * {@code '\r'} only directly before a {@code '\n'}.
     *
     * <p>Function aBareCarriageReturnBetweenTwoDirectivesIsALineEndAsInC coded on 261109, commented
     * in full on 261109.
     */
    @Test
    void aBareCarriageReturnBetweenTwoDirectivesIsALineEndAsInC() throws IOException {
        ParseResult<Brand> result = load("bare-cr.txt",
                "code:FIRE_3\nname:fire\nverb:burn\nmultiplier:3\no-multiplier:25\npower:113\r"
                        + "resist-flag:IM_FIRE\n");

        assertEquals(1, result.items().size());
        assertEquals(113, result.items().get(0).getPower());
    }

    /**
     * Blanks after the last flag of a {@code resist-flag:} or {@code vuln-flag:} line, a space or a
     * tab, are invisible: the flag still resolves and the record still loads. C does not do this
     * (it keeps the blank in the flag text and fails the lookup), so this is the accepted
     * leniency recorded for Stage 6 item 1, not C's behaviour.
     *
     * <p>Function aBlankOrTabAfterABrandFlagIsInvisibleHereWhereCKeepsItInTheName coded on 261109,
     * commented in full on 261109.
     */
    @Test
    void aBlankOrTabAfterABrandFlagIsInvisibleHereWhereCKeepsItInTheName() throws IOException {
        for (String blank : List.of(" ", "\t", " \t ")) {
            ParseResult<Brand> result = load("flag-blank.txt",
                    brand("FIRE_3", "fire", "burn", "3", "25", "113", "IM_FIRE" + blank, "HURT_FIRE" + blank));

            assertEquals(1, result.items().size(), "blank [" + blank.replace("\t", "\\t") + "]");
        }
    }

    /**
     * An interior tab stays a tab: C's reader would turn the one in {@code name:fire<tab>brand}
     * into spaces up to the next four-column stop, here it reaches the name unchanged. The
     * Stage 6 decision is that only tabs at the edges of a line are handled, so this is an
     * accepted deviation, pinned so that a later pre-lexer expansion shows up as a test to flip.
     *
     * <p>Function anInteriorTabIsKeptHereWhereCExpandsItToSpaces coded on 261109, commented in full
     * on 261109.
     */
    @Test
    void anInteriorTabIsKeptHereWhereCExpandsItToSpaces() throws IOException {
        ParseResult<Brand> result = load("interior-tab.txt",
                brand("FIRE_3", "fire\tbrand", "burn", "3", "25", "113", "IM_FIRE", ""));

        assertEquals("fire\tbrand", result.items().get(0).getName());
    }
}
