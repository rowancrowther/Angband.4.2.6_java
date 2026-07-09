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
 * {@link GameEventData} payload reporting the outcome of carving one tunnel
 * during level generation — step/pierce/dig counts and the start/end "city
 * block" distances to the goal. Emitted for generation-introspection events; a
 * {@code dend} of 0 means the tunnel reached its goal.
 *
 * @author Rowan Crowther
 */
public class EventDataTunnel implements GameEventData {
    /**
     * The total number of tunnelling steps made.
     *
     * @author Rowan Crowther
     */
    private int nstep;      // The total number of tunnelling steps made
    /**
     * The total number of wall piercings for rooms.
     *
     * @author Rowan Crowther
     */
    private int npierce;    // The total number of wall piercings for rooms
    /**
     * The number of times excavated (excluding wall piercings).
     *
     * @author Rowan Crowther
     */
    private int ndug;       // The number of times excavated (excluding wall piercings)

    /**
     * City-block distance between the starting point and the tunnel goal
     * (i.e. {@code abs(start.x-end.x) + abs(start.y-end.y)}).
     *
     * @author Rowan Crowther
     */
    private int dstart;     // The 'city block' distance between the starting point and the tunnel goal
    // i.e. ABS(start.x - end.x) + ABS(start.y - end.y)
    /**
     * City-block distance between the final tunnel point and the goal; 0 means
     * the tunnel reached its goal.
     *
     * @author Rowan Crowther
     */
    private int dend;       // The 'city block' distance between the final point in the tunnel and the goal
    //  dend value of 0 means the tunnel reached its goal

    /**
     * Whether the tunnel was cut short by the random early-termination criteria.
     *
     * @author Rowan Crowther
     */
    private boolean early;  // Whether the tunnel was terminated by the random early termination criteria

    /**
     * Build a tunnel-result payload.
     *
     * @param nstep   tunnelling steps made
     * @param npierce wall piercings made
     * @param ndug    excavations made
     * @param dstart  start-to-goal distance
     * @param dend    end-to-goal distance (0 = reached goal)
     * @param early   whether it terminated early
     * @author Rowan Crowther
     */
    public EventDataTunnel(int nstep, int npierce, int ndug, int dstart, int dend, boolean early) {
        this.nstep = nstep;
        this.npierce = npierce;
        this.ndug = ndug;
        this.dstart = dstart;
        this.dend = dend;
        this.early = early;
    }

    /**
     * @return the number of tunnelling steps made
     * @author Rowan Crowther
     */
    public int getNstep() {
        return nstep;
    }

    /**
     * @return the number of wall piercings made
     * @author Rowan Crowther
     */
    public int getNpierce() {
        return npierce;
    }

    /**
     * @return the number of excavations made
     * @author Rowan Crowther
     */
    public int getNdug() {
        return ndug;
    }

    /**
     * @return the start-to-goal city-block distance
     * @author Rowan Crowther
     */
    public int getDstart() {
        return dstart;
    }

    /**
     * @return the end-to-goal city-block distance (0 = reached goal)
     * @author Rowan Crowther
     */
    public int getDend() {
        return dend;
    }

    /**
     * @return whether the tunnel terminated early
     * @author Rowan Crowther
     */
    public boolean isEarly() {
        return early;
    }
}