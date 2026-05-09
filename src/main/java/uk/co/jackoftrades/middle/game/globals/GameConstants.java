package uk.co.jackoftrades.middle.game.globals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter.ConstantsFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter.ConstantsFormatterParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GameConstants {
    private static final Logger logger = LogManager.getLogger();

    // The directory structure of Angband - OS neutral.
    // Note, if the user wants to save on a custom area, then we will have to amend the function getLibDir()
    // to return that value. That's a future issue
    // TODO Amend getLibDir() to return a potential user specific path
    private static final String libPath = File.separator + "lib" + File.separator;
    private static final String configPath = File.separator + "config" + File.separator;
    private static final String userPath = File.separator + "user" + File.separator;
    public static final String ANGBAND_DIR_GAMEDATA = getLibDir() + libPath + "gamedata";
    public static final String ANGBAND_DIR_CUSTOMIZE = getLibDir() + configPath + "customize";
    public static final String ANGBAND_DIR_HELP = getLibDir() + libPath + "help";
    public static final String ANGBAND_DIR_SCREENS = getLibDir() + libPath + "screens";
    public static final String ANGBAND_DIR_FONTS = getLibDir() + libPath + "fonts";
    public static final String ANGBAND_DIR_TILES = getLibDir() + libPath + "tiles";
    public static final String ANGBAND_DIR_SOUNDS = getLibDir() + libPath + "sounds";
    public static final String ANGBAND_DIR_ICONS = getLibDir() + libPath + "icons";
    public static final String ANGBAND_DIR_USER = getLibDir() + File.separator + "user";
    public static final String ANGBAND_DIR_ARCHIVE = getLibDir() + userPath + "archives";
    public static final String ANGBAND_DIR_SCORES = getLibDir() + userPath + "scores";
    public static final String ANGBAND_DIR_SAVE = getLibDir() + userPath + "save";
    public static final String ANGBAND_DIR_PANIC = getLibDir() + userPath + "panic";

    @Contract(pure = true)
    private static String getLibDir() {
        System.out.println(System.getProperty("user.dir"));
        return System.getProperty("user.dir");
    }

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

    // Object creation consants
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

    private GameConstants() {
    }

    public static void init() {
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

                    default:
                        String message = "Invalid token found. Tokens were: " + value + ":" + set;
                        logger.error(message);
                        throw new InvalidTokenFoundDuringParse(message);
                }
            }
        }
    }

    private static void setNonOMeleeCrits(@NotNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        switch (name) {
            case "max-depth":
                maxDepth = val;
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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length == 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were :" + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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
        tag = tag + ":";
        String[] results = set.split(":");

        if (results.length != 2) {
            String message = "Invalid number of tokens found. Tokens were: " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val = 0;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number found. Tokens were: " + tag + set;
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

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

    private static void setLevelMax(@NonNull String set, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        tag = tag + ":";
        String[] results = set.split(":");
        String name = results[0];
        int val = 0;

        if (results.length != 2) {
            String message = "Invalid number of arguments found in incoming line from constants.txt. Line was " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + set;
            logger.error(message);
        }

        if (name.equals("monsters")) {
            levelMonsterMax = val;
        } else {
            String message = "Unknown tag found in constants.txt file. Token was " + tag + set;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
    }
}