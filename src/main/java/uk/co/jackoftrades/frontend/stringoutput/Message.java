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
        // TODO: Flesh out this stub method
    }
}
