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

package uk.co.jackoftrades.middle.cave.enums;

public enum TerrainFeatureFlags {
    TF_NONE(""),
    TF_LOS("Allows line of sight"),
    TF_PROJECT("Allows projections to pass through"),
    TF_PASSABLE("Can be passed through by all creatures"),
    TF_INTERESTING("Is noticed on looking around"),
    TF_PERMANENT("Is permanent"),
    TF_EASY("Is easily passed through"),
    TF_TRAP("Can hold a trap"),
    TF_NO_SCENT("Cannot store scent"),
    TF_NO_FLOW("No flow through"),
    TF_OBJECT("Can hold objects"),
    TF_TORCH("Becomes bright when torch-lit"),
    TF_HIDDEN("Can be found by searching"),
    TF_GOLD("Contains treasure"),
    TF_CLOSABLE("Can be closed"),
    TF_FLOOR("Is a clear floor"),
    TF_WALL("Is a solid wall"),
    TF_ROCK("Is rocky"),
    TF_GRANITE("Is a granite rock wall"),
    TF_DOOR_ANY("Is any door"),
    TF_DOOR_CLOSED("Is a closed door"),
    TF_SHOP("Is a shop"),
    TF_DOOR_JAMMED("Is a jammed door"),
    TF_DOOR_LOCKED("Is a locked door"),
    TF_MAGMA("Is a magma seam"),
    TF_QUARTZ("Is a quartz seam"),
    TF_STAIR("Is a stair"),
    TF_UPSTAIR("Is an up staircase"),
    TF_DOWNSTAIR("Is a down staircase"),
    TF_SMOOTH("Should have smooth boundaries"),
    TF_BRIGHT("Is internally lit"),
    TF_FIERY("Is fire-based"),
    TF_MAX("");

    private final String description;

    TerrainFeatureFlags(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}