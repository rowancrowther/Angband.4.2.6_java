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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link SummonReader}: file text -> {@code SummonLexer}/
 * {@code SummonGrammar} -> {@code SummonParseRecord} -> {@code SummonAssembler} -> {@link Summon}.
 * Where {@code SummonGrammarTest} stops at the raw parse record, these pin the assembler half - the
 * optional-field resolution (an absent {@code race-flag} becomes {@link MonsterRaceFlag#RF_NONE}
 * rather than a dropped record), the message-type/base/flag lookups, the two-pass fallback linking,
 * and the two ways a load can fail: the assembler's faults, which stop the load with a
 * {@link DataFileParseException} as C's first parser error does, and the hard fail-closed channel
 * {@code GrammarDriver} threads through {@link ParseResult}.
 *
 * <p>The assembler resolves {@code base:} via {@link MonsterRegistry#getBaseFromName}, which reads the
 * static {@code monsterBases} list (and throws if it is {@code null}); loading those in turn needs
 * {@code monsterPains}. Rather than run the whole heavy {@code GameConstants.init()}, {@link #seed()}
 * loads {@code pain.txt} then {@code monster_base.txt} directly through their readers and injects the
 * results into the private static fields by reflection, restoring them afterwards so no global state
 * leaks to other suites.
 *
 * @author Rowan Crowther
 */
class SummonReaderTest {

    private static final String REAL_FILE = "lib/gamedata/summon.txt";
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
     * A complete summon record. {@code discriminator} is the optional base/race-flag/fallback
     * line(s) between {@code uniques:} and {@code desc:} - pass {@code ""} for a record that names
     * none of them. {@code uniques} is a string so a test could feed a raw value if needed.
     */
    private static String summon(String name, String msgt, String uniques,
                                 String discriminator, String desc) {
        return "name:" + name + "\n"
                + "msgt:" + msgt + "\n"
                + "uniques:" + uniques + "\n"
                + (discriminator.isEmpty() ? "" : discriminator + "\n")
                + "desc:" + desc + "\n";
    }

    /**
     * The common valid case: a plain monster summon with no base/flag/fallback restriction.
     */
    private static String plainSummon(String name) {
        return summon(name, "SUM_MONSTER", "1", "", "a test monster");
    }

    private ParseResult<Summon> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new SummonReader().parseWithResults(file.toString());
    }

    private static Summon byName(List<Summon> summons, String name) {
        return summons.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }

    // ---- Happy path ------------------------------------------------------

    @Test
    void realFileLoadsAllSeventeenSummonsWithNoErrors() throws IOException {
        ParseResult<Summon> result = new SummonReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(17, result.items().size());

        // ANY is the first record: no restriction, uniques allowed.
        Summon any = result.items().get(0);
        assertEquals("ANY", any.getName());
        assertTrue(any.isUniquesAllowed());
        assertEquals(MonsterRaceFlag.RF_NONE, any.getRaceFlag());
        assertNull(any.getFallback());

        // HOUND lists two bases (zephyr hound, canine) - proves multi-base accumulation.
        assertEquals(2, byName(result.items(), "HOUND").getBases().size());

        // Both WRAITH and UNIQUE fall back to HI_UNDEAD; the two-pass link must resolve to the
        // very same instance that lives in the list, not a stale copy.
        Summon hiUndead = byName(result.items(), "HI_UNDEAD");
        assertSame(hiUndead, byName(result.items(), "WRAITH").getFallback());
        assertSame(hiUndead, byName(result.items(), "UNIQUE").getFallback());
    }

    // ---- Optional-field resolution / regression --------------------------

    @Test
    void absentRaceFlagResolvesToRfNoneAndTheRecordStillLoads() throws IOException {
        // Regression guard: an omitted race-flag arrives as "" and must map to RF_NONE, NOT be
        // treated as an invalid flag that drops the record.
        ParseResult<Summon> result = load("no-flag.txt", withHeader(1, plainSummon("MONSTER")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(MonsterRaceFlag.RF_NONE, result.items().get(0).getRaceFlag());
    }

    @Test
    void digitBearingRaceFlagResolves() throws IOException {
        // FLAG coverage including a digit-bearing name: DROP_4 -> RF_DROP_4.
        ParseResult<Summon> result =
                load("digit-flag.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "race-flag:DROP_4", "test")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(MonsterRaceFlag.RF_DROP_4, result.items().get(0).getRaceFlag());
    }

    @Test
    void baseResolvesAgainstTheLoadedMonsterBases() throws IOException {
        ParseResult<Summon> result =
                load("base.txt", withHeader(1, summon("DRAGON", "SUM_DRAGON", "1",
                        "base:" + KNOWN_BASE, "ancient dragons")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().get(0).getBases().size());
        assertEquals(MessageType.MSG_SUM_DRAGON, result.items().get(0).getMessageType());
    }

    // ---- Assembler faults: the load stops, as C's first parser error ends the file ----

    @Test
    void invalidMessageTypeStopsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-msgt.txt", withHeader(1, summon("MONSTER", "NOTREAL", "0", "", "test"))));
        assertTrue(e.getMessage().contains("message type") && e.getMessage().contains("NOTREAL"), e::getMessage);
    }

    @Test
    void presentButInvalidRaceFlagStopsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-flag.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "race-flag:NOTAFLAG", "test"))));
        assertTrue(e.getMessage().contains("race flag") && e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    @Test
    void unknownBaseStopsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-base.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "base:no such base", "test"))));
        assertTrue(e.getMessage().contains("monster base") && e.getMessage().contains("no such base"),
                e::getMessage);
    }

    // ---- An unresolved fallback is no fault ------------------------------

    @Test
    void unresolvableFallbackIsSilentAndTheRecordStillLoads() throws IOException {
        // finish_parse_summon stores summon_name_to_idx() = -1 for an unknown name, with no error.
        ParseResult<Summon> result =
                load("bad-fallback.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "fallback:NOSUCH", "test")));

        assertEquals(1, result.items().size());
        assertNull(result.items().get(0).getFallback());
        assertFalse(result.hasErrors(), result.errors()::toString);
    }

    // ---- Fallback resolution ---------------------------------------------

    @Test
    void forwardFallbackReferenceResolvesToTheLaterRecord() throws IOException {
        // A names B as its fallback, but B is defined AFTER A. Because all records are built in pass
        // one before any linking happens, the forward reference still resolves to the B instance.
        String body = summon("A", "SUM_MONSTER", "0", "fallback:B", "first")
                + summon("B", "SUM_MONSTER", "0", "", "second");
        ParseResult<Summon> result = load("forward.txt", withHeader(2, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertSame(byName(result.items(), "B"), byName(result.items(), "A").getFallback());
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        ParseResult<Summon> result = load("bad-count.txt", withHeader(5, plainSummon("MONSTER")));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        // The file rule requires the record-count header; without it the parse fails and the hard
        // channel yields an empty result carrying the error.
        ParseResult<Summon> result = load("no-header.txt", plainSummon("MONSTER"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        // A record must open with name:; starting with msgt: is a syntax error.
        String noName = "msgt:SUM_MONSTER\nuniques:0\ndesc:orphan\n";
        ParseResult<Summon> result = load("no-name.txt", withHeader(1, noName));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Two faults at once ----------------------------------------------

    @Test
    void theFirstOfTwoFaultsIsTheOneReportedAndNothingIsHandedBack() {
        // C's first parser error ends the file: the clean record before the fault is not handed
        // back, and the second fault is never reached.
        String body = plainSummon("GOOD")
                + summon("BADMSG", "NOTREAL", "0", "", "bad msgt")
                + summon("BADFLAG", "SUM_MONSTER", "0", "race-flag:NOTAFLAG", "bad flag");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("two-errors.txt", withHeader(3, body)));
        assertTrue(e.getMessage().contains("NOTREAL"), e::getMessage);
    }

    // ---- Order, shape and reader contract, against mon-summon.c ------------

    /**
     * Name, message type, uniques flag, base names (in file order), race flag and fallback name for
     * each record of the upstream {@code summon.txt}, in file order. Copied from the C data file, not
     * from the Java loader. C's {@code finish_parse_summon} fills {@code summons[]} from the highest
     * index down while walking a head-first chain, so the array is in file order.
     */
    private record Upstream(String name, MessageType msgt, boolean uniques, List<String> bases,
                            MonsterRaceFlag raceFlag, String fallback) {
    }

    private static final List<Upstream> UPSTREAM = List.of(
            new Upstream("ANY", MessageType.MSG_SUM_MONSTER, true, List.of(), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("KIN", MessageType.MSG_SUM_MONSTER, false, List.of(), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("MONSTER", MessageType.MSG_SUM_MONSTER, false, List.of(), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("MONSTERS", MessageType.MSG_SUM_MONSTER, false, List.of(), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("ANIMAL", MessageType.MSG_SUM_ANIMAL, false, List.of(), MonsterRaceFlag.RF_ANIMAL, ""),
            new Upstream("SPIDER", MessageType.MSG_SUM_SPIDER, false, List.of("spider"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("HOUND", MessageType.MSG_SUM_HOUND, false, List.of("zephyr hound", "canine"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("HYDRA", MessageType.MSG_SUM_HYDRA, false, List.of("hydra"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("AINU", MessageType.MSG_SUM_AINU, false, List.of("ainu"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("DEMON", MessageType.MSG_SUM_DEMON, false, List.of(), MonsterRaceFlag.RF_DEMON, ""),
            new Upstream("UNDEAD", MessageType.MSG_SUM_UNDEAD, false, List.of(), MonsterRaceFlag.RF_UNDEAD, ""),
            new Upstream("DRAGON", MessageType.MSG_SUM_DRAGON, false, List.of(), MonsterRaceFlag.RF_DRAGON, ""),
            new Upstream("HI_DEMON", MessageType.MSG_SUM_HI_DEMON, true, List.of("major demon"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("HI_UNDEAD", MessageType.MSG_SUM_HI_UNDEAD, true, List.of("vampire", "wraith", "lich"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("HI_DRAGON", MessageType.MSG_SUM_HI_DRAGON, true, List.of("ancient dragon"), MonsterRaceFlag.RF_NONE, ""),
            new Upstream("WRAITH", MessageType.MSG_SUM_WRAITH, true, List.of("wraith"), MonsterRaceFlag.RF_UNIQUE, "HI_UNDEAD"),
            new Upstream("UNIQUE", MessageType.MSG_SUM_UNIQUE, true, List.of(), MonsterRaceFlag.RF_UNIQUE, "HI_UNDEAD"));

    @Test
    void realFileComesBackInFileOrderWithEveryFieldAsUpstream() throws IOException {
        List<Summon> loaded = new SummonReader().parseWithResults(REAL_FILE).items();

        assertEquals(UPSTREAM.size(), loaded.size());
        for (int i = 0; i < UPSTREAM.size(); i++) {
            Upstream expected = UPSTREAM.get(i);
            Summon actual = loaded.get(i);
            String where = "record " + i + " (" + expected.name() + ")";
            assertEquals(expected.name(), actual.getName(), where);
            assertEquals(expected.msgt(), actual.getMessageType(), where);
            assertEquals(expected.uniques(), actual.isUniquesAllowed(), where);
            assertEquals(expected.bases(),
                    actual.getBases().stream().map(MonsterBase::getCodeName).toList(), where);
            assertEquals(expected.raceFlag(), actual.getRaceFlag(), where);
            assertEquals(expected.fallback(), actual.getFallbackName(), where);
        }
    }

    @Test
    void parseReturnsTheSameSummonsAsParseWithResultsForTheRealFile() throws IOException {
        List<Summon> viaParse = new SummonReader().parse(REAL_FILE);
        List<Summon> viaResults = new SummonReader().parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.stream().map(Summon::getName).toList(),
                viaParse.stream().map(Summon::getName).toList());
    }

    @Test
    void parseThrowsOnAnAssemblerFaultToo() throws IOException {
        String body = plainSummon("GOOD") + summon("BAD", "NOTREAL", "0", "", "bad msgt");
        Path file = tempDir.resolve("parse-fault.txt");
        Files.writeString(file, withHeader(2, body));

        assertThrows(DataFileParseException.class, () -> new SummonReader().parse(file.toString()));
    }

    @Test
    void parseReturnsAnEmptyListOnASyntaxError() throws IOException {
        Path file = tempDir.resolve("parse-hard.txt");
        Files.writeString(file, withHeader(1, "msgt:SUM_MONSTER\nuniques:0\ndesc:orphan\n"));

        assertTrue(new SummonReader().parse(file.toString()).isEmpty());
    }

    @Test
    void unreadableFileIsAnIoExceptionWhereCQuits() {
        // run_parse_summon calls parse_file_quit_not_found, which quit()s. Here the caller decides.
        Path missing = tempDir.resolve("no-such-file.txt");

        assertThrows(IOException.class, () -> new SummonReader().parseWithResults(missing.toString()));
        assertThrows(IOException.class, () -> new SummonReader().parse(missing.toString()));
    }

    @Test
    void recordsComeBackInFileOrderWhateverTheirNames() throws IOException {
        // C's array is in file order (unlike blow_methods), so a later name that sorts earlier
        // must not move.
        String body = plainSummon("ZETA") + plainSummon("ALPHA") + plainSummon("MID");
        ParseResult<Summon> result = load("order.txt", withHeader(3, body));

        assertEquals(List.of("ZETA", "ALPHA", "MID"),
                result.items().stream().map(Summon::getName).toList());
    }

    @Test
    void duplicateNamesAreBothKeptAndAFallbackFindsTheFirst() throws IOException {
        // summon_name_to_idx returns the first match, so a fallback naming a duplicated summon
        // resolves to the earlier record.
        String body = summon("TWIN", "SUM_MONSTER", "0", "", "first twin")
                + summon("TWIN", "SUM_MONSTER", "0", "", "second twin")
                + summon("USER", "SUM_MONSTER", "0", "fallback:TWIN", "user");
        ParseResult<Summon> result = load("dup.txt", withHeader(3, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(3, result.items().size());
        assertSame(result.items().get(0), result.items().get(2).getFallback());
    }

    @Test
    void aBadRecordStopsTheLoadBeforeTheLaterRecords() {
        // C stops at the bad msgt:, so the later record is never read.
        String body = summon("BAD", "NOTREAL", "0", "", "bad")
                + plainSummon("LATER");

        assertThrows(DataFileParseException.class, () -> load("stop.txt", withHeader(2, body)));
    }

    @Test
    void anUnresolvedFallbackKeepsTheRecordWhereCStoresMinusOneSilently() throws IOException {
        // finish_parse_summon back-patches fallback = summon_name_to_idx(name), -1 for no match,
        // with no error. Here the record is kept and nothing is reported.
        ParseResult<Summon> result = load("fb.txt",
                withHeader(1, summon("A", "SUM_MONSTER", "0", "fallback:NOSUCH", "a")));

        assertEquals(1, result.items().size());
        assertNull(result.items().get(0).getFallback());
        assertEquals("NOSUCH", result.items().get(0).getFallbackName());
        assertFalse(result.hasErrors());
    }

    @Test
    void multiWordBaseNameAndColonFreeDescriptionAreKeptWhole() throws IOException {
        // base is a sym in C (tokenized on ':' only), so a name with spaces stays in one piece.
        ParseResult<Summon> result = load("multiword.txt", withHeader(1,
                summon("H", "SUM_MONSTER", "0", "base:zephyr hound\nbase:canine", "two words here")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Summon h = result.items().get(0);
        assertEquals(List.of("zephyr hound", "canine"),
                h.getBases().stream().map(MonsterBase::getCodeName).toList());
        assertEquals("two words here", h.getDescription());
    }

    // ---- Where the Java grammar/assembler differ from C (pinned, see docs/) ---

    @Test
    void uniquesOtherThanOneIsTrueAsInCWhichTreatsAnyNonZeroAsTrue() throws IOException {
        // parse_summon_unique: unique = parser_getint(); if (unique) s->unique_allowed = true.
        // So C reads 2 and -1 as true.
        ParseResult<Summon> two = load("u2.txt", withHeader(1, summon("A", "SUM_MONSTER", "2", "", "a")));
        ParseResult<Summon> minusOne = load("um1.txt", withHeader(1, summon("A", "SUM_MONSTER", "-1", "", "a")));

        assertTrue(two.items().get(0).isUniquesAllowed());
        assertTrue(minusOne.items().get(0).isUniquesAllowed());
    }

    @Test
    void uniquesZeroAndOneAreReadAsC() throws IOException {
        ParseResult<Summon> result = load("u01.txt", withHeader(2,
                summon("OFF", "SUM_MONSTER", "0", "", "off") + summon("ON", "SUM_MONSTER", "1", "", "on")));

        assertFalse(byName(result.items(), "OFF").isUniquesAllowed());
        assertTrue(byName(result.items(), "ON").isUniquesAllowed());
    }

    @Test
    void raceFlagBeforeBaseIsAHardErrorHereWhereCTakesAnyOrder() throws IOException {
        // C dispatches each line on its own, so race-flag: may precede base:.
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\nrace-flag:UNIQUE\nbase:" + KNOWN_BASE + "\ndesc:a\n";
        ParseResult<Summon> result = load("order-rb.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void baseLinesSplitByAnotherDirectiveAreAHardErrorHereWhereCAccumulatesThem() throws IOException {
        // parse_summon_base pushes onto s->bases whenever it runs; here base: lines must be adjacent.
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\nbase:" + KNOWN_BASE + "\nrace-flag:UNIQUE\nbase:vampire\ndesc:a\n";
        ParseResult<Summon> result = load("split-base.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void repeatedRaceFlagIsAHardErrorHereWhereCKeepsTheLast() throws IOException {
        // parse_summon_race_flag overwrites s->race_flag.
        String body = summon("A", "SUM_MONSTER", "0", "race-flag:DEMON\nrace-flag:UNDEAD", "a");
        ParseResult<Summon> result = load("two-flags.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void repeatedDescIsAHardErrorHereWhereCKeepsTheLast() throws IOException {
        // parse_summon_desc is string_make, an overwrite (unlike parse_eff_desc, which appends).
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\ndesc:first\ndesc:second\n";
        ParseResult<Summon> result = load("two-desc.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingMsgtIsAHardErrorHereWhereCLeavesTheGenericMessage() throws IOException {
        // msgt: is optional in C (mem_zalloc leaves message_type 0).
        String body = "name:A\nuniques:0\ndesc:a\n";
        ParseResult<Summon> result = load("no-msgt.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingDescIsAHardErrorHereWhereCLeavesItNull() throws IOException {
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\n";
        ParseResult<Summon> result = load("no-desc.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing() throws IOException {
        // init/run/finish_parse_summon accept an empty file (summon_max becomes 1, one blank slot).
        ParseResult<Summon> result = load("empty.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void lowerCaseMessageTypeIsAHardErrorHereWhereCMatchesCaseInsensitively() throws IOException {
        // message_lookup_by_name compares with my_stricmp. The lexer's FLAG token is upper-case only.
        ParseResult<Summon> result = load("lower-msgt.txt",
                withHeader(1, summon("A", "sum_monster", "0", "", "a")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void numericMessageTypeIsAHardErrorHereWhereCAcceptsAMessageNumber() throws IOException {
        // message_lookup_by_name reads digits as a message number first.
        ParseResult<Summon> result = load("num-msgt.txt",
                withHeader(1, summon("A", "12", "0", "", "a")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void unknownDirectiveIsAHardErrorAsInC() throws IOException {
        // PARSE_ERROR_UNDEFINED_DIRECTIVE is fatal in C too.
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\nweight:5\ndesc:a\n";
        ParseResult<Summon> result = load("unknown.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
