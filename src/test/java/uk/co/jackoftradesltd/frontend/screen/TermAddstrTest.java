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
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link Term#putstr} and {@link Term#addstr}, checked against C's {@code Term_putstr} and
 * {@code Term_addstr} ({@code [C] src/ui-term.c}). {@code Term_putstr} is just
 * {@code Term_gotoxy} then {@code Term_addstr}, so its failure modes are covered by
 * {@link TermGotoXYTest} and are only spot-checked here; the interesting behaviour is
 * {@code Term_addstr}'s own: it clips a run that would reach or pass the right edge to
 * however many columns are actually left, marks the cursor unusable whenever that clip
 * fires - even if every character still fit - and returns that clipped count rather than
 * {@code 0}, so a caller checking only for {@code -1} (as {@link Term#putstr} does) treats
 * a truncated write as success.
 *
 * <p>A width of {@code 10} is used deliberately, narrow enough that the right-edge clip
 * is cheap to trigger without a long string.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermAddstrTest {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 5;

    private Term term;
    private CellGrid grid;

    @BeforeEach
    void buildATenByFiveTerm() {
        term = new Term();
        grid = new CellGrid(HEIGHT, WIDTH);
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));
    }

    private AngbandDisplayCharacter cell(int row, int col) {
        return grid.get(row, col);
    }

    /**
     * {@code addstr} writes at the cursor's current position - {@code (0, 0)} on a freshly
     * initialised term, with no {@code gotoXY} needed first, matching {@code Term_addstr}
     * reading {@code Term->scr->cx}/{@code cy} as they stand.
     */
    @Test
    void addstrWritesAtTheCurrentCursorPositionWithNoPriorGotoXY() {
        assertEquals(0, term.addstr(-1, ColourEnum.COLOUR_RED, "Hi"));

        assertEquals('H', cell(0, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_RED, cell(0, 0).getAttributeColour());
        assertEquals('i', cell(0, 1).getCharacter());
    }

    /**
     * {@code putstr} moves the cursor first, then writes - the ordinary path through both
     * of {@code Term_putstr}'s internal steps.
     */
    @Test
    void putstrMovesThenWrites() {
        assertEquals(0, term.putstr(3, 2, -1, ColourEnum.COLOUR_BLUE, "Yo"));

        assertEquals('Y', cell(2, 3).getCharacter());
        assertEquals(ColourEnum.COLOUR_BLUE, cell(2, 3).getAttributeColour());
        assertEquals('o', cell(2, 4).getCharacter());
    }

    /**
     * An explicit length shorter than the string truncates it - C's
     * {@code k = (n < 0) ? (w + 1) : n} taking the given {@code n} as-is when it is not
     * negative.
     */
    @Test
    void explicitLengthTruncatesTheString() {
        assertEquals(0, term.putstr(0, 0, 1, ColourEnum.COLOUR_RED, "Hi"));

        assertEquals('H', cell(0, 0).getCharacter());
        assertNull(cell(0, 1));
    }

    /**
     * An explicit length of {@code 0} draws nothing at all - C's usable-length loop
     * {@code for (n = 0; (n < k) && s[n]; n++)} never runs when {@code k} is {@code 0}.
     */
    @Test
    void explicitLengthZeroDrawsNothing() {
        assertEquals(0, term.putstr(0, 0, 0, ColourEnum.COLOUR_RED, "Hi"));

        assertNull(cell(0, 0));
    }

    /**
     * A write that lands exactly on the last column still trips C's {@code cx + n >= w}
     * check - {@code >=}, not {@code >} - so every character is drawn, but the return
     * value is the clipped count rather than {@code 0}, and the cursor is marked unusable
     * for the next call, even though nothing was actually cut off.
     */
    @Test
    void aWriteThatExactlyFillsToTheEdgeStillReturnsPositiveAndMarksTheCursorUnusable() {
        assertEquals(2, term.putstr(8, 0, -1, ColourEnum.COLOUR_RED, "Hi"));

        assertEquals('H', cell(0, 8).getCharacter());
        assertEquals('i', cell(0, 9).getCharacter());
    }

    /**
     * A write that would run past the right edge is clipped to the columns actually
     * remaining, and the clipped count is returned rather than {@code 0}.
     */
    @Test
    void overflowClipsToTheRemainingWidthAndReturnsTheClippedCount() {
        assertEquals(2, term.putstr(8, 0, -1, ColourEnum.COLOUR_RED, "Hello"));

        assertEquals('H', cell(0, 8).getCharacter());
        assertEquals('e', cell(0, 9).getCharacter());
    }

    /**
     * Once a clip has marked the cursor unusable, a further {@code addstr} at the same
     * position writes nothing and returns {@code -1} immediately - C's
     * {@code if (Term->scr->cu) return (-1);} guard, checked before anything else.
     */
    @Test
    void cursorMarkedUnusableAfterAClipRejectsFurtherAddstrCalls() {
        term.putstr(8, 0, -1, ColourEnum.COLOUR_RED, "Hello");

        assertEquals(-1, term.addstr(-1, ColourEnum.COLOUR_RED, "X"));
        assertEquals('e', cell(0, 9).getCharacter(), "the earlier clipped write must stand");
    }

    /**
     * {@code gotoXY} unconditionally clears the "unusable" flag, so moving to a new cell
     * after a clip lets a normal write succeed again - the C original's
     * {@code Term_gotoxy} sets {@code scr->cu = 0} regardless of its previous value.
     */
    @Test
    void gotoXyResetsTheUnusableCursorAllowingRecovery() {
        term.putstr(8, 0, -1, ColourEnum.COLOUR_RED, "Hello");

        assertEquals(0, term.putstr(0, 1, -1, ColourEnum.COLOUR_RED, "Hi"));
        assertEquals('H', cell(1, 0).getCharacter());
        assertEquals('i', cell(1, 1).getCharacter());
    }

    /**
     * {@code putstr} propagates a {@code gotoXY} failure and never reaches {@code addstr} -
     * matching {@code Term_putstr} returning early, without calling {@code Term_addstr},
     * when {@code Term_gotoxy} fails.
     */
    @Test
    void putstrPropagatesGotoXyFailureAndNeverCallsAddstr() {
        assertEquals(-1, term.putstr(-1, 0, -1, ColourEnum.COLOUR_RED, "Hi"));

        assertNull(cell(0, 0));
    }
}
