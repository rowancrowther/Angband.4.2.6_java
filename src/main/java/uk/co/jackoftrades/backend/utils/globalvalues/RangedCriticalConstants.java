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
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

public class RangedCriticalConstants {
    private static final String tag = "ranged-critical";

    private static int debuffToh;
    private static int chanceWeightScale;
    private static int chanceTohScale;
    private static int chanceLevelScale;
    private static int chanceLaunchedTohSkill;
    private static int chanceThrownTohSkillScale;
    private static int chanceOffset;
    private static int chanceRange;
    private static int powerWeightScale;
    private static int powerRandom;

    private static final Logger logger = LogManager.getLogger();

    public static void setValue(@NotNull String value) throws InvalidTokenFoundDuringParse {
        String name;
        int val;

        String[] tokens = value.split(":");

        // check we have 2 tokens
        if (tokens.length != 2) {
            String message = "Invalid number of tokens in constants.txt. Token was: "
                    + tag + ":" + value;
            InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
            logger.error(message, e);
            throw e;
        }

        name = tokens[0];
        try {
            val = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            String message = "Invalid number format in constants.txt file. Token was: "
                    + tag + ":" + value;
            logger.error(message, e);
            throw e;
        }

        switch (name) {
            case "debuff-toh":
                setDebuffToh(val);
                break;

            case "chance-weight-scale":
                setChanceWeightScale(val);
                break;

            case "chance-toh-scale":
                setChanceTohScale(val);
                break;

            case "chance-level-scale":
                setChanceLevelScale(val);
                break;

            case "chance-launched-toh-skill-scale":
                setChanceLaunchedTohSkill(val);
                break;

            case "chance-thrown-toh-skill-scale":
                setChanceThrownTohSkillScale(val);
//                setChanceLaunchedTohSkill(val);
                break;

            case "chance-offset":
                setChanceOffset(val);
                break;

            case "chance-range":
                setChanceRange(val, name);
                break;

            case "power-weight-scale":
                setPowerWeightScale(val);
                break;

            case "power-random":
                setPowerRandom(val);
                break;

            default:
                String message = "Unknown token found while parsing constants.txt. Token was: "
                        + tag + ":" + value;
                InvalidTokenFoundDuringParse e = new InvalidTokenFoundDuringParse(message);
                logger.error(message, e);
                throw e;
        }
    }

    /**
     * Amount added to the to-hit value for calculating the chance of a ranged critical when the target is debuffed
     *
     * @return Amount added to the to-hit value for calculating the chance of a ranged critical when the target is
     * debuffed
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getDebuffToh() {
        return debuffToh;
    }

    private static void setDebuffToh(int debuffToh) {
        // No tests - run this through without checks
        RangedCriticalConstants.debuffToh = debuffToh;
    }

    /**
     * Scale factor for the missile's weight in the chance for a ranged critical
     *
     * @return Scale factor for the missile's weight in the chance for a ranged critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceWeightScale() {
        return chanceWeightScale;
    }

    private static void setChanceWeightScale(int chanceWeightScale) {
        // No checks pass this straight through
        RangedCriticalConstants.chanceWeightScale = chanceWeightScale;
    }

    /**
     * Get the scale factor for the overall to-hit value when calculating the chance for a ranged critical
     *
     * @return the scale factor for the overall to-hit value when calculating the chance for a ranged critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceTohScale() {
        return chanceTohScale;
    }

    private static void setChanceTohScale(int chanceTohScale) {
        // No checks - pass straight through
        RangedCriticalConstants.chanceTohScale = chanceTohScale;
    }

    /**
     * Gets scale factor for player level when calculating the chance for a ranged critical
     *
     * @return scale factor for player level when calculating the chance for a ranged critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceLevelScale() {
        return chanceLevelScale;
    }

    private static void setChanceLevelScale(int chanceLevelScale) {
        // No checks - pass straight through
        RangedCriticalConstants.chanceLevelScale = chanceLevelScale;
    }

    /**
     * Scale factor fot the launched to-hit skill when calculating the chance for a ranged critical chance when using a
     * launcher
     *
     * @return Scale factor fot the launched to-hit skill when calculating the chance for a ranged critical chance when
     * using a launcher
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceLaunchedTohSkill() {
        return chanceLaunchedTohSkill;
    }

    private static void setChanceLaunchedTohSkill(int chanceLaunchedTohSkill) {
        // No checks - pass the value straight through
        RangedCriticalConstants.chanceLaunchedTohSkill = chanceLaunchedTohSkill;
    }

    /**
     * Scale factor for the thrown to-hit skill when calculating the chance for a ranged critical for a thrown object
     *
     * @return factor for the thrown to-hit skill when calculating the chance for a ranged critical for a thrown object
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceThrownTohSkillScale() {
        return chanceThrownTohSkillScale;
    }

    private static void setChanceThrownTohSkillScale(int chanceThrownTohSkillScale) {
        // No checks - pass straight through
        RangedCriticalConstants.chanceThrownTohSkillScale = chanceThrownTohSkillScale;
    }

    /**
     * Value added to the chance for a ranged critical
     *
     * @return Value added to the chance for a ranged critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceOffset() {
        return chanceOffset;
    }

    private static void setChanceOffset(int chanceOffset) {
        // No checks - pass straight through
        RangedCriticalConstants.chanceOffset = chanceOffset;
    }

    /**
     * Maximum range for ranged critical chance; if the chance is N and the maximum range is M, then the probability a
     * critical will happen is min(max(0, N), M) / M; to avoid extra inaccuracies for the damage shown within object
     * descriptions, this should be a multiple of 100
     *
     * @return Maximum range for ranged critical chance
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getChanceRange() {
        return chanceRange;
    }

    private static void setChanceRange(int chanceRange, String name) {
        if (chanceRange < 0) {
            String message = "Invalid chance range while parsing constants.txt. Correct value should be > 0, Token was: "
                    + tag + ":" + name + ":" + chanceRange;
            logger.error(message);
            throw new InvalidTokenFoundDuringParse(message);
        }

        if ((chanceRange % 100) != 0) {
            String message = "Maximum range for ranged critical chance should be divisible by 100 to avoid extra " +
                    "inaccuracies fore the damage shown withing object descriptions.";
            logger.warn(message);
        }

        RangedCriticalConstants.chanceRange = chanceRange;
    }

    /**
     * Get the scale factor for the missile weight in the power of a ranged critical
     *
     * @return the scale factor for the missile weight in the power of a ranged critical
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getPowerWeightScale() {
        return powerWeightScale;
    }

    private static void setPowerWeightScale(int powerWeightScale) {
        // No checks, just pass straight through
        RangedCriticalConstants.powerWeightScale = powerWeightScale;
    }

    /**
     * Get the maximum of the random part of the power for a ranged critical.
     *
     * @return the maximum of the random part of the power for a ranged critical.
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static int getPowerRandom() {
        return powerRandom;
    }

    private static void setPowerRandom(int powerRandom) {
        // No checks, pass straight through
        RangedCriticalConstants.powerRandom = powerRandom;
    }
}