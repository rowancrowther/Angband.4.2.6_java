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

package uk.co.jackoftradesltd.channel.messages;

import uk.co.jackoftradesltd.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftradesltd.channel.enums.GameEventType;

/**
 * Everything the core can say to the front end.
 * <p>
 * The members are <strong>one record per payload shape, not one per occasion</strong>. The
 * tempting alternative — a record for the splash screen, one for a progress line, one for
 * redrawing a character — grows without bound and was rejected for that reason: the game has
 * barely started before there are a dozen of them. What actually varies is the <em>data</em> a
 * message carries, and that set is small and closed, because C enumerated it years ago in
 * {@code game-event.h}. So the occasion travels in {@link GameEventType} and only the shape gets
 * a record.
 * <p>
 * What is <em>not</em> here is as deliberate: no message says "draw this at row 5 in red". Those
 * are C's {@code term} hooks, and in C they are called by the front end, never by the game — the
 * core signals {@code EVENT_HP} and the front end decides where the health bar lives. Putting
 * drawing primitives on the channel would hand the core a screen layout, which is the coupling
 * this whole arrangement exists to remove. <b>What crosses a channel is the event, not the
 * drawing.</b>
 * <p>
 * Sealed, so a {@code switch} in the front end covering all three members needs no {@code
 * default}, and adding a fourth breaks the build at every place that must learn about it. That is
 * the intended way to find them.
 *
 * @see UIMessage the traffic going the other way
 */
public sealed interface CoreMessage extends ChannelMessage permits CoreMessage.SimpleCoreMessage,
        CoreMessage.TextCoreMessage, CoreMessage.LifecycleCoreMessage {

    /**
     * A game event with no data beyond the fact that it happened — the port of C's
     * {@code event_signal()}. Today's example is {@code EVENT_ENTER_INIT}.
     *
     * @param gameEventType which event occurred
     */
    record SimpleCoreMessage(GameEventType gameEventType) implements CoreMessage {
    }

    /**
     * A game event carrying one string, the port of C's {@code event_signal_message()} family.
     * One record serves {@code EVENT_INITSTATUS} and the birth notes alike, because the shape of
     * what they carry is identical and only the {@link GameEventType} differs — which is the
     * payload-shape rule in miniature.
     *
     * @param gameEventType which event occurred
     * @param message       the text it carries
     */
    record TextCoreMessage(GameEventType gameEventType, String message) implements CoreMessage {
    }

    /**
     * Protocol rather than gameplay: the core reporting on its own life, currently only that it
     * has stopped and the front end may now exit.
     * <p>
     * The meaning sits in the enum field rather than in the record's identity — one record with
     * {@link CoreLifecycleEvent#STOPPED} in it, not a {@code StoppedCoreMessage}. With a
     * single-constant enum the two are equivalent today; the field is the shape that stays right
     * as the enum grows.
     *
     * @param event what the core is reporting
     */
    record LifecycleCoreMessage(CoreLifecycleEvent event) implements CoreMessage {
    }
}
