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

package uk.co.jackoftrades.frontend.splash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.colour.ColourEnum;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.Frontend;
import uk.co.jackoftrades.frontend.screen.Window;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplay;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * The title screen: the front end's {@link StatusDisplay}, which paints {@code lib/screens/news.txt}
 * into the game window while the data files load. The port of {@code ui_enter_init}'s display half
 * ({@code [C] src/ui-display.c}), which C reaches through {@code show_splashscreen()}
 * ({@code [C] src/ui-init.c}).
 *
 * <p>Registered into {@link uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder}
 * by {@code Frontend.init}, and reached from there by {@code InitHandlers.enterInit} when
 * {@code EVENT_ENTER_INIT} is signalled.
 *
 * <p><b>Called on the game thread, and currently touches Swing directly.</b> The signal is raised
 * from inside {@code GameConstants.init()}, which runs on the game thread, so everything below
 * happens off the event dispatch thread: {@link Window#clear()} lays the frame out again and
 * {@code setChars}/{@code repaint} mutate panel state. Only {@code repaint()} is documented as
 * thread-safe. This needs a hop onto the EDT.
 *
 * <p>Builds a whole screen and hands it over in one go, rather than writing cell by cell: the title
 * screen replaces everything, so there is nothing underneath worth preserving. Cells past the end
 * of each news line are left null and painted as blanks.
 *
 * @author Rowan Crowther
 */
public class SplashScreen implements StatusDisplay {
    /**
     * Logger for read failures on {@code news.txt}.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger(SplashScreen.class);

    /**
     * The window to paint into, taken from the front end at construction.
     *
     * <p>Captured rather than looked up per call, so this splash screen is pinned to whichever
     * window was active when it was built. Fine while there is exactly one window; C's terms are an
     * array of eight, and the title screen belongs to the main one.
     *
     * @author Rowan Crowther
     */
    private Window activeWindow;

    /**
     * Build the title screen against a front end's active window.
     *
     * @param frontend the front end whose active window this paints into
     * @author Rowan Crowther
     */
    public SplashScreen(Frontend frontend) {
        activeWindow = frontend.getActiveWindow();
    }

    private enum SplashScreenState {
        START_TAG, END_TAG, IN_COLOUR_TEXT,
        IN_NORMAL_TEXT, VERSION
    }

    /**
     * Read {@code lib/screens/news.txt} and put it on screen.
     *
     * <p>Checks the file exists first and treats its absence as fatal, which is C's behaviour
     * exactly: a missing {@code news.txt} means the {@code lib} directory is broken, and there is
     * no point continuing into a data load that is about to fail worse.
     *
     * <p>The path is asked of {@code ANGBAND_DIRS.SCREENS} at call time, so a {@code -d} override
     * on the command line is honoured.
     *
     * <p>Note the fatal path throws but does not return, so a missing file falls through into the
     * painting below - harmless only because the throw happens first. Note also that the exception
     * is raised on the game thread inside {@code GameConstants.init()}'s {@code try}, so it is
     * caught there and re-reported as a data-load failure rather than the {@code lib} error it is.
     *
     * @author Rowan Crowther
     */
    @Override
    public void showSplashScreen() {
        String filename = AngbandDirs.ANGBAND_DIRS.SCREENS.getPath() + "news.txt";
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            initAngbandAux("Cannot access the " + filename + " file.");
        }

        activeWindow.clear();

        Frontend.JPanelArea panel = activeWindow.getArea();

        AngbandDisplayCharacter[][] display = new AngbandDisplayCharacter[24][80];
        int row = 0;
        try (Scanner newsScanner = new Scanner(path)) {
            ColourEnum colour = ColourEnum.COLOUR_WHITE;
            SplashScreenState state = SplashScreenState.IN_NORMAL_TEXT;
            int printCol = 0;
            String colourName = "";
            while (newsScanner.hasNextLine()) {
                String line = newsScanner.nextLine();
                printCol = 0;
                state = SplashScreenState.IN_NORMAL_TEXT;
                colour = ColourEnum.COLOUR_WHITE;
                colourName = "";
                // logger.debug(line);
                for (int col = 0; col < line.length(); col++) {
                    char character = line.charAt(col);

                    if (character == '{') {
                        state = SplashScreenState.IN_COLOUR_TEXT;
                        colourName = "";
                    } else if (character == '}') {
                        state = SplashScreenState.IN_NORMAL_TEXT;

                        if (colourName.equals("/")) {
                            colour = ColourEnum.COLOUR_WHITE;
                        } else if (!colourName.isEmpty()) {
                            colour = ColourEnum.fromCode(colourName);
                        }
                        colourName = "";

                    } else if (state == SplashScreenState.IN_COLOUR_TEXT) {
                        colourName += character;
                    } else if (character == '$') {
                        if (line.substring(col).startsWith("$VERSION")) {
                            String version = String.format("%-8s", GameConstants.version);
                            col += 7;
                            if (col > display[row].length) {
                                logger.error("Version tag exceeds line length");
                            } else {
                                for (int index = 0; index < 8; index++) {
                                    display[row][printCol] = new AngbandDisplayCharacter(version.charAt(index), colour);
                                    printCol++;
                                }
                            }
                        } else {
                            if (state == SplashScreenState.IN_NORMAL_TEXT) {
                                display[row][printCol] = new AngbandDisplayCharacter(character, colour);
                                printCol++;
                            } else {
                                logger.error("'$' character found outside normal text.");
                                col = display[row].length;
                            }
                        }
                    } else if (state == SplashScreenState.IN_NORMAL_TEXT) {
                        if (printCol < 80)
                            display[row][printCol] = new AngbandDisplayCharacter(character, colour);
                        printCol++;
                    }
                }
                row++;

                if (row == 24)
                    break;
            }
            panel.setChars(display);
            panel.repaint();
        } catch (IOException exception) {
            String message = "Trying to read news.txt when error occured.\n";
            logger.error(message, exception);
        }
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
     * Show a progress note under the title screen while the data files load.
     *
     * <p>Not implemented, so the title screen sits unchanged for the whole load. C draws the note
     * on the bottom row and flushes ({@code splashscreen_note}, {@code [C] src/ui-display.c}) -
     * which the grid can now express as a {@code put} along row 23 followed by a repaint.
     *
     * <p>It has no caller yet either: {@code InitHandlers.splashScreenNote} logs rather than
     * forwarding to here, so wiring both halves is one change.
     *
     * @param message the progress note to show; currently discarded
     * @author Rowan Crowther
     */
    @Override
    public void splashScreenNote(String message) {

    }
}
