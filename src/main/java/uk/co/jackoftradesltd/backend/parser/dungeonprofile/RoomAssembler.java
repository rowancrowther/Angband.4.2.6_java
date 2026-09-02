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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.Assembler;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.RoomProfile;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the {@code room:} lines of one profile into {@link RoomProfile}s.
 *
 * <p>The port of the numeric half of {@code parse_profile_room} (generate.c:150). Alongside the
 * conversions it does the one lookup that gives a room line its meaning: the name is resolved to
 * a {@link RoomType}, which is the port's stand-in for C's {@code room_builder} function pointer.
 *
 * <p>Order is preserved, and deliberately so — the generator walks these in order when picking a
 * room by cutoff, so reordering them would change which rooms a level gets.
 *
 * @author Rowan Crowther
 */
public class RoomAssembler implements Assembler<DungeonProfileParseRecord.Room, List<RoomProfile>> {
    /**
     * Assemble one profile's room lines, dropping any that will not convert.
     *
     * <p>{@code pit} is the odd field out: a 0/1 int in the file that C stores as a bool
     * (generate.c:182), so it is compared rather than parsed and any value other than {@code "1"}
     * reads as false.
     *
     * @param records the parsed room lines, in file order
     * @param errors  the soft-error sink, appended to for each unusable line
     * @return the assembled room profiles, in file order, minus any that failed
     */
    @Override
    public List<RoomProfile> assemble(@NotNull List<DungeonProfileParseRecord.Room> records, @NotNull List<String> errors) {
        List<RoomProfile> roomProfiles = new ArrayList<>();

        for (DungeonProfileParseRecord.Room record : records) {
            int line = record.lineNo();
            String name = record.roomName();
            RoomType type = RoomType.getRoomTypeFromName(name);
            int rating = -1;
            try {
                rating = Integer.parseInt(record.rating());
            } catch (NumberFormatException e) {
                errors.add("Room at line: " + line + " has " +
                        "an invalid rating integer: " + record.rating());
                continue;
            }
            int height = -1;
            try {
                height = Integer.parseInt(record.height());
            } catch (NumberFormatException e) {
                errors.add("Room at line: " + line + " has " +
                        "an invalid height integer: " + record.height());
                continue;
            }
            int width = -1;
            try {
                width = Integer.parseInt(record.width());
            } catch (NumberFormatException e) {
                errors.add("Room at line: " + line + " has " +
                        "an invalid width integer: " + record.width());
                continue;
            }
            int level = -1;
            try {
                level = Integer.parseInt(record.level());
            } catch (NumberFormatException e) {
                errors.add("Room at line: " + line + " has " +
                        "an invalid level integer: " + record.level());
                continue;
            }
            boolean pit = record.pit().equals("1");
            int rarity = -1;
            try {
                rarity = Integer.parseInt(record.rarity());
            } catch (NumberFormatException e) {
                errors.add("Room at line: " + line + " has " +
                        "an invalid rarity integer: " + record.rarity());
                continue;
            }
            int cutoff = -1;
            try {
                cutoff = Integer.parseInt(record.cutoff());
            } catch (NumberFormatException e) {
                errors.add("Room at line: " + line + " has " +
                        "an invalid cutoff integer: " + record.cutoff());
                continue;
            }

            roomProfiles.add(new RoomProfile(name, type, rating,
                    height, width, level, pit, rarity, cutoff));
        }

        return roomProfiles;
    }
}
