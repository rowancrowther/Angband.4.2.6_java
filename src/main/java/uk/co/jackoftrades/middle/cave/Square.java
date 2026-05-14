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

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.Player;

import java.util.ArrayList;

public class Square {
    private Feature feat;
    private Flag<SquareEnum> info;

    private int light;
    private int monster;
    private ArrayList<ItemObject> objects;
    private ArrayList<Trap> traps;

    /**
     * Test for normal open floor
     *
     * @return true if the square is normal open floor
     */
    public boolean isFloor() {
        return feat.isFloor();
    }

    /**
     * Tests for the ability to hold a trap
     *
     * @return true if the square can hold a trap
     */
    public boolean isTrappable() {
        return feat.isTrapHolding();
    }

    /**
     * Tests for whether the square can hold an object
     *
     * @return true if the square can hold an object
     */
    public boolean isObjectHolding() {
        return feat.isObjectHolding();
    }

    /**
     * Check to see if the square is a granite wall
     *
     * @return true if the square is a granite wall
     */
    public boolean isRock() {
        return feat.isGranite() && !feat.hasAnyDoor();
    }

    /**
     * Tests to see if this is granite
     *
     * @return true if the square is granite
     */
    public boolean isGranite() {
        return feat.isGranite();
    }

    /**
     * Test to see if the feature is a permanent wall
     *
     * @return true for a permanent wall
     */
    public boolean isPerm() {
        return feat.isPermanent() && feat.isRock();
    }

    /**
     * Test for magma (Stef beware!)
     *
     * @return true if the feature is magma
     */
    public boolean isMagma() {
        return feat.isMagma();
    }

    /**
     * Tests for Quartz
     *
     * @return true if this square is quartz
     */
    public boolean isQuartz() {
        return feat.isQuartz();
    }

    /**
     * Tests for minerals
     *
     * @return true if this square is rock, quartz or magma
     */
    public boolean isMineral() {
        return feat.isRock() || feat.isQuartz() || feat.isMagma();
    }

    /**
     * Tests for gold veins
     *
     * @return true if there is a gold vein here
     */
    public boolean hasGoldVein() {
        return feat.isTreasure();
    }

    /**
     * Tests for rubble, defined as rock which isn't in a wall
     *
     * @return true if this square has rubble in it
     */
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
    public boolean isSecretDoor() {
        return feat.hasAnyDoor() && feat.isRock();
    }

    /**
     * Tests for open doors
     *
     * @return true if a door is open here
     */
    public boolean isOpenDoor() {
        return feat.isOpenDoor();
    }

    /**
     * Test to see if this is a closed door (locked/jammed are also closed)
     *
     * @return true for a closed door
     */
    public boolean isClosedDoor() {
        return feat.isClosedDoor();
    }

    /**
     * Tests for a broken door
     *
     * @return true if this door is broken
     */
    public boolean isBrokenDoor() {
        return feat.hasAnyDoor() && feat.isPassable() && !feat.isCloseable();
    }

    /**
     * Tests for any door including open, closed, & hidden
     *
     * @return true for any door
     */
    public boolean isDoor() {
        return feat.hasAnyDoor();
    }

    /**
     * Tests for any type of staircase
     *
     * @return true for any type of staircase
     */
    public boolean isStairs() {
        return feat.isStair();
    }

    /**
     * Tests for an upward staircase
     *
     * @return true for an up staircase
     */
    public boolean isUpStairs() {
        return feat.isUpStair();
    }

    /**
     * Tests for the presence of a downward going staircase
     *
     * @return true for downstairs
     */
    public boolean isDownStairs() {
        return feat.isDownStair();
    }

    /**
     * Test for shop entrance
     *
     * @return true if this is a shop entrance
     */
    public boolean isShop() {
        return feat.isShop();
    }

    /**
     * Test for the location of the player
     *
     * @return true if the player is here
     */
    public boolean isPlayer() {
        return (monster < 0);
    }

    /**
     * Tests if a mob or the player is in this square
     *
     * @return true if the square contains either a mob or the player
     */
    public boolean isOccupied() {
        return (monster != 0);
    }

    /**
     * Tests if this square is known by the player
     *
     * @param c    The chunk we are examining, should be the owning chunk of this square
     * @param grid The location in that chunk of this square
     * @return True if the information known about this square is also known by the player
     */
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
    public boolean isMark() {
        return info.has(SquareEnum.SQUARE_MARK);
    }

    /**
     * Tests for illumination
     *
     * @return true if the square is lit
     */
    public boolean isLit() {
        return info.has(SquareEnum.SQUARE_GLOW);
    }

    /**
     * Tests to see if this room is part of a vault, not the role it plays in that vault
     *
     * @return true if the square is part of a vault
     */
    public boolean isVault() {
        return info.has(SquareEnum.SQUARE_VAULT);
    }

    /**
     * Tests to see if this is part of a room
     *
     * @return true if it is part of a room
     */
    public boolean isRoom() {
        return info.has(SquareEnum.SQUARE_ROOM);
    }

    /**
     * Tests whether the player has seen this square
     *
     * @return true if the player has seen this square
     */
    public boolean isSeen() {
        return info.has(SquareEnum.SQUARE_SEEN);
    }

    /**
     * Tests to see whether the player can currently see this square
     *
     * @return true if this square is in view
     */
    public boolean isView() {
        return info.has(SquareEnum.SQUARE_VIEW);
    }
}