package uk.co.jackoftrades.background.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.background.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.combat.O_CriticalLevel;
import uk.co.jackoftrades.middle.combat.enums.HitType;

import java.util.ArrayList;

public class O_CriticalLevelConstants {
    private static final Logger logger = LogManager.getLogger();
    private static final String meleeTag = "o-melee-critical-level";
    private static final String rangedTag = "o-range-critical-level";
    private static final ArrayList<O_CriticalLevel> meleeCriticals = new ArrayList<>();
    private static final ArrayList<O_CriticalLevel> rangedCriticals = new ArrayList<>();

    @Contract(pure = true)
    private O_CriticalLevelConstants() {
    }

    /**
     * Create a o-critical-level constant and store it in the correct array list
     *
     * @param value The string containing the three tokens needed to create a o-critical-level
     * @param type  The type of this level, either MELEE or RANGED
     * @throws InvalidTokenFoundDuringParse Either there were the wrong number of tokens sent through, or
     *
     */
    public static void setValue(@NotNull String value, CriticalType type) throws InvalidTokenFoundDuringParse {
        String tag = type == CriticalType.MELEE ? meleeTag : rangedTag;
        // String token = tag + ":" + value;

        // check we have three tokens
        String[] tokens = value.split(":");

        if (tokens.length != 3) {
            String message = "Invalid number of tokens found during parsing. Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int chance;
        try {
            chance = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer format in chance found while parsing constants.txt. Token was: " + tag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        if (chance <= 0) {
            String message = "Invalid value of chance found while parsing constants.txt; should be positive."
                    + " Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        int dice;
        try {
            dice = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer format in dice found while parsing constants.txt. Token was: "
                    + tag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        if (dice < 0) {
            String message = "Invalid number of dice found while parsing constants.txt. Token was: " + tag + ":" + value;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        HitType msgt;
        try {
            msgt = HitType.valueOf(tokens[2]);
        } catch (IllegalArgumentException e) {
            String message = "Invalid value of type found while parsing constants.txt. Token was: "
                    + tag + ":" + value
                    + "\n\n" + e.getMessage();
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        O_CriticalLevel level = new O_CriticalLevel(dice, chance, msgt);

        if (type == CriticalType.MELEE)
            meleeCriticals.add(level);
        else
            rangedCriticals.add(level);
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