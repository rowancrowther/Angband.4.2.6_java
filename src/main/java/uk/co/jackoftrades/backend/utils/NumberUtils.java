package uk.co.jackoftrades.backend.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.backend.numerics.Rational;

import java.util.ArrayList;

public abstract class NumberUtils {
    private static final Logger logger = LogManager.getLogger();

    private NumberUtils() {};

    /**
     * Return a + b coerced to be in the range Integer.MIN_VALUE to Integer.MAX_VALUE
     * @param a one of the integers to be added
     * @param b one of the integers to be added
     * @return if Integer.MIN_VALUE <= a + b <= Integer.MAX_VALUE then returns a + b.<br>
     *         if a + b < Integer.MIN_VALUE then returns Integer.MIN_VALUE.<br>
     *         if a + b > Integer.MAX_VALUE then returns Integer.MAX_VALUE.
     */
    public static int addGuardI(int a, int b) {
        if (a < 0)
            return b >= 0 || (b > Integer.MIN_VALUE && a >= Integer.MIN_VALUE - b)
                 ? a + b
                 : Integer.MIN_VALUE;
        return b <= 0 || a <= Integer.MAX_VALUE - b
             ? a + b
             : Integer.MAX_VALUE;
    }

    /**
     * Return a - b coerced to be in the range Integer.MIN_VALUE to Integer.MAX_VALUE
     * @param a the number to subtract from
     * @param b the number to subtract
     * @return a - b provided this falls within the range Integer.MIN_VALUE to Integer.MAX_VALUE, if it is too large
     * then return Integer.MAX_VALUE, and if it is too small return Integer.MIN_VALUE
     */
    public static int subGuardI(int a, int b) {
        if (a < 0) {
            return b <= 0 || a >= Integer.MIN_VALUE + b
                    ? a - b
                    : Integer.MIN_VALUE;
        }

        return b >= 0 || a <= Integer.MAX_VALUE + b
                ? a - b
                : Integer.MAX_VALUE;
    }

    /**
     * Coerce the value of a + b to be in the range -32768 to 32767
     * @param a One of the integers to add
     * @param b One of the integers to add
     * @return If a + b is outside the range -32768 to 32767 then return either -32758 if it is too low, or 32767 if it
     * is too high, otherwise return a + b.
     */
    public static int addGuardI16(int a, int b) {
        int maxValue = 32767;
        int minValue = -32768;

        if (a < 0) {
            return b >= 0 || a >= minValue - b
                    ? a + b
                    : minValue;
        }

        return b <= 0 || a <= maxValue - b
                ? a + b
                : maxValue;
    }

    /**
     * Coerces a - b into the range -32768 to 32767
     * @param a the number to subtract from
     * @param b the number to subtract
     * @return a - b if this falls within the range -32768 to 32767, -32768 if it is too small, or 32767 otherwise
     */
    public static int subGuardI16(int a, int b) {
        int maxValue = 32767;
        int minValue = -32768;

        if (a < 0) {
            return b <= 0 || a >= minValue + b
                    ? a - b
                    : minValue;
        }

        return b >= 0 || a <= maxValue + b
                ? a - b
                : maxValue;
    }

    /**
     * Create a rational based on the first size numbers in the incoming array numbers.
     * <br><br>
     * Original required the result to potentially be rounded to the nearest integer, or the largest number above the
     * mean with a new Rational used to store the fractional part. This just returns a new Rational either where the
     * numerator is divisible by the denominator, or not.
     * @param numbers The ArrayList of numbers to sum to calculate the mean
     * @param size The number of integers in the array numbers to calculate the mean of
     * @return The mean of the first size numbers in the ArrayList numbers as a Rational. If divisible is true then the
     * return value will be one where the numerator is divisible by the denominator, i.e. 4 / 2. If it is false then
     * this requirement will not be there. If size is 0 then a null value is returned. If the size is greater than the
     * size of the ArrayList numbers, then it is changed to be the size of the list.
     */
    @Contract("_, _ -> new")
    public static @NonNull Rational mean(@NotNull ArrayList<Integer> numbers, int size) {
        if (size == 0) {
            return Rational.zero;
        }
        if (size > numbers.size()) size = numbers.size();

        Rational result = Rational.zero;

        for (int index = 0; index < size; index++)
            result = result.add(new Rational(numbers.get(index)));

        return result.div(new Rational(size));
    }

    /**
     * Calculates the variance of a set of integers as a rational number
     *
     * @param numbers  a list of integers
     * @param size     the first n numbers in the list to work out the variance of
     * @param unbiased whether the result is divided by size or size - 1
     * @param ofMean   whether we are dividing by a further size or not
     * @return the variance of the first size numbers in a list
     */
    @Contract("_, _, _, _ -> new")
    public static @NonNull Rational variance(ArrayList<Integer> numbers, int size, boolean unbiased, boolean ofMean)
    {
        // Deal with division by zero errors
        if (size <= 1) return Rational.zero;
        if (size > numbers.size()) size = numbers.size();

        Rational result;

        Rational sumNums = Rational.zero;

        Rational mean = mean(numbers, size);
        // mean to subtract
        Rational minusMean = mean.multi(new Rational(-1));

        for (int index = 0; index < size; index++) {
            int num = numbers.get(index);
            Rational numRational = new Rational(num, 1);
            // subtract the mean
            Rational currentTerm = numRational.add(minusMean);
            currentTerm = currentTerm.multi(currentTerm);
            sumNums = sumNums.add(currentTerm);
        }

        Rational biasedRat = new Rational(size);
        Rational unbiasedRat = new Rational(size - 1);

        if (unbiased)
            result = sumNums.div(unbiasedRat);
        else
            result = sumNums.div(biasedRat);

        if (ofMean)
            result = result.div(new Rational(size));

        return result;
    }
}