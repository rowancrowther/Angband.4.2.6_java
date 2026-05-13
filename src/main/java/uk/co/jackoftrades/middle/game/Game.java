package uk.co.jackoftrades.middle.game;

import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.frontend.screen.Screen;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

public class Game {
    private static final Logger logger = LogManager.getLogger();
    private static Game instance;
    private Screen screen;
    private Stage stage;

    private Game(Stage stage) {
        this.stage = stage;
        screen = new Screen(this.stage);
        initGame();
    }

    private void initGame() {
        screen.setStatusLabelText("Initializing events handler...");
        EventsHandler handler = EventsHandler.getInstance();
        screen.setStatusLabelText("Initialized game constants...");
        GameConstants.init();
        screen.setStatusLabelText("Select New Game from Game menu to start the game...");
    }

    @CheckReturnValue
    public static Game getGame(Stage stage) {
        if (instance == null) instance = new Game(stage);
        return instance;
    }
}