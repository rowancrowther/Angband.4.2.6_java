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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftradesltd.middle.effect.Expression;
import uk.co.jackoftradesltd.middle.enums.EffectBaseType;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification of {@link EffectAssembler#assemble} against {@code grab_effect_data} ({@code init.c}),
 * the {@code parse_*_effect_yx}, {@code parse_*_dice}, {@code parse_*_expr} and
 * {@code parse_*_effect_msg} handlers that sit beside it, and the grouping in {@code effect_subtype}
 * ({@code effects.c}). {@code EffectAssemblerSubTypeTest} covers the sub-type lists themselves; this
 * class covers the rest of the effect: which kind each effect takes, the numbers, the message, the
 * dice text, the expression split, and the error text. Expected values come from the C source, not
 * from the Java.
 *
 * <p>Class EffectAssemblerFieldsTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class EffectAssemblerFieldsTest {

    // The effect_subtype switch in effects.c, case label by case label.
    private static final Set<String> PROJECTION_EFFECTS = Set.of("PROJECT_LOS", "PROJECT_LOS_AWARE",
            "DESTRUCTION", "SPOT", "SPHERE", "BALL", "BREATH", "ARC", "SHORT_BEAM", "LASH", "SWARM",
            "STRIKE", "STAR", "STAR_BALL", "BOLT", "BEAM", "BOLT_OR_BEAM", "LINE", "ALTER",
            "BOLT_STATUS", "BOLT_STATUS_DAM", "BOLT_AWARE", "MELEE_BLOWS", "TOUCH", "TOUCH_AWARE");
    private static final Set<String> TIMED_EFFECTS = Set.of("CURE", "TIMED_SET", "TIMED_INC",
            "TIMED_INC_NO_RES", "TIMED_DEC");
    private static final Set<String> STAT_EFFECTS = Set.of("RESTORE_STAT", "DRAIN_STAT",
            "LOSE_RANDOM_STAT", "GAIN_STAT");

    private static Object field(Object owner, String name) throws Exception {
        Field f = owner.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(owner);
    }

    private static EffectParseRecord record(String type, String sub, String radius, String other,
                                            String dice, String y, String x, String chars,
                                            String bases, String ops, String msg, int line) {
        return new EffectParseRecord(type, sub, radius, other, dice, y, x, chars, bases, ops, msg, line);
    }

    private static Effect one(EffectParseRecord r) {
        return EffectAssembler.assemble(List.of(r), new ArrayList<>(), "test.txt").get(0);
    }

    private static Effect plain(String type, String sub) {
        return one(record(type, sub, "", "", "", "", "", "", "", "", null, 1));
    }

    private static Effect withNumbers(String radius, String other, String y, String x) {
        return one(record("DAMAGE", "", radius, other, "", y, x, "", "", "", null, 1));
    }

    private static DataFileParseException refusedWith(EffectParseRecord r) {
        return assertThrows(DataFileParseException.class,
                () -> EffectAssembler.assemble(List.of(r), new ArrayList<>(), "test.txt"));
    }

    // ---- which sub-type kind each effect takes (the effect_subtype switch) ----

    @Test
    void everyEffectTakesTheKindTheSwitchGivesIt() {
        for (EffectEnum e : EffectEnum.values()) {
            String name = e.name().substring(3);
            EffectSubTypeEnum expected;
            if (PROJECTION_EFFECTS.contains(name)) expected = EffectSubTypeEnum.EST_PROJ;
            else if (TIMED_EFFECTS.contains(name)) expected = EffectSubTypeEnum.EST_TMD;
            else if (STAT_EFFECTS.contains(name)) expected = EffectSubTypeEnum.EST_STAT;
            else if (name.equals("NOURISH")) expected = EffectSubTypeEnum.EST_NOURISH;
            else if (name.equals("MON_TIMED_INC")) expected = EffectSubTypeEnum.EST_MON_TMD;
            else if (name.equals("SUMMON")) expected = EffectSubTypeEnum.EST_SUMMON;
            else if (name.equals("ENCHANT")) expected = EffectSubTypeEnum.EST_ENCHANT;
            else if (name.equals("SHAPECHANGE")) expected = EffectSubTypeEnum.EST_SHAPECHANGE;
            else if (name.equals("EARTHQUAKE")) expected = EffectSubTypeEnum.EST_EARTHQUAKE;
            else if (name.equals("GLYPH")) expected = EffectSubTypeEnum.EST_GLYPH;
            else if (name.equals("TELEPORT")) expected = EffectSubTypeEnum.EST_TELEPORT;
            else if (name.equals("TELEPORT_TO")) expected = EffectSubTypeEnum.EST_TELEPORT_TO;
            else expected = EffectSubTypeEnum.EST_NONE;
            assertEquals(expected, e.getSubType(), e.name());
        }
    }

    @Test
    void theDefaultKindAcceptsOnlyNoneOrNothing() {
        // the default branch of effect_subtype: streq(type, "NONE") gives 0, anything else -1
        for (EffectEnum e : EffectEnum.values()) {
            if (e.getSubType() != EffectSubTypeEnum.EST_NONE) continue;
            String name = e.name().substring(3);
            assertNotNull(plain(name, "NONE"), name);
            assertNotNull(plain(name, ""), name);
            assertThrows(DataFileParseException.class, () -> plain(name, "none"), name);
            assertThrows(DataFileParseException.class, () -> plain(name, "FIRE"), name);
        }
    }

    @Test
    void everyProjectionEffectReadsAProjectionName() throws Exception {
        for (String name : PROJECTION_EFFECTS) {
            EffectSubTypeWrapper w = (EffectSubTypeWrapper) field(plain(name, "COLD"), "value");
            assertEquals(ProjectionEnum.PROJ_COLD, w.getProjectionWrapper(), name);
            assertThrows(DataFileParseException.class, () -> plain(name, "NONE"), name);
        }
    }

    @Test
    void everyTimedAndStatEffectReadsItsOwnList() throws Exception {
        for (String name : TIMED_EFFECTS) {
            EffectSubTypeWrapper w = (EffectSubTypeWrapper) field(plain(name, "slow"), "value");
            assertEquals(TimedEffect.TMD_SLOW, w.getTimedWrapper(), name);
        }
        for (String name : STAT_EFFECTS) {
            assertNotNull(plain(name, "WIS"), name);
            assertThrows(DataFileParseException.class, () -> plain(name, "FIRE"), name);
        }
    }

    @Test
    void theStoredKindIsTheEffectsOwnKind() throws Exception {
        assertEquals(EffectEnum.EF_BOLT, field(plain("BOLT", "FIRE"), "index"));
        assertEquals(EffectSubTypeEnum.EST_PROJ, field(plain("BOLT", "FIRE"), "subType"));
        assertEquals(EffectSubTypeEnum.EST_NONE, field(plain("DAMAGE", ""), "subType"));
    }

    // ---- radius, other parameter, y and x (parser_getint reads them with strtol base 0) ----

    @Test
    void absentNumbersAreZero() throws Exception {
        Effect e = withNumbers("", "", "", "");
        assertEquals(0, field(e, "radius"));
        assertEquals(0, field(e, "otherParameter"));
        assertEquals(0, field(e, "y"));
        assertEquals(0, field(e, "x"));
    }

    @Test
    void numbersAreReadAsGiven() throws Exception {
        Effect e = withNumbers("3", "-7", "12", "-1");
        assertEquals(3, field(e, "radius"));
        assertEquals(-7, field(e, "otherParameter"));
        assertEquals(12, field(e, "y"));
        assertEquals(-1, field(e, "x"));
    }

    @Test
    void numbersAreReadInBaseZero() throws Exception {
        // strtol(tok, &z, 0): a leading 0 is octal, 0x is hexadecimal
        Effect e = withNumbers("010", "0x10", "-010", "0");
        assertEquals(8, field(e, "radius"));
        assertEquals(16, field(e, "otherParameter"));
        assertEquals(-8, field(e, "y"));
        assertEquals(0, field(e, "x"));
    }

    @Test
    void aNumberThatIsNotANumberStopsTheLoad() {
        for (String[] bad : new String[][]{{"abc", "", "", ""}, {"", "abc", "", ""},
                {"", "", "abc", ""}, {"", "", "", "abc"}}) {
            assertThrows(DataFileParseException.class,
                    () -> withNumbers(bad[0], bad[1], bad[2], bad[3]));
        }
    }

    @Test
    void aNumberBeyondIntIsClampedWhereCWraps() throws Exception {
        // accepted deviation (docs/Accepted_Deviations.md): C keeps the low 32 bits of the strtol
        // result, so 4294967296 would be 0 there; here ParseUtils clamps it to Integer.MAX_VALUE
        assertEquals(Integer.MAX_VALUE, field(withNumbers("4294967296", "", "", ""), "radius"));
        assertEquals(Integer.MIN_VALUE, field(withNumbers("", "-4294967296", "", ""), "otherParameter"));
    }

    // ---- message, dice text, expressions ----

    @Test
    void theMessageIsPassedThroughAndIsNullWhenAbsent() throws Exception {
        Effect with = one(record("DAMAGE", "", "", "", "", "", "", "", "", "", "You feel odd.", 1));
        assertEquals("You feel odd.", field(with, "msg"));
        assertNull(field(plain("DAMAGE", ""), "msg"));
    }

    @Test
    void theDiceIsKeptAsTextAndNotRolled() throws Exception {
        Effect e = one(record("DAMAGE", "", "", "", "2d6+$B", "", "", "B", "PLAYER_LEVEL", "* 2", null, 1));
        assertEquals("2d6+$B", field(e, "diceString"));
        assertNull(field(e, "dice"));
        assertEquals("", field(plain("DAMAGE", ""), "diceString"));
    }

    @Test
    void noExpressionLinesGiveAnEmptyList() throws Exception {
        assertEquals(List.of(), field(plain("DAMAGE", ""), "expression"));
    }

    @Test
    void severalExpressionsSplitInOrderAndKeepTheirOperationText() throws Exception {
        Effect e = one(record("DAMAGE", "", "", "", "$B+$S", "", "", "B^S", "PLAYER_HP^DUNGEON_LEVEL",
                "* 4^/ 2 + 1", null, 1));
        @SuppressWarnings("unchecked")
        List<Expression> xs = (List<Expression>) field(e, "expression");
        assertEquals(2, xs.size());
        assertEquals('B', field(xs.get(0), "codeLetter"));
        assertEquals(EffectBaseType.EFB_PLAYER_HP, field(xs.get(0), "baseType"));
        assertEquals("* 4", field(xs.get(0), "operations"));
        assertEquals('S', field(xs.get(1), "codeLetter"));
        assertEquals(EffectBaseType.EFB_DUNGEON_LEVEL, field(xs.get(1), "baseType"));
        assertEquals("/ 2 + 1", field(xs.get(1), "operations"));
    }

    @Test
    void everyBaseNameC_KnowsIsFound() throws Exception {
        // value_bases in effect_value_base_by_name
        String[] names = {"SPELL_POWER", "PLAYER_LEVEL", "DUNGEON_LEVEL", "MAX_SIGHT", "WEAPON_DAMAGE",
                "PLAYER_HP", "MONSTER_PERCENT_HP_GONE"};
        EffectBaseType[] types = {EffectBaseType.EFB_SPELL_POWER, EffectBaseType.EFB_PLAYER_LEVEL,
                EffectBaseType.EFB_DUNGEON_LEVEL, EffectBaseType.EFB_MAX_SIGHT,
                EffectBaseType.EFB_WEAPON_DAMAGE, EffectBaseType.EFB_PLAYER_HP,
                EffectBaseType.EFB_MONSTER_PERCENT_HP_GONE};
        for (int i = 0; i < names.length; i++) {
            Effect e = one(record("DAMAGE", "", "", "", "$B", "", "", "B", names[i], "+ 1", null, 1));
            @SuppressWarnings("unchecked")
            List<Expression> xs = (List<Expression>) field(e, "expression");
            assertEquals(types[i], field(xs.get(0), "baseType"), names[i]);
        }
    }

    @Test
    void differingNumbersOfExpressionPartsStopTheLoad() {
        DataFileParseException ex = refusedWith(record("DAMAGE", "", "", "", "$B+$S", "", "", "B^S",
                "PLAYER_HP", "* 4^/ 2", null, 4));
        assertTrue(ex.getMessage().contains("differing numbers of chars/bases & operation values"),
                ex.getMessage());
        refusedWith(record("DAMAGE", "", "", "", "$B+$S", "", "", "B^S", "PLAYER_HP^MAX_SIGHT",
                "* 4", null, 4));
    }

    @Test
    void anExpressionCodeOfMoreThanOneCharacterStopsTheLoad() {
        DataFileParseException ex = refusedWith(record("DAMAGE", "", "", "", "$B", "", "", "BB",
                "PLAYER_HP", "* 4", null, 4));
        assertTrue(ex.getMessage().contains("an illegal expression value: 'BB'"), ex.getMessage());
    }

    // ---- the failures: wording, line number, no partial result ----

    @Test
    void anUnknownEffectNameNamesTheLineAndTheFile() {
        DataFileParseException ex = refusedWith(record("NOTANEFFECT", "", "", "", "", "", "", "", "", "", null, 7));
        assertTrue(ex.getMessage().startsWith("Effect starting at line: 7 of file: test.txt has "),
                ex.getMessage());
        assertTrue(ex.getMessage().endsWith("an illegal element value: NOTANEFFECT"), ex.getMessage());
    }

    @Test
    void anEmptyEffectNameStopsTheLoad() {
        DataFileParseException ex = refusedWith(record("", "", "", "", "", "", "", "", "", "", null, 9));
        assertTrue(ex.getMessage().startsWith("Effect starting at line: 9 of file: test.txt has "),
                ex.getMessage());
    }

    @Test
    void aBadSubTypeNamesTheLine() {
        DataFileParseException ex = refusedWith(record("BOLT", "NOSUCH", "", "", "", "", "", "", "", "", null, 12));
        assertTrue(ex.getMessage().startsWith("Effect starting at line: 12 of file: test.txt has "),
                ex.getMessage());
        assertTrue(ex.getMessage().endsWith("an illegal projection value: NOSUCH"), ex.getMessage());
    }

    @Test
    void aBadNumberNamesTheLine() {
        DataFileParseException ex = refusedWith(record("DAMAGE", "", "x", "", "", "", "", "", "", "", null, 15));
        assertTrue(ex.getMessage().contains("line: 15 of file: test.txt"), ex.getMessage());
    }

    @Test
    void theFirstBadEffectStopsTheWholeBlock() {
        EffectParseRecord good = record("DAMAGE", "", "", "", "", "", "", "", "", "", null, 1);
        EffectParseRecord bad = record("NOTANEFFECT", "", "", "", "", "", "", "", "", "", null, 2);
        assertThrows(DataFileParseException.class,
                () -> EffectAssembler.assemble(List.of(good, bad, good), new ArrayList<>(), "test.txt"));
    }

    // ---- assemble itself ----

    @Test
    void anEmptyBlockGivesAnEmptyList() {
        assertEquals(List.of(), EffectAssembler.assemble(List.of(), new ArrayList<>(), "test.txt"));
    }

    @Test
    void effectsComeBackInTheOrderGiven() throws Exception {
        List<Effect> out = EffectAssembler.assemble(List.of(
                record("DAMAGE", "", "", "", "", "", "", "", "", "", null, 1),
                record("BOLT", "FIRE", "", "", "", "", "", "", "", "", null, 2),
                record("HEAL_HP", "", "", "", "", "", "", "", "", "", null, 3)), new ArrayList<>(), "test.txt");
        assertEquals(3, out.size());
        assertEquals(EffectEnum.EF_DAMAGE, field(out.get(0), "index"));
        assertEquals(EffectEnum.EF_BOLT, field(out.get(1), "index"));
        assertEquals(EffectEnum.EF_HEAL_HP, field(out.get(2), "index"));
    }

    @Test
    void theErrorsListIsNeverWrittenTo() {
        List<String> errors = new ArrayList<>();
        EffectAssembler.assemble(List.of(record("DAMAGE", "", "1", "2", "", "3", "4", "", "", "", null, 1)),
                errors, "test.txt");
        assertTrue(errors.isEmpty());
    }
}
