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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth}'s {@code wieldAll}, the port of C's {@code wield_all}
 * ({@code player-birth.c:463}).
 *
 * <p>{@code wieldAll} is private, as C's is {@code static}, so it is reached by reflection. Every
 * item is built through {@link ItemFixture} and dispatched through the real {@link ItemObject#wieldSlot()}
 * — which reads {@link ObjectUtils#slotByType} against whatever body the test installs — rather than
 * a stub, because the boundary case this suite exists to lock in ({@link #skipsAnItemWithNoMatchingSlotType})
 * is exactly the value {@code slotByType} answers when a body has no slot of a type: its own
 * fallback is {@code p->body.count}'s Java equivalent, {@code getSlots().size()}, and that is only
 * ever reachable through the real method.
 *
 * <p>Two fixtures matter for every case here. First, {@link GameState#setPlayer} must run before an
 * item is built, because every {@link ItemObject} constructor reads the static player field from
 * {@code GameState.getPlayer()} at construction time — build the item first and {@code wieldSlot()}
 * asks the wrong player's body. Second, {@code TV_SWORD}, {@code TV_LIGHT}, {@code TV_AMULET} and
 * {@code TV_POTION} are chosen deliberately: none of them can have charges or a timeout
 * ({@code TValue.canHaveCharges}/{@code canHaveTimeout}), so the stack-splitting path's charge
 * distribution never touches a {@code Random} the fixture leaves unset.
 *
 * <p>The stack-splitting case additionally needs a known half ({@code fullyKnown()}): C's
 * {@code wield_all} inserts {@code new->known} into the known pile unconditionally, with no null
 * check, and so does the port ({@code PlayerBirth.java:861}) — safe here because
 * {@code player_outfit} always attaches a known half before {@code wield_all} runs, an invariant a
 * test exercising the split branch has to respect rather than paper over. Every other case leaves
 * the known half unset, since {@code objectLearnOnWield} tolerates a missing one by logging and
 * returning ({@code PlayerKnowledge.java:1886-1890}).
 *
 * <p>{@link #splitsAStackAndKeepsTheRemainderInGear} also stands as the regression test for a bug
 * found while writing this suite and fixed separately: {@code Pile.insertEnd(Pile)} used to insert
 * each carried-over item without first clearing the ownership {@code wieldAll} had just given it in
 * the scratch pile, so the merge at {@code PlayerBirth.java:874-875} threw on every stack split. This
 * suite does not test {@code Pile} itself — only that {@code wieldAll}'s own call to it now
 * completes.
 *
 * <p>Neither C nor the port removes a worn item from the gear list — {@code p->body.slots[slot].obj}
 * and the gear pointer both end up referring to the same object — so every case here that wears
 * something also checks it is still reachable through {@link Player#getGear()} afterwards.
 *
 * <p>Class PlayerBirthWieldAllTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthWieldAllTest {

    /**
     * The live player installed before this class ran, restored afterwards so a later class does
     * not inherit this suite's player.
     */
    private Player savedPlayer;

    /**
     * The character under test, fresh for each case.
     */
    private Player player;

    /**
     * Finds a slot's position in a body by its type, so a test does not depend on
     * {@link SeededPlayerRegistry#humanoidBody()}'s declared order.
     *
     * @param body the body to search
     * @param type the slot type to find
     * @return the slot's index
     */
    private static int indexOfType(PlayerBody body, EquipmentSlotsEnum type) {
        List<EquipSlot> slots = body.getSlots();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getType() == type) return i;
        }
        throw new AssertionError("no " + type + " slot in " + body.getName());
    }

    /**
     * The humanoid body with every slot of one type removed — used for the case where an item's
     * type has nowhere to go at all.
     *
     * @param missing the slot type to omit
     * @return a body one slot shorter than the humanoid one
     */
    private static PlayerBody bodyWithout(EquipmentSlotsEnum missing) {
        List<EquipSlot> slots = new ArrayList<>();
        for (EquipSlot slot : SeededPlayerRegistry.humanoidBody().getSlots()) {
            if (slot.getType() != missing) {
                slots.add(new EquipSlot(slot.getType(), slot.getName()));
            }
        }
        return new PlayerBody("no-" + missing, slots);
    }

    /**
     * A body with exactly one slot, for the case where two items compete for it within the same
     * scan.
     *
     * @param type the one slot's type
     * @return a single-slot body
     */
    private static PlayerBody singleSlotBody(EquipmentSlotsEnum type) {
        return new PlayerBody("single", List.of(new EquipSlot(type, "slot")));
    }

    /**
     * A plain sword — a melee weapon, so {@code wieldSlot} resolves it through
     * {@code EQUIP_WEAPON}.
     *
     * @return the sword
     */
    private static ItemObject sword() {
        return ItemFixture.item(TValue.TV_SWORD).build();
    }

    /**
     * A plain amulet — resolved through {@code EQUIP_AMULET}.
     *
     * @return the amulet
     */
    private static ItemObject amulet() {
        return ItemFixture.item(TValue.TV_AMULET).build();
    }

    /**
     * A potion — no case in {@code wieldSlot}'s switch or predicates answers it, so it always comes
     * back {@code -1}.
     *
     * @return the potion
     */
    private static ItemObject potion() {
        return ItemFixture.item(TValue.TV_POTION).build();
    }

    /**
     * A stack of light sources, fully known — the known half is what the split branch needs to
     * carry over without a null check tripping it.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject lights(int number) {
        return ItemFixture.item(TValue.TV_LIGHT).number(number).fullyKnown().build();
    }

    /**
     * Saves the live player and builds a fresh one with the humanoid body, installing it as current
     * before any item in the test is constructed.
     */
    @BeforeEach
    void setUp() {
        savedPlayer = GameState.getPlayer();
        player = new Player();
        player.setBody(SeededPlayerRegistry.humanoidBody());
        GameState.setPlayer(player);
    }

    /**
     * Restores the player that was live before this test.
     */
    @AfterEach
    void tearDown() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * Invokes the private {@code PlayerBirth.wieldAll(Player)} by reflection, unwrapping the
     * {@link InvocationTargetException} JUnit would otherwise report instead of the real failure.
     *
     * @throws Exception if the method cannot be reached, or rethrows whatever it threw
     */
    private void wieldAll() throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("wieldAll", Player.class);
        method.setAccessible(true);
        try {
            method.invoke(null, player);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    /**
     * The ordinary path: one wieldable item, one empty matching slot.
     */
    @Test
    @DisplayName("wears a wieldable item into its slot")
    void wearsAWieldableItemIntoItsSlot() throws Exception {
        ItemObject sword = sword();
        player.getGear().insertEnd(sword);
        int weaponSlot = indexOfType(player.getPlayerBody(), EquipmentSlotsEnum.EQUIP_WEAPON);

        wieldAll();

        assertSame(sword, player.getPlayerBody().getSlot(weaponSlot).getItem());
        assertEquals(1, player.getPlayerUpkeep().getEquipCount());
        assertTrue(player.getGear().contains(sword));
    }

    /**
     * {@code wieldSlot} answers {@code -1} for anything with no slot at all — C's
     * {@code slot < 0} branch. Nothing should be worn, and the item stays exactly where it was.
     */
    @Test
    @DisplayName("leaves an unwieldable item in gear untouched")
    void leavesAnUnwieldableItemInGear() throws Exception {
        ItemObject potion = potion();
        player.getGear().insertEnd(potion);

        assertEquals(-1, potion.wieldSlot());

        wieldAll();

        assertEquals(0, player.getPlayerUpkeep().getEquipCount());
        assertTrue(player.getGear().contains(potion));
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            assertNull(slot.getItem());
        }
    }

    /**
     * A slot already holding something is left alone — C's {@code obj_temp} check. The occupant
     * predates the call (as a previously-worn item would), and the newcomer is left unequipped
     * rather than displacing it.
     */
    @Test
    @DisplayName("skips a slot that is already occupied")
    void skipsAnAlreadyOccupiedSlot() throws Exception {
        ItemObject oldSword = sword();
        int weaponSlot = indexOfType(player.getPlayerBody(), EquipmentSlotsEnum.EQUIP_WEAPON);
        player.getPlayerBody().getSlot(weaponSlot).setItem(oldSword);
        player.getGear().insertEnd(oldSword);

        ItemObject newSword = sword();
        player.getGear().insertEnd(newSword);

        wieldAll();

        assertSame(oldSword, player.getPlayerBody().getSlot(weaponSlot).getItem());
        assertEquals(0, player.getPlayerUpkeep().getEquipCount());
        assertTrue(player.getGear().contains(newSword));
    }

    /**
     * Two items of the same wieldable type in a body with only one matching slot: the first scanned
     * takes it, and by the time the second is scanned the slot the first just filled is what
     * {@code wieldSlot} answers for it too — occupied, so it is skipped in the same pass rather than
     * from a snapshot taken before the loop started.
     */
    @Test
    @DisplayName("only one of two competing items ends up worn")
    void onlyOneOfTwoCompetingItemsEndsUpWorn() throws Exception {
        player.setBody(singleSlotBody(EquipmentSlotsEnum.EQUIP_WEAPON));
        ItemObject first = sword();
        ItemObject second = sword();
        player.getGear().insertEnd(first);
        player.getGear().insertEnd(second);

        wieldAll();

        ItemObject worn = player.getPlayerBody().getSlot(0).getItem();
        assertTrue(worn == first || worn == second);
        assertEquals(1, player.getPlayerUpkeep().getEquipCount());
        assertTrue(player.getGear().contains(first));
        assertTrue(player.getGear().contains(second));
    }

    /**
     * The boundary this port's fix addresses. With no {@code EQUIP_AMULET} slot on the body at all,
     * {@code slotByType}'s fallback is {@code getSlots().size()} — one past the last valid index —
     * and {@code wieldSlot} answers exactly that. The fixed guard is {@code >=}; the bug it replaced
     * was {@code >}, which let this value through to
     * {@code getSlot(slotNum)} and threw {@link IndexOutOfBoundsException}.
     */
    @Test
    @DisplayName("skips an item whose type has no slot at all, without throwing")
    void skipsAnItemWithNoMatchingSlotType() throws Exception {
        PlayerBody bodyWithoutAmulet = bodyWithout(EquipmentSlotsEnum.EQUIP_AMULET);
        player.setBody(bodyWithoutAmulet);

        ItemObject amulet = amulet();
        assertEquals(bodyWithoutAmulet.getSlots().size(), amulet.wieldSlot());

        player.getGear().insertEnd(amulet);

        assertDoesNotThrow(this::wieldAll);

        assertEquals(0, player.getPlayerUpkeep().getEquipCount());
        assertTrue(player.getGear().contains(amulet));
        for (EquipSlot slot : bodyWithoutAmulet.getSlots()) {
            assertNull(slot.getItem());
        }
    }

    /**
     * A stack bigger than one splits: one copy is worn, and the rest is carried over to the gear
     * pile rather than vanishing. This is also the regression test for the fixed
     * {@code Pile.insertEnd(Pile)} — before the fix, this merge threw on every call, so a run that
     * completes and leaves the remainder findable is the property being pinned down.
     */
    @Test
    @DisplayName("splits a stack, wears one, and keeps the remainder in gear")
    void splitsAStackAndKeepsTheRemainderInGear() throws Exception {
        ItemObject stack = lights(3);
        player.getGear().insertEnd(stack);
        player.getGearKnown().insertEnd(stack.getKnown());
        int lightSlot = indexOfType(player.getPlayerBody(), EquipmentSlotsEnum.EQUIP_LIGHT);

        assertDoesNotThrow(this::wieldAll);

        assertSame(stack, player.getPlayerBody().getSlot(lightSlot).getItem());
        assertEquals(1, stack.getNumber());
        assertEquals(1, player.getPlayerUpkeep().getEquipCount());

        ItemObject remainder = null;
        for (var it = player.getGear().getIterator(); it.hasNext(); ) {
            ItemObject candidate = it.next();
            if (candidate != stack) {
                remainder = candidate;
            }
        }

        assertNotNull(remainder, "the split-off remainder should have been merged back into gear");
        assertEquals(2, remainder.getNumber());
        assertNotNull(remainder.getKnown());
        assertEquals(2, remainder.getKnown().getNumber());
        assertFalse(player.getGear().contains(remainder) && remainder == stack);
    }
}
