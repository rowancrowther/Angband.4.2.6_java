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
 * Which enchantment bonus an "enchant item" effect targets — to-hit, to-damage,
 * both, or armour class. Mirrors the enchant-mode parameter of the C original's
 * effect code.
 *
 * @author ClaudeCode
 */
public enum EffectEnchant {
    /**
     * No enchantment. @author ClaudeCode
     */
    EE_NONE,
    /** Enchant both to-hit and to-damage. @author ClaudeCode */
    EE_TOBOTH,
    /** Enchant to-hit only. @author ClaudeCode */
    EE_TOHIT,
    /** Enchant to-damage only. @author ClaudeCode */
    EE_TODAM,
    /** Enchant armour class. @author ClaudeCode */
    EE_TOAC
}
