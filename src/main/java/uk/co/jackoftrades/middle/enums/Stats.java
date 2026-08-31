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

import java.util.Locale;

/**
 * The player's primary statistics, mirroring the C original's {@code STAT_*}
 * constants. {@code STAT_NONE}/{@code STAT_MAX} bracket the five real stats.
 *
 * @author Rowan Crowther
 */
public enum Stats {
    /**
     * No/unset stat. @author Rowan Crowther
     */
    STAT_NONE(-1, ""),
    /**
     * Strength. @author Rowan Crowther
     */
    STAT_STR(0, "STR"),
    /** Intelligence. @author Rowan Crowther */
    STAT_INT(1, "INT"),
    /** Wisdom. @author Rowan Crowther */
    STAT_WIS(2, "WIS"),
    /** Dexterity. @author Rowan Crowther */
    STAT_DEX(3, "DEX"),
    /** Constitution. @author Rowan Crowther */
    STAT_CON(4, "CON"),
    /** Count sentinel. @author Rowan Crowther */
    STAT_MAX(5, "");

    private final int value;
    private final String statString;

    Stats(int value, String statString) {
        this.value = value;
        this.statString = statString;
    }

    public String getStatString() {
        return statString;
    }

    /**
     * The stat holding a given index, the inverse of {@link #getValue()}. Where C indexes an array
     * with the integer directly, the port has to turn it back into a constant, so this stands in
     * for every {@code stat_max[i]}-style subscript in the original.
     *
     * <p>Every index is answered by exactly one constant, sentinels included: {@code -1} gives
     * {@code STAT_NONE} and {@code 5} gives {@code STAT_MAX}. That is only true because the two
     * sentinels carry distinct values; while both held {@code -1} the walk could never reach
     * {@code STAT_MAX}, and callers converting an index would have had no way to name it.
     *
     * <p>An index outside the set answers {@code null} rather than throwing. C has no counterpart
     * to fail here - it would simply read past the end of the array - so the {@code null} is a
     * boundary the port adds, and callers that build an index by arithmetic should test it.
     *
     * <p>Method getStats coded on 260831, commented in full on 260831.
     *
     * @param value the index to look up
     * @return the stat carrying that index, or {@code null} if none does
     */
    public static Stats getStats(int value) {
        for (Stats stat : Stats.values()) {
            if (stat.getValue() == value)
                return stat;
        }

        return null;
    }

    /**
     * Looks a stat up by the name the game data writes it under, the port of C's
     * {@code stat_name_to_idx} ({@code player.c:111}). This is the route the parsers take: the
     * {@code stat:} line of a magic realm ({@code init.c:2876}) and the sub-type of the four
     * stat effects - {@code RESTORE_STAT}, {@code DRAIN_STAT}, {@code LOSE_RANDOM_STAT} and
     * {@code GAIN_STAT} ({@code effects.c:217}).
     *
     * <p>The names are the bare ones C's {@code stat_name_list[]} holds - {@code STR}, {@code INT},
     * {@code WIS}, {@code DEX}, {@code CON} and {@code MAX} - not the {@code STAT_} prefixed
     * constant names, which is why the prefix is added here before the lookup rather than being
     * expected from the caller.
     *
     * <p>C compares with {@code my_stricmp}, so the match is case-insensitive and the port
     * upper-cases first to the same effect. The case folding is pinned to {@link Locale#ROOT}
     * deliberately: under a Turkish locale the default folding maps {@code i} to a dotted capital,
     * and {@code "int"} and {@code "wis"} - the two names carrying an {@code i} - would stop
     * resolving while the other three carried on working. C's {@code toupper} runs in the
     * {@code C} locale and has no such rule.
     *
     * <p>{@code MAX} resolves, to {@code STAT_MAX}. That looks like a mistake and is not: C's name
     * list carries {@code "MAX"} at index 5 and returns it like any other, so the port answers the
     * constant holding 5. It is a by-product of the sentinel sitting in the list rather than
     * something the game data ever asks for, and a caller that treats the answer as a real stat
     * should reject it - as C's callers do, by bounds-testing against {@code STAT_MAX}.
     *
     * <p>{@code NONE} does not resolve, because C's list has no such entry and would return its
     * not-found answer for it. That answer is {@code -1} in C and {@code null} here, and it is also
     * what an empty or unrecognised name gives.
     *
     * <p>Method statNameToIdx coded on 260831, commented in full on 260831.
     *
     * @param name the bare stat name from the game data, in any case
     * @return the stat that name identifies, or {@code null} where C would answer {@code -1}
     */
    public static Stats statNameToIdx(String name) {
        String tag = "STAT_" + name.toUpperCase(Locale.ROOT);
        try {
            Stats stat = Stats.valueOf(tag);
            if (stat != STAT_NONE)
                return stat;
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * The name a stat is written under in the game data and shown under in prompts, the port of
     * C's {@code stat_idx_to_name} ({@code player.c:122}) and the inverse of
     * {@link #statNameToIdx(String)}. The wizard's stat editor round-trips through the pair,
     * seeding its prompt with a name and reading the reply back as a stat
     * ({@code cmd-wizard.c:1309-1313}).
     *
     * <p>The answer is the bare name - {@code STR}, not {@code STAT_STR}. Reaching for the
     * constant's own name here would break that round trip, because the reply would come back
     * carrying a prefix {@link #statNameToIdx(String)} adds for itself.
     *
     * <p>C asserts that the index is one of the five real stats, so the sentinels are a
     * programming error there rather than a value. The port has no assertion and hands back the
     * empty string both constants carry, which is a difference in failure mode rather than in
     * behaviour: no input C accepts is answered differently. A {@code null} stat throws rather
     * than failing an assertion, for the same reason.
     *
     * <p>Method statIdxToName coded on 260831, commented in full on 260831.
     *
     * @param stat one of the five real stats
     * @return the bare name C's {@code stat_name_list[]} holds for it, or the empty string for
     * either sentinel
     */
    public static String statIdxToName(Stats stat) {
        return stat.getStatString();
    }

    /**
     * The stat's index, which is its position in C's stat enum ({@code player.h:32}) and therefore
     * its position in every stat-indexed array the game keeps - {@code stat_max}, {@code stat_cur},
     * the race and class adjustments, and the savefile.
     *
     * <p>C generates those indices from {@code list-stats.h} with the {@code STAT()} macro, and its
     * own comment is blunt about what they are: changing the order breaks savefiles. So these are
     * not arbitrary ordinals but part of the file format, which is why they are written out here
     * rather than taken from {@link #ordinal()} - an enum constant inserted above {@code STAT_STR}
     * would silently renumber the stats if they were.
     *
     * <p>{@code STAT_MAX} carries 5, the value C's {@code STAT_MAX} takes as the last member of the
     * enum, so it counts the real stats. {@code STAT_NONE} carries -1, which is the port's own: C
     * has no {@code STAT_NONE} in the stat enum at all, and -1 is instead what its
     * {@code stat_name_to_idx} returns for a name it does not recognise.
     *
     * <p>Method getValue coded on 260831, commented in full on 260831.
     *
     * @return the stat's index, {@code -1} for {@code STAT_NONE} and {@code 5} for
     * {@code STAT_MAX}
     */
    public int getValue() {
        return value;
    }
}
