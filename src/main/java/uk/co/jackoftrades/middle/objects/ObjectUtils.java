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

package uk.co.jackoftrades.middle.objects;

/**
 * Free-standing helper routines for the object/inventory subsystem — a port landing spot for the
 * gear-management corners of C's {@code obj-gear.c} and friends.
 *
 * <p>Static methods that act on objects and the player's pack without belonging to any one object's
 * data model. Populated as the game loop and commands need them.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop.
 *
 * @author Rowan Crowther
 */
public class ObjectUtils {
    /**
     * Handle "pack overflow" — the port of C's {@code pack_overflow} ({@code obj-gear.c}). When the
     * pack holds more than it can carry, the excess is dropped (or the offending item is), so the
     * game never proceeds with an over-full inventory. The player-processing pass calls it defensively
     * at the top of each command, in case a menu action left the pack corrupted.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the gear subsystem is ported.
     *
     * @param object the specific item to overflow, or {@code null} to overflow the last pack slot as
     *               C does with {@code pack_overflow(NULL)}
     */
    public static void packOverflow(ItemObject object) {
        // Stub class TODO: implement
    }
}
