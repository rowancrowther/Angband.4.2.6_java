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
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * {@link Term#gotoXY}, checked against C's {@code Term_gotoxy} ({@code [C] src/ui-term.c}):
 * {@code (x < 0) || (x >= w)} and {@code (y < 0) || (y >= h)} reject the request, everything
 * else moves the cursor and clears its "unused" flag.
 *
 * <p>{@link TermWin} has no getters for {@code cx}/{@code cy}/{@code cu} - only the setters
 * {@link Term#gotoXY} itself calls - so cursor state is read and seeded here by reflection,
 * the same pattern {@code PlayerStatValueAccessorTest} uses for {@code Player}'s private
 * fields.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermGotoXYTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;

    private Term term;
    private TermWin scr;

    private static Field field(Object owner, String name) throws Exception {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @BeforeEach
    void buildAnEightyByTwentyFourTerm() throws Exception {
        term = new Term();
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(new CellGrid(HEIGHT, WIDTH), new ArrayList<>()));
        scr = (TermWin) field(term, "scr").get(term);
    }

    private void seedCursor(int x, int y, boolean cu) throws Exception {
        field(scr, "cx").setInt(scr, x);
        field(scr, "cy").setInt(scr, y);
        field(scr, "cu").setBoolean(scr, cu);
    }

    private int cx() throws Exception {
        return field(scr, "cx").getInt(scr);
    }

    private int cy() throws Exception {
        return field(scr, "cy").getInt(scr);
    }

    private boolean cu() throws Exception {
        return field(scr, "cu").getBoolean(scr);
    }

    /**
     * An ordinary in-range move: succeeds, and the cursor lands exactly on the requested
     * cell with its "unused" flag cleared - {@code Term->scr->cx/cy/cu} in
     * {@code Term_gotoxy}.
     */
    @Test
    void anOrdinaryMoveSucceedsAndPositionsTheCursor() throws Exception {
        seedCursor(1, 1, true);

        assertEquals(0, term.gotoXY(10, 5));
        assertEquals(10, cx());
        assertEquals(5, cy());
        assertFalse(cu(), "gotoXY must clear the cursor's unused flag");
    }

    /**
     * The top-left corner, {@code (0, 0)}, is in range.
     */
    @Test
    void theFirstCellIsInRange() {
        assertEquals(0, term.gotoXY(0, 0));
    }

    /**
     * The bottom-right corner, {@code (width - 1, height - 1)}, is in range - C's bound is
     * {@code x >= w}, not {@code x > w}, so the last column and row are still legal.
     */
    @Test
    void theLastCellIsInRange() {
        assertEquals(0, term.gotoXY(WIDTH - 1, HEIGHT - 1));
    }

    /**
     * A negative column is rejected.
     */
    @Test
    void aNegativeColumnIsRejected() {
        assertEquals(-1, term.gotoXY(-1, 5));
    }

    /**
     * A negative row is rejected.
     */
    @Test
    void aNegativeRowIsRejected() {
        assertEquals(-1, term.gotoXY(5, -1));
    }

    /**
     * A column equal to the width is one past the last valid column - C's {@code x >= w}.
     */
    @Test
    void aColumnEqualToTheWidthIsRejected() {
        assertEquals(-1, term.gotoXY(WIDTH, 5));
    }

    /**
     * A row equal to the height is one past the last valid row - C's {@code y >= h}.
     */
    @Test
    void aRowEqualToTheHeightIsRejected() {
        assertEquals(-1, term.gotoXY(5, HEIGHT));
    }

    /**
     * "Illegal requests do not move the cursor" - {@code Term_gotoxy}'s own comment. A
     * rejected request must leave the previously-seeded cursor state exactly as it was.
     */
    @Test
    void aRejectedRequestLeavesTheCursorWhereItWas() throws Exception {
        seedCursor(3, 4, false);

        assertEquals(-1, term.gotoXY(-1, -1));
        assertEquals(3, cx());
        assertEquals(4, cy());
        assertFalse(cu());
    }
}
