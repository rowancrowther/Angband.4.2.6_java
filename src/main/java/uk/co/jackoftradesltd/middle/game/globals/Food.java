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

package uk.co.jackoftradesltd.middle.game.globals;

/**
 * The nourishment thresholds the game compares the {@code TMD_FOOD} counter against — the port of
 * C's {@code PY_FOOD_*} globals ({@code player-timed.c:36-41}).
 *
 * <p><b>These are not constants in C.</b> They are plain {@code int}s filled in during parsing:
 * each grade of the {@code FOOD} timed effect in {@code player_timed.txt} has its name matched
 * against a fixed list and its maximum copied into the matching global
 * ({@code player-timed.c:321-336}). The maxima in the data file are percentages — {@code 1 / 4 /
 * 8 / 15 / 90 / 100} — and every one is multiplied by {@code z_info->food_value} on the way in
 * ({@code player-timed.c:263, 322}), which {@code constants.txt} sets to 100. The values below are
 * that product, so a full stomach is 10000 rather than 100.
 *
 * <p>The port reaches the same numbers from the other end: the assembler applies the same scale
 * when it builds the {@code FOOD} grades, and this enum states the results. Two things follow. The
 * numbers here must stay in step with {@code player_timed.txt} and with
 * {@code player:food-value} — change either and these are wrong, with no compiler to say so. And
 * the constants are only meaningful against a {@code TMD_FOOD} counter on the same scale; comparing
 * them with an unscaled grade maximum silently mixes the two.
 *
 * <p>Order matters and is ascending. {@code calcBonuses} works out how far outside the "Fed" band
 * the player is by subtracting {@link #PY_FOOD_FULL} from the counter for a surfeit and the counter
 * from {@link #PY_FOOD_HUNGRY} for a shortfall, then scales each by the width of its range
 * ({@code player-calcs.c:2092-2130}).
 *
 * @author Rowan Crowther
 */
public enum Food {
    /**
     * The "Starving" grade's ceiling. Below this the player takes damage from hunger.
     */
    PY_FOOD_STARVING(100),
    /**
     * The "Faint" grade's ceiling — the band in which the player passes out at random.
     */
    PY_FOOD_FAINT(400),
    /**
     * The "Weak" grade's ceiling.
     */
    PY_FOOD_WEAK(800),
    /**
     * The "Hungry" grade's ceiling, and the point below which {@code calcBonuses} starts taking
     * to-hit, to-damage and skill away — the divisor for the shortfall scaling as well as its
     * origin.
     */
    PY_FOOD_HUNGRY(1500),
    /**
     * The "Fed" grade's ceiling: comfortably nourished, the state in which no food adjustment
     * applies at all. Anything above it is a surfeit that costs speed.
     */
    PY_FOOD_FULL(9000),
    /**
     * The "Full" grade's ceiling and the counter's maximum — bloated. The gap between this and
     * {@link #PY_FOOD_FULL} is the range the speed penalty is scaled over, so the two are only
     * meaningful as a pair.
     */
    PY_FOOD_MAX(10000);

    /** The counter value at which this grade gives way to the next. */
    final int foodValue;

    /**
     * Binds a nourishment grade to its ceiling.
     *
     * @param foodValue the already-scaled {@code TMD_FOOD} value this grade tops out at
     */
    Food(int foodValue) {
        this.foodValue = foodValue;
    }

    /**
     * @return this grade's ceiling, on the same scale as the player's {@code TMD_FOOD} counter
     */
    public int getFoodValue() {
        return foodValue;
    }
}
