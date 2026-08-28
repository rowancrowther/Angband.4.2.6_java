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
import uk.co.jackoftrades.channel.messages.data.EventDataGrid;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.GameWorld;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.gameinput.DefaultGameInput;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#redrawStuff()}, the port of C's {@code redraw_stuff}
 * ({@code player-calcs.c:2678}).
 *
 * <p>Nothing is painted by the method, so what is worth pinning is which events reach the bus, which
 * flags are cleared, and which of C's four ways out was taken. All of it is read off the C function
 * and its {@code redraw_events} table ({@code player-calcs.c:2634}) rather than off the port: the
 * flag-to-event pairing below is a transcription of that table, with {@code PR_MAP} - which the
 * table deliberately omits - handled separately, as C does, because it is the one event carrying
 * data.
 *
 * <p>Ordering is only partly checked, and deliberately so. C emits its events in table order; the
 * port iterates the flag set instead, and Rowan has chosen not to reproduce the table's order. What
 * C's structure does guarantee, and what is therefore checked here, is that the map comes after the
 * other events and {@code EVENT_END} comes last of all.
 *
 * <p>The interesting boundaries are the resting/running hack ({@code % 100}, with a pending message
 * or map overriding it) and the hidden-map narrowing to {@code PR_SUBWINDOW}. The two interact: the
 * narrowing happens first, so with the map hidden the two overrides have already been masked out of
 * the snapshot and the hack always returns - a case worth its own test because it is easy to port in
 * the wrong order and hard to notice.
 *
 * <p>Globals are involved ({@link GameWorld#characterGenerated}, the {@code GameInput} boundary and
 * the events bus), so all three are set explicitly here and put back afterwards.
 *
 * <p>Class PlayerRedrawStuffTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerRedrawStuffTest {

    /**
     * C's {@code redraw_events} table, transcribed: the event each flag signals. {@code PR_MAP} is
     * absent from the C table and so absent here.
     */
    private static final Map<PlayerRedraw, GameEventType> C_TABLE = new EnumMap<>(PlayerRedraw.class);
    /**
     * C's {@code PR_SUBWINDOW} group ({@code player-calcs.h:95-96}), the only flags that survive a
     * hidden map.
     */
    private static final Set<PlayerRedraw> C_SUBWINDOW = Set.of(PlayerRedraw.PR_MONSTER,
            PlayerRedraw.PR_OBJECT, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST);

    static {
        C_TABLE.put(PlayerRedraw.PR_MISC, GameEventType.EVENT_RACE_CLASS);
        C_TABLE.put(PlayerRedraw.PR_TITLE, GameEventType.EVENT_PLAYERTITLE);
        C_TABLE.put(PlayerRedraw.PR_LEV, GameEventType.EVENT_PLAYERLEVEL);
        C_TABLE.put(PlayerRedraw.PR_EXP, GameEventType.EVENT_EXPERIENCE);
        C_TABLE.put(PlayerRedraw.PR_STATS, GameEventType.EVENT_STATS);
        C_TABLE.put(PlayerRedraw.PR_ARMOR, GameEventType.EVENT_AC);
        C_TABLE.put(PlayerRedraw.PR_HP, GameEventType.EVENT_HP);
        C_TABLE.put(PlayerRedraw.PR_MANA, GameEventType.EVENT_MANA);
        C_TABLE.put(PlayerRedraw.PR_GOLD, GameEventType.EVENT_GOLD);
        C_TABLE.put(PlayerRedraw.PR_HEALTH, GameEventType.EVENT_MONSTERHEALTH);
        C_TABLE.put(PlayerRedraw.PR_DEPTH, GameEventType.EVENT_DUNGEONLEVEL);
        C_TABLE.put(PlayerRedraw.PR_SPEED, GameEventType.EVENT_PLAYERSPEED);
        C_TABLE.put(PlayerRedraw.PR_STATE, GameEventType.EVENT_STATE);
        C_TABLE.put(PlayerRedraw.PR_STATUS, GameEventType.EVENT_STATUS);
        C_TABLE.put(PlayerRedraw.PR_STUDY, GameEventType.EVENT_STUDYSTATUS);
        C_TABLE.put(PlayerRedraw.PR_DTRAP, GameEventType.EVENT_DETECTIONSTATUS);
        C_TABLE.put(PlayerRedraw.PR_FEELING, GameEventType.EVENT_FEELING);
        C_TABLE.put(PlayerRedraw.PR_LIGHT, GameEventType.EVENT_LIGHT);
        C_TABLE.put(PlayerRedraw.PR_INVEN, GameEventType.EVENT_INVENTORY);
        C_TABLE.put(PlayerRedraw.PR_EQUIP, GameEventType.EVENT_EQUIPMENT);
        C_TABLE.put(PlayerRedraw.PR_MONLIST, GameEventType.EVENT_MONSTERLIST);
        C_TABLE.put(PlayerRedraw.PR_ITEMLIST, GameEventType.EVENT_ITEMLIST);
        C_TABLE.put(PlayerRedraw.PR_MONSTER, GameEventType.EVENT_MONSTERTARGET);
        C_TABLE.put(PlayerRedraw.PR_OBJECT, GameEventType.EVENT_OBJECTTARGET);
        C_TABLE.put(PlayerRedraw.PR_MESSAGE, GameEventType.EVENT_MESSAGE);
    }

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The bus installed for the test, capturing every event signalled.
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
     * A generated character, a visible map and a capturing bus - the ordinary mid-game conditions
     * under which every clause is reachable.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();

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
        GameInputHolder.resetInstance();
    }

    /**
     * Raises the given redraw flags on the player's upkeep.
     *
     * @param flags the flags to raise
     */
    private void raise(PlayerRedraw... flags) {
        for (PlayerRedraw flag : flags) {
            player.getPlayerUpkeep().setRedrawFlagsOn(flag);
        }
    }

    /**
     * @return the flags left raised on the upkeep
     */
    private Flag<PlayerRedraw> pending() {
        return player.getPlayerUpkeep().getRedrawFlags();
    }

    /**
     * Sets one of the upkeep's counters, neither of which has a setter.
     *
     * @param name  the field to set, {@code restingCounter} or {@code runningCounter}
     * @param value the value to set it to
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void setCounter(String name, int value) throws ReflectiveOperationException {
        Field field = PlayerUpkeep.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player.getPlayerUpkeep(), value);
    }

    /**
     * Captures the events signalled during the test, with the data each carried.
     *
     * @author Rowan Crowther
     */
    private static class CapturingBus implements EventsHandler {

        /**
         * Every event type signalled since the bus was installed, in order.
         */
        private final List<GameEventType> events = new ArrayList<>();

        /**
         * The data carried by each of those events, positionally.
         */
        private final List<GameEventData> data = new ArrayList<>();

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
            events.add(eventType);
            data.add(eventData);
        }
    }

    /**
     * A hidden map, as when a menu or the character sheet is covering it.
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
     * The events sent for the raised flags, and the flags cleared afterwards.
     */
    @Nested
    @DisplayName("signalling")
    class Signalling {

        /**
         * With nothing stale the method returns at C's leading {@code if (!redraw) return;} and the
         * bus is never touched.
         */
        @Test
        @DisplayName("nothing stale means nothing signalled")
        void noFlagsMeansNoEvents() {
            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "no event was signalled");
        }

        /**
         * The ordinary path: one event per raised flag, taken from C's table, then the closing
         * {@code EVENT_END}.
         */
        @Test
        @DisplayName("each raised flag signals its C event, then EVENT_END")
        void raisedFlagsSignalTheirEvents() {
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_GOLD, PlayerRedraw.PR_DEPTH);

            player.redrawStuff();

            assertEquals(Set.of(GameEventType.EVENT_HP, GameEventType.EVENT_GOLD,
                            GameEventType.EVENT_DUNGEONLEVEL, GameEventType.EVENT_END),
                    new LinkedHashSet<>(bus.events));
            assertEquals(4, bus.events.size(), "no event was signalled twice");
            assertEquals(GameEventType.EVENT_END, bus.events.get(bus.events.size() - 1),
                    "EVENT_END closes the batch");
        }

        /**
         * Every flag at once, checked against the transcribed C table so that a flag wired to the
         * wrong event is caught. {@code PR_MAP} is the extra one the table omits.
         */
        @Test
        @DisplayName("every flag maps to the event C's table gives it")
        void everyFlagMapsToItsCEvent() {
            for (PlayerRedraw flag : PlayerRedraw.values()) {
                raise(flag);
            }

            player.redrawStuff();

            Set<GameEventType> expected = new LinkedHashSet<>(C_TABLE.values());
            expected.add(GameEventType.EVENT_MAP);
            expected.add(GameEventType.EVENT_END);

            assertEquals(expected, new LinkedHashSet<>(bus.events));
            assertEquals(expected.size(), bus.events.size(), "no event was signalled twice");
        }

        /**
         * C signals the map after the table loop and {@code EVENT_END} after everything; that much
         * of the order is honoured, and is what this checks.
         */
        @Test
        @DisplayName("the map comes after the other events, and EVENT_END last")
        void mapThenEnd() {
            raise(PlayerRedraw.PR_MAP, PlayerRedraw.PR_HP, PlayerRedraw.PR_MESSAGE,
                    PlayerRedraw.PR_MONLIST);

            player.redrawStuff();

            int map = bus.events.indexOf(GameEventType.EVENT_MAP);
            assertEquals(bus.events.size() - 2, map, "the map is the last event before EVENT_END");
            assertEquals(GameEventType.EVENT_END, bus.events.get(bus.events.size() - 1));
        }

        /**
         * The map event is the one carrying data: C's {@code event_signal_point(EVENT_MAP, -1, -1)},
         * its sentinel for "the whole map" rather than one grid.
         */
        @Test
        @DisplayName("the map event carries C's whole-map sentinel")
        void mapCarriesWholeMapSentinel() {
            raise(PlayerRedraw.PR_MAP);

            player.redrawStuff();

            int map = bus.events.indexOf(GameEventType.EVENT_MAP);
            assertEquals(new EventDataGrid(-1, -1), bus.data.get(map));
        }

        /**
         * C clears exactly the flags it acted on ({@code p->upkeep->redraw &= ~redraw}).
         */
        @Test
        @DisplayName("the flags acted on are cleared")
        void actedFlagsAreCleared() {
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_MAP);

            player.redrawStuff();

            assertTrue(pending().isEmpty(), "nothing is left pending");
        }

        /**
         * Because C clears the snapshot rather than the live field, a flag raised by a handler
         * during the pass survives it. Here the bus dirties the gold display on seeing the hit
         * points change.
         */
        @Test
        @DisplayName("a flag raised during the pass survives it")
        void flagRaisedDuringPassSurvives() {
            GameEngine.setEventsBusHandler(new CapturingBus() {
                @Override
                public void gameEventDispatch(GameEventType eventType, GameEventData eventData) {
                    super.gameEventDispatch(eventType, eventData);
                    if (eventType == GameEventType.EVENT_HP) {
                        player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);
                    }
                }
            });
            raise(PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(pending().has(PlayerRedraw.PR_GOLD), "the new flag is still pending");
            assertFalse(pending().has(PlayerRedraw.PR_HP), "the handled flag was cleared");
        }

        /**
         * The snapshot is the caller's own, so the pass cannot be derailed by the upkeep's set
         * changing under it - and asking twice gives two objects.
         */
        @Test
        @DisplayName("the snapshot is not the upkeep's own set")
        void snapshotIsACopy() {
            raise(PlayerRedraw.PR_HP);

            Flag<PlayerRedraw> first = pending();
            Flag<PlayerRedraw> second = pending();

            assertFalse(first == second, "each call gives its own snapshot");
            first.off(PlayerRedraw.PR_HP);
            assertTrue(pending().has(PlayerRedraw.PR_HP), "the upkeep kept its flag");
        }
    }

    /**
     * C's two early returns, and the narrowing that looks like one but is not.
     */
    @Nested
    @DisplayName("guards")
    class Guards {

        /**
         * Before the character exists C returns without signalling, leaving the work for the first
         * pass after birth.
         */
        @Test
        @DisplayName("no character means nothing signalled and nothing cleared")
        void ungeneratedCharacterDoesNothing() {
            GameWorld.characterGenerated = false;
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_MAP);

            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP), "the flags are still pending");
            assertTrue(pending().has(PlayerRedraw.PR_MAP));
        }

        /**
         * A hidden map narrows the snapshot to C's {@code PR_SUBWINDOW}: the detachable panes still
         * refresh, everything else stays pending, and there is no {@code EVENT_END}.
         */
        @Test
        @DisplayName("a hidden map refreshes the subwindows only")
        void hiddenMapDoesSubwindowsOnly() {
            GameInputHolder.setInstance(new HiddenMapInput());
            for (PlayerRedraw flag : PlayerRedraw.values()) {
                raise(flag);
            }

            player.redrawStuff();

            Set<GameEventType> expected = new LinkedHashSet<>();
            for (PlayerRedraw flag : C_SUBWINDOW) {
                expected.add(C_TABLE.get(flag));
            }
            assertEquals(expected, new LinkedHashSet<>(bus.events));
            assertFalse(bus.events.contains(GameEventType.EVENT_END),
                    "the batch is not closed when only subwindows were refreshed");

            for (PlayerRedraw flag : PlayerRedraw.values()) {
                assertEquals(!C_SUBWINDOW.contains(flag), pending().has(flag),
                        flag + " pending after a hidden-map pass");
            }
        }

        /**
         * With the map hidden and nothing in the subwindow group raised, the narrowed snapshot is
         * empty: the loop signals nothing, the clear removes nothing, and every flag is still
         * waiting for the map to come back.
         */
        @Test
        @DisplayName("a hidden map with no subwindow flags leaves everything pending")
        void hiddenMapWithNoSubwindowFlags() {
            GameInputHolder.setInstance(new HiddenMapInput());
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_MAP, PlayerRedraw.PR_MESSAGE);

            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP));
            assertTrue(pending().has(PlayerRedraw.PR_MAP));
            assertTrue(pending().has(PlayerRedraw.PR_MESSAGE));
        }
    }

    /**
     * C's speed hack: while resting or running, refresh only every hundredth turn.
     */
    @Nested
    @DisplayName("the resting and running hack")
    class RestingHack {

        /**
         * Mid-rest, an ordinary flag waits: C returns while the resting counter is not a multiple
         * of a hundred.
         */
        @Test
        @DisplayName("mid-rest an ordinary flag is left pending")
        void midRestSkipsTheRedraw() throws ReflectiveOperationException {
            setCounter("restingCounter", 50);
            raise(PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP), "the flag is still pending");
        }

        /**
         * The running counter is the other half of C's {@code ||} and skips the redraw on its own.
         */
        @Test
        @DisplayName("mid-run an ordinary flag is left pending")
        void midRunSkipsTheRedraw() throws ReflectiveOperationException {
            setCounter("runningCounter", 7);
            raise(PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP));
        }

        /**
         * Every hundredth turn the hack lets a pass through - the boundary the {@code % 100} draws.
         */
        @Test
        @DisplayName("on the hundredth turn the redraw happens")
        void hundredthTurnRedraws() throws ReflectiveOperationException {
            setCounter("restingCounter", 100);
            raise(PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(bus.events.contains(GameEventType.EVENT_HP), "the redraw happened");
            assertTrue(pending().isEmpty());
        }

        /**
         * A "rest until healed" sentinel is negative, and C's {@code % 100} is negative with it, so
         * the hack fires exactly as it does mid-count. Java's remainder keeps the sign too, which is
         * what makes the two agree.
         */
        @Test
        @DisplayName("a negative rest sentinel counts as mid-rest")
        void negativeRestSentinelSkipsTheRedraw() throws ReflectiveOperationException {
            setCounter("restingCounter", -1);
            raise(PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP));
        }

        /**
         * A pending message overrides the hack, and once through, everything else raised is
         * redrawn with it.
         */
        @Test
        @DisplayName("a pending message overrides the hack")
        void pendingMessageOverridesTheHack() throws ReflectiveOperationException {
            setCounter("restingCounter", 50);
            raise(PlayerRedraw.PR_MESSAGE, PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(bus.events.contains(GameEventType.EVENT_MESSAGE));
            assertTrue(bus.events.contains(GameEventType.EVENT_HP),
                    "the other flags ride through with it");
            assertTrue(pending().isEmpty());
        }

        /**
         * A pending map redraw is the other override.
         */
        @Test
        @DisplayName("a pending map redraw overrides the hack")
        void pendingMapOverridesTheHack() throws ReflectiveOperationException {
            setCounter("runningCounter", 50);
            raise(PlayerRedraw.PR_MAP, PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertTrue(bus.events.contains(GameEventType.EVENT_MAP));
            assertTrue(bus.events.contains(GameEventType.EVENT_HP));
            assertEquals(GameEventType.EVENT_END, bus.events.get(bus.events.size() - 1));
        }

        /**
         * The narrowing happens before the hack, so with the map hidden the two overrides have
         * already been masked out of the snapshot and cannot save the pass. Reversing the two steps
         * would let the subwindows refresh here, which is the divergence this pins.
         */
        @Test
        @DisplayName("a hidden map masks the overrides away before the hack is tested")
        void hiddenMapMasksTheOverridesFirst() throws ReflectiveOperationException {
            GameInputHolder.setInstance(new HiddenMapInput());
            setCounter("restingCounter", 50);
            raise(PlayerRedraw.PR_MESSAGE, PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST);

            player.redrawStuff();

            assertTrue(bus.events.isEmpty(), "the hack returned before the subwindows refreshed");
            assertTrue(pending().has(PlayerRedraw.PR_MONLIST), "even the subwindow flag waits");
        }

        /**
         * Not resting and not running is the ordinary case: both counters are zero, both remainders
         * are zero, and the hack never fires.
         */
        @Test
        @DisplayName("standing still, the hack never fires")
        void standingStillAlwaysRedraws() {
            raise(PlayerRedraw.PR_HP);

            player.redrawStuff();

            assertSame(GameEventType.EVENT_HP, bus.events.get(0));
        }
    }
}
