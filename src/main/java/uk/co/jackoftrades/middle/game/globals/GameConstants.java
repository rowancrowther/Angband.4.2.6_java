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

package uk.co.jackoftrades.middle.game.globals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.numerics.Rational;
import uk.co.jackoftrades.backend.parser.*;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;
import uk.co.jackoftrades.backend.parser.world.WorldParser;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.screen.Screen;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Feature;
import uk.co.jackoftrades.middle.cave.PitProfile;
import uk.co.jackoftrades.middle.cave.TrapKind;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.combat.BlowMethod;
import uk.co.jackoftrades.middle.combat.CriticalLevel;
import uk.co.jackoftrades.middle.combat.O_CriticalLevel;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.Projection;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.monsters.*;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.*;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The central global registry of all loaded game data and tunable constants — the
 * Java port's equivalent of the C original's many global tables and {@code z-info}
 * constants gathered into one place. It holds:
 * <ul>
 *   <li>the filesystem layout ({@code ANGBAND_DIR_*});</li>
 *   <li>the scalar constants read from {@code constants.txt}, grouped by the
 *       section comments below (these mirror, one-to-one, the per-value
 *       documentation in the {@code backend.utils.globalvalues} classes);</li>
 *   <li>the master lists of every data type (monsters, objects, features,
 *       projections, classes, …) loaded from the {@code gamedata} files;</li>
 *   <li>the live {@link Chunk} {@code cave} and the {@link Player};</li>
 *   <li>{@code lookup*}/{@code get*} accessors over those lists, and the
 *       {@link #init()} pipeline plus its {@code load*} helpers that populate
 *       everything in dependency order.</li>
 * </ul>
 * It is a static-only holder (private constructor); the lookups throw
 * {@link IllegalStateException} if queried before {@link #init()} has populated
 * the relevant list.
 *
 * @author ClaudeCode
 */
public class GameConstants {
    /**
     * Logger used to report load failures and premature/invalid access.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Data structure for the Game Constants data read from the constants.txt
     * game data file.
     */
    private static GameConstantsParseRecord gameConstantsData;

    /**
     * A <code>record</code> used to store String key, String value pairs for the <code>constants.txt</code> file.
     * Currently only used for the <code>loadGameConstants</code> method. May be extended later.
     *
     * @param key   the String key
     * @param value the String value
     */
    private record Entry(@NotNull String key, @NotNull String value) {
    }

    /**
     * A <code>record</code> used to store Sting name, int value pairs for the <code>constants.txt</code> file.
     * Currently only used for the <code>loadGameConstants</code> method. May be extended later.
     *
     * @param name  the String key
     * @param value the int value
     */
    private record NameValuePair(@NotNull String name, Integer value) {
    }

    /**
     * A World record used to store the details of each level of the dungeon, including the Town
     *
     * @param levelName the name of the level
     * @param prevLevel the name of the previous level in the List of worlds
     * @param nextLevel the name of the next level in the list of worlds
     */
    public record World(int levelNumber, @NotNull String levelName, @NotNull String prevLevel,
                        @NotNull String nextLevel) {
    }

//    /**
//     * Translates a list of <code>GameConstantsParser.Entry</code> to <code>Entry</code>.
//     *
//     * @param parserEntries the incoming list of <code>GameConstantsParser</code> entries.
//     * @return A List of <code>Entry</code> records
//     */
//    @NotNull
//    @Unmodifiable
//    @Contract("_ -> !null")
//    private static List<Entry> toEntries(@Nullable List<GameConstantsParser.Entry> parserEntries) {
//        if (parserEntries == null) return List.of();
//
//        return parserEntries.stream()
//                .map(e -> new Entry(e.key(), e.value()))
//                .toList();
//    }

//    /**
//     * Translates a list of <code>WorldParser.ParsedWorld</code> records to <code>World</code> records.
//     *
//     * @param worlds an ArrayList of WorldParser.ParsedWorld records.
//     * @return a List of <code>Record</code> records.
//     */
//    @NotNull
//    @Unmodifiable
//    @Contract("_ -> !null")
//    private static List<World> toWorlds(@Nullable List<WorldParser.ParsedWorld> worlds) {
//        if (worlds == null) return List.of();
//
//        return worlds.stream()
//                .map(w -> new World(w.level(), w.levelName(), w.levelUp(), w.levelDown()))
//                .toList();
//    }

    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less than 128 may prevent some objects being created
     */
    private static int maxRandDepth;

    public static final int MAX_PVAL = 32_767;

    // The directory structure of Angband - OS neutral.
    // Note, if the user wants to save on a custom area, then we will have to amend the function BASE_DIR
    // to return that value. That's a future issue
    private static final String BASE_DIR = System.getProperty("user.dir");

    private static final String libPath = File.separator + "lib" + File.separator;
    private static final String configPath = File.separator + "config" + File.separator;
    private static final String userPath = File.separator + "user" + File.separator;
    public static final String ANGBAND_DIR_GAMEDATA = BASE_DIR + libPath + "gamedata" + File.separator;
    public static final String ANGBAND_DIR_CUSTOMIZE = BASE_DIR + configPath + "customize" + File.separator;
    public static final String ANGBAND_DIR_HELP = BASE_DIR + libPath + "help" + File.separator;
    public static final String ANGBAND_DIR_SCREENS = BASE_DIR + libPath + "screens" + File.separator;
    public static final String ANGBAND_DIR_FONTS = BASE_DIR + libPath + "fonts" + File.separator;
    public static final String ANGBAND_DIR_TILES = BASE_DIR + libPath + "tiles" + File.separator;
    public static final String ANGBAND_DIR_SOUNDS = BASE_DIR + libPath + "sounds" + File.separator;
    public static final String ANGBAND_DIR_ICONS = BASE_DIR + libPath + "icons" + File.separator;
    public static final String ANGBAND_DIR_USER = BASE_DIR + File.separator + "user" + File.separator;
    public static final String ANGBAND_DIR_ARCHIVE = BASE_DIR + userPath + "archives" + File.separator;
    public static final String ANGBAND_DIR_SCORES = BASE_DIR + userPath + "scores" + File.separator;
    public static final String ANGBAND_DIR_SAVE = BASE_DIR + userPath + "save" + File.separator;
    public static final String ANGBAND_DIR_PANIC = BASE_DIR + userPath + "panic" + File.separator;

    public static final String ANGBAND_INI = BASE_DIR + "angband.ini";

    public static final String ANGBAND_SYS = "xxx";

    public static final String iniFile = BASE_DIR + "angband.ini";

    public static final HashMap<Integer, Screen> AngbandScreens = new HashMap<>();

    public boolean gameInProgress = false;
    public boolean initialised = false;
    public boolean screensaverActive = false;

    public boolean canUseGraphics = false;
    public boolean changeTileSize = false;

    /*
     * Specific object kinds to be used to test against
     */
    public static final ObjectKind unknownGoldKind = new ObjectKind();
    public static final ObjectKind unknownItemKind = new ObjectKind();

    /*
     * Array bounds from C Not sure that these are still needed as most of the 'things' are stored in ArrayLists. Need to set
     * them on the load function,
     */
    private static int storeMax;
    private static int trapMax;
    private static int objectBaseKindMax;
    private static int artifactKindMax;
    private static int egoItemKindMax;
    private static int monsterRaceMax;
    private static int monsterPainMsgMax;
    private static int magicSpellMax;
    private static int monsterPitTypeMax;
    private static int randartActivationsMax;
    private static int curseMax;
    private static int slayMax;
    private static int brandMax;
    private static int monsterBlowsMax;
    private static int monsterBlowsMethodsMax;
    private static int monsterBlowsEffectsMax;
    private static int playerEquipmentSlotsMax;
    private static int caveProfileMax;
    private static int questMax;
    private static int projectionTypeMax;
    private static int objectPowerCalculationMax;
    private static int objectPropertyMax;
    private static int objectsInObject_txt;
    private static int playerShapeMax;

    private static GameConstantsData data;

    /*
     * Things read from constants.txt
     */
    private static void loadGameConstants() throws IOException {
        String filename = ANGBAND_DIR_GAMEDATA + "constants.txt";
        GameConstantsReader reader = new GameConstantsReader();
        GameConstantsParseResult result;

        try {
            result = reader.parse(filename);
            if (result.hasErrors()) return;
        } catch (IOException e) {
            logger.error("Error while reading file {}", filename, e);
            throw e;
        }

        data = result.getData();
    }

//    // Level things
//    private static int levelMonsterMax;
//
//    // Monster generation things
//    private static int allocMonsterChance;
//    private static int levelMonsterMin;
//    private static int townMonstersDay;
//    private static int townMonstersNight;
//    private static int reproMonstersNight;
//    private static int oodMonsterChance;
//    private static int oodMonsterAmount;
//    private static int monsterGroupMax;
//    private static int monsterGroupDist;
//
//    // Monster gameplay constants
//    private static int glyphHardness;
//    private static int reproMonsterRate;
//    private static int lifeDrainPercent;
//    private static int fleeRange;
//    private static int turnRange;
//
//    // Dungeon generation constants
//    private static int levelRoomMax;
//    private static int levelDoorMax;
//    private static int wallPierceMax;
//    private static int tunnGridMax;
//    private static int roomItemAv;
//    private static int bothItemAv;
//    private static int bothGoldAv;
//    private static int levelPitMax;
//
//    // World shape constants
//    private static int maxDepth;
//    private static int dayLength;
//    private static int dungeonHeight;
//    private static int dungeonWidth;
//    private static int townHeight;
//    private static int townWidth;
//    private static int feelingTotal;
//    private static int feelingNeed;
//    private static int stairSkip;
//    private static int moveEnergy;
//
//    // Carry capacity constants
//    private static int packSize;
//    private static int quiverSize;
//    private static int quiverSlotSize;
//    private static int thrownQuiverMult;
//    private static int floorSize;
//
//    // Store parameters
//    private static int storeInvenMax;
//    private static int storeTurns;
//    private static int storeShuffle;
//    private static int storeMagicLevel;
//
//    // Object creation constants
//    private static int maxObjDepth;
//    private static int greatObj;
//    private static int greatEgo;
//    private static int fuelTorch;
//    private static int fuelLamp;
//    private static int defaultLamp;
//
//    // Player constants
//    private static int maxSight;
//    private static int maxRange;
//    private static int startGold;
//    private static int foodValue;
//
//    // non-O melee critical calculations
//    private static int mCritDebuffToh;
//    private static int mCritChanceWeightScl;
//    private static int mCritChanceTohScl;
//    private static int mCritChanceLevelScl;
//    private static int mCritChanceTohSkillScl;
//    private static int mCritChanceOffset;
//    private static int mCritChanceRange;
//    private static int mCritPowerWeightScl;
//    private static int mCritPowerRandom;
//
//    // non-O ranged critical calculations
//    private static int rCritDebuffToh;
//    private static int rCritChanceWeightScl;
//    private static int rCritChanceTohScl;
//    private static int rCritChanceLevelScl;
//    private static int rCritChanceLaunchedTohSkillScl;
//    private static int rCritChanceThrownTohSkillScl;
//    private static int rCritChanceOffset;
//    private static int rCritChanceRange;
//    private static int rCritPowerWeightScl;
//    private static int rCritPowerRandom;
//
//    // O melee critical calculations
//    private static int oMCritDebuffToh;
//    private static int oMCritPowerTohSclNum;
//    private static int oMCritPowerTohSclDen;
//    private static int oMCritChancePowerSclNum;
//    private static int oMCritChancePowerSclDen;
//    private static int oMCritChanceAddDen;
//
//    private static Rational oMeleeMaxAdded;
//
//    // O ranged critical calculations
//    private static int oRCritDebuffToh;
//    private static int oRCritPowerLaunchedTohSclNum;
//    private static int oRCritPowerLaunchedTohSclDen;
//    private static int oRCritPowerThrownTohSclNum;
//    private static int oRCritPowerThrownTohSclDen;
//    private static int oRCritChancePowerSclNum;
//    private static int oRCritChancePowerSclDen;
//    private static int oRCritChanceAddDen;
//
//    private static Rational oRangedMaxAdded;
//
//    /*
//     * Maps of things
//     */
//    private static final HashMap<EquipmentSlotsEnum, Object> slots = new HashMap<>();
//    private static final HashMap<ObjectFlag, Object> flags = new HashMap<>();
//    private static final HashMap<ObjectModifier, Object> modifiers = new HashMap<>();
//    private static final HashMap<EffectEnum, Object> effects = new HashMap<>();
//    private static final HashMap<TrapEnum, Object> traps = new HashMap<>();
//    private static final HashMap<TerrainFeatureFlags, Object> terrainFlags = new HashMap<>();
//    private static final HashMap<MonsterRaceFlag, Object> monsterRaceFlags = new HashMap<>();
//    private static final HashMap<PlayerFlag, Object> playerInfoFlags = new HashMap<>();
//
//    private static final HashMap<TerrainFlags, Feature> terrains = new HashMap<>();
//
//    // non-O melee and ranged criticals
//    private static final ArrayList<CriticalLevel> mCriticalLevels = new ArrayList<>();
//    private static final ArrayList<CriticalLevel> rCriticalLevels = new ArrayList<>();
//
//    // O melee and ranged criticals
//    private static final ArrayList<O_CriticalLevel> mOCriticalLevels = new ArrayList<>();
//    private static final ArrayList<O_CriticalLevel> rOCriticalLevels = new ArrayList<>();

    /**
     * Worlds is a list of World records. There should be exactly 128 levels, as outlined in the maxRandDepth,
     * and should be from Town (level 0) to Angband127.
     */
    private static List<World> worlds = null;

    /*
     * Global arrays of master values
     */
    private static List<Projection> projections;
    private static List<UIEntryRenderer> uiEntryRenderers;
    private static List<UIEntryBase> uiEntryBases;
    private static List<UIEntry> uiEntries;
    private static List<PlayerProperty> playerProperties;
    private static List<Feature> features;
    private static List<ObjectBase> objectBases;
    private static List<Slay> slays;
    private static List<Brand> brands;
    private static List<MonsterPain> monsterPains;
    private static List<MonsterBase> monsterBases;
    private static List<Summon> summons;
    private static List<Curse> curses;
    private static List<PlayerShape> playerShapes;
    private static List<ItemObject> itemObjects;
    private static List<Activation> activations;
    private static List<EgoItem> egoItems;
    private static List<PlayerHistoryChart> playerHistoryCharts;
    private static List<PlayerBody> playerBodies;
    private static List<PlayerRace> playerRaces;
    private static List<MagicRealm> realms;
    private static List<PlayerClass> playerClasses;
    private static List<Artifact> artifacts;
    private static List<ObjectProperty> objectProperties;
    private static List<PlayerTimedEffect> playerTimedEffects;
    private static List<BlowMethod> blowMethods;
    private static List<BlowEffect> blowEffects;
    private static List<MonsterSpellType> monsterSpellTypes;
    private static VisualsCycler visualsCyclerTable = null;
    private static List<MonsterRace> monsterRaces;
    private static List<PitProfile> monsterPitProfiles;
    private static List<MonsterLore> monsterLore;

    private static final List<TrapKind> trapInfo = new ArrayList<>();
    public static List<ObjectKind> objectKinds = new ArrayList<>();

    public static final Chunk cave = new Chunk("Current Level", 0, 0, 0, 0, 0, false, 10, 10, 4, 3, 3, 1, 1, 15);
    public static final Player mainPlayer = new Player();

    /**
     * Searches for a particular monster race by name
     */
    @Nullable
    public static MonsterRace lookupMonsterRace(@NotNull String name) {
        if (monsterRaces == null) {
            String message = "Invalid attempt to access monsterRaces when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return null;
        // return monsterRaces.stream().filter(mr -> name.equals(mr.getName())).findFirst().orElse(null);
    }

    /**
     * Searches for a magic realm based on the magic realm name
     *
     * @param realmName the name of the realm to return
     * @return the MagicRealm with the relevant name or null
     */
    @Nullable
    @CheckReturnValue
    public static MagicRealm lookupRealm(String realmName) {
        if (realms == null) {
            String message = "Invalid attempt to access realms when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return realms.stream()
                .filter(r -> realmName.equals(r.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Searches for a summon based on the summon name
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
     * Find a MonsterBase from its code name
     *
     * @param name the code name of the monster base we are searching for
     * @return either the MonsterBase with the associated code name, or null
     */
    @Nullable
    @CheckReturnValue
    public static MonsterBase getMonsterBase(@NotNull String name) {
        if (monsterBases == null) {
            throw new IllegalStateException("MonsterBase has not been initialized yet");
        }

        return monsterBases.stream()
                .filter(e -> e.getCodeName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a monster base by its code name.
     *
     * @param name the monster base code name
     * @return the matching {@link MonsterBase}, or {@code null} if none matches
     * @throws IllegalStateException if monster bases have not been loaded
     * @author ClaudeCode
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
     * Get an ObjectKind based on its name
     *
     * @param name the name of the object kind we are searching for
     * @return the object kind with that name or null if it doesn't
     * exist
     */
    @Nullable
    public static ObjectKind lookupObjectKind(@NotNull String name) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectKinds.stream()
                .filter(e -> name.equals(e.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Look up an object kind by both its tval (type) and name.
     *
     * @param tval the object type value
     * @param name the object kind name
     * @return the matching {@link ObjectKind}, or {@code null} if none matches
     * @throws IllegalStateException if object kinds have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static ObjectKind lookupObjectKind(@NotNull TValue tval, @NotNull String name) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectKinds.stream()
                .filter(e -> name.equals(e.getName()) && tval.equals(e.gettValue()))
                .findFirst().orElse(null);
    }

    /**
     * Search through the slays to get a slay with the same code as
     * the incoming parameter
     *
     * @param slayName the name/code of the slay to find
     * @return A slay where the code name is the same as the incoming
     * parameter, or null
     */
    @Nullable
    @CheckReturnValue
    public static Slay lookupSlay(String slayName) {
        if (slays == null) {
            String message = "Invalid attempt to access slays when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return slays.stream()
                .filter(s -> s.getCode().equals(slayName))
                .findFirst().orElse(null);
    }

    /**
     * Locate a cruse by its name
     *
     * @param curseName the name of the curse we are looking for
     * @return The curse with the relevant name or null
     */
    @Nullable
    @CheckReturnValue
    public static Curse lookupCurse(String curseName) {
        if (curses == null) {
            String message = "Invalid attempt to access curses when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return curses.stream()
                .filter(c -> c.getName().equals(curseName))
                .findFirst().orElse(null);
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
        if (monsterType <= 0 || monsterType > 12)
            return null;

        return monsterPains.stream()
                .filter(e -> e.getPainIndex() == monsterType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a terrain feature by its terrain code.
     *
     * @param flag the terrain code
     * @return the matching {@link Feature}, or {@code null} if none matches
     * @throws IllegalStateException if features have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static Feature lookupFeature(@NotNull TerrainFlags flag) {
        if (features == null) {
            String message = "Invalid attempt to access features when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return features.stream().filter(f -> flag.equals(f.getTerrainFlag()))
                .findFirst().orElse(null);
    }

    /**
     * Look up a player history chart by its chart number.
     *
     * @param chartId the chart number
     * @return the matching {@link PlayerHistoryChart}, or {@code null} if none matches
     * @throws IllegalStateException if history charts have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static PlayerHistoryChart lookupPlayerHistoryChart(int chartId) {
        if (playerHistoryCharts == null) {
            String message = "Invalid attempt to access playerHistoryCharts when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerHistoryCharts.stream().filter(c -> c.getChartNumber() == chartId)
                .findFirst().orElse(null);
    }

    /**
     * Look up a trap kind by its description, trying an exact match first and
     * then a case-insensitive match.
     *
     * @param description the trap description
     * @return the matching {@link TrapKind}, or {@code null} if none matches
     * @throws IllegalStateException if trap info has not been loaded
     * @author ClaudeCode
     */
    @CheckReturnValue
    public static @Nullable TrapKind lookupTrap(@NotNull String description) {
        if (trapInfo.isEmpty()) {
            String message = "Invalid attempt to access trapInfo when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        for (TrapKind trap : trapInfo) {
            if (trap.getDescription().equals(description)) {
                return trap;
            }
        }

        // check for a close match as we can't find an exact one. Close matches are ones where the cases don't match
        // but the characters do.
        for (TrapKind trap : trapInfo) {
            if (trap.getDescription().equalsIgnoreCase(description)) {
                return trap;
            }
        }

        // not found - return null
        return null;
    }

    /**
     * Add a new object kind to the list of object kinds
     *
     * @param toAdd the ObjectKind to add
     */
    public static void addObjectKind(ObjectKind toAdd) {
        objectKinds.add(toAdd);
    }

    /**
     * Get the ObjectBase which has a given string as its name
     *
     * @param name the name we are searching the object base list for
     * @return the object base with given name or null
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase lookupObjectBase(@NotNull String name) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(o -> name.equals(o.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the ObjectBase which has a given string as its name and TValue as its TValue
     *
     * @param name   the name we are searching the object base list for
     * @param tValue the TValue we are searching for
     * @return the object base with given name or null
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase lookupObjectBase(@NotNull String name, @NotNull TValue tValue) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(o -> (name.equals(o.getName()) && tValue == o.gettVal()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lookup an item base based on its tval and return the first base found
     *
     * @param tVal The tval we are looking for
     * @return the first item base with that tVal
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase getBaseFromTVal(@NotNull TValue tVal) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(ob -> tVal.equals(ob.gettVal()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find a monster base by its code name (linear scan).
     *
     * @param name the monster base code name
     * @return the matching {@link MonsterBase}, or {@code null} if none matches
     * @author ClaudeCode
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
     * Find a monster pain record by its index (linear scan).
     *
     * @param index the pain-record index
     * @return the matching {@link MonsterPain}, or {@code null} if none matches
     * @author ClaudeCode
     */
    public static @Nullable MonsterPain getPainFromIndex(int index) {
        for (MonsterPain monsterPain : monsterPains) {
            if (monsterPain.getPainIndex() == index) {
                return monsterPain;
            }
        }

        return null;
    }

    /**
     * Return a UIEntryRenderer from the arraylist of all renderers by its name
     *
     * @param name the name of the renderer we wish to obtain
     * @return a reference to the renderer with the same name as the incoming parameter
     */
    @Nullable
    @Contract("_ -> _")
    public static UIEntryRenderer getUIEntryRenderer(@NotNull String name) {
        if (uiEntryRenderers == null) {
            String message = "Invalid attempt to access uiEntryRenderers when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return uiEntryRenderers.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Return a UIEntryBase from the arrayList of all bases by its name
     *
     * @param name the name of the base we are wanting to find
     * @return a reference to the base with the name equal to the incoming parameter, or null if no base is found
     * with that name
     */
    @Nullable
    @Contract("_ -> _")
    public static UIEntryBase getUIEntryBase(@NotNull String name) {
        if (uiEntryBases == null) {
            String message = "Invalid attempt to access UIEntryBase when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return uiEntryBases.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a UIEntry from a string
     *
     * @param name the string representation of the UIEntry's name
     * @return a UIEntry
     */
    @Nullable
    @Contract("_ -> _")
    public static UIEntry getUIEntry(String name) {
        if (uiEntries == null) {
            String message = "Invalid attempt to access UIEntry when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return uiEntries.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Initialize the game objects in the correct order
     */
    public static void init() {
        try {
            loadGameConstants();
//            loadWorld();                // World arraylist determines maxRandDepth
//            loadProjections();
//            loadUIEntryRenderers();
//            loadUIEntryBases();         // Dependent on UIEntyRenderers
//            loadUIEntries();            // Dependent on UIEntryBase & UIEntryRenderers
//            loadPlayerProperties();     // Dependent on UIEntry
//            loadTerrainFeatures();
//            loadObjectBases();
//            loadPain();
//            loadMonsterBases();         // Dependent on MonsterPain
//            loadSlays();                // Dependent on MonsterBases
//            loadBrands();
//            loadSummons();              // Dependent on MonsterBases
//            checkSummons();             // Check that the summons list correctly handles the fallback strings
//            loadCurses();               // Dependent on ObjectBases
//            loadPlayerShapes();
//            loadItemObjects();          // Dependent on Summons, Curse, Slay & ObjectBase
            loadActivations();
//            loadEgoItems();             // Dependent on Activations, Brand, Slay & Curse
//            loadPlayerHistories();
//            loadBodies();
//            loadPlayerRaces();          // Dependent on PlayerBodies & PlayerHistories
//            loadMagicRealms();
//            loadPlayerClasses();        // Dependent on ItemObjects, Summons, MagicRealms
//            loadArtifacts();            // Dependent on Activations, ObjectKind, Brand, Slay & Curse
//            loadObjectProperties();     // Dependent on UIEntry
//            loadPlayerTimedProperties();
//            loadBlowMethods();
//            loadBlowEffects();
//            loadMonsterSpellTypes();
//            loadVisualCyclerTable();
//            loadMonsters();             // Dependent on MonsterBase, VisualsCyclerTable, BlowMethods & VisualColours
//            loadPitProfiles();          // Dependent on Monsters, MonsterBase & MonsterSpellTypes
//            loadMonsterLore();          // Dependent on MonsterKind, MonsterBase & ObjectKind (amongst others)
        } catch (Exception e) {
            String message = "Unable to load data from " + ANGBAND_DIR_GAMEDATA + " error message: " + e.getMessage();
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private static void loadMonsterLore() {
        LoreReader loreReader = new LoreReader();
        String filename = ANGBAND_DIR_USER + "lore.txt";

        try {
            monsterLore = loreReader.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadPitProfiles() {
        PitReader parser = new PitReader();
        String filename = ANGBAND_DIR_GAMEDATA + "pit.txt";

        try {
            monsterPitProfiles = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Look up a projection by its lash-form description.
     *
     * @param lashType the lash description
     * @return the matching {@link Projection}, or {@code null} if none matches
     * @throws IllegalStateException if projections have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static Projection lookupProjectionByLash(String lashType) {
        if (projections == null) {
            String message = "Invalid attempt to access projections when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return projections.stream().filter(p -> lashType.equals(p.getLashDescription()))
                .findFirst()
                .orElse(null);
    }

    private static void loadMonsters() {
        MonsterReader parser = new MonsterReader();
        String filename = ANGBAND_DIR_GAMEDATA + "monster.txt";

        try {
            monsterRaces = parser.parse(filename);
            for (MonsterRace race : monsterRaces) {
                if (!(race == null))
                    race.setFriends();
            }
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadVisualCyclerTable() {
        VisualsReader parser = new VisualsReader();
        String filename = ANGBAND_DIR_GAMEDATA + "visuals.txt";

        try {
            visualsCyclerTable = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Look up a monster blow effect by name.
     *
     * @param effectName the blow effect name
     * @return the matching {@link BlowEffect}, or {@code null} if none matches
     * @throws IllegalStateException if blow effects have not been loaded
     * @author ClaudeCode
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
     * @author ClaudeCode
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
     * @return the loaded colour-cycling table
     * @author ClaudeCode
     */
    public static VisualsCycler getVisualsCyclerTable() {
        return visualsCyclerTable;
    }

    private static void loadMonsterSpellTypes() {
        MonsterSpellReader parser = new MonsterSpellReader();
        String filename = ANGBAND_DIR_GAMEDATA + "monster_spell.txt";

        try {
            monsterSpellTypes = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadBlowEffects() {
        BlowEffectReader parser = new BlowEffectReader();
        String filename = ANGBAND_DIR_GAMEDATA + "blow_effects.txt";

        try {
            blowEffects = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadBlowMethods() {
        BlowMethodReader parser = new BlowMethodReader();
        String filename = ANGBAND_DIR_GAMEDATA + "blow_methods.txt";

        try {
            blowMethods = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadPlayerTimedProperties() {
        PlayerTimedReader parser = new PlayerTimedReader();
        String filename = ANGBAND_DIR_GAMEDATA + "player_timed.txt";

        try {
            playerTimedEffects = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadObjectProperties() {
        ObjectPropertyReader parser = new ObjectPropertyReader();
        String filename = ANGBAND_DIR_GAMEDATA + "object_property.txt";

        try {
            objectProperties = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadArtifacts() {
        ArtifactReader parser = new ArtifactReader();
        String filename = ANGBAND_DIR_GAMEDATA + "artifact.txt";

        try {
            artifacts = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadPlayerClasses() {
        PlayerClassReader parser = new PlayerClassReader();
        String filename = ANGBAND_DIR_GAMEDATA + "class.txt";

        try {
            playerClasses = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadMagicRealms() {
        RealmReader parser = new RealmReader();
        String filename = ANGBAND_DIR_GAMEDATA + "realm.txt";

        try {
            realms = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    private static void loadPlayerRaces() {
        PlayerRaceReader parser = new PlayerRaceReader();
        String filename = ANGBAND_DIR_GAMEDATA + "p_race.txt";

        try {
            playerRaces = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Look up a player body type by its count/index.
     *
     * @param number the body's count value
     * @return the matching {@link PlayerBody}, or {@code null} if none matches
     * @throws IllegalStateException if player bodies have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static PlayerBody lookupPlayerBody(int number) {
        if (playerBodies == null) {
            String message = "Invalid attempt to access playerBodies when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerBodies.stream()
                .filter(b -> number == b.getCount())
                .findFirst()
                .orElse(null);
    }

    private static void loadBodies() {
        BodyReader reader = new BodyReader();
        String filename = ANGBAND_DIR_GAMEDATA + "body.txt";

        try {
            playerBodies = reader.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the player histories into the relevant List
     */
    private static void loadPlayerHistories() {
        HistoryReader reader = new HistoryReader();
        String filename = ANGBAND_DIR_GAMEDATA + "history.txt";

        try {
            playerHistoryCharts = reader.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the ego items into the relevant list
     */
    private static void loadEgoItems() {
        EgoItemReader reader = new EgoItemReader();
        String filename = ANGBAND_DIR_GAMEDATA + "ego_item.txt";

        try {
            egoItems = reader.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Return a brand based on the brand name
     *
     * @param name the name of the brand to return
     * @return the brand or null if it isn't found
     */
    @Nullable
    public static Brand lookupBrandCode(@NotNull String name) {
        if (brands == null) {
            String message = "Invalid attempt to access brands when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return brands.stream().filter(b -> name.equals(b.getCode()))
                .findFirst().orElse(null);
    }

    /**
     * Get an activation by its name
     *
     * @param name The name of the activation we are searching for
     * @return the Activation in the List activations with the name equal to the incoming parameter
     */
    @Nullable
    public static Activation lookupActivation(@NotNull String name) {
        if (activations == null) {
            String message = "Invalid attempt to access activations when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return activations.stream().filter(e -> name.equals(e.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Load in the activations from activation.txt and store them in a List
     */
    private static void loadActivations() {
        ActivationReader reader = new ActivationReader();
        String filename = ANGBAND_DIR_GAMEDATA + "activation.txt";

        try {
            activations = reader.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the items from object.txt and store them in a List
     */
    private static void loadItemObjects() {
        ItemObjectReader parser = new ItemObjectReader();
        String filename = ANGBAND_DIR_GAMEDATA + "object.txt";

        try {
            itemObjects = parser.parse(filename);
            objectKinds = parser.parseKinds(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Check the summons list to ensure that all fallback positions are correctly stored
     *
     * @throws IllegalStateException There was no entry where the fallback of one summon was the name of another.
     */
    private static void checkSummons() throws IllegalStateException {
        for (Summon summon : summons) {
            String fallBack = summon.getFallback();
            if (!fallBack.isEmpty()) {
                if (summons.stream().noneMatch(e -> e.getName().equals(fallBack))) {
                    String errorMessage = "No fallback record of " + fallBack + " found for " + summon.getName();
                    throw new IllegalStateException(errorMessage);
                }
            }
        }
    }

    /**
     * Look up a player shape by name.
     *
     * @param name the shape name
     * @return the matching {@link PlayerShape}, or {@code null} if none matches
     * @throws IllegalStateException if player shapes have not been loaded
     * @author ClaudeCode
     */
    @Nullable
    public static PlayerShape lookupPlayerShape(@NotNull String name) {
        if (playerShapes == null) {
            String message = "Invalid attempt to access playerShapes when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerShapes.stream().filter(s -> name.equals(s.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Load in the PlayerShape information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadPlayerShapes() {
        ShapeReader parser = new ShapeReader();
        String filename = ANGBAND_DIR_GAMEDATA + "shape.txt";

        try {
            playerShapes = parser.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the Curses information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadCurses() throws IOException {
        CurseReader curseReader = new CurseReader();
        String filename = ANGBAND_DIR_GAMEDATA + "curse.txt";

        try {
            curses = curseReader.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Summon information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadSummons() throws IOException {
        SummonReader parser = new SummonReader();
        String filename = ANGBAND_DIR_GAMEDATA + "summon.txt";

        try {
            summons = parser.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the MonsterBase information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadMonsterBases() throws IOException {
        MonsterBaseReader parser = new MonsterBaseReader();
        String filename = ANGBAND_DIR_GAMEDATA + "monster_base.txt";

        try {
            monsterBases = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Pain information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadPain() throws IOException {
        PainReader parser = new PainReader();
        String filename = ANGBAND_DIR_GAMEDATA + "pain.txt";

        try {
            monsterPains = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Brand information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadBrands() throws IOException {
        BrandReader parser = new BrandReader();
        String filename = ANGBAND_DIR_GAMEDATA + "brand.txt";

        try {
            brands = parser.parse(filename);
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Slays information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadSlays() {
        SlayReader parser = new SlayReader();
        String filename = ANGBAND_DIR_GAMEDATA + "slay.txt";

        try {
            slays = parser.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the ObjectBase information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadObjectBases() throws IOException {
        ObjectBaseReader objectBaseParser = new ObjectBaseReader();
        String filename = ANGBAND_DIR_GAMEDATA + "object_base.txt";

        try {
            objectBases = objectBaseParser.parse(filename);
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Terrain Feature information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadTerrainFeatures() throws IOException {
        TerrainReader parser = new TerrainReader();
        String filename = ANGBAND_DIR_GAMEDATA + "terrain.txt";

        try {
            features = parser.parse(filename);
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
    private static void loadPlayerProperties() throws IOException {
        PlayerPropertyReader parser = new PlayerPropertyReader();
        String filename = GameConstants.ANGBAND_DIR_GAMEDATA + "player_property.txt";

        try {
            playerProperties = parser.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the UI ENtry information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadUIEntries() throws IOException {
        UIEntryReader parser = new UIEntryReader();
        String filename = ANGBAND_DIR_GAMEDATA + "ui_entry.txt";

        try {
            uiEntries = parser.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the UI Bases information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    private static void loadUIEntryBases() throws IOException {
        UIEntryBaseReader uiEntryBaseReader = new UIEntryBaseReader();
        String filename = ANGBAND_DIR_GAMEDATA + "ui_entry_base.txt";

        try {
            uiEntryBases = uiEntryBaseReader.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the UI Entry Renderer information and store it in a List.
     *
     * @throws IOException an error occurred during the parsing - log it and rethrow it
     */
    private static void loadUIEntryRenderers() throws IOException {
        UIEntryRendererReader renderer = new UIEntryRendererReader();
        String filename = ANGBAND_DIR_GAMEDATA + "ui_entry_renderer.txt";

        try {
            uiEntryRenderers = renderer.parse(filename);
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the various projection types and store them in a List
     * @throws IOException an error occurred during the parsing - log it and rethrow it
     */
    private static void loadProjections() throws IOException {
        ProjectionReader projectionParser = new ProjectionReader();
        String filename = ANGBAND_DIR_GAMEDATA + "projection.txt";

        try {
            projections = projectionParser.parse(filename);
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

//    /**
//     * Load in the different levels available in the world and store them in a List
//     * @throws IOException an error occurred during the parsing - log it and rethrow it
//     */
//    private static void loadWorld() throws IOException {
//        WorldReader worldReader = new WorldReader();
//        String filename = ANGBAND_DIR_GAMEDATA + "world.txt";
//
//        try {
//            worlds = toWorlds(worldReader.parse(filename));
//        } catch (IOException ex) {
//            logger.error("Error while loading file {}", filename, ex);
//            throw ex;
//        }
//
//        maxRandDepth = worlds.size();
//    }

    /**
     * Private constructor preventing instantiation of this static-only registry.
     *
     * @author ClaudeCode
     */
    @Contract(pure = true)
    private GameConstants() {
    }

    /**
     * Loads the contents of the file constants.txt in the lib\gamedata directory and splits the values out
     * into the various GameConstants values.
     */
//    private static void loadGameConstants() throws IOException {
//        GameConstantsReader reader = new GameConstantsReader();
//        String filename = ANGBAND_DIR_GAMEDATA + "constants.txt";
//        List<Entry> keyValues = toEntries(reader.parse(filename));
//
//        for (Entry entry : keyValues) {
//            switch (entry.key()) {
//                case "level-max" -> setLevelMax(entry);
//                case "mon-gen" -> setMonGen(entry);
//                case "mon-play" -> setMonPlay(entry);
//                case "dun-gen" -> setDunGen(entry);
//                case "world" -> setWorld(entry);
//                case "carry-cap" -> setCarryCap(entry);
//                case "store" -> setStoreParameters(entry);
//                case "obj-make" -> setObjectCreation(entry);
//                case "player" -> setPlayerConstants(entry);
//                case "melee-critical" -> setNonOMeleeCrits(entry);
//                case "melee-critical-level" -> setNonOMeleeCriticalLevels(entry);
//                case "ranged-critical" -> setNonORangedCrits(entry);
//                case "ranged-critical-level" -> setNonORangedCriticalLevels(entry);
//                case "o-melee-critical" -> setOMeleeCrits(entry);
//                case "o-melee-critical-level" -> setOMeleeCriticalLevels(entry);
//                case "o-ranged-critical" -> setORangedCrits(entry);
//                case "o-ranged-critical-level" -> setORangedCriticalLevels(entry);
//
//                default -> {
//                    String message = "Invalid token found while parsing file " + filename + ". Tokens were: " + entry.key() + ":" + entry.value();
//                    InvalidTokenFoundDuringParse ex = new InvalidTokenFoundDuringParse(message);
//                    logger.error(message, ex);
//                    throw ex;
//                }
//            }
//        }
//    }


//    /**
//     * Deals with the O Ranged Critical levels from the constants.txt file
//     *
//     * @param entry the key value pair from the ORanged section of the constants.txt file
//     */
//    private static void setORangedCriticalLevels(@NotNull Entry entry) {
//        String tag = entry.key() + ":";
//
//        String[] values = entry.value().split(":");
//
//        if (values.length != 3) {
//            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
//            logger.error(message);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        int chance;
//        int dice;
//        MessageType message;
//
//        try {
//            chance = Integer.parseInt(values[0]);
//            dice = Integer.parseInt(values[1]);
//            message = MessageType.valueOf("MSG_" + values[2]);
//        } catch (NumberFormatException e) {
//            String errorMessage = "Invalid number format found. Tokens were: " + tag + entry.value();
//            logger.error(errorMessage);
//            throw new InvalidTokenFoundDuringParse(errorMessage);
//        } catch (IllegalArgumentException e) {
//            String errorMessage = "Invalid message found. Tokens were: " + tag + entry.value();
//            logger.error(errorMessage);
//            throw new InvalidTokenFoundDuringParse(errorMessage);
//        }
//
//        rOCriticalLevels.add(new O_CriticalLevel(chance, dice, message));
//    }
//
//    /**
//     * Deals with an O Ranged Crit record from the constants.txt file
//     *
//     * @param entry the record to examine
//     */
//    private static void setORangedCrits(@NotNull Entry entry) {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "debuff-toh":
//                oRCritDebuffToh = val;
//                break;
//
//            case "power-launched-toh-scale-numerator":
//                oRCritPowerLaunchedTohSclNum = val;
//                break;
//
//            case "power-launched-toh-scale-denominator":
//                oRCritPowerLaunchedTohSclDen = val;
//                break;
//
//            case "power-thrown-toh-scale-numerator":
//                oRCritPowerThrownTohSclNum = val;
//                break;
//
//            case "power-thrown-toh-scale-denominator":
//                oRCritPowerThrownTohSclDen = val;
//                break;
//
//            case "chance-power-scale-numerator":
//                oRCritChancePowerSclNum = val;
//                break;
//
//            case "chance-power-scale-denominator":
//                oRCritChancePowerSclDen = val;
//                break;
//
//            case "chance-add-denominator":
//                oRCritChanceAddDen = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deals with an O Melee Critical Level record from the constants.txt file
//     *
//     * @param entry the record to deal with
//     */
//    private static void setOMeleeCriticalLevels(@NotNull Entry entry) {
//        String tag = entry.key() + ":";
//        String[] values = entry.value().split(":");
//
//        if (values.length != 3) {
//            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
//            logger.error(message);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        int chance;
//        int dice;
//        MessageType messageName;
//
//        try {
//            chance = Integer.parseInt(values[0]);
//            dice = Integer.parseInt(values[1]);
//            messageName = MessageType.valueOf("MSG_" + values[2]);
//        } catch (NumberFormatException e) {
//            String message = "Invalid number format found. Tokens were: " + tag + entry.value();
//            logger.error(message);
//            throw new InvalidTokenFoundDuringParse(message);
//        } catch (IllegalArgumentException e) {
//            String message = "Message flag not found. Tokens were: " + tag + entry.value();
//            logger.error(message, e);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        mOCriticalLevels.add(new O_CriticalLevel(chance, dice, messageName));
//    }
//
//    /**
//     * Deal with an O Melee Crit entry
//     *
//     * @param entry the String key String value record for this entry
//     */
//    private static void setOMeleeCrits(@NotNull Entry entry) {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "debuff-toh":
//                oMCritDebuffToh = val;
//                break;
//
//            case "power-toh-scale-numerator":
//                oMCritPowerTohSclNum = val;
//                break;
//
//            case "power-toh-scale-denominator":
//                oMCritPowerTohSclDen = val;
//                break;
//
//            case "chance-power-scale-numerator":
//                oMCritChancePowerSclNum = val;
//                break;
//
//            case "chance-power-scale-denominator":
//                oMCritChancePowerSclDen = val;
//                break;
//
//            case "chance-add-denominator":
//                oMCritChanceAddDen = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a normal ranged critical level entry
//     *
//     * @param entry the entry
//     */
//    private static void setNonORangedCriticalLevels(@NotNull Entry entry) {
//        String tag = entry.key() + ":";
//        String[] results = entry.value().split(":");
//
//        if (results.length != 4) {
//            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
//            logger.error(message);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        int cutoffPower = 0;
//        int damageMult = 0;
//        int amountAdded = 0;
//        MessageType messageEnum = MessageType.MSG_NONE;
//        String messageEnumString = "";
//
//        try {
//            cutoffPower = Integer.parseInt(results[0]);
//            damageMult = Integer.parseInt(results[1]);
//            amountAdded = Integer.parseInt(results[2]);
//            messageEnumString = results[3];
//            messageEnum = MessageType.valueOf("MSG_" + messageEnumString);
//        } catch (NumberFormatException e) {
//            String message = "Invalid number found. Tokens were: " + tag + entry.value();
//            logger.error(message, e);
//            throw new InvalidTokenFoundDuringParse(message);
//        } catch (IllegalArgumentException e) {
//            String message = "Message flag not found. Tokens were: " + tag + entry.value();
//            logger.error(message, e);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        rCriticalLevels.add(new CriticalLevel(cutoffPower, damageMult, amountAdded, messageEnum));
//    }
//
//    /**
//     * Deal with a normal Ranged critical entry
//     *
//     * @param entry the entry
//     */
//    private static void setNonORangedCrits(@NotNull Entry entry) {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "debuff-toh":
//                rCritDebuffToh = val;
//                break;
//
//            case "chance-weight-scale":
//                rCritChanceWeightScl = val;
//                break;
//
//            case "chance-toh-scale":
//                rCritChanceTohScl = val;
//                break;
//
//            case "chance-level-scale":
//                rCritChanceLevelScl = val;
//                break;
//
//            case "chance-launched-toh-skill-scale":
//                rCritChanceLaunchedTohSkillScl = val;
//                break;
//
//            case "chance-thrown-toh-skill-scale":
//                rCritChanceThrownTohSkillScl = val;
//                break;
//
//            case "chance-offset":
//                rCritChanceOffset = val;
//                break;
//
//            case "chance-range":
//                rCritChanceRange = val;
//                break;
//
//            case "power-weight-scale":
//                rCritPowerWeightScl = val;
//                break;
//
//            case "power-random":
//                rCritPowerRandom = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deals with a normal melee critical level entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an invalid number of tokens were input for this entry
//     */
//    private static void setNonOMeleeCriticalLevels(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        String tag = entry.key() + ":";
//        String[] results = entry.value().split(":");
//
//        if (results.length != 4) {
//            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
//            logger.error(message);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        int cutoffPower;
//        int damageMult;
//        int amountAdded;
//        MessageType messageEnum;
//        String messageEnumString;
//
//        try {
//            cutoffPower = Integer.parseInt(results[0]);
//            damageMult = Integer.parseInt(results[1]);
//            amountAdded = Integer.parseInt(results[2]);
//            messageEnumString = results[3];
//            messageEnum = MessageType.valueOf("MSG_" + messageEnumString);
//        } catch (NumberFormatException e) {
//            String message = "Invalid number found. Tokens were: " + tag + entry.value();
//            logger.error(message, e);
//            throw new InvalidTokenFoundDuringParse(message);
//        } catch (IllegalArgumentException e) {
//            String message = "Message flag not found. Tokens were: " + tag + entry.value();
//            logger.error(message, e);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//
//        mCriticalLevels.add(new CriticalLevel(cutoffPower, damageMult, amountAdded, messageEnum));
//    }
//
//    /**
//     * Deal with a normal melee critical entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse a melee critical entry with an unknown key was found
//     */
//    private static void setNonOMeleeCrits(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "debuff-toh":
//                mCritDebuffToh = val;
//                break;
//
//            case "chance-weight-scale":
//                mCritChanceWeightScl = val;
//                break;
//
//            case "chance-toh-scale":
//                mCritChanceTohScl = val;
//                break;
//
//            case "chance-level-scale":
//                mCritChanceLevelScl = val;
//                break;
//
//            case "chance-toh-skill-scale":
//                mCritChanceTohSkillScl = val;
//                break;
//
//            case "chance-offset":
//                mCritChanceOffset = val;
//                break;
//
//            case "chance-range":
//                mCritChanceRange = val;
//                break;
//
//            case "power-weight-scale":
//                mCritPowerWeightScl = val;
//                break;
//
//            case "power-random":
//                mCritPowerRandom = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a Player Constant entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an invalid key found
//     */
//    private static void setPlayerConstants(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "max-sight":
//                maxSight = val;
//                break;
//
//            case "max-range":
//                maxRange = val;
//                break;
//
//            case "start-gold":
//                startGold = val;
//                break;
//
//            case "food-value":
//                foodValue = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a Object Creation entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an unknown key found
//     * @throws NumberFormatException        a badly formatted integer found
//     */
//    private static void setObjectCreation(@NotNull Entry entry) throws InvalidTokenFoundDuringParse, NumberFormatException {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "max-depth":
//                maxObjDepth = val;
//                break;
//
//            case "great-obj":
//                greatObj = val;
//                break;
//
//            case "great-ego":
//                greatEgo = val;
//                break;
//
//            case "fuel-torch":
//                fuelTorch = val;
//                break;
//
//            case "fuel-lamp":
//                fuelLamp = val;
//                break;
//
//            case "default-lamp":
//                defaultLamp = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a Store Parameter entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an unknown key found
//     */
//    private static void setStoreParameters(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "inven-max":
//                storeInvenMax = val;
//                break;
//
//            case "turns":
//                storeTurns = val;
//                break;
//
//            case "shuffle":
//                storeShuffle = val;
//                break;
//
//            case "magic-level":
//                storeMagicLevel = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a Carry Cap entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an unknown entry.key found
//     */
//    private static void setCarryCap(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "pack-size":
//                packSize = val;
//                break;
//
//            case "quiver-size":
//                quiverSize = val;
//                break;
//
//            case "quiver-slot-size":
//                quiverSlotSize = val;
//                break;
//
//            case "thrown-quiver-mult":
//                thrownQuiverMult = val;
//                break;
//
//            case "floor-size":
//                floorSize = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a world constant entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an unknown entry.key found
//     */
//    private static void setWorld(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "max-depth":
//                maxDepth = val;
//                break;
//
//            case "day-length":
//                dayLength = val;
//                break;
//
//            case "dungeon-hgt":
//                dungeonHeight = val;
//                break;
//
//            case "dungeon-wid":
//                dungeonWidth = val;
//                break;
//
//            case "town-hgt":
//                townHeight = val;
//                break;
//
//            case "town-wid":
//                townWidth = val;
//                break;
//
//            case "feeling-total":
//                feelingTotal = val;
//                break;
//
//            case "feeling-need":
//                feelingNeed = val;
//                break;
//
//            case "stair-skip":
//                stairSkip = val;
//                break;
//
//            case "move-energy":
//                moveEnergy = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a dungeon generation constant entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse invalid entry.key found
//     */
//    private static void setDunGen(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "cent-max":
//                levelRoomMax = val;
//                break;
//
//            case "door-max":
//                levelDoorMax = val;
//                break;
//
//            case "wall-max":
//                wallPierceMax = val;
//                break;
//
//            case "tunn-max":
//                tunnGridMax = val;
//                break;
//
//            case "amt-room":
//                roomItemAv = val;
//                break;
//
//            case "amt-item":
//                bothItemAv = val;
//                break;
//
//            case "amt-gold":
//                bothGoldAv = val;
//                break;
//
//            case "pit-max":
//                levelPitMax = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a monster entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse unknown entry.key found
//     */
//    private static void setMonPlay(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "break-glyph":
//                glyphHardness = val;
//                break;
//
//            case "mult-rate":
//                reproMonsterRate = val;
//                break;
//
//            case "life-drain":
//                lifeDrainPercent = val;
//                break;
//
//            case "flee-range":
//                fleeRange = val;
//                break;
//
//            case "turn-range":
//                turnRange = val;
//                break;
//
//            default:
//                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
//                logger.error(message);
//                throw new InvalidTokenFoundDuringParse(message);
//        }
//    }
//
//    /**
//     * Deal with a monster generation entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse an unknown entry.key found
//     */
//    private static void setMonGen(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        switch (name) {
//            case "chance":
//                allocMonsterChance = val;
//                break;
//
//            case "level-min":
//                levelMonsterMin = val;
//                break;
//
//            case "town-day":
//                townMonstersDay = val;
//                break;
//
//            case "town-night":
//                townMonstersNight = val;
//                break;
//
//            case "repro-max":
//                reproMonstersNight = val;
//                break;
//
//            case "ood-chance":
//                oodMonsterChance = val;
//                break;
//
//            case "ood-amount":
//                oodMonsterAmount = val;
//                break;
//
//            case "group-max":
//                monsterGroupMax = val;
//                break;
//
//            case "group-dist":
//                monsterGroupDist = val;
//                break;
//
//            default:
//                String msg = "Invalid token found in constants.txt file. Input was " + entry.key() + ":" + entry.value();
//                logger.error(msg);
//                throw new InvalidTokenFoundDuringParse(msg);
//        }
//    }
//
//    /**
//     * Deal with a level maximum entry
//     *
//     * @param entry the entry
//     * @throws InvalidTokenFoundDuringParse An unknown entry.key found
//     */
//    private static void setLevelMax(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
//        NameValuePair pair = getValues(entry.value(), entry.key());
//
//        String name = pair.name();
//        int val = pair.value();
//
//        if (name.equals("monsters")) {
//            levelMonsterMax = val;
//        } else {
//            String message = "Unknown tag found in constants.txt file. Token was " + entry.key() + ":" + entry.value();
//            logger.error(message);
//            throw new InvalidTokenFoundDuringParse(message);
//        }
//    }

    /**
     * Convert a String key, String value record into a String key int value record
     * @param value the String value to convert to a int value
     * @param key the String key to match the new int value against
     * @return A new String key int value pair
     * @throws InvalidTokenFoundDuringParse Either the wrong number of tokens in the value or a badly formatted integer
     * in the value String
     */
    @NotNull
    @Contract("_, _ -> new")
    private static NameValuePair getValues(@NotNull String value, @NotNull String key) throws InvalidTokenFoundDuringParse {
        String tag = key + ":";
        String[] results = value.split(":");

        if (results.length != 2) {
            String message = "Invalid number of arguments found in incoming line from constants.txt. Line was " + key + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val;

        try {
            val = Integer.parseInt(results[1]);
            return new NameValuePair(name, val);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + value;
            logger.error(message, e);
            throw e;
        }
    }

    /**
     * @return the configured value of {@code storeMax}
     * @author ClaudeCode
     */
    public static int getStoreMax() {
        return storeMax;
    }

    /**
     * @return the configured value of {@code trapMax}
     * @author ClaudeCode
     */
    public static int getTrapMax() {
        return trapMax;
    }

    /**
     * @return the configured value of {@code objectBaseKindMax}
     * @author ClaudeCode
     */
    public static int getObjectBaseKindMax() {
        return objectBaseKindMax;
    }

    /**
     * @return the configured value of {@code artifactKindMax}
     * @author ClaudeCode
     */
    public static int getArtifactKindMax() {
        return artifactKindMax;
    }

    /**
     * @return the configured value of {@code egoItemKindMax}
     * @author ClaudeCode
     */
    public static int getEgoItemKindMax() {
        return egoItemKindMax;
    }

    /**
     * @return the configured value of {@code monsterRaceMax}
     * @author ClaudeCode
     */
    public static int getMonsterRaceMax() {
        return monsterRaceMax;
    }

    /**
     * @return the configured value of {@code monsterPainMsgMax}
     * @author ClaudeCode
     */
    public static int getMonsterPainMsgMax() {
        return monsterPainMsgMax;
    }

    /**
     * @return the configured value of {@code magicSpellMax}
     * @author ClaudeCode
     */
    public static int getMagicSpellMax() {
        return magicSpellMax;
    }

    /**
     * @return the configured value of {@code monsterPitTypeMax}
     * @author ClaudeCode
     */
    public static int getMonsterPitTypeMax() {
        return monsterPitTypeMax;
    }

    /**
     * @return the configured value of {@code randartActivationsMax}
     * @author ClaudeCode
     */
    public static int getRandartActivationsMax() {
        return randartActivationsMax;
    }

    /**
     * @return the configured value of {@code curseMax}
     * @author ClaudeCode
     */
    public static int getCurseMax() {
        return curseMax;
    }

    /**
     * @return the configured value of {@code slayMax}
     * @author ClaudeCode
     */
    public static int getSlayMax() {
        return slayMax;
    }

    /**
     * @return the configured value of {@code brandMax}
     * @author ClaudeCode
     */
    public static int getBrandMax() {
        return brandMax;
    }

    /**
     * @return the configured value of {@code monsterBlowsMax}
     * @author ClaudeCode
     */
    public static int getMonsterBlowsMax() {
        return monsterBlowsMax;
    }

    /**
     * @return the configured value of {@code monsterBlowsMethodsMax}
     * @author ClaudeCode
     */
    public static int getMonsterBlowsMethodsMax() {
        return monsterBlowsMethodsMax;
    }

    /**
     * @return the configured value of {@code monsterBlowsEffectsMax}
     * @author ClaudeCode
     */
    public static int getMonsterBlowsEffectsMax() {
        return monsterBlowsEffectsMax;
    }

    /**
     * @return the configured value of {@code playerEquipmentSlotsMax}
     * @author ClaudeCode
     */
    public static int getPlayerEquipmentSlotsMax() {
        return playerEquipmentSlotsMax;
    }

    /**
     * @return the configured value of {@code caveProfileMax}
     * @author ClaudeCode
     */
    public static int getCaveProfileMax() {
        return caveProfileMax;
    }

    /**
     * @return the configured value of {@code questMax}
     * @author ClaudeCode
     */
    public static int getQuestMax() {
        return questMax;
    }

    /**
     * @return the configured value of {@code projectionTypeMax}
     * @author ClaudeCode
     */
    public static int getProjectionTypeMax() {
        return projectionTypeMax;
    }

    /**
     * @return the configured value of {@code objectPowerCalculationMax}
     * @author ClaudeCode
     */
    public static int getObjectPowerCalculationMax() {
        return objectPowerCalculationMax;
    }

    /**
     * @return the configured value of {@code objectPropertyMax}
     * @author ClaudeCode
     */
    public static int getObjectPropertyMax() {
        return objectPropertyMax;
    }

    /**
     * @return the configured value of {@code objectsInObject_txt}
     * @author ClaudeCode
     */
    public static int getObjectsInObject_txt() {
        return objectsInObject_txt;
    }

    /**
     * @return the configured value of {@code playerShapeMax}
     * @author ClaudeCode
     */
    public static int getPlayerShapeMax() {
        return playerShapeMax;
    }


    /**
     * @return the value of {@code level-max:monsters}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getLevelMaxMonsters() {
        return data.levelMax().monsters();
    }

    /**
     * @return the value of {@code mon-gen:chance}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenChance() {
        return data.monGen().chance();
    }

    /**
     * @return the value of {@code mon-gen:level-min}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenLevelMin() {
        return data.monGen().levelMin();
    }

    /**
     * @return the value of {@code mon-gen:town-day}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenTownDay() {
        return data.monGen().townDay();
    }

    /**
     * @return the value of {@code mon-gen:town-night}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenTownNight() {
        return data.monGen().townNight();
    }

    /**
     * @return the value of {@code mon-gen:repro-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenReproMax() {
        return data.monGen().reproMax();
    }

    /**
     * @return the value of {@code mon-gen:ood-chance}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenOodChance() {
        return data.monGen().oodChance();
    }

    /**
     * @return the value of {@code mon-gen:ood-amount}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenOodAmount() {
        return data.monGen().oodAmount();
    }

    /**
     * @return the value of {@code mon-gen:group-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenGroupMax() {
        return data.monGen().groupMax();
    }

    /**
     * @return the value of {@code mon-gen:group-dist}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonGenGroupDist() {
        return data.monGen().groupDist();
    }

    /**
     * @return the value of {@code mon-play:break-glyph}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayBreakGlyph() {
        return data.monPlay().breakGlyph();
    }

    /**
     * @return the value of {@code mon-play:mult-rate}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayMultRate() {
        return data.monPlay().multRate();
    }

    /**
     * @return the value of {@code mon-play:life-drain}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayLifeDrain() {
        return data.monPlay().lifeDrain();
    }

    /**
     * @return the value of {@code mon-play:flee-range}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayFleeRange() {
        return data.monPlay().fleeRange();
    }

    /**
     * @return the value of {@code mon-play:turn-range}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMonPlayTurnRange() {
        return data.monPlay().turnRange();
    }

    /**
     * @return the value of {@code dun-gen:cent-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenCentMax() {
        return data.dunGen().centMax();
    }

    /**
     * @return the value of {@code dun-gen:door-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenDoorMax() {
        return data.dunGen().doorMax();
    }

    /**
     * @return the value of {@code dun-gen:wall-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenWallMax() {
        return data.dunGen().wallMax();
    }

    /**
     * @return the value of {@code dun-gen:tunn-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenTunnMax() {
        return data.dunGen().tunnMax();
    }

    /**
     * @return the value of {@code dun-gen:amt-room}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtRoom() {
        return data.dunGen().amtRoom();
    }

    /**
     * @return the value of {@code dun-gen:amt-item}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtItem() {
        return data.dunGen().amtItem();
    }

    /**
     * @return the value of {@code dun-gen:amt-gold}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenAmtGold() {
        return data.dunGen().amtGold();
    }

    /**
     * @return the value of {@code dun-gen:pit-max}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDunGenPitMax() {
        return data.dunGen().pitMax();
    }

    /**
     * @return the value of {@code world:max-depth}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldMaxDepth() {
        return data.world().maxDepth();
    }

    /**
     * @return the value of {@code world:day-length}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDayLength() {
        return data.world().dayLength();
    }

    /**
     * @return the value of {@code world:dungeon-hgt}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDungeonHgt() {
        return data.world().dungeonHgt();
    }

    /**
     * @return the value of {@code world:dungeon-wid}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldDungeonWid() {
        return data.world().dungeonWid();
    }

    /**
     * @return the value of {@code world:town-hgt}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldTownHgt() {
        return data.world().townHgt();
    }

    /**
     * @return the value of {@code world:town-wid}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldTownWid() {
        return data.world().townWid();
    }

    /**
     * @return the value of {@code world:feeling-total}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldFeelingTotal() {
        return data.world().feelingTotal();
    }

    /**
     * @return the value of {@code world:feeling-need}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldFeelingNeed() {
        return data.world().feelingNeed();
    }

    /**
     * @return the value of {@code world:stair-skip}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldStairSkip() {
        return data.world().stairSkip();
    }

    /**
     * @return the value of {@code world:move-energy}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWorldMoveEnergy() {
        return data.world().moveEnergy();
    }

    /**
     * @return the value of {@code carry-cap:pack-size}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapPackSize() {
        return data.carryCap().packSize();
    }

    /**
     * @return the value of {@code carry-cap:quiver-size}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapQuiverSize() {
        return data.carryCap().quiverSize();
    }

    /**
     * @return the value of {@code carry-cap:quiver-slot-size}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapQuiverSlotSize() {
        return data.carryCap().quiverSlotSize();
    }

    /**
     * @return the value of {@code carry-cap:thrown-quiver-mult}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapThrownQuiverMult() {
        return data.carryCap().thrownQuiverMult();
    }

    /**
     * @return the value of {@code carry-cap:floor-size}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCarryCapFloorSize() {
        return data.carryCap().floorSize();
    }

    /**
     * @return the value of {@code store:magic-level}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreInvenMax() {
        return data.store().invenMax();
    }

    /**
     * @return the value of {@code store:turns}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreTurns() {
        return data.store().turns();
    }

    /**
     * @return the value of {@code store:shuffle}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreShuffle() {
        return data.store().shuffle();
    }

    /**
     * @return the value of {@code store:magic-level}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStoreMagicLevel() {
        return data.store().magicLevel();
    }

    /**
     * @return the value of {@code obj-make:max-depth}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeMaxDepth() {
        return data.objMake().maxDepth();
    }

    /**
     * @return the value of {@code obj-make:great-obj}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeGreatObj() {
        return data.objMake().greatObj();
    }

    /**
     * @return the value of {@code obj-make:great-ego}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeGreatEgo() {
        return data.objMake().greatEgo();
    }

    /**
     * @return the value of {@code obj-make:fuel-torch}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeFuelTorch() {
        return data.objMake().fuelTorch();
    }

    /**
     * @return the value of {@code obj-make:fuel-lamp}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeFuelLamp() {
        return data.objMake().fuelLamp();
    }

    /**
     * @return the value of {@code obj-make:default-lamp}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectMakeDefaultLamp() {
        return data.objMake().defaultLamp();
    }

    /**
     * @return the value of {@code player:max-sight}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerMaxSight() {
        return data.player().maxSight();
    }

    /**
     * @return the value of {@code player:max-range}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerMaxRange() {
        return data.player().maxRange();
    }

    /**
     * @return the value of {@code player:start-gold}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerStartGold() {
        return data.player().startGold();
    }

    /**
     * @return the value of {@code player:food-value}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPlayerFoodValue() {
        return data.player().foodValue();
    }

    /**
     * @return the value of {@code melee-ranged:debuff-toh}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalDebuffToh() {
        return data.meleeCritical().debuffToh();
    }

    /**
     * @return the value of {@code melee-ranged:chance-weight-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceWeightScale() {
        return data.meleeCritical().chanceWeightScale();
    }

    /**
     * @return the value of {@code melee-ranged:chance-toh-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceTohScale() {
        return data.meleeCritical().chanceTohScale();
    }

    /**
     * @return the value of {@code melee-ranged:chance-level-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceLevelScale() {
        return data.meleeCritical().chanceLevelScale();
    }

    /**
     * @return the value of {@code melee-ranged:chance-toh-skill-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceTohSkillScale() {
        return data.meleeCritical().chanceTohSkillScale();
    }
    /**
     * @return the value of {@code melee-ranged:chance-offset}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceOffset() {
        return data.meleeCritical().chanceOffset();
    }

    /**
     * @return the value of {@code melee-ranged:chance-range}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalChanceRange() {
        return data.meleeCritical().chanceRange();
    }

    /**
     * @return the value of {@code melee-ranged:power-weight-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalPowerWeightScale() {
        return data.meleeCritical().powerWeightScale();
    }

    /**
     * @return the value of {@code melee-ranged:power-random}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMeleeCriticalPowerRandom() {
        return data.meleeCritical().powerRandom();
    }

    /**
     * @return the value of {@code ranged-critical:chance-level-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalDebuffToh() {
        return data.rangedCritical().debuffToh();
    }

    /**
     * @return the value of {@code ranged-critical:chance-weight-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceWeightScale() {
        return data.rangedCritical().chanceWeightScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-toh-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceTohScale() {
        return data.rangedCritical().chanceTohScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-level-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceLevelScale() {
        return data.rangedCritical().chanceLevelScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-launched-toh-skill-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceLaunchedTohSkillScale() {
        return data.rangedCritical().chanceLaunchedTohSkillScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-thrown-toh-skill-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceThrownTohSkillScale() {
        return data.rangedCritical().chanceThrownTohSkillScale();
    }

    /**
     * @return the value of {@code ranged-critical:chance-offset}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceOffset() {
        return data.rangedCritical().chanceOffset();
    }


    /**
     * @return the value of {@code ranged-critical:chance-range}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalChanceRange() {
        return data.rangedCritical().chanceRange();
    }

    /**
     * @return the value of {@code ranged-critical:power-weight-scale}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalPowerWeightScale() {
        return data.rangedCritical().powerWeightScale();
    }

    /**
     * @return the value of {@code ranged-critical:power-random}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getRangedCriticalPowerRandom() {
        return data.rangedCritical().powerRandom();
    }

    /**
     * @return the value of {@code o-melee-critical:debuff-toh}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalDebuffToh() {
        return data.oMeleeCritical().debuffToh();
    }

    /**
     * @return the value of {@code o-melee-critical:power-toh-scale-numerator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalPowerTohScaleNumerator() {
        return data.oMeleeCritical().powerTohScaleNumerator();
    }

    /**
     * @return the value of {@code o-melee-critical:power-toh-scale-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalPowerTohScaleDenominator() {
        return data.oMeleeCritical().powerTohScaleDenominator();
    }

    /**
     * @return the value of {@code o-melee-critical:chance-add-numerator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChancePowerScaleNumerator() {
        return data.oMeleeCritical().chancePowerScaleNumerator();
    }

    /**
     * @return the value of {@code o-melee-critical:chance-power-scale-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChancePowerScaleDenominator() {
        return data.oMeleeCritical().chancePowerScaleDenominator();
    }

    /**
     * @return the value of {@code o-melee-critical:chance-add-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getOMeleeCriticalChanceAddDenominator() {
        return data.oMeleeCritical().chanceAddDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-numerator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int getORangedCriticalDebuffToh() {
        return data.oRangedCritical().debuffToh();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-numerator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerLaunchedTohScaleNumerator() {
        return data.oRangedCritical().powerLaunchedTohScaleNumerator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-launched-toh-scale-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerLaunchedTohScaleDenominator() {
        return data.oRangedCritical().powerLaunchedTohScaleDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-thrown-toh-scale-numerator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerThrownTohScaleNumerator() {
        return data.oRangedCritical().powerThrownTohScaleNumerator();
    }

    /**
     * @return the value of {@code o-ranged-critical:power-thrown-toh-scale-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalPowerThrownTohScaleDenominator() {
        return data.oRangedCritical().powerThrownTohScaleDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:chance-power-scale-numerator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChancePowerScaleNumerator() {
        return data.oRangedCritical().chancePowerScaleNumerator();
    }

    /**
     * @return the value of {@code o-ranged-critical:chance-power-scale-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChancePowerScaleDenominator() {
        return data.oRangedCritical().chancePowerScaleDenominator();
    }

    /**
     * @return the value of {@code o-ranged-critical:chance-add-denominator}
     * read from the constants.txt file.
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getORangedCriticalChanceAddDenominator() {
        return data.oRangedCritical().chanceAddDenominator();
    }

    /**
     * @return the configured value of {@code maxRandDepth}
     *
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMaxRandDepth() {
        return maxRandDepth;
    }
}