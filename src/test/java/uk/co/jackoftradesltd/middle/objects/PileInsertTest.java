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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Pile#insert(ItemObject)}.
 *
 * <p>Ports C's {@code pile_insert} ({@code obj-pile.c}). C makes the newest object the pile's
 * head; this pile represents "newest" as the last (top) index instead, so {@code reversed()}
 * (head first, as C walks {@code obj->next}) and {@code peekLastItem()} (the head) are used below
 * to read insertion order back out. C guards the precondition with
 * {@code obj->prev || obj->next}, which cannot detect an object that is the sole member of some
 * other list — a singleton has null prev and next either way, same as a fresh object. This port
 * tracks ownership directly via {@link ItemObject#getOwningPile()}, so
 * {@link #insertRejectsSoloMemberOfAnotherPile()} covers the case where the two checks diverge.
 *
 * @author Rowan Crowther
 */
class PileInsertTest {

    @Test
    void insertSetsOwningPileAndPlacesOnTop() {
        Pile pile = new Pile();
        ItemObject item = new ItemObject();

        pile.insert(item);

        assertSame(pile, item.getOwningPile());
        assertFalse(pile.isEmpty());
        assertTrue(pile.contains(item));
        assertEquals(1, pile.size());
        assertSame(item, pile.peekLastItem());
    }

    @Test
    void insertPlacesEachNewItemOnTopInLifoOrder() {
        Pile pile = new Pile();
        ItemObject first = new ItemObject();
        ItemObject second = new ItemObject();
        ItemObject third = new ItemObject();

        pile.insert(first);
        pile.insert(second);
        pile.insert(third);

        // C: each pile_insert links the new object in at the head, so head to tail is third, second, first.
        assertEquals(List.of(third, second, first), pile.reversed());
        assertSame(third, pile.peekLastItem());
        assertSame(first, pile.lastItem());
    }

    @Test
    void insertRejectsItemAlreadyOwnedByThisPile() {
        Pile pile = new Pile();
        ItemObject a = new ItemObject();
        ItemObject b = new ItemObject();
        pile.insert(a);
        pile.insert(b);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> pile.insert(b));

        assertTrue(thrown.getMessage().contains("Pile integrity failure"));
        assertTrue(pile.contains(a));
        assertTrue(pile.contains(b));
        assertSame(pile, b.getOwningPile());
    }

    @Test
    void insertRejectsSoloMemberOfAnotherPile() {
        Pile source = new Pile();
        Pile destination = new Pile();
        ItemObject solo = new ItemObject();
        source.insert(solo);

        assertThrows(RuntimeException.class, () -> destination.insert(solo));

        assertSame(source, solo.getOwningPile());
        assertFalse(destination.contains(solo));
    }
}
