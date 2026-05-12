package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class MonsterGenerationConstants {
    private final static String constantsTag = "mon-gen";
    private static int chance;
    private static int levelMin;
    private static int townDay;
    private static int townNight;
    private static int reproMax;
    private static int oodChance;
    private static int oodAmount;
    private static int groupMax;
    private static int groupDist;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Returns the 1 in chance of a new monster being generated
     *
     * @return The chance a new monster is generated
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getAllocMonsterChance() {
        return chance;
    }

    private static void setChance(int chanceVal, @NotNull String tag) throws IllegalArgumentException {
        if (chanceVal <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":" + tag + ":" + chanceVal;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        chance = chanceVal;
    }

    /**
     * Receive a text:value pair in a string, and store in the correct field
     *
     * @param value the text to parse down into a string and value, and store the value in the correct field as denoted
     *              by the string
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");

        // Check we have a valid input
        if (results.length != 2) {
            String message = "Invalid number of tokens found. Token was: " + constantsTag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];
        int val;

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer tokens found. Token was: " + constantsTag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "chance":
                setChance(val, name);
                break;

            case "level-min":
                setLevelMin(val, name);
                break;

            case "town-day":
                setTownDay(val, name);
                break;

            case "town-night":
                setTownNight(val, name);
                break;

            case "repro-max":
                setReproMax(val, name);
                break;

            case "ood-chance":
                setOodChance(val, name);
                break;

            case "ood-amount":
                setOodAmount(val, name);
                break;

            case "group-max":
                setGroupMax(val, name);
                break;

            case "group-dist":
                setGroupDist(val, name);
                break;

            default:
                String msg = "Invalid token found in constants.txt file. Input was " + constantsTag + ":" + value;
                logger.error(msg);
                throw new InvalidTokenFoundDuringParse(msg);
        }
    }

    private static void setLevelMin(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        levelMin = value;
    }

    /**
     * Get the minimum number of monsters generated on a level
     *
     * @return the minimum number of monsters generated on a level as determined by the constants.txt file
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getLevelMonsterMin() {
        return levelMin;
    }

    private static void setTownDay(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value < 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid integer value found in constants.txt file, should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        townDay = value;
    }

    /**
     * Return the value of mon-gen:town-day in the constants.txt file
     *
     * @return The number of townsfolk generated in the day
     */
    @Contract(pure = true)
    public static int getTownMonstersDay() {
        return townDay;
    }

    private static void setTownNight(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value < 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        townNight = value;
    }

    /**
     * Return the value of mon-gen:town-night in the constants.txt file
     *
     * @return The number of townsfolk generated in the night
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getTownMonstersNight() {
        return townNight;
    }

    private static void setReproMax(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value < 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        reproMax = value;
    }

    /**
     * Get the maximum number of breeding monsters allowed on a level
     *
     * @return the maximum number of breeding monsters allowed on a level
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getReproMax() {
        return reproMax;
    }

    private static void setOodChance(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been greater or equal to 0. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        oodChance = value;
    }

    /**
     * Get the chance that a monster will be found out of its normal depth
     *
     * @return The 1-in-value that a monster will be found out of its normal depth
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getOodChance() {
        return oodChance;
    }

    private static void setOodAmount(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been greater or equal to 0. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        oodAmount = value;
    }

    /**
     * Get the maximum out-of-depth amount for monster generation
     *
     * @return The maximum out-of-depth a monster can be generated
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getOodAmount() {
        return oodAmount;
    }

    private static void setGroupMax(int value, @NotNull String tag) throws InvalidTokenFoundDuringParse {
        if (value <= 0 || value > LevelMaxConstants.getMaxMonstersPerLevel()) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been between 1 and "
                    + LevelMaxConstants.getMaxMonstersPerLevel()
                    + ". Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        groupMax = value;
    }

    /**
     * Get the maximum number of monsters in a group
     *
     * @return the maximum allowable monsters in a group
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGroupMax() {
        return groupMax;
    }

    private static void setGroupDist(int value, @NotNull String tag) {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Value was " + value
                    + " should have been greater or equal to 0. Input was " + constantsTag + ":" + tag + ":" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        groupDist = value;
    }

    /**
     * Gets the maximum distance a group of monsters can generate from a related group of monsters
     *
     * @return the maximum distance a group of monsters can generate from a related group of monsters
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGroupDist() {
        return groupDist;
    }
}