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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Objects;

/**
 * An exact fraction (numerator / denominator) kept in lowest terms, used where
 * the game needs precise fractional arithmetic without floating-point drift —
 * for example accumulating fractional regeneration or growth rates that must
 * stay exact over many turns. Fractions are simplified on construction so that
 * equal values share a single canonical representation and never balloon into
 * huge numerator/denominator pairs.
 *
 * <p>This is the Java form of C's {@code struct my_rational} ({@code z-util.h}). The constructors stand in for
 * {@code my_rational_construct}, {@link #multi} for {@code my_rational_product}, {@link #add} for
 * {@code my_rational_sum}, {@link #toUint} (with {@link #getRemainder} for the remainder out-parameter) for
 * {@code my_rational_to_uint}, and the private {@link #getGCD} for {@code gcd}, all in {@code z-util.c}.
 * {@link #div}, {@link #sub}, both {@link #getIntegerPart} overloads, {@link #getRemainder}, {@link #equals},
 * {@link #hashCode} and {@link #toString} have no C counterpart.
 *
 * <p><b>Accepted deviation (2026-10-14, recorded in {@code docs/Accepted_Deviations.md}):</b> C holds two unsigned
 * 32-bit ints and saturates at {@code UINT_MAX}; this class holds two signed longs and saturates at
 * {@link Long#MAX_VALUE}. The approximation algorithm used when an exact result will not fit is the same, run at the
 * wider boundary, so results agree with C exactly while every numerator and denominator stays below 2^32 and differ
 * above it ({@code 4294967295 + 1} is {@code 4294967296/1} here and {@code 4294967295/1} in C). Negative values and
 * negative denominators are accepted, which C cannot represent. C's callers work on dice, counts and turn figures far
 * below 2^32, so none of this is reachable from the game.
 *
 * <p>One edge is shared with C: when an approximated result would be exactly the maximum with a rounding bit still to
 * add, C's {@code assert(rn < UINT_MAX)} fires and {@link #multi} throws an {@link ArithmeticException}.
 *
 * <p>Class Rational coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class Rational {
    /**
     * The denominator, C's {@code my_rational.d}. Never zero; positive and coprime with the numerator after
     * {@link #simplify()}. Only a rational built with simplification suppressed can hold a raw pair, including a
     * negative denominator.
     *
     * <p>Field denominator coded before 261014, commented in full on 261014.
     */
    private long denominator;
    /**
     * The numerator, C's {@code my_rational.n}. Carries the sign of the fraction after {@link #simplify()}, which C
     * cannot, as its numerator is unsigned. A simplified zero is stored as {@code 0/1}, as
     * {@code my_rational_construct} stores it.
     *
     * <p>Field numerator coded before 261014, commented in full on 261014.
     */
    private long numerator;

    /**
     * The shared value {@code 0/1}, standing in for C's {@code my_rational_construct(0, 1)}. {@link #multi} returns
     * this very instance when either operand is zero, so it is shared by every caller; nothing mutates it, because
     * {@link #simplify()} leaves {@code 0/1} unchanged. The field is not {@code final}.
     *
     * <p>Field zero coded before 261014, commented in full on 261014.
     */
    public static Rational zero = new Rational(0, 1);

    /**
     * The shared value {@code 1/1}, standing in for C's {@code my_rational_construct(1, 1)}, which
     * {@code sum_o_criticals} in {@code obj-info.c} uses as its starting chance. The field is not {@code final}.
     *
     * <p>Field one coded before 261014, commented in full on 261014.
     */
    public static Rational one = new Rational(1, 1);

    /**
     * Reads the numerator. C reads {@code my_rational.n} directly. This is the reduced numerator, so it carries the
     * sign of the fraction.
     *
     * <p>Function getNumerator coded before 261014, commented in full on 261014.
     *
     * @return the numerator
     */
    @CheckReturnValue
    @Contract(pure = true)
    public long getNumerator() {
        return numerator;
    }

    /**
     * Reads the denominator. C reads {@code my_rational.d} directly. This is the reduced denominator, so it is
     * positive unless the rational was built with simplification suppressed.
     *
     * <p>Function getDenominator coded before 261014, commented in full on 261014.
     *
     * @return the denominator
     */
    @Contract(pure = true)
    @CheckReturnValue
    public long getDenominator() {
        return denominator;
    }

    /**
     * Builds the whole number {@code numerator/1}. There is nothing to reduce, so it does not simplify. C has no
     * one-argument form; the nearest is {@code my_rational_construct(n, 1)}, which also leaves the pair as it is.
     *
     * <p>Function Rational(long) coded before 261014, commented in full on 261014.
     *
     * @param numerator the value of the numerator for this Rational
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Rational(long numerator) {
        this.numerator = numerator;
        denominator = 1;
    }

    /**
     * Multiplies this by another rational and returns the product in lowest terms. This is C's
     * {@code my_rational_product} ({@code z-util.c}): the numerator of each side is cancelled against the denominator
     * of the other before multiplying, and a zero on either side gives the shared {@link #zero}.
     *
     * <p>The signs are taken off first and put back on the product, because C's values are unsigned. When the
     * cancelled numerator and denominator both fit in a {@code long} the product is exact. Otherwise it is
     * approximated as C does: the whole part of the quotient is kept, the widest denominator that still fits is
     * chosen for the fractional part, and a rounding step adds one when the remaining bits say the fraction is at
     * least a half. If the whole part alone would not fit, the result saturates at {@link Long#MAX_VALUE}{@code /1}
     * with the sign of the product. C does the same at {@code UINT_MAX}; see the class comment for the accepted
     * deviation. Where the whole part is exactly {@link Long#MAX_VALUE} and the rounding step then adds one, C asserts
     * and this throws an {@link ArithmeticException}.
     *
     * <p>Function multi coded before 261014, commented in full on 261014.
     *
     * @param rational the rational to multiply this by
     * @return a simplified version of the rational of this * rational
     */
    @Contract(pure = true)
    @CheckReturnValue
    public Rational multi(@NotNull Rational rational) {
        long thisSign = getNumerator() >= 0 ? 1 : -1;
        long otherSign = rational.getNumerator() >= 0 ? 1 : -1;
        long resultSign = thisSign * otherSign;
        
        long absThisNumerator = this.getNumerator() * thisSign;
        long thisDenominator = getDenominator();
        long absOtherNumerator = rational.getNumerator() * otherSign;
        long otherDenominator = rational.getDenominator();
        
        if (absThisNumerator == 0 || absOtherNumerator == 0)
            return zero;
        
        // cross cancel
        long gcd1 = Math.abs(getGCD(absThisNumerator, otherDenominator));
        long gcd2 = Math.abs(getGCD(absOtherNumerator, thisDenominator));
        
        long thisNumeratorReduced = absThisNumerator / gcd1;
        long otherDenominatorReduced = otherDenominator / gcd1;
        long otherNumeratorReduced = absOtherNumerator / gcd2;
        long thisDenominatorReduced = thisDenominator / gcd2;
        
        BigInteger numerator = BigInteger.valueOf(thisNumeratorReduced)
                .multiply(BigInteger.valueOf(otherNumeratorReduced));
        BigInteger denominator = BigInteger.valueOf(thisDenominatorReduced)
                .multiply(BigInteger.valueOf(otherDenominatorReduced));
        
        // Check fit
        if (numerator.bitLength() <= 63 && denominator.bitLength() <= 63) {
            long longNumerator = numerator.longValueExact();
            long longDenominator = denominator.longValueExact();
            return new Rational(longNumerator * resultSign, longDenominator);
        }
        
        // Approximate
        BigInteger [] quotientAndRemainder = numerator.divideAndRemainder(denominator);
        
        BigInteger quotient = quotientAndRemainder[0];
        BigInteger remainder = quotientAndRemainder[1];
        
        if (quotient.bitLength() > 63) 
            return new Rational(resultSign > 0 ? Long.MAX_VALUE : -Long.MAX_VALUE, 1);
        
        BigInteger max = BigInteger.valueOf(Long.MAX_VALUE); 
        
        BigInteger remainderDenominator = ((quotient.compareTo(max) < 0)
                ? max.divide(quotient.add(BigInteger.ONE)) : BigInteger.ONE);
        BigInteger remainderNominator = quotient.multiply(remainderDenominator);
        
        BigInteger [] remainderRemainder = remainder.multiply(remainderDenominator).divideAndRemainder(denominator);
        
        BigInteger t = remainderRemainder[0];
        BigInteger rem = remainderRemainder[1];
        
        remainderNominator = remainderNominator.add(t);
        
        if (rem.bitLength() + 1 >= denominator.bitLength())
            remainderNominator = remainderNominator.add(BigInteger.ONE);
        
        long longNominator = remainderNominator.longValueExact();
        long longDenominator = remainderDenominator.longValueExact();
        
        return new Rational(longNominator * resultSign, longDenominator);
    }

    /**
     * Divides this rational by another by multiplying by its reciprocal. C has no division, so this has no C
     * counterpart; it exists for the Java mean in {@code NumberUtils}.
     *
     * <p>The reciprocal is built with the two-argument constructor, so a zero divisor throws an
     * {@link IllegalArgumentException} there and a negative divisor ends with its sign on the numerator. The product
     * is {@link #multi}, so it saturates as {@link #multi} does. The final {@code simplify()} call re-reduces a result
     * that {@link #multi} has already reduced.
     *
     * <p>Function div coded before 261014, commented in full on 261014.
     *
     * @param other the other rational to divide this one by
     * @return a new rational equal to this / other
     * @throws IllegalArgumentException if {@code other} is zero
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational div(@NotNull Rational other) {
        Rational flipped = new Rational(other.getDenominator(), other.getNumerator());
        Rational result = this.multi(flipped);
        result.simplify();
        return result;
    }

    /**
     * Adds another rational to this one and returns the sum in lowest terms. This is C's {@code my_rational_sum}
     * ({@code z-util.c}): the denominators are divided by their GCD so the common denominator is as small as
     * possible, and the numerator is the cross-multiplied sum.
     *
     * <p>The sign of the numerator is taken off and put back on the result, because C's values are unsigned. When the
     * numerator and denominator both fit in a {@code long} the sum is exact. Otherwise it is approximated as C does:
     * the whole part is kept, the widest fractional denominator that fits is used, and a rounding step adds one when
     * the remaining bits say the fraction is at least a half and the numerator is still below the maximum. A whole
     * part that does not fit saturates at {@link Long#MAX_VALUE}{@code /1}; a fractional part that would push the
     * numerator past the maximum clamps it there. C does the same at {@code UINT_MAX}; see the class comment for the
     * accepted deviation.
     *
     * <p>Function add coded before 261014, commented in full on 261014.
     *
     * @param rational the rational we are adding to this
     * @return a new rational equal to this + rational
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational add(@NotNull Rational rational) {
        BigInteger gcd = BigInteger.valueOf(this.getDenominator()).gcd(BigInteger.valueOf(rational.getDenominator()));
        
        BigInteger thisDenominatorReduced = BigInteger.valueOf(this.getDenominator()).divide(gcd);
        BigInteger otherDenominatorReduced = BigInteger.valueOf(rational.getDenominator()).divide(gcd);
        
        BigInteger numerator = BigInteger.valueOf(this.getNumerator()).multiply(otherDenominatorReduced)
                .add(BigInteger.valueOf(rational.getNumerator()).multiply(thisDenominatorReduced));
        BigInteger denominator = thisDenominatorReduced.multiply(BigInteger.valueOf(rational.getDenominator()));
        long sign = numerator.signum();
        BigInteger absNumerator = BigInteger.valueOf(sign).multiply(numerator);
        
        long longNumerator;
        long longDenominator;
        BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);
        BigInteger resultNumerator;
        BigInteger resultDenominator;
        
        if (absNumerator.bitLength() <= 63 && denominator.bitLength() <= 63) {
            longNumerator = absNumerator.longValueExact();
            longDenominator = denominator.longValueExact();
        } else {
            BigInteger [] quotientRemainder = absNumerator.divideAndRemainder(denominator);
            if (quotientRemainder[0].bitLength() > 63) {
                resultNumerator = max;
                resultDenominator = BigInteger.ONE;
            } else {
                resultNumerator = quotientRemainder[0];
                resultDenominator = resultNumerator.compareTo(max) < 0 
                        ? max.divide(resultNumerator.add(BigInteger.ONE))
                        : BigInteger.ONE;
                resultNumerator = resultNumerator.multiply(resultDenominator);
                
                BigInteger [] tRem = (quotientRemainder[1].multiply(resultDenominator)).divideAndRemainder(denominator);
                
                BigInteger t = tRem[0];
                BigInteger rem = tRem[1];
                
                if (resultNumerator.compareTo(max.subtract(t)) <= 0) {
                    resultNumerator = resultNumerator.add(t);
                    if (rem.bitLength() + 1 >= denominator.bitLength() && resultNumerator.compareTo(max) < 0) {
                        resultNumerator = resultNumerator.add(BigInteger.ONE);
                    }
                } else {
                        resultNumerator = max;
                }
            }
            
            longNumerator = resultNumerator.longValueExact();
            longDenominator = resultDenominator.longValueExact();
        }
        
        return new Rational(sign * longNumerator, longDenominator);
    }

    /**
     * Builds {@code numerator/denominator} in lowest terms. This is C's {@code my_rational_construct}
     * ({@code z-util.c}): the pair is divided by its GCD and a zero numerator is stored as {@code 0/1}.
     *
     * <p>Two things differ from C. A zero denominator throws an {@link IllegalArgumentException} where C asserts
     * {@code denominator > 0}, and a negative numerator or denominator is accepted, the sign ending on the numerator.
     * It delegates to the three-argument constructor with simplification on.
     *
     * <p>Function Rational(long, long) coded before 261014, commented in full on 261014.
     *
     * @param numerator   the numerator of the new Rational
     * @param denominator the denominator of the new Rational
     * @throws IllegalArgumentException if the denominator is 0
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Rational(long numerator, long denominator) {
        this(numerator, denominator, true);
    }

    /**
     * Builds {@code numerator/denominator}, reducing it only when asked. With {@code simplify} true this is the
     * two-argument constructor. With it false the raw pair is kept as given, which has no C counterpart: C's
     * {@code my_rational_construct} always reduces. {@link #equals} and {@link #hashCode} compare by value, so an
     * unreduced rational still equals its reduced form, and {@link #multi} and {@link #add} give the correct reduced
     * result from a raw pair, including one with a negative denominator.
     *
     * <p>Function Rational(long, long, boolean) coded before 261014, commented in full on 261014.
     *
     * @param numerator   the numerator of the Rational
     * @param denominator the denominator of the Rational
     * @param simplify    whether this Rational should be simplified before being stored
     * @throws IllegalArgumentException if the denominator is 0 an illegal argument exception is thrown
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public Rational(long numerator, long denominator, boolean simplify) throws IllegalArgumentException {
        if (denominator == 0) throw new IllegalArgumentException("Divide by zero not allowed.");

        this.numerator = numerator;
        this.denominator = denominator;

        if (simplify) this.simplify();
    }

    /**
     * Compares this rational with another by <em>value</em> rather than by representation, so
     * {@code 1/2} equals {@code 2/4}. Both sides are simplified into throwaway copies before
     * their numerators and denominators are compared, which is what lets two rationals that were
     * never simplified in place still compare equal.
     *
     * <p>The zero-denominator branches are defensive and currently unreachable: the three-argument
     * constructor rejects a zero denominator outright, the one-argument constructor fixes the
     * denominator at 1, there are no setters, and {@link #simplify()} divides by a GCD that is
     * itself never zero. They are kept because they are cheap and because they pair with the same
     * guard in {@link #multi} and {@link #hashCode()} — but a zero-denominator {@code Rational}
     * cannot be built, so nothing exercises them.
     *
     * <p>C has no equality test for {@code struct my_rational}; this method has no C counterpart.
     *
     * <p>Function equals coded before 261014, commented in full on 261014.
     *
     * @param obj the object to compare against
     * @return {@code true} if {@code obj} is a {@link Rational} of the same value
     */
    @Contract(value = "null -> false")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Rational other) {

            if (denominator == 0 && other.getDenominator() == 0) return true;
            if (other.getDenominator() == 0 || denominator == 0) return false;

            Rational thisRational = new Rational(this.numerator, this.denominator, true);
            Rational objRational = new Rational(other.getNumerator(), (other.getDenominator()), true);

            return thisRational.numerator == objRational.numerator && thisRational.denominator == objRational.denominator;
        }

        return false;
    }

    /**
     * Calculates the greatest common divisor of two numbers with Euclid's algorithm. This is C's {@code gcd}
     * ({@code z-util.c}), which is the iterative form on unsigned ints; this one recurses on signed longs.
     *
     * <p>The result is zero only when both arguments are, and it takes the sign Euclid's remainders give it, so it
     * can be negative when an argument is: {@code getGCD(-1, 2)} is {@code -1}. {@link #simplify()} relies on that
     * sign to bring the signs of the pair together, and {@link #multi} takes {@link Math#abs} of it.
     *
     * <p>Function getGCD coded before 261014, commented in full on 261014.
     *
     * @param a a number to calculate the GCD of
     * @param b another number to calculate the GCD of
     * @return the greatest common divisor of a and b
     */
    @Contract(pure = true)
    @CheckReturnValue
    private long getGCD(long a, long b) {
        return (b == 0)
                ? a : getGCD(b, a % b);
    }

    /**
     * Reduces this in place to lowest terms, the work C's {@code my_rational_construct} does with one {@code gcd}.
     * It splits off the whole part, divides the fractional remainder and the denominator by their GCD, and puts the
     * whole part back, so an improper fraction such as {@code 7/2} stays improper.
     *
     * <p>The signs are then brought together: a negative denominator with a positive numerator is flipped so that the
     * sign of the fraction is on the numerator. A zero numerator reduces to {@code 0/1}, as in C. This keeps a value
     * such as {@code 1/2} from growing into a numerator and denominator of a hundred digits.
     *
     * <p>Function simplify coded before 261014, commented in full on 261014.
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    private void simplify() {
        // do the calculation
        long whole = numerator / denominator;
        numerator = numerator - whole * denominator;
        long gcd = getGCD(numerator, denominator);

        numerator = (numerator + whole * denominator) / gcd;
        denominator = denominator / gcd;

        // Flip any negative sign to the numerator
        if (denominator < 0 && numerator > 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    /**
     * Returns the whole part of this fraction, truncated towards zero: {@code 7/2} gives 3 and {@code -7/2} gives -3.
     * C has no such method; the nearest is the first step of {@code my_rational_to_uint}, {@code a->n / a->d}.
     *
     * <p>Function getIntegerPart coded before 261014, commented in full on 261014.
     *
     * @return The integer part of this Rational
     */
    @CheckReturnValue
    @Contract(pure = true)
    public long getIntegerPart()
    {
        return getIntegerPart(1);
    }

    /**
     * Returns the whole part of this fraction after it has been multiplied by {@code scale}, truncated towards zero:
     * {@code 7/2} scaled by 100 gives 350 and {@code 1/3} scaled by 100 gives 33.
     *
     * <p>The multiplication is a plain {@code long} product of the numerator and the scale, with no overflow check and
     * no saturation, so a numerator near {@link Long#MAX_VALUE} wraps. {@link #toUint} goes through {@link #multi}
     * instead, which does saturate. C has no counterpart.
     *
     * <p>Function getIntegerPart(int) coded before 261014, commented in full on 261014.
     *
     * @param scale The integer to scale this rational by
     * @return The integer part of this rational, once it has been scaled
     */
    @CheckReturnValue
    @Contract(pure = true)
    public long getIntegerPart(int scale) {
        Rational scaled = new Rational(numerator * scale, denominator, true);
        return scaled.getNumerator() / scaled.getDenominator();
    }

    /**
     * Subtracts another rational from this one, as {@code this + (other * -1)}. C has no subtraction, as its values
     * are unsigned, so this has no C counterpart. It inherits {@link #multi} and {@link #add}'s saturation and their
     * approximation above {@link Long#MAX_VALUE}.
     *
     * <p>Function sub coded before 261014, commented in full on 261014.
     *
     * @param other The rational to subtract
     * @return A new Rational consisting of this - other
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational sub(@NotNull Rational other) {
        return this.add(other.multi(new Rational(-1)));
    }

    /**
     * Returns what is left of this fraction once its whole part has been taken off, carrying the sign of this:
     * {@code 7/2} gives {@code 1/2} and {@code -7/2} gives {@code -1/2}.
     *
     * <p>This is how Java gets the remainder that C's {@code my_rational_to_uint} hands back through its
     * {@code remainder} out-parameter: call {@code multi(new Rational(scale)).getRemainder()}, which is the remainder
     * of the scaled value, not of this. C returns it as a numerator over the original denominator, unreduced, and its
     * callers reduce it with {@code my_rational_construct}; here the value comes back already reduced, so
     * {@code 1/6} scaled by 3 is {@code 3} over 6 in C and {@code 1/2} here.
     *
     * <p>Function getRemainder coded before 261014, commented in full on 261014.
     *
     * @return The remainder of the rational
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Rational getRemainder() {
        return this.sub(new Rational(getIntegerPart()));
    }

    /**
     * Writes this rational as {@code numerator / denominator} with a space either side of the slash, for example
     * {@code 1 / 2}, from the stored pair as it stands. C has no counterpart.
     *
     * <p>Function toString coded before 261014, commented in full on 261014.
     *
     * @return a string representation of this Rational
     */
    @Override
    @CheckReturnValue
    @Contract(pure = true)
    public String toString() {
        return numerator + " / " + denominator;
    }

    /**
     * Scales this rational and returns the whole part of the result, standing in for C's
     * {@code my_rational_to_uint} ({@code z-util.c}): {@code 7/2} scaled by 100 is 350, {@code 1/3} scaled by 100 is
     * 33, and a scale of 0 is 0. The scaling is {@link #multi}, so a result too large for a {@code long} saturates
     * there.
     *
     * <p>Three things differ from C. There is no {@code remainder} out-parameter; use
     * {@code multi(new Rational(scale)).getRemainder()} for it. The result is a signed {@code long} that does not
     * saturate at {@code UINT_MAX}, so {@code 4294967295/1} scaled by 2 is 8589934590 here and 4294967295 in C (the
     * accepted deviation in the class comment). And a negative scale or a negative rational is accepted, the whole
     * part truncating towards zero. The name keeps C's {@code uint} though the result is signed.
     *
     * <p>Function toUint coded before 261014, commented in full on 261014.
     *
     * @param scale The integer to scale the rational by
     * @return the integer value of the rational times the scale
     */
    @Contract(pure = true)
    @CheckReturnValue
    public long toUint(int scale) {
        return multi(new Rational(scale)).getIntegerPart();
    }

    /**
     * Hashes this rational by <em>value</em>, so that any two rationals {@link #equals} calls equal
     * hash alike — {@code 1/2} and {@code 2/4} included.
     *
     * <p>The hash is taken from a throwaway simplified copy rather than from the fields directly,
     * which is the whole point of the method: a rational built with simplification suppressed
     * ({@code new Rational(2, 4, false)}) keeps its raw pair, so hashing the fields would give it a
     * different hash from the {@code 1/2} it compares equal to. Building the copy the same way
     * {@link #equals} does keeps the two consistent by construction, including any future change to
     * {@link #simplify()}, its sign handling, or {@link #getGCD}.
     *
     * <p>The zero-denominator guard mirrors the unreachable one in {@link #equals} and exists for
     * the same reason; see that method for why no such rational can be built. C has no counterpart.
     *
     * <p>Function hashCode coded before 261014, commented in full on 261014.
     *
     * @return a hash consistent with {@link #equals}
     */
    @Override
    public int hashCode() {
        if (denominator == 0) return 0;

        Rational simplified = new Rational(numerator, denominator, true);
        return Objects.hash(simplified.numerator, simplified.denominator);
    }
}