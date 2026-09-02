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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.gameinput.GameInput;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the two {@link ItemObject} methods that talk to the player: {@code flagMessage}, which
 * announces a property giving itself away, and {@code verifyObject}, which asks a yes-or-no
 * question.
 *
 * <p>Both reach outside the middle layer — one to the event bus, the other to the input boundary —
 * so both are tested by standing something in the way and watching what arrives. That is the only
 * way to assert the part that matters: {@code flagMessage} has three outcomes and two of them are
 * silence, and telling "this property has no message" from "this property does not exist" is the
 * whole of its logic.
 *
 * @author Rowan Crowther
 */
class ItemObjectMessagingTest {

    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;

    /**
     * The bus the engine had before, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * The property table as it was before each test.
     */
    private Object savedProperties;

    /**
     * The registry's property table, made accessible so a test can state its contents.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field propertiesField() throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField("objectProperties");
        field.setAccessible(true);
        return field;
    }

    /**
     * Registers a flag property carrying the given notice message.
     *
     * @param flag    the flag it describes
     * @param message the notice message, which may be {@code null}
     * @throws Exception if the property table cannot be reached
     */
    @SuppressWarnings("unchecked")
    private static void registerProperty(ObjectFlag flag, String message) throws Exception {
        ObjectProperty property = new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG,
                ObjectFlagType.OFT_MISC, ObjectFlagID.OFID_WIELD,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag),
                0, 0, Map.of(), flag.name(), "", "", message, "", List.of());

        ((List<ObjectProperty>) propertiesField().get(null)).add(property);
    }

    /**
     * Stands an input boundary in front of the real one, answering every yes-or-no question the same
     * way and remembering what it was asked.
     *
     * <p>Built as a dynamic proxy rather than a written-out class. {@link GameInput} is the whole
     * boundary between the middle layer and the interface — a dozen and a half methods — and a stub
     * implementing all of them would need changing every time one is added, for the sake of the one
     * method under test.
     *
     * @param answer  the answer to give to every question
     * @param prompts the list to record prompts into
     * @return the stub
     */
    private static GameInput recordingInput(boolean answer, List<String> prompts) {
        return (GameInput) java.lang.reflect.Proxy.newProxyInstance(
                GameInput.class.getClassLoader(),
                new Class<?>[]{GameInput.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getCheck")) {
                        prompts.add((String) args[0]);
                        return answer;
                    }
                    return method.getReturnType().isPrimitive() ? false : null;
                });
    }

    /**
     * Stands a capturing bus and an empty property table in front of the real ones.
     *
     * @throws Exception if the property table cannot be reached
     */
    @BeforeEach
    void setUp() throws Exception {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        savedProperties = propertiesField().get(null);
        propertiesField().set(null, new ArrayList<ObjectProperty>());
    }

    /**
     * Puts both back, and clears any input stub.
     *
     * @throws Exception if the property table cannot be reached
     */
    @AfterEach
    void tearDown() throws Exception {
        GameEngine.setEventsBusHandler(realBus);
        propertiesField().set(null, savedProperties);
        GameInputHolder.resetInstance();
    }

    /**
     * An event bus that records the messages the middle layer raises.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * The text of every message event seen, in order.
         */
        private final List<String> messages = new ArrayList<>();

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
            if (data instanceof EventDataMessage message) {
                messages.add(message.message());
            }
        }
    }

    /**
     * {@code flagMessage}, whose three outcomes are a message, a deliberate silence, and a logged
     * data error.
     */
    @Nested
    @DisplayName("flagMessage")
    class FlagMessages {

        /**
         * A property with a message announces it, with the item's name substituted for the tag the
         * data file writes.
         *
         * @throws Exception if the property table cannot be reached
         */
        @Test
        @DisplayName("a property with a message announces it, name substituted")
        void messageIsAnnounced() throws Exception {
            registerProperty(ObjectFlag.OF_FEATHER, "Your {name} lifts you clear.");

            new ItemObject().flagMessage(ObjectFlag.OF_FEATHER, "boots of elvenkind");

            assertEquals(1, bus.messages.size());
            assertEquals("Your boots of elvenkind lifts you clear.", bus.messages.get(0));
        }

        /**
         * A property with no message says nothing, and that is not an error — most flags are learned
         * silently. The distinction from the error case below is the whole point of the method.
         *
         * @throws Exception if the property table cannot be reached
         */
        @Test
        @DisplayName("a property with no message says nothing")
        void noMessageIsSilent() throws Exception {
            registerProperty(ObjectFlag.OF_FEATHER, null);

            new ItemObject().flagMessage(ObjectFlag.OF_FEATHER, "boots");

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * A flag with no property at all is a data error, and is logged rather than announced — the
         * player is told nothing, because there is nothing to tell them.
         */
        @Test
        @DisplayName("a flag with no property announces nothing")
        void missingPropertyAnnouncesNothing() {
            new ItemObject().flagMessage(ObjectFlag.OF_FEATHER, "boots");

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The two sentinel flags are the other half of that error case: an index that could never
         * be a real property, as against a real flag the data file forgot. Neither reaches the
         * player.
         */
        @Test
        @DisplayName("the sentinel flags announce nothing either")
        void sentinelsAnnounceNothing() {
            new ItemObject().flagMessage(ObjectFlag.OF_NONE, "boots");
            new ItemObject().flagMessage(ObjectFlag.OF_MAX, "boots");

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * A message with no tag is passed through unchanged, so a property that does not name the
         * item still reads properly.
         *
         * @throws Exception if the property table cannot be reached
         */
        @Test
        @DisplayName("a message without the tag is passed through unchanged")
        void untaggedMessagePassesThrough() throws Exception {
            registerProperty(ObjectFlag.OF_FEATHER, "You feel lighter.");

            new ItemObject().flagMessage(ObjectFlag.OF_FEATHER, "boots");

            assertEquals("You feel lighter.", bus.messages.get(0));
        }
    }

    /**
     * {@code verifyObject}, which puts a question and answers with what the player said.
     */
    @Nested
    @DisplayName("verifyObject")
    class Verify {

        /**
         * The answer comes back as the player gave it, either way round.
         */
        @Test
        @DisplayName("the player's answer is what comes back")
        void answerIsReturned() {
            GameInputHolder.setInstance(recordingInput(true, new ArrayList<>()));

            assertTrue(new ItemObject().verifyObject("Really drop", null));

            GameInputHolder.setInstance(recordingInput(false, new ArrayList<>()));

            assertFalse(new ItemObject().verifyObject("Really drop", null));
        }

        /**
         * The prompt is the caller's words followed by the item's description and a question mark,
         * so the player is asked about a named thing rather than about "it".
         */
        @Test
        @DisplayName("the prompt names the item")
        void promptNamesTheItem() {
            List<String> prompts = new ArrayList<>();
            GameInputHolder.setInstance(recordingInput(true, prompts));

            new ItemObject().verifyObject("Really take off and drop", null);

            assertEquals(1, prompts.size());
            assertTrue(prompts.get(0).startsWith("Really take off and drop "),
                    "the caller's words come first");
            assertTrue(prompts.get(0).endsWith("? "),
                    "and the question mark comes last");
        }
    }
}
