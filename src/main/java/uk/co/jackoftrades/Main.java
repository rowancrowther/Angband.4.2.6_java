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

import uk.co.jackoftrades.backend.io.AngDir;
import uk.co.jackoftrades.frontend.Frontend;
import uk.co.jackoftrades.middle.game.gameengine.GameRunner;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The entry point: parse the command line, then either answer it and stop, or hand the parsed
 * options to the front end and get out of the way. This is the port of {@code main()}
 * ({@code [C] src/main.c}).
 *
 * <p>Where C's {@code main()} goes on to call {@code play_game()} and blocks there for the whole
 * session, this one returns almost immediately. Everything after argument parsing happens on
 * other threads - the front end on Swing's event dispatch thread (EDT), the game loop on the
 * thread {@link GameRunner} owns - so {@code main} returning is the normal path, not a shutdown.
 * The JVM stays up because a shown window keeps AWT's non-daemon EDT alive.
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
    /**
     * The parsed command line, handed to {@link Frontend#init} by {@link #startFrontend}.
     *
     * <p>Static because the {@link Runnable} below runs later, on the EDT, after {@code main} has
     * returned and its locals are gone. Publishing it is safe without further synchronisation:
     * {@link SwingUtilities#invokeLater} establishes a happens-before edge, so the EDT is
     * guaranteed to see the write made just above the call.
     *
     * @author Rowan Crowther
     */
    private static StartupOptions options;

    /**
     * Parse {@code args}, then start the front end - unless an argument asked a question instead,
     * in which case the answer is displayed and this returns without starting anything.
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
     * @author Rowan Crowther
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
                    String dirString = arg.substring(2);
                    String[] dirs = dirString.split("=");
                    // Check that the save file is correctly named
                    if (dirs.length != 2) {
                        System.out.println("Error: invalid directory path incorrect number of parameters supplied");
                        System.exit(1);
                    }
                    if (!AngbandDirs.ANGBAND_DIRS.contains(dirs[0])) {
                        System.out.println("Error: invalid directory path unknown directory name");
                        System.exit(1);
                    }
                    if (!Paths.get(dirs[1]).toFile().exists()) {
                        System.out.println("Error: invalid directory path does not exist");
                        System.exit(1);
                    }
                    AngbandDirs.setDirectory(dirs[0], dirs[1]);
                }

                // For future development Modules (if needed) and Sound.

                default -> {
                    printUsage();
                    return;
                }
            }
        }

        Main.options = new StartupOptions(selectSavefile, startNewCharacter,
                resurrectDeadCharacter, requestGraphicsMode, useSpecificCharacter,
                "", new ArrayList<>());

        SwingUtilities.invokeLater(startFrontend);
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
     *
     * @author Rowan Crowther
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
     * Builds the front end and its game-thread owner, and starts them. Handed to
     * {@link SwingUtilities#invokeLater} so all of it happens on the EDT, which is the only thread
     * allowed to touch Swing components once they are realised.
     *
     * <p>This is the whole of the port's start-up wiring: a {@link GameRunner} to own the game
     * thread, a {@link Frontend} given that runner as its single handle on the middle end, and
     * {@code init} to build the window and start the loop. It reads {@link #options} rather than
     * taking a parameter because {@code Runnable} has none.
     *
     * @author Rowan Crowther
     */
    static Runnable startFrontend = new Runnable() {
        @Override
        public void run() {
            GameRunner gameRunner = new GameRunner();
            Frontend frontend = new Frontend(gameRunner);
            frontend.init(options);
        }
    };

    /**
     * List the savefiles the player could load - the {@code -l} switch. Reads the save directory
     * through {@link AngDir}, which yields plain files one at a time and skips sub-directories,
     * and shows the names in a window.
     *
     * <p>Asks {@code ANGBAND_DIRS.SAVE} for its path rather than reading a constant, so a
     * {@code -d} earlier on the same command line is honoured.
     *
     * @throws IOException if the save directory cannot be read
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * {@code Frontend} builds.
     *
     * @author Rowan Crowther
     */
    private static class OutputWindow extends JFrame {

    }
}