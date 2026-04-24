package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class LevelMax {
    private final static String constantsTag = "level-max";
    private static int monsters;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Private constructor - only has static variables on it
     */
    @Contract(pure = true)
    private LevelMax() {
    }

    /**
     * Get the maximum number of allowed monsters per level
     *
     * @return level-max:monsters as read from the constants.txt file.
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxMonstersPerLevel() {
        return monsters;
    }

    /**
     * Handle the input of a string with a name:value format.
     * This is the level maxima version of this, so there should only be one line
     *
     * @param value a string of the format name:value
     */
    public static void setValue(@NonNull String value) throws IllegalArgumentException {
        String[] results = value.split(":");
        String name = results[0];
        int val = Integer.parseInt(results[1]);

        /*
         * Left as switch statement to allow increase of inputs
         */
        switch (name) {
            case "monsters":
                setMonsters(val);
                return;

            default:
                String msg = "Invalid switch found in constants.txt file. Input was " + constantsTag + ":" + name + ":" + val;
                logger.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    @Contract(pure = true)
    private static void setMonsters(int monsters) throws IllegalArgumentException {
        if (monsters <= 0) {
            String message = "Invalid value for max monsters on a given level. Incoming value is " + monsters
                    + ". Should be greater than 0";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        LevelMax.monsters = monsters;
    }
}