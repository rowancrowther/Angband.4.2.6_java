package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class StoreConstants {
    private static final String tag = "store";

    private static int invenMax;
    private static int turns;
    private static int shuffle;
    private static int magicLevel;

    private static final Logger logger = LogManager.getLogger();

    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        if (values.length != 2) {
            String message = "Invalid number of tokens in incoming value from constants.txt. Token was: "
                    + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = values[0];
        int val;

        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer value found in constants.txt. Token was: "
                    + tag + ":" + value + "\n\n" + e.getMessage();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        switch (name) {
            case "inven-max":
                setInvenMax(val, name);
                break;

            case "turns":
                setTurns(val, name);
                break;

            case "shuffle":
                setShuffle(val, name);
                break;

            case "magic-level":
                setMagicLevel(val, name);
                break;

            default:
                String message = "Invalid token found when parsing constants.txt. Token was: "
                        + tag + ":" + name + ":" + val;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
        }
    }

    /**
     * Get the maximum number of discrete items in a store's inventlry
     *
     * @return the maximum number of discrete items in a store's inventlry
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getInvenMax() {
        return invenMax;
    }

    private static void setInvenMax(int invenMax, String name) throws InvalidTokenFoundDuringParse {
        if (invenMax < 1) {
            String message = "Invalid number of store's inventory items from constants.txt. Token was: "
                    + tag + ":" + name + ":" + invenMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
        StoreConstants.invenMax = invenMax;
    }

    /**
     * Gets the number of turns between store's inventory turnovers
     *
     * @return the number of turns between store's inventory turnovers
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getTurns() {
        return turns;
    }

    private static void setTurns(int turns, String name) throws InvalidTokenFoundDuringParse {
        if (turns < 1) {
            String message = "Invalid number of turns between store's turnovers. Token was: "
                    + tag + ":" + name + ":" + turns;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        StoreConstants.turns = turns;
    }

    /**
     * Get the 1/chance per day of an owner changing
     *
     * @return the 1/chance per day of an owner changing
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getShuffle() {
        return shuffle;
    }

    private static void setShuffle(int shuffle, String name) throws InvalidTokenFoundDuringParse {
        if (shuffle < 1) {
            String message = "Invalid number 1/chance of when reading constants.txt. Token was: "
                    + tag + ":" + name + ":" + shuffle;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        StoreConstants.shuffle = shuffle;
    }

    /**
     * Gets the dungeon level required before objects in normal stores gain magical bonuses
     *
     * @return the dungeon level required before objects in normal stores gain magical bonuses
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMagicLevel() {
        return magicLevel;
    }

    private static void setMagicLevel(int magicLevel, String name) throws InvalidTokenFoundDuringParse {
        // dungeon levels start at 1
        if (magicLevel < 1) {
            String message = "Invalid dungeon level required for magic in mormal stores. Token was: "
                    + tag + ":" + name + ":" + magicLevel;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        StoreConstants.magicLevel = magicLevel;
    }
}