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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocTest {

    @Test
    void testEquals() {
        Loc result = new Loc(1, 3);
        boolean Equals = result.equals(new Loc(1, 3));
        assertTrue(Equals);
    }

    @Test
    void isZero() {
        assertTrue(Loc.zero.isZero());
    }

    @Test
    void sum() {
        Loc a = new Loc(15, 21);
        Loc b = new Loc(-10, 7);

        Loc sum = a.sum(b);

        assertTrue(sum.equals(new Loc(5, 28)));
    }

    @Test
    void diff() {
        Loc a = new Loc(15, 21);
        Loc b = new Loc(-10, 7);

        Loc diff = a.diff(b);

        assertTrue(diff.equals(new Loc(25, 14)));
    }

    @Test
    void randLoc() {
        Loc start = new Loc(10, 15);
        Loc result = start.randLoc(10, 5);

        assertAll(
                () -> assertTrue(result.getX() < 20),
                () -> assertTrue(result.getX() >= 0),
                () -> assertTrue(result.getY() < 20),
                () -> assertTrue(result.getY() >= 10)
        );
    }

    @Test
    void locOffset() {
        Loc start = new Loc(10, 15);
        Loc expected = new Loc(13, 13);

        assertTrue(expected.equals(start.locOffset(3, -2)));
    }
}