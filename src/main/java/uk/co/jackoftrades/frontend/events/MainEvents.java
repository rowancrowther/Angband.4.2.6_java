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

package uk.co.jackoftrades.frontend.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The front end's half of the session's phase transitions: what the screen does when the data load
 * ends, when a character starts playing, and when a level is entered or left. The UI-side port of
 * {@code ui_leave_init}, {@code ui_enter_game}, {@code ui_leave_game}, {@code ui_enter_world} and
 * {@code ui_leave_world} ({@code [C] src/ui-display.c}).
 *
 * <p><b>Every method here is a stub that logs, and that is the point.</b> The class was written
 * before it had anything to draw, because writing it is what finishes stage 5's split: each phase
 * event now has a home on this side of the boundary, so the day one of them grows a body there is
 * somewhere obvious for that body to go. Without this class the only file naming
 * {@code EVENT_ENTER_WORLD} would be the core's handler, and the drawing would land there by
 * gravity - which is the arrangement the split exists to prevent.
 *
 * <p><b>The counterpart is {@code InitHandlers}</b>, whose same-named handlers run on the game
 * thread. Those decide <em>that</em> a phase changed and put a {@code SimpleCoreMessage} on the core
 * channel; these decide what the player sees, and run on the UI thread after {@code UILoop} has
 * taken the message off the queue. That is C's arrangement, where {@code game-event.c} broadcasts
 * and {@code ui-*.c} draws.
 *
 * <p><b>It names nothing of the core.</b> The only import is the logger - no {@code middle}, no
 * {@code backend} - which is the import rule stage 5 turns into a test, satisfied here by
 * construction rather than by care.
 *
 * <p><b>No state yet, and one consequence.</b> {@code UILoop} currently builds a fresh instance per
 * message. That is harmless only while these methods are stateless; the first one that has to
 * remember something between events - as the splash screen already does - makes the per-message
 * construction wrong, and the instance has to become a field of the loop. Worth knowing before
 * adding the first field rather than after.
 *
 * @author Rowan Crowther
 */
public class MainEvents {
    /**
     * Logger for the phase transitions, which is currently all these methods do.
     */
    private static final Logger logger = LogManager.getLogger(MainEvents.class);

    /**
     * Builds the receiver. Nothing to wire: there is no channel end here, because messages arrive
     * by being handed to a method rather than by being read - {@code UILoop} owns the queue and
     * this owns the response to what comes off it.
     */
    public MainEvents() {
    }

    /**
     * The data load has finished, so the title screen comes down and the real display goes up. The
     * port of {@code ui_leave_init} ({@code [C] src/ui-display.c}), which resets the visuals,
     * processes the character preference files, reacts to the changes, redraws every term and
     * prints "Please wait..." - and, notably, unsubscribes {@code splashscreen_note}.
     *
     * <p>That unsubscribe is the half that does not port directly. C removes a handler from its
     * event bus; here the subscription lives on the far side of the boundary, in
     * {@code InitHandlers}, so the tidy-up belongs there and not in this method. What this method
     * will eventually own is only the painting: dropping the {@code SplashScreen} and putting the
     * game display in its place.
     */
    public void leaveInit() {
        logger.info("Executing EVENT_LEAVE_INIT");
    }

    /**
     * A character exists and play is starting. The port of {@code ui_enter_game}
     * ({@code [C] src/ui-display.c}), which subscribes the four handlers that carry text to the
     * player: {@code EVENT_MESSAGE}, {@code EVENT_BELL}, {@code EVENT_INPUT_FLUSH} and
     * {@code EVENT_MESSAGE_FLUSH}.
     *
     * <p>All four are message plumbing rather than layout, so this is the method that will need the
     * message protocol to grow before it can do anything - the core has to be able to say "print
     * this" before the front end can decide where. Chapter 3's work, not this stage's.
     */
    public void enterGame() {
        logger.info("Executing EVENT_ENTER_GAME");
    }

    /**
     * Play has ended - the character died, or the player quit. The port of {@code ui_leave_game}
     * ({@code [C] src/ui-display.c}), which removes exactly the four handlers {@link #enterGame}
     * added. Symmetrical by construction, and worth keeping that way: an asymmetry here is a
     * handler that survives into the next session.
     */
    public void leaveGame() {
        logger.info("Executing EVENT_LEAVE_GAME");
    }

    /**
     * A level is being entered and the world is live. The port of {@code ui_enter_world}
     * ({@code [C] src/ui-display.c}), which allows the big cursor, forces a redraw of the
     * inventory, equipment, monster and message panels, and subscribes the two handler <em>sets</em>
     * that keep the sidebar and the status line current.
     *
     * <p>Those sets are why this is the largest of the five in C: the sidebar is flexible, so a
     * long list of player events all route to one {@code update_sidebar}, and the status line does
     * the same. Porting it will want the equivalent of {@code event_add_handler_set} on this side,
     * which does not exist yet.
     *
     * <p>Unlike {@link #enterGame}, this can happen several times in one session - entering a store
     * leaves the world and returning enters it again - so whatever this eventually allocates has to
     * be safe to allocate twice.
     */
    public void enterWorld() {
        logger.info("Executing EVENT_ENTER_WORLD");
    }

    /**
     * The level is being left. The port of {@code ui_leave_world}
     * ({@code [C] src/ui-display.c}), which disallows the big cursor and removes the two handler
     * sets {@link #enterWorld} added.
     */
    public void leaveWorld() {
        logger.info("Executing EVENT_LEAVE_WORLD");
    }
}
