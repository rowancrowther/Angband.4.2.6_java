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
import uk.co.jackoftradesltd.backend.parser.ProjectionReader;
import uk.co.jackoftradesltd.backend.parser.QuestReader;
import uk.co.jackoftradesltd.backend.parser.WorldReader;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Startup loader for the world slice: parses the world/level-generation gamedata files
 * ({@code world.txt}, {@code projection.txt}, {@code quest.txt}) and populates
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry} through its setters.
 *
 * <p>This is the write side of the world slice, paired with {@code WorldRegistry} (the read side). It
 * was split out of {@code GameConstants} as one domain slice of the loader/registry refactor, and
 * {@code GameConstants.init()} calls its three loaders.
 *
 * <p>The three loaders stand in for C's {@code world_parser} ({@code init.c}), {@code projection_parser}
 * ({@code obj-init.c}) and {@code quests_parser} ({@code player-quest.c}), which {@code init_arrays()}
 * runs. Each follows the same shape: build the reader, parse the file under {@code lib/gamedata}, and
 * install the assembled list.
 *
 * <p>Every error is fatal, as in C, where {@code run_parser} quits on a bad line and
 * {@code parse_file_quit_not_found} on a missing file. There are three ways a load fails, and only
 * the first is handled here:
 * <ul>
 *   <li>an {@link IOException} from the reader is logged at {@code FATAL} and rethrown wrapped in a
 *       {@link RuntimeException} naming the file;</li>
 *   <li>a grammar or lexer error is thrown by the reader as a {@code ParseCancellationException}
 *       ({@code ParseErrors.throwIfAny});</li>
 *   <li>a record the assembler refuses is thrown as a {@code DataFileParseException}.</li>
 * </ul>
 * The last two are unchecked and pass through these loaders unlogged and unwrapped;
 * {@code GameConstants.init()} catches them and names the file. Nothing is installed from a file
 * that fails, so the registry never holds a partial list.
 *
 * <p>Order matters. {@code loadWorld} and {@code loadProjections} run straight after the game
 * constants, as C's first two parsers do, because later loaders depend on them (blow effects resolve
 * their element through the projections). {@code loadQuests} runs after the monsters, because each
 * quest names the monster race that completes it and {@code QuestAssembler} resolves that name.
 *
 * <p>The checks C makes in its {@code finish_parse_*} functions are made too: a bad level reference
 * stops {@code loadWorld}, and a projection file without exactly 25 elements, or with one of its
 * first 25 codes unknown or out of order, makes the assembler throw, so {@code loadProjections}
 * stops.
 *
 * <p>They differ from C in three ways:
 * <ul>
 *   <li>The files are read from the gamedata directory only. C's {@code parse_file}
 *   ({@code datafile.c}) tries a customised copy in the user directory first.</li>
 *   <li>The {@code "None"} that {@code world.txt} writes for a missing neighbour stays as text on the
 *   {@code World} record, where C turns it into {@code NULL}; {@code loadWorld} is where it is
 *   tested.</li>
 *   <li>The registry counts are the plain list sizes. C's {@code z_info->quest_max} equals the
 *   number of quests, with no blank slot, so the two agree.</li>
 * </ul>
 *
 * <p>Class WorldDataLoader coded before 261014, commented in full on 261014, failure paragraph
 * rewritten on 261104 for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class WorldDataLoader {
    /**
     * Logger for all three loaders: the {@code FATAL} line written before an IO failure or a bad
     * level reference is thrown. Grammar and assembler errors are not logged here (see the class
     * description). C has no counterpart: it reports through {@code quit()} and the parser's own
     * error text.
     *
     * <p>Field logger coded before 261014, commented in full on 261014, updated on 261104 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger(WorldDataLoader.class);

    /**
     * Load the dungeon level list from {@code world.txt} into {@link WorldRegistry}, standing in for
     * C's {@code world_parser} ({@code parse_world_level}, {@code run_parse_world} and
     * {@code finish_parse_world} in {@code init.c}).
     *
     * <p>Every error stops start-up before the registry is touched. A file that cannot be read,
     * which C reaches through {@code parse_file_quit_not_found}, and a bad level reference are each
     * logged at fatal and thrown as a {@link RuntimeException}; a grammar or assembler error passes
     * through unwrapped (see the class description).
     *
     * <p>The level reference check is {@code finish_parse_world}. For each level in file order, its
     * previous level and then its next level must be the name of some loaded level, unless the text is
     * {@code "None"}, which stands where C holds {@code NULL}. The comparison is exact and
     * case-sensitive, as {@code streq} is, so a neighbour written {@code none} is an ordinary name and
     * fails. C quits with "Invalid level reference"; the port names the missing level and whether it
     * was a previous or next reference. Nothing else is checked, as in C: depths and names may repeat
     * and need not be in order.
     *
     * <p>The levels are installed in file order, which is the order C's linked list holds them in.
     * Calling this again replaces the registry list rather than appending.
     *
     * <p>Function loadWorld coded before 261014, commented in full on 261014, updated on 261104 for
     * the removal of the soft-error path.
     *
     * @throws IOException declared, but never thrown: an {@code IOException} from the reader is
     *                     wrapped in a {@link RuntimeException}
     * @throws RuntimeException if {@code world.txt} cannot be read, or a level names a previous or
     *                          next level that is not loaded; or, unwrapped, if the file holds a
     *                          grammar or assembler error
     */
    public static void loadWorld() throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "world.txt";
        WorldReader worldReader = new WorldReader();

        try {
            ParseResult<World> result = worldReader.parseWithResults(filename);
            
            List<String> currLevels = new ArrayList<>();
            for (World world : result.items()) {
                currLevels.add(world.levelName());
            }
            
            for (World world : result.items()) {
                if (world.prevLevel() != null 
                        && !world.prevLevel().equals("None")
                        && !currLevels.contains(world.prevLevel())) {
                    String message = "Previous level " + world.prevLevel() + " does not exist in list of world levels";
                    logger.fatal(message);
                    throw new RuntimeException(message);
                }
                if (world.nextLevel() != null 
                        && !world.nextLevel().equals("None")
                        && !currLevels.contains(world.nextLevel())) {
                    String message = "Next level " + world.nextLevel() + " does not exist in list of world levels";
                    logger.fatal(message);
                    throw new RuntimeException(message);
                }
            }
            
            WorldRegistry.setWorlds(result.items());
        } catch (IOException e) {
            String message = "Error loading " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the projection types from {@code projection.txt} into {@link WorldRegistry}, standing in
     * for C's {@code projection_parser} ({@code init_parse_projection}, {@code run_parse_projection}
     * and {@code finish_parse_projection} in {@code obj-init.c}). Must run before blow effects, which
     * resolve their projection by code.
     *
     * <p>Every error stops start-up before the registry is touched. A file that cannot be read,
     * which C reaches through {@code parse_file_quit_not_found}, is logged at fatal and thrown as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description). Later loaders depend on this list, so failing here names the real file rather
     * than surfacing as an unresolvable projection much later.
     *
     * <p>The checks C makes in {@code parse_projection_code} (the first 25 codes are the elements in
     * order) and in {@code finish_parse_projection} ("Too few elements" and "Too many elements" in
     * {@code projection.txt}) are not made here. {@code ProjectionAssembler} makes them and throws a
     * {@code DataFileParseException}, a {@link RuntimeException}, which this method does not catch.
     * It leaves {@code loadProjections} unchanged, before the registry is touched, with the position
     * or the count in its message. The projections are installed in
     * file order, which is the order of C's {@code projections[]}. Calling this again replaces the
     * registry list rather than appending.
     *
     * <p>Function loadProjections coded before 261014, commented in full on 261014; the element
     * checks reworded on 261021 for the assembler's stop-the-load path; updated on 261104 for the
     * removal of the soft-error path.
     *
     * @throws IOException declared, but never thrown: an {@code IOException} from the reader is
     *                     wrapped in a {@link RuntimeException}
     * @throws RuntimeException if {@code projection.txt} cannot be read; or, unwrapped, if the file
     *                          holds a grammar error or the assembler stops the load with a
     *                          {@code DataFileParseException}
     */
    public static void loadProjections() throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "projection.txt";
        ProjectionReader reader = new ProjectionReader();

        try {
            ParseResult<Projection> result = reader.parseWithResults(filename);

            WorldRegistry.setProjections(result.items());
        } catch (IOException e) {
            String message = "Error loading " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the quest definitions from {@code quest.txt} into {@link WorldRegistry}, standing in for
     * C's {@code quests_parser} ({@code init_parse_quest}, {@code run_parse_quest} and
     * {@code finish_parse_quest} in {@code player-quest.c}). Must run after monsters, since each quest
     * references the monster race that completes it.
     *
     * <p>Every error stops start-up before the registry is touched. A file that cannot be read,
     * which C reaches through {@code parse_file_quit_not_found}, is logged at fatal and thrown as a
     * {@link RuntimeException}; a grammar or assembler error passes through unwrapped (see the class
     * description). A file with no quests at all is one of those grammar errors, which differs from
     * C: {@code finish_parse_quest} accepts an empty list and sets {@code z_info->quest_max} to 0,
     * but {@code QuestGrammar} requires at least one quest.
     *
     * <p>The work C does in its {@code parse_quest_*} handlers and in {@code finish_parse_quest}, the
     * race lookup and the numbering of the quests from 0 in file order, is done by
     * {@code QuestAssembler}. The quests are installed in that order, and
     * {@code WorldRegistry.getQuestMax} reports the list size, which is C's {@code z_info->quest_max}.
     * Calling this again replaces the registry list rather than appending.
     *
     * <p>Function loadQuests coded before 261014, commented in full on 261014, updated on 261104 for
     * the removal of the soft-error path.
     */
    public static void loadQuests() {
        QuestReader parser = new QuestReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "quest.txt";

        try {
            ParseResult<Quest> result = parser.parseWithResults(filename);

            WorldRegistry.setQuests(result.items());
        } catch (IOException e) {
            String message = "Error loading " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
