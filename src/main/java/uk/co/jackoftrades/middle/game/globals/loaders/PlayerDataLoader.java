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
import uk.co.jackoftrades.backend.parser.*;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.player.*;

import java.io.IOException;

/**
 * Startup loader for the player slice: parses the player-domain gamedata files
 * ({@code player_property.txt}, {@code shape.txt}, {@code history.txt}, {@code body.txt},
 * {@code p_race.txt}, {@code realm.txt}, {@code class.txt}, {@code player_timed.txt}) and populates
 * {@link uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry} through its setters.
 *
 * <p>This is the write side of the player slice, paired with {@code PlayerRegistry} (the read
 * side). Its loaders are invoked by {@code GameConstants.init()} in dependency order — bodies and
 * history before races; magic realms and (cross-slice) item objects and summons before classes;
 * UI entries before properties. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class PlayerDataLoader {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Load the timed-effect definitions from {@code player_timed.txt} into {@link PlayerRegistry}.
     * A file with soft errors is logged and skipped, leaving the field unpopulated.
     */
    public static void loadPlayerTimedProperties() {
        PlayerTimedReader parser = new PlayerTimedReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "player_timed.txt";

        try {
            ParseResult<PlayerTimedEffect> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setPlayerTimedEffects(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player classes from {@code class.txt} into {@link PlayerRegistry}. Must run after
     * magic realms, item objects and summons, which the class assembler resolves against.
     */
    public static void loadPlayerClasses() {
        PlayerClassReader parser = new PlayerClassReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "class.txt";

        try {
            ParseResult<PlayerClass> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setPlayerClasses(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the magic realms from {@code realm.txt} into {@link PlayerRegistry}. Must run before
     * classes, whose spellbooks reference a realm by name.
     */
    public static void loadMagicRealms() {
        RealmReader parser = new RealmReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "realm.txt";

        try {
            ParseResult<MagicRealm> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setMagicRealm(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player races from {@code p_race.txt} into {@link PlayerRegistry}. Must run after
     * bodies and history, which each race references.
     */
    public static void loadPlayerRaces() {
        PlayerRaceReader parser = new PlayerRaceReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "p_race.txt";

        try {
            ParseResult<PlayerRace> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setPlayerRaces(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the body layouts from {@code body.txt} into {@link PlayerRegistry}. Must run before
     * races, which reference a body by index.
     */
    public static void loadBodies() {
        BodyReader reader = new BodyReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "body.txt";

        try {
            ParseResult<PlayerBody> results = reader.parseWithResults(filename);

            if (results.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setPlayerBodies(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player histories into the relevant List
     */
    public static void loadPlayerHistories() {
        HistoryReader reader = new HistoryReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "history.txt";

        try {
            ParseResult<PlayerHistoryChart> results = reader.parseWithResults(filename);

            if (results.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setPlayerHistoryCharts(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the PlayerShape information and store it in a List
     */
    public static void loadPlayerShapes() throws IOException {
        ShapeReader parser = new ShapeReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "shape.txt";

        try {
            ParseResult<PlayerShape> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            PlayerRegistry.setPlayerShape(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Player Property information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadPlayerProperties() throws IOException {
        PlayerPropertyReader parser = new PlayerPropertyReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "player_property.txt";

        try {
            ParseResult<PlayerProperty> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String message = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(message);
                logger.fatal(message, e);
                return;
            }

            PlayerRegistry.setPlayerProperties(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
