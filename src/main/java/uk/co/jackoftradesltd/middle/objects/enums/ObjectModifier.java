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
 * The numeric modifiers an object can grant: the five stat bonuses, then stealth, searching
 * skill, infravision, tunnelling, speed, extra blows, shooting speed, shooting power, light
 * radius, damage reduction and extra moves. Mirrors the C original's {@code OBJ_MOD_*} enum,
 * which {@code obj-properties.h} builds by including {@code list-stats.h} (the five {@code STAT()}
 * entries) and then {@code list-object-modifiers.h} (the eleven {@code OBJ_MOD()} entries).
 *
 * <p>Order matters. C's header warns that changing the modifier order breaks savefiles, and the
 * constants here are in exactly the order of that enum: {@code OM_NONE} first, then the five
 * stats in {@code list-stats.h} order, then the eleven {@code OBJ_MOD()} entries, then
 * {@code OM_MAX}. The stats come first and in stat order because C indexes a modifier array by
 * {@code OBJ_MOD_MIN_STAT + stat} ({@code obj-randart.c} picks a random stat modifier that way);
 * {@code OBJ_MOD_MIN_STAT} is {@code OBJ_MOD_STR}, so it has no constant of its own here.
 *
 * <p>{@code OM_NONE} and {@code OM_MAX} are sentinels, not real modifiers. {@code OM_NONE} has no
 * C counterpart, which shifts every real constant's {@link #ordinal()} up by one against C's
 * {@code OBJ_MOD_*} value, so {@code ordinal()} must never be used as a C index or written to a
 * savefile. A loop over every modifier skips both sentinels, as C's {@code 0 .. OBJ_MOD_MAX - 1}
 * loops cover only the sixteen real ones.
 *
 * <p>What each modifier adds to which piece of player state lives in the callers, not here; the
 * properties of each modifier are defined in {@code lib/gamedata/object_property.txt}.
 *
 * <p>Class ObjectModifier coded before 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
public enum ObjectModifier {
    /**
     * Placeholder for "no modifier"; has no C counterpart and is never a real index.
     */
    OM_NONE,
    /** Strength bonus, {@code OBJ_MOD_STR}; the first real modifier and {@code OBJ_MOD_MIN_STAT}. */
    OM_STR,
    /** Intelligence bonus, {@code OBJ_MOD_INT}. */
    OM_INT,
    /** Wisdom bonus, {@code OBJ_MOD_WIS}. */
    OM_WIS,
    /** Dexterity bonus, {@code OBJ_MOD_DEX}. */
    OM_DEX,
    /** Constitution bonus, {@code OBJ_MOD_CON}; the last of the five stat modifiers. */
    OM_CON,
    /** Stealth bonus, {@code OBJ_MOD_STEALTH}; the first {@code OBJ_MOD()} entry after the stats. */
    OM_STEALTH,
    /** Searching skill bonus, {@code OBJ_MOD_SEARCH}; C scales it by 5 into the search skill. */
    OM_SEARCH,
    /** Infravision bonus, {@code OBJ_MOD_INFRA}. */
    OM_INFRA,
    /** Tunnelling (digging) bonus, {@code OBJ_MOD_TUNNEL}. */
    OM_TUNNEL,
    /**
     * Speed bonus, {@code OBJ_MOD_SPEED}.
     */
    OM_SPEED,
    /** Extra blows, {@code OBJ_MOD_BLOWS}. */
    OM_BLOWS,
    /** Extra shots (shooting speed), {@code OBJ_MOD_SHOTS}. */
    OM_SHOTS,
    /** Extra might (shooting power), {@code OBJ_MOD_MIGHT}. */
    OM_MIGHT,
    /**
     * Light radius, {@code OBJ_MOD_LIGHT}.
     */
    OM_LIGHT,
    /**
     * Damage reduction, {@code OBJ_MOD_DAM_RED}.
     */
    OM_DAM_RED,
    /** Extra moves, {@code OBJ_MOD_MOVES}; the last real modifier. */
    OM_MOVES,
    /** Count sentinel standing in for C's {@code OBJ_MOD_MAX}; never a real modifier. */
    OM_MAX
}
