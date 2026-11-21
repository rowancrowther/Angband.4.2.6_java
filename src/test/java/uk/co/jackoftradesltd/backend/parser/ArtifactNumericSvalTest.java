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
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * What {@code ArtifactReader} does with a {@code base-object:} line whose tval or sval is a number,
 * measured against {@code parse_artifact_base_object} ({@code obj-init.c}), {@code tval_find_idx}
 * ({@code obj-tval.c}) and {@code lookup_sval} ({@code obj-util.c}).
 *
 * <p>In C, an all-digit sval below {@code INT_MAX} is the sval itself, whether or not a kind has it,
 * and {@code write_dummy_object_record} is not run. Anything {@code strtoul} does not read as a whole
 * number below {@code INT_MAX} (a minus sign on anything but zero, {@code INT_MAX} itself, trailing
 * text, no digits) is a name, and a name that matches no kind gets a dummy kind. A directive that then
 * calls {@code lookup_kind} ({@code weight:}, {@code cost:} and {@code graphics:} assert on the
 * result; {@code act:} and {@code time:} read it only for a light) crashes where no kind exists, and
 * the port throws a {@link DataFileParseException} naming the line instead.
 *
 * <p>These tests mint kinds, so they live apart from {@code ArtifactReaderTest}, whose tests count
 * the kind table. Each test that mints a kind uses an sval no other test uses, and counts the table
 * before and after, so the order the tests run in does not matter.
 *
 * <p>Class ArtifactNumericSvalTest coded on 261121, commented in full on 261121.
 *
 * @author Rowan Crowther
 */
class ArtifactNumericSvalTest {

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

    private static Object savedPains, savedBases, savedSummons, savedSlays, savedBrands,
            savedObjectBases, savedCurses, savedActivations, savedObjectKinds,
            savedKindsByTvalSval, savedObjectBaseKindMax, savedPlayerShapes;

    @TempDir
    Path tempDir;

    /**
     * Seeds the registries the artifact assembler resolves against, as {@code ArtifactReaderTest}
     * does, but does not load {@code artifact.txt}: every record here is written by the test.
     *
     * <p>Function seed coded on 261121, commented in full on 261121.
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
        savedActivations = setStatic("activations", new ActivationReader().parse(ACTIVATION_FILE));
        savedPlayerShapes = setStatic("playerShapes", new ShapeReader().parse(SHAPE_FILE));

        savedObjectKinds = setStatic("objectKinds", new ArrayList<ObjectKind>());
        savedKindsByTvalSval = setStatic("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        for (ObjectKind kind : new ItemObjectReader().parse(OBJECT_FILE)) {
            ObjectRegistry.addObjectKind(kind);
        }
        savedObjectBaseKindMax = setStatic("objectBaseKindMax", ObjectRegistry.getObjectKindCount());
    }

    /**
     * Puts back the registries {@link #seed()} replaced, so the kinds these tests mint do not reach
     * another test class.
     *
     * <p>Function restore coded on 261121, commented in full on 261121.
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

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * Writes {@code content} to a temp file and drives it through the reader.
     */
    private ParseResult<Artifact> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new ArtifactReader().parseWithResults(file.toString());
    }

    /**
     * Loads one artifact named X with {@code body} as its lines after the name.
     */
    private Artifact loadOne(String fileName, String body) throws IOException {
        ParseResult<Artifact> r = load(fileName, "name:X\n" + body);
        assertEquals(1, r.items().size());
        return r.items().get(0);
    }

    /**
     * Loads {@code content} and insists it stops with a {@link DataFileParseException} whose message
     * names {@code fragment}.
     */
    private void assertStops(String fileName, String content, String fragment) {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load(fileName, content),
                fileName + " should have stopped the load");
        assertTrue(e.getMessage().contains(fragment), () -> "expected '" + fragment + "' in: " + e.getMessage());
    }

    // ---- the tval ----------------------------------------------------------

    /**
     * {@code tval_find_idx} reads a number below {@code TV_MAX} as the tval itself, so the sword's own
     * number names the sword and the Dagger kind is found as usual.
     *
     * <p>Function aNumericTvalNamesThatTvalAsInC coded on 261121, commented in full on 261121.
     */
    @Test
    void aNumericTvalNamesThatTvalAsInC() throws IOException {
        int kinds = ObjectRegistry.getObjectKindCount();

        Artifact artifact = loadOne("tval-number.txt", "base-object:" + TValue.TV_SWORD.ordinal() + ":Dagger\n");

        assertSame(TValue.TV_SWORD, artifact.gettValue());
        assertEquals(kinds, ObjectRegistry.getObjectKindCount(), "Dagger is a real kind, so none is made");
    }

    /**
     * A number that is not below {@code TV_MAX} is no tval: {@code tval_find_idx} answers -1 and
     * {@code parse_artifact_base_object} returns {@code PARSE_ERROR_UNRECOGNISED_TVAL}.
     *
     * <p>Function aNumericTvalPastTheLastTvalIsRefusedAsInC coded on 261121, commented in full on
     * 261121.
     */
    @Test
    void aNumericTvalPastTheLastTvalIsRefusedAsInC() {
        int past = TValue.values().length;

        assertStops("tval-past-max.txt", "name:X\nbase-object:" + past + ":Dagger\n",
                "unknown base-obj tvalue: " + past);
    }

    // ---- an sval that is a number ------------------------------------------

    /**
     * {@code lookup_sval} returns an all-digit sval as it stands, and {@code lookup_kind} then finds
     * the kind that has it. Sword sval 1 exists, so nothing is made.
     *
     * <p>Function aNumericSvalNamingAKindFindsThatKindAsInC coded on 261121, commented in full on
     * 261121.
     */
    @Test
    void aNumericSvalNamingAKindFindsThatKindAsInC() throws IOException {
        int kinds = ObjectRegistry.getObjectKindCount();

        Artifact artifact = loadOne("sval-number.txt", "base-object:sword:1\n");

        assertSame(TValue.TV_SWORD, artifact.gettValue());
        assertEquals(kinds, ObjectRegistry.getObjectKindCount());
        assertNotNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, 1));
    }

    /**
     * Each of these is a whole number below {@code INT_MAX} to {@code strtoul}, once the blanks C
     * skips are allowed for, so C stores it as the sval and runs no {@code write_dummy_object_record}.
     * No light kind has sval 99, 2147483646 or 0, and none is made. {@code -0} is zero to
     * {@code strtoul}, which wraps a minus sign, so it is a number too.
     *
     * <p>Function aNumericSvalNamingNoKindMakesNoKindAsInC coded on 261121, commented in full on
     * 261121.
     */
    @Test
    void aNumericSvalNamingNoKindMakesNoKindAsInC() throws IOException {
        String[] numbers = {"99", "+99", "-0", " 99", "99 ", "99\t", "2147483646", "0"};
        int i = 0;
        for (String number : numbers) {
            int kinds = ObjectRegistry.getObjectKindCount();

            Artifact artifact = loadOne("number-" + i++ + ".txt", "base-object:light:" + number + "\n");

            assertSame(TValue.TV_LIGHT, artifact.gettValue(), "[" + number + "]");
            assertEquals(kinds, ObjectRegistry.getObjectKindCount(), "no kind for [" + number + "]");
        }
    }

    /**
     * The number path does not depend on the tval: {@code sword:99} is stored as sval 99 with no kind,
     * as {@code light:99} is.
     *
     * <p>Function aNumericSvalNamingNoKindLoadsOnAnyTvalAsInC coded on 261121, commented in full on
     * 261121.
     */
    @Test
    void aNumericSvalNamingNoKindLoadsOnAnyTvalAsInC() throws IOException {
        int kinds = ObjectRegistry.getObjectKindCount();

        Artifact artifact = loadOne("sword-99.txt", "base-object:sword:99\n");

        assertSame(TValue.TV_SWORD, artifact.gettValue());
        assertEquals(kinds, ObjectRegistry.getObjectKindCount());
    }

    // ---- an sval that looks like a number but is not one to C ---------------

    /**
     * {@code lookup_sval} answers -1 for each of these, so {@code write_dummy_object_record} makes a
     * dummy kind named after the text: a minus sign on anything but zero ({@code strtoul} wraps it past
     * {@code INT_MAX}), {@code INT_MAX} itself, a value too large for an {@code int}, trailing text, a
     * hex prefix, a second sign, and a sign or nothing but a sign. The dummy kind is an insta-art kind
     * and the artifact finds it again by name.
     *
     * <p>Function aNumberCRefusesAsAnSvalGetsADummyKindAsInC coded on 261121, commented in full on
     * 261121.
     */
    @Test
    void aNumberCRefusesAsAnSvalGetsADummyKindAsInC() throws IOException {
        String[] names = {"-1", "-5", "2147483647", "99999999999", "9999999999999999999999",
                "12abc", "0x10", "99 x", "+-5", "+", "-"};
        int i = 0;
        for (String name : names) {
            int kinds = ObjectRegistry.getObjectKindCount();

            loadOne("name-" + i++ + ".txt", "base-object:light:" + name + "\n");

            assertEquals(kinds + 1, ObjectRegistry.getObjectKindCount(), "one dummy kind for [" + name + "]");
            ObjectKind dummy = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, name);
            assertNotNull(dummy, "the artifact finds its kind again: [" + name + "]");
            assertEquals(name, dummy.getsValueName());
            assertTrue(dummy.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART), "[" + name + "]");
        }
    }

    /**
     * A dummy kind is an ordinary special kind to the directives that follow: {@code weight:} and
     * {@code cost:} are copied onto it, as {@code parse_artifact_weight} and {@code parse_artifact_cost}
     * do for a kind above {@code ordinary_kind_max}, and {@code graphics:} sets its glyph.
     *
     * <p>Function aDummyKindMadeFromANumberTakesItsDirectivesAsInC coded on 261121, commented in
     * full on 261121.
     */
    @Test
    void aDummyKindMadeFromANumberTakesItsDirectivesAsInC() throws IOException {
        loadOne("dummy-directives.txt",
                "base-object:light:-7\nweight:50\ncost:10\ntime:5\nact:ILLUMINATION\ngraphics:~:y\n");

        ObjectKind dummy = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "-7");
        assertNotNull(dummy);
        assertEquals(50, dummy.getWeight());
        assertEquals(10, dummy.getCost());
        assertEquals('~', dummy.getCharacter().getNarrowCharacter());
        assertEquals(1, dummy.getActivations().size(), "a special light keeps the activation on its kind");
    }

    // ---- the directives that need a kind that is not there ------------------

    /**
     * {@code parse_artifact_weight}, {@code parse_artifact_cost} and {@code parse_artifact_graphics}
     * call {@code lookup_kind} and {@code assert(k)}, so C stops on a numeric sval that names no kind,
     * whatever the tval. The port throws a {@link DataFileParseException} that names the directive
     * and its value (the accepted rule for every place C crashes on a data file).
     *
     * <p>Function aWeightCostOrGraphicsLineOnANumericSvalWithNoKindIsRefusedHereWhereCCrashes coded
     * on 261121, commented in full on 261121.
     */
    @Test
    void aWeightCostOrGraphicsLineOnANumericSvalWithNoKindIsRefusedHereWhereCCrashes() {
        for (String tval : new String[]{"light", "sword"}) {
            String base = "name:X\nbase-object:" + tval + ":99\n";
            assertStops(tval + "-weight.txt", base + "weight:50\n", "null kind value used at weight: 50");
            assertStops(tval + "-cost.txt", base + "cost:10\n", "null kind value used at cost: 10");
            assertStops(tval + "-graphics.txt", base + "graphics:~:y\n", "null kind value used at glyph: ~");
        }
    }

    /**
     * {@code parse_artifact_act} and {@code parse_artifact_time} read the kind only when the tval is a
     * light ({@code (a->tval == TV_LIGHT) && (k->kidx >= ...)}), so on a light with no kind C reads
     * through a null pointer and stops. The port throws, naming the directive and its value.
     *
     * <p>Function anActOrTimeLineOnALightWithANumericSvalAndNoKindIsRefusedHereWhereCCrashes coded on
     * 261121, commented in full on 261121.
     */
    @Test
    void anActOrTimeLineOnALightWithANumericSvalAndNoKindIsRefusedHereWhereCCrashes() {
        String base = "name:X\nbase-object:light:99\n";

        assertStops("light-act.txt", base + "act:ILLUMINATION\n", "null kind value used at activation: ILLUMINATION");
        assertStops("light-time.txt", base + "time:5\n", "null kind value used at time: 5");
    }

    /**
     * On any other tval the same two lines never read the kind in C: the {@code &&} stops at the
     * tval, so the line goes to the artifact and the record loads.
     *
     * <p>Function anActOrTimeLineOnANonLightWithANumericSvalAndNoKindLoadsAsInC coded on 261121,
     * commented in full on 261121.
     */
    @Test
    void anActOrTimeLineOnANonLightWithANumericSvalAndNoKindLoadsAsInC() throws IOException {
        int kinds = ObjectRegistry.getObjectKindCount();

        Artifact withAct = loadOne("sword-act.txt", "base-object:sword:99\nact:ILLUMINATION\n");
        Artifact withTime = loadOne("sword-time.txt", "base-object:sword:99\ntime:5\n");

        assertNotNull(withAct.getActivation(), "the activation is the artifact's own");
        assertNotNull(withTime.getTime());
        assertEquals(kinds, ObjectRegistry.getObjectKindCount());
    }

    // ---- parsing the file a second time (the birth re-parse) ----------------

    /**
     * A name that starts with a letter is looked up by name in C, and {@code lookup_sval} finds the
     * dummy kind the first parse made (its name, {@code "& Redo~"}, reads as {@code Redo} once the
     * markers are stripped). So the second parse makes none, and the port agrees.
     *
     * <p>Function aSecondParseOfANameFindsItsDummyKindAsInC coded on 261121, commented in full on
     * 261121.
     */
    @Test
    void aSecondParseOfANameFindsItsDummyKindAsInC() throws IOException {
        int kinds = ObjectRegistry.getObjectKindCount();

        loadOne("reparse-name-first.txt", "base-object:light:Redo\n");
        assertEquals(kinds + 1, ObjectRegistry.getObjectKindCount(), "the first parse makes the dummy kind");
        ObjectKind first = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Redo");

        loadOne("reparse-name-second.txt", "base-object:light:Redo\n");

        assertEquals(kinds + 1, ObjectRegistry.getObjectKindCount(), "the second parse finds it");
        assertSame(first, ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Redo"));
    }

    /**
     * For text that {@code strtoul} reads digits from but that is not a whole number below
     * {@code INT_MAX}, such as {@code -9}, {@code lookup_sval} answers -1 at once and never tries the
     * kind names. So C makes another dummy kind each time the file is parsed, and a character who is
     * born re-parses {@code artifact.txt}. The port finds the dummy the first parse made, by name,
     * so a second parse adds none. This is the accepted deviation recorded under "Artifact" in
     * {@code docs/Accepted_Deviations.md}.
     *
     * <p>Function aSecondParseOfADigitLedNonNumberFindsItsDummyKindHereWhereCMakesAnother coded on
     * 261121, commented in full on 261121.
     */
    @Test
    void aSecondParseOfADigitLedNonNumberFindsItsDummyKindHereWhereCMakesAnother() throws IOException {
        int kinds = ObjectRegistry.getObjectKindCount();

        loadOne("reparse-number-first.txt", "base-object:light:-9\n");
        assertEquals(kinds + 1, ObjectRegistry.getObjectKindCount(), "the first parse makes the dummy kind");
        ObjectKind first = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "-9");

        loadOne("reparse-number-second.txt", "base-object:light:-9\n");

        assertEquals(kinds + 1, ObjectRegistry.getObjectKindCount(),
                "the second parse finds the dummy kind, where C makes another");
        assertSame(first, ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "-9"));
    }
}
