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
import uk.co.jackoftradesltd.middle.cave.chunkbuilders.BuilderType;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.RoomProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.StreamerProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.TunnelProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns parsed {@code dungeon_profile.txt} records into the {@link CaveProfile}s the level
 * generator selects between.
 *
 * <p>This is where the file stops being text: the profile name is resolved to a
 * {@link BuilderType}, every number is narrowed from {@code String} to {@code int}, and the
 * three sub-records are handed to {@link TunnelAssembler}, {@link StreamerAssembler} and
 * {@link RoomAssembler}. C does the same work inside the seven {@code parse_profile_*} callbacks
 * in {@code generate.c}, registered by {@code init_parse_profile}: {@code parse_profile_name}
 * looks the name up in {@code cave_builders[]}, and the others read each field with
 * {@code parser_getint}, which is {@code strtol} in base 0.
 *
 * <p><strong>Failure stops the load, as in C.</strong> An unknown profile name
 * ({@code PARSE_ERROR_NO_BUILDER_FOUND} in C) or a number with no leading digits
 * ({@code PARSE_ERROR_NOT_NUMBER}) throws a {@link DataFileParseException}, where C's
 * {@code print_error} quits; nothing is skipped and no partial list is returned. The one
 * difference is which message comes first when a profile has more than one fault: C stops at the
 * first bad line in file order, and this stops at the first bad field in the order
 * {@code assemble} checks them (name, {@code params:}, {@code tunnel:}, {@code streamer:},
 * {@code room:}, {@code min-level:}, {@code alloc:}). The load fails either way.
 *
 * <p>{@code finish_parse_profile}'s list-building is here too: C prepends each record and then
 * fills {@code cave_profiles} from the far end, so the array is in file order, which is the order
 * of the returned list.
 *
 * <p>Class DungeonProfileAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class DungeonProfileAssembler implements Assembler<DungeonProfileParseRecord, List<CaveProfile>> {
    /**
     * Receives the fatal message for an unknown profile name before the
     * {@link DataFileParseException} is thrown. The number errors are logged by
     * {@link ParseUtils} under its own name. C has no counterpart: it reports through
     * {@code print_error} in {@code datafile.c} and quits.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(DungeonProfileAssembler.class);

    /**
     * Assemble every parsed profile record in file order, as C's {@code parse_profile_*}
     * callbacks and {@code finish_parse_profile} in {@code generate.c} build
     * {@code cave_profiles}.
     *
     * <p>The name must match a {@link BuilderType} exactly, case and spaces included
     * ({@code "hard centre"}), as C's {@code streq} against {@code cave_builders[]} does. The
     * {@code params:}, {@code min-level:} and {@code alloc:} numbers are read by
     * {@link ParseUtils#parseIntString(String, String, int, String, String, int)} in base 0, as
     * {@code parser_getint} reads them, so {@code 010} is 8 and {@code 08} is 0.
     *
     * <p>A line the file leaves out reads as 0, which is what C's {@code mem_zalloc} in
     * {@code parse_profile_name} leaves in the field: no {@code params:} line gives a block size,
     * room count, unusualness and rarity of 0, and no {@code min-level:} or {@code alloc:} line
     * gives 0 (and with {@code alloc} 0 the profile is never picked). The exception is
     * {@code tunnel:} and {@code streamer:}: C embeds those as structs that stay zeroed, but here
     * they are left {@code null}, as {@link CaveProfile} documents. No shipped profile without
     * those lines reaches a builder that reads them.
     *
     * <p>The two optional sub-records are each wrapped in a one-element list because the shared
     * {@link Assembler} interface takes a list; the sub-assemblers reject anything else. The rooms
     * go to {@link RoomAssembler} as they are, in file order, and a profile with none gets an empty
     * list where C has {@code n_room_profiles} 0 and a {@code NULL} array.
     *
     * <p>Function assemble coded before 260915, commented in full on 261023.
     *
     * @param records  the parsed profile records, in file order
     * @param errors   the error list handed on to the sub-assemblers; this method adds nothing to
     *                 it, because every failure here throws
     * @param filename the file being read, for the error messages
     * @return the assembled profiles, in file order, one for each record
     * @throws DataFileParseException if a profile name matches no builder, or a number, here or in
     *                                a sub-record, has no leading digits
     */
    @Override
    public List<CaveProfile> assemble(@NotNull List<DungeonProfileParseRecord> records, @NotNull List<String> errors,
                                      String filename) {
        List<CaveProfile> caveProfiles = new ArrayList<>();

        for (DungeonProfileParseRecord record : records) {
            int line = record.lineNo();
            BuilderType builderType = BuilderType.fromName(record.profileName());
            if (builderType == null) {
                String msg = "Dungeon profile starting at line: " + line + " of file: " + filename + " has " +
                        "an invalid builder type value: " + record.profileName();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int blockSize;
            int rooms;
            int unusual;
            int rarity;
            if (record.params() == null) {
                blockSize = 0;
                rooms = 0;
                unusual = 0;
                rarity = 0;
            } else {
                blockSize = ParseUtils.parseIntString("Dungeon profile", record.params().blockSize(), line, filename, "block size", 0);
                rooms = ParseUtils.parseIntString("Dungeon profile", record.params().rooms(), line, filename, "rooms", 0);
                unusual = ParseUtils.parseIntString("Dungeon profile", record.params().unusual(), line, filename, "unusual", 0);
                rarity = ParseUtils.parseIntString("Dungeon profile", record.params().rarity(), line, filename, "rarity", 0);
            }
            TunnelProfile tunnelProfile;
            if (record.tunnel() != null) {
                List<DungeonProfileParseRecord.Tunnel> tunnelProfiles = new ArrayList<>();
                tunnelProfiles.add(record.tunnel());
                tunnelProfile = new TunnelAssembler().assemble(tunnelProfiles, errors, filename);
            } else
                tunnelProfile = null;
            StreamerProfile streamerProfile;
            if (record.streamer() != null) {
                List<DungeonProfileParseRecord.Streamer> streamers = new ArrayList<>();
                streamers.add(record.streamer());
                streamerProfile = new StreamerAssembler().assemble(streamers, errors, filename);
            } else {
                DungeonProfileParseRecord.Streamer streamer = new DungeonProfileParseRecord.Streamer("0", "0",
                        "0", "0", "0", "0", 0);
                List<DungeonProfileParseRecord.Streamer> streamers = new ArrayList<>();
                streamers.add(streamer);
                streamerProfile = new StreamerAssembler().assemble(streamers, errors, filename);
            }
            List<RoomProfile> roomProfiles = new RoomAssembler().assemble(record.rooms(), errors, filename);
            int minLevel = record.minLevel() == null ? 0 : ParseUtils.parseIntString("Dungeon profile", record.minLevel(), line, filename, "min level", 0);
            int alloc = record.alloc() == null ? 0 : ParseUtils.parseIntString("Dungeon profile", record.alloc(), line, filename, "allocation", 0);
            
            caveProfiles.add(new CaveProfile(builderType, blockSize, rooms, unusual,
                    rarity, tunnelProfile, streamerProfile, roomProfiles, minLevel,
                    alloc));
        }

        return caveProfiles;
    }
}
