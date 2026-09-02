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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterGroup;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.*;
import java.util.stream.Stream;

import static uk.co.jackoftradesltd.middle.cave.ChunkUtils.los;

/**
 * A whole level (or self-contained piece of one): the 2D grid of {@link Square}s
 * plus everything that lives on it — monsters, objects, noise/scent flow maps,
 * generation metadata and level feeling. The large family of {@code squareXxx()}
 * methods are bounds-checked convenience accessors that delegate to the
 * {@link Square} at a {@link Loc}. This is the Java port of the C original's
 * {@code struct chunk} ({@code src/cave.h}), which represents both the live cave
 * and the player's remembered copy of it.
 *
 * @author Rowan Crowther
 */
public class Chunk {
    /**
     * Logger used to report out-of-bounds access and similar errors.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The chunk's name (e.g. the level/vault it represents).
     */
    private String name;
    /**
     * The game turn this chunk was generated/last updated.
     */
    private int turn;
    /**
     * The dungeon depth (level) of this chunk.
     */
    private int depth;

    /**
     * The level feeling value (how dangerous/rewarding the level feels).
     */
    private int feeling;
    /**
     * Accumulated rating of the objects on this level.
     */
    private int objectRating;
    /**
     * Accumulated rating of the monsters on this level.
     */
    private int monsterRating;
    /**
     * Whether the level contains a notably good item.
     */
    private boolean goodItem;

    /**
     * Level height in rows.
     */
    private int height;
    /**
     * Level width in columns.
     */
    private int width;

    /* How many feeling squares the player has visited */
    /**
     * How many feeling squares the player has visited so far.
     */
    private int feelingSquares;
    /**
     * Count of grids carrying each terrain-feature flag (used for level feeling).
     */
    private HashMap<TerrainFeatureFlags, Integer> featCount;

    /**
     * The grid of squares, indexed {@code [y][x]}.
     */
    private Square[][] squares;
    /**
     * Noise flow map used for monster pathfinding toward sound.
     */
    private Heatmap noise;
    /**
     * Scent flow map used for monsters that track by smell.
     */
    private Heatmap scent;
    /**
     * Location of the player's decoy, if one is placed.
     */
    private Loc decoy;

    /**
     * Master list of all objects in this chunk.
     */
    private List<ItemObject> objects; // Should this be ItemObject[][] objects?
    /**
     * Highest object index in use.
     */
    private int objMax;

    /**
     * The monsters present in this chunk, indexed by monster index.
     */
    private Monster[] monsters;
    /**
     * Capacity of the {@link #monsters} array (maximum monster index).
     */
    private int monMax;
    /**
     * Current count of live monsters.
     */
    private int monCnt;
    /**
     * Index of the monster currently being processed.
     */
    private int monCurrent;
    /**
     * Number of breeding monsters currently on the level.
     */
    private int numRepro;

    /**
     * The monster groups (packs) on this level.
     */
    private ArrayList<MonsterGroup> monsterGroups;

    /**
     * Connection points used when stitching this chunk into a larger level.
     */
    private ArrayList<Connector> join;

    /**
     * The player associated with this chunk, used by the knowledge accessors that compare this chunk
     * against the player's remembered cave (see {@link #isKnown} and {@link #squareSetKnownFeat}).
     */
    private Player player;
    /**
     * The live current level — {@link GameState#getCave()} at construction. Several accessors compare
     * {@code this} against it to tell whether this chunk is the real cave or the player's remembered
     * copy, since the two share the same {@code Chunk} type.
     */
    private Chunk currentLevel;

    /**
     * Build a chunk of the given dimensions and metadata, allocating a fresh
     * grid of blank {@link Square}s, empty flow maps, an empty object pile and a
     * monster array sized to {@code monMax}.
     *
     * @param name           chunk name
     * @param turn           generation turn
     * @param depth          dungeon depth
     * @param feeling        level feeling value
     * @param objectRating   object rating
     * @param monsterRating  monster rating
     * @param goodItem       whether a notably good item is present
     * @param height         level height in rows
     * @param width          level width in columns
     * @param feelingSquares number of feeling squares visited
     * @param objMax         highest object index
     * @param monMax         monster array capacity
     * @param monCnt         live monster count
     * @param monCurrent     index of the monster being processed
     * @param numRepro       number of breeding monsters
     * @param player         the player associated with this chunk
     */
    public Chunk(String name, int turn, int depth, int feeling, int objectRating, int monsterRating,
                 boolean goodItem, int height, int width, int feelingSquares, int objMax, int monMax,
                 int monCnt, int monCurrent, int numRepro, Player player) {
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

        this.noise = new Heatmap(width, height);
        this.scent = new Heatmap(width, height);
        this.decoy = Loc.zero;
        this.objects = new ArrayList<>();
        this.objMax = objMax;
        this.monsters = new Monster[monMax];
        this.monMax = monMax;
        this.monCnt = monCnt;
        this.monCurrent = monCurrent;
        this.numRepro = numRepro;
        this.monsterGroups = new ArrayList<>();
        this.join = new ArrayList<>();
        this.player = player;
    }

    /**
     * @return the chunk that is the live current level - C's global {@code cave}. A chunk compares
     * itself against this to tell whether it is the real level or the player's remembered
     * copy, since the two share this class
     */
    public Chunk getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Records which chunk is the live current level.
     *
     * <p>Must be called on every chunk once the real level exists - the real level points at itself,
     * and the player's remembered copy points at the real one. Several accessors here return early
     * or answer {@code false} while it is unset, so a chunk that never receives it is quietly inert
     * rather than obviously broken.
     *
     * <p>Exists because the answer cannot be taken at construction: the real level is built before
     * it has been installed as the current one, so a chunk reading the game state in its constructor
     * would capture whatever came before it.
     *
     * <p>Function setCurrentLevel commented in full on 260827.
     *
     * @param currentLevel the chunk that is the live current level
     */
    public void setCurrentLevel(Chunk currentLevel) {
        this.currentLevel = currentLevel;
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
        return grid.getX() >= 0 && grid.getX() < width
                && grid.getY() >= 0 && grid.getY() < height;
    }

    /**
     * Test to see if the grid location is fully inside the bounds of this chunk
     *
     * @param grid the Loc to test
     * @return true if grid is wholly in the bounds of this chunk
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean inBoundsFully(@NotNull Loc grid) {
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
        return inBounds(grid) && getSquare(grid).isGlow();
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
    public boolean squareIsNoFlow(@NotNull Loc grid) {
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
    public boolean squareIsNoScent(@NotNull Loc grid) {
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
        return inBounds(grid) && getSquare(grid).wasSeen();
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
    boolean squareIsProjectable(@NotNull Loc grid) {
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

        return !player.getCave().getSquare(grid).featIsProjectable();
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

        return player.getCave().squareIsPassable(grid);
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
    public Square getSquare(@NotNull Loc grid) {
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
    public Monster caveMonster(int index) {
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
    public int getWidth() {
        return width;
    }

    /**
     * Getter
     * @return the height of this chunk
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getHeight() {
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
        Chunk cave = this;

        // Remove the object from those tracked by the player upkeep
        if (player.getPlayerUpkeep() != null
                && item == player.getPlayerUpkeep().getObject())
            player.getPlayerUpkeep().setObject(null);

        if (playerCave != null
                && cave.objects.contains(item)
                && playerCave.objects.contains(item)) {
            item.setGrid(Loc.zero);
            item.setHeldMIndex(0);
            item.setMimickingMIndex(0);

            if (item.getKnown() != null) item.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_IMAGINED);
            return;
        }

        if (playerCave != null && playerCave.objects.contains(item))
            playerCave.objects.remove(item);

        if (cave.objects.contains(item))
            cave.objects.remove(item);
    }

    /**
     * Remove an object from the object pile in this chunk
     *
     * @param item the object to remove
     */
    @Contract(mutates = "this")
    public void delistObject(ItemObject item) {
        if (!objects.contains(item)) return;

        if (this.equals(currentLevel) && player.getCave() != null && player.getCave().objects.contains(item))
            return;

        objects.remove(item);
    }

    /**
     * Memorize the feature on this square by setting the feature on the same square in the player cave to it
     *
     * @param grid the Loc of the square we are memorizing
     */
    void squareMemorize(@NotNull Loc grid) {
        if (this != currentLevel) return;
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
        if (this != currentLevel) return;

        player.getCave().getSquare(grid).setFeature(feature);
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

        if (this != currentLevel && this != player.getCave()) return false;

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

    /**
     * @return this level's name, or {@code null} if unnamed - the port of C's {@code c->name}; used
     * for example to recognise the "arena" level
     */
    public String getName() {
        return name;
    }

    /**
     * @return the raw monster array, indexed by monster index — the port of reading C's
     * {@code cave->monsters}. Index 0 is a reserved dummy and unused slots are {@code null} (C's
     * empty, {@code race == NULL} slots), so callers iterating this must skip {@code null} entries.
     */
    public Monster[] getMonsters() {
        return monsters;
    }

    /**
     * Flag a single grid as needing to be relit and redrawn on the map — the port of C's
     * {@code square_light_spot}. Sets the item-list redraw flag and signals the map-update event for
     * this grid, so the display recomputes the square (for example to reshow a monster or a light
     * change) on the next refresh; out-of-bounds grids are ignored.
     *
     * @param grid the grid whose display needs refreshing
     */
    public void squareLightSpot(@NotNull Loc grid) {
        if (!inBounds(grid)) return;

        player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_ITEMLIST);
        // The data is passed x/y and not y/x on this line as that is what the C does.
        GameEngine.getEventsBusHandler().eventSignalPoint(GameEventType.EVENT_MAP, grid.getX(), grid.getY());
    }

    /**
     * @return the number of monster slots in this chunk's monster array
     */
    public int monsterCount() {
        return Arrays.stream(monsters).toList().size();
    }

    /**
     * Culls monsters from the level to free up space, the port of C's {@code compact_monsters}
     * ({@code mon-make.c}). Passes over the monster list with escalating aggression each iteration —
     * raising the level cap and shrinking the distance threshold — deleting eligible monsters that
     * fail a saving throw, with quest monsters and uniques given progressively better odds of
     * surviving. Finally excises the dead entries and shrinks the array's high-water mark.
     *
     * @param numToCompact the minimum number of monsters to remove; {@code 0} simply excises the
     *                     already-dead entries without a "Compacting monsters..." message
     */
    public void compactMonsters(int numToCompact) {
        int monIndex;
        int numCompacted;
        int iteration;

        int maxLevel;
        int minDistance;
        int chance;

        if (numToCompact != 0)
            Message.message("Compacting monsters...");

        // Compact at least numToCompact monsters
        for (numCompacted = 0, iteration = 1;
             numCompacted < numToCompact;
             iteration++) {
            // Get more vicious each iteration
            maxLevel = 5 * iteration;

            // Get closer each iteration
            minDistance = 5 * (20 - iteration);

            // Check all the monsters
            for (Monster monster : monsters) {
                if (monster == null) continue;

                // skip dead monsters
                if (monster.getMonsterRace() == null) continue;

                // High level monsters start out immune
                if (monster.getMonsterRace().getLevel() > maxLevel) continue;

                // Ignore nearby monsters
                if ((minDistance > 0) && (monster.getcDistance() < minDistance)) continue;

                // Base saving throw
                chance = 90;

                // Only compact quest monsters in an emergency
                if (monster.getMonsterRace().hasMonsterRaceFlag(MonsterRaceFlag.RF_QUESTOR) && (iteration < 1000))
                    chance = 100;

                // Try to save unique monsters
                if (monster.isUnique()) chance = 99;

                if (RandomValueUtils.randInt0(100) < chance)
                    continue;

                deleteMonster(monster.getGrid());

                numCompacted++;
            }
        }

        // Excise dead monsters (backwards)
        for (monIndex = monMax - 1; monIndex >= 1; monIndex--) {
            Monster monster = monsters[monIndex];

            if (monster != null) continue;

            monsterIndexMove(monMax - 1, monIndex);

            monMax--;
        }
    }

    /**
     * Relocates a monster from one index to another in the monster array, updating all references,
     * the port of C's {@code monster_index_move} ({@code mon-make.c}). Used when compacting the array.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param fromIndex the monster's current index
     * @param toIndex   the index to move it to
     */
    public void monsterIndexMove(int fromIndex, int toIndex) {
        // Stub function : TODO: implement this
    }

    /**
     * Deletes the monster occupying the given grid, if any — the port of C's {@code delete_monster}
     * ({@code mon-make.c}). Resolves the grid to its square and delegates to
     * {@link #deleteMonsterIndex(int)}; out-of-bounds grids are ignored.
     *
     * @param grid the map location to clear of its monster
     */
    private void deleteMonster(@NotNull Loc grid) {
        if (!inBounds(grid)) return;

        Square square = getSquare(grid);
        if (square.getMonsterIndex() > 0)
            deleteMonsterIndex(square.getMonsterIndex());
    }

    /**
     * Deletes the monster at the given array index, freeing its slot and clearing its square — the
     * port of C's {@code delete_monster_idx} ({@code mon-make.c}).
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param monsterIndex the index of the monster to delete
     */
    private void deleteMonsterIndex(int monsterIndex) {
        // Stub function : TODO: implement this
    }

    /**
     * Lights or darkens the whole level as appropriate — the port of C's {@code cave_illuminate}
     * ({@code cave.c}). In town this reflects day/night; in the dungeon it handles lit rooms.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param daytime {@code true} if it is daytime (relevant in the town)
     */
    public void illuminate(boolean daytime) {
        // Stub function : TODO: implement this
        // TODO: When implementing this call GameWorld.isDaytime as opposed to taking in a boolean
    }

    /**
     * Places a new monster on the level at least a given distance from a grid — the port of C's
     * {@code pick_and_place_distant_monster} ({@code mon-make.c}), used to spawn wandering monsters
     * away from the player.
     *
     * <p><b>Stub:</b> not yet implemented; always reports failure.
     *
     * @param toAvoid  the grid to keep the new monster away from
     * @param distance the minimum distance from {@code toAvoid}
     * @param sleep    whether the placed monster starts asleep
     * @param depth    the depth to generate the monster at
     * @return {@code true} if a monster was placed
     */
    public boolean pickAndPlaceDistantMonster(Loc toAvoid, int distance, boolean sleep, int depth) {
        // Stub function : TODO: implement this
        return false;
    }

    /**
     * Marks the traps on a square as remembered by the player so they stay drawn — the port of C's
     * {@code square_memorize_traps} ({@code cave-square.c}).
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param grid the grid whose traps to memorize
     */
    public void squareMemorizeTraps(Loc grid) {
        // Stub function : TODO: implement this
    }

    /**
     * Ticks every trap on the level down by one turn, re-memorising and re-lighting any square whose
     * trap just became active again (timeout reaching zero) while it is in view. Mirrors the trap
     * half of C's per-turn trap ageing.
     */
    public void decreaseTrapTimeout() {
        for (int y = 0; y < squares.length; y++) {
            for (int x = 0; x < squares[y].length; x++) {
                Square square = squares[y][x];
                boolean changed = false;
                for (Trap trap : square.getTraps()) {
                    if (trap.getTimeout() > 0) {
                        trap.decrementTimeout();
                        if (trap.getTimeout() == 0) changed = true;
                    }
                }
                if (changed && square.isSeen()) {
                    squareMemorizeTraps(Loc.row(y).col(x));
                    squareLightSpot(Loc.row(y).col(x));

                }
            }
        }
    }

    /**
     * @return an unmodifiable view of the objects lying on this chunk's floor
     */
    public List<ItemObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Clears this chunk's sound map back to silence by replacing it with a fresh {@link
     * Heatmap} (every grid {@code 0}). Ports the "set all the grids to silence" loop that opens
     * C's {@code make_noise} ({@code src/game-world.c}); a new zeroed map is equivalent to
     * zeroing the interior in place, since only interior grids are ever read.
     */
    public void resetNoise() {
        noise = new Heatmap(width, height);
    }

    /**
     * Ages every scent trail on the level by one turn, making all existing scent one step staler.
     * Ports the "update scent for all grids" loop that opens C's {@code update_scent} ({@code
     * src/game-world.c}): only grids that already carry scent ({@code > 0}) are incremented, so
     * never-visited grids stay at the {@code 0} baseline, and only the interior is scanned (the
     * outermost ring is skipped, matching the {@code 1 .. dimension - 2} bounds in C).
     */
    public void updateScent() {
        // ignore outside boundary of cave
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (scent.getValue(y, x) > 0) {
                    scent.setValue(y, x, scent.getValue(y, x) + 1);
                }
            }
        }
    }

    /**
     * Returns this chunk's sound map — the per-grid noise distances from the player used by
     * monster hearing to home in along passable terrain. Ports access to C's {@code
     * cave->noise} ({@code src/cave.h}).
     *
     * @return the noise {@link Heatmap} for this chunk
     */
    public Heatmap getNoise() {
        return noise;
    }

    /**
     * Returns this chunk's scent map — the per-grid age of the player's scent trail, read by
     * monster smell to track the player along open floor. Ports access to C's {@code cave->scent}
     * ({@code src/cave.h}).
     *
     * @return the scent {@link Heatmap} for this chunk
     */
    public Heatmap getScent() {
        return scent;
    }

    /**
     * Tells the player how this level feels, the port of C's {@code display_feeling}
     * ({@code cave.c}).
     *
     * <p>Level feeling is Angband's way of hinting at what is on a level before the player has
     * walked it: one reading for danger and one for the quality of the loot. The two are learned at
     * different rates — the danger half is available on arrival, while the object half only firms up
     * once enough of the level has been explored — which is what the flag selects between.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the message and level-feeling subsystems; takes
     * no action, so the player currently arrives on a level without being told anything about it.
     *
     * <p>Function displayFeeling coded before 260817, commented in full on 260817.
     *
     * @param objectOnly {@code true} to report only the object half of the feeling, as C does when
     *                   the threshold for knowing it has just been crossed
     */
    public void displayFeeling(boolean objectOnly) {
        // Stub class TODO: Implement
    }

    /**
     * Recalculates everything the player can currently see, the port of C's {@code update_view}
     * ({@code cave-view.c}). This is the method that answers "what is visible from where the player
     * is standing now", and it is called whenever something could have changed that answer — a step
     * taken, a light lit or spent, terrain altered, blindness coming or going.
     *
     * <p>The visibility flags are rebuilt from scratch rather than edited. {@link #markWasSeen()}
     * first copies the current answer into {@code SQUARE_WASSEEN} and wipes {@code SQUARE_VIEW},
     * {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} across the level, so the sweeps below
     * start from an empty board while the previous answer survives for comparison.
     * {@link #calcLighting(Player)} then recomputes every grid's light level, because what can be
     * seen depends on what is lit.
     *
     * <p>The player's own grid is handled before the sweeps and by hand. {@code SQUARE_VIEW} goes on
     * unconditionally — there is always a line of sight to where you are standing — but
     * {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} only follow if there is something to see
     * by: a light being carried, terrain that is lit anyway, or the {@code PF_UNLIGHT} personality
     * that sees in the dark. A player in a dark corridor with a spent lantern is therefore in view
     * of their own grid without seeing it.
     *
     * <p>The blind clause that follows asks its two questions of two different chunks, exactly as C
     * does. Whether the grid is known is asked of this, the live level, while whether it is passable
     * is asked of {@link Player#getCave()}, the player's remembered copy — the terrain the player
     * believes is there. A blind player standing on a grid they remember as impassable is holding a
     * memory that reality has just disproved, since they are standing on it, so
     * {@link #squareForget(Loc)} drops the remembered terrain rather than leaving a wall drawn
     * underneath them. C notes that a variant with a timed effect allowing movement through
     * impassable terrain would have to revisit this, as the contradiction would no longer be one.
     *
     * <p>Two full-level sweeps then run in row-major order, and they are two rather than one.
     * {@link #updateViewOne(Loc, Player)} decides for each grid whether line of sight reaches it and
     * marks it viewed and perhaps seen; only once that has settled for every grid does
     * {@link #updateOne(Loc, Player)} sweep again to compare each grid against
     * {@code SQUARE_WASSEEN}, act on the grids whose visibility changed, and clear the snapshot flag
     * behind it. Keeping the passes separate means no grid is ever judged against a view that is
     * still half recalculated.
     *
     * <p>Function updateView coded before 260828, commented in full on 260828.
     *
     * @param player the player whose view is being recalculated; supplies the grid the view is
     *               centred on, the light carried, the blindness timer and the personality flags
     */
    public void updateView(Player player) {
        // Record the current view
        markWasSeen();

        // Calculate light levels
        calcLighting(player);

        // Assume we can view the player grid
        getSquare(player.getGrid()).sqInfoOn(SquareEnum.SQUARE_VIEW);

        if (player.getStateLight() > 0 || squareIsLit(player.getGrid())
                || player.hasPlayerFlag(PlayerFlag.PF_UNLIGHT)) {
            getSquare(player.getGrid()).sqInfoOn(SquareEnum.SQUARE_SEEN);
            getSquare(player.getGrid()).sqInfoOn(SquareEnum.SQUARE_CLOSE_PLAYER);
        }

        /*
         * If the player is blind and in terrain that was remembered to be
         * impassable, forget the remembered terrain.  This will have to be
         * modified in variants that have timed effects which allow a player
         * to move through impassable terrain.
         */
        if (player.getTimedEffect(TimedEffect.TMD_BLIND) != 0 && isKnown(player.getGrid())
                && !player.getCave().squareIsPassable(player.getGrid())) {
            squareForget(player.getGrid());
        }

        // Squares we have LoS to get marked as in the view, and perhaps seen
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                updateViewOne(Loc.row(y).col(x), player);
            }
        }

        // Update each grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                updateOne(Loc.row(y).col(x), player);
            }
        }
    }

    /**
     * Settles what one grid's visibility means now that the view has been recalculated, the port of
     * C's {@code update_one} ({@code cave-view.c}). {@link #updateView(Player)} sweeps every grid of
     * the level twice: the first sweep decides which grids line of sight reaches and marks them
     * seen, and this method is the second sweep, which compares that answer against the one recorded
     * before the recalculation and acts on the difference.
     *
     * <p>Blindness is applied first and overrides everything the light and sight calculation
     * decided: a blind player sees nothing, so both {@code SQUARE_SEEN} and
     * {@code SQUARE_CLOSE_PLAYER} come off here rather than being withheld earlier. A sighted player
     * instead gets each currently seen grid checked for a trap to reveal, which is how walking into
     * view of a trap is what discovers it.
     *
     * <p>The two comparisons that follow are deliberately not an if/else. Because the blind branch
     * above can clear {@code SQUARE_SEEN} between them, the seen and unseen tests are asked
     * independently of one another, and a grid can satisfy neither. A grid crossing from unseen to
     * seen is noted and redrawn; a grid crossing the other way is only redrawn, since there is
     * nothing new to learn about a grid that has just gone out of sight.
     *
     * <p>The level feeling is collected on the unseen-to-seen crossing. A grid carrying
     * {@code SQUARE_FEEL} counts once towards {@code feelingSquares} and then has the flag cleared,
     * so the same grid cannot be counted again on a later pass; the test, the count and the clear in
     * that order are what make {@link #squareIsFeel(Loc)} a pure read. The feeling is announced on
     * the exact pass that takes the count to {@code feelingNeed} — C tests equality, not
     * {@code >=}, so a count that somehow overshot would never announce — and is suppressed while
     * {@code onlyPartial} is set, which is the flag the interface raises when it is rebuilding a
     * character's state rather than playing a turn, so that the arrival on a new level makes the
     * announcement instead.
     *
     * <p>{@code SQUARE_WASSEEN} comes off unconditionally at the foot, on every grid and whichever
     * branches ran. That is what leaves the level clean for the next recalculation, which begins by
     * writing the record afresh in {@link #markWasSeen()}.
     *
     * <p><b>Outstanding:</b> {@link #squareRevealTrap(Loc, boolean, boolean)} and
     * {@link #squareNoteSpot(Loc)} are stubs awaiting chapter 4, and {@link #displayFeeling(boolean)}
     * awaits the message subsystem, so trap discovery, remembering the contents of a newly seen grid
     * and the feeling message itself take no effect yet. The counting, the flag work and the redraws
     * around them are complete.
     *
     * <p>Function updateOne coded on 260828, commented in full on 260828.
     *
     * @param grid   the grid to settle
     * @param player the player whose view has just been recalculated
     */
    private void updateOne(Loc grid, Player player) {
        // remove view if player is blind
        if (player.getTimedEffect(TimedEffect.TMD_BLIND) != 0) {
            getSquare(grid).sqInfoOff(SquareEnum.SQUARE_SEEN);
            getSquare(grid).sqInfoOff(SquareEnum.SQUARE_CLOSE_PLAYER);
        } else if (squareIsSeen(grid)) {
            squareRevealTrap(grid, false, true);
        }

        // square went from unseen -> seen
        if (squareIsSeen(grid) && !squareWasSeen(grid)) {
            if (squareIsFeel(grid)) {
                feelingSquares++;
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_FEEL);
                // Don't disaply feeling if it will display for the new level  
                if (feelingSquares == GameConstants.getWorldFeelingNeed()
                        && !player.getPlayerUpkeep().isOnlyPartial()) {
                    displayFeeling(true);
                    player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_FEELING);
                }
            }

            squareNoteSpot(grid);
            squareLightSpot(grid);
        }

        // Square went from seen -> unseen
        if (!squareIsSeen(grid) && squareWasSeen(grid))
            squareLightSpot(grid);

        getSquare(grid).sqInfoOff(SquareEnum.SQUARE_WASSEEN);
    }

    /**
     * Memorises whatever is interesting in a grid the player can now see, the port of C's
     * {@code square_note_spot} ({@code cave-map.c}). Seeing a grid and remembering it are two
     * different things: the view calculation decides what is currently visible, and this is the
     * method that writes what was visible into the player's own memory of the level, so that the
     * map still shows the staircase or the pile of loot after the player has walked away.
     *
     * <p>C guards on two conditions before doing anything. The chunk must be the level the player
     * is actually on, since memorising into the player's map from a chunk they are not standing in
     * would record a level they have never visited; and the grid must be seen, or else be the
     * player's own grid, which is what lets a blind player still know the square under their feet.
     * What follows is three separate acts of memory: the pile of objects is learned exactly, a
     * secret trap on the grid is revealed and then the traps are memorised, and finally the terrain
     * itself is memorised — but only if what is currently remembered about it is wrong, which is
     * the {@code square_ismemorybad} test. The object memory and the terrain memory are kept
     * deliberately apart so that picking a detected object off a dark floor does not memorise the
     * floor, and dropping an object into a remembered but unseen grid does not memorise the object.
     *
     * <p>The one caller here is {@link #updateOne(Loc, Player)}, on the pass where a grid crosses
     * from unseen to seen, which is C's primary call site too. C calls it from several others —
     * when an object is created or dropped, when terrain changes from floor to non-floor, and when
     * a trap is set — the general rule being that it is called whenever what the player ought to
     * remember about a grid has been called into question.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the object and trap subsystems in chapter 4; it
     * does nothing for every input. Until it is filled in, a newly seen grid is redrawn by
     * {@link #squareLightSpot(Loc)} but nothing about it is committed to the player's memory of the
     * level.
     *
     * <p>Function squareNoteSpot stubbed on 260828, commented in full on 260828.
     *
     * @param grid the grid whose contents are to be memorised
     */
    private void squareNoteSpot(Loc grid) {
        // STUB function to be implemented in chapter 4 
        // 
        // TODO: Implement in chapter 4
    }

    /**
     * Tests whether a grid is one of the level's feeling trigger squares, the port of C's
     * {@code square_isfeel} ({@code cave-square.c}). Level generation scatters a fixed number of
     * these markers across the interesting parts of a new level, and the player earns the level
     * feeling by walking far enough to see enough of them. This method only reads the marker; the
     * counting and the clearing of the flag belong to the caller.
     *
     * <p>The one caller is {@link #updateOne(Loc, Player)}, which asks the question exactly when a
     * grid crosses from unseen to seen, then clears {@code SQUARE_FEEL} so the same grid cannot be
     * counted twice. That ordering — test, count, clear — matches C's {@code update_one}
     * ({@code cave-view.c}), and it is why this method is a pure read with no side effects of its
     * own.
     *
     * <p>C asserts that the grid is in bounds, which would halt the game on a bad grid. Following
     * the boundary convention of the other square predicates here, an out-of-bounds grid answers
     * false instead, so a stray grid simply triggers no feeling.
     *
     * <p>Function squareIsFeel coded on 260828, commented in full on 260828.
     *
     * @param grid the Loc of the square to test
     * @return true if the square is a feeling trigger square
     */
    private boolean squareIsFeel(Loc grid) {
        if (!inBounds(grid)) return false;
        Square square = getSquare(grid);
        return square.isFeel();
    }

    /**
     * Reveals the player traps hidden in a grid, the port of C's {@code square_reveal_trap}
     * ({@code trap.c}). A trap set against the player starts out invisible, and it stays that way
     * until the player is good enough to spot it: the trap carries a power, the player carries a
     * searching skill, and the trap becomes visible the moment the skill reaches the power. There
     * is no searching command in 4.2 — the check is made for free, on every grid the player can
     * see, every time the view is recalculated.
     *
     * <p>C walks the grid's whole trap list rather than stopping at the first hit, skipping the
     * entries that are not player traps and, unless {@code always} is set, the ones whose power
     * outruns the player's searching skill. Each surviving invisible trap is turned visible, and
     * the grid's traps are then memorised into the player's own map. The count of newly revealed
     * traps is what drives the tail: if it is non-zero the grid is memorised and redrawn, and if
     * {@code domsg} is set the player is told, with the message choosing singular or plural on that
     * same count. C returns whether anything was found, which lets a caller such as the magic
     * mapping effect report that its detection actually turned something up.
     *
     * <p>The parameters carry C's {@code always} and {@code domsg} in that order. {@code always}
     * bypasses the skill test, so a grid can be stripped of its secrets outright — the trap
     * detection effect passes it true, while the view calculation passes false and lets the player's
     * skill decide. {@code domsg} governs only whether the discovery is announced, which is why the
     * view calculation asks for the message but the terrain projection code does not: a trap
     * revealed by a passing spell should not interrupt with a line about the player having found it.
     *
     * <p>The one caller here is {@link #updateOne(Loc, Player)}, which asks it of every grid the
     * player can currently see and is not blind for, passing {@code (false, true)} exactly as C's
     * {@code update_one} ({@code cave-view.c}) does. C has three further call sites — the same
     * {@code (false, true)} from {@code square_note_spot} ({@code cave-map.c}), {@code (false,
     * false)} from the terrain projection in {@code project-feat.c}, and {@code (true, false)} from
     * the detection effect in {@code effect-handler-general.c} — which will arrive with the
     * subsystems that own them.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the trap subsystem in chapter 4; it does nothing
     * for every input, so a trap the player is standing in front of is never revealed and no trap is
     * ever committed to their map. Two things are to be settled when it is filled in: the parameters
     * want C's names, and C's {@code bool} return is dropped here, which the detection effect will
     * need back when it arrives.
     *
     * <p>Function squareRevealTrap stubbed on 260828, commented in full on 260828.
     *
     * @param grid the grid whose player traps are to be revealed
     * @param b    C's {@code always}: true to reveal regardless of the player's searching skill
     * @param b1   C's {@code domsg}: true to announce the discovery to the player
     */
    private void squareRevealTrap(Loc grid, boolean b, boolean b1) {
        // STUB function to be implemented in chapter 4 
        // 
        // TODO: Implement in chapter 4
    }

    /**
     * Reports whether the player is standing in the dark, the port of C's {@code no_light}
     * ({@code cave-view.c}). The question is not how much light the player carries but whether
     * their own grid has ended up marked as seen by the most recent view calculation, so a player
     * with no light of their own standing in a lit room is not in the dark, and a player carrying a
     * torch is never in the dark. Callers pair it with blindness — C's several call sites all read
     * {@code p->timed[TMD_BLIND] || no_light(p)} — to decide whether a task that needs light can be
     * attempted at all.
     *
     * <p>C reads the global {@code cave} rather than a chunk handed to it, so the answer there is
     * always about the level the player is actually on; here the question is asked of whichever
     * chunk the method is called on, and it is the caller's business to ask the current level.
     *
     * <p>C asserts that the grid is in bounds. {@link #squareIsSeen(Loc)} answers false for an
     * out-of-bounds grid instead of failing, which makes an out-of-bounds player count as being in
     * the dark rather than halting the game.
     *
     * <p>Function noLight coded on 260828, commented in full on 260828.
     *
     * @param player the player whose grid is tested
     * @return true if the player's grid is not currently seen
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean noLight(Player player) {
        return (!squareIsSeen(player.getGrid()));
    }

    /**
     * Decides whether a single grid belongs in the player's current view, the port of C's
     * {@code update_view_one} ({@code cave-view.c}). The caller sweeps every grid of the chunk and
     * hands each one to this method; a grid that passes ends up in {@link #becomeViewable(Loc,
     * Player, boolean)}, and one that does not is simply left alone.
     *
     * <p>Two independent questions are answered here. The first is whether the grid is close enough
     * to be lit by the player themselves: {@code close} is the approximate distance measured against
     * the player's current light radius, and it is passed down rather than acted on here. The second
     * is whether line of sight reaches the grid at all, which is the {@link ChunkUtils#los(Chunk,
     * Loc, Loc)} call at the foot of the method. Distance is computed before the sight-range test so
     * that {@code close} exists whichever way the method exits, but a grid beyond
     * {@code maxSight} returns without ever consulting line of sight.
     *
     * <p>Players with {@link PlayerFlag#PF_UNLIGHT} carrying no real light of their own — a current
     * radius of one or zero — replace the ordinary radius test with a level-scaled one, so that a
     * character who sees in the dark gains reach as they gain levels, and loses it again as soon as
     * they pick up a light source. The division by six is integer division on a non-negative level,
     * so the radius grows one grid every six levels.
     *
     * <p>The bulk of the method is the wall-lighting special case. Line of sight is traced to the
     * grid's own centre, and for a wall that line has to pass through the wall itself, so the naive
     * test fails for the very grids the player is looking straight at:
     *
     * <pre>
     * #1#############
     * #............@#
     * ###############
     * </pre>
     *
     * <p>The wall marked {@code 1} is plainly visible, but the line to it runs into the adjacent
     * wall cell first. So a wall borrows the line of sight of the grid one step toward the player,
     * {@code checkX}/{@code checkY} stepping each coordinate independently and leaving a coordinate
     * alone when it already matches the player's, which keeps the borrowed grid adjacent and inside
     * the level. Two conditions cancel the loan. If the grid being borrowed from is itself a wall
     * the loan is refused, since otherwise both faces of a double-thickness wall would light up. And
     * if the grid was reached by a knight's move — offsets of two and one in either order — the loan
     * is refused when the wall is being approached around a corner, which is the pair of
     * {@code squareAllowsLOS} tests on the intervening grids. In both cases the check grid falls
     * back to the grid itself and the ordinary line-of-sight test decides.
     *
     * <p>Note that the borrowed line of sight decides only whether {@code becomeViewable} is called;
     * the grid handed to it is always the original one, never the check grid.
     *
     * <p>Function updateViewOne coded before 260828, commented in full on 260828.
     *
     * @param grid   the location being considered for the view
     * @param player the player whose view is being built, supplying the grid distances are measured
     *               from, the light radius, and the level used by the unlight radius
     */
    private void updateViewOne(Loc grid, Player player) {
        int x = grid.getX();
        int y = grid.getY();
        int checkX = x;
        int checkY = y;
        int distance = grid.distance(player.getGrid());
        boolean close = distance < player.getStateLight();

        // Too far away
        if (distance > GameConstants.getPlayerMaxSight()) return;

        // UNLIGHT players have a special radius of view
        if (player.hasPlayerFlag(PlayerFlag.PF_UNLIGHT) && player.getPlayerState().getCurLight() <= 1) {
            close = distance < (2 + player.getLevel() / 6 - player.getPlayerState().getCurLight());
        }

        /* Special case for wall lighting. If we are a wall and the square in
         * the direction of the player is in LOS, we are in LOS. This avoids
         * situations like:
         * #1#############
         * #............@#
         * ###############
         * where the wall cell marked '1' would not be lit because the LOS
         * algorithm runs into the adjacent wall cell.
         */
        if (!squareAllowsLOS(grid)) {
            int deltaX = x - player.getGrid().getX();
            int deltaY = y - player.getGrid().getY();
            int absX = Math.abs(deltaX);
            int absY = Math.abs(deltaY);
            int signX = deltaX > 0 ? 1 : -1;
            int signY = deltaY > 0 ? 1 : -1;
            int playerX = player.getGrid().getX();
            int playerY = player.getGrid().getY();

            checkX = (x < playerX) ? (x + 1) : (x > playerX) ? (x - 1) : x;
            checkY = (y < playerY) ? (y + 1) : (y > playerY) ? (y - 1) : y;

            // Check that the cell we're trying to steal LoS from isn't a
            // wall. If we don't do this, double-thickness walls will have
            // both sides visible.
            if (!squareAllowsLOS(Loc.row(checkY).col(checkX))) {
                checkX = x;
                checkY = y;
            }

            // Check if we got here via a 'knight's move', and if so
            // don't steal LoS
            if (absX == 2 && absY == 1) {
                if (squareAllowsLOS(Loc.row(y).col(x - signX))
                        && !squareAllowsLOS(Loc.row(y - signY).col(x - signX))) {
                    checkX = x;
                    checkY = y;
                }
            } else if (absX == 1 && absY == 2) {
                if (squareAllowsLOS(Loc.row(y - signY).col(x))
                        && !squareAllowsLOS(Loc.row(y - signY).col(x - signX))) {
                    checkX = x;
                    checkY = y;
                }
            }
        }

        if (los(this, player.getGrid(), Loc.row(checkY).col(checkX))) {
            becomeViewable(grid, player, close);
        }
    }

    /**
     * Adds a grid to the player's current view, the port of C's {@code become_viewable}
     * ({@code cave-view.c}).
     *
     * <p>Being in view and being seen are two different things, and this method is where they part
     * company. {@code SQUARE_VIEW} says only that line of sight reaches the grid; it is set for
     * every grid the caller has established a line to. {@code SQUARE_SEEN} — the flag that actually
     * decides whether the grid is drawn as its true terrain rather than from memory — needs light as
     * well, and is set by either of two independent routes: the grid is close enough to fall inside
     * the player's own light radius, which is what the {@code close} argument carries down from
     * {@link #updateViewOne(Loc, Player)}; or the grid is lit by anything at all, which is
     * {@link #squareIsLit(Loc)} testing the light level accumulated by {@code calcLighting} rather
     * than the {@code SQUARE_GLOW} flag. {@code SQUARE_CLOSE_PLAYER} rides along with the first
     * route only.
     *
     * <p>The early return on a grid already in view is what keeps this idempotent: the visibility
     * sweep can reach the same grid by more than one path, and without the guard a grid could pick
     * up {@code SQUARE_SEEN} on a later visit that the first visit had deliberately withheld.
     *
     * <p>Walls take the longer path through the lit branch. A wall is opaque, so light never reaches
     * the face the player is looking at from the wall's own grid — what matters is whether the grid
     * one step back toward the player is lit, since that is the light falling on the visible face.
     * The two nested conditionals step {@code checkX} and {@code checkY} one square toward the
     * player independently, leaving a coordinate alone when it already matches the player's, which
     * is why the check grid is always adjacent and can never leave the level. A lit wall whose
     * approach is dark stays unseen.
     *
     * <p>Function becomeViewable coded before 260828, commented in full on 260828.
     *
     * @param grid   the location being brought into view
     * @param player the player whose view is being built, supplying the grid the light is measured
     *               from
     * @param close  true if the grid lies within the player's light radius, in which case it is
     *               seen regardless of the level's own lighting
     */
    private void becomeViewable(Loc grid, Player player, boolean close) {
        int x = grid.getX();
        int y = grid.getY();

        // already visible - just return
        if (squareIsView(grid)) return;

        // Add the grid to the view, make it seen if it's close enough to the player
        getSquare(grid).sqInfoOn(SquareEnum.SQUARE_VIEW);
        if (close) {
            getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
            getSquare(grid).sqInfoOn(SquareEnum.SQUARE_CLOSE_PLAYER);
        }

        // Mark lit grids, and walls near to them, as seen
        if (squareIsLit(grid)) {
            if (!squareAllowsLOS(grid)) {
                // for walls, check for a lit grid closer to the player
                int checkX = (x < player.getGrid().getX() ? x + 1 :
                        (x > player.getGrid().getX() ? x - 1 : x));
                int checkY = (y < player.getGrid().getY() ? y + 1 :
                        (y > player.getGrid().getY() ? y - 1 : y));

                if (squareIsLit(Loc.row(checkY).col(checkX))) {
                    getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
                }
            } else {
                getSquare(grid).sqInfoOn(SquareEnum.SQUARE_SEEN);
            }
        }
    }

    /**
     * Forgets the terrain remembered at a grid, the port of C's {@code square_forget}
     * ({@code cave-square.c}).
     *
     * <p>Forgetting is not a flag being cleared: the player's remembered copy of the level simply
     * has its feature at this grid overwritten with {@code FEAT_NONE}, the "nothing/unknown"
     * terrain, which is what {@link #isKnown(Loc)} tests for. The real level is never touched, so
     * the grid keeps whatever terrain it actually has and only the player's memory of it is lost.
     *
     * <p>The guard is C's {@code if (c != cave) return;} — the operation is only meaningful when
     * invoked on the live level, since it is the live level's chunk that owns the boundary across to
     * the player's remembered copy. Called on the remembered copy itself, or on any other chunk, it
     * does nothing at all. {@link #squareSetKnownFeat(Loc, Feature)} repeats that same test, so the
     * guard here is C's belt and braces rather than the only thing standing between a stale chunk
     * and the player's memory.
     *
     * <p>Function squareForget coded before 260828, commented in full on 260828.
     *
     * @param grid the location whose remembered terrain is to be forgotten
     */
    private void squareForget(Loc grid) {
        if (GameState.getCave() != this)
            return;

        Feature none = TerrainRegistry.lookupFeature(TerrainFlags.FEAT_NONE);
        squareSetKnownFeat(grid, none);
    }

    /**
     * Recomputes the light level of every grid on the level from scratch, the port of C's
     * {@code calc_lighting} ({@code cave-view.c}), which C in turn notes was taken from Sil.
     *
     * <p>Lighting is rebuilt in two stages. The first stage sweeps the whole chunk and assigns each
     * grid a base level from its terrain alone: {@code 1} for a grid marked as permanently glowing
     * that either lets light through or, being a wall, passes
     * {@link #glowCanLightWall(Player, Loc)}, and {@code 0} for everything else. Bright terrain then
     * adds {@code 2} on top of that base — so a glowing bright grid reaches {@code 3}, not
     * {@code 2} — and spills a further {@code 1} into each of the eight neighbours, subject to the
     * same rule that a wall is only brightened when {@link #sourceCanLightWall(Player, Loc, Loc)}
     * says the player is placed to see the face being lit. The neighbour set is C's
     * {@code ddgrid_ddd[0..7]}: the four cardinals and four diagonals, and deliberately not the
     * {@code (0, 0)} centre entry that closes that table, which would otherwise brighten the bright
     * grid a second time.
     *
     * <p>The sweep runs in row-major order and writes the base level with an assignment, so a
     * neighbour spill that lands on a grid the sweep has not reached yet is later overwritten when
     * that grid's own turn comes. This is C's behaviour rather than an oversight in the port, and
     * the ordering is kept so the two produce the same numbers grid for grid.
     *
     * <p>The second stage adds the moving sources on top through {@link #addLight(Player, Loc, int,
     * int)}: first the player's own light, then every monster on the level. A monster is skipped if
     * it is dead, if it is camouflaged and so not showing its light, if its race emits nothing, or
     * if it is far enough away that even its reach cannot come within the player's maximum sight.
     * Note C tests the intensity for zero only after computing the radius from it, and the order is
     * preserved here. Each source is passed {@code radius == |intensity| - 1}, so an unlight source
     * (a negative intensity) darkens grids by the same falloff rule that a lamp brightens them.
     *
     * <p>Finally the player's own grid is compared against the level it held on entry, and the
     * light indicator is flagged for redraw only if it actually changed.
     *
     * <p><em>Function calcLighting coded before 260828, commented in full on 260828.</em>
     *
     * @param player the player the lighting is calculated for; supplies the light radius carried,
     *               the grid it is centred on, and the viewpoint used to decide which wall faces
     *               are worth lighting
     */
    private void calcLighting(Player player) {
        int oldLight = squareLight(player.getGrid());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Loc grid = Loc.row(y).col(x);

                if (squareIsGlow(grid)
                        && (squareAllowsLOS(grid) || glowCanLightWall(player, grid))) {
                    getSquare(grid).setLight(1);
                } else
                    getSquare(grid).setLight(0);

                // Squares with bright terrain have intensity 2
                if (squareIsBright(grid)) {
                    getSquare(grid).setLight(getSquare(grid).getLight() + 2);
                    for (DirectionEnum direction : DirectionEnum.surroundingDirections()) {
                        Loc adjacentGrid = grid.sum(direction.ddgrid());
                        if (!inBounds(adjacentGrid)) continue;

                        /*
                         * Only brighten a wall if the player
                         * is in position to view the face
                         * that's lit up.
                         */
                        if (!squareAllowsLOS(adjacentGrid)
                                && !sourceCanLightWall(player, grid, adjacentGrid)) continue;

                        getSquare(adjacentGrid).setLight(getSquare(adjacentGrid).getLight() + 1);
                    }
                }
            }
        }

        // Light around the player
        int light = player.getStateLight();
        int radius = Math.abs(light) - 1;
        addLight(player, player.getGrid(), radius, light);

        // Scan monster list and add monster light or darkness
        for (Monster mon : getMonsters()) {
            // skip null or dead monsters
            if (mon == null || mon.getMonsterRace() == null) continue;

            if (mon.monsterIsCamouflaged()) continue;

            // Get light info for this monster
            light = mon.getMonsterRace().getLight();
            radius = Math.abs(light) - 1;

            // SKip monster not affecting light
            if (light == 0) continue;

            // Skip if the player can't see it
            if (player.getGrid().distance(mon.getGrid()) - radius > GameConstants.getPlayerMaxSight()) continue;

            addLight(player, mon.getGrid(), radius, light);
        }

        // Update light level indicator
        if (squareLight(player.getGrid()) != oldLight) {
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_LIGHT);
        }
    }

    /**
     * Adds the effect of one light source into the accumulated light levels of the grids around it.
     * The port of C's {@code add_light} ({@code cave-view.c}).
     *
     * <p>Light in Angband is accumulated rather than assigned: {@link #calcLighting(Player)} first
     * lays down a base level from permanently glowing and bright terrain, then each source in turn
     * adds its own contribution on top through this method. Every grid in the square of side
     * {@code 2 * radius + 1} centred on the source is visited, and the ones that survive three
     * filters have their light adjusted.
     *
     * <p>The filters, in C's order, are: the grid must lie inside the level; its
     * {@link Loc#distance(Loc)} from the source — the cheap integer approximation, not the true
     * Euclidean one — must not exceed {@code radius}, which rounds the visited square off to a
     * rough disc; and {@link ChunkUtils#los} must find an unbroken line from the source, so light
     * does not leak through walls. A wall grid itself then has to pass
     * {@link #sourceCanLightWall(Player, Loc, Loc)} as well, because a wall is only worth lighting
     * when the face this source lights is the face the player is looking at.
     *
     * <p>The contribution falls off with distance, and the arithmetic is written so that it does so
     * for darkness as well. A positive {@code inten} contributes {@code inten - dist}, brightest at
     * the source and fading outwards; a negative {@code inten} contributes {@code inten + dist},
     * darkest at the source and weakening outwards. Both reach zero at {@code dist == |inten|}, and
     * since callers pass {@code radius == |inten| - 1} the outermost ring visited still carries a
     * contribution of magnitude one. The value is added to whatever the grid already holds, so it
     * can be negative overall where an unlight source overlaps lit terrain.
     *
     * <p>C notes this is a brute-force approach: it sweeps the whole bounding square rather than
     * propagating outwards from the source and stopping at walls, and the port keeps that
     * behaviour so the resulting light levels match grid for grid.
     *
     * <p>A {@code radius} below zero makes both loops empty and the method a no-op, which is how a
     * player with no light at all is handled.
     *
     * <p><em>Function addLight coded before 260828, commented in full on 260828.</em>
     *
     * @param player     the player the lighting is calculated for; only used to judge which wall
     *                   faces are visible to them
     * @param sourceGrid the grid the light is emitted from
     * @param radius     the reach of the source in grids; grids further than this are untouched
     * @param inten      the intensity at the source, positive for light and negative for unlight
     */
    private void addLight(Player player, Loc sourceGrid, int radius, int inten) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                Loc grid = sourceGrid.sum(Loc.row(y).col(x));
                int dist = sourceGrid.distance(grid);
                if (!inBounds(grid)) continue;
                if (dist > radius) continue;
                if (!los(this, sourceGrid, grid)) continue;

                // Only light a wall if the face lit is possibly visible to the player
                if (!squareAllowsLOS(grid) && !sourceCanLightWall(player, sourceGrid, grid)) continue;

                // adjust the light level
                int currLight = getSquare(grid).getLight();
                if (inten > 0) {
                    // light decreasing further away
                    getSquare(grid).setLight(currLight + (inten - dist));
                } else {
                    // Light increasing further away
                    getSquare(grid).setLight(currLight + (inten + dist));
                }
            }
        }
    }

    /**
     * Decides whether a permanently glowing wall shows a <em>lit</em> face to the player, the port
     * of C's {@code glow_can_light_wall} ({@code cave-view.c}).
     *
     * <p>A wall is drawn as one grid but lit as a block with four faces, and the glow flag on the
     * wall itself says nothing about which of those faces is actually alight. What lights a face is
     * an open glowing grid standing against it, so the question this method answers is whether any
     * of the open grids on the player's side of the wall is glowing and placed to light the face
     * the player is looking at. The caller has already established that the wall carries the glow
     * flag; that is never re-tested here.
     *
     * <p>Both the wall's neighbours and the player are reduced to a single bearing.
     * {@code playerNext} is one step from the wall towards the player, obtained through
     * {@link Loc#motionDir(Loc)} and {@link Loc#nextGrid(DirectionEnum)}. Two cases settle
     * immediately: if that step lands back on the wall grid the player is standing in the wall,
     * sees every face, and one of them is lit; and if {@code playerNext} is itself an open glowing
     * grid, it lights the face turned towards the player.
     *
     * <p>Otherwise the two grids flanking {@code playerNext} are tried, and each must be open,
     * glowing, and pass {@link #sourceCanLightWall(Player, Loc, Loc)} — the flanker lights a face,
     * but not necessarily the face the player can see. Which pair the flankers are depends on the
     * bearing. When the step is diagonal, they are the two cardinal neighbours of the wall that lie
     * beside it, each built from one coordinate of the wall and one of {@code playerNext}; both are
     * therefore inside the chunk already and need no bounds test. When the step is a straight one
     * along a row or a column, the flankers are the grids to either side of {@code playerNext}
     * across that bearing, and those can fall off the edge of the map, so each is guarded by
     * {@link #inBounds(Loc)} first. C draws the same distinction, and the missing guards on the
     * diagonal pair are deliberate there rather than an omission carried over.
     *
     * <p>With every candidate exhausted the wall is glowing but has no lit face to show, and the
     * method returns {@code false}.
     *
     * <p><em>Function glowCanLightWall coded before 260828, commented in full on 260828.</em>
     *
     * @param player   the player the wall is being lit for; supplies the grid the lit face has to
     *                 be turned towards
     * @param wallGrid the location of the glowing wall under test
     * @return {@code true} if a face of the wall is both lit by a neighbouring glowing grid and
     * turned towards the player, {@code false} otherwise
     */
    private boolean glowCanLightWall(Player player, Loc wallGrid) {
        Loc playerNext = wallGrid.nextGrid(wallGrid.motionDir(player.getGrid()));
        Loc check;

        // If the player is in the wall grid, the player will see the lit face
        if (playerNext.equals(wallGrid)) return true;

        // If the grid in the direction of the player is not a wall, and is glowing
        // it'll illuminate the wall
        if (squareAllowsLOS(playerNext) && squareIsGlow(playerNext)) return true;

        // Try the two neighbouring squares adjacent to the one in the direction
        // of the player to see if one or more will illuminate the wall by
        // glowing. Those could be out of bounds if the direction isn't
        // diagonal.
        if (playerNext.getX() != wallGrid.getX()) {
            if (playerNext.getY() != wallGrid.getY()) {
                check = Loc.row(wallGrid.getY()).col(playerNext.getX());
                if (squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
                check = Loc.row(playerNext.getY()).col(wallGrid.getX());
                if (squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
            } else {
                check = Loc.row(wallGrid.getY() - 1).col(playerNext.getX());
                if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
                check = Loc.row(wallGrid.getY() + 1).col(playerNext.getX());
                if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                        && sourceCanLightWall(player, check, wallGrid)) return true;
            }
        } else {
            check = Loc.row(playerNext.getY()).col(wallGrid.getX() - 1);
            if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                    && sourceCanLightWall(player, check, wallGrid)) return true;
            check = Loc.row(playerNext.getY()).col(wallGrid.getX() + 1);
            if (inBounds(check) && squareAllowsLOS(check) && squareIsGlow(check)
                    && sourceCanLightWall(player, check, wallGrid)) return true;
        }

        // Adjacent squares have all been tested and won't light the wall by glowing
        return false;
    }

    /**
     * Decides whether a wall would <em>appear</em> lit to the player when a light source sits at
     * {@code sourceGrid}, setting aside range and whether the line of sight is actually clear. The
     * port of C's {@code source_can_light_wall} ({@code cave-view.c}).
     *
     * <p>A wall is drawn as a single grid, but it is lit as a solid block with four faces. A light
     * source only ever illuminates the face pointing towards it, and the player only ever sees the
     * face pointing towards them, so the wall looks lit exactly when those are the same face — or
     * when one of the two is standing close enough to see or light more than one face at once.
     * That geometric question is all this method answers; the caller is left to decide whether the
     * light reaches that far and whether anything stands in the way.
     *
     * <p>Both parties are reduced to a bearing rather than a position. {@code sourceNext} is the
     * single step from the wall towards the light, and {@code playerNext} the single step from the
     * wall towards the player, each obtained through {@link Loc#motionDir(Loc)} and
     * {@link Loc#nextGrid}. Two positions anywhere along the same bearing therefore give the same
     * answer, which is what makes the test a cheap comparison of two adjacent grids instead of a
     * trace along a line.
     *
     * <p>A bearing of {@link uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum#DIR_NONE} carries a
     * zero offset, so the step lands back on the wall itself. That is the signal for "coincident
     * with the wall", and it is handled first for each of the two in turn: a light source inside
     * the wall lights every face, and a player inside the wall sees every face. Either way the
     * faces cannot disagree and the answer is true.
     *
     * <p>Otherwise the two bearings must share at least one component. Sharing both means the light
     * and the player are on the same side of the wall, looking at the one lit face, and the answer
     * is true outright. Sharing neither means they are looking at different faces, and the answer is
     * false. Sharing exactly one leaves a diagonal pair, and the shared component names the grid
     * beside the wall through which the player's view of the lit face has to pass:
     *
     * <pre>
     *  p
     * ###1#
     *  &#64;
     * </pre>
     *
     * <p>Here the light-emitting monster {@code p} and the player {@code @} both have line of sight
     * to the wall {@code 1}, but the lit face is hidden behind the wall immediately to the left of
     * {@code 1}. Testing that intervening grid with {@link #squareAllowsLOS(Loc)} is what rules the
     * case out.
     *
     * <p>That intervening grid is always orthogonally adjacent to the wall, so it can only fall
     * outside the level when the wall sits on the outermost row or column. C asserts in-bounds at
     * that point; {@link #squareAllowsLOS(Loc)} returns false instead, which leaves the wall unlit
     * — the same answer the level's permanent outer boundary would give anyway.
     *
     * <p><em>Function sourceCanLightWall coded before 260828, commented in full on 260828.</em>
     *
     * @param player     the player the appearance is judged for; only their grid is read
     * @param sourceGrid the grid the light is emitted from
     * @param wallGrid   the grid of the wall being lit
     * @return true if the lit face of the wall is the face the player is looking at, and nothing
     * beside the wall blocks their view of it; false otherwise
     */
    private boolean sourceCanLightWall(Player player, Loc sourceGrid, Loc wallGrid) {
        Loc sourceNext = wallGrid.nextGrid(wallGrid.motionDir(sourceGrid));

        /*
         * If the light source is coincident with the wall, all faces will be
         * lit, and the player can potentially see it if it's within range and
         * the line of sight isn't broken.
         */
        if (sourceNext.equals(wallGrid)) return true;

        /*
         * If the player is coincident with the wall, all faces of the wall are
         * visible to the player and the player can see whichever of those is
         * lit by the light source.
         */
        Loc playerNext = wallGrid.nextGrid(wallGrid.motionDir(player.getGrid()));
        if (playerNext.equals(wallGrid)) return true;

        Loc check;

        /*
         * For the lit face of the wall to be visible to the player, the
         * view directions from the wall to the player and the wall to the
         * light source must share at least one component.
         */
        if (sourceNext.getX() == playerNext.getX()) {
            /*
             * If the view directions share both components, the lit face
             * will be visible to the player if in range and the line of
             * sight isn't broken.
             */
            if (sourceNext.getY() == playerNext.getY()) return true;
            check = Loc.row(wallGrid.getY()).col(sourceNext.getX());
        } else if (sourceNext.getY() == playerNext.getY()) {
            check = Loc.row(sourceNext.getY()).col(wallGrid.getX());
        } else {
            /*
             * If the view directions don't share a component, the lit face
             * is not visible to the player.
             */
            return false;
        }

        /*
         * When only one component of the view directions is shared, take the
         * common component and test whether there's a wall there that would
         * block the player's view of the lit face.  That prevents instances
         * like this:
         *  p
         * ###1#
         *  @
         * where both the light-emitting monster, 'p', and the player, '@',
         * have line of sight to the wall, '1', but the face of '1' that would
         * be lit is blocked by the wall immediately to the left of '1'.
         */
        return squareAllowsLOS(check);
    }

    /**
     * Snapshots which grids the player can currently see, then wipes the live visibility flags
     * ready for them to be recalculated. The port of C's {@code mark_wasseen}
     * ({@code cave-view.c}).
     *
     * <p>Every grid that is seen right now has {@link SquareEnum#SQUARE_WASSEEN} turned on, and
     * then every grid on the level — seen or not — has {@link SquareEnum#SQUARE_VIEW},
     * {@link SquareEnum#SQUARE_SEEN} and {@link SquareEnum#SQUARE_CLOSE_PLAYER} turned off. The
     * three cleared flags are the answer to "what can be seen from where the player is standing",
     * and they are rebuilt from scratch on every update rather than edited, so the sweep that
     * follows starts from an empty board.
     *
     * <p>The snapshot is what makes that affordable. Redrawing the whole level after every step
     * would be wasteful, so {@code updateView} only redraws the grids whose visibility changed —
     * and a change can only be spotted by comparing the new value against the old one. Holding the
     * old value in {@code SQUARE_WASSEEN} is how the comparison survives the wipe: after the
     * recalculation, {@link #squareIsSeen(Loc)} answers for now and {@code squareWasSeen} answers
     * for a moment ago, and the two disagreeing is precisely the redraw condition.
     *
     * <p>Function markWasSeen coded before 260827, commented in full on 260827.
     */
    private void markWasSeen() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Loc grid = Loc.row(y).col(x);
                if (squareIsSeen(grid))
                    getSquare(grid).sqInfoOn(SquareEnum.SQUARE_WASSEEN);
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_VIEW);
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_SEEN);
                getSquare(grid).sqInfoOff(SquareEnum.SQUARE_CLOSE_PLAYER);
            }
        }
    }

    public int getMonCurrent() {
        return monCurrent;
    }
}