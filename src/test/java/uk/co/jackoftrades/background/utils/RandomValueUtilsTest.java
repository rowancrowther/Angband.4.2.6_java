package uk.co.jackoftrades.background.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}