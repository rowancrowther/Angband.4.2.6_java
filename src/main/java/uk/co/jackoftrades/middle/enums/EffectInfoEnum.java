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
 * The formatting categories used when building an effect's description string —
 * each value tells the description code how to interpret an effect's parameters
 * (as dice, healing, a cure, a timed effect, a summon, a breath, a bolt, …).
 * Mirrors the C original's {@code EFINFO_*} constants ({@code src/effects-info.c}).
 * The constants are self-describing and are documented collectively here.
 *
 * @author ClaudeCode
 */
public enum EffectInfoEnum {
    EFINFO_NONE,
    EFINFO_DICE,
    EFINFO_HEAL,
    EFINFO_CONST,
    EFINFO_FOOD,
    EFINFO_CURE,
    EFINFO_TIMED,
    EFINFO_STAT,
    EFINFO_SEEN,
    EFINFO_SUMM,
    EFINFO_TELE,
    EFINFO_QUAKE,
    EFINFO_BALL,
    EFINFO_SPOT,
    EFINFO_BREATH,
    EFINFO_SHORT,
    EFINFO_LASH,
    EFINFO_BOLT,
    EFINFO_BOLTD,
    EFINFO_TOUCH
}
