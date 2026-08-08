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
import uk.co.jackoftrades.middle.cave.profiles.dungeon.TunnelProfile;

import java.util.List;

/**
 * Turns one profile's {@code tunnel:} line into a {@link TunnelProfile}.
 *
 * <p>The port of {@code parse_profile_tunnel} (generate.c:123). Unlike the room assembler this
 * one yields a single value rather than a list, because a profile has at most one tunnel line —
 * C embeds the struct in {@code cave_profile} by value (generate.h:228) rather than pointing at a
 * list of them.
 *
 * <p>It still implements the list-taking {@link Assembler} interface for consistency with the
 * rest of the suite, so its caller wraps the single record before calling and the count is
 * checked here.
 *
 * @author Rowan Crowther
 */
public class TunnelAssembler implements Assembler<DungeonProfileParseRecord.Tunnel, TunnelProfile> {
    /**
     * Assemble the one tunnel record, or report why it could not be assembled.
     *
     * <p>Fails closed: any bad field abandons the whole line rather than leaving a half-built
     * profile, since a tunnel chance defaulting silently to zero would change level generation
     * without saying so.
     *
     * @param records exactly one parsed tunnel record
     * @param errors  the soft-error sink, appended to if the record is unusable
     * @return the assembled tunnel profile, or {@code null} if any field would not convert
     * @author Rowan Crowther
     */
    @Override
    public TunnelProfile assemble(@NotNull List<DungeonProfileParseRecord.Tunnel> records, @NotNull List<String> errors) {
        if (records == null || records.size() != 1) {
            errors.add("Expected exactly one tunnel profile");
            return null;
        }

        DungeonProfileParseRecord.Tunnel record = records.get(0);
        int line = record.lineNo();

        int randomChance = -1;
        int directionChangeChance = -1;
        int concludeChance = -1;
        int doorAtRoomEntranceChance = -1;
        int junctionChance = -1;

        try {
            randomChance = Integer.parseInt(record.randomChance());
        } catch (NumberFormatException e) {
            errors.add("Tunnel record for profile starting at line: " + line + " has " +
                    "a malformed randomChance, must be an integer: " + record.randomChance());
            return null;
        }

        try {
            directionChangeChance = Integer.parseInt(record.directionChangeChance());
        } catch (NumberFormatException e) {
            errors.add("Tunnel record for profile starting at line: " + line + " has " +
                    "a malformed direction change, must be an integer: " + record.directionChangeChance());
            return null;
        }

        try {
            concludeChance = Integer.parseInt(record.concludeChance());
        } catch (NumberFormatException e) {
            errors.add("Tunnel record for profile starting at line: " + line + " has " +
                    "a malformed conclude chance, must be an integer: " + record.concludeChance());
            return null;
        }

        try {
            doorAtRoomEntranceChance = Integer.parseInt(record.doorAtRoomEntranceChance());
        } catch (NumberFormatException e) {
            errors.add("Tunnel record for profile starting at line: " + line + " has " +
                    "a malformed door at a room entrance value, must be an integer: " + record.doorAtRoomEntranceChance());
            return null;
        }

        try {
            junctionChance = Integer.parseInt(record.junctionChance());
        } catch (NumberFormatException e) {
            errors.add("Tunnel record for profile starting at line: " + line + " has " +
                    "a malformed junction change, must be an integer: " + record.junctionChance());
            return null;
        }

        return new TunnelProfile(randomChance, directionChangeChance,
                concludeChance, doorAtRoomEntranceChance, junctionChance);
    }
}
