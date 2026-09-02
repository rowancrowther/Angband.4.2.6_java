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

package uk.co.jackoftradesltd.middle.objects.enums;

public enum ChestTrapCode {
    NO_TRAP,
    POISON,
    LOSE_STR,
    LOSE_CON,
    SUMMON,
    PARALYZE,
    EXPLODE;

    private final static int MAX_TRAPS = 14;
    private final int pval;

    ChestTrapCode() {
        this.pval = 1 << ordinal();
    }

    public static int getMaxTraps() {
        return MAX_TRAPS;
    }

    public int getPval() {
        return pval;
    }
}