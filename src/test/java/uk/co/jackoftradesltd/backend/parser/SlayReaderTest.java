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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.objects.Slay;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link SlayReader}: file text -> {@code SlayLexer}/
 * {@code SlayGrammar} -> {@code SlayParseRecord} -> {@code SlayAssembler} -> {@link Slay}. Where
 * {@code SlayGrammarTest} stops at the raw parse record, these pin the assembler half - the
 * race-flag XOR base branch (resolve exactly the one that is present), the numeric-field parsing,
 * and the skip-and-continue soft-error channel that {@code GrammarDriver} threads through
 * {@link ParseResult}, plus the fail-closed hard-error channel.
 *
 * <p>The assembler resolves {@code base:} via {@link MonsterRegistry#getBaseFromName}, which reads the
 * static {@code monsterBases} list (and throws if it is {@code null}); loading those in turn needs
 * {@code monsterPains}. Rather than run the whole heavy {@code GameConstants.init()}, {@link #seed()}
 * loads {@code pain.txt} then {@code monster_base.txt} directly through their readers and injects the
 * results into the private static fields by reflection, restoring them afterwards so no global state
 * leaks to other suites.
 *
 * <p>The reader's own contract is pinned against values worked out from the upstream
 * {@code slay.txt} and {@code obj-init.c}: every field of all eleven records, C's last-first list
 * order, directive order and repeats, and the missing-file {@link IOException}. A final group pins
 * the places the grammar and assembler differ from C, each named for the C behaviour it departs
 * from, so that an assembler or grammar fix shows up here as a test to flip.
 *
 * <p>Class SlayReaderTest coded before 261017, extended and commented in full on 261017.
 *
 * @author Rowan Crowther
 */
class SlayReaderTest {

    private static final String REAL_FILE = "lib/gamedata/slay.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";

    /**
     * A monster base known to exist in the shipped {@code monster_base.txt}.
     */
    private static final String KNOWN_BASE = "ancient dragon";

    private static Object savedPains;
    private static Object savedBases;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seed() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        savedPains = setStatic("monsterPains", pains);

        List<MonsterBase> bases = new MonsterBaseReader().parseWithResults(BASE_FILE).items();
        savedBases = setStatic("monsterBases", bases);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A complete slay record. {@code discriminator} is the race-flag/base line(s) between name and
     * multiplier - pass {@code ""} for a record that names neither. {@code mult}/{@code oMult}/
     * {@code power} are strings so a test can feed an out-of-range value.
     */
    private static String slay(String code, String discriminator,
                               String mult, String oMult, String power) {
        return "code:" + code + "\n"
                + "name:test creatures\n"
                + (discriminator.isEmpty() ? "" : discriminator + "\n")
                + "multiplier:" + mult + "\n"
                + "o-multiplier:" + oMult + "\n"
                + "power:" + power + "\n"
                + "melee-verb:smite\n"
                + "range-verb:pierces\n";
    }

    /**
     * The common valid case: a race-flag slay with sensible numeric fields.
     */
    private static String raceFlagSlay(String code, String flag) {
        return slay(code, "race-flag:" + flag, "2", "18", "100");
    }

    private ParseResult<Slay> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new SlayReader().parseWithResults(file.toString());
    }

    // ---- Happy path ------------------------------------------------------

    @Test
    void realFileLoadsAllElevenSlaysWithNoErrors() throws IOException {
        ParseResult<Slay> result = new SlayReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(11, result.items().size());
        // C's slays[] runs in reverse file order, so the last record, UNDEAD_5, is first
        assertEquals("UNDEAD_5", result.items().get(0).getCode());
    }

    @Test
    void syntheticRaceFlagSlayAssembles() throws IOException {
        ParseResult<Slay> result = load("ok-flag.txt", withHeader(1, raceFlagSlay("EVIL_2", "EVIL")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("EVIL_2", result.items().get(0).getCode());
    }

    @Test
    void syntheticBaseSlayResolvesAgainstTheLoadedMonsterBases() throws IOException {
        // A base: record never appears in the shipped slay.txt, so this is the only coverage of the
        // base branch - it also proves getMonsterBase found a match (a miss would skip the record).
        ParseResult<Slay> result =
                load("ok-base.txt", withHeader(1, slay("DRAGON_5", "base:" + KNOWN_BASE, "5", "35", "110")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertFalse(result.items().get(0).toString().contains("base=null"),
                () -> "expected a resolved base, got: " + result.items().get(0));
    }

    // ---- Assembler soft-error branches (skip-and-continue) ---------------

    @Test
    void bothRaceFlagAndBaseIsReportedAndSkipped() throws IOException {
        ParseResult<Slay> result = load("both.txt",
                withHeader(1, slay("EVIL_2", "race-flag:EVIL\nbase:" + KNOWN_BASE, "2", "18", "100")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("both")),
                result.errors()::toString);
    }

    @Test
    void unknownRaceFlagIsReportedAndSkipped() throws IOException {
        ParseResult<Slay> result =
                load("bad-flag.txt", withHeader(1, raceFlagSlay("EVIL_2", "NOTAFLAG")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("unknown monster race flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
    }

    @Test
    void unknownBaseIsReportedAndSkipped() throws IOException {
        ParseResult<Slay> result = load("bad-base.txt",
                withHeader(1, slay("EVIL_2", "base:no such base", "2", "18", "100")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown monster base")),
                result.errors()::toString);
    }

    @Test
    void neitherRaceFlagNorBaseIsReportedAndSkipped() throws IOException {
        ParseResult<Slay> result =
                load("neither.txt", withHeader(1, slay("EVIL_2", "", "2", "18", "100")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("neither")),
                result.errors()::toString);
    }

    @Test
    void overflowingMultiplierIsReportedAndSkipped() throws IOException {
        // multiplier: is an INTEGER token, so the only route into the NumberFormatException branch is
        // a value too large for int.
        ParseResult<Slay> result = load("bad-mult.txt",
                withHeader(1, raceFlagSlayWith("EVIL_2", "EVIL", "99999999999999", "18", "100")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("multiplier")),
                result.errors()::toString);
    }

    @Test
    void overflowingOMultiplierIsReportedAndSkipped() throws IOException {
        ParseResult<Slay> result = load("bad-omult.txt",
                withHeader(1, raceFlagSlayWith("EVIL_2", "EVIL", "2", "99999999999999", "100")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("oMult")),
                result.errors()::toString);
    }

    @Test
    void overflowingPowerIsReportedAndSkipped() throws IOException {
        ParseResult<Slay> result = load("bad-power.txt",
                withHeader(1, raceFlagSlayWith("EVIL_2", "EVIL", "2", "18", "99999999999999")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("power")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        ParseResult<Slay> result =
                load("bad-count.txt", withHeader(5, raceFlagSlay("EVIL_2", "EVIL")));

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
        ParseResult<Slay> result = load("no-header.txt", raceFlagSlay("EVIL_2", "EVIL"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingCodeHeaderIsAHardError() throws IOException {
        // A record must open with code:; starting with name: is a syntax error.
        String noCode = "name:orphan\nrace-flag:EVIL\nmultiplier:2\no-multiplier:18\n"
                + "power:100\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("no-code.txt", withHeader(1, noCode));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Two soft errors at once -----------------------------------------

    @Test
    void twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads() throws IOException {
        // Three records, declared count 3 (so no count error): one clean, one with an unknown flag,
        // one naming both a flag and a base. The two bad ones are skipped independently, both errors
        // are recorded, and the clean record still loads - proving the errors accumulate rather than
        // the first one aborting the file.
        String body = raceFlagSlay("EVIL_2", "EVIL")
                + raceFlagSlay("BAD_2", "NOTAFLAG")
                + slay("BOTH_2", "race-flag:EVIL\nbase:" + KNOWN_BASE, "2", "18", "100");
        ParseResult<Slay> result = load("two-errors.txt", withHeader(3, body));

        assertEquals(1, result.items().size());
        assertEquals("EVIL_2", result.items().get(0).getCode());

        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("unknown monster race flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("both")),
                result.errors()::toString);
    }

    // ---- Contract of the reader itself (expected values from C's slay.txt and obj-init.c) ----

    /**
     * The text {@link Slay#toString()} gives for a race-flag slay, so a whole record can be checked in
     * one assertion against the values in the upstream {@code slay.txt}.
     */
    private static String expected(String code, String name, String flag, int mult, int oMult,
                                   int power, String melee, String range) {
        return "Slay{code='" + code + "', name='" + name + "', base=null, meleeVerb='" + melee
                + "', rangedVerb='" + range + "', raceFlag=RF_" + flag + ", multiplier=" + mult
                + ", oMultiplier=" + oMult + ", power=" + power + "}";
    }

    /**
     * The eleven records of the upstream {@code lib/gamedata/slay.txt}, in the order the file lists
     * them, worked out from that file and not from the Java output.
     */
    private static List<String> upstreamInFileOrder() {
        String smite = "smite";
        String pierces = "pierces";
        String fierce = "fiercely smite";
        String deep = "deeply pierces";
        return List.of(
                expected("EVIL_2", "evil creatures", "EVIL", 2, 18, 200, smite, pierces),
                expected("ANIMAL_2", "animals", "ANIMAL", 2, 20, 115, smite, pierces),
                expected("ORC_3", "orcs", "ORC", 3, 25, 101, smite, pierces),
                expected("TROLL_3", "trolls", "TROLL", 3, 25, 101, smite, pierces),
                expected("GIANT_3", "giants", "GIANT", 3, 25, 102, smite, pierces),
                expected("DEMON_3", "demons", "DEMON", 3, 25, 110, smite, pierces),
                expected("DRAGON_3", "dragons", "DRAGON", 3, 25, 105, smite, pierces),
                expected("UNDEAD_3", "undead", "UNDEAD", 3, 25, 115, smite, pierces),
                expected("DEMON_5", "demons", "DEMON", 5, 35, 120, fierce, deep),
                expected("DRAGON_5", "dragons", "DRAGON", 5, 35, 110, fierce, deep),
                expected("UNDEAD_5", "undead", "UNDEAD", 5, 35, 130, fierce, deep));
    }

    @Test
    void everyRecordOfTheRealFileMatchesTheUpstreamTextAndComesBackInCsOrder() throws IOException {
        // finish_parse_slay copies the head-first chain into slays[1..], so the last record in the
        // file is slot 1 and the first record is last: the list is the file order reversed.
        List<String> reversed = new java.util.ArrayList<>(upstreamInFileOrder());
        java.util.Collections.reverse(reversed);

        List<String> actual = new SlayReader().parseWithResults(REAL_FILE).items().stream()
                .map(Slay::toString).toList();

        assertEquals(reversed, actual);
    }

    @Test
    void realFileCodesRunFromUndead5BackToEvil2() throws IOException {
        List<String> codes = new SlayReader().parseWithResults(REAL_FILE).items().stream()
                .map(Slay::getCode).toList();

        assertEquals(List.of("UNDEAD_5", "DRAGON_5", "DEMON_5", "UNDEAD_3", "DRAGON_3", "DEMON_3",
                "GIANT_3", "TROLL_3", "ORC_3", "ANIMAL_2", "EVIL_2"), codes);
    }

    @Test
    void parseReturnsTheSameSlaysAsParseWithResults() throws IOException {
        List<Slay> viaParse = new SlayReader().parse(REAL_FILE);
        List<Slay> viaResults = new SlayReader().parseWithResults(REAL_FILE).items();

        assertEquals(viaResults, viaParse);
        assertEquals(11, viaParse.size());
    }

    @Test
    void parseDropsErrorsSoASyntaxErrorLooksLikeAnEmptyList() throws IOException {
        Path file = tempDir.resolve("bad-syntax.txt");
        Files.writeString(file, withHeader(1, "name:orphan\n"));

        assertTrue(new SlayReader().parse(file.toString()).isEmpty());
    }

    @Test
    void missingFileIsAnIOExceptionNotAnEmptyResult() {
        // run_parse_slay is parse_file_quit_not_found: a missing file is fatal in C, so it must not
        // come back as "no slays".
        String missing = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new SlayReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new SlayReader().parse(missing));
    }

    @Test
    void directivesMayComeInAnyOrderAfterTheCodeLine() throws IOException {
        // C dispatches on the keyword, so only code: has to come first.
        String body = "code:ORC_3\nrange-verb:pierces\npower:101\nmelee-verb:smite\n"
                + "o-multiplier:25\nrace-flag:ORC\nmultiplier:3\nname:orcs\n";
        ParseResult<Slay> result = load("any-order.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of(expected("ORC_3", "orcs", "ORC", 3, 25, 101, "smite", "pierces")),
                result.items().stream().map(Slay::toString).toList());
    }

    @Test
    void repeatedDirectiveKeepsTheLastValueAsCOverwrites() throws IOException {
        // parse_slay_name, _multiplier and _melee_verb free or overwrite the earlier value.
        String body = "code:ORC_3\nname:first\nname:orcs\nrace-flag:ORC\nmultiplier:9\nmultiplier:3\n"
                + "o-multiplier:25\npower:101\nmelee-verb:hit\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("repeat.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(expected("ORC_3", "orcs", "ORC", 3, 25, 101, "smite", "pierces"),
                result.items().get(0).toString());
    }

    @Test
    void duplicateCodesAreBothKeptLastFirst() throws IOException {
        // C does not check codes for uniqueness: both records go onto the chain.
        String body = raceFlagSlay("EVIL_2", "EVIL")
                + slay("EVIL_2", "race-flag:ORC", "3", "25", "101");
        ParseResult<Slay> result = load("dup.txt", withHeader(2, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(3, result.items().get(0).getMultiplier());
        assertEquals(2, result.items().get(1).getMultiplier());
    }

    @Test
    void textValuesRunToTheEndOfTheLineColonsAndSpacesIncluded() throws IOException {
        // parser_getstr takes the rest of the line, so a colon in a name or verb is data.
        String body = "code:EVIL_2\nname:evil: creatures of note\nrace-flag:EVIL\nmultiplier:2\n"
                + "o-multiplier:18\npower:200\nmelee-verb:smite: hard\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("colons.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("evil: creatures of note", result.items().get(0).getName());
        assertTrue(result.items().get(0).toString().contains("meleeVerb='smite: hard'"));
    }

    @Test
    void commentsAndBlankLinesBetweenDirectivesAreSkipped() throws IOException {
        String body = "# a comment\n\ncode:EVIL_2\n# between\nname:evil creatures\n\nrace-flag:EVIL\n"
                + "multiplier:2\no-multiplier:18\npower:200\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("comments.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    // ---- Known differences from C (see docs/grammar_issues.md and docs/Assembler_Issues.md) ----

    @Test
    void secondRaceFlagInOneRecordKeepsTheLastHereWhereCRejects() throws IOException {
        // C: parse_slay_race_flag returns PARSE_ERROR_INVALID_SLAY when race_flag is already set
        // ("not both nor multiple race flags") and the load stops. Here the grammar keeps the last.
        String body = "code:EVIL_2\nname:evil creatures\nrace-flag:ORC\nrace-flag:EVIL\nmultiplier:2\n"
                + "o-multiplier:18\npower:200\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("two-flags.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).toString().contains("raceFlag=RF_EVIL"));
    }

    @Test
    void secondBaseInOneRecordKeepsTheLastHereWhereCRejects() throws IOException {
        String body = "code:DRAGON_5\nname:dragons\nbase:" + KNOWN_BASE + "\nbase:no such base\n"
                + "multiplier:5\no-multiplier:35\npower:110\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("two-bases.txt", withHeader(1, body));

        // The last base: wins, and it is the unknown one, so the record is skipped for that reason
        // and not because there were two of them.
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown monster base")),
                result.errors()::toString);
    }

    @Test
    void missingPowerSkipsTheRecordHereWhereCLeavesItZero() throws IOException {
        // C allocates the slay zeroed, so a record with no power: loads with power 0.
        String body = "code:EVIL_2\nname:evil creatures\nrace-flag:EVIL\nmultiplier:2\n"
                + "o-multiplier:18\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("no-power.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("power")),
                result.errors()::toString);
    }

    @Test
    void missingMultiplierSkipsTheRecordHereWhereCLeavesItZero() throws IOException {
        String body = "code:EVIL_2\nname:evil creatures\nrace-flag:EVIL\n"
                + "o-multiplier:18\npower:200\nmelee-verb:smite\nrange-verb:pierces\n";
        ParseResult<Slay> result = load("no-mult.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("multiplier")),
                result.errors()::toString);
    }

    @Test
    void negativeMultiplierLoadsHereWhereCParserGetUintRejectsIt() throws IOException {
        // C reads the three numbers with parser_getuint, which refuses a leading minus sign.
        ParseResult<Slay> result = load("negative.txt",
                withHeader(1, raceFlagSlayWith("EVIL_2", "EVIL", "-2", "18", "200")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-2, result.items().get(0).getMultiplier());
    }

    @Test
    void raceFlagNoneLoadsHereWhereCLookupFlagSkipsIt() throws IOException {
        // lookup_flag starts at FLAG_START (1), so "NONE" at index 0 is never found and C returns
        // PARSE_ERROR_INVALID_FLAG. RF_NONE is a real constant here, so the record is accepted.
        ParseResult<Slay> result = load("none-flag.txt",
                withHeader(1, slay("EVIL_2", "race-flag:NONE", "2", "18", "200")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).toString().contains("raceFlag=RF_NONE"));
    }

    @Test
    void codeThatIsNotAFlagUnderscoreNumberLoadsAsInC() throws IOException {
        // C never takes the code apart, so BANE, EVIL_X and a bare EVIL are all records. The Slay
        // constructor stores the text as given and the load neither throws nor skips the record.
        for (String code : new String[]{"BANE", "EVIL", "EVIL_X", "NOSUCHFLAG_2"}) {
            ParseResult<Slay> result = load("odd-code.txt", withHeader(1, raceFlagSlay(code, "EVIL")));

            assertFalse(result.hasErrors(), () -> code + ": " + result.errors());
            assertEquals(1, result.items().size(), code);
            assertEquals(code, result.items().get(0).getCode());
            assertTrue(result.items().get(0).toString().contains("raceFlag=RF_EVIL"), code);
        }
    }

    @Test
    void headerOnlyFileIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        // The file rule needs at least one slay record after record-count:.
        ParseResult<Slay> result = load("empty.txt", withHeader(0, ""));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * {@link #slay} but with an explicit race flag, for the numeric-overflow cases.
     */
    private static String raceFlagSlayWith(String code, String flag,
                                           String mult, String oMult, String power) {
        return slay(code, "race-flag:" + flag, mult, oMult, power);
    }
}
