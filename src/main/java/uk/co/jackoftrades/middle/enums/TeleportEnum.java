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

package uk.co.jackoftrades.middle.enums;

/**
 * The kinds of teleportation effect the game can apply, distinguishing who/where
 * is moved (self, a target, away, to a location, to another level, …). Mirrors
 * the teleport variants of the C original's effect code.
 *
 * @author ClaudeCode
 */
public enum TeleportEnum {
    /**
     * No teleport. @author ClaudeCode
     */
    TELE_NONE,
    /** Teleport the subject a random short/medium distance. @author ClaudeCode */
    TELE_TELEPORT,
    /** Teleport the subject to a chosen location. @author ClaudeCode */
    TELE_TELEPORT_TO,
    /** Teleport the caster itself. @author ClaudeCode */
    TELE_TELEPORT_SELF,
    /** Teleport the subject to a different dungeon level. @author ClaudeCode */
    TELE_TELEPORT_LEVEL,
    /** Teleport another creature (away from the caster). @author ClaudeCode */
    TELE_TELEPORT_OTHER,
    /** Teleport the subject far away. @author ClaudeCode */
    TELE_TELEPORT_AWAY,
    /** Teleport the subject to a random location. @author ClaudeCode */
    TELE_TELEPORT_RANDOM,
    /** Count sentinel. @author ClaudeCode */
    TELE_MAX
}