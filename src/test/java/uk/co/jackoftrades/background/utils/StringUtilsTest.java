package uk.co.jackoftrades.background.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {
    private String result;
    private final String toFormat = "This string has $d formatted $s";
    private final String frontString = "This string is at the front. ";
    private final int max = 12;

    @Test
    void strnfcat() {
        result = StringUtils.strnfcat(frontString, toFormat, 2, "objects");
        assertEquals(frontString + String.format(toFormat, 2, "objects"), result);
    }

    @Test
    void vstrnfmt() {
        result = StringUtils.vstrnfmt(max, toFormat, 2, "objects.");
        assertEquals(String.format(toFormat, 2, "objects.").substring(0, max - 1), result);
    }

    @Test
    void vformat() {
        result = StringUtils.vformat(toFormat, 2, "objects.");
        assertEquals(String.format(toFormat, 2, "objects."), result);
    }

    @Test
    void strnfmt() {
        result = StringUtils.strnfmt(toFormat, max, 2, "objects.");
        assertEquals(String.format(toFormat, 2, "objects.").substring(0, max - 1), result);
    }

    @Test
    void format() {
        result = StringUtils.format(toFormat, 2, "objects.");
        assertEquals(String.format(toFormat, 2, "objects."), result);
    }

    @Test
    void plogFmt() {
        // This cannot be tested
    }

    @Test
    void testPlogFmt() {
        // This cannot be tested
    }

    @Test
    void quitFmt() {
        // This cannot be tested at the moment
        // TODO: Test this once we have implemented quit(String)
    }
}