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

package uk.co.jackoftradesltd.middle.cave.roombuilders;

import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Loc;

import java.util.stream.Stream;

/**
 * The room types a level may contain — the port of C's {@code room_builders[]} table, which is
 * itself built from {@code list-rooms.h}.
 *
 * <p>Every constant ties a name to the algorithm that carves that kind of room, plus the size cap
 * the room may not exceed. The name is the one {@code dungeon_profile.txt} uses in its
 * {@code room:} lines, so the data file decides which rooms a level style may contain and how
 * often each is attempted.
 *
 * <p>Where C keeps the name, the two size caps and the function pointer in a static array and
 * indexes into it, the enum holds all four on the constant. The three C accessors that existed
 * only to index that array become {@link #getRoomBuilderCount()}, {@link #getIndexFromName} and
 * {@link #getNameFromIndex}.
 *
 * <p>Constants are declared in {@code list-rooms.h} order, because that order is what the indices
 * mean.
 *
 * @author Rowan Crowther
 */
public enum RoomType {
    /**
     * A staircase joining up with one on the level above, or occasionally the level below.
     */
    STAIRCASE("staircase room", 0, 0, new StaircaseRoom()),
    /**
     * A plain rectangular room.
     */
    SIMPLE("simple room", 0, 0, new SimpleRoom()),
    /**
     * Oangband starburst room: large, ragged-edged and roughly oval.
     */
    MORIA("moria room", 0, 0, new MoriaRoom()),
    /**
     * A large room with an inner room, in one of five sub-types.
     */
    LARGE("large room", 0, 0, new LargeRoom()),
    /**
     * Two rectangles crossed, often leaving a central pillar.
     */
    CROSSED("crossed room", 0, 0, new CrossedRoom()),
    /**
     * A circular room, interior radius 4-7.
     */
    CIRCULAR("circular room", 0, 0, new CircularRoom()),
    /**
     * Two overlapping rectangles.
     */
    OVERLAP("overlap room", 0, 0, new OverlapRoom()),
    /**
     * A room laid out from {@code room_template.txt}; the one type that uses the rating.
     */
    TEMPLATE("room template", 11, 33, new TemplateRoom()),
    /**
     * An "interesting room" from {@code vault.txt}, able to place named monsters and objects.
     */
    INTERESTING("Interesting room", 40, 50, new InterestingRoom()),
    /**
     * A monster pit: a moated room laid out by depth, stocked from {@code pit.txt}.
     */
    PIT("monster pit", 0, 0, new PitRoom()),
    /**
     * A monster nest: a moated room of one monster type, stocked from {@code pit.txt}.
     */
    NEST("monster nest", 0, 0, new NestRoom()),
    /**
     * A starburst room of extreme size, only below level 40.
     */
    HUGE("huge room", 0, 0, new HugeRoom()),
    /**
     * Many small irregular chambers joined by doors and short tunnels, heavily populated.
     */
    CHAMBERS("room of chambers", 0, 0, new RoomOfChambers()),
    /**
     * A lesser vault from {@code vault.txt}.
     */
    LESSER_VAULT("Lesser vault", 22, 22, new LesserVault()),
    /**
     * A medium vault from {@code vault.txt}.
     */
    MEDIUM_VAULT("Medium vault", 22, 33, new MediumVault()),
    /**
     * A greater vault from {@code vault.txt}.
     */
    GREATER_VAULT("Greater vault", 44, 66, new GreaterVault()),
    /**
     * A lesser vault of the newer style.
     */
    NEW_LESSER_VAULT("Lesser vault (new)", 22, 22, new NewLesserVault()),
    /**
     * A medium vault of the newer style.
     */
    NEW_MEDIUM_VAULT("Medium vault (new)", 22, 33, new NewMediumVault()),
    /**
     * A greater vault of the newer style.
     */
    NEW_GREATER_VAULT("Greater vault (new)", 44, 66, new NewGreaterVault());

    /**
     * The name this room type is known by in the data files.
     */
    private final String name;

    /**
     * Largest number of rows the room may occupy; 0 where no cap applies.
     */
    private final int maxHeight;

    /**
     * Largest number of columns the room may occupy; 0 where no cap applies.
     */
    private final int maxWidth;

    /**
     * The algorithm that carves a room of this type.
     */
    private final RoomBuilder builder;

    /**
     * @param name      the name the data files refer to this room type by
     * @param maxHeight largest number of rows the room may occupy, or 0 for no cap
     * @param maxWidth  largest number of columns the room may occupy, or 0 for no cap
     * @param builder   the algorithm that carves a room of this type
     */
    RoomType(String name, int maxHeight, int maxWidth, RoomBuilder builder) {
        this.name = name;
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.builder = builder;
    }

    /**
     * Carve a room of this type, by handing off to the builder held on the constant.
     *
     * @param cave   the level under construction
     * @param centre where to centre the room; outside the level means "find your own space"
     * @param rating the room rating to select, where the room type cares about it
     * @return {@code true} if the room was built, {@code false} if it could not be placed
     * @see RoomBuilder#build
     */
    public boolean build(Chunk cave, Loc centre, int rating) {
        return builder.build(cave, centre, rating);
    }

    /**
     * @return the name this room type is known by in the data files
     */
    public String getName() {
        return name;
    }

    /**
     * Find a room type's index from the name the data files use — the port of C's
     * {@code get_room_builder_index_from_name}.
     *
     * <p>C compares with {@code streq}, which is case-sensitive; this match is not. That matters
     * for the six vault types, whose names are the only ones that are capitalised.
     *
     * @param name the name to look for
     * @return the matching room type's index, or -1 if no room type has that name
     */
    public static int getIndexFromName(String name) {
        for (RoomType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type.ordinal();
            }
        }

        return -1;
    }

    /**
     * Get a room type's data-file name from its index — the port of C's
     * {@code get_room_builder_name_from_index}.
     *
     * <p>C bounds-checks and returns {@code NULL} for an index outside the table; here an
     * out-of-range index throws instead.
     *
     * @param index the index to look up
     * @return the name of the room type at that index
     * @throws ArrayIndexOutOfBoundsException if the index is not a valid room type index
     */
    public static String getNameFromIndex(int index) {
        return values()[index].name;
    }

    /**
     * @return how many room types there are — the port of C's {@code get_room_builder_count}
     */
    public static int getRoomBuilderCount() {
        return values().length;
    }

    /**
     * Find a room type by the name the data files use.
     *
     * <p>The same lookup as {@link #getIndexFromName}, returning the constant rather than its
     * index — what the assembler wants, since it stores the type on a {@code RoomProfile} rather
     * than indexing a table. Matching is case-insensitive for the same reason given there.
     *
     * <p>Where C treats an unmatched name as a fatal parse error
     * ({@code PARSE_ERROR_NO_ROOM_FOUND}, generate.c:176), this returns {@code null} and leaves
     * the caller to decide.
     *
     * @param name the name to look for
     * @return the matching room type, or {@code null} if no room type has that name
     */
    public static RoomType getRoomTypeFromName(String name) {
        return Stream.of(values())
                .filter(r -> r.name.equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }
}