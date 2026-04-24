package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class MonsterGameplay {
    private final static String constantsTag = "mon-play";
    private static int glyphHardness;
    private static int reproMonsterRate;
    private static int lifeDrainPercent;
    private static int fleeRange;
    private static int turnRange;
    private final static Logger logger = LogManager.getLogger();

    /**
     * Receive a text:value pair in a string, and store in the correct field
     *
     * @param value the text to parse down into a string and value, and store the value in the correct field as denoted
     *              by the string
     */
    @Contract(pure = true)
    public static void setValue(@NonNull String value) throws IllegalArgumentException {
        String[] results = value.split(":");
        String name = results[0];
        int val = Integer.parseInt(results[1]);

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "break-glyph":
                setGlyphHardness(val);
                break;

            //noinspection SpellCheckingInspection
            case "mult-rate":
                setReproMonsterRate(val);
                break;

            case "life-drain":
                setLifeDrainPercent(val);
                break;

            case "flee-range":
                setFleeRange(val);
                break;

            case "turn-range":
                setTurnRange(val);
                break;

            default:
                String msg = "Invalid switch found in constants.txt file. Input was " + constantsTag + ":" + name + ":" + val;
                logger.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    @Contract(pure = true)
    private static void setGlyphHardness(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":break-glyph:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        glyphHardness = value;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public static int getGlyphHardness() {
        return glyphHardness;
    }

    @Contract(pure = true)
    private static void setReproMonsterRate(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":mult-rate:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        reproMonsterRate = value;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public static int getReproMonsterRate() {
        return reproMonsterRate;
    }

    @Contract(pure = true)
    private static void setLifeDrainPercent(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":life-drain:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        lifeDrainPercent = value;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public static int getLifeDrainPercent() {
        return lifeDrainPercent;
    }

    @Contract(pure = true)
    private static void setFleeRange(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":flee-range:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        fleeRange = value;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public static int getFleeRange() {
        return fleeRange;
    }

    @Contract(pure = true)
    private static void setTurnRange(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":turn-range:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        turnRange = value;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public static int getTurnRange() {
        return turnRange;
    }
}