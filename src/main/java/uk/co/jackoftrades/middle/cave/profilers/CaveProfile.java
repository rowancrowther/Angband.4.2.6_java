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

package uk.co.jackoftrades.middle.cave.profilers;

import java.util.List;

/**
 * One style of level and everything that governs how it is built — the port of C's
 * {@code struct cave_profile} (generate.h:218), loaded from {@code dungeon_profile.txt}.
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
 * @author Rowan Crowther
 */
public class CaveProfile {
    /**
     * The profile's name, which is also the level builder it selects.
     */
    private String name;

    /**
     * The edge of the square block rooms are allocated in. Rooms take a whole number of blocks and
     * do not share them, so this sets how densely rooms pack and how many can fit at all.
     */
    private int blockSize;

    /**
     * How many rooms to aim for on a level of this style.
     */
    private int dunRooms;

    /**
     * How strongly rare rooms are penalised; larger values make them rarer still.
     */
    private int dunUnusual;

    /**
     * The highest room rarity this profile allows.
     */
    private int maxRarity;

    /**
     * How corridors are dug on this style of level.
     */
    private TunnelProfile tun;

    /**
     * The mineral veins drawn through this style of level.
     */
    private StreamerProfile str;

    /**
     * The rooms this style may contain, in file order — the order the cutoff scan walks.
     */
    private List<RoomProfile> roomProfiles;

    /**
     * The shallowest depth this profile may be used at.
     */
    private int minLevel;

    /**
     * Selection weight against the other profiles legal at a given depth: the chance of being
     * chosen is this divided by the total weight of the candidates. Zero, or less than -1,
     * disables the profile; -1 means it is reachable only through the hard-coded checks in
     * {@code generate.c}, which is how town, moria and labyrinth are selected.
     */
    private int alloc;

    /**
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
     * @author Rowan Crowther
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
     * @return the profile's name, which is also the level builder it selects
     */
    public String getName() {
        return name;
    }

    /**
     * @return the edge of the square block rooms are allocated in
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * @return how many rooms to aim for on a level of this style
     */
    public int getDunRooms() {
        return dunRooms;
    }

    /**
     * @return how strongly rare rooms are penalised
     */
    public int getDunUnusual() {
        return dunUnusual;
    }

    /**
     * @return the highest room rarity this profile allows
     */
    public int getMaxRarity() {
        return maxRarity;
    }

    /**
     * @return how corridors are dug, or {@code null} if the file gave no tunnel line
     */
    public TunnelProfile getTun() {
        return tun;
    }

    /**
     * @return the mineral veins, or {@code null} if the file gave no streamer line
     */
    public StreamerProfile getStr() {
        return str;
    }

    /**
     * @return the rooms this style may contain, in the order the cutoff scan walks them
     */
    public List<RoomProfile> getRoomProfiles() {
        return roomProfiles;
    }

    /**
     * @return the shallowest depth this profile may be used at
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * @return the selection weight; zero or below -1 disables the profile, -1 restricts it to the
     * hard-coded checks in {@code generate.c}
     */
    public int getAlloc() {
        return alloc;
    }
}
