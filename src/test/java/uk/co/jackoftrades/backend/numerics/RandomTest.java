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
 *    Java code copyright (c) Rowan Crowther 2026
 */

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
        int expBase = -1 * (min + max);
        random.negate();
        int newBase = random.getBase();
        random.negate();
        int newBase2 = random.getBase();

        assertAll(
                () -> assertEquals(expBase, newBase),

                // The second negate should not negate again. You should only be able to negate a dice once.
                () -> assertEquals(expBase, newBase2)
        );
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

    @Test
    void isValid() {
        assertAll(
                () -> assertTrue(random.isValid(10)),
                () -> assertFalse(random.isValid(0)),
                () -> assertFalse(random.isValid(20))
        );
    }

    @Test
    void varies() {
        boolean actualResult1 = random.varies();

        random = new Random(0, 0, 1, 1, false);
        boolean actualResult2 = random.varies();

        assertAll(
                () -> assertTrue(actualResult1),
                () -> assertFalse(actualResult2)
        );
    }

    @Test
    void hasBase() {
        boolean actualResult1 = random.hasBase();
        random.setBase(0);
        boolean actualResult2 = random.hasBase();

        assertAll(
                () -> assertTrue(actualResult1),
                () -> assertFalse(actualResult2)
        );
    }

    @Test
    void hasBonus() {
        boolean actualResult1 = random.hasBonus();
        random.setMBonus(0);
        boolean actualResult2 = random.hasBonus();

        assertAll(
                () -> assertTrue(actualResult1),
                () -> assertFalse(actualResult2)
        );
    }

    @Test
    void parseRandom() {
        Random dice1 = Random.parseRandom("8+d6");
        Random dice2 = Random.parseRandom("3+2d8");
        Random dice3 = Random.parseRandom("-1+2*d6");
        dice3.negate();

        assertAll(
                () -> assertEquals(8, dice1.getBase()),
                () -> assertEquals(1, dice1.getMBonus()),
                () -> assertEquals(1, dice1.getDice()),
                () -> assertEquals(6, dice1.getSides()),
                () -> assertEquals(3, dice2.getBase()),
                () -> assertEquals(1, dice2.getMBonus()),
                () -> assertEquals(2, dice2.getDice()),
                () -> assertEquals(8, dice2.getSides()),
                () -> assertEquals(-16, dice3.getBase()), // This could be wrong
                () -> assertEquals(2, dice3.getMBonus()),
                () -> assertEquals(1, dice3.getDice()),
                () -> assertEquals(6, dice3.getSides())
        );
    }
}