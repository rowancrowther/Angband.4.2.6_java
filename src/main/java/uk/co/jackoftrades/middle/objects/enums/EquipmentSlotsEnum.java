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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The player's equipment slots (weapon, bow, rings, amulet, light, armour
 * pieces), each carrying whether items in it are acid-resistant by location,
 * whether the slot name is mentioned, and the phrases used to describe wearing or
 * wielding the item. Mirrors the C original's equipment-slot table
 * ({@code src/list-equip-slots.h}); the constants are self-describing and
 * documented collectively here.
 *
 * @author ClaudeCode
 */
public enum EquipmentSlotsEnum {
    EQUIP_NONE(false, false, "", "", ""),
    EQUIP_WEAPON(false, false, "Wielding", "just lifting", "attacking monsters with"),
    EQUIP_BOW(false, false, "Shooting", "just holding", "shooting missiles with"),
    EQUIP_RING(false, true, "On %s", "", "wearing on your %s"),
    EQUIP_AMULET(false, true, "Around %s", "", "wearing around your %s"),
    EQUIP_LIGHT(false, false, "Light source", "", "using to light your way"),
    EQUIP_BODY_ARMOR(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_CLOAK(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_SHIELD(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_HAT(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_GLOVES(true, true, "On %s", "", "wearing on your %s"),
    EQUIP_BOOTS(true, true, "On %s", "", "wearing on your %s");

    /**
     * Whether items worn in this slot are treated as acid-resistant by location.
     *
     * @author ClaudeCode
     */
    private final boolean acidResistant;
    /**
     * Whether the slot's name is mentioned (e.g. which finger a ring is on).
     *
     * @author ClaudeCode
     */
    private final boolean mentionName;
    /**
     * Template for mentioning what is worn in this slot.
     *
     * @author ClaudeCode
     */
    private final String mentionString;
    /**
     * Description used when the item is too heavy to use effectively.
     *
     * @author ClaudeCode
     */
    private final String heavyDescribe;
    /**
     * Description of how the item in this slot is used.
     *
     * @author ClaudeCode
     */
    private final String describe;

    /**
     * Bind an equipment slot to its descriptive attributes.
     *
     * @param acidResistant whether items here are acid-resistant by location
     * @param mentionName   whether the slot name is mentioned
     * @param mentionString mention template
     * @param heavyDescribe too-heavy description
     * @param describe      usage description
     * @author ClaudeCode
     */
    EquipmentSlotsEnum(boolean acidResistant, boolean mentionName, String mentionString, String heavyDescribe, String describe) {
        this.acidResistant = acidResistant;
        this.mentionName = mentionName;
        this.mentionString = mentionString;
        this.heavyDescribe = heavyDescribe;
        this.describe = describe;
    }
}