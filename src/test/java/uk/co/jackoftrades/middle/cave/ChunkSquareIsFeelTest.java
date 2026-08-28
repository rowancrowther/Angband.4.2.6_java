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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.squareIsFeel}, the port of C's {@code square_isfeel}
 * ({@code cave-square.c}).
 *
 * <p>The C body is a single {@code sqinfo_has(square(c, grid)->info, SQUARE_FEEL)} behind an
 * in-bounds assert, so the expected values here come from what that flag test means rather than
 * from branches the function does not have. Four things are worth pinning down: the answer comes
 * from {@code SQUARE_FEEL} and not from any of the flags stored beside it; it is read from the
 * grid asked about and no other; it is a pure read, so asking twice gives the same answer and
 * clearing the flag is left to the caller; and it holds at the corners of the level as well as in
 * the middle.
 *
 * <p>The level is not square and the marked grids are off the diagonal and are not each other's
 * transpose, so a chunk that indexed {@code squares[y][x]} would fail here rather than quietly
 * agree with itself.
 *
 * <p>The last test records a deliberate divergence rather than a bug. C asserts the grid is in
 * bounds and would abort; the port answers false out of bounds, in line with the other square
 * predicates on {@link Chunk}. The test states the port's behaviour, and it is the test to change
 * if that guard ever becomes an exception.
 *
 * <p>Class ChunkSquareIsFeelTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkSquareIsFeelTest {

    /**
     * The chunk's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 9;

    /**
     * The chunk's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 6;

    /**
     * A grid the tests mark as a feeling trigger. Off the diagonal, and inside the level.
     */
    private static final Loc TRIGGER = Loc.row(1).col(7);

    /**
     * A grid the tests leave alone. Off the diagonal, and the transpose of no grid used here.
     */
    private static final Loc PLAIN = Loc.row(4).col(2);

    /**
     * The lowest in-bounds grid of the level.
     */
    private static final Loc FIRST = Loc.row(0).col(0);

    /**
     * The highest in-bounds grid of the level.
     */
    private static final Loc LAST = Loc.row(HEIGHT - 1).col(WIDTH - 1);

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * A small level, every grid starting with empty info flags.
     */
    @BeforeEach
    void newLevel() {
        Player player = new Player();
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
    }

    /**
     * Asks the method under test about a grid. Private, and reached by reflection rather than
     * through {@code updateOne}, which would also count and clear the flag.
     *
     * @param grid the grid to ask about
     * @return the method's answer
     */
    private boolean squareIsFeel(Loc grid) {
        try {
            Method method = Chunk.class.getDeclaredMethod("squareIsFeel", Loc.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(level, grid);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("squareIsFeel threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("squareIsFeel is no longer reachable", e);
        }
    }

    /**
     * Marks a grid of the level under test as a feeling trigger.
     *
     * @param grid the grid to mark
     */
    private void markFeel(Loc grid) {
        level.getSquare(grid).sqInfoOn(SquareEnum.SQUARE_FEEL);
    }

    /**
     * A grid carrying {@code SQUARE_FEEL} is a trigger square.
     */
    @Test
    @DisplayName("a marked grid is a feeling square")
    void markedGridIsFeel() {
        markFeel(TRIGGER);

        assertTrue(squareIsFeel(TRIGGER));
    }

    /**
     * A grid without the flag is not. This is the state of every grid of a fresh chunk, so it is
     * also the answer before level generation has scattered any markers.
     */
    @Test
    @DisplayName("an unmarked grid is not a feeling square")
    void unmarkedGridIsNotFeel() {
        assertFalse(squareIsFeel(PLAIN));
    }

    /**
     * Only the grid asked about counts. A trigger elsewhere on the level must not answer for a
     * plain grid, which is what would happen if the method read the chunk rather than the square.
     */
    @Test
    @DisplayName("a trigger elsewhere does not answer for this grid")
    void triggerElsewhereIsIgnored() {
        markFeel(TRIGGER);

        assertFalse(squareIsFeel(PLAIN));
    }

    /**
     * The right flag is read. Every other info flag the square can carry is set on a grid that has
     * no {@code SQUARE_FEEL}, so a method reading the wrong bit of {@code info} fails here.
     */
    @Test
    @DisplayName("only SQUARE_FEEL is read")
    void otherFlagsDoNotTrigger() {
        for (SquareEnum flag : SquareEnum.values()) {
            if (flag != SquareEnum.SQUARE_FEEL) {
                level.getSquare(PLAIN).sqInfoOn(flag);
            }
        }

        assertFalse(squareIsFeel(PLAIN));
    }

    /**
     * Setting a neighbouring flag on a trigger grid does not lose the answer either — the flags
     * live side by side in the same set.
     */
    @Test
    @DisplayName("SQUARE_FEEL survives its neighbours")
    void feelSurvivesOtherFlags() {
        markFeel(TRIGGER);
        level.getSquare(TRIGGER).sqInfoOn(SquareEnum.SQUARE_SEEN);
        level.getSquare(TRIGGER).sqInfoOn(SquareEnum.SQUARE_GLOW);

        assertTrue(squareIsFeel(TRIGGER));
    }

    /**
     * The read has no side effects. C's {@code update_one} clears the flag itself after counting,
     * which only works because the test that precedes it leaves the flag alone.
     */
    @Test
    @DisplayName("asking does not clear the flag")
    void readingDoesNotClearTheFlag() {
        markFeel(TRIGGER);

        assertTrue(squareIsFeel(TRIGGER));
        assertTrue(squareIsFeel(TRIGGER));
        assertTrue(level.getSquare(TRIGGER).isFeel());
    }

    /**
     * The corners of the level are in bounds, so both answer from their own flag. C's assert
     * passes for these grids, and the port's guard must not turn them away.
     */
    @Test
    @DisplayName("the corner grids are in bounds")
    void cornerGridsAreInBounds() {
        markFeel(FIRST);
        markFeel(LAST);

        assertTrue(squareIsFeel(FIRST));
        assertTrue(squareIsFeel(LAST));
    }

    /**
     * Grids one step outside each edge of the level answer false. C asserts here instead; see the
     * class comment.
     */
    @Test
    @DisplayName("grids outside the level are not feeling squares")
    void outOfBoundsGridsAreNotFeel() {
        assertFalse(squareIsFeel(Loc.row(0).col(-1)));
        assertFalse(squareIsFeel(Loc.row(-1).col(0)));
        assertFalse(squareIsFeel(Loc.row(0).col(WIDTH)));
        assertFalse(squareIsFeel(Loc.row(HEIGHT).col(0)));
    }
}
