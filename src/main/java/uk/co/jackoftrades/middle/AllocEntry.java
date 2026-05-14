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

package uk.co.jackoftrades.middle;

public class AllocEntry {
    private int Index;
    private int level;
    private int prob1;
    private int prob2;
    private int prob3;

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getProb1() {
        return prob1;
    }

    public void setProb1(int prob1) {
        this.prob1 = prob1;
    }

    public int getProb2() {
        return prob2;
    }

    public void setProb2(int prob2) {
        this.prob2 = prob2;
    }

    public int getProb3() {
        return prob3;
    }

    public void setProb3(int prob3) {
        this.prob3 = prob3;
    }
}
