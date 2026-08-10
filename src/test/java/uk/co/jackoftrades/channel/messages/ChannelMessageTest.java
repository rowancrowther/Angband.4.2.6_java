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

package uk.co.jackoftrades.channel.messages;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.CoreLIfecycleEvent;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the channel protocol: {@link ChannelMessage} and its two branches,
 * {@link CoreMessage} (what the core sends) and {@link UIMessage} (what the UI sends).
 *
 * <p>These types carry no behaviour, so there is no logic here to catch out. What the tests
 * defend is the <em>shape</em> of the protocol, which later stages lean on in ways that fail
 * quietly if it drifts:
 *
 * <ul>
 *   <li><b>Sealedness.</b> The receive loops in stages 2 and 3 switch over these interfaces
 *       and rely on the compiler proving the switch exhaustive. That proof only exists while
 *       the hierarchy is sealed, and {@code sealed} is one keyword away from being lost in a
 *       refactor. The permits clauses are pinned here so the loss is loud.</li>
 *   <li><b>Record equality.</b> Every test that asserts "this message reached the channel"
 *       compares messages by value. Records give that for free — but only for as long as they
 *       stay records, and a payload-free message being a singleton-like value is precisely what
 *       makes the shutdown handshake assertable.</li>
 *   <li><b>Queue round-trip.</b> The channels are {@code LinkedBlockingQueue}s of
 *       {@link ChannelMessage}, and both branches have to fit through one. That is checked
 *       directly rather than assumed from the type declaration.</li>
 * </ul>
 *
 * @author Rowan Crowther
 */
class ChannelMessageTest {

    /**
     * @param sealedType the sealed interface to read
     * @return the classes the interface permits, as a set so declaration order is not pinned
     * @author Rowan Crowther
     */
    private static Set<Class<?>> permitted(Class<?> sealedType) {
        return Set.of(sealedType.getPermittedSubclasses());
    }

    /**
     * @param constants the enum constants to name
     * @return their names as a set
     * @author Rowan Crowther
     */
    private static Set<String> names(Enum<?>[] constants) {
        return java.util.Arrays.stream(constants).map(Enum::name).collect(Collectors.toSet());
    }

    /**
     * The protocol root's two branches, one per sender. If a third ever appears, this fails and
     * whoever added it has to decide consciously whether a message that is neither the core's nor
     * the UI's makes sense.
     *
     * @author Rowan Crowther
     */
    @Test
    void channelMessageIsSealedOverExactlyTheTwoSenders() {
        assertTrue(ChannelMessage.class.isSealed(), "ChannelMessage must stay sealed - the UI loop's "
                + "exhaustive switch depends on it");

        assertEquals(Set.of(CoreMessage.class, UIMessage.class), permitted(ChannelMessage.class));
    }

    /**
     * Both branches are themselves sealed, so a switch can go a level deeper and still be
     * exhaustive without a default arm.
     *
     * @author Rowan Crowther
     */
    @Test
    void bothBranchesAreThemselvesSealed() {
        assertTrue(CoreMessage.class.isSealed(), "CoreMessage must stay sealed");
        assertTrue(UIMessage.class.isSealed(), "UIMessage must stay sealed");
    }

    /**
     * Every leaf of the protocol is a record. Anything that is not brings mutable state or
     * identity equality onto a queue shared by two threads.
     *
     * @author Rowan Crowther
     */
    @Test
    void everyLeafOfTheProtocolIsARecord() {
        List<Class<?>> leaves = new ArrayList<>();
        leaves.addAll(permitted(CoreMessage.class));
        leaves.addAll(permitted(UIMessage.class));

        assertFalse(leaves.isEmpty(), "the protocol should have at least one message in it");

        for (Class<?> leaf : leaves) {
            assertTrue(leaf.isRecord(), leaf.getSimpleName() + " must be a record - messages crossing "
                    + "a channel are compared by value and must not be mutable");
        }
    }

    /**
     * The core channel is the UI thread's single inbox, so it is typed as the protocol root and
     * has to carry both senders' messages. Checked against a real queue rather than inferred from
     * the declaration.
     *
     * @author Rowan Crowther
     */
    @Test
    void bothSendersFitThroughOneChannelInOrder() throws InterruptedException {
        LinkedBlockingQueue<ChannelMessage> coreChannel = new LinkedBlockingQueue<>();

        ChannelMessage fromCore = new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_ENTER_INIT);
        ChannelMessage fromUi = new UIMessage.LifecycleUIMessage(UILifecycleEvent.START);

        coreChannel.put(fromCore);
        coreChannel.put(fromUi);

        assertEquals(fromCore, coreChannel.take());
        assertEquals(fromUi, coreChannel.take());
        assertTrue(coreChannel.isEmpty());
    }

    /**
     * The UI channel is typed to the UI's messages alone, so the core cannot send on it by type.
     * Nothing to assert at runtime — the check is that this compiles with the narrower element
     * type, and would not if {@code UIMessage} stopped being a distinct branch.
     *
     * @author Rowan Crowther
     */
    @Test
    void theUiChannelAcceptsOnlyUiMessages() throws InterruptedException {
        LinkedBlockingQueue<UIMessage> uiChannel = new LinkedBlockingQueue<>();

        uiChannel.put(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));

        assertEquals(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP), uiChannel.take());
    }

    /**
     * Tests for the core's half of the protocol.
     *
     * @author Rowan Crowther
     */
    @Nested
    class CoreMessages {

        /**
         * @author Rowan Crowther
         */
        @Test
        void aSimpleMessageCarriesItsEventTypeAndNothingElse() {
            CoreMessage.SimpleCoreMessage message = new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_HP);

            assertEquals(GameEventType.EVENT_HP, message.gameEventType());
            assertInstanceOf(CoreMessage.class, message);
            assertInstanceOf(ChannelMessage.class, message);
        }

        /**
         * The point of one record serving many events: the payload-free constants all ride the
         * same shape, and only the event type tells them apart. If that stopped being true the
         * record count would start tracking game content instead of payload variety.
         *
         * @author Rowan Crowther
         */
        @Test
        void simpleMessagesDifferOnlyByEventType() {
            CoreMessage.SimpleCoreMessage hp = new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_HP);
            CoreMessage.SimpleCoreMessage gold = new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_GOLD);

            assertEquals(hp, new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_HP));
            assertNotEquals(hp, gold);
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void aTextMessageCarriesItsEventTypeAndText() {
            CoreMessage.TextCoreMessage message =
                    new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, "Initializing arrays...");

            assertEquals(GameEventType.EVENT_INITSTATUS, message.gameEventType());
            assertEquals("Initializing arrays...", message.message());
        }

        /**
         * Same text under a different event is a different message. Worth pinning because the
         * event type is what the UI switches on, and equality that ignored it would let a test
         * pass while the wrong branch ran.
         *
         * @author Rowan Crowther
         */
        @Test
        void textMessagesAreDistinguishedByEventTypeAsWellAsText() {
            CoreMessage.TextCoreMessage initNote =
                    new CoreMessage.TextCoreMessage(GameEventType.EVENT_INITSTATUS, "same words");
            CoreMessage.TextCoreMessage birthNote =
                    new CoreMessage.TextCoreMessage(GameEventType.EVENT_BIRTHPOINTS, "same words");

            assertNotEquals(initNote, birthNote);
        }

        /**
         * The shutdown sentinel compares equal to any other instance of itself, which is what lets
         * the stage 3 handshake test assert on it without holding the original reference.
         *
         * @author Rowan Crowther
         */
        @Test
        void theLifecycleMessageIsComparableByValue() {
            assertEquals(new CoreMessage.LifecycleCoreMessage(CoreLIfecycleEvent.STOPPED),
                    new CoreMessage.LifecycleCoreMessage(CoreLIfecycleEvent.STOPPED));
        }
    }

    /**
     * Tests for the UI's half of the protocol.
     *
     * @author Rowan Crowther
     */
    @Nested
    class UiMessages {

        /**
         * Start and save-and-stop share one record and are told apart by the enum inside it, which
         * is the same construction the core's lifecycle uses.
         *
         * @author Rowan Crowther
         */
        @Test
        void startAndSaveAndStopAreOneRecordDistinguishedByTheirEvent() {
            UIMessage.LifecycleUIMessage start = new UIMessage.LifecycleUIMessage(UILifecycleEvent.START);
            UIMessage.LifecycleUIMessage stop = new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP);

            assertEquals(UILifecycleEvent.START, start.event());
            assertEquals(UILifecycleEvent.SAVE_AND_STOP, stop.event());
            assertNotEquals(start, stop);
            assertEquals(start, new UIMessage.LifecycleUIMessage(UILifecycleEvent.START));
        }

        /**
         * The two halves' lifecycle vocabularies are separate enums, so neither end can name a
         * signal it has no business sending. This is a compile-time property; the test records the
         * intent so that merging them reads as the decision it would be.
         *
         * @author Rowan Crowther
         */
        @Test
        void eachHalfHasItsOwnLifecycleVocabulary() {
            Set<String> uiEvents = names(UILifecycleEvent.values());
            Set<String> coreEvents = names(CoreLIfecycleEvent.values());

            assertEquals(Set.of("START", "SAVE_AND_STOP"), uiEvents);
            assertEquals(Set.of("STOPPED"), coreEvents);
            assertTrue(java.util.Collections.disjoint(uiEvents, coreEvents),
                    "neither half should be able to send the other's lifecycle signals");
        }
    }
}