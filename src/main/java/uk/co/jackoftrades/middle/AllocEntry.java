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

package uk.co.jackoftrades.middle;

/**
 * One entry in an allocation table used to pick a random object/monster/etc.
 * weighted by dungeon level. The three probability fields are the standard
 * Angband three-stage weights ({@code prob1}/{@code prob2}/{@code prob3}) applied
 * at successive filtering stages of the selection. This is the Java port of the
 * C original's {@code struct alloc_entry} ({@code src/init.h}).
 *
 * @author Rowan Crowther
 */
public class AllocEntry {
    /**
     * Index of the thing this entry allocates (into its kind table).
     *
     * @author Rowan Crowther
     */
    private int Index;
    /**
     * Native level of the thing (depth at which it naturally appears).
     *
     * @author Rowan Crowther
     */
    private int level;
    /**
     * Base (stage 1) allocation probability weight.
     *
     * @author Rowan Crowther
     */
    private int prob1;
    /**
     * Stage 2 allocation probability weight (after the first filter).
     *
     * @author Rowan Crowther
     */
    private int prob2;
    /**
     * Stage 3 allocation probability weight (after the second filter).
     *
     * @author Rowan Crowther
     */
    private int prob3;

    /**
     * @return the index of the allocated thing
     * @author Rowan Crowther
     */
    public int getIndex() {
        return Index;
    }

    /**
     * @param index the index of the allocated thing
     * @author Rowan Crowther
     */
    public void setIndex(int index) {
        Index = index;
    }

    /**
     * @return the native level of the allocated thing
     * @author Rowan Crowther
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the native level of the allocated thing
     * @author Rowan Crowther
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the stage 1 probability weight
     * @author Rowan Crowther
     */
    public int getProb1() {
        return prob1;
    }

    /**
     * @param prob1 the stage 1 probability weight
     * @author Rowan Crowther
     */
    public void setProb1(int prob1) {
        this.prob1 = prob1;
    }

    /**
     * @return the stage 2 probability weight
     * @author Rowan Crowther
     */
    public int getProb2() {
        return prob2;
    }

    /**
     * @param prob2 the stage 2 probability weight
     * @author Rowan Crowther
     */
    public void setProb2(int prob2) {
        this.prob2 = prob2;
    }

    /**
     * @return the stage 3 probability weight
     * @author Rowan Crowther
     */
    public int getProb3() {
        return prob3;
    }

    /**
     * @param prob3 the stage 3 probability weight
     * @author Rowan Crowther
     */
    public void setProb3(int prob3) {
        this.prob3 = prob3;
    }
}
