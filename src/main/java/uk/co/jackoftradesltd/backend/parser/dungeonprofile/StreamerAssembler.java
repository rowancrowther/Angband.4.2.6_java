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
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.StreamerProfile;

import java.util.List;

/**
 * Turns one profile's {@code streamer:} line into a {@link StreamerProfile}.
 *
 * <p>The port of {@code parse_profile_streamer} (generate.c:136). Like {@link TunnelAssembler} it
 * yields a single value rather than a list, because C embeds the struct in {@code cave_profile}
 * by value (generate.h:229), and it checks the record count for the same reason.
 *
 * @author Rowan Crowther
 */
public class StreamerAssembler implements Assembler<DungeonProfileParseRecord.Streamer, StreamerProfile> {
    private final static Logger logger = LogManager.getLogger(StreamerAssembler.class);

    /**
     * Assemble the one streamer record, or report why it could not be assembled.
     *
     * <p>Fails closed on any bad field, as the tunnel assembler does: a vein count or treasure
     * chance quietly defaulting to zero would change what the level contains.
     *
     * <p>Function assemble commented in full before 260915, provenance stamp added on 260915.
     *
     * @param records exactly one parsed streamer record
     * @param errors  the soft-error sink, appended to if the record is unusable
     * @return the assembled streamer profile; never {@code null}, because a bad field or a record count other
     * than one throws {@link DataFileParseException} and stops the load, as C quits on the line
     */
    @Override
    public StreamerProfile assemble(@NotNull List<DungeonProfileParseRecord.Streamer> records, @NotNull List<String> errors,
                                    String filename) {
        if (records == null || records.size() != 1) {
            String message = "Streamer must not be null and have exactly one Streamer. Found " +
                    "in file: " + filename;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        DungeonProfileParseRecord.Streamer streamer = records.get(0);
        int line = streamer.lineNo();
        int density = -1;
        int range = -1;
        int magmaStreamers = -1;
        int magmaTreasure = -1;
        int quartzStreamers = -1;
        int quartzTreasure = -1;
        try {
            density = Integer.parseInt(streamer.density());
        } catch (NumberFormatException e) {
            String message = "Streamer at line: " + line + " of file: " 
                    + filename + " has " +
                    "an invalid density integer value: " + streamer.density();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        try {
            range = Integer.parseInt(streamer.range());
        } catch (NumberFormatException e) {
            String message = "Streamer at line: " + line + " of file: "
                    + filename + " has " +
                    "an invalid range integer value: " + streamer.range();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        try {
            magmaStreamers = Integer.parseInt(streamer.magmaStreamersPerLevel());
        } catch (NumberFormatException e) {
            String message = "Streamer at line: " + line + " of file: "
                    + filename + " has " +
                    "an invalid magma integer value: " + streamer.magmaStreamersPerLevel();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        try {
            magmaTreasure = Integer.parseInt(streamer.magmaTreasureChance());
        } catch (NumberFormatException e) {
            String message = "Streamer at line: " + line + " of file: "
                    + filename + " has " +
                    "an invalid magma treasure integer value: " + streamer.magmaTreasureChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        try {
            quartzStreamers = Integer.parseInt(streamer.quartzStreamersPerLevel());
        } catch (NumberFormatException e) {
            String message = "Streamer at line: " + line + " of file: "
                    + filename + " has " +
                    "an invalid quartz integer value: " + streamer.quartzStreamersPerLevel();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        try {
            quartzTreasure = Integer.parseInt(streamer.quartzTreasureChance());
        } catch (NumberFormatException e) {
            String message = "Streamer at line: " + line + " of file: "
                    + filename + " has " +
                    "an invalid quartz treasure integer value: " + streamer.quartzTreasureChance();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        return new StreamerProfile(density, range, magmaStreamers, magmaTreasure,
                quartzStreamers, quartzTreasure);
    }
}
