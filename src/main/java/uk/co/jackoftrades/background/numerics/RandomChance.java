package uk.co.jackoftrades.background.numerics;

import uk.co.jackoftrades.background.utils.RandomValueUtils;

/**
 * A class representing a random chance of success, such as 12 in 13
 */
public class RandomChance {
    private final int numerator;
    private final int denominator;

    /**
     * Constructor
     *
     * @param numerator   the number X of (a X in Y chance)
     * @param denominator the number Y of (a X in Y chance)
     */
    public RandomChance(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

//    /**
//     * Getter for the numerator
//     * @return the numerator
//     */
//    public int getNumerator() {
//        return numerator;
//    }
//
//    /**
//     * Getter for the denominator
//     * @return the denominator
//     */
//    public int getDenominator() {
//        return denominator;
//    }

    /**
     * Scale a random chance and return an appropriate integer
     *
     * @param scale the scale by which the ratio is multiplied
     * @return a value equivalent to the percentage chance of this random chance times scale being rolled
     */
    public int scale(int scale) {
        return scale * numerator / denominator;
    }

    /**
     * roll a check on this random chance and return true if it succeeds and false if it fails
     *
     * @return true if a check on this random chance succeeds and false if it fails
     */
    public boolean check() {
        return RandomValueUtils.randInt0(denominator) >= denominator - numerator;
    }


}