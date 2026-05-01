package uk.co.jackoftrades.backend.numerics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.enums.DamageAspect;

import static org.junit.jupiter.api.Assertions.*;

class RandomTest {
    // private final Logger logger = AngbandLogger.getRootLogger();
    private Random random;
    private int base;
    private int mBonus;
    private int dice;
    private int sides;

    @BeforeEach
    void setUp() {
        base = 3;
        mBonus = 2;
        dice = 2;
        sides = 6;
        boolean toNegate = true;
        random = new Random(base, mBonus, dice, sides, toNegate);
    }

    @Test
    void setBase() {
        int expected1 = 12;
        random.setBase(expected1);
        int result1 = random.getBase();

        int expected2 = 0;
        random.setBase(expected2);
        int result2 = random.getBase();

        int expected3 = 0;
        random.setBase(-1);
        int result3 = random.getBase();

        assertAll(
                () -> assertEquals(expected1, result1),
                () -> assertEquals(expected2, result2),
                () -> assertEquals(expected3, result3)
        );
    }

    @Test
    void setDice() {
        int expected1 = 13;
        random.setDice(expected1);
        int result1 = random.getDice();

        int expected2 = 1;
        random.setDice(0);
        int result2 = random.getDice();

        int expected3 = 1;
        random.setDice(-1);
        int result3 = random.getDice();

        assertAll(
                () -> assertEquals(expected1, result1),
                () -> assertEquals(expected2, result2),
                () -> assertEquals(expected3, result3)
        );
    }

    @Test
    void setSides() {
        int expected1 = 12;
        random.setSides(expected1);
        int result1 = random.getSides();

        int expected2 = 1;
        random.setSides(0);
        int result2 = random.getSides();

        int expected3 = 1;
        random.setSides(-1);
        int result3 = random.getSides();

        assertAll(
                () -> assertEquals(expected1, result1),
                () -> assertEquals(expected2, result2),
                () -> assertEquals(expected3, result3)
        );
    }

    @Test
    void setMBonus() {
        int expected1 = 12;
        random.setMBonus(expected1);
        int result1 = random.getMBonus();

        int expected2 = 1;
        random.setMBonus(0);
        int result2 = random.getMBonus();

        int expected3 = 1;
        random.setMBonus(-1);
        int result3 = random.getMBonus();

        assertAll(
                () -> assertEquals(expected1, result1),
                () -> assertEquals(expected2, result2),
                () -> assertEquals(expected3, result3)
        );
    }

    @Test
    void getBase() {
        setBase();
    }

    @Test
    void getDice() {
        setDice();
    }

    @Test
    void getSides() {
        setSides();
    }

    @Test
    void getMBonus() {
        setMBonus();
    }

    @Test
    void negate() {
        int min = base + mBonus * dice;
        int max = base + mBonus * dice * sides;
        int newBase = -1 * (min + max);

        random.negate();

        assertEquals(newBase, random.getBase());
    }

    @Test
    void testToString() {
        String expected = "Base: " + base + "\nMBonus: " + mBonus + "\nDice: " + dice + "\nSides: " + sides;

        assertEquals(expected, random.toString());
    }

    // 3, 2, 2, 6
    @Test
    void randCalc() {
        int result = random.randCalc(1, DamageAspect.RANDOMIZE);

        assertAll(
                () -> assertEquals(17, random.randCalc(1, DamageAspect.MAXIMIZE)),
                () -> assertEquals(17, random.randCalc(1, DamageAspect.EXTREMIFY)),
                () -> assertEquals(5, random.randCalc(1, DamageAspect.MINIMIZE)),
                () -> assertEquals(10, random.randCalc(1, DamageAspect.AVERAGE)),
                () -> assertTrue(result <= 17),
                () -> assertTrue(result >= 5)
        );
    }
}