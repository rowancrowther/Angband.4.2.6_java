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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.utils.globalvalues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * Holds the "o-ranged-critical" group of constants from {@code constants.txt} —
 * the O-combat critical tuning for ranged attacks, the ranged counterpart to
 * {@link O_MeleeCriticalConstants}. It keeps separate numerator/denominator
 * scale pairs for launched (bow/sling) versus thrown attacks, with the thrown
 * pair deliberately tuned so thrown missiles crit more often. All denominators
 * are guarded against zero. Part of the Java port of the C constants loader.
 *
 * @author ClaudeCode
 */
public class O_RangedCriticalConstants {
    /**
     * The data-file group tag this class consumes ({@code o-ranged-critical}).
     *
     * @author ClaudeCode
     */
    private static final String tag = "o-ranged-critical";

    /**
     * To-hit added when calculating critical power against a debuffed target.
     *
     * @author ClaudeCode
     */
    private static int debufToh;
    /**
     * Numerator of the launched-missile to-hit-to-power scale.
     *
     * @author ClaudeCode
     */
    private static int powerLaunchedTohScaleNumerator;
    /**
     * Denominator of the launched-missile to-hit-to-power scale (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int powerLaunchedTohScaleDenominator;
    /**
     * Numerator of the thrown-missile to-hit-to-power scale.
     *
     * @author ClaudeCode
     */
    private static int powerThrownTohScaleNumerator;
    /**
     * Denominator of the thrown-missile to-hit-to-power scale (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int powerThrownTohScaleDenominator;
    /**
     * Numerator of the power-to-chance scale factor.
     *
     * @author ClaudeCode
     */
    private static int chancePowerScaleNumerator;
    /**
     * Denominator of the power-to-chance scale factor (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int chancePowerScaleDenominator;
    /**
     * Additive term in the critical-chance denominator (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int chanceAddDenominator;

    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Private constructor preventing instantiation; the class is a static holder.
     *
     * @author ClaudeCode
     */
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
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        String name = allTokens[0];
        int value;

        try {
            value = Integer.parseInt(allTokens[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer format found while parsing constants.txt. Token was: " + tag + ":"
                    + tokens + "\n\n" + e.getMessage();
            logger.error(message, e);
            throw e;
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

    /**
     * Store the debuff to-hit bonus. Accepted without validation.
     *
     * @param debufToh the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the launched power-scale numerator. Accepted without validation.
     *
     * @param powerLaunchedTohScaleNumerator the value to store
     * @author ClaudeCode
     */
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

    /**
     * Validate and store the launched power-scale denominator. Rejects zero.
     *
     * @param powerLaunchedTohScaleDenominator the proposed denominator
     * @param name                             the constant name, used only for error reporting
     * @throws IllegalArgumentException if the denominator is zero
     * @author ClaudeCode
     */
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

    /**
     * Store the thrown power-scale numerator. Accepted without validation.
     *
     * @param powerThrownTohScaleNumerator the value to store
     * @author ClaudeCode
     */
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

    /**
     * Validate and store the thrown power-scale denominator. Rejects zero.
     *
     * @param powerThrownTohScaleDenominator the proposed denominator
     * @param name                           the constant name, used only for error reporting
     * @throws IllegalArgumentException if the denominator is zero
     * @author ClaudeCode
     */
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

    /**
     * Store the chance-scale numerator. Accepted without validation.
     *
     * @param chancePowerScaleNumerator the value to store
     * @author ClaudeCode
     */
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

    /**
     * Validate and store the chance-scale denominator. Rejects zero.
     *
     * @param chancePowerScaleDenominator the proposed denominator
     * @param name                        the constant name, used only for error reporting
     * @throws IllegalArgumentException if the denominator is zero
     * @author ClaudeCode
     */
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

    /**
     * Validate and store the additive chance-denominator term. Rejects zero.
     *
     * @param chanceAddDenominator the proposed value
     * @param name                 the constant name, used only for error reporting
     * @throws IllegalArgumentException if the value is zero
     * @author ClaudeCode
     */
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