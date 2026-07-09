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

package uk.co.jackoftrades.middle.enums;

/**
 * The base quantities an effect's magnitude can scale from (spell power, player
 * or dungeon level, weapon damage, the player's HP, etc.).
 * The constants are self-describing and documented collectively here.
 *
 * @author Rowan Crowther
 */
public enum EffectBaseType {
    EFB_NONE,
    EFB_SPELL_POWER,
    EFB_PLAYER_LEVEL,
    EFB_DUNGEON_LEVEL,
    EFB_MAX_SIGHT,
    EFB_WEAPON_DAMAGE,
    EFB_PLAYER_HP,
    EFB_MONSTER_PERCENT_HP_GONE,
    EFB_MAX
}