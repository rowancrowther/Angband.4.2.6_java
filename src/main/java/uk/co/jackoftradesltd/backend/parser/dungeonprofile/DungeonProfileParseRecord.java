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

import java.util.List;

/**
 * One profile record as it comes off the parser, before any of it means anything.
 *
 * <p>This is the DTO stage of the {@code dungeon_profile.txt} pipeline: the grammar fills it,
 * {@link DungeonProfileAssembler} drains it. Every field is a {@code String} because the grammar
 * deliberately does no conversion — a number that will not parse, or a room name no builder
 * answers to, should be reported as a data error naming its line, not thrown as a parse error that
 * abandons the file. Whether an optional directive was present at all is carried as {@code null}.
 *
 * <p>It corresponds to C's {@code struct cave_profile} (generate.h:218) at its half-built stage,
 * mid-parse, before {@code finish_parse_profile} (generate.c:223) flattens the linked lists. Two
 * of C's fields have no counterpart here: {@code next}, which the {@link List} replaces, and
 * {@code builder}, the function pointer C resolves during the parse and the assembler resolves
 * afterwards.
 *
 * @param profileName the profile's name, which must match a level builder
 * @param params      the layout parameters, or {@code null} if the file gave none
 * @param tunnel      the corridor chances, or {@code null} if the file gave none
 * @param streamer    the mineral-vein settings, or {@code null} if the file gave none
 * @param alloc       the selection weight, or {@code null} if the file gave none
 * @param minLevel    the shallowest usable depth, or {@code null} if the file gave none
 * @param rooms       the rooms this profile may contain, in file order; never {@code null},
 *                    but empty for the profiles that build no rooms
 * @param lineNo      the source line the record starts on, for error messages
 * @author Rowan Crowther
 */
public record DungeonProfileParseRecord(String profileName,
                                        Params params,
                                        Tunnel tunnel,
                                        Streamer streamer,
                                        String alloc,
                                        String minLevel,
                                        List<Room> rooms,
                                        int lineNo) {

    /**
     * The {@code params:} line — how the level is laid out.
     *
     * @param blockSize the edge of the square block rooms are allocated in, which sets how densely
     *                  rooms can pack and how many will fit
     * @param rooms     how many rooms to aim for
     * @param unusual   how strongly high-rarity rooms are penalised; higher makes them rarer
     * @param rarity    the highest room rarity this profile allows
     * @author Rowan Crowther
     */
    public record Params(String blockSize,
                         String rooms,
                         String unusual,
                         String rarity) {

        /**
         * Rebuild the source line this record came from.
         *
         * <p>Overridden away from the default record {@code toString} so an assembler error can
         * quote the offending line back in the form the reader will find it in the file, rather
         * than in component-name form.
         *
         * @return the record as it appears in {@code dungeon_profile.txt}
         */
        @Override
        public @NotNull String toString() {
            return "params:" + blockSize + ":" + rooms + ":" + unusual + ":" + rarity;
        }
    }

    ;

    /**
     * The {@code tunnel:} line — five percentage chances steering how corridors are dug.
     *
     * @param randomChance             chance of digging in a random direction instead of towards
     *                                 the target
     * @param directionChangeChance    chance of changing direction at a tunnel grid
     * @param concludeChance           chance of simply ending the tunnel
     * @param doorAtRoomEntranceChance chance of a door where the tunnel pierces a room
     * @param junctionChance           chance of a door at a tunnel junction
     * @param lineNo                   the source line, for error messages
     * @author Rowan Crowther
     */
    public record Tunnel(String randomChance,
                         String directionChangeChance,
                         String concludeChance,
                         String doorAtRoomEntranceChance,
                         String junctionChance,
                         int lineNo) {
    }

    ;

    /**
     * The {@code streamer:} line — the magma and quartz veins running through the rock.
     *
     * <p>A streamer is drawn as a random walk that stops at the level edge; the first two fields
     * describe how wide a band of rock that walk turns to vein.
     *
     * @param density                 how many grids near each walk step become vein
     * @param range                   how far from the walk those grids may lie
     * @param magmaStreamersPerLevel  how many magma streamers the level gets
     * @param magmaTreasureChance     reciprocal chance of treasure in magma: a grid holds treasure
     *                                with probability 1/this, so a larger number means rarer
     * @param quartzStreamersPerLevel how many quartz streamers the level gets
     * @param quartzTreasureChance    reciprocal chance of treasure in quartz, as above
     * @param lineNo                  the source line, for error messages
     * @author Rowan Crowther
     */
    public record Streamer(String density,
                           String range,
                           String magmaStreamersPerLevel,
                           String magmaTreasureChance,
                           String quartzStreamersPerLevel,
                           String quartzTreasureChance,
                           int lineNo) {
    }

    ;

    /**
     * One {@code room:} line — a room the enclosing profile may contain.
     *
     * <p>Order matters: the rooms are held in file order because the cutoff scan walks them in
     * that order, and a room whose cutoff is smaller than its predecessor's is reached only when
     * the earlier room fails to place.
     *
     * @param roomName the room's name, which must match a room builder
     * @param rating   selects between variants; used only by template rooms
     * @param height   the rows to reserve for the room
     * @param width    the columns to reserve for the room
     * @param level    the shallowest depth this room may appear at
     * @param pit      {@code "1"} if the room is a pit or nest, {@code "0"} otherwise — an int in
     *                 the file, a bool in C, narrowed by the assembler
     * @param rarity   how unusual the room is, normally 0, 1 or 2
     * @param cutoff   the room is eligible if a 0-99 roll comes in under this
     * @param lineNo   the source line, for error messages
     * @author Rowan Crowther
     */
    public record Room(String roomName,
                       String rating,
                       String height,
                       String width,
                       String level,
                       String pit,
                       String rarity,
                       String cutoff, int lineNo) {
    }

    ;
}
