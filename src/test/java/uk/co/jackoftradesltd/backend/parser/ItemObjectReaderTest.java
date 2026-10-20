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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link ItemObjectReader}: file text -> {@code ItemObjectLexer}/
 * {@code ItemObjectGrammar} -> {@code ItemObjectParseRecord} -> {@code ItemObjectAssembler} ->
 * {@link ObjectKind}.
 *
 * <p>The object-kind assembler resolves the {@code type:} tval against {@code object_base.txt},
 * brands/slays/curses against their registries, and {@code effect:SHAPECHANGE:} subtypes against
 * loaded {@link uk.co.jackoftradesltd.middle.player.PlayerShape}s. Rather than run the whole heavy
 * {@code GameConstants.init()}, {@link #seed()} loads each dependency directly through its reader
 * and injects it into the private static registries by reflection (in dependency order), restoring
 * them afterwards so no global state leaks to other suites.
 *
 * @author Rowan Crowther
 */
class ItemObjectReaderTest {

    private static final String REAL_FILE = "lib/gamedata/object.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";
    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String SHAPE_FILE = "lib/gamedata/shape.txt";

    private static Object savedPains, savedBases, savedSummons, savedSlays,
            savedBrands, savedObjectBases, savedCurses, savedShapes;

    @BeforeAll
    static void seed() throws Exception {
        // Dependency order: pains <- bases <- summons/slays; object bases + summons <- curses.
        savedPains = setStatic("monsterPains", new PainReader().parse(PAIN_FILE));
        savedBases = setStatic("monsterBases", new MonsterBaseReader().parse(BASE_FILE));
        savedSummons = setStatic("summons", new SummonReader().parse(SUMMON_FILE));
        savedSlays = setStatic("slays", new SlayReader().parse(SLAY_FILE));
        savedBrands = setStatic("brands", new BrandReader().parse(BRAND_FILE));
        savedObjectBases = setStatic("objectBases", new ObjectBaseReader().parse(OBJECT_BASE_FILE));
        savedCurses = setStatic("curses", new CurseReader().parse(CURSE_FILE));
        savedShapes = setStatic("playerShapes", new ShapeReader().parse(SHAPE_FILE));
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
        setStatic("slays", savedSlays);
        setStatic("brands", savedBrands);
        setStatic("objectBases", savedObjectBases);
        setStatic("curses", savedCurses);
        setStatic("playerShapes", savedShapes);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * Reads a private {@link ObjectKind} field — it exposes no getter for {@code curses}.
     */
    @SuppressWarnings("unchecked")
    private static <T> T kindField(ObjectKind target, String name) throws Exception {
        Field f = ObjectKind.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    // ---- fixture helpers -------------------------------------------------

    /**
     * A minimal valid record head: a real tval and a valid glyph/colour.
     */
    private static final String HEAD = "type:sword\ngraphics:|:w\n";

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * An object record: a name line followed by whatever body lines the test supplies.
     */
    private static String obj(String name, String body) {
        return "name:" + name + "\n" + body;
    }

    @TempDir
    Path tempDir;

    private ParseResult<ObjectKind> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new ItemObjectReader().parseWithResults(file.toString());
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllKindsWithNoErrors() throws IOException {
        ParseResult<ObjectKind> result = new ItemObjectReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(375, result.items().size());
    }

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void unknownTypeIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-type.txt", withHeader(1,
                obj("weird", "type:no such tval\ngraphics:|:w\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("unknown tVal") && e.contains("no such tval")),
                result.errors()::toString);
    }

    @Test
    void unknownFlagIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-flag.txt", withHeader(1,
                obj("weird", HEAD + "flags:NOTAFLAG\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag")),
                result.errors()::toString);
    }

    @Test
    void unknownModifierIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-mod.txt", withHeader(1,
                obj("weird", HEAD + "values:NOTAMOD[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown value key")),
                result.errors()::toString);
    }

    @Test
    void unknownResistanceElementIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-res.txt", withHeader(1,
                obj("weird", HEAD + "values:RES_NOTREAL[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown value name")),
                result.errors()::toString);
    }

    @Test
    void unknownBrandIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-brand.txt", withHeader(1,
                obj("weird", HEAD + "brand:NOTABRAND\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown brand")),
                result.errors()::toString);
    }

    @Test
    void unknownSlayIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-slay.txt", withHeader(1,
                obj("weird", HEAD + "slay:NOTASLAY\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown slay")),
                result.errors()::toString);
    }

    @Test
    void unknownCurseIsReportedAndSkipped() throws IOException {
        ParseResult<ObjectKind> result = load("bad-curse.txt", withHeader(1,
                obj("weird", HEAD + "curse:no such curse:10\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown curse")),
                result.errors()::toString);
    }

    /**
     * The curse leg's happy path. C keeps {@code int *curses} on a kind — indexed by curse, value =
     * power, 0 meaning absent ({@code obj-init.c}, {@code parse_object_curse}); the port keys by {@link Curse} and carries the
     * power in the entry's {@link CurseData}, whose timeout stays 0 because timeout is a property of
     * a live {@code struct object}, not of a data-file record.
     *
     * <p>Until this existed the only curse coverage was the unknown-curse error path, so the map's
     * shape was untested — {@link #powerZeroCurseIsNotRecorded()} covers the other half.
     */
    @Test
    void curseResolvesToAnEntryKeyedByCurseCarryingItsPower() throws Exception {
        ParseResult<ObjectKind> result = load("cursed.txt", withHeader(1,
                obj("cursed blade", HEAD + "curse:teleportation:100\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");

        assertEquals(1, curses.size());
        Curse curse = curses.keySet().iterator().next();
        assertEquals("teleportation", curse.getName());
        assertEquals(100, curses.get(curse).getPower());
        assertEquals(0, curses.get(curse).getTimeout(), "timeout is runtime-only");
    }

    /**
     * C: {@code /* Only add if it has power. *}{@code /  if (power > 0)} ({@code obj-init.c},
     * {@code parse_object_curse}). A
     * zero-power curse line is not an error — the curse is simply not recorded on the kind.
     */
    @Test
    void powerZeroCurseIsNotRecorded() throws Exception {
        ParseResult<ObjectKind> result = load("zero-curse.txt", withHeader(1,
                obj("uncursed blade", HEAD + "curse:teleportation:0\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size(), "a zero-power curse is not an error; the kind loads");
        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");
        assertTrue(curses.isEmpty(), () -> "power 0 must not be recorded, got: " + curses);
    }

    // ---- time: belongs to the kind, not to an effect -----------------------

    /**
     * Asserts that two dice agree on every component.
     *
     * @param expected the dice the data file's {@code time:} line spells
     * @param actual   the dice the kind holds
     */
    private static void assertSameDice(Random expected, Random actual) {
        assertNotNull(actual, "a kind's time is never null");
        assertEquals(expected.getBase(), actual.getBase(), "time base");
        assertEquals(expected.getDice(), actual.getDice(), "time dice");
        assertEquals(expected.getSides(), actual.getSides(), "time sides");
        assertEquals(expected.getMBonus(), actual.getMBonus(), "time m-bonus");
    }

    /**
     * C's {@code parse_object_time} writes {@code kind->time}; {@code struct effect} has no time.
     * A {@code time:} line after the kind's effect must therefore land on the {@link ObjectKind}.
     */
    @Test
    void timeAfterTheEffectReachesTheKind() throws IOException {
        ParseResult<ObjectKind> result = load("timed.txt", withHeader(1,
                obj("rod of waking", HEAD + "effect:WAKE\ntime:100+1d50\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertSameDice(Random.parseStr("100+1d50"), result.items().get(0).getTime());
    }

    /**
     * With several effects the old design hung the time on the last one only. The kind holds it
     * once, whatever the number of effects.
     */
    @Test
    void timeWithSeveralEffectsReachesTheKindOnce() throws IOException {
        ParseResult<ObjectKind> result = load("timed-two.txt", withHeader(1,
                obj("rod of two things", HEAD + "effect:WAKE\neffect:ACQUIRE\ntime:1d500\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ObjectKind kind = result.items().get(0);
        assertEquals(2, kind.getEffect().size());
        assertSameDice(Random.parseStr("1d500"), kind.getTime());
    }

    /**
     * {@code time:} is a record-level line, so its position among the record's other lines does not
     * matter; before the effect it still reaches the kind.
     */
    @Test
    void timeBeforeTheEffectReachesTheKind() throws IOException {
        ParseResult<ObjectKind> result = load("timed-first.txt", withHeader(1,
                obj("rod of early time", HEAD + "time:d100\neffect:WAKE\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertSameDice(Random.parseStr("d100"), result.items().get(0).getTime());
    }

    /**
     * A kind with no {@code time:} line holds a zero {@link Random}, as C's zeroed
     * {@code random_value}, and never {@code null}.
     */
    @Test
    void absentTimeIsZeroNotNull() throws IOException {
        ParseResult<ObjectKind> result = load("untimed.txt", withHeader(1,
                obj("plain blade", HEAD + "effect:WAKE\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertSameDice(Random.Zero(), result.items().get(0).getTime());
    }

    /**
     * Every kind in {@code object.txt} holds exactly the dice its own {@code time:} line spells, and
     * a zero {@link Random} where it has none. The expected values come from the data file's text,
     * read record by record, not from the reader under test.
     */
    @Test
    void everyKindInTheRealFileHoldsItsOwnTime() throws IOException {
        List<String> expected = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("name:")) expected.add("");
            else if (line.startsWith("time:")) expected.set(expected.size() - 1, line.substring(5).trim());
        }

        ParseResult<ObjectKind> result = new ItemObjectReader().parseWithResults(REAL_FILE);

        assertEquals(expected.size(), result.items().size(), "one kind per name: line");
        int timed = 0;
        for (int i = 0; i < expected.size(); i++) {
            ObjectKind kind = result.items().get(i);
            Random want = expected.get(i).isEmpty() ? Random.Zero() : Random.parseStr(expected.get(i));
            if (!expected.get(i).isEmpty()) timed++;
            assertSameDice(want, kind.getTime());
        }
        assertEquals(43, timed, "object.txt carries 43 time: lines");
    }

    @Test
    void twoDifferentSoftErrorsAreBothReportedAndTheValidKindStillLoads() throws IOException {
        String body = obj("good", HEAD)
                + obj("badflag", HEAD + "flags:NOTAFLAG\n")
                + obj("badmod", HEAD + "values:NOTAMOD[1]\n");
        ParseResult<ObjectKind> result = load("two-errors.txt", withHeader(3, body));

        assertEquals(1, result.items().size());
        assertEquals("good", result.items().get(0).getName());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown value key")),
                result.errors()::toString);
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordCountMismatchIsReportedButValidKindStillLoads() throws IOException {
        ParseResult<ObjectKind> result = load("bad-count.txt", withHeader(5, obj("plain", HEAD)));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<ObjectKind> result = load("no-header.txt", obj("plain", HEAD));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        ParseResult<ObjectKind> result = load("no-name.txt", withHeader(1, HEAD));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- kind flags and IGNORE_ element flags (obj-init.c: parse_object_flags) --------------

    /**
     * C's {@code grab_flag} against {@code kind_flags[]}: a {@code KF_} token on the kind's
     * {@code flags:} line lands in the kind's kind flags, and the base's kind flags are unioned in
     * ({@code finish_parse_object}). {@code sword} in {@code object_base.txt} carries
     * {@code SHOW_DICE}.
     */
    @Test
    void kindFlagTokenReachesKindFlagsAndBaseFlagsAreUnioned() throws IOException {
        ParseResult<ObjectKind> result = load("kf.txt", withHeader(1,
                obj("kf", HEAD + "flags:EASY_KNOW\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_SHOW_DICE));
        assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD));
    }

    /**
     * C's {@code grab_element_flag}: {@code IGNORE_FIRE} sets {@code EL_INFO_IGNORE} on the
     * <em>fire</em> slot of the kind's own {@code el_info}, and on no other element.
     */
    @Test
    void ignoreTokenSetsIgnoreOnThatElementOfTheKindOnly() throws IOException {
        ParseResult<ObjectKind> result = load("ign.txt", withHeader(1,
                obj("ign", HEAD + "flags:IGNORE_FIRE\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_COLD).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    /**
     * Several {@code IGNORE_} tokens, in one {@code flags:} line with a kind flag between them,
     * all take effect; nothing is lost to the kind flag, and none becomes a kind flag.
     */
    @Test
    void severalIgnoreTokensAndAKindFlagShareOneLine() throws IOException {
        ParseResult<ObjectKind> result = load("ign2.txt", withHeader(1,
                obj("ign2", HEAD + "flags:IGNORE_ACID | EASY_KNOW | IGNORE_FIRE\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_ELEC).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
    }

    /**
     * In C the ignore goes on the kind ({@code k->el_info}), never on the base ({@code kb_info}),
     * so one kind's {@code IGNORE_FIRE} must not make its siblings fire-proof.
     */
    @Test
    void ignoreOnOneKindDoesNotReachItsBaseOrItsSiblings() throws IOException {
        ParseResult<ObjectKind> result = load("sib.txt", withHeader(2,
                obj("fireproof", HEAD + "flags:IGNORE_FIRE\n")
                        + obj("plain", HEAD)));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind fireproof = result.items().get(0);
        ObjectKind plain = result.items().get(1);
        assertFalse(plain.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(fireproof.getBase().getElementMap().containsKey(ElementEnum.ELEM_FIRE)
                        && fireproof.getBase().getElementMap().get(ElementEnum.ELEM_FIRE)
                        .has(ElementInfoEnum.EL_INFO_IGNORE),
                "the shared base must not gain the kind's ignore");
    }

    /**
     * C requires the {@code IGNORE_} (or {@code HATES_}) prefix: a bare element name is not a flag
     * and is {@code PARSE_ERROR_INVALID_FLAG}. The kind is dropped with an "unknown flag" error.
     */
    @Test
    void bareElementNameIsNotAFlag() throws IOException {
        ParseResult<ObjectKind> result = load("bare.txt", withHeader(1,
                obj("bare", HEAD + "flags:FIRE\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag")),
                result.errors()::toString);
    }

    /**
     * A token shorter than {@code IGNORE_} is an unknown flag, reported softly like any other. It
     * must not escape as a runtime exception that aborts the whole load.
     */
    @Test
    void shortUnknownTokenIsASoftError() throws IOException {
        ParseResult<ObjectKind> result = load("short.txt", withHeader(1,
                obj("short", HEAD + "flags:FOO\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag")),
                result.errors()::toString);
    }

    /**
     * A seven-or-more-character token that merely ends in an element name is not an ignore flag.
     * C compares the whole prefix, so {@code NOTIGNORE_FIRE} fails.
     */
    @Test
    void tokenWithWrongPrefixButAnElementSuffixIsNotAnIgnoreFlag() throws IOException {
        ParseResult<ObjectKind> result = load("prefix.txt", withHeader(1,
                obj("prefix", HEAD + "flags:XXXXXXXFIRE\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag")),
                result.errors()::toString);
    }

    /**
     * The real file, against {@code object.txt} as shipped: the Lantern carries only
     * {@code IGNORE_FIRE}; the Mithril Arrow carries {@code IGNORE_ACID | IGNORE_FIRE}.
     */
    @Test
    void realFileLanternAndMithrilArrowIgnoreExactlyTheirElements() throws IOException {
        ParseResult<ObjectKind> result = new ItemObjectReader().parseWithResults(REAL_FILE);

        ObjectKind lantern = result.items().stream()
                .filter(k -> "Lantern".equals(k.getsValueName())).findFirst().orElseThrow();
        assertTrue(lantern.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(lantern.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(lantern.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));

        ObjectKind arrow = result.items().stream()
                .filter(k -> "Mithril Arrow".equals(k.getsValueName())).findFirst().orElseThrow();
        assertTrue(arrow.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(arrow.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(arrow.getElInfo(ElementEnum.ELEM_ELEC).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    // ---- the real file against a line-by-line reading of object.txt -------

    /**
     * Reads {@link #REAL_FILE} straight off its lines, with no lexer or grammar involved: one map per
     * {@code name:} block, each directive name to the values given for it, in order. It is the
     * independent witness for what {@code finish_parse_object} is handed in C, so the assertions
     * below compare the port against the file and not against itself.
     */
    private static List<Map<String, List<String>>> scanRealFile() throws IOException {
        List<Map<String, List<String>>> records = new ArrayList<>();
        Map<String, List<String>> current = null;
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.isBlank() || line.startsWith("#") || line.startsWith("record-count:")) continue;
            int colon = line.indexOf(':');
            String key = line.substring(0, colon);
            if (key.equals("name")) {
                current = new LinkedHashMap<>();
                records.add(current);
            }
            current.computeIfAbsent(key, k -> new ArrayList<>()).add(line.substring(colon + 1));
        }
        return records;
    }

    /**
     * The value of the last line a record gives for {@code key}, which is the one C is left holding
     * when the handler assigns.
     */
    private static String last(Map<String, List<String>> record, String key) {
        List<String> values = record.get(key);
        return values.get(values.size() - 1);
    }

    /**
     * A scalar directive's value in C's terms: a repeat overwrites, so the last line wins, and a
     * directive the record never gave leaves the zeroed record's 0.
     */
    private static int lastInt(Map<String, List<String>> record, String key) {
        List<String> values = record.get(key);
        return values == null ? 0 : Integer.parseInt(values.get(values.size() - 1).trim());
    }

    /**
     * A dice component, as {@code "base+dice d sides m-bonus"}, so one string assertion covers all
     * four fields of a {@code struct random}.
     */
    private static String dice(Random random) {
        return random.getBase() + "+" + random.getDice() + "d" + random.getSides() + "m" + random.getMBonus();
    }

    private static ObjectKind kindNamed(List<ObjectKind> kinds, String name, TValue tval) {
        return kinds.stream()
                .filter(k -> name.equals(k.getName()) && tval == k.gettValue())
                .findFirst().orElseThrow(() -> new AssertionError("no " + tval + " kind named " + name));
    }

    /**
     * {@code finish_parse_object} fills {@code k_info} from the head of a chain built last-first,
     * counting {@code kidx} down from the top, so the first record in the file is {@code kidx} 0.
     * The port's list is in file order, kind for kind.
     */
    @Test
    void realFileKindsComeBackInFileOrder() throws IOException {
        List<Map<String, List<String>>> expected = scanRealFile();
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        assertEquals(375, expected.size(), "the file as shipped holds 375 name: lines");
        assertEquals(expected.size(), kinds.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).get("name").get(0), kinds.get(i).getName(), "kind " + i);
        }
    }

    /**
     * The first four records of {@code object.txt} are the sentinel kinds, all {@code type:none}.
     * The reader loads them like any other record.
     */
    @Test
    void realFileOpensWithTheFourSentinelKinds() throws IOException {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        assertEquals(List.of("<pile>", "<unknown item>", "<unknown treasure>", "<curse object>"),
                kinds.subList(0, 4).stream().map(ObjectKind::getName).collect(Collectors.toList()));
        for (ObjectKind sentinel : kinds.subList(0, 4)) {
            assertEquals(TValue.TV_NONE, sentinel.gettValue(), sentinel.getName());
        }
        assertEquals(TValue.TV_LIGHT, kinds.get(4).gettValue(), "the first ordinary kind is the Wooden Torch");
    }

    /**
     * Counting the {@code type:} lines of the file gives how many kinds each tval must end up
     * with. {@code digger} is {@code TV_DIGGING} and {@code dragon armor} is {@code TV_DRAG_ARMOR}.
     */
    @Test
    void realFileHoldsTheNumberOfKindsPerTvalThatItsTypeLinesGive() throws IOException {
        Map<String, Integer> expected = new TreeMap<>(Map.ofEntries(
                Map.entry("TV_POTION", 41), Map.entry("TV_SCROLL", 39), Map.entry("TV_RING", 30),
                Map.entry("TV_WAND", 30), Map.entry("TV_ROD", 25), Map.entry("TV_STAFF", 24),
                Map.entry("TV_AMULET", 17), Map.entry("TV_POLEARM", 16), Map.entry("TV_FOOD", 14),
                Map.entry("TV_SWORD", 14), Map.entry("TV_HAFTED", 14), Map.entry("TV_MUSHROOM", 12),
                Map.entry("TV_HARD_ARMOR", 12), Map.entry("TV_DRAG_ARMOR", 12), Map.entry("TV_GOLD", 11),
                Map.entry("TV_BOOTS", 6), Map.entry("TV_SHIELD", 6), Map.entry("TV_CHEST", 6),
                Map.entry("TV_BOW", 5), Map.entry("TV_SOFT_ARMOR", 5), Map.entry("TV_GLOVES", 5),
                Map.entry("TV_NONE", 4), Map.entry("TV_HELM", 4), Map.entry("TV_CROWN", 4),
                Map.entry("TV_CLOAK", 4), Map.entry("TV_ARROW", 3), Map.entry("TV_BOLT", 3),
                Map.entry("TV_SHOT", 3), Map.entry("TV_DIGGING", 3), Map.entry("TV_LIGHT", 2),
                Map.entry("TV_FLASK", 1)));

        Map<String, Integer> actual = new TreeMap<>();
        for (ObjectKind kind : new ItemObjectReader().parse(REAL_FILE)) {
            actual.merge(kind.gettValue().name(), 1, Integer::sum);
        }

        assertEquals(expected, actual);
    }

    /**
     * Every scalar the file gives, for every one of the 375 kinds, against a plain reading of the
     * lines: the numbers by C's last-one-wins rule, {@code attack:}'s dice and sides, the alloc
     * triple, and the {@code msg:}, {@code vis-msg:} and {@code desc:} texts joined with no
     * separator as {@code string_append} joins them.
     */
    @Test
    void realFileScalarsAndTextsMatchALineByLineReadingOfTheFile() throws Exception {
        List<Map<String, List<String>>> expected = scanRealFile();
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        for (int i = 0; i < expected.size(); i++) {
            Map<String, List<String>> record = expected.get(i);
            ObjectKind kind = kinds.get(i);
            String at = kind.getName() + " (kind " + i + ")";

            assertEquals(lastInt(record, "level"), (int) kindField(kind, "level"), "level of " + at);
            assertEquals(lastInt(record, "weight"), kind.getWeight(), "weight of " + at);
            assertEquals(lastInt(record, "cost"), kind.getCost(), "cost of " + at);
            assertEquals(lastInt(record, "power"), kind.getPower(), "power of " + at);

            int common = 0, lo = 0, hi = 0;
            if (record.containsKey("alloc")) {
                String[] alloc = last(record, "alloc").trim().split(":");
                String[] range = alloc[1].trim().split(" to ");
                common = Integer.parseInt(alloc[0]);
                lo = Integer.parseInt(range[0].trim());
                hi = Integer.parseInt(range[1].trim());
            }
            assertEquals(common, (int) kindField(kind, "alloc_prob"), "alloc commonness of " + at);
            assertEquals(lo, (int) kindField(kind, "alloc_min"), "alloc min of " + at);
            assertEquals(hi, (int) kindField(kind, "alloc_max"), "alloc max of " + at);

            int dd = 0, ds = 0, ac = 0;
            if (record.containsKey("attack")) {
                String[] hd = last(record, "attack").split(":")[0].split("d");
                dd = Integer.parseInt(hd[0]);
                ds = Integer.parseInt(hd[1]);
            }
            if (record.containsKey("armor")) {
                ac = Integer.parseInt(last(record, "armor").split(":")[0]);
            }
            assertEquals(dd, kind.getDamageDice(), "dice of " + at);
            assertEquals(ds, kind.getDamageSides(), "sides of " + at);
            assertEquals(ac, kind.getAc(), "base ac of " + at);

            int pile = record.containsKey("pile") ? Integer.parseInt(last(record, "pile").split(":")[0]) : 0;
            assertEquals(pile, (int) kindField(kind, "genMultProb"), "pile chance of " + at);

            assertEquals(String.join("", record.getOrDefault("desc", List.of())),
                    kindField(kind, "text"), "desc of " + at);
            assertEquals(String.join("", record.getOrDefault("msg", List.of())),
                    kindField(kind, "effectMessage"), "msg of " + at);
            assertEquals(String.join("", record.getOrDefault("vis-msg", List.of())),
                    kindField(kind, "visMessage"), "vis-msg of " + at);
            assertEquals(record.getOrDefault("effect", List.of()).size(), kind.getEffect().size(),
                    "effect count of " + at);
        }
    }

    /**
     * Svals and kind indexes are given out when {@code ObjectDataLoader} registers the kinds
     * ({@code ObjectRegistry.addObjectKind}), the port's half of what {@code parse_object_type} and
     * {@code finish_parse_object} do. A kind that has only been through the reader is still 0 and 0.
     */
    @Test
    void realFileKindsLeaveTheReaderWithSvalAndIndexUnset() throws IOException {
        for (ObjectKind kind : new ItemObjectReader().parse(REAL_FILE)) {
            assertEquals(0, kind.getsVal(), kind.getName());
            assertEquals(0, kind.getKindIndex(), kind.getName());
        }
    }

    // ---- the real file: spot records worked out from the file --------------

    /**
     * {@code & Wooden Torch~}: {@code BURNS_OUT | LIGHT_2} are object flags, {@code EASY_KNOW} is a
     * kind flag, {@code attack:1d1:0:0} gives 1d1 with to-hit and to-damage zero, and
     * {@code graphics:~:u} is umber.
     */
    @Test
    void realFileWoodenTorchCarriesEveryFieldTheFileGives() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind torch = kindNamed(kinds, "& Wooden Torch~", TValue.TV_LIGHT);

        assertEquals(1, (int) kindField(torch, "level"));
        assertEquals(22, torch.getWeight());
        assertEquals(2, torch.getCost());
        assertEquals(70, (int) kindField(torch, "alloc_prob"));
        assertEquals(1, (int) kindField(torch, "alloc_min"));
        assertEquals(100, (int) kindField(torch, "alloc_max"));
        assertEquals(1, torch.getDamageDice());
        assertEquals(1, torch.getDamageSides());
        assertEquals("0+0d0m0", dice(torch.getToH()));
        assertEquals("0+0d0m0", dice(torch.getToD()));
        assertEquals('~', torch.getCharacter().getCharacter());
        assertEquals(ColourEnum.COLOUR_UMBER, torch.getCharacter().getAttributeColour());
        assertTrue(torch.getFlags().has(ObjectFlag.OF_BURNS_OUT));
        assertTrue(torch.getFlags().has(ObjectFlag.OF_LIGHT_2));
        assertFalse(torch.getFlags().has(ObjectFlag.OF_TAKES_FUEL));
        assertTrue(torch.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
    }

    /**
     * {@code & Flask~ of oil}: {@code pval:7500} is a bare number, and its four {@code desc:} lines
     * run together exactly as written, the double space after "light." included.
     */
    @Test
    void realFileFlaskOfOilHasItsPvalAndItsFourDescLinesJoinedWithNoSeparator() throws Exception {
        ObjectKind flask = kindNamed(new ItemObjectReader().parse(REAL_FILE), "& Flask~ of oil", TValue.TV_FLASK);

        assertEquals("7500+0d0m0", dice(flask.getPVal()));
        assertEquals("It can be used to fuel a lantern with 7500 turns of light, up to a maximum of 15000 "
                + "turns of light.  When thrown, it is considered lit, so it does more than nominal damage "
                + "(which occurs even against creatures resistant to fire).", kindField(flask, "text"));
    }

    /**
     * {@code Flames}: {@code armor:0:5+d5M10} (a {@code d} with no number before it is one die),
     * {@code IGNORE_FIRE} and {@code RES_FIRE[1]} landing on the same element, {@code time:50+d50}
     * on the kind, and two effects.
     */
    @Test
    void realFileRingOfFlamesCombinesIgnoreResistanceTimeAndToArmour() throws Exception {
        ObjectKind flames = kindNamed(new ItemObjectReader().parse(REAL_FILE), "Flames", TValue.TV_RING);

        assertEquals("5+1d5m10", dice(flames.getToA()));
        assertEquals(1, flames.getElInfo(ElementEnum.ELEM_FIRE).getResLevel());
        assertTrue(flames.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertEquals(0, flames.getElInfo(ElementEnum.ELEM_COLD).getResLevel());
        assertEquals("50+1d50m0", dice(flames.getTime()));
        assertEquals(11, flames.getPower());
        assertEquals(2, flames.getEffect().size());
    }

    /**
     * {@code Trickery} gives its {@code values:} over two lines: the first holds object modifiers,
     * the second {@code RES_} elements. Both lines count, and a modifier's dice keep their base,
     * dice, sides and bonus apart ({@code 1+d3M3} is base 1, 1d3, bonus 3).
     */
    @Test
    void realFileAmuletOfTrickerySplitsItsValuesBetweenModifiersAndResistances() throws Exception {
        ObjectKind trickery = kindNamed(new ItemObjectReader().parse(REAL_FILE), "Trickery", TValue.TV_AMULET);

        assertEquals("1+0d0m4", dice(trickery.getModifier(ObjectModifier.OM_DEX)));
        assertEquals("1+0d0m2", dice(trickery.getModifier(ObjectModifier.OM_SPEED)));
        assertEquals("1+0d0m2", dice(trickery.getModifier(ObjectModifier.OM_STEALTH)));
        assertEquals("1+1d3m3", dice(trickery.getModifier(ObjectModifier.OM_SEARCH)));
        assertEquals("1+1d3m3", dice(trickery.getModifier(ObjectModifier.OM_INFRA)));
        assertEquals(1, trickery.getElInfo(ElementEnum.ELEM_NEXUS).getResLevel());
        assertEquals(1, trickery.getElInfo(ElementEnum.ELEM_POIS).getResLevel());
        assertEquals(0, trickery.getElInfo(ElementEnum.ELEM_FIRE).getResLevel());
    }

    /**
     * {@code Reckless Attacks} has {@code armor:0:-8+4d3}: a leading minus negates the whole roll, and
     * {@code parse_random} keeps the random parts positive, so it lowers the base by the most the dice
     * can add (4 x (3 + 1) = 16) as well as negating it, giving base -24 with 4d3 on top. {@code the
     * Magi} has {@code armor:0:d5M5} (no base, one die, a bonus).
     */
    @Test
    void realFileToArmourDiceKeepTheirBaseDiceSidesAndBonusApart() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        assertEquals("-24+4d3m0", dice(kindNamed(kinds, "Reckless Attacks", TValue.TV_RING).getToA()));
        assertEquals("0+1d5m5", dice(kindNamed(kinds, "the Magi", TValue.TV_AMULET).getToA()));
    }

    /**
     * The wand of Light has {@code charges:6+d10} and a {@code vis-msg:} but no {@code msg:}; the
     * ring of Light shares its name and carries neither. Kinds are told apart by tval, not name.
     */
    @Test
    void realFileWandOfLightHasChargesAndAVisMsgAndSharesItsNameWithARing() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind wand = kindNamed(kinds, "Light", TValue.TV_WAND);
        ObjectKind ring = kindNamed(kinds, "Light", TValue.TV_RING);

        assertNotSame(wand, ring);
        assertEquals("6+1d10m0", dice(wand.getCharge()));
        assertEquals("A line of shimmering blue light appears.", kindField(wand, "visMessage"));
        assertEquals("", kindField(wand, "effectMessage"));
        assertEquals("", kindField(ring, "visMessage"));
        assertEquals("0+0d0m0", dice(ring.getCharge()));
    }

    /**
     * {@code Cure Light Wounds} is both a potion and a staff. The potion has {@code pile:90:2d3} and
     * five effects; the staff has {@code charges:6+d5}. C allows the repeated name, so both load.
     */
    @Test
    void realFileCureLightWoundsIsBothAPotionAndAStaff() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind potion = kindNamed(kinds, "Cure Light Wounds", TValue.TV_POTION);
        ObjectKind staff = kindNamed(kinds, "Cure Light Wounds", TValue.TV_STAFF);

        assertEquals(90, (int) kindField(potion, "genMultProb"));
        assertEquals("0+2d3m0", dice(kindField(potion, "stackSize")));
        assertEquals(5, potion.getEffect().size());
        assertEquals("6+1d5m0", dice(staff.getCharge()));
    }

    /**
     * {@code Dragon Breath} is the one record in the file that repeats a scalar directive: it has
     * {@code pile:100:1d2} and, after its {@code alloc:}, {@code pile:70:1d3}. The handler assigns,
     * so C is left holding the second.
     */
    @Test
    void realFileDragonBreathRepeatsPileAndTheLastLineWins() throws Exception {
        ObjectKind breath = kindNamed(new ItemObjectReader().parse(REAL_FILE), "Dragon Breath", TValue.TV_POTION);

        assertEquals(70, (int) kindField(breath, "genMultProb"));
        assertEquals("0+1d3m0", dice(kindField(breath, "stackSize")));
    }

    /**
     * The shipped file's only {@code slay:} line is on the Mace of Disruption, and it has no
     * {@code brand:} lines at all.
     */
    @Test
    void realFileMaceOfDisruptionCarriesTheOnlySlayAndNoKindCarriesABrand() throws IOException {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind mace = kindNamed(kinds, "& Mace~ of Disruption", TValue.TV_HAFTED);

        assertEquals(Set.of("UNDEAD_3"), mace.getSlays().stream().map(Slay::getCode).collect(Collectors.toSet()));
        assertEquals(1, kinds.stream().filter(k -> !k.getSlays().isEmpty()).count());
        assertEquals(0, kinds.stream().filter(k -> !k.getBrands().isEmpty()).count());
    }

    /**
     * The only two {@code curse:} lines in the file are {@code curse:teleportation:100}, on the ring
     * and the amulet that are both called Teleportation.
     */
    @Test
    void realFileTeleportationRingAndAmuletEachCarryTheTeleportationCurseAtOneHundred() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        for (TValue tval : List.of(TValue.TV_RING, TValue.TV_AMULET)) {
            Map<Curse, CurseData> curses = kindField(kindNamed(kinds, "Teleportation", tval), "curses");
            assertEquals(1, curses.size(), tval.name());
            assertEquals("teleportation", curses.keySet().iterator().next().getName());
            assertEquals(100, curses.values().iterator().next().getPower());
        }
        assertEquals(2, kinds.stream().filter(k -> !k.getCurses().isEmpty()).count());
    }

    /**
     * {@code & Elven Cloak~}'s two {@code desc:} lines run together: the first ends "wondrously" and
     * the second begins with the space that keeps the words apart.
     */
    @Test
    void realFileElvenCloakDescriptionIsItsTwoLinesJoinedWithNoSeparator() throws Exception {
        ObjectKind cloak = kindNamed(new ItemObjectReader().parse(REAL_FILE), "& Elven Cloak~", TValue.TV_CLOAK);

        assertEquals("A mantle made of curious silken material by the Galadrim that wondrously takes on "
                + "the hues and shapes of its surroundings.", kindField(cloak, "text"));
    }

    // ---- a missing file, and the directives C cannot read either -----------

    /**
     * {@code run_parse_object} calls {@code parse_file_quit_not_found}, which ends the program. The
     * port surfaces the same failure as an {@link IOException} for the caller to treat as fatal.
     */
    @Test
    void missingFileIsAnIOException() {
        String missing = tempDir.resolve("no-such-object.txt").toString();

        assertThrows(IOException.class, () -> new ItemObjectReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new ItemObjectReader().parse(missing));
    }

    /**
     * An {@code attack:} takes {@code rand hd rand to-h rand to-d} and C registers all three as
     * required, so two of the three is a parser error. Here it is a syntax error: no items.
     */
    @Test
    void attackWithOnlyTwoOfItsThreeFieldsIsAHardError() throws IOException {
        ParseResult<ObjectKind> result = load("short-attack.txt", withHeader(1,
                obj("short", HEAD + "attack:1d4:0\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * {@code init_parse_object} registers no {@code foo} directive, so C reports an undefined
     * directive. Here it is a syntax error: no items.
     */
    @Test
    void unknownDirectiveIsAHardError() throws IOException {
        ParseResult<ObjectKind> result = load("unknown-directive.txt", withHeader(1,
                obj("odd", HEAD + "foo:bar\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * {@code parse_object_alloc} hands the range to {@code grab_int_range} with the separator
     * {@code "to"}, which fails when it is missing. So does the grammar.
     */
    @Test
    void allocWithoutToIsAHardError() throws IOException {
        ParseResult<ObjectKind> result = load("alloc-no-to.txt", withHeader(1,
                obj("no to", HEAD + "alloc:5:10\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- repeated and accumulating directives (obj-init.c: parse_object_*) -----

    /**
     * Every scalar handler assigns, so a repeated {@code level:} keeps the last value.
     */
    @Test
    void aRepeatedScalarDirectiveKeepsTheLastValue() throws Exception {
        ParseResult<ObjectKind> result = load("repeat-level.txt", withHeader(1,
                obj("again", HEAD + "level:3\nlevel:9\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(9, (int) kindField(result.items().get(0), "level"));
    }

    /**
     * {@code msg:}, {@code vis-msg:} and {@code desc:} each call {@code string_append}, which adds
     * the new text to the end of the old with nothing between: any space has to be in the data.
     */
    @Test
    void msgVisMsgAndDescAppendWithNoSeparator() throws Exception {
        ParseResult<ObjectKind> result = load("append.txt", withHeader(1,
                obj("joined", HEAD + "msg:one\nmsg: two\nvis-msg:three\nvis-msg:four\ndesc:five\ndesc: six\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertEquals("one two", kindField(kind, "effectMessage"));
        assertEquals("threefour", kindField(kind, "visMessage"));
        assertEquals("five six", kindField(kind, "text"));
    }

    /**
     * {@code parse_object_flags} sets bits and never clears them, so flags from two lines are both
     * kept; {@code slay:} and {@code brand:} each set another entry in the kind's array.
     */
    @Test
    void flagsSlaysAndBrandsAccumulateAcrossLines() throws Exception {
        ParseResult<ObjectKind> result = load("accumulate.txt", withHeader(1,
                obj("more", HEAD + "flags:EASY_KNOW\nflags:GOOD | SHOW_MULT\n"
                        + "slay:EVIL_2\nslay:ANIMAL_2\nbrand:FIRE_2\nbrand:COLD_2\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_SHOW_MULT));
        assertEquals(Set.of("EVIL_2", "ANIMAL_2"),
                kind.getSlays().stream().map(Slay::getCode).collect(Collectors.toSet()));
        assertEquals(Set.of("FIRE_2", "COLD_2"),
                kind.getBrands().stream().map(Brand::getCode).collect(Collectors.toSet()));
    }

    /**
     * {@code values:} lines add to the same arrays, so a second line extends the first.
     */
    @Test
    void valuesOnTwoLinesAreBothKept() throws Exception {
        ParseResult<ObjectKind> result = load("two-values.txt", withHeader(1,
                obj("both", HEAD + "values:STR[2]\nvalues:RES_FIRE[1] | DEX[3]\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertEquals("2+0d0m0", dice(kind.getModifier(ObjectModifier.OM_STR)));
        assertEquals("3+0d0m0", dice(kind.getModifier(ObjectModifier.OM_DEX)));
        assertEquals(1, kind.getElInfo(ElementEnum.ELEM_FIRE).getResLevel());
    }

    /**
     * C never checks a name against the ones before it: every {@code name:} makes a new kind.
     */
    @Test
    void twoKindsWithTheSameNameBothLoadInFileOrder() throws IOException {
        ParseResult<ObjectKind> result = load("dup.txt", withHeader(3,
                obj("same", HEAD + "level:1\n") + obj("other", HEAD) + obj("same", HEAD + "level:2\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(List.of("same", "other", "same"),
                result.items().stream().map(ObjectKind::getName).collect(Collectors.toList()));
    }

    // ---- places the grammar or assembler differ from C ----------------------

    /**
     * C leaves every field of a record 0 unless a directive sets it, so a kind with no {@code attack:},
     * {@code armor:}, {@code charges:}, {@code pile:}, {@code pval:} or {@code time:} line holds a zero
     * {@code struct random} for each, and the port does the same. {@code ObjectUtils.objectPrep} calls
     * {@code randCalc} on {@code getToH()}, {@code getToD()} and {@code getToA()} unconditionally, and on
     * {@code getPVal()} for a potion, food, flask or launcher, so a plain kind such as a ration needs
     * them non-null.
     */
    @Test
    void absentDiceAreAZeroRandomAsInC() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind pile = kindNamed(kinds, "<pile>", TValue.TV_NONE);

        assertEquals("0+0d0m0", dice(pile.getToH()));
        assertEquals("0+0d0m0", dice(pile.getToD()));
        assertEquals("0+0d0m0", dice(pile.getToA()));
        assertEquals("0+0d0m0", dice(pile.getCharge()));
        assertEquals("0+0d0m0", dice(kindField(pile, "stackSize")));
        assertEquals("0+0d0m0", dice(pile.getPVal()));
        assertEquals("0+0d0m0", dice(pile.getTime()));
        assertEquals("0+0d0m0", dice(kindField(pile, "baseDamage")));
    }

    /**
     * {@code parse_object_name} needs nothing after it, so C accepts a record of a name alone. The
     * grammar wants at least one directive, so it is a syntax error here: no items.
     */
    @Test
    void aRecordWithOnlyANameLineIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<ObjectKind> result = load("name-only.txt", withHeader(1, "name:bare\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * {@code grab_int_range} insists on white space after the first number, so C rejects
     * {@code 1to100}. The lexer's {@code ALLOC_TO} makes the spaces optional.
     */
    @Test
    void allocWithNoSpacesAroundToIsAcceptedHereWhereCRejectsIt() throws Exception {
        ParseResult<ObjectKind> result = load("alloc-tight.txt", withHeader(1,
                obj("tight", HEAD + "alloc:5:1to100\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertEquals(1, (int) kindField(kind, "alloc_min"));
        assertEquals(100, (int) kindField(kind, "alloc_max"));
    }

    /**
     * {@code parse_object_curse} stores a power only when it is above 0 and never clears one, so
     * {@code curse:teleportation:50} followed by {@code curse:teleportation:0} leaves the 50 in C.
     * The grammar keeps the last line per curse name, so the zero replaces the 50 and the assembler
     * then drops it.
     */
    @Test
    void aLaterZeroPowerCurseDropsAnEarlierOneHereWhereCKeepsIt() throws Exception {
        ParseResult<ObjectKind> result = load("curse-zero-after.txt", withHeader(1,
                obj("cursed", HEAD + "curse:teleportation:50\ncurse:teleportation:0\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");
        assertTrue(curses.isEmpty(), () -> "C would hold teleportation at 50, got: " + curses);
    }

    /**
     * The reverse order does keep the curse, as it does in C.
     */
    @Test
    void aZeroPowerCurseBeforeANonZeroOneLeavesTheNonZeroOne() throws Exception {
        ParseResult<ObjectKind> result = load("curse-zero-before.txt", withHeader(1,
                obj("cursed", HEAD + "curse:teleportation:0\ncurse:teleportation:50\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");
        assertEquals(1, curses.size());
        assertEquals(50, curses.values().iterator().next().getPower());
    }

    /**
     * {@code grab_element_flag} also accepts {@code HATES_} followed by an element name and sets
     * {@code EL_INFO_HATES}. The assembler knows only {@code IGNORE_}, so a {@code HATES_FIRE} token
     * is an unknown flag here and drops the kind. No record in the shipped file uses it.
     */
    @Test
    void aHatesFlagIsAnUnknownFlagHereWhereCSetsTheHatesBit() throws IOException {
        ParseResult<ObjectKind> result = load("hates.txt", withHeader(1,
                obj("hates", HEAD + "flags:HATES_FIRE\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag: HATES_FIRE")),
                result.errors()::toString);
    }

    /**
     * {@code init_parse_object} registers no {@code effect-msg} directive, so C reports an undefined
     * directive. The shared effect block accepts it here without complaint.
     */
    @Test
    void effectMsgIsAcceptedHereWhereCHasNoSuchDirective() throws IOException {
        ParseResult<ObjectKind> result = load("effect-msg.txt", withHeader(1,
                obj("chatty", HEAD + "effect:HEAL_HP\ndice:5\neffect-msg:hello\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(1, result.items().size());
    }

    /**
     * A record with no {@code type:} keeps C's zero tval, {@code TV_NONE}, and a {@code NULL} base.
     * The port leaves the tval {@code null} as well as the base, and
     * {@code ObjectRegistry.addObjectKind} reads the base unguarded, so registering such a kind
     * would fail. No record in the shipped file lacks a {@code type:}.
     */
    @Test
    void aRecordWithNoTypeHasANullTvalAndBaseWhereCHasTvalZero() throws Exception {
        ParseResult<ObjectKind> result = load("no-type.txt", withHeader(1,
                obj("typeless", "graphics:|:w\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        ObjectKind kind = result.items().get(0);
        assertNull(kind.gettValue());
        assertNull(kind.getBase());
    }

    /**
     * {@code parse_object_graphics} turns a one-character colour with {@code color_char_to_attr} and a
     * longer one with {@code color_text_to_attr}; both answer white for a name they do not know. The
     * colour is a fixed set of lexer tokens here, so {@code 9} and {@code q} (neither is a colour
     * code) are lexer errors, and the whole file loads as nothing.
     */
    @Test
    void anUnknownColourIsAHardErrorHereWhereCFallsBackToWhite() throws IOException {
        for (String colour : List.of("9", "q")) {
            ParseResult<ObjectKind> result = load("bad-colour.txt", withHeader(1,
                    obj("dim", "type:sword\ngraphics:|:" + colour + "\n")));

            assertTrue(result.items().isEmpty(), "colour " + colour);
            assertTrue(result.hasErrors(), "colour " + colour);
        }
    }
}
