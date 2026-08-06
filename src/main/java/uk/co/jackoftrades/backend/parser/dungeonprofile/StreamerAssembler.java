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
import uk.co.jackoftrades.middle.cave.profilers.StreamerProfile;

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

    /**
     * Assemble the one streamer record, or report why it could not be assembled.
     *
     * <p>Fails closed on any bad field, as the tunnel assembler does: a vein count or treasure
     * chance quietly defaulting to zero would change what the level contains.
     *
     * @param records exactly one parsed streamer record
     * @param errors  the soft-error sink, appended to if the record is unusable
     * @return the assembled streamer profile, or {@code null} if any field would not convert
     * @author Rowan Crowther
     */
    @Override
    public StreamerProfile assemble(@NotNull List<DungeonProfileParseRecord.Streamer> records, @NotNull List<String> errors) {
        if (records == null || records.size() != 1) {
            errors.add("Streamer must not be null and have exactly one Streamer");
            return null;
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
            errors.add("Streamer at line: " + line + " has " +
                    "an invalid density integer value: " + streamer.density());
            return null;
        }
        try {
            range = Integer.parseInt(streamer.range());
        } catch (NumberFormatException e) {
            errors.add("Streamer at line: " + line + " has " +
                    "an invalid range integer value: " + streamer.range());
            return null;
        }
        try {
            magmaStreamers = Integer.parseInt(streamer.magmaStreamersPerLevel());
        } catch (NumberFormatException e) {
            errors.add("Streamer at line: " + line + " has " +
                    "an invalid magma integer value: " + streamer.magmaStreamersPerLevel());
            return null;
        }
        try {
            magmaTreasure = Integer.parseInt(streamer.magmaTreasureChance());
        } catch (NumberFormatException e) {
            errors.add("Streamer at line: " + line + " has " +
                    "an invalid magma treasure integer value: " + streamer.magmaTreasureChance());
            return null;
        }
        try {
            quartzStreamers = Integer.parseInt(streamer.quartzStreamersPerLevel());
        } catch (NumberFormatException e) {
            errors.add("Streamer at line: " + line + " has " +
                    "an invalid quartz integer value: " + streamer.quartzStreamersPerLevel());
            return null;
        }
        try {
            quartzTreasure = Integer.parseInt(streamer.quartzTreasureChance());
        } catch (NumberFormatException e) {
            errors.add("Streamer at line: " + line + " has " +
                    "an invalid quartz treasure integer value: " + streamer.quartzTreasureChance());
            return null;
        }

        return new StreamerProfile(density, range, magmaStreamers, magmaTreasure,
                quartzStreamers, quartzTreasure);
    }
}
