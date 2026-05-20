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

public class GameEngine {
    private static final Logger logger = LogManager.getLogger();
    private static GameEngine instance;
    private Screen screen;
    private Stage stage;

    private GameEngine(Stage stage) {
        this.stage = stage;
        screen = new Screen(this.stage);
        GameConstants.AngbandScreens.clear();
        GameConstants.AngbandScreens.put(0, screen);
        initGame();
    }

    private void initGame() {
        // Initialise the Java classes
        Colour.init();

        screen.setStatusLabelText("Initializing events handler...");
        EventsHandler handler = EventsHandler.getInstance();
        screen.setStatusLabelText("Initialized game constants...");
        GameConstants.init();
        screen.setStatusLabelText("Select New Game from File menu to start the game...");
    }

    @CheckReturnValue
    public static GameEngine getGame(Stage stage) {
        if (instance == null) instance = new GameEngine(stage);
        return instance;
    }
}