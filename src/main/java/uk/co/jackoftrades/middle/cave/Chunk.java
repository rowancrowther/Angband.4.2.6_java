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

    public Square getSquare(Loc grid) {
        return squares[grid.getX()][grid.getY()];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
