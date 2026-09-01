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
import uk.co.jackoftrades.middle.game.globals.registry.StatTables;
import uk.co.jackoftrades.middle.gameinput.DefaultGameInput;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.monsters.MonsterUtils;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.CalcBonusesFixture;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#updateStuff(Player)}, the port of C's {@code update_stuff}
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
 * <p><b>How the dispatch is observed, now that the calculations are static.</b> The recalculations
 * live on {@link PlayerCalcs} as static methods, so a player subclass can no longer stand in front
 * of them and record the calls. What the dispatcher does reach through, on every clause, is
 * {@link PlayerUpkeep}: each clause asks {@link PlayerUpkeep#updateHas} and then clears with
 * {@link PlayerUpkeep#updateOff}. So the recording moves down a layer, to a {@link RecordingUpkeep}
 * installed on the player, and the sequence of those questions <em>is</em> C's clause sequence -
 * one question per clause, in source order, whether or not the flag it asks about is raised.
 *
 * <p>That gives the order for free and without running a single calculation: with
 * {@link RecordingUpkeep#pretendStale} set, the leading guard is passed while every flag reads
 * clear, so every clause is asked and none does any work. The calculations themselves then run for
 * real in the rest of the suite - the player is a {@link CalcBonusesFixture} plain character, which
 * is exactly the null character those calculations are already tested against - and each clause is
 * checked by the mark its own calculation leaves: new hit points, a fresh state object, a rebuilt
 * view, a monster pass, an event on the bus.
 *
 * <p>Three calculations leave no mark that this suite can read. {@code calcSpells} is a stub, and
 * {@code calcMana} on a class with no magic writes a zero over a zero; for those clauses the
 * dispatch is pinned by the question and the clearing, and the arithmetic belongs to
 * {@link PlayerCalcManaTest} either way. The third is
 * {@link MonsterUtils#updateMonsters(boolean)}, which is both static - so no subclass can stand in
 * front of it - and a chapter-6 stub that does nothing at all. Its two clauses are read off the
 * clearing instead, and that reading is exact rather than a fallback: the whole of what separates
 * the full pass from the cheap one is that {@code PU_DISTANCE} gives up {@code PU_MONSTERS}
 * alongside its own flag, so a {@code cleared} list of both flags <em>is</em> the full pass and a
 * list of {@code PU_MONSTERS} alone is the partial one.
 *
 * <p>The two early returns read global state ({@link GameWorld#characterGenerated} and the
 * {@code GameInput} boundary), so both are set explicitly here and put back afterwards; a test that
 * left either changed would silently decide the outcome of another class.
 *
 * <p>Class PlayerUpdateStuffTest coded on 260828, commented in full on 260828, reworked on 260901
 * for the move of the calculations to {@link PlayerCalcs} and of the monster pass to
 * {@link MonsterUtils}.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpdateStuffTest {

    /**
     * The hit points every level rolls, so {@code calcHitpoints} has a table to read.
     */
    private static final int HP_PER_LEVEL = 10;

    /**
     * The level-one maximum {@code calcHitpoints} arrives at once the bonus pass has filled the
     * stat indices: a neutral constitution's row of {@code adjConMhp} is zero, so the roll stands.
     */
    private static final int HP_AFTER_BONUSES = HP_PER_LEVEL;

    /**
     * The level-one maximum on a state the bonus pass has <em>not</em> filled, where every stat
     * index is still zero - the bottom row of {@code adjConMhp}, and the table's harshest penalty.
     * Pinning it makes the point that the hit point clause reads whatever the state currently holds
     * rather than recomputing the stats itself.
     */
    private static final int HP_ON_A_RAW_STATE =
            HP_PER_LEVEL + StatTables.adjConMhp[0] / 100;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The player's upkeep, recording the questions the dispatcher asks of it.
     */
    private RecordingUpkeep upkeep;

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
     * A plain character with a level, a visible map and a generated character - the ordinary
     * mid-game conditions under which every clause is reachable.
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    @BeforeEach
    void newPlayer() throws ReflectiveOperationException {
        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

        upkeep = new RecordingUpkeep();
        set("playerUpkeep", upkeep);

        // calcHitpoints reads the per-level roll table, which birth would have filled.
        int[] hitPoints = new int[50];
        for (int i = 0; i < hitPoints.length; i++) hitPoints[i] = HP_PER_LEVEL * (i + 1);
        set("playerHP", hitPoints);

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
     * Writes one of {@link Player}'s private fields.
     *
     * @param name  the field
     * @param value the value
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void set(String name, Object value) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
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

        set("playerClass", playerClass);
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
     * An upkeep that records the questions the dispatcher asks of it.
     *
     * <p>Every answer is the real one - this only listens. The clause order is the order of
     * {@link #queries}, and {@link #cleared} is the order in which the clauses gave their flags up.
     *
     * @author Rowan Crowther
     */
    private static final class RecordingUpkeep extends PlayerUpkeep {

        /**
         * The flags asked about, in the order the clauses asked.
         */
        private final List<PlayerUpdateEnum> queries = new ArrayList<>();

        /**
         * The flags cleared, in the order the clauses cleared them.
         */
        private final List<PlayerUpdateEnum> cleared = new ArrayList<>();

        /**
         * Whether the leading "is anything stale" guard is answered yes regardless, so a pass can
         * reach the clauses with nothing raised and do nothing at all.
         */
        private boolean pretendStale;

        @Override
        public boolean getUpdate() {
            return pretendStale || super.getUpdate();
        }

        @Override
        public boolean updateHas(PlayerUpdateEnum flag) {
            queries.add(flag);
            return super.updateHas(flag);
        }

        @Override
        public boolean updateOff(PlayerUpdateEnum flag) {
            cleared.add(flag);
            return super.updateOff(flag);
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
     * The dispatch itself: which clauses are reached, and in which order.
     */
    @Nested
    @DisplayName("dispatch")
    class Dispatch {

        /**
         * With nothing stale the method returns at C's leading {@code if (!p->upkeep->update)}, so
         * no clause is even asked about.
         */
        @Test
        @DisplayName("nothing stale means no clause is reached")
        void noFlagsMeansNoWork() {
            PlayerCalcs.updateStuff(player);

            assertTrue(upkeep.queries.isEmpty(), "the leading guard returned before any clause");
            assertTrue(upkeep.cleared.isEmpty(), "and nothing was cleared");
            assertEquals(0, level.viewUpdates);
            assertTrue(bus.events.isEmpty());
        }

        /**
         * C's clause order is load-bearing - the inventory feeds the bonuses, and the bonuses feed
         * the light, hit points and mana - so it is pinned as a sequence rather than a set. Past the
         * leading guard every clause asks its question whether or not its flag is raised, so with
         * nothing raised the questions are the clause order on their own, and no calculation runs to
         * obscure it.
         */
        @Test
        @DisplayName("every clause is reached in C's order")
        void allClausesRunInOrder() {
            upkeep.pretendStale = true;

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(PlayerUpdateEnum.PU_INVEN, PlayerUpdateEnum.PU_BONUS,
                            PlayerUpdateEnum.PU_TORCH, PlayerUpdateEnum.PU_HP,
                            PlayerUpdateEnum.PU_MANA, PlayerUpdateEnum.PU_SPELLS,
                            PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_DISTANCE,
                            PlayerUpdateEnum.PU_MONSTERS, PlayerUpdateEnum.PU_PANEL),
                    upkeep.queries);
            assertTrue(upkeep.cleared.isEmpty(), "no flag was raised, so none was cleared");
            assertEquals(0, level.viewUpdates);
            assertTrue(bus.events.isEmpty());
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

            PlayerCalcs.updateStuff(player);

            assertFalse(player.getPlayerUpkeep().getUpdate(), "nothing was left stale");

            upkeep.queries.clear();
            PlayerCalcs.updateStuff(player);

            assertTrue(upkeep.queries.isEmpty(), "the second pass found nothing to do");
        }

        /**
         * Each clause clears in its own order too, and clears before it calls - the clearing is what
         * lets a calculation legitimately ask for another pass of itself.
         */
        @Test
        @DisplayName("each clause gives up its own flag")
        void eachClauseClearsItsOwn() {
            raise(PlayerUpdateEnum.PU_HP, PlayerUpdateEnum.PU_TORCH, PlayerUpdateEnum.PU_PANEL);

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(PlayerUpdateEnum.PU_TORCH, PlayerUpdateEnum.PU_HP,
                    PlayerUpdateEnum.PU_PANEL), upkeep.cleared);
        }

        /**
         * One raised flag runs one calculation - here the hit point pass, which is visible in the
         * new maximum and the repaint it asks for. The others are neither run nor disturbed.
         */
        @Test
        @DisplayName("a single flag runs only its own calculation")
        void oneFlagRunsOneCalculation() {
            raise(PlayerUpdateEnum.PU_HP);

            PlayerCalcs.updateStuff(player);

            assertEquals(HP_ON_A_RAW_STATE, player.getMaxHP(), "the hit points were recalculated");
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_HP));
            assertEquals(List.of(PlayerUpdateEnum.PU_HP), upkeep.cleared,
                    "and no monster pass was reached");
            assertEquals(0, level.viewUpdates);
        }

        /**
         * The bonus clause installs a freshly derived state over the old one, which is the mark it
         * leaves; the flags it raises for the figures it changed are then serviced by the clauses
         * below it, on this same pass, because C puts {@code PU_BONUS} first for that reason.
         */
        @Test
        @DisplayName("the bonus clause runs, and its own requests are serviced below it")
        void bonusClauseRunsAndFeedsTheRest() {
            PlayerState before = player.getPlayerState();
            raise(PlayerUpdateEnum.PU_BONUS);

            PlayerCalcs.updateStuff(player);

            assertNotSame(before, player.getPlayerState(), "a fresh state was installed");
            assertTrue(upkeep.cleared.contains(PlayerUpdateEnum.PU_HP),
                    "the hit point recalculation it asked for ran on the same pass");
            assertEquals(HP_AFTER_BONUSES, player.getMaxHP());
        }
    }

    /**
     * The spell clause, which is the one with a condition of its own.
     */
    @Nested
    @DisplayName("the spell clause")
    class Spells {

        /**
         * A class with spells to learn reaches the recalculation.
         *
         * <p><b>Outstanding:</b> {@code calcSpells} is a stub taking no arguments, so a caster's
         * pass leaves no mark to assert on; what is pinned here is that the clause was reached and
         * the flag consumed. When the magic subsystem lands, this is the test that should grow to
         * check the spells themselves.
         *
         * @throws ReflectiveOperationException if the class cannot be installed
         */
        @Test
        @DisplayName("a caster reaches the recalculation")
        void casterRecalculatesSpells() throws ReflectiveOperationException {
            giveClass(9);
            raise(PlayerUpdateEnum.PU_SPELLS);

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(PlayerUpdateEnum.PU_SPELLS), upkeep.cleared);
            assertTrue(player.getPlayerClass().getMagic().getTotalSpells() > 0,
                    "the class the clause tested had spells to learn");
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

            PlayerCalcs.updateStuff(player);

            assertEquals(0, player.getPlayerClass().getMagic().getTotalSpells());
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
         * the character - but nothing below the guard is even asked about.
         */
        @Test
        @DisplayName("an ungenerated character gets the model half only")
        void ungeneratedCharacterStopsAtTheMap() {
            GameWorld.characterGenerated = false;
            raise(PlayerUpdateEnum.PU_HP, PlayerUpdateEnum.PU_UPDATE_VIEW,
                    PlayerUpdateEnum.PU_PANEL);

            PlayerCalcs.updateStuff(player);

            assertEquals(HP_ON_A_RAW_STATE, player.getMaxHP(), "the model half ran");
            assertEquals(List.of(PlayerUpdateEnum.PU_INVEN, PlayerUpdateEnum.PU_BONUS,
                            PlayerUpdateEnum.PU_TORCH, PlayerUpdateEnum.PU_HP,
                            PlayerUpdateEnum.PU_MANA, PlayerUpdateEnum.PU_SPELLS),
                    upkeep.queries, "and the map half was never reached");
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

            PlayerCalcs.updateStuff(player);

            assertEquals(HP_ON_A_RAW_STATE, player.getMaxHP(), "the model half ran");
            assertFalse(upkeep.queries.contains(PlayerUpdateEnum.PU_UPDATE_VIEW),
                    "the map half was never reached");
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

            PlayerCalcs.updateStuff(player);

            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_UPDATE_VIEW));
            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_PANEL));

            GameInputHolder.resetInstance();
            PlayerCalcs.updateStuff(player);

            assertEquals(1, level.viewUpdates, "the deferred view rebuild ran");
            assertEquals(List.of(GameEventType.EVENT_PLAYERMOVED), bus.events);
        }
    }

    /**
     * The monster clauses, where one flag consumes the other.
     *
     * <p>The pass itself is a static stub, so each case is read off which flags the clause gave up
     * - the subsumption is the observable behaviour, and it is the only thing C's two clauses
     * differ by.
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

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(PlayerUpdateEnum.PU_DISTANCE, PlayerUpdateEnum.PU_MONSTERS),
                    upkeep.cleared, "the distance clause gave up both flags, so the full pass ran");
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

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(PlayerUpdateEnum.PU_DISTANCE, PlayerUpdateEnum.PU_MONSTERS),
                    upkeep.cleared, "the distance clause still gave up both flags");
        }

        /**
         * Without a distance update the cheaper pass runs, with {@code full} clear.
         */
        @Test
        @DisplayName("a monster update alone is the partial pass")
        void monstersAloneIsPartial() {
            raise(PlayerUpdateEnum.PU_MONSTERS);

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(PlayerUpdateEnum.PU_MONSTERS), upkeep.cleared,
                    "only the monster clause was reached, so the pass was the cheap one");
        }
    }

    /**
     * The view and panel clauses, the two that reach across the boundary.
     */
    @Nested
    @DisplayName("the map clauses")
    class MapClauses {

        /**
         * The view is rebuilt on the real level, not the player's own reference to it.
         */
        @Test
        @DisplayName("the view clause rebuilds the level's view")
        void viewClauseRebuilds() {
            raise(PlayerUpdateEnum.PU_UPDATE_VIEW);

            PlayerCalcs.updateStuff(player);

            assertEquals(1, level.viewUpdates);
            assertTrue(bus.events.isEmpty(), "and nothing else was signalled");
        }

        /**
         * The panel clause signals the event the viewport re-centres on, which is all C does here
         * too.
         */
        @Test
        @DisplayName("the panel clause signals a player move")
        void panelClauseSignals() {
            raise(PlayerUpdateEnum.PU_PANEL);

            PlayerCalcs.updateStuff(player);

            assertEquals(List.of(GameEventType.EVENT_PLAYERMOVED), bus.events);
            assertEquals(0, level.viewUpdates);
        }
    }
}
