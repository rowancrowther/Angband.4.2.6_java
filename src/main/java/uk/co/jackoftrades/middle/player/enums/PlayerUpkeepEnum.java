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

package uk.co.jackoftrades.middle.player.enums;

/**
 * "Update" flags identifying which derived player quantity has gone stale and must be
 * recalculated — the {@code PU_*} set from the C original ({@code player-calcs.h}).
 *
 * <p>Sibling to {@link PlayerRedraw}: where {@code PR_*} marks parts of the <em>screen</em>
 * needing a repaint, {@code PU_*} marks parts of the <em>model</em> needing recomputation
 * (bonuses, hit points, field of view, the monster list, …). When state changes, code raises
 * the relevant {@code PU_*} flags on the player's upkeep and a later update pass recomputes
 * only those, clearing each as it goes. Recomputing on demand rather than after every
 * mutation keeps per-turn work bounded.
 *
 * <p>Held as an {@link java.util.EnumSet} in the port instead of C's packed bitflags, for the
 * same type-safety reason as the other flag families.
 *
 * @author ClaudeCode
 */
public enum PlayerUpkeepEnum {
    /**
     * Recalculate all derived bonuses (to-hit, AC, resistances, speed, …).
     */
    PU_BONUS,
    /** Recalculate light radius / torch burn. */
    PU_TORCH,
    /** Recalculate maximum hit points. */
    PU_HP,
    /** Recalculate maximum spell points (mana). */
    PU_MANA,
    /** Recalculate the set of learnable / known spells. */
    PU_SPELLS,
    /** Rebuild the field of view — what the player can currently see. */
    PU_UPDATE_VIEW,
    /** Recompute monster visibility and awareness. */
    PU_MONSTERS,
    /** Recompute distances to the player (e.g. noise/scent flow). */
    PU_DISTANCE,
    /** Recompute the visible map panel / viewport. */
    PU_PANEL,
    /** Rebuild inventory-derived state. */
    PU_INVEN
}
