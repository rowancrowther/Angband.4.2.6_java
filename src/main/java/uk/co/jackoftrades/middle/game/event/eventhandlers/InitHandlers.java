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
import uk.co.jackoftrades.middle.game.event.EventDataString;
import uk.co.jackoftrades.middle.game.event.GameEventData;
import uk.co.jackoftrades.middle.statusdisplay.StatusDisplayHolder;

public class InitHandlers {
    private static final Logger logger = LogManager.getLogger(InitHandlers.class);

    public static void enterInit(GameEventType eventType, GameEventData data) {
        logger.info("Entering init");

        if (data instanceof EventDataString message) {
            StatusDisplayHolder.getInstance().showInitStatus(message.getString());
        }
    }

    public static void leaveInit(GameEventType eventType, GameEventData data) {
        logger.info("Leaving init");
    }

    public static void enterGame(GameEventType eventType, GameEventData data) {
        logger.info("Entering game");
    }

    public static void leaveGame(GameEventType eventType, GameEventData data) {
        logger.info("Leaving game");
    }

    public static void enterWorld(GameEventType eventType, GameEventData data) {
        logger.info("Entering world");
    }

    public static void leaveWorld(GameEventType eventType, GameEventData data) {
        logger.info("Leaving world");
    }

    // uiInitBirthstateHandlers();
}