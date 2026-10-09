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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.BlowEffectReader;
import uk.co.jackoftradesltd.backend.parser.ProjectionReader;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link BlowEffect}, the Java port of {@code struct blow_effect} in {@code mon-blows.h}.
 *
 * <p>The class has no behaviour beyond storing what the {@code parse_eff_*} handlers in
 * {@code mon-init.c} leave in the struct. The first group pins the constructor and getters,
 * including the {@code null} and sentinel values that stand in for C's zeroed defaults. The second
 * group loads the shipped {@code blow_effects.txt} and checks every one of its thirty records
 * against a table typed in from that file, so the expected values come from the C data rather than
 * from the Java assembler.
 *
 * <p>Class BlowEffectTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class BlowEffectTest {

    private static final String REAL_FILE = "lib/gamedata/blow_effects.txt";
    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";

    private static Object savedProjections;

    /**
     * The assembler resolves {@code lash-type:} through {@link WorldRegistry#lookupProjectionByLash},
     * so the projection table has to be loaded before the real file can be assembled.
     *
     * <p>Function seed coded on 261009, commented in full on 261009.
     */
    @BeforeAll
    static void seed() throws Exception {
        List<Projection> projections = new ProjectionReader().parseWithResults(PROJECTION_FILE).items();
        savedProjections = setProjections(projections);
    }

    /**
     * Put the real projection table back so the suite's shared statics are as they were found.
     *
     * <p>Function restore coded on 261009, commented in full on 261009.
     */
    @AfterAll
    static void restore() throws Exception {
        setProjections(savedProjections);
    }

    private static Object setProjections(Object value) throws Exception {
        Field f = WorldRegistry.class.getDeclaredField("projections");
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static List<BlowEffect> realFile() throws IOException {
        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(REAL_FILE);
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        return result.items();
    }

    private static Projection projection(ProjectionEnum which) {
        Projection p = WorldRegistry.lookupProjectionByLash(which);
        assertNotNull(p, "projection.txt should define " + which);
        return p;
    }

    // ---- Constructor and getters -----------------------------------------

    @Test
    @DisplayName("the constructor stores every field and each getter returns it unchanged")
    void constructorStoresEveryField() {
        // POISON, as blow_effects.txt spells it.
        Projection lash = projection(ProjectionEnum.PROJ_POIS);
        BlowEffect e = new BlowEffect("POISON", 20, 10, "poison", ColourEnum.COLOUR_ORANGE,
                ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_DARK, BlowEffectType.BET_ELEMENT,
                ObjectFlag.OF_NONE, ElementEnum.ELEM_POIS, lash);

        assertEquals("POISON", e.getName());
        assertEquals(20, e.getPower());
        assertEquals(10, e.getEval());
        assertEquals("poison", e.getDesc());
        assertEquals(ColourEnum.COLOUR_ORANGE, e.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, e.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_DARK, e.getLoreAttrImmune());
        assertEquals(BlowEffectType.BET_ELEMENT, e.getEffectType());
        assertEquals(ObjectFlag.OF_NONE, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_POIS, e.getElementEnumResist());
        assertSame(lash, e.getLashType());
    }

    @Test
    @DisplayName("null and sentinel values pass through untouched, as for the NONE record")
    void absentValuesPassThrough() {
        // NONE names only power, eval and lore-color-base. C leaves the rest zeroed; the port
        // models those as null (colours, effect type, lash) or the X_NONE sentinels. The class
        // itself stores whatever it is given, so null is legal for desc here even though the
        // assembler supplies "" (see realFileScalarFieldsMatchTheCData).
        BlowEffect e = new BlowEffect("NONE", 0, 0, null, ColourEnum.COLOUR_DARK, null, null, null,
                ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, null);

        assertEquals("NONE", e.getName());
        assertEquals(0, e.getPower());
        assertEquals(0, e.getEval());
        assertNull(e.getDesc());
        assertEquals(ColourEnum.COLOUR_DARK, e.getLoreAttr());
        assertNull(e.getLoreAttrResist());
        assertNull(e.getLoreAttrImmune());
        assertNull(e.getEffectType());
        assertEquals(ObjectFlag.OF_NONE, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
        assertNull(e.getLashType());
    }

    @Test
    @DisplayName("a flag-type effect carries the flag and leaves the element on its sentinel")
    void flagTypeLeavesElementOnSentinel() {
        // PARALYZE: effect-type:flag, resist:FREE_ACT.
        BlowEffect e = new BlowEffect("PARALYZE", 0, 40, "paralyze", ColourEnum.COLOUR_LIGHT_RED,
                ColourEnum.COLOUR_LIGHT_GREEN, null, BlowEffectType.BET_FLAG, ObjectFlag.OF_FREE_ACT,
                ElementEnum.ELEM_NONE, null);

        assertEquals(BlowEffectType.BET_FLAG, e.getEffectType());
        assertEquals(ObjectFlag.OF_FREE_ACT, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
    }

    @Test
    @DisplayName("power and eval keep their sign and magnitude")
    void numericFieldsAreNotClamped() {
        // SHATTER has the file's largest values (power 60, eval 300); nothing in C bounds them.
        BlowEffect big = new BlowEffect("SHATTER", 60, 300, "shatter", ColourEnum.COLOUR_YELLOW,
                null, null, null, ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, null);
        assertEquals(60, big.getPower());
        assertEquals(300, big.getEval());

        BlowEffect negative = new BlowEffect("X", -5, -7, null, null, null, null, null,
                ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, null);
        assertEquals(-5, negative.getPower());
        assertEquals(-7, negative.getEval());
    }

    // ---- The shipped blow_effects.txt ------------------------------------

    /**
     * One expected record, typed in from {@code blow_effects.txt}. Absent directives are
     * {@code null}; {@code resist} is the raw {@code resist:} token.
     */
    private record Expected(String name, int power, int eval, String desc, ColourEnum base,
                            ColourEnum resistColour, ColourEnum immuneColour, BlowEffectType type,
                            String resist, ProjectionEnum lash) {
    }

    private static final ColourEnum LGREEN = ColourEnum.COLOUR_LIGHT_GREEN;
    private static final ColourEnum LRED = ColourEnum.COLOUR_LIGHT_RED;
    private static final ColourEnum ORANGE = ColourEnum.COLOUR_ORANGE;
    private static final ColourEnum YELLOW = ColourEnum.COLOUR_YELLOW;
    private static final ProjectionEnum MISSILE = ProjectionEnum.PROJ_MISSILE;

    /**
     * Every record of {@code blow_effects.txt}, in file order. SHATTER names {@code lash-type:SHARDS},
     * which is not a projection name (the code is {@code SHARD}), so C's
     * {@code parse_eff_lash_type} falls back to {@code PROJ_MISSILE}.
     */
    private static final List<Expected> EXPECTED = List.of(
            new Expected("NONE", 0, 0, null, ColourEnum.COLOUR_DARK, null, null, null, null, null),
            new Expected("HURT", 40, 0, "attack", LGREEN, null, null, null, null, MISSILE),
            new Expected("POISON", 20, 10, "poison", ORANGE, LGREEN, null,
                    BlowEffectType.BET_ELEMENT, "POIS", ProjectionEnum.PROJ_POIS),
            new Expected("DISENCHANT", 10, 30, "disenchant", LRED, LGREEN, null,
                    BlowEffectType.BET_ELEMENT, "DISEN", ProjectionEnum.PROJ_DISEN),
            new Expected("DRAIN_CHARGES", 10, 30, "drain charges", LRED, LGREEN, null,
                    BlowEffectType.BET_DRAIN, null, ProjectionEnum.PROJ_DISEN),
            new Expected("EAT_GOLD", 0, 5, "steal gold", YELLOW, LGREEN, null,
                    BlowEffectType.BET_THEFT, null, MISSILE),
            new Expected("EAT_ITEM", 0, 5, "steal items", LRED, LGREEN, null,
                    BlowEffectType.BET_THEFT, null, MISSILE),
            new Expected("EAT_FOOD", 0, 5, "eat your food", YELLOW, LGREEN, null,
                    BlowEffectType.BET_EAT_FOOD, null, MISSILE),
            new Expected("EAT_LIGHT", 0, 5, "absorb light", YELLOW, LGREEN, null,
                    BlowEffectType.BET_EAT_LIGHT, null, MISSILE),
            new Expected("ACID", 20, 20, "shoot acid", ORANGE, YELLOW, LGREEN, null, null,
                    ProjectionEnum.PROJ_ACID),
            new Expected("ELEC", 40, 10, "electrify", ORANGE, YELLOW, LGREEN, null, null,
                    ProjectionEnum.PROJ_ELEC),
            new Expected("FIRE", 40, 10, "burn", ORANGE, YELLOW, LGREEN, null, null,
                    ProjectionEnum.PROJ_FIRE),
            new Expected("COLD", 40, 10, "freeze", ORANGE, YELLOW, LGREEN, null, null,
                    ProjectionEnum.PROJ_ICE),
            new Expected("BLIND", 0, 20, "blind", YELLOW, LGREEN, null,
                    BlowEffectType.BET_FLAG, "PROT_BLIND", ProjectionEnum.PROJ_DARK),
            new Expected("CONFUSE", 20, 20, "confuse", ORANGE, LGREEN, null,
                    BlowEffectType.BET_FLAG, "PROT_CONF", ProjectionEnum.PROJ_CHAOS),
            new Expected("TERRIFY", 0, 10, "terrify", YELLOW, LGREEN, null,
                    BlowEffectType.BET_FLAG, "PROT_FEAR", ProjectionEnum.PROJ_CHAOS),
            new Expected("PARALYZE", 0, 40, "paralyze", LRED, LGREEN, null,
                    BlowEffectType.BET_FLAG, "FREE_ACT", ProjectionEnum.PROJ_INERTIA),
            new Expected("LOSE_STR", 0, 20, "reduce strength", ORANGE, LGREEN, null,
                    BlowEffectType.BET_FLAG, "SUST_STR", ProjectionEnum.PROJ_TIME),
            new Expected("LOSE_INT", 0, 20, "reduce intelligence", ORANGE, LGREEN, null,
                    BlowEffectType.BET_FLAG, "SUST_INT", ProjectionEnum.PROJ_TIME),
            new Expected("LOSE_WIS", 0, 20, "reduce wisdom", ORANGE, LGREEN, null,
                    BlowEffectType.BET_FLAG, "SUST_WIS", ProjectionEnum.PROJ_TIME),
            new Expected("LOSE_DEX", 0, 20, "reduce dexterity", ORANGE, LGREEN, null,
                    BlowEffectType.BET_FLAG, "SUST_DEX", ProjectionEnum.PROJ_TIME),
            new Expected("LOSE_CON", 0, 30, "reduce constitution", ORANGE, LGREEN, null,
                    BlowEffectType.BET_FLAG, "SUST_CON", ProjectionEnum.PROJ_TIME),
            new Expected("LOSE_ALL", 0, 40, "reduce all stats", LRED, LGREEN, null,
                    BlowEffectType.BET_ALL_SUSTAINS, null, ProjectionEnum.PROJ_TIME),
            new Expected("SHATTER", 60, 300, "shatter", YELLOW, null, null, null, null, MISSILE),
            new Expected("EXP_10", 20, 5, "lower experience", ORANGE, YELLOW, null,
                    BlowEffectType.BET_FLAG, "HOLD_LIFE", ProjectionEnum.PROJ_NETHER),
            new Expected("EXP_20", 20, 5, "lower experience", ORANGE, YELLOW, null,
                    BlowEffectType.BET_FLAG, "HOLD_LIFE", ProjectionEnum.PROJ_NETHER),
            new Expected("EXP_40", 20, 10, "lower experience", ORANGE, YELLOW, null,
                    BlowEffectType.BET_FLAG, "HOLD_LIFE", ProjectionEnum.PROJ_NETHER),
            new Expected("EXP_80", 20, 10, "lower experience", ORANGE, YELLOW, null,
                    BlowEffectType.BET_FLAG, "HOLD_LIFE", ProjectionEnum.PROJ_NETHER),
            new Expected("HALLU", 0, 20, "cause hallucinations", YELLOW, LGREEN, null,
                    BlowEffectType.BET_ELEMENT, "CHAOS", ProjectionEnum.PROJ_CHAOS),
            new Expected("BLACK_BREATH", 0, 10, "inflict Black Breath", LRED, null, null, null, null,
                    ProjectionEnum.PROJ_TIME));

    @Test
    @DisplayName("the real file loads the thirty records in file order")
    void realFileHasThirtyRecordsInOrder() throws IOException {
        List<BlowEffect> effects = realFile();

        assertEquals(EXPECTED.size(), effects.size());
        for (int i = 0; i < EXPECTED.size(); i++) {
            assertEquals(EXPECTED.get(i).name(), effects.get(i).getName(), "record " + i);
        }
    }

    @Test
    @DisplayName("every record's scalar fields, colours and effect type match blow_effects.txt")
    void realFileScalarFieldsMatchTheCData() throws IOException {
        List<BlowEffect> effects = realFile();

        for (int i = 0; i < EXPECTED.size(); i++) {
            Expected want = EXPECTED.get(i);
            BlowEffect got = effects.get(i);
            String at = want.name() + ": ";
            assertEquals(want.power(), got.getPower(), at + "power");
            assertEquals(want.eval(), got.getEval(), at + "eval");
            // C holds NULL for an absent desc (blowe.c: null(e->desc)); the assembler yields "".
            assertEquals(want.desc() == null ? "" : want.desc(), got.getDesc(), at + "desc");
            assertEquals(want.base(), got.getLoreAttr(), at + "lore-color-base");
            assertEquals(want.resistColour(), got.getLoreAttrResist(), at + "lore-color-resist");
            assertEquals(want.immuneColour(), got.getLoreAttrImmune(), at + "lore-color-immune");
            assertEquals(want.type(), got.getEffectType(), at + "effect-type");
        }
    }

    @Test
    @DisplayName("resist: lands in the element field for element effects and the flag field for flag effects")
    void realFileResistSplitMatchesTheEffectType() throws IOException {
        List<BlowEffect> effects = realFile();

        for (int i = 0; i < EXPECTED.size(); i++) {
            Expected want = EXPECTED.get(i);
            BlowEffect got = effects.get(i);
            ElementEnum wantElement = want.type() == BlowEffectType.BET_ELEMENT
                    ? ElementEnum.valueOf("ELEM_" + want.resist()) : ElementEnum.ELEM_NONE;
            ObjectFlag wantFlag = want.type() == BlowEffectType.BET_FLAG
                    ? ObjectFlag.valueOf("OF_" + want.resist()) : ObjectFlag.OF_NONE;
            assertEquals(wantElement, got.getElementEnumResist(), want.name() + ": element resist");
            assertEquals(wantFlag, got.getObjectFlagResist(), want.name() + ": flag resist");
        }
    }

    @Test
    @DisplayName("lash-type: resolves to the named projection, and an unknown name to PROJ_MISSILE")
    void realFileLashTypesMatchTheCData() throws IOException {
        List<BlowEffect> effects = realFile();

        for (int i = 0; i < EXPECTED.size(); i++) {
            Expected want = EXPECTED.get(i);
            Projection got = effects.get(i).getLashType();
            if (want.lash() == null) {
                // Only NONE ships no lash-type. C holds index 0 there; the port holds null.
                assertNull(got, want.name() + ": no lash-type");
            } else {
                assertNotNull(got, want.name() + ": lash");
                assertEquals(want.lash(), got.getProjection(), want.name() + ": lash");
            }
        }
    }

    @Test
    @DisplayName("SHATTER's lash-type:SHARDS is not a projection name, so C falls back to MISSILE")
    void shatterLashFallsBackToMissile() throws IOException {
        BlowEffect shatter = realFile().stream().filter(e -> e.getName().equals("SHATTER"))
                .findFirst().orElseThrow();

        assertEquals(MISSILE, shatter.getLashType().getProjection());
    }

    @Test
    @DisplayName("an effect with no resist colours has both as null, never COLOUR_DARK")
    void unsetResistColoursAreNullNotDark() throws IOException {
        // C stores 0 (COLOUR_DARK) and blow_color() in mon-lore.c reads a zero resist and immune
        // pair as "no protected colours". The port's null must stay distinct from an explicit Dark.
        Map<String, BlowEffect> byName = new java.util.HashMap<>();
        for (BlowEffect e : realFile()) {
            byName.put(e.getName(), e);
        }

        for (String name : List.of("NONE", "HURT", "SHATTER", "BLACK_BREATH")) {
            assertNull(byName.get(name).getLoreAttrResist(), name + ": resist colour");
            assertNull(byName.get(name).getLoreAttrImmune(), name + ": immune colour");
        }
        assertEquals(ColourEnum.COLOUR_DARK, byName.get("NONE").getLoreAttr(),
                "NONE names lore-color-base:Dark explicitly");
    }

    @Test
    @DisplayName("only ACID, ELEC, FIRE and COLD carry an immune colour")
    void onlyTheFourRawElementsHaveAnImmuneColour() throws IOException {
        List<String> withImmune = realFile().stream()
                .filter(e -> e.getLoreAttrImmune() != null).map(BlowEffect::getName).toList();

        assertEquals(List.of("ACID", "ELEC", "FIRE", "COLD"), withImmune);
    }
}
