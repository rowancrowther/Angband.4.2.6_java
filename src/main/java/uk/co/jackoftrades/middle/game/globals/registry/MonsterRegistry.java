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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.frontend.colour.FlickerTable;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;
import uk.co.jackoftrades.middle.cave.PitProfile;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.monsters.*;

import java.util.Collections;
import java.util.List;

/**
 * Runtime holder for all monster-domain game data — races, bases, pain messages, summons, blow
 * methods and effects, spell types, pit profiles, lore, and the visuals cycler/flicker tables —
 * together with the derived {@code *Max} counters and the name/index lookups the running game
 * queries against.
 *
 * <p>This is the read side of the monster slice: it is populated once at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.MonsterDataLoader} (driven from
 * {@code GameConstants.init()} in dependency order) and thereafter only read. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor, so the monster data
 * has a single cohesive home rather than living among every other data type.
 *
 * @author Rowan Crowther
 */
public class MonsterRegistry {
    private static final Logger logger = LogManager.getLogger();

    /**
     * The loaded monster races — the master list every race lookup scans.
     */
    public static List<MonsterRace> monsterRaces;
    /**
     * Number of loaded monster races (C's {@code z_info->r_max}).
     */
    public static int monsterRaceMax;
    /**
     * Number of monster-pain message records (C's {@code z_info->mp_max}).
     */
    public static int monsterPainMsgMax;
    /**
     * Number of pit/nest profile types.
     */
    private static int monsterPitTypeMax;
    /**
     * Maximum number of blows a single monster attack can carry.
     */
    private static int monsterBlowsMax;
    /**
     * Number of loaded blow methods (set from {@link #setBlowMethods}).
     */
    private static int monsterBlowsMethodsMax;
    /**
     * Number of loaded blow effects (set from {@link #setBlowEffects}).
     */
    private static int monsterBlowsEffectsMax;
    /**
     * The loaded monster-pain message sets, indexed by pain type.
     */
    private static List<MonsterPain> monsterPains;
    /**
     * The loaded monster bases — shared templates that races reference.
     */
    private static List<MonsterBase> monsterBases;
    /**
     * The loaded summon specifications, resolved by name.
     */
    private static List<Summon> summons;
    /**
     * The loaded blow methods — how a monster attack is delivered.
     */
    private static List<BlowMethod> blowMethods;
    /**
     * The loaded blow effects — what a monster attack does.
     */
    private static List<BlowEffect> blowEffects;
    /**
     * The loaded monster spell types.
     */
    private static List<MonsterSpellType> monsterSpellTypes;
    /**
     * The loaded pit/nest profiles used during level generation.
     */
    private static List<PitProfile> monsterPitProfiles;
    /**
     * The loaded monster lore (not currently populated; see the commented loader).
     */
    private static List<MonsterLore> monsterLore;
    /**
     * The loaded colour-cycling table for animated monster colours.
     */
    public static VisualsCycler visualsCyclerTable = null;
    /**
     * The loaded colour-flicker table for flickering monster colours.
     */
    public static FlickerTable visualsFlickerTable = null;

    /**
     * Stores the loaded colour-cycling (visuals) table; set once by {@code MonsterDataLoader}.
     */
    public static void setVisualsCyclerTable(VisualsCycler visualsCyclerTable) {
        MonsterRegistry.visualsCyclerTable = visualsCyclerTable;
    }

    /**
     * Stores the loaded colour-flicker table; set once by {@code MonsterDataLoader}.
     */
    public static void setVisualsFlickerTable(FlickerTable visualsFlickerTable) {
        MonsterRegistry.visualsFlickerTable = visualsFlickerTable;
    }

    /**
     * Stores the loaded monster-pain records; set once by {@code MonsterDataLoader}.
     */
    public static void setMonsterPains(List<MonsterPain> monsterPains) {
        MonsterRegistry.monsterPains = monsterPains;
    }

    /**
     * Stores the loaded monster bases; set once by {@code MonsterDataLoader}.
     */
    public static void setMonsterBases(List<MonsterBase> monsterBases) {
        MonsterRegistry.monsterBases = monsterBases;
    }

    /**
     * Stores the loaded summon records; set once by {@code MonsterDataLoader}.
     */
    public static void setSummons(@Nullable List<Summon> summons) {
        MonsterRegistry.summons = summons;
    }

    /**
     * Stores the loaded blow methods and records their count in {@code monsterBlowsMethodsMax}.
     */
    public static void setBlowMethods(List<BlowMethod> methods) {
        MonsterRegistry.blowMethods = methods;
        monsterBlowsMethodsMax = methods.size();
    }

    /**
     * Stores the loaded blow effects and records their count in {@code monsterBlowsEffectsMax}.
     */
    public static void setBlowEffects(List<BlowEffect> blowEffects) {
        MonsterRegistry.blowEffects = blowEffects;
        monsterBlowsEffectsMax = blowEffects.size();
    }

    /**
     * @return an unmodifiable view of the loaded monster races
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static List<MonsterRace> getMonsterRaces() {
        return Collections.unmodifiableList(monsterRaces);
    }

    /**
     * Stores the loaded monster races; set once by {@code MonsterDataLoader}.
     */
    public static void setMonsterRaces(List<MonsterRace> monsterRaces) {
        MonsterRegistry.monsterRaces = monsterRaces;
    }

    /**
     * Stores the loaded pit/nest profiles; set once by {@code MonsterDataLoader}.
     */
    public static void setMonsterPitProfiles(List<PitProfile> monsterPitProfiles) {
        MonsterRegistry.monsterPitProfiles = monsterPitProfiles;
    }

    /**
     * Stores the loaded monster spell types; set once by {@code MonsterDataLoader}.
     */
    public static void setMonsterSpellTypes(List<MonsterSpellType> monsterSpellTypes) {
        MonsterRegistry.monsterSpellTypes = monsterSpellTypes;
    }

    /**
     * Find a monster race by name, mirroring C's {@code lookup_monster}: an exact case-insensitive
     * match wins, and failing that the first race whose name <em>contains</em> the query (also
     * case-insensitive) is returned as the closest match. Used to resolve friend and shape references,
     * and by the lore and pit parsers.
     *
     * @param name the race name to look up
     * @return the exact match, the closest substring match, or {@code null} if neither exists
     * @throws IllegalStateException if the monster races have not been loaded yet
     * @author Rowan Crowther
     */
    @Nullable
    public static MonsterRace lookupMonsterRace(@NotNull String name) {
        if (monsterRaces == null) {
            String message = "Invalid attempt to access monsterRaces when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        MonsterRace closest = null;

        for (MonsterRace monsterRace : monsterRaces) {
            if (name.equalsIgnoreCase(monsterRace.getName())) return monsterRace;
            if (closest == null && monsterRace.getName().toLowerCase().contains(name.toLowerCase()))
                closest = monsterRace;
        }

        return closest;
    }

    /**
     * Searches for a summon based on the summon name
     *
     * @param summonName the name/type of the Summon
     * @return the Summon where name is equal to the incoming parameter
     */
    @Nullable
    @CheckReturnValue
    public static Summon lookupSummon(String summonName) {
        if (summons == null) {
            String message = "Invalid attempt to access summons when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return summons.stream()
                .filter(s -> s.getName().equals(summonName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a monster base by its code name.
     *
     * @param name the monster base code name
     * @return the matching {@link MonsterBase}, or {@code null} if none matches
     * @throws IllegalStateException if monster bases have not been loaded
     * @author Rowan Crowther
     */
    @Nullable
    public static MonsterBase lookupMonsterBase(@NotNull String name) {
        if (monsterBases == null) {
            String message = "Invalid attempt to access monsterBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return monsterBases.stream().filter(b -> name.equals(b.getCodeName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find a MonsterPain record from a incoming number
     *
     * @param monsterType The number from the 12 monster pain records
     * @return The monsterPain with index = monsterType
     */
    @Nullable
    @Contract(pure = true)
    @CheckReturnValue
    public static MonsterPain lookupMonsterPain(int monsterType) {
        if (monsterPains == null) {
            String message = "Invalid attempt to access monsterPain when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        if (monsterType <= 0 || monsterType > 12)
            return null;

        return monsterPains.stream()
                .filter(e -> e.getPainIndex() == monsterType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a monster blow effect by name.
     *
     * @param effectName the blow effect name
     * @return the matching {@link BlowEffect}, or {@code null} if none matches
     * @throws IllegalStateException if blow effects have not been loaded
     * @author Rowan Crowther
     */
    @Nullable
    public static BlowEffect lookupBlowEffect(@NotNull String effectName) {
        if (blowEffects == null) {
            String message = "Invalid attempt to access blowEffects when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return blowEffects.stream().filter(b -> effectName.equals(b.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Look up a monster blow method by name.
     *
     * @param methodName the blow method name
     * @return the matching {@link BlowMethod}, or {@code null} if none matches
     * @throws IllegalStateException if blow methods have not been loaded
     * @author Rowan Crowther
     */
    @Nullable
    public static BlowMethod lookupBlowMethod(@NotNull String methodName) {
        if (blowMethods == null) {
            String message = "Invalid attempt to access blowMethods when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return blowMethods.stream().filter(b -> methodName.equals(b.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Find a monster pain record by its index (linear scan).
     *
     * @param index the pain-record index
     * @return the matching {@link MonsterPain}, or {@code null} if none matches
     * @author Rowan Crowther
     */
    public static @Nullable MonsterPain getPainFromIndex(int index) {
        if (monsterPains == null) {
            String message = "Invalid attempt to access monsterPain when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        for (MonsterPain monsterPain : monsterPains) {
            if (monsterPain.getPainIndex() == index) {
                return monsterPain;
            }
        }

        return null;
    }

    /**
     * @return the configured value of {@code monsterRaceMax}
     * @author Rowan Crowther
     */
    public static int getMonsterRaceMax() {
        return monsterRaceMax;
    }

    /**
     * @return the configured value of {@code monsterPainMsgMax}
     * @author Rowan Crowther
     */
    public static int getMonsterPainMsgMax() {
        return monsterPainMsgMax;
    }

    /**
     * @return the configured value of {@code monsterPitTypeMax}
     * @author Rowan Crowther
     */
    public static int getMonsterPitTypeMax() {
        return monsterPitTypeMax;
    }

    /**
     * @return the configured value of {@code monsterBlowsMax}
     * @author Rowan Crowther
     */
    public static int getMonsterBlowsMax() {
        return monsterBlowsMax;
    }

    /**
     * @return the configured value of {@code monsterBlowsMethodsMax}
     * @author Rowan Crowther
     */
    public static int getMonsterBlowsMethodsMax() {
        return monsterBlowsMethodsMax;
    }

    /**
     * @return the configured value of {@code monsterBlowsEffectsMax}
     * @author Rowan Crowther
     */
    public static int getMonsterBlowsEffectsMax() {
        return monsterBlowsEffectsMax;
    }

    /**
     * Find a monster base by its code name (linear scan).
     *
     * @param name the monster base code name
     * @return the matching {@link MonsterBase}, or {@code null} if none matches
     * @author Rowan Crowther
     */
    public static @Nullable MonsterBase getBaseFromName(String name) {
        for (MonsterBase monsterBase : monsterBases) {
            if (monsterBase.getCodeName().equals(name)) {
                return monsterBase;
            }
        }

        return null;
    }

    /**
     * @return the loaded colour-cycling table
     * @author Rowan Crowther
     */
    public static VisualsCycler getVisualsCyclerTable() {
        return visualsCyclerTable;
    }

}
