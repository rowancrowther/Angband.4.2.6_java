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

package uk.co.jackoftradesltd.backend.parser.grammars;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.parser.MonsterBaseReader;
import uk.co.jackoftradesltd.backend.parser.PainReader;
import uk.co.jackoftradesltd.backend.parser.ShapeReader;
import uk.co.jackoftradesltd.backend.parser.SummonReader;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.effect.*;
import uk.co.jackoftradesltd.middle.enums.*;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimed;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification of the sub-type and expression-base rules of {@link EffectAssembler} against what
 * {@code effect_subtype} ({@code effects.c}), {@code grab_effect_data} ({@code init.c}) and
 * {@code effect_value_base_by_name} ({@code effects.c}) do. Expected values come from the C source,
 * not from the Java.
 *
 * @author Rowan Crowther
 */
class EffectAssemblerSubTypeTest {

    private static Object savedPains;
    private static Object savedBases;
    private static Object savedShapes;
    private static Object savedSummons;

    private static Object set(Class<?> owner, String field, Object value) throws Exception {
        Field f = owner.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    @BeforeAll
    static void seed() throws Exception {
        savedPains = set(MonsterRegistry.class, "monsterPains",
                new PainReader().parseWithResults("lib/gamedata/pain.txt").items());
        savedBases = set(MonsterRegistry.class, "monsterBases",
                new MonsterBaseReader().parseWithResults("lib/gamedata/monster_base.txt").items());
        savedSummons = set(MonsterRegistry.class, "summons",
                new SummonReader().parseWithResults("lib/gamedata/summon.txt").items());
        savedShapes = set(PlayerRegistry.class, "playerShapes",
                new ShapeReader().parseWithResults("lib/gamedata/shape.txt").items());
    }

    @AfterAll
    static void restore() throws Exception {
        set(MonsterRegistry.class, "monsterPains", savedPains);
        set(MonsterRegistry.class, "monsterBases", savedBases);
        set(MonsterRegistry.class, "summons", savedSummons);
        set(PlayerRegistry.class, "playerShapes", savedShapes);
    }

    private static EffectSubTypeWrapper sub(EffectSubTypeEnum kind, String value) {
        return EffectAssembler.getWrapperSubType(kind, value);
    }

    private static void refused(EffectSubTypeEnum kind, String value) {
        assertThrows(DataFileParseException.class, () -> sub(kind, value), kind + " '" + value + "'");
    }

    private static Effect assembleOne(String type, String sub, String chars, String bases, String ops) {
        EffectParseRecord r = new EffectParseRecord(type, sub, "", "", "", "", "",
                chars, bases, ops, null, 1);
        return EffectAssembler.assemble(List.of(r), "test.txt").get(0);
    }

    // ---- the four stage 1 findings ----

    @Test
    void summonUnknownNameStopsTheLoad() {
        refused(EffectSubTypeEnum.EST_SUMMON, "ANIMALS");
        refused(EffectSubTypeEnum.EST_SUMMON, "any");
    }

    @Test
    void summonKnownAndAbsent() throws Exception {
        assertEquals("ANY", sub(EffectSubTypeEnum.EST_SUMMON, "").getSummonWrapper().getName());
        assertEquals("ANY", sub(EffectSubTypeEnum.EST_SUMMON, "ANY").getSummonWrapper().getName());
        assertEquals("UNDEAD", sub(EffectSubTypeEnum.EST_SUMMON, "UNDEAD").getSummonWrapper().getName());
    }

    @Test
    void glyphAbsentIsNoGlyphAsInC() throws Exception {
        // effect_subtype never runs for a missing token, so effect->subtype stays 0, which is
        // GLYPH_NONE in trap.h (GLYPH_WARDING is 1)
        assertEquals(GlyphType.GLYPH_NONE, sub(EffectSubTypeEnum.EST_GLYPH, "").getGlyphType());
        assertEquals(GlyphType.GLYPH_NONE,
                valueOf(assembleOne("GLYPH", "", "", "", "")).getGlyphType());
        assertEquals(GlyphType.GLYPH_WARDING, sub(EffectSubTypeEnum.EST_GLYPH, "WARDING").getGlyphType());
        assertEquals(GlyphType.GLYPH_DECOY, sub(EffectSubTypeEnum.EST_GLYPH, "DECOY").getGlyphType());
        refused(EffectSubTypeEnum.EST_GLYPH, "NONE");
        refused(EffectSubTypeEnum.EST_GLYPH, "warding");
    }

    @Test
    void shapechangeAbsentIsTheFirstShapeInTheFile() throws Exception {
        // shape.txt's first name: line is "normal" (sidx 0 in C)
        assertEquals("normal", sub(EffectSubTypeEnum.EST_SHAPECHANGE, "").getShapeWrapper().getName());
        assertEquals("bat", sub(EffectSubTypeEnum.EST_SHAPECHANGE, "bat").getShapeWrapper().getName());
        assertEquals("Pukel-man",
                sub(EffectSubTypeEnum.EST_SHAPECHANGE, "Pukel-man").getShapeWrapper().getName());
        refused(EffectSubTypeEnum.EST_SHAPECHANGE, "Bat");
        refused(EffectSubTypeEnum.EST_SHAPECHANGE, "nosuchshape");
    }

    @Test
    void exprBaseNamedMaxHasNoBaseAsInC() throws Exception {
        assertEquals(EffectBaseType.EFB_NONE, base("MAX"));
        assertEquals(EffectBaseType.EFB_NONE, base("NONE"));
        assertEquals(EffectBaseType.EFB_NONE, base("BOGUS"));
        assertEquals(EffectBaseType.EFB_PLAYER_HP, base("PLAYER_HP"));
        assertEquals(EffectBaseType.EFB_SPELL_POWER, base("SPELL_POWER"));
        assertEquals(EffectBaseType.EFB_MONSTER_PERCENT_HP_GONE, base("MONSTER_PERCENT_HP_GONE"));
    }

    private static EffectBaseType base(String name) throws Exception {
        Effect e = assembleOne("DAMAGE", "", "B", name, "* 2");
        Field fe = Effect.class.getDeclaredField("expression");
        fe.setAccessible(true);
        Expression x = ((List<Expression>) fe.get(e)).get(0);
        Field fb = Expression.class.getDeclaredField("baseType");
        fb.setAccessible(true);
        return (EffectBaseType) fb.get(x);
    }

    private static EffectSubTypeWrapper valueOf(Effect e) throws Exception {
        Field f = Effect.class.getDeclaredField("value");
        f.setAccessible(true);
        return (EffectSubTypeWrapper) f.get(e);
    }

    // ---- absent token: grab_effect_data skips effect_subtype, so the sub-type is C's 0 ----

    @Test
    void absentSubTypeIsIndexZeroOfEveryKind() throws Exception {
        assertEquals(ProjectionEnum.PROJ_ACID, valueOf(assembleOne("BOLT", "", "", "", "")).getProjectionWrapper());
        assertEquals(TimedEffect.TMD_FAST, valueOf(assembleOne("TIMED_INC", "", "", "", "")).getTimedWrapper());
        assertEquals(EffectNourish.EN_INC_BY, valueOf(assembleOne("NOURISH", "", "", "", "")).getNourishWrapper());
        assertEquals(MonTimed.MON_TMD_SLEEP, valueOf(assembleOne("MON_TIMED_INC", "", "", "", "")).getMonTimedWrapper());
        assertEquals(Stats.STAT_STR, valueOf(assembleOne("GAIN_STAT", "", "", "", "")).getStatsWrapper());
        // an absent token leaves effect->subtype at 0, which sets none of the three enchant bits
        assertEquals("", enchantBits(valueOf(assembleOne("ENCHANT", "", "", "", ""))));
        assertEquals(Earthquake.QUAKE_NONE, valueOf(assembleOne("EARTHQUAKE", "", "", "", "")).getQuakeWrapper());
        assertEquals("ANY", valueOf(assembleOne("SUMMON", "", "", "", "")).getSummonWrapper().getName());
        assertEquals("normal", valueOf(assembleOne("SHAPECHANGE", "", "", "", "")).getShapeWrapper().getName());
        assertFalse(valueOf(assembleOne("TELEPORT", "", "", "", "")).getTeleportMonsterMayCast());
        assertFalse(valueOf(assembleOne("TELEPORT_TO", "", "", "", "")).getTeleportToMonsterMayCast());
    }

    // ---- which kinds fold case: my_stricmp for proj/timed/stat, streq for the rest ----

    @Test
    void nourishNamesAreCaseExactAndHaveFourValues() throws Exception {
        assertEquals(EffectNourish.EN_INC_BY, sub(EffectSubTypeEnum.EST_NOURISH, "INC_BY").getNourishWrapper());
        assertEquals(EffectNourish.EN_DEC_BY, sub(EffectSubTypeEnum.EST_NOURISH, "DEC_BY").getNourishWrapper());
        assertEquals(EffectNourish.EN_SET_TO, sub(EffectSubTypeEnum.EST_NOURISH, "SET_TO").getNourishWrapper());
        assertEquals(EffectNourish.EN_INC_TO, sub(EffectSubTypeEnum.EST_NOURISH, "INC_TO").getNourishWrapper());
        refused(EffectSubTypeEnum.EST_NOURISH, "inc_by");
        refused(EffectSubTypeEnum.EST_NOURISH, "NONE");
        refused(EffectSubTypeEnum.EST_NOURISH, "MAX");
    }

    @Test
    void monsterTimedNamesAreCaseExactAndRefuseBothSentinels() throws Exception {
        assertEquals(MonTimed.MON_TMD_SLEEP, sub(EffectSubTypeEnum.EST_MON_TMD, "SLEEP").getMonTimedWrapper());
        refused(EffectSubTypeEnum.EST_MON_TMD, "sleep");
        refused(EffectSubTypeEnum.EST_MON_TMD, "NONE");
        refused(EffectSubTypeEnum.EST_MON_TMD, "MAX");
    }

    @Test
    void enchantAndEarthquakeNamesAreCaseExact() throws Exception {
        // effect_subtype returns ENCH_TOHIT 0x01, ENCH_TODAM 0x02, ENCH_TOBOTH 0x03, ENCH_TOAC 0x04
        // (effect-handler.h), and TOBOTH is both bits 0x01 and 0x02
        assertEquals("TOHIT,TODAM", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "TOBOTH")));
        assertEquals("TOHIT", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "TOHIT")));
        assertEquals("TODAM", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "TODAM")));
        refused(EffectSubTypeEnum.EST_ENCHANT, "toac");
        refused(EffectSubTypeEnum.EST_ENCHANT, "MAX");
        assertEquals(Earthquake.QUAKE_TARGETED, sub(EffectSubTypeEnum.EST_EARTHQUAKE, "TARGETED").getQuakeWrapper());
        assertEquals(Earthquake.QUAKE_NONE, sub(EffectSubTypeEnum.EST_EARTHQUAKE, "NONE").getQuakeWrapper());
        refused(EffectSubTypeEnum.EST_EARTHQUAKE, "none");
    }

    @Test
    void projectionTimedAndStatNamesFoldCase() throws Exception {
        assertEquals(ProjectionEnum.PROJ_FIRE, sub(EffectSubTypeEnum.EST_PROJ, "FiRe").getProjectionWrapper());
        assertEquals(TimedEffect.TMD_FREE_ACT, sub(EffectSubTypeEnum.EST_TMD, "Free_Act").getTimedWrapper());
        assertEquals(Stats.STAT_DEX, sub(EffectSubTypeEnum.EST_STAT, "dEx").getStatsWrapper());
    }

    // ---- the rest of the sub-type table, to catch a regression from the edits ----

    @Test
    void projections() throws Exception {
        assertEquals(ProjectionEnum.PROJ_ACID, sub(EffectSubTypeEnum.EST_PROJ, "").getProjectionWrapper());
        assertEquals(ProjectionEnum.PROJ_FIRE, sub(EffectSubTypeEnum.EST_PROJ, "fire").getProjectionWrapper());
        assertEquals(ProjectionEnum.PROJ_MAX, sub(EffectSubTypeEnum.EST_PROJ, "MAX").getProjectionWrapper());
        assertEquals(ProjectionEnum.PROJ_MON_CRUSH,
                sub(EffectSubTypeEnum.EST_PROJ, "MON_CRUSH").getProjectionWrapper());
        refused(EffectSubTypeEnum.EST_PROJ, "NONE");
        refused(EffectSubTypeEnum.EST_PROJ, "BOGUS");
    }

    @Test
    void timedEffects() throws Exception {
        assertEquals(TimedEffect.TMD_FAST, sub(EffectSubTypeEnum.EST_TMD, "").getTimedWrapper());
        assertEquals(TimedEffect.TMD_FREE_ACT, sub(EffectSubTypeEnum.EST_TMD, "free_act").getTimedWrapper());
        refused(EffectSubTypeEnum.EST_TMD, "NONE");
        refused(EffectSubTypeEnum.EST_TMD, "MAX");
    }

    @Test
    void stats() throws Exception {
        assertEquals(Stats.STAT_STR, sub(EffectSubTypeEnum.EST_STAT, "").getStatsWrapper());
        assertEquals(Stats.STAT_CON, sub(EffectSubTypeEnum.EST_STAT, "con").getStatsWrapper());
        assertEquals(Stats.STAT_MAX, sub(EffectSubTypeEnum.EST_STAT, "MAX").getStatsWrapper());
        refused(EffectSubTypeEnum.EST_STAT, "NONE");
    }

    @Test
    void enchantEarthquakeTeleportAndNone() throws Exception {
        assertEquals("", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "")));
        assertEquals("TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "TOAC")));
        refused(EffectSubTypeEnum.EST_ENCHANT, "NONE");
        assertNotNull(sub(EffectSubTypeEnum.EST_EARTHQUAKE, "NONE"));
        assertNotNull(sub(EffectSubTypeEnum.EST_EARTHQUAKE, "TARGETED"));
        refused(EffectSubTypeEnum.EST_EARTHQUAKE, "targeted");
        assertTrue(sub(EffectSubTypeEnum.EST_TELEPORT, "AWAY").getTeleportMonsterMayCast());
        assertFalse(sub(EffectSubTypeEnum.EST_TELEPORT, "").getTeleportMonsterMayCast());
        refused(EffectSubTypeEnum.EST_TELEPORT, "SELF");
        assertTrue(sub(EffectSubTypeEnum.EST_TELEPORT_TO, "SELF").getTeleportToMonsterMayCast());
        refused(EffectSubTypeEnum.EST_TELEPORT_TO, "AWAY");
        assertNotNull(sub(EffectSubTypeEnum.EST_NONE, "NONE"));
        assertNotNull(sub(EffectSubTypeEnum.EST_NONE, ""));
        refused(EffectSubTypeEnum.EST_NONE, "FOO");
    }

    @Test
    void effectNameIsCaseSensitiveAndSentinelsPass() {
        assertThrows(DataFileParseException.class, () -> assembleOne("bolt", "", "", "", ""));
        assertThrows(DataFileParseException.class, () -> assembleOne("NOTANEFFECT", "", "", "", ""));
        assertNotNull(assembleOne("NONE", "", "", "", ""));
        assertNotNull(assembleOne("MAX", "", "", "", ""));
    }

    // ---- teleport kinds: effect_subtype accepts exactly one case-exact token each ----

    @Test
    void teleportKindsRefuseNoneAndWrongCase() {
        // effect_subtype: EF_TELEPORT takes only "AWAY", EF_TELEPORT_TO only "SELF" (streq), and
        // both break to the -1 return for anything else, "NONE" included
        refused(EffectSubTypeEnum.EST_TELEPORT, "NONE");
        refused(EffectSubTypeEnum.EST_TELEPORT, "away");
        refused(EffectSubTypeEnum.EST_TELEPORT_TO, "NONE");
        refused(EffectSubTypeEnum.EST_TELEPORT_TO, "self");
    }

    // ---- enchant: the sub-type is a set of bits, as effect_handler_ENCHANT reads it ----

    /** The three bits C tests, in C's order, as "TOHIT,TODAM,TOAC" with only the set ones present. */
    private static String enchantBits(EffectSubTypeWrapper w) throws Exception {
        var flags = w.getEnchantWrapper();
        StringBuilder sb = new StringBuilder();
        for (EffectEnchant e : new EffectEnchant[]{EffectEnchant.EE_TOHIT, EffectEnchant.EE_TODAM,
                EffectEnchant.EE_TOAC}) {
            if (!flags.has(e)) continue;
            if (!sb.isEmpty()) sb.append(',');
            sb.append(e.name().substring(3));
        }
        return sb.toString();
    }

    @Test
    void enchantDigitsAreBitFlagsAsInC() throws Exception {
        // effect_subtype stores the number as it is, and effect_handler_ENCHANT tests
        // subtype & 0x01 (to-hit), & 0x02 (to-dam) and & 0x04 (armour); a number carries any mix of
        // the three, and any bit above them is ignored. This path cannot be reached from a data
        // file (accepted deviation 261025), so the method is called directly.
        assertEquals("", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "0")));
        assertEquals("TOHIT", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "1")));
        assertEquals("TODAM", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "2")));
        assertEquals("TOHIT,TODAM", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "3")));
        assertEquals("TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "4")));
        assertEquals("TOHIT,TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "5")));
        assertEquals("TODAM,TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "6")));
        assertEquals("TOHIT,TODAM,TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "7")));
        assertEquals("", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "8")));
        assertEquals("TOHIT", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "9")));
        assertEquals("TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "12")));
        assertEquals("TOHIT,TODAM,TOAC", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "15")));
        assertEquals("", enchantBits(sub(EffectSubTypeEnum.EST_ENCHANT, "16")));
    }

    // ---- registries: shapes and summons are resolved against what is loaded ----

    @Test
    void shapeKindAgainstAnEmptyShapeList() throws Exception {
        Object saved = set(PlayerRegistry.class, "playerShapes", List.of());
        try {
            // a named miss is C's shape_name_to_idx returning -1; an absent token is the first shape
            // in the list, which an empty list cannot give (C would fail later, the port stops here)
            refused(EffectSubTypeEnum.EST_SHAPECHANGE, "normal");
            refused(EffectSubTypeEnum.EST_SHAPECHANGE, "");
        } finally {
            set(PlayerRegistry.class, "playerShapes", saved);
        }
    }

    @Test
    void summonKindAgainstAnEmptySummonList() throws Exception {
        Object saved = set(MonsterRegistry.class, "summons", List.of());
        try {
            // a named miss is summon_name_to_idx returning -1, a refused value
            refused(EffectSubTypeEnum.EST_SUMMON, "ANY");
            // an absent token reads index 0 of an empty list, which is not a data-file error
            assertThrows(IllegalStateException.class, () -> sub(EffectSubTypeEnum.EST_SUMMON, ""));
        } finally {
            set(MonsterRegistry.class, "summons", saved);
        }
    }

    @Test
    void shapesAndSummonsMustBeLoadedFirst() throws Exception {
        Object savedShapes = set(PlayerRegistry.class, "playerShapes", null);
        Object savedSummons = set(MonsterRegistry.class, "summons", null);
        try {
            assertThrows(IllegalStateException.class, () -> sub(EffectSubTypeEnum.EST_SHAPECHANGE, "normal"));
            assertThrows(IllegalStateException.class, () -> sub(EffectSubTypeEnum.EST_SHAPECHANGE, ""));
            assertThrows(IllegalStateException.class, () -> sub(EffectSubTypeEnum.EST_SUMMON, "ANY"));
            assertThrows(IllegalStateException.class, () -> sub(EffectSubTypeEnum.EST_SUMMON, ""));
        } finally {
            set(PlayerRegistry.class, "playerShapes", savedShapes);
            set(MonsterRegistry.class, "summons", savedSummons);
        }
    }

    // ---- accepted deviation 261025: a numeric sub-type is refused, where C reads it with strtol ----

    @TempDir
    Path tempDir;

    @Test
    void numericSubTypeIsRefusedFromADataFile() throws IOException {
        // effect_subtype reads "5" with strtol and takes it as projection 5; the lexer's sub-type
        // token has no digits, so the lexer refuses the line before the assembler sees it. The named form of the same
        // line loads, so it is the digit that is refused.
        Path named = tempDir.resolve("named.txt");
        Files.writeString(named, "name:w\neffect:BOLT:ACID\n");
        assertEquals(1, new ShapeReader().parseWithResults(named.toString()).items().size());

        Path numeric = tempDir.resolve("numeric.txt");
        Files.writeString(numeric, "name:w\neffect:BOLT:5\n");
        assertThrows(ParseCancellationException.class, () -> new ShapeReader().parseWithResults(numeric.toString()));
    }
}
