package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class DungeonGenerationConstants {
    private final static String tag = "dun-gen";
    private static int centMax;
    private static int doorMax;
    private static int wallMax;
    private static int tunnMax;
    private static int amtRoom;
    private static int amtItems;
    private static int amtGold;
    private static int pitMax;
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
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        String name = results[0];

        try {
            val = Integer.parseInt(results[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format found while parsing string " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
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
                String msg = "Invalid switch found in constants.txt file. Input was " + tag + ":" + name + ":" + val;
                logger.error(msg);
                throw new InvalidTokenFoundDuringParse(msg);
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

    private static void setCentMax(int centMax, String name) throws InvalidTokenFoundDuringParse {
        if (centMax <= 0) {
            String message = "Invalid maximum of room centres in parse of line " + tag + ":" + name + ":" + centMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
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

    private static void setPitMax(int pitMax, String name) throws IllegalArgumentException {
        if (pitMax < 0) {
            String message = "Invalid maximum of pits or nests per level in parse of line " + tag + ":" + name + ":" + pitMax;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        DungeonGenerationConstants.pitMax = pitMax;
    }
}