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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge}'s {@code cursesFindModifiers}, the port of C's
 * {@code object_curses_find_modifiers} ({@code obj-knowledge.c}).
 *
 * <p>The expected values are read off the C. A curse is examined only if the item carries it at a
 * non-zero power; each modifier the curse <em>definition</em> sets to a non-zero value is then
 * learned if the player cannot yet read it, with {@code mod_message} said only while playing; and
 * the curse's own rune is learned for every such modifier, whether or not the modifier was new.
 *
 * <p>Three of the cases exist because the port's data shape differs from C's. C walks a dense
 * array, so a curse the item does not carry is a slot of power zero and is skipped; the port's
 * {@link ItemObject#getCurses()} is a map with no entry for it, so {@code get} answers
 * {@code null}, and the absent curse has to be skipped the same way - without throwing, and without
 * learning the modifiers of a curse the item never had. A curse's modifier map holds only the
 * modifiers it names, so an absent modifier has to read as C's zero.
 *
 * <p>The method is private, as C's is {@code static}, so it is reached by reflection rather than
 * through {@code objectLearnOnWield}: the caller has branches of its own, and a test that went
 * through them would be reporting on two functions at once.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCursesFindModifiersTest {

    /**
     * The registry fields this suite overwrites, saved and restored by reflection because they are
     * null until something loads them - the same note as in {@code PlayerCursesFindElementTest}.
     */
    private static final List<String> SAVED_FIELDS =
            List.of("curses", "allRunes", "curseMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    /**
     * A curse that lowers strength - the ordinary case.
     */
    private static Curse weakness;

    /**
     * A second curse that lowers strength, so two carried curses can name the same modifier.
     */
    private static Curse feebleness;

    /**
     * A curse with two modifiers, to show every nonzero modifier is learned in one pass.
     */
    private static Curse clumsiness;

    /**
     * A curse with no modifier data at all, as most of {@code curse.txt} has none.
     */
    private static Curse teleportation;

    /**
     * A curse carrying an explicit strength entry of zero, which C reads as "does not touch it".
     */
    private static Curse neutral;

    /**
     * A strength-lowering curse deliberately left out of the rune list, for C's {@code index < 0}.
     */
    private static Curse unruned;

    private Player player;
    private KnownObject knowledge;
    private CapturingBus bus;
    private EventsHandler realBus;

    /**
     * Builds the six curses above and saves the registry fields this suite overwrites.
     * {@link #seedRegistry()} installs them, with a rune for each curse that should have one plus
     * the strength and dexterity modifier runes; {@link #unruned} is left out of the rune list on
     * purpose.
     */
    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        weakness = curse("weakness", Map.of(ObjectModifier.OM_STR, -3), 1);
        feebleness = curse("feebleness", Map.of(ObjectModifier.OM_STR, -1), 2);
        clumsiness = curse("clumsiness",
                Map.of(ObjectModifier.OM_STR, -2, ObjectModifier.OM_DEX, -2), 3);
        teleportation = curse("teleportation", Map.of(), 4);
        neutral = curse("neutral", Map.of(ObjectModifier.OM_STR, 0), 5);
        unruned = curse("unruned", Map.of(ObjectModifier.OM_STR, -1), 6);
    }

    /**
     * Puts the six curses and their runes into the registry. Done before every test rather than
     * once, because {@link SeededPlayerRegistry} is inherited by each {@code @Nested} class and
     * empties the curse list when the first of them finishes - a registry seeded only in
     * {@link #seed()} is gone by the time the second class runs.
     */
    private static void seedRegistry() {
        ObjectRegistry.setCurses(List.of(weakness, feebleness, clumsiness, teleportation, neutral,
                unruned));

        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, property("strength"))),
                new Rune(new RuneVariety.ModKey(ObjectModifier.OM_DEX, property("dexterity"))),
                new Rune(new RuneVariety.CurseKey(weakness)),
                new Rune(new RuneVariety.CurseKey(feebleness)),
                new Rune(new RuneVariety.CurseKey(clumsiness)),
                new Rune(new RuneVariety.CurseKey(teleportation)),
                new Rune(new RuneVariety.CurseKey(neutral)))));
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    /**
     * A curse carrying nothing but a name and a set of modifier figures. Everything else a curse
     * can hold is irrelevant to the modifier search, and leaving it empty keeps a failure here
     * about the modifier search.
     *
     * @param name      the curse's name, which is what the rune lookup matches on
     * @param modifiers the modifier figures, as sparse as the parser would leave them
     * @param index     the curse's index in {@code curse.txt}, which orders an item's curse map
     */
    private static Curse curse(String name, Map<ObjectModifier, Integer> modifiers, int index) {
        return CurseFixture.curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), modifiers,
                Map.<ElementEnum, ElementInfo>of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class),
                name, "The curse fires.", index);
    }

    /**
     * A property carrying only its name, which is all a rune's announcement reads from it.
     */
    private static ObjectProperty property(String name) {
        return new ObjectProperty(null, null, null, null, 0, 0, null, name, null, null, null, null,
                null);
    }

    private static Field field(String name) throws NoSuchFieldException {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void set(Player target, String name, Object value) throws Exception {
        Field f = Player.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Writes a field on anything, for the state that only the machinery this suite does not run
     * would fill in.
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * An item carrying the given curses at the given powers, in order, and nothing else.
     *
     * @param entries the curses paired with their power on this item
     */
    @SafeVarargs
    private static ItemObject itemWith(Map.Entry<Curse, CurseData>... entries) {
        ItemObject item = new ItemObject();
        Map<Curse, CurseData> curses = new LinkedHashMap<>();
        for (Map.Entry<Curse, CurseData> entry : entries) {
            curses.put(entry.getKey(), entry.getValue());
        }
        item.clearAndPutCurses(curses);
        return item;
    }

    /**
     * A curse on an item at a given power. The timeout is always zero: no test here advances a
     * turn, and the figure the search gates on is the power.
     */
    private static Map.Entry<Curse, CurseData> cursed(Curse curse, int power) {
        return Map.entry(curse, new CurseData(power, 0));
    }

    /**
     * Empties {@link Message}'s log, which is static and outlives a test, so an assertion does not
     * depend on run order.
     */
    @SuppressWarnings("unchecked")
    private static void clearMessageLog() {
        try {
            Field field = Message.class.getDeclaredField("messageLog");
            field.setAccessible(true);
            ((java.util.Deque<Object>) field.get(null)).clear();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Message.messageLog is no longer reachable by reflection", e);
        }
    }

    /**
     * An item carrying one curse at power 40 and the strength penalty that curse stands for, so
     * {@code mod_message}, which reads the item's own figure, has a sign to word itself from.
     */
    private static ItemObject weakItem(Curse curse) {
        ItemObject item = itemWith(cursed(curse, 40));
        item.putModifier(ObjectModifier.OM_STR, -3);
        return item;
    }

    /**
     * Calls the method under test. It is private on {@link PlayerKnowledge}, as C's is
     * {@code static}, so reflection is how a test reaches it; the cause of any exception thrown
     * inside is unwrapped so a failure reads as itself rather than as an
     * {@link InvocationTargetException}.
     */
    private void findModifiers(ItemObject item) throws Exception {
        Method method = PlayerKnowledge.class.getDeclaredMethod("cursesFindModifiers",
                Player.class, ItemObject.class);
        method.setAccessible(true);
        try {
            method.invoke(null, player, item);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    private List<String> announced() {
        return bus.messages.stream().map(EventDataMessage::message).toList();
    }

    /**
     * Puts the player in a started game, which is what {@code p->upkeep->playing} reports. It is
     * false on a fresh {@link PlayerUpkeep}, and the method gates {@code mod_message} on it.
     */
    private void startPlaying() throws Exception {
        poke(player.getPlayerUpkeep(), "playing", true);
    }

    @BeforeEach
    void setUp() throws Exception {
        seedRegistry();
        player = new Player();
        knowledge = new KnownObject();
        set(player, "itemKnowledge", knowledge);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
        clearMessageLog();
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Catches what the learning announces. {@link Message} reaches the bus through
     * {@link GameEngine#getEventsBusHandler()}, which is static, so the real one is put back after
     * each test.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<EventDataMessage> messages = new ArrayList<>();

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
            if (eventType != GameEventType.EVENT_MESSAGE) return;

            assertInstanceOf(EventDataMessage.class, data);
            messages.add((EventDataMessage) data);
        }
    }

    /**
     * The paths C takes when a carried curse confers a modifier: the modifier rune, the message,
     * the curse rune.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a carried curse that confers a modifier")
    class Found {

        @Test
        @DisplayName("teaches the modifier and the curse, and says so while playing")
        void ordinary() throws Exception {
            startPlaying();
            ItemObject item = weakItem(weakness);

            findModifiers(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR),
                            "the modifier rune is learned"),
                    () -> assertTrue(knowledge.curseIsKnown(weakness),
                            "the curse's own rune is learned"),
                    () -> assertTrue(announced().contains("You feel weaker!"),
                            "mod_message is said while playing"));
        }

        @Test
        @DisplayName("learns silently when the game is not being played")
        void notPlaying() throws Exception {
            // C guards only mod_message on p->upkeep->playing; the runes are learned regardless.
            ItemObject item = weakItem(weakness);

            findModifiers(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertTrue(knowledge.curseIsKnown(weakness)),
                    () -> assertFalse(announced().contains("You feel weaker!"),
                            "no mod_message outside play"));
        }

        @Test
        @DisplayName("an already-known modifier is not announced again, but the curse is learned")
        void alreadyKnown() throws Exception {
            // C's !p->obj_k->modifiers[j] guards the rune and the message together. The curse rune
            // sits outside it and is learned on every hit.
            startPlaying();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            clearMessageLog();
            bus.messages.clear();
            ItemObject item = weakItem(weakness);

            findModifiers(item);

            assertAll(
                    () -> assertFalse(announced().contains("You feel weaker!"),
                            "nothing new was learned, so nothing is said"),
                    () -> assertTrue(knowledge.curseIsKnown(weakness),
                            "the curse is learned even so"));
        }

        @Test
        @DisplayName("every nonzero modifier on the curse is learned in one pass")
        void twoModifiers() throws Exception {
            // C's j loop runs the whole modifier array, so one curse can teach several.
            ItemObject item = itemWith(cursed(clumsiness, 40));

            findModifiers(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_DEX)),
                    () -> assertTrue(knowledge.curseIsKnown(clumsiness)));
        }

        @Test
        @DisplayName("two carried curses naming the same modifier teach it once and learn both curses")
        void twoCurses() throws Exception {
            startPlaying();
            ItemObject item = itemWith(cursed(weakness, 40), cursed(feebleness, 20));
            item.putModifier(ObjectModifier.OM_STR, -4);

            findModifiers(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertEquals(1, announced().stream()
                                    .filter(m -> m.equals("You feel weaker!")).count(),
                            "the first curse teaches it, so the second finds it known"),
                    () -> assertTrue(knowledge.curseIsKnown(weakness)),
                    () -> assertTrue(knowledge.curseIsKnown(feebleness)));
        }

        @Test
        @DisplayName("the figure comes from the curse definition, not from the item")
        void figureFromCurse() throws Exception {
            // C reads curses[i].obj->modifiers[j]. An item with no strength figure of its own still
            // teaches the rune; mod_message reads the item's figure, finds zero and stays quiet.
            startPlaying();
            ItemObject item = itemWith(cursed(weakness, 40));

            findModifiers(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertTrue(knowledge.curseIsKnown(weakness)),
                    () -> assertFalse(announced().contains("You feel weaker!")));
        }

        @Test
        @DisplayName("a curse with no rune still teaches the modifier")
        void curseWithoutRune() throws Exception {
            // C's rune_index returns -1 and "if (index >= 0)" skips learning the curse. The port
            // reaches the same place through a null Rune it declines to learn.
            ItemObject item = itemWith(cursed(unruned, 40));

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertFalse(knowledge.curseIsKnown(unruned),
                            "there is no rune for it to learn"));
        }
    }

    /**
     * The paths C takes when nothing is found: a curse of zero power, a curse the item does not
     * carry, a curse with no modifier data, a zero modifier, and an item with no curses at all. All
     * must leave the knowledge untouched and say nothing.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("nothing to learn")
    class NotFound {

        @Test
        @DisplayName("a curse of zero power is skipped before its modifiers are read")
        void zeroPower() throws Exception {
            ItemObject item = itemWith(cursed(weakness, 0));

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertFalse(knowledge.curseIsKnown(weakness)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("a registry curse the item does not carry is skipped, and does not throw")
        void absentCurse() throws Exception {
            // C reads a power of zero out of a dense array and takes the continue. The port's map
            // has no entry, so get() is null: the item carries only teleportation, which has no
            // modifiers, while weakness, clumsiness and the rest are registry curses it never had.
            // Their modifiers must not be learned.
            ItemObject item = itemWith(cursed(teleportation, 40));

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR),
                            "weakness is not on the item"),
                    () -> assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_DEX),
                            "clumsiness is not on the item"),
                    () -> assertFalse(knowledge.curseIsKnown(weakness)),
                    () -> assertFalse(knowledge.curseIsKnown(clumsiness)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("an absent curse next to a carried one is skipped while the carried one teaches")
        void absentAndCarried() throws Exception {
            // weakness is carried; feebleness and clumsiness, which follow it, are not. The walk
            // must carry on past the absent entries rather than stop or throw at the first.
            ItemObject item = itemWith(cursed(weakness, 40));

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertTrue(knowledge.curseIsKnown(weakness)),
                    () -> assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_DEX),
                            "dexterity belongs to clumsiness, which is not carried"),
                    () -> assertFalse(knowledge.curseIsKnown(feebleness)),
                    () -> assertFalse(knowledge.curseIsKnown(clumsiness)));
        }

        @Test
        @DisplayName("a curse with no modifier data at all is passed over")
        void noModifierData() throws Exception {
            // C reads modifiers[j] out of a full array and finds zero. The port's map has no entry
            // to read, which has to mean the same thing.
            ItemObject item = itemWith(cursed(teleportation, 40));

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertFalse(knowledge.curseIsKnown(teleportation),
                            "no modifier, so nothing ties the curse to a find"),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("an explicit modifier of zero does not count")
        void zeroModifier() throws Exception {
            ItemObject item = itemWith(cursed(neutral, 40));

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertFalse(knowledge.curseIsKnown(neutral)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("an uncursed item finds nothing")
        void noCurses() throws Exception {
            // getCurses() answers an empty map for an item whose backing map was never built, which
            // is what C's null obj->curses means.
            ItemObject item = new ItemObject();

            assertDoesNotThrow(() -> findModifiers(item));

            assertAll(
                    () -> assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertEquals(List.of(), announced()));
        }
    }
}
