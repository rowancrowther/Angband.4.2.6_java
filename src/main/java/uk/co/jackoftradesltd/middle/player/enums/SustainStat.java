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

package uk.co.jackoftradesltd.middle.player.enums;

import uk.co.jackoftradesltd.middle.enums.Stats;

import java.util.Arrays;

/**
 * The five sustain flags, one per stat, named by the stat they protect. C has no type for this:
 * the sustains are the first five {@code OF_*} object flags ({@code list-object-flags.h}), laid
 * out so that a stat's index plus one is the index of its sustain, and
 * {@code sustain_flag()} ({@code obj-properties.c}) does that addition. This enum gives the
 * result of the addition a name.
 *
 * <p>The values are those flag indices. {@code SUS_STAT_NONE} is 0, the slot {@code OF_NONE}
 * holds at the head of {@link uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag}, and
 * {@code SUS_STAT_STR} to {@code SUS_STAT_CON} are 1 to 5, the positions of {@code OF_SUST_STR}
 * to {@code OF_SUST_CON} behind it. Stat order and sustain order must stay in step, as the
 * comments in {@code list-stats.h} and {@code list-object-flags.h} both insist.
 *
 * <p>{@code SUS_STAT_NONE} and {@code SUS_STAT_MAX} are the sentinels; there is no sustain for
 * either.
 *
 * <p>Enum SustainStat coded on 261004, commented in full on 261004.
 *
 * @author Rowan Crowther
 */
public enum SustainStat {
    SUS_STAT_NONE(0),
    SUS_STAT_STR(1),
    SUS_STAT_INT(2),
    SUS_STAT_WIS(3),
    SUS_STAT_DEX(4),
    SUS_STAT_CON(5),
    SUS_STAT_MAX(6);

    /**
     * The index of the matching object flag, as C's {@code OF_SUST_*} numbers it: 0 for the
     * {@code NONE} sentinel, 1 to 5 for the five real sustains, 6 for the {@code MAX} sentinel.
     *
     * <p>Field sustainValue coded on 261004, commented in full on 261004.
     */
    int sustainValue;

    /**
     * Binds each constant to its object flag index.
     *
     * <p>Constructor SustainStat coded on 261004, commented in full on 261004.
     *
     * @param sustainValue the index of the matching {@code OF_SUST_*} flag
     */
    SustainStat(int sustainValue) {
        this.sustainValue = sustainValue;
    }

    /**
     * The sustain that protects a given stat, the port of {@code sustain_flag()}
     * ({@code obj-properties.c}). C is handed a stat index and returns that index plus one; the
     * port takes the {@link Stats} constant and hands back the {@code SustainStat} holding that
     * number. The work is done in {@link #getSustainFromInt(int)}.
     *
     * <p>The answer for the five real stats is {@code STAT_STR} to {@code SUS_STAT_STR} through
     * {@code STAT_CON} to {@code SUS_STAT_CON}. C answers {@code -1} for an index outside 0 to 4,
     * which covers {@code STAT_NONE} (-1) and {@code STAT_MAX} (5); the port answers
     * {@code null} for both. That is a change of representation, not of behaviour: {@code -1} is
     * not a flag index in C, so {@code null} carries the same "no sustain" meaning.
     *
     * <p>Three of the four C callers ({@code obj-knowledge.c}, {@code obj-info.c},
     * {@code ui-knowledge.c}) pass a loop index over the five real stats, so the out-of-range
     * answer never arises for them. The fourth, {@code effect_handler_DRAIN_STAT} in
     * {@code effect-handler-general.c}, passes the effect's subtype and <em>does</em> act on it,
     * testing {@code flag < 0} and returning {@code false}. A port of that handler must test for
     * {@code null} here in the same place.
     *
     * <p>A {@code null} stat throws {@link NullPointerException}; C has no equivalent.
     *
     * <p>Method getSustainFromStat coded on 261004, commented in full on 261005.
     *
     * @param stat the stat whose sustain is wanted
     * @return the matching sustain, or {@code null} for either stat sentinel
     */
    public static SustainStat getSustainFromStat(Stats stat) {
        return getSustainFromInt(stat.getValue());
    }

    /**
     * The sustain for a stat given as a raw index, the body of {@code sustain_flag()}
     * ({@code obj-properties.c}). Despite the name the argument is a <em>stat</em> index, as
     * {@link Stats#getValue()} numbers it, not a sustain value: the method adds one to reach the
     * sustain's flag index and then finds the constant carrying that number.
     *
     * <p>The bounds are those of C's {@code stat < 0 || stat >= STAT_MAX} test, restated on the
     * incremented number: an index of 0 to 4 gives 1 to 5 and resolves, and anything else,
     * including -1 and 5, gives {@code null} where C gives {@code -1}. See
     * {@link #getSustainFromStat(Stats)} for what a caller must do with that {@code null}.
     *
     * <p>Method getSustainFromInt coded on 261004, commented in full on 261005.
     *
     * @param statValue a stat index, 0 to 4 for the real stats
     * @return the sustain for that stat, or {@code null} if the index is not a real stat
     */
    public static SustainStat getSustainFromInt(int statValue) {
        statValue++;
        if (statValue < 1 || statValue > 5) return null;

        int finalStatValue = statValue;
        return Arrays.stream(SustainStat.values()).filter(s -> s.getSustainValue() == finalStatValue)
                .findFirst().orElse(null);

    }

    /**
     * The object flag index of the sustain, the number {@code sustain_flag()} would have returned
     * for the stat ({@code obj-properties.c}).
     *
     * <p>Method getSustainValue coded on 261004, commented in full on 261004.
     *
     * @return 1 to 5 for a real sustain, 0 for {@code SUS_STAT_NONE}, 6 for {@code SUS_STAT_MAX}
     */
    public int getSustainValue() {
        return sustainValue;
    }
}
