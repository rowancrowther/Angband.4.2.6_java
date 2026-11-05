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
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
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
 * <p>In C each of these files has a parser in {@code init.c} ({@code player_property_parser},
 * {@code shape_parser}, {@code history_parser}, {@code body_parser}, {@code p_race_parser},
 * {@code realm_parser}, {@code class_parser}, and {@code player_timed_parser} in
 * {@code player-timed.c}), all run in turn by {@code init_arrays()} and all fatal on failure: a file
 * that cannot be opened ends the program through {@code parse_file_quit_not_found} in
 * {@code datafile.c}, and any parser error ends it through {@code init_arrays()}. The eight loaders
 * here keep that: every error stops the load. There are three ways a load fails, and only the first
 * is handled here:
 * <ul>
 *   <li>an {@link IOException} from the reader is logged at {@code ERROR} and rethrown wrapped in a
 *       {@link RuntimeException};</li>
 *   <li>a grammar or lexer error is thrown by the reader as a {@code ParseCancellationException}
 *       ({@code ParseErrors.throwIfAny});</li>
 *   <li>a record the assembler refuses is thrown as a {@code DataFileParseException}.</li>
 * </ul>
 * The last two are unchecked and pass through every loader unlogged and unwrapped;
 * {@code GameConstants.init()} catches all three and turns them into its own
 * {@code RuntimeException} naming the file. Nothing is registered from a file that fails, so the
 * registry never holds a partial list. There is no check on the number of records: a file the
 * grammar accepts with no records registers an empty list.
 *
 * <p>The class also holds {@link #initializeExpLevel()}, which is not a parser but the port of C's
 * compile-time {@code player_exp[]} table.
 *
 * <p>Class PlayerDataLoader coded before 261011, commented in full on 261011, failure paragraph
 * rewritten on 261104 for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class PlayerDataLoader {
    /**
     * Logger for this class - receives the name of the file, at {@code ERROR}, when an IO failure
     * stops a load. Grammar and assembler errors are not logged here (see the class description).
     *
     * <p>Field logger coded before 261011, commented in full on 261011, updated on 261104 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger(PlayerDataLoader.class);

    /**
     * Populate {@link PlayerRegistry#playerExperience} with the fifty-entry experience-to-level
     * table, then push a copy of it as a primitive array to the character-sheet UI cache. The
     * port of C's {@code player_exp[PY_MAX_LEVEL]} constant array ({@code player.c}); this method
     * is the point at which the port loads the values C declares statically at compile time.
     *
     * <p>The table is rebuilt from scratch on every call: the map is cleared and refilled before
     * being copied into the local {@code long[]}, which is then handed to
     * {@link PlayerEventStatusUpdate#updatePlayerCharSheetExpToLevel} to keep the cached
     * character-sheet view in step.
     *
     * <p>Keys run 0 to 49 with level 1 at key 0, so C's {@code player_exp[lev - 1]} is
     * {@code playerExperience.get(lev - 1)}; the first value is 10 and the last 5,000,000. Unlike C
     * the table does not exist until this runs, which {@code GameConstants.init()} does after every
     * data file has loaded, so nothing read during loading can see it.
     *
     * <p>Method initializeExpLevel coded on 260925, commented in full on 261011.
     */
    public static void initializeExpLevel() {
        long[] playerExp = new long[50];
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

        for (int index = 0; index < 50; index++) {
            playerExp[index] = PlayerRegistry.playerExperience.get(index);
        }
        PlayerEventStatusUpdate.updatePlayerCharSheetExpToLevel(playerExp);
    }
    
    /**
     * Load the timed-effect definitions from {@code player_timed.txt} into {@link PlayerRegistry}.
     * The port of C's {@code player_timed_parser} ({@code player-timed.c}), run by {@code init_arrays()}
     * after the classes.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>Method loadPlayerTimedProperties coded before 261011, commented in full on 261011, updated
     * on 261011 to stop the load on an unreadable or empty file, and on 261104 for the
     * removal of the soft-error path.
     */
    public static void loadPlayerTimedProperties() {
        PlayerTimedReader parser = new PlayerTimedReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "player_timed.txt";

        try {
            ParseResult<PlayerTimedEffect> result = parser.parseWithResults(filename);

            PlayerRegistry.setPlayerTimedEffects(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the player classes from {@code class.txt} into {@link PlayerRegistry}. Must run after
     * magic realms, item objects and summons, which the class assembler resolves against. The port of
     * C's {@code class_parser} ({@code init.c}), whose {@code run_parse_class} reads {@code class.txt}.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>This loader's dependencies are the deepest in the suite - a class resolves spellbooks
     * through the object kinds. A failure upstream stops the load there, so it never reaches this
     * loader; a reference that still cannot be resolved here is the class assembler's error to
     * throw.
     *
     * <p>Method loadPlayerClasses coded before 261011, commented in full on 261011, updated on
     * 261011 to stop the load on an unreadable or empty file, and on 261104 for the
     * removal of the soft-error path.
     */
    public static void loadPlayerClasses() {
        PlayerClassReader parser = new PlayerClassReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "class.txt";

        try {
            ParseResult<PlayerClass> result = parser.parseWithResults(filename);
            
            PlayerRegistry.setPlayerClasses(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the magic realms from {@code realm.txt} into {@link PlayerRegistry}. Must run before
     * classes, whose spellbooks reference a realm by name. The port of C's {@code realm_parser}
     * ({@code init.c}), whose {@code run_parse_realm} reads {@code realm.txt}.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>Method loadMagicRealms coded before 261011, commented in full on 261011, updated on 261011
     * to stop the load on an unreadable or empty file, and on 261104 for the
     * removal of the soft-error path.
     */
    public static void loadMagicRealms() {
        RealmReader parser = new RealmReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "realm.txt";

        try {
            ParseResult<MagicRealm> result = parser.parseWithResults(filename);

            PlayerRegistry.setMagicRealm(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the player races from {@code p_race.txt} into {@link PlayerRegistry}. Must run after
     * bodies and history, which each race references. The port of C's {@code p_race_parser}
     * ({@code init.c}), whose {@code run_parse_p_race} reads {@code p_race.txt}.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>Method loadPlayerRaces coded before 261011, commented in full on 261011, updated on 261011
     * to stop the load on an unreadable or empty file, and on 261104 for the
     * removal of the soft-error path.
     */
    public static void loadPlayerRaces() {
        PlayerRaceReader parser = new PlayerRaceReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "p_race.txt";

        try {
            ParseResult<PlayerRace> result = parser.parseWithResults(filename);

            PlayerRegistry.setPlayerRaces(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the body layouts from {@code body.txt} into {@link PlayerRegistry}. Must run before
     * races, which reference a body by index. The port of C's {@code body_parser} ({@code init.c}),
     * whose {@code run_parse_body} reads {@code body.txt}.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>Races resolve a body by <em>index</em> rather than by name, so a partial load here would
     * shift every later index. None can happen: any error stops the load before anything is
     * registered.
     *
     * <p>Method loadBodies coded before 261011, commented in full on 261011, updated on 261011 to
     * stop the load on an unreadable or empty file, and on 261104 for the removal of the soft-error
     * path.
     */
    public static void loadBodies() {
        BodyReader reader = new BodyReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "body.txt";

        try {
            ParseResult<PlayerBody> results = reader.parseWithResults(filename);

            PlayerRegistry.setPlayerBodies(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the background-history charts from {@code history.txt} into {@link PlayerRegistry}. Must
     * run before races, each of which starts at a named chart. The port of C's {@code history_parser}
     * ({@code init.c}), whose {@code run_parse_history} reads {@code history.txt}.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>The charts are a linked structure. Any error stops the load before anything is
     * registered, so an entry can never be left pointing at a successor that did not load.
     *
     * <p>Method loadPlayerHistories coded before 261011, commented in full on 261011, updated on
     * 261011 to stop the load on an unreadable or empty file, and on 261104 for the
     * removal of the soft-error path.
     */
    public static void loadPlayerHistories() {
        HistoryReader reader = new HistoryReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "history.txt";

        try {
            ParseResult<PlayerHistoryChart> results = reader.parseWithResults(filename);

            PlayerRegistry.setPlayerHistoryCharts(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the player shapes from {@code shape.txt} into {@link PlayerRegistry}. Must run before the
     * effects that reference a shape by name (C's {@code EST_SHAPECHANGE}). The port of C's
     * {@code shape_parser} ({@code init.c}), whose {@code run_parse_shape} reads {@code shape.txt};
     * C runs it before the objects, as the Java load order does.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>Method loadPlayerShapes coded before 261011, commented in full on 261011, updated on
     * 261011 to stop the load on an unreadable or empty file and to drop the checked
     * {@code IOException} from its signature, and on 261104 for the removal of the
     * soft-error path.
     */
    public static void loadPlayerShapes() {
        ShapeReader parser = new ShapeReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "shape.txt";

        try {
            ParseResult<PlayerShape> result = parser.parseWithResults(filename);

            PlayerRegistry.setPlayerShape(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }

    /**
     * Load the player properties (stats, flags and their UI bindings) from
     * {@code player_property.txt} into {@link PlayerRegistry}. Must run after the UI entries its
     * {@code bindui:} lines resolve against. The port of C's {@code player_property_parser}
     * ({@code init.c}), whose {@code run_parse_player_prop} reads {@code player_property.txt}.
     *
     * <p>Every error stops the load, as C's {@code parse_file_quit_not_found} and
     * {@code init_arrays()} do, and nothing is registered. An unreadable file is logged and
     * rethrown as a {@code RuntimeException}; a grammar or assembler error passes through unwrapped
     * (see the class description).
     *
     * <p>Method loadPlayerProperties coded before 261011, commented in full on 261011, updated on
     * 261011 to stop the load on an unreadable or empty file and to drop the checked
     * {@code IOException} from its signature, and on 261104 for the removal of the
     * soft-error path.
     */
    public static void loadPlayerProperties() {
        PlayerPropertyReader parser = new PlayerPropertyReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "player_property.txt";

        try {
            ParseResult<PlayerProperty> result = parser.parseWithResults(filename);

            PlayerRegistry.setPlayerProperties(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw new RuntimeException("Error while loading file " + filename, e);
        }
    }
}
