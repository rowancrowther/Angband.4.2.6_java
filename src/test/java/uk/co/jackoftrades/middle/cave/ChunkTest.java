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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class ChunkTest {
    private Chunk cave;
    private static final Loc inBounds = new Loc(5, 5);
    private static final Loc outBounds = new Loc(-1, 20);
    private static final Loc squareAllSet = new Loc(8, 8);

    @BeforeAll
    static void setUpClass() {

    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void inBounds() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertTrue(cave.inBounds(inBounds)),
                () -> assertFalse(cave.inBounds(outBounds))
        );
    }

    @Test
    void squareIsGlow() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertTrue(cave.squareIsGlow(squareAllSet)),
                () -> assertFalse(cave.squareIsGlow(inBounds))
        );
    }

    @Test
    void squareIsSeen() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertTrue(cave.squareIsSeen(squareAllSet)),
                () -> assertFalse(cave.squareIsSeen(inBounds))
        );
    }

    @Test
    void squareIsTrap() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertTrue(cave.squareIsTrap(squareAllSet)),
                () -> assertFalse(cave.squareIsTrap(inBounds))
        );
    }

    @Test
    void getPileIterator() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        Iterator<ItemObject> it = cave.getPileIterator(squareAllSet);
        ArrayList<ItemObject> expected = new ArrayList<>();
        while (it.hasNext()) {
            expected.add(it.next());
        }

        assertFalse(expected.isEmpty());
    }

    @Test
    void squareIsPlayer() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertFalse(cave.squareIsPlayer(squareAllSet)),
                () -> assertTrue(cave.squareIsPlayer(inBounds))
        );
    }

    @Test
    void getSquare() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        Object fullSquare = cave.getSquare(squareAllSet);
        boolean result = fullSquare != null && fullSquare instanceof Square;

        assertTrue(result);
    }

    @Test
    void getMonster() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        Monster result1 = cave.getMonster(squareAllSet); // should be a new monster
        Monster result2 = cave.getMonster(inBounds); // Should be null

        assertAll(
                () -> assertTrue(result1 != null && result1.getMonsterRace() == null),
                () -> assertNull(result2)
        );
    }

    @Test
    void squareHasInfoFlag() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertTrue(cave.squareHasInfoFlag(squareAllSet, SquareEnum.SQUARE_CLOSE_PLAYER)),
                () -> assertTrue(cave.squareHasInfoFlag(squareAllSet, SquareEnum.SQUARE_PROJECT)),
                () -> assertFalse(cave.squareHasInfoFlag(inBounds, SquareEnum.SQUARE_CLOSE_PLAYER)),
                () -> assertFalse(cave.squareHasInfoFlag(inBounds, SquareEnum.SQUARE_PROJECT))
        );
    }

    @Test
    void squareIsLit() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertTrue(cave.squareIsLit(squareAllSet)),
                () -> assertFalse(cave.squareIsLit(inBounds))
        );
    }

    @Test
    void squareExciseObject() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        assertAll(
                () -> assertFalse(cave.squareIsPlayer(squareAllSet)),
                () -> assertTrue(cave.squareIsPlayer(inBounds))
        );
    }

    @Test
    void objectDelete() {
        Chunk cave = new Chunk("Town", 0, 0, 3, 3, 1,
                true, 10, 10, 3, 7, 7, 4, 2, 0);
        cave.setUpTest(squareAllSet, inBounds);

        Iterator<ItemObject> it = cave.getPileIterator(squareAllSet);

        ArrayList<ItemObject> oldItems = new ArrayList<>();

        while (it.hasNext()) {
            oldItems.add(it.next());
        }

        cave.objectDelete(null, oldItems.get(1));

        ArrayList<ItemObject> newItems = new ArrayList<>();

        Iterator<ItemObject> newIt = cave.getPileIterator(squareAllSet);

        while (newIt.hasNext()) {
            newItems.add(newIt.next());
        }

        int newSize = newItems.size();
        int oldSize = oldItems.size();

        assertAll(
                () -> assertTrue(newSize == oldSize - 1),
                () -> assertTrue(newItems.get(0).equals(oldItems.get(0))),
                () -> assertTrue(newItems.get(1).equals(oldItems.get(2)))
        );
    }

    @Test
    void delistObject() {
    }

    @Test
    void squareMemorize() {
    }

    @Test
    void squareSetKnownFeat() {
    }

    @Test
    void isKnown() {
    }

    @Test
    void getMonMax() {
    }
}