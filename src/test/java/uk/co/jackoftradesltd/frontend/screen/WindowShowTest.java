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

package uk.co.jackoftradesltd.frontend.screen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.SwingUI;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Frame;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link Window#show(Frame)} has no C original to check against - {@link Frame} is new
 * architecture, and {@code show} is the Java side of the boundary crossing it exists for. What
 * this class checks instead is the contract {@code show}'s own Javadoc and
 * {@link CellGrid#getCells()}'s Javadoc both describe: the call lands on the event dispatch
 * thread even though every real caller is off it, and the grid the panel ends up painting from
 * shares row arrays with the {@link CellGrid} the {@link Frame} was built from, rather than
 * copying them.
 *
 * <p>{@code display} has no public accessor, so it is read here by reflection, the same pattern
 * {@code JPanelAreaPutTest} uses.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class WindowShowTest {

    private Window window;

    private static AngbandDisplayCharacter[][] display(SwingUI.JPanelArea area) throws Exception {
        Field field = SwingUI.JPanelArea.class.getDeclaredField("display");
        field.setAccessible(true);
        return (AngbandDisplayCharacter[][]) field.get(area);
    }

    private static AngbandDisplayCharacter[][] cellsOf(CellGrid grid) throws Exception {
        Field field = CellGrid.class.getDeclaredField("cells");
        field.setAccessible(true);
        return (AngbandDisplayCharacter[][]) field.get(grid);
    }

    @BeforeEach
    void buildAWindowWithAPanel() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: a Window is a JFrame");

        SwingUI swingUi = new SwingUI(null, null, null);
        window = swingUi.getActiveWindow();
        window.add(swingUi.new JPanelArea());
    }

    /**
     * Flushes the event queue: {@link Window#show} defers onto the EDT with
     * {@link SwingUtilities#invokeLater}, so a test calling it from an ordinary thread - the same
     * position every real caller is in - must wait for that queued task to run before asserting
     * on its effect. An empty {@link SwingUtilities#invokeAndWait} runs after every runnable
     * already queued ahead of it, {@code show}'s included.
     */
    private void flushEventQueue() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
        });
    }

    /**
     * The ordinary case: the panel paints from the frame's grid once {@code show} has run on the
     * EDT, the counterpart to what C's {@code Term_fresh()} flush makes visible.
     */
    @Test
    void showPaintsTheFramesGrid() throws Exception {
        CellGrid grid = new CellGrid(24, 80);
        grid.set(5, 10, new AngbandDisplayCharacter('k', ColourEnum.COLOUR_RED));
        Frame frame = new Frame(grid, List.of());

        window.show(frame);
        flushEventQueue();

        AngbandDisplayCharacter cell = display(window.getArea())[5][10];
        assertEquals('k', cell.getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell.getAttributeColour());
    }

    /**
     * {@link CellGrid#getCells()}'s Javadoc documents that its rows are shared with the grid it
     * was read from, not copied - {@code show} is named there as the one caller that discipline
     * relies on. This checks that sharing actually happens: the row array the panel paints from
     * after {@code show} is the very same array instance the {@link CellGrid} holds internally,
     * not a structurally-equal copy of it.
     */
    @Test
    void showSharesRowsRatherThanCopyingThem() throws Exception {
        CellGrid grid = new CellGrid(24, 80);
        grid.set(0, 0, new AngbandDisplayCharacter('@', ColourEnum.COLOUR_WHITE));
        Frame frame = new Frame(grid, List.of());

        window.show(frame);
        flushEventQueue();

        AngbandDisplayCharacter[][] gridCells = cellsOf(grid);
        AngbandDisplayCharacter[][] painted = display(window.getArea());
        assertSame(gridCells[0], painted[0]);
    }
}
