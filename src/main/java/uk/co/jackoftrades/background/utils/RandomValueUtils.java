package uk.co.jackoftrades.background.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.background.enums.DamageAspect;

import java.util.Random;

public abstract class RandomValueUtils {
    private static final Random random = new Random();
    private static final Logger logger = LogManager.getLogger();

    /**
     * Generates a random unsigned integer number along a uniform distribution of 0 <= X < max - 1
     *
     * @param max the top of the distribution range
     * @return A random number between 0 and max - 1
     */
    public static int randDiv(int max) {
        if (max <= 1) return 0;
        return random.nextInt(0, max);
    }

    /**
     * Generates a random unsigned integer number along a uniform distribution of 0 <= X < x
     *
     * @param max the maximum value we are looking for
     * @return a random number between 0 and max - 1
     */
    public static int randInt0(int max) {
        return randDiv(max);
    }

    /**
     * Generates a random unsigned integer number along a uniform distribution of 0 < X <= x
     *
     * @param max the maximum value we are looking for
     * @return a random number between 1 and max
     */
    public static int randInt1(int max) {
        return randDiv(max) + 1;
    }

    /**
     * Generates a random unsigned number which is between centre - diff <= X < centre + diff
     *
     * @param centre the centre of where in the spread we want the random number to be
     * @param diff   the right and left bounds of the random number
     * @return A random number in the bounds (centre - diff, centre + diff)
     */
    public static int randSpread(int centre, int diff) {
        return centre + randInt0(1 + diff + diff) - diff;
    }

    /**
     * Returns true one time in x, returns false otherwise
     *
     * @param x The value of oneIn we are looking at
     * @return true if randInt0(x) = 0, false otherwise
     */
    public static boolean oneIn(int x) {
        return randInt0(x) == 0;
    }

    /**
     * Set the seed of this RandomValueUtil
     *
     * @param seed the long seed for this RandomValueUtil
     */
    public static void stateInit(long seed) {
        random.setSeed(seed);
    }

    /**
     * Set the seed of this Random to a seed dependent on the current time in milliseconds and the current process id
     */
    public static void stateInit() {
        ProcessHandle current = ProcessHandle.current();
        long currentProcessId = current.pid();

        long seed = System.currentTimeMillis();

        // mutate the seed
        seed = ((seed >> 3) * (currentProcessId << 1));

        stateInit(seed);
    }

    /**
     * Get a random value from a Normal distribution with given mean and standard deviation
     *
     * @param mean The mean of the distribution
     * @param std  The standard deviation of the distribution
     * @return A random value from a normal distribution with mean and standard deviation from incoming parmeters.
     * This uses the Box-Muller algorithm and chooses which of the values to return randomly.
     */
    public static int normal(int mean, int std) {
        double twoPi = Math.PI * 2;

        double random1 = random.nextDouble();
        double random2 = random.nextDouble();

        // Ensure that we don't get a ln(0) error
        while (random1 == 0) random1 = random.nextDouble();

        double theta = twoPi * random2;
        double R = Math.sqrt(-2 * Math.log(random1));

        int result;

        if (oneIn(2))
            result = (int) (R * Math.cos(theta));
        else
            result = (int) (R * Math.sin(theta));

        // convert from standard deviation to one with mean and std based on the incoming parameters
        result = result + mean;
        result = result * std;

        return result;
    }

    /**
     * Calculates a number from a distribution consisting of two normal distribution halves. The lower one has a bound
     * of lower and a standard deviation of stdLower and the upper one has a bound of upper and a standard distribution
     * of stdUpper.
     *
     * @param mean     The mean of the distribution we are looking at
     * @param upper    The upper bound of this distribution
     * @param lower    The lower bound of this distribution
     * @param stdUpper The standard deviation of the upper portion of this distribution
     * @param stdLower The standard deviation of the lower portion of this distribution
     * @return A random number picked from this distribution
     */
    public static int sample(int mean, int upper, int lower, int stdUpper, int stdLower) {
        int result = normal(0, 1000);

        if (result > 0) {
            result = (result * (upper - mean)) / (100 * stdUpper);
        } else if (result < 0) {
            result = (result * (mean - lower)) / (100 * stdLower);
        }

        return mean + result;
    }

    /**
     * Return the result of a roll of numOfDie dice, each with sides sides.
     *
     * @param numOfDie The number of dice to roll
     * @param sides    The sides of each die
     * @return A roll of the form numOfDie d sides
     */
    public static int damRoll(int numOfDie, int sides) {
        if (sides <= 0 || numOfDie <= 0) return 0;

        int roll = 0;
        for (int index = 0; index < numOfDie; index++) {
            roll += randInt1(sides);
        }

        return roll;
    }

    /**
     * Calculates a damage roll based on a given aspect as follows:
     * <br>aspect = MAXIMIZE, return the highest possible roll,
     * <br>aspect = EXTREMIFY, as MAXIMIZE
     * <br>aspect = RANDOMIZE, return a random roll
     * <br>aspect = MINIMIZE, return the lowest possible roll
     * <br>aspect = AVERAGE, return the average of the possible rolls
     *
     * @param number the number of die to roll
     * @param sides  each dies sides
     * @param aspect the aspect of this roll
     * @return a random die roll
     */
    public static int damCalc(int number, int sides, @NonNull DamageAspect aspect) {
        return switch (aspect) {
            case MAXIMIZE -> number * sides;
            case EXTREMIFY -> number * sides;
            case RANDOMIZE -> damRoll(number, sides);
            case MINIMIZE -> number;
            case AVERAGE -> number * (sides + 1) / 2;
        };
    }

    /**
     * Calculates a random integer x such that lowest <= x < highest, from a uniform distribution
     *
     * @param lowest  The lowest possible number to return
     * @param highest The highest possible number to return, less 1 (i.e. x <= highest - 1)
     * @return The random number
     */
    public static int randRange(int lowest, int highest) {
        if (lowest == highest) return lowest;
        if (lowest > highest) {
            logger.error("lowest " + lowest + " should be less or equal to highest " + highest);
            throw new IllegalArgumentException("lowest " + lowest + " should be less or equal to highest " + highest);
        }

        return (lowest + (int) randInt1(1 + highest - lowest));
    }

    /**
     * Perform division, rounding up or down depending on the remainder and chance
     *
     * @param dividend The dividend in the division
     * @param divisor  The divisor in the division
     * @return The quotient of dividend / devisor, rounded up or down randomly
     */
    public static int simulateDivision(int dividend, int divisor) {
        int quotient = dividend / divisor;
        int remainder = dividend % divisor;
        if (randInt0(divisor) < remainder) quotient++;
        return quotient;
    }

    /**
     * Calculate an enchantment bonus for an object.
     *
     * @param max   The maximum value of the bonus
     * @param level The level the object is encountered on
     * @return A random number based on a normal distrubution of mean (max * level / max depth) and
     * standard deviation max / 4 (allow up to 4 standard deviations)
     */
    public static int mBonus(int max, int level) {
        if (level >= GlobalUtils.maxRandDepth) level = GlobalUtils.maxRandDepth - 1;

        int bonus = simulateDivision(max * level, GlobalUtils.maxRandDepth);

        int std = simulateDivision(max, 4);

        int value = normal(bonus, std);

        if (value < 0) return 0;
        return Math.min(value, max);
    }

    /**
     * Create a mBonus based on a damage aspect
     *
     * @param max    the maximum value of the aspect
     * @param level  the level on which the damage takes place
     * @param aspect the level aspect
     * @return a random number based on a normal distribution dependent on the aspect
     */
    public static int mBonusCalc(int max, int level, @NonNull DamageAspect aspect) {
        return switch (aspect) {
            case EXTREMIFY, MAXIMIZE -> max;
            case RANDOMIZE -> mBonus(max, level);
            case MINIMIZE -> 0;
            case AVERAGE -> max * level / GlobalUtils.maxRandDepth;
        };
    }
}