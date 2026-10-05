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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#getCurses()}, the port of reading C's {@code obj->curses}.
 *
 * <p>C has no accessor function: {@code struct object} holds {@code struct curse_data *curses}, a
 * pointer to an array with one slot per curse, or {@code NULL} for an object with no curses. The
 * expected values here come from how C code uses that pointer. {@code curses_are_equal} in
 * {@code obj-curse.c} reads a {@code NULL} array as "no curses"; the loops in {@code game-world.c}
 * ({@code process_world_aux}) and {@code obj-knowledge.c} walk the array from slot 0 up and
 * decrement {@code curse[j].timeout} in place through the pointer; and the array is the object's own, so nothing outside
 * the object can resize it.
 *
 * <p>Class ItemObjectGetCursesTest coded on 261003, commented in full on 261003.
 *
 * @author Rowan Crowther
 */
@DisplayName("ItemObject.getCurses")
class ItemObjectGetCursesTest {

    private ItemObject item;
    private Curse first;
    private Curse second;
    private Curse third;

    /**
     * A minimal curse with the given registry index. The index is the last constructor argument and
     * is what {@link ItemObject#CURSE_ORDER} sorts on, standing in for the slot number in C's array.
     */
    private static Curse curse(String name, int index) {
        return new Curse(name, List.of(), new ItemObject(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "", index);
    }

    /**
     * Forces the backing field to null, the state C's {@code NULL} array maps to. Nothing in the
     * public surface produces it, so the guard in {@code getCurses} can only be reached this way.
     */
    private static void nullBackingMap(ItemObject target) throws ReflectiveOperationException {
        Field field = ItemObject.class.getDeclaredField("curses");
        field.setAccessible(true);
        field.set(target, null);
    }

    @BeforeEach
    void setUp() {
        item = new ItemObject();
        first = curse("first", 1);
        second = curse("second", 3);
        third = curse("third", 5);
    }

    @Test
    @DisplayName("an item with no curses answers an empty map, as C reads a NULL array as no curses")
    void freshItemIsEmpty() {
        assertAll(
                () -> assertNotNull(item.getCurses()),
                () -> assertTrue(item.getCurses().isEmpty()),
                () -> assertEquals(0, item.getCurses().size()));
    }

    @Test
    @DisplayName("an item wiped of its curses answers an empty map again")
    void wipedItemIsEmpty() {
        item.addCurse(first, 1, 1);

        item.wipe();

        assertTrue(item.getCurses().isEmpty());
    }

    @Test
    @DisplayName("a null backing field is absorbed into an empty, unmodifiable map")
    void nullBackingFieldIsAbsorbed() throws ReflectiveOperationException {
        nullBackingMap(item);

        Map<Curse, CurseData> view = item.getCurses();

        assertAll(
                () -> assertNotNull(view),
                () -> assertTrue(view.isEmpty()),
                () -> assertFalse(view.containsKey(first)),
                () -> assertThrows(UnsupportedOperationException.class, () -> view.put(first, new CurseData(1, 1))));
    }

    @Test
    @DisplayName("a null backing field is rebuilt on demand by the editing mutators")
    void nullBackingFieldIsRebuilt() throws ReflectiveOperationException {
        nullBackingMap(item);

        item.addCurse(first, 4, 9);

        assertAll(
                () -> assertEquals(1, item.getCurses().size()),
                () -> assertEquals(4, item.getCurses().get(first).getPower()));
    }

    @Test
    @DisplayName("curses walk in ascending index however they were added, as C's loop over the array does")
    void walksInIndexOrder() {
        item.addCurse(third, 1, 1);
        item.addCurse(first, 1, 1);
        item.addCurse(second, 1, 1);

        assertEquals(List.of(first, second, third), new ArrayList<>(item.getCurses().keySet()));
    }

    @Test
    @DisplayName("index order holds after a replace through setCurses and clearAndPutCurses")
    void indexOrderSurvivesReplace() {
        Map<Curse, CurseData> given = new java.util.LinkedHashMap<>();
        given.put(third, new CurseData(1, 1));
        given.put(first, new CurseData(1, 1));

        item.setCurses(given);
        assertEquals(List.of(first, third), new ArrayList<>(item.getCurses().keySet()));

        item.clearAndPutCurses(given);
        assertEquals(List.of(first, third), new ArrayList<>(item.getCurses().keySet()));
    }

    @Test
    @DisplayName("index order holds on a copy of the item")
    void indexOrderSurvivesCopy() {
        item.addCurse(third, 1, 1);
        item.addCurse(first, 1, 1);

        ItemObject copy = item.copy(false);

        assertEquals(List.of(first, third), new ArrayList<>(copy.getCurses().keySet()));
    }

    @Test
    @DisplayName("the map is an unmodifiable view: put, remove and clear all throw")
    void viewIsUnmodifiable() {
        item.addCurse(first, 2, 2);
        Map<Curse, CurseData> view = item.getCurses();

        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> view.put(second, new CurseData(1, 1))),
                () -> assertThrows(UnsupportedOperationException.class, () -> view.remove(first)),
                () -> assertThrows(UnsupportedOperationException.class, view::clear),
                () -> assertThrows(UnsupportedOperationException.class, () -> view.keySet().remove(first)),
                () -> assertEquals(1, item.getCurses().size()));
    }

    @Test
    @DisplayName("the view is live: a curse added after the call shows up in the view already held")
    void viewTracksLaterAdds() {
        Map<Curse, CurseData> view = item.getCurses();
        assertTrue(view.isEmpty());

        item.addCurse(first, 3, 6);

        assertAll(
                () -> assertEquals(1, view.size()),
                () -> assertEquals(3, view.get(first).getPower()));
    }

    @Test
    @DisplayName("the values are the live data, so a timeout ticked in place is seen on the next read")
    void valuesAreLive() {
        CurseData data = new CurseData(2, 5);
        item.addCurse(first, data);

        CurseData viaView = item.getCurses().get(first);
        viaView.decrementTimeout();

        assertAll(
                () -> assertSame(data, viaView),
                () -> assertEquals(4, item.getCurses().get(first).getTimeout()),
                () -> assertEquals(2, item.getCurses().get(first).getPower()));
    }

    @Test
    @DisplayName("a curse that was never added is absent, which C reads as power zero")
    void absentCurseIsAbsent() {
        item.addCurse(first, 1, 1);

        assertAll(
                () -> assertFalse(item.getCurses().containsKey(second)),
                () -> assertEquals(null, item.getCurses().get(second)));
    }

    @Test
    @DisplayName("an entry stored at power zero is reported, not hidden: the port keeps what it is handed")
    void zeroPowerEntryIsReported() {
        item.addCurse(first, 0, 0);

        assertAll(
                () -> assertEquals(1, item.getCurses().size()),
                () -> assertEquals(0, item.getCurses().get(first).getPower()));
    }

    @Test
    @DisplayName("a lookup by a null curse throws on a non-empty map and answers false on an empty one")
    void nullKeyLookup() {
        assertFalse(item.getCurses().containsKey(null));

        item.addCurse(first, 1, 1);

        assertThrows(NullPointerException.class, () -> item.getCurses().containsKey(null));
    }

    @Test
    @DisplayName("the map handed out by one item is not shared with another")
    void notSharedBetweenItems() {
        ItemObject other = new ItemObject();
        item.addCurse(first, 1, 1);

        assertAll(
                () -> assertEquals(1, item.getCurses().size()),
                () -> assertTrue(other.getCurses().isEmpty()));
    }
}
