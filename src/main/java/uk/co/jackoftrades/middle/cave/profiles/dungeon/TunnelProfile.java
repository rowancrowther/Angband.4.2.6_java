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

package uk.co.jackoftrades.middle.cave.profiles.dungeon;

/**
 * How corridors are dug on one style of level — the port of C's {@code struct tunnel_profile}
 * (generate.h:192), loaded from the {@code tunnel:} line of {@code dungeon_profile.txt}.
 *
 * <p>Every field is a percentage rolled against as the tunneller advances one grid at a time, so
 * together they decide whether corridors run straight and purposeful or wander and dead-end. See
 * {@code gen-cave.c:520-830} for the rolls themselves.
 *
 * <p>C's struct also carries a {@code name}, but nothing ever sets or reads it — the struct is
 * embedded in {@code cave_profile} by value and reached as {@code profile->tun}. It is left out
 * here rather than ported as a permanently-null field.
 *
 * @author Rowan Crowther
 */
public class TunnelProfile {
    /**
     * Percentage chance of digging in a random direction rather than towards the target.
     */
    private int rnd;

    /**
     * Percentage chance of changing direction at a tunnel grid.
     */
    private int chg;

    /**
     * Percentage chance of simply ending the tunnel where it stands.
     */
    private int con;

    /**
     * Percentage chance of a door where the tunnel pierces a room wall.
     */
    private int pen;

    /**
     * Percentage chance of a door at a junction between tunnels.
     */
    private int jct;

    /**
     * @param rnd chance of a random direction instead of the intended one
     * @param chg chance of changing direction at a tunnel grid
     * @param con chance of ending the tunnel
     * @param pen chance of a door at a room entrance
     * @param jct chance of a door at a tunnel junction
     */
    public TunnelProfile(int rnd, int chg, int con, int pen, int jct) {
        this.chg = chg;
        this.con = con;
        this.jct = jct;
        this.pen = pen;
        this.rnd = rnd;
    }

    /**
     * @return the chance of changing direction at a tunnel grid
     */
    public int getChg() {
        return chg;
    }

    /**
     * @return the chance of ending the tunnel
     */
    public int getCon() {
        return con;
    }

    /**
     * @return the chance of a door at a tunnel junction
     */
    public int getJct() {
        return jct;
    }

    /**
     * @return the chance of a door where the tunnel pierces a room
     */
    public int getPen() {
        return pen;
    }

    /**
     * @return the chance of digging in a random direction rather than towards the target
     */
    public int getRnd() {
        return rnd;
    }
}
