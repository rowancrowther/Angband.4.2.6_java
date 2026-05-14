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

public enum SquareEnum {
    SQUARE_NONE(""),
    SQUARE_MARK("memorized feature"),
    SQUARE_GLOW("self-illuminating"),
    SQUARE_VAULT("part of a vault"),
    SQUARE_ROOM("part of a room"),
    SQUARE_SEEN("seen flag"),
    SQUARE_VIEW("view flag"),
    SQUARE_WASSEEN("previously seen _during update),"),
    SQUARE_FEEL("hidden points to trigger feelings"),
    SQUARE_TRAP("square containing a known trap"),
    SQUARE_INVIS("square containing an unknown trap"),
    SQUARE_WALL_INNER("inner wall generation flag"),
    SQUARE_WALL_OUTER("outer wall generation flag"),
    SQUARE_WALL_SOLID("solid wall generation flag"),
    SQUARE_MON_RESTRICT("no random monster flag"),
    SQUARE_NO_TELEPORT("player can't teleport from this square"),
    SQUARE_NO_MAP("square can't be magically mapped"),
    SQUARE_NO_ESP("telepathy doesn't work on this square"),
    SQUARE_PROJECT("marked for projection processing"),
    SQUARE_DTRAP("trap detected square"),
    SQUARE_NO_STAIRS("square is not suitable for placing stairs"),
    SQUARE_CLOSE_PLAYER("square is seen and in player's light radius or UNLIGHT detection radius"),
    SQUARE_MAX("");

    private final String description;

    private SquareEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}