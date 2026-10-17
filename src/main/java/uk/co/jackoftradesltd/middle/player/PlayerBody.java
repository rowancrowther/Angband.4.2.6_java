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
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A body layout — a named, ordered set of equipment slots a creature presents.
 *
 * <p>Ports the C {@code struct player_body} ({@code player.h}), defined by {@code body.txt}: a body
 * name followed by its slots in declared order. C's {@code count} member is not stored; it is the
 * size of the slot list ({@link #getCount()}).
 *
 * <p><b>One class, two roles.</b> C uses the same struct for the template and for the player's own
 * body, and so does this class. The templates are built once by {@code BodyAssembler} and held in
 * the registry, with every slot empty. At character birth {@link #copy()} makes the player's body
 * from the race's template, and from then on the {@link EquipSlot}s of that copy are filled and
 * emptied by the wield code (C's {@code equip_slot.obj}). The list is installed once, by
 * {@link #setSlots}, after the constructor has built the body empty; from then on the list never
 * changes and the slots in it do, so the class is only "immutable" in the sense that no slot is
 * added, removed or reordered. The three gear queries here ({@link #itemIsEquipped},
 * {@link #equippedItemSlot}, {@link #equippedItemBySlotName}) are meaningful on the player's copy
 * only, a template having nothing worn.
 *
 * <p><b>Slots are index-addressed.</b> The whole gear system reaches equipment by slot number
 * ({@code slots[i]}); name and type are only lookup keys that resolve to an index (C's
 * {@code slot_by_name} / {@code slot_by_type}). So the slots are held as an ordered {@link List} and
 * position is significant — e.g. the two {@code RING} slots ("right hand", "left hand") are distinct
 * only by their index and name.
 *
 * <p>Class PlayerBody coded before 260815, commented in full on 261010, construction and slot
 * installation described as two steps on 261017.
 *
 * @author Rowan Crowther
 */
public class PlayerBody {
    /**
     * Logger for this class, used to report a null or empty slot list before {@link #setSlots}
     * throws. C has no equivalent: its bodies come from a parser that has already rejected a
     * malformed {@code body.txt}, and a body with no slots is simply an array of length zero.
     *
     * <p>Field logger coded before 260815, commented in full on 261010.
     */
    private final static Logger logger = LogManager.getLogger(PlayerBody.class);
    
    /**
     * Display name of this body layout, e.g. {@code "Humanoid"} (C: {@code player_body.name}).
     * Set once by the constructor and never reassigned, though not declared {@code final}.
     *
     * <p>Field name coded before 260815, commented in full on 261010.
     */
    private String name;

    /**
     * The equipment slots in declared (body) order; a slot's index is its identity (C:
     * {@code player_body.slots}, with {@code count} being this list's size). The constructor starts
     * it empty and {@link #setSlots} installs the real list, once; the {@link EquipSlot}s in it are
     * not fixed, as each one's worn item changes.
     *
     * <p>Field slots coded before 260815, commented in full on 261010, updated on 261017 for
     * {@link #setSlots}.
     */
    private List<EquipSlot> slots;

    /**
     * Creates a body with a name and no slots yet; {@link #setSlots} supplies them.
     *
     * <p>Until {@link #setSlots} has run the slot list is empty, so {@link #getCount()} answers zero
     * and the gear queries find nothing. Both callers, {@code BodyAssembler} and {@link #copy()},
     * call {@link #setSlots} straight away. Nothing is pushed to the character sheet here; that
     * happens in {@link #setSlots}.
     *
     * <p>Constructor PlayerBody coded before 260815, commented in full on 261010, rewritten on
     * 261017 once the slot list moved to {@link #setSlots}.
     *
     * @param name the body's display name
     */
    public PlayerBody(String name) {
        this.name = name;
        this.slots = new ArrayList<>();
    }

    /**
     * Installs this body's slots, in declared order.
     *
     * <p>The list is copied into one of the body's own, so the caller adding to or clearing its list
     * afterwards changes nothing here; the {@link EquipSlot}s themselves are not copied, and are
     * shared with the caller's list. A null or empty list is refused with an
     * {@link IllegalArgumentException}, after a fatal log entry, because C can never produce one:
     * its bodies come from a parser that has already rejected the file. The previous list is left
     * in place when the call is refused.
     *
     * <p><b>Side effect with no C counterpart.</b> A successful call also pushes the slot count to
     * the character sheet through {@link PlayerEventStatusUpdate#updatePlayerCharSheetBodyCount}. In
     * C the sheet reads {@code player->body.count} on demand; the cache-to-messages migration
     * replaces that read with this push. The push happens for every body given slots, so it fires
     * once per template when {@code body.txt} is loaded and again for each {@link #copy()}, and what
     * the sheet holds is whichever body was given slots last.
     *
     * <p>Function setSlots coded on 261017, commented in full on 261017.
     *
     * @param slots the equipment slots in body order
     * @throws IllegalArgumentException if {@code slots} is {@code null} or empty
     */
    public void setSlots(List<EquipSlot> slots) {
        if (slots == null || slots.isEmpty()) {
            logger.fatal("Invalid slot list. Slot list is null or empty.");
            throw new IllegalArgumentException("Invalid slot list. Slot list is null or empty.");
        } else {
            this.slots = new ArrayList<>(slots);
            PlayerEventStatusUpdate.updatePlayerCharSheetBodyCount(slots.size());
        } 
    }
    
    /**
     * Copies this body plan into a new, unworn one — the port of the copying half of C's
     * {@code player_embody} ({@code player-birth.c}).
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
     * <p>The copy goes through the constructor and {@link #setSlots}, so it also pushes the new
     * body's slot count to the character sheet, which {@code player_embody} does not do; see
     * {@link #setSlots}.
     *
     * <p>Function copy coded on 260818, commented in full on 261010, updated on 261017 for
     * {@link #setSlots}.
     *
     * @return a new body with this one's name and slot layout, and every slot empty
     */
    public PlayerBody copy() {
        String name = this.name;
        List<EquipSlot> slots = new ArrayList<>();
        for (EquipSlot slot : this.slots) {
            slots.add(new EquipSlot(slot.getType(), slot.getName()));
        }
        PlayerBody copy = new PlayerBody(name);
        copy.setSlots(slots);
        return copy;
    }

    /**
     * Tests whether a given item is currently worn in one of this body's equipment slots — the port
     * of C's {@code object_is_equipped} ({@code obj-gear.c}). Empty slots never match a real item;
     * the match is by object identity.
     *
     * <p>C writes this as {@code object_slot(body, obj) < body.count}, where {@code object_slot}
     * walks the slots comparing {@code obj == body.slots[i].obj} and returns the index it stopped at.
     * The port answers the yes/no question directly and has no separate {@code object_slot}.
     *
     * <p>Worn is a different question from carried. An object in an equipment slot is still on the
     * gear list, so {@link uk.co.jackoftradesltd.middle.objects.ObjectUtils#isCarried} answers
     * {@code true} for it too; this narrows that to the objects actually in use, which is what
     * decides whether an object is labelled by its slot or by its position in the pack.
     *
     * <p><b>A null item is "equipped" when any slot is empty.</b> C compares pointers, so a NULL
     * argument equals the NULL held by every empty slot and the answer is true as soon as the body
     * has a gap. The port reproduces that with the {@code emptySlot} flag rather than refusing the
     * null. No caller relies on it: {@code ObjectUtils.gearToLabel} returns before asking when the
     * item is null.
     *
     * <p>Matching is by identity, {@link ItemObject} declaring no {@code equals} of its own, which
     * is what C's pointer comparison means.
     *
     * <p>Function itemIsEquipped coded before 260816, commented in full on 261010.
     *
     * @param item the item to look for, or {@code null}
     * @return {@code true} if the item occupies one of this body's slots, or if {@code item} is
     * {@code null} and at least one slot is empty
     */
    public boolean itemIsEquipped(ItemObject item) {
        boolean emptySlot = false;
        for (EquipSlot slot : slots) {
            if (slot.getItem() == null) {
                emptySlot = true;
                continue;
            }

            if (slot.getItem().equals(item)) {
                return true;
            }
        }

        return item == null && emptySlot;
    }

    /**
     * Returns this body's display name, e.g. {@code "Humanoid"} (C: {@code player_body.name}).
     *
     * <p>Method getName coded before 260815, commented in full on 261010.
     *
     * @return this body's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of equipment slots this body provides. C stores this as
     * {@code player_body.count}; here it is the size of the slot list, so it cannot disagree with
     * it. It is also the value {@link #equippedItemSlot} returns for an item that is not worn.
     *
     * <p>Method getCount coded before 260815, commented in full on 261010.
     *
     * @return the number of equipment slots this body provides
     */
    public int getCount() {
        return slots.size();
    }

    /**
     * Returns the slots in body order, as a read-only view. The list cannot be changed through it,
     * but the {@link EquipSlot}s in it can, so a caller can still wear or remove items.
     *
     * <p>Method getSlots coded before 260815, commented in full on 261010.
     *
     * @return the slots in body order (unmodifiable)
     */
    public List<EquipSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    /**
     * Returns the slot at a given position, C's {@code body.slots[index]}. No bounds check is made
     * beyond the list's own: an index outside {@code 0..getCount()-1} throws
     * {@link IndexOutOfBoundsException}, where C reads past the array (or trips the assert in
     * {@code slot_object}). In particular the "not worn" result of {@link #equippedItemSlot} is
     * not a valid index here.
     *
     * <p>Method getSlot coded before 260815, commented in full on 261010.
     *
     * @param index the slot's position, its C {@code slots[i]} address
     * @return the slot at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public EquipSlot getSlot(int index) {
        return slots.get(index);
    }

    /**
     * Finds which equipment slot an item is worn in, the port of C's {@code equipped_item_slot}
     * ({@code player-calcs.c}).
     *
     * <p>The return for "not worn" is {@link #getCount()} — one past the last slot — rather than
     * {@code -1}. That follows C, whose loop simply runs off the end and hands back the index it
     * stopped at, and callers test for it explicitly: {@code obj-gear.c} guards with
     * {@code if (slot == player->body.count) return;}. A null item takes the same exit, which is
     * why the size is returned up front rather than falling through the loop.
     *
     * <p>This is the one place C and {@link #itemIsEquipped} part company over a null item. C's
     * {@code object_is_equipped} lets NULL match an empty slot, whereas {@code equipped_item_slot}
     * returns early and never does, so {@code equippedItemSlot(null)} is {@code getCount()} even on
     * a body with a gap.
     *
     * <p>The reason it is an index and not a slot is that the index <em>is</em> the answer the
     * callers want: {@link uk.co.jackoftradesltd.middle.objects.ObjectUtils#gearToLabel} uses it to
     * subscript the equipment label string, so an item's slot position becomes the letter the
     * player selects it by.
     *
     * <p>Matching is by identity, {@link ItemObject} declaring no {@code equals} of its own, which
     * is what C's pointer comparison means. Two identical swords are two different objects and only
     * the one actually worn is found.
     *
     * <p>Function equippedItemSlot coded before 260817, commented in full on 261010.
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
     * {@code equipped_item_by_slot_name} ({@code obj-gear.c}).
     *
     * <p>Slot names are the second field of each {@code slot:} line in {@code body.txt}: "weapon",
     * "shooting", "light", "body", "head" and so on. They are how the rest of the game asks for a
     * particular piece of equipment without knowing the body's layout — some thirty call sites in C
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
     * {@code assert(i < p->body.count)} in {@code slot_by_name} — so asking for a slot this body
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
     * player whose body has not been built yet, where the slot array is still NULL. The list here is
     * never null, as the constructor starts it empty and {@link #setSlots} refuses a null one; before
     * {@link #setSlots} has run it is empty and the search finds nothing.
     *
     * <p>Function equippedItemBySlotName coded on 260818, commented in full on 261010, slot-list
     * note corrected on 261017.
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