package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.background.utils.GlobalUtils;

public class MonsterGeneration {
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

    @Contract(pure = true)
    private static void setChance(int chanceVal) throws IllegalArgumentException {
        if (chanceVal <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":chance:" + chanceVal;
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
    public static void setValue(@NonNull String value) throws IllegalArgumentException {
        String[] results = value.split(":");
        String name = results[0];
        int val = Integer.parseInt(results[1]);

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "chance":
                setChance(val);
                break;

            case "level-min":
                setLevelMin(val);
                break;

            case "town-day":
                setTownDay(val);
                break;

            case "town-night":
                setTownNight(val);
                break;

            case "repro-max":
                setReproMax(val);
                break;

            case "ood-chance":
                setOodChance(val);
                break;

            case "ood-amount":
                setOodAmount(val);
                break;

            case "group-max":
                setGroupMax(val);
                break;

            case "group-dist":
                setGroupDist(val);
                break;

            default:
                String msg = "Invalid switch found in constants.txt file. Input was " + constantsTag + ":" + name + ":" + val;
                logger.error(msg);
                throw new IllegalArgumentException(msg);
        }
    }

    @Contract(pure = true)
    private static void setLevelMin(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":level-min:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
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

    private static void setTownDay(int td) throws IllegalArgumentException {
        if (td < 0 || td > GlobalUtils.getLevelMonsterMax()) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":town-day:" + td;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        townDay = td;
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

    @Contract(pure = true)
    private static void setTownNight(int value) {
        if (value < 0 || value > GlobalUtils.getLevelMonsterMax()) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":town-night:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
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

    @Contract(pure = true)
    private static void setReproMax(int value) throws IllegalArgumentException {
        if (value < 0 || value > GlobalUtils.getLevelMonsterMax()) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":repro-max:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
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

    @Contract(pure = true)
    private static void setOodChance(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":ood-chance:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
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

    @Contract(pure = true)
    private static void setOodAmount(int value) throws IllegalArgumentException {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":ood-amount:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
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

    @Contract(pure = true)
    private static void setGroupMax(int value) throws IllegalArgumentException {
        if (value <= 0 || value > GlobalUtils.getLevelMonsterMax()) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":group-max:" + value;
            logger.error(message);
            throw new IllegalArgumentException(message);
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

    @Contract(pure = true)
    private static void setGroupDist(int value) {
        if (value <= 0) {
            String message = "Invalid switch value found in constants.txt file. Input was " + constantsTag + ":group-dist:" + value;
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