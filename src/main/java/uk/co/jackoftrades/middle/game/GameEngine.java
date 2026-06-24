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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.game;

import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.frontend.colour.Colour;
import uk.co.jackoftrades.frontend.screen.Screen;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

/**
 * The top-level game runtime: a singleton that owns the JavaFX {@link Stage} and
 * main {@link Screen} and performs game start-up (colours, the events handler and
 * the game constants). It is the entry point the front end calls into once the
 * window exists, roughly the Java counterpart of the C original's {@code play_game}
 * / initialisation bootstrap.
 *
 * @author ClaudeCode
 */
public class GameEngine {
    /**
     * Logger for start-up diagnostics.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The lazily-created singleton instance.
     *
     * @author ClaudeCode
     */
    private static GameEngine instance;
    /**
     * The main on-screen surface.
     *
     * @author ClaudeCode
     */
    private Screen screen;
    /**
     * The JavaFX stage hosting the game.
     *
     * @author ClaudeCode
     */
    private Stage stage;

    /**
     * Private constructor: build the main screen on the given stage, register it
     * as screen 0, and run {@link #initGame()}.
     *
     * @param stage the JavaFX stage to host the game
     * @author ClaudeCode
     */
    private GameEngine(Stage stage) {
        this.stage = stage;
        screen = new Screen(this.stage);
        GameConstants.AngbandScreens.clear();
        GameConstants.AngbandScreens.put(0, screen);
        initGame();
    }

    /**
     * Initialise the game's subsystems in order — colours, the events handler and
     * the game constants — updating the status line as each step completes.
     *
     * @author ClaudeCode
     */
    private void initGame() {
        // Initialise the Java classes
        Colour.init();

        screen.setStatusLabelText("Initializing events handler...");
        EventsHandler handler = EventsHandler.getInstance();
        screen.setStatusLabelText("Initialized game constants...");
        GameConstants.init();
        screen.setStatusLabelText("Select New Game from File menu to start the game...");
    }

    /**
     * Get the game engine singleton, creating it on the given stage the first
     * time this is called.
     *
     * @param stage the JavaFX stage to host the game
     * @return the singleton game engine
     * @author ClaudeCode
     */
    @CheckReturnValue
    public static GameEngine getGame(Stage stage) {
        if (instance == null) instance = new GameEngine(stage);
        return instance;
    }
}