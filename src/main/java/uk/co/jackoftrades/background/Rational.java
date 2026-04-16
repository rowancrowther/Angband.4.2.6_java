package uk.co.jackoftrades.background;

public class Rational {
    int numerator;
    int denominator;

    public Rational() {
        numerator = 1;
        denominator = 1;
    }

    public Rational(int numerator) {
        this.numerator = numerator;
        denominator = 1;
    }

    public Rational(int numerator, int denominator) throws IllegalArgumentException {
        this.numerator = numerator;
        if (denominator != 0)
            this.denominator = denominator;
        else {
            this.denominator = 1;
            throw new IllegalArgumentException("Divide by zero not allowed.");
        }
    }
}