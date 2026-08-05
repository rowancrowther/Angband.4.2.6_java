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

package uk.co.jackoftrades.middle.game.event.eventhandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.event.GameEventData;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;

public class UIBirth {
    private static final Logger logger = LogManager.getLogger(UIBirth.class);

    public static void uiInitBirthstateHandlers() {
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        eventsHandler.eventAddHandler(GameEventType.EVENT_ENTER_BIRTH, UIBirth::uiEnterBirthscreen);
        eventsHandler.eventAddHandler(GameEventType.EVENT_LEAVE_BIRTH, UIBirth::uiLeaveBirthscreen);
    }

    public static void uiEnterBirthscreen(GameEventType eventType, GameEventData data) {
        logger.info("Entering birthscreen");
    }

    public static void uiLeaveBirthscreen(GameEventType eventType, GameEventData data) {
        logger.info("Leaving birthscreen");
    }
}
