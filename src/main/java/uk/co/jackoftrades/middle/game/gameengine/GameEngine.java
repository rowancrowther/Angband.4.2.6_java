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

package uk.co.jackoftrades.middle.game.gameengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.middle.game.event.EventsBusHandler;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

/**
 * The top-level game runtime: a singleton that performs game start-up
 * for the middle end, (the events handler and
 * the game constants). It is the entry point the front end calls into once the
 * window exists, roughly the Java counterpart of the C original's {@code play_game}
 * / initialisation bootstrap.
 *
 * @author Rowan Crowther
 */
public class GameEngine {
    /**
     * Logger for start-up diagnostics.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The live event bus, held here as the game-wide seam other layers reach through
     * {@link #getEventsBusHandler()}. Created eagerly (so it is never {@code null}, even
     * before a game starts) and typed to the {@link EventsHandler} interface so a test
     * can swap in its own bus via {@link #setEventsBusHandler(EventsHandler)}.
     *
     * @author Rowan Crowther
     */
    private static EventsHandler eventsBusHandler = new EventsBusHandler();

    private static GameEngine instance;


    /**
     * Private constructor: build the main screen on the given stage, register it
     * as screen 0, and run {@link #initGame()}.
     *
     * @author Rowan Crowther
     */
    private GameEngine() {
        initGame();
    }

    /**
     * Initialise the game's subsystems in order — colours, the events handler and
     * the game constants — updating the status line as each step completes.
     *
     * @author Rowan Crowther
     */
    private void initGame() {
        GameConstants.init();

        GameState.initGameState();
        eventsBusHandler = new EventsBusHandler();
    }

    /**
     * The live event bus that game logic signals through. Never {@code null} - the field
     * is initialised eagerly - so callers can dispatch without a null check.
     *
     * @return the current event bus
     * @author Rowan Crowther
     */
    public static EventsHandler getEventsBusHandler() {
        return eventsBusHandler;
    }

    /**
     * Replace the live event bus - the injection seam for tests, which can install their
     * own {@link EventsBusHandler} (or a spy over one) to observe what gets signalled.
     *
     * @param eventsBusHandler the bus to install
     * @author Rowan Crowther
     */
    public static void setEventsBusHandler(EventsHandler eventsBusHandler) {
        GameEngine.eventsBusHandler = eventsBusHandler;
    }

    /**
     * Get the game engine singleton, creating it on the given stage the first
     * time this is called.
     *
     * @return the singleton game engine
     * @author Rowan Crowther
     */
    @CheckReturnValue
    public static GameEngine getGame() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }
}