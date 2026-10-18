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
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link ArtifactReader}: file text -> {@code ArtifactLexer}/
 * {@code ArtifactGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord}
 * -> {@link uk.co.jackoftradesltd.backend.parser.artifact.ArtifactAssembler} -> {@link Artifact}.
 *
 * <p>The artifact assembler resolves four registries through {@link ObjectRegistry} — brands by code,
 * slays by code, curses by name and activations by name — so rather than run the whole heavy
 * {@code GameConstants.init()}, {@link #seed()} loads each dependency directly through its own reader
 * and injects it into the private static registries by reflection, restoring them afterwards so no
 * global state leaks into other suites.
 *
 * <p>The suite is the three tests that were asked for:
 * <ol>
 *   <li><b>The happy path</b> — the real {@code artifact.txt} loads all 138 artifacts with no
 *       errors (itself proof that every brand/slay/curse/activation cross-reference resolved),
 *       plus field-level checks on the Phial of Galadriel and on the two records that exercise
 *       the trickier legs: element resistances and a curse.</li>
 *   <li><b>Every reachable error site in one file</b> — one artifact record per place the pipeline
 *       can append a soft error, plus a deliberately wrong record-count header.</li>
 *   <li><b>Two soft errors</b> — two bad flags on a single record, pinning the assembler's
 *       "collect every failure in the loop, then drop the record" contract rather than fail-fast.</li>
 * </ol>
 *
 * <p><b>What is deliberately not tested:</b> the assembler guards every numeric field with a
 * {@code NumberFormatException} catch (to-hit, to-dam, AC, weight, cost, level, alloc, curse power,
 * value integers), but each of those is lexed as an integer token (ARTIFACT_INTEGER / ALLOC_INT /
 * DELIM_INTEGER / VALUES_INTEGER), so a non-numeric value is rejected by the <em>grammar</em> as a
 * hard syntax error and never reaches the {@code parseInt}. The malformed-{@code time:} guard is
 * unreachable for the same reason — {@code time:} only accepts SIMPLE_DICE_STRING or
 * ARTIFACT_INTEGER, both of which {@link Random#parseStr} accepts. Those catches are defensive and
 * cannot be exercised through a data file.
 *
 * <p>The older assertions here read {@link Artifact}'s private fields reflectively via {@link #field};
 * the tests added on 261017 use its getters.
 *
 * <p>The second half pins the reader's own contract against {@code artifact_parser} in
 * {@code obj-init.c}: a missing file, the list in file order with every field of the 138 shipped
 * artifacts worked out from the upstream {@code artifact.txt} by a separate parser (the table at the
 * foot of the class), which repeated directives overwrite and which accumulate, the error cases C
 * shares, and each place the grammar or assembler differs from C. The differences are written up in
 * {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}.
 *
 * <p>Class ArtifactReaderTest coded before 261017, reader-contract tests added and commented in full
 * on 261017.
 *
 * @author Rowan Crowther
 */
class ArtifactReaderTest {

    private static final String ARTIFACT_FILE = "lib/gamedata/artifact.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";
    private static final String OBJECT_FILE = "lib/gamedata/object.txt";
    private static final String SHAPE_FILE = "lib/gamedata/shape.txt";
    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String ACTIVATION_FILE = "lib/gamedata/activation.txt";

    /**
     * The size of the kind table after object.txt alone, i.e. the value {@code objectBaseKindMax}
     * takes in a real load. Every kind at or above this index is one the artifact assembler
     * synthesised.
     */
    private static final int ORDINARY_KIND_MAX = 375;

    /**
     * The 14 artifacts whose {@code base-object:} sval does not resolve against object.txt, and so
     * must have a kind minted for them. Exactly the 14 records carrying a {@code graphics:} line.
     */
    private static final int SYNTHESISED_KINDS = 14;

    private static Object savedPains, savedBases, savedSummons, savedSlays,
            savedBrands, savedObjectBases, savedCurses, savedActivations,
            savedObjectKinds, savedKindsByTvalSval, savedObjectBaseKindMax, savedPlayerShapes;

    /**
     * The one shared load of the real file; the happy-path test asserts against this.
     */
    private static ParseResult<Artifact> result;

    @TempDir
    Path tempDir;

    /**
     * Seeds the registries the artifact assembler resolves against, then loads the real
     * {@code artifact.txt} once for the happy-path test.
     */
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
        savedActivations = setStatic("activations", new ActivationReader().parse(ACTIVATION_FILE));

        // object.txt's effects include EST_SHAPECHANGE, whose subtype resolves against playerShapes.
        savedPlayerShapes = setStatic("playerShapes", new ShapeReader().parse(SHAPE_FILE));

        // The assembler resolves each base-object: against the kind table and mints a kind when the
        // sval does not resolve, so object.txt has to be loaded and registered exactly as
        // GameConstants.loadItemObjects() does it -- addObjectKind is what allocates the sval and
        // the kind index the synthesis path then gates on.
        savedObjectKinds = setStatic("objectKinds", new ArrayList<ObjectKind>());
        savedKindsByTvalSval = setStatic("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        for (ObjectKind kind : new ItemObjectReader().parse(OBJECT_FILE)) {
            ObjectRegistry.addObjectKind(kind);
        }
        savedObjectBaseKindMax = setStatic("objectBaseKindMax", ObjectRegistry.getObjectKindCount());

        result = new ArtifactReader().parseWithResults(ARTIFACT_FILE);
    }

    /**
     * Restores the registries mutated by {@link #seed()} so the shared static state on
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
        setStatic("activations", savedActivations);
        setStatic("objectKinds", savedObjectKinds);
        setStatic("kindsByTvalSval", savedKindsByTvalSval);
        setStatic("objectBaseKindMax", savedObjectBaseKindMax);
        setStatic("playerShapes", savedPlayerShapes);
    }

    // ---- fixture + reflection helpers ------------------------------------

    /**
     * Writes {@code content} to a temp file and drives it through the reader.
     */
    private ParseResult<Artifact> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new ArtifactReader().parseWithResults(file.toString());
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * True if any collected error contains {@code fragment}.
     */
    private static boolean hasError(ParseResult<Artifact> r, String fragment) {
        return r.errors().stream().anyMatch(s -> s.contains(fragment));
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object target, String name) throws Exception {
        Field f = Artifact.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    /**
     * As {@link #field}, but for {@link ObjectKind} — which exposes no {@code getTime()}.
     */
    @SuppressWarnings("unchecked")
    private static <T> T kindField(ObjectKind target, String name) throws Exception {
        Field f = ObjectKind.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    private static Artifact byName(String name) throws Exception {
        for (Artifact a : result.items()) {
            if (name.equals(field(a, "name"))) return a;
        }
        return null;
    }

    // =====================================================================
    // (1) HAPPY PATH
    // =====================================================================

    @Test
    void theRealFileLoadsAll138ArtifactsWithNoErrorsAndTheirFieldsAreCorrect() throws Exception {
        // A clean load is the end-to-end proof: any unresolved brand, slay, curse or activation
        // would have appended a soft error and dropped the artifact rather than thrown.
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(138, result.items().size());

        // ---- the Phial of Galadriel: the first record, and a full spread of field types ----
        Artifact phial = byName("of Galadriel");
        assertNotNull(phial);

        assertSame(TValue.TV_LIGHT, field(phial, "tValue"));   // base-object:light:Phial
        assertEquals("Phial", field(phial, "sValue"));
        assertEquals(5, (int) field(phial, "level"));          // level:5
        assertEquals(10, (int) field(phial, "weight"));        // weight:10
        assertEquals(10000, (int) field(phial, "cost"));       // cost:10000
        assertEquals(40, (int) field(phial, "allocProb"));     // alloc:40:5 to 100
        assertEquals(5, (int) field(phial, "allocMin"));
        assertEquals(100, (int) field(phial, "allocMax"));
        assertEquals("1d1", field(phial, "diceString"));       // attack:1d1:0:0
        assertEquals(0, (int) field(phial, "toHit"));
        assertEquals(0, (int) field(phial, "toDam"));
        assertEquals(0, (int) field(phial, "ac"));             // armor:0:0
        assertEquals(0, (int) field(phial, "toAC"));

        Flag<ObjectFlag> flags = field(phial, "flags");        // flags:NO_FUEL
        assertTrue(flags.has(ObjectFlag.OF_NO_FUEL));

        Map<ObjectModifier, Integer> modifiers = field(phial, "modifiers");  // values:LIGHT[4]
        assertEquals(4, (int) modifiers.get(ObjectModifier.OM_LIGHT));

        // act:/time: on a SPECIAL light are a property of the base object, not the artifact:
        // C's parse_artifact_act/_time write k-> instead of a-> when tval is TV_LIGHT and the kind
        // was synthesised (obj-init.c, parse_artifact_act and parse_artifact_time). It is either/or, so the artifact keeps neither.
        assertNull(field(phial, "activation"), "special light: activation belongs to the kind");
        assertNull(field(phial, "time"), "special light: time belongs to the kind");

        ObjectKind phialKind = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Phial");
        assertNotNull(phialKind, "the Phial's kind is synthesised, so lookup by sval name finds it");
        assertEquals(1, phialKind.getActivations().size());
        assertEquals("ILLUMINATION", phialKind.getActivations().get(0).getName());
        assertNotNull(kindField(phialKind, "time"), "time:10+d10 parsed to a Random on the kind");

        assertTrue(((String) field(phial, "text"))
                .contains("light of Eärendil's Star"));   // desc: accumulates

        // ---- Angrist: the brand/slay leg resolved against the seeded registries ----
        Artifact angrist = byName("'Angrist'");
        assertNotNull(angrist);
        Set<Brand> brands = field(angrist, "brands");          // brand:ACID_3
        Set<Slay> slays = field(angrist, "slays");             // slay:EVIL_2 / TROLL_3 / ORC_3
        assertEquals(1, brands.size());
        assertTrue(brands.stream().anyMatch(b -> b.getCode().equals("ACID_3")));
        assertEquals(3, slays.size());

        // ---- Calris: the two legs the port most recently fixed — RES_* element info, and a
        // curse whose power lexes as an integer rather than a string.
        Artifact calris = byName("'Calris'");
        assertNotNull(calris);

        // values:CON[5] | RES_DISEN[1] -> the two-family split: CON to modifiers, RES_ to el_info.
        Map<ObjectModifier, Integer> calrisMods = field(calris, "modifiers");
        assertEquals(5, (int) calrisMods.get(ObjectModifier.OM_CON));
        Map<ElementEnum, ElementInfo> elInfo = field(calris, "elInfo");
        assertNotNull(elInfo.get(ElementEnum.ELEM_DISEN));
        assertEquals(1, elInfo.get(ElementEnum.ELEM_DISEN).getResLevel());
        assertFalse(calrisMods.containsKey(ObjectModifier.OM_CON) && elInfo.isEmpty());

        // curse:air swing:30 -- the one curse line in the file. C keeps `int *curses` indexed by
        // curse holding the power (obj-init.c, parse_artifact_curse); the port keys by Curse and carries the
        // power in the entry's CurseData, whose timeout stays 0 until the item is in the world.
        Map<Curse, CurseData> curses = field(calris, "curses");
        assertEquals(1, curses.size());
        Curse curse = curses.keySet().iterator().next();
        assertEquals("air swing", curse.getName());
        assertEquals(30, curses.get(curse).getPower());
        assertEquals(0, curses.get(curse).getTimeout(), "timeout is runtime-only");
    }

    // =====================================================================
    // (1a) EVERY ARTIFACT IGNORES THE BASE ELEMENTS
    // =====================================================================

    /**
     * C's parse_artifact_name ends by setting EL_INFO_IGNORE across ELEM_BASE_MIN..ELEM_HIGH_MIN
     * (obj-init.c, parse_artifact_name) — an exclusive range that is exactly ACID/ELEC/FIRE/COLD
     * (object.h), poison excluded. Artifacts cannot be burnt, frozen, melted or shocked away.
     *
     * <p>The port filters on {@link ElementEnum#isBase()} rather than the C index range, which is
     * necessary as well as tidier: the port's enum has ELEM_NONE at ordinal 0 where C's ELEM_ACID is
     * 0, so a literal index port would be off by one.
     *
     * <p>The Phial has no base element in its values: line, so all four entries here exist only
     * because of that sweep.
     */
    @Test
    void everyArtifactIgnoresTheFourBaseElements() throws Exception {
        Artifact phial = byName("of Galadriel");
        assertNotNull(phial);
        Map<ElementEnum, ElementInfo> elInfo = field(phial, "elInfo");

        for (ElementEnum e : ElementEnum.values()) {
            if (!e.isBase()) continue;
            ElementInfo info = elInfo.get(e);
            assertNotNull(info, () -> e + " must have an el_info entry");
            assertTrue(info.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE),
                    () -> e + " must be ignored");
        }

        // The range is exclusive of ELEM_POIS: poison is a "high" element, never swept.
        assertNull(elInfo.get(ElementEnum.ELEM_POIS), "POIS is not a base element");
    }

    /**
     * The merge case, and the reason the sweep must use computeIfAbsent rather than overwrite: C
     * ORs the ignore flag into the same el_info[] slot the values: line writes its resist level to.
     * Angrist declares values:DEX[4] | RES_ACID[1] | RES_DARK[1] (artifact.txt:189), so ACID must
     * carry BOTH a res level and the ignore flag on one entry, while DARK — not a base element —
     * gets its res level and no flag.
     */
    @Test
    void aBaseElementResistMergesWithTheIgnoreSweepRatherThanReplacingIt() throws Exception {
        Artifact angrist = byName("'Angrist'");
        assertNotNull(angrist);
        Map<ElementEnum, ElementInfo> elInfo = field(angrist, "elInfo");

        ElementInfo acid = elInfo.get(ElementEnum.ELEM_ACID);
        assertNotNull(acid);
        assertEquals(1, acid.getResLevel(), "RES_ACID[1] survives the sweep");
        assertTrue(acid.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE), "and is still ignored");

        ElementInfo dark = elInfo.get(ElementEnum.ELEM_DARK);
        assertNotNull(dark);
        assertEquals(1, dark.getResLevel());
        assertFalse(dark.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE),
                "DARK is not a base element, so the sweep must not touch it");
    }

    // =====================================================================
    // (1b) SPECIAL-ARTIFACT KIND SYNTHESIS
    // =====================================================================

    /**
     * C's write_dummy_object_record (obj-init.c) mints an object kind for an artifact whose
     * base-object sval does not resolve; detection is not a flag but the lookup_sval failure itself
     * (parse_artifact_base_object). In artifact.txt that is exactly the 14 records carrying a graphics: line — 3
     * lights, 6 rings, 5 amulets — so the kind table must grow by 14 and no more.
     *
     * <p>A clean 138-artifact load cannot see any of this: it passes whether synthesis mints 14
     * kinds or 138.
     */
    @Test
    void exactlyTheFourteenUnresolvableBaseObjectsGetASynthesisedKind() {
        assertEquals(ORDINARY_KIND_MAX, ObjectRegistry.getObjectBaseKindMax(),
                "object.txt's own kind count, i.e. C's ordinary_kind_max");

        List<ObjectKind> synthesised = ObjectRegistry.getObjectKinds().stream()
                .filter(k -> k.getKindIndex() >= ORDINARY_KIND_MAX)
                .toList();

        assertEquals(SYNTHESISED_KINDS, synthesised.size(),
                () -> "synthesised: " + synthesised.stream().map(ObjectKind::getName).toList());
        assertEquals(ORDINARY_KIND_MAX + SYNTHESISED_KINDS, ObjectRegistry.getObjectKindCount());

        // kf_on(dummy->kind_flags, KF_INSTA_ART) -- obj-init.c, write_dummy_object_record.
        for (ObjectKind k : synthesised) {
            assertTrue(k.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART),
                    () -> k.getName() + " must be INSTA_ART");
        }
    }

    /**
     * The Phial is the fully-worked case: an unresolvable sval, so its kind is minted, named from
     * the <em>sval</em> not the artifact name (strnfmt("& %s~", name), obj-init.c — the artifact
     * is only "of Galadriel"), given the artifact's level/weight/cost, and its graphics: line
     * overrides the '*'/red default.
     */
    @Test
    void thePhialsSynthesisedKindTakesItsNameFromTheSvalAndItsFieldsFromTheArtifact() throws Exception {
        ObjectKind phial = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Phial");
        assertNotNull(phial);

        assertEquals("& Phial~", phial.getName(), "templated from the base-object sval, not the artifact");
        assertEquals("Phial", phial.getsValueName(), "what lookupObjectKind(tval, sval) matches on");
        assertTrue(phial.getKindIndex() >= ORDINARY_KIND_MAX, "synthesised kinds sit above ordinary_kind_max");
        assertTrue(phial.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART));
        assertEquals(TValue.TV_LIGHT, phial.gettValue());
        assertEquals(5, (int) kindField(phial, "level"));      // artifact.txt level:5
        assertEquals(10, (int) kindField(phial, "weight"));    // weight:10 -- copied: kidx >= max
        assertEquals(10000, (int) kindField(phial, "cost"));   // cost:10000

        // graphics:~:y -- gates on kf_has(KF_INSTA_ART) (obj-init.c, parse_artifact_graphics), overriding '*'/red.
        AngbandDisplayCharacter glyph = kindField(phial, "character");
        assertEquals('~', glyph.getCharacter());
    }

    /**
     * Grond is the trap the two gates exist to avoid. Its base-object (hafted:Mighty Hammer) DOES
     * resolve, and object.txt:5287 declares INSTA_ART on it — so it is INSTA_ART yet NOT synthesised,
     * and sits below ordinary_kind_max. weight:/cost: gate on kidx (obj-init.c, parse_artifact_weight and parse_artifact_cost), not on
     * the flag, so Grond's artifact weight:1000/cost:500000 must NOT reach the shared kind, which
     * keeps object.txt's weight:200/cost:1000. Collapsing the two gates clobbers it silently.
     */
    @Test
    void grondIsInstaArtButNotSynthesisedSoItsKindKeepsObjectTxtsWeightAndCost() throws Exception {
        ObjectKind hammer = ObjectRegistry.lookupObjectKind(TValue.TV_HAFTED, "Mighty Hammer");
        assertNotNull(hammer);

        assertTrue(hammer.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART), "declared in object.txt");
        assertTrue(hammer.getKindIndex() < ORDINARY_KIND_MAX, "an ordinary kind, not synthesised");
        assertEquals(200, (int) kindField(hammer, "weight"), "object.txt's weight, NOT the artifact's 1000");
        assertEquals(1000, (int) kindField(hammer, "cost"), "object.txt's cost, NOT the artifact's 500000");
    }

    // =====================================================================
    // (2) EVERY REACHABLE ERROR SITE, IN ONE FILE
    // =====================================================================

    @Test
    void everyReachableErrorSiteIsReportedAndTheBadArtifactDropped() throws Exception {
        // One record per soft-error site in ArtifactAssembler, all in a single file, so the run also
        // proves the assembler carries on to the next record after dropping a bad one. The header
        // deliberately declares 99 against the 8 records present, which additionally exercises the
        // GrammarDriver record-count check (a soft error raised outside the assembler).
        ParseResult<Artifact> r = load("all-errors.txt", withHeader(99,
                "name:BadTval\nbase-object:notatval:Thing\n"
                        + "name:BadFlag\nflags:NOTAFLAG\n"
                        + "name:BadModifier\nvalues:NOTAMOD[1]\n"
                        + "name:BadElement\nvalues:RES_NOTANELEM[1]\n"
                        + "name:BadBrand\nbrand:NOTABRAND\n"
                        + "name:BadSlay\nslay:NOTASLAY\n"
                        + "name:BadCurse\ncurse:notacurse:10\n"
                        + "name:BadActivation\nact:NOTANACTIVATION\n"));

        // Every record here is malformed, so every one is dropped.
        assertTrue(r.items().isEmpty(), r.items()::toString);

        assertTrue(hasError(r, "unknown base-obj tvalue: notatval"), r.errors()::toString);
        assertTrue(hasError(r, "unknown flag: NOTAFLAG"), r.errors()::toString);
        assertTrue(hasError(r, "unknown modifier/element name: NOTAMOD"), r.errors()::toString);
        // RES_ is stripped before the ELEM_ lookup, so the message echoes the bare element name.
        assertTrue(hasError(r, "unknown modifier/element name: NOTANELEM"), r.errors()::toString);
        assertTrue(hasError(r, "unknown brand: NOTABRAND"), r.errors()::toString);
        assertTrue(hasError(r, "unknown slay: NOTASLAY"), r.errors()::toString);
        assertTrue(hasError(r, "unknown curse: notacurse"), r.errors()::toString);
        // The activation message does not echo the offending name (it interpolates the resolved
        // Activation, which is null on this branch), so only the site itself can be asserted.
        assertTrue(hasError(r, "unknown activation"), r.errors()::toString);

        assertTrue(hasError(r, "record-count header declares 99"), r.errors()::toString);
    }

    // =====================================================================
    // (3) TWO SOFT ERRORS
    // =====================================================================

    @Test
    void twoBadFlagsOnOneArtifactAreBothReported() throws IOException {
        // The flag loop must not fail-fast: given two bad flags on a single record it collects BOTH
        // before dropping the artifact. This pins the "keep going, report every failure" contract —
        // the same shape the values/brand/slay/curse loops use.
        ParseResult<Artifact> r = load("two-bad-flags.txt", withHeader(1,
                "name:DoublyBad\nflags:NOTAFLAG | ALSONOTAFLAG\n"));

        assertTrue(r.items().isEmpty());
        assertEquals(2, r.errors().size(), r.errors()::toString);
        assertTrue(hasError(r, "unknown flag: NOTAFLAG"), r.errors()::toString);
        assertTrue(hasError(r, "unknown flag: ALSONOTAFLAG"), r.errors()::toString);
    }

    // =====================================================================
    // (4) THE READER'S OWN CONTRACT AGAINST artifact_parser IN obj-init.c
    // =====================================================================

    /**
     * The minimal {@code base-object:} line the contract tests use: an ordinary kind in
     * {@code object.txt}, so no kind is minted and the registry is left as {@link #seed()} made it.
     */
    private static final String BASE = "base-object:sword:Dagger\n";

    /**
     * The two artifacts whose {@code desc:} text this repository's {@code artifact.txt} gives without
     * the colon the upstream file has ("war-cry: ", "echo: "), one character shorter than the table's
     * length, which is the upstream one.
     */
    private static final Set<String> SHIPPED_COPY_DROPS_A_COLON = Set.of("of Húrin", "of Tuor");

    private static List<String> names(ParseResult<Artifact> r) {
        return r.items().stream().map(Artifact::getName).toList();
    }

    private void assertHardError(String fileName, String content) throws IOException {
        ParseResult<Artifact> r = load(fileName, content);
        assertTrue(r.items().isEmpty(), () -> r.items().toString());
        assertFalse(r.errors().isEmpty(), "a hard error has to say why");
    }

    /**
     * Loads one artifact named X on the Dagger kind with {@code body} after its base line, and
     * insists on a clean result.
     */
    private Artifact loadClean(String fileName, String body) throws IOException {
        ParseResult<Artifact> r = load(fileName, withHeader(1, "name:X\n" + BASE + body));
        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(1, r.items().size());
        return r.items().get(0);
    }

    private static String flagNames(Artifact a) {
        return Arrays.stream(ObjectFlag.values()).filter(f -> a.getFlags().has(f))
                .map(f -> f.name().substring(3)).sorted().collect(Collectors.joining(","));
    }

    private static String valueList(Artifact a) {
        List<String> values = new ArrayList<>();
        a.getModifiers().forEach((m, v) -> values.add(m.name().substring(3) + "=" + v));
        a.getElInfo().forEach((e, info) -> {
            if (info.getResLevel() != 0) values.add("RES_" + e.name().substring(5) + "=" + info.getResLevel());
        });
        Collections.sort(values);
        return String.join(",", values);
    }

    private static String dice(Random r) {
        return r == null ? "" : r.getBase() + "+" + r.getDice() + "d" + r.getSides();
    }

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        // parse_file_quit_not_found calls quit() on a file that cannot be opened; here it surfaces
        // as an exception for the caller to treat as fatal.
        String missing = tempDir.resolve("absent.txt").toString();
        assertThrows(IOException.class, () -> new ArtifactReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new ArtifactReader().parse(missing));
    }

    @Test
    void parseReturnsTheSameArtifactsAsParseWithResultsInFileOrder() throws Exception {
        List<Artifact> viaParse = new ArtifactReader().parse(ARTIFACT_FILE);

        assertEquals(138, viaParse.size());
        assertEquals("of Galadriel", viaParse.get(0).getName());
        assertEquals("of Morgoth", viaParse.get(137).getName());
        for (int i = 0; i < viaParse.size(); i++) {
            assertEquals(result.items().get(i).getName(), viaParse.get(i).getName(), "record " + i);
        }
    }

    @Test
    void parseHidesASyntaxErrorAsAnEmptyListWhereParseWithResultsReportsIt() throws IOException {
        Path file = tempDir.resolve("unknown-directive.txt");
        Files.writeString(file, withHeader(1, "name:X\n" + BASE + "foo:bar\n"));

        assertTrue(new ArtifactReader().parse(file.toString()).isEmpty());
        assertTrue(new ArtifactReader().parseWithResults(file.toString()).hasErrors());
    }

    @Test
    void theShippedFileDeclaresItsRecordCountAndAMiscountKeepsTheRecords() throws IOException {
        // The header is this repository's addition; upstream artifact.txt has none. It must equal the
        // number of name: lines or every normal load would report a miscount.
        List<String> lines = Files.readAllLines(Path.of(ARTIFACT_FILE));
        assertEquals(1, lines.stream().filter(l -> l.startsWith("record-count:")).count());
        assertTrue(lines.contains("record-count:138"));
        assertEquals(138, lines.stream().filter(l -> l.startsWith("name:")).count());

        ParseResult<Artifact> r = load("miscount.txt", withHeader(5, "name:A\n" + BASE));
        assertEquals(List.of("A"), names(r));
        assertTrue(hasError(r, "record-count header declares 5 records, but file contains 1"), r.errors()::toString);
    }

    /**
     * Every artifact of the shipped file, in file order, against a table worked out from the upstream
     * {@code artifact.txt} with a separate parser following the {@code parse_artifact_*} handlers in
     * {@code obj-init.c}: scalars take the last line, {@code flags:} and {@code values:} accumulate
     * through {@code strtok} on {@code " |"}, {@code msg:} and {@code desc:} append with no separator,
     * and the {@code RES_} entries of {@code values:} go to {@code el_info}. The columns are name,
     * tval, sval, level, weight, cost, alloc chance, alloc min, alloc max, attack dice, to-hit,
     * to-dam, AC, to-AC, flags, values, brands, slays, curses, activation, time as base+dice d sides,
     * message, length of the description, glyph and colour letter. A special light keeps its
     * activation and time on its kind, as {@code parse_artifact_act} and {@code parse_artifact_time}
     * put them there, so those two columns are read from the kind for the three of them.
     */
    @Test
    void theShippedFileMatchesTheTableWorkedOutFromTheUpstreamFile() throws Exception {
        List<String> rows = EXPECTED_TABLE.lines().toList();
        Map<String, String> colours = Map.of("y", "COLOUR_YELLOW", "d", "COLOUR_DARK",
                "g", "COLOUR_GREEN", "w", "COLOUR_WHITE");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(138, rows.size());
        assertEquals(rows.size(), result.items().size());
        for (int i = 0; i < rows.size(); i++) {
            String[] c = rows.get(i).split("\\|", -1);
            assertEquals(25, c.length, rows.get(i));
            Artifact a = result.items().get(i);
            String who = "#" + i + " " + c[0] + ": ";

            assertEquals(c[0], a.getName(), who + "name");
            assertSame(TValue.valueOf(c[1]), a.gettValue(), who + "tval");
            assertEquals(c[2], a.getsValue(), who + "sval");
            assertEquals(Integer.parseInt(c[3]), a.getLevel(), who + "level");
            assertEquals(Integer.parseInt(c[4]), a.getWeight(), who + "weight");
            assertEquals(Integer.parseInt(c[5]), a.getCost(), who + "cost");
            assertEquals(Integer.parseInt(c[6]), a.getAllocProb(), who + "alloc chance");
            assertEquals(Integer.parseInt(c[7]), a.getAllocMin(), who + "alloc min");
            assertEquals(Integer.parseInt(c[8]), a.getAllocMax(), who + "alloc max");
            assertEquals(c[9], a.getDiceString(), who + "attack dice");
            assertEquals(Integer.parseInt(c[10]), a.getToHit(), who + "to-hit");
            assertEquals(Integer.parseInt(c[11]), a.getToDam(), who + "to-dam");
            assertEquals(Integer.parseInt(c[12]), a.getAc(), who + "AC");
            assertEquals(Integer.parseInt(c[13]), a.getToAC(), who + "to-AC");
            assertEquals(c[14], flagNames(a), who + "flags");
            assertEquals(c[15], valueList(a), who + "values");
            assertEquals(c[16], a.getBrands().stream().map(Brand::getCode).sorted()
                    .collect(Collectors.joining(",")), who + "brands");
            assertEquals(c[17], a.getSlays().stream().map(Slay::getCode).sorted()
                    .collect(Collectors.joining(",")), who + "slays");
            assertEquals(c[18], a.getCurses().entrySet().stream()
                    .map(e -> e.getKey().getName() + "=" + e.getValue().getPower()).sorted()
                    .collect(Collectors.joining(",")), who + "curses");

            ObjectKind kind = ObjectRegistry.lookupObjectKind(a.gettValue(), a.getsValue());
            assertNotNull(kind, who + "kind");
            if (a.gettValue() == TValue.TV_LIGHT && !c[23].isEmpty()) {
                assertNull(a.getActivation(), who + "a special light's activation is the kind's");
                assertNull(a.getTime(), who + "a special light's time is the kind's");
                assertEquals(c[19], kind.getActivations().get(0).getName(), who + "kind activation");
                assertEquals(c[20], dice(kindField(kind, "time")), who + "kind time");
            } else {
                assertEquals(c[19], a.getActivation() == null ? "" : a.getActivation().getName(), who + "activation");
                assertEquals(c[20], dice(a.getTime()), who + "time");
            }
            assertEquals(c[21], a.getActivationMessage(), who + "message");
            int length = Integer.parseInt(c[22]) - (SHIPPED_COPY_DROPS_A_COLON.contains(c[0]) ? 1 : 0);
            assertEquals(length, a.getText().length(), who + "description length");
            if (!c[23].isEmpty()) {
                assertEquals(c[23].charAt(0), kind.getCharacter().getCharacter(), who + "glyph");
                assertEquals(colours.get(c[24]), kind.getCharacter().getAttributeColour().name(), who + "colour");
            }
        }
    }

    @Test
    void theDescriptionLinesOfAnArtifactAreJoinedWithNoSeparator() {
        // desc: lines are appended as written; the second line's leading space is the only gap.
        assertEquals("A small crystal phial containing the light of Eärendil's Star.  "
                        + "Its light is imperishable, and near it darkness cannot endure.",
                byNameOrFail("of Galadriel").getText());
    }

    @Test
    void anActivationMessageIsReadFromMsgLines() {
        assertEquals("Your {kind} grow{s} magical spikes...", byNameOrFail("of Fingolfin").getActivationMessage());
        assertEquals("{name} glows deep red...", byNameOrFail("'Narya'").getActivationMessage());
    }

    private static Artifact byNameOrFail(String name) {
        return result.items().stream().filter(a -> name.equals(a.getName())).findFirst().orElseThrow();
    }

    // ---- List order and numbering ----------------------------------------

    /**
     * C numbers by place in the file, but a parse error there ends the load. Here a skipped record
     * takes no place in the list, so every artifact after it moves up one.
     */
    @Test
    void aSkippedRecordTakesNoPlaceInTheListSoLaterArtifactsMoveUp() throws IOException {
        ParseResult<Artifact> r = load("skip.txt", withHeader(3,
                "name:A\n" + BASE + "name:Bad\n" + BASE + "flags:NOTAFLAG\n" + "name:C\n" + BASE));

        assertEquals(List.of("A", "C"), names(r));
        assertEquals(1, r.errors().size(), r.errors()::toString);
    }

    @Test
    void twoRecordsWithTheSameNameBothLoad() throws IOException {
        // parse_artifact_name allocates a new record each time and checks nothing against the others.
        ParseResult<Artifact> r = load("dupes.txt", withHeader(2,
                "name:A\n" + BASE + "level:1\n" + "name:A\n" + BASE + "level:3\n"));

        assertEquals(List.of("A", "A"), names(r));
        assertEquals(1, r.items().get(0).getLevel());
        assertEquals(3, r.items().get(1).getLevel());
    }

    @Test
    void recordsComeBackInFileOrderNotSortedOrder() throws IOException {
        ParseResult<Artifact> r = load("order.txt", withHeader(3,
                "name:B\n" + BASE + "name:A\n" + BASE + "name:C\n" + BASE));

        assertEquals(List.of("B", "A", "C"), names(r));
    }

    // ---- Directive order, repeats and accumulation -----------------------

    /**
     * Nothing in the handlers for these twelve reads another directive's value, so any order loads.
     * ({@code weight:}, {@code cost:}, {@code act:}, {@code time:} and {@code graphics:} look the
     * kind up through the base object in C, so they would have to follow it there; they do here.)
     */
    @Test
    void directivesMayComeInAnyOrder() throws IOException {
        ParseResult<Artifact> r = load("any-order.txt", withHeader(1,
                "name:X\nlevel:7\nflags:NO_FUEL\nbase-object:sword:Dagger\nalloc:5:10 to 20\n"
                        + "attack:2d4:3:4\narmor:1:2\nvalues:STR[2]\nslay:EVIL_2\nbrand:ACID_3\n"
                        + "curse:air swing:30\ndesc: d\nmsg:m\nweight:12\ncost:99\n"));
        assertFalse(r.hasErrors(), () -> r.errors().toString());
        Artifact a = r.items().get(0);

        assertEquals(7, a.getLevel());
        assertEquals(12, a.getWeight());
        assertEquals(99, a.getCost());
        assertEquals("5/10/20", a.getAllocProb() + "/" + a.getAllocMin() + "/" + a.getAllocMax());
        assertEquals("2d4", a.getDiceString());
        assertEquals(3, a.getToHit());
        assertEquals(4, a.getToDam());
        assertEquals(1, a.getAc());
        assertEquals(2, a.getToAC());
        assertEquals("NO_FUEL", flagNames(a));
        assertEquals("STR=2", valueList(a));
        assertEquals(1, a.getSlays().size());
        assertEquals(1, a.getBrands().size());
        assertEquals(1, a.getCurses().size());
        assertEquals(" d", a.getText());
        assertEquals("m", a.getActivationMessage());
    }

    @Test
    void repeatedScalarDirectivesKeepTheLastLine() throws IOException {
        // parse_artifact_level, _weight, _cost, _alloc, _attack, _armor, _act and _time all assign.
        Artifact a = loadClean("repeat-scalars.txt",
                "level:5\nlevel:9\nweight:1\nweight:2\ncost:3\ncost:4\nalloc:1:1 to 2\nalloc:3:4 to 5\n"
                        + "attack:1d1:1:1\nattack:2d2:2:2\narmor:1:1\narmor:2:2\n"
                        + "act:ILLUMINATION\nact:FIRE_BOLT\ntime:5\ntime:7+d3\n");

        assertEquals(9, a.getLevel());
        assertEquals(2, a.getWeight());
        assertEquals(4, a.getCost());
        assertEquals("3/4/5", a.getAllocProb() + "/" + a.getAllocMin() + "/" + a.getAllocMax());
        assertEquals("2d2", a.getDiceString());
        assertEquals(2, a.getToHit());
        assertEquals(2, a.getToDam());
        assertEquals(2, a.getAc());
        assertEquals(2, a.getToAC());
        assertEquals("FIRE_BOLT", a.getActivation().getName());
        assertEquals("7+1d3", dice(a.getTime()));
    }

    @Test
    void repeatedAccumulatingDirectivesAddUp() throws IOException {
        // flags: and values: strtok every line into the same arrays (a later value for the same
        // modifier overwrites), slay: and brand: set a bit each, and msg: and desc: string_append.
        Artifact a = loadClean("accumulate.txt",
                "flags:NO_FUEL\nflags:SEE_INVIS\nvalues:STR[1]\nvalues:DEX[2] | STR[3]\n"
                        + "slay:EVIL_2\nslay:ORC_3\nslay:EVIL_2\nbrand:ACID_3\nbrand:FIRE_3\n"
                        + "msg:m1\nmsg: m2\ndesc: a\ndesc:b\ndesc: c:d\n");

        assertEquals("NO_FUEL,SEE_INVIS", flagNames(a));
        assertEquals("DEX=2,STR=3", valueList(a));
        assertEquals(Set.of("EVIL_2", "ORC_3"), a.getSlays().stream().map(Slay::getCode).collect(Collectors.toSet()));
        assertEquals(Set.of("ACID_3", "FIRE_3"), a.getBrands().stream().map(Brand::getCode).collect(Collectors.toSet()));
        assertEquals("m1 m2", a.getActivationMessage());
        assertEquals(" ab c:d", a.getText());
    }

    @Test
    void aCurseWithNoPowerIsNotAddedAndALaterPowerReplacesAnEarlierOne() throws IOException {
        // parse_artifact_curse only stores a power above zero, and stores it over the old one.
        assertEquals(0, loadClean("curse-zero.txt", "curse:air swing:0\n").getCurses().size());
        assertEquals(0, loadClean("curse-negative.txt", "curse:air swing:-5\n").getCurses().size());

        Artifact twice = loadClean("curse-twice.txt", "curse:air swing:30\ncurse:air swing:40\n");
        assertEquals(40, twice.getCurses().values().iterator().next().getPower());
    }

    @Test
    void anUnknownCurseNameIsReportedAndTheRecordSkipped() throws IOException {
        ParseResult<Artifact> r = load("curse-unknown.txt", withHeader(1, "name:X\n" + BASE + "curse:zzz:5\n"));

        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown curse: zzz"), r.errors()::toString);
    }

    @Test
    void commentsAndBlankLinesBetweenDirectivesAreIgnored() throws IOException {
        ParseResult<Artifact> r = load("comments.txt",
                "# header comment\n\nrecord-count:1\n# before\nname:X\n# middle\n" + BASE + "\nlevel:3\n# end\n");

        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(3, r.items().get(0).getLevel());
    }

    @Test
    void windowsLineEndingsLoad() throws IOException {
        ParseResult<Artifact> r = load("crlf.txt",
                "record-count:1\r\nname:X\r\nbase-object:sword:Dagger\r\nlevel:3\r\ndesc: a\r\n");

        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(3, r.items().get(0).getLevel());
        assertEquals(" a", r.items().get(0).getText());
    }

    @Test
    void aNameKeepsItsTrailingBlankAsInC() throws IOException {
        // name is a str, which takes the rest of the line.
        ParseResult<Artifact> r = load("name-blank.txt", withHeader(1, "name:X \n" + BASE));

        assertEquals(List.of("X "), names(r));
    }

    @Test
    void tvalAndSvalAreMatchedWithoutRegardToCaseAndTheBritishSpellingOfArmourIsAccepted() throws IOException {
        // tval_find_idx runs de_armour over the name and compares with my_stricmp; lookup_sval
        // compares the kind name with my_stricmp too.
        for (String base : List.of("SWORD:Dagger", "sword:DAGGER", "soft armour:Soft Leather Armour",
                "soft armor:Soft Leather Armour")) {
            ParseResult<Artifact> r = load("base.txt", withHeader(1, "name:X\nbase-object:" + base + "\n"));
            assertFalse(r.hasErrors(), () -> base + ": " + r.errors());
            assertEquals(1, r.items().size(), base);
        }
    }

    @Test
    void allocFiguresOfZeroAnd255AreAccepted() throws IOException {
        Artifact a = loadClean("alloc-limits.txt", "alloc:0:0 to 255\n");

        assertEquals("0/0/255", a.getAllocProb() + "/" + a.getAllocMin() + "/" + a.getAllocMax());
    }

    @Test
    void trailingBlanksAfterIntegersDiceAndABarAreAcceptedAsInC() throws IOException {
        // parser_parse reads the figures with strtol and parse_random, which stop at the blank, and
        // strtok on " |" drops a trailing separator. The upstream file has such lines.
        Artifact a = loadClean("trailing-blanks.txt",
                "level:5 \narmor:1:2 \nattack:1d1:0:0 \ntime:5+d5 \nflags:NO_FUEL | \nvalues:STR[1] | \n");

        assertEquals(5, a.getLevel());
        assertEquals(1, a.getAc());
        assertEquals("1d1", a.getDiceString());
        assertEquals("5+1d5", dice(a.getTime()));
        assertEquals("NO_FUEL", flagNames(a));
        assertEquals("STR=1", valueList(a));
    }

    @Test
    void valuesTakeANegativeFigure() throws IOException {
        Artifact a = loadClean("values-negative.txt", "values:STEALTH[-2] | RES_FIRE[-1]\n");

        assertEquals("RES_FIRE=-1,STEALTH=-2", valueList(a));
    }

    // ---- Errors that are errors in C too ---------------------------------

    @Test
    void aMissingRecordCountHeaderIsAHardError() throws IOException {
        // Java only: the header is this port's addition.
        assertHardError("no-header.txt", "name:X\n" + BASE);
        assertHardError("empty-file.txt", "");
        assertHardError("header-not-a-number.txt", "record-count:abc\nname:X\n" + BASE);
    }

    @Test
    void aDirectiveBeforeAnyNameIsAHardErrorAsInC() throws IOException {
        // PARSE_ERROR_MISSING_RECORD_HEADER.
        assertHardError("before-name.txt", withHeader(1, "level:5\nname:X\n" + BASE));
    }

    @Test
    void anUnknownDirectiveIsAHardErrorAsInC() throws IOException {
        // PARSE_ERROR_UNDEFINED_DIRECTIVE.
        assertHardError("unknown-directive.txt", withHeader(1, "name:X\n" + BASE + "foo:bar\n"));
    }

    @Test
    void anEmptyDescOrMsgIsAHardErrorAsInC() throws IOException {
        // A str field with nothing after the colon is PARSE_ERROR_MISSING_FIELD.
        assertHardError("desc-empty.txt", withHeader(1, "name:X\n" + BASE + "desc:\n"));
        assertHardError("msg-empty.txt", withHeader(1, "name:X\n" + BASE + "msg:\n"));
    }

    @Test
    void aLowercaseFlagOrModifierIsRejectedAsInC() throws IOException {
        // grab_flag and grab_int_value compare with streq, so only the upper-case names match.
        assertHardError("flag-lower.txt", withHeader(1, "name:X\n" + BASE + "flags:no_fuel\n"));
        assertHardError("value-lower.txt", withHeader(1, "name:X\n" + BASE + "values:str[2]\n"));
    }

    // ---- Where the grammar is stricter than C ----------------------------

    /**
     * {@code parse_artifact_name} builds a complete record from the name alone. The grammar wants at
     * least one directive after it, and at least one record after the header.
     */
    @Test
    void aRecordOfOnlyANameOrAFileOfNoRecordsIsASyntaxErrorHereWhereCAcceptsThem() throws IOException {
        assertHardError("name-only.txt", withHeader(1, "name:X\n"));
        assertHardError("no-records.txt", "record-count:0\n");
    }

    /**
     * {@code flags ?str flags} is optional, and {@code parse_artifact_flags} returns no error when
     * {@code parser_hasval} is false.
     */
    @Test
    void anEmptyFlagsLineIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("flags-empty.txt", withHeader(1, "name:X\n" + BASE + "flags:\n"));
    }

    /**
     * Both handlers walk their text with {@code strtok(s, " |")}, so any run of spaces and bars is one
     * separator. The lexer wants a single bar with at most one space either side.
     */
    @Test
    void flagsOrValuesSeparatedBySpaceAloneOrByOddBarsAreSyntaxErrorsHereWhereCAcceptsThem() throws IOException {
        assertHardError("flags-space.txt", withHeader(1, "name:X\n" + BASE + "flags:NO_FUEL SEE_INVIS\n"));
        assertHardError("flags-double-bar.txt", withHeader(1, "name:X\n" + BASE + "flags:NO_FUEL || SEE_INVIS\n"));
        assertHardError("flags-leading-bar.txt", withHeader(1, "name:X\n" + BASE + "flags:| NO_FUEL\n"));
        assertHardError("values-space.txt", withHeader(1, "name:X\n" + BASE + "values:STR[1] DEX[2]\n"));
    }

    /**
     * The last flag, value or alloc figure followed by a blank is a lexer error. A bar then a blank
     * is fine, which is why only one line of the upstream file shows it: the {@code flags:} line of
     * "'Bladeturner'", which ends {@code PROT_CONF } and which this repository's copy has trimmed.
     */
    @Test
    void aBlankAfterTheLastFlagValueOrAllocFigureIsALexerErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("flags-blank.txt", withHeader(1, "name:X\n" + BASE + "flags:NO_FUEL \n"));
        assertHardError("values-blank.txt", withHeader(1, "name:X\n" + BASE + "values:STR[1] \n"));
        assertHardError("alloc-blank.txt", withHeader(1, "name:X\n" + BASE + "alloc:1:2 to 3 \n"));
    }

    /**
     * {@code grab_int_range} takes the figures round {@code "to"} with {@code strtol}, which skips
     * blanks, so any spacing loads. The lexer's {@code ALLOC_TO} is the literal {@code " to "}.
     */
    @Test
    void allocSpacingOtherThanOneSpaceEitherSideOfToIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("alloc-two-spaces.txt", withHeader(1, "name:X\n" + BASE + "alloc:1:2  to 3\n"));
        assertHardError("alloc-no-spaces.txt", withHeader(1, "name:X\n" + BASE + "alloc:1:2to3\n"));
    }

    /**
     * {@code parser_parse} reads an {@code int} with {@code strtol(tok, &z, 0)}: blanks are skipped,
     * {@code +5} is 5, {@code 0x10} is 16 and {@code 010} is 8. The lexer's integers are decimal digits
     * with an optional minus, so the first three are lexer errors and the last reads as ten.
     */
    @Test
    void integersWithLeadingSpaceAPlusSignOrAHexPrefixAreErrorsAndALeadingZeroIsDecimalHereWhereCDiffers() throws IOException {
        assertHardError("level-space.txt", withHeader(1, "name:X\n" + BASE + "level: 5\n"));
        assertHardError("level-plus.txt", withHeader(1, "name:X\n" + BASE + "level:+5\n"));
        assertHardError("level-hex.txt", withHeader(1, "name:X\n" + BASE + "level:0x10\n"));
        assertHardError("value-plus.txt", withHeader(1, "name:X\n" + BASE + "values:STR[+2]\n"));

        assertEquals(10, loadClean("level-octal.txt", "level:010\n").getLevel());
    }

    /**
     * {@code tval_find_idx} and {@code lookup_sval} both accept a number, the tval's value or the
     * sval itself. The lexer reads a number between the colons as {@code DELIM_INTEGER}, which the
     * {@code baseObject} rule does not take.
     */
    @Test
    void aNumericTvalOrSvalInBaseObjectIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        assertHardError("tval-number.txt", withHeader(1, "name:X\nbase-object:23:Dagger\n"));
        assertHardError("sval-number.txt", withHeader(1, "name:X\nbase-object:sword:1\n"));
    }

    @Test
    void aLaterCurseWithNoPowerErasesAnEarlierOneHereWhereCKeepsIt() throws IOException {
        // The grammar keeps one power per name, the last, and the assembler then drops a zero; C
        // stores only powers above zero, so the 30 survives the 0.
        assertEquals(0, loadClean("curse-then-zero.txt", "curse:air swing:30\ncurse:air swing:0\n").getCurses().size());
    }

    // ---- Where the assembler differs from C ------------------------------

    @Test
    void ignoreAndHatesFlagsAreUnknownHereWhereCSetsElementInfo() throws IOException {
        // grab_element_flag turns IGNORE_FIRE and HATES_FIRE into EL_INFO bits on el_info.
        ParseResult<Artifact> ignore = load("flag-ignore.txt", withHeader(1, "name:X\n" + BASE + "flags:IGNORE_FIRE\n"));
        ParseResult<Artifact> hates = load("flag-hates.txt", withHeader(1, "name:X\n" + BASE + "flags:HATES_FIRE\n"));

        assertTrue(ignore.items().isEmpty());
        assertTrue(hasError(ignore, "unknown flag: IGNORE_FIRE"), ignore.errors()::toString);
        assertTrue(hates.items().isEmpty());
        assertTrue(hasError(hates, "unknown flag: HATES_FIRE"), hates.errors()::toString);
    }

    @Test
    void allocFiguresAreNotCheckedAgainstZeroTo255HereWhereCRefusesThem() throws IOException {
        // parse_artifact_alloc returns PARSE_ERROR_OUT_OF_BOUNDS for a figure above 255 or below 0.
        Artifact high = loadClean("alloc-high.txt", "alloc:1:300 to 400\n");
        Artifact negative = loadClean("alloc-negative.txt", "alloc:1:-5 to 10\n");
        Artifact backwards = loadClean("alloc-backwards.txt", "alloc:1:50 to 10\n");

        assertEquals("300/400", high.getAllocMin() + "/" + high.getAllocMax());
        assertEquals("-5/10", negative.getAllocMin() + "/" + negative.getAllocMax());
        // C has no check on the order, so this one agrees.
        assertEquals("50/10", backwards.getAllocMin() + "/" + backwards.getAllocMax());
    }

    @Test
    void anUnknownActNameSkipsTheRecordHereWhereCKeepsItWithNoActivation() throws IOException {
        // findact walks the list and returns NULL at the end; parse_artifact_act stores it without
        // a complaint.
        ParseResult<Artifact> r = load("act-unknown.txt", withHeader(1, "name:X\n" + BASE + "act:NOPE\n"));

        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown activation: NOPE"), r.errors()::toString);
    }

    @Test
    void graphicsOnAnOrdinaryKindIsIgnoredHereWhereCRefusesIt() throws IOException {
        // parse_artifact_graphics returns PARSE_ERROR_NOT_SPECIAL_ARTIFACT unless the kind is
        // INSTA_ART, and otherwise writes d_char and d_attr.
        ObjectKind dagger = ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "Dagger");
        char glyph = dagger.getCharacter().getCharacter();
        Object colour = dagger.getCharacter().getAttributeColour();

        loadClean("graphics-ordinary.txt", "graphics:#:r\n");

        assertEquals(glyph, dagger.getCharacter().getCharacter());
        assertEquals(colour, dagger.getCharacter().getAttributeColour());
    }

    @Test
    void aRecordWithNoBaseObjectIsRejectedHereWhereCKeepsItWithTvalZero() throws IOException {
        // parse_artifact_name zeroes the record, so tval and sval stay 0. (weight:, cost:, graphics:,
        // act: and time: would then assert on a missing kind; level: and the rest do not.)
        ParseResult<Artifact> r = load("no-base.txt", withHeader(1, "name:X\nlevel:3\n"));

        assertTrue(r.items().isEmpty());
        assertTrue(hasError(r, "unknown object type"), r.errors()::toString);
    }

    /**
     * Birth parses the file a second time to restore the standard artifacts after a random set.
     * C's {@code parse_artifact_act} and {@code parse_artifact_time} decide where a light's
     * activation goes from the kind's index (special kinds sit above {@code ordinary_kind_max}), so
     * the second run gives the kind the same values again and the artifact still holds none. The
     * assembler decides from whether the kind was minted by this record, which it was not the second
     * time, so the artifact gets the activation and the time itself.
     */
    @Test
    void aSecondParseGivesTheSpecialLightsTheirActivationAndTimeHereWhereCKeepsThemOnTheKind() throws Exception {
        int kindsBefore = ObjectRegistry.getObjectKindCount();

        ParseResult<Artifact> second = new ArtifactReader().parseWithResults(ARTIFACT_FILE);

        assertFalse(second.hasErrors(), () -> second.errors().toString());
        assertEquals(138, second.items().size());
        assertEquals(kindsBefore, ObjectRegistry.getObjectKindCount(), "the special kinds are found again, not added again");

        Artifact first = byNameOrFail("of Galadriel");
        Artifact again = second.items().get(0);
        assertEquals("of Galadriel", again.getName());
        assertNull(first.getActivation());
        assertNull(first.getTime());
        assertEquals("ILLUMINATION", again.getActivation().getName());
        assertNotNull(again.getTime());
    }

    // ---- The table -------------------------------------------------------

    private static final String EXPECTED_TABLE = """
            of Galadriel|TV_LIGHT|Phial|5|10|10000|40|5|100|1d1|0|0|0|0|NO_FUEL|LIGHT=4||||ILLUMINATION|10+1d10||126|~|y
            of Elendil|TV_LIGHT|Star|30|5|30000|4|30|100|1d1|0|0|0|0|NO_FUEL,SEE_INVIS|LIGHT=4||||MAPPING|50+1d50||71|~|y
            of Thráin|TV_LIGHT|Arkenstone|50|5|50000|2|50|127|1d1|0|0|0|0|HOLD_LIFE,NO_FUEL,SEE_INVIS|LIGHT=4,RES_DARK=1,RES_LIGHT=1||||CLAIRVOYANCE|50+1d50||198|~|y
            'Angrist'|TV_SWORD|Dagger|20|12|100000|2|60|100|2d7|18|16|0|5|FREE_ACT,SUST_DEX|DEX=4,RES_ACID=1,RES_DARK=1|ACID_3|EVIL_2,ORC_3,TROLL_3|||||213||
            'Narthanc'|TV_SWORD|Dagger|1|12|12000|40|1|50|1d4|9|12|0|10||RES_FIRE=1|FIRE_3|||FIRE_BOLT|8+1d8||68||
            'Nimthanc'|TV_SWORD|Dagger|1|12|11000|40|1|50|1d4|9|12|0|10||RES_COLD=1|COLD_3|||COLD_BOLT|7+1d8||70||
            'Dethanc'|TV_SWORD|Dagger|1|12|13000|40|1|50|1d4|9|12|0|10||RES_ELEC=1|ELEC_3|||ELEC_BOLT|6+1d6||52||
            of Rilia|TV_SWORD|Dagger|5|12|15000|5|5|75|2d4|14|13|0|0||RES_DISEN=1,RES_POIS=1|POIS_3|ORC_3,TROLL_3||STINKING_CLOUD|4+1d4||99||
            'Belangil'|TV_SWORD|Dagger|10|12|40000|2|10|80|3d4|16|14|0|0|REGEN,SEE_INVIS,SLOW_DIGEST|DEX=2,RES_COLD=1|COLD_3|||COLD_BALL50|5+1d5||98||
            of Azaghâl|TV_SWORD|Main Gauche|18|30|50000|3|18|100|2d5|12|14|0|0|PROT_FEAR|RES_ACID=1,RES_FIRE=3||DRAGON_5|||||288||
            of Maedhros|TV_SWORD|Main Gauche|15|30|20000|3|15|65|2d6|12|15|0|0|FREE_ACT,SEE_INVIS|DEX=3,INT=3||GIANT_3,TROLL_3|||||148||
            'Forasgil'|TV_SWORD|Rapier|1|40|15000|38|5|50|1d12|12|19|0|0||LIGHT=1,RES_COLD=1,RES_LIGHT=1|COLD_3|ANIMAL_2|||||68||
            'Careth Asdriag'|TV_SWORD|Rapier|15|40|25000|10|15|80|2d6|16|12|0|0||BLOWS=1,CON=2,DEX=2,STR=2||ANIMAL_2,DRAGON_3,GIANT_3,ORC_3,TROLL_3|||||116||
            'Sting'|TV_SWORD|Short Sword|20|75|100000|6|20|100|1d7|7|8|0|0|FREE_ACT,PROT_FEAR,SEE_INVIS|BLOWS=2,CON=1,DEX=1,LIGHT=1,RES_LIGHT=1,SPEED=3,STR=1||ANIMAL_2,EVIL_2,ORC_3,UNDEAD_3|||||244||
            'Dagmor'|TV_SWORD|Short Sword|20|80|15000|12|20|80|1d7|13|11|0|0|REGEN,SLOW_DIGEST|BLOWS=2|POIS_3|ANIMAL_2|||||81||
            'Gondricam'|TV_SWORD|Cutlass|20|110|28000|12|20|65|1d8|10|11|0|50|FEATHER,FREE_ACT,REGEN,SEE_INVIS|DEX=4,RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,STEALTH=2|||||||180||
            'Haradekket'|TV_SWORD|Scimitar|20|130|30000|7|20|80|4d2|9|11|0|0|SEE_INVIS|BLOWS=2,DEX=2||ANIMAL_2,EVIL_2,UNDEAD_3|||||125||
            'Ringil'|TV_SWORD|Long Sword|40|130|300000|1|20|127|4d5|22|25|0|0|BLESSED,FREE_ACT,PROT_FEAR,REGEN,SEE_INVIS,SLOW_DIGEST|LIGHT=1,RES_COLD=1,RES_LIGHT=1,SPEED=10|COLD_3|DEMON_5,EVIL_2,TROLL_3,UNDEAD_3||COLD_BALL100|40+0d0||224||
            'Andúril'|TV_SWORD|Long Sword|35|130|80000|3|20|127|3d5|10|15|0|10|BLESSED,FREE_ACT,PROT_FEAR,SEE_INVIS,SUST_DEX,SUST_STR|DEX=4,RES_DISEN=1,RES_FIRE=1,STR=4|FIRE_3|EVIL_2,ORC_3,TROLL_3,UNDEAD_3||FIRE_BALL72|40+0d0||189||
            'Anguirel'|TV_SWORD|Long Sword|20|130|40000|3|20|127|2d5|10|12|0|0|AGGRAVATE,FREE_ACT,SEE_INVIS|BLOWS=1,CON=2,LIGHT=1,RES_DARK=1,RES_ELEC=1,RES_LIGHT=1,SPEED=3,STR=2|POIS_3|DEMON_3,EVIL_2|||||166||
            'Elvagil'|TV_SWORD|Long Sword|1|130|30000|50|10|50|2d5|12|12|0|0|FEATHER,SEE_INVIS|DEX=2,STEALTH=2||ORC_3,TROLL_3|||||104||
            'Aranrúth'|TV_SWORD|Broad Sword|20|150|50000|2|20|100|3d5|20|12|0|0|FEATHER,FREE_ACT,SLOW_DIGEST|DEX=4,RES_COLD=1||DEMON_3,ORC_3||COLD_BOLT2|50+0d0||198||
            'Glamdring'|TV_SWORD|Broad Sword|20|150|40000|5|20|80|2d5|10|15|0|0|BLESSED,SLOW_DIGEST|LIGHT=1,RES_FIRE=1,RES_LIGHT=1,SEARCH=1|FIRE_3|DEMON_3,EVIL_2,ORC_3|||||126||
            'Aeglin'|TV_SWORD|Broad Sword|20|150|45000|3|20|100|2d5|12|16|0|0|BLESSED,PROT_BLIND,SLOW_DIGEST|LIGHT=1,RES_ELEC=1,SEARCH=1|ELEC_3|GIANT_3,ORC_3,TROLL_3|||||192||
            'Orcrist'|TV_SWORD|Broad Sword|20|150|40000|5|20|80|2d5|10|15|0|0|BLESSED,SLOW_DIGEST|LIGHT=1,RES_COLD=1,RES_DARK=1,SEARCH=1|COLD_3|DRAGON_3,EVIL_2,ORC_3|||||120||
            'Calris'|TV_SWORD|Bastard Sword|30|140|100000|7|30|127|3d7|0|20|0|0|AGGRAVATE|CON=5,RES_DISEN=1||DEMON_3,DRAGON_5,EVIL_2,TROLL_3|air swing=30||||150||
            of Éowyn|TV_SWORD|Bastard Sword|30|140|120000|3|30|127|4d4|12|16|0|0|PROT_FEAR|RES_COLD=1,RES_DARK=1,RES_NETHER=1,STEALTH=2,STR=4||ANIMAL_2,EVIL_2,GIANT_3,UNDEAD_5|||||177||
            'Aglarang'|TV_SWORD|Katana|30|50|40000|4|30|100|6d6|10|10|0|0|SUST_DEX|BLOWS=1,DEX=5,SPEED=5|||||||171||
            'Gurthang'|TV_SWORD|Zweihander|30|200|100000|3|30|100|3d6|13|17|0|0|FREE_ACT,REGEN,SLOW_DIGEST|RES_FIRE=1,RES_POIS=1,STR=2|FIRE_3,POIS_3|DRAGON_5|||||152||
            'Zarcuthra'|TV_SWORD|Zweihander|30|240|200000|1|30|127|3d8|19|21|0|0|AGGRAVATE,FREE_ACT,SEE_INVIS|INFRA=2,RES_CHAOS=1,RES_FIRE=1,STR=4|FIRE_3|ANIMAL_2,DEMON_3,DRAGON_5,EVIL_2,GIANT_3,ORC_3,TROLL_3,UNDEAD_3|||||158||
            'Mormegil'|TV_SWORD|Zweihander|30|250|10000|7|30|100|3d6|-15|-15|0|-10|AGGRAVATE,DRAIN_EXP,HOLD_LIFE,SEE_INVIS|SPEED=-10|POIS_3|DRAGON_5,UNDEAD_3|||||177||
            'Crisdurian'|TV_SWORD|Executioner's Sword|40|260|100000|4|40|127|4d6|18|19|0|0|SEE_INVIS|||DRAGON_3,EVIL_2,GIANT_3,ORC_3,TROLL_3,UNDEAD_3|||||176||
            'Doomcaller'|TV_SWORD|Blade of Chaos|70|180|200000|4|70|127|4d7|18|28|0|-50|AGGRAVATE,FREE_ACT,SEE_INVIS,TELEPATHY|RES_ACID=1,RES_CHAOS=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1|COLD_3|ANIMAL_2,DEMON_3,DRAGON_5,EVIL_2,TROLL_3|||||208||
            of Gothmog|TV_HAFTED|Whip|60|90|25000|4|60|127|6d3|13|15|0|0|AGGRAVATE|DEX=-3,INT=-3,LIGHT=1,RES_DARK=1,RES_ELEC=1,RES_FIRE=3,STR=3,WIS=-3|FIRE_3|ANIMAL_2,DRAGON_5,GIANT_3,TROLL_3||FIRE_BALL|15+0d0||89||
            of Fundin Bluecloak|TV_HAFTED|Ball-and-Chain|65|130|60000|2|65|127|4d4|13|17|0|10|HOLD_LIFE|LIGHT=1,RES_ELEC=1,RES_FIRE=1,RES_NETHER=1,STR=4,WIS=4||EVIL_2,UNDEAD_3||DISPEL_EVIL|100+1d100||99||
            'Bloodspike'|TV_HAFTED|Morning Star|20|150|30000|3|20|75|2d6|8|22|0|0|SEE_INVIS|RES_NEXUS=1,STR=4|POIS_3|ANIMAL_2,ORC_3,TROLL_3|||||106||
            'Firestar'|TV_HAFTED|Morning Star|25|150|35000|7|20|100|2d6|15|17|0|2||RES_FIRE=3|FIRE_3|||FIRE_BALL72|20+0d0||88||
            'Totila'|TV_HAFTED|Flail|20|150|55000|12|20|65|3d6|16|12|0|0|PROT_CONF|RES_FIRE=1,SPEED=2,STEALTH=2|FIRE_3|EVIL_2||CONFUSE2|15+0d0||114||
            'Thunderfist'|TV_HAFTED|Two-Handed Great Flail|45|300|160000|3|45|127|3d9|5|18|0|0|PROT_FEAR|CON=3,RES_DARK=1,RES_ELEC=1,RES_FIRE=1,STR=4|ELEC_3,FIRE_3|ANIMAL_2,ORC_3,TROLL_3|||||168||
            'Taratol'|TV_HAFTED|Mace|35|200|50000|7|20|100|3d6|12|12|0|0||RES_ELEC=3|ELEC_3|DRAGON_5||HASTE1|100+1d100||165||
            'Nar-i-vagil'|TV_HAFTED|Quarterstaff|20|150|70000|5|20|80|1d9|10|20|0|0||INT=4,RES_FIRE=1,RES_POIS=1|FIRE_3|ANIMAL_2|||||98||
            'Eriril'|TV_HAFTED|Quarterstaff|15|150|20000|5|20|100|1d9|13|15|0|0|SEE_INVIS|INT=4,LIGHT=1,RES_LIGHT=1,WIS=4||EVIL_2||DETECT_OBJECTS|50+1d50||132||
            of Olórin|TV_HAFTED|Quarterstaff|20|150|130000|1|30|100|2d9|10|13|0|0|HOLD_LIFE,SEE_INVIS|INT=4,RES_FIRE=1,RES_NETHER=1,WIS=4|FIRE_3|DEMON_5,EVIL_2,ORC_3,TROLL_3||PROBING|20+0d0||224||
            of Aulë|TV_HAFTED|Great Hammer|40|120|250000|1|40|127|18d1|19|21|0|5|FREE_ACT,PROT_FEAR,SEE_INVIS|RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,RES_NEXUS=1,TUNNEL=8,WIS=4|ACID_3|DEMON_3,DRAGON_5,EVIL_2,UNDEAD_3|||||217||
            'Deathwreaker'|TV_HAFTED|Mace of Disruption|80|400|400000|3|80|127|7d8|18|18|0|0|AGGRAVATE|RES_CHAOS=1,RES_DARK=1,RES_DISEN=1,RES_FIRE=3,STR=6,TUNNEL=4|FIRE_3|ANIMAL_2,DRAGON_3,EVIL_2,UNDEAD_5|||||303||
            'Aiglos'|TV_POLEARM|Spear|40|50|140000|2|15|100|3d6|15|25|0|5|BLESSED,FREE_ACT,PROT_FEAR,SLOW_DIGEST|DEX=4,RES_COLD=1,WIS=2|COLD_3|EVIL_2,ORC_3,TROLL_3,UNDEAD_5||COLD_BALL100|35+0d0||139||
            of Oromë|TV_POLEARM|Spear|5|50|60000|2|15|127|4d6|15|15|0|0|BLESSED,FEATHER,SEE_INVIS|INFRA=4,INT=4,LIGHT=1,RES_FIRE=1,RES_LIGHT=1,SPEED=4|FIRE_3|ANIMAL_2,GIANT_3||STONE_TO_MUD|5+0d0||119||
            'Nimloth'|TV_POLEARM|Spear|15|50|30000|8|15|65|1d7|11|13|0|0|BLESSED,SEE_INVIS|RES_COLD=1,SPEED=3,STEALTH=3|COLD_3|UNDEAD_3|||||224||
            of Melkor|TV_POLEARM|Spear|65|200|100000|2|65|127|4d6|-12|20|0|0|AGGRAVATE,DRAIN_EXP,PROT_BLIND|RES_DARK=1,RES_LIGHT=1,RES_NETHER=1,STEALTH=-4,WIS=-4|POIS_3||||||180||
            of Wrath|TV_POLEARM|Trident|15|70|90000|3|15|100|3d10|16|18|0|0|BLESSED,SEE_INVIS|DEX=2,RES_DARK=1,RES_LIGHT=1,STR=2|POIS_3|EVIL_2,UNDEAD_5|||||156||
            of Ulmo|TV_POLEARM|Trident|27|70|120000|1|30|127|4d10|15|19|0|0|BLESSED,FREE_ACT,HOLD_LIFE,REGEN,SEE_INVIS,SLOW_DIGEST|DEX=4,RES_ACID=3,RES_NETHER=1||ANIMAL_2,DRAGON_3||TELE_OTHER|50+0d0||213||
            'Osondir'|TV_POLEARM|Halberd|20|140|22000|12|25|80|3d7|16|12|0|0|BLESSED,FEATHER,SEE_INVIS|RES_FIRE=1,RES_SOUND=1,STR=3,WIS=3|FIRE_3|GIANT_3,UNDEAD_3|||||131||
            'Til-i-arc'|TV_POLEARM|Pike|20|160|32000|7|20|80|1d15|10|12|0|10|SLOW_DIGEST,SUST_INT|INT=3,RES_COLD=1,RES_FIRE=1,SPEED=2,STR=3|COLD_3,FIRE_3|DEMON_3,GIANT_3,TROLL_3|||||130||
            of Théoden|TV_POLEARM|Beaked Axe|30|180|40000|7|20|100|2d6|8|10|0|0|SLOW_DIGEST,TELEPATHY|CON=3,WIS=3||DRAGON_3||DRAIN_LIFE2|40+0d0||134||
            of Húrin|TV_POLEARM|Beaked Axe|20|180|90000|7|20|100|3d6|12|15|0|0||CON=2,LIGHT=1,RES_ACID=1,RES_DARK=1,RES_FIRE=1,STR=2|ACID_3|DEMON_5,DRAGON_3,TROLL_3||BERSERKER|80+1d80||327||
            'Barukkheled'|TV_POLEARM|Broad Axe|20|160|50000|12|20|75|2d6|13|19|0|0|SEE_INVIS|CON=3||EVIL_2,GIANT_3,ORC_3,TROLL_3|||||93||
            of Balli Stonehand|TV_POLEARM|Battle Axe|30|170|90000|7|30|100|3d8|8|11|0|5|FEATHER,FREE_ACT,PROT_BLIND,REGEN,SEE_INVIS|CON=3,RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,STEALTH=1,STR=3||DEMON_3,ORC_3,TROLL_3|||||126||
            'Lotharang'|TV_POLEARM|Battle Axe|30|170|21000|7|30|100|2d8|14|13|0|0||BLOWS=1,DEX=3,RES_DISEN=1,STR=3||ORC_3,TROLL_3||CURE_SERIOUS|3+1d3||96||
            'Mundwine'|TV_POLEARM|Lochaber Axe|30|240|30000|12|30|100|3d10|12|17|0|0||RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1||ANIMAL_2,DEMON_3,EVIL_2|||||189||
            of Durin|TV_POLEARM|Great Axe|30|230|150000|1|40|127|4d4|10|20|0|15|FREE_ACT,PROT_FEAR|CON=3,RES_ACID=1,RES_CHAOS=1,RES_DARK=1,RES_FIRE=1,RES_LIGHT=1,STEALTH=-1,STR=3,TUNNEL=5|ACID_3,FIRE_3|DEMON_3,DRAGON_5,ORC_3,TROLL_3|||||193||
            of Eönwë|TV_POLEARM|Great Axe|30|230|200000|1|40|127|5d4|15|18|0|8|BLESSED,FREE_ACT,PROT_FEAR,SEE_INVIS|CON=2,DEX=2,INT=2,RES_COLD=3,STR=2,WIS=2|COLD_3|DEMON_5,EVIL_2,ORC_3,UNDEAD_3||LOSKILL|1000+0d0||189||
            'Avavir'|TV_POLEARM|Scythe|20|180|18000|12|45|100|5d3|18|12|0|30|FREE_ACT,SEE_INVIS|DEX=3,LIGHT=1,RES_COLD=1,RES_FIRE=1,RES_LIGHT=1,SPEED=3|COLD_3,FIRE_3|||RECALL|200+0d0||153||
            of Pain|TV_POLEARM|Glaive|30|300|50000|4|30|127|6d9|0|30|0|0|||||||||150||
            of the Eorlingas|TV_POLEARM|Lance|20|360|55000|4|20|100|2d17|3|21|0|0|PROT_FEAR,SEE_INVIS|DEX=2,SPEED=2,STR=2||EVIL_2,ORC_3,TROLL_3|||||162||
            'Turmil'|TV_HAFTED|Lead-Filled Mace|20|120|30000|7|20|100|4d4|10|11|0|8|BLESSED,REGEN|INFRA=4,LIGHT=1,RES_COLD=3,RES_LIGHT=1,WIS=4|COLD_3|ORC_3||DRAIN_LIFE1|40+0d0||155||
            of Amrod|TV_BOW|Short Bow|25|30|9000|10|30|100|0d0|12|15|0|0|REGEN|CON=2,MIGHT=2,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,STR=2|||||||142||
            of Amras|TV_BOW|Short Bow|25|30|9000|10|30|80|0d0|12|15|0|0|SLOW_DIGEST|DEX=2,INT=2,MIGHT=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,SHOTS=5,SPEED=1,WIS=2|||||||142||
            'Belthronding'|TV_BOW|Long Bow|40|40|35000|5|40|127|0d0|20|22|0|0||DEX=3,RES_DISEN=1,SHOTS=10,SPEED=1,STEALTH=1|||||||174||
            of Bard|TV_BOW|Long Bow|30|40|20000|5|40|127|0d0|17|19|0|0|FREE_ACT|DEX=1,MIGHT=2,SPEED=1|||||||85||
            'Cubragol'|TV_BOW|Light Crossbow|50|110|50000|4|50|127|0d0|10|14|0|0||RES_FIRE=1,SPEED=10|FIRE_3||||||95||
            of Umbar|TV_BOW|Heavy Crossbow|45|200|35000|5|60|127|4d1|18|18|0|0|AGGRAVATE,PROT_BLIND|CON=1,MIGHT=2,RES_DARK=1,RES_ELEC=1,RES_LIGHT=1,STR=1||||ARROW|20+1d20||131||
            of Erebor|TV_DIGGING|Pick|55|180|30000|20|55|100|4d3|5|20|0|0|SUST_STR|CON=3,LIGHT=1,RES_CHAOS=1,RES_DARK=1,RES_LIGHT=1,STR=3,TUNNEL=5|ACID_3|DEMON_3,ORC_3,TROLL_3|||||127||
            of Náin|TV_DIGGING|Mattock|10|240|30000|12|60|100|2d8|12|18|0|0||INFRA=4,RES_DARK=1,RES_DISEN=1,SEARCH=4,STR=4,TUNNEL=6||DRAGON_3,GIANT_3,ORC_3,TROLL_3||STONE_TO_MUD|2+0d0||258||
            of Fëanor|TV_BOOTS|Pair of Leather Boots|30|20|300000|1|40|127|1d1|0|0|2|20||RES_NEXUS=1,SPEED=15||||HASTE1|200+0d0||302||
            'Dal-i-thalion'|TV_BOOTS|Pair of Leather Boots|10|20|40000|4|50|100|1d1|0|0|2|15|FREE_ACT,PROT_CONF,SUST_CON|DEX=5,RES_NETHER=1,SPEED=5||||REM_FEAR_POIS|5+0d0||117||
            of Wormtongue|TV_BOOTS|Pair of Leather Boots|10|20|17000|8|15|80|1d1|-1|-1|2|0|FEATHER,TRAP_IMMUNE|DEX=2,INT=2,MOVES=1,STEALTH=2||||TELE_PHASE|20+0d0||172||
            of Radagast|TV_BOOTS|Pair of Leather Boots|10|20|7000|8|15|70|1d1|0|0|2|12|FEATHER,FREE_ACT|INFRA=5,RES_COLD=1,STEALTH=3,WIS=1|||||||134||
            of Thrór|TV_BOOTS|Pair of Steel Shod Boots|30|60|12000|5|30|100|1d1|0|0|7|20|PROT_FEAR|CON=3,SPEED=3,STEALTH=-1,STR=3|||||||107||
            'Stormwalker'|TV_BOOTS|Pair of Mithril Shod Boots|30|40|40000|3|60|127|1d1|0|0|8|24||RES_ELEC=3,RES_SOUND=1,SPEED=6||||BERSERKER|100+0d0||357||
            of Thranduil|TV_HELM|Hard Leather Cap|50|15|50000|14|40|100|0d0|0|0|2|10|PROT_BLIND,TELEPATHY|INT=2,WIS=2|||||||91||
            of Thengel|TV_HELM|Metal Cap|10|20|22000|18|10|100|1d1|0|0|3|12|PROT_CONF|WIS=3|||||||138||
            of Celebrimbor|TV_HELM|Metal Cap|55|20|45000|8|55|100|1d1|0|0|3|18||DEX=2,INT=2,RES_ACID=1,RES_DISEN=1,RES_FIRE=1,RES_SHARD=1,SEARCH=3|||||||171||
            of Dor-Lómin|TV_HELM|Iron Helm|40|50|300000|5|40|127|1d3|0|0|7|20|PROT_FEAR,SEE_INVIS,TELEPATHY|CON=4,DEX=2,RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,STEALTH=-1,STR=4|||||||252||
            'Holhenneth'|TV_HELM|Iron Helm|20|50|100000|18|20|100|1d3|0|0|7|10|PROT_BLIND,PROT_CONF,SEE_INVIS|INT=2,SEARCH=2,WIS=2||||DETECT_ALL|55+1d55||78||
            of Gorlim|TV_HELM|Iron Helm|20|75|1|18|20|75|1d3|8|8|7|10|AGGRAVATE,FREE_ACT,PROT_FEAR,SEE_INVIS|INT=-3,SEARCH=-5,WIS=-3|||||||84||
            of Hammerhand|TV_HELM|Steel Helm|20|60|45000|11|40|127|1d3|0|0|9|20|FREE_ACT|CON=3,DEX=3,RES_ACID=1,RES_COLD=1,RES_DARK=1,RES_NEXUS=1,STEALTH=-2,STR=3|||||||263||
            of Berúthiel|TV_CROWN|Iron Crown|40|20|1|8|45|100|1d1|0|0|0|20|FREE_ACT,SEE_INVIS,TELEPATHY|CON=-5,DEX=-3,STR=-3|||||||149||
            of Gondor|TV_CROWN|Golden Crown|40|30|100000|5|45|127|1d1|0|0|0|15|PROT_BLIND,PROT_CONF,PROT_STUN,REGEN,SEE_INVIS|CON=3,LIGHT=1,RES_COLD=1,RES_FIRE=1,RES_LIGHT=1,STR=3,WIS=2||||HEAL1|250+0d0||110||
            of Númenor|TV_CROWN|Jewel Encrusted Crown|60|40|50000|5|60|127|1d1|0|0|0|18|FREE_ACT,PROT_BLIND,PROT_STUN,SEE_INVIS|DEX=2,INT=3,LIGHT=1,RES_COLD=1,RES_DARK=1,RES_LIGHT=1,RES_SHARD=1,SEARCH=3|||||||115||
            'Hithlomir'|TV_SOFT_ARMOR|Soft Leather Armour|20|80|45000|20|20|80|0d0|0|0|10|20||RES_ACID=1,RES_COLD=1,RES_DARK=1,RES_ELEC=1,RES_FIRE=1,RES_NETHER=1,STEALTH=3|||||||101||
            of Himring|TV_SOFT_ARMOR|Hard Leather Armour|30|100|35000|20|25|90|0d0|0|0|20|15||RES_CHAOS=1,RES_NETHER=1,RES_POIS=1||||PROTEVIL|100+1d100||139||
            'Thalkettoth'|TV_SOFT_ARMOR|Leather Scale Mail|20|60|25000|20|30|100|1d1|-1|0|20|25|FREE_ACT|DEX=3,RES_ACID=1,RES_SHARD=1,SPEED=3|||||||143||
            of Arvedui|TV_HARD_ARMOR|Chain Mail|20|220|32000|30|20|75|1d4|-2|0|40|15||RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,RES_NEXUS=1,RES_SHARD=1,STR=2|||||||166||
            of Caspanion|TV_HARD_ARMOR|Augmented Chain Mail|25|270|40000|11|35|100|1d4|-2|0|45|20|PROT_CONF|CON=3,INT=2,RES_ACID=1,RES_POIS=1,WIS=2||||DESTROY_DOORS|10+0d0||219||
            of the Rohirrim|TV_HARD_ARMOR|Metal Brigandine Armour|30|200|30000|30|45|100|1d4|0|0|50|15|PROT_CONF,PROT_FEAR|DEX=2,RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,RES_SOUND=1,STR=2|||||||211||
            of Isildur|TV_HARD_ARMOR|Full Plate Armour|30|300|50000|30|55|127|2d4|0|0|75|25|PROT_CONF|CON=1,RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,RES_NEXUS=1,RES_SOUND=1|||||||130||
            'Belegennon'|TV_HARD_ARMOR|Mithril Chain Mail|20|150|105000|30|55|127|1d4|-1|0|35|20||DAM_RED=5,RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,RES_POIS=1||||TELE_PHASE|2+0d0||300||
            of Celeborn|TV_HARD_ARMOR|Mithril Plate Mail|40|250|150000|30|65|127|2d4|-3|0|65|25||RES_ACID=1,RES_COLD=1,RES_DARK=1,RES_DISEN=1,RES_ELEC=1,RES_FIRE=1,STR=3||||BANISHMENT|500+0d0||177||
            'Soulkeeper'|TV_HARD_ARMOR|Adamantite Plate Mail|75|420|300000|11|75|127|2d4|-4|0|90|20|HOLD_LIFE,PROT_FEAR,SUST_CON|CON=2,RES_ACID=1,RES_CHAOS=1,RES_COLD=1,RES_DARK=1,RES_NETHER=1,RES_NEXUS=1||||HEAL2|444+0d0||159||
            'Colluin'|TV_CLOAK|Cloak|12|10|50000|2|5|100|0d0|0|0|1|15||RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1,RES_POIS=1||||RESIST_ALL|111+0d0||81||
            'Holcolleth'|TV_CLOAK|Cloak|13|10|18000|5|5|100|0d0|0|0|1|4||INT=2,RES_ACID=1,STEALTH=3,WIS=2||||SLEEPII|55+0d0||146||
            of Thingol|TV_CLOAK|Cloak|62|10|35000|5|5|100|0d0|0|0|1|18|FREE_ACT|DEX=3,RES_ACID=1,RES_COLD=1,RES_FIRE=1||||RECHARGE|70+0d0||122||
            of Thorongil|TV_CLOAK|Cloak|5|10|8000|10|5|75|0d0|0|0|1|10|FREE_ACT,PROT_FEAR,SEE_INVIS|RES_ACID=1|||||||149||
            'Colannon'|TV_CLOAK|Cloak|26|10|20000|5|5|100|0d0|0|0|1|15||RES_NEXUS=1,SPEED=2,STEALTH=2||||TELE_LONG|45+0d0||135||
            of Lúthien|TV_CLOAK|Elven Cloak|40|5|45000|2|40|127|0d0|0|0|6|20||INT=4,RES_ACID=1,RES_COLD=1,RES_FIRE=1,SPEED=2,STEALTH=3,WIS=4||||RESTORE_LIFE|250+0d0||216||
            of Tuor|TV_CLOAK|Ethereal Cloak|40|0|35000|2|70|127|0d0|0|0|6|12|FREE_ACT,SEE_INVIS|DEX=4,RES_ACID=3,STEALTH=4|||||||161||
            'Cambeleg'|TV_GLOVES|Set of Leather Gloves|10|5|36000|3|10|100|0d0|8|8|1|15|FREE_ACT|CON=2,STR=2|||||||53||
            'Cammithrim'|TV_GLOVES|Set of Leather Gloves|10|5|30000|18|10|80|0d0|5|5|1|10|FREE_ACT,SUST_CON|LIGHT=1,RES_LIGHT=1||||MISSILE|2+0d0||109||
            'Paurhach'|TV_GLOVES|Set of Gauntlets|1|25|15000|40|10|60|1d1|0|0|3|14|REGEN|RES_FIRE=1||||FIRE_BOLT|8+1d8||56||
            'Paurnimmen'|TV_GLOVES|Set of Gauntlets|1|25|13000|5|50|127|1d1|0|0|3|14|SLOW_DIGEST|RES_COLD=1|COLD_2|||COLD_BOLT|7+1d7||48||
            'Pauraegen'|TV_GLOVES|Set of Gauntlets|1|25|11000|23|10|75|1d1|0|0|3|14||LIGHT=1,RES_ELEC=1,RES_LIGHT=1||||ELEC_BOLT|6+1d6||68||
            'Paurnen'|TV_GLOVES|Set of Gauntlets|1|25|12000|30|10|60|1d1|3|3|3|14|FEATHER|RES_ACID=1||||ACID_BOLT|5+1d5||76||
            'Camlost'|TV_GLOVES|Set of Gauntlets|10|25|0|5|10|100|1d1|-12|-12|3|0|AGGRAVATE,DRAIN_EXP,FREE_ACT|DEX=-3,RES_DISEN=1,RES_FIRE=1,STR=-3|||||||133||
            of Eöl|TV_GLOVES|Set of Gauntlets|55|25|40000|3|55|127|1d1|0|0|3|14|AGGRAVATE,FEATHER,FREE_ACT|INT=3,RES_DARK=1,RES_ELEC=1,RES_FIRE=3,RES_POIS=1||||MANA_BOLT|30+1d30||108||
            of Fingolfin|TV_GLOVES|Set of Caestus|40|40|110000|3|40|127|1d1|10|10|5|20|FREE_ACT|DEX=4,RES_ACID=1||||ARROW|30+1d30|Your {kind} grow{s} magical spikes...|141||
            of Elros|TV_SHIELD|Wicker Shield|40|30|60000|10|50|127|1d1|0|0|2|20|HOLD_LIFE,PROT_CONF|CON=4,INT=1,RES_ELEC=1,RES_SHARD=1,WIS=1|||||||128||
            of Thorin|TV_SHIELD|Small Metal Shield|40|65|60000|14|40|127|1d2|0|0|5|25|FREE_ACT,PROT_FEAR|CON=4,RES_ACID=3,RES_CHAOS=1,RES_SOUND=1,STEALTH=-1,STR=3|||||||135||
            of Celegorm|TV_SHIELD|Leather Shield|30|60|12000|28|30|100|1d3|0|0|8|20|PROT_BLIND|RES_ACID=1,RES_COLD=1,RES_DARK=1,RES_ELEC=1,RES_FIRE=1,RES_LIGHT=1|||||||185||
            of the Haradrim|TV_SHIELD|Leather Shield|35|90|25000|10|35|127|1d3|5|5|9|15|PROT_BLIND,PROT_FEAR,SUST_CON,SUST_STR|CON=2,RES_POIS=1,STEALTH=-2,STR=2||||BERSERKER|50+0d0||169||
            of Anárion|TV_SHIELD|Large Metal Shield|40|120|160000|11|40|100|1d5|0|0|12|20|SUST_CON,SUST_DEX,SUST_INT,SUST_STR,SUST_WIS|RES_ACID=1,RES_COLD=1,RES_ELEC=1,RES_FIRE=1|||||||98||
            of Eärendil|TV_SHIELD|Knight's Shield|32|160|90000|6|65|127|1d6|0|0|10|15|PROT_BLIND|LIGHT=1,RES_DARK=1,RES_ELEC=1,RES_FIRE=1,RES_NETHER=1||||CURE_TEMP|100+0d0|Your {kind} throbs deep blue...|130||
            of Gil-galad|TV_SHIELD|Mithril Shield|30|80|65000|25|70|127|1d4|0|0|20|20|SUST_DEX,SUST_WIS|LIGHT=1,RES_ACID=1,RES_DARK=1,RES_DISEN=1,RES_ELEC=1,WIS=5||||STARLIGHT2|100+0d0||138||
            of Barahir|TV_RING|Ring|50|2|65000|4|50|100||0|0|0|0||CON=1,DEX=1,INT=1,RES_DARK=1,RES_POIS=1,STEALTH=1,STR=1,WIS=1|||||||128|=|d
            of Tulkas|TV_RING|Band|60|2|150000|2|70|127||0|0|0|0|PROT_FEAR|CON=4,DEX=4,STR=4||||HASTE2|150+1d150||61|=|d
            'Narya'|TV_RING|Ring of Fire|30|2|100000|2|70|127|0d0|2|2|0|0|FREE_ACT,PROT_FEAR,REGEN,SEE_INVIS,SLOW_DIGEST,SUST_CON,SUST_STR,TELEPATHY|CON=1,DEX=1,INT=1,RES_FIRE=3,SPEED=5,STR=1,WIS=1||||PROTEVIL|200+4d50|{name} glows deep red...|175|=|d
            'Nenya'|TV_RING|Ring of Adamant|40|2|200000|2|80|127|0d0|3|3|0|0|FEATHER,FREE_ACT,HOLD_LIFE,PROT_BLIND,REGEN,SEE_INVIS,SUST_CON,SUST_INT,SUST_WIS,TELEPATHY|CON=2,DEX=2,INT=2,RES_COLD=3,SPEED=5,STR=2,WIS=2||||RESTORE_LIFE|200+2d50|{name} glows bright white...|178|=|d
            'Vilya'|TV_RING|Ring of Firmament|80|2|300000|1|90|127|0d0|5|5|0|0|FEATHER,FREE_ACT,HOLD_LIFE,REGEN,SEE_INVIS,SLOW_DIGEST,SUST_CON,SUST_DEX,SUST_INT,TELEPATHY|CON=3,DEX=3,INT=3,RES_DARK=1,RES_ELEC=3,RES_POIS=1,SPEED=5,STR=3,WIS=3||||HEAL2|200+2d50|{name} glows deep blue...|250|=|d
            'The One Ring'|TV_RING|Ring of Power|75|2|5000000|1|100|127|0d0|15|15|0|0|AGGRAVATE,DRAIN_EXP,REGEN,SEE_INVIS,STICKY,SUST_CON,SUST_DEX,SUST_INT,SUST_STR,SUST_WIS,TELEPATHY|CON=5,DEX=5,INT=5,RES_ACID=3,RES_COLD=3,RES_DARK=1,RES_DISEN=1,RES_ELEC=3,RES_FIRE=3,RES_NETHER=1,RES_POIS=1,SPEED=5,STR=5,WIS=5||||BIZARRE|200+9d50||304|=|y
            of Carlammas|TV_AMULET|Amulet|30|3|60000|10|50|100||0|0|0|0||CON=3,RES_FIRE=1,STR=1||||PROTEVIL|225+1d225||73|\"|d
            of Ingwë|TV_AMULET|Pendant|65|3|90000|3|65|127||0|0|0|0|FREE_ACT,PROT_CONF,SEE_INVIS|INFRA=4,INT=3,RES_ACID=1,RES_COLD=1,RES_ELEC=1,WIS=3||||DISPEL_EVIL|50+1d50||97|\"|d
            of the Dwarves|TV_AMULET|Necklace|50|3|75000|2|50|127||0|0|0|0|FREE_ACT,PROT_FEAR,REGEN,SEE_INVIS|CON=3,INFRA=5,LIGHT=1,STR=3|||||||299|\"|y
            'Elessar'|TV_AMULET|Elfstone|60|3|40000|2|60|127|0d0|0|0|0|10|PROT_FEAR|RES_FIRE=1,RES_POIS=1,SPEED=2,STR=2,WIS=2||||HEAL1|200+0d0||196|\"|g
            'Evenstar'|TV_AMULET|Jewel|40|3|25000|2|30|70||0|0|0|0|HOLD_LIFE,REGEN,SUST_CON,SUST_WIS|RES_COLD=1,RES_DARK=1,RES_NETHER=1,WIS=2||||RESTORE_LIFE|150+0d0||90|\"|w
            'Razorback'|TV_DRAG_ARMOR|Multi-Hued Dragon Scale Mail|60|200|400000|11|90|127|2d4|-4|0|50|25|AGGRAVATE,FREE_ACT,SEE_INVIS|LIGHT=1,RES_ACID=1,RES_COLD=1,RES_DARK=1,RES_DISEN=1,RES_ELEC=3,RES_FIRE=1,RES_LIGHT=1,RES_POIS=1||||STAR_BALL|50+0d0||180||
            'Mediator'|TV_DRAG_ARMOR|Balance Dragon Scale Mail|60|250|400000|8|95|127|2d4|-4|0|60|25|FREE_ACT,REGEN,SLOW_DIGEST|RES_CHAOS=1,RES_DISEN=1,RES_NEXUS=1,RES_SHARD=1,RES_SOUND=1||||STAR_BALL|50+0d0||135||
            'Bladeturner'|TV_DRAG_ARMOR|Power Dragon Scale Mail|55|250|500000|6|100|127|2d4|-8|0|80|35|HOLD_LIFE,PROT_BLIND,PROT_CONF,PROT_FEAR,REGEN|RES_ACID=1,RES_CHAOS=1,RES_COLD=1,RES_DARK=1,RES_DISEN=1,RES_ELEC=1,RES_FIRE=1,RES_LIGHT=1,RES_NETHER=1,RES_NEXUS=1,RES_POIS=1,RES_SHARD=1,RES_SOUND=1||||RAGE_BLESS_RESIST|50+0d0||143||
            'Grond'|TV_HAFTED|Mighty Hammer|100|1000|500000|0|100|100|9d9|5|25|0|10|AGGRAVATE,IMPACT,SEE_INVIS,TELEPATHY|||ANIMAL_2,DEMON_5,DRAGON_5,EVIL_2,ORC_3,TROLL_3,UNDEAD_5|||||143||
            of Morgoth|TV_CROWN|Massive Iron Crown|100|400|10000000|0|100|100|1d1|0|0|0|0|PROT_CONF,PROT_FEAR,SEE_INVIS,STICKY,TELEPATHY|CON=50,DEX=50,INFRA=50,INT=50,LIGHT=1,RES_ACID=1,RES_COLD=1,RES_DARK=1,RES_ELEC=1,RES_FIRE=1,RES_LIGHT=1,RES_NETHER=1,RES_NEXUS=1,RES_POIS=1,STR=50,WIS=50|||||||181||
            """;
}
