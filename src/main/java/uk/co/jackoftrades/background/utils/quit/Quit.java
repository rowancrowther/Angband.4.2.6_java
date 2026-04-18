package uk.co.jackoftrades.background.utils.quit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Quit implements QuitAux {
    private final Logger logger = LogManager.getLogger();

    /**
     * @param quitMessage
     */
    @Override
    public void quit(String quitMessage) {
        logger.info(quitMessage);
        System.exit(0);
    }
}
