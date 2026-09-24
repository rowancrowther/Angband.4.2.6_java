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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.Player;

/**
 * The object-level flag cache reused across a batch of UI-entry evaluations for one item — the port
 * of C's {@code struct cached_object_data} ({@code ui-entry.c:46-48}). C allocates one of these per
 * object at the top of {@code compute_ui_entry_values_for_object} and reuses it for every
 * {@code ui_entry} bound to that object in the same pass, filling it with either the object's known
 * flags or its real ones depending on whether a viewing player was supplied
 * ({@code ui-entry.c:679-687}). This class is the plain cache; the loop that walks an object's bound
 * properties using it lives in {@code UIEntryValueRegistry.computeForObject}.
 *
 * @author Rowan Crowther
 */
public class ObjectValueCache {
    /**
     * The resolved object flags for this pass (C: {@code cached_object_data.f}).
     */
    private Flag<ObjectFlag> resolvedFlags;

    /**
     * Fills {@link #resolvedFlags} the first time it is called, and does nothing on any later call
     * for the same cache — the port of C's {@code if (*cache == NULL) { ... }} guard
     * ({@code ui-entry.c:679-687}). With a {@code player} given, the object's <em>known</em> flags
     * are cached ({@code object_flags_known}); with none, its real flags are ({@code object_flags}),
     * matching C's choice between the two based on whether {@code p} is null.
     *
     * @param item   the object whose flags are being cached
     * @param player the player assessing the object's knowledge, or {@code null} to assume every
     *               flag is known
     */
    void populateFlags(ItemObject item, Player player) {
        if (resolvedFlags == null) {
            if (player != null) {
                resolvedFlags = item.flagsKnown();
            } else {
                resolvedFlags = item.getFlags();
            }
        }
    }

    /**
     * @return the flags {@link #populateFlags(ItemObject, Player)} resolved, or {@code null} if it
     * has not yet been called
     */
    Flag<ObjectFlag> getResolvedFlags() {
        return resolvedFlags;
    }

    /**
     * Overwrites the cached flags directly, bypassing {@link #populateFlags(ItemObject, Player)}'s
     * once-only guard.
     *
     * @param objectFlags the flags to store
     */
    public void setResolvedFlags(Flag<ObjectFlag> objectFlags) {
        resolvedFlags = objectFlags;
    }
}
