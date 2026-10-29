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

package uk.co.jackoftradesltd.backend.parser.roomprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw, text-typed {@link RoomProfileParseRecord}s from {@code room_template.txt} into
 * typed {@link RoomTemplate}s — the port of the validation C spreads across
 * {@code parse_room_type}/{@code parse_room_rating}/{@code parse_room_height}/
 * {@code parse_room_width}/{@code parse_room_doors}/{@code parse_room_tval}/{@code parse_room_flags}/
 * {@code parse_room_d} in {@code generate.c}.
 *
 * <p>Every fault ends the load. C turns any {@code PARSE_ERROR_*} from this file into a start-up
 * failure ({@code quit("Cannot initialize room templates")}), and this class does the same by
 * logging the problem and throwing {@link DataFileParseException}. No record is ever skipped, and
 * the {@code errors} list is not used.
 *
 * <p>The number fields follow C's types. {@code type:}, {@code rows:}, {@code columns:} and
 * {@code doors:} are read as unsigned ({@code parser_getuint}), so a leading minus sign, including
 * {@code -0}, is refused. {@code rating:} is read as signed ({@code parser_getint}). All five are
 * then stored in a {@code uint8_t} in {@code struct room_template}, so each is reduced modulo 256:
 * {@code type:257} is stored as 1 and {@code rating:-1} as 255. For {@code rows:} and
 * {@code columns:} the wrap comes first and the size cap is tested on the wrapped value, as in
 * C, so {@code columns:289} is stored as 33 and passes.
 *
 * <p>The layout is not checked. C's {@code parse_room_d} appends every {@code D:} line to one
 * buffer with no test against {@code rows:} or {@code columns:}, so a ragged layout, or a number of
 * lines that differs from {@code rows:}, loads here too.
 *
 * <p>Class RoomProfileAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class RoomProfileAssembler implements Assembler<RoomProfileParseRecord, List<RoomTemplate>> {
    /**
     * Logs each fault at {@code fatal} level just before the {@link DataFileParseException} that
     * ends the load is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(RoomProfileAssembler.class);

    /**
     * Validates and converts every record, in the order C's handlers run on one record: {@code type:},
     * {@code rating:}, {@code rows:}, {@code columns:}, {@code doors:}, {@code tval:}, {@code flags:}
     * and the {@code D:} lines. The first fault throws {@link DataFileParseException}, naming the
     * record's {@code name:} line and the file.
     *
     * <p>The faults are:
     * <ul>
     *     <li>a leading minus sign on {@code type:}, {@code rows:}, {@code columns:} or
     *     {@code doors:};</li>
     *     <li>a wrapped {@code rows:} above 11 or a wrapped {@code columns:} above 33, the caps of
     *     the {@code "room template"} builder ({@link RoomType#TEMPLATE}); zero is allowed;</li>
     *     <li>a {@code tval:} that resolves to no tval. The numbers 0 to 35 and the name
     *     {@code none} are all valid, and 0 and {@code none} give {@link TValue#TV_NONE};</li>
     *     <li>a {@code flags:} name that is not {@code NONE}, {@code FEW_ENTRANCES} or {@code MAX}.
     *     The match is case-sensitive, and the first bad name is the fault, as C stops at the
     *     first name {@code grab_flag} rejects.</li>
     * </ul>
     *
     * <p>The result is in reverse file order. C's {@code parse_room_name} points each new template
     * at the previous head, so {@code room_templates} runs from the last template in the file to
     * the first, and {@code addFirst} gives the same order here.
     *
     * <p>Function assemble coded before 260915, updated on 261029 for the fatal contract and the
     * {@code uint8_t} wrap, commented in full on 261029.
     *
     * @param records  the raw parse records to assemble, in file order
     * @param errors   not used: every fault is thrown rather than collected
     * @param filename the file the records came from, named in each failure message
     * @return the templates, in reverse file order
     * @throws DataFileParseException if any record has a fault listed above, or a number that
     *                                {@link ParseUtils#parseIntString} cannot read
     */
    @Override
    public List<RoomTemplate> assemble(@NotNull List<RoomProfileParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<RoomTemplate> profiles = new ArrayList<>();

        for (RoomProfileParseRecord record : records) {
            int line = record.profileLineNo();
            String name = record.profileName();
            // type/rating/rows/columns/doors are mandatory in the grammar, so the isEmpty()
            // guards below can't actually be hit by any record that reaches this loop; they're
            // defensive rather than reachable, and the defaults they leave behind are never seen.
            int type = 0;
            if (!record.type().isEmpty()) {
                if (ParseUtils.minusFound(record.type())) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative type value: " + record.type();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                type = ParseUtils.parseIntString("Room profile", record.type(), line, filename, "type");
            }
            type = Math.floorMod(type, 256);
            int rating = -1;
            if (!record.rating().isEmpty()) {
                rating = ParseUtils.parseIntString("Room profile", record.rating(), line, filename, "rating");
            }
            rating = Math.floorMod(rating, 256);
            int rows = -1;
            if (!record.rows().isEmpty()) {
                if (ParseUtils.minusFound(record.rows())) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative row value: " + record.rows();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                rows = ParseUtils.parseIntString("Room profile", record.rows(), line, filename, "rows");
                // C stores the value in a uint8_t before it tests it, so the wrap comes first.
                rows = Math.floorMod(rows, 256);
                // Mirrors C's parse_room_height, which rejects a template taller than
                // room_builders["room template"].max_height (PARSE_ERROR_VAULT_TOO_BIG).
                // RoomType.TEMPLATE carries that same bound (11), read from list-rooms.h.
                if (rows < 0 || rows > RoomType.TEMPLATE.getMaxHeight()) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal row count value (outside room template size) " + record.rows();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            int columns = -1;
            if (!record.columns().isEmpty()) {
                columns = ParseUtils.parseIntString("Room profile", record.columns(), line, filename, "columns");
                if (ParseUtils.minusFound(record.columns())) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative column value: " + record.columns();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                columns = Math.floorMod(columns, 256);
                // Mirrors C's parse_room_width against the same room_builders entry (max_width 33).
                if (columns < 0 || columns > RoomType.TEMPLATE.getMaxWidth()) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal column count value (outside room template size) " + record.columns();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            int doors = 0;
            if (!record.doors().isEmpty()) {
                if (ParseUtils.minusFound(record.doors())) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative doors value: " + record.doors();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                doors = ParseUtils.parseIntString("Room profile", record.doors(), line, filename, "doors");
            }
            if (doors > 0) doors = doors % 256;
            // tval: is text in the data — numeric ("0") or a name ("rod", "wand", ...). Try the
            // numeric fast path first, then fall back to name resolution, matching the order
            // C's tval_find_idx checks them in.
            TValue tValue = TValue.TV_NONE;
            String tValStr = record.tval();
            if (!tValStr.isEmpty()) {
                if (ParseUtils.onlyDigits(tValStr)) {
                    int tValInt = ParseUtils.parseIntString("Room profile", record.tval(), line, filename, "tValue");
                    if (tValInt < 0) {
                        String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal negative tValue value: " + record.tval();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                    tValue = TValue.fromName(tValStr);
                } else {
                    tValue = TValue.fromName(tValStr);
                }
                if (tValue == null) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal tValue value: " + record.tval();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            // flags: is optional in the data (many records have none), so record.flags() is
            // simply empty rather than absent when there's nothing to set. The first bad flag
            // name ends the load, as in C, where parse_room_flags stops at the first name
            // grab_flag rejects. The match is case-sensitive, as C's streq is.
            Flag<RoomFlags> roomFlags = new Flag<>(RoomFlags.class);
            for (String flag : record.flags()) {
                try {
                    RoomFlags rFlag = RoomFlags.valueOf(flag);
                    roomFlags.on(rFlag);
                } catch (IllegalArgumentException e) {
                    String message = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal room flag value: " + flag;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            // Straight concatenation with no separator, matching C's parse_room_d, which
            // string_appends each D: line onto the same buffer with nothing between them; row
            // boundaries are re-derived from rows/columns rather than stored in the text itself.
            StringBuilder sb = new StringBuilder();
            for (String mapLine : record.roomMap()) {
                sb.append(mapLine);
            }
            List<String> map = new ArrayList<>(record.roomMap());
            String mapText = sb.toString();

            profiles.addFirst(new RoomTemplate(name, mapText, map, roomFlags, type, rating, rows,
                    columns, doors, tValue));
        }

        return profiles;
    }
}
