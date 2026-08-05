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

import uk.co.jackoftrades.StartupOptions;
import uk.co.jackoftrades.frontend.colour.Colour;
import uk.co.jackoftrades.frontend.screen.Window;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplay;
import uk.co.jackoftrades.middle.game.event.statusdisplay.StatusDisplayHolder;
import uk.co.jackoftrades.middle.game.gameengine.GameRunner;

import javax.swing.*;
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
 * <p>Barely started: {@link #init} builds one blank 80x24 window and starts the game thread. The
 * panel paints a black rectangle and no glyphs, and the window list only ever holds the one
 * window it was constructed with.
 *
 * @author Rowan Crowther
 */
public class Frontend implements StatusDisplay {
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
        mainPanel.setPreferredSize(new Dimension(80 * charWidth, 24 * charHeight));
        activeWindow.add(mainPanel);
        activeWindow.pack();
        activeWindow.setLocationRelativeTo(null);

        activeWindow.setVisible(true);

        // Register this as the status display holder
        StatusDisplayHolder.setInstance(this);

        gameRunner.start();
    }

    @Override
    public void showInitStatus(String text) {
        SwingUtilities.invokeLater(() -> activeWindow.displayString(text));
    }

    /**
     * The character grid: the component the game is actually drawn on, and the port's {@code Term}
     * surface.
     *
     * <p>A placeholder. {@link #paintComponent} blacks the panel out and sets up the pen, but
     * draws no glyphs - there is nothing to draw until the renderer is ported.
     *
     * <p>The font and metrics are static, so they are shared by every panel in the JVM rather than
     * belonging to the front end that measured them. They are per-front-end values, and a second
     * front end with a different font would overwrite them.
     *
     * @author Rowan Crowther
     */
    private class JPanelArea extends JPanel {
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
         * Clear to black and set the pen to the font and white. Glyph drawing lands here when the
         * renderer is ported.
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
         * button is routed here rather than disposing the window, so shutdown goes through
         * {@link #closeDown()} and the game thread is told about it.
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
