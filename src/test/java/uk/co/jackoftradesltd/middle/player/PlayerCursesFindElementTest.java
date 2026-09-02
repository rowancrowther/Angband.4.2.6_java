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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

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
 * Tests {@link Player}'s {@code objectCursesFindElement}, the port of C's
 * {@code object_curses_find_element} ({@code src/obj-knowledge.c:1748}).
 *
 * <p>The C function is short but every clause of it is load-bearing, and the expected values here
 * are read off the C rather than off the port: a curse is examined only when its power is non-zero;
 * it counts only when the resistance level it imposes is {@code != 0}, so a vulnerability teaches
 * exactly as a resistance does; the "glows" message is printed only on the first discovery, while
 * the curse's own rune is learned on every hit; and the return value is the "did any curse mention
 * this element" answer its callers gate on.
 *
 * <p>Two of the cases exist because the port's data shape differs from C's. C reads
 * {@code curse->obj->el_info[elem]} out of an array of length {@code ELEM_MAX}, so the entry is
 * always there with a {@code res_level} of zero by default. {@link Curse#getElInfo()} is a map
 * carrying only the elements that curse's data lines actually name — and 24 of the curses in
 * {@code curse.txt}, {@code teleportation} and {@code dullness} among them, name none at all. The
 * absent entry has to behave as C's zero, so there is a case for a curse with no element data and a
 * case for a curse carrying a different element than the one asked about.
 *
 * <p>The method is private, as C's is {@code static}, so it is reached by reflection rather than
 * through {@code equipLearnElement}: the caller has branches of its own, and a test that went
 * through them would be reporting on two functions at once.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCursesFindElementTest {

    /**
     * The registry fields this suite overwrites. Saved and restored by reflection because they are
     * null until something loads them, which the accessors cannot report — the same note as in
     * {@code PlayerRuneLearningTest}.
     */
    private static final List<String> SAVED_FIELDS =
            List.of("curses", "allRunes", "curseMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    /**
     * A curse that resists fire — the ordinary case.
     */
    private static Curse fireResisting;

    /**
     * A curse that makes fire worse. C's test is {@code != 0}, so this teaches the same rune.
     */
    private static Curse fireVulnerable;

    /**
     * A curse carrying an explicit fire entry of zero, which C reads as "does not touch fire".
     */
    private static Curse fireNeutral;

    /**
     * A curse whose only element is cold, so a question about fire must pass it over.
     */
    private static Curse coldResisting;

    /**
     * A curse with no element data at all, as most of {@code curse.txt} has none.
     */
    private static Curse elementless;

    /**
     * A fire-resisting curse deliberately left out of the rune list, for C's {@code index < 0}.
     */
    private static Curse unruned;

    private Player player;
    private KnownObject knowledge;
    private CapturingBus bus;
    private EventsHandler realBus;

    /**
     * Seeds the registry with the six curses above and a rune for each one that should have one,
     * plus the fire resistance rune the learning resolves through. {@link #unruned} is left out of
     * the rune list on purpose; the resist rune for cold is left out too, since no case here learns
     * one.
     */
    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        fireResisting = curse("burning", Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
        fireVulnerable = curse("vulnerability", Map.of(ElementEnum.ELEM_FIRE, elementInfo(-1)));
        fireNeutral = curse("neutral", Map.of(ElementEnum.ELEM_FIRE, elementInfo(0)));
        coldResisting = curse("chilled", Map.of(ElementEnum.ELEM_COLD, elementInfo(1)));
        elementless = curse("teleportation", Map.of());
        unruned = curse("unruned", Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));

        ObjectRegistry.setCurses(List.of(fireResisting, fireVulnerable, fireNeutral, coldResisting,
                elementless, unruned));

        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE,
                        projection(ProjectionEnum.PROJ_FIRE, "fire"))),
                new Rune(new RuneVariety.CurseKey(fireResisting)),
                new Rune(new RuneVariety.CurseKey(fireVulnerable)),
                new Rune(new RuneVariety.CurseKey(fireNeutral)),
                new Rune(new RuneVariety.CurseKey(coldResisting)),
                new Rune(new RuneVariety.CurseKey(elementless)))));
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    /**
     * A curse carrying nothing but a name and a set of element figures. Everything else a curse can
     * hold — effects, modifiers, combat figures, conflicts — is irrelevant to the element search,
     * and leaving it empty keeps a failure here about the element search.
     *
     * @param name   the curse's name, which is what the rune lookup matches on
     * @param elInfo the element figures, as sparse as the parser would leave them
     */
    private static Curse curse(String name, Map<ElementEnum, ElementInfo> elInfo) {
        return new Curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), elInfo,
                0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), name, "The curse fires.");
    }

    /**
     * A projection carrying only its name. An element is named through its projection rather than
     * off the element tag, so a resist rune needs one before it can announce itself — the rune
     * message is half of what these tests assert on.
     */
    private static Projection projection(ProjectionEnum code, String name) {
        return new Projection(code, name, null, null, null, null, null, 1, 1, 1, 0, null, false,
                false, null);
    }

    /**
     * An {@link ElementInfo} at the given resistance level, standing in for one {@code RES_} token
     * on a curse's {@code values:} line.
     */
    private static ElementInfo elementInfo(int resLevel) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        return info;
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
     * would fill in — {@link ItemObject}'s no-argument constructor leaves the curse map null, where
     * a parsed item would always have one.
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
    private static ItemObject itemWith(Map.Entry<Curse, CurseData>... entries) throws Exception {
        ItemObject item = new ItemObject();
        Map<Curse, CurseData> curses = new LinkedHashMap<>();
        for (Map.Entry<Curse, CurseData> entry : entries) {
            curses.put(entry.getKey(), entry.getValue());
        }
        poke(item, "curses", curses);
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
     * Empties {@link Message}'s log, which is static and outlives a test. C's {@code message_add}
     * bumps a count in place when the newest entry repeats and the port decorates the text with
     * {@code (x2)}, so a log left dirty by an earlier class makes assertions depend on run order.
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
     * Calls the method under test. It is private on {@link PlayerKnowledge}, as C's is
     * {@code static} in {@code obj-knowledge.c}, so reflection is how a test reaches it; the cause
     * of any exception thrown inside is unwrapped so a failure reads as itself rather than as an
     * {@link InvocationTargetException}.
     *
     * <p>It is now static in the port too, with the player as its first argument rather than as
     * {@code this} — hence the {@code null} receiver and the three-argument call.
     */
    private boolean findElement(ItemObject item, ElementEnum elem) throws Exception {
        Method method = PlayerKnowledge.class.getDeclaredMethod("objectCursesFindElement",
                Player.class, ItemObject.class, ElementEnum.class);
        method.setAccessible(true);
        try {
            return (boolean) method.invoke(null, player, item, elem);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    private List<String> announced() {
        return bus.messages.stream().map(EventDataMessage::message).toList();
    }

    /**
     * How many times the item was said to glow. The learning also announces the runes it learns, so
     * a count of everything would not say what a test about the glow wants to know.
     */
    private long glows() {
        return announced().stream().filter(m -> m.startsWith("Your {DESCRIPTION_TAG} glows.")).count();
    }

    @BeforeEach
    void setUp() throws Exception {
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
     * The path C takes when a curse does move the resistance: message, resist rune, curse rune,
     * true.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a curse that touches the element")
    class Found {

        @Test
        @DisplayName("teaches the resistance and the curse, and says so")
        void ordinary() throws Exception {
            ItemObject item = itemWith(cursed(fireResisting, 40));

            boolean found = findElement(item, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertTrue(found, "the element was on a curse"),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE),
                            "the resistance rune is learned"),
                    () -> assertTrue(knowledge.curseIsKnown(fireResisting),
                            "the curse's own rune is learned"),
                    () -> assertEquals(1, glows(), "the item glows exactly once"));
        }

        @Test
        @DisplayName("a vulnerability teaches as a resistance does")
        void vulnerability() throws Exception {
            // C tests res_level != 0, not res_level > 0. The rune names the element, not the
            // direction, so a curse that makes fire worse teaches the fire rune just the same.
            ItemObject item = itemWith(cursed(fireVulnerable, 40));

            boolean found = findElement(item, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertTrue(found),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(knowledge.curseIsKnown(fireVulnerable)),
                    () -> assertEquals(1, glows()));
        }

        @Test
        @DisplayName("an already-known resistance is not announced again, but the curse is learned")
        void alreadyKnown() throws Exception {
            // C guards only the message and the resist rune on !p->obj_k->el_info[elem].res_level.
            // The curse rune is learned unconditionally, and the return is still true.
            knowledge.learnResistance(ElementEnum.ELEM_FIRE);
            clearMessageLog();
            bus.messages.clear();
            ItemObject item = itemWith(cursed(fireResisting, 40));

            boolean found = findElement(item, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertTrue(found, "the element was still on a curse"),
                    () -> assertEquals(0, glows(), "nothing was discovered, so nothing glows"),
                    () -> assertTrue(knowledge.curseIsKnown(fireResisting),
                            "the curse is learned even so"));
        }

        @Test
        @DisplayName("two matching curses glow once and are both learned")
        void twoCurses() throws Exception {
            // The first hit learns the resistance, so the second finds it already known and stays
            // quiet — the same sequence C produces from its per-curse guard.
            ItemObject item = itemWith(cursed(fireResisting, 40), cursed(fireVulnerable, 20));

            boolean found = findElement(item, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertTrue(found),
                    () -> assertEquals(1, glows(), "the discovery is announced once, not per curse"),
                    () -> assertTrue(knowledge.curseIsKnown(fireResisting)),
                    () -> assertTrue(knowledge.curseIsKnown(fireVulnerable)));
        }

        @Test
        @DisplayName("a curse with no rune still teaches the resistance")
        void curseWithoutRune() throws Exception {
            // C's rune_index returns -1 and the "if (index >= 0)" guard skips learning the curse.
            // The port reaches the same place through a null rune that learnRune declines.
            ItemObject item = itemWith(cursed(unruned, 40));

            boolean found = assertDoesNotThrow(() -> findElement(item, ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertTrue(found, "the element was on the curse regardless"),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(knowledge.curseIsKnown(unruned),
                            "there is no rune for it to learn"));
        }
    }

    /**
     * The paths C takes when nothing is found: an inert curse, a zero resistance level, an element
     * the curse never mentions, and an item with no curses at all. All four must leave the knowledge
     * untouched, say nothing, and return false.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("a curse that does not touch the element")
    class NotFound {

        @Test
        @DisplayName("a curse of zero power is skipped before its elements are read")
        void zeroPower() throws Exception {
            ItemObject item = itemWith(cursed(fireResisting, 0));

            boolean found = findElement(item, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertFalse(found),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(knowledge.curseIsKnown(fireResisting)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("an explicit resistance level of zero does not count")
        void zeroResistLevel() throws Exception {
            ItemObject item = itemWith(cursed(fireNeutral, 40));

            boolean found = findElement(item, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertFalse(found),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(knowledge.curseIsKnown(fireNeutral)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("a curse with no element data at all is passed over")
        void noElementData() throws Exception {
            // C reads el_info[ELEM_FIRE].res_level out of a full array and finds zero. The port's
            // map has no entry to read, which has to mean the same thing — 24 curses in curse.txt
            // carry no element data, so this is the common case rather than an edge one.
            ItemObject item = itemWith(cursed(elementless, 40));

            boolean found = assertDoesNotThrow(() -> findElement(item, ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertFalse(found),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(knowledge.curseIsKnown(elementless)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("a curse naming another element is passed over")
        void differentElement() throws Exception {
            // The map is sparse per element, not merely per curse: a curse with only a cold entry
            // has nothing to read when asked about fire.
            ItemObject item = itemWith(cursed(coldResisting, 40));

            boolean found = assertDoesNotThrow(() -> findElement(item, ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertFalse(found),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(knowledge.curseIsKnown(coldResisting)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("the cold question the same curse does answer")
        void differentElementFound() throws Exception {
            // The mirror of the case above, so that its false is about the element asked for and not
            // about the curse being unreadable. No cold resist rune is registered, so the resistance
            // itself cannot be learned — but the curse rune and the return value still land.
            ItemObject item = itemWith(cursed(coldResisting, 40));

            boolean found = findElement(item, ElementEnum.ELEM_COLD);

            assertAll(
                    () -> assertTrue(found),
                    () -> assertTrue(knowledge.curseIsKnown(coldResisting)));
        }

        @Test
        @DisplayName("an uncursed item finds nothing")
        void noCurses() throws Exception {
            // getCurses() answers an empty map for an item whose backing map was never built, which
            // is what C's null obj->curses means.
            ItemObject item = new ItemObject();

            boolean found = assertDoesNotThrow(() -> findElement(item, ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertFalse(found),
                    () -> assertEquals(List.of(), announced()));
        }
    }
}
