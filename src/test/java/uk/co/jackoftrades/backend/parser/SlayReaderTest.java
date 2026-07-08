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
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.objects.Slay;

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
 * <p>The assembler resolves {@code base:} via {@link GameConstants#getMonsterBase}, which reads the
 * static {@code monsterBases} list (and throws if it is {@code null}); loading those in turn needs
 * {@code monsterPains}. Rather than run the whole heavy {@code GameConstants.init()}, {@link #seed()}
 * loads {@code pain.txt} then {@code monster_base.txt} directly through their readers and injects the
 * results into the private static fields by reflection, restoring them afterwards so no global state
 * leaks to other suites.
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
        assertEquals("EVIL_2", result.items().get(0).getCode());
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

    /**
     * {@link #slay} but with an explicit race flag, for the numeric-overflow cases.
     */
    private static String raceFlagSlayWith(String code, String flag,
                                           String mult, String oMult, String power) {
        return slay(code, "race-flag:" + flag, mult, oMult, power);
    }
}
