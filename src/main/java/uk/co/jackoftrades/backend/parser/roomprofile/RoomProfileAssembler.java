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

package uk.co.jackoftrades.backend.parser.roomprofile;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.RoomFlags;
import uk.co.jackoftrades.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftrades.middle.cave.roombuilders.RoomType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw, text-typed {@link RoomProfileParseRecord}s from {@code room_template.txt} into
 * typed {@link RoomTemplate}s — the port of the validation C spreads across
 * {@code parse_room_type}/{@code parse_room_rating}/{@code parse_room_height}/
 * {@code parse_room_width}/{@code parse_room_doors}/{@code parse_room_tval}/{@code parse_room_flags}/
 * {@code parse_room_d} in {@code generate.c}.
 *
 * <p>Follows the partial-results contract every {@link Assembler} does: a record with a problem is
 * skipped (via {@code continue}) rather than aborting the whole file, and the problem is appended to
 * {@code errors} so the caller can report it. Every other record still assembles.
 *
 * @author Rowan Crowther
 */
public class RoomProfileAssembler implements Assembler<RoomProfileParseRecord, List<RoomTemplate>> {
    /**
     * Validate and convert every record, skipping (and reporting) any that don't resolve cleanly.
     *
     * @param records the raw parse records to assemble
     * @param errors  soft-error sink; one message is appended per record that gets skipped
     * @return the successfully assembled templates, in file order
     */
    @Override
    public List<RoomTemplate> assemble(@NotNull List<RoomProfileParseRecord> records, @NotNull List<String> errors) {
        List<RoomTemplate> profiles = new ArrayList<>();

        for (RoomProfileParseRecord record : records) {
            int line = record.profileLineNo();
            String name = record.profileName();
            // type/rating/rows/columns/doors are mandatory in the grammar, so the isEmpty()
            // guards below can't actually be hit by any record that reaches this loop; they're
            // defensive rather than reachable, and the -1 they leave behind is never seen.
            int type = -1;
            if (!record.type().isEmpty()) {
                try {
                    type = Integer.parseInt(record.type());
                } catch (NumberFormatException e) {
                    errors.add("Room at line: " + line + " has " +
                            "an invalid type integer: " + record.type());
                    continue;
                }
            }
            int rating = -1;
            if (!record.rating().isEmpty()) {
                try {
                    rating = Integer.parseInt(record.rating());
                } catch (NumberFormatException e) {
                    errors.add("Room at line: " + line + " has " +
                            "an invalid rating integer: " + record.rating());
                    continue;
                }
            }
            int rows = -1;
            if (!record.rows().isEmpty()) {
                try {
                    rows = Integer.parseInt(record.rows());
                } catch (NumberFormatException e) {
                    errors.add("Room at line: " + line + " has " +
                            "an invalid rows integer: " + record.rows());
                    continue;
                }
                // Mirrors C's parse_room_height, which rejects a template taller than
                // room_builders["room template"].max_height (PARSE_ERROR_VAULT_TOO_BIG).
                // RoomType.TEMPLATE carries that same bound (11), read from list-rooms.h.
                if (rows < 1 || rows > RoomType.TEMPLATE.getMaxHeight()) {
                    errors.add("Room at line: " + line + " has " +
                            "a row count outside room template size");
                    continue;
                }
            }
            int columns = -1;
            if (!record.columns().isEmpty()) {
                try {
                    columns = Integer.parseInt(record.columns());
                } catch (NumberFormatException e) {
                    errors.add("Room at line: " + line + " has " +
                            "an invalid columns integer: " + record.columns());
                    continue;
                }
                // Mirrors C's parse_room_width against the same room_builders entry (max_width 33).
                if (columns < 1 || columns > RoomType.TEMPLATE.getMaxWidth()) {
                    errors.add("Room at line: " + line + " has " +
                            "a column count outside room template size");
                    continue;
                }
            }
            int doors = -1;
            if (!record.doors().isEmpty()) {
                try {
                    doors = Integer.parseInt(record.doors());
                } catch (NumberFormatException e) {
                    errors.add("Room at line: " + line + " has " +
                            "an invalid doors integer: " + record.doors());
                    continue;
                }
            }
            // tval: is text in the data — numeric ("0") or a name ("rod", "wand", ...). Try the
            // numeric fast path first, then fall back to name resolution, matching the order
            // C's tval_find_idx checks them in.
            TValue tValue = TValue.TV_NONE;
            String tValStr = record.tval();
            if (!tValStr.isEmpty()) {
                try {
                    int tValInt = Integer.parseInt(tValStr);
                    tValue = TValue.fromName(tValInt);
                } catch (NumberFormatException e) {
                    tValue = TValue.fromName(tValStr);
                }
                if (tValue == null) {
                    errors.add("Room at line: " + line + " has " +
                            "an invalid tValue: " + tValStr);
                    continue;
                }
            }
            // flags: is optional in the data (many records have none), so record.flags() is
            // simply empty rather than absent when there's nothing to set. Every bad flag name
            // in a record is collected before the record is skipped, rather than bailing on the
            // first one, so a single error report covers all of a record's problems at once.
            Flag<RoomFlags> roomFlags = new Flag<>(RoomFlags.class);
            boolean badFlag = false;
            for (String flag : record.flags()) {
                try {
                    RoomFlags rFlag = RoomFlags.valueOf(flag.toUpperCase());
                    roomFlags.on(rFlag);
                } catch (IllegalArgumentException e) {
                    errors.add("Room at line: " + line + " has " +
                            "an unknown room flag: " + flag);
                    badFlag = true;
                }
            }
            if (badFlag) continue;
            // Straight concatenation with no separator, matching C's parse_room_d, which
            // string_appends each D: line onto the same buffer with nothing between them; row
            // boundaries are re-derived from rows/columns rather than stored in the text itself.
            StringBuilder sb = new StringBuilder();
            boolean badMap = false;
            for (String mapLine : record.roomMap()) {
                if (mapLine.length() != columns) {
                    errors.add("Room at line: " + line + " has " +
                            "a map line of the wrong length: " + mapLine);
                    badMap = true;
                }
                sb.append(mapLine);
            }
            if (badMap) continue;
            List<String> map = new ArrayList<>(record.roomMap());
            if (map.size() != rows) {
                errors.add("Room at line: " + line + " has " +
                        "the wrong number of rows in the map.");
                continue;
            }
            String mapText = sb.toString();

            profiles.add(new RoomTemplate(name, mapText, map, roomFlags, type, rating, rows,
                    columns, doors, tValue));
        }

        return profiles;
    }
}
