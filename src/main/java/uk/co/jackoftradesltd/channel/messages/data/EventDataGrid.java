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

package uk.co.jackoftradesltd.channel.messages.data;

/**
 * {@link GameEventData} payload carrying one map square. The port of C's {@code struct loc}
 * where it appears in the {@code game_event_data} union ({@code src/game-event.h}) — but only
 * the coordinate pair, not the arithmetic: {@link uk.co.jackoftradesltd.middle.cave.Loc} keeps the
 * movement and randomisation helpers the core needs and the display never asks for.
 *
 * <p>Doubles as the component other payloads use wherever they carry positions, so an explosion's
 * blast grids and a bolt's endpoints are the same type as a bare point.
 *
 * <p><b>Named for rows and columns, not x and y.</b> Two bare {@code int}s let a transposed pair
 * compile in silence and surface much later as a mirrored map, a long way from the conversion that
 * caused it. {@code Loc}'s own constructor takes {@code (x, y)}, so anything translating between
 * the two crosses the order and wants a test that would fail on a swap — which a round-trip will
 * not, since it passes when both directions are wrong the same way.
 *
 * @param row the row, counting down the map — C's {@code y}
 * @param col the column, counting across the map — C's {@code x}
 * @author Rowan Crowther
 */
public record EventDataGrid(int row, int col) implements GameEventData {
}
