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
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.colour.ColourEnum;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.Frontend;
import uk.co.jackoftrades.frontend.screen.Window;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplay;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import javax.swing.*;
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

    private static int birthLine = 2;

    private AngbandDisplayCharacter[][] display = new AngbandDisplayCharacter[24][80];

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

        Frontend.JPanelArea panel = activeWindow.getArea();

        int row = 0;
        try (Scanner newsScanner = new Scanner(path)) {
            ColourEnum colour = ColourEnum.COLOUR_WHITE;
            SplashScreenState state = SplashScreenState.IN_NORMAL_TEXT;
            int printCol = 0;
            StringBuilder colourName = new StringBuilder();
            while (newsScanner.hasNextLine()) {
                String line = newsScanner.nextLine();
                printCol = 0;
                state = SplashScreenState.IN_NORMAL_TEXT;
                colour = ColourEnum.COLOUR_WHITE;
                colourName = new StringBuilder();
                // logger.debug(line);
                for (int col = 0; col < line.length(); col++) {
                    char character = line.charAt(col);

                    if (character == '{') {
                        state = SplashScreenState.IN_COLOUR_TEXT;
                        colourName = new StringBuilder();
                    } else if (character == '}') {
                        state = SplashScreenState.IN_NORMAL_TEXT;

                        if (colourName.toString().equals("/")) {
                            colour = ColourEnum.COLOUR_WHITE;
                        } else if (!colourName.isEmpty()) {
                            colour = ColourEnum.fromCode(colourName.toString());
                            if (colour == null) {
                                logger.error("Invalid colour name " + colourName.toString());
                                colour = ColourEnum.COLOUR_WHITE;
                            }
                        }
                        colourName = new StringBuilder();

                    } else if (state == SplashScreenState.IN_COLOUR_TEXT) {
                        colourName.append(character);
                    } else if (character == '$') {
                        if (line.substring(col).startsWith("$VERSION")) {
                            String version = String.format("%-8s", GameConstants.version);
                            col += 7;
                            if (printCol + 8 > display[row].length) {
                                logger.error("Version tag exceeds line length");
                            } else {
                                for (int index = 0; index < 8; index++) {
                                    display[row][printCol] = new AngbandDisplayCharacter(version.charAt(index), colour);
                                    printCol++;
                                }
                            }
                        } else {
                            if (state == SplashScreenState.IN_NORMAL_TEXT) {
                                if (printCol < display[row].length)
                                    display[row][printCol] = new AngbandDisplayCharacter(character, colour);
                                printCol++;
                            } else {
                                logger.error("'$' character found outside normal text.");
                                col = display[row].length;
                            }
                        }
                    } else if (state == SplashScreenState.IN_NORMAL_TEXT) {
                        if (printCol < display[row].length)
                            display[row][printCol] = new AngbandDisplayCharacter(character, colour);
                        printCol++;
                    }
                }
                row++;

                if (row == 24)
                    break;
            }

            onEventDispatchThread(new Runnable() {
                @Override
                public void run() {
                    activeWindow.clear();
                    panel.setChars(display);
                    panel.repaint();
                }
            });
        } catch (IOException exception) {
            String message = "Trying to read news.txt when error occurred.\n";
            logger.error(message, exception);
        }
    }

    private void onEventDispatchThread(Runnable event) {
        SwingUtilities.invokeLater(event);
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
     * Show a progress note under the title screen while the data files load: bracketed, centred, on
     * the bottom row. The port of {@code splashscreen_note}'s non-birth branch
     * ({@code [C] src/ui-display.c}).
     *
     * <p>Row 23 is where C puts it too - {@code (Term->hgt - 23) / 5 + 23}, which is 23 on a
     * 24-row term. It is the one row the title artwork leaves free, and later the row the status
     * line and message prompt live on. Note that the row is written as a literal here while C
     * derives it from the term's height, so a taller window would put the note in the wrong place;
     * the whole class assumes 80x24, so this is one of several places that would need the real
     * dimensions rather than a special case.
     *
     * <p><b>The row is blanked before the note is written</b>, which is C's {@code Term_erase} and
     * is not optional. These notes arrive in a stream - one per data file - each a different length
     * and each centred on its own length, so writing one over another without erasing would leave
     * both ends of every note that was ever longer than the current one lying on the row.
     *
     * <p>The brackets are part of the format C chose, not decoration: they mark the text as a
     * transient status line rather than as part of the artwork it is sitting under. They are
     * included in the length the centring is computed from, as in C.
     *
     * <p>A note wider than the screen would be centred to a negative column. Nothing here rejects
     * that, and nothing needs to - {@code JPanelArea.put} clips a string to the grid at both ends -
     * but the note would lose its beginning as well as its end.
     *
     * <p><b>Called on the game thread, and touches Swing directly</b>, exactly as
     * {@link #showSplashScreen()} does and for the same reason: the {@code EVENT_INITSTATUS} signal
     * is raised from inside the data load. {@code put} mutates panel state off the event dispatch
     * thread; only {@code repaint()} is documented as thread-safe. This needs a hop onto the EDT.
     *
     * <p>No caller yet: {@code InitHandlers.splashScreenNote} logs rather than forwarding here,
     * because the event payload cannot yet say whether a note is a birth note or a load note - the
     * distinction this method and {@link #splashScreenBirthNote(String)} are split on.
     *
     * @param message the progress note to show, unbracketed; the brackets are added here
     * @author Rowan Crowther
     */
    @Override
    public void splashScreenNote(@NotNull String message) {
        Frontend.JPanelArea panel = activeWindow.getArea();
        int row = 23;

        // clear the status line
        panel.put(row, 0, String.format("%80s", ""), ColourEnum.COLOUR_WHITE);

        String toWrite = String.format("[%s]", message);
        int col = (80 - toWrite.length()) / 2;
        panel.put(row, col, toWrite, ColourEnum.COLOUR_WHITE);
        panel.repaint();
    }

    /**
     * Show a character-creation note, stacking down the screen from row 2. The port of
     * {@code splashscreen_note}'s {@code MSG_BIRTH} branch ({@code [C] src/ui-display.c}).
     *
     * <p>These behave oppositely to {@link #splashScreenNote(String)} and that is the point of the
     * split: a load note is one row rewritten over and over, while birth notes accumulate, each on
     * its own row, so the player can read the sequence. So this one neither erases nor centres -
     * it writes from column 0 and leaves everything above it alone.
     *
     * <p>C reaches both through a single callback and chooses between them at run time, on
     * {@code data->message.type == MSG_BIRTH}, because a term registers one function pointer per
     * event and both notes arrive on {@code EVENT_INITSTATUS}. The port routes display calls through
     * a named interface instead, so the same choice can be made at the call site and checked by the
     * compiler. Splitting is the port's decision, not C's.
     *
     * <p><b>Where it stops differs from C.</b> C wraps with {@code if (++y >= 24) y = 2}, so it uses
     * rows 2 to 23 and reuses the note row once birth is under way; this wraps at 23, so row 23 is
     * never written and one row of the twenty-two is lost. Whether that is worth keeping is a real
     * choice - leaving 23 clear keeps the load note undisturbed - but it is a divergence, and the
     * wrap point is where it lives.
     *
     * <p>The row counter is {@code static}, so it belongs to the class rather than to this splash
     * screen. C's is a function-static and equally process-wide, so a single-window game behaves the
     * same; two front ends would share one counter, which is not what the field's placement
     * suggests.
     *
     * <p><b>C pauses here and this does not.</b> {@code splashscreen_note} calls
     * {@code pause_line(Term)} after each birth note, so the player reads them one at a time; these
     * will all appear at once. Closing that gap needs the input seam, since a pause is a read.
     *
     * <p><b>Called on the game thread, and touches Swing directly</b> - see
     * {@link #splashScreenNote(String)}, which has the same problem for the same reason.
     *
     * <p>No caller yet, for the same reason as {@link #splashScreenNote(String)}: nothing on the
     * event payload distinguishes a birth note from a load note, so {@code InitHandlers} has
     * nothing to dispatch on.
     *
     * @param message the note to show, written from column 0 as given
     * @author Rowan Crowther
     */
    @Override
    public void splashScreenBirthNote(@NotNull String message) {
        Frontend.JPanelArea panel = activeWindow.getArea();
        panel.put(birthLine, 0, message, ColourEnum.COLOUR_WHITE);
        birthLine++;
        if (birthLine >= 24)
            birthLine = 2;
        panel.repaint();
    }
}
