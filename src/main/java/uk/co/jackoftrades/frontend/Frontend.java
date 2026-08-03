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

import uk.co.jackoftrades.backend.colour.ColourEnum;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.colour.Colour;
import uk.co.jackoftrades.frontend.screen.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Frontend {
    private List<Window> windows;
    private Window activeWindow;

    public Frontend() {
        this.windows = new ArrayList<>();
        Window main = new Window();
        windows.add(main);
        activeWindow = main;
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
        activeWindow = new Window();
        activeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        activeWindow.setSize(80 * charWidth, 24 * charHeight);
        activeWindow.setTitle("Test Window");
        JPanelArea mainPanel = new JPanelArea();
        mainPanel.setPreferredSize(new Dimension(80 * charWidth, 24 * charHeight));
        activeWindow.add(mainPanel);
        activeWindow.pack();
        activeWindow.setLocationRelativeTo(null);
        activeWindow.setVisible(true);
    }

    private static class JPanelArea extends JPanel {
        public static Font font;
        public static int charWidth;
        public static int charHeight;
        public static int charAscent;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (g == null) return;

            List<AngbandDisplayCharacter> characters = new ArrayList<>();
            characters.add(new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE));
            characters.add(new AngbandDisplayCharacter('D', ColourEnum.COLOUR_RED));
            characters.add(new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_SHADE));

            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setFont(font);
        }
    }
}
