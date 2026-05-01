package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.parsers.ParserValueRandom;
import uk.co.jackoftrades.backend.numerics.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserValueRandomTest {
    private ParserValueRandom random;
    private Random value;

    @BeforeEach
    void setUp() {
        value = new Random(2, 3, 4, 1, true);
        random = new ParserValueRandom();
    }

    @Test
    void setValue() {
        getValue();
    }

    @Test
    void getValue() {
        boolean worked = true;
        boolean failed = false;

        try {
            random.setValue("This should fail");
        } catch (IllegalArgumentException e) {
            failed = true;
        }

        try {
            random.setValue(value);
        } catch (IllegalArgumentException e) {
            worked = false;
        }

        final boolean finalWorked = worked;
        final boolean finalFailed = failed;

        assertAll(
                () -> assertTrue(finalWorked),
                () -> assertTrue(finalFailed),
                () -> assertEquals(value, random.getValue())
        );
    }
}