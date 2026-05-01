package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class GameWorldConstants {
    private final static String tag = "world";
    private static int maxDepth;
    private static int dayLength;
    private static int dungeonHgt;
    private static int dungeonWid;
    private static int townHgt;
    private static int townWid;
    private static int feelingTotal;
    private static int feelingNeed;
    private static int stairSkip;
    private static int moveEnergy;
    private final static Logger logger = LogManager.getLogger();

    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");
        int val;

        // Check for correct string type
        if (results.length != 2) {
            String message = "Invalid number of tokens in Constants.txt file. Token was " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];

        switch (name) {
            case "max-depth":
                setMaxDepth(val, name);
                break;

            case "day-length":
                setDayLength(val, name);
                break;

            case "dungeon-hgt":
                setDungeonHgt(val, name);
                break;

            case "dungeon-wid":
                setDungeonWid(val, name);
                break;

            case "town-hgt":
                setTownHgt(val, name);
                break;

            case "town-wid":
                setTownWid(val, name);
                break;

            case "feeling-total":
                setFeelingTotal(val, name);
                break;

            case "feeling-need":
                setFeelingNeed(val, name);
                break;

            case "stair-skip":
                setStairSkip(val, name);
                break;

            case "move-energy":
                setMoveEnergy(val, name);
                break;

            default:
                String msg = "Invalid switch found in constants.txt file. Input was " + tag + ":" + value;
                logger.error(msg);
                throw new InvalidTokenFoundDuringParse(msg);
        }
    }

    /**
     * Get the maximum dungeon level
     * This must be at least 100 and setting it below 128 may prevent the creation of some objects
     *
     * @return the maximum dungeon level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMaxDepth() {
        return maxDepth;
    }

    private static void setMaxDepth(int maxDepth, String name) throws InvalidTokenFoundDuringParse {
        if (maxDepth < 100) {
            String message = "Dungeon depth set in constants.txt to be less than 100. This value should be at least 100."
                    + " Value was " + tag + ":" + name + ":" + maxDepth;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        } else if (maxDepth < 128) {
            String message = "Dungeon depth set in constants.txt to be less than 128."
                    + "This value may cause some objects not to be created. Value was "
                    + tag + ":" + name + ":" + maxDepth;
            logger.warn(message);
        }

        GameWorldConstants.maxDepth = maxDepth;
    }

    /**
     * Get the length of the day (dawn to dawn) in turns
     *
     * @return the length of the day in turns
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDayLength() {
        return dayLength;
    }

    private static void setDayLength(int dayLength, String name) {
        if (dayLength % 2 != 0) {
            String message = "Number of turns for a day is not even. This may cause issues in the code. Value was: "
                    + tag + ":" + name + ":" + dayLength;
            logger.warn(message);
        }

        GameWorldConstants.dayLength = dayLength;
    }

    /**
     * Get the number of vertical lines (x) in the grid for each dungeon level
     *
     * @return the number of vertical lines (x) in the grid for each dungeon level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDungeonHgt() {
        return dungeonHgt;
    }

    private static void setDungeonHgt(int dungeonHgt, String name) throws InvalidTokenFoundDuringParse {
        if (dungeonHgt < 1) {
            String message = "Invalid maximum dungeon height. Token string was: "
                    + tag + ":" + name + ":" + dungeonHgt;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.dungeonHgt = dungeonHgt;
    }

    /**
     * Get the number of horizontal lines (y) in the grid for each dungeon level
     *
     * @return the number of horizontal lines (y) in the grid for each dungeon level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDungeonWid() {
        return dungeonWid;
    }

    private static void setDungeonWid(int dungeonWid, String name) throws InvalidTokenFoundDuringParse {
        if (dungeonWid < 1) {
            String message = "Invalid maximum dungeon width. Token string was: "
                    + tag + ":" + name + ":" + dungeonWid;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.dungeonWid = dungeonWid;
    }

    /**
     * Get the number of vertical lines (x) in the grid for the town
     *
     * @return the number of vertical lines (x) in the grid for the town
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getTownHgt() {
        return townHgt;
    }

    private static void setTownHgt(int townHgt, String name) throws InvalidTokenFoundDuringParse {
        if (townHgt < 1) {
            String message = "Invalid maximum town height. Token string was: "
                    + tag + ":" + name + ":" + townHgt;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.townHgt = townHgt;
    }

    /**
     * Get the number of horizontal lines (y) in the grid for the town
     *
     * @return the number of horizontal lines (y) in the grid for the town
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getTownWid() {
        return townWid;
    }

    private static void setTownWid(int townWid, String name) throws InvalidTokenFoundDuringParse {
        if (townWid < 1) {
            String message = "Invalid maximum town width. Token string was: "
                    + tag + ":" + name + ":" + townWid;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.townWid = townWid;
    }

    /**
     * Get the total number of feeling squares per level
     *
     * @return the number of feeling squares per level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getFeelingTotal() {
        return feelingTotal;
    }

    private static void setFeelingTotal(int feelingTotal, String name) throws InvalidTokenFoundDuringParse {
        if (feelingTotal < 1) {
            String message = "Invalid total feeling squares per level. Token string was: "
                    + tag + ":" + name + ":" + feelingTotal;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.feelingTotal = feelingTotal;
    }

    /**
     * Get the number of squares the player has to move before the sees the first feeling
     *
     * @return the number of squares the player has to move before the sees the first feeling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getFeelingNeed() {
        return feelingNeed;
    }

    private static void setFeelingNeed(int feelingNeed, String name) throws InvalidTokenFoundDuringParse {
        if (feelingNeed < 1) {
            String message = "Invalid squares needed to visit before feeling. Token string was: "
                    + tag + ":" + name + ":" + feelingNeed;
            logger.error(message);

            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.feelingNeed = feelingNeed;
    }

    /**
     * Gets the number of levels each staircase takes the player down
     *
     * @return the number of levels each staircase takes the player down
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getStairSkip() {
        return stairSkip;
    }

    private static void setStairSkip(int stairSkip, String name) throws InvalidTokenFoundDuringParse {
        if (feelingNeed < 1) {
            String message = "Invalid squares needed to visit before feeling. Token string was: "
                    + tag + ":" + name + ":" + stairSkip;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
        GameWorldConstants.stairSkip = stairSkip;
    }

    /**
     * Gets the energy needed by the player and the monsters to move
     *
     * @return the energy needed by the player and the monsters to move
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getMoveEnergy() {
        return moveEnergy;
    }

    private static void setMoveEnergy(int moveEnergy, String name) throws InvalidTokenFoundDuringParse {
        if (feelingNeed < 1) {
            String message = "Invalid energy needed to move by player or monster. Token string was: "
                    + tag + ":" + name + ":" + moveEnergy;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        GameWorldConstants.moveEnergy = moveEnergy;
    }
}