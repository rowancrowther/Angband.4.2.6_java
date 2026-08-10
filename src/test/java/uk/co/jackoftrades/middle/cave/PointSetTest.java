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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PointSet}, the port of the C source's grid collection
 * ({@code src/z-set.c}).
 *
 * <p>Despite the name this is a bag rather than a set - it is backed by an
 * {@link java.util.ArrayList} and admits duplicates - so the tests pin that explicitly
 * rather than assuming set semantics from the type name. What it does provide is
 * <em>value</em> membership: {@link PointSet#contains} goes through {@link Loc#equals}, so a
 * grid put in as one instance is found again as an equal but distinct one. That is the
 * property callers actually depend on, since locations are freshly constructed on almost
 * every step of level generation.
 *
 * @author Rowan Crowther
 */
class PointSetTest {

    /**
     * The collection under test, empty at the start of every test.
     */
    private PointSet points;

    @BeforeEach
    void setUp() {
        points = new PointSet();
    }

    @Test
    void aFreshSetIsEmpty() {
        assertEquals(0, points.size());
        assertFalse(points.contains(Loc.row(1).col(1)));
    }

    @Test
    void anAddedGridIsFoundAgain() {
        points.add(Loc.row(3).col(5));

        assertTrue(points.contains(Loc.row(3).col(5)));
        assertEquals(1, points.size());
    }

    @Test
    void membershipIsByValueNotIdentity() {
        // The grid searched for is a different instance from the one stored - which is the
        // normal case, since level generation builds a fresh Loc at every step.
        points.add(Loc.row(3).col(5));

        assertTrue(points.contains(Loc.row(3).col(5)));
    }

    @Test
    void aGridThatWasNeverAddedIsNotFound() {
        points.add(Loc.row(3).col(5));

        assertFalse(points.contains(Loc.row(3).col(6)));
        assertFalse(points.contains(Loc.row(4).col(5)));
    }

    @Test
    void transposedCoordinatesAreADifferentGrid() {
        points.add(Loc.row(3).col(5));

        assertFalse(points.contains(Loc.row(5).col(3)));
    }

    @Test
    void severalGridsAreAllHeldAtOnce() {
        points.add(Loc.row(1).col(1));
        points.add(Loc.row(2).col(2));
        points.add(Loc.row(3).col(3));

        assertEquals(3, points.size());
        assertTrue(points.contains(Loc.row(1).col(1)));
        assertTrue(points.contains(Loc.row(2).col(2)));
        assertTrue(points.contains(Loc.row(3).col(3)));
    }

    @Test
    void addingTheSameGridTwiceStoresItTwice() {
        // A bag, not a set, despite the name - a caller counting members must dedupe first.
        points.add(Loc.row(3).col(5));
        points.add(Loc.row(3).col(5));

        assertEquals(2, points.size());
        assertTrue(points.contains(Loc.row(3).col(5)));
    }

    @Test
    void theOriginIsAnOrdinaryMember() {
        points.add(Loc.zero);

        assertTrue(points.contains(Loc.row(0).col(0)));
        assertEquals(1, points.size());
    }

    @Test
    void negativeCoordinatesAreHeldLikeAnyOther() {
        points.add(Loc.row(-2).col(-7));

        assertTrue(points.contains(Loc.row(-2).col(-7)));
        assertFalse(points.contains(Loc.row(2).col(7)));
    }
}
