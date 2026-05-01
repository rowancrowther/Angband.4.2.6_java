package uk.co.jackoftrades.backend.numerics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RationalTest {
    private Rational r0;
    private Rational r1;
    private Rational r2;

    @BeforeEach
    void setUp() {
        r0 = Rational.zero;
        r1 = new Rational(26);
        r2 = new Rational(12,13);
    }

    @Test
    void getNumerator() {
        assertAll(
                () -> assertEquals(0, r0.getNumerator()),
                () -> assertEquals(26, r1.getNumerator()),
                () -> assertEquals(12, r2.getNumerator())
        );
    }

    @Test
    void getDenominator() {
        assertAll (
                () -> assertEquals(1, r0.getDenominator()),
                () -> assertEquals(1, r1.getDenominator()),
                () -> assertEquals(13, r2.getDenominator())
        );
    }

    @Test
    void multi() {
        Rational result = r2.multi(r1);

        assertAll(
                () -> assertEquals(24, result.getNumerator()),
                () -> assertEquals(1, result.getDenominator())
        );
    }

    @Test
    void div() {
        Rational result = r2.div(r1);

        assertAll(
                () -> assertEquals(6, result.getNumerator()),
                () -> assertEquals(169, result.getDenominator())
        );
    }

    @Test
    void add() {
        Rational r1plusr2 = r1.add(r2);

        assertAll(
                () -> assertEquals(350, r1plusr2.getNumerator()),
                () -> assertEquals(13, r1plusr2.getDenominator())
        );
    }

    @Test
    void testEquals() {
        assertAll(
                () -> assertTrue(new Rational(12,13).equals(r2)),
                () -> assertFalse(r1.equals(r2))
        );
    }

    @Test
    void getIntegerPart() {
        assertAll(
                () -> assertEquals(0, r0.getIntegerPart()),
                () -> assertEquals(26, r1.getIntegerPart()),
                () -> assertEquals(0, r2.getIntegerPart())
        );
    }

    @Test
    void sub() {
        // r0 - r2
        Rational exp1 = new Rational(-12, 13);
        // r1 - r2
        Rational exp2 = new Rational(326, 13);

        assertAll(
                () -> assertTrue(exp1.equals(r0.sub(r2))),
                () -> assertTrue(exp2.equals(r1.sub(r2)))
        );
    }

    @Test
    void getRemainder() {
        assertAll(
                () -> assertTrue(new Rational(12, 13).equals(r2.getRemainder())),
                () -> assertTrue(Rational.zero.equals(r1.getRemainder())),
                () -> assertTrue(Rational.zero.equals(r0.getRemainder()))
        );
    }

    @Test
    void testGetIntegerPart() {
        int scale = 4;
        Rational rational = new Rational(2, 3, false);
        int expected = 2;

        assertEquals(expected, rational.getIntegerPart(scale));
    }

    @Test
    void testGetIntegerPart1() {
        Rational rational = new Rational(4, 3, false);
        int expected = 1;

        assertEquals(expected, rational.getIntegerPart());
    }

    @Test
    void toUint() {
        Rational rat0 = Rational.zero;
        Rational rat1 = Rational.one;
        Rational rat2 = new Rational(12, 13, false);

        assertAll(
                () -> assertEquals(0, rat0.toUint(100)),
                () -> assertEquals(100, rat1.toUint(100)),
                () -> assertEquals(92, rat2.toUint(100))
        );
    }
}