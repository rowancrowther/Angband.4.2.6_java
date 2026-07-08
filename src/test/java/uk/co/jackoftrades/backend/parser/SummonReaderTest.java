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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

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
 * and both error channels {@code GrammarDriver} threads through {@link ParseResult}: the soft
 * skip-and-continue {@code errors} list and the hard fail-closed channel.
 *
 * <p>The assembler resolves {@code base:} via {@link GameConstants#getMonsterBase}, which reads the
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
        Field f = GameConstants.class.getDeclaredField(field);
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

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void invalidMessageTypeIsReportedAndSkipped() throws IOException {
        ParseResult<Summon> result =
                load("bad-msgt.txt", withHeader(1, summon("MONSTER", "NOTREAL", "0", "", "test")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid message type") && e.contains("NOTREAL")),
                result.errors()::toString);
    }

    @Test
    void presentButInvalidRaceFlagIsReportedAndSkipped() throws IOException {
        ParseResult<Summon> result =
                load("bad-flag.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "race-flag:NOTAFLAG", "test")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid race flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
    }

    @Test
    void unknownBaseIsReportedAndSkipped() throws IOException {
        ParseResult<Summon> result =
                load("bad-base.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "base:no such base", "test")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid monster base")),
                result.errors()::toString);
    }

    // ---- Soft error: kept record -----------------------------------------

    @Test
    void unresolvableFallbackIsReportedButTheRecordStillLoads() throws IOException {
        // Unlike the msgt/base/flag branches, an unresolvable fallback does NOT drop the record: it
        // logs a soft error and leaves the fallback null.
        ParseResult<Summon> result =
                load("bad-fallback.txt", withHeader(1, summon("MONSTER", "SUM_MONSTER", "0",
                        "fallback:NOSUCH", "test")));

        assertEquals(1, result.items().size());
        assertNull(result.items().get(0).getFallback());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("Invalid fallback name") && e.contains("NOSUCH")),
                result.errors()::toString);
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

    // ---- Two soft errors at once -----------------------------------------

    @Test
    void twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads() throws IOException {
        // Three records, declared count 3 (so no count error): one clean, one with an unknown
        // message type, one with an unknown race flag. The two bad ones are skipped independently,
        // both errors are recorded, and the clean record still loads - proving the errors accumulate
        // rather than the first one aborting the file.
        String body = plainSummon("GOOD")
                + summon("BADMSG", "NOTREAL", "0", "", "bad msgt")
                + summon("BADFLAG", "SUM_MONSTER", "0", "race-flag:NOTAFLAG", "bad flag");
        ParseResult<Summon> result = load("two-errors.txt", withHeader(3, body));

        assertEquals(1, result.items().size());
        assertEquals("GOOD", result.items().get(0).getName());

        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid message type")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid race flag")),
                result.errors()::toString);
    }
}
