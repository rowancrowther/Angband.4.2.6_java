/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link RandomReader}: one dice-notation string in, one {@link Random} (or {@code null}) out.
 *
 * <p>The expected figures come from the C original, not from the Java. The ordinary and boundary
 * cases were taken from {@code parse_random} in {@code parser.c}, compiled on its own and fed the
 * same strings. The sweep tests rest on {@link #parseRandomInC}, a line-for-line transliteration of
 * that function, and compare it with the reader over every string up to five characters drawn from
 * {@code 0 1 2 d M + -}. Where the two disagree the sweep insists the string falls into one of the
 * named differences below and fails on anything else, so a new divergence cannot hide among them.
 *
 * <p>The differences pinned here are the ones written up for {@code Random.g4} in
 * {@code docs/grammar_issues.md}. The reader is a superset of {@code parse_random} in that it
 * accepts a lower-case {@code m}, a base straight into {@code M} and {@code $} variables (the last
 * two because {@code z-dice.c} reads them). It is stricter in that it refuses blanks, a repeated
 * {@code d} or {@code M}, and a signed zero such as {@code 2d-0}. A number above {@code INT_MAX} is
 * a rejection in C and an unchecked {@link NumberFormatException} here.
 *
 * <p>Logging is switched off for the class: the sweeps reject thousands of strings, and each one
 * would otherwise write an error with a stack trace.
 *
 * <p>Class RandomReaderTest coded on 261019, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
class RandomReaderTest {

    /**
     * The characters the sweeps build their strings from.
     */
    private static final String SWEEP_ALPHABET = "012dM+-";
    /**
     * The longest string the sweeps try, 19,607 strings in all.
     */
    private static final int SWEEP_LENGTH = 5;
    /**
     * Strings that, once an optional leading {@code -} is gone, are a number, {@code M}, a number:
     * C's loop sees the first number, finds neither {@code d} nor {@code +} after it and refuses.
     */
    private static final Pattern BASE_THEN_BONUS = Pattern.compile("\\d+M\\d+");

    private final RandomReader reader = new RandomReader();

    @BeforeAll
    static void quietLogging() {
        Configurator.setRootLevel(Level.OFF);
    }

    @AfterAll
    static void restoreLogging() {
        Configurator.setRootLevel(Level.ALL);
    }

    // ---- helpers ---------------------------------------------------------

    /**
     * The four terms of a value, in C's struct order.
     */
    private static List<Integer> terms(Random r) {
        return List.of(r.getBase(), r.getDice(), r.getSides(), r.getMBonus());
    }

    /**
     * Asserts the reader accepts {@code text} and builds the given base, dice, sides and
     * {@code m_bonus}.
     */
    private void assertReads(String text, int base, int dice, int sides, int mBonus) throws Exception {
        Random r = reader.parse(text);
        assertNotNull(r, () -> "reader refused '" + text + "'");
        assertEquals(List.of(base, dice, sides, mBonus), terms(r), "terms of '" + text + "'");
    }

    /**
     * The same through {@link Random#parseStr}, the route every assembler takes.
     */
    private static void assertParsesStr(String text, int base, int dice, int sides, int mBonus) {
        Random r = Random.parseStr(text);
        assertNotNull(r, () -> "parseStr refused '" + text + "'");
        assertEquals(List.of(base, dice, sides, mBonus), terms(r), "terms of '" + text + "'");
    }

    /**
     * Asserts the reader and {@link Random#parseStr} both return {@code null}, without throwing.
     */
    private void assertRefused(String text) {
        assertDoesNotThrow(() -> assertNull(reader.parse(text), "reader accepted '" + text + "'"));
        assertDoesNotThrow(() -> assertNull(Random.parseStr(text), "parseStr accepted '" + text + "'"));
    }

    /**
     * A line-for-line transliteration of {@code parse_random} in {@code parser.c}, with
     * {@code contains_only_spaces} from {@code z-util.c} and {@code strtoul} as the C library does it.
     * Returns base, dice, sides and {@code m_bonus}, or {@code null} where C returns false.
     */
    private static int[] parseRandomInC(String text) {
        int[] values = {0, 0, 0, 0};
        int i = 0;
        int minI = 1;
        boolean negative = false;
        int p = 0;

        if (text.startsWith("-")) {
            negative = true;
            p = 1;
        }

        while (true) {
            char c = p < text.length() ? text.charAt(p) : '\0';
            if (c == 'd') {
                if (i > 2) return null;
                if (i < 2) {
                    i = 2;
                    values[1] = 1;
                }
                minI = 3;
                p++;
            } else if (c == 'M') {
                if (i == 2) return null;
                i = 3;
                minI = 4;
                p++;
            } else {
                int q = p;
                while (q < text.length() && Character.isWhitespace(text.charAt(q))) q++;
                boolean minus = false;
                if (q < text.length() && (text.charAt(q) == '+' || text.charAt(q) == '-')) {
                    minus = text.charAt(q) == '-';
                    q++;
                }
                int digitsFrom = q;
                long uv = 0;
                while (q < text.length() && Character.isDigit(text.charAt(q))) {
                    uv = Math.min(uv * 10 + (text.charAt(q) - '0'), 1L << 40);
                    q++;
                }
                if (q == digitsFrom) {
                    if (!containsOnlySpaces(text, p) || i < minI) return null;
                    break;
                }
                if (minus && uv != 0) uv = 1L << 40;
                if (uv > Integer.MAX_VALUE || c == '+') return null;
                p = q;
                char next = p < text.length() ? text.charAt(p) : '\0';
                if (i == 0) {
                    if (next == 'd') {
                        i = 1;
                    } else if (next == '+') {
                        p++;
                        minI = 3;
                    } else {
                        if (!containsOnlySpaces(text, p)) return null;
                        values[0] = (int) uv;
                        break;
                    }
                } else if (i == 4) {
                    return null;
                }
                values[i] = (int) uv;
                i++;
            }
        }

        if (negative) {
            values[0] *= -1;
            values[0] -= values[3];
            values[0] -= values[1] * (values[2] + 1);
        }
        return values;
    }

    /**
     * {@code contains_only_spaces} from {@code z-util.c}: only blanks and tabs from {@code from} on.
     */
    private static boolean containsOnlySpaces(String text, int from) {
        for (int k = from; k < text.length(); k++) {
            if (text.charAt(k) != ' ' && text.charAt(k) != '\t') return false;
        }
        return true;
    }

    /**
     * Every string over {@link #SWEEP_ALPHABET} from one character up to {@link #SWEEP_LENGTH}.
     */
    private static List<String> sweepStrings() {
        List<String> all = new ArrayList<>();
        List<String> layer = List.of("");
        for (int n = 1; n <= SWEEP_LENGTH; n++) {
            List<String> next = new ArrayList<>(layer.size() * SWEEP_ALPHABET.length());
            for (String s : layer) {
                for (char ch : SWEEP_ALPHABET.toCharArray()) next.add(s + ch);
            }
            all.addAll(next);
            layer = next;
        }
        return all;
    }

    // ---- ordinary strings: C and Java agree ------------------------------

    @Test
    void aPlainDiceStringGivesZeroBaseAndNoBonus() throws Exception {
        assertReads("2d6", 0, 2, 6, 0);
    }

    @Test
    void aBareDMeansOneDie() throws Exception {
        assertReads("d6", 0, 1, 6, 0);
    }

    @Test
    void aBaseBeforeAPlusIsTheBaseNotTheDiceCount() throws Exception {
        assertReads("1+1d3", 1, 1, 3, 0);
        assertReads("1+d3", 1, 1, 3, 0);
    }

    @Test
    void aBonusAloneIsReadWithNothingElse() throws Exception {
        assertReads("M80", 0, 0, 0, 80);
        assertReads("M0", 0, 0, 0, 0);
    }

    @Test
    void aBareNumberIsTheBase() throws Exception {
        assertReads("5", 5, 0, 0, 0);
        assertReads("0", 0, 0, 0, 0);
    }

    @Test
    void leadingZerosAreReadAsDecimal() throws Exception {
        assertReads("007", 7, 0, 0, 0);
        assertReads("00d6", 0, 0, 6, 0);
        assertReads("0d0", 0, 0, 0, 0);
    }

    @Test
    void aBaseAndABonusAreJoinedByAPlus() throws Exception {
        assertReads("5+M3", 5, 0, 0, 3);
    }

    @Test
    void everyTermTogether() throws Exception {
        assertReads("1+2d3M4", 1, 2, 3, 4);
        assertReads("2d6M3", 0, 2, 6, 3);
    }

    // ---- boundaries ------------------------------------------------------

    @Test
    void intMaxIsAcceptedInEverySlot() throws Exception {
        assertReads("2147483647", Integer.MAX_VALUE, 0, 0, 0);
        assertReads("2147483647d2147483647M2147483647",
                0, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test
    void aNumberAboveIntMaxThrowsHereWhereCRejectsIt() {
        for (String text : List.of("2147483648", "99999999999", "4294967296", "1+2147483648d3")) {
            assertThrows(NumberFormatException.class, () -> reader.parse(text), text);
            assertThrows(NumberFormatException.class, () -> Random.parseStr(text), text);
        }
    }

    // ---- a leading minus flips the whole range ---------------------------

    @Test
    void aLeadingMinusFlipsTheRangeWhenGivenToTheReaderDirectly() throws Exception {
        assertReads("-1d4", -5, 1, 4, 0);
        assertReads("-1+2d3", -9, 2, 3, 0);
        assertReads("-5", -5, 0, 0, 0);
        assertReads("-0", 0, 0, 0, 0);
    }

    @Test
    void parseStrFlipsTheRangeForTheFormsTheReaderTakesOnlyPositive() {
        assertParsesStr("-1d4", -5, 1, 4, 0);
        assertParsesStr("-M4", -4, 0, 0, 4);
        assertParsesStr("-d4", -5, 1, 4, 0);
        assertParsesStr("-2d6M3", -17, 2, 6, 3);
        assertParsesStr("-1+2d3", -9, 2, 3, 0);
    }

    // ---- malformed text: both refuse -------------------------------------

    @Test
    void textBothParsersRefuseGivesNull() {
        for (String text : List.of("", "-", "+", "+5", "5+", "5+2M3", "2d6d3", "1+2+3", "1d2d3",
                "1+2d3+4", "5d", "d", "0x10")) {
            assertRefused(text);
        }
    }

    @Test
    void aMissingNumberAfterTheMarkerIsRefusedNotThrown() {
        for (String text : List.of("M", "5M", "2d6M", "1+M", "1+2d3M", "d6M", "5+dM", "-1d", "$S+dM", "$D+dm")) {
            assertRefused(text);
        }
    }

    // ---- where Java accepts and C refuses --------------------------------

    @Test
    void aLowerCaseMIsAcceptedHereWhereParseRandomRefusesIt() throws Exception {
        assertReads("2d6m3", 0, 2, 6, 3);
    }

    @Test
    void aBaseStraightIntoMIsAcceptedHereWhereParseRandomRefusesIt() throws Exception {
        assertReads("5M3", 5, 0, 0, 3);
        assertReads("11M22", 11, 0, 0, 22);
    }

    @Test
    void dollarVariablesAreAcceptedHereWhereParseRandomHasNone() throws Exception {
        assertReads("$S", 0, 0, 0, 0);
        assertReads("4d$S", 0, 4, 0, 0);
    }

    @Test
    void aDoubleMinusIsAcceptedByParseStrWhereCRefusesIt() {
        assertParsesStr("--1", -1, 0, 0, 0);
    }

    // ---- where C accepts and Java refuses --------------------------------

    @Test
    void blanksAreRefusedHereWhereCAllowsThemAtTheEnds() {
        for (String text : List.of(" 2d6", "2d6 ", "5 ", "5\t", "M 3")) {
            assertNotNull(parseRandomInC(text), "C should accept '" + text + "'");
            assertRefused(text);
        }
    }

    @Test
    void aRepeatedDOrMIsRefusedHereWhereCsLoopLetsItThrough() {
        assertArrayEquals(new int[]{0, 1, 1, 0}, parseRandomInC("dd1"));
        assertArrayEquals(new int[]{0, 0, 0, 1}, parseRandomInC("MM1"));
        assertArrayEquals(new int[]{0, 0, 0, 2}, parseRandomInC("M1M2"));
        for (String text : List.of("dd1", "MM1", "M1M2")) {
            assertRefused(text);
        }
    }

    @Test
    void aSignedZeroInsideTheStringIsRefusedHereWhereStrtoulReadsIt() {
        assertArrayEquals(new int[]{0, 2, 0, 0}, parseRandomInC("2d-0"));
        assertRefused("2d-0");
    }

    @Test
    void aVariableDiceCountIsRefusedHereWhereZDiceReadsIt() {
        assertRefused("$Dd5");
        assertRefused("$B+d$S");
    }

    // ---- the oracle itself -----------------------------------------------

    @Test
    void theTransliterationReproducesTheValuesTakenFromTheCBinary() {
        assertArrayEquals(new int[]{0, 2, 6, 0}, parseRandomInC("2d6"));
        assertArrayEquals(new int[]{1, 1, 3, 0}, parseRandomInC("1+d3"));
        assertArrayEquals(new int[]{-5, 1, 4, 0}, parseRandomInC("-1d4"));
        assertArrayEquals(new int[]{-9, 2, 3, 0}, parseRandomInC("-1+2d3"));
        assertArrayEquals(new int[]{-4, 0, 0, 4}, parseRandomInC("-M4"));
        assertArrayEquals(new int[]{-17, 2, 6, 3}, parseRandomInC("-2d6M3"));
        assertNull(parseRandomInC("5M3"));
        assertNull(parseRandomInC("--1"));
        assertNull(parseRandomInC("2147483648"));
        assertArrayEquals(new int[]{Integer.MAX_VALUE, 0, 0, 0}, parseRandomInC("2147483647"));
    }

    // ---- sweeps: every short string against the transliteration ----------

    /**
     * Compares one string. Returns a description of anything that is not a known difference, or
     * {@code null} when the reader and C agree or differ in a named way. {@code counts} gets one
     * tick for each named difference seen: lenient, strict.
     */
    private static String compare(String text, boolean viaParseStr, Random java, int[] c, int[] counts) {
        if (c != null && java != null) {
            return List.of(c[0], c[1], c[2], c[3]).equals(terms(java)) ? null
                    : "values differ for '" + text + "': C " + List.of(c[0], c[1], c[2], c[3]) + " Java " + terms(java);
        }
        if (java != null) {
            String rest = viaParseStr && text.startsWith("-") ? text.substring(1) : text;
            boolean known = BASE_THEN_BONUS.matcher(rest).matches() || (viaParseStr && text.startsWith("--"));
            counts[0]++;
            return known ? null : "Java accepts '" + text + "' but C refuses it";
        }
        if (c != null) {
            boolean known = text.contains("dd") || text.chars().filter(ch -> ch == 'M').count() >= 2
                    || Pattern.compile(".-0").matcher(text).find();
            counts[1]++;
            return known ? null : "C accepts '" + text + "' but Java refuses it";
        }
        return null;
    }

    @Test
    void sweepThroughParseStrAgreesWithCExceptForTheNamedDifferences() {
        List<String> problems = new ArrayList<>();
        int[] counts = new int[2];
        for (String text : sweepStrings()) {
            Random java;
            try {
                java = Random.parseStr(text);
            } catch (RuntimeException e) {
                problems.add("'" + text + "' threw " + e);
                continue;
            }
            String problem = compare(text, true, java, parseRandomInC(text), counts);
            if (problem != null) problems.add(problem);
        }
        assertTrue(problems.isEmpty(), () -> problems.size() + " problems, first: "
                + problems.subList(0, Math.min(10, problems.size())));
        assertTrue(counts[0] > 0 && counts[1] > 0, "the sweep should meet both kinds of named difference");
    }

    @Test
    void sweepThroughTheReaderAgreesWithCExceptForTheNamedDifferences() throws Exception {
        List<String> problems = new ArrayList<>();
        int[] counts = new int[2];
        for (String text : sweepStrings()) {
            if (text.startsWith("-")) continue;
            Random java;
            try {
                java = reader.parse(text);
            } catch (Exception e) {
                problems.add("'" + text + "' threw " + e);
                continue;
            }
            String problem = compare(text, false, java, parseRandomInC(text), counts);
            if (problem != null) problems.add(problem);
        }
        assertTrue(problems.isEmpty(), () -> problems.size() + " problems, first: "
                + problems.subList(0, Math.min(10, problems.size())));
        assertTrue(counts[0] > 0 && counts[1] > 0, "the sweep should meet both kinds of named difference");
    }

    @Test
    void theReaderReadsALeadingMinusAsCDoesWhereverItAcceptsTheString() throws Exception {
        int agreed = 0;
        for (String text : sweepStrings()) {
            if (!text.startsWith("-")) continue;
            Random java = reader.parse(text);
            int[] c = parseRandomInC(text);
            if (java != null && c != null) {
                assertEquals(List.of(c[0], c[1], c[2], c[3]), terms(java), "terms of '" + text + "'");
                agreed++;
            }
        }
        assertTrue(agreed > 0, "some negated strings should be accepted by both");
    }
}
