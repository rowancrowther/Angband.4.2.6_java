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

package uk.co.jackoftrades.middle.enums;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Stats}' index and name lookups against C's originals.
 *
 * <p>The expected figures come from three places in the C, none of them from the port. The indices
 * are the order of {@code list-stats.h}, whose own comment warns that changing it breaks savefiles.
 * The names are {@code stat_name_list[]} ({@code player.c:103}), which the {@code STAT()} macro
 * builds as the bare {@code STR INT WIS DEX CON}, followed by {@code MAX} and a {@code NULL}
 * terminator. And the two lookups are {@code stat_name_to_idx} ({@code player.c:111}) and
 * {@code stat_idx_to_name} ({@code player.c:122}).
 *
 * <p>Three properties carry the weight here. The indices are a file format rather than an ordering,
 * so they are asserted as literal numbers. The names have no {@code STAT_} prefix, which is what
 * lets the wizard's stat editor seed a prompt with one and read the reply back
 * ({@code cmd-wizard.c:1309-1313}) - so the round trip is tested in both directions. And C matches
 * names with {@code my_stricmp}, so case must not matter; the case folding is pinned to
 * {@link Locale#ROOT}, and the Turkish locale is what proves the pin, because the default folding
 * there maps {@code i} to a dotted capital and would lose {@code int} and {@code wis} alone.
 *
 * <p>Class StatsTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
class StatsTest {

    /**
     * The five real stats in C's order, which is the order {@code list-stats.h} declares them and
     * therefore the order savefiles are written in.
     */
    private static final Stats[] REAL_STATS = {
            Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON
    };

    /**
     * The first five entries of C's {@code stat_name_list[]}, in the same order.
     */
    private static final String[] REAL_NAMES = {"STR", "INT", "WIS", "DEX", "CON"};

    /**
     * The indices, which are part of the savefile format rather than an implementation detail.
     */
    @Nested
    @DisplayName("getValue")
    class Indices {

        /**
         * The five real stats carry 0 to 4 in {@code list-stats.h} order. These are asserted as
         * literals because that is what they are in C: the header's comment says a stat's sequence
         * number is fixed, and reordering the enum would break every savefile.
         */
        @Test
        @DisplayName("the five real stats carry C's savefile indices")
        void realStatsCarryTheirIndices() {
            assertEquals(0, Stats.STAT_STR.getValue());
            assertEquals(1, Stats.STAT_INT.getValue());
            assertEquals(2, Stats.STAT_WIS.getValue());
            assertEquals(3, Stats.STAT_DEX.getValue());
            assertEquals(4, Stats.STAT_CON.getValue());
        }

        /**
         * {@code STAT_MAX} carries 5, the value C's enum gives it as the member after {@code CON},
         * so it counts the real stats and can be compared against as a bound.
         */
        @Test
        @DisplayName("STAT_MAX carries 5, the count of the real stats")
        void maxCountsTheRealStats() {
            assertEquals(5, Stats.STAT_MAX.getValue());
            assertEquals(REAL_STATS.length, Stats.STAT_MAX.getValue());
        }

        /**
         * {@code STAT_NONE} carries -1, which is the port's own. C's stat enum has no
         * {@code STAT_NONE}; -1 is what its {@code stat_name_to_idx} returns for a name it does not
         * recognise, and the constant stands in for that answer.
         */
        @Test
        @DisplayName("STAT_NONE carries -1, C's not-found answer")
        void noneCarriesMinusOne() {
            assertEquals(-1, Stats.STAT_NONE.getValue());
        }

        /**
         * The two sentinels must not share a value. While both held -1 the reverse lookup could
         * never reach {@code STAT_MAX}, and an index of 5 - which C uses as its bound - had no
         * constant to name it.
         */
        @Test
        @DisplayName("the two sentinels are distinguishable by value")
        void sentinelsDoNotCollide() {
            assertEquals(Stats.values().length,
                    java.util.Arrays.stream(Stats.values()).map(Stats::getValue).distinct().count(),
                    "every constant should carry a value no other constant carries");
        }
    }

    /**
     * The reverse lookup, which stands in for C's array subscripting.
     */
    @Nested
    @DisplayName("getStats")
    class ByIndex {

        /**
         * Every index answers the constant that carries it, sentinels included.
         */
        @Test
        @DisplayName("each index answers its own stat")
        void eachIndexAnswersItsStat() {
            assertSame(Stats.STAT_NONE, Stats.getStats(-1));
            assertSame(Stats.STAT_STR, Stats.getStats(0));
            assertSame(Stats.STAT_INT, Stats.getStats(1));
            assertSame(Stats.STAT_WIS, Stats.getStats(2));
            assertSame(Stats.STAT_DEX, Stats.getStats(3));
            assertSame(Stats.STAT_CON, Stats.getStats(4));
            assertSame(Stats.STAT_MAX, Stats.getStats(5));
        }

        /**
         * An index outside the set answers {@code null} rather than throwing. C has nothing to
         * fail here - it would read past the end of the array - so this boundary is the port's.
         */
        @Test
        @DisplayName("an index outside the set answers null")
        void unknownIndexIsNull() {
            assertNull(Stats.getStats(-2));
            assertNull(Stats.getStats(6));
            assertNull(Stats.getStats(Integer.MAX_VALUE));
        }

        /**
         * And it is the exact inverse of {@link Stats#getValue()} for every constant, which is what
         * lets a stat survive a trip through an index and back.
         */
        @Test
        @DisplayName("it inverts getValue for every constant")
        void invertsGetValue() {
            for (Stats stat : Stats.values()) {
                assertSame(stat, Stats.getStats(stat.getValue()), stat + " should survive the trip");
            }
        }
    }

    /**
     * The name lookups, which are the parsers' route in.
     */
    @Nested
    @DisplayName("the names")
    class Names {

        /**
         * The names are the bare ones C's {@code stat_name_list[]} holds. A {@code STAT_} prefix
         * here would break the wizard editor's round trip, and would not match the game data.
         */
        @Test
        @DisplayName("statIdxToName gives C's bare names")
        void bareNames() {
            for (int i = 0; i < REAL_STATS.length; i++) {
                assertEquals(REAL_NAMES[i], Stats.statIdxToName(REAL_STATS[i]));
            }
        }

        /**
         * Each of those names resolves back to its stat, which is the lookup {@code init.c:2876}
         * and {@code effects.c:217} depend on.
         */
        @Test
        @DisplayName("statNameToIdx resolves each of them")
        void namesResolve() {
            for (int i = 0; i < REAL_NAMES.length; i++) {
                assertSame(REAL_STATS[i], Stats.statNameToIdx(REAL_NAMES[i]));
            }
        }

        /**
         * And the pair closes in both directions, which is what {@code cmd-wizard.c:1309-1313}
         * does: it prints a name, takes it back from the player and reads it as a stat.
         */
        @Test
        @DisplayName("the round trip closes both ways")
        void roundTripCloses() {
            for (Stats stat : REAL_STATS) {
                assertSame(stat, Stats.statNameToIdx(Stats.statIdxToName(stat)));
            }
            for (String name : REAL_NAMES) {
                assertEquals(name, Stats.statIdxToName(Stats.statNameToIdx(name)));
            }
        }

        /**
         * C matches with {@code my_stricmp}, which upper-cases both sides, so any mixture of case
         * resolves.
         */
        @Test
        @DisplayName("case does not matter, as my_stricmp says")
        void caseDoesNotMatter() {
            assertSame(Stats.STAT_STR, Stats.statNameToIdx("str"));
            assertSame(Stats.STAT_STR, Stats.statNameToIdx("Str"));
            assertSame(Stats.STAT_STR, Stats.statNameToIdx("sTr"));
            assertSame(Stats.STAT_CON, Stats.statNameToIdx("con"));
        }

        /**
         * {@code MAX} resolves, because C's name list carries it at index 5 and returns it like
         * any other entry. It is not a stat the game data ever asks for; it is in the list because
         * the sentinel is, and a caller bounds-testing against {@code STAT_MAX} - as C's callers do
         * - rejects it afterwards.
         */
        @Test
        @DisplayName("MAX resolves, as it does in C's name list")
        void maxResolves() {
            assertSame(Stats.STAT_MAX, Stats.statNameToIdx("MAX"));
            assertEquals(5, Stats.statNameToIdx("MAX").getValue(), "C's stat_name_list[5]");
        }

        /**
         * {@code NONE} does not, because C's list has no such entry and would answer -1 for it.
         * The constant exists in the port only to stand in for that -1, so accepting its name would
         * turn a not-found into a find.
         */
        @Test
        @DisplayName("NONE does not resolve, because C has no such entry")
        void noneDoesNotResolve() {
            assertNull(Stats.statNameToIdx("NONE"));
            assertNull(Stats.statNameToIdx("none"));
        }

        /**
         * Anything else is C's -1, which the port spells {@code null}. The empty string is worth
         * naming separately because it is what both sentinels carry as their name, so a sentinel
         * fed back through the lookup fails rather than resolving to itself.
         */
        @Test
        @DisplayName("an unrecognised name is C's -1, spelled null")
        void unrecognisedNamesAreNull() {
            assertNull(Stats.statNameToIdx("XYZ"));
            assertNull(Stats.statNameToIdx(""));
            assertNull(Stats.statNameToIdx("STAT_STR"), "the prefixed form is not a data name");
            assertNull(Stats.statNameToIdx(Stats.statIdxToName(Stats.STAT_MAX)));
        }
    }

    /**
     * The case folding, which has to be locale-independent to match C's.
     */
    @Nested
    @DisplayName("locale independence")
    class LocaleIndependence {

        /**
         * The default locale, restored after each test in this group so the mutation cannot leak
         * into another suite.
         */
        private final Locale original = Locale.getDefault();

        /**
         * Puts the default locale back.
         */
        @AfterEach
        void restoreLocale() {
            Locale.setDefault(original);
        }

        /**
         * Turkish is the case that catches an unpinned folding: its dotless-i rule maps {@code i}
         * to a dotted capital, so {@code "int"} folds to {@code "İNT"} and stops matching, while
         * {@code "str"}, {@code "dex"} and {@code "con"} carry on working and hide the fault.
         *
         * <p>C folds with {@code toupper} under the {@code C} locale, which has no such rule, so
         * all five must resolve here whatever the machine's locale is.
         */
        @Test
        @DisplayName("lowercase names resolve under a Turkish default locale")
        void turkishLocaleDoesNotBreakTheLookup() {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertSame(Stats.STAT_INT, Stats.statNameToIdx("int"), "the dotless-i case");
            assertSame(Stats.STAT_WIS, Stats.statNameToIdx("wis"), "and the other name carrying an i");
            assertSame(Stats.STAT_STR, Stats.statNameToIdx("str"));
            assertSame(Stats.STAT_DEX, Stats.statNameToIdx("dex"));
            assertSame(Stats.STAT_CON, Stats.statNameToIdx("con"));
        }

        /**
         * The same holds for a locale that folds ordinarily, so the pin has not broken the common
         * case on the way past.
         */
        @Test
        @DisplayName("and under an ordinary one")
        void ordinaryLocaleStillWorks() {
            Locale.setDefault(Locale.UK);

            for (int i = 0; i < REAL_NAMES.length; i++) {
                assertSame(REAL_STATS[i], Stats.statNameToIdx(REAL_NAMES[i].toLowerCase(Locale.ROOT)));
            }
        }
    }
}
