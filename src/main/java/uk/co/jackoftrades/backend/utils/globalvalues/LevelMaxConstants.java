package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class LevelMaxConstants {
    private final static String constantsTag = "level-max";
    private static int monsters;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Private constructor - only has static variables on it
     */
    @Contract(pure = true)
    private LevelMaxConstants() {
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
    public static void setValue(@NonNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");
        int val = 0;

        // Check for correct string type
        if (results.length != 2) {
            String message = "Invalid number of arguments found in incoming value. Value was: " + constantsTag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        } else {
            try {
                val = Integer.parseInt(results[1]);
            } catch (NumberFormatException e) {
                String message = "Poorly formatted integer in incoming token. Token was " + constantsTag + ":" + value;
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
            }
        }

        String name = results[0];

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
                throw new InvalidTokenFoundDuringParse(msg);
        }
    }

    @Contract(pure = false)
    private static void setMonsters(int monsters) throws InvalidTokenFoundDuringParse {
        if (monsters <= 0) {
            String message = "Invalid value for max monsters on a given level. Incoming value is " + monsters
                    + ". Should be greater than 0";
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }
        LevelMaxConstants.monsters = monsters;
    }
}