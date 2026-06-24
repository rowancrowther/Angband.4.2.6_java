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

/**
 * Holds the "ranged-critical" group of constants from {@code constants.txt} —
 * the Vanilla-Angband (non-O) critical-hit tuning for ranged combat, the ranged
 * counterpart to {@link MeleeCriticalConstants}. It carries separate to-hit-skill
 * scales for launched versus thrown attacks. Part of the Java port of the C
 * constants loader; {@link #setValue(String)} decodes one {@code name:value}
 * line and routes it to the matching setter.
 *
 * @author ClaudeCode
 */
public class RangedCriticalConstants {
    /**
     * The data-file group tag this class consumes ({@code ranged-critical}).
     *
     * @author ClaudeCode
     */
    private static final String tag = "ranged-critical";

    /**
     * To-hit added when calculating critical chance against a debuffed target.
     *
     * @author ClaudeCode
     */
    private static int debuffToh;
    /**
     * Scale applied to missile weight in the critical-chance formula.
     *
     * @author ClaudeCode
     */
    private static int chanceWeightScale;
    /**
     * Scale applied to the overall to-hit value in the critical-chance formula.
     *
     * @author ClaudeCode
     */
    private static int chanceTohScale;
    /**
     * Scale applied to player level in the critical-chance formula.
     *
     * @author ClaudeCode
     */
    private static int chanceLevelScale;
    /**
     * Scale applied to the to-hit skill when firing from a launcher (bow/sling).
     *
     * @author ClaudeCode
     */
    private static int chanceLaunchedTohSkill;
    /**
     * Scale applied to the to-hit skill when throwing an object.
     *
     * @author ClaudeCode
     */
    private static int chanceThrownTohSkillScale;
    /**
     * Flat offset added to the computed critical chance.
     *
     * @author ClaudeCode
     */
    private static int chanceOffset;
    /**
     * Denominator/range over which the critical chance is evaluated (should be a multiple of 100).
     *
     * @author ClaudeCode
     */
    private static int chanceRange;
    /**
     * Scale applied to missile weight when computing critical power (severity).
     *
     * @author ClaudeCode
     */
    private static int powerWeightScale;
    /**
     * Maximum of the random term added to critical power.
     *
     * @author ClaudeCode
     */
    private static int powerRandom;

    /**
     * Logger used to report malformed/invalid constants during parsing.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse and store a single {@code ranged-critical} constant from the data
     * file ({@code name:integer}), routing it to the matching setter.
     *
     * @param value the raw {@code name:value} token from {@code constants.txt}
     * @throws InvalidTokenFoundDuringParse if the token is malformed, the integer
     *                                      cannot be parsed, or the name is unrecognised
     * @author ClaudeCode
     */
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

    /**
     * Store the debuff to-hit bonus. Accepted without validation.
     *
     * @param debuffToh the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the weight scale for critical chance. Accepted without validation.
     *
     * @param chanceWeightScale the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the to-hit scale for critical chance. Accepted without validation.
     *
     * @param chanceTohScale the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the player-level scale for critical chance. Accepted without validation.
     *
     * @param chanceLevelScale the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the launched (launcher) to-hit-skill scale. Accepted without validation.
     *
     * @param chanceLaunchedTohSkill the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the thrown to-hit-skill scale. Accepted without validation.
     *
     * @param chanceThrownTohSkillScale the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the flat critical-chance offset. Accepted without validation.
     *
     * @param chanceOffset the value to store
     * @author ClaudeCode
     */
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

    /**
     * Validate and store the critical-chance range. Rejects negatives (it is a
     * divisor) and warns if it is not a multiple of 100, which would introduce
     * rounding error into displayed damage.
     *
     * @param chanceRange the proposed range
     * @param name        the constant name, used only for error reporting
     * @throws InvalidTokenFoundDuringParse if {@code chanceRange < 0}
     * @author ClaudeCode
     */
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

    /**
     * Store the weight scale for critical power. Accepted without validation.
     *
     * @param powerWeightScale the value to store
     * @author ClaudeCode
     */
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

    /**
     * Store the random-power maximum. Accepted without validation.
     *
     * @param powerRandom the value to store
     * @author ClaudeCode
     */
    private static void setPowerRandom(int powerRandom) {
        // No checks, pass straight through
        RangedCriticalConstants.powerRandom = powerRandom;
    }
}