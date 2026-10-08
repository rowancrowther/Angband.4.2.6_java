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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the helpers a stack merge leans on: {@link ItemObject}'s {@code originCombine},
 * {@code distributeCharges}, {@code effectIsKnown} and {@code isFullyKnown}, the ports of C's
 * {@code object_origin_combine}, {@code distribute_charges}, {@code object_effect_is_known} and
 * {@code object_fully_known}. {@code cursesAreEqual} is covered through
 * {@code ItemObjectSimilarTest}, which has the registry set up for it.
 *
 * <p>Expected values are worked by hand from the C functions, not read back from the port. The
 * helpers are private, so they are reached by reflection.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectMergeHelpersTest {

    /**
     * The {@code GameConstants.data} in place before this class added a world section to it.
     */
    private static GameConstantsData savedConstants;

    /**
     * Adds {@code world:max-depth} to the constants table so a rod's recharge dice can be averaged.
     *
     * <p>{@code distributeCharges} averages {@code time} through {@code RandomValueUtils.mBonusCalc},
     * whose AVERAGE branch divides by {@code GameConstants.getWorldMaxDepth()} even when the bonus
     * term is zero. {@link SeededPlayerRegistry} leaves the world section null, so without this the
     * rod cases pass only when an earlier class in the same JVM happens to have left one behind.
     * The sections the extension seeded are carried across unchanged.
     *
     * @throws ReflectiveOperationException if the constants field cannot be reached
     */
    @BeforeAll
    static void seedWorldMaxDepth() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        savedConstants = (GameConstantsData) field.get(null);

        GameConstantsData old = savedConstants;
        WorldData world = new WorldData(128, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        field.set(null, old == null
                ? new GameConstantsData(null, null, null, null, world, null, null, null, null, null,
                null, null, null, null, null, null, null)
                : new GameConstantsData(old.levelMax(), old.monGen(), old.monPlay(), old.dunGen(),
                world, old.carryCap(), old.store(), old.objMake(), old.player(),
                old.meleeCritical(), old.meleeCriticalLevel(), old.rangedCritical(),
                old.rangedCriticalLevel(), old.oMeleeCritical(), old.oMeleeCriticalLevel(),
                old.oRangedCritical(), old.oRangedCriticalLevel()));
    }

    /**
     * Puts back the table {@link #seedWorldMaxDepth()} replaced.
     *
     * @throws ReflectiveOperationException if the constants field cannot be reached
     */
    @AfterAll
    static void restoreConstants() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        field.set(null, savedConstants);
    }

    private static Object call(ItemObject target, String name, Class<?>[] types, Object... args) {
        try {
            Method method = ItemObject.class.getDeclaredMethod(name, types);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw new AssertionError(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(name + " is no longer reachable by reflection", e);
        }
    }

    private static void originCombine(ItemObject into, ItemObject from) {
        call(into, "originCombine", new Class<?>[]{ItemObject.class}, from);
    }

    private static void distributeCharges(ItemObject source, ItemObject dest, int amount,
                                          boolean destNew) {
        call(source, "distributeCharges", new Class<?>[]{ItemObject.class, int.class, boolean.class},
                dest, amount, destNew);
    }

    private static boolean effectIsKnown(ItemObject item) {
        return (boolean) call(item, "effectIsKnown", new Class<?>[]{});
    }

    private static ItemObject item(TValue tValue) {
        return ItemFixture.item(tValue).kind(new ObjectKind()).build();
    }

    private static MonsterRace race(boolean unique) {
        MonsterRace race = new MonsterRace();
        if (unique) {
            ItemFixture.set(race, "flags", new Flag<>(MonsterRaceFlag.class, MonsterRaceFlag.RF_UNIQUE));
        }
        return race;
    }

    private static ItemObject withOrigin(ObjectOriginEnum origin, int depth, MonsterRace race) {
        return ItemFixture.item(TValue.TV_SWORD).kind(new ObjectKind()).origin(origin, depth, race).build();
    }

    private static Effect effect() {
        return new Effect(EffectEnum.EF_NONE, null, null, 0, 0, null, null, 0, 0, List.of(), null);
    }

    /**
     * C's {@code object_origin_combine}: the origin of the absorbed stack is folded into the
     * surviving one, preferring the record of a unique monster.
     */
    @Nested
    @DisplayName("originCombine")
    class OriginCombine {

        @Test
        @DisplayName("the same origin and depth, with no race, is left alone")
        void sameOriginIsUnchanged() {
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_FLOOR, 5, null);
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_FLOOR, 5, null));

            assertEquals(ObjectOriginEnum.ORIGIN_FLOOR, ItemFixture.read(into, "origin"));
        }

        @Test
        @DisplayName("a different origin type with the same race is mixed")
        void differentOriginIsMixed() {
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_FLOOR, 5, null);
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_CHEST, 5, null));

            assertEquals(ObjectOriginEnum.ORIGIN_MIXED, ItemFixture.read(into, "origin"));
        }

        @Test
        @DisplayName("a different depth with the same origin and race is mixed")
        void differentDepthIsMixed() {
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_FLOOR, 5, null);
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_FLOOR, 6, null));

            assertEquals(ObjectOriginEnum.ORIGIN_MIXED, ItemFixture.read(into, "origin"));
        }

        @Test
        @DisplayName("this item's unique is kept against a non-unique")
        void ownUniqueIsKept() {
            MonsterRace unique = race(true);
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_DROP_UNKNOWN, 9, unique);
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, race(false)));

            assertAll(
                    () -> assertEquals(ObjectOriginEnum.ORIGIN_DROP_UNKNOWN, ItemFixture.read(into, "origin")),
                    () -> assertEquals(9, ItemFixture.read(into, "originDepth")),
                    () -> assertSame(unique, ItemFixture.read(into, "originRace")));
        }

        @Test
        @DisplayName("the other item's unique replaces this item's non-unique record")
        void otherUniqueIsTaken() {
            MonsterRace unique = race(true);
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, race(false));
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_DROP_UNKNOWN, 9, unique));

            assertAll(
                    () -> assertEquals(ObjectOriginEnum.ORIGIN_DROP_UNKNOWN, ItemFixture.read(into, "origin")),
                    () -> assertEquals(9, ItemFixture.read(into, "originDepth")),
                    () -> assertSame(unique, ItemFixture.read(into, "originRace")));
        }

        @Test
        @DisplayName("two different non-unique races are mixed and keep this item's race")
        void bothNonUniqueIsMixed() {
            MonsterRace mine = race(false);
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, mine);
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, race(false)));

            assertAll(
                    () -> assertEquals(ObjectOriginEnum.ORIGIN_MIXED, ItemFixture.read(into, "origin")),
                    () -> assertSame(mine, ItemFixture.read(into, "originRace")));
        }

        @Test
        @DisplayName("two different uniques are mixed")
        void bothUniqueIsMixed() {
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, race(true));
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, race(true)));

            assertEquals(ObjectOriginEnum.ORIGIN_MIXED, ItemFixture.read(into, "origin"));
        }

        @Test
        @DisplayName("a race against no race counts as different races")
        void raceAgainstNoRaceIsMixed() {
            ItemObject into = withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, race(false));
            originCombine(into, withOrigin(ObjectOriginEnum.ORIGIN_DROP, 3, null));

            assertEquals(ObjectOriginEnum.ORIGIN_MIXED, ItemFixture.read(into, "origin"));
        }
    }

    /**
     * C's {@code distribute_charges}: the receiver is C's {@code source}, the argument its
     * {@code dest}. A rod's recharge time here is a flat 10 turns, so the cap for {@code n} rods
     * is {@code 10 * n}.
     */
    @Nested
    @DisplayName("distributeCharges")
    class DistributeCharges {

        private ItemObject wand(int pval, int number) {
            ItemObject wand = item(TValue.TV_WAND);
            ItemFixture.set(wand, "pValue", pval);
            ItemFixture.set(wand, "number", number);
            return wand;
        }

        private ItemObject rod(int timeout, int number) {
            ItemObject rod = item(TValue.TV_ROD);
            ItemFixture.set(rod, "timeout", timeout);
            ItemFixture.set(rod, "number", number);
            ItemFixture.set(rod, "time", new Random(10, 0, 0, 0, false));
            return rod;
        }

        @Test
        @DisplayName("a partial move to a new stack splits the charges in proportion")
        void partialMoveToNewStack() {
            ItemObject source = wand(10, 4);
            ItemObject dest = wand(0, 0);

            distributeCharges(source, dest, 2, true);

            assertAll(
                    () -> assertEquals(5, ItemFixture.read(dest, "pValue")),
                    () -> assertEquals(5, ItemFixture.read(source, "pValue")));
        }

        @Test
        @DisplayName("moving the whole stack leaves the source's charges alone")
        void wholeMoveLeavesSource() {
            ItemObject source = wand(10, 4);
            ItemObject dest = wand(0, 0);

            distributeCharges(source, dest, 4, true);

            assertAll(
                    () -> assertEquals(10, ItemFixture.read(dest, "pValue")),
                    () -> assertEquals(10, ItemFixture.read(source, "pValue")));
        }

        @Test
        @DisplayName("an existing destination gains the share instead of being replaced")
        void existingDestinationAdds() {
            ItemObject source = wand(10, 4);
            ItemObject dest = wand(3, 1);

            distributeCharges(source, dest, 2, false);

            assertAll(
                    () -> assertEquals(8, ItemFixture.read(dest, "pValue")),
                    () -> assertEquals(5, ItemFixture.read(source, "pValue")));
        }

        @Test
        @DisplayName("the share is integer division, rounded down, with the remainder left behind")
        void shareRoundsDown() {
            ItemObject source = wand(7, 3);
            ItemObject dest = wand(0, 0);

            distributeCharges(source, dest, 1, true);

            assertAll(
                    () -> assertEquals(2, ItemFixture.read(dest, "pValue")),
                    () -> assertEquals(5, ItemFixture.read(source, "pValue")));
        }

        @Test
        @DisplayName("a rod's timeout moves to a new stack up to the cap for the rods moved")
        void rodTimeoutCappedForNewStack() {
            ItemObject source = rod(25, 3);
            ItemObject dest = rod(0, 0);

            distributeCharges(source, dest, 2, true);

            assertAll(
                    () -> assertEquals(20, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(5, ItemFixture.read(source, "timeout")));
        }

        @Test
        @DisplayName("a rod's timeout below the cap all moves across")
        void rodTimeoutBelowCapAllMoves() {
            ItemObject source = rod(15, 3);
            ItemObject dest = rod(0, 0);

            distributeCharges(source, dest, 2, true);

            assertAll(
                    () -> assertEquals(15, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(0, ItemFixture.read(source, "timeout")));
        }

        @Test
        @DisplayName("moving the whole rod stack leaves the source's timeout alone")
        void wholeRodMoveLeavesSource() {
            ItemObject source = rod(25, 2);
            ItemObject dest = rod(0, 0);

            distributeCharges(source, dest, 2, true);

            assertAll(
                    () -> assertEquals(20, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(25, ItemFixture.read(source, "timeout")));
        }

        @Test
        @DisplayName("an existing rod stack takes timeout up to the cap for its rods after the move")
        void rodTimeoutToExistingStack() {
            ItemObject source = rod(25, 3);
            ItemObject dest = rod(10, 2);

            distributeCharges(source, dest, 2, false);

            assertAll(
                    () -> assertEquals(30, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(5, ItemFixture.read(source, "timeout")));
        }

        @Test
        @DisplayName("an existing rod stack close to its cap takes only what fits")
        void rodTimeoutClampedToExistingCap() {
            ItemObject source = rod(25, 3);
            ItemObject dest = rod(35, 2);

            distributeCharges(source, dest, 2, false);

            assertAll(
                    () -> assertEquals(40, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(20, ItemFixture.read(source, "timeout")));
        }

        @Test
        @DisplayName("an existing rod stack already at its cap takes nothing")
        void rodTimeoutAtCapMovesNothing() {
            ItemObject source = rod(25, 3);
            ItemObject dest = rod(40, 2);

            distributeCharges(source, dest, 2, false);

            assertAll(
                    () -> assertEquals(40, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(25, ItemFixture.read(source, "timeout")));
        }

        @Test
        @DisplayName("other item types are untouched")
        void otherTypesUntouched() {
            ItemObject source = item(TValue.TV_POTION);
            ItemFixture.set(source, "pValue", 9);
            ItemFixture.set(source, "number", 3);
            ItemFixture.set(source, "timeout", 9);
            ItemObject dest = item(TValue.TV_POTION);

            distributeCharges(source, dest, 1, true);

            assertAll(
                    () -> assertEquals(0, ItemFixture.read(dest, "pValue")),
                    () -> assertEquals(0, ItemFixture.read(dest, "timeout")),
                    () -> assertEquals(9, ItemFixture.read(source, "pValue")),
                    () -> assertEquals(9, ItemFixture.read(source, "timeout")));
        }
    }

    /**
     * C's {@code object_effect_is_known} and {@code object_fully_known}.
     */
    @Nested
    @DisplayName("effect knowledge")
    class EffectKnowledge {

        @Test
        @DisplayName("an item with no known counterpart does not know its effect")
        void noCounterpart() {
            assertFalse(effectIsKnown(item(TValue.TV_POTION)));
        }

        @Test
        @DisplayName("an item with no known counterpart is not fully known")
        void notFullyKnownWithoutCounterpart() {
            assertFalse(item(TValue.TV_POTION).isFullyKnown());
        }

        @Test
        @DisplayName("two empty effect lists are known")
        void emptyListsAreKnown() {
            ItemObject known = item(TValue.TV_POTION);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(new ObjectKind()).known(known).build();

            assertTrue(effectIsKnown(item));
        }

        @Test
        @DisplayName("an effect the counterpart has learned is known")
        void sharedEffectIsKnown() {
            Effect effect = effect();
            ItemObject known = item(TValue.TV_POTION);
            known.setEffect(new ArrayList<>(List.of(effect)));
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(new ObjectKind()).known(known).build();
            item.setEffect(new ArrayList<>(List.of(effect)));

            assertTrue(effectIsKnown(item));
        }

        @Test
        @DisplayName("an effect the counterpart lacks is not known")
        void unlearnedEffectIsNotKnown() {
            ItemObject known = item(TValue.TV_POTION);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(new ObjectKind()).known(known).build();
            item.setEffect(new ArrayList<>(List.of(effect())));

            assertFalse(effectIsKnown(item));
        }

        @Test
        @DisplayName("an effect on the counterpart that the item lacks is not known")
        void extraKnownEffectIsNotKnown() {
            ItemObject known = item(TValue.TV_POTION);
            known.setEffect(new ArrayList<>(List.of(effect())));
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(new ObjectKind()).known(known).build();

            assertFalse(effectIsKnown(item));
        }
    }
}
