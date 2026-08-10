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

package uk.co.jackoftrades.frontend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.StartupOptions;
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.colour.Colour;
import uk.co.jackoftrades.frontend.screen.Window;
import uk.co.jackoftrades.frontend.splash.SplashScreen;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder;
import uk.co.jackoftrades.middle.game.gameengine.GameRunner;

import javax.swing.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Swing front end: owns the game's windows, and owns the one handle on the middle end.
 * It stands in for C's {@code main-*.c} modules ({@code [C] src/main-gcu.c} and friends), each of
 * which builds its platform's terms and installs the hooks the core calls back through.
 *
 * <p>Everything here runs on Swing's event dispatch thread (EDT). The middle end runs on the
 * thread {@link GameRunner} owns, and {@code GameRunner} is the only middle-end type this class
 * imports - one object wide, which is what keeps the seam between the two halves checkable by
 * reading the import list.
 *
 * <p>State is per-instance, not static. A previous version made the fields static so a static
 * {@code closeDown} could reach them, which left the class half-static and would have let a second
 * front end quietly overwrite the first one's runner.
 *
 * <p>Barely started: {@link #init} builds one 80x24 window and starts the game thread. The panel
 * can now hold and paint a grid of coloured characters, but the window list only ever holds the one
 * window it was constructed with, and nothing yet drives the grid except the splash screen.
 *
 * @author Rowan Crowther
 */
public class Frontend {
    private static final Logger logger = LogManager.getLogger(Frontend.class);

    /**
     * Every window this front end has opened, so shutdown can dispose them all. Holds exactly one
     * for now; C's terms are a fixed array of eight.
     *
     * @author Rowan Crowther
     */
    private List<Window> windows;
    /**
     * The window currently being drawn to and configured - C's {@code Term}, the term that
     * display calls implicitly act on.
     *
     * @author Rowan Crowther
     */
    private Window activeWindow;
    /**
     * The game thread's owner, and this class's entire view of the middle end: started at the end
     * of {@link #init} and asked to stop by {@link #closeDown}.
     *
     * @author Rowan Crowther
     */
    private GameRunner gameRunner;

    /**
     * The parsed command line, kept from {@link #init}.
     *
     * <p>Nothing reads it yet. {@code requestGraphicsMode} is the component that belongs to this
     * class - tiles or plain text is a decision taken while building the window - and the
     * savefile group is not the front end's to act on.
     *
     * @author Rowan Crowther
     */
    private StartupOptions startupOptions;

    /**
     * Build the front end around the runner it will drive, and open its first window.
     *
     * <p>Only assembles state; nothing is shown and no thread starts until {@link #init}. Runs on
     * the EDT, since {@link Window} is a Swing component.
     *
     * @param gameRunner the game thread's owner, this front end's one handle on the middle end
     * @author Rowan Crowther
     */
    public Frontend(GameRunner gameRunner) {
        windows = new ArrayList<>();
        Window main = new Window();
        windows.add(main);
        activeWindow = main;
        this.gameRunner = gameRunner;
    }

    /**
     * Shut the game down: dispose every window, ask the game thread to stop, and exit.
     *
     * <p>Reached from the window-closing listener. The window is set to
     * {@code DO_NOTHING_ON_CLOSE} precisely so the close button arrives here instead of quietly
     * disposing the frame, which is the port's equivalent of C routing a quit through
     * {@code quit_aux} rather than letting the display vanish underneath the game.
     *
     * <p>The stop is only requested, never waited for: {@link System#exit} follows immediately, so
     * the game thread is killed wherever it happens to be rather than finishing. That is
     * survivable only while the loop holds no state worth saving. Once it does, this needs to join
     * the thread - or hand the exit to it - before the process goes.
     *
     * <p>Exits directly rather than through {@code QuitAux}, so nothing is logged on the way out
     * and the front end does not get the cleanup hook C gives it.
     *
     * @author Rowan Crowther
     */
    public void closeDown() {
        for (Window window : windows) {
            window.dispose();
        }
        gameRunner.requestStop();
        System.exit(0);
    }

    /**
     * Bring the front end up: size the window from the chosen font, wire the close handler, start
     * the game thread, and show it. The port of a {@code main-*.c} module's {@code init_*}
     * function, which C calls before {@code init_angband()} so the display exists to report
     * loading errors on.
     *
     * <p>The metrics drive everything. Angband is written against a character grid, so the window
     * is sized as 80x24 cells of whatever the font's {@code 'M'} measures - the port's equivalent
     * of C asking a terminal how big it is. {@code TerminalVector} is preferred and the platform
     * monospace is the fallback, so the grid stays square-ish on a machine without the game font.
     *
     * <p>Ordering worth keeping: the listener is attached before the window is shown, so a close
     * can never arrive before there is something to handle it, and {@code gameRunner.start()}
     * comes last, so the game thread cannot outlive a failure to build the display. An exception
     * before {@code setVisible} leaves the JVM alive with no window on screen, because
     * {@code pack()} has already made the frame displayable and so kept the EDT running.
     *
     * @param options the parsed command line; stored, not yet acted on
     * @author Rowan Crowther
     */
    public void init(StartupOptions options) {
        Colour.init();

        startupOptions = options;

        List<String> fontNames = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        Font font;
        int fontSize = 24;
        if (fontNames.contains("TerminalVector"))
            font = new Font("TerminalVector", Font.PLAIN, fontSize);
        else
            font = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);

        JPanelArea.font = font;

        FontMetrics metrics = activeWindow.getFontMetrics(font);
        int charWidth = metrics.charWidth('M');
        int charHeight = metrics.getHeight();

        JPanelArea.charAscent = metrics.getAscent();
        JPanelArea.charHeight = charHeight;
        JPanelArea.charWidth = charWidth;

        JFrame.setDefaultLookAndFeelDecorated(true);
        activeWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        activeWindow.setSize(80 * charWidth, 24 * charHeight);
        activeWindow.setTitle("Test Window");
        activeWindow.addWindowListener(windowListener);

        JPanelArea mainPanel = new JPanelArea();
        mainPanel.setToolTipText("Main Panel");
        mainPanel.setPreferredSize(new Dimension(80 * charWidth, 24 * charHeight));
        activeWindow.add(mainPanel);
        activeWindow.pack();
        activeWindow.setLocationRelativeTo(null);

        activeWindow.setVisible(true);

        // Register the event holders
        StatusDisplayHolder.setInstance(new SplashScreen(this));

        gameRunner.start();
    }

    /**
     * The window display calls currently act on - C's {@code Term}.
     *
     * @return the active window
     * @author Rowan Crowther
     */
    public Window getActiveWindow() {
        return activeWindow;
    }

    /**
     * The character grid: the component the game is actually drawn on, and the port's {@code Term}
     * surface. Holds one {@link AngbandDisplayCharacter} per cell of an 80x24 terminal, and paints
     * the whole of it on every repaint.
     *
     * <p>Repainting everything is deliberate, and is what makes "change one cell and show the
     * screen with only that changed" fall out for free: callers write into the buffer and call
     * {@code repaint()}, and the unchanged cells come back identical because they come from the
     * same array. C has to work harder - {@code Term_fresh} ({@code [C] src/z-term.c}) diffs the
     * working grid against the displayed one and emits only the runs that differ - because a real
     * terminal charges per character written. Swing does not, so the diffing can wait until there
     * is a measured reason for it.
     *
     * <p><b>Indexed {@code [row][column]} throughout</b> - the grid, {@link #put}, the callers of
     * {@link #setChars} and {@link #paintComponent} all agree, and it is the order C stores its
     * {@code term_win} grids in. Keep it that way: the disagreement this class briefly had was
     * invisible in a blank grid and showed up as a screen painted sideways.
     *
     * <p>The font and metrics are static, so they are shared by every panel in the JVM rather than
     * belonging to the front end that measured them. They are per-front-end values, and a second
     * front end with a different font would overwrite them.
     *
     * @author Rowan Crowther
     */
    public class JPanelArea extends JPanel {
        /**
         * The grid font, measured and installed by {@link Frontend#init}.
         */
        public static Font font;
        /** Cell width in pixels, from the font's {@code 'M'}. */
        public static int charWidth;
        /** Cell height in pixels, the font's full line height. */
        public static int charHeight;
        /** Baseline offset within a cell, for placing glyphs once there are any. */
        public static int charAscent;

        /**
         * The screen contents, one cell per character position. Every repaint is rendered from
         * this and nothing else, so it is the single source of truth for what is on screen.
         *
         * <p>Allocated {@code [row][column]}, matching {@link #put}, {@link #paintComponent}, the
         * callers of {@link #setChars}, and C's {@code term_win} grids.
         *
         * @author Rowan Crowther
         */
        private AngbandDisplayCharacter[][] display = new AngbandDisplayCharacter[24][80];

        /**
         * Build a panel over a blank screen: every cell a dark space, so the grid is fully
         * populated before anything paints and no cell is ever null on the first repaint.
         *
         * @author Rowan Crowther
         */
        public JPanelArea() {
            super();
            for (int i = 0; i < display.length; i++) {
                for (int j = 0; j < display[i].length; j++) {
                    display[i][j] = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_DARK);
                }
            }
        }

        /**
         * Replace the whole screen in one go, for callers that have built a full grid rather than
         * writing cell by cell.
         *
         * <p>The array is taken by reference, not copied, so the caller must not keep writing to it
         * afterwards - the panel would repaint mid-change.
         *
         * <p>Both dimensions are checked, so a grid of the wrong shape is rejected here rather than
         * overrunning on the next repaint. This is the only write path into the panel that is
         * bounds-checked - {@link #put} is not.
         *
         * @param display the replacement grid, which must be exactly 24 rows of 80 columns
         * @throws IllegalStateException if the grid is not 24x80
         * @author Rowan Crowther
         */
        public void setChars(AngbandDisplayCharacter[][] display) {
            if (display.length != 24 || display[0].length != 80) {
                logger.fatal("Displays must be 80 x 24");
                throw new IllegalStateException("Displays must be 24 x 80");
            }
            this.display = display;
        }

        /**
         * Repaint the whole screen from {@link #display}: black the panel out, then draw every
         * cell's glyph in its own colour. The port of the {@code text_hook} a {@code main-*.c}
         * module installs ({@code [C] src/z-term.c} calls it), except that C is handed only the
         * runs that changed and this redraws everything.
         *
         * <p>One {@code drawString} per cell - 1,920 of them - which is more calls than needed but
         * not enough to matter at this size. The optimisation, when it is wanted, is the one C's
         * interface already implies: batch each run of same-coloured cells into a single call.
         *
         * <p>Null cells are tolerated and drawn as dark spaces, which covers a grid handed in by
         * {@link #setChars} that was not fully populated - {@code SplashScreen} leaves every cell
         * past the end of a news line unset.
         *
         * <p><b>The bounds check aborts rather than clips, and costs the bottom row.</b> A cell
         * that would extend past the panel's edge ends both loops, so every cell after it is lost -
         * a panel one pixel too narrow loses not just its right-hand column but every row below
         * wherever the check first tripped. The last row goes even at the exact intended size: a
         * glyph on row 23 has its baseline at {@code 23 * charHeight + charAscent}, so
         * {@code top + charHeight} is {@code 24 * charHeight + charAscent} - past the bottom of a
         * panel exactly {@code 24 * charHeight} tall. Row 23 is where the status line and the
         * message prompt go, so this is not a spare row to lose. Clipping the offending cell and
         * continuing would fix both.
         *
         * @param g the graphics context, which Swing may pass as {@code null} before the panel is
         *          realised
         * @author Rowan Crowther
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (g == null) return;

            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(font);
            g.setColor(new Color(255, 255, 255));

            for (int row = 0; row < display.length; row++) {
                for (int col = 0; col < display[0].length; col++) {
                    AngbandDisplayCharacter character = display[row][col];
                    if (character == null)
                        character = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_DARK);
                    int testTop = (row + 1) * charHeight;
                    int testLeft = (col + 1) * charWidth;

                    if (testTop > this.getHeight() || testLeft > this.getWidth()) {
                        logger.warn("Character attempted to be drawn outside the window.");
                        col = display[0].length;
                    } else {
                        int drawTop = (row * charHeight) + charAscent;
                        int drawLeft = col * charWidth;
                        g.setColor(Colour.getColour(character.getAttributeColour()));
                        BasicGraphicsUtils.drawString(g, String.valueOf(character.getCharacter()), -1,
                                drawLeft, drawTop);
                    }
                }
            }
        }

        /**
         * Write one coloured character into a cell, overwriting whatever was there. The port of
         * {@code Term_putch} ({@code [C] src/z-term.c}).
         *
         * <p>Changes the buffer only - the caller repaints when it has finished writing, so a run
         * of writes costs one repaint rather than one each.
         *
         * <p>Unbounded: an off-screen row or column throws a raw
         * {@link ArrayIndexOutOfBoundsException} out of the display layer rather than being
         * rejected. C's {@code Term_putch} returns an error code for a write outside the terminal,
         * on the grounds that a caller computing a position off the edge is common and not fatal.
         *
         * @param row    the row to write to, from the top
         * @param col    the column to write to, from the left
         * @param c      the glyph
         * @param colour the colour to draw it in
         * @author Rowan Crowther
         */
        public void put(int row, int col, char c, ColourEnum colour) {
            if (row >= 24 || row < 0 || col >= 80 || col < 0)
                return;
            display[row][col] = new AngbandDisplayCharacter(c, colour);
        }

        /**
         * Write a string along a row, one character per cell, starting at a column. The port of
         * {@code Term_putstr} ({@code [C] src/z-term.c}).
         *
         * <p>Clipped at the right-hand edge rather than wrapped, which is what C does and what the
         * grid demands: a terminal has no row below the last one to continue onto, and wrapping
         * would silently corrupt whatever was on the next row. Only the part that fits is written.
         *
         * @param row    the row to write along, from the top
         * @param col    the column to start at, from the left
         * @param s      the string to write
         * @param colour the colour to draw it in
         * @author Rowan Crowther
         */
        public void put(int row, int col, String s, ColourEnum colour) {
            int end = col + s.length();
            if (end > 80) end = 80;
            for (int i = col; i < end; i++) {
                put(row, i, s.charAt(i - col), colour);
            }
        }
    }

    /**
     * The game window's window events. Only {@code windowClosing} does anything; the rest are
     * generated overrides that call {@code super} and could go.
     *
     * @author Rowan Crowther
     */
    private WindowListener windowListener = new WindowAdapter() {
        /**
         * The player closed the window: shut the game down.
         *
         * <p>This runs at all only because the frame is {@code DO_NOTHING_ON_CLOSE} - the close
         * button is routed here rather than disposing the window.
         *
         * @param e the close event, not inspected
         * @author Rowan Crowther
         */
        public void windowClosing(WindowEvent e) {
            closeDown();
        }

        /**
         * Invoked when a window has been opened.
         *
         * @param e
         */
        @Override
        public void windowOpened(WindowEvent e) {
            super.windowOpened(e);
        }

        /**
         * Invoked when a window has been closed.
         *
         * @param e
         */
        @Override
        public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
        }

        /**
         * Invoked when a window is iconified.
         *
         * @param e
         */
        @Override
        public void windowIconified(WindowEvent e) {
            super.windowIconified(e);
        }

        /**
         * Invoked when a window is de-iconified.
         *
         * @param e
         */
        @Override
        public void windowDeiconified(WindowEvent e) {
            super.windowDeiconified(e);
        }

        /**
         * Invoked when a window is activated.
         *
         * @param e
         */
        @Override
        public void windowActivated(WindowEvent e) {
            super.windowActivated(e);
        }

        /**
         * Invoked when a window is de-activated.
         *
         * @param e
         */
        @Override
        public void windowDeactivated(WindowEvent e) {
            super.windowDeactivated(e);
        }

        /**
         * Invoked when a window state is changed.
         *
         * @param e
         * @since 1.4
         */
        @Override
        public void windowStateChanged(WindowEvent e) {
            super.windowStateChanged(e);
        }

        /**
         * Invoked when the Window is set to be the focused Window, which means
         * that the Window, or one of its subcomponents, will receive keyboard
         * events.
         *
         * @param e
         * @since 1.4
         */
        @Override
        public void windowGainedFocus(WindowEvent e) {
            super.windowGainedFocus(e);
        }

        /**
         * Invoked when the Window is no longer the focused Window, which means
         * that keyboard events will no longer be delivered to the Window or any of
         * its subcomponents.
         *
         * @param e
         * @since 1.4
         */
        @Override
        public void windowLostFocus(WindowEvent e) {
            super.windowLostFocus(e);
        }
    };
}
