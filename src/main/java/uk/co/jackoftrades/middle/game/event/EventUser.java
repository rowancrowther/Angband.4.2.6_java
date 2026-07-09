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

package uk.co.jackoftrades.middle.game.event;

/**
 * Marker interface for an opaque "user"/context object passed through to a
 * {@link EventHandlerInterface} when an event fires — letting a handler recover
 * the state it registered with. This is the Java port of the {@code void *user}
 * argument carried by the C original's event handlers.
 *
 * @author Rowan Crowther
 */
public interface EventUser {
}
