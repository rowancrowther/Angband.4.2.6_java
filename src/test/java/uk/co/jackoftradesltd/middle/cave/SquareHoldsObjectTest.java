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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Square#holdsObject}, the port of C's {@code square_holds_object}
 * ({@code cave-square.c}).
 *
 * <p><b>What is worth pinning here is the identity test, not the containment.</b> That a pile
 * reports what was put in it is {@link uk.co.jackoftradesltd.middle.objects.Pile}'s business. What this
 * method promises on top is that two objects which are alike in every respect are still told apart —
 * without that, a known object could be judged to be on a pile it was never attached to, and
 * {@link PlayerKnowledge#knowObject} could report an object as being under
 * the player's feet when a duplicate of it was.
 *
 * <p>Class SquareHoldsObjectTest coded on 260816, commented in full on 260816.
 *
 * @author Rowan Crowther
 */
class SquareHoldsObjectTest {

    private Square square;

    /**
     * A square with no feature and no light. Nothing here reads either — the pile is created by the
     * constructor regardless, and that is all this method touches.
     */
    @BeforeEach
    void setUp() {
        square = new Square(null, 0, 0);
    }

    @Test
    @DisplayName("an object on the square is held")
    void objectOnSquareIsHeld() {
        ItemObject item = new ItemObject();
        square.getObjectPile().insert(item);

        assertTrue(square.holdsObject(item));
    }

    @Test
    @DisplayName("an object that was never placed is not held")
    void absentObjectIsNotHeld() {
        square.getObjectPile().insert(new ItemObject());

        assertFalse(square.holdsObject(new ItemObject()));
    }

    @Test
    @DisplayName("an empty square holds nothing")
    void emptySquareHoldsNothing() {
        assertFalse(square.holdsObject(new ItemObject()));
    }

    /**
     * The case the method exists for. Two freshly built {@link ItemObject}s are indistinguishable by
     * their fields, and a pile of Flasks of Oil on a dungeon floor is exactly that situation. The
     * test would pass on an equality-based implementation only by accident of {@link ItemObject} not
     * overriding {@code equals}; asserting it here is what stops that becoming true silently.
     */
    @Test
    @DisplayName("an identical object that is not this one is not held")
    void identicalButDistinctObjectIsNotHeld() {
        ItemObject onFloor = new ItemObject();
        ItemObject itsTwin = new ItemObject();
        square.getObjectPile().insert(onFloor);

        assertTrue(square.holdsObject(onFloor));
        assertFalse(square.holdsObject(itsTwin));
    }

    /**
     * Every object in a pile is found, not just the head of it. C walks {@code obj->next} from
     * {@code square_object(c, grid)}, so a pile deep enough to have a middle is worth one case.
     */
    @Test
    @DisplayName("every object in a deep pile is held")
    void allObjectsInAPileAreHeld() {
        ItemObject first = new ItemObject();
        ItemObject second = new ItemObject();
        ItemObject third = new ItemObject();
        square.getObjectPile().insert(first);
        square.getObjectPile().insert(second);
        square.getObjectPile().insert(third);

        assertTrue(square.holdsObject(first));
        assertTrue(square.holdsObject(second));
        assertTrue(square.holdsObject(third));
    }

    /**
     * An object taken off the floor is no longer on it. This is the transition the method is
     * consulted across — C uses it to decide whether a known object needs detaching from a stale
     * pile — so the "no" side needs to be reachable, not just initially true.
     */
    @Test
    @DisplayName("an excised object is no longer held")
    void excisedObjectIsNotHeld() {
        ItemObject item = new ItemObject();
        square.getObjectPile().insert(item);
        square.getObjectPile().excise(item);

        assertFalse(square.holdsObject(item));
    }
}
