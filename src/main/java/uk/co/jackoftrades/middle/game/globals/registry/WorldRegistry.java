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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.middle.cave.World;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
import uk.co.jackoftrades.middle.game.event.projection.Projection;
import uk.co.jackoftrades.middle.player.Quest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Runtime holder for the world/level-generation game data — the {@link World} levels (the tower of
 * dungeon depths), the {@link Projection} types (how damage and effects travel), and the
 * {@link Quest} definitions — plus the derived size accessors and the projection lookups the running
 * game queries.
 *
 * <p>This is the read side of the world slice: it is populated once at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.WorldDataLoader} and thereafter only read.
 * It was split out of {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * <p>The {@code get*Max}/{@code getMaxRandDepth} accessors report list sizes rather than separate
 * counters — the C {@code z_info} bounds they replace — so they stay in step with the loaded data by
 * construction. The projection lookups are search-style and carry the "not initialised" guard for
 * the reason the object-kind lookups do: an unloaded list would otherwise degrade to a false "not
 * found" rather than a loud failure.
 *
 * @author Rowan Crowther
 */
public class WorldRegistry {
    /**
     * Logger used to report access before the registry has been populated.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * The loaded world levels — Town (level 0) up to Angband 127. Its size drives
     * {@link #getMaxRandDepth}.
     */
    private static List<World> worlds;
    /**
     * The loaded projection types, resolved by code/description via the {@code lookupProjection*} methods.
     */
    private static List<Projection> projections;
    /**
     * The loaded quest definitions.
     */
    private static List<Quest> quests;

    /**
     * @return an unmodifiable view of the loaded world levels
     */
    public static List<World> getWorlds() {
        return Collections.unmodifiableList(worlds);
    }

    /**
     * Stores the loaded world levels; set once by {@code WorldDataLoader}.
     */
    public static void setWorlds(List<World> worlds) {
        WorldRegistry.worlds = worlds;
    }

    /**
     * @return an unmodifiable view of the loaded projection types
     */
    public static List<Projection> getProjections() {
        return Collections.unmodifiableList(projections);
    }

    /**
     * Stores the loaded projection types; set once by {@code WorldDataLoader}.
     */
    public static void setProjections(List<Projection> projections) {
        WorldRegistry.projections = projections;
    }

    /**
     * @return an unmodifiable view of the loaded quest definitions
     */
    public static List<Quest> getQuests() {
        return Collections.unmodifiableList(quests);
    }

    /**
     * Stores the loaded quest definitions; set once by {@code WorldDataLoader}.
     */
    public static void setQuests(List<Quest> quests) {
        WorldRegistry.quests = quests;
    }

    /**
     * Look up a projection by its code, as used by the {@code lash-type:} directive in
     * {@code blow_effects.txt}.
     * <p>
     * Note this matches on the projection's {@code code:} - its {@link ProjectionEnum} -
     * and not on its {@code lash-desc:}, which is the flavour text shown to the player
     * ({@code venom}, {@code razors}) and never appears in another data file. This mirrors
     * [C] {@code proj_name_to_idx} ({@code src/project.c:60}).
     *
     * @param lashType the projection code to find
     * @return the matching {@link Projection}, or {@code null} if none matches
     * @throws IllegalStateException if projections have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static Projection lookupProjectionByLash(ProjectionEnum lashType) {
        if (projections == null) {
            String message = "Invalid attempt to access projections when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return projections.stream().filter(p -> lashType.equals(p.getProjection()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a projection by its {@code lash-desc:} — the flavour text shown to the player
     * ({@code venom}, {@code razors}). This is the description-side counterpart to
     * {@link #lookupProjectionByLash}, which matches on the machine-readable {@code code:}.
     *
     * @param name the projection's lash description to find
     * @return the matching {@link Projection}, or {@code null} if none matches
     * @throws IllegalStateException if projections have not been loaded
     * @author Rowan Crowther
     */
    @Nullable
    public static Projection lookupProjectionByName(String name) {
        if (projections == null) {
            String message = "Invalid attempt to access projections when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return projections.stream().filter(p -> name.equals(p.getLashDescription()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the first loaded projection of the given broad category (element/environs/monster).
     * <p>
     * Note that despite the name this matches on {@code type:} rather than on {@code code:}, and
     * that many projections share a type — {@code projection.txt} declares twenty-five elements
     * alone — so "first" here means whichever came earliest in the file, not a uniquely identified
     * projection. To find one particular projection, use {@link #lookupProjectionByLash}, which
     * matches on the code.
     *
     * @param type the category to find a projection for
     * @return the first {@link Projection} of that category, or {@code null} if none is loaded
     * @throws IllegalStateException if projections have not been loaded
     * @author Rowan Crowther
     */
    @Nullable
    public static Projection lookupProjectionByCode(ProjectionType type) {
        if (projections == null) {
            String message = "Invalid attempt to access projections when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return projections.stream().filter(p -> p.getType() == type)
                .findFirst()
                .orElse(null);
    }

    /**
     * @return the number of loaded quests (C's {@code z_info->quest_max})
     * @author ClaudeCode
     */
    public static int getQuestMax() {
        return quests.size();
    }

    /**
     * @return the number of loaded projection types (C's {@code z_info->proj_max})
     * @author ClaudeCode
     */
    public static int getProjMax() {
        return projections.size();
    }

    /**
     * @return the deepest reachable dungeon level — the number of loaded world levels
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMaxRandDepth() {
        if (worlds == null) {
            IllegalStateException e = new IllegalStateException("Worlds hasn't been initialized");
            logger.fatal("Worlds hasn't been initialized", e);
            throw e;
        }

        return worlds.size();
    }

    /**
     * Finds the world level with the given name. Ports C's {@code level_by_name}
     * ({@code src/game-world.c}), which walks the {@code world} linked list looking for a
     * name match.
     *
     * @param name the level name to search for
     * @return the matching {@link World}, or {@link Optional#empty()} if no level has that name
     * @throws IllegalStateException if the world list has not been initialised
     * @author Rowan Crowther
     */
    @CheckReturnValue
    public static Optional<World> getLevelByName(String name) {
        if (worlds == null) {
            IllegalStateException e = new IllegalStateException("Worlds hasn't been initialized");
            logger.fatal("Worlds hasn't been initialized", e);
            throw e;
        }

        return worlds.stream().filter(w -> w.levelName().equals(name))
                .findFirst();
    }

    /**
     * Finds the world level at the given depth. Ports C's {@code level_by_depth}
     * ({@code src/game-world.c}), which walks the {@code world} linked list looking for the level
     * whose depth matches. In the port a level's depth is its {@link World#levelNumber()}.
     *
     * @param depth the dungeon depth to search for
     * @return the matching {@link World}, or {@link Optional#empty()} if no level is at that depth
     * @throws IllegalStateException if the world list has not been initialised
     * @author Rowan Crowther
     */
    @CheckReturnValue
    public static Optional<World> getLevelByDepth(int depth) {
        if (worlds == null) {
            IllegalStateException e = new IllegalStateException("Worlds hasn't been initialized");
            logger.fatal("Worlds hasn't been initialized", e);
            throw e;
        }

        return worlds.stream().filter(w -> w.levelNumber() == depth)
                .findFirst();
    }
}
