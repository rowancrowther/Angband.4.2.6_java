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

package uk.co.jackoftrades.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.parser.DungeonProfileReader;
import uk.co.jackoftrades.backend.parser.ParseResult;
import uk.co.jackoftrades.backend.parser.RoomProfileReader;
import uk.co.jackoftrades.backend.parser.VaultReader;
import uk.co.jackoftrades.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftrades.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftrades.middle.cave.profiles.vault.Vault;
import uk.co.jackoftrades.channel.directories.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.registry.DungeonRegistry;

import java.io.IOException;

/**
 * Loads the level-generation data files into their registries at start-up.
 *
 * <p>One of the per-subsystem loaders {@code GameConstants} drives; the port's equivalent of the
 * {@code run_parser} calls in {@code init_arrays} ({@code generate.c:644}).
 *
 * @author Rowan Crowther
 */
public class DungeonLoader {
    private static final Logger logger = LogManager.getLogger(DungeonLoader.class);

    /**
     * Read {@code dungeon_profile.txt} and install the profiles in {@link DungeonRegistry}.
     *
     * <p>Data problems are reported but not fatal — {@code ErrorParsing.reportAndCheck} logs
     * whatever the reader gathered, and the profiles that did assemble are still installed. An
     * unreadable file is logged and swallowed, leaving the registry unset; C by contrast quits the
     * game outright, since {@code run_parse_profile} uses {@code parse_file_quit_not_found}
     * ({@code generate.c:219}).
     *
     * @author Rowan Crowther
     */
    public static void loadDungeonProfiles() {
        DungeonProfileReader parser = new DungeonProfileReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "dungeon_profile.txt";

        try {
            ParseResult<CaveProfile> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            DungeonRegistry.setCaveProfiles(result.items());
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
     * ({@code generate.c}).
     *
     * @author Rowan Crowther
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
     * ({@code [C] src/generate.c:611}).
     *
     * <p>Must run after {@code GameConstants}, because the assembler needs the world's maximum
     * depth: {@code vault.txt} writes {@code max-depth:0} to mean "no maximum", and C rewrites that
     * to {@code z_info->max_depth} while parsing ({@code [C] src/generate.c:561}).
     *
     * @author Rowan Crowther
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
