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

public enum IgnoreType {
    ITYPE_NONE(""),
    ITYPE_SHARP("Sharp Melee Weapons"),
    ITYPE_BLUNT("Blunt Melee Weapons"),
    ITYPE_GREAT("Great Weapons"),
    ITYPE_SLING("Slings"),
    ITYPE_BOW("Bows"),
    ITYPE_CROSSBOW("Crossbows"),
    ITYPE_SHOT("Shots and Pebbles"),
    ITYPE_ARROW("Arrows"),
    ITYPE_BOLT("Bolts"),
    ITYPE_ROBE("Robes"),
    ITYPE_BODY_ARMOR("Body Armor"),
    ITYPE_BASIC_DRAGON_ARMOR("Basic Dragon Scale Mail"),
    ITYPE_MULTI_DRAGON_ARMOR("Multi-Hued Dragon Scale Mail"),
    ITYPE_HIGH_DRAGON_ARMOR("High Dragon Scale Mail"),
    ITYPE_BALANCE_DRAGON_ARMOR("Balance Dragon Scale Mail"),
    ITYPE_POWER_DRAGON_ARMOR("Power Dragon Scale Mail"),
    ITYPE_CLOAK("Cloaks"),
    ITYPE_ELVEN_CLOAK("Elven Cloaks"),
    ITYPE_SHIELD("Shields"),
    ITYPE_HEADGEAR("Headgear"),
    ITYPE_HANDGEAR("Handgear"),
    ITYPE_FEET("Footgear"),
    ITYPE_DIGGER("Diggers"),
    ITYPE_RING("Rings"),
    ITYPE_AMULET("Amulets"),
    ITYPE_LIGHT("Lights"),
    ITYPE_MAX("");

    private final String name;

    IgnoreType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
