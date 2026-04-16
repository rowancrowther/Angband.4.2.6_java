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
                () -> assertEquals(plural, StringUtils.verbAgreement(2, singular, plural))
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
        char isPrintable = 'a';
        char isNotPrintable = (char) 10;

        assertAll(
                () -> assertFalse(StringUtils.isPrint(isNotPrintable)),
                () -> assertTrue(StringUtils.isPrint(isPrintable))
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

    @Test
    void strICmp() {
        String first = "ABc";
        String second = "aBd";

        assertAll(
                () -> assertEquals(-1, StringUtils.strICmp(first, second)),
                () -> assertEquals(0, StringUtils.strICmp(first, first)),
                () -> assertEquals(1, StringUtils.strICmp(second, first))
        );
    }

    @Test
    void strNICmp() {
        String first = "aBC";
        String second = "ab";
        String third = "AC";
        int number = 2;

        assertAll(
                () -> assertEquals(0, StringUtils.strNICmp(first, second, number)),
                () -> assertEquals(-1, StringUtils.strNICmp(second, third, number)),
                () -> assertEquals(1, StringUtils.strNICmp(third, first, number)),
                () -> assertEquals(0, StringUtils.strNICmp(first, second, 10))
        );
    }

    @Test
    void strIStr() {
        String toSearch = "Have fUn with StrinGs!";
        String toFind = "FuN";
        String toNotFind = "Count";
        String resultFound = "fUn with StrinGs!";
        String resultNotFound = "Count";

        assertAll(
                () -> assertEquals(resultFound, StringUtils.strIStr(toSearch, toFind)),
                () -> assertEquals(resultNotFound, StringUtils.strIStr(toSearch, toNotFind))
        );
    }

    @Test
    void strCpy() {
        String source = "abcd";
        String result1 = "ab";
        String result2 = "";

        assertAll(
                () -> assertEquals(result1, StringUtils.strCpy(source, 2)),
                () -> assertEquals(result2, StringUtils.strCpy(source, 0)),
                () -> assertEquals(source, StringUtils.strCpy(source, 6)),
                () -> assertEquals(result2, StringUtils.strCpy(source, -1))
        );
    }

    @Test
    void strCat() {
        String start = "ab";
        String finish = "cd";
        int length1 = 4;
        String result1 = "abcd";
        int length2 = 3;
        String result2 = "abc";
        int length3 = 1;
        String result3 = "a";
        int length4 = -1;
        String result4 = "";
        int length5 = 5;
        String result5 = "abcd";

        assertAll(
                () -> assertEquals(result1, StringUtils.strCat(start, finish, length1)),
                () -> assertEquals(result2, StringUtils.strCat(start, finish, length2)),
                () -> assertEquals(result3, StringUtils.strCat(start, finish, length3)),
                () -> assertEquals(result4, StringUtils.strCat(start, finish, length4)),
                () -> assertEquals(result5, StringUtils.strCat(start, finish, length5))
        );
    }

    @Test
    void strCap() {
        String string1 = "capital";
        String string2 = "Capital";
        String string3 = "";

        assertAll(
                () -> assertEquals(string2, StringUtils.strCap(string1)),
                () -> assertEquals(string2, StringUtils.strCap(string2)),
                () -> assertEquals(string3, StringUtils.strCap(string3))
        );
    }

    @Test
    void suffix() {
        String string = "Concatenate";
        String emptyString = "";
        String suffix = "ate";
        String wrongSuffix = "eight";
        String suffixTooLong = "Concatenated";
        String notSuffix = "nat";

        assertAll(
                () -> assertTrue(StringUtils.suffix(string, emptyString)),
                () -> assertTrue(StringUtils.suffix(string, suffix)),
                () -> assertFalse(StringUtils.suffix(string, wrongSuffix)),
                () -> assertFalse(StringUtils.suffix(string, notSuffix)),
                () -> assertFalse(StringUtils.suffix(string, suffixTooLong))
        );
    }

    @Test
    void suffixI() {
        String string = "ConcatenaTe";
        String emptyString = "";
        String suffix = "ate";
        String wrongSuffix = "eight";
        String suffixTooLong = "Concatenated";
        String notSuffix = "nat";

        assertAll(
                () -> assertTrue(StringUtils.suffixI(string, emptyString)),
                () -> assertTrue(StringUtils.suffixI(string, suffix)),
                () -> assertFalse(StringUtils.suffixI(string, wrongSuffix)),
                () -> assertFalse(StringUtils.suffixI(string, notSuffix)),
                () -> assertFalse(StringUtils.suffixI(string, suffixTooLong))
        );
    }

    @Test
    void prefix() {
        String string = "Foxes eat dogs";
        String rightPrefix = "Fox";
        String emptyPrefix = "";
        String caseSensitivePrefix = "f";
        String wrongPrefix = "Cats";

        assertAll(
                () -> assertTrue(StringUtils.prefix(string, rightPrefix)),
                () -> assertTrue(StringUtils.prefix(string, emptyPrefix)),
                () -> assertFalse(StringUtils.prefix(string, caseSensitivePrefix)),
                () -> assertFalse(StringUtils.prefix(string, wrongPrefix))
        );
    }

    @Test
    void prefixI() {
        String string = "Foxes eat dogs";
        String rightPrefix = "Fox";
        String emptyPrefix = "";
        String caseSensitivePrefix = "f";
        String wrongPrefix = "Cats";

        assertAll(
                () -> assertTrue(StringUtils.prefixI(string, rightPrefix)),
                () -> assertTrue(StringUtils.prefixI(string, emptyPrefix)),
                () -> assertTrue(StringUtils.prefixI(string, caseSensitivePrefix)),
                () -> assertFalse(StringUtils.prefixI(string, wrongPrefix))
        );
    }
}