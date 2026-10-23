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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link CurseReader}: file text -> {@code CurseLexer}/
 * {@code CurseGrammar} -> {@code CurseParseRecord} -> {@code CurseAssembler} -> {@link Curse}.
 * These pin the assembler half — the two-family split of the {@code flags:} and {@code values:}
 * lines (object flags vs element flags; additive modifiers vs per-element resistances), the
 * optional weight/combat defaulting to zero, the two-pass {@code conflict:} linking, and both
 * error channels {@code GrammarDriver} threads through {@link ParseResult}: the soft
 * skip-and-continue {@code errors} list and the hard fail-closed channel.
 *
 * <p>The curse assembler resolves {@code type:} via {@link ObjectRegistry#lookupObjectBase}, and the
 * shipped {@code curse.txt} uses {@code EF_SUMMON}, which {@code EffectAssembler} resolves via
 * {@link MonsterRegistry#lookupSummon}. Loading summons in turn needs monster bases, which need
 * monster pains. Rather than run the whole heavy {@code GameConstants.init()}, {@link #seed()}
 * loads {@code pain.txt}, {@code monster_base.txt}, {@code summon.txt} and {@code object_base.txt}
 * directly through their readers and injects the results into the private static registries by
 * reflection, restoring them afterwards so no global state leaks to other suites.
 *
 * <p>The second half pins the reader's own contract against {@code curse_parser} in
 * {@code obj-init.c}: the list in C's last-first order with every field of the 27 shipped curses
 * worked out from the upstream {@code curse.txt}, the shipped file against the upstream file, which
 * repeated directives overwrite ({@code weight:}, {@code combat:}) and which append ({@code type:},
 * {@code flags:}, {@code values:}, {@code desc:}, {@code conflict:}, {@code conflict-flags:}), the
 * 16-bit weight bounds and the two {@code finish_parse_curse} checks, and each place the grammar or
 * assembler differs from C. The differences are written up in {@code docs/grammar_issues.md} and
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class CurseReaderTest coded before 261017, reader-contract tests added and commented in full on
 * 261017.
 *
 * @author Rowan Crowther
 */
class CurseReaderTest {

    private static final String REAL_FILE = "lib/gamedata/curse.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";

    private static Object savedPains;
    private static Object savedBases;
    private static Object savedSummons;
    private static Object savedObjectBases;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seed() throws Exception {
        // Order matters: summons resolve monster bases, bases resolve pains.
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        savedPains = setStatic("monsterPains", pains);

        List<MonsterBase> bases = new MonsterBaseReader().parseWithResults(BASE_FILE).items();
        savedBases = setStatic("monsterBases", bases);

        List<Summon> summons = new SummonReader().parseWithResults(SUMMON_FILE).items();
        savedSummons = setStatic("summons", summons);

        List<ObjectBase> objectBases = new ObjectBaseReader().parseWithResults(OBJECT_BASE_FILE).items();
        savedObjectBases = setStatic("objectBases", objectBases);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
        setStatic("objectBases", savedObjectBases);
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
     * A curse record: a name line followed by whatever body lines the test supplies.
     */
    private static String curse(String name, String body) {
        return "name:" + name + "\n" + body;
    }

    private ParseResult<Curse> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new CurseReader().parseWithResults(file.toString());
    }

    private static Curse byName(List<Curse> curses, String name) {
        return curses.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllTwentySevenCursesWithNoErrors() throws IOException {
        ParseResult<Curse> result = new CurseReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(27, result.items().size());
    }

    @Test
    void combatPenaltiesAndAfflictableBasesResolve() throws IOException {
        // 'vulnerability' has combat:0:0:-50 and afflicts armour/cloak/shield but not boots.
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();
        Curse vuln = byName(curses, "vulnerability");

        assertEquals(0, vuln.getItemObject().getToHit());
        assertEquals(0, vuln.getItemObject().getToDam());
        assertEquals(-50, vuln.getItemObject().getToAC());
        assertTrue(vuln.canAfflict(ObjectRegistry.getBaseFromTVal(TValue.TV_CLOAK)));
        assertFalse(vuln.canAfflict(ObjectRegistry.getBaseFromTVal(TValue.TV_BOOTS)));
    }

    @Test
    void valuesLineSplitsIntoModifiersAndResistancesOnTheRealFile() throws IOException {
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();

        // 'annoyance' values:SPEED[-10] | STEALTH[-10] -> both additive modifiers, no resistances.
        Curse annoyance = byName(curses, "annoyance");
        assertEquals(-10, annoyance.getItemObject().getModifiers().get(ObjectModifier.OM_SPEED));
        assertEquals(-10, annoyance.getItemObject().getModifiers().get(ObjectModifier.OM_STEALTH));
        assertTrue(annoyance.getItemObject().getElInfo().isEmpty());
        assertTrue(annoyance.getItemObject().getFlags().has(ObjectFlag.OF_AGGRAVATE));

        // 'burning up' values:RES_FIRE[-1] | RES_COLD[1] -> both resistances, no modifiers.
        Curse burning = byName(curses, "burning up");
        assertTrue(burning.getItemObject().getModifiers().isEmpty());
        assertEquals(-1, burning.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        assertEquals(1, burning.getItemObject().getElInfo().get(ElementEnum.ELEM_COLD).getResLevel());
    }

    @Test
    void conflictingCursesLinkToTheSameInstancesBothWays() throws IOException {
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();
        Curse burning = byName(curses, "burning up");
        Curse chilled = byName(curses, "chilled to the bone");

        // Each names the other; the second pass must link to the very instances in the list.
        assertEquals(1, burning.getConflict().size());
        assertSame(chilled, burning.getConflict().get(0));
        assertSame(burning, chilled.getConflict().get(0));
    }

    // ---- Values two-family split (synthetic) -----------------------------

    @Test
    void aStatModifierAndAResistanceOnOneLineSplitToTheRightCarriers() throws IOException {
        ParseResult<Curse> result = load("split.txt", withHeader(1,
                curse("mixed", "type:cloak\nvalues:STR[-5] | RES_FIRE[-1]\ndesc:test\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertEquals(-5, c.getItemObject().getModifiers().get(ObjectModifier.OM_STR));
        assertEquals(-1, c.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        // The resistance must NOT have leaked into the additive modifier map.
        assertEquals(1, c.getItemObject().getModifiers().size());
    }

    // ---- Flags two-family split incl. FLAG coverage ----------------------

    @Test
    void hatesAndIgnoreElementFlagsRouteToElInfoFlags() throws IOException {
        // FLAG coverage: HATES_/IGNORE_ tokens set element flags, not object flags.
        ParseResult<Curse> result = load("elem-flags.txt", withHeader(1,
                curse("elemental", "type:cloak\nflags:HATES_FIRE | IGNORE_ACID\ndesc:test\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getItemObject().getFlags().isEmpty());
        assertTrue(c.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE).getFlags().has(ElementInfoEnum.EL_INFO_HATES));
        assertTrue(c.getItemObject().getElInfo().get(ElementEnum.ELEM_ACID).getFlags().has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    @Test
    void plainObjectFlagRoutesToObjectFlags() throws IOException {
        // FLAG coverage: a non-element flag stays an object flag.
        ParseResult<Curse> result = load("obj-flag.txt", withHeader(1,
                curse("annoying", "type:cloak\nflags:AGGRAVATE\ndesc:test\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getItemObject().getFlags().has(ObjectFlag.OF_AGGRAVATE));
        assertTrue(c.getItemObject().getElInfo().isEmpty());
    }

    // ---- Optional weight/combat default to zero (regression) -------------

    @Test
    void absentWeightAndCombatDefaultToZeroAndTheRecordStillLoads() throws IOException {
        // Regression guard: a curse with no weight:/combat: line must load with those at 0,
        // not be dropped as a malformed integer (the empty-string parse bug).
        ParseResult<Curse> result = load("no-weight-combat.txt", withHeader(1,
                curse("plain", "type:cloak\ndesc:test\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertEquals(0, c.getItemObject().getWeight());
        assertEquals(0, c.getItemObject().getToHit());
        assertEquals(0, c.getItemObject().getToDam());
        assertEquals(0, c.getItemObject().getToAC());
    }

    // ---- time: belongs to the curse's object, not to an effect ------------

    /**
     * C's {@code parse_curse_time} writes {@code curse->obj->time}. A {@code time:} line after the
     * curse's effect must land on {@code getItemObject().getTime()}.
     */
    @Test
    void timeAfterTheEffectReachesTheCurseObject() throws IOException {
        ParseResult<Curse> result = load("timed.txt", withHeader(1,
                curse("timed", "type:cloak\ndesc:test\neffect:WAKE\ntime:100+1d50\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Random time = result.items().get(0).getItemObject().getTime();
        assertNotNull(time);
        assertEquals(100, time.getBase());
        assertEquals(1, time.getDice());
        assertEquals(50, time.getSides());
    }

    /**
     * {@code time:} is a record-level line: before the effect it reaches the object just the same.
     */
    @Test
    void timeBeforeTheEffectReachesTheCurseObject() throws IOException {
        ParseResult<Curse> result = load("timed-first.txt", withHeader(1,
                curse("timed first", "type:cloak\ndesc:test\ntime:1d500\neffect:WAKE\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Random time = result.items().get(0).getItemObject().getTime();
        assertEquals(0, time.getBase());
        assertEquals(1, time.getDice());
        assertEquals(500, time.getSides());
    }

    /**
     * A curse with no {@code time:} line holds a zero {@link Random}, never {@code null}.
     */
    @Test
    void absentTimeIsZeroNotNull() throws IOException {
        ParseResult<Curse> result = load("untimed.txt", withHeader(1,
                curse("untimed", "type:cloak\ndesc:test\neffect:WAKE\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Random time = result.items().get(0).getItemObject().getTime();
        assertNotNull(time);
        assertEquals(0, time.getBase());
        assertEquals(0, time.getDice());
        assertEquals(0, time.getSides());
    }

    // ---- A bad record ends the load (run_parser stops at the first error) ----

    @Test
    void unknownTypeEndsTheLoad() {
        // parse_curse_type returns PARSE_ERROR_UNRECOGNISED_TVAL, which stops run_parser.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-type.txt",
                withHeader(1, curse("weird", "type:no such base\ndesc:test\n"))));

        assertTrue(e.getMessage().contains("no such base"), e::getMessage);
    }

    @Test
    void unknownModifierEndsTheLoad() {
        // parse_curse_values returns PARSE_ERROR_INVALID_VALUE.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-mod.txt",
                withHeader(1, curse("weird", "type:cloak\nvalues:NOTAMOD[1]\ndesc:test\n"))));

        assertTrue(e.getMessage().contains("NOTAMOD"), e::getMessage);
    }

    @Test
    void unknownResistanceElementEndsTheLoad() {
        // parse_curse_values returns PARSE_ERROR_INVALID_VALUE.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-res.txt",
                withHeader(1, curse("weird", "type:cloak\nvalues:RES_NOTREAL[1]\ndesc:test\n"))));

        assertTrue(e.getMessage().contains("RES_NOTREAL"), e::getMessage);
    }

    @Test
    void unknownObjectFlagEndsTheLoad() {
        // parse_curse_flags returns PARSE_ERROR_INVALID_FLAG.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-oflag.txt",
                withHeader(1, curse("weird", "type:cloak\nflags:NOTAFLAG\ndesc:test\n"))));

        assertTrue(e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    @Test
    void unknownElementFlagEndsTheLoad() {
        // parse_curse_flags returns PARSE_ERROR_INVALID_FLAG.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-eflag.txt",
                withHeader(1, curse("weird", "type:cloak\nflags:HATES_NOTREAL\ndesc:test\n"))));

        assertTrue(e.getMessage().contains("HATES_NOTREAL"), e::getMessage);
    }

    // ---- Conflict names (accepted deviation) -------------------------------

    @Test
    void unresolvableConflictEndsTheLoadNamingTheCurseAndTheName() {
        // Accepted deviation (docs/Accepted_Deviations.md): C never checks a conflict: name.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-conflict.txt",
                withHeader(1, curse("lonely", "type:cloak\nconflict:no such curse\ndesc:test\n"))));

        assertTrue(e.getMessage().contains("lonely"), e::getMessage);
        assertTrue(e.getMessage().contains("no such curse"), e::getMessage);
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        ParseResult<Curse> result = load("bad-count.txt", withHeader(5,
                curse("plain", "type:cloak\ndesc:test\n")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<Curse> result = load("no-header.txt", curse("plain", "type:cloak\ndesc:test\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        String noName = "type:cloak\ndesc:orphan\n";
        ParseResult<Curse> result = load("no-name.txt", withHeader(1, noName));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Two bad records: only the first is reached ------------------------

    @Test
    void theFirstOfTwoBadRecordsEndsTheLoad() {
        // run_parser stops at the first parse error, so the second bad record is never read.
        String body = curse("good", "type:cloak\ndesc:fine\n")
                + curse("badmod", "type:cloak\nvalues:NOTAMOD[1]\ndesc:bad\n")
                + curse("badflag", "type:cloak\nflags:NOTAFLAG\ndesc:bad\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("two-errors.txt", withHeader(3, body)));

        assertTrue(e.getMessage().contains("NOTAMOD"), e::getMessage);
        assertFalse(e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    // ---- Reader contract, worked out from the upstream curse.txt and obj-init.c ----

    private static final Path UPSTREAM_FILE =
            Path.of(System.getProperty("user.home"), "Desktop/Angband-4.2.6/lib/gamedata/curse.txt");

    /**
     * One row of the table below: what {@code parse_curse_*} leaves on a curse for the upstream
     * record of that name. {@code timeBase}/{@code timeDice}/{@code timeSides} are
     * {@code curse->obj->time}; an absent {@code time:} line is all zero.
     */
    private record Expected(String name, int bases, int toHit, int toDam, int toAC, int effects,
                            int timeBase, int timeDice, int timeSides, String msg, String desc) {
    }

    private static Expected row(String name, int bases, int toh, int tod, int toa, int effects,
                                int tb, int td, int ts, String msg, String desc) {
        return new Expected(name, bases, toh, tod, toa, effects, tb, td, ts, msg, desc);
    }

    /**
     * The 27 upstream records in C's order. {@code parse_curse_name} pushes each record on the head
     * of a chain and {@code finish_parse_curse} fills {@code curses[1..]} from that head, so the
     * file's last record ({@code air swing}) is slot 1 and its first ({@code vulnerability}) is
     * last. {@code bases} counts the {@code type:} lines; {@code msg} is "" where the record has no
     * {@code msg:} line.
     */
    private static final List<Expected> UPSTREAM_IN_C_ORDER = List.of(
            row("air swing", 4, -20, 0, 0, 0, 0, 0, 0, "", "makes it hard to hit anything"),
            row("steelskin", 12, 0, 0, 20, 0, 0, 0, 0, "", "makes your skin harder to damage, but conduct electricity"),
            row("chilled to the bone", 9, 0, 0, 0, 0, 0, 0, 0, "", "makes you vulnerable to cold attacks, but resistant to fire"),
            row("burning up", 9, 0, 0, 0, 0, 0, 0, 0, "", "makes you vulnerable to fire attacks, but resistant to cold"),
            row("treacherous weapon", 4, 0, 0, 0, 1, 0, 1, 200, "Your weapon turns on you!", "makes your weapon attack you"),
            row("anti-teleportation", 7, 0, 0, 0, 0, 0, 0, 0, "", "prevents you from teleporting"),
            row("stone", 2, 0, 0, 0, 1, 0, 1, 1000, "", "periodically turns your skin to stone"),
            row("cowardice", 2, 0, 0, 0, 0, 0, 0, 0, "", "makes you too scared to fight"),
            row("impair hitpoint recovery", 7, 0, 0, 0, 0, 0, 0, 0, "", "makes you slow to recover hitpoints"),
            row("impair mana recovery", 2, 0, 0, 0, 0, 0, 0, 0, "", "makes you slow to recover mana"),
            row("undead summon", 4, 0, 0, 0, 1, 0, 1, 500, "Your weapon calls beyond the grave!", "occasionally summons an undead"),
            row("demon summon", 4, 0, 0, 0, 1, 0, 1, 500, "Your weapon gives out a demonic shriek!", "occasionally summons a demon"),
            row("dragon summon", 1, 0, 0, 0, 1, 0, 1, 500, "Your armour lets out a cry...", "sometimes summons a dragon"),
            row("paralysis", 17, 0, 0, 0, 1, 0, 1, 1000, "Your equipment grabs you!", "paralyses you every now and then"),
            row("hallucination", 17, 0, 0, 0, 1, 0, 1, 500, "", "makes you hallucinate sometimes"),
            row("siren", 17, 0, 0, 0, 1, 100, 1, 50, "A horn blares out!", "occasionally makes a loud noise"),
            row("poison", 17, 0, 0, 0, 1, 0, 1, 500, "", "poisons you from time to time"),
            row("annoyance", 1, 0, 0, 0, 0, 0, 0, 0, "", "wakes up and annoys nearby monsters, and delays your escape"),
            row("slowness", 1, 0, 0, 0, 0, 0, 0, 0, "", "drags at your feet"),
            row("clumsiness", 1, 0, 0, 0, 0, 0, 0, 0, "", "gives you butterfingers"),
            row("weakness", 1, 0, 0, 0, 0, 0, 0, 0, "", "weakens your grasp"),
            row("irritation", 1, -15, -15, 0, 0, 0, 0, 0, "", "annoys monsters and makes it hard for you to hit them"),
            row("enveloping", 1, -5, -5, 20, 0, 0, 0, 0, "", "protects you at the expense of free movement"),
            row("sickliness", 2, 0, 0, 0, 0, 0, 0, 0, "", "makes you frail"),
            row("dullness", 2, 0, 0, 0, 0, 0, 0, 0, "", "makes you mentally slow"),
            row("teleportation", 4, 0, 0, 0, 1, 0, 1, 100, "Space warps around you.", "randomly makes you teleport"),
            row("vulnerability", 5, 0, 0, -50, 0, 0, 0, 0, "", "attracts opponents and weakens the defences"));

    /**
     * Reads {@code ItemObject.effectMessage}, which has no getter yet; the only production reader
     * will be {@code do_curse_effect}.
     */
    private static String effectMessage(Curse curse) throws Exception {
        Field f = curse.getItemObject().getClass().getDeclaredField("effectMessage");
        f.setAccessible(true);
        return (String) f.get(curse.getItemObject());
    }

    private static Expected actual(Curse c) throws Exception {
        Random t = c.getItemObject().getTime();
        String msg = effectMessage(c);
        return new Expected(c.getName(), c.getTValues().size(), c.getItemObject().getToHit(),
                c.getItemObject().getToDam(), c.getItemObject().getToAC(),
                c.getItemObject().getEffect().size(), t.getBase(), t.getDice(), t.getSides(),
                msg == null ? "" : msg, c.getDescription());
    }

    @Test
    void realFileHoldsEveryCurseInCsLastFirstOrderWithTheFieldsTheUpstreamFileGives() throws Exception {
        ParseResult<Curse> result = new CurseReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<Expected> got = new java.util.ArrayList<>();
        for (Curse c : result.items()) {
            got.add(actual(c));
        }
        assertEquals(UPSTREAM_IN_C_ORDER, got);
    }

    @Test
    void realFileIndicesAreBaseZeroAndFollowTheListOrder() throws IOException {
        // C's slot 0 is a blank record, so air swing is curses[1]; the accepted deviation keeps no
        // blank slot, so here it is index 0 and vulnerability is index 26.
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();

        for (int i = 0; i < curses.size(); i++) {
            assertEquals(i, curses.get(i).getIndex());
        }
        assertEquals("air swing", curses.get(0).getName());
        assertEquals("vulnerability", curses.get(26).getName());
    }

    @Test
    void realFileMatchesTheUpstreamFileApartFromTheRecordCountHeader() throws IOException {
        // The shipped copy adds one record-count: line and a blank line after it; the upstream file
        // has neither. Every other line is the same, so the expected values above are C's.
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.exists(UPSTREAM_FILE));

        List<String> ours = new java.util.ArrayList<>(Files.readAllLines(Path.of(REAL_FILE)));
        int header = ours.indexOf("record-count:27");
        ours.remove(header + 1);
        ours.remove(header);

        assertEquals(Files.readAllLines(UPSTREAM_FILE), ours);
    }

    @Test
    void realFileConflictsAndFlagsAreAsUpstreamGivesThem() throws IOException {
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();

        // conflict: lines
        assertEquals(List.of("anti-teleportation"), byName(curses, "teleportation").getConflictNames());
        assertEquals(List.of("teleportation"), byName(curses, "anti-teleportation").getConflictNames());
        assertTrue(byName(curses, "air swing").getConflictNames().isEmpty());
        // conflict-flags: lines, including the two-flag line on cowardice
        assertTrue(byName(curses, "teleportation").getConflictFlags().has(ObjectFlag.OF_NO_TELEPORT));
        Curse cowardice = byName(curses, "cowardice");
        assertTrue(cowardice.getConflictFlags().has(ObjectFlag.OF_AFRAID));
        assertTrue(cowardice.getConflictFlags().has(ObjectFlag.OF_PROT_FEAR));
        // flags: lines
        assertTrue(cowardice.getItemObject().getFlags().has(ObjectFlag.OF_AFRAID));
        assertFalse(cowardice.getItemObject().getFlags().has(ObjectFlag.OF_PROT_FEAR));
        assertTrue(byName(curses, "impair mana recovery").getItemObject().getFlags().has(ObjectFlag.OF_IMPAIR_MANA));
    }

    @Test
    void realFileValuesAreAsUpstreamGivesThem() throws IOException {
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();

        var dullness = byName(curses, "dullness").getItemObject().getModifiers();
        assertEquals(-5, dullness.get(ObjectModifier.OM_INT));
        assertEquals(-5, dullness.get(ObjectModifier.OM_WIS));
        assertEquals(2, dullness.size());
        var sickliness = byName(curses, "sickliness").getItemObject().getModifiers();
        assertEquals(-5, sickliness.get(ObjectModifier.OM_STR));
        assertEquals(-5, sickliness.get(ObjectModifier.OM_DEX));
        assertEquals(-5, sickliness.get(ObjectModifier.OM_CON));
        assertEquals(-10, byName(curses, "weakness").getItemObject().getModifiers().get(ObjectModifier.OM_STR));
        assertEquals(-5, byName(curses, "slowness").getItemObject().getModifiers().get(ObjectModifier.OM_SPEED));
        var steel = byName(curses, "steelskin").getItemObject().getElInfo();
        assertEquals(-1, steel.get(ElementEnum.ELEM_ELEC).getResLevel());
        assertEquals(1, steel.get(ElementEnum.ELEM_SHARD).getResLevel());
    }

    @Test
    void parseReturnsTheSameCursesAsParseWithResults() throws IOException {
        CurseReader reader = new CurseReader();

        assertEquals(reader.parseWithResults(REAL_FILE).items().stream().map(Curse::getName).toList(),
                reader.parse(REAL_FILE).stream().map(Curse::getName).toList());
    }

    @Test
    void parseAlsoEndsTheLoadOnABadRecord() throws IOException {
        Path file = tempDir.resolve("parse-bad.txt");
        Files.writeString(file, withHeader(2, curse("good", "type:cloak\ndesc:fine\n")
                + curse("bad", "type:no such base\ndesc:bad\n")));

        assertThrows(DataFileParseException.class, () -> new CurseReader().parse(file.toString()));
    }

    @Test
    void missingFileIsAnIOExceptionWhereCQuits() {
        // parse_file_quit_not_found calls quit() on a file that cannot be opened; here it surfaces
        // as an exception for the caller to treat as fatal.
        assertThrows(IOException.class,
                () -> new CurseReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    // ---- List order ------------------------------------------------------

    @Test
    void listIsInReverseFileOrderAndIndicesFollowIt() throws IOException {
        ParseResult<Curse> result = load("order.txt", withHeader(3,
                curse("first", "type:cloak\ndesc:a\n") + curse("second", "type:cloak\ndesc:b\n")
                        + curse("third", "type:cloak\ndesc:c\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("third", "second", "first"), result.items().stream().map(Curse::getName).toList());
        assertEquals(List.of(0, 1, 2), result.items().stream().map(Curse::getIndex).toList());
    }

    @Test
    void aTvalWithNoObjectBaseLoadsAsInC() throws IOException {
        // tval_find_idx accepts "none" and "other book", neither of which has an object_base.txt
        // entry; parse_curse_type only sets poss[tval].
        ParseResult<Curse> result = load("baseless.txt", withHeader(1,
                curse("odd", "type:none\ntype:other book\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of(TValue.TV_NONE, TValue.TV_OTHER_BOOK), result.items().get(0).getTValues());
    }

    @Test
    void twoRecordsWithTheSameNameBothLoadAsInCAndTheLaterOneComesFirst() throws IOException {
        ParseResult<Curse> result = load("dupe.txt", withHeader(2,
                curse("twin", "type:cloak\ndesc:early\n") + curse("twin", "type:cloak\ndesc:late\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("late", "early"), result.items().stream().map(Curse::getDescription).toList());
    }

    @Test
    void conflictOnASharedNameLinksToTheFirstInTheReversedList() throws IOException {
        // lookup_curse walks curses[] from slot 1, so the later record in the file wins.
        ParseResult<Curse> result = load("dupe-conflict.txt", withHeader(3,
                curse("twin", "type:cloak\ndesc:early\n") + curse("twin", "type:cloak\ndesc:late\n")
                        + curse("other", "type:cloak\nconflict:twin\ndesc:x\n")));

        Curse other = byName(result.items(), "other");
        assertEquals(1, other.getConflict().size());
        assertEquals("late", other.getConflict().get(0).getDescription());
    }

    // ---- Repeated directives: what C overwrites and what it appends -----------

    @Test
    void repeatedDescLinesRunTogetherWithNoSeparatorAsInC() throws IOException {
        // parse_curse_desc is string_append, so a space has to be written into the data.
        ParseResult<Curse> result = load("desc2.txt", withHeader(1,
                curse("long", "type:cloak\ndesc:attracts\ndesc: opponents\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("attracts opponents", result.items().get(0).getDescription());
    }

    @Test
    void repeatedFlagsLinesAccumulateAsInC() throws IOException {
        ParseResult<Curse> result = load("flags2.txt", withHeader(1,
                curse("two", "type:cloak\nflags:AGGRAVATE\nflags:AFRAID | HATES_FIRE\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getItemObject().getFlags().has(ObjectFlag.OF_AGGRAVATE));
        assertTrue(c.getItemObject().getFlags().has(ObjectFlag.OF_AFRAID));
        assertTrue(c.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE).getFlags().has(ElementInfoEnum.EL_INFO_HATES));
    }

    @Test
    void repeatedValuesLinesAccumulateAndALaterOneForTheSameKeyWinsAsInC() throws IOException {
        ParseResult<Curse> result = load("values2.txt", withHeader(1,
                curse("two", "type:cloak\nvalues:STR[-1] | INT[-2]\nvalues:STR[-7] | RES_FIRE[1]\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertEquals(-7, c.getItemObject().getModifiers().get(ObjectModifier.OM_STR));
        assertEquals(-2, c.getItemObject().getModifiers().get(ObjectModifier.OM_INT));
        assertEquals(1, c.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
    }

    @Test
    void repeatedTypeLinesAccumulateAsInC() throws IOException {
        ParseResult<Curse> result = load("type2.txt", withHeader(1,
                curse("two", "type:cloak\ntype:boots\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.canAfflict(ObjectRegistry.getBaseFromTVal(TValue.TV_CLOAK)));
        assertTrue(c.canAfflict(ObjectRegistry.getBaseFromTVal(TValue.TV_BOOTS)));
        assertFalse(c.canAfflict(ObjectRegistry.getBaseFromTVal(TValue.TV_GLOVES)));
    }

    @Test
    void repeatedWeightAndCombatLinesKeepTheLastAsInC() throws IOException {
        ParseResult<Curse> result = load("wc2.txt", withHeader(1,
                curse("two", "type:cloak\nweight:50\nweight:-20\ncombat:1:2:3\ncombat:4:5:6\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertEquals(-20, c.getItemObject().getWeight());
        // combat: is to-h : to-d : to-a
        assertEquals(4, c.getItemObject().getToHit());
        assertEquals(5, c.getItemObject().getToDam());
        assertEquals(6, c.getItemObject().getToAC());
    }

    @Test
    void repeatedConflictLinesAccumulateAsInC() throws IOException {
        ParseResult<Curse> result = load("conf2.txt", withHeader(3,
                curse("a", "type:cloak\ndesc:x\n") + curse("b", "type:cloak\ndesc:x\n")
                        + curse("c", "type:cloak\nconflict:a\nconflict:b\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("a", "b"),
                byName(result.items(), "c").getConflict().stream().map(Curse::getName).toList());
    }

    @Test
    void repeatedConflictFlagsLinesAccumulateAsInC() throws IOException {
        ParseResult<Curse> result = load("cflags2.txt", withHeader(1,
                curse("two", "type:cloak\nconflict-flags:AGGRAVATE\nconflict-flags:AFRAID | PROT_FEAR\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getConflictFlags().has(ObjectFlag.OF_AGGRAVATE));
        assertTrue(c.getConflictFlags().has(ObjectFlag.OF_AFRAID));
        assertTrue(c.getConflictFlags().has(ObjectFlag.OF_PROT_FEAR));
    }

    @Test
    void conflictFlagsAreNotAddedToTheCursesOwnFlags() throws IOException {
        // parse_curse_conflict_flags fills curse->conflict_flags; parse_curse_flags fills obj->flags.
        ParseResult<Curse> result = load("cflags-only.txt", withHeader(1,
                curse("one", "type:cloak\nconflict-flags:AGGRAVATE\ndesc:x\n")));

        Curse c = result.items().get(0);
        assertTrue(c.getConflictFlags().has(ObjectFlag.OF_AGGRAVATE));
        assertTrue(c.getItemObject().getFlags().isEmpty());
    }

    @Test
    void directivesMayComeInAnyOrderAfterTheNameAsInC() throws IOException {
        ParseResult<Curse> result = load("any-order.txt", withHeader(1,
                curse("shuffled", "desc:last first\ncombat:1:2:3\nflags:AGGRAVATE\nweight:5\ntype:cloak\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertEquals("last first", c.getDescription());
        assertEquals(5, c.getItemObject().getWeight());
        assertEquals(3, c.getItemObject().getToAC());
        assertEquals(1, c.getTValues().size());
    }

    // ---- Text fields -----------------------------------------------------

    @Test
    void freeTextKeepsColonsAndTrailingSpaces() throws IOException {
        // "str" fields take the rest of the line, colons and trailing blanks included.
        ParseResult<Curse> result = load("free-text.txt", withHeader(1,
                curse("odd: name ", "type:cloak\ndesc:a: b \n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("odd: name ", result.items().get(0).getName());
        assertEquals("a: b ", result.items().get(0).getDescription());
    }

    @Test
    void commentAndBlankLinesInsideARecordAreIgnored() throws IOException {
        ParseResult<Curse> result = load("comments.txt", withHeader(1,
                "# a note\n\n" + curse("noted", "# inside\ntype:cloak\n\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("noted", result.items().get(0).getName());
    }

    @Test
    void theStopNamesTheLineOfTheRecordsNameDirective() {
        // Line 1 is the header, line 2 the first record's name:, line 5 the second's.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("lines.txt",
                withHeader(2, curse("good", "type:cloak\ndesc:x\n") + curse("bad", "type:no such base\ndesc:x\n"))));

        assertTrue(e.getMessage().contains("line: 5"), e::getMessage);
    }

    // ---- msg: and time: --------------------------------------------------

    @Test
    void absentMsgLeavesNullAsCDoes() throws Exception {
        // Recorded in docs/Assembler_Issues.md: C's effect_msg stays NULL when there is no msg: line.
        ParseResult<Curse> result = load("no-msg.txt", withHeader(1, curse("quiet", "type:cloak\ndesc:x\n")));

        assertNull(effectMessage(result.items().get(0)));
    }

    @Test
    void msgReachesTheCurseObjectNotTheEffect() throws Exception {
        ParseResult<Curse> result = load("msg.txt", withHeader(1,
                curse("loud", "type:cloak\neffect:WAKE\nmsg:A horn blares out!\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("A horn blares out!", effectMessage(result.items().get(0)));
    }

    @Test
    void aSecondMsgLineIsAppendedToTheFirstAsCDoes() throws Exception {
        // parse_curse_msg is string_append, so C holds "AB". Recorded in docs/grammar_issues.md.
        ParseResult<Curse> result = load("msg2.txt", withHeader(1,
                curse("loud", "type:cloak\neffect:WAKE\nmsg:A\nmsg:B\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("AB", effectMessage(result.items().get(0)));
    }

    @Test
    void timeTakesTheRandomForms() throws IOException {
        // parse_random: "5" is a base only, "d10" one die, "2d6M3" dice with an M bonus.
        for (String[] c : new String[][]{{"5", "5", "0", "0"}, {"d10", "0", "1", "10"}, {"2d6", "0", "2", "6"}}) {
            ParseResult<Curse> result = load("time-" + c[0] + ".txt", withHeader(1,
                    curse("t", "type:cloak\neffect:WAKE\ntime:" + c[0] + "\ndesc:x\n")));

            assertFalse(result.hasErrors(), () -> c[0] + " " + result.errors());
            Random t = result.items().get(0).getItemObject().getTime();
            assertEquals(Integer.parseInt(c[1]), t.getBase(), c[0]);
            assertEquals(Integer.parseInt(c[2]), t.getDice(), c[0]);
            assertEquals(Integer.parseInt(c[3]), t.getSides(), c[0]);
        }
    }

    // ---- Effects ---------------------------------------------------------

    @Test
    void severalEffectsOnOneCurseAllLoadInOrder() throws IOException {
        ParseResult<Curse> result = load("effects2.txt", withHeader(1,
                curse("busy", "type:cloak\neffect:WAKE\neffect:TIMED_INC:POISONED\ndice:5\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().get(0).getItemObject().getEffect().size());
    }

    @Test
    void effectYxLoadsWhereTheCurseParserRegistersIt() throws IOException {
        ParseResult<Curse> result = load("yx.txt", withHeader(1,
                curse("ranged", "type:cloak\neffect:WAKE\neffect-yx:2:3\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().get(0).getItemObject().getEffect().size());
    }

    @Test
    void anUnknownEffectEndsTheLoad() {
        // parse_curse_effect returns grab_effect_data's error, which stops run_parser.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-effect.txt",
                withHeader(1, curse("odd", "type:cloak\neffect:NOT_AN_EFFECT\ndesc:x\n"))));

        assertTrue(e.getMessage().contains("effect"), e::getMessage);
    }

    // ---- Weight and the finish_parse_curse checks -----------------------------

    @Test
    void weightAtTheInt16BoundsLoadsAndOneBeyondEndsTheLoad() throws IOException {
        // parse_curse_weight rejects anything outside -32768..32767 with PARSE_ERROR_INVALID_VALUE.
        for (int w : new int[]{-32768, 32767}) {
            ParseResult<Curse> ok = load("w" + w + ".txt", withHeader(1,
                    curse("w", "type:cloak\nweight:" + w + "\ndesc:x\n")));
            assertFalse(ok.hasErrors(), () -> ok.errors().toString());
            assertEquals(w, ok.items().get(0).getItemObject().getWeight());
        }
        for (int w : new int[]{-32769, 32768}) {
            DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("w" + w + ".txt",
                    withHeader(1, curse("w", "type:cloak\nweight:" + w + "\ndesc:x\n"))), "weight " + w);
            assertTrue(e.getMessage().contains("out of range"), e::getMessage);
        }
    }

    @Test
    void negativeWeightWithMultiplyWeightEndsTheLoadAsFinishParseCurseDoes() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("mw-neg.txt",
                withHeader(2, curse("shrink", "type:cloak\nweight:-5\nflags:MULTIPLY_WEIGHT\ndesc:x\n")
                        + curse("grow", "type:cloak\nweight:150\nflags:MULTIPLY_WEIGHT\ndesc:x\n"))));

        assertTrue(e.getMessage().contains("negative weight"), e::getMessage);
    }

    @Test
    void negativeWeightWithoutMultiplyWeightIsAccepted() throws IOException {
        ParseResult<Curse> result = load("add-neg.txt", withHeader(1,
                curse("light", "type:cloak\nweight:-5\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-5, result.items().get(0).getItemObject().getWeight());
    }

    @Test
    void twoHundredFiftyFourCursesLoadAndTheLimitIsTheLastOneC() throws IOException {
        // finish_parse_curse stops counting at 254 entries.
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < 254; i++) {
            body.append(curse("c" + i, "type:cloak\ndesc:x\n"));
        }
        ParseResult<Curse> result = load("max.txt", withHeader(254, body.toString()));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(254, result.items().size());
    }

    @Test
    void aTwoHundredFiftyFifthCurseEndsTheLoad() {
        // C returns PARSE_ERROR_TOO_MANY_ENTRIES from finish_parse_curse, which ends the load.
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            body.append(curse("c" + i, "type:cloak\ndesc:x\n"));
        }

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("over.txt", withHeader(255, body.toString())));
        assertTrue(e.getMessage().contains("Too many"), e::getMessage);
    }

    // ---- Where the grammar is stricter than C ---------------------------------

    private void assertHardError(String name, String content) throws IOException {
        ParseResult<Curse> result = load(name, content);

        assertTrue(result.items().isEmpty(), () -> name + " loaded " + result.items().size());
        assertTrue(result.hasErrors(), name);
    }

    @Test
    void aRecordWithOnlyANameIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // parse_curse_name builds a complete record on its own.
        assertHardError("name-only.txt", withHeader(1, "name:bare\n"));
    }

    @Test
    void aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing() throws IOException {
        // finish_parse_curse copes with an empty chain: curse_max becomes 1, only the blank slot.
        assertHardError("empty.txt", "record-count:0\n");
    }

    @Test
    void aDiceLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        // parse_curse_dice returns PARSE_ERROR_NONE when the curse has no effect yet.
        assertHardError("dice-first.txt", withHeader(1,
                curse("early", "type:cloak\ndice:5\neffect:WAKE\ndesc:x\n")));
    }

    @Test
    void effectYxAndDiceOnTheSameEffectAreAHardErrorHereWhereCTakesBoth() throws IOException {
        assertHardError("yx-dice.txt", withHeader(1,
                curse("both", "type:cloak\neffect:TIMED_INC:POISONED\neffect-yx:2:3\ndice:5\ndesc:x\n")));
    }

    @Test
    void aSecondDiceLineOnOneEffectIsAHardErrorHereWhereCReplacesTheFirst() throws IOException {
        // parse_curse_dice frees the effect's old dice and installs the new ones.
        assertHardError("dice2.txt", withHeader(1,
                curse("twice", "type:cloak\neffect:TIMED_INC:POISONED\ndice:5\ndice:6\ndesc:x\n")));
    }

    @Test
    void effectMsgIsAcceptedHereWhereCHasNoSuchDirective() throws IOException {
        // init_parse_curse registers no effect-msg, so C gives PARSE_ERROR_UNDEFINED_DIRECTIVE.
        ParseResult<Curse> result = load("effect-msg.txt", withHeader(1,
                curse("chatty", "type:cloak\neffect:WAKE\neffect-msg:hello\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void flagsSeparatedBySpacesAloneAreAHardErrorHereWhereCAcceptsThem() throws IOException {
        // strtok(s, " |") makes "AGGRAVATE AFRAID" two flags.
        assertHardError("flags-space.txt", withHeader(1,
                curse("spaced", "type:cloak\nflags:AGGRAVATE AFRAID\ndesc:x\n")));
    }

    @Test
    void valuesSeparatedBySpacesAloneAreAHardErrorHereWhereCAcceptsThem() throws IOException {
        // parse_curse_values runs strtok(s, " |") as parse_curse_flags does.
        assertHardError("values-space.txt", withHeader(1,
                curse("spaced", "type:cloak\nvalues:STR[-1] INT[-1]\ndesc:x\n")));
    }

    @Test
    void conflictFlagsSeparatedBySpacesAloneAreAHardErrorHereWhereCAcceptsThem() throws IOException {
        assertHardError("cflags-space.txt", withHeader(1,
                curse("spaced", "type:cloak\nconflict-flags:AGGRAVATE AFRAID\ndesc:x\n")));
    }

    @Test
    void aLeadingZeroWeightIsOctalAsInC() throws IOException {
        // parser_parse reads an int with strtol(tok, &z, 0), so "010" is 8 in C.
        ParseResult<Curse> result = load("octal.txt", withHeader(1, curse("oct", "type:cloak\nweight:010\ndesc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(8, result.items().get(0).getItemObject().getWeight());
    }

    @Test
    void aHexWeightIsAHardErrorHereWhereCReadsItAsSixteen() throws IOException {
        assertHardError("hex.txt", withHeader(1, curse("hex", "type:cloak\nweight:0x10\ndesc:x\n")));
    }

    @Test
    void aPlusSignOnACombatNumberIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // strtol accepts a leading '+'.
        assertHardError("plus.txt", withHeader(1, curse("plus", "type:cloak\ncombat:+1:0:0\ndesc:x\n")));
    }

    @Test
    void anUnknownDirectiveIsAHardErrorAsInC() throws IOException {
        // PARSE_ERROR_UNDEFINED_DIRECTIVE
        assertHardError("unknown-directive.txt", withHeader(1,
                curse("odd", "type:cloak\nrarity:3\ndesc:x\n")));
    }

    @Test
    void aNonNumericWeightIsAHardErrorAsInC() throws IOException {
        // PARSE_ERROR_NOT_NUMBER
        assertHardError("weight-text.txt", withHeader(1,
                curse("odd", "type:cloak\nweight:heavy\ndesc:x\n")));
    }

    @Test
    void aCombatLineWithTwoNumbersIsAHardErrorAsInC() throws IOException {
        // "combat int to-h int to-d int to-a" needs all three.
        assertHardError("combat-two.txt", withHeader(1,
                curse("odd", "type:cloak\ncombat:1:2\ndesc:x\n")));
    }

    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        assertHardError("indent.txt", withHeader(1, curse("odd", "type:cloak\n  desc:x\n")));
    }

    // ---- Where the assembler differs from C ------------------------------------

    @Test
    void aConflictNameNoCurseHasEndsTheLoadHereWhereCNeverChecksIt() {
        // C keeps "|name|" in curse->conflict and only ever searches it, so an unmatched name is
        // harmless. Accepted deviation, recorded in docs/Accepted_Deviations.md.
        assertThrows(DataFileParseException.class, () -> load("conflict-unknown.txt",
                withHeader(1, curse("lonely", "type:cloak\nconflict:nobody\ndesc:x\n"))));
    }

    @Test
    void aRecordWithNoTypeLoadsAndAfflictsNothing() throws IOException {
        // poss is zero-filled, so a curse with no type: lines can afflict no tval.
        ParseResult<Curse> result = load("no-type.txt", withHeader(1, curse("loose", "desc:x\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getTValues().isEmpty());
        assertFalse(c.canAfflict(ObjectRegistry.getBaseFromTVal(TValue.TV_CLOAK)));
    }
}
