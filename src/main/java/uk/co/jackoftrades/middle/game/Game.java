package uk.co.jackoftrades.middle.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

public class Game {
    private static final Logger logger = LogManager.getLogger();
    private static Game instance;

    private Game() {
        EventsHandler.initialise();
        GameConstants.init();
    }

    public static Game getGame() {
        if (instance == null) instance = new Game();
        return instance;
    }
}