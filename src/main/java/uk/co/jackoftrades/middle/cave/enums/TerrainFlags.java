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

package uk.co.jackoftrades.middle.cave.enums;

public enum TerrainFlags {
    FEAT_NONE, /* nothing/unknown */
    FEAT_FLOOR, /* open floor */
    FEAT_CLOSED, /* closed door */
    FEAT_OPEN, /* open door */
    FEAT_BROKEN, /* broken door */
    FEAT_LESS, /* up staircase */
    FEAT_MORE, /* down staircase */
    FEAT_STORE_GENERAL,
    FEAT_STORE_ARMOR,
    FEAT_STORE_WEAPON,
    FEAT_STORE_BOOK,
    FEAT_STORE_ALCHEMY,
    FEAT_STORE_MAGIC,
    FEAT_STORE_BLACK,
    FEAT_HOME,
    FEAT_SECRET, /* secret door */
    FEAT_RUBBLE, /* impassable rubble */
    FEAT_MAGMA, /* magma vein wall */
    FEAT_QUARTZ, /* quartz vein wall */
    FEAT_MAGMA_K, /* magma vein wall with treasure */
    FEAT_QUARTZ_K, /* quartz vein wall with treasure */
    FEAT_GRANITE, /* granite wall */
    FEAT_PERM, /* permanent wall */
    FEAT_LAVA,
    FEAT_PASS_RUBBLE,
    FEAT_MAX
}
