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

package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.SwingUI;

import javax.swing.*;
import java.awt.*;

/**
 * One game window: a frame wrapped around a single character grid. This is the port of C's
 * {@code term} as a platform object - the window a {@code main-*.c} module creates and hands to the
 * core ({@code [C] src/main-win.c} and friends).
 *
 * <p>The frame holds exactly one {@code JPanelArea}, captured by {@link #add} as it goes in so the
 * rest of the front end can reach the grid without walking the component tree. C's terms are a
 * fixed array of eight - a main one plus subwindows - so more of these are expected; nothing here
 * assumes it is the only one.
 *
 * <p>Everything on this class is Swing, so every method belongs on the event dispatch thread.
 * {@link #clear()} in particular is currently reached from the game thread through
 * {@code SplashScreen}, which is a bug in the caller rather than here.
 *
 * @author Rowan Crowther
 */
public class Window extends JFrame {
    /**
     * The character grid this window displays, captured by {@link #add}. Null until a
     * {@code JPanelArea} has been added, which {@code SwingUI.init} does during start-up.
     */
    private SwingUI.JPanelArea area;

    /**
     * Build an empty window with a placeholder title. Nothing is sized, laid out or shown here -
     * {@code SwingUI.init} does all of that once it has measured the font.
     */
    public Window() {
        super("Welcome");
    }

    /**
     * Put a string in the window's title bar.
     *
     * <p>Chrome, not game display - the grid is where the game is drawn. Named for what it will
     * eventually be rather than what it does: this is the hook C's {@code Term_xtra(TERM_XTRA_TITLE)}
     * fills in.
     *
     * @param string the title to show
     */
    public void displayString(String string) {
        super.setTitle(string);
    }

    /**
     * Add a component, and remember it if it is the character grid.
     *
     * <p>Overridden purely to capture the grid on its way in, so {@link #getArea()} has something
     * to hand back without searching the component tree. Everything else is left to
     * {@link java.awt.Container#add(Component)}, including the layout invalidation it performs.
     *
     * <p>Only the last grid added is kept: a second one silently replaces the reference, though the
     * component itself is still added. That is fine while a window holds exactly one grid, which is
     * the arrangement {@code SwingUI.init} builds.
     *
     * @param comp the component to add; kept as this window's grid if it is a {@code JPanelArea}
     * @return the component argument, as {@code Container.add} contracts
     * @throws NullPointerException if {@code comp} is {@code null}
     */
    @Override
    public Component add(Component comp) {
        if (comp instanceof SwingUI.JPanelArea jPanel) {
            area = jPanel;
        }
        return super.add(comp);
    }

    /**
     * Put a whole screen of characters into the grid and repaint it.
     *
     * <p><b>The hop onto the event dispatch thread lives here</b>, not in the caller. Everything that
     * paints now arrives from {@code UILoop}, which runs on the display thread rather than the EDT,
     * so a caller that forgot to hop would be mutating panel state from the wrong thread - the class
     * of bug that shows up as a repaint that half-happened, a long way from its cause. Putting the
     * {@code invokeLater} in the method every caller has to go through makes the guarantee a property
     * of this call rather than something each caller must remember.
     *
     * <p>Whole screen at a time, rather than cell by cell: the messages that reach here replace
     * everything, so there is nothing underneath worth preserving. C's {@code term} hooks work the
     * other way round, drawing runs of characters as they change, and the port will need that shape
     * too once the map is being redrawn per turn rather than per screen.
     *
     * <p>Runs against whatever {@link #getArea()} holds at the time the queued block runs, so a
     * window with no grid added yet fails inside the EDT rather than here.
     *
     * @param display the character grid to show, indexed {@code [row][column]}
     */
    public void display(AngbandDisplayCharacter[][] display) {
        SwingUtilities.invokeLater(() -> {
            area.setChars(display);
            area.repaint();
        });
    }

    /**
     * The character grid this window displays.
     *
     * @return the grid, or {@code null} if none has been added yet
     */
    public SwingUI.JPanelArea getArea() {
        return area;
    }

    /**
     * Blank the whole screen: fill the grid with dark spaces and repaint. The port of
     * {@code Term_clear} ({@code [C] src/z-term.c}).
     *
     * <p>Goes through the grid rather than painting, which is the important part. An earlier
     * version cleared by fetching a {@link Graphics} from the panel and calling
     * {@code clearRect} - that draws outside {@code paintComponent}, so the next repaint threw the
     * result away and the clear appeared not to happen. Writing blanks into the buffer and asking
     * for a repaint is the only way a change survives.
     *
     * <p>Built {@code [row][column]}, matching the panel's own grid and C's {@code term_win}.
     */
    public void clear() {
        AngbandDisplayCharacter[][] clearedDisplay = new AngbandDisplayCharacter[24][80];
        for (int x = 0; x < clearedDisplay.length; x++) {
            for (int y = 0; y < clearedDisplay[x].length; y++) {
                clearedDisplay[x][y] = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_DARK);
            }
        }

        area.setChars(clearedDisplay);
        area.repaint();
    }
}
