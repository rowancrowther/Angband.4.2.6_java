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
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.TunnelProfile;

import java.util.List;

/**
 * Turns one profile's {@code tunnel:} line into a {@link TunnelProfile}: the port of
 * {@code generate.c}, function {@code parse_profile_tunnel()}, which reads the five {@code int}
 * fields {@code rnd}, {@code chg}, {@code con}, {@code pen} and {@code jct} into the profile's
 * {@code tun} with {@code parser_getint}.
 *
 * <p>Unlike {@link RoomAssembler} it yields a single value rather than a list, because C embeds
 * {@code struct tunnel_profile} in {@code struct cave_profile} by value ({@code generate.h})
 * rather than pointing at a list of them. It still implements the list-taking {@link Assembler}
 * interface for consistency with the rest of the suite, so {@link DungeonProfileAssembler} wraps
 * the single record before calling and the count is checked here.
 *
 * <p>Only the profiles with a {@code tunnel:} line ever have it read in C: {@code classic},
 * {@code modified}, {@code moria} and {@code lair}, through {@code build_tunnel()} and
 * {@code try_door()} in {@code gen-cave.c}. A profile with no {@code tunnel:} line still has a
 * {@code tun} in C, left at zero by {@code mem_zalloc} in {@code parse_profile_name()}; this
 * class is never called for one, and {@link DungeonProfileAssembler} leaves the tunnel
 * {@code null} instead.
 *
 * <p>Class TunnelAssembler coded before 260915, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
public class TunnelAssembler implements Assembler<DungeonProfileParseRecord.Tunnel, TunnelProfile> {
    /**
     * Receives the fatal message for a record list that is {@code null} or not exactly one long,
     * before the {@link DataFileParseException} is thrown. The number errors are logged by
     * {@link ParseUtils} under its own name. C has no counterpart: it reports through
     * {@code print_error} in {@code datafile.c} and quits.
     *
     * <p>Field logger coded before 260915, commented in full on 261023.
     */
    private static final Logger logger = LogManager.getLogger(TunnelAssembler.class);

    /**
     * Assemble the one tunnel record, as {@code parse_profile_tunnel()} in {@code generate.c}
     * fills {@code c->tun}.
     *
     * <p>Each of the five fields is read by the five-argument
     * {@link ParseUtils#parseIntString(String, String, int, String, String)}, as
     * {@code parser_getint} reads it with {@code strtol} in base 0: {@code 50} is 50, {@code -3}
     * is -3, {@code 010} is 8 and {@code 08} is 0, because C's only check is that some digit was
     * read. The values are stored as given, with no range check, as C stores them; the
     * percentage reading is left to {@code gen-cave.c}, which clamps {@code chg} and {@code rnd}
     * to 0 to 100 only where {@code build_tunnel()} biases its choice of room exit. Empty text
     * gives 0, the
     * {@code mem_zalloc} value; C has no empty {@code int} field and the grammar's
     * {@code INTEGER} token holds at least one digit, so only a caller building a record by hand
     * reaches it.
     *
     * <p>Two failures stop the load. A field with no leading digit throws, as C's
     * {@code PARSE_ERROR_NOT_NUMBER} quits. A record list that is {@code null} or not exactly one
     * long throws too; the nearest C case is a {@code tunnel:} line before any {@code name:},
     * which gives {@code PARSE_ERROR_MISSING_RECORD_HEADER}. A repeated {@code tunnel:} line,
     * where C keeps the last, never gets here, because the grammar has already kept only one.
     *
     * <p>Accepted deviation, from {@link ParseUtils}: a figure too big for an {@code int} is
     * clamped, where C keeps the low 32 bits, so {@code 99999999999} is 2147483647 here and
     * 1215752191 in C. No shipped figure is near the limit.
     *
     * <p>Function assemble coded before 260915, commented in full on 261023.
     *
     * @param records  exactly one parsed tunnel record
     * @param errors   the shared error list; nothing is added to it, because every failure here
     *                 throws
     * @param filename the file being read, for the error messages
     * @return the assembled tunnel profile; never {@code null}
     * @throws DataFileParseException if {@code records} is {@code null} or not exactly one long, or
     *                                a field has no leading digit
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

        int randomChance = ParseUtils.parseIntString("Tunnel Profile", record.randomChance(), line, filename, "random chance");
        int directionChangeChance = ParseUtils.parseIntString("Tunnel Profile", record.directionChangeChance(), line, filename, "direction change chance");
        int concludeChance = ParseUtils.parseIntString("Tunnel Profile", record.concludeChance(), line, filename, "conclude chance");
        int doorAtRoomEntranceChance = ParseUtils.parseIntString("Tunnel Profile", record.doorAtRoomEntranceChance(), line, filename, "door at room entrance chance");
        int junctionChance = ParseUtils.parseIntString("Tunnel Profile", record.junctionChance(), line, filename, "junction chance");

        return new TunnelProfile(randomChance, directionChangeChance,
                concludeChance, doorAtRoomEntranceChance, junctionChance);
    }
}
