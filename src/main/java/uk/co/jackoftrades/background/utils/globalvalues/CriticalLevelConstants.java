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
    private static final String tag = "melee-critical-level";
    private static final ArrayList<CriticalLevel> levels = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();

    @Contract(pure = true)
    private CriticalLevelConstants() {
    }

    public static void setValue(@NonNull String value) throws InvalidTokenFoundDuringParse {
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

        levels.add(criticalLevel);
    }
}