package uk.co.jackoftrades.background;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Random {
    private int base;
    private int dice;
    private int sides;
    private int mBonus;
    private boolean toNegate;
    private boolean negated;
    private final Logger logger = LogManager.getLogger(Random.class.getName());

    /**
     * Constructor: four integer parameters
     * <br><br>
     * Creates a non negated dice of the form:
     * base + mBonus * dice 'd'' dice
     * <br><br>
     * Once this has been created then the dice is negated to deal with negative base values.
     * @param base the base or minimum value that the die can roll. If this is negative then
     *             the dice is marked as to negate
     * @param dice the number of dice to roll
     * @param sides the sides on each die
     * @param mBonus the multiplier of the die roll
     */
    public Random(int base, int mBonus, int dice, int sides, boolean toNegate) {
        logger.traceEntry("Random.constructor with values base: {} mBonus: {} dice: {} sides: {} toNegate: {}", base, mBonus, dice, sides, toNegate);
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
     * Sets this random to be negatable. This should only be called by the test.
     * TODO: Remove this once testing of whole project is complete.
     * @param toNegate whether this random should be negated or not.
     */
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
        logger.traceEntry("Random.negate() oldBase: {}", base);

        if (toNegate && !negated) {
            int min = base + mBonus * dice;
            int max = base + mBonus * dice * sides;
            base = -(min + max);
            toNegate = false;
            negated = true;
        }

        logger.traceExit("Random.negate() newBase: {}", base);
    }

    /**
     * Returns a readable string format - put in for debug purposes
     * TODO: Remove this once testing of whole project is complete
     * @return A string consisting of all the four integers preceded by their
     * variable names.
     */
    @Override
    public String toString() {
        return "Base: " + base + "\nMBonus: " + mBonus + "\nDice: " + dice + "\nSides: " + sides;
    }
}