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
 * Holds the "dun-gen" group of constants from {@code constants.txt} — the
 * budgets the dungeon generator works to: maximum room centres, door/wall/tunnel
 * grid counts, average object/treasure amounts and the pit/nest cap. Part of the
 * Java port of the C constants loader; values are {@code static} globals and
 * {@link #setValue(String)} decodes one {@code name:value} line and routes it to
 * a validating setter.
 *
 * @author Rowan Crowther
 */
public class DungeonGenerationConstants {
    /**
     * The data-file group tag this class consumes ({@code dun-gen}).
     *
     * @author Rowan Crowther
     */
    private final static String tag = "dun-gen";
    /**
     * Maximum number of room centre points (and hence rooms) on a normal level.
     *
     * @author Rowan Crowther
     */
    private static int centMax;
    /**
     * Maximum number of candidate door locations.
     *
     * @author Rowan Crowther
     */
    private static int doorMax;
    /**
     * Maximum number of grids where room walls may be pierced by tunnels.
     *
     * @author Rowan Crowther
     */
    private static int wallMax;
    /**
     * Maximum number of tunnel grids.
     *
     * @author Rowan Crowther
     */
    private static int tunnMax;
    /**
     * Average number of objects placed in rooms.
     *
     * @author Rowan Crowther
     */
    private static int amtRoom;
    /**
     * Average number of objects placed across rooms and corridors.
     *
     * @author Rowan Crowther
     */
    private static int amtItems;
    /**
     * Average amount of treasure placed across rooms and corridors.
     *
     * @author Rowan Crowther
     */
    private static int amtGold;
    /**
     * Maximum number of pits or nests allowed per level.
     *
     * @author Rowan Crowther
     */
    private static int pitMax;
    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Receive a text:value pair in a string, and store in the correct field
     *
     * @param value the text to parse down into a string and value, and store the value in the correct field as denoted
     *              by the string
     */
    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String[] results = value.split(":");
        int val;

        if (results.length != 2) {
            String message = "Invalid number of tokens found parsing string " + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        String name = results[0];

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format found while parsing string " + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "cent-max":
                setCentMax(val, name);
                break;

            case "door-max":
                setDoorMax(val, name);
                break;

            case "wall-max":
                setWallMax(val, name);
                break;

            case "tunn-max":
                setTunnMax(val, name);
                break;

            case "amt-room":
                setAmtRoom(val, name);
                break;

            case "amt-item":
                setAmtItems(val, name);
                break;

            case "amt-gold":
                setAmtGold(val, name);
                break;

            case "pit-max":
                setPitMax(val, name);
                break;

            default:
                String message = "Invalid switch found in constants.txt file. Input was " + tag + ":" + name + ":" + val;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the maximum number of centre points of rooms (and hence of rooms) in a normal level
     *
     * @return the maximum number of centre points of rooms in a normal level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCentMax() {
        return centMax;
    }

    /**
     * Validate and store the maximum room-centre count. Rejects values {@code <= 0}.
     *
     * @param centMax the proposed maximum
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code centMax <= 0}
     * @author Rowan Crowther
     */
    private static void setCentMax(int centMax, String name) throws InvalidTokenFoundDuringParse {
        if (centMax <= 0) {
            String message = "Invalid maximum of room centres in parse of line " + tag + ":" + name + ":" + centMax;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        DungeonGenerationConstants.centMax = centMax;
    }

    /**
     * Get the maximum potential number of places where a door may be generated
     *
     * @return the maximum potential number of places where a door may be generated
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getDoorMax() {
        return doorMax;
    }

    /**
     * Validate and store the maximum door-location count. Rejects values {@code <= 0}.
     *
     * @param doorMax the proposed maximum
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code doorMax <= 0}
     * @author Rowan Crowther
     */
    private static void setDoorMax(int doorMax, String name) throws InvalidTokenFoundDuringParse {
        if (doorMax <= 0) {
            String message = "Invalid maximum of potential door locations in parse of line " + tag + ":" + name + ":" + doorMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.doorMax = doorMax;
    }

    /**
     * Get the maximum number of places to pierce room walls with tunnels
     *
     * @return the maximum number of places to pierce room walls with tunnels
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getWallMax() {
        return wallMax;
    }

    /**
     * Validate and store the maximum wall-piercing count. Rejects values {@code <= 0}.
     *
     * @param wallMax the proposed maximum
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code wallMax <= 0}
     * @author Rowan Crowther
     */
    private static void setWallMax(int wallMax, String name) throws InvalidTokenFoundDuringParse {
        if (wallMax <= 0) {
            String message = "Invalid maximum of grids for walls in parse of line " + tag + ":" + name + ":" + wallMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.wallMax = wallMax;
    }

    /**
     * Get the maximum potential number of tunnel grids
     *
     * @return the maximum potential number of tunnel grids
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getTunnMax() {
        return tunnMax;
    }

    /**
     * Validate and store the maximum tunnel-grid count. Rejects values {@code <= 0}.
     *
     * @param tunnMax the proposed maximum
     * @param name    the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code tunnMax <= 0}
     * @author Rowan Crowther
     */
    private static void setTunnMax(int tunnMax, String name) throws InvalidTokenFoundDuringParse {
        if (tunnMax <= 0) {
            String message = "Invalid maximum of tunnel grids in parse of line " + tag + ":" + name + ":" + tunnMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.tunnMax = tunnMax;
    }

    /**
     * Get the average number of objects to place in rooms
     *
     * @return the average number of objects to place in rooms
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getAmtRoom() {
        return amtRoom;
    }

    /**
     * Validate and store the average objects-per-room amount. Rejects negatives.
     *
     * @param amtRoom the proposed average
     * @param name    the constant name, used only for error reporting
     * @throws IllegalArgumentException if {@code amtRoom < 0}
     * @author Rowan Crowther
     */
    private static void setAmtRoom(int amtRoom, String name) throws IllegalArgumentException {
        if (amtRoom < 0) {
            String message = "Invalid maximum of room centres in parse of line " + tag + ":" + name + ":" + amtRoom;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.amtRoom = amtRoom;
    }

    /**
     * Get the average number of objects to place in rooms/corridors
     *
     * @return the average number of objects to place in rooms/corridors
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getAmtItems() {
        return amtItems;
    }

    /**
     * Validate and store the average objects-everywhere amount. Rejects negatives.
     *
     * @param amtItems the proposed average
     * @param name     the constant name, used only for error reporting
     * @throws IllegalArgumentException if {@code amtItems < 0}
     * @author Rowan Crowther
     */
    private static void setAmtItems(int amtItems, String name) throws IllegalArgumentException {
        if (amtItems < 0) {
            String message = "Invalid maximum of items in all places in parse of line " + tag + ":" + name + ":" + amtItems;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.amtItems = amtItems;
    }

    /**
     * Get the average amount of treasure to place in rooms/corridors
     *
     * @return the average amount of treasure to place in rooms/corridors
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getAmtGold() {
        return amtGold;
    }

    /**
     * Validate and store the average treasure amount. Rejects negatives.
     *
     * @param amtGold the proposed average
     * @param name    the constant name, used only for error reporting
     * @throws IllegalArgumentException if {@code amtGold < 0}
     * @author Rowan Crowther
     */
    private static void setAmtGold(int amtGold, String name) throws IllegalArgumentException {
        if (amtGold < 0) {
            String message = "Invalid treasure being put anywhere parse of line " + tag + ":" + name + ":" + amtGold;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.amtGold = amtGold;
    }

    /**
     * Get the maximum number of pits or nests allowed per level
     *
     * @return the maximum number of pits or nests allowed per level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getPitMax() {
        return pitMax;
    }

    /**
     * Validate and store the maximum pits/nests per level. Rejects negatives.
     *
     * @param pitMax the proposed maximum
     * @param name   the constant name, used only for error reporting
     * @throws IllegalArgumentException if {@code pitMax < 0}
     * @author Rowan Crowther
     */
    private static void setPitMax(int pitMax, String name) throws IllegalArgumentException {
        if (pitMax < 0) {
            String message = "Invalid maximum of pits or nests per level in parse of line " + tag + ":" + name + ":" + pitMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.pitMax = pitMax;
    }
}