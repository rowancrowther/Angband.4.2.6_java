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

package uk.co.jackoftradesltd.middle.numerics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;

import java.util.Random;

/**
 * Static collection of the game's random-number helpers — the Java port of the
 * standard Angband RNG utilities in the original C source ({@code src/z-rand.c}:
 * {@code randint0}, {@code randint1}, {@code rand_spread}, {@code one_in_},
 * {@code Rand_normal}, {@code damroll}, {@code m_bonus} and friends).
 * <p>
 * Everything is {@code static} around a single shared {@link java.util.Random}
 * so the whole game draws from one reproducible stream, which is what makes
 * seeded runs (and tests via {@link #stateInit(long)}) repeatable.
 *
 * <p>The deliberate divergences from C are these. The JDK generator replaces the WELL1024a and
 * LCRNG pair, so draws never match C's for the same seed. {@link #normal} uses Box-Muller capped
 * at four standard deviations instead of C's 256-entry lookup table. C's fixed-value test mode
 * ({@code rand_fixed}) and its "quick" generator are not ported. Everything else follows
 * {@code z-rand.c} and {@code z-rand.h} clause by clause.
 *
 * <p>Class RandomValueUtils coded before 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
public class RandomValueUtils {
    /**
     * Logger used to report illegal argument ranges before throwing. Only {@link #randRange}
     * logs; the other range checks in this class throw without logging.
     *
     * <p>Field logger coded before 261005, commented in full on 261005.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The depth at which {@link #mBonus} stops growing, the port of the C macro
     * {@code MAX_RAND_DEPTH} in {@code z-rand.h}. It is a literal 128 exactly as in C, not a value
     * read from the loaded world data, so this class never depends on the registries being
     * initialized before first use.
     *
     * <p>Field MAX_RAND_DEPTH coded on 261005, commented in full on 261005.
     */
    private static final int MAX_RAND_DEPTH = 128;

    /**
     * The single shared PRNG backing every helper here, so all randomness comes
     * from one seedable stream. Seeded by {@link #stateInit(long)} or {@link #stateInit()}; until
     * one of those runs it carries the JDK's own default seed.
     *
     * <p>Field random coded before 261005, commented in full on 261005.
     */
    private static final Random random = new Random();

    /**
     * Draws a uniformly distributed integer {@code X} with {@code 0 <= X < max}, the port of C's
     * {@code Rand_div} ({@code z-rand.c}, function {@code Rand_div()}). Every other draw in this
     * class is built on it.
     *
     * <p>The range check comes first, as C's {@code assert(m <= 0x10000000)} does. C's parameter is
     * a {@code uint32_t}, so a negative argument wraps to a value above {@code 0x10000000} and trips
     * that same assert. The port's {@code int} parameter cannot wrap, so both a negative {@code max}
     * and one above {@code 0x10000000} are rejected explicitly; {@code 0x10000000} itself is
     * allowed. Folding a negative into the {@code max <= 1} case instead would return 0 and turn a
     * caller's arithmetic error into a plausible-looking roll.
     *
     * <p>A {@code max} of 0 or 1 then returns 0 without drawing, matching C's {@code if (m <= 1)
     * return (0);}. Both have exactly one possible answer, and call sites pass them freely from
     * data-driven values.
     *
     * <p>The port does not reproduce C's rejection-sampling partition scheme, which exists to strip
     * bias from the WELL and LCRNG generators; the JDK generator is already unbiased. Draws
     * therefore do not match C's for the same seed, and C's {@code rand_fixed} test mode is not
     * ported.
     *
     * <p>Function randDiv coded on 260830, reworked on 261005, commented in full on 261005.
     *
     * @param max the top of the distribution range, exclusive
     * @return A random number between 0 and max - 1, or 0 if {@code max} is 0 or 1
     * @throws IllegalArgumentException if {@code max} is negative or above {@code 0x10000000}
     */
    public static int randDiv(int max) {
        if (max < 0 || max > 0x10000000)
            throw new IllegalArgumentException("max must be less that 0x10000000");
        if (max <= 1) return 0;

        return random.nextInt(0, max);
    }

    /**
     * Draws a uniformly distributed integer {@code X} with {@code 0 <= X < max}, the port of the C
     * macro {@code randint0} ({@code z-rand.h}).
     *
     * <p>C's macro is a bare cast over {@code Rand_div}, and this is the same: it delegates to
     * {@link #randDiv} and inherits its handling of the degenerate bounds (0 and 1 give 0) and of
     * the rejected ones (negative, or above {@code 0x10000000}).
     *
     * <p>Function randInt0 coded before 261005, commented in full on 261005.
     *
     * @param max the maximum value we are looking for, exclusive
     * @return a random number between 0 and max - 1, or 0 if {@code max} is 0 or 1
     * @throws IllegalArgumentException if {@code max} is negative or above {@code 0x10000000}
     */
    public static int randInt0(int max) {
        return randDiv(max);
    }

    /**
     * Draws a uniformly distributed integer {@code X} with {@code 1 <= X <= max}, the port of the C
     * macro {@code randint1} ({@code z-rand.h}).
     *
     * <p>C's macro adds one to {@code Rand_div}, and so does this. The shift rides on top of
     * {@link #randDiv}'s degenerate case, so a {@code max} of 0 or 1 yields 1, which is why a
     * one-sided die always rolls one rather than rolling nothing.
     *
     * <p>Function randInt1 coded before 261005, commented in full on 261005.
     *
     * @param max the maximum value we are looking for, inclusive
     * @return a random number between 1 and max, or 1 if {@code max} is 0 or 1
     * @throws IllegalArgumentException if {@code max} is negative or above {@code 0x10000000}
     */
    public static int randInt1(int max) {
        return randDiv(max) + 1;
    }

    /**
     * Draws a uniformly distributed integer {@code X} with {@code centre - diff <= X <= centre + diff},
     * both ends inclusive, the port of the C macro {@code rand_spread} ({@code z-rand.h}).
     *
     * <p>That is {@code 1 + 2 * diff} equally likely values, so it is the same draw as
     * {@code randRange(centre - diff, centre + diff)}. A {@code diff} of 0 always gives
     * {@code centre}. A negative {@code diff} makes the bound handed to {@link #randInt0}
     * negative, which is rejected there.
     *
     * <p>Function randSpread coded before 261005, commented in full on 261005.
     *
     * @param centre the centre of where in the spread we want the random number to be
     * @param diff   how far either side of {@code centre} the result may fall
     * @return A random number from {@code centre - diff} to {@code centre + diff}, inclusive
     * @throws IllegalArgumentException if {@code diff} is negative
     */
    public static int randSpread(int centre, int diff) {
        return centre + randInt0(1 + diff + diff) - diff;
    }

    /**
     * Returns true one time in {@code x}, the port of the C macro {@code one_in_}
     * ({@code z-rand.h}), which is {@code !randint0(x)}.
     *
     * <p>An {@code x} of 0 or 1 is always true, because {@link #randInt0} gives 0 for both; 2 is a
     * coin flip. A negative {@code x} is rejected by {@link #randDiv}.
     *
     * <p>Function oneIn coded before 261005, commented in full on 261005.
     *
     * @param x The value of oneIn we are looking at
     * @return true if {@code randInt0(x) == 0}, false otherwise
     * @throws IllegalArgumentException if {@code x} is negative or above {@code 0x10000000}
     */
    public static boolean oneIn(int x) {
        return randInt0(x) == 0;
    }

    /**
     * Seeds the shared generator, so every later draw is repeatable. The counterpart of C's
     * {@code Rand_state_init(uint32_t seed)} ({@code z-rand.c}), which fills the WELL1024a state
     * table from the seed.
     *
     * <p>The port calls {@link java.util.Random#setSeed(long)} instead, and takes a {@code long}.
     * The same seed therefore does not reproduce C's sequence, only a repeatable one of its own;
     * that repeatability is what the tests rely on.
     *
     * <p>Function stateInit(long) coded before 261005, commented in full on 261005.
     *
     * @param seed the seed for the shared generator
     */
    public static void stateInit(long seed) {
        random.setSeed(seed);
    }

    /**
     * Seeds the shared generator from the clock and the process id, the port of C's
     * {@code Rand_init()} ({@code z-rand.c}).
     *
     * <p>C takes {@code time(NULL)}, seconds since the epoch, and on Unix mutates it as
     * {@code (seed >> 3) * (getpid() << 1)}. The port keeps that mutation but starts from
     * {@code System.currentTimeMillis()}, so its seeds are about a thousand times finer than C's,
     * and takes the id from {@link ProcessHandle}. C then also seeds its WELL state via
     * {@code Rand_state_init} and switches {@code Rand_quick} off; the port has no quick generator
     * to switch off.
     *
     * <p>Function stateInit() coded before 261005, commented in full on 261005.
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
     * Draws an integer from a normal distribution around {@code mean} with standard deviation
     * {@code std}, the port of C's {@code Rand_normal} ({@code z-rand.c}, function
     * {@code Rand_normal()}).
     *
     * <p>A {@code std} below 1 returns {@code mean} untouched, as C's {@code if (stand < 1) return
     * (mean);} does. Otherwise two uniform doubles are turned into a standard normal value by
     * Box-Muller ({@code R = sqrt(-2 ln u1)}, angle {@code 2 pi u2}), with {@code u1} redrawn while it
     * is 0 to avoid {@code ln(0)}. A coin flip via {@link #oneIn} picks the cosine or the sine of
     * the pair; the other is discarded. That value is clamped to {@code -4..4}, multiplied by
     * {@code std}, truncated toward zero and added to {@code mean}.
     *
     * <p>This is a deliberate divergence from C, which draws an index into a 256-entry table
     * instead and returns {@code mean +/- stand * index / 64}, so its results move in steps of
     * {@code stand / 64} and its largest offset is just under {@code 4 * stand}. The clamp here
     * gives the same four-standard-deviation cap, but at exactly {@code 4 * std}: with {@code std}
     * of 1, C stops at {@code mean +/- 3} and the port can reach {@code mean +/- 4}, on roughly one
     * draw in 16,000. {@code std} is a {@code float} here and an {@code int} in C, and the result
     * is a plain {@code int} where C returns an {@code int16_t}.
     *
     * <p>Function normal coded before 261005, reworked on 261005, commented in full on 261005.
     *
     * @param mean The mean of the distribution
     * @param std  The standard deviation of the distribution; below 1 means no spread
     * @return {@code mean} plus a normally distributed offset of at most four standard deviations
     */
    public static int normal(int mean, float std) {
        if (std < 1) return mean;
                
        double twoPi = Math.PI * 2;

        double random1 = random.nextDouble();
        double random2 = random.nextDouble();

        // Ensure that we don't get a ln(0) error
        while (random1 == 0) random1 = random.nextDouble();

        double theta = twoPi * random2;
        double R = Math.sqrt(-2 * Math.log(random1));

        double result;

        if (oneIn(2))
            result = (R * Math.cos(theta));
        else
            result = (R * Math.sin(theta));

        // convert from standard deviation to one with mean and std based on the incoming parameters
        result = Math.clamp(result, -4, 4);
        return (int) (result * std) + mean;
    }

    /**
     * Picks an integer from a distribution with a known mean and approximate upper and lower
     * bounds, the port of C's {@code Rand_sample} ({@code z-rand.c}, function
     * {@code Rand_sample()}).
     *
     * <p>The imagined distribution is split into two halves at {@code mean}. A value is drawn with
     * {@code normal(0, 1000)}; a positive draw is scaled by {@code (upper - mean) / (100 * stdUpper)},
     * a negative one by {@code (mean - lower) / (100 * stdLower)}, a zero is left alone, and the
     * result is added to {@code mean}. C's comment describes {@code stdUpper} and {@code stdLower}
     * as ten times the number of standard deviations from the mean that the bound is to sit at.
     *
     * <p>Two departures from C. The standard deviations are {@code float} here and {@code int} in
     * C, and the scaling is done in {@code float} with one truncation at the end, where C truncates
     * at each integer division; for exactly representable products the two agree. A zero standard
     * deviation divides by zero, which is undefined in C and gives infinity here. The draw also
     * comes from the Box-Muller {@link #normal}, so its range is {@code -4000..4000} where C's
     * table gives just under {@code +/-3984}.
     *
     * <p>Function sample coded before 261005, reworked on 261005, commented in full on 261005.
     *
     * @param mean     The mean of the distribution we are looking at
     * @param upper    The upper bound of this distribution
     * @param lower    The lower bound of this distribution
     * @param stdUpper Ten times the standard deviations from {@code mean} to {@code upper}
     * @param stdLower Ten times the standard deviations from {@code mean} to {@code lower}
     * @return A random number picked from this distribution
     */
    public static int sample(int mean, int upper, int lower, float stdUpper, float stdLower) {
        float result = normal(0, 1000);
        
        if (result > 0) {
            result = (result * (upper - mean)) / (100 * stdUpper);
        } else if (result < 0) {
            result = (result * (mean - lower)) / (100 * stdLower);
        }

        return mean + (int) result;
    }

    /**
     * Rolls {@code numOfDie} dice of {@code sides} sides each and sums them, the port of C's
     * {@code damroll} ({@code z-rand.c}), the "2d6" style roll.
     *
     * <p>Each die is a {@link #randInt1}, so it lands in {@code 1..sides}. A {@code sides} of 0 or
     * below returns 0, as C does. C has no guard on the dice count because its loop simply runs
     * zero times for a count of 0 or below; the port tests it up front, with the same result.
     *
     * <p>Function damRoll coded before 261005, commented in full on 261005.
     *
     * @param numOfDie The number of dice to roll
     * @param sides    The sides of each die
     * @return A roll of the form numOfDie d sides, or 0 if either is 0 or below
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
     * Resolves a dice expression for a given aspect, the port of C's {@code damcalc}
     * ({@code z-rand.c}). Rather than always rolling, the caller can ask for a fixed summary of
     * the distribution:
     * <br>MAXIMIZE and EXTREMIFY return the highest possible roll, {@code number * sides}
     * <br>RANDOMIZE returns a random roll from {@link #damRoll}
     * <br>MINIMIZE returns the lowest possible roll, {@code number}
     * <br>AVERAGE returns {@code number * (sides + 1) / 2} in integer arithmetic, so 1d4 gives 2
     * and 2d6 gives 7
     *
     * <p>EXTREMIFY is the same as MAXIMIZE here. Choosing between the minimum and maximum for it is
     * done a level up, by {@code randcalc}, which has the whole {@code random_value} to compare.
     * C's closing {@code assert(0)} for an unknown aspect has no counterpart, because the switch
     * covers every {@link DamageAspect} constant.
     *
     * <p>Function damCalc coded before 261005, commented in full on 261005.
     *
     * @param number the number of die to roll
     * @param sides  each dies sides
     * @param aspect the aspect of this roll
     * @return the roll, or the minimum, average or maximum of it, per {@code aspect}
     */
    public static int damCalc(int number, int sides, @NotNull DamageAspect aspect) {
        return switch (aspect) {
            case MAXIMIZE, EXTREMIFY -> number * sides;
            case RANDOMIZE -> damRoll(number, sides);
            case MINIMIZE -> number;
            case AVERAGE -> number * (sides + 1) / 2;
        };
    }

    /**
     * Draws a uniformly distributed integer {@code x} with {@code lowest <= x <= highest}, both ends
     * inclusive, the port of C's {@code rand_range} ({@code z-rand.c}). The draw spans
     * {@code 1 + highest - lowest} values, which is why {@code highest} can itself be returned.
     * {@code randRange(0, N - 1)} is the same draw as {@code randInt0(N)}.
     *
     * <p>Equal bounds return that value without drawing. Where C has
     * {@code assert(A < B)} for {@code lowest > highest}, the port logs the problem and throws.
     *
     * <p>Function randRange coded before 261005, commented in full on 261005.
     *
     * @param lowest  The lowest possible number to return
     * @param highest The highest possible number to return, inclusive
     * @return The random number
     * @throws IllegalArgumentException if {@code lowest} is greater than {@code highest}
     */
    public static int randRange(int lowest, int highest) {
        if (lowest == highest) return lowest;
        if (lowest > highest) {
            String message = "lowest " + lowest + " should be less or equal to highest " + highest;
            IllegalArgumentException e = new IllegalArgumentException(message);
            logger.error(message, e);
            throw e;
        }

        return (lowest + (int) randInt0(1 + highest - lowest));
    }

    /**
     * Divides, rounding up or down at random in proportion to the remainder, so that the average
     * of many results is the exact quotient. The port of C's {@code simulate_division}
     * ({@code z-rand.c}), a {@code static} function there.
     *
     * <p>The integer quotient is rounded up when {@code randInt0(divisor)} is below the remainder,
     * which happens with probability {@code remainder / divisor}. For example 10 / 4 is 2 remainder
     * 2, so it gives 3 half the time and 2 otherwise. An exact division draws and never rounds up.
     * A zero divisor throws {@link ArithmeticException}, where C divides by zero.
     *
     * <p>Function simulateDivision coded before 261005, commented in full on 261005.
     *
     * @param dividend The dividend in the division
     * @param divisor  The divisor in the division
     * @return The quotient of dividend / divisor, rounded up or down randomly
     */
    public static int simulateDivision(int dividend, int divisor) {
        int quotient = dividend / divisor;
        int remainder = dividend % divisor;
        if (randInt0(divisor) < remainder) quotient++;
        return quotient;
    }

    /**
     * Calculates an enchantment bonus for an object, the port of C's {@code m_bonus}
     * ({@code z-rand.c}).
     *
     * <p>The level is first capped at {@code MAX_RAND_DEPTH - 1}. The mean is
     * {@code simulateDivision(max * level, MAX_RAND_DEPTH)}, so it climbs from 0 towards
     * {@code max} as the level reaches the cap, and the standard deviation is
     * {@code simulateDivision(max, 4)}, a quarter of {@code max}, so the four-standard-deviation cap
     * of {@link #normal} spans the whole range. The value comes from {@code normal(mean, std)} and
     * is forced into {@code 0..max}. A {@code max} below 4 can give a standard deviation of 0, which
     * {@link #normal} treats as no spread. The random draws happen in C's order: mean, standard
     * deviation, then the normal value.
     *
     * <p>Because {@link #normal} is Box-Muller rather than C's table, the distribution is the same
     * shape but not the same figures as the sample table in the C comment above {@code m_bonus}.
     * The result is an {@code int} where C returns an {@code int16_t}.
     *
     * <p>Function mBonus coded before 261005, reworked on 261005, commented in full on 261005.
     *
     * @param max   The maximum value of the bonus
     * @param level The level the object is encountered on
     * @return A random number from 0 to {@code max}, normally distributed around
     * {@code max * level / MAX_RAND_DEPTH} with standard deviation {@code max / 4}
     */
    public static int mBonus(int max, int level) {
        int maxRandDepth = MAX_RAND_DEPTH;
        if (level >= maxRandDepth) level = maxRandDepth - 1;

        int bonus = simulateDivision(max * level, maxRandDepth);

        float std = simulateDivision(max, 4);

        int value = normal(bonus, std);

        if (value < 0) return 0;
        return Math.min(value, max);
    }

    /**
     * Resolves the level bonus of a {@code random_value} for a given aspect, the port of C's
     * {@code m_bonus_calc} ({@code z-rand.c}):
     * <br>MAXIMIZE and EXTREMIFY return {@code max}
     * <br>RANDOMIZE returns {@link #mBonus}
     * <br>MINIMIZE returns 0
     * <br>AVERAGE returns {@code max * level / MAX_RAND_DEPTH} in integer arithmetic
     *
     * <p>As in C, the AVERAGE arm does not cap {@code level} the way {@link #mBonus} does, so a
     * level of {@code MAX_RAND_DEPTH} or more gives a result of {@code max} or more.
     *
     * <p>Function mBonusCalc coded before 261005, updated on 261005 to share {@code MAX_RAND_DEPTH}
     * with {@link #mBonus}, commented in full on 261005.
     *
     * @param max    the maximum level bonus of the {@code random_value}
     * @param level  the level on which the bonus is being worked out
     * @param aspect which summary of the bonus to return
     * @return the bonus, or its minimum, average or maximum, per {@code aspect}
     */
    @CheckReturnValue
    public static int mBonusCalc(int max, int level, @NotNull DamageAspect aspect) {
        return switch (aspect) {
            case EXTREMIFY, MAXIMIZE -> max;
            case RANDOMIZE -> mBonus(max, level);
            case MINIMIZE -> 0;
            case AVERAGE -> max * level / MAX_RAND_DEPTH;
        };
    }
}