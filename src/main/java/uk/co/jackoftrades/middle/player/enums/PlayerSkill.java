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
 * The player's skill axes — the {@code SKILL_*} proficiencies that race, class and level
 * contribute to and that are resolved into concrete combat/utility bonuses.
 *
 * <p>Ports the C {@code SKILL_*} enumeration. These index the per-skill values held in
 * {@link uk.co.jackoftrades.middle.player.PlayerState} and supplied by the race/class
 * skill tables, each turned into an effective bonus during the player's recalculation pass.
 *
 * <p>{@code SKILL_NONE} and {@code SKILL_MAX} are the sentinel/count markers carried over
 * from the C enum so any array-sizing or iteration that depended on them still works.
 *
 * @author ClaudeCode
 */
public enum PlayerSkill {
    /**
     * Sentinel "no skill" (index 0).
     */
    SKILL_NONE,
    /** Disarming physical (mechanical) traps. */
    SKILL_DISARM_PHYS,
    /** Disarming magical traps. */
    SKILL_DISARM_MAGIC,
    /** Using magical devices — wands, rods and staves. */
    SKILL_DEVICE,
    /** Saving throws against harmful effects. */
    SKILL_SAVE,
    /** Searching for hidden doors and traps. */
    SKILL_SEARCH,
    /** Stealth — avoiding waking or alerting monsters. */
    SKILL_STEALTH,
    /** Accuracy with melee weapons. */
    SKILL_TO_HIT_MELEE,
    /** Accuracy with launchers (bows, slings, crossbows). */
    SKILL_TO_HIT_BOW,
    /** Accuracy with thrown objects. */
    SKILL_TO_HIT_THROW,
    /** Digging through walls and mineral veins. */
    SKILL_DIGGING,
    /** Count marker / upper bound of the skill set (not a real skill). */
    SKILL_MAX
}
