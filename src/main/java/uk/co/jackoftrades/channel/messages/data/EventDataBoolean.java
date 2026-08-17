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

package uk.co.jackoftrades.channel.messages.data;

import org.jetbrains.annotations.Contract;

/**
 * {@link GameEventData} payload carrying a single yes/no answer — the port of the {@code flag}
 * arm of C's {@code game_event_data} union ({@code src/game-event.h}). Used by events that report
 * an outcome rather than a value, such as whether level generation succeeded.
 *
 * <p>The flag means whatever its event means; the payload deliberately says nothing about which
 * question was asked, because the {@code GameEventType} accompanying it already does.
 *
 * @param value the answer this event is reporting
 * @author Rowan Crowther
 */
public record EventDataBoolean(boolean value) implements GameEventData {
    /**
     * Builds the payload for the negative case, which is common enough to be worth not spelling
     * out at every call site.
     */
    public EventDataBoolean() {
        this(false);
    }
}
