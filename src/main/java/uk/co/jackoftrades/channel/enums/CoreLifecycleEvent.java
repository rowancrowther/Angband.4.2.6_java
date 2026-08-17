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

package uk.co.jackoftrades.channel.enums;

/**
 * What the core can report about its own life, as opposed to the game's. Travels in
 * {@link uk.co.jackoftrades.channel.messages.CoreMessage.LifecycleCoreMessage}.
 *
 * <p>Protocol rather than gameplay, which is why these are not
 * {@link GameEventType} constants: C has no equivalent, because a single-threaded
 * game needs no way to say "I have finished". Two threads do.
 *
 * <p>One constant today. It is an enum rather than an empty marker record so that the shape stays
 * right as the handshake grows — a core that can also report having failed to start, or having
 * saved, adds a constant here rather than a message type to the protocol.
 *
 * @author Rowan Crowther
 */
public enum CoreLifecycleEvent {
    /**
     * The core has finished shutting down: the save is written and the game thread is about to
     * end. Sent in reply to {@link UILifecycleEvent#SAVE_AND_STOP}, and the front end's cue that
     * it may now close the window and exit. Waiting for it is what stops the process dying
     * mid-save.
     */
    STOPPED
}
