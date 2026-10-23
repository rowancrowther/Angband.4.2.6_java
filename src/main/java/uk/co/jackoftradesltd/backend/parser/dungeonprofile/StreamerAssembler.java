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
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.StreamerProfile;

import java.util.List;

/**
 * Turns one profile's {@code streamer:} line into a {@link StreamerProfile}: the port of
 * {@code generate.c}, function {@code parse_profile_streamer()}, which reads the six
 * {@code int} fields {@code den}, {@code rng}, {@code mag}, {@code mc}, {@code qua} and
 * {@code qc} into the profile's {@code str} with {@code parser_getint}.
 *
 * <p>Like {@link TunnelAssembler} it yields a single value rather than a list, because C embeds
 * {@code struct streamer_profile} in {@code struct cave_profile} by value ({@code generate.h}),
 * and it insists on exactly one record for the same reason. A profile with no {@code streamer:}
 * line still has a {@code str} in C, left at zero by {@code mem_zalloc} in
 * {@code parse_profile_name()}; {@link DungeonProfileAssembler} matches that by handing this
 * class a record of six {@code "0"}s, so every {@code CaveProfile} carries a
 * {@code StreamerProfile}.
 *
 * <p>Only the profiles with a {@code streamer:} line ever have it read in C: {@code town},
 * {@code classic}, {@code modified}, {@code moria} and {@code lair}, through
 * {@code build_streamer()} and the level builders in {@code gen-cave.c}.
 *
 * <p>Class StreamerAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class StreamerAssembler implements Assembler<DungeonProfileParseRecord.Streamer, StreamerProfile> {
    /**
     * Receives the fatal message for a record list that is {@code null} or not exactly one long,
     * before the {@link DataFileParseException} is thrown. The number errors are logged by
     * {@link ParseUtils} under its own name. C has no counterpart: it reports through
     * {@code print_error} in {@code datafile.c} and quits.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private final static Logger logger = LogManager.getLogger(StreamerAssembler.class);

    /**
     * Assemble the one streamer record, as {@code parse_profile_streamer()} in
     * {@code generate.c} fills {@code c->str}.
     *
     * <p>Each of the six fields is read by the five-argument
     * {@link ParseUtils#parseIntString(String, String, int, String, String)}, as
     * {@code parser_getint} reads it with {@code strtol} in base 0: {@code 90} is 90, {@code -3}
     * is -3, {@code 010} is 8 and {@code 08} is 0, because C's only check is that some digit was
     * read. The values are stored as given, with no range check, as C stores them. Empty text
     * gives 0, the {@code mem_zalloc} value; C has no empty {@code int} field and the grammar's
     * {@code INTEGER} token holds at least one digit, so only a caller building a record by hand
     * reaches it.
     *
     * <p>Two failures stop the load. A field with no leading digit throws, as C's
     * {@code PARSE_ERROR_NOT_NUMBER} quits. A record list that is {@code null} or not exactly one
     * long throws too; the nearest C case is a {@code streamer:} line before any {@code name:},
     * which gives {@code PARSE_ERROR_MISSING_RECORD_HEADER}. A repeated {@code streamer:} line,
     * where C keeps the last, never gets here, because the grammar has already kept only one.
     *
     * <p>Accepted deviation, from {@link ParseUtils}: a figure too big for an {@code int} is
     * clamped, where C keeps the low 32 bits, so {@code 99999999999} is 2147483647 here and
     * 1215752191 in C. No shipped figure is near the limit.
     *
     * <p>Function assemble coded before 260915, commented in full on 261023.
     *
     * @param records  exactly one parsed streamer record
     * @param errors   the shared error list; nothing is added to it, because every failure here
     *                 throws
     * @param filename the file being read, for the error messages
     * @return the assembled streamer profile; never {@code null}
     * @throws DataFileParseException if {@code records} is {@code null} or not exactly one long, or
     *                                a field has no leading digit
     */
    @Override
    public StreamerProfile assemble(@NotNull List<DungeonProfileParseRecord.Streamer> records, @NotNull List<String> errors,
                                    String filename) {
        if (records == null || records.size() != 1) {
            String msg = "Streamer profile in file: " + filename +
                    " must not be null and must have exactly one record";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        DungeonProfileParseRecord.Streamer streamer = records.get(0);
        int line = streamer.lineNo();
        int density = ParseUtils.parseIntString("Streamer profile", streamer.density(), line, filename, "density");
        int range = ParseUtils.parseIntString("Streamer profile", streamer.range(), line, filename, "range");
        int magmaStreamers = ParseUtils.parseIntString("Streamer profile", streamer.magmaStreamersPerLevel(),  line, filename, "magma streamers per level");
        int magmaTreasure = ParseUtils.parseIntString("Streamer profile", streamer.magmaTreasureChance(), line, filename, "magma treasure chance");
        int quartzStreamers = ParseUtils.parseIntString("Streamer profile", streamer.quartzStreamersPerLevel(),  line, filename, "quartz streamers per level");
        int quartzTreasure = ParseUtils.parseIntString("Streamer profile", streamer.quartzTreasureChance(),  line, filename, "quartz treasure chance");

        return new StreamerProfile(density, range, magmaStreamers, magmaTreasure,
                quartzStreamers, quartzTreasure);
    }
}
