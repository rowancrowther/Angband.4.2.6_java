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

import uk.co.jackoftrades.frontend.colour.Colour;
import uk.co.jackoftrades.frontend.screen.Window;
import uk.co.jackoftrades.middle.game.gameengine.GameRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frontend {
    private List<Window> windows;
    private Window activeWindow;
    private GameRunner gameRunner;

    public Frontend(GameRunner gameRunner) {
        this.windows = new ArrayList<>();
        Window main = new Window();
        windows.add(main);
        activeWindow = main;
        this.gameRunner = gameRunner;
    }

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
        int charAscent = metrics.getAscent();

        JPanelArea.charAscent = charAscent;
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

        gameRunner.start();
        activeWindow.setVisible(true);
    }

    private class JPanelArea extends JPanel {
        public static Font font;
        public static int charWidth;
        public static int charHeight;
        public static int charAscent;

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

    private WindowListener windowListener = new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            gameRunner.requestStop();
            System.exit(0);
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
