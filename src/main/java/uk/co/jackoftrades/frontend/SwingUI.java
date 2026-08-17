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
import uk.co.jackoftrades.channel.StartupOptions;
import uk.co.jackoftrades.channel.EDTChannel;
import uk.co.jackoftrades.channel.UIChannel;
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.enums.UILifecycleEvent;
import uk.co.jackoftrades.channel.globals.Angband;
import uk.co.jackoftrades.channel.messages.UIMessage;
import uk.co.jackoftrades.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.colour.Colour;
import uk.co.jackoftrades.frontend.inputfromuser.UILoop;
import uk.co.jackoftrades.frontend.screen.Window;

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
 * The Swing front end: owns the game's windows and nothing else. It stands in for C's
 * {@code main-*.c} modules ({@code [C] src/main-gcu.c} and friends), each of which builds its
 * platform's terms and installs the hooks the core calls back through.
 *
 * <p><b>Two threads share this object, and which is which matters.</b> {@link #init()} is queued
 * onto Swing's event dispatch thread (EDT) by {@code main()}, so everything it builds is built
 * there, and the window listener fires there too. {@link #startLoop()} runs on the UI thread and
 * stays there for the session, blocking on the inbox; the painting it drives hops back to the EDT
 * through {@code invokeLater}. The middle end is a third thread this class knows nothing about.
 *
 * <p><b>The import list now respects the boundary.</b> Stage 4 removed the last of the middle-end
 * imports: this class held a {@code Core} as its single handle on the middle end, and built a
 * core-side display object to push into a static slot the core read. {@code main()} now gives each
 * half its channel ends, and the core's own handlers send on theirs, so what is left here names
 * {@code channel} and the front end's own packages - which is what makes the boundary checkable by
 * reading the imports, and what stage 5's boundary test will pin down.
 *
 * <p>State is per-instance, not static. A previous version made the fields static so a static
 * {@code closeDown} could reach them, which left the class half-static and would have let a second
 * front end quietly overwrite the first one's windows.
 *
 * <p>Barely started: {@link #init()} builds one 80x24 window. The panel can now hold and paint a
 * grid of coloured characters, but the window list only ever holds the one window it was
 * constructed with, and nothing yet drives the grid except the splash screen.
 *
 * @author Rowan Crowther
 */
public class SwingUI {
    private static final Logger logger = LogManager.getLogger(SwingUI.class);

    /**
     * This half's pair of channel ends: the inbox {@link #uiLoop} reads, and the sender
     * {@link #sendStartToCore} reports readiness on.
     */
    private UIChannel uiChannel;

    /**
     * The EDT's whole view of the channels: a send-only end, given to the window listener.
     *
     * <p>Narrow on purpose. A listener holding a full {@link UIChannel} could call {@code receive()}
     * and park the event dispatch thread, which is the one thing the front end must never do; there
     * is no method here to do it with, so the rule is enforced by what the EDT was handed rather
     * than by remembering it.
     */
    private EDTChannel edtChannel;

    /**
     * Every window this front end has opened, so shutdown can dispose them all. Holds exactly one
     * for now; C's terms are a fixed array of eight.
     */
    private List<Window> windows;
    /**
     * The window currently being drawn to and configured - C's {@code Term}, the term that
     * display calls implicitly act on.
     */
    private Window activeWindow;

    /**
     * The consumer half: the loop that reads this half's inbox and paints what the core sent.
     *
     * <p>Built in the constructor and entered by {@link #startLoop()}, which is the UI thread's
     * whole body from then on. It is given this front end, so the two refer to each other: the loop
     * needs a window to paint into and this class needs somewhere for the thread to go.
     *
     * <p>That mutual reference is the one shortcut stage 4 left in place. The alternative was for
     * {@code main()} to build the loop and pass it the front end, which would make this class the
     * display and nothing else; keeping it here makes {@code SwingUI} the single public face of the
     * front end. Either is defensible, and the choice is recorded rather than settled.
     */
    private UILoop uiLoop;

    /**
     * The parsed command line, handed in at construction by {@code main()}.
     *
     * <p>It arrived through {@link #init} until stage 4, which is why that method used to take a
     * parameter it no longer needs; both halves are now given their own copy up front.
     *
     * <p>Nothing reads it yet. {@code requestGraphicsMode} is the component that belongs to this
     * class - tiles or plain text is a decision taken while building the window - and the
     * savefile group is not the front end's to act on.
     */
    private StartupOptions startupOptions;

    /**
     * Build the front end around the channel ends {@code main()} gives it, and make its first
     * window.
     *
     * <p>Only assembles state: nothing is shown until {@link #init()} and nothing is read from the
     * inbox until {@link #startLoop()}. The three arguments are the whole of what this half is
     * given - no handle on the core, and no way to obtain one.
     *
     * <p><b>Runs on the UI thread, not the EDT</b>, unlike everything it builds. That is the benign
     * case rather than a violation: the {@link Window} made here is not yet realised, no other
     * thread has a reference to it, and the {@code invokeLater} that queues {@link #init()}
     * establishes the happens-before edge the EDT needs before it touches any of it.
     *
     * @param uiChannel      this half's pair of channel ends
     * @param edtChannel     the send-only end the window listener will post on
     * @param startupOptions the parsed command line
     */
    public SwingUI(UIChannel uiChannel, EDTChannel edtChannel, StartupOptions startupOptions) {
        this.uiChannel = uiChannel;
        this.edtChannel = edtChannel;
        this.startupOptions = startupOptions;

        uiLoop = new UILoop(uiChannel, this);

        windows = new ArrayList<>();
        Window main = new Window();
        windows.add(main);
        activeWindow = main;
    }

    /**
     * The game window's window events. Only {@code windowClosing} does anything; the rest are
     * generated overrides that call {@code super} and could go.
     */
    private WindowListener windowListener = new WindowAdapter() {
        /**
         * The player closed the window: report it, and stop. The first step of the shutdown
         * handshake and the only one taken on the EDT.
         *
         * <p>This runs at all only because the frame is {@code DO_NOTHING_ON_CLOSE} - the close
         * button is routed here rather than disposing the window, so there is a chance to save
         * before the display goes.
         *
         * <p><b>Forwarding, and nothing else.</b> The whole body is one message onto the UI
         * thread's inbox. It decides nothing - not whether to save, not whether to exit, not even
         * whether the window really should close - because deciding here would mean deciding on
         * the EDT, and every interesting answer involves waiting for the core. A listener that
         * waits is a frozen window. So the EDT states the fact and the UI thread, which is allowed
         * to block, runs the exchange: see {@code UILoop.loop} for the other three steps.
         *
         * <p>The {@link WindowEvent} itself does not cross. It is a Swing object belonging to this
         * thread, and the channel exists to keep exactly that kind of thing on the side it came
         * from; what crosses is a record saying what happened.
         *
         * @param e the close event, not inspected - there is one window, and its identity is
         *          implied
         */
        public void windowClosing(WindowEvent e) {
            UIMessage closingDown = new UIMessage.WindowCloseRequested();
            edtChannel.edtSender().send(closingDown);
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

    /**
     * Dispose every window. The last step of the shutdown handshake, and now the whole of it that
     * happens front-end side.
     *
     * <p><b>Must run on the EDT.</b> {@link java.awt.Window#dispose()} touches Swing state, and
     * this method is called from the UI thread's loop, so the call is wrapped in
     * {@code invokeLater} by its caller rather than here - the same convention the painting
     * methods follow. Called directly from another thread it would be a race, not an error, which
     * is the kind that shows up once and never reproduces.
     *
     * <p><b>Three things this deliberately no longer does.</b> It does not stop the game thread:
     * that thread has already stopped itself, which is what the {@code STOPPED} message the caller
     * just received means. It does not call {@link System#exit}: with every window disposed the
     * EDT has nothing left to keep it alive, so the JVM exits on its own once the last non-daemon
     * thread finishes. And it is no longer the window listener's target - the listener posts a
     * message and this runs at the end of the exchange that message begins.
     *
     * <p>The window is set to {@code DO_NOTHING_ON_CLOSE} to make that exchange possible: the
     * close button starts the handshake instead of quietly disposing the frame, which is the
     * port's equivalent of C routing a quit through {@code quit_aux} rather than letting the
     * display vanish underneath the game. What C does inside {@code quit_aux} - logging, the front
     * end's cleanup hook - has no equivalent here yet.
     */
    public void closeDown() {
        for (Window window : windows) {
            window.dispose();
        }
    }
    
    /**
     * Tell the core the front end is up and it may begin. The document's step 1.5, and the first
     * message ever to cross in this direction.
     *
     * <p>Sent after the window is visible, so the core's first act - loading the data and
     * reporting its progress - has somewhere to be painted. Nothing enforces that ordering, and
     * nothing needs to: the channel buffers, so a message sent early would wait rather than be
     * lost. Sending it late is a choice about what the player sees, not about correctness.
     *
     * <p>Sent from the EDT, on {@link #init()}'s last line, while the core is already running and
     * blocked on its inbox waiting for exactly this. Every other thing this half tells the core is
     * sent by the UI thread from {@code UILoop}.
     *
     * <p><b>Which makes this the one place the EDT reaches past its own end.</b> The whole point of
     * {@link EDTChannel} is that the event dispatch thread is given a send-only view and so cannot
     * block on a receive; but {@link #init()} now runs on the EDT and this method sends through
     * {@link #uiChannel}, the full pair, because it is a field of an object the EDT is running
     * inside. Harmless in itself - a send is a send - but it means the containment rests on nobody
     * calling {@code receive()} here rather than on there being no way to. Worth settling when the
     * boundary test arrives. Note that simply switching to {@link #edtChannel} would not do it:
     * that end writes to this half's own inbox, not the core's, so the EDT would be telling the UI
     * thread to tell the core - the shape the close request already has, and a defensible answer,
     * but a different one from what this line does today.
     */
    private void sendStartToCore() {
        UIMessage.LifecycleUIMessage uiMessage = new UIMessage.LifecycleUIMessage(UILifecycleEvent.START);
        uiChannel.uiSender().send(uiMessage);
    }

    /**
     * The window display calls currently act on - C's {@code Term}.
     *
     * @return the active window
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
         * The grid font, measured and installed by {@link SwingUI#init}.
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
         */
        private AngbandDisplayCharacter[][] display = new AngbandDisplayCharacter[24][80];

        /**
         * Build a panel over a blank screen: every cell a dark space, so the grid is fully
         * populated before anything paints and no cell is ever null on the first repaint.
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
     * Hand the calling thread to the inbox loop. One line, and the line that makes a thread the UI
     * thread.
     *
     * <p><b>This does not return until the session ends.</b> {@code main()} calls it as the last
     * statement of the UI thread's body, so the thread spends its life inside
     * {@code UILoop.loop()}, blocked on the queue; when the core's {@code STOPPED} arrives the loop
     * returns, this returns, and that thread finishes - which is one of the two events that let the
     * JVM exit.
     *
     * <p>Called after {@link #init()} has been queued but without waiting for it. Safe because the
     * core sends nothing before the {@code START} that {@code init} ends with, so this loop cannot
     * be handed anything to paint before there is a window to paint it into.
     */
    public void startLoop() {
        uiLoop.loop();
    }

    /**
     * Bring the front end up: size the window from the chosen font, wire the close handler, show
     * it, and tell the core to begin. The port of a {@code main-*.c} module's {@code init_*}
     * function, which C calls before {@code init_angband()} so the display exists to report
     * loading errors on.
     *
     * <p><b>Runs on the EDT.</b> {@code main()} queues it there with {@code invokeLater} rather
     * than calling it on the UI thread, because every line below touches a Swing component and the
     * window is realised part-way through.
     *
     * <p>The metrics drive everything. Angband is written against a character grid, so the window
     * is sized as 80x24 cells of whatever the font's {@code 'M'} measures - the port's equivalent
     * of C asking a terminal how big it is. {@code TerminalVector} is preferred and the platform
     * monospace is the fallback, so the grid stays square-ish on a machine without the game font.
     *
     * <p>Ordering worth keeping: the listener is attached before the window is shown, so a close
     * can never arrive before there is something to handle it, and {@link #sendStartToCore} comes
     * last, so the core cannot begin reporting progress before there is a window for it to be
     * reported into. An exception before {@code setVisible} leaves the JVM alive with no window on
     * screen, because {@code pack()} has already made the frame displayable and so kept the EDT
     * running.
     *
     * <p><b>The last line is the whole of the channel wiring left here.</b> It used to be four:
     * this method also built a core-side status display and pushed it into the core's holder, and
     * started a separate {@code angband-display} thread for the inbox loop. Stage 4 took both away
     * - the core installs its own display now, and the loop runs on the UI thread that queued this
     * method rather than on one of its own.
     *
     * <p><b>This method returns as soon as the window is up; it waits for nothing.</b> The session
     * carries on in two other places: {@link #startLoop()} on the UI thread, and the core on its.
     * The program ends when both of those finish and {@code main()}'s two joins return.
     */
    public void init() {
        Colour.init();

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
        activeWindow.setTitle(Angband.buildId);
        activeWindow.addWindowListener(windowListener);

        JPanelArea mainPanel = new JPanelArea();
        mainPanel.setToolTipText("Main Panel");
        mainPanel.setPreferredSize(new Dimension(80 * charWidth, 24 * charHeight));
        activeWindow.add(mainPanel);
        activeWindow.pack();
        activeWindow.setLocationRelativeTo(null);

        activeWindow.setVisible(true);

        // The display is up, so the core may begin. Installing the core's display object used to
        // happen here too; the core's own handlers now send on the sender they are constructed
        // with, so this side has nothing to register.
        sendStartToCore();
    }
}
