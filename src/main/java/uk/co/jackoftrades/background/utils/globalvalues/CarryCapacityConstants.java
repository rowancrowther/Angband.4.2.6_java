package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.background.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class CarryCapacityConstants {
    private static final String tag = "carry-cap";

    private static int packSize;
    private static int quiverSize;
    private static int quiverSlotSize;
    private static int thrownQuiverMult;
    private static int floorSize;

    private static final Logger logger = LogManager.getLogger();

    public static void setValue(@NonNull String value) throws InvalidTokenFoundDuringParse {
        String[] values = value.split(":");

        // check we have the right incoming parameters
        if (values.length != 2) {
            String message = "Incorrect number of incoming tokens during parse. String was " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int val;
        String name = values[0];

        try {
            val = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Poorly formatted integer in incoming token. Token was " + tag + ":" + value + ".\n\n"
                    + e.getMessage();
            logger.error(message);
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
                logger.error(message);
                throw new InvalidTokenFoundDuringParse(message);
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

    private static void setPackSize(int packSize, String name) throws InvalidTokenFoundDuringParse {
        if (packSize <= 0) {
            String message = "Invalid pack size imported from constants.txt. Token was: " + tag + ":" + name + ":" + packSize;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
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

    private static void setQuiverSize(int quiverSize, String name) throws InvalidTokenFoundDuringParse {
        if (quiverSize <= 0) {
            String message = "Invalid quiver size imported from constants.txt. Token was: " + tag + ":" + name + ":" + quiverSize;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
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

    private static void setQuiverSlotSize(int quiverSlotSize, String name) {
        if (quiverSlotSize < 0) {
            String message = "Invalid quiver slot size imported from constants.txt. Token was: " + tag + ":" + name + ":" + quiverSlotSize;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
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

    private static void setThrownQuiverMult(int thrownQuiverMult, String name) {
        if (thrownQuiverMult < 0) {
            String message = "Invalid thrown quiver multiplier imported from constants.txt. Token was: "
                    + tag + ":" + name + ":" + thrownQuiverMult;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
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

    private static void setFloorSize(int floorSize, String name) {
        if (floorSize <= 1) {
            String message = "Invalid floor stack size imported from constants.txt. Token was: "
                    + tag + ":" + name + ":" + floorSize;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        CarryCapacityConstants.floorSize = floorSize;
    }
}
