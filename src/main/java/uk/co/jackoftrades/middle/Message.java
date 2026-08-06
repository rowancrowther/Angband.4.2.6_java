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

package uk.co.jackoftrades.middle;

import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.PlayerOptionEnum;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The engine's outbound message channel - the port of C's {@code msg}/{@code msgt} family
 * ({@code src/message.c}). The middle layer calls this to surface a line of text to the player
 * without knowing how the front-end displays it.
 *
 * <p>Each call does two things, in C's order: it records the text in the recent-message log
 * (C's {@code message_add}) and then signals an {@link GameEventType#EVENT_MESSAGE} carrying the
 * text and its {@link MessageType}, leaving display entirely to whichever front-end has
 * registered a handler. Nothing here knows about screens, colours or sound.
 *
 * <p>Callers that pass caller-controlled text should send it as a {@code "%s"} argument (as
 * {@link uk.co.jackoftrades.middle.game.gameengine.Command#getString} does) so a stray {@code %}
 * is not read as a format directive - mirroring C's {@code msg("%s", text)} idiom, and the reason
 * C's own header warns never to hand a string read from a file straight to {@code msg}.
 *
 * @author Rowan Crowther
 */
public class Message {
    /**
     * How many messages the log keeps before the oldest is discarded - the port of C's
     * {@code messages->max}, set to the same 2048 in {@code messages_init} ({@code
     * src/message.c}).
     *
     * @author Rowan Crowther
     */
    private final static int queueSize = 2048;

    /**
     * The recent-message log, newest first: the port of the doubly-linked {@code message_t}
     * chain hanging off C's {@code messages} ({@code src/message.c}).
     *
     * <p>Held newest-at-the-head so that {@code peekFirst} is C's {@code messages->head} - the
     * entry {@link #messageType} compares against when deciding whether a message is a repeat -
     * and the tail is C's {@code messages->tail}, the oldest, which is what gets dropped when
     * the log is full. A {@link Deque} is the natural fit because both ends are worked: pushed
     * at the head, trimmed at the tail.
     *
     * <p>The {@code queueSize} passed to the constructor is only an initial-capacity hint;
     * {@link ArrayDeque} is unbounded, so the cap is enforced explicitly by the
     * {@code removeLast} in {@link #messageType}.
     *
     * @author Rowan Crowther
     */
    private static Deque<MessageT> messageLog = new ArrayDeque<>(queueSize);

    private Message() {
    }

    /**
     * Formats a message and announces it to the player — the port of C's {@code msg}
     * ({@code src/message.c}), which is {@code msgt} tagged {@code MSG_GENERIC} and with no sound
     * attached.
     *
     * <p>The message is logged and raised as an {@link GameEventType#EVENT_MESSAGE} carrying
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
        messageType(MessageType.MSG_GENERIC, message, args);
    }

    /**
     * Formats a message, records it in the log and announces it under a specific message type —
     * the port of C's {@code msgt} ({@code src/message.c}) together with the {@code message_add}
     * it calls.
     *
     * <p>Identical to {@link #message} except that the type travels with the message, letting the
     * front-end colour it by category and play the matching sound.
     *
     * <p><b>Repeats coalesce.</b> A message whose text and type both match the newest entry in the
     * log does not get an entry of its own; the existing entry's count is bumped instead, exactly
     * as C's {@code message_add} does when {@code messages->head} matches on both {@code str} and
     * {@code type}. Only the newest entry is ever compared, so the sequence A, B, A yields three
     * entries rather than folding the two As together - a burst of "You miss the orc." collapses,
     * but a message that merely recurred earlier does not.
     *
     * <p><b>Port divergence: the {@code (xN)} suffix.</b> When the newest entry has been seen more
     * than once, the text signalled to the front-end gains a {@code " (x3)"} suffix. C instead
     * signals the plain text every time and keeps the count in the log alone, for a UI that walks
     * the log and renders the repeat count itself. Decorating here keeps the count visible without
     * every front-end having to reach into the log, at the cost of the event text no longer being
     * byte-identical to C's. The <em>stored</em> text stays plain either way, so the count is never
     * parsed back out of it.
     *
     * @param messageType the category to tag the message with
     * @param message     the message text, or a {@link String#format} pattern when {@code args} is given
     * @param args        optional format arguments substituted into {@code message}
     * @author Rowan Crowther
     */
    public static void messageType(MessageType messageType, String message, Object... args) {
        String toSend = String.format(message, args);

        // C's message_add: a repeat of the newest entry bumps its count in place rather than
        // taking a slot of its own. peekFirst returning null covers the empty log, which is the
        // null half of C's "if (messages->head && ...)" guard.
        MessageT top = messageLog.peekFirst();

        if (top != null && top.getType() == messageType && top.getText().equals(toSend))
            top.incrementCount();
        else {
            // Only a genuinely new entry can push the log over its cap, so the oldest is dropped
            // here rather than before the comparison - a repeat arriving at full capacity must
            // not cost the log its oldest message.
            if (messageLog.size() >= queueSize) {
                messageLog.removeLast();
            }
            messageLog.addFirst(new MessageT(1, toSend, messageType));
        }

        // Re-read the head: after a coalesce it is the bumped entry, after an insert the new one.
        top = messageLog.peekFirst();
        MessageType type = top.getType();
        int count = top.getCount();
        String msg = top.getText();

        // The repeat count decorates the outgoing text only - the logged text stays plain.
        if (count > 1) {
            msg = msg + " (x" + count + ")";
        }

        // TODO: C's msgt plays the type's sound before signalling - sound(type) in src/message.c.
        //  Not wired up until the front-end has a sound module to hear it.
        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_MESSAGE, type, msg);
    }

    /**
     * Makes a noise without any accompanying text — the port of C's {@code sound} ({@code
     * src/message.c}). Front-end sound modules hook the {@link GameEventType#EVENT_SOUND} event to
     * play the matching audio.
     * <p>
     * Does nothing unless the player has the {@code use_sound} option switched on (C's {@code
     * OPT(player, use_sound)} guard); when enabled it signals an {@link GameEventType#EVENT_SOUND}
     * carrying the sound's {@link MessageType} and no message text.
     *
     * @param messageType the sound category to play
     * @param player      the player whose sound option gates the event
     * @author Rowan Crowther
     */
    public static void sound(MessageType messageType, Player player) {
        if (!player.getPlayerOptions().has(PlayerOptionEnum.OP_use_sound))
            return;

        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_SOUND, messageType, null);
    }

    /**
     * One entry in the message log - the port of C's {@code message_t} ({@code src/message.c}),
     * minus the {@code older}/{@code newer} pointers that {@link #messageLog} provides for free.
     *
     * <p>Text and count are kept apart, as in C: {@link #text} is the message exactly as it was
     * formatted and {@link #count} is how many times it has been seen in a row. Nothing writes
     * the count into the text, so nothing ever has to parse it back out.
     *
     * @author Rowan Crowther
     */
    private static class MessageT {
        /**
         * The formatted message text, stored plain - C's {@code message_t.str}.
         *
         * @author Rowan Crowther
         */
        private String text;

        /**
         * The category the message was raised under - C's {@code message_t.type}. Part of the
         * repeat test: the same words under a different type are a different message.
         *
         * @author Rowan Crowther
         */
        private MessageType type;

        /**
         * How many times this message has been raised consecutively - C's
         * {@code message_t.count}, which starts at 1 for a message seen once.
         *
         * @author Rowan Crowther
         */
        private int count;

        /**
         * Build a log entry.
         *
         * @param count the number of consecutive occurrences so far, 1 for a first sighting
         * @param text  the formatted message text, without any repeat-count decoration
         * @param type  the category the message was raised under
         * @author Rowan Crowther
         */
        public MessageT(int count, String text, MessageType type) {
            this.count = count;
            this.text = text;
            this.type = type;
        }

        /**
         * Records one more consecutive sighting of this message. C guards the equivalent
         * increment against wrapping its 16-bit counter; a Java {@code int} has room to spare.
         *
         * @author Rowan Crowther
         */
        public void incrementCount() {
            count++;
        }

        /**
         * @return how many times this message has been raised consecutively
         * @author Rowan Crowther
         */
        public int getCount() {
            return count;
        }

        /**
         * @return the message text, without repeat-count decoration
         * @author Rowan Crowther
         */
        public String getText() {
            return text;
        }

        /**
         * @return the category the message was raised under
         * @author Rowan Crowther
         */
        public MessageType getType() {
            return type;
        }
    }
}
