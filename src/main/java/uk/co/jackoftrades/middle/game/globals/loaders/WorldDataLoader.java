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
import uk.co.jackoftrades.backend.parser.ParseResult;
import uk.co.jackoftrades.backend.parser.ProjectionReader;
import uk.co.jackoftrades.backend.parser.QuestReader;
import uk.co.jackoftrades.backend.parser.WorldReader;
import uk.co.jackoftrades.middle.cave.World;
import uk.co.jackoftrades.middle.game.event.projection.Projection;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftrades.middle.player.Quest;

import java.io.IOException;

/**
 * Startup loader for the world slice: parses the world/level-generation gamedata files
 * ({@code world.txt}, {@code projection.txt}, {@code quest.txt}) and populates
 * {@link uk.co.jackoftrades.middle.game.globals.registry.WorldRegistry} through its setters.
 *
 * <p>This is the write side of the world slice, paired with {@code WorldRegistry} (the read side).
 * Note the three loaders deliberately report a bad file three different ways, and each must be
 * preserved:
 * <ul>
 *   <li>{@code loadWorld} <em>throws</em> on parse errors (a broken world table aborts init);</li>
 *   <li>{@code loadProjections} logs and <em>returns</em> on parse errors, but rethrows IO errors;</li>
 *   <li>{@code loadQuests} logs and returns on parse errors <em>and</em> swallows IO errors.</li>
 * </ul>
 * {@code loadQuests} runs after monsters (quests reference monster races); world and projections are
 * loaded early because much of the rest of init depends on them. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class WorldDataLoader {
    private static final Logger logger = LogManager.getLogger(WorldDataLoader.class);

    /**
     * Load the dungeon level list from {@code world.txt} into {@link WorldRegistry}. Self-contained
     * (each level names only its neighbours, resolved within the file).
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the levels that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>rethrown</em>: there is no game without a dungeon, so this one stops the load.
     *
     * @throws IOException if there is a problem loading the file
     * @author Rowan Crowther
     */
    public static void loadWorld() throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "world.txt";
        WorldReader worldReader = new WorldReader();

        try {
            ParseResult<World> result = worldReader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            WorldRegistry.setWorlds(result.items());
        } catch (IOException e) {
            String message = "Error loading lib/gamedata/world.txt file.";
            logger.error(message, e);
            throw e;
        }
    }

    /**
     * Load the projection (element/effect) types from {@code projection.txt} into
     * {@link WorldRegistry}. Must run before blow effects, which resolve their element by name.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the projections that
     * did assemble are registered regardless, per the partial-results contract. An IO failure is
     * logged and <em>rethrown</em>; later loaders depend on this list, so failing here names the
     * real file rather than surfacing as an unresolvable element much later.
     *
     * @throws IOException if there is a problem loading the file
     * @author Rowan Crowther
     */
    public static void loadProjections() throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "projection.txt";
        ProjectionReader reader = new ProjectionReader();

        try {
            ParseResult<Projection> result = reader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            WorldRegistry.setProjections(result.items());
        } catch (IOException e) {
            String message = "Error loading lib/gamedata/projection.txt file.";
            logger.error(message, e);
            throw e;
        }
    }

    /**
     * Load the quest definitions from {@code quest.txt} into {@link WorldRegistry}. Must run after
     * monsters, since each quest references the monster race that completes it.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the quests that did
     * assemble are registered regardless, per the partial-results contract. An IO failure is logged
     * and <em>swallowed</em>: nothing else loads from the quest list, so a missing file costs the
     * quests rather than the game.
     *
     * @author Rowan Crowther
     */
    public static void loadQuests() {
        QuestReader parser = new QuestReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "quest.txt";

        try {
            ParseResult<Quest> result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            WorldRegistry.setQuests(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }
}
