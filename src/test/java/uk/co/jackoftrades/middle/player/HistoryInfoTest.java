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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.Artifact;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerHistoryType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link HistoryInfo} — the port of C's {@code struct history_info}
 * ({@code src/player-history.h:47}).
 *
 * <p>The type has no behaviour of its own; the only C code that writes one is the slot-filling
 * block of {@code history_add_full} ({@code src/player-history.c:93}), so what is checked here is
 * that the six values C writes into a slot come back out of an entry unchanged and in the right
 * places. The expectations come from that block, not from the constructor: C writes {@code dlev}
 * into {@code dlev} and {@code clev} into {@code clev}, and a port that crossed the two adjacent
 * {@code int} parameters would compile and pass every ledger test.
 *
 * <p>The rest of the file covers the three places C's struct and the port disagree in shape, since
 * each one is a limit C has and the port does not: the artifact is a reference where C has a
 * {@code uint8_t} index (so {@code null} has to mean C's zero), the text is a {@link String} where
 * C has {@code char[80]} (so nothing is truncated at eighty), and the type is a flag set rather
 * than a single value, because {@code history_lose_artifact} ({@code src/player-history.c:246})
 * genuinely sets two flags on one entry.
 *
 * <p>Mutation is tested too, but only of the flags. C's {@code history_mark_artifact_known},
 * {@code history_mark_artifact_lost} and {@code history_unmask_unknown} all reach into an entry
 * that is already logged and turn flags on and off; nothing in C ever rewrites the other five
 * fields after the fact, so an entry has to permit exactly that much change and no more.
 *
 * <p>Class HistoryInfoTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
class HistoryInfoTest {

    /**
     * A flag set holding exactly the given types, C's {@code hist_wipe} followed by one
     * {@code hist_on} per type.
     *
     * @param types the history types to switch on
     * @return the flag set
     */
    private static Flag<PlayerHistoryType> flags(PlayerHistoryType... types) {
        Flag<PlayerHistoryType> flag = new Flag<>(PlayerHistoryType.class);
        for (PlayerHistoryType type : types) {
            flag.on(type);
        }
        return flag;
    }

    /**
     * A minimal artifact. Nothing on it is read — an entry only keeps the reference, standing in
     * for C's {@code artifact->aidx} — so the fixture exists solely to be a non-null artifact.
     *
     * @return an artifact with every field empty
     */
    private static Artifact artifact() {
        return new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(),
                new LinkedHashMap<>(), 0, 0, 0, 0, null, null, null);
    }

    /**
     * The six values C's {@code history_add_full} writes into a slot are the six an entry holds,
     * each in its own field.
     */
    @Nested
    @DisplayName("an entry holds what it was built with")
    class AnEntryHoldsWhatItWasBuiltWith {

        /**
         * C writes {@code dlev} and {@code clev} into separate fields from separate arguments.
         * They are checked with different values so that a port which swapped them would fail:
         * a character of level 12 who is 43 levels down is not the same entry as the reverse.
         */
        @Test
        @DisplayName("the depth and the character level do not cross over")
        void theDepthAndCharacterLevelDoNotCrossOver() {
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_GAIN_LEVEL),
                    43, 12, null, 0L, "Reached level 12");

            assertEquals(43, entry.dLevel, "dlev is the dungeon level");
            assertEquals(12, entry.cLevel, "clev is the character level");
        }

        /**
         * C's {@code turn} is filled from {@code p->total_energy / 100}, a player-turn count that
         * runs well past a short game; a long carries it without wrapping.
         */
        @Test
        @DisplayName("the turn is kept as given")
        void theTurnIsKeptAsGiven() {
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_PLAYER_BIRTH),
                    0, 1, null, 123456L, "Began the quest to destroy Morgoth.");

            assertEquals(123456L, entry.turn, "the turn is recorded as passed in");
        }

        /**
         * C's {@code my_strcpy} into {@code event} copies the text as it stands.
         */
        @Test
        @DisplayName("the text is kept as given")
        void theTextIsKeptAsGiven() {
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_USER_INPUT),
                    0, 1, null, 0L, "Remembered to buy more Cure Light Wounds");

            assertEquals("Remembered to buy more Cure Light Wounds", entry.historyText,
                    "the text is recorded as passed in");
        }

        /**
         * The birth entry C logs at depth zero and level one, with no artifact: the ordinary
         * shape of an entry, every field at once.
         */
        @Test
        @DisplayName("a birth entry holds all six values")
        void aBirthEntryHoldsAllSixValues() {
            Flag<PlayerHistoryType> type = flags(PlayerHistoryType.HIST_PLAYER_BIRTH);
            HistoryInfo entry = new HistoryInfo(type, 0, 1, null, 0L,
                    "Began the quest to destroy Morgoth.");

            assertSame(type, entry.type, "the flag set is stored, the copy having been made upstream");
            assertEquals(0, entry.dLevel, "born in the town");
            assertEquals(1, entry.cLevel, "born at level one");
            assertNull(entry.artifact, "a birth entry names no artifact");
            assertEquals(0L, entry.turn, "born on turn zero");
            assertEquals("Began the quest to destroy Morgoth.", entry.historyText, "the birth text");
        }
    }

    /**
     * C's {@code a_idx} is a {@code uint8_t} whose zero means "this entry is not about an
     * artifact"; the port carries the artifact itself, so {@code null} has to carry that meaning
     * instead, and a real artifact has to survive as the same object the scans in
     * {@code history_is_artifact_known} will compare against.
     */
    @Nested
    @DisplayName("the artifact stands in for C's a_idx")
    class TheArtifactStandsInForAIdx {

        /**
         * C's {@code history_add_with_flags} passes {@code artifact ? artifact->aidx : 0}, so an
         * entry about nothing in particular carries index zero. Null is that zero.
         */
        @Test
        @DisplayName("no artifact is null, C's index zero")
        void noArtifactIsNull() {
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_SLAY_UNIQUE),
                    3, 5, null, 900L, "Killed Grip, Farmer Maggot's dog");

            assertNull(entry.artifact, "a non-artifact entry carries C's a_idx of zero");
        }

        /**
         * C matches entries by comparing {@code a_idx} to {@code artifact->aidx}; the port matches
         * by identity, so the entry has to hold the very artifact it was given rather than a copy
         * of it, or no later scan would ever find it.
         */
        @Test
        @DisplayName("an artifact entry holds the artifact itself")
        void anArtifactEntryHoldsTheArtifactItself() {
            Artifact artifact = artifact();
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_ARTIFACT_KNOWN),
                    20, 25, artifact, 4200L, "Found the Phial of Galadriel");

            assertSame(artifact, entry.artifact, "the scans match on this object's identity");
        }
    }

    /**
     * C's {@code event} is {@code char[80]} and {@code history_add_full} truncates into it with
     * {@code my_strcpy}. Every C caller has already formatted into an eighty-character buffer
     * before it gets there, so the truncation never fires in practice and the port does not
     * reproduce it; this pins that decision down, since a later change that started truncating
     * would be a change in behaviour, not a fix.
     */
    @Nested
    @DisplayName("the text is not bounded at C's eighty characters")
    class TheTextIsNotBoundedAtEighty {

        /**
         * A string of exactly eighty characters, which C would store as seventy-nine plus its
         * terminator, is kept whole.
         */
        @Test
        @DisplayName("eighty characters survive intact")
        void eightyCharactersSurviveIntact() {
            String text = "x".repeat(80);
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_USER_INPUT),
                    0, 1, null, 0L, text);

            assertEquals(80, entry.historyText.length(), "nothing is trimmed at C's buffer size");
            assertEquals(text, entry.historyText, "the text is unchanged");
        }

        /**
         * Past the buffer entirely: C would keep the first seventy-nine characters, the port keeps
         * them all.
         */
        @Test
        @DisplayName("longer text is not truncated")
        void longerTextIsNotTruncated() {
            String text = "y".repeat(200);
            HistoryInfo entry = new HistoryInfo(flags(PlayerHistoryType.HIST_USER_INPUT),
                    0, 1, null, 0L, text);

            assertEquals(200, entry.historyText.length(), "the String has no eighty-character bound");
        }
    }

    /**
     * C's type is a bitflag array, not an enum value, and the artifact functions rely on that:
     * {@code history_lose_artifact} sets {@code HIST_ARTIFACT_UNKNOWN} and
     * {@code HIST_ARTIFACT_LOST} on the same entry, and {@code history_mark_artifact_known} turns
     * one off and another on afterwards.
     */
    @Nested
    @DisplayName("the type is a set of flags, as C's bitflags are")
    class TheTypeIsASetOfFlags {

        /**
         * The two flags {@code history_lose_artifact} switches on before logging.
         */
        @Test
        @DisplayName("a missed artifact carries two types at once")
        void aMissedArtifactCarriesTwoTypesAtOnce() {
            HistoryInfo entry = new HistoryInfo(
                    flags(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN, PlayerHistoryType.HIST_ARTIFACT_LOST),
                    30, 30, artifact(), 8000L, "Missed the Phial of Galadriel");

            assertTrue(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN),
                    "history_lose_artifact sets ARTIFACT_UNKNOWN");
            assertTrue(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_LOST),
                    "history_lose_artifact sets ARTIFACT_LOST");
            assertFalse(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_KNOWN),
                    "it has not been identified");
        }

        /**
         * C's {@code history_mark_artifact_known} does {@code hist_off(ARTIFACT_UNKNOWN)} then
         * {@code hist_on(ARTIFACT_KNOWN)} on an entry that is already in the ledger, so an entry
         * has to allow its flags to be rewritten after it was built. {@code ARTIFACT_LOST} is left
         * alone by that function and has to stay put.
         */
        @Test
        @DisplayName("a logged entry's flags can still be rewritten")
        void aLoggedEntrysFlagsCanStillBeRewritten() {
            HistoryInfo entry = new HistoryInfo(
                    flags(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN, PlayerHistoryType.HIST_ARTIFACT_LOST),
                    30, 30, artifact(), 8000L, "Missed the Phial of Galadriel");

            entry.type.off(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN);
            entry.type.on(PlayerHistoryType.HIST_ARTIFACT_KNOWN);

            assertFalse(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN),
                    "hist_off cleared it");
            assertTrue(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_KNOWN), "hist_on set it");
            assertTrue(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_LOST),
                    "history_mark_artifact_known does not touch ARTIFACT_LOST");
        }
    }
}
