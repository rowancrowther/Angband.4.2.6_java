package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.parsers.ParserValueString;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserValueStringTest {
    ParserValueString string;
    String value;

    @BeforeEach
    void setUp() {
        value = "Testing string";
        string = new ParserValueString();
    }

    @Test
    void setValue() {
        getValue();
    }

    @Test
    void getValue() {
        boolean worked = true;
        boolean didntWork = false;

        try {
            string.setValue(3);
        } catch (IllegalArgumentException e) {
            didntWork = true;
        }

        try {
            string.setValue(value);
        } catch (IllegalArgumentException e) {
            worked = false;
        }

        final boolean w = worked;
        final boolean d = didntWork;

        assertAll(
                () -> assertTrue(w),
                () -> assertTrue(d),
                () -> assertEquals(value, string.getValue())
        );
    }
}