package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.parsers.ParserValueUInt;

import static org.junit.jupiter.api.Assertions.*;

class ParserValueUIntTest {
    private ParserValueUInt valueUInt;
    private long value;

    @BeforeEach
    void setUp() {
        value = 23L;
        valueUInt = new ParserValueUInt();
    }

    @Test
    void setValue() {
        getValue();
    }

    @Test
    void getValue() {
        boolean falseExceptionThrown = false;
        boolean trueExceptionThrown = true;

        try {
            valueUInt.setValue("abc");
        } catch (IllegalArgumentException e) {
            falseExceptionThrown = true;
        }

        try {
            valueUInt.setValue(value);
        } catch (IllegalArgumentException e) {
            trueExceptionThrown = false;
        }

        final boolean finalFalseTest = falseExceptionThrown;
        final boolean finalTrueTest = trueExceptionThrown;

        assertAll(
                () -> assertTrue(finalFalseTest),
                () -> assertEquals(value, (long)valueUInt.getValue()),
                () -> assertTrue(finalTrueTest)
        );
    }
}
