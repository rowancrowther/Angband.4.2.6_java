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

import uk.co.jackoftrades.backend.parser.body.BodyParser.BodySlot;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A body layout — a named arrangement of equipment slots a creature (the player, or a
 * shapechanged form) presents, together with what currently occupies each slot.
 *
 * <p>Ports the C {@code struct player_body} ({@code player.h}), defined by {@code body.txt}.
 * A body is essentially "the shape of a character's equipment": a humanoid exposes a weapon
 * slot, a bow slot, two ring fingers, an amulet, a light and several armour slots, while an
 * alternate form may differ. Modelling the body as data lets a shapechange swap the entire
 * slot layout at once.
 *
 * @author Rowan Crowther
 */
public class PlayerBody {
    /**
     * Identity of a single slot within this body: the slot's category plus its display name.
     *
     * <p>Used as the map key so a body can hold several slots of the same category (e.g. two
     * ring fingers) that remain individually addressable.
     *
     * @param equipmentSlot the slot category
     * @param name          the slot's display label
     */
    private record BodySlotEntry(EquipmentSlotsEnum equipmentSlot, String name) {
    }

    /**
     * Display name of this body layout, e.g. {@code "Humanoid"} (C: {@code player_body.name}).
     */
    private String name;
    /** Number of equipment slots this body provides (C: {@code player_body.count}). */
    private int count;
    /** Each slot mapped to the item occupying it; insertion-ordered so slots stay in declared order. */
    private Map<BodySlotEntry, ItemObject> equipmentSlots;

    /**
     * Builds a body from the parser's slot map, translating each parser {@code BodySlot} into an
     * internal {@link BodySlotEntry} key.
     *
     * <p>A {@link LinkedHashMap} is used deliberately: equipment is displayed in body order, so
     * the slots' iteration order is significant rather than incidental.
     *
     * @param name           the body's display name
     * @param count          the number of slots
     * @param equipmentSlots the slots (as parsed) mapped to their initial occupants
     */
    public PlayerBody(String name, int count, Map<BodySlot, ItemObject> equipmentSlots) {
        this.name = name;
        this.count = count;
        this.equipmentSlots = new LinkedHashMap<>();
        for (BodySlot bodySlot : equipmentSlots.keySet()) {
            BodySlotEntry bodySlotEntry = new BodySlotEntry(bodySlot.slotType(), bodySlot.name());
            this.equipmentSlots.put(bodySlotEntry, equipmentSlots.get(bodySlot));
        }
    }

    /**
     * @return the number of equipment slots this body provides
     */
    public int getCount() {
        return count;
    }

    /**
     * Reports whether a given item is currently equipped in any slot of this body.
     *
     * @param item the item to look for
     * @return {@code true} if the item occupies one of the slots
     */
    public boolean isEquipped(ItemObject item) {
        return equipmentSlots.containsValue(item);
    }
}
