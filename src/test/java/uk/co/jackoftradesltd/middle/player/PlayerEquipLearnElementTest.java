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
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge#equipLearnElement}, the port of C's {@code equip_learn_element}
 * ({@code src/obj-knowledge.c:2155}).
 *
 * <p>The expected values are read off the C rather than off the port. The clauses that carry weight
 * there are: the two bounds returns; the "already known" return, which stops the walk before any
 * item is touched; the {@code res_level != 0} test, so a vulnerability teaches exactly as a
 * resistance does; the message and rune in the affecting branch, which are <em>not</em> guarded on
 * what the player already knows and so repeat per item, unlike the guarded pair in
 * {@code object_curses_find_element}; the else branch marking a not-fully-known item as having had
 * its chance; and the curse search, which runs for every item whichever branch was taken.
 *
 * <p>Two cases exist because the port's data shape differs from C's. C reads
 * {@code obj->el_info[element]} out of an array of length {@code ELEM_MAX} ({@code object.h:451}),
 * so an element the object's data line never mentioned still reads back as a zero resistance level
 * and an empty flag set. {@link ItemObject#getElInfo()} is a map holding only the elements that
 * item actually names — {@code ItemObjectAssembler} builds it that way — so there is a case for an
 * item carrying an explicit zero and a case for an item carrying no entry for the element at all,
 * and both have to land where C's zero lands.
 *
 * <p>Items are placed straight into {@link PlayerBody}'s slots rather than through the wield code,
 * and the player's {@code gear} and {@code cave} are left null: the rune learning ends in
 * {@code updateObjectKnowledge}, which walks both populations, and a walk over the same items would
 * rewrite the very known counterparts these tests assert on.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerEquipLearnElementTest {

    /**
     * The registry fields this suite overwrites, saved and restored around the class.
     */
    private static final List<String> SAVED_FIELDS = List.of("allRunes", "curses", "curseMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    /**
     * A curse that resists fire, for the case proving the curse search runs on every item.
     */
    private static Curse fireResisting;

    private Player player;
    private KnownObject knowledge;
    private PlayerBody body;
    private CapturingBus bus;
    private EventsHandler realBus;

    /**
     * Seeds the registry with resistance runes for fire and cold, and one curse with a rune of its
     * own. Cold is present so that a test can ask about an element the items do not carry without
     * the answer being "there was no rune to learn anyway".
     */
    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        fireResisting = new Curse("burning", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)), 0, 0, 0, List.of(),
                new Flag<>(ObjectFlag.class), "burning", "The curse fires.");

        ObjectRegistry.setCurses(List.of(fireResisting));

        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE,
                        projection(ProjectionEnum.PROJ_FIRE, "fire"))),
                new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_COLD,
                        projection(ProjectionEnum.PROJ_COLD, "cold"))),
                new Rune(new RuneVariety.CurseKey(fireResisting)))));
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    private static Projection projection(ProjectionEnum code, String name) {
        return new Projection(code, name, null, null, null, null, null, 1, 1, 1, 0, null, false,
                false, null);
    }

    /**
     * An {@link ElementInfo} at the given resistance level and nothing else.
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

    /**
     * Writes a private field on any object, for state no constructor here can reach.
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Builds an item with a known counterpart, the given element figures, and nothing else.
     *
     * <p>The counterpart's element map starts empty, which is what a player who has learned nothing
     * about the item has. The two objects are otherwise identical, so
     * {@link ItemObject#isFullyKnown()} answers true unless {@link #makeUnknown} is used to drive a
     * wedge between them.
     *
     * @param elInfo the element figures, as sparse as the parser would leave them
     */
    private static ItemObject item(Map<ElementEnum, ElementInfo> elInfo) throws Exception {
        ItemObject item = new ItemObject();
        ItemObject known = new ItemObject();
        poke(item, "elInfo", new LinkedHashMap<>(elInfo));
        poke(known, "elInfo", new LinkedHashMap<ElementEnum, ElementInfo>());
        poke(item, "flags", new Flag<>(ObjectFlag.class));
        poke(known, "flags", new Flag<>(ObjectFlag.class));
        poke(item, "effect", new ArrayList<Effect>());
        poke(known, "effect", new ArrayList<Effect>());
        poke(item, "known", known);
        return item;
    }

    /**
     * Brings an item's counterpart level with the item, so that {@link ItemObject#isFullyKnown()}
     * answers true. Only the element figures need copying: everything else on a fixture built by
     * {@link #item} is already identical on both sides, and {@code nonCurseRunesKnown} rejects an
     * element the counterpart has no entry for ({@code Player.java:853}).
     */
    private static void makeFullyKnown(ItemObject item) throws Exception {
        Map<ElementEnum, ElementInfo> known = new LinkedHashMap<>();
        for (Map.Entry<ElementEnum, ElementInfo> entry : item.getElInfo().entrySet()) {
            known.put(entry.getKey(), entry.getValue().copy());
        }
        poke(item.getKnown(), "elInfo", known);
    }

    /**
     * Makes an item not fully known, by way of a to-armour bonus its counterpart has not learned.
     * {@code nonCurseRunesKnown} compares the two figures first ({@code Player.java:834}), so this
     * is the shortest lever that does not disturb the element figures the tests are about.
     */
    private static void makeUnknown(ItemObject item) throws Exception {
        poke(item, "toAC", 5);
    }

    /**
     * Hangs a curse on an item at the given power.
     */
    private static void curse(ItemObject item, Curse curse, int power) throws Exception {
        Map<Curse, CurseData> curses = new LinkedHashMap<>();
        curses.put(curse, new CurseData(power, 0));
        poke(item, "curses", curses);
    }

    private static void set(Player target, String name, Object value) throws Exception {
        Field f = Player.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * The resistance level recorded on an item's known counterpart, or null if none is.
     */
    private static Integer knownResLevel(ItemObject item, ElementEnum elem) {
        ElementInfo info = item.getKnown().getElInfo().get(elem);
        return info == null ? null : info.getResLevel();
    }

    /**
     * Empties {@link Message}'s static log, which outlives a test and would otherwise make the
     * repeat-count decoration depend on run order.
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
     * Puts items into consecutive body slots, in the order given, and adds one empty slot after
     * them. The empty slot is C's {@code slot_object} answering null, which the walk has to step
     * over; it also keeps {@link PlayerBody} constructible when no items are given at all, since it
     * refuses an empty slot list ({@code PlayerBody.java:71-74}).
     */
    private void equip(ItemObject... items) throws Exception {
        List<EquipSlot> slots = new ArrayList<>();
        for (ItemObject each : items) {
            EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "on your body");
            poke(slot, "item", each);
            slots.add(slot);
        }
        slots.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_CLOAK, "on your back"));
        body = new PlayerBody("Humanoid", slots);
        set(player, "body", body);
    }

    private List<String> announced() {
        return bus.messages.stream().map(EventDataMessage::message).toList();
    }

    /**
     * How many times something was said to glow. The rune learning announces the runes it learns
     * too, so a count of everything would not say what a test about the glow wants to know.
     *
     * <p>Matched loosely because the two glow messages name the item by different routes.
     * {@code equipLearnElement} goes through {@code ObjectUtils.objectDesc}, a stub answering the
     * empty string; {@code objectCursesFindElement} goes through {@link ItemObject#description},
     * which answers a placeholder tag. C reaches {@code object_desc} in both places, so this is a
     * difference in how far the port's two naming paths have got rather than one in behaviour, and
     * a test that pinned either exact string would fail the day the stub is filled in.
     */
    private long glows() {
        return announced().stream().filter(m -> m.matches("Your .*glows\\..*")).count();
    }

    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        knowledge = new KnownObject();
        set(player, "itemKnowledge", knowledge);
        equip();

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
     * Catches what the learning announces, in place of the real bus, which is reached statically
     * through {@link GameEngine} and so is put back after every test.
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
     * The two returns that come before any item is looked at.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the guards")
    class Guards {

        @Test
        @DisplayName("a sentinel element is refused, as C refuses one out of bounds")
        void sentinels() throws Exception {
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            makeUnknown(armour);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_NONE);
            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_MAX);

            assertAll(
                    () -> assertEquals(List.of(), announced(), "nothing was announced"),
                    () -> assertNull(knownResLevel(armour, ElementEnum.ELEM_FIRE),
                            "the item was never looked at"));
        }

        @Test
        @DisplayName("an already-known resistance stops before the walk begins")
        void alreadyKnown() throws Exception {
            // C returns on p->obj_k->el_info[element].res_level == 1, so the else branch that would
            // have marked this item never runs. That is the point of the case: the return is before
            // the loop, not inside it.
            ItemObject armour = item(Map.of());
            makeUnknown(armour);
            equip(armour);
            knowledge.learnResistance(ElementEnum.ELEM_FIRE);
            bus.messages.clear();
            clearMessageLog();

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(List.of(), announced()),
                    () -> assertNull(knownResLevel(armour, ElementEnum.ELEM_FIRE),
                            "the item was not marked"));
        }

        @Test
        @DisplayName("a body wearing nothing is walked without incident")
        void noItems() {
            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(List.of(), announced()),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)));
        }

        @Test
        @DisplayName("an item with no known counterpart is skipped")
        void noKnownCounterpart() throws Exception {
            // C asserts obj->known here; the port skips instead. Either way the item contributes
            // nothing, and the walk carries on to the next slot.
            ItemObject bare = new ItemObject();
            poke(bare, "elInfo", new LinkedHashMap<>(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1))));
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            equip(bare, armour);

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertEquals(1, glows(), "only the item with a counterpart glowed"),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)));
        }
    }

    /**
     * The branch C takes when the item moves the resistance level: message, then rune.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("an item that affects the resistance")
    class Affecting {

        @Test
        @DisplayName("glows and teaches the resistance rune")
        void ordinary() throws Exception {
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(1, glows(), "the item glows once"),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE),
                            "the resistance rune is learned"));
        }

        @Test
        @DisplayName("a vulnerability teaches as a resistance does")
        void vulnerability() throws Exception {
            // C tests res_level != 0, not > 0: the rune names the element, not the direction.
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(-1)));
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(1, glows()),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)));
        }

        @Test
        @DisplayName("an immunity teaches as a resistance does")
        void immunity() throws Exception {
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(3)));
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(1, glows()),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)));
        }

        @Test
        @DisplayName("two affecting items glow twice, though only the first teaches anything")
        void twoItems() throws Exception {
            // Unlike object_curses_find_element, C guards neither the message nor the rune call on
            // what the player already knows: msg() and player_learn_rune() run per item. The second
            // learn is a no-op inside the rune code, but the second glow reaches the player.
            ItemObject first = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            ItemObject second = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            equip(first, second);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(2, glows(), "each item announces itself"),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)));
        }

        @Test
        @DisplayName("the known counterpart is not marked by this branch")
        void counterpartUntouched() throws Exception {
            // The res_level = 1 write belongs to the else branch alone. What records the resistance
            // on the counterpart here is the rune learning, which is a different mechanism.
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            makeUnknown(armour);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertNull(knownResLevel(armour, ElementEnum.ELEM_FIRE),
                    "the else branch did not run for this item");
        }
    }

    /**
     * The branch C takes when the item does not move the resistance level: a not-fully-known item is
     * marked as having had its chance to show the property, and a fully known one is left alone.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("an item that does not affect the resistance")
    class NotAffecting {

        @Test
        @DisplayName("an explicit zero marks the counterpart and says nothing")
        void explicitZero() throws Exception {
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(0)));
            makeUnknown(armour);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(List.of(), announced(), "nothing glows"),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(1, knownResLevel(armour, ElementEnum.ELEM_FIRE),
                            "the counterpart records that the chance was had"));
        }

        @Test
        @DisplayName("no entry for the element lands where C's zero lands")
        void absentEntry() throws Exception {
            // C reads el_info[ELEM_FIRE] out of a full array and finds a zero. The port's map has no
            // entry to read, and that has to mean the same thing — a sparse map is the normal shape
            // for a parsed item, not an edge case.
            ItemObject armour = item(Map.of(ElementEnum.ELEM_COLD, elementInfo(1)));
            makeUnknown(armour);
            equip(armour);

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE));

            assertAll(
                    () -> assertEquals(List.of(), announced()),
                    () -> assertEquals(1, knownResLevel(armour, ElementEnum.ELEM_FIRE)));
        }

        @Test
        @DisplayName("an item with no element data at all lands there too")
        void noElementData() throws Exception {
            ItemObject armour = item(Map.of());
            makeUnknown(armour);
            equip(armour);

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE));

            assertEquals(1, knownResLevel(armour, ElementEnum.ELEM_FIRE));
        }

        @Test
        @DisplayName("the element's own flags are copied onto the counterpart")
        void flagsCopied() throws Exception {
            // C assigns obj->known->el_info[e].flags = obj->el_info[e].flags alongside the level.
            // The hates/ignores figures are what the player has just had a chance to see burn or not
            // burn, so they travel with it.
            ElementInfo hates = elementInfo(0);
            hates.getFlags().on(ElementInfoEnum.EL_INFO_HATES);
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, hates));
            makeUnknown(armour);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertTrue(armour.getKnown().getElInfo().get(ElementEnum.ELEM_FIRE).getFlags()
                            .has(ElementInfoEnum.EL_INFO_HATES),
                    "the counterpart carries the flag the item does");
        }

        @Test
        @DisplayName("an absent entry leaves the counterpart's flags empty, as C's zero would")
        void absentEntryLeavesFlagsEmpty() throws Exception {
            ItemObject armour = item(Map.of());
            makeUnknown(armour);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertFalse(armour.getKnown().getElInfo().get(ElementEnum.ELEM_FIRE).getFlags()
                            .has(ElementInfoEnum.EL_INFO_HATES),
                    "there was nothing to copy, so nothing was set");
        }

        @Test
        @DisplayName("a fully known item is left alone")
        void fullyKnown() throws Exception {
            // C's "else if (!object_fully_known(obj))" — there is nothing to record about an item
            // the player already reads completely.
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(0)));
            makeFullyKnown(armour);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertTrue(armour.isFullyKnown(), "the fixture is fully known"),
                    () -> assertEquals(List.of(), announced()),
                    () -> assertEquals(0, knownResLevel(armour, ElementEnum.ELEM_FIRE),
                            "the counterpart's zero was not bumped to the marker"));
        }
    }

    /**
     * The curse search that closes the loop body. C calls it outside the if/else, so it runs for
     * every item whichever branch was taken.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the curse search")
    class Curses {

        @Test
        @DisplayName("runs for an item that does not affect the resistance itself")
        void afterElseBranch() throws Exception {
            ItemObject armour = item(Map.of());
            makeUnknown(armour);
            curse(armour, fireResisting, 40);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE),
                            "the curse taught the resistance"),
                    () -> assertTrue(knowledge.curseIsKnown(fireResisting),
                            "and its own rune with it"),
                    () -> assertEquals(1, glows(), "announced once, by the curse search"));
        }

        @Test
        @DisplayName("runs for an item that does affect the resistance")
        void afterAffectingBranch() throws Exception {
            // The item teaches the resistance first, so the curse search finds it already known and
            // stays quiet about it — but still learns the curse. One glow, from the item.
            ItemObject armour = item(Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)));
            curse(armour, fireResisting, 40);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertEquals(1, glows()),
                    () -> assertTrue(knowledge.curseIsKnown(fireResisting)));
        }

        @Test
        @DisplayName("an inert curse teaches nothing")
        void zeroPower() throws Exception {
            ItemObject armour = item(Map.of());
            makeUnknown(armour);
            curse(armour, fireResisting, 0);
            equip(armour);

            PlayerKnowledge.equipLearnElement(player, ElementEnum.ELEM_FIRE);

            assertAll(
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertFalse(knowledge.curseIsKnown(fireResisting)),
                    () -> assertEquals(List.of(), announced()));
        }
    }
}
