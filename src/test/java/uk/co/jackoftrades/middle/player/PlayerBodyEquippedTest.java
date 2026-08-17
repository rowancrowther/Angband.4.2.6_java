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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBody#itemIsEquipped}, the port of C's {@code object_is_equipped}
 * ({@code obj-gear.c}).
 *
 * <p><b>The empty-slot skip is the whole reason this needs a test.</b> C indexes an array of slot
 * structs and compares {@code slot->obj == obj}, where an empty slot holds a null and the comparison
 * is harmless. The port has to reach through {@link EquipSlot#getItem} to make the same comparison,
 * so the null it would otherwise dereference has to be stepped over explicitly. A body with a gap in
 * it — which is every body the player has, until they are fully kitted out — is therefore the normal
 * case rather than an edge one, and most of the cases below put the gap somewhere different.
 *
 * <p>{@link EquipSlot} has no setter for its item, so the fixture writes the field directly. That is
 * a statement about the class rather than about this test: slots are filled by the wield code, which
 * is not yet ported.
 *
 * <p>Class PlayerBodyEquippedTest coded on 260816, commented in full on 260816.
 *
 * @author Rowan Crowther
 */
class PlayerBodyEquippedTest {

    /**
     * Fills a slot, standing in for the wield code that would normally do it.
     *
     * @author Rowan Crowther
     */
    private static void wear(EquipSlot slot, ItemObject item) throws Exception {
        Field f = EquipSlot.class.getDeclaredField("item");
        f.setAccessible(true);
        f.set(slot, item);
    }

    /**
     * An empty slot of an arbitrary kind. Which kind it is never matters here — the method walks
     * every slot without consulting its type.
     *
     * @author Rowan Crowther
     */
    private static EquipSlot emptySlot() {
        return new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "wielding");
    }

    /**
     * @author Rowan Crowther
     */
    private static PlayerBody bodyOf(EquipSlot... slots) {
        return new PlayerBody("test", new ArrayList<>(List.of(slots)));
    }

    /**
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("an item in a slot is equipped")
    void wornItemIsEquipped() throws Exception {
        ItemObject sword = new ItemObject();
        EquipSlot slot = emptySlot();
        wear(slot, sword);

        assertTrue(bodyOf(slot).itemIsEquipped(sword));
    }

    /**
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("an item in no slot is not equipped")
    void unwornItemIsNotEquipped() throws Exception {
        EquipSlot slot = emptySlot();
        wear(slot, new ItemObject());

        assertFalse(bodyOf(slot).itemIsEquipped(new ItemObject()));
    }

    /**
     * The gap before the match. Reaching the item at all means the null in the first slot was
     * survived, which is the guard C does not need.
     *
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("an empty slot before the match is stepped over")
    void emptySlotBeforeMatchIsSkipped() throws Exception {
        ItemObject shield = new ItemObject();
        EquipSlot empty = emptySlot();
        EquipSlot filled = emptySlot();
        wear(filled, shield);

        assertTrue(bodyOf(empty, filled).itemIsEquipped(shield));
    }

    /**
     * The gap after the match, and the gap with no match at all. The second is the case that would
     * throw rather than answer if the guard were missing: the walk runs to the end without finding
     * anything, so every null in the body is dereferenced.
     *
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("a body of empty slots answers no rather than throwing")
    void whollyEmptyBodyIsSurvivable() {
        PlayerBody body = bodyOf(emptySlot(), emptySlot(), emptySlot());

        assertDoesNotThrow(() -> body.itemIsEquipped(new ItemObject()));
        assertFalse(body.itemIsEquipped(new ItemObject()));
    }

    /**
     * A body with no slots at all — not a state the game produces, but the loop's degenerate case
     * and free to check.
     *
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("a body with no slots equips nothing")
    void slotlessBodyEquipsNothing() {
        assertFalse(bodyOf().itemIsEquipped(new ItemObject()));
    }

    /**
     * Every filled slot is searched, not merely the first. C loops to {@code body->count}, and a
     * match in the last slot of a full body is the case that says the port does too.
     *
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("a match in the last slot is found")
    void matchInLastSlotIsFound() throws Exception {
        ItemObject ring = new ItemObject();
        EquipSlot first = emptySlot();
        EquipSlot second = emptySlot();
        EquipSlot third = emptySlot();
        wear(first, new ItemObject());
        wear(second, new ItemObject());
        wear(third, ring);

        assertTrue(bodyOf(first, second, third).itemIsEquipped(ring));
    }

    /**
     * Two objects alike in every field are still distinguished, as on the floor — see
     * {@code SquareHoldsObjectTest}. A player wearing one of two identical rings has one equipped and
     * one in the pack, and the difference matters when the pack one is dropped.
     *
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("an identical object that is not the worn one is not equipped")
    void identicalButDistinctObjectIsNotEquipped() throws Exception {
        ItemObject worn = new ItemObject();
        ItemObject itsTwin = new ItemObject();
        EquipSlot slot = emptySlot();
        wear(slot, worn);
        PlayerBody body = bodyOf(slot);

        assertTrue(body.itemIsEquipped(worn));
        assertFalse(body.itemIsEquipped(itsTwin));
    }
}
