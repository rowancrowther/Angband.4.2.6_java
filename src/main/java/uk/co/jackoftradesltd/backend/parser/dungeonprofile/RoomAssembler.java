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

package uk.co.jackoftradesltd.backend.parser.dungeonprofile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.RoomProfile;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the {@code room:} lines of one profile into {@link RoomProfile}s, the port of the
 * per-field work of {@code parse_profile_room} in {@code generate.c}. {@link DungeonProfileAssembler}
 * calls it once per profile with that profile's room lines.
 *
 * <p>Alongside the number conversions it does the one lookup that gives a room line its meaning:
 * the name is resolved to a {@link RoomType}, the port's stand-in for the {@code room_builder}
 * function pointer C finds by walking {@code room_builders[]} (built from {@code list-rooms.h}).
 * The match is exact and case-sensitive, as C's {@code streq} is, so {@code "interesting room"}
 * does not find {@code "Interesting room"}.
 *
 * <p>Order is preserved, and deliberately so. C appends each room to the tail of the profile's
 * {@code room_profiles} list, so the list runs in file order, and the generator walks it in that
 * order when picking a room by cutoff; reordering would change which rooms a level gets.
 *
 * <p>Not done here: C refuses a {@code room:} line before any {@code name:} with
 * {@code PARSE_ERROR_MISSING_RECORD_HEADER}. In the port the grammar only accepts room lines
 * inside a profile, so that case never reaches this class.
 *
 * <p>Class RoomAssembler coded before 260915, commented in full on 261023, every fault made fatal
 * on 261023.
 *
 * @author Rowan Crowther
 */
public class RoomAssembler implements Assembler<DungeonProfileParseRecord.Room, List<RoomProfile>> {
    /**
     * Logs each fault at {@code FATAL} just before the {@link DataFileParseException} that ends the
     * load, standing in for C's {@code print_error} in {@code datafile.c}.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(RoomAssembler.class);

    /**
     * Assemble one profile's room lines into {@link RoomProfile}s, in file order. The first fault
     * ends the load; nothing is skipped.
     *
     * <p>The load ends on a room name that matches no {@link RoomType}, as C's
     * {@code parse_profile_room} returns {@code PARSE_ERROR_NO_ROOM_FOUND}. The name is checked
     * before any number is read, as in C. The seven numbers are then read in file order,
     * {@code rating}, {@code height}, {@code width}, {@code level}, {@code pit}, {@code rarity},
     * {@code cutoff}, each by {@link ParseUtils#parseIntString(String, String, int, String, String)},
     * which reads base 0 as C's {@code parser_getint} does: {@code 010} is eight. The grammar's
     * {@code INTEGER} token guarantees each field is an optional {@code -} and decimal digits.
     *
     * <p>{@code pit} is the odd field out: an int in the file that C stores as a bool by comparing
     * it with 1 ({@code r->pit = (parser_getint(p, "pit") == 1)}). The comparison is on the parsed
     * value, not the text, so {@code 01} is a pit as well as {@code 1}, while {@code 0}, {@code 2}
     * and {@code -1} are not.
     *
     * <p>Function assemble coded before 260915, commented in full on 261023, every fault made fatal
     * on 261023.
     *
     * @param records  the parsed room lines of one profile, in file order
     * @param errors   unused; every fault now ends the load by throwing, so nothing is appended
     * @param filename the file being read, for the message
     * @return the assembled room profiles, one per record, in file order
     * @throws DataFileParseException if a room name matches no {@link RoomType}, or a number field
     *                                has no leading digits; the message names the file and the
     *                                line the room starts on
     */
    @Override
    public List<RoomProfile> assemble(@NotNull List<DungeonProfileParseRecord.Room> records, @NotNull List<String> errors,
                                      String filename) {
        List<RoomProfile> roomProfiles = new ArrayList<>();

        for (DungeonProfileParseRecord.Room record : records) {
            int line = record.lineNo();
            String name = record.roomName();
            RoomType type = RoomType.getRoomTypeFromName(name);
            if (type == null) {
                String msg = "Room profile starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid room type value: " + record.roomName();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }            
            int rating = ParseUtils.parseIntString("Room profile", record.rating(), line, filename, "rating");
            int height = ParseUtils.parseIntString("Room profile", record.height(), line, filename, "height");
            int width = ParseUtils.parseIntString("Room profile", record.width(), line, filename, "width");
            int level = ParseUtils.parseIntString("Room profile", record.level(), line, filename, "level");
            int pitInt = ParseUtils.parseIntString("Room profile", record.pit(), line, filename, "pit");
            boolean pit = pitInt == 1;
            int rarity = ParseUtils.parseIntString("Room profile", record.rarity(), line, filename, "rarity");
            int cutoff = ParseUtils.parseIntString("Room profile", record.cutoff(), line, filename, "cutoff");

            roomProfiles.add(new RoomProfile(name, type, rating,
                    height, width, level, pit, rarity, cutoff));
        }

        return roomProfiles;
    }
}
