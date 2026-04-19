package uk.co.jackoftrades.background.numerics;

import org.jspecify.annotations.NonNull;

public class Rational {
    private int denominator;
    private int numerator;

    public static Rational zero = new Rational(0, 1);
    public static Rational one = new Rational(1, 1);

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public Rational(int numerator) {
        this.numerator = numerator;
        denominator = 1;
    }

    public Rational multi(@NonNull Rational rational) {
        if (this.denominator == 0 || rational.denominator == 0)
            return new Rational(0, 1);

        Rational result = new Rational(this.numerator * rational.numerator, this.denominator * rational.denominator);
        result.simplify();
        return result;
    }

    public Rational div(@NonNull Rational other) {
        Rational flipped = new Rational(other.getDenominator(), other.getNumerator());
        Rational result = this.multi(flipped);
        result.simplify();
        return result;
    }

    public Rational add(@NonNull Rational rational) {
        int otherNumerator = rational.getNumerator();
        int otherDenominator = rational.getDenominator();

        Rational result = new Rational(this.numerator * otherDenominator + otherNumerator * this.denominator, otherDenominator * this.denominator);
        result.simplify();
        return result;
    }

    public Rational(int numerator, int denominator) {
        this(numerator, denominator, true);
    }

    public Rational(int numerator, int denominator, boolean simplify) throws IllegalArgumentException {
        if (denominator == 0) throw new IllegalArgumentException("Divide by zero not allowed.");

        this.numerator = numerator;
        this.denominator = denominator;

        if (simplify) this.simplify();
    }

    public boolean equals(@NonNull Rational other) {
        if (denominator == 0 && other.denominator == 0) return true;
        if (other.denominator == 0 || denominator == 0) return false;

        this.simplify();
        other.simplify();

        return this.numerator == other.numerator && this.denominator == other.denominator;
    }

    private long getGCD(long a, long b) {
        return (b == 0)
                ? a : getGCD(b, a % b);
    }

    private void simplify() {
        // do the calculation
        int whole = numerator / denominator;
        numerator = numerator - whole * denominator;
        long gcd = getGCD(numerator, denominator);

        numerator = (int) ((numerator + whole * denominator) / gcd);
        denominator = (int) (denominator / gcd);

        // Flip any negative sign to the numerator
        if (denominator < 0 && numerator > 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    /**
     * Get the integer part of this i.e. 7, 2 would return 1
     * @return The integer part of this Rational
     */
    public int getIntegerPart()
    {
        return getIntegerPart(1);
    }

    /**
     * Return the integer part of this Rational scaled up by an integer
     * @param scale The integer to scale this rational by
     * @return THe integer part of this rational, once it has been scaled
     */
    public int getIntegerPart(int scale) {
        Rational scaled = new Rational(numerator * scale, denominator, true);
        return scaled.getNumerator() / scaled.getDenominator();
    }

    /**
     * Subtract one rational from this rational
     * @param other The rational to subtract
     * @return A new Rational consisting of this - other
     */
    public Rational sub(Rational other) {
        return this.add(other.multi(new Rational(-1)));
    }

    /**
     * Get the remainder of this Rational
     * @return The remainder of the rational
     */
    public Rational getRemainder() {
        return this.sub(new Rational(getIntegerPart()));
    }

    @Override
    public String toString() {
        return numerator + " / " + denominator;
    }

    /**
     * Returns the integer value of the rational multiplied by scale (a) where (a*c+b) = as an integer
     *
     * @param scale The integer to scale the rational by
     * @return the integer value of the rational times the scale
     */
    public int toUint(int scale) {
        return multi(new Rational(scale)).getIntegerPart();
    }
}