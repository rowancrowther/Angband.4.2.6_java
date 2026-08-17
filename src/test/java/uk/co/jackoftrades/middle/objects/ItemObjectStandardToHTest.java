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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#hasStandardToH()}, the port of C's {@code object_has_standard_to_h}
 * ({@code obj-knowledge.c:580}).
 *
 * <p>The method answers one narrow question — is this item's to-hit the one it ought to have? — and
 * exists because to-hit is the only combat figure an ordinary item carries as a matter of course.
 * Body armour is heavy and awkward, so its kind declares a penalty: Chain Mail is
 * {@code attack:1d4:-2:0} in {@code object.txt} and every hauberk rolled from it has
 * {@code toHit == -2}. Anything that tested that against zero would conclude the armour was
 * enchanted and teach the to-hit rune to whoever put it on, which is the false positive this method
 * exists to prevent.
 *
 * <p>The three branches are tested apart from any player, because none of them involves one. What
 * the learning code then does with the answer is
 * {@code PlayerRuneLearningTest.EquipLearnOnMeleeAttack}'s business.
 *
 * <p>The fixtures build kinds and items by reflection rather than through the long constructor. The
 * constructor would work, but it takes thirty-odd arguments to say one thing, and a test that reads
 * as a wall of zeroes hides which of them the case turns on.
 *
 * <p>Class ItemObjectStandardToHTest coded on 260815, commented in full on 260815.
 *
 * @author Rowan Crowther
 */
class ItemObjectStandardToHTest {

    /**
     * Writes a private field on anything, since neither {@link ItemObject} nor {@link ObjectKind}
     * offers setters for the fields under test — a real item gets them at generation, from machinery
     * this suite does not run.
     *
     * @param target the object to modify
     * @param name   the declared field name
     * @param value  the value to write
     * @author Rowan Crowther
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * A kind whose to-hit is the fixed value given — the ordinary case, and the one body armour is
     * in. Built with the integer constructor rather than {@link Random#parseStr} so the test does
     * not depend on the dice parser: a negative base arrives as a positive with the negate flag set,
     * which is how {@code parseStr} hands one over.
     *
     * @param toH the kind's fixed to-hit figure
     * @return a kind declaring that to-hit and nothing else
     * @author Rowan Crowther
     */
    private static ObjectKind kindWithFixedToH(int toH) throws Exception {
        ObjectKind kind = new ObjectKind();
        poke(kind, "toH", toH < 0
                ? new Random(-toH, 0, 0, 0, true)
                : new Random(toH, 0, 0, 0, false));
        return kind;
    }

    /**
     * A kind whose to-hit is rolled from dice, so that no single figure is the expected one.
     *
     * @return a kind whose to-hit varies
     * @author Rowan Crowther
     */
    private static ObjectKind kindWithVaryingToH() throws Exception {
        ObjectKind kind = new ObjectKind();
        poke(kind, "toH", new Random(0, 0, 1, 4, false));
        return kind;
    }

    /**
     * An item of the given type and kind, carrying the given to-hit.
     *
     * @param tValue the item type, which decides whether the body-armour branch is taken
     * @param kind   the kind this item is an instance of, or {@code null} for none
     * @param toHit  the figure this particular item rolled
     * @return the item
     * @author Rowan Crowther
     */
    private static ItemObject item(TValue tValue, ObjectKind kind, int toHit) throws Exception {
        ItemObject item = new ItemObject();
        poke(item, "tValue", tValue);
        poke(item, "kind", kind);
        poke(item, "toHit", toHit);
        return item;
    }

    /**
     * C's {@code if (!obj->kind) return true;}, commented there as a hack for curse object
     * structures. A curse's contribution is carried on a bare {@code struct object} that was never
     * generated from a template, so there is no normal value to compare against.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("an item with no kind")
    class NoKind {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard, having no template to have departed from")
        void noKindIsStandard() throws Exception {
            assertTrue(item(TValue.TV_SWORD, null, 0).hasStandardToH());
        }

        /**
         * The interesting half: the answer does not depend on the figure. A curse structure may well
         * carry a to-hit — that is what makes it a curse — and it is still not evidence about an
         * item, because there is no item.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard even when it carries a to-hit")
        void noKindIsStandardWhateverTheFigure() throws Exception {
            assertTrue(item(TValue.TV_SWORD, null, -5).hasStandardToH());
        }

        /**
         * The kind is tested before the type is, so a null kind cannot fall over on the body-armour
         * branch. Worth pinning: reordering the two tests would throw here rather than answer.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard without consulting its type")
        void noKindIsAnsweredBeforeTheTypeIsRead() throws Exception {
            ItemObject item = new ItemObject();
            poke(item, "kind", null);
            poke(item, "tValue", null);

            assertTrue(item.hasStandardToH());
        }
    }

    /**
     * The branch the method exists for. Body armour's penalty comes from its kind, so "standard"
     * here means "still what the kind said", not "zero".
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("body armour whose kind fixes its to-hit")
    class BodyArmourWithFixedToH {

        /**
         * Chain Mail as {@code object.txt} writes it: a to-hit of -2 that every hauberk has and none
         * of them earned. This is the case a plain non-zero test would get wrong.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard at the penalty its kind declares")
        void atTheKindsBaseIsStandard() throws Exception {
            assertTrue(item(TValue.TV_HARD_ARMOR, kindWithFixedToH(-2), -2).hasStandardToH());
        }

        /**
         * The same armour improved. An ego or an enchantment scroll has moved it off its kind's
         * figure, and that departure is the thing worth learning.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is not standard once something has moved it off that figure")
        void awayFromTheKindsBaseIsNotStandard() throws Exception {
            assertFalse(item(TValue.TV_HARD_ARMOR, kindWithFixedToH(-2), 3).hasStandardToH());
        }

        /**
         * The trap the branch is there to avoid, stated directly: zero is the wrong answer for
         * armour whose kind says -2, in both directions.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is not standard at zero, when its kind declares a penalty")
        void zeroIsNotStandardForPenalisedArmour() throws Exception {
            assertFalse(item(TValue.TV_SOFT_ARMOR, kindWithFixedToH(-2), 0).hasStandardToH());
        }

        /**
         * All three body-armour types take the branch, since C's {@code tval_is_body_armor} lists
         * soft, hard and dragon-scale together.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("covers soft, hard and dragon-scale alike")
        void allThreeArmourTypesTakeTheBranch() throws Exception {
            assertTrue(item(TValue.TV_SOFT_ARMOR, kindWithFixedToH(-1), -1).hasStandardToH());
            assertTrue(item(TValue.TV_HARD_ARMOR, kindWithFixedToH(-1), -1).hasStandardToH());
            assertTrue(item(TValue.TV_DRAG_ARMOR, kindWithFixedToH(-1), -1).hasStandardToH());
        }

        /**
         * Armour whose kind declares no penalty is not a special case at all — the comparison is
         * against zero either way, and arrives at it through the armour branch rather than the last
         * one.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard at zero when its kind declares no penalty")
        void unpenalisedArmourIsStandardAtZero() throws Exception {
            assertTrue(item(TValue.TV_SOFT_ARMOR, kindWithFixedToH(0), 0).hasStandardToH());
            assertFalse(item(TValue.TV_SOFT_ARMOR, kindWithFixedToH(0), 2).hasStandardToH());
        }
    }

    /**
     * The {@code !varies()} guard. If the kind rolls its to-hit from dice there is no one figure the
     * item was expected to have, so comparing against the base would be picking an end of the range
     * arbitrarily; C falls through to the zero test instead.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("body armour whose kind rolls its to-hit")
    class BodyArmourWithVaryingToH {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard at zero, not at the dice's base")
        void varyingKindFallsThroughToTheZeroTest() throws Exception {
            assertTrue(item(TValue.TV_HARD_ARMOR, kindWithVaryingToH(), 0).hasStandardToH());
        }

        /**
         * The distinguishing case. {@code new Random(0, 0, 1, 4, false)} has a base of 0, so the two
         * branches would agree at zero and only disagree away from it: a 1d4 kind and an item at 3
         * is standard under the base comparison and not standard under the zero test. C gives the
         * second answer.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is not standard away from zero, whatever the dice could have rolled")
        void varyingKindDoesNotExcuseANonZeroFigure() throws Exception {
            assertFalse(item(TValue.TV_HARD_ARMOR, kindWithVaryingToH(), 3).hasStandardToH());
        }
    }

    /**
     * Everything that is not body armour, which is most things. A sword has no built-in accuracy, so
     * any figure at all came from an ego, an artifact or a curse.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("anything that is not body armour")
    class NotBodyArmour {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is standard at zero")
        void zeroIsStandard() throws Exception {
            assertTrue(item(TValue.TV_SWORD, kindWithFixedToH(0), 0).hasStandardToH());
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is not standard with a bonus")
        void aBonusIsNotStandard() throws Exception {
            assertFalse(item(TValue.TV_SWORD, kindWithFixedToH(0), 4).hasStandardToH());
        }

        /**
         * A penalty is as much a departure as a bonus. The rune names the enchantment, not its
         * direction, so a cursed blade teaches to-hit exactly as a blessed one does.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("is not standard with a penalty either")
        void aPenaltyIsNotStandard() throws Exception {
            assertFalse(item(TValue.TV_SWORD, kindWithFixedToH(0), -4).hasStandardToH());
        }

        /**
         * Non-armour never reaches the kind's dice, so it does not matter what they say. Shield,
         * helm and gloves all live here — C's {@code tval_is_body_armor} means body armour
         * specifically, not armour in general, and only body armour carries a to-hit penalty in the
         * data.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("ignores its kind's to-hit, however that kind was written")
        void theKindsFigureIsNotConsulted() throws Exception {
            assertTrue(item(TValue.TV_SHIELD, kindWithFixedToH(-2), 0).hasStandardToH());
            assertFalse(item(TValue.TV_SHIELD, kindWithFixedToH(-2), -2).hasStandardToH());
            assertTrue(item(TValue.TV_HELM, kindWithVaryingToH(), 0).hasStandardToH());
        }
    }
}