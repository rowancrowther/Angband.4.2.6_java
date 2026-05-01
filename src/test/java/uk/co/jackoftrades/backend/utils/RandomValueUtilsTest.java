package uk.co.jackoftrades.backend.utils;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.enums.DamageAspect;

import static org.junit.jupiter.api.Assertions.*;

class RandomValueUtilsTest {

    @Test
    void randDiv() {
        int result = RandomValueUtils.randDiv(10);

        assertAll(
                () -> assertTrue(result >= 0),
                () -> assertTrue(result < 9)
        );
    }

    @Test
    void randInt0() {
        int result = RandomValueUtils.randInt0(10);

        assertAll(
                () -> assertTrue(result >= 0),
                () -> assertTrue(result < 9)
        );
    }

    @Test
    void randInt1() {
        int result = RandomValueUtils.randInt1(10);

        assertAll(
                () -> assertTrue(result >= 1),
                () -> assertTrue(result < 10)
        );
    }

    @Test
    void randSpread() {
        int result = RandomValueUtils.randSpread(0, 2);

        assertAll(
                () -> assertTrue(result < 2),
                () -> assertTrue(result >= -2)
        );
    }

    @Test
    void oneIn() {
        assertTrue(RandomValueUtils.oneIn(1));
    }

    @Test
    void stateInit() {
        // Cannot be tested
    }

    @Test
    void normal() {
        // cannot be tested
    }

    @Test
    void sample() {
        int result = RandomValueUtils.sample(0, 25, -35, 4, 15);

        assertAll(
                () -> assertTrue(-35 <= result),
                () -> assertTrue(25 >= result)
        );
    }

    @Test
    void damRoll() {
        int result = RandomValueUtils.damRoll(2, 6);

        assertAll(
                () -> assertTrue(2 <= result),
                () -> assertTrue(12 >= result)
        );
    }

    @Test
    void damCalc() {
        int number = 2;
        int sides = 6;

        int resultExt = RandomValueUtils.damCalc(number, sides, DamageAspect.EXTREMIFY);
        int resultMax = RandomValueUtils.damCalc(number, sides, DamageAspect.MAXIMIZE);
        int resultAvg = RandomValueUtils.damCalc(number, sides, DamageAspect.AVERAGE);
        int resultMin = RandomValueUtils.damCalc(number, sides, DamageAspect.MINIMIZE);
        int resultRnd = RandomValueUtils.damCalc(number, sides, DamageAspect.RANDOMIZE);

        assertAll(
                () -> assertEquals(12, resultExt),
                () -> assertEquals(12, resultMax),
                () -> assertEquals(7, resultAvg),
                () -> assertEquals(2, resultMin),
                () -> assertTrue(2 <= resultRnd),
                () -> assertTrue(12 >= resultRnd)
        );
    }

    @Test
    void randRange() {
        int lowest = 3;
        int falseHighest = 1;
        int highest = 8;

        assertAll(
                () -> assertEquals(RandomValueUtils.randRange(lowest, lowest), lowest),
                () -> assertThrows(IllegalArgumentException.class, () -> {
                    RandomValueUtils.randRange(lowest, falseHighest);
                }),
                () -> assertTrue(lowest <= RandomValueUtils.randRange(lowest, highest)),
                () -> assertTrue(highest > RandomValueUtils.randRange(lowest, highest))
        );
    }

    @Test
    void simulateDivision() {
        int numerator = 7;
        int denominator = 2;
        int result = RandomValueUtils.simulateDivision(numerator, denominator);

        assertAll(
                () -> assertTrue(3 <= result),
                () -> assertTrue(4 >= result)
        );
    }

    @Test
    void mBonus() {
        int max = 16;
        int result = RandomValueUtils.mBonus(max, 1);
        assertAll(
                () -> assertTrue(result >= 0),
                () -> assertTrue(result <= max)
        );
    }

    @Test
    void mBonusCalc() {
        int max = 18;
        int level = 128;
        int maxRes = 18;
        int extRes = maxRes;
        int rndRes = RandomValueUtils.mBonusCalc(maxRes, level, DamageAspect.RANDOMIZE);
        int minRes = 0;
        int avgRes = 18 * level / GlobalUtils.maxRandDepth;

        assertAll(
                () -> assertEquals(extRes, RandomValueUtils.mBonusCalc(maxRes, level, DamageAspect.EXTREMIFY)),
                () -> assertEquals(maxRes, RandomValueUtils.mBonusCalc(maxRes, level, DamageAspect.MAXIMIZE)),
                () -> assertEquals(avgRes, RandomValueUtils.mBonusCalc(maxRes, level, DamageAspect.AVERAGE)),
                () -> assertEquals(minRes, RandomValueUtils.mBonusCalc(maxRes, level, DamageAspect.MINIMIZE)),
                () -> assertTrue(rndRes >= 0),
                () -> assertTrue(rndRes <= maxRes)
        );
    }
}