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

package uk.co.jackoftrades.middle.game.globals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Food}, the port of C's {@code PY_FOOD_*} globals — the nourishment thresholds the
 * {@code TMD_FOOD} counter is compared against.
 *
 * <p><b>Six constants would not normally be worth a test.</b> These are, because they are not really
 * constants: in C they are filled in during parsing from the {@code FOOD} grades of
 * {@code player_timed.txt}, each multiplied by {@code player:food-value}
 * ({@code player-timed.c:321-336}). The port states the products directly, which means the enum and
 * the data file are two copies of one thing with nothing keeping them in step. If the data file's
 * grades change, or the food-value constant does, nothing here fails to compile and nothing at
 * runtime complains — the character simply starves at the wrong moment.
 *
 * <p>So the tests pin the arithmetic that ties the two together: each value is the data file's
 * percentage times a hundred, the order is ascending, and the two ranges {@code calcBonuses} divides
 * by are non-zero. That last one is not pedantry — the surfeit scaling divides by
 * {@code MAX - FULL} and the shortfall scaling by {@code HUNGRY}, so a careless edit that made two
 * neighbouring grades equal would divide by zero on the next mouthful.
 *
 * <p>Class FoodTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class FoodTest {

    /**
     * The scale C applies to every {@code FOOD} grade — {@code constants.txt}'s food-value.
     */
    private static final int FOOD_SCALE = 100;

    /**
     * Each constant is the corresponding grade maximum from {@code player_timed.txt} — 1, 4, 8, 15,
     * 90, 100 — multiplied by the food-value scale. Written as the multiplication rather than as the
     * product so the derivation is visible in the failure message.
     */
    @Test
    @DisplayName("each threshold is its data-file grade times the food-value scale")
    void thresholdsMatchScaledGrades() {
        assertAll(
                () -> assertEquals(1 * FOOD_SCALE, Food.PY_FOOD_STARVING.getFoodValue()),
                () -> assertEquals(4 * FOOD_SCALE, Food.PY_FOOD_FAINT.getFoodValue()),
                () -> assertEquals(8 * FOOD_SCALE, Food.PY_FOOD_WEAK.getFoodValue()),
                () -> assertEquals(15 * FOOD_SCALE, Food.PY_FOOD_HUNGRY.getFoodValue()),
                () -> assertEquals(90 * FOOD_SCALE, Food.PY_FOOD_FULL.getFoodValue()),
                () -> assertEquals(100 * FOOD_SCALE, Food.PY_FOOD_MAX.getFoodValue()));
    }

    /**
     * The grades are bands of one counter, so each ceiling must sit above the last. Declaration
     * order is the ascending order, and code that walks the values relies on that — an inversion
     * would put a starving player in the "weak" band.
     */
    @Test
    @DisplayName("the thresholds ascend in declaration order")
    void thresholdsAscend() {
        Food[] values = Food.values();
        assertAll(() -> {
            for (int i = 1; i < values.length; i++) {
                assertTrue(values[i].getFoodValue() > values[i - 1].getFoodValue(),
                        values[i] + " should sit above " + values[i - 1]);
            }
        });
    }

    /**
     * The two spans {@code calcBonuses} scales by. Both are divisors there
     * ({@code player-calcs.c:2096, 2101}), so both have to be non-zero, and the surfeit span in
     * particular is a gap between two neighbouring grades that an edit could easily close.
     */
    @Test
    @DisplayName("the two ranges calcBonuses divides by are non-zero")
    void divisorRangesAreUsable() {
        int surfeitRange = Food.PY_FOOD_MAX.getFoodValue() - Food.PY_FOOD_FULL.getFoodValue();

        assertAll(
                () -> assertTrue(surfeitRange > 0, "MAX must sit above FULL"),
                () -> assertTrue(Food.PY_FOOD_HUNGRY.getFoodValue() > 0, "HUNGRY must be positive"));
    }
}
