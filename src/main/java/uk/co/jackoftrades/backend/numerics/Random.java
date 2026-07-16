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
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.numerics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;
import uk.co.jackoftrades.backend.enums.DamageAspect;
import uk.co.jackoftrades.backend.parser.RandomReader;

import java.io.IOException;
import java.util.List;

/**
 * A parameterised random value of the classic Angband form
 * {@code base + m_bonus + dice 'd' sides}, where the {@code m_bonus} term scales
 * with dungeon level. This is the Java port of the {@code random_value} struct
 * and its {@code randcalc()} helper from the original C source
 * ({@code src/z-rand.h} / {@code src/z-rand.c}); it is used throughout the data
 * files to express things like damage rolls, durations and quantities.
 * <p>
 * Negative ranges are handled by the {@link #negate()} mechanism rather than a
 * negative base, because the data-file grammar cannot itself express a negative
 * base — the value is built positive and then flipped.
 *
 * @author Rowan Crowther
 */
public class Random {
    /**
     * Unparsed string form of the base term, used by the constructors that take
     * the base as an expression rather than a resolved integer.
     *
     * @author Rowan Crowther
     */
    private String baseStr;

    private boolean isComplex;
    /**
     * The fixed base (minimum) contribution to the rolled value.
     *
     * @author Rowan Crowther
     */
    private int base;
    /**
     * Number of dice rolled (the {@code N} in {@code NdM}).
     *
     * @author Rowan Crowther
     */
    private int dice;
    /**
     * Unparsed string form of the sides term, for the expression-based constructor.
     *
     * @author Rowan Crowther
     */
    private String sidesStr;
    /**
     * Number of sides on each die (the {@code M} in {@code NdM}).
     *
     * @author Rowan Crowther
     */
    private int sides;
    /**
     * Level-scaling bonus multiplier ({@code m_bonus} in the C original); its
     * contribution grows with the dungeon level passed to {@link #randCalc(int, uk.co.jackoftrades.backend.enums.DamageAspect)}.
     *
     * @author Rowan Crowther
     */
    private int mBonus;
    /**
     * Flag set when the constructed value should ultimately represent a negative
     * range; consumed once by {@link #negate()}.
     *
     * @author Rowan Crowther
     */
    private boolean toNegate;
    /**
     * Guard ensuring {@link #negate()} only flips the value a single time.
     *
     * @author Rowan Crowther
     */
    private boolean negated;
    /**
     * Shared logger for parse/calculation diagnostics.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger(Random.class.getName());
    //private final boolean debug = false;

    /**
     * Constructor: four integer parameters
     * <br><br>
     * Creates a non negated dice of the form:
     * base + mBonus * dice 'd' dice
     * <br><br>
     * Once this has been created then the dice is negated to deal with negative base values.
     * @param base the base or minimum value that the die can roll. If this is negative then
     *             the dice is marked as to negate
     * @param dice the number of dice to roll
     * @param sides the sides on each die
     * @param mBonus the multiplier of the die roll
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Random(int base, int mBonus, int dice, int sides, boolean toNegate) {
        //if (debug) logger.traceEntry("Random.constructor with values base: {} mBonus: {} dice: {} sides: {} toNegate: {}", base, mBonus, dice, sides, toNegate);
        this.base = base;
        this.dice = dice;
        this.sides = sides;
        this.mBonus = mBonus;
        this.toNegate = toNegate;
        negate();

//        // The below lines are put in to deal with the issue of negative bases not being caught by the parser.
//        // TODO: Check this in the upcoming Reader sweep
//        if (this.base < 0) {
//            this.toNegate = true;
//            this.base = this.base * -1;
//        }
//        negated = false;
    }

    /**
     * Constructor variant where the base term arrives as an unresolved
     * expression string (stored in {@link #baseStr}) rather than an integer —
     * used when the data file gives the base as a formula to evaluate later.
     *
     * @param base   the base term as a string expression
     * @param mBonus the level-scaling bonus multiplier
     * @param dice   the number of dice
     * @param sides  the sides per die
     * @author Rowan Crowther
     */
    public Random(String base, int mBonus, int dice, int sides) {
        this.baseStr = base;
        this.dice = dice;
        this.sides = sides;
        this.mBonus = mBonus;
    }

    /**
     * Constructor variant where the sides term arrives as an unresolved
     * expression string (stored in {@link #sidesStr}) rather than an integer.
     *
     * @param base  the base (minimum) value
     * @param mBonus the level-scaling bonus multiplier
     * @param dice  the number of dice
     * @param sides the sides per die as a string expression
     * @author Rowan Crowther
     */
    public Random(int base, int mBonus, int dice, String sides) {
        this.base = base;
        this.dice = dice;
        this.mBonus = mBonus;
        this.sidesStr = sides;
    }


    /**
     * Turn a string into a Random
     *
     * @param randomString the Random string we are trying to turn into a random
     * @return A Random based on the string, or null if an error occurred
     */
    @Nullable
    @CheckReturnValue
    public static Random parseStr(@NotNull String randomString) {
        if (randomString.isEmpty()) return null;

        boolean negFound = randomString.charAt(0) == '-';
        boolean complex = randomString.contains("$");

        // neg and complex - not allowed
        if (negFound && complex) {
            logger.warn("negated complex variable dice not supported: {}", randomString);
            return null;
        }

        // neg & simple
        if (negFound) {
            randomString = randomString.substring(1);
            if (randomString.isEmpty()) return null;
        }

        // randomString is always positive at this point

        RandomReader reader = new RandomReader();
        List<Random> randoms;
        try {
            randoms = reader.parse(randomString);
        } catch (IOException e) {
            logger.error("Error while parsing random string: {}", randomString, e);
            return null;
        }

        if (randoms.isEmpty()) return null;

        Random random = randoms.getFirst();
        if (random == null) return null;

        if (negFound) {
            random.toNegate = true;
            random.negate();
        }

        return random;
    }

    /**
     * Sets the base value for this Random die. If an invalid value (less than 0) is
     * passed in, the value is set to 0.
     * @param base the lowest value that the Random can roll
     */
    @Contract(mutates = "this")
    public void setBase(int base) {
        if (base < 0) base = 0;
        this.base = base;
    }

    /**
     * Sets the dice value for this Random die. If an invalid value (less than 1) is
     * passed in, the value is set to 1.
     * @param dice the number of dice to roll to create this Random value
     */
    @Contract(mutates = "this")
    public void setDice(int dice) {
        if (dice <= 0) dice = 1;
        this.dice = dice;
    }

    /**
     * Sets the sides of the dice to roll for this Random die. If an invalid value (less than 1) is
     * passed in, the value is set to 1.
     * @param sides the type of dice to roll, i.e. d4, d6, d5, etc.
     */
    @Contract(mutates = "this")
    public void setSides(int sides) {
        if (sides <= 0) sides = 1;
        this.sides = sides;
    }

    /**
     * Sets the multiplier (m) in the 1+m*2d3. If an invalid value (less than 1) is passed
     * in, the value is set to 1.
     * @param mBonus the multiplier in the dice formula
     */
    @Contract(mutates = "this")
    public void setMBonus(int mBonus) {
        if (mBonus <= 0) mBonus = 1;
        this.mBonus = mBonus;
    }

    /**
     * Sets this random to be negatable. This should only be called by the test
     *
     * @param toNegate whether this random should be negated or not.
     */
    @Contract(mutates = "this")
    @TestOnly
    public void setToNegate(boolean toNegate) {
        if (negated)
            this.toNegate = false;
        else
            this.toNegate = toNegate;
    }

    /**
     * Returns the base of this die
     * @return The lowest possible number this die can roll
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getBase() {
        return base;
    }

    /**
     * Returns the number of dice this random rolls
     * @return the number of dice this random rolls
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getDice() {
        return dice;
    }

    /**
     * Returns the sides on each of the dice that this Random rolls.
     * @return The sides on each of the dice that this Random rolls.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getSides() {
        return sides;
    }

    /**
     * Gets the multiplier for this dice formula
     * @return the multiplier for this dice formula
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getMBonus() {
        return mBonus;
    }

    /**
     * Negates this random providing the Random.toNegate is set to true, and the
     * random has not already been negated. This calculates a new base value based
     * on the entire value rolled being negated
     */
    @Contract(mutates = "this")
    public void negate() {
        //if (debug) logger.traceEntry("Random.negate() oldBase: {}", base);

        if (toNegate && !negated) {
            base = base * -1;
            base = base - mBonus;
            base = base - (dice * (sides + 1));
            toNegate = false;
            negated = true;
        }

        // if (debug) logger.traceExit("Random.negate() newBase: {}", base);
    }

    /**
     * Return a single line showing the values of the members of this object
     * @return A string showing the values of the members of this object
     */
    @CheckReturnValue
    @Contract(pure = true)
    @TestOnly
    @Override
    public String toString() {
        return "Random{" +
                "negated=" + negated +
                ", toNegate=" + toNegate +
                ", mBonus=" + mBonus +
                ", sides=" + sides +
                ", dice=" + dice +
                ", base=" + base +
                '}';
    }

    /**
     * Calculates a random value based on the level we are on and a damage aspect
     *
     * @param level  The level we are on
     * @param aspect The damage aspect to use
     * @return A random number created by rolling this Random
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int randCalc(int level, DamageAspect aspect) {
        if (aspect == DamageAspect.EXTREMIFY) {
            int min = randCalc(level, DamageAspect.MINIMIZE);
            int max = randCalc(level, DamageAspect.MAXIMIZE);

            return Math.abs(min) > Math.abs(max) ? min : max;
        }

        int damage = RandomValueUtils.damCalc(dice, sides, aspect);
        int bonus = RandomValueUtils.mBonusCalc(mBonus, level, aspect);
        return base + damage + bonus;
    }

    /**
     * Check to see if a test value is valid for this Random
     *
     * @param test the value to check
     * @return true if the value test is within the upper and lower bounds that can be generated by this Random, false
     * otherwise
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isValid(int test) {
        return test >= randCalc(0, DamageAspect.MINIMIZE) && test <= randCalc(0, DamageAspect.MAXIMIZE);
    }

    /**
     * Confirms that this random has more than one possible result
     *
     * @return true if there is more than one value that this random can produce, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean varies() {
        int min = randCalc(0, DamageAspect.MINIMIZE);
        int max = randCalc(0, DamageAspect.MAXIMIZE);
        return min != max;
    }

    /**
     * Determine whether this random has a base or not
     *
     * @return True if the value of base is not zero, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasBase() {
        return base != 0;
    }

    /**
     * Determine whether this random has a die value or not
     *
     * @return true if the value of dice is not zero, false otherwise - note, this should always return true
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasDice() {
        return dice != 0;
    }

    /**
     * Determine whether this random has a die side value or not
     *
     * @return true if the count of dice side is not zero, false otherwise - note, this should always return true
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasSides() {
        return sides != 0;
    }

    /**
     * Determine whether this random has a bonus value or not
     *
     * @return true if the value of dice is not one, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasBonus() {
        return mBonus != 1;
    }
}