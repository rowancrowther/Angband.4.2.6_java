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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.util.ArrayList;
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
     * Logger for this class, used to report a body built without slots before the constructor
     * throws. C has no equivalent: its bodies come from a parser that has already rejected a
     * malformed {@code body.txt}, and a body with no slots is simply an array of length zero.
     */
    private final static Logger logger = LogManager.getLogger(PlayerBody.class);
    
    /**
     * Display name of this body layout, e.g. {@code "Humanoid"} (C: {@code player_body.name}).
     */
    private String name;

    /**
     * The equipment slots in declared (body) order; a slot's index is its identity.
     */
    private final List<EquipSlot> slots;

    /**
     * Creates an immutable body template.
     *
     * @param name  the body's display name
     * @param slots the equipment slots in declared order (defensively copied and kept immutable)
     */
    public PlayerBody(String name, List<EquipSlot> slots) {
        this.name = name;
        this.slots = new ArrayList<>();
        if (slots == null || slots.isEmpty()) {
            logger.fatal("Invalid slot list. Slot list is null or empty.");
            throw new IllegalArgumentException("Invalid slot list. Slot list is null or empty.");
        } else {
            this.slots.addAll(slots);
        }
    }

    /**
     * Copies this body plan into a new, unworn one — the port of the copying half of C's
     * {@code player_embody} ({@code player-birth.c:369}).
     *
     * <p>C builds the player's body from the race's template in two steps: a {@code memcpy} of the
     * whole {@code player_body} struct to carry the name and the slot count across, then a freshly
     * allocated slot array whose entries take their type and name from the template one at a time.
     * The allocation is {@code mem_zalloc}, so every slot's {@code obj} starts null. This method is
     * both steps at once, which is why {@code embody} is a single assignment.
     *
     * <p><b>The copy is unworn.</b> Each slot is rebuilt through
     * {@link EquipSlot#EquipSlot(uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum, String)}, which takes a type and a name and
     * nothing else, so any item held in the source's slots is not carried over. That is the
     * behaviour {@code player_embody} needs and the one thing a caller cannot read off the
     * signature: this is "copy the plan", not "copy the body as it stands".
     *
     * <p><b>Nothing is shared with the source.</b> The slot list is new and so is every
     * {@link EquipSlot} in it, which is the point of the exercise — the source is normally a race's
     * template, held once in {@link uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry}
     * and shared by every player of that race. A copy that shared its slots would have one
     * character wearing another's equipment, and would let a player write into the registry's own
     * data. C gets that for free from {@code memcpy} plus a separate allocation; here it has to be
     * done deliberately.
     *
     * <p>The name needs no copying of its own, {@link String} being immutable, where C calls
     * {@code string_make} to take a copy it can free with the player.
     *
     * <p>Named {@code copy} rather than {@code clone} deliberately. {@link Object#clone} carries a
     * protocol — {@link Cloneable}, a checked exception, and a field-by-field shallow copy from
     * {@code super.clone()} — that this class has no use for, since every field here needs handling
     * of its own. It also matches {@code ElementInfo.copy}, the same decision taken elsewhere in
     * the port.
     *
     * <p>Function copy coded on 260818, commented in full on 260818.
     *
     * @return a new body with this one's name and slot layout, and every slot empty
     */
    public PlayerBody copy() {
        String name = this.name;
        List<EquipSlot> slots = new ArrayList<>();
        for (EquipSlot slot : this.slots) {
            slots.add(new EquipSlot(slot.getType(), slot.getName()));
        }
        return new PlayerBody(name, slots);
    }

    /**
     * Tests whether a given item is currently worn in one of this body's equipment slots — the port
     * of C's {@code object_is_equipped} ({@code obj-gear.c}). Empty slots are skipped; the match is
     * by object identity.
     *
     * <p>Worn is a different question from carried. An object in an equipment slot is still on the
     * gear list, so {@code Player.isCarried} answers {@code true}
     * for it too; this narrows that to the objects actually in use, which is what decides whether an
     * object is labelled by its slot or by its position in the pack.
     *
     * <p>Function itemIsEquipped commented in full on 260816.
     *
     * @param item the item to look for
     * @return {@code true} if the item occupies one of this body's slots
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
     */
    public String getName() {
        return name;
    }

    /**
     * @return the number of equipment slots this body provides (C: {@code player_body.count})
     */
    public int getCount() {
        return slots.size();
    }

    /**
     * @return the slots in body order (unmodifiable)
     */
    public List<EquipSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    /**
     * @param index the slot's position, its C {@code slots[i]} address
     * @return the slot at the given index
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
     * callers want: {@code Player.gearToLabel} uses it to
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

    /**
     * Finds the item worn in the slot with the given name — the port of C's
     * {@code equipped_item_by_slot_name} ({@code obj-gear.c:127}).
     *
     * <p>Slot names are the second field of each {@code slot:} line in {@code body.txt}: "weapon",
     * "shooting", "light", "body", "head" and so on. They are how the rest of the game asks for a
     * particular piece of equipment without knowing the body's layout — twenty-nine call sites in C
     * do exactly this, from the light source the fuel code burns down to the weapon a brand is
     * applied to. This is the reason the name exists at all; the gear system otherwise addresses
     * slots by index.
     *
     * <p>C reaches the answer through two helpers this method folds together:
     * {@code slot_by_name} walks the slots for a name match and returns its index, and
     * {@code slot_object} turns that index into the worn object or NULL. Neither is worth a method
     * of its own here, since nothing else in the port needs an index found by name.
     *
     * <p><b>Names are not unique, and the first match wins.</b> A humanoid body has two
     * {@code RING} slots, distinguished only by their names "right hand" and "left hand"; C's loop
     * stops at the first match and this stops at the first the stream finds, which is the same slot
     * because the list is in declared order. Matching is exact and case-sensitive, C using
     * {@code streq}.
     *
     * <p><b>Deliberate divergence from the C original.</b> C asserts that the name was found —
     * {@code assert(i < p->body.count)} at {@code obj-gear.c:62} — so asking for a slot this body
     * does not have aborts the game in a debug build and silently reads past the end of the array
     * in a release one. This returns null instead, on the grounds that a Java null is both safer
     * than the read and easier to trace than an abort. The cost is that "no such slot" and "that
     * slot is empty" become the same answer, which is a distinction no C caller makes either: every
     * one of them tests the returned pointer against NULL and nothing more.
     *
     * <p>A null name is likewise answered with null rather than being passed on to the comparison.
     * C would hand NULL to {@code streq} and crash; the guard is the port's, and it is why the name
     * is compared as {@code name.equals(slotName)} would be were the argument known good.
     *
     * <p>C's other guard, {@code if (p->body.slots)}, has no counterpart: it protects against a
     * player whose body has not been built yet, where the slot array is still NULL. The constructor
     * here refuses to build a body without slots, so the list always exists.
     *
     * <p>Function equippedItemBySlotName coded on 260818, commented in full on 260818.
     *
     * @param name the slot's name from {@code body.txt}, or {@code null}
     * @return the item worn in that slot, or {@code null} if the slot is empty, the name is unknown,
     * or the name is {@code null}
     */
    public ItemObject equippedItemBySlotName(String name) {
        if (name == null) return null;

        EquipSlot slot = slots.stream().filter(s -> name.equals(s.getName()))
                .findFirst().orElse(null);
        if (slot == null) return null;

        return slot.getItem();
    }
}