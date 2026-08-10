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

import uk.co.jackoftrades.channel.enums.ProjectionEnum;

/**
 * {@link GameEventData} payload describing one frame of a bolt travelling between two grids —
 * the port of C's {@code game_event_data.bolt} ({@code src/game-event.h}), signalled by
 * {@code event_signal_bolt}. Sent repeatedly as the projectile advances; each message is one
 * step, not the whole flight.
 *
 * <p>The display layer animates it: C's {@code bolt_pict} ({@code ui-display.c}) derives the
 * direction of travel from the two grids, picks the character from {@code "*|/-\\"} accordingly,
 * and takes the colour from the projection. Nothing here says where on the screen any of that
 * goes — the grids are map coordinates, and translating them into a screen position is the UI's
 * business.
 *
 * @param projectionType what is being thrown, as C's {@code PROJ_*} value; selects the colour the
 *                       display draws it in
 * @param drawing        whether the bolt is currently being drawn, so the display knows to paint
 *                       this step rather than erase it
 * @param seen           whether the player can see it — an unseen bolt still travels, and still
 *                       reports each step, but must not be drawn
 * @param beam           whether this bolt should be drawn as a beam {@code true} or not
 * @param origin         the grid the bolt is moving from
 * @param current        the grid it has reached
 * @author Rowan Crowther
 */
public record EventDataBolt(ProjectionEnum projectionType, boolean drawing, boolean seen, boolean beam,
                            EventDataGrid origin,
                            EventDataGrid current) implements GameEventData {
}
