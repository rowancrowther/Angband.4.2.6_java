package uk.co.jackoftrades.background.utils;

import java.util.Random;

public abstract class RandomValueUtils {
    /**
     * Generates a random unsigned integer number along a uniform distribution of 0 <= X < max - 1
     *
     * @param max the top of the distribution range
     * @return A random number between 0 and max - 1
     */
    public static int randDiv(int max) {
        if (max <= 1) return 0;
        return new Random().nextInt(0, max - 1);
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
}
