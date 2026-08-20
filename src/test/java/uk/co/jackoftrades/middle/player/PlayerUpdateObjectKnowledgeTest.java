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
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.objects.ItemObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

/**
 * Tests the two populations {@link Player#updateObjectKnowledge()} can currently reach — the objects
 * lying on the level and the ones the player is carrying — and the events it always signals. The
 * port of C's {@code update_player_object_knowledge} ({@code obj-knowledge.c:1214}).
 *
 * <p><b>What is observed, and why it is the walk rather than the outcome.</b> The method's real work
 * is delegated to {@link Player#knowObject}. These tests watch <em>which objects were handed to
 * it</em>, through a {@link Player} subclass that records its arguments, rather than what the
 * knowledge transfer then did. That is deliberately the durable half of the behaviour: this method
 * is responsible for visiting the right objects in the right order and signalling afterwards, not
 * for what visiting one does, so the suite stays valid however {@code knowObject} changes. It was
 * written on 260815 while {@code knowObject} was still a stub and passed unchanged when it was
 * implemented on 260816, which is the property it was built for.
 *
 * <p>Two of C's four populations have no test here because they have no code yet: stores wait on
 * Chapter 8, and curse objects wait on {@link uk.co.jackoftrades.middle.objects.Curse} gaining
 * somewhere to hold what is known about it. Autoinscribe is Chapter 4. The absence of those branches
 * is not something a test can assert, so it is recorded in the method's Javadoc instead.
 *
 * <p>Class PlayerUpdateObjectKnowledgeTest coded on 260815, commented in full on 260815.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpdateObjectKnowledgeTest {

    private CountingPlayer player;
    private CapturingBus bus;
    private EventsHandler realBus;

    /**
     * Writes a private field on anything, for the state a running game would have filled in and this
     * suite does not run.
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Writes a private field declared on {@link Player} itself, which {@link #poke} cannot reach on a
     * subclass instance — {@code getDeclaredField} does not search superclasses.
     */
    private static void pokePlayer(Player target, String name, Object value) throws Exception {
        Field f = Player.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @BeforeEach
    void setUp() {
        player = new CountingPlayer();
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * A level holding the given objects. The smallest legal chunk is 0×0 — nothing here reads a
     * square, and a real level's dimensions would only slow the fixture down.
     */
    private Chunk levelHolding(ItemObject... items) throws Exception {
        Chunk chunk = new Chunk("test", 0, 0, 0, 0, 0, false, 0, 0, 0, 0, 0, 0, 0, 0, player);
        poke(chunk, "objects", new ArrayList<>(List.of(items)));
        return chunk;
    }

    /**
     * Puts the given items in the player's pack. C walks {@code p->gear} as a linked list; the port
     * holds an {@link ArrayList}, which is why the method needs a null guard where C needs none.
     */
    private void carrying(ItemObject... items) throws Exception {
        pokePlayer(player, "gear", new ArrayList<>(List.of(items)));
    }

    /**
     * A {@link Player} that records what {@link Player#knowObject} was handed, since the method
     * itself is a stub and leaves no state to inspect. Overriding rather than spying keeps the test
     * honest about the boundary being checked: this suite is about the walk, and stops where
     * {@code knowObject} begins.
     *
     * @author Rowan Crowther
     */
    private static final class CountingPlayer extends Player {
        private final List<ItemObject> visited = new ArrayList<>();

        @Override
        public void knowObject(ItemObject item) {
            visited.add(item);
        }
    }

    /**
     * Catches the event types signalled, in order. The rune-learning suite's bus keeps only
     * {@code EVENT_MESSAGE}; this one wants the two redraws instead, so it records the type of every
     * dispatch.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventType> signalled = new ArrayList<>();

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
            signalled.add(eventType);
        }
    }

    /**
     * The objects visited on the level and in the pack, in the order they were handed over.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the populations it walks")
    class Populations {

        @Test
        @DisplayName("every object on the level is visited")
        void levelObjectsAreVisited() throws Exception {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            pokePlayer(player, "cave", levelHolding(first, second));
            carrying();

            player.updateObjectKnowledge();

            assertEquals(List.of(first, second), player.visited);
        }

        @Test
        @DisplayName("every object in the pack is visited")
        void gearObjectsAreVisited() throws Exception {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            carrying(first, second);

            player.updateObjectKnowledge();

            assertEquals(List.of(first, second), player.visited);
        }

        /**
         * Both populations in one call, and the level before the pack — C's order. Nothing depends on
         * it today, but a walk that reordered itself would be a divergence worth noticing.
         */
        @Test
        @DisplayName("the level is walked before the pack")
        void levelComesBeforeGear() throws Exception {
            ItemObject onFloor = new ItemObject();
            ItemObject inPack = new ItemObject();
            pokePlayer(player, "cave", levelHolding(onFloor));
            carrying(inPack);

            player.updateObjectKnowledge();

            assertEquals(List.of(onFloor, inPack), player.visited);
        }

        /**
         * The same object in both populations is visited twice. C does the same — the loops do not
         * consult each other — and `player_know_object` is idempotent, so the repeat is wasted work
         * rather than a bug.
         */
        @Test
        @DisplayName("an object in both populations is visited from each")
        void anObjectInBothIsVisitedTwice() throws Exception {
            ItemObject item = new ItemObject();
            pokePlayer(player, "cave", levelHolding(item));
            carrying(item);

            player.updateObjectKnowledge();

            assertEquals(List.of(item, item), player.visited);
            assertSame(player.visited.get(0), player.visited.get(1));
        }

        @Test
        @DisplayName("an empty level and an empty pack visit nothing")
        void emptyPopulationsVisitNothing() throws Exception {
            pokePlayer(player, "cave", levelHolding());
            carrying();

            player.updateObjectKnowledge();

            assertTrue(player.visited.isEmpty());
        }
    }

    /**
     * The two null guards. They are not the same in origin, which is worth keeping apart.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the null guards")
    class NullGuards {

        /**
         * C guards the level walk with {@code if (cave)} for a real reason: knowledge is updated
         * during birth and on loading a save, before any level exists.
         */
        @Test
        @DisplayName("no level is not an error, and the pack is still walked")
        void noCaveIsSurvivable() throws Exception {
            ItemObject inPack = new ItemObject();
            carrying(inPack);

            assertDoesNotThrow(() -> player.updateObjectKnowledge());

            assertEquals(List.of(inPack), player.visited);
        }

        /**
         * The gear guard has no counterpart in C, and needs none: {@code for (obj = p->gear; obj;
         * obj = obj->next)} on a null head is simply an empty loop. A Java {@code null} list would
         * throw, so this guard is the port paying for the container change rather than copying
         * anything.
         */
        @Test
        @DisplayName("no pack is not an error, and the level is still walked")
        void noGearIsSurvivable() throws Exception {
            ItemObject onFloor = new ItemObject();
            pokePlayer(player, "cave", levelHolding(onFloor));
            pokePlayer(player, "gear", null);

            assertDoesNotThrow(() -> player.updateObjectKnowledge());

            assertEquals(List.of(onFloor), player.visited);
        }

        /**
         * A player with neither — the state a fresh {@link Player} is in before birth fills it.
         */
        @Test
        @DisplayName("neither level nor pack is not an error")
        void neitherIsSurvivable() throws Exception {
            pokePlayer(player, "gear", null);

            assertDoesNotThrow(() -> player.updateObjectKnowledge());

            assertTrue(player.visited.isEmpty());
        }
    }

    /**
     * The two signals at the tail. C sends them unconditionally, outside every guard, which is what
     * these tests are really pinning.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the events it signals")
    class Signals {

        @Test
        @DisplayName("inventory and equipment are both signalled, in that order")
        void bothEventsAreSignalled() throws Exception {
            carrying(new ItemObject());

            player.updateObjectKnowledge();

            assertEquals(List.of(GameEventType.EVENT_INVENTORY, GameEventType.EVENT_EQUIPMENT),
                    bus.signalled);
        }

        /**
         * Unconditional, and this is the case that says so: nothing was walked, and both signals
         * still went out. C puts them after the guards rather than inside them, because the display
         * has to be redrawn on the strength of the rune that was just learned even if no object in
         * play happens to carry it.
         */
        @Test
        @DisplayName("both are signalled even when nothing was visited")
        void eventsFireWithNothingToDo() throws Exception {
            pokePlayer(player, "gear", null);

            player.updateObjectKnowledge();

            assertEquals(List.of(GameEventType.EVENT_INVENTORY, GameEventType.EVENT_EQUIPMENT),
                    bus.signalled);
        }

        /**
         * Once per call, not once per object — a walk of three items still redraws twice.
         */
        @Test
        @DisplayName("signalled once per call, whatever the population size")
        void eventsAreNotPerObject() throws Exception {
            pokePlayer(player, "cave",
                    levelHolding(new ItemObject(), new ItemObject(), new ItemObject()));
            carrying(new ItemObject());

            player.updateObjectKnowledge();

            assertEquals(2, bus.signalled.size());
        }
    }
}