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

package uk.co.jackoftradesltd.frontend.splash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.globals.Angband;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.SwingUI;
import uk.co.jackoftradesltd.frontend.screen.Window;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Region;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The title screen: the front end's main window, which paints {@code lib/screens/news.txt}
 * into the game window while the data files load. The port of {@code ui_enter_init}'s display half,
 * which C reaches through {@code show_splashscreen()} - both live in {@code [C] src/ui-display.c}.
 *
 * <p><b>No longer anything the core can name.</b> Until stage 2 an instance of this class sat in a
 * static slot the middle end read, and the core called it directly. It is now built by
 * {@code UILoop} when {@code EVENT_ENTER_INIT} arrives as a message - so this class implements
 * nothing, is named by no core code, and is reached only from the UI half. Stage 5 removed the slot
 * and its remaining implementations altogether. The methods kept their names because they still
 * answer the same two questions; what changed is who asks.
 *
 * <p>(The thread was called {@code angband-display} until stage 4, when the loop stopped having a
 * thread of its own and became the body of the UI thread {@code main()} starts. Nothing about the
 * reasoning changed - it is still not the EDT - but the name in a stack trace did.)
 *
 * <p><b>Writes straight into a caller-supplied {@link Screen} now, and no longer hops to the EDT
 * itself.</b> {@link #readAndParse} and {@link #splashScreenNote(String, Screen)} both take a
 * {@code Screen} and mutate its live grid synchronously through {@link Region}, on whatever thread
 * calls them. The EDT hop that used to happen inside this class, via {@link #onEventDispatchThread},
 * has moved to {@link Window#show} instead, which every caller already goes through afterwards to
 * make a write visible - so {@code onEventDispatchThread} and the per-instance {@link #display}
 * grid it used to protect are dead code, left over from before that move.
 * {@link #splashScreenBirthNote(String)} is the one method still on the old path: it writes to
 * {@code activeWindow}'s own {@link SwingUI.JPanelArea} directly, with no EDT hop of its own either.
 *
 * <p>Writes character by character straight into the live grid rather than building a separate
 * buffer to swap in - there is nothing on screen yet worth preserving this early in start-up, so
 * overwriting cell by cell is safe. A cell no news line reaches is left however {@link CellGrid}
 * initialised it - {@code null}, since nothing paints anything before this runs - so a short news
 * line does not need padding.
 *
 * <p>Class SplashScreen coded on 260813, commented in full on 260917.
 *
 * @author Rowan Crowther
 */
public class SplashScreen {
    /**
     * Logger for read failures on {@code news.txt}.
     *
     * <p>Field logger coded on 260813, commented in full on 260916.
     */
    private static final Logger logger = LogManager.getLogger(SplashScreen.class);
    /**
     * The row the next birth note goes on, counting down from 2 and wrapping.
     *
     * <p>{@code static}, so it belongs to the class rather than to a splash screen - see
     * {@link #splashScreenBirthNote(String)}, which is where that matters and where C's own
     * function-static is discussed.
     *
     * <p>Field birthLine coded on 260813, commented in full on 260916.
     */
    private static int birthLine = 2;
    /**
     * The window to paint into, taken from the front end at construction.
     *
     * <p>Captured rather than looked up per call, so this splash screen is pinned to whichever
     * window was active when it was built. Fine while there is exactly one window; C's terms are an
     * array of eight, and the title screen belongs to the main one.
     *
     * <p>Field activeWindow coded on 260813, commented in full on 260916.
     */
    private Window activeWindow;
    /**
     * A 24x80 grid of coloured characters, C's term dimensions - built when this class painted its
     * own per-instance grid, before {@link #readAndParse} and {@link #splashScreenNote(String, Screen)}
     * moved onto the {@link Screen}/{@link Region} pair those methods now take as a parameter instead.
     *
     * <p>Currently unused: nothing in this class reads or writes it any longer, since both methods
     * that once shared it now write straight into the {@code Screen} handed to them. Left in place
     * rather than removed here, since that is a code change and not a comment - whether it, and the
     * now-unused {@link CellGrid} and {@code java.util.ArrayList} imports, should go is Rowan's call.
     *
     * <p>Field display coded on 260813, commented in full on 260916.
     */
    private AngbandDisplayCharacter[][] display = new AngbandDisplayCharacter[24][80];

    /**
     * Build the title screen against a front end's active window.
     *
     * <p>Constructor SplashScreen coded on 260813, commented in full on 260916.
     *
     * @param swingUI the front end whose active window this paints into
     */
    public SplashScreen(SwingUI swingUI) {
        activeWindow = swingUI.getActiveWindow();
    }

    /**
     * Read {@code news.txt} and paint it straight into {@code screen}'s live grid. The port of the
     * parsing half of {@code show_splashscreen()} ({@code [C] src/ui-init.c}).
     *
     * <p>The format is the file's own: {@code {colour}} switches the colour for what follows,
     * {@code {/}} puts it back to white, and {@code $VERSION} is replaced by the version string
     * padded to eight characters. Each of those is C's handling exactly, including the padding
     * width, which is why a version string longer than eight would overflow the line in both - and,
     * matching C's {@code strnfmt} truncating the line at that point, nothing after {@code $VERSION}
     * on the same line is read once the version stamp is written.
     *
     * <p><b>Parsing, not painting.</b> Nothing here touches Swing, which is what lets it run on the
     * UI thread while the file is read; the caller paints the result separately, via
     * {@code screen.frame()} and {@link Window#show}. Nothing is returned or kept on this instance -
     * the grid filled in is {@code screen}'s own, mutated in place through {@link Screen#root()},
     * and the caller already holds the same {@code screen} reference it passed in here.
     *
     * <p>Stops after 24 rows and clips anything past column 80, so a news file larger than the term
     * is truncated rather than throwing. A read failure is logged and leaves the grid partly filled:
     * the title screen appears half-drawn rather than not at all, which is a poor way to report a
     * broken {@code lib} directory - the existence check the caller makes first is what usually
     * catches that.
     *
     * <p>Method readAndParse coded on 260909, commented in full on 260916.
     *
     * @param path   the news file to read, resolved by the caller so a {@code -d} override is honoured
     * @param screen the screen whose grid this paints into
     */
    public void readAndParse(Path path, Screen screen) {
        Region root = screen.root();
        
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
                            col += 7;
                            if (printCol + 8 > root.cols()) {
                                logger.error("Version tag exceeds line length");
                            } else {
                                for (int index = 0; index < 8; index++) {
                                    char c = String.format("%-8s", Angband.versionString).charAt(index);
                                    root.put(row, printCol, new AngbandDisplayCharacter(c, colour));
                                    printCol++;
                                }
                                col = root.cols();
                            }
                        } else {
                            if (state == SplashScreenState.IN_NORMAL_TEXT) {
                                if (printCol < root.cols())
                                    root.put(row, printCol, new AngbandDisplayCharacter(character, colour));
                                printCol++;
                            } else {
                                logger.error("'$' character found outside normal text.");
                                col = root.cols();
                            }
                        }
                    } else if (state == SplashScreenState.IN_NORMAL_TEXT) {
                        if (printCol < root.cols())
                            root.put(row, printCol, new AngbandDisplayCharacter(character, colour));
                        printCol++;
                    }
                }
                row++;

                if (row == 24)
                    break;
            }

            System.out.println("SplashScreen read complete");
        } catch (IOException e) {
            String message = "Trying to read news.txt when error occurred.\n";
            logger.error(message, e);
        }
    }

    /**
     * Queue a block to run on Swing's event dispatch thread.
     *
     * <p>A named wrapper over {@code SwingUtilities.invokeLater} rather than the call itself, so a
     * caller reads as a statement of where the work goes. {@code invokeLater} and not
     * {@code invokeAndWait}: the UI thread has no reason to wait for a repaint, and waiting is
     * how it would deadlock if the EDT ever came to need something from it.
     *
     * <p><b>Currently unused.</b> No method in this class calls it any longer -
     * {@link #readAndParse} and {@link #splashScreenNote(String, Screen)} write synchronously into
     * a caller-supplied {@link Screen} instead, and {@link Window#show} does the equivalent
     * {@code invokeLater} hop on the caller's behalf once painting is done.
     * {@link #splashScreenBirthNote(String)} paints with no EDT hop at all. Left in place as a
     * private method rather than removed here, since that is a code change and not a comment.
     *
     * <p>Method onEventDispatchThread coded on 260813, commented in full on 260916.
     *
     * @param event the block to run on the EDT
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
     * that, and nothing needs to - {@link Region#put(int, int, String, ColourEnum)} drops the
     * overhanging characters at both ends rather than throwing - but the note would lose its
     * beginning as well as its end.
     *
     * <p>Writes straight into {@code screen}'s live grid, synchronously, on whatever thread this is
     * called from - there is no EDT hop here, or anywhere else in this class any more. Whatever
     * makes the write visible ({@link Window#show}) does its own {@code invokeLater} afterwards, on
     * the caller's side, not this method's.
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
     * <p>Method splashScreenNote coded on 260909, commented in full on 260916.
     *
     * @param message the progress note to show, unbracketed; the brackets are added here
     * @param screen  the screen whose grid this paints into
     */
    public void splashScreenNote(@NotNull String message, Screen screen) {
        screen.root().erase(23, 0, 255);

        int row = 23;

        String toWrite = String.format("[%s]", message);
        int col = (80 - toWrite.length()) / 2;
        screen.root().put(row, col, toWrite, ColourEnum.COLOUR_WHITE);
    }

    /**
     * Show a character-creation note, stacking down the screen from row 2. The port of
     * {@code splashscreen_note}'s {@code MSG_BIRTH} branch ({@code [C] src/ui-display.c}).
     *
     * <p>These behave oppositely to {@link #splashScreenNote(String, Screen)} and that is the point of the
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
     * <p>Wraps the same way C does: {@code if (++y >= 24) y = 2} uses rows 2 to 23 and reuses the
     * note row once birth is under way, and {@code if (birthLine >= 24) birthLine = 2} below does
     * the same thing - both write rows 2 through 23 inclusive before wrapping back to row 2.
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
     * <p><b>Still on the old {@link SwingUI.JPanelArea} path</b>, unlike its neighbour: this writes
     * to {@code activeWindow}'s panel directly and calls {@code repaint()} with no EDT hop at all,
     * where {@link #splashScreenNote(String, Screen)} now writes into a caller-supplied
     * {@link Screen} instead. Migrating this one too needs a birth-note path on the wire first,
     * since nothing calls it yet - see below.
     *
     * <p>No caller yet, and the reason is on the wire rather than here: nothing in the message says
     * whether a note is a birth note or a load note, so there is nothing for {@code UILoop} to
     * dispatch on. C's discriminator is {@code MSG_BIRTH} on the message payload, which is the shape
     * the port will probably follow. The painting below is written and waiting.
     *
     * <p>Method splashScreenBirthNote coded on 260813, commented in full on 260916.
     *
     * @param message the note to show, written from column 0 as given
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
     * <p>Only {@link #IN_COLOUR_TEXT} and {@link #IN_NORMAL_TEXT} are ever used by
     * {@link SplashScreen#readAndParse}; {@link #START_TAG}, {@link #END_TAG} and {@link #VERSION}
     * are declared but never reached by anything in this class.
     *
     * <p>Enum SplashScreenState coded on 260813, commented in full on 260916.
     *
     * @author Rowan Crowther
     */
    private enum SplashScreenState {
        START_TAG, END_TAG, IN_COLOUR_TEXT,
        IN_NORMAL_TEXT, VERSION
    }
}
