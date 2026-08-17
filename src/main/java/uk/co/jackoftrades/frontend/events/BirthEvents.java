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
 * The front end's half of character creation's two ends: putting the birth screen up, and taking it
 * down once a character exists. The UI-side port of {@code ui_enter_birthscreen} and
 * {@code ui_leave_birthscreen} ({@code [C] src/ui-birth.c}).
 *
 * <p>Separate from {@link MainEvents} because birth is separate in C - {@code ui-birth.c}
 * subscribes its own pair in {@code ui_init_birthstate_handlers}, rather than
 * {@code ui-display.c} carrying them with the rest. The port keeps that division on both sides of
 * the boundary: {@code UIBirth} is the core-side counterpart to this class, exactly as
 * {@code InitHandlers} is to {@code MainEvents}.
 *
 * <p><b>Both methods are stubs that log</b>, for the reason given on {@link MainEvents}: the class
 * exists so that stage 5's split covers every event rather than most of them, and so the drawing
 * has a home before it is written.
 *
 * <p><b>This is the pair to be careful with.</b> The other five phase transitions are one-way
 * reports - the core says what happened and carries on. Birth is not: in C it is where the player
 * is first asked questions, so its real implementation needs the input boundary, where the core
 * sends a request and blocks on the UI channel for the reply. That is Chapter 3's design set piece,
 * and the empty method below is deliberately not a head start on it: forwarding the event is
 * settled, and how the answer travels back is not.
 *
 * @author Rowan Crowther
 */
public class BirthEvents {
    /**
     * Logger for the two transitions, which is currently all these methods do.
     */
    private static final Logger logger = LogManager.getLogger(BirthEvents.class);

    /**
     * Builds the receiver. Nothing to wire, for the reason {@link MainEvents#MainEvents()} gives.
     */
    public BirthEvents() {
    }

    /**
     * Character creation is starting: put the birth screen up. The port of
     * {@code ui_enter_birthscreen} ({@code [C] src/ui-birth.c}), which reads
     * {@code data->flag} into the {@code quickstart_allowed} global and then calls
     * {@code setup_menus()}.
     *
     * <p><b>The flag is the interesting half.</b> C carries "may this player quickstart?" on the
     * event payload, which means the ported form cannot stay a bare
     * {@code SimpleCoreMessage} - it needs the boolean, and {@code EventDataBoolean} is the
     * payload type that already exists to hold one. Nothing reads it yet because nothing draws the
     * menus yet, so the wire stays simple until the menus need it.
     */
    public void enterBirth() {
        logger.info("Executing EVENT_ENTER_BIRTH");
    }

    /**
     * Character creation has finished. The port of {@code ui_leave_birthscreen}
     * ({@code [C] src/ui-birth.c}), which names the savefile after the player if it has not been
     * named already, and frees the birth menus.
     *
     * <p>Only the second of those belongs on this side. Naming the savefile is core work that C
     * happens to do from a UI file - the port should not inherit that, and this method is the
     * place the split gets decided when birth is written.
     */
    public void leaveBirth() {
        logger.info("Executing EVENT_LEAVE_BIRTH");
    }
}