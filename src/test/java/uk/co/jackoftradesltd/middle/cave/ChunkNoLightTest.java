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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.noLight}, the port of C's {@code no_light} ({@code cave-view.c}).
 *
 * <p>The C is one line — {@code return (!square_isseen(cave, p->grid))} — so the tests are about
 * what that line means rather than about branches it does not have. Three things are worth
 * pinning down. The answer comes from {@code SQUARE_SEEN} at the player's <em>own</em> grid, not
 * from any light the player carries and not from a neighbouring grid; it inverts, so a seen grid
 * must answer false; and it must follow the player, so moving between a seen grid and an unseen
 * one flips the answer with nothing else changed.
 *
 * <p>The seen grid and the dark grid are both off the diagonal and are not each other's transpose,
 * so a chunk indexing {@code squares[x][y]} would fail here rather than agree with itself. The
 * chunk is not square for the same reason.
 *
 * <p>The last test records a deliberate divergence rather than a bug. C asserts the grid is in
 * bounds and would abort; {@link Chunk#squareIsSeen(Loc)} answers false out of bounds, so the port
 * reports an out-of-bounds player as being in the dark. The test states the port's behaviour, and
 * it is the test to change if that guard ever becomes an exception.
 *
 * <p>Class ChunkNoLightTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkNoLightTest {

    /**
     * The chunk's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 9;

    /**
     * The chunk's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 6;

    /**
     * A grid the tests mark as seen. Off the diagonal, and inside the level.
     */
    private static final Loc SEEN = Loc.row(1).col(7);

    /**
     * A grid the tests leave unseen. Off the diagonal, and the transpose of no grid used here.
     */
    private static final Loc DARK = Loc.row(4).col(2);

    /**
     * A grid outside the level, for the bounds test.
     */
    private static final Loc OUTSIDE = Loc.row(-1).col(-1);

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player whose grid is asked about.
     */
    private Player player;

    /**
     * A small level with a player attached, every grid starting with empty info flags.
     */
    @BeforeEach
    void newLevel() {
        player = new Player();
        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
    }

    /**
     * Moves the player to a grid. {@code Player} has no setter for its grid — placing a character
     * is the dungeon's job, and that is not ported — so the field is set directly.
     *
     * @param grid the grid to stand the player on
     */
    private void placePlayer(Loc grid) {
        try {
            Field field = Player.class.getDeclaredField("grid");
            field.setAccessible(true);
            field.set(player, grid);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Player.grid is no longer reachable", e);
        }
    }

    /**
     * Marks a grid of the level under test as seen.
     *
     * @param grid the grid to mark
     */
    private void markSeen(Loc grid) {
        level.getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
    }

    /**
     * A player standing on a seen grid is not in the dark, whatever light they carry.
     */
    @Test
    @DisplayName("a seen grid is not dark")
    void seenGridIsNotDark() {
        markSeen(SEEN);
        placePlayer(SEEN);

        assertFalse(level.noLight(player));
    }

    /**
     * A player standing on an unseen grid is in the dark. This is the default state of every grid
     * of a fresh chunk, so it is also the answer before any view calculation has run.
     */
    @Test
    @DisplayName("an unseen grid is dark")
    void unseenGridIsDark() {
        placePlayer(DARK);

        assertTrue(level.noLight(player));
    }

    /**
     * Only the player's own grid counts. A seen grid elsewhere on the level does not light the
     * player up, which is what would happen if the method looked at the chunk rather than the grid.
     */
    @Test
    @DisplayName("a seen grid elsewhere does not help")
    void seenGridElsewhereIsIgnored() {
        markSeen(SEEN);
        placePlayer(DARK);

        assertTrue(level.noLight(player));
    }

    /**
     * The answer follows the player. Both grids are set up once and only the player moves, so a
     * method that cached its answer, or read a fixed grid, would fail one half of this.
     */
    @Test
    @DisplayName("the answer tracks the player's grid")
    void answerFollowsThePlayer() {
        markSeen(SEEN);

        placePlayer(SEEN);
        assertFalse(level.noLight(player));

        placePlayer(DARK);
        assertTrue(level.noLight(player));
    }

    /**
     * A player outside the level counts as being in the dark. C asserts here instead; see the class
     * comment.
     */
    @Test
    @DisplayName("a grid outside the level is dark")
    void outOfBoundsGridIsDark() {
        placePlayer(OUTSIDE);

        assertTrue(level.noLight(player));
    }
}
