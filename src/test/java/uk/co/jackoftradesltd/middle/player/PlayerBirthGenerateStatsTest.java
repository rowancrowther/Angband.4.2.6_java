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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the private {@link PlayerBirth#generateStats}, the port of C's {@code generate_stats}
 * ({@code player-birth.c:824-981}), the point-buy birth screen's auto-generate heuristic.
 *
 * <p>{@code generateStats} is private and instance-scoped, and nothing in the port calls it yet -
 * the same gap noted on the method's own Javadoc. Every test here reaches it through reflection on
 * a freshly constructed {@link PlayerBirth}.
 *
 * <p><b>Three things this suite exists to pin down, because all three were wrong at some point
 * during the port.</b> {@link CaseFourRegression} pins down the one that mattered in play: step 4
 * once bought a stat once and then sold it straight back down to 10 instead of buying it up to
 * whatever the points could afford, which is the opposite of what C's
 * {@code while (buy_stat(...));} does. {@link WarriorFullRun} and {@link CasterFullRun} each run
 * the whole method start to finish and would have caught the other two: a {@code maxed} flag set
 * only when a sell succeeded rather than unconditionally, and a no-spells fallback that pointed at
 * the {@code STAT_NONE} sentinel instead of C's literal {@code 0} ({@code STAT_STR}).
 *
 * <p>Every expected number below is worked out from {@link PlayerBirth#birthStatCosts} and, for
 * {@link WarriorFullRun}, from the real {@code adjStrBlow}/{@code adjDexBlow}/{@code blowsTable}
 * data in {@code StatTables} - the same tables C's {@code calc_blows} uses - not from reading back
 * what the port happens to produce.
 *
 * <p>Class PlayerBirthGenerateStatsTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthGenerateStatsTest {

    /**
     * {@code birth_stat_costs[11]} through {@code [16]} - the flat cost of every point-buy point
     * from base 10 up to 16.
     */
    private static final int COST_FLAT = 1;

    /**
     * A placeholder starting gold; no test here reads it.
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
     * Whatever bus was installed before the test, restored afterwards.
     */
    private EventsHandler realBus;

    /**
     * Whether a character was generated before the test, put back afterwards.
     */
    private boolean realCharacterGenerated;

    /**
     * The constants table as it was before the test replaced it.
     */
    private Object savedConstants;

    private static Field constantsField() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static void seedConstants(int startGold) throws ReflectiveOperationException {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, startGold, 5000),
                null, null, null, null, null, null, null, null));
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
     * Five distinct values, one per real stat, in the order the shipped enum declares them.
     */
    private static Map<Stats, Integer> statsMap(int str, int intel, int wis, int dex, int con) {
        Map<Stats, Integer> stats = new HashMap<>();
        stats.put(Stats.STAT_STR, str);
        stats.put(Stats.STAT_INT, intel);
        stats.put(Stats.STAT_WIS, wis);
        stats.put(Stats.STAT_DEX, dex);
        stats.put(Stats.STAT_CON, con);
        return stats;
    }

    /**
     * A class with no skills, no stat adjustments and no flags of its own - only the four fields
     * each test cares about vary.
     *
     * @param maxAttacks    caps blows per turn and picks the caster ({@code < 5}) / warrior
     *                      ({@code > 5}) branch
     * @param minWeight     the divisor {@code calcBlows} falls back to when unarmed
     * @param attMultiplier the numerator {@code calcBlows} scales strength by
     * @param magic         the class's spellcasting, or {@link ClassMagic#NONE} for none
     * @return the class
     */
    private static PlayerClass customClass(int maxAttacks, int minWeight, int attMultiplier,
                                           ClassMagic magic) {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        return new PlayerClass("Test Class", List.of(), stats, skills, new HashMap<>(skills), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                maxAttacks, minWeight, attMultiplier, List.of(), magic);
    }

    /**
     * A class-magic profile with one book, one spell, and the given realm stat - enough to make
     * {@code generateStats}'s {@code spellStat} resolve to it.
     */
    private static ClassMagic magicWithStat(Stats stat) {
        MagicRealm realm = new MagicRealm("Test Realm", stat, "casts", "spell", TValue.TV_MAGIC_BOOK);
        MagicBook book = new MagicBook(TValue.TV_MAGIC_BOOK, false, "Test Book", 1, realm);
        return new ClassMagic(1, 0, 1, List.of(book));
    }

    /**
     * Invokes {@link PlayerBirth#generateStats} through reflection, since it is private.
     */
    private int generate(Map<Stats, Integer> st, Map<Stats, Integer> spent, Map<Stats, Integer> inc,
                         int left) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("generateStats", Map.class, Map.class,
                Map.class, int.class);
        method.setAccessible(true);
        return (int) method.invoke(playerBirth, st, spent, inc, left);
    }

    /**
     * A plain character with the shipped starting gold published - {@code generateStats} always
     * finishes with one unconditional {@link PlayerBirth#recalculateStats} call, so every test needs
     * a character that call can run against, whether or not that test's own path through the method
     * ever sets {@code updateDisplay}.
     */
    @BeforeEach
    void newCharacter() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants(START_GOLD);

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(new NoOpBus());

        realCharacterGenerated = GameWorld.characterGenerated;
        GameWorld.characterGenerated = false;
    }

    @AfterEach
    void restoreGlobals() throws Exception {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        GameWorld.characterGenerated = realCharacterGenerated;
        constantsField().set(null, savedConstants);
    }

    /**
     * An {@link EventsHandler} that does nothing - {@code recalculateStats} needs one installed, but
     * these tests are about {@code generateStats}'s own arithmetic, not the events it triggers.
     */
    private static class NoOpBus implements EventsHandler {
        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData eventData) {
        }
    }

    /**
     * Step 4 - C's {@code while (buy_stat(next_stat, ...));} followed by
     * {@code maxed[next_stat] = true}: buy a stat repeatedly until a purchase fails, then stop. The
     * port once bought it once and then sold it back to 10 instead - this pins that fix down with a
     * fixture where steps 0-3 are no-ops, so only step 4 has anything to do.
     */
    @Nested
    @DisplayName("step 4 buys a stat up to its ceiling, not once-then-refunded")
    class CaseFourRegression {

        /**
         * A caster (so steps 1-2 never run) whose STR, spell-stat and CON are all pre-set past
         * where steps 0 and 3 would touch them, leaving DEX - untouched, and so unmaxed - as step
         * 4's only candidate. With 5 points and DEX at base 10, C buys 10 &rarr; 15 (five flat-cost
         * points) and stops there, since the sixth costs a sixth point this fixture doesn't have.
         * The old buy-once-then-sell bug would instead have bought once and sold straight back to
         * 10, refunding every point.
         */
        @Test
        @DisplayName("buys DEX from 10 to 15 with 5 points, rather than selling it back to 10")
        void buysDexToItsAffordableCeiling() throws Exception {
            writeInstance(player, "playerClass",
                    customClass(2, 30, 5, magicWithStat(Stats.STAT_INT)));

            Map<Stats, Integer> st = statsMap(17, 18, 10, 10, 16);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(99, 99, 99, COST_FLAT, 99);

            int left = generate(st, spent, inc, 5);

            assertEquals(0, left, "all 5 points should be spent buying DEX up");
            assertEquals(15, st.get(Stats.STAT_DEX), "DEX should climb to what 5 flat-cost points buy");
            assertEquals(10, st.get(Stats.STAT_WIS), "WIS is never touched by this path");
            assertEquals(17, st.get(Stats.STAT_STR), "STR was pre-maxed, step 0 must be a no-op");
            assertEquals(18, st.get(Stats.STAT_INT), "INT (the spell stat) was pre-capped, step 3 must be a no-op");
            assertEquals(16, st.get(Stats.STAT_CON), "CON was pre-capped at 16, step 3 must be a no-op");
        }
    }

    /**
     * A full run for a non-caster, non-magic class - C's warrior branch of step 3, and the real
     * {@code STAT_DEX} breakpoint-tracking of steps 1-2.
     *
     * <p>With {@code minWeight} 100 and {@code attMultiplier} 1, {@code calcBlows}'s strength rung
     * saturates at row 0 of {@code blowsTable} for any point-buy STR ({@code adjStrBlow[17] * 1 / 100}
     * is 0 by integer division), and that row's dexterity columns 1 and 2 - DEX 10-16 and DEX 17 -
     * give energies 100 and 95, i.e. 100 and 105 blows-per-turn-times-100. Both divided by 10 answer
     * 10, no more than the loop's own starting {@code blows} of 10, so the breakpoint tracked in
     * step 1 never moves off its initial value: every point bought into DEX in step 1 is sold back
     * out again in step 2.
     */
    @Nested
    @DisplayName("a warrior's full run: STR, a wasted DEX excursion, CON, then DEX again")
    class WarriorFullRun {

        /**
         * 20 points: STR to 17 costs 8 (step 0); DEX to 17 costs a further 8 (step 1) and is sold
         * straight back to 10 (step 2), refunding the 8; CON to 16 costs 6 of the 12 left (the
         * warrior branch of step 3, which allows spending all of what's left rather than half); the
         * remaining 6 buy DEX from 10 to 16 (step 4), exhausting the points before the seventh
         * point (to 17, costing 2) can be afforded.
         */
        @Test
        @DisplayName("ends at STR 17, DEX 16, CON 16, WIS/INT untouched, with every point spent")
        void runsTheWholeSequence() throws Exception {
            writeInstance(player, "playerClass", customClass(6, 100, 1, ClassMagic.NONE));

            Map<Stats, Integer> st = statsMap(10, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT);

            int left = generate(st, spent, inc, 20);

            assertEquals(0, left, "all 20 points should end up spent");
            assertEquals(17, st.get(Stats.STAT_STR));
            assertEquals(16, st.get(Stats.STAT_DEX), "bought to 17, sold back to 10, then rebought to 16");
            assertEquals(16, st.get(Stats.STAT_CON));
            assertEquals(10, st.get(Stats.STAT_WIS), "no spell stat and not warrior-favoured, so untouched");
            assertEquals(10, st.get(Stats.STAT_INT), "no spell stat and not warrior-favoured, so untouched");
        }
    }

    /**
     * A full run for a caster whose spell stat is {@code STAT_INT} - C's caster skip from step 0 to
     * step 3, the caster's unbounded spend on its spell stat (past the base-16 cap a non-pure class
     * would face), and step 4's exclusion of whichever of {@code STAT_INT}/{@code STAT_WIS} is the
     * spell stat.
     */
    @Nested
    @DisplayName("a caster's full run: INT past 16, CON to 16, then DEX and WIS in step 4")
    class CasterFullRun {

        /**
         * 40 points: STR to 17 costs 8 (step 0, then jumps straight to step 3); with 32 left, half
         * (16) goes toward INT, which - being the caster's spell stat - is allowed past the
         * non-caster base-16 cap and reaches 18 for 12 of those 16 points before the point-buy
         * ceiling itself refuses a nineteenth point; the other half goes toward CON, capped at 16
         * for 6 points. Of the 14 points remaining, step 4 spends 12 maxing DEX to 18 (skipping
         * INT, already maxed, and never offering it a second look), then the last 2 buy WIS from
         * 10 to 12 before running out.
         */
        @Test
        @DisplayName("ends at STR 17, INT 18, DEX 18, CON 16, WIS 12, with every point spent")
        void runsTheWholeSequence() throws Exception {
            writeInstance(player, "playerClass",
                    customClass(2, 30, 5, magicWithStat(Stats.STAT_INT)));

            Map<Stats, Integer> st = statsMap(10, 10, 10, 10, 10);
            Map<Stats, Integer> spent = statsMap(0, 0, 0, 0, 0);
            Map<Stats, Integer> inc = statsMap(COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT, COST_FLAT);

            int left = generate(st, spent, inc, 40);

            assertEquals(0, left, "all 40 points should end up spent");
            assertEquals(17, st.get(Stats.STAT_STR));
            assertEquals(18, st.get(Stats.STAT_INT), "the spell stat, allowed past the base-16 cap");
            assertEquals(16, st.get(Stats.STAT_CON));
            assertEquals(18, st.get(Stats.STAT_DEX), "step 4's first candidate, bought to the point-buy ceiling");
            assertEquals(12, st.get(Stats.STAT_WIS), "step 4's second candidate, INT being excluded as the spell stat");
        }
    }
}
