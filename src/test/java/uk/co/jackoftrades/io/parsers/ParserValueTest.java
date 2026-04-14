package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.background.Random;

import static org.junit.jupiter.api.Assertions.*;

class ParserValueTest {
    private ParserValue charParserValue;
    private ParserValue intParserValue;
    private ParserValue randomParserValue;
    private ParserValue stringParserValue;
    private ParserValue uIntParserValue;

    private char charValue;
    private int intValue;
    private Random randomValue;
    private String stringValue;
    private long uIntValue;

    @BeforeEach
    void setUp() {
        charValue = 'a';
        intValue = 1;
        randomValue = new Random(2, 5, 8, 9, false);
        stringValue = "lkjsd";
        uIntValue = 12L;

        charParserValue = new ParserValue(charValue);
        intParserValue = new ParserValue(intValue);
        randomParserValue = new ParserValue(randomValue);
        stringParserValue = new ParserValue(stringValue);
        uIntParserValue = new ParserValue(uIntValue);
    }

    @Test
    void getValue() {
        assertAll(
                () -> assertEquals(charValue, (char) charParserValue.getValue()),
                () -> assertEquals(intValue, (int) intParserValue.getValue()),
                () -> assertEquals(randomValue, (Random) randomParserValue.getValue()),
                () -> assertEquals(stringValue, (String) stringParserValue.getValue()),
                () -> assertEquals(uIntValue, (long) uIntParserValue.getValue())
        );
    }

    @Test
    void setValue() {
        boolean charFailed = false;
        try {
            charParserValue.setValue(intValue);
        } catch (IllegalArgumentException e) {
            charFailed = true;
        }

        boolean intFailed = false;
        try {
            intParserValue.setValue(randomValue);
        } catch (IllegalArgumentException e) {
            intFailed = true;
        }

        boolean randomFailed = false;
        try {
            randomParserValue.setValue(stringValue);
        } catch (IllegalArgumentException e) {
            randomFailed = true;
        }

        boolean stringFailed = false;
        try {
            stringParserValue.setValue(uIntValue);
        } catch (IllegalArgumentException e) {
            stringFailed = true;
        }

        boolean uIntFailed = false;
        try {
            uIntParserValue.setValue(stringValue);
        } catch (IllegalArgumentException e) {
            uIntFailed = true;
        }

        boolean finalCharFailed = charFailed;
        boolean finalIntFailed = intFailed;
        boolean finalUIntFailed = uIntFailed;
        boolean finalRandomFailed = randomFailed;
        boolean finalStringFailed = stringFailed;

        charParserValue.setValue(charValue);
        intParserValue.setValue(intValue);
        randomParserValue.setValue(randomValue);
        stringParserValue.setValue(stringValue);
        uIntParserValue.setValue(uIntValue);

        assertAll(
                () -> assertTrue(finalCharFailed),
                () -> assertTrue(finalIntFailed),
                () -> assertTrue(finalRandomFailed),
                () -> assertTrue(finalStringFailed),
                () -> assertTrue(finalUIntFailed),
                () -> assertEquals(charValue, charParserValue.getValue()),
                () -> assertEquals(intValue, intParserValue.getValue()),
                () -> assertEquals(randomValue, randomParserValue.getValue()),
                () -> assertEquals(stringValue, stringParserValue.getValue()),
                () -> assertEquals(uIntValue, uIntParserValue.getValue())
        );
    }
}