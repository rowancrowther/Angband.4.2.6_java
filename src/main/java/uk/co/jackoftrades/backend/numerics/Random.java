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

package uk.co.jackoftrades.backend.numerics;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftrades.backend.enums.DamageAspect;
import uk.co.jackoftrades.backend.io.parsers.antlr.randomformatter.RandomFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.randomformatter.RandomFormatterParser;
import uk.co.jackoftrades.backend.utils.RandomValueUtils;

public class Random {
    private int base;
    private int dice;
    private int sides;
    private int mBonus;
    private boolean toNegate;
    private boolean negated;
    private final Logger logger = LogManager.getLogger(Random.class.getName());
    private final boolean debug = false;

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
    public Random(int base, int mBonus, int dice, int sides, boolean toNegate) {
        //if (debug) logger.traceEntry("Random.constructor with values base: {} mBonus: {} dice: {} sides: {} toNegate: {}", base, mBonus, dice, sides, toNegate);
        this.base = base;
        this.dice = dice;
        this.sides = sides;
        this.mBonus = mBonus;
        this.toNegate = toNegate;
        negated = false;
    }

    /**
     * Sets the base value for this Random die. If an invalid value (less than 0) is
     * passed in, the value is set to 0.
     * @param base the lowest value that the Random can roll
     */
    public void setBase(int base) {
        if (base < 0) base = 0;
        this.base = base;
    }

    /**
     * Sets the dice value for this Random die. If an invalid value (less than 1) is
     * passed in, the value is set to 1.
     * @param dice the number of dice to roll to create this Random value
     */
    public void setDice(int dice) {
        if (dice <= 0) dice = 1;
        this.dice = dice;
    }

    /**
     * Sets the sides of the dice to roll for this Random die. If an invalid value (less than 1) is
     * passed in, the value is set to 1.
     * @param sides the type of dice to roll, i.e. d4, d6, d5, etc.
     */
    public void setSides(int sides) {
        if (sides <= 0) sides = 1;
        this.sides = sides;
    }

    /**
     * Sets the multiplier (m) in the 1+m*2d3. If an invalid value (less than 1) is passed
     * in, the value is set to 1.
     * @param mBonus the multiplier in the dice formula
     */
    public void setMBonus(int mBonus) {
        if (mBonus <= 0) mBonus = 1;
        this.mBonus = mBonus;
    }

    /**
     * Sets this random to be negatable. This should only be called by the test
     *
     * @param toNegate whether this random should be negated or not.
     */
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
    public int getBase() {
        return base;
    }

    /**
     * Returns the number of dice this random rolls
     * @return the number of dice this random rolls
     */
    public int getDice() {
        return dice;
    }

    /**
     * Returns the sides on each of the dice that this Random rolls.
     * @return The sides on each of the dice that this Random rolls.
     */
    public int getSides() {
        return sides;
    }

    /**
     * Gets the multiplier for this dice formula
     * @return the multiplier for this dice formula
     */
    public int getMBonus() {
        return mBonus;
    }

    /**
     * Negates this random providing the Random.toNegate is set to true, and the
     * random has not already been negated. This calculates a new base value based
     * on the entire value rolled being negated
     */
    public void negate() {
        //if (debug) logger.traceEntry("Random.negate() oldBase: {}", base);

        if (toNegate && !negated) {
            int min = base + mBonus * dice;
            int max = base + mBonus * dice * sides;
            base = -(min + max);
            toNegate = false;
            negated = true;
        }

        // if (debug) logger.traceExit("Random.negate() newBase: {}", base);
    }

    /**
     * Returns a readable string format - put in for debug purposes
     * @return A string consisting of all the four integers preceded by their
     * variable names.
     */
    @TestOnly
    @Override
    public String toString() {
        return "Base: " + base + "\nMBonus: " + mBonus + "\nDice: " + dice + "\nSides: " + sides;
    }

    /**
     * Calculates a random value based on the level we are on and a damage aspect
     *
     * @param level  The level we are on
     * @param aspect The damage aspect to use
     * @return A random number created by rolling this Random
     */
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
    public boolean isValid(int test) {
        return test >= randCalc(0, DamageAspect.MINIMIZE) && test <= randCalc(0, DamageAspect.MAXIMIZE);
    }

    /**
     * Confirms that this random has more than one possible result
     *
     * @return true if there is more than one value that this random can produce, false otherwise
     */
    public boolean varies() {
        return randCalc(0, DamageAspect.MINIMIZE) != randCalc(0, DamageAspect.MAXIMIZE);
    }

    /**
     * Determine whether this random has a base or not
     *
     * @return True if the value of base is not zero, false otherwise
     */
    public boolean hasBase() {
        return base != 0;
    }

    /**
     * Determine whether this random has a die value or not
     *
     * @return true if the value of dice is not zero, false otherwise - note, this should always return true
     */
    public boolean hasDice() {
        return dice != 0;
    }

    /**
     * Determine whether this random has a die side value or not
     *
     * @return true if the count of dice side is not zero, false otherwise - note, this should always return true
     */
    public boolean hasSides() {
        return sides != 0;
    }

    /**
     * Determine whether this random has a bonus value or not
     *
     * @return true if the value of dice is not one, false otherwise
     */
    public boolean hasBonus() {
        return mBonus != 1;
    }

    /**
     * Parse a String and return a Random object
     *
     * @param string the string we are going to parse
     * @return a Random object based on the incoming string
     */
    @CheckReturnValue
    @Contract(pure = true)
    public static Random parseRandom(String string) {
        CharStream stream = CharStreams.fromString(string);
        RandomFormatterLexer lexer = new RandomFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RandomFormatterParser parser = new RandomFormatterParser(tokens);
        RandomFormatterParser.RandomContext result = parser.random();

        return result.randomDice;
    }


}