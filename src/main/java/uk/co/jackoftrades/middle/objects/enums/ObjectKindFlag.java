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

import org.jetbrains.annotations.Contract;

/**
 * Flags applied to an object <em>kind</em> (the template) rather than to an
 * individual item — controlling random generation (extra resistances/sustains/
 * powers), instant-artifact status, display hints (show dice/multiplier), ammo
 * compatibility and elemental ignore. Mirrors the C original's {@code KF_*}
 * kind flags ({@code src/list-kind-flags.h}); the constants are self-describing
 * and documented collectively here.
 *
 * @author ClaudeCode
 */
public enum ObjectKindFlag {
    KF_NONE(""),
    KF_RAND_HI_RES(""),
    KF_RAND_SUSTAIN(""),
    KF_RAND_POWER(""),
    KF_INSTA_ART(""),
    KF_QUEST_ART(""),
    KF_EASY_KNOW(""),
    KF_GOOD(""),
    KF_SHOW_DICE(""),
    KF_SHOW_MULT(""),
    KF_SHOOTS_SHOTS(""),
    KF_SHOOTS_ARROWS(""),
    KF_SHOOTS_BOLTS(""),
    KF_RAND_BASE_RES(""),
    KF_RAND_RES_POWER(""),

    // Added in to handle IGNORE flags
    KF_IGNORE_FIRE(""),
    KF_IGNORE_ACID(""),
    KF_IGNORE_ELEC(""),
    KF_IGNORE_COLD(""),

    KF_MAX("");

    /**
     * Optional descriptive string associated with the flag (often empty).
     *
     * @author ClaudeCode
     */
    private String flag;

    /**
     * Bind a kind flag to its descriptive string.
     *
     * @param flag the descriptive string
     * @author ClaudeCode
     */
    @Contract(pure = true)
    ObjectKindFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @return this flag's descriptive string
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public String getFlag() {
        return flag;
    }
}