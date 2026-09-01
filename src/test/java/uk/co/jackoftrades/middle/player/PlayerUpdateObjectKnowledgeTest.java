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
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

/**
 * Tests the two populations {@link PlayerKnowledge#updateObjectKnowledge} can currently reach — the
 * objects lying on the level and the ones the player is carrying — and the events it always signals.
 * The port of C's {@code update_player_object_knowledge} ({@code obj-knowledge.c:1214}).
 *
 * <p><b>What is observed, and why it is the walk rather than the outcome.</b> The method's real work
 * is delegated to {@link PlayerKnowledge#knowObject}. These tests watch <em>which objects the two
 * loops yielded</em>, and in what order, rather than what the knowledge transfer then did. That is
 * deliberately the durable half of the behaviour: this method is responsible for visiting the right
 * objects in the right order and signalling afterwards, not for what visiting one does, so the suite
 * stays valid however {@code knowObject} changes. It was written on 260815 while {@code knowObject}
 * was still a stub and passed unchanged when it was implemented on 260816, which is the property it
 * was built for.
 *
 * <p><b>The seam is the collection, not the player.</b> Until 260901 the recording was done by a
 * {@link Player} subclass overriding {@code knowObject}. That override is no longer possible, and
 * would no longer be right: knowledge has moved out of {@link Player} into {@link PlayerKnowledge},
 * where both methods are static, and a static call has nothing to override. What records instead is
 * {@link RecordingList}, the list the walk iterates — pushed into the level's objects and into the
 * player's gear — which reports each element as the loop takes it. That watches the loop itself
 * rather than what it calls, so it survives {@code knowObject} moving again, changing signature, or
 * being called through something else entirely.
 *
 * <p>Two of C's four populations have no test here because they have no code yet: stores wait on
 * Chapter 8, and curse objects wait on {@link uk.co.jackoftrades.middle.objects.Curse} gaining
 * somewhere to hold what is known about it. Autoinscribe is Chapter 4. The absence of those branches
 * is not something a test can assert, so it is recorded in the method's Javadoc instead.
 *
 * <p>Class PlayerUpdateObjectKnowledgeTest coded on 260815, commented in full on 260815, reworked
 * onto the collection seam on 260901.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpdateObjectKnowledgeTest {

    /**
     * The objects the walk yielded, in order, filled in by the {@link RecordingList}s the fixtures
     * install. Cleared for each test with the player.
     */
    private final List<ItemObject> visited = new ArrayList<>();
    private CapturingBus bus;
    private EventsHandler realBus;
    private Player player;

    /**
     * Writes a private field on anything, for the state a running game would have filled in and this
     * suite does not run.
     *
     * <p>One helper suffices now the player under test is a plain {@link Player}. While it was a
     * subclass this could not reach the fields {@code Player} declares, since
     * {@code getDeclaredField} does not search superclasses, and a second helper existed for them.
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @BeforeEach
    void setUp() {
        player = new Player();
        visited.clear();
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
        poke(chunk, "objects", new RecordingList(items));
        return chunk;
    }

    /**
     * Puts the given items in the player's pack. C walks {@code p->gear} as a linked list; the port
     * holds an {@link ArrayList}, which is why the method needs a null guard where C needs none.
     */
    private void carrying(ItemObject... items) throws Exception {
        poke(player, "gear", new RecordingList(items));
    }

    /**
     * The list the walk iterates, which notes each element into {@link #visited} as the loop takes
     * it. An {@link ArrayList} subclass rather than a bare {@link List} because that is the declared
     * type of {@code Player.gear}, and the field is written by reflection.
     *
     * <p>Only {@link #iterator()} is intercepted, so the recording happens exactly where the walk
     * happens and nowhere else. The level's copy is read back through
     * {@code Collections.unmodifiableList}, whose iterator delegates to this one, so a floor object
     * is recorded the same way a carried one is.
     *
     * @author Rowan Crowther
     */
    private final class RecordingList extends ArrayList<ItemObject> {
        RecordingList(ItemObject... items) {
            super(List.of(items));
        }

        @Override
        public Iterator<ItemObject> iterator() {
            Iterator<ItemObject> underlying = super.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return underlying.hasNext();
                }

                @Override
                public ItemObject next() {
                    ItemObject item = underlying.next();
                    visited.add(item);
                    return item;
                }
            };
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
            poke(player, "cave", levelHolding(first, second));
            carrying();

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(first, second), visited);
        }

        @Test
        @DisplayName("every object in the pack is visited")
        void gearObjectsAreVisited() throws Exception {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            carrying(first, second);

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(first, second), visited);
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
            poke(player, "cave", levelHolding(onFloor));
            carrying(inPack);

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(onFloor, inPack), visited);
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
            poke(player, "cave", levelHolding(item));
            carrying(item);

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(item, item), visited);
            assertSame(visited.get(0), visited.get(1));
        }

        @Test
        @DisplayName("an empty level and an empty pack visit nothing")
        void emptyPopulationsVisitNothing() throws Exception {
            poke(player, "cave", levelHolding());
            carrying();

            PlayerKnowledge.updateObjectKnowledge(player);

            assertTrue(visited.isEmpty());
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

            assertDoesNotThrow(() -> PlayerKnowledge.updateObjectKnowledge(player));

            assertEquals(List.of(inPack), visited);
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
            poke(player, "cave", levelHolding(onFloor));
            poke(player, "gear", null);

            assertDoesNotThrow(() -> PlayerKnowledge.updateObjectKnowledge(player));

            assertEquals(List.of(onFloor), visited);
        }

        /**
         * A player with neither — the state a fresh {@link Player} is in before birth fills it.
         */
        @Test
        @DisplayName("neither level nor pack is not an error")
        void neitherIsSurvivable() throws Exception {
            poke(player, "gear", null);

            assertDoesNotThrow(() -> PlayerKnowledge.updateObjectKnowledge(player));

            assertTrue(visited.isEmpty());
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

            PlayerKnowledge.updateObjectKnowledge(player);

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
            poke(player, "gear", null);

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(GameEventType.EVENT_INVENTORY, GameEventType.EVENT_EQUIPMENT),
                    bus.signalled);
        }

        /**
         * Once per call, not once per object — a walk of three items still redraws twice.
         */
        @Test
        @DisplayName("signalled once per call, whatever the population size")
        void eventsAreNotPerObject() throws Exception {
            poke(player, "cave",
                    levelHolding(new ItemObject(), new ItemObject(), new ItemObject()));
            carrying(new ItemObject());

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(2, bus.signalled.size());
        }
    }
}