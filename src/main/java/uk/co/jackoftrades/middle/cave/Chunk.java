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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;

import java.util.HashMap;

public class Chunk {
    private String name;
    private int turn;
    private int depth;

    private int feeling;
    private int objectRating;
    private int monsterRating;
    private boolean goodItem;

    private int height;
    private int width;

    /* How many feeling squares the player has visited */
    private int feelingSquares;
    private HashMap<TerrainFlags, Integer> featCount;

    private Square[][] squares;

    /**
     * Test to see whether a grid location is in the bounds for this chunk
     *
     * @param grid The Loc of this square
     * @return true if this square is in the bounds of this chunk
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean inBounds(@NotNull Loc grid) {
        return grid.getX() >= 0 && grid.getX() <= width
                && grid.getY() >= 0 && grid.getY() <= height;
    }

    /**
     * Tests to see if the square is marked
     *
     * @param grid The Loc of the square
     * @return true if the square is marked
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsMarked(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isMark();
    }

    /**
     * Tests to see if the square is lit
     *
     * @param grid the Loc of the square
     * @return true if the square is lit
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsGlow(@NotNull Loc grid) {
        return inBounds(grid) & getSquare(grid).isLit();
    }

    /**
     * Tests to see if the square is part of a vault
     *
     * @param grid the Loc of this grid
     * @return true if the square is part of a vault
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsVault(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isVault();
    }

    /**
     * Tests to see if the square at location grid has been seen by the player
     *
     * @param grid The Loc of the square
     * @return true if the square has been seen by the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsSeen(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isSeen();
    }

    /**
     * Tests to see if the square at Location grid is in view of the player
     *
     * @param grid the Loc of the square
     * @return true if the square is in view of the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsView(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isView();
    }

    /**
     * Tests if a square was seen before the current update
     *
     * @param grid the Loc of this square
     * @return true if this square was seen before the current update
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareWasSeen(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isSeen();
    }

    /**
     * Tests to see if the square at Loc grid has a known trap
     *
     * @param grid the Loc grid of the square
     * @return true if the grid has a known trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isTrap();
    }

    /**
     * Tests to see if the square at grid has an unknown trap
     *
     * @param grid the Loc of the square
     * @return true if the square has an unknown trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsInvis(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isInvis();
    }

    /**
     * Tests to see if the square at Loc grid is an inner wall
     *
     * @param grid th Loc of the square
     * @return true if the square at grid is an inner wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsWallInner(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWallInner();
    }

    /**
     * Tests to see if the square at Loc grid is an outer wall
     *
     * @param grid the Loc of the square
     * @return true if the square at grid is an outer wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsWallOuter(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWallOuter();
    }

    /**
     * Tests to see if the square at Loc grid is a solid wall
     *
     * @param grid the Loc of the square
     * @return true if the square is a solid wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsWallSolid(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWallSolid();
    }

    /**
     * Tests to see if a square has monster restrictions (generation)
     *
     * @param grid the Loc of the square
     * @return true if the square has monster restrictions
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsMonRestrict(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isMonRestrict();
    }

    /**
     * Tests to see if the player can teleport FROM the square
     *
     * @param grid the Loc of the square
     * @return true if the player can teleport from the square at Loc grid
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsNoTeleport(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoTeleport();
    }

    /**
     * Tests to see if the square can be magically mapped by the player
     *
     * @param grid the Loc of the square
     * @return true if the square CANNOT be magically mapped by the player
     */
    private boolean squareIsNoMap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoMap();
    }

    /**
     * Tests to see if the player can see the square by ESP
     *
     * @param grid the Loc of the square
     * @return true if the sqaure CANNOT be detected by ESP
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsNoESP(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoEsp();
    }

    /**
     * Tests to see if the square is marked for projection passing
     *
     * @param grid the Loc of the square
     * @return true if the square is marked for projection passing
     */
    private boolean squareIsProject(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isProject();
    }

    /**
     * Tests to see if a square has been detected for traps
     *
     * @param grid the Loc of the square
     * @return true if the square has been detected for traps
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsDTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isDTrap();
    }

    /**
     * Tests to see if stairs can be placed on a square
     *
     * @param grid the Loc of the square
     * @return true if the square is NOT appropriate to place squares
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsNoStairs(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isNoStairs();
    }

    /**
     * Returns the square at a given grid location, or null if the location is out of bounds
     *
     * @param grid A grid Loc
     * @return the square at the location grid, or null if the location is out of bounds
     */
    @Contract(pure = true)
    @CheckReturnValue
    public Square getSquare(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return squares[grid.getX()][grid.getY()];
    }

    @Contract(pure = true)
    @CheckReturnValue
    public int getWidth() {
        return width;
    }

    @Contract(pure = true)
    @CheckReturnValue
    public int getHeight() {
        return height;
    }
}
