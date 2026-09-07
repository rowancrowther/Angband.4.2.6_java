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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the private {@link PlayerBirth#saveRollerData}, the port of C's {@code save_roller_data}
 * ({@code player-birth.c:146}) — the snapshot the birth process keeps for undo and quickstart.
 *
 * <p>{@code saveRollerData} is private and instance-scoped, and reads its subject through
 * {@link GameState#getPlayer()} rather than taking one as a parameter, so every test here installs a
 * fixture {@link Player} with {@link GameState#setPlayer} and reaches the method itself through
 * reflection on a freshly constructed {@link PlayerBirth}, restoring whatever {@link GameState} held
 * before.
 *
 * <p>Every expected value below is worked out from {@code save_roller_data} itself, not from reading
 * back what the port happens to produce. Three things this suite exists to pin down, because all
 * three were wrong at some point during the port: {@link BirthCopiesNotLive} pins down that the
 * method reads {@code wt_birth}/{@code ht_birth}/{@code au_birth} and never the live {@code wt}/
 * {@code ht}/{@code au} — an easy field to reach for by name alone; {@link StatLoopBounds} pins down
 * that the stat loop covers exactly the five real stats and not the port's own {@code STAT_NONE}/
 * {@code STAT_MAX} sentinels, which C's {@code for (i = 0; i < STAT_MAX; i++)} has no equivalent of;
 * and {@link HistoryHandoff} pins down that the background-text read and the null-out that follows it
 * hit the <em>same</em> field — {@code toSave.setHistoryBirth(player.getHistoryBirth())} then
 * {@code player.setHistoryBirth(null)} — matching C's {@code tosave->history = player->history;
 * player->history = NULL;} ({@code player-birth.c:165-166}). A field-name collision earlier in the
 * port had the read and the null-out landing on two unrelated fields.
 *
 * <p>Class PlayerBirthSaveRollerDataTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthSaveRollerDataTest {

    /**
     * The five real stats, in C's order.
     */
    private static final Stats[] REAL_STATS = {Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS,
            Stats.STAT_DEX, Stats.STAT_CON};

    /**
     * The port under test.
     */
    private final PlayerBirth playerBirth = new PlayerBirth();

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    /**
     * Whatever {@link GameState} held as the current player before the test, restored afterwards.
     */
    private Player realPlayer;

    /**
     * A class carrying nothing a test here reads — only identity matters, so
     * {@link PlayerBirth#saveRollerData} either copies this exact reference across or it does not.
     *
     * @return the class
     */
    private static PlayerClass testClass() {
        return new PlayerClass("Test Class", List.of(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), 0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                0, 0, 0, List.of(), null);
    }

    private static Field accessibleField(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static void writeInstance(Object target, String name, Object value) throws Exception {
        accessibleField(target.getClass(), name).set(target, value);
    }

    /**
     * Invokes {@link PlayerBirth#saveRollerData} through reflection, since it is private.
     */
    private Birther save(Birther toSave) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("saveRollerData", Birther.class);
        method.setAccessible(true);
        return (Birther) method.invoke(playerBirth, toSave);
    }

    /**
     * Sets every one of the five real stats' birth copy, one distinct value each, and their maximal
     * value to a different set of five, so a test can tell whether a read came from the birth copy or
     * the live one.
     */
    private void seedBirthStats(int str, int intel, int wis, int dex, int con) {
        player.setStatBirth(Stats.STAT_STR, str);
        player.setStatBirth(Stats.STAT_INT, intel);
        player.setStatBirth(Stats.STAT_WIS, wis);
        player.setStatBirth(Stats.STAT_DEX, dex);
        player.setStatBirth(Stats.STAT_CON, con);
    }

    /**
     * A new player, installed as {@link GameState}'s current one, since {@code saveRollerData} reads
     * its subject from there rather than taking a parameter.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);
    }

    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(realPlayer);
    }

    /**
     * The ordinary path: every field {@code save_roller_data} copies ends up in {@code toSave},
     * matching C clause by clause ({@code player-birth.c:151-167}).
     */
    @Nested
    @DisplayName("the ordinary path copies every field save_roller_data does")
    class OrdinaryPath {

        @Test
        @DisplayName("copies race, class, age, birth weight/height/gold, stats, history and name")
        void copiesEveryField() throws Exception {
            PlayerRace race = SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody());
            PlayerClass playerClass = testClass();

            player.setRace(race);
            writeInstance(player, "playerClass", playerClass);
            writeInstance(player, "fullName", "Bilbo");
            player.setAge(42);
            player.setWeightBirth(150);
            player.setHeightBirth(70);
            player.setAUBirth(600);
            player.setHistoryBirth("A rolled background.");
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertSame(race, toSave.getRace());
            assertSame(playerClass, toSave.getPlayerClass());
            assertEquals("Bilbo", toSave.getName());
            assertEquals(42, toSave.getAge());
            assertEquals(150, toSave.getWeight());
            assertEquals(70, toSave.getHeight());
            assertEquals(600L, toSave.getAu());
            assertEquals("A rolled background.", toSave.getHistory());
            assertEquals(18, toSave.getStat().get(Stats.STAT_STR));
            assertEquals(10, toSave.getStat().get(Stats.STAT_INT));
            assertEquals(9, toSave.getStat().get(Stats.STAT_WIS));
            assertEquals(14, toSave.getStat().get(Stats.STAT_DEX));
            assertEquals(17, toSave.getStat().get(Stats.STAT_CON));
        }

        /**
         * {@code toSave} comes back as the same reference passed in, matching C's {@code void}
         * function writing through the pointer it was handed rather than allocating a new one.
         */
        @Test
        @DisplayName("returns the same Birther instance it was given")
        void returnsTheSameInstance() throws Exception {
            seedBirthStats(18, 10, 9, 14, 17);
            Birther toSave = new Birther();
            assertSame(toSave, save(toSave));
        }
    }

    /**
     * C reads {@code wt_birth}/{@code ht_birth}/{@code au_birth}, never the live {@code wt}/
     * {@code ht}/{@code au} ({@code player-birth.c:154-156}) — an easy field to reach for by name
     * alone, since the port keeps both pairs on {@link Player}.
     */
    @Nested
    @DisplayName("reads the birth copies, never the live weight, height or gold")
    class BirthCopiesNotLive {

        @Test
        @DisplayName("weight comes from getWeightBirth, not getWeight")
        void weightIsTheBirthCopy() throws Exception {
            player.setWeight(999);
            player.setWeightBirth(150);
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertEquals(150, toSave.getWeight());
        }

        @Test
        @DisplayName("height comes from getHeightBirth, not getHeight")
        void heightIsTheBirthCopy() throws Exception {
            player.setHeight(1);
            player.setHeightBirth(70);
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertEquals(70, toSave.getHeight());
        }

        @Test
        @DisplayName("gold comes from getAUBirth, not getAU")
        void goldIsTheBirthCopy() throws Exception {
            player.setAU(12_345);
            player.setAUBirth(600);
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertEquals(600L, toSave.getAu());
        }

        @Test
        @DisplayName("stats come from getStatBirth, not getMaxStatValue")
        void statsAreTheBirthCopy() throws Exception {
            player.setStatMax(Stats.STAT_STR, 3);
            player.setStatMax(Stats.STAT_INT, 3);
            player.setStatMax(Stats.STAT_WIS, 3);
            player.setStatMax(Stats.STAT_DEX, 3);
            player.setStatMax(Stats.STAT_CON, 3);
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertEquals(18, toSave.getStat().get(Stats.STAT_STR));
            assertEquals(10, toSave.getStat().get(Stats.STAT_INT));
            assertEquals(9, toSave.getStat().get(Stats.STAT_WIS));
            assertEquals(14, toSave.getStat().get(Stats.STAT_DEX));
            assertEquals(17, toSave.getStat().get(Stats.STAT_CON));
        }
    }

    /**
     * C's loop is {@code for (i = 0; i < STAT_MAX; i++)} over a bare {@code int} array
     * ({@code player-birth.c:159-160}) — five slots, no sentinel among them. The port's
     * {@link Stats} enum carries two sentinels of its own, {@code STAT_NONE} and {@code STAT_MAX},
     * which the loop in {@code saveRollerData} must skip.
     */
    @Nested
    @DisplayName("the stat loop covers exactly the five real stats")
    class StatLoopBounds {

        @Test
        @DisplayName("copies exactly five stats, the sentinels included in neither count nor keys")
        void copiesExactlyFiveRealStats() throws Exception {
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertEquals(REAL_STATS.length, toSave.getStat().size());
            for (Stats stat : REAL_STATS) {
                assertEquals(true, toSave.getStat().containsKey(stat));
            }
            assertFalse(toSave.getStat().containsKey(Stats.STAT_NONE));
            assertFalse(toSave.getStat().containsKey(Stats.STAT_MAX));
        }
    }

    /**
     * C's {@code tosave->history = player->history; player->history = NULL;}
     * ({@code player-birth.c:165-166}) reads and nulls the same field, handing the rolled
     * background-text pointer to {@code toSave} and leaving the player without one. An earlier field-
     * name collision in the port had the read and the null-out landing on two different fields.
     */
    @Nested
    @DisplayName("the history handoff reads and nulls the same field")
    class HistoryHandoff {

        @Test
        @DisplayName("the rolled background text ends up on toSave")
        void backgroundTextIsCopied() throws Exception {
            player.setHistoryBirth("A rolled background.");
            seedBirthStats(18, 10, 9, 14, 17);

            Birther toSave = save(new Birther());

            assertEquals("A rolled background.", toSave.getHistory());
        }

        @Test
        @DisplayName("the player's own copy is left null afterward")
        void playersCopyIsNulledAfterward() throws Exception {
            player.setHistoryBirth("A rolled background.");
            seedBirthStats(18, 10, 9, 14, 17);

            save(new Birther());

            assertNull(player.getHistoryBirth());
        }

        /**
         * A second roll, saved into a fresh {@code toSave} after the first left the player's copy
         * null, must still come through — the null-out from the first call must not leave the second
         * with nothing to read.
         */
        @Test
        @DisplayName("a second roll after the first is still copied")
        void aSecondRollIsStillCopied() throws Exception {
            seedBirthStats(18, 10, 9, 14, 17);
            player.setHistoryBirth("First roll.");
            save(new Birther());

            player.setHistoryBirth("Second roll.");
            Birther second = save(new Birther());

            assertEquals("Second roll.", second.getHistory());
            assertNull(player.getHistoryBirth());
        }
    }
}
