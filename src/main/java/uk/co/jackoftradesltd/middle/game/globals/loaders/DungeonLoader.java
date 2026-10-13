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
 * {@link DungeonRegistry}. A file that cannot be read, or whose parse leaves errors and no
 * records at all, stops start-up with a {@link RuntimeException} naming the file, which is the
 * port of C's {@code quit()} (a missing file in {@code parse_file_quit_not_found}, anything else
 * in {@code run_template_parser()}).
 *
 * <p>They differ from C in two ways:
 * <ul>
 *   <li>Soft errors are not fatal while at least one record assembled. C's parser stops at the
 *   first bad line and quits; here {@code ErrorParsing.reportAndCheck} logs every error, and
 *   the records that survived are installed anyway. This is the same deliberate softening the
 *   other data loaders make.</li>
 *   <li>The file is read from the gamedata directory only. C's {@code parse_file}
 *   ({@code datafile.c}) tries a customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class DungeonLoader coded before 261010, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
public class DungeonLoader {
    /**
     * Logger for the three loaders: the soft errors {@code ErrorParsing.reportAndCheck} finds in
     * each file, and the fatal line written before a load failure is thrown. C has no
     * counterpart: it reports through {@code quit()} and the parser's own error text.
     *
     * <p>Field logger coded before 261010, commented in full on 261013.
     */
    private static final Logger logger = LogManager.getLogger(DungeonLoader.class);

    /**
     * Read {@code dungeon_profile.txt} and install the profiles in {@link DungeonRegistry} - the
     * first of the three parsers in C's {@code run_template_parser()} ({@code generate.c}).
     *
     * <p>Soft data problems are reported but not fatal — {@code ErrorParsing.reportAndCheck} logs
     * whatever the reader gathered, and the profiles that did assemble are still installed. Two
     * cases stop start-up instead, as C's {@code quit()} does: an unreadable file, which C's
     * {@code run_parse_profile} reaches through {@code parse_file_quit_not_found}
     * ({@code generate.c}), and a parse that leaves errors and no profiles. Both are logged at
     * fatal and thrown, before the registry is touched. A file that parses cleanly but is empty
     * installs an empty list, as in C.
     *
     * <p>The profile count is recorded in {@code GameConstants} as well: C's
     * {@code finish_parse_profile} ({@code generate.c}) counts the parsed profiles into
     * {@code z_info->profile_max}, and the port sets the same figure from the number of profiles it
     * assembled.
     *
     * <p>Calling it again reloads rather than appends: the registry list and the count are both
     * replaced. C builds its profile array back to front from a prepended list, so the finished
     * array is in file order, which is the order installed here.
     *
     * <p>Method loadDungeonProfiles coded before 261010, commented in full on 261013.
     *
     * @throws RuntimeException if {@code dungeon_profile.txt} cannot be read, or the parse leaves
     *                          errors and no profiles
     */
    public static void loadDungeonProfiles() {
        DungeonProfileReader parser = new DungeonProfileReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "dungeon_profile.txt";

        try {
            ParseResult<CaveProfile> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error parsing Dungeon profiles file";
                logger.fatal(message);
                throw new RuntimeException(message);
            }

            DungeonRegistry.setCaveProfiles(result.items());
            GameConstants.setCaveProfileMax(result.items().size());
        } catch (IOException e) {
            logger.fatal("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }

    }

    /**
     * Read {@code room_template.txt} and install the templates in {@link DungeonRegistry}.
     *
     * <p>Failure handling follows {@link #loadDungeonProfiles()}: soft errors are logged by
     * {@code ErrorParsing.reportAndCheck} and the templates that did assemble are still installed,
     * while an unreadable file (C's {@code run_parse_room} also uses
     * {@code parse_file_quit_not_found}, {@code generate.c}) or a parse that leaves errors and no
     * templates is logged at fatal and thrown, before the registry is touched. The second of the
     * three parsers in {@code run_template_parser()}.
     *
     * <p>Unlike the profiles, no count is recorded: C's {@code finish_parse_room} just keeps the
     * list as {@code room_templates}, and nothing in {@code z_info} counts it.
     *
     * <p>Method loadRoomTemplates coded before 261010, commented in full on 261013.
     *
     * @throws RuntimeException if {@code room_template.txt} cannot be read, or the parse leaves
     *                          errors and no templates
     */
    public static void loadRoomTemplates() {
        RoomProfileReader parser = new RoomProfileReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "room_template.txt";
        
        try {
            ParseResult<RoomTemplate> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error parsing Room profiles file";
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
            DungeonRegistry.setRoomTemplates(result.items());
        } catch (IOException e) {
            logger.fatal("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Read {@code vault.txt} and install the vaults in {@link DungeonRegistry}.
     *
     * <p>Failure handling follows {@link #loadDungeonProfiles()}: soft errors are logged by
     * {@code ErrorParsing.reportAndCheck} and the vaults that did assemble are still installed,
     * while an unreadable file (C's {@code run_parse_vault} also uses
     * {@code parse_file_quit_not_found}, {@code generate.c}) or a parse that leaves errors and no
     * vaults is logged at fatal and thrown, before the registry is touched. The last of the three
     * parsers in {@code run_template_parser()}.
     *
     * <p>Must run after {@code GameConstants}, because the assembler needs the world's maximum
     * depth: {@code vault.txt} writes {@code max-depth:0} to mean "no maximum", and C rewrites that
     * to {@code z_info->max_depth} in {@code parse_vault_max_depth()} ({@code generate.c}).
     *
     * <p>Method loadVaultTemplates coded before 261010, commented in full on 261013.
     *
     * @throws RuntimeException if {@code vault.txt} cannot be read, or the parse leaves errors and
     *                          no vaults
     */
    public static void loadVaultTemplates() {
        VaultReader parser = new VaultReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "vault.txt";
        
        try {
            ParseResult<Vault> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error parsing Vault profiles file";
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
            DungeonRegistry.setVaultTemplates(result.items());
        } catch (IOException e) {
            logger.fatal("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }
}
