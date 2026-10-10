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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.enums.UILifecycleEvent;
import uk.co.jackoftradesltd.channel.messages.ChannelMessage;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.channel.messages.UIMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link Core#handleChannelOutput(UIMessage)} on its own: one message in, a verdict out, and
 * whatever the core said back left on the front end's inbox.
 *
 * <p>There is no C function to take expected values from. {@code play_game()} in
 * {@code src/ui-game.c} is single-threaded and has no inbox, so what is pinned here is the
 * contract the Javadoc on the method states - which of the four {@code UIMessage} kinds ends
 * the game thread and which are only logged or ignored - and the one ordering the C shutdown path
 * shares with it, that the save (here, the {@code STOPPED} reply) happens before the thread goes.
 *
 * <p><b>No engine, no data load, no thread.</b> {@code handleChannelOutput} touches nothing but the
 * core's sender, so these tests build a {@code Core} around a fresh pair of channels and call it
 * inline. That is what {@code CoreTest} cannot do: it drives the whole {@code gameLoop()} and
 * never sends {@code UIEntriesLoaded}, so its messages are all read by the load's own wait and
 * the loop after the load is never reached.
 *
 * <p><b>"Nothing was sent" is proved by a sentinel, not a timeout.</b> The channel is a FIFO, so
 * after the call a marker is sent from the core's own sender and read back; if the marker is the
 * first thing to arrive, nothing was queued before it. The check is exact and needs no waiting.
 *
 * <p>Class CoreHandleChannelOutputTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class CoreHandleChannelOutputTest {

    /**
     * Defaults for every switch; {@code handleChannelOutput} reads none of them.
     */
    private static final StartupOptions NO_OPTIONS =
            new StartupOptions(false, false, false, false, "", "", List.of());

    /**
     * A message no code path of {@code handleChannelOutput} can produce, so reading it back means
     * nothing else came before it. {@code EVENT_HP} is a payload-free event that the core sends
     * during play but never from this method.
     */
    private static final CoreMessage SENTINEL = new CoreMessage.SimpleCoreMessage(GameEventType.EVENT_HP);

    private Channels channels;
    private Core core;

    @BeforeEach
    void setUp() {
        channels = Channels.create();
        core = new Core(channels.coreChannel(), NO_OPTIONS);
    }

    /**
     * Sends the sentinel from the core's sender and reads the front end's inbox once.
     *
     * @return what arrived first: the sentinel if the core had said nothing, otherwise whatever it
     *         had said
     */
    private ChannelMessage firstThingOnTheFrontEndsInbox() throws InterruptedException {
        channels.coreChannel().coreSender().send(SENTINEL);
        return channels.uiChannel().uiReceiver().receive();
    }

    /**
     * {@code SAVE_AND_STOP} ends the thread: the verdict is {@code true} and exactly one
     * {@code STOPPED} is on the queue by the time the call returns.
     *
     * <p>The reply being queued before the return is what lets the caller end the thread without
     * the front end waiting on a message that cannot arrive.
     */
    @Test
    void saveAndStopSendsStoppedAndSaysToEnd() throws Exception {
        boolean end = core.handleChannelOutput(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));

        assertTrue(end, "SAVE_AND_STOP must tell the caller to end the game thread");
        assertEquals(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED),
                channels.uiChannel().uiReceiver().receive(),
                "STOPPED must already be on the queue when the call returns");
        assertEquals(SENTINEL, firstThingOnTheFrontEndsInbox(),
                "STOPPED must be sent once, and nothing else with it");
    }

    /**
     * {@code START} is logged and nothing more: the verdict is {@code false} and the core says
     * nothing. Calling it on a fresh {@code Core} also covers the branch where the load has not
     * finished, since the flag starts unset - the method must not throw and must not end the thread
     * there either.
     */
    @Test
    void startIsNotAStopAndSendsNothing() throws Exception {
        boolean end = core.handleChannelOutput(new UIMessage.LifecycleUIMessage(UILifecycleEvent.START));

        assertFalse(end, "START must not end the game thread");
        assertEquals(SENTINEL, firstThingOnTheFrontEndsInbox(), "START must not make the core reply");
    }

    /**
     * A raw window event is ignored. The EDT posts these to the UI thread, so one arriving here
     * means the wiring slipped, and the right answer is still to carry on.
     */
    @Test
    void windowCloseRequestedIsIgnored() throws Exception {
        boolean end = core.handleChannelOutput(new UIMessage.WindowCloseRequested());

        assertFalse(end, "a window event is not a shutdown request to the core");
        assertEquals(SENTINEL, firstThingOnTheFrontEndsInbox());
    }

    /**
     * A bus event forwarded by the front end is ignored, including {@code EVENT_ENTER_INIT}, the one
     * the code names. The check covers both the named type and an unrelated one, since the arm tests
     * the type and then does nothing either way.
     */
    @Test
    void simpleUiMessagesAreIgnoredWhateverTheirType() throws Exception {
        assertFalse(core.handleChannelOutput(new UIMessage.SimpleUIMessage(GameEventType.EVENT_ENTER_INIT)));
        assertFalse(core.handleChannelOutput(new UIMessage.SimpleUIMessage(GameEventType.EVENT_HP)));

        assertEquals(SENTINEL, firstThingOnTheFrontEndsInbox());
    }

    /**
     * A {@code UIEntriesLoaded} that reaches this method is ignored. During the load it is
     * consumed by the load's own wait and never gets here; after it, nothing wants it.
     */
    @Test
    void aLateUiEntriesLoadedIsIgnored() throws Exception {
        boolean end = core.handleChannelOutput(new UIMessage.UIEntriesLoaded(List.of()));

        assertFalse(end);
        assertEquals(SENTINEL, firstThingOnTheFrontEndsInbox());
    }

    /**
     * The verdict does not stick: a {@code START} after a {@code SAVE_AND_STOP} is judged on its
     * own, and a {@code SAVE_AND_STOP} after several ignored messages still ends the thread. The
     * method keeps no state between calls that changes its answer.
     */
    @Test
    void eachCallIsJudgedOnItsOwn() throws Exception {
        assertFalse(core.handleChannelOutput(new UIMessage.LifecycleUIMessage(UILifecycleEvent.START)));
        assertFalse(core.handleChannelOutput(new UIMessage.WindowCloseRequested()));
        assertTrue(core.handleChannelOutput(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP)));
        assertFalse(core.handleChannelOutput(new UIMessage.LifecycleUIMessage(UILifecycleEvent.START)));
    }

    /**
     * {@code getCoreChannel} hands back the very pair the constructor was given - the data load
     * reads its inbox through it, so a copy would be a second queue and a load that waits forever.
     */
    @Test
    void getCoreChannelReturnsTheChannelItWasBuiltWith() {
        assertSame(channels.coreChannel(), core.getCoreChannel());
    }
}
