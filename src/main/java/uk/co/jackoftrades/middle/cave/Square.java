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
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.Player;

import java.util.ArrayList;

public class Square {
    private Feature feat;
    private final Flag<SquareEnum> info;

    private int light;
    private int monster;
    private final ArrayList<ItemObject> objects;
    private final ArrayList<Trap> traps;

    public Square(Feature feature, int light, int monster) {
        this.feat = feature;
        this.light = light;
        this.monster = monster;

        info = new Flag<>(SquareEnum.class);
        objects = new ArrayList<>();
        traps = new ArrayList<>();
    }

    /**
     * Get the current light status of this square
     * TODO: Change the light intensity from an integer to an enum?
     *
     * @return the current light status of this square
     */
    private int getLight() {
        return light;
    }

    /**
     * Checks to see the current lighting level of this square
     *
     * @return the current lighting value of this square
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isCurrentlyLit() {
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
     * Tests for any door including open, closed, & hidden
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
        return monster < 0;
    }

    /**
     * Tests if a mob or the player is in this square
     *
     * @return true if the square contains either a mob or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isOccupied() {
        return monster != 0;
    }

    /**
     * Tests to see if a square is occupied
     *
     * @return true if the square doesn't contain a monster or the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFree() {
        return monster == 0;
    }

    /**
     * Tests if this square is known by the player
     *
     * @param c    The chunk we are examining, should be the owning chunk of this square
     * @param grid The location in that chunk of this square
     * @return True if the information known about this square is also known by the player
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isKnown(Chunk c, Loc grid) {
        Chunk mainCave = GameConstants.cave;
        Player mainPlayer = GameConstants.mainPlayer;
        if (!c.equals(mainCave) && (!c.equals(mainPlayer.getCave())))
            return false;

        if (mainPlayer.getCave() == null)
            return false;

        return !mainPlayer.getCave().getSquare(grid).feat.isNoFeat();
    }

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
        Chunk cave = GameConstants.cave;
        Player mainPlayer = GameConstants.mainPlayer;

        return !isKnown(c, grid) || !(mainPlayer.getCave().getSquare(grid).feat.equals(cave.getSquare(grid).feat));
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
     * Tests for illumination
     *
     * @return true if the square is lit
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isLit() {
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
        return info.has(SquareEnum.SQUARE_SEEN);
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
        return isFloor() && monster != 0;
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
        return isOpen() && (objects.isEmpty());
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
        return feat.isLos();
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
        return objects.isEmpty();
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
}