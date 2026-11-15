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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
        ParseResult<Summon> result = load("no-flag.txt", plainSummon("MONSTER"));

        assertEquals(1, result.items().size());
        assertEquals(MonsterRaceFlag.RF_NONE, result.items().get(0).getRaceFlag());
    }

    @Test
    void digitBearingRaceFlagResolves() throws IOException {
        // FLAG coverage including a digit-bearing name: DROP_4 -> RF_DROP_4.
        ParseResult<Summon> result =
                load("digit-flag.txt", summon("MONSTER", "SUM_MONSTER", "0",
                        "race-flag:DROP_4", "test"));

        assertEquals(MonsterRaceFlag.RF_DROP_4, result.items().get(0).getRaceFlag());
    }

    @Test
    void baseResolvesAgainstTheLoadedMonsterBases() throws IOException {
        ParseResult<Summon> result =
                load("base.txt", summon("DRAGON", "SUM_DRAGON", "1",
                        "base:" + KNOWN_BASE, "ancient dragons"));

        assertEquals(1, result.items().get(0).getBases().size());
        assertEquals(MessageType.MSG_SUM_DRAGON, result.items().get(0).getMessageType());
    }

    // ---- Assembler faults: the load stops, as C's first parser error ends the file ----

    @Test
    void invalidMessageTypeStopsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-msgt.txt", summon("MONSTER", "NOTREAL", "0", "", "test")));
        assertTrue(e.getMessage().contains("message type") && e.getMessage().contains("NOTREAL"), e::getMessage);
    }

    @Test
    void presentButInvalidRaceFlagStopsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-flag.txt", summon("MONSTER", "SUM_MONSTER", "0",
                        "race-flag:NOTAFLAG", "test")));
        assertTrue(e.getMessage().contains("race flag") && e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    @Test
    void unknownBaseStopsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-base.txt", summon("MONSTER", "SUM_MONSTER", "0",
                        "base:no such base", "test")));
        assertTrue(e.getMessage().contains("monster base") && e.getMessage().contains("no such base"),
                e::getMessage);
    }

    // ---- An unresolved fallback is no fault ------------------------------

    @Test
    void unresolvableFallbackIsSilentAndTheRecordStillLoads() throws IOException {
        // ACCEPTED DEVIATION (261107): C's summon_name_to_idx() runs past the last real summon into
        // the blank slot finish_parse_summon adds (name NULL) and fails in strcmp(name, NULL), so
        // C never reaches -1 for an unknown name. Here the record loads with a null fallback.
        ParseResult<Summon> result =
                load("bad-fallback.txt", summon("MONSTER", "SUM_MONSTER", "0",
                        "fallback:NOSUCH", "test"));

        assertEquals(1, result.items().size());
        assertNull(result.items().get(0).getFallback());
    }

    // ---- Fallback resolution ---------------------------------------------

    @Test
    void forwardFallbackReferenceResolvesToTheLaterRecord() throws IOException {
        // A names B as its fallback, but B is defined AFTER A. Because all records are built in pass
        // one before any linking happens, the forward reference still resolves to the B instance.
        String body = summon("A", "SUM_MONSTER", "0", "fallback:B", "first")
                + summon("B", "SUM_MONSTER", "0", "", "second");
        ParseResult<Summon> result = load("forward.txt", body);

        assertEquals(2, result.items().size());
        assertSame(byName(result.items(), "B"), byName(result.items(), "A").getFallback());
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        // A record must open with name:; starting with msgt: is a syntax error.
        String noName = "msgt:SUM_MONSTER\nuniques:0\ndesc:orphan\n";
        assertThrows(ParseCancellationException.class, () -> load("no-name.txt", noName));

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
                () -> load("two-errors.txt", body));
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

    /**
     * The summons come back in file order ({@code finish_parse_summon} fills the array downward from
     * the end of a head-pushed chain), but each summon's bases are held in the reverse of the file, as
     * the chain of {@code parse_summon_base} pushes leaves them. {@link #UPSTREAM} lists the bases as
     * the file writes them, so they are compared reversed.
     */
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
            assertEquals(expected.bases().reversed(),
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
        Files.writeString(file, body);

        assertThrows(DataFileParseException.class, () -> new SummonReader().parse(file.toString()));
    }

    @Test
    void parseThrowsOnASyntaxErrorRatherThanReturningAnEmptyList() throws IOException {
        Path file = tempDir.resolve("parse-hard.txt");
        Files.writeString(file, "msgt:SUM_MONSTER\nuniques:0\ndesc:orphan\n");

        assertThrows(ParseCancellationException.class, () -> new SummonReader().parse(file.toString()));
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
        ParseResult<Summon> result = load("order.txt", body);

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
        ParseResult<Summon> result = load("dup.txt", body);

        assertEquals(3, result.items().size());
        assertSame(result.items().get(0), result.items().get(2).getFallback());
    }

    @Test
    void aBadRecordStopsTheLoadBeforeTheLaterRecords() {
        // C stops at the bad msgt:, so the later record is never read.
        String body = summon("BAD", "NOTREAL", "0", "", "bad")
                + plainSummon("LATER");

        assertThrows(DataFileParseException.class, () -> load("stop.txt", body));
    }

    @Test
    void anUnresolvedFallbackKeepsTheRecordWhereCFailsInStrcmp() throws IOException {
        // ACCEPTED DEVIATION (261107): finish_parse_summon back-patches fallback =
        // summon_name_to_idx(name); for a name matching no summon that search reaches the blank
        // last slot (name NULL) and C fails in strcmp. Here the record is kept, nothing reported.
        ParseResult<Summon> result = load("fb.txt",
                summon("A", "SUM_MONSTER", "0", "fallback:NOSUCH", "a"));

        assertEquals(1, result.items().size());
        assertNull(result.items().get(0).getFallback());
        assertEquals("NOSUCH", result.items().get(0).getFallbackName());
    }

    @Test
    void multiWordBaseNameAndColonFreeDescriptionAreKeptWhole() throws IOException {
        // base is a sym in C (tokenized on ':' only), so a name with spaces stays in one piece.
        ParseResult<Summon> result = load("multiword.txt", summon("H", "SUM_MONSTER", "0", "base:zephyr hound\nbase:canine", "two words here"));

        Summon h = result.items().get(0);
        // Written zephyr hound then canine; the bases are held last-written first, as in C.
        assertEquals(List.of("canine", "zephyr hound"),
                h.getBases().stream().map(MonsterBase::getCodeName).toList());
        assertEquals("two words here", h.getDescription());
    }

    // ---- Where the Java grammar/assembler differ from C (pinned, see docs/) ---

    @Test
    void uniquesOtherThanOneIsTrueAsInCWhichTreatsAnyNonZeroAsTrue() throws IOException {
        // parse_summon_unique: unique = parser_getint(); if (unique) s->unique_allowed = true.
        // So C reads 2 and -1 as true.
        ParseResult<Summon> two = load("u2.txt", summon("A", "SUM_MONSTER", "2", "", "a"));
        ParseResult<Summon> minusOne = load("um1.txt", summon("A", "SUM_MONSTER", "-1", "", "a"));

        assertTrue(two.items().get(0).isUniquesAllowed());
        assertTrue(minusOne.items().get(0).isUniquesAllowed());
    }

    @Test
    void uniquesZeroAndOneAreReadAsC() throws IOException {
        ParseResult<Summon> result = load("u01.txt", summon("OFF", "SUM_MONSTER", "0", "", "off") + summon("ON", "SUM_MONSTER", "1", "", "on"));

        assertFalse(byName(result.items(), "OFF").isUniquesAllowed());
        assertTrue(byName(result.items(), "ON").isUniquesAllowed());
    }

    // ---- Directives in any order, repeated, or left out (Stage 7a) ----------------

    @Test
    void raceFlagBeforeBaseIsReadAsCDoesSinceEveryLineIsDispatchedOnItsOwn() throws IOException {
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\nrace-flag:UNIQUE\nbase:" + KNOWN_BASE + "\ndesc:a\n";

        Summon a = load("order-rb.txt", body).items().get(0);

        assertEquals(List.of(KNOWN_BASE), a.getBases().stream().map(MonsterBase::getCodeName).toList());
        assertEquals(MonsterRaceFlag.RF_UNIQUE, a.getRaceFlag());
    }

    @Test
    void baseLinesSplitByAnotherDirectiveAccumulateAsInC() throws IOException {
        // parse_summon_base pushes onto s->bases whenever it runs, so the second group is not lost.
        // Written ancient dragon then vampire, held last-written first.
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\nbase:" + KNOWN_BASE + "\nrace-flag:UNIQUE\nbase:vampire\ndesc:a\n";

        Summon a = load("split-base.txt", body).items().get(0);

        assertEquals(List.of("vampire", KNOWN_BASE), a.getBases().stream().map(MonsterBase::getCodeName).toList());
        assertEquals(MonsterRaceFlag.RF_UNIQUE, a.getRaceFlag());
    }

    @Test
    void threeSeparateBaseGroupsAreHeldLastWrittenFirst() throws IOException {
        String body = "name:A\nbase:canine\nmsgt:SUM_MONSTER\nbase:vampire\nuniques:0\nbase:" + KNOWN_BASE + "\ndesc:a\n";

        Summon a = load("three-base-groups.txt", body).items().get(0);

        assertEquals(List.of(KNOWN_BASE, "vampire", "canine"),
                a.getBases().stream().map(MonsterBase::getCodeName).toList());
    }

    @Test
    void repeatedRaceFlagKeepsTheLastAsInC() throws IOException {
        // parse_summon_race_flag overwrites s->race_flag.
        String body = summon("A", "SUM_MONSTER", "0", "race-flag:DEMON\nrace-flag:UNDEAD", "a");

        assertEquals(MonsterRaceFlag.RF_UNDEAD, load("two-flags.txt", body).items().get(0).getRaceFlag());
    }

    @Test
    void repeatedDescKeepsTheLastAsInC() throws IOException {
        // parse_summon_desc is string_make, an overwrite (unlike parse_eff_desc, which appends).
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\ndesc:first\ndesc:second\n";

        assertEquals("second", load("two-desc.txt", body).items().get(0).getDescription());
    }

    @Test
    void repeatedFallbackKeepsTheLastAsInC() throws IOException {
        // parse_summon_fallback is string_make, an overwrite.
        String body = summon("A", "SUM_MONSTER", "0", "fallback:A\nfallback:B", "a");

        assertEquals("B", load("two-fallbacks.txt", body).items().get(0).getFallbackName());
    }

    @Test
    void repeatedMsgtKeepsTheLastAsInC() throws IOException {
        String body = "name:A\nmsgt:SUM_MONSTER\nmsgt:SUM_UNDEAD\nuniques:0\ndesc:a\n";

        assertEquals(MessageType.MSG_SUM_UNDEAD, load("two-msgt.txt", body).items().get(0).getMessageType());
    }

    @Test
    void missingMsgtGivesTheGenericMessageAsInC() throws IOException {
        // msgt: is optional in C (mem_zalloc leaves message_type 0, which is MSG_GENERIC).
        String body = "name:A\nuniques:0\ndesc:a\n";

        assertEquals(MessageType.MSG_GENERIC, load("no-msgt.txt", body).items().get(0).getMessageType());
    }

    @Test
    void missingUniquesIsFalseAsInC() throws IOException {
        String body = "name:A\nmsgt:SUM_MONSTER\ndesc:a\n";

        assertFalse(load("no-uniques.txt", body).items().get(0).isUniquesAllowed());
    }

    @Test
    void missingDescGivesEmptyTextHereWhereCLeavesItNull() throws IOException {
        // C's desc stays NULL; here it is the empty string.
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\n";

        assertEquals("", load("no-desc.txt", body).items().get(0).getDescription());
    }

    @Test
    void aRecordWithOnlyANameLoadsWithCDefaults() throws IOException {
        Summon a = load("name-only.txt", "name:A\n").items().get(0);

        assertEquals("A", a.getName());
        assertEquals(MessageType.MSG_GENERIC, a.getMessageType());
        assertFalse(a.isUniquesAllowed());
        assertTrue(a.getBases().isEmpty());
        assertEquals(MonsterRaceFlag.RF_NONE, a.getRaceFlag());
        assertEquals("", a.getFallbackName());
        assertEquals("", a.getDescription());
    }

    @Test
    void aNameOnlyRecordIsFollowedByTheNextRecordAsInC() throws IOException {
        List<Summon> items = load("name-only-then-full.txt",
                "name:A\nname:B\nmsgt:SUM_MONSTER\ndesc:b\n").items();

        assertEquals(List.of("A", "B"), items.stream().map(Summon::getName).toList());
        assertEquals(MessageType.MSG_GENERIC, items.get(0).getMessageType());
        assertEquals(MessageType.MSG_SUM_MONSTER, items.get(1).getMessageType());
    }

    @Test
    void anyDirectiveMayFollowTheNameInAnyOrder() throws IOException {
        Summon a = load("desc-first.txt", "name:A\ndesc:a\nuniques:1\nmsgt:SUM_MONSTER\n").items().get(0);

        assertEquals("a", a.getDescription());
        assertTrue(a.isUniquesAllowed());
        assertEquals(MessageType.MSG_SUM_MONSTER, a.getMessageType());
    }

    @Test
    void uniquesLinesAccumulateAsInCWhereAnEarlierNonZeroIsNeverCleared() throws IOException {
        // parse_summon_unique only ever sets unique_allowed to true.
        String oneThenZero = "name:A\nmsgt:SUM_MONSTER\nuniques:1\nuniques:0\ndesc:a\n";
        String zeroThenOne = "name:B\nmsgt:SUM_MONSTER\nuniques:0\nuniques:1\ndesc:b\n";
        String zeroThenZero = "name:C\nmsgt:SUM_MONSTER\nuniques:0\nuniques:0\ndesc:c\n";

        List<Summon> items = load("uniques-lines.txt", oneThenZero + zeroThenOne + zeroThenZero).items();

        assertTrue(byName(items, "A").isUniquesAllowed());
        assertTrue(byName(items, "B").isUniquesAllowed());
        assertFalse(byName(items, "C").isUniquesAllowed());
    }

    @Test
    void everyUniquesLineIsCheckedEvenAfterOneThatIsTrue() {
        // C reads each line with parser_getint, so a bad figure after a non-zero one still counts.
        // A figure outside the int range is refused here (accepted deviation, 261027).
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:1\nuniques:99999999999\ndesc:a\n";

        assertThrows(DataFileParseException.class, () -> load("uniques-bad-after-true.txt", body));
    }

    @Test
    void anUnresolvableMsgtStopsTheLoadEvenWhenALaterOneIsValid() {
        // parse_summon_message_type refuses the bad line when it reads it; the later line never helps.
        String body = "name:A\nmsgt:NOPE\nmsgt:SUM_MONSTER\ndesc:a\n";

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-then-good-msgt.txt", body));

        assertTrue(e.getMessage().contains("message type value: NOPE"), e.getMessage());
    }

    @Test
    void anUnresolvableMsgtStopsTheLoadWhenItFollowsAValidOne() {
        String body = "name:A\nmsgt:SUM_MONSTER\nmsgt:NOPE\ndesc:a\n";

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("good-then-bad-msgt.txt", body));

        assertTrue(e.getMessage().contains("message type value: NOPE"), e.getMessage());
    }

    @Test
    void anUnresolvableRaceFlagStopsTheLoadEvenWhenALaterOneIsValid() {
        // parse_summon_race_flag returns PARSE_ERROR_INVALID_FLAG at the bad line.
        String body = summon("A", "SUM_MONSTER", "0", "race-flag:NOSUCH\nrace-flag:DEMON", "a");

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-then-good-flag.txt", body));

        assertTrue(e.getMessage().contains("race flag value: NOSUCH"), e.getMessage());
    }

    @Test
    void raceFlagNoneStopsTheLoadEvenWhenALaterOneIsValid() {
        // lookup_flag starts at FLAG_START, so NONE never matches, whatever follows it.
        String body = summon("A", "SUM_MONSTER", "0", "race-flag:NONE\nrace-flag:DEMON", "a");

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("none-then-good-flag.txt", body));

        assertTrue(e.getMessage().contains("sentinel monster race flag value: NONE"), e.getMessage());
    }

    @Test
    void anUnresolvableRaceFlagStopsTheLoadWhenItFollowsAValidOne() {
        String body = summon("A", "SUM_MONSTER", "0", "race-flag:DEMON\nrace-flag:NOSUCH", "a");

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("good-then-bad-flag.txt", body));

        assertTrue(e.getMessage().contains("race flag value: NOSUCH"), e.getMessage());
    }

    @Test
    void anUnresolvableBaseStopsTheLoadEvenWhenAnotherDirectiveSitsBetweenItAndAValidOne() {
        String body = "name:A\nbase:nosuchbase\nmsgt:SUM_MONSTER\nbase:vampire\ndesc:a\n";

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-base-first.txt", body));

        assertTrue(e.getMessage().contains("monster base value: nosuchbase"), e.getMessage());
    }

    @Test
    void aFileWithNoRecordsLoadsNothingAsInC() throws IOException {
        // init/run/finish_parse_summon accept an empty file. C's summon_max becomes 1 with one blank
        // slot; here the list is empty, with no blank slot (the accepted deviation on MonsterRegistry).
        assertTrue(load("empty.txt", "").items().isEmpty());
    }

    @Test
    void aFileOfOnlyCommentsAndBlankLinesLoadsNothingAsInC() throws IOException {
        assertTrue(load("comments-only.txt", "# a comment\n\n# another\n\n").items().isEmpty());
    }

    @Test
    void lowerCaseMessageTypeIsAHardErrorHereWhereCMatchesCaseInsensitively() throws IOException {
        // message_lookup_by_name compares with my_stricmp. The lexer's FLAG token is upper-case only.
        assertThrows(ParseCancellationException.class, () -> load("lower-msgt.txt",
                summon("A", "sum_monster", "0", "", "a")));

    }

    @Test
    void numericMessageTypeIsAHardErrorHereWhereCAcceptsAMessageNumber() throws IOException {
        // message_lookup_by_name reads digits as a message number first.
        assertThrows(ParseCancellationException.class, () -> load("num-msgt.txt",
                summon("A", "12", "0", "", "a")));

    }

    @Test
    void unknownDirectiveIsAHardErrorAsInC() throws IOException {
        // PARSE_ERROR_UNDEFINED_DIRECTIVE is fatal in C too.
        String body = "name:A\nmsgt:SUM_MONSTER\nuniques:0\nweight:5\ndesc:a\n";
        assertThrows(ParseCancellationException.class, () -> load("unknown.txt", body));

    }
}
