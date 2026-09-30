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

package uk.co.jackoftradesltd.middle.cave.profiles.dungeon;

import java.util.List;

/**
 * One style of level and everything that governs how it is built — the port of C's
 * {@code struct cave_profile} in {@code generate.h}, loaded from {@code dungeon_profile.txt}.
 *
 * <p>The town, a labyrinth and a cavern are all cave profiles, as are the four ordinary dungeon
 * styles. When a level is generated the game picks one profile by depth and weight, then builds
 * the level entirely according to what that profile says: how much space rooms get, how corridors
 * wander, what veins run through the rock, and which rooms may appear.
 *
 * <p>Two of C's fields are absent. {@code next} is gone because a {@link List} holds the profiles
 * instead of a linked list, and so is {@code n_room_profiles}, which only existed to size the
 * flattened array. The {@code builder} function pointer, which C resolves against
 * {@code cave_builders[]} at parse time, is not yet carried here.
 *
 * <p>C embeds {@code tun} and {@code str} by value, so an absent {@code tunnel:} or
 * {@code streamer:} line leaves them zeroed. Here they are references and stay {@code null}
 * instead; callers must handle that. The class is an immutable-by-convention data holder: it has
 * no setters and no behaviour, and the selection logic that reads it lives with the generator.
 *
 * <p>Class CaveProfile coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class CaveProfile {
    /**
     * The profile's name, which is also the level builder it selects.
     *
     * <p>In C the {@code name:} line looks the string up in {@code cave_builders[]} and the parse
     * fails with {@code PARSE_ERROR_NO_BUILDER_FOUND} if nothing matches, so every loaded name
     * corresponds to a real builder. Names are also how {@code generate.c} finds the town, moria
     * and labyrinth profiles ({@code find_cave_profile()}).
     *
     * <p>Field name coded before 260930, commented in full on 260930.
     */
    private String name;

    /**
     * The edge of the square block rooms are allocated in. Rooms take a whole number of blocks and
     * do not share them, so this sets how densely rooms pack and how many can fit at all.
     *
     * <p>Read from the {@code block} value of the {@code params:} line ({@code block_size} in C).
     * The level is divided into non-overlapping square blocks of this many grids on a side, and
     * each room is assigned a rectangle of whole blocks.
     *
     * <p>Field blockSize coded before 260930, commented in full on 260930.
     */
    private int blockSize;

    /**
     * How many rooms to aim for on a level of this style.
     *
     * <p>Read from the {@code rooms} value of the {@code params:} line ({@code dun_rooms} in C).
     * It is a target the builder attempts, not a guarantee: placement can fail when no free run
     * of blocks fits the room.
     *
     * <p>Field dunRooms coded before 260930, commented in full on 260930.
     */
    private int dunRooms;

    /**
     * How strongly rare rooms are penalised; larger values make them rarer still.
     *
     * <p>Read from the {@code unusual} value of the {@code params:} line ({@code dun_unusual} in
     * C). Used together with the level depth when the builder rolls how unusual a room may be,
     * capped by {@link #maxRarity}.
     *
     * <p>Field dunUnusual coded before 260930, commented in full on 260930.
     */
    private int dunUnusual;

    /**
     * The highest room rarity this profile allows.
     *
     * <p>Read from the {@code rarity} value of the {@code params:} line ({@code max_rarity} in C).
     * Each {@link RoomProfile} carries its own rarity, normally 0, 1 or 2; a room whose rarity
     * exceeds this value cannot be chosen on levels of this style.
     *
     * <p>Field maxRarity coded before 260930, commented in full on 260930.
     */
    private int maxRarity;

    /**
     * How corridors are dug on this style of level.
     *
     * <p>Built from the {@code tunnel:} line ({@code rnd}, {@code chg}, {@code con}, {@code pen},
     * {@code jct}: percentage chances of a random heading, a change of direction, an early stop, a
     * door at a room entrance and a door at a junction). C holds this by value, zeroed if the line
     * is missing; here it is {@code null} in that case.
     *
     * <p>Field tun coded before 260930, commented in full on 260930.
     */
    private TunnelProfile tun;

    /**
     * The mineral veins drawn through this style of level.
     *
     * <p>Built from the {@code streamer:} line ({@code den}, {@code rng}, {@code mag}, {@code mc},
     * {@code qua}, {@code qc}: vein thickness, spread, the number of magma and quartz streamers,
     * and the 1-in-N chance of treasure in each). C holds this by value, zeroed if the line is
     * missing; here it is {@code null} in that case.
     *
     * <p>Field str coded before 260930, commented in full on 260930.
     */
    private StreamerProfile str;

    /**
     * The rooms this style may contain, in file order — the order the cutoff scan walks.
     *
     * <p>C builds a linked list one {@code room:} line at a time, appending at the tail, then
     * flattens it into an array in {@code finish_parse_profile()}. A {@link List} in file order is
     * the equivalent; the order matters because the cutoff scan takes the first room whose cutoff
     * beats the roll.
     *
     * <p>Field roomProfiles coded before 260930, commented in full on 260930.
     */
    private List<RoomProfile> roomProfiles;

    /**
     * The shallowest depth this profile may be used at.
     *
     * <p>Read from the {@code min-level:} line ({@code min_level} in C). In the weighted
     * pick in {@code generate.c} a profile is skipped when the level's depth is less than this
     * value, so the depth is inclusive: a profile with {@code min-level} 10 is a candidate at 10.
     *
     * <p>Field minLevel coded before 260930, commented in full on 260930.
     */
    private int minLevel;

    /**
     * Selection weight against the other profiles legal at a given depth: the chance of being
     * chosen is this divided by the total weight of the candidates. Zero, or less than -1,
     * disables the profile; -1 means it is reachable only through the hard-coded checks in
     * {@code generate.c}, which is how town, moria and labyrinth are selected.
     *
     * <p>The pick is a running weighted choice over the candidates, and {@code alloc <= 0}
     * profiles are skipped by it. A -1 on a profile the hard-coded checks do not know about
     * behaves exactly like 0.
     *
     * <p>Field alloc coded before 260930, commented in full on 260930.
     */
    private int alloc;

    /**
     * Builds a profile from the values the {@code dungeon_profile.txt} parser collects.
     *
     * <p>Arguments are stored as given: there is no validation, no defensive copy of
     * {@code roomProfiles}, and no defaulting of {@code tun} or {@code str}. That matches C, where
     * the parser fills a zeroed struct field by field and checks nothing beyond the builder name.
     *
     * <p>Function CaveProfile coded before 260930, commented in full on 260930.
     *
     * @param name         the profile's name, matching a level builder
     * @param blockSize    the edge of the square block rooms are allocated in
     * @param dunRooms     how many rooms to aim for
     * @param dunUnusual   how strongly rare rooms are penalised
     * @param maxRarity    the highest room rarity allowed
     * @param tun          how corridors are dug, or {@code null} if the file gave no tunnel line
     * @param str          the mineral veins, or {@code null} if the file gave no streamer line
     * @param roomProfiles the rooms this style may contain, in file order
     * @param minLevel     the shallowest usable depth
     * @param alloc        the selection weight
     */
    public CaveProfile(String name, int blockSize, int dunRooms, int dunUnusual, int maxRarity,
                       TunnelProfile tun, StreamerProfile str, List<RoomProfile> roomProfiles,
                       int minLevel, int alloc) {
        this.name = name;
        this.blockSize = blockSize;
        this.dunRooms = dunRooms;
        this.dunUnusual = dunUnusual;
        this.maxRarity = maxRarity;
        this.tun = tun;
        this.str = str;
        this.roomProfiles = roomProfiles;
        this.minLevel = minLevel;
        this.alloc = alloc;
    }

    /**
     * Returns the profile's name, which is also the level builder it selects.
     *
     * <p>Function getName coded before 260930, commented in full on 260930.
     *
     * @return the profile's name, which is also the level builder it selects
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the edge, in grids, of the square block rooms are allocated in.
     *
     * <p>Function getBlockSize coded before 260930, commented in full on 260930.
     *
     * @return the edge of the square block rooms are allocated in
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Returns the number of rooms the builder attempts to place; a target, not a guarantee.
     *
     * <p>Function getDunRooms coded before 260930, commented in full on 260930.
     *
     * @return how many rooms to aim for on a level of this style
     */
    public int getDunRooms() {
        return dunRooms;
    }

    /**
     * Returns the unusual-room measure; higher values make rare rooms rarer.
     *
     * <p>Function getDunUnusual coded before 260930, commented in full on 260930.
     *
     * @return how strongly rare rooms are penalised
     */
    public int getDunUnusual() {
        return dunUnusual;
    }

    /**
     * Returns the highest room rarity this profile permits.
     *
     * <p>Function getMaxRarity coded before 260930, commented in full on 260930.
     *
     * @return the highest room rarity this profile allows
     */
    public int getMaxRarity() {
        return maxRarity;
    }

    /**
     * Returns the corridor parameters, or {@code null} where C would have held a zeroed struct.
     * Callers must null-check before reading the individual percentages.
     *
     * <p>Function getTun coded before 260930, commented in full on 260930.
     *
     * @return how corridors are dug, or {@code null} if the file gave no tunnel line
     */
    public TunnelProfile getTun() {
        return tun;
    }

    /**
     * Returns the mineral vein parameters, or {@code null} where C would have held a zeroed
     * struct. Callers must null-check before reading them.
     *
     * <p>Function getStr coded before 260930, commented in full on 260930.
     *
     * @return the mineral veins, or {@code null} if the file gave no streamer line
     */
    public StreamerProfile getStr() {
        return str;
    }

    /**
     * Returns the live list of rooms this style may contain, in file order. It is not copied, so
     * the order must be preserved by any caller: the cutoff scan depends on it.
     *
     * <p>Function getRoomProfiles coded before 260930, commented in full on 260930.
     *
     * @return the rooms this style may contain, in the order the cutoff scan walks them
     */
    public List<RoomProfile> getRoomProfiles() {
        return roomProfiles;
    }

    /**
     * Returns the shallowest depth at which this profile may be chosen.
     *
     * <p>Function getMinLevel coded before 260930, commented in full on 260930.
     *
     * @return the shallowest depth this profile may be used at
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * Returns the selection weight. The raw value is returned, so callers must treat 0, -1 and
     * values below -1 specially rather than using it as a probability directly.
     *
     * <p>Function getAlloc coded before 260930, commented in full on 260930.
     *
     * @return the selection weight; zero or below -1 disables the profile, -1 restricts it to the
     * hard-coded checks in {@code generate.c}
     */
    public int getAlloc() {
        return alloc;
    }
}
