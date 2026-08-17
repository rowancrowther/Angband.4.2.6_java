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

package uk.co.jackoftrades.middle.cave.profiles.room;

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.RoomFlags;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.List;

/**
 * A room template loaded from {@code room_template.txt} — the port of C's
 * {@code struct room_template} ({@code generate.h}). Built by
 * {@link uk.co.jackoftrades.backend.parser.roomprofile.RoomProfileAssembler}
 * from a {@link uk.co.jackoftrades.backend.parser.roomprofile.RoomProfileParseRecord}
 * once every field has been resolved to its typed form (integers parsed, {@code tval:}
 * resolved to a {@link TValue}, {@code flags:} resolved to a {@link Flag} of
 * {@link RoomFlags}).
 *
 * <p>Where C stores the room layout as one flat {@code char *text} buffer and derives row
 * boundaries from {@code hgt}/{@code wid} at build time, this keeps both: {@link #mapText}
 * for parity with C, and {@link #map} (one string per {@code D:} line) for callers that want
 * row-by-row access without re-deriving it.
 *
 * <p>C's field names ({@code hgt}, {@code wid}, {@code dor}) are spelled out here as
 * {@link #height}, {@link #width} and {@link #doors}; {@code typ} and {@code rat} keep their
 * short C names as {@link #type} and {@link #rating}, matching the {@code type:}/{@code rating:}
 * directives they come from.
 *
 * @author Rowan Crowther
 */
public class RoomTemplate {
    /** The room's name, from the {@code name:} directive. */
    private String name;
    /** Every {@code D:} line concatenated with no separator, matching C's flat {@code text} buffer. */
    private String mapText;
    /** The room layout as one string per {@code D:} line, in file order. */
    private List<String> map;
    /** The flags set on this room via the (optional) {@code flags:} directive. */
    private Flag<RoomFlags> flags;
    /** The room's type, from {@code type:}. Every template in the current data uses {@code 1}. */
    private int type;
    /** The room's rating, from {@code rating:} — what a dungeon profile selects templates by. */
    private int rating;
    /** Number of rows, from {@code rows:}; C's {@code hgt}. */
    private int height;
    /** Number of columns, from {@code columns:}; C's {@code wid}. */
    private int width;
    /** Number of possible door positions, from {@code doors:}; C's {@code dor}. */
    private int doors;
    /** The tval objects placed at {@code [} squares in this room must have, from {@code tval:}. */
    private TValue tval;

    /**
     * @param name    the room's name
     * @param mapText every {@code D:} line concatenated with no separator
     * @param map     the room layout as one string per {@code D:} line
     * @param flags   the flags set on this room
     * @param type    the room's type
     * @param rating  the room's rating
     * @param height  number of rows
     * @param width   number of columns
     * @param doors   number of possible door positions
     * @param tval    the tval objects placed in this room must have
     */
    public RoomTemplate(String name, String mapText, List<String> map,
                        Flag<RoomFlags> flags, int type, int rating, int height,
                        int width, int doors, TValue tval) {
        this.name = name;
        this.mapText = mapText;
        this.map = map;
        this.flags = flags;
        this.type = type;
        this.rating = rating;
        this.height = height;
        this.width = width;
        this.doors = doors;
        this.tval = tval;
    }

    /** @return the room's name */
    public String getName() {
        return name;
    }

    /** @return every {@code D:} line concatenated with no separator, matching C's flat {@code text} buffer */
    public String getMapText() {
        return mapText;
    }

    /** @return the room layout as one string per {@code D:} line, in file order */
    public List<String> getMap() {
        return map;
    }

    /** @return the flags set on this room */
    public Flag<RoomFlags> getFlags() {
        return flags;
    }

    /** @return the room's type */
    public int getType() {
        return type;
    }

    /** @return the room's rating — what a dungeon profile selects templates by */
    public int getRating() {
        return rating;
    }

    /** @return number of rows; C's {@code hgt} */
    public int getHeight() {
        return height;
    }

    /** @return number of columns; C's {@code wid} */
    public int getWidth() {
        return width;
    }

    /** @return number of possible door positions; C's {@code dor} */
    public int getDoors() {
        return doors;
    }

    /** @return the tval objects placed at {@code [} squares in this room must have */
    public TValue getTval() {
        return tval;
    }
}
