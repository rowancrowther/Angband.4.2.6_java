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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pins the guard that closes the {@code cursesFactory} comparator observation: a {@link Curse} can no longer
 * be built with a {@code null} or empty name, so {@link ItemObject#CURSE_ORDER} never meets one.
 *
 * <p>C has no counterpart: its curses are slots in an array and carry no comparator.
 */
@DisplayName("Curse name guard")
class CurseNameGuardTest {

    private static Curse curse(String name, int index) {
        return new Curse(name, List.of(), null, List.of(), new Flag<>(ObjectFlag.class), "", index);
    }

    @Test
    @DisplayName("a null name is rejected")
    void nullNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> curse(null, 0));
    }

    @Test
    @DisplayName("an empty name is rejected")
    void emptyNameRejected() {
        assertThrows(IllegalArgumentException.class, () -> curse("", 0));
    }

    @Test
    @DisplayName("two curses on one index are ordered by name and both kept")
    void tiedIndexOrdersByName() {
        TreeMap<Curse, Integer> map = new TreeMap<>(ItemObject.CURSE_ORDER);
        map.put(curse("b", 0), 1);
        map.put(curse("a", 0), 2);
        assertEquals(List.of("a", "b"), map.keySet().stream().map(Curse::getName).toList());
    }

    @Test
    @DisplayName("a lower index sorts first whatever the names")
    void indexBeatsName() {
        TreeMap<Curse, Integer> map = new TreeMap<>(ItemObject.CURSE_ORDER);
        map.put(curse("a", 1), 1);
        map.put(curse("z", 0), 2);
        assertEquals(List.of("z", "a"), map.keySet().stream().map(Curse::getName).toList());
    }
}
