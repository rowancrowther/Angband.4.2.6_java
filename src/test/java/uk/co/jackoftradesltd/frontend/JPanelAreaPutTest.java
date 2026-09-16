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

package uk.co.jackoftradesltd.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link SwingUI.JPanelArea#put(int, int, char, ColourEnum)} and
 * {@link SwingUI.JPanelArea#put(int, int, String, ColourEnum)}, checked against C's
 * {@code Term_putch} and {@code Term_putstr} ({@code [C] src/ui-term.c}), by way of the
 * {@code Term_gotoxy}/{@code Term_addch}/{@code Term_addstr} helpers both are built from.
 *
 * <p>{@code display} has no public accessor, so it is read here by reflection, the same pattern
 * {@link SwingUIActiveTermDataTest} uses for {@code SwingUI#terms}.
 *
 * <p><b>Constructing a {@code JPanelArea} means constructing a {@code SwingUI}</b>, the same
 * reason {@link JPanelAreaDisplaySizeTest} needs a display.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class JPanelAreaPutTest {

    private SwingUI.JPanelArea area;

    private static AngbandDisplayCharacter[][] display(SwingUI.JPanelArea area) throws Exception {
        Field field = SwingUI.JPanelArea.class.getDeclaredField("display");
        field.setAccessible(true);
        return (AngbandDisplayCharacter[][]) field.get(area);
    }

    private static void assertBlank(AngbandDisplayCharacter[][] display, int row, int col) {
        AngbandDisplayCharacter cell = display[row][col];
        assertEquals(' ', cell.getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell.getAttributeColour());
    }

    @BeforeEach
    void buildAnArea() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing a JPanelArea constructs the SwingUI enclosing it");

        SwingUI swingUi = new SwingUI(null, null, null);
        area = swingUi.new JPanelArea();
    }

    /**
     * The ordinary case: {@code Term_gotoxy} succeeds and {@code Term_addch} queues the glyph, so
     * exactly the named cell changes.
     */
    @Test
    void putCharWritesTheCell() throws Exception {
        area.put(5, 10, 'k', ColourEnum.COLOUR_RED);

        AngbandDisplayCharacter cell = display(area)[5][10];
        assertEquals('k', cell.getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell.getAttributeColour());
    }

    /**
     * A negative row is declined the way {@code Term_gotoxy} declines {@code y < 0}: nothing is
     * written, and - unlike the class's old Javadoc claim - nothing is thrown either.
     */
    @Test
    void putCharDeclinesNegativeRow() throws Exception {
        assertDoesNotThrow(() -> area.put(-1, 10, 'k', ColourEnum.COLOUR_RED));

        assertBlank(display(area), 0, 10);
    }

    /**
     * A row at or past the grid's height is declined the way {@code Term_gotoxy} declines
     * {@code y >= h}.
     */
    @Test
    void putCharDeclinesRowAtHeight() throws Exception {
        assertDoesNotThrow(() -> area.put(24, 10, 'k', ColourEnum.COLOUR_RED));

        assertBlank(display(area), 23, 10);
    }

    /**
     * A negative column is declined the way {@code Term_gotoxy} declines {@code x < 0}.
     */
    @Test
    void putCharDeclinesNegativeColumn() throws Exception {
        assertDoesNotThrow(() -> area.put(5, -1, 'k', ColourEnum.COLOUR_RED));

        assertBlank(display(area), 5, 0);
    }

    /**
     * A column at or past the grid's width is declined the way {@code Term_gotoxy} declines
     * {@code x >= w}.
     */
    @Test
    void putCharDeclinesColumnAtWidth() throws Exception {
        assertDoesNotThrow(() -> area.put(5, 80, 'k', ColourEnum.COLOUR_RED));

        assertBlank(display(area), 5, 79);
    }

    /**
     * The ordinary case for the string form: every character lands in its own cell, in order,
     * matching {@code Term_addstr}'s character-by-character queueing.
     */
    @Test
    void putStringWritesEachCell() throws Exception {
        area.put(3, 2, "hi", ColourEnum.COLOUR_BLUE);

        AngbandDisplayCharacter[][] display = display(area);
        assertEquals('h', display[3][2].getCharacter());
        assertEquals('i', display[3][3].getCharacter());
        assertEquals(ColourEnum.COLOUR_BLUE, display[3][2].getAttributeColour());
        assertEquals(ColourEnum.COLOUR_BLUE, display[3][3].getAttributeColour());
    }

    /**
     * A string overrunning the right edge is clipped, not wrapped - {@code Term_addstr} computes
     * {@code n = w - cx} once {@code cx + n >= w} ({@code [C] src/ui-term.c:2147}) and queues only
     * that many characters. Starting at column 75 with an 8-character string leaves room for 5.
     */
    @Test
    void putStringClipsAtTheRightEdge() throws Exception {
        area.put(1, 75, "abcdefgh", ColourEnum.COLOUR_GREEN);

        AngbandDisplayCharacter[][] display = display(area);
        assertEquals('a', display[1][75].getCharacter());
        assertEquals('b', display[1][76].getCharacter());
        assertEquals('c', display[1][77].getCharacter());
        assertEquals('d', display[1][78].getCharacter());
        assertEquals('e', display[1][79].getCharacter());
    }

    /**
     * A negative starting column declines the whole call, matching {@code Term_putstr} calling
     * {@code Term_gotoxy} first ({@code [C] src/ui-term.c:2238}): a negative {@code x} fails there
     * and {@code Term_addstr} never runs, so C draws none of the string - not even the trailing
     * part that would otherwise fit on screen.
     */
    @Test
    void putStringDeclinesNegativeColumn() throws Exception {
        assertDoesNotThrow(() -> area.put(2, -3, "abcdef", ColourEnum.COLOUR_YELLOW));

        AngbandDisplayCharacter[][] display = display(area);
        assertBlank(display, 2, 0);
        assertBlank(display, 2, 1);
        assertBlank(display, 2, 2);
    }

    /**
     * A starting column exactly at the grid's width fails {@code Term_gotoxy} the same way a
     * negative one does, so this too draws nothing.
     */
    @Test
    void putStringDeclinesColumnAtWidth() throws Exception {
        assertDoesNotThrow(() -> area.put(2, 80, "hi", ColourEnum.COLOUR_YELLOW));

        assertBlank(display(area), 2, 79);
    }
}
