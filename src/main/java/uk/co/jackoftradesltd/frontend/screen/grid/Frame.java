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

import java.util.List;

/**
 * An immutable snapshot of the screen, handed from the UI thread to the event dispatch thread -
 * the boundary crossing this drawing architecture is built around, set out in
 * {@code docs/UIPanelArchitecture.md}. No C original: this class is new architecture, not a
 * port. It carries both what to paint ({@link #grid}) and what the painted thing means
 * ({@link #hotspots}), so the frame that gets hit-tested against a click is always the frame
 * that was actually painted.
 *
 * <p>{@link Screen#frame()} is the only route to one, and it is the point at which the
 * invariant this type exists to support is enforced: {@code grid} is a defensive copy of the UI
 * thread's live grid, and {@code hotspots} is wrapped in {@link List#copyOf}, so once a
 * {@code Frame} is constructed neither component can be mutated by anything the UI thread still
 * holds a reference to. This record does not repeat that copying in its own canonical
 * constructor - it trusts the caller, today only {@link Screen#frame()}, to have already made
 * both arguments safe to publish.
 *
 * <p><b>Record equality is shallower than it looks.</b> {@link CellGrid} declares no
 * {@code equals} or {@code hashCode} of its own, so the generated {@code equals} this record
 * inherits compares {@code grid} by reference, not by cell contents - two frames over
 * structurally identical but distinct {@code CellGrid} instances are unequal. {@code hotspots}
 * compares by value, since {@link List#copyOf} returns a list whose {@code equals} does.
 *
 * <p>Record Frame coded on 260909, commented in full on 260909.
 *
 * @param grid     the screen contents at the moment this frame was taken
 * @param hotspots the clickable regions live on {@code grid}, published alongside it so a click
 *                 is always interpreted against the same frame that was painted
 */
public record Frame(CellGrid grid, List<Hotspot> hotspots) {
}