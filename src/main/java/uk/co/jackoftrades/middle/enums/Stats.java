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

package uk.co.jackoftrades.middle.enums;

/**
 * The player's primary statistics, mirroring the C original's {@code STAT_*}
 * constants. {@code STAT_NONE}/{@code STAT_MAX} bracket the five real stats.
 *
 * @author ClaudeCode
 */
public enum Stats {
    /**
     * No/unset stat. @author ClaudeCode
     */
    STAT_NONE(-1, ""),
    /** Strength. @author ClaudeCode */
    STAT_STR(0, "STR"),
    /** Intelligence. @author ClaudeCode */
    STAT_INT(1, "INT"),
    /** Wisdom. @author ClaudeCode */
    STAT_WIS(2, "WIS"),
    /** Dexterity. @author ClaudeCode */
    STAT_DEX(3, "DEX"),
    /** Constitution. @author ClaudeCode */
    STAT_CON(4, "CON"),
    /** Count sentinel. @author ClaudeCode */
    STAT_MAX(-1, "");

    private final int value;
    private final String statString;

    Stats(int value, String statString) {
        this.value = value;
        this.statString = statString;
    }

    public String getStatString() {
        return statString;
    }

    public int getValue() {
        return value;
    }

    public static Stats getStats(int value) {
        for (Stats stat : Stats.values()) {
            if (stat.getValue() == value)
                return stat;
        }

        return Stats.STAT_NONE;
    }
}
