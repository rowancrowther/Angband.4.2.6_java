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

import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.Collections;
import java.util.List;

/**
 * A body layout — a named, ordered set of equipment slots a creature presents.
 *
 * <p>Ports the C {@code struct player_body} ({@code player.h}), defined by {@code body.txt}: a body
 * name followed by its slots in declared order. This is the immutable <em>template</em>; the runtime
 * occupant of each slot (C's {@code equip_slot.obj}) is per-player state and lives elsewhere, so it
 * is deliberately absent here — at character birth C copies the template and allocates a fresh slot
 * array to hold the worn items.
 *
 * <p><b>Slots are index-addressed.</b> The whole gear system reaches equipment by slot number
 * ({@code slots[i]}); name and type are only lookup keys that resolve to an index (C's
 * {@code slot_by_name} / {@code slot_by_type}). So the slots are held as an ordered {@link List} and
 * position is significant — e.g. the two {@code RING} slots ("right hand", "left hand") are distinct
 * only by their index and name.
 *
 * @author Rowan Crowther
 */
public class PlayerBody {

    /**
     * Display name of this body layout, e.g. {@code "Humanoid"} (C: {@code player_body.name}).
     */
    private final String name;

    /**
     * The equipment slots in declared (body) order; a slot's index is its identity.
     */
    private final List<EquipSlot> slots;

    /**
     * Creates an immutable body template.
     *
     * @param name  the body's display name
     * @param slots the equipment slots in declared order (defensively copied and kept immutable)
     * @author Rowan Crowther
     */
    public PlayerBody(String name, List<EquipSlot> slots) {
        this.name = name;
        this.slots = List.copyOf(slots);
    }

    /**
     * Tests whether a given item is currently worn in one of this body's equipment slots — the port
     * of C's {@code object_is_equipped} ({@code obj-gear.c}). Empty slots are skipped; the match is
     * by object identity.
     *
     * <p>Worn is a different question from carried. An object in an equipment slot is still on the
     * gear list, so {@link uk.co.jackoftrades.middle.player.Player#isCarried} answers {@code true}
     * for it too; this narrows that to the objects actually in use, which is what decides whether an
     * object is labelled by its slot or by its position in the pack.
     *
     * <p>Function itemIsEquipped commented in full on 260816.
     *
     * @param item the item to look for
     * @return {@code true} if the item occupies one of this body's slots
     * @author Rowan Crowther
     */
    public boolean itemIsEquipped(ItemObject item) {
        for (EquipSlot slot : slots) {
            if (slot.getItem() == null) continue;

            if (slot.getItem().equals(item)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return this body's display name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return the number of equipment slots this body provides (C: {@code player_body.count})
     * @author Rowan Crowther
     */
    public int getCount() {
        return slots.size();
    }

    /**
     * @return the slots in body order (unmodifiable)
     * @author Rowan Crowther
     */
    public List<EquipSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    /**
     * @param index the slot's position, its C {@code slots[i]} address
     * @return the slot at the given index
     * @author Rowan Crowther
     */
    public EquipSlot getSlot(int index) {
        return slots.get(index);
    }

    /**
     * Finds which equipment slot an item is worn in, the port of C's {@code equipped_item_slot}
     * ({@code obj-gear.c}).
     *
     * <p>The return for "not worn" is {@link #getCount()} — one past the last slot — rather than
     * {@code -1}. That follows C, whose loop simply runs off the end and hands back the index it
     * stopped at, and callers test for it explicitly: {@code obj-gear.c:1040} guards with
     * {@code if (slot == player->body.count) return;}. A null item takes the same exit, which is
     * why the size is returned up front rather than falling through the loop.
     *
     * <p>The reason it is an index and not a slot is that the index <em>is</em> the answer the
     * callers want: {@link uk.co.jackoftrades.middle.player.Player#gearToLabel} uses it to
     * subscript the equipment label string, so an item's slot position becomes the letter the
     * player selects it by.
     *
     * <p>Matching is by identity, {@link ItemObject} declaring no {@code equals} of its own, which
     * is what C's pointer comparison means. Two identical swords are two different objects and only
     * the one actually worn is found.
     *
     * <p>Function equippedItemSlot coded before 260817, commented in full on 260817.
     *
     * @param item the item to locate, or {@code null}
     * @return the slot's index, or {@link #getCount()} if the item is not worn
     * @author Rowan Crowther
     */
    public int equippedItemSlot(ItemObject item) {
        if (item == null) return slots.size();

        int index = 0;
        for (EquipSlot slot : slots) {
            if (slot.getItem() != null && slot.getItem().equals(item))
                return index;
            index++;
        }

        return index;
    }
}