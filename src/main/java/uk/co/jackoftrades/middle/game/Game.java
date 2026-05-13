package uk.co.jackoftrades.middle.game;

import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

public class Game {
    private static final Logger logger = LogManager.getLogger();
    private static Game instance;

    private Game(@NotNull Label statusLabel) {

        statusLabel.setText("Initializing events handler...");
        EventsHandler.initialise();
        statusLabel.setText("Initialized game constants...");
        GameConstants.init();
        statusLabel.setText("Select New Game from Game menu to start the game...");
    }

    @CheckReturnValue
    public static Game getGame(@NotNull Label statusLabel) {
        if (instance == null) instance = new Game(statusLabel);
        return instance;
    }
}