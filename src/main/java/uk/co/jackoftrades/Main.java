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

package uk.co.jackoftrades;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.VisibleForTesting;
import uk.co.jackoftrades.backend.io.AngDir;
import uk.co.jackoftrades.channel.*;
import uk.co.jackoftrades.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.messages.CoreMessage;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.frontend.SwingUI;
import uk.co.jackoftrades.middle.game.gameengine.Core;
import uk.co.jackoftrades.channel.directories.AngbandDirs;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The entry point: parse the command line, then either answer it and stop, or build the two halves
 * of the game, start them, and wait for both to finish. This is the port of {@code main()}
 * ({@code [C] src/main.c}).
 *
 * <p>Where C's {@code main()} goes on to call {@code play_game()} and blocks there for the whole
 * session, this one blocks in {@link Thread#join()} instead - which is the same shape from a
 * distance and a different thing underneath. C is waiting for the game because it <em>is</em> the
 * game; this waits for two threads it owns and does no game work itself, holding neither channel
 * end after it has handed them out.
 *
 * <p><b>Waiting is the point, not politeness.</b> The last thing the core does before its thread
 * ends is answer the shutdown handshake, so a {@code main} that returned early would let the JVM
 * race the save. Joining both is what turns "the save finished before the process did" from a
 * hope into a guarantee - and it is why nothing on either half calls {@link System#exit}: the
 * program ends by running out of threads.
 *
 * <p>This is also the only class that names both halves. {@link SwingUI} and {@link Core}
 * are imported here and nowhere across the boundary from each other; everything they say to one
 * another goes through the {@code channel} package.
 *
 * <p>Two kinds of argument, and the difference decides how the method ends. Most switches only
 * set a value and fall through to the launch at the bottom. {@code -l} and anything unrecognised
 * are questions rather than settings: they put their answer in a window and {@code return}, so
 * the game never starts. Those windows close the process themselves through
 * {@code EXIT_ON_CLOSE}, which is why returning is enough.
 *
 * @author Rowan Crowther
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    
    /**
     * The UI thread's body: build the front end, put its window up on the EDT, and then become the
     * loop that reads this half's inbox.
     *
     * <p><b>A method that returns a {@code Runnable}, not a {@code Runnable} itself.</b>
     * {@code run()} takes no arguments, so anything the body needs has to be state it carries; a
     * lambda built here carries it by capturing these three parameters. That is what let the
     * options stop being a static field read from the EDT and become an ordinary argument.
     *
     * <p><b>Two threads, and the {@code invokeLater} is the join between them.</b> Only the EDT may
     * touch realised Swing components, so {@link SwingUI#init()} is queued onto it - but
     * {@link SwingUI#startLoop()} blocks for the whole session, and blocking the EDT would freeze
     * the window it just built. So the bootstrap goes to the EDT and the loop stays here, on the
     * thread this runnable is given to.
     *
     * <p>Nothing waits for the queued {@code init} to finish; the loop starts immediately and could
     * in principle receive a message before the window exists. It cannot in practice, and the
     * reason is the handshake rather than luck: the core sends nothing until it is told to start,
     * and {@code init} is what tells it, on its last line.
     *
     * <p><b>The {@code catch} is this half's crash path, and it is a {@code catch} rather than an
     * {@link Thread.UncaughtExceptionHandler} for one plain reason: scope.</b> A handler installed
     * on the thread in {@code main} would have no front end to shut down - {@code swingUI} is a
     * local of this lambda, built after {@code main} has handed the work over. Catching here also
     * means the thread ends by returning rather than by throwing, so there is nothing left for a
     * handler to do. The core's half of the same problem has the opposite shape and so takes the
     * opposite answer; see {@link #main}.
     *
     * <p>A dead UI half owes two messages, and the {@code catch} and the {@code finally} send one
     * each. {@code SAVE_AND_STOP} is what unparks the core, which would otherwise sit in
     * {@code receive()} forever waiting for a front end that no longer exists; nothing waits for
     * the {@code STOPPED} that comes back, and it simply rests unread on an inbox whose reader has
     * gone. Disposing the windows is the other, and it is what actually ends the process: the EDT
     * is kept alive by displayable windows, so until they go the JVM outlives both halves - a
     * running program with nothing running in it.
     *
     * <p>The disposal is in a {@code finally} because it is owed on the clean path too, where
     * {@code startLoop()} returns normally after {@code UILoop} has already queued a
     * {@code closeDown} of its own. Two disposals are harmless - {@code dispose()} on a disposed
     * window does nothing, which {@code SwingUITest.closeDownIsSafeToRepeat} pins - and a missing
     * one hangs the program, so the duplicate is the right way to be wrong.
     *
     * <p>Two limits worth knowing before they bite. Only {@link RuntimeException} is caught, so an
     * {@link Error} - {@code OutOfMemoryError}, a stack overflow - still leaves by the default
     * handler and still hangs; that is a considered line rather than an oversight, since a JVM in
     * that state cannot be relied on to send anything. And the disposal only releases the EDT if
     * <em>every</em> displayable window goes: {@link SwingUI#closeDown()} walks the front end's own
     * list, which today holds the single game window, but the {@code -l} and usage frames built by
     * {@link #displayText} are not in it and neither would a dialog be.
     *
     * @param uiChannel      the UI thread's pair of ends - its inbox, and its way of reaching the
     *                       core
     * @param edtChannel     the send-only end the window listener posts close requests on
     * @param startupOptions the parsed command line
     * @return the body for the {@code angband-ui} thread
     */
    private static Runnable startSwingUI(UIChannel uiChannel, EDTChannel edtChannel,
                                         StartupOptions startupOptions) {
        return () -> {
            SwingUI swingUI = new SwingUI(uiChannel, edtChannel, startupOptions);
            try {
                SwingUtilities.invokeLater(swingUI::init);
                swingUI.startLoop();
            } catch (RuntimeException e) {
                logger.fatal("angband-ui died.", e);
                uiChannel.uiSender().send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    swingUI.closeDown();
                });
            }
        };
    }

    /**
     * The core thread's body: build the core and hand it the thread for the rest of the session.
     *
     * <p>Two lines where the front end used to be three, and where this method used to be. The
     * core's set-up moved inside {@link Core#gameLoop()}, so there is no longer an ordering
     * for a caller to get wrong - which is the difference between this lambda and the UI's. That
     * one coordinates two objects and an EDT hop; this one just names where the core's thread
     * begins.
     *
     * @param coreChannel    the core's pair of ends - its inbox, and its way of reaching the UI
     * @param startupOptions the parsed command line
     * @return the body for the {@code angband-core} thread
     */
    private static Runnable startCore(CoreChannel coreChannel, StartupOptions startupOptions) {
        return () -> {
            Core core = new Core(coreChannel, startupOptions);
            core.gameLoop();
        };
    }
    
    /**
     * Parse {@code args}, then start both halves and wait for them - unless an argument asked a
     * question instead, in which case the answer is displayed and this returns without starting
     * anything.
     *
     * <p><b>The last dozen lines are the whole of the port's start-up wiring.</b> One
     * {@link Channels} set is created, which is the only place the two queues come into existence
     * and the only way a sender and its matching receiver can be guaranteed to be looking at the
     * same one. Each half is then given exactly the ends it is entitled to and nothing more - the
     * UI gets both of its own plus the EDT's send-only view, the core gets its pair - and after
     * that neither can reach the other except by sending.
     *
     * <p>Both threads are named, which costs nothing and is worth it the first time a stack trace
     * or a thread dump has to be read: {@code angband-ui} and {@code angband-core} say which half
     * is stuck without anyone having to work it out from the frames.
     *
     * <p><b>The handler is the crash path, and it exists because the handshake cannot cover its own
     * failure.</b> The shutdown is an exchange - the core answers {@code SAVE_AND_STOP} with
     * {@code STOPPED} - and a half that dies by exception never gets to take its turn, leaving the
     * other half parked on a queue nothing will ever arrive on. So the core's handler sends the
     * {@code STOPPED} its thread did not live to send, and a crash leaves by the shutdown path that
     * already exists rather than by a second one written for the purpose.
     *
     * <p>It is installed <em>before</em> {@link Thread#start()}, which is the whole reason those
     * two lines are separated. A handler set afterwards is a race: a thread that dies in its first
     * instruction dies before its handler exists, and the default handler prints to
     * {@code System.err} instead.
     *
     * <p><b>Only the core is handled here, and the asymmetry is deliberate.</b> The UI half needs
     * to dispose its windows as well as unpark its opposite number, and the front end it must
     * dispose is a local inside {@link #startSwingUI}'s lambda that this method never sees - so
     * that half is caught where it can be acted on, which is also why its thread ends by returning
     * rather than by throwing. Neither shape is the "right" one: each is where the thing that needs
     * shutting down happens to be in scope.
     *
     * <p>What no handler here can do is rescue the EDT. It is not a thread this method started, so
     * it cannot be named, joined or handled; it ends only when the last displayable window is
     * disposed. That is why the UI half's shutdown is the one that actually ends the process, and
     * why a crash on that side that skipped the disposal would hang the JVM around a dead program.
     *
     * <p>The order of the two {@code join} calls does not matter. Neither thread can finish before
     * the other has played its part in the handshake, so whichever is waited on first, the second
     * has either already ended or is about to.
     *
     * <p>An interrupt while joining is logged and returns, which leaves the two threads running
     * with nobody waiting for them - the program would then end whenever they do, without the
     * guarantee the joins exist to provide. Nothing interrupts this thread today, so it is a
     * recorded gap rather than a live one.
     *
     * <p>The loop rewrites anything that is not a switch (too short, or not starting with
     * {@code -}) to {@code "-h"} so it falls into {@code default} and prints the usage, which is
     * how C's {@code main()} treats a stray word too.
     *
     * <p>{@code -d} is applied here rather than being carried in {@link StartupOptions}, because
     * a directory override has to be in force before anything opens a file under it - the same
     * reason C rewrites its {@code ANGBAND_DIR_*} buffers before {@code init_angband()}. Each one
     * is validated three ways (well-formed, a known directory name, an existing path) before it
     * is applied.
     *
     * @param args the raw command line
     * @throws IOException if {@code -l} cannot read the save directory
     */
    public static void main(String[] args) throws IOException {
        // Declare the start option defaults
        boolean selectSavefile = false;
        boolean startNewCharacter = false;
        boolean resurrectDeadCharacter = false;
        boolean requestGraphicsMode = false;
        String useSpecificCharacter = "";

        for (String arg : args) {
            if (arg.length() < 2 || arg.charAt(0) != '-') arg = "-h";
            switch (arg.charAt(1)) {
                case 'c' -> selectSavefile = true;
                case 'n' -> startNewCharacter = true;
                case 'l' -> {
                    listSaves();
                    return;
                }
                case 'w' -> resurrectDeadCharacter = true;
                case 'g' -> requestGraphicsMode = true;
                case 'u' -> {
                    useSpecificCharacter = arg.substring(2);
                    if (useSpecificCharacter.isEmpty()) {
                        printUsage();
                        return;
                    }
                }
                case 'd' -> {
                    String error = checkDirectoryOption(arg);
                    if (error != null) {
                        logger.fatal(error);
                        System.err.println(error);
                        System.exit(1);
                    }
                    String dirs[] = arg.substring(2).split("=", 2);
                    AngbandDirs.setDirectory(dirs[0], dirs[1]);
                }

                // For future development Modules (if needed) and Sound.

                default -> {
                    printUsage();
                    return;
                }
            }
        }

        StartupOptions options = new StartupOptions(selectSavefile, startNewCharacter,
                resurrectDeadCharacter, requestGraphicsMode, useSpecificCharacter,
                "", new ArrayList<>());

        Channels channels = Channels.create();

        Thread uiThread = new Thread(startSwingUI(channels.uiChannel(), channels.edtChannel(), options), "angband-ui");
        Thread coreThread = new Thread(startCore(channels.coreChannel(), options), "angband-core");

        coreThread.setUncaughtExceptionHandler((t, e) -> {
            logger.fatal("{} died.", t.getName(), e);
            channels.coreChannel().coreSender().send(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED));
        });

        // The UIThread catch of a dead thread without a closedown message is in startSwingUI. 

        uiThread.start();
        coreThread.start();

        try {
            uiThread.join();
            coreThread.join();
        } catch (InterruptedException e) {
            logger.error("Unable to join threads", e);
        }
    }

    @VisibleForTesting
    static String checkDirectoryOption(String arg) {
        String[] dirs = arg.substring(2).split("=", 2);
        if (dirs.length != 2)
            return "Error: invalid directory path '" + arg + "'. Should be '-d<dir>=<path>'.";
        if (dirs[1].isEmpty())
            return "Error: empty directory path, expected '-d<dir>=<path>', received '-d<dir>='";
        if (!AngbandDirs.ANGBAND_DIRS.contains(dirs[0]))
            return "Error: invalid directory path unknown directory name: " + dirs[0];
        if (!Paths.get(dirs[1]).toFile().isDirectory())
            return "Error: invalid directory path " + dirs[1] + " is not a directory.";
        // No errors - return null to signal this, as opposed to an empty string which would signal an error
        return null;
    }

    /**
     * Show the {@code -} switches and stop being useful - the port of C's {@code usage()}
     * ({@code [C] src/main.c}). Reached by {@code -h}, by any unknown switch, and by a {@code -u}
     * with no name after it.
     *
     * <p>The directory lines are generated from {@code ANGBAND_DIRS} rather than typed out, so a
     * new directory appears in the usage text by existing. They print the paths <em>live</em>, so
     * an earlier {@code -d} on the same command line is reflected in what is shown.
     *
     * <p>The sound and module lines are commented out because neither option is parsed yet;
     * they are left in place as the shape of what C prints there.
     */
    private static void printUsage() {
        List<String> output = new ArrayList<>();
        output.add("Usage: angband [options] [-- subopts]");
        output.add("  -c             Select savefile with a menu; overrides -n");
        output.add("  -n             Start a new character (WARNING: overwrites default savefile without -u)");
        output.add("  -l             Lists all savefiles you can play");
        output.add("  -w             Resurrect dead character (marks savefile)");
        output.add("  -g             Request graphics mode");
        output.add("  -u<who>        Use your <who> savefile");
        output.add("  -d<dir>=<path> Override a specific directory with <path>. <path> can be:");

        for (AngbandDirs.ANGBAND_DIRS dir : AngbandDirs.ANGBAND_DIRS.values()) {
            output.add(String.format("    %s (default is %s)", dir.getName(), dir.getPath()));
        }

        output.add("                 Multiple -d options are allowed.");
        //      System.out.println("  -s<mod>        Use sound module <sys>:");
        //      printSoundHelp();
        //      System.out.println("  -m<sys>        Use module <sys>, where <sys> can be:");

        displayText(output);
    }

    /**
     * List the savefiles the player could load - the {@code -l} switch. Reads the save directory
     * through {@link AngDir}, which yields plain files one at a time and skips sub-directories,
     * and shows the names in a window.
     *
     * <p>Asks {@code ANGBAND_DIRS.SAVE} for its path rather than reading a constant, so a
     * {@code -d} earlier on the same command line is honoured.
     *
     * @throws IOException if the save directory cannot be read
     */
    private static void listSaves() throws IOException {
        List<String> saves = new ArrayList<>();

        AngDir saveDirectory = new AngDir(AngbandDirs.ANGBAND_DIRS.SAVE.getPath());

        String nextFile = saveDirectory.read();
        while (!nextFile.isEmpty()) {
            saves.add(nextFile);
            nextFile = saveDirectory.read();
        }

        displayText(saves);
    }

    /**
     * Put one line per entry in a scrollable, read-only, monospaced window and show it. The
     * shared back end of {@link #printUsage()} and {@link #listSaves()} - the port's stand-in for
     * C printing to the terminal, which is not available once the game owns the display.
     *
     * <p>Monospaced and unwrapped because the usage text is column-aligned and would be nonsense
     * reflowed; both scrollbars appear as needed so long directory paths stay readable.
     *
     * <p>{@code EXIT_ON_CLOSE} is doing real work here, not just tidying up. Both callers
     * {@code return} straight after this, so nothing else will ever end the process: closing this
     * window <em>is</em> how the program terminates. A close operation that merely hides the
     * window would leave the JVM alive on the EDT with nothing on screen.
     *
     * <p>Built on the calling thread rather than the EDT, which is against Swing's usual rule.
     * It is the benign case: this is only reached on paths that never start the front end, so no
     * other thread ever touches these components, and nothing here is read again after
     * {@code setVisible}.
     *
     * @param messages the lines to show, in order
     */
    private static void displayText(List<String> messages) {
        OutputWindow window = new OutputWindow();

        // create the window stats
        window.setTitle("Angband 4.2.6 - initial details");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        StringBuilder text = new StringBuilder();
        for (String message : messages) {
            text.append(message).append("\n");
        }
        textArea.setText(text.toString());
        window.setContentPane(scrollPane);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    /**
     * The frame {@link #displayText} fills. A bare {@link JFrame} subclass that adds nothing yet;
     * it exists to give these pre-game windows a name of their own, separate from the game window
     * {@code SwingUI} builds.
     *
     * @author Rowan Crowther
     */
    private static class OutputWindow extends JFrame {

    }
}