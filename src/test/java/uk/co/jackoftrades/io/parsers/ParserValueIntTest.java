package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserValueIntTest {
    private int value;
    private ParserValueInt parser;
    private boolean worked;
    private boolean failed;

    @BeforeEach
    void setUp() {
        value = -1;
        parser = new ParserValueInt();

        worked = true;
        failed = false;
    }

    @Test
    void setValue() {
        getValue();
    }

    @Test
    void getValue() {
        try {
            parser.setValue('v');
        } catch (IllegalArgumentException e) {
            failed = true;
        }

        try {
            parser.setValue(value);
        } catch (IllegalArgumentException e) {
            worked = false;
        }

        assertAll(
                () -> assertTrue(worked),
                () -> assertTrue(failed),
                () -> assertEquals(value, parser.getValue())
        );
    }
}