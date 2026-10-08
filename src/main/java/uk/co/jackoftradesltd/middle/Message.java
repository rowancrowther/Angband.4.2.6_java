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

package uk.co.jackoftradesltd.middle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IllegalFormatException;

/**
 * The engine's outbound message channel - the port of C's {@code msg}/{@code msgt} family
 * ({@code src/message.c}). The middle layer calls this to surface a line of text to the player
 * without knowing how the front-end displays it.
 *
 * <p>Each call, in C's order, cuts the formatted text to fit C's 1024-byte buffer (1023
 * characters), records it in the recent-message log (C's {@code message_add}), and then signals
 * an {@link GameEventType#EVENT_MESSAGE} carrying the text and its {@link MessageType}, leaving
 * display entirely to whichever front-end has registered a handler. {@link #messageType} also
 * raises an {@link GameEventType#EVENT_SOUND} between the log write and the message event, as
 * C's {@code msgt} does; {@link #message} does not, as C's {@code msg} does not. Nothing here
 * knows about screens or colours, and the audio itself is left to a front-end sound module.
 *
 * <p>The text signalled is always the plain text. The repeat count of consecutive identical
 * messages is kept in the log alone, as in C, for a front-end that walks the log to render.
 *
 * <p>Callers that pass caller-controlled text should send it as a {@code "%s"} argument (as
 * {@link uk.co.jackoftradesltd.middle.game.gameengine.Command#getString} does) so a stray {@code %}
 * is not read as a format directive - mirroring C's {@code msg("%s", text)} idiom, and the reason
 * the doc comment on C's {@code msg} warns never to hand a string read from a file straight to it.
 *
 * <p><b>ASCII only.</b> All message text is assumed to be ASCII. C cuts at 1023 <em>bytes</em>
 * and Java cuts at 1023 <em>characters</em>; the two agree only for ASCII, so the cut here is
 * exact for ASCII text and unspecified for anything else (multi-byte text would keep more
 * characters than C does, and a supplementary character could be split).
 *
 * <p><b>Accepted format vocabulary.</b> C formats with {@code vstrnfmt} ({@code src/z-form.c}),
 * this port with {@link String#format}. The two share a core, and only that core is accepted:
 * <ul>
 *   <li>{@code %%} - a literal percent sign.</li>
 *   <li>{@code %s} - a string; {@code %c} - a character; {@code %d} - a decimal integer.</li>
 *   <li>{@code %x}, {@code %X}, {@code %o} - hexadecimal and octal integers.</li>
 *   <li>{@code %f}, {@code %e}, {@code %E} - floating point.</li>
 *   <li>The {@code -}, {@code +} and {@code 0} flags, a width and a {@code .precision}. C
 *       documents {@code +} and {@code 0} as unsafe with {@code %s} and {@code %c}; Java
 *       throws for them.</li>
 * </ul>
 * Everything else is out of vocabulary. Some of it fails and some of it silently gives
 * different text, and the silent cases are the dangerous ones:
 * <ul>
 *   <li>Valid in C, an error in Java: {@code %i}, {@code %u}, the {@code l} modifier
 *       ({@code %ld}, {@code %lu}), a {@code *} width or precision, and {@code %p}.</li>
 *   <li>Valid in both with different meanings: {@code %n} (C stores the length written so far
 *       and takes a pointer; Java emits a line separator and takes no argument), and
 *       {@code %g} (the two differ over trailing zeros and when the exponent form is chosen).</li>
 *   <li>A {@code null} argument to {@code %s}: C writes the empty string, Java writes
 *       {@code "null"}. Callers must not pass one.</li>
 *   <li>Java-only conversions ({@code %b}, {@code %h}, {@code %t}, {@code %S}, the {@code ,}
 *       flag) are errors in C and must not be used here.</li>
 * </ul>
 *
 * <p><b>Malformed patterns become an empty message, as in C.</b> When {@code vstrnfmt} meets an
 * illegal conversion or an unterminated {@code %} it empties the buffer, and C's {@code msg}
 * and {@code msgt} carry on regardless: an empty string is logged and an empty
 * {@link GameEventType#EVENT_MESSAGE} is signalled (with the sound, for {@code msgt}). Here the
 * {@link IllegalFormatException} from {@link String#format} is caught and the text is set to
 * {@code ""}, after which the call follows the ordinary path, so the log, the sound and the event
 * all see the empty message exactly as C's do. The one addition is a warning written to the
 * logger, naming the offending pattern, which C does not do. A {@code null} pattern is not an
 * {@link IllegalFormatException} and is not caught.
 *
 * <p>Class Message commented in full on 260929.
 *
 * @author Rowan Crowther
 */
public class Message {
    /**
     * Where a malformed format pattern is reported. C's {@code vstrnfmt} fails silently, so this
     * has no C counterpart; it exists so that a bad pattern, which becomes a blank message, can
     * still be traced to its source.
     */
    private static final Logger logger = LogManager.getLogger(Message.class);
    
    /**
     * How many messages the log keeps before the oldest is discarded - the port of C's
     * {@code messages->max}, set to the same 2048 in {@code messages_init} ({@code
     * src/message.c}).
     */
    private final static int queueSize = 2048;

    /**
     * The recent-message log, newest first: the port of the doubly-linked {@code message_t}
     * chain hanging off C's {@code messages} ({@code src/message.c}).
     *
     * <p>Held newest-at-the-head so that {@code peekFirst} is C's {@code messages->head} - the
     * entry {@link #messageAdd} compares against when deciding whether a message is a repeat -
     * and the tail is C's {@code messages->tail}, the oldest, which is what gets dropped when
     * the log is full. A {@link Deque} is the natural fit because both ends are worked: pushed
     * at the head, trimmed at the tail.
     *
     * <p>The {@code queueSize} passed to the constructor is only an initial-capacity hint;
     * {@link ArrayDeque} is unbounded, so the cap is enforced explicitly by the
     * {@code removeLast} in {@link #messageAdd}.
     */
    private static Deque<MessageT> messageLog = new ArrayDeque<>(queueSize);

    /**
     * Not instantiable: like the C {@code message.c} functions, everything here is static and
     * works on the one shared log.
     */
    private Message() {
    }

    /**
     * Formats a message and announces it to the player — the port of C's {@code msg}
     * ({@code src/message.c}), which is {@code msgt} tagged {@code MSG_GENERIC} and with no sound
     * attached.
     *
     * <p>The text is formatted, cut to 1023 characters (C formats into {@code char buf[1024]}),
     * logged with {@link MessageType#MSG_GENERIC} through {@link #messageAdd}, and raised as an
     * {@link GameEventType#EVENT_MESSAGE} carrying that type and the plain text, leaving it to the
     * front-end to decide how it is shown. Use {@link #messageType} instead when the message should
     * be tagged with a specific type so the front-end can colour it or play a sound.
     *
     * <p>Text that did not come from a literal here should be passed as a {@code "%s"} argument
     * rather than as the pattern itself, so a stray {@code %} in an object or monster name is not
     * read as a format directive.
     *
     * <p>A pattern {@link String#format} rejects is treated as C treats one {@code vstrnfmt}
     * rejects: the text becomes {@code ""} and the message is still logged and signalled, blank.
     * A warning is also written to the logger. See the class comment for the accepted format
     * vocabulary.
     *
     * <p><b>Port divergence:</b> C's {@code msg} returns silently while {@code messages} is still
     * {@code NULL}, before {@code messages_init} has run or after {@code messages_free}. This port
     * has no such guard: {@link #messageLog} is created with the class and never replaced, so
     * there is no "store not loaded" state to check for. It cannot bite in play, where the log
     * exists before any message is sent. See {@code docs/Accepted_Deviations.md}.
     *
     * <p>Method message commented in full on 260929.
     *
     * @param message the message text, or a {@link String#format} pattern when {@code args} is given
     * @param args    optional format arguments substituted into {@code message}
     */
    public static void message(String message, Object... args) {
        String toSend;
        try {
            toSend = String.format(message, args);
        } catch (IllegalFormatException e) {
            logger.warn("Exception while trying to send message: {}", message, e);
            toSend = "";
        }

        toSend = size(toSend, 1023);

        messageAdd(toSend, MessageType.MSG_GENERIC);

        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_MESSAGE,
                MessageType.MSG_GENERIC, toSend);
    }

    /**
     * Formats a message, records it in the log and announces it under a specific message type —
     * the port of C's {@code msgt} ({@code src/message.c}) together with the {@code message_add}
     * it calls.
     *
     * <p>Differs from {@link #message} in that the type travels with the message, and in the sound:
     * after the log write and before the {@link GameEventType#EVENT_MESSAGE} is signalled it calls
     * {@link #sound} for that type, in C's order. The sound is only heard if the current player has
     * {@code use_sound} on. The player is taken from {@link GameState#getPlayer()}, standing in for
     * C's {@code player} global.
     *
     * <p>The formatted text is cut to 1023 characters (C's {@code char buf[1024]}) <em>before</em> it
     * is logged, so the log and the signalled event both see the cut text, and two over-long lines
     * that differ only past the cut count as the same message.
     *
     * <p><b>Repeats coalesce.</b> A message whose text and type both match the newest entry in the
     * log does not get an entry of its own; the existing entry's count is bumped instead - see
     * {@link #messageAdd}. Only the newest entry is ever compared, so the sequence A, B, A yields
     * three entries rather than folding the two As together - a burst of "You miss the orc."
     * collapses, but a message that merely recurred earlier does not. Every call still signals: the
     * coalescing affects the log, not the traffic, and the signalled text is always the plain text,
     * as C's is.
     *
     * <p>A pattern {@link String#format} rejects is treated as C treats one {@code vstrnfmt}
     * rejects: the text becomes {@code ""}, and the empty message is logged, sounded and signalled
     * in the usual order. A warning is also written to the logger. See the class comment for the
     * accepted format vocabulary.
     *
     * <p><b>Port divergence:</b> C's {@code msgt} returns silently while {@code messages} is still
     * {@code NULL}, before {@code messages_init} has run or after {@code messages_free}, and so
     * makes no sound either. This port has no such guard: {@link #messageLog} is created with the
     * class and never replaced, so there is no "store not loaded" state to check for. It cannot
     * bite in play, where the log exists before any message is sent. See
     * {@code docs/Accepted_Deviations.md}.
     *
     * <p>Method messageType commented in full on 260929.
     *
     * @param messageType the category to tag the message with
     * @param message     the message text, or a {@link String#format} pattern when {@code args} is given
     * @param args        optional format arguments substituted into {@code message}
     */
    public static void messageType(MessageType messageType, String message, Object... args) {
        String toSend;
        try {
            toSend = String.format(message, args);
        } catch (IllegalFormatException e) {
            logger.warn("Exception while trying to send message: {}", message, e);
            toSend = "";
        }

        toSend = size(toSend, 1023);

        messageAdd(toSend, messageType);

        sound(messageType, GameState.getPlayer());
        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_MESSAGE, messageType, toSend);
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
     * <p><b>Port divergence:</b> C's {@code sound} reads {@code OPT(player, use_sound)} without
     * checking that {@code player} exists. This port treats a null player, or a player with no
     * option set, as "sound off" and returns quietly, so a message raised before a character exists
     * is still delivered.
     *
     * <p>Method sound commented in full on 260929.
     *
     * @param messageType the sound category to play
     * @param player      the player whose sound option gates the event, or {@code null} for none
     */
    public static void sound(MessageType messageType, Player player) {
        if (player == null || player.getPlayerOptions() == null
                || !player.getPlayerOptions().has(PlayerOptionEnum.OP_use_sound))
            return;

        GameEngine.getEventsBusHandler().eventSignalMessage(GameEventType.EVENT_SOUND, messageType, null);
    }

    /**
     * Records a message in the log without announcing it — the port of C's {@code message_add}
     * ({@code src/message.c}), the half of {@code msgt} that {@link #messageType} calls before it
     * signals the {@link GameEventType#EVENT_MESSAGE} event. {@link #message} and
     * {@link #messageType} both go through it. Callers that only need the log entry (nothing on the
     * boundary listening yet) can reach this directly.
     *
     * <p>Repeats coalesce: a message whose text and type both match the newest entry - C's
     * {@code messages->head} - bumps that entry's count in place rather than taking a slot of its
     * own. Nothing else in the log is compared. C additionally guards the increment against
     * wrapping its 16-bit counter ({@code count != (uint16_t)-1}), starting a fresh entry at 65535
     * instead; that guard is not ported here, as a Java {@code int} has room to spare, so a run of
     * more than 65535 identical messages stays one entry here where C would split it.
     *
     * <p>The log is capped at {@link #queueSize} entries, C's {@code messages->max}: once a
     * genuinely new entry would push the log over the cap, the oldest entry is dropped first. C
     * inserts and then trims; the end state is the same. A repeat never evicts anything.
     *
     * <p>Like C's {@code message_add}, this takes an already-formatted string and does no
     * formatting or truncation of its own; the 1023-character cut happens in {@link #message} and
     * {@link #messageType}.
     *
     * <p>Method messageAdd coded on 260908, commented in full on 260929.
     *
     * @param message the message text, stored exactly as given, without any repeat-count decoration
     * @param type    the category the message was raised under
     */
    public static void messageAdd(String message, MessageType type) {
        MessageT first = messageLog.peekFirst();
        if (first != null && first.type == type && first.getText().equals(message)) {
            first.incrementCount();
            return;
        }

        MessageT messageT = new MessageT(1, message, type);

        if (messageLog.size() >= queueSize) {
            messageLog.removeLast();
        }
        messageLog.offerFirst(messageT);
    }

    /**
     * Cuts text to at most {@code length} characters, standing in for the cut C gets from
     * formatting into a fixed {@code char buf[1024]}. Text already short enough is returned as it
     * is. The cut is by character, which matches C's by-byte cut only for ASCII text (see the
     * class comment).
     *
     * <p>Method size commented in full on 260929.
     *
     * @param text   the formatted text
     * @param length the most characters to keep (callers pass 1023)
     * @return {@code text}, or its first {@code length} characters
     */
    private static String size(String text, int length) {
        int size = Math.min(length, text.length());
        return text.substring(0, size);
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
         */
        private String text;

        /**
         * The category the message was raised under - C's {@code message_t.type}. Part of the
         * repeat test: the same words under a different type are a different message.
         */
        private MessageType type;

        /**
         * How many times this message has been raised consecutively - C's
         * {@code message_t.count}, which starts at 1 for a message seen once.
         */
        private int count;

        /**
         * Build a log entry.
         *
         * @param count the number of consecutive occurrences so far, 1 for a first sighting
         * @param text  the formatted message text, without any repeat-count decoration
         * @param type  the category the message was raised under
         */
        public MessageT(int count, String text, MessageType type) {
            this.count = count;
            this.text = text;
            this.type = type;
        }

        /**
         * Records one more consecutive sighting of this message. C guards the equivalent
         * increment against wrapping its 16-bit counter; a Java {@code int} has room to spare.
         */
        public void incrementCount() {
            count++;
        }

        /**
         * @return how many times this message has been raised consecutively
         */
        public int getCount() {
            return count;
        }

        /**
         * @return the message text, without repeat-count decoration
         */
        public String getText() {
            return text;
        }

        /**
         * @return the category the message was raised under
         */
        public MessageType getType() {
            return type;
        }
    }
}
