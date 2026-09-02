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
import uk.co.jackoftradesltd.backend.parser.*;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.player.*;

import java.io.IOException;

/**
 * Startup loader for the player slice: parses the player-domain gamedata files
 * ({@code player_property.txt}, {@code shape.txt}, {@code history.txt}, {@code body.txt},
 * {@code p_race.txt}, {@code realm.txt}, {@code class.txt}, {@code player_timed.txt}) and populates
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry} through its setters.
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
    private static final Logger logger = LogManager.getLogger(PlayerDataLoader.class);

    public static void initialiseExpLevel() {
        PlayerRegistry.playerExperience.clear();
        PlayerRegistry.playerExperience.put(0, 10L);
        PlayerRegistry.playerExperience.put(1, 25L);
        PlayerRegistry.playerExperience.put(2, 45L);
        PlayerRegistry.playerExperience.put(3, 70L);
        PlayerRegistry.playerExperience.put(4, 100L);
        PlayerRegistry.playerExperience.put(5, 140L);
        PlayerRegistry.playerExperience.put(6, 200L);
        PlayerRegistry.playerExperience.put(7, 280L);
        PlayerRegistry.playerExperience.put(8, 380L);
        PlayerRegistry.playerExperience.put(9, 500L);
        PlayerRegistry.playerExperience.put(10, 650L);
        PlayerRegistry.playerExperience.put(11, 850L);
        PlayerRegistry.playerExperience.put(12, 1100L);
        PlayerRegistry.playerExperience.put(13, 1400L);
        PlayerRegistry.playerExperience.put(14, 1800L);
        PlayerRegistry.playerExperience.put(15, 2300L);
        PlayerRegistry.playerExperience.put(16, 2900L);
        PlayerRegistry.playerExperience.put(17, 3600L);
        PlayerRegistry.playerExperience.put(18, 4400L);
        PlayerRegistry.playerExperience.put(19, 5400L);
        PlayerRegistry.playerExperience.put(20, 6800L);
        PlayerRegistry.playerExperience.put(21, 8400L);
        PlayerRegistry.playerExperience.put(22, 10200L);
        PlayerRegistry.playerExperience.put(23, 12500L);
        PlayerRegistry.playerExperience.put(24, 17500L);
        PlayerRegistry.playerExperience.put(25, 25000L);
        PlayerRegistry.playerExperience.put(26, 35000L);
        PlayerRegistry.playerExperience.put(27, 50000L);
        PlayerRegistry.playerExperience.put(28, 75000L);
        PlayerRegistry.playerExperience.put(29, 100000L);
        PlayerRegistry.playerExperience.put(30, 150000L);
        PlayerRegistry.playerExperience.put(31, 200000L);
        PlayerRegistry.playerExperience.put(32, 275000L);
        PlayerRegistry.playerExperience.put(33, 350000L);
        PlayerRegistry.playerExperience.put(34, 450000L);
        PlayerRegistry.playerExperience.put(35, 550000L);
        PlayerRegistry.playerExperience.put(36, 700000L);
        PlayerRegistry.playerExperience.put(37, 850000L);
        PlayerRegistry.playerExperience.put(38, 1000000L);
        PlayerRegistry.playerExperience.put(39, 1250000L);
        PlayerRegistry.playerExperience.put(40, 1500000L);
        PlayerRegistry.playerExperience.put(41, 1800000L);
        PlayerRegistry.playerExperience.put(42, 2100000L);
        PlayerRegistry.playerExperience.put(43, 2400000L);
        PlayerRegistry.playerExperience.put(44, 2700000L);
        PlayerRegistry.playerExperience.put(45, 3000000L);
        PlayerRegistry.playerExperience.put(46, 3500000L);
        PlayerRegistry.playerExperience.put(47, 4000000L);
        PlayerRegistry.playerExperience.put(48, 4500000L);
        PlayerRegistry.playerExperience.put(49, 5000000L);
    }
    
    /**
     * Load the timed-effect definitions from {@code player_timed.txt} into {@link PlayerRegistry}.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the effects that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>, leaving the registry unset for the first effect that fires.
     */
    public static void loadPlayerTimedProperties() {
        PlayerTimedReader parser = new PlayerTimedReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "player_timed.txt";

        try {
            ParseResult<PlayerTimedEffect> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            PlayerRegistry.setPlayerTimedEffects(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player classes from {@code class.txt} into {@link PlayerRegistry}. Must run after
     * magic realms, item objects and summons, which the class assembler resolves against.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the classes that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>. Note this loader's dependencies are the deepest in the suite - a class
     * resolves spellbooks through the object kinds - so a failure upstream tends to arrive here as a
     * wall of unresolved references rather than as an IO error.
     */
    public static void loadPlayerClasses() {
        PlayerClassReader parser = new PlayerClassReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "class.txt";

        try {
            ParseResult<PlayerClass> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            PlayerRegistry.setPlayerClasses(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the magic realms from {@code realm.txt} into {@link PlayerRegistry}. Must run before
     * classes, whose spellbooks reference a realm by name.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the realms that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>; the classes that depend on a realm will report it as an unresolved
     * name shortly afterwards.
     */
    public static void loadMagicRealms() {
        RealmReader parser = new RealmReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "realm.txt";

        try {
            ParseResult<MagicRealm> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            PlayerRegistry.setMagicRealm(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player races from {@code p_race.txt} into {@link PlayerRegistry}. Must run after
     * bodies and history, which each race references.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the races that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>, leaving character creation with nothing to offer.
     */
    public static void loadPlayerRaces() {
        PlayerRaceReader parser = new PlayerRaceReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "p_race.txt";

        try {
            ParseResult<PlayerRace> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            PlayerRegistry.setPlayerRaces(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the body layouts from {@code body.txt} into {@link PlayerRegistry}. Must run before
     * races, which reference a body by index.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the bodies that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>. Because races resolve a body by <em>index</em> rather than by name, a
     * partial load here shifts every later index - a case worth treating as structural if it ever
     * bites.
     */
    public static void loadBodies() {
        BodyReader reader = new BodyReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "body.txt";

        try {
            ParseResult<PlayerBody> results = reader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, results, logger);

            PlayerRegistry.setPlayerBodies(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the background-history charts from {@code history.txt} into {@link PlayerRegistry}. Must
     * run before races, each of which starts at a named chart.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the charts that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>; the charts are a linked structure, so a partial load can leave an
     * entry pointing at a successor that never loaded.
     */
    public static void loadPlayerHistories() {
        HistoryReader reader = new HistoryReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "history.txt";

        try {
            ParseResult<PlayerHistoryChart> results = reader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, results, logger);

            PlayerRegistry.setPlayerHistoryCharts(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player shapes from {@code shape.txt} into {@link PlayerRegistry}. Must run before the
     * effects that reference a shape by name (C's {@code EST_SHAPECHANGE}).
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the shapes that did
     * assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} rather than {@code IOException} and <em>rethrows</em>, so any failure here
     * stops the load.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadPlayerShapes() throws IOException {
        ShapeReader parser = new ShapeReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "shape.txt";

        try {
            ParseResult<PlayerShape> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            PlayerRegistry.setPlayerShape(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the player properties (stats, flags and their UI bindings) from
     * {@code player_property.txt} into {@link PlayerRegistry}. Must run after the UI entries its
     * {@code bindui:} lines resolve against.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the properties that
     * did assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} and <em>rethrows</em>: an unresolvable {@code bindui:} target is a data bug
     * worth stopping for rather than a property silently missing from the character sheet.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadPlayerProperties() throws IOException {
        PlayerPropertyReader parser = new PlayerPropertyReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "player_property.txt";

        try {
            ParseResult<PlayerProperty> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            PlayerRegistry.setPlayerProperties(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
