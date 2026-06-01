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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.game.globals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.numerics.Rational;
import uk.co.jackoftrades.backend.parser.*;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParser;
import uk.co.jackoftrades.backend.parser.world.WorldParser;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.screen.Screen;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Feature;
import uk.co.jackoftrades.middle.cave.TrapKind;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.combat.CriticalLevel;
import uk.co.jackoftrades.middle.combat.O_CriticalLevel;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.MessageEnum;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.Projection;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerProperty;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameConstants {
    private static final Logger logger = LogManager.getLogger();

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
     * A World record used to store the details of each level of the dunteon, including the Town
     *
     * @param levelName the name of the level
     * @param prevLevel the name of the previous level in the List of worlds
     * @param nextLevel the name of the next level in the list of worlds
     */
    public record World(int levelNumber, @NotNull String levelName, @NotNull String prevLevel,
                        @NotNull String nextLevel) {
    }

    /**
     * Translates a list of <code>GameConstantsParser.Entry</code> to <code>Entry</code>.
     *
     * @param parserEntries the incoming list of <code>GameConstantsParser</code> entries.
     * @return A List of <code>Entry</code> records
     */
    @NotNull
    @Unmodifiable
    @Contract("_ -> !null")
    private static List<Entry> toEntries(@Nullable List<GameConstantsParser.Entry> parserEntries) {
        if (parserEntries == null) return List.of();

        return parserEntries.stream()
                .map(e -> new Entry(e.key(), e.value()))
                .toList();
    }

    /**
     * Translates a list of <code>WorldParser.ParsedWorld</code> records to <code>World</code> records.
     *
     * @param worlds an ArrayList of WorldParser.ParsedWorld records.
     * @return a List of <code>Record</code> records.
     */
    @NotNull
    @Unmodifiable
    @Contract("_ -> !null")
    private static List<World> toWorlds(@Nullable List<WorldParser.ParsedWorld> worlds) {
        if (worlds == null) return List.of();

        return worlds.stream()
                .map(w -> new World(w.level(), w.levelName(), w.levelUp(), w.levelDown()))
                .toList();
    }

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

    /*
     * Things read from constants.txt
     */
    // Level things
    private static int levelMonsterMax;

    // Monster generation things
    private static int allocMonsterChance;
    private static int levelMonsterMin;
    private static int townMonstersDay;
    private static int townMonstersNight;
    private static int reproMonstersNight;
    private static int oodMonsterChance;
    private static int oodMonsterAmount;
    private static int monsterGroupMax;
    private static int monsterGroupDist;

    // Monster gameplay constants
    private static int glyphHardness;
    private static int reproMonsterRate;
    private static int lifeDrainPercent;
    private static int fleeRange;
    private static int turnRange;

    // Dungeon generation constants
    private static int levelRoomMax;
    private static int levelDoorMax;
    private static int wallPierceMax;
    private static int tunnGridMax;
    private static int roomItemAv;
    private static int bothItemAv;
    private static int bothGoldAv;
    private static int levelPitMax;

    // World shape constants
    private static int maxDepth;
    private static int dayLength;
    private static int dungeonHeight;
    private static int dungeonWidth;
    private static int townHeight;
    private static int townWidth;
    private static int feelingTotal;
    private static int feelingNeed;
    private static int stairSkip;
    private static int moveEnergy;

    // Carry capacity constants
    private static int packSize;
    private static int quiverSize;
    private static int quiverSlotSize;
    private static int thrownQuiverMult;
    private static int floorSize;

    // Store parameters
    private static int storeInvenMax;
    private static int storeTurns;
    private static int storeShuffle;
    private static int storeMagicLevel;

    // Object creation constants
    private static int maxObjDepth;
    private static int greatObj;
    private static int greatEgo;
    private static int fuelTorch;
    private static int fuelLamp;
    private static int defaultLamp;

    // Player constants
    private static int maxSight;
    private static int maxRange;
    private static int startGold;
    private static int foodValue;

    // non-O melee critical calculations
    private static int mCritDebuffToh;
    private static int mCritChanceWeightScl;
    private static int mCritChanceTohScl;
    private static int mCritChanceLevelScl;
    private static int mCritChanceTohSkillScl;
    private static int mCritChanceOffset;
    private static int mCritChanceRange;
    private static int mCritPowerWeightScl;
    private static int mCritPowerRandom;

    // non-O ranged critical calculations
    private static int rCritDebuffToh;
    private static int rCritChanceWeightScl;
    private static int rCritChanceTohScl;
    private static int rCritChanceLevelScl;
    private static int rCritChanceLaunchedTohSkillScl;
    private static int rCritChanceThrownTohSkillScl;
    private static int rCritChanceOffset;
    private static int rCritChanceRange;
    private static int rCritPowerWeightScl;
    private static int rCritPowerRandom;

    // O melee critical calculations
    private static int oMCritDebuffToh;
    private static int oMCritPowerTohSclNum;
    private static int oMCritPowerTohSclDen;
    private static int oMCritChancePowerSclNum;
    private static int oMCritChancePowerSclDen;
    private static int oMCritChanceAddDen;

    private static Rational oMeleeMaxAdded;

    // O ranged critical calculations
    private static int oRCritDebuffToh;
    private static int oRCritPowerLaunchedTohSclNum;
    private static int oRCritPowerLaunchedTohSclDen;
    private static int oRCritPowerThrownTohSclNum;
    private static int oRCritPowerThrownTohSclDen;
    private static int oRCritChancePowerSclNum;
    private static int oRCritChancePowerSclDen;
    private static int oRCritChanceAddDen;

    private static Rational oRangedMaxAdded;

    /*
     * Maps of things
     */
    private static final HashMap<EquipmentSlotsEnum, Object> slots = new HashMap<>();
    private static final HashMap<ObjectFlagName, Object> flags = new HashMap<>();
    private static final HashMap<ObjectModifier, Object> modifiers = new HashMap<>();
    private static final HashMap<EffectEnum, Object> effects = new HashMap<>();
    private static final HashMap<TrapEnum, Object> traps = new HashMap<>();
    private static final HashMap<TerrainFeatureFlags, Object> terrainFlags = new HashMap<>();
    private static final HashMap<MonsterRaceFlag, Object> monsterRaceFlags = new HashMap<>();
    private static final HashMap<PlayerFlag, Object> playerInfoFlags = new HashMap<>();

    private static final HashMap<TerrainFlags, Feature> terrains = new HashMap<>();

    // non-O melee and ranged criticals
    private static final ArrayList<CriticalLevel> mCriticalLevels = new ArrayList<>();
    private static final ArrayList<CriticalLevel> rCriticalLevels = new ArrayList<>();

    // O melee and ranged criticals
    private static final ArrayList<O_CriticalLevel> mOCriticalLevels = new ArrayList<>();
    private static final ArrayList<O_CriticalLevel> rOCriticalLevels = new ArrayList<>();

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

    private static final List<TrapKind> trapInfo = new ArrayList<>();
    public static final List<ObjectKind> objectKinds = new ArrayList<>();

    public static final Chunk cave = new Chunk("Current Level", 0, 0, 0, 0, 0, false, 10, 10, 4, 3, 3, 1, 1, 15);
    public static final Player mainPlayer = new Player();

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
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
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
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
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

    public static @Nullable Feature lookupFeature(@NotNull TerrainFlags flag) {
        if (features == null) return null;
        for (Feature feature : features) {
            if (feature.getTerrainFlag().equals(flag))
                return feature;
        }
        return null;
    }

    @CheckReturnValue
    public static @Nullable TrapKind lookupTrap(@NotNull String description) {
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

    public static @Nullable ObjectBase getBaseFromTVal(TValue tVal) {
        for (ObjectBase base : objectBases) {
            if (base.gettVal() == tVal) {
                return base;
            }
        }

        return null;
    }

    public static @Nullable MonsterBase getBaseFromName(String name) {
        for (MonsterBase monsterBase : monsterBases) {
            if (monsterBase.getCodeName().equals(name)) {
                return monsterBase;
            }
        }

        return null;
    }

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
            loadWorld();                // World arraylist determines maxRandDepth
            loadProjections();
            loadUIEntryRenderers();
            loadUIEntryBases();         // Dependent on UIEntyRenderers
            loadUIEntries();            // Dependent on UIEntryBase and UIEntryRenderers
            loadPlayerProperties();     // Dependent on UIEntry
            loadTerrainFeatures();
            loadObjectBases();
            loadPain();
            loadMonsterBases();         // Dependent on MonsterPain
            loadSlays();                // Dependent on MonsterBases
            loadBrands();
            loadSummons();              // Dependent on MonsterBases
            checkSummons();             // Check that the summons list correctly handles the fallback strings
            loadCurses();               // Dependent on ObjectBases
            loadPlayerShapes();
            loadItemObjects();          // Dependent on Summons, Curse, Slay & ObjectBase
        } catch (IOException e) {
            String message = "Unable to load data from " + ANGBAND_DIR_GAMEDATA + " error message: " + e.getMessage();
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    private static void loadItemObjects() {
        ItemObjectReader parser = new ItemObjectReader();
        String filename = ANGBAND_DIR_GAMEDATA + "object.txt";

        try {
            itemObjects = parser.parse(filename);
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

    /**
     * Load in the different levels available in the world and store them in a List
     * @throws IOException an error occurred during the parsing - log it and rethrow it
     */
    private static void loadWorld() throws IOException {
        WorldReader worldReader = new WorldReader();
        String filename = ANGBAND_DIR_GAMEDATA + "world.txt";

        try {
            worlds = toWorlds(worldReader.parse(filename));
        } catch (IOException ex) {
            logger.error("Error while loading file {}", filename, ex);
            throw ex;
        }

        maxRandDepth = worlds.size();
    }

    @Contract(pure = true)
    private GameConstants() {
    }

    /**
     * Loads the contents of the file constants.txt in the lib\gamedata directory and splits the values out
     * into the various GameConstants values.
     */
    private static void loadGameConstants() throws IOException {
        GameConstantsReader reader = new GameConstantsReader();
        String filename = ANGBAND_DIR_GAMEDATA + "constants.txt";
        List<Entry> keyValues = toEntries(reader.parse(filename));

        for (Entry entry : keyValues) {
            switch (entry.key()) {
                case "level-max" -> setLevelMax(entry);
                case "mon-gen" -> setMonGen(entry);
                case "mon-play" -> setMonPlay(entry);
                case "dun-gen" -> setDunGen(entry);
                case "world" -> setWorld(entry);
                case "carry-cap" -> setCarryCap(entry);
                case "store" -> setStoreParameters(entry);
                case "obj-make" -> setObjectCreation(entry);
                case "player" -> setPlayerConstants(entry);
                case "melee-critical" -> setNonOMeleeCrits(entry);
                case "melee-critical-level" -> setNonOMeleeCriticalLevels(entry);
                case "ranged-critical" -> setNonORangedCrits(entry);
                case "ranged-critical-level" -> setNonORangedCriticalLevels(entry);
                case "o-melee-critical" -> setOMeleeCrits(entry);
                case "o-melee-critical-level" -> setOMeleeCriticalLevels(entry);
                case "o-ranged-critical" -> setORangedCrits(entry);
                case "o-ranged-critical-level" -> setORangedCriticalLevels(entry);

                default -> {
                    String message = "Invalid token found while parsing file " + filename + ". Tokens were: " + entry.key() + ":" + entry.value();
                    InvalidTokenFoundDuringParse ex = new InvalidTokenFoundDuringParse(message);
                    logger.error(message, ex);
                    throw ex;
                }
            }
        }
    }


    /**
     * Deals with the O Ranged Critical levels from the constants.txt file
     *
     * @param entry the key value pair from the ORanged section of the constants.txt file
     */
    private static void setORangedCriticalLevels(@NotNull Entry entry) {
        String tag = entry.key() + ":";

        String[] values = entry.value().split(":");

        if (values.length != 3) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int chance;
        int dice;
        MessageEnum message;

        try {
            chance = Integer.parseInt(values[0]);
            dice = Integer.parseInt(values[1]);
            message = MessageEnum.valueOf("MSG_" + values[2]);
        } catch (NumberFormatException e) {
            String errorMessage = "Invalid number format found. Tokens were: " + tag + entry.value();
            logger.error(errorMessage);
            throw new InvalidTokenFoundDuringParse(errorMessage);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Invalid message found. Tokens were: " + tag + entry.value();
            logger.error(errorMessage);
            throw new InvalidTokenFoundDuringParse(errorMessage);
        }

        rOCriticalLevels.add(new O_CriticalLevel(chance, dice, message));
    }

    /**
     * Deals with an O Ranged Crit record from the constants.txt file
     *
     * @param entry the record to examine
     */
    private static void setORangedCrits(@NotNull Entry entry) {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "debuff-toh":
                oRCritDebuffToh = val;
                break;

            case "power-launched-toh-scale-numerator":
                oRCritPowerLaunchedTohSclNum = val;
                break;

            case "power-launched-toh-scale-denominator":
                oRCritPowerLaunchedTohSclDen = val;
                break;

            case "power-thrown-toh-scale-numerator":
                oRCritPowerThrownTohSclNum = val;
                break;

            case "power-thrown-toh-scale-denominator":
                oRCritPowerThrownTohSclDen = val;
                break;

            case "chance-power-scale-numerator":
                oRCritChancePowerSclNum = val;
                break;

            case "chance-power-scale-denominator":
                oRCritChancePowerSclDen = val;
                break;

            case "chance-add-denominator":
                oRCritChanceAddDen = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deals with an O Melee Critical Level record from the constants.txt file
     *
     * @param entry the record to deal with
     */
    private static void setOMeleeCriticalLevels(@NotNull Entry entry) {
        String tag = entry.key() + ":";
        String[] values = entry.value().split(":");

        if (values.length != 3) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int chance;
        int dice;
        MessageEnum messageName;

        try {
            chance = Integer.parseInt(values[0]);
            dice = Integer.parseInt(values[1]);
            messageName = MessageEnum.valueOf("MSG_" + values[2]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format found. Tokens were: " + tag + entry.value();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        } catch (IllegalArgumentException e) {
            String message = "Message flag not found. Tokens were: " + tag + entry.value();
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        mOCriticalLevels.add(new O_CriticalLevel(chance, dice, messageName));
    }

    /**
     * Deal with an O Melee Crit entry
     *
     * @param entry the String key String value record for this entry
     */
    private static void setOMeleeCrits(@NotNull Entry entry) {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "debuff-toh":
                oMCritDebuffToh = val;
                break;

            case "power-toh-scale-numerator":
                oMCritPowerTohSclNum = val;
                break;

            case "power-toh-scale-denominator":
                oMCritPowerTohSclDen = val;
                break;

            case "chance-power-scale-numerator":
                oMCritChancePowerSclNum = val;
                break;

            case "chance-power-scale-denominator":
                oMCritChancePowerSclDen = val;
                break;

            case "chance-add-denominator":
                oMCritChanceAddDen = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a normal ranged critical level entry
     *
     * @param entry the entry
     */
    private static void setNonORangedCriticalLevels(@NotNull Entry entry) {
        String tag = entry.key() + ":";
        String[] results = entry.value().split(":");

        if (results.length != 4) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int cutoffPower = 0;
        int damageMult = 0;
        int amountAdded = 0;
        MessageEnum messageEnum = MessageEnum.MSG_NONE;
        String messageEnumString = "";

        try {
            cutoffPower = Integer.parseInt(results[0]);
            damageMult = Integer.parseInt(results[1]);
            amountAdded = Integer.parseInt(results[2]);
            messageEnumString = results[3];
            messageEnum = MessageEnum.valueOf("MSG_" + messageEnumString);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + entry.value();
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        } catch (IllegalArgumentException e) {
            String message = "Message flag not found. Tokens were: " + tag + entry.value();
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        rCriticalLevels.add(new CriticalLevel(cutoffPower, damageMult, amountAdded, messageEnum));
    }

    /**
     * Deal with a normal Ranged critical entry
     *
     * @param entry the entry
     */
    private static void setNonORangedCrits(@NotNull Entry entry) {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "debuff-toh":
                rCritDebuffToh = val;
                break;

            case "chance-weight-scale":
                rCritChanceWeightScl = val;
                break;

            case "chance-toh-scale":
                rCritChanceTohScl = val;
                break;

            case "chance-level-scale":
                rCritChanceLevelScl = val;
                break;

            case "chance-launched-toh-skill-scale":
                rCritChanceLaunchedTohSkillScl = val;
                break;

            case "chance-thrown-toh-skill-scale":
                rCritChanceThrownTohSkillScl = val;
                break;

            case "chance-offset":
                rCritChanceOffset = val;
                break;

            case "chance-range":
                rCritChanceRange = val;
                break;

            case "power-weight-scale":
                rCritPowerWeightScl = val;
                break;

            case "power-random":
                rCritPowerRandom = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deals with a normal melee critical level entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an invalid number of tokens were input for this entry
     */
    private static void setNonOMeleeCriticalLevels(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        String tag = entry.key() + ":";
        String[] results = entry.value().split(":");

        if (results.length != 4) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + entry.value();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int cutoffPower;
        int damageMult;
        int amountAdded;
        MessageEnum messageEnum;
        String messageEnumString;

        try {
            cutoffPower = Integer.parseInt(results[0]);
            damageMult = Integer.parseInt(results[1]);
            amountAdded = Integer.parseInt(results[2]);
            messageEnumString = results[3];
            messageEnum = MessageEnum.valueOf("MSG_" + messageEnumString);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + entry.value();
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        } catch (IllegalArgumentException e) {
            String message = "Message flag not found. Tokens were: " + tag + entry.value();
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        mCriticalLevels.add(new CriticalLevel(cutoffPower, damageMult, amountAdded, messageEnum));
    }

    /**
     * Deal with a normal melee critical entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse a melee critical entry with an unknown key was found
     */
    private static void setNonOMeleeCrits(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "debuff-toh":
                mCritDebuffToh = val;
                break;

            case "chance-weight-scale":
                mCritChanceWeightScl = val;
                break;

            case "chance-toh-scale":
                mCritChanceTohScl = val;
                break;

            case "chance-level-scale":
                mCritChanceLevelScl = val;
                break;

            case "chance-toh-skill-scale":
                mCritChanceTohSkillScl = val;
                break;

            case "chance-offset":
                mCritChanceOffset = val;
                break;

            case "chance-range":
                mCritChanceRange = val;
                break;

            case "power-weight-scale":
                mCritPowerWeightScl = val;
                break;

            case "power-random":
                mCritPowerRandom = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a Player Constant entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an invalid key found
     */
    private static void setPlayerConstants(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "max-sight":
                maxSight = val;
                break;

            case "max-range":
                maxRange = val;
                break;

            case "start-gold":
                startGold = val;
                break;

            case "food-value":
                foodValue = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a Object Creation entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an unknown key found
     * @throws NumberFormatException        a badly formatted integer found
     */
    private static void setObjectCreation(@NotNull Entry entry) throws InvalidTokenFoundDuringParse, NumberFormatException {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "max-depth":
                maxObjDepth = val;
                break;

            case "great-obj":
                greatObj = val;
                break;

            case "great-ego":
                greatEgo = val;
                break;

            case "fuel-torch":
                fuelTorch = val;
                break;

            case "fuel-lamp":
                fuelLamp = val;
                break;

            case "default-lamp":
                defaultLamp = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a Store Parameter entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an unknown key found
     */
    private static void setStoreParameters(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "inven-max":
                storeInvenMax = val;
                break;

            case "turns":
                storeTurns = val;
                break;

            case "shuffle":
                storeShuffle = val;
                break;

            case "magic-level":
                storeMagicLevel = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a Carry Cap entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an unknown entry.key found
     */
    private static void setCarryCap(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "pack-size":
                packSize = val;
                break;

            case "quiver-size":
                quiverSize = val;
                break;

            case "quiver-slot-size":
                quiverSlotSize = val;
                break;

            case "thrown-quiver-mult":
                thrownQuiverMult = val;
                break;

            case "floor-size":
                floorSize = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a world constant entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an unknown entry.key found
     */
    private static void setWorld(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "max-depth":
                maxDepth = val;
                break;

            case "day-length":
                dayLength = val;
                break;

            case "dungeon-hgt":
                dungeonHeight = val;
                break;

            case "dungeon-wid":
                dungeonWidth = val;
                break;

            case "town-hgt":
                townHeight = val;
                break;

            case "town-wid":
                townWidth = val;
                break;

            case "feeling-total":
                feelingTotal = val;
                break;

            case "feeling-need":
                feelingNeed = val;
                break;

            case "stair-skip":
                stairSkip = val;
                break;

            case "move-energy":
                moveEnergy = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a dungeon generation constant entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse invalid entry.key found
     */
    private static void setDunGen(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "cent-max":
                levelRoomMax = val;
                break;

            case "door-max":
                levelDoorMax = val;
                break;

            case "wall-max":
                wallPierceMax = val;
                break;

            case "tunn-max":
                tunnGridMax = val;
                break;

            case "amt-room":
                roomItemAv = val;
                break;

            case "amt-item":
                bothItemAv = val;
                break;

            case "amt-gold":
                bothGoldAv = val;
                break;

            case "pit-max":
                levelPitMax = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a monster entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse unknown entry.key found
     */
    private static void setMonPlay(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "break-glyph":
                glyphHardness = val;
                break;

            case "mult-rate":
                reproMonsterRate = val;
                break;

            case "life-drain":
                lifeDrainPercent = val;
                break;

            case "flee-range":
                fleeRange = val;
                break;

            case "turn-range":
                turnRange = val;
                break;

            default:
                String message = "Invalid token found. Tokens were: " + entry.key() + ":" + entry.value();
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Deal with a monster generation entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse an unknown entry.key found
     */
    private static void setMonGen(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        switch (name) {
            case "chance":
                allocMonsterChance = val;
                break;

            case "level-min":
                levelMonsterMin = val;
                break;

            case "town-day":
                townMonstersDay = val;
                break;

            case "town-night":
                townMonstersNight = val;
                break;

            case "repro-max":
                reproMonstersNight = val;
                break;

            case "ood-chance":
                oodMonsterChance = val;
                break;

            case "ood-amount":
                oodMonsterAmount = val;
                break;

            case "group-max":
                monsterGroupMax = val;
                break;

            case "group-dist":
                monsterGroupDist = val;
                break;

            default:
                String msg = "Invalid token found in constants.txt file. Input was " + entry.key() + ":" + entry.value();
                logger.error(msg);
                throw new InvalidTokenFoundDuringParse(msg);
        }
    }

    /**
     * Deal with a level maximum entry
     *
     * @param entry the entry
     * @throws InvalidTokenFoundDuringParse An unknown entry.key found
     */
    private static void setLevelMax(@NotNull Entry entry) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(entry.value(), entry.key());

        String name = pair.name();
        int val = pair.value();

        if (name.equals("monsters")) {
            levelMonsterMax = val;
        } else {
            String message = "Unknown tag found in constants.txt file. Token was " + entry.key() + ":" + entry.value();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
    }

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

    public static int getStoreMax() {
        return storeMax;
    }

    public static int getTrapMax() {
        return trapMax;
    }

    public static int getObjectBaseKindMax() {
        return objectBaseKindMax;
    }

    public static int getArtifactKindMax() {
        return artifactKindMax;
    }

    public static int getEgoItemKindMax() {
        return egoItemKindMax;
    }

    public static int getMonsterRaceMax() {
        return monsterRaceMax;
    }

    public static int getMonsterPainMsgMax() {
        return monsterPainMsgMax;
    }

    public static int getMagicSpellMax() {
        return magicSpellMax;
    }

    public static int getMonsterPitTypeMax() {
        return monsterPitTypeMax;
    }

    public static int getRandartActivationsMax() {
        return randartActivationsMax;
    }

    public static int getCurseMax() {
        return curseMax;
    }

    public static int getSlayMax() {
        return slayMax;
    }

    public static int getBrandMax() {
        return brandMax;
    }

    public static int getMonsterBlowsMax() {
        return monsterBlowsMax;
    }

    public static int getMonsterBlowsMethodsMax() {
        return monsterBlowsMethodsMax;
    }

    public static int getMonsterBlowsEffectsMax() {
        return monsterBlowsEffectsMax;
    }

    public static int getPlayerEquipmentSlotsMax() {
        return playerEquipmentSlotsMax;
    }

    public static int getCaveProfileMax() {
        return caveProfileMax;
    }

    public static int getQuestMax() {
        return questMax;
    }

    public static int getProjectionTypeMax() {
        return projectionTypeMax;
    }

    public static int getObjectPowerCalculationMax() {
        return objectPowerCalculationMax;
    }

    public static int getObjectPropertyMax() {
        return objectPropertyMax;
    }

    public static int getObjectsInObject_txt() {
        return objectsInObject_txt;
    }

    public static int getPlayerShapeMax() {
        return playerShapeMax;
    }

    public static int getLevelMonsterMax() {
        return levelMonsterMax;
    }

    public static int getAllocMonsterChance() {
        return allocMonsterChance;
    }

    public static int getLevelMonsterMin() {
        return levelMonsterMin;
    }

    public static int getTownMonstersDay() {
        return townMonstersDay;
    }

    public static int getTownMonstersNight() {
        return townMonstersNight;
    }

    public static int getReproMonstersNight() {
        return reproMonstersNight;
    }

    public static int getOodMonsterChance() {
        return oodMonsterChance;
    }

    public static int getOodMonsterAmount() {
        return oodMonsterAmount;
    }

    public static int getMonsterGroupMax() {
        return monsterGroupMax;
    }

    public static int getMonsterGroupDist() {
        return monsterGroupDist;
    }

    public static int getGlyphHardness() {
        return glyphHardness;
    }

    public static int getReproMonsterRate() {
        return reproMonsterRate;
    }

    public static int getLifeDrainPercent() {
        return lifeDrainPercent;
    }

    public static int getFleeRange() {
        return fleeRange;
    }

    public static int getTurnRange() {
        return turnRange;
    }

    public static int getLevelRoomMax() {
        return levelRoomMax;
    }

    public static int getLevelDoorMax() {
        return levelDoorMax;
    }

    public static int getWallPierceMax() {
        return wallPierceMax;
    }

    public static int getTunnGridMax() {
        return tunnGridMax;
    }

    public static int getRoomItemAv() {
        return roomItemAv;
    }

    public static int getBothItemAv() {
        return bothItemAv;
    }

    public static int getBothGoldAv() {
        return bothGoldAv;
    }

    public static int getLevelPitMax() {
        return levelPitMax;
    }

    public static int getMaxDepth() {
        return maxDepth;
    }

    public static int getDayLength() {
        return dayLength;
    }

    public static int getDungeonHeight() {
        return dungeonHeight;
    }

    public static int getDungeonWidth() {
        return dungeonWidth;
    }

    public static int getTownHeight() {
        return townHeight;
    }

    public static int getTownWidth() {
        return townWidth;
    }

    public static int getFeelingTotal() {
        return feelingTotal;
    }

    public static int getFeelingNeed() {
        return feelingNeed;
    }

    public static int getStairSkip() {
        return stairSkip;
    }

    public static int getMoveEnergy() {
        return moveEnergy;
    }

    public static int getPackSize() {
        return packSize;
    }

    public static int getQuiverSize() {
        return quiverSize;
    }

    public static int getQuiverSlotSize() {
        return quiverSlotSize;
    }

    public static int getThrownQuiverMult() {
        return thrownQuiverMult;
    }

    public static int getFloorSize() {
        return floorSize;
    }

    public static int getStoreInvenMax() {
        return storeInvenMax;
    }

    public static int getStoreTurns() {
        return storeTurns;
    }

    public static int getStoreShuffle() {
        return storeShuffle;
    }

    public static int getStoreMagicLevel() {
        return storeMagicLevel;
    }

    public static int getMaxObjDepth() {
        return maxObjDepth;
    }

    public static int getGreatObj() {
        return greatObj;
    }

    public static int getGreatEgo() {
        return greatEgo;
    }

    public static int getFuelTorch() {
        return fuelTorch;
    }

    public static int getFuelLamp() {
        return fuelLamp;
    }

    public static int getDefaultLamp() {
        return defaultLamp;
    }

    public static int getMaxSight() {
        return maxSight;
    }

    public static int getMaxRange() {
        return maxRange;
    }

    public static int getStartGold() {
        return startGold;
    }

    public static int getFoodValue() {
        return foodValue;
    }

    public static int getmCritDebuffToh() {
        return mCritDebuffToh;
    }

    public static int getmCritChanceWeightScl() {
        return mCritChanceWeightScl;
    }

    public static int getmCritChanceTohScl() {
        return mCritChanceTohScl;
    }

    public static int getmCritChanceLevelScl() {
        return mCritChanceLevelScl;
    }

    public static int getmCritChanceTohSkillScl() {
        return mCritChanceTohSkillScl;
    }

    public static int getmCritChanceOffset() {
        return mCritChanceOffset;
    }

    public static int getmCritChanceRange() {
        return mCritChanceRange;
    }

    public static int getmCritPowerWeightScl() {
        return mCritPowerWeightScl;
    }

    public static int getmCritPowerRandom() {
        return mCritPowerRandom;
    }

    public static int getrCritDebuffToh() {
        return rCritDebuffToh;
    }

    public static int getrCritChanceWeightScl() {
        return rCritChanceWeightScl;
    }

    public static int getrCritChanceTohScl() {
        return rCritChanceTohScl;
    }

    public static int getrCritChanceLevelScl() {
        return rCritChanceLevelScl;
    }

    public static int getrCritChanceLaunchedTohSkillScl() {
        return rCritChanceLaunchedTohSkillScl;
    }

    public static int getrCritChanceThrownTohSkillScl() {
        return rCritChanceThrownTohSkillScl;
    }

    public static int getrCritChanceOffset() {
        return rCritChanceOffset;
    }

    public static int getrCritChanceRange() {
        return rCritChanceRange;
    }

    public static int getrCritPowerWeightScl() {
        return rCritPowerWeightScl;
    }

    public static int getrCritPowerRandom() {
        return rCritPowerRandom;
    }

    public static int getoMCritDebuffToh() {
        return oMCritDebuffToh;
    }

    public static int getoMCritPowerTohSclNum() {
        return oMCritPowerTohSclNum;
    }

    public static int getoMCritPowerTohSclDen() {
        return oMCritPowerTohSclDen;
    }

    public static int getoMCritChancePowerSclNum() {
        return oMCritChancePowerSclNum;
    }

    public static int getoMCritChancePowerSclDen() {
        return oMCritChancePowerSclDen;
    }

    public static int getoMCritChanceAddDen() {
        return oMCritChanceAddDen;
    }

    public static Rational getoMeleeMaxAdded() {
        return oMeleeMaxAdded;
    }

    public static int getoRCritDebuffToh() {
        return oRCritDebuffToh;
    }

    public static int getoRCritPowerLaunchedTohSclNum() {
        return oRCritPowerLaunchedTohSclNum;
    }

    public static int getoRCritPowerLaunchedTohSclDen() {
        return oRCritPowerLaunchedTohSclDen;
    }

    public static int getoRCritPowerThrownTohSclNum() {
        return oRCritPowerThrownTohSclNum;
    }

    public static int getoRCritPowerThrownTohSclDen() {
        return oRCritPowerThrownTohSclDen;
    }

    public static int getoRCritChancePowerSclNum() {
        return oRCritChancePowerSclNum;
    }

    public static int getoRCritChancePowerSclDen() {
        return oRCritChancePowerSclDen;
    }

    public static int getoRCritChanceAddDen() {
        return oRCritChanceAddDen;
    }

    public static Rational getoRangedMaxAdded() {
        return oRangedMaxAdded;
    }

    public static int getMaxRandDepth() {
        return maxRandDepth;
    }
}