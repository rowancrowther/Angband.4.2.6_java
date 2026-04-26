package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.background.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.combat.CriticalLevel;
import uk.co.jackoftrades.middle.combat.enums.HitType;

import java.util.ArrayList;

public class CriticalLevelConstants {
    private static final String meleeTag = "melee-critical-level";
    private static final String rangedTag = "ranged-critical-level";
    private static final ArrayList<CriticalLevel> meleeLevels = new ArrayList<>();
    private static final ArrayList<CriticalLevel> rangedLevels = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();

    @Contract(pure = true)
    private CriticalLevelConstants() {
    }

    /**
     * Get a string value, CriticalType pair and pull the values from the incoming value parameter and create a new
     * CriticalLevel instance and put it in the correct ArrayList based on the CriticalType
     *
     * @param value The string containing the tokens for the critical value
     * @param type  whether this is a mêlée or ranged hit
     * @throws InvalidTokenFoundDuringParse A token was invalid or found incorrectly
     */
    public static void setValue(@NonNull String value, CriticalType type) throws InvalidTokenFoundDuringParse {
        String tag = type == CriticalType.MELEE ? meleeTag : rangedTag;

        String[] values = value.split(":");

        if (values.length != 4) {
            String message = "Invalid number of tokens found during a parse. Should be 1 tag and 4 sub-tokens. "
                    + values.length + " sub-tokens found. Token was: "
                    + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        // Get the three integer values, remember for one of the Critical Level the first integer is negative.
        // Apart from that they should come in increasing values - to ease we will take them in any order, and fix them
        // in the ArrayList.
        int currentCutoff;
        int damageMult;
        int amountAdded;
        String hitTypeString = values[3];
        HitType hitType;

        try {
            hitType = HitType.valueOf(hitTypeString);
        } catch (IllegalArgumentException e) {
            String message = "Unknown Hit Type message in token. Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        try {
            currentCutoff = Integer.parseInt(values[0]);
        } catch (NumberFormatException e) {
            String message = "Invalid cut off value, could not parse string. Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        try {
            damageMult = Integer.parseInt(values[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid damage multiplier value, could not parse string. Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        try {
            amountAdded = Integer.parseInt(values[2]);
        } catch (NumberFormatException e) {
            String message = "Invalid damage amount added value, could not parse string. Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        CriticalLevel criticalLevel = new CriticalLevel(currentCutoff, damageMult, amountAdded, hitType);

        if (type == CriticalType.MELEE)
            meleeLevels.add(criticalLevel);
        else
            rangedLevels.add(criticalLevel);
    }

    /**
     * Get the relevant critical level based on the type of combat, and the power of this hit.<br><br>
     * <strong>Note:</strong> this assumes that the values in the two array lists are read in, in the correct order from
     * constants.txt. If they are out of order in that file, then errors will occur. These errors are not checked for
     * here.
     *
     * @param power The power of this hit
     * @param type  The type of combat we are dealing with
     * @return The critical level for this type of combat where the previous level's power was less than the power of
     * the hit, and this level's power is greater than or equal to it.
     */
    @Contract(pure = true)
    public static CriticalLevel getCriticalLevel(int power, CriticalType type) {
        CriticalLevel last = null;

        if (type == CriticalType.MELEE) {
            for (CriticalLevel level : meleeLevels) {
                if (level.getCutOff() > power)
                    return level;

                last = level;
            }
        } else {
            for (CriticalLevel level : rangedLevels) {
                if (level.getCutOff() > power)
                    return level;

                last = level;
            }
        }

        return last;
    }

    /**
     * Enum for which type of critical we are looking, melee or ranged.
     */
    public enum CriticalType {
        /**
         * Mêlée combat, combat with anything but magic & thrown/ranged
         */
        MELEE,

        /**
         * Ranged combat, combat with slings, bows, etc.
         */
        RANGED
    }
}