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

package uk.co.jackoftradesltd.frontend.screen.grid;

/**
 * A clickable rectangle on the screen, published alongside the {@link Frame} it was
 * drawn into. No C original: this record is new architecture, not a port -
 * {@code docs/UIPanelArchitecture.md} ("The return path") introduces it because a bare
 * {@link CellGrid} carries no meaning of its own; a click at a row and column means
 * nothing until something records what was drawn there.
 *
 * <p>{@code top}/{@code left}/{@code rows}/{@code cols} give the rectangle in the same
 * {@code [row][col]} addressing {@link CellGrid} and {@link Region} use. {@code event} is
 * the opaque payload this hotspot carries back across the boundary when hit - the UI
 * thread turns a raw click into this value before any {@code UIMessage} reaches the core,
 * per the design doc's "Send meaning, never coordinates".
 *
 * <p>Record Hotspot coded on 260909, commented in full on 260909.
 *
 * @param top   the hotspot's top row
 * @param left  the hotspot's left column
 * @param rows  the hotspot's height in rows
 * @param cols  the hotspot's width in columns
 * @param event the value returned when this hotspot is hit, interpreted by the UI thread
 */
public record Hotspot(int top, int left, int rows, int cols,
                      Object event) {
}