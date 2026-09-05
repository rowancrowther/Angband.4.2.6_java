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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerClass#getStartItems()}, the read of C's {@code class->start_items} chain
 * ({@code player.h}).
 *
 * <p>C walks the list head-to-tail with a {@code for (si = c->start_items; si; si = si->next)}
 * loop ({@code player-birth.c:607}); the port holds the same sequence as a {@link List}, so what
 * matters here is that the accessor preserves the order the class was built with and hands back
 * the live list C's shared pointer would equally expose, not a defensive copy.
 *
 * <p>Class PlayerClassGetStartItemsTest coded on 260904, commented in full on 260904.
 *
 * @author Rowan Crowther
 */
class PlayerClassGetStartItemsTest {

    private static StartItem item(String svalName) {
        return new StartItem(TValue.TV_FOOD, svalName, 1, 1, List.of());
    }

    /**
     * A class whose only interesting property is its starting equipment.
     *
     * @param startItems the class's starting equipment
     * @return the class
     */
    private static PlayerClass playerClass(List<StartItem> startItems) {
        return new PlayerClass("Warrior", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5,
                startItems, ClassMagic.NONE);
    }

    /**
     * The empty case: a non-caster with no starting kit still returns a list, matching C's
     * {@code NULL} head making the loop body run zero times rather than crashing.
     */
    @Test
    @DisplayName("an empty starting list is returned as an empty list, not null")
    void emptyListIsReturnedAsEmpty() {
        assertTrue(playerClass(List.of()).getStartItems().isEmpty());
    }

    /**
     * C's list is built by prepending ({@code si->next = c->start_items; c->start_items = si;} in
     * {@code parse_class_equip}), so the class.txt reading order is reversed by the time
     * {@code player_outfit} walks it. The port's constructor takes the list already in whatever
     * order the caller assembled it — this test pins that {@code getStartItems} preserves that
     * order rather than reversing or re-sorting it itself.
     */
    @Test
    @DisplayName("starting items are returned in the order the class was built with")
    void orderIsPreserved() {
        StartItem torch = item("Wooden Torch");
        StartItem ration = item("Ration of Food");

        List<StartItem> result = playerClass(List.of(torch, ration)).getStartItems();

        assertEquals(List.of(torch, ration), result);
    }

    /**
     * The same {@link List} instance is handed back, not a copy — consistent with C's direct field
     * read through {@code p->class->start_items}, which exposes the one list every reader shares.
     */
    @Test
    @DisplayName("the same List instance is returned, not a copy")
    void returnsSameInstance() {
        List<StartItem> startItems = List.of(item("Wooden Torch"));

        assertSame(startItems, playerClass(startItems).getStartItems());
    }
}
