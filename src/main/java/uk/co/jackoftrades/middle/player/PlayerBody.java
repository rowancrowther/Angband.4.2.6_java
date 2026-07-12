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
        return slots;
    }

    /**
     * @param index the slot's position, its C {@code slots[i]} address
     * @return the slot at the given index
     * @author Rowan Crowther
     */
    public EquipSlot getSlot(int index) {
        return slots.get(index);
    }
}