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

import uk.co.jackoftrades.middle.enums.MessageType;

/**
 * {@link GameEventData} payload carrying a line of text for the player and the category it falls
 * under — the port of C's {@code game_event_data.message} ({@code src/game-event.h}), signalled
 * from {@code msgt} ({@code src/message.c}).
 *
 * <p>The type travels with the text because the display is what acts on it: C colours the message
 * by category and plays the category's sound. Deciding <em>which</em> category a message belongs
 * to is the core's judgement; deciding what that category looks and sounds like is not.
 *
 * <p>Repeated messages arrive already decorated. {@link uk.co.jackoftrades.middle.Message}
 * coalesces a run of identical messages in its log and appends the count to the text it sends, so
 * a payload may read {@code "You miss the orc. (x3)"} where C would have sent the plain line three
 * times and left the counting to the front end.
 *
 * @param type    the category the message falls under, which selects its colour and sound
 * @param message the text to show, already formatted and already carrying any repeat count
 * @author Rowan Crowther
 */
public record EventDataMessage(MessageType type, String message) implements GameEventData {
}
