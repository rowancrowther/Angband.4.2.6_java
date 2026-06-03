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

package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class PlayerConstants {
    private final static String tag = "player";

    private static int maxSight = 0;
    private static int maxRange = 0;
    private static int startGold = 0;
    private static int foodValue = 0;

    private final static Logger logger = LogManager.getLogger();

    public static void setValue(String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        if (values.length != 2) {
            String message = "Invalid number of tokens incoming from constants.txt. Value was: "
                    + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        String name = values[0];
        int val;

        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format found in input string from constants.txt. Token was: "
                    + tag + ":" + value + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        switch (name) {
            case "max-sight":
                setMaxSight(val, name);
                break;

            case "max-range":
                setMaxRange(val, name);
                break;

            case "start-gold":
                setStartGold(val, name);
                break;

            case "food-value":
                setFoodValue(val, name);
                break;

            default:
                String message = "Unknown value imported from constants.txt. Token was: "
                        + tag + ":" + value;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the maximum visual range of the player
     *
     * @return the maximum visual range of the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxSight() {
        return maxSight;
    }

    private static void setMaxSight(int maxSight, String name) throws InvalidTokenFoundDuringParse {
        if (maxSight <= 0) {
            String message = "Invalid maximum player sight range. Token was: " + tag + ":" + name + ":" + maxSight;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        PlayerConstants.maxSight = maxSight;
    }

    /**
     * Get the maximum missile and spell range of the player
     *
     * @return the maximum missile and spell range of the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxRange() {
        return maxRange;
    }

    private static void setMaxRange(int maxRange, String name) throws InvalidTokenFoundDuringParse {
        if (maxRange <= 0) {
            String message = "Invalid maximum player missile/spell range. Token was: " + tag + ":" + name + ":" + maxRange;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        PlayerConstants.maxRange = maxRange;
    }

    /**
     * Get the starting amount of gold or value of equipment that the player has
     *
     * @return the starting amount of gold that the player has
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getStartGold() {
        return startGold;
    }

    private static void setStartGold(int startGold, String name) throws InvalidTokenFoundDuringParse {
        if (startGold <= 0) {
            String message = "Invalid player starting gold/equipment amount. Token was: " + tag + ":" + name + ":"
                    + startGold;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        PlayerConstants.startGold = startGold;
    }

    /**
     * Get the number of turns that 1% of player food capacity feeds them for
     *
     * @return the player food capacity duration
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getFoodValue() {
        return foodValue;
    }

    private static void setFoodValue(int foodValue, String name) throws InvalidTokenFoundDuringParse {
        if (foodValue <= 0) {
            String message = "Invalid player food turns. Token was: " + tag + ":" + name + ":" + foodValue;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        PlayerConstants.foodValue = foodValue;
    }
}