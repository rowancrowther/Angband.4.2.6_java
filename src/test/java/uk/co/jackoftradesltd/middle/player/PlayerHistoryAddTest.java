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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerHistoryType;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerHistory.historyAdd}, {@code PlayerHistory.historyAddWithFlags} and
 * {@code PlayerHistory.historyAddFull} — the ports of C's {@code history_add},
 * {@code history_add_with_flags} and {@code history_add_full}
 * ({@code src/player-history.c:76}, {@code :110}, {@code :127}).
 *
 * <p>Every expectation is read off that C rather than off the port. The three form a funnel: the
 * outer two only decide what to put in an entry, and the inner one is the only place an entry is
 * ever stored, so the tests are arranged the same way — what each wrapper fills in, then what the
 * ledger ends up holding.
 *
 * <p><b>The wrappers differ only in where the numbers come from.</b> {@code history_add_with_flags}
 * reads {@code p->depth}, {@code p->lev} and {@code p->total_energy / 100} off the player, while
 * {@code history_add_full} takes all three as arguments. {@link TheCircumstancesAreReadOffThePlayer}
 * checks the first against a player whose three values are distinct, so an entry stamped with the
 * wrong one cannot pass by coincidence, and {@link TheLedgerIsWrittenInOrder} checks the second
 * against values the player does not hold at all.
 *
 * <p><b>A history turn is not a game turn.</b> C divides {@code total_energy} by one hundred, in
 * integer arithmetic, so the turn recorded is a player-turn and the remainder is dropped.
 * {@link TheTurnIsEnergyOverAHundred} walks the boundary from both sides — 99 records 0 and 100
 * records 1 — because a port that rounded instead of truncating would agree with C on the exact
 * multiples and nowhere else.
 *
 * <p><b>The flags are copied, not borrowed.</b> C's {@code hist_copy} takes the caller's bitflags
 * into the entry, and the difference only shows when a caller reuses its flag set afterwards.
 * {@link TheFlagsAreCopiedNotBorrowed} mutates the set that was passed in and requires the stored
 * entry to be unmoved, which is the one assertion here that a by-reference port would fail.
 *
 * <p><b>Growth past the initial twenty.</b> C allocates twenty slots and adds twenty more when the
 * cursor reaches the end. The port has a list, so there is no arithmetic to check — but the
 * behaviour the arithmetic exists to provide is checked all the same, in
 * {@link TheLedgerIsWrittenInOrder}, by filling well past the C's first two blocks and requiring
 * every entry to survive in the order it was added.
 *
 * <p>Class PlayerHistoryAddTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerHistoryAddTest {

    /**
     * The player under test, fresh for each test since the ledger is mutable and cumulative.
     */
    private Player player;

    /**
     * A minimal artifact. Nothing on it is read — the entry only keeps the reference, standing in
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
     * A new player, whose constructor already gives it an empty ledger.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Writes one of the player's private fields, for the state with no setter.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private void set(String name, Object value) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * @return the entries in the order they were added, C's {@code h->entries} up to {@code h->next}
     */
    private List<HistoryInfo> entries() {
        return player.getPlayerHistory().entries;
    }

    /**
     * @return the only entry in the ledger, failing if there is not exactly one
     */
    private HistoryInfo onlyEntry() {
        assertEquals(1, entries().size(), "exactly one entry should have been added");
        return entries().getFirst();
    }

    /**
     * Calls {@code historyAdd}, which is package-private and so needs no reflection from here.
     *
     * @param text the entry's text
     * @param type the single history type
     * @return the method's answer
     */
    private boolean historyAdd(String text, PlayerHistoryType type) {
        return PlayerHistory.historyAdd(player, text, type);
    }

    /**
     * Calls the private {@code historyAddWithFlags}.
     *
     * @param text     the entry's text
     * @param flags    the history types
     * @param artifact the artifact, or {@code null}
     * @return the method's answer
     * @throws Exception if the method cannot be reached or throws
     */
    private boolean historyAddWithFlags(String text, Flag<PlayerHistoryType> flags,
                                        Artifact artifact) throws Exception {
        Method method = PlayerHistory.class.getDeclaredMethod("historyAddWithFlags", Player.class,
                String.class, Flag.class, Artifact.class);
        method.setAccessible(true);
        return (boolean) method.invoke(null, player, text, flags, artifact);
    }

    /**
     * Calls the private {@code historyAddFull}.
     *
     * @param flags    the history types
     * @param artifact the artifact, or {@code null}
     * @param dLev     the dungeon level to record
     * @param cLev     the character level to record
     * @param turnNo   the turn to record
     * @param text     the entry's text
     * @return the method's answer
     * @throws Exception if the method cannot be reached or throws
     */
    private boolean historyAddFull(Flag<PlayerHistoryType> flags, Artifact artifact, int dLev,
                                   int cLev, int turnNo, String text) throws Exception {
        Method method = PlayerHistory.class.getDeclaredMethod("historyAddFull", Player.class,
                Flag.class, Artifact.class, int.class, int.class, int.class, String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(null, player, flags, artifact, dLev, cLev, turnNo, text);
    }

    /**
     * C's {@code history_add} wipes a local bitflag array, switches one type on, and passes a null
     * artifact. The entry that results should therefore carry that type and no other, and relate to
     * nothing.
     */
    @Nested
    @DisplayName("historyAdd puts one type and no artifact into the entry")
    class HistoryAddCarriesOneType {

        /**
         * The type asked for is on, and the text arrives unaltered.
         */
        @Test
        @DisplayName("the type asked for is the type recorded")
        void theTypeAskedForIsRecorded() {
            assertTrue(historyAdd("Reached level 7", PlayerHistoryType.HIST_GAIN_LEVEL));

            HistoryInfo entry = onlyEntry();
            assertTrue(entry.type.has(PlayerHistoryType.HIST_GAIN_LEVEL), "the type should be on");
            assertEquals("Reached level 7", entry.historyText);
        }

        /**
         * C starts from {@code hist_wipe}, so exactly one type is on and every other is off. Two
         * of the others are named explicitly, since a count alone would not catch a set holding the
         * wrong single type.
         */
        @Test
        @DisplayName("no other type is switched on")
        void noOtherTypeIsOn() {
            historyAdd("Killed Grip, Farmer Maggot's dog", PlayerHistoryType.HIST_SLAY_UNIQUE);

            HistoryInfo entry = onlyEntry();
            assertEquals(1, entry.type.count(), "exactly one type should be on");
            assertFalse(entry.type.has(PlayerHistoryType.HIST_GAIN_LEVEL));
            assertFalse(entry.type.has(PlayerHistoryType.HIST_NONE));
        }

        /**
         * C passes {@code NULL} for the artifact, which reaches the entry as index 0; the port's
         * equivalent is a null reference.
         */
        @Test
        @DisplayName("the entry relates to no artifact")
        void theEntryRelatesToNoArtifact() {
            historyAdd("Began the quest to destroy Morgoth.",
                    PlayerHistoryType.HIST_PLAYER_BIRTH);

            assertNull(onlyEntry().artifact, "historyAdd never names an artifact");
        }
    }

    /**
     * C's {@code history_add_with_flags} is the only place the player's own state is read, and it
     * reads three separate values. The player here holds three distinct numbers so that a stamp
     * taken from the wrong field cannot coincide with the right answer.
     */
    @Nested
    @DisplayName("historyAddWithFlags stamps the entry with the player's circumstances")
    class TheCircumstancesAreReadOffThePlayer {

        /**
         * Depth, character level and turn come from {@code p->depth}, {@code p->lev} and
         * {@code p->total_energy / 100} respectively.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("depth, level and turn are the player's")
        void depthLevelAndTurnAreThePlayers() throws Exception {
            player.setDepth(23);
            set("level", 17);
            player.setTotalEnergy(4200);

            historyAddWithFlags("Found The Phial of Galadriel",
                    new Flag<>(PlayerHistoryType.class, PlayerHistoryType.HIST_ARTIFACT_KNOWN),
                    null);

            HistoryInfo entry = onlyEntry();
            assertEquals(23, entry.dLevel, "dlev is p->depth");
            assertEquals(17, entry.cLevel, "clev is p->lev");
            assertEquals(42L, entry.turn, "turn is p->total_energy / 100");
        }

        /**
         * The artifact reference is handed through untouched, standing in for C passing
         * {@code artifact->aidx}.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("the artifact is passed through")
        void theArtifactIsPassedThrough() throws Exception {
            Artifact phial = artifact();

            historyAddWithFlags("Missed The Phial of Galadriel",
                    new Flag<>(PlayerHistoryType.class, PlayerHistoryType.HIST_ARTIFACT_LOST),
                    phial);

            assertSame(phial, onlyEntry().artifact);
        }

        /**
         * C's {@code history_lose_artifact} switches two types on before calling, so a set holding
         * more than one must survive the funnel intact.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("a set of several types arrives whole")
        void severalTypesArriveWhole() throws Exception {
            Flag<PlayerHistoryType> flags = new Flag<>(PlayerHistoryType.class,
                    PlayerHistoryType.HIST_ARTIFACT_UNKNOWN, PlayerHistoryType.HIST_ARTIFACT_LOST);

            historyAddWithFlags("Missed The Phial of Galadriel", flags, artifact());

            HistoryInfo entry = onlyEntry();
            assertEquals(2, entry.type.count());
            assertTrue(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN));
            assertTrue(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_LOST));
        }
    }

    /**
     * The turn stamped on an entry is {@code p->total_energy / 100} in C integer arithmetic, so the
     * remainder is discarded rather than rounded.
     */
    @Nested
    @DisplayName("the recorded turn is total energy divided by a hundred")
    class TheTurnIsEnergyOverAHundred {

        /**
         * Records an entry at a given energy and reports the turn it was stamped with.
         *
         * @param totalEnergy the player's cumulative energy
         * @return the turn stored on the entry
         */
        private long turnAt(int totalEnergy) {
            player.setTotalEnergy(totalEnergy);
            historyAdd("note", PlayerHistoryType.HIST_USER_INPUT);
            return onlyEntry().turn;
        }

        /**
         * A fresh character has recorded no energy and so is on turn zero.
         */
        @Test
        @DisplayName("no energy is turn zero")
        void noEnergyIsTurnZero() {
            assertEquals(0L, turnAt(0));
        }

        /**
         * The lower side of the boundary: 99 is still turn 0, which is where a port that rounded
         * would answer 1 instead.
         */
        @Test
        @DisplayName("ninety-nine is still turn zero")
        void ninetyNineIsStillTurnZero() {
            assertEquals(0L, turnAt(99));
        }

        /**
         * The exact multiple, the one value both truncation and rounding agree on.
         */
        @Test
        @DisplayName("a hundred is turn one")
        void aHundredIsTurnOne() {
            assertEquals(1L, turnAt(100));
        }

        /**
         * The upper side: the remainder is dropped, not carried.
         */
        @Test
        @DisplayName("a hundred and ninety-nine is still turn one")
        void aHundredAndNinetyNineIsStillTurnOne() {
            assertEquals(1L, turnAt(199));
        }
    }

    /**
     * C's {@code hist_copy} takes the caller's bitflags into the entry rather than pointing at them.
     * Nothing in the game currently reuses a flag set after logging, so this is the one place the
     * difference between a copy and a reference can be seen.
     */
    @Nested
    @DisplayName("the entry keeps its own copy of the flags")
    class TheFlagsAreCopiedNotBorrowed {

        /**
         * The stored entry is unmoved by a type switched on afterwards.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("switching a type on afterwards does not reach the entry")
        void switchingATypeOnDoesNotReachTheEntry() throws Exception {
            Flag<PlayerHistoryType> flags = new Flag<>(PlayerHistoryType.class,
                    PlayerHistoryType.HIST_ARTIFACT_KNOWN);

            historyAddWithFlags("Found The Phial of Galadriel", flags, artifact());
            flags.on(PlayerHistoryType.HIST_ARTIFACT_LOST);

            HistoryInfo entry = onlyEntry();
            assertEquals(1, entry.type.count(), "the entry should hold only what it was given");
            assertFalse(entry.type.has(PlayerHistoryType.HIST_ARTIFACT_LOST));
        }

        /**
         * Nor by the caller wiping its set, which a by-reference port would let empty the entry.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("wiping the caller's set does not empty the entry")
        void wipingTheCallersSetDoesNotEmptyTheEntry() throws Exception {
            Flag<PlayerHistoryType> flags = new Flag<>(PlayerHistoryType.class,
                    PlayerHistoryType.HIST_SLAY_UNIQUE);

            historyAddFull(flags, null, 5, 6, 7, "Killed Fang, Farmer Maggot's dog");
            flags.wipe();

            assertTrue(onlyEntry().type.has(PlayerHistoryType.HIST_SLAY_UNIQUE));
        }
    }

    /**
     * {@code history_add_full} is the only writer. It appends at {@code h->next} and advances, so
     * entries come out in the order they went in, and it grows the array rather than overwriting.
     */
    @Nested
    @DisplayName("historyAddFull appends to the ledger in order")
    class TheLedgerIsWrittenInOrder {

        /**
         * All six fields are the caller's, none the player's — the values here are deliberately
         * unlike anything the fresh player holds.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("every field is the caller's, not the player's")
        void everyFieldIsTheCallers() throws Exception {
            player.setDepth(1);
            set("level", 2);
            player.setTotalEnergy(300);
            Artifact phial = artifact();

            assertTrue(historyAddFull(
                    new Flag<>(PlayerHistoryType.class, PlayerHistoryType.HIST_SAVEFILE_IMPORT),
                    phial, 48, 39, 12345, "Found The Phial of Galadriel"));

            HistoryInfo entry = onlyEntry();
            assertEquals(48, entry.dLevel);
            assertEquals(39, entry.cLevel);
            assertEquals(12345L, entry.turn);
            assertSame(phial, entry.artifact);
            assertEquals("Found The Phial of Galadriel", entry.historyText);
            assertTrue(entry.type.has(PlayerHistoryType.HIST_SAVEFILE_IMPORT));
        }

        /**
         * A fresh player's ledger starts empty, C's {@code h->next == 0}.
         */
        @Test
        @DisplayName("a new player's ledger is empty")
        void aNewPlayersLedgerIsEmpty() {
            assertTrue(entries().isEmpty());
        }

        /**
         * Entries accumulate in call order rather than replacing one another.
         */
        @Test
        @DisplayName("entries keep the order they were added in")
        void entriesKeepTheirOrder() {
            historyAdd("first", PlayerHistoryType.HIST_PLAYER_BIRTH);
            historyAdd("second", PlayerHistoryType.HIST_GAIN_LEVEL);
            historyAdd("third", PlayerHistoryType.HIST_PLAYER_DEATH);

            List<HistoryInfo> entries = entries();
            assertEquals(3, entries.size());
            assertEquals("first", entries.get(0).historyText);
            assertEquals("second", entries.get(1).historyText);
            assertEquals("third", entries.get(2).historyText);
        }

        /**
         * C allocates twenty slots and grows by twenty; fifty entries carries the ledger past both
         * the initial block and the first growth, and every one must still be there in order.
         */
        @Test
        @DisplayName("the ledger grows past its first twenty entries")
        void theLedgerGrowsPastTwenty() {
            for (int i = 0; i < 50; i++) {
                historyAdd("Reached level " + i, PlayerHistoryType.HIST_GAIN_LEVEL);
            }

            List<HistoryInfo> entries = entries();
            assertEquals(50, entries.size());
            assertEquals("Reached level 0", entries.get(0).historyText);
            assertEquals("Reached level 19", entries.get(19).historyText);
            assertEquals("Reached level 20", entries.get(20).historyText);
            assertEquals("Reached level 49", entries.get(49).historyText);
        }

        /**
         * C returns {@code true} unconditionally, and so must the port — there is no failure path
         * for a caller to branch on.
         *
         * @throws Exception if reflection fails
         */
        @Test
        @DisplayName("all three report success")
        void allThreeReportSuccess() throws Exception {
            assertTrue(historyAdd("a", PlayerHistoryType.HIST_GENERIC));
            assertTrue(historyAddWithFlags("b",
                    new Flag<>(PlayerHistoryType.class, PlayerHistoryType.HIST_GENERIC), null));
            assertTrue(historyAddFull(
                    new Flag<>(PlayerHistoryType.class, PlayerHistoryType.HIST_GENERIC),
                    null, 0, 1, 2, "c"));
        }
    }
}
