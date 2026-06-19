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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.monsters.MonsterGroup;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.Pile;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

public class Chunk {
    private static final Logger logger = LogManager.getLogger();

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
    private HashMap<TerrainFeatureFlags, Integer> featCount;

    private Square[][] squares;
    private Heatmap noise;
    private Heatmap scent;
    private Loc decoy;

    private Pile objectPile; // Should this be ItemObject[][] objects?
    private int objMax;

    private Monster[] monsters;
    private int monMax;
    private int monCnt;
    private int monCurrent;
    private int numRepro;

    private ArrayList<MonsterGroup> monsterGroups;

    private ArrayList<Connector> join;

    public Chunk(String name, int turn, int depth, int feeling, int objectRating, int monsterRating,
                 boolean goodItem, int height, int width, int feelingSquares, int objMax, int monMax,
                 int monCnt, int monCurrent, int numRepro) {
        this.name = name;
        this.turn = turn;
        this.depth = depth;
        this.feeling = feeling;
        this.objectRating = objectRating;
        this.monsterRating = monsterRating;
        this.goodItem = goodItem;
        this.height = height;
        this.width = width;
        this.feelingSquares = feelingSquares;
        this.featCount = new HashMap<>();

        this.squares = new Square[this.width][this.height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                squares[x][y] = new Square(null, 1, 1);
            }
        }

        this.noise = new Heatmap();
        this.scent = new Heatmap();
        this.decoy = Loc.zero;
        this.objectPile = new Pile();
        this.objMax = objMax;
        this.monsters = new Monster[monMax];
        this.monMax = monMax;
        this.monCnt = monCnt;
        this.monCurrent = monCurrent;
        this.numRepro = numRepro;
        this.monsterGroups = new ArrayList<>();
        this.join = new ArrayList<>();
    }

    /**
     * Test to see whether a grid location is in the bounds for this chunk
     *
     * @param grid The Loc of this square
     * @return true if this square is in the bounds of this chunk
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean inBounds(@NotNull Loc grid) {
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
    boolean squareIsGlow(@NotNull Loc grid) {
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
    public boolean squareIsSeen(@NotNull Loc grid) {
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
    boolean squareIsTrap(@NotNull Loc grid) {
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
     * Gets an iterator to iterate through the objects on a particular square
     *
     * @param grid the Loc of the square
     * @return an Iterator<ItemObject> for the objects on square located at grid
     */
    @CheckReturnValue
    @Contract(pure = true)
    Iterator<ItemObject> getPileIterator(@NotNull Loc grid) {
        return getSquare(grid).getSquarePileIterator();
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

    /**
     * Check to see if a given square can be destroyed. Used by destruction spells, and for placing stairs, etc.
     *
     * @param grid the Loc of the square we are examining
     * @return true if it can be destroyed
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareChangeable(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        Square square = getSquare(grid);

        if (square.isPerm() || square.isShop() || square.isStairs()) return false;

        return !square.hasObjectArtifact();
    }

    /**
     * Check to see if a square is at the (inner) edge of a trap detection area
     *
     * @param grid the Loc of the square
     * @return true if the square is on the edge of a trap detection area
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareDTrapEdge(@NotNull Loc grid) {
        if (!inBounds(grid) || getSquare(grid).isDTrap()) return false;

        return Stream.of(DirectionEnum.DIR_N, DirectionEnum.DIR_S, DirectionEnum.DIR_E, DirectionEnum.DIR_W)
                .map(grid::nextGrid)
                .anyMatch(neighbour -> inBoundsFully(neighbour) && !squareIsDTrap(neighbour));
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
    @CheckReturnValue
    @Contract(pure = true)
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
    @CheckReturnValue
    @Contract(pure = true)
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
    @CheckReturnValue
    @Contract(pure = true)
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
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsLockedDoor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isLockedDoor();
    }

    /**
     * Tests the existence of an unlocked door
     *
     * @param grid the Loc of the square to test
     * @return true if the square is an unlocked door
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsUnlockedDoor(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isUnlockedDoor();
    }

    /**
     * Tests for the existence of a player trap
     *
     * @param grid the Loc to test
     * @return true if there is a player trap on the square at grid
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsPlayerTrap(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isPlayerTrap();
    }

    /**
     * Tests to see if the player is on this square
     *
     * @param grid the Loc of this square
     * @return true if the player is on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsPlayer(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isPlayer();
    }

    /**
     * Checks to see if a square at a given Loc is empty - open without any items
     *
     * @param grid the Loc of the square
     * @return true if the square at grid is empty
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareIsEmpty(@NotNull Loc grid) {
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
    @CheckReturnValue
    @Contract(pure = true)
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
    @CheckReturnValue
    @Contract(pure = true)
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
     * Check to see if the player thinks a square will block projections
     *
     * @param grid the Loc of the square
     * @return true if the player believes the square will block projections/is a wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareIsBelievedWall(@NotNull Loc grid) {
        if (!inBoundsFully(grid)) return true;

        if (!this.isKnown(grid)) return false;

        return !GameConstants.mainPlayer.getCave().getSquare(grid).featIsProjectable();
    }

    /**
     * Check to see if a square is known by the player to be passible
     *
     * @param grid the Loc of the square
     * @return true if the player knows this square is passable
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean isKnownPassible(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (!isKnown(grid)) return false;

        return GameConstants.mainPlayer.getCave().squareIsPassable(grid);
    }

    /**
     * Tests if a square is in a cul-de-sac
     *
     * @param grid the Loc of the square we are testing
     * @return true if the square has exactly 3 horizontal/vertical neighbouring walls and 4 diagonal neighbouring walls
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareSuitsStairsWell(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (squareIsVault(grid) || squareIsNoStairs(grid)) return false;

        return squareNumWallsAdjacent(grid) == 3 && squareNumWallsDiagonal(grid) == 4 && squareIsEmpty(grid);
    }

    /**
     * Checks whether a square is in a corridor
     *
     * @param grid the Loc of this square
     * @return true if the square has exactly 4 diagonal neighbouring walls and 2 adjacent neighbouring walls
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareSuitsStairsOK(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        if (squareIsVault(grid) || squareIsNoStairs(grid)) return false;

        return squareNumWallsDiagonal(grid) == 4 && squareNumWallsAdjacent(grid) == 2 && squareIsEmpty(grid);
    }

    /**
     * Check to see if a square is suitable for placing a summoned monster
     *
     * @param grid the Loc of this square
     * @return true if the square is appropriate for summoning a monster onto
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean squareAllowsSummoning(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;

        return squareIsEmpty(grid) && !squareIsWarded(grid) && !squareIsDecoyed(grid);
    }

    /**
     * Counts the number of adjacent squares vertically or horizontally which are walls
     *
     * @param grid the Loc of the grid we are examining
     * @return the number of adjacent walls
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int squareNumWallsAdjacent(@NotNull Loc grid) {
        if (!inBounds(grid)) return 0;

        return (int) Stream.of(
                        DirectionEnum.DIR_S, DirectionEnum.DIR_N, DirectionEnum.DIR_E, DirectionEnum.DIR_W
                )
                .filter(dir -> getSquare(grid.nextGrid(dir)).featIsWall())
                .count();
    }

    /**
     * Counts the number of adjacent walls diagonally
     *
     * @param grid the Loc we are looking at
     * @return the number of diagonal neighbouring walls
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int squareNumWallsDiagonal(@NotNull Loc grid) {
        if (!inBounds(grid)) return 0;

        return (int) Stream.of(
                        DirectionEnum.DIR_SE, DirectionEnum.DIR_NW, DirectionEnum.DIR_NE, DirectionEnum.DIR_SW
                )
                .filter(dir -> getSquare(grid.nextGrid(dir)).featIsWall())
                .count();
    }

    /**
     * Returns the square at a given grid location, or null if the location is out of bounds
     * @param grid A grid Loc
     * @return the square at the location grid, or null if the location is out of bounds
     */
    @Contract(pure = true)
    @CheckReturnValue
    Square getSquare(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return squares[grid.getX()][grid.getY()];
    }

    /**
     * Gets the feature of a given square
     *
     * @param grid the Loc of the square
     * @return the feature of the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable Feature squareFeature(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return getSquare(grid).getFeature();
    }

    /**
     * Gets the light value for this square
     *
     * @param grid the Loc of this square
     * @return the light value for this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private int squareLight(@NotNull Loc grid) {
        if (!inBounds(grid)) return 0;
        return getSquare(grid).getLight();
    }

    /**
     * Get a monster in this chunk based on its location
     *
     * @param grid the Loc of the square to check for a monster
     * @return the monster on this square, or null if no monster is on the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable Monster squareMonster(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;

        Square square = getSquare(grid);
        int monsterIndex = square.getMonsterIndex();

        if (monsterIndex > 0) {
            Monster mon = caveMonster(monsterIndex);
            return mon != null && mon.getMonsterRace() != null ? mon : null;
        }

        return null;
    }

    /**
     * Get a monster in this chunk by its index
     *
     * @param index the index of the monster
     * @return the monster on this level with the given index
     */
    @CheckReturnValue
    @Contract(pure = true)
    private Monster caveMonster(int index) {
        return monsters[index];
    }

    /**
     * Gets the monster on a given grid
     *
     * @param grid the loc of the monster
     * @return the monster on this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    Monster getMonster(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        int mIndex = getSquare(grid).getMonsterIndex();
        if (mIndex > 0) return monsters[mIndex];
        else return null;
    }

    /**
     * Checks for a particular info flag on a square at a given grid location
     *
     * @param grid     the Loc of the square
     * @param infoFlag the info flag we are checking for
     * @return true if the square at Loc grid has infoFlag set
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareHasInfoFlag(@NotNull Loc grid, @NotNull SquareEnum infoFlag) {
        return (inBounds(grid) && getSquare(grid).hasInfoFlag(infoFlag));
    }

    /**
     * Gets whether a square is lit or not
     *
     * @param grid the Loc of the square
     * @return true if the square is lit
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean squareIsLit(@NotNull Loc grid) {
        return inBounds(grid) && getSquare(grid).isLit();
    }

    /**
     * Gets the top object of a pile on the current level by its position
     *
     * @param grid the Loc of the square we are examining
     * @return The topmost object on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable ItemObject squareObject(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return getSquare(grid).getTopObject();
    }

    /**
     * Get the top most trap from a square
     *
     * @param grid the Loc of the square
     * @return the top most/only trap on the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private @Nullable Trap squareTrap(@NotNull Loc grid) {
        if (!inBounds(grid)) return null;
        return getSquare(grid).getTrap();
    }

    /**
     * Tests if a given object is on a specific square
     *
     * @param grid   the Loc of the square
     * @param object the object we are checking for
     * @return true if the object is in the square
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean squareHoldsObject(@NotNull Loc grid, @NotNull ItemObject object) {
        if (!inBounds(grid)) return false;
        return getSquare(grid).holdsObject(object);
    }

    /**
     * Getter
     * @return the width of this chunk
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int getWidth() {
        return width;
    }

    /**
     * Getter
     * @return the height of this chunk
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int getHeight() {
        return height;
    }

    /**
     * Excise an object from a floor pile leaving it orphaned
     *
     * @param grid the location of the object
     * @param item the object to excise
     * @throws IndexOutOfBoundsException if the grid is outside the chunk's boundaries
     */
    @Contract(mutates = "this")
    public void squareExciseObject(@NotNull Loc grid, @NotNull ItemObject item) throws IndexOutOfBoundsException {
        if (!inBounds(grid)) {
            String message = "Location out of bounds, being thrown as a fatal error after logging";
            IndexOutOfBoundsException ex = new IndexOutOfBoundsException(message);
            logger.fatal(message, ex);
            throw ex;
        }

        getSquare(grid).pileExcise(item);
    }

    /**
     * Delete an object from the cave, and release it for the garbage collector to remove
     *
     * @param item The object we wish to delete
     */
    @Contract(mutates = "this")
    public void objectDelete(@Nullable Chunk playerCave, @NotNull ItemObject item) {
        objectPile.excise(item);

        Player player = GameConstants.mainPlayer;

        // Remove the object from those tracked by the player upkeep
        if (player.getPlayerUpkeep() != null) player.getPlayerUpkeep().getPile().excise(item);

        Chunk cave = GameConstants.cave;
        if (playerCave != null
                && cave.objectPile.contains(item)
                && playerCave.objectPile.contains(item)) {
            item.setGrid(Loc.zero);
            item.setHeldMIndex(0);
            item.setMimickingMIndex(0);

            item.orNotice(ObjectNotice.OBJ_NOTICE_IMAGINED);
            return;
        }

        if (playerCave != null && playerCave.objectPile.contains(item))
            playerCave.objectPile.excise(item);

        if (cave.objectPile.contains(item))
            cave.objectPile.excise(item);
    }

    /**
     * Remove an object from the object pile in this chunk
     *
     * @param item the object to remove
     */
    @Contract(mutates = "this")
    public void delistObject(ItemObject item) {
        if (!objectPile.contains(item)) return;

        if (this.equals(GameConstants.cave) && GameConstants.mainPlayer.getCave().objectPile.contains(item))
            return;

        objectPile.excise(item);
    }

    /**
     * Memorize the feature on this square by setting the feature on the same square in the player cave to it
     *
     * @param grid the Loc of the square we are memorizing
     */
    void squareMemorize(@NotNull Loc grid) {
        if (this != GameConstants.cave) return;
        squareSetKnownFeat(grid, getSquare(grid).getFeature());
    }

    /**
     * Set the feature that is on the main cave to that on the player cave, so they 'know' it
     *
     * @param grid    the location of the grid we are setting the feature
     * @param feature the feature to set
     */
    void squareSetKnownFeat(@NotNull Loc grid, Feature feature) {
        if (!inBounds(grid)) return;
        if (this != GameConstants.cave) return;

        GameConstants.mainPlayer.getCave().getSquare(grid).setFeature(feature);
    }

    /**
     * Checks whether the square at a particular location is known
     *
     * @param grid the Loc of the square
     * @return true if the location is known
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean isKnown(@NotNull Loc grid) {
        if (!inBounds(grid)) return false;
        Player player = GameConstants.mainPlayer;

        if (this != GameConstants.cave && this != player.getCave()) return false;

        if (player.getCave() == null) return false;

        return !player.getCave().getSquare(grid).getFeature().isNoFeat();
    }

    /**
     * Get the maximum number of monsters on this level
     *
     * @return the maximum number of monsters on this level
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getMonMax() {
        return monMax;
    }
}