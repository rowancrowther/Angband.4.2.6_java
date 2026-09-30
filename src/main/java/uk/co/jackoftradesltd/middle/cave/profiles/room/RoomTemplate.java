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

package uk.co.jackoftradesltd.middle.cave.profiles.room;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.List;

/**
 * A room template loaded from {@code room_template.txt} — the port of C's
 * {@code struct room_template} ({@code generate.h}). Built by
 * {@link uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileAssembler}
 * from a {@link uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileParseRecord}
 * once every field has been resolved to its typed form (integers parsed, {@code tval:}
 * resolved to a {@link TValue}, {@code flags:} resolved to a {@link Flag} of
 * {@link RoomFlags}).
 *
 * <p>This is a pure data holder: it has no behaviour of its own and every field is fixed at
 * construction. In C the same record is what {@code gen-room.c}'s {@code random_room_template()}
 * filters by {@code typ} and {@code rat}, and what {@code build_room_template_type()} hands,
 * field by field, to {@code build_room_template()} to lay the room out.
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
 * <p>C declares {@code typ}, {@code rat}, {@code hgt}, {@code wid}, {@code dor} and {@code tval}
 * as {@code uint8_t}; here they are {@code int}, so the C truncation to 0..255 is not
 * reproduced. None of the values in the shipped data comes near that limit.
 *
 * <p>Class RoomTemplate coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class RoomTemplate {
    /**
     * The room's name, from the {@code name:} directive; C's {@code name}. In C a {@code name:}
     * line is what starts a new record, and the name is what the level builder reports when it
     * chooses this template.
     *
     * <p>Field name coded before 260930, commented in full on 260930.
     */
    private String name;
    /**
     * Every {@code D:} line concatenated with no separator, matching C's flat {@code text} buffer.
     * C's {@code parse_room_d()} appends each line onto one string, so row boundaries exist only
     * as multiples of {@link #width}. Holds {@link #height} × {@link #width} characters when the
     * template is well formed.
     *
     * <p>Field mapText coded before 260930, commented in full on 260930.
     */
    private String mapText;
    /**
     * The room layout as one string per {@code D:} line, in file order. A Java-side convenience
     * with no C counterpart; it is the same data as {@link #mapText}, pre-split into rows.
     *
     * <p>Field map coded before 260930, commented in full on 260930.
     */
    private List<String> map;
    /**
     * The flags set on this room via the (optional) {@code flags:} directive; C's
     * {@code flags[ROOMF_SIZE]}. Empty, not null, when the record has no {@code flags:} line.
     *
     * <p>Field flags coded before 260930, commented in full on 260930.
     */
    private Flag<RoomFlags> flags;
    /**
     * The room's type, from {@code type:}; C's {@code typ}. Together with {@link #rating} this
     * is the key {@code random_room_template()} matches on. Every template in the current data
     * (500 of 500) uses {@code 1}.
     *
     * <p>Field type coded before 260930, commented in full on 260930.
     */
    private int type;
    /**
     * The room's rating, from {@code rating:}; C's {@code rat}. The room builder asks for a
     * template by type and rating, and picks uniformly at random among those that match both.
     *
     * <p>Field rating coded before 260930, commented in full on 260930.
     */
    private int rating;
    /**
     * Number of rows, from {@code rows:}; C's {@code hgt}. C rejects a template taller than the
     * {@code "room template"} room profile's maximum height when it parses this line.
     *
     * <p>Field height coded before 260930, commented in full on 260930.
     */
    private int height;
    /**
     * Number of columns, from {@code columns:}; C's {@code wid}. C rejects a template wider than
     * the {@code "room template"} room profile's maximum width when it parses this line.
     *
     * <p>Field width coded before 260930, commented in full on 260930.
     */
    private int width;
    /**
     * Number of possible door positions, from {@code doors:}; C's {@code dor}. The builder
     * picks one random value in {@code 1..doors} per room, and every square marked with that
     * digit in the layout becomes a door.
     *
     * <p>Field doors coded before 260930, commented in full on 260930.
     */
    private int doors;
    /**
     * The tval objects placed at {@code [} squares in this room must have, from {@code tval:};
     * C's {@code tval}. {@link TValue#TV_NONE} (C's {@code 0}) when the record has no
     * {@code tval:} line, which lets the object generator choose any kind.
     *
     * <p>Field tval coded before 260930, commented in full on 260930.
     */
    private TValue tval;

    /**
     * Builds a template from already-resolved values. No validation is done here; the
     * {@link uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileAssembler} checks row
     * count and line length before calling this, which C does not.
     *
     * <p>Constructor RoomTemplate coded before 260930, commented in full on 260930.
     *
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

    /**
     * Returns the room's name.
     *
     * <p>Function getName coded before 260930, commented in full on 260930.
     *
     * @return the room's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the flat layout text, all {@code D:} lines joined with no separator, matching C's
     * {@code text} buffer.
     *
     * <p>Function getMapText coded before 260930, commented in full on 260930.
     *
     * @return every {@code D:} line concatenated with no separator
     */
    public String getMapText() {
        return mapText;
    }

    /**
     * Returns the layout as one string per {@code D:} line, in file order.
     *
     * <p>Function getMap coded before 260930, commented in full on 260930.
     *
     * @return the room layout as one string per {@code D:} line
     */
    public List<String> getMap() {
        return map;
    }

    /**
     * Returns the room flags; empty when the record had no {@code flags:} line.
     *
     * <p>Function getFlags coded before 260930, commented in full on 260930.
     *
     * @return the flags set on this room
     */
    public Flag<RoomFlags> getFlags() {
        return flags;
    }

    /**
     * Returns the room type, C's {@code typ}.
     *
     * <p>Function getType coded before 260930, commented in full on 260930.
     *
     * @return the room's type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the room rating, C's {@code rat} — with the type, what the room builder selects
     * templates by.
     *
     * <p>Function getRating coded before 260930, commented in full on 260930.
     *
     * @return the room's rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Returns the number of rows, C's {@code hgt}.
     *
     * <p>Function getHeight coded before 260930, commented in full on 260930.
     *
     * @return number of rows
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the number of columns, C's {@code wid}.
     *
     * <p>Function getWidth coded before 260930, commented in full on 260930.
     *
     * @return number of columns
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the number of possible door positions, C's {@code dor}.
     *
     * <p>Function getDoors coded before 260930, commented in full on 260930.
     *
     * @return number of possible door positions
     */
    public int getDoors() {
        return doors;
    }

    /**
     * Returns the tval that objects placed at {@code [} squares must have; {@link TValue#TV_NONE}
     * when the template does not constrain it.
     *
     * <p>Function getTval coded before 260930, commented in full on 260930.
     *
     * @return the tval objects placed at {@code [} squares in this room must have
     */
    public TValue getTval() {
        return tval;
    }
}