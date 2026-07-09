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
 * Marker interface for the payload carried by a {@link uk.co.jackoftrades.middle.game.enums.GameEventType}
 * event. Concrete {@code EventData*} classes implement it to carry the specific
 * data a given event needs (a point, a message, an explosion, …). This is the
 * Java port of the C original's {@code game_event_data} union ({@code src/game-event.h});
 * the type hierarchy replaces the C union.
 *
 * @author Rowan Crowther
 */
public interface GameEventData {

}
