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
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.TunnelProfile;

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
    private static final Logger logger = LogManager.getLogger(TunnelAssembler.class);
    
    /**
     * Assemble the one tunnel record, or report why it could not be assembled.
     *
     * <p>Fails closed: any bad field abandons the whole line rather than leaving a half-built
     * profile, since a tunnel chance defaulting silently to zero would change level generation
     * without saying so.
     *
     * <p>Function assemble commented in full before 260915, provenance stamp added on 260915.
     *
     * @param records exactly one parsed tunnel record
     * @param errors  the soft-error sink, appended to if the record is unusable
     * @return the assembled tunnel profile; never {@code null}, because a bad field or a record count other
     * than one throws {@link DataFileParseException} and stops the load, as C quits on the line
     */
    @Override
    public TunnelProfile assemble(@NotNull List<DungeonProfileParseRecord.Tunnel> records, @NotNull List<String> errors,
                                    String filename) {
        if (records == null || records.size() != 1) {
            String message = "Expected exactly one tunnel profile in file: " + filename;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        DungeonProfileParseRecord.Tunnel record = records.getFirst();
        int line = record.lineNo();

        int randomChance;
        int directionChangeChance;
        int concludeChance;
        int doorAtRoomEntranceChance;
        int junctionChance;

        try {
            randomChance = Integer.parseInt(record.randomChance());
        } catch (NumberFormatException e) {
            String message = "Tunnel record for profile starting at line: " + line +
                    " of file: " + filename + " has " +
                    "a malformed randomChance, must be an integer: " + record.randomChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        try {
            directionChangeChance = Integer.parseInt(record.directionChangeChance());
        } catch (NumberFormatException e) {
            String message = "Tunnel record for profile starting at line: " + line +
                    " of file: " + filename + " has " +
                    "a malformed direction change, must be an integer: " + record.directionChangeChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        try {
            concludeChance = Integer.parseInt(record.concludeChance());
        } catch (NumberFormatException e) {
            String message = "Tunnel record for profile starting at line: " + line +
                    " of file: " + filename + " has " +
                    "a malformed conclude chance, must be an integer: " + record.concludeChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        try {
            doorAtRoomEntranceChance = Integer.parseInt(record.doorAtRoomEntranceChance());
        } catch (NumberFormatException e) {
            String message = "Tunnel record for profile starting at line: " + line +
                    " of file: " + filename + " has " +
                    "a malformed door at a room entrance value, must be an integer: " + record.doorAtRoomEntranceChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        try {
            junctionChance = Integer.parseInt(record.junctionChance());
        } catch (NumberFormatException e) {
            String message = "Tunnel record for profile starting at line: " + line +
                    " of file: " + filename + " has " +
                    "a malformed junction change, must be an integer: " + record.junctionChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        return new TunnelProfile(randomChance, directionChangeChance,
                concludeChance, doorAtRoomEntranceChance, junctionChance);
    }
}
