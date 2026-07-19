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

package uk.co.jackoftrades.middle.monsters.enums;

import java.util.Arrays;

/**
 * What kind of player attribute protects against a monster blow effect, as named by the
 * {@code effect-type:} directive in {@code blow_effects.txt}.
 * <p>
 * The C original keeps this as a bare {@code char *} on {@code struct blow_effect} and
 * compares it with {@code streq} at each use ([C] {@code blow_color}, {@code src/mon-lore.c});
 * modelling it as an enum lets the port resolve the string once, at load time. Only
 * {@link #BET_ELEMENT} and {@link #BET_FLAG} carry an accompanying {@code resist:} - the
 * rest identify a protection that is computed rather than named, such as the dexterity
 * check behind {@link #BET_THEFT}.
 * <p>
 * The wire strings are the data file's spellings and are deliberately inconsistent:
 * {@code eat-food} and {@code eat-light} are hyphenated but {@code all_sustains} is
 * underscored, upstream included. They must be matched exactly as written.
 *
 * @author Rowan Crowther
 */
public enum BlowEffectType {
    BET_ELEMENT("element"),
    BET_FLAG("flag"),
    BET_DRAIN("drain"),
    BET_THEFT("theft"),
    BET_EAT_FOOD("eat-food"),
    BET_EAT_LIGHT("eat-light"),
    BET_ALL_SUSTAINS("all_sustains");

    /**
     * The spelling this type has in {@code blow_effects.txt}.
     *
     * @author Rowan Crowther
     */
    private final String type;

    /**
     * @param type the data-file spelling this constant is known by
     * @author Rowan Crowther
     */
    BlowEffectType(String type) {
        this.type = type;
    }

    /**
     * @return the data-file spelling of this effect type
     * @author Rowan Crowther
     */
    public String getType() {
        return type;
    }

    /**
     * Resolve a data-file {@code effect-type:} value to its constant.
     *
     * @param type the spelling read from the data file
     * @return the matching constant, or {@code null} if none matches - which includes the
     * empty string, since an effect is allowed to name no {@code effect-type:} at all
     * @author Rowan Crowther
     */
    public static BlowEffectType getFromString(String type) {
        return Arrays.stream(BlowEffectType.values()).filter(s -> s.getType().equals(type))
                .findFirst().orElse(null);
    }
}
