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
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.SwingUI;
import uk.co.jackoftrades.frontend.screen.Window;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * The title screen: the front end's main window, which paints {@code lib/screens/news.txt}
 * into the game window while the data files load. The port of {@code ui_enter_init}'s display half
 * ({@code [C] src/ui-display.c}), which C reaches through {@code show_splashscreen()}
 * ({@code [C] src/ui-init.c}).
 *
 * <p><b>No longer anything the core can name.</b> Until stage 2 an instance of this class sat in a
 * static slot the middle end read, and the core called it directly. It is now built by
 * {@code UILoop} when {@code EVENT_ENTER_INIT} arrives as a message - so this class implements
 * nothing, is named by no core code, and is reached only from the UI half. Stage 5 removed the slot
 * and its remaining implementations altogether. The methods kept their names because they still
 * answer the same two questions; what changed is who asks.
 *
 * <p><b>Called on the UI thread, and hops to the EDT itself.</b> {@code UILoop} runs on
 * {@code angband-ui}, which is neither the game thread nor Swing's event dispatch thread, so the
 * painting below is queued with {@code invokeLater} rather than run where it is called. That closes
 * a hole this class's Javadoc used to admit to: the old arrangement painted straight from the game
 * thread, and only {@code repaint()} is documented as safe to do that with.
 *
 * <p>(The thread was called {@code angband-display} until stage 4, when the loop stopped having a
 * thread of its own and became the body of the UI thread {@code main()} starts. Nothing about the
 * reasoning changed - it is still not the EDT - but the name in a stack trace did.)
 *
 * <p>One consequence worth holding on to: the character grid is filled in on the UI thread and only
 * handed over inside the queued block, so the parse and the paint never race. That works because
 * each instance owns its grid - two splash screens would be two grids, not one contended one.
 *
 * <p>Builds a whole screen and hands it over in one go, rather than writing cell by cell: the title
 * screen replaces everything, so there is nothing underneath worth preserving. Cells past the end
 * of each news line are left null and painted as blanks.
 *
 * @author Rowan Crowther
 */
public class SplashScreen {
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
     * The row the next birth note goes on, counting down from 2 and wrapping.
     *
     * <p>{@code static}, so it belongs to the class rather than to a splash screen - see
     * {@link #splashScreenBirthNote(String)}, which is where that matters and where C's own
     * function-static is discussed.
     *
     * @author Rowan Crowther
     */
    private static int birthLine = 2;

    /**
     * The character grid this splash screen builds and paints: 24 rows of 80, C's term dimensions.
     *
     * <p>Per-instance, and that is what makes the notes work. {@link #readAndParse} fills it with the
     * title artwork and {@link #splashScreenNote(String)} rewrites row 23 of it, so both must be
     * looking at the same array - a note painted onto a fresh grid would be one line on an otherwise
     * blank screen. It is why {@code UILoop} keeps the instance it built rather than making a new one
     * per message.
     *
     * <p>Cells never written stay {@code null} and are painted as blanks, so a short news line does
     * not need padding.
     *
     * @author Rowan Crowther
     */
    private AngbandDisplayCharacter[][] display = new AngbandDisplayCharacter[24][80];

    /**
     * Build the title screen against a front end's active window.
     *
     * @param swingUI the front end whose active window this paints into
     * @author Rowan Crowther
     */
    public SplashScreen(SwingUI swingUI) {
        activeWindow = swingUI.getActiveWindow();
    }

    /**
     * Read {@code news.txt} and turn it into a grid of coloured characters. The port of the parsing
     * half of {@code show_splashscreen()} ({@code [C] src/ui-init.c}).
     *
     * <p>The format is the file's own: {@code {colour}} switches the colour for what follows,
     * {@code {/}} puts it back to white, and {@code $VERSION} is replaced by the version string
     * padded to eight characters. Each of those is C's handling exactly, including the padding width,
     * which is why a version string longer than eight would overflow the line in both.
     *
     * <p><b>Parsing, not painting.</b> Nothing here touches Swing, which is what lets it run on the
     * UI thread while the file is read - the caller paints the result separately. The grid is also
     * returned as well as kept, so the caller need not reach back in for it.
     *
     * <p>Stops after 24 rows and clips anything past column 80, so a news file larger than the term
     * is truncated rather than throwing. A read failure is logged and leaves the grid partly filled:
     * the title screen appears half-drawn rather than not at all, which is a poor way to report a
     * broken {@code lib} directory - the existence check the caller makes first is what usually
     * catches that.
     *
     * @param path the news file to read, resolved by the caller so a {@code -d} override is honoured
     * @return this splash screen's character grid, filled in
     * @author Rowan Crowther
     */
    public AngbandDisplayCharacter[][] readAndParse(Path path) {
        int row = 0;
        try (Scanner newsScanner = new Scanner(path)) {
            ColourEnum colour;
            SplashScreenState state;
            int printCol = 0;
            StringBuilder colourName;
            while (newsScanner.hasNextLine()) {
                String line = newsScanner.nextLine();
                printCol = 0;
                state = SplashScreenState.IN_NORMAL_TEXT;
                colour = ColourEnum.COLOUR_WHITE;
                colourName = new StringBuilder();
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
                                logger.error("Invalid colour name {}", colourName.toString());
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
        } catch (IOException e) {
            String message = "Trying to read news.txt when error occurred.\n";
            logger.error(message, e);
        }

        return display;
    }

    /**
     * Put the grid on screen: paint whatever {@link #readAndParse} left in it.
     *
     * <p><b>Reduced to the painting half.</b> This used to find {@code news.txt}, check it existed,
     * treat its absence as fatal and then parse it. All of that moved to {@code UILoop}, which now
     * does the locating and the existence check before building a splash screen at all - so this
     * method assumes the grid is already filled and does nothing but hand it over. The commented-out
     * lines below are what moved, kept while the move is still recent.
     *
     * <p>No caller today, and that is the loose end: {@code UILoop} paints the grid
     * {@code readAndParse} returns by handing it to {@code Window.display}, which does the same job
     * through a different route. One of the two routes should win - either this method is how a
     * splash screen paints itself, or {@code Window.display} is how anything paints and this method
     * goes. Worth settling before stage 5 adds more painting to the loop.
     *
     * @author Rowan Crowther
     */
    public void showSplashScreen() {
//        String filename = AngbandDirs.ANGBAND_DIRS.SCREENS.getPath() + "news.txt";
//        Path path = Paths.get(filename);
//        if (!Files.exists(path)) {
//            initAngbandAux("Cannot access the " + filename + " file.");
//        }

        SwingUI.JPanelArea panel = activeWindow.getArea();
        
            onEventDispatchThread(new Runnable() {
                @Override
                public void run() {
                    panel.setChars(display);
                    panel.repaint();
                }
            });
    }

    /**
     * Queue a block to run on Swing's event dispatch thread.
     *
     * <p>A named wrapper over {@code SwingUtilities.invokeLater} rather than the call itself, so the
     * painting methods read as a statement of where the work goes. {@code invokeLater} and not
     * {@code invokeAndWait}: the UI thread has no reason to wait for a repaint, and waiting is
     * how it would deadlock if the EDT ever came to need something from it.
     *
     * @param event the block to run on the EDT
     * @author Rowan Crowther
     */
    private void onEventDispatchThread(Runnable event) {
        SwingUtilities.invokeLater(event);
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
     * <p><b>Called on the UI thread, and hops to the EDT itself.</b> The note is written into
     * this instance's grid where it is called, and only the {@code clear}/{@code setChars}/
     * {@code repaint} sequence is queued with {@code invokeLater} - so the grid is complete before
     * anything looks at it, and no Swing state is mutated off the event dispatch thread.
     *
     * <p><b>The live path:</b> {@code GameConstants.init()} signals {@code EVENT_INITSTATUS},
     * {@code InitHandlers.splashScreenNote} turns it into a {@code TextCoreMessage} on the core
     * channel, and {@code UILoop} takes it off the inbox and calls this with the text. Every note in
     * the data load comes through here.
     *
     * <p>Its birth counterpart {@link #splashScreenBirthNote(String)} is still unreached, because
     * nothing on the wire distinguishes the two kinds of note - which is the whole reason the split
     * exists. Chapter 3 supplies the distinction.
     *
     * @param message the progress note to show, unbracketed; the brackets are added here
     * @author Rowan Crowther
     */

    public void splashScreenNote(@NotNull String message) {
        SwingUI.JPanelArea panel = activeWindow.getArea();
        int row = 23;

        // clear the status line
        for (int col = 0; col < display[row].length; col++) {
            display[row][col] = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);
        }

        String toWrite = String.format("[%s]", message);
        int col = (80 - toWrite.length()) / 2;
        for (int index = 0; index < toWrite.length(); index++) {
            display[row][index + col] = new AngbandDisplayCharacter(toWrite.charAt(index), ColourEnum.COLOUR_WHITE);
        }

        onEventDispatchThread(new Runnable() {
            @Override
            public void run() {
                activeWindow.clear();
                activeWindow.getArea().setChars(display);
                activeWindow.getArea().repaint();
            }
        });
    }

//    /**
//     * Report that the {@code lib} directory is unusable and give up. The port of
//     * {@code init_angband_aux()} ({@code [C] src/ui-init.c}), which prints the same four lines.
//     *
//     * <p>C can put this on the terminal it started from; the port has no terminal, so it logs and
//     * throws. Nothing catches the exception meaningfully - it surfaces inside
//     * {@code GameConstants.init()}'s handler and is re-reported as a data-load failure - so the
//     * message the player would most want is the one they are least likely to see.
//     *
//     * @param why what could not be read, used as the first line of the message
//     * @throws RuntimeException always; this method does not return
//     * @author Rowan Crowther
//     */
//    private void initAngbandAux(String why) {
//        String message = why + "\n" +
//                "The 'lib' directory is probably missing or broken.\n" +
//                "Perhaps the archive was not extracted correctly.\n" +
//                "See the 'readme.txt' file for more information.";
//        logger.fatal(message);
//        throw new RuntimeException(message);
//    }

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
     * will all appear at once. Closing that gap needs the input boundary, since a pause is a read.
     *
     * <p><b>Still paints from wherever it is called</b>, unlike its neighbour: {@code put} mutates
     * panel state and only {@code repaint()} is documented as safe off the event dispatch thread. Its
     * first caller will arrive on the UI thread, so this needs the same {@code invokeLater}
     * {@link #splashScreenNote(String)} now has before it is wired up.
     *
     * <p>No caller yet, and the reason is on the wire rather than here: nothing in the message says
     * whether a note is a birth note or a load note, so there is nothing for {@code UILoop} to
     * dispatch on. C's discriminator is {@code MSG_BIRTH} on the message payload, which is the shape
     * the port will probably follow. The painting below is written and waiting.
     *
     * @param message the note to show, written from column 0 as given
     * @author Rowan Crowther
     */
    public void splashScreenBirthNote(@NotNull String message) {
        SwingUI.JPanelArea panel = activeWindow.getArea();
        panel.put(birthLine, 0, message, ColourEnum.COLOUR_WHITE);
        birthLine++;
        if (birthLine >= 24)
            birthLine = 2;
        panel.repaint();
    }

    /**
     * Where the scanner is in a news line: inside a {@code {colour}} tag, or in the text it applies
     * to. C tracks the same two states with a pair of {@code if}s over the current character; an enum
     * makes the states nameable and the transitions readable.
     *
     * @author Rowan Crowther
     */
    private enum SplashScreenState {
        START_TAG, END_TAG, IN_COLOUR_TEXT,
        IN_NORMAL_TEXT, VERSION
    }
}
