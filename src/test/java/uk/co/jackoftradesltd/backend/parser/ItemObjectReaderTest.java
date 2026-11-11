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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.backend.parser.itemobject.ItemObjectAssembler;
import uk.co.jackoftradesltd.backend.parser.itemobject.ItemObjectParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
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
 * End-to-end throughput tests for {@link ItemObjectReader}: file text -> {@code ItemObjectLexer}/
 * {@code ItemObjectGrammar} -> {@code ItemObjectParseRecord} -> {@code ItemObjectAssembler} ->
 * {@link ObjectKind}.
 *
 * <p>The object-kind assembler resolves the {@code type:} tval against {@code object_base.txt},
 * brands/slays/curses against their registries, and {@code effect:SHAPECHANGE:} subtypes against
 * loaded {@link uk.co.jackoftradesltd.middle.player.PlayerShape}s. Rather than run the whole heavy
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
        Field f = RegistrySeeding.resolve(field);
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

        assertEquals(375, result.items().size());
    }

    // ---- Fatal errors: the first bad record ends the load -----------------

    /**
     * Loads {@code content} and asserts that the load ends in a {@link DataFileParseException}
     * whose message contains every one of {@code fragments}. C stops the load on any
     * {@code PARSE_ERROR_*} result, and so does the assembler: nothing is skipped.
     *
     * @param fileName  the name of the temporary file the content is written to
     * @param content   the whole text of the data file
     * @param fragments the text the exception message must name
     * @return the exception, for any further check
     */
    private DataFileParseException assertLoadIsFatal(String fileName, String content, String... fragments) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> load(fileName, content));
        for (String fragment : fragments) {
            assertTrue(thrown.getMessage().contains(fragment),
                    () -> "message should name '" + fragment + "' but was: " + thrown.getMessage());
        }
        return thrown;
    }

    /**
     * {@code parse_object_type} returns {@code PARSE_ERROR_UNRECOGNISED_TVAL} for a tval
     * {@code tval_find_idx} does not know, and C stops the load on a parse error. Here the assembler
     * throws {@link DataFileParseException} naming the tval, the line and the file.
     */
    @Test
    void unknownTypeStopsTheLoadAsCDoes() {
        assertLoadIsFatal("bad-type.txt",
                obj("weird", "type:no such tval\ngraphics:|:w\n"),
                "no such tval", "bad-type.txt", "line: 1");
    }

    @Test
    void unknownFlagStopsTheLoad() {
        assertLoadIsFatal("bad-flag.txt",
                obj("weird", HEAD + "flags:NOTAFLAG\n"),
                "unknown flag: NOTAFLAG", "bad-flag.txt", "line: 1");
    }

    @Test
    void unknownModifierStopsTheLoad() {
        assertLoadIsFatal("bad-mod.txt",
                obj("weird", HEAD + "values:NOTAMOD[1]\n"),
                "object modifier", "NOTAMOD", "bad-mod.txt");
    }

    @Test
    void unknownResistanceElementStopsTheLoad() {
        assertLoadIsFatal("bad-res.txt",
                obj("weird", HEAD + "values:RES_NOTREAL[1]\n"),
                "resistance element", "RES_NOTREAL", "bad-res.txt");
    }

    /**
     * The message names the brand code that failed, not the whole list of brands on the record.
     */
    @Test
    void unknownBrandStopsTheLoadAndTheMessageNamesThatBrand() {
        DataFileParseException thrown = assertLoadIsFatal("bad-brand.txt",
                obj("weird", HEAD + "brand:FIRE_3\nbrand:NOTABRAND\n"),
                "brand value: NOTABRAND");

        assertFalse(thrown.getMessage().contains("["), "no list brackets: " + thrown.getMessage());
    }

    /**
     * The message names the slay code that failed, not the whole list of slays on the record.
     */
    @Test
    void unknownSlayStopsTheLoadAndTheMessageNamesThatSlay() {
        DataFileParseException thrown = assertLoadIsFatal("bad-slay.txt",
                obj("weird", HEAD + "slay:EVIL_2\nslay:NOTASLAY\n"),
                "slay value: NOTASLAY");

        assertFalse(thrown.getMessage().contains("["), "no list brackets: " + thrown.getMessage());
    }

    @Test
    void unknownCurseStopsTheLoad() {
        assertLoadIsFatal("bad-curse.txt",
                obj("weird", HEAD + "curse:no such curse:10\n"),
                "no such curse", "bad-curse.txt");
    }

    /**
     * The curse leg's happy path. C keeps {@code int *curses} on a kind — indexed by curse, value =
     * power, 0 meaning absent ({@code obj-init.c}, {@code parse_object_curse}); the port keys by {@link Curse} and carries the
     * power in the entry's {@link CurseData}, whose timeout stays 0 because timeout is a property of
     * a live {@code struct object}, not of a data-file record.
     *
     * <p>Until this existed the only curse coverage was the unknown-curse error path, so the map's
     * shape was untested — {@link #powerZeroCurseIsNotRecorded()} covers the other half.
     */
    @Test
    void curseResolvesToAnEntryKeyedByCurseCarryingItsPower() throws Exception {
        ParseResult<ObjectKind> result = load("cursed.txt", obj("cursed blade", HEAD + "curse:teleportation:100\n"));

        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");

        assertEquals(1, curses.size());
        Curse curse = curses.keySet().iterator().next();
        assertEquals("teleportation", curse.getName());
        assertEquals(100, curses.get(curse).getPower());
        assertEquals(0, curses.get(curse).getTimeout(), "timeout is runtime-only");
    }

    /**
     * C: {@code /* Only add if it has power. *}{@code /  if (power > 0)} ({@code obj-init.c},
     * {@code parse_object_curse}). A
     * zero-power curse line is not an error — the curse is simply not recorded on the kind.
     */
    @Test
    void powerZeroCurseIsNotRecorded() throws Exception {
        ParseResult<ObjectKind> result = load("zero-curse.txt", obj("uncursed blade", HEAD + "curse:teleportation:0\n"));

        assertEquals(1, result.items().size(), "a zero-power curse is not an error; the kind loads");
        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");
        assertTrue(curses.isEmpty(), () -> "power 0 must not be recorded, got: " + curses);
    }

    // ---- time: belongs to the kind, not to an effect -----------------------

    /**
     * Asserts that two dice agree on every component.
     *
     * @param expected the dice the data file's {@code time:} line spells
     * @param actual   the dice the kind holds
     */
    private static void assertSameDice(Random expected, Random actual) {
        assertNotNull(actual, "a kind's time is never null");
        assertEquals(expected.getBase(), actual.getBase(), "time base");
        assertEquals(expected.getDice(), actual.getDice(), "time dice");
        assertEquals(expected.getSides(), actual.getSides(), "time sides");
        assertEquals(expected.getMBonus(), actual.getMBonus(), "time m-bonus");
    }

    /**
     * C's {@code parse_object_time} writes {@code kind->time}; {@code struct effect} has no time.
     * A {@code time:} line after the kind's effect must therefore land on the {@link ObjectKind}.
     */
    @Test
    void timeAfterTheEffectReachesTheKind() throws IOException {
        ParseResult<ObjectKind> result = load("timed.txt", obj("rod of waking", HEAD + "effect:WAKE\ntime:100+1d50\n"));

        assertSameDice(Random.parseStr("100+1d50"), result.items().get(0).getTime());
    }

    /** C: {@code parse_object_effect_yx} and {@code parse_object_dice} both act on the last effect, in either order. */
    @Test
    void effectYxThenDiceOnTheSameEffectBothLoadAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("yx-dice.txt", obj("rod of reach", HEAD + "effect:WAKE\neffect-yx:2:3\ndice:5\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("2,3,5", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    @Test
    void diceThenEffectYxOnTheSameEffectBothLoadAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("dice-yx.txt", obj("rod of reach", HEAD + "effect:WAKE\ndice:5\neffect-yx:2:3\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("2,3,5", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    // ---- Stray dice:, effect-yx: and expr: lines (Issue_Approach_Order Stage 6 item 2.i, ii) ----

    /**
     * C ({@code obj-init.c}, {@code parse_object_dice}) returns no error and does nothing when the
     * kind has no effect yet, so a {@code dice:} line ahead of the first {@code effect:} is dropped
     * and the effect that follows keeps its defaults.
     *
     * <p>Function diceBeforeTheFirstEffectIsDroppedAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void diceBeforeTheFirstEffectIsDroppedAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("dice-first.txt", obj("rod of nothing", HEAD + "dice:5\neffect:WAKE\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("0,0,", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * C ({@code parse_object_effect_yx}) drops an {@code effect-yx:} line ahead of the first
     * {@code effect:} in the same way.
     *
     * <p>Function effectYxBeforeTheFirstEffectIsDroppedAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void effectYxBeforeTheFirstEffectIsDroppedAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("yx-first.txt", obj("rod of nothing", HEAD + "effect-yx:2:3\neffect:WAKE\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("0,0,", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * C ({@code parse_object_expr}) drops an {@code expr:} line when the kind has no effect, so one
     * ahead of the first {@code effect:} binds to nothing.
     *
     * <p>Function exprBeforeTheFirstEffectIsDroppedAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void exprBeforeTheFirstEffectIsDroppedAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("expr-first.txt",
                obj("rod of nothing", HEAD + "expr:B:PLAYER_LEVEL:* 3\neffect:WAKE\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("0,0,", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
        assertEquals("", EffectProbe.expressions(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * A record whose only effect lines are strays has no effects at all, and the load goes on to the
     * next directive.
     *
     * <p>Function strayLinesWithNoEffectLeaveTheKindWithNoEffectsAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void strayLinesWithNoEffectLeaveTheKindWithNoEffectsAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("strays-only.txt", obj("rod of nothing",
                HEAD + "dice:$B+1d25\nexpr:B:PLAYER_LEVEL:* 3\neffect-yx:2:3\nlevel:1\n"));

        assertEquals(1, result.items().size());
        assertEquals(0, result.items().get(0).getEffect().size());
    }

    /**
     * Stray lines ahead of the first effect do not leak onto it: a {@code dice:} written after the
     * effect is the only one that lands.
     *
     * <p>Function strayLinesBeforeAnEffectDoNotReachItAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void strayLinesBeforeAnEffectDoNotReachItAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("strays-then-effect.txt", obj("rod of nothing",
                HEAD + "dice:5\neffect-yx:1:1\nexpr:B:PLAYER_LEVEL:* 3\neffect:WAKE\ndice:6\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("0,0,6", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
        assertEquals("", EffectProbe.expressions(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * C acts on the kind's last effect wherever the line falls, so a {@code dice:} separated from
     * its {@code effect:} by another directive still reaches it.
     *
     * <p>Function diceSeparatedFromItsEffectStillReachesItAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void diceSeparatedFromItsEffectStillReachesItAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("dice-apart.txt", obj("rod of reach", HEAD + "effect:WAKE\nlevel:1\ndice:5\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("0,0,5", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * The same holds for {@code effect-yx:}: one separated from its {@code effect:} by another
     * directive still reaches it.
     *
     * <p>Function effectYxSeparatedFromItsEffectStillReachesItAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void effectYxSeparatedFromItsEffectStillReachesItAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("yx-apart.txt", obj("rod of reach", HEAD + "effect:WAKE\nlevel:1\neffect-yx:2:3\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("2,3,", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * With two effects, a late {@code dice:} lands on the second one and leaves the first alone.
     *
     * <p>Function aLateDiceReachesTheLastOfSeveralEffectsAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void aLateDiceReachesTheLastOfSeveralEffectsAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("dice-last.txt",
                obj("rod of two things", HEAD + "effect:WAKE\neffect:ACQUIRE\nlevel:1\ndice:9\n"));

        List<?> effects = result.items().get(0).getEffect();
        assertEquals(2, effects.size());
        assertEquals("0,0,", EffectProbe.yxBase(effects.get(0)));
        assertEquals("0,0,9", EffectProbe.yxBase(effects.get(1)));
    }

    /**
     * C ({@code parse_object_dice}) frees the old dice and installs the new one, so a second
     * {@code dice:} on one effect replaces the first. A second {@code effect-yx:} overwrites in the
     * same way.
     *
     * <p>Function aSecondDiceOrEffectYxReplacesTheFirstAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void aSecondDiceOrEffectYxReplacesTheFirstAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("replace.txt", obj("rod of reach",
                HEAD + "effect:WAKE\ndice:5\ndice:7\neffect-yx:2:3\neffect-yx:4:5\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("4,5,7", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * A replaced complex {@code dice:} takes its own {@code expr:} lines with it: only the second
     * dice's expression is left on the effect.
     *
     * <p>Function aSecondComplexDiceTakesItsOwnExprLinesAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void aSecondComplexDiceTakesItsOwnExprLinesAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("replace-complex.txt", obj("rod of reach", HEAD + "effect:WAKE\n"
                + "dice:$B+1d25\nexpr:B:PLAYER_LEVEL:* 3\ndice:$C+1d4\nexpr:C:PLAYER_LEVEL:+ 1\n"));

        Object effect = result.items().get(0).getEffect().getFirst();
        assertEquals("0,0,$C+1d4", EffectProbe.yxBase(effect));
        assertEquals("C:EFB_PLAYER_LEVEL:+ 1", EffectProbe.expressions(effect));
    }

    /**
     * C ({@code dice_parse_string}) accepts a {@code $} variable and leaves it waiting for a value,
     * so a complex {@code dice:} followed by another directive instead of an {@code expr:} loads:
     * the raw string lands on the effect and no expression is bound. The line after it still
     * belongs to the object.
     *
     * <p>Function aComplexDiceWithNoExprLoadsAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void aComplexDiceWithNoExprLoadsAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("complex-no-expr.txt",
                obj("rod of reach", HEAD + "effect:WAKE\ndice:$B+1d25\nlevel:1\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        Object effect = result.items().get(0).getEffect().getFirst();
        assertEquals("0,0,$B+1d25", EffectProbe.yxBase(effect));
        assertEquals("", EffectProbe.expressions(effect));
    }

    /**
     * C ({@code parse_object_expr}) does nothing when the last effect has no dice, so an
     * {@code expr:} straight after a bare {@code effect:} is ignored.
     *
     * <p>Function anExprWithNoDiceIsIgnoredAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void anExprWithNoDiceIsIgnoredAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("expr-no-dice.txt",
                obj("rod of reach", HEAD + "effect:WAKE\nexpr:B:PLAYER_LEVEL:* 3\n"));

        assertEquals(1, result.items().get(0).getEffect().size());
        assertEquals("", EffectProbe.expressions(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * An {@code expr:} line that falls after another directive still binds to the dice already on
     * the last effect, and joins the expression that came with it.
     *
     * <p>Function aLateExprJoinsTheDiceItFollowsAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void aLateExprJoinsTheDiceItFollowsAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("expr-late.txt", obj("rod of reach", HEAD + "effect:WAKE\n"
                + "dice:$B+1d25\nexpr:B:PLAYER_LEVEL:* 3\nlevel:1\nexpr:B:PLAYER_LEVEL:+ 1\n"));

        assertEquals("B:EFB_PLAYER_LEVEL:* 3|B:EFB_PLAYER_LEVEL:+ 1",
                EffectProbe.expressions(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * A {@code dice:} that follows an {@code effect-msg:} is a line like any other and lands on the
     * last effect.
     *
     * <p>Function aDiceAfterAnEffectMsgReachesTheEffectAsInC coded on 261111, commented in full on 261111.
     */
    @Test
    void aDiceAfterAnEffectMsgReachesTheEffectAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("dice-after-msg.txt",
                obj("rod of reach", HEAD + "effect:WAKE\neffect-msg:hello\ndice:5\n"));

        assertEquals("0,0,5", EffectProbe.yxBase(result.items().get(0).getEffect().getFirst()));
    }

    /**
     * With several effects the old design hung the time on the last one only. The kind holds it
     * once, whatever the number of effects.
     */
    @Test
    void timeWithSeveralEffectsReachesTheKindOnce() throws IOException {
        ParseResult<ObjectKind> result = load("timed-two.txt", obj("rod of two things", HEAD + "effect:WAKE\neffect:ACQUIRE\ntime:1d500\n"));

        ObjectKind kind = result.items().get(0);
        assertEquals(2, kind.getEffect().size());
        assertSameDice(Random.parseStr("1d500"), kind.getTime());
    }

    /**
     * {@code time:} is a record-level line, so its position among the record's other lines does not
     * matter; before the effect it still reaches the kind.
     */
    @Test
    void timeBeforeTheEffectReachesTheKind() throws IOException {
        ParseResult<ObjectKind> result = load("timed-first.txt", obj("rod of early time", HEAD + "time:d100\neffect:WAKE\n"));

        assertSameDice(Random.parseStr("d100"), result.items().get(0).getTime());
    }

    /**
     * A kind with no {@code time:} line holds a zero {@link Random}, as C's zeroed
     * {@code random_value}, and never {@code null}.
     */
    @Test
    void absentTimeIsZeroNotNull() throws IOException {
        ParseResult<ObjectKind> result = load("untimed.txt", obj("plain blade", HEAD + "effect:WAKE\n"));

        assertSameDice(Random.Zero(), result.items().get(0).getTime());
    }

    /**
     * Every kind in {@code object.txt} holds exactly the dice its own {@code time:} line spells, and
     * a zero {@link Random} where it has none. The expected values come from the data file's text,
     * read record by record, not from the reader under test.
     */
    @Test
    void everyKindInTheRealFileHoldsItsOwnTime() throws IOException {
        List<String> expected = new ArrayList<>();
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.startsWith("name:")) expected.add("");
            else if (line.startsWith("time:")) expected.set(expected.size() - 1, line.substring(5).trim());
        }

        ParseResult<ObjectKind> result = new ItemObjectReader().parseWithResults(REAL_FILE);

        assertEquals(expected.size(), result.items().size(), "one kind per name: line");
        int timed = 0;
        for (int i = 0; i < expected.size(); i++) {
            ObjectKind kind = result.items().get(i);
            Random want = expected.get(i).isEmpty() ? Random.Zero() : Random.parseStr(expected.get(i));
            if (!expected.get(i).isEmpty()) timed++;
            assertSameDice(want, kind.getTime());
        }
        assertEquals(43, timed, "object.txt carries 43 time: lines");
    }

    /**
     * The first bad record ends the load, as a {@code PARSE_ERROR_*} does in C: the record after it
     * is never read, so its own error is never reported, and the valid record before it is not
     * returned either.
     */
    @Test
    void theFirstBadRecordEndsTheLoadAndALaterOneIsNeverReached() {
        String body = obj("good", HEAD)
                + obj("badflag", HEAD + "flags:NOTAFLAG\n")
                + obj("badmod", HEAD + "values:NOTAMOD[1]\n");

        DataFileParseException thrown = assertLoadIsFatal("two-errors.txt", body,
                "unknown flag: NOTAFLAG", "line: 4");

        assertFalse(thrown.getMessage().contains("NOTAMOD"), thrown.getMessage());
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("no-name.txt", HEAD));

    }

    // ---- kind flags and IGNORE_ element flags (obj-init.c: parse_object_flags) --------------

    /**
     * C's {@code grab_flag} against {@code kind_flags[]}: a {@code KF_} token on the kind's
     * {@code flags:} line lands in the kind's kind flags, and the base's kind flags are unioned in
     * ({@code finish_parse_object}). {@code sword} in {@code object_base.txt} carries
     * {@code SHOW_DICE}.
     */
    @Test
    void kindFlagTokenReachesKindFlagsAndBaseFlagsAreUnioned() throws IOException {
        ParseResult<ObjectKind> result = load("kf.txt", obj("kf", HEAD + "flags:EASY_KNOW\n"));

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_SHOW_DICE));
        assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD));
    }

    /**
     * C's {@code grab_element_flag}: {@code IGNORE_FIRE} sets {@code EL_INFO_IGNORE} on the
     * <em>fire</em> slot of the kind's own {@code el_info}, and on no other element.
     */
    @Test
    void ignoreTokenSetsIgnoreOnThatElementOfTheKindOnly() throws IOException {
        ParseResult<ObjectKind> result = load("ign.txt", obj("ign", HEAD + "flags:IGNORE_FIRE\n"));

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_COLD).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    /**
     * Several {@code IGNORE_} tokens, in one {@code flags:} line with a kind flag between them,
     * all take effect; nothing is lost to the kind flag, and none becomes a kind flag.
     */
    @Test
    void severalIgnoreTokensAndAKindFlagShareOneLine() throws IOException {
        ParseResult<ObjectKind> result = load("ign2.txt", obj("ign2", HEAD + "flags:IGNORE_ACID | EASY_KNOW | IGNORE_FIRE\n"));

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_ELEC).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
    }

    /**
     * In C the ignore goes on the kind ({@code k->el_info}), never on the base ({@code kb_info}),
     * so one kind's {@code IGNORE_FIRE} must not make its siblings fire-proof.
     */
    @Test
    void ignoreOnOneKindDoesNotReachItsBaseOrItsSiblings() throws IOException {
        ParseResult<ObjectKind> result = load("sib.txt", obj("fireproof", HEAD + "flags:IGNORE_FIRE\n")
                        + obj("plain", HEAD));

        ObjectKind fireproof = result.items().get(0);
        ObjectKind plain = result.items().get(1);
        assertFalse(plain.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(fireproof.getBase().getElementMap().containsKey(ElementEnum.ELEM_FIRE)
                        && fireproof.getBase().getElementMap().get(ElementEnum.ELEM_FIRE)
                        .has(ElementInfoEnum.EL_INFO_IGNORE),
                "the shared base must not gain the kind's ignore");
    }

    /**
     * C requires the {@code IGNORE_} (or {@code HATES_}) prefix: a bare element name is not a flag
     * and is {@code PARSE_ERROR_INVALID_FLAG}. The load ends with an "unknown flag" error.
     */
    @Test
    void bareElementNameIsNotAFlag() {
        assertLoadIsFatal("bare.txt", obj("bare", HEAD + "flags:FIRE\n"),
                "unknown flag: FIRE");
    }

    /**
     * A token shorter than {@code IGNORE_} is an unknown flag like any other. It must not escape
     * as a runtime exception other than the load's own.
     */
    @Test
    void shortUnknownTokenIsAnUnknownFlag() {
        assertLoadIsFatal("short.txt", obj("short", HEAD + "flags:FOO\n"),
                "unknown flag: FOO");
    }

    /**
     * A seven-or-more-character token that merely ends in an element name is not an ignore flag.
     * C compares the whole prefix, so a token such as {@code XXXXXXXFIRE} fails.
     */
    @Test
    void tokenWithWrongPrefixButAnElementSuffixIsNotAnIgnoreFlag() {
        assertLoadIsFatal("prefix.txt", obj("prefix", HEAD + "flags:XXXXXXXFIRE\n"),
                "unknown flag: XXXXXXXFIRE");
    }

    /**
     * An {@code IGNORE_} or {@code HATES_} prefix followed by something that is not an element is an
     * unknown flag; {@code grab_element_flag} finds no element and returns false.
     */
    @Test
    void anElementPrefixWithNoElementAfterItIsAnUnknownFlag() {
        assertLoadIsFatal("ign-none.txt", obj("none", HEAD + "flags:IGNORE_NOTANELEMENT\n"),
                "unknown flag: IGNORE_NOTANELEMENT");
        assertLoadIsFatal("hates-none.txt", obj("none", HEAD + "flags:HATES_NOTANELEMENT\n"),
                "unknown flag: HATES_NOTANELEMENT");
    }

    /**
     * The real file, against {@code object.txt} as shipped: the Lantern carries only
     * {@code IGNORE_FIRE}; the Mithril Arrow carries {@code IGNORE_ACID | IGNORE_FIRE}.
     */
    @Test
    void realFileLanternAndMithrilArrowIgnoreExactlyTheirElements() throws IOException {
        ParseResult<ObjectKind> result = new ItemObjectReader().parseWithResults(REAL_FILE);

        ObjectKind lantern = result.items().stream()
                .filter(k -> "Lantern".equals(k.getsValueName())).findFirst().orElseThrow();
        assertTrue(lantern.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(lantern.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(lantern.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));

        ObjectKind arrow = result.items().stream()
                .filter(k -> "Mithril Arrow".equals(k.getsValueName())).findFirst().orElseThrow();
        assertTrue(arrow.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(arrow.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(arrow.getElInfo(ElementEnum.ELEM_ELEC).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    // ---- the real file against a line-by-line reading of object.txt -------

    /**
     * Reads {@link #REAL_FILE} straight off its lines, with no lexer or grammar involved: one map per
     * {@code name:} block, each directive name to the values given for it, in order. It is the
     * independent witness for what {@code finish_parse_object} is handed in C, so the assertions
     * below compare the port against the file and not against itself.
     */
    private static List<Map<String, List<String>>> scanRealFile() throws IOException {
        List<Map<String, List<String>>> records = new ArrayList<>();
        Map<String, List<String>> current = null;
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.isBlank() || line.startsWith("#") || line.startsWith("record-count:")) continue;
            int colon = line.indexOf(':');
            String key = line.substring(0, colon);
            if (key.equals("name")) {
                current = new LinkedHashMap<>();
                records.add(current);
            }
            current.computeIfAbsent(key, k -> new ArrayList<>()).add(line.substring(colon + 1));
        }
        return records;
    }

    /**
     * The value of the last line a record gives for {@code key}, which is the one C is left holding
     * when the handler assigns.
     */
    private static String last(Map<String, List<String>> record, String key) {
        List<String> values = record.get(key);
        return values.get(values.size() - 1);
    }

    /**
     * A text directive's value in C's terms: every line is appended with {@code string_append}, so
     * the lines join, and a directive the record never gave leaves the zeroed record's {@code NULL}.
     */
    private static String joinedOrNull(Map<String, List<String>> record, String key) {
        List<String> values = record.get(key);
        return values == null ? null : String.join("", values);
    }

    /**
     * A scalar directive's value in C's terms: a repeat overwrites, so the last line wins, and a
     * directive the record never gave leaves the zeroed record's 0.
     */
    private static int lastInt(Map<String, List<String>> record, String key) {
        List<String> values = record.get(key);
        return values == null ? 0 : Integer.parseInt(values.get(values.size() - 1).trim());
    }

    /**
     * A dice component, as {@code "base+dice d sides m-bonus"}, so one string assertion covers all
     * four fields of a {@code struct random}.
     */
    private static String dice(Random random) {
        return random.getBase() + "+" + random.getDice() + "d" + random.getSides() + "m" + random.getMBonus();
    }

    private static ObjectKind kindNamed(List<ObjectKind> kinds, String name, TValue tval) {
        return kinds.stream()
                .filter(k -> name.equals(k.getName()) && tval == k.gettValue())
                .findFirst().orElseThrow(() -> new AssertionError("no " + tval + " kind named " + name));
    }

    /**
     * {@code finish_parse_object} fills {@code k_info} from the head of a chain built last-first,
     * counting {@code kidx} down from the top, so the first record in the file is {@code kidx} 0.
     * The port's list is in file order, kind for kind.
     */
    @Test
    void realFileKindsComeBackInFileOrder() throws IOException {
        List<Map<String, List<String>>> expected = scanRealFile();
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        assertEquals(375, expected.size(), "the file as shipped holds 375 name: lines");
        assertEquals(expected.size(), kinds.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).get("name").get(0), kinds.get(i).getName(), "kind " + i);
        }
    }

    /**
     * The first four records of {@code object.txt} are the sentinel kinds, all {@code type:none}.
     * The reader loads them like any other record.
     */
    @Test
    void realFileOpensWithTheFourSentinelKinds() throws IOException {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        assertEquals(List.of("<pile>", "<unknown item>", "<unknown treasure>", "<curse object>"),
                kinds.subList(0, 4).stream().map(ObjectKind::getName).collect(Collectors.toList()));
        for (ObjectKind sentinel : kinds.subList(0, 4)) {
            assertEquals(TValue.TV_NONE, sentinel.gettValue(), sentinel.getName());
        }
        assertEquals(TValue.TV_LIGHT, kinds.get(4).gettValue(), "the first ordinary kind is the Wooden Torch");
    }

    /**
     * Counting the {@code type:} lines of the file gives how many kinds each tval must end up
     * with. {@code digger} is {@code TV_DIGGING} and {@code dragon armor} is {@code TV_DRAG_ARMOR}.
     */
    @Test
    void realFileHoldsTheNumberOfKindsPerTvalThatItsTypeLinesGive() throws IOException {
        Map<String, Integer> expected = new TreeMap<>(Map.ofEntries(
                Map.entry("TV_POTION", 41), Map.entry("TV_SCROLL", 39), Map.entry("TV_RING", 30),
                Map.entry("TV_WAND", 30), Map.entry("TV_ROD", 25), Map.entry("TV_STAFF", 24),
                Map.entry("TV_AMULET", 17), Map.entry("TV_POLEARM", 16), Map.entry("TV_FOOD", 14),
                Map.entry("TV_SWORD", 14), Map.entry("TV_HAFTED", 14), Map.entry("TV_MUSHROOM", 12),
                Map.entry("TV_HARD_ARMOR", 12), Map.entry("TV_DRAG_ARMOR", 12), Map.entry("TV_GOLD", 11),
                Map.entry("TV_BOOTS", 6), Map.entry("TV_SHIELD", 6), Map.entry("TV_CHEST", 6),
                Map.entry("TV_BOW", 5), Map.entry("TV_SOFT_ARMOR", 5), Map.entry("TV_GLOVES", 5),
                Map.entry("TV_NONE", 4), Map.entry("TV_HELM", 4), Map.entry("TV_CROWN", 4),
                Map.entry("TV_CLOAK", 4), Map.entry("TV_ARROW", 3), Map.entry("TV_BOLT", 3),
                Map.entry("TV_SHOT", 3), Map.entry("TV_DIGGING", 3), Map.entry("TV_LIGHT", 2),
                Map.entry("TV_FLASK", 1)));

        Map<String, Integer> actual = new TreeMap<>();
        for (ObjectKind kind : new ItemObjectReader().parse(REAL_FILE)) {
            actual.merge(kind.gettValue().name(), 1, Integer::sum);
        }

        assertEquals(expected, actual);
    }

    /**
     * Every scalar the file gives, for every one of the 375 kinds, against a plain reading of the
     * lines: the numbers by C's last-one-wins rule, {@code attack:}'s dice and sides, the alloc
     * triple, and the {@code msg:}, {@code vis-msg:} and {@code desc:} texts joined with no
     * separator as {@code string_append} joins them.
     */
    @Test
    void realFileScalarsAndTextsMatchALineByLineReadingOfTheFile() throws Exception {
        List<Map<String, List<String>>> expected = scanRealFile();
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        for (int i = 0; i < expected.size(); i++) {
            Map<String, List<String>> record = expected.get(i);
            ObjectKind kind = kinds.get(i);
            String at = kind.getName() + " (kind " + i + ")";

            assertEquals(lastInt(record, "level"), (int) kindField(kind, "level"), "level of " + at);
            assertEquals(lastInt(record, "weight"), kind.getWeight(), "weight of " + at);
            assertEquals(lastInt(record, "cost"), kind.getCost(), "cost of " + at);
            assertEquals(lastInt(record, "power"), kind.getPower(), "power of " + at);

            int common = 0, lo = 0, hi = 0;
            if (record.containsKey("alloc")) {
                String[] alloc = last(record, "alloc").trim().split(":");
                String[] range = alloc[1].trim().split(" to ");
                common = Integer.parseInt(alloc[0]);
                lo = Integer.parseInt(range[0].trim());
                hi = Integer.parseInt(range[1].trim());
            }
            assertEquals(common, (int) kindField(kind, "alloc_prob"), "alloc commonness of " + at);
            assertEquals(lo, (int) kindField(kind, "alloc_min"), "alloc min of " + at);
            assertEquals(hi, (int) kindField(kind, "alloc_max"), "alloc max of " + at);

            int dd = 0, ds = 0, ac = 0;
            if (record.containsKey("attack")) {
                String[] hd = last(record, "attack").split(":")[0].split("d");
                dd = Integer.parseInt(hd[0]);
                ds = Integer.parseInt(hd[1]);
            }
            if (record.containsKey("armor")) {
                ac = Integer.parseInt(last(record, "armor").split(":")[0]);
            }
            assertEquals(dd, kind.getDamageDice(), "dice of " + at);
            assertEquals(ds, kind.getDamageSides(), "sides of " + at);
            assertEquals(ac, kind.getAc(), "base ac of " + at);

            int pile = record.containsKey("pile") ? Integer.parseInt(last(record, "pile").split(":")[0]) : 0;
            assertEquals(pile, (int) kindField(kind, "genMultProb"), "pile chance of " + at);

            assertEquals(joinedOrNull(record, "desc"),
                    kindField(kind, "text"), "desc of " + at);
            assertEquals(joinedOrNull(record, "msg"),
                    kindField(kind, "effectMessage"), "msg of " + at);
            assertEquals(joinedOrNull(record, "vis-msg"),
                    kindField(kind, "visMessage"), "vis-msg of " + at);
            assertEquals(record.getOrDefault("effect", List.of()).size(), kind.getEffect().size(),
                    "effect count of " + at);
        }
    }

    /**
     * Svals and kind indexes are given out when {@code ObjectDataLoader} registers the kinds
     * ({@code ObjectRegistry.addObjectKind}), the port's half of what {@code parse_object_type} and
     * {@code finish_parse_object} do. A kind that has only been through the reader is still 0 and 0.
     */
    @Test
    void realFileKindsLeaveTheReaderWithSvalAndIndexUnset() throws IOException {
        for (ObjectKind kind : new ItemObjectReader().parse(REAL_FILE)) {
            assertEquals(0, kind.getsVal(), kind.getName());
            assertEquals(0, kind.getKindIndex(), kind.getName());
        }
    }

    // ---- the real file: spot records worked out from the file --------------

    /**
     * {@code & Wooden Torch~}: {@code BURNS_OUT | LIGHT_2} are object flags, {@code EASY_KNOW} is a
     * kind flag, {@code attack:1d1:0:0} gives 1d1 with to-hit and to-damage zero, and
     * {@code graphics:~:u} is umber.
     */
    @Test
    void realFileWoodenTorchCarriesEveryFieldTheFileGives() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind torch = kindNamed(kinds, "& Wooden Torch~", TValue.TV_LIGHT);

        assertEquals(1, (int) kindField(torch, "level"));
        assertEquals(22, torch.getWeight());
        assertEquals(2, torch.getCost());
        assertEquals(70, (int) kindField(torch, "alloc_prob"));
        assertEquals(1, (int) kindField(torch, "alloc_min"));
        assertEquals(100, (int) kindField(torch, "alloc_max"));
        assertEquals(1, torch.getDamageDice());
        assertEquals(1, torch.getDamageSides());
        assertEquals("0+0d0m0", dice(torch.getToH()));
        assertEquals("0+0d0m0", dice(torch.getToD()));
        assertEquals('~', torch.getCharacter().getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_UMBER, torch.getCharacter().getAttributeColour());
        assertTrue(torch.getFlags().has(ObjectFlag.OF_BURNS_OUT));
        assertTrue(torch.getFlags().has(ObjectFlag.OF_LIGHT_2));
        assertFalse(torch.getFlags().has(ObjectFlag.OF_TAKES_FUEL));
        assertTrue(torch.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
    }

    /**
     * {@code & Flask~ of oil}: {@code pval:7500} is a bare number, and its four {@code desc:} lines
     * run together exactly as written, the double space after "light." included.
     */
    @Test
    void realFileFlaskOfOilHasItsPvalAndItsFourDescLinesJoinedWithNoSeparator() throws Exception {
        ObjectKind flask = kindNamed(new ItemObjectReader().parse(REAL_FILE), "& Flask~ of oil", TValue.TV_FLASK);

        assertEquals("7500+0d0m0", dice(flask.getPVal()));
        assertEquals("It can be used to fuel a lantern with 7500 turns of light, up to a maximum of 15000 "
                + "turns of light.  When thrown, it is considered lit, so it does more than nominal damage "
                + "(which occurs even against creatures resistant to fire).", kindField(flask, "text"));
    }

    /**
     * {@code Flames}: {@code armor:0:5+d5M10} (a {@code d} with no number before it is one die),
     * {@code IGNORE_FIRE} and {@code RES_FIRE[1]} landing on the same element, {@code time:50+d50}
     * on the kind, and two effects.
     */
    @Test
    void realFileRingOfFlamesCombinesIgnoreResistanceTimeAndToArmour() throws Exception {
        ObjectKind flames = kindNamed(new ItemObjectReader().parse(REAL_FILE), "Flames", TValue.TV_RING);

        assertEquals("5+1d5m10", dice(flames.getToA()));
        assertEquals(1, flames.getElInfo(ElementEnum.ELEM_FIRE).getResLevel());
        assertTrue(flames.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertEquals(0, flames.getElInfo(ElementEnum.ELEM_COLD).getResLevel());
        assertEquals("50+1d50m0", dice(flames.getTime()));
        assertEquals(11, flames.getPower());
        assertEquals(2, flames.getEffect().size());
    }

    /**
     * {@code Trickery} gives its {@code values:} over two lines: the first holds object modifiers,
     * the second {@code RES_} elements. Both lines count, and a modifier's dice keep their base,
     * dice, sides and bonus apart ({@code 1+d3M3} is base 1, 1d3, bonus 3).
     */
    @Test
    void realFileAmuletOfTrickerySplitsItsValuesBetweenModifiersAndResistances() throws Exception {
        ObjectKind trickery = kindNamed(new ItemObjectReader().parse(REAL_FILE), "Trickery", TValue.TV_AMULET);

        assertEquals("1+0d0m4", dice(trickery.getModifier(ObjectModifier.OM_DEX)));
        assertEquals("1+0d0m2", dice(trickery.getModifier(ObjectModifier.OM_SPEED)));
        assertEquals("1+0d0m2", dice(trickery.getModifier(ObjectModifier.OM_STEALTH)));
        assertEquals("1+1d3m3", dice(trickery.getModifier(ObjectModifier.OM_SEARCH)));
        assertEquals("1+1d3m3", dice(trickery.getModifier(ObjectModifier.OM_INFRA)));
        assertEquals(1, trickery.getElInfo(ElementEnum.ELEM_NEXUS).getResLevel());
        assertEquals(1, trickery.getElInfo(ElementEnum.ELEM_POIS).getResLevel());
        assertEquals(0, trickery.getElInfo(ElementEnum.ELEM_FIRE).getResLevel());
    }

    /**
     * {@code Reckless Attacks} has {@code armor:0:-8+4d3}: a leading minus negates the whole roll, and
     * {@code parse_random} keeps the random parts positive, so it lowers the base by the most the dice
     * can add (4 x (3 + 1) = 16) as well as negating it, giving base -24 with 4d3 on top. {@code the
     * Magi} has {@code armor:0:d5M5} (no base, one die, a bonus).
     */
    @Test
    void realFileToArmourDiceKeepTheirBaseDiceSidesAndBonusApart() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        assertEquals("-24+4d3m0", dice(kindNamed(kinds, "Reckless Attacks", TValue.TV_RING).getToA()));
        assertEquals("0+1d5m5", dice(kindNamed(kinds, "the Magi", TValue.TV_AMULET).getToA()));
    }

    /**
     * The wand of Light has {@code charges:6+d10} and a {@code vis-msg:} but no {@code msg:}; the
     * ring of Light shares its name and carries neither. Kinds are told apart by tval, not name.
     */
    @Test
    void realFileWandOfLightHasChargesAndAVisMsgAndSharesItsNameWithARing() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind wand = kindNamed(kinds, "Light", TValue.TV_WAND);
        ObjectKind ring = kindNamed(kinds, "Light", TValue.TV_RING);

        assertNotSame(wand, ring);
        assertEquals("6+1d10m0", dice(wand.getCharge()));
        assertEquals("A line of shimmering blue light appears.", kindField(wand, "visMessage"));
        assertNull(kindField(wand, "effectMessage"));
        assertNull(kindField(ring, "visMessage"));
        assertEquals("0+0d0m0", dice(ring.getCharge()));
    }

    /**
     * {@code Cure Light Wounds} is both a potion and a staff. The potion has {@code pile:90:2d3} and
     * five effects; the staff has {@code charges:6+d5}. C allows the repeated name, so both load.
     */
    @Test
    void realFileCureLightWoundsIsBothAPotionAndAStaff() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind potion = kindNamed(kinds, "Cure Light Wounds", TValue.TV_POTION);
        ObjectKind staff = kindNamed(kinds, "Cure Light Wounds", TValue.TV_STAFF);

        assertEquals(90, (int) kindField(potion, "genMultProb"));
        assertEquals("0+2d3m0", dice(kindField(potion, "stackSize")));
        assertEquals(5, potion.getEffect().size());
        assertEquals("6+1d5m0", dice(staff.getCharge()));
    }

    /**
     * {@code Dragon Breath} is the one record in the file that repeats a scalar directive: it has
     * {@code pile:100:1d2} and, after its {@code alloc:}, {@code pile:70:1d3}. The handler assigns,
     * so C is left holding the second.
     */
    @Test
    void realFileDragonBreathRepeatsPileAndTheLastLineWins() throws Exception {
        ObjectKind breath = kindNamed(new ItemObjectReader().parse(REAL_FILE), "Dragon Breath", TValue.TV_POTION);

        assertEquals(70, (int) kindField(breath, "genMultProb"));
        assertEquals("0+1d3m0", dice(kindField(breath, "stackSize")));
    }

    /**
     * The shipped file's only {@code slay:} line is on the Mace of Disruption, and it has no
     * {@code brand:} lines at all.
     */
    @Test
    void realFileMaceOfDisruptionCarriesTheOnlySlayAndNoKindCarriesABrand() throws IOException {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind mace = kindNamed(kinds, "& Mace~ of Disruption", TValue.TV_HAFTED);

        assertEquals(Set.of("UNDEAD_3"), mace.getSlays().stream().map(Slay::getCode).collect(Collectors.toSet()));
        assertEquals(1, kinds.stream().filter(k -> !k.getSlays().isEmpty()).count());
        assertEquals(0, kinds.stream().filter(k -> !k.getBrands().isEmpty()).count());
    }

    /**
     * The only two {@code curse:} lines in the file are {@code curse:teleportation:100}, on the ring
     * and the amulet that are both called Teleportation.
     */
    @Test
    void realFileTeleportationRingAndAmuletEachCarryTheTeleportationCurseAtOneHundred() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);

        for (TValue tval : List.of(TValue.TV_RING, TValue.TV_AMULET)) {
            Map<Curse, CurseData> curses = kindField(kindNamed(kinds, "Teleportation", tval), "curses");
            assertEquals(1, curses.size(), tval.name());
            assertEquals("teleportation", curses.keySet().iterator().next().getName());
            assertEquals(100, curses.values().iterator().next().getPower());
        }
        assertEquals(2, kinds.stream().filter(k -> !k.getCurses().isEmpty()).count());
    }

    /**
     * {@code & Elven Cloak~}'s two {@code desc:} lines run together: the first ends "wondrously" and
     * the second begins with the space that keeps the words apart.
     */
    @Test
    void realFileElvenCloakDescriptionIsItsTwoLinesJoinedWithNoSeparator() throws Exception {
        ObjectKind cloak = kindNamed(new ItemObjectReader().parse(REAL_FILE), "& Elven Cloak~", TValue.TV_CLOAK);

        assertEquals("A mantle made of curious silken material by the Galadrim that wondrously takes on "
                + "the hues and shapes of its surroundings.", kindField(cloak, "text"));
    }

    // ---- a missing file, and the directives C cannot read either -----------

    /**
     * {@code run_parse_object} calls {@code parse_file_quit_not_found}, which ends the program. The
     * port surfaces the same failure as an {@link IOException} for the caller to treat as fatal.
     */
    @Test
    void missingFileIsAnIOException() {
        String missing = tempDir.resolve("no-such-object.txt").toString();

        assertThrows(IOException.class, () -> new ItemObjectReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new ItemObjectReader().parse(missing));
    }

    /**
     * An {@code attack:} takes {@code rand hd rand to-h rand to-d} and C registers all three as
     * required, so two of the three is a parser error. Here it is a syntax error: no items.
     */
    @Test
    void attackWithOnlyTwoOfItsThreeFieldsIsAHardError() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("short-attack.txt", obj("short", HEAD + "attack:1d4:0\n")));

    }

    /**
     * {@code init_parse_object} registers no {@code foo} directive, so C reports an undefined
     * directive. Here it is a syntax error: no items.
     */
    @Test
    void unknownDirectiveIsAHardError() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("unknown-directive.txt", obj("odd", HEAD + "foo:bar\n")));

    }

    /**
     * {@code parse_object_alloc} hands the range to {@code grab_int_range} with the separator
     * {@code "to"}, which fails when it is missing. So does the grammar.
     */
    @Test
    void allocWithoutToIsAHardError() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("alloc-no-to.txt", obj("no to", HEAD + "alloc:5:10\n")));

    }

    // ---- repeated and accumulating directives (obj-init.c: parse_object_*) -----

    /**
     * Every scalar handler assigns, so a repeated {@code level:} keeps the last value.
     */
    @Test
    void aRepeatedScalarDirectiveKeepsTheLastValue() throws Exception {
        ParseResult<ObjectKind> result = load("repeat-level.txt", obj("again", HEAD + "level:3\nlevel:9\n"));

        assertEquals(9, (int) kindField(result.items().get(0), "level"));
    }

    /**
     * {@code msg:}, {@code vis-msg:} and {@code desc:} each call {@code string_append}, which adds
     * the new text to the end of the old with nothing between: any space has to be in the data.
     */
    @Test
    void msgVisMsgAndDescAppendWithNoSeparator() throws Exception {
        ParseResult<ObjectKind> result = load("append.txt", obj("joined", HEAD + "msg:one\nmsg: two\nvis-msg:three\nvis-msg:four\ndesc:five\ndesc: six\n"));

        ObjectKind kind = result.items().get(0);
        assertEquals("one two", kindField(kind, "effectMessage"));
        assertEquals("threefour", kindField(kind, "visMessage"));
        assertEquals("five six", kindField(kind, "text"));
    }

    /**
     * {@code parse_object_flags} sets bits and never clears them, so flags from two lines are both
     * kept; {@code slay:} and {@code brand:} each set another entry in the kind's array.
     */
    @Test
    void flagsSlaysAndBrandsAccumulateAcrossLines() throws Exception {
        ParseResult<ObjectKind> result = load("accumulate.txt", obj("more", HEAD + "flags:EASY_KNOW\nflags:GOOD | SHOW_MULT\n"
                        + "slay:EVIL_2\nslay:ANIMAL_2\nbrand:FIRE_2\nbrand:COLD_2\n"));

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_SHOW_MULT));
        assertEquals(Set.of("EVIL_2", "ANIMAL_2"),
                kind.getSlays().stream().map(Slay::getCode).collect(Collectors.toSet()));
        assertEquals(Set.of("FIRE_2", "COLD_2"),
                kind.getBrands().stream().map(Brand::getCode).collect(Collectors.toSet()));
    }

    /**
     * {@code values:} lines add to the same arrays, so a second line extends the first.
     */
    @Test
    void valuesOnTwoLinesAreBothKept() throws Exception {
        ParseResult<ObjectKind> result = load("two-values.txt", obj("both", HEAD + "values:STR[2]\nvalues:RES_FIRE[1] | DEX[3]\n"));

        ObjectKind kind = result.items().get(0);
        assertEquals("2+0d0m0", dice(kind.getModifier(ObjectModifier.OM_STR)));
        assertEquals("3+0d0m0", dice(kind.getModifier(ObjectModifier.OM_DEX)));
        assertEquals(1, kind.getElInfo(ElementEnum.ELEM_FIRE).getResLevel());
    }

    /**
     * C never checks a name against the ones before it: every {@code name:} makes a new kind.
     */
    @Test
    void twoKindsWithTheSameNameBothLoadInFileOrder() throws IOException {
        ParseResult<ObjectKind> result = load("dup.txt", obj("same", HEAD + "level:1\n") + obj("other", HEAD) + obj("same", HEAD + "level:2\n"));

        assertEquals(List.of("same", "other", "same"),
                result.items().stream().map(ObjectKind::getName).collect(Collectors.toList()));
    }

    // ---- places the grammar or assembler differ from C ----------------------

    /**
     * C leaves every field of a record 0 unless a directive sets it, so a kind with no {@code attack:},
     * {@code armor:}, {@code charges:}, {@code pile:}, {@code pval:} or {@code time:} line holds a zero
     * {@code struct random} for each, and the port does the same. {@code ObjectUtils.objectPrep} calls
     * {@code randCalc} on {@code getToH()}, {@code getToD()} and {@code getToA()} unconditionally, and on
     * {@code getPVal()} for a potion, food, flask or launcher, so a plain kind such as a ration needs
     * them non-null.
     */
    @Test
    void absentDiceAreAZeroRandomAsInC() throws Exception {
        List<ObjectKind> kinds = new ItemObjectReader().parse(REAL_FILE);
        ObjectKind pile = kindNamed(kinds, "<pile>", TValue.TV_NONE);

        assertEquals("0+0d0m0", dice(pile.getToH()));
        assertEquals("0+0d0m0", dice(pile.getToD()));
        assertEquals("0+0d0m0", dice(pile.getToA()));
        assertEquals("0+0d0m0", dice(pile.getCharge()));
        assertEquals("0+0d0m0", dice(kindField(pile, "stackSize")));
        assertEquals("0+0d0m0", dice(pile.getPVal()));
        assertEquals("0+0d0m0", dice(pile.getTime()));
        assertEquals("0+0d0m0", dice(kindField(pile, "baseDamage")));
    }

    /**
     * {@code parse_object_name} needs nothing after it, so C accepts a record of a name alone. The
     * grammar wants at least one directive, so it is a syntax error here: no items.
     */
    @Test
    void aRecordWithOnlyANameLineIsASyntaxErrorHereWhereCAcceptsIt() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("name-only.txt", "name:bare\n"));

    }

    /**
     * {@code grab_int_range} insists on white space after the first number, so C rejects
     * {@code 1to100}. The lexer's {@code ALLOC_TO} makes the spaces optional.
     */
    @Test
    void allocWithNoSpacesAroundToIsAcceptedHereWhereCRejectsIt() throws Exception {
        ParseResult<ObjectKind> result = load("alloc-tight.txt", obj("tight", HEAD + "alloc:5:1to100\n"));

        ObjectKind kind = result.items().get(0);
        assertEquals(1, (int) kindField(kind, "alloc_min"));
        assertEquals(100, (int) kindField(kind, "alloc_max"));
    }

    /**
     * {@code parse_object_curse} stores a power only when it is above 0 and never clears one, so
     * {@code curse:teleportation:50} followed by {@code curse:teleportation:0} leaves the 50 in C.
     * The grammar now hands the assembler every {@code curse:} line in file order, and the assembler
     * ignores a power of 0 or below, so the 50 survives here too.
     *
     * <p>Method aLaterZeroPowerCurseLeavesAnEarlierOneAsInC coded before 261105, commented in full on
     * 261105 (was aLaterZeroPowerCurseDropsAnEarlierOneHereWhereCKeepsIt; flipped to the C behaviour
     * on 261105 when the curse lines became a list).
     */
    @Test
    void aLaterZeroPowerCurseLeavesAnEarlierOneAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("curse-zero-after.txt", obj("cursed", HEAD + "curse:teleportation:50\ncurse:teleportation:0\n"));

        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");
        assertEquals(1, curses.size(), () -> "C holds teleportation at 50, got: " + curses);
        assertEquals(50, curses.values().iterator().next().getPower());
    }

    /**
     * The reverse order does keep the curse, as it does in C.
     */
    @Test
    void aZeroPowerCurseBeforeANonZeroOneLeavesTheNonZeroOne() throws Exception {
        ParseResult<ObjectKind> result = load("curse-zero-before.txt", obj("cursed", HEAD + "curse:teleportation:0\ncurse:teleportation:50\n"));

        Map<Curse, CurseData> curses = kindField(result.items().get(0), "curses");
        assertEquals(1, curses.size());
        assertEquals(50, curses.values().iterator().next().getPower());
    }

    /**
     * {@code grab_element_flag} accepts {@code HATES_} followed by an element name and sets
     * {@code EL_INFO_HATES} on that element's {@code info[i].flags}, and on no other bit or element.
     * No record in the shipped file uses it.
     */
    @Test
    void aHatesTokenSetsTheHatesBitOnThatElementOnly() throws IOException {
        ParseResult<ObjectKind> result = load("hates.txt", obj("hates", HEAD + "flags:HATES_FIRE\n"));

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_HATES));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_HATES));
    }

    /**
     * {@code IGNORE_} and {@code HATES_} are different bits of the same per-element field: one
     * element can carry both, and each token reaches only its own bit. The two prefixes are
     * different lengths, so this also guards the element name being cut at the right place for
     * each.
     */
    @Test
    void ignoreAndHatesTokensReachTheirOwnBitsOfTheSameElementInfo() throws IOException {
        ParseResult<ObjectKind> result = load("ign-hates.txt", obj("both", HEAD + "flags:IGNORE_FIRE | HATES_FIRE | IGNORE_ACID | HATES_COLD\n"));

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(kind.getElInfo(ElementEnum.ELEM_FIRE).has(ElementInfoEnum.EL_INFO_HATES));
        assertTrue(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_ACID).has(ElementInfoEnum.EL_INFO_HATES));
        assertTrue(kind.getElInfo(ElementEnum.ELEM_COLD).has(ElementInfoEnum.EL_INFO_HATES));
        assertFalse(kind.getElInfo(ElementEnum.ELEM_COLD).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    /**
     * An element flag and a {@code RES_} level on the same element share one {@code ElementInfo}:
     * C sets {@code res_level} and the flag bit on the same {@code el_info[i]}.
     */
    @Test
    void anElementFlagAndAResistanceLevelShareTheElementInfo() throws IOException {
        ParseResult<ObjectKind> result = load("hates-res.txt", obj("shared", HEAD + "flags:HATES_FIRE\nvalues:RES_FIRE[1]\n"));

        ElementInfo fire = result.items().get(0).getElInfo(ElementEnum.ELEM_FIRE);
        assertTrue(fire.has(ElementInfoEnum.EL_INFO_HATES));
        assertEquals(1, fire.getResLevel());
    }

    /**
     * {@code init_parse_object} registers no {@code effect-msg} directive, so C reports an undefined
     * directive. The shared effect block accepts it here without complaint.
     */
    @Test
    void effectMsgIsAcceptedHereWhereCHasNoSuchDirective() throws IOException {
        ParseResult<ObjectKind> result = load("effect-msg.txt", obj("chatty", HEAD + "effect:HEAL_HP\ndice:5\neffect-msg:hello\n"));

        assertEquals(1, result.items().size());
    }

    /**
     * A record with no {@code type:} keeps C's zero tval, {@code TV_NONE}, and a {@code NULL} base,
     * and C's load accepts it: only {@code parse_object_type} sets the base and the sval. The port
     * gives the kind {@code TV_NONE} and a {@code null} base. No record in the shipped file lacks a
     * {@code type:}.
     */
    @Test
    void aRecordWithNoTypeHasTvNoneAndANullBaseAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("no-type.txt", obj("typeless", "graphics:|:w\n"));

        ObjectKind kind = result.items().get(0);
        assertEquals(TValue.TV_NONE, kind.gettValue());
        assertNull(kind.getBase());
    }

    /**
     * {@code parse_object_graphics} turns a one-character colour with {@code color_char_to_attr} and a
     * longer one with {@code color_text_to_attr}; both answer white for a name they do not know. The
     * lexer takes any colour text, so {@code 9} and {@code q} (neither is a colour code) and a
     * longer unknown name all load, and {@code ItemObjectAssembler} supplies the white. The
     * assembler alone is covered by {@link #anUnresolvedColourFallsBackToWhite}.
     */
    @Test
    void anUnknownColourFallsBackToWhiteAsInC() throws IOException {
        for (String colour : List.of("9", "q", "nonsense")) {
            ParseResult<ObjectKind> result = load("bad-colour.txt", obj("dim", "type:sword\ngraphics:|:" + colour + "\n"));

            AngbandDisplayCharacter cell = result.items().get(0).getCharacter();
            assertEquals('|', cell.getNarrowCharacter(), "colour " + colour);
            assertEquals(ColourEnum.COLOUR_WHITE, cell.getAttributeColour(), "colour " + colour);
        }
    }

    // ---- the assembler alone: records the lexer would not let through ------

    /**
     * Builds the parse record of one kind with every directive absent, as the grammar seeds them,
     * except the few the caller names. It goes straight to {@link ItemObjectAssembler}, so a value
     * the lexer would refuse (a supplementary glyph, an unknown colour, a zero-padded number) can
     * still be put to the assembler.
     */
    private static ItemObjectParseRecord rec(String glyph, String colour, String pVal,
                                             String attackBase, String attackToh,
                                             String allocCommon, String allocLower, String allocUpper) {
        return new ItemObjectParseRecord("probe", glyph, colour, "sword", "", "", "",
                attackBase, attackToh, "", "", "", allocCommon, allocLower, allocUpper, "", "", "", "",
                "", "", List.of(), List.of(), Map.of(), List.of(), List.of(), List.of(), pVal, "", "", 7);
    }

    private static ObjectKind assembleOne(ItemObjectParseRecord record) {
        return new ItemObjectAssembler().assemble(List.of(record), "probe.txt").get(0);
    }

    /**
     * {@code parser_getchar} keeps one wide character. A glyph that fits in a UTF-16 unit is stored
     * narrow, so the cell equals the one the {@code char} constructor builds for it, as C's single
     * {@code wchar_t} makes {@code '|'} and 124 the same glyph.
     */
    @Test
    void aBasicGlyphIsStoredNarrowAndEqualsTheCharBuiltCell() {
        ObjectKind kind = assembleOne(rec("|", "w", "", "", "", "", "", ""));

        AngbandDisplayCharacter cell = kind.getCharacter();
        assertEquals(new AngbandDisplayCharacter('|', ColourEnum.COLOUR_WHITE), cell);
        assertEquals('|', cell.getNarrowCharacter());
        assertEquals(0, cell.getWideCharacter());
    }

    /**
     * U+1F600 does not fit in a {@code char}. It must arrive whole, in the wide half, with the
     * narrow half left at NUL, and not as the first surrogate of its pair.
     */
    @Test
    void aSupplementaryGlyphIsStoredWholeInTheWideHalf() {
        String smile = new String(Character.toChars(0x1F600));
        ObjectKind kind = assembleOne(rec(smile, "w", "", "", "", "", "", ""));

        AngbandDisplayCharacter cell = kind.getCharacter();
        assertEquals(0x1F600, cell.getWideCharacter());
        assertEquals('\0', cell.getNarrowCharacter());
        assertEquals(smile, cell.getCharacter());
        assertEquals(new AngbandDisplayCharacter(0x1F600, ColourEnum.COLOUR_WHITE), cell);
    }

    /**
     * {@code text_mbstowcs(&cval, tok, 1)} converts one character and ignores the rest of the
     * token, whether that first character is a basic one or a supplementary one. In C a longer
     * token never gets that far, since {@code parser_parse()} refuses a {@code char} field of more
     * than one character ({@code PARSE_ERROR_FIELD_TOO_LONG}); here the lexer's glyph token is one
     * code point, so the assembler is only handed a longer one by a test like this.
     */
    @Test
    void onlyTheFirstCodePointOfTheGlyphTokenIsKept() {
        assertEquals('a', assembleOne(rec("abc", "w", "", "", "", "", "", "")).getCharacter().getNarrowCharacter());

        String smile = new String(Character.toChars(0x1F600));
        assertEquals(0x1F600,
                assembleOne(rec(smile + "x", "w", "", "", "", "", "", "")).getCharacter().getWideCharacter());
    }

    /**
     * {@code color_char_to_attr} and {@code color_text_to_attr} both answer white for a colour they
     * do not know. A file cannot reach this, because the lexer refuses such a colour, but the
     * assembler applies it all the same, for a one-character code and for a longer name.
     */
    @Test
    void anUnresolvedColourFallsBackToWhite() {
        for (String colour : List.of("9", "no such colour")) {
            ObjectKind kind = assembleOne(rec("|", colour, "", "", "", "", "", ""));

            assertEquals(ColourEnum.COLOUR_WHITE, kind.getCharacter().getAttributeColour(), colour);
        }
    }

    /**
     * With no glyph at all the kind has no cell: C's zeroed {@code d_char} and {@code d_attr}.
     */
    @Test
    void aRecordWithNoGlyphHasNoCell() {
        assertNull(assembleOne(rec("", "", "", "", "", "", "", "")).getCharacter());
    }

    /**
     * An absent {@code pval:} is the zero {@code random_value} of a zeroed C kind, not an error and
     * not {@code null}.
     */
    @Test
    void anAbsentPvalIsAZeroRandom() {
        assertSameDice(Random.Zero(), assembleOne(rec("|", "w", "", "", "", "", "", "")).getPVal());
    }

    /**
     * {@code parser_getrand} refuses a {@code pval} that is not dice, with
     * {@code PARSE_ERROR_NOT_RANDOM}, and C stops the load. It does not become zero.
     */
    @Test
    void aPvalThatIsNotDiceStopsTheLoad() {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> assembleOne(rec("|", "w", "not dice", "", "", "", "", "")));

        assertTrue(thrown.getMessage().contains("not dice"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("line: 7"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("probe.txt"), thrown.getMessage());
    }

    /**
     * Each rand message names its own value. The base attack and to-hit messages once printed the
     * kind's {@code time:} string instead.
     */
    @Test
    void anUnparseableAttackFigureIsNamedInItsOwnMessage() {
        DataFileParseException base = assertThrows(DataFileParseException.class,
                () -> assembleOne(rec("|", "w", "", "bad base", "", "", "", "")));
        assertTrue(base.getMessage().contains("base attack value: bad base"), base.getMessage());

        DataFileParseException toh = assertThrows(DataFileParseException.class,
                () -> assembleOne(rec("|", "w", "", "", "bad hit", "", "", "")));
        assertTrue(toh.getMessage().contains("attack-to-hit value: bad hit"), toh.getMessage());
    }

    /**
     * {@code parse_object_alloc} reads the commonness with {@code parser_getint}, which is
     * {@code strtol} in base 0, so {@code 010} is eight; it reads the two ends of the range with
     * {@code grab_int_range}, which is {@code strtol} in base 10, so {@code 010} is ten there and
     * {@code 08} is eight.
     */
    @Test
    void allocCommonnessIsReadInBaseZeroAndTheRangeInBaseTen() throws Exception {
        ObjectKind padded = assembleOne(rec("|", "w", "", "", "", "010", "010", "020"));
        assertEquals(8, (int) kindField(padded, "alloc_prob"));
        assertEquals(10, (int) kindField(padded, "alloc_min"));
        assertEquals(20, (int) kindField(padded, "alloc_max"));

        ObjectKind eightNine = assembleOne(rec("|", "w", "", "", "", "0x10", "08", "09"));
        assertEquals(16, (int) kindField(eightNine, "alloc_prob"));
        assertEquals(8, (int) kindField(eightNine, "alloc_min"));
        assertEquals(9, (int) kindField(eightNine, "alloc_max"));
    }

    /**
     * {@code alloc} is one line of commonness and range together in C. A commonness with either end
     * of the range missing is refused here.
     */
    @Test
    void anAllocCommonnessWithoutBothRangeEndsStopsTheLoad() {
        assertThrows(DataFileParseException.class,
                () -> assembleOne(rec("|", "w", "", "", "", "5", "", "20")));
        assertThrows(DataFileParseException.class,
                () -> assembleOne(rec("|", "w", "", "", "", "5", "1", "")));
    }

    // ---- ItemObjectAssembler, clause by clause against parse_object_* and finish_parse_object ----

    /**
     * A record with every directive absent except what a test sets. The tests below hand this to
     * the assembler directly, so they reach cases the lexer and grammar would stop first.
     */
    private static final class Probe {
        String tval = "sword";
        String colour = "w";
        String weight = "";
        String cost = "";
        String power = "";
        String pileChance = "";
        String pileNumber = "";
        String attackBase = "";
        List<String> flags = List.of();
        Map<String, String> values = Map.of();
        List<String> brand = List.of();
        List<String> slay = List.of();
        List<FullCurseDataParseRecord> curse = List.of();

        ObjectKind build() {
            return new ItemObjectAssembler().assemble(List.of(new ItemObjectParseRecord("probe", "|", colour,
                    tval, "", weight, cost, attackBase, "", "", "", "", "", "", "", "", pileChance, pileNumber,
                    power, "", "", List.of(), flags, values, brand, slay, curse, "", "", "", 7)), "probe.txt").get(0);
        }
    }

    /**
     * {@code obj_flags} and {@code kind_flags} in {@code obj-init.c} both end in {@code MAX} (the
     * {@code list-*-flags.h} headers end on it), and {@code lookup_flag} starts at table index 1, so
     * only the {@code NONE} at index 0 is skipped. C therefore takes a {@code MAX} token and sets
     * the sentinel bit in both flag sets.
     */
    @Test
    void aMaxFlagTokenIsTakenIntoBothFlagTablesAsInC() {
        Probe probe = new Probe();
        probe.flags = List.of("MAX");

        ObjectKind kind = probe.build();

        assertTrue(kind.getFlags().has(ObjectFlag.OF_MAX));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_MAX));
    }

    /**
     * Index 0 of both C flag tables holds {@code NONE} and {@code lookup_flag} never looks there,
     * and no element name is {@code NONE}, so the token matches nothing and
     * {@code parse_object_flags} returns {@code PARSE_ERROR_INVALID_FLAG}.
     */
    @Test
    void aNoneFlagTokenIsRefusedAsInC() {
        Probe probe = new Probe();
        probe.flags = List.of("NONE");

        DataFileParseException thrown = assertThrows(DataFileParseException.class, probe::build);
        assertTrue(thrown.getMessage().contains("NONE"), thrown.getMessage());
    }

    /**
     * {@code grab_element_flag} compares the text after the underscore with {@code element_names},
     * which stops short of {@code ELEM_MAX} and holds no {@code NONE}, so neither sentinel names an
     * element and the token is an invalid flag.
     */
    @Test
    void anIgnoreOrHatesTokenNamingASentinelElementIsRefused() {
        for (String token : List.of("IGNORE_NONE", "HATES_MAX", "IGNORE_MAX", "HATES_NONE")) {
            Probe probe = new Probe();
            probe.flags = List.of(token);

            assertThrows(DataFileParseException.class, probe::build, token);
        }
    }

    /**
     * {@code grab_flag} and {@code grab_element_flag} compare with {@code streq} and
     * {@code strncmp}, which are case-sensitive, so a lower-case flag name is no flag.
     */
    @Test
    void flagTokensAreCaseSensitiveAsInC() {
        for (String token : List.of("easy_know", "ignore_fire", "Hates_Fire")) {
            Probe probe = new Probe();
            probe.flags = List.of(token);

            assertThrows(DataFileParseException.class, probe::build, token);
        }
    }

    /**
     * {@code obj_mods} is the stats followed by the modifiers, with no {@code NONE} and no
     * {@code MAX}, and {@code grab_rand_value} matches names with {@code streq}. A sentinel name or
     * a lower-case one matches nothing, and {@code parse_object_values} returns
     * {@code PARSE_ERROR_INVALID_VALUE}.
     */
    @Test
    void aModifierSentinelOrLowerCaseModifierIsRefusedAsInC() {
        for (String key : List.of("NONE", "MAX", "str")) {
            Probe probe = new Probe();
            probe.values = Map.of(key, "1");

            assertThrows(DataFileParseException.class, probe::build, key);
        }
    }

    /**
     * {@code grab_index_and_int} reads the figure with {@code find_value_arg}: {@code strtol} in
     * base 10, accepted only when the next character is the closing bracket, and refused at
     * {@code INT_MIN} or {@code INT_MAX}. An empty bracket reads nothing, stops on the
     * {@code ]} and is accepted as 0. The level is then stored in the element's {@code res_level}.
     */
    @Test
    void aResistanceFigureIsReadAsFindValueArgReadsIt() {
        Map<String, Integer> accepted = new LinkedHashMap<>();
        accepted.put("1", 1);
        accepted.put("-1", -1);
        accepted.put("+3", 3);
        accepted.put("0", 0);
        accepted.put("", 0);
        accepted.put("2147483646", (int) (short) 2147483646);
        accepted.put("-2147483647", (int) (short) -2147483647);
        for (Map.Entry<String, Integer> entry : accepted.entrySet()) {
            Probe probe = new Probe();
            probe.values = Map.of("RES_FIRE", entry.getKey());

            assertEquals(entry.getValue(), (int) probe.build().getElInfo(ElementEnum.ELEM_FIRE).getResLevel(),
                    "[" + entry.getKey() + "]");
        }

        for (String refused : List.of("5x", "-", "+", "1d2", "2147483647", "-2147483648")) {
            Probe probe = new Probe();
            probe.values = Map.of("RES_FIRE", refused);

            assertThrows(DataFileParseException.class, probe::build, "[" + refused + "]");
        }
    }

    /**
     * {@code element_names} holds upper-case names only and {@code streq} is case-sensitive, so
     * {@code RES_fire} names no element.
     */
    @Test
    void aLowerCaseResistanceElementIsRefusedAsInC() {
        Probe probe = new Probe();
        probe.values = Map.of("RES_fire", "1");

        assertThrows(DataFileParseException.class, probe::build);
    }

    /**
     * C sets {@code k->slays[i]} and {@code k->brands[i]} in boolean arrays indexed by the
     * registry, so the order the record names them in is lost and a walk of the kind meets them in
     * registry order. The sets here are built by walking the registry, so they must come out in
     * registry order whichever way round the record lists them.
     */
    @Test
    void brandsAndSlaysComeOutInRegistryOrderWhateverOrderTheRecordNamesThem() {
        List<Slay> slays = ObjectRegistry.getSlays();
        List<Brand> brands = ObjectRegistry.getBrands();
        assertTrue(slays.size() >= 3 && brands.size() >= 3, "the seeded registries are large enough");

        Probe probe = new Probe();
        probe.slay = List.of(slays.get(2).getCode(), slays.get(0).getCode(), slays.get(1).getCode());
        probe.brand = List.of(brands.get(2).getCode(), brands.get(0).getCode(), brands.get(1).getCode());
        ObjectKind kind = probe.build();

        assertEquals(slays.subList(0, 3), new ArrayList<>(kind.getSlays()));
        assertEquals(brands.subList(0, 3), new ArrayList<>(kind.getBrands()));
    }

    /**
     * {@code k->curses} is an array indexed by curse, so a walk of it meets the curses in index
     * order whichever order the record named them in.
     */
    @Test
    void cursesComeOutInCurseIndexOrderWhateverOrderTheRecordNamesThem() {
        List<Curse> registry = new ArrayList<>(ObjectRegistry.getCurses());
        registry.sort(Curse.CURSE_ORDER);
        Curse low = registry.get(0);
        Curse high = registry.get(registry.size() - 1);
        assertTrue(low.getIndex() < high.getIndex(), "the two curses have different indices");

        Probe probe = new Probe();
        probe.curse = List.of(new FullCurseDataParseRecord(high.getName(), "50", ""),
                new FullCurseDataParseRecord(low.getName(), "20", ""));
        ObjectKind kind = probe.build();

        assertEquals(List.of(low, high), new ArrayList<>(kind.getCurses().keySet()));
        assertEquals(20, kind.getCurses().get(low).getPower());
        assertEquals(50, kind.getCurses().get(high).getPower());
    }

    /**
     * {@code tval_find_idx("none")} is 0, not -1, so {@code type:none} is a legal line. The kind
     * keeps {@code TV_NONE} and whatever base the registry holds for it, and the load goes on.
     */
    @Test
    void typeNoneIsALegalTvalAsInC() {
        Probe probe = new Probe();
        probe.tval = "none";

        ObjectKind kind = probe.build();

        assertEquals(TValue.TV_NONE, kind.gettValue());
        assertSame(ObjectRegistry.getBaseFromTVal(TValue.TV_NONE), kind.getBase());
    }

    /**
     * {@code color_text_to_attr} matches a name with {@code my_stricmp}, so case does not matter;
     * {@code color_char_to_attr} compares the code character with {@code ==}, so it does.
     */
    @Test
    void aColourNameIgnoresCaseButAColourCodeDoesNot() {
        for (String name : List.of("red", "RED", "Red")) {
            Probe probe = new Probe();
            probe.colour = name;

            assertEquals(ColourEnum.COLOUR_RED, probe.build().getCharacter().getAttributeColour(), name);
        }

        Probe lower = new Probe();
        lower.colour = "r";
        Probe upper = new Probe();
        upper.colour = "R";

        assertEquals(ColourEnum.COLOUR_RED, lower.build().getCharacter().getAttributeColour());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, upper.build().getCharacter().getAttributeColour());
    }

    /**
     * {@code parser_getint} is {@code strtol} in base 0, so {@code weight}, {@code cost} and
     * {@code power} read {@code 010} as eight, {@code 0x10} as sixteen and {@code -3} as minus three.
     */
    @Test
    void theIntegerDirectivesAreReadInBaseZeroAsParserGetintReadsThem() {
        Probe probe = new Probe();
        probe.weight = "010";
        probe.cost = "0x10";
        probe.power = "-3";

        ObjectKind kind = probe.build();

        assertEquals(8, kind.getWeight());
        assertEquals(16, kind.getCost());
        assertEquals(-3, kind.getPower());
    }

    /**
     * {@code parse_object_attack} stores the dice and sides of {@code hd} in {@code k->dd} and
     * {@code k->ds}.
     */
    @Test
    void theAttackDiceAndSidesAreTheHdDiceAndSides() {
        Probe probe = new Probe();
        probe.attackBase = "3d4";

        ObjectKind kind = probe.build();

        assertEquals(3, kind.getDamageDice());
        assertEquals(4, kind.getDamageSides());
    }

    /**
     * {@code pile} is one line of probability and stack dice in C ({@code "pile int prob rand
     * stack"}), so a probability with no stack is a missing field. The stack dice themselves must
     * parse.
     */
    @Test
    void aPileChanceNeedsAStackAmountThatParses() {
        Probe noAmount = new Probe();
        noAmount.pileChance = "25";
        assertThrows(DataFileParseException.class, noAmount::build);

        Probe badAmount = new Probe();
        badAmount.pileChance = "25";
        badAmount.pileNumber = "not dice";
        assertThrows(DataFileParseException.class, badAmount::build);

        Probe fine = new Probe();
        fine.pileChance = "25";
        fine.pileNumber = "2d3";
        assertDoesNotThrow(fine::build);
    }

    // ---- Stage 6 item 1: the mode newline rules take the shared blanks, tabs and \r ------------
    //
    // The default-mode half of the same work is proved on BrandLexer in BrandReaderTest. Here every
    // line that pushes a mode ({@code flags:}, {@code alloc:}, {@code values:}, {@code pval:}) ends
    // in that mode's own newline rule, which has to take what the shared NL fragment takes.

    /**
     * C's reader has already turned a tab into spaces by the time it parses, so blanks and tabs
     * before the newline are trailing whitespace on a flag list, an alloc range, a value list and a
     * number. Each of those lines ends in its own mode's newline rule.
     *
     * <p>Function modeLinesTakeBlanksAndTabsBeforeTheNewlineAsInC coded on 261109, commented in full
     * on 261109.
     */
    @Test
    void modeLinesTakeBlanksAndTabsBeforeTheNewlineAsInC() throws Exception {
        for (String blank : List.of(" ", "\t", " \t ")) {
            String shown = "blank [" + blank.replace("\t", "\\t") + "]";
            ParseResult<ObjectKind> result = load("mode-blank.txt", obj("blanks", HEAD
                    + "flags:EASY_KNOW | SHOW_DICE" + blank + "\n"
                    + "alloc:70:1 to 100" + blank + "\n"
                    + "values:STR[1]" + blank + "\n"
                    + "pval:7" + blank + "\n"
                    + "weight:5\n"));

            ObjectKind kind = result.items().get(0);
            assertEquals(1, result.items().size(), shown);
            assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW), shown);
            assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_SHOW_DICE), shown);
            assertEquals(70, (int) kindField(kind, "alloc_prob"), shown);
            assertEquals(100, (int) kindField(kind, "alloc_max"), shown);
            assertEquals(5, kind.getWeight(), shown);
        }
    }

    /**
     * C ends a line at a bare {@code '\r'} and at {@code '\r\r\n'} as well as at {@code '\n'}, so the
     * directive after a moded line is read whichever of the three ended it. The weight after each
     * line shows the mode popped and the next directive was seen.
     *
     * <p>Function modeLinesEndAtABareCarriageReturnAsInC coded on 261109, commented in full on 261109.
     */
    @Test
    void modeLinesEndAtABareCarriageReturnAsInC() throws Exception {
        for (String eol : List.of("\r", "\r\n", "\r\r\n")) {
            String shown = "line end " + eol.replace("\r", "\\r").replace("\n", "\\n");
            ParseResult<ObjectKind> result = load("mode-cr.txt", "name:cr" + "\n" + HEAD
                    + "flags:EASY_KNOW" + eol
                    + "alloc:70:1 to 100" + eol
                    + "values:STR[1]" + eol
                    + "pval:7" + eol
                    + "weight:5" + eol
                    + "cost:9" + eol);

            ObjectKind kind = result.items().get(0);
            assertEquals(1, result.items().size(), shown);
            assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW), shown);
            assertEquals(70, (int) kindField(kind, "alloc_prob"), shown);
            assertEquals(5, kind.getWeight(), shown);
            assertEquals(9, kind.getCost(), shown);
        }
    }

    /**
     * Indentation before a directive and before a comment is skipped by the line-start rule, the
     * moded directives included: the rule is in the default mode, and each pop returns to it at the
     * start of a fresh line.
     *
     * <p>Function modeLinesAfterIndentationLoadAsInC coded on 261109, commented in full on 261109.
     */
    @Test
    void modeLinesAfterIndentationLoadAsInC() throws Exception {
        ParseResult<ObjectKind> result = load("mode-indent.txt", "name:indented\n"
                + "\ttype:sword\n  graphics:|:w\n\tflags:EASY_KNOW\n  # indented comment\n"
                + "\talloc:70:1 to 100\n\tweight:5\n");

        ObjectKind kind = result.items().get(0);
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        assertEquals(70, (int) kindField(kind, "alloc_prob"));
        assertEquals(5, kind.getWeight());
    }
}
