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

package uk.co.jackoftrades.middle.objects.enums;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link TValue}'s classification predicates — the port of the {@code tval_is_*} family in C's
 * {@code obj-tval.c}.
 *
 * <p>Each predicate answers "is this object type one of these?", and the risk they carry is not that
 * one returns the wrong answer for the type it names but that its <em>membership list</em> is wrong:
 * a missing case makes an object silently stop being a weapon, and an extra one makes a wand
 * wearable. So the tests here assert the whole membership rather than one member — the helper
 * {@link #assertMembership} checks that a predicate is true for exactly the types listed and false
 * for all thirty-odd others.
 *
 * <p>The lists are C's, taken from {@code obj-tval.c}, so a divergence here is a divergence from the
 * original rather than from an opinion about what ought to count as armour.
 *
 * @author Rowan Crowther
 */
class TValueClassificationTest {

    /**
     * Asserts that a predicate holds for exactly the given types and for no others.
     *
     * <p>The negative half is what makes this worth doing: any predicate passes a test that only
     * checks the types it is supposed to accept.
     *
     * @param name      the predicate's name, for the failure message
     * @param predicate the predicate under test
     * @param expected  every type it should accept
     */
    private static void assertMembership(String name, Predicate<TValue> predicate, Set<TValue> expected) {
        for (TValue tval : TValue.values()) {
            boolean actual = predicate.test(tval);
            if (expected.contains(tval)) {
                assertTrue(actual, name + " should accept " + tval);
            } else {
                assertFalse(actual, name + " should reject " + tval);
            }
        }
    }

    /**
     * The digger, which is both a tool and a melee weapon, and so appears in lists a reader might
     * not expect.
     */
    @Test
    @DisplayName("a digger is a melee weapon and a weapon")
    void diggerIsAWeapon() {
        assertTrue(TValue.TV_DIGGING.isDigger());
        assertTrue(TValue.TV_DIGGING.isMeleeWeapon());
        assertTrue(TValue.TV_DIGGING.isWeapon());
        assertTrue(TValue.TV_DIGGING.isWearable());
    }

    /**
     * {@code findIndex} answers the enum ordinal, which is the numeric tval the data files use. The
     * only fact worth pinning is that {@code TV_NONE} is zero, as C's is: the constants are declared
     * in C's order, and a reordering would change every number silently.
     */
    @Test
    @DisplayName("findIndex answers the ordinal, and TV_NONE is zero")
    void findIndexIsTheOrdinal() {
        assertEquals(0, TValue.TV_NONE.ordinal());
        assertEquals(TValue.TV_SWORD.ordinal(), TValue.findIndex("sword"));
        assertEquals(TValue.TV_POTION.ordinal(), TValue.findIndex("potion"));
    }

    /**
     * The predicates that name a single type. Each is one identity comparison, so what is worth
     * checking is that it is comparing against the type its name claims.
     */
    @Nested
    @DisplayName("single-type predicates")
    class SingleType {

        /**
         * Each accepts its own type and nothing else.
         */
        @Test
        @DisplayName("each accepts exactly the type it names")
        void singleTypesAreExact() {
            assertMembership("isStaff", TValue::isStaff, EnumSet.of(TValue.TV_STAFF));
            assertMembership("isWand", TValue::isWand, EnumSet.of(TValue.TV_WAND));
            assertMembership("isRod", TValue::isRod, EnumSet.of(TValue.TV_ROD));
            assertMembership("isPotion", TValue::isPotion, EnumSet.of(TValue.TV_POTION));
            assertMembership("isScroll", TValue::isScroll, EnumSet.of(TValue.TV_SCROLL));
            assertMembership("isFood", TValue::isFood, EnumSet.of(TValue.TV_FOOD));
            assertMembership("isMushroom", TValue::isMushroom, EnumSet.of(TValue.TV_MUSHROOM));
            assertMembership("isLight", TValue::isLight, EnumSet.of(TValue.TV_LIGHT));
            assertMembership("isRing", TValue::isRing, EnumSet.of(TValue.TV_RING));
            assertMembership("isChest", TValue::isChest, EnumSet.of(TValue.TV_CHEST));
            assertMembership("isMoney", TValue::isMoney, EnumSet.of(TValue.TV_GOLD));
            assertMembership("isBolt", TValue::isBolt, EnumSet.of(TValue.TV_BOLT));
        }

        /**
         * Two whose names do not match their type, and which would therefore pass unnoticed if only
         * the obvious cases were checked: fuel is the flask, and the launcher family has only the
         * one member.
         */
        @Test
        @DisplayName("fuel is the flask, and a launcher is a bow")
        void namesThatDoNotMatchTheirType() {
            assertMembership("isFuel", TValue::isFuel, EnumSet.of(TValue.TV_FLASK));
            assertMembership("isLauncher", TValue::isLauncher, EnumSet.of(TValue.TV_BOW));
        }
    }

    /**
     * The predicates that name a family. These are the ones where a missing case matters.
     */
    @Nested
    @DisplayName("family predicates")
    class Families {

        /**
         * The combat families. Note that ammunition counts as a weapon while a melee weapon does not
         * count as ammunition, and that the digger is a melee weapon — an easy one to leave out.
         */
        @Test
        @DisplayName("weapons, melee weapons and ammunition")
        void weaponFamilies() {
            assertMembership("isMeleeWeapon", TValue::isMeleeWeapon,
                    EnumSet.of(TValue.TV_SWORD, TValue.TV_HAFTED, TValue.TV_POLEARM, TValue.TV_DIGGING));
            assertMembership("isAmmo", TValue::isAmmo,
                    EnumSet.of(TValue.TV_SHOT, TValue.TV_ARROW, TValue.TV_BOLT));
            assertMembership("isSharpMissile", TValue::isSharpMissile,
                    EnumSet.of(TValue.TV_ARROW, TValue.TV_BOLT));
            assertMembership("isWeapon", TValue::isWeapon,
                    EnumSet.of(TValue.TV_SWORD, TValue.TV_HAFTED, TValue.TV_POLEARM, TValue.TV_DIGGING,
                            TValue.TV_BOW, TValue.TV_BOLT, TValue.TV_ARROW, TValue.TV_SHOT));
        }

        /**
         * The armour families. Body armour is the three that cover the torso; head armour is the
         * helm and the crown; the general predicate is all nine pieces.
         */
        @Test
        @DisplayName("armour, body armour and head armour")
        void armourFamilies() {
            assertMembership("isBodyArmour", TValue::isBodyArmour,
                    EnumSet.of(TValue.TV_SOFT_ARMOR, TValue.TV_HARD_ARMOR, TValue.TV_DRAG_ARMOR));
            assertMembership("isHeadArmour", TValue::isHeadArmour,
                    EnumSet.of(TValue.TV_CROWN, TValue.TV_HELM));
            assertMembership("isArmour", TValue::isArmour,
                    EnumSet.of(TValue.TV_DRAG_ARMOR, TValue.TV_HARD_ARMOR, TValue.TV_SOFT_ARMOR,
                            TValue.TV_SHIELD, TValue.TV_CLOAK, TValue.TV_CROWN, TValue.TV_HELM,
                            TValue.TV_BOOTS, TValue.TV_GLOVES));
        }

        /**
         * The consumable and device families, including the two that overlap: a zapper can fail, and
         * so can a rod, but a rod is not a zapper.
         */
        @Test
        @DisplayName("edibles, devices and the things that can fail")
        void consumableFamilies() {
            assertMembership("isEdible", TValue::isEdible,
                    EnumSet.of(TValue.TV_FOOD, TValue.TV_MUSHROOM));
            assertMembership("isZapper", TValue::isZapper,
                    EnumSet.of(TValue.TV_WAND, TValue.TV_STAFF));
            assertMembership("canHaveFailure", TValue::canHaveFailure,
                    EnumSet.of(TValue.TV_STAFF, TValue.TV_WAND, TValue.TV_ROD));
            assertMembership("canHaveCharges", TValue::canHaveCharges,
                    EnumSet.of(TValue.TV_STAFF, TValue.TV_WAND));
            assertMembership("canHaveTimeout", TValue::canHaveTimeout,
                    EnumSet.of(TValue.TV_ROD));
            assertMembership("canHaveNourishment", TValue::canHaveNourishment,
                    EnumSet.of(TValue.TV_FOOD, TValue.TV_POTION, TValue.TV_MUSHROOM));
            assertMembership("isUseable", TValue::isUseable,
                    EnumSet.of(TValue.TV_ROD, TValue.TV_WAND, TValue.TV_STAFF, TValue.TV_SCROLL,
                            TValue.TV_POTION, TValue.TV_FOOD, TValue.TV_MUSHROOM));
        }

        /**
         * Jewellery, books, and the flavoured types — the last being what decides whether an
         * unidentified object is described by a colour rather than a name.
         */
        @Test
        @DisplayName("jewellery, books and flavoured types")
        void otherFamilies() {
            assertMembership("isJewellery", TValue::isJewellery,
                    EnumSet.of(TValue.TV_RING, TValue.TV_AMULET));
            assertMembership("isBook", TValue::isBook,
                    EnumSet.of(TValue.TV_MAGIC_BOOK, TValue.TV_PRAYER_BOOK, TValue.TV_NATURE_BOOK,
                            TValue.TV_SHADOW_BOOK, TValue.TV_OTHER_BOOK));
            assertMembership("canHaveFlavour", TValue::canHaveFlavour,
                    EnumSet.of(TValue.TV_AMULET, TValue.TV_RING, TValue.TV_STAFF, TValue.TV_WAND,
                            TValue.TV_ROD, TValue.TV_POTION, TValue.TV_MUSHROOM, TValue.TV_SCROLL));
        }
    }

    /**
     * The two widest predicates, which decide how an object is priced and whether it can be worn.
     * They differ by exactly one type, and that difference is the point.
     */
    @Nested
    @DisplayName("wearable and variable-power")
    class WearableAndPower {

        /**
         * Everything that can be worn or wielded: the launcher, the melee weapons, the nine pieces
         * of armour, the light and the two pieces of jewellery. Ammunition is not wearable.
         */
        @Test
        @DisplayName("wearable covers the wielded and worn types, not ammunition")
        void wearableMembership() {
            assertMembership("isWearable", TValue::isWearable,
                    EnumSet.of(TValue.TV_BOW, TValue.TV_DIGGING, TValue.TV_HAFTED, TValue.TV_POLEARM,
                            TValue.TV_SWORD, TValue.TV_BOOTS, TValue.TV_GLOVES, TValue.TV_HELM,
                            TValue.TV_CROWN, TValue.TV_SHIELD, TValue.TV_CLOAK, TValue.TV_SOFT_ARMOR,
                            TValue.TV_HARD_ARMOR, TValue.TV_DRAG_ARMOR, TValue.TV_LIGHT,
                            TValue.TV_RING, TValue.TV_AMULET));
        }

        /**
         * Variable power is the wearable list plus ammunition, because a missile's worth also
         * depends on its bonuses. That single difference is what sends an object down the
         * known-half pricing route instead of the flat base price.
         */
        @Test
        @DisplayName("variable power is wearable plus ammunition")
        void variablePowerIsWearablePlusAmmo() {
            EnumSet<TValue> expected = EnumSet.of(TValue.TV_SHOT, TValue.TV_ARROW, TValue.TV_BOLT);
            for (TValue tval : TValue.values()) {
                if (tval.isWearable()) expected.add(tval);
            }

            assertMembership("hasVariablePower", TValue::hasVariablePower, expected);
        }

        /**
         * Stated directly, because it is the one place the two lists part company.
         */
        @Test
        @DisplayName("ammunition has variable power but is not wearable")
        void ammunitionIsTheDifference() {
            assertTrue(TValue.TV_ARROW.hasVariablePower());
            assertFalse(TValue.TV_ARROW.isWearable());
        }
    }

    /**
     * The two helpers that count and list the object kinds of a given type. Both read the object
     * registry, so the tests seed it with three kinds of known shape and put it back afterwards.
     */
    @Nested
    @DisplayName("registry-backed lookups")
    class RegistryLookups {

        /**
         * Whatever the registry held before this class ran.
         */
        private static List<ObjectKind> savedKinds;

        /**
         * Seeds two swords and one potion, with distinct svals, so that a count can be wrong in
         * either direction and be noticed.
         */
        @BeforeAll
        static void seedRegistry() {
            savedKinds = new ArrayList<>(ObjectRegistry.getObjectKinds());

            ObjectRegistry.setObjectKinds(new ArrayList<>(List.of(
                    kind(TValue.TV_SWORD, 3),
                    kind(TValue.TV_SWORD, 7),
                    kind(TValue.TV_POTION, 11))));
        }

        /**
         * Puts the registry back, so a class running after this one sees what it expected.
         */
        @AfterAll
        static void restoreRegistry() {
            ObjectRegistry.setObjectKinds(savedKinds);
        }

        /**
         * Builds an object kind of a given type and sub-type.
         *
         * @param tval the object type
         * @param sval the sub-type within it
         * @return the kind
         */
        private static ObjectKind kind(TValue tval, int sval) {
            ObjectKind kind = new ObjectKind(null, 0, 0, 0, 0, "test kind", tval, "test", null, false);
            kind.setsVal(sval);
            return kind;
        }

        /**
         * The count is of kinds carrying the named type, and nothing else.
         */
        @Test
        @DisplayName("the count answers per type")
        void countsPerType() {
            assertEquals(2, TValue.tValSValCount("sword"));
            assertEquals(1, TValue.tValSValCount("potion"));
            assertEquals(0, TValue.tValSValCount("ring"));
        }

        /**
         * A name that resolves to no type is answered with zero rather than an exception, because C
         * returns the same for an unknown name and its callers do not check.
         */
        @Test
        @DisplayName("an unknown name counts as none")
        void unknownNameCountsZero() {
            assertEquals(0, TValue.tValSValCount("not a tval"));
        }

        /**
         * The list holds the svals, in registry order, and agrees in size with the count for the
         * same name.
         */
        @Test
        @DisplayName("the list holds the svals in registry order")
        void listsSvalsInOrder() {
            assertEquals(List.of(3, 7), TValue.tvalSvalList("sword"));
            assertEquals(TValue.tValSValCount("sword"), TValue.tvalSvalList("sword").size());
        }

        /**
         * And an unknown name yields an empty list rather than null, which is what lets a caller
         * iterate the answer without checking it.
         */
        @Test
        @DisplayName("an unknown name lists nothing")
        void unknownNameListsNothing() {
            assertTrue(TValue.tvalSvalList("not a tval").isEmpty());
        }
    }
}
