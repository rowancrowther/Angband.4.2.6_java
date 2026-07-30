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

package uk.co.jackoftrades.frontend.stringoutput;

import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

/**
 * The engine's outbound message channel - the port of C's {@code msg}/{@code msgt} family. The
 * middle layer calls this to surface a line of text to the player without knowing how the front-end
 * displays it.
 *
 * <p>A stub for now: it formats the message but does not yet display it. Callers that pass
 * caller-controlled text should send it as a {@code "%s"} argument (as {@link
 * uk.co.jackoftrades.middle.game.gameengine.Command#getString} does) so a stray {@code %} is not
 * read as a format directive - mirroring C's {@code msg("%s", text)} idiom.
 *
 * @author Rowan Crowther
 */
public class Message {

    /**
     * Formats and sends a message to the player.
     *
     * @param message the message text, or a {@link String#format} pattern when {@code args} is given
     * @param args    optional format arguments substituted into {@code message}
     */
    public static void send(String message, Object... args) {
        String toSend = String.format(message, args);
        // TODO: Flesh out this stub
    }

    /**
     * Formats a message and announces it to the player — the port of C's {@code msg}
     * ({@code z-msg.c}), which is itself {@code msgt} with no sound attached.
     *
     * <p>The message is raised as an {@link GameEventType#EVENT_MESSAGE} carrying
     * {@link MessageType#MSG_GENERIC}, leaving it to the front-end to decide how it is shown. Use
     * {@link #messageType} instead when the message should be tagged with a specific type so the
     * front-end can colour it or play a sound.
     *
     * <p>Text that did not come from a literal here should be passed as a {@code "%s"} argument
     * rather than as the pattern itself, so a stray {@code %} in an object or monster name is not
     * read as a format directive.
     *
     * @param message the message text, or a {@link String#format} pattern when {@code args} is given
     * @param args    optional format arguments substituted into {@code message}
     * @author Rowan Crowther
     */
    public static void message(String message, Object... args) {
        String toSend = String.format(message, args);

        // Add to the message log TODO: Add in a message log

        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_MESSAGE, MessageType.MSG_GENERIC, toSend);
    }

    /**
     * Formats a message and announces it under a specific message type — the port of C's
     * {@code msgt} ({@code z-msg.c}).
     *
     * <p>Identical to {@link #message} except that the type travels with the message, letting the
     * front-end colour it by category and play the matching sound.
     *
     * @param messageType the category to tag the message with
     * @param message     the message text, or a {@link String#format} pattern when {@code args} is given
     * @param args        optional format arguments substituted into {@code message}
     * @author Rowan Crowther
     */
    public static void messageType(MessageType messageType, String message, Object... args) {
        String toSend = String.format(message, args);

        // Add to the message log TODO: Add in a message log

        // Add in a sound function
        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_MESSAGE, messageType, toSend);
    }
}
