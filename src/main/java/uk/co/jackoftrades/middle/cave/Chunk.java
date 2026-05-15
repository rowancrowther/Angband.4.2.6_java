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
import uk.co.jackoftrades.middle.enums.TrapEnum;

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
     * Test to see if the grid location is fully inside the bounds of this chunk
     *
     * @param grid the Loc to test
     * @return true if grid is wholly in the bounds of this chunk
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean inBoundsFully(@NotNull Loc grid) {
        return grid.getX() > 0 && grid.getX() < width - 1
                && grid.getY() > 0 && grid.getY() < height - 1;
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
        return inBounds(grid) && getSquare(grid).isLit();
    }

    /**
     * Tests the current illumination status of the square
     *
     * @param grid the Loc of the square
     * @return true if the square is currently lit
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsCurrentlyLit(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isLit();
    }

    /**
     * Checks to see if a square is damaging to its inhabitants - currently only lava
     *
     * @param grid the Loc of the square
     * @return true if the square damages its occupants
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDamaging(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isDamaging();
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
     * Checks to see if a specific square allows monster flow information
     *
     * @param grid the Loc of the square
     * @return true if the square does NOT allow monster flow information
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsNoFlow(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsNoFlow();
    }

    /**
     * Checks to see if a specific square carries the player scent
     *
     * @param grid the Loc of the square
     * @return true if the square does NOT carry player scent
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsNoScent(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsNoScent();
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
     * Tests to see if there is a visible trap on a square
     *
     * @param grid the Loc of the square
     * @return true if the square has a visible trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsVisibleTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isVisibleTrap();
    }

    /**
     * Tests for an unknown player trap
     *
     * @param grid the Loc of the square
     * @return true if the square at grid contains an unknown player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsSecretTrap(@NotNull Loc grid) {
        return !squareIsVisibleTrap(grid) && squareIsPlayerTrap(grid);
    }

    /**
     * Checks for the location of a known disabled player trap
     *
     * @param grid the Loc of the square
     * @return true if the square contains a visible disabled player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDisabledTrap(@NotNull Loc grid) {
        return inBounds(grid) && squareIsVisibleTrap(grid) && getSquare(grid).trapTimeout(-1) > 0;
    }

    /**
     * Check if the square contains a trap that can be disarmed
     *
     * @param grid the Loc of the square to check
     * @return true if the square contains a known, disarmable player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDisarmableTrap(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (squareIsDisabledTrap(grid)) return false;

        return squareIsVisibleTrap(grid) && squareIsPlayerTrap(grid);
    }

    private boolean squareDTrapEdge(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);
        if (!square.isDTrap()) return false;

        return false;
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
     * Tests to see if a square is open, a floor square not occupied by a monster (or the player)
     *
     * @param grid the Loc of the sqyare
     * @return true if the square is a floor unoccupied by a monster or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsOpen(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isOpen();
    }

    /**
     * Tests for a warded trap on a given square
     *
     * @param grid the Loc of the square
     * @return whether there is a glyph of warding on the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsWarded(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWarded();
    }

    /**
     * Checks for a decoy trap on a given square
     *
     * @param grid the Loc of the square
     * @return true if a trap exists on the given square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDecoyed(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isDecoyed();
    }

    /**
     * Checks for a web trap on a given square
     *
     * @param grid the Loc of the square
     * @return true if a web trap exists on the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsWebbed(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isWebbed();
    }

    /**
     * Tests whether a specific square seems to be a wall
     *
     * @param grid the Loc of the square
     * @return true if the square seems to be a wall
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareSeemsLikeWall(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featSeemsLikeWall();
    }

    /**
     * Tests for whether a square has an interesting feature or not
     *
     * @param grid the Loc of the square
     * @return true if the square has an interesting feature
     */
    private boolean squareIsInteresting(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsIntersting();
    }

    /**
     * Tests for a trap of a certain type in a square at location grid. The square already tests for the location of
     * a trap at all, so we leave that ti the square
     *
     * @param grid     the Loc of the square
     * @param trapFlag the trap type
     * @return true if the square at location grid contains a trap with a particular flag
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareTrapFlag(@NotNull Loc grid, @NotNull TrapEnum trapFlag) {
        return inBounds(grid) && getSquare(grid).trapFlag(trapFlag);
    }

    /**
     * Tests the existence of a locked door
     *
     * @param grid the Loc of the square to test
     * @return true if the square is a locked door
     */
    private boolean squareIsLockedDoor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isLockedDoor();
    }

    /**
     * Tests the existence of an unlocked door
     *
     * @param grid the Loc of the square to test
     * @return true if the square is an unlocked door
     */
    private boolean squareIsUnlockedDoor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isUnlockedDoor();
    }

    /**
     * Tests for the existence of a player trap
     *
     * @param grid the Loc to test
     * @return true if there is a player trap on the square at grid
     */
    private boolean squareIsPlayerTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isPlayerTrap();
    }

    /**
     * Checks to see if a square at a given Loc is empty - open without any items
     *
     * @param grid the Loc of the square
     * @return true if the square at grid is empty
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsEmpty(Loc grid) {
        if (!inBounds(grid))
            return false;

        return getSquare(grid).isEmpty();
    }

    /**
     * Checks to see if the square at location grid can be run through
     *
     * @param grid the Loc of the square
     * @return whether the square can be run through
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsArrivable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isArrivable();
    }

    /**
     * Check whether a specific square is untrapped without items
     *
     * @param grid the Loc of the square
     * @return true if the square is untrapped without items
     */
    private boolean squareCanPutItem(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).canPutItem();
    }

    /**
     * Checks to see if this square can be dug. This includes rubble and non-permanent walls
     *
     * @param grid the location of this square
     * @return true if the player can dig this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsDiggable(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);
        return square.isMineral() || square.isSecretDoor() || square.isRubble();
    }

    /**
     * Checks to see if the square at location grid is a floor square
     *
     * @param grid the Loc of this square
     * @return true if this square is floor
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsFloor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isFloor();
    }

    /**
     * Checks to see if a square is a floor without any traps
     *
     * @param grid the Loc of the square
     * @return true if the square is trap free floor
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsWebbable(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;
        if (getSquare(grid).getTraps().isEmpty()) return false;
        return squareIsFloor(grid);
    }

    /**
     * Checks to see if a monster can walk through a particular square
     *
     * @param grid the Loc of the square
     * @return true if a monster can walk through this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsMonsterWalkable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsMonsterWalkable();
    }

    /**
     * Check to see if the player can walk through a particular square
     *
     * @param grid the Loc of the square
     * @return true if the player can pass through the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsPassable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsPassable();
    }

    /**
     * Checks if a given square can have a projectile go through it
     *
     * @param grid the Loc of the square
     * @return true if the square is projectile passable
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsProjectable(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsProjectable();
    }

    /**
     * Checks to see if a square can be used as a feeling square
     *
     * @param grid the Loc of the square
     * @return whether the square can be used as a feeling square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareAllowsFeel(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).allowsFeel();
    }

    /**
     * Checks whether line of sight can pass through this square
     *
     * @param grid the Loc of the square
     * @return true if line of sight passes through this square
     */
    private boolean squareAllowsLOS(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featAllowsLOS();
    }

    /**
     * Checks to see if the square is a stronger or permanent wall, such as granite, magma and quartz.
     * This excludes secret doors and rubble
     *
     * @param grid the Loc of the square
     * @return true if the square is a strong wall
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsStrongWall(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);
        return square.isMineral() || square.isPerm();
    }

    /**
     * Checks to see whether a square is internally lit
     *
     * @param grid the Loc of the square
     * @return true if the square is internally lit
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsBright(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsBright();
    }

    /**
     * Checks whether a square is fire-based
     *
     * @param grid the Loc of the square
     * @return true if the square is lava
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsFiery(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).featIsFiery();
    }

    /**
     * Returns the square at a given grid location, or null if the location is out of bounds
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
