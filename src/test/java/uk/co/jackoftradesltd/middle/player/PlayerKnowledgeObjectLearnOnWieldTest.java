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
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.middle.objects.enums.CombatRunes;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge#objectLearnOnWield}, the port of C's {@code object_learn_on_wield}
 * ({@code obj-knowledge.c}).
 *
 * <p>The expected values are read off the C function clause by clause, not off the port. In C's
 * order the function: returns at once if the item is already marked worn, otherwise marks it;
 * marks the flavour tried; builds the {@code OFID_WIELD} mask and adds the sustain of every stat
 * the item has a nonzero modifier for; learns each flag that is in the item <em>and</em> the mask
 * <em>and</em> not already known, announcing it only while playing; learns each nonzero modifier
 * not already known, likewise; runs the five curse routes; and finally runs the element route for
 * every element whose resistance the player can already read.
 *
 * <p>The cases are shaped by the three faults found when the port was first checked, so that each
 * one would fail here if it came back: the flag test inverted (known flags learned, unknown ones
 * skipped), the learning sitting inside the loop over stats (so every message arrived five times
 * and the curse routes saw a part-built mask), and the missing element loop at the foot.
 *
 * <p>The fixture's {@code object_property.txt} is five flags and one stat, classified as the real
 * data file classifies them: {@code AFRAID} is {@code on wield}, {@code SUST_STR} and
 * {@code IMPAIR_HP} are not. That split is what lets a case tell "in the mask" from "merely on the
 * item".
 *
 * <p>The description of every item reads {@code {DESCRIPTION_TAG}} in these tests, which is how the
 * port's {@code description} renders a name for the UI to fill in.
 *
 * <p>Test class coded on 261004.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("PlayerKnowledge.objectLearnOnWield")
class PlayerKnowledgeObjectLearnOnWieldTest {

    private static final List<String> SAVED_FIELDS =
            List.of("curses", "allRunes", "curseMax", "objectProperties", "objectPropertyMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    private static final String TREMBLE = "Your {DESCRIPTION_TAG} makes you tremble.";

    /**
     * On wield, with a notice message.
     */
    private static ObjectProperty fear;
    /**
     * A sustain: identified by effect, so never obvious on wield.
     */
    private static ObjectProperty sustainStrength;
    /**
     * Timed, so never obvious on wield.
     */
    private static ObjectProperty impairHp;
    private static ObjectProperty strength;

    /**
     * Carries {@code AFRAID} in its object, so only the curse route can find that flag.
     */
    private static Curse cowardice;
    /**
     * Carries a strength penalty in its object.
     */
    private static Curse weakness;
    /**
     * Carries a to-AC penalty and nothing else.
     */
    private static Curse vulnerability;
    /**
     * Resists fire and nothing else.
     */
    private static Curse burning;

    private Player player;
    private KnownObject knowledge;
    private CapturingBus bus;
    private EventsHandler realBus;

    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        fear = flagProperty(ObjectFlag.OF_AFRAID, ObjectFlagType.OFT_BAD, ObjectFlagID.OFID_WIELD,
                "fear", "Your {name} makes you tremble.");
        sustainStrength = flagProperty(ObjectFlag.OF_SUST_STR, ObjectFlagType.OFT_SUST,
                ObjectFlagID.OFID_NORMAL, "sustain strength", null);
        impairHp = flagProperty(ObjectFlag.OF_IMPAIR_HP, ObjectFlagType.OFT_BAD,
                ObjectFlagID.OFID_TIMED, "impaired hitpoint recovery", null);
        strength = new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectFlagType.OFT_NONE,
                ObjectFlagID.OFID_NONE,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR),
                9, 13, null, "strength", "strong", "weak", null, null, null);

        cowardice = curse("cowardice", flags(ObjectFlag.OF_AFRAID), Map.of(), Map.of(), 0, 0);
        weakness = curse("weakness", flags(), Map.of(ObjectModifier.OM_STR, -3), Map.of(), 0, 1);
        vulnerability = curse("vulnerability", flags(), Map.of(), Map.of(), -50, 2);
        burning = curse("burning", flags(), Map.of(),
                Map.of(ElementEnum.ELEM_FIRE, elementInfo(1)), 0, 3);
    }

    /**
     * Puts the curses, properties and runes into the registry. Done before every test rather than
     * once, because {@link SeededPlayerRegistry} is inherited by each {@code @Nested} class and
     * empties the curse list when the first of them finishes - a registry seeded only in
     * {@link #seed()} is gone by the time the second class runs, and
     * {@code cursesFindModifiers} walks the registry's list rather than the item's.
     */
    private static void seedRegistry() {
        ObjectRegistry.setCurses(List.of(cowardice, weakness, vulnerability, burning));
        ObjectRegistry.setObjectProperties(List.of(fear, sustainStrength, impairHp));
        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_AFRAID, fear)),
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainStrength)),
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_IMPAIR_HP, impairHp)),
                new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strength)),
                new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)),
                new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)),
                new Rune(new RuneVariety.CurseKey(cowardice)),
                new Rune(new RuneVariety.CurseKey(weakness)),
                new Rune(new RuneVariety.CurseKey(vulnerability)),
                new Rune(new RuneVariety.CurseKey(burning)))));
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    private static ObjectProperty flagProperty(ObjectFlag flag, ObjectFlagType subtype, ObjectFlagID id,
                                               String name, String message) {
        return new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, subtype, id,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag),
                0, 0, null, name, null, null, message, null, null);
    }

    private static Curse curse(String name, Flag<ObjectFlag> curseFlags,
                               Map<ObjectModifier, Integer> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo, int toA, int index) {
        return new Curse(name, List.of(), 0, null, curseFlags, modifiers, elInfo, 0, 0, toA,
                List.of(), new Flag<>(ObjectFlag.class), name, "The curse fires.", index);
    }

    private static Flag<ObjectFlag> flags(ObjectFlag... on) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (on.length > 0) result.set(List.of(on));
        return result;
    }

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

    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

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
     * A ring with the given flags and a known counterpart, not yet worn.
     */
    private static ItemObject ring(ObjectFlag... on) {
        return ItemFixture.item(TValue.TV_RING).flags(on).known(new ItemObject()).build();
    }

    /**
     * A ring with a strength modifier as well as the given flags.
     */
    private static ItemObject ringOfStrength(int bonus, ObjectFlag... on) {
        ItemObject item = ring(on);
        item.putModifier(ObjectModifier.OM_STR, bonus);
        return item;
    }

    private static ItemObject cursedRing(Curse curse) {
        return ItemFixture.item(TValue.TV_RING).known(new ItemObject())
                .curse(curse, new CurseData(40, 0)).build();
    }

    private List<String> announced() {
        return bus.messages.stream().map(EventDataMessage::message).toList();
    }

    private void startPlaying() throws Exception {
        poke(player.getPlayerUpkeep(), "playing", true);
    }

    private void wield(ItemObject item) {
        PlayerKnowledge.objectLearnOnWield(player, item);
    }

    @BeforeEach
    void setUp() throws Exception {
        seedRegistry();
        player = new Player();
        knowledge = new KnownObject();
        Field f = Player.class.getDeclaredField("itemKnowledge");
        f.setAccessible(true);
        f.set(player, knowledge);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
        clearMessageLog();
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

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
     * The ordinary path, and the two faults that made it wrong: a test the wrong way round, and a
     * body that ran once for every stat.
     */
    @Nested
    @DisplayName("flags and modifiers")
    class FlagsAndModifiers {

        @Test
        @DisplayName("an unknown on-wield flag is learned and announced, once")
        void unknownFlagIsLearned() throws Exception {
            startPlaying();
            ItemObject item = ring(ObjectFlag.OF_AFRAID);

            wield(item);

            // C: !of_has(obj_k->flags, flag) is the condition to LEARN. The rune message comes
            // from player_learn_rune, then flag_message.
            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of fear.",
                                    TREMBLE),
                            announced()));
        }

        @Test
        @DisplayName("an already-known flag is silent")
        void knownFlagIsSilent() throws Exception {
            startPlaying();
            knowledge.learnFlag(ObjectFlag.OF_AFRAID);
            ItemObject item = ring(ObjectFlag.OF_AFRAID);

            wield(item);

            assertEquals(List.of(), announced());
        }

        @Test
        @DisplayName("not playing: the rune is learned but the flag is not announced")
        void notPlayingIsQuiet() {
            ItemObject item = ring(ObjectFlag.OF_AFRAID);

            wield(item);

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertEquals(List.of("You have learned the rune of fear."), announced()));
        }

        @Test
        @DisplayName("a flag that is not obvious on wield is left alone")
        void notObviousIsNotLearned() throws Exception {
            startPlaying();
            ItemObject item = ring(ObjectFlag.OF_IMPAIR_HP);

            wield(item);

            assertAll(
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_IMPAIR_HP)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("a positive modifier is learned and announced")
        void positiveModifier() throws Exception {
            startPlaying();
            ItemObject item = ringOfStrength(2);

            wield(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of strength.",
                                    "You feel stronger!"),
                            announced()));
        }

        @Test
        @DisplayName("a negative modifier is learned too: C tests nonzero, not positive")
        void negativeModifier() throws Exception {
            startPlaying();
            ItemObject item = ringOfStrength(-2);

            wield(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of strength.",
                                    "You feel weaker!"),
                            announced()));
        }

        @Test
        @DisplayName("an already-known modifier is silent")
        void knownModifierIsSilent() throws Exception {
            startPlaying();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            ItemObject item = ringOfStrength(2);

            wield(item);

            assertEquals(List.of(), announced());
        }

        @Test
        @DisplayName("a modifier that is not on the item is not learned")
        void absentModifier() throws Exception {
            startPlaying();
            ItemObject item = ring(ObjectFlag.OF_AFRAID);

            wield(item);

            assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_STR));
        }

        @Test
        @DisplayName("a stat modifier makes the sustain obvious, if the item carries it")
        void sustainFollowsModifier() throws Exception {
            startPlaying();
            ItemObject item = ringOfStrength(2, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_AFRAID);

            wield(item);

            // Everything announced exactly once, in C's order: flags in flag order (the sustain
            // sorts ahead of AFRAID and has no notice message), then modifiers. A body that ran
            // once per stat would repeat the lot.
            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR)),
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of sustain strength.",
                                    "You have learned the rune of fear.",
                                    TREMBLE,
                                    "You have learned the rune of strength.",
                                    "You feel stronger!"),
                            announced()));
        }

        @Test
        @DisplayName("a stat modifier does not conjure a sustain the item does not carry")
        void sustainNeedsTheFlag() throws Exception {
            startPlaying();
            ItemObject item = ringOfStrength(2);

            wield(item);

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
        }

        @Test
        @DisplayName("a sustain with no stat modifier stays unlearned")
        void sustainWithoutModifier() throws Exception {
            startPlaying();
            ItemObject item = ring(ObjectFlag.OF_SUST_STR);

            wield(item);

            assertAll(
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("the item's own flags are not disturbed by the mask intersection")
        void itemFlagsSurvive() {
            ItemObject item = ring(ObjectFlag.OF_AFRAID, ObjectFlag.OF_IMPAIR_HP);

            wield(item);

            assertAll(
                    () -> assertTrue(item.hasFlag(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(item.hasFlag(ObjectFlag.OF_IMPAIR_HP)));
        }
    }

    /**
     * The worn marker, the flavour, and the missing known counterpart.
     */
    @Nested
    @DisplayName("once per item")
    class OncePerItem {

        @Test
        @DisplayName("the first call marks the item worn and the flavour tried")
        void marks() {
            ItemObject item = ring();

            assertFalse((boolean) ItemFixture.read(item.getKind(), "tried"));
            wield(item);

            assertAll(
                    () -> assertTrue(item.getKnown().getNoticeHas(ObjectNotice.OBJ_NOTICE_WORN)),
                    () -> assertTrue((boolean) ItemFixture.read(item.getKind(), "tried")));
        }

        @Test
        @DisplayName("a second call teaches nothing, even to a player who has since forgotten")
        void secondCallReturnsEarly() throws Exception {
            startPlaying();
            ItemObject item = ring(ObjectFlag.OF_AFRAID);
            wield(item);
            KnownObject fresh = new KnownObject();
            Field f = Player.class.getDeclaredField("itemKnowledge");
            f.setAccessible(true);
            f.set(player, fresh);
            bus.messages.clear();

            wield(item);

            assertAll(
                    () -> assertFalse(fresh.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("an item already marked worn returns before the flavour is touched")
        void alreadyWorn() {
            ItemObject item = ring(ObjectFlag.OF_AFRAID);
            item.getKnown().setNoticeOn(ObjectNotice.OBJ_NOTICE_WORN);

            wield(item);

            assertAll(
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertFalse((boolean) ItemFixture.read(item.getKind(), "tried")));
        }

        @Test
        @DisplayName("no known counterpart: logged and abandoned, where C asserts")
        void noKnownCounterpart() throws Exception {
            startPlaying();
            ItemObject item = ItemFixture.item(TValue.TV_RING).flags(ObjectFlag.OF_AFRAID).build();

            assertDoesNotThrow(() -> wield(item));

            assertAll(
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertEquals(List.of(), announced()),
                    () -> assertFalse((boolean) ItemFixture.read(item.getKind(), "tried")));
        }
    }

    /**
     * The five curse routes and the element loop that C runs last.
     */
    @Nested
    @DisplayName("curses")
    class Curses {

        @Test
        @DisplayName("a curse flag in the wield mask is learned with the curse")
        void curseFlagRoute() throws Exception {
            startPlaying();
            ItemObject item = cursedRing(cowardice);

            wield(item);

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(knowledge.curseIsKnown(cowardice)));
        }

        @Test
        @DisplayName("a curse modifier is learned with the curse")
        void curseModifierRoute() throws Exception {
            startPlaying();
            ItemObject item = cursedRing(weakness);

            wield(item);

            assertAll(
                    () -> assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR)),
                    () -> assertTrue(knowledge.curseIsKnown(weakness)));
        }

        @Test
        @DisplayName("a curse armour penalty is learned with the curse")
        void curseToARoute() throws Exception {
            startPlaying();
            ItemObject item = cursedRing(vulnerability);

            wield(item);

            assertAll(
                    () -> assertTrue(knowledge.toAIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(vulnerability)));
        }

        @Test
        @DisplayName("an element the player can already read lets its curse show itself")
        void elementRouteKnownElement() throws Exception {
            startPlaying();
            knowledge.learnResistance(ElementEnum.ELEM_FIRE);
            ItemObject item = cursedRing(burning);

            wield(item);

            // The last loop in C: res_level of the player's knowledge, not of the item.
            assertTrue(knowledge.curseIsKnown(burning));
        }

        @Test
        @DisplayName("an element the player cannot yet read is not searched")
        void elementRouteUnknownElement() throws Exception {
            startPlaying();
            ItemObject item = cursedRing(burning);

            wield(item);

            assertAll(
                    () -> assertFalse(knowledge.curseIsKnown(burning)),
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(List.of(), announced()));
        }

        @Test
        @DisplayName("an uncursed item runs every route and finds nothing")
        void uncursed() throws Exception {
            startPlaying();
            knowledge.learnResistance(ElementEnum.ELEM_FIRE);
            ItemObject item = ring();

            assertDoesNotThrow(() -> wield(item));

            assertEquals(List.of(), announced());
        }
    }
}
