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

package uk.co.jackoftradesltd.middle.effect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.enums.EffectBaseType;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Effect}'s delegating accessors and its copy, along with {@link Expression}'s dice
 * string.
 *
 * <p>{@code isAim}, {@code getInfo} and {@code getDescription} all read from the effect's identity
 * rather than from the effect itself — C keeps the same information in its {@code effect_kind}
 * table. Each is therefore guarded by {@link Effect#isValid()}, and the guard is the part worth
 * testing: an effect whose identity is the {@code EF_NONE} placeholder or the {@code EF_MAX}
 * sentinel is not a real effect, and must answer harmlessly rather than reach through to a table row
 * that means nothing.
 *
 * @author Rowan Crowther
 */
class EffectAccessorsTest {

    /**
     * Builds an effect around one identity, with everything else empty.
     *
     * @param index the effect's identity
     * @return the effect
     */
    private static Effect effect(EffectEnum index) {
        return new Effect(index, new Random(0, 1, 1, 1, false), "1d1", 0, 0,
                EffectSubTypeEnum.EST_PROJ,
                new EffectSubTypeWrapper(ProjectionEnum.PROJ_ACID),
                0, 0, new Random(0, 1, 1, 1, false), new ArrayList<>(), "a message");
    }

    /**
     * The three accessors that read the effect's identity.
     */
    @Nested
    @DisplayName("identity-backed accessors")
    class IdentityBacked {

        /**
         * A real effect answers from its table row: a bolt is aimed, and carries the damage label
         * the description code prints.
         */
        @Test
        @DisplayName("a real effect answers from its identity")
        void realEffectAnswers() {
            Effect bolt = effect(EffectEnum.EF_BOLT);

            assertTrue(bolt.isValid());
            assertTrue(bolt.isAim(), "a bolt is aimed at something");
            assertEquals("dam", bolt.getInfo());
        }

        /**
         * Not every real effect is aimed, so the aim test is reading the row rather than answering
         * true for anything valid.
         */
        @Test
        @DisplayName("an unaimed effect reports so")
        void unaimedEffectReportsFalse() {
            Effect bolt = effect(EffectEnum.EF_BOLT);
            Effect unaimed = effect(EffectEnum.EF_HEAL_HP);

            assertTrue(bolt.isAim());
            assertFalse(unaimed.isAim());
        }

        /**
         * The placeholder identity is not a real effect. Both accessors take their guarded exit —
         * {@code false} and {@code null} — rather than reading a row that describes nothing.
         */
        @Test
        @DisplayName("the placeholder identity answers harmlessly")
        void placeholderIsGuarded() {
            Effect none = effect(EffectEnum.EF_NONE);

            assertFalse(none.isValid());
            assertFalse(none.isAim());
            assertNull(none.getInfo());
        }

        /**
         * So does the count sentinel at the far end of the enum, which is the other value
         * {@link Effect#isValid()} excludes.
         */
        @Test
        @DisplayName("the count sentinel answers harmlessly too")
        void sentinelIsGuarded() {
            Effect max = effect(EffectEnum.EF_MAX);

            assertFalse(max.isValid());
            assertFalse(max.isAim());
            assertNull(max.getInfo());
        }
    }

    /**
     * The copy, which duplicates the mutable parts and shares the identity.
     *
     * <p>{@link Effect} exposes few accessors — the fields are read by the effect handlers rather
     * than from outside — so what a test can observe is the recharge dice, which is its own object,
     * and the identity-backed answers, which must survive the copy unchanged.
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * The recharge dice are copied rather than shared, so the two effects can be re-diced
         * independently. That is the visible half of a copy that also duplicates the magnitude dice
         * and the sub-type payload.
         */
        @Test
        @DisplayName("the recharge dice are duplicated")
        void diceDuplicated() {
            Effect original = effect(EffectEnum.EF_BOLT);
            Effect duplicate = original.copy();

            assertNotSame(original, duplicate);
            assertNotSame(original.getTime(), duplicate.getTime());
            assertEquals(original.getTime().getSides(), duplicate.getTime().getSides());
        }

        /**
         * The identity is shared rather than copied, because it is what the effect <em>is</em>: two
         * copies of a bolt must both be a bolt, and must both answer from the same table row.
         */
        @Test
        @DisplayName("the identity survives the copy")
        void identitySurvives() {
            Effect original = effect(EffectEnum.EF_BOLT);
            Effect duplicate = original.copy();

            assertTrue(duplicate.isValid());
            assertEquals(original.isAim(), duplicate.isAim());
            assertEquals(original.getInfo(), duplicate.getInfo());
            assertEquals(original.getDescription(), duplicate.getDescription());
        }

        /**
         * Copying an invalid effect keeps it invalid, rather than promoting it to something the
         * guards would let through.
         */
        @Test
        @DisplayName("an invalid effect copies as invalid")
        void invalidStaysInvalid() {
            Effect duplicate = effect(EffectEnum.EF_NONE).copy();

            assertFalse(duplicate.isValid());
            assertNull(duplicate.getInfo());
        }
    }

    /**
     * {@link Expression}'s dice string, which the constructor does not take.
     *
     * <p>There is no accessor for it — the field is written by the parser and read by the effect
     * evaluation, neither of which goes through a getter — so these tests reach it by reflection.
     * That is the only way to show that {@link Expression#copy()} carries it, which matters because
     * the copy assigns it after construction rather than passing it through, and an omission there
     * would leave a copied expression pointing at no dice at all.
     */
    @Nested
    @DisplayName("Expression dice string")
    class ExpressionDiceString {

        /**
         * Reads an expression's dice string, which has no accessor.
         *
         * @param expression the expression to read
         * @return its dice string, or {@code null} if it has none
         * @throws Exception if the field cannot be reached
         */
        private String diceStringOf(Expression expression) throws Exception {
            Field field = Expression.class.getDeclaredField("diceString");
            field.setAccessible(true);
            return (String) field.get(expression);
        }

        /**
         * A fresh expression has none, because the constructor takes the three fields that identify
         * it and not the dice it feeds.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a fresh expression has no dice string")
        void freshHasNone() throws Exception {
            assertNull(diceStringOf(new Expression('B', EffectBaseType.EFB_PLAYER_LEVEL, "* 2")));
        }

        /**
         * Setting it stores it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the dice string is stored")
        void diceStringStored() throws Exception {
            Expression expression = new Expression('B', EffectBaseType.EFB_PLAYER_LEVEL, "* 2");
            expression.setDiceString("$B+2d4");

            assertEquals("$B+2d4", diceStringOf(expression));
        }

        /**
         * And the copy carries it across, along with the three identifying fields.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a copy carries the dice string")
        void copyCarriesDiceString() throws Exception {
            Expression original = new Expression('B', EffectBaseType.EFB_PLAYER_LEVEL, "* 2");
            original.setDiceString("$B+2d4");

            Expression duplicate = original.copy();

            assertNotSame(original, duplicate);
            assertEquals("$B+2d4", diceStringOf(duplicate));
        }
    }
}
