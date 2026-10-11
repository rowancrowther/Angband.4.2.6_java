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

import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;

/**
 * One equipment slot in a {@link PlayerBody} — a wearable position (weapon, body armour, a ring
 * finger, …) that may hold a single item. The Java port of the C original's {@code struct
 * equip_slot} ({@code player.h}).
 *
 * <p>C's struct has four fields: {@code next}, {@code type}, {@code name} and {@code obj}. The
 * port keeps the last three. {@code next} is how C chains a body's slots while {@code body.txt}
 * is being parsed (and it is later re-pointed along a flat array by {@code finish_parse_body} in
 * {@code init.c}); here a body holds its slots in a {@link java.util.List}, so no link is needed.
 * A slot is created empty, as C's {@code mem_zalloc} leaves {@code obj} {@code NULL}: the
 * parser creates the slots of a body template, and {@code player_embody} in {@code
 * player-birth.c} makes the player's own copies, which {@link PlayerBody#copy()} mirrors.
 *
 * <p>The type and the name are fixed for the slot's life and the item is the only part that
 * changes, so there is a setter for the item alone. Slots are compared and found by identity, as
 * C compares {@code obj} pointers, so this class does not override {@code equals}.
 *
 * <p>Class EquipSlot coded before 260815, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class EquipSlot {
    /**
     * The kind of slot this is, C's {@code uint16_t type} holding a value of the {@code EQUIP_*}
     * enumeration in {@code list-equip-slots.h}. Several slots of one body may share a type: the
     * standard body has two {@link EquipmentSlotsEnum#EQUIP_RING} slots. It is what code asks
     * for when it wants "the weapon slot" ({@code slot_type_is} in {@code obj-gear.c}), and it
     * selects the row of {@code slot_table} that supplies the slot's wording.
     *
     * <p>Field type coded before 260815, commented in full on 261011.
     */
    private EquipmentSlotsEnum type;
    /**
     * The slot's own name, C's {@code char *name}: the name given on the {@code slot:} line of
     * {@code body.txt}, such as {@code "weapon"}, {@code "left hand"} or {@code "body"}. It is not
     * the wording shown to the player. That comes from the slot's type, and the name is only
     * substituted into it for types whose {@code name_in_desc} flag is set, giving "On left hand"
     * ({@code equip_mention} and {@code equip_describe} in {@code obj-gear.c}).
     *
     * <p>Field name coded before 260815, commented in full on 261011.
     */
    private String name;
    /**
     * The item currently worn in this slot, C's {@code struct object *obj}, or {@code null} if the
     * slot is empty, as C's {@code NULL}. The slot only refers to the item, as C's pointer does: in C
     * the equipped object is the same object that the player's gear list holds, so a slot is never
     * given a copy.
     *
     * <p>Field item coded before 260815, commented in full on 261011.
     */
    private ItemObject item;

    /**
     * Creates an empty equipment slot of the given kind. C has no constructor: the slot is
     * zero-filled memory and {@code type} and {@code name} are then assigned, by
     * {@code parse_body_slot} in {@code init.c} for a body template and by {@code player_embody}
     * in {@code player-birth.c} for the player's copy. The port does both assignments here and
     * leaves {@code item} {@code null}, which is C's zero fill.
     *
     * <p>Constructor EquipSlot coded before 260815, commented in full on 261011.
     *
     * @param type the slot kind (body position)
     * @param name the slot's own name from {@code body.txt}, such as {@code "left hand"}
     */
    public EquipSlot(EquipmentSlotsEnum type, String name) {
        this.type = type;
        this.name = name;
        item = null;
    }

    /**
     * Returns the item worn in this slot, C's {@code slot.obj}. Callers must test for
     * {@code null} where C tests {@code obj != NULL}: an empty slot is the ordinary case, not an
     * error.
     *
     * <p>Method getItem coded before 260815, commented in full on 261011.
     *
     * @return the item currently worn in this slot, or {@code null} if the slot is empty
     */
    public ItemObject getItem() {
        return item;
    }

    /**
     * Returns the kind of slot this is, C's {@code slot.type}. The kind never changes after
     * construction.
     *
     * <p>Method getType coded before 260815, commented in full on 261011.
     *
     * @return the kind of slot this is (its body position)
     */
    public EquipmentSlotsEnum getType() {
        return type;
    }

    /**
     * Returns the slot's own name from {@code body.txt}, C's {@code slot.name}, such as
     * {@code "left hand"}. This is not the wording shown to the player; see the {@code name}
     * field. The name never changes after construction.
     *
     * <p>Method getName coded before 260815, commented in full on 261011.
     *
     * @return the slot's own name, as written on its {@code slot:} line
     */
    public String getName() {
        return name;
    }

    /**
     * Puts an item in this slot, or empties it if {@code obj} is {@code null}. The C original has
     * no equivalent function - {@code obj} is a plain field on {@code struct equip_slot}
     * ({@code player.h}), and every caller that changes what a slot holds (wielding, taking
     * off, birth equipping, and so on) writes it directly, e.g.
     * {@code player->body.slots[slot].obj = wielded;} in {@code inven_wield()}, {@code obj-gear.c}, and {@code = NULL} in {@code inven_takeoff()}.
     * This setter is the port's encapsulation of that same direct field write; it carries no extra
     * bookkeeping of its own; freeing the outgoing item, updating carried weight, and the like
     * remain the caller's responsibility in both versions.
     *
     * <p>Method setItem coded on 260905, commented in full on 261011.
     *
     * @param obj the item to place in this slot, or {@code null} to empty it
     */
    public void setItem(ItemObject obj) {
        this.item = obj;
    }
}
