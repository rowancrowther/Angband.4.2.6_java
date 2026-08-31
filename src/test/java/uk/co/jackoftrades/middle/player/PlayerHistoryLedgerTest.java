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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.player.enums.PlayerHistoryType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerHistory} — the port of C's {@code struct player_history}
 * ({@code src/player-history.h:47}) together with the memory management C keeps beside it in
 * {@code history_init} and {@code history_realloc} ({@code src/player-history.c:36}, {@code :46}).
 *
 * <p>The ledger is tested here on its own, rather than through the {@code Player.historyAdd} funnel
 * that {@link PlayerHistoryAddTest} drives. The funnel decides <em>what</em> goes into an entry;
 * this class is only concerned with <em>where</em> entries end up and in what order, which is the
 * half of {@code history_add_full} that the struct owns.
 *
 * <p><b>Three C fields became one.</b> C carries {@code entries}, {@code next} and {@code length},
 * and the port carries a list. So the expectations are written against what C's arithmetic
 * achieves rather than against the arithmetic itself: a fresh ledger has nothing readable in it
 * ({@code next == 0}), a write lands at the end and nowhere else, and no number of writes loses or
 * reorders what came before, whatever C would have been reallocating at that point.
 *
 * <p><b>Twenty and forty are the interesting counts.</b> C starts with {@code HISTORY_LEN_INIT}
 * slots and adds {@code HISTORY_LEN_INCR} more each time the cursor reaches the end, both twenty.
 * {@link TheLedgerGrowsWithoutLosingEntries} therefore checks the entries either side of both
 * boundaries, since a growth that dropped or duplicated the block it was copying would show there
 * first and only there.
 *
 * <p><b>Entries are stored, not copied.</b> C writes the caller's values into the slot and copies
 * the flags on the way, all inside {@code history_add_full}; by the time an entry reaches the
 * ledger in the port it is already the ledger's own, so what is checked here is that the ledger
 * hands back the very object it was given — anything else would mean a second copy the C does not
 * make.
 *
 * <p>Class PlayerHistoryLedgerTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
class PlayerHistoryLedgerTest {

    /**
     * The ledger under test, fresh for each test since it is mutable and cumulative.
     */
    private PlayerHistory ledger;

    /**
     * An entry carrying the given text. Nothing else on it is read, so the remaining fields hold
     * whatever is cheapest; the text is the label the ordering assertions recognise entries by.
     *
     * @param text the entry's text
     * @return an entry logging a user note at depth zero, level one, turn zero
     */
    private static HistoryInfo entry(String text) {
        return new HistoryInfo(new Flag<>(PlayerHistoryType.class, PlayerHistoryType.HIST_USER_INPUT),
                0, 1, null, 0L, text);
    }

    /**
     * A newly constructed ledger, C's {@code history_init}.
     */
    @BeforeEach
    void newLedger() {
        ledger = new PlayerHistory();
    }

    /**
     * C's {@code history_init} zeroes twenty slots and sets {@code next} to zero, which means a new
     * ledger has an array to write into and nothing readable in it.
     */
    @Nested
    @DisplayName("a new ledger is empty and ready to write to")
    class ANewLedgerIsEmpty {

        /**
         * C's {@code h->next == 0}: no entry is readable, however many slots exist behind it.
         */
        @Test
        @DisplayName("it holds no entries")
        void itHoldsNoEntries() {
            assertNotNull(ledger.entries, "the ledger should never be absent");
            assertTrue(ledger.entries.isEmpty(), "next starts at zero");
        }

        /**
         * C allocates before the first write so that {@code history_add_full} always has somewhere
         * to put an entry; the port allocates in the constructor, and the observable consequence is
         * the same — the first write needs no preparation and lands at index zero.
         */
        @Test
        @DisplayName("the first entry lands at the front")
        void theFirstEntryLandsAtTheFront() {
            HistoryInfo birth = entry("Began the quest to destroy Morgoth.");

            ledger.addEntry(birth);

            assertEquals(1, ledger.entries.size());
            assertSame(birth, ledger.entries.getFirst());
        }
    }

    /**
     * C stores at {@code h->entries[h->next]} and then advances the cursor, so every write goes to
     * the end and nothing already written is touched.
     */
    @Nested
    @DisplayName("entries are appended in the order they were logged")
    class EntriesAreAppendedInOrder {

        /**
         * Three writes, three slots, in call order — C never inserts or replaces.
         */
        @Test
        @DisplayName("each entry follows the last")
        void eachEntryFollowsTheLast() {
            ledger.addEntry(entry("first"));
            ledger.addEntry(entry("second"));
            ledger.addEntry(entry("third"));

            List<HistoryInfo> entries = ledger.entries;
            assertEquals(3, entries.size());
            assertEquals("first", entries.get(0).historyText);
            assertEquals("second", entries.get(1).historyText);
            assertEquals("third", entries.get(2).historyText);
        }

        /**
         * The entry object itself is what the ledger holds. C's copying is done by the caller
         * before it arrives — the port's {@code Player.historyAddFull} builds the entry with its
         * own flag set — so a ledger that copied again would be doing work C does not.
         */
        @Test
        @DisplayName("the entry given is the entry kept")
        void theEntryGivenIsTheEntryKept() {
            HistoryInfo slain = entry("Killed Grip, Farmer Maggot's dog");

            ledger.addEntry(slain);

            assertSame(slain, ledger.entries.getFirst());
        }

        /**
         * C compares nothing before writing, so two identical events are two entries. This matters
         * for real play: a character who gains the same level twice after a drain, or finds and
         * re-finds an artifact, should have both events in the log.
         */
        @Test
        @DisplayName("an identical event logged twice is logged twice")
        void anIdenticalEventLoggedTwiceIsLoggedTwice() {
            ledger.addEntry(entry("Reached level 12"));
            ledger.addEntry(entry("Reached level 12"));

            assertEquals(2, ledger.entries.size(), "the ledger never deduplicates");
        }

        /**
         * The same entry object logged twice is likewise two slots in C, both holding a copy of the
         * same values. Nothing in the game does this, but it is where a ledger that mistook the
         * object for a key would collapse two events into one.
         */
        @Test
        @DisplayName("the same entry object logged twice occupies two slots")
        void theSameEntryObjectLoggedTwiceOccupiesTwoSlots() {
            HistoryInfo note = entry("A note");

            ledger.addEntry(note);
            ledger.addEntry(note);

            assertEquals(2, ledger.entries.size());
            assertSame(note, ledger.entries.get(0));
            assertSame(note, ledger.entries.get(1));
        }
    }

    /**
     * C's {@code HISTORY_LEN_INIT} is twenty and {@code HISTORY_LEN_INCR} is twenty, so the array
     * is reallocated as the twenty-first and forty-first entries are written. The port has no
     * arithmetic to check, but the behaviour the arithmetic exists to provide is checked all the
     * same: nothing is lost, reordered or duplicated across either boundary.
     */
    @Nested
    @DisplayName("the ledger grows past its allocated blocks")
    class TheLedgerGrowsWithoutLosingEntries {

        /**
         * Logs {@code count} entries labelled by their index.
         *
         * @param count how many entries to log
         */
        private void fill(int count) {
            for (int i = 0; i < count; i++) {
                ledger.addEntry(entry("entry " + i));
            }
        }

        /**
         * Exactly twenty entries fill C's first allocation without triggering a realloc — the last
         * value where no growth has happened yet.
         */
        @Test
        @DisplayName("twenty entries fit the first block")
        void twentyEntriesFitTheFirstBlock() {
            fill(20);

            assertEquals(20, ledger.entries.size());
            assertEquals("entry 0", ledger.entries.get(0).historyText);
            assertEquals("entry 19", ledger.entries.get(19).historyText);
        }

        /**
         * The twenty-first entry is the one C reallocates for: {@code h->next == h->length}, so the
         * array is copied into a larger one before the write. The entry either side of the join is
         * what a bad copy would disturb.
         */
        @Test
        @DisplayName("the twenty-first entry follows the twentieth")
        void theTwentyFirstEntryFollowsTheTwentieth() {
            fill(21);

            assertEquals(21, ledger.entries.size());
            assertEquals("entry 19", ledger.entries.get(19).historyText);
            assertEquals("entry 20", ledger.entries.get(20).historyText);
        }

        /**
         * The second growth, at forty-one, which is where a port that grew only once would fail
         * even though it passed at twenty-one.
         */
        @Test
        @DisplayName("the forty-first entry follows the fortieth")
        void theFortyFirstEntryFollowsTheFortieth() {
            fill(41);

            assertEquals(41, ledger.entries.size());
            assertEquals("entry 39", ledger.entries.get(39).historyText);
            assertEquals("entry 40", ledger.entries.get(40).historyText);
        }

        /**
         * Well past both boundaries, every entry still in its place. A long character's log runs to
         * hundreds of entries, so the ledger has to keep growing indefinitely rather than wrapping
         * or capping.
         */
        @Test
        @DisplayName("a hundred entries are all present and in order")
        void aHundredEntriesAreAllPresentAndInOrder() {
            fill(100);

            assertEquals(100, ledger.entries.size());
            for (int i = 0; i < 100; i++) {
                assertEquals("entry " + i, ledger.entries.get(i).historyText,
                        "entry " + i + " should be at index " + i);
            }
        }
    }

    /**
     * Two ledgers are two logs. C reaches its through {@code &p->hist}, one struct per player, and
     * the port allocates a list per instance — a static or shared list would put one character's
     * events in another's history.
     */
    @Nested
    @DisplayName("each ledger is its own log")
    class EachLedgerIsItsOwnLog {

        /**
         * Writing to one ledger leaves another untouched.
         */
        @Test
        @DisplayName("logging to one does not reach the other")
        void loggingToOneDoesNotReachTheOther() {
            PlayerHistory other = new PlayerHistory();

            ledger.addEntry(entry("mine"));

            assertEquals(1, ledger.entries.size());
            assertTrue(other.entries.isEmpty(), "the second ledger should still be empty");
        }
    }
}
