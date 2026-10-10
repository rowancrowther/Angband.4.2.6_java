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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.CoreChannel;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.corechannel.CoreSender;
import uk.co.jackoftradesltd.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.middle.game.event.eventhandlers.InitHandlers;
import uk.co.jackoftradesltd.middle.game.event.eventhandlers.RedrawHandlers;

/**
 * The middle end's body: what the game thread runs, kept clear of Swing's event
 * dispatch thread (EDT) so game work never blocks repainting and input never
 * blocks the game. The thread itself belongs to {@code main()}, which names it
 * {@code angband-core}, starts it and waits for it.
 *
 * <p><b>Built by {@code main()}, named by nothing else.</b> This used to be the
 * front end's single handle on the middle end - {@code SwingUI} constructed one
 * and drove it. Stage 4 moved that: {@code main()} now builds this with the
 * core's channel ends and the parsed command line, hands it to a thread, and the
 * front end holds nothing of the middle end at all. What this class knows of the
 * other half is a queue it reads and a queue it writes, which is what keeps the
 * boundary between them one package wide.
 *
 * <p>{@link #gameLoop()} is the thread's body, reached through the {@code
 * Runnable} {@code main()} wraps this in. The wrapping is why the loop builds the
 * engine itself rather than trusting a caller to have done it first: there is one
 * entry point, and everything it needs is set up inside it.
 *
 * <p>There is no C counterpart to this class. The C original is single-threaded:
 * {@code main()} ({@code src/main.c}) initializes and then calls
 * {@code play_game()} ({@code src/ui-game.c}) on that same thread, blocking for
 * input inside the command hook. Splitting the loop onto its own thread is a
 * port-only decision forced by Swing, which reserves the EDT for the UI.
 *
 * <p><b>Wired at both ends, with nothing in the middle yet.</b> {@link #gameLoop()}
 * builds the engine, registers the handlers the start-up events leave through and runs the
 * data load - all on this thread, so the engine's mutable state is confined to it by
 * construction; it then blocks on the core's inbox and answers the shutdown handshake.
 * What is still missing is the game: {@link #handleChannelOutput(UIMessage)} acts on one
 * message, {@code SAVE_AND_STOP}, and merely logs or ignores every other {@code UIMessage},
 * because nothing else is sent yet. Commands arrive with Chapter 5 and the
 * save with Chapter 8, and both land as further arms of the same switch.
 *
 * <p><b>Two callers reach that switch.</b> {@link #gameLoop()} calls it once the load is
 * done, but the load itself also calls it: {@code GameConstants.init(Core)} stops part-way
 * to wait for the front end's {@code UIEntriesLoaded} message, and hands every other message
 * it reads while waiting to {@link #handleChannelOutput(UIMessage)}. So a shutdown request
 * that arrives mid-load is answered by the same code as one that arrives mid-game.
 *
 * <p>Class Core coded before 260813, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class Core {
    /**
     * The class's log4j logger, used by {@link #gameLoop()} for the interrupt that ends it and by
     * {@link #handleChannelOutput(UIMessage)} for each lifecycle message it is given.
     *
     * <p>Static and final, so shared by every {@code Core} and safe to use from the game thread
     * without further thought. There is no C counterpart: C reports through {@code msg()} and
     * {@code quit()}, neither of which is a log.
     *
     * <p>Field logger coded before 260813, commented in full on 261010.
     */
    private static final Logger logger = LogManager.getLogger(Core.class);

    /**
     * Whether the data load has run to completion, so the front end's {@code START} message is
     * arriving at a core that can act on it.
     *
     * <p>{@code false} from construction until {@link #gameLoop()} sees
     * {@code loadGameConstants()} return normally, then {@code true} for the rest of the run; it
     * never goes back. The only reader is the {@code START} arm of
     * {@link #handleChannelOutput(UIMessage)}, and that arm sees {@code false} only when the load
     * itself passed the message across, because the loop in {@code gameLoop()} is not entered
     * before the flag is set. Today the arm does nothing with either value; a {@code START} that
     * arrives early is logged and dropped, not held over.
     *
     * <p>Not {@code volatile}: both the write and the reads happen on the game thread.
     *
     * <p>Field gameConstantsFinishedLoading coded before 261010, commented in full on 261010.
     */
    private boolean gameConstantsFinishedLoading = false;

    /**
     * The middle end this class drives, built by {@link #getGameEngine()} and used only on the
     * game thread.
     *
     * <p>Built at the top of {@link #gameLoop()} and before {@code initHandlers()}, which is an
     * ordering rather than a preference: constructing the engine replaces the event bus, so
     * handlers registered first would go onto a bus that is then thrown away, and their events
     * would never arrive.
     *
     * <p>Not {@code volatile}, and does not need to be. It is written and read on one thread - this
     * object is constructed on {@code main}'s thread but every field it touches after that belongs
     * to the game thread, and starting a thread publishes everything written before the start to
     * it.
     *
     * <p>Field gameEngine coded before 260813, commented in full on 261010.
     */
    private GameEngine gameEngine;

    /**
     * The core's pair of channel ends: the sender it reports to the other half on,
     * and the receiver {@link #gameLoop()} is a loop over.
     *
     * <p>The receiving end is this class's whole knowledge of the front end - the
     * loop waits on it and never calls the UI at all. The sending end goes out
     * three times over: directly, for the shutdown reply, and handed to both
     * {@code InitHandlers} and {@code RedrawHandlers}, so the start-up narration
     * the one produces and the redraw events the other forwards both leave by the
     * same queue. All three routes are this one sender. Stage 4 made that
     * true - the front end used to hold a copy of the sender it had no business
     * holding - and stage 5 removed the last indirection on the second route, so the
     * handlers now send on it themselves rather than through a registered display.
     *
     * <p>Handed in at construction, on {@code main}'s thread, and read on the game
     * thread; safe without {@code volatile} because starting a thread publishes
     * everything written before it.
     *
     * <p>Reached from outside through {@link #getCoreChannel()}, which is how the data load finds
     * the inbox it waits on.
     *
     * <p>Field coreChannel coded before 260813, commented in full on 261010.
     */
    private CoreChannel coreChannel;

    /**
     * The parsed command line, handed in by {@code main()} so the core has its own copy rather
     * than reading one the front end owns.
     *
     * <p>Nothing reads it yet. The savefile group is what this half will want -
     * {@code selectSavefile}, {@code startNewCharacter} and {@code useSpecificCharacter} all decide
     * what happens before the first turn, which is Chapter 3's work. {@code requestGraphicsMode}
     * belongs to the other half and is carried here only because both halves get the whole record.
     *
     * <p>The C counterpart is what {@code main()} ({@code src/main.c}) works out from {@code argv}
     * before it calls {@code play_game()}: the {@code new_game} and {@code select_game} locals,
     * which choose the {@code game_mode_type} passed in, and the savefile name set through
     * {@code savefile_set_name()}.
     *
     * <p>Field startupOptions coded before 260813, commented in full on 261010.
     */
    private StartupOptions startupOptions;

    /**
     * Put the game engine in {@link #gameEngine}, building the singleton on first call.
     *
     * <p>An instance method wrapping a static call, so the dependency on
     * {@link GameEngine#getGame()} is reachable by name and could be replaced without touching the
     * singleton itself.
     *
     * <p><b>Caveat, and it is a real one.</b> This assigns the field rather than returning the
     * engine, which means a subclass overriding it cannot install a stand-in: {@link #gameEngine}
     * is private, so the override has nothing it can write to, and {@link #gameLoop()} would find
     * the field still null. The boundary is nominal until either the field is made visible to
     * subclasses or this hands the engine back instead of storing it.
     *
     * <p>Method getGameEngine coded before 260813, commented in full on 261010.
     */
    public void getGameEngine() {
        gameEngine = GameEngine.getGame();
    }

    /**
     * Build the core around the channel ends and the options {@code main()} gives it.
     *
     * <p>Only assembles state: no thread is started, no engine is built and no message is sent
     * until {@link #gameLoop()} runs on the thread {@code main()} wraps this in. That split is what
     * lets construction happen on one thread and everything else on another.
     *
     * <p>Constructor Core coded before 260813, commented in full on 261010.
     *
     * @param coreChannel    the core's pair of channel ends - its inbox and its way of replying
     * @param startupOptions the parsed command line
     */
    public Core(CoreChannel coreChannel, StartupOptions startupOptions) {
        this.coreChannel = coreChannel;
        this.startupOptions = startupOptions;
    }

    /**
     * The game thread's body: load the data, then block on the core's inbox and act
     * on what the front end sends, until it sends the message that ends the loop.
     *
     * <p>The port of C's {@code play_game()} ({@code src/ui-game.c}), at the stage
     * where only its skeleton exists. C initializes and then alternates between
     * fetching a command and running the game world; this initializes and then waits
     * for messages, which is the same shape with a queue where C has a blocking call
     * into the display module. The alternation itself is Chapter 5's.
     *
     * <p><b>What of {@code play_game()} is not here yet.</b> C opens with a switch on the
     * {@code game_mode_type} - {@code GAME_LOAD}, {@code GAME_NEW} or {@code GAME_SELECT} - and
     * calls {@code start_game()} to load a savefile or birth a character, which is Chapter 3. Its
     * inner loop runs while the player is neither dead nor finished playing, calling
     * {@code pre_turn_refresh()}, the command hook and {@code run_game_loop()} each time round
     * (Chapter 5). It ends with {@code close_game()}, which is the save of Chapter 8, and with the
     * {@code play_again} branch that tears the game down and starts over, which has no equivalent
     * because the thread simply returns. In C the data load belongs to {@code main()}, which calls
     * {@code init_display()}, {@code init_angband()} and {@code textui_init()} before
     * {@code play_game()}; here it is the first thing the method does, in the same order relative
     * to the handlers.
     *
     * <p><b>The statements before the loop are the core's whole set-up, in the only order that
     * works.</b> The engine is built first, because building it replaces the event bus. Both
     * {@code InitHandlers} and {@code RedrawHandlers} are then constructed around the core's
     * sender and subscribed to that bus, before the load: {@code InitHandlers} has to be, because
     * {@code EVENT_ENTER_INIT} is raised from inside the load and a handler registered afterwards
     * would miss it, leaving the title screen blank and the notes going nowhere; {@code
     * RedrawHandlers} has no such deadline of its own - {@code EVENT_HP} is not signalled until
     * play begins - but is wired alongside it for the same reason and at no cost. Only then does
     * {@code loadGameConstants()} produce the events {@code InitHandlers} carries across.
     *
     * <p><b>The load is not a straight run.</b> Part-way through, {@code GameConstants.init(Core)}
     * waits on this core's inbox for the front end's {@code UIEntriesLoaded} message and passes
     * anything else it reads to {@link #handleChannelOutput(UIMessage)}. If that call says to
     * stop, the load is abandoned and {@code loadGameConstants()} returns {@code true}; the
     * {@code STOPPED} reply has already been sent by then, so returning without sending anything
     * further is the clean exit. {@link #gameConstantsFinishedLoading} is set only on the
     * {@code false} return, so a {@code START} that is handled inside the load sees it unset.
     *
     * <p>Nothing keeps either the {@code InitHandlers} or the {@code RedrawHandlers} instance
     * after this method drops its local, and nothing needs to: subscribing hands the bus a bound
     * method reference per handler, and each of those holds the object.
     *
     * <p>The engine is built here rather than by the caller, replacing a two-call sequence the
     * caller had to know about, where calling this method alone dereferenced a null field. The null
     * check would leave a pre-supplied engine alone, but nothing can supply one today:
     * {@link #gameEngine} is private and {@link #getGameEngine()} assigns it rather than returning
     * it, which is the caveat recorded on that method.
     *
     * <p><b>Blocked, not spinning.</b> {@code receive()} parks the thread until
     * something arrives, so an idle game costs no CPU at all - the arrangement the
     * placeholder {@code sleep} loop this replaced was standing in for. The flip side
     * is that the thread is unresponsive to anything that is not a message, which is
     * the point: there is no longer any other way to reach it.
     *
     * <p><b>The shutdown handshake, core half.</b> {@link #handleChannelOutput(UIMessage)}
     * answers {@code SAVE_AND_STOP} with {@code STOPPED} and reports that the loop should
     * end, in that order, so the reply is on the queue before this thread stops existing. The
     * front end is waiting for that reply before it disposes anything, which is what makes "the
     * save finished before the window went" a guarantee rather than a race - and why nothing here
     * calls {@code System.exit}. The thread simply returns; the JVM exits when the last non-daemon
     * thread does. (There is nothing to save yet. When there is, it goes between the send and the
     * return in {@code handleChannelOutput}, and the guarantee is already in place for it.)
     *
     * <p>An interrupt is treated as a hard stop rather than as a request: nothing
     * interrupts this thread today, so an interrupt means something unexpected has
     * happened, and the loop logs and gives up rather than pretending it can carry
     * on. Note that it gives up <em>without</em> sending {@code STOPPED} - the UI
     * would then wait forever, which is a real gap and the reason interrupting this
     * thread is not part of any shutdown path.
     *
     * <p>Method gameLoop coded before 260813, commented in full on 261010.
     */
    public void gameLoop() {
        if (gameEngine == null)
            getGameEngine();

        CoreSender coreSender = coreChannel.coreSender();

        InitHandlers initHandlers = new InitHandlers(coreSender);
        initHandlers.initHandlers();

        RedrawHandlers redrawHandlers = new RedrawHandlers(coreSender);
        redrawHandlers.initHandlers();

        if (gameEngine.loadGameConstants(this)) {
            return;
        } else {
            gameConstantsFinishedLoading = true;
        }

        while (true) {
            try {
                UIMessage uiMessage = coreChannel.coreReceiver().receive();
                if (handleChannelOutput(uiMessage)) {
                    return;
                }
            } catch (InterruptedException e) {
                // Nothing interrupts this thread, so reaching here means something unexpected
                // did. Give up rather than continue - but note the UI is left waiting.
                logger.error("Game loop interrupted");
                return;
            }
        }
    }

    /**
     * Act on one message from the front end, and say whether the game thread should now end.
     *
     * <p>The one place the core's inbox is interpreted. It is public because two callers need it:
     * the loop in {@link #gameLoop()} and the data load, which reads the same inbox while it waits
     * for {@code UIEntriesLoaded} and passes every other message here. Both treat {@code true} as
     * "stop at once" - the loop returns, the load abandons its work and reports the same to
     * {@code gameLoop()}.
     *
     * <p>What each message does today:
     * <ul>
     *   <li>{@code LifecycleUIMessage(SAVE_AND_STOP)} sends {@code STOPPED} on the core's sender
     *       and returns {@code true}. The send completes before the return, so the reply is on the
     *       queue before the caller ends the thread.</li>
     *   <li>{@code LifecycleUIMessage(START)} is logged and does nothing else; birth lands here in
     *       Chapter 3. The branch for a {@code START} that arrives before the load has finished is
     *       an empty placeholder, so such a message is lost rather than held over.</li>
     *   <li>{@code WindowCloseRequested}, {@code SimpleUIMessage} and {@code UIEntriesLoaded} are
     *       ignored. The first is the EDT's, posted to the UI thread's inbox and never expected
     *       here; the second is a bus event the loaders use, and means nothing to the core; the
     *       third is consumed by the load before it could reach this method, and is ignored if it
     *       arrives later.</li>
     * </ul>
     *
     * <p>The outer switch is over the sealed {@code UIMessage} with type patterns, so a new record
     * added to the protocol stops this method compiling. The inner switch over
     * {@code UILifecycleEvent} has no such protection - an arrow-form switch statement over an enum
     * is not checked for exhaustiveness - so a new lifecycle constant would be ignored without a
     * warning.
     *
     * <p>There is no C counterpart. C is single-threaded and reads its input by calling the
     * command hook from inside {@code play_game()}; a message handed across a queue is the port's
     * own mechanism.
     *
     * <p>Method handleChannelOutput coded before 261010, commented in full on 261010.
     *
     * @param uiMessage the message just read from the core's inbox
     * @return {@code true} if the caller should end the game thread, {@code false} to carry on
     */
    public boolean handleChannelOutput(UIMessage uiMessage) {
        // Exhaustive over the sealed UIMessage, so a new record added to the protocol
        // breaks this switch at compile time rather than being silently ignored at run
        // time. The inner switch over UILifecycleEvent is NOT checked: an arrow-form
        // statement switch over an enum need not be exhaustive, so a new constant there
        // would fall through unnoticed.
        switch (uiMessage) {
            case UIMessage.LifecycleUIMessage lifecycleUIMessage -> {
                logger.info("Received {}", lifecycleUIMessage.event());

                switch (lifecycleUIMessage.event()) {
                    // Reply first, then leave: the STOPPED must be on the queue before
                    // this thread ends, because it is what releases the front end to
                    // dispose the windows. The save goes above the send, in Chapter 8.
                    case SAVE_AND_STOP -> {
                        coreChannel.coreSender()
                                .send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
                        return true;
                    }
                    // Nothing to do with it yet - the window is up and the data is
                    // already loaded by the time it arrives. This is where character
                    // birth goes in Chapter 3.
                    case START -> {
                        if (!gameConstantsFinishedLoading) {
                            // Send a new message to tell the UI to wait and resend the START command?
                        }
                        logger.debug("Start message received");
                    }
                }
            }
            // Not ours: the EDT posts these to the UI thread's inbox, not to this one.
            // The arm exists to make the switch exhaustive, and ignores rather than
            // failing because a raw window event means nothing to the core in any case.
            case UIMessage.WindowCloseRequested ignored -> {
            }

            case UIMessage.SimpleUIMessage simpleUIMessage -> {
                if (simpleUIMessage.type().equals(GameEventType.EVENT_ENTER_INIT)) {
                    // Ignore, we are not handling this - it is there for the loaders
                    // of the UI Entry classes.
                }
            }

            // The arm exists to make the switch exhaustive
            case UIMessage.UIEntriesLoaded ignored -> {
            }
        }

        return false;
    }

    /**
     * The core's pair of channel ends, as handed to the constructor.
     *
     * <p>Exists for {@code GameConstants.init(Core)}, which reads the inbox through it while the
     * data load waits for the front end's {@code UIEntriesLoaded} message. The same instance every
     * call; nothing here copies it.
     *
     * <p>Method getCoreChannel coded before 261010, commented in full on 261010.
     *
     * @return the channel ends this core was built with
     */
    public CoreChannel getCoreChannel() {
        return coreChannel;
    }
}
