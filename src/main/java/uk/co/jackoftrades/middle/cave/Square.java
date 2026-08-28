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

import org.jetbrains.annotations.*;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.Pile;
import uk.co.jackoftrades.middle.player.Player;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A single dungeon grid's contents: its terrain {@link Feature}, the per-grid
 * {@link SquareEnum} info flags, lighting, the occupying monster (or player), the
 * object {@link Pile} and any {@link Trap}s. The large family of {@code isXxx()}
 * predicates are convenience tests over the feature and info flags. This is the
 * Java port of the C original's {@code struct square} and the {@code square_*}
 * predicates ({@code src/cave.h} / {@code src/cave-square.c}).
 *
 * @author Rowan Crowther
 */
public class Square {
    /**
     * The terrain feature occupying this grid.
     */
    private Feature feat;
    /**
     * Per-grid info flags (seen, view, room, vault, generation hints, …).
     */
    private final Flag<SquareEnum> info;

    /**
     * Current light intensity of this grid (>0 means lit).
     */
    private int light;
    /**
     * Occupant index: positive for a monster, negative for the player, 0 if empty.
     */
    private int monsterIndex;
    /**
     * The pile of objects lying on this grid.
     */
    private Pile objectPile;
    /**
     * The traps present on this grid.
     */
    private ArrayList<Trap> traps;

    /**
     * Build a square with the given feature, light level and occupant, starting
     * with empty info flags, an empty object pile and no traps.
     *
     * @param feature      the terrain feature
     * @param light        the initial light level
     * @param monsterIndex the occupant index (monster &gt; 0, player &lt; 0, 0 if empty)
     */
    public Square(Feature feature, int light, int monsterIndex) {
        this.feat = feature;
        this.light = light;
        this.monsterIndex = monsterIndex;

        info = new Flag<>(SquareEnum.class);
        objectPile = new Pile();
        traps = new ArrayList<>();
    }

    /**
     * Check the square info field to see if a particular flag is set on it
     *
     * @param squareInfo the flag we are checking for
     * @return true if the flag is set on the info field
     */
    boolean hasInfoFlag(SquareEnum squareInfo) {
        return info.has(squareInfo);
    }

    /**
     * Excise an object from a floor pile, leaving it orphaned (and hence potential bait for the garbage collector)
     *
     * @param item The item we are removing.
     */
    public void pileExcise(ItemObject item) {
        objectPile.excise(item);
    }

    /**
     * Gets the top most object on this square
     * <br/><br/>
     * TODO: Deal with returning the next object from the square as at present this is impossible
     * @return the top most object on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public @Nullable ItemObject getTopObject() {
        if (objectPile.isEmpty()) return null;
        return objectPile.lastItem();
    }

    /**
     * Gets the top most trap on a square
     * <br/><br/>
     * Can have multiple traps on a square, but currently not allowed in C code. TODO check if this needs to be kept in
     *
     * @return the top most trap on a square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public @Nullable Trap getTrap() {
        if (traps.isEmpty()) return null;
        return traps.getFirst();
    }

    /**
     * Get the current light status of this square
     * TODO: Change the light intensity from an integer to an enum?
     *
     * @return the current light status of this square
     */
    public int getLight() {
        return light;
    }

    /**
     * Checks whether a given object is in this square's pile, the port of C's
     * {@code square_holds_object} ({@code cave-square.c}).
     *
     * <p>Identity, not equality of kind: the question is whether <em>this</em> object is here, not
     * whether something like it is. Two Flasks of Oil on the floor are distinct objects and the test
     * distinguishes them, which is what makes it usable as a location check — C uses it to decide
     * whether a known object is still attached to the pile it thinks it is on, and
     * {@link uk.co.jackoftrades.middle.player.Player#knowObject} uses it to tell an object under the
     * player's feet from one elsewhere on the level.
     *
     * <p>Function holdsObject coded on 260816, commented in full on 260816.
     *
     * @param object the object we are looking for
     * @return {@code true} if the object is in this square's pile
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean holdsObject(@NotNull ItemObject object) {
        return objectPile.contains(object);
    }

    /**
     * Checks to see the current lighting level of this square, the port of C's
     * {@code square_islit} ({@code cave-square.c}). This is the light actually falling on the grid
     * from every source — glow, the player's light, monster light — as recomputed by
     * {@code calcLighting}, not the {@code SQUARE_GLOW} flag tested by {@link #isGlow()}.
     *
     * @return true if the square's light level is above zero
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isLit() {
        return light > 0;
    }

    /**
     * Test for normal open floor
     *
     * @return true if the square is normal open floor
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFloor() {
        return feat.isFloor();
    }

    /**
     * Tests for the ability to hold a trap
     *
     * @return true if the square can hold a trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isTrappable() {
        return feat.isTrapHolding();
    }

    /**
     * Tests for whether the square can hold an object
     *
     * @return true if the square can hold an object
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isObjectHolding() {
        return feat.isObjectHolding();
    }

    /**
     * Check to see if the square is a granite wall
     *
     * @return true if the square is a granite wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isRock() {
        return feat.isGranite() && !feat.hasAnyDoor();
    }

    /**
     * Tests whether the square seems like a wall or not
     *
     * @return true if this square seems like a wall to the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featSeemsLikeWall() {
        return feat.isRock();
    }

    /**
     * Tests for whether we have an interesting feat or not
     *
     * @return true if the feat is interesting
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsIntersting() {
        return feat.isInteresting();
    }

    /**
     * Tests to see if this is granite
     *
     * @return true if the square is granite
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isGranite() {
        return feat.isGranite();
    }

    /**
     * Test to see if the feature is a permanent wall
     *
     * @return true for a permanent wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isPerm() {
        return feat.isPermanent() && feat.isRock();
    }

    /**
     * Checks to see if there is an artefact on this square
     *
     * @return true if this square contains an artefact
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean hasObjectArtifact() {
        return objectPile.hasArtifact();
    }

    /**
     * Test for magma (Stef beware!)
     *
     * @return true if the feature is magma
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMagma() {
        return feat.isMagma();
    }

    /**
     * Tests for Quartz
     *
     * @return true if this square is quartz
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isQuartz() {
        return feat.isQuartz();
    }

    /**
     * Tests for minerals
     *
     * @return true if this square is rock, quartz or magma
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMineral() {
        return feat.isRock() || feat.isQuartz() || feat.isMagma();
    }

    /**
     * Tests for gold veins
     *
     * @return true if there is a gold vein here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean hasGoldVein() {
        return feat.isTreasure();
    }

    /**
     * Tests for rubble, defined as rock which isn't in a wall
     *
     * @return true if this square has rubble in it
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isRubble() {
        return !feat.isWall() && feat.isRock();
    }

    /**
     * Get an iterator through the pile
     *
     * @return an Iterator<ItemObject> for the pile of objects on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    Iterator<ItemObject> getSquarePileIterator() {
        return objectPile.getIterator();
    }

    /**
     * Tests for secret doors
     * <br/><br/>
     * These appear as if they were granite, when detected they are replaced by a closed door
     *
     * @return true if this square contains a secret door
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isSecretDoor() {
        return feat.hasAnyDoor() && feat.isRock();
    }

    /**
     * Tests for open doors
     *
     * @return true if a door is open here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isOpenDoor() {
        return feat.isOpenDoor();
    }

    /**
     * Test to see if this is a closed door (locked/jammed are also closed)
     *
     * @return true for a closed door
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isClosedDoor() {
        return feat.isClosedDoor();
    }

    /**
     * Tests for a broken door
     *
     * @return true if this door is broken
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isBrokenDoor() {
        return feat.hasAnyDoor() && feat.isPassable() && !feat.isCloseable();
    }

    /**
     * Test to see if this square is a locked door
     *
     * @return true if this square contains a door of power greater than 0
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isLockedDoor() {
        return squareDoorPower() > 0;
    }

    /**
     * Test to see if this square is an unlocked door
     *
     * @return true if this square contains a door of power of 0
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isUnlockedDoor() {
        return isClosedDoor() && squareDoorPower() == 0;
    }

    /**
     * The current power of the lock on the door of this square
     *
     * @return the current door lock power
     */
    @CheckReturnValue
    @Contract(pure = true)
    private int squareDoorPower() {
        if (!isClosedDoor()) return 0;

        // Confirm there is a trap before actually looking it up in the registry
        if (!isTrap()) return 0;
        TrapKind lock = TrapKind.lookupTrap("door lock");

        if (!trapSpecific(lock)) return 0;

        for (Trap trap : traps) {
            if (trap.getKind() == lock) {
                return trap.getPower();
            }
        }

        return 0;
    }

    /**
     * Tests for any door including open, closed, and hidden
     *
     * @return true for any door
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDoor() {
        return feat.hasAnyDoor();
    }

    /**
     * Tests for any type of staircase
     *
     * @return true for any type of staircase
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isStairs() {
        return feat.isStair();
    }

    /**
     * Tests for an upward staircase
     *
     * @return true for an up staircase
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isUpStairs() {
        return feat.isUpStair();
    }

    /**
     * Tests for the presence of a downward going staircase
     *
     * @return true for downstairs
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDownStairs() {
        return feat.isDownStair();
    }

    /**
     * Test for shop entrance
     *
     * @return true if this is a shop entrance
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isShop() {
        return feat.isShop();
    }

    /**
     * Test for the location of the player
     *
     * @return true if the player is here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isPlayer() {
        return monsterIndex < 0;
    }

    /**
     * Tests if a mob or the player is in this square
     *
     * @return true if the square contains either a mob or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isOccupied() {
        return monsterIndex != 0;
    }

    /**
     * Tests to see if a square is occupied
     *
     * @return true if the square doesn't contain a monster or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFree() {
        return monsterIndex == 0;
    }
//
//    /**
//     * Tests if this square is known by the player
//     *
//     * @param c    The chunk we are examining, should be the owning chunk of this square
//     * @param grid The location in that chunk of this square
//     * @return True if the information known about this square is also known by the player
//     */
//    @Contract(pure = true)
//    @CheckReturnValue
//    public boolean isKnown(Chunk c, Loc grid) {
//        Chunk mainCave = GameConstants.cave;
//        Player mainPlayer = GameConstants.mainPlayer;
//        if (!c.equals(mainCave) && (!c.equals(mainPlayer.getCave())))
//            return false;
//
//        if (mainPlayer.getCave() == null)
//            return false;
//
//        return !mainPlayer.getCave().getSquare(grid).feat.isNoFeat();
//    }

    /**
     * Tests to see if the player's memory of this square has failed
     *
     * @param c    The chunk we are examining
     * @param grid the grid in that chunk which points to this square in the other grids
     * @return true if there is a difference between the features of this square and the players chunk square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMemoryBad(Chunk c, Loc grid) {
        Chunk cave = GameState.getCave();
        Player mainPlayer = GameState.getPlayer();

        return !c.isKnown(grid) || !(mainPlayer.getCave().getSquare(grid).feat.equals(cave.getSquare(grid).feat));
    }

    /*
     * Square predicates
     */

    /**
     * Tests to see if this square is marked
     *
     * @return true if this square is marked
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMark() {
        return info.has(SquareEnum.SQUARE_MARK);
    }

    /**
     * Tests for the permanent glow flag, the port of C's {@code square_isglow}
     * ({@code cave-square.c}). This is the terrain's own illumination — a lit room, a daylit
     * surface grid — and is independent of the transient light level tested by {@link #isLit()}.
     *
     * @return true if the square carries {@code SQUARE_GLOW}
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isGlow() {
        return info.has(SquareEnum.SQUARE_GLOW);
    }

    /**
     * Tests to see if this room is part of a vault, not the role it plays in that vault
     *
     * @return true if the square is part of a vault
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isVault() {
        return info.has(SquareEnum.SQUARE_VAULT);
    }

    /**
     * Tests to see if this is part of a room
     *
     * @return true if it is part of a room
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isRoom() {
        return info.has(SquareEnum.SQUARE_ROOM);
    }

    /**
     * Tests whether the player has seen this square
     *
     * @return true if the player has seen this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isSeen() {
        return info.has(SquareEnum.SQUARE_SEEN);
    }

    /**
     * Tests to see whether the player can currently see this square
     *
     * @return true if this square is in view
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isView() {
        return info.has(SquareEnum.SQUARE_VIEW);
    }

    /**
     * Tests if this square was seen before the current update
     *
     * @return true if the square was seen
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean wasSeen() {
        return info.has(SquareEnum.SQUARE_WASSEEN);
    }

    /**
     * Tests if this square triggers a feeling
     *
     * @return true if this square triggers a feeling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFeel() {
        return info.has(SquareEnum.SQUARE_FEEL);
    }

    /**
     * Tests if this square has a known trap
     *
     * @return true if this square has a known trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isTrap() {
        return info.has(SquareEnum.SQUARE_TRAP);
    }

    /**
     * Get all the traps associated with this square
     *
     * @return the traps on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public ArrayList<Trap> getTraps() {
        return traps;
    }

    /**
     * Tests to see if this square has an unknown trap
     *
     * @return true if this square has an unknown trap
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isInvis() {
        return info.has(SquareEnum.SQUARE_INVIS);
    }

    /**
     * Tests to see if this square in an inner wall (generation)
     *
     * @return true if this square is an inner wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isWallInner() {
        return info.has(SquareEnum.SQUARE_WALL_INNER);
    }

    /**
     * Tests to see if this square is an outer wall (generation)
     *
     * @return true if this square is an outer wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isWallOuter() {
        return info.has(SquareEnum.SQUARE_WALL_OUTER);
    }

    /**
     * Tests to see if this square is a solid wall (generation)
     *
     * @return true if this square is a solid wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isWallSolid() {
        return info.has(SquareEnum.SQUARE_WALL_SOLID);
    }

    /**
     * Tests to see if there are monster restrictions on this square (generation)
     *
     * @return true for monster restrictions on this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isMonRestrict() {
        return info.has(SquareEnum.SQUARE_MON_RESTRICT);
    }

    /**
     * Tests tp see of the square cannot be teleported FROM by the player
     *
     * @return true if the player cannot teleport from this square
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoTeleport() {
        return info.has(SquareEnum.SQUARE_NO_TELEPORT);
    }

    /**
     * Tests if this square cannot be magically mapped by the player
     *
     * @return true if this square CANNOT be magically mapped
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoMap() {
        return info.has(SquareEnum.SQUARE_NO_MAP);
    }

    /**
     * Tests if the square can't be detected by player ESP
     *
     * @return true if the player cannot detect this square by ESP
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoEsp() {
        return info.has(SquareEnum.SQUARE_NO_ESP);
    }

    /**
     * Tests to see if this square is marked for projection processing
     *
     * @return true if this square is marked for projection processing
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isProject() {
        return info.has(SquareEnum.SQUARE_PROJECT);
    }

    /**
     * Tests to see if this square has been detected for traps
     *
     * @return true if the player has detected for traps here
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isDTrap() {
        return info.has(SquareEnum.SQUARE_DTRAP);
    }

    /**
     * Tests to see if the square is inappropriate to place stairs
     *
     * @return true if this square is inappropriate to place stairs
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isNoStairs() {
        return info.has(SquareEnum.SQUARE_NO_STAIRS);
    }

    /**
     * Check for the location of a player trap on this square
     *
     * @return true if this square contains a player trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isPlayerTrap() {
        return trapFlag(TrapEnum.TRF_TRAP);
    }

    /**
     * Check whether this square has a web trap on it
     *
     * @return true if this square has a web trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isWebbed() {
        if (!isTrap()) return false;
        TrapKind webTrap = TrapKind.lookupTrap("web");
        return trapSpecific(webTrap);
    }

    /**
     * Checks for a decoy trap
     *
     * @return true if this square has a decoy trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isDecoyed() {
        TrapKind decoyedTrap = TrapKind.lookupTrap("decoy");
        return trapSpecific(decoyedTrap);
    }

    /**
     * Checks for a warded trap
     *
     * @return true if this square has a warded trap on it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isWarded() {
        TrapKind wardedTrap = TrapKind.lookupTrap("glyph of warding");
        return trapSpecific(wardedTrap);
    }

    /**
     * Check for a specific kind of trap on a square. This only checks for the same description text, as the TrapKind
     * class also contains information which may not be the same for trap of the same kind
     * TODO: Check this out
     *
     * @param kind the kind of trap we are checking for
     * @return true if one of the traps on this square is the same kind of trap as the incoming kind
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean trapSpecific(TrapKind kind) {
        if (!isTrap()) return false;

        for (Trap trap : traps) {
            if (trap.getKind().getDescription().equals(kind.getDescription())) return true;
        }

        return false;
    }

    /**
     * Checks if there is a visible trap on this square
     *
     * @return true for the existance of visible traps
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isVisibleTrap() {
        return trapFlag(TrapEnum.TRF_VISIBLE);
    }

    /**
     * Check for the existance of a trap with a given flag on this square
     *
     * @param trapFlag the flag to check for
     * @return if there is a trap on this square with the given flag set
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean trapFlag(TrapEnum trapFlag) {
        if (!isTrap())
            return false;

        for (Trap trap : traps) {
            if (trap.hasTrap(trapFlag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the remaining time for a trap identified by its index to be disabled. Note, the first matching trap on the
     * square is used
     *
     * @param trapIndex the integer index of the trap
     * @return the number of turns until this trap disarms
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int trapTimeout(int trapIndex) {
        for (Trap trap : traps) {
            if (trapIndex >= 0 && trapIndex != trap.getTrapIndex())
                continue;

            return trap.getTimeout();
        }

        return 0;
    }

    /**
     * Checks to see if this square is open, a floor square not occupied by a monster
     *
     * @return true for an empty square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isOpen() {
        return isFloor() && isFree();
    }

    /**
     * Tests to see if this square is empty, (an open square without any items)
     *
     * @return true if the square doesn't contain any items and is open
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isEmpty() {
        if (isPlayerTrap() || isWebbed()) return false;
        return isOpen() && (objectPile.isEmpty());
    }

    /**
     * Check to see if this square can br run through
     *
     * @return true if this square can be run through
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isArrivable() {
        if (isOccupied() || isPlayerTrap() || isWebbed()) return false;
        if (isFloor() || isStairs()) return true;
        return false;
    }

    /**
     * Checks to see if this square is monster walkable
     *
     * @return true if a monster can walk through this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsMonsterWalkable() {
        return feat != null && feat.isMonsterWalkable();
    }

    /**
     * Checks to see if the player can walk through this square
     *
     * @return true if the square is passable by the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsPassable() {
        return feat != null && feat.isPassable();
    }

    /**
     * Checks to see if a projectile can pass through this square
     *
     * @return true if this square can have a projectable in it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsProjectable() {
        return feat != null && feat.isProjectable();
    }

    /**
     * Checks to see if the feature of this square allows line of sight
     *
     * @return true if this square allows LoS
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featAllowsLOS() {
        return feat != null && feat.isLos();
    }

    /**
     * Checks to see if the feature of this square is a wall
     *
     * @return true if this square is a wall
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean featIsWall() {
        return feat != null && feat.isWall();
    }

    /**
     * Check to see if this square is internally lit
     *
     * @return true if this square is internally lit
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsBright() {
        return feat != null && feat.isBright();
    }

    /**
     * Checks if this square is fire based
     *
     * @return true if this square is lava
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsFiery() {
        return feat != null && feat.isFiery();
    }

    /**
     * Checks if the square doesn't allow monster flow information
     *
     * @return true if the square DOESN'T allow monster flow information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsNoFlow() {
        return feat != null && feat.isNoFlow();
    }

    /**
     * Tests to see if this square carries player scent or not
     *
     * @return true if this square DOESN'T carry player scent
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean featIsNoScent() {
        return feat != null && feat.isNoScent();
    }

    /**
     * Check to see if this is an untrapped square without items
     *
     * @return true if this is an untrapped square without items
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean canPutItem() {
        if (isObjectHolding() || isTrap()) return false;
        return objectPile.isEmpty();
    }

    /**
     * Check to see if the square can damage an individual - currently only lava
     *
     * @return true if the square is lava
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isDamaging() {
        return feat.isFiery();
    }

    /**
     * True if a feeling can be used on this square
     *
     * @return true if this square can be used for a feeling
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean allowsFeel() {
        return featIsPassable() && !isDamaging();
    }

    /**
     * Getter
     *
     * @return the feat of this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Feature getFeature() {
        return feat;
    }

    /**
     * Getter
     *
     * @return the int index of the monster on this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getMonsterIndex() {
        return monsterIndex;
    }

    /**
     * Setter
     *
     * @param feature the feature to set this.feat to
     */
    void setFeature(@NotNull Feature feature) {
        feat = feature; }

    /**
     * Test-only helper that populates this square with a known fixture. When
     * {@code full} is true the square becomes a lit floor occupied by a monster,
     * carrying three objects, a trap and all info flags set; otherwise it becomes
     * an empty, dark, unknown square holding the player.
     *
     * @param full whether to build the fully-populated fixture
     */
    @TestOnly
    void setUpTest(boolean full) {
        if (objectPile == null)
            objectPile = new Pile();

        if (full) {
            feat = TerrainRegistry.lookupFeature(TerrainFlags.FEAT_FLOOR);
            light = 2;
            monsterIndex = 1;
            objectPile.clear();
            objectPile.insert(new ItemObject());
            objectPile.insert(new ItemObject());
            objectPile.insert(new ItemObject());
            traps.clear();
            traps.addFirst(new Trap());
            info.clear();
            info.negate();
        } else {
            feat = TerrainRegistry.lookupFeature(TerrainFlags.FEAT_NONE);
            light = 0;
            monsterIndex = -1;
            objectPile.clear();
            traps.clear();
            info.clear();
        }
    }

    /**
     * Returns the objects lying on this square, the port of reading C's {@code square(c, grid)->obj}.
     *
     * <p>Live, not a copy — this is how objects are added to and taken from the floor, so a snapshot
     * would be useless. C reaches the same pile through {@code square_object}, which hands back the
     * head of a linked list that callers then walk by {@code obj->next}; the port keeps a
     * {@link Pile}, which is why iteration here goes through {@link Pile#getIterator} and why
     * {@link #holdsObject} can be a single containment test rather than a walk.
     *
     * <p>Function getObjectPile commented in full on 260816.
     *
     * @return this square's object pile, shared with this instance
     */
    public Pile getObjectPile() {
        return objectPile;
    }

    /**
     * Redraws this square on screen, the port of C's {@code square_light_spot}
     * ({@code cave-view.c}).
     *
     * <p>A display refresh rather than a change of state: nothing about the square is altered, the
     * player is simply shown it again because something that decides how it is drawn has moved on.
     * {@code Player.flavourAware} is the current caller — becoming aware of a kind can change the
     * glyph its items are drawn with, so every floor square holding one is refreshed.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the display side of the rework; takes no action,
     * so callers currently make their decisions correctly and simply leave the screen stale.
     *
     * <p>Function lightSpot coded before 260817, commented in full on 260817.
     */
    public void lightSpot() {
        // STUB function. TODO: Implement
    }

    /**
     * Turns one info flag on for this square, the port of C's {@code sqinfo_on}
     * ({@code cave.h}).
     *
     * <p>The info field is a set of independent flags rather than a value, so this adds the one
     * named and leaves the rest of the field alone. Setting a flag already set is not an error and
     * changes nothing — callers such as {@code Chunk.markWasSeen} sweep whole levels and set as
     * they go rather than testing first, which only works because the operation is idempotent.
     *
     * <p>The write goes through this method rather than exposing the field because the info flags
     * are the square's own state: a caller handed the {@link Flag} itself could keep it and write
     * to the square long after it stopped looking like a caller.
     *
     * <p>The return value distinguishes the two cases the method itself treats alike: {@code true}
     * means the square changed, {@code false} that the flag was already on and the call did
     * nothing. It matches C, where {@code sqinfo_on} resolves to {@code flag_on}
     * ({@code z-bitflag.c}) and reports the same thing. Nothing in C reads it, so a caller here has
     * no ported precedent to follow — it is available for the redraw question, "did this call
     * actually change what the player would see", which is otherwise only answerable by testing the
     * flag first.
     *
     * <p>Function sqInfoOn coded before 260827, commented in full on 260827, updated on 260827 when
     * the return type changed from void to boolean.
     *
     * @param flag the info flag to set on this square
     * @return true if the flag was off and is now on, false if it was already on and nothing
     * changed
     */
    public boolean sqInfoOn(SquareEnum flag) {
        return info.on(flag);
    }

    /**
     * Turns one info flag off for this square, the port of C's {@code sqinfo_off}
     * ({@code cave.h}).
     *
     * <p>The counterpart to {@link #sqInfoOn(SquareEnum)}, and equally narrow: it clears the one
     * flag named and no other. That matters more here than it does for setting, because the info
     * field mixes flags with very different lifetimes — {@code SQUARE_VIEW} is rebuilt every time
     * the player moves, while {@code SQUARE_MARK} records what they have explored and must survive
     * the whole game. Clearing per flag is what lets the visibility sweep run over every grid on
     * the level without erasing the map.
     *
     * <p>Clearing a flag that is not set is not an error and changes nothing.
     *
     * <p>The return value says which of those happened: {@code true} that the flag was on and has
     * been cleared, {@code false} that it was already off. As with {@link #sqInfoOn(SquareEnum)}
     * this matches C's {@code flag_off} ({@code z-bitflag.c}). It is worth more here than on the
     * setting side, because clearing is the operation done in bulk — the visibility sweep clears
     * three flags from every grid on the level, and the great majority of those calls return
     * {@code false} because there was nothing there to clear.
     *
     * <p>Function sqInfoOff coded before 260827, commented in full on 260827, updated on 260827
     * when the return type changed from void to boolean.
     *
     * @param flag the info flag to clear from this square
     * @return true if the flag was on and is now off, false if it was already off and nothing
     * changed
     */
    public boolean sqInfoOff(SquareEnum flag) {
        return info.off(flag);
    }

    /**
     * Sets the light falling on this square, the port of writing C's
     * {@code c->squares[y][x].light} ({@code cave-view.c}).
     *
     * <p>C has no setter for this: {@code calc_lighting} and {@code add_light} assign the field
     * directly, and {@code square_light} ({@code cave-square.c}) is the only accessor of the pair.
     * The port needs the write to go through a method because the field is private, so this is a
     * plain assignment with no C counterpart to match beyond the assignments themselves.
     *
     * <p>The value is a light <em>intensity</em>, not a flag, and it is not validated or clamped
     * here. Zero is dark and anything above it is lit, which is all {@link #isLit()} asks; higher
     * values matter to the display, which draws a brightly lit grid differently from a dimly lit
     * one. Negative values are legitimate — a monster with negative {@code light} radiates darkness
     * and {@code addLight} subtracts, so a grid can finish a recalculation below zero. Clamping
     * here would silently diverge from C, which lets the arithmetic stand.
     *
     * <p>The caller is {@code Chunk.calcLighting}, which owns the field's whole lifetime:
     * it resets every grid on the level to 1 or 0 from permanent glow alone, then accumulates
     * bright terrain, the player's light and each monster's light on top. Nothing else should write
     * a light level, because a value set outside that sweep survives only until the next one runs.
     *
     * <p>Function setLight coded before 260828, commented in full on 260828.
     *
     * @param level the new light intensity for this square: zero for dark, positive for lit, negative
     *              for a grid the darkness sources have taken below zero
     */
    public void setLight(int level) {
        light = level;
    }
}