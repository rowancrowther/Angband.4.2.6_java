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
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link ItemObjectReader}: file text -> {@code ItemObjectLexer}/
 * {@code ItemObjectGrammar} -> {@code ItemObjectParseRecord} -> {@code ItemObjectAssembler} ->
 * {@link ObjectKind}.
 *
 * <p>The object-kind assembler resolves the {@code type:} tval against {@code object_base.txt},
 * brands/slays/curses against their registries, and {@code effect:SHAPECHANGE:} subtypes against
 * loaded {@link uk.co.jackoftrades.middle.player.PlayerShape}s. Rather than run the whole heavy
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
        Field f = GameConstants.class.getDeclaredField(field);
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
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown type")),
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
     * power, 0 meaning absent (obj-init.c:2130-2132); the port keys by {@link Curse} and carries the
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
        Map<Curse, Curse.CurseEntry> curses = kindField(result.items().get(0), "curses");

        assertEquals(1, curses.size());
        Curse curse = curses.keySet().iterator().next();
        assertEquals("teleportation", curse.getName());
        assertEquals(curse, curses.get(curse).curse(), "entry's curse must match its key");
        assertEquals(100, curses.get(curse).curseData().getPower());
        assertEquals(0, curses.get(curse).curseData().getTimeout(), "timeout is runtime-only");
    }

    /**
     * C: {@code /* Only add if it has power. *}{@code /  if (power > 0)} (obj-init.c:2129-2132). A
     * zero-power curse line is not an error — the curse is simply not recorded on the kind.
     */
    @Test
    void powerZeroCurseIsNotRecorded() throws Exception {
        ParseResult<ObjectKind> result = load("zero-curse.txt", withHeader(1,
                obj("uncursed blade", HEAD + "curse:teleportation:0\n")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size(), "a zero-power curse is not an error; the kind loads");
        Map<Curse, Curse.CurseEntry> curses = kindField(result.items().get(0), "curses");
        assertTrue(curses.isEmpty(), () -> "power 0 must not be recorded, got: " + curses);
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
}
