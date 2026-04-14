package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserValueCharTest {
    private boolean worked;
    private boolean failed;
    private ParserValueChar parser;
    private char value;

    @BeforeEach
    void setUp() {
        worked = true;
        failed = false;
        parser = new ParserValueChar();
    }

    @Test
    void setValue() {
        getValue();
    }

    @Test
    void getValue() {
        try {
            parser.setValue("a");
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