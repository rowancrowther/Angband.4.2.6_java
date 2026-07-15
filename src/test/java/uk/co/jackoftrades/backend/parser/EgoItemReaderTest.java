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
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.CurseData;
import uk.co.jackoftrades.middle.objects.EgoItem;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
 * <p>{@link EgoItem} exposes no getters, so assertions read its private fields reflectively via
 * {@link #field}.
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
     *
     * @author Rowan Crowther
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
     * {@link GameConstants} does not leak into other test suites.
     *
     * @author Rowan Crowther
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
        Field f = GameConstants.class.getDeclaredField(field);
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
        int swordKinds = GameConstants.lookupObjectKind(TValue.TV_SWORD).size();
        assertTrue(swordKinds > 1, "fixture assumes several sword kinds exist");
        assertEquals(swordKinds, poss.size());
        assertTrue(poss.stream().allMatch(k -> k.gettValue() == TValue.TV_SWORD));
    }

    @Test
    void multipleTypeLinesAccumulateIntoOneList() throws Exception {
        EgoItem e = loadOne("types.txt", "type:sword\ntype:hafted\n");

        List<ObjectKind> poss = field(e, "possItems");
        int expected = GameConstants.lookupObjectKind(TValue.TV_SWORD).size()
                + GameConstants.lookupObjectKind(TValue.TV_HAFTED).size();
        assertEquals(expected, poss.size());
    }

    @Test
    void itemLineResolvesExactlyOneKindBySval() throws Exception {
        EgoItem e = loadOne("item.txt", "item:light:Lantern\n");

        List<ObjectKind> poss = field(e, "possItems");
        assertEquals(1, poss.size());
        assertEquals("Lantern", poss.get(0).getsValueName());
        assertSame(GameConstants.lookupObjectKind(TValue.TV_LIGHT, "Lantern"), poss.get(0));
    }

    @Test
    void typeAndItemLinesMergeIntoTheSameList() throws Exception {
        EgoItem e = loadOne("merge.txt", "type:hafted\nitem:light:Lantern\n");

        List<ObjectKind> poss = field(e, "possItems");
        int haftedKinds = GameConstants.lookupObjectKind(TValue.TV_HAFTED).size();
        assertEquals(haftedKinds + 1, poss.size());
        assertTrue(poss.stream().anyMatch(k -> "Lantern".equals(k.getsValueName())));
    }

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void unknownTypeTvalIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-type.txt",
                withHeader(1, ego("weird", "type:no such tval\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown type TValue")),
                result.errors()::toString);
    }

    @Test
    void unknownItemTvalIsReportedAndSkipped() throws IOException {
        ParseResult<EgoItem> result = load("bad-item-tval.txt",
                withHeader(1, ego("weird", "item:no such tval:Whatever\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("illegal TValue in its item")),
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
        // The port stores svals as names, not C's int indices, so a numeric sval matches
        // no kind and must be rejected rather than silently resolved.
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

    // ---- brand / slay / curse --------------------------------------------

    @Test
    void brandAndSlayAndCurseResolveAgainstTheirRegistries() throws Exception {
        EgoItem e = loadOne("bsc.txt",
                "type:sword\nbrand:ACID_3\nslay:EVIL_2\ncurse:vulnerability:10\n");

        Map<?, ?> brands = field(e, "brands");
        Map<?, ?> slays = field(e, "slays");
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

    @Test
    void unknownActivationIsReportedAndSkipped() throws IOException {
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
}
