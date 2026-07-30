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

import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.Player;

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

    /**
     * Marks a single object flag as learned on all of the player's worn equipment — the port of
     * C's {@code equip_learn_flag} ({@code obj-knowledge.c}). Called when an event reveals a flag
     * (e.g. taking damage of a resisted element), so any equipped item carrying it becomes known.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the knowledge subsystem is ported.
     *
     * @param player     the player whose equipment is checked
     * @param objectFlag the object flag to mark as learned
     * @author Rowan Crowther
     */
    public static void equipLearnFlag(Player player, ObjectFlag objectFlag) {
        // Stub class TODO: implement
    }

    /**
     * Learns the timed/after-time properties of the player's worn equipment — the port of C's
     * {@code equip_learn_after_time} ({@code obj-knowledge.c}), run periodically so flags that are
     * only revealed through prolonged wear become known.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the knowledge subsystem is ported.
     *
     * @param player the player whose equipment is checked
     * @author Rowan Crowther
     */
    public static void equipLearnAfterTime(Player player) {
        // Stub class TODO: implement
    }

    /**
     * Fire a curse's effect against the player, as the source item's curse timeout
     * expires. The port of C's {@code do_curse_effect}; the returned flag drives
     * whether the player then learns the curse's identity.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the effect runtime; reports
     * {@code false} (nothing happened, so the curse is not revealed).</p>
     *
     * @param curseEntry the curse and its per-object instance data
     * @param item       the worn item the curse is attached to (the effect's source)
     * @return {@code true} if the effect did something the player would notice
     * @author Rowan Crowther
     */
    public static boolean doCurseEffect(Curse.CurseEntry curseEntry, ItemObject item) {
        // Stub class TODO: implement
        return false;
    }
}
