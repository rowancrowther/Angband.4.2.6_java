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

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ObjectDescription;
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
     * Builds the display name of an object as this player would see it — the port of C's
     * {@code object_desc} ({@code obj-desc.c}), which writes into a caller-supplied buffer where the
     * port returns a string.
     *
     * <p>The {@code description} flags select how much of the name to include. C treats them as a
     * bit mask ({@code obj-desc.h:26-42}), so an empty set is C's {@code ODESC_BASE == 0x00} — the
     * bare name with no combat bonuses, charges or inscription — and callers OR in extras such as
     * {@code ODESC_COMBAT} or {@code ODESC_PREFIX} from there.
     *
     * <p>The player is a parameter rather than a global because the name depends on what they know:
     * an unidentified potion shows its flavour, an unlearned ego stays anonymous.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the object-naming and knowledge runtimes;
     * returns an empty string, so callers currently produce messages with a blank where the item
     * name belongs. Note that C's {@code ODESC_ALTNUM} passes a count through the high 16 bits of
     * the mode word, which a flag set cannot carry — it will need a separate parameter when this is
     * ported.
     *
     * @param item        the object to name
     * @param description the {@link ObjectDescription} flags selecting how much detail to include
     * @param player      the player whose knowledge governs what the name reveals
     * @return the object's display name
     * @author Rowan Crowther
     */
    public static String objectDesc(ItemObject item, Flag<ObjectDescription> description, Player player) {
        // Stub class TODO: implement
        return "";
    }

    /**
     * Fire a curse's effect against the player, as the source item's curse timeout expires. The
     * port of C's {@code do_curse_effect} ({@code obj-curse.c:353}); the returned flag drives
     * whether the player then learns the curse's identity.
     *
     * <p>Takes the curse and the item, and nothing else. The curse's per-object
     * {@link CurseData} is not wanted: the effect is a property of the curse itself, read from the
     * template's own object ({@code curse->obj->effect} and its message), while the timeout that
     * brought us here has already been dealt with by the caller. C's signature is the same shape,
     * taking a curse index rather than the instance data.
     *
     * <p>The return is a discovery, not a success: it reports whether something happened that the
     * player was not already expecting, which is what makes a previously unknown curse worth
     * revealing. C computes it as {@code !was_aware && ident}.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the effect runtime; reports {@code false}
     * (nothing happened, so the curse is not revealed).</p>
     *
     * <p>Function doCurseEffect coded before 260817, retyped from taking a {@code CurseEntry} on
     * 260817, commented in full on 260817.
     *
     * @param curse the curse whose effect is firing
     * @param item  the worn item the curse is attached to (the effect's source)
     * @return {@code true} if the effect did something the player would notice
     * @author Rowan Crowther
     */
    public static boolean doCurseEffect(Curse curse, ItemObject item) {
        // Stub class TODO: implement
        return false;
    }
}
