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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.io.parsers.*;
import uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter.ConstantsFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter.ConstantsFormatterParser;
import uk.co.jackoftrades.backend.numerics.Rational;
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
import java.util.LinkedList;

public class GameConstants {
    private static final Logger logger = LogManager.getLogger();

    private record NameValuePair(String name, Integer value) {
    }

    /**
     * The deepest level the dungeon can reach. Used in object and monster creations. Must be greater than 100. Setting
     * less than 128 may prevent some objects being created
     */
    private final static int maxRandDepth = 128;

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
     * World is simply a string in a linked list. It starts and ends with null (instead of the C "none" tag). There
     * should be exactly 128 levels, as outlined in the maxRandDepth, and should be from the town (level 0) to Angband
     * 127.
     */
    private static LinkedList<World> world;

    /*
     * Global arrays of master values
     */
    private static ArrayList<Projection> projections;
    private static ArrayList<UIEntryRenderer> uiEntryRenderers;
    private static ArrayList<UIEntryBase> uiBases;
    private static ArrayList<UIEntry> uiEntries;
    private static ArrayList<PlayerProperty> playerProperties;
    private static ArrayList<Feature> features;
    private static ArrayList<ObjectBase> objectBases;
    private static ArrayList<Slay> slays;
    private static ArrayList<Brand> brands;
    private static ArrayList<MonsterPain> monsterPains;
    private static ArrayList<MonsterBase> monsterBases;
    private static ArrayList<Summon> summons;
    private static ArrayList<Curse> curses;
    private static ArrayList<PlayerShape> playerShapes;

    private static final ArrayList<TrapKind> trapInfo = new ArrayList<>();
    public static final ArrayList<ObjectKind> objectKinds = new ArrayList<>();

    public static final Chunk cave = new Chunk("Current Level", 0, 0, 0, 0, 0, false, 10, 10, 4, 3, 3, 1, 1, 15);
    public static final Player mainPlayer = new Player();

    public static @Nullable Feature lookupFeature(@NotNull TerrainFlags flag) {
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

    public static @Nullable UIEntryRenderer getUIEntryRenderer(String name) {
        if (uiEntryRenderers == null) return null;

        for (UIEntryRenderer entry : uiEntryRenderers) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }

        return null;
    }

    public static @Nullable UIEntry getUIEntry(String name) {
        if (uiEntries == null) return null;
        for (UIEntry entry : uiEntries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }

        return null;
    }

    /**
     * Initialise the game objects in the correct order
     */
    public static void init() {
        loadGameConstants();
        loadWorld();
        loadProjections();
        loadUIEntryRenderers();
        loadUIEntryBases();
        loadUIEntries();
        loadPlayerProperties();
        loadTerrainFeatures();
        loadObjectBases();
        loadSlays();
        loadBrands();
        loadPain();
        loadMonsterBases();
        loadSummons();
        loadCurses();
        loadPlayerShapes();
    }

    private static void loadPlayerShapes() {
        PlayerShapeParser parser = new PlayerShapeParser();

        try {
            playerShapes = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "shape.txt");
        } catch (Exception e) {
            logger.error("Error while parsing shape file", e);
        }

//        for (PlayerShape shape : playerShapes) {
//                logger.info(shape.toString());
//        }
    }

    private static void loadCurses() {
        CurseReader curseReader = new CurseReader();

        try {
            curses = curseReader.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "curse.txt");
        } catch (Exception e) {
            logger.error("Error while parsing curses file", e);
        }

//        for (Curse curse : curses) {
//            logger.info(curse.toString());
//        }
    }

    private static void loadSummons() {
        SummonParser parser = new SummonParser();

        try {
            summons = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "summon.txt");
        } catch (Exception e) {
            logger.error("Error while parsing summons file!", e);
        }

//        for (Summon summon : summons) {
//            logger.info(summon.toString());
//        }
    }

    private static void loadMonsterBases() {
        MonsterBaseParser monsterBaseParser = new MonsterBaseParser();

        try {
            monsterBases = monsterBaseParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "monster_base.txt");
        } catch (Exception e) {
            logger.error("Error while parsing MonsterBase", e);
        }

        /* for (MonsterBase monsterBase : monsterBases) {
            logger.info(monsterBase.toString());
        } */
    }

    private static void loadPain() {
        MonsterPainParser parser = new MonsterPainParser();

        try {
            monsterPains = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "pain.txt");
        } catch (Exception e) {
            logger.error("Exception while parsing pain.txt", e);
        }

/*        for (MonsterPain monsterPain : monsterPains) {
            logger.info(monsterPain.toString());
        }*/
    }

    private static void loadBrands() {
        BrandParser brandParser = new BrandParser();

        try {
            brands = brandParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "brand.txt");
        } catch (Exception e) {
            logger.error("Exception while parsing brands.txt", e);
        }

        /* for (Brand brand : brands) {
            logger.info(brand.toString());
        } */
    }

    private static void loadSlays() {
        SlayFormatter slayFormatter = new SlayFormatter();

        try {
            slays = slayFormatter.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "slay.txt");
        } catch (Exception e) {
            logger.error("Exception thrown during loading Slays.", e);
        }

/*        for (Slay slay : slays) {
            logger.info(slay.toString());
        } */
    }

    private static void loadObjectBases() {
        objectBases = new ArrayList<>();
        ObjectBaseParser objectBaseParser = new ObjectBaseParser();

        try {
            objectBases = objectBaseParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "object_base.txt");
        } catch (IOException e) {
            logger.error("Error while loading object_base.txt file", e);
        }

/*        for (ObjectBase objectBase : objectBases) {
            logger.info(objectBase.toString());
        } */
    }

    private static void loadTerrainFeatures() {
        features = new ArrayList<>();
        TerrainFeatureReader parser = new TerrainFeatureReader();

        try {
            // TODO - move and change this string from being hard-coded
            features = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "terrain.txt");
        } catch (Exception e) {
            logger.error("Error while loading terrain properties!", e);
        }

        /* for (Feature terrainFeature : features) {
            logger.info(terrainFeature.toString());
        } */
    }

    private static void loadPlayerProperties() {
        playerProperties = new ArrayList<>();
        PlayerPropertyReader parser = new PlayerPropertyReader();

        try {
            playerProperties = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "player_property.txt");
        } catch (Exception e) {
            logger.error("Error while loading player properties!", e);
        }

        /* for (PlayerProperty property : playerProperties) {
            logger.info(property.toString());
        } */
    }

    private static void loadUIEntries() {
        uiEntries = new ArrayList<>();

        UIEntryParser parser = new UIEntryParser();

        try {
            uiEntries = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "ui_entry.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI entries!", e);
        }

        /* for (UIEntry entry : uiEntries) {
            logger.info(entry.toString());
        } */
    }

    private static void loadUIEntryBases() {
        uiBases = new ArrayList<>();

        UIEntryBaseParser parser = new UIEntryBaseParser();

        try {
            uiBases = parser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "ui_entry_base.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI entry base.", e);
        }

        /* for (UIEntryBase entry : uiBases) {
            logger.info(entry.toString());
        } */
    }

    private static void loadUIEntryRenderers() {
        uiEntryRenderers = new ArrayList<>();

        UIEntryRendParser renderer = new UIEntryRendParser();

        try {
            uiEntryRenderers = renderer.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "ui_entry_renderer.txt");
        } catch (Exception e) {
            logger.error("Error while loading UI Entry Renderers", e);
        }

        /*
        for (UIEntryRenderer uiEntryRenderer : uiEntries) {
            logger.info(uiEntryRenderer.toString());
        }*/
    }

    private static void loadProjections() {
        projections = new ArrayList<>();

        ProjectionParser projectionParser = new ProjectionParser();

        try {
            projections = projectionParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "projection.txt");
        } catch (Exception e) {
            logger.error("Error while loading projections", e);
        }

        /* logger.info("Loaded " + projections.size() + " projections");
        for (Projection projection : projections) {
            logger.info(projection.toString());
        } */
    }

    private static void loadWorld() {
        world = new LinkedList<>();

        WorldParser worldParser = new WorldParser();
        HashMap<Integer, ArrayList<String>> worlds = new HashMap<>();

        try {
            worlds = worldParser.parse(GameConstants.ANGBAND_DIR_GAMEDATA + "world.txt");
        } catch (IOException e) {
            logger.error("Error while loading world", e);
        }

        for (int i : worlds.keySet()) {
            ArrayList<String> readWorld = worlds.get(i);

            int prev = i - 1;
            int next = i < maxRandDepth - 1 ? i + 1 : -1;

            World thisWorld = new World(i, readWorld.getFirst(), prev, next);
            world.add(thisWorld);
        }

        /* Logged the results of this load - worked perfectly
        logger.info("Loaded " + world.size() + " worlds");

        for (World w : world) {
            logger.info(w.toString());
        }
        */
    }

    @Contract(pure = true)
    private GameConstants() {
    }

    private static void loadGameConstants() {
        ConstantsFormatterParser.FileContext fileContext = null;
        try {
            CharStream stream = CharStreams.fromFileName(ANGBAND_DIR_GAMEDATA + File.separator + "constants.txt");
            ConstantsFormatterLexer lexer = new ConstantsFormatterLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ConstantsFormatterParser parser = new ConstantsFormatterParser(tokens);
            fileContext = parser.file();
        } catch (Exception e) {
            logger.error("Error while loading constants.txt file", e);
        }

        if (fileContext != null) {
            ArrayList<HashMap<String, String>> values = fileContext.results;

            for (HashMap<String, String> map : values) {
                String[] keys = map.keySet().toArray(new String[0]);
                String value = keys[0];
                String set = map.get(value);
                switch (value) {
                    case "level-max":
                        setLevelMax(set, value);
                        break;

                    case "mon-gen":
                        setMonGen(set, value);
                        break;

                    case "mon-play":
                        setMonPlay(set, value);
                        break;

                    case "dun-gen":
                        setDunGen(set, value);
                        break;

                    case "world":
                        setWorld(set, value);
                        break;

                    case "carry-cap":
                        setCarryCap(set, value);
                        break;

                    case "store":
                        setStoreParameters(set, value);
                        break;

                    case "obj-make":
                        setObjectCreation(set, value);
                        break;

                    case "player":
                        setPlayerConstants(set, value);
                        break;

                    case "melee-critical":
                        setNonOMeleeCrits(set, value);
                        break;

                    case "melee-critical-level":
                        setNonOMeleeCriticalLevels(set, value);
                        break;

                    case "ranged-critical":
                        setNonORangedCrits(set, value);
                        break;

                    case "ranged-critical-level":
                        setNonORangedCriticalLevels(set, value);
                        break;

                    case "o-melee-critical":
                        setOMeleeCrits(set, value);
                        break;

                    case "o-melee-critical-level":
                        setOMeleeCriticalLevels(set, value);
                        break;

                    case "o-ranged-critical":
                        setORangedCrits(set, value);
                        break;

                    case "o-ranged-critical-level":
                        setORangedCriticalLevels(set, value);
                        break;

                    default:
                        String message = "Invalid token found. Tokens were: " + value + ":" + set;
                        logger.error(message);
                        throw new InvalidTokenFoundDuringParse(message);
                }
            }
        }
    }

    private static void setORangedCriticalLevels(String set, String tag) {
        tag = tag + ":";

        String[] values = set.split(":");

        if (values.length != 3) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
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
            String errorMessage = "Invalid number format found. Tokens were: " + tag + set;
            logger.error(errorMessage);
            throw new InvalidTokenFoundDuringParse(errorMessage);
        } catch (IllegalArgumentException e) {
            String errorMessage = "Invalid message found. Tokens were: " + tag + set;
            logger.error(errorMessage);
            throw new InvalidTokenFoundDuringParse(errorMessage);
        }

        rOCriticalLevels.add(new O_CriticalLevel(chance, dice, message));
    }

    private static void setORangedCrits(String set, String tag) {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setOMeleeCriticalLevels(@NotNull String set, @NotNull String tag) {
        tag = tag + ":";
        String[] values = set.split(":");

        if (values.length != 3) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
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
            String message = "Invalid number format found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        } catch (IllegalArgumentException e) {
            String message = "Message flag not found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        mOCriticalLevels.add(new O_CriticalLevel(chance, dice, messageName));
    }

    private static void setOMeleeCrits(String set, String tag) {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setNonORangedCriticalLevels(@NotNull String set, @NotNull String tag) {
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 4) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
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
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        } catch (IllegalArgumentException e) {
            String message = "Message flag not found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        rCriticalLevels.add(new CriticalLevel(cutoffPower, damageMult, amountAdded, messageEnum));
    }

    private static void setNonORangedCrits(@NotNull String set, @NotNull String tag) {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setNonOMeleeCriticalLevels(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 4) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
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
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        } catch (IllegalArgumentException e) {
            String message = "Message flag not found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        mCriticalLevels.add(new CriticalLevel(cutoffPower, damageMult, amountAdded, messageEnum));
    }

    private static void setNonOMeleeCrits(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setPlayerConstants(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }

    }

    private static void setObjectCreation(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse, NumberFormatException {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setStoreParameters(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setCarryCap(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setWorld(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setDunGen(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setMonPlay(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String message = "Invalid token found. Tokens were: " + tag + set;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    private static void setMonGen(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

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
                String msg = "Invalid token found in constants.txt file. Input was " + tag + set;
                logger.error(msg);
                throw new InvalidTokenFoundDuringParse(msg);
        }
    }

    private static void setLevelMax(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        NameValuePair pair = getValues(set, tag);

        String name = pair.name();
        int val = pair.value();

        if (name.equals("monsters")) {
            levelMonsterMax = val;
        } else {
            String message = "Unknown tag found in constants.txt file. Token was " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
    }

    @Contract("_, _ -> new")
    private static @NotNull NameValuePair getValues(@NotNull String set, @NotNull String tag) {
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of arguments found in incoming line from constants.txt. Line was " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + set;
            logger.error(message);
        }

        return new NameValuePair(name, val);
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