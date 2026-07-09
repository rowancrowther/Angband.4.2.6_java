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
 * Holds the "carry-cap" group of tunable constants loaded from the game's
 * {@code constants.txt} data file — the inventory/quiver/floor capacity limits.
 * This is part of the Java port of the C original's {@code z-info}/constants
 * parsing: each constant lives as a {@code static} so it is globally readable,
 * and {@link #setValue(String)} is the parser callback that decodes one
 * {@code name:value} line for this group and routes it to the matching
 * validating setter.
 *
 * @author Rowan Crowther
 */
public class CarryCapacityConstants {
    /**
     * The data-file group tag this class consumes ({@code carry-cap}).
     *
     * @author Rowan Crowther
     */
    private static final String tag = "carry-cap";

    /**
     * Maximum number of pack (inventory) slots the player can carry.
     *
     * @author Rowan Crowther
     */
    private static int packSize;
    /**
     * Maximum number of quiver slots available for ammunition.
     *
     * @author Rowan Crowther
     */
    private static int quiverSize;
    /**
     * Maximum number of missiles that fit in a single quiver slot.
     *
     * @author Rowan Crowther
     */
    private static int quiverSlotSize;
    /**
     * Multiplier applied to non-ammo thrown items when fitting them into a quiver slot.
     *
     * @author Rowan Crowther
     */
    private static int thrownQuiverMult;
    /**
     * Maximum number of items that may stack on a single floor grid.
     *
     * @author Rowan Crowther
     */
    private static int floorSize;

    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse and store a single {@code carry-cap} constant from the data file.
     * The incoming value is expected as {@code name:integer}; the name selects
     * which constant to set and each is validated before being stored.
     *
     * @param value the raw {@code name:value} token from {@code constants.txt}
     * @throws InvalidTokenFoundDuringParse if the token is malformed, the integer
     *                                      cannot be parsed, or the name is not recognised
     * @author Rowan Crowther
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        // check we have the right incoming parameters
        if (values.length != 2) {
            String message = "Incorrect number of incoming tokens during parse. String was " + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int val;
        String name = values[0];

        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + ":" + value + ".\n\n"
                    + e.getMessage();
            logger.error(message, e);
            throw new InvalidTokenFoundDuringParse(message);
        }

        switch (name) {
            case "pack-size":
                setPackSize(val, name);
                break;

            case "quiver-size":
                setQuiverSize(val, name);
                break;

            case "quiver-slot-size":
                setQuiverSlotSize(val, name);
                break;

            case "thrown-quiver-mult":
                setThrownQuiverMult(val, name);
                break;

            case "floor-size":
                setFloorSize(val, name);
                break;

            default:
                String message = "Invalid token found during parse of constants.txt file. Token was :"
                        + tag + ":" + value;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Gets the maximum number of pack slots that can be carried in inventory
     *
     * @return the maximum number of pack slots
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getPackSize() {
        return packSize;
    }

    /**
     * Validate and store the pack size. Rejects non-positive values, since a
     * pack with no slots would make the game unplayable.
     *
     * @param packSize the proposed pack size
     * @param name     the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code packSize <= 0}
     * @author Rowan Crowther
     */
    private static void setPackSize(int packSize, String name) throws InvalidTokenFoundDuringParse {
        if (packSize <= 0) {
            String message = "Invalid pack size imported from constants.txt. Token was: " + tag + ":" + name + ":" + packSize;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        CarryCapacityConstants.packSize = packSize;
    }

    /**
     * Get the maximum number of quiver slots for carrying missiles in
     *
     * @return The maximum number of quiver slots
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getQuiverSize() {
        return quiverSize;
    }

    /**
     * Validate and store the quiver size. Rejects non-positive values.
     *
     * @param quiverSize the proposed quiver size
     * @param name       the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code quiverSize <= 0}
     * @author Rowan Crowther
     */
    private static void setQuiverSize(int quiverSize, String name) throws InvalidTokenFoundDuringParse {
        if (quiverSize <= 0) {
            String message = "Invalid quiver size imported from constants.txt. Token was: " + tag + ":" + name + ":" + quiverSize;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        CarryCapacityConstants.quiverSize = quiverSize;
    }

    /**
     * Get the maximum number of missiles per quiver slot
     *
     * @return the maximum number of missiles per quiver slot
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getQuiverSlotSize() {
        return quiverSlotSize;
    }

    /**
     * Validate and store the per-slot quiver capacity. Rejects negative values.
     *
     * @param quiverSlotSize the proposed missiles-per-slot value
     * @param name           the constant name, used only for error reporting
     * @author Rowan Crowther
     */
    private static void setQuiverSlotSize(int quiverSlotSize, String name) {
        if (quiverSlotSize < 0) {
            String message = "Invalid quiver slot size imported from constants.txt. Token was: " + tag + ":" + name + ":" + quiverSlotSize;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        CarryCapacityConstants.quiverSlotSize = quiverSlotSize;
    }

    /**
     * Gets the multiplier for non-ammo thrown items for calculating how many fit in a single quiver slot
     *
     * @return the multiplier for non-ammo thrown items for calculating how many fit in a single quiver slot
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getThrownQuiverMult() {
        return thrownQuiverMult;
    }

    /**
     * Validate and store the thrown-item quiver multiplier. Rejects negative values.
     *
     * @param thrownQuiverMult the proposed multiplier
     * @param name             the constant name, used only for error reporting
     * @author Rowan Crowther
     */
    private static void setThrownQuiverMult(int thrownQuiverMult, String name) {
        if (thrownQuiverMult < 0) {
            String message = "Invalid thrown quiver multiplier imported from constants.txt. Token was: "
                    + tag + ":" + name + ":" + thrownQuiverMult;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        CarryCapacityConstants.thrownQuiverMult = thrownQuiverMult;
    }

    /**
     * Get the maximum number of items allowed on a floor grid. The main screen originally had a minimum size of 24
     * rows, so it could always display 23 objects and 1 header row.
     *
     * @return the maximum number of items per floor grid.
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getFloorSize() {
        return floorSize;
    }

    /**
     * Validate and store the floor stack size. Requires more than one item per
     * grid (a value of 1 or less would be degenerate).
     *
     * @param floorSize the proposed maximum items per floor grid
     * @param name      the constant name, used only for error reporting
     * @author Rowan Crowther
     */
    private static void setFloorSize(int floorSize, String name) {
        if (floorSize <= 1) {
            String message = "Invalid floor stack size imported from constants.txt. Token was: "
                    + tag + ":" + name + ":" + floorSize;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        CarryCapacityConstants.floorSize = floorSize;
    }
}
