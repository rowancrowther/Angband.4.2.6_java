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
import uk.co.jackoftrades.channel.directories.AngbandDirs;
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
    private static final Logger logger = LogManager.getLogger(PlayerDataLoader.class);

    /**
     * Load the timed-effect definitions from {@code player_timed.txt} into {@link PlayerRegistry}.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the effects that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>, leaving the registry unset for the first effect that fires.
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
