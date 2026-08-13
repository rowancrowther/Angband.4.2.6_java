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

package uk.co.jackoftrades.frontend.inputfromuser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.jackoftrades.channel.UIChannel;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.messages.ChannelMessage;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.SwingUI;
import uk.co.jackoftrades.frontend.events.BirthEvents;
import uk.co.jackoftrades.frontend.events.MainEvents;
import uk.co.jackoftrades.frontend.splash.SplashScreen;
import uk.co.jackoftrades.channel.directories.AngbandDirs;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The UI half's body: the loop that drains this half's inbox and turns each message the core sent
 * into painting. This is the consumer of the migration's producer-consumer pair (see
 * {@code docs/primers/producer-consumer.md}), and the reason the core no longer calls the front end
 * at all - it puts a message on a queue and moves on.
 *
 * <p><b>No C counterpart, and there could not be one.</b> C is single-threaded: {@code game-event.c}
 * dispatches straight into the {@code ui-*.c} handler, on the game's own thread, and the drawing
 * happens inside the signal call. This loop is what that arrangement becomes once the two halves are
 * separate threads - the dispatch still happens core-side, but what it produces is a message, and
 * this is where it is picked up again.
 *
 * <p><b>Three threads, and this is the middle one.</b> It runs on {@code angband-ui} - the thread
 * {@code main()} starts and {@code SwingUI.startLoop} hands over to - which is neither the game
 * thread nor Swing's event dispatch thread. Both of those matter: {@code receive()} blocks, so running this on the EDT would freeze
 * the window for the whole data load (see {@code docs/primers/edt-and-invokelater.md}), and painting
 * touches Swing components, so every paint below has to hop onto the EDT. That hop is not made here
 * - {@code Window.display} and {@code SplashScreen}'s painting methods each do their own
 * {@code invokeLater}, so the hop is a property of the painting call rather than something this loop
 * has to remember.
 *
 * <p><b>The ordering constraint the bus has, this does not.</b> A message sent before this loop
 * starts simply waits on the queue: the channel buffers, so there is no "was anyone listening yet?"
 * window of the kind that makes {@code InitHandlers}' registration timing load-bearing core-side.
 *
 * <p><b>Two writers, one inbox, and that is what the class is for.</b> The core sends events to be
 * painted; the EDT sends window events it is not allowed to act on itself. Both land on the same
 * queue, and this loop is the one thread that reads it - so it is also the one place where a raw
 * AWT event becomes a message the core understands. The package name, {@code inputfromuser}, was
 * chosen for that half of the job before it existed; the close handler below is its first member,
 * and keypresses join it in Chapter 5.
 *
 * @author Rowan Crowther
 */
public class UILoop {
    /**
     * Logger for messages that arrive out of order or on the wrong queue - the failures that are
     * otherwise invisible, because an unhandled message is simply dropped.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * This half's pair of channel ends: the receiver the loop blocks on, and the sender it answers
     * the core with.
     *
     * <p>Named for the half that owns it, not for a direction - the messages read from
     * {@code uiChannel.uiReceiver()} are the ones the <em>core</em> sent. Handed in rather than
     * created here, because a channel built at the point of use is a channel with no other end;
     * {@code Channels} is the one place a matched pair comes from.
     *
     * @author Rowan Crowther
     */
    private UIChannel uiChannel;

    /**
     * The front end this loop paints through, used for its active window.
     *
     * <p>A concrete class rather than a boundary, which is the one thing here stage 5 is expected to
     * change: with a painting interface in its place, this loop could be tested without a live Swing
     * window, which is why the message-to-paint hop is the one part of stage 2 that has no test.
     *
     * @author Rowan Crowther
     */
    private SwingUI swingUI;

    /**
     * Build the loop around the channel ends it reads and the front end it paints through.
     *
     * <p>Constructed by {@code SwingUI}, whose {@code startLoop()} then hands this loop the UI
     * thread. The channel arrives from outside rather than being built here, which is what keeps
     * this loop's queue the same object the core is sending on.
     *
     * @param uiChannel this half's pair of channel ends
     * @param swingUI   the front end whose active window the messages are painted into
     * @author Rowan Crowther
     */
    public UILoop(UIChannel uiChannel, SwingUI swingUI) {
        this.uiChannel = uiChannel;
        this.swingUI = swingUI;
    }

    /**
     * Report that the {@code lib} directory is unusable and give up. The port of
     * {@code init_angband_aux()} ({@code [C] src/ui-init.c}), which prints the same four lines.
     *
     * <p>C can put this on the terminal it started from; the port has no terminal, so it logs and
     * throws. Nothing catches the exception meaningfully - it surfaces inside
     * {@code GameConstants.init()}'s handler and is re-reported as a data-load failure - so the
     * message the player would most want is the one they are least likely to see.
     *
     * @param why what could not be read, used as the first line of the message
     * @throws RuntimeException always; this method does not return
     * @author Rowan Crowther
     */
    private void initAngbandAux(String why) {
        String message = why + "\n" +
                "The 'lib' directory is probably missing or broken.\n" +
                "Perhaps the archive was not extracted correctly.\n" +
                "See the 'readme.txt' file for more information.";
        logger.fatal(message);
        throw new RuntimeException(message);
    }

    /**
     * Block on the inbox and act on each message until one of them ends the loop. The thread's
     * whole body.
     *
     * <p>The {@code switch} is over the sealed {@code ChannelMessage}, so the compiler checks the
     * arms cover the protocol: a new record added to {@code CoreMessage} makes this fail to compile
     * rather than silently doing nothing when the message arrives. That is the whole reason the
     * protocol is sealed records. The inner switches over {@code GameEventType} are the opposite
     * case and are deliberately partial - 65 constants, most of which the core never sends here -
     * so an unrecognised event is ignored rather than being an error.
     *
     * <p><b>The splash screen is a local, and that is the protocol.</b> Notes are painted onto the
     * same character grid the title screen was parsed into, so a note needs the instance that holds
     * that grid; building a fresh {@code SplashScreen} per note would paint a blank screen with one
     * bracketed line on it. Holding it as a local rather than a field also says how long it lives -
     * one run of the loop - and makes "a note before the title screen" a state this method can see
     * and report, rather than a null field somewhere else.
     *
     * <p><b>Two of the arms are the shutdown handshake, and they are its middle and its end.</b>
     * {@code WindowCloseRequested} is the EDT saying what the player did; this loop turns it into
     * {@code SAVE_AND_STOP} for the core, which is the whole of the translation the EDT was kept
     * from doing itself. {@code STOPPED} is the core's reply, and the only message that ends this
     * loop: the windows go, the method returns, and the thread finishes. Ending by returning
     * rather than by being killed is the point of the exchange - see
     * {@code docs/Architecture.md}'s shutdown sequence.
     *
     * <p><b>The last arm is a message that should never arrive.</b> A {@code UIMessage} that is
     * not the close request came from this half and belongs on the core's queue, so it means the
     * wiring is wrong rather than that the sender is early. It throws instead of logging on,
     * because a misrouted message on a queue nobody else reads is lost silently otherwise, and a
     * loop that quietly drops half the protocol is the hardest kind of channel bug to find. The
     * arm is also what makes the {@code switch} exhaustive over {@code ChannelMessage}.
     *
     * <p>An interrupt ends the loop, which is what a thread being interrupted means anywhere it
     * is not being used as a signal - and nothing interrupts this thread, so reaching it at all is
     * a bug worth the log line. Note that the windows are not disposed on that path: an interrupt
     * is a failure, not a shutdown, and the two should not be made to look alike.
     *
     * @author Rowan Crowther
     */
    public void loop() {
        SplashScreen splashScreen = null;

        while (true) {
            try {
                ChannelMessage message = uiChannel.uiReceiver().receive();

                switch (message) {
                    // Protocol rather than gameplay: the core reporting on its own lifecycle.
                    case CoreMessage.LifecycleCoreMessage lifecycleCoreMessage -> {
                        CoreLifecycleEvent event = lifecycleCoreMessage.event();

                        logger.info("Received {}", event);
                        
                        switch (event) {
                            // The core has finished and its thread is ending, so the windows may
                            // now go. The hop onto the EDT is required - dispose() is a Swing call
                            // and this is not the EDT - and invokeLater rather than invokeAndWait
                            // because there is nothing left to wait for: returning ends this
                            // thread, and the disposal runs on its own.
                            //
                            // No System.exit follows, deliberately. Once the windows are gone the
                            // EDT has nothing to keep it alive, so the JVM ends by running out of
                            // threads rather than by being told to.
                            case STOPPED -> {
                                SwingUtilities.invokeLater(() -> {
                                    swingUI.closeDown();
                                });
                                return;
                            }
                        }
                    }

                    // The EDT's report that the player closed the window, turned into the request
                    // the core understands. This is the one place that translation happens, and it
                    // happens on this thread because the EDT must not be the one to wait for what
                    // comes back.
                    case UIMessage.WindowCloseRequested uiMessage -> {
                        UILifecycleEvent event = UILifecycleEvent.SAVE_AND_STOP;

                        logger.info("Received {}", uiMessage.toString());
                        
                        UIMessage uiMessageToSend = new UIMessage.LifecycleUIMessage(event);
                        uiChannel.uiSender().send(uiMessageToSend);
                    }

                    case CoreMessage.SimpleCoreMessage simpleCoreMessage -> {
                        GameEventType eventType = simpleCoreMessage.gameEventType();
                        logger.info("Received {}", eventType);
                        switch (eventType) {
                            // The data load has started. C's show_splashscreen() ([C] src/ui-init.c),
                            // reached the same way: the core signals, the front end decides that
                            // means news.txt on the screen. The path is read at call time so a -d
                            // override on the command line is honoured.
                            case EVENT_ENTER_INIT -> {
                                String filename = AngbandDirs.ANGBAND_DIRS.SCREENS.getPath() + "news.txt";
                                Path path = Paths.get(filename);
                                if (!Files.exists(path)) {
                                    initAngbandAux("Cannot access the " + filename + " file.");
                                }

                                splashScreen = new SplashScreen(swingUI);
                                AngbandDisplayCharacter[][] display = splashScreen.readAndParse(path);
                                swingUI.getActiveWindow().display(display);
                            }
                            case EVENT_LEAVE_INIT -> new MainEvents().leaveInit();
                            case EVENT_ENTER_GAME -> new MainEvents().enterGame();
                            case EVENT_LEAVE_GAME -> new MainEvents().leaveGame();
                            case EVENT_ENTER_WORLD -> new MainEvents().enterWorld();
                            case EVENT_LEAVE_WORLD -> new MainEvents().leaveWorld();
                            case EVENT_ENTER_BIRTH -> new BirthEvents().enterBirth();
                            case EVENT_LEAVE_BIRTH -> new BirthEvents().leaveBirth();
                            default -> {
                            }
                        }
                    }

                    case CoreMessage.TextCoreMessage textCoreMessage -> {
                        GameEventType eventType = textCoreMessage.gameEventType();
                        String eventMessage = textCoreMessage.message();
                        logger.info("Received {}", eventType);
                        switch (eventType) {

                            // A progress note, one per data file. Painted onto the title screen's
                            // own grid, so it needs the instance built above - a note that arrives
                            // first has nowhere to go, and saying so is better than a blank screen
                            // with one line on it.
                            case EVENT_INITSTATUS -> {
                                if (splashScreen == null)
                                    logger.warn("CoreMessage.EVENT_INITSTATUS received before CoreMessage.EVENT_ENTER_INIT");
                                else {
                                    splashScreen.splashScreenNote(eventMessage);
                                }
                            }
                        }
                    }

                    // Anything else this half sent is misrouted: it should have gone to the core's
                    // inbox. Loud rather than ignored - nothing else reads this queue, so a
                    // dropped message here disappears without trace.
                    case UIMessage m -> {
                        logger.info("Received {}", m.toString());
                        
                        logger.warn("UI message on the UI inbox: {}", m);
                        throw new RuntimeException("UI message on the UI inbox");
                    }
                }

            } catch (InterruptedException e) {
                // Not a shutdown path: nothing interrupts this thread, so this is a failure. The
                // windows are left alone - only the STOPPED arm above disposes them.
                logger.error("Interrupted receive in coreChannel", e);
                break;
            }
        }
    }
}
