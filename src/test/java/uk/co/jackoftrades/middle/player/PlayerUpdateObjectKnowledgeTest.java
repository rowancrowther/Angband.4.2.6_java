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
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.KnownObject;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

/**
 * Tests the three populations {@link PlayerKnowledge#updateObjectKnowledge} can currently reach —
 * the objects lying on the level, the ones the player is carrying, and the curse definitions in the
 * registry — and the events it always signals. The port of C's
 * {@code update_player_object_knowledge} ({@code obj-knowledge.c:1214}).
 *
 * <p><b>What is observed, and why it is the walk rather than the outcome.</b> The method's real work
 * is delegated to {@link PlayerKnowledge#knowObject}. These tests watch <em>what the three
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
 * <p><b>The curse population is the exception to the walk-only rule.</b> {@link Curse} now holds
 * its own {@code known*} fields, so the third loop both visits and writes, and there is no known
 * counterpart object to inspect afterwards — {@link Curse#isFullyKnown()} is the only public window
 * onto the result. So that group tests the walk as the other two do, and adds a pair of end-to-end
 * cases that show the visit is a transfer rather than an empty pass.
 *
 * <p>The curse loop is fed by installing a recording list into {@link ObjectRegistry} for the
 * duration of one test, and putting back whatever was there afterwards. That matters more than the
 * usual tidiness: the registry is global static state, and
 * {@link uk.co.jackoftrades.testsupport.SeededPlayerRegistry} seeds it only when it is empty, so a
 * class that left curses behind would change what a later class sees.
 *
 * <p><b>Every test starts from an empty curse registry, not from whatever was loaded.</b> The
 * extension can only seed a list that is {@code null}, so a class running after a reader has parsed
 * {@code curse.txt} inherits the real forty-odd curses. That is not a neutral difference here: the
 * curse loop dereferences {@code player.itemKnowledge} before it does anything else, and a
 * {@link Player} that has not been through birth has none, so the whole class throws in a full-suite
 * run and passes when run alone. Pinning the list makes the result independent of what ran first.
 * The underlying asymmetry — the two object loops survive a knowledge-free player and the curse loop
 * does not — is a property of the code under test, not of the fixture.
 *
 * <p>One of C's four populations still has no test here because it has no code yet: stores wait on
 * Chapter 8, and autoinscribe is Chapter 4. The absence of those branches is not something a test
 * can assert, so it is recorded in the method's Javadoc instead.
 *
 * <p>Class PlayerUpdateObjectKnowledgeTest coded on 260815, commented in full on 260815, reworked
 * onto the collection seam on 260901, curse population added on 260901.
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

    /**
     * Everything the three loops yielded, in one list and in order, so that a test can pin the
     * order of the populations against each other. Objects and curses have no common supertype, so
     * this is the only place they can be compared side by side.
     */
    private final List<Object> visitOrder = new ArrayList<>();

    /**
     * Whatever {@link ObjectRegistry} held before a test replaced it, put back afterwards.
     */
    private List<Curse> realCurses;
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

    /**
     * Reads a private static field, for saving global registry state a test is about to replace.
     */
    @SuppressWarnings("unchecked")
    private static <T> T peekStatic(Class<?> owner, String name) throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(null);
    }

    /**
     * Writes a private static field, the counterpart of {@link #peekStatic}.
     */
    private static void pokeStatic(Class<?> owner, String name, Object value) throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        f.set(null, value);
    }

    /**
     * A curse carrying the given modifiers and nothing else — no flags, no elements, no effect and
     * no combat figures, so that {@link Curse#isFullyKnown()} turns on the modifiers alone.
     */
    private static Curse curseWithModifiers(String name, Map<ObjectModifier, Integer> modifiers) {
        return new Curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), modifiers,
                Map.<ElementEnum, ElementInfo>of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        visited.clear();
        visitOrder.clear();
        realCurses = peekStatic(ObjectRegistry.class, "curses");
        pokeStatic(ObjectRegistry.class, "curses", new RecordingCurseList());
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() throws Exception {
        GameEngine.setEventsBusHandler(realBus);
        pokeStatic(ObjectRegistry.class, "curses", realCurses);
    }

    /**
     * Puts the given curses in the registry, where the third loop reads them, and gives the player
     * the knowledge object that loop dereferences.
     *
     * <p>The knowledge is built after the curses are installed, because {@link KnownObject} sizes
     * its own curse map from the registry — C's {@code z_info->curse_max} ordering constraint, kept.
     * That constructor walks the same list this is recording, so the record is cleared afterwards
     * and each test sees only what {@code updateObjectKnowledge} itself visited.
     */
    private void registryHolding(Curse... curses) throws Exception {
        pokeStatic(ObjectRegistry.class, "curses", new RecordingCurseList(curses));
        player.itemKnowledge = new KnownObject();
        visited.clear();
        visitOrder.clear();
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
                    visitOrder.add(item);
                    return item;
                }
            };
        }
    }

    /**
     * The curse list the third loop iterates, recording each curse into {@link #visitOrder} as the
     * loop takes it. The same device as {@link RecordingList} and for the same reason, but a
     * separate class because the registry field is a {@code List<Curse>}.
     *
     * <p>{@link ObjectRegistry#getCurses()} hands out a {@code Collections.unmodifiableList} view
     * rather than the field itself, and that view's iterator delegates to this one, so the recording
     * survives the wrapper.
     *
     * @author Rowan Crowther
     */
    private final class RecordingCurseList extends ArrayList<Curse> {
        RecordingCurseList(Curse... curses) {
            super(List.of(curses));
        }

        @Override
        public Iterator<Curse> iterator() {
            Iterator<Curse> underlying = super.iterator();
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return underlying.hasNext();
                }

                @Override
                public Curse next() {
                    Curse curse = underlying.next();
                    visitOrder.add(curse);
                    return curse;
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
     * The null guards. They are not the same in origin, which is worth keeping apart: two are about
     * the populations being absent, the third about the player's knowledge being absent.
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

        /**
         * The curse loop is the one population that reads {@code player.itemKnowledge}, and it reads
         * it four times over - the combat figures, the modifiers, the elements and the flags. A
         * player who has not been through birth has none, and C cannot reach this state at all
         * because {@code p->obj_k} is allocated with the player rather than assigned into them, so
         * the guards are the port paying for its own construction order.
         *
         * <p>The curse here is deliberately not the empty one the other curse tests use: it carries
         * a modifier, a flag, an element and all three combat figures, so that every one of the four
         * reads is actually reached rather than skipped over an empty collection.
         *
         * <p>The registry is installed directly rather than through {@link #registryHolding}, which
         * would supply the {@link KnownObject} this test exists to withhold.
         */
        @Test
        @DisplayName("no knowledge is not an error, even with a curse to walk")
        void noItemKnowledgeIsSurvivable() throws Exception {
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.set(ObjectFlag.OF_FEATHER);
            Curse curse = new Curse("uncursed player", List.of(), 0, null, flags,
                    Map.of(ObjectModifier.OM_STR, 3),
                    Map.of(ElementEnum.ELEM_FIRE, new ElementInfo()), 2, 3, 4,
                    List.of(), new Flag<>(ObjectFlag.class), "", "");
            pokeStatic(ObjectRegistry.class, "curses", new RecordingCurseList(curse));
            poke(player, "gear", null);
            player.itemKnowledge = null;

            assertDoesNotThrow(() -> PlayerKnowledge.updateObjectKnowledge(player));

            assertEquals(List.of(curse), visitOrder);
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

    /**
     * The third population — the curse definitions in {@link ObjectRegistry}. C walks
     * {@code curses[i].obj}, the carrier object each curse hangs its properties on; the port walks
     * the {@link Curse} objects themselves, which is the same population reached without the
     * indirection.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the curse population")
    class Curses {

        @Test
        @DisplayName("every curse in the registry is visited")
        void everyCurseIsVisited() throws Exception {
            Curse first = curseWithModifiers("first", Map.of());
            Curse second = curseWithModifiers("second", Map.of());
            registryHolding(first, second);
            carrying();

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(first, second), visitOrder);
        }

        /**
         * C's order: level, gear, stores, curses. Stores are missing, so what can be pinned is that
         * the curses come last of the three that exist.
         */
        @Test
        @DisplayName("the curses are walked after the level and the pack")
        void cursesComeLast() throws Exception {
            ItemObject onFloor = new ItemObject();
            ItemObject inPack = new ItemObject();
            Curse curse = curseWithModifiers("last", Map.of());
            registryHolding(curse);
            poke(player, "cave", levelHolding(onFloor));
            carrying(inPack);

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(onFloor, inPack, curse), visitOrder);
        }

        /**
         * The state the shipped fixtures leave the registry in, and the state a data-free unit test
         * runs in. Nothing to walk is not an error.
         */
        @Test
        @DisplayName("an empty registry visits no curse")
        void emptyRegistryVisitsNothing() throws Exception {
            registryHolding();
            carrying();

            assertDoesNotThrow(() -> PlayerKnowledge.updateObjectKnowledge(player));

            assertTrue(visitOrder.isEmpty());
        }

        /**
         * The visit is a transfer, not a pass. A curse whose only property is a modifier is not
         * fully known before the walk, because nothing has yet been written into its known
         * modifiers, and is afterwards once the player can read that modifier.
         *
         * <p>{@link Curse#isFullyKnown()} is the only public window onto the result — the
         * {@code known*} fields have setters but no getters — which is why this asserts on the
         * predicate rather than on the values.
         */
        @Test
        @DisplayName("a curse the player can read becomes fully known")
        void aReadableCurseBecomesFullyKnown() throws Exception {
            Curse curse = curseWithModifiers("sickliness", Map.of(ObjectModifier.OM_STR, -5));
            registryHolding(curse);
            player.itemKnowledge.learnModifier(ObjectModifier.OM_STR);
            carrying();

            assertFalse(curse.isFullyKnown());

            PlayerKnowledge.updateObjectKnowledge(player);

            assertTrue(curse.isFullyKnown());
        }

        /**
         * The other half of the pair, and the one that shows the transfer is gated on knowledge
         * rather than unconditional: the same curse, a player who cannot read the modifier, and the
         * walk leaves it as unknown as it found it. The known modifier is written — as a zero,
         * C's mask for a property the player cannot read — which is why this is not the same
         * assertion as the one before the walk above.
         */
        @Test
        @DisplayName("a curse the player cannot read stays unknown")
        void anUnreadableCurseStaysUnknown() throws Exception {
            Curse curse = curseWithModifiers("sickliness", Map.of(ObjectModifier.OM_STR, -5));
            registryHolding(curse);
            carrying();

            PlayerKnowledge.updateObjectKnowledge(player);

            assertFalse(curse.isFullyKnown());
        }

        /**
         * The signals are after all three loops, so a registry full of curses does not change how
         * many go out.
         */
        @Test
        @DisplayName("the events still fire once each after the curse walk")
        void eventsAreUnaffectedByTheCurseWalk() throws Exception {
            registryHolding(curseWithModifiers("one", Map.of()),
                    curseWithModifiers("two", Map.of()));
            carrying();

            PlayerKnowledge.updateObjectKnowledge(player);

            assertEquals(List.of(GameEventType.EVENT_INVENTORY, GameEventType.EVENT_EQUIPMENT),
                    bus.signalled);
        }
    }
}