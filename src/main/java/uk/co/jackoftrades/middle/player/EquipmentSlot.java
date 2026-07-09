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

/**
 * One equipment slot on a player's body — a place an item can be worn or wielded
 * (weapon, bow, a specific ring finger, amulet, light source, armour piece, …).
 *
 * <p>Ports the C {@code struct equip_slot} ({@code player.h}): a {@code type} (which
 * kind of slot), a {@code name} (its display label, e.g. "Wielding" or "On left
 * hand"), and the object currently occupying it. A player's full equipment is an
 * ordered set of these, described per body by {@link PlayerBody} and ultimately by
 * {@code body.txt}.
 *
 * <p><b>Why slots are first-class objects:</b> Angband bodies are data-driven — a
 * normal humanoid and a shapechanged form can expose different slot layouts — so the
 * available slots cannot be a fixed list of fields on the player. Modelling each slot
 * as its own object lets a body declare any arrangement it likes.
 *
 * <p><b>Status:</b> currently a field-only stub; constructors and accessors are still
 * to be ported.
 *
 * @author Rowan Crowther
 */
public class EquipmentSlot {
    /**
     * Human-readable label for the slot, e.g. {@code "Wielding"} (C: {@code equip_slot.name}).
     */
    private String name;
    /** Which kind of slot this is (C: {@code equip_slot.type}); identifies the slot category. */
    private int type;
    /** The item currently equipped here, or {@code null} when the slot is empty (C: {@code equip_slot.obj}). */
    private ItemObject item;


}
