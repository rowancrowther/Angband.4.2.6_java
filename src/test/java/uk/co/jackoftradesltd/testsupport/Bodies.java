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

package uk.co.jackoftradesltd.testsupport;

import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.PlayerBody;

import java.util.List;

/**
 * Builds a {@link PlayerBody} in one expression, for tests.
 *
 * <p>A body is built in two steps, the name by the constructor and the slots by
 * {@link PlayerBody#setSlots}, and a test that wants a body as an argument has no place to put the
 * second step. This does both, so the refusal of a null or empty slot list is still
 * {@code setSlots}'s own {@link IllegalArgumentException}.
 *
 * <p>Class Bodies coded on 261017, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public final class Bodies {

    private Bodies() {
    }

    /**
     * Builds a body with the given name and slots.
     *
     * @param name  the body's name
     * @param slots the body's slots, which {@link PlayerBody#setSlots} refuses if null or empty
     * @return the body
     * @throws IllegalArgumentException if the slot list is null or empty
     */
    public static PlayerBody of(String name, List<EquipSlot> slots) {
        PlayerBody body = new PlayerBody(name);
        body.setSlots(slots);
        return body;
    }
}
