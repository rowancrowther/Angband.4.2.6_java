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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.player.PlayerBlow;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link ShapeReader}: file text -> {@code ShapeLexer}/
 * {@code ShapeGrammar} -> {@code ShapeParseRecord} -> {@code ShapeAssembler} -> {@link PlayerShape}.
 * These pin the assembler half — the two-family split of the {@code values:} line (additive
 * {@code obj_mods} modifiers vs per-element {@code RES_} resistances), accumulation of repeatable
 * lines ({@code obj-flags}, {@code values}, {@code blow}), the name-only record, and both error
 * channels {@code GrammarDriver} threads through {@link ParseResult}: the soft skip-and-continue
 * {@code errors} list and the hard fail-closed channel.
 *
 * <p>Unlike {@code CurseReaderTest}, no registry seeding is required: every effect subtype used by
 * {@code shape.txt} ({@code DAMAGE}, {@code CURE}, {@code TIMED_INC}, {@code PROJECT_LOS}) resolves
 * through {@code EffectAssembler} via compile-time enums, so nothing needs {@code GameConstants}
 * loaded first.
 *
 * @author Rowan Crowther
 */
class ShapeReaderTest {

    private static final String REAL_FILE = "lib/gamedata/shape.txt";

    @TempDir
    Path tempDir;

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A shape record: a name line followed by whatever body lines the test supplies.
     */
    private static String shape(String name, String body) {
        return "name:" + name + "\n" + body;
    }

    private ParseResult<PlayerShape> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new ShapeReader().parseWithResults(file.toString());
    }

    private static PlayerShape byName(List<PlayerShape> shapes, String name) {
        return shapes.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllNineShapesWithNoErrors() throws IOException {
        ParseResult<PlayerShape> result = new ShapeReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(9, result.items().size());
    }

    @Test
    void nameOnlyNormalShapeLoadsWithEmptyContributions() throws IOException {
        // 'normal' is a bare 'name:' line — the record must load with everything at its default.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();
        PlayerShape normal = byName(shapes, "normal");

        assertNotNull(normal);
        assertEquals(0, normal.getToHit());
        assertEquals(0, normal.getToDam());
        assertEquals(0, normal.getToAc());
        assertTrue(normal.getSkills().isEmpty());
        assertTrue(normal.getObjectValueModifiers().isEmpty());
        assertTrue(normal.getElementValueModifiers().isEmpty());
        assertTrue(normal.getEffect().isEmpty());
        assertEquals(0, normal.getNumBlows());
        assertTrue(normal.getPlayerBlow().isEmpty());
    }

    @Test
    void combatAndSkillsResolveOnTheRealFile() throws IOException {
        // 'bear' combat:15:15:5 -> to-h/to-d/to-a; skills disarm-phys/-magic/melee.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();
        PlayerShape bear = byName(shapes, "bear");

        assertEquals(15, bear.getToHit());
        assertEquals(15, bear.getToDam());
        assertEquals(5, bear.getToAc());
        assertEquals(-5, bear.getSkills().get(PlayerSkill.SKILL_DISARM_PHYS));
        assertEquals(-10, bear.getSkills().get(PlayerSkill.SKILL_DISARM_MAGIC));
        assertEquals(10, bear.getSkills().get(PlayerSkill.SKILL_TO_HIT_MELEE));
    }

    @Test
    void valuesLineSplitsIntoModifiersAndResistancesAndMergesRepeatedLines() throws IOException {
        // 'Pukel-man' has two values: lines. Line 1 mixes obj_mods and RES_; line 2 adds DAM_RED.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();
        PlayerShape pukel = byName(shapes, "Pukel-man");

        // obj_mods half (incl. the second line's DAM_RED — proves the two lines merged).
        assertEquals(4, pukel.getObjectValueModifiers().get(ObjectModifier.OM_STR));
        assertEquals(-5, pukel.getObjectValueModifiers().get(ObjectModifier.OM_SPEED));
        assertEquals(10, pukel.getObjectValueModifiers().get(ObjectModifier.OM_DAM_RED));
        // RES_ half -> elInfo with the bracketed level preserved.
        assertEquals(3, pukel.getElementValueModifiers().get(ElementEnum.ELEM_POIS).getResLevel());
        assertEquals(1, pukel.getElementValueModifiers().get(ElementEnum.ELEM_SHARD).getResLevel());
        // The resistances must NOT have leaked into the additive modifier map.
        assertFalse(pukel.getObjectValueModifiers().containsKey(ObjectModifier.OM_STR)
                && pukel.getElementValueModifiers().containsKey(ElementEnum.ELEM_POIS)
                && pukel.getObjectValueModifiers().size() > 5);
    }

    @Test
    void objectFlagsAccumulateAcrossTwoLines() throws IOException {
        // FLAG coverage (happy path): 'eagle' declares obj-flags over two lines; both must apply.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();
        PlayerShape eagle = byName(shapes, "eagle");

        assertTrue(eagle.getFlags().has(ObjectFlag.OF_SUST_WIS));   // first line
        assertTrue(eagle.getFlags().has(ObjectFlag.OF_TRAP_IMMUNE)); // second line
    }

    @Test
    void repeatedBlowsAreAllKeptWithDuplicates() throws IOException {
        // 'bear' lists 7 blows including 'hit' twice — duplicates weight frequency, so all are kept.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();
        PlayerShape bear = byName(shapes, "bear");

        assertEquals(7, bear.getNumBlows());
        assertEquals(7, bear.getPlayerBlow().size());
        long hits = bear.getPlayerBlow().stream()
                .map(PlayerBlow::getBlowName).filter("hit"::equals).count();
        assertEquals(2, hits);
    }

    @Test
    void effectsAreAssembledOnTheRealFile() throws IOException {
        // 'bat' fires a DAMAGE effect on taking the shape.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();
        PlayerShape bat = byName(shapes, "bat");

        assertFalse(bat.getEffect().isEmpty());
    }

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void unknownObjectFlagIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerShape> result = load("bad-oflag.txt", withHeader(1,
                shape("weird", "obj-flags:NOTAFLAG\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid object flag")),
                result.errors()::toString);
    }

    @Test
    void unknownPlayerFlagIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerShape> result = load("bad-pflag.txt", withHeader(1,
                shape("weird", "player-flags:NOTREAL\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid player flag")),
                result.errors()::toString);
    }

    @Test
    void unknownModifierIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerShape> result = load("bad-mod.txt", withHeader(1,
                shape("weird", "values:NOTAMOD[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid modifier name")),
                result.errors()::toString);
    }

    @Test
    void unknownResistanceElementIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerShape> result = load("bad-res.txt", withHeader(1,
                shape("weird", "values:RES_NOTREAL[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid element name")),
                result.errors()::toString);
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordCountMismatchIsReportedButValidShapeStillLoads() throws IOException {
        ParseResult<PlayerShape> result = load("bad-count.txt", withHeader(5,
                shape("plain", "combat:0:0:0\n")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<PlayerShape> result = load("no-header.txt", shape("plain", "combat:0:0:0\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        ParseResult<PlayerShape> result = load("no-name.txt", withHeader(1, "combat:0:0:0\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
