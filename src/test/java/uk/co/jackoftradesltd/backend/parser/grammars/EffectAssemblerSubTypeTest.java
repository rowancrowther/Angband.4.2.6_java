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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.MonsterBaseReader;
import uk.co.jackoftradesltd.backend.parser.PainReader;
import uk.co.jackoftradesltd.backend.parser.ShapeReader;
import uk.co.jackoftradesltd.backend.parser.SummonReader;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.effect.*;
import uk.co.jackoftradesltd.middle.enums.*;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimed;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        return EffectAssembler.getWrapperSubType(kind, value, new ArrayList<>(), 1);
    }

    private static void refused(EffectSubTypeEnum kind, String value) {
        assertThrows(DataFileParseException.class, () -> sub(kind, value), kind + " '" + value + "'");
    }

    private static Effect assembleOne(String type, String sub, String chars, String bases, String ops) {
        EffectParseRecord r = new EffectParseRecord(type, sub, "", "", "", "", "",
                chars, bases, ops, null, 1);
        return EffectAssembler.assemble(List.of(r), new ArrayList<>(), "test.txt").get(0);
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
        assertEquals(EffectEnchant.EE_NONE, valueOf(assembleOne("ENCHANT", "", "", "", "")).getEnchantWrapper());
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
        assertEquals(EffectEnchant.EE_TOBOTH, sub(EffectSubTypeEnum.EST_ENCHANT, "TOBOTH").getEnchantWrapper());
        assertEquals(EffectEnchant.EE_TOHIT, sub(EffectSubTypeEnum.EST_ENCHANT, "TOHIT").getEnchantWrapper());
        assertEquals(EffectEnchant.EE_TODAM, sub(EffectSubTypeEnum.EST_ENCHANT, "TODAM").getEnchantWrapper());
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
        assertEquals(EffectEnchant.EE_NONE, sub(EffectSubTypeEnum.EST_ENCHANT, "").getEnchantWrapper());
        assertEquals(EffectEnchant.EE_TOAC, sub(EffectSubTypeEnum.EST_ENCHANT, "TOAC").getEnchantWrapper());
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
}
