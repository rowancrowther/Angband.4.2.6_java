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

// STUB ENUM - the numbers are random and need changing once the
// timed_grade struct is implemented
//
// TODO: Revisit once timed_grade is implemented

public enum Food {
    PY_FOOD_STARVING(20),
    PY_FOOD_FAINT(40),
    PY_FOOD_WEAK(30),
    PY_FOOD_HUNGRY(50),
    PY_FOOD_FULL(95),
    PY_FOOD_MAX(100);

    final int foodValue;

    Food(int foodValue) {
        this.foodValue = foodValue;
    }

    public int getFoodValue() {
        return foodValue;
    }
}
