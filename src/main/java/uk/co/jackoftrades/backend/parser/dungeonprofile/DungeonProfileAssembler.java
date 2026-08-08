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

package uk.co.jackoftrades.backend.parser.dungeonprofile;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftrades.middle.cave.profiles.dungeon.RoomProfile;
import uk.co.jackoftrades.middle.cave.profiles.dungeon.StreamerProfile;
import uk.co.jackoftrades.middle.cave.profiles.dungeon.TunnelProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns parsed {@code dungeon_profile.txt} records into the {@link CaveProfile}s the level
 * generator selects between.
 *
 * <p>This is where the file stops being text: every field is narrowed from {@code String} to
 * {@code int}, and the three sub-records are handed to {@link TunnelAssembler},
 * {@link StreamerAssembler} and {@link RoomAssembler}. C does the same conversions inside its
 * parse callbacks (generate.c:111-204), where {@code parser_getint} makes a malformed number a
 * fatal parse error; here it is a recoverable one, so a bad line costs its own profile and no
 * more.
 *
 * <p><strong>Partial results:</strong> a profile that fails conversion is skipped with a message
 * appended to {@code errors}, and the rest of the file still loads. That is the port's contract,
 * not C's — C quits the game on the first bad line.
 *
 * @author Rowan Crowther
 */
public class DungeonProfileAssembler implements Assembler<DungeonProfileParseRecord, List<CaveProfile>> {
    /**
     * Assemble every parsed profile record, dropping any that will not convert.
     *
     * <p>The three optional sub-records are each wrapped in a one-element list because the shared
     * {@link Assembler} interface takes a list; the sub-assemblers reject anything else. Absent
     * ones stay {@code null} on the result, matching the file having no such line.
     *
     * @param records the parsed profile records, in file order
     * @param errors  the soft-error sink, appended to for each unusable line
     * @return the assembled profiles, in file order, minus any that failed
     * @author Rowan Crowther
     */
    @Override
    public List<CaveProfile> assemble(@NotNull List<DungeonProfileParseRecord> records, @NotNull List<String> errors) {
        List<CaveProfile> caveProfiles = new ArrayList<>();

        for (DungeonProfileParseRecord record : records) {
            int line = record.lineNo();
            String profileName = record.profileName();
            int blockSize = -1;
            int rooms = -1;
            int unusual = -1;
            int rarity = -1;
            if (record.params() != null) {
                DungeonProfileParseRecord.Params params = record.params();
                try {
                    blockSize = Integer.parseInt(params.blockSize());
                    rooms = Integer.parseInt(params.rooms());
                    unusual = Integer.parseInt(params.unusual());
                    rarity = Integer.parseInt(params.rarity());
                } catch (NumberFormatException e) {
                    errors.add("Cave profile at line: " + line + " has " +
                            "an invalid param line. " + params.toString());
                    continue;
                }
            }
            TunnelProfile tunnelProfile;
            if (record.tunnel() != null) {
                List<DungeonProfileParseRecord.Tunnel> tunnelProfiles = new ArrayList<>();
                tunnelProfiles.add(record.tunnel());
                tunnelProfile = new TunnelAssembler().assemble(tunnelProfiles, errors);
            } else
                tunnelProfile = null;
            StreamerProfile streamerProfile;
            if (record.streamer() != null) {
                List<DungeonProfileParseRecord.Streamer> streamers = new ArrayList<>();
                streamers.add(record.streamer());
                streamerProfile = new StreamerAssembler().assemble(streamers, errors);
            } else
                streamerProfile = null;
            List<RoomProfile> roomProfiles = new ArrayList<>();
            roomProfiles = new RoomAssembler().assemble(record.rooms(), errors);
            int minLevel;
            if (record.minLevel() != null) {
                try {
                    minLevel = Integer.parseInt(record.minLevel());
                } catch (NumberFormatException e) {
                    errors.add("Cave profile at line: " + line + " has " +
                            "an invalid min level line: " + record.minLevel());
                    continue;
                }
            } else minLevel = 0;
            int alloc;
            if (record.alloc() != null) {
                try {
                    alloc = Integer.parseInt(record.alloc());
                } catch (NumberFormatException e) {
                    errors.add("Cave profile at line: " + line + " has " +
                            "an invalid alloc line: " + record.alloc());
                    continue;
                }
            } else alloc = -1;

            caveProfiles.add(new CaveProfile(profileName, blockSize, rooms, unusual,
                    rarity, tunnelProfile, streamerProfile, roomProfiles, minLevel,
                    alloc));
        }

        return caveProfiles;
    }
}
