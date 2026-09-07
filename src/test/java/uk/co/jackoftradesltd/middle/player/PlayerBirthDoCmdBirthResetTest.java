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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
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
 * Tests {@link PlayerBirth#doCmdBirthReset}, the port of C's {@code do_cmd_birth_reset}
 * ({@code player-birth.c:1101-1106}) - the {@code CMD_BIRTH_RESET} handler that puts the character
 * and its point-buy state back to how they'd be on entering the birth screen fresh.
 *
 * <p>Every expected value below is read off {@code do_cmd_birth_reset} itself, not off the port:
 *
 * <pre>{@code
 * void do_cmd_birth_reset(struct command *cmd)
 * {
 *         player_init(player);
 *         reset_stats(stats, points_spent, points_inc, &points_left, false);
 *         do_birth_reset(quickstart_allowed, &quickstart_prev);
 *         rolled_stats = false;
 * }
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#playerInit}, {@link
 * PlayerBirth#resetStats} and the private {@code doBirthReset} each have their own suite; what is
 * left here is the wiring between them, in C's own order, and one thing the wiring gets wrong
 * without it: C threads {@code points_left} through {@code reset_stats} by pointer, so the reset
 * total lands in the shared global for free. {@link PlayerBirth#resetStats} has no such pointer - it
 * returns the new total instead - so {@code doCmdBirthReset} must capture that return and write it
 * back to {@link PlayerBirthStateRegistry#setPointsLeft} itself. {@link #resetsPointsLeftRegardlessOfWhatWasThere}
 * exists to pin that down; an earlier draft discarded the return value entirely, leaving {@link
 * PlayerBirthStateRegistry#getPointsLeft()} stale after a reset.
 *
 * <p><b>The quickstart test also pins down C's call order.</b> {@code player_init} defaults the
 * character to the registry's first race before {@code do_birth_reset} runs; when quickstart data is
 * in play, {@code do_birth_reset} then overwrites that default with the saved snapshot. If the two
 * calls ran in the wrong order, {@code player_init}'s own defaulting would run last and clobber the
 * quickstart race right back to the registry's first entry - so {@link
 * #loadsQuickstartDataAheadOfTheRegistrysDefaultRace} verifying the quickstart race survives is
 * itself an order check, not just a wiring check.
 *
 * <p>The fixture is the union of what {@code playerInit} and {@code doBirthReset} each need on their
 * own terms ({@link PlayerBirthPlayerInitTest}, {@link PlayerBirthDoBirthResetTest}): every registry
 * {@code playerInit} reads, plus a race with a fixed-phrase history chart so {@code doBirthReset}'s
 * reroll is deterministic. Everything seeded is saved beforehand and restored afterwards, so this
 * suite runs the same whether it is the only class executed or one of hundreds.
 *
 * <p>Class PlayerBirthDoCmdBirthResetTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdBirthResetTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}).
     */
    private static final int START_GOLD = 600;

    /**
     * C's {@code MAX_BIRTH_POINTS} ({@code player-birth.c:683}), {@code 3 * (1+1+1+1+1+1+2)}.
     */
    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * The cost {@code birth_stat_costs} charges to raise a stat from 10 - {@code birth_stat_costs[11]}
     * ({@code player-birth.c:679}) - which every real stat's increment cost must equal once reset.
     */
    private static final int COST_AT_TEN = 1;

    /**
     * The fixed phrase the registry's default race's own chart yields, distinct from anything a
     * quickstart snapshot carries.
     */
    private static final String FRESH_HISTORY = "You are a test subject, born under a fixed star.  ";

    /**
     * The registry's sole race, the one {@code playerInit} defaults every fresh character to.
     */
    private static final String DEFAULT_RACE_NAME = "Human";

    /**
     * The character's initial race before a reset, distinct from {@link #DEFAULT_RACE_NAME} so the
     * defaulting is visible.
     */
    private static final String INITIAL_RACE_NAME = "Elf";

    /**
     * The character under test, installed as {@link GameState}'s current player.
     */
    private Player player;

    private Player realPlayer;
    private Object savedConstants;
    private List<PlayerBody> savedBodies;
    private List<PlayerRace> savedRaces;
    private List<PlayerClass> savedClasses;
    private List<PlayerShape> savedShapes;
    private List<Artifact> savedArtifacts;
    private List<ObjectKind> savedObjectKinds;
    private List<Curse> savedCurses;
    private List<MonsterRace> savedMonsterRaces;
    private List<Quest> savedQuests;
    private boolean realCharacterGenerated;
    private int savedTurn;

    private Map<Stats, Integer> savedRegistryStats;
    private Map<Stats, Integer> savedRegistryPointsSpent;
    private Map<Stats, Integer> savedRegistryPointsInc;
    private int savedRegistryPointsLeft;
    private boolean savedQuickstartAllowed;
    private Birther savedQuickstartPrev;
    private boolean savedRolledStats;

    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static void setCharacterGenerated(boolean value) throws Exception {
        Field field = GameState.class.getDeclaredField("characterGenerated");
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Reads a private static field's raw value, bypassing every registry getter's own
     * {@code Collections.unmodifiableList} - which throws on a still-{@code null} field rather than
     * answering it, exactly the state a not-yet-loaded registry is in when this suite runs alone.
     *
     * @param owner the class the field belongs to
     * @param name  the field's name
     * @return the field's current value, {@code null} included
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Object readRawStatic(Class<?> owner, String name) throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }

    /**
     * Writes a private static field's raw value directly, bypassing setters such as
     * {@link PlayerRegistry#setPlayerShape} that unconditionally read the list's {@code size()} -
     * which would throw restoring a field that was legitimately {@code null} before this suite ran.
     *
     * @param owner the class the field belongs to
     * @param name  the field's name
     * @param value the value to restore, {@code null} included
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static void writeRawStatic(Class<?> owner, String name, Object value)
            throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static void seedConstants() throws Exception {
        constantsField().set(null, new GameConstantsData(
                null, null, null, null,
                new WorldData(128, 10000, 66, 198, 22, 66, 100, 10, 1, 100),
                new CarryCapData(23, 10, 40, 5, 16),
                null, null,
                new PlayerData(20, 20, START_GOLD, 5000),
                null, null, null, null, null, null, null, null));
    }

    /**
     * Builds a race whose chart has exactly one entry, so the background it yields is fixed rather
     * than randomly chosen among several.
     *
     * @param name    the race's name
     * @param hitDie  the race's {@code r_mhp} contribution
     * @param expFact the race's {@code r_exp} contribution
     * @param phrase  the single background phrase the chart yields
     * @return the race
     */
    private static PlayerRace race(String name, int hitDie, int expFact, String phrase) {
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
        chart.addEntry(new PlayerHistoryEntry(100, phrase));
        return new PlayerRace(name, 0, hitDie, expFact, 14, 6, 72, 6, 180, 25, 0,
                new PlayerBody("Humanoid", slots), stats, skills,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), chart, new HashMap<>());
    }

    /**
     * Builds a class contributing nothing but its hit die and experience factor.
     *
     * @param hitDie  the class's {@code c_mhp} contribution
     * @param expFact the class's {@code c_exp} contribution
     * @return the class
     */
    private static PlayerClass testClass(int hitDie, int expFact) {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }
        return new PlayerClass("Test Class", List.of(), stats, skills, extra, hitDie, expFact,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), ClassMagic.NONE);
    }

    private static PlayerShape normalShape() {
        return new PlayerShape("normal", 0, 0, 0, new HashMap<>(), new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), new HashMap<>(), new HashMap<>(), new ArrayList<>(),
                0, new ArrayList<>());
    }

    /**
     * Builds a quickstart snapshot with one distinct value per field, so a test can tell whether a
     * read came from it rather than from the character's own prior state.
     *
     * @param race        the snapshot's race
     * @param playerClass the snapshot's class
     * @param history     the snapshot's background text
     * @return the snapshot
     */
    private static Birther quickstartBirther(PlayerRace race, PlayerClass playerClass, String history) {
        Birther saved = new Birther();
        saved.setRace(race);
        saved.setPlayerClass(playerClass);
        saved.setAge(50);
        saved.setWeight(160);
        saved.setHeight(68);
        saved.setAu(1_234L);
        saved.setStat(Stats.STAT_STR, 17);
        saved.setStat(Stats.STAT_INT, 11);
        saved.setStat(Stats.STAT_WIS, 9);
        saved.setStat(Stats.STAT_DEX, 13);
        saved.setStat(Stats.STAT_CON, 16);
        saved.setHistoryBirth(history);
        saved.setName("Quickstart");
        return saved;
    }

    /**
     * Asserts every one of the five real stats in a map equals an expected value; the sentinels are
     * never checked here, since {@code resetStats}'s loop never writes them.
     *
     * @param map      the map to check
     * @param expected the value every real stat must carry
     * @param label    what the map represents, for the failure message
     */
    private static void assertAllRealStats(Map<Stats, Integer> map, int expected, String label) {
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            assertEquals(expected, map.get(stat), label + " for " + stat);
        }
    }

    @BeforeEach
    void seedFixture() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        savedBodies = (List<PlayerBody>) readRawStatic(PlayerRegistry.class, "playerBodies");
        savedRaces = (List<PlayerRace>) readRawStatic(PlayerRegistry.class, "playerRaces");
        savedClasses = (List<PlayerClass>) readRawStatic(PlayerRegistry.class, "playerClasses");
        savedShapes = (List<PlayerShape>) readRawStatic(PlayerRegistry.class, "playerShapes");
        PlayerBody body = SeededPlayerRegistry.humanoidBody();
        PlayerRegistry.setPlayerBodies(List.of(body));
        PlayerRegistry.setPlayerRaces(List.of(race(DEFAULT_RACE_NAME, 10, 100, FRESH_HISTORY)));
        PlayerRegistry.setPlayerClasses(List.of(testClass(9, 0)));
        PlayerRegistry.setPlayerShape(List.of(normalShape()));

        savedArtifacts = (List<Artifact>) readRawStatic(ObjectRegistry.class, "artifacts");
        savedObjectKinds = (List<ObjectKind>) readRawStatic(ObjectRegistry.class, "objectKinds");
        savedCurses = (List<Curse>) readRawStatic(ObjectRegistry.class, "curses");
        ObjectRegistry.setArtifacts(List.of());
        ObjectRegistry.setObjectKinds(List.of());
        ObjectRegistry.setCurses(new ArrayList<>());

        savedMonsterRaces = MonsterRegistry.monsterRaces;
        MonsterRegistry.setMonsterRaces(List.of());

        savedQuests = (List<Quest>) readRawStatic(WorldRegistry.class, "quests");
        WorldRegistry.setQuests(List.of());

        player = new Player();
        player.setRace(race(INITIAL_RACE_NAME, 20, 150, "unused phrase"));
        player.setClass(testClass(1, 1));

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        realCharacterGenerated = GameState.getCharacterGenerated();
        setCharacterGenerated(false);

        savedTurn = GameState.getTurn();

        savedRegistryStats = PlayerBirthStateRegistry.getStats();
        savedRegistryPointsSpent = PlayerBirthStateRegistry.getPointsSpent();
        savedRegistryPointsInc = PlayerBirthStateRegistry.getPointsInc();
        savedRegistryPointsLeft = PlayerBirthStateRegistry.getPointsLeft();
        savedQuickstartAllowed = PlayerBirthStateRegistry.isQuickstartAllowed();
        savedQuickstartPrev = PlayerBirthStateRegistry.getQuickstartPrev();
        savedRolledStats = PlayerBirthStateRegistry.isRolledStats();

        // Leftover point-buy state from a hypothetical earlier session, so the reset is visible.
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
        PlayerBirthStateRegistry.setQuickstartAllowed(false);
        PlayerBirthStateRegistry.setQuickstartPrev(null);
    }

    @AfterEach
    void restoreFixture() throws Exception {
        GameState.setPlayer(realPlayer);
        constantsField().set(null, savedConstants);
        writeRawStatic(PlayerRegistry.class, "playerBodies", savedBodies);
        writeRawStatic(PlayerRegistry.class, "playerRaces", savedRaces);
        writeRawStatic(PlayerRegistry.class, "playerClasses", savedClasses);
        writeRawStatic(PlayerRegistry.class, "playerShapes", savedShapes);
        writeRawStatic(ObjectRegistry.class, "artifacts", savedArtifacts);
        writeRawStatic(ObjectRegistry.class, "objectKinds", savedObjectKinds);
        writeRawStatic(ObjectRegistry.class, "curses", savedCurses);
        MonsterRegistry.monsterRaces = savedMonsterRaces;
        writeRawStatic(WorldRegistry.class, "quests", savedQuests);
        setCharacterGenerated(realCharacterGenerated);
        GameState.setTurn(savedTurn);

        PlayerBirthStateRegistry.setStats(savedRegistryStats);
        PlayerBirthStateRegistry.setPointsSpent(savedRegistryPointsSpent);
        PlayerBirthStateRegistry.setPointsInc(savedRegistryPointsInc);
        PlayerBirthStateRegistry.setPointsLeft(savedRegistryPointsLeft);
        PlayerBirthStateRegistry.setQuickstartAllowed(savedQuickstartAllowed);
        PlayerBirthStateRegistry.setQuickstartPrev(savedQuickstartPrev);
        PlayerBirthStateRegistry.setRolledStats(savedRolledStats);
    }

    /**
     * The stat-reset fix's own test: C writes {@code MAX_BIRTH_POINTS} through
     * {@code *points_left_local}, updating the shared global for free; the port's {@code resetStats}
     * only returns the new total, so {@code doCmdBirthReset} must write it back itself. The fixture
     * seeds {@link PlayerBirthStateRegistry#getPointsLeft()} at 3 - a plausible mid-spend leftover -
     * so a version that dropped the return value would leave it there instead of {@link
     * #MAX_BIRTH_POINTS}.
     */
    @Test
    @DisplayName("resets pointsLeft to MAX_BIRTH_POINTS, not left at the pre-call value")
    void resetsPointsLeftRegardlessOfWhatWasThere() {
        PlayerBirth.doCmdBirthReset(null);

        assertEquals(MAX_BIRTH_POINTS, PlayerBirthStateRegistry.getPointsLeft());
    }

    /**
     * {@code reset_stats}'s own stat loop, reached through the registry's live maps rather than
     * throwaway ones - proof that {@code doCmdBirthReset} hands {@code resetStats} the registry's own
     * {@link PlayerBirthStateRegistry#getStats()}/{@link PlayerBirthStateRegistry#getPointsSpent()}/
     * {@link PlayerBirthStateRegistry#getPointsInc()}, not copies.
     */
    @Test
    @DisplayName("overwrites every real stat in the registry to 10/0/1")
    void resetsRegistryStatMaps() {
        PlayerBirth.doCmdBirthReset(null);

        assertAllRealStats(PlayerBirthStateRegistry.getStats(), 10, "reset stat value");
        assertAllRealStats(PlayerBirthStateRegistry.getPointsSpent(), 0, "reset points spent");
        assertAllRealStats(PlayerBirthStateRegistry.getPointsInc(), COST_AT_TEN, "reset increment cost");
    }

    /**
     * C's trailing {@code rolled_stats = false}, unconditional.
     */
    @Test
    @DisplayName("clears rolledStats")
    void clearsRolledStats() {
        PlayerBirth.doCmdBirthReset(null);

        assertFalse(PlayerBirthStateRegistry.isRolledStats());
    }

    /**
     * With no quickstart data in play, {@code player_init} defaults the character to the registry's
     * sole race, and {@code do_birth_reset}'s trailing {@code player_generate(player, NULL, NULL,
     * false)} then rerolls the history from that race's own chart and fully heals the character -
     * both only possible once {@code player_init} has already run.
     */
    @Test
    @DisplayName("with no quickstart data, defaults to the registry's race and regenerates it")
    void ordinaryResetDefaultsToRegistryRace() {
        PlayerBirth.doCmdBirthReset(null);

        assertEquals(DEFAULT_RACE_NAME, player.getRace().getName(),
                "player_init did not default the race to the registry's first entry");
        assertEquals(FRESH_HISTORY, player.getHistoryBirth(),
                "do_birth_reset did not reroll history from the newly-defaulted race's chart");
        assertTrue(player.getMaxHP() > 0, "a generated character has some maximum");
        assertEquals(player.getMaxHP(), player.getCurrentHP(), "do_birth_reset must fully heal");
    }

    /**
     * With quickstart data in play, {@code do_birth_reset}'s {@code load_roller_data} overwrites
     * {@code player_init}'s registry default with the saved snapshot - which only survives if
     * {@code player_init} ran <em>first</em>. A version that called the two in the wrong order would
     * see {@code player_init}'s defaulting run last and clobber the quickstart race right back to
     * {@link #DEFAULT_RACE_NAME}.
     */
    @Test
    @DisplayName("with quickstart data, loads the saved snapshot ahead of the registry's default race")
    void loadsQuickstartDataAheadOfTheRegistrysDefaultRace() {
        PlayerRace quickstartRace = race("Quickstart Race", 20, 150, "the quickstart race's own phrase");
        PlayerClass quickstartClass = testClass(5, 60);
        Birther prev = quickstartBirther(quickstartRace, quickstartClass, "A restored adventurer.");
        PlayerBirthStateRegistry.setQuickstartAllowed(true);
        PlayerBirthStateRegistry.setQuickstartPrev(prev);

        PlayerBirth.doCmdBirthReset(null);

        assertEquals("Quickstart Race", player.getRace().getName(),
                "quickstart race must survive player_init's own defaulting");
        assertEquals(20 + 5, player.getHitDie());
        assertEquals(17, player.getStatMax(Stats.STAT_STR));
        assertEquals("A restored adventurer.", player.getHistoryBirth(),
                "old_history keeps the loaded background rather than rerolling it");
    }
}
