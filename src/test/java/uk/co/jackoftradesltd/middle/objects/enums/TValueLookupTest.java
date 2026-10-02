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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link TValue}'s lookups — {@link TValue#fromName(String)}, {@link TValue#fromName(int)}
 * and {@link TValue#findIndex(String)} — against C's {@code tval_find_idx} and its helper
 * {@code de_armour} ({@code obj-tval.c}).
 *
 * <p>Every expected value was worked out by walking the input through the C, not by running the
 * port. C answers a number; the port answers a {@code TValue} from {@code fromName} and the same
 * number from {@code findIndex}, with {@code null} and {@code -1} standing for C's {@code -1}.
 *
 * <p>The tests follow C's three stages in order: the {@code strtoul} number path with its
 * whitespace rules, the {@code de_armour} rewrite, and the case-insensitive name match. A last
 * group pins the identifier form, which is port-only.
 *
 * <p>Class TValueLookupTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
class TValueLookupTest {

    /**
     * Asserts that a text resolves to the given tval through both entry points, so that
     * {@code fromName} and {@code findIndex} cannot drift apart.
     *
     * <p>Function assertResolves coded on 261002, commented in full on 261002.
     *
     * @param expected the tval C resolves the text to
     * @param text     the tval text
     */
    private static void assertResolves(TValue expected, String text) {
        assertSame(expected, TValue.fromName(text), "fromName(\"" + text + "\")");
        assertEquals(expected.ordinal(), TValue.findIndex(text), "findIndex(\"" + text + "\")");
    }

    /**
     * Asserts that a text resolves to no tval: {@code null} from {@code fromName}, and C's
     * {@code -1} from {@code findIndex}.
     *
     * <p>Function assertRejected coded on 261002, commented in full on 261002.
     *
     * @param text the tval text
     */
    private static void assertRejected(String text) {
        assertNull(TValue.fromName(text), "fromName(\"" + text + "\")");
        assertEquals(-1, TValue.findIndex(text), "findIndex(\"" + text + "\")");
    }

    /**
     * The declaration order is the contract with C: each constant's ordinal is its C value. If a
     * constant were moved, every numeric tval in the data files would change meaning without
     * anything failing to parse.
     *
     * <p>Function constantsAreInListTvalsOrder coded on 261002, commented in full on 261002.
     */
    @Test
    @DisplayName("constants are declared in list-tvals.h order, with list-tvals.h names")
    void constantsAreInListTvalsOrder() {
        List<String> cNames = List.of("none", "chest", "shot", "arrow", "bolt", "bow", "digger",
                "hafted", "polearm", "sword", "boots", "gloves", "helm", "crown", "shield", "cloak",
                "soft armor", "hard armor", "dragon armor", "light", "amulet", "ring", "staff",
                "wand", "rod", "scroll", "potion", "flask", "food", "mushroom", "magic book",
                "prayer book", "nature book", "shadow book", "other book", "gold");

        assertEquals(cNames.size(), TValue.values().length);
        for (int i = 0; i < cNames.size(); i++) {
            assertEquals(cNames.get(i), TValue.values()[i].getName(), "tval " + i);
        }
    }

    /**
     * The number path: C's {@code strtoul}, then {@code contains_only_spaces} on what follows,
     * then {@code r < TV_MAX}.
     */
    @Nested
    @DisplayName("numbers")
    class Numbers {

        /**
         * Both ends of the valid range. {@code 0} is {@code TV_NONE} and is a success, not a
         * failure; {@code 35} is the last tval.
         *
         * <p>Function rangeEnds coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("0 and 35 are the ends of the range")
        void rangeEnds() {
            assertResolves(TValue.TV_NONE, "0");
            assertResolves(TValue.TV_BOW, "5");
            assertResolves(TValue.TV_GOLD, "35");
        }

        /**
         * {@code 36} is C's {@code TV_MAX} and fails {@code r < TV_MAX}. A negative number wraps
         * to a huge unsigned value in {@code strtoul} and fails the same test. A number too big
         * for {@code int} is also out of range.
         *
         * <p>Function outOfRange coded on 261002, commented in full on 261002.
         */
        @ParameterizedTest
        @ValueSource(strings = {"36", "-1", "99999999999"})
        @DisplayName("TV_MAX, negatives and overflow are rejected")
        void outOfRange(String text) {
            assertRejected(text);
        }

        /**
         * {@code strtoul} accepts a leading sign.
         *
         * <p>Function signIsAccepted coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("a leading plus sign is accepted")
        void signIsAccepted() {
            assertResolves(TValue.TV_BOW, "+5");
        }

        /**
         * {@code strtoul} skips any of the six {@code isspace} characters before the digits.
         *
         * <p>Function leadingWhitespaceIsSkipped coded on 261002, commented in full on 261002.
         *
         * @param text a number preceded by C whitespace
         */
        @ParameterizedTest
        @ValueSource(strings = {" 5", "\t5", "\n5", "\r5", "\f5", "\u000B5", " \n\t5"})
        @DisplayName("leading C whitespace is skipped")
        void leadingWhitespaceIsSkipped(String text) {
            assertResolves(TValue.TV_BOW, text);
        }

        /**
         * After the digits, {@code contains_only_spaces} allows spaces and tabs and nothing else.
         *
         * <p>Function trailingSpacesAndTabsAreAllowed coded on 261002, commented in full on
         * 261002.
         *
         * @param text a number followed by spaces and tabs
         */
        @ParameterizedTest
        @ValueSource(strings = {"5 ", "5\t", "5 \t ", " \t5 \t"})
        @DisplayName("trailing spaces and tabs are allowed")
        void trailingSpacesAndTabsAreAllowed(String text) {
            assertResolves(TValue.TV_BOW, text);
        }

        /**
         * Any other trailing character makes C return {@code -1} at once, without falling
         * through to the name match. That includes the whitespace {@code contains_only_spaces}
         * does not accept.
         *
         * <p>Function otherTrailingTextIsRejected coded on 261002, commented in full on 261002.
         *
         * @param text a number followed by something other than spaces and tabs
         */
        @ParameterizedTest
        @ValueSource(strings = {"5x", "5\n", "5 \n", "5\r", "5\u000B", "0x5", "5 sword"})
        @DisplayName("any other trailing text is rejected")
        void otherTrailingTextIsRejected(String text) {
            assertRejected(text);
        }

        /**
         * A character outside C's {@code isspace} set is not skipped, so no digits are found.
         *
         * <p>Function nonCWhitespaceIsNotSkipped coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("a character outside C's whitespace set is not skipped")
        void nonCWhitespaceIsNotSkipped() {
            assertRejected("\u001C5");
        }

        /**
         * {@code fromName(int)} is the bounds check alone.
         *
         * <p>Function fromIntBounds coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("fromName(int) accepts 0 to 35 only")
        void fromIntBounds() {
            assertSame(TValue.TV_NONE, TValue.fromName(0));
            assertSame(TValue.TV_GOLD, TValue.fromName(35));
            assertNull(TValue.fromName(-1));
            assertNull(TValue.fromName(36));
        }
    }

    /**
     * The British-spelling rewrite, C's {@code de_armour}: the first lowercase {@code "armour"}
     * becomes {@code "armor"}, and everything after it is dropped.
     */
    @Nested
    @DisplayName("armour spelling")
    class ArmourSpelling {

        /**
         * All three body armours resolve from the British spelling, which the data files use 23
         * times.
         *
         * <p>Function britishSpellingResolves coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("the British spelling resolves for all three body armours")
        void britishSpellingResolves() {
            assertResolves(TValue.TV_SOFT_ARMOR, "soft armour");
            assertResolves(TValue.TV_HARD_ARMOR, "hard armour");
            assertResolves(TValue.TV_DRAG_ARMOR, "dragon armour");
        }

        /**
         * The American spelling passes through the rewrite untouched.
         *
         * <p>Function americanSpellingResolves coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("the American spelling resolves unchanged")
        void americanSpellingResolves() {
            assertResolves(TValue.TV_SOFT_ARMOR, "soft armor");
            assertResolves(TValue.TV_HARD_ARMOR, "hard armor");
            assertResolves(TValue.TV_DRAG_ARMOR, "dragon armor");
        }

        /**
         * The rest of the name is matched without regard to case; only the {@code "armour"}
         * itself must be lowercase for the rewrite to fire.
         *
         * <p>Function prefixCaseDoesNotMatter coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("the words before armour may be in any case")
        void prefixCaseDoesNotMatter() {
            assertResolves(TValue.TV_SOFT_ARMOR, "SOFT armour");
        }

        /**
         * C's {@code strstr} is case-sensitive, so a capitalised {@code "Armour"} is not
         * rewritten and then matches nothing.
         *
         * <p>Function capitalisedArmourIsNotRewritten coded on 261002, commented in full on
         * 261002.
         *
         * @param text a British spelling with {@code armour} not in lowercase
         */
        @ParameterizedTest
        @ValueSource(strings = {"Dragon Armour", "SOFT ARMOUR", "hard Armour"})
        @DisplayName("a capitalised Armour is not rewritten")
        void capitalisedArmourIsNotRewritten(String text) {
            assertRejected(text);
        }

        /**
         * C writes {@code "r"} and the string terminator over the {@code "ur"}, so anything after
         * the match is lost, and a name with trailing junk still resolves.
         *
         * <p>Function textAfterArmourIsDropped coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("text after armour is dropped")
        void textAfterArmourIsDropped() {
            assertResolves(TValue.TV_DRAG_ARMOR, "dragon armourX");
            assertResolves(TValue.TV_SOFT_ARMOR, "soft armour of resistance");
        }

        /**
         * Only the first lowercase match is rewritten. Here it is the second word, so the result
         * is {@code "ARMOUR armor"}, which matches nothing.
         *
         * <p>Function onlyTheFirstLowercaseMatchIsRewritten coded on 261002, commented in full
         * on 261002.
         */
        @Test
        @DisplayName("only the first lowercase armour is rewritten")
        void onlyTheFirstLowercaseMatchIsRewritten() {
            assertRejected("ARMOUR armour");
        }

        /**
         * {@code "armour"} on its own rewrites to {@code "armor"}, which is not a tval.
         *
         * <p>Function bareArmourIsNotATval coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("armour on its own is not a tval")
        void bareArmourIsNotATval() {
            assertRejected("armour");
            assertRejected("armor");
        }
    }

    /**
     * The name match: C's {@code my_stricmp} against the {@code list-tvals.h} names.
     */
    @Nested
    @DisplayName("display names")
    class DisplayNames {

        /**
         * Every display name resolves to its own constant, so no two rows share a name.
         *
         * <p>Function everyDisplayNameResolves coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("every display name resolves to its own constant")
        void everyDisplayNameResolves() {
            for (TValue tval : TValue.values()) {
                assertResolves(tval, tval.getName());
            }
        }

        /**
         * The match ignores case.
         *
         * <p>Function caseIsIgnored coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("case is ignored")
        void caseIsIgnored() {
            assertResolves(TValue.TV_SWORD, "SWORD");
            assertResolves(TValue.TV_MAGIC_BOOK, "Magic Book");
        }

        /**
         * {@code "none"} is a successful lookup of tval {@code 0}, not a failure.
         *
         * <p>Function noneIsATval coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("none resolves to TV_NONE")
        void noneIsATval() {
            assertResolves(TValue.TV_NONE, "none");
        }

        /**
         * Unlike the number path, the name match does not strip whitespace, so a padded name
         * fails in C.
         *
         * <p>Function paddedNamesAreRejected coded on 261002, commented in full on 261002.
         *
         * @param text a name with surrounding whitespace
         */
        @ParameterizedTest
        @ValueSource(strings = {" sword", "sword ", "\tsword"})
        @DisplayName("names are not trimmed")
        void paddedNamesAreRejected(String text) {
            assertRejected(text);
        }

        /**
         * Text that is neither a number nor a name resolves to nothing, including the empty
         * string and whitespace alone.
         *
         * <p>Function unknownTextIsRejected coded on 261002, commented in full on 261002.
         *
         * @param text text that names no tval
         */
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\n", "bogus", "null", "dragon"})
        @DisplayName("unknown text is rejected")
        void unknownTextIsRejected(String text) {
            assertRejected(text);
        }
    }

    /**
     * The reverse lookup, C's {@code tval_find_name}: a number to its {@code list-tvals.h} name,
     * or {@code "unknown"}.
     */
    @Nested
    @DisplayName("findName")
    class FindName {

        /**
         * The ends of the range, plus the two rows whose names differ from their identifiers.
         *
         * <p>Function namesInRange coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("a valid tval gives its list-tvals.h name")
        void namesInRange() {
            assertEquals("none", TValue.findName(0));
            assertEquals("digger", TValue.findName(6));
            assertEquals("dragon armor", TValue.findName(18));
            assertEquals("gold", TValue.findName(35));
        }

        /**
         * No row of {@code tval_names[]} has a tval below {@code 0} or from {@code TV_MAX}
         * upwards, so C's loop finds nothing and returns {@code "unknown"}.
         *
         * <p>Function outOfRangeIsUnknown coded on 261002, commented in full on 261002.
         *
         * @param tval a number that is not a tval
         */
        @ParameterizedTest
        @ValueSource(ints = {-1, 36, Integer.MIN_VALUE, Integer.MAX_VALUE})
        @DisplayName("a number that is not a tval gives unknown")
        void outOfRangeIsUnknown(int tval) {
            assertEquals("unknown", TValue.findName(tval));
        }

        /**
         * Name to number and back is the identity for every tval, as it is in C.
         *
         * <p>Function roundTripsWithFindIndex coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("findName and findIndex are inverses")
        void roundTripsWithFindIndex() {
            for (int i = 0; i < TValue.values().length; i++) {
                assertEquals(i, TValue.findIndex(TValue.findName(i)), "tval " + i);
            }
        }
    }

    /**
     * The identifier form, which has no C equivalent: the constant name without {@code TV_}.
     * Pinned so that a change to it is deliberate.
     */
    @Nested
    @DisplayName("identifiers (port only)")
    class Identifiers {

        /**
         * The constant name without its prefix resolves, in any case, with spaces standing in
         * for underscores. This is what reaches the two rows whose display names differ.
         *
         * <p>Function identifierResolves coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("the identifier without TV_ resolves")
        void identifierResolves() {
            assertResolves(TValue.TV_SOFT_ARMOR, "soft_armor");
            assertResolves(TValue.TV_SOFT_ARMOR, "SOFT_ARMOR");
            assertResolves(TValue.TV_DRAG_ARMOR, "drag_armor");
            assertResolves(TValue.TV_DRAG_ARMOR, "drag armor");
            assertResolves(TValue.TV_DIGGING, "digging");
        }

        /**
         * The prefix is added by the lookup, so passing it gives {@code TV_TV_SWORD}, which does
         * not exist.
         *
         * <p>Function prefixedIdentifierIsRejected coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("an identifier with TV_ is rejected")
        void prefixedIdentifierIsRejected() {
            assertRejected("TV_SWORD");
        }

        /**
         * The identifier is {@code DRAG_ARMOR}, so the display name with underscores does not
         * resolve.
         *
         * <p>Function underscoredDisplayNameIsNotAnIdentifier coded on 261002, commented in full
         * on 261002.
         */
        @Test
        @DisplayName("dragon_armor is not an identifier")
        void underscoredDisplayNameIsNotAnIdentifier() {
            assertRejected("dragon_armor");
        }
    }
}
