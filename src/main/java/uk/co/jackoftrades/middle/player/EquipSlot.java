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
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;

/**
 * One equipment slot in a {@link PlayerBody} — a wearable position (weapon, body armour, a ring
 * finger, …) that may hold a single item. The Java port of the C original's {@code struct
 * equip_slot} ({@code player.h}).
 *
 * @author Rowan Crowther
 */
public class EquipSlot {
    /**
     * The kind of slot this is (which body position it represents).
     */
    private EquipmentSlotsEnum type;
    /** The slot's display name, e.g. {@code "wielding"} or {@code "on left hand"}. */
    private String name;
    /** The item currently worn in this slot, or {@code null} if empty. */
    private ItemObject item;

    /**
     * Creates an empty equipment slot of the given kind.
     *
     * @param type the slot kind (body position)
     * @param name the slot's display name
     */
    public EquipSlot(EquipmentSlotsEnum type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the item currently worn in this slot, or {@code null} if the slot is empty
     */
    public ItemObject getItem() {
        return item;
    }

    /**
     * @return the kind of slot this is (its body position)
     */
    public EquipmentSlotsEnum getType() {
        return type;
    }

    /**
     * @return the slot's display name
     */
    public String getName() {
        return name;
    }
}
