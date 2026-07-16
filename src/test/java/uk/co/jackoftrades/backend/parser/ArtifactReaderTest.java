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
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link ArtifactReader}: file text -> {@code ArtifactLexer}/
 * {@code ArtifactGrammar} -> {@link uk.co.jackoftrades.backend.parser.artifact.ArtifactParseRecord}
 * -> {@link uk.co.jackoftrades.backend.parser.artifact.ArtifactAssembler} -> {@link Artifact}.
 *
 * <p>The artifact assembler resolves four registries through {@link GameConstants} — brands by code,
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
 * <p>{@link Artifact} exposes no getters, so field-level assertions read its private fields
 * reflectively via {@link #field}.
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
    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String ACTIVATION_FILE = "lib/gamedata/activation.txt";

    private static Object savedPains, savedBases, savedSummons, savedSlays,
            savedBrands, savedObjectBases, savedCurses, savedActivations;

    /**
     * The one shared load of the real file; the happy-path test asserts against this.
     */
    private static ParseResult<Artifact> result;

    @TempDir
    Path tempDir;

    /**
     * Seeds the registries the artifact assembler resolves against, then loads the real
     * {@code artifact.txt} once for the happy-path test.
     *
     * @author Rowan Crowther
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

        result = new ArtifactReader().parseWithResults(ARTIFACT_FILE);
    }

    /**
     * Restores the registries mutated by {@link #seed()} so the shared static state on
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
        setStatic("activations", savedActivations);
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
        Field f = GameConstants.class.getDeclaredField(field);
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

        Activation act = field(phial, "activation");           // act:ILLUMINATION
        assertNotNull(act);
        assertEquals("ILLUMINATION", act.getName());
        assertNotNull(field(phial, "time"));                   // time:10+d10 parsed to a Random
        assertTrue(((String) field(phial, "text"))
                .contains("light of Eärendil's Star"));   // desc: accumulates

        // ---- Angrist: the brand/slay leg resolved against the seeded registries ----
        Artifact angrist = byName("'Angrist'");
        assertNotNull(angrist);
        List<Brand> brands = field(angrist, "brands");         // brand:ACID_3
        List<Slay> slays = field(angrist, "slays");            // slay:EVIL_2 / TROLL_3 / ORC_3
        assertEquals(1, brands.size());
        assertEquals("ACID_3", brands.getFirst().getCode());
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

        // curse:air swing:30 -- the one curse line in the file.
        Map<Curse, CurseData> curses = field(calris, "curses");
        assertEquals(1, curses.size());
        Curse curse = curses.keySet().iterator().next();
        assertEquals("air swing", curse.getName());
        assertEquals(30, curses.get(curse).getPower());
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
        //
        // RED, pinning a live defect: the base-object leg does not report at all. The assembler
        // wraps TValue.fromName in a catch for IllegalArgumentException, but fromName RETURNS NULL
        // for an unknown name (TValue.java:465) and never throws, so that catch is dead code and
        // "notatval" yields an artifact with a null tValue instead of an error. The BadTval record
        // is the only survivor of this file. Fix is a null check on the fromName result rather than
        // (or as well as) the catch; this test goes green once the site reports.
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
}
