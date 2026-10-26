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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.PlayerBlow;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

    // ---- Reader contract: the C parser's three entry points ---------------
    //
    // C's shape_parser is init_parse_shape (the directive table), run_parse_shape (one call to
    // parse_file_quit_not_found) and finish_parse_shape (shapes = parser_priv(p)). The tests
    // below pin the file-level behaviour of those three; the per-directive handlers are
    // parse_shape_* in init.c.

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        // run_parse_shape: parse_file_quit_not_found ends the program when shape.txt cannot be opened.
        String missing = tempDir.resolve("no-such-shape.txt").toString();

        assertThrows(IOException.class, () -> new ShapeReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new ShapeReader().parse(missing));
    }

    @Test
    void parseReturnsTheSameShapesAsParseWithResults() throws IOException {
        List<PlayerShape> viaParse = new ShapeReader().parse(REAL_FILE);
        List<PlayerShape> viaResults = new ShapeReader().parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.stream().map(PlayerShape::getName).toList(),
                viaParse.stream().map(PlayerShape::getName).toList());
    }

    @Test
    void parseDiscardsTheErrorsSoAHardErrorLooksLikeAnEmptyFile() throws IOException {
        // The convenience form cannot tell a grammar error from an empty result.
        Path file = tempDir.resolve("hard.txt");
        Files.writeString(file, "record-count:1\nbogus:1\n");

        assertTrue(new ShapeReader().parse(file.toString()).isEmpty());
        assertTrue(new ShapeReader().parseWithResults(file.toString()).hasErrors());
    }

    @Test
    void shapesComeBackInFileOrderWhereCsListIsReversed() throws IOException {
        // C's finish_parse_shape installs a list built by head insertion: reverse file order, with
        // each sidx counted up in file order. The Java list is in file order, so its index is sidx.
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();

        assertEquals(List.of("normal", "fox", "Pukel-man", "bear", "eagle", "bat", "warg", "vampire",
                "werewolf"), shapes.stream().map(PlayerShape::getName).toList());
    }

    @Test
    void twoShapesWithOneNameBothLoadInFileOrder() throws IOException {
        // C's lookup_player_shape walks the reversed list, so it finds the LAST of two same-named
        // shapes; the Java list is in file order and PlayerRegistry looks up first-match.
        ParseResult<PlayerShape> result = load("dup-name.txt", withHeader(2,
                shape("twin", "combat:1:1:1\n") + shape("twin", "combat:2:2:2\n")));

        assertFalse(result.hasErrors(), result.errors()::toString);
        assertEquals(2, result.items().size());
        assertEquals(1, result.items().get(0).getToHit());
        assertEquals(2, result.items().get(1).getToHit());
    }

    // ---- The nine shipped shapes against shape.txt -----------------------
    //
    // Expected values are read off the upstream lib/gamedata/shape.txt, not off the Java output.

    private record Row(String name, int toh, int tod, int toa,
                       Map<PlayerSkill, Integer> skills,
                       List<ObjectFlag> objectFlags, List<PlayerFlag> playerFlags,
                       Map<ObjectModifier, Integer> mods, Map<ElementEnum, Integer> resists,
                       List<String> blows, int effects) {
    }

    private static final List<Row> TABLE = List.of(
            new Row("normal", 0, 0, 0, Map.of(), List.of(), List.of(), Map.of(), Map.of(),
                    List.of(), 0),
            new Row("fox", -3, -3, 3, Map.of(),
                    List.of(ObjectFlag.OF_FEATHER, ObjectFlag.OF_FREE_ACT), List.of(),
                    Map.of(ObjectModifier.OM_STR, -3, ObjectModifier.OM_STEALTH, 5,
                            ObjectModifier.OM_BLOWS, 1, ObjectModifier.OM_MOVES, 1),
                    Map.of(), List.of("bite", "claw"), 0),
            new Row("Pukel-man", 0, 5, 20, Map.of(PlayerSkill.SKILL_SAVE, 20),
                    List.of(ObjectFlag.OF_SUST_STR, ObjectFlag.OF_SUST_CON, ObjectFlag.OF_PROT_STUN,
                            ObjectFlag.OF_REGEN, ObjectFlag.OF_HOLD_LIFE),
                    List.of(PlayerFlag.PF_ROCK),
                    Map.of(ObjectModifier.OM_STR, 4, ObjectModifier.OM_CON, 4,
                            ObjectModifier.OM_STEALTH, -2, ObjectModifier.OM_SPEED, -5,
                            ObjectModifier.OM_DAM_RED, 10),
                    Map.of(ElementEnum.ELEM_POIS, 3, ElementEnum.ELEM_SHARD, 1),
                    List.of("punch", "crush", "thump", "strike"), 2),
            new Row("bear", 15, 15, 5,
                    Map.of(PlayerSkill.SKILL_DISARM_PHYS, -5, PlayerSkill.SKILL_DISARM_MAGIC, -10,
                            PlayerSkill.SKILL_TO_HIT_MELEE, 10),
                    List.of(ObjectFlag.OF_PROT_FEAR), List.of(),
                    Map.of(ObjectModifier.OM_STR, 3, ObjectModifier.OM_INT, -2,
                            ObjectModifier.OM_CON, 2, ObjectModifier.OM_INFRA, 1,
                            ObjectModifier.OM_STEALTH, -3, ObjectModifier.OM_BLOWS, 1),
                    Map.of(), List.of("cuff", "clout", "claw", "hit", "hit", "swipe", "slash"), 0),
            new Row("eagle", 5, 0, 10, Map.of(),
                    List.of(ObjectFlag.OF_SUST_WIS, ObjectFlag.OF_PROT_BLIND, ObjectFlag.OF_PROT_CONF,
                            ObjectFlag.OF_PROT_FEAR, ObjectFlag.OF_SEE_INVIS, ObjectFlag.OF_FREE_ACT,
                            ObjectFlag.OF_TRAP_IMMUNE),
                    List.of(), Map.of(ObjectModifier.OM_MOVES, 3), Map.of(),
                    List.of("peck", "claw", "claw", "strike", "tear"), 0),
            new Row("bat", 0, -10, 10, Map.of(),
                    List.of(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SEE_INVIS, ObjectFlag.OF_PROT_BLIND),
                    List.of(),
                    Map.of(ObjectModifier.OM_SPEED, 3, ObjectModifier.OM_STEALTH, 3,
                            ObjectModifier.OM_INFRA, 5),
                    Map.of(), List.of("bite", "scratch"), 1),
            new Row("warg", 5, 5, 0, Map.of(), List.of(), List.of(),
                    Map.of(ObjectModifier.OM_BLOWS, 2), Map.of(),
                    List.of("bite", "bite", "claw", "tear"), 3),
            new Row("vampire", 3, 3, -5, Map.of(),
                    List.of(ObjectFlag.OF_SEE_INVIS, ObjectFlag.OF_HOLD_LIFE), List.of(),
                    Map.of(ObjectModifier.OM_LIGHT, -1, ObjectModifier.OM_STEALTH, 5,
                            ObjectModifier.OM_INFRA, 5, ObjectModifier.OM_SPEED, 5),
                    Map.of(), List.of("bite"), 2),
            new Row("werewolf", 5, 5, 0, Map.of(), List.of(ObjectFlag.OF_REGEN), List.of(),
                    Map.of(ObjectModifier.OM_BLOWS, 1, ObjectModifier.OM_MOVES, 1), Map.of(),
                    List.of("bite", "bite", "slash", "tear"), 3));

    /** The varargs {@code Flag} constructor refuses an empty list, so the empty set is built by hand. */
    private static <E extends Enum<E>> Flag<E> flagsOf(Class<E> type, List<E> flags) {
        Flag<E> result = new Flag<>(type);
        flags.forEach(result::on);
        return result;
    }

    @Test
    void everyShippedShapeAgreesWithTheUpstreamFile() throws IOException {
        List<PlayerShape> shapes = new ShapeReader().parseWithResults(REAL_FILE).items();

        assertEquals(TABLE.size(), shapes.size());
        for (int i = 0; i < TABLE.size(); i++) {
            Row row = TABLE.get(i);
            PlayerShape got = shapes.get(i);
            String at = row.name();

            assertEquals(row.name(), got.getName(), at);
            assertEquals(row.toh(), got.getToHit(), at + " to-h");
            assertEquals(row.tod(), got.getToDam(), at + " to-d");
            assertEquals(row.toa(), got.getToAc(), at + " to-a");
            assertEquals(row.skills(), got.getSkills(), at + " skills");
            assertTrue(got.getFlags().isEqual(flagsOf(ObjectFlag.class, row.objectFlags())), at + " obj-flags");
            assertTrue(got.getPflags().isEqual(flagsOf(PlayerFlag.class, row.playerFlags())), at + " player-flags");
            assertEquals(row.mods(), got.getObjectValueModifiers(), at + " modifiers");
            assertEquals(row.resists().keySet(), got.getElementValueModifiers().keySet(), at + " elements");
            row.resists().forEach((element, level) -> assertEquals(level,
                    got.getElementValueModifiers().get(element).getResLevel(), at + " " + element));
            assertEquals(row.blows(), got.getPlayerBlow().stream().map(PlayerBlow::getBlowName).toList(),
                    at + " blows");
            assertEquals(row.blows().size(), got.getNumBlows(), at + " num_blows");
            assertEquals(row.effects(), got.getEffect().size(), at + " effects");
        }
    }

    // ---- Repeated directives: what C's handlers overwrite and what they accumulate ----

    @Test
    void aRepeatedCombatOrSkillLineKeepsTheLastValueAsCDoes() throws IOException {
        // parse_shape_combat and parse_shape_skill_dig assign, so the second line wins.
        PlayerShape s = load("repeat.txt", withHeader(1, shape("r",
                "combat:1:1:1\ncombat:2:3:4\nskill-dig:1\nskill-dig:7\n"))).items().get(0);

        assertEquals(2, s.getToHit());
        assertEquals(3, s.getToDam());
        assertEquals(4, s.getToAc());
        assertEquals(7, s.getSkills().get(PlayerSkill.SKILL_DIGGING));
    }

    @Test
    void repeatedFlagAndValuesLinesAccumulate() throws IOException {
        // The flag handlers OR into shape->flags and the values handler writes by index.
        PlayerShape s = load("accumulate.txt", withHeader(1, shape("acc",
                "obj-flags:REGEN\nobj-flags:FREE_ACT\nplayer-flags:ROCK\n"
                        + "values:STR[1]\nvalues:INT[2] | RES_FIRE[1]\n"))).items().get(0);

        assertTrue(s.getFlags().has(ObjectFlag.OF_REGEN));
        assertTrue(s.getFlags().has(ObjectFlag.OF_FREE_ACT));
        assertTrue(s.getPflags().has(PlayerFlag.PF_ROCK));
        assertEquals(Map.of(ObjectModifier.OM_STR, 1, ObjectModifier.OM_INT, 2), s.getObjectValueModifiers());
        assertEquals(1, s.getElementValueModifiers().get(ElementEnum.ELEM_FIRE).getResLevel());
    }

    @Test
    void aValueNamedTwiceKeepsTheLastAsCDoes() throws IOException {
        PlayerShape s = load("dup-key.txt", withHeader(1, shape("d", "values:STR[1] | STR[2]\n")))
                .items().get(0);

        assertEquals(2, s.getObjectValueModifiers().get(ObjectModifier.OM_STR));
    }

    @Test
    void negativeCombatAndSkillFiguresAreAccepted() throws IOException {
        // parser_getint takes a leading minus; the shipped bear and bat rely on it.
        PlayerShape s = load("neg.txt", withHeader(1, shape("n", "combat:-5:-3:2\nskill-dig:-10\n")))
                .items().get(0);

        assertEquals(-5, s.getToHit());
        assertEquals(-3, s.getToDam());
        assertEquals(2, s.getToAc());
        assertEquals(-10, s.getSkills().get(PlayerSkill.SKILL_DIGGING));
    }

    @Test
    void anExplicitZeroSkillIsKept() throws IOException {
        PlayerShape s = load("zero.txt", withHeader(1, shape("z", "skill-dig:0\n"))).items().get(0);

        assertEquals(0, s.getSkills().get(PlayerSkill.SKILL_DIGGING));
    }

    // ---- Soft errors: one bad shape does not cost the others -------------

    @Test
    void aBadShapeIsDroppedAndTheNextOneStillLoads() throws IOException {
        ParseResult<PlayerShape> result = load("one-bad.txt", withHeader(2,
                shape("bad", "obj-flags:NOTAFLAG\n") + shape("good", "combat:1:2:3\n")));

        assertEquals(1, result.items().size());
        assertEquals("good", result.items().get(0).getName());
        assertEquals(1, result.errors().size(), result.errors()::toString);
    }

    @Test
    void anOversizedFigureIsASoftErrorThatDropsTheShape() throws IOException {
        ParseResult<PlayerShape> result = load("huge.txt", withHeader(1,
                shape("h", "skill-dig:99999999999\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid skill dig value")),
                result.errors()::toString);
    }

    @Test
    void anUnknownEffectStopsTheLoad() {
        // grab_effect_data's error from parse_shape_effect ends the C load. Record-count is
        // line 1, name 2 and the effect line 3, which is the line the message names.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-effect.txt",
                withHeader(1, shape("e", "effect:NOTANEFFECT\n"))));

        assertTrue(e.getMessage().contains("Effect starting at line: 3"), e::getMessage);
        assertTrue(e.getMessage().contains("illegal element value: NOTANEFFECT"), e::getMessage);
    }

    @Test
    void aNonNumericResistanceLevelIsAHardErrorHere() throws IOException {
        // The value lexer takes only an integer inside the brackets, so this never reaches the assembler.
        ParseResult<PlayerShape> result = load("res-x.txt", withHeader(1,
                shape("r", "values:RES_FIRE[x]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Where the grammar and assembler differ from C (docs/grammar_issues.md, docs/Assembler_Issues.md) ----

    @Test
    void anEmptyFileIsAHardErrorWhereCAcceptsOne() throws IOException {
        ParseResult<PlayerShape> result = load("empty.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anEmptyFlagLineIsAHardErrorWhereCTakesAnOptionalString() throws IOException {
        // "obj-flags ?str flags" and "player-flags ?str flags": C returns PARSE_ERROR_NONE with no flags.
        assertTrue(load("empty-of.txt", withHeader(1, shape("a", "obj-flags:\n"))).hasErrors());
        assertTrue(load("empty-pf.txt", withHeader(1, shape("a", "player-flags:\n"))).hasErrors());
    }

    @Test
    void namesSeparatedBySpacesAreAHardErrorWhereCSplitsOnSpaceAndBar() throws IOException {
        // strtok(s, " |") in parse_shape_obj_flags and parse_shape_values.
        assertTrue(load("sp-flags.txt", withHeader(1, shape("a", "obj-flags:REGEN FREE_ACT\n"))).hasErrors());
        assertTrue(load("sp-vals.txt", withHeader(1, shape("a", "values:STR[1] INT[2]\n"))).hasErrors());
    }

    @Test
    void aBarWithNoBlanksAroundItIsAccepted() throws IOException {
        PlayerShape s = load("tight.txt", withHeader(1, shape("t", "obj-flags:REGEN|FREE_ACT\nvalues:STR[1]|INT[2]\n")))
                .items().get(0);

        assertTrue(s.getFlags().has(ObjectFlag.OF_REGEN));
        assertTrue(s.getFlags().has(ObjectFlag.OF_FREE_ACT));
        assertEquals(Map.of(ObjectModifier.OM_STR, 1, ObjectModifier.OM_INT, 2), s.getObjectValueModifiers());
    }

    @Test
    void aBlankAfterValuesColonIsAHardErrorWhereCSkipsIt() throws IOException {
        // The upstream shape.txt writes "values: STR[3] | ..." for bear; the shipped copy had the blank removed.
        ParseResult<PlayerShape> result = load("lead-blank.txt", withHeader(1, shape("a", "values: STR[3]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aPlusSignOrBlankInAFigureIsAHardErrorWhereStrtolTakesThem() throws IOException {
        assertTrue(load("plus.txt", withHeader(1, shape("a", "combat:+5:0:0\n"))).hasErrors());
        assertTrue(load("plus-skill.txt", withHeader(1, shape("a", "skill-dig:+5\n"))).hasErrors());
        assertTrue(load("blank-skill.txt", withHeader(1, shape("a", "skill-dig: 5\n"))).hasErrors());
        assertTrue(load("blank-combat.txt", withHeader(1, shape("a", "combat: 1 : 2 : 3\n"))).hasErrors());
    }

    @Test
    void anEffectMessageBeforeTheFirstEffectIsAHardErrorWhereCDropsItSilently() throws IOException {
        // The upstream werewolf record opens its effects with an effect-msg line; parse_shape_effect_msg
        // returns PARSE_ERROR_NONE when shape->effect is NULL. The shipped copy moves that line down.
        ParseResult<PlayerShape> result = load("msg-first.txt", withHeader(1, shape("w",
                "effect-msg:taking form\neffect:DAMAGE\ndice:5\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void diceBeforeTheFirstEffectIsAHardErrorWhereCIgnoresIt() throws IOException {
        ParseResult<PlayerShape> result = load("dice-first.txt", withHeader(1, shape("w",
                "dice:5\neffect:DAMAGE\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aSecondEffectMessageIsAHardErrorWhereCAppendsIt() throws IOException {
        // parse_shape_effect_msg uses string_append, so two lines join in C.
        ParseResult<PlayerShape> result = load("msg-twice.txt", withHeader(1, shape("w",
                "effect:DAMAGE\ndice:5\neffect-msg:one\neffect-msg:two\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void theNoneAndMaxNamesAreAcceptedForModifiersAndElementsWhereCHasNeither() throws IOException {
        // C's obj_mods table (list-stats.h then list-object-modifiers.h) and list_element_names
        // contain neither NONE nor MAX, so grab_int_value and grab_index_and_int fail with
        // PARSE_ERROR_INVALID_VALUE.
        PlayerShape s = load("none-max.txt", withHeader(1, shape("n",
                "values:NONE[1] | MAX[2] | RES_NONE[1] | RES_MAX[2]\n"))).items().get(0);

        assertEquals(1, s.getObjectValueModifiers().get(ObjectModifier.OM_NONE));
        assertEquals(2, s.getObjectValueModifiers().get(ObjectModifier.OM_MAX));
        assertTrue(s.getElementValueModifiers().containsKey(ElementEnum.ELEM_NONE));
        assertTrue(s.getElementValueModifiers().containsKey(ElementEnum.ELEM_MAX));
    }

    @Test
    void aLowerCaseFlagOrValueNameFailsHereAsItDoesInC() throws IOException {
        // lookup_flag and grab_int_value compare with streq, so C rejects these too; here the lexer does.
        assertTrue(load("lc-flag.txt", withHeader(1, shape("a", "obj-flags:regen\n"))).hasErrors());
        assertTrue(load("lc-val.txt", withHeader(1, shape("a", "values:str[1]\n"))).hasErrors());
    }

    @Test
    void aDirectiveBeforeNameOrAnUnknownDirectiveIsAHardError() throws IOException {
        // C: PARSE_ERROR_MISSING_RECORD_HEADER and PARSE_ERROR_UNDEFINED_DIRECTIVE.
        assertTrue(load("before-name.txt", withHeader(1, "combat:1:1:1\nname:a\n")).hasErrors());
        assertTrue(load("unknown-dir.txt", withHeader(1, shape("a", "bogus:1\n"))).hasErrors());
    }

    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsLeadingWhitespace() throws IOException {
        ParseResult<PlayerShape> result = load("indent.txt", withHeader(1, shape("a", "  combat:1:1:1\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void theShippedFileDiffersFromUpstreamInThreeLinesThatTheseTestsDependOn() throws IOException {
        // lib/gamedata/shape.txt adds the record-count header, drops the blank after "values:" on bear,
        // and moves the werewolf effect-msg below its first effect; the upstream file loads in none of those forms.
        String text = Files.readString(Path.of(REAL_FILE));

        assertTrue(text.contains("record-count:9"));
        assertFalse(text.contains("values: "));
        assertTrue(text.indexOf("effect-msg:taking werewolf form") > text.indexOf("effect:PROJECT_LOS:TURN_ALL"));
    }
}
