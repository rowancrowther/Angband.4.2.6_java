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

package uk.co.jackoftrades.middle.cave.profilers.dungeon;

import uk.co.jackoftrades.middle.cave.roombuilders.RoomType;

/**
 * One kind of room a level style may contain, and the terms on which it may appear — the port of
 * C's {@code struct room_profile} (generate.h:247), loaded from a {@code room:} line of
 * {@code dungeon_profile.txt}.
 *
 * <p>A {@link CaveProfile} holds these in file order, and that order is load-bearing: when a
 * rarity has been chosen the generator rolls 0-99 and walks the list for a room whose
 * {@link #cutoff} clears the roll, so a room listed after one with a larger cutoff is only
 * reached when the earlier room fails to place.
 *
 * <p>Where C stores a {@code room_builder} function pointer resolved from the name at parse time,
 * the port stores the {@link RoomType} the name resolves to, which carries the builder on the
 * enum constant. C's {@code next} field is dropped in favour of the enclosing {@link java.util.List}.
 *
 * @author Rowan Crowther
 */
public class RoomProfile {
    /**
     * The room's name as the data file gives it, which is also how it resolves to a room builder.
     */
    private String name;

    /**
     * The room type the name resolved to — the port's stand-in for C's builder function pointer.
     */
    private RoomType roomType;

    /**
     * Selects between variants of the room; used only by template rooms.
     */
    private int rating;

    /**
     * The rows to reserve for this room.
     */
    private int height;

    /**
     * The columns to reserve for this room.
     */
    private int width;

    /**
     * The shallowest depth this room may appear at.
     */
    private int level;

    /**
     * Whether this room is a pit or nest, and so stocked from {@code pit.txt}.
     */
    private boolean pit;

    /**
     * How unusual the room is — normally 0, 1 or 2. Rooms chosen by other means usually sit at 0.
     */
    private int rarity;

    /**
     * The room is eligible when a 0-99 roll comes in under this. Rooms of the same rarity within
     * a profile normally list ascending cutoffs.
     */
    private int cutoff;

    /**
     * @param name     the room's name as the data file gives it
     * @param roomType the room type that name resolved to, or {@code null} if none matched
     * @param rating   selects between variants; used only by template rooms
     * @param height   the rows to reserve for the room
     * @param width    the columns to reserve for the room
     * @param level    the shallowest depth this room may appear at
     * @param pit      whether the room is a pit or nest
     * @param rarity   how unusual the room is
     * @param cutoff   the room is eligible if a 0-99 roll comes in under this
     * @author Rowan Crowther
     */
    public RoomProfile(String name, RoomType roomType, int rating, int height,
                       int width, int level, boolean pit, int rarity, int cutoff) {
        this.name = name;
        this.roomType = roomType;
        this.rating = rating;
        this.height = height;
        this.width = width;
        this.level = level;
        this.pit = pit;
        this.rarity = rarity;
        this.cutoff = cutoff;
    }

    public String getName() {
        return name;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getRating() {
        return rating;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLevel() {
        return level;
    }

    public boolean isPit() {
        return pit;
    }

    public int getRarity() {
        return rarity;
    }

    public int getCutoff() {
        return cutoff;
    }
}
