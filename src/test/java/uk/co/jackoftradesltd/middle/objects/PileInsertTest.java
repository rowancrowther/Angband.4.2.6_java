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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Pile#insert(ItemObject)}.
 *
 * <p>Ports C's {@code pile_insert} ({@code obj-pile.c}). C makes the newest object the pile's
 * head; this pile represents "newest" as the last (top) index instead, so {@code lastItem()} pops
 * are used below to read insertion order back out. C guards the precondition with
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
        assertSame(item, pile.lastItem());
        assertNull(item.getOwningPile());
        assertTrue(pile.isEmpty());
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

        assertSame(third, pile.lastItem());
        assertSame(second, pile.lastItem());
        assertSame(first, pile.lastItem());
        assertTrue(pile.isEmpty());
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
