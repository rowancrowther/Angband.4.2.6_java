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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.GameWorld;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.gameinput.DefaultGameInput;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#updateStuff()}, the port of C's {@code update_stuff}
 * ({@code player-calcs.c:2565}).
 *
 * <p>The method is a dispatcher, so what is worth pinning is not arithmetic but which calculations
 * run, in what order, and which flags are left behind. Every expectation below is read off the C
 * function rather than off the port: the clause order, the fact that each clause clears its flag
 * before calling, the {@code total_spells > 0} guard around {@code calc_spells} that still clears
 * {@code PU_SPELLS} for a warrior, the two early returns for an ungenerated character and a hidden
 * map, and {@code PU_DISTANCE} clearing {@code PU_MONSTERS} as well so the monster pass runs once
 * with {@code full} set rather than twice.
 *
 * <p>The recalculations themselves are covered by their own suites, so the player under test is a
 * {@link RecordingPlayer} that notes each call and does nothing else. That is what makes the
 * ordering observable — with the real calculations running, the order would only be visible in their
 * results, and a wrong order would show up as a wrong number somewhere else entirely.
 *
 * <p>The two early returns read global state ({@link GameWorld#characterGenerated} and the
 * {@code GameInput} boundary), so both are set explicitly here and put back afterwards; a test that
 * left either changed would silently decide the outcome of another class.
 *
 * <p>Class PlayerUpdateStuffTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpdateStuffTest {

    /**
     * The player under test, recording rather than calculating.
     */
    private RecordingPlayer player;

    /**
     * The level the view clause updates.
     */
    private RecordingChunk level;

    /**
     * The bus installed for the test, capturing the panel clause's event.
     */
    private CapturingBus bus;

    /**
     * The bus that was installed before the test, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * Whether a character was generated before the test, put back afterwards.
     */
    private boolean realCharacterGenerated;

    /**
     * The level {@link GameState} held before the test, put back afterwards.
     */
    private Chunk realCave;

    /**
     * A player with a level, a visible map and a generated character - the ordinary mid-game
     * conditions under which every clause is reachable.
     */
    @BeforeEach
    void newPlayer() {
        player = new RecordingPlayer();
        level = new RecordingChunk(player);
        player.setCave(level);

        // The view clause rebuilds on the real level rather than the player's view, so it reads the
        // C `cave` global (GameState) and not the player's own reference — both have to be the
        // recording level or the rebuild goes to a null.
        realCave = GameState.getCave();
        GameState.setCave(level);

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        realCharacterGenerated = GameWorld.characterGenerated;
        GameWorld.characterGenerated = true;
        GameInputHolder.resetInstance();
    }

    /**
     * Puts the globals back, so nothing here decides another class's outcome.
     */
    @AfterEach
    void restoreGlobals() {
        GameEngine.setEventsBusHandler(realBus);
        GameWorld.characterGenerated = realCharacterGenerated;
        GameState.setCave(realCave);
        GameInputHolder.resetInstance();
    }

    /**
     * Gives the player a class knowing the given number of spells.
     *
     * @param spells how many spells its single book holds
     * @throws ReflectiveOperationException if the class field cannot be reached
     */
    private void giveClass(int spells) throws ReflectiveOperationException {
        MagicRealm arcane = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell",
                TValue.TV_MAGIC_BOOK);
        List<MagicBook> books = spells > 0
                ? List.of(new MagicBook(TValue.TV_MAGIC_BOOK, "Magic for Beginners", false,
                spells, arcane, null, 0, 0, 0, 0, List.of()))
                : List.of();

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

        PlayerClass playerClass = new PlayerClass("Test Class", List.of(), stats, skills, extra,
                0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), new ClassMagic(5, 300, books.size(), books));

        Field field = Player.class.getDeclaredField("playerClass");
        field.setAccessible(true);
        field.set(player, playerClass);
    }

    /**
     * Raises the given update flags on the player's upkeep.
     *
     * @param flags the flags to raise
     */
    private void raise(PlayerUpdateEnum... flags) {
        for (PlayerUpdateEnum flag : flags) {
            player.getPlayerUpkeep().setUpdateFlagOn(flag);
        }
    }

    /**
     * A player that records which recalculations were asked for, and runs none of them.
     *
     * @author Rowan Crowther
     */
    private static final class RecordingPlayer extends Player {

        /**
         * The recalculations asked for, in the order they were asked for.
         */
        private final List<String> calls = new ArrayList<>();

        @Override
        public void calcInventory() {
            calls.add("inventory");
        }

        @Override
        public void updateBonuses() {
            calls.add("bonuses");
        }

        @Override
        public void calcLight(PlayerState state, boolean update) {
            calls.add("light");
        }

        @Override
        public void calcHitpoints() {
            calls.add("hitpoints");
        }

        @Override
        public void calcMana(PlayerState state, boolean update) {
            calls.add("mana");
        }

        @Override
        public void calcSpells() {
            calls.add("spells");
        }

        @Override
        public void updateMonsters(boolean full) {
            calls.add(full ? "monsters(full)" : "monsters(partial)");
        }
    }

    /**
     * A level that records the view rebuild rather than performing it.
     *
     * @author Rowan Crowther
     */
    private static final class RecordingChunk extends Chunk {

        /**
         * How many times the view was rebuilt.
         */
        private int viewUpdates;

        /**
         * @param player the player this level belongs to
         */
        private RecordingChunk(Player player) {
            super("test level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        }

        @Override
        public void updateView(Player player) {
            viewUpdates++;
        }
    }

    /**
     * Captures the events signalled during the test.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * Every event type signalled since the bus was installed.
         */
        private final List<GameEventType> events = new ArrayList<>();

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
            events.add(eventType);
        }
    }

    /**
     * A hidden map, as when the character sheet or a menu is covering it.
     *
     * @author Rowan Crowther
     */
    private static final class HiddenMapInput extends DefaultGameInput {

        @Override
        public boolean mapIsVisible() {
            return false;
        }
    }

    /**
     * The dispatch itself: which calculations run, and in which order.
     */
    @Nested
    @DisplayName("dispatch")
    class Dispatch {

        /**
         * With nothing stale the method returns at C's leading {@code if (!p->upkeep->update)}, and
         * no calculation is asked for.
         */
        @Test
        @DisplayName("nothing stale means nothing recalculated")
        void noFlagsMeansNoWork() {
            player.updateStuff();

            assertTrue(player.calls.isEmpty(), "no recalculation was asked for");
            assertEquals(0, level.viewUpdates);
            assertTrue(bus.events.isEmpty());
        }

        /**
         * C's clause order is load-bearing - the inventory feeds the bonuses, and the bonuses feed
         * the light, hit points and mana - so it is pinned as a sequence rather than a set.
         *
         * @throws ReflectiveOperationException if the class cannot be installed
         */
        @Test
        @DisplayName("every clause runs in C's order")
        void allClausesRunInOrder() throws ReflectiveOperationException {
            giveClass(4);
            raise(PlayerUpdateEnum.PU_PANEL, PlayerUpdateEnum.PU_MONSTERS,
                    PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_SPELLS,
                    PlayerUpdateEnum.PU_MANA, PlayerUpdateEnum.PU_HP,
                    PlayerUpdateEnum.PU_TORCH, PlayerUpdateEnum.PU_BONUS,
                    PlayerUpdateEnum.PU_INVEN);

            player.updateStuff();

            assertEquals(List.of("inventory", "bonuses", "light", "hitpoints", "mana", "spells",
                    "monsters(partial)"), player.calls);
            assertEquals(1, level.viewUpdates, "the view was rebuilt once");
            assertEquals(List.of(GameEventType.EVENT_PLAYERMOVED), bus.events);
        }

        /**
         * Every clause clears its own flag, so a pass leaves nothing stale and a second pass does
         * nothing at all.
         *
         * @throws ReflectiveOperationException if the class cannot be installed
         */
        @Test
        @DisplayName("a pass clears every flag it handled")
        void flagsAreCleared() throws ReflectiveOperationException {
            giveClass(4);
            raise(PlayerUpdateEnum.values());

            player.updateStuff();

            assertFalse(player.getPlayerUpkeep().getUpdate(), "nothing was left stale");

            player.calls.clear();
            player.updateStuff();

            assertTrue(player.calls.isEmpty(), "the second pass found nothing to do");
        }

        /**
         * One raised flag runs one calculation - the others are neither run nor disturbed.
         */
        @Test
        @DisplayName("a single flag runs only its own calculation")
        void oneFlagRunsOneCalculation() {
            raise(PlayerUpdateEnum.PU_HP);

            player.updateStuff();

            assertEquals(List.of("hitpoints"), player.calls);
        }
    }

    /**
     * The spell clause, which is the one with a condition of its own.
     */
    @Nested
    @DisplayName("the spell clause")
    class Spells {

        /**
         * A class with spells to learn has them recalculated.
         *
         * @throws ReflectiveOperationException if the class cannot be installed
         */
        @Test
        @DisplayName("a caster has their spells recalculated")
        void casterRecalculatesSpells() throws ReflectiveOperationException {
            giveClass(9);
            raise(PlayerUpdateEnum.PU_SPELLS);

            player.updateStuff();

            assertEquals(List.of("spells"), player.calls);
        }

        /**
         * A class with no spells at all skips the recalculation, but C clears the flag before
         * testing the class, so the request is still consumed rather than left pending forever.
         *
         * @throws ReflectiveOperationException if the class cannot be installed
         */
        @Test
        @DisplayName("a warrior skips the recalculation but still clears the flag")
        void nonCasterClearsTheFlagAnyway() throws ReflectiveOperationException {
            giveClass(0);
            raise(PlayerUpdateEnum.PU_SPELLS);

            player.updateStuff();

            assertTrue(player.calls.isEmpty(), "no spell recalculation was asked for");
            assertFalse(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_SPELLS));
        }
    }

    /**
     * The two early returns that separate the model half from the map half.
     */
    @Nested
    @DisplayName("the map guards")
    class MapGuards {

        /**
         * Before the character exists the model half still runs - birth calls this while building
         * the character - but nothing touches the map.
         */
        @Test
        @DisplayName("an ungenerated character gets the model half only")
        void ungeneratedCharacterStopsAtTheMap() {
            GameWorld.characterGenerated = false;
            raise(PlayerUpdateEnum.PU_HP, PlayerUpdateEnum.PU_UPDATE_VIEW,
                    PlayerUpdateEnum.PU_PANEL);

            player.updateStuff();

            assertEquals(List.of("hitpoints"), player.calls);
            assertEquals(0, level.viewUpdates);
            assertTrue(bus.events.isEmpty());
        }

        /**
         * A hidden map stops the pass at the same point.
         */
        @Test
        @DisplayName("a hidden map gets the model half only")
        void hiddenMapStopsAtTheMap() {
            GameInputHolder.setInstance(new HiddenMapInput());
            raise(PlayerUpdateEnum.PU_HP, PlayerUpdateEnum.PU_UPDATE_VIEW,
                    PlayerUpdateEnum.PU_PANEL);

            player.updateStuff();

            assertEquals(List.of("hitpoints"), player.calls);
            assertEquals(0, level.viewUpdates);
            assertTrue(bus.events.isEmpty());
        }

        /**
         * The map-half flags survive the early return, so the work happens on the first pass after
         * the map comes back rather than being lost.
         */
        @Test
        @DisplayName("the map-half flags are left raised for a later pass")
        void mapFlagsSurviveTheGuard() {
            GameInputHolder.setInstance(new HiddenMapInput());
            raise(PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_PANEL);

            player.updateStuff();

            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_UPDATE_VIEW));
            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_PANEL));

            GameInputHolder.resetInstance();
            player.updateStuff();

            assertEquals(1, level.viewUpdates, "the deferred view rebuild ran");
            assertEquals(List.of(GameEventType.EVENT_PLAYERMOVED), bus.events);
        }
    }

    /**
     * The monster clauses, where one flag consumes the other.
     */
    @Nested
    @DisplayName("the monster clauses")
    class Monsters {

        /**
         * A distance update is the full monster update, so C clears {@code PU_MONSTERS} alongside
         * it and the partial pass never runs.
         */
        @Test
        @DisplayName("a distance update subsumes a monster update")
        void distanceSubsumesMonsters() {
            raise(PlayerUpdateEnum.PU_DISTANCE, PlayerUpdateEnum.PU_MONSTERS);

            player.updateStuff();

            assertEquals(List.of("monsters(full)"), player.calls);
            assertFalse(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_MONSTERS));
        }

        /**
         * A distance update on its own is still the full pass - the clause does not depend on
         * {@code PU_MONSTERS} being raised too.
         */
        @Test
        @DisplayName("a distance update alone is still the full pass")
        void distanceAloneIsFull() {
            raise(PlayerUpdateEnum.PU_DISTANCE);

            player.updateStuff();

            assertEquals(List.of("monsters(full)"), player.calls);
        }

        /**
         * Without a distance update the cheaper pass runs, with {@code full} clear.
         */
        @Test
        @DisplayName("a monster update alone is the partial pass")
        void monstersAloneIsPartial() {
            raise(PlayerUpdateEnum.PU_MONSTERS);

            player.updateStuff();

            assertEquals(List.of("monsters(partial)"), player.calls);
        }
    }
}
