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
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the private {@link PlayerBirth#doBirthReset}, the port of C's {@code do_birth_reset}
 * ({@code player-birth.c:1045-1058}) - the four-step reset the birth screen runs to undo every
 * choice made so far: reload quickstart data if there is any to reload, rebuild the derived
 * character fields, drop the character back to town, and recompute everything that depends on the
 * now-current stats.
 *
 * <p>Every expected value below is read off {@code do_birth_reset} itself, not off the port:
 *
 * <pre>{@code
 * if (use_quickstart && quickstart_prev_local)
 *         load_roller_data(quickstart_prev_local, NULL);
 * player_generate(player, NULL, NULL, use_quickstart && quickstart_prev_local);
 * player->depth = 0;
 * get_bonuses();
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerBirth#LoadRollerData}, {@link
 * PlayerBirth#playerGenerate} and {@link PlayerBirth#getBonuses} each have their own suite; what is
 * left here is the wiring between them - the compound {@code useQuickstart && quickstartPrevLevel
 * != null} guard, gating both the load and the {@code oldHistory} flag handed to {@code
 * playerGenerate}, and the fixed trailing order of depth-reset then bonus recalculation.
 *
 * <p><b>The guard needs both edges pressed.</b> {@link
 * QuickstartFlagFalseIgnoresPreviousLevel} presses the case C's parenthesisation actually
 * distinguishes from a plain {@code ||}: a previous level is available, but {@code useQuickstart}
 * is {@code false}, so it must be ignored entirely. {@link QuickstartWithNoPreviousLevel} presses
 * the other edge - {@code useQuickstart} true with nothing to load - which behaves identically to
 * an ordinary reset because a null pointer reads as false in C's own condition.
 *
 * <p>The fixture gives the character a starting depth away from town and a race whose chart yields
 * a fixed, recognisable phrase, so that the depth-reset and the history-reroll are both visible
 * rather than accidentally already true.
 *
 * <p>Class PlayerBirthDoBirthResetTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoBirthResetTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}) - not asserted on
     * directly here (that is {@link PlayerBirthLoadRollerDataTest}'s job), but needed so {@link
     * PlayerBirth#LoadRollerData} can run at all when a previous level is loaded.
     */
    private static final int START_GOLD = 600;

    /**
     * The fixed phrase the character's own race chart yields, distinct from anything a quickstart
     * snapshot carries.
     */
    private static final String FRESH_HISTORY = "You are a test subject, born under a fixed star.  ";

    /**
     * The starting depth the fixture uses, away from town so the reset to depth zero is visible.
     */
    private static final int STARTING_DEPTH = 5;

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

    /**
     * Whether a character was generated before the test, put back afterwards - birth itself always
     * runs with this false, since there is no dungeon yet.
     */
    private boolean realCharacterGenerated;

    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    /**
     * Seeds the constants table with real shipped figures: the carry cap {@link Player}'s
     * constructor needs, the starting gold {@link PlayerBirth#LoadRollerData} resets the purse to,
     * and the day length {@code constants.txt:101} gives, needed because {@code doBirthReset} sets
     * the character's depth to zero before recalculating bonuses, and the town-daytime branch of
     * {@code calcLight} reads it.
     */
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
     * Invokes {@link PlayerBirth#doBirthReset} through reflection, since it is private.
     *
     * @param useQuickstart       the flag argument
     * @param quickstartPrevLevel the previous-level argument
     * @throws Exception if the method cannot be reached or throws
     */
    private void reset(boolean useQuickstart, Birther quickstartPrevLevel) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("doBirthReset", boolean.class, Birther.class);
        method.setAccessible(true);
        method.invoke(null, useQuickstart, quickstartPrevLevel);
    }

    /**
     * Builds a character with a real, computable stat spread and a fixed-phrase race chart, at a
     * non-town depth, installed as {@link GameState}'s current player.
     *
     * @throws Exception if the fixture cannot be built
     */
    @BeforeEach
    void seedAndBuild() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        player = new Player();
        CalcBonusesFixture.plainCharacter(player)
                .race(race("Human", 10, 100, FRESH_HISTORY))
                .playerClass(testClass(9, 0))
                .depth(STARTING_DEPTH);

        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        realCharacterGenerated = GameWorld.characterGenerated;
        GameWorld.characterGenerated = false;
    }

    /**
     * Restores every piece of global state the test touched.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @AfterEach
    void restore() throws Exception {
        GameState.setPlayer(realPlayer);
        constantsField().set(null, savedConstants);
        GameWorld.characterGenerated = realCharacterGenerated;
    }

    /**
     * The plain path: no quickstart data in play at all.
     */
    @Nested
    @DisplayName("an ordinary reset, with no quickstart data")
    class OrdinaryReset {

        /**
         * {@code player->depth = 0} runs unconditionally.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("resets depth to town")
        void resetsDepth() throws Exception {
            reset(false, null);

            assertEquals(0, player.getDepth());
        }

        /**
         * {@code get_bonuses()} runs last and fully heals the character.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("fully heals the character")
        void healsCharacter() throws Exception {
            reset(false, null);

            assertTrue(player.getMaxHP() > 0, "a generated character has some maximum");
            assertEquals(player.getMaxHP(), player.getCurrentHP());
        }

        /**
         * {@code old_history} is {@code use_quickstart && quickstart_prev_local}, false here, so
         * {@code player_generate} rerolls the background from the race's own chart.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("regenerates history from the race's own chart")
        void regeneratesHistory() throws Exception {
            reset(false, null);

            assertEquals(FRESH_HISTORY, player.getHistoryBirth());
        }

        /**
         * {@code player_generate(player, NULL, NULL, ...)} keeps whatever race and class the
         * character already has - as a fresh {@link PlayerRace#copy()}/{@link PlayerClass#copy()}
         * of it, since {@code playerGenerate} copies even a kept definition, so the check is on
         * identifying attributes rather than reference identity.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("keeps the character's existing race and class")
        void keepsRaceAndClass() throws Exception {
            String raceNameBefore = player.getRace().getName();
            int classHitDieBefore = player.getPlayerClass().getMaxHitDie();

            reset(false, null);

            assertEquals(raceNameBefore, player.getRace().getName());
            assertEquals(classHitDieBefore, player.getPlayerClass().getMaxHitDie());
        }
    }

    /**
     * The edge of the compound guard C's parenthesisation actually distinguishes from a plain
     * {@code ||}: quickstart data is available, but the flag saying to use it is not set.
     */
    @Nested
    @DisplayName("useQuickstart false ignores any previous level, even when one is given")
    class QuickstartFlagFalseIgnoresPreviousLevel {

        /**
         * A previous level is passed, but {@code use_quickstart} is false, so {@code
         * load_roller_data} must not run at all - the character's own race and history survive
         * untouched, and the reset behaves exactly like the ordinary path.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("the quickstart snapshot is never loaded")
        void quickstartSnapshotNeverLoaded() throws Exception {
            String raceNameBefore = player.getRace().getName();
            Birther prev = quickstartBirther(race("Elf", 99, 999, "unused phrase"),
                    testClass(99, 999), "Unused quickstart history.");

            reset(false, prev);

            assertEquals(raceNameBefore, player.getRace().getName());
            assertEquals(FRESH_HISTORY, player.getHistoryBirth());
        }
    }

    /**
     * The other edge: {@code use_quickstart} is true, but there is nothing to load, since a null
     * pointer reads as false in C's own condition.
     */
    @Nested
    @DisplayName("useQuickstart true with no previous level behaves like an ordinary reset")
    class QuickstartWithNoPreviousLevel {

        /**
         * With no snapshot to load, the guard is false either way, so this is indistinguishable
         * from {@link OrdinaryReset}.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("keeps the existing race and regenerates history")
        void behavesAsOrdinaryReset() throws Exception {
            String raceNameBefore = player.getRace().getName();

            reset(true, null);

            assertEquals(raceNameBefore, player.getRace().getName());
            assertEquals(FRESH_HISTORY, player.getHistoryBirth());
            assertEquals(0, player.getDepth());
        }
    }

    /**
     * The real quickstart path: both halves of the guard are true.
     */
    @Nested
    @DisplayName("useQuickstart true with a saved previous level")
    class QuickstartWithPreviousLevel {

        /**
         * {@code load_roller_data(quickstart_prev_local, NULL)} runs first, so the snapshot's race,
         * class and stats land on the live player before {@code player_generate} rebuilds the
         * derived fields on top of them - as fresh copies, since {@code playerGenerate} copies
         * whatever race and class it is handed, kept or not, so the check is on identifying
         * attributes rather than reference identity.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("loads the quickstart race, class and stats onto the player")
        void loadsQuickstartData() throws Exception {
            PlayerRace quickstartRace = race("Elf", 20, 150, "the new race's own phrase");
            PlayerClass quickstartClass = testClass(5, 60);
            Birther prev = quickstartBirther(quickstartRace, quickstartClass,
                    "A restored adventurer.");

            reset(true, prev);

            assertEquals("Elf", player.getRace().getName());
            assertEquals(20 + 5, player.getHitDie());
            assertEquals(17, player.getStatMax(Stats.STAT_STR));
            assertEquals(16, player.getStatMax(Stats.STAT_CON));
        }

        /**
         * {@code old_history} is true here, so {@code player_generate} leaves the background {@code
         * load_roller_data} just installed alone, rather than rerolling it from the newly loaded
         * race's own chart.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("keeps the quickstart's history rather than rerolling it")
        void keepsQuickstartHistory() throws Exception {
            PlayerRace quickstartRace = race("Elf", 20, 150, "the new race's own phrase");
            Birther prev = quickstartBirther(quickstartRace, testClass(5, 60),
                    "A restored adventurer.");

            reset(true, prev);

            assertEquals("A restored adventurer.", player.getHistoryBirth());
        }

        /**
         * The trailing two lines run regardless of which branch the guard took.
         *
         * @throws Exception if the reset cannot be invoked
         */
        @Test
        @DisplayName("still resets depth and fully heals")
        void stillResetsDepthAndHeals() throws Exception {
            Birther prev = quickstartBirther(race("Elf", 20, 150, "x"), testClass(5, 60),
                    "A restored adventurer.");

            reset(true, prev);

            assertEquals(0, player.getDepth());
            assertTrue(player.getMaxHP() > 0);
            assertEquals(player.getMaxHP(), player.getCurrentHP());
        }
    }
}
