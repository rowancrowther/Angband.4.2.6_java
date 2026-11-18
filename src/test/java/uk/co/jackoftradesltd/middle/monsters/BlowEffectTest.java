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
 * {@code mon-init.c} leave in the struct. The first group pins the full constructor and getters,
 * including the {@code null} and sentinel values that stand in for C's zeroed defaults. The second
 * group pins the no-argument constructor, which is C's {@code mem_zalloc} state (including the ACID
 * lash that C's zeroed {@code lash_type} 0 names), and the setters. The third group loads the
 * shipped {@code blow_effects.txt} and checks every one of its thirty records against a table typed
 * in from that file, so the expected values come from the C data rather than from the Java
 * assembler.
 *
 * <p>Class BlowEffectTest coded on 261009, commented in full on 261118.
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
                ObjectFlag.OF_NONE, ElementEnum.ELEM_POIS, ProjectionEnum.PROJ_POIS, lash);

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
        assertEquals(ProjectionEnum.PROJ_POIS, e.getProjectionResist());
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
                ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, ProjectionEnum.PROJ_NONE, null);

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
        assertEquals(ProjectionEnum.PROJ_NONE, e.getProjectionResist());
        assertNull(e.getLashType());
    }

    @Test
    @DisplayName("a flag-type effect carries the flag and leaves the element on its sentinel")
    void flagTypeLeavesElementOnSentinel() {
        // PARALYZE: effect-type:flag, resist:FREE_ACT.
        BlowEffect e = new BlowEffect("PARALYZE", 0, 40, "paralyze", ColourEnum.COLOUR_LIGHT_RED,
                ColourEnum.COLOUR_LIGHT_GREEN, null, BlowEffectType.BET_FLAG, ObjectFlag.OF_FREE_ACT,
                ElementEnum.ELEM_NONE, ProjectionEnum.PROJ_NONE, null);

        assertEquals(BlowEffectType.BET_FLAG, e.getEffectType());
        assertEquals(ObjectFlag.OF_FREE_ACT, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
    }

    @Test
    @DisplayName("power and eval keep their sign and magnitude")
    void numericFieldsAreNotClamped() {
        // SHATTER has the file's largest values (power 60, eval 300); nothing in C bounds them.
        BlowEffect big = new BlowEffect("SHATTER", 60, 300, "shatter", ColourEnum.COLOUR_YELLOW,
                null, null, null, ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, ProjectionEnum.PROJ_NONE, null);
        assertEquals(60, big.getPower());
        assertEquals(300, big.getEval());

        BlowEffect negative = new BlowEffect("X", -5, -7, null, null, null, null, null,
                ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, ProjectionEnum.PROJ_NONE, null);
        assertEquals(-5, negative.getPower());
        assertEquals(-7, negative.getEval());
    }

    // ---- The no-argument constructor and the setters ----------------------

    /**
     * C's {@code parse_eff_name} makes each record with {@code mem_zalloc}, so every number is 0,
     * every pointer is NULL and every colour is 0, which is {@code COLOUR_DARK}. The port holds the
     * empty string for the two NULL strings and its {@code X_NONE} values for the zeroed resist.
     *
     * <p>Function noArgumentConstructorHoldsCsZeroedStruct coded on 261118, commented in full on
     * 261118.
     */
    @Test
    @DisplayName("new BlowEffect() holds the state C's mem_zalloc gives a new record")
    void noArgumentConstructorHoldsCsZeroedStruct() {
        BlowEffect e = new BlowEffect();

        assertEquals("", e.getName(), "C: NULL name");
        assertEquals(0, e.getPower());
        assertEquals(0, e.getEval());
        assertEquals("", e.getDesc(), "C: NULL desc");
        assertEquals(ColourEnum.COLOUR_DARK, e.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_DARK, e.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_DARK, e.getLoreAttrImmune());
        assertNull(e.getEffectType(), "C: NULL effect_type");
        assertEquals(ObjectFlag.OF_NONE, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, e.getProjectionResist());
    }

    /**
     * C's zeroed {@code lash_type} is 0, the first entry of {@code proj_name_list} and the first
     * record of {@code projection.txt}, which is {@code code:ACID}. The effect-handler and
     * {@code mon-spell.c} read it as {@code projections[0]}, so the default lash has to be that
     * same first record, not just any projection with the ACID code.
     *
     * <p>Function noArgumentConstructorLashIsProjectionZeroWhichIsAcid coded on 261118, commented
     * in full on 261118.
     */
    @Test
    @DisplayName("new BlowEffect() holds projections[0], which is ACID, as C's zeroed lash_type does")
    void noArgumentConstructorLashIsProjectionZeroWhichIsAcid() {
        Projection first = WorldRegistry.getProjections().get(0);

        assertEquals(ProjectionEnum.PROJ_ACID, first.getProjection(),
                "projection.txt lists acid first, so C's index 0 is ACID");
        assertSame(first, new BlowEffect().getLashType());
    }

    /**
     * Two default effects are two objects, and they share the registry's one ACID record rather
     * than each taking a copy, as two C records would both index {@code projections[0]}.
     *
     * <p>Function twoDefaultsAreIndependentButShareTheLashRecord coded on 261118, commented in
     * full on 261118.
     */
    @Test
    @DisplayName("two default effects are independent but share the one ACID projection record")
    void twoDefaultsAreIndependentButShareTheLashRecord() {
        BlowEffect first = new BlowEffect();
        BlowEffect second = new BlowEffect();

        first.setPower(5);
        first.setName("A");

        assertEquals(0, second.getPower());
        assertEquals("", second.getName());
        assertSame(first.getLashType(), second.getLashType());
    }

    /**
     * The default lash is a registry lookup, which C does not need because it stores only the
     * index. With the table empty the constructor fails at once with an
     * {@link IllegalStateException}; this is the accepted deviation recorded in
     * {@code docs/Accepted_Deviations.md}. The table is put back afterwards.
     *
     * <p>Function noArgumentConstructorNeedsTheProjectionsLoaded coded on 261118, commented in
     * full on 261118.
     */
    @Test
    @DisplayName("new BlowEffect() with no projections loaded is an IllegalStateException")
    void noArgumentConstructorNeedsTheProjectionsLoaded() throws Exception {
        Object saved = setProjections(null);
        try {
            assertThrows(IllegalStateException.class, BlowEffect::new);
        } finally {
            setProjections(saved);
        }
    }

    /**
     * The full constructor stores what it is given and never reads the registry, so it still works
     * with no projections loaded, and a {@code null} lash stays {@code null}.
     *
     * <p>Function fullConstructorNeedsNoRegistry coded on 261118, commented in full on 261118.
     */
    @Test
    @DisplayName("the full constructor does not touch the registry")
    void fullConstructorNeedsNoRegistry() throws Exception {
        Object saved = setProjections(null);
        try {
            BlowEffect e = new BlowEffect("X", 1, 2, "x", null, null, null, null,
                    ObjectFlag.OF_NONE, ElementEnum.ELEM_NONE, ProjectionEnum.PROJ_NONE, null);

            assertEquals("X", e.getName());
            assertNull(e.getLashType());
            assertNull(e.getLoreAttr());
        } finally {
            setProjections(saved);
        }
    }

    /**
     * Each setter replaces its own field and no other. Every field is changed from its default to
     * a distinct value, and all twelve are read back afterwards, so a setter that wrote the wrong
     * field, or two, shows up.
     *
     * <p>Function eachSetterReplacesItsOwnFieldOnly coded on 261118, commented in full on 261118.
     */
    @Test
    @DisplayName("each setter replaces its own field and no other")
    void eachSetterReplacesItsOwnFieldOnly() {
        Projection poison = projection(ProjectionEnum.PROJ_POIS);
        BlowEffect e = new BlowEffect();

        e.setName("POISON");
        e.setPower(20);
        e.setEval(10);
        e.setDesc("poison");
        e.setLoreAttr(ColourEnum.COLOUR_ORANGE);
        e.setLoreAttrResist(ColourEnum.COLOUR_LIGHT_GREEN);
        e.setLoreAttrImmune(ColourEnum.COLOUR_YELLOW);
        e.setEffectType(BlowEffectType.BET_ELEMENT);
        e.setObjectFlagResist(ObjectFlag.OF_FREE_ACT);
        e.setElementEnumResist(ElementEnum.ELEM_POIS);
        e.setProjectionResist(ProjectionEnum.PROJ_POIS);
        e.setLashType(poison);

        assertEquals("POISON", e.getName());
        assertEquals(20, e.getPower());
        assertEquals(10, e.getEval());
        assertEquals("poison", e.getDesc());
        assertEquals(ColourEnum.COLOUR_ORANGE, e.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, e.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_YELLOW, e.getLoreAttrImmune());
        assertEquals(BlowEffectType.BET_ELEMENT, e.getEffectType());
        assertEquals(ObjectFlag.OF_FREE_ACT, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_POIS, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_POIS, e.getProjectionResist());
        assertSame(poison, e.getLashType());
    }

    /**
     * C's {@code parse_eff_desc} appends with {@code string_append}, so two {@code desc:} lines
     * make one longer description. The setter only replaces; the appending is the caller's job, and
     * {@code BlowEffect2Assembler} does it by passing the old text plus the new. A second call
     * with a shorter text therefore leaves only the second text.
     *
     * <p>Function setDescReplacesWhereCAppends coded on 261118, commented in full on 261118.
     */
    @Test
    @DisplayName("setDesc replaces; the appending C does is the caller's")
    void setDescReplacesWhereCAppends() {
        BlowEffect e = new BlowEffect();

        e.setDesc("reduce ");
        e.setDesc(e.getDesc() + "strength");
        assertEquals("reduce strength", e.getDesc());

        e.setDesc("x");
        assertEquals("x", e.getDesc());
    }

    /**
     * C's {@code parse_eff_effect_type} replaces only the type string, so the one {@code resist}
     * int survives a change of type. The setter does the same for the three resist fields: it
     * leaves them as they are, and any clearing is the caller's decision (accepted deviation, see
     * {@code docs/Accepted_Deviations.md}, "Changing effect-type: clears the resist").
     *
     * <p>Function setEffectTypeLeavesTheResistFieldsAlone coded on 261118, commented in full on
     * 261118.
     */
    @Test
    @DisplayName("setEffectType leaves the resist fields alone")
    void setEffectTypeLeavesTheResistFieldsAlone() {
        BlowEffect e = new BlowEffect();
        e.setEffectType(BlowEffectType.BET_ELEMENT);
        e.setElementEnumResist(ElementEnum.ELEM_FIRE);
        e.setProjectionResist(ProjectionEnum.PROJ_FIRE);
        e.setObjectFlagResist(ObjectFlag.OF_FREE_ACT);

        e.setEffectType(BlowEffectType.BET_FLAG);

        assertEquals(BlowEffectType.BET_FLAG, e.getEffectType());
        assertEquals(ElementEnum.ELEM_FIRE, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_FIRE, e.getProjectionResist());
        assertEquals(ObjectFlag.OF_FREE_ACT, e.getObjectFlagResist());
    }

    /**
     * A setter given {@code null} stores it, as the full constructor does, so a caller can clear
     * the effect type and the lash again.
     *
     * <p>Function settersStoreNullWhereTheFieldIsNullable coded on 261118, commented in full on
     * 261118.
     */
    @Test
    @DisplayName("setEffectType and setLashType accept null")
    void settersStoreNullWhereTheFieldIsNullable() {
        BlowEffect e = new BlowEffect();
        e.setEffectType(BlowEffectType.BET_THEFT);

        e.setEffectType(null);
        e.setLashType(null);

        assertNull(e.getEffectType());
        assertNull(e.getLashType());
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
            // NONE ships no lash-type:, so C's zeroed lash_type is 0, which is PROJ_ACID (the first
            // entry of proj_name_list). The BlowEffect() default looks that projection up, so the
            // expected lash is PROJ_ACID.
            new Expected("NONE", 0, 0, null, ColourEnum.COLOUR_DARK, null, null, null, null,
                    ProjectionEnum.PROJ_ACID),
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
            // C's zeroed lore_attr_resist and lore_attr_immune are 0, which is COLOUR_DARK.
            assertEquals(want.resistColour() == null ? ColourEnum.COLOUR_DARK : want.resistColour(),
                    got.getLoreAttrResist(), at + "lore-color-resist");
            assertEquals(want.immuneColour() == null ? ColourEnum.COLOUR_DARK : want.immuneColour(),
                    got.getLoreAttrImmune(), at + "lore-color-immune");
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
            // C's single resist int starts at 0, which reads as ELEM_ACID in the element slot
            // and OF_NONE in the flag slot. The port holds ELEM_NONE and PROJ_NONE for "no resist"
            // in the element and projection slots (the accepted X_NONE register).
            ElementEnum wantElement = want.type() == BlowEffectType.BET_ELEMENT
                    ? ElementEnum.valueOf("ELEM_" + want.resist()) : ElementEnum.ELEM_NONE;
            ProjectionEnum wantProjection = want.type() == BlowEffectType.BET_ELEMENT
                    ? ProjectionEnum.valueOf("PROJ_" + want.resist()) : ProjectionEnum.PROJ_NONE;
            ObjectFlag wantFlag = want.type() == BlowEffectType.BET_FLAG
                    ? ObjectFlag.valueOf("OF_" + want.resist()) : ObjectFlag.OF_NONE;
            assertEquals(wantElement, got.getElementEnumResist(), want.name() + ": element resist");
            assertEquals(wantProjection, got.getProjectionResist(), want.name() + ": projection resist");
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
            assertNotNull(got, want.name() + ": lash");
            assertEquals(want.lash(), got.getProjection(), want.name() + ": lash");
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
    @DisplayName("an effect with no resist or immune colour holds COLOUR_DARK, as C's zeroed fields do")
    void unsetResistColoursAreDarkAsInC() throws IOException {
        // C stores 0 (COLOUR_DARK) and blow_color() in mon-lore.c reads a zero resist and immune
        // pair as "no protected colours". The BlowEffect() constructor now holds the same zero.
        Map<String, BlowEffect> byName = new java.util.HashMap<>();
        for (BlowEffect e : realFile()) {
            byName.put(e.getName(), e);
        }

        for (String name : List.of("NONE", "HURT", "SHATTER", "BLACK_BREATH")) {
            assertEquals(ColourEnum.COLOUR_DARK, byName.get(name).getLoreAttrResist(), name + ": resist colour");
            assertEquals(ColourEnum.COLOUR_DARK, byName.get(name).getLoreAttrImmune(), name + ": immune colour");
        }
        assertEquals(ColourEnum.COLOUR_DARK, byName.get("NONE").getLoreAttr(),
                "NONE names lore-color-base:Dark explicitly");
    }

    @Test
    @DisplayName("only ACID, ELEC, FIRE and COLD carry an immune colour")
    void onlyTheFourRawElementsHaveAnImmuneColour() throws IOException {
        List<String> withImmune = realFile().stream()
                .filter(e -> e.getLoreAttrImmune() != ColourEnum.COLOUR_DARK).map(BlowEffect::getName).toList();

        assertEquals(List.of("ACID", "ELEC", "FIRE", "COLD"), withImmune);
    }
}
