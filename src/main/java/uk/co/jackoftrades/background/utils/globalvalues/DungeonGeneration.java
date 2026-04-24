package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public class DungeonGeneration {
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
    public static void setValue(@NonNull String value) throws IllegalArgumentException {
        String[] results = value.split(":");
        String name = results[0];
        int val = Integer.parseInt(results[1]);

        /*
         * Switch statement for monster generation
         */
        switch (name) {
            case "cent-max":
                setCentMax(val);
                break;

            case "door-max":
                setDoorMax(val);
                break;

            case "wall-max":
                setWallMax(val);
                break;

            case "tunn-max":
                setTunnMax(val);
                break;

            case "amt-room":
                setAmtRoom(val);
                break;

            case "amt-item":
                setAmtItems(val);
                break;

            case "amt-gold":
                setAmtGold(val);
                break;

            case "pit-max":
                setPitMax(val);
                break;

            default:
                String msg = "Invalid switch found in constants.txt file. Input was " + tag + ":" + name + ":" + val;
                logger.error(msg);
                throw new IllegalArgumentException(msg);
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

    @Contract(pure = true)
    private static void setCentMax(int centMax) throws IllegalArgumentException {
        DungeonGeneration.centMax = centMax;
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

    @Contract(pure = true)
    private static void setDoorMax(int doorMax) throws IllegalArgumentException {
        DungeonGeneration.doorMax = doorMax;
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

    @Contract(pure = true)
    private static void setWallMax(int wallMax) throws IllegalArgumentException {
        DungeonGeneration.wallMax = wallMax;
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

    @Contract(pure = true)
    private static void setTunnMax(int tunnMax) throws IllegalArgumentException {
        DungeonGeneration.tunnMax = tunnMax;
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

    @Contract(pure = true)
    private static void setAmtRoom(int amtRoom) throws IllegalArgumentException {
        DungeonGeneration.amtRoom = amtRoom;
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

    @Contract(pure = true)
    private static void setAmtItems(int amtItems) throws IllegalArgumentException {
        DungeonGeneration.amtItems = amtItems;
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

    @Contract(pure = true)
    private static void setAmtGold(int amtGold) throws IllegalArgumentException {
        DungeonGeneration.amtGold = amtGold;
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

    @Contract(pure = true)
    private static void setPitMax(int pitMax) throws IllegalArgumentException {
        DungeonGeneration.pitMax = pitMax;
    }
}