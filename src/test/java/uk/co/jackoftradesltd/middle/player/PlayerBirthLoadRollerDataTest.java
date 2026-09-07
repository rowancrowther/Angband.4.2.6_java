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
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the private {@link PlayerBirth#LoadRollerData}, the port of C's {@code load_roller_data}
 * ({@code player-birth.c:181-225}) — the other half of the roller's undo, restoring a
 * {@link Birther} snapshot onto the live player and, if the caller wants it, handing back what the
 * player held before.
 *
 * <p>C documents that {@code saved} and {@code prev_player} may be the very same struct
 * ({@code player-birth.c:177-178}), and its one real caller does exactly that -
 * {@code load_roller_data(&prev, &prev)} ({@code player-birth.c:1211}) - so the "flick between two
 * rolls" is a swap of one struct's contents with the live player's. {@link SavedAndPrevPlayerAreTheSameObject}
 * pins that case down; {@link PrevPlayerCapturesPriorState} pins down the simpler two-object case the
 * same code path also has to get right.
 *
 * <p>Every expected value below is worked out from {@code load_roller_data} itself, not from reading
 * back what the port happens to produce. Two things this suite exists to pin down, because both were
 * wrong at some point during the port: {@link GoldIsReset} pins down that the live purse is reset to
 * the data file's starting gold rather than carried over from {@code saved}, matching C's
 * {@code player->au = z_info->start_gold} rather than {@code player->au = saved->au}; and
 * {@link ReturnsTheSameReference} pins down that the caller's own {@code prevPlayer} object is
 * mutated and handed back, not replaced with an unrelated copy - C's raw
 * {@code *prev_player = temp;} ({@code player-birth.c:223}) writes through the caller's own pointer,
 * and a port that instead reassigned a local variable to a fresh object would silently break the one
 * real C caller's swap.
 *
 * <p>Class PlayerBirthLoadRollerDataTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthLoadRollerDataTest {

    /**
     * The five real stats, in C's order.
     */
    private static final Stats[] REAL_STATS = {Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS,
            Stats.STAT_DEX, Stats.STAT_CON};

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}), the value
     * {@code load_roller_data} resets the live purse to regardless of what {@code saved} carries.
     */
    private static final int START_GOLD = 600;

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
     * The constants table as it was before the test replaced it.
     */
    private Object savedConstants;

    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static void seedConstants(int startGold) throws Exception {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, startGold, 5000),
                null, null, null, null, null, null, null, null));
    }

    /**
     * A class carrying nothing a test here reads — only identity matters, so
     * {@link PlayerBirth#LoadRollerData} either shares this exact reference or it does not.
     *
     * @return the class
     */
    private static PlayerClass testClass() {
        return new PlayerClass("Test Class", List.of(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), 0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                0, 0, 0, List.of(), null);
    }

    /**
     * A race carrying nothing a test here reads — only identity matters. A fresh object each call, so
     * two calls give two references a test can tell apart.
     *
     * @return the race
     */
    private static PlayerRace testRace() {
        return SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody());
    }

    /**
     * Builds a {@link Birther} with one distinct value per field, so a test can tell whether a read
     * came from {@code saved} or somewhere else.
     */
    private static Birther savedBirther(PlayerRace race, PlayerClass playerClass) {
        Birther saved = new Birther();
        saved.setRace(race);
        saved.setPlayerClass(playerClass);
        saved.setAge(42);
        saved.setWeight(150);
        saved.setHeight(70);
        saved.setAu(900L);
        saved.setStat(Stats.STAT_STR, 18);
        saved.setStat(Stats.STAT_INT, 10);
        saved.setStat(Stats.STAT_WIS, 9);
        saved.setStat(Stats.STAT_DEX, 14);
        saved.setStat(Stats.STAT_CON, 17);
        saved.setHistoryBirth("A rolled background.");
        saved.setName("Bilbo");
        return saved;
    }

    /**
     * Invokes {@link PlayerBirth#LoadRollerData} through reflection, since it is private.
     */
    private Birther load(Birther saved, Birther prevPlayer) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("LoadRollerData", Birther.class, Birther.class);
        method.setAccessible(true);
        return (Birther) method.invoke(playerBirth, saved, prevPlayer);
    }

    /**
     * Reads the player's private {@code statMap} field, which has no getter - C's
     * {@code p->stat_map[stat] = stat}, the identity scramble the load writes.
     */
    @SuppressWarnings("unchecked")
    private Stats currentMap(Stats stat) throws Exception {
        Field field = Player.class.getDeclaredField("statMap");
        field.setAccessible(true);
        Map<Stats, Stats> map = (Map<Stats, Stats>) field.get(player);
        return map.get(stat);
    }

    /**
     * Seeds the live player with a state entirely distinct from {@link #savedBirther}, so a test can
     * tell the two apart after a call that touches both.
     */
    private void seedPriorPlayerState(PlayerRace race, PlayerClass playerClass) {
        player.setRace(race);
        player.setClass(playerClass);
        player.setAge(33);
        player.setWeight(999);
        player.setWeightBirth(200);
        player.setHeight(1);
        player.setHeightBirth(65);
        player.setAU(12_345);
        player.setAUBirth(700L);
        player.setStatBirth(Stats.STAT_STR, 8);
        player.setStatBirth(Stats.STAT_INT, 8);
        player.setStatBirth(Stats.STAT_WIS, 8);
        player.setStatBirth(Stats.STAT_DEX, 8);
        player.setStatBirth(Stats.STAT_CON, 8);
        player.setHistoryBirth("Old background.");
        player.setFullName("OldName");
    }

    @BeforeEach
    void seedAndBuild() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants(START_GOLD);
        player = new Player();
        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);
    }

    @AfterEach
    void restore() throws Exception {
        GameState.setPlayer(realPlayer);
        constantsField().set(null, savedConstants);
    }

    /**
     * The ordinary path: every field {@code load_roller_data} writes onto the live player from
     * {@code saved}, matching C clause by clause ({@code player-birth.c:196-216}).
     */
    @Nested
    @DisplayName("loads every field save_roller_data would have saved")
    class OrdinaryLoad {

        @Test
        @DisplayName("copies race, class, age, weight/height, stats, history and name onto the player")
        void copiesEveryField() throws Exception {
            PlayerRace race = testRace();
            PlayerClass playerClass = testClass();
            Birther saved = savedBirther(race, playerClass);

            Birther result = load(saved, null);

            assertSame(race, player.getRace());
            assertSame(playerClass, player.getPlayerClass());
            assertEquals(42, player.getAge());
            assertEquals(150, player.getWeight());
            assertEquals(70, player.getHeight());
            assertEquals(900L, player.getAUBirth());
            assertEquals("A rolled background.", player.getHistoryBirth());
            assertEquals("Bilbo", player.getFullName());
            assertEquals(18, player.getStatMax(Stats.STAT_STR));
            assertEquals(10, player.getStatMax(Stats.STAT_INT));
            assertEquals(9, player.getStatMax(Stats.STAT_WIS));
            assertEquals(14, player.getStatMax(Stats.STAT_DEX));
            assertEquals(17, player.getStatMax(Stats.STAT_CON));
            assertNull(result);
        }
    }

    /**
     * {@code player->wt = player->wt_birth = saved->wt} and
     * {@code player->ht = player->ht_birth = saved->ht} ({@code player-birth.c:199-200}) - one figure
     * from {@code saved} lands in both the live and the birth field, unlike the gold, which splits
     * (see {@link GoldIsReset}).
     */
    @Nested
    @DisplayName("weight and height write both the live field and the birth copy")
    class WeightAndHeightWriteBothFields {

        @Test
        @DisplayName("weight and height both take saved's single figure")
        void writeOneFigureToBoth() throws Exception {
            Birther saved = savedBirther(testRace(), testClass());

            load(saved, null);

            assertEquals(150, player.getWeight());
            assertEquals(150, player.getWeightBirth());
            assertEquals(70, player.getHeight());
            assertEquals(70, player.getHeightBirth());
        }
    }

    /**
     * {@code player->au_birth = saved->au; player->au = z_info->start_gold;}
     * ({@code player-birth.c:201-202}) - unlike weight and height, gold does not carry {@code saved}'s
     * figure into both fields. The birth copy takes {@code saved}'s gold; the live purse is reset to
     * the data file's starting figure regardless of what {@code saved} held.
     */
    @Nested
    @DisplayName("gold is reset to the data file's figure, not carried from saved")
    class GoldIsReset {

        @Test
        @DisplayName("au_birth takes saved's gold; au takes the data file's starting gold")
        void auBirthFromSavedAuFromConstants() throws Exception {
            Birther saved = savedBirther(testRace(), testClass());
            saved.setAu(9_999L);

            load(saved, null);

            assertEquals(9_999L, player.getAUBirth());
            assertEquals(START_GOLD, player.getAU());
        }
    }

    /**
     * {@code player->stat_max[i] = player->stat_cur[i] = player->stat_birth[i] = saved->stat[i];
     * player->stat_map[i] = i;} ({@code player-birth.c:206-208}) - all four targets take
     * {@code saved}'s figure, and the scramble map resets to the identity permutation.
     */
    @Nested
    @DisplayName("each real stat writes all four targets")
    class StatsWriteAllFourTargets {

        @Test
        @DisplayName("max, current, birth and the identity scramble map all take saved's value")
        void writesMaxCurrentBirthAndIdentityMap() throws Exception {
            Birther saved = savedBirther(testRace(), testClass());

            load(saved, null);

            int[] expected = {18, 10, 9, 14, 17};
            for (int i = 0; i < REAL_STATS.length; i++) {
                Stats stat = REAL_STATS[i];
                assertEquals(expected[i], player.getStatMax(stat));
                assertEquals(expected[i], player.getCurStatValue(stat));
                assertEquals(expected[i], player.getStatBirth(stat));
                assertEquals(stat, currentMap(stat));
            }
        }
    }

    /**
     * The one subtlety in reusing {@link PlayerBirth#saveRollerData} to capture the outgoing state:
     * it nulls the player's {@code historyBirth} as a side effect ({@code player-birth.c:167}), and
     * that happens <em>before</em> this method's own history write further down
     * ({@code player-birth.c:212-215}). The player must not be left with a null history at the end of
     * the call just because the capture step passed through it on the way.
     */
    @Nested
    @DisplayName("history survives the mid-call null-out from the capture step")
    class HistorySurvivesTheCapture {

        @Test
        @DisplayName("the player's history is saved's, not null, once prevPlayer is captured too")
        void historyIsSavedsNotNull() throws Exception {
            seedPriorPlayerState(testRace(), testClass());
            Birther saved = savedBirther(testRace(), testClass());

            load(saved, new Birther());

            assertEquals("A rolled background.", player.getHistoryBirth());
        }
    }

    /**
     * {@code prev_player == NULL} skips the capture entirely ({@code player-birth.c:192, 219}); the
     * port's {@code prevPlayer == null} guard does the same, and the load onto the player still runs.
     */
    @Nested
    @DisplayName("a null prevPlayer skips the capture and still loads the player")
    class PrevPlayerNull {

        @Test
        @DisplayName("returns null and still loads every field onto the player")
        void returnsNullAndStillLoads() throws Exception {
            Birther saved = savedBirther(testRace(), testClass());

            Birther result = load(saved, null);

            assertNull(result);
            assertEquals(150, player.getWeight());
            assertEquals("Bilbo", player.getFullName());
        }
    }

    /**
     * The two-object case: {@code saved} and {@code prevPlayer} are different snapshots, so the
     * capture ({@code save_roller_data(&temp)}, {@code player-birth.c:193}) and the final
     * {@code *prev_player = temp;} ({@code player-birth.c:223}) write onto an object the load never
     * reads from.
     */
    @Nested
    @DisplayName("a distinct prevPlayer captures the player's state from before the load")
    class PrevPlayerCapturesPriorState {

        @Test
        @DisplayName("prevPlayer ends up holding what the player held before the call")
        void prevPlayerHoldsThePriorState() throws Exception {
            PlayerRace priorRace = testRace();
            PlayerClass priorClass = testClass();
            seedPriorPlayerState(priorRace, priorClass);
            Birther saved = savedBirther(testRace(), testClass());
            Birther prevPlayer = new Birther();

            Birther result = load(saved, prevPlayer);

            assertSame(prevPlayer, result);
            assertSame(priorRace, result.getRace());
            assertSame(priorClass, result.getPlayerClass());
            assertEquals(33, result.getAge());
            assertEquals(200, result.getWeight());
            assertEquals(65, result.getHeight());
            assertEquals(700L, result.getAu());
            assertEquals(8, result.getStat().get(Stats.STAT_STR));
            assertEquals(8, result.getStat().get(Stats.STAT_CON));
            assertEquals("Old background.", result.getHistory());
            assertEquals("OldName", result.getName());
        }
    }

    /**
     * C's documented, and only real, caller shape: the same struct for both parameters
     * ({@code player-birth.c:177-178, 1211}) - a swap of {@code saved}'s contents onto the player and
     * the player's own outgoing state back onto the very same object.
     */
    @Nested
    @DisplayName("saved and prevPlayer as the same object swap their contents")
    class SavedAndPrevPlayerAreTheSameObject {

        @Test
        @DisplayName("the player ends up with the snapshot's old values, and the snapshot with the player's")
        void swapsContents() throws Exception {
            PlayerRace priorRace = testRace();
            PlayerClass priorClass = testClass();
            seedPriorPlayerState(priorRace, priorClass);

            PlayerRace savedRace = testRace();
            PlayerClass savedClass = testClass();
            Birther snapshot = savedBirther(savedRace, savedClass);

            Birther result = load(snapshot, snapshot);

            assertSame(snapshot, result);

            // The player now holds what the snapshot held going in.
            assertSame(savedRace, player.getRace());
            assertEquals(150, player.getWeight());
            assertEquals("Bilbo", player.getFullName());

            // The snapshot now holds what the player held going in.
            assertSame(priorRace, result.getRace());
            assertSame(priorClass, result.getPlayerClass());
            assertEquals(33, result.getAge());
            assertEquals(200, result.getWeight());
            assertEquals(65, result.getHeight());
            assertEquals(700L, result.getAu());
            assertEquals("Old background.", result.getHistory());
            assertEquals("OldName", result.getName());
        }
    }

    /**
     * The whole point of returning the same reference: a caller following the same
     * "reassign from the return value" convention {@link PlayerBirth#saveRollerData} already uses
     * must not be handed an unrelated object.
     */
    @Nested
    @DisplayName("the return value is the same prevPlayer reference, mutated in place")
    class ReturnsTheSameReference {

        @Test
        @DisplayName("returns exactly the object passed in as prevPlayer")
        void returnsTheSameInstance() throws Exception {
            seedPriorPlayerState(testRace(), testClass());
            Birther saved = savedBirther(testRace(), testClass());
            Birther prevPlayer = new Birther();

            Birther result = load(saved, prevPlayer);

            assertSame(prevPlayer, result);
        }
    }
}
