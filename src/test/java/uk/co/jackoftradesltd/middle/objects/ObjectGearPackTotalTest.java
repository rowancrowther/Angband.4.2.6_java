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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.setStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the private {@code ObjectGear.objectPackTotal} - the port of C's {@code object_pack_total}
 * ({@code obj-gear.c:195}).
 *
 * <p>The method has two jobs at once: sum every gear stack like a template object, and identify
 * which of those stacks would be shown first in the inventory/quiver listing. The sum is the easy
 * half; the "first" half is where the port drifted from C twice while this suite was being written,
 * both times invisibly - a wrong "first" never throws, it just names the wrong stack in a message.
 *
 * <p><b>Why {@code first} is reset at entry is asserted directly.</b> C resets {@code *first} to
 * {@code NULL} unconditionally before searching, regardless of what the caller's pointer held. The
 * port cannot take an address, so it takes an {@link ItemObject} value instead and has to discard it
 * itself; the real caller, {@code invenCarry}, passes a non-null placeholder rather than {@code null},
 * which is exactly the shape that let the old bug hide. {@link FirstReset} rebuilds that call shape.
 *
 * <p><b>Why the label tie-break is re-run on every match, not just the first, is asserted directly.</b>
 * The gear list's iteration order need not match the letter/digit order the game assigns, so the
 * earliest-labelled match can turn up anywhere in the list; a search that stops comparing after the
 * first match found returns whichever stack the list happens to reach first instead. {@link
 * LabelTieBreak} builds gear lists where those two orders disagree.
 *
 * <p>Class ObjectGearPackTotalTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@DisplayName("ObjectGear.objectPackTotal")
class ObjectGearPackTotalTest {

    /**
     * A pack size generous enough to hold every fixture's inventory slot without exercising
     * {@code packSlotsUsed} itself, which this suite is not about.
     */
    private static final int PACK_SIZE = 5;

    /**
     * A quiver with room for the one or two slots these tests place.
     */
    private static final int QUIVER_SLOTS = 2;

    private static final int SLOT_SIZE = 1;

    private static final int THROWN_MULT = 1;

    /**
     * The kind's maximum stack - high enough that no test here is limited by it.
     */
    private static final int MAX_STACK = 99;

    private static Player savedPlayer;

    private static Object savedConstants;

    private static Object savedBodies;

    private static Object savedRaces;

    /**
     * The kind every fixture item shares. {@code similar} compares kinds by identity, so sharing
     * one instance is what makes separately-built stacks mutually stackable.
     */
    private static ObjectKind kind;

    private Player player;

    @BeforeAll
    static void seedGlobals() throws Exception {
        savedConstants = setStatic(GameConstants.class, "data", new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(PACK_SIZE, QUIVER_SLOTS, SLOT_SIZE, THROWN_MULT, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        PlayerBody humanoid = new PlayerBody("test",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(humanoid)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(testRace(humanoid))));

        savedPlayer = GameState.getPlayer();

        kind = ItemFixture.kindWithBase(TValue.TV_POTION, "potion", MAX_STACK);
    }

    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedPlayer);
        setStatic(GameConstants.class, "data", savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
    }

    /**
     * Builds a potion stack. Potions clear {@code similar}'s type-specific checks (bonuses, dice,
     * modifiers) by returning true as soon as the kind, flags and elements agree, which keeps every
     * fixture here about only what {@code objectPackTotal} itself decides.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject potion(int number) {
        return ItemFixture.item(TValue.TV_POTION).kind(kind).number(number).build();
    }

    /**
     * Builds the one race the seeded registry holds; only its body is read.
     *
     * @param body the body the race presents
     * @return the race
     */
    private static PlayerRace testRace(PlayerBody body) {
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * @param fieldName the registry field to reach
     * @return that private static field, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField(String fieldName) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * A fresh player with empty gear, installed as the live player because {@code similar} and
     * {@code objectStackable} reach their equipped-item checks through {@link GameState}.
     */
    @BeforeEach
    void buildPlayer() {
        player = new Player();
        GameState.setPlayer(player);
    }

    /**
     * @return the player's gear pile, the one {@link Player}'s own constructor installed
     */
    private Pile gear() {
        return player.getGear();
    }

    /**
     * @return the player's quiver slots
     */
    private ItemObject[] quiver() {
        return player.getPlayerUpkeep().getQuiver();
    }

    /**
     * @return the player's inventory (pack) slots, which {@code gearToLabel} reads to hand out
     * letters
     */
    private ItemObject[] inventory() {
        return player.getPlayerUpkeep().getInventory();
    }

    /**
     * Puts an item in the body's one equipment slot, so {@code itemIsEquipped} finds it.
     *
     * @param item the item to equip
     */
    private void equip(ItemObject item) {
        set(player.getPlayerBody().getSlots().get(0), "item", item);
    }

    /**
     * Calls the private {@code ObjectGear.objectPackTotal} and returns its result unpacked, since
     * the record it returns is public and can be typed directly.
     *
     * @param obj           the template object other gear stacks are compared against
     * @param ignoreInscrip whether inscriptions are ignored when testing for a match
     * @param first         the placeholder passed in the {@code first} slot - discarded by the
     *                      method itself, so its value should never affect the result
     * @return the method's result
     */
    private ObjectGear.ItemObjectAndInt packTotal(ItemObject obj, boolean ignoreInscrip, ItemObject first) {
        try {
            Method method = ObjectGear.class.getDeclaredMethod("objectPackTotal", Player.class,
                    ItemObject.class, boolean.class, ItemObject.class);
            method.setAccessible(true);
            return (ObjectGear.ItemObjectAndInt) method.invoke(null, player, obj, ignoreInscrip, first);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw new AssertionError(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(
                    "ObjectGear.objectPackTotal is no longer callable by reflection", e);
        }
    }

    /**
     * The ordinary path: summing matching stacks and reporting the one found.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("aggregation")
    class Aggregation {

        @Test
        @DisplayName("empty gear totals nothing and finds nothing")
        void emptyGearFindsNothing() {
            ObjectGear.ItemObjectAndInt result = packTotal(potion(1), false, null);

            assertEquals(0, result.number());
            assertNull(result.obj());
        }

        @Test
        @DisplayName("a single matching pack stack is totalled and returned as first")
        void singleMatchIsTotalledAndFirst() {
            ItemObject stack = potion(3);
            inventory()[0] = stack;
            gear().insertEnd(stack);

            ObjectGear.ItemObjectAndInt result = packTotal(potion(1), false, null);

            assertEquals(3, result.number());
            assertSame(stack, result.obj());
        }

        @Test
        @DisplayName("every matching stack contributes to the total, not just the first found")
        void everyMatchContributesToTheTotal() {
            ItemObject stackA = potion(3);
            inventory()[0] = stackA;
            gear().insertEnd(stackA);

            ItemObject stackB = potion(2);
            inventory()[1] = stackB;
            gear().insertEnd(stackB);

            ObjectGear.ItemObjectAndInt result = packTotal(potion(1), false, null);

            assertEquals(5, result.number());
        }
    }

    /**
     * The {@code cursor == obj} branch: a template that is itself already in the gear only counts
     * if it is not equipped, since {@link ItemObject#similar} and {@link
     * ItemObject#objectStackable} both refuse to compare an object to itself and would otherwise
     * silently drop it from its own total.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("self-match")
    class SelfMatch {

        @Test
        @DisplayName("an unequipped template counts itself")
        void unequippedTemplateCountsItself() {
            ItemObject obj = potion(2);
            inventory()[0] = obj;
            gear().insertEnd(obj);

            ObjectGear.ItemObjectAndInt result = packTotal(obj, false, null);

            assertEquals(2, result.number());
            assertSame(obj, result.obj());
        }

        @Test
        @DisplayName("an equipped template excludes itself")
        void equippedTemplateExcludesItself() {
            ItemObject obj = potion(2);
            gear().insertEnd(obj);
            equip(obj);

            ObjectGear.ItemObjectAndInt result = packTotal(obj, false, null);

            assertEquals(0, result.number());
            assertNull(result.obj());
        }
    }

    /**
     * The {@code ignoreInscrip} switch, which chooses between {@link ItemObject#similar}
     * (inscriptions ignored) and {@link ItemObject#objectStackable} (inscriptions considered) - the
     * one branch inside {@code objectPackTotal} itself that is not exercised by the aggregation or
     * self-match cases.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("ignoreInscrip")
    class IgnoreInscription {

        @Test
        @DisplayName("differing inscriptions still match when ignoreInscrip is true")
        void differingInscriptionsMatchWhenIgnored() {
            ItemObject obj = potion(1);
            obj.setNote("A");

            ItemObject cursor = potion(2);
            cursor.setNote("B");
            inventory()[0] = cursor;
            gear().insertEnd(cursor);

            ObjectGear.ItemObjectAndInt result = packTotal(obj, true, null);

            assertEquals(2, result.number());
            assertSame(cursor, result.obj());
        }

        @Test
        @DisplayName("differing inscriptions block the match when ignoreInscrip is false")
        void differingInscriptionsBlockWhenConsidered() {
            ItemObject obj = potion(1);
            obj.setNote("A");

            ItemObject cursor = potion(2);
            cursor.setNote("B");
            inventory()[0] = cursor;
            gear().insertEnd(cursor);

            ObjectGear.ItemObjectAndInt result = packTotal(obj, false, null);

            assertEquals(0, result.number());
            assertNull(result.obj());
        }
    }

    /**
     * The label-based tie-break that decides which matching stack is reported as {@code first},
     * independent of gear-list order.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("label tie-break")
    class LabelTieBreak {

        /**
         * The gear list holds the 'b'-labelled stack before the 'a'-labelled one, so a search that
         * stopped comparing after its first match - the shape of the regression this suite guards
         * against - would report 'b' instead.
         */
        @Test
        @DisplayName("the lower pack letter wins even when it is encountered second")
        void lowerLetterWinsRegardlessOfEncounterOrder() {
            ItemObject stackB = potion(2);
            inventory()[1] = stackB;
            gear().insertEnd(stackB);

            ItemObject stackA = potion(3);
            inventory()[0] = stackA;
            gear().insertEnd(stackA);

            ObjectGear.ItemObjectAndInt result = packTotal(potion(1), false, null);

            assertEquals(5, result.number());
            assertSame(stackA, result.obj());
        }

        @Test
        @DisplayName("a quiver digit outranks a pack letter encountered first")
        void quiverDigitOutranksPackLetterFoundFirst() {
            ItemObject packStack = potion(2);
            inventory()[0] = packStack;
            gear().insertEnd(packStack);

            ItemObject quiverStack = potion(3);
            quiver()[0] = quiverStack;
            gear().insertEnd(quiverStack);

            ObjectGear.ItemObjectAndInt result = packTotal(potion(1), false, null);

            assertSame(quiverStack, result.obj());
        }

        @Test
        @DisplayName("a quiver digit outranks a pack letter found later too")
        void quiverDigitOutranksPackLetterFoundLater() {
            ItemObject quiverStack = potion(3);
            quiver()[0] = quiverStack;
            gear().insertEnd(quiverStack);

            ItemObject packStack = potion(2);
            inventory()[0] = packStack;
            gear().insertEnd(packStack);

            ObjectGear.ItemObjectAndInt result = packTotal(potion(1), false, null);

            assertSame(quiverStack, result.obj());
        }
    }

    /**
     * Confirms that {@code first} is reset at the method's own entry rather than trusting whatever
     * the caller passed - the fix for the bug that let an uppercase-labelled, or otherwise
     * unlabelled, first match be silently dropped in favour of the caller's placeholder.
     *
     * <p>{@code invenCarry} always calls with a non-null placeholder ({@code new ItemObject()}),
     * never {@code null}; {@link #firstIsSetRegardlessOfCallersPlaceholder} rebuilds exactly that
     * call shape against a stack that carries no label at all, which is the case the old code got
     * wrong.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("first reset")
    class FirstReset {

        @Test
        @DisplayName("first is found even when it carries no inventory/quiver label")
        void firstIsSetRegardlessOfCallersPlaceholder() {
            ItemObject stack = potion(4);
            // Deliberately left out of both the inventory and quiver arrays, and not equipped, so
            // gearToLabel falls through to '\0' - the label neither the letter nor digit tie-break
            // branch recognises.
            gear().insertEnd(stack);

            ObjectGear.ItemObjectAndInt withPlaceholder = packTotal(potion(1), false, new ItemObject());
            ObjectGear.ItemObjectAndInt withNull = packTotal(potion(1), false, null);

            assertSame(stack, withPlaceholder.obj());
            assertEquals(4, withPlaceholder.number());
            assertSame(stack, withNull.obj());
            assertEquals(4, withNull.number());
        }
    }
}
