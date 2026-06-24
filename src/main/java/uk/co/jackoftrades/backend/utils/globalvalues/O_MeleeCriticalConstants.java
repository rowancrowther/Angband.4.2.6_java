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
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

/**
 * Holds the "o-melee-critical" group of constants from {@code constants.txt} —
 * the tuning for the O-combat melee critical formulae. Unlike the Vanilla
 * weight-based system, the O system derives a critical's power and probability
 * from scaled to-hit values expressed as numerator/denominator pairs, so several
 * of these constants are divisors that must be non-zero. Part of the Java port
 * of the C constants loader.
 *
 * @author ClaudeCode
 */
public class O_MeleeCriticalConstants {
    /**
     * The data-file group tag this class consumes ({@code o-melee-critical}).
     *
     * @author ClaudeCode
     */
    private final static String tag = "o-melee-critical";

    /**
     * To-hit added when calculating critical power against a debuffed target.
     *
     * @author ClaudeCode
     */
    private static int debuffToh;
    /**
     * Numerator of the to-hit-to-power scale factor.
     *
     * @author ClaudeCode
     */
    private static int powerTohSclNum;
    /**
     * Denominator of the to-hit-to-power scale factor (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int powerTohSclDen;
    /**
     * Numerator of the power-to-chance scale factor.
     *
     * @author ClaudeCode
     */
    private static int chancePowerSclNum;
    /**
     * Denominator of the power-to-chance scale factor (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int chancePowerSclDen;
    /**
     * Additive term in the critical-chance denominator (must be non-zero).
     *
     * @author ClaudeCode
     */
    private static int chanceAddDen;

    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Get a token from the constants.txt file preceeded by o-melee-critical and parse it.
     *
     * @param token a token from the constants.txt file
     * @throws InvalidTokenFoundDuringParse An error occurred while trying to parse this file. Put the token we are
     *                                      trying to parse into the error message.
     */
    public static void setValue(@NotNull String token) throws InvalidTokenFoundDuringParse {
        String[] tokens = token.split(":");

        // count the tokens - should be 2
        if (tokens.length != 2) {
            String message = "Invalid number of tokens found while parsing. Tokens was: " + tag + ":" + token;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        String name = tokens[0];
        int value;

        // try and get the integer value out
        try {
            value = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid integer found while parsing. Tokens was: " + tag + ":" + token + "\n\n"
                    + e.getMessage();
            logger.error(message, e);
            throw e;
        }

        switch (name) {
            case "debuff-toh":
                setDebuffToh(value);
                break;

            case "power-toh-scale-numerator":
                setPowerTohSclNum(value);
                break;

            case "power-toh-scale-denominator":
                setPowerTohSclDen(value, name);
                break;

            case "chance-power-scale-numerator":
                setChancePowerSclNum(value);
                break;

            case "chance-power-scale-denominator":
                setChancePowerSclDen(value, name);
                break;

            case "chance-add-denominator":
                setChanceAddDen(value, name);
                break;

            default:
                String message = "Invalid token found while parsing constants.txt. Token was: " + tag + ":" + token;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Get the (power) amount to be added to the to-hit value for calculating the power of a mêlée critical when the
     * target is debuffed.
     *
     * @return the (power) amount to be added to the to-hit value for calculating the power of a mêlée critical when the
     * target is debuffed.
     */
    @Contract(pure = true)
    public static int getDebuffToh() {
        return debuffToh;
    }

    /**
     * Store the debuff to-hit bonus. Accepted without validation.
     *
     * @param debuffToh the value to store
     * @author ClaudeCode
     */
    private static void setDebuffToh(int debuffToh) {
        // No checks - pass the value through directly
        O_MeleeCriticalConstants.debuffToh = debuffToh;
    }

    /**
     * The numerator for the scale factor applied to the combined to-hit value to get the power of the critical
     *
     * @return numerator for the scale factor applied to the combined to-hit value to get the power of the critical
     */
    @Contract(pure = true)
    public static int getPowerTohSclNum() {
        return powerTohSclNum;
    }

    /**
     * Store the power-scale numerator. Accepted without validation.
     *
     * @param powerTohSclNum the value to store
     * @author ClaudeCode
     */
    private static void setPowerTohSclNum(int powerTohSclNum) {
        // No checks on this, pass straight through
        O_MeleeCriticalConstants.powerTohSclNum = powerTohSclNum;
    }

    /**
     * Get the scale factor applied to the combined to-hit value to get the power of the critical
     *
     * @return the scale factor applied to the combined to-hit value to get the power of the critical
     */
    public static int getPowerTohSclDen() {
        return powerTohSclDen;
    }

    /**
     * Store the power-scale denominator.
     * <p>
     * Note: a zero value only builds an (unused) error message in the original
     * code and is not actually rejected — a latent bug that could allow a
     * divide-by-zero downstream.
     *
     * @param powerTohSclDen the value to store
     * @param name           the constant name, used only for error reporting
     * @author ClaudeCode
     */
    private static void setPowerTohSclDen(int powerTohSclDen, String name) {
        // Denominator - can't be zero

        if (powerTohSclDen == 0) {
            String message = "Cannot divide by zero when determine critical level. Token was: "
                    + tag + ":" + name + ":" + powerTohSclDen;
        }

        O_MeleeCriticalConstants.powerTohSclDen = powerTohSclDen;
    }

    /**
     * Get the scale factor for the critical's power in the numerator for the chance of the critical
     *
     * @return the scale factor for the critical's power in the numerator for the chance of the critical
     */
    @Contract(pure = true)
    public static int getChancePowerSclNum() {
        return chancePowerSclNum;
    }

    /**
     * Store the chance-scale numerator. Accepted without validation.
     *
     * @param chancePowerSclNum the value to store
     * @author ClaudeCode
     */
    private static void setChancePowerSclNum(int chancePowerSclNum) {
        // No checks - pass the value straight through
        O_MeleeCriticalConstants.chancePowerSclNum = chancePowerSclNum;
    }

    /**
     * Get the scale factor for the critical's power in the denominator for the chance of the critical
     *
     * @return the scale factor for the critical's power in the denominator for the chance of the critical
     */
    @Contract(pure = true)
    public static int getChancePowerSclDen() {
        return chancePowerSclDen;
    }

    /**
     * Validate and store the chance-scale denominator. Rejects zero to prevent a
     * divide-by-zero in the critical-chance calculation.
     *
     * @param chancePowerSclDen the proposed denominator
     * @param name              the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code chancePowerSclDen == 0}
     * @author ClaudeCode
     */
    private static void setChancePowerSclDen(int chancePowerSclDen, String name) {
        // Denominator - can't be zero
        if (chancePowerSclDen == 0) {
            String message = "Invalid denominator value in constants.txt file. Token was: "
                    + tag + ":" + name + ":" + chancePowerSclDen;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        O_MeleeCriticalConstants.chancePowerSclDen = chancePowerSclDen;
    }

    /**
     * Get the value of an added term in the denominator for the chance of a critical
     *
     * @return the value of an added term in the denominator for the chance of a critical
     */
    @Contract(pure = true)
    public static int getChanceAddDen() {
        return chanceAddDen;
    }

    /**
     * Validate and store the additive chance-denominator term. Rejects zero to
     * prevent a divide-by-zero in the critical-chance calculation.
     *
     * @param chanceAddDen the proposed value
     * @param name         the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code chanceAddDen == 0}
     * @author ClaudeCode
     */
    private static void setChanceAddDen(int chanceAddDen, String name) {
        // should not be zero as this may cause a divide by zero
        if (chanceAddDen == 0) {
            String message = "Invalid token found in constants.txt, may cause a divide by zero error. Token was: "
                    + tag + ":" + name + ":" + chanceAddDen;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        O_MeleeCriticalConstants.chanceAddDen = chanceAddDen;
    }
}