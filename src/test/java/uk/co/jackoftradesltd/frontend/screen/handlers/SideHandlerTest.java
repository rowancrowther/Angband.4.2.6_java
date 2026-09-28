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

package uk.co.jackoftradesltd.frontend.screen.handlers;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.GameEventType;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SideHandler} holding exactly what its constructor was given and invoking its hook - the
 * Java stand-in for C's anonymous {@code side_handler_t} struct literal ({@code [C]
 * ui-display.c}), which has no behaviour of its own beyond storing the three values a
 * {@code side_handlers[]} row is initialised with and calling through the function pointer.
 *
 * <p>Class SideHandlerTest coded on 260927, commented in full on 260927.
 *
 * @author Rowan Crowther
 */
class SideHandlerTest {

    /**
     * The priority and type given to the constructor are read back unchanged.
     */
    @Test
    void priorityAndTypeRoundTrip() {
        SideHandler handler = new SideHandler((x, y) -> {
        }, 19, GameEventType.EVENT_RACE_CLASS);

        assertEquals(19, handler.getPriority());
        assertEquals(GameEventType.EVENT_RACE_CLASS, handler.getType());
    }

    /**
     * {@code getResult} calls through to the wrapped hook with the given column and row, in
     * that order - there is no C {@code side_handlers[]} dispatch to compare this against, since
     * C's hooks are {@code void} and so is the wrapped hook here.
     */
    @Test
    void getResultInvokesTheHookWithTheGivenCoordinates() {
        int[] seenX = new int[1];
        int[] seenY = new int[1];
        SideHandler handler = new SideHandler((x, y) -> {
            seenX[0] = x;
            seenY[0] = y;
        }, 1, GameEventType.EVENT_RACE_CLASS);

        handler.getResult(7, 3);

        assertEquals(7, seenX[0]);
        assertEquals(3, seenY[0]);
    }
}
