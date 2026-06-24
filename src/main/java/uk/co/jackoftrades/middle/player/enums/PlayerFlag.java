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
 * Per-class innate ability flags — the {@code PF_*} set marking the special powers
 * and quirks a {@link uk.co.jackoftrades.middle.player.PlayerClass} grants (and which
 * a few races contribute to).
 *
 * <p>Ports the {@code PF_*} flags generated from the C X-macro list
 * {@code list-player-flags.h}. In the original these are bit positions packed into a
 * {@code bitflag} array on the class; here each is an enum constant and the "set of
 * flags a class has" is carried by an {@link java.util.EnumSet} rather than raw bit
 * arithmetic.
 *
 * <p><b>Why an enum and not bit constants:</b> the port trades C's compact bitfield
 * for type-safety — a {@code PF_*} value cannot be confused with an unrelated flag
 * family (object flags, redraw flags, …) — while the constant ordering is kept
 * identical to the C list so any index-based data/save compatibility is preserved.
 *
 * @author ClaudeCode
 */
public enum PlayerFlag {
    /**
     * No flag / list terminator (index 0).
     */
    PF_NONE,
    /** Missile attacks cost progressively less energy as the character gains levels (Ranger). */
    PF_FAST_SHOT,
    /** Becomes immune to fear on reaching level 30 (Warrior). */
    PF_BRAVERY_30,
    /** Suffers a melee penalty unless wielding a blessed or blunt/hafted weapon (Priest). */
    PF_BLESS_WEAPON,
    /** Able to drive a spell's failure rate all the way down to 0% (Mage). */
    PF_ZERO_FAIL,
    /** Bolt spells have an increased chance of extending into a beam (Mage). */
    PF_BEAM,
    /** Chooses which spells to learn rather than receiving them at random (Mage). */
    PF_CHOOSE_SPELLS,
    /** Learns a mushroom's effect by eating it. */
    PF_KNOW_MUSHROOM,
    /** Learns a wand's or rod's effect by using it. */
    PF_KNOW_ZAPPER,
    /** Senses mineral veins and buried treasure in nearby walls. */
    PF_SEE_ORE,
    /** Has no spell points at all (Warrior). */
    PF_NO_MANA,
    /** Receives a bonus when charming or otherwise influencing monsters. */
    PF_CHARM,
    /** At home in darkness — unhindered by unlit tiles and able to see without light (Necromancer). */
    PF_UNLIGHT,
    /** Innate class flag governing rock/earth interaction (see {@code list-player-flags.h}). */
    PF_ROCK,
    /** Able to steal from monsters (Rogue). */
    PF_STEAL,
    /** Able to bash an adjacent monster with a wielded shield (Warrior). */
    PF_SHIELD_BASH,
    /** Counts as evil-aligned for effects that test alignment (Necromancer). */
    PF_EVIL,
    /** Uses the combat-regeneration mechanic, tying resource recovery to fighting (Necromancer). */
    PF_COMBAT_REGEN
}