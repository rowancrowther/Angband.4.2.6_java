package uk.co.jackoftrades.backend.numerics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomChanceTest {

    @Test
    void scale() {
        RandomChance chance = new RandomChance(12, 13);

        assertAll(
                () -> assertEquals(0, chance.scale(1)),
                () -> assertEquals(15, chance.scale(17))
        );
    }

    @Test
    void check() {
        RandomChance succeed = new RandomChance(100, 100);
        RandomChance fail = new RandomChance(0, 100);

        assertAll(
                () -> assertTrue(succeed.check()),
                () -> assertFalse(fail.check())
        );
    }
}