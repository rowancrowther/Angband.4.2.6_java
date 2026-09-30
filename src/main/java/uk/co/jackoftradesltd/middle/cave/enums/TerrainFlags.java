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

package uk.co.jackoftradesltd.middle.cave.enums;

/**
 * The set of distinct terrain feature types a dungeon grid can be — floor, the
 * various doors, staircases, the eight store entrances, walls/veins, rubble,
 * lava, etc.
 *
 * <p>Despite the name, this is <em>not</em> the port of the {@code TF_*} property
 * flags (that is {@link TerrainFeatureFlags}). It is the Java form of the C
 * {@code FEAT_*} feature indexes: {@code list-terrain.h} lists them as
 * {@code FEAT(name)} lines, and {@code cave.h} expands that list into an anonymous
 * enum ending in {@code FEAT_MAX}. The constants here follow that header in order,
 * so every ordinal equals the C value: {@code FEAT_NONE} is 0, {@code FEAT_FLOOR} is
 * 1, {@code FEAT_PASS_RUBBLE} is 24 and {@code FEAT_MAX} is 25. The C header warns
 * that reordering breaks savefiles, and {@code Feature} relies on the same order
 * when it treats {@code code.ordinal()} as the index of a {@code terrain.txt} entry.
 *
 * <p>{@code FEAT_MAX} is a sentinel rather than a terrain: it is the number of
 * terrain types, and no grid should ever hold it. In C the terrain code is stored as
 * an unsigned 8-bit integer, so there can be at most 256 types.
 *
 * <p>Class TerrainFlags coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public enum TerrainFlags {
    /**
     * Nothing or unknown; index 0 of the C list. The terrain of a grid the player has
     * never seen.
     */
    FEAT_NONE, /* nothing/unknown */
    /**
     * Open floor.
     */
    FEAT_FLOOR, /* open floor */
    /** A closed door. */
    FEAT_CLOSED, /* closed door */
    /** An open door. */
    FEAT_OPEN, /* open door */
    /** A broken door. */
    FEAT_BROKEN, /* broken door */
    /** An up staircase. */
    FEAT_LESS, /* up staircase */
    /** A down staircase. */
    FEAT_MORE, /* down staircase */
    /** The entrance to the General Store. */
    FEAT_STORE_GENERAL,
    /** The entrance to the Armoury. */
    FEAT_STORE_ARMOR,
    /** The entrance to the Weapon Smiths. */
    FEAT_STORE_WEAPON,
    /** The entrance to the Bookseller. */
    FEAT_STORE_BOOK,
    /** The entrance to the Alchemy Shop. */
    FEAT_STORE_ALCHEMY,
    /** The entrance to the Magic Shop. */
    FEAT_STORE_MAGIC,
    /** The entrance to the Black Market. */
    FEAT_STORE_BLACK,
    /** The entrance to the player's home. */
    FEAT_HOME,
    /** A secret door, which looks like a wall until it is found. */
    FEAT_SECRET, /* secret door */
    /** Impassable rubble. */
    FEAT_RUBBLE, /* impassable rubble */
    /** A magma vein wall. */
    FEAT_MAGMA, /* magma vein wall */
    /** A quartz vein wall. */
    FEAT_QUARTZ, /* quartz vein wall */
    /** A magma vein wall with treasure. */
    FEAT_MAGMA_K, /* magma vein wall with treasure */
    /** A quartz vein wall with treasure. */
    FEAT_QUARTZ_K, /* quartz vein wall with treasure */
    /** A granite wall. */
    FEAT_GRANITE, /* granite wall */
    /** A permanent wall, which cannot be dug. */
    FEAT_PERM, /* permanent wall */
    /** Lava. */
    FEAT_LAVA,
    /** Passable rubble, which can be walked through. */
    FEAT_PASS_RUBBLE,
    /** Sentinel: the number of terrain types. Never a real terrain. */
    FEAT_MAX
}