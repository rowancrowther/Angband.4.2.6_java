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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link ItemObject#wieldSlot()}, the port of C's {@code wield_slot} ({@code obj-gear.c}).
 *
 * <p>The expected values come from C. {@code wield_slot} switches on six tvals that have a slot of
 * their own, then falls through to {@code tval_is_melee_weapon} (sword, hafted, polearm, digging),
 * {@code tval_is_ring}, {@code tval_is_light}, {@code tval_is_body_armor} (soft, hard and dragon
 * armour) and {@code tval_is_head_armor} (helm, crown), and answers {@code -1} for anything else.
 * Each of those but the last calls {@code slot_by_type(player, type, false)}, which answers the
 * first <em>empty</em> slot of the type, else the first slot of the type, else the body's slot
 * count.
 *
 * <p>The slot a type lands on is named by the humanoid body from {@link SeededPlayerRegistry} and
 * looked up by that name, so the cases do not depend on the order the body declares its slots in.
 *
 * <p>The cases that matter most are the ones about <em>which player</em> is asked. C's
 * {@code wield_slot} reads its {@code player} global at the moment of the call, so an item built
 * before a character exists, or before the character changed, must still be asked about the live
 * one. An {@link ItemObject} captures the player at construction, and {@code wieldSlot} has to
 * refresh that rather than trust it.
 *
 * <p>Class ItemObjectWieldSlotTest coded on 261002.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("ItemObject.wieldSlot")
class ItemObjectWieldSlotTest {

    /**
     * The player that was live before the test, put back afterwards.
     */
    private Player savedPlayer;

    /**
     * The character under test, with the humanoid body.
     */
    private Player player;

    /**
     * Builds an item of the given type.
     *
     * @param tValue the item's type
     * @return the item
     */
    private static ItemObject item(TValue tValue) {
        return ItemFixture.item(tValue).build();
    }

    /**
     * Saves the live player and installs a fresh one with the humanoid body.
     */
    @BeforeEach
    void setUp() {
        savedPlayer = GameState.getPlayer();
        player = new Player();
        player.setBody(SeededPlayerRegistry.humanoidBody());
        GameState.setPlayer(player);
    }

    /**
     * Puts the saved player back.
     */
    @AfterEach
    void tearDown() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * Finds a slot's index in the player's body by its name.
     *
     * @param name the slot's name, as the humanoid body spells it
     * @return the slot's index
     */
    private int indexOf(String name) {
        List<EquipSlot> slots = player.getPlayerBody().getSlots();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getName().equals(name)) return i;
        }
        throw new AssertionError("no slot named " + name);
    }

    /**
     * Every type with a slot of its own or a family predicate, against the slot C sends it to.
     * Each is the first empty slot of its type, and all of the humanoid body's are empty.
     */
    @Test
    @DisplayName("each wearable type goes to the slot of its type")
    void eachWearableTypeGoesToItsSlot() {
        Map<TValue, String> expected = new LinkedHashMap<>();
        expected.put(TValue.TV_BOW, "shooting");
        expected.put(TValue.TV_AMULET, "neck");
        expected.put(TValue.TV_CLOAK, "back");
        expected.put(TValue.TV_SHIELD, "arm");
        expected.put(TValue.TV_GLOVES, "hands");
        expected.put(TValue.TV_BOOTS, "feet");
        expected.put(TValue.TV_SWORD, "weapon");
        expected.put(TValue.TV_HAFTED, "weapon");
        expected.put(TValue.TV_POLEARM, "weapon");
        expected.put(TValue.TV_DIGGING, "weapon");
        expected.put(TValue.TV_RING, "right hand");
        expected.put(TValue.TV_LIGHT, "light");
        expected.put(TValue.TV_SOFT_ARMOR, "body");
        expected.put(TValue.TV_HARD_ARMOR, "body");
        expected.put(TValue.TV_DRAG_ARMOR, "body");
        expected.put(TValue.TV_HELM, "head");
        expected.put(TValue.TV_CROWN, "head");

        for (Map.Entry<TValue, String> entry : expected.entrySet()) {
            assertEquals(indexOf(entry.getValue()), item(entry.getKey()).wieldSlot(),
                    entry.getKey() + " should go to the " + entry.getValue() + " slot");
        }
    }

    /**
     * Types that are carried or used but never worn fall past every test and answer {@code -1}.
     * Ammunition is the one to watch: it is thrown or fired, and C does not give it a slot.
     */
    @Test
    @DisplayName("a type that is never worn answers -1")
    void neverWornAnswersMinusOne() {
        TValue[] notWorn = {TValue.TV_POTION, TValue.TV_SCROLL, TValue.TV_FOOD, TValue.TV_WAND,
                TValue.TV_STAFF, TValue.TV_ROD, TValue.TV_FLASK, TValue.TV_ARROW, TValue.TV_SHOT,
                TValue.TV_CHEST};

        for (TValue tValue : notWorn) {
            assertEquals(-1, item(tValue).wieldSlot(), tValue + " is never worn");
        }
    }

    /**
     * Two ring slots: the first empty one is chosen, so putting on a second ring goes to the other
     * hand, which is the reason {@code slot_by_type} searches at all.
     */
    @Test
    @DisplayName("a ring takes the first empty ring slot")
    void ringTakesFirstEmptyRingSlot() {
        ItemObject ring = item(TValue.TV_RING);

        assertEquals(indexOf("right hand"), ring.wieldSlot());

        player.getPlayerBody().getSlot(indexOf("right hand")).setItem(item(TValue.TV_RING));

        assertEquals(indexOf("left hand"), ring.wieldSlot());
    }

    /**
     * With both ring slots occupied there is no empty one, so C falls back to the first slot of the
     * type: a caller wanting to swap has somewhere to put the new ring.
     */
    @Test
    @DisplayName("a ring with every ring slot full falls back to the first one")
    void ringWithAllSlotsFullFallsBackToFirst() {
        player.getPlayerBody().getSlot(indexOf("right hand")).setItem(item(TValue.TV_RING));
        player.getPlayerBody().getSlot(indexOf("left hand")).setItem(item(TValue.TV_RING));

        assertEquals(indexOf("right hand"), item(TValue.TV_RING).wieldSlot());
    }

    /**
     * A body with no slot of the type an item needs: {@code slot_by_type} runs off the end and
     * answers the body's slot count, one past the last index, which is neither a slot nor C's
     * {@code -1}. The count is what C's own callers test for.
     */
    @Test
    @DisplayName("a type the body has no slot for answers the slot count")
    void bodyWithoutTheSlotAnswersTheSlotCount() {
        List<EquipSlot> slots = new ArrayList<>();
        for (EquipSlot slot : SeededPlayerRegistry.humanoidBody().getSlots()) {
            if (slot.getType() != EquipmentSlotsEnum.EQUIP_AMULET) {
                slots.add(new EquipSlot(slot.getType(), slot.getName()));
            }
        }
        player.setBody(new PlayerBody("no-amulet", slots));

        assertEquals(slots.size(), item(TValue.TV_AMULET).wieldSlot());
        assertEquals(-1, item(TValue.TV_POTION).wieldSlot(), "-1 is still what a never-worn type gets");
    }

    /**
     * The item is built while one player is live and asked about after another has replaced it. C
     * reads its {@code player} global when the function runs, so the answer is the second player's
     * body: here a one-slot body whose only slot is the amulet, which puts the amulet at index 0,
     * where the first player's body puts it elsewhere.
     */
    @Test
    @DisplayName("asks the player who is live now, not the one live when the item was built")
    void asksTheLivePlayer() {
        ItemObject amulet = item(TValue.TV_AMULET);
        int underFirstPlayer = amulet.wieldSlot();

        Player second = new Player();
        second.setBody(new PlayerBody("one slot",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_AMULET, "neck"))));
        GameState.setPlayer(second);

        assertEquals(indexOf("neck"), underFirstPlayer, "sanity: the first body puts the amulet at its neck");
        assertEquals(0, amulet.wieldSlot());
    }

    /**
     * The item is built before any character exists, as a store's stock or a data-file template
     * can be. It holds no player, and still has to answer for the one who arrives afterwards.
     */
    @Test
    @DisplayName("an item built before any player exists is asked about the player who arrives later")
    void itemBuiltBeforeAnyPlayer() {
        GameState.setPlayer(null);
        ItemObject sword = item(TValue.TV_SWORD);

        GameState.setPlayer(player);

        assertEquals(indexOf("weapon"), sword.wieldSlot());
    }

    /**
     * With no character at all the slot lookup has no body to search. C would crash on its null
     * global; the port throws a {@link NullPointerException}, which is what the Javadoc promises.
     * A type that is never worn never reaches the lookup, so it still answers {@code -1}.
     */
    @Test
    @DisplayName("with no player a wearable type throws, and a never-worn type still answers -1")
    void noPlayerAtAll() {
        ItemObject sword = item(TValue.TV_SWORD);
        ItemObject potion = item(TValue.TV_POTION);

        GameState.setPlayer(null);

        assertThrows(NullPointerException.class, sword::wieldSlot);
        assertEquals(-1, potion.wieldSlot());
    }
}
