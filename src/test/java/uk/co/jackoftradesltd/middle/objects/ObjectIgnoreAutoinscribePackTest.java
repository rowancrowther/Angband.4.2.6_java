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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link ObjectIgnore#autoinscribePack}, the port of C's {@code autoinscribe_pack}
 * ({@code obj-ignore.c:352-359}).
 *
 * <p>{@link ObjectIgnore#applyAutoinscription} has its own suite
 * ({@code ObjectIgnoreApplyAutoinscriptionTest}) that pins its guards one at a time; this one
 * exists to check the loop around it - that every item in the gear is visited, in order, and that
 * one item's guard has no bearing on the next item's.
 *
 * <p>Class ObjectIgnoreAutoinscribePackTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectIgnoreAutoinscribePackTest {

    /**
     * The bus this test listens on, for the messages a successful inscription sends.
     */
    private CapturingBus bus;

    /**
     * The bus the engine had before, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A kind with the given aware state and aware autoinscription, built via reflection since
     * {@code noteAware} has no public setter (mirroring {@code ObjectIgnoreApplyAutoinscriptionTest}).
     *
     * @param noteAware the aware autoinscription, or {@code null} for none
     * @return the kind
     */
    private static ObjectKind kind(String noteAware) {
        ObjectKind kind = new ObjectKind();
        kind.setAware(true);
        set(kind, "noteAware", noteAware);
        return kind;
    }

    /**
     * A bare item on the given kind, not yet in any pile.
     *
     * @param kind the kind to build on
     * @return the item
     */
    private static ItemObject item(ObjectKind kind) {
        return ItemFixture.item(TValue.TV_POTION).kind(kind).build();
    }

    @BeforeEach
    void setUp() throws Exception {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        player = new Player();

        // Message coalesces a repeat of the newest entry into a "(xN)" count, and the log is static.
        Field log = uk.co.jackoftradesltd.middle.Message.class.getDeclaredField("messageLog");
        log.setAccessible(true);
        ((Deque<?>) log.get(null)).clear();
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    @Test
    @DisplayName("does nothing on an empty gear")
    void doesNothingOnAnEmptyGear() {
        ObjectIgnore.autoinscribePack(player);

        assertTrue(bus.messages.isEmpty());
    }

    @Test
    @DisplayName("inscribes a single due item")
    void inscribesASingleDueItem() {
        ItemObject obj = item(kind("{tried}"));
        player.getGear().insertEnd(obj);

        ObjectIgnore.autoinscribePack(player);

        assertEquals("{tried}", obj.getNote());
    }

    @Test
    @DisplayName("inscribes every due item in the gear, not just the first")
    void inscribesEveryDueItemInTheGear() {
        ItemObject first = item(kind("{first}"));
        ItemObject second = item(kind("{second}"));
        player.getGear().insertEnd(first);
        player.getGear().insertEnd(second);

        ObjectIgnore.autoinscribePack(player);

        assertEquals("{first}", first.getNote());
        assertEquals("{second}", second.getNote());
        assertEquals(2, bus.messages.size());
    }

    @Test
    @DisplayName("an already-inscribed item does not stop the next item being visited")
    void alreadyInscribedItemDoesNotStopTheNextItem() {
        ItemObject already = item(kind("{stale}"));
        already.setNote("@w1");
        ItemObject due = item(kind("{tried}"));
        player.getGear().insertEnd(already);
        player.getGear().insertEnd(due);

        ObjectIgnore.autoinscribePack(player);

        assertEquals("@w1", already.getNote(), "the reinscribe guard leaves it untouched");
        assertEquals("{tried}", due.getNote(), "but the item after it is still visited");
    }

    @Test
    @DisplayName("an item with no configured note leaves nothing behind")
    void itemWithNoConfiguredNoteLeavesNothingBehind() {
        ItemObject obj = item(kind(null));
        player.getGear().insertEnd(obj);

        ObjectIgnore.autoinscribePack(player);

        assertNull(obj.getNote());
        assertTrue(bus.messages.isEmpty());
    }

    /**
     * An event bus that records the messages the middle layer raises, the same technique
     * {@code ObjectIgnoreApplyAutoinscriptionTest} uses.
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
