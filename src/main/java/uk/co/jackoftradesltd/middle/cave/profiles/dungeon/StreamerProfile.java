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
 * The mineral veins running through one style of level — the port of C's
 * {@code struct streamer_profile} in {@code generate.h}, loaded from the {@code streamer:} line of
 * {@code dungeon_profile.txt} (fields in file order: {@code den : rng : mag : mc : qua : qc}).
 *
 * <p>A streamer is drawn by C's {@code build_streamer} in {@code gen-cave.c}: it starts near the
 * middle of the level, picks one of the eight directions and walks that way until it leaves the
 * level. At every step it makes {@link #den} attempts, each picking a random grid within
 * {@link #rng} of the walk in both axes; a grid that is plain rock becomes magma or quartz, and
 * with probability 1/{@link #mc} (or 1/{@link #qc}) it is then upgraded to a vein holding known
 * treasure. Each level builder calls it {@link #mam} times for magma and {@link #qua} times for
 * quartz, after the tunnels are dug.
 *
 * <p>The town's record ({@code streamer:1:1:0:0:0:0}) has no streamers; {@code classic} and
 * {@code modified} use {@code streamer:5:2:3:90:2:40}.
 *
 * <p>As with {@link TunnelProfile}, C's unused {@code name} field is not ported. C's
 * {@code mag} field is named {@code mam} here; it is the same count.
 *
 * <p>Class StreamerProfile coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class StreamerProfile {
    /**
     * C's {@code den}: how many grids near each step of the walk become vein. It is the loop count
     * of attempts per step, so a pick that lands on something other than rock still uses one up.
     *
     * <p>Field den coded before 260930, commented in full on 260930.
     */
    private int den;

    /**
     * C's {@code rng}: how far from the walk those grids may lie. The pick is a square of side
     * {@code 2 * rng + 1} centred on the walk, limited to grids fully inside the level.
     *
     * <p>Field rng coded before 260930, commented in full on 260930.
     */
    private int rng;

    /**
     * C's {@code mag}: how many magma streamers the level gets.
     *
     * <p>Field mam coded before 260930, commented in full on 260930.
     */
    private int mam;

    /**
     * C's {@code mc}: reciprocal chance of treasure in magma: a vein grid holds treasure with
     * probability 1/this, so a larger number means rarer treasure. It is only consulted when
     * {@link #mam} is above zero.
     *
     * <p>Field mc coded before 260930, commented in full on 260930.
     */
    private int mc;

    /**
     * C's {@code qua}: how many quartz streamers the level gets.
     *
     * <p>Field qua coded before 260930, commented in full on 260930.
     */
    private int qua;

    /**
     * C's {@code qc}: reciprocal chance of treasure in quartz, as {@link #mc}.
     *
     * <p>Field qc coded before 260930, commented in full on 260930.
     */
    private int qc;

    /**
     * Builds a streamer profile from the six integers of a {@code streamer:} line, stored as given
     * with no clamping, as C's {@code parse_profile_streamer} does.
     *
     * <p>Constructor StreamerProfile coded before 260930, commented in full on 260930.
     *
     * @param den how many grids near each walk step become vein
     * @param rng how far from the walk those grids may lie
     * @param mam how many magma streamers the level gets
     * @param mc  reciprocal chance of treasure in magma
     * @param qua how many quartz streamers the level gets
     * @param qc  reciprocal chance of treasure in quartz
     */
    public StreamerProfile(int den, int rng, int mam, int mc, int qua, int qc) {
        this.den = den;
        this.rng = rng;
        this.mam = mam;
        this.mc = mc;
        this.qua = qua;
        this.qc = qc;
    }

    /**
     * Function getDen coded before 260930, commented in full on 260930.
     *
     * @return how many grids near each step of the walk become vein (C's {@code den})
     */
    public int getDen() {
        return den;
    }

    /**
     * Function getRng coded before 260930, commented in full on 260930.
     *
     * @return how far from the walk those grids may lie (C's {@code rng})
     */
    public int getRng() {
        return rng;
    }

    /**
     * Function getMam coded before 260930, commented in full on 260930.
     *
     * @return how many magma streamers the level gets (C's {@code mag})
     */
    public int getMam() {
        return mam;
    }

    /**
     * Function getMc coded before 260930, commented in full on 260930.
     *
     * @return the reciprocal chance of treasure in magma (C's {@code mc})
     */
    public int getMc() {
        return mc;
    }

    /**
     * Function getQua coded before 260930, commented in full on 260930.
     *
     * @return how many quartz streamers the level gets (C's {@code qua})
     */
    public int getQua() {
        return qua;
    }

    /**
     * Function getQc coded before 260930, commented in full on 260930.
     *
     * @return the reciprocal chance of treasure in quartz (C's {@code qc})
     */
    public int getQc() {
        return qc;
    }
}