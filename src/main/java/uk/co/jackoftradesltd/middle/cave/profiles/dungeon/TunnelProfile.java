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

package uk.co.jackoftradesltd.middle.cave.profiles.dungeon;

/**
 * How corridors are dug on one style of level — the port of C's {@code struct tunnel_profile}
 * ({@code generate.h}), loaded from the {@code tunnel:} line of {@code dungeon_profile.txt}.
 *
 * <p>Every field is a percentage, rolled as {@code randint0(100) < value} (except {@code con},
 * which is rolled the other way round — see {@link #con}) while the tunneller advances one grid at
 * a time, so together they decide whether corridors run straight and purposeful or wander and
 * dead-end. The rolls live in {@code gen-cave.c}, functions {@code build_tunnel()} (rnd, chg, con,
 * pen) and {@code try_door()} (jct). Values are stored raw; C never clamps them at load time.
 *
 * <p>C's struct also carries a {@code name}, but nothing ever sets or reads it — the struct is
 * embedded in {@code cave_profile} by value and reached as {@code profile->tun}. It is left out
 * here rather than ported as a permanently-null field.
 *
 * <p>Class TunnelProfile coded on 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class TunnelProfile {
    /**
     * Percentage chance of digging in a random direction rather than towards the target. In C
     * ({@code build_tunnel()}) this is only rolled after a bend has been allowed by {@link #chg},
     * and again when the tunneller has to be steered back into bounds.
     *
     * <p>Field rnd coded on 260930, commented in full on 260930.
     */
    private int rnd;

    /**
     * Percentage chance of changing direction at a tunnel grid. Rolled each time the bend interval
     * has run out; on success the tunneller first corrects towards the target, then {@link #rnd}
     * may knock it into a random direction.
     *
     * <p>Field chg coded on 260930, commented in full on 260930.
     */
    private int chg;

    /**
     * Percentage chance of "extra tunneling": the chance that the tunneller is <em>exempt</em> from
     * pre-emptive termination at a grid. C rolls {@code randint0(100) >= con} and only then checks
     * whether the tunnel has strayed more than 10 grids horizontally or vertically from its start,
     * abandoning it if so. A high value therefore makes tunnels persist, not stop — despite the
     * comment in {@code dungeon_profile.txt} calling it the chance of terminating a tunnel.
     *
     * <p>Field con coded on 260930, commented in full on 260930.
     */
    private int con;

    /**
     * Percentage chance of a random door where the tunnel pierces a room wall. Rolled per pierced
     * wall grid in {@code build_tunnel()}, and the door is only placed if the grid allows a
     * wall-piercing door.
     *
     * <p>Field pen coded on 260930, commented in full on 260930.
     */
    private int pen;

    /**
     * Percentage chance of a door at a junction between tunnels. Used by {@code try_door()}: a
     * door is placed when {@code randint0(100) < jct}, otherwise a trap is placed when the much
     * rarer {@code randint0(500) < jct} succeeds, in both cases only where the grid is a possible
     * doorway.
     *
     * <p>Field jct coded on 260930, commented in full on 260930.
     */
    private int jct;

    /**
     * Builds a tunnel profile from the five values of a {@code tunnel:} line, in the order the
     * line gives them ({@code rnd:chg:con:pen:jct}). No range checking is done, matching C's
     * parser, which stores whatever {@code parser_getint()} returns.
     *
     * <p>Constructor coded on 260930, commented in full on 260930.
     *
     * @param rnd chance of a random direction instead of the intended one
     * @param chg chance of changing direction at a tunnel grid
     * @param con chance of being exempt from pre-emptive termination (extra tunneling)
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
     * Getter for {@link #chg}. Coded on 260930, commented in full on 260930.
     *
     * @return the chance of changing direction at a tunnel grid
     */
    public int getChg() {
        return chg;
    }

    /**
     * Getter for {@link #con}. Coded on 260930, commented in full on 260930.
     *
     * @return the chance of being exempt from pre-emptive termination (extra tunneling)
     */
    public int getCon() {
        return con;
    }

    /**
     * Getter for {@link #jct}. Coded on 260930, commented in full on 260930.
     *
     * @return the chance of a door at a tunnel junction
     */
    public int getJct() {
        return jct;
    }

    /**
     * Getter for {@link #pen}. Coded on 260930, commented in full on 260930.
     *
     * @return the chance of a door where the tunnel pierces a room
     */
    public int getPen() {
        return pen;
    }

    /**
     * Getter for {@link #rnd}. Coded on 260930, commented in full on 260930.
     *
     * @return the chance of digging in a random direction rather than towards the target
     */
    public int getRnd() {
        return rnd;
    }
}
