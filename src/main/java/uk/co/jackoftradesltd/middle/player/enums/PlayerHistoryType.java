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

package uk.co.jackoftradesltd.middle.player.enums;

public enum PlayerHistoryType {

    HIST_NONE(""),
    HIST_PLAYER_BIRTH("Player was born"),
    HIST_ARTIFACT_UNKNOWN("Player found but not IDd an artifact"),
    HIST_ARTIFACT_KNOWN("Player has IDed an artifact"),
    HIST_ARTIFACT_LOST("Player had an artifact and lost it"),
    HIST_PLAYER_DEATH("Player has been slain"),
    HIST_SLAY_UNIQUE("Player has slain a unique monster"),
    HIST_USER_INPUT("User-added note"),
    HIST_SAVEFILE_IMPORT("Added when an older version savefile is imported"),
    HIST_GAIN_LEVEL("Player gained a level"),
    HIST_GENERIC("Anything else not covered here (unused)"),
    HIST_MAX("");

    private final String description;

    PlayerHistoryType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
