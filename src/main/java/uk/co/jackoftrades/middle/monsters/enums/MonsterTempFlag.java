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

package uk.co.jackoftrades.middle.monsters.enums;

public enum MonsterTempFlag {
    MFLAG_NONE(""),
    MFLAG_VIEW("Monster is in line of sight"),
    MFLAG_ACTIVE("Monster is in active mode"),
    MFLAG_NICE("Monster is still being nice"),
    MFLAG_SHOW("Monster is recently memorized"),
    MFLAG_MARK("Monster is currently memorized"),
    MFLAG_VISIBLE("Monster is \"visible\""),
    MFLAG_CAMOUFLAGE("Player doesn't know this is a monster"),
    MFLAG_AWARE("Monster is aware of the player"),
    MFLAG_HANDLED("Monster has been processed this turn"),
    MFLAG_TRACKING("Monster is tracking the player by sound or scent");

    private final String description;

    private MonsterTempFlag(String description) {
        this.description = description;
    }
}
