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
 * "Redraw" flags identifying which on-screen region has become stale and needs
 * repainting — the {@code PR_*} set from the C original ({@code player-calcs.h}).
 *
 * <p>When game state changes, code raises the relevant {@code PR_*} flag(s) on the
 * player's upkeep ({@link uk.co.jackoftrades.middle.player.PlayerUpkeep}); the render
 * loop later consults them and repaints <em>only</em> the flagged areas, clearing each
 * flag as it goes. This is the engine's dirty-region mechanism: it avoids redrawing
 * the whole terminal every turn, which matters for responsiveness on a character cell
 * display.
 *
 * <p><b>Why an enum rather than C bit constants:</b> the original packs these as bit
 * positions in a single integer and OR-s them together. The port represents the set
 * with an {@link java.util.EnumSet}, trading bit-twiddling for type-safety (a redraw
 * flag can never be mixed up with an update/{@code PU_*} or notice flag) while keeping
 * the same declared order as the C list.
 *
 * @author ClaudeCode
 */
public enum PlayerRedraw {
    /**
     * Miscellaneous header fields (race / class line).
     */
    PR_MISC,
    /** The character's title (changes with class level). */
    PR_TITLE,
    /** Experience level. */
    PR_LEV,
    /** Experience points. */
    PR_EXP,
    /** The six primary stats. */
    PR_STATS,
    /** Armour class. */
    PR_ARMOR,
    /** Hit points (current / maximum). */
    PR_HP,
    /** Spell points / mana. */
    PR_MANA,
    /** Gold total. */
    PR_GOLD,
    /** The monster health bar. */
    PR_HEALTH,
    /** Movement speed. */
    PR_SPEED,
    /** Number of spells available to study. */
    PR_STUDY,
    /** Current dungeon depth. */
    PR_DEPTH,
    /** Status-line indicators (hunger, conditions, …). */
    PR_STATUS,
    /** Trap-detection boundary indicator. */
    PR_DTRAP,
    /** Action state (resting, searching, etc.). */
    PR_STATE,
    /** The dungeon map view. */
    PR_MAP,
    /** Inventory listing. */
    PR_INVEN,
    /** Equipment listing. */
    PR_EQUIP,
    /** Message line. */
    PR_MESSAGE,
    /** The currently targeted/tracked monster's recall. */
    PR_MONSTER,
    /** The currently tracked object's details. */
    PR_OBJECT,
    /** The visible-monster list subwindow. */
    PR_MONLIST,
    /** The visible-item list subwindow. */
    PR_ITEMLIST,
    /** The level-feeling indicator. */
    PR_FEELING,
    /** The player's light radius (affects what must be repainted). */
    PR_LIGHT
}
