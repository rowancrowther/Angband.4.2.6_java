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

package uk.co.jackoftrades.middle.game.event;

/**
 * {@link GameEventData} payload describing a bolt/beam being drawn between two
 * grids — the projection type, whether it is a beam, whether the player sees it,
 * the origin {@code (ox, oy)} and current {@code (x, y)} grids. Consumed by the
 * display layer to animate the projectile.
 *
 * @author Rowan Crowther
 */
public class EventDataBolt implements GameEventData {
    /**
     * The projection type being drawn.
     *
     * @author Rowan Crowther
     */
    private int projType;        // Probably going to be replaced by Enum
    /**
     * Whether the bolt is currently being drawn.
     *
     * @author Rowan Crowther
     */
    private boolean drawing;
    /**
     * Whether the player can see the bolt.
     *
     * @author Rowan Crowther
     */
    private boolean seen;
    /**
     * Whether this is a beam (true) rather than a single bolt (false).
     *
     * @author Rowan Crowther
     */
    private boolean beam;
    /**
     * Origin row.
     *
     * @author Rowan Crowther
     */
    private int oy;
    /**
     * Origin column.
     *
     * @author Rowan Crowther
     */
    private int ox;
    /**
     * Current row.
     *
     * @author Rowan Crowther
     */
    private int y;
    /**
     * Current column.
     *
     * @author Rowan Crowther
     */
    private int x;

    /**
     * Build a bolt/beam payload.
     *
     * @param projType the projection type
     * @param drawing  whether it is being drawn
     * @param seen     whether the player sees it
     * @param beam     whether it is a beam
     * @param oy       origin row
     * @param ox       origin column
     * @param y        current row
     * @param x        current column
     * @author Rowan Crowther
     */
    public EventDataBolt(int projType, boolean drawing, boolean seen, boolean beam, int oy, int ox, int y, int x) {
        this.projType = projType;
        this.drawing = drawing;
        this.seen = seen;
        this.beam = beam;
        this.oy = oy;
        this.ox = ox;
        this.y = y;
        this.x = x;
    }

    /**
     * @return the projection type
     * @author Rowan Crowther
     */
    public int getProjType() {
        return projType;
    }

    /**
     * @return whether the bolt is being drawn
     * @author Rowan Crowther
     */
    public boolean isDrawing() {
        return drawing;
    }

    /**
     * @return whether the player sees the bolt
     * @author Rowan Crowther
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @return whether this is a beam
     * @author Rowan Crowther
     */
    public boolean isBeam() {
        return beam;
    }

    /**
     * @return the origin row
     * @author Rowan Crowther
     */
    public int getOy() {
        return oy;
    }

    /**
     * @return the origin column
     * @author Rowan Crowther
     */
    public int getOx() {
        return ox;
    }

    /**
     * @return the current row
     * @author Rowan Crowther
     */
    public int getY() {
        return y;
    }

    /**
     * @return the current column
     * @author Rowan Crowther
     */
    public int getX() {
        return x;
    }
}
