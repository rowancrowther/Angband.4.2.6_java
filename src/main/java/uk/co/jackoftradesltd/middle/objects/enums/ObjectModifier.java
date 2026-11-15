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

import java.util.Arrays;

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
 * <p>Name lookup is {@link #lookup(String)}, the counterpart of C's {@code code_index_in_array}
 * over the {@code obj_mods} name table. It departs from C in two accepted ways, both recorded
 * on that method.
 *
 * <p>Class ObjectModifier coded before 261007, commented in full on 261115.
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
    OM_MAX;

    /**
     * Finds the modifier a data-file name refers to, or {@code null} when there is none.
     *
     * <p>C does this with {@code code_index_in_array(obj_mods, code)} ({@code datafile.c}), an exact,
     * case-sensitive {@code streq} against the sixteen names built from {@code list-stats.h} and
     * {@code list-object-modifiers.h} ({@code "STR"} to {@code "MOVES"}), answering -1 for anything
     * else. {@code parse_object_property_code} ({@code obj-init.c}) is the caller that matters here,
     * and the {@code grab_int_value}, {@code grab_rand_value} and {@code grab_index_and_int} helpers
     * use the same table for {@code values:} lists. This method matches C for the bare names and
     * for any name in the wrong case, which finds nothing. A {@code null} argument has no C
     * counterpart and returns {@code null}.
     *
     * <p>Two accepted deviations, so do not report either as a mismatch:
     * <ul>
     *   <li>The sentinels can be found. C's table has no {@code NONE} or {@code MAX}, so those
     *   names find nothing there; here {@code "NONE"}, {@code "MAX"}, {@code "OM_NONE"} and
     *   {@code "OM_MAX"} return {@link #OM_NONE} and {@link #OM_MAX}. A caller that must refuse
     *   them tests for the sentinels itself, as {@code ObjectProperty2Assembler} does.</li>
     *   <li>The {@code OM_} prefix is optional. {@code "STR"} and {@code "OM_STR"} both return
     *   {@link #OM_STR}, where C finds nothing for {@code "OM_STR"}.</li>
     * </ul>
     *
     * <p>Method lookup coded before 261115, commented in full on 261115.
     *
     * @param first the name from the data file, with or without the {@code OM_} prefix; may be
     *              {@code null}
     * @return the matching constant (sentinels included), or {@code null} when none matches
     */
    public static ObjectModifier lookup(String first) {
        if (first == null) return null;
        
        String search;
        if (!first.startsWith("OM_"))
            search = "OM_" + first;
        else
            search = first;
        
        return Arrays.stream(ObjectModifier.values()).filter(o -> o.name().equals(search))
                .findFirst().orElse(null);
    }
}
