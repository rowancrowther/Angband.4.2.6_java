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
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * Holds the "obj-make" group of tunable constants from {@code constants.txt} —
 * object-generation tuning (inflation chances for great/ego items) and light-
 * source fuel limits. Part of the Java port of the C constants loader: values
 * are {@code static} globals and {@link #setValue(String)} decodes one
 * {@code name:value} line and routes it to a validating setter.
 * <p>
 * Some setters depend on others having already run (e.g. fuel-lamp must be set
 * after fuel-torch), so they additionally guard against being parsed out of
 * order in the data file.
 *
 * @author Rowan Crowther
 */
public class ObjectConstants {
    /**
     * The data-file group tag this class consumes ({@code obj-make}).
     *
     * @author Rowan Crowther
     */
    private final static String tag = "obj-make";

    /**
     * Maximum depth used for object allocation (distinct from the dungeon's max level).
     *
     * @author Rowan Crowther
     */
    private static int maxDepth = 0;
    /**
     * 1/chance of inflating a requested object's item level (a "great" object roll).
     *
     * @author Rowan Crowther
     */
    private static int greatObj = 0;
    /**
     * 1/chance of inflating a requested ego item's level (a "great" ego roll).
     *
     * @author Rowan Crowther
     */
    private static int greatEgo = 0;
    /**
     * Maximum fuel a torch can hold, in turns.
     *
     * @author Rowan Crowther
     */
    private static int fuelTorch = 0;
    /**
     * Maximum fuel a lamp can hold, in turns.
     *
     * @author Rowan Crowther
     */
    private static int fuelLamp = 0;
    /**
     * Default fuel a freshly generated lamp starts with.
     *
     * @author Rowan Crowther
     */
    private static int defaultLamp = 0;

    /**
     * Logger used to report malformed/invalid/out-of-order constants during parsing.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse and store a single {@code obj-make} constant from the data file.
     * The value is expected as {@code name:integer}; the name selects the target
     * constant and each is validated before storage.
     *
     * @param value the raw {@code name:value} token from {@code constants.txt}
     * @throws InvalidTokenFoundDuringParse if the token is malformed, the integer
     *                                      cannot be parsed, or the name is unrecognised
     * @author Rowan Crowther
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        // separate the value out to string name and int val
        String[] values = value.split(":");

        if (values.length != 2) {
            String message = "Invalid number of tokens incoming on parsing constants.txt. Token was: "
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
            case "max-depth":
                setMaxDepth(val, name);
                break;

            case "great-obj":
                setGreatObj(val, name);
                break;

            case "great-ego":
                setGreatEgo(val, name);
                break;

            case "fuel-torch":
                setFuelTorch(val, name);
                break;

            case "fuel-lamp":
                setFuelLamp(val, name);
                break;

            case "default-lamp":
                setDefaultLamp(val, name);
                break;

            default:
                String message = "Unknown value imported from constants.txt. Token was: "
                        + tag + ":" + name + ":" + val;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the maximum depth used in object allocation - note, this is not the same as the dungeon max level.
     *
     * @return the maximum depth used in object allocation
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Validate and store the object-allocation max depth. Rejects negatives and
     * warns if it exceeds the dungeon's max depth (which could leave some objects
     * ungeneratable).
     *
     * @param maxDepth the proposed allocation max depth
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code maxDepth < 0}
     * @author Rowan Crowther
     */
    public static void setMaxDepth(int maxDepth, String name) throws InvalidTokenFoundDuringParse {
        if (maxDepth < 0) {
            String message = "Invalid max depth for object allocation. Token was: "
                    + tag + ":" + name + ":" + maxDepth;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        if (maxDepth > GameWorldConstants.getMaxDepth()) {
            String message = "Max depth for object allocation greater than dungeon max depth. Some objects may not be created.";
            logger.warn(message);
        }

        ObjectConstants.maxDepth = maxDepth;
    }

    /**
     * Get the 1/chance of inflating a requested object item level
     *
     * @return the 1/chance of inflating a requested object item level
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGreatObj() {
        return greatObj;
    }

    /**
     * Validate and store the great-object inflation chance. Rejects values below 1
     * (it is used as a 1/N denominator).
     *
     * @param greatObj the proposed 1/chance value
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code greatObj < 1}
     * @author Rowan Crowther
     */
    public static void setGreatObj(int greatObj, String name) throws InvalidTokenFoundDuringParse {
        if (greatObj < 1) {
            String message = "Invalid value for chance of inflating a requested object. Token was: "
                    + tag + ":" + name + ":" + greatObj;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        ObjectConstants.greatObj = greatObj;
    }

    /**
     * Gets the 1/chance of inflating the requested ego level
     *
     * @return the 1/chance of inflating the requested ego level
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getGreatEgo() {
        return greatEgo;
    }

    /**
     * Validate and store the great-ego inflation chance. Rejects values below 1
     * (it is used as a 1/N denominator).
     *
     * @param greatEgo the proposed 1/chance value
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code greatEgo < 1}
     * @author Rowan Crowther
     */
    public static void setGreatEgo(int greatEgo, String name) throws InvalidTokenFoundDuringParse {
        if (greatEgo < 1) {
            String message = "Invalid value for chance of inflating a requested ego item. Token was: "
                    + tag + ":" + name + ":" + greatEgo;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        ObjectConstants.greatEgo = greatEgo;
    }

    /**
     * Get the maximum amount of fuel in a torch in terms of turns
     *
     * @return the maximum amount of fuel in a torch in terms of turns
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getFuelTorch() {
        return fuelTorch;
    }

    /**
     * Validate and store the torch fuel maximum. Rejects values below 1.
     *
     * @param fuelTorch the proposed maximum torch fuel in turns
     * @param name      the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code fuelTorch < 1}
     * @author Rowan Crowther
     */
    public static void setFuelTorch(int fuelTorch, String name) throws InvalidTokenFoundDuringParse {
        if (fuelTorch < 1) {
            String message = "Invalid value for amount of fuel in a torch. Token was: "
                    + tag + ":" + name + ":" + fuelTorch;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        ObjectConstants.fuelTorch = fuelTorch;
    }

    /**
     * Get the maximum amount of fuel in a lamp in terms of turns
     *
     * @return the maximum amount of fuel in a lamp in terms of turns
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getFuelLamp() {
        return fuelLamp;
    }

    /**
     * Validate and store the lamp fuel maximum. Rejects values below 1, requires
     * that {@code fuelTorch} was already parsed (ordering guard), and warns if a
     * lamp would hold less than a torch.
     *
     * @param fuelLamp the proposed maximum lamp fuel in turns
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code fuelLamp < 1} or fuel-torch
     *         has not yet been set
     * @author Rowan Crowther
     */
    public static void setFuelLamp(int fuelLamp, String name) throws InvalidTokenFoundDuringParse {
        if (fuelLamp < 1) {
            String message = "Invalid value for the amount of fuel in a lamp. Token was: "
                    + tag + ":" + name + ":" + fuelLamp;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        if (fuelTorch == 0) {
            String message = "Tokens pulled in in wrong order from constants.txt. Should be fuel-torch before "
                    + "fuel-lamp, but was initiated in incorrect order.";
            logger.fatal(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        if (fuelTorch > fuelLamp) {
            String message = "Warning: Maximum amount of fuel in a lamp is less than maximum amount of fuel in a torch. "
                    + "File constants.txt may have become corrupt. Token was: "
                    + tag + ":" + name + ":" + fuelLamp;
            logger.warn(message);

        }

        ObjectConstants.fuelLamp = fuelLamp;
    }

    /**
     * Get the default amount of fuel in a lamp
     *
     * @return the default amount of fuel in a lamp
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getDefaultLamp() {
        return defaultLamp;
    }

    /**
     * Validate and store the default lamp fuel. Rejects values below 1, requires
     * that {@code fuelLamp} was already parsed (ordering guard), and rejects a
     * default that exceeds the lamp's maximum.
     *
     * @param defaultLamp the proposed default lamp fuel in turns
     * @param name        the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code defaultLamp < 1}, fuel-lamp
     *         has not yet been set, or the default exceeds the lamp maximum
     * @author Rowan Crowther
     */
    public static void setDefaultLamp(int defaultLamp, String name) throws InvalidTokenFoundDuringParse {
        if (defaultLamp < 1) {
            String message = "Invalid value for the default amount of fuel in a lamp. Token was: "
                    + tag + ":" + name + ":" + defaultLamp;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        if (fuelLamp == 0) {
            String message = "Tokens pulled in in wrong order from constants.txt. Should be fuel-lamp before "
                    + "default-lamp, but was initiated in incorrect order.";
            logger.fatal(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        if (defaultLamp > fuelLamp) {
            String message = "Invalid default level of fuel in a lamp, should be less than maximum amount of fuel "
                    + "in a lamp. Token was: " + tag + ":" + name + ":" + defaultLamp;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        ObjectConstants.defaultLamp = defaultLamp;
    }
}