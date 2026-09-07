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
import uk.co.jackoftradesltd.channel.messages.data.EventDataBoolean;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.MessageType;
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
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#doCmdBirthInit}, the port of C's {@code do_cmd_birth_init}
 * ({@code player-birth.c:1061-1096}) - the {@code CMD_BIRTH_INIT} handler that starts the birth
 * screen, either restoring a quickstart character or generating a fresh one from the first race
 * and class.
 *
 * <p>Every expected value below is read off {@code do_cmd_birth_init} itself, not off the port:
 *
 * <pre>{@code
 * character_dungeon = false;
 * if (player->ht_birth) {
 *         buf = find_roman_suffix_start(player->full_name);
 *         if (buf) {
 *                 int success = int_to_roman(roman_to_int(buf) + 1, buf, ...);
 *                 if (!success) msg("Sorry, could not deal with suffix");
 *         }
 *         save_roller_data(&quickstart_prev);
 *         quickstart_allowed = true;
 * } else {
 *         player_generate(player, player_id2race(0), player_id2class(0), false);
 *         quickstart_allowed = false;
 * }
 * event_signal_flag(EVENT_ENTER_BIRTH, quickstart_allowed);
 * }</pre>
 *
 * <p><b>What is actually under test.</b> {@link PlayerName#findRomanSuffixStart}, {@link
 * PlayerBirth#romanToInt}, {@link PlayerBirth#intToRoman}, {@link PlayerBirth#saveRollerData} and
 * {@link PlayerBirth#playerGenerate} each have their own suite; what is left here is the wiring
 * between them - which branch runs when, whether the incremented numeral is actually written back
 * onto the player, and which flag reaches the trailing event signal.
 *
 * <p><b>The rename's success/failure split was the bug this suite exists to pin down.</b> C shows
 * its message only when {@code int_to_roman} <em>fails</em> (an empty result); an earlier draft of
 * the port had that test inverted, so an ordinary reused name like {@code "Bob IV"} raised the
 * "Sorry" message and was never renamed, while the one genuinely unrepresentable case - the empty
 * suffix {@link PlayerName#findRomanSuffixStart} returns for a name ending in a trailing space -
 * went unreported.
 *
 * <p>The bus is replaced by a capture so both the "Sorry" message and the trailing
 * {@code EVENT_ENTER_BIRTH} signal can be asserted without a display attached, and {@link
 * PlayerRegistry}'s race and class lists are swapped for single-entry fixtures so the no-quickstart
 * branch's {@code player_id2race(0)}/{@code player_id2class(0)} lookups are deterministic.
 *
 * <p>Class PlayerBirthDoCmdBirthInitTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthDoCmdBirthInitTest {

    /**
     * The shipped {@code player:start-gold} figure ({@code constants.txt:201}), needed so {@link
     * PlayerBirth#playerGenerate}'s trailing {@code getPyFoodFull()} read has a table to come from.
     */
    private static final int START_GOLD = 600;

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
     * Whatever {@link PlayerRegistry} held as its race list before the test, restored afterwards.
     */
    private List<PlayerRace> previousRaces;

    /**
     * Whatever {@link PlayerRegistry} held as its class list before the test, restored afterwards.
     */
    private List<PlayerClass> previousClasses;

    /**
     * The real event bus, swapped out for {@link #bus} and restored afterwards.
     */
    private EventsHandler realBus;

    /**
     * The capture standing in for the display, so a message and the trailing flag signal can both
     * be asserted.
     */
    private CapturingBus bus;

    /**
     * Whatever {@code quickstartPrev} held before the test, restored afterwards.
     */
    private Birther previousQuickstartPrev;

    /**
     * Whatever {@code quickstartAllowed} held before the test, restored afterwards.
     */
    private boolean previousQuickstartAllowed;

    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static Field playerRacesField() throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField("playerRaces");
        field.setAccessible(true);
        return field;
    }

    private static Field playerClassesField() throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField("playerClasses");
        field.setAccessible(true);
        return field;
    }

    private static Field quickstartPrevField() throws Exception {
        Field field = PlayerBirth.class.getDeclaredField("quickstartPrev");
        field.setAccessible(true);
        return field;
    }

    private static Field quickstartAllowedField() throws Exception {
        Field field = PlayerBirth.class.getDeclaredField("quickstartAllowed");
        field.setAccessible(true);
        return field;
    }

    /**
     * Seeds the constants table with the figures {@link PlayerBirth#playerGenerate} needs when it
     * rolls age/weight/height and looks up the well-fed food level, mirroring
     * {@code PlayerBirthDoBirthResetTest}'s fixture.
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
     * Builds a race whose chart has exactly one entry, so {@code playerGenerate}'s history reroll
     * is fixed rather than randomly chosen among several.
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
        chart.addEntry(new PlayerHistoryEntry(100, "A fixed background."));
        return new PlayerRace(name, 0, 10, 100, 14, 6, 72, 6, 180, 25, 0,
                new PlayerBody("Humanoid", slots), stats, skills,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), chart, new HashMap<>());
    }

    /**
     * Builds a class contributing nothing but its own name.
     *
     * @param name the class's name
     * @return the class
     */
    private static PlayerClass testClass(String name) {
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
        return new PlayerClass(name, List.of(), stats, skills, extra, 9, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), ClassMagic.NONE);
    }

    /**
     * Sets every one of the five real stats' birth copy, so {@link PlayerBirth#saveRollerData}'s
     * stat loop - run unconditionally on the quickstart branch - never reads an unseeded map entry.
     */
    private void seedBirthStats() {
        player.setStatBirth(Stats.STAT_STR, 18);
        player.setStatBirth(Stats.STAT_INT, 10);
        player.setStatBirth(Stats.STAT_WIS, 9);
        player.setStatBirth(Stats.STAT_DEX, 14);
        player.setStatBirth(Stats.STAT_CON, 17);
    }

    @BeforeEach
    void seedFixture() throws Exception {
        savedConstants = constantsField().get(null);
        seedConstants();

        previousRaces = (List<PlayerRace>) playerRacesField().get(null);
        previousClasses = (List<PlayerClass>) playerClassesField().get(null);
        PlayerRegistry.setPlayerRaces(List.of(race("Human")));
        playerClassesField().set(null, List.of(testClass("Warrior")));

        player = new Player();
        player.setRace(race("Elf"));
        player.setClass(testClass("Mage"));
        seedBirthStats();
        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        previousQuickstartPrev = (Birther) quickstartPrevField().get(null);
        previousQuickstartAllowed = (boolean) quickstartAllowedField().get(null);
        quickstartPrevField().set(null, new Birther());
        quickstartAllowedField().set(null, false);

        GameWorld.setCharacterDungeon(true);
    }

    @AfterEach
    void restoreFixture() throws Exception {
        GameState.setPlayer(realPlayer);
        constantsField().set(null, savedConstants);
        playerRacesField().set(null, previousRaces);
        playerClassesField().set(null, previousClasses);
        GameEngine.setEventsBusHandler(realBus);
        quickstartPrevField().set(null, previousQuickstartPrev);
        quickstartAllowedField().set(null, previousQuickstartAllowed);
    }

    /**
     * {@code character_dungeon = false} runs unconditionally, before either branch - checked here
     * with the flag deliberately seeded {@code true} beforehand so the reset is visible.
     */
    @Test
    @DisplayName("marks the dungeon not ready regardless of which branch runs")
    void marksDungeonNotReady() {
        player.setHeightBirth(0);

        PlayerBirth.doCmdBirthInit(null);

        assertFalse(GameWorld.hasCharacterDungeon());
    }

    /**
     * An {@link EventsHandler} that keeps every dispatch instead of delivering it, so both the
     * "Sorry" message and the trailing flag signal can be inspected. The three registration methods
     * are unused here and left empty.
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventType> types = new ArrayList<>();
        private final List<GameEventData> payloads = new ArrayList<>();

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
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            types.add(eventType);
            payloads.add(data);
        }

        private boolean hasDispatch(GameEventType type) {
            return types.contains(type);
        }

        private GameEventType lastType() {
            return types.get(types.size() - 1);
        }

        private <T extends GameEventData> T lastOfType(Class<T> dataType) {
            for (int i = payloads.size() - 1; i >= 0; i--) {
                if (dataType.isInstance(payloads.get(i))) {
                    return assertInstanceOf(dataType, payloads.get(i));
                }
            }
            return null;
        }
    }

    /**
     * The branch guard itself: {@code player->ht_birth} non-zero, matching the port's
     * {@code player.getHeightBirth() != 0}.
     */
    @Nested
    @DisplayName("with quickstart data present")
    class WithQuickstartData {

        /**
         * A name with no space at all - {@code find_roman_suffix_start} returns {@code NULL}, so
         * the whole rename block is skipped, and the character still proceeds to
         * {@code save_roller_data}/{@code quickstart_allowed = true}.
         */
        @Test
        @DisplayName("a name with no suffix is left untouched, and the roller data is still saved")
        void nameWithNoSuffixIsUntouched() throws Exception {
            player.setHeightBirth(70);
            player.setFullName("Bilbo");

            PlayerBirth.doCmdBirthInit(null);

            assertEquals("Bilbo", player.getFullName());
            assertTrue((boolean) quickstartAllowedField().get(null));
            Birther saved = (Birther) quickstartPrevField().get(null);
            assertEquals("Bilbo", saved.getName(), "save_roller_data still ran unconditionally");
            assertFalse(bus.hasDispatch(GameEventType.EVENT_MESSAGE), "no failure, so no message");
        }

        /**
         * The ordinary success path: a valid Roman suffix is incremented and written back onto the
         * player's own name before the roller data is saved - matching C's in-place
         * {@code int_to_roman(..., buf, ...)} write through the same pointer {@code roman_to_int}
         * read from. This is the case an earlier, inverted success/failure test broke: it raised the
         * "Sorry" message here and never renamed at all.
         */
        @Test
        @DisplayName("a valid Roman suffix is incremented and written back onto the player")
        void validSuffixIsIncremented() throws Exception {
            player.setHeightBirth(70);
            player.setFullName("Bob IV");

            PlayerBirth.doCmdBirthInit(null);

            assertEquals("Bob V", player.getFullName());
            assertFalse(bus.hasDispatch(GameEventType.EVENT_MESSAGE), "a successful build shows no message");
            Birther saved = (Birther) quickstartPrevField().get(null);
            assertEquals("Bob V", saved.getName(),
                    "save_roller_data reads full_name after the rename, matching C's call order");
        }

        /**
         * The one failure case reachable through {@link PlayerName#findRomanSuffixStart}: a name
         * ending in a trailing space yields an <em>empty</em> (not {@code null}) suffix, so
         * {@code roman_to_int("")} answers -1, {@code int_to_roman(0, ...)} fails on C's own
         * {@code n < 1} guard, and C's {@code msg("Sorry, could not deal with suffix")} fires. The
         * name is left exactly as it was, matching C leaving an already-empty buffer empty.
         */
        @Test
        @DisplayName("a trailing-space empty suffix fails to build and shows the sorry message")
        void emptySuffixFailsAndShowsMessage() throws Exception {
            player.setHeightBirth(70);
            player.setFullName("Bob ");

            PlayerBirth.doCmdBirthInit(null);

            assertEquals("Bob ", player.getFullName(), "a build failure must not touch the name");
            assertTrue(bus.hasDispatch(GameEventType.EVENT_MESSAGE));
            EventDataMessage message = bus.lastOfType(EventDataMessage.class);
            assertEquals(MessageType.MSG_GENERIC, message.type());
            assertEquals("Sorry, couldn't deal with suffix.", message.message());
            assertTrue((boolean) quickstartAllowedField().get(null),
                    "quickstart_allowed is still set true regardless of the rename outcome");
        }

        /**
         * The trailing event signal carries {@code quickstart_allowed}, which this branch always
         * sets {@code true}.
         */
        @Test
        @DisplayName("signals EVENT_ENTER_BIRTH with quickstartAllowed true")
        void signalsEnterBirthTrue() {
            player.setHeightBirth(70);
            player.setFullName("Bilbo");

            PlayerBirth.doCmdBirthInit(null);

            assertEquals(new EventDataBoolean(true), bus.lastOfType(EventDataBoolean.class));
            assertEquals(GameEventType.EVENT_ENTER_BIRTH, bus.lastType());
        }
    }

    /**
     * The other edge of the guard: {@code player->ht_birth} zero, so C falls straight to
     * {@code player_generate(player, player_id2race(0), player_id2class(0), false)} and
     * {@code quickstart_allowed = false}.
     */
    @Nested
    @DisplayName("with no quickstart data")
    class WithNoQuickstartData {

        @Test
        @DisplayName("generates a fresh character from the first race and class by index")
        void generatesFromFirstRaceAndClass() {
            player.setHeightBirth(0);

            PlayerBirth.doCmdBirthInit(null);

            assertEquals("Human", player.getRace().getName(), "player_id2race(0)");
            assertEquals("Warrior", player.getPlayerClass().getName(), "player_id2class(0)");
        }

        @Test
        @DisplayName("clears quickstartAllowed and signals EVENT_ENTER_BIRTH false")
        void clearsQuickstartAllowed() throws Exception {
            player.setHeightBirth(0);

            PlayerBirth.doCmdBirthInit(null);

            assertFalse((boolean) quickstartAllowedField().get(null));
            assertEquals(new EventDataBoolean(false), bus.lastOfType(EventDataBoolean.class));
            assertEquals(GameEventType.EVENT_ENTER_BIRTH, bus.lastType());
        }

        @Test
        @DisplayName("never touches the roller-data snapshot")
        void neverSavesRollerData() throws Exception {
            Birther before = (Birther) quickstartPrevField().get(null);
            player.setHeightBirth(0);

            PlayerBirth.doCmdBirthInit(null);

            assertEquals(before, (Birther) quickstartPrevField().get(null));
        }
    }
}
