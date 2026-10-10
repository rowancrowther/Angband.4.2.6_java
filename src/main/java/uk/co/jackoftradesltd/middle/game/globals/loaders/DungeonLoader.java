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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.backend.parser.DungeonProfileReader;
import uk.co.jackoftradesltd.backend.parser.RoomProfileReader;
import uk.co.jackoftradesltd.backend.parser.VaultReader;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry;

import java.io.IOException;

/**
 * Loads the level-generation data files into their registries at start-up.
 *
 * <p>Driven by {@code GameEngine.loadGameConstants}, which calls the three loaders in turn after the
 * player is built. They are the port of C's {@code generate_module}, whose init function
 * {@code run_template_parser()} ({@code generate.c}) runs the dungeon-profile, room-template and
 * vault parsers in that order. They are <em>not</em> part of C's {@code init_arrays()}
 * ({@code init.c}), which loads the other data files; the module table runs {@code arrays_module},
 * then {@code player_module}, then {@code generate_module}.
 *
 * <p>Every loader follows the same shape: build the reader, parse the file under
 * {@code lib/gamedata}, report data problems, and install what assembled into
 * {@link DungeonRegistry}. They differ from C in one way: an unreadable file is logged and
 * swallowed here, where C quits the game.
 *
 * <p>Class DungeonLoader coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class DungeonLoader {
    /**
     * Logger for data problems found while parsing the three files. C has no counterpart: it
     * reports through {@code quit()} and the parser's own error text.
     *
     * <p>Field logger coded before 261010, commented in full on 261010.
     */
    private static final Logger logger = LogManager.getLogger(DungeonLoader.class);

    /**
     * Read {@code dungeon_profile.txt} and install the profiles in {@link DungeonRegistry} - the
     * first of the three parsers in C's {@code run_template_parser()} ({@code generate.c}).
     *
     * <p>Data problems are reported but not fatal — {@code ErrorParsing.reportAndCheck} logs
     * whatever the reader gathered, and the profiles that did assemble are still installed. An
     * unreadable file is logged and swallowed, leaving the registry unset; C by contrast quits the
     * game outright, since {@code run_parse_profile} uses {@code parse_file_quit_not_found}
     * ({@code generate.c}).
     *
     * <p>The profile count is recorded in {@code GameConstants} as well: C's
     * {@code finish_parse_profile} ({@code generate.c}) counts the parsed profiles into
     * {@code z_info->profile_max}, and the port sets the same figure from the number of profiles it
     * assembled.
     *
     * <p>Calling it again reloads rather than appends: the registry list and the count are both
     * replaced.
     *
     * <p>Method loadDungeonProfiles coded before 261010, commented in full on 261010.
     */
    public static void loadDungeonProfiles() {
        DungeonProfileReader parser = new DungeonProfileReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "dungeon_profile.txt";

        try {
            ParseResult<CaveProfile> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            DungeonRegistry.setCaveProfiles(result.items());
            GameConstants.setCaveProfileMax(result.items().size());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }

    }

    /**
     * Read {@code room_template.txt} and install the templates in {@link DungeonRegistry}.
     *
     * <p>Data problems are reported but not fatal, following the same pattern as
     * {@link #loadDungeonProfiles()}: {@code ErrorParsing.reportAndCheck} logs whatever the reader
     * gathered, and the templates that did assemble are still installed. An unreadable file is
     * logged and swallowed, leaving the registry unset; C by contrast quits the game outright,
     * since {@code run_parse_room} also uses {@code parse_file_quit_not_found}
     * ({@code generate.c}). The second of the three parsers in {@code run_template_parser()}.
     *
     * <p>Method loadRoomTemplates coded before 261010, commented in full on 261010.
     */
    public static void loadRoomTemplates() {
        RoomProfileReader parser = new RoomProfileReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "room_template.txt";
        
        try {
            ParseResult<RoomTemplate> result = parser.parseWithResults(filename);
            
            ErrorParsing.reportAndCheck(filename, result, logger);
            
            DungeonRegistry.setRoomTemplates(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Read {@code vault.txt} and install the vaults in {@link DungeonRegistry}.
     *
     * <p>Data problems are reported but not fatal, following the same pattern as
     * {@link #loadDungeonProfiles()}: {@code ErrorParsing.reportAndCheck} logs whatever the reader
     * gathered, and the vaults that did assemble are still installed. An unreadable file is logged
     * and swallowed, leaving the registry unset; C by contrast quits the game outright, since
     * {@code run_parse_vault} also uses {@code parse_file_quit_not_found}
     * ({@code [C] src/generate.c}). The last of the three parsers in {@code run_template_parser()}.
     *
     * <p>Must run after {@code GameConstants}, because the assembler needs the world's maximum
     * depth: {@code vault.txt} writes {@code max-depth:0} to mean "no maximum", and C rewrites that
     * to {@code z_info->max_depth} in {@code parse_vault_max_depth()} ({@code [C] src/generate.c}).
     *
     * <p>Method loadVaultTemplates coded before 261010, commented in full on 261010.
     */
    public static void loadVaultTemplates() {
        VaultReader parser = new VaultReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "vault.txt";
        
        try {
            ParseResult<Vault> result = parser.parseWithResults(filename);
            
            ErrorParsing.reportAndCheck(filename, result, logger);
            
            DungeonRegistry.setVaultTemplates(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }
}
