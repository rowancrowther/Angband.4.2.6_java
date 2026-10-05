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
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#modMessage(ObjectModifier)}, the port of {@code mod_message} in
 * {@code obj-knowledge.c}.
 *
 * <p>The expected text is copied from the C source, not from the Java. The method has three
 * behaviours worth separating: the nine sign-dependent modifiers print one of two lines or nothing,
 * infravision and light print whatever the value, and every other modifier is silent. The
 * zero-value and null-map cases are where C and Java could plausibly part company, because C reads
 * a zeroed array and Java reads a map that may be absent.
 *
 * <p>Messages are observed by standing a capturing bus in front of the engine's, as
 * {@code ItemObjectMessagingTest} does.
 *
 * @author Rowan Crowther
 */
class ItemObjectModMessageTest {

    /**
     * Every sign-dependent case in {@code mod_message}, in C's order, with C's exact strings.
     */
    private static final List<SignCase> SIGN_CASES = List.of(
            new SignCase(ObjectModifier.OM_STR, "You feel stronger!", "You feel weaker!"),
            new SignCase(ObjectModifier.OM_INT, "You feel smarter!", "You feel more stupid!"),
            new SignCase(ObjectModifier.OM_WIS, "You feel wiser!", "You feel more naive!"),
            new SignCase(ObjectModifier.OM_DEX, "You feel more dextrous!", "You feel clumsier!"),
            new SignCase(ObjectModifier.OM_CON, "You feel healthier!", "You feel sicklier!"),
            new SignCase(ObjectModifier.OM_STEALTH, "You feel stealthier.", "You feel noisier."),
            new SignCase(ObjectModifier.OM_SPEED, "You feel strangely quick.",
                    "You feel strangely sluggish."),
            new SignCase(ObjectModifier.OM_BLOWS, "Your weapon tingles in your hands.",
                    "Your weapon aches in your hands."),
            new SignCase(ObjectModifier.OM_SHOTS, "Your missile weapon tingles in your hands.",
                    "Your missile weapon aches in your hands."));
    /**
     * The modifiers C's switch does not name, so they reach its {@code default} and print nothing.
     */
    private static final List<ObjectModifier> SILENT = List.of(
            ObjectModifier.OM_SEARCH, ObjectModifier.OM_TUNNEL, ObjectModifier.OM_MIGHT,
            ObjectModifier.OM_MOVES, ObjectModifier.OM_DAM_RED);
    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;
    /**
     * The bus the engine had before, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * Builds an item whose only modifier is the one given.
     *
     * @param mod   the modifier to set
     * @param value its value
     * @return the item
     */
    private static ItemObject itemWith(ObjectModifier mod, int value) {
        ItemObject item = new ItemObject();
        item.setModifiers(new java.util.LinkedHashMap<>(Map.of(mod, value)));
        return item;
    }

    /**
     * Stands a capturing bus in front of the engine's.
     */
    @BeforeEach
    void setUp() {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    /**
     * Puts the real bus back.
     */
    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * A value above zero prints the positive line, exactly one message, for each of the nine
     * sign-dependent modifiers.
     */
    @Test
    @DisplayName("a positive value prints the positive line, once")
    void positiveValuePrintsPositiveLine() {
        assertAll(SIGN_CASES.stream().map(c -> () -> {
            bus.messages.clear();
            itemWith(c.mod(), 3).modMessage(c.mod());
            assertEquals(List.of(c.positive()), bus.messages, c.mod().name());
        }));
    }

    /**
     * A value below zero prints the negative line, exactly one message.
     */
    @Test
    @DisplayName("a negative value prints the negative line, once")
    void negativeValuePrintsNegativeLine() {
        assertAll(SIGN_CASES.stream().map(c -> () -> {
            bus.messages.clear();
            itemWith(c.mod(), -3).modMessage(c.mod());
            assertEquals(List.of(c.negative()), bus.messages, c.mod().name());
        }));
    }

    /**
     * The sign is all that matters: a value of one and a value of one hundred print the same line,
     * and so do minus one and minus one hundred.
     */
    @Test
    @DisplayName("only the sign matters, not the size")
    void magnitudeDoesNotMatter() {
        assertAll(SIGN_CASES.stream().map(c -> () -> {
            bus.messages.clear();
            itemWith(c.mod(), 1).modMessage(c.mod());
            itemWith(c.mod(), 100).modMessage(c.mod());
            itemWith(c.mod(), -1).modMessage(c.mod());
            itemWith(c.mod(), -100).modMessage(c.mod());
            assertEquals(List.of(c.positive(), c.positive(), c.negative(), c.negative()),
                    bus.messages, c.mod().name());
        }));
    }

    /**
     * A value of exactly zero is neither branch of C's {@code if}/{@code else if}, so nothing is
     * printed.
     */
    @Test
    @DisplayName("a value of zero prints nothing")
    void zeroValueIsSilent() {
        assertAll(SIGN_CASES.stream().map(c -> () -> {
            itemWith(c.mod(), 0).modMessage(c.mod());
            assertTrue(bus.messages.isEmpty(), c.mod().name());
        }));
    }

    /**
     * An absent entry reads zero, as C's zeroed array does, so nothing is printed.
     */
    @Test
    @DisplayName("a modifier the item does not carry prints nothing")
    void absentEntryIsSilent() {
        assertAll(SIGN_CASES.stream().map(c -> () -> {
            new ItemObject().modMessage(c.mod());
            assertTrue(bus.messages.isEmpty(), c.mod().name());
        }));
    }

    /**
     * Only the modifier named is consulted: an item with a positive strength, asked about
     * intelligence, says nothing about either.
     */
    @Test
    @DisplayName("only the named modifier is read")
    void otherModifiersAreNotRead() {
        itemWith(ObjectModifier.OM_STR, 5).modMessage(ObjectModifier.OM_INT);

        assertTrue(bus.messages.isEmpty());
    }

    /**
     * An item whose modifier map is {@code null} reads as all zeros, so every sign-dependent case
     * is silent rather than throwing. This is the case C cannot express and the one the port had to
     * handle.
     */
    @Test
    @DisplayName("a null modifier map is silent, not a NullPointerException")
    void nullMapIsSilent() {
        ItemObject item = new ItemObject();
        item.setModifiers(null);

        assertAll(SIGN_CASES.stream().map(c -> () -> {
            item.modMessage(c.mod());
            assertTrue(bus.messages.isEmpty(), c.mod().name());
        }));
    }

    /**
     * Infravision prints "Your eyes tingle." with no sign test, whether the value is positive,
     * negative, zero or the entry absent.
     */
    @Test
    @DisplayName("infravision always prints, whatever the value")
    void infraAlwaysPrints() {
        itemWith(ObjectModifier.OM_INFRA, 2).modMessage(ObjectModifier.OM_INFRA);
        itemWith(ObjectModifier.OM_INFRA, -2).modMessage(ObjectModifier.OM_INFRA);
        itemWith(ObjectModifier.OM_INFRA, 0).modMessage(ObjectModifier.OM_INFRA);
        new ItemObject().modMessage(ObjectModifier.OM_INFRA);

        assertEquals(List.of("Your eyes tingle.", "Your eyes tingle.", "Your eyes tingle.",
                "Your eyes tingle."), bus.messages);
    }

    /**
     * Light prints "It glows!" with no sign test, including on an item whose map is {@code null}.
     */
    @Test
    @DisplayName("light always prints, whatever the value, even with a null map")
    void lightAlwaysPrints() {
        ItemObject nullMap = new ItemObject();
        nullMap.setModifiers(null);

        itemWith(ObjectModifier.OM_LIGHT, 1).modMessage(ObjectModifier.OM_LIGHT);
        itemWith(ObjectModifier.OM_LIGHT, -1).modMessage(ObjectModifier.OM_LIGHT);
        itemWith(ObjectModifier.OM_LIGHT, 0).modMessage(ObjectModifier.OM_LIGHT);
        nullMap.modMessage(ObjectModifier.OM_LIGHT);

        assertEquals(List.of("It glows!", "It glows!", "It glows!", "It glows!"), bus.messages);
    }

    /**
     * Modifiers C's switch does not name reach its {@code default} and print nothing, even with a
     * non-zero value.
     */
    @Test
    @DisplayName("modifiers with no message print nothing")
    void unnamedModifiersAreSilent() {
        assertAll(SILENT.stream().map(m -> () -> {
            itemWith(m, 4).modMessage(m);
            itemWith(m, -4).modMessage(m);
            assertTrue(bus.messages.isEmpty(), m.name());
        }));
    }

    /**
     * {@code OM_NONE} and {@code OM_MAX} are the port's own bookends and also reach the default.
     */
    @Test
    @DisplayName("OM_NONE and OM_MAX print nothing")
    void bookendsAreSilent() {
        new ItemObject().modMessage(ObjectModifier.OM_NONE);
        new ItemObject().modMessage(ObjectModifier.OM_MAX);

        assertTrue(bus.messages.isEmpty());
    }

    /**
     * A {@code null} modifier, which C's {@code int} cannot be, returns without printing or
     * throwing.
     */
    @Test
    @DisplayName("a null modifier argument prints nothing")
    void nullArgumentIsSilent() {
        itemWith(ObjectModifier.OM_STR, 5).modMessage(null);

        assertTrue(bus.messages.isEmpty());
    }

    /**
     * The method only reports: the item's modifier value is the same after the call.
     */
    @Test
    @DisplayName("the item is not changed")
    void itemIsNotChanged() {
        ItemObject item = itemWith(ObjectModifier.OM_STR, 5);

        item.modMessage(ObjectModifier.OM_STR);

        assertEquals(Map.of(ObjectModifier.OM_STR, 5), item.getModifiers());
    }

    /**
     * One row of the C switch: a sign-dependent modifier with its positive and negative line.
     *
     * @param mod      the modifier
     * @param positive the line C prints for a value above zero
     * @param negative the line C prints for a value below zero
     */
    private record SignCase(ObjectModifier mod, String positive, String negative) {
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
}
