package uk.co.jackoftrades.background.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void plural() {
        String expectedSingular = "";
        String expectedPlural = "s";

        assertAll(
                () -> assertEquals(expectedSingular, StringUtils.plural(1)),
                () -> assertEquals(expectedPlural, StringUtils.plural(2))
        );
    }

    @Test
    void verbAgreement() {
        String singular = "item";
        String plural = "items";

        assertAll(
                () -> assertEquals(singular, StringUtils.verbAgreement(1, singular, plural)),
                () -> assertEquals(plural,   StringUtils.verbAgreement(2, singular, plural))
        );
    }

    @Test
    void clipTo() {
        String toClip = "Clip me.";
        int amount = 4;
        String expected = "Clip";

        assertEquals(expected, StringUtils.clipTo(toClip, amount));
    }

    @Test
    void streq() {
        String same = "This is the same.";
        String alsoSame = "This is the same.";
        String different = "This is different.";

        assertAll(
                () -> assertTrue(StringUtils.streq(same, same)),
                () -> assertTrue(StringUtils.streq(same, alsoSame)),
                () -> assertFalse(StringUtils.streq(same, different))
        );
    }

    @Test
    void isPrint() {
        char nullCharacter = '\0';
        char isPrintable = 'a';
        char isNotPrintable = (char) 10;

        assertAll(
                () -> assertEquals(nullCharacter, StringUtils.isPrint(isNotPrintable)),
                () -> assertEquals(isPrintable, StringUtils.isPrint(isPrintable))
        );
    }

    @Test
    void textSchr() {
        String findCharacter = "abcdefg";
        String notFindCharacter = "abdefg";
        char charToFind = 'c';

        assertAll(
                () -> assertTrue(StringUtils.textSchr(findCharacter, charToFind)),
                () -> assertFalse(StringUtils.textSchr(notFindCharacter, charToFind))
        );
    }

    @Test
    void stringLength() {
        String string = "12345";
        assertEquals(string.length(), StringUtils.stringLength(string));
    }
}