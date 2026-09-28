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

import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

/**
 * Payload for a redraw event that carries an array of {@link AngbandDisplayCharacter}s, one per
 * displayed cell — used today only by {@code EVENT_EQUIPMENT}, the equippy row
 * {@code HandlersHolder.prtEquippy} draws.
 *
 * <p>There is no {@code event_signal_*} counterpart for this shape in C: C's {@code prt_equippy}
 * ({@code [C] ui-display.c}) reads {@code player->body} and calls {@code object_attr}/
 * {@code object_char} on each slot's object directly at draw time. A handler on the far side of
 * the core-to-front-end boundary has no such global to read, so {@code PlayerCalcs.redrawStuff}'s
 * {@code PR_EQUIP} arm builds the whole glyph/colour array up front and sends it as this payload
 * instead; {@code RedrawRouter.setEquippy} is what unpacks it.
 *
 * <p>Record EventDataColourString coded on 260927, commented in full on 260928.
 *
 * @param string one {@link AngbandDisplayCharacter} per cell, in display order
 * @author Rowan Crowther
 */
public record EventDataColourString(AngbandDisplayCharacter[] string) implements GameEventData {
}
