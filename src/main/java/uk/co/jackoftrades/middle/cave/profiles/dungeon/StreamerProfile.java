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
 * The mineral veins running through one style of level — the port of C's
 * {@code struct streamer_profile} (generate.h:201), loaded from the {@code streamer:} line of
 * {@code dungeon_profile.txt}.
 *
 * <p>A streamer is drawn as a random walk that stops at the level edge, turning a band of rock
 * around its path to magma or quartz; some of those grids hold buried treasure. See
 * {@code gen-cave.c:121} and {@code build_streamer}.
 *
 * <p>As with {@link TunnelProfile}, C's unused {@code name} field is not ported.
 *
 * @author Rowan Crowther
 */
public class StreamerProfile {
    /**
     * How many grids near each step of the walk become vein.
     */
    private int den;

    /**
     * How far from the walk those grids may lie.
     */
    private int rng;

    /**
     * How many magma streamers the level gets.
     */
    private int mam;

    /**
     * Reciprocal chance of treasure in magma: a grid holds treasure with probability 1/this, so a
     * larger number means rarer treasure.
     */
    private int mc;

    /**
     * How many quartz streamers the level gets.
     */
    private int qua;

    /**
     * Reciprocal chance of treasure in quartz, as {@link #mc}.
     */
    private int qc;

    /**
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
     * @return how many grids near each step of the walk become vein
     */
    public int getDen() {
        return den;
    }

    /**
     * @return how far from the walk those grids may lie
     */
    public int getRng() {
        return rng;
    }

    /**
     * @return how many magma streamers the level gets
     */
    public int getMam() {
        return mam;
    }

    /**
     * @return the reciprocal chance of treasure in magma
     */
    public int getMc() {
        return mc;
    }

    /**
     * @return how many quartz streamers the level gets
     */
    public int getQua() {
        return qua;
    }

    /**
     * @return the reciprocal chance of treasure in quartz
     */
    public int getQc() {
        return qc;
    }
}
