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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link EgoItemReader}: file text -> {@code EgoItemsLexer}/
 * {@code EgoItemsGrammar} -> {@code EgoItemParseRecord} -> {@code EgoItemAssembler} ->
 * {@link EgoItem}.
 *
 * <p>Unlike the earlier readers, the ego-item assembler resolves its {@code type:} and
 * {@code item:} lines into concrete {@link ObjectKind}s at load time (the port's fail-fast
 * mirror of C's {@code parse_ego_type}/{@code parse_ego_item}, which build the
 * {@code poss_items} list of kind indices). A {@code type:} tval fans out to <em>every</em>
 * loaded kind of that tval; an {@code item:} tval+sval resolves to exactly one. Both therefore
 * require {@code object.txt} to already be loaded, so {@link #seed()} loads the whole dependency
 * chain (pains &lt;- bases &lt;- summons/slays/brands; +object bases &lt;- curses &lt;- shapes
 * &lt;- object kinds) plus activations directly through their readers and injects them into the
 * private static registries by reflection, restoring them afterwards so no global state leaks to
 * other suites.
 *
 * <p>{@link EgoItem} exposes few getters, so assertions read its private fields reflectively via
 * {@link #field}.
 *
 * <p>The second half pins the reader's own contract against {@code ego_parser} in {@code obj-init.c}:
 * a missing file, the list in file order with every field of the 107 shipped egos worked out from the
 * upstream {@code ego_item.txt} by a separate parser (the table at the foot of the class), which
 * repeated directives overwrite ({@code info:}, {@code alloc:}, {@code combat:}, {@code min-combat:},
 * {@code act:}, {@code time:}) and which accumulate, the tval and sval rules of {@code tval_find_idx}
 * and {@code lookup_sval}, and each place the grammar or assembler differs from C. The differences are
 * written up in {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}.
 *
 * <p>Class EgoItemReaderTest coded before 261017, reader-contract tests added and commented in full on
 * 261017.
 *
 * @author Rowan Crowther
 */
class EgoItemReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ego_item.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";
    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String SHAPE_FILE = "lib/gamedata/shape.txt";
    private static final String OBJECT_FILE = "lib/gamedata/object.txt";
    private static final String ACTIVATION_FILE = "lib/gamedata/activation.txt";

    private static Object savedPains, savedBases, savedSummons, savedSlays, savedBrands,
            savedObjectBases, savedCurses, savedShapes, savedObjectKinds, savedActivations;

    /**
     * Seeds every registry the ego-item resolution depends on, in dependency order, so that
     * {@code type:}/{@code item:} lines can be resolved against real object kinds and
     * brand/slay/curse/activation lines against real codes.
     */
    @BeforeAll
    static void seed() throws Exception {
        savedPains = setStatic("monsterPains", new PainReader().parse(PAIN_FILE));
        savedBases = setStatic("monsterBases", new MonsterBaseReader().parse(BASE_FILE));
        savedSummons = setStatic("summons", new SummonReader().parse(SUMMON_FILE));
        savedSlays = setStatic("slays", new SlayReader().parse(SLAY_FILE));
        savedBrands = setStatic("brands", new BrandReader().parse(BRAND_FILE));
        savedObjectBases = setStatic("objectBases", new ObjectBaseReader().parse(OBJECT_BASE_FILE));
        savedCurses = setStatic("curses", new CurseReader().parse(CURSE_FILE));
        savedShapes = setStatic("playerShapes", new ShapeReader().parse(SHAPE_FILE));
        savedObjectKinds = setStatic("objectKinds", new ItemObjectReader().parse(OBJECT_FILE));
        savedActivations = setStatic("activations", new ActivationReader().parse(ACTIVATION_FILE));
    }

    /**
     * Restores every registry mutated by {@link #seed()} so the shared static state on
     * the registries do not leak into other test suites.
     */
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
        setStatic("objectKinds", savedObjectKinds);
        setStatic("activations", savedActivations);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    // ---- fixture + reflection helpers ------------------------------------

    @TempDir
    Path tempDir;

    private ParseResult<EgoItem> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new EgoItemReader().parseWithResults(file.toString());
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A single ego record: a name line followed by whatever body lines the test supplies.
     */
    private static String ego(String name, String body) {
        return "name:" + name + "\n" + body;
    }

    /**
     * Loads exactly one ego record and returns it, asserting the parse was clean.
     */
    private EgoItem loadOne(String fileName, String body) throws IOException {
        ParseResult<EgoItem> result = load(fileName, withHeader(1, ego("test", body)));
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        return result.items().get(0);
    }

    /**
     * Reads a private field off an {@link EgoItem} (which exposes no getters).
     */
    @SuppressWarnings("unchecked")
    private static <T> T field(EgoItem e, String name) throws Exception {
        Field f = EgoItem.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(e);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllEgosWithNoErrors() throws IOException {
        ParseResult<EgoItem> result = new EgoItemReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(107, result.items().size());
    }

    // ---- type: fan-out and item: resolution ------------------------------

    @Test
    void typeLineExpandsToEveryKindOfThatTval() throws Exception {
        EgoItem e = loadOne("type.txt", "type:sword\n");

        List<ObjectKind> poss = field(e, "possItems");
        int swordKinds = ObjectRegistry.lookupObjectKind(TValue.TV_SWORD).size();
        assertTrue(swordKinds > 1, "fixture assumes several sword kinds exist");
        assertEquals(swordKinds, poss.size());
        assertTrue(poss.stream().allMatch(k -> k.gettValue() == TValue.TV_SWORD));
    }

    @Test
    void multipleTypeLinesAccumulateIntoOneList() throws Exception {
        EgoItem e = loadOne("types.txt", "type:sword\ntype:hafted\n");

        List<ObjectKind> poss = field(e, "possItems");
        int expected = ObjectRegistry.lookupObjectKind(TValue.TV_SWORD).size()
                + ObjectRegistry.lookupObjectKind(TValue.TV_HAFTED).size();
        assertEquals(expected, poss.size());
    }

    @Test
    void itemLineResolvesExactlyOneKindBySval() throws Exception {
        EgoItem e = loadOne("item.txt", "item:light:Lantern\n");

        List<ObjectKind> poss = field(e, "possItems");
        assertEquals(1, poss.size());
        assertEquals("Lantern", poss.get(0).getsValueName());
        assertSame(ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Lantern"), poss.get(0));
    }

    @Test
    void typeAndItemLinesMergeIntoTheSameList() throws Exception {
        EgoItem e = loadOne("merge.txt", "type:hafted\nitem:light:Lantern\n");

        List<ObjectKind> poss = field(e, "possItems");
        int haftedKinds = ObjectRegistry.lookupObjectKind(TValue.TV_HAFTED).size();
        assertEquals(haftedKinds + 1, poss.size());
        assertTrue(poss.stream().anyMatch(k -> "Lantern".equals(k.getsValueName())));
    }

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void unknownTypeTvalIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-type.txt",
                withHeader(1, ego("weird", "type:no such tval\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        s -> s.contains("unknown TValue") && s.contains("no such tval")),
                result.errors()::toString);
    }

    @Test
    void unknownItemTvalIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-item-tval.txt",
                withHeader(1, ego("weird", "item:no such tval:Whatever\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        s -> s.contains("unknown TValue") && s.contains("no such tval")),
                result.errors()::toString);
    }

    @Test
    void unknownItemSvalIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-item-sval.txt",
                withHeader(1, ego("weird", "item:light:No Such Lantern\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("illegal sValue in its item")),
                result.errors()::toString);
    }

    @Test
    void numericItemSvalIsRejected() throws IOException {
        // C's lookup_sval takes a number as the sval itself. The kinds seeded here come straight from
        // ItemObjectReader, so none has been numbered and the registry's (tval, sval) index is empty:
        // a numeric sval finds nothing and the record is rejected. (In a running game
        // ObjectDataLoader numbers the kinds first, and the registry reads an all-digit reference as
        // a number, as C does.)
        ParseResult<EgoItem> result = load("numeric-sval.txt",
                withHeader(1, ego("weird", "item:light:3\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("illegal sValue in its item")),
                result.errors()::toString);
    }

    // ---- flags: OF / KF / element split, digits, and the space regression -

    @Test
    void flagsSplitAcrossObjectKindAndElementFlags() throws Exception {
        EgoItem e = loadOne("flags.txt",
                "type:sword\nflags:FREE_ACT | DIG_1 | RAND_POWER | IGNORE_FIRE\n");

        Flag<ObjectFlag> of = field(e, "flags");
        Flag<ObjectKindFlag> kf = field(e, "kindFLags");
        Map<ElementEnum, ElementInfo> elInfo = field(e, "elInfo");

        assertTrue(of.has(ObjectFlag.OF_FREE_ACT));
        assertTrue(of.has(ObjectFlag.OF_DIG_1), "digit-bearing OF flag");
        assertTrue(kf.has(ObjectKindFlag.KF_RAND_POWER));
        assertTrue(elInfo.get(ElementEnum.ELEM_FIRE).getFlags().has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    @Test
    void flagWithSpaceAfterColonStillResolves() throws Exception {
        // Regression: "flags: RAND_RES_POWER" (space after the colon, as on the of *Slay Demon*
        // record) was captured with a leading space by the FLAG lexer rule and failed KF lookup;
        // the .trim() in the flags/flagsOff grammar actions strips it.
        EgoItem e = loadOne("flag-space.txt", "type:sword\nflags: RAND_RES_POWER\n");

        Flag<ObjectKindFlag> kf = field(e, "kindFLags");
        assertTrue(kf.has(ObjectKindFlag.KF_RAND_RES_POWER));
    }

    @Test
    void unknownFlagIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-flag.txt",
                withHeader(1, ego("weird", "type:sword\nflags:NOTAFLAG\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown flag")),
                result.errors()::toString);
    }

    @Test
    void flagsOffAreParsedAsObjectFlags() throws Exception {
        EgoItem e = loadOne("flags-off.txt", "type:sword\nflags-off:SEE_INVIS\n");

        Flag<ObjectFlag> off = field(e, "flagsOff");
        assertTrue(off.has(ObjectFlag.OF_SEE_INVIS));
    }

    @Test
    void unknownFlagOffIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-flag-off.txt",
                withHeader(1, ego("weird", "type:sword\nflags-off:NOTAFLAG\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown flag-off")),
                result.errors()::toString);
    }

    // ---- values: dice-vs-RES_-int split, and min-values ints -------------

    @Test
    void valuesSplitDiceModifiersFromIntegerResistances() throws Exception {
        EgoItem e = loadOne("values.txt",
                "type:sword\nvalues:STEALTH[d2] | LIGHT[-4] | RES_FIRE[1]\n");

        Map<ObjectModifier, Random> mods = field(e, "modifiers");
        Map<ElementEnum, ElementInfo> elInfo = field(e, "elInfo");

        assertNotNull(mods.get(ObjectModifier.OM_STEALTH), "obj-mod parsed as dice");
        assertNotNull(mods.get(ObjectModifier.OM_LIGHT), "negative dice value parsed");
        assertFalse(mods.containsKey(ObjectModifier.OM_STR), "unrelated modifier absent");
        assertEquals(1, elInfo.get(ElementEnum.ELEM_FIRE).getResLevel(), "RES_ value stored as int");
    }

    @Test
    void minValuesStoredAsPlainInts() throws Exception {
        EgoItem e = loadOne("min-values.txt",
                "type:sword\nmin-values:STEALTH[2] | BLOWS[0]\n");

        Map<ObjectModifier, Integer> min = field(e, "minModifiers");
        assertEquals(2, min.get(ObjectModifier.OM_STEALTH));
        assertEquals(0, min.get(ObjectModifier.OM_BLOWS));
    }

    @Test
    void unknownModifierIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-mod.txt",
                withHeader(1, ego("weird", "type:sword\nvalues:NOTAMOD[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown modifier")),
                result.errors()::toString);
    }

    // ---- combat / min-combat ---------------------------------------------

    @Test
    void combatRandomFormsAreParsed() throws Exception {
        EgoItem e = loadOne("combat.txt", "type:sword\ncombat:-2:1d6M4:d4\n");

        assertNotNull(field(e, "toHit"), "negative combat form");
        assertNotNull(field(e, "toDam"), "base+dice+bonus form");
        assertNotNull(field(e, "toAC"), "bare-dice form");
    }

    @Test
    void minCombat255SentinelIsPreserved() throws Exception {
        // 255 means "no minimum"; the assembler stores it verbatim for the roll engine to honour.
        EgoItem e = loadOne("min-combat.txt", "type:sword\nmin-combat:255:0:255\n");

        assertEquals(255, (int) field(e, "minToHit"));
        assertEquals(0, (int) field(e, "minToDam"));
        assertEquals(255, (int) field(e, "minToAC"));
    }

    /**
     * C's {@code parse_ego_name} starts all three minimums at {@code NO_MINIMUM} (255), so an ego with
     * no {@code min-combat:} line has no minimum rather than a minimum of 0.
     */
    @Test
    void absentMinCombatDefaultsToNoMinimum() throws Exception {
        EgoItem e = loadOne("no-min-combat.txt", "type:sword\n");

        assertEquals(255, (int) field(e, "minToHit"));
        assertEquals(255, (int) field(e, "minToDam"));
        assertEquals(255, (int) field(e, "minToAC"));
    }

    /**
     * "of Accuracy" in the real {@code ego_item.txt} reads {@code min-combat:15:255:0}: a floor of 15
     * to-hit, no floor on damage, and a floor of 0 to-AC (which 255 exists to keep distinct).
     */
    @Test
    void realAccuracyEgoKeepsItsMinimums() throws Exception {
        ParseResult<EgoItem> result = new EgoItemReader().parseWithResults(REAL_FILE);

        EgoItem accuracy = result.items().get(33);
        assertEquals("of Accuracy", field(accuracy, "name"));
        assertEquals(15, (int) field(accuracy, "minToHit"));
        assertEquals(255, (int) field(accuracy, "minToDam"));
        assertEquals(0, (int) field(accuracy, "minToAC"));
    }

    /**
     * C's {@code finish_parse_ego} sets {@code eidx} to the record's place in the file, from 0. The
     * first, third and last records of the real file are "of Shadows", "(Everburning)" and "of Speed".
     */
    @Test
    void egoIndexIsFileOrderFromZero() throws Exception {
        ParseResult<EgoItem> result = new EgoItemReader().parseWithResults(REAL_FILE);

        for (int i = 0; i < result.items().size(); i++) {
            assertEquals(i, (int) field(result.items().get(i), "egoIndex"), "record " + i);
        }
        assertEquals("of Shadows", field(result.items().get(0), "name"));
        assertEquals("(Everburning)", field(result.items().get(2), "name"));
        assertEquals("of Speed", field(result.items().get(106), "name"));
    }

    // ---- brand / slay / curse --------------------------------------------

    /**
     * C's {@code parse_ego_curse} adds a curse "only if it has power": a power of 0 or less is
     * skipped, with no error, and the record loads without that curse.
     */
    @Test
    void curseWithNoPowerIsSkippedNotRejected() throws Exception {
        ParseResult<EgoItem> zero = load("curse-zero.txt",
                withHeader(1, ego("test", "type:sword\ncurse:vulnerability:0\n")));
        ParseResult<EgoItem> negative = load("curse-negative.txt",
                withHeader(1, ego("test", "type:sword\ncurse:vulnerability:-3\n")));

        for (ParseResult<EgoItem> result : List.of(zero, negative)) {
            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(1, result.items().size());
            Map<?, ?> curses = field(result.items().get(0), "curses");
            assertTrue(curses.isEmpty());
        }
    }

    @Test
    void brandAndSlayAndCurseResolveAgainstTheirRegistries() throws Exception {
        EgoItem e = loadOne("bsc.txt",
                "type:sword\nbrand:ACID_3\nslay:EVIL_2\ncurse:vulnerability:10\n");

        Set<?> brands = field(e, "brands");
        Set<?> slays = field(e, "slays");
        Map<?, CurseData> curses = field(e, "curses");
        assertEquals(1, brands.size());
        assertEquals(1, slays.size());
        assertEquals(1, curses.size());
        assertEquals(10, curses.values().iterator().next().getPower());
    }

    @Test
    void unknownBrandIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-brand.txt",
                withHeader(1, ego("weird", "type:sword\nbrand:NOTABRAND\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown brand")),
                result.errors()::toString);
    }

    @Test
    void unknownSlayIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-slay.txt",
                withHeader(1, ego("weird", "type:sword\nslay:NOTASLAY\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown slay")),
                result.errors()::toString);
    }

    @Test
    void unknownCurseIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-curse.txt",
                withHeader(1, ego("weird", "type:sword\ncurse:no such curse:10\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown curse")),
                result.errors()::toString);
    }

    // ---- desc + latent act: (unused in real data, kept for futureproofing) -

    @Test
    void descriptionLinesAreConcatenated() throws Exception {
        EgoItem e = loadOne("desc.txt", "type:sword\ndesc:First part \ndesc:second part.\n");

        assertEquals("First part second part.", field(e, "text"));
    }

    /**
     * C's {@code parse_ego_act} stores {@code findact(name)} and returns no error, so an unknown name
     * leaves the ego loaded with a {@code NULL} activation. Here the record is skipped with a soft
     * error; see {@code docs/Assembler_Issues.md}.
     */
    @Test
    void unknownActivationIsReportedAndSkippedHereWhereCKeepsTheRecordWithNone() throws IOException {
        ParseResult<EgoItem> result = load("bad-act.txt",
                withHeader(1, ego("weird", "type:sword\nact:NO_SUCH_ACT\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown activation type")),
                result.errors()::toString);
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordCountMismatchIsReportedButValidEgoStillLoads() throws IOException {
        ParseResult<EgoItem> result = load("bad-count.txt",
                withHeader(5, ego("plain", "type:sword\n")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<EgoItem> result = load("no-header.txt", ego("plain", "type:sword\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        ParseResult<EgoItem> result = load("no-name.txt", withHeader(1, "type:sword\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Reader contract, worked out from the upstream ego_item.txt and obj-init.c ----

    private static final String ACT_A = "CURE_POISON";
    private static final String ACT_B = "CURE_BLINDNESS";

    private static String dice(Random r) {
        return r.getBase() + "+" + r.getDice() + "d" + r.getSides() + "M" + r.getMBonus();
    }

    private static <E extends Enum<E>> String names(Iterable<E> flags, String prefix) {
        List<String> out = new ArrayList<>();
        for (E f : flags) out.add(f.name().substring(prefix.length()));
        Collections.sort(out);
        return String.join(",", out);
    }

    /**
     * Renders an ego as one row of {@link #EXPECTED_TABLE}: name, cost, rating, alloc
     * (common,min,max), min-combat, the three combat dice, the number of eligible kinds, the object
     * flags, flags-off, kind flags, ignored elements, hated elements, resistance levels, modifiers,
     * minimum modifiers, slay codes, brand codes, curse powers, activation, time and the description
     * (length and a character sum). An omitted {@code combat:} die shows as zero, as C holds it, and
     * an omitted {@code time:} as {@code -}.
     */
    private static String render(EgoItem e) throws Exception {
        Map<ElementEnum, ElementInfo> el = field(e, "elInfo");
        List<String> ign = new ArrayList<>();
        List<String> hate = new ArrayList<>();
        List<String> res = new ArrayList<>();
        for (Map.Entry<ElementEnum, ElementInfo> en : el.entrySet()) {
            String n = en.getKey().name().substring("ELEM_".length());
            if (en.getValue().has(ElementInfoEnum.EL_INFO_IGNORE)) ign.add(n);
            if (en.getValue().has(ElementInfoEnum.EL_INFO_HATES)) hate.add(n);
            if (en.getValue().getResLevel() != 0) res.add(n + "=" + en.getValue().getResLevel());
        }
        Collections.sort(ign);
        Collections.sort(hate);
        Collections.sort(res);

        Map<ObjectModifier, Random> mods = field(e, "modifiers");
        List<String> modList = new ArrayList<>();
        mods.forEach((k, v) -> modList.add(k.name().substring("OM_".length()) + "=" + dice(v)));
        Collections.sort(modList);

        Map<ObjectModifier, Integer> mins = field(e, "minModifiers");
        List<String> minList = new ArrayList<>();
        mins.forEach((k, v) -> minList.add(k.name().substring("OM_".length()) + "=" + v));
        Collections.sort(minList);

        Set<Slay> slays = field(e, "slays");
        List<String> slayList = new ArrayList<>(slays.stream().map(Slay::getCode).toList());
        Collections.sort(slayList);
        Set<Brand> brands = field(e, "brands");
        List<String> brandList = new ArrayList<>(brands.stream().map(Brand::getCode).toList());
        Collections.sort(brandList);
        Map<Curse, CurseData> curses = field(e, "curses");
        List<String> curseList = new ArrayList<>();
        curses.forEach((k, v) -> curseList.add(k.getName() + "=" + v.getPower()));
        Collections.sort(curseList);

        Random toHit = field(e, "toHit");
        Random toDam = field(e, "toDam");
        Random toAC = field(e, "toAC");
        String combat = Stream.of(toHit, toDam, toAC)
                .map(r -> r == null ? "0+0d0M0" : dice(r)).collect(Collectors.joining(";"));
        Random time = field(e, "time");
        Activation act = field(e, "activation");
        List<ObjectKind> poss = field(e, "possItems");
        String text = field(e, "text");
        int sum = 0;
        for (char c : text.toCharArray()) sum += c;

        Flag<ObjectFlag> of = field(e, "flags");
        Flag<ObjectFlag> off = field(e, "flagsOff");
        Flag<ObjectKindFlag> kf = field(e, "kindFLags");
        return String.join("|",
                (String) field(e, "name"),
                String.valueOf((int) field(e, "cost")),
                String.valueOf((int) field(e, "rating")),
                field(e, "allocProb") + "," + field(e, "allocMin") + "," + field(e, "allocMax"),
                field(e, "minToHit") + "," + field(e, "minToDam") + "," + field(e, "minToAC"),
                combat,
                String.valueOf(poss.size()),
                names(of, "OF_"), names(off, "OF_"), names(kf, "KF_"),
                String.join(",", ign), String.join(",", hate), String.join(",", res),
                String.join(",", modList), String.join(",", minList),
                String.join(",", slayList), String.join(",", brandList), String.join(",", curseList),
                act == null ? "" : act.getName(),
                time == null ? "-" : dice(time),
                text.length() + ":" + (sum % 65521));
    }

    private void assertHardError(String fileName, String body) throws IOException {
        ParseResult<EgoItem> result = load(fileName, withHeader(1, ego("test", body)));
        assertTrue(result.items().isEmpty(), "a syntax error returns no items");
        assertTrue(result.hasErrors());
    }

    private void assertSoftError(String fileName, String body, String message) throws IOException {
        ParseResult<EgoItem> result = load(fileName, withHeader(1, ego("test", body)));
        assertTrue(result.items().isEmpty(), "the record is skipped");
        assertTrue(result.errors().stream().anyMatch(s -> s.contains(message)),
                result.errors()::toString);
    }

    @Test
    void missingFileIsAnIOExceptionWhereCQuits() {
        // parse_file_quit_not_found calls quit() on a file that cannot be opened; here it surfaces
        // as an exception for the caller to treat as fatal.
        String missing = tempDir.resolve("absent.txt").toString();
        assertThrows(IOException.class, () -> new EgoItemReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new EgoItemReader().parse(missing));
    }

    @Test
    void parseReturnsTheSameEgosAsParseWithResultsInFileOrder() throws Exception {
        EgoItemReader reader = new EgoItemReader();
        List<EgoItem> viaParse = reader.parse(REAL_FILE);
        List<EgoItem> viaResults = reader.parseWithResults(REAL_FILE).items();

        assertEquals(107, viaParse.size());
        assertEquals(viaResults.size(), viaParse.size());
        for (int i = 0; i < viaParse.size(); i++) {
            assertEquals(render(viaResults.get(i)), render(viaParse.get(i)), "record " + i);
        }
    }

    @Test
    void parseHidesASyntaxErrorAsAnEmptyListWhereParseWithResultsReportsIt() throws IOException {
        Path file = tempDir.resolve("name-only.txt");
        Files.writeString(file, withHeader(1, "name:lonely\n"));

        assertTrue(new EgoItemReader().parse(file.toString()).isEmpty());
        assertTrue(new EgoItemReader().parseWithResults(file.toString()).hasErrors());
    }

    @Test
    void shippedFileDeclaresItsRecordCount() throws IOException {
        // The header is this repository's addition; upstream ego_item.txt has none. It must equal
        // the number of name: lines or every normal load would report a miscount.
        List<String> lines = Files.readAllLines(Path.of(REAL_FILE));

        assertEquals(1, lines.stream().filter(l -> l.startsWith("record-count:")).count());
        assertTrue(lines.contains("record-count:107"));
        assertEquals(107, lines.stream().filter(l -> l.startsWith("name:")).count());
    }

    /**
     * Every ego of the shipped file, in file order, against a table worked out from the upstream
     * {@code ego_item.txt} with a separate parser following {@code parse_ego_*} in {@code obj-init.c}:
     * the token rules of {@code grab_flag}, {@code grab_element_flag}, {@code grab_rand_value},
     * {@code grab_index_and_int} and {@code grab_int_value}, {@code parse_random} in {@code parser.c}
     * for the dice, and the kind counts taken from {@code object.txt} by tval (what the {@code type:}
     * handler's loop over {@code k_info} finds) plus one for each {@code item:} line.
     */
    @Test
    void shippedFileMatchesTableWorkedOutFromTheUpstreamFile() throws Exception {
        ParseResult<EgoItem> result = new EgoItemReader().parseWithResults(REAL_FILE);
        String[] expected = EXPECTED_TABLE.strip().split("\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(expected.length, result.items().size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], render(result.items().get(i)), "record " + i);
        }
    }

    // ---- List order and numbering ----------------------------------------

    /**
     * C numbers by place in the file, but a parse error there ends the load. Here a skipped record
     * takes no number, so the ego after it is numbered one lower than its place in the file and the
     * index still equals the position in the returned list.
     */
    @Test
    void aSkippedRecordTakesNoEgoIndexHereWhereCNumbersByFilePosition() throws Exception {
        ParseResult<EgoItem> result = load("skip-index.txt", withHeader(3,
                ego("first", "type:sword\n")
                        + ego("second", "type:sword\nflags:NOTAFLAG\n")
                        + ego("third", "type:sword\n")));

        assertEquals(2, result.items().size());
        assertEquals("first", field(result.items().get(0), "name"));
        assertEquals(0, (int) field(result.items().get(0), "egoIndex"));
        assertEquals("third", field(result.items().get(1), "name"));
        assertEquals(1, (int) field(result.items().get(1), "egoIndex"));
        assertEquals(1, result.errors().size(), result.errors()::toString);
    }

    @Test
    void twoRecordsWithTheSameNameBothLoad() throws Exception {
        // parse_ego_name allocates a new record each time and checks nothing against the others.
        ParseResult<EgoItem> result = load("dupe-names.txt", withHeader(2,
                ego("same", "info:100:1\n") + ego("same", "info:200:2\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(100, (int) field(result.items().get(0), "cost"));
        assertEquals(200, (int) field(result.items().get(1), "cost"));
    }

    // ---- Directive order and repetition ----------------------------------

    @Test
    void directivesMayComeInAnyOrder() throws Exception {
        String[] lines = ("info:100:5\nalloc:10:1 to 2\ncombat:1:2:3\nmin-combat:4:5:6\ntype:sword\n"
                + "item:light:Lantern\nflags:SEE_INVIS\nflags-off:FREE_ACT\nvalues:STEALTH[2]\n"
                + "min-values:STEALTH[1]\nslay:EVIL_2\nbrand:ACID_3\ncurse:vulnerability:10\n"
                + "desc:text\n").split("\n");
        List<String> reversed = new ArrayList<>(List.of(lines));
        Collections.reverse(reversed);

        EgoItem forward = loadOne("order-forward.txt", String.join("\n", lines) + "\n");
        EgoItem backward = loadOne("order-backward.txt", String.join("\n", reversed) + "\n");

        assertEquals(render(forward), render(backward));
    }

    /**
     * {@code info:}, {@code alloc:}, {@code combat:}, {@code min-combat:}, {@code act:} and
     * {@code time:} each overwrite what an earlier line of the record set.
     */
    @Test
    void repeatedScalarDirectivesKeepTheLastLine() throws Exception {
        EgoItem e = loadOne("overwrite.txt",
                "info:100:5\ninfo:200:6\nalloc:10:1 to 2\nalloc:30:3 to 4\n"
                        + "combat:1:1:1\ncombat:2:3:4\nmin-combat:1:1:1\nmin-combat:7:8:9\n"
                        + "act:" + ACT_A + "\nact:" + ACT_B + "\ntime:1\ntime:2\n");

        assertEquals(200, (int) field(e, "cost"));
        assertEquals(6, (int) field(e, "rating"));
        assertEquals(30, (int) field(e, "allocProb"));
        assertEquals(3, (int) field(e, "allocMin"));
        assertEquals(4, (int) field(e, "allocMax"));
        assertEquals(2, ((Random) field(e, "toHit")).getBase());
        assertEquals(3, ((Random) field(e, "toDam")).getBase());
        assertEquals(4, ((Random) field(e, "toAC")).getBase());
        assertEquals(7, (int) field(e, "minToHit"));
        assertEquals(8, (int) field(e, "minToDam"));
        assertEquals(9, (int) field(e, "minToAC"));
        assertEquals(ACT_B, ((Activation) field(e, "activation")).getName());
        assertEquals(2, ((Random) field(e, "time")).getBase());
    }

    /**
     * {@code type:}, {@code item:}, {@code flags:}, {@code flags-off:}, {@code values:},
     * {@code min-values:}, {@code slay:}, {@code brand:}, {@code curse:} and {@code desc:} add to
     * what earlier lines set, and the description lines are joined with no separator.
     */
    @Test
    void repeatedAccumulatingDirectivesAddUp() throws Exception {
        EgoItem e = loadOne("accumulate.txt",
                "type:sword\ntype:hafted\nitem:light:Lantern\nitem:light:Wooden Torch\n"
                        + "flags:SEE_INVIS\nflags:FREE_ACT\nflags-off:FEATHER\nflags-off:TELEPATHY\n"
                        + "values:STEALTH[1]\nvalues:LIGHT[2]\nmin-values:STEALTH[1]\nmin-values:LIGHT[2]\n"
                        + "slay:EVIL_2\nslay:ORC_3\nbrand:ACID_3\nbrand:FIRE_3\n"
                        + "curse:vulnerability:10\ncurse:teleportation:20\n"
                        + "desc:one \ndesc:two \ndesc:three\n");

        List<ObjectKind> poss = field(e, "possItems");
        assertEquals(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD).size()
                + ObjectRegistry.lookupObjectKind(TValue.TV_HAFTED).size() + 2, poss.size());
        Flag<ObjectFlag> of = field(e, "flags");
        Flag<ObjectFlag> off = field(e, "flagsOff");
        assertEquals("FREE_ACT,SEE_INVIS", names(of, "OF_"));
        assertEquals("FEATHER,TELEPATHY", names(off, "OF_"));
        assertEquals(2, ((Map<?, ?>) field(e, "modifiers")).size());
        assertEquals(2, ((Map<?, ?>) field(e, "minModifiers")).size());
        assertEquals(2, ((Set<?>) field(e, "slays")).size());
        assertEquals(2, ((Set<?>) field(e, "brands")).size());
        assertEquals(2, ((Map<?, ?>) field(e, "curses")).size());
        assertEquals("one two three", field(e, "text"));
    }

    @Test
    void commentsAndBlankLinesBetweenDirectivesAreIgnored() throws Exception {
        // parser_parse skips a line that is empty after leading whitespace, or starts with '#'.
        EgoItem e = loadOne("comments.txt",
                "# a comment\n\ntype:sword\n   \n# another\ninfo:100:5\n");

        assertEquals(100, (int) field(e, "cost"));
    }

    @Test
    void windowsLineEndingsLoad() throws Exception {
        ParseResult<EgoItem> result = load("crlf.txt",
                "record-count:1\r\nname:test\r\ntype:sword\r\ninfo:100:5\r\nflags:SEE_INVIS\r\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(100, (int) field(result.items().get(0), "cost"));
    }

    // ---- info:, alloc: and the combat figures ------------------------------

    @Test
    void infoAndAllocFigures() throws Exception {
        EgoItem e = loadOne("info-alloc.txt", "info:-500:12\nalloc:40:10 to 100\n");

        assertEquals(-500, (int) field(e, "cost"));
        assertEquals(12, (int) field(e, "rating"));
        assertEquals(40, (int) field(e, "allocProb"));
        assertEquals(10, (int) field(e, "allocMin"));
        assertEquals(100, (int) field(e, "allocMax"));
    }

    @Test
    void anEgoWithNoInfoOrAllocHoldsZeros() throws Exception {
        EgoItem e = loadOne("no-info-alloc.txt", "type:sword\n");

        assertEquals(0, (int) field(e, "cost"));
        assertEquals(0, (int) field(e, "rating"));
        assertEquals(0, (int) field(e, "allocProb"));
        assertEquals(0, (int) field(e, "allocMin"));
        assertEquals(0, (int) field(e, "allocMax"));
    }

    @Test
    void allocBoundsOfZeroAnd255AreAccepted() throws Exception {
        EgoItem e = loadOne("alloc-bounds.txt", "alloc:10:0 to 255\n");

        assertEquals(0, (int) field(e, "allocMin"));
        assertEquals(255, (int) field(e, "allocMax"));
    }

    @Test
    void allocMinimumAboveMaximumIsNotAnError() throws Exception {
        // parse_ego_alloc checks each figure against 0..255 and nothing else.
        EgoItem e = loadOne("alloc-inverted.txt", "alloc:10:50 to 20\n");

        assertEquals(50, (int) field(e, "allocMin"));
        assertEquals(20, (int) field(e, "allocMax"));
    }

    @Test
    void combatAcceptsNegatedDiceAndTheMBonusForms() throws Exception {
        // parse_random: a leading '-' negates the whole die, which is folded into the base.
        EgoItem e = loadOne("combat-forms.txt", "combat:-26+d25:1+M3:d4\n");

        Random toHit = field(e, "toHit");
        assertEquals(-52, toHit.getBase());
        assertEquals(1, toHit.getDice());
        assertEquals(25, toHit.getSides());
        Random toDam = field(e, "toDam");
        assertEquals(1, toDam.getBase());
        assertEquals(3, toDam.getMBonus());
        Random toAC = field(e, "toAC");
        assertEquals(0, toAC.getBase());
        assertEquals(1, toAC.getDice());
        assertEquals(4, toAC.getSides());
    }

    @Test
    void timeLineSetsTheRechargeDiceAndActivationNamesTheActivation() throws Exception {
        EgoItem e = loadOne("act-time.txt", "act:" + ACT_A + "\ntime:5+d10\n");

        Random time = field(e, "time");
        assertEquals(5, time.getBase());
        assertEquals(1, time.getDice());
        assertEquals(10, time.getSides());
        assertSame(ObjectRegistry.lookupActivation(ACT_A), field(e, "activation"));
    }

    // ---- type: and item: ----------------------------------------------------

    @Test
    void tvalAndSvalAreMatchedWithoutRegardToCase() throws Exception {
        // tval_find_idx and lookup_sval compare with my_stricmp.
        EgoItem e = loadOne("case.txt", "type:SWORD\nitem:LIGHT:lantern\n");

        List<ObjectKind> poss = field(e, "possItems");
        assertEquals(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD).size() + 1, poss.size());
    }

    @Test
    void aTvalMayBeWrittenWithTheBritishSpellingOfArmour() throws Exception {
        // de_armour turns "armour" into "armor" before the lookup.
        EgoItem e = loadOne("armour.txt", "type:soft armour\n");

        List<ObjectKind> poss = field(e, "possItems");
        assertEquals(ObjectRegistry.lookupObjectKind(TValue.TV_SOFT_ARMOR).size(), poss.size());
        assertFalse(poss.isEmpty());
    }

    @Test
    void aTrailingSpaceAfterATvalIsNotIgnoredAsInC() throws IOException {
        // The sym token keeps the space, so my_stricmp("sword ", "sword") fails in C as well.
        assertSoftError("tval-space.txt", "type:sword \n", "unknown TValue");
    }

    @Test
    void aTvalWithNoKindsInObjectTxtIsRejected() throws IOException {
        // The books are created by class.txt, so no kind of this tval exists when the egos load.
        // parse_ego_type returns PARSE_ERROR_NO_KIND_FOR_EGO_TYPE.
        assertSoftError("no-kinds.txt", "type:magic book\n", "no known ObjectKinds");
    }

    // ---- flags:, flags-off: and values: --------------------------------------

    @Test
    void aTrailingBarAfterTheLastFlagIsAccepted() throws Exception {
        // The upstream file has "flags:FREE_ACT | " on one record.
        EgoItem e = loadOne("flags-trailing-bar.txt", "type:sword\nflags:FREE_ACT |\n");

        Flag<ObjectFlag> of = field(e, "flags");
        assertEquals("FREE_ACT", names(of, "OF_"));
    }

    @Test
    void flagsWithoutSpacesAroundTheBarAreAccepted() throws Exception {
        EgoItem e = loadOne("flags-tight-bar.txt", "type:sword\nflags:SEE_INVIS|FREE_ACT\n");

        Flag<ObjectFlag> of = field(e, "flags");
        assertEquals("FREE_ACT,SEE_INVIS", names(of, "OF_"));
    }

    @Test
    void aLowercaseFlagIsRejectedAsInC() throws IOException {
        // grab_flag compares with streq.
        assertHardError("flag-lower.txt", "type:sword\nflags:see_invis\n");
    }

    @Test
    void anIgnoreAndAResistanceOnTheSameElementShareOneRecord() throws Exception {
        // Both write to e->el_info[ELEM_FIRE].
        EgoItem e = loadOne("fire-both.txt", "type:sword\nflags:IGNORE_FIRE\nvalues:RES_FIRE[1]\n");

        Map<ElementEnum, ElementInfo> el = field(e, "elInfo");
        assertEquals(1, el.size());
        assertTrue(el.get(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertEquals(1, el.get(ElementEnum.ELEM_FIRE).getResLevel());
    }

    @Test
    void aResistanceLevelMayBeNegative() throws Exception {
        EgoItem e = loadOne("res-neg.txt", "type:sword\nvalues:RES_FIRE[-1]\n");

        Map<ElementEnum, ElementInfo> el = field(e, "elInfo");
        assertEquals(-1, el.get(ElementEnum.ELEM_FIRE).getResLevel());
    }

    @Test
    void aDiceFigureInAResistanceIsRejectedAsInC() throws IOException {
        // grab_index_and_int reads the bracket with strtol and wants the ']' straight after it.
        assertSoftError("res-dice.txt", "type:sword\nvalues:RES_FIRE[d2]\n", "malformed integer value");
    }

    @Test
    void aDiceFigureInAMinValueIsRejectedAsInC() throws IOException {
        // grab_int_value reads the bracket with strtol.
        assertSoftError("minval-dice.txt", "type:sword\nmin-values:STEALTH[d2]\n", "malformed min value");
    }

    @Test
    void anUnknownMinValueNameIsRejected() throws IOException {
        assertSoftError("minval-name.txt", "type:sword\nmin-values:NOTAMOD[1]\n", "unknown min value name");
    }

    // ---- slay:, brand: and curse: ----------------------------------------------

    @Test
    void slayBrandAndCurseNamesAreMatchedExactly() throws IOException {
        // All three lookups in C are streq.
        assertSoftError("slay-case.txt", "type:sword\nslay:evil_2\n", "unknown slay");
        assertSoftError("brand-case.txt", "type:sword\nbrand:acid_3\n", "unknown brand");
        assertSoftError("curse-case.txt", "type:sword\ncurse:Vulnerability:5\n", "unknown curse");
    }

    @Test
    void aCurseKeepsTheLastPowerWrittenForItsName() throws Exception {
        EgoItem e = loadOne("curse-repeat.txt",
                "type:sword\ncurse:vulnerability:10\ncurse:vulnerability:20\n");

        Map<?, CurseData> curses = field(e, "curses");
        assertEquals(1, curses.size());
        assertEquals(20, curses.values().iterator().next().getPower());
    }

    // ---- Where the grammar is stricter than C ------------------------------------

    /**
     * {@code parse_ego_name} builds a complete record from the name alone. The grammar wants at least
     * one directive after it.
     */
    @Test
    void aRecordOfOnlyANameIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<EgoItem> result = load("name-only.txt", withHeader(1, "name:lonely\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aFileWithNoRecordsIsASyntaxErrorHereWhereCLoadsNothing() throws IOException {
        ParseResult<EgoItem> result = load("no-records.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * {@code flags ?str flags} is optional, and {@code parse_ego_flags} and {@code parse_ego_flags_off}
     * return no error when {@code parser_hasval} is false.
     */
    @Test
    void emptyFlagsAndFlagsOffLinesAreSyntaxErrorsHereWhereCAcceptsThem() throws IOException {
        assertHardError("flags-empty.txt", "type:sword\nflags:\n");
        assertHardError("flags-off-empty.txt", "type:sword\nflags-off:\n");
    }

    /**
     * C splits the list with {@code strtok(flags, " |")}, so a space alone, a doubled bar and a
     * leading bar all separate flags. The grammar wants exactly {@code |} between two flags, with
     * optional spaces around it.
     */
    @Test
    void flagsSeparatedBySpaceAloneOrByOddBarsAreSyntaxErrorsHereWhereCAcceptsThem() throws IOException {
        assertHardError("flags-space.txt", "type:sword\nflags:SEE_INVIS FREE_ACT\n");
        assertHardError("flags-double-bar.txt", "type:sword\nflags:SEE_INVIS || FREE_ACT\n");
        assertHardError("flags-leading-bar.txt", "type:sword\nflags:| SEE_INVIS\n");
    }

    /**
     * The same {@code strtok(s, " |")} splits {@code values:} and {@code min-values:}. The grammar
     * accepts a trailing bar after flags but not after values, and wants a bar between two values.
     */
    @Test
    void valuesSeparatedBySpaceAloneOrEndingInABarAreSyntaxErrorsHereWhereCAcceptsThem() throws IOException {
        assertHardError("values-space.txt", "type:sword\nvalues:STEALTH[1] LIGHT[2]\n");
        assertHardError("values-trailing-bar.txt", "type:sword\nvalues:STEALTH[1] |\n");
        assertHardError("values-double-bar.txt", "type:sword\nvalues:STEALTH[1] || LIGHT[2]\n");
        assertHardError("min-values-trailing-bar.txt", "type:sword\nmin-values:STEALTH[1] |\n");
    }

    /**
     * {@code grab_int_range} takes {@code <int><whitespace><optional "to"><whitespace><int>} and
     * ignores trailing spaces, and {@code strtol} skips leading ones. The lexer matches the literal
     * {@code " to "} and nothing else around the figures.
     */
    @Test
    void allocSpacingOtherThanOneSpaceEitherSideOfToIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("alloc-wide.txt", "alloc:10:5  to  20\n");
        assertHardError("alloc-tabs.txt", "alloc:10:5\tto\t20\n");
        assertHardError("alloc-leading.txt", "alloc:10: 5 to 20\n");
        assertHardError("alloc-trailing.txt", "alloc:10:5 to 20 \n");
    }

    /**
     * {@code parser_parse} reads an {@code int} field with {@code strtol(tok, &z, 0)}: leading
     * whitespace skipped, a plus sign allowed, base 0 so {@code 0x10} is 16. The lexer's integer is
     * an optional minus and decimal digits.
     */
    @Test
    void integersWithLeadingSpaceAPlusSignOrAHexPrefixAreErrorsHereWhereCAcceptsThem() throws IOException {
        assertHardError("info-space.txt", "info: 5: 6\n");
        assertHardError("info-plus.txt", "info:+5:6\n");
        assertHardError("info-hex.txt", "info:0x10:6\n");
        assertHardError("minval-plus.txt", "type:sword\nmin-values:STEALTH[+2]\n");
        assertHardError("res-plus.txt", "type:sword\nvalues:RES_FIRE[+1]\n");
    }

    @Test
    void aCombatLineWithTwoDiceIsASyntaxErrorAsInC() throws IOException {
        // "combat rand th rand td rand ta": a missing field is PARSE_ERROR_MISSING_FIELD.
        assertHardError("combat-two.txt", "combat:1:2\n");
    }

    @Test
    void anUnknownDirectiveIsASyntaxErrorAsInC() throws IOException {
        assertHardError("unknown-directive.txt", "banana:1\n");
    }

    // ---- Where the assembler differs from C ----------------------------------------

    /**
     * {@code parse_ego_alloc} returns {@code PARSE_ERROR_OUT_OF_BOUNDS} for either figure above 255
     * or below 0. The assembler parses the two figures and checks neither.
     */
    @Test
    void allocFiguresOutsideZeroTo255AreAcceptedHereWhereCRejectsThem() throws Exception {
        EgoItem above = loadOne("alloc-above.txt", "alloc:10:256 to 300\n");
        EgoItem below = loadOne("alloc-below.txt", "alloc:10:-1 to 5\n");

        assertEquals(300, (int) field(above, "allocMax"));
        assertEquals(-1, (int) field(below, "allocMin"));
    }

    /**
     * {@code parse_ego_curse} adds a curse "only if it has power", so a later {@code curse:} line for
     * the same name with power 0 or less is a no-op and the earlier power stays. The grammar keeps
     * the last power per name whatever it is, so the assembler then drops the curse altogether.
     */
    @Test
    void aLaterCurseLineWithNoPowerErasesTheEarlierOneHereWhereCKeepsIt() throws Exception {
        EgoItem zero = loadOne("curse-then-zero.txt",
                "type:sword\ncurse:vulnerability:10\ncurse:vulnerability:0\n");
        EgoItem negative = loadOne("curse-then-negative.txt",
                "type:sword\ncurse:vulnerability:5\ncurse:vulnerability:-5\n");

        assertTrue(((Map<?, ?>) field(zero, "curses")).isEmpty());
        assertTrue(((Map<?, ?>) field(negative, "curses")).isEmpty());
    }

    /**
     * {@code grab_element_flag} accepts {@code HATES_<element>} as well as {@code IGNORE_<element>}.
     * The assembler only knows the {@code IGNORE_} prefix. The shipped file uses neither
     * {@code HATES_} nor anything else of the kind.
     */
    @Test
    void aHatesFlagIsAnUnknownFlagHereWhereCAcceptsIt() throws IOException {
        assertSoftError("hates.txt", "type:sword\nflags:HATES_FIRE\n", "unknown flag");
    }

    /**
     * C's name tables stop short of the sentinels: {@code element_names} and {@code obj_mods} have
     * no {@code NONE} or {@code MAX}. The assembler looks names up with {@code valueOf} on the Java
     * enums, which do have them.
     */
    @Test
    void sentinelElementAndModifierNamesAreAcceptedHereWhereCRejectsThem() throws Exception {
        EgoItem ignored = loadOne("sentinel-ignore.txt", "type:sword\nflags:IGNORE_NONE | IGNORE_MAX\n");
        EgoItem resisted = loadOne("sentinel-res.txt", "type:sword\nvalues:RES_NONE[1] | RES_MAX[1]\n");
        EgoItem modified = loadOne("sentinel-mod.txt", "type:sword\nvalues:NONE[1]\n");

        Map<ElementEnum, ElementInfo> ignoredEl = field(ignored, "elInfo");
        assertTrue(ignoredEl.containsKey(ElementEnum.ELEM_NONE));
        assertTrue(ignoredEl.containsKey(ElementEnum.ELEM_MAX));
        Map<ElementEnum, ElementInfo> resistedEl = field(resisted, "elInfo");
        assertEquals(1, resistedEl.get(ElementEnum.ELEM_NONE).getResLevel());
        assertEquals(1, resistedEl.get(ElementEnum.ELEM_MAX).getResLevel());
        Map<ObjectModifier, Random> mods = field(modified, "modifiers");
        assertTrue(mods.containsKey(ObjectModifier.OM_NONE));
    }

    /**
     * C's zeroed {@code struct ego_item} holds a zero {@code struct random} for each of the three
     * combat dice and for {@code time}. The assembler leaves them {@code null} when the line is
     * absent, as {@code EgoItem}'s constructor Javadoc says, so a caller that rolls them has to
     * guard.
     */
    @Test
    void omittedCombatAndTimeDiceAreNullHereWhereCHoldsZero() throws Exception {
        EgoItem e = loadOne("no-dice.txt", "type:sword\n");

        assertNull(field(e, "toHit"));
        assertNull(field(e, "toDam"));
        assertNull(field(e, "toAC"));
        assertNull(field(e, "time"));
    }

    /**
     * {@code parse_ego_type} and {@code parse_ego_item} push each kind on the head of
     * {@code poss_items}, so the list runs from the last kind added back to the first, and
     * {@code object_info_ego} takes the head as the example item. The assembler keeps every
     * {@code type:} line's kinds in {@code k_info} order, then every {@code item:} line's, whatever
     * order the lines came in.
     */
    @Test
    void eligibleKindsRunFirstToLastHereWhereCRunsLastToFirst() throws Exception {
        EgoItem e = loadOne("poss-order.txt", "item:light:Lantern\ntype:sword\n");

        List<ObjectKind> poss = field(e, "possItems");
        List<ObjectKind> swords = ObjectRegistry.lookupObjectKind(TValue.TV_SWORD);
        assertEquals(swords.size() + 1, poss.size());
        assertEquals(swords, poss.subList(0, swords.size()), "type: kinds first, in k_info order");
        assertEquals("Lantern", poss.get(poss.size() - 1).getsValueName(), "item: kinds after them");
    }

    // ---- The table ----------------------------------------------------------------

    private static final String EXPECTED_TABLE = """
of Shadows|1000|10|40,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|1||||||LIGHT=1|LIGHT=-4+0d0M0,STEALTH=0+1d2M0|LIGHT=-4,STEALTH=0|||||-|0:0
of Brightness|2000|10|50,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|2|||||||LIGHT=1+0d0M0|LIGHT=1|||||-|0:0
(Everburning)|2500|10|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|1|NO_FUEL|TAKES_FUEL|||||||||||-|0:0
of True Sight|6000|10|10,20,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|1|PROT_BLIND,SEE_INVIS||||||||||||-|0:0
(Holy Avenger)|20000|30|10,15,127|255,255,255|0+1d6M0;0+1d6M0;0+1d4M0|44|BLESSED,PROT_FEAR,SEE_INVIS||RAND_SUSTAIN||||WIS=0+1d4M0|WIS=0|DEMON_3,EVIL_2,UNDEAD_3||||-|0:0
(Defender)|15000|25|10,10,80|255,255,255|0+1d4M0;0+1d4M0;0+1d8M0|44|FEATHER,FREE_ACT,REGEN,SEE_INVIS||RAND_SUSTAIN|ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|STEALTH=0+1d4M0|STEALTH=0|||||-|0:0
(Blessed)|5000|20|20,1,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|BLESSED||RAND_POWER||||WIS=0+1d3M0|WIS=0|||||-|0:0
of Gondolin|25000|30|10,20,127|255,255,255|0+1d7M0;0+1d7M0;0+0d0M0|44|FREE_ACT,SEE_INVIS||RAND_POWER|ACID,FIRE||DARK=1|LIGHT=1+0d0M0|LIGHT=1|DEMON_3,DRAGON_3,ORC_3,TROLL_3||||-|0:0
of Westernesse|20000|20|10,10,70|255,255,255|0+1d5M0;0+1d5M0;0+0d0M0|44|FREE_ACT,SEE_INVIS||||||CON=0+1d2M0,DEX=0+1d2M0,STR=0+1d2M0|CON=1,DEX=1,STR=1|GIANT_3,ORC_3,TROLL_3||||-|0:0
of Extra Attacks|10000|20|10,10,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||BLOWS=0+1d2M0|BLOWS=0|||||-|0:0
of Fury|20000|30|2,50,127|255,255,255|0+1d10M0;0+1d10M0;0+0d0M0|0|AGGRAVATE,PROT_FEAR|||ACID,FIRE|||BLOWS=0+1d2M0,STR=1+1d2M2|BLOWS=1,STR=2|||||-|0:0
of Acid|5000|20|30,1,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44||||ACID||ACID=1||||ACID_3|||-|0:0
of Lightning|4500|20|30,1,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44||||ELEC||ELEC=1||||ELEC_3|||-|0:0
of Flame|3500|15|40,1,50|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44||||FIRE||FIRE=1||||FIRE_3|||-|0:0
of Frost|3000|15|40,1,50|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44||||COLD||COLD=1||||COLD_3|||-|0:0
of Venom|4000|15|60,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44||||||||||POIS_3|||-|0:0
of Slay Animal|3000|18|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||ANIMAL_2||||-|0:0
of Slay Evil|3000|18|60,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||EVIL_2||||-|0:0
of Slay Undead|3500|18|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|41|||||||||UNDEAD_3||||-|0:0
of Slay Demon|3500|14|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||DEMON_3||||-|0:0
of Slay Orc|2500|10|150,1,20|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||ORC_3||||-|0:0
of Slay Troll|2500|10|150,1,25|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||TROLL_3||||-|0:0
of Slay Giant|2500|14|60,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||GIANT_3||||-|0:0
of Slay Dragon|3500|18|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||||||||DRAGON_3||||-|0:0
of *Slay Animal*|6000|20|40,1,45|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||RAND_RES_POWER||||INT=0+1d2M0|INT=0|ANIMAL_2||||-|0:0
of *Slay Evil*|5000|20|15,10,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|BLESSED||RAND_RES_POWER||||WIS=0+1d2M0|WIS=0|EVIL_2||||-|0:0
of *Slay Undead*|8000|24|30,10,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|41|||RAND_RES_POWER||||WIS=0+1d2M0|WIS=0|UNDEAD_5||||-|0:0
of *Slay Demon*|8000|16|30,10,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||RAND_RES_POWER||||INT=0+1d2M0|INT=0|DEMON_5||||-|0:0
of *Slay Orc*|4000|14|60,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||RAND_RES_POWER||||DEX=0+1d2M0|DEX=0|ORC_3||||-|0:0
of *Slay Troll*|4000|14|60,1,45|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||RAND_RES_POWER||||STR=0+1d2M0|STR=0|TROLL_3||||-|0:0
of *Slay Giant*|4000|16|20,1,45|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||RAND_RES_POWER||||STR=0+1d2M0|STR=0|GIANT_3||||-|0:0
of *Slay Dragon*|8000|24|30,10,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|||RAND_RES_POWER||||CON=0+1d2M0|CON=0|DRAGON_5||||-|0:0
of Morgul|1|0|10,10,80|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|44|AGGRAVATE,DRAIN_EXP,HOLD_LIFE,SEE_INVIS||||||||UNDEAD_3|POIS_3|||-|0:0
of Accuracy|1000|10|100,5,70|15,255,0|0+1d15M0;0+1d5M0;0+0d0M0|5|||||||||||||-|72:6644
of Power|1000|10|100,5,70|255,15,0|0+1d5M0;0+1d15M0;0+0d0M0|5|||||||||||||-|72:6625
of Lothlórien|20000|30|5,20,127|255,255,255|0+1d10M0;0+1d10M0;0+0d0M0|2|FREE_ACT||RAND_POWER|ACID,FIRE|||DEX=1+1d2M2,MIGHT=0+1d2M0|DEX=2,MIGHT=1|||||-|0:0
of the Haradrim|20000|30|5,20,127|255,255,255|0+1d5M0;0+1d5M0;0+0d0M0|1||||ACID,FIRE|||MIGHT=1+0d0M0,SHOTS=0+2d3M0|MIGHT=1,SHOTS=2|||||-|0:0
of Extra Might|10000|20|20,15,100|255,255,255|0+1d5M0;0+1d10M0;0+0d0M0|5|||||||MIGHT=1+0d0M0|MIGHT=0|||||-|0:0
of Extra Shots|10000|20|10,15,100|255,255,255|0+1d10M0;0+1d5M0;0+0d0M0|5|||||||SHOTS=0+2d3M0|SHOTS=2|||||-|0:0
of Buckland|20000|25|5,20,127|255,255,255|0+1d8M0;0+1d8M0;0+0d0M0|1||||ACID,FIRE|||DEX=1+1d2M2,MIGHT=0+1d2M0,SHOTS=0+2d5M0|DEX=2,MIGHT=1,SHOTS=2|||||-|0:0
of the Nazgûl|0|0|10,10,80|255,255,255|0+1d10M0;0+1d10M0;0+0d0M0|5|DRAIN_EXP,SEE_INVIS||||||||||||-|0:0
of Slay Animal|20|10|80,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9|||||||||ANIMAL_2||||-|0:0
of Slay Evil|20|10|10,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9|||||||||EVIL_2||||-|0:0
of Slay Undead|25|10|15,5,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9|||||||||UNDEAD_3||||-|0:0
of Slay Demon|25|10|15,5,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9|||||||||DEMON_3||||-|0:0
of Acid|50|10|5,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9||||ACID||||||ACID_3|||-|0:0
of Lightning|45|10|5,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9||||ELEC||||||ELEC_3|||-|0:0
of Slay Giant|25|10|5,5,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9|||||||||GIANT_3||||-|0:0
of Slay Dragon|40|10|10,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9|||||||||DRAGON_3||||-|0:0
of Holy Might|60|20|2,30,127|255,255,255|0+1d10M0;0+1d10M0;0+0d0M0|5||||ACID,FIRE|||||DEMON_3,EVIL_2,UNDEAD_3||||-|0:0
of Venom|40|10|20,10,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9||||||||||POIS_3|||-|0:0
of Flame|35|10|10,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9||||FIRE||||||FIRE_3|||-|0:0
of Frost|30|10|10,10,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|9||||COLD||||||COLD_3|||-|0:0
of Wounding|20|5|15,15,127|255,255,255|3+1d5M0;3+1d5M0;0+0d0M0|9|||||||||||||-|73:6899
of Backbiting|0|0|0,10,80|255,255,0|-52+1d25M0;-52+1d25M0;0+0d0M0|9|||||||||||||-|0:0
of Digging|500|4|100,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|3|||||||TUNNEL=0+1d4M0|TUNNEL=1||ACID_3|||-|0:0
of Earthquakes|3000|8|10,10,127|255,255,255|0+1d10M0;0+1d10M0;0+0d0M0|3|IMPACT||||||STR=0+1d4M0,TUNNEL=0+1d6M0|STR=1,TUNNEL=1||ACID_3|||-|0:0
of Slow Descent|250|7|100,1,15|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6|FEATHER||||||||||||-|0:0
of Stealth|500|16|20,15,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|3|||||||STEALTH=0+1d3M0|STEALTH=0|||||-|0:0
of Free Action|1000|15|20,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6|FREE_ACT||||||||||||-|0:0
of Speed|100000|25|3,20,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6|||||||SPEED=2+0d0M8|SPEED=0|||||-|0:0
of Stability|5000|20|10,15,100|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6|FEATHER|||||NEXUS=1|||||||-|0:0
of Elvenkind|200000|30|3,60,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|2|FEATHER|||ACID,FIRE|||SPEED=4+0d0M5,STEALTH=0+1d4M0|SPEED=1,STEALTH=1|||||-|0:0
of Intelligence|500|13|100,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|SUST_INT||||||INT=0+1d2M0|INT=0|||||-|0:0
of Wisdom|500|13|100,1,40|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|SUST_WIS||||||WIS=0+1d2M0|WIS=0|||||-|0:0
of the Magi|7500|15|20,20,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|SUST_INT||RAND_POWER|ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|INT=0+1d3M0|INT=0|||||-|0:0
of Might|7500|19|10,40,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|FREE_ACT,SUST_CON,SUST_DEX,SUST_STR|||ACID,COLD,ELEC,FIRE|||CON=0+1d3M0,DEX=0+1d3M0,STR=0+1d3M0|CON=1,DEX=1,STR=1|||||-|0:0
of Lordliness|7500|17|20,40,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|PROT_FEAR,SUST_WIS||RAND_POWER|ACID,COLD,ELEC,FIRE|||WIS=0+1d3M0|WIS=1|||||-|0:0
of Seeing|2000|8|50,10,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|PROT_BLIND,SEE_INVIS||||||SEARCH=0+1d5M0|SEARCH=0|||||-|0:0
of Infravision|500|11|100,1,20|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|||||||INFRA=0+1d5M0|INFRA=0|||||-|0:0
of Light|1000|6|50,5,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4||||||LIGHT=1|LIGHT=1+0d0M0|LIGHT=1|||||-|0:0
of Telepathy|50000|20|10,40,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|TELEPATHY||||||||||||-|0:0
of Regeneration|1500|10|20,1,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|REGEN||||||||||||-|0:0
of Serenity|4000|20|15,20,127|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|PROT_CONF,PROT_FEAR,PROT_STUN|||||SOUND=1|||||||-|0:0
of Night and Day|4000|18|15,10,80|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|PROT_BLIND,SEE_INVIS|||ACID||DARK=1,LIGHT=1|LIGHT=1+0d0M0|LIGHT=1|||||-|0:0
of Resist Acid|1000|16|100,1,30|255,0,0|0+0d0M0;0+0d0M0;0+0d0M0|11||||ACID||ACID=1|||||||-|0:0
of Resist Lightning|400|10|100,1,30|255,0,0|0+0d0M0;0+0d0M0;0+0d0M0|11||||ELEC||ELEC=1|||||||-|0:0
of Resist Fire|800|14|100,1,30|255,0,0|0+0d0M0;0+0d0M0;0+0d0M0|11||||FIRE||FIRE=1|||||||-|0:0
of Resist Cold|600|12|100,1,30|255,0,0|0+0d0M0;0+0d0M0;0+0d0M0|11||||COLD||COLD=1|||||||-|0:0
of Resistance|12500|20|50,10,100|255,0,0|0+0d0M0;0+0d0M0;0+1d10M0|17||||ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|||||||-|0:0
of Elvenkind|15000|25|10,30,127|255,0,0|0+0d0M0;0+0d0M0;0+1d10M0|17|||RAND_HI_RES|ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|STEALTH=0+1d2M0|STEALTH=0|||||-|0:0
of Permanence|30000|30|10,30,127|255,255,255|0+0d0M0;0+0d0M0;0+1d20M0|1|HOLD_LIFE,SUST_CON,SUST_DEX,SUST_INT,SUST_STR,SUST_WIS||RAND_HI_RES|ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|||||||-|0:0
(Dwarven)|5000|18|20,20,127|255,0,0|0+0d0M0;0+0d0M0;0+1d15M0|12|FREE_ACT|||ACID,FIRE|||CON=0+1d2M0,INFRA=0+1d2M3,STR=0+1d2M0|CON=1,INFRA=1,STR=1|||||-|0:0
of Protection|1500|10|30,1,40|255,255,255|0+0d0M0;0+0d0M0;0+1d10M0|4||||ACID,COLD,ELEC,FIRE||SHARD=1|||||||-|0:0
of Stealth|500|10|60,1,70|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|4|||||||STEALTH=0+1d3M0|STEALTH=0|||||-|0:0
of Aman|4000|20|10,20,127|255,255,255|0+0d0M0;0+0d0M0;0+1d20M0|4|||RAND_HI_RES|ACID,COLD,ELEC,FIRE|||STEALTH=0+1d3M0|STEALTH=0|||||-|0:0
of the Magi|2000|15|10,30,127|255,255,255|0+0d0M0;0+0d0M0;0+1d4M0|4|SUST_INT||RAND_POWER|ACID|||INT=0+1d2M0,STEALTH=0+1d3M0|INT=1,STEALTH=1|||||-|0:0
of Free Action|1000|11|100,1,60|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|5|FREE_ACT||||||||||||-|0:0
of Slaying|1500|17|20,10,75|255,255,255|0+1d5M0;0+1d5M0;0+0d0M0|5|||||||||||||-|41:3800
of Agility|1000|14|10,20,50|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|5|||||||DEX=0+1d5M0|DEX=0|||||-|0:0
of Power|2500|22|5,30,127|255,255,255|0+1d5M0;0+1d5M0;0+0d0M0|5|||||||STR=0+1d5M0|STR=0|||||-|0:0
of Thievery|5000|22|5,40,127|255,255,255|0+1d8M0;0+1d3M0;0+0d0M0|2|FEATHER,FREE_ACT||||||DEX=1+0d0M4,SEARCH=1+1d2M3|DEX=1,SEARCH=2|||||-|0:0
of Combat|7000|22|5,50,127|255,255,255|0+1d5M0;0+1d8M0;0+0d0M0|3|AGGRAVATE||||||CON=0+1d2M0,STR=0+1d2M0|CON=1,STR=1|||||-|0:0
(Dwarven)|5000|20|20,40,127|255,255,255|1+0d0M3;1+0d0M3;0+1d5M0|1|FREE_ACT|||ACID,FIRE|||CON=0+1d2M0,STR=0+1d2M0|CON=1,STR=1|||||-|0:0
of Resist Acid|1000|16|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6||||ACID||ACID=1|||||||-|0:0
of Resist Lightning|400|10|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6||||ELEC||ELEC=1|||||||-|0:0
of Resist Fire|800|14|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6||||FIRE||FIRE=1|||||||-|0:0
of Resist Cold|600|12|100,1,30|255,255,255|0+0d0M0;0+0d0M0;0+0d0M0|6||||COLD||COLD=1|||||||-|0:0
of Resistance|12500|20|50,10,100|255,255,255|0+0d0M0;0+0d0M0;0+1d10M0|6||||ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|||||||-|0:0
of Elvenkind|18000|25|10,30,127|255,255,255|0+0d0M0;0+0d0M0;0+1d10M0|6|||RAND_HI_RES|ACID,COLD,ELEC,FIRE||ACID=1,COLD=1,ELEC=1,FIRE=1|STEALTH=0+1d2M0|STEALTH=0|||||-|0:0
of Preservation|24000|25|5,40,127|255,255,255|0+0d0M0;0+0d0M0;0+1d20M0|6|HOLD_LIFE,SUST_CON,SUST_DEX,SUST_STR||RAND_HI_RES|ACID,COLD,ELEC,FIRE||DISEN=1|||||||-|0:0
of Craftsmanship|500|4|60,20,95|0,0,4|0+0d0M0;0+0d0M0;0+1d6M4|12|||||||||||||-|55:5318
of Stealth|500|16|10,20,100|255,0,0|0+0d0M0;0+0d0M0;0+0d0M0|12|||||||STEALTH=0+1d2M0|STEALTH=0|||||-|0:0
of Resistance|12500|10|5,40,100|255,0,0|0+0d0M0;0+0d0M0;0+1d10M0|5||||||ACID=1,COLD=1,ELEC=1,FIRE=1|||||||-|0:0
of Elvenkind|15000|25|3,50,127|255,255,255|0+0d0M0;0+0d0M0;0+1d10M0|5|||RAND_HI_RES|||ACID=1,COLD=1,ELEC=1,FIRE=1|STEALTH=0+1d2M0|STEALTH=0|||||-|0:0
(Dwarven)|5000|18|5,40,127|255,0,5|0+0d0M0;0+0d0M0;0+1d15M0|12|FREE_ACT||||||CON=0+1d2M0,INFRA=0+1d2M3,STR=0+1d2M0|CON=1,INFRA=1,STR=1|||||-|0:0
of Speed|100000|25|2,40,127|255,0,0|0+0d0M0;0+0d0M0;0+0d0M0|12|||||||SPEED=0+1d5M0|SPEED=0|||||-|0:0
""";
}
