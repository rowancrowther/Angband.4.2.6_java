package uk.co.jackoftrades.background;

import org.jspecify.annotations.NonNull;

public class Rational {
    private int denominator;
    private int numerator;

    public static Rational zero = new Rational(0, 1);
    public static Rational one = new Rational(1, 1);

    public int getNumerator() {
        return numerator;
    }

    private void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    private void setDenominator(int denominator) {
        this.denominator = denominator;
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
        if (denominator != 0 && other.denominator == 0) return false;
        if (denominator == 0 && other.denominator != 0) return false;

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
            numerator = 0 - numerator;
            denominator = 0 - denominator;
        }
    }

    /**
     * Get the integer part of this i.e. 7, 2 would return 1
     * @return The integer part of this Rational
     */
    public int getIntegerPart()
    {
        return numerator / denominator;
    }

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
}