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

import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link Window#erase}, the wrapper that hands the actual work to
 * {@link SwingUI.JPanelArea#erase} and then repaints - the same two-step shape as
 * {@link Window#clear()}. {@link JPanelAreaEraseTest} already pins the erase behaviour itself
 * against C's {@code Term_erase}; what this class checks is that the wrapper reaches the same
 * grid the panel does, rather than a copy of it.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class WindowEraseTest {

    private static final int ROWS = 24;
    private static final int COLS = 80;

    private static final AngbandDisplayCharacter MARKER =
            new AngbandDisplayCharacter('#', ColourEnum.COLOUR_RED);

    private static final AngbandDisplayCharacter BLANK =
            new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);

    private Window window;
    private AngbandDisplayCharacter[][] grid;

    @BeforeEach
    void buildAWindowOverAFullyMarkedGrid() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: a Window is a JFrame");

        SwingUI swingUi = new SwingUI(null, null, null);
        window = swingUi.getActiveWindow();
        window.add(swingUi.new JPanelArea());

        grid = new AngbandDisplayCharacter[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = MARKER;
            }
        }
        window.getArea().setChars(grid);
    }

    private void assertRowErasedOnly(int row, int erasedLo, int erasedHi) {
        for (int col = 0; col < COLS; col++) {
            AngbandDisplayCharacter expected = (col >= erasedLo && col <= erasedHi) ? BLANK : MARKER;
            assertEquals(expected, grid[row][col],
                    "row " + row + ", column " + col + " should be "
                            + (expected == BLANK ? "blanked" : "untouched"));
        }
    }

    /**
     * The wrapper reaches the panel's real grid, not some copy of it - erasing through the
     * window is visible in the same array {@link SwingUI.JPanelArea#setChars} was given.
     */
    @Test
    void erasingThroughTheWindowReachesThePanelsGrid() {
        window.erase(20, 11, 6);

        assertRowErasedOnly(11, 20, 25);
    }

    /**
     * An out-of-range coordinate is a no-op here for the same reason it is in
     * {@link JPanelAreaEraseTest} - the wrapper adds nothing of its own, so it inherits the
     * panel's guard rather than needing one twice.
     */
    @Test
    void anOutOfRangeCoordinateIsANoOpThroughTheWindowToo() {
        window.erase(-1, 3, 5);

        assertRowErasedOnly(3, 0, -1);
    }

    /**
     * The repaint {@code erase} triggers must not itself throw, on a window that has never been
     * packed or shown - the case a real call after a screen update would be made from.
     */
    @Test
    void erasingRepaintsWithoutThrowing() {
        assertDoesNotThrow(() -> window.erase(0, 0, COLS));
    }
}
