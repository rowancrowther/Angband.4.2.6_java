package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

public class O_RangedCriticalConstants {
    private static final String tag = "o-ranged-critical";

    private static int debufToh;
    private static int powerLaunchedTohScaleNumerator;
    private static int powerLaunchedTohScaleDenominator;
    private static int powerThrownTohScaleNumerator;
    private static int powerThrownTohScaleDenominator;
    private static int chancePowerScaleNumerator;
    private static int chancePowerScaleDenominator;
    private static int chanceAddDenominator;

    private static final Logger logger = LogManager.getLogger();

    @Contract(pure = true)
    private O_RangedCriticalConstants() {
    }

    /**
     * Set the various numbers which are used to calculate the ranged critical
     *
     * @param tokens a string containing two tokens - the name of the field, and it's value
     */
    public static void setValue(String tokens) {
        // check we only have two tokens.
        String[] allTokens = tokens.split(":");

        if (allTokens.length != 2) {
            String message = "Invalid number of tokens when parsing constants.txt. Token was: " + tag + ":" + tokens;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        String name = allTokens[0];
        int value;

        try {
            value = Integer.parseInt(allTokens[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer format found while parsing constants.txt. Token was: " + tag + ":"
                    + tokens + "\n\n" + e.getMessage();
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        switch (name) {
            case "debuff-toh":
                setDebufToh(value);
                break;

            case "power-launched-toh-scale-numerator":
                setPowerLaunchedTohScaleNumerator(value);
                break;

            case "power-launched-toh-scale-denominator":
                setPowerLaunchedTohScaleDenominator(value, name);
                break;

            case "power-thrown-toh-scale-numerator":
                setPowerThrownTohScaleNumerator(value);
                break;

            case "power-thrown-toh-scale-denominator":
                setPowerThrownTohScaleDenominator(value, name);
                break;

            case "chance-power-scale-numerator":
                setChancePowerScaleNumerator(value);
                break;

            case "chance-power-scale-denominator":
                setChancePowerScaleDenominator(value, name);
                break;

            case "chance-add-denominator":
                setChanceAddDenominator(value, name);
                break;

            default:
                String message = "Unknown token found while parsing constants.txt. Token was: " + tag + ":"
                        + name + ":" + value;
                logger.error(message);
                throw new IllegalArgumentException(message);
        }
    }

    /**
     * Amount added to the to-hit value for calculating the power of a ranged critical when the target is debuffed.
     *
     * @return Amount added to the to-hit value for calculating the power of a ranged critical when the target is
     * debuffed.
     */
    @Contract(pure = true)
    public static int getDebufToh() {
        return debufToh;
    }

    private static void setDebufToh(int debufToh) {
        // No checks - pass right through
        O_RangedCriticalConstants.debufToh = debufToh;
    }

    /**
     * Get the numerator for the scale factor applied to the combined to-hit value to get the power of the critical
     * with a launched missile.
     *
     * @return the numerator for the scale factor applied to the combined to-hit value to get the power of the critical
     * with a launched missile.
     */
    @Contract(pure = true)
    public static int getPowerLaunchedTohScaleNumerator() {
        return powerLaunchedTohScaleNumerator;
    }

    private static void setPowerLaunchedTohScaleNumerator(int powerLaunchedTohScaleNumerator) {
        // No checks on numerators - pass right through
        O_RangedCriticalConstants.powerLaunchedTohScaleNumerator = powerLaunchedTohScaleNumerator;
    }

    /**
     * Get the denominator for the scale factor applied to the combined to-hit value to get the power of the critical
     * with a launched missile.
     *
     * @return the denominator for the scale factor applied to the combined to-hit value to get the power of the
     * critical with a launched missile.
     */
    @Contract(pure = true)
    public static int getPowerLaunchedTohScaleDenominator() {
        return powerLaunchedTohScaleDenominator;
    }

    private static void setPowerLaunchedTohScaleDenominator(int powerLaunchedTohScaleDenominator, String name) {
        // denominator - must not be zero
        if (powerLaunchedTohScaleDenominator == 0) {
            String message = "Invalid value for a denominator while parsing constants.txt. Token was: " + tag + ":"
                    + name + ":" + powerLaunchedTohScaleDenominator;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        O_RangedCriticalConstants.powerLaunchedTohScaleDenominator = powerLaunchedTohScaleDenominator;
    }

    /**
     * Get the numerator for the scale factor applied tot he combined to-hit value to get the power of the critical with
     * a thrown missile; this and the denominator are currently set so thrown missiles get more criticals.
     *
     * @return the numerator for the scale factor applied tot he combined to-hit value to get the power of the critical
     * with a thrown missile; this and the denominator are currently set so thrown missiles get more criticals.
     */
    @Contract(pure = true)
    public static int getPowerThrownTohScaleNumerator() {
        return powerThrownTohScaleNumerator;
    }

    private static void setPowerThrownTohScaleNumerator(int powerThrownTohScaleNumerator) {
        // No checks needed - pass straight through
        O_RangedCriticalConstants.powerThrownTohScaleNumerator = powerThrownTohScaleNumerator;
    }

    /**
     * Get the denominator for the scale factor applied to the combined to-hit value to get the power of the critical
     * with a thrown missile.
     *
     * @return the denominator for the scale factor applied to the combined to-hit value to get the power of the
     * critical with a thrown missile.
     */
    @Contract(pure = true)
    public static int getPowerThrownTohScaleDenominator() {
        return powerThrownTohScaleDenominator;
    }

    private static void setPowerThrownTohScaleDenominator(int powerThrownTohScaleDenominator, String name) {
        // Check that we are not passing through a zero denominator
        if (powerThrownTohScaleDenominator == 0) {
            String message = "Illegal value for  a denominator while parsing constants.txt. Token was: "
                    + tag + ":" + name + ":" + powerThrownTohScaleDenominator;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        O_RangedCriticalConstants.powerThrownTohScaleDenominator = powerThrownTohScaleDenominator;
    }

    /**
     * Get the scale factor for the critical's power in the numerator for the chance of the critical.
     *
     * @return the scale factor for the critical's power in the numerator for the chance of the critical.
     */
    @Contract(pure = true)
    public static int getChancePowerScaleNumerator() {
        return chancePowerScaleNumerator;
    }

    private static void setChancePowerScaleNumerator(int chancePowerScaleNumerator) {
        // No checks needed - pass right through
        O_RangedCriticalConstants.chancePowerScaleNumerator = chancePowerScaleNumerator;
    }

    /**
     * Get the scale factor for the critical's power in the denominator for the chance of the critical
     *
     * @return the scale factor for the critical's power in the denominator for the chance of the critical
     */
    @Contract(pure = true)
    public static int getChancePowerScaleDenominator() {
        return chancePowerScaleDenominator;
    }

    private static void setChancePowerScaleDenominator(int chancePowerScaleDenominator, String name) {
        // denominator - check for divide by zero
        if (chancePowerScaleDenominator == 0) {
            String message = "Invalid zero value found in denominator while parsing constants.txt. Token was: "
                    + tag + ":" + name + ":" + chancePowerScaleDenominator;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        O_RangedCriticalConstants.chancePowerScaleDenominator = chancePowerScaleDenominator;
    }

    /**
     * Get the value of an added term in the denominator for the chance of a critical
     *
     * @return the value of an added term in the denominator for the chance of a critical
     */
    @Contract(pure = true)
    public static int getChanceAddDenominator() {
        return chanceAddDenominator;
    }

    private static void setChanceAddDenominator(int chanceAddDenominator, String name) {
        if (chanceAddDenominator == 0) {
            String message = "Invalid zero value found in denominator while parsing constants.txt. Token was: "
                    + tag + ":" + name + ":" + chanceAddDenominator;
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        O_RangedCriticalConstants.chanceAddDenominator = chanceAddDenominator;
    }
}