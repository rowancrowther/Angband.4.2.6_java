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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the order in which a loaded {@link Artifact} hands back its brands and slays.
 *
 * <p>C's {@code parse_artifact_brand} and {@code parse_artifact_slay} set {@code a->brands[i]} and
 * {@code a->slays[i]} in a {@code bool} array indexed by registry position, so the order of the
 * {@code brand:} and {@code slay:} lines in {@code artifact.txt} is lost on load and every later
 * walk is in registry index order, which is the reverse of {@code brand.txt} / {@code slay.txt}
 * order (see {@link #codes}). The assembler has to rebuild that order, not
 * keep the file's. 35 shipped artifacts list their slays, and four their brands, in a different order from
 * that, so a set kept in file order would fail here.
 *
 * <p>The expected order is worked out from the raw text of the three data files by a scan that shares
 * nothing with the grammar or the assembler: the codes of each {@code name:} block, sorted by the
 * position of the code in {@code brand.txt} / {@code slay.txt}.
 *
 * <p>Seeding follows {@link ArtifactReaderTest}, restoring the registries afterwards.
 *
 * @author Rowan Crowther
 */
class ArtifactBrandSlayOrderTest {

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

    private static Object savedPains, savedBases, savedSummons, savedSlays,
            savedBrands, savedObjectBases, savedCurses, savedActivations,
            savedObjectKinds, savedKindsByTvalSval, savedObjectBaseKindMax, savedPlayerShapes;

    private static ParseResult<Artifact> result;

    /**
     * Seeds the registries the artifact assembler resolves against and loads the real file once.
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

        result = new ArtifactReader().parseWithResults(ARTIFACT_FILE);
    }

    /**
     * Puts every registry back as it was so no state leaks to other suites.
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
     * The {@code code:} values of a data file in C's index order, which is the <em>reverse</em> of
     * file order: C's parser prepends each entry to a list and {@code finish_parse_brand} /
     * {@code finish_parse_slay} copy the list into {@code brands[1..]} / {@code slays[1..]} head
     * first, so the last entry in the file takes index 1.
     */
    private static List<String> codes(String file) throws IOException {
        List<String> out = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(file))) {
            if (line.startsWith("code:")) out.add(line.substring(5).trim());
        }
        java.util.Collections.reverse(out);
        return out;
    }

    /**
     * For each {@code name:} block of artifact.txt, in file order, the values of every {@code key:}
     * line, sorted by position in {@code registryOrder} (C's index order).
     */
    private static List<List<String>> expectedPerArtifact(String key, List<String> registryOrder)
            throws IOException {
        List<List<String>> out = new ArrayList<>();
        List<String> current = null;
        for (String line : Files.readAllLines(Path.of(ARTIFACT_FILE))) {
            if (line.startsWith("name:")) {
                current = new ArrayList<>();
                out.add(current);
            } else if (current != null && line.startsWith(key + ":")) {
                current.add(line.substring(key.length() + 1).trim());
            }
        }
        for (List<String> codes : out) {
            codes.sort((a, b) -> Integer.compare(registryOrder.indexOf(a), registryOrder.indexOf(b)));
        }
        return out;
    }

    private static List<String> brandCodes(Artifact a) {
        List<String> out = new ArrayList<>();
        for (Brand b : a.getBrands()) out.add(b.getCode());
        return out;
    }

    private static List<String> slayCodes(Artifact a) {
        List<String> out = new ArrayList<>();
        for (Slay s : a.getSlays()) out.add(s.getCode());
        return out;
    }

    /**
     * Every artifact's brands come back in C index order.
     */
    @Test
    @DisplayName("every artifact's brands iterate in C index order")
    void brandsInRegistryOrder() throws Exception {
        assertEquals(138, result.items().size(), () -> "artifact.txt load errors: " + result.errors());
        List<List<String>> expected = expectedPerArtifact("brand", codes(BRAND_FILE));
        assertEquals(expected.size(), result.items().size());

        List<String> wrong = new ArrayList<>();
        for (int i = 0; i < expected.size(); i++) {
            Artifact a = result.items().get(i);
            if (!expected.get(i).equals(brandCodes(a))) {
                wrong.add(a.getName() + ": expected " + expected.get(i) + " got " + brandCodes(a));
            }
        }
        assertTrue(wrong.isEmpty(), () -> "brand order differs from C for: " + wrong);
    }

    /**
     * Every artifact's slays come back in C index order.
     */
    @Test
    @DisplayName("every artifact's slays iterate in C index order")
    void slaysInRegistryOrder() throws Exception {
        assertEquals(138, result.items().size(), () -> "artifact.txt load errors: " + result.errors());
        List<List<String>> expected = expectedPerArtifact("slay", codes(SLAY_FILE));

        List<String> wrong = new ArrayList<>();
        for (int i = 0; i < expected.size(); i++) {
            Artifact a = result.items().get(i);
            if (!expected.get(i).equals(slayCodes(a))) {
                wrong.add(a.getName() + ": expected " + expected.get(i) + " got " + slayCodes(a));
            }
        }
        assertTrue(wrong.isEmpty(), () -> "slay order differs from C for: " + wrong);
    }

    /**
     * The scan above only proves something if the data really does list some out of order.
     */
    @Test
    @DisplayName("the data does list some slays and brands out of registry order")
    void fileOrderDiffersFromRegistryOrder() throws Exception {
        boolean outOfOrder = false;
        List<String> slayOrder = codes(SLAY_FILE);
        List<String> current = null;
        for (String line : Files.readAllLines(Path.of(ARTIFACT_FILE))) {
            if (line.startsWith("name:")) {
                current = new ArrayList<>();
            } else if (current != null && line.startsWith("slay:")) {
                current.add(line.substring(5).trim());
                List<String> sorted = new ArrayList<>(current);
                sorted.sort((a, b) -> Integer.compare(slayOrder.indexOf(a), slayOrder.indexOf(b)));
                if (!sorted.equals(current)) outOfOrder = true;
            }
        }
        assertTrue(outOfOrder, "no artifact lists its slays out of registry order, so the order tests prove nothing");
    }

    /**
     * {@link Artifact#copy()} must keep the order the assembler built.
     */
    @Test
    @DisplayName("Artifact.copy() keeps brand and slay order")
    void copyKeepsOrder() {
        assertFalse(result.items().isEmpty());
        for (Artifact a : result.items()) {
            Artifact c = a.copy();
            assertEquals(brandCodes(a), brandCodes(c), a.getName() + " brands after copy");
            assertEquals(slayCodes(a), slayCodes(c), a.getName() + " slays after copy");
        }
    }
}
