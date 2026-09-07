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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#doCmdChooseRace}, the port of C's {@code do_cmd_choose_race}
 * ({@code player-birth.c:1110-1119}).
 *
 * <p>The C is short:
 *
 * <pre>{@code
 * void do_cmd_choose_race(struct command *cmd)
 * {
 *         int choice;
 *         cmd_get_arg_choice(cmd, "choice", &choice);
 *         player_generate(player, player_id2race(choice), NULL, false);
 *
 *         reset_stats(stats, points_spent, points_inc, &points_left, false);
 *         generate_stats(stats, points_spent, points_inc, &points_left);
 *         rolled_stats = false;
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#playerGenerate}, {@link
 * PlayerBirth#resetStats} and the private {@code generateStats} each have their own suite; what is
 * left here is the wiring between them, in C's own order, plus the one place the wiring gets wrong
 * without an explicit write: C threads {@code points_left} through both {@code reset_stats} and
 * {@code generate_stats} by pointer, so the final total lands in the shared global for free. The
 * port's two helpers return the new total instead, so {@code doCmdChooseRace} must capture
 * {@code generateStats}'s return and write it back to {@link PlayerBirthStateRegistry#setPointsLeft}
 * itself - an earlier draft dropped that second write, leaving the registry stuck on
 * {@code resetStats}'s total. {@link #wiresGenerateStatsPointsLeftBackToTheRegistry} pins that down.
 *
 * <p><b>The deliberate divergence.</b> C never checks {@code cmd_get_arg_choice}'s return value, so
 * on the arg-missing path it falls through and calls {@code player_id2race} on whatever garbage was
 * left on the stack - dead-but-present behaviour, since every producer of {@code CMD_CHOOSE_RACE} in
 * the C tree sets the arg on the same command it just pushed. The port instead returns early when
 * the arg is absent. {@link #argMissingDoesNothing} exercises that path and confirms nothing in the
 * pipeline runs, matching the method's own Javadoc rather than C's literal fallthrough.
 *
 * <p>The fixture is the union of what {@link PlayerBirthPlayerGenerateTest} needs for
 * {@code playerGenerate}'s own history/AHW rolls and what {@link PlayerBirthGenerateStatsTest} needs
 * for {@code generateStats}'s auto-buy pass and its trailing, unconditional
 * {@link PlayerBirth#recalculateStats} call: {@link CalcBonusesFixture#plainCharacter} supplies a
 * character {@code getBonuses} can run against, then the class is overwritten with the same
 * non-caster, non-warrior-favoured profile {@code PlayerBirthGenerateStatsTest.WarriorFullRun} uses,
 * so a 20-point reset from base 10 lands on the same known figures that suite already proved:
 * {@code STR 17, DEX 16, CON 16}, {@code WIS}/{@code INT} untouched, every point spent.
 *
 * <p>Class PlayerBirthDoCmdChooseRaceTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdChooseRaceTest {

    /**
     * A placeholder starting gold; no test here reads it back.
     */
    private static final int START_GOLD = 600;

    /**
     * {@code birth_stat_costs[11]} - the flat cost of every point-buy point from base 10 up to 16,
     * which is every point {@code generateStats} spends in this fixture.
     */
    private static final int COST_FLAT = 1;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}), what {@code resetStats} always
     * resets the running total to before {@code generateStats} spends any of it.
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * The race at registry index 0, distinct from {@link #CHOSEN_RACE_NAME} so a test can tell
     * whether the choice was actually applied.
     */
    private static final String OTHER_RACE_NAME = "Dwarf";

    /**
     * The race at registry index 1 - the one every "ordinary path" test chooses.
     */
    private static final String CHOSEN_RACE_NAME = "Half-Troll";

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    private Player realPlayer;
    private EventsHandler realBus;
    private boolean realCharacterGenerated;
    private Object savedConstants;
    private List<PlayerRace> savedRaces;

    private Map<Stats, Integer> savedRegistryStats;
    private Map<Stats, Integer> savedRegistryPointsSpent;
    private Map<Stats, Integer> savedRegistryPointsInc;
    private int savedRegistryPointsLeft;
    private boolean savedRolledStats;

    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static void seedConstants() throws Exception {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, START_GOLD, 5000),
                null, null, null, null, null, null, null, null));
    }

    private static Field registryField(String name) throws ReflectiveOperationException {
        Field field = PlayerRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static void writeInstance(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Builds a race whose chart has exactly one entry, so the background it yields is fixed rather
     * than randomly chosen among several. None of the age/height/weight ranges can reach zero.
     *
     * @param name the race's name
     * @return the race
     */
    private static PlayerRace race(String name) {
        List<EquipSlot> slots = new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        PlayerHistoryChart chart = new PlayerHistoryChart(1, 0);
        chart.addEntry(new PlayerHistoryEntry(100, "A fixed phrase.  "));
        return new PlayerRace(name, 0, 10, 100, 14, 6, 72, 6, 180, 25, 0,
                new PlayerBody("Humanoid", slots), stats, skills,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), chart, new HashMap<>());
    }

    /**
     * A class with no skills, no stat adjustments and no flags of its own - the same profile
     * {@code PlayerBirthGenerateStatsTest.WarriorFullRun} uses: {@code maxAttacks} above 5 selects
     * C's warrior branch, and {@code minWeight}/{@code attMultiplier} are irrelevant here since
     * nothing in this suite reads {@code calcBlows}'s breakpoint tracking.
     *
     * @return the class
     */
    private static PlayerClass warriorClass() {
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
        return new PlayerClass("Test Warrior", List.of(), stats, skills, new HashMap<>(skills), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 100, 1, List.of(), ClassMagic.NONE);
    }

    /**
     * Builds the {@code CMD_CHOOSE_RACE} command C's {@code cmd_get_arg_choice} would read from -
     * the port of {@code cmd_set_arg_choice(cmdq_peek(), "choice", choice)}, which every real
     * producer of this command calls before it is ever executed.
     *
     * @param choice the race index to store, or {@code null} to leave the arg unset
     * @return the command
     */
    private static Command chooseRaceCommand(Integer choice) {
        Command cmd = new Command(CommandContext.CTX_BIRTH, CommandCode.CMD_CHOOSE_RACE, 0, 0,
                new ArrayList<>());
        if (choice != null) cmd.setArgChoice("choice", choice);
        return cmd;
    }

    @BeforeEach
    void seedFixture() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        savedRaces = (List<PlayerRace>) registryField("playerRaces").get(null);
        PlayerRegistry.setPlayerRaces(List.of(race(OTHER_RACE_NAME), race(CHOSEN_RACE_NAME)));

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);
        writeInstance(player, "playerClass", warriorClass());
        writeInstance(player, "race", race(OTHER_RACE_NAME));

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(new NoOpBus());

        realCharacterGenerated = GameState.getCharacterGenerated();
        Field characterGenerated = GameState.class.getDeclaredField("characterGenerated");
        characterGenerated.setAccessible(true);
        characterGenerated.set(null, false);

        savedRegistryStats = PlayerBirthStateRegistry.getStats();
        savedRegistryPointsSpent = PlayerBirthStateRegistry.getPointsSpent();
        savedRegistryPointsInc = PlayerBirthStateRegistry.getPointsInc();
        savedRegistryPointsLeft = PlayerBirthStateRegistry.getPointsLeft();
        savedRolledStats = PlayerBirthStateRegistry.isRolledStats();

        // Leftover point-buy state from a hypothetical earlier session, so a reset is visible.
        Map<Stats, Integer> leftoverStats = new HashMap<>();
        Map<Stats, Integer> leftoverSpent = new HashMap<>();
        Map<Stats, Integer> leftoverInc = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            leftoverStats.put(stat, 17);
            leftoverSpent.put(stat, 8);
            leftoverInc.put(stat, 42);
        }
        PlayerBirthStateRegistry.setStats(leftoverStats);
        PlayerBirthStateRegistry.setPointsSpent(leftoverSpent);
        PlayerBirthStateRegistry.setPointsInc(leftoverInc);
        PlayerBirthStateRegistry.setPointsLeft(3);
        PlayerBirthStateRegistry.setRolledStats(true);
    }

    @AfterEach
    void restoreFixture() throws Exception {
        GameState.setPlayer(realPlayer);
        GameEngine.setEventsBusHandler(realBus);
        constantsField().set(null, savedConstants);
        registryField("playerRaces").set(null, savedRaces);
        Field characterGenerated = GameState.class.getDeclaredField("characterGenerated");
        characterGenerated.setAccessible(true);
        characterGenerated.set(null, realCharacterGenerated);

        PlayerBirthStateRegistry.setStats(savedRegistryStats);
        PlayerBirthStateRegistry.setPointsSpent(savedRegistryPointsSpent);
        PlayerBirthStateRegistry.setPointsInc(savedRegistryPointsInc);
        PlayerBirthStateRegistry.setPointsLeft(savedRegistryPointsLeft);
        PlayerBirthStateRegistry.setRolledStats(savedRolledStats);
    }

    /**
     * The ordinary path: {@code player_generate} takes on the chosen race, then {@code reset_stats}
     * and {@code generate_stats} run in sequence against the registry's own maps. With base-10 stats,
     * 20 flat-cost points and a non-caster, non-warrior-favoured class, this is exactly
     * {@code PlayerBirthGenerateStatsTest.WarriorFullRun}'s fixture, so the same figures apply: STR
     * to 17, DEX to 16 (bought to 17, sold back to 10, then rebought to 16), CON to 16, WIS/INT
     * untouched, every point spent.
     */
    @Test
    @DisplayName("applies the chosen race, then resets and auto-generates the point-buy stats")
    void ordinaryChoiceAppliesRaceAndRunsThePointBuyPipeline() {
        PlayerBirth.doCmdChooseRace(chooseRaceCommand(1));

        assertEquals(CHOSEN_RACE_NAME, player.getRace().getName());
        assertEquals(17, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR));
        assertEquals(16, PlayerBirthStateRegistry.getStats().get(Stats.STAT_DEX));
        assertEquals(16, PlayerBirthStateRegistry.getStats().get(Stats.STAT_CON));
        assertEquals(10, PlayerBirthStateRegistry.getStats().get(Stats.STAT_WIS));
        assertEquals(10, PlayerBirthStateRegistry.getStats().get(Stats.STAT_INT));
    }

    /**
     * The stat-reset fix's own test: C writes {@code points_left} through
     * {@code generate_stats}'s own {@code int *} out-parameter, updating the shared global for free
     * once more after {@code reset_stats} already did. The port's {@code generateStats} only returns
     * the new total, so {@code doCmdChooseRace} must write it back a second time. The fixture spends
     * every point of the 20-point reset, so a version that dropped this second write would leave
     * {@link PlayerBirthStateRegistry#getPointsLeft()} at {@link #MAX_BIRTH_POINTS} instead of 0.
     */
    @Test
    @DisplayName("wires generateStats's own return back to the registry's pointsLeft")
    void wiresGenerateStatsPointsLeftBackToTheRegistry() {
        PlayerBirth.doCmdChooseRace(chooseRaceCommand(1));

        assertEquals(0, PlayerBirthStateRegistry.getPointsLeft(),
                "every point of the 20-point reset should end up spent by generateStats");
    }

    /**
     * C's trailing {@code rolled_stats = false}, unconditional on the ordinary path.
     */
    @Test
    @DisplayName("clears rolledStats")
    void clearsRolledStats() {
        PlayerBirth.doCmdChooseRace(chooseRaceCommand(1));

        assertFalse(PlayerBirthStateRegistry.isRolledStats());
    }

    /**
     * The port's deliberate divergence from C: with the {@code "choice"} arg unset,
     * {@code doCmdChooseRace} returns before touching the player, the registry's point-buy maps, or
     * {@code rolledStats} - unlike C, which would fall through and call {@code player_id2race} on
     * whatever garbage {@code choice} held. Every producer of {@code CMD_CHOOSE_RACE} in the C tree
     * sets the arg before the command runs, so this path is otherwise unreachable in practice; this
     * test exists to pin down what the port does about it on paper.
     */
    @Test
    @DisplayName("with the choice arg unset, does nothing rather than following C's fallthrough")
    void argMissingDoesNothing() {
        PlayerBirth.doCmdChooseRace(chooseRaceCommand(null));

        assertEquals(OTHER_RACE_NAME, player.getRace().getName(), "race must be untouched");
        assertEquals(17, PlayerBirthStateRegistry.getStats().get(Stats.STAT_STR),
                "the leftover stat map must be untouched");
        assertEquals(3, PlayerBirthStateRegistry.getPointsLeft(), "pointsLeft must be untouched");
        assertTrue(PlayerBirthStateRegistry.isRolledStats(), "rolledStats must be untouched");
    }

    /**
     * An {@link EventsHandler} that does nothing - {@code recalculateStats} (reached through
     * {@code generateStats}) needs one installed, but this suite is about the wiring between the
     * point-buy helpers, not the events they fire.
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
}
