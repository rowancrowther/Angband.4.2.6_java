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

package uk.co.jackoftrades.middle.game.event.birthhandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.event.GameEventData;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

/**
 * The birth-screen event handlers: the enter/leave pair for character creation, and their
 * registration. The port of {@code ui_init_birthstate_handlers()} ({@code [C] src/ui-birth.c}),
 * which C calls from {@code init_display()} alongside the other start-up subscriptions.
 *
 * <p>Split out from {@code InitHandlers} the same way C splits {@code ui-birth.c} from
 * {@code ui-display.c} - birth is a self-contained screen with its own state machine, and it will
 * grow the most of the four phases. {@code InitHandlers.initHandlers()} calls into here, so there
 * is still one entry point for the whole of start-up.
 *
 * <p>Both handlers only log so far. C's counterparts push and pop the birth menu's display state.
 *
 * @author Rowan Crowther
 */
public class UIBirth {
    /**
     * Logger for the birth transitions, which is currently all these handlers do.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger(UIBirth.class);

    /**
     * Subscribe the birth handlers to the live event bus. Called from
     * {@code InitHandlers.initHandlers()}, so the birth screen is listening before any character
     * can be rolled.
     *
     * <p>Reads the bus at call time rather than taking it as a parameter, so it wires whichever bus
     * is live - the same convention {@code InitHandlers} follows. Not idempotent, for the same
     * reason: a second call registers both handlers a second time.
     *
     * @author Rowan Crowther
     */
    public static void uiInitBirthstateHandlers() {
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_BIRTH, UIBirth::uiEnterBirthscreen);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_BIRTH, UIBirth::uiLeaveBirthscreen);
    }

    /**
     * Character creation is starting. C sets the birth screen up here; this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_ENTER_BIRTH}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void uiEnterBirthscreen(GameEventType eventType, GameEventData data) {
        logger.info("Entering birthscreen");
    }

    /**
     * Character creation has finished. C restores the previous display here; this logs and returns.
     *
     * @param eventType the event being handled, always {@code EVENT_LEAVE_BIRTH}; not read
     * @param data      the payload; not read
     * @author Rowan Crowther
     */
    public static void uiLeaveBirthscreen(GameEventType eventType, GameEventData data) {
        logger.info("Leaving birthscreen");
    }
}
