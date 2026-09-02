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

package uk.co.jackoftradesltd.middle.objects.enums;

/**
 * The kinds of equipment slot a player's body can have (weapon, bow, rings, amulet, light, armour
 * pieces), each carrying whether items worn there need checking for acid damage, whether the slot's
 * own name is mentioned when something happens to the item, and the phrases used to describe
 * wearing or wielding it. The port of C's equipment-slot table ({@code list-equip-slots.h}), read
 * through the {@code EQUIP(...)} macro into both an enum ({@code obj-gear.h:28-34}) and a parallel
 * {@code slot_table} ({@code obj-gear.c:34-46}).
 *
 * <p>A slot's <em>kind</em> is not a slot: a body ({@code body.txt}) names its slots and gives each
 * one of these types, and two of its twelve slots share {@link #EQUIP_RING}. Code that wants "the
 * weapon slot" asks a body for the slot whose type is {@link #EQUIP_WEAPON}.
 *
 * <p>Slot type is what excludes a slot from a calculation. {@code calcBonuses} declines to add a
 * to-hit or to-damage bonus from {@link #EQUIP_WEAPON} or {@link #EQUIP_BOW}, so that wielding a
 * weapon does not improve unrelated actions ({@code player-calcs.c:1997-1998}); mana encumbrance
 * weighs everything except those two plus {@link #EQUIP_RING}, {@link #EQUIP_AMULET} and
 * {@link #EQUIP_LIGHT} ({@code player-calcs.c:1511-1519}).
 *
 * @author Rowan Crowther
 */
public enum EquipmentSlotsEnum {
    /**
     * A slot with no kind — the first row of {@code list-equip-slots.h} and a real type, not a
     * placeholder for "empty". No body in the shipped {@code body.txt} uses it, and an unworn slot
     * is expressed by the slot holding no item rather than by this type.
     */
    EQUIP_NONE(false, false, "", "", ""),
    /**
     * The melee weapon slot. Excluded from the to-hit and to-damage totals, and from mana encumbrance.
     */
    EQUIP_WEAPON(false, false, "Wielding", "just lifting", "attacking monsters with"),
    /** The launcher slot — bow, sling or crossbow. Excluded on the same terms as {@link #EQUIP_WEAPON}. */
    EQUIP_BOW(false, false, "Shooting", "just holding", "shooting missiles with"),
    /** A ring finger. The one type a standard body carries twice, which is why the slot's own name is mentioned. */
    EQUIP_RING(false, true, "On %s", "", "wearing on your %s"),
    /** The neck slot. */
    EQUIP_AMULET(false, true, "Around %s", "", "wearing around your %s"),
    /** The light source slot — the only slot {@code calcLight} expects a fuelled item in. */
    EQUIP_LIGHT(false, false, "Light source", "", "using to light your way"),
    /** The body armour slot. Spelled the American way, as C's {@code EQUIP(BODY_ARMOR, ...)} is. */
    EQUIP_BODY_ARMOR(true, true, "On %s", "", "wearing on your %s"),
    /** The cloak slot. */
    EQUIP_CLOAK(true, true, "On %s", "", "wearing on your %s"),
    /** The shield arm. */
    EQUIP_SHIELD(true, true, "On %s", "", "wearing on your %s"),
    /** The head slot — helmets and crowns. */
    EQUIP_HAT(true, true, "On %s", "", "wearing on your %s"),
    /** The hands slot. */
    EQUIP_GLOVES(true, true, "On %s", "", "wearing on your %s"),
    /**
     * The feet slot.
     */
    EQUIP_BOOTS(true, true, "On %s", "", "wearing on your %s"),
    /**
     * The end-of-list sentinel, C's hand-written last member of the enum ({@code obj-gear.h:33})
     * and the terminating row of {@code slot_table} ({@code obj-gear.c:45}). Never a slot's type,
     * so loops over a body never meet it — only loops over this enum's values need to skip it.
     */
    EQUIP_MAX(false, false, "", "", "");

    /**
     * Whether items worn in this slot need checking for acid damage — C's {@code acid_v} column,
     * true for the six armour slots and false for weapon, bow, ring, amulet and light.
     *
     * <p>Note the sense: {@code true} means the slot is <em>vulnerable</em>, not protected. The
     * column is carried but never read in 4.2.6 — {@code obj-gear.c:36} declares it and nothing
     * consults it — so it is data waiting for a caller rather than behaviour.
     */
    private final boolean acidResistant;
    /**
     * Whether the slot's own name appears when something happens to the item in it — C's
     * {@code name} column. True where the slot alone does not identify the item's position, which
     * in practice means the rings and the armour pieces.
     */
    private final boolean mentionName;
    /**
     * Short phrase naming the slot in passing, C's {@code mention} column — "Wielding", "Around
     * %s". A {@code %s} takes the slot's own name, so a ring reads "On your right hand".
     */
    private final String mentionString;
    /**
     * Phrase used when the item in the slot is too heavy for the player to use properly, C's
     * {@code heavy describe} column — "just lifting" a weapon, "just holding" a launcher. Empty
     * for every slot that has no heaviness rule.
     */
    private final String heavyDescribe;
    /**
     * Longer phrase describing what the player does with the item, C's {@code describe} column —
     * "attacking monsters with", "wearing on your %s". A {@code %s} takes the slot's own name.
     */
    private final String describe;

    /**
     * Binds a slot kind to the five columns of its {@code list-equip-slots.h} row.
     *
     * @param acidResistant whether items here need checking for acid damage
     * @param mentionName   whether the slot's own name is mentioned
     * @param mentionString mention template
     * @param heavyDescribe too-heavy description
     * @param describe      usage description
     */
    EquipmentSlotsEnum(boolean acidResistant, boolean mentionName, String mentionString, String heavyDescribe, String describe) {
        this.acidResistant = acidResistant;
        this.mentionName = mentionName;
        this.mentionString = mentionString;
        this.heavyDescribe = heavyDescribe;
        this.describe = describe;
    }
}