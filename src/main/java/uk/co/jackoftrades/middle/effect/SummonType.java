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

package uk.co.jackoftrades.middle.effect;

/**
 * The categories of monster a summon effect can call — kin, animals, spiders,
 * hounds, dragons, undead, demons, uniques and so on, including the "high"
 * variants. This is the Java port of the C original's {@code summon_types}
 * ({@code src/list-summon-types.h}); the constants are self-describing and
 * documented collectively here.
 *
 * @author ClaudeCode
 */
public enum SummonType {
    SUM_NONE,
    SUM_KIN,
    SUM_MONSTER,
    SUM_MONSTERS,
    SUM_ANIMAL,
    SUM_SPIDER,
    SUM_HOUND,
    SUM_HYDRA,
    SUM_AINU,
    SUM_DEMON,
    SUM_UNDEAD,
    SUM_DRAGON,
    SUM_HI_DEMON,
    SUM_HI_UNDEAD,
    SUM_HI_DRAGON,
    SUM_WRAITH,
    SUM_UNIQUE
}
