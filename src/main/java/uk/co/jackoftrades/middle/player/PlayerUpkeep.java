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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.objects.Pile;

/**
 * The player's transient runtime bookkeeping — the recomputed-each-session state that is
 * <em>not</em> part of the saved character, as opposed to the persistent data held on
 * {@link Player}.
 *
 * <p>Ports the C {@code struct player_upkeep} ({@code player.h}). In the original this carries
 * the pending redraw ({@code PR_*}) and update ({@code PU_*}) flag sets, current targeting, the
 * floor object pile under the player, and similar volatile fields that are rebuilt rather than
 * serialised. Keeping it separate from the saved player state is exactly what lets the engine
 * discard the upkeep and recompute it on load.
 *
 * <p><b>Status:</b> partially ported — currently only the floor object pile is modelled.
 *
 * @author Rowan Crowther
 */
public class PlayerUpkeep {
    /**
     * The pile of objects on the floor beneath the player.
     */
    private Pile objectPile;

    /**
     * @return the pile of objects currently under the player
     */
    public Pile getPile() {
        return objectPile;
    }
}
