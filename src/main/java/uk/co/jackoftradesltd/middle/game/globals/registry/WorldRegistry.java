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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.player.Quest;

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
 * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.WorldDataLoader} and thereafter only read.
 * It was split out of {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * <p>The {@code get*Max}/{@code getMaxRandDepth} accessors report list sizes rather than separate
 * counters, so they stay in step with the loaded data by construction. Only {@link #getQuestMax}
 * has a C counterpart ({@code z_info->quest_max}); C's {@code PROJ_MAX} is a compile-time enum
 * constant in {@code project.h}, not a {@code z_info} field, and {@code z_info->max_depth} comes
 * from {@code world:max-depth} in {@code constants.txt} (see
 * {@code GameConstants.getWorldMaxDepth()}), not from the length of the {@code world} list. The
 * projection lookups are search-style and carry the "not initialized" guard for the reason the
 * object-kind lookups do: an unloaded list would otherwise degrade to a false "not found" rather
 * than a loud failure.
 *
 * <p>C reaches a projection by indexing {@code projections[]} with the {@code PROJ_*} value; the
 * port searches its list for the {@link ProjectionEnum} instead, so the list order carries no
 * meaning. None of the three lists carries a blank slot in C ({@code quest_max} counts only real
 * records, and {@code world.txt} and {@code projection.txt} start at real entries), so the accepted
 * blank-slot deviation for base-1 registries does not arise here.
 *
 * <p>Class WorldRegistry coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class WorldRegistry {
    /**
     * Logger used to report access before the registry has been populated.
     *
     * <p>Field logger coded before 261009, commented in full on 261009.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * The loaded world levels — Town (level 0) up to Angband 127. Its size drives
     * {@link #getMaxRandDepth}. This is the Java form of the C {@code world} linked list built from
     * {@code world.txt}; it is {@code null} until {@code WorldDataLoader} has run.
     *
     * <p>Field worlds coded before 261009, commented in full on 261009.
     */
    private static List<World> worlds;
    /**
     * The loaded projection types, resolved by code/description via the {@code lookupProjection*} methods.
     * This is the Java form of the C {@code projections[]} array built from {@code projection.txt};
     * it is {@code null} until {@code WorldDataLoader} has run.
     *
     * <p>Field projections coded before 261009, commented in full on 261009.
     */
    private static List<Projection> projections;
    /**
     * The loaded quest definitions. This is the Java form of the C {@code quests[]} array built from
     * {@code quest.txt}; it is {@code null} until {@code WorldDataLoader} has run.
     *
     * <p>Field quests coded before 261009, commented in full on 261009.
     */
    private static List<Quest> quests;

    /**
     * Returns the loaded world levels as a read-only view, in the order {@code world.txt} lists
     * them. Unlike the lookups, this has no "not initialized" guard: before loading it fails with a
     * {@link NullPointerException}.
     *
     * <p>Method getWorlds coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded world levels
     */
    public static List<World> getWorlds() {
        return Collections.unmodifiableList(worlds);
    }

    /**
     * Stores the loaded world levels; set once by {@code WorldDataLoader}. The list is kept as
     * given, not copied, so the caller must not modify it afterwards.
     *
     * <p>Method setWorlds coded before 261009, commented in full on 261009.
     *
     * @param worlds the levels parsed from {@code world.txt}
     */
    public static void setWorlds(List<World> worlds) {
        WorldRegistry.worlds = worlds;
    }

    /**
     * Returns the loaded projection types as a read-only view, in the order {@code projection.txt}
     * lists them. Unlike the lookups, this has no "not initialized" guard: before loading it fails
     * with a {@link NullPointerException}.
     *
     * <p>Method getProjections coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded projection types
     */
    public static List<Projection> getProjections() {
        return Collections.unmodifiableList(projections);
    }

    /**
     * Stores the loaded projection types; set once by {@code WorldDataLoader}. The list is kept as
     * given, not copied, so the caller must not modify it afterwards.
     *
     * <p>Method setProjections coded before 261009, commented in full on 261009.
     *
     * @param projections the projections parsed from {@code projection.txt}
     */
    public static void setProjections(List<Projection> projections) {
        WorldRegistry.projections = projections;
    }

    /**
     * Returns the loaded quest definitions as a read-only view, in the order {@code quest.txt}
     * lists them. Unlike the lookups, this has no "not initialized" guard: before loading it fails
     * with a {@link NullPointerException}.
     *
     * <p>Method getQuests coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the loaded quest definitions
     */
    public static List<Quest> getQuests() {
        return Collections.unmodifiableList(quests);
    }

    /**
     * Stores the loaded quest definitions; set once by {@code WorldDataLoader}. The list is kept as
     * given, not copied, so the caller must not modify it afterwards.
     *
     * <p>Method setQuests coded before 261009, commented in full on 261009.
     *
     * @param quests the quests parsed from {@code quest.txt}
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
     * [C] {@code proj_name_to_idx} ({@code project.c}), with two differences: C compares the name
     * string case-insensitively against its name list and returns the index (or {@code -1}), while
     * the port has already turned the name into a {@link ProjectionEnum} at parse time and returns
     * the {@link Projection} (or {@code null}). Searching by enum, not by list position, is why no
     * index is needed.
     *
     * <p>Method lookupProjectionByLash coded before 261009, commented in full on 261009.
     *
     * @param lashType the projection code to find
     * @return the matching {@link Projection}, or {@code null} if none matches
     * @throws IllegalStateException if projections have not been loaded
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
     * {@link #lookupProjectionByLash}, which matches on the machine-readable {@code code:}. The
     * comparison is case-sensitive. C has no counterpart: it reads {@code lash_desc} straight off
     * {@code projections[]} once it has the index.
     *
     * <p>Method lookupProjectionByName coded before 261009, commented in full on 261009.
     *
     * @param name the projection's lash description to find
     * @return the matching {@link Projection}, or {@code null} if none matches
     * @throws IllegalStateException if projections have not been loaded
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
     * matches on the code. C has no counterpart: its {@code type} string is only parsed into
     * {@code projection.txt} records.
     *
     * <p>Method lookupProjectionByType coded before 261009, commented in full on 261009.
     *
     * @param type the category to find a projection for
     * @return the first {@link Projection} of that category, or {@code null} if none is loaded
     * @throws IllegalStateException if projections have not been loaded
     */
    @Nullable
    public static Projection lookupProjectionByType(ProjectionType type) {
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
     * Looks up the single projection whose {@code code:} is the given {@link ProjectionEnum}. This is
     * the Java form of C's {@code projections[type]} indexing (and, with the name already resolved,
     * of {@code proj_name_to_idx} in {@code project.c}): C finds the record by position because the
     * {@code PROJ_*} value is its index, whereas the port searches the list for the matching enum.
     * It matches the same field as {@link #lookupProjectionByLash} and differs only in using
     * {@code ==} rather than {@code equals}, which is equivalent for an enum.
     *
     * <p>Method lookupProjectionByCode coded before 261009, commented in full on 261009.
     *
     * @param pCode the projection code to find
     * @return the matching {@link Projection}, or {@code null} if none matches (for example
     * {@code PROJ_NONE}, which has no record in {@code projection.txt})
     * @throws IllegalStateException if projections have not been loaded
     */
    public static Projection lookupProjectionByCode(ProjectionEnum pCode) {
        if (projections == null) {
            String message = "Invalid attempt to access projections when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return projections.stream().filter(p -> p.getProjection() == pCode)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the number of loaded quests. In C, {@code finish_parse_quest} ({@code player-quest.c})
     * sets {@code z_info->quest_max} by counting the parsed records, with no blank slot, so the
     * list size is the same number. Unlike the lookups it has no "not initialized" guard: before
     * loading it fails with a {@link NullPointerException}.
     *
     * <p>Method getQuestMax coded before 261009, commented in full on 261009.
     *
     * @return the number of loaded quests (C's {@code z_info->quest_max})
     */
    public static int getQuestMax() {
        return quests.size();
    }

    /**
     * Returns the number of loaded projection types. C has no {@code z_info->proj_max}: its bound
     * is {@code PROJ_MAX}, the last value of the compile-time {@code PROJ_*} enum in
     * {@code project.h} (the elements from {@code list-elements.h} followed by the projections from
     * {@code list-projections.h}), which equals the number of records in {@code projection.txt}.
     * Unlike the lookups it has no "not initialized" guard: before loading it fails with a
     * {@link NullPointerException}.
     *
     * <p>Method getProjMax coded before 261009, commented in full on 261009.
     *
     * @return the number of loaded projection types (C's {@code PROJ_MAX})
     */
    public static int getProjMax() {
        return projections.size();
    }

    /**
     * Returns the number of loaded world levels, which is one more than the deepest level number
     * (the Town is level 0). This stands in for C's {@code z_info->max_depth} only by coincidence of
     * data: C reads that bound from {@code world:max-depth} in {@code constants.txt} (the port's
     * {@code GameConstants.getWorldMaxDepth()}), independently of the length of {@code world.txt}.
     * Both are 128 in the shipped game data, so the two agree until one file is edited without the
     * other.
     *
     * <p>Method getMaxRandDepth coded before 261009, commented in full on 261009.
     *
     * @return the number of loaded world levels
     * @throws IllegalStateException if the world list has not been initialized
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
     * name match. The comparison is exact and case-sensitive, as {@code streq} is. Level names are
     * unique in {@code world.txt}, so taking the first match loses nothing. Where C falls off the
     * end of the list and returns {@code NULL}, this returns {@link Optional#empty()}.
     *
     * <p>Function getLevelByName coded on 260830, commented in full on 261009.
     *
     * @param name the level name to search for
     * @return the matching {@link World}, or {@link Optional#empty()} if no level has that name
     * @throws IllegalStateException if the world list has not been initialized
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
     * whose depth matches. In the port a level's depth is its {@link World#levelNumber()}. Depths
     * are unique in {@code world.txt}, so taking the first match loses nothing. Where C falls off
     * the end of the list and returns {@code NULL}, this returns {@link Optional#empty()}.
     *
     * <p>Function getLevelByDepth coded on 260830, commented in full on 261009.
     *
     * @param depth the dungeon depth to search for
     * @return the matching {@link World}, or {@link Optional#empty()} if no level is at that depth
     * @throws IllegalStateException if the world list has not been initialized
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
