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
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

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
 * <p>The curse assembler resolves {@code type:} via {@link GameConstants#lookupObjectBase}, and the
 * shipped {@code curse.txt} uses {@code EF_SUMMON}, which {@code EffectAssembler} resolves via
 * {@link GameConstants#lookupSummon}. Loading summons in turn needs monster bases, which need
 * monster pains. Rather than run the whole heavy {@code GameConstants.init()}, {@link #seed()}
 * loads {@code pain.txt}, {@code monster_base.txt}, {@code summon.txt} and {@code object_base.txt}
 * directly through their readers and injects the results into the private static registries by
 * reflection, restoring them afterwards so no global state leaks to other suites.
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

        assertEquals(0, vuln.getCombatToHit());
        assertEquals(0, vuln.getCombatDam());
        assertEquals(-50, vuln.getCombatAC());
        assertTrue(vuln.canAfflict(GameConstants.getBaseFromTVal(TValue.TV_CLOAK)));
        assertFalse(vuln.canAfflict(GameConstants.getBaseFromTVal(TValue.TV_BOOTS)));
    }

    @Test
    void valuesLineSplitsIntoModifiersAndResistancesOnTheRealFile() throws IOException {
        List<Curse> curses = new CurseReader().parseWithResults(REAL_FILE).items();

        // 'annoyance' values:SPEED[-10] | STEALTH[-10] -> both additive modifiers, no resistances.
        Curse annoyance = byName(curses, "annoyance");
        assertEquals(-10, annoyance.getModifiers().get(ObjectModifier.OM_SPEED));
        assertEquals(-10, annoyance.getModifiers().get(ObjectModifier.OM_STEALTH));
        assertTrue(annoyance.getElInfo().isEmpty());
        assertTrue(annoyance.getObjectFlags().contains(ObjectFlag.OF_AGGRAVATE));

        // 'burning up' values:RES_FIRE[-1] | RES_COLD[1] -> both resistances, no modifiers.
        Curse burning = byName(curses, "burning up");
        assertTrue(burning.getModifiers().isEmpty());
        assertEquals(-1, burning.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        assertEquals(1, burning.getElInfo().get(ElementEnum.ELEM_COLD).getResLevel());
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
        assertEquals(-5, c.getModifiers().get(ObjectModifier.OM_STR));
        assertEquals(-1, c.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        // The resistance must NOT have leaked into the additive modifier map.
        assertEquals(1, c.getModifiers().size());
    }

    // ---- Flags two-family split incl. FLAG coverage ----------------------

    @Test
    void hatesAndIgnoreElementFlagsRouteToElInfoFlags() throws IOException {
        // FLAG coverage: HATES_/IGNORE_ tokens set element flags, not object flags.
        ParseResult<Curse> result = load("elem-flags.txt", withHeader(1,
                curse("elemental", "type:cloak\nflags:HATES_FIRE | IGNORE_ACID\ndesc:test\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getObjectFlags().isEmpty());
        assertTrue(c.getElInfo().get(ElementEnum.ELEM_FIRE).getFlags().has(ElementInfoEnum.EL_INFO_HATES));
        assertTrue(c.getElInfo().get(ElementEnum.ELEM_ACID).getFlags().has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    @Test
    void plainObjectFlagRoutesToObjectFlags() throws IOException {
        // FLAG coverage: a non-element flag stays an object flag.
        ParseResult<Curse> result = load("obj-flag.txt", withHeader(1,
                curse("annoying", "type:cloak\nflags:AGGRAVATE\ndesc:test\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Curse c = result.items().get(0);
        assertTrue(c.getObjectFlags().contains(ObjectFlag.OF_AGGRAVATE));
        assertTrue(c.getElInfo().isEmpty());
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
        assertEquals(0, c.getWeight());
        assertEquals(0, c.getCombatToHit());
        assertEquals(0, c.getCombatDam());
        assertEquals(0, c.getCombatAC());
    }

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void unknownTypeIsReportedAndSkipped() throws IOException {
        ParseResult<Curse> result = load("bad-type.txt", withHeader(1,
                curse("weird", "type:no such base\ndesc:test\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("unknown type") && e.contains("no such base")),
                result.errors()::toString);
    }

    @Test
    void unknownModifierIsReportedAndSkipped() throws IOException {
        ParseResult<Curse> result = load("bad-mod.txt", withHeader(1,
                curse("weird", "type:cloak\nvalues:NOTAMOD[1]\ndesc:test\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown modifier")),
                result.errors()::toString);
    }

    @Test
    void unknownResistanceElementIsReportedAndSkipped() throws IOException {
        ParseResult<Curse> result = load("bad-res.txt", withHeader(1,
                curse("weird", "type:cloak\nvalues:RES_NOTREAL[1]\ndesc:test\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown element")),
                result.errors()::toString);
    }

    @Test
    void unknownObjectFlagIsReportedAndSkipped() throws IOException {
        ParseResult<Curse> result = load("bad-oflag.txt", withHeader(1,
                curse("weird", "type:cloak\nflags:NOTAFLAG\ndesc:test\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown object flag")),
                result.errors()::toString);
    }

    @Test
    void unknownElementFlagIsReportedAndSkipped() throws IOException {
        ParseResult<Curse> result = load("bad-eflag.txt", withHeader(1,
                curse("weird", "type:cloak\nflags:HATES_NOTREAL\ndesc:test\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown element flag")),
                result.errors()::toString);
    }

    // ---- Soft error: kept record -----------------------------------------

    @Test
    void unresolvableConflictIsReportedButTheRecordStillLoads() throws IOException {
        // An unresolvable conflict name does NOT drop the record: it logs a soft error and the
        // resolved conflict list simply omits it.
        ParseResult<Curse> result = load("bad-conflict.txt", withHeader(1,
                curse("lonely", "type:cloak\nconflict:no such curse\ndesc:test\n")));

        assertEquals(1, result.items().size());
        assertTrue(result.items().get(0).getConflict().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("Invalid curse conflict name") && e.contains("no such curse")),
                result.errors()::toString);
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

    // ---- Two soft errors at once -----------------------------------------

    @Test
    void twoDifferentSoftErrorsAreBothReportedAndTheValidRecordStillLoads() throws IOException {
        String body = curse("good", "type:cloak\ndesc:fine\n")
                + curse("badmod", "type:cloak\nvalues:NOTAMOD[1]\ndesc:bad\n")
                + curse("badflag", "type:cloak\nflags:NOTAFLAG\ndesc:bad\n");
        ParseResult<Curse> result = load("two-errors.txt", withHeader(3, body));

        assertEquals(1, result.items().size());
        assertEquals("good", result.items().get(0).getName());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown modifier")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown object flag")),
                result.errors()::toString);
    }
}
