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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
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
 * Pins the level {@code ArtifactAssembler} gives a special artifact's kind against the order of the
 * record's lines, as C's {@code obj-init.c} sets it.
 *
 * <p>C reads the lines in file order. {@code parse_artifact_base_object} calls
 * {@code write_dummy_object_record} when the sval names no kind, and that copies {@code art->level}
 * as it stands at that moment: 0 if no {@code level:} line has been read yet, since
 * {@code parse_artifact_name} zeroes the record. A later {@code level:} line sets the artifact's
 * level only. An ordinary kind is never written, and a second run finds the kind it made before
 * and leaves its level alone. {@code parser_getint} reads the figure with {@code strtol} base 0,
 * so {@code 010} is 8.
 *
 * <p>Each test mints kinds under svals of its own, so this class seeds and restores its own
 * registries. Sharing {@code ArtifactReaderTest}'s would break that class's count of the 14 kinds
 * the shipped file makes. {@code ObjectKind} has no level getter, so the field is read by reflection.
 *
 * <p>Class ArtifactKindLevelOrderTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class ArtifactKindLevelOrderTest {

    private static final String[] REGISTRIES = {"monsterPains", "monsterBases", "summons", "slays",
            "brands", "objectBases", "curses", "activations", "playerShapes", "objectKinds",
            "kindsByTvalSval", "objectBaseKindMax"};
    private static final Object[] saved = new Object[REGISTRIES.length];

    @TempDir
    Path tempDir;

    /**
     * Seeds the registries the assembler resolves against, in the same dependency order as
     * {@code ArtifactReaderTest}, with {@code object.txt}'s kinds registered so the ordinary kind
     * limit is the real one.
     */
    @BeforeAll
    static void seed() throws Exception {
        saved[0] = setStatic("monsterPains", new PainReader().parse("lib/gamedata/pain.txt"));
        saved[1] = setStatic("monsterBases", new MonsterBaseReader().parse("lib/gamedata/monster_base.txt"));
        saved[2] = setStatic("summons", new SummonReader().parse("lib/gamedata/summon.txt"));
        saved[3] = setStatic("slays", new SlayReader().parse("lib/gamedata/slay.txt"));
        saved[4] = setStatic("brands", new BrandReader().parse("lib/gamedata/brand.txt"));
        saved[5] = setStatic("objectBases", new ObjectBaseReader().parse("lib/gamedata/object_base.txt"));
        saved[6] = setStatic("curses", new CurseReader().parse("lib/gamedata/curse.txt"));
        saved[7] = setStatic("activations", new ActivationReader().parse("lib/gamedata/activation.txt"));
        saved[8] = setStatic("playerShapes", new ShapeReader().parse("lib/gamedata/shape.txt"));
        saved[9] = setStatic("objectKinds", new ArrayList<ObjectKind>());
        saved[10] = setStatic("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        for (ObjectKind kind : new ItemObjectReader().parse("lib/gamedata/object.txt")) {
            ObjectRegistry.addObjectKind(kind);
        }
        saved[11] = setStatic("objectBaseKindMax", ObjectRegistry.getObjectKindCount());
    }

    /**
     * Puts back every registry {@link #seed()} replaced.
     */
    @AfterAll
    static void restore() throws Exception {
        for (int i = 0; i < REGISTRIES.length; i++) {
            setStatic(REGISTRIES[i], saved[i]);
        }
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static int kindLevel(TValue tVal, String sVal) throws ReflectiveOperationException {
        ObjectKind kind = ObjectRegistry.lookupObjectKind(tVal, sVal);
        assertNotNull(kind, sVal);
        Field f = ObjectKind.class.getDeclaredField("level");
        f.setAccessible(true);
        return (int) f.get(kind);
    }

    /**
     * Loads one record from {@code body} and returns its artifact, insisting on a clean load.
     */
    private Artifact loadOne(String fileName, String body) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, "record-count:1\nname:X\n" + body);
        ParseResult<Artifact> r = new ArtifactReader().parseWithResults(file.toString());
        assertFalse(r.hasErrors(), () -> r.errors().toString());
        assertEquals(1, r.items().size());
        return r.items().get(0);
    }

    @Test
    @DisplayName("level: after base-object: reaches the artifact only, so the kind is 0 (the shipped order)")
    void levelAfterBaseObjectLeavesTheKindAtZero() throws Exception {
        Artifact a = loadOne("after.txt", "base-object:light:OrderAfter\nlevel:7\n");
        assertEquals(7, a.getLevel());
        assertEquals(0, kindLevel(TValue.TV_LIGHT, "OrderAfter"));
    }

    @Test
    @DisplayName("level: before base-object: is copied to the new kind")
    void levelBeforeBaseObjectGivesTheKindThatLevel() throws Exception {
        Artifact a = loadOne("before.txt", "level:7\nbase-object:light:OrderBefore\n");
        assertEquals(7, a.getLevel());
        assertEquals(7, kindLevel(TValue.TV_LIGHT, "OrderBefore"));
    }

    @Test
    @DisplayName("no level: line leaves both the artifact and the kind at 0")
    void noLevelLineLeavesTheKindAtZero() throws Exception {
        Artifact a = loadOne("none.txt", "base-object:light:OrderNone\n");
        assertEquals(0, a.getLevel());
        assertEquals(0, kindLevel(TValue.TV_LIGHT, "OrderNone"));
    }

    @Test
    @DisplayName("level:5, base-object:, level:9 gives the kind 5 and the artifact 9")
    void aLaterLevelLineReachesTheArtifactButNotTheKind() throws Exception {
        Artifact a = loadOne("both.txt", "level:5\nbase-object:light:OrderBoth\nlevel:9\n");
        assertEquals(9, a.getLevel(), "the last level: wins for the artifact");
        assertEquals(5, kindLevel(TValue.TV_LIGHT, "OrderBoth"), "the kind keeps the level in force at base-object:");
    }

    @Test
    @DisplayName("other lines between level: and base-object: do not change what the kind takes")
    void linesBetweenDoNotChangeTheKindsLevel() throws Exception {
        loadOne("between.txt", "level:7\nweight:10\ncost:300\nbase-object:light:OrderBetween\n");
        assertEquals(7, kindLevel(TValue.TV_LIGHT, "OrderBetween"));
    }

    @Test
    @DisplayName("level:010 before base-object: is 8 on both, as strtol base 0 reads it")
    void anOctalLevelIsReadAsCReadsIt() throws Exception {
        Artifact a = loadOne("octal.txt", "level:010\nbase-object:light:OrderOctal\n");
        assertEquals(8, a.getLevel());
        assertEquals(8, kindLevel(TValue.TV_LIGHT, "OrderOctal"));
    }

    @Test
    @DisplayName("a negative level: before base-object: is copied as it is")
    void aNegativeLevelIsCopiedAsItIs() throws Exception {
        loadOne("negative.txt", "level:-3\nbase-object:light:OrderNegative\n");
        assertEquals(-3, kindLevel(TValue.TV_LIGHT, "OrderNegative"));
    }

    @Test
    @DisplayName("an ordinary kind keeps its object.txt level whatever the line order")
    void anOrdinaryKindKeepsItsObjectTxtLevel() throws Exception {
        assertEquals(3, kindLevel(TValue.TV_LIGHT, "Lantern"), "object.txt gives & Lantern~ level 3");
        loadOne("ordinary.txt", "level:20\nbase-object:light:Lantern\n");
        assertEquals(3, kindLevel(TValue.TV_LIGHT, "Lantern"));
    }

    @Test
    @DisplayName("a second run, as at birth, finds the kind and leaves its level alone")
    void aSecondRunLeavesAnExistingSpecialKindsLevelAlone() throws Exception {
        loadOne("first.txt", "level:7\nbase-object:light:OrderRerun\n");
        int kinds = ObjectRegistry.getObjectKindCount();

        Artifact again = loadOne("second.txt", "level:12\nbase-object:light:OrderRerun\n");

        assertEquals(kinds, ObjectRegistry.getObjectKindCount(), "the kind is found, not made again");
        assertEquals(12, again.getLevel());
        assertEquals(7, kindLevel(TValue.TV_LIGHT, "OrderRerun"));
    }
}
