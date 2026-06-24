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

package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.cave.Loc;

import java.util.ArrayList;

/**
 * {@link GameEventData} payload describing an explosion ("ball") effect — the
 * projection type, the affected grids with their distances from the centre and
 * per-grid visibility, and the blast centre. Consumed by the display layer to
 * animate the blast radius.
 *
 * @author ClaudeCode
 */
public class EventDataExplosion implements GameEventData {
    /**
     * The projection type of the explosion.
     *
     * @author ClaudeCode
     */
    private int projType;       // Probably going to be replaced by an enum
    /**
     * Number of grids affected by the blast.
     *
     * @author ClaudeCode
     */
    private int numGrids;
    /**
     * Distance from the centre for each affected grid (parallel to {@link #blastGrid}).
     *
     * @author ClaudeCode
     */
    private ArrayList<Integer> distanceToGrid;
    /**
     * Whether the explosion is currently being drawn.
     *
     * @author ClaudeCode
     */
    private boolean drawing;
    /**
     * Whether the player sees each affected grid (parallel to {@link #blastGrid}).
     *
     * @author ClaudeCode
     */
    private ArrayList<Boolean> playerSeesGrid;
    /**
     * The grids affected by the blast.
     *
     * @author ClaudeCode
     */
    private ArrayList<Loc> blastGrid;
    /**
     * The centre of the explosion.
     *
     * @author ClaudeCode
     */
    private Loc centre;

    /**
     * Build an explosion payload.
     *
     * @param projType       the projection type
     * @param numGrids       number of affected grids
     * @param distanceToGrid per-grid distance from centre
     * @param drawing        whether it is being drawn
     * @param playerSeesGrid per-grid visibility
     * @param blastGrid      the affected grids
     * @param centre         the blast centre
     * @author ClaudeCode
     */
    public EventDataExplosion(int projType, int numGrids, ArrayList<Integer> distanceToGrid, boolean drawing,
                              ArrayList<Boolean> playerSeesGrid, ArrayList<Loc> blastGrid, Loc centre) {
        this.projType = projType;
        this.numGrids = numGrids;
        this.distanceToGrid = distanceToGrid;
        this.drawing = drawing;
        this.playerSeesGrid = playerSeesGrid;
        this.blastGrid = blastGrid;
        this.centre = centre;
    }

    /**
     * @return the projection type
     * @author ClaudeCode
     */
    public int getProjType() {
        return projType;
    }

    /**
     * @return the number of affected grids
     * @author ClaudeCode
     */
    public int getNumGrids() {
        return numGrids;
    }

    /**
     * @return the per-grid distances from the centre
     * @author ClaudeCode
     */
    public ArrayList<Integer> getDistanceToGrid() {
        return distanceToGrid;
    }

    /**
     * @return whether the explosion is being drawn
     * @author ClaudeCode
     */
    public boolean isDrawing() {
        return drawing;
    }

    /**
     * @return the per-grid visibility flags
     * @author ClaudeCode
     */
    public ArrayList<Boolean> getPlayerSeesGrid() {
        return playerSeesGrid;
    }

    /**
     * @return the affected grids
     * @author ClaudeCode
     */
    public ArrayList<Loc> getBlastGrid() {
        return blastGrid;
    }

    /**
     * @return the blast centre
     * @author ClaudeCode
     */
    public Loc getCentre() {
        return centre;
    }
}